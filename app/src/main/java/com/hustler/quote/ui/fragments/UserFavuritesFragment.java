package com.hustler.quote.ui.fragments;

import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hustler.quote.R;
import com.hustler.quote.ui.activities.QuoteDetailsActivity;
import com.hustler.quote.ui.adapters.LocalAdapter;
import com.hustler.quote.ui.apiRequestLauncher.Constants;
import com.hustler.quote.ui.database.QuotesDbHelper;
import com.hustler.quote.ui.livewallpaper.MyWallpaperService;
import com.hustler.quote.ui.pojo.Quote;

import java.util.ArrayList;

/**
 * Created by anvaya5 on 20/12/2017.
 */

public class UserFavuritesFragment extends Fragment {
    ImageView imageView;
    RecyclerView recyclerView;
    ArrayList<Quote> quotes;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_fav_layout, container, false);
        imageView = (ImageView) view.findViewById(R.id.iv);
        recyclerView = (RecyclerView) view.findViewById(R.id.rv);
        Glide.with(getActivity()).load(Uri.parse("http://drive.google.com/uc?export=view&id=1-xt_qFGV_IsKJ7ZH2iJ7OL9a3WQB0ERs")).
                centerCrop().crossFade().diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        setAdapter(recyclerView);
        return view;
    }

    private void setAdapter(RecyclerView recyclerView) {
        final ArrayList<Quote>[] arrayLists = new ArrayList[]{new ArrayList<>()};
        new Thread(new Runnable() {
            @Override
            public void run() {
                arrayLists[0] = (ArrayList<Quote>) new QuotesDbHelper(getActivity()).getAllFav_Quotes();
            }
        }).run();
        recyclerView.setAdapter(new LocalAdapter(getActivity(), arrayLists[0], new LocalAdapter.OnQuoteClickListener() {
            @Override
            public void onQuoteClicked(int position, int color, Quote quote, View view) {
//                Intent intent = new Intent(getActivity(), QuoteDetailsActivity.class);
//                intent.putExtra(Constants.INTENT_QUOTE_OBJECT_KEY, quote);
//                startActivity(intent);
                Intent intent = new Intent(
                        WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
                intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                        new ComponentName(getActivity(), MyWallpaperService.class));
                startActivity(intent);
            }
        }));

    }

    @Override
    public void onResume() {
        super.onResume();
        recyclerView.setAdapter(null);
        setAdapter(recyclerView);
    }
}
