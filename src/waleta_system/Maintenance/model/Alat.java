/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package waleta_system.Maintenance.model;

import java.util.Date;

/**
 *
 * @author User
 */
public class Alat {    
    private String idAlat;
    private String kodeAlat;
    private KategoriAlat kategoriAlat;
    private Ruangan ruangan;
    private String merk;
    private String spesifikasi;
    private String fotoAlat;
    private int statusAlat;
    private int posisiX;
    private int posisiY;
    private float rotasi;
    private int skala;
    private Date tglLapor;
    private String keluhan;
    private int statusTemuan;

    public Alat(KategoriAlat kategoriAlat, Ruangan ruangan, String merk, String spesifikasi, String fotoAlat, int statusAlat, int posisiX, int posisiY, float rotasi, int skala) {
        this.kategoriAlat = kategoriAlat;
        this.ruangan = ruangan;
        this.merk = merk;
        this.spesifikasi = spesifikasi;
        this.fotoAlat = fotoAlat;
        this.statusAlat = statusAlat;
        this.posisiX = posisiX;
        this.posisiY = posisiY;
        this.rotasi = rotasi;
        this.skala = skala;
    }

    public Alat(String idAlat, KategoriAlat kategoriAlat, Ruangan ruangan, String merk, String spesifikasi, String fotoAlat, int statusAlat, int posisiX, int posisiY, float rotasi, int skala) {
        this.idAlat = idAlat;
        this.kategoriAlat = kategoriAlat;
        this.ruangan = ruangan;
        this.merk = merk;
        this.spesifikasi = spesifikasi;
        this.fotoAlat = fotoAlat;
        this.statusAlat = statusAlat;
        this.posisiX = posisiX;
        this.posisiY = posisiY;
        this.rotasi = rotasi;
        this.skala = skala;
    }

    public Alat(String idAlat, KategoriAlat kategoriAlat, Ruangan ruangan, String merk, String spesifikasi, String fotoAlat, int statusAlat, int posisiX, int posisiY, float rotasi, int skala, Date tglLapor, String keluhan, int statusTemuan) {
        this.idAlat = idAlat;
        this.kategoriAlat = kategoriAlat;
        this.ruangan = ruangan;
        this.merk = merk;
        this.spesifikasi = spesifikasi;
        this.fotoAlat = fotoAlat;
        this.statusAlat = statusAlat;
        this.posisiX = posisiX;
        this.posisiY = posisiY;
        this.rotasi = rotasi;
        this.skala = skala;
        this.tglLapor = tglLapor;
        this.keluhan = keluhan;
        this.statusTemuan = statusTemuan;
    }

    public Alat(String idAlat, String kodeAlat, KategoriAlat kategoriAlat, Ruangan ruangan, String merk, String spesifikasi, String fotoAlat, int statusAlat, int posisiX, int posisiY, float rotasi, int skala) {
        this.idAlat = idAlat;
        this.kodeAlat = kodeAlat;
        this.kategoriAlat = kategoriAlat;
        this.ruangan = ruangan;
        this.merk = merk;
        this.spesifikasi = spesifikasi;
        this.fotoAlat = fotoAlat;
        this.statusAlat = statusAlat;
        this.posisiX = posisiX;
        this.posisiY = posisiY;
        this.rotasi = rotasi;
        this.skala = skala;
    }

    public String getIdAlat() {
        return idAlat;
    }

    public void setIdAlat(String idAlat) {
        this.idAlat = idAlat;
    }

    public String getKodeAlat() {
        return kodeAlat;
    }

    public void setKodeAlat(String kodeAlat) {
        this.kodeAlat = kodeAlat;
    }

    public KategoriAlat getKategoriAlat() {
        return kategoriAlat;
    }

    public void setKategoriAlat(KategoriAlat kategoriAlat) {
        this.kategoriAlat = kategoriAlat;
    }

    public Ruangan getRuangan() {
        return ruangan;
    }

    public void setRuangan(Ruangan ruangan) {
        this.ruangan = ruangan;
    }

    public String getMerk() {
        return merk;
    }

    public void setMerk(String merk) {
        this.merk = merk;
    }

    public String getSpesifikasi() {
        return spesifikasi;
    }

    public void setSpesifikasi(String spesifikasi) {
        this.spesifikasi = spesifikasi;
    }

    public String getFotoAlat() {
        return fotoAlat;
    }

    public void setFotoAlat(String fotoAlat) {
        this.fotoAlat = fotoAlat;
    }

    public int getStatusAlat() {
        return statusAlat;
    }

    public void setStatusAlat(int statusAlat) {
        this.statusAlat = statusAlat;
    }

    public int getPosisiX() {
        return posisiX;
    }

    public void setPosisiX(int posisiX) {
        this.posisiX = posisiX;
    }

    public int getPosisiY() {
        return posisiY;
    }

    public void setPosisiY(int posisiY) {
        this.posisiY = posisiY;
    }

    public float getRotasi() {
        return rotasi;
    }

    public void setRotasi(float rotasi) {
        this.rotasi = rotasi;
    }

    public int getSkala() {
        return skala;
    }

    public void setSkala(int skala) {
        this.skala = skala;
    }

    public Date getTglLapor() {
        return tglLapor;
    }

    public void setTglLapor(Date tglLapor) {
        this.tglLapor = tglLapor;
    }

    public String getKeluhan() {
        return keluhan;
    }

    public void setKeluhan(String keluhan) {
        this.keluhan = keluhan;
    }

    public int getStatusTemuan() {
        return statusTemuan;
    }

    public void setStatusTemuan(int statusTemuan) {
        this.statusTemuan = statusTemuan;
    }
}
