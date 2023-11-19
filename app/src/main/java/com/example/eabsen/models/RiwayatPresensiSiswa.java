package com.example.eabsen.models;

public class RiwayatPresensiSiswa {

    private String tanggal;

    public RiwayatPresensiSiswa(String tanggal, String jam, String status) {
        this.tanggal = tanggal;
        this.jam = jam;
        this.status = status;
    }

    private String jam;
    private String status;

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getJam() {
        return jam;
    }

    public void setJam(String jam) {
        this.jam = jam;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
