package com.example.zhang.popmovies.app;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by zhang on 02/10/15.
 */

public class PreviewAdapter extends ArrayAdapter<Uri> {

    //Context context;

    public PreviewAdapter(Context context, int resourceId, List<Uri> items) {
        super(context, resourceId, items);
        //this.context = context;
    }
    ImageView imageView = null;


    public View getView(int position, View convertView, ViewGroup parent) {
        Uri uri = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) getContext()
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.grid_item_preview, null);
            imageView = (ImageView) convertView.findViewById(R.id.grid_item_preview_imageView);
            convertView.setTag(imageView);
        } else {
            imageView = (ImageView) convertView.getTag();
        }

        Picasso.with(getContext()).load(uri).into(imageView);

        return convertView;
    }

}
