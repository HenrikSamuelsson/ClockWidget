package com.samdide.android.clockwidget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MyWidgetProvider extends AppWidgetProvider {

    //Custom Intent name that is used by the "AlarmManager" to update the clock once per second
    public static String CLOCK_UPDATE = "com.samdide.android.clockwidget.CLOCK_UPDATE";
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            int appWidgetId = appWidgetIds[i];

            // Get the layout for the App Widget
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

            //to update the textView
            views.setTextViewText(R.id.clock, "test");

            //Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }

    }

    private PendingIntent createClockTickIntent(Context context){
        Intent intent = new Intent(CLOCK_UPDATE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }
    @Override
    public void onEnabled(Context context){
        super.onEnabled(context);
        Calendar calendar = Calendar.getInstance();
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), 1000, createClockTickIntent(context));
    }
    @Override
    public void onDisabled(Context context){
        super.onDisabled(context);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(context.ALARM_SERVICE);
        alarmManager.cancel(createClockTickIntent(context));
    }

    @Override
    public void onReceive(Context context, Intent intent){
        super.onReceive(context, intent);
        //Log.d("onReceive", "Received intent " + intent);
        if(CLOCK_UPDATE.equals(intent.getAction())){
            ComponentName thisAppWidget = new ComponentName(context.getPackageName(), getClass().getName());
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int ids[] = appWidgetManager.getAppWidgetIds(thisAppWidget);
            for (int appWidgetID: ids){
                upDateAppWidget(context, appWidgetManager, appWidgetID);
            }
        }
    }
    public void upDateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetID){
        // Get the layout for the App Widget
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

        //to update the textView (the clock)
        Calendar calendar = Calendar.getInstance();
        views.setTextViewText(R.id.clock, "Time: " + calendar.get(Calendar.HOUR) + ":"
                + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND)
                + "   Week: " + calendar.get(Calendar.WEEK_OF_YEAR));

        //Tell the AppWidgetManager to perform an update on the current app widget
        appWidgetManager.updateAppWidget(appWidgetID, views);
    }
}
