package waleta_system.Class;

import java.util.Date;

public class StockBahanBaku {
    private String no_grading;
    private String no_kartu_waleta;
    private String kode_grade;
    private Date tanggal_kartu;
    private int jumlah_keping;
    private int total_berat;
    private double harga_bahanbaku;
    
    public StockBahanBaku(String no_grading, String no_kartu, Date tanggal_kartu, String kode_grde, int jmlh_keping, int tot_berat, double harga_bahanbaku){
        this.no_grading = no_grading;
        this.no_kartu_waleta = no_kartu;
        this.tanggal_kartu = tanggal_kartu;
        this.kode_grade = kode_grde;
        this.jumlah_keping = jmlh_keping;
        this.total_berat = tot_berat;
        this.harga_bahanbaku = harga_bahanbaku;
    }

    public String getNo_grading() {
        return no_grading;
    }

    public void setNo_grading(String no_grading) {
        this.no_grading = no_grading;
    }

    public String getNo_kartu_waleta() {
        return no_kartu_waleta;
    }

    public void setNo_kartu_waleta(String no_kartu_waleta) {
        this.no_kartu_waleta = no_kartu_waleta;
    }

    public Date getTanggal_kartu() {
        return tanggal_kartu;
    }

    public void setTanggal_kartu(Date tanggal_kartu) {
        this.tanggal_kartu = tanggal_kartu;
    }

    public String getKode_grade() {
        return kode_grade;
    }

    public void setKode_grade(String kode_grade) {
        this.kode_grade = kode_grade;
    }

    public int getJumlah_keping() {
        return jumlah_keping;
    }

    public void setJumlah_keping(int jumlah_keping) {
        this.jumlah_keping = jumlah_keping;
    }

    public int getTotal_berat() {
        return total_berat;
    }

    public void setTotal_berat(int total_berat) {
        this.total_berat = total_berat;
    }

    public double getHarga_bahanbaku() {
        return harga_bahanbaku;
    }

    public void setHarga_bahanbaku(double harga_bahanbaku) {
        this.harga_bahanbaku = harga_bahanbaku;
    }
    
}
