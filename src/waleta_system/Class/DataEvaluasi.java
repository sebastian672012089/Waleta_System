/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package waleta_system.Class;

import java.util.Date;

/**
 *
 * @author PT. Waleta new PC 1
 */
public class DataEvaluasi {
    private Date tanggal_lp;
    private int total_lp;
    private int total_berat;
    private int total_berat_cuci;
    private int total_berat_selesai_cabut;
    private int total_berat_selesai_cetak;
    private int total_berat_selesai_f2;
    private int total_berat_selesai_qc;
    private int total_berat_selesai_grading_bj;

    public DataEvaluasi(Date tanggal_lp, int total_lp, int total_berat, int total_berat_cuci, int total_berat_selesai_cabut, int total_berat_selesai_cetak, int total_berat_selesai_f2, int total_berat_selesai_qc, int total_berat_selesai_grading_bj) {
        this.tanggal_lp = tanggal_lp;
        this.total_berat = total_berat;
        this.total_lp = total_lp;
        this.total_berat_cuci = total_berat_cuci;
        this.total_berat_selesai_cabut = total_berat_selesai_cabut;
        this.total_berat_selesai_cetak = total_berat_selesai_cetak;
        this.total_berat_selesai_f2 = total_berat_selesai_f2;
        this.total_berat_selesai_qc = total_berat_selesai_qc;
        this.total_berat_selesai_grading_bj = total_berat_selesai_grading_bj;
    }

    public int getTotal_berat_selesai_grading_bj() {
        return total_berat_selesai_grading_bj;
    }

    public void setTotal_berat_selesai_grading_bj(int total_berat_selesai_grading_bj) {
        this.total_berat_selesai_grading_bj = total_berat_selesai_grading_bj;
    }

    public Date getTanggal_lp() {
        return tanggal_lp;
    }

    public void setTanggal_lp(Date tanggal_lp) {
        this.tanggal_lp = tanggal_lp;
    }

    public int getTotal_lp() {
        return total_lp;
    }

    public void setTotal_lp(int total_lp) {
        this.total_lp = total_lp;
    }
    
    public int getTotal_berat() {
        return total_berat;
    }

    public void setTotal_berat(int total_berat) {
        this.total_berat = total_berat;
    }

    public int getTotal_berat_cuci() {
        return total_berat_cuci;
    }

    public void setTotal_berat_cuci(int total_berat_cuci) {
        this.total_berat_cuci = total_berat_cuci;
    }

    public int getTotal_berat_selesai_cabut() {
        return total_berat_selesai_cabut;
    }

    public void setTotal_berat_selesai_cabut(int total_berat_selesai_cabut) {
        this.total_berat_selesai_cabut = total_berat_selesai_cabut;
    }

    public int getTotal_berat_selesai_cetak() {
        return total_berat_selesai_cetak;
    }

    public void setTotal_berat_selesai_cetak(int total_berat_selesai_cetak) {
        this.total_berat_selesai_cetak = total_berat_selesai_cetak;
    }

    public int getTotal_berat_selesai_f2() {
        return total_berat_selesai_f2;
    }

    public void setTotal_berat_selesai_f2(int total_berat_selesai_f2) {
        this.total_berat_selesai_f2 = total_berat_selesai_f2;
    }

    public int getTotal_berat_selesai_qc() {
        return total_berat_selesai_qc;
    }

    public void setTotal_berat_selesai_qc(int total_berat_selesai_qc) {
        this.total_berat_selesai_qc = total_berat_selesai_qc;
    }
}
