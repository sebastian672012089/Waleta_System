package waleta_system.Class;

import java.util.Date;

public class DataF2 {

    private String no_kartu;
    private String no_lp;
    private int jumlah_keping;
    private int berat_kering;
    private String memo_lp;
    private String grade_bk;
    private String grade_bentuk;
    private String grade_bulu;
    private String ruangan;
    private String cetak;
    private String cabut;
    private Date tgl_input_bp;
    private Date tgl_dikerjakan_f2;
    private String pekerja_koreksi;
    private Date tgl_f1;
    private String pekerja_f1;
    private Date tgl_f2;
    private String pekerja_f2;
    private Date tgl_mulai_f2;
    private String f2_diterima;
    private Date tgl_setor_f2;
    private String f2_disetor;
    private String f2_timbang;
    private int fbonus_keping;
    private float fbonus_berat;
    private int fnol_keping;
    private float fnol_berat;
    private int pecah_keping;
    private float pecah_berat;
    private int flat_keping;
    private float flat_berat;
    private int jidun_keping;
    private int jidun_pecah;
    private float jidun_berat;

    private float sesekan;
    private float hancuran;
    private float rontokan;
    private float bonggol;
    private float serabut;

    private float tambah_kaki1;
    private String lp_kaki1;
    private float tambah_kaki2;
    private String lp_kaki2;
    private String admin_f2;
    private String otorisasi;
    private String keterangan;

    public DataF2(String kartu, String lp, int jumlah_keping, int berat_kering, String memo_lp, String grade, String bentuk, String bulu, String ruang, String pencetak, String pencabut, Date tgl_input_bp, Date tgl_dikerjakan, String pekerja_koreksi, Date tgl_f1, String pekerja_f1, Date tgl_f2, String pekerja_f2, Date tgl_mulai, String diterima, Date tgl_setor, String disetor, String f2_timbang, int fbonus_kpg, float fbonus_brt, int fnol_kpg, float fnol_brt, int pecah_kpg, float pecah_brt, int flat_kpg, float flat_brt, int jidun_kpg, int jidun_pecah, float jidun_brt, float ss, float h, float r, float b, float s, float tmbh_kaki1, String lp_kk1, float tmbh_kaki2, String lp_kk2, String admin, String otorisasi, String keterangan) {
        this.no_kartu = kartu;
        this.no_lp = lp;
        this.jumlah_keping = jumlah_keping;
        this.berat_kering = berat_kering;
        this.memo_lp = memo_lp;
        this.grade_bk = grade;
        this.grade_bentuk = bentuk;
        this.grade_bulu = bulu;
        this.ruangan = ruang;
        this.cetak = pencetak;
        this.cabut = pencabut;
        this.tgl_input_bp = tgl_input_bp;
        this.tgl_dikerjakan_f2 = tgl_dikerjakan;
        this.pekerja_koreksi = pekerja_koreksi;
        this.tgl_f1 = tgl_f1;
        this.pekerja_f1 = pekerja_f1;
        this.tgl_f2 = tgl_f2;
        this.pekerja_f2 = pekerja_f2;
        this.tgl_mulai_f2 = tgl_mulai;
        this.f2_diterima = diterima;
        this.tgl_setor_f2 = tgl_setor;
        this.f2_disetor = disetor;
        this.f2_timbang = f2_timbang;

        this.fbonus_keping = fbonus_kpg;
        this.fbonus_berat = fbonus_brt;
        this.fnol_keping = fnol_kpg;
        this.fnol_berat = fnol_brt;
        this.pecah_keping = pecah_kpg;
        this.pecah_berat = pecah_brt;
        this.flat_keping = flat_kpg;
        this.flat_berat = flat_brt;
        this.jidun_keping = jidun_kpg;
        this.jidun_pecah = jidun_pecah;
        this.jidun_berat = jidun_brt;
        this.sesekan = ss;
        this.hancuran = h;
        this.rontokan = r;
        this.bonggol = b;
        this.serabut = s;
        this.tambah_kaki1 = tmbh_kaki1;
        this.lp_kaki1 = lp_kk1;
        this.tambah_kaki2 = tmbh_kaki2;
        this.lp_kaki2 = lp_kk2;
        this.admin_f2 = admin;
        this.otorisasi = otorisasi;
        this.keterangan = keterangan;
    }

    public String getNo_kartu() {
        return no_kartu;
    }

    public void setNo_kartu(String no_kartu) {
        this.no_kartu = no_kartu;
    }

    public String getNo_lp() {
        return no_lp;
    }

    public void setNo_lp(String no_lp) {
        this.no_lp = no_lp;
    }

    public int getJumlah_keping() {
        return jumlah_keping;
    }

    public void setJumlah_keping(int jumlah_keping) {
        this.jumlah_keping = jumlah_keping;
    }

    public int getBerat_Kering() {
        return berat_kering;
    }

    public void setBerat_Kering(int berat_kering) {
        this.berat_kering = berat_kering;
    }

    public String getMemo_lp() {
        return memo_lp;
    }

    public void setMemo_lp(String memo_lp) {
        this.memo_lp = memo_lp;
    }

    public String getGrade_bk() {
        return grade_bk;
    }

    public void setGrade_bk(String grade_bk) {
        this.grade_bk = grade_bk;
    }

    public String getGrade_bentuk() {
        return grade_bentuk;
    }

    public void setGrade_bentuk(String grade_bentuk) {
        this.grade_bentuk = grade_bentuk;
    }

    public String getGrade_bulu() {
        return grade_bulu;
    }

    public void setGrade_bulu(String grade_bulu) {
        this.grade_bulu = grade_bulu;
    }

    public String getRuangan() {
        return ruangan;
    }

    public void setRuangan(String ruangan) {
        this.ruangan = ruangan;
    }

    public String getCetak() {
        return cetak;
    }

    public void setCetak(String cetak) {
        this.cetak = cetak;
    }

    public String getCabut() {
        return cabut;
    }

    public void setCabut(String cabut) {
        this.cabut = cabut;
    }

    public Date getTgl_input_bp() {
        return tgl_input_bp;
    }

    public void setTgl_input_bp(Date tgl_input_bp) {
        this.tgl_input_bp = tgl_input_bp;
    }

    public Date getTgl_dikerjakan_f2() {
        return tgl_dikerjakan_f2;
    }

    public void setTgl_dikerjakan_f2(Date tgl_dikerjakan_f2) {
        this.tgl_dikerjakan_f2 = tgl_dikerjakan_f2;
    }

    public String getPekerja_koreksi() {
        return pekerja_koreksi;
    }

    public void setPekerja_koreksi(String pekerja_koreksi) {
        this.pekerja_koreksi = pekerja_koreksi;
    }

    public Date getTgl_f1() {
        return tgl_f1;
    }

    public void setTgl_f1(Date tgl_f1) {
        this.tgl_f1 = tgl_f1;
    }

    public String getPekerja_f1() {
        return pekerja_f1;
    }

    public void setPekerja_f1(String pekerja_f1) {
        this.pekerja_f1 = pekerja_f1;
    }

    public Date getTgl_f2() {
        return tgl_f2;
    }

    public void setTgl_f2(Date tgl_f2) {
        this.tgl_f2 = tgl_f2;
    }

    public String getPekerja_f2() {
        return pekerja_f2;
    }

    public void setPekerja_f2(String pekerja_f2) {
        this.pekerja_f2 = pekerja_f2;
    }

    public Date getTgl_mulai_f2() {
        return tgl_mulai_f2;
    }

    public void setTgl_mulai_f2(Date tgl_mulai_f2) {
        this.tgl_mulai_f2 = tgl_mulai_f2;
    }

    public String getF2_diterima() {
        return f2_diterima;
    }

    public void setF2_diterima(String f2_diterima) {
        this.f2_diterima = f2_diterima;
    }

    public Date getTgl_setor_f2() {
        return tgl_setor_f2;
    }

    public void setTgl_setor_f2(Date tgl_setor_f2) {
        this.tgl_setor_f2 = tgl_setor_f2;
    }

    public String getF2_disetor() {
        return f2_disetor;
    }

    public void setF2_disetor(String f2_disetor) {
        this.f2_disetor = f2_disetor;
    }

    public String getF2_timbang() {
        return f2_timbang;
    }

    public void setF2_timbang(String f2_timbang) {
        this.f2_timbang = f2_timbang;
    }

    public int getFbonus_keping() {
        return fbonus_keping;
    }

    public void setFbonus_keping(int fbonus_keping) {
        this.fbonus_keping = fbonus_keping;
    }

    public float getFbonus_berat() {
        return fbonus_berat;
    }

    public void setFbonus_berat(float fbonus_berat) {
        this.fbonus_berat = fbonus_berat;
    }

    public int getFnol_keping() {
        return fnol_keping;
    }

    public void setFnol_keping(int fnol_keping) {
        this.fnol_keping = fnol_keping;
    }

    public float getFnol_berat() {
        return fnol_berat;
    }

    public void setFnol_berat(float fnol_berat) {
        this.fnol_berat = fnol_berat;
    }

    public int getPecah_keping() {
        return pecah_keping;
    }

    public void setPecah_keping(int pecah_keping) {
        this.pecah_keping = pecah_keping;
    }

    public float getPecah_berat() {
        return pecah_berat;
    }

    public void setPecah_berat(float pecah_berat) {
        this.pecah_berat = pecah_berat;
    }

    public int getFlat_keping() {
        return flat_keping;
    }

    public void setFlat_keping(int flat_keping) {
        this.flat_keping = flat_keping;
    }

    public float getFlat_berat() {
        return flat_berat;
    }

    public void setFlat_berat(float flat_berat) {
        this.flat_berat = flat_berat;
    }

    public int getJidun_keping() {
        return jidun_keping;
    }

    public int getJidun_pecah() {
        return jidun_pecah;
    }

    public void setJidun_pecah(int jidun_pecah) {
        this.jidun_pecah = jidun_pecah;
    }

    public void setJidun_keping(int jidun_keping) {
        this.jidun_keping = jidun_keping;
    }

    public float getJidun_berat() {
        return jidun_berat;
    }

    public void setJidun_berat(float jidun_berat) {
        this.jidun_berat = jidun_berat;
    }

    public float getSesekan() {
        return sesekan;
    }

    public void setSesekan(float sesekan) {
        this.sesekan = sesekan;
    }

    public float getHancuran() {
        return hancuran;
    }

    public void setHancuran(float hancuran) {
        this.hancuran = hancuran;
    }

    public float getRontokan() {
        return rontokan;
    }

    public void setRontokan(float rontokan) {
        this.rontokan = rontokan;
    }

    public float getBonggol() {
        return bonggol;
    }

    public void setBonggol(float bonggol) {
        this.bonggol = bonggol;
    }

    public float getSerabut() {
        return serabut;
    }

    public void setSerabut(float serabut) {
        this.serabut = serabut;
    }

    public int getBerat_kering() {
        return berat_kering;
    }

    public void setBerat_kering(int berat_kering) {
        this.berat_kering = berat_kering;
    }

    public float getTambah_kaki1() {
        return tambah_kaki1;
    }

    public void setTambah_kaki1(float tambah_kaki1) {
        this.tambah_kaki1 = tambah_kaki1;
    }

    public String getLp_kaki1() {
        return lp_kaki1;
    }

    public void setLp_kaki1(String lp_kaki1) {
        this.lp_kaki1 = lp_kaki1;
    }

    public float getTambah_kaki2() {
        return tambah_kaki2;
    }

    public void setTambah_kaki2(float tambah_kaki2) {
        this.tambah_kaki2 = tambah_kaki2;
    }

    public String getLp_kaki2() {
        return lp_kaki2;
    }

    public void setLp_kaki2(String lp_kaki2) {
        this.lp_kaki2 = lp_kaki2;
    }

    public String getAdmin_f2() {
        return admin_f2;
    }

    public void setAdmin_f2(String admin_f2) {
        this.admin_f2 = admin_f2;
    }

    public String getOtorisasi() {
        return otorisasi;
    }

    public void setOtorisasi(String otorisasi) {
        this.otorisasi = otorisasi;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }
    
}
