package com.mbhonsle.weatherapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private EditText cityNameText;
    private EditText latText;
    private EditText longText;
    private TextView resultView;
    private MainController mainController;
    private LocationManager locationManager;
    private LocationListener locationListener;

    private void initViews() {
        this.cityNameText = findViewById(R.id.cityNameText);
        this.resultView = findViewById(R.id.resultView);
        this.latText = findViewById(R.id.latText);
        this.longText = findViewById(R.id.longText);
    }

    public void getWeather(View view) {
        this.resultView.setText("");
        String cityName = String.valueOf(this.cityNameText.getText());
        updatedUIWithDetails(cityName, String.valueOf(latText.getText()), String.valueOf(longText.getText()));
        clearTextElements();
    }

    private void updatedUIWithDetails(String cityName, String latitude, String logitude) {
        String cityWeather;
        if (!cityName.isEmpty()) {
            cityWeather = mainController.getDetailedWeatherWithCityName(cityName);
        } else {
            cityWeather = mainController.getDetailedWeatherWithCoordinates(latitude, logitude);
        }
        if (Objects.equals(cityWeather, "")) {
            Toast.makeText(getApplicationContext(), "Couldn't get the weather data, please check the city Name", Toast.LENGTH_SHORT).show();
        } else {
            this.resultView.setText(cityWeather);
        }
    }

    private void clearTextElements() {
        this.latText.getText().clear();
        this.longText.getText().clear();
        this.cityNameText.getText().clear();
    }

    private void initializeLocationElements() {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updatedUIWithDetails("", String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.w("GPS device status changed", status + " ");
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.w("GPS device enabled", "");
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.w("GPS device disabled", "");
            }
        };
    }

    private void checkAndRequestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.mainController = new MainController(getApplicationContext());
        initializeLocationElements();
        checkAndRequestLocationPermission();
        initViews();
    }
}
