package com.hustler.quote.ui.adapters;

import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hustler.quote.R;
import com.hustler.quote.ui.customviews.CustomImageView;
import com.hustler.quote.ui.customviews.CustomView;
import com.hustler.quote.ui.superclasses.App;

import java.util.ArrayList;

import static android.support.v7.recyclerview.R.styleable.RecyclerView;

/**
 * Created by Sayi on 11-10-2017.
 */


public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ContentViewholder> {
    Activity activity;
    ArrayList<String> items;
    int color;
    ArrayList<Integer> resolvedColorsList=new ArrayList<>();

    public ContentAdapter(Activity activity, ArrayList<String> items, ContentAdapter.onItemClickListener onItemClickListener) {
        this.activity = activity;
        this.items = items;
        this.onItemClickListener = onItemClickListener;
        getColorids();
    }

    public onItemClickListener onItemClickListener;

    public ContentAdapter(Activity activity, ArrayList<String> items) {
        this.activity = activity;
        this.items = items;

    }

    private void getColorids() {
        for(int i=0;i<items.size();i++){
            resolvedColorsList.add(i,App.getArrayItem(activity,"allColors",i,Color.WHITE));
            Log.d("Files Added -->",String.valueOf(resolvedColorsList.size()));

        }
        Log.d("Files Added -->",String.valueOf(resolvedColorsList.size()));
    }

    public ContentAdapter(Activity activity) {
        this.activity = activity;
    }

    public interface onItemClickListener {
        void onItemClick(int val);
    }

    @Override
    public ContentAdapter.ContentViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ContentViewholder(activity.getLayoutInflater().inflate(R.layout.content_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ContentAdapter.ContentViewholder holder, final int position) {

//       int arrayId=activity.getResources().getIdentifier("allColors","array",activity.getApplicationContext().getPackageName());
//        TypedArray typedArray=activity.getResources().obtainTypedArray(arrayId);
//        holder.customView.setCircleColor(typedArray.getResourceId(position, Color.BLACK));
//        color=App.getArrayItem(activity,"array",position,Color.WHITE);
        holder.customView.setCircleColor(App.getArrayItem(activity,"allColors",position,Color.WHITE));
        Log.d("Resolved color",resolvedColorsList.get(position).toString());
        if(this instanceof onItemClickListener)
        {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {

        if (items.size() <= 0) {
            return 0;
        } else {
            return items.size();
        }
    }

    public class ContentViewholder extends RecyclerView.ViewHolder {
        CustomView customView;
        TextView textView;

        public ContentViewholder(View itemView) {
            super(itemView);
            customView = (CustomView) itemView.findViewById(R.id.content_color_item);
//            textView = (TextView) itemView.findViewById(R.id.content_font_item);
//            textView.setVisibility(View.GONE);

        }
    }
}