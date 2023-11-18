package com.example.eabsen.models;

public class Kelas {
    private String id,Jurusan,namaKelas,kodeKelas;

    public String getKodeKelas() {
        return kodeKelas;
    }

    public void setKodeKelas(String kodeKelas) {
        this.kodeKelas = kodeKelas;
    }

    public Kelas(String id,String namaKelas, String jurusan, String KodeKelas) {
        this.id = id;
        Jurusan = jurusan;
        this.kodeKelas = KodeKelas;
        this.namaKelas = namaKelas;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJurusan() {
        return Jurusan;
    }

    public void setJurusan(String jurusan) {
        Jurusan = jurusan;
    }

    public String getNamaKelas() {
        return namaKelas;
    }

    public void setNamaKelas(String namaKelas) {
        this.namaKelas = namaKelas;
    }
}
