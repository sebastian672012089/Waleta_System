/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package waleta_system.Maintenance.model;

import java.util.Date;

/**
 *
 * @author WALETA-PROJECT-PC
 */
public class Jadwal {
    private String idJadwal;
    private String namaPegawai;
    private String idAlat;
    private String namaAlat;
    private String namaKegiatan;
    private Date tglKegiatan;

    public Jadwal(String idJadwal, String namaPegawai, String idAlat, String namaAlat, String namaKegiatan, Date tglKegiatan) {
        this.idJadwal = idJadwal;
        this.namaPegawai = namaPegawai;
        this.idAlat = idAlat;
        this.namaAlat = namaAlat;
        this.namaKegiatan = namaKegiatan;
        this.tglKegiatan = tglKegiatan;
    }

    public String getIdJadwal() {
        return idJadwal;
    }

    public void setIdJadwal(String idJadwal) {
        this.idJadwal = idJadwal;
    }

    public String getNamaPegawai() {
        return namaPegawai;
    }

    public void setNamaPegawai(String namaPegawai) {
        this.namaPegawai = namaPegawai;
    }

    public String getIdAlat() {
        return idAlat;
    }

    public void setIdAlat(String idAlat) {
        this.idAlat = idAlat;
    }

    public String getNamaAlat() {
        return namaAlat;
    }

    public void setNamaAlat(String namaAlat) {
        this.namaAlat = namaAlat;
    }

    public String getNamaKegiatan() {
        return namaKegiatan;
    }

    public void setNamaKegiatan(String namaKegiatan) {
        this.namaKegiatan = namaKegiatan;
    }

    public Date getTglKegiatan() {
        return tglKegiatan;
    }

    public void setTglKegiatan(Date tglKegiatan) {
        this.tglKegiatan = tglKegiatan;
    }
    
    
}
