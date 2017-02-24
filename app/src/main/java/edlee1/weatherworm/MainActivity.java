package edlee1.weatherworm;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

/**
 * WeatherWorm by Edward Lee
 * An app that keeps track of the weather around you and reminds you to go outside when there's
 * good weather!
 *
 * Last updated: 2/22/17 - Cleaned up some code and added some comments
 */
public class MainActivity extends AppCompatActivity {
    // Constants
    private long DEFAULT_DURATION = 60 * 60 * 1000; // Check every hour

    // Arbitrary values
    public static int WEATHER_CHECKER_CODE = 62873;
    public static String SETTINGS_FILENAME = "com.edlee1.weatherworm.settings";
    public static String SETTINGS_TIME_INTERVAL = "check time interval";
    public static String IS_SERVICE_RUNNING = "is service running";

    // Variables
    private long tInterval;
    private boolean isRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Retrieve correct time interval
        SharedPreferences settings = getSharedPreferences(SETTINGS_FILENAME, MODE_PRIVATE);
        tInterval = settings.getLong(SETTINGS_TIME_INTERVAL, DEFAULT_DURATION);

        // Store correct time interval
        if (!settings.contains(SETTINGS_TIME_INTERVAL))
            settings.edit().putLong(SETTINGS_TIME_INTERVAL, tInterval).apply();

        // Check if permissions available to get location and internet
        TextView permission = (TextView) findViewById(R.id.permissions);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                        != PackageManager.PERMISSION_GRANTED) {
            permission.setVisibility(View.VISIBLE);
        } else {
            permission.setVisibility(View.GONE);
        }

        // Check if service is running
        SharedPreferences instances = getPreferences(MODE_PRIVATE);
        isRunning = instances.getBoolean(IS_SERVICE_RUNNING, true);
        setFabImage();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        preferences.edit().putBoolean(IS_SERVICE_RUNNING, isRunning).apply();
    }

    /**
     * Change the Floating Action Button Image to fit correct running state.
     */
    private void setFabImage() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (isRunning) {
            fab.setImageDrawable(
                    getResources().getDrawable(R.drawable.ic_pause_white_24dp, null));
        } else {
            fab.setImageDrawable(
                    getResources().getDrawable(R.drawable.ic_play_arrow_white_24dp, null));
        }
    }

    /**
     * If running, cancel all pending intents.
     * If not running, start running the weather detection service.
     */
    public void toggleWeatherChecker(View v) {
        Intent intent = new Intent(this, WeatherCheckBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, WEATHER_CHECKER_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        if (!isRunning) {
            System.out.println("Running?");
            am.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + tInterval,
                    tInterval, pendingIntent);
            isRunning = true;
            setFabImage();
        } else {
            am.cancel(pendingIntent);
            isRunning = false;
            setFabImage();
        }
    }

}
