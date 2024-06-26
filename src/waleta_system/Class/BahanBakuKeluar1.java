package waleta_system.Class;

import java.util.Date;

public class BahanBakuKeluar1 {
    private int kode_keluar;
    private String jenis;
    private String no_kartu_waleta;
    private String kode_grade;
    private Date tanggal_keluar;
    private String customer;
    private String keterangan;
    private int total_keping;
    private int total_berat;
    
    public BahanBakuKeluar1(int kode, String jenis, String no_kartu, String grade, Date tgl_keluar, String cust, String keterangan, int keping, int berat){
        this.kode_keluar = kode;
        this.jenis = jenis;
        this.no_kartu_waleta = no_kartu;
        this.kode_grade = grade;
        this.tanggal_keluar = tgl_keluar;
        this.customer = cust;
        this.keterangan = keterangan;
        this.total_keping = keping;
        this.total_berat = berat;
    }

    public int getKode_keluar() {
        return kode_keluar;
    }

    public void setKode_keluar(int kode_keluar) {
        this.kode_keluar = kode_keluar;
    }

    public String getJenis() {
        return jenis;
    }

    public void setJenis(String jenis) {
        this.jenis = jenis;
    }

    public String getNo_kartu_waleta() {
        return no_kartu_waleta;
    }

    public void setNo_kartu_waleta(String no_kartu_waleta) {
        this.no_kartu_waleta = no_kartu_waleta;
    }

    public Date getTanggal_keluar() {
        return tanggal_keluar;
    }

    public void setTanggal_keluar(Date tanggal_keluar) {
        this.tanggal_keluar = tanggal_keluar;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public int getTotal_berat() {
        return total_berat;
    }

    public void setTotal_berat(int total_berat) {
        this.total_berat = total_berat;
    }

    public String getKode_grade() {
        return kode_grade;
    }

    public void setKode_grade(String kode_grade) {
        this.kode_grade = kode_grade;
    }

    public int getTotal_keping() {
        return total_keping;
    }

    public void setTotal_keping(int total_keping) {
        this.total_keping = total_keping;
    }
}
