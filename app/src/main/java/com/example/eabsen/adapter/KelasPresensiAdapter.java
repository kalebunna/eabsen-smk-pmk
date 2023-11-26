package com.example.eabsen.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.eabsen.R;
import com.example.eabsen.models.Kelas;
import com.example.eabsen.models.KelasPresensi;
import com.example.eabsen.models.RiwayatPresensiSiswa;

import java.util.List;

public class KelasPresensiAdapter extends BaseAdapter {
    private Context context;
    private List<KelasPresensi> listKelasPresensi;

    public KelasPresensiAdapter(Context context, List<KelasPresensi> bookList) {
        this.context = context;
        this.listKelasPresensi = bookList;
    }

    @Override
    public int getCount() {
        return listKelasPresensi.size();
    }

    @Override
    public Object getItem(int position) {
        return listKelasPresensi.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.kelas_presensi_item, parent, false);
        }

        TextView Jam = convertView.findViewById(R.id.kelasPresensiItemJam);
        TextView  Nama = convertView.findViewById(R.id.kelasPresensiItemNama);
        TextView Status= convertView.findViewById(R.id.kelasPresensiItemStatus);
        TextView Keterangan =convertView.findViewById(R.id.kelasPresensiItemKeterangan);

        //RiwayatPresensiSiswa riwayat = riwayatList.get(position);

        KelasPresensi listKelas = listKelasPresensi.get(position);

        Jam.setText(listKelas.getJamMasuk());
        Nama.setText(listKelas.getNamaSiswa());
        Status.setText(listKelas.getStatus());
        Keterangan.setText(listKelas.getKeteranga());



        return convertView;
    }
}
