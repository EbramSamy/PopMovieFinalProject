package com.example.ebram.popmovies;

import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.ebram.popmovies.Data.MovieContract.PopMovieEntry;
import com.example.ebram.popmovies.Data.MovieContract.RatedMovieEntry;
import com.example.ebram.popmovies.Data.MovieContract.FavoriteMovieEntry;

import com.example.ebram.popmovies.Data.MovieContract;


/**
 * A placeholder fragment containing a simple view.
 */
public class MoviesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public interface Callback {
        public void onItemSelected(Uri dateUri);
    }

    private static final int MOVIE_LOADER = 0;


    private static final String[] POP_MOVIE_COLUMNS = {
            PopMovieEntry.TABLE_NAME + "." + PopMovieEntry.COLUMN_MOVIE_ID,
            PopMovieEntry._ID,
            PopMovieEntry.COLUMN_MOVIE_OVERVIEW,
            PopMovieEntry.COLUMN_MOVIE_TITLE,
            PopMovieEntry.COLUMN_MOVIE_POSTER_PATH,
            PopMovieEntry.COLUMN_MOVIE_VOTE_AVERAGE,
            PopMovieEntry.COLUMN_MOVIE_RELEASE_DATE
    };
    private static final String[] RATED_MOVIE_COLUMNS = {
            RatedMovieEntry.TABLE_NAME + "." + RatedMovieEntry.COLUMN_MOVIE_ID,
            RatedMovieEntry._ID,
            RatedMovieEntry.COLUMN_MOVIE_OVERVIEW,
            RatedMovieEntry.COLUMN_MOVIE_TITLE,
            RatedMovieEntry.COLUMN_MOVIE_POSTER_PATH,
            RatedMovieEntry.COLUMN_MOVIE_VOTE_AVERAGE,
            RatedMovieEntry.COLUMN_MOVIE_RELEASE_DATE
    };
    private static final String[] FAVORITE_MOVIE_COLUMNS = {
            FavoriteMovieEntry.TABLE_NAME + "." + FavoriteMovieEntry.COLUMN_MOVIE_ID,
            FavoriteMovieEntry._ID,
            FavoriteMovieEntry.COLUMN_MOVIE_OVERVIEW,
            FavoriteMovieEntry.COLUMN_MOVIE_TITLE,
            FavoriteMovieEntry.COLUMN_MOVIE_POSTER_PATH,
            FavoriteMovieEntry.COLUMN_MOVIE_VOTE_AVERAGE,
            FavoriteMovieEntry.COLUMN_MOVIE_RELEASE_DATE
    };

    static final int COLUMN_MOVIE_ID = 0;
    static final int _ID = 1;
    static final int COLUMN_MOVIE_OVERVIEW = 2;
    static final int COLUMN_MOVIE_TITLE = 3;
    static final int COLUMN_MOVIE_POSTER_PATH = 4;
    static final int COLUMN_MOVIE_VOTE_AVERAGE = 5;
    static final int COLUMN_MOVIE_RELEASE_DATE = 6;

    //private MovieAdapter movieAdapter;
    private MovieCursorAdapter mMovieCursorAdapter;
    private GridView gridView;

    public MoviesFragment() {

    }

    @Override
    public void onResume() {
        updateMovies();
        super.onResume();
    }


    private void updateMovies() {
        FetchMovies fetchMovies = new FetchMovies(getActivity());
        fetchMovies.execute(Utility.getSortingType(getActivity()));
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movies_fragment_layout, container, false);
        mMovieCursorAdapter = new MovieCursorAdapter(getActivity(), null, 0);
        gridView = (GridView) rootView.findViewById(R.id.gridview);
        gridView.setAdapter(mMovieCursorAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);

                String sort = Utility.getSortingType(getActivity());
                if (sort.equals(getString(R.string.SortHighRated))) {
                    ((Callback) getActivity()).onItemSelected(MovieContract.RatedMovieEntry.buildMovieUri(cursor.getLong(COLUMN_MOVIE_ID)));
                } else if (sort.equals(getString(R.string.SortMostPop))) {
                    ((Callback) getActivity()).onItemSelected(MovieContract.PopMovieEntry.buildMovieUri(cursor.getLong(COLUMN_MOVIE_ID)));

                } else {
                    ((Callback) getActivity()).onItemSelected(MovieContract.FavoriteMovieEntry.buildMovieUri(cursor.getLong(COLUMN_MOVIE_ID)));
                }

            }
        });
        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String sort = Utility.getSortingType(getActivity());
        Uri movieUri;
        if (sort.equals(getString(R.string.SortHighRated))) {
            movieUri = RatedMovieEntry.CONTENT_URI;

            return new CursorLoader(getActivity(),
                    movieUri,
                    RATED_MOVIE_COLUMNS,
                    null,
                    null,
                    null);
        } else if (sort.equals(getString(R.string.SortMostPop))) {
            movieUri = PopMovieEntry.CONTENT_URI;

            return new CursorLoader(getActivity(),
                    movieUri,
                    POP_MOVIE_COLUMNS,
                    null,
                    null,
                    null);
        } else {
            movieUri = FavoriteMovieEntry.CONTENT_URI;

            return new CursorLoader(getActivity(),
                    movieUri,
                    FAVORITE_MOVIE_COLUMNS,
                    null,
                    null,
                    null);
        }
    }


    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mMovieCursorAdapter.swapCursor(cursor);
    }


    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mMovieCursorAdapter.swapCursor(null);
    }

}
