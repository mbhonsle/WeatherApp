package com.mbhonsle.weatherapp.util;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.MessageFormat;

/**
 * Created by mak on 12/16/17.
 */

public class WeatherUtil {

    private static String WEATHER_CONSTANT = "weather";
    private static String MAIN_WEATHER_CONSTANT = "main";
    private static String WEATHER_DESC_CONSTANT = "description";
    private static String TEMP_CONSTANT = "temp";
    private static String PRESSURE_CONSTANT = "pressure";
    private static String HUMIDITY_CONSTANT = "humidity";
    private static String TEMP_MIN_CONSTANT = "temp_min";
    private static String TEMP_MAX_CONSTANT = "temp_max";
    private static String WIND_CONSTANT = "wind";
    private static String WIND_SPEED_CONSTANT = "speed";
    private static String WIND_DEG_CONSTANT = "deg";
    private static String CLOUDS_CONSTANT = "clouds";
    private static String CLOUDS_ALL_CONSTANT = "all";
    private static final String WEATHER_URL_CITY_NAME = "http://api.openweathermap.org/data/2.5/weather?q={0}&appid={1}&units=metric";
    private static final String WEATHER_URL_COORDINATES = "http://api.openweathermap.org/data/2.5/weather?lat={0}&lon={1}&appid={2}&units=metric";
    private static final String API_KEY = "<API-KEY>";
    private static final String ERR_MSG = "WEATHER DATA FETCH ERROR";

    private String summary = "";
    private String weatherDataTemplate =
            "\nSummary:                 {0} \n" +
                    "Description:               {1} \n" +
                    "\nWeather Details:\n" +
                    "Temperature:               {2} Celsius \n" +
                    "Pressure:                  {3} \n" +
                    "Humidity:                  {4} \n" +
                    "Minimum Temperature:       {5} Celsius \n" +
                    "Maximum Temperature:       {6} Celsius \n" +
                    "\nWind Details: \n" +
                    "Speed:                     {7} \n" +
                    "Angle:                     {8} \n" +
                    "\nCloud Details: \n" +
                    "All:                       {9}";


    public String getCityWeatherWithCityName(String cityName) {
        try {
            return parseWeatherData(queryWithCityName(cityName));
        } catch (Exception e) {
            logError("Download data", e);
        }

        return "";
    }

    public JSONObject queryWithCityName(String cityName) {
        try {
            return new DownloadWeatherWithCityName().execute(cityName).get();
        } catch (Exception e) {
            logError("WEATHER DATA FETCH FAILED w/ CITYNAME", e);
        }

        return null;
    }

    public String getCityWeatherWithCoordinate(String[] coordinates) {
        try {
            return parseWeatherData(queryWithCoordinates(coordinates));
        } catch (Exception e) {
            logError("Download data", e);
        }

        return "";
    }

    public JSONObject queryWithCoordinates(String[] coordinates) {
        try {
            return new DownloadWeatherWithCoordinates().execute(coordinates).get();
        } catch (Exception e) {
            logError("WEATHER DATA FETCH FAILED w/ COORDINATES", e);
        }

        return null;
    }

    public String getSummary() {
        return this.summary;
    }

    private String parseWeatherData(JSONObject baseObject) throws Exception {
        JSONObject summary = parseSummaryWeather(baseObject);
        JSONObject main = parseMainWeather(baseObject);
        JSONObject wind = parseWindDetails(baseObject);
        JSONObject clouds = parsecCloudDetails(baseObject);
        String[] results = new String[10];
        parseJsonDetails(summary, results, 0, MAIN_WEATHER_CONSTANT, WEATHER_DESC_CONSTANT);
        parseJsonDetails(main, results, 2, TEMP_CONSTANT, PRESSURE_CONSTANT, HUMIDITY_CONSTANT,
                TEMP_MIN_CONSTANT, TEMP_MAX_CONSTANT);
        parseJsonDetails(wind, results, 7, WIND_SPEED_CONSTANT, WIND_DEG_CONSTANT);
        parseJsonDetails(clouds, results, 9, CLOUDS_ALL_CONSTANT);
        this.summary = results[0];
        return MessageFormat.format(this.weatherDataTemplate, results);
    }

    private void parseJsonDetails(JSONObject object, String[] results, int start, String... keys) throws JSONException {
        int i = start;
        for (String s : keys) {
            try {
                Object o = object.get(s);
                if (o instanceof Double) {
                    results[i++] = Double.toString((Double) o);
                } else {
                    results[i++] = o.toString();
                }
            } catch (Exception e) {
                Log.w("PROPERTY NOT FOUND", s);
                results[i++] = "Not Available";
            }
        }
    }

    private JSONObject parsecCloudDetails(JSONObject baseObject) throws JSONException {
        return baseObject.getJSONObject(CLOUDS_CONSTANT);
    }

    private JSONObject parseWindDetails(JSONObject baseObject) throws JSONException {
        return baseObject.getJSONObject(WIND_CONSTANT);
    }

    private JSONObject parseMainWeather(JSONObject baseObject) throws JSONException {
        return baseObject.getJSONObject(MAIN_WEATHER_CONSTANT);
    }

    private JSONObject parseSummaryWeather(JSONObject baseObject) throws JSONException {
        Object o = baseObject.get(WEATHER_CONSTANT);
        return o instanceof JSONObject ? (JSONObject) o : (JSONObject) ((JSONArray) o).get(0);
    }

    static void logError(String customMsg, Exception e) {
        Log.e(ERR_MSG + ": " + customMsg, e.getMessage());
    }

    static String buildQueryUrlWithCityName(String cityName) throws UnsupportedEncodingException {
        return MessageFormat.format(WEATHER_URL_CITY_NAME, URLEncoder.encode(cityName, "UTF-8"), API_KEY);
    }

    static String buildQueryUrlWithCoordinates(String... coordinates) throws UnsupportedEncodingException {
        return MessageFormat.format(WEATHER_URL_COORDINATES, URLEncoder.encode(coordinates[0], "UTF-8"),
                URLEncoder.encode(coordinates[1], "UTF-8"), API_KEY);
    }

    static JSONObject fetchDataOnUrl(URL url) {
        HttpURLConnection httpURLConnection = null;
        StringBuilder sb = new StringBuilder();
        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStreamReader inputStreamReader = new InputStreamReader(httpURLConnection.getInputStream());
            int data = inputStreamReader.read();
            while (data != -1) {
                sb.append((char) data);
                data = inputStreamReader.read();
            }
            return new JSONObject(sb.toString());
        } catch (Exception ignored) {
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }

        return null;
    }

    static class DownloadWeatherWithCityName extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... cityNames) {
            try {
                String url = buildQueryUrlWithCityName(cityNames[0]);
                Log.i("CITY NAME URL: ", url);
                return fetchDataOnUrl(new URL(url));
            } catch (Exception e) {
                logError("WEATHER DATA FETCH FAILED w/ CITYNAME", e);
            }

            return null;
        }
    }

    static class DownloadWeatherWithCoordinates extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... coordinates) {
            try {
                String url = buildQueryUrlWithCoordinates(coordinates);
                Log.i("COORDINATES URL: ", url);
                return fetchDataOnUrl(new URL(url));
            } catch (Exception e) {
                logError("WEATHER DATA FETCH FAILED w/ COORDINATES", e);
            }

            return null;
        }
    }
}
