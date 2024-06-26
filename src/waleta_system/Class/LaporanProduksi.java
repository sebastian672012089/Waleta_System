package waleta_system.Class;

import java.util.Date;

public class LaporanProduksi {

    private String no_laporan_produksi;
    private String no_kartu_waleta;
    private Date tanggal_lp;
    private Date tanggal_grading;
    private String Ruangan;
    private String kode_grade;
    private String jenis_bulu_grading;
    private String jenis_bulu_upah;
    private String memo_lp;
    private float kadar_air_lp;
    private int berat_basah;
    private int berat_kering;
    private int jumlah_keping;
    private int keping_upah;
    private int jumlah_kaki_besar_lp;
    private int jumlah_kaki_kecil_lp;
    private int jumlah_hilang_kaki_lp;
    private int jumlah_ada_susur_lp;
    private int jumlah_ada_susur_besar_lp;
    private int jumlah_tanpa_susur_lp;
    private int jumlah_utuh_lp;
    private int jumlah_hilang_ujung_lp;
    private int jumlah_pecah_1_lp;
    private int jumlah_pecah_2;
    private int jumlah_sobek;
    private int jumlah_sobek_lepas;
    private int jumlah_gumpil;
    private double harga_bahanbaku;
    private String jenis_bentuk;
    private int grup_lp;
    private int rontokan_gbm;

    public LaporanProduksi(String no_lp, String no_kartu, Date tgl_lp, Date tgl_grading, String Ruang, String kode_grde, String jenis_bulu_grading, String jenis_bulu_upah, String memo, float kadar_air, int brt_basah, int brt_kering, int jmlh_keping, int keping_upah,
            int kaki_besar_lp, 
            int kaki_kecil_lp, 
            int hilang_kaki_lp, 
            int ada_susur_lp, 
            int ada_susur_besar_lp, 
            int tanpa_susur_lp, 
            int utuh_lp, 
            int hilang_ujung_lp, 
            int jmlh_pecah_1_lp, 
            int pecah_2, 
            int jmlh_sobek, 
            int sobek_lepas, 
            int jmlh_gumpil, 
            double harga, 
            String jenis_bentuk, 
            int grup_lp,
            int rontokan_gbm
    ) {
        this.no_laporan_produksi = no_lp;
        this.no_kartu_waleta = no_kartu;
        this.tanggal_lp = tgl_lp;
        this.tanggal_grading = tgl_grading;
        this.Ruangan = Ruang;
        this.kode_grade = kode_grde;
        this.jenis_bulu_grading = jenis_bulu_grading;
        this.jenis_bulu_upah = jenis_bulu_upah;
        this.memo_lp = memo;
        this.kadar_air_lp = kadar_air;
        this.berat_basah = brt_basah;
        this.berat_kering = brt_kering;
        this.jumlah_keping = jmlh_keping;
        this.keping_upah = keping_upah;
        this.jumlah_kaki_besar_lp = kaki_besar_lp;
        this.jumlah_kaki_kecil_lp = kaki_kecil_lp;
        this.jumlah_hilang_kaki_lp = hilang_kaki_lp;
        this.jumlah_ada_susur_lp = ada_susur_lp;
        this.jumlah_ada_susur_besar_lp = ada_susur_besar_lp;
        this.jumlah_tanpa_susur_lp = tanpa_susur_lp;
        this.jumlah_utuh_lp = utuh_lp;
        this.jumlah_hilang_ujung_lp = hilang_ujung_lp;
        this.jumlah_pecah_1_lp = jmlh_pecah_1_lp;
        this.jumlah_pecah_2 = pecah_2;
        this.jumlah_sobek = jmlh_sobek;
        this.jumlah_sobek_lepas = sobek_lepas;
        this.jumlah_gumpil = jmlh_gumpil;
        this.harga_bahanbaku = harga;
        this.jenis_bentuk = jenis_bentuk;
        this.grup_lp = grup_lp;
        this.rontokan_gbm = rontokan_gbm;
    }

    public String getNo_laporan_produksi() {
        return no_laporan_produksi;
    }

    public void setNo_laporan_produksi(String no_laporan_produksi) {
        this.no_laporan_produksi = no_laporan_produksi;
    }

    public String getNo_kartu_waleta() {
        return no_kartu_waleta;
    }

    public void setNo_kartu_waleta(String no_kartu_waleta) {
        this.no_kartu_waleta = no_kartu_waleta;
    }

    public Date getTanggal_lp() {
        return tanggal_lp;
    }

    public void setTanggal_lp(Date tanggal_lp) {
        this.tanggal_lp = tanggal_lp;
    }

    public String getRuangan() {
        return Ruangan;
    }

    public void setRuangan(String Ruangan) {
        this.Ruangan = Ruangan;
    }

    public String getKode_grade() {
        return kode_grade;
    }

    public void setKode_grade(String kode_grade) {
        this.kode_grade = kode_grade;
    }

    public String getJenis_bulu_grading() {
        return jenis_bulu_grading;
    }

    public void setJenis_bulu_grading(String jenis_bulu_grading) {
        this.jenis_bulu_grading = jenis_bulu_grading;
    }

    public String getJenis_bulu_upah() {
        return jenis_bulu_upah;
    }

    public void setJenis_bulu_upah(String jenis_bulu_upah) {
        this.jenis_bulu_upah = jenis_bulu_upah;
    }

    public String getMemo_lp() {
        return memo_lp;
    }

    public void setMemo_lp(String memo_lp) {
        this.memo_lp = memo_lp;
    }

    public float getKadar_air_lp() {
        return kadar_air_lp;
    }

    public void setKadar_air_lp(float kadar_air_lp) {
        this.kadar_air_lp = kadar_air_lp;
    }

    public int getBerat_basah() {
        return berat_basah;
    }

    public void setBerat_basah(int berat_basah) {
        this.berat_basah = berat_basah;
    }

    public int getBerat_kering() {
        return berat_kering;
    }

    public void setBerat_kering(int berat_kering) {
        this.berat_kering = berat_kering;
    }

    public int getJumlah_keping() {
        return jumlah_keping;
    }

    public void setJumlah_keping(int jumlah_keping) {
        this.jumlah_keping = jumlah_keping;
    }

    public int getKeping_upah() {
        return keping_upah;
    }

    public void setKeping_upah(int keping_upah) {
        this.keping_upah = keping_upah;
    }

    public int getJumlah_gumpil() {
        return jumlah_gumpil;
    }

    public void setJumlah_gumpil(int jumlah_gumpil) {
        this.jumlah_gumpil = jumlah_gumpil;
    }

    public int getJumlah_hilang_kaki_lp() {
        return jumlah_hilang_kaki_lp;
    }

    public void setJumlah_hilang_kaki_lp(int jumlah_hilang_kaki_lp) {
        this.jumlah_hilang_kaki_lp = jumlah_hilang_kaki_lp;
    }

    public int getJumlah_hilang_ujung_lp() {
        return jumlah_hilang_ujung_lp;
    }

    public void setJumlah_hilang_ujung_lp(int jumlah_hilang_ujung_lp) {
        this.jumlah_hilang_ujung_lp = jumlah_hilang_ujung_lp;
    }

    public int getJumlah_sobek() {
        return jumlah_sobek;
    }

    public void setJumlah_sobek(int jumlah_sobek) {
        this.jumlah_sobek = jumlah_sobek;
    }

    public int getJumlah_pecah_1_lp() {
        return jumlah_pecah_1_lp;
    }

    public void setJumlah_pecah_1_lp(int jumlah_pecah_1_lp) {
        this.jumlah_pecah_1_lp = jumlah_pecah_1_lp;
    }

    public int getJumlah_utuh_lp() {
        return jumlah_utuh_lp;
    }

    public void setJumlah_utuh_lp(int jumlah_utuh_lp) {
        this.jumlah_utuh_lp = jumlah_utuh_lp;
    }

    public Date getTanggal_grading() {
        return tanggal_grading;
    }

    public void setTanggal_grading(Date tanggal_grading) {
        this.tanggal_grading = tanggal_grading;
    }

    public int getJumlah_sobek_lepas() {
        return jumlah_sobek_lepas;
    }

    public void setJumlah_sobek_lepas(int jumlah_sobek_lepas) {
        this.jumlah_sobek_lepas = jumlah_sobek_lepas;
    }

    public int getJumlah_pecah_2() {
        return jumlah_pecah_2;
    }

    public void setJumlah_pecah_2(int jumlah_pecah_2) {
        this.jumlah_pecah_2 = jumlah_pecah_2;
    }

    public int getJumlah_ada_susur_lp() {
        return jumlah_ada_susur_lp;
    }

    public void setJumlah_ada_susur_lp(int jumlah_ada_susur_lp) {
        this.jumlah_ada_susur_lp = jumlah_ada_susur_lp;
    }

    public int getJumlah_ada_susur_besar_lp() {
        return jumlah_ada_susur_besar_lp;
    }

    public void setJumlah_ada_susur_besar_lp(int jumlah_ada_susur_besar_lp) {
        this.jumlah_ada_susur_besar_lp = jumlah_ada_susur_besar_lp;
    }

    public int getJumlah_kaki_besar_lp() {
        return jumlah_kaki_besar_lp;
    }

    public void setJumlah_kaki_besar_lp(int jumlah_kaki_besar_lp) {
        this.jumlah_kaki_besar_lp = jumlah_kaki_besar_lp;
    }

    public int getJumlah_kaki_kecil_lp() {
        return jumlah_kaki_kecil_lp;
    }

    public void setJumlah_kaki_kecil_lp(int jumlah_kaki_kecil_lp) {
        this.jumlah_kaki_kecil_lp = jumlah_kaki_kecil_lp;
    }

    public int getJumlah_tanpa_susur_lp() {
        return jumlah_tanpa_susur_lp;
    }

    public void setJumlah_tanpa_susur_lp(int jumlah_tanpa_susur_lp) {
        this.jumlah_tanpa_susur_lp = jumlah_tanpa_susur_lp;
    }

    public double getHarga_bahanbaku() {
        return harga_bahanbaku;
    }

    public void setHarga_bahanbaku(double harga_bahanbaku) {
        this.harga_bahanbaku = harga_bahanbaku;
    }

    public String getJenis_bentuk() {
        return jenis_bentuk;
    }

    public void setJenis_bentuk(String jenis_bentuk) {
        this.jenis_bentuk = jenis_bentuk;
    }

    public int getGrup_lp() {
        return grup_lp;
    }

    public void setGrup_lp(int grup_lp) {
        this.grup_lp = grup_lp;
    }

    public int getRontokan_gbm() {
        return rontokan_gbm;
    }

    public void setRontokan_gbm(int rontokan_gbm) {
        this.rontokan_gbm = rontokan_gbm;
    }

}
