package com.example.eabsen.models;

public class KelasPresensi {
    private String NamaSiswa,Status,JamMasuk,keteranga;

    public KelasPresensi(String namaSiswa, String status, String jamMasuk, String keteranga) {
        NamaSiswa = namaSiswa;
        Status = status;
        JamMasuk = jamMasuk;
        this.keteranga = keteranga;
    }

    public String getNamaSiswa() {
        return NamaSiswa;
    }

    public void setNamaSiswa(String namaSiswa) {
        NamaSiswa = namaSiswa;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getJamMasuk() {
        return JamMasuk;
    }

    public void setJamMasuk(String jamMasuk) {
        JamMasuk = jamMasuk;
    }

    public String getKeteranga() {
        return keteranga;
    }

    public void setKeteranga(String keteranga) {
        this.keteranga = keteranga;
    }
}
