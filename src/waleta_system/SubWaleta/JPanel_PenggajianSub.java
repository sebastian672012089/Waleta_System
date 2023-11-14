package waleta_system.SubWaleta;

import java.awt.event.KeyEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.ExportToExcel;
import waleta_system.Class.Utility;

public class JPanel_PenggajianSub extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    PreparedStatement pst;
    Date date = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DecimalFormat decimalFormat = new DecimalFormat();
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    HashMap<String, Double> bonus_mku_setiap_karyawan = new HashMap<>();
    HashMap<String, Double> bonus_mku_cabut_setiap_sub = new HashMap<>();
    HashMap<String, Double> bonus_mku_cetak_setiap_sub = new HashMap<>();
    HashMap<String, Double> upah_cuci_per_karyawan = new HashMap<>();
    HashMap<String, Double> bonus_cuci_per_karyawan = new HashMap<>();

    //HashMap untuk rekap per sub
    HashMap<String, Double> total_gram_lp_cabut_dikerjakan_per_sub = new HashMap<>();
    HashMap<String, Double> total_gram_lp_sesekan_dikerjakan_per_sub = new HashMap<>();
    HashMap<String, Double> total_gram_lp_sapon_dikerjakan_per_sub = new HashMap<>();
    HashMap<String, Double> total_bobot_cabut_dikerjakan_per_sub = new HashMap<>();
    HashMap<String, Double> total_bobot_sesekan_dikerjakan_per_sub = new HashMap<>();
    HashMap<String, Double> total_bobot_sapon_dikerjakan_per_sub = new HashMap<>();
    HashMap<String, Double> upah_cabut_per_sub = new HashMap<>();
    HashMap<String, Double> bonus_cabut_per_sub = new HashMap<>();
    HashMap<String, Double> upah_sesekan_per_sub = new HashMap<>();
    HashMap<String, Double> bonus_sesekan_per_sub = new HashMap<>();
    HashMap<String, Double> upah_sapon_per_sub = new HashMap<>();
    HashMap<String, Double> bonus_sapon_per_sub = new HashMap<>();
    HashMap<String, Long> upah_cuci_per_sub = new HashMap<>();
    HashMap<String, Long> bonus_cuci_per_sub = new HashMap<>();
    HashMap<String, Long> bonus_MKU_per_sub = new HashMap<>();
    HashMap<String, Long> subsidi_training_per_sub = new HashMap<>();
    HashMap<String, Long> tunjangan_hadir_per_sub = new HashMap<>();
    HashMap<String, Long> piutang_per_sub = new HashMap<>();
    HashMap<String, Long> total_gaji_karyawan_per_sub = new HashMap<>();

    public JPanel_PenggajianSub() {
        initComponents();
    }

    public void init() {
        decimalFormat = Utility.DecimalFormatUS(decimalFormat);
        try {
            Utility.db_sub.connect();
            ComboBox_sub.removeAllItems();
            ComboBox_sub_bonusCabut.removeAllItems();
            ComboBox_sub_bonusMKU.removeAllItems();
            ComboBox_sub.addItem("All");
            ComboBox_sub_bonusCabut.addItem("All");
            ComboBox_sub_bonusMKU.addItem("All");
            String sub = "SELECT `kode_sub` FROM `tb_sub_waleta` WHERE 1";
            ResultSet rs2 = Utility.db_sub.getStatement().executeQuery(sub);
            while (rs2.next()) {
                ComboBox_sub.addItem(rs2.getString("kode_sub"));
                ComboBox_sub_bonusCabut.addItem(rs2.getString("kode_sub"));
                ComboBox_sub_bonusMKU.addItem(rs2.getString("kode_sub"));
            }

            ComboBox_bagian.removeAllItems();
            ComboBox_bagian.addItem("All");
            sql = "SELECT DISTINCT(`sub`) AS 'sub' FROM `tb_slip_gaji` WHERE `sub` IS NOT NULL";
            rs = Utility.db_sub.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_bagian.addItem(rs.getString("sub"));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_PenggajianSub.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_data_pencabut() {
        try {
            Utility.db_sub.connect();
            float total_gram_cabut = 0, total_upah = 0;
            decimalFormat.setMaximumFractionDigits(2);
            DefaultTableModel model = (DefaultTableModel) table_data_pencabut.getModel();
            model.setRowCount(0);
            String search_sub = " AND `tb_laporan_produksi`.`ruangan` = '" + ComboBox_sub.getSelectedItem().toString() + "'";
            if (ComboBox_sub.getSelectedItem().toString().equals("All")) {
                search_sub = "";
            }
            if (Date_kinerja_borong1.getDate() != null && Date_kinerja_borong2.getDate() != null) {
                sql = "SELECT `tb_detail_pencabut`.`id_pegawai`, `nama_pegawai`, `bagian`, `tb_detail_pencabut`.`no_laporan_produksi`, `jenis_bulu_lp`, `tb_detail_pencabut`.`tanggal_cabut`, `tgl_setor_cabut`, `jumlah_cabut`, `jumlah_gram`, "
                        + "`tb_tarif_upah`.`upah_cabut` AS 'upah_per_gram', ROUND(`jumlah_gram` * `tb_tarif_upah`.`upah_cabut`) AS 'upah_cabut' \n"
                        + "FROM `tb_detail_pencabut` \n"
                        + "LEFT JOIN `tb_laporan_produksi` ON `tb_detail_pencabut`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                        + "LEFT JOIN `tb_tarif_upah` ON `tb_laporan_produksi`.`jenis_bulu_lp` = `tb_tarif_upah`.`bulu_upah` \n"
                        + "LEFT JOIN `tb_cabut` ON `tb_detail_pencabut`.`no_laporan_produksi` = `tb_cabut`.`no_laporan_produksi`\n"
                        + "LEFT JOIN `tb_karyawan` ON `tb_detail_pencabut`.`id_pegawai` = `tb_karyawan`.`id_pegawai`\n"
                        + "WHERE `tb_karyawan`.`nama_pegawai` LIKE '%" + txt_search_nama.getText() + "%' "
                        + "AND `tb_detail_pencabut`.`no_laporan_produksi` LIKE '%" + txt_search_no_lp.getText() + "%' "
                        + "AND `tb_detail_pencabut`.`no_laporan_produksi` LIKE 'WL.%'"
                        + search_sub
                        + "AND (`tgl_setor_cabut` BETWEEN '" + dateFormat.format(Date_kinerja_borong1.getDate()) + "' AND '" + dateFormat.format(Date_kinerja_borong2.getDate()) + "') ";
                rs = Utility.db_sub.getStatement().executeQuery(sql);
                Object[] row = new Object[15];
                while (rs.next()) {
                    row[0] = rs.getString("id_pegawai");
                    row[1] = rs.getString("nama_pegawai");
                    row[2] = rs.getString("bagian");
                    row[3] = rs.getString("no_laporan_produksi");
                    row[4] = rs.getString("jenis_bulu_lp");
                    row[5] = rs.getDate("tanggal_cabut");
                    row[6] = rs.getDate("tgl_setor_cabut");
                    row[7] = rs.getInt("jumlah_cabut");
                    int jumlah_cabut = 0;
                    if (rs.getInt("jumlah_cabut") == 0) {
                        jumlah_cabut = Math.round(rs.getFloat("jumlah_gram") / 8);
                    } else {
                        jumlah_cabut = rs.getInt("jumlah_cabut");
                    }
                    row[8] = jumlah_cabut;
                    row[9] = rs.getFloat("jumlah_gram");
                    row[10] = rs.getInt("upah_per_gram");
                    row[11] = rs.getFloat("upah_cabut");
                    model.addRow(row);
                    total_gram_cabut = total_gram_cabut + rs.getFloat("jumlah_gram");
                    total_upah = total_upah + rs.getFloat("upah_cabut");
                }
                refreshTable_rekap_pencabut(sql);
                ColumnsAutoSizer.sizeColumnsToFit(table_data_pencabut);
                label_total_gram_cabut.setText(decimalFormat.format(total_gram_cabut));
                label_total_upah_cabut.setText(decimalFormat.format(total_upah));
            } else {
                JOptionPane.showMessageDialog(this, "Filter Tanggal harus dipilih!");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_PenggajianSub.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_rekap_pencabut(String query) {
        try {
            float total_upah_rekap_cabut = 0;
            decimalFormat.setMaximumFractionDigits(2);
            DefaultTableModel model = (DefaultTableModel) table_data_rekap_cabut.getModel();
            model.setRowCount(0);
            if (query != null && !query.equals("")) {
//                String qry = "SELECT `tgl_setor_cabut`, `id_pegawai`, `nama_pegawai`, `nama_bagian`, `grup_cabut`, SUM(`jumlah_cabut`) AS 'jumlah_cabut', SUM(`jumlah_gram`) AS 'jumlah_gram', SUM(`upah_cabut`) AS 'upah_cabut' \n"
//                        + "FROM (" + query + ") detail "
//                        + "GROUP BY `id_pegawai`, `tgl_setor_cabut`";
                String qry = "SELECT `tgl_setor_cabut`, `id_pegawai`, `nama_pegawai`, `bagian`, SUM(`jumlah_cabut`) AS 'jumlah_cabut', SUM(`jumlah_gram`) AS 'jumlah_gram', SUM(`upah_cabut`) AS 'upah_cabut' \n"
                        + "FROM (" + query + ") detail "
                        + "GROUP BY `id_pegawai`, `tgl_setor_cabut`";
                rs = Utility.db_sub.getStatement().executeQuery(qry);
                Object[] row = new Object[15];
                while (rs.next()) {
                    row[0] = rs.getDate("tgl_setor_cabut");
                    row[1] = rs.getString("id_pegawai");
                    row[2] = rs.getString("nama_pegawai");
                    row[3] = rs.getString("bagian");
                    row[4] = rs.getInt("jumlah_cabut");
                    row[5] = rs.getFloat("jumlah_gram");
                    row[6] = rs.getFloat("upah_cabut");
                    model.addRow(row);
                    total_upah_rekap_cabut = total_upah_rekap_cabut + rs.getFloat("upah_cabut");
                }
                ColumnsAutoSizer.sizeColumnsToFit(table_data_rekap_cabut);
                label_total_upah_rekap_cabut.setText(decimalFormat.format(total_upah_rekap_cabut));
            }
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_PenggajianSub.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_data_penyesek() {
        try {
            Utility.db_sub.connect();
            float total_gram_sesekan = 0, total_upah_sesekan = 0;
            decimalFormat.setMaximumFractionDigits(2);
            DefaultTableModel model = (DefaultTableModel) table_data_pekerja_sesekan.getModel();
            model.setRowCount(0);
            String search_sub = " AND `tb_laporan_produksi_sesekan`.`sub` = '" + ComboBox_sub.getSelectedItem().toString() + "'";
            if (ComboBox_sub.getSelectedItem().toString().equals("All")) {
                search_sub = "";
            }
            if (Date_kinerja_borong1.getDate() != null && Date_kinerja_borong2.getDate() != null) {
                String query = "SELECT `tb_detail_penyesek`.`id_pegawai`, `nama_pegawai`, `bagian`, `tb_detail_penyesek`.`no_lp_sesekan`, `bulu_upah`, "
                        + "`tb_detail_penyesek`.`tanggal_input`, DATE(`waktu_setor_lp`) AS 'tgl_setor_sesekan', `gram_sesekan`, `nilai_sesekan` \n"
                        + "FROM `tb_detail_penyesek` \n"
                        + "LEFT JOIN `tb_laporan_produksi_sesekan` ON `tb_detail_penyesek`.`no_lp_sesekan` = `tb_laporan_produksi_sesekan`.`no_lp_sesekan`\n"
                        + "LEFT JOIN `tb_karyawan` ON `tb_detail_penyesek`.`id_pegawai` = `tb_karyawan`.`id_pegawai`\n"
                        + "WHERE `tb_karyawan`.`nama_pegawai` LIKE '%" + txt_search_nama.getText() + "%' "
                        + "AND `tb_detail_penyesek`.`no_lp_sesekan` LIKE '%" + txt_search_no_lp.getText() + "%' "
                        + search_sub
                        + "AND (DATE(`waktu_setor_lp`) BETWEEN '" + dateFormat.format(Date_kinerja_borong1.getDate()) + "' AND '" + dateFormat.format(Date_kinerja_borong2.getDate()) + "') ";
                rs = Utility.db_sub.getStatement().executeQuery(query);
                Object[] row = new Object[15];
                while (rs.next()) {
                    row[0] = rs.getString("id_pegawai");
                    row[1] = rs.getString("nama_pegawai");
                    row[2] = rs.getString("bagian");
                    row[3] = rs.getString("no_lp_sesekan");
                    row[4] = rs.getString("bulu_upah");
                    row[5] = rs.getDate("tanggal_input");
                    row[6] = rs.getDate("tgl_setor_sesekan");
                    row[7] = rs.getFloat("gram_sesekan");
                    row[8] = rs.getInt("nilai_sesekan");
                    model.addRow(row);
                    total_gram_sesekan = total_gram_sesekan + rs.getFloat("gram_sesekan");
                    total_upah_sesekan = total_upah_sesekan + rs.getFloat("nilai_sesekan");
                }
                refreshTable_rekap_penyesek(query);
                ColumnsAutoSizer.sizeColumnsToFit(table_data_pekerja_sesekan);
                label_total_gram_sesekan.setText(decimalFormat.format(total_gram_sesekan));
                label_total_upah_sesekan.setText(decimalFormat.format(total_upah_sesekan));
            } else {
                JOptionPane.showMessageDialog(this, "Filter Tanggal harus dipilih!");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_PenggajianSub.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_rekap_penyesek(String query) {
        try {
            float total_upah_rekap_sesekan = 0;
            decimalFormat.setMaximumFractionDigits(2);
            DefaultTableModel model = (DefaultTableModel) table_data_rekap_sesekan.getModel();
            model.setRowCount(0);
            if (query != null && !query.equals("")) {
                String qry = "SELECT `tgl_setor_sesekan`, `id_pegawai`, `nama_pegawai`, `bagian`, SUM(`gram_sesekan`) AS 'jumlah_gram', SUM(`nilai_sesekan`) AS 'upah_sesekan' \n"
                        + "FROM (" + query + ") detail "
                        + "GROUP BY `id_pegawai`, `tgl_setor_sesekan`";
                rs = Utility.db_sub.getStatement().executeQuery(qry);
                Object[] row = new Object[15];
                while (rs.next()) {
                    row[0] = rs.getDate("tgl_setor_sesekan");
                    row[1] = rs.getString("id_pegawai");
                    row[2] = rs.getString("nama_pegawai");
                    row[3] = rs.getString("bagian");
                    row[4] = rs.getFloat("jumlah_gram");
                    row[5] = rs.getFloat("upah_sesekan");
                    model.addRow(row);
                    total_upah_rekap_sesekan = total_upah_rekap_sesekan + rs.getFloat("upah_sesekan");
                }
                ColumnsAutoSizer.sizeColumnsToFit(table_data_rekap_sesekan);
                label_total_upah_rekap_sesekan.setText(decimalFormat.format(total_upah_rekap_sesekan));
            }
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_PenggajianSub.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_data_sapon() {
        try {
            Utility.db_sub.connect();
            float total_gram_sapon = 0, total_upah_sapon = 0;
            decimalFormat.setMaximumFractionDigits(2);
            DefaultTableModel model = (DefaultTableModel) table_data_pekerja_sapon.getModel();
            model.setRowCount(0);
            String search_sub = " AND `tb_laporan_produksi_sapon`.`sub` = '" + ComboBox_sub.getSelectedItem().toString() + "'";
            if (ComboBox_sub.getSelectedItem().toString().equals("All")) {
                search_sub = "";
            }
            if (Date_kinerja_borong1.getDate() != null && Date_kinerja_borong2.getDate() != null) {
                String query = "SELECT `tb_detail_pekerja_sapon`.`id_pegawai`, `nama_pegawai`, `bagian`, `tb_detail_pekerja_sapon`.`no_lp_sapon`, `bulu_upah`, "
                        + "`tb_detail_pekerja_sapon`.`tanggal_input`, DATE(`waktu_setor_lp`) AS 'tgl_setor_sapon', `tb_detail_pekerja_sapon`.`gram_sapon`, `nilai_sapon` \n"
                        + "FROM `tb_detail_pekerja_sapon` \n"
                        + "LEFT JOIN `tb_laporan_produksi_sapon` ON `tb_detail_pekerja_sapon`.`no_lp_sapon` = `tb_laporan_produksi_sapon`.`no_lp_sapon`\n"
                        + "LEFT JOIN `tb_karyawan` ON `tb_detail_pekerja_sapon`.`id_pegawai` = `tb_karyawan`.`id_pegawai`\n"
                        + "WHERE `tb_karyawan`.`nama_pegawai` LIKE '%" + txt_search_nama.getText() + "%' "
                        + "AND `tb_detail_pekerja_sapon`.`no_lp_sapon` LIKE '%" + txt_search_no_lp.getText() + "%' "
                        + search_sub
                        + "AND (DATE(`waktu_setor_lp`) BETWEEN '" + dateFormat.format(Date_kinerja_borong1.getDate()) + "' AND '" + dateFormat.format(Date_kinerja_borong2.getDate()) + "') ";
                rs = Utility.db_sub.getStatement().executeQuery(query);
                Object[] row = new Object[15];
                while (rs.next()) {
                    row[0] = rs.getString("id_pegawai");
                    row[1] = rs.getString("nama_pegawai");
                    row[2] = rs.getString("bagian");
                    row[3] = rs.getString("no_lp_sapon");
                    row[4] = rs.getString("bulu_upah");
                    row[5] = rs.getDate("tanggal_input");
                    row[6] = rs.getDate("tgl_setor_sapon");
                    row[7] = rs.getFloat("gram_sapon");
                    row[8] = rs.getInt("nilai_sapon");
                    model.addRow(row);
                    total_gram_sapon = total_gram_sapon + rs.getFloat("gram_sapon");
                    total_upah_sapon = total_upah_sapon + rs.getFloat("nilai_sapon");
                }
                refreshTable_rekap_sapon(query);
                ColumnsAutoSizer.sizeColumnsToFit(table_data_pekerja_sapon);
                label_total_gram_sapon.setText(decimalFormat.format(total_gram_sapon));
                label_total_upah_sapon.setText(decimalFormat.format(total_upah_sapon));
            } else {
                JOptionPane.showMessageDialog(this, "Filter Tanggal harus dipilih!");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_PenggajianSub.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_rekap_sapon(String query) {
        try {
            float total_upah_rekap_sapon = 0;
            decimalFormat.setMaximumFractionDigits(2);
            DefaultTableModel model = (DefaultTableModel) table_data_rekap_sapon.getModel();
            model.setRowCount(0);
            if (query != null && !query.equals("")) {
                String qry = "SELECT `tgl_setor_sapon`, `id_pegawai`, `nama_pegawai`, `bagian`, SUM(`gram_sapon`) AS 'jumlah_gram', SUM(`nilai_sapon`) AS 'upah_sapon' \n"
                        + "FROM (" + query + ") detail "
                        + "GROUP BY `id_pegawai`, `tgl_setor_sapon`";
                rs = Utility.db_sub.getStatement().executeQuery(qry);
                Object[] row = new Object[15];
                while (rs.next()) {
                    row[0] = rs.getDate("tgl_setor_sapon");
                    row[1] = rs.getString("id_pegawai");
                    row[2] = rs.getString("nama_pegawai");
                    row[3] = rs.getString("bagian");
                    row[4] = rs.getFloat("jumlah_gram");
                    row[5] = rs.getFloat("upah_sapon");
                    model.addRow(row);
                    total_upah_rekap_sapon = total_upah_rekap_sapon + rs.getFloat("upah_sapon");
                }
                ColumnsAutoSizer.sizeColumnsToFit(table_data_rekap_sapon);
                label_total_upah_rekap_sapon.setText(decimalFormat.format(total_upah_rekap_sapon));
            }
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_PenggajianSub.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refresh_BonusLP_Sub() {
        boolean check = true;
        Date tanggal_mulai = new Date(Date_tutupan_bonusCabut.getDate().getTime() - (6 * 24 * 60 * 60 * 1000));
        Date tanggal_selesai = new Date(Date_tutupan_bonusCabut.getDate().getTime() - (0 * 24 * 60 * 60 * 1000));
        double jumlah_hari_kerja = Utility.countDaysWithoutFreeDays(tanggal_mulai, tanggal_selesai) + 1;
        txt_jumlah_hari_kerja_bonus.setText(decimalFormat.format(jumlah_hari_kerja));
        double min_lp_dikerjakan = (jumlah_hari_kerja / 6d) * 3d;
        txt_min_lp_dikerjakan.setText(decimalFormat.format(min_lp_dikerjakan));
        if (Date_tutupan_bonusCabut.getDate() == null) {
            check = false;
            JOptionPane.showMessageDialog(this, "Harap masukkan tanggal tutupan");
        } else if (!(new SimpleDateFormat("EEEEE").format(Date_tutupan_bonusCabut.getDate()).toUpperCase().equals("SATURDAY") || new SimpleDateFormat("EEEEE").format(Date_tutupan_bonusCabut.getDate()).toUpperCase().equals("SABTU"))) {
            check = false;
            JOptionPane.showMessageDialog(this, "Tanggal tutupan seharusnya hari SABTU");
        }
        if (check) {
            upah_cabut_per_sub = new HashMap<>();
            bonus_cabut_per_sub = new HashMap<>();
            upah_cuci_per_karyawan = new HashMap<>();
            bonus_cuci_per_karyawan = new HashMap<>();
            upah_sesekan_per_sub = new HashMap<>();
            bonus_sesekan_per_sub = new HashMap<>();
            upah_sapon_per_sub = new HashMap<>();
            bonus_sapon_per_sub = new HashMap<>();

            refresh_BonusCabut_Sub();
            refresh_BonusSesekan_Sub();
            refresh_BonusSapon_Sub();
            refresh_tabel_rekap_bonus_lp(tanggal_mulai, tanggal_selesai, min_lp_dikerjakan);
        }

    }

    public void refresh_BonusCabut_Sub() {
        double total_gram_lp = 0, total_bobot_lp = 0, total_upah_cabut = 0, total_bonus_cabut = 0;
        Date tanggal_mulai = new Date(Date_tutupan_bonusCabut.getDate().getTime() - (6 * 24 * 60 * 60 * 1000));
        Date tanggal_selesai = new Date(Date_tutupan_bonusCabut.getDate().getTime() - (0 * 24 * 60 * 60 * 1000));
        try {
            Utility.db_sub.connect();
            DefaultTableModel model = (DefaultTableModel) table_data_Bonus_LP_WL.getModel();
            model.setRowCount(0);
            String search_sub = " AND `tb_laporan_produksi`.`ruangan` = '" + ComboBox_sub_bonusCabut.getSelectedItem().toString() + "'";
            if (ComboBox_sub_bonusCabut.getSelectedItem().toString().equals("All")) {
                search_sub = "";
            }
            sql = "SELECT `tb_laporan_produksi`.`no_laporan_produksi`, `kode_grade`, `ruangan`, `jenis_bulu_lp`, `tb_cabut`.`tgl_setor_cabut`, `tb_laporan_produksi`.`jumlah_keping`, "
                    + "`keping_upah`, `berat_basah`, "
                    + "IF(`kode_grade` = 'KK/KULIT', (`berat_basah` / `bobot_gram_lp`), (`keping_upah` / `bobot_1_lp`)) AS 'bobot_lp', "
                    + "IF(`kode_grade` = 'KK/KULIT', (`berat_basah` * `bonus_sesekan`), (`berat_basah` * `bonus_cabut`)) AS 'bonus_cabut', "
                    + "(`berat_basah` * `upah_cabut`) AS 'upah_cabut', "
                    + "(`berat_basah` * `upah_cuci`) AS 'upah_cuci', "
                    + "(`berat_basah` * `bonus_cuci`) AS 'bonus_cuci' "
                    + "FROM `tb_laporan_produksi` "
                    + "LEFT JOIN `tb_tarif_upah` ON `tb_laporan_produksi`.`jenis_bulu_lp` = `tb_tarif_upah`.`bulu_upah` "
                    + "LEFT JOIN `tb_cabut` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_cabut`.`no_laporan_produksi` "
                    + "WHERE `tb_laporan_produksi`.`no_laporan_produksi` LIKE 'WL.%' "
                    + "AND `tb_laporan_produksi`.`no_laporan_produksi` LIKE '%" + txt_search_no_lp_bonusCabut.getText() + "%' "
                    + search_sub
                    + "AND (`tgl_setor_cabut` BETWEEN '" + dateFormat.format(tanggal_mulai) + "' AND '" + dateFormat.format(tanggal_selesai) + "') ";
            rs = Utility.db_sub.getStatement().executeQuery(sql);
            rs.last();
            int total_data = rs.getRow();
            rs.beforeFirst();
            while (rs.next()) {
                Object[] row = new Object[15];
                jProgressBar1.setValue((rs.getRow() / total_data) * 100 * (1 / 3));
                row[0] = rs.getString("no_laporan_produksi");
                row[1] = rs.getString("kode_grade");
                row[2] = rs.getString("ruangan");
                row[3] = rs.getString("jenis_bulu_lp");
                row[4] = rs.getDate("tgl_setor_cabut");
                row[5] = rs.getFloat("jumlah_keping");
                row[6] = rs.getFloat("keping_upah");
                row[7] = rs.getFloat("berat_basah");
                double bobot_Lp = Math.round(rs.getFloat("bobot_lp") * 100d) / 100d;
                row[8] = bobot_Lp;
                row[9] = rs.getFloat("upah_cabut");//upah cabut 1 lp
                row[10] = rs.getFloat("bonus_cabut");
                model.addRow(row);
                total_gram_lp = total_gram_lp + rs.getInt("berat_basah");
                total_bobot_lp = total_bobot_lp + Math.round(rs.getDouble("bobot_lp") * 100d) / 100d;
                total_bonus_cabut = total_bonus_cabut + rs.getInt("bonus_cabut");
                total_upah_cabut = total_upah_cabut + rs.getDouble("upah_cabut");

                upah_cabut_per_sub.put(rs.getString("ruangan"), upah_cabut_per_sub.getOrDefault(rs.getString("ruangan"), 0d) + rs.getDouble("upah_cabut"));
                bonus_cabut_per_sub.put(rs.getString("ruangan"), bonus_cabut_per_sub.getOrDefault(rs.getString("ruangan"), 0d) + rs.getDouble("bonus_cabut"));
                upah_cuci_per_karyawan.put(rs.getString("ruangan"), upah_cuci_per_karyawan.getOrDefault(rs.getString("ruangan"), 0d) + rs.getDouble("upah_cuci"));
                bonus_cuci_per_karyawan.put(rs.getString("ruangan"), bonus_cuci_per_karyawan.getOrDefault(rs.getString("ruangan"), 0d) + rs.getDouble("bonus_cuci"));
            }

            ColumnsAutoSizer.sizeColumnsToFit(table_data_Bonus_LP_WL);
            decimalFormat.setMaximumFractionDigits(2);
            label_bonusCabut_total_LP.setText(decimalFormat.format(table_data_Bonus_LP_WL.getRowCount()));
            label_bonusCabut_total_gramLP.setText(decimalFormat.format(total_gram_lp));
            label_bonusCabut_total_bobotLP.setText(decimalFormat.format(total_bobot_lp));
            label_bonusCabut_total_bonusLP.setText(decimalFormat.format(total_bonus_cabut));
            label_bonusCabut_total_nilaiLP.setText(decimalFormat.format(total_upah_cabut));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_PenggajianSub.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refresh_BonusSesekan_Sub() {
        double total_gram_lp_bp = 0, total_bobot_lp_bp = 0, total_upah_cabut_lp_bp = 0, total_bonus_lp_bp = 0;
        Date tanggal_mulai = new Date(Date_tutupan_bonusCabut.getDate().getTime() - (6 * 24 * 60 * 60 * 1000));
        Date tanggal_selesai = new Date(Date_tutupan_bonusCabut.getDate().getTime() - (0 * 24 * 60 * 60 * 1000));

        try {
            Utility.db_sub.connect();
            DefaultTableModel model = (DefaultTableModel) table_data_Bonus_LP_BP.getModel();
            model.setRowCount(0);
            String search_sub = " AND `sub` = '" + ComboBox_sub_bonusCabut.getSelectedItem().toString() + "'";
            if (ComboBox_sub_bonusCabut.getSelectedItem().toString().equals("All")) {
                search_sub = "";
            }
            sql = "SELECT `no_lp_sesekan`, `kode_grade`, `sub`, `tb_laporan_produksi_sesekan`.`bulu_upah`, DATE(`waktu_setor_lp`) AS 'tgl_setor', `keping`, `sesekan_bersih`, "
                    + "(`sesekan_bersih` / `tb_tarif_upah`.`bobot_gram_lp`) AS 'bobot_lp', `nilai_lp`,  "
                    + "(`sesekan_bersih` * `bonus_sesekan`) AS 'bonus_lp', "
                    + "(`sesekan_bersih` * `upah_cuci`) AS 'upah_cuci', "
                    + "(`sesekan_bersih` * `bonus_cuci`) AS 'bonus_cuci' "
                    + "FROM `tb_laporan_produksi_sesekan` "
                    + "LEFT JOIN `tb_tarif_upah` ON `tb_laporan_produksi_sesekan`.`bulu_upah` = `tb_tarif_upah`.`bulu_upah` "
                    + "WHERE `no_lp_sesekan` LIKE '%" + txt_search_no_lp_bonusCabut.getText() + "%' "
                    + search_sub
                    + "AND (DATE(`waktu_setor_lp`) BETWEEN '" + dateFormat.format(tanggal_mulai) + "' AND '" + dateFormat.format(tanggal_selesai) + "') ";
            rs = Utility.db_sub.getStatement().executeQuery(sql);
            Object[] row = new Object[15];
            while (rs.next()) {
                row[0] = rs.getString("no_lp_sesekan");
                row[1] = rs.getString("kode_grade");
                row[2] = rs.getString("sub");
                row[3] = rs.getString("bulu_upah");
                row[4] = rs.getDate("tgl_setor");
                row[5] = rs.getFloat("keping");
                row[6] = rs.getFloat("sesekan_bersih");
                double bobot_Lp = Math.round(rs.getFloat("bobot_lp") * 100d) / 100d;
                row[7] = bobot_Lp;
                row[8] = rs.getFloat("nilai_lp");//upah cabut 1 lp
                row[9] = rs.getFloat("bonus_lp");
                model.addRow(row);
                total_gram_lp_bp = total_gram_lp_bp + rs.getInt("sesekan_bersih");
                total_bobot_lp_bp = total_bobot_lp_bp + bobot_Lp;
                total_bonus_lp_bp = total_bonus_lp_bp + rs.getInt("bonus_lp");
                total_upah_cabut_lp_bp = total_upah_cabut_lp_bp + rs.getDouble("nilai_lp");

                if (upah_sesekan_per_sub.containsKey(rs.getString("sub"))) {
                    upah_sesekan_per_sub.put(rs.getString("sub"), upah_sesekan_per_sub.get(rs.getString("sub")) + rs.getDouble("nilai_lp"));
                } else {
                    upah_sesekan_per_sub.put(rs.getString("sub"), rs.getDouble("nilai_lp"));
                }

                if (bonus_sesekan_per_sub.containsKey(rs.getString("sub"))) {
                    bonus_sesekan_per_sub.put(rs.getString("sub"), bonus_sesekan_per_sub.get(rs.getString("sub")) + rs.getDouble("bonus_lp"));
                } else {
                    bonus_sesekan_per_sub.put(rs.getString("sub"), rs.getDouble("bonus_lp"));
                }

                if (upah_cuci_per_karyawan.containsKey(rs.getString("sub"))) {
                    upah_cuci_per_karyawan.put(rs.getString("sub"), upah_cuci_per_karyawan.get(rs.getString("sub")) + rs.getDouble("upah_cuci"));
                } else {
                    upah_cuci_per_karyawan.put(rs.getString("sub"), rs.getDouble("upah_cuci"));
                }

                if (bonus_cuci_per_karyawan.containsKey(rs.getString("sub"))) {
                    bonus_cuci_per_karyawan.put(rs.getString("sub"), bonus_cuci_per_karyawan.get(rs.getString("sub")) + rs.getDouble("bonus_cuci"));
                } else {
                    bonus_cuci_per_karyawan.put(rs.getString("sub"), rs.getDouble("bonus_cuci"));
                }
            }
            ColumnsAutoSizer.sizeColumnsToFit(table_data_Bonus_LP_BP);
            decimalFormat.setMaximumFractionDigits(2);
            label_bonusCabut_total_LP_BP.setText(decimalFormat.format(table_data_Bonus_LP_BP.getRowCount()));
            label_bonusCabut_total_gramLP_BP.setText(decimalFormat.format(total_gram_lp_bp));
            label_bonusCabut_total_bobotLP_BP.setText(decimalFormat.format(total_bobot_lp_bp));
            label_bonusCabut_total_bonus_LP_BP.setText(decimalFormat.format(total_bonus_lp_bp));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_PenggajianSub.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refresh_BonusSapon_Sub() {
        double total_gram_lp_sp = 0, total_bobot_lp_sp = 0, total_upah_cabut_lp_sp = 0, total_bonus_lp_sp = 0;
        Date tanggal_mulai = new Date(Date_tutupan_bonusCabut.getDate().getTime() - (6 * 24 * 60 * 60 * 1000));
        Date tanggal_selesai = new Date(Date_tutupan_bonusCabut.getDate().getTime() - (0 * 24 * 60 * 60 * 1000));

        try {
            Utility.db_sub.connect();
            DefaultTableModel model = (DefaultTableModel) table_data_Bonus_LP_SP.getModel();
            model.setRowCount(0);
            String search_sub = " AND `sub` = '" + ComboBox_sub_bonusCabut.getSelectedItem().toString() + "'";
            if (ComboBox_sub_bonusCabut.getSelectedItem().toString().equals("All")) {
                search_sub = "";
            }
            sql = "SELECT `no_lp_sapon`, `kode_grade`, `sub`, `tb_laporan_produksi_sapon`.`bulu_upah`, DATE(`waktu_setor_lp`) AS 'tgl_setor', `keping`, `gram_sapon`, "
                    + "(`gram_sapon` / `tb_tarif_upah`.`bobot_gram_lp`) AS 'bobot_lp', `nilai_lp`,  "
                    + "(`gram_sapon` * `bonus_sesekan`) AS 'bonus_lp', "
                    + "(`gram_sapon` * `upah_cuci`) AS 'upah_cuci', "
                    + "(`gram_sapon` * `bonus_cuci`) AS 'bonus_cuci' "
                    + "FROM `tb_laporan_produksi_sapon` "
                    + "LEFT JOIN `tb_tarif_upah` ON `tb_laporan_produksi_sapon`.`bulu_upah` = `tb_tarif_upah`.`bulu_upah` "
                    + "WHERE `no_lp_sapon` LIKE '%" + txt_search_no_lp_bonusCabut.getText() + "%' "
                    + search_sub
                    + "AND (DATE(`waktu_setor_lp`) BETWEEN '" + dateFormat.format(tanggal_mulai) + "' AND '" + dateFormat.format(tanggal_selesai) + "') ";
            rs = Utility.db_sub.getStatement().executeQuery(sql);
            Object[] row = new Object[15];
            while (rs.next()) {
                row[0] = rs.getString("no_lp_sapon");
                row[1] = rs.getString("kode_grade");
                row[2] = rs.getString("sub");
                row[3] = rs.getString("bulu_upah");
                row[4] = rs.getDate("tgl_setor");
                row[5] = rs.getFloat("keping");
                row[6] = rs.getFloat("gram_sapon");
                double bobot_Lp = Math.round(rs.getFloat("bobot_lp") * 100d) / 100d;
                row[7] = bobot_Lp;
                row[8] = rs.getFloat("nilai_lp");//upah cabut 1 lp
                row[9] = rs.getFloat("bonus_lp");
                model.addRow(row);
                total_gram_lp_sp = total_gram_lp_sp + rs.getInt("gram_sapon");
                total_bobot_lp_sp = total_bobot_lp_sp + bobot_Lp;
                total_bonus_lp_sp = total_bonus_lp_sp + rs.getInt("bonus_lp");
                total_upah_cabut_lp_sp = total_upah_cabut_lp_sp + rs.getDouble("nilai_lp");

                if (upah_sapon_per_sub.containsKey(rs.getString("sub"))) {
                    upah_sapon_per_sub.put(rs.getString("sub"), upah_sapon_per_sub.get(rs.getString("sub")) + rs.getDouble("nilai_lp"));
                } else {
                    upah_sapon_per_sub.put(rs.getString("sub"), rs.getDouble("nilai_lp"));
                }

                if (bonus_sapon_per_sub.containsKey(rs.getString("sub"))) {
                    bonus_sapon_per_sub.put(rs.getString("sub"), bonus_sapon_per_sub.get(rs.getString("sub")) + rs.getDouble("bonus_lp"));
                } else {
                    bonus_sapon_per_sub.put(rs.getString("sub"), rs.getDouble("bonus_lp"));
                }

                if (upah_cuci_per_karyawan.containsKey(rs.getString("sub"))) {
                    upah_cuci_per_karyawan.put(rs.getString("sub"), upah_cuci_per_karyawan.get(rs.getString("sub")) + rs.getDouble("upah_cuci"));
                } else {
                    upah_cuci_per_karyawan.put(rs.getString("sub"), rs.getDouble("upah_cuci"));
                }

                if (bonus_cuci_per_karyawan.containsKey(rs.getString("sub"))) {
                    bonus_cuci_per_karyawan.put(rs.getString("sub"), bonus_cuci_per_karyawan.get(rs.getString("sub")) + rs.getDouble("bonus_cuci"));
                } else {
                    bonus_cuci_per_karyawan.put(rs.getString("sub"), rs.getDouble("bonus_cuci"));
                }
            }
            ColumnsAutoSizer.sizeColumnsToFit(table_data_Bonus_LP_SP);
            decimalFormat.setMaximumFractionDigits(2);
            label_bonusCabut_total_LP_SP.setText(decimalFormat.format(table_data_Bonus_LP_SP.getRowCount()));
            label_bonusCabut_total_gramLP_SP.setText(decimalFormat.format(total_gram_lp_sp));
            label_bonusCabut_total_bobotLP_SP.setText(decimalFormat.format(total_bobot_lp_sp));
            label_bonusCabut_total_bonus_LP_SP.setText(decimalFormat.format(total_bonus_lp_sp));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_PenggajianSub.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refresh_tabel_rekap_bonus_lp(Date tanggal_mulai, Date tanggal_selesai, double min_lp_dikerjakan) {
        try {
            Utility.db_sub.connect();
            double total_bonus_cabut_per_sub = 0;
            DefaultTableModel model = (DefaultTableModel) table_data_RekapBonus_LP_Sub.getModel();
            model.setRowCount(0);
            total_gram_lp_cabut_dikerjakan_per_sub = new HashMap<>();
            total_gram_lp_sesekan_dikerjakan_per_sub = new HashMap<>();
            total_gram_lp_sapon_dikerjakan_per_sub = new HashMap<>();
            total_bobot_cabut_dikerjakan_per_sub = new HashMap<>();
            total_bobot_sesekan_dikerjakan_per_sub = new HashMap<>();
            total_bobot_sapon_dikerjakan_per_sub = new HashMap<>();

            upah_cuci_per_sub = new HashMap<>();
            bonus_cuci_per_sub = new HashMap<>();
            bonus_MKU_per_sub = new HashMap<>();
            subsidi_training_per_sub = new HashMap<>();
            tunjangan_hadir_per_sub = new HashMap<>();
            piutang_per_sub = new HashMap<>();
            total_gaji_karyawan_per_sub = new HashMap<>();
            String qry = "SELECT `kode_sub`, `total_gram_lp_wl`, `total_gram_lp_bp`, `total_gram_lp_sp`, `bobot_lp_wl`, `bobot_lp_bp`, `bobot_lp_sp` \n"
                    + "FROM `tb_sub_waleta` \n"
                    + "LEFT JOIN (\n"
                    + "SELECT `ruangan`, SUM(`berat_basah`) AS 'total_gram_lp_wl', SUM(IF(`kode_grade` = 'KK/KULIT', (`berat_basah` / `bobot_gram_lp`), (`keping_upah` / `bobot_1_lp`))) AS 'bobot_lp_wl' FROM `tb_laporan_produksi` \n"
                    + "LEFT JOIN `tb_cabut` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_cabut`.`no_laporan_produksi` \n"
                    + "LEFT JOIN `tb_tarif_upah` ON `tb_laporan_produksi`.`jenis_bulu_lp` = `tb_tarif_upah`.`bulu_upah` \n"
                    + "WHERE `tgl_setor_cabut` BETWEEN '" + dateFormat.format(tanggal_mulai) + "' AND '" + dateFormat.format(tanggal_selesai) + "' GROUP BY `ruangan`\n"
                    + ") WL ON `tb_sub_waleta`.`kode_sub` = WL.`ruangan`\n"
                    + "LEFT JOIN (\n"
                    + "SELECT `sub`, SUM(`sesekan_bersih`) AS 'total_gram_lp_bp', SUM(`sesekan_bersih` / `bobot_gram_lp`) AS 'bobot_lp_bp' FROM `tb_laporan_produksi_sesekan` \n"
                    + "LEFT JOIN `tb_tarif_upah` ON `tb_laporan_produksi_sesekan`.`bulu_upah` = `tb_tarif_upah`.`bulu_upah` \n"
                    + "WHERE DATE(`waktu_setor_lp`) BETWEEN '" + dateFormat.format(tanggal_mulai) + "' AND '" + dateFormat.format(tanggal_selesai) + "' GROUP BY `sub`\n"
                    + ") BP ON `tb_sub_waleta`.`kode_sub` = BP.`sub`\n"
                    + "LEFT JOIN (\n"
                    + "SELECT `sub`, SUM(`gram_sapon`) AS 'total_gram_lp_sp', SUM(`gram_sapon` / `bobot_gram_lp`) AS 'bobot_lp_sp' FROM `tb_laporan_produksi_sapon` \n"
                    + "LEFT JOIN `tb_tarif_upah` ON `tb_laporan_produksi_sapon`.`bulu_upah` = `tb_tarif_upah`.`bulu_upah` \n"
                    + "WHERE DATE(`waktu_setor_lp`) BETWEEN '" + dateFormat.format(tanggal_mulai) + "' AND '" + dateFormat.format(tanggal_selesai) + "' GROUP BY `sub`\n"
                    + ") SP ON `tb_sub_waleta`.`kode_sub` = SP.`sub`\n"
                    + "WHERE `kode_sub` LIKE '%" + txt_search_sub.getText() + "%'";
            rs = Utility.db_sub.getStatement().executeQuery(qry);
            Object[] row = new Object[15];
            while (rs.next()) {
                total_gram_lp_cabut_dikerjakan_per_sub.put(rs.getString("kode_sub"), rs.getDouble("total_gram_lp_wl"));
                total_gram_lp_sesekan_dikerjakan_per_sub.put(rs.getString("kode_sub"), rs.getDouble("total_gram_lp_bp"));
                total_gram_lp_sapon_dikerjakan_per_sub.put(rs.getString("kode_sub"), rs.getDouble("total_gram_lp_sp"));
                total_bobot_cabut_dikerjakan_per_sub.put(rs.getString("kode_sub"), rs.getDouble("bobot_lp_wl"));
                total_bobot_sesekan_dikerjakan_per_sub.put(rs.getString("kode_sub"), rs.getDouble("bobot_lp_bp"));
                total_bobot_sapon_dikerjakan_per_sub.put(rs.getString("kode_sub"), rs.getDouble("bobot_lp_sp"));
                upah_cuci_per_sub.put(rs.getString("kode_sub"), 0L);
                bonus_cuci_per_sub.put(rs.getString("kode_sub"), 0L);
                bonus_MKU_per_sub.put(rs.getString("kode_sub"), 0L);
                subsidi_training_per_sub.put(rs.getString("kode_sub"), 0L);
                tunjangan_hadir_per_sub.put(rs.getString("kode_sub"), 0L);
                piutang_per_sub.put(rs.getString("kode_sub"), 0L);
                total_gaji_karyawan_per_sub.put(rs.getString("kode_sub"), 0L);

                double total_gram_dikerjakan = total_gram_lp_cabut_dikerjakan_per_sub.get(rs.getString("kode_sub")) + total_gram_lp_sesekan_dikerjakan_per_sub.get(rs.getString("kode_sub")) + total_gram_lp_sapon_dikerjakan_per_sub.get(rs.getString("kode_sub"));
                double total_bobot_dikerjakan = total_bobot_cabut_dikerjakan_per_sub.get(rs.getString("kode_sub")) + total_bobot_sesekan_dikerjakan_per_sub.get(rs.getString("kode_sub")) + total_bobot_sapon_dikerjakan_per_sub.get(rs.getString("kode_sub"));

                row[0] = rs.getString("kode_sub");
                row[1] = total_gram_dikerjakan;
                row[2] = total_bobot_cabut_dikerjakan_per_sub.get(rs.getString("kode_sub"));
                row[3] = total_bobot_sesekan_dikerjakan_per_sub.get(rs.getString("kode_sub"));
                row[4] = total_bobot_sapon_dikerjakan_per_sub.get(rs.getString("kode_sub"));
                row[5] = total_bobot_dikerjakan;
                if (total_bobot_dikerjakan < min_lp_dikerjakan) {
                    bonus_cabut_per_sub.put(rs.getString("kode_sub"), 0d);
                    bonus_sesekan_per_sub.put(rs.getString("kode_sub"), 0d);
                    bonus_sapon_per_sub.put(rs.getString("kode_sub"), 0d);
                }
                double total_bonus_lp = bonus_cabut_per_sub.getOrDefault(rs.getString("kode_sub"), 0d) + bonus_sesekan_per_sub.getOrDefault(rs.getString("kode_sub"), 0d) + bonus_sapon_per_sub.getOrDefault(rs.getString("kode_sub"), 0d);
                row[6] = bonus_cabut_per_sub.getOrDefault(rs.getString("kode_sub"), 0d);
                row[7] = bonus_sesekan_per_sub.getOrDefault(rs.getString("kode_sub"), 0d);
                row[8] = bonus_sapon_per_sub.getOrDefault(rs.getString("kode_sub"), 0d);
                row[9] = total_bonus_lp;
                row[10] = upah_cuci_per_karyawan.getOrDefault(rs.getString("kode_sub"), 0d);
                row[11] = bonus_cuci_per_karyawan.getOrDefault(rs.getString("kode_sub"), 0d);
                total_bonus_cabut_per_sub = total_bonus_cabut_per_sub + total_bonus_lp;
                model.addRow(row);
            }

            ColumnsAutoSizer.sizeColumnsToFit(table_data_RekapBonus_LP_Sub);
            label_total_bonusCabut_Sub.setText(decimalFormat.format(total_bonus_cabut_per_sub));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_PenggajianSub.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refresh_BonusMKU_Cabut_Sub(Date tanggal_mulai, Date tanggal_selesai) {
        try {
            Utility.db_sub.connect();
            double total_bonus_mku_cabut = 0, total_bonus_mku_karyawan = 0;
            DefaultTableModel model = (DefaultTableModel) table_bonusMKU_Cabut.getModel();
            model.setRowCount(0);
            String search_sub = " AND `tb_laporan_produksi`.`ruangan` = '" + ComboBox_sub_bonusMKU.getSelectedItem().toString() + "'";
            if (ComboBox_sub_bonusMKU.getSelectedItem().toString().equals("All")) {
                search_sub = "";
            }
            sql = "SELECT `tb_finishing_2`.`no_laporan_produksi`, `tb_laporan_produksi`.`kode_grade`, `tb_grade_bahan_baku`.`kategori`, `ruangan`, `jenis_bulu_lp`, `tgl_setor_f2`, `fbonus_f2`, `fnol_f2`, `pecah_f2`, `flat_f2`, `bonus_mk_utuh_cabut_sub`, "
                    + "IF(`tb_grade_bahan_baku`.`jenis_bentuk` IN ('Mangkok', 'Oval'), ((`fbonus_f2` + `fnol_f2`) - (`pecah_f2` + `flat_f2`)), (`fbonus_f2` + `fnol_f2`))  AS 'kpg_bonus', "
                    + "IF(`tb_grade_bahan_baku`.`jenis_bentuk` IN ('Mangkok', 'Oval'), ((`fbonus_f2` + `fnol_f2`) - (`pecah_f2` + `flat_f2`)) * `bonus_mk_utuh_cabut_sub`, (`fbonus_f2` + `fnol_f2`) * `bonus_mk_utuh_cabut_sub`)  AS 'bonus_mku_cabut' "
                    + "FROM `tb_finishing_2` \n"
                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_finishing_2`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_grade_bahan_baku` ON `tb_laporan_produksi`.`kode_grade` = `tb_grade_bahan_baku`.`kode_grade`\n"
                    + "LEFT JOIN `tb_tarif_cabut` ON `tb_laporan_produksi`.`jenis_bulu_lp` = `tb_tarif_cabut`.`bulu_upah`\n"
                    + "WHERE `tb_finishing_2`.`no_laporan_produksi` LIKE 'WL.%' "
                    + "AND `tb_finishing_2`.`no_laporan_produksi` LIKE '%" + txt_search_no_lp_bonusMKU.getText() + "%' "
                    + search_sub
                    + "AND (`tgl_setor_f2` BETWEEN '" + dateFormat.format(tanggal_mulai) + "' AND '" + dateFormat.format(tanggal_selesai) + "')"
                    + "ORDER BY `tb_finishing_2`.`no_laporan_produksi`  DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[20];
            int jumlah_lp = 0;
            while (rs.next()) {
                jumlah_lp++;
                row[0] = rs.getString("no_laporan_produksi");
                row[1] = rs.getString("kode_grade");
                row[2] = rs.getString("ruangan");
                row[3] = rs.getString("jenis_bulu_lp");
                row[4] = rs.getDate("tgl_setor_f2");
                row[5] = rs.getFloat("fbonus_f2") + rs.getFloat("fnol_f2");
                row[6] = rs.getFloat("pecah_f2");
                row[7] = rs.getFloat("flat_f2");
                row[8] = rs.getFloat("kpg_bonus");
                row[9] = rs.getFloat("bonus_mk_utuh_cabut_sub");
                float bonus_mku_cabut = rs.getFloat("kpg_bonus") * rs.getFloat("bonus_mk_utuh_cabut_sub");
                if (bonus_mku_cabut < 0) {
                    bonus_mku_cabut = 0;
                }
                row[10] = bonus_mku_cabut;
                total_bonus_mku_cabut = total_bonus_mku_cabut + bonus_mku_cabut;
                bonus_mku_cabut_setiap_sub.put(rs.getString("ruangan"), bonus_mku_cabut_setiap_sub.getOrDefault(rs.getString("ruangan"), 0d) + 0d);

                String qry = "SELECT `tb_detail_pencabut`.`id_pegawai`, `nama_pegawai`, `bagian`, `tb_detail_pencabut`.`no_laporan_produksi`, `jenis_bulu_lp`, "
                        + "ROUND(`berat_basah` * `tb_tarif_upah`.`upah_cabut`) AS 'upah_lp', ROUND(`jumlah_gram` * `tb_tarif_upah`.`upah_cabut`) AS 'upah_cabut' "
                        + "FROM `tb_detail_pencabut` \n"
                        + "LEFT JOIN `tb_laporan_produksi` ON `tb_detail_pencabut`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                        + "LEFT JOIN `tb_tarif_upah` ON `tb_laporan_produksi`.`jenis_bulu_lp` = `tb_tarif_upah`.`bulu_upah` \n"
                        + "LEFT JOIN `tb_cabut` ON `tb_detail_pencabut`.`no_laporan_produksi` = `tb_cabut`.`no_laporan_produksi`\n"
                        + "LEFT JOIN `tb_karyawan` ON `tb_detail_pencabut`.`id_pegawai` = `tb_karyawan`.`id_pegawai`\n"
                        + "WHERE `tb_detail_pencabut`.`no_laporan_produksi` = '" + rs.getString("no_laporan_produksi") + "' ";
                ResultSet result = Utility.db_sub.getStatement().executeQuery(qry);
                if (result.next()) {
                    result.beforeFirst();
                    while (result.next()) {
                        row[11] = result.getString("id_pegawai");
                        row[12] = result.getString("nama_pegawai");
                        row[13] = result.getString("bagian");
                        row[14] = result.getFloat("upah_cabut");
                        row[15] = result.getFloat("upah_lp");
                        double bonus_mku_karyawan = (result.getFloat("upah_cabut") / result.getFloat("upah_lp")) * bonus_mku_cabut;
                        row[16] = Math.round(bonus_mku_karyawan);
                        model.addRow(row);
                        total_bonus_mku_karyawan = total_bonus_mku_karyawan + bonus_mku_karyawan;
                        bonus_mku_setiap_karyawan.put(result.getString("id_pegawai"), bonus_mku_setiap_karyawan.getOrDefault(result.getString("id_pegawai"), 0d) + bonus_mku_karyawan);
                        bonus_mku_cabut_setiap_sub.put(result.getString("bagian"), bonus_mku_cabut_setiap_sub.getOrDefault(result.getString("bagian"), 0d) + bonus_mku_karyawan);
                    }
                } else {
                    row[11] = null;
                    row[12] = null;
                    row[13] = null;
                    row[14] = null;
                    row[15] = null;
                    row[16] = null;
                    model.addRow(row);
                }
            }
            ColumnsAutoSizer.sizeColumnsToFit(table_bonusMKU_Cabut);
            decimalFormat.setMaximumFractionDigits(2);
            label_bonusMKU_Cabut_total_Data.setText(decimalFormat.format(table_bonusMKU_Cabut.getRowCount()));
            label_bonusMKU_Cabut_total_LP.setText(decimalFormat.format(jumlah_lp));
            label_bonusMKU_Cabut_total_bonusLP.setText(decimalFormat.format(total_bonus_mku_cabut));
            label_bonusMKU_Cabut_total_bonusKaryawan.setText(decimalFormat.format(total_bonus_mku_karyawan));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_PenggajianSub.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refresh_BonusMKU_Cetak_Sub(Date tanggal_mulai, Date tanggal_selesai) {
        try {
            Utility.db_sub.connect();
            double total_bonus_mku_cetak = 0, total_bonus_mku_karyawan = 0;
            DefaultTableModel model = (DefaultTableModel) table_bonusMKU_Cetak.getModel();
            model.setRowCount(0);
            String search_sub = " AND `tb_laporan_produksi`.`ruangan` = '" + ComboBox_sub_bonusMKU.getSelectedItem().toString() + "'";
            if (ComboBox_sub_bonusMKU.getSelectedItem().toString().equals("All")) {
                search_sub = "";
            }
            sql = "SELECT `tb_finishing_2`.`no_laporan_produksi`, `tb_laporan_produksi`.`kode_grade`, `tb_grade_bahan_baku`.`kategori`, `ruangan`, `jenis_bulu_lp`, `tgl_setor_f2`, `fbonus_f2`, `fnol_f2`, `pecah_f2`, `flat_f2`, `bonus_mk_utuh_cetak_sub`, "
                    + "IF(`tb_grade_bahan_baku`.`jenis_bentuk` IN ('Mangkok', 'Oval'), ((`fbonus_f2` + `fnol_f2`) - (`pecah_f2` + `flat_f2`)), (`fbonus_f2` + `fnol_f2`))  AS 'kpg_bonus', "
                    + "IF(`tb_grade_bahan_baku`.`jenis_bentuk` IN ('Mangkok', 'Oval'), ((`fbonus_f2` + `fnol_f2`) - (`pecah_f2` + `flat_f2`)) * `bonus_mk_utuh_cetak_sub`, (`fbonus_f2` + `fnol_f2`) * `bonus_mk_utuh_cetak_sub`)  AS 'bonus_mku_cetak' "
                    + "FROM `tb_finishing_2` \n"
                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_finishing_2`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_grade_bahan_baku` ON `tb_laporan_produksi`.`kode_grade` = `tb_grade_bahan_baku`.`kode_grade`\n"
                    + "LEFT JOIN `tb_tarif_cabut` ON `tb_laporan_produksi`.`jenis_bulu_lp` = `tb_tarif_cabut`.`bulu_upah`\n"
                    + "WHERE `tb_finishing_2`.`no_laporan_produksi` LIKE 'WL.%' "
                    + "AND `tb_finishing_2`.`no_laporan_produksi` LIKE '%" + txt_search_no_lp_bonusMKU.getText() + "%' "
                    + search_sub
                    + "AND (`tgl_setor_f2` BETWEEN '" + dateFormat.format(tanggal_mulai) + "' AND '" + dateFormat.format(tanggal_selesai) + "')"
                    + "ORDER BY `tb_finishing_2`.`no_laporan_produksi`  DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[20];
            while (rs.next()) {
                row[0] = rs.getString("no_laporan_produksi");
                row[1] = rs.getString("kode_grade");
                row[2] = rs.getString("ruangan");
                row[3] = rs.getString("jenis_bulu_lp");
                row[4] = rs.getDate("tgl_setor_f2");
                row[5] = rs.getFloat("fbonus_f2") + rs.getFloat("fnol_f2");
                row[6] = rs.getFloat("pecah_f2");
                row[7] = rs.getFloat("flat_f2");
                row[8] = rs.getFloat("kpg_bonus");
                row[9] = rs.getFloat("bonus_mk_utuh_cetak_sub");
                float bonus_mku_cetak = rs.getFloat("kpg_bonus") * rs.getFloat("bonus_mk_utuh_cetak_sub");
                if (bonus_mku_cetak < 0) {
                    bonus_mku_cetak = 0;
                }
                row[10] = bonus_mku_cetak;
                total_bonus_mku_cetak = total_bonus_mku_cetak + bonus_mku_cetak;

                String qry = "SELECT `no_laporan_produksi`, `cetak_dikerjakan`, `tb_karyawan`.`nama_pegawai`, `tb_karyawan`.`bagian`\n"
                        + "FROM `tb_cetak` \n"
                        + "LEFT JOIN `tb_karyawan` ON `tb_cetak`.`cetak_dikerjakan` = `tb_karyawan`.`id_pegawai`\n"
                        + "WHERE `no_laporan_produksi` = '" + rs.getString("no_laporan_produksi") + "' ";
                ResultSet result = Utility.db_sub.getStatement().executeQuery(qry);
                if (result.next()) {
                    row[11] = result.getString("cetak_dikerjakan");
                    row[12] = result.getString("nama_pegawai");
                    row[13] = result.getString("bagian");
                    model.addRow(row);
                    total_bonus_mku_karyawan = total_bonus_mku_karyawan + bonus_mku_cetak;
                    bonus_mku_setiap_karyawan.put(result.getString("cetak_dikerjakan"), bonus_mku_setiap_karyawan.getOrDefault(result.getString("cetak_dikerjakan"), 0d) + bonus_mku_cetak);
                    bonus_mku_cetak_setiap_sub.put(result.getString("bagian"), bonus_mku_cetak_setiap_sub.getOrDefault(result.getString("bagian"), 0d) + bonus_mku_cetak);
                } else {
                    row[11] = null;
                    row[12] = null;
                    row[13] = null;
                    model.addRow(row);
                }
            }
            ColumnsAutoSizer.sizeColumnsToFit(table_bonusMKU_Cetak);
            decimalFormat.setMaximumFractionDigits(2);
            label_bonusMKU_Cetak_total_LP.setText(decimalFormat.format(table_bonusMKU_Cetak.getRowCount()));
            label_bonusMKU_Cetak_total_bonusLP.setText(decimalFormat.format(total_bonus_mku_cetak));
            label_bonusMKU_Cetak_total_bonusKaryawan.setText(decimalFormat.format(total_bonus_mku_karyawan));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_PenggajianSub.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refresh_BonusMKU_Sub() {
        boolean check = true;
        if (Date_tutupan_bonusMKU.getDate() == null) {
            check = false;
            JOptionPane.showMessageDialog(this, "Harap masukkan tanggal tutupan");
        } else if (!(new SimpleDateFormat("EEEEE").format(Date_tutupan_bonusMKU.getDate()).toUpperCase().equals("SATURDAY") || new SimpleDateFormat("EEEEE").format(Date_tutupan_bonusMKU.getDate()).toUpperCase().equals("SABTU"))) {
            check = false;
            JOptionPane.showMessageDialog(this, "Tanggal tutupan seharusnya hari SABTU");
        }
        if (check) {
            bonus_mku_setiap_karyawan = new HashMap<>();
            bonus_mku_cabut_setiap_sub = new HashMap<>();
            bonus_mku_cetak_setiap_sub = new HashMap<>();
            Date tanggal_mulai = new Date(Date_tutupan_bonusMKU.getDate().getTime() - (6 * 24 * 60 * 60 * 1000));
            Date tanggal_selesai = new Date(Date_tutupan_bonusMKU.getDate().getTime() - (0 * 24 * 60 * 60 * 1000));
            refresh_BonusMKU_Cabut_Sub(tanggal_mulai, tanggal_selesai);
            refresh_BonusMKU_Cetak_Sub(tanggal_mulai, tanggal_selesai);

            double total_rekap_bonus_mku = 0;
            DefaultTableModel model = (DefaultTableModel) table_bonusMKU_Sub.getModel();
            model.setRowCount(0);
            Object[] row = new Object[5];
            for (String key : bonus_mku_cabut_setiap_sub.keySet()) {
                row[0] = key;
                row[1] = bonus_mku_cabut_setiap_sub.getOrDefault(key, 0d);
                row[2] = bonus_mku_cetak_setiap_sub.getOrDefault(key, 0d);
                model.addRow(row);
                total_rekap_bonus_mku = total_rekap_bonus_mku + bonus_mku_cabut_setiap_sub.getOrDefault(key, 0d) + bonus_mku_cetak_setiap_sub.getOrDefault(key, 0d);
            }

            ColumnsAutoSizer.sizeColumnsToFit(table_bonusMKU_Sub);
            label_total_bonusMKU_Sub.setText(decimalFormat.format(total_rekap_bonus_mku));
        }
    }

    public void refreshTable_penggajian() {
        try {
            Utility.db_sub.connect();
            decimalFormat.setMaximumFractionDigits(2);
            HashMap<String, Integer[]> absen_map = new HashMap<>();
            HashMap<String, String[]> data_bank_kepala = new HashMap<>();
            DefaultTableModel model = (DefaultTableModel) Tabel_data_perhitungan_upah.getModel();
            model.setRowCount(0);
            boolean check = true;
            if (Date_tutupan_upahKaryawan.getDate() == null) {
                check = false;
                JOptionPane.showMessageDialog(this, "Harap masukkan tanggal tutupan");
            } else if (!(new SimpleDateFormat("EEEEE").format(Date_tutupan_upahKaryawan.getDate()).toUpperCase().equals("SATURDAY") || new SimpleDateFormat("EEEEE").format(Date_tutupan_upahKaryawan.getDate()).toUpperCase().equals("SABTU"))) {
                check = false;
                JOptionPane.showMessageDialog(this, "Tanggal tutupan seharusnya hari SABTU");
            }
            if (check) {
                HashMap<String, Double> jumlah_gaji_dapat_bonus = new HashMap<>();
                //SET header tabel
                Date tanggal_mulai = new Date(Date_tutupan_upahKaryawan.getDate().getTime() - (6 * 24 * 60 * 60 * 1000));
                Date tanggal_selesai = new Date(Date_tutupan_upahKaryawan.getDate().getTime() - (0 * 24 * 60 * 60 * 1000));
                JTableHeader th = Tabel_data_perhitungan_upah.getTableHeader();
                TableColumnModel tcm = th.getColumnModel();
                for (int i = 0; i < 7; i++) {
                    Date header = new Date(tanggal_mulai.getTime() + (i * 24 * 60 * 60 * 1000));
                    TableColumn tc = tcm.getColumn(i + 8);
                    tc.setHeaderValue(new SimpleDateFormat("dd MMM").format(header));
                }
                double jumlah_hari_kerja = Utility.countDaysWithoutFreeDays(tanggal_mulai, tanggal_selesai) + 1;
                txt_jumlah_hari_kerja.setText(decimalFormat.format(jumlah_hari_kerja));
                double target_bonus_cabut = jumlah_hari_kerja / 6 * 30;
                double target_bonus_sesekan = jumlah_hari_kerja / 6 * 120000;
                txt_target_cabut.setText(decimalFormat.format(target_bonus_cabut));
                //ambil data bank dan no rek kepala sub
                sql = "SELECT `bagian`, `id_pegawai`, `nama_pegawai`, `bank`, `no_rekening` \n"
                        + "FROM `tb_karyawan` WHERE `status` = 'IN-SUB' AND `jenis_pegawai` = 'KEPALA'  \n"
                        + "GROUP BY `bagian`";
                rs = Utility.db_sub.getStatement().executeQuery(sql);
                while (rs.next()) {
                    data_bank_kepala.put(rs.getString("bagian"),
                            new String[]{rs.getString("nama_pegawai"), rs.getString("bank"), rs.getString("no_rekening")});
                }
//                for (String bagian : data_bank_kepala.keySet()) {
//                    System.out.println(bagian + "[" + Arrays.toString(data_bank_kepala.get(bagian)) + "]");
//                }

                //get data absen dari data online
                sql = "SELECT `pin`,"
                        + "(SELECT IF(COUNT(`scan_date`)>0, 1, 0) FROM `att_log` absen1 WHERE `inoutmode` = '1' AND `pin` = absen.`pin` AND DATE(`scan_date`) = DATE_ADD('" + dateFormat.format(tanggal_selesai) + "', INTERVAL -6 DAY)) AS 'masuk_tgl1', "
                        + "(SELECT IF(COUNT(`scan_date`)>0, 1, 0) FROM `att_log` absen1 WHERE `inoutmode` = '2' AND `pin` = absen.`pin` AND DATE(`scan_date`) = DATE_ADD('" + dateFormat.format(tanggal_selesai) + "', INTERVAL -6 DAY)) AS 'pulang_tgl1', "
                        + "(SELECT IF(COUNT(`scan_date`)>0, 1, 0) FROM `att_log` absen1 WHERE `inoutmode` = '1' AND `pin` = absen.`pin` AND DATE(`scan_date`) = DATE_ADD('" + dateFormat.format(tanggal_selesai) + "', INTERVAL -5 DAY)) AS 'masuk_tgl2', "
                        + "(SELECT IF(COUNT(`scan_date`)>0, 1, 0) FROM `att_log` absen1 WHERE `inoutmode` = '2' AND `pin` = absen.`pin` AND DATE(`scan_date`) = DATE_ADD('" + dateFormat.format(tanggal_selesai) + "', INTERVAL -5 DAY)) AS 'pulang_tgl2', "
                        + "(SELECT IF(COUNT(`scan_date`)>0, 1, 0) FROM `att_log` absen1 WHERE `inoutmode` = '1' AND `pin` = absen.`pin` AND DATE(`scan_date`) = DATE_ADD('" + dateFormat.format(tanggal_selesai) + "', INTERVAL -4 DAY)) AS 'masuk_tgl3', "
                        + "(SELECT IF(COUNT(`scan_date`)>0, 1, 0) FROM `att_log` absen1 WHERE `inoutmode` = '2' AND `pin` = absen.`pin` AND DATE(`scan_date`) = DATE_ADD('" + dateFormat.format(tanggal_selesai) + "', INTERVAL -4 DAY)) AS 'pulang_tgl3', "
                        + "(SELECT IF(COUNT(`scan_date`)>0, 1, 0) FROM `att_log` absen1 WHERE `inoutmode` = '1' AND `pin` = absen.`pin` AND DATE(`scan_date`) = DATE_ADD('" + dateFormat.format(tanggal_selesai) + "', INTERVAL -3 DAY)) AS 'masuk_tgl4', "
                        + "(SELECT IF(COUNT(`scan_date`)>0, 1, 0) FROM `att_log` absen1 WHERE `inoutmode` = '2' AND `pin` = absen.`pin` AND DATE(`scan_date`) = DATE_ADD('" + dateFormat.format(tanggal_selesai) + "', INTERVAL -3 DAY)) AS 'pulang_tgl4', "
                        + "(SELECT IF(COUNT(`scan_date`)>0, 1, 0) FROM `att_log` absen1 WHERE `inoutmode` = '1' AND `pin` = absen.`pin` AND DATE(`scan_date`) = DATE_ADD('" + dateFormat.format(tanggal_selesai) + "', INTERVAL -2 DAY)) AS 'masuk_tgl5', "
                        + "(SELECT IF(COUNT(`scan_date`)>0, 1, 0) FROM `att_log` absen1 WHERE `inoutmode` = '2' AND `pin` = absen.`pin` AND DATE(`scan_date`) = DATE_ADD('" + dateFormat.format(tanggal_selesai) + "', INTERVAL -2 DAY)) AS 'pulang_tgl5', "
                        + "(SELECT IF(COUNT(`scan_date`)>0, 1, 0) FROM `att_log` absen1 WHERE `inoutmode` = '1' AND `pin` = absen.`pin` AND DATE(`scan_date`) = DATE_ADD('" + dateFormat.format(tanggal_selesai) + "', INTERVAL -1 DAY)) AS 'masuk_tgl6', "
                        + "(SELECT IF(COUNT(`scan_date`)>0, 1, 0) FROM `att_log` absen1 WHERE `inoutmode` = '2' AND `pin` = absen.`pin` AND DATE(`scan_date`) = DATE_ADD('" + dateFormat.format(tanggal_selesai) + "', INTERVAL -1 DAY)) AS 'pulang_tgl6', "
                        + "(SELECT IF(COUNT(`scan_date`)>0, 1, 0) FROM `att_log` absen1 WHERE `inoutmode` = '1' AND `pin` = absen.`pin` AND DATE(`scan_date`) = DATE_ADD('" + dateFormat.format(tanggal_selesai) + "', INTERVAL 0 DAY)) AS 'masuk_tgl7', "
                        + "(SELECT IF(COUNT(`scan_date`)>0, 1, 0) FROM `att_log` absen1 WHERE `inoutmode` = '2' AND `pin` = absen.`pin` AND DATE(`scan_date`) = DATE_ADD('" + dateFormat.format(tanggal_selesai) + "', INTERVAL 0 DAY)) AS 'pulang_tgl7' "
                        + "FROM `att_log` absen WHERE DATE(`scan_date`) BETWEEN DATE_ADD('" + dateFormat.format(tanggal_selesai) + "', INTERVAL -6 DAY) AND DATE_ADD('" + dateFormat.format(tanggal_selesai) + "', INTERVAL 0 DAY) "
                        + "GROUP BY `pin`";
                rs = Utility.db_sub.getStatement().executeQuery(sql);
                while (rs.next()) {
                    absen_map.put(rs.getString("pin"),
                            new Integer[]{
                                (rs.getInt("masuk_tgl1") + rs.getInt("pulang_tgl1")),
                                (rs.getInt("masuk_tgl2") + rs.getInt("pulang_tgl2")),
                                (rs.getInt("masuk_tgl3") + rs.getInt("pulang_tgl3")),
                                (rs.getInt("masuk_tgl4") + rs.getInt("pulang_tgl4")),
                                (rs.getInt("masuk_tgl5") + rs.getInt("pulang_tgl5")),
                                (rs.getInt("masuk_tgl6") + rs.getInt("pulang_tgl6")),
                                (rs.getInt("masuk_tgl7") + rs.getInt("pulang_tgl7"))
                            });
                }

                double total_bonus = 0, total_upah_borong_cabut = 0, total_bonus_cabut = 0, total_upah_sesekan = 0, total_bonus_sesekan = 0, total_upah_sapon = 0, total_bonus_sapon = 0, total_subsidi_training = 0,
                        total_upah_cuci = 0, total_bonus_cuci = 0, total_bonus_mku = 0, total_tunjangan_hadir = 0, total_piutang = 0, total_bonus_operational = 0, total_upah_transfer = 0;
                sql = "SELECT `tb_karyawan`.`id_pegawai`, `pin_calculated`, `nama_pegawai`, `bagian`, `jenis_pegawai`, `status`, `tb_karyawan`.`level_gaji`, `tanggal_masuk`, `tanggal_keluar`, `tb_level_gaji`.`upah_per_hari`, `tb_level_gaji`.`premi_hadir`,"
                        + "CABUT.`kpg_cabut_dikerjakan`, CABUT.`gram_cabut_dikerjakan`, CABUT.`upah_cabut`, CABUT.`upah_kk_kulit`, \n"
                        + "SESEKAN.`nilai_sesekan`, SAPON.`nilai_sapon`, PIUTANG.`piutang` \n"
                        + "FROM `tb_karyawan` \n"
                        + "LEFT JOIN `tb_level_gaji` ON `tb_karyawan`.`level_gaji` = `tb_level_gaji`.`level_gaji`"
                        + "LEFT JOIN ("
                        + "SELECT `id_pegawai`, ROUND(SUM(`jumlah_cabut`), 2) AS 'kpg_cabut_dikerjakan', ROUND(SUM(`jumlah_gram`), 2) AS 'gram_cabut_dikerjakan', "
                        + "ROUND(SUM(IF(`kode_grade` <> 'KK/KULIT', `jumlah_gram`, 0) * `tb_tarif_upah`.`upah_cabut`), 2) AS 'upah_cabut', \n"
                        + "ROUND(SUM(IF(`kode_grade` = 'KK/KULIT', `jumlah_gram`, 0) * `tb_tarif_upah`.`upah_cabut`), 2) AS 'upah_kk_kulit' \n"
                        + "FROM `tb_detail_pencabut` \n"
                        + "LEFT JOIN `tb_cabut` ON `tb_detail_pencabut`.`no_laporan_produksi` = `tb_cabut`.`no_laporan_produksi`\n"
                        + "LEFT JOIN `tb_laporan_produksi` ON `tb_detail_pencabut`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                        + "LEFT JOIN `tb_tarif_upah` ON `tb_laporan_produksi`.`jenis_bulu_lp` = `tb_tarif_upah`.`bulu_upah`\n"
                        + "WHERE `tgl_setor_cabut` BETWEEN '" + dateFormat.format(tanggal_mulai) + "' AND '" + dateFormat.format(tanggal_selesai) + "' "
                        + "GROUP BY `tb_detail_pencabut`.`id_pegawai`"
                        + ") CABUT ON `tb_karyawan`.`id_pegawai` = CABUT.`id_pegawai` \n"
                        + "LEFT JOIN ("
                        + "SELECT `tb_detail_penyesek`.`id_pegawai`, ROUND(SUM(`nilai_sesekan`), 2) AS 'nilai_sesekan' \n"
                        + "FROM `tb_detail_penyesek` \n"
                        + "LEFT JOIN `tb_laporan_produksi_sesekan` ON `tb_detail_penyesek`.`no_lp_sesekan` = `tb_laporan_produksi_sesekan`.`no_lp_sesekan`\n"
                        + "WHERE DATE(`waktu_setor_lp`) BETWEEN '" + dateFormat.format(tanggal_mulai) + "' AND '" + dateFormat.format(tanggal_selesai) + "' "
                        + "GROUP BY `tb_detail_penyesek`.`id_pegawai`"
                        + ") SESEKAN ON `tb_karyawan`.`id_pegawai` = SESEKAN.`id_pegawai` \n"
                        + "LEFT JOIN ("
                        + "SELECT `tb_detail_pekerja_sapon`.`id_pegawai`, ROUND(SUM(`nilai_sapon`), 2) AS 'nilai_sapon' \n"
                        + "FROM `tb_detail_pekerja_sapon` \n"
                        + "LEFT JOIN `tb_laporan_produksi_sapon` ON `tb_detail_pekerja_sapon`.`no_lp_sapon` = `tb_laporan_produksi_sapon`.`no_lp_sapon`\n"
                        + "WHERE DATE(`waktu_setor_lp`) BETWEEN '" + dateFormat.format(tanggal_mulai) + "' AND '" + dateFormat.format(tanggal_selesai) + "' "
                        + "GROUP BY `tb_detail_pekerja_sapon`.`id_pegawai`"
                        + ") SAPON ON `tb_karyawan`.`id_pegawai` = SAPON.`id_pegawai` \n"
                        + "LEFT JOIN ("
                        + "SELECT `id_pegawai`, SUM(`nominal_piutang`) AS 'piutang' FROM `tb_piutang_karyawan` "
                        + "WHERE `tanggal_piutang` <= '" + dateFormat.format(tanggal_selesai) + "' AND `status` = 0 "
                        + "GROUP BY `id_pegawai`"
                        + ") PIUTANG ON `tb_karyawan`.`id_pegawai` = PIUTANG.`id_pegawai` \n"
                        + "WHERE `tb_karyawan`.`nama_pegawai` LIKE '%" + txt_search_nama2.getText() + "%'  "
                        + "AND `bagian` LIKE '%" + txt_search_sub.getText() + "%' ";
                rs = Utility.db_sub.getStatement().executeQuery(sql);
                Object[] row = new Object[40];
                int no = 0;
                while (rs.next()) {
                    String id_pegawai = rs.getString("id_pegawai");
                    String pin_calculated = rs.getString("pin_calculated");
                    if (absen_map.get(pin_calculated) != null) {
                        String kode_sub = rs.getString("bagian");
                        double jumlah_hari_masuk_kerja = 0;

                        for (int j = 0; j < absen_map.get(pin_calculated).length; j++) {
                            jumlah_hari_masuk_kerja = jumlah_hari_masuk_kerja + Math.floor(Float.valueOf(absen_map.get(pin_calculated)[j]) / 2f);
                        }
                        double tunjangan_hadir = 0;
                        if (jumlah_hari_masuk_kerja >= jumlah_hari_kerja) {
                            tunjangan_hadir = rs.getDouble("premi_hadir") * jumlah_hari_masuk_kerja;
                        }
                        double subsidi_training = jumlah_hari_masuk_kerja * rs.getDouble("upah_per_hari");
                        double upah_borong_cabut = rs.getDouble("upah_cabut");
                        double upah_borong_kk_kulit = rs.getDouble("upah_kk_kulit");
                        double upah_borong_sesekan = rs.getDouble("nilai_sesekan");
                        double upah_borong_sapon = rs.getDouble("nilai_sapon");
                        double upah_borong_bp = (upah_borong_kk_kulit + upah_borong_sesekan + upah_borong_sapon);
                        double upah_borong_total = (upah_borong_cabut + upah_borong_bp);
                        double kpg_cabut_dikerjakan = rs.getDouble("kpg_cabut_dikerjakan");
                        double gram_cabut_dikerjakan = rs.getDouble("gram_cabut_dikerjakan");
                        double piutang = rs.getDouble("piutang");

//                        if (upah_borong_kk_kulit > 0 || upah_borong_sesekan > 0 || upah_borong_sapon > 0) {
//                            double upah_borong = (upah_borong_kk_kulit + upah_borong_sesekan + upah_borong_sapon);
//                            if (!rs.getString("level_gaji").contains("Training") && upah_borong >= target_bonus_sesekan) {
//                                //menjumlah gaji borong dari karyawan yang berhak mendapat bonus
//                                jumlah_gaji_dapat_bonus.put(kode_sub, upah_borong + jumlah_gaji_dapat_bonus.getOrDefault(kode_sub, 0d));
//                            }
//                        } else {
//                            if (!rs.getString("level_gaji").contains("Training") && kpg_cabut_dikerjakan >= target_bonus_cabut) {
//                                //menjumlah gaji borong dari karyawan yang berhak mendapat bonus
//                                jumlah_gaji_dapat_bonus.put(kode_sub, upah_borong_cabut + jumlah_gaji_dapat_bonus.getOrDefault(kode_sub, 0d));
//                            }
//                        }
                        if (!rs.getString("level_gaji").contains("Training") && (upah_borong_bp >= target_bonus_sesekan || kpg_cabut_dikerjakan >= target_bonus_cabut)) {
                            //menjumlah gaji borong dari karyawan yang berhak mendapat bonus
                            jumlah_gaji_dapat_bonus.put(kode_sub, upah_borong_total + jumlah_gaji_dapat_bonus.getOrDefault(kode_sub, 0d));
                        }
                        double upah_cuci = 0, bonus_cuci = 0;
                        switch (rs.getString("level_gaji")) {
                            case "Cuci 1":
                                upah_cuci = upah_cuci_per_karyawan.getOrDefault(kode_sub, 0d) * 0.8f;//Upah Cuci
                                bonus_cuci = bonus_cuci_per_karyawan.getOrDefault(kode_sub, 0d) * 0.8f;//Bonus Cuci
                                break;
                            case "Cuci 2":
                                upah_cuci = upah_cuci_per_karyawan.getOrDefault(kode_sub, 0d) * 0.9f;//Upah Cuci
                                bonus_cuci = bonus_cuci_per_karyawan.getOrDefault(kode_sub, 0d) * 0.9f;//Bonus Cuci
                                break;
                            case "Cuci 3":
                                upah_cuci = upah_cuci_per_karyawan.getOrDefault(kode_sub, 0d);//Upah Cuci
                                bonus_cuci = bonus_cuci_per_karyawan.getOrDefault(kode_sub, 0d);//Bonus Cuci
                                break;
                            default:
                                upah_cuci = 0;
                                bonus_cuci = 0;
                                break;
                        }

                        double bonus_mku_karyawan = bonus_mku_setiap_karyawan.getOrDefault(rs.getString("id_pegawai"), 0d);
                        if (bonus_mku_karyawan < 0) {
                            bonus_mku_karyawan = 0;
                        }
                        double upah_transfer = upah_borong_cabut
                                + upah_borong_kk_kulit
                                + upah_borong_sesekan
                                + upah_borong_sapon
                                + upah_cuci
                                + bonus_cuci
                                + bonus_mku_karyawan + subsidi_training + tunjangan_hadir
                                + piutang;
//                        System.out.println("(" + upah_borong_cabut + "/" + total_upah_cabut_setiap_sub.getOrDefault(kode_sub, 0d) + ") x " + bonus_mku_sub + " = " + bonus_mku_karyawan);
                        no++;
                        row[0] = no;
                        row[1] = id_pegawai;
                        row[2] = rs.getString("nama_pegawai");
                        row[3] = kode_sub;
                        row[4] = rs.getString("tanggal_masuk");
                        row[5] = rs.getString("tanggal_keluar");
                        row[6] = rs.getString("status");
                        row[7] = rs.getString("level_gaji");
                        row[8] = absen_map.get(pin_calculated)[0];
                        row[9] = absen_map.get(pin_calculated)[1];
                        row[10] = absen_map.get(pin_calculated)[2];
                        row[11] = absen_map.get(pin_calculated)[3];
                        row[12] = absen_map.get(pin_calculated)[4];
                        row[13] = absen_map.get(pin_calculated)[5];
                        row[14] = absen_map.get(pin_calculated)[6];
                        row[15] = jumlah_hari_masuk_kerja;//hari kerja
                        row[16] = kpg_cabut_dikerjakan;//Kpg dikerjakan
                        row[17] = Math.round(upah_borong_cabut);//Gaji Cabut
                        row[18] = Math.round(upah_borong_kk_kulit);//Gaji Cabut
                        row[19] = Math.round(upah_borong_sesekan);//Gaji Borong Sesekan
                        row[20] = Math.round(upah_borong_sapon);//Gaji Borong Sapon
                        row[21] = Math.round(upah_borong_cabut + upah_borong_kk_kulit + upah_borong_sesekan + upah_borong_sapon);//Total Upah
                        row[22] = 0;//Bonus
                        row[23] = Math.round(upah_cuci);//Upah cuci
                        row[24] = Math.round(bonus_cuci);//Bonus Cuci
                        row[25] = Math.round(bonus_mku_karyawan);//Bonus Mk utuh
                        row[26] = Math.round(subsidi_training);//Subsidi Training
                        row[27] = Math.round(tunjangan_hadir);//Tunjangan Hadir
                        row[28] = Math.round(piutang);//Piutang
                        row[29] = Math.round(upah_transfer);//Total Gaji
                        row[30] = 0;//Pembulatan Total Gaji
//                        row[31] = "";//Keterangan
//                        row[32] = rs.getDouble("upah_per_hari");
//                        row[33] = 0;//Gaji dapat bonus
                        model.addRow(row);

                        total_upah_borong_cabut = total_upah_borong_cabut + upah_borong_cabut + upah_borong_kk_kulit;
                        total_upah_sesekan = total_upah_sesekan + upah_borong_sesekan;
                        total_upah_sapon = total_upah_sapon + upah_borong_sapon;
                        total_upah_cuci = total_upah_cuci + upah_cuci;
                        total_bonus_cuci = total_bonus_cuci + bonus_cuci;
                        total_bonus_mku = total_bonus_mku + bonus_mku_karyawan;
                        total_subsidi_training = total_subsidi_training + subsidi_training;
                        total_tunjangan_hadir = total_tunjangan_hadir + tunjangan_hadir;
                        total_piutang = total_piutang + piutang;
                        total_upah_transfer = total_upah_transfer + upah_transfer;

                        upah_cuci_per_sub.put(kode_sub, upah_cuci_per_sub.getOrDefault(kode_sub, 0l) + Math.round(upah_cuci));
                        bonus_cuci_per_sub.put(kode_sub, bonus_cuci_per_sub.getOrDefault(kode_sub, 0l) + Math.round(bonus_cuci));
                        bonus_MKU_per_sub.put(kode_sub, bonus_MKU_per_sub.getOrDefault(kode_sub, 0l) + Math.round(bonus_mku_karyawan));
                        subsidi_training_per_sub.put(kode_sub, subsidi_training_per_sub.getOrDefault(kode_sub, 0l) + Math.round(subsidi_training));
                        tunjangan_hadir_per_sub.put(kode_sub, tunjangan_hadir_per_sub.getOrDefault(kode_sub, 0l) + Math.round(tunjangan_hadir));
                        piutang_per_sub.put(kode_sub, piutang_per_sub.getOrDefault(kode_sub, 0l) + Math.round(piutang));
                        total_gaji_karyawan_per_sub.put(kode_sub, total_gaji_karyawan_per_sub.getOrDefault(kode_sub, 0l) + Math.round(upah_transfer));
                    }
                }

                for (int i = 0; i < Tabel_data_perhitungan_upah.getRowCount(); i++) {
                    String sub = Tabel_data_perhitungan_upah.getValueAt(i, 3).toString();
                    double bonus_karyawan = 0;
                    double bonus_cabut_karyawan = 0;
                    double bonus_lp_sesekan = 0;
                    double bonus_lp_sapon = 0;

                    String level_gaji = Tabel_data_perhitungan_upah.getValueAt(i, 7).toString();
                    double kpg_cabut_dikerjakan = Double.valueOf(Tabel_data_perhitungan_upah.getValueAt(i, 16).toString());
                    double upah_borong_cabut = Double.valueOf(Tabel_data_perhitungan_upah.getValueAt(i, 17).toString());
                    double upah_borong_kk_kulit = Double.valueOf(Tabel_data_perhitungan_upah.getValueAt(i, 18).toString());
                    double upah_borong_sesekan = Double.valueOf(Tabel_data_perhitungan_upah.getValueAt(i, 19).toString());
                    double upah_borong_sapon = Double.valueOf(Tabel_data_perhitungan_upah.getValueAt(i, 20).toString());
                    double upah_borong_bp = upah_borong_kk_kulit + upah_borong_sesekan + upah_borong_sapon;
                    double upah_borong_total = upah_borong_cabut + upah_borong_kk_kulit + upah_borong_sesekan + upah_borong_sapon;

//                    if (upah_borong_kk_kulit > 0 || upah_borong_sesekan > 0 || upah_borong_sapon > 0) {
//                        double total_bonus_per_sub = (bonus_cabut_per_sub.getOrDefault(sub, 0d) + bonus_sesekan_per_sub.getOrDefault(sub, 0d) + bonus_sapon_per_sub.getOrDefault(sub, 0d));
//                        if (!level_gaji.contains("Training") && upah_borong_bp >= target_bonus_sesekan) {
//                            bonus_cabut_karyawan = (upah_borong_kk_kulit / jumlah_gaji_dapat_bonus.get(sub)) * total_bonus_per_sub;
//                            if (Double.isNaN(bonus_cabut_karyawan) || Double.isInfinite(bonus_cabut_karyawan)) {
//                                bonus_cabut_karyawan = 0;
//                            }
//                            bonus_lp_sesekan = (upah_borong_sesekan / jumlah_gaji_dapat_bonus.get(sub)) * total_bonus_per_sub;
//                            if (Double.isNaN(bonus_lp_sesekan) || Double.isInfinite(bonus_lp_sesekan)) {
//                                bonus_lp_sesekan = 0;
//                            }
//                            bonus_lp_sapon = (upah_borong_sapon / jumlah_gaji_dapat_bonus.get(sub)) * total_bonus_per_sub;
//                            if (Double.isNaN(bonus_lp_sapon) || Double.isInfinite(bonus_lp_sapon)) {
//                                bonus_lp_sapon = 0;
//                            }
//                        }
//                    } else {
//                        if (!level_gaji.contains("Training") && kpg_cabut_dikerjakan >= target_bonus_cabut) {
//                            bonus_cabut_karyawan = (upah_borong_cabut / jumlah_gaji_dapat_bonus.get(sub)) * bonus_cabut_per_sub.getOrDefault(sub, 0d);
//                            if (Double.isNaN(bonus_cabut_karyawan) || Double.isInfinite(bonus_cabut_karyawan)) {
//                                bonus_cabut_karyawan = 0;
//                            }
//                        }
//                    }
                    double total_bonus_per_sub = bonus_cabut_per_sub.getOrDefault(sub, 0d) + bonus_sesekan_per_sub.getOrDefault(sub, 0d) + bonus_sapon_per_sub.getOrDefault(sub, 0d);
                    if (!level_gaji.contains("Training") && (kpg_cabut_dikerjakan >= target_bonus_cabut || upah_borong_bp >= target_bonus_sesekan)) {
                        bonus_karyawan = (upah_borong_total / jumlah_gaji_dapat_bonus.get(sub)) * total_bonus_per_sub;
                        if (Double.isNaN(bonus_karyawan) || Double.isInfinite(bonus_karyawan)) {
                            bonus_karyawan = 0;
                        }
                    }
                    total_bonus = total_bonus + bonus_karyawan;
                    total_bonus_cabut = total_bonus_cabut + bonus_cabut_karyawan;
                    total_bonus_sesekan = total_bonus_sesekan + bonus_lp_sesekan;
                    total_bonus_sapon = total_bonus_sapon + bonus_lp_sapon;

                    total_upah_transfer = total_upah_transfer + bonus_cabut_karyawan + bonus_lp_sesekan + bonus_lp_sapon + bonus_karyawan;
                    Tabel_data_perhitungan_upah.setValueAt(Math.round(bonus_karyawan), i, 22);//isi kolom bonus cabut di tabel
//                    Tabel_data_perhitungan_upah.setValueAt(Math.round(bonus_cabut_karyawan), i, 19);//isi kolom bonus cabut di tabel
//                    Tabel_data_perhitungan_upah.setValueAt(Math.round(bonus_lp_sesekan), i, 21);//isi kolom bonus sesekan di tabel
//                    Tabel_data_perhitungan_upah.setValueAt(Math.round(bonus_lp_sapon), i, 23);//isi kolom bonus sapon di tabel
                    double update_total_gaji = Math.round((bonus_karyawan) + Double.valueOf(Tabel_data_perhitungan_upah.getValueAt(i, 29).toString()));
                    Tabel_data_perhitungan_upah.setValueAt(update_total_gaji, i, 29);//update total gaji transfer
                    Tabel_data_perhitungan_upah.setValueAt(Math.floor(update_total_gaji / 500d) * 500d, i, 30);
                    total_gaji_karyawan_per_sub.put(sub, total_gaji_karyawan_per_sub.get(sub) + Math.round(bonus_cabut_karyawan + bonus_lp_sesekan + bonus_lp_sapon));
                    
                }

                ColumnsAutoSizer.sizeColumnsToFit(Tabel_data_perhitungan_upah);
                Tabel_data_perhitungan_upah_total.setValueAt(Tabel_data_perhitungan_upah.getRowCount(), 0, 0);
                Tabel_data_perhitungan_upah_total.setValueAt(Math.round(total_upah_borong_cabut), 0, 1);
                Tabel_data_perhitungan_upah_total.setValueAt(Math.round(total_upah_sesekan), 0, 2);
                Tabel_data_perhitungan_upah_total.setValueAt(Math.round(total_upah_sapon), 0, 3);
                Tabel_data_perhitungan_upah_total.setValueAt(Math.round(total_bonus), 0, 4);
                Tabel_data_perhitungan_upah_total.setValueAt(Math.round(total_upah_cuci), 0, 5);
                Tabel_data_perhitungan_upah_total.setValueAt(Math.round(total_bonus_cuci), 0, 6);
                Tabel_data_perhitungan_upah_total.setValueAt(Math.round(total_bonus_mku), 0, 7);
                Tabel_data_perhitungan_upah_total.setValueAt(Math.round(total_subsidi_training), 0, 8);
                Tabel_data_perhitungan_upah_total.setValueAt(Math.round(total_tunjangan_hadir), 0, 9);
                Tabel_data_perhitungan_upah_total.setValueAt(Math.round(total_piutang), 0, 10);
                Tabel_data_perhitungan_upah_total.setValueAt(Math.round(total_upah_transfer), 0, 11);

                // refresh Tabel rekap per sub
                DefaultTableModel model_Tabel_data_rekap_transfer_sub = (DefaultTableModel) Tabel_data_rekap_transfer_sub.getModel();
                model_Tabel_data_rekap_transfer_sub.setRowCount(0);
                row = new Object[30];

                for (String kodeSub : total_gaji_karyawan_per_sub.keySet()) {
                    double bonus_operational_per_sub = (total_gram_lp_cabut_dikerjakan_per_sub.get(kodeSub) + total_gram_lp_sesekan_dikerjakan_per_sub.get(kodeSub) + total_gram_lp_sapon_dikerjakan_per_sub.get(kodeSub)) * 10d;
                    total_bonus_operational = total_bonus_operational + bonus_operational_per_sub;
                    row[0] = kodeSub;
                    row[1] = upah_cabut_per_sub.get(kodeSub);
                    row[2] = bonus_cabut_per_sub.get(kodeSub);
                    row[3] = upah_sesekan_per_sub.get(kodeSub);
                    row[4] = bonus_sesekan_per_sub.get(kodeSub);
                    row[5] = upah_sapon_per_sub.get(kodeSub);
                    row[6] = bonus_sapon_per_sub.get(kodeSub);
                    row[7] = upah_cuci_per_sub.get(kodeSub);
                    row[8] = bonus_cuci_per_sub.get(kodeSub);
                    row[9] = bonus_MKU_per_sub.get(kodeSub);
                    row[10] = subsidi_training_per_sub.get(kodeSub);
                    row[11] = tunjangan_hadir_per_sub.get(kodeSub);
                    row[12] = piutang_per_sub.get(kodeSub);
                    row[13] = total_gaji_karyawan_per_sub.get(kodeSub);
                    row[14] = bonus_operational_per_sub;
                    row[15] = upah_cabut_per_sub.getOrDefault(kodeSub, 0d)
                            + bonus_cabut_per_sub.getOrDefault(kodeSub, 0d)
                            + upah_sesekan_per_sub.getOrDefault(kodeSub, 0d)
                            + bonus_sesekan_per_sub.getOrDefault(kodeSub, 0d)
                            + upah_sapon_per_sub.getOrDefault(kodeSub, 0d)
                            + bonus_sapon_per_sub.getOrDefault(kodeSub, 0d)
                            + upah_cuci_per_sub.getOrDefault(kodeSub, 0l)
                            + bonus_cuci_per_sub.getOrDefault(kodeSub, 0l)
                            + bonus_MKU_per_sub.getOrDefault(kodeSub, 0l)
                            + subsidi_training_per_sub.getOrDefault(kodeSub, 0l)
                            + tunjangan_hadir_per_sub.getOrDefault(kodeSub, 0l)
                            + piutang_per_sub.getOrDefault(kodeSub, 0l)
                            + bonus_operational_per_sub;
                    row[16] = upah_cabut_per_sub.getOrDefault(kodeSub, 0d)
                            + upah_sesekan_per_sub.getOrDefault(kodeSub, 0d)
                            + upah_sapon_per_sub.getOrDefault(kodeSub, 0d);

                    row[17] = data_bank_kepala.getOrDefault(kodeSub, new String[]{"", "", ""})[0];
                    row[18] = data_bank_kepala.getOrDefault(kodeSub, new String[]{"", "", ""})[1];
                    row[19] = data_bank_kepala.getOrDefault(kodeSub, new String[]{"", "", ""})[2];
                    model_Tabel_data_rekap_transfer_sub.addRow(row);
                }
                Tabel_data_perhitungan_upah_total.setValueAt(Math.round(total_bonus_operational), 0, 12);
                Tabel_data_perhitungan_upah_total.setValueAt(Math.round(total_upah_transfer + total_bonus_operational), 0, 13);
                ColumnsAutoSizer.sizeColumnsToFit(Tabel_data_perhitungan_upah_total);
                ColumnsAutoSizer.sizeColumnsToFit(Tabel_data_rekap_transfer_sub);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_PenggajianSub.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void refreshTable_data_tersimpan() {
        try {
            Utility.db_sub.connect();
            String bagian = "AND `sub` = '" + ComboBox_bagian.getSelectedItem().toString() + "' ";
            if ("All".equals(ComboBox_bagian.getSelectedItem().toString())) {
                bagian = "";
            }
            String tanggal = "";
            if (Date1.getDate() != null && Date2.getDate() != null) {
                tanggal = " AND `tgl_penggajian` BETWEEN '" + dateFormat.format(Date1.getDate()) + "' AND '" + dateFormat.format(Date2.getDate()) + "'";
            }

            DefaultTableModel model = (DefaultTableModel) Tabel_data_tersimpan.getModel();
            model.setRowCount(0);
            sql = "SELECT `tgl_penggajian`, `tb_slip_gaji`.`id_pegawai`, `tb_karyawan`.`nama_pegawai`, `sub`, `tb_karyawan`.`status`, `tb_slip_gaji`.`level_gaji`, "
                    + "`hari_masuk_kerja`, `kpg_cabut`, `upah_cabut`, `upah_kk_kulit`, `bonus_cabut`, `upah_sesekan`, `bonus_sesekan`, `upah_sapon`, `bonus_sapon`, "
                    + "`upah_cuci`, `bonus_cuci`, `bonus_mku`, `subsidi_training`, `tunjangan_hadir`, `piutang`, `gaji_transfer` \n"
                    + "FROM `tb_slip_gaji` \n"
                    + "LEFT JOIN `tb_karyawan` ON `tb_slip_gaji`.`id_pegawai` = `tb_karyawan`.`id_pegawai`\n"
                    + "WHERE `nama_pegawai` LIKE '%" + txt_nama.getText() + "%' "
                    + "AND `tb_slip_gaji`.`id_pegawai` LIKE '%" + txt_id.getText() + "%' "
                    + bagian
                    + tanggal
                    + "ORDER BY `tgl_penggajian` DESC, `tb_karyawan`.`nama_pegawai`";
            rs = Utility.db_sub.getStatement().executeQuery(sql);
            Object[] row = new Object[25];
            while (rs.next()) {
                row[0] = rs.getDate("tgl_penggajian");
                row[1] = rs.getString("id_pegawai");
                row[2] = rs.getString("nama_pegawai");
                row[3] = rs.getString("sub");
                row[4] = rs.getString("status");
                row[5] = rs.getString("level_gaji");
                row[6] = rs.getInt("hari_masuk_kerja");
                row[7] = rs.getInt("kpg_cabut");
                row[8] = rs.getInt("upah_cabut");
                row[9] = rs.getInt("upah_kk_kulit");
                row[10] = rs.getInt("bonus_cabut");
                row[11] = rs.getInt("upah_sesekan");
                row[12] = rs.getInt("bonus_sesekan");
                row[13] = rs.getInt("upah_sapon");
                row[14] = rs.getInt("bonus_sapon");
                row[15] = rs.getInt("upah_cuci");
                row[16] = rs.getInt("bonus_cuci");
                row[17] = rs.getInt("bonus_mku");
                row[18] = rs.getInt("subsidi_training");
                row[19] = rs.getInt("tunjangan_hadir");
                row[20] = rs.getInt("piutang");
                row[21] = rs.getInt("gaji_transfer");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(Tabel_data_tersimpan);
            int rowData = Tabel_data_tersimpan.getRowCount();
            label_total_data_tersimpan.setText(Integer.toString(rowData));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_PenggajianSub.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel_kinerja_pekerja_Cabut = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        txt_search_nama = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txt_search_no_lp = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        ComboBox_sub = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        Date_kinerja_borong2 = new com.toedter.calendar.JDateChooser();
        Date_kinerja_borong1 = new com.toedter.calendar.JDateChooser();
        button_refresh_allData = new javax.swing.JButton();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel_tab_cabut = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        label_total_gram_cabut = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        label_total_upah_cabut = new javax.swing.JLabel();
        button_export_pencabut = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        table_data_pencabut = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        table_data_rekap_cabut = new javax.swing.JTable();
        jLabel14 = new javax.swing.JLabel();
        label_total_upah_rekap_cabut = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        button_export_rekap_pencabut = new javax.swing.JButton();
        jPanel_tab_sesekan = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        label_total_gram_sesekan = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        label_total_upah_sesekan = new javax.swing.JLabel();
        button_export_penyesek = new javax.swing.JButton();
        jScrollPane16 = new javax.swing.JScrollPane();
        table_data_pekerja_sesekan = new javax.swing.JTable();
        jPanel10 = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        label_total_upah_rekap_sesekan = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        button_export_rekap_penyesek = new javax.swing.JButton();
        jScrollPane17 = new javax.swing.JScrollPane();
        table_data_rekap_sesekan = new javax.swing.JTable();
        jPanel_tab_sapon = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jLabel37 = new javax.swing.JLabel();
        label_total_gram_sapon = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        label_total_upah_sapon = new javax.swing.JLabel();
        button_export5 = new javax.swing.JButton();
        jScrollPane14 = new javax.swing.JScrollPane();
        table_data_pekerja_sapon = new javax.swing.JTable();
        jPanel12 = new javax.swing.JPanel();
        jScrollPane15 = new javax.swing.JScrollPane();
        table_data_rekap_sapon = new javax.swing.JTable();
        jLabel44 = new javax.swing.JLabel();
        label_total_upah_rekap_sapon = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        button_export6 = new javax.swing.JButton();
        jPanel_Bonus_Cabut = new javax.swing.JPanel();
        button_refresh_bonusCabut = new javax.swing.JButton();
        Date_tutupan_bonusCabut = new com.toedter.calendar.JDateChooser();
        jLabel18 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        ComboBox_sub_bonusCabut = new javax.swing.JComboBox<>();
        jLabel27 = new javax.swing.JLabel();
        txt_search_no_lp_bonusCabut = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        txt_jumlah_hari_kerja_bonus = new javax.swing.JTextField();
        jTabbedPane4 = new javax.swing.JTabbedPane();
        jPanel_bonus_LP_WL = new javax.swing.JPanel();
        label_bonusCabut_total_bobotLP = new javax.swing.JLabel();
        label_bonusCabut_total_LP = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        table_data_Bonus_LP_WL = new javax.swing.JTable();
        label_bonusCabut_total_gramLP = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        button_export_bonus_LP_WL = new javax.swing.JButton();
        jLabel31 = new javax.swing.JLabel();
        label_bonusCabut_total_bonusLP = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        label_bonusCabut_total_nilaiLP = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jPanel_bonus_LP_BP = new javax.swing.JPanel();
        label_bonusCabut_total_bobotLP_BP = new javax.swing.JLabel();
        label_bonusCabut_total_LP_BP = new javax.swing.JLabel();
        jScrollPane13 = new javax.swing.JScrollPane();
        table_data_Bonus_LP_BP = new javax.swing.JTable();
        label_bonusCabut_total_gramLP_BP = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        button_export_bonus_LP_BP = new javax.swing.JButton();
        jLabel47 = new javax.swing.JLabel();
        label_bonusCabut_total_bonus_LP_BP = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        jPanel_bonus_LP_SP = new javax.swing.JPanel();
        label_bonusCabut_total_bobotLP_SP = new javax.swing.JLabel();
        label_bonusCabut_total_LP_SP = new javax.swing.JLabel();
        jScrollPane18 = new javax.swing.JScrollPane();
        table_data_Bonus_LP_SP = new javax.swing.JTable();
        label_bonusCabut_total_gramLP_SP = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        button_export_bonus_LP_SP = new javax.swing.JButton();
        jLabel51 = new javax.swing.JLabel();
        label_bonusCabut_total_bonus_LP_SP = new javax.swing.JLabel();
        jLabel52 = new javax.swing.JLabel();
        jLabel53 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        table_data_RekapBonus_LP_Sub = new javax.swing.JTable();
        button_export_bonus_LP_Sub = new javax.swing.JButton();
        jLabel26 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        label_total_bonusCabut_Sub = new javax.swing.JLabel();
        jScrollPane7 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jLabel54 = new javax.swing.JLabel();
        jProgressBar1 = new javax.swing.JProgressBar();
        jScrollPane19 = new javax.swing.JScrollPane();
        jTextArea4 = new javax.swing.JTextArea();
        jLabel55 = new javax.swing.JLabel();
        txt_min_lp_dikerjakan = new javax.swing.JTextField();
        jPanel_Bonus_MKU = new javax.swing.JPanel();
        button_refresh_bonusMKU = new javax.swing.JButton();
        Date_tutupan_bonusMKU = new com.toedter.calendar.JDateChooser();
        jLabel21 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        ComboBox_sub_bonusMKU = new javax.swing.JComboBox<>();
        jPanel8 = new javax.swing.JPanel();
        label_bonusMKU_Cabut_total_LP = new javax.swing.JLabel();
        button_export_bonusMKU_Cabut = new javax.swing.JButton();
        jScrollPane8 = new javax.swing.JScrollPane();
        table_bonusMKU_Cabut = new javax.swing.JTable();
        jLabel34 = new javax.swing.JLabel();
        label_bonusMKU_Cabut_total_bonusLP = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        label_bonusMKU_Cabut_total_bonusKaryawan = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        label_bonusMKU_Cabut_total_Data = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jScrollPane10 = new javax.swing.JScrollPane();
        table_bonusMKU_Sub = new javax.swing.JTable();
        button_export_bonusMKU_sub = new javax.swing.JButton();
        jLabel39 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        label_total_bonusMKU_Sub = new javax.swing.JLabel();
        jScrollPane11 = new javax.swing.JScrollPane();
        jTextArea3 = new javax.swing.JTextArea();
        jLabel41 = new javax.swing.JLabel();
        txt_search_no_lp_bonusMKU = new javax.swing.JTextField();
        jPanel13 = new javax.swing.JPanel();
        label_bonusMKU_Cetak_total_LP = new javax.swing.JLabel();
        button_export_bonusMKU_Cetak = new javax.swing.JButton();
        jScrollPane20 = new javax.swing.JScrollPane();
        table_bonusMKU_Cetak = new javax.swing.JTable();
        jLabel56 = new javax.swing.JLabel();
        label_bonusMKU_Cetak_total_bonusLP = new javax.swing.JLabel();
        jLabel57 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        label_bonusMKU_Cetak_total_bonusKaryawan = new javax.swing.JLabel();
        jPanel_Upah_Karyawan_Sub = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        Date_tutupan_upahKaryawan = new com.toedter.calendar.JDateChooser();
        button_refresh_perhitungan_upah = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();
        txt_search_nama2 = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        txt_jumlah_hari_kerja = new javax.swing.JTextField();
        button_export2 = new javax.swing.JButton();
        jLabel20 = new javax.swing.JLabel();
        txt_search_sub = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel33 = new javax.swing.JLabel();
        txt_target_cabut = new javax.swing.JTextField();
        button_save_data_fix = new javax.swing.JButton();
        jScrollPane9 = new javax.swing.JScrollPane();
        Tabel_data_perhitungan_upah_total = new javax.swing.JTable();
        jTabbedPane3 = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        Tabel_data_perhitungan_upah = new javax.swing.JTable();
        jScrollPane12 = new javax.swing.JScrollPane();
        Tabel_data_rekap_transfer_sub = new javax.swing.JTable();
        button_naik_level_gaji = new javax.swing.JButton();
        button_export_rekap_sub = new javax.swing.JButton();
        button_form_laporan_sub = new javax.swing.JButton();
        jPanel_DataTersimpan = new javax.swing.JPanel();
        button_export = new javax.swing.JButton();
        jLabel58 = new javax.swing.JLabel();
        label_total_data_tersimpan = new javax.swing.JLabel();
        Date1 = new com.toedter.calendar.JDateChooser();
        jLabel4 = new javax.swing.JLabel();
        ComboBox_bagian = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        txt_nama = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txt_id = new javax.swing.JTextField();
        jLabel59 = new javax.swing.JLabel();
        button_search = new javax.swing.JButton();
        Date2 = new com.toedter.calendar.JDateChooser();
        jScrollPane21 = new javax.swing.JScrollPane();
        Tabel_data_tersimpan = new javax.swing.JTable();

        setBackground(new java.awt.Color(255, 255, 255));

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N

        jPanel_kinerja_pekerja_Cabut.setBackground(new java.awt.Color(255, 255, 255));

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel7.setText("Nama :");

        txt_search_nama.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_nama.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_namaKeyPressed(evt);
            }
        });

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel9.setText("No LP :");

        txt_search_no_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_no_lp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_no_lpKeyPressed(evt);
            }
        });

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel8.setText("Sub :");

        ComboBox_sub.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_sub.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel5.setText("Tanggal Setor :");

        Date_kinerja_borong2.setBackground(new java.awt.Color(255, 255, 255));
        Date_kinerja_borong2.setDate(new Date());
        Date_kinerja_borong2.setDateFormatString("dd MMMM yyyy");
        Date_kinerja_borong2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Date_kinerja_borong1.setBackground(new java.awt.Color(255, 255, 255));
        Date_kinerja_borong1.setDate(new Date());
        Date_kinerja_borong1.setDateFormatString("dd MMMM yyyy");
        Date_kinerja_borong1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        button_refresh_allData.setBackground(new java.awt.Color(255, 255, 255));
        button_refresh_allData.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_refresh_allData.setText("Refresh");
        button_refresh_allData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refresh_allDataActionPerformed(evt);
            }
        });

        jTabbedPane2.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jPanel_tab_cabut.setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel10.setText("Total Gram Cabut :");

        label_total_gram_cabut.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_cabut.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_gram_cabut.setText("88");

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel11.setText("Total Upah :");

        label_total_upah_cabut.setBackground(new java.awt.Color(255, 255, 255));
        label_total_upah_cabut.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_upah_cabut.setText("88");

        button_export_pencabut.setBackground(new java.awt.Color(255, 255, 255));
        button_export_pencabut.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_pencabut.setText("Export to Excel");
        button_export_pencabut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_pencabutActionPerformed(evt);
            }
        });

        table_data_pencabut.setAutoCreateRowSorter(true);
        table_data_pencabut.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_data_pencabut.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Pegawai", "Nama", "Bagian", "No LP", "Jenis Blu Upah", "Tanggal Cabut", "Tanggal Setor Cabut", "Kpg", "Kpg Upah", "Gram", "Upah (Rp.)/Gr", "Total Upah (Rp.)"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(table_data_pencabut);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 740, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gram_cabut)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_upah_cabut)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_export_pencabut)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_upah_cabut, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_export_pencabut, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_gram_cabut, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 555, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));

        table_data_rekap_cabut.setAutoCreateRowSorter(true);
        table_data_rekap_cabut.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_data_rekap_cabut.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tgl Setor Cabut", "ID Pegawai", "Nama", "Bagian", "Kpg Cabut", "Gram", "Total Upah (Rp.)"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table_data_rekap_cabut.getTableHeader().setReorderingAllowed(false);
        jScrollPane5.setViewportView(table_data_rekap_cabut);

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel14.setText("Rekap /Karyawan /hari");

        label_total_upah_rekap_cabut.setBackground(new java.awt.Color(255, 255, 255));
        label_total_upah_rekap_cabut.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_upah_rekap_cabut.setText("88");

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel12.setText("Total Upah :");

        button_export_rekap_pencabut.setBackground(new java.awt.Color(255, 255, 255));
        button_export_rekap_pencabut.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_rekap_pencabut.setText("Export to Excel");
        button_export_rekap_pencabut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_rekap_pencabutActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_upah_rekap_cabut)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_export_rekap_pencabut))
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 570, Short.MAX_VALUE)))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_upah_rekap_cabut, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_export_rekap_pencabut, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel_tab_cabutLayout = new javax.swing.GroupLayout(jPanel_tab_cabut);
        jPanel_tab_cabut.setLayout(jPanel_tab_cabutLayout);
        jPanel_tab_cabutLayout.setHorizontalGroup(
            jPanel_tab_cabutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_tab_cabutLayout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel_tab_cabutLayout.setVerticalGroup(
            jPanel_tab_cabutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jTabbedPane2.addTab("CABUT", jPanel_tab_cabut);

        jPanel_tab_sesekan.setBackground(new java.awt.Color(255, 255, 255));

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel13.setText("Total Gram Sesekan :");

        label_total_gram_sesekan.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_sesekan.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_gram_sesekan.setText("88");

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel16.setText("Total Upah :");

        label_total_upah_sesekan.setBackground(new java.awt.Color(255, 255, 255));
        label_total_upah_sesekan.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_upah_sesekan.setText("88");

        button_export_penyesek.setBackground(new java.awt.Color(255, 255, 255));
        button_export_penyesek.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_penyesek.setText("Export to Excel");
        button_export_penyesek.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_penyesekActionPerformed(evt);
            }
        });

        table_data_pekerja_sesekan.setAutoCreateRowSorter(true);
        table_data_pekerja_sesekan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_data_pekerja_sesekan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Pegawai", "Nama", "Bagian", "No LP", "Jenis Blu Upah", "Tgl Sesek", "Tgl Setor Sesekan", "Gram Sesek", "Upah (Rp.)", "Bobot LP"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane16.setViewportView(table_data_pekerja_sesekan);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gram_sesekan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_upah_sesekan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 429, Short.MAX_VALUE)
                        .addComponent(button_export_penyesek))
                    .addComponent(jScrollPane16, javax.swing.GroupLayout.DEFAULT_SIZE, 740, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_upah_sesekan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_export_penyesek, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_gram_sesekan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane16, javax.swing.GroupLayout.DEFAULT_SIZE, 555, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel10.setBackground(new java.awt.Color(255, 255, 255));

        jLabel22.setBackground(new java.awt.Color(255, 255, 255));
        jLabel22.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel22.setText("Rekap /Karyawan /hari");

        label_total_upah_rekap_sesekan.setBackground(new java.awt.Color(255, 255, 255));
        label_total_upah_rekap_sesekan.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_upah_rekap_sesekan.setText("88");

        jLabel36.setBackground(new java.awt.Color(255, 255, 255));
        jLabel36.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel36.setText("Total Upah :");

        button_export_rekap_penyesek.setBackground(new java.awt.Color(255, 255, 255));
        button_export_rekap_penyesek.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_rekap_penyesek.setText("Export to Excel");
        button_export_rekap_penyesek.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_rekap_penyesekActionPerformed(evt);
            }
        });

        table_data_rekap_sesekan.setAutoCreateRowSorter(true);
        table_data_rekap_sesekan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_data_rekap_sesekan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tgl Setor Sesekan", "ID Pegawai", "Nama", "Bagian", "Tot Gr", "Total Upah (Rp.)"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table_data_rekap_sesekan.getTableHeader().setReorderingAllowed(false);
        jScrollPane17.setViewportView(table_data_rekap_sesekan);

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel22)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel36)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_upah_rekap_sesekan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 260, Short.MAX_VALUE)
                        .addComponent(button_export_rekap_penyesek))
                    .addComponent(jScrollPane17, javax.swing.GroupLayout.DEFAULT_SIZE, 570, Short.MAX_VALUE)))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export_rekap_penyesek, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_upah_rekap_sesekan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane17)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel_tab_sesekanLayout = new javax.swing.GroupLayout(jPanel_tab_sesekan);
        jPanel_tab_sesekan.setLayout(jPanel_tab_sesekanLayout);
        jPanel_tab_sesekanLayout.setHorizontalGroup(
            jPanel_tab_sesekanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_tab_sesekanLayout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel_tab_sesekanLayout.setVerticalGroup(
            jPanel_tab_sesekanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel10, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jTabbedPane2.addTab("SESEKAN", jPanel_tab_sesekan);

        jPanel_tab_sapon.setBackground(new java.awt.Color(255, 255, 255));

        jPanel11.setBackground(new java.awt.Color(255, 255, 255));

        jLabel37.setBackground(new java.awt.Color(255, 255, 255));
        jLabel37.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel37.setText("Total Gram Sapon :");

        label_total_gram_sapon.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_sapon.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_gram_sapon.setText("88");

        jLabel43.setBackground(new java.awt.Color(255, 255, 255));
        jLabel43.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel43.setText("Total Upah :");

        label_total_upah_sapon.setBackground(new java.awt.Color(255, 255, 255));
        label_total_upah_sapon.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_upah_sapon.setText("88");

        button_export5.setBackground(new java.awt.Color(255, 255, 255));
        button_export5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export5.setText("Export to Excel");
        button_export5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export5ActionPerformed(evt);
            }
        });

        table_data_pekerja_sapon.setAutoCreateRowSorter(true);
        table_data_pekerja_sapon.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_data_pekerja_sapon.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Pegawai", "Nama", "Bagian", "No LP", "Jenis Blu Upah", "Tgl Sesek", "Tgl Setor Sesekan", "Gram Sesek", "Upah (Rp.)", "Bobot LP"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane14.setViewportView(table_data_pekerja_sapon);

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane14, javax.swing.GroupLayout.DEFAULT_SIZE, 740, Short.MAX_VALUE)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jLabel37)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gram_sapon)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel43)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_upah_sapon)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_export5)))
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_upah_sapon, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_export5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_gram_sapon, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane14, javax.swing.GroupLayout.DEFAULT_SIZE, 555, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel12.setBackground(new java.awt.Color(255, 255, 255));

        table_data_rekap_sapon.setAutoCreateRowSorter(true);
        table_data_rekap_sapon.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_data_rekap_sapon.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tgl Setor Sesekan", "ID Pegawai", "Nama", "Bagian", "Tot Gr", "Total Upah (Rp.)"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table_data_rekap_sapon.getTableHeader().setReorderingAllowed(false);
        jScrollPane15.setViewportView(table_data_rekap_sapon);

        jLabel44.setBackground(new java.awt.Color(255, 255, 255));
        jLabel44.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel44.setText("Rekap /Karyawan /hari");

        label_total_upah_rekap_sapon.setBackground(new java.awt.Color(255, 255, 255));
        label_total_upah_rekap_sapon.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_upah_rekap_sapon.setText("88");

        jLabel45.setBackground(new java.awt.Color(255, 255, 255));
        jLabel45.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel45.setText("Total Upah :");

        button_export6.setBackground(new java.awt.Color(255, 255, 255));
        button_export6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export6.setText("Export to Excel");
        button_export6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export6ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(jLabel44)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel45)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_upah_rekap_sapon)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_export6))
                    .addComponent(jScrollPane15, javax.swing.GroupLayout.DEFAULT_SIZE, 570, Short.MAX_VALUE)))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel45, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_upah_rekap_sapon, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane15)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel_tab_saponLayout = new javax.swing.GroupLayout(jPanel_tab_sapon);
        jPanel_tab_sapon.setLayout(jPanel_tab_saponLayout);
        jPanel_tab_saponLayout.setHorizontalGroup(
            jPanel_tab_saponLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_tab_saponLayout.createSequentialGroup()
                .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel_tab_saponLayout.setVerticalGroup(
            jPanel_tab_saponLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel12, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jTabbedPane2.addTab("SAPON", jPanel_tab_sapon);

        javax.swing.GroupLayout jPanel_kinerja_pekerja_CabutLayout = new javax.swing.GroupLayout(jPanel_kinerja_pekerja_Cabut);
        jPanel_kinerja_pekerja_Cabut.setLayout(jPanel_kinerja_pekerja_CabutLayout);
        jPanel_kinerja_pekerja_CabutLayout.setHorizontalGroup(
            jPanel_kinerja_pekerja_CabutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_kinerja_pekerja_CabutLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_search_nama, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_search_no_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ComboBox_sub, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Date_kinerja_borong1, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Date_kinerja_borong2, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_refresh_allData)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jTabbedPane2, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        jPanel_kinerja_pekerja_CabutLayout.setVerticalGroup(
            jPanel_kinerja_pekerja_CabutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_kinerja_pekerja_CabutLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_kinerja_pekerja_CabutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel_kinerja_pekerja_CabutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel_kinerja_pekerja_CabutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_search_no_lp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel_kinerja_pekerja_CabutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_search_nama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ComboBox_sub, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(Date_kinerja_borong1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_kinerja_borong2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_refresh_allData, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane2))
        );

        jTabbedPane1.addTab("DATA KINERJA BORONG", jPanel_kinerja_pekerja_Cabut);

        jPanel_Bonus_Cabut.setBackground(new java.awt.Color(255, 255, 255));

        button_refresh_bonusCabut.setBackground(new java.awt.Color(255, 255, 255));
        button_refresh_bonusCabut.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_refresh_bonusCabut.setText("Refresh");
        button_refresh_bonusCabut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refresh_bonusCabutActionPerformed(evt);
            }
        });

        Date_tutupan_bonusCabut.setBackground(new java.awt.Color(255, 255, 255));
        Date_tutupan_bonusCabut.setDate(new Date());
        Date_tutupan_bonusCabut.setDateFormatString("dd MMMM yyyy");
        Date_tutupan_bonusCabut.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel18.setText("Tanggal Tutupan (Sabtu) :");

        jLabel23.setBackground(new java.awt.Color(255, 255, 255));
        jLabel23.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel23.setText("Sub :");

        ComboBox_sub_bonusCabut.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        ComboBox_sub_bonusCabut.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        jLabel27.setBackground(new java.awt.Color(255, 255, 255));
        jLabel27.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel27.setText("No LP :");

        txt_search_no_lp_bonusCabut.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_search_no_lp_bonusCabut.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_no_lp_bonusCabutKeyPressed(evt);
            }
        });

        jLabel19.setBackground(new java.awt.Color(255, 255, 255));
        jLabel19.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel19.setText("Jumlah Hari Kerja :");

        txt_jumlah_hari_kerja_bonus.setEditable(false);
        txt_jumlah_hari_kerja_bonus.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_jumlah_hari_kerja_bonus.setText("6");

        jTabbedPane4.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane4.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N

        jPanel_bonus_LP_WL.setBackground(new java.awt.Color(255, 255, 255));

        label_bonusCabut_total_bobotLP.setBackground(new java.awt.Color(255, 255, 255));
        label_bonusCabut_total_bobotLP.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_bonusCabut_total_bobotLP.setText("88");

        label_bonusCabut_total_LP.setBackground(new java.awt.Color(255, 255, 255));
        label_bonusCabut_total_LP.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_bonusCabut_total_LP.setText("88");

        table_data_Bonus_LP_WL.setAutoCreateRowSorter(true);
        table_data_Bonus_LP_WL.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_data_Bonus_LP_WL.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No LP", "Grade", "Ruangan", "Jenis Blu Upah", "Setor Cabut", "Kpg", "Kpg Upah", "Gram", "Bobot LP", "Upah Cabut", "Bonus Cabut"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table_data_Bonus_LP_WL.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(table_data_Bonus_LP_WL);

        label_bonusCabut_total_gramLP.setBackground(new java.awt.Color(255, 255, 255));
        label_bonusCabut_total_gramLP.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_bonusCabut_total_gramLP.setText("88");

        jLabel24.setBackground(new java.awt.Color(255, 255, 255));
        jLabel24.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel24.setText("Total LP :");

        button_export_bonus_LP_WL.setBackground(new java.awt.Color(255, 255, 255));
        button_export_bonus_LP_WL.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_bonus_LP_WL.setText("Export to Excel");
        button_export_bonus_LP_WL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_bonus_LP_WLActionPerformed(evt);
            }
        });

        jLabel31.setBackground(new java.awt.Color(255, 255, 255));
        jLabel31.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel31.setText("Total Bonus :");

        label_bonusCabut_total_bonusLP.setBackground(new java.awt.Color(255, 255, 255));
        label_bonusCabut_total_bonusLP.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_bonusCabut_total_bonusLP.setText("88");

        jLabel28.setBackground(new java.awt.Color(255, 255, 255));
        jLabel28.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel28.setText("Total Bobot LP :");

        jLabel30.setBackground(new java.awt.Color(255, 255, 255));
        jLabel30.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel30.setText("Total Nilai LP :");

        label_bonusCabut_total_nilaiLP.setBackground(new java.awt.Color(255, 255, 255));
        label_bonusCabut_total_nilaiLP.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_bonusCabut_total_nilaiLP.setText("88");

        jLabel29.setBackground(new java.awt.Color(255, 255, 255));
        jLabel29.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel29.setText("Total Gram :");

        javax.swing.GroupLayout jPanel_bonus_LP_WLLayout = new javax.swing.GroupLayout(jPanel_bonus_LP_WL);
        jPanel_bonus_LP_WL.setLayout(jPanel_bonus_LP_WLLayout);
        jPanel_bonus_LP_WLLayout.setHorizontalGroup(
            jPanel_bonus_LP_WLLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_bonus_LP_WLLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_bonus_LP_WLLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_bonus_LP_WLLayout.createSequentialGroup()
                        .addComponent(jLabel24)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_bonusCabut_total_LP)
                        .addGap(14, 14, 14)
                        .addComponent(jLabel29)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_bonusCabut_total_gramLP)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel28)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_bonusCabut_total_bobotLP)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel30)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_bonusCabut_total_nilaiLP)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel31)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_bonusCabut_total_bonusLP)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_export_bonus_LP_WL))
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 1336, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel_bonus_LP_WLLayout.setVerticalGroup(
            jPanel_bonus_LP_WLLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_bonus_LP_WLLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_bonus_LP_WLLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_bonusCabut_total_LP, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export_bonus_LP_WL, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_bonusCabut_total_nilaiLP, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_bonusCabut_total_bonusLP, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_bonusCabut_total_bobotLP, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_bonusCabut_total_gramLP, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 484, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane4.addTab("LP WL", jPanel_bonus_LP_WL);

        jPanel_bonus_LP_BP.setBackground(new java.awt.Color(255, 255, 255));

        label_bonusCabut_total_bobotLP_BP.setBackground(new java.awt.Color(255, 255, 255));
        label_bonusCabut_total_bobotLP_BP.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_bonusCabut_total_bobotLP_BP.setText("88");

        label_bonusCabut_total_LP_BP.setBackground(new java.awt.Color(255, 255, 255));
        label_bonusCabut_total_LP_BP.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_bonusCabut_total_LP_BP.setText("88");

        table_data_Bonus_LP_BP.setAutoCreateRowSorter(true);
        table_data_Bonus_LP_BP.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_data_Bonus_LP_BP.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No LP", "Grade", "Ruangan", "Jenis Blu Upah", "Setor Cabut", "Kpg", "Sesekan Bersih", "Bobot LP", "Nilai LP", "Bonus LP"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table_data_Bonus_LP_BP.getTableHeader().setReorderingAllowed(false);
        jScrollPane13.setViewportView(table_data_Bonus_LP_BP);

        label_bonusCabut_total_gramLP_BP.setBackground(new java.awt.Color(255, 255, 255));
        label_bonusCabut_total_gramLP_BP.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_bonusCabut_total_gramLP_BP.setText("88");

        jLabel46.setBackground(new java.awt.Color(255, 255, 255));
        jLabel46.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel46.setText("Total LP :");

        button_export_bonus_LP_BP.setBackground(new java.awt.Color(255, 255, 255));
        button_export_bonus_LP_BP.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_bonus_LP_BP.setText("Export to Excel");
        button_export_bonus_LP_BP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_bonus_LP_BPActionPerformed(evt);
            }
        });

        jLabel47.setBackground(new java.awt.Color(255, 255, 255));
        jLabel47.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel47.setText("Total Bonus :");

        label_bonusCabut_total_bonus_LP_BP.setBackground(new java.awt.Color(255, 255, 255));
        label_bonusCabut_total_bonus_LP_BP.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_bonusCabut_total_bonus_LP_BP.setText("88");

        jLabel48.setBackground(new java.awt.Color(255, 255, 255));
        jLabel48.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel48.setText("Total Bobot LP :");

        jLabel50.setBackground(new java.awt.Color(255, 255, 255));
        jLabel50.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel50.setText("Total Gram :");

        javax.swing.GroupLayout jPanel_bonus_LP_BPLayout = new javax.swing.GroupLayout(jPanel_bonus_LP_BP);
        jPanel_bonus_LP_BP.setLayout(jPanel_bonus_LP_BPLayout);
        jPanel_bonus_LP_BPLayout.setHorizontalGroup(
            jPanel_bonus_LP_BPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_bonus_LP_BPLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_bonus_LP_BPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_bonus_LP_BPLayout.createSequentialGroup()
                        .addComponent(jLabel46)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_bonusCabut_total_LP_BP)
                        .addGap(14, 14, 14)
                        .addComponent(jLabel50)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_bonusCabut_total_gramLP_BP)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel48)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_bonusCabut_total_bobotLP_BP)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel47)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_bonusCabut_total_bonus_LP_BP)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_export_bonus_LP_BP))
                    .addComponent(jScrollPane13, javax.swing.GroupLayout.DEFAULT_SIZE, 1336, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel_bonus_LP_BPLayout.setVerticalGroup(
            jPanel_bonus_LP_BPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_bonus_LP_BPLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_bonus_LP_BPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel46, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_bonusCabut_total_LP_BP, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export_bonus_LP_BP, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel47, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_bonusCabut_total_bonus_LP_BP, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel48, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_bonusCabut_total_bobotLP_BP, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel50, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_bonusCabut_total_gramLP_BP, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane13, javax.swing.GroupLayout.DEFAULT_SIZE, 484, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane4.addTab("LP BP (Sesekan)", jPanel_bonus_LP_BP);

        jPanel_bonus_LP_SP.setBackground(new java.awt.Color(255, 255, 255));

        label_bonusCabut_total_bobotLP_SP.setBackground(new java.awt.Color(255, 255, 255));
        label_bonusCabut_total_bobotLP_SP.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_bonusCabut_total_bobotLP_SP.setText("88");

        label_bonusCabut_total_LP_SP.setBackground(new java.awt.Color(255, 255, 255));
        label_bonusCabut_total_LP_SP.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_bonusCabut_total_LP_SP.setText("88");

        table_data_Bonus_LP_SP.setAutoCreateRowSorter(true);
        table_data_Bonus_LP_SP.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_data_Bonus_LP_SP.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No LP", "Grade", "Ruangan", "Jenis Blu Upah", "Setor Cabut", "Kpg", "Sesekan Bersih", "Bobot LP", "Nilai LP", "Bonus LP"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table_data_Bonus_LP_SP.getTableHeader().setReorderingAllowed(false);
        jScrollPane18.setViewportView(table_data_Bonus_LP_SP);

        label_bonusCabut_total_gramLP_SP.setBackground(new java.awt.Color(255, 255, 255));
        label_bonusCabut_total_gramLP_SP.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_bonusCabut_total_gramLP_SP.setText("88");

        jLabel49.setBackground(new java.awt.Color(255, 255, 255));
        jLabel49.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel49.setText("Total LP :");

        button_export_bonus_LP_SP.setBackground(new java.awt.Color(255, 255, 255));
        button_export_bonus_LP_SP.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_bonus_LP_SP.setText("Export to Excel");
        button_export_bonus_LP_SP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_bonus_LP_SPActionPerformed(evt);
            }
        });

        jLabel51.setBackground(new java.awt.Color(255, 255, 255));
        jLabel51.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel51.setText("Total Bonus :");

        label_bonusCabut_total_bonus_LP_SP.setBackground(new java.awt.Color(255, 255, 255));
        label_bonusCabut_total_bonus_LP_SP.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_bonusCabut_total_bonus_LP_SP.setText("88");

        jLabel52.setBackground(new java.awt.Color(255, 255, 255));
        jLabel52.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel52.setText("Total Bobot LP :");

        jLabel53.setBackground(new java.awt.Color(255, 255, 255));
        jLabel53.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel53.setText("Total Gram :");

        javax.swing.GroupLayout jPanel_bonus_LP_SPLayout = new javax.swing.GroupLayout(jPanel_bonus_LP_SP);
        jPanel_bonus_LP_SP.setLayout(jPanel_bonus_LP_SPLayout);
        jPanel_bonus_LP_SPLayout.setHorizontalGroup(
            jPanel_bonus_LP_SPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_bonus_LP_SPLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_bonus_LP_SPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_bonus_LP_SPLayout.createSequentialGroup()
                        .addComponent(jLabel49)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_bonusCabut_total_LP_SP)
                        .addGap(14, 14, 14)
                        .addComponent(jLabel53)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_bonusCabut_total_gramLP_SP)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel52)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_bonusCabut_total_bobotLP_SP)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel51)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_bonusCabut_total_bonus_LP_SP)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_export_bonus_LP_SP))
                    .addComponent(jScrollPane18, javax.swing.GroupLayout.DEFAULT_SIZE, 1336, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel_bonus_LP_SPLayout.setVerticalGroup(
            jPanel_bonus_LP_SPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_bonus_LP_SPLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_bonus_LP_SPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel49, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_bonusCabut_total_LP_SP, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export_bonus_LP_SP, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel51, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_bonusCabut_total_bonus_LP_SP, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel52, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_bonusCabut_total_bobotLP_SP, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel53, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_bonusCabut_total_gramLP_SP, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane18, javax.swing.GroupLayout.DEFAULT_SIZE, 484, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane4.addTab("LP SP (Sapon)", jPanel_bonus_LP_SP);

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));

        table_data_RekapBonus_LP_Sub.setAutoCreateRowSorter(true);
        table_data_RekapBonus_LP_Sub.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_data_RekapBonus_LP_Sub.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "SUB", "Total Gram", "Bobot LP WL", "Bobot LP BP", "Bobot LP SP", "Total Bobot LP", "Bonus Cabut", "Bonus Sesekan", "Bonus Sapon", "Total Bonus", "Upah Cuci / Karyawan", "Bonus Cuci / Karyawan"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table_data_RekapBonus_LP_Sub.getTableHeader().setReorderingAllowed(false);
        jScrollPane6.setViewportView(table_data_RekapBonus_LP_Sub);

        button_export_bonus_LP_Sub.setBackground(new java.awt.Color(255, 255, 255));
        button_export_bonus_LP_Sub.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_bonus_LP_Sub.setText("Export to Excel");
        button_export_bonus_LP_Sub.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_bonus_LP_SubActionPerformed(evt);
            }
        });

        jLabel26.setBackground(new java.awt.Color(255, 255, 255));
        jLabel26.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel26.setText("Rekap Bonus LP / Sub");

        jLabel25.setBackground(new java.awt.Color(255, 255, 255));
        jLabel25.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel25.setText("Total Bonus :");

        label_total_bonusCabut_Sub.setBackground(new java.awt.Color(255, 255, 255));
        label_total_bonusCabut_Sub.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_bonusCabut_Sub.setText("88");

        jTextArea2.setEditable(false);
        jTextArea2.setColumns(20);
        jTextArea2.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        jTextArea2.setLineWrap(true);
        jTextArea2.setRows(5);
        jTextArea2.setText("Note :\n1. Bonus LP diberikan kepada SUB yang mengerjakan >= 3 bobot LP (gabungan WL, BP, SP) dalam 1 minggu (akan diproporsional berdasarkan jumlah hari kerja).\n2. Bonus Operasional = 10.000 / 1kg LP dikerjakan.\n3. Bonus Mk utuh diambil dari data Bonus MK utuh waleta, untuk melihat detail besaran bonus setiap bulu upah dan aturan2nya silahkan ke menu bonus mk utuh waleta.\n4. Bonus Cuci 1 (80%), Cuci 2 (90%), Cuci 3 (100%)");
        jScrollPane7.setViewportView(jTextArea2);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel26)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel25)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_bonusCabut_Sub)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_export_bonus_LP_Sub))
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 1336, Short.MAX_VALUE)
                    .addComponent(jScrollPane7))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export_bonus_LP_Sub, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_bonusCabut_Sub, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 407, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane4.addTab("Rekap Bonus LP / Sub", jPanel7);

        jLabel54.setBackground(new java.awt.Color(255, 255, 255));
        jLabel54.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel54.setText("Loading :");

        jProgressBar1.setBackground(new java.awt.Color(255, 255, 255));
        jProgressBar1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jTextArea4.setEditable(false);
        jTextArea4.setColumns(20);
        jTextArea4.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        jTextArea4.setRows(3);
        jTextArea4.setText("1. LP WL dengan kode grade \"KK/KULIT\" akan menggunakan perhitungan bobot LP dan bonus LP sesekan.\n2. Bonus Sesekan dan Bonus Sapon menggunakan bonus sesekan / gram di tabel tarif SUB.");
        jScrollPane19.setViewportView(jTextArea4);

        jLabel55.setBackground(new java.awt.Color(255, 255, 255));
        jLabel55.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel55.setText("Min LP Dikerjakan :");

        txt_min_lp_dikerjakan.setEditable(false);
        txt_min_lp_dikerjakan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_min_lp_dikerjakan.setText("3");

        javax.swing.GroupLayout jPanel_Bonus_CabutLayout = new javax.swing.GroupLayout(jPanel_Bonus_Cabut);
        jPanel_Bonus_Cabut.setLayout(jPanel_Bonus_CabutLayout);
        jPanel_Bonus_CabutLayout.setHorizontalGroup(
            jPanel_Bonus_CabutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Bonus_CabutLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Date_tutupan_bonusCabut, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel27)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_search_no_lp_bonusCabut, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel23)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ComboBox_sub_bonusCabut, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_refresh_bonusCabut)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_jumlah_hari_kerja_bonus, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel55)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_min_lp_dikerjakan, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel54)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jTabbedPane4, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(jPanel_Bonus_CabutLayout.createSequentialGroup()
                .addComponent(jScrollPane19, javax.swing.GroupLayout.PREFERRED_SIZE, 839, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel_Bonus_CabutLayout.setVerticalGroup(
            jPanel_Bonus_CabutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Bonus_CabutLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_Bonus_CabutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_tutupan_bonusCabut, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_refresh_bonusCabut, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_no_lp_bonusCabut, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_sub_bonusCabut, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_jumlah_hari_kerja_bonus, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel55, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_min_lp_dikerjakan, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel54, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTabbedPane4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane19, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jTabbedPane1.addTab("BONUS SUB", jPanel_Bonus_Cabut);

        jPanel_Bonus_MKU.setBackground(new java.awt.Color(255, 255, 255));

        button_refresh_bonusMKU.setBackground(new java.awt.Color(255, 255, 255));
        button_refresh_bonusMKU.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_refresh_bonusMKU.setText("Refresh");
        button_refresh_bonusMKU.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refresh_bonusMKUActionPerformed(evt);
            }
        });

        Date_tutupan_bonusMKU.setBackground(new java.awt.Color(255, 255, 255));
        Date_tutupan_bonusMKU.setDate(new Date());
        Date_tutupan_bonusMKU.setDateFormatString("dd MMMM yyyy");
        Date_tutupan_bonusMKU.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel21.setBackground(new java.awt.Color(255, 255, 255));
        jLabel21.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel21.setText("Tanggal Tutupan (Sabtu) :");

        jLabel32.setBackground(new java.awt.Color(255, 255, 255));
        jLabel32.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel32.setText("Sub :");

        ComboBox_sub_bonusMKU.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        ComboBox_sub_bonusMKU.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));
        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Bonus MKU Cabut", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 11))); // NOI18N

        label_bonusMKU_Cabut_total_LP.setBackground(new java.awt.Color(255, 255, 255));
        label_bonusMKU_Cabut_total_LP.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_bonusMKU_Cabut_total_LP.setText("88");

        button_export_bonusMKU_Cabut.setBackground(new java.awt.Color(255, 255, 255));
        button_export_bonusMKU_Cabut.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_bonusMKU_Cabut.setText("Export to Excel");
        button_export_bonusMKU_Cabut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_bonusMKU_CabutActionPerformed(evt);
            }
        });

        table_bonusMKU_Cabut.setAutoCreateRowSorter(true);
        table_bonusMKU_Cabut.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_bonusMKU_Cabut.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No LP", "Grade", "Ruangan", "Bulu Upah", "Setor F2", "F Bonus + 0", "Kpg Pch", "Kpg Flat", "Kpg Bonus", "Bonus MKU Cabut/Kpg", "Bonus MKU Cabut", "ID", "Nama", "Bagian", "Upah cabut", "Upah LP", "Bonus MKU"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table_bonusMKU_Cabut.getTableHeader().setReorderingAllowed(false);
        jScrollPane8.setViewportView(table_bonusMKU_Cabut);
        if (table_bonusMKU_Cabut.getColumnModel().getColumnCount() > 0) {
            table_bonusMKU_Cabut.getColumnModel().getColumn(9).setHeaderValue("Bonus MKU Cabut/Kpg");
            table_bonusMKU_Cabut.getColumnModel().getColumn(10).setHeaderValue("Bonus MKU Cabut");
            table_bonusMKU_Cabut.getColumnModel().getColumn(14).setHeaderValue("Upah cabut");
            table_bonusMKU_Cabut.getColumnModel().getColumn(15).setHeaderValue("Upah LP");
            table_bonusMKU_Cabut.getColumnModel().getColumn(16).setHeaderValue("Bonus MKU");
        }

        jLabel34.setBackground(new java.awt.Color(255, 255, 255));
        jLabel34.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel34.setText("Total LP :");

        label_bonusMKU_Cabut_total_bonusLP.setBackground(new java.awt.Color(255, 255, 255));
        label_bonusMKU_Cabut_total_bonusLP.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_bonusMKU_Cabut_total_bonusLP.setText("88");

        jLabel38.setBackground(new java.awt.Color(255, 255, 255));
        jLabel38.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel38.setText("Total Bonus MKU :");

        jLabel42.setBackground(new java.awt.Color(255, 255, 255));
        jLabel42.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel42.setText("Total Bonus MKU Karyawan :");

        label_bonusMKU_Cabut_total_bonusKaryawan.setBackground(new java.awt.Color(255, 255, 255));
        label_bonusMKU_Cabut_total_bonusKaryawan.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_bonusMKU_Cabut_total_bonusKaryawan.setText("88");

        jLabel35.setBackground(new java.awt.Color(255, 255, 255));
        jLabel35.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel35.setText("Total Data :");

        label_bonusMKU_Cabut_total_Data.setBackground(new java.awt.Color(255, 255, 255));
        label_bonusMKU_Cabut_total_Data.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_bonusMKU_Cabut_total_Data.setText("88");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 753, Short.MAX_VALUE)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel35)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_bonusMKU_Cabut_total_Data)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel34)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_bonusMKU_Cabut_total_LP)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel38)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_bonusMKU_Cabut_total_bonusLP)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel42)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_bonusMKU_Cabut_total_bonusKaryawan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_export_bonusMKU_Cabut)))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel42, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_bonusMKU_Cabut_total_bonusKaryawan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_bonusMKU_Cabut_total_Data, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_bonusMKU_Cabut_total_LP, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_export_bonusMKU_Cabut, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_bonusMKU_Cabut_total_bonusLP, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));

        table_bonusMKU_Sub.setAutoCreateRowSorter(true);
        table_bonusMKU_Sub.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_bonusMKU_Sub.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "SUB", "Bonus MKU Cabut", "Bonus MKU Cetak"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table_bonusMKU_Sub.getTableHeader().setReorderingAllowed(false);
        jScrollPane10.setViewportView(table_bonusMKU_Sub);

        button_export_bonusMKU_sub.setBackground(new java.awt.Color(255, 255, 255));
        button_export_bonusMKU_sub.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_bonusMKU_sub.setText("Export to Excel");
        button_export_bonusMKU_sub.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_bonusMKU_subActionPerformed(evt);
            }
        });

        jLabel39.setBackground(new java.awt.Color(255, 255, 255));
        jLabel39.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel39.setText("Rekap Bonus Cabut / Sub");

        jLabel40.setBackground(new java.awt.Color(255, 255, 255));
        jLabel40.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel40.setText("Total Bonus :");

        label_total_bonusMKU_Sub.setBackground(new java.awt.Color(255, 255, 255));
        label_total_bonusMKU_Sub.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_bonusMKU_Sub.setText("88");

        jTextArea3.setEditable(false);
        jTextArea3.setColumns(20);
        jTextArea3.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        jTextArea3.setLineWrap(true);
        jTextArea3.setRows(5);
        jTextArea3.setText("Note :");
        jScrollPane11.setViewportView(jTextArea3);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel39)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel40)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_bonusMKU_Sub)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_export_bonusMKU_sub))
                    .addComponent(jScrollPane10, javax.swing.GroupLayout.DEFAULT_SIZE, 540, Short.MAX_VALUE)
                    .addComponent(jScrollPane11))
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export_bonusMKU_sub, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_bonusMKU_Sub, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane10, javax.swing.GroupLayout.DEFAULT_SIZE, 504, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jLabel41.setBackground(new java.awt.Color(255, 255, 255));
        jLabel41.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel41.setText("No LP :");

        txt_search_no_lp_bonusMKU.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_search_no_lp_bonusMKU.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_no_lp_bonusMKUKeyPressed(evt);
            }
        });

        jPanel13.setBackground(new java.awt.Color(255, 255, 255));
        jPanel13.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Bonus MKU Cetak", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 11))); // NOI18N

        label_bonusMKU_Cetak_total_LP.setBackground(new java.awt.Color(255, 255, 255));
        label_bonusMKU_Cetak_total_LP.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_bonusMKU_Cetak_total_LP.setText("88");

        button_export_bonusMKU_Cetak.setBackground(new java.awt.Color(255, 255, 255));
        button_export_bonusMKU_Cetak.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_bonusMKU_Cetak.setText("Export to Excel");
        button_export_bonusMKU_Cetak.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_bonusMKU_CetakActionPerformed(evt);
            }
        });

        table_bonusMKU_Cetak.setAutoCreateRowSorter(true);
        table_bonusMKU_Cetak.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_bonusMKU_Cetak.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No LP", "Grade", "Ruangan", "Bulu Upah", "Setor F2", "F Bonus + 0", "Kpg Pch", "Kpg Flat", "Kpg Bonus", "Bonus MKU Cetak/Kpg", "Bonus MKU Cetak", "ID", "Nama", "Bagian"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table_bonusMKU_Cetak.getTableHeader().setReorderingAllowed(false);
        jScrollPane20.setViewportView(table_bonusMKU_Cetak);

        jLabel56.setBackground(new java.awt.Color(255, 255, 255));
        jLabel56.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel56.setText("Total LP :");

        label_bonusMKU_Cetak_total_bonusLP.setBackground(new java.awt.Color(255, 255, 255));
        label_bonusMKU_Cetak_total_bonusLP.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_bonusMKU_Cetak_total_bonusLP.setText("88");

        jLabel57.setBackground(new java.awt.Color(255, 255, 255));
        jLabel57.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel57.setText("Total Bonus MKU Cetak :");

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel1.setText("Total Bonus MKU Cetak Karyawan :");

        label_bonusMKU_Cetak_total_bonusKaryawan.setBackground(new java.awt.Color(255, 255, 255));
        label_bonusMKU_Cetak_total_bonusKaryawan.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_bonusMKU_Cetak_total_bonusKaryawan.setText("88");

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane20)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addComponent(jLabel56)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_bonusMKU_Cetak_total_LP)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel57)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_bonusMKU_Cetak_total_bonusLP)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_bonusMKU_Cetak_total_bonusKaryawan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_export_bonusMKU_Cetak)))
                .addContainerGap())
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel56, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_bonusMKU_Cetak_total_LP, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export_bonusMKU_Cetak, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel57, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_bonusMKU_Cetak_total_bonusLP, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_bonusMKU_Cetak_total_bonusKaryawan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane20, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel_Bonus_MKULayout = new javax.swing.GroupLayout(jPanel_Bonus_MKU);
        jPanel_Bonus_MKU.setLayout(jPanel_Bonus_MKULayout);
        jPanel_Bonus_MKULayout.setHorizontalGroup(
            jPanel_Bonus_MKULayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Bonus_MKULayout.createSequentialGroup()
                .addGroup(jPanel_Bonus_MKULayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(jPanel_Bonus_MKULayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel21)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Date_tutupan_bonusMKU, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel41)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_search_no_lp_bonusMKU, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel32)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ComboBox_sub_bonusMKU, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_refresh_bonusMKU)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel_Bonus_MKULayout.setVerticalGroup(
            jPanel_Bonus_MKULayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Bonus_MKULayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_Bonus_MKULayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_tutupan_bonusMKU, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_refresh_bonusMKU, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_no_lp_bonusMKU, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_sub_bonusMKU, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_Bonus_MKULayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel_Bonus_MKULayout.createSequentialGroup()
                        .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        jTabbedPane1.addTab("BONUS MK UTUH", jPanel_Bonus_MKU);

        jPanel_Upah_Karyawan_Sub.setBackground(new java.awt.Color(255, 255, 255));

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setText("Tanggal Tutupan (Sabtu) :");

        Date_tutupan_upahKaryawan.setBackground(new java.awt.Color(255, 255, 255));
        Date_tutupan_upahKaryawan.setDateFormatString("dd MMMM yyyy");
        Date_tutupan_upahKaryawan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        button_refresh_perhitungan_upah.setBackground(new java.awt.Color(255, 255, 255));
        button_refresh_perhitungan_upah.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_refresh_perhitungan_upah.setText("Refresh");
        button_refresh_perhitungan_upah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refresh_perhitungan_upahActionPerformed(evt);
            }
        });

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel15.setText("Nama :");

        txt_search_nama2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_search_nama2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_nama2KeyPressed(evt);
            }
        });

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel17.setText("Jumlah Hari Kerja :");

        txt_jumlah_hari_kerja.setEditable(false);
        txt_jumlah_hari_kerja.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_jumlah_hari_kerja.setText("0");

        button_export2.setText("Export Detail Karyawan");
        button_export2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export2ActionPerformed(evt);
            }
        });

        jLabel20.setBackground(new java.awt.Color(255, 255, 255));
        jLabel20.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel20.setText("Sub :");

        txt_search_sub.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_search_sub.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_subKeyPressed(evt);
            }
        });

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        jTextArea1.setRows(5);
        jTextArea1.setText("1. Hanya mengambil data karyawan yang absen pada periode terpilih.\n2. hari kerja = minimal absen 1x\n3. Tunjangan hadir diberikan ke anak yang hadir 6 hari dlm seminggu dan absennya lengkap\n4. Jika Level gaji Training 1-6, bonus cabut = 0.\n5. bonus cabut hanya diberikan kepada karyawan BUKAN level gaji \"Training\".\n6. Total keping cabutan >=30 kpg untuk pekerja LP WL non KK/KULIT yang berhak mendapat bonus LP.\n7. Total jumlah upah Sesekan, Sapon dan KK/KULIT >= Rp.120.000 untuk pekerja sesekan / sapon yang berhak mendapat bonus LP.\n8. bonus cabut karyawan = (gaji borong / total gaji borong cabut sub tsb (hanya total dari yang mendapatkan bonus)) * bonus cabut sub tsb.\n9. Setelah \"Save Data\", semua karyawan \"Training\" kan naik 1 level gaji, level gaji \"Training 6\" akan naik ke level Borong.\n10. Piutang = total semua piutang yang belum lunas sampai tanggal penggajian.");
        jScrollPane3.setViewportView(jTextArea1);

        jLabel33.setBackground(new java.awt.Color(255, 255, 255));
        jLabel33.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel33.setText("Target Cabut :");

        txt_target_cabut.setEditable(false);
        txt_target_cabut.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_target_cabut.setText("0");

        button_save_data_fix.setText("Save Data");
        button_save_data_fix.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_save_data_fixActionPerformed(evt);
            }
        });

        Tabel_data_perhitungan_upah_total.setAutoCreateRowSorter(true);
        Tabel_data_perhitungan_upah_total.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        Tabel_data_perhitungan_upah_total.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Total Data", "Total Upah Cabut", "Total Upah Sesekan", "Total Upah Sapon", "Total Bonus", "Total Upah Cuci", "Total Bonus Cuci", "Total Bonus MKU", "Total Subsidi Training", "Total Tunj. hadir", "Total Piutang", "Total Gaji Karyawan", "Total Bonus Operational", "Total Transfer"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Tabel_data_perhitungan_upah_total.setRowHeight(25);
        Tabel_data_perhitungan_upah_total.getTableHeader().setReorderingAllowed(false);
        jScrollPane9.setViewportView(Tabel_data_perhitungan_upah_total);

        jTabbedPane3.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        Tabel_data_perhitungan_upah.setAutoCreateRowSorter(true);
        Tabel_data_perhitungan_upah.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Tabel_data_perhitungan_upah.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "ID", "Nama", "Sub", "Tgl Masuk", "Tgl Keluar", "Status", "Level", "01 MMM", "02 MMM", "03 MMM", "04 MMM", "05 MMM", "06 MMM", "07 MMM", "Hari Kerja", "Tot Kpg Cbt", "Upah Cabut", "Upah KK Kulit", "Upah Sesekan", "Upah Sapon", "Total Upah", "Bonus", "Upah Cuci", "Bonus Cuci", "Bonus MKU", "Subsidi Training", "Tunj. hadir", "Piutang", "Gaji Trasfer", "~Gaji Trasfer"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Tabel_data_perhitungan_upah.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(Tabel_data_perhitungan_upah);

        jTabbedPane3.addTab("Data / Karyawan", jScrollPane1);

        Tabel_data_rekap_transfer_sub.setAutoCreateRowSorter(true);
        Tabel_data_rekap_transfer_sub.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Tabel_data_rekap_transfer_sub.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Sub", "Upah Cabut", "Bonus Cabut", "Upah Sesekan", "Bonus Sesekan", "Upah Sapon", "Bonus Sapon", "Upah Cuci", "Bonus Cuci", "Bonus MKU", "Subsidi Training", "Tunj. hadir", "Piutang", "Total Gaji Karyawan", "Bonus Operational", "Total Transfer Sub", "Total Upah Borong", "Kepala", "Bank", "No Rekening"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Tabel_data_rekap_transfer_sub.getTableHeader().setReorderingAllowed(false);
        jScrollPane12.setViewportView(Tabel_data_rekap_transfer_sub);

        jTabbedPane3.addTab("Rekap / SUB", jScrollPane12);

        button_naik_level_gaji.setText("Naik Level Gaji");
        button_naik_level_gaji.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_naik_level_gajiActionPerformed(evt);
            }
        });

        button_export_rekap_sub.setText("Export Rekap / Sub");
        button_export_rekap_sub.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_rekap_subActionPerformed(evt);
            }
        });

        button_form_laporan_sub.setText("Form Laporan / Sub");
        button_form_laporan_sub.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_form_laporan_subActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_Upah_Karyawan_SubLayout = new javax.swing.GroupLayout(jPanel_Upah_Karyawan_Sub);
        jPanel_Upah_Karyawan_Sub.setLayout(jPanel_Upah_Karyawan_SubLayout);
        jPanel_Upah_Karyawan_SubLayout.setHorizontalGroup(
            jPanel_Upah_Karyawan_SubLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Upah_Karyawan_SubLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_Upah_Karyawan_SubLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_Upah_Karyawan_SubLayout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 839, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel_Upah_Karyawan_SubLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(button_export2, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(button_export_rekap_sub, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(button_form_laporan_sub, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addGroup(jPanel_Upah_Karyawan_SubLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_tutupan_upahKaryawan, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_nama2, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_sub, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_refresh_perhitungan_upah)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_jumlah_hari_kerja, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel33)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_target_cabut, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 155, Short.MAX_VALUE)
                        .addComponent(button_naik_level_gaji)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_save_data_fix))
                    .addComponent(jScrollPane9)
                    .addComponent(jTabbedPane3))
                .addContainerGap())
        );
        jPanel_Upah_Karyawan_SubLayout.setVerticalGroup(
            jPanel_Upah_Karyawan_SubLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Upah_Karyawan_SubLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_Upah_Karyawan_SubLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_tutupan_upahKaryawan, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_nama2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_sub, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_refresh_perhitungan_upah, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_jumlah_hari_kerja, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_target_cabut, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel_Upah_Karyawan_SubLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(button_save_data_fix)
                        .addComponent(button_naik_level_gaji)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 414, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_Upah_Karyawan_SubLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel_Upah_Karyawan_SubLayout.createSequentialGroup()
                        .addComponent(button_export2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_export_rekap_sub)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_form_laporan_sub)))
                .addContainerGap())
        );

        jTabbedPane1.addTab("PERHITUNGAN UPAH KARYAWAN SUB", jPanel_Upah_Karyawan_Sub);

        jPanel_DataTersimpan.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_DataTersimpan.setPreferredSize(new java.awt.Dimension(1366, 701));

        button_export.setBackground(new java.awt.Color(255, 255, 255));
        button_export.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export.setText("Export To Excel");
        button_export.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_exportActionPerformed(evt);
            }
        });

        jLabel58.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel58.setText("Total Data :");

        label_total_data_tersimpan.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        label_total_data_tersimpan.setText("TOTAL");

        Date1.setBackground(new java.awt.Color(255, 255, 255));
        Date1.setDateFormatString("dd MMM yyyy");
        Date1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel4.setText("Tanggal Penggajian :");

        ComboBox_bagian.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_bagian.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel6.setText("Sub :");

        txt_nama.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_nama.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_namaKeyPressed(evt);
            }
        });

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("Nama :");

        txt_id.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_id.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_idKeyPressed(evt);
            }
        });

        jLabel59.setBackground(new java.awt.Color(255, 255, 255));
        jLabel59.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel59.setText("ID Pegawai :");

        button_search.setBackground(new java.awt.Color(255, 255, 255));
        button_search.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search.setText("Search");
        button_search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_searchActionPerformed(evt);
            }
        });

        Date2.setBackground(new java.awt.Color(255, 255, 255));
        Date2.setDateFormatString("dd MMM yyyy");
        Date2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Tabel_data_tersimpan.setAutoCreateRowSorter(true);
        Tabel_data_tersimpan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Tabel_data_tersimpan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tgl Gajian", "ID", "Nama", "Sub", "Status", "Level", "Hari Masuk Kerja", "Tot Kpg Cbt", "Upah Cabut", "Upah KK Kulit", "Bonus Cabut", "Upah Sesekan", "Bonus Sesekan", "Upah Sapon", "Bonus Sapon", "Upah Cuci", "Bonus Cuci", "Bonus MKU", "Subsidi Training", "Tunj. hadir", "Piutang", "Gaji Trasfer"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Tabel_data_tersimpan.getTableHeader().setReorderingAllowed(false);
        jScrollPane21.setViewportView(Tabel_data_tersimpan);

        javax.swing.GroupLayout jPanel_DataTersimpanLayout = new javax.swing.GroupLayout(jPanel_DataTersimpan);
        jPanel_DataTersimpan.setLayout(jPanel_DataTersimpanLayout);
        jPanel_DataTersimpanLayout.setHorizontalGroup(
            jPanel_DataTersimpanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_DataTersimpanLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_DataTersimpanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_DataTersimpanLayout.createSequentialGroup()
                        .addComponent(jLabel59)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_id, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_nama, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_bagian, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_search)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_export)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel58)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_data_tersimpan)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane21, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1341, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel_DataTersimpanLayout.setVerticalGroup(
            jPanel_DataTersimpanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_DataTersimpanLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_DataTersimpanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel_DataTersimpanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(jLabel59, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_id, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_nama, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(Date1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(Date2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel_DataTersimpanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ComboBox_bagian, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel_DataTersimpanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(button_search, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel_DataTersimpanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(button_export, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel_DataTersimpanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(label_total_data_tersimpan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel58, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane21, javax.swing.GroupLayout.DEFAULT_SIZE, 621, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("DATA PENGGAJIAN TERSIMPAN", jPanel_DataTersimpan);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void button_refresh_allDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refresh_allDataActionPerformed
        // TODO add your handling code here:
        refreshTable_data_pencabut();
        refreshTable_data_penyesek();
        refreshTable_data_sapon();
    }//GEN-LAST:event_button_refresh_allDataActionPerformed

    private void txt_search_namaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_namaKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_data_pencabut();
            refreshTable_data_penyesek();
            refreshTable_data_sapon();
        }
    }//GEN-LAST:event_txt_search_namaKeyPressed

    private void button_export_pencabutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_pencabutActionPerformed
        // TODO add your handling code here:
        DefaultTableModel modelA = (DefaultTableModel) table_data_pencabut.getModel();
        ExportToExcel.writeToExcel(modelA, this);
    }//GEN-LAST:event_button_export_pencabutActionPerformed

    private void button_export_rekap_pencabutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_rekap_pencabutActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_button_export_rekap_pencabutActionPerformed

    private void txt_search_no_lpKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_no_lpKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_search_no_lpKeyPressed

    private void button_refresh_perhitungan_upahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refresh_perhitungan_upahActionPerformed
        // TODO add your handling code here:
        if (table_data_pencabut.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Silahkan jalankan data kinerja borong terlebih dahulu !");
        } else if (table_data_RekapBonus_LP_Sub.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Silahkan jalankan perhitungan bonus cabut terlebih dahulu !");
        } else if (table_bonusMKU_Sub.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Silahkan jalankan perhitungan bonus MK utuh terlebih dahulu !");
        } else if ((dateFormat.format(Date_tutupan_upahKaryawan.getDate()) == null ? dateFormat.format(Date_kinerja_borong2.getDate()) != null : !dateFormat.format(Date_tutupan_upahKaryawan.getDate()).equals(dateFormat.format(Date_kinerja_borong2.getDate())))
                || (dateFormat.format(Date_tutupan_upahKaryawan.getDate()) == null ? dateFormat.format(Date_tutupan_bonusCabut.getDate()) != null : !dateFormat.format(Date_tutupan_upahKaryawan.getDate()).equals(dateFormat.format(Date_tutupan_bonusCabut.getDate())))
                || (dateFormat.format(Date_tutupan_upahKaryawan.getDate()) == null ? dateFormat.format(Date_tutupan_bonusMKU.getDate()) != null : !dateFormat.format(Date_tutupan_upahKaryawan.getDate()).equals(dateFormat.format(Date_tutupan_bonusMKU.getDate())))) {
            JOptionPane.showMessageDialog(this, "Tanggal penggajian yang di pilih harus sama semua di semua tab!");
        } else {
            refreshTable_penggajian();
        }
    }//GEN-LAST:event_button_refresh_perhitungan_upahActionPerformed

    private void txt_search_nama2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_nama2KeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_penggajian();
        }
    }//GEN-LAST:event_txt_search_nama2KeyPressed

    private void button_export2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export2ActionPerformed
        // TODO add your handling code here:
        DefaultTableModel table = (DefaultTableModel) Tabel_data_perhitungan_upah.getModel();
        ExportToExcel.writeToExcel(table, this);
    }//GEN-LAST:event_button_export2ActionPerformed

    private void txt_search_subKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_subKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_penggajian();
        }
    }//GEN-LAST:event_txt_search_subKeyPressed

    private void button_refresh_bonusCabutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refresh_bonusCabutActionPerformed
        // TODO add your handling code here:
        refresh_BonusLP_Sub();
    }//GEN-LAST:event_button_refresh_bonusCabutActionPerformed

    private void button_export_bonus_LP_WLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_bonus_LP_WLActionPerformed
        // TODO add your handling code here:
        DefaultTableModel table = (DefaultTableModel) table_data_Bonus_LP_WL.getModel();
        ExportToExcel.writeToExcel(table, this);
    }//GEN-LAST:event_button_export_bonus_LP_WLActionPerformed

    private void button_export_bonus_LP_SubActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_bonus_LP_SubActionPerformed
        // TODO add your handling code here:
        DefaultTableModel table = (DefaultTableModel) table_data_RekapBonus_LP_Sub.getModel();
        ExportToExcel.writeToExcel(table, this);
    }//GEN-LAST:event_button_export_bonus_LP_SubActionPerformed

    private void txt_search_no_lp_bonusCabutKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_no_lp_bonusCabutKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refresh_BonusLP_Sub();
        }
    }//GEN-LAST:event_txt_search_no_lp_bonusCabutKeyPressed

    private void button_save_data_fixActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_save_data_fixActionPerformed
        // TODO add your handling code here:
        Date tanggal_mulai = new Date(Date_tutupan_upahKaryawan.getDate().getTime() - (6 * 24 * 60 * 60 * 1000));
        Date tanggal_selesai = new Date(Date_tutupan_upahKaryawan.getDate().getTime() - (0 * 24 * 60 * 60 * 1000));
        int dialogResult = JOptionPane.showConfirmDialog(this, "Setelah simpan data penggajian, \n"
                + "1. Level Training TIDAK otomatis naik setelah simpan data penggajian\n"
                + "2. Piutang dianggap lunas\n"
                + "Lanjutkan?", "Warning", 0);
        if (dialogResult == JOptionPane.YES_OPTION) {
            try {
                int saved_data_payroll = 0;
                Utility.db_sub.connect();
                Utility.db_sub.getConnection().setAutoCommit(false);
                for (int i = 0; i < Tabel_data_perhitungan_upah.getRowCount(); i++) {

                    float piutang = Float.valueOf(Tabel_data_perhitungan_upah.getValueAt(i, 29).toString());
                    if (piutang != 0) {
                        String query_update_piutang = "UPDATE `tb_piutang_karyawan` SET `status` = 1, `tgl_lunas` = '" + dateFormat.format(tanggal_selesai) + "' "
                                + "WHERE `id_pegawai` = '" + Tabel_data_perhitungan_upah.getValueAt(i, 1).toString() + "' "
                                + "AND `tanggal_piutang` <= '" + dateFormat.format(tanggal_selesai) + "' AND `status` = 0";
                        Utility.db_sub.getConnection().createStatement();
                        Utility.db_sub.getStatement().executeUpdate(query_update_piutang);
                    }

                    String insert_payroll_data = "INSERT INTO `tb_slip_gaji`(`tgl_penggajian`, `id_pegawai`, `sub`, `level_gaji`, `hari_masuk_kerja`, `kpg_cabut`, `upah_cabut`, `upah_kk_kulit`, `bonus_cabut`, `upah_sesekan`, `bonus_sesekan`, `upah_sapon`, `bonus_sapon`, `upah_cuci`, `bonus_cuci`, `bonus_mku`, `subsidi_training`, `tunjangan_hadir`, `piutang`, `gaji_transfer`) "
                            + "VALUES ('" + dateFormat.format(tanggal_selesai) + "',"
                            + "'" + Tabel_data_perhitungan_upah.getValueAt(i, 1).toString() + "'," //id_pegawai
                            + "'" + Tabel_data_perhitungan_upah.getValueAt(i, 3).toString() + "'," //sub
                            + "'" + Tabel_data_perhitungan_upah.getValueAt(i, 7).toString() + "'," //level_gaji
                            + "'" + Tabel_data_perhitungan_upah.getValueAt(i, 15).toString() + "'," //hari_masuk_kerja
                            + "'" + Tabel_data_perhitungan_upah.getValueAt(i, 16).toString() + "'," //kpg_cabut
                            + "'" + Tabel_data_perhitungan_upah.getValueAt(i, 17).toString() + "'," //upah_cabut
                            + "'" + Tabel_data_perhitungan_upah.getValueAt(i, 18).toString() + "'," //upah_kk_kulit
                            + "'" + Tabel_data_perhitungan_upah.getValueAt(i, 22).toString() + "'," //bonus_cabut
                            + "'" + Tabel_data_perhitungan_upah.getValueAt(i, 19).toString() + "'," //upah_sesekan
                            + "'0'," //bonus_sesekan
                            + "'" + Tabel_data_perhitungan_upah.getValueAt(i, 20).toString() + "'," //upah_sapon
                            + "'0'," //bonus_sapon
                            + "'" + Tabel_data_perhitungan_upah.getValueAt(i, 23).toString() + "'," //upah_cuci
                            + "'" + Tabel_data_perhitungan_upah.getValueAt(i, 24).toString() + "'," //bonus_cuci
                            + "'" + Tabel_data_perhitungan_upah.getValueAt(i, 25).toString() + "'," //bonus_mku
                            + "'" + Tabel_data_perhitungan_upah.getValueAt(i, 26).toString() + "'," //subsidi_training
                            + "'" + Tabel_data_perhitungan_upah.getValueAt(i, 27).toString() + "'," //tunjangan_hadir
                            + "'" + Tabel_data_perhitungan_upah.getValueAt(i, 28).toString() + "'," //piutang
                            + "'" + Tabel_data_perhitungan_upah.getValueAt(i, 30).toString() + "'" //gaji_transfer
                            + ")"
                            + "ON DUPLICATE KEY UPDATE "
                            + "`sub`='" + Tabel_data_perhitungan_upah.getValueAt(i, 3).toString() + "',"
                            + "`level_gaji`='" + Tabel_data_perhitungan_upah.getValueAt(i, 7).toString() + "',"
                            + "`hari_masuk_kerja`='" + Tabel_data_perhitungan_upah.getValueAt(i, 15).toString() + "',"
                            + "`kpg_cabut`='" + Tabel_data_perhitungan_upah.getValueAt(i, 16).toString() + "',"
                            + "`upah_cabut`='" + Tabel_data_perhitungan_upah.getValueAt(i, 17).toString() + "',"
                            + "`upah_kk_kulit`='" + Tabel_data_perhitungan_upah.getValueAt(i, 18).toString() + "',"
                            + "`bonus_cabut`='" + Tabel_data_perhitungan_upah.getValueAt(i, 22).toString() + "',"
                            + "`upah_sesekan`='" + Tabel_data_perhitungan_upah.getValueAt(i, 19).toString() + "',"
                            + "`bonus_sesekan`='0',"
                            + "`upah_sapon`='" + Tabel_data_perhitungan_upah.getValueAt(i, 20).toString() + "',"
                            + "`bonus_sapon`='0',"
                            + "`upah_cuci`='" + Tabel_data_perhitungan_upah.getValueAt(i, 23).toString() + "',"
                            + "`bonus_cuci`='" + Tabel_data_perhitungan_upah.getValueAt(i, 24).toString() + "',"
                            + "`bonus_mku`='" + Tabel_data_perhitungan_upah.getValueAt(i, 25).toString() + "',"
                            + "`subsidi_training`='" + Tabel_data_perhitungan_upah.getValueAt(i, 26).toString() + "',"
                            + "`tunjangan_hadir`='" + Tabel_data_perhitungan_upah.getValueAt(i, 27).toString() + "',"
                            + "`piutang`='" + Tabel_data_perhitungan_upah.getValueAt(i, 28).toString() + "',"
                            + "`gaji_transfer`='" + Tabel_data_perhitungan_upah.getValueAt(i, 30).toString() + "'";
                    Utility.db_sub.getConnection().createStatement();
                    if (Utility.db_sub.getStatement().executeUpdate(insert_payroll_data) == 1) {
                        saved_data_payroll++;
                    }
                }
                Utility.db_sub.getConnection().commit();
                JOptionPane.showMessageDialog(this, "data penggajian SUB berhasil tersimpan : " + saved_data_payroll);
            } catch (Exception e) {
                try {
                    Utility.db_sub.getConnection().rollback();
                } catch (SQLException ex) {
                    Logger.getLogger(JPanel_PenggajianSub.class.getName()).log(Level.SEVERE, null, ex);
                }
                JOptionPane.showMessageDialog(this, e);
                Logger.getLogger(JPanel_PenggajianSub.class.getName()).log(Level.SEVERE, null, e);
            } finally {
                try {
                    Utility.db_sub.getConnection().setAutoCommit(true);
                } catch (SQLException ex) {
                    Logger.getLogger(JPanel_PenggajianSub.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_button_save_data_fixActionPerformed

    private void button_refresh_bonusMKUActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refresh_bonusMKUActionPerformed
        // TODO add your handling code here:
        refresh_BonusMKU_Sub();
    }//GEN-LAST:event_button_refresh_bonusMKUActionPerformed

    private void button_export_bonusMKU_CabutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_bonusMKU_CabutActionPerformed
        // TODO add your handling code here:
        DefaultTableModel table = (DefaultTableModel) table_bonusMKU_Cabut.getModel();
        ExportToExcel.writeToExcel(table, this);
    }//GEN-LAST:event_button_export_bonusMKU_CabutActionPerformed

    private void button_export_bonusMKU_subActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_bonusMKU_subActionPerformed
        // TODO add your handling code here:
        DefaultTableModel table = (DefaultTableModel) table_bonusMKU_Sub.getModel();
        ExportToExcel.writeToExcel(table, this);
    }//GEN-LAST:event_button_export_bonusMKU_subActionPerformed

    private void txt_search_no_lp_bonusMKUKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_no_lp_bonusMKUKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refresh_BonusMKU_Sub();
        }
    }//GEN-LAST:event_txt_search_no_lp_bonusMKUKeyPressed

    private void button_export_penyesekActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_penyesekActionPerformed
        // TODO add your handling code here:
        DefaultTableModel table = (DefaultTableModel) table_data_pekerja_sesekan.getModel();
        ExportToExcel.writeToExcel(table, this);
    }//GEN-LAST:event_button_export_penyesekActionPerformed

    private void button_export_rekap_penyesekActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_rekap_penyesekActionPerformed
        // TODO add your handling code here:
        DefaultTableModel table = (DefaultTableModel) table_data_rekap_sesekan.getModel();
        ExportToExcel.writeToExcel(table, this);
    }//GEN-LAST:event_button_export_rekap_penyesekActionPerformed

    private void button_export5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export5ActionPerformed
        // TODO add your handling code here:
        DefaultTableModel table = (DefaultTableModel) table_data_pekerja_sapon.getModel();
        ExportToExcel.writeToExcel(table, this);
    }//GEN-LAST:event_button_export5ActionPerformed

    private void button_export6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export6ActionPerformed
        // TODO add your handling code here:
        DefaultTableModel table = (DefaultTableModel) table_data_rekap_sapon.getModel();
        ExportToExcel.writeToExcel(table, this);
    }//GEN-LAST:event_button_export6ActionPerformed

    private void button_export_bonus_LP_BPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_bonus_LP_BPActionPerformed
        // TODO add your handling code here:
        DefaultTableModel table = (DefaultTableModel) table_data_Bonus_LP_BP.getModel();
        ExportToExcel.writeToExcel(table, this);
    }//GEN-LAST:event_button_export_bonus_LP_BPActionPerformed

    private void button_export_bonus_LP_SPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_bonus_LP_SPActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_button_export_bonus_LP_SPActionPerformed

    private void button_naik_level_gajiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_naik_level_gajiActionPerformed
        // TODO add your handling code here:
        int dialogResult = JOptionPane.showConfirmDialog(this, "Level Training akan otomatis naik\n"
                + "Lanjutkan?", "Warning", 0);
        if (dialogResult == JOptionPane.YES_OPTION) {
            try {
                int saved_data_payroll = 0;
                Utility.db_sub.connect();
                Utility.db_sub.getConnection().setAutoCommit(false);
                for (int i = 0; i < Tabel_data_perhitungan_upah.getRowCount(); i++) {
                    String level_gaji_lama = Tabel_data_perhitungan_upah.getValueAt(i, 7).toString();
                    if (level_gaji_lama.contains("Training")) {
                        int level_training = Integer.valueOf(level_gaji_lama.split(" ")[1]);
                        String level_gaji_baru = level_gaji_lama.split(" ")[0] + " " + String.format("%02d", level_training + 1);
                        if (level_training == 6) {
                            level_gaji_baru = "Borong";
                        }
                        String query_update_lvlGaji = "UPDATE `tb_karyawan` SET `level_gaji` = '" + level_gaji_baru + "' "
                                + "WHERE `id_pegawai` = '" + Tabel_data_perhitungan_upah.getValueAt(i, 1).toString() + "' AND `level_gaji` LIKE 'Training __'";
                        Utility.db_sub.getConnection().createStatement();
                        Utility.db_sub.getStatement().executeUpdate(query_update_lvlGaji);
                        saved_data_payroll++;
                    }
                }
                Utility.db_sub.getConnection().commit();
                JOptionPane.showMessageDialog(this, "Jumlah Karyawan naik level : " + saved_data_payroll);
            } catch (Exception e) {
                try {
                    Utility.db_sub.getConnection().rollback();
                } catch (SQLException ex) {
                    Logger.getLogger(JPanel_PenggajianSub.class.getName()).log(Level.SEVERE, null, ex);
                }
                JOptionPane.showMessageDialog(this, e);
                Logger.getLogger(JPanel_PenggajianSub.class.getName()).log(Level.SEVERE, null, e);
            } finally {
                try {
                    Utility.db_sub.getConnection().setAutoCommit(true);
                } catch (SQLException ex) {
                    Logger.getLogger(JPanel_PenggajianSub.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_button_naik_level_gajiActionPerformed

    private void button_export_rekap_subActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_rekap_subActionPerformed
        // TODO add your handling code here:
        DefaultTableModel table = (DefaultTableModel) table_data_Bonus_LP_BP.getModel();
        ExportToExcel.writeToExcel(table, this);
    }//GEN-LAST:event_button_export_rekap_subActionPerformed

    private void button_form_laporan_subActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_form_laporan_subActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_button_form_laporan_subActionPerformed

    private void button_export_bonusMKU_CetakActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_bonusMKU_CetakActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_button_export_bonusMKU_CetakActionPerformed

    private void button_exportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_exportActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Tabel_data_perhitungan_upah.getModel();
        ExportToExcel.writeToExcel(model, jPanel_DataTersimpan);
    }//GEN-LAST:event_button_exportActionPerformed

    private void txt_namaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_namaKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_data_tersimpan();
        }
    }//GEN-LAST:event_txt_namaKeyPressed

    private void txt_idKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_idKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_data_tersimpan();
        }
    }//GEN-LAST:event_txt_idKeyPressed

    private void button_searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_searchActionPerformed
        // TODO add your handling code here:
        refreshTable_data_tersimpan();
    }//GEN-LAST:event_button_searchActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_bagian;
    private javax.swing.JComboBox<String> ComboBox_sub;
    private javax.swing.JComboBox<String> ComboBox_sub_bonusCabut;
    private javax.swing.JComboBox<String> ComboBox_sub_bonusMKU;
    private com.toedter.calendar.JDateChooser Date1;
    private com.toedter.calendar.JDateChooser Date2;
    private com.toedter.calendar.JDateChooser Date_kinerja_borong1;
    private com.toedter.calendar.JDateChooser Date_kinerja_borong2;
    private com.toedter.calendar.JDateChooser Date_tutupan_bonusCabut;
    private com.toedter.calendar.JDateChooser Date_tutupan_bonusMKU;
    private com.toedter.calendar.JDateChooser Date_tutupan_upahKaryawan;
    private javax.swing.JTable Tabel_data_perhitungan_upah;
    private javax.swing.JTable Tabel_data_perhitungan_upah_total;
    private javax.swing.JTable Tabel_data_rekap_transfer_sub;
    private javax.swing.JTable Tabel_data_tersimpan;
    private javax.swing.JButton button_export;
    private javax.swing.JButton button_export2;
    private javax.swing.JButton button_export5;
    private javax.swing.JButton button_export6;
    private javax.swing.JButton button_export_bonusMKU_Cabut;
    private javax.swing.JButton button_export_bonusMKU_Cetak;
    private javax.swing.JButton button_export_bonusMKU_sub;
    private javax.swing.JButton button_export_bonus_LP_BP;
    private javax.swing.JButton button_export_bonus_LP_SP;
    private javax.swing.JButton button_export_bonus_LP_Sub;
    private javax.swing.JButton button_export_bonus_LP_WL;
    private javax.swing.JButton button_export_pencabut;
    private javax.swing.JButton button_export_penyesek;
    private javax.swing.JButton button_export_rekap_pencabut;
    private javax.swing.JButton button_export_rekap_penyesek;
    private javax.swing.JButton button_export_rekap_sub;
    private javax.swing.JButton button_form_laporan_sub;
    private javax.swing.JButton button_naik_level_gaji;
    private javax.swing.JButton button_refresh_allData;
    private javax.swing.JButton button_refresh_bonusCabut;
    private javax.swing.JButton button_refresh_bonusMKU;
    private javax.swing.JButton button_refresh_perhitungan_upah;
    private javax.swing.JButton button_save_data_fix;
    public static javax.swing.JButton button_search;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel jPanel_Bonus_Cabut;
    private javax.swing.JPanel jPanel_Bonus_MKU;
    private javax.swing.JPanel jPanel_DataTersimpan;
    private javax.swing.JPanel jPanel_Upah_Karyawan_Sub;
    private javax.swing.JPanel jPanel_bonus_LP_BP;
    private javax.swing.JPanel jPanel_bonus_LP_SP;
    private javax.swing.JPanel jPanel_bonus_LP_WL;
    private javax.swing.JPanel jPanel_kinerja_pekerja_Cabut;
    private javax.swing.JPanel jPanel_tab_cabut;
    private javax.swing.JPanel jPanel_tab_sapon;
    private javax.swing.JPanel jPanel_tab_sesekan;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane13;
    private javax.swing.JScrollPane jScrollPane14;
    private javax.swing.JScrollPane jScrollPane15;
    private javax.swing.JScrollPane jScrollPane16;
    private javax.swing.JScrollPane jScrollPane17;
    private javax.swing.JScrollPane jScrollPane18;
    private javax.swing.JScrollPane jScrollPane19;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane20;
    private javax.swing.JScrollPane jScrollPane21;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTabbedPane jTabbedPane3;
    private javax.swing.JTabbedPane jTabbedPane4;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTextArea jTextArea3;
    private javax.swing.JTextArea jTextArea4;
    private javax.swing.JLabel label_bonusCabut_total_LP;
    private javax.swing.JLabel label_bonusCabut_total_LP_BP;
    private javax.swing.JLabel label_bonusCabut_total_LP_SP;
    private javax.swing.JLabel label_bonusCabut_total_bobotLP;
    private javax.swing.JLabel label_bonusCabut_total_bobotLP_BP;
    private javax.swing.JLabel label_bonusCabut_total_bobotLP_SP;
    private javax.swing.JLabel label_bonusCabut_total_bonusLP;
    private javax.swing.JLabel label_bonusCabut_total_bonus_LP_BP;
    private javax.swing.JLabel label_bonusCabut_total_bonus_LP_SP;
    private javax.swing.JLabel label_bonusCabut_total_gramLP;
    private javax.swing.JLabel label_bonusCabut_total_gramLP_BP;
    private javax.swing.JLabel label_bonusCabut_total_gramLP_SP;
    private javax.swing.JLabel label_bonusCabut_total_nilaiLP;
    private javax.swing.JLabel label_bonusMKU_Cabut_total_Data;
    private javax.swing.JLabel label_bonusMKU_Cabut_total_LP;
    private javax.swing.JLabel label_bonusMKU_Cabut_total_bonusKaryawan;
    private javax.swing.JLabel label_bonusMKU_Cabut_total_bonusLP;
    private javax.swing.JLabel label_bonusMKU_Cetak_total_LP;
    private javax.swing.JLabel label_bonusMKU_Cetak_total_bonusKaryawan;
    private javax.swing.JLabel label_bonusMKU_Cetak_total_bonusLP;
    private javax.swing.JLabel label_total_bonusCabut_Sub;
    private javax.swing.JLabel label_total_bonusMKU_Sub;
    private javax.swing.JLabel label_total_data_tersimpan;
    private javax.swing.JLabel label_total_gram_cabut;
    private javax.swing.JLabel label_total_gram_sapon;
    private javax.swing.JLabel label_total_gram_sesekan;
    private javax.swing.JLabel label_total_upah_cabut;
    private javax.swing.JLabel label_total_upah_rekap_cabut;
    private javax.swing.JLabel label_total_upah_rekap_sapon;
    private javax.swing.JLabel label_total_upah_rekap_sesekan;
    private javax.swing.JLabel label_total_upah_sapon;
    private javax.swing.JLabel label_total_upah_sesekan;
    private javax.swing.JTable table_bonusMKU_Cabut;
    private javax.swing.JTable table_bonusMKU_Cetak;
    private javax.swing.JTable table_bonusMKU_Sub;
    private javax.swing.JTable table_data_Bonus_LP_BP;
    private javax.swing.JTable table_data_Bonus_LP_SP;
    private javax.swing.JTable table_data_Bonus_LP_WL;
    private javax.swing.JTable table_data_RekapBonus_LP_Sub;
    private javax.swing.JTable table_data_pekerja_sapon;
    private javax.swing.JTable table_data_pekerja_sesekan;
    private javax.swing.JTable table_data_pencabut;
    private javax.swing.JTable table_data_rekap_cabut;
    private javax.swing.JTable table_data_rekap_sapon;
    private javax.swing.JTable table_data_rekap_sesekan;
    private javax.swing.JTextField txt_id;
    private javax.swing.JTextField txt_jumlah_hari_kerja;
    private javax.swing.JTextField txt_jumlah_hari_kerja_bonus;
    private javax.swing.JTextField txt_min_lp_dikerjakan;
    private javax.swing.JTextField txt_nama;
    private javax.swing.JTextField txt_search_nama;
    private javax.swing.JTextField txt_search_nama2;
    private javax.swing.JTextField txt_search_no_lp;
    private javax.swing.JTextField txt_search_no_lp_bonusCabut;
    private javax.swing.JTextField txt_search_no_lp_bonusMKU;
    private javax.swing.JTextField txt_search_sub;
    private javax.swing.JTextField txt_target_cabut;
    // End of variables declaration//GEN-END:variables

}
