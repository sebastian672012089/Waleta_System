/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package waleta_system.Maintenance.model;

/**
 *
 * @author User
 */
public class Lantai {
    private String idLantai;
    private String namaLantai;
    private String gambar;
    
    public Lantai(String idLantai) {
        this.idLantai = idLantai;
    }
    
    public Lantai(String namaLantai, String gambar) {
        this.namaLantai = namaLantai;
        this.gambar = gambar;
    }

    public Lantai(String idLantai, String namaLantai, String gambar) {
        this.idLantai = idLantai;
        this.namaLantai = namaLantai;
        this.gambar = gambar;
    }

    public String getIdLantai() {
        return idLantai;
    }

    public void setIdLantai(String idLantai) {
        this.idLantai = idLantai;
    }

    public String getNamaLantai() {
        return namaLantai;
    }

    public void setNamaLantai(String namaLantai) {
        this.namaLantai = namaLantai;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }
}
