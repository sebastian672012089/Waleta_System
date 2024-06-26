package waleta_system.Class;

public class RumahBurung {
    private int kode_rb;
    private String no_registrasi;
    private String nama_rumah_burung;
    private String alamat_rumah_burung;
    private Integer kapasitas_rumah;
    
    public RumahBurung(int kode, String no_rumah, String nama_rumah, String alamat_rumah, int kapasitas_rumah){
        this.kode_rb = kode;
        this.no_registrasi = no_rumah;
        this.nama_rumah_burung = nama_rumah;
        this.alamat_rumah_burung = alamat_rumah;
        this.kapasitas_rumah = kapasitas_rumah;
        
    }

    public int getKode_rb() {
        return kode_rb;
    }

    public void setKode_rb(int kode_rb) {
        this.kode_rb = kode_rb;
    }
    
    public String getNo_registrasi() {
        return no_registrasi;
    }
    public void setNo_registrasi(String no_registrasi) {
        this.no_registrasi = no_registrasi;
    }
    public String getNama_rumah_burung() {
        return nama_rumah_burung;
    }
    public void setNama_rumah_burung(String nama_rumah_burung) {
        this.nama_rumah_burung = nama_rumah_burung;
    }
    public String getAlamat_rumah_burung() {
        return alamat_rumah_burung;
    }
    public void setAlamat_rumah_burung(String alamat_rumah_burung) {
        this.alamat_rumah_burung = alamat_rumah_burung;
    }
    public Integer getKapasitas_rumah() {
        return kapasitas_rumah;
    }
    public void setKapasitas_rumah(Integer kapasitas_rumah) {
        this.kapasitas_rumah = kapasitas_rumah;
    }
}
