package com.example.eabsen;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.text.format.Formatter;
import android.util.Base64;
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

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.eabsen.API.URI;
import com.example.eabsen.activity.KelasActivity;
import com.example.eabsen.activity.KelasPresensiActivity;
import com.example.eabsen.activity.RiwayatPresensiSiswaActivity;
import com.google.android.material.button.MaterialButton;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

import es.dmoral.toasty.Toasty;
import com.example.eabsen.tools.sessionManager;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int CAMERA_REQUEST = 1888;
    private LocationManager locationManager;
    private LocationListener locationListener;
    MaterialButton btnProfile,btnKelas,btnRiwayat,btnAbsensi;
    private double longitude, latitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        MaterialButton btnGetLocation = findViewById(R.id.DashboardPresensi);
        btnProfile = findViewById(R.id.dashboardbtnprofile);
        btnKelas = findViewById(R.id.dashboardRiwayatKelas);
        btnRiwayat = findViewById(R.id.DashboardBtnRiwayat);
        btnAbsensi = findViewById(R.id.DashboardPresensi);

        Log.d("token", sessionManager.getString(MainActivity.this,"token",""));

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

        btnRiwayat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent In = new Intent(MainActivity.this, RiwayatPresensiSiswaActivity.class);
                startActivity(In);
            }
        });

        btnAbsensi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA},
                            MY_CAMERA_PERMISSION_CODE);
                } else {
                    // Izin kamera sudah diberikan
                   getLocation();
                }
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
                longitude = location.getLongitude();
                latitude= location.getLatitude();

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
                longitude = bestLocation.getLongitude();
                latitude=bestLocation.getLatitude();

                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra("android.intent.extras.CAMERA_FACING", 1);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);

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


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            // Menambahkan kata di dalam foto
            String textToAdd = "Hello, OpenAI!";
            Bitmap finalPhoto = addTextToBitmap(photo, textToAdd);


            // Mengonversi foto ke Base64
            String base64Image = bitmapToBase64(finalPhoto);

            // Mengirim foto ke server
            sendImageToServer(base64Image);
        }
    }

    private Bitmap addTextToBitmap(Bitmap bitmap, String text) {
        // Menggabungkan teks ke dalam foto
        Bitmap result = bitmap.copy(bitmap.getConfig(), true);
        // Tambahkan teks sesuai kebutuhan, misalnya di tengah atau di sudut foto
        // ...

        return result;
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    private void sendImageToServer(final String base64Image) {
        Log.d("foto", base64Image);
        AndroidNetworking.post(URI.url+"presensi")
                .addBodyParameter("token",sessionManager.getString(MainActivity.this,"token",""))
                .addBodyParameter("foto", base64Image)
                .addBodyParameter("lat", new Double(latitude).toString())
                .addBodyParameter("long", new Double(longitude).toString())
                .addBodyParameter("ip", getLocalIpAddress())
                .addBodyParameter("email", sessionManager.getString(MainActivity.this,"email",""))

                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("anu", "onResponse: "+response);
                        // Handle response from server if needed
                        // ...
                    }

                    @Override
                    public void onError(ANError error) {
                        Log.d("anu", "error response: "+error.getMessage());

                        System.out.println("error : "+error.getErrorBody());

                        System.out.println("error : "+error.getErrorCode());

                        System.out.println("error : "+error.getErrorDetail());
                    }
                });
    }

    public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
                 en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        String ip = Formatter.formatIpAddress(inetAddress.hashCode());
                        Log.i("TAG", "***** IP="+ ip);
                        return ip;
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("", ex.toString());
        }
        return null;
    }
}