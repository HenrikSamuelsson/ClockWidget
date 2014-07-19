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


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MyWidgetProvider extends AppWidgetProvider {

    //Custom Intent name that is used by the "AlarmManager" to update the clock once per second
    public static String CLOCK_UPDATE = "com.samdide.android.clockwidget.CLOCK_UPDATE";

    private static String hour24Format = "HH";
    private static String hour12Format = "h";
    private static String minuteFormat = "mm";
    private static String dateFormat = "EEE d MMM";
    private static String weekFormat = "w";
    private static String amPmFormat = "a";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {

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
        long oneMinuteFromNow = calendar.getTimeInMillis() + 60 * 1000;
        long nextMinuteRollover = oneMinuteFromNow - (oneMinuteFromNow % (60 * 1000));

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(context.ALARM_SERVICE);
        // Set first alarm to next minute rollover and the repeat the alarm every one minute.
        alarmManager.setRepeating(AlarmManager.RTC, nextMinuteRollover, 60 * 1000, createClockTickIntent(context));


        // Update clock
        ComponentName thisAppWidget = new ComponentName(context.getPackageName(), getClass().getName());
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int ids[] = appWidgetManager.getAppWidgetIds(thisAppWidget);
        for (int appWidgetID: ids){
            upDateAppWidget(context, appWidgetManager, appWidgetID);
        }
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
        Date date = new Date();

        // Check if the user wants 12 or 24 hour clock.
        String value = android.provider.Settings.System.getString(context.getContentResolver(), android.provider.Settings.System.TIME_12_24);

        // Update the hour field.
        SimpleDateFormat sdfHour;
        if(value.equals("12")) {
            sdfHour = new SimpleDateFormat(hour12Format, Locale.getDefault());
        } else {
            sdfHour = new SimpleDateFormat(hour24Format, Locale.getDefault());
        }
        views.setTextViewText(R.id.hour, sdfHour.format(date));

        // Update the minute field.
        SimpleDateFormat sdfMinute;
        sdfMinute = new SimpleDateFormat(minuteFormat, Locale.getDefault());
        views.setTextViewText(R.id.minute, sdfMinute.format(date));

        // Update the am/pm field.
        if(value.equals("12")) {
            SimpleDateFormat sdfAmPm = new SimpleDateFormat(amPmFormat, Locale.getDefault());
            views.setTextViewText(R.id.ampm, sdfAmPm.format(date));
        } else {
            views.setTextViewText(R.id.ampm, "");
        }

        // Update the date field.
        SimpleDateFormat sdfDate = new SimpleDateFormat(dateFormat, Locale.getDefault());
        views.setTextViewText(R.id.date, sdfDate.format(date).toUpperCase());

        // Update the week field.
        SimpleDateFormat sdfWeek = new SimpleDateFormat(weekFormat, Locale.getDefault());
        String w = context.getResources().getString(R.string.week);
        w = w.toUpperCase();
        views.setTextViewText(R.id.week, w + " " + sdfWeek.format(date));

        /*Calendar calendar = Calendar.getInstance();
        views.setTextViewText(R.id.clock, "Time: " + calendar.get(Calendar.HOUR) + ":"
                + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND)
                + "   Week: " + calendar.get(Calendar.WEEK_OF_YEAR));
*/
        //Tell the AppWidgetManager to perform an update on the current app widget
        appWidgetManager.updateAppWidget(appWidgetID, views);
    }
}
