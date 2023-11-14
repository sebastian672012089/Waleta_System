package waleta_system.Class;

public class GradeBahanJadi {

    private String kode;
    private String kode_grade;
    private String nama;
    private String bentuk;
    
    public GradeBahanJadi(String kode, String kode_grade, String nama, String bentuk) {
        this.kode = kode;
        this.kode_grade = kode_grade;
        this.nama = nama;
        this.bentuk = bentuk;
    }
    
    public String getKode() {
        return kode;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }

    public String getKode_grade() {
        return kode_grade;
    }

    public void setKode_grade(String kode_grade) {
        this.kode_grade = kode_grade;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getBentuk() {
        return bentuk;
    }

    public void setBentuk(String bentuk) {
        this.bentuk = bentuk;
    }
    
}
