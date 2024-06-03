package com.bkacad.ntg.weatherapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {
    Context context;
    ArrayList<ThoiTiet> arrayList;

    public CustomAdapter(Context context, ArrayList<ThoiTiet> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.dong_listview, null);

        ThoiTiet thoiTiet = arrayList.get(position);

        TextView txtDay = convertView.findViewById(R.id.textViewNgay);
        TextView txtStatus = convertView.findViewById(R.id.textViewTrangThai);
        TextView txtTemp = convertView.findViewById(R.id.textViewNhietDo);
        ImageView imgStatus = convertView.findViewById(R.id.imageViewTrangThai);

        txtDay.setText(thoiTiet.day);
        txtStatus.setText(thoiTiet.status);
        txtTemp.setText(thoiTiet.temp + "°C");

        Picasso.get().load("https://openweathermap.org/img/wn/"+thoiTiet.image+"@4x.png").into(imgStatus);
        return convertView;
    }
}
