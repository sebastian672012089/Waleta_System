package waleta_system.Class;

import java.util.Date;
import java.util.List;

public class DataUser {
    private String username;
    private String password;
    private String id_pegawai;
    private String nama_pegawai;
    private String posisi;
    private String departemen;
    private String bagian;
    private String status;
    private List<AksesMenu.Akses> dataMenu;
    
    public DataUser(String user, String pass, String id, String nama, String psisi, String dep, String bag, String status){
        this.username = user;
        this.password = pass;
        this.id_pegawai = id;
        this.nama_pegawai = nama;
        this.posisi = psisi;
        this.departemen = dep;
        this.bagian = bag;
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId_pegawai() {
        return id_pegawai;
    }

    public void setId_pegawai(String id_pegawai) {
        this.id_pegawai = id_pegawai;
    }

    public String getNama_pegawai() {
        return nama_pegawai;
    }

    public void setNama_pegawai(String nama_pegawai) {
        this.nama_pegawai = nama_pegawai;
    }

    public String getPosisi() {
        return posisi;
    }

    public void setPosisi(String posisi) {
        this.posisi = posisi;
    }

    public String getDepartemen() {
        return departemen;
    }

    public void setDepartemen(String departemen) {
        this.departemen = departemen;
    }

    public String getBagian() {
        return bagian;
    }

    public void setBagian(String bagian) {
        this.bagian = bagian;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<AksesMenu.Akses> getDataMenu() {
        return dataMenu;
    }

    public void setDataMenu(List<AksesMenu.Akses> dataMenu) {
        this.dataMenu = dataMenu;
    }
    
}
