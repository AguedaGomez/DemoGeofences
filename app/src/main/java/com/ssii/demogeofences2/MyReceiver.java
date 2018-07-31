package com.ssii.demogeofences2;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Ague on 31/07/2018.
 */

public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Notification notification = new Notification.Builder(context)
                .setContentTitle("Geofence detectada")
                .setContentText("Están dentro de una geofence")
                .setSmallIcon(R.drawable.marker)
                .build();
    }

}
