package com.thumbsone.popmovies;


import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainActivityFragment extends Fragment {

    final String API_KEY = BuildConfig.MOVIE_DB_API_KEY;

    private PosterAdapter mPosterAdapter;

    public MainActivityFragment() {
        // Required empty public constructor
    }

    // Update the gridView for movie posters on start
    @Override
    public void onStart() {
        super.onStart();
        updateMovie();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // ArrayList to contain Movies list
        ArrayList<Movie> posters = new ArrayList<>();

        // CustomAdapter connecting ArrayList & GridView
        mPosterAdapter = new PosterAdapter(getActivity(), posters);

        // Find GridView by its ID
        GridView gridView = (GridView) rootView.findViewById(R.id.grid_view_movies);

        // Attach the CustomAdapter to the GridView
        gridView.setAdapter(mPosterAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = mPosterAdapter.getItem(position);
                // Log.v("Before intent: ", movie.getTitle());
                Intent intent = new Intent(getActivity(), DetailActivity.class).putExtra("movie", movie);
                startActivity(intent);
            }
        });

        return rootView;
    }

    // AsyncTask to fetch data from internet in background UI
    public class FetchMovieTask extends AsyncTask<String, Void, Movie[]> {

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        @Override
        protected Movie[] doInBackground(String... params) {

            // ********************** Network working code snippet *********************
            HttpsURLConnection urlConnection = null;
            BufferedReader reader = null;

            String movieJsonStr;

            try {
                final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/discover/movie?";
                final String SORT_PARAM = "sort_by";
                final String APPID_PARAM = "api_key";

                Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_PARAM, params[0])
                        .appendQueryParameter(APPID_PARAM, API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());

                Log.v(LOG_TAG, "Built URI " + builtUri.toString());

                urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                movieJsonStr = buffer.toString();

                Log.v(LOG_TAG, "Movie JSON String: " + movieJsonStr);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            // ********************** Network working code snippet *********************

            // Get human readable data from JSON
            try {
                return getMovieDataFromJson(movieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        // Close the Adapter and transfer returned data from background to main thread
        @Override
        protected void onPostExecute(Movie[] movies) {
            if (movies != null) {
                mPosterAdapter.clear();
                for (Movie m : movies) {
                    mPosterAdapter.add(m);
                }
            }
        }

        // Helper method to get human readable data from JSON
        private Movie[] getMovieDataFromJson(String movieJsonStr) throws JSONException {

            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray("results");

            Movie[] results = new Movie[movieArray.length()];

            for (int i = 0; i < movieArray.length(); i++) {
                JSONObject movie = movieArray.getJSONObject(i);

                long movieId;
                String title;
                String posterPath;
                String overview;
                double rating;
                String date;

                movieId = movie.getLong("id");
                title = movie.getString("original_title");
                posterPath = movie.getString("poster_path");
                overview = movie.getString("overview");
                rating = movie.getDouble("vote_average");
                date = movie.getString("release_date");

                results[i] = new Movie(movieId, title, posterPath, overview, rating, date);
            }

            for (Movie m : results) {
                Log.v(LOG_TAG, "Movie entry: " + m.getInfo());
            }

            return results;
        }
    }

    // Helper method to start an AsyncTask with info from SharedPreferences
    private void updateMovie() {
        // Reference the SharedPreferences
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        // Get info by using key-value pair from the SharedPreferences
        // Tip: 1st arg = key, 2nd arg = default value
        String sortBy = sharedPrefs.getString(getString(R.string.pref_sort_order_key), getString(R.string.pref_sort_order_default));

        FetchMovieTask movieTask = new FetchMovieTask();
        movieTask.execute(sortBy);
    }
}
