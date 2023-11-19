package com.example.eabsen;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;

import com.example.eabsen.activity.KelasActivity;
import com.example.eabsen.activity.KelasPresensiActivity;
import com.google.android.material.button.MaterialButton;

import java.util.List;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private LocationManager locationManager;
    private LocationListener locationListener;
    MaterialButton btnProfile,btnKelas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        MaterialButton btnGetLocation = findViewById(R.id.DashboardPresensi);
        btnProfile = findViewById(R.id.dashboardbtnprofile);
        btnKelas = findViewById(R.id.dashboardRiwayatKelas);

        btnGetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
            }
        });

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent In = new Intent(MainActivity.this, Profile.class);
                startActivity(In);
            }
        });

        btnKelas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent In = new Intent(MainActivity.this, KelasActivity.class);
                startActivity(In);
            }
        });







        // Memeriksa apakah layanan lokasi sudah diaktifkan
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // Jika tidak diaktifkan, buka pengaturan lokasi untuk mengaktifkannya
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                // Dapatkan nilai longitude dan latitude dari objek Location
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();

                // Gunakan nilai longitude dan latitude sesuai kebutuhan Anda
                // Misalnya, tampilkan di log atau UI
                System.out.println("Longitude: " + longitude + " Latitude: " + latitude);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };

        // Periksa izin lokasi
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // Izin sudah diberikan, dapatkan lokasi
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    0, 0, locationListener);
        } else {
            // Jika izin belum diberikan, minta izin
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
    }

    private void getLocation() {
        // Dapatkan lokasi terkini jika izin sudah diberikan
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            try {
                List<String> providers = locationManager.getProviders(true);
                Location bestLocation = null;
                for (String provider : providers) {
                    Location l = locationManager.getLastKnownLocation(provider);
                    if (l == null) {
                        continue;
                    }
                    if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                        // Found best last known location: %s", l);
                        bestLocation = l;
                    }
                }
                Log.d("d", "Last Known Location - Longitude: attitude"+bestLocation.getLongitude()+" "+bestLocation.getLatitude());
            } catch (Exception e) {
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    // Jika tidak diaktifkan, buka pengaturan lokasi untuk mengaktifkannya
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }else {
                    Toasty.error(this,"PLEASE GRANT YOUR LOCATION",Toasty.LENGTH_SHORT,true);
                }
            }

        } else {
            // Jika izin belum diberikan, minta izin
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            // Periksa hasil izin lokasi
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Izin diberikan, dapatkan lokasi
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        0, 0, locationListener);
            }
        }
    }
}