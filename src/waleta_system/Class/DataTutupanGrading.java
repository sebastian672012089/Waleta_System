package waleta_system.Class;

import java.util.Date;

public class DataTutupanGrading {
    private String kode_tutupan;
    private Date tanggal_mulai;
    private Date tanggal_selesai;
    private String nama_rumah_burung;
    private int jumlah_asal;
    private int jumlah_keping;
    private int total_berat;
    private String status;
    private String status_box;
    private Date tgl_statusBox;
    
    public DataTutupanGrading(String kode_tutupan, Date tanggal_mulai, Date tanggal_selesai, String nama_rumah_burung, int jumlah_asal, int jumlah_keping, int total_berat, String status, String status_box, Date tgl_statusBox){
        this.kode_tutupan = kode_tutupan;
        this.tanggal_mulai = tanggal_mulai;
        this.tanggal_selesai = tanggal_selesai;
        this.nama_rumah_burung = nama_rumah_burung;
        this.jumlah_asal = jumlah_asal;
        this.jumlah_keping = jumlah_keping;
        this.total_berat = total_berat;
        this.status = status;
        this.status_box = status_box;
        this.tgl_statusBox = tgl_statusBox;
    }

    public String getKode_tutupan() {
        return kode_tutupan;
    }

    public void setKode_tutupan(String kode_tutupan) {
        this.kode_tutupan = kode_tutupan;
    }

    public Date getTanggal_mulai() {
        return tanggal_mulai;
    }

    public void setTanggal_mulai(Date tanggal_mulai) {
        this.tanggal_mulai = tanggal_mulai;
    }

    public Date getTanggal_selesai() {
        return tanggal_selesai;
    }

    public void setTanggal_selesai(Date tanggal_selesai) {
        this.tanggal_selesai = tanggal_selesai;
    }

    public String getNama_rumah_burung() {
        return nama_rumah_burung;
    }

    public void setNama_rumah_burung(String nama_rumah_burung) {
        this.nama_rumah_burung = nama_rumah_burung;
    }

    public int getJumlah_asal() {
        return jumlah_asal;
    }

    public void setJumlah_asal(int jumlah_asal) {
        this.jumlah_asal = jumlah_asal;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus_box() {
        return status_box;
    }

    public void setStatus_box(String status_box) {
        this.status_box = status_box;
    }

    public Date getTgl_statusBox() {
        return tgl_statusBox;
    }

    public void setTgl_statusBox(Date tgl_statusBox) {
        this.tgl_statusBox = tgl_statusBox;
    }
    
}
