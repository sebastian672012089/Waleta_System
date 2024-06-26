/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package waleta_system.Class;

/**
 *
 * @author mn
 */
public class DataPencabutBonus {
    private String idPegawai;
    private String namaPegawai;
    private int jumlah_cabut;
    private String grup_cabut;

    public DataPencabutBonus(String idPegawai, String namaPegawai) {
        this.idPegawai = idPegawai;
        this.namaPegawai = namaPegawai;
    }

    public DataPencabutBonus(String idPegawai, String namaPegawai, int jumlah_cabut, String grup_cabut) {
        this.idPegawai = idPegawai;
        this.namaPegawai = namaPegawai;
        this.jumlah_cabut = jumlah_cabut;
        this.grup_cabut = grup_cabut;
    }

    public int getJumlah_cabut() {
        return jumlah_cabut;
    }

    public void setJumlah_cabut(int jumlah_cabut) {
        this.jumlah_cabut = jumlah_cabut;
    }

    public String getGrup_cabut() {
        return grup_cabut;
    }

    public void setGrup_cabut(String grup_cabut) {
        this.grup_cabut = grup_cabut;
    }

    public String getIdPegawai() {
        return idPegawai;
    }

    public void setIdPegawai(String idPegawai) {
        this.idPegawai = idPegawai;
    }

    public String getNamaPegawai() {
        return namaPegawai;
    }

    public void setNamaPegawai(String namaPegawai) {
        this.namaPegawai = namaPegawai;
    }
}
