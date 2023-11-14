package waleta_system.Class;

public class GradeBahanBaku {
    private String kode_grade;
    private String jenis_bentuk;
    private String jenis_bulu;
    private String jenis_warna;
    private String kategori;
    
    public GradeBahanBaku(String grade, String bentuk, String bulu, String warna, String kategori){
        this.kode_grade = grade;
        this.jenis_bentuk = bentuk;
        this.jenis_bulu = bulu;
        this.jenis_warna = warna;
        this.kategori = kategori;
    }
    
    public String getKode_grade() {
        return kode_grade;
    }
    
    public void setKode_grade(String kode_grade) {
        this.kode_grade = kode_grade;
    }
    
    public String getJenis_bentuk() {
        return jenis_bentuk;
    }
    
    public void setJenis_bentuk(String jenis_bentuk) {
        this.jenis_bentuk = jenis_bentuk;
    }
    
    public String getJenis_bulu() {
        return jenis_bulu;
    }
    
    public void setJenis_bulu(String jenis_bulu) {
        this.jenis_bulu = jenis_bulu;
    }
    
    public String getJenis_warna() {
        return jenis_warna;
    }
    
    public void setJenis_warna(String jenis_warna) {
        this.jenis_warna = jenis_warna;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }
    
}
