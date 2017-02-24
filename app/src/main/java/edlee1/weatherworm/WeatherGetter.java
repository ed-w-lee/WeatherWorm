package edlee1.weatherworm;

import org.json.JSONObject;

/**
 * An interface on how checking weather should be implemented.
 * Currently only supports checking if weather is sunny, but more features can be added if wanted.
 * Also, might be good to somehow move to a database if possible.
 */

public interface WeatherGetter {
    /**
     * Gets the current weather as a JSONObject
     */
    public JSONObject retrieveWeather();

    /**
     * Checks if the current weather is sunny & clear.
     */
    public boolean isSunny();
}
