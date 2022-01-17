package com.example.android_network_manager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.example.android_network_manager.R;

/**
 * Implementation of App Widget functionality.
 */
public class CityWifiWidget extends AppWidgetProvider {
    static int level;
    static int wifiRSSIindBm = 0;
    static String wifiSSID = "Aucun wifi";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.wifi_signal_widget);
        //views.setTextViewText(R.id.appwidget_text, widgetText);
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        wifiSSID = wifiInfo.getSSID().replace("\"", "");
        level = SignalWifi(wifiInfo);
        wifiRSSIindBm = wifiInfo.getRssi();

        if(wifiRSSIindBm > -60){
            views.setViewVisibility(R.id.intStrenght, View.VISIBLE);
            views.setViewVisibility(R.id.intStrenght2, View.INVISIBLE);
            views.setViewVisibility(R.id.intStrenght3, View.INVISIBLE);
        } else if(wifiRSSIindBm <= -60 && wifiRSSIindBm >= -80) {
            views.setViewVisibility(R.id.intStrenght2, View.VISIBLE);
            views.setViewVisibility(R.id.intStrenght, View.INVISIBLE);
            views.setViewVisibility(R.id.intStrenght3, View.INVISIBLE);
        } else if(wifiRSSIindBm < -80){
            views.setViewVisibility(R.id.intStrenght, View.INVISIBLE);
            views.setViewVisibility(R.id.intStrenght2, View.INVISIBLE);
            views.setViewVisibility(R.id.intStrenght3, View.VISIBLE);
        }
//        views.setProgressBar(R.id.progressBarGreen,100,level,false);
        views.setTextViewText(R.id.appwidget_text ,wifiSSID);
        views.setTextViewText(R.id.intStrenght ,""+level+"%");
        views.setTextViewText(R.id.intStrenght2,""+level+"%");
        views.setTextViewText(R.id.intStrenght3,""+level+"%");

        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        //Create an Intent with the AppWidgetManager.ACTION_APPWIDGET_UPDATE action//
        Intent intentUpdate = new Intent(context, CityWifiWidget.class);
        intentUpdate.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

        //Update the current widget instance only, by creating an array that contains the widget’s unique ID//
        int[] idArray = new int[]{appWidgetId};
        intentUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, idArray);

        //Wrap the intent as a PendingIntent, using PendingIntent.getBroadcast()//
        PendingIntent pendingUpdate = PendingIntent.getBroadcast(
                context, appWidgetId, intentUpdate,
                PendingIntent.FLAG_UPDATE_CURRENT);

        alarm.cancel(pendingUpdate);
        long interval = 0;
        alarm.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(),interval,pendingUpdate);

        appWidgetManager.updateAppWidget(appWidgetId, views);

    }

    private static int SignalWifi(WifiInfo wifiInfo) {
        int numberOfLevels = 100;
        int level = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), numberOfLevels);
        return level;
    }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds){
            updateAppWidget(context,appWidgetManager,appWidgetId);
        }
    }
}