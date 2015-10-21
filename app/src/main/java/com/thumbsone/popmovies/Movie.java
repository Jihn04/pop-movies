package com.thumbsone.popmovies;

import android.os.Parcel;
import android.os.Parcelable;

// Movie class to store data for a movie
public class Movie implements Parcelable {

    static final String BASE_URL = "http://image.tmdb.org/t/p/";
    static final String IMAGE_SIZE = "w185/";

    private long movieId;
    private String title;
    private String posterPath;
    private String overview;
    private double rating;
    private String date;

    public Movie(long movieId, String title, String posterPath, String overview, double rating, String date) {
        this.movieId = movieId;
        this.title = title;
        this.posterPath = posterPath;
        this.overview = overview;
        this.rating = rating;
        this.date = date;
    }

    public Movie(Parcel source) {
        this.movieId = source.readLong();
        this.title = source.readString();
        this.posterPath = source.readString();
        this.overview = source.readString();
        this.rating = source.readDouble();
        this.date = source.readString();
    }

    public String getInfo() {
        return movieId + " " + posterPath + " " + title;
    }

    public String getPath() {
        return BASE_URL + IMAGE_SIZE + posterPath;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public double getRating() {
        return rating;
    }

    public String getOverview() {
        return overview;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(movieId);
        dest.writeString(title);
        dest.writeString(posterPath);
        dest.writeString(overview);
        dest.writeDouble(rating);
        dest.writeString(date);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
