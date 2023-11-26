package com.example.eabsen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.eabsen.API.URI;
import com.example.eabsen.Login;
import com.example.eabsen.Profile;
import com.example.eabsen.R;
import com.example.eabsen.adapter.KelasAdapter;
import com.example.eabsen.models.Kelas;
import com.example.eabsen.tools.sessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class KelasActivity extends AppCompatActivity {
    private ListView listView;
    private KelasAdapter kelasAdapter;
    private List<Kelas> bookList = new ArrayList<>();

    private List<Kelas> filteredBookList = new ArrayList<>();
    private SearchView cariBuku;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kelas_list);

        listView = findViewById(R.id.kelasListKelas);
        cariBuku = findViewById(R.id.kelaslistCariBuku);

        kelasAdapter = new KelasAdapter(KelasActivity.this, filteredBookList);
        listView.setAdapter(kelasAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent In = new Intent(KelasActivity.this, KelasPresensiActivity.class);
               In.putExtra("idKelas",filteredBookList.get(i).getId());
                startActivity(In);
            }
        });

        setupSearchView();
        fetchDataFromApi();
    }

    private void fetchDataFromApi() {
        String apiUrl = "YOUR_API_URL"; // Ganti dengan URL API yang sesuai

        AndroidNetworking.post(URI.url+"list-kelas")
                .setPriority(Priority.MEDIUM)
                .addBodyParameter("token", sessionManager.getString(KelasActivity.this,"token",""))
                .addBodyParameter("email", sessionManager.getString(KelasActivity.this,"email",""))
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
                                List<Kelas> books = parseJsonResponse(data);
                                // Perbarui data buku dan adapter
                                filteredBookList.clear();
                                bookList.clear();
                                bookList.addAll(books);
                                filteredBookList.addAll(books);
                                kelasAdapter.notifyDataSetChanged();
                            } else {
                                // Login gagal, tampilkan pesan error
                                Toasty.error(KelasActivity.this,message, Toast.LENGTH_SHORT,true).show();
                            }
                        } catch (JSONException e) {
                            Toasty.error(KelasActivity.this,"Terjadi Kesalahan", Toast.LENGTH_SHORT,true).show();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        // Handle error
                        Toasty.error(KelasActivity.this,anError.getErrorCode()+" : Terjadi Kesalahan", Toast.LENGTH_SHORT,true).show();
                    }
                });
    }

    private List<Kelas> parseJsonResponse(JSONArray response) {
        List<Kelas> kelasList = new ArrayList<>();
        try {
            for (int i = 0; i < response.length(); i++) {
                // Parsing data dari JSON dan menambahkannya ke daftar buku
                // Sesuaikan dengan struktur JSON API Anda
                Kelas kelas = new Kelas(response.getJSONObject(i).getString("kelasId"),
                        response.getJSONObject(i).getString("kelas"),
                        response.getJSONObject(i).getString("kodeJurusan"),
                        response.getJSONObject(i).getString("kodeKelas"));
                kelasList.add(kelas);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return kelasList;
    }

    private void setupSearchView() {

        cariBuku.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterBooks(newText);
                return true;
            }
        });
    }

    private void filterBooks(String query) {
        filteredBookList.clear();

        if (TextUtils.isEmpty(query)) {
            filteredBookList.addAll(bookList);
        } else {
            for (Kelas book : bookList) {
                if (book.getKodeKelas().toLowerCase().contains(query.toLowerCase()) ||
                        book.getJurusan().toLowerCase().contains(query.toLowerCase()) ||

                book.getNamaKelas().toLowerCase().contains(query.toLowerCase())) {
                    filteredBookList.add(book);
                }
            }
        }

        kelasAdapter.notifyDataSetChanged();
    }
}

