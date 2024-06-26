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
public class KategoriAlat {
    private int idKategoriAlat;
    private String namaAlat;
    private String iconHealthy;
    private String iconInRepair;
    private String iconDamaged;

    public KategoriAlat(int idKategoriAlat) {
        this.idKategoriAlat = idKategoriAlat;
    }

    public KategoriAlat(String namaAlat) {
        this.namaAlat = namaAlat;
    }

    public KategoriAlat(String namaAlat, String iconHealthy, String iconInRepair, String iconDamaged) {
        this.namaAlat = namaAlat;
        this.iconHealthy = iconHealthy;
        this.iconInRepair = iconInRepair;
        this.iconDamaged = iconDamaged;
    }

    public KategoriAlat(int idKategoriAlat, String namaAlat, String iconHealthy, String iconInRepair, String iconDamaged) {
        this.idKategoriAlat = idKategoriAlat;
        this.namaAlat = namaAlat;
        this.iconHealthy = iconHealthy;
        this.iconInRepair = iconInRepair;
        this.iconDamaged = iconDamaged;
    }

    public int getIdKategoriAlat() {
        return idKategoriAlat;
    }

    public void setIdKategoriAlat(int idKategoriAlat) {
        this.idKategoriAlat = idKategoriAlat;
    }

    public String getNamaAlat() {
        return namaAlat;
    }

    public void setNamaAlat(String namaAlat) {
        this.namaAlat = namaAlat;
    }

    public String getIconHealthy() {
        return iconHealthy;
    }

    public void setIconHealthy(String iconHealthy) {
        this.iconHealthy = iconHealthy;
    }

    public String getIconInRepair() {
        return iconInRepair;
    }

    public void setIconInRepair(String iconInRepair) {
        this.iconInRepair = iconInRepair;
    }

    public String getIconDamaged() {
        return iconDamaged;
    }

    public void setIconDamaged(String iconDamaged) {
        this.iconDamaged = iconDamaged;
    }
}
