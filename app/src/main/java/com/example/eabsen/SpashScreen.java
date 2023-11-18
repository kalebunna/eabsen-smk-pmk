package com.example.eabsen;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;
import android.window.SplashScreen;

import com.example.eabsen.tools.sessionManager;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SpashScreen extends AppCompatActivity {

    private static final int SPLASH_TIMEOUT = 2000;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);

        final ProgressBar progressBar = findViewById(R.id.progressBar);

        // Handler untuk menunda pindah ke activity berikutnya
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Intent untuk pindah ke activity berikutnya
                Boolean check = sessionManager.getBoolean(SpashScreen.this,"login",false);

                Log.d("boolean", "run: "+check);
                if (check){
                    Intent intent = new Intent(SpashScreen.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    sessionManager.clearSession(SpashScreen.this);
                    Intent intent = new Intent(SpashScreen.this, Login.class);
                    startActivity(intent);
                    finish();
                }


            }
        }, SPLASH_TIMEOUT);

        // Handler untuk menyembunyikan ProgressBar setelah waktu tertentu
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(android.view.View.INVISIBLE);
            }
        }, SPLASH_TIMEOUT - 500);
    }
}
