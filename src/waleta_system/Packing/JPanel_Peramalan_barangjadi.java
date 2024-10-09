package waleta_system.Packing;

import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;
import waleta_system.Class.ExportToExcel;

public class JPanel_Peramalan_barangjadi extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    float total_gram_lp = 0;

    public JPanel_Peramalan_barangjadi() {
        initComponents();
    }

    public void init() {
        try {
            refreshTableLP();
            try {
                ComboBox_Ruangan.removeAllItems();
                ComboBox_Ruangan.addItem("All");
                sql = "SELECT DISTINCT(`ruangan`) AS 'ruangan' FROM `tb_laporan_produksi` WHERE 1";
                rs = Utility.db.getStatement().executeQuery(sql);
                while (rs.next()) {
                    ComboBox_Ruangan.addItem(rs.getString("ruangan"));
                }

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
                Logger.getLogger(JPanel_Peramalan_barangjadi.class.getName()).log(Level.SEVERE, null, e);
            }

            tabel_LaporanProduksi.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent event) {
                    if (!event.getValueIsAdjusting() && tabel_LaporanProduksi.getSelectedRow() != -1) {
                        int i = tabel_LaporanProduksi.getSelectedRow();
//                        refreshTableGrading();
                    }
                }
            });
        } catch (Exception ex) {
            Logger.getLogger(JPanel_Peramalan_barangjadi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTableLP() {
        try {
            decimalFormat.setMaximumFractionDigits(2);
            int tot_kpg = 0, tot_gram_lp = 0;
            DefaultTableModel model = (DefaultTableModel) tabel_LaporanProduksi.getModel();
            model.setRowCount(0);

            String tgl = "";
            if (Date1_search_tgl.getDate() != null && Date2_search_tgl.getDate() != null) {
                if ("Tanggal LP".equals(ComboBox_SearchTgl.getSelectedItem().toString())) {
                    tgl = "AND `tb_laporan_produksi`.`tanggal_lp` BETWEEN '" + dateFormat.format(Date1_search_tgl.getDate()) + "' AND '" + dateFormat.format(Date2_search_tgl.getDate()) + "'";
                } else if ("Tanggal Grading".equals(ComboBox_SearchTgl.getSelectedItem().toString())) {
                    tgl = "AND `tanggal_grading` BETWEEN '" + dateFormat.format(Date1_search_tgl.getDate()) + "' AND '" + dateFormat.format(Date2_search_tgl.getDate()) + "'";
                }
            }

            String ruangan = " AND `tb_laporan_produksi`.`ruangan` = '" + ComboBox_Ruangan.getSelectedItem().toString() + "'";
            if (ComboBox_Ruangan.getSelectedIndex() == 0) {
                ruangan = "";
            }

            sql = "SELECT `no_laporan_produksi`, `no_kartu_waleta`, `tb_laporan_produksi`.`kode_grade`, `memo_lp`, `ruangan`, `tanggal_lp`, `tanggal_grading`, `tb_laporan_produksi`.`jumlah_keping`, `berat_basah`"
                    + "FROM `tb_laporan_produksi` "
                    + "LEFT JOIN `tb_bahan_jadi_masuk` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_bahan_jadi_masuk`.`kode_asal` "
                    + "LEFT JOIN `tb_grading_bahan_jadi` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_grading_bahan_jadi`.`kode_asal_bahan_jadi` "
                    + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_grade_bahan_jadi`.`kode` = `tb_grading_bahan_jadi`.`grade_bahan_jadi`"
                    + "WHERE `kode_tutupan` <> 'SALDO_AWAL2018' "
                    + "AND `tanggal_grading` IS NOT NULL "
                    + "AND `no_laporan_produksi` LIKE '%" + txt_search_no_lp.getText() + "%' "
                    + "AND `tb_laporan_produksi`.`no_kartu_waleta` LIKE '%" + txt_search_no_kartu.getText() + "%' "
                    + "AND `tb_laporan_produksi`.`memo_lp` LIKE '%" + txt_search_memo.getText() + "%' "
                    + ruangan
                    + tgl
                    + "GROUP BY `tb_laporan_produksi`.`no_laporan_produksi`";
//            PreparedStatement pst = Utility.db.getConnection().prepareStatement(sql);
            rs = Utility.db.getConnection().prepareStatement(sql).executeQuery();
            Object[] row = new Object[15];
            while (rs.next()) {
                row[0] = rs.getString("no_kartu_waleta");
                row[1] = rs.getString("no_laporan_produksi");
                row[2] = rs.getDate("tanggal_lp");
                row[3] = rs.getString("memo_lp");
                row[4] = rs.getString("kode_grade");
                row[5] = rs.getString("ruangan");
                row[6] = rs.getDate("tanggal_grading");
                row[7] = rs.getInt("jumlah_keping");
                row[8] = rs.getInt("berat_basah");
                model.addRow(row);
                tot_kpg = tot_kpg + rs.getInt("jumlah_keping");
                tot_gram_lp = tot_gram_lp + rs.getInt("berat_basah");
            }

            decimalFormat.setGroupingUsed(true);
            ColumnsAutoSizer.sizeColumnsToFit(tabel_LaporanProduksi);
            label_total_LP.setText(Integer.toString(tabel_LaporanProduksi.getRowCount()));
            label_total_kpg_LP.setText(decimalFormat.format(tot_kpg));
            label_total_gram_LP.setText(decimalFormat.format(tot_gram_lp));
            total_gram_lp = tot_gram_lp;
            refreshTable_gradeBaku();
            refreshTable_gradeJadi();
            refreshTable_baku_jadi();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_Peramalan_barangjadi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_gradeBaku() {
        try {
            decimalFormat.setMaximumFractionDigits(2);
            int tot_kpg = 0, tot_gram = 0;
            DefaultTableModel model = (DefaultTableModel) tabel_rekap_gradeBaku.getModel();
            model.setRowCount(0);

            String tgl = "";
            if (Date1_search_tgl.getDate() != null && Date2_search_tgl.getDate() != null) {
                if ("Tanggal LP".equals(ComboBox_SearchTgl.getSelectedItem().toString())) {
                    tgl = "AND `tb_laporan_produksi`.`tanggal_lp` BETWEEN '" + dateFormat.format(Date1_search_tgl.getDate()) + "' AND '" + dateFormat.format(Date2_search_tgl.getDate()) + "'";
                } else if ("Tanggal Grading".equals(ComboBox_SearchTgl.getSelectedItem().toString())) {
                    tgl = "AND `tanggal_grading` BETWEEN '" + dateFormat.format(Date1_search_tgl.getDate()) + "' AND '" + dateFormat.format(Date2_search_tgl.getDate()) + "'";
                }
            }

            String ruangan = " AND `tb_laporan_produksi`.`ruangan` = '" + ComboBox_Ruangan.getSelectedItem().toString() + "'";
            if (ComboBox_Ruangan.getSelectedIndex() == 0) {
                ruangan = "";
            }

            sql = "SELECT `tb_laporan_produksi`.`kode_grade`, SUM(`tb_laporan_produksi`.`jumlah_keping`) AS 'keping', SUM(`berat_basah`) AS 'gram'"
                    + "FROM `tb_laporan_produksi` "
                    + "LEFT JOIN `tb_bahan_jadi_masuk` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_bahan_jadi_masuk`.`kode_asal` "
                    + "WHERE `kode_tutupan` <> 'SALDO_AWAL2018' "
                    + "AND `tanggal_grading` IS NOT NULL "
                    + "AND `no_laporan_produksi` LIKE '%" + txt_search_no_lp.getText() + "%' "
                    + "AND `tb_laporan_produksi`.`no_kartu_waleta` LIKE '%" + txt_search_no_kartu.getText() + "%' "
                    + "AND `tb_laporan_produksi`.`memo_lp` LIKE '%" + txt_search_memo.getText() + "%' "
                    + ruangan
                    + tgl
                    + "GROUP BY `tb_laporan_produksi`.`kode_grade`";
//            PreparedStatement pst = Utility.db.getConnection().prepareStatement(sql);
            rs = Utility.db.getConnection().prepareStatement(sql).executeQuery();
            Object[] row = new Object[5];
            while (rs.next()) {
                row[0] = rs.getString("kode_grade");
                row[1] = rs.getInt("keping");
                row[2] = rs.getInt("gram");
                row[3] = Math.round(rs.getFloat("gram") / total_gram_lp * 10000f) / 100f;
                model.addRow(row);
                tot_kpg = tot_kpg + rs.getInt("keping");
                tot_gram = tot_gram + rs.getInt("gram");
            }

            decimalFormat.setGroupingUsed(true);
            ColumnsAutoSizer.sizeColumnsToFit(tabel_rekap_gradeBaku);
            label_total_gradebaku.setText(Integer.toString(tabel_rekap_gradeBaku.getRowCount()));
            label_total_kpg_gradeBaku.setText(decimalFormat.format(tot_kpg));
            label_total_gram_gradeBaku.setText(decimalFormat.format(tot_gram));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_Peramalan_barangjadi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_gradeJadi() {
        try {
            decimalFormat.setMaximumFractionDigits(2);
            int tot_kpg = 0;
            float tot_gram = 0;
            DefaultTableModel model = (DefaultTableModel) tabel_rekap_gradeJadi.getModel();
            model.setRowCount(0);

            String tgl = "";
            if (Date1_search_tgl.getDate() != null && Date2_search_tgl.getDate() != null) {
                if ("Tanggal LP".equals(ComboBox_SearchTgl.getSelectedItem().toString())) {
                    tgl = "AND `tb_laporan_produksi`.`tanggal_lp` BETWEEN '" + dateFormat.format(Date1_search_tgl.getDate()) + "' AND '" + dateFormat.format(Date2_search_tgl.getDate()) + "' ";
                } else if ("Tanggal Grading".equals(ComboBox_SearchTgl.getSelectedItem().toString())) {
                    tgl = "AND `tanggal_grading` BETWEEN '" + dateFormat.format(Date1_search_tgl.getDate()) + "' AND '" + dateFormat.format(Date2_search_tgl.getDate()) + "' ";
                }
            }

            String ruangan = " AND `tb_laporan_produksi`.`ruangan` = '" + ComboBox_Ruangan.getSelectedItem().toString() + "' ";
            if (ComboBox_Ruangan.getSelectedIndex() == 0) {
                ruangan = "";
            }

            sql = "SELECT `tb_grade_bahan_jadi`.`kode_grade`, SUM(`tb_grading_bahan_jadi`.`keping`) AS 'keping', SUM(`tb_grading_bahan_jadi`.`gram`) AS 'gram'"
                    + "FROM `tb_laporan_produksi` "
                    + "LEFT JOIN `tb_bahan_jadi_masuk` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_bahan_jadi_masuk`.`kode_asal` "
                    + "LEFT JOIN `tb_grading_bahan_jadi` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_grading_bahan_jadi`.`kode_asal_bahan_jadi` "
                    + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_grade_bahan_jadi`.`kode` = `tb_grading_bahan_jadi`.`grade_bahan_jadi`"
                    + "WHERE `kode_tutupan` <> 'SALDO_AWAL2018' "
                    + "AND `tanggal_grading` IS NOT NULL AND `tb_grade_bahan_jadi`.`kode_grade` IS NOT NULL "
                    + "AND `no_laporan_produksi` LIKE '%" + txt_search_no_lp.getText() + "%' "
                    + "AND `tb_laporan_produksi`.`no_kartu_waleta` LIKE '%" + txt_search_no_kartu.getText() + "%' "
                    + "AND `tb_laporan_produksi`.`memo_lp` LIKE '%" + txt_search_memo.getText() + "%' "
                    + ruangan
                    + tgl
                    + "GROUP BY `tb_grading_bahan_jadi`.`grade_bahan_jadi`";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[5];
            while (rs.next()) {
                row[0] = rs.getString("kode_grade");
                row[1] = rs.getInt("keping");
                row[2] = rs.getFloat("gram");
                row[3] = Math.round(rs.getFloat("gram") / total_gram_lp * 10000f) / 100f;
                model.addRow(row);
                tot_kpg = tot_kpg + rs.getInt("keping");
                tot_gram = tot_gram + rs.getFloat("gram");
            }

            decimalFormat.setGroupingUsed(true);
            ColumnsAutoSizer.sizeColumnsToFit(tabel_rekap_gradeJadi);
            label_total_gradeJadi.setText(Integer.toString(tabel_rekap_gradeJadi.getRowCount()));
            label_total_kpg_gradeJadi.setText(decimalFormat.format(tot_kpg));
            label_total_gram_gradeJadi.setText(decimalFormat.format(tot_gram));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_Peramalan_barangjadi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_baku_jadi() {
        try {
            decimalFormat.setMaximumFractionDigits(2);
            float tot_gram = 0;
            DefaultTableModel model = (DefaultTableModel) tabel_rekap_bakujadi.getModel();
            model.setRowCount(0);

            String tgl = "";
            if (Date1_search_tgl.getDate() != null && Date2_search_tgl.getDate() != null) {
                if ("Tanggal LP".equals(ComboBox_SearchTgl.getSelectedItem().toString())) {
                    tgl = "AND `tb_laporan_produksi`.`tanggal_lp` BETWEEN '" + dateFormat.format(Date1_search_tgl.getDate()) + "' AND '" + dateFormat.format(Date2_search_tgl.getDate()) + "' \n";
                } else if ("Tanggal Grading".equals(ComboBox_SearchTgl.getSelectedItem().toString())) {
                    tgl = "AND `tanggal_grading` BETWEEN '" + dateFormat.format(Date1_search_tgl.getDate()) + "' AND '" + dateFormat.format(Date2_search_tgl.getDate()) + "' \n";
                }
            }

            String ruangan = " AND `tb_laporan_produksi`.`ruangan` = '" + ComboBox_Ruangan.getSelectedItem().toString() + "' \n";
            if (ComboBox_Ruangan.getSelectedIndex() == 0) {
                ruangan = "";
            }

            sql = "SELECT `tb_laporan_produksi`.`kode_grade` AS 'grade_baku', `tb_grade_bahan_jadi`.`kode_grade` AS 'grade_jadi', SUM(`tb_grading_bahan_jadi`.`gram`) AS 'gram', gram_baku.`gram_baku`"
                    + "FROM `tb_laporan_produksi` \n"
                    + "LEFT JOIN `tb_bahan_jadi_masuk` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_bahan_jadi_masuk`.`kode_asal` \n"
                    + "LEFT JOIN `tb_grading_bahan_jadi` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_grading_bahan_jadi`.`kode_asal_bahan_jadi` \n"
                    + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_grade_bahan_jadi`.`kode` = `tb_grading_bahan_jadi`.`grade_bahan_jadi` \n"
                    + "LEFT JOIN \n"
                    + "(SELECT `kode_grade`, SUM(`berat_basah`) AS 'gram_baku'\n"
                    + "FROM `tb_laporan_produksi` \n"
                    + "LEFT JOIN `tb_bahan_jadi_masuk` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_bahan_jadi_masuk`.`kode_asal`\n"
                    + "WHERE `kode_tutupan` <> 'SALDO_AWAL2018' \n"
                    + "AND `tanggal_grading` IS NOT NULL \n"
                    + "AND `no_laporan_produksi` LIKE '%" + txt_search_no_lp.getText() + "%' \n"
                    + "AND `tb_laporan_produksi`.`no_kartu_waleta` LIKE '%" + txt_search_no_kartu.getText() + "%' \n"
                    + "AND `tb_laporan_produksi`.`memo_lp` LIKE '%" + txt_search_memo.getText() + "%' \n"
                    + ruangan
                    + tgl
                    + "GROUP BY `tb_laporan_produksi`.`kode_grade`) gram_baku\n"
                    + "ON `tb_laporan_produksi`.`kode_grade` = gram_baku.`kode_grade`"
                    + "WHERE `kode_tutupan` <> 'SALDO_AWAL2018' \n"
                    + "AND `tanggal_grading` IS NOT NULL \n"
                    + "AND `no_laporan_produksi` LIKE '%" + txt_search_no_lp.getText() + "%' \n"
                    + "AND `tb_laporan_produksi`.`no_kartu_waleta` LIKE '%" + txt_search_no_kartu.getText() + "%' \n"
                    + "AND `tb_laporan_produksi`.`memo_lp` LIKE '%" + txt_search_memo.getText() + "%' \n"
                    + ruangan
                    + tgl
                    + "GROUP BY `tb_laporan_produksi`.`kode_grade`, `tb_grading_bahan_jadi`.`grade_bahan_jadi` \n"
                    + "ORDER BY `tb_laporan_produksi`.`kode_grade`, `tb_grade_bahan_jadi`.`kode_grade`";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[5];
            while (rs.next()) {
                row[0] = rs.getString("grade_baku");
                row[1] = rs.getString("grade_jadi");
                row[2] = rs.getFloat("gram_baku");
                row[3] = rs.getFloat("gram");
                row[4] = Math.round(rs.getFloat("gram") / rs.getFloat("gram_baku") * 10000f) / 100f;
                model.addRow(row);
                tot_gram = tot_gram + rs.getFloat("gram");
            }

            decimalFormat.setGroupingUsed(true);
            ColumnsAutoSizer.sizeColumnsToFit(tabel_rekap_bakujadi);
            label_total_bakujadi.setText(Integer.toString(tabel_rekap_bakujadi.getRowCount()));
            label_total_gram_bakujadi.setText(decimalFormat.format(tot_gram));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_Peramalan_barangjadi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTableLP_Ramalan() {
        if (tabel_rekap_bakujadi.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "ANDA BELUM MEMILIH DATA UNTUK PERHITUNGAN RAMALAN");
        } else {
            LoadData_ramalan();
        }
    }

    public void LoadData_ramalan() {
        try {
            decimalFormat.setMaximumFractionDigits(2);
            int tot_kpg = 0, tot_gram_lp = 0;
            float tot_kpg_ramalan_hasil_grading = 0, tot_gram_ramalan_hasil_grading = 0;
            float tot_kpg_rekap_hasil_grading = 0, tot_gram_rekap_hasil_grading = 0;
            DefaultTableModel model = (DefaultTableModel) tabel_LaporanProduksi_ramalan.getModel();
            model.setRowCount(0);
            DefaultTableModel model_tabel_hasil_grading_ramalan = (DefaultTableModel) tabel_hasil_grading_ramalan.getModel();
            model_tabel_hasil_grading_ramalan.setRowCount(0);
            DefaultTableModel model_tabel_rekap_ramalan = (DefaultTableModel) tabel_rekap_ramalan.getModel();
            model_tabel_rekap_ramalan.setRowCount(0);

            String tgl = "";
            if (Date1_search_tgl_ramalan.getDate() != null && Date2_search_tgl_ramalan.getDate() != null) {
                tgl = "AND `tb_laporan_produksi`.`tanggal_lp` BETWEEN '" + dateFormat.format(Date1_search_tgl_ramalan.getDate()) + "' AND '" + dateFormat.format(Date2_search_tgl_ramalan.getDate()) + "'";
            }

            String ruangan = " AND `tb_laporan_produksi`.`ruangan` = '" + ComboBox_Ruangan_ramalan.getSelectedItem().toString() + "'";
            if (ComboBox_Ruangan_ramalan.getSelectedIndex() == 0) {
                ruangan = "";
            }

            sql = "SELECT `no_laporan_produksi`, `no_kartu_waleta`, `tb_laporan_produksi`.`kode_grade`, `memo_lp`, `ruangan`, `tanggal_lp`, `tanggal_grading`, `tb_laporan_produksi`.`jumlah_keping`, `berat_basah`"
                    + "FROM `tb_laporan_produksi` "
                    + "LEFT JOIN `tb_bahan_jadi_masuk` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_bahan_jadi_masuk`.`kode_asal` "
                    + "LEFT JOIN `tb_grading_bahan_jadi` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_grading_bahan_jadi`.`kode_asal_bahan_jadi` "
                    + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_grade_bahan_jadi`.`kode` = `tb_grading_bahan_jadi`.`grade_bahan_jadi`"
                    + "WHERE `tanggal_grading` IS NULL "
                    + "AND `no_laporan_produksi` LIKE '%" + txt_search_no_lp_ramalan.getText() + "%' "
                    + "AND `tb_laporan_produksi`.`no_kartu_waleta` LIKE '%" + txt_search_no_kartu_ramalan.getText() + "%' "
                    + "AND `tb_laporan_produksi`.`memo_lp` LIKE '%" + txt_search_memo_ramalan.getText() + "%' "
                    + ruangan
                    + tgl
                    + "GROUP BY `tb_laporan_produksi`.`no_laporan_produksi`";
//            PreparedStatement pst = Utility.db.getConnection().prepareStatement(sql);
            rs = Utility.db.getConnection().prepareStatement(sql).executeQuery();
            HashMap<String, Float[]> RekapGradeBJ = new HashMap<String, Float[]>();
            Object[] row = new Object[15];
            while (rs.next()) {
                row[0] = rs.getString("no_kartu_waleta");
                row[1] = rs.getString("no_laporan_produksi");
                row[2] = rs.getDate("tanggal_lp");
                row[3] = rs.getString("memo_lp");
                row[4] = rs.getString("kode_grade");
                row[5] = rs.getString("ruangan");
                row[6] = rs.getInt("jumlah_keping");
                row[7] = rs.getInt("berat_basah");
                model.addRow(row);
                tot_kpg = tot_kpg + rs.getInt("jumlah_keping");
                tot_gram_lp = tot_gram_lp + rs.getInt("berat_basah");
                String grade_baku = rs.getString("kode_grade");

                Object[] ramalan_hasil_grading = new Object[10];
                ramalan_hasil_grading[0] = rs.getString("no_laporan_produksi");
                ramalan_hasil_grading[1] = rs.getString("kode_grade");
                ramalan_hasil_grading[2] = null;
                ramalan_hasil_grading[3] = null;
                ramalan_hasil_grading[4] = null;
                ramalan_hasil_grading[5] = null;
                for (int i = 0; i < tabel_rekap_bakujadi.getRowCount(); i++) {
                    if (grade_baku.equals(tabel_rekap_bakujadi.getValueAt(i, 0).toString())) {
                        String grade_BJ = tabel_rekap_bakujadi.getValueAt(i, 1).toString();
                        float persentase = (float) tabel_rekap_bakujadi.getValueAt(i, 4);
                        float kpg_ramalan = Math.round(persentase * rs.getFloat("jumlah_keping") / 100f);
                        float gram_ramalan = Math.round(persentase * rs.getFloat("berat_basah") / 100f);
                        ramalan_hasil_grading[0] = rs.getString("no_laporan_produksi");
                        ramalan_hasil_grading[1] = rs.getString("kode_grade");
                        ramalan_hasil_grading[2] = grade_BJ;
                        ramalan_hasil_grading[3] = persentase;
                        ramalan_hasil_grading[4] = kpg_ramalan;
                        ramalan_hasil_grading[5] = gram_ramalan;
                        tot_kpg_ramalan_hasil_grading = tot_kpg_ramalan_hasil_grading + kpg_ramalan;
                        tot_gram_ramalan_hasil_grading = tot_gram_ramalan_hasil_grading + gram_ramalan;
                        model_tabel_hasil_grading_ramalan.addRow(ramalan_hasil_grading);

                        if (RekapGradeBJ.containsKey(grade_BJ)) {
                            Float[] value = RekapGradeBJ.get(grade_BJ);
                            RekapGradeBJ.put(grade_BJ, new Float[]{kpg_ramalan + value[0], gram_ramalan + value[1]});
                        } else {
                            RekapGradeBJ.put(grade_BJ, new Float[]{kpg_ramalan, gram_ramalan});
                        }
                    }
                }
                if (ramalan_hasil_grading[5] == null) {
                    model_tabel_hasil_grading_ramalan.addRow(ramalan_hasil_grading);
                }
            }
            for (String kodeGradeBJ : RekapGradeBJ.keySet()) {
                Object[] rekap_ramalan_BJ = new Object[10];
                float rekap_ramalan_kpg_BJ = RekapGradeBJ.get(kodeGradeBJ)[0];
                float rekap_ramalan_gram_BJ = RekapGradeBJ.get(kodeGradeBJ)[1];
                rekap_ramalan_BJ[0] = kodeGradeBJ;
                rekap_ramalan_BJ[1] = Math.round(rekap_ramalan_kpg_BJ);
                rekap_ramalan_BJ[2] = Math.round(rekap_ramalan_gram_BJ);
                rekap_ramalan_BJ[3] = Math.round(rekap_ramalan_gram_BJ / tot_gram_lp * 10000f) / 100f;
                model_tabel_rekap_ramalan.addRow(rekap_ramalan_BJ);
                tot_kpg_rekap_hasil_grading = tot_kpg_rekap_hasil_grading + rekap_ramalan_kpg_BJ;
                tot_gram_rekap_hasil_grading = tot_gram_rekap_hasil_grading + rekap_ramalan_gram_BJ;
            }

            decimalFormat.setGroupingUsed(true);
            ColumnsAutoSizer.sizeColumnsToFit(tabel_LaporanProduksi_ramalan);
            label_total_LP_ramalan.setText(Integer.toString(tabel_LaporanProduksi_ramalan.getRowCount()));
            label_total_kpg_LP_ramalan.setText(decimalFormat.format(tot_kpg));
            label_total_gram_LP_ramalan.setText(decimalFormat.format(tot_gram_lp));

            ColumnsAutoSizer.sizeColumnsToFit(tabel_hasil_grading_ramalan);
            label_total_hasil_grading_ramalan.setText(Integer.toString(tabel_hasil_grading_ramalan.getRowCount()));
            label_total_kpg_hasil_grading_ramalan.setText(decimalFormat.format(tot_kpg_ramalan_hasil_grading));
            label_total_gram_hasil_grading_ramalan.setText(decimalFormat.format(tot_gram_ramalan_hasil_grading));

            ColumnsAutoSizer.sizeColumnsToFit(tabel_rekap_ramalan);
            label_total_grade_rekap_ramalan.setText(Integer.toString(tabel_rekap_ramalan.getRowCount()));
            label_total_kpg_rekap_ramalan.setText(decimalFormat.format(tot_kpg_rekap_hasil_grading));
            label_total_gram_rekap_ramalan.setText(decimalFormat.format(tot_gram_rekap_hasil_grading));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_Peramalan_barangjadi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        txt_search_memo = new javax.swing.JTextField();
        ComboBox_SearchTgl = new javax.swing.JComboBox<>();
        Date1_search_tgl = new com.toedter.calendar.JDateChooser();
        jLabel3 = new javax.swing.JLabel();
        ComboBox_Ruangan = new javax.swing.JComboBox<>();
        button_refresh = new javax.swing.JButton();
        Date2_search_tgl = new com.toedter.calendar.JDateChooser();
        txt_search_no_lp = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel65 = new javax.swing.JLabel();
        txt_search_no_kartu = new javax.swing.JTextField();
        jLabel66 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        label_total_gradebaku = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tabel_rekap_gradeBaku = new javax.swing.JTable();
        jLabel18 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        label_total_kpg_gradeBaku = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel58 = new javax.swing.JLabel();
        label_total_gram_gradeBaku = new javax.swing.JLabel();
        jLabel59 = new javax.swing.JLabel();
        button_export_gradeBaku = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabel_rekap_gradeJadi = new javax.swing.JTable();
        jLabel56 = new javax.swing.JLabel();
        button_export_gradeJadi = new javax.swing.JButton();
        jLabel57 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        label_total_gram_gradeJadi = new javax.swing.JLabel();
        label_total_gradeJadi = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        label_total_kpg_gradeJadi = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        label_total_gram_LP = new javax.swing.JLabel();
        label_total_LP = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabel_LaporanProduksi = new javax.swing.JTable();
        jLabel20 = new javax.swing.JLabel();
        label_total_kpg_LP = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        button_export_LP = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tabel_rekap_bakujadi = new javax.swing.JTable();
        jLabel60 = new javax.swing.JLabel();
        button_export_bakujadi = new javax.swing.JButton();
        jLabel61 = new javax.swing.JLabel();
        label_total_gram_bakujadi = new javax.swing.JLabel();
        label_total_bakujadi = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel67 = new javax.swing.JLabel();
        txt_search_no_kartu_ramalan = new javax.swing.JTextField();
        jLabel68 = new javax.swing.JLabel();
        txt_search_memo_ramalan = new javax.swing.JTextField();
        Date1_search_tgl_ramalan = new com.toedter.calendar.JDateChooser();
        jLabel4 = new javax.swing.JLabel();
        ComboBox_Ruangan_ramalan = new javax.swing.JComboBox<>();
        button_refresh_ramalan = new javax.swing.JButton();
        Date2_search_tgl_ramalan = new com.toedter.calendar.JDateChooser();
        jLabel13 = new javax.swing.JLabel();
        txt_search_no_lp_ramalan = new javax.swing.JTextField();
        jPanel7 = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        label_total_gram_LP_ramalan = new javax.swing.JLabel();
        label_total_LP_ramalan = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        tabel_LaporanProduksi_ramalan = new javax.swing.JTable();
        jLabel26 = new javax.swing.JLabel();
        label_total_kpg_LP_ramalan = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        button_export_LP_ramalan = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        tabel_hasil_grading_ramalan = new javax.swing.JTable();
        jLabel62 = new javax.swing.JLabel();
        button_ramalan_hasil_grading = new javax.swing.JButton();
        jLabel63 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        label_total_gram_hasil_grading_ramalan = new javax.swing.JLabel();
        label_total_hasil_grading_ramalan = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        label_total_kpg_hasil_grading_ramalan = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jLabel33 = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        tabel_rekap_ramalan = new javax.swing.JTable();
        jLabel70 = new javax.swing.JLabel();
        button_export_rekap_ramalan = new javax.swing.JButton();
        jLabel71 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        label_total_gram_rekap_ramalan = new javax.swing.JLabel();
        label_total_grade_rekap_ramalan = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        label_total_kpg_rekap_ramalan = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));

        jTabbedPane2.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane2.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        txt_search_memo.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_memo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_memoKeyPressed(evt);
            }
        });

        ComboBox_SearchTgl.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_SearchTgl.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tanggal LP", "Tanggal Grading" }));

        Date1_search_tgl.setBackground(new java.awt.Color(255, 255, 255));
        Date1_search_tgl.setDate(new Date(new Date().getTime()- (7 * 24 * 60 * 60 * 1000)));
        Date1_search_tgl.setDateFormatString("dd MMMM yyyy");
        Date1_search_tgl.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("Ruangan :");

        ComboBox_Ruangan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_Ruangan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "A", "B", "C", "D" }));

        button_refresh.setBackground(new java.awt.Color(255, 255, 255));
        button_refresh.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_refresh.setText("Load Data");
        button_refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refreshActionPerformed(evt);
            }
        });

        Date2_search_tgl.setBackground(new java.awt.Color(255, 255, 255));
        Date2_search_tgl.setDate(new Date());
        Date2_search_tgl.setDateFormatString("dd MMMM yyyy");
        Date2_search_tgl.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        txt_search_no_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_no_lp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_no_lpKeyPressed(evt);
            }
        });

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel9.setText("Memo LP :");

        jLabel65.setBackground(new java.awt.Color(255, 255, 255));
        jLabel65.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel65.setText("No LP :");

        txt_search_no_kartu.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_no_kartu.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_no_kartuKeyPressed(evt);
            }
        });

        jLabel66.setBackground(new java.awt.Color(255, 255, 255));
        jLabel66.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel66.setText("No Kartu :");

        jPanel2.setBackground(new java.awt.Color(204, 204, 255));

        label_total_gradebaku.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gradebaku.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_gradebaku.setText("0");

        tabel_rekap_gradeBaku.setAutoCreateRowSorter(true);
        tabel_rekap_gradeBaku.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tabel_rekap_gradeBaku.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Grade", "Kpg", "Gram", "%"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabel_rekap_gradeBaku.setRowSelectionAllowed(false);
        jScrollPane3.setViewportView(tabel_rekap_gradeBaku);

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel18.setText("Rekap Grade");

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel8.setText("Total Grade :");

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel10.setText("Total Keping :");

        label_total_kpg_gradeBaku.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kpg_gradeBaku.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_kpg_gradeBaku.setText("0");

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel16.setText("Keping");

        jLabel58.setBackground(new java.awt.Color(255, 255, 255));
        jLabel58.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel58.setText("Total Gram :");

        label_total_gram_gradeBaku.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_gradeBaku.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_gram_gradeBaku.setText("0");

        jLabel59.setBackground(new java.awt.Color(255, 255, 255));
        jLabel59.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel59.setText("Gram");

        button_export_gradeBaku.setBackground(new java.awt.Color(255, 255, 255));
        button_export_gradeBaku.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_gradeBaku.setText("Export");
        button_export_gradeBaku.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_gradeBakuActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_gradebaku))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_kpg_gradeBaku)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel16))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel58)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_gram_gradeBaku)
                                .addGap(5, 5, 5)
                                .addComponent(jLabel59)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 122, Short.MAX_VALUE)
                        .addComponent(button_export_gradeBaku)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export_gradeBaku, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_gradebaku, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_kpg_gradeBaku, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel58, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_gram_gradeBaku, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel59, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel3.setBackground(new java.awt.Color(204, 204, 255));

        jLabel19.setBackground(new java.awt.Color(255, 255, 255));
        jLabel19.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel19.setText("Rekap Hasil Grading");

        tabel_rekap_gradeJadi.setAutoCreateRowSorter(true);
        tabel_rekap_gradeJadi.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tabel_rekap_gradeJadi.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Grade BJ", "Kpg", "Gram", "%"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabel_rekap_gradeJadi.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(tabel_rekap_gradeJadi);

        jLabel56.setBackground(new java.awt.Color(255, 255, 255));
        jLabel56.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel56.setText("Total Gram :");

        button_export_gradeJadi.setBackground(new java.awt.Color(255, 255, 255));
        button_export_gradeJadi.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_gradeJadi.setText("Export");
        button_export_gradeJadi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_gradeJadiActionPerformed(evt);
            }
        });

        jLabel57.setBackground(new java.awt.Color(255, 255, 255));
        jLabel57.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel57.setText("Gram");

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel15.setText("Keping");

        label_total_gram_gradeJadi.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_gradeJadi.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_gram_gradeJadi.setText("0");

        label_total_gradeJadi.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gradeJadi.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_gradeJadi.setText("0");

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel6.setText("Total Grade :");

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel7.setText("Total Keping :");

        label_total_kpg_gradeJadi.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kpg_gradeJadi.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_kpg_gradeJadi.setText("0");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 83, Short.MAX_VALUE)
                        .addComponent(button_export_gradeJadi))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_gradeJadi))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_kpg_gradeJadi)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel15))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel56)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_gram_gradeJadi)
                                .addGap(5, 5, 5)
                                .addComponent(jLabel57)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_export_gradeJadi, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_gradeJadi, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_kpg_gradeJadi, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel56, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_gram_gradeJadi, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel57, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel4.setBackground(new java.awt.Color(255, 204, 204));

        jLabel22.setBackground(new java.awt.Color(255, 255, 255));
        jLabel22.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel22.setText("Gram");

        jLabel43.setBackground(new java.awt.Color(255, 255, 255));
        jLabel43.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel43.setText("Total Keping :");

        label_total_gram_LP.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_LP.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_gram_LP.setText("0");

        label_total_LP.setBackground(new java.awt.Color(255, 255, 255));
        label_total_LP.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_LP.setText("0");

        jLabel21.setBackground(new java.awt.Color(255, 255, 255));
        jLabel21.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel21.setText("Total Gram LP :");

        tabel_LaporanProduksi.setAutoCreateRowSorter(true);
        tabel_LaporanProduksi.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tabel_LaporanProduksi.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No Kartu", "No LP", "Tgl LP", "Memo", "Grade", "Ruang", "Tgl Grading", "Kpg", "Berat LP"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabel_LaporanProduksi.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tabel_LaporanProduksi);

        jLabel20.setBackground(new java.awt.Color(255, 255, 255));
        jLabel20.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel20.setText("Data LP");

        label_total_kpg_LP.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kpg_LP.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_kpg_LP.setText("0");

        jLabel44.setBackground(new java.awt.Color(255, 255, 255));
        jLabel44.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel44.setText("Kpg");

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("Total Laporan Produksi :");

        button_export_LP.setBackground(new java.awt.Color(255, 255, 255));
        button_export_LP.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_LP.setText("Export");
        button_export_LP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_LPActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 368, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel21)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_gram_LP)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel22))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_LP))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel43)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_kpg_LP)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel44)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_export_LP, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export_LP, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_LP, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_kpg_LP, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_gram_LP, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel5.setBackground(new java.awt.Color(153, 255, 204));

        jLabel23.setBackground(new java.awt.Color(255, 255, 255));
        jLabel23.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel23.setText("Rekap Baku-Jadi");

        tabel_rekap_bakujadi.setAutoCreateRowSorter(true);
        tabel_rekap_bakujadi.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tabel_rekap_bakujadi.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Grade Baku", "Grade BJ", "Gr Baku", "Gram", "%"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabel_rekap_bakujadi.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(tabel_rekap_bakujadi);

        jLabel60.setBackground(new java.awt.Color(255, 255, 255));
        jLabel60.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel60.setText("Total Gram :");

        button_export_bakujadi.setBackground(new java.awt.Color(255, 255, 255));
        button_export_bakujadi.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_bakujadi.setText("Export");
        button_export_bakujadi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_bakujadiActionPerformed(evt);
            }
        });

        jLabel61.setBackground(new java.awt.Color(255, 255, 255));
        jLabel61.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel61.setText("Gram");

        label_total_gram_bakujadi.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_bakujadi.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_gram_bakujadi.setText("0");

        label_total_bakujadi.setBackground(new java.awt.Color(255, 255, 255));
        label_total_bakujadi.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_bakujadi.setText("0");

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel11.setText("Total Grade :");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_bakujadi))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel60)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_gram_bakujadi)
                                .addGap(5, 5, 5)
                                .addComponent(jLabel61)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel23)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_export_bakujadi))
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 385, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_export_bakujadi, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 529, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_bakujadi, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel60, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_gram_bakujadi, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel61, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(37, 37, 37))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel65)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_search_no_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel66)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_search_no_kartu, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_search_memo, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ComboBox_SearchTgl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Date1_search_tgl, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Date2_search_tgl, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ComboBox_Ruangan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_refresh)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_search_no_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel65, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_no_kartu, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel66, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_memo, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date2_search_tgl, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_SearchTgl, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date1_search_tgl, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_Ruangan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_refresh, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        jTabbedPane2.addTab("Master Data Hitung", jPanel1);

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel12.setText("Memo LP :");

        jLabel67.setBackground(new java.awt.Color(255, 255, 255));
        jLabel67.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel67.setText("No LP :");

        txt_search_no_kartu_ramalan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_no_kartu_ramalan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_no_kartu_ramalanKeyPressed(evt);
            }
        });

        jLabel68.setBackground(new java.awt.Color(255, 255, 255));
        jLabel68.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel68.setText("No Kartu :");

        txt_search_memo_ramalan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_memo_ramalan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_memo_ramalanKeyPressed(evt);
            }
        });

        Date1_search_tgl_ramalan.setBackground(new java.awt.Color(255, 255, 255));
        Date1_search_tgl_ramalan.setDate(new Date(new Date().getTime()- (7 * 24 * 60 * 60 * 1000)));
        Date1_search_tgl_ramalan.setDateFormatString("dd MMMM yyyy");
        Date1_search_tgl_ramalan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel4.setText("Ruangan :");

        ComboBox_Ruangan_ramalan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_Ruangan_ramalan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "A", "B", "C", "D" }));

        button_refresh_ramalan.setBackground(new java.awt.Color(255, 255, 255));
        button_refresh_ramalan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_refresh_ramalan.setText("Refresh");
        button_refresh_ramalan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refresh_ramalanActionPerformed(evt);
            }
        });

        Date2_search_tgl_ramalan.setBackground(new java.awt.Color(255, 255, 255));
        Date2_search_tgl_ramalan.setDate(new Date());
        Date2_search_tgl_ramalan.setDateFormatString("dd MMMM yyyy");
        Date2_search_tgl_ramalan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel13.setText("Tanggal LP :");

        txt_search_no_lp_ramalan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_no_lp_ramalan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_no_lp_ramalanKeyPressed(evt);
            }
        });

        jPanel7.setBackground(new java.awt.Color(255, 204, 204));

        jLabel24.setBackground(new java.awt.Color(255, 255, 255));
        jLabel24.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel24.setText("Gram");

        jLabel45.setBackground(new java.awt.Color(255, 255, 255));
        jLabel45.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel45.setText("Total Keping :");

        label_total_gram_LP_ramalan.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_LP_ramalan.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_gram_LP_ramalan.setText("0");

        label_total_LP_ramalan.setBackground(new java.awt.Color(255, 255, 255));
        label_total_LP_ramalan.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_LP_ramalan.setText("0");

        jLabel25.setBackground(new java.awt.Color(255, 255, 255));
        jLabel25.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel25.setText("Total Gram LP :");

        tabel_LaporanProduksi_ramalan.setAutoCreateRowSorter(true);
        tabel_LaporanProduksi_ramalan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tabel_LaporanProduksi_ramalan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No Kartu", "No LP", "Tgl LP", "Memo", "Grade", "Ruang", "Kpg", "Berat LP"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabel_LaporanProduksi_ramalan.getTableHeader().setReorderingAllowed(false);
        jScrollPane5.setViewportView(tabel_LaporanProduksi_ramalan);

        jLabel26.setBackground(new java.awt.Color(255, 255, 255));
        jLabel26.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel26.setText("DATA LP BELUM GRADING");

        label_total_kpg_LP_ramalan.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kpg_LP_ramalan.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_kpg_LP_ramalan.setText("0");

        jLabel46.setBackground(new java.awt.Color(255, 255, 255));
        jLabel46.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel46.setText("Kpg");

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("Total Laporan Produksi :");

        button_export_LP_ramalan.setBackground(new java.awt.Color(255, 255, 255));
        button_export_LP_ramalan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_LP_ramalan.setText("Export");
        button_export_LP_ramalan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_LP_ramalanActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel25)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_gram_LP_ramalan)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel24))
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_LP_ramalan))
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel45)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_kpg_LP_ramalan)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel46)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel26)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_export_LP_ramalan, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export_LP_ramalan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_LP_ramalan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel45, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_kpg_LP_ramalan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel46, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_gram_LP_ramalan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel8.setBackground(new java.awt.Color(204, 204, 255));

        jLabel27.setBackground(new java.awt.Color(255, 255, 255));
        jLabel27.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel27.setText("RAMALAN HASIL GRADING");

        tabel_hasil_grading_ramalan.setAutoCreateRowSorter(true);
        tabel_hasil_grading_ramalan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tabel_hasil_grading_ramalan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No LP", "Grade Baku", "Grade BJ", "%", "Kpg", "Gram"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
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
        tabel_hasil_grading_ramalan.getTableHeader().setReorderingAllowed(false);
        jScrollPane6.setViewportView(tabel_hasil_grading_ramalan);

        jLabel62.setBackground(new java.awt.Color(255, 255, 255));
        jLabel62.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel62.setText("Total Gram :");

        button_ramalan_hasil_grading.setBackground(new java.awt.Color(255, 255, 255));
        button_ramalan_hasil_grading.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_ramalan_hasil_grading.setText("Export");
        button_ramalan_hasil_grading.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_ramalan_hasil_gradingActionPerformed(evt);
            }
        });

        jLabel63.setBackground(new java.awt.Color(255, 255, 255));
        jLabel63.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel63.setText("Gram");

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel17.setText("Keping");

        label_total_gram_hasil_grading_ramalan.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_hasil_grading_ramalan.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_gram_hasil_grading_ramalan.setText("0");

        label_total_hasil_grading_ramalan.setBackground(new java.awt.Color(255, 255, 255));
        label_total_hasil_grading_ramalan.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_hasil_grading_ramalan.setText("0");

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel14.setText("Total Data :");

        jLabel28.setBackground(new java.awt.Color(255, 255, 255));
        jLabel28.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel28.setText("Total Keping :");

        label_total_kpg_hasil_grading_ramalan.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kpg_hasil_grading_ramalan.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_kpg_hasil_grading_ramalan.setText("0");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel27)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_ramalan_hasil_grading))
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 503, Short.MAX_VALUE)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addComponent(jLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_hasil_grading_ramalan))
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addComponent(jLabel28)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_kpg_hasil_grading_ramalan)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel17))
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addComponent(jLabel62)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_gram_hasil_grading_ramalan)
                                .addGap(5, 5, 5)
                                .addComponent(jLabel63)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_ramalan_hasil_grading, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_hasil_grading_ramalan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_kpg_hasil_grading_ramalan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel62, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_gram_hasil_grading_ramalan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel63, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel10.setBackground(new java.awt.Color(204, 204, 255));

        jLabel33.setBackground(new java.awt.Color(255, 255, 255));
        jLabel33.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel33.setText("Rekap Ramalan / grade Jadi");

        tabel_rekap_ramalan.setAutoCreateRowSorter(true);
        tabel_rekap_ramalan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tabel_rekap_ramalan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Grade BJ", "Kpg", "Gram", "%"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabel_rekap_ramalan.getTableHeader().setReorderingAllowed(false);
        jScrollPane8.setViewportView(tabel_rekap_ramalan);

        jLabel70.setBackground(new java.awt.Color(255, 255, 255));
        jLabel70.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel70.setText("Total Gram :");

        button_export_rekap_ramalan.setBackground(new java.awt.Color(255, 255, 255));
        button_export_rekap_ramalan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_rekap_ramalan.setText("Export");
        button_export_rekap_ramalan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_rekap_ramalanActionPerformed(evt);
            }
        });

        jLabel71.setBackground(new java.awt.Color(255, 255, 255));
        jLabel71.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel71.setText("Gram");

        jLabel34.setBackground(new java.awt.Color(255, 255, 255));
        jLabel34.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel34.setText("Keping");

        label_total_gram_rekap_ramalan.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_rekap_ramalan.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_gram_rekap_ramalan.setText("0");

        label_total_grade_rekap_ramalan.setBackground(new java.awt.Color(255, 255, 255));
        label_total_grade_rekap_ramalan.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_grade_rekap_ramalan.setText("0");

        jLabel35.setBackground(new java.awt.Color(255, 255, 255));
        jLabel35.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel35.setText("Total Grade :");

        jLabel36.setBackground(new java.awt.Color(255, 255, 255));
        jLabel36.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel36.setText("Total Keping :");

        label_total_kpg_rekap_ramalan.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kpg_rekap_ramalan.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_kpg_rekap_ramalan.setText("0");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel33)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 118, Short.MAX_VALUE)
                        .addComponent(button_export_rekap_ramalan))
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addComponent(jLabel35)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_grade_rekap_ramalan))
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addComponent(jLabel36)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_kpg_rekap_ramalan)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel34))
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addComponent(jLabel70)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_gram_rekap_ramalan)
                                .addGap(5, 5, 5)
                                .addComponent(jLabel71)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_export_rekap_ramalan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 529, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_grade_rekap_ramalan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_kpg_rekap_ramalan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel70, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_gram_rekap_ramalan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel71, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel67)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_search_no_lp_ramalan, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel68)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_search_no_kartu_ramalan, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_search_memo_ramalan, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Date1_search_tgl_ramalan, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Date2_search_tgl_ramalan, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ComboBox_Ruangan_ramalan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_refresh_ramalan)
                .addContainerGap(356, Short.MAX_VALUE))
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_search_no_lp_ramalan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel67, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_no_kartu_ramalan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel68, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_memo_ramalan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date2_search_tgl_ramalan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date1_search_tgl_ramalan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_Ruangan_ramalan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_refresh_ramalan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        jTabbedPane2.addTab("Ramalan Hasil Produksi", jPanel6);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane2, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane2, javax.swing.GroupLayout.Alignment.TRAILING)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void button_refreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refreshActionPerformed
        // TODO add your handling code here:
        refreshTableLP();
    }//GEN-LAST:event_button_refreshActionPerformed

    private void txt_search_no_lpKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_no_lpKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTableLP();
        }
    }//GEN-LAST:event_txt_search_no_lpKeyPressed

    private void txt_search_memoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_memoKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTableLP();
        }
    }//GEN-LAST:event_txt_search_memoKeyPressed

    private void button_export_LPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_LPActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) tabel_LaporanProduksi.getModel();
        ExportToExcel.writeToExcel(model, this);
    }//GEN-LAST:event_button_export_LPActionPerformed

    private void button_export_gradeJadiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_gradeJadiActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) tabel_rekap_gradeJadi.getModel();
        ExportToExcel.writeToExcel(model, this);
    }//GEN-LAST:event_button_export_gradeJadiActionPerformed

    private void txt_search_no_kartuKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_no_kartuKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTableLP();
        }
    }//GEN-LAST:event_txt_search_no_kartuKeyPressed

    private void button_export_gradeBakuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_gradeBakuActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) tabel_rekap_gradeBaku.getModel();
        ExportToExcel.writeToExcel(model, this);
    }//GEN-LAST:event_button_export_gradeBakuActionPerformed

    private void button_export_bakujadiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_bakujadiActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) tabel_rekap_bakujadi.getModel();
        ExportToExcel.writeToExcel(model, this);
    }//GEN-LAST:event_button_export_bakujadiActionPerformed

    private void txt_search_no_kartu_ramalanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_no_kartu_ramalanKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTableLP_Ramalan();
        }
    }//GEN-LAST:event_txt_search_no_kartu_ramalanKeyPressed

    private void txt_search_memo_ramalanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_memo_ramalanKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTableLP_Ramalan();
        }
    }//GEN-LAST:event_txt_search_memo_ramalanKeyPressed

    private void button_refresh_ramalanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refresh_ramalanActionPerformed
        // TODO add your handling code here:
        refreshTableLP_Ramalan();
    }//GEN-LAST:event_button_refresh_ramalanActionPerformed

    private void txt_search_no_lp_ramalanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_no_lp_ramalanKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTableLP_Ramalan();
        }
    }//GEN-LAST:event_txt_search_no_lp_ramalanKeyPressed

    private void button_export_LP_ramalanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_LP_ramalanActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) tabel_LaporanProduksi_ramalan.getModel();
        ExportToExcel.writeToExcel(model, this);
    }//GEN-LAST:event_button_export_LP_ramalanActionPerformed

    private void button_ramalan_hasil_gradingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_ramalan_hasil_gradingActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) tabel_hasil_grading_ramalan.getModel();
        ExportToExcel.writeToExcel(model, this);
    }//GEN-LAST:event_button_ramalan_hasil_gradingActionPerformed

    private void button_export_rekap_ramalanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_rekap_ramalanActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) tabel_rekap_ramalan.getModel();
        ExportToExcel.writeToExcel(model, this);
    }//GEN-LAST:event_button_export_rekap_ramalanActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_Ruangan;
    private javax.swing.JComboBox<String> ComboBox_Ruangan_ramalan;
    private javax.swing.JComboBox<String> ComboBox_SearchTgl;
    private com.toedter.calendar.JDateChooser Date1_search_tgl;
    private com.toedter.calendar.JDateChooser Date1_search_tgl_ramalan;
    private com.toedter.calendar.JDateChooser Date2_search_tgl;
    private com.toedter.calendar.JDateChooser Date2_search_tgl_ramalan;
    private javax.swing.JButton button_export_LP;
    private javax.swing.JButton button_export_LP_ramalan;
    private javax.swing.JButton button_export_bakujadi;
    private javax.swing.JButton button_export_gradeBaku;
    private javax.swing.JButton button_export_gradeJadi;
    private javax.swing.JButton button_export_rekap_ramalan;
    private javax.swing.JButton button_ramalan_hasil_grading;
    private javax.swing.JButton button_refresh;
    private javax.swing.JButton button_refresh_ramalan;
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
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JLabel label_total_LP;
    private javax.swing.JLabel label_total_LP_ramalan;
    private javax.swing.JLabel label_total_bakujadi;
    private javax.swing.JLabel label_total_gradeJadi;
    private javax.swing.JLabel label_total_grade_rekap_ramalan;
    private javax.swing.JLabel label_total_gradebaku;
    private javax.swing.JLabel label_total_gram_LP;
    private javax.swing.JLabel label_total_gram_LP_ramalan;
    private javax.swing.JLabel label_total_gram_bakujadi;
    private javax.swing.JLabel label_total_gram_gradeBaku;
    private javax.swing.JLabel label_total_gram_gradeJadi;
    private javax.swing.JLabel label_total_gram_hasil_grading_ramalan;
    private javax.swing.JLabel label_total_gram_rekap_ramalan;
    private javax.swing.JLabel label_total_hasil_grading_ramalan;
    private javax.swing.JLabel label_total_kpg_LP;
    private javax.swing.JLabel label_total_kpg_LP_ramalan;
    private javax.swing.JLabel label_total_kpg_gradeBaku;
    private javax.swing.JLabel label_total_kpg_gradeJadi;
    private javax.swing.JLabel label_total_kpg_hasil_grading_ramalan;
    private javax.swing.JLabel label_total_kpg_rekap_ramalan;
    private javax.swing.JTable tabel_LaporanProduksi;
    private javax.swing.JTable tabel_LaporanProduksi_ramalan;
    private javax.swing.JTable tabel_hasil_grading_ramalan;
    private javax.swing.JTable tabel_rekap_bakujadi;
    private javax.swing.JTable tabel_rekap_gradeBaku;
    private javax.swing.JTable tabel_rekap_gradeJadi;
    private javax.swing.JTable tabel_rekap_ramalan;
    private javax.swing.JTextField txt_search_memo;
    private javax.swing.JTextField txt_search_memo_ramalan;
    private javax.swing.JTextField txt_search_no_kartu;
    private javax.swing.JTextField txt_search_no_kartu_ramalan;
    private javax.swing.JTextField txt_search_no_lp;
    private javax.swing.JTextField txt_search_no_lp_ramalan;
    // End of variables declaration//GEN-END:variables
}
