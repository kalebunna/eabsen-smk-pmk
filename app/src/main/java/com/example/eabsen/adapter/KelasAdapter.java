package com.example.eabsen.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.eabsen.R;
import com.example.eabsen.models.Kelas;

import java.util.List;

public class KelasAdapter  extends  BaseAdapter{

    private Context context;
    private List<Kelas> kelasList;

    public KelasAdapter(Context context, List<Kelas> bookList) {
        this.context = context;
        this.kelasList = bookList;
    }

    @Override
    public int getCount() {
        return kelasList.size();
    }

    @Override
    public Object getItem(int position) {
        return kelasList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.kelas_item, parent, false);
        }

        TextView titleTextView = convertView.findViewById(R.id.kelasItemNamaKelas);

        Kelas book = kelasList.get(position);

        titleTextView.setText(book.getNamaKelas()+ " " + book.getJurusan()+" "+book.getKodeKelas());

        return convertView;
    }

    
}
