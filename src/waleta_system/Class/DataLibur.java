/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package waleta_system.Class;

import java.util.Date;

/**
 *
 * @author mn
 */
public class DataLibur {
    private Date tanggal;
    private String keterangan;

    public DataLibur(Date tanggal, String keterangan) {
        this.tanggal = tanggal;
        this.keterangan = keterangan;
    }

    public Date getTanggal() {
        return tanggal;
    }

    public void setTanggal(Date tanggal) {
        this.tanggal = tanggal;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }
}
