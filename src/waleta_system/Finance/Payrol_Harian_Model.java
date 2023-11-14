package waleta_system.Finance;

import waleta_system.Class.Utility;

public class Payrol_Harian_Model {
    private String id;
    private String nama;
    private String tgl1;
    private String tgl2;
    private String tgl3;
    private String tgl4;
    private String tgl5;
    private String tgl6;
    private String tgl7;
    private String totalLembur;
    private String lembur;
    private String terlambat;
    private String ijinKeluar;
    private String tunjHadir;
    private String transport;
    private String bpjs;
    private String bpjs_tk;
    private String bonusTBT;
    private String hariKerja;
    private String gajiHarian;
    private String gajiBorong;
    private String bonus1;
    private String bonus2;
    private String bonus_pencapaian_produksi;
    private String piutang_karyawan;
    private String totalGaji;
    private String ket;
    private String grup;

    public Payrol_Harian_Model(String id, String nama, String tgl1, String tgl2, String tgl3, String tgl4, String tgl5, String tgl6, String tgl7, String totalLembur, String lembur, String terlambat, String ijinKeluar, String tunjHadir, String transport, String bpjs, String bpjs_tk, String bonusTBT, String hariKerja, String gajiHarian, String gajiBorong, String bonus1, String bonus2, String bonus_pencapaian_produksi, String piutang_karyawan, String totalGaji, String ket, String grup) {
        this.id = id;
        this.nama = nama;
        this.tgl1 = tgl1;
        this.tgl2 = tgl2;
        this.tgl3 = tgl3;
        this.tgl4 = tgl4;
        this.tgl5 = tgl5;
        this.tgl6 = tgl6;
        this.tgl7 = tgl7;
        this.totalLembur = totalLembur;
        this.lembur = lembur;
        this.terlambat = terlambat;
        this.ijinKeluar = ijinKeluar;
        this.tunjHadir = tunjHadir;
        this.transport = transport;
        this.bpjs = bpjs;
        this.bpjs_tk = bpjs_tk;
        this.bonusTBT = bonusTBT;
        this.hariKerja = hariKerja;
        this.gajiHarian = gajiHarian;
        this.gajiBorong = gajiBorong;
        this.bonus1 = bonus1;
        this.bonus2 = bonus2;
        this.bonus_pencapaian_produksi = bonus_pencapaian_produksi;
        this.piutang_karyawan = piutang_karyawan;
        this.totalGaji = totalGaji;
        this.ket = ket;
        this.grup = grup;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getTgl1() {
        return tgl1;
    }

    public void setTgl1(String tgl1) {
        this.tgl1 = tgl1;
    }

    public String getTgl2() {
        return tgl2;
    }

    public void setTgl2(String tgl2) {
        this.tgl2 = tgl2;
    }

    public String getTgl3() {
        return tgl3;
    }

    public void setTgl3(String tgl3) {
        this.tgl3 = tgl3;
    }

    public String getTgl4() {
        return tgl4;
    }

    public void setTgl4(String tgl4) {
        this.tgl4 = tgl4;
    }

    public String getTgl5() {
        return tgl5;
    }

    public void setTgl5(String tgl5) {
        this.tgl5 = tgl5;
    }

    public String getTgl6() {
        return tgl6;
    }

    public void setTgl6(String tgl6) {
        this.tgl6 = tgl6;
    }

    public String getTgl7() {
        return tgl7;
    }

    public void setTgl7(String tgl7) {
        this.tgl7 = tgl7;
    }

    public String getTotalLembur() {
        return totalLembur;
    }

    public void setTotalLembur(String totalLembur) {
        this.totalLembur = totalLembur;
    }

    public String getLembur() {
        return lembur;
    }

    public void setLembur(String lembur) {
        this.lembur = lembur;
    }

    public String getTerlambat() {
        return terlambat;
    }

    public void setTerlambat(String terlambat) {
        this.terlambat = terlambat;
    }

    public String getIjinKeluar() {
        return ijinKeluar;
    }

    public void setIjinKeluar(String ijinKeluar) {
        this.ijinKeluar = ijinKeluar;
    }

    public String getTunjHadir() {
        return tunjHadir;
    }

    public void setTunjHadir(String tunjHadir) {
        this.tunjHadir = tunjHadir;
    }

    public String getTransport() {
        return transport;
    }

    public void setTransport(String transport) {
        this.transport = transport;
    }

    public String getBpjs() {
        return bpjs;
    }

    public void setBpjs(String bpjs) {
        this.bpjs = bpjs;
    }

    public String getBpjs_tk() {
        return bpjs_tk;
    }

    public void setBpjs_tk(String bpjs_tk) {
        this.bpjs_tk = bpjs_tk;
    }

    public String getBonusTBT() {
        return bonusTBT;
    }

    public void setBonusTBT(String bonusTBT) {
        this.bonusTBT = bonusTBT;
    }

    public String getHariKerja() {
        return hariKerja;
    }

    public void setHariKerja(String hariKerja) {
        this.hariKerja = hariKerja;
    }

    public String getGajiHarian() {
        return gajiHarian;
    }

    public void setGajiHarian(String gajiHarian) {
        this.gajiHarian = gajiHarian;
    }

    public String getGajiBorong() {
        return gajiBorong;
    }

    public void setGajiBorong(String gajiBorong) {
        this.gajiBorong = gajiBorong;
    }

    public String getBonus1() {
        return bonus1;
    }

    public void setBonus1(String bonus1) {
        this.bonus1 = bonus1;
    }

    public String getBonus2() {
        return bonus2;
    }

    public void setBonus2(String bonus2) {
        this.bonus2 = bonus2;
    }

    public String getBonus_pencapaian_produksi() {
        return bonus_pencapaian_produksi;
    }

    public void setBonus_pencapaian_produksi(String bonus_pencapaian_produksi) {
        this.bonus_pencapaian_produksi = bonus_pencapaian_produksi;
    }

    public String getPiutang_karyawan() {
        return piutang_karyawan;
    }

    public void setPiutang_karyawan(String piutang_karyawan) {
        this.piutang_karyawan = piutang_karyawan;
    }

    public String getTotalGaji() {
        return totalGaji;
    }

    public void setTotalGaji(String totalGaji) {
        this.totalGaji = totalGaji;
    }

    public String getGrup() {
        return grup;
    }

    public void setGrup(String grup) {
        this.grup = grup;
    }

    public String getKet() {
        return ket;
    }

    public void setKet(String ket) {
        this.ket = ket;
    }
}
