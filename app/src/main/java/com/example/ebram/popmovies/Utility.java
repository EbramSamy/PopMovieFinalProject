package com.example.ebram.popmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Ebram Samy on 9/23/2015.
 */
public class Utility {

    public static String getSortingType(Context context) {
        SharedPreferences prf = PreferenceManager.getDefaultSharedPreferences(context);
        String unitType = prf.getString(context.getString(R.string.pref_sort_key), context.getString(R.string.pref_sort_popular));
        if (unitType.equals(context.getString(R.string.pref_highest_rated))) {
            return context.getString(R.string.SortHighRated);
        } else if (unitType.equals(context.getString(R.string.pref_sort_popular))) {
            return context.getString(R.string.SortMostPop);
        } else {
            return context.getString(R.string.SortFavorite);
        }
    }
}
