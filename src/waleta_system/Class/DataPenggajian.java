package waleta_system.Class;

public class DataPenggajian {
    
    private String id;
    private String nama;
    private String grup;
    private float bobot_lp;
    private int jumlah_keping;
    private int jumlah_gram;
    private int hari_kerja;
    private int gajiBorong;
    private int gajiHarian;
    private int tunjHadir;
    private int bonus1;
    private int bonus2;
    private int bonus_pencapaian_produksi;
    private int bonus_tbt;
    private int lembur;
    private int piutang;
    private int pot_terlambat;
    private int pot_ijin_keluar;
    private int pot_transport;
    private int pot_bpjs;
    private int pot_bpjs_tk;
    private int gaji;

    public DataPenggajian(String id, String nama, String grup, float bobot_lp, int jumlah_keping, int jumlah_gram, int hari_kerja, int gajiBorong, int gajiHarian, int tunjHadir, int bonus1, int bonus2, int bonus_pencapaian_produksi, int bonus_tbt, int lembur, int piutang, int pot_terlambat, int pot_ijin_keluar, int pot_transport, int pot_bpjs, int pot_bpjs_tk, int gaji) {
        this.id = id;
        this.nama = nama;
        this.grup = grup;
        this.bobot_lp = bobot_lp;
        this.jumlah_keping = jumlah_keping;
        this.jumlah_gram = jumlah_gram;
        this.hari_kerja = hari_kerja;
        this.gajiBorong = gajiBorong;
        this.gajiHarian = gajiHarian;
        this.tunjHadir = tunjHadir;
        this.bonus1 = bonus1;
        this.bonus2 = bonus2;
        this.bonus_pencapaian_produksi = bonus_pencapaian_produksi;
        this.bonus_tbt = bonus_tbt;
        this.lembur = lembur;
        this.piutang = piutang;
        this.pot_terlambat = pot_terlambat;
        this.pot_ijin_keluar = pot_ijin_keluar;
        this.pot_transport = pot_transport;
        this.pot_bpjs = pot_bpjs;
        this.pot_bpjs_tk = pot_bpjs_tk;
        this.gaji = gaji;
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

    public String getGrup() {
        return grup;
    }

    public void setGrup(String grup) {
        this.grup = grup;
    }

    public float getBobot_lp() {
        return bobot_lp;
    }

    public void setBobot_lp(float bobot_lp) {
        this.bobot_lp = bobot_lp;
    }

    public int getJumlah_keping() {
        return jumlah_keping;
    }

    public void setJumlah_keping(int jumlah_keping) {
        this.jumlah_keping = jumlah_keping;
    }

    public int getJumlah_gram() {
        return jumlah_gram;
    }

    public void setJumlah_gram(int jumlah_gram) {
        this.jumlah_gram = jumlah_gram;
    }

    public int getHari_kerja() {
        return hari_kerja;
    }

    public void setHari_kerja(int hari_kerja) {
        this.hari_kerja = hari_kerja;
    }

    public int getGajiBorong() {
        return gajiBorong;
    }

    public void setGajiBorong(int gajiBorong) {
        this.gajiBorong = gajiBorong;
    }

    public int getGajiHarian() {
        return gajiHarian;
    }

    public void setGajiHarian(int gajiHarian) {
        this.gajiHarian = gajiHarian;
    }

    public int getTunjHadir() {
        return tunjHadir;
    }

    public void setTunjHadir(int tunjHadir) {
        this.tunjHadir = tunjHadir;
    }

    public int getBonus1() {
        return bonus1;
    }

    public void setBonus1(int bonus1) {
        this.bonus1 = bonus1;
    }

    public int getBonus2() {
        return bonus2;
    }

    public void setBonus2(int bonus2) {
        this.bonus2 = bonus2;
    }

    public int getBonus_pencapaian_produksi() {
        return bonus_pencapaian_produksi;
    }

    public void setBonus_pencapaian_produksi(int bonus_pencapaian_produksi) {
        this.bonus_pencapaian_produksi = bonus_pencapaian_produksi;
    }

    public int getBonus_tbt() {
        return bonus_tbt;
    }

    public void setBonus_tbt(int bonus_tbt) {
        this.bonus_tbt = bonus_tbt;
    }

    public int getLembur() {
        return lembur;
    }

    public void setLembur(int lembur) {
        this.lembur = lembur;
    }

    public int getPiutang() {
        return piutang;
    }

    public void setPiutang(int piutang) {
        this.piutang = piutang;
    }

    public int getPot_terlambat() {
        return pot_terlambat;
    }

    public void setPot_terlambat(int pot_terlambat) {
        this.pot_terlambat = pot_terlambat;
    }

    public int getPot_ijin_keluar() {
        return pot_ijin_keluar;
    }

    public void setPot_ijin_keluar(int pot_ijin_keluar) {
        this.pot_ijin_keluar = pot_ijin_keluar;
    }

    public int getPot_transport() {
        return pot_transport;
    }

    public void setPot_transport(int pot_transport) {
        this.pot_transport = pot_transport;
    }

    public int getPot_bpjs() {
        return pot_bpjs;
    }

    public void setPot_bpjs(int pot_bpjs) {
        this.pot_bpjs = pot_bpjs;
    }

    public int getPot_bpjs_tk() {
        return pot_bpjs_tk;
    }

    public void setPot_bpjs_tk(int pot_bpjs_tk) {
        this.pot_bpjs_tk = pot_bpjs_tk;
    }

    public int getGaji() {
        return gaji;
    }

    public void setGaji(int gaji) {
        this.gaji = gaji;
    }
    
    
}
