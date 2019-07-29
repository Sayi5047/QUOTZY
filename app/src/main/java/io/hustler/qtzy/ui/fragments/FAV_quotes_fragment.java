package io.hustler.qtzy.ui.fragments;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.hustler.qtzy.R;
import io.hustler.qtzy.ui.Executors.AppExecutor;
import io.hustler.qtzy.ui.ORM.AppDatabase;
import io.hustler.qtzy.ui.ORM.Tables.QuotesTable;
import io.hustler.qtzy.ui.activities.QuoteDetailsActivity;
import io.hustler.qtzy.ui.adapters.LocalAdapter;
import io.hustler.qtzy.ui.apiRequestLauncher.Constants;
import io.hustler.qtzy.ui.utils.IntentConstants;

/**
 * Created by Sayi Manoj Sugavasi on 02/02/2018.
 */

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
public class FAV_quotes_fragment extends android.support.v4.app.Fragment {
    ImageView iv_no_fav;
    RecyclerView rv_imag_no_fv;
    AppDatabase appDatabase;
    AppExecutor appExecutor;
    RelativeLayout error_layout, main_layout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fav_quotes_fragment_layout, container, false);

        iv_no_fav = view.findViewById(R.id.iv);
        rv_imag_no_fv = view.findViewById(R.id.rv);
        error_layout = view.findViewById(R.id.error_layout);
        main_layout = view.findViewById(R.id.main_layout);
        error_layout.setVisibility(View.GONE);
        rv_imag_no_fv.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        appDatabase = AppDatabase.getmAppDatabaseInstance(getContext());
        appExecutor = AppExecutor.getInstance();
        setAdapter(rv_imag_no_fv);
        return view;
    }

    private void setAdapter(final RecyclerView recyclerView) {
        final LiveData<List<QuotesTable>> liveData = appDatabase.quotesDao().getlikedQuotes(true);
        liveData.observe(Objects.requireNonNull(getActivity()), new Observer<List<QuotesTable>>() {
            @Override
            public void onChanged(@Nullable final List<QuotesTable> quotesTables) {
                Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (quotesTables.size() <= 0) {
                            error_layout.setVisibility(View.VISIBLE);
                            main_layout.setVisibility(View.GONE);
                        } else {
                            error_layout.setVisibility(View.GONE);
                            main_layout.setVisibility(View.VISIBLE);
                            setAdapterData(recyclerView, quotesTables);

                        }

                    }
                });
            }
        });


    }

    private void setAdapterData(RecyclerView recyclerView, List<QuotesTable> liveData) {
        recyclerView.setAdapter(new LocalAdapter(getActivity(), (ArrayList<QuotesTable>) liveData, new LocalAdapter.OnQuoteClickListener() {
            @Override
            public void onQuoteClicked(int position, @NonNull GradientDrawable color, QuotesTable quote, View view) {
                Intent intent = new Intent(getActivity(), QuoteDetailsActivity.class);
                intent.putExtra(Constants.INTENT_QUOTE_OBJECT_KEY, quote.getId());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    intent.putExtra(IntentConstants.GRADIENT_COLOR1, color.getColors());
                }
                Objects.requireNonNull(getActivity()).startActivity(intent);


            }
        }));
    }

}
