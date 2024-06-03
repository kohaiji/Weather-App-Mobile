package com.bkacad.ntg.weatherapp;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import static com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;


import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Granularity;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    EditText edtSearch;
    Button btnSearch, btnNextDay, btnGetLocation;
    TextView txtName, txtCountry, txtTemp, txtStatus, txtHumidity, txtCloud, txtWind, txtDescription, txtDay, tv;
    ImageView imgIcon;
    FusedLocationProviderClient fusedLocationProviderClient;
    private final static int REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        linkById();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);

        btnGetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    askPermission();
                }else {
                    getLastLocation();
                }
            }
        });


        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = edtSearch.getText().toString();
                if (city.isEmpty()){
                    edtSearch.setError("Tên thành phố không được để trống!");
                    return;
                }
                GetCurrentWeatherData(city);
            }
        });

        // Nut chuyen man hinh
        btnNextDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = txtName.getText().toString();
                if(city.equals("")){
                    showAlertDialog("Hãy chọn địa điểm mà bạn muốn xem thời tiết!");
                }
                else {
                    Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                    intent.putExtra("name", city);
                    startActivity(intent);
                }
            }
        });

    }

    // hien thong bao
    private void showAlertDialog(String msg) {
        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle("Thông báo")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create();
        dialog.show();
    }

    private void linkById() {
        edtSearch = findViewById(R.id.edtPlaceHolder);
        btnSearch = findViewById(R.id.btnSearch);
        btnNextDay = findViewById(R.id.btnNextDay);
        btnGetLocation = findViewById(R.id.btnGetLocation);
        txtName = findViewById(R.id.textviewName);
        txtCountry = findViewById(R.id.textviewCountry);
        txtTemp = findViewById(R.id.textviewTemp);
        txtStatus = findViewById(R.id.textviewStatus);
        txtHumidity = findViewById(R.id.textviewHumidity);
        txtCloud = findViewById(R.id.textviewCloud);
        txtWind = findViewById(R.id.textviewWind);
        txtDescription = findViewById(R.id.textviewDescription);
        txtDay = findViewById(R.id.textviewDay);
        imgIcon = findViewById(R.id.imgIcon);
        tv = findViewById(R.id.tv);
    }

    private void getLastLocation() {
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            //update location
            LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 100)
                    .setWaitForAccurateLocation(true)
                    .setMinUpdateIntervalMillis(2000)
                    .setMaxUpdateDelayMillis(100)
                    .build();
            LocationCallback locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(@NonNull LocationResult locationResult) {

                    if (locationResult == null) {
                        return;
                    }
                }
            };
            LocationServices.getFusedLocationProviderClient(getApplicationContext())
                    .requestLocationUpdates(locationRequest, locationCallback, null);

            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null){
//                                Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
//                                try {
//                                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
//                                    String latitude = String.valueOf(addresses.get(0).getLatitude());
//                                    String longitude = String.valueOf(addresses.get(0).getLongitude());
//                                    GetWeatherCurrentLocation(latitude, longitude);
//                                    tv.setText(
//                                            "Latitude: " + latitude +
//                                                    "\nLongtitude: " + longitude);
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }

                                String latitude = Double.toString(location.getLatitude());
                                String longitude = Double.toString(location.getLongitude());
                                GetWeatherCurrentLocation(latitude, longitude);

//                                tv.setText(
//                                            "Latitude: " + latitude +
//                                                    "\nLongtitude: " + longitude);
                            }
                        }
                    });
        }
        else {
            askPermission();

        }

    }

    private void askPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLastLocation();
            }
            else {
                showAlertDialog("Yêu cầu cấp quyền truy cập vị trí!" +
                        "\nSettings -> Location -> Weather App");
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void GetWeatherCurrentLocation(String lat, String lon){
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        String url = "https://api.openweathermap.org/data/2.5/weather?lat="+lat+"&lon="+lon+"&appid=71240e9b586c0f14a2f26cd44c12d58a&units=metric&lang=vi";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d("response", response);
                            // thoi gian, vi tri
                            JSONObject jsonObject = new JSONObject(response);
                            String day =  jsonObject.getString("dt");
                            String name = jsonObject.getString("name");
                            txtName.setText(name);

                            long l = Long.valueOf(day);
                            Date date = new Date(l*1000L);
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE dd-MM-yyyy HH:mm:ss a");
                            String Day = simpleDateFormat.format(date);
                            txtDay.setText(Day);

                            // mo ta, trang thai, icon
                            JSONArray jsonArrayWeather = jsonObject.getJSONArray("weather");
                            JSONObject jsonObjectWeather = jsonArrayWeather.getJSONObject(0);
                            String status = jsonObjectWeather.getString("main");
                            String icon = jsonObjectWeather.getString("icon");
                            String description = jsonObjectWeather.getString("description");

                            Picasso.get().load("https://openweathermap.org/img/wn/"+icon+"@4x.png").into(imgIcon);
                            txtStatus.setText(status);
                            txtDescription.setText(description);

                            // nhiet do, do am
                            JSONObject jsonObjectMain = jsonObject.getJSONObject("main");
                            String temperature = jsonObjectMain.getString("temp");
                            String humidity = jsonObjectMain.getString("humidity");

                            Double a = Double.valueOf(temperature);
                            String Temperature = String.valueOf(a.intValue());

                            txtTemp.setText(Temperature+"°C");
                            txtHumidity.setText(humidity+"%");

                            // gio
                            JSONObject jsonObjectWind = jsonObject.getJSONObject("wind");
                            String wind = jsonObjectWind.getString("speed");
                            txtWind.setText(wind+"m/s");

                            // may
                            JSONObject jsonObjectClouds = jsonObject.getJSONObject("clouds");
                            String cloud = jsonObjectClouds.getString("all");
                            txtCloud.setText(cloud+"%");

                            // dat nuoc
                            JSONObject jsonObjectSys = jsonObject.getJSONObject("sys");
                            String country = jsonObjectSys.getString("country");
                            txtCountry.setText(country);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Vị trí không hợp lệ!", Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(stringRequest);
    }
    public void GetCurrentWeatherData(String data){
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        String url = "https://api.openweathermap.org/data/2.5/weather?q="+data+"&appid=71240e9b586c0f14a2f26cd44c12d58a&units=metric&lang=vi";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d("response", response);
                            // thoi gian, vi tri
                            JSONObject jsonObject = new JSONObject(response);
                            String day =  jsonObject.getString("dt");
                            String name = jsonObject.getString("name");
                            txtName.setText(name);

                            long l = Long.valueOf(day);
                            Date date = new Date(l*1000L);
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE dd-MM-yyyy HH:mm:ss a");
                            String Day = simpleDateFormat.format(date);
                            txtDay.setText(Day);

                            // mo ta, trang thai, icon
                            JSONArray jsonArrayWeather = jsonObject.getJSONArray("weather");
                            JSONObject jsonObjectWeather = jsonArrayWeather.getJSONObject(0);
                            String status = jsonObjectWeather.getString("main");
                            String icon = jsonObjectWeather.getString("icon");
                            String description = jsonObjectWeather.getString("description");

                            Picasso.get().load("https://openweathermap.org/img/wn/"+icon+"@4x.png").into(imgIcon);
                            txtStatus.setText(status);
                            txtDescription.setText(description);

                            // nhiet do, do am
                            JSONObject jsonObjectMain = jsonObject.getJSONObject("main");
                            String temperature = jsonObjectMain.getString("temp");
                            String humidity = jsonObjectMain.getString("humidity");

                            Double a = Double.valueOf(temperature);
                            String Temperature = String.valueOf(a.intValue());

                            txtTemp.setText(Temperature+"°C");
                            txtHumidity.setText(humidity+"%");

                            // gio
                            JSONObject jsonObjectWind = jsonObject.getJSONObject("wind");
                            String wind = jsonObjectWind.getString("speed");
                            txtWind.setText(wind+"m/s");

                            // may
                            JSONObject jsonObjectClouds = jsonObject.getJSONObject("clouds");
                            String cloud = jsonObjectClouds.getString("all");
                            txtCloud.setText(cloud+"%");

                            // dat nuoc
                            JSONObject jsonObjectSys = jsonObject.getJSONObject("sys");
                            String country = jsonObjectSys.getString("country");
                            txtCountry.setText(country);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Vị trí không hợp lệ!", Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(stringRequest);
    }




}