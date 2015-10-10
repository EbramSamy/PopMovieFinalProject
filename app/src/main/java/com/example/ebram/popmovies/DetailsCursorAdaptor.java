package com.example.ebram.popmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.net.URI;


/**
 * Created by Ebram Samy on 10/8/2015.
 */
public class DetailsCursorAdaptor extends CursorAdapter {

    private static final int REVIEW_TYPE = 0;
    private static final int TRAILER_TYPE = 1;
    private static final int VIEW_TYPE_COUNT = 2;

    public DetailsCursorAdaptor(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int type = getItemViewType(cursor.getPosition());
        View view = null;
        switch (type) {
            case TRAILER_TYPE: {
                view = LayoutInflater.from(context).inflate(R.layout.trailer_layout, parent, false);
                TrailerViewHolder trailerViewHolder = new TrailerViewHolder(view);
                view.setTag(trailerViewHolder);
                return view;
            }
            case REVIEW_TYPE: {
                view = LayoutInflater.from(context).inflate(R.layout.review_layout, parent, false);
                ReviewViewHolder reviewHolder = new ReviewViewHolder(view);
                view.setTag(reviewHolder);
                return view;
            }
        }
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        int type = getItemViewType(cursor.getPosition());
        switch (type) {
            case TRAILER_TYPE: {
                TrailerViewHolder trailerViewHolder = (TrailerViewHolder) view.getTag();
                String trailerName = cursor.getString(MovieDetailsFragment.COLUMN_REVIEW_TRAILER_NAME);
                final String newVideoPath = cursor.getString(MovieDetailsFragment.COLUMN_REVIEW_TRAILER_CONTENT);

                TextView trailerTextView = trailerViewHolder.trailerName;


                trailerTextView.setText(trailerName);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(newVideoPath));
                        mContext.startActivity(intent);
                    }
                });
                break;
            }
            case REVIEW_TYPE: {
                ReviewViewHolder reviewViewHolder = (ReviewViewHolder) view.getTag();
                String author = cursor.getString(MovieDetailsFragment.COLUMN_REVIEW_TRAILER_NAME);
                String content = cursor.getString(MovieDetailsFragment.COLUMN_REVIEW_TRAILER_CONTENT);

                TextView reviewAuthor = reviewViewHolder.reviewAuthor;
                reviewAuthor.setText(author + " : ");

                TextView reviewContent = reviewViewHolder.reviewContent;
                reviewContent.setText(content);
                break;
            }
        }

    }


    @Override
    public int getItemViewType(int position) {
        Cursor cursor = getCursor();
        cursor.moveToPosition(position);
        return cursor.getInt(MovieDetailsFragment.COLUMN_REVIEW_TRAILER_TYPE);
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    public static class ReviewViewHolder {

        public final TextView reviewAuthor;

        public final TextView reviewContent;

        public ReviewViewHolder(View view) {
            reviewAuthor = (TextView) view.findViewById(R.id.review_author);
            reviewContent = (TextView) view.findViewById(R.id.review_content);
        }
    }

    public static class TrailerViewHolder {

        public final TextView trailerName;

        public TrailerViewHolder(View view) {
            trailerName = (TextView) view.findViewById(R.id.trailer_name);
        }
    }

}
