package com.example.zhang.popmovies.app;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zhang on 10/10/15.
 */
public class MovieInfo implements Parcelable {

    String movieId;
    String originalTitle;
    String releaseDate;
    String posterPath;
    Double voteAverage;
    String overview;


    public MovieInfo(){}

    public MovieInfo(String movieId, String originalTitle, String overview, String releaseDate,
                     String posterPath, Double voteAverage) {
        this.movieId = movieId;
        this.originalTitle = originalTitle;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.posterPath = posterPath;
        this.voteAverage = voteAverage;
    }

    private MovieInfo(Parcel in) {
        movieId = in.readString();
        originalTitle = in.readString();
        releaseDate = in.readString();
        posterPath = in.readString();
        voteAverage = in.readDouble();
        overview = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(movieId);
        dest.writeString(originalTitle);
        dest.writeString(releaseDate);
        dest.writeString(posterPath);
        dest.writeDouble(voteAverage);
        dest.writeString(overview);
    }

    public static final Parcelable.Creator<MovieInfo> CREATOR = new Parcelable.Creator<MovieInfo>(){
        @Override
        public MovieInfo createFromParcel(Parcel source) {
            return new MovieInfo(source);
        }

        @Override
        public MovieInfo[] newArray(int size) {
            return new MovieInfo[size];
        }
    };

}
