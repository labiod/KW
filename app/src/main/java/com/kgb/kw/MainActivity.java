package com.kgb.kw;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.LoaderManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.kgb.kw.data.provider.Currency;
import com.kgb.kw.data.provider.CurrencyProvider;

/**
 * @author Krzysztof Betlej <k.betlej@samsung.com>.
 * @date 11/15/17
 * @copyright Copyright (c) 2016 by Samsung Electronics Polska Sp. z o. o.
 */

public class MainActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final int CHECK_ACTION_FROM_SERVICE = 667;
    public static final int REQUEST_CODE = 669;

    public static final int ALARM_INTERVAL = 60 * 1000;
    public static final String TAG = MainActivity.class.getSimpleName();

    private TextView mSale;
    private TextView mPurchase;
    private final String[] mPermissions = new String[] {
            "android.permission.INTERNET"
    };
    private Spinner mSpinner;
    private CurrencyAdapter mCurrencyAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!checkPermissions(mPermissions)) {
            if (Build.VERSION.SDK_INT >= 23) {
                requestPermissions(mPermissions, REQUEST_CODE);
            }
        } else {
            prepareActivity();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri CONTENT_URI = Uri.parse("content://" + CurrencyProvider.CONTENT_URI + "/"
                + Currency.CurrencyRateContracts.TABLE_NAME);
        Log.d(TAG, "onCreateLoader: create loader uri : " + CONTENT_URI);
        String selection = mCurrencyAdapter.getCount() > 0 ?
                Currency.CurrencyRateContracts.CURRENCY_ID + " = " + mSpinner.getSelectedItemId() :
                null;
        return new CursorLoader(
                this,
                CONTENT_URI,
                null,
                selection,
                null,
                Currency.CurrencyRateContracts.CURRENCY_RATE_DATE + " DESC"
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null && cursor.moveToNext()) {
            final String sale = cursor.getString(cursor.getColumnIndex(Currency.CurrencyRateContracts.CURRENCY_SALE));
            final String purchase = cursor.getString(cursor.getColumnIndex(Currency.CurrencyRateContracts.CURRENCY_PURCHASE));
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mPurchase.setText(purchase);
                    mSale.setText(sale);
                }
            });
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private boolean checkPermissions(String[] permissions) {
        boolean resutl = true;
        if (Build.VERSION.SDK_INT >= 23) {
            for (String per : permissions) {
                if (checkSelfPermission(per) != PackageManager.PERMISSION_GRANTED) {
                    resutl &= false;
                }
            }
        }
        return resutl;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean granted = true;
        for (int result : grantResults) {
            granted &= result == PackageManager.PERMISSION_GRANTED;
        }
        if (granted) {
            prepareActivity();
        }
    }

    private void prepareActivity() {
        mCurrencyAdapter = new CurrencyAdapter(this);
        mSpinner = findViewById(R.id.currency_chooser);
        mSpinner.setAdapter(mCurrencyAdapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                getLoaderManager().restartLoader(0, null, MainActivity.this);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        mSale = findViewById(R.id.sale_value);
        mPurchase = findViewById(R.id.purchase_value);
        ToggleButton toggleButton = findViewById(R.id.enabled_notification);
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                Intent i = new Intent(MainActivity.this, CheckingReceiver.class);
                PendingIntent sender = PendingIntent.getBroadcast(MainActivity.this, CHECK_ACTION_FROM_SERVICE, i, 0);
                AlarmManager am = (AlarmManager) MainActivity.this.getSystemService(Context.ALARM_SERVICE);
                if (am != null) {
                    if (checked) {
                        long firstTime = SystemClock.elapsedRealtime();
                        firstTime += ALARM_INTERVAL;

                        am.setRepeating(AlarmManager.RTC_WAKEUP, firstTime, ALARM_INTERVAL, sender);
                    } else {
                        am.cancel(sender);
                    }
                }
            }
        });

        Button refresh = findViewById(R.id.refresh_button);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLoaderManager().restartLoader(0, null, MainActivity.this);
                getLoaderManager().restartLoader(1, null, mCurrencyAdapter);
            }
        });
        getLoaderManager().initLoader(0, null, this);
        getLoaderManager().initLoader(1, null, mCurrencyAdapter);
    }
}
