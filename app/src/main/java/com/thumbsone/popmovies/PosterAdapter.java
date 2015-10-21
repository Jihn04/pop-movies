package com.thumbsone.popmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

// CustomAdapter to inflate an image to an imageView
public class PosterAdapter extends ArrayAdapter<Movie> {

    public PosterAdapter(Context context, List<Movie> movies) {
        super(context, 0, movies);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Movie movie = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item_poster, parent, false);
        }

        ImageView movieView = (ImageView) convertView.findViewById(R.id.image_view_poster);
        int width = getContext().getResources().getDisplayMetrics().widthPixels;
        Picasso.with(getContext())
                .load(movie.getPath())
                .centerCrop().resize(width/2, width)
                .into(movieView);

        return convertView;
    }
}
