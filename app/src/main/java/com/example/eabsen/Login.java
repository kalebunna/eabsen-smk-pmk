package com.example.eabsen;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class Login extends AppCompatActivity {
        private EditText username,password;
        private MaterialButton btnLogin;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidNetworking.initialize(getApplicationContext());
        setContentView(R.layout.login);
        username = findViewById(R.id.loginUsename);
        password = findViewById(R.id.loginPassword);
        btnLogin = findViewById(R.id.loginBtnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginAction();
            }
        });

    }

    private void loginAction () {
        String username = this.username.getText().toString().trim();
        String password = this.password.getText().toString().trim();

        // Pastikan username dan password tidak kosong
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Username dan password harus diisi", Toast.LENGTH_SHORT).show();
            return;
        } else {

            // Lakukan request login menggunakan Fast Android Networking
            AndroidNetworking.post(URI.url + "login")
                    .addBodyParameter("name", username)
                    .addBodyParameter("password", password)
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
                                    Log.d("data", "onResponse: " + response.toString());
                                    sessionManager.saveString(Login.this, "email", response.getString("email"));
                                    sessionManager.saveString(Login.this, "token", response.getString("token"));
                                    sessionManager.saveString(Login.this, "role", response.getString("role"));
                                    sessionManager.saveBoolean(Login.this, "login", true);
                                    Intent intent = new Intent(Login.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // Login gagal, tampilkan pesan error
                                    Toasty.error(Login.this, message, Toast.LENGTH_SHORT, true).show();
                                }
                            } catch (JSONException e) {
                                Toasty.error(Login.this, "Terjadi Kesalahan", Toast.LENGTH_SHORT, true).show();
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            Toasty.error(Login.this, anError.getErrorCode() + ": Terjadi Kesalahan", Toast.LENGTH_SHORT, true);
                        }

                    });
        }
    }
}
