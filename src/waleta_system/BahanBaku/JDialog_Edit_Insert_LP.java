package waleta_system.BahanBaku;

import java.awt.HeadlessException;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import waleta_system.MainForm;
import waleta_system.Class.Utility;

public class JDialog_Edit_Insert_LP extends javax.swing.JDialog {

    String sql = null;
    ResultSet rs;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DecimalFormat decimalFormat = new DecimalFormat();
    String no_lp, status = "", ruangan_awal = "";
    int berat_basah = 0, berat_kering = 0;
    int jumlah_keping = 0, keping_upah = 0;
    float kadar_air_bahan_baku = 0;
    float berat_per_keping_grading = 0, berat_per_keping_lp = 0;
    ArrayList<String> list_kode_sub = new ArrayList<>();

    public JDialog_Edit_Insert_LP(java.awt.Frame parent, boolean modal, String no_lp, String status) {
        super(parent, modal);
        initComponents();
        label_kode_pecah_lp.setVisible(false);
        this.no_lp = no_lp;
        this.status = status;
        try {
            sql = "SELECT `bulu_upah` FROM `tb_tarif_cabut` WHERE `status` = 'AKTIF'";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_jenisBulu.addItem(rs.getString("bulu_upah"));
            }

            Utility.db_sub.connect();
            sql = "SELECT `kode_sub` FROM `tb_sub_waleta` WHERE `tanggal_tutup` IS NULL";
            rs = Utility.db_sub.getStatement().executeQuery(sql);
            while (rs.next()) {
                list_kode_sub.add(rs.getString("kode_sub"));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JDialog_Edit_Insert_LP.class.getName()).log(Level.SEVERE, null, ex);
        }
        if ("insert".equals(status)) {
            label_NO_LP.setVisible(false);
            button_update_LP.setVisible(false);
            ComboBox_ruangan.removeAllItems();
            ComboBox_ruangan.addItem("A");
            ComboBox_ruangan.addItem("B");
            ComboBox_ruangan.addItem("C");
            ComboBox_ruangan.addItem("D");
            ComboBox_ruangan.addItem("E");
            ComboBox_ruangan.addItem("CABUTO");
        } else if ("sub".equals(status)) {
            label_NO_LP.setVisible(false);
            button_update_LP.setVisible(false);
            ComboBox_ruangan.removeAllItems();
            for (String kode_sub : list_kode_sub) {
                ComboBox_ruangan.addItem(kode_sub);
            }
        } else if ("edit".equals(status)) {
            button_insert_LP.setVisible(false);
            label_title.setText("Edit Laporan Produksi");
            label_NO_LP.setVisible(true);
            label_NO_LP.setText(no_lp);
            getDataEdit(no_lp);//get data setelah isi combobox
        }
    }

    public String no_LP_Baru(String status) {
        int LastNumber = 0;
        String kode = "";
        try {
            if (status.equals("insert")) {
                kode = "WL-";
                String get_lastNumber = "SELECT MAX(RIGHT(`no_laporan_produksi`, 5)) AS 'last' "
                        + "FROM `tb_laporan_produksi` "
                        + "WHERE YEAR(`tanggal_lp`) = '" + new SimpleDateFormat("yyyy").format(Date_LP.getDate()) + "' "
                        + "AND `no_laporan_produksi` LIKE 'WL-%'";
                ResultSet result_lastNumber = Utility.db.getStatement().executeQuery(get_lastNumber);
                if (result_lastNumber.next()) {
                    LastNumber = result_lastNumber.getInt("last") + 1;
                }
            } else if (status.equals("sub")) {
                if (Integer.valueOf(new SimpleDateFormat("yyyy").format(Date_LP.getDate())) > 2023) {
                    kode = "WL-";
                    String get_lastNumber = "SELECT MAX(RIGHT(`no_laporan_produksi`, 5)) AS 'last' "
                            + "FROM `tb_laporan_produksi` "
                            + "WHERE YEAR(`tanggal_lp`) = '" + new SimpleDateFormat("yyyy").format(Date_LP.getDate()) + "' "
                            + "AND `no_laporan_produksi` LIKE 'WL-%'";
                    ResultSet result_lastNumber = Utility.db.getStatement().executeQuery(get_lastNumber);
                    if (result_lastNumber.next()) {
                        LastNumber = result_lastNumber.getInt("last") + 1;
                    }
                } else {
                    kode = "WL." + ComboBox_ruangan.getSelectedItem() + "-";
                    String get_lastNumber = "SELECT MAX(RIGHT(`no_laporan_produksi`, 5)) AS 'last' "
                            + "FROM `tb_laporan_produksi` "
                            + "WHERE YEAR(`tanggal_lp`) = '" + new SimpleDateFormat("yyyy").format(Date_LP.getDate()) + "' "
                            + "AND `no_laporan_produksi` LIKE '" + kode + "%'";
                    ResultSet result_lastNumber = Utility.db.getStatement().executeQuery(get_lastNumber);
                    if (result_lastNumber.next()) {
                        LastNumber = result_lastNumber.getInt("last") + 1;
                    }
                }
            }
        } catch (SQLException | NullPointerException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Laporan_Produksi.class.getName()).log(Level.SEVERE, null, ex);
        }
        String no_LP = kode + new SimpleDateFormat("yyMM").format(Date_LP.getDate()) + String.format("%05d", LastNumber);
        return no_LP;
    }

    public void getDataEdit(String no_lp) {
        try {
            sql = "SELECT `tanggal_lp`, `no_laporan_produksi`, `tb_laporan_produksi`.`no_kartu_waleta`, `kode_grade`, `jenis_bulu_lp`, `ruangan`, `memo_lp`, `tb_bahan_baku_masuk`.`kadar_air_bahan_baku`, `jumlah_keping`, `keping_upah`, `berat_basah`, `berat_kering`, "
                    + "`kaki_besar_lp`, "
                    + "`kaki_kecil_lp`, "
                    + "`hilang_kaki_lp`, "
                    + "`ada_susur_lp`, "
                    + "`ada_susur_besar_lp`, "
                    + "`tanpa_susur_lp`, "
                    + "`utuh_lp`, "
                    + "`hilang_ujung_lp`, "
                    + "`pecah_1_lp`, "
                    + "`pecah_2`, "
                    + "`jumlah_sobek`, "
                    + "`sobek_lepas`, "
                    + "`jumlah_gumpil`, "
                    + "`grup_lp`, "
                    + "`kode_pecah_lp`, "
                    + "`rontokan_gbm` "
                    + "FROM `tb_laporan_produksi` "
                    + "LEFT JOIN `tb_bahan_baku_masuk` ON `tb_laporan_produksi`.`no_kartu_waleta` = `tb_bahan_baku_masuk`.`no_kartu_waleta`"
                    + "WHERE  `no_laporan_produksi` = '" + no_lp + "' ";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                Date_LP.setDate(rs.getDate("tanggal_lp"));
                Label_no_kartu_LP.setText(rs.getString("no_kartu_waleta"));
                Label_kode_grade_lp.setText(rs.getString("kode_grade"));
                ComboBox_jenisBulu.setSelectedItem(rs.getString("jenis_bulu_lp"));

                ComboBox_ruangan.removeAllItems();
                ComboBox_ruangan.addItem("A");
                ComboBox_ruangan.addItem("B");
                ComboBox_ruangan.addItem("C");
                ComboBox_ruangan.addItem("D");
                ComboBox_ruangan.addItem("E");
                if (rs.getString("ruangan").equals("CABUTO")) {
                    ruangan_awal = "CABUTO";
                    ComboBox_ruangan.addItem("CABUTO");
                } else if (list_kode_sub.indexOf(rs.getString("ruangan")) > -1) {//ruangan sub
                    ruangan_awal = "SUB";
                    for (String kode_sub : list_kode_sub) {
                        ComboBox_ruangan.addItem(kode_sub);
                    }
                    ComboBox_ruangan.removeItem("SUB00");
                } else {
                    ruangan_awal = "WALETA";
                    for (String kode_sub : list_kode_sub) {
                        ComboBox_ruangan.addItem(kode_sub);
                    }
                    ComboBox_ruangan.removeItem("SUB00");
                }

                ComboBox_ruangan.setSelectedItem(rs.getString("ruangan"));
                txt_jumlah_keping_lp.setText(rs.getString("jumlah_keping"));
                txt_jumlah_keping_upah.setText(rs.getString("keping_upah"));
                txt_berat_basah_lp.setText(rs.getString("berat_basah"));
                label_berat_kering_lp.setText(rs.getString("berat_kering"));
                txt_memo.setText(rs.getString("memo_lp"));
                label_kadar_air_lp.setText(rs.getString("kadar_air_bahan_baku") + "%");
                txt_kaki_besar_lp.setText(rs.getString("kaki_besar_lp"));
                txt_kaki_kecil_lp.setText(rs.getString("kaki_kecil_lp"));
                txt_hilang_kaki_lp.setText(rs.getString("hilang_kaki_lp"));
                txt_ada_susur_lp.setText(rs.getString("ada_susur_lp"));
                txt_ada_susur_besar_lp.setText(rs.getString("ada_susur_besar_lp"));
                txt_tanpa_susur_lp.setText(rs.getString("tanpa_susur_lp"));
                txt_utuh_lp.setText(rs.getString("utuh_lp"));
                txt_hilang_ujung_lp.setText(rs.getString("hilang_ujung_lp"));
                txt_pecah_1_lp.setText(rs.getString("pecah_1_lp"));
                txt_pecah_2_lp.setText(rs.getString("pecah_2"));
                txt_sobek_lp.setText(rs.getString("jumlah_sobek"));
                txt_sobek_lepas_lp.setText(rs.getString("sobek_lepas"));
                txt_gumpil_lp.setText(rs.getString("jumlah_gumpil"));
                txt_grup_lp.setText(rs.getString("grup_lp"));
                if (rs.getString("kode_pecah_lp") != null && !rs.getString("kode_pecah_lp").equals("")) {
                    label_kode_pecah_lp.setText(rs.getString("kode_pecah_lp"));
                    label_kode_pecah_lp.setVisible(true);
                }
                txt_rontokan_gbm.setText(rs.getString("rontokan_gbm"));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JDialog_Edit_Insert_LP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void countBK() throws Exception {
        String query = "SELECT `kadar_air_bahan_baku` FROM `tb_bahan_baku_masuk` WHERE `no_kartu_waleta` = '" + Label_no_kartu_LP.getText() + "'";
        ResultSet rs2 = Utility.db.getStatement().executeQuery(query);
        if (rs2.next()) {
            kadar_air_bahan_baku = rs2.getFloat("kadar_air_bahan_baku");
            label_kadar_air_lp.setText(kadar_air_bahan_baku + "%");
        }

        if (txt_berat_basah_lp.getText() != null && !txt_berat_basah_lp.getText().equals("")) {
            berat_basah = Integer.valueOf(txt_berat_basah_lp.getText());
        }

        if (txt_jumlah_keping_lp.getText() != null && !txt_jumlah_keping_lp.getText().equals("")) {
            jumlah_keping = Integer.valueOf(txt_jumlah_keping_lp.getText());
        }

        berat_kering = (int) (berat_basah * (1 - (kadar_air_bahan_baku / 100)));
        berat_per_keping_lp = 0;
        if (jumlah_keping > 0) {
            berat_per_keping_lp = (float) berat_basah / (float) jumlah_keping;
        }
        label_berat_kering_lp.setText(String.valueOf(berat_kering));

        sql = "SELECT (`total_berat` / `jumlah_keping`) AS 'berat_kpg' FROM `tb_grading_bahan_baku` "
                + "WHERE `no_kartu_waleta` = '" + Label_no_kartu_LP.getText() + "' AND `kode_grade` = '" + Label_kode_grade_lp.getText() + "'";
        rs = Utility.db.getStatement().executeQuery(sql);
        if (rs.next()) {
            berat_per_keping_grading = rs.getFloat("berat_kpg");
            label_berat_per_kpg_grading.setText(decimalFormat.format(rs.getFloat("berat_kpg")));
        }

        if (jumlah_keping > 0) {
            keping_upah = jumlah_keping;
        } else {
            keping_upah = Math.round(Float.valueOf(berat_basah) / 8f);
        }
        txt_jumlah_keping_upah.setText(Integer.toString(keping_upah));

        decimalFormat.setMaximumFractionDigits(2);
        txt_berat_per_kpg.setText(decimalFormat.format(berat_per_keping_lp));
    }

    private void insert_lp_waleta() {
        try {
            Utility.db.getConnection().setAutoCommit(false);

            String no_laporan_produksi = no_LP_Baru(status);
            String insert_lp_local = "INSERT INTO `tb_laporan_produksi`(`no_laporan_produksi`, `no_kartu_waleta`, `tanggal_lp`, `ruangan`, `kode_grade`, `jenis_bulu_lp`, `memo_lp`, `berat_basah`, `berat_kering`, `jumlah_keping`, `keping_upah`, `kaki_besar_lp`, `kaki_kecil_lp`, `hilang_kaki_lp`, `ada_susur_lp`, `ada_susur_besar_lp`, `tanpa_susur_lp`, `utuh_lp`, `hilang_ujung_lp`, `pecah_1_lp`, `pecah_2`, `jumlah_sobek`, `sobek_lepas`, `jumlah_gumpil`, `grup_lp`, `kode_pecah_lp`, `rontokan_gbm`) "
                    + "VALUES ("
                    + "'" + no_laporan_produksi + "',"
                    + "'" + Label_no_kartu_LP.getText() + "',"
                    + "'" + dateFormat.format(Date_LP.getDate()) + "',"
                    + "'" + ComboBox_ruangan.getSelectedItem() + "',"
                    + "'" + Label_kode_grade_lp.getText() + "',"
                    + "'" + ComboBox_jenisBulu.getSelectedItem() + "',"
                    + "'" + txt_memo.getText() + "',"
                    + "'" + txt_berat_basah_lp.getText() + "',"
                    + "'" + berat_kering + "',"
                    + "'" + txt_jumlah_keping_lp.getText() + "',"
                    + "'" + txt_jumlah_keping_upah.getText() + "',"
                    + "'" + txt_kaki_besar_lp.getText() + "', "
                    + "'" + txt_kaki_kecil_lp.getText() + "', "
                    + "'" + txt_hilang_kaki_lp.getText() + "',"
                    + "'" + txt_ada_susur_lp.getText() + "', "
                    + "'" + txt_ada_susur_besar_lp.getText() + "', "
                    + "'" + txt_tanpa_susur_lp.getText() + "', "
                    + "'" + txt_utuh_lp.getText() + "',"
                    + "'" + txt_hilang_ujung_lp.getText() + "',"
                    + "'" + txt_pecah_1_lp.getText() + "',"
                    + "'" + txt_pecah_2_lp.getText() + "', "
                    + "'" + txt_sobek_lp.getText() + "',"
                    + "'" + txt_sobek_lepas_lp.getText() + "',"
                    + "'" + txt_gumpil_lp.getText() + "',"
                    + "'" + txt_grup_lp.getText() + "', "
                    + "'" + label_kode_pecah_lp.getText() + "',"
                    + "'" + txt_rontokan_gbm.getText() + "'"
                    + ")";
            if ((Utility.db.getStatement().executeUpdate(insert_lp_local)) == 1) {
                JOptionPane.showMessageDialog(this, "Input LP berhasil");
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Input LP failed!");
            }
        } catch (Exception e) {
            try {
                Utility.db.getConnection().rollback();
            } catch (SQLException ex) {
                Logger.getLogger(JDialog_Edit_Insert_LP.class.getName()).log(Level.SEVERE, null, ex);
            }
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_Laporan_Produksi.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            try {
                Utility.db.getConnection().setAutoCommit(true);
            } catch (SQLException ex) {
                Logger.getLogger(JDialog_Edit_Insert_LP.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void insert_lp_sub() {
        try {
            Utility.db_sub.connect();
            Utility.db.getConnection().setAutoCommit(false);
            Utility.db_sub.getConnection().setAutoCommit(false);

            String upah_per_gram = "";
            String get_upah_per_gram = "SELECT `tarif_sub` FROM `tb_tarif_cabut` WHERE `bulu_upah` = '" + ComboBox_jenisBulu.getSelectedItem().toString() + "'";
            ResultSet result_upah_per_gram = Utility.db.getStatement().executeQuery(get_upah_per_gram);
            if (result_upah_per_gram.next()) {
                upah_per_gram = result_upah_per_gram.getString("tarif_sub");
            }

            String no_laporan_produksi = no_LP_Baru(status);
            String insert_lp_online = "INSERT INTO `tb_laporan_produksi`(`no_laporan_produksi`, `no_kartu_waleta`, `tanggal_lp`, `ruangan`, `kode_grade`, `jenis_bulu_lp`, `memo_lp`, `berat_basah`, `berat_kering`, `jumlah_keping`, `keping_upah`, `kaki_besar_lp`, `kaki_kecil_lp`, `hilang_kaki_lp`, `ada_susur_lp`, `ada_susur_besar_lp`, `tanpa_susur_lp`, `utuh_lp`, `hilang_ujung_lp`, `pecah_1_lp`, `pecah_2`, `jumlah_sobek`, `sobek_lepas`, `jumlah_gumpil`, `grup_lp`, `upah_per_gram`) "
                    + "VALUES ("
                    + "'" + no_laporan_produksi + "',"
                    + "'" + Label_no_kartu_LP.getText() + "',"
                    + "'" + dateFormat.format(Date_LP.getDate()) + "',"
                    + "'" + ComboBox_ruangan.getSelectedItem() + "',"
                    + "'" + Label_kode_grade_lp.getText() + "',"
                    + "'" + ComboBox_jenisBulu.getSelectedItem() + "',"
                    + "'" + txt_memo.getText() + "',"
                    + "'" + txt_berat_basah_lp.getText() + "',"
                    + "'" + berat_kering + "',"
                    + "'" + txt_jumlah_keping_lp.getText() + "',"
                    + "'" + txt_jumlah_keping_upah.getText() + "',"
                    + "'" + txt_kaki_besar_lp.getText() + "', "
                    + "'" + txt_kaki_kecil_lp.getText() + "', "
                    + "'" + txt_hilang_kaki_lp.getText() + "',"
                    + "'" + txt_ada_susur_lp.getText() + "', "
                    + "'" + txt_ada_susur_besar_lp.getText() + "', "
                    + "'" + txt_tanpa_susur_lp.getText() + "', "
                    + "'" + txt_utuh_lp.getText() + "',"
                    + "'" + txt_hilang_ujung_lp.getText() + "',"
                    + "'" + txt_pecah_1_lp.getText() + "',"
                    + "'" + txt_pecah_2_lp.getText() + "', "
                    + "'" + txt_sobek_lp.getText() + "',"
                    + "'" + txt_sobek_lepas_lp.getText() + "',"
                    + "'" + txt_gumpil_lp.getText() + "',"
                    + "'" + txt_grup_lp.getText() + "', "
                    + "'" + upah_per_gram + "')";

            String insert_lp_local = "INSERT INTO `tb_laporan_produksi`(`no_laporan_produksi`, `no_kartu_waleta`, `tanggal_lp`, `ruangan`, `kode_grade`, `jenis_bulu_lp`, `memo_lp`, `berat_basah`, `berat_kering`, `jumlah_keping`, `keping_upah`, `kaki_besar_lp`, `kaki_kecil_lp`, `hilang_kaki_lp`, `ada_susur_lp`, `ada_susur_besar_lp`, `tanpa_susur_lp`, `utuh_lp`, `hilang_ujung_lp`, `pecah_1_lp`, `pecah_2`, `jumlah_sobek`, `sobek_lepas`, `jumlah_gumpil`, `grup_lp`, `kode_pecah_lp`) "
                    + "VALUES ("
                    + "'" + no_laporan_produksi + "',"
                    + "'" + Label_no_kartu_LP.getText() + "',"
                    + "'" + dateFormat.format(Date_LP.getDate()) + "',"
                    + "'" + ComboBox_ruangan.getSelectedItem() + "',"
                    + "'" + Label_kode_grade_lp.getText() + "',"
                    + "'" + ComboBox_jenisBulu.getSelectedItem() + "',"
                    + "'" + txt_memo.getText() + "',"
                    + "'" + txt_berat_basah_lp.getText() + "',"
                    + "'" + berat_kering + "',"
                    + "'" + txt_jumlah_keping_lp.getText() + "',"
                    + "'" + txt_jumlah_keping_upah.getText() + "',"
                    + "'" + txt_kaki_besar_lp.getText() + "', "
                    + "'" + txt_kaki_kecil_lp.getText() + "', "
                    + "'" + txt_hilang_kaki_lp.getText() + "',"
                    + "'" + txt_ada_susur_lp.getText() + "', "
                    + "'" + txt_ada_susur_besar_lp.getText() + "', "
                    + "'" + txt_tanpa_susur_lp.getText() + "', "
                    + "'" + txt_utuh_lp.getText() + "',"
                    + "'" + txt_hilang_ujung_lp.getText() + "',"
                    + "'" + txt_pecah_1_lp.getText() + "',"
                    + "'" + txt_pecah_2_lp.getText() + "', "
                    + "'" + txt_sobek_lp.getText() + "',"
                    + "'" + txt_sobek_lepas_lp.getText() + "',"
                    + "'" + txt_gumpil_lp.getText() + "',"
                    + "'" + txt_grup_lp.getText() + "', "
                    + "'" + label_kode_pecah_lp.getText() + "')";
            Utility.db_sub.getStatement().executeUpdate(insert_lp_online);
            if (!ComboBox_ruangan.getSelectedItem().toString().equals("SUB00")) {
                if ((Utility.db.getStatement().executeUpdate(insert_lp_local)) == 1) {
                    JOptionPane.showMessageDialog(this, "Input LP berhasil");
                } else {
                    JOptionPane.showMessageDialog(this, "Input LP failed!");
                }
            }
            Utility.db.getConnection().commit();
            Utility.db_sub.getConnection().commit();
            JOptionPane.showMessageDialog(this, "Input LP berhasil !");
            this.dispose();
        } catch (Exception e) {
            try {
                Utility.db.getConnection().rollback();
                Utility.db_sub.getConnection().rollback();
            } catch (SQLException ex) {
                Logger.getLogger(JDialog_Edit_Insert_LP.class.getName()).log(Level.SEVERE, null, ex);
            }
            JOptionPane.showMessageDialog(this, "FAILED : " + e);
            Logger.getLogger(JPanel_Laporan_Produksi.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            try {
                Utility.db.getConnection().setAutoCommit(true);
                Utility.db_sub.getConnection().setAutoCommit(true);
            } catch (SQLException ex) {
                Logger.getLogger(JDialog_Edit_Insert_LP.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void insert_lp_cabuto() {
        try {
            Utility.db_cabuto.connect();
            Utility.db.getConnection().setAutoCommit(false);
            Utility.db_cabuto.getConnection().setAutoCommit(false);

            int harga_gram_cabuto = 0;
            String Qry = "SELECT `harga_gram_cabuto` FROM `tb_grade_bahan_baku` "
                    + "WHERE `kode_grade` = '" + Label_kode_grade_lp.getText() + "'";
            ResultSet result = Utility.db.getStatement().executeQuery(Qry);
            if (result.next()) {
                harga_gram_cabuto = result.getInt("harga_gram_cabuto");
            }
            int harga_baku_lp = berat_basah * harga_gram_cabuto;
//            if (harga_baku_lp == 0) {
//                throw new Exception("Harga Baku LP cabuto tidak boleh 0!\nsilahkan masukkan harga baku pada master grade baku!");
//            }

            String no_laporan_produksi = no_LP_Baru(status);
            String jenis_bulu = ComboBox_jenisBulu.getSelectedItem().toString();
            if (jenis_bulu.length() < 2) {
                jenis_bulu = "-";
            } else if (!jenis_bulu.substring(0, 2).equals("BR") && !jenis_bulu.substring(0, 2).equals("BS") && !jenis_bulu.substring(0, 2).equals("BB")) {
                jenis_bulu = "-";
            } else {
                jenis_bulu = ComboBox_jenisBulu.getSelectedItem().toString().substring(0, 2);
            }
            String insert_lp_cabuto = "INSERT INTO `tb_lp`(`no_laporan_produksi`, `no_kartu_waleta`, `tanggal_lp`, `ruangan`, `kode_grade`, `jenis_bulu_lp`, `memo_lp`, `berat_basah`, `berat_kering`, `jumlah_keping`, `keping_upah`, `harga_baku`) "
                    + "VALUES ('" + no_laporan_produksi + "',"
                    + "'" + Label_no_kartu_LP.getText() + "',"
                    + "'" + dateFormat.format(Date_LP.getDate()) + "',"
                    + "'" + ComboBox_ruangan.getSelectedItem() + "',"
                    + "'" + Label_kode_grade_lp.getText() + "',"
                    + "'" + jenis_bulu + "',"
                    + "'" + txt_memo.getText() + "',"
                    + "'" + txt_berat_basah_lp.getText() + "',"
                    + "'" + berat_kering + "',"
                    + "'" + txt_jumlah_keping_lp.getText() + "',"
                    + "'" + txt_jumlah_keping_upah.getText() + "',"
                    + "'" + harga_baku_lp + "')";

            String insert_lp_waleta = "INSERT INTO `tb_laporan_produksi`(`no_laporan_produksi`, `no_kartu_waleta`, `tanggal_lp`, `ruangan`, `kode_grade`, `jenis_bulu_lp`, `memo_lp`, `berat_basah`, `berat_kering`, `jumlah_keping`, `keping_upah`, `kaki_besar_lp`, `kaki_kecil_lp`, `hilang_kaki_lp`, `ada_susur_lp`, `ada_susur_besar_lp`, `tanpa_susur_lp`, `utuh_lp`, `hilang_ujung_lp`, `pecah_1_lp`, `pecah_2`, `jumlah_sobek`, `sobek_lepas`, `jumlah_gumpil`, `grup_lp`, `kode_pecah_lp`) "
                    + "VALUES ('" + no_laporan_produksi + "',"
                    + "'" + Label_no_kartu_LP.getText() + "',"
                    + "'" + dateFormat.format(Date_LP.getDate()) + "',"
                    + "'" + ComboBox_ruangan.getSelectedItem() + "',"
                    + "'" + Label_kode_grade_lp.getText() + "',"
                    + "'" + ComboBox_jenisBulu.getSelectedItem() + "',"
                    + "'" + txt_memo.getText() + "',"
                    + "'" + txt_berat_basah_lp.getText() + "',"
                    + "'" + berat_kering + "',"
                    + "'" + txt_jumlah_keping_lp.getText() + "',"
                    + "'" + txt_jumlah_keping_upah.getText() + "',"
                    + "'" + txt_kaki_besar_lp.getText() + "', "
                    + "'" + txt_kaki_kecil_lp.getText() + "', "
                    + "'" + txt_hilang_kaki_lp.getText() + "',"
                    + "'" + txt_ada_susur_lp.getText() + "', "
                    + "'" + txt_ada_susur_besar_lp.getText() + "', "
                    + "'" + txt_tanpa_susur_lp.getText() + "', "
                    + "'" + txt_utuh_lp.getText() + "',"
                    + "'" + txt_hilang_ujung_lp.getText() + "',"
                    + "'" + txt_pecah_1_lp.getText() + "',"
                    + "'" + txt_pecah_2_lp.getText() + "', "
                    + "'" + txt_sobek_lp.getText() + "',"
                    + "'" + txt_sobek_lepas_lp.getText() + "',"
                    + "'" + txt_gumpil_lp.getText() + "',"
                    + "'" + txt_grup_lp.getText() + "', "
                    + "'" + label_kode_pecah_lp.getText() + "')";

            Utility.db.getStatement().executeUpdate(insert_lp_waleta);
            Utility.db_cabuto.getStatement().executeUpdate(insert_lp_cabuto);
            Utility.db.getConnection().commit();
            Utility.db_cabuto.getConnection().commit();
            JOptionPane.showMessageDialog(this, "Input LP berhasil !");
            this.dispose();
        } catch (Exception e) {
            try {
                Utility.db.getConnection().rollback();
                Utility.db_cabuto.getConnection().rollback();
            } catch (SQLException ex) {
                Logger.getLogger(JDialog_Edit_Insert_LP.class.getName()).log(Level.SEVERE, null, ex);
            }
            JOptionPane.showMessageDialog(this, "ERROR : " + e);
            Logger.getLogger(JDialog_Edit_Insert_LP.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            try {
                Utility.db.getConnection().setAutoCommit(true);
                Utility.db_cabuto.getConnection().setAutoCommit(true);
            } catch (SQLException ex) {
                Logger.getLogger(JDialog_Edit_Insert_LP.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void edit_lp_waleta() throws Exception {
        String Query = "UPDATE `tb_laporan_produksi` SET "
                + "`no_kartu_waleta` = '" + Label_no_kartu_LP.getText() + "', "
                + "`tanggal_lp` = '" + dateFormat.format(Date_LP.getDate()) + "', "
                + "`ruangan` = '" + ComboBox_ruangan.getSelectedItem() + "', "
                + "`kode_grade` = '" + Label_kode_grade_lp.getText() + "', "
                + "`jenis_bulu_lp` = '" + ComboBox_jenisBulu.getSelectedItem() + "', "
                + "`memo_lp` = '" + txt_memo.getText() + "', "
                + "`berat_basah` = '" + txt_berat_basah_lp.getText() + "', "
                + "`berat_kering` = '" + berat_kering + "', "
                + "`jumlah_keping` = '" + txt_jumlah_keping_lp.getText() + "', "
                + "`keping_upah` = '" + txt_jumlah_keping_upah.getText() + "', "
                + "`kaki_besar_lp` = '" + txt_kaki_besar_lp.getText() + "', "
                + "`kaki_kecil_lp` = '" + txt_kaki_kecil_lp.getText() + "', "
                + "`hilang_kaki_lp` = '" + txt_hilang_kaki_lp.getText() + "', "
                + "`ada_susur_lp` = '" + txt_ada_susur_lp.getText() + "', "
                + "`ada_susur_besar_lp` = '" + txt_ada_susur_besar_lp.getText() + "', "
                + "`tanpa_susur_lp` = '" + txt_tanpa_susur_lp.getText() + "', "
                + "`utuh_lp` = '" + txt_utuh_lp.getText() + "', "
                + "`hilang_ujung_lp` = '" + txt_hilang_ujung_lp.getText() + "', "
                + "`pecah_1_lp` = '" + txt_pecah_1_lp.getText() + "', "
                + "`pecah_2` = '" + txt_pecah_2_lp.getText() + "', "
                + "`jumlah_sobek` = '" + txt_sobek_lp.getText() + "', "
                + "`sobek_lepas` = '" + txt_sobek_lepas_lp.getText() + "', "
                + "`jumlah_gumpil` = '" + txt_gumpil_lp.getText() + "', "
                + "`grup_lp` = '" + txt_grup_lp.getText() + "', "
                + "`kode_pecah_lp` = '" + label_kode_pecah_lp.getText() + "', "
                + "`rontokan_gbm` = '" + txt_rontokan_gbm.getText() + "' "
                + "WHERE `tb_laporan_produksi`.`no_laporan_produksi` = '" + no_lp + "'";
        Utility.db.getStatement().executeUpdate(Query);
    }

    private void edit_lp_sub() throws Exception {
        String upah_per_gram = "";
        String get_upah_per_gram = "SELECT `tarif_sub` FROM `tb_tarif_cabut` WHERE `bulu_upah` = '" + ComboBox_jenisBulu.getSelectedItem().toString() + "'";
        ResultSet result_upah_per_gram = Utility.db.getStatement().executeQuery(get_upah_per_gram);
        if (result_upah_per_gram.next()) {
            upah_per_gram = "`upah_per_gram` = '" + result_upah_per_gram.getString("tarif_sub") + "' ";
        }
        String Query = "UPDATE `tb_laporan_produksi` SET "
                + "`no_kartu_waleta` = '" + Label_no_kartu_LP.getText() + "', "
                + "`tanggal_lp` = '" + dateFormat.format(Date_LP.getDate()) + "', "
                + "`ruangan` = '" + ComboBox_ruangan.getSelectedItem().toString() + "', "
                + "`kode_grade` = '" + Label_kode_grade_lp.getText() + "', "
                + "`jenis_bulu_lp` = '" + ComboBox_jenisBulu.getSelectedItem() + "', "
                + "`memo_lp` = '" + txt_memo.getText() + "', "
                + "`berat_basah` = '" + txt_berat_basah_lp.getText() + "', "
                + "`berat_kering` = '" + berat_kering + "', "
                + "`jumlah_keping` = '" + txt_jumlah_keping_lp.getText() + "', "
                + "`keping_upah` = '" + txt_jumlah_keping_upah.getText() + "', "
                + "`kaki_besar_lp` = '" + txt_kaki_besar_lp.getText() + "', "
                + "`kaki_kecil_lp` = '" + txt_kaki_kecil_lp.getText() + "', "
                + "`hilang_kaki_lp` = '" + txt_hilang_kaki_lp.getText() + "', "
                + "`ada_susur_lp` = '" + txt_ada_susur_lp.getText() + "', "
                + "`ada_susur_besar_lp` = '" + txt_ada_susur_besar_lp.getText() + "', "
                + "`tanpa_susur_lp` = '" + txt_tanpa_susur_lp.getText() + "', "
                + "`utuh_lp` = '" + txt_utuh_lp.getText() + "', "
                + "`hilang_ujung_lp` = '" + txt_hilang_ujung_lp.getText() + "', "
                + "`pecah_1_lp` = '" + txt_pecah_1_lp.getText() + "', "
                + "`pecah_2` = '" + txt_pecah_2_lp.getText() + "', "
                + "`jumlah_sobek` = '" + txt_sobek_lp.getText() + "', "
                + "`sobek_lepas` = '" + txt_sobek_lepas_lp.getText() + "', "
                + "`jumlah_gumpil` = '" + txt_gumpil_lp.getText() + "', "
                + "`grup_lp` = '" + txt_grup_lp.getText() + "', "
                + upah_per_gram
                + "WHERE `tb_laporan_produksi`.`no_laporan_produksi` = '" + no_lp + "'";
        Utility.db_sub.getStatement().executeUpdate(Query);
    }

    private void input_lp_sub() throws Exception {
        String upah_per_gram = "";
        String get_upah_per_gram = "SELECT `tarif_sub` FROM `tb_tarif_cabut` WHERE `bulu_upah` = '" + ComboBox_jenisBulu.getSelectedItem().toString() + "'";
        ResultSet result_upah_per_gram = Utility.db.getStatement().executeQuery(get_upah_per_gram);
        if (result_upah_per_gram.next()) {
            upah_per_gram = result_upah_per_gram.getString("tarif_sub");
        }
        String insert_lp_online = "INSERT INTO `tb_laporan_produksi`(`no_laporan_produksi`, `no_kartu_waleta`, `tanggal_lp`, `ruangan`, `kode_grade`, `jenis_bulu_lp`, `memo_lp`, `berat_basah`, `berat_kering`, `jumlah_keping`, `keping_upah`, `kaki_besar_lp`, `kaki_kecil_lp`, `hilang_kaki_lp`, `ada_susur_lp`, `ada_susur_besar_lp`, `tanpa_susur_lp`, `utuh_lp`, `hilang_ujung_lp`, `pecah_1_lp`, `pecah_2`, `jumlah_sobek`, `sobek_lepas`, `jumlah_gumpil`, `grup_lp`, `upah_per_gram`) "
                + "VALUES ("
                + "'" + no_lp + "',"
                + "'" + Label_no_kartu_LP.getText() + "',"
                + "'" + dateFormat.format(Date_LP.getDate()) + "',"
                + "'" + ComboBox_ruangan.getSelectedItem() + "',"
                + "'" + Label_kode_grade_lp.getText() + "',"
                + "'" + ComboBox_jenisBulu.getSelectedItem() + "',"
                + "'" + txt_memo.getText() + "',"
                + "'" + txt_berat_basah_lp.getText() + "',"
                + "'" + berat_kering + "',"
                + "'" + txt_jumlah_keping_lp.getText() + "',"
                + "'" + txt_jumlah_keping_upah.getText() + "',"
                + "'" + txt_kaki_besar_lp.getText() + "', "
                + "'" + txt_kaki_kecil_lp.getText() + "', "
                + "'" + txt_hilang_kaki_lp.getText() + "',"
                + "'" + txt_ada_susur_lp.getText() + "', "
                + "'" + txt_ada_susur_besar_lp.getText() + "', "
                + "'" + txt_tanpa_susur_lp.getText() + "', "
                + "'" + txt_utuh_lp.getText() + "',"
                + "'" + txt_hilang_ujung_lp.getText() + "',"
                + "'" + txt_pecah_1_lp.getText() + "',"
                + "'" + txt_pecah_2_lp.getText() + "', "
                + "'" + txt_sobek_lp.getText() + "',"
                + "'" + txt_sobek_lepas_lp.getText() + "',"
                + "'" + txt_gumpil_lp.getText() + "',"
                + "'" + txt_grup_lp.getText() + "', "
                + "'" + upah_per_gram + "')";
        Utility.db_sub.getStatement().executeUpdate(insert_lp_online);
    }

    private void delete_lp_sub() throws Exception {
        String Query = "DELETE FROM `tb_laporan_produksi` WHERE `no_laporan_produksi` = '" + no_lp + "'";
        Utility.db_sub.getStatement().executeUpdate(Query);
    }

    private void edit_lp_cabuto() throws Exception {
        int harga_gram_cabuto = 0;
        String Qry = "SELECT `harga_gram_cabuto` FROM `tb_grade_bahan_baku` "
                + "WHERE `kode_grade` = '" + Label_kode_grade_lp.getText() + "'";
        ResultSet result = Utility.db.getStatement().executeQuery(Qry);
        if (result.next()) {
            harga_gram_cabuto = result.getInt("harga_gram_cabuto");
        }
        int harga_baku_lp = berat_basah * harga_gram_cabuto;
//        if (harga_baku_lp == 0) {
//            throw new Exception("Harga Baku LP cabuto tidak boleh 0!\nsilahkan masukkan harga baku pada master grade baku!");
//        }
        String Query = "UPDATE `tb_lp` SET "
                + "`no_kartu_waleta` = '" + Label_no_kartu_LP.getText() + "', "
                + "`tanggal_lp` = '" + dateFormat.format(Date_LP.getDate()) + "', "
                + "`ruangan` = '" + ComboBox_ruangan.getSelectedItem() + "', "
                + "`kode_grade` = '" + Label_kode_grade_lp.getText() + "', "
                + "`jenis_bulu_lp` = '" + ComboBox_jenisBulu.getSelectedItem() + "', "
                + "`memo_lp` = '" + txt_memo.getText() + "', "
                + "`berat_basah` = '" + txt_berat_basah_lp.getText() + "', "
                + "`berat_kering` = '" + berat_kering + "', "
                + "`jumlah_keping` = '" + txt_jumlah_keping_lp.getText() + "', "
                + "`keping_upah` = '" + txt_jumlah_keping_upah.getText() + "', "
                + "`harga_baku` = '" + harga_baku_lp + "' "
                + "WHERE `no_laporan_produksi` = '" + no_lp + "'";
        Utility.db_cabuto.getStatement().executeUpdate(Query);
    }

    private void input_lp_cabuto() throws Exception {
        int harga_gram_cabuto = 0;
        String Qry = "SELECT `harga_gram_cabuto` FROM `tb_grade_bahan_baku` "
                + "WHERE `kode_grade` = '" + Label_kode_grade_lp.getText() + "'";
        ResultSet result = Utility.db.getStatement().executeQuery(Qry);
        if (result.next()) {
            harga_gram_cabuto = result.getInt("harga_gram_cabuto");
        }
        int harga_baku_lp = berat_basah * harga_gram_cabuto;
//            if (harga_baku_lp == 0) {
//                throw new Exception("Harga Baku LP cabuto tidak boleh 0!\nsilahkan masukkan harga baku pada master grade baku!");
//            }

        String insert_lp_cabuto = "INSERT INTO `tb_lp`(`no_laporan_produksi`, `no_kartu_waleta`, `tanggal_lp`, `ruangan`, `kode_grade`, `jenis_bulu_lp`, `memo_lp`, `berat_basah`, `berat_kering`, `jumlah_keping`, `keping_upah`, `harga_baku`) "
                + "VALUES ('" + no_lp + "',"
                + "'" + Label_no_kartu_LP.getText() + "',"
                + "'" + dateFormat.format(Date_LP.getDate()) + "',"
                + "'" + ComboBox_ruangan.getSelectedItem() + "',"
                + "'" + Label_kode_grade_lp.getText() + "',"
                + "'" + ComboBox_jenisBulu.getSelectedItem() + "',"
                + "'" + txt_memo.getText() + "',"
                + "'" + txt_berat_basah_lp.getText() + "',"
                + "'" + berat_kering + "',"
                + "'" + txt_jumlah_keping_lp.getText() + "',"
                + "'" + txt_jumlah_keping_upah.getText() + "',"
                + "'" + harga_baku_lp + "')";
        Utility.db_cabuto.getStatement().executeUpdate(insert_lp_cabuto);
    }

    private void delete_lp_cabuto() throws Exception {
        String Query = "DELETE FROM `tb_lp` WHERE `no_laporan_produksi` = '" + no_lp + "'";
        Utility.db_cabuto.getStatement().executeUpdate(Query);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel_operation_lp = new javax.swing.JPanel();
        label_title = new javax.swing.JLabel();
        label_NO_LP = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        label_kadar_air_lp = new javax.swing.JLabel();
        label_berat_basah_lp1 = new javax.swing.JLabel();
        label_kartu_waleta_LP = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        button_LP_select_kartu = new javax.swing.JButton();
        label_berat_kering = new javax.swing.JLabel();
        Label_kode_grade_lp = new javax.swing.JLabel();
        label_berat_kering_lp = new javax.swing.JLabel();
        txt_jumlah_keping_lp = new javax.swing.JTextField();
        label_jumlah_pecah_lp1 = new javax.swing.JLabel();
        ComboBox_ruangan = new javax.swing.JComboBox<>();
        Label_no_kartu_LP = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        label_jumlah_pecah_lp5 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        label_berat_per_kpg_grading = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        label_jumlah_kpg_lp = new javax.swing.JLabel();
        txt_berat_basah_lp = new javax.swing.JTextField();
        label_jumlah_keping_lp = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        label_berat_basah_lp = new javax.swing.JLabel();
        label_jumlah_keping_lp1 = new javax.swing.JLabel();
        label_kode_grade_lp = new javax.swing.JLabel();
        txt_jumlah_keping_upah = new javax.swing.JTextField();
        ComboBox_jenisBulu = new javax.swing.JComboBox<>();
        txt_berat_per_kpg = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        label_jumlah_pecah_lp6 = new javax.swing.JLabel();
        txt_memo = new javax.swing.JTextField();
        label_jumlah_pecah_lp2 = new javax.swing.JLabel();
        Date_LP = new com.toedter.calendar.JDateChooser();
        label_tgl_lp = new javax.swing.JLabel();
        button_update_LP = new javax.swing.JButton();
        button_insert_LP = new javax.swing.JButton();
        label_jumlah_pecah_lp7 = new javax.swing.JLabel();
        txt_gumpil_lp = new javax.swing.JTextField();
        label_jumlah_sobek_lp = new javax.swing.JLabel();
        label_jumlah_hilang_ujung_lp1 = new javax.swing.JLabel();
        label_jumlah_gumpil_lp = new javax.swing.JLabel();
        txt_ada_susur_lp = new javax.swing.JTextField();
        label_jumlah_sobek_lp1 = new javax.swing.JLabel();
        label_jumlah_hilang_kaki_lp1 = new javax.swing.JLabel();
        label_jumlah_gumpil_lp1 = new javax.swing.JLabel();
        label_jumlah_pecah_lp3 = new javax.swing.JLabel();
        txt_kaki_besar_lp = new javax.swing.JTextField();
        txt_tanpa_susur_lp = new javax.swing.JTextField();
        label_jumlah_hilang_kaki_lp2 = new javax.swing.JLabel();
        label_jumlah_pecah_lp4 = new javax.swing.JLabel();
        label_jumlah_hilang_ujung_lp = new javax.swing.JLabel();
        label_jumlah_hilang_kaki_lp = new javax.swing.JLabel();
        label_jumlah_pecah_lp = new javax.swing.JLabel();
        txt_kaki_kecil_lp = new javax.swing.JTextField();
        txt_pecah_1_lp = new javax.swing.JTextField();
        txt_hilang_ujung_lp = new javax.swing.JTextField();
        txt_sobek_lepas_lp = new javax.swing.JTextField();
        txt_pecah_2_lp = new javax.swing.JTextField();
        txt_sobek_lp = new javax.swing.JTextField();
        txt_hilang_kaki_lp = new javax.swing.JTextField();
        txt_utuh_lp = new javax.swing.JTextField();
        label_jumlah_gumpil_lp2 = new javax.swing.JLabel();
        txt_ada_susur_besar_lp = new javax.swing.JTextField();
        label_jumlah_pecah_lp8 = new javax.swing.JLabel();
        txt_rontokan_gbm = new javax.swing.JTextField();
        txt_grup_lp = new javax.swing.JTextField();
        button_pilih_pecah_lp = new javax.swing.JButton();
        label_kode_pecah_lp = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Insert Laporan Produksi");
        setResizable(false);

        jPanel_operation_lp.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_operation_lp.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel_operation_lp.setName("aah"); // NOI18N

        label_title.setBackground(new java.awt.Color(255, 255, 255));
        label_title.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_title.setText("Buat Laporan Produksi Baru");

        label_NO_LP.setBackground(new java.awt.Color(255, 255, 255));
        label_NO_LP.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_NO_LP.setForeground(new java.awt.Color(255, 0, 0));
        label_NO_LP.setText("LP");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        label_kadar_air_lp.setBackground(new java.awt.Color(255, 255, 255));
        label_kadar_air_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_kadar_air_lp.setText("%");

        label_berat_basah_lp1.setBackground(new java.awt.Color(255, 255, 255));
        label_berat_basah_lp1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_berat_basah_lp1.setText("Berat / Kpg :");

        label_kartu_waleta_LP.setBackground(new java.awt.Color(255, 255, 255));
        label_kartu_waleta_LP.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_kartu_waleta_LP.setText("No. Kartu Waleta :");

        jLabel19.setBackground(new java.awt.Color(255, 255, 255));
        jLabel19.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel19.setText("Gr");

        button_LP_select_kartu.setBackground(new java.awt.Color(255, 255, 255));
        button_LP_select_kartu.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_LP_select_kartu.setText("Pilih Kartu Baku");
        button_LP_select_kartu.setEnabled(false);
        button_LP_select_kartu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_LP_select_kartuActionPerformed(evt);
            }
        });

        label_berat_kering.setBackground(new java.awt.Color(255, 255, 255));
        label_berat_kering.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_berat_kering.setText("Berat 0% :");

        Label_kode_grade_lp.setBackground(new java.awt.Color(255, 255, 255));
        Label_kode_grade_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Label_kode_grade_lp.setText("-");
        Label_kode_grade_lp.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));

        label_berat_kering_lp.setBackground(new java.awt.Color(255, 255, 255));
        label_berat_kering_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_berat_kering_lp.setText("0");

        txt_jumlah_keping_lp.setEditable(false);
        txt_jumlah_keping_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_jumlah_keping_lp.setText("0");

        label_jumlah_pecah_lp1.setBackground(new java.awt.Color(255, 255, 255));
        label_jumlah_pecah_lp1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_jumlah_pecah_lp1.setText("Ruangan :");

        ComboBox_ruangan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Label_no_kartu_LP.setBackground(new java.awt.Color(255, 255, 255));
        Label_no_kartu_LP.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Label_no_kartu_LP.setText("-");
        Label_no_kartu_LP.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));

        jLabel25.setBackground(new java.awt.Color(255, 255, 255));
        jLabel25.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel25.setText("Kadar Air LP :");

        label_jumlah_pecah_lp5.setBackground(new java.awt.Color(255, 255, 255));
        label_jumlah_pecah_lp5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_jumlah_pecah_lp5.setText("Jenis Bulu :");

        jLabel21.setBackground(new java.awt.Color(255, 255, 255));
        jLabel21.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel21.setText("Kpg");

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel17.setText("Gram");

        label_berat_per_kpg_grading.setBackground(new java.awt.Color(255, 255, 255));
        label_berat_per_kpg_grading.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_berat_per_kpg_grading.setForeground(new java.awt.Color(255, 0, 0));
        label_berat_per_kpg_grading.setText("0");

        jLabel23.setBackground(new java.awt.Color(255, 255, 255));
        jLabel23.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel23.setText("Kpg");

        label_jumlah_kpg_lp.setBackground(new java.awt.Color(255, 255, 255));
        label_jumlah_kpg_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_jumlah_kpg_lp.setForeground(new java.awt.Color(255, 0, 0));
        label_jumlah_kpg_lp.setText("0");

        txt_berat_basah_lp.setEditable(false);
        txt_berat_basah_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_berat_basah_lp.setText("0");

        label_jumlah_keping_lp.setBackground(new java.awt.Color(255, 255, 255));
        label_jumlah_keping_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_jumlah_keping_lp.setText("Jumlah Keping LP :");

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel18.setText("Gram");

        jLabel22.setBackground(new java.awt.Color(255, 255, 255));
        jLabel22.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(255, 0, 0));
        jLabel22.setText("~");

        label_berat_basah_lp.setBackground(new java.awt.Color(255, 255, 255));
        label_berat_basah_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_berat_basah_lp.setText("Berat Angin2 :");

        label_jumlah_keping_lp1.setBackground(new java.awt.Color(255, 255, 255));
        label_jumlah_keping_lp1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_jumlah_keping_lp1.setText("Jumlah Keping Upah :");

        label_kode_grade_lp.setBackground(new java.awt.Color(255, 255, 255));
        label_kode_grade_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_kode_grade_lp.setText("Kode Grade :");

        txt_jumlah_keping_upah.setEditable(false);
        txt_jumlah_keping_upah.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_jumlah_keping_upah.setText("0");

        ComboBox_jenisBulu.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        txt_berat_per_kpg.setEditable(false);
        txt_berat_per_kpg.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_berat_per_kpg.setText("0");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(Label_no_kartu_LP, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Label_kode_grade_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_LP_select_kartu)
                            .addComponent(ComboBox_jenisBulu, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ComboBox_ruangan, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(1, 1, 1))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(label_jumlah_pecah_lp5, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_berat_basah_lp1, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_kartu_waleta_LP, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_kode_grade_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_jumlah_pecah_lp1, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_jumlah_keping_lp1, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_berat_basah_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_berat_kering, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_jumlah_keping_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txt_berat_per_kpg, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txt_berat_basah_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txt_jumlah_keping_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txt_jumlah_keping_upah, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel17)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addComponent(jLabel21)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(jLabel22)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(label_jumlah_kpg_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(jLabel23))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel19)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(label_berat_per_kpg_grading, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_kadar_air_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(117, 117, 117)
                        .addComponent(label_berat_kering_lp)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel18)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGap(26, 26, 26)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(51, 51, 51)
                                        .addComponent(ComboBox_jenisBulu, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(Label_kode_grade_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(button_LP_select_kartu, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_ruangan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_jumlah_kpg_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txt_jumlah_keping_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txt_jumlah_keping_upah, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txt_berat_basah_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGap(26, 26, 26)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addGap(51, 51, 51)
                                            .addComponent(label_jumlah_pecah_lp5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(label_kode_grade_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(label_jumlah_pecah_lp1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(label_jumlah_keping_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(label_jumlah_keping_lp1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(label_berat_basah_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(label_kartu_waleta_LP, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(Label_no_kartu_LP, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(26, 26, 26))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(txt_berat_per_kpg, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_berat_per_kpg_grading, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(label_berat_basah_lp1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_berat_kering_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(label_berat_kering, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_kadar_air_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        label_jumlah_pecah_lp6.setBackground(new java.awt.Color(255, 255, 255));
        label_jumlah_pecah_lp6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_jumlah_pecah_lp6.setText("Grup :");

        txt_memo.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_memo.setText("-");

        label_jumlah_pecah_lp2.setBackground(new java.awt.Color(255, 255, 255));
        label_jumlah_pecah_lp2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_jumlah_pecah_lp2.setText("Memo :");

        Date_LP.setBackground(new java.awt.Color(255, 255, 255));
        Date_LP.setDate(new Date());
        Date_LP.setDateFormatString("dd MMMM yyyy");
        Date_LP.setMinSelectableDate(new java.util.Date(1704045662000L));

        label_tgl_lp.setBackground(new java.awt.Color(255, 255, 255));
        label_tgl_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_tgl_lp.setText("Tanggal LP :");

        button_update_LP.setBackground(new java.awt.Color(255, 255, 255));
        button_update_LP.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_update_LP.setText("Update");
        button_update_LP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_update_LPActionPerformed(evt);
            }
        });

        button_insert_LP.setBackground(new java.awt.Color(255, 255, 255));
        button_insert_LP.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_insert_LP.setText("insert");
        button_insert_LP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_insert_LPActionPerformed(evt);
            }
        });

        label_jumlah_pecah_lp7.setBackground(new java.awt.Color(255, 255, 255));
        label_jumlah_pecah_lp7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_jumlah_pecah_lp7.setForeground(new java.awt.Color(255, 51, 51));
        label_jumlah_pecah_lp7.setText("Grup > 100 adalah LP tambahan");

        txt_gumpil_lp.setEditable(false);
        txt_gumpil_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_gumpil_lp.setText("0");
        txt_gumpil_lp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_gumpil_lpKeyTyped(evt);
            }
        });

        label_jumlah_sobek_lp.setBackground(new java.awt.Color(255, 255, 255));
        label_jumlah_sobek_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_jumlah_sobek_lp.setText("Sobek :");

        label_jumlah_hilang_ujung_lp1.setBackground(new java.awt.Color(255, 255, 255));
        label_jumlah_hilang_ujung_lp1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_jumlah_hilang_ujung_lp1.setText("Kaki Kecil :");

        label_jumlah_gumpil_lp.setBackground(new java.awt.Color(255, 255, 255));
        label_jumlah_gumpil_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_jumlah_gumpil_lp.setText("Gumpil :");

        txt_ada_susur_lp.setEditable(false);
        txt_ada_susur_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_ada_susur_lp.setText("0");
        txt_ada_susur_lp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_ada_susur_lpKeyTyped(evt);
            }
        });

        label_jumlah_sobek_lp1.setBackground(new java.awt.Color(255, 255, 255));
        label_jumlah_sobek_lp1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_jumlah_sobek_lp1.setText("Sobek Lepas :");

        label_jumlah_hilang_kaki_lp1.setBackground(new java.awt.Color(255, 255, 255));
        label_jumlah_hilang_kaki_lp1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_jumlah_hilang_kaki_lp1.setText("Kaki Besar :");

        label_jumlah_gumpil_lp1.setBackground(new java.awt.Color(255, 255, 255));
        label_jumlah_gumpil_lp1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_jumlah_gumpil_lp1.setText("Susur KECIL :");

        label_jumlah_pecah_lp3.setBackground(new java.awt.Color(255, 255, 255));
        label_jumlah_pecah_lp3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_jumlah_pecah_lp3.setText("Pecah 2 :");

        txt_kaki_besar_lp.setEditable(false);
        txt_kaki_besar_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_kaki_besar_lp.setText("0");
        txt_kaki_besar_lp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_kaki_besar_lpKeyTyped(evt);
            }
        });

        txt_tanpa_susur_lp.setEditable(false);
        txt_tanpa_susur_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_tanpa_susur_lp.setText("0");
        txt_tanpa_susur_lp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_tanpa_susur_lpKeyTyped(evt);
            }
        });

        label_jumlah_hilang_kaki_lp2.setBackground(new java.awt.Color(255, 255, 255));
        label_jumlah_hilang_kaki_lp2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_jumlah_hilang_kaki_lp2.setText("Utuh :");

        label_jumlah_pecah_lp4.setBackground(new java.awt.Color(255, 255, 255));
        label_jumlah_pecah_lp4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_jumlah_pecah_lp4.setText("Tanpa Susur :");

        label_jumlah_hilang_ujung_lp.setBackground(new java.awt.Color(255, 255, 255));
        label_jumlah_hilang_ujung_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_jumlah_hilang_ujung_lp.setText("Hilang Ujung :");

        label_jumlah_hilang_kaki_lp.setBackground(new java.awt.Color(255, 255, 255));
        label_jumlah_hilang_kaki_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_jumlah_hilang_kaki_lp.setText("Tanpa Kaki :");

        label_jumlah_pecah_lp.setBackground(new java.awt.Color(255, 255, 255));
        label_jumlah_pecah_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_jumlah_pecah_lp.setText("Pecah 1 :");

        txt_kaki_kecil_lp.setEditable(false);
        txt_kaki_kecil_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_kaki_kecil_lp.setText("0");
        txt_kaki_kecil_lp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_kaki_kecil_lpKeyTyped(evt);
            }
        });

        txt_pecah_1_lp.setEditable(false);
        txt_pecah_1_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_pecah_1_lp.setText("0");
        txt_pecah_1_lp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_pecah_1_lpKeyTyped(evt);
            }
        });

        txt_hilang_ujung_lp.setEditable(false);
        txt_hilang_ujung_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_hilang_ujung_lp.setText("0");
        txt_hilang_ujung_lp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_hilang_ujung_lpKeyTyped(evt);
            }
        });

        txt_sobek_lepas_lp.setEditable(false);
        txt_sobek_lepas_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_sobek_lepas_lp.setText("0");
        txt_sobek_lepas_lp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_sobek_lepas_lpKeyTyped(evt);
            }
        });

        txt_pecah_2_lp.setEditable(false);
        txt_pecah_2_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_pecah_2_lp.setText("0");
        txt_pecah_2_lp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_pecah_2_lpKeyTyped(evt);
            }
        });

        txt_sobek_lp.setEditable(false);
        txt_sobek_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_sobek_lp.setText("0");
        txt_sobek_lp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_sobek_lpKeyTyped(evt);
            }
        });

        txt_hilang_kaki_lp.setEditable(false);
        txt_hilang_kaki_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_hilang_kaki_lp.setText("0");
        txt_hilang_kaki_lp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_hilang_kaki_lpKeyTyped(evt);
            }
        });

        txt_utuh_lp.setEditable(false);
        txt_utuh_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_utuh_lp.setText("0");
        txt_utuh_lp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_utuh_lpKeyTyped(evt);
            }
        });

        label_jumlah_gumpil_lp2.setBackground(new java.awt.Color(255, 255, 255));
        label_jumlah_gumpil_lp2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_jumlah_gumpil_lp2.setText("Susur BESAR :");

        txt_ada_susur_besar_lp.setEditable(false);
        txt_ada_susur_besar_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_ada_susur_besar_lp.setText("0");
        txt_ada_susur_besar_lp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_ada_susur_besar_lpKeyTyped(evt);
            }
        });

        label_jumlah_pecah_lp8.setBackground(new java.awt.Color(255, 255, 255));
        label_jumlah_pecah_lp8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_jumlah_pecah_lp8.setText("Rontokan GBM :");

        txt_rontokan_gbm.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_rontokan_gbm.setText("0");
        txt_rontokan_gbm.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_rontokan_gbmKeyTyped(evt);
            }
        });

        txt_grup_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_grup_lp.setText("0");
        txt_grup_lp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_grup_lpKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(button_update_LP)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_insert_LP)
                .addGap(10, 10, 10))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(label_jumlah_pecah_lp6, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_tgl_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_jumlah_pecah_lp2, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_jumlah_gumpil_lp1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_jumlah_hilang_kaki_lp1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_jumlah_pecah_lp8, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_jumlah_hilang_ujung_lp1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_jumlah_gumpil_lp2, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_jumlah_hilang_kaki_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_jumlah_pecah_lp4, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(Date_LP, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(txt_ada_susur_besar_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(label_jumlah_gumpil_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_jumlah_sobek_lp1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_jumlah_sobek_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_jumlah_pecah_lp3, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_jumlah_pecah_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_jumlah_hilang_ujung_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_jumlah_hilang_kaki_lp2, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(4, 4, 4)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txt_gumpil_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txt_sobek_lepas_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txt_sobek_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txt_pecah_2_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txt_pecah_1_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txt_hilang_ujung_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txt_utuh_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(txt_grup_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_jumlah_pecah_lp7))
                            .addComponent(txt_memo, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(94, 94, 94)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_rontokan_gbm, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_ada_susur_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_tanpa_susur_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_hilang_kaki_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_kaki_kecil_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_kaki_besar_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(label_tgl_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_LP, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(label_jumlah_hilang_kaki_lp1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_kaki_besar_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_jumlah_hilang_kaki_lp2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_utuh_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(label_jumlah_hilang_ujung_lp1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_kaki_kecil_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_jumlah_hilang_ujung_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_hilang_ujung_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(label_jumlah_hilang_kaki_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_hilang_kaki_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_jumlah_pecah_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_pecah_1_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(label_jumlah_pecah_lp3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_pecah_2_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_jumlah_gumpil_lp2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_ada_susur_besar_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(label_jumlah_sobek_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_sobek_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_jumlah_gumpil_lp1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_ada_susur_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_jumlah_sobek_lp1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_sobek_lepas_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(label_jumlah_pecah_lp4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_tanpa_susur_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_jumlah_gumpil_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_gumpil_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_jumlah_pecah_lp2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_memo, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_jumlah_pecah_lp6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_jumlah_pecah_lp7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_grup_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(label_jumlah_pecah_lp8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_rontokan_gbm, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_update_LP, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_insert_LP, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        button_pilih_pecah_lp.setBackground(new java.awt.Color(255, 255, 255));
        button_pilih_pecah_lp.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        button_pilih_pecah_lp.setText("Pilih data dari pecah LP");
        button_pilih_pecah_lp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_pilih_pecah_lpActionPerformed(evt);
            }
        });

        label_kode_pecah_lp.setBackground(new java.awt.Color(255, 255, 255));
        label_kode_pecah_lp.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_kode_pecah_lp.setForeground(new java.awt.Color(255, 0, 0));
        label_kode_pecah_lp.setText("-");

        javax.swing.GroupLayout jPanel_operation_lpLayout = new javax.swing.GroupLayout(jPanel_operation_lp);
        jPanel_operation_lp.setLayout(jPanel_operation_lpLayout);
        jPanel_operation_lpLayout.setHorizontalGroup(
            jPanel_operation_lpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_operation_lpLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_operation_lpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_operation_lpLayout.createSequentialGroup()
                        .addComponent(label_title)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_NO_LP))
                    .addGroup(jPanel_operation_lpLayout.createSequentialGroup()
                        .addComponent(button_pilih_pecah_lp)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_kode_pecah_lp)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel_operation_lpLayout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel_operation_lpLayout.setVerticalGroup(
            jPanel_operation_lpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_operation_lpLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_operation_lpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_title, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_NO_LP, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_lpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_pilih_pecah_lp)
                    .addComponent(label_kode_pecah_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3)
                .addGroup(jPanel_operation_lpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel_operation_lp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel_operation_lp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void button_LP_select_kartuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_LP_select_kartuActionPerformed
        Stock_Bahan_Baku dialog = new Stock_Bahan_Baku(new javax.swing.JFrame(), true, "lp");
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);
        if (Label_kode_grade_lp.getText().contains("BRS/BR")) {
            ComboBox_jenisBulu.setSelectedIndex(0);
        } else if (Label_kode_grade_lp.getText().contains("BR") && !Label_kode_grade_lp.getText().contains("BRT")) {
            ComboBox_jenisBulu.setSelectedIndex(1);
        } else if (Label_kode_grade_lp.getText().contains("BS")) {
            ComboBox_jenisBulu.setSelectedIndex(2);
        } else if (Label_kode_grade_lp.getText().contains("BB2")) {
            ComboBox_jenisBulu.setSelectedIndex(4);
        } else if (Label_kode_grade_lp.getText().contains("BB")) {
            ComboBox_jenisBulu.setSelectedIndex(3);
        }
        if (Label_kode_grade_lp.getText().equals("Ragam Serat")) {
            txt_jumlah_keping_upah.setEditable(true);
        } else {
            txt_jumlah_keping_upah.setEditable(false);
        }
        try {
            sql = "SELECT `memo_untuk_lp` FROM `tb_grade_bahan_baku` WHERE `kode_grade` = '" + Label_kode_grade_lp.getText() + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                txt_memo.setText(rs.getString("memo_untuk_lp"));
            }
            countBK();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_LP_select_kartuActionPerformed

    private void button_update_LPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_update_LPActionPerformed
        Boolean Check = true;
        try {
            countBK();
            Utility.db_sub.connect();
            Utility.db_cabuto.connect();
            Utility.db.getConnection().setAutoCommit(false);
            Utility.db_sub.getConnection().setAutoCommit(false);
            Utility.db_cabuto.getConnection().setAutoCommit(false);

            if (berat_kering > berat_basah) {
                JOptionPane.showMessageDialog(this, "Maaf Berat Kering tidak bisa lebih besar dari berat angin2");
                Check = false;
            } else if (keping_upah <= 0) {
                JOptionPane.showMessageDialog(this, "Maaf Keping upah tidak bisa kurang dari 1");
                Check = false;
            } else if (berat_basah <= 0) {
                JOptionPane.showMessageDialog(this, "Maaf Berat tidak bisa kurang dari 1");
                Check = false;
            } else if (label_kode_pecah_lp.getText() == null || label_kode_pecah_lp.getText().equals("") || label_kode_pecah_lp.getText().equals("-")) {
                JOptionPane.showMessageDialog(this, "Kode Pecah belum dipilih!");
                Check = false;
            }

            String query2 = "SELECT `no_laporan_produksi` FROM `tb_cetak` WHERE `no_laporan_produksi` = '" + no_lp + "'";
            ResultSet rs3 = Utility.db.getStatement().executeQuery(query2);
            if (rs3.next()) {
                JOptionPane.showMessageDialog(this, "Maaf no LP " + no_lp + " sudah masuk CETAK, tidak dapat di edit");
                Check = false;
            } else if ("No. Kartu Waleta".equals(Label_no_kartu_LP.getText())) {
                JOptionPane.showMessageDialog(this, "Belum Memilih Nomor Kartu !");
                Check = false;
            }

            if (Check) {
                String ruangan_akhir = "";
                if (list_kode_sub.indexOf(ComboBox_ruangan.getSelectedItem().toString()) > -1) {
                    ruangan_akhir = "SUB";
                } else if (ComboBox_ruangan.getSelectedItem().toString().length() == 1) {
                    ruangan_akhir = "WALETA";
                } else if (ComboBox_ruangan.getSelectedItem().toString().equals("CABUTO")) {
                    ruangan_akhir = "CABUTO";
                }
                if (ruangan_awal.equals("WALETA") && ruangan_akhir.equals("WALETA")) {
                    edit_lp_waleta();
                } else if (ruangan_awal.equals("SUB") && ruangan_akhir.equals("SUB")) {
                    edit_lp_waleta();
                    edit_lp_sub();
                } else if (ruangan_awal.equals("WALETA") && ruangan_akhir.equals("SUB")) {
                    edit_lp_waleta();
                    input_lp_sub();
                } else if (ruangan_awal.equals("SUB") && ruangan_akhir.equals("WALETA")) {
                    edit_lp_waleta();
                    delete_lp_sub();
                } else if (ruangan_awal.equals("CABUTO") && ruangan_akhir.equals("CABUTO")) {
                    edit_lp_waleta();
                    edit_lp_cabuto();
                } else if (ruangan_awal.equals("CABUTO") && ruangan_akhir.equals("WALETA")) {
                    edit_lp_waleta();
                    delete_lp_cabuto();
                } else if (ruangan_awal.equals("WALETA") && ruangan_akhir.equals("CABUTO")) {
                    JOptionPane.showMessageDialog(this, "Tidak bisa memindahkan LP WALETA ke CABUTO !");
//                    edit_lp_waleta();
//                    input_lp_cabuto();
                } else if (ruangan_awal.equals("SUB") && ruangan_akhir.equals("CABUTO")) {
                    JOptionPane.showMessageDialog(this, "Tidak bisa memindahkan LP SUB ke CABUTO !");
                } else if (ruangan_awal.equals("CABUTO") && ruangan_akhir.equals("SUB")) {
                    JOptionPane.showMessageDialog(this, "Tidak bisa memindahkan LP CABUTO ke SUB !");
                } else {
                    JOptionPane.showMessageDialog(this, "Ruangan tidak ditemukan, silahkan hubungi bagian IT");
                }

                Utility.db.getConnection().commit();
                Utility.db_sub.getConnection().commit();
                Utility.db_cabuto.getConnection().commit();
                this.dispose();
                JOptionPane.showMessageDialog(this, "LP berhasil di Edit");
            }
        } catch (Exception ex) {
            try {
                Utility.db.getConnection().rollback();
                Utility.db_sub.getConnection().rollback();
                Utility.db_cabuto.getConnection().rollback();
            } catch (SQLException e) {
                Logger.getLogger(JDialog_Edit_Insert_LP.class.getName()).log(Level.SEVERE, null, e);
            }
            JOptionPane.showMessageDialog(this, "EDIT GAGAL : " + ex);
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                Utility.db.getConnection().setAutoCommit(true);
                Utility.db_sub.getConnection().setAutoCommit(true);
                Utility.db_cabuto.getConnection().setAutoCommit(true);
            } catch (SQLException ex) {
                Logger.getLogger(JDialog_Edit_Insert_LP.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_button_update_LPActionPerformed

    private void button_insert_LPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_insert_LPActionPerformed
        Boolean Check = true;
        try {
            String get_tgl_kartu = "SELECT `tgl_masuk` FROM `tb_bahan_baku_masuk` WHERE `no_kartu_waleta` = '" + Label_no_kartu_LP.getText() + "'";
            ResultSet result = Utility.db.getStatement().executeQuery(get_tgl_kartu);
            if (result.next()) {
                if (Date_LP.getDate() == null) {
                    JOptionPane.showMessageDialog(this, "Maaf Tanggal LP tidak boleh kosong !");
                    Check = false;
                } else if (result.getDate("tgl_masuk") == null) {
                    JOptionPane.showMessageDialog(this, "Maaf Tanggal kartu masuk tidak boleh kosong !");
                    Check = false;
                } else if (result.getDate("tgl_masuk").after(Date_LP.getDate())) {
                    JOptionPane.showMessageDialog(this, "Maaf Tanggal LP harus setelah tanggal kartu masuk !");
                    Check = false;
                }
            } else {
                JOptionPane.showMessageDialog(this, "Kartu baku tidak ditemukan !");
                Check = false;
            }
        } catch (SQLException ex) {
            Check = false;
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JDialog_Edit_Insert_LP.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (berat_per_keping_lp > (berat_per_keping_grading + 1)
                || berat_per_keping_lp < (berat_per_keping_grading - 1)) {
            JOptionPane.showMessageDialog(this, "Maaf Berat/Keping tidak wajar (± 1gr)");
            Check = false;
        } else if (berat_kering > berat_basah) {
            JOptionPane.showMessageDialog(this, "Maaf Berat Kering tidak bisa lebih besar dari berat angin2\nSilahkan cek kadar air kartu baku");
            Check = false;
        }
        if (Check) {
            if (status.equals("sub")) {
                insert_lp_sub();
            } else {
                if (ComboBox_ruangan.getSelectedItem().toString().equals("CABUTO")) {
                    insert_lp_cabuto();
                } else {
                    insert_lp_waleta();
                }
            }
        }
    }//GEN-LAST:event_button_insert_LPActionPerformed

    private void button_pilih_pecah_lpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_pilih_pecah_lpActionPerformed
        try {
            // TODO add your handling code here:
            JDialog_Pilih_PecahLP dialog = new JDialog_Pilih_PecahLP(new javax.swing.JFrame(), true, this);
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            dialog.setEnabled(true);

            if (label_kode_pecah_lp.getText() != null && !label_kode_pecah_lp.getText().equals("") && !label_kode_pecah_lp.getText().equals("-")) {
                sql = "SELECT (`total_berat` / `jumlah_keping`) AS 'berat_kpg' FROM `tb_grading_bahan_baku` "
                        + "WHERE `no_kartu_waleta` = '" + Label_no_kartu_LP.getText() + "' AND `kode_grade` = '" + Label_kode_grade_lp.getText() + "'";
                rs = Utility.db.getStatement().executeQuery(sql);
                if (rs.next()) {
                    berat_per_keping_grading = rs.getFloat("berat_kpg");
                    label_berat_per_kpg_grading.setText(decimalFormat.format(rs.getFloat("berat_kpg")));
                }

                if (txt_memo.getText().equals("")) {
                    sql = "SELECT `memo_untuk_lp` FROM `tb_grade_bahan_baku` WHERE `kode_grade` = '" + Label_kode_grade_lp.getText() + "'";
                    rs = Utility.db.getStatement().executeQuery(sql);
                    if (rs.next()) {
                        txt_memo.setText(rs.getString("memo_untuk_lp"));
                    }
                }
                countBK();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JDialog_Edit_Insert_LP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_pilih_pecah_lpActionPerformed

    private void txt_kaki_besar_lpKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_kaki_besar_lpKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_kaki_besar_lpKeyTyped

    private void txt_utuh_lpKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_utuh_lpKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_utuh_lpKeyTyped

    private void txt_kaki_kecil_lpKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_kaki_kecil_lpKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_kaki_kecil_lpKeyTyped

    private void txt_hilang_ujung_lpKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_hilang_ujung_lpKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_hilang_ujung_lpKeyTyped

    private void txt_hilang_kaki_lpKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_hilang_kaki_lpKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_hilang_kaki_lpKeyTyped

    private void txt_pecah_1_lpKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_pecah_1_lpKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_pecah_1_lpKeyTyped

    private void txt_ada_susur_besar_lpKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_ada_susur_besar_lpKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_ada_susur_besar_lpKeyTyped

    private void txt_pecah_2_lpKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_pecah_2_lpKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_pecah_2_lpKeyTyped

    private void txt_ada_susur_lpKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_ada_susur_lpKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_ada_susur_lpKeyTyped

    private void txt_sobek_lpKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_sobek_lpKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_sobek_lpKeyTyped

    private void txt_tanpa_susur_lpKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_tanpa_susur_lpKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_tanpa_susur_lpKeyTyped

    private void txt_sobek_lepas_lpKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_sobek_lepas_lpKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_sobek_lepas_lpKeyTyped

    private void txt_gumpil_lpKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_gumpil_lpKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_gumpil_lpKeyTyped

    private void txt_grup_lpKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_grup_lpKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_grup_lpKeyTyped

    private void txt_rontokan_gbmKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_rontokan_gbmKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_rontokan_gbmKeyTyped


    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JComboBox<String> ComboBox_jenisBulu;
    public javax.swing.JComboBox<String> ComboBox_ruangan;
    private com.toedter.calendar.JDateChooser Date_LP;
    public static javax.swing.JLabel Label_kode_grade_lp;
    public static javax.swing.JLabel Label_no_kartu_LP;
    public javax.swing.JButton button_LP_select_kartu;
    public javax.swing.JButton button_insert_LP;
    private javax.swing.JButton button_pilih_pecah_lp;
    public javax.swing.JButton button_update_LP;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel_operation_lp;
    private javax.swing.JLabel label_NO_LP;
    private javax.swing.JLabel label_berat_basah_lp;
    private javax.swing.JLabel label_berat_basah_lp1;
    private javax.swing.JLabel label_berat_kering;
    public javax.swing.JLabel label_berat_kering_lp;
    public static javax.swing.JLabel label_berat_per_kpg_grading;
    private javax.swing.JLabel label_jumlah_gumpil_lp;
    private javax.swing.JLabel label_jumlah_gumpil_lp1;
    private javax.swing.JLabel label_jumlah_gumpil_lp2;
    private javax.swing.JLabel label_jumlah_hilang_kaki_lp;
    private javax.swing.JLabel label_jumlah_hilang_kaki_lp1;
    private javax.swing.JLabel label_jumlah_hilang_kaki_lp2;
    private javax.swing.JLabel label_jumlah_hilang_ujung_lp;
    private javax.swing.JLabel label_jumlah_hilang_ujung_lp1;
    private javax.swing.JLabel label_jumlah_keping_lp;
    private javax.swing.JLabel label_jumlah_keping_lp1;
    public static javax.swing.JLabel label_jumlah_kpg_lp;
    private javax.swing.JLabel label_jumlah_pecah_lp;
    private javax.swing.JLabel label_jumlah_pecah_lp1;
    private javax.swing.JLabel label_jumlah_pecah_lp2;
    private javax.swing.JLabel label_jumlah_pecah_lp3;
    private javax.swing.JLabel label_jumlah_pecah_lp4;
    private javax.swing.JLabel label_jumlah_pecah_lp5;
    private javax.swing.JLabel label_jumlah_pecah_lp6;
    private javax.swing.JLabel label_jumlah_pecah_lp7;
    private javax.swing.JLabel label_jumlah_pecah_lp8;
    private javax.swing.JLabel label_jumlah_sobek_lp;
    private javax.swing.JLabel label_jumlah_sobek_lp1;
    private javax.swing.JLabel label_kadar_air_lp;
    private javax.swing.JLabel label_kartu_waleta_LP;
    private javax.swing.JLabel label_kode_grade_lp;
    public javax.swing.JLabel label_kode_pecah_lp;
    private javax.swing.JLabel label_tgl_lp;
    private javax.swing.JLabel label_title;
    public javax.swing.JTextField txt_ada_susur_besar_lp;
    public javax.swing.JTextField txt_ada_susur_lp;
    public static javax.swing.JTextField txt_berat_basah_lp;
    public static javax.swing.JTextField txt_berat_per_kpg;
    public javax.swing.JTextField txt_grup_lp;
    public javax.swing.JTextField txt_gumpil_lp;
    public javax.swing.JTextField txt_hilang_kaki_lp;
    public javax.swing.JTextField txt_hilang_ujung_lp;
    public static javax.swing.JTextField txt_jumlah_keping_lp;
    public static javax.swing.JTextField txt_jumlah_keping_upah;
    public javax.swing.JTextField txt_kaki_besar_lp;
    public javax.swing.JTextField txt_kaki_kecil_lp;
    public javax.swing.JTextField txt_memo;
    public javax.swing.JTextField txt_pecah_1_lp;
    public javax.swing.JTextField txt_pecah_2_lp;
    public javax.swing.JTextField txt_rontokan_gbm;
    public javax.swing.JTextField txt_sobek_lepas_lp;
    public javax.swing.JTextField txt_sobek_lp;
    public javax.swing.JTextField txt_tanpa_susur_lp;
    public javax.swing.JTextField txt_utuh_lp;
    // End of variables declaration//GEN-END:variables
}
