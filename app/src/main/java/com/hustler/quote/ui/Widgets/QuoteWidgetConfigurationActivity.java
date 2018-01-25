package com.hustler.quote.ui.Widgets;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.ColorInt;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hustler.quote.R;
import com.hustler.quote.ui.Services.RandomQuoteUpdateService;
import com.hustler.quote.ui.activities.BaseActivity;
import com.hustler.quote.ui.activities.EditorActivity;
import com.hustler.quote.ui.adapters.QuoteWidgetConfigAdapter;
import com.hustler.quote.ui.apiRequestLauncher.Constants;
import com.hustler.quote.ui.pojo.Quote;
import com.pes.androidmaterialcolorpickerdialog.ColorPicker;
import com.pes.androidmaterialcolorpickerdialog.ColorPickerCallback;

import static android.appwidget.AppWidgetManager.INVALID_APPWIDGET_ID;

/**
 * Created by anvaya5 on 23/01/2018.
 */

public class QuoteWidgetConfigurationActivity extends BaseActivity implements View.OnClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private Button colorChanger;
    private Button SizeChanger;
    private Button addWidget;
    RemoteViews remoteViews;
    int mAppWidgetId;

    private RelativeLayout root;
    private LinearLayout mainWidgetLayout;
    private LinearLayout rootWidget;
    private TextView quoteBody;
    private TextView quoteAuthor;
    private LinearLayout btLayout;
    private ImageView widgetEditQuote;
    private ImageView widgetEditRefresh;
    private LinearLayout navLayout;
    private Button btClose;
    private Button btDone;
    private ViewPager widgetPreferencePager;
    AppWidgetManager appWidgetManager;
    AlarmManager alarmManager;
    SharedPreferences sharedPreferences;
    PendingIntent service;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quote_widget_configuration_layout);
        findViews();
        mAppWidgetId = INVALID_APPWIDGET_ID;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        appWidgetManager = AppWidgetManager.getInstance(this);
        remoteViews = new RemoteViews(this.getPackageName(), R.layout.quote_widget_layout);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        intialiseAppWidget();


    }

    private void intialiseAppWidget() {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, INVALID_APPWIDGET_ID);

        }
        if (mAppWidgetId == INVALID_APPWIDGET_ID) {
            finish();
        }
    }

    private void findViews() {
        root = (RelativeLayout) findViewById(R.id.root);
        mainWidgetLayout = (LinearLayout) findViewById(R.id.main_widget_layout);
        rootWidget = (LinearLayout) findViewById(R.id.root_widget);
        quoteBody = (TextView) findViewById(R.id.quote_body);
        quoteAuthor = (TextView) findViewById(R.id.quote_author);
        btLayout = (LinearLayout) findViewById(R.id.bt_layout);
        widgetEditQuote = (ImageView) findViewById(R.id.widget_edit_quote);
        widgetEditRefresh = (ImageView) findViewById(R.id.widget_edit_refresh);
        navLayout = (LinearLayout) findViewById(R.id.nav_layout);
        btClose = (Button) findViewById(R.id.bt_close);
        btDone = (Button) findViewById(R.id.bt_done);
        widgetPreferencePager = (ViewPager) findViewById(R.id.widget_preference_pager);
        widgetPreferencePager.setAdapter(new QuoteWidgetConfigAdapter(getFragmentManager(), this));

        btClose.setOnClickListener(this);
        btDone.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btClose) {
            // Handle clicks for btClose
            finish();
        } else if (v == btDone) {
//            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.root_widget);
//            AnimationDrawable animationDrawable = (AnimationDrawable) linearLayout.getBackground();
//            animationDrawable.setEnterFadeDuration(2000);
//            animationDrawable.setExitFadeDuration(4000);
//            animationDrawable.start();


            Intent intent_edit = new Intent(this, EditorActivity.class);
            intent_edit.putExtra(Constants.INTENT_IS_FROM_EDIT_KEY, true);
            Quote quote = new Gson().fromJson(sharedPreferences.getString(Constants.Widget_current_object, null), Quote.class);
            intent_edit.putExtra(Constants.INTENT_QUOTE_OBJECT_KEY, (quote));
            PendingIntent pendingIntent_edit = PendingIntent.getActivity(this, 0, intent_edit, 0);
            remoteViews.setOnClickPendingIntent(R.id.root_widget, pendingIntent_edit);
            appWidgetManager.updateAppWidget(mAppWidgetId, remoteViews);
            Intent intent = new Intent(QuoteWidgetConfigurationActivity.this, RandomQuoteUpdateService.class);

            if (service == null) {
                service = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            }
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), 1000, service);
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.widget_shared_prefs_layout_size_key))) {
            String[] sizes = this.getResources().getStringArray(R.array.layout_styles_vals);

            String val = sharedPreferences.getString(key, null) == null ? sizes[0] : sharedPreferences.getString(key, null);


        } else if (key.equals(getString(R.string.widget_shared_prefs_background_key))) {
//            Log.d("Layout Key", sharedPreferences.getString(key, null));

            Boolean val = sharedPreferences.getBoolean(key, true);
            if (val == true) {
                final ColorPicker colorPicker = new ColorPicker(this);

                colorPicker.setCallback(new ColorPickerCallback() {
                    @Override
                    public void onColorChosen(@ColorInt int color) {
                        mainWidgetLayout.setBackgroundColor(color);
                        remoteViews.setInt(R.id.root_widget, "setBackgroundColor", color);
                        colorPicker.cancel();
                    }

                });
                colorPicker.show();
            } else {

            }
        } else if (key.equals(getString(R.string.widget_shared_prefs_show_author_key))) {
            Log.d("Layout Key", String.valueOf(sharedPreferences.getBoolean(key, true)));
            Boolean val = sharedPreferences.getBoolean(key, true);
            if (val == true) {
                quoteAuthor.setVisibility(View.GONE);
                remoteViews.setViewVisibility(R.id.quote_author, View.GONE);
            } else {
                quoteAuthor.setVisibility(View.VISIBLE);
                remoteViews.setViewVisibility(R.id.quote_author, View.VISIBLE);
            }

        } else if (key.equals(getString(R.string.widget_shared_prefs_show_quotes_mark_key))) {
            Boolean val = sharedPreferences.getBoolean(key, false);
            Log.d("Layout Key", String.valueOf(val));
            if (val == true) {
                quoteBody.setText(" \"SAYI MANOJ SUGAVASI IS A GOOD BOY\"");
                remoteViews.setTextViewText(R.id.quote_body, "\"SAYI MANOJ SUGAVASI IS A GOOD BOY\"");
            } else {
                quoteBody.setText("SAYI MANOJ SUGAVASI IS A GOOD BOY");
                remoteViews.setTextViewText(R.id.quote_body, "SAYI MANOJ SUGAVASI IS A GOOD BOY");
            }

        } else if (key.equals(getString(R.string.widget_shared_prefs_text_key))) {
            Log.d("Layout Key", String.valueOf(sharedPreferences.getBoolean(key, true)));
            Boolean val = sharedPreferences.getBoolean(key, true);
            if (val == true) {
                final ColorPicker colorPicker = new ColorPicker(this);

                colorPicker.setCallback(new ColorPickerCallback() {
                    @Override
                    public void onColorChosen(@ColorInt int color) {
                        quoteBody.setTextColor(color);
                        quoteAuthor.setTextColor(color);
                        remoteViews.setTextColor(R.id.quote_body, color);
                        remoteViews.setTextColor(R.id.quote_author, color);
                        colorPicker.cancel();
                    }

                });
                colorPicker.show();
            } else {

            }
        } else if (key.equals(getString(R.string.widget_shared_prefs_update_key))) {
            Log.d("Layout Key", String.valueOf(sharedPreferences.getBoolean(key, true)));


        } else if (key.equals(getString(R.string.widget_shared_prefs_quotes_resource_key))) {


        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }
}




