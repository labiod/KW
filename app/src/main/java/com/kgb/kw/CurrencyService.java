package com.kgb.kw;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kgb.kw.data.provider.Currency;
import com.kgb.kw.data.provider.CurrencyProvider;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CurrencyService extends Service implements Response.ErrorListener, Response.Listener<String> {
    public static final int NOTIFICATION_ID = 0x01;

    public static final String CHECK_CURRENCY = "check_currency";
    public static final String TAG = CurrencyService.class.getSimpleName();

    public CurrencyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                case CHECK_CURRENCY:
                    String channelId = "my_channel_01";
                    String name = "Ch1";
                    String desc = "Hello World!";
                    sendMsgToChannel(this, channelId, name, desc);
                    checkLastCurrency();
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }

    @Override
    public void onResponse(String response) {
        Document document = Jsoup.parse(response);
        Elements elements = document.select("span.__currency-rate");
        long scribeDate = System.currentTimeMillis();
        Uri CURRENCY_URI = Uri.parse("content://" + CurrencyProvider.CONTENT_URI + "/" + Currency.CurrencyRateContracts.TABLE_NAME);
        for (Element element : elements) {
            ContentValues newCurrency = new ContentValues();
            Log.d(TAG, "onResponse: element:" + element);
            Log.d(TAG, "onResponse: element val:" + element.html());
            if (element.hasAttr("data-quantity")) {
                newCurrency.put(Currency.CurrencyRateContracts.CURRENCY_ID, getCurrencyTypeId(element.attr("data-currency")));
                newCurrency.put(Currency.CurrencyRateContracts.CURRENCY_QUANTITY, element.attr("data-quantity"));
                newCurrency.put(Currency.CurrencyRateContracts.CURRENCY_SALE, element.attr("data-amount"));
                newCurrency.put(Currency.CurrencyRateContracts.CURRENCY_PURCHASE, element.html());
                newCurrency.put(Currency.CurrencyRateContracts.CURRENCY_RATE_DATE, scribeDate);
                getContentResolver().insert(CURRENCY_URI, newCurrency);
            }
        }
    }

    private int getCurrencyTypeId(String currencyName) {
        Uri CURRENCY_TYPE_URI = Uri.parse("content://" + CurrencyProvider.CONTENT_URI + "/" + Currency.CurrencyContracts.TABLE_NAME);
        Cursor cursor = getContentResolver().query(
                CURRENCY_TYPE_URI,
                null,
                Currency.CurrencyContracts.CURRENCY_NAME + " = ?",
                new String[] {currencyName},
                null
        );
        if (cursor != null) {
            if (cursor.getCount() == 1 && cursor.moveToFirst()) {
                Log.d(TAG, "getCurrencyTypeId: found currency");
                return cursor.getInt(cursor.getColumnIndex(Currency.CurrencyContracts._ID));
            } else {
                ContentValues values = new ContentValues();
                values.put(Currency.CurrencyContracts.CURRENCY_NAME, currencyName);
                Uri uri = getContentResolver().insert(CURRENCY_TYPE_URI, values);
                if (uri != null) {
                    return Integer.parseInt(uri.getLastPathSegment());
                }
            }
            cursor.close();
        }
        return -1;
    }

    private void checkLastCurrency() {
        String url = "https://kantor.aliorbank.pl/forex";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(url, this, this);
        requestQueue.add(stringRequest);
    }

    private void sendMsgToChannel(Context context, String channelId, String channelName, String text) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            if (Build.VERSION.SDK_INT >= 26) {
                NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
                channel.setDescription(text);
                channel.enableLights(true);
                channel.setLightColor(Color.RED);
                notificationManager.createNotificationChannel(channel);
            }
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId);
            builder.setContentTitle("First notification");
            builder.setContentText(text);
            builder.setSmallIcon(R.drawable.ic_launcher_background);
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }
}
