package com.example.zhang.popmovies.app;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by zhang on 06/12/15.
 */
public class PreviewAdapter extends CursorAdapter
{

    private static final String previewPosterSize = "w320";

    public PreviewAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    public static class ViewHolder {

        public final ImageView previewView;

        public ViewHolder(View view) {
            previewView = (ImageView) view.findViewById(R.id.grid_item_preview_imageView);
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.grid_item_preview, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(R.string.app_name, viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag(R.string.app_name);

        Picasso.with(context).load(Utility.getPreviewImage(cursor, previewPosterSize)).into(viewHolder.previewView);
//
//        int networkType = Utility.getNetworkType(context);
//
//        if (networkType == ConnectivityManager.TYPE_WIFI) {
//
//        } else {
//            viewHolder.previewView.setImageResource(R.mipmap.ic_launcher);
//        }
    }
}
