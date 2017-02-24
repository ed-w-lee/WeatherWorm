package edlee1.weatherworm;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A WeatherGetter based on the OpenWeatherMap API.
 * See WeatherGetter for more information.
 */
public class OpenWeatherMapGetter implements WeatherGetter{
    /**
     * TODO: Add additional options for location
     * Options for how to format location parameter for getting weather
     * 0 - lat, lon
     * 1 - city
     * 2 - zip code
     */
//    private int option;

    private double mCoordLat;
    private double mCoordLon;

    private JSONObject mWeather;

    // TODO: Get a server and abstract the API key away from the user to prevent misuse
    private String API_KEY = "1d0b74aec5bbd9be21906e573eccf547";
    private String OWM_URL = "http://api.openweathermap.org/data/2.5/weather";

    /**
     * Constructor with latitude and longitude
     */
    public OpenWeatherMapGetter(double lat, double lon) {
        mCoordLat = lat;
        mCoordLon = lon;
        retrieveWeather();
    }

    /**
     * Gets the weather through the OpenWeatherMap API
     * @return weather as a JSON
     */
    @Override
    public JSONObject retrieveWeather() {
        try {
            mWeather = new RetrieveWeatherTask().execute().get();
            return mWeather;
        } catch (Exception e) {
            Log.e("error", e.toString());
        }
        return null;
    }

    /**
     * Class intended to help asynchronously get the weather from OWM.
     */
    private class RetrieveWeatherTask extends AsyncTask<Void, Void, JSONObject> {

        /**
         * Attempts to query the OpenWeatherMap API for the weather.
         * @param params the latitude and longitude, which do not need to be passed in
         * @return the weather as a JSON object
         */
        @Override
        protected JSONObject doInBackground(Void... params) {
            JSONObject newWeather;
            HttpURLConnection conn = null;
            try {

                // Build query to API
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("lat", Double.toString(mCoordLat))
                        .appendQueryParameter("lon", Double.toString(mCoordLon))
                        .appendQueryParameter("APPID", API_KEY);
                String query = builder.build().getEncodedQuery();

                // Finish building query
                URL url = new URL(OWM_URL+"?"+query);
                conn = (HttpURLConnection) url.openConnection();
                // Create http connections
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // Connect to the API
                conn.connect();

                // Get response
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    String strWeather = (new BufferedReader(
                            new InputStreamReader(conn.getInputStream()))).readLine();
                    newWeather = new JSONObject(strWeather);
                }
                else {
                    newWeather = null;
                }
            } catch (Exception e) {
                Log.e("HTTP error", e.toString());
                newWeather = null;
            } finally {
                if (conn != null) conn.disconnect();
            }
            return newWeather;
        }
    }

    /**
     * Checks if the current weather retrieved from OpenWeatherMap is sunny (clear and daytime)
     * @return boolean that is true when weather is currently day
     */
    @Override
    public boolean isSunny() {
        try {
            if (mWeather == null) {
                Log.i("No Weather", "No Weather");
                return false;
            }
            String currWeather = ((JSONArray) mWeather.get("weather")).getJSONObject(0)
                    .getString("main");

            long sunriseTime = mWeather.getJSONObject("sys").getLong("sunrise");
            long sunsetTime = mWeather.getJSONObject("sys").getLong("sunset");
            long unixTime = System.currentTimeMillis() / 1000L;

            // Returns true if weather is clear and still currently day
            return (currWeather.equals("Clear") && unixTime < sunsetTime && unixTime > sunriseTime);
        } catch (JSONException e) {
            return false;
        }
    }

    /**
     * Getter for the weather
     * @return the weather
     */
    public JSONObject getWeather() {
        return mWeather;
    }
}
