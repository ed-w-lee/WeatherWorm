package edlee1.weatherworm;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

/**
 * Receives pending intents from the MainActivity at semi-regular intervals and
 * checks if to show or not show the "Go Outside!" notification.
 */
public class WeatherCheckBroadcastReceiver extends BroadcastReceiver {
    private static int NOTIFICATION_ID = 45109;
    public static int START_MAIN_ACTIVITY_ID = 86439;

    public WeatherCheckBroadcastReceiver() {
    }

    /**
     * When an intent is received, check if the weather is clear and good for having fun, and
     * post a notification of it.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        LocationTracker locationTracker = new LocationTracker(context);
        if (!locationTracker.getTrackingInOperation()) {
            return;
        }
        double lat = locationTracker.getLatitude();
        double lon = locationTracker.getLongitude();
        WeatherGetter weatherGetter = new OpenWeatherMapGetter(lat, lon);
        System.out.println(((OpenWeatherMapGetter)weatherGetter).getWeather());
        if (weatherGetter.isSunny()) {
            /**
             * Based on Android Developer's simple notification code
             */
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.ic_wb_sunny_gold_24dp)
                    .setContentTitle("It's sunny out!")
                    .setContentText("Get outside!");

            Intent resultIntent = new Intent(context, MainActivity.class);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(START_MAIN_ACTIVITY_ID,
                            PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);
            NotificationManager mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        }
        else {
            // Delete the notification if the weather is not clear and sunny
            NotificationManager mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(NOTIFICATION_ID);
        }
    }
}
