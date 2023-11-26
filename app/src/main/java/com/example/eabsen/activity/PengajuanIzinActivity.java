package com.example.eabsen.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.eabsen.API.URI;
import com.example.eabsen.MainActivity;
import com.example.eabsen.R;
import com.example.eabsen.tools.sessionManager;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

import es.dmoral.toasty.Toasty;

public class PengajuanIzinActivity extends AppCompatActivity {
    private CheckBox checkBoxIzin ;
    private  EditText keterangan;
    private MaterialButton btnPIlihfoto,btnKirim;
    private ImageView gambarLampiran;

    private String GambarBase64 = "";
    private static final int REQUEST_IMAGE_PICK = 2;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.pengajuan_izin);

        checkBoxIzin = findViewById(R.id.PengajuancheckboxIzin);
        keterangan= findViewById(R.id.pengajuanIzinKeterangan);
        btnPIlihfoto = findViewById(R.id.pengajuanbtnPilihFOto);
        btnKirim = findViewById(R.id.pengajuanbtnKirim);
        gambarLampiran = findViewById(R.id.pengajuanGambarIjin);


        cek_centang();

        checkBoxIzin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                cek_centang();
            }
        });

        btnPIlihfoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageDialog();
            }
        });

        btnKirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check_kirim();
            }
        });
    }

    private void cek_centang(){
        if (checkBoxIzin.isChecked()){
            keterangan.setVisibility(View.GONE);
            btnPIlihfoto.setVisibility(View.VISIBLE);
            gambarLampiran.setVisibility(View.VISIBLE);
        }else {
            keterangan.setVisibility(View.VISIBLE);
            btnPIlihfoto.setVisibility(View.GONE);
            gambarLampiran.setVisibility(View.GONE);
        }
    }

    private void check_kirim(){




        if (checkBoxIzin.isChecked()){
            if (GambarBase64.equalsIgnoreCase("")){
                Toasty.error(PengajuanIzinActivity.this,"Pilih Gambar Gambar Dulu !",Toasty.LENGTH_SHORT,true).show();
            }else {
                AndroidNetworking.post(URI.url + "pengajuan-izin")
                        .addBodyParameter("token", sessionManager.getString(PengajuanIzinActivity.this, "token", ""))
                        .addBodyParameter("email", sessionManager.getString(PengajuanIzinActivity.this, "email", ""))
                        .addBodyParameter("lampiran", GambarBase64)
                        .addBodyParameter("sakit", String.valueOf(true))
                        .addBodyParameter("keterangan", "")
                        .setPriority(Priority.MEDIUM)
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    // Handle response dari server
                                    boolean success = response.getBoolean("req_status");
                                    String message = response.getString("message");

                                    Log.d("response", "onResponse: " + response);
                                    if (success) {
                                        Toasty.success(PengajuanIzinActivity.this, message, Toast.LENGTH_LONG, true).show();
                                        finish();
                                    } else {
                                        // Login gagal, tampilkan pesan error
                                        Toasty.error(PengajuanIzinActivity.this, message, Toast.LENGTH_SHORT, true).show();
                                    }
                                } catch (JSONException e) {
                                    Log.d("response", "onResponse: " + response);
                                    Toasty.error(PengajuanIzinActivity.this, "Terjadi Kesalahan", Toast.LENGTH_SHORT, true).show();
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(ANError anError) {
                                Toasty.error(PengajuanIzinActivity.this, anError.getErrorCode() + ": Terjadi Kesalahan", Toast.LENGTH_SHORT, true).show();
                            }

                        });
            }
        }else {
            AndroidNetworking.post(URI.url + "pengajuan-izin")
                    .addBodyParameter("token", sessionManager.getString(PengajuanIzinActivity.this,"token",""))
                    .addBodyParameter("email", sessionManager.getString(PengajuanIzinActivity.this,"email",""))
                    .addBodyParameter("lampiran",String.valueOf(false))
                    .addBodyParameter("sakit",String.valueOf(false))
                    .addBodyParameter("keterangan",keterangan.getText().toString())
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                // Handle response dari server
                                boolean success = response.getBoolean("req_status");
                                String message = response.getString("message");

                                Log.d("response", "onResponse: "+response);
                                if (success) {
                                    Toasty.success(PengajuanIzinActivity.this,message,Toast.LENGTH_LONG,true).show();
                                    finish();
                                } else {
                                    // Login gagal, tampilkan pesan error
                                    Toasty.error(PengajuanIzinActivity.this, message, Toast.LENGTH_SHORT, true).show();
                                }
                            } catch (JSONException e) {
                                Log.d("response", "onResponse: "+response);
                                Toasty.error(PengajuanIzinActivity.this, "Terjadi Kesalahan", Toast.LENGTH_SHORT, true).show();
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            Toasty.error(PengajuanIzinActivity.this, anError.getErrorCode() + ": Terjadi Kesalahan", Toast.LENGTH_SHORT, true).show();
                        }

                    });
        }
    }


    private void showImageDialog() {
        // Tampilkan dialog untuk memilih sumber gambar (kamera atau galeri)

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");

        Intent chooser = Intent.createChooser(intent, "Choose Image");
        startActivityForResult(chooser, REQUEST_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                // Mengambil gambar dari kamera
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                processAndDisplayImage(imageBitmap);
            } else if (requestCode == REQUEST_IMAGE_PICK) {
                // Mengambil gambar dari galeri
                try {
                    Bitmap imageBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(data.getData()));
                    processAndDisplayImage(imageBitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void processAndDisplayImage(Bitmap imageBitmap) {
        // Tampilkan gambar di ImageView
        gambarLampiran.setImageBitmap(imageBitmap);

        // Konversi gambar ke base64
        GambarBase64= convertBitmapToBase64(imageBitmap);
        // Lakukan sesuatu dengan base64Image, misalnya kirim ke server, simpan ke database, dll.
    }

    private String convertBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}
