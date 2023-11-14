package waleta_system.Class;

import java.util.Date;

public class DataLPYangDikerjakan {
    private String noLP;
    private Date tglMasuk;
    private Date tglMulai;
    private Date tglSelesai;
    private Date tglSetor;
    private double bonus;

    public DataLPYangDikerjakan(String noLP, Date tglMasuk, Date tglMulai, Date tglSelesai, Date tglSetor, double bonus) {
        this.noLP = noLP;
        this.tglMasuk = tglMasuk;
        this.tglMulai = tglMulai;
        this.tglSelesai = tglSelesai;
        this.tglSetor = tglSetor;
        this.bonus = bonus;
    }

    public String getNoLP() {
        return noLP;
    }

    public void setNoLP(String noLP) {
        this.noLP = noLP;
    }

    public Date getTglMasuk() {
        return tglMasuk;
    }

    public void setTglMasuk(Date tglMasuk) {
        this.tglMasuk = tglMasuk;
    }

    public Date getTglMulai() {
        return tglMulai;
    }

    public void setTglMulai(Date tglMulai) {
        this.tglMulai = tglMulai;
    }

    public Date getTglSelesai() {
        return tglSelesai;
    }

    public void setTglSelesai(Date tglSelesai) {
        this.tglSelesai = tglSelesai;
    }

    public Date getTglSetor() {
        return tglSetor;
    }

    public void setTglSetor(Date tglSetor) {
        this.tglSetor = tglSetor;
    }

    public double getBonus() {
        return bonus;
    }

    public void setBonus(double bonus) {
        this.bonus = bonus;
    }
}
