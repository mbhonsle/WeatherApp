package com.mbhonsle.weatherapp;

import android.content.Context;

import com.mbhonsle.weatherapp.util.WallPaperUtil;
import com.mbhonsle.weatherapp.util.WeatherUtil;

/**
 * Created by mak on 12/17/17.
 */

public class MainController {

    private Context context;
    private WallPaperUtil wallPaperUtil;
    private WeatherUtil weatherUtil;

    public MainController(Context context) {
        this.context = context;
        this.wallPaperUtil = new WallPaperUtil(this.context);
    }

    public String getDetailedWeatherWithCityName(String cityName) {
        this.weatherUtil = new WeatherUtil();
        String weatherString = weatherUtil.getCityWeatherWithCityName(cityName);
        this.setWallPaper();
        return weatherString;
    }

    public String getDetailedWeatherWithCoordinates(String... coordinates) {
        this.weatherUtil = new WeatherUtil();
        String weatherString = weatherUtil.getCityWeatherWithCoordinate(coordinates);
        this.setWallPaper();
        return weatherString;
    }

    private void setWallPaper() {
        this.wallPaperUtil.setWallpaper(weatherUtil.getSummary());
    }
}
