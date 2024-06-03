package com.bkacad.ntg.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity2 extends AppCompatActivity {
    ImageView imgBack;
    TextView txtName;
    ListView lv;
    CustomAdapter customAdapter;
    ArrayList<ThoiTiet> mangThoiTiet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        linkById();
        Intent intent = getIntent();
        String city = intent.getStringExtra("name");
        Log.d("ketqua", "du lieu la: " + city);
        GetDaysData(city);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });
    }

    private void linkById() {
        imgBack = findViewById(R.id.imageViewBack);
        txtName = findViewById(R.id.textViewTenThanhPho);
        lv = findViewById(R.id.listView);
        mangThoiTiet = new ArrayList<ThoiTiet>();
        customAdapter = new CustomAdapter(MainActivity2.this, mangThoiTiet);
        lv.setAdapter(customAdapter);
    }

    private void GetDaysData(String data) {
        String url = "https://api.openweathermap.org/data/2.5/forecast?q="+data+"&units=metric&cnt=40&appid=71240e9b586c0f14a2f26cd44c12d58a&lang=vi";
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity2.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // ten thanh pho
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject jsonObjectCity = jsonObject.getJSONObject("city");
                            String name = jsonObjectCity.getString("name");
                            txtName.setText("Thành Phố: " + name);

                            // list
                            JSONArray jsonArrayList = jsonObject.getJSONArray("list");
                            for (int i = 0; i < jsonArrayList.length(); i+=8){
                                JSONObject jsonObjectList = jsonArrayList.getJSONObject(i);

                                String ngay = jsonObjectList.getString("dt");

                                long l = Long.valueOf(ngay);
                                Date date = new Date(l*1000L);
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE dd-MM-yyyy");
                                String Day = simpleDateFormat.format(date);

                                JSONObject jsonObjectMain = jsonObjectList.getJSONObject("main");
                                String temperature = jsonObjectMain.getString("temp");

                                Double a = Double.valueOf(temperature);
                                String Temperature = String.valueOf(a.intValue());

                                JSONArray jsonArrayWeather = jsonObjectList.getJSONArray("weather");
                                JSONObject jsonObjectWeather = jsonArrayWeather.getJSONObject(0);
                                String status = jsonObjectWeather.getString("description");
                                String icon = jsonObjectWeather.getString("icon");

                                mangThoiTiet.add(new ThoiTiet(Day, status, icon, Temperature));
                            }
                            customAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity2.this, "Vị trí không hợp lệ!", Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(stringRequest);
    }
}