package com.kgb.kw;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kgb.kw.data.provider.Currency;
import com.kgb.kw.data.provider.CurrencyProvider;
import com.kgb.kw.model.CurrencyModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Krzysztof Betlej <k.betlej@samsung.com>.
 * @date 11/22/17
 * @copyright Copyright (c) 2016 by Samsung Electronics Polska Sp. z o. o.
 */

public class CurrencyAdapter extends BaseAdapter implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TAG = CurrencyAdapter.class.getSimpleName();
    private final List<CurrencyModel> mModelList;
    private Context mContext;

    public CurrencyAdapter(Context context) {
        mContext = context;
        mModelList = new ArrayList<>();
    }
    @Override
    public int getCount() {
        return mModelList.size();
    }

    @Override
    public CurrencyModel getItem(int i) {
        return mModelList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return mModelList.get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return getDropDownView(i, view, viewGroup);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
            holder = new Holder();
            holder.countryCode = convertView.findViewById(android.R.id.text1);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.countryCode.setText(getItem(position).getCode());
        return convertView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri CONTENT_URI = Uri.parse("content://" + CurrencyProvider.CONTENT_URI + "/"
                + Currency.CurrencyContracts.TABLE_NAME);
        Log.d(TAG, "onCreateLoader: create loader uri : " + CONTENT_URI);
        return new CursorLoader(
                mContext,
                CONTENT_URI,
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mModelList.clear();
        if (cursor != null && cursor.moveToFirst()) {
            while (cursor.moveToNext()) {
                mModelList.add(new CurrencyModel(cursor));
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private static class Holder {
        TextView countryCode;
    }
}
