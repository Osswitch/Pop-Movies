package com.example.zhang.popmovies.app;

import android.app.Activity;
import android.content.Context;
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

public class PreviewAdapter extends ArrayAdapter<RowImages> {

    //Context context;

    public PreviewAdapter(Context context, int resourceId, List<RowImages> items) {
        super(context, resourceId, items);
        //this.context = context;
    }

    private class ViewHolder {
        ImageView firstImageView = null;
        ImageView secondImageView = null;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        RowImages rowImages = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) getContext()
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_preview, null);
            holder = new ViewHolder();
            holder.firstImageView = (ImageView) convertView.findViewById(R.id.first_imageView_preview);
            holder.secondImageView = (ImageView) convertView.findViewById(R.id.second_imageView_preview);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //holder.firstImageView.setImageResource(rowImage.getFirstImageId());
        Picasso.with(getContext())
         .load(rowImages.getFirstRowImageUri()).into(holder.firstImageView);
        //holder.secondImageView.setImageResource(rowImage.getSecondImageId());
        Picasso.with(getContext())
         .load(rowImages.getSecondRowImageUri()).into(holder.secondImageView);

        return convertView;
    }

}
