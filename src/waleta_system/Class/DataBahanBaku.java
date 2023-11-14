package waleta_system.Class;

import java.util.Date;

public class DataBahanBaku {
    private String no_kartu_waleta;
    private String nama_supplier;
    private String nama_rumah_burung;
    private Date tgl_masuk;
    private Date tgl_panen;
    private Date tgl_grading;
    private Date tgl_timbang;
    private int nitrit_bahan_mentah;
    private float kadar_air_bahan_baku;
    private int berat_asal;
    private int berat_real;
    private int keping_real;
    private double harga_kartu;
    private int last_stok;
    
    public DataBahanBaku(String no_kartu, String nama, String rumah_burung, Date masuk, Date Panen, Date grading, Date Timbang, int nitrit, float kadar_air, int berat_asal, int berat_real, int keping_real, double harga_kartu, int last_stok){
        this.no_kartu_waleta = no_kartu;
        this.nama_supplier = nama;
        this.nama_rumah_burung = rumah_burung;
        this.tgl_masuk = masuk;
        this.tgl_panen = Panen;
        this.tgl_grading = grading;
        this.tgl_timbang = Timbang;
        this.nitrit_bahan_mentah = nitrit;
        this.kadar_air_bahan_baku = kadar_air;
        this.berat_asal = berat_asal;
        this.berat_real = berat_real;
        this.keping_real = keping_real;
        this.harga_kartu = harga_kartu;
        this.last_stok = last_stok;
    }

    public String getNo_kartu_waleta() {
        return no_kartu_waleta;
    }

    public void setNo_kartu_waleta(String no_kartu_waleta) {
        this.no_kartu_waleta = no_kartu_waleta;
    }

    public String getNama_supplier() {
        return nama_supplier;
    }

    public void setNama_supplier(String nama_supplier) {
        this.nama_supplier = nama_supplier;
    }

    public String getNama_rumah_burung() {
        return nama_rumah_burung;
    }

    public void setNama_rumah_burung(String nama_rumah_burung) {
        this.nama_rumah_burung = nama_rumah_burung;
    }

    public Date getTgl_masuk() {
        return tgl_masuk;
    }

    public void setTgl_masuk(Date tgl_masuk) {
        this.tgl_masuk = tgl_masuk;
    }

    public Date getTgl_panen() {
        return tgl_panen;
    }

    public void setTgl_panen(Date tgl_panen) {
        this.tgl_panen = tgl_panen;
    }

    public Date getTgl_grading() {
        return tgl_grading;
    }

    public void setTgl_grading(Date tgl_grading) {
        this.tgl_grading = tgl_grading;
    }

    public Date getTgl_timbang() {
        return tgl_timbang;
    }

    public void setTgl_timbang(Date tgl_timbang) {
        this.tgl_timbang = tgl_timbang;
    }

    public int getNitrit_bahan_mentah() {
        return nitrit_bahan_mentah;
    }

    public void setNitrit_bahan_mentah(int nitrit_bahan_mentah) {
        this.nitrit_bahan_mentah = nitrit_bahan_mentah;
    }

    public float getKadar_air_bahan_baku() {
        return kadar_air_bahan_baku;
    }

    public void setKadar_air_bahan_baku(float kadar_air_bahan_baku) {
        this.kadar_air_bahan_baku = kadar_air_bahan_baku;
    }

    public int getBerat_asal() {
        return berat_asal;
    }

    public void setBerat_asal(int berat_asal) {
        this.berat_asal = berat_asal;
    }

    public int getBerat_real() {
        return berat_real;
    }

    public void setBerat_real(int berat_real) {
        this.berat_real = berat_real;
    }

    public int getKeping_real() {
        return keping_real;
    }

    public void setKeping_real(int keping_real) {
        this.keping_real = keping_real;
    }

    public double getHarga_kartu() {
        return harga_kartu;
    }

    public void setHarga_kartu(double harga_kartu) {
        this.harga_kartu = harga_kartu;
    }

    public int getLast_stok() {
        return last_stok;
    }

    public void setLast_stok(int last_stok) {
        this.last_stok = last_stok;
    }
    
}
