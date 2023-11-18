package com.example.eabsen;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.eabsen.API.URI;
import com.example.eabsen.tools.sessionManager;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

import es.dmoral.toasty.Toasty;

public class Profile extends AppCompatActivity {
    TextView NIK, Namalengkap, tetala, gender,NISN;
    EditText Email,Username;

    MaterialButton btnLogout;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        NIK = findViewById(R.id.profileNik);
        Namalengkap = findViewById(R.id.profileNama);
        tetala = findViewById(R.id.profileTempatTanggalLahir);
        gender = findViewById(R.id.profileGender);
        NISN = findViewById(R.id.profileNISN);

        Email = findViewById(R.id.profileEmail);
        Username = findViewById(R.id.profileUsename);
        btnLogout = findViewById(R.id.profileLogout);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sessionManager.clearSession(Profile.this);
                Intent i = new Intent(Profile.this, Login.class);
// set the new task and clear flags
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        });


        profileAction();
    }

    private  void profileAction(){
        Log.d("email Tes", "profileAction: "+sessionManager.getString(Profile.this,"email","kosonog"));
        AndroidNetworking.post(URI.url+"profile")
                .addBodyParameter("token", sessionManager.getString(Profile.this,"token",""))
                .addBodyParameter("email", sessionManager.getString(Profile.this,"email",""))
                .setPriority(Priority.MEDIUM)
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
                                JSONObject data = response.getJSONObject("data");
                                Log.d("response", "onResponse: "+data);
                                NIK.setText(data.getString("nik"));
                                Namalengkap.setText(data.getString("namaLengkap"));
                                NISN.setText(data.getString("nisn"));
                                tetala.setText(data.getString("tempatLahir")+", "+data.getString("tglLahir"));
                                gender.setText( data.getString("gender").equalsIgnoreCase("L") ? "Laki-Laki" : "Perempuan" );
                                Username.setText(data.getString("name"));
                                Email.setText(data.getString("email"));
                            } else {
                                // Login gagal, tampilkan pesan error
                                Toasty.error(Profile.this,message, Toast.LENGTH_SHORT,true).show();
                            }
                        } catch (JSONException e) {
                            Toasty.error(Profile.this,"Terjadi Kesalahan", Toast.LENGTH_SHORT,true).show();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Toasty.error(Profile.this,anError.getErrorCode()+": Terjadi Kesalahan", Toast.LENGTH_SHORT,true);
                    }

                });
    }


}
