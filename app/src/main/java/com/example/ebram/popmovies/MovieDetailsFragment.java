package com.example.ebram.popmovies;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ebram.popmovies.Data.MovieContract;
import com.squareup.picasso.Picasso;
import com.example.ebram.popmovies.Data.MovieContract.PopMovieEntry;
import com.example.ebram.popmovies.Data.MovieContract.RatedMovieEntry;
import com.example.ebram.popmovies.Data.MovieContract.FavoriteMovieEntry;
import com.example.ebram.popmovies.Data.MovieContract.ReviewsTrailersMovieEntry;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    static final String DETAIL_URI = "URI";
    private Uri mUri;
    private LinearLayout favButton;
    private TextView favTextView;
    private ImageView favImageView;

    private ShareActionProvider mShareActionProvider;
    private static final int DETAIL_LOADER = 1;
    private static final int REVIEWS_TRAILERS_LOADER = 2;

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


    private static final String[] REVIEWS_TRAILERS_MOVIE_COLUMNS = {
            ReviewsTrailersMovieEntry.TABLE_NAME + "." + ReviewsTrailersMovieEntry._ID,
            ReviewsTrailersMovieEntry.COLUMN_REVIEW_TRAILER_ID,
            ReviewsTrailersMovieEntry.COLUMN_REVIEW_TRAILER_MOVIE_ID,
            ReviewsTrailersMovieEntry.COLUMN_REVIEW_TRAILER_NAME,
            ReviewsTrailersMovieEntry.COLUMN_REVIEW_TRAILER_CONTENT,
            ReviewsTrailersMovieEntry.COLUMN_REVIEW_TRAILER_TYPE
    };

    static final int COLUMN_MOVIE_ID = 0;
    static final int _ID = 1;
    static final int COLUMN_MOVIE_OVERVIEW = 2;
    static final int COLUMN_MOVIE_TITLE = 3;
    static final int COLUMN_MOVIE_POSTER_PATH = 4;
    static final int COLUMN_MOVIE_VOTE_AVERAGE = 5;
    static final int COLUMN_MOVIE_RELEASE_DATE = 6;

    static final int R_ID = 0;
    static final int COLUMN_REVIEW_TRAILER_ID = 1;
    static final int COLUMN_REVIEW_TRAILER_MOVIE_ID = 2;
    static final int COLUMN_REVIEW_TRAILER_NAME = 3;
    static final int COLUMN_REVIEW_TRAILER_CONTENT = 4;
    static final int COLUMN_REVIEW_TRAILER_TYPE = 5;


    private DetailsCursorAdaptor mDetailsCursorAdaptor;

    public MovieDetailsFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_movie_details, menu);


        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);


        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(getActivity(), com.example.ebram.popmovies.SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Intent createShareForecastIntent(String shared) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shared);
        return shareIntent;
    }

    @Override
    public void onStart() {
        FetchTrailersAndReviews fetchTrailersAndReviews = new FetchTrailersAndReviews(getActivity());
        fetchTrailersAndReviews.execute(mUri.getLastPathSegment());
        super.onStart();
    }

    private ListView detailsListView;
    private Uri mov_fav_uri;
    private Uri fav_id_uri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(MovieDetailsFragment.DETAIL_URI);
        }
        View rootView = inflater.inflate(R.layout.fragment_movie_details, container, false);
        mov_fav_uri = MovieContract.FavoriteMovieEntry.CONTENT_URI.buildUpon().appendEncodedPath(mUri.getPathSegments().get(0)).appendEncodedPath(mUri.getLastPathSegment()).build();
        fav_id_uri = MovieContract.FavoriteMovieEntry.CONTENT_URI.buildUpon().appendEncodedPath(mUri.getLastPathSegment()).build();
        mDetailsCursorAdaptor = new DetailsCursorAdaptor(getActivity(), null, 0);
        detailsListView = (ListView) rootView.findViewById(R.id.details_listView);
        ViewGroup header = (ViewGroup) inflater.inflate(R.layout.header_layout, detailsListView, false);
        detailsListView.addHeaderView(header, null, false);
        detailsListView.setAdapter(mDetailsCursorAdaptor);


        favButton = (LinearLayout) rootView.findViewById(R.id.favorite_button);
        favTextView = (TextView) rootView.findViewById(R.id.fav_text);
        favImageView = (ImageView) rootView.findViewById(R.id.fav_img);
        favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (favTextView.getCurrentTextColor() == getResources().getColor(R.color.myGold)) {
                    favImageView.setImageResource(R.drawable.ic_add_fav);
                    favTextView.setText("REMOVE FAVORITE");
                    favTextView.setTextColor(getResources().getColor(R.color.myGray));
                    getActivity().getContentResolver().insert(mov_fav_uri, null);
                } else {
                    favImageView.setImageResource(R.drawable.ic_rem_fav);
                    favTextView.setText("MARK AS FAVORITE");
                    favTextView.setTextColor(getResources().getColor(R.color.myGold));


                    getActivity().getContentResolver().delete(fav_id_uri, null, new String[]{fav_id_uri.getLastPathSegment().toString()});
                }
            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        getLoaderManager().initLoader(REVIEWS_TRAILERS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        switch (id) {
            case DETAIL_LOADER: {
                String sort = Utility.getSortingType(getActivity());
                if (sort.equals(getString(R.string.SortHighRated))) {

                    return new CursorLoader(getActivity(),
                            mUri,
                            RATED_MOVIE_COLUMNS,
                            null,
                            null,
                            null);
                } else if (sort.equals(getString(R.string.SortMostPop))) {
                    return new CursorLoader(getActivity(),
                            mUri,
                            POP_MOVIE_COLUMNS,
                            null,
                            null,
                            null);
                } else {
                    return new CursorLoader(getActivity(),
                            mUri,
                            FAVORITE_MOVIE_COLUMNS,
                            null,
                            null,
                            null);
                }
            }
            case REVIEWS_TRAILERS_LOADER: {
                Uri movieTrailerUri;
                movieTrailerUri = ReviewsTrailersMovieEntry.CONTENT_URI.buildUpon().appendEncodedPath(mUri.getLastPathSegment()).build();
                return new CursorLoader(getActivity(),
                        movieTrailerUri,
                        REVIEWS_TRAILERS_MOVIE_COLUMNS,
                        null,
                        null,
                        null);
            }

        }
        return null;
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int id = loader.getId();
        switch (id) {
            case DETAIL_LOADER: {
                if (!data.moveToFirst()) {
                    return;
                }

                if (getActivity().getContentResolver().query(fav_id_uri, FAVORITE_MOVIE_COLUMNS, null, null, null).getCount() == 0) {
                    favImageView.setImageResource(R.drawable.ic_rem_fav);
                    favTextView.setText("MARK AS FAVORITE");
                    favTextView.setTextColor(getResources().getColor(R.color.myGold));
                } else {
                    favImageView.setImageResource(R.drawable.ic_add_fav);
                    favTextView.setText("REMOVE FAVORITE");
                    favTextView.setTextColor(getResources().getColor(R.color.myGray));
                }

                TextView releaseDate = (TextView) getView().findViewById(R.id.dateText);

                TextView title = (TextView) getView().findViewById(R.id.titleText);

                TextView overView = (TextView) getView().findViewById(R.id.overviewText);
                TextView rate = (TextView) getView().findViewById(R.id.rateTextView);

                ImageView postImg = (ImageView) getView().findViewById(R.id.posterImg);

                releaseDate.setText(data.getString(COLUMN_MOVIE_RELEASE_DATE));
                title.setText(data.getString(COLUMN_MOVIE_TITLE));
                rate.setText(data.getString(COLUMN_MOVIE_VOTE_AVERAGE) + " / 10.0");
                overView.setText(data.getString(COLUMN_MOVIE_OVERVIEW));
                URL url = null;
                try {
                    url = new URL(data.getString(COLUMN_MOVIE_POSTER_PATH));
                    Picasso.with(getActivity()).load(url.toString()).into(postImg);
                } catch (MalformedURLException e) {
                    Toast toast = Toast.makeText(getActivity(), "No Poster", Toast.LENGTH_SHORT);
                    toast.show();
                }
                break;
            }
            case REVIEWS_TRAILERS_LOADER: {

                if (data.getCount() != 0) {
                    data.moveToFirst();
                    if (data.getInt(COLUMN_REVIEW_TRAILER_TYPE) == 1) {
                        String share = data.getString(COLUMN_REVIEW_TRAILER_CONTENT);
                        if (share != null) {
                            Intent intent = createShareForecastIntent(share);
                            if (mShareActionProvider != null) {
                                mShareActionProvider.setShareIntent(intent);
                            }
                        }
                    } else {
                        Intent intent = createShareForecastIntent("No thing to share");
                        if (mShareActionProvider != null) {
                            mShareActionProvider.setShareIntent(intent);
                        }
                    }

                } else {
                    Intent intent = createShareForecastIntent("No thing to share");
                    if (mShareActionProvider != null) {
                        mShareActionProvider.setShareIntent(intent);
                    }
                }


                mDetailsCursorAdaptor.swapCursor(data);

            }
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() != DETAIL_LOADER) {
            mDetailsCursorAdaptor.swapCursor(null);
        }
    }
}
