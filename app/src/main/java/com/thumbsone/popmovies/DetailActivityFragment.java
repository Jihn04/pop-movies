package com.thumbsone.popmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Movie movie = null;
        Intent intent = getActivity().getIntent();

        if (intent != null && intent.hasExtra("movie")) {
            movie = intent.getParcelableExtra("movie");
        }

        if (movie != null) {
            TextView title = (TextView) rootView.findViewById(R.id.textview_title);
            title.setText(movie.getTitle());

            ImageView thumbnail = (ImageView) rootView.findViewById(R.id.imageview_thumbnail);
            Picasso.with(getContext())
                    .load(movie.getPath())
                    .into(thumbnail);
            Log.v("Image: ", movie.getPath());

            TextView date = (TextView) rootView.findViewById(R.id.textview_date);
            date.setText(movie.getDate());

            TextView rating = (TextView) rootView.findViewById(R.id.textview_rating);
            rating.setText(String.valueOf(movie.getRating()) + " / 10.0");

            TextView overview = (TextView) rootView.findViewById(R.id.textview_overview);
            overview.setText(movie.getOverview());
        }
        return rootView;
    }
}
