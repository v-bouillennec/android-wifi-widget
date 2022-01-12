package com.example.myapplication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

/**
 * Implementation of App Widget functionality.
 */
public class NewAppWidget extends AppWidgetProvider {
    static int level;
    static int wifiRSSIindBm = 0;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
        //views.setTextViewText(R.id.appwidget_text, widgetText);
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        level = SignalWifi(wifiInfo);
        wifiRSSIindBm = getWifiSignalLoss(wifiInfo);

        if(level > 50){
            views.setViewVisibility(R.id.intStrenght, View.VISIBLE);
            views.setViewVisibility(R.id.intStrenght2, View.INVISIBLE);
            views.setViewVisibility(R.id.intStrenght3, View.INVISIBLE);
        } else if (level <= 50 && level >20) {
            views.setViewVisibility(R.id.intStrenght2, View.VISIBLE);
            views.setViewVisibility(R.id.intStrenght, View.INVISIBLE);
            views.setViewVisibility(R.id.intStrenght3, View.INVISIBLE);
        } else if (level <= 20) {
            views.setViewVisibility(R.id.intStrenght, View.INVISIBLE);
            views.setViewVisibility(R.id.intStrenght2, View.INVISIBLE);
            views.setViewVisibility(R.id.intStrenght3, View.VISIBLE);
        }
//        views.setProgressBar(R.id.progressBarGreen,100,level,false);
        views.setTextViewText(R.id.intStrenght ,""+level+"\n"+wifiRSSIindBm+"dBm");
        views.setTextViewText(R.id.intStrenght2,""+level+"\n"+wifiRSSIindBm+"dBm");
        views.setTextViewText(R.id.intStrenght3,""+level+"\n"+wifiRSSIindBm+"dBm");

        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);


        //Create an Intent with the AppWidgetManager.ACTION_APPWIDGET_UPDATE action//

        Intent intentUpdate = new Intent(context, NewAppWidget.class);
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

        //Send the pending intent in response to the user tapping the ‘Update’ TextView//

        //views.setOnClickPendingIntent(R.id.buttonUpdate, pendingUpdate);

        // Instruct the widget manager to update the widget
                appWidgetManager.updateAppWidget(appWidgetId, views);

    }

    private static int SignalWifi(WifiInfo wifiInfo) {
        int numberOfLevels = 100;
        int level = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), numberOfLevels);
        Log.i("log", wifiInfo.getRssi()+"");
        return level;
    }

    private static int getWifiSignalLoss(WifiInfo wifiInfo) {
        return wifiInfo.getRssi();
    }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds){
            updateAppWidget(context,appWidgetManager,appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    /*private static void ColorProgress(){
        ProgressBar progressBarColor;
        switch (level){
            case 35 : progressBarColor.setBackgroundColor(Integer.parseInt("#FF0000"));
            break;
            case 75 : progressBarColor.setBackgroundColor(Integer.parseInt("#FFA500"));
            break;
            case 100 : progressBarColor.setBackgroundColor(Integer.parseInt("#008000"));
        }
    }*/
}