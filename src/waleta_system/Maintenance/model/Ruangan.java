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
public class Ruangan {
    private String idRuangan;
    private String namaRuangan;
    private Lantai lantai;
    
    public Ruangan(String namaRuangan, Lantai lantai) {
        this.namaRuangan = namaRuangan;
        this.lantai = lantai;
    }

    public Ruangan(String idRuangan, String namaRuangan, Lantai lantai) {
        this.idRuangan = idRuangan;
        this.namaRuangan = namaRuangan;
        this.lantai = lantai;
    }

    public String getIdRuangan() {
        return idRuangan;
    }

    public void setIdRuangan(String idRuangan) {
        this.idRuangan = idRuangan;
    }

    public String getNamaRuangan() {
        return namaRuangan;
    }

    public void setNamaRuangan(String namaRuangan) {
        this.namaRuangan = namaRuangan;
    }

    public Lantai getLantai() {
        return lantai;
    }

    public void setLantai(Lantai lantai) {
        this.lantai = lantai;
    }
}
