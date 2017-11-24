package com.kgb.kw.model;

import android.database.Cursor;

import com.kgb.kw.data.provider.Currency;

/**
 * @author Krzysztof Betlej <k.betlej@samsung.com>.
 * @date 11/23/17
 * @copyright Copyright (c) 2016 by Samsung Electronics Polska Sp. z o. o.
 */

public class CurrencyModel {
    private long mId;
    private String mCurrencyCode;
    private String mCurrencyFullName;

    public CurrencyModel(Cursor cursor) {
        mId = cursor.getLong(cursor.getColumnIndex(Currency.CurrencyContracts._ID));
        mCurrencyCode = cursor.getString(cursor.getColumnIndex(Currency.CurrencyContracts.CURRENCY_NAME));
        mCurrencyFullName = mCurrencyCode;
    }

    public long getId() {
        return mId;
    }

    public String getCode() {
        return mCurrencyCode;
    }
}
