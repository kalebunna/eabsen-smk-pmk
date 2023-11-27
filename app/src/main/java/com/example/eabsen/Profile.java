package com.example.eabsen;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
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
    TextView NIK, Namalengkap, tetala, gender, NISN;
    EditText Email, Username;

    MaterialButton btnLogout, btnUpdate,resetPassword;

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
        btnUpdate = findViewById(R.id.profilebtnupdate);
        resetPassword =findViewById(R.id.profile_resetPassowrd);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sessionManager.clearSession(Profile.this);
                Intent i = new Intent(Profile.this, Login.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String edit_email = Email.getText().toString();
                String username = Username.getText().toString();
                updateProfileAction(edit_email, username);

            }
        });

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Uri uri = Uri.parse(URI.link+"forgot-password"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });


        profileAction();
    }

    private void updateProfileAction(String email, String username) {
        AndroidNetworking.post(URI.url + "update-profile")
                .addBodyParameter("token", sessionManager.getString(Profile.this, "token", ""))
                .addBodyParameter("email", sessionManager.getString(Profile.this, "email", ""))
                .addBodyParameter("req_email", email)
                .addBodyParameter("name", username)

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
                                Log.d("response", "onResponse: " + response);
                                JSONObject data = response.getJSONObject("data");
                                sessionManager.saveString(Profile.this, "email", data.getString("email"));
                                Toasty.success(Profile.this, "UPDATE DATA BERHHASIL", Toasty.LENGTH_SHORT, true).show();

                            } else {
                                // Login gagal, tampilkan pesan error
                                Toasty.error(Profile.this, message, Toast.LENGTH_SHORT, true).show();
                            }
                        } catch (JSONException e) {
                            Toasty.error(Profile.this, "Terjadi Kesalahan", Toast.LENGTH_SHORT, true).show();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Toasty.error(Profile.this, anError.getErrorCode() + ": Terjadi Kesalahan", Toast.LENGTH_SHORT, true);
                    }

                });
    }

    private void profileAction() {
        AndroidNetworking.post(URI.url + "profile")
                .addBodyParameter("token", sessionManager.getString(Profile.this, "token", ""))
                .addBodyParameter("email", sessionManager.getString(Profile.this, "email", ""))
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
                                Log.d("response", "onResponse: " + response);
                                JSONObject data = response.getJSONObject("data");
                                Log.d("response", "onResponse: " + data);
                                if (sessionManager.getString(Profile.this, "role", "").equalsIgnoreCase("guru")) {
                                    NIK.setText(data.getString("nip"));
                                    NISN.setText(data.getString("nuptk"));
                                    LinearLayout Lgn = findViewById(R.id.profileLinearGender);
                                    Lgn.setVisibility(View.GONE);
                                } else {
                                    NIK.setText(data.getString("nik"));
                                    NISN.setText(data.getString("nisn"));
                                    gender.setText(data.getString("gender").equalsIgnoreCase("L") ? "Laki-Laki" : "Perempuan");
                                }
                                Namalengkap.setText(data.getString("namaLengkap"));
                                tetala.setText(data.getString("tempatLahir") + ", " + data.getString("tglLahir"));
                                Username.setText(data.getString("name"));
                                Email.setText(data.getString("email"));
                            } else {
                                // Login gagal, tampilkan pesan error
                                Toasty.error(Profile.this, message, Toast.LENGTH_SHORT, true).show();
                            }
                        } catch (JSONException e) {
                            Toasty.error(Profile.this, "Terjadi Kesalahan", Toast.LENGTH_SHORT, true).show();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Toasty.error(Profile.this, anError.getErrorCode() + ": Terjadi Kesalahan", Toast.LENGTH_SHORT, true);
                    }

                });
    }


}
