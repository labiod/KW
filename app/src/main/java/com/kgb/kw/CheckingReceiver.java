package com.kgb.kw;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Trace;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * @author Krzysztof Betlej <k.betlej@samsung.com>.
 * @date 11/15/17
 * @copyright Copyright (c) 2016 by Samsung Electronics Polska Sp. z o. o.
 */

public class CheckingReceiver extends BroadcastReceiver {

    public static final int NOTIFICATION_ID = 0x01;
    public static final String TAG = CheckingReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        checkCurrency(context);
    }

    private void checkCurrency(Context context) {
        Intent startIntent = new Intent(context, CurrencyService.class);
        startIntent.setAction(CurrencyService.CHECK_CURRENCY);
        context.startService(startIntent);
    }

}
