package com.example.ericpinedo.weatherapp;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class WeatherActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener
, GoogleApiClient.ConnectionCallbacks,LocationListener {

    final String URL_BASE = "http://api.openweathermap.org/data/2.5/forecast";
    final String URL_COORD = "?lat="; //?lat=34&lon=-118";
    final String URL_UNITS = "&units=imperial";
    final String URL_API_KEY = "&appid=0d7826669fd26a3914cac854fdc9610c";
    //34.065843, -118.058119

    private GoogleApiClient mGoogleApiClient;
    private final int PERMISSION_LOCATION = 111;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    public void downloadWeatherData(Location location) {

        final String fullCoords = URL_COORD + location.getLatitude() + "&lon="+ location.getLongitude();
        final String url = URL_BASE + fullCoords + URL_UNITS+ URL_API_KEY;

        System.out.println("ERRRORRORO");

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url
                , null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONObject city = response.getJSONObject("city");
                    String cityname = city.getString("name");
                    String country = city.getString("country");

                    Log.v("JSON", "Name: " + cityname + " - "+ "Country: " + country);

                } catch ( JSONException e ){


                }




            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("FUN", "ERR: " + error.getLocalizedMessage());
            }
        });

        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null)
            downloadWeatherData(location);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_LOCATION);
        } else {
            startLocationServices();
        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    public void startLocationServices() {
        try {
            LocationRequest req = LocationRequest.create().setPriority(LocationRequest.PRIORITY_LOW_POWER);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,req,this);
        } catch (SecurityException exception) {

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSION_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startLocationServices();
                } else {
                    //show a dialog saying something like, "I can't run your location dummy - you denied permission!"
                    Toast.makeText(this, "I can't run location, permission was denied", Toast.LENGTH_LONG);
                }
            }
        }
    }

}
