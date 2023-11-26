package com.example.eabsen.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.eabsen.API.URI;
import com.example.eabsen.R;
import com.example.eabsen.adapter.KelasPresensiAdapter;
import com.example.eabsen.models.KelasPresensi;
import com.example.eabsen.tools.sessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class KelasPresensiActivity extends AppCompatActivity {

    private ListView listView;
    private KelasPresensiAdapter kelasPresensiAdapter;
    private List<KelasPresensi> listKelasPresensi = new ArrayList<>();
    private SearchView cariRiwayat;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kelas_presensi_list);

        setContentView(R.layout.kelas_presensi_list);
        listView = findViewById(R.id.kelasPresensiListSiswa);
        cariRiwayat = findViewById(R.id.kelasPresensiCari);

        kelasPresensiAdapter = new KelasPresensiAdapter(KelasPresensiActivity.this, listKelasPresensi);
        listView.setAdapter(kelasPresensiAdapter);

        fetchDataFromApi();
    }

    private void fetchDataFromApi() {
        String id = getIntent().getStringExtra("idKelas");
        Log.d("data Id", "fetchDataFromApi: "+id);
        String token = sessionManager.getString(KelasPresensiActivity.this,"token","");
        String email = sessionManager.getString(KelasPresensiActivity.this,"email","");

        AndroidNetworking.post(URI.url+"absen-today-byKelas")
                .setPriority(Priority.MEDIUM)
                .addBodyParameter("token", token)
                .addBodyParameter("email", email)
                .addBodyParameter("idkelas", id)

                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Handle response dari server
                            boolean success = response.getBoolean("req_status");
                            String message = response.getString("message");
                            Log.d("response : ", "onResponse: "+response);
                            if (success) {
                                Log.d("response", "onResponse: "+response);
                                JSONArray data = response.getJSONArray("data");
                                List<KelasPresensi> dataPresensi = parseJsonResponse(data);
                                // Perbarui data buku dan adapter

                                listKelasPresensi.clear();
                                listKelasPresensi.addAll(dataPresensi);

                                kelasPresensiAdapter.notifyDataSetChanged();
                            } else {
                                // Login gagal, tampilkan pesan error
                                Toasty.error(KelasPresensiActivity.this,message, Toast.LENGTH_SHORT,true).show();
                            }
                        } catch (JSONException e) {
                            Toasty.error(KelasPresensiActivity.this,"Terjadi Kesalahan", Toast.LENGTH_SHORT,true).show();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        // Handle error
                        Log.d("err", "onError: "+anError.getErrorDetail());

                        Log.d("err", "onError: "+anError.getErrorBody());
                        Toasty.error(KelasPresensiActivity.this,anError.getErrorCode()+" : Terjadi Kesalahan", Toast.LENGTH_SHORT,true).show();
                    }
                });
    }

    private List<KelasPresensi> parseJsonResponse(JSONArray response) {
        List<KelasPresensi> kelasPresensiList = new ArrayList<>();
        try {
            for (int i = 0; i < response.length(); i++) {
                // Parsing data dari JSON dan menambahkannya ke daftar buku
                // Sesuaikan dengan struktur JSON API Anda
                KelasPresensi kelas = new KelasPresensi(
                        response.getJSONObject(i).getString("namaSiswa"),
                        response.getJSONObject(i).getString("status"),
                        response.getJSONObject(i).getString("jamMasuk"),
                        response.getJSONObject(i).getString("keterangan"));
                kelasPresensiList.add(kelas);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return kelasPresensiList;
    }
}
