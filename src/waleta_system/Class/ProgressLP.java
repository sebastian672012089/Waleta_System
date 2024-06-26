package waleta_system.Class;

import java.util.Date;

public class ProgressLP {

    private String no_lp;
    private String no_kartu;
    private String memo;
    private String kode_grade;
    private String jenis_bulu;
    private String ruangan;
    private int keping;
    private int Berat_basah;
    private Date tgl_lp;
    private Date tgl_rendam;
    private Date tgl_cuci;
    private Date tgl_masuk_cabut;
    private Date tgl_mulai_cabut;
    private Date tgl_selesai_cabut;
    private Date tgl_masuk_cetak;
    private Date tgl_cetak_dikerjakan1;
    private Date tgl_selesai_cetak;
    private Date tgl_masuk_f2;
    private Date tgl_koreksi;
    private Date tgl_f1;
    private Date tgl_f2;
    private Date tgl_setor_f2;
    private Date tgl_masuk_lab;
    private Date tgl_setor_lab;
    private Date tgl_grading_bj;
    private String status_box;
    private String rsb_ct1;
    
    public ProgressLP(String no, 
                        String no_kartu, 
                        String memo, 
                        String grade, 
                        String jenis_bulu, 
                        String ruang, 
                        int keping, 
                        int Berat_basah, 
                        Date lp, 
                        Date rendam, 
                        Date cuci, 
                        Date masuk_cabut, 
                        Date mulai_cabut, 
                        Date selesai_cabut, 
                        Date masuk_cetak, 
                        Date tgl_cetak_dikerjakan1, 
                        Date selesai_cetak, 
                        Date masuk_f2, 
                        Date tgl_koreksi, 
                        Date tgl_f1, 
                        Date tgl_f2, 
                        Date setor_f2,
                        Date masuk_lab,
                        Date setor_lab,
                        Date tgl_grading_bj,
                        String status_box,
                        String rsb_ct1){
        this.no_lp = no;
        this.no_kartu = no_kartu;
        this.memo = memo;
        this.kode_grade = grade;
        this.jenis_bulu = jenis_bulu;
        this.ruangan = ruang;
        this.keping = keping;
        this.Berat_basah = Berat_basah;
        this.tgl_lp = lp;
        this.tgl_rendam = rendam;
        this.tgl_cuci = cuci;
        this.tgl_masuk_cabut = masuk_cabut;
        this.tgl_mulai_cabut = mulai_cabut;
        this.tgl_selesai_cabut = selesai_cabut;
        this.tgl_masuk_cetak = masuk_cetak;
        this.tgl_cetak_dikerjakan1 = tgl_cetak_dikerjakan1;
        this.tgl_selesai_cetak = selesai_cetak;
        this.tgl_masuk_f2 = masuk_f2;
        this.tgl_koreksi = tgl_koreksi;
        this.tgl_f1 = tgl_f1;
        this.tgl_f2 = tgl_f2;
        this.tgl_setor_f2 = setor_f2;
        this.tgl_masuk_lab = masuk_lab;
        this.tgl_setor_lab = setor_lab;
        this.tgl_grading_bj = tgl_grading_bj;
        this.status_box = status_box;
        this.rsb_ct1 = rsb_ct1;
    }

    public String getNo_lp() {
        return no_lp;
    }

    public String getNo_kartu() {
        return no_kartu;
    }

    public void setNo_kartu(String no_kartu) {
        this.no_kartu = no_kartu;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getKode_grade() {
        return kode_grade;
    }

    public void setKode_grade(String kode_grade) {
        this.kode_grade = kode_grade;
    }

    public String getJenis_bulu() {
        return jenis_bulu;
    }

    public void setJenis_bulu(String jenis_bulu) {
        this.jenis_bulu = jenis_bulu;
    }

    public String getRuangan() {
        return ruangan;
    }

    public void setRuangan(String ruangan) {
        this.ruangan = ruangan;
    }

    public int getKeping() {
        return keping;
    }

    public void setKeping(int keping) {
        this.keping = keping;
    }

    public int getBerat_basah() {
        return Berat_basah;
    }

    public void setBerat_basah(int Berat_basah) {
        this.Berat_basah = Berat_basah;
    }

    public void setNo_lp(String no_lp) {
        this.no_lp = no_lp;
    }

    public Date getTgl_lp() {
        return tgl_lp;
    }

    public void setTgl_lp(Date tgl_lp) {
        this.tgl_lp = tgl_lp;
    }

    public Date getTgl_rendam() {
        return tgl_rendam;
    }

    public void setTgl_rendam(Date tgl_rendam) {
        this.tgl_rendam = tgl_rendam;
    }

    public Date getTgl_cuci() {
        return tgl_cuci;
    }

    public void setTgl_cuci(Date tgl_cuci) {
        this.tgl_cuci = tgl_cuci;
    }

    public Date getTgl_masuk_cabut() {
        return tgl_masuk_cabut;
    }

    public void setTgl_masuk_cabut(Date tgl_masuk_cabut) {
        this.tgl_masuk_cabut = tgl_masuk_cabut;
    }

    public Date getTgl_mulai_cabut() {
        return tgl_mulai_cabut;
    }

    public void setTgl_mulai_cabut(Date tgl_mulai_cabut) {
        this.tgl_mulai_cabut = tgl_mulai_cabut;
    }

    public Date getTgl_selesai_cabut() {
        return tgl_selesai_cabut;
    }

    public void setTgl_selesai_cabut(Date tgl_selesai_cabut) {
        this.tgl_selesai_cabut = tgl_selesai_cabut;
    }

    public Date getTgl_masuk_cetak() {
        return tgl_masuk_cetak;
    }

    public void setTgl_masuk_cetak(Date tgl_masuk_cetak) {
        this.tgl_masuk_cetak = tgl_masuk_cetak;
    }

    public Date getTgl_cetak_dikerjakan1() {
        return tgl_cetak_dikerjakan1;
    }

    public void setTgl_cetak_dikerjakan1(Date tgl_cetak_dikerjakan1) {
        this.tgl_cetak_dikerjakan1 = tgl_cetak_dikerjakan1;
    }

    public Date getTgl_selesai_cetak() {
        return tgl_selesai_cetak;
    }

    public void setTgl_selesai_cetak(Date tgl_selesai_cetak) {
        this.tgl_selesai_cetak = tgl_selesai_cetak;
    }

    public Date getTgl_masuk_f2() {
        return tgl_masuk_f2;
    }

    public void setTgl_masuk_f2(Date tgl_masuk_f2) {
        this.tgl_masuk_f2 = tgl_masuk_f2;
    }

    public Date getTgl_koreksi() {
        return tgl_koreksi;
    }

    public void setTgl_koreksi(Date tgl_koreksi) {
        this.tgl_koreksi = tgl_koreksi;
    }

    public Date getTgl_f1() {
        return tgl_f1;
    }

    public void setTgl_f1(Date tgl_f1) {
        this.tgl_f1 = tgl_f1;
    }

    public Date getTgl_f2() {
        return tgl_f2;
    }

    public void setTgl_f2(Date tgl_f2) {
        this.tgl_f2 = tgl_f2;
    }

    public Date getTgl_setor_f2() {
        return tgl_setor_f2;
    }

    public void setTgl_setor_f2(Date tgl_setor_f2) {
        this.tgl_setor_f2 = tgl_setor_f2;
    }   
    
    public Date getTgl_masuk_lab() {
        return tgl_masuk_lab;
    }
    
    public void setTgl_masuk_lab(Date tgl_masuk_lab) {
        this.tgl_masuk_lab = tgl_masuk_lab;
    }
    
    public Date getTgl_setor_lab() {
        return tgl_setor_lab;
    }
    
    public void setTgl_setor_lab(Date tgl_setor_lab) {
        this.tgl_setor_lab = tgl_setor_lab;
    }

    public Date getTgl_grading_bj() {
        return tgl_grading_bj;
    }

    public void setTgl_grading_bj(Date tgl_grading_bj) {
        this.tgl_grading_bj = tgl_grading_bj;
    }

    public String getStatus_box() {
        return status_box;
    }

    public void setStatus_box(String status_box) {
        this.status_box = status_box;
    }

    public String getRsb_ct1() {
        return rsb_ct1;
    }

    public void setRsb_ct1(String rsb_ct1) {
        this.rsb_ct1 = rsb_ct1;
    }

}
