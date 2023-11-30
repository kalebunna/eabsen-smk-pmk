package com.example.eabsen;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.format.Formatter;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.eabsen.API.URI;
import com.example.eabsen.activity.KelasActivity;
import com.example.eabsen.activity.KelasPresensiActivity;
import com.example.eabsen.activity.PengajuanIzinActivity;
import com.example.eabsen.activity.RiwayatPresensiSiswaActivity;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.zip.Inflater;

import es.dmoral.toasty.Toasty;
import com.example.eabsen.tools.sessionManager;
import com.google.android.material.card.MaterialCardView;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int CAMERA_REQUEST = 1888;
    private LocationManager locationManager;
    private LocationListener locationListener;
    MaterialButton btnProfile,btnKelas,btnRiwayat,btnAbsensi,btnPengajuanIzin;
    private double longitude, latitude;

    private TextView Nama,tanggal,ucapan,email,NISN,status,ip;

    private MaterialCardView sudahAbsen,BelumAbsen;

    private AlertDialog.Builder dialog;
    private AlertDialog  alert;

    private LayoutInflater inflater;

    private View dialogView;
    private EditText dialog_emal;
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
        btnPengajuanIzin = findViewById(R.id.dashboardbtnpengajuanizin);
        Nama = findViewById(R.id.Dashboard_nama);
        NISN = findViewById(R.id.dashboard_NISN);
        email = findViewById(R.id.dashboard_email);
        tanggal = findViewById(R.id.Dashboard_tanggal);
        ucapan = findViewById(R.id.dashboard_ucapan);
        sudahAbsen = findViewById(R.id.dashboard_sudaPresensi);
        BelumAbsen =findViewById(R.id.dashboard_belumPresensi);
        status=findViewById(R.id.dashbaord_status_absen);
        ip = findViewById(R.id.dashboard_ip);


        ip.setText("IP anda = "+getLocalIpAddress());

        setupDashboard();
        profileAction();
        statusAbsen();
        setbuttonvisible();
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
                    //skrip untuk absen
                 //  getLocation();

                    DialogForm();
                }
            }
        });

        btnPengajuanIzin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent In = new Intent(MainActivity.this, PengajuanIzinActivity.class);
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
                longitude = location.getLongitude();
                latitude= location.getLatitude();

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

    private void DialogForm(){
        dialog =  new AlertDialog.Builder((MainActivity.this));
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.form_insert_email,null);
        dialog.setView(dialogView);
        dialog.setCancelable(true);
        dialog.setIcon(R.drawable.smk3pmk);
        dialog.setTitle("Masukkan Email");

        dialog_emal = (EditText) dialogView.findViewById(R.id.dialog_email);
        TextView error_text  = (TextView) dialogView.findViewById(R.id.dialog_warning);
        dialog.setPositiveButton("Absen Sekarang !", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String enteredEmail = dialog_emal.getText().toString();
                String storedEmail = sessionManager.getString(MainActivity.this, "email", "");

                  if (enteredEmail.equals(storedEmail)){
                      getLocation();
                      Log.d("enterd email true", "onClick: "+enteredEmail);
                      Log.d("stored email true", "onClick: "+storedEmail);

                      dialogInterface.dismiss();
                }else{
                      Log.d("enterd email false", "onClick: "+enteredEmail);
                      Log.d("stored email false", "onClick: "+storedEmail);
                      DialogForm();
                      Toasty.error(MainActivity.this,"Email Tidak Dikenal",Toast.LENGTH_LONG,true).show();
                }
            }
        });

        dialog.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        dialog.show();
    }

    private void setbuttonvisible() {
        if (sessionManager.getString(MainActivity.this,"role","").equalsIgnoreCase("guru")){
            btnAbsensi.setVisibility(View.GONE);
            btnRiwayat.setVisibility(View.GONE);
            btnRiwayat.setVisibility(View.GONE);
            btnPengajuanIzin.setVisibility(View.GONE);
        }else{
            btnKelas.setVisibility(View.GONE);
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
                .addBodyParameter("lng", new Double(longitude).toString())
                .addBodyParameter("lat", new Double(latitude).toString())
                .addBodyParameter("ip", getLocalIpAddress())
                .addBodyParameter("email", sessionManager.getString(MainActivity.this,"email",""))

                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Handle response dari server
                            boolean success = response.getBoolean("req_status");
                            String message = response.getString("message");

                            if (success) {
                                statusAbsen();
                               Toasty.success(MainActivity.this,message,Toasty.LENGTH_SHORT).show();
                            } else {
                                // Login gagal, tampilkan pesan error
                                Toasty.error(MainActivity.this,message, Toast.LENGTH_SHORT,true).show();
                            }
                        } catch (JSONException e) {
                            Toasty.error(MainActivity.this,"Terjadi Kesalahan", Toast.LENGTH_SHORT,true).show();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        Log.d("anu", "error response: "+error.getMessage());

                        System.out.println("error : "+error.getErrorBody());

                        System.out.println("error : "+error.getErrorCode());

                        System.out.println("error : "+error.getErrorDetail());

                        Toasty.error(MainActivity.this,"ERR : "+error.getErrorCode() + " | Message "+error.getErrorDetail());
                    }
                });
    }

    public String getLocalIpAddress() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(MainActivity.this.WIFI_SERVICE);
        if (wifiManager != null && wifiManager.isWifiEnabled()) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();

            // Format the IP address
            String ipAddressString = String.format(
                    "%d.%d.%d.%d",
                    (ipAddress & 0xff),
                    (ipAddress >> 8 & 0xff),
                    (ipAddress >> 16 & 0xff),
                    (ipAddress >> 24 & 0xff)
            );

            // Display the IP address
          return ipAddressString;
        } else {
            return "0.0.0.0";
        }

    }


    private void profileAction() {
        AndroidNetworking.post(URI.url + "profile")
                .addBodyParameter("token", sessionManager.getString(MainActivity.this, "token", ""))
                .addBodyParameter("email", sessionManager.getString(MainActivity.this, "email", ""))
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
                                JSONObject data = response.getJSONObject("data");
                                Nama.setText(data.getString("namaLengkap"));
                                if (sessionManager.getString(MainActivity.this,"role","").equalsIgnoreCase("guru")){
                                    NISN.setText(data.getString("nip"));
                                }else{

                                NISN.setText(data.getString("nisn"));

                                }
                                email.setText(sessionManager.getString(MainActivity.this,"email",""));

                            } else {
                                // Login gagal, tampilkan pesan error
                                Toasty.error(MainActivity.this, message, Toast.LENGTH_SHORT, true).show();
                            }
                        } catch (JSONException e) {
                            Toasty.error(MainActivity.this, "Terjadi Kesalahan", Toast.LENGTH_SHORT, true).show();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Toasty.error(MainActivity.this, anError.getErrorCode() + ": Terjadi Kesalahan", Toast.LENGTH_SHORT, true);
                    }

                });
    }

    private  void setupDashboard (){
        Date currentDate = new Date();

        // Membuat objek SimpleDateFormat dengan format yang diinginkan
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", new Locale("id", "ID"));

        // Memformat tanggal dan waktu sesuai dengan format yang telah ditentukan
        String formattedDate = sdf.format(currentDate);

        // Menampilkan hasil pada TextView atau tempat lain yang sesuai

        tanggal.setText(formattedDate);

        SimpleDateFormat sdf2 = new SimpleDateFormat("HH", Locale.getDefault());
        int currentHour = Integer.parseInt(sdf2.format(currentDate));

        // Menentukan ucapan berdasarkan waktu
        String greeting;
        if (currentHour >= 5 && currentHour < 12) {
            greeting = "Selamat Pagi!";
        } else if (currentHour >= 12 && currentHour < 17) {
            greeting = "Selamat Siang!";
        } else {
            greeting = "Selamat Malam!";
        }

        // Menampilkan ucapan pada TextView
        ucapan.setText(greeting);

    }

    private void statusAbsen(){
        if (sessionManager.getString(MainActivity.this,"role","").equalsIgnoreCase("guru")){

        }else{

        AndroidNetworking.post(URI.url + "status-absen-today")
                .addBodyParameter("token", sessionManager.getString(MainActivity.this, "token", ""))
                .addBodyParameter("email", sessionManager.getString(MainActivity.this, "email", ""))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("login", "onResponse: "+response);
                            // Handle response dari server
                            boolean success = response.getBoolean("req_status");
                            String message = response.getString("message");

                            if (success) {
                                JSONObject data = response.getJSONObject("data");
                                Log.d("dd", "onResponse: "+data);
                                if (data.getString("status").equalsIgnoreCase("")){
                                    BelumAbsen.setVisibility(View.VISIBLE);
                                    sudahAbsen.setVisibility(View.GONE);
                                }else {
                                    sudahAbsen.setVisibility(View.VISIBLE);
                                    status.setText(data.getString("status"));
                                    BelumAbsen.setVisibility(View.GONE);
                                }

                            } else {
                                // Login gagal, tampilkan pesan error
                                Toasty.error(MainActivity.this, message, Toast.LENGTH_SHORT, true).show();
                            }
                        } catch (JSONException e) {
                            Toasty.error(MainActivity.this, "Terjadi Kesalahan", Toast.LENGTH_SHORT, true).show();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("err", "onError: "+anError.getErrorBody());
                        Toasty.error(MainActivity.this, anError.getErrorCode() + ": Terjadi Kesalahan", Toast.LENGTH_SHORT, true).show();
                    }

                });
        }

    }
}