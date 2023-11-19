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
import com.example.eabsen.adapter.RiwayatPresensiSiswaAdapater;
import com.example.eabsen.models.RiwayatPresensiSiswa;
import com.example.eabsen.tools.sessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class RiwayatPresensiSiswaActivity extends AppCompatActivity {
    private ListView listView;
    private RiwayatPresensiSiswaAdapater riwayatPresensiSiswaAdapater;
    private List<RiwayatPresensiSiswa> riwayatPresensiSiswas = new ArrayList<>();

    private SearchView cariRiwayat;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.kelas_presensi_list);
        listView = findViewById(R.id.kelasPresensiListSiswa);
        cariRiwayat = findViewById(R.id.kelasPresensiCari);

        riwayatPresensiSiswaAdapater = new RiwayatPresensiSiswaAdapater(RiwayatPresensiSiswaActivity.this, riwayatPresensiSiswas);
        listView.setAdapter(riwayatPresensiSiswaAdapater);

        fetchDataFromApi();

    }


    private void fetchDataFromApi() {
        String apiUrl = "YOUR_API_URL"; // Ganti dengan URL API yang sesuai

        AndroidNetworking.post(URI.url+"history-absen-thisMonth")
                .setPriority(Priority.MEDIUM)
                .addBodyParameter("token", sessionManager.getString(RiwayatPresensiSiswaActivity.this,"token",""))
                .addBodyParameter("email", sessionManager.getString(RiwayatPresensiSiswaActivity.this,"email",""))
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Handle response dari server
                            boolean success = response.getBoolean("req_status");
                            String message = response.getString("message");

                            if (success) {
                                Log.d("response", "onResponse: "+response);
                                JSONArray data = response.getJSONArray("data");
                                List<RiwayatPresensiSiswa> riwayat = parseJsonResponse(data);
                                // Perbarui data buku dan adapter

                                riwayatPresensiSiswas.clear();
                                riwayatPresensiSiswas.addAll(riwayat);

                                riwayatPresensiSiswaAdapater.notifyDataSetChanged();
                            } else {
                                // Login gagal, tampilkan pesan error
                                Toasty.error(RiwayatPresensiSiswaActivity.this,message, Toast.LENGTH_SHORT,true).show();
                            }
                        } catch (JSONException e) {
                            Toasty.error(RiwayatPresensiSiswaActivity.this,"Terjadi Kesalahan", Toast.LENGTH_SHORT,true).show();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        // Handle error
                        Toasty.error(RiwayatPresensiSiswaActivity.this,anError.getErrorCode()+" : Terjadi Kesalahan", Toast.LENGTH_SHORT,true).show();
                    }
                });
    }

    private List<RiwayatPresensiSiswa> parseJsonResponse(JSONArray response) {
        List<RiwayatPresensiSiswa> kelasList = new ArrayList<>();
        try {
            for (int i = 0; i < response.length(); i++) {
                // Parsing data dari JSON dan menambahkannya ke daftar buku
                // Sesuaikan dengan struktur JSON API Anda
                RiwayatPresensiSiswa kelas = new RiwayatPresensiSiswa(
                        response.getJSONObject(i).getString("tanggal"),
                        response.getJSONObject(i).getString("jam"),
                        response.getJSONObject(i).getString("status"));
                kelasList.add(kelas);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return kelasList;
    }
}
