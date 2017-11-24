package com.kgb.kw.data.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * @author Krzysztof Betlej <k.betlej@samsung.com>.
 * @date 11/17/17
 * @copyright Copyright (c) 2016 by Samsung Electronics Polska Sp. z o. o.
 */

public class CurrencyProvider extends ContentProvider {
    public static final String CONTENT_URI = "com.kgb.kw.data.provider";
    // Creates a UriMatcher object.
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    public static final String TAG = CurrencyProvider.class.getSimpleName();

    static {
        sUriMatcher.addURI(CONTENT_URI, Currency.CurrencyRateContracts.TABLE_NAME, 1);
        sUriMatcher.addURI(CONTENT_URI, Currency.CurrencyContracts.TABLE_NAME, 2);
        sUriMatcher.addURI(CONTENT_URI, Currency.CurrencyRateContracts.TABLE_NAME + "/#", 3);
    }

    private Currency mDBHelper;

    @Override
    public boolean onCreate() {
        mDBHelper = new Currency(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Log.d(TAG, "query: test query");
        switch (sUriMatcher.match(uri)) {
            case 1:
                return mDBHelper.getReadableDatabase().query(
                        false,
                        Currency.CurrencyRateContracts.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder,
                        "1"
                );
            case 2:
                return mDBHelper.getReadableDatabase().query(
                        false,
                        Currency.CurrencyContracts.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder,
                        null
                );
        }
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        switch (sUriMatcher.match(uri)) {
            case 1:
                return uri.buildUpon().appendPath(
                        String.valueOf(mDBHelper.getWritableDatabase().insert(
                                Currency.CurrencyRateContracts.TABLE_NAME,
                                null,
                                contentValues)
                        )
                ).build();
            case 2:
                return uri.buildUpon().appendPath(
                        String.valueOf(mDBHelper.getWritableDatabase().insert(
                                Currency.CurrencyContracts.TABLE_NAME,
                                null,
                                contentValues)
                        )
                ).build();
            default:
                return null;
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
