
package waleta_system.Class;


public class HakAkses {
    private int kode_bagian;
    private String nama_bagian;
    private String posisi;
    private String hak_akses;
    
    public HakAkses(int kode_bagian, String nama, String posisi, String hak_akses){
        this.kode_bagian = kode_bagian;
        this.nama_bagian = nama;
        this.posisi = posisi;
        this.hak_akses = hak_akses;
    }

    public int getKode_bagian() {
        return kode_bagian;
    }

    public void setKode_bagian(int kode_bagian) {
        this.kode_bagian = kode_bagian;
    }

    public String getNama_bagian() {
        return nama_bagian;
    }

    public void setNama_bagian(String nama_bagian) {
        this.nama_bagian = nama_bagian;
    }

    public String getPosisi() {
        return posisi;
    }

    public void setPosisi(String posisi) {
        this.posisi = posisi;
    }

    public String getHak_akses() {
        return hak_akses;
    }

    public void setHak_akses(String hak_akses) {
        this.hak_akses = hak_akses;
    }
    
}
