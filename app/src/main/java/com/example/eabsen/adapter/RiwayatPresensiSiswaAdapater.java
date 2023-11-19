package com.example.eabsen.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.eabsen.R;
import com.example.eabsen.activity.RiwayatPresensiSiswaActivity;
import com.example.eabsen.models.RiwayatPresensiSiswa;

import java.util.List;

public class RiwayatPresensiSiswaAdapater extends BaseAdapter {
    private Context context;
    private List<RiwayatPresensiSiswa> riwayatList;

    public RiwayatPresensiSiswaAdapater(Context context, List<RiwayatPresensiSiswa> bookList) {
        this.context = context;
        this.riwayatList = bookList;
    }

    @Override
    public int getCount() {
        return riwayatList.size();
    }

    @Override
    public Object getItem(int position) {
        return riwayatList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.riwayat_presensi_siswa_item, parent, false);
        }

        TextView Tanggal = convertView.findViewById(R.id.RiwayatPresensiItemTanggal);
        TextView  Jam = convertView.findViewById(R.id.RiwayatPresensiItemJam);
        TextView Status= convertView.findViewById(R.id.RiwayatPresensiItemStatus);

       RiwayatPresensiSiswa riwayat = riwayatList.get(position);

        Tanggal.setText(riwayat.getTanggal() );
        Jam.setText(riwayat.getJam());

        if (riwayat.getStatus().equalsIgnoreCase("hadir")){
            Status.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(parent.getContext(), R.color.success)));
            Status.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.white));
            Status.setText(riwayat.getStatus());
        }else {
            Status.setText(riwayat.getStatus());
        }

        return convertView;
    }
}
