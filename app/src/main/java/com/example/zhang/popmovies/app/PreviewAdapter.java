package com.example.zhang.popmovies.app;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by zhang on 06/12/15.
 */
public class PreviewAdapter extends CursorAdapter {

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
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        Picasso.with(context).load(Utility.getPreviewImage(cursor)).into(viewHolder.previewView);
    }
}
