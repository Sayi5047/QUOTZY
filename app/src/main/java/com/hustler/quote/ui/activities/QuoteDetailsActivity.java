package com.hustler.quote.ui.activities;

import android.Manifest;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.android.gms.ads.AdView;
import com.google.firebase.crash.FirebaseCrash;
import com.hustler.quote.R;
import com.hustler.quote.ui.apiRequestLauncher.Constants;
import com.hustler.quote.ui.database.QuotesDbHelper;
import com.hustler.quote.ui.pojo.Quote;
import com.hustler.quote.ui.superclasses.BaseActivity;
import com.hustler.quote.ui.utils.AdUtils;
import com.hustler.quote.ui.utils.FileUtils;
import com.hustler.quote.ui.utils.IntentConstants;
import com.hustler.quote.ui.utils.PermissionUtils;
import com.hustler.quote.ui.utils.TextUtils;

import java.io.File;

import static com.hustler.quote.ui.utils.FileUtils.savetoDevice;
import static com.hustler.quote.ui.utils.FileUtils.show_post_save_dialog;

/*   Copyright [2018] [Sayi Manoj Sugavasi]

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.*/
public class QuoteDetailsActivity extends BaseActivity implements View.OnClickListener {
    private static final int MY_PERMISSION_REQUEST_STORAGE = 1001;
    private static final int MY_PERMISSION_REQUEST_STORAGE_WALLPAPER = 1003;
    Quote quote;
    RelativeLayout root;
    LinearLayout quote_layout;
    LinearLayout quote_bottom;
    TextView tv_Quote_Body, tv_Quote_Author, image_saved_message;
    FloatingActionButton fab_save, fab_edit, fab_share, fab_set_wall, fab_set_like;
    ImageView quote_anim;
    File savedFile;
    Window window;
    int val = 1001;
    boolean IS_LIKED_FLAG;
    private RelativeLayout wallpaper_layout;
    private final int MY_PERMISSION_REQUEST_STORAGE_FIRST = 1002;
    private AdView mAdView;
    Toolbar toolbar;
    GradientDrawable gradientDrawable;
    int[] color1, color2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quote_details);
        setToolbar(this);
        initView();
        getIntentData();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (PermissionUtils.isPermissionAvailable(QuoteDetailsActivity.this)) {
        } else {
            requestFirstAppPermissions();
        }
    }

    interface OnImageSaveListner {
        void onImageSaved(Uri uri);
    }

    private void initView() {
        root = findViewById(R.id.root);
        mAdView = findViewById(R.id.adView);
        tv_Quote_Author = findViewById(R.id.tv_Quote_Author);
        tv_Quote_Body = findViewById(R.id.tv_Quote_Body);

        quote_layout = findViewById(R.id.quote_layout);
        wallpaper_layout = findViewById(R.id.wallpaper_layout);
        quote_bottom = findViewById(R.id.quote_bottom);
        quote_anim = findViewById(R.id.quote_anim);
        TextUtils.findText_and_applyTypeface(root, QuoteDetailsActivity.this);

        fab_edit = findViewById(R.id.fab_edit);
        fab_save = findViewById(R.id.fab_download);
        fab_share = findViewById(R.id.fab_share);
        fab_set_wall = findViewById(R.id.fab_set_wall);
        fab_set_like = findViewById(R.id.fab_set_like);

        fab_edit.setOnClickListener(this);
        fab_save.setOnClickListener(this);
        fab_share.setOnClickListener(this);
        fab_set_wall.setOnClickListener(this);
        fab_set_like.setOnClickListener(this);

        AdUtils.loadBannerAd(mAdView, QuoteDetailsActivity.this);

    }

    @Override
    public void setToolbar(Activity activity) {
        super.setToolbar(activity);
        window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    private GradientDrawable createDrawable(int[] colors) {

        int[] color = {colors[0], colors[1]};
        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BR_TL, colors);
        gradientDrawable.setGradientRadius(135);
//        gradientDrawable.setCornerRadius(20f);
        return gradientDrawable;
//        holder.imageView.layout(0,0,100,100);
//
//        holder.imageView.setDrawingCacheEnabled(true);
//        holder.imageView.buildDrawingCache();
//        Bitmap bitmap=holder.imageView.getDrawingCache();
//        Bitmap finalbitmap =ImageProcessingUtils.create_blur(bitmap,5.0f,activity);
//        holder.imageView.setImageBitmap(finalbitmap);
    }

    private void getIntentData() {
        quote = (Quote) getIntent().getSerializableExtra(Constants.INTENT_QUOTE_OBJECT_KEY);
        int length = quote.getQuote_body().length();
        color1 = getIntent().getIntArrayExtra(IntentConstants.GRADIENT_COLOR1);
        if (color1 != null) {
            quote_layout.setBackground(createDrawable(color1));
        } else {
            int color1 = TextUtils.getMatColor(QuoteDetailsActivity.this, "mdcolor_500");
            int color2 = TextUtils.getMatColor(QuoteDetailsActivity.this, "mdcolor_500");
            quote_layout.setBackground(createDrawable(new int[]{color1, color2}));

        }
//        quote_bottom.setBackground(createDrawable(color1));
//        wallpaper_layout.setBackground(createDrawable(color1));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            quote_bottom.setElevation(getResources().getDimension(R.dimen.elevation4));
        }

        if (length > 230) {
            tv_Quote_Body.setTextSize(25.0f);
        } else if (length < 230 && length > 150) {
            tv_Quote_Body.setTextSize(28.0f);

        } else if (length > 100 && length < 150) {
            tv_Quote_Body.setTextSize(32.0f);

        } else if (length > 50 && length < 100) {
            tv_Quote_Body.setTextSize(38.0f);

        } else if (length > 2 && length < 50) {
            tv_Quote_Body.setTextSize(42.0f);

        } else {
            tv_Quote_Body.setTextSize(48.0f);

        }
        tv_Quote_Body.setText(quote.getQuote_body());
        tv_Quote_Author.setText(quote.getQuote_author());
        tv_Quote_Body.setTextColor(Color.WHITE);
        tv_Quote_Author.setTextColor(Color.WHITE);
        if (quote.getIsLiked() == 1) {
            IS_LIKED_FLAG = true;
            fab_set_like.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_favorite_black_24dp));
        } else {
            IS_LIKED_FLAG = false;
            fab_set_like.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_favorite_border_black_24dp));

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_set_like: {
                if (IS_LIKED_FLAG) {
                    removeFavourite();
                } else {
                    addFavourite();

                }
//                root.setBackgroundDrawable(gradientDrawable);
            }
            break;
            case R.id.fab_download:
                checkpermissions_and_proceed();
                break;
            case R.id.fab_edit:
                edit();
                break;
            case R.id.fab_share:
                share();
                break;
            case R.id.fab_set_wall:
                if (PermissionUtils.isPermissionAvailable(QuoteDetailsActivity.this)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        setWallPaer();
                    } else {
                        setWallPaerCompat();
                    }
                } else {
                    requestFirstAppPermissions_Wall();
                }

                break;
        }
//        root.setBackgroundDrawable(gradientDrawable);

    }

    private void removeFavourite() {
        new QuotesDbHelper(QuoteDetailsActivity.this).removeFromFavorites(quote);
        fab_set_like.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_favorite_border_black_24dp));
        IS_LIKED_FLAG = false;
    }

    private void addFavourite() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                new QuotesDbHelper(QuoteDetailsActivity.this).addToFavourites(quote);
                fab_set_like.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_favorite_black_24dp));
                IS_LIKED_FLAG = true;
            }
        }).run();

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void setWallPaer() {
        checkandRetrieveUri(quote_layout, new OnImageSaveListner() {
            @Override
            public void onImageSaved(Uri uri) {
                try {
                    File file = new File(uri.getPath());
                    Intent intent = new Intent(WallpaperManager.
                            getInstance(getApplicationContext()).
                            getCropAndSetWallpaperIntent(FileUtils.getImageContentUri(getApplicationContext(), file)));

                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    FirebaseCrash.log(e.getMessage());
                }
            }
        });

    }

    private void setWallPaerCompat() {
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        try {

            wallpaperManager.setBitmap(FileUtils.returnBitmap(quote_layout));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkpermissions_and_proceed() {
        if (PermissionUtils.isPermissionAvailable(QuoteDetailsActivity.this)) {
            savetoDevice(quote_layout, QuoteDetailsActivity.this, new FileUtils.onSaveComplete() {
                @Override
                public void onImageSaveListner(File file) {
                    savedFile = file;
                    show_post_save_dialog(QuoteDetailsActivity.this, savedFile);
                }
            });
        } else {
            requestAppPermissions();
        }
    }


    private void requestAppPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                (MY_PERMISSION_REQUEST_STORAGE));
    }

    private void requestFirstAppPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                (MY_PERMISSION_REQUEST_STORAGE_FIRST));
    }

    private void requestFirstAppPermissions_Wall() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                (MY_PERMISSION_REQUEST_STORAGE_WALLPAPER));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    savetoDevice(quote_layout, QuoteDetailsActivity.this, new FileUtils.onSaveComplete() {
                        @Override
                        public void onImageSaveListner(File file) {
                            savedFile = file;
                            show_post_save_dialog(QuoteDetailsActivity.this, savedFile);

                        }
                    });
                }
            }
            break;
            case MY_PERMISSION_REQUEST_STORAGE_FIRST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                }
            }
            break;
            case MY_PERMISSION_REQUEST_STORAGE_WALLPAPER: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        setWallPaer();
                    } else {
                        setWallPaerCompat();
                    }
                }
            }


        }
    }

    private void share() {
        final Uri[] uri = new Uri[1];
        final Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "SUBJECT");
        shareIntent.putExtra(Intent.EXTRA_TITLE, "Title");
        shareIntent.setType("image/jpeg");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        if (savedFile == null) {
            checkandRetrieveUri(quote_layout, new OnImageSaveListner() {
                @Override
                public void onImageSaved(Uri val) {
                    uri[0] = val;
                    shareIntent.putExtra(Intent.EXTRA_STREAM, uri[0]);
                    startActivity(Intent.createChooser(shareIntent, "send"));
                }
            });

        } else {

            Uri uriValue = null;
            if (Build.VERSION.SDK_INT >= 24) {
                uriValue = FileProvider.getUriForFile(getApplicationContext(), getString(R.string.file_provider_authority), savedFile);
            } else {
                uriValue = Uri.fromFile(savedFile);
            }
            shareIntent.putExtra(Intent.EXTRA_STREAM, uriValue);
            startActivity(Intent.createChooser(shareIntent, "send"));
        }
    }

    private Uri checkandRetrieveUri(ViewGroup rootview, final OnImageSaveListner OnImageSaveListner) {
        final Uri[] uri = {null};
        if (savedFile != null) {
            if (Build.VERSION.SDK_INT >= 24) {
                uri[0] = FileProvider.getUriForFile(getApplicationContext(), getString(R.string.file_provider_authority), savedFile);
            } else {
                uri[0] = Uri.fromFile(savedFile);
            }
        } else {
            savetoDevice(rootview, QuoteDetailsActivity.this, new FileUtils.onSaveComplete() {
                @Override
                public void onImageSaveListner(File file) {
                    savedFile = file;
                    if (Build.VERSION.SDK_INT >= 24) {
                        uri[0] = FileProvider.getUriForFile((getApplicationContext()), getString(R.string.file_provider_authority), savedFile);
                    } else {
                        uri[0] = Uri.fromFile(savedFile);
                    }
                    OnImageSaveListner.onImageSaved(uri[0]);
                    show_post_save_dialog(QuoteDetailsActivity.this, savedFile);


                }
            });
        }
        return uri[0];
    }

    private void edit() {
        Intent intent = new Intent(this, EditorActivity.class);
        intent.putExtra(Constants.INTENT_QUOTE_OBJECT_KEY, quote);
        intent.putExtra(Constants.INTENT_IS_FROM_EDIT_KEY, 1);
        startActivity(intent);
    }


    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        finishAfterTransition();
    }
}
