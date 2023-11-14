package waleta_system.Class;

import java.util.Date;

public class DataKaryawan {
    private String id_pegawai;
    private String pin;
    private String nik_ktp;
    private String nama_pegawai;
    private String jenis_kelamin;
    private String tempat_lahir;
    private Date tanggal_lahir;
    private String alamat;
    private String desa;
    private String kecamatan;
    private String kota;
    private String provinsi;
    private String golongan_darah;
    private String status_kawin;
    private String nama_ibu;
    private String no_telp;
    private String email;
    private String kategori_keluar;
    private String keterangan;
    private boolean uid_card;
    
    private String kode_bagian;
    private String kode_departemen;
    private String posisi;
    private String pendidikan;
    private Date tanggal_interview;
    private Date tanggal_masuk;
    private Date tanggal_keluar;
    private String status;
    private String jam_kerja;
    private String jalur_jemputan;
    private int potongan_bpjs;
    private int fc_ktp;
    private int sertifikat_vaksin1;
    private int sertifikat_vaksin2;
    private int berkas_surat_pernyataan;
    private Date tanggal_surat;
    private Date tanggal_berakhir;
    private byte[] image;
    
    public DataKaryawan(String id, String pin, String nik, String nama, String kelamin, String tempatLahir, Date tanggalLahir, String Alamat, String Desa, String Kecamatan, String kota, String provinsi, String golongan_darah, String kawin, String nama_ibu, String no_telp, String email, String kategori_keluar, String keterangan, boolean uid_card, 
            String bagian, String Posisi, String departemen, String pend, Date tanggal_interview, Date tgl_masuk, Date tgl_keluar, String Status, String jam_kerja, String jalur_jemputan, int potongan_bpjs, int fc_ktp, int sertifikat_vaksin1, int sertifikat_vaksin2, int pernyataan, Date tgl_surat, Date tgl_berakhir){
        this.id_pegawai = id;
        this.pin = pin;
        this.nik_ktp = nik;
        this.nama_pegawai = nama;
        this.jenis_kelamin = kelamin;
        this.tempat_lahir = tempatLahir;
        this.tanggal_lahir = tanggalLahir;
        this.alamat = Alamat;
        this.desa = Desa;
        this.kecamatan = Kecamatan;
        this.kota = kota;
        this.provinsi = provinsi;
        this.golongan_darah = golongan_darah;
        this.status_kawin = kawin;
        this.nama_ibu = nama_ibu;
        this.no_telp = no_telp;
        this.email = email;
        this.kategori_keluar = kategori_keluar;
        this.keterangan = keterangan;
        this.uid_card = uid_card;
    
        this.kode_bagian = bagian;
        this.kode_departemen = departemen;
        this.posisi = Posisi;
        this.pendidikan = pend;
        this.tanggal_interview = tanggal_interview;
        this.tanggal_masuk = tgl_masuk;
        this.tanggal_keluar = tgl_keluar;
        this.status = Status;
        this.jam_kerja = jam_kerja;
        this.jalur_jemputan = jalur_jemputan;
        this.potongan_bpjs = potongan_bpjs;        
        
        this.fc_ktp = fc_ktp;
        this.sertifikat_vaksin1 = sertifikat_vaksin1;
        this.sertifikat_vaksin2 = sertifikat_vaksin2;
        this.berkas_surat_pernyataan = pernyataan;
        this.tanggal_surat = tgl_surat;
        this.tanggal_berakhir = tgl_berakhir;
    }

    public String getId_pegawai() {
        return id_pegawai;
    }

    public void setId_pegawai(String id_pegawai) {
        this.id_pegawai = id_pegawai;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getNik_ktp() {
        return nik_ktp;
    }

    public void setNik_ktp(String nik_ktp) {
        this.nik_ktp = nik_ktp;
    }

    public String getNama_pegawai() {
        return nama_pegawai;
    }

    public void setNama_pegawai(String nama_pegawai) {
        this.nama_pegawai = nama_pegawai;
    }

    public String getJenis_kelamin() {
        return jenis_kelamin;
    }

    public void setJenis_kelamin(String jenis_kelamin) {
        this.jenis_kelamin = jenis_kelamin;
    }

    public String getTempat_lahir() {
        return tempat_lahir;
    }

    public void setTempat_lahir(String tempat_lahir) {
        this.tempat_lahir = tempat_lahir;
    }

    public Date getTanggal_lahir() {
        return tanggal_lahir;
    }

    public void setTanggal_lahir(Date tanggal_lahir) {
        this.tanggal_lahir = tanggal_lahir;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getDesa() {
        return desa;
    }

    public void setDesa(String desa) {
        this.desa = desa;
    }

    public String getKecamatan() {
        return kecamatan;
    }

    public void setKecamatan(String kecamatan) {
        this.kecamatan = kecamatan;
    }

    public String getKota() {
        return kota;
    }

    public void setKota(String kota) {
        this.kota = kota;
    }

    public String getProvinsi() {
        return provinsi;
    }

    public void setProvinsi(String provinsi) {
        this.provinsi = provinsi;
    }

    public String getGolongan_darah() {
        return golongan_darah;
    }

    public void setGolongan_darah(String golongan_darah) {
        this.golongan_darah = golongan_darah;
    }

    public String getStatus_kawin() {
        return status_kawin;
    }

    public void setStatus_kawin(String status_kawin) {
        this.status_kawin = status_kawin;
    }

    public String getNama_ibu() {
        return nama_ibu;
    }

    public void setNama_ibu(String nama_ibu) {
        this.nama_ibu = nama_ibu;
    }

    public String getNo_telp() {
        return no_telp;
    }

    public void setNo_telp(String no_telp) {
        this.no_telp = no_telp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getKategori_keluar() {
        return kategori_keluar;
    }

    public void setKategori_keluar(String kategori_keluar) {
        this.kategori_keluar = kategori_keluar;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public boolean isUid_card() {
        return uid_card;
    }

    public void setUid_card(boolean uid_card) {
        this.uid_card = uid_card;
    }

    public String getKode_bagian() {
        return kode_bagian;
    }

    public void setKode_bagian(String kode_bagian) {
        this.kode_bagian = kode_bagian;
    }

    public String getKode_departemen() {
        return kode_departemen;
    }

    public void setKode_departemen(String kode_departemen) {
        this.kode_departemen = kode_departemen;
    }
    
    public String getPosisi() {
        return posisi;
    }

    public void setPosisi(String posisi) {
        this.posisi = posisi;
    }

    public String getPendidikan() {
        return pendidikan;
    }

    public void setPendidikan(String pendidikan) {
        this.pendidikan = pendidikan;
    }

    public Date getTanggal_interview() {
        return tanggal_interview;
    }

    public void setTanggal_interview(Date tanggal_interview) {
        this.tanggal_interview = tanggal_interview;
    }

    public Date getTanggal_masuk() {
        return tanggal_masuk;
    }

    public void setTanggal_masuk(Date tanggal_masuk) {
        this.tanggal_masuk = tanggal_masuk;
    }

    public Date getTanggal_keluar() {
        return tanggal_keluar;
    }

    public void setTanggal_keluar(Date tanggal_keluar) {
        this.tanggal_keluar = tanggal_keluar;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getJam_kerja() {
        return jam_kerja;
    }

    public void setJam_kerja(String jam_kerja) {
        this.jam_kerja = jam_kerja;
    }

    public String getJalur_jemputan() {
        return jalur_jemputan;
    }

    public void setJalur_jemputan(String jalur_jemputan) {
        this.jalur_jemputan = jalur_jemputan;
    }

    public int getPotongan_bpjs() {
        return potongan_bpjs;
    }

    public void setPotongan_bpjs(int potongan_bpjs) {
        this.potongan_bpjs = potongan_bpjs;
    }

    public int getFc_ktp() {
        return fc_ktp;
    }

    public void setFc_ktp(int fc_ktp) {
        this.fc_ktp = fc_ktp;
    }

    public int getSertifikat_vaksin1() {
        return sertifikat_vaksin1;
    }

    public void setSertifikat_vaksin1(int sertifikat_vaksin1) {
        this.sertifikat_vaksin1 = sertifikat_vaksin1;
    }

    public int getSertifikat_vaksin2() {
        return sertifikat_vaksin2;
    }

    public void setSertifikat_vaksin2(int sertifikat_vaksin2) {
        this.sertifikat_vaksin2 = sertifikat_vaksin2;
    }

    public int getBerkas_surat_pernyataan() {
        return berkas_surat_pernyataan;
    }

    public void setBerkas_surat_pernyataan(int berkas_surat_pernyataan) {
        this.berkas_surat_pernyataan = berkas_surat_pernyataan;
    }

    public void setBerkas_surat_pernyataan(char berkas_surat_pernyataan) {
        this.berkas_surat_pernyataan = berkas_surat_pernyataan;
    }
    
    public Date getTanggal_surat() {
        return tanggal_surat;
    }

    public void setTanggal_surat(Date tanggal_surat) {
        this.tanggal_surat = tanggal_surat;
    }

    public Date getTanggal_berakhir() {
        return tanggal_berakhir;
    }

    public void setTanggal_berakhir(Date tanggal_berakhir) {
        this.tanggal_berakhir = tanggal_berakhir;
    }
}
