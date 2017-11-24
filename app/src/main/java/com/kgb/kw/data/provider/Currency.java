package com.kgb.kw.data.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * @author Krzysztof Betlej <k.betlej@samsung.com>.
 * @date 11/22/17
 * @copyright Copyright (c) 2016 by Samsung Electronics Polska Sp. z o. o.
 */

public class Currency extends SQLiteOpenHelper {
    public static final class CurrencyRateContracts {
        public static final String TABLE_NAME = "currency_rate";
        public static final String _ID = "id_rate";
        public static final String CURRENCY_ID = "currency_id";
        public static final String CURRENCY_RATE_DATE = "currency_rate_date";
        public static final String CURRENCY_SALE = "currency_sale";
        public static final String CURRENCY_PURCHASE = "currency_purchase";
        public static final String CURRENCY_QUANTITY = "currency_quantity";
    }

    public static  final class CurrencyContracts {
        public static final String TABLE_NAME = "currency";
        public static final String _ID = "id_currency";
        public static final String CURRENCY_NAME = "currency_name";
    }

    private static final String SQL_CREATE_CURRENCY =
            "CREATE TABLE " + CurrencyRateContracts.TABLE_NAME + "(" +
                    CurrencyRateContracts._ID + " INTEGER PRIMARY KEY, " +
                    CurrencyRateContracts.CURRENCY_ID + " TEXT, " +
                    CurrencyRateContracts.CURRENCY_QUANTITY + " INTEGER, " +
                    CurrencyRateContracts.CURRENCY_SALE + " DECIMAL(10,5), " +
                    CurrencyRateContracts.CURRENCY_PURCHASE + " DECIMAL(10,5), " +
                    CurrencyRateContracts.CURRENCY_RATE_DATE + " INTEGER )";

    private static final String SQL_CREATE_CURRENCY_TYPE =
            "CREATE TABLE " + CurrencyContracts.TABLE_NAME + "(" +
                    CurrencyContracts._ID + " INTEGER PRIMARY KEY, " +
                    CurrencyContracts.CURRENCY_NAME + " TEXT )";
    public static final String DBNAME = "currency_db";

    Currency(Context context) {
        super(context, DBNAME, null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_CURRENCY);
        sqLiteDatabase.execSQL(SQL_CREATE_CURRENCY_TYPE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
