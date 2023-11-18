package com.example.eabsen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.eabsen.activity.KelasActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnProfile,btnRiwayatKelas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        btnProfile = findViewById(R.id.dashboardbtnprofile);
        btnRiwayatKelas = findViewById(R.id.dashboardRiwayatKelas);
        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Profile.class);
                startActivity(intent);
            }
        });

        btnRiwayatKelas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, KelasActivity.class);
                startActivity(intent);
            }
        });
    }
}