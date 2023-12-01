package waleta_system.HRD;

import waleta_system.Class.Utility;
import java.awt.Color;
import java.awt.Component;
import java.awt.HeadlessException;
import java.awt.event.KeyEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.ExportToExcel;
import waleta_system.MainForm;

public class JPanel_DataLembur extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    PreparedStatement pst;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Date today = new Date();
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    DefaultTableModel dm = new DefaultTableModel();
    JFileChooser chooser = new JFileChooser();

    public JPanel_DataLembur() {
        initComponents();
        Table_SPL_PEJUANG.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && Table_SPL_PEJUANG.getSelectedRow() != -1) {
                    int row = Table_SPL_PEJUANG.getSelectedRow();
                    String nomor_spl = Table_SPL_PEJUANG.getValueAt(row, 0).toString();
                    refreshTable_detailLembur(nomor_spl, tabel_pegawai_lembur_SPL_PEJUANG);
                    label_total_karyawan_SPL_PEJUANG.setText(Integer.toString(tabel_pegawai_lembur_SPL_PEJUANG.getRowCount()));
                    if (Table_SPL_PEJUANG.getValueAt(row, 8) == null) {
                        button_edit_SPL_PEJUANG.setEnabled(true);
                        button_disetujui_SPL_PEJUANG.setEnabled(true);
                        button_diketahui_SPL_PEJUANG.setEnabled(false);
                    } else {
                        button_edit_SPL_PEJUANG.setEnabled(false);
                        button_disetujui_SPL_PEJUANG.setEnabled(false);
                        if (Table_SPL_PEJUANG.getValueAt(row, 9) == null) {
                            button_diketahui_SPL_PEJUANG.setEnabled(true);
                        } else {
                            button_diketahui_SPL_PEJUANG.setEnabled(false);
                        }
                    }
                }
            }
        });
        Table_SPL_STAFF.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && Table_SPL_STAFF.getSelectedRow() != -1) {
                    int row = Table_SPL_STAFF.getSelectedRow();
                    String nomor_spl = Table_SPL_STAFF.getValueAt(row, 0).toString();
                    refreshTable_detailLembur(nomor_spl, tabel_pegawai_lembur_SPL_STAFF);
                    label_total_karyawan_SPL_STAFF.setText(Integer.toString(tabel_pegawai_lembur_SPL_STAFF.getRowCount()));
                    if (Table_SPL_STAFF.getValueAt(row, 8) == null) {
                        button_edit_SPL_STAFF.setEnabled(true);
                        button_disetujui_SPL_STAFF.setEnabled(true);
                        button_diketahui_SPL_STAFF.setEnabled(false);
                    } else {
                        button_edit_SPL_STAFF.setEnabled(false);
                        button_disetujui_SPL_STAFF.setEnabled(false);
                        if (Table_SPL_STAFF.getValueAt(row, 9) == null) {
                            button_diketahui_SPL_STAFF.setEnabled(true);
                        } else {
                            button_diketahui_SPL_STAFF.setEnabled(false);
                        }
                    }
                }
            }
        });

        tabel_rekap_pegawai.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && tabel_rekap_pegawai.getSelectedRow() != -1) {
                    refreshTable_SPLKaryawan();
                }
            }
        });
    }

    public void init(String otorisasi) {
        try {
            if (otorisasi.equals("UMUM")) {
                // Hide tab data lembur makan dan rekap / karyawan
                jTabbedPane1.setEnabledAt(3, false);
                jTabbedPane1.setEnabledAt(4, false);
                button_diketahui_SPL_PEJUANG.setVisible(false);
                button_diketahui_semua_SPL_PEJUANG.setVisible(false);
                button_diketahui_SPL_STAFF.setVisible(false);
                button_diketahui_semua_SPL_STAFF.setVisible(false);
            } else {
                jTabbedPane1.setEnabledAt(3, true);
                jTabbedPane1.setEnabledAt(4, true);
                button_diketahui_SPL_PEJUANG.setVisible(true);
                button_diketahui_semua_SPL_PEJUANG.setVisible(true);
                button_diketahui_SPL_STAFF.setVisible(true);
                button_diketahui_semua_SPL_STAFF.setVisible(true);
            }
            if (MainForm.Login_Posisi.equals("PEJUANG")) {
                // Hide tab SPL STAFF
                jTabbedPane1.setEnabledAt(1, false);
            } else {
                jTabbedPane1.setEnabledAt(1, true);
            }

            ComboBox_departemen_SPL_PEJUANG.removeAllItems();
            ComboBox_departemen_SPL_STAFF.removeAllItems();
            ComboBox_departemen_data_lembur.removeAllItems();
            ComboBox_departemen_rekap.removeAllItems();
            ComboBox_departemen_SPL_PEJUANG.addItem("All");
            ComboBox_departemen_SPL_STAFF.addItem("All");
            ComboBox_departemen_data_lembur.addItem("All");
            ComboBox_departemen_rekap.addItem("All");
            sql = "SELECT `kode_dep` FROM `tb_departemen`";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_departemen_SPL_PEJUANG.addItem(rs.getString("kode_dep"));
                ComboBox_departemen_SPL_STAFF.addItem(rs.getString("kode_dep"));
                ComboBox_departemen_data_lembur.addItem(rs.getString("kode_dep"));
                ComboBox_departemen_rekap.addItem(rs.getString("kode_dep"));
            }

            ComboBox_posisi_data_lembur.removeAllItems();
            ComboBox_posisi_data_lembur.addItem("All");
            sql = "SELECT DISTINCT(`posisi`) AS 'posisi' FROM `tb_karyawan` WHERE `status` = 'IN' AND `posisi` <> 'DIREKTUR' AND `posisi` IS NOT NULL";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_posisi_data_lembur.addItem(rs.getString("posisi"));
            }

            ComboBox_status_karyawan_data_lembur.removeAllItems();
            ComboBox_status_karyawan_data_lembur.addItem("All");
            sql = "SELECT DISTINCT(`status`) AS 'status' FROM `tb_karyawan` WHERE `status` IS NOT NULL";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_status_karyawan_data_lembur.addItem(rs.getString("status"));
            }
            ComboBox_status_karyawan_data_lembur.setSelectedItem("IN");
            refreshTable_SPL_PEJUANG();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_DataLembur.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void refreshTable_All() {
        try {
            String bagian = "AND `nama_bagian` LIKE '%" + ComboBox_bagian_data_lembur.getSelectedItem().toString() + "%' ";
            String departemen = "AND `kode_departemen` LIKE '%" + ComboBox_departemen_data_lembur.getSelectedItem().toString() + "%' ";
            String kelamin = "AND `jenis_kelamin` LIKE '%" + ComboBox_kelamin_data_lembur.getSelectedItem().toString() + "%' ";
            String status = "AND `status` LIKE '%" + ComboBox_status_karyawan_data_lembur.getSelectedItem().toString() + "%' ";
            String jenis_lembur = "AND `jenis_lembur` = '" + ComboBox_jenis_lembur_data_lembur.getSelectedItem().toString() + "' ";
            String posisi = "AND `posisi` = '" + ComboBox_posisi_data_lembur.getSelectedItem().toString() + "' ";
            String tgl = "";
            if ("All".equals(ComboBox_bagian_data_lembur.getSelectedItem().toString())) {
                bagian = "";
            }
            if ("All".equals(ComboBox_departemen_data_lembur.getSelectedItem().toString())) {
                departemen = "";
            }
            if ("All".equals(ComboBox_kelamin_data_lembur.getSelectedItem().toString())) {
                kelamin = "";
            }
            if ("All".equals(ComboBox_status_karyawan_data_lembur.getSelectedItem().toString())) {
                status = "";
            }
            if ("All".equals(ComboBox_jenis_lembur_data_lembur.getSelectedItem().toString())) {
                jenis_lembur = "";
            }
            if ("All".equals(ComboBox_posisi_data_lembur.getSelectedItem().toString())) {
                posisi = "";
            }
            if (Date_data_lembur1.getDate() != null && Date_data_lembur2.getDate() != null) {
                tgl = "AND `tanggal_lembur` BETWEEN '" + dateFormat.format(Date_data_lembur1.getDate()) + "' AND '" + dateFormat.format(Date_data_lembur2.getDate()) + "' ";
            }
            DefaultTableModel model = (DefaultTableModel) tabel_data_lembur.getModel();
            model.setRowCount(0);
            sql = "SELECT `nomor_lembur`, `tb_surat_lembur_detail`.`id_pegawai`, `tanggal_lembur`,`mulai_lembur`, `selesai_lembur`, `jumlah_jam`, `nama_pegawai`, `nama_bagian`, `jenis_lembur` "
                    + "FROM `tb_surat_lembur_detail` "
                    + "LEFT JOIN `tb_karyawan` ON `tb_surat_lembur_detail`.`id_pegawai` = `tb_karyawan`.`id_pegawai` "
                    + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`"
                    + "WHERE `nama_pegawai` LIKE '%" + txt_search_karyawan_data_lembur.getText() + "%' "
                    + bagian
                    + departemen
                    + kelamin
                    + posisi
                    + status
                    + jenis_lembur
                    + tgl;
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[9];
            while (rs.next()) {
                row[0] = rs.getString("nomor_lembur");
                row[1] = rs.getString("id_pegawai");
                row[2] = rs.getString("nama_pegawai");
                row[3] = rs.getString("nama_bagian");
                row[4] = rs.getDate("tanggal_lembur");
                row[5] = rs.getString("mulai_lembur");
                row[6] = rs.getString("selesai_lembur");
                row[7] = rs.getFloat("jumlah_jam");
                row[8] = rs.getString("jenis_lembur");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(tabel_data_lembur);
            label_total_data_lembur.setText(Integer.toString(tabel_data_lembur.getRowCount()));
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_DataLembur.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_SPL_PEJUANG() {
        try {
            DefaultTableModel model = (DefaultTableModel) Table_SPL_PEJUANG.getModel();
            model.setRowCount(0);
            String filter_lembur = "";
            if (Date_SPL_PEJUANG1.getDate() != null && Date_SPL_PEJUANG2.getDate() != null) {
                filter_lembur = " AND `tb_surat_lembur`.`tanggal_lembur` BETWEEN '" + dateFormat.format(Date_SPL_PEJUANG1.getDate()) + "' AND '" + dateFormat.format(Date_SPL_PEJUANG2.getDate()) + "'";
            }

            String departemen = ComboBox_departemen_SPL_PEJUANG.getSelectedItem().toString();
            if ("All".equals(ComboBox_departemen_SPL_PEJUANG.getSelectedItem().toString())) {
                departemen = "";
            }

            sql = "SELECT `tb_surat_lembur`.`nomor_surat`, `tanggal_surat`, `jenis_spl`, `kode_departemen`, `jenis_hari`, `tb_surat_lembur`.`tanggal_lembur`, `uraian_tugas`, `diajukan`, `disetujui`, `diketahui`, `tb_surat_lembur_detail`.`jenis_lembur`, COUNT(`tb_surat_lembur_detail`.`id_pegawai`) AS 'jumlah_anak' "
                    + "FROM `tb_surat_lembur` "
                    + "LEFT JOIN `tb_surat_lembur_detail` ON `tb_surat_lembur`.`nomor_surat` = `tb_surat_lembur_detail`.`nomor_surat`"
                    + "WHERE kode_departemen LIKE '%" + departemen + "%' \n"
                    + "AND `jenis_spl` = 'PEJUANG' "
                    + filter_lembur
                    + " GROUP BY `tb_surat_lembur`.`nomor_surat` ORDER BY `tanggal_surat` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[15];
            while (rs.next()) {
                row[0] = rs.getString("nomor_surat");
                row[1] = rs.getDate("tanggal_surat");
                row[2] = rs.getString("kode_departemen");
                row[3] = rs.getString("jenis_hari");
                row[4] = rs.getDate("tanggal_lembur");
                row[5] = rs.getString("jenis_lembur");
                row[6] = rs.getInt("jumlah_anak");
                row[7] = rs.getString("diajukan");
                row[8] = rs.getString("disetujui");
                row[9] = rs.getString("diketahui");
                row[10] = rs.getString("uraian_tugas");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_SPL_PEJUANG);
            int rowCount = Table_SPL_PEJUANG.getRowCount();
            label_total_SPL_PEJUANG.setText(String.valueOf(rowCount));

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_DataLembur.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_SPL_STAFF() {
        try {
            DefaultTableModel model = (DefaultTableModel) Table_SPL_STAFF.getModel();
            model.setRowCount(0);
            String filter_lembur = "";
            if (Date_SPL_STAFF1.getDate() != null && Date_SPL_STAFF2.getDate() != null) {
                filter_lembur = " AND `tb_surat_lembur`.`tanggal_lembur` BETWEEN '" + dateFormat.format(Date_SPL_STAFF1.getDate()) + "' AND '" + dateFormat.format(Date_SPL_STAFF2.getDate()) + "'";
            }

            String departemen = ComboBox_departemen_SPL_STAFF.getSelectedItem().toString();
            if ("All".equals(ComboBox_departemen_SPL_STAFF.getSelectedItem().toString())) {
                departemen = "";
            }

            sql = "SELECT `tb_surat_lembur`.`nomor_surat`, `tanggal_surat`, `jenis_spl`, `kode_departemen`, `jenis_hari`, `tb_surat_lembur`.`tanggal_lembur`, `uraian_tugas`, `diajukan`, `disetujui`, `diketahui`, `tb_surat_lembur_detail`.`jenis_lembur`, COUNT(`tb_surat_lembur_detail`.`id_pegawai`) AS 'jumlah_anak' "
                    + "FROM `tb_surat_lembur` "
                    + "LEFT JOIN `tb_surat_lembur_detail` ON `tb_surat_lembur`.`nomor_surat` = `tb_surat_lembur_detail`.`nomor_surat`"
                    + "WHERE kode_departemen LIKE '%" + departemen + "%' \n"
                    + "AND `jenis_spl` = 'STAFF' "
                    + filter_lembur
                    + " GROUP BY `tb_surat_lembur`.`nomor_surat` ORDER BY `tanggal_surat` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[15];
            while (rs.next()) {
                row[0] = rs.getString("nomor_surat");
                row[1] = rs.getDate("tanggal_surat");
                row[2] = rs.getString("kode_departemen");
                row[3] = rs.getString("jenis_hari");
                row[4] = rs.getDate("tanggal_lembur");
                row[5] = rs.getString("jenis_lembur");
                row[6] = rs.getInt("jumlah_anak");
                row[7] = rs.getString("diajukan");
                row[8] = rs.getString("disetujui");
                row[9] = rs.getString("diketahui");
                row[10] = rs.getString("uraian_tugas");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_SPL_STAFF);
            int rowCount = Table_SPL_STAFF.getRowCount();
            label_total_SPL_STAFF.setText(String.valueOf(rowCount));

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_DataLembur.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_detailLembur(String nomor_surat, JTable Table) {
        try {
            DefaultTableModel model = (DefaultTableModel) Table.getModel();
            model.setRowCount(0);
            sql = "SELECT `nomor_lembur`, `tb_surat_lembur_detail`.`id_pegawai`, `tanggal_lembur`,`mulai_lembur`, `selesai_lembur`, `jumlah_jam`, `menit_istirahat_lembur`, `nama_pegawai`, `nama_bagian` "
                    + "FROM `tb_surat_lembur_detail` "
                    + "LEFT JOIN `tb_karyawan` ON `tb_surat_lembur_detail`.`id_pegawai` = `tb_karyawan`.`id_pegawai` "
                    + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`"
                    + "WHERE `nomor_surat` = '" + nomor_surat + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[10];
            while (rs.next()) {
                row[0] = rs.getString("nomor_lembur");
                row[1] = rs.getString("id_pegawai");
                row[2] = rs.getString("nama_pegawai");
                row[3] = rs.getString("nama_bagian");
                row[4] = rs.getDate("tanggal_lembur");
                row[5] = rs.getString("mulai_lembur");
                row[6] = rs.getString("selesai_lembur");
                row[7] = rs.getFloat("jumlah_jam");
                row[8] = rs.getFloat("menit_istirahat_lembur");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_DataLembur.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_pegawai() {
        try {
            float tot_jam = 0;
            String bagian = ComboBox_bagian_rekap.getSelectedItem().toString();
            String departemen = ComboBox_departemen_rekap.getSelectedItem().toString();
            if ("All".equals(ComboBox_bagian_rekap.getSelectedItem().toString())) {
                bagian = "";
            }
            if ("All".equals(ComboBox_departemen_rekap.getSelectedItem().toString())) {
                departemen = "";
            }
            DefaultTableModel model = (DefaultTableModel) tabel_rekap_pegawai.getModel();
            model.setRowCount(0);
            String query = "SELECT `id_pegawai`, `nama_pegawai`, `tb_bagian`.`nama_bagian`,`tb_bagian`.`kode_departemen`, `tanggal_masuk`, `status` "
                    + "FROM `tb_karyawan` JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`"
                    + "WHERE `nama_pegawai` LIKE '%" + txt_search_karyawan_rekap.getText() + "%' "
                    + "AND `nama_bagian` LIKE '%" + bagian + "%' "
                    + "AND `kode_departemen` LIKE '%" + departemen + "%' "
                    + "ORDER BY `id_pegawai` DESC";
            ResultSet result = Utility.db.getStatement().executeQuery(query);
            Object[] row = new Object[8];
            while (result.next()) {
                row[0] = result.getString("id_pegawai");
                row[1] = result.getString("nama_pegawai");
                row[2] = result.getString("kode_departemen");
                row[3] = result.getString("nama_bagian");
                row[4] = result.getDate("tanggal_masuk");
                row[5] = result.getString("status");

                if (Date_SPL_PEJUANG1.getDate() != null && Date_SPL_PEJUANG2.getDate() != null) {
                    sql = "SELECT `tb_karyawan`.`id_pegawai`, SUM(`tb_surat_lembur_detail`.`jumlah_jam`) AS 'jam', COUNT(`tb_surat_lembur_detail`.`jumlah_jam`) AS 'hari' \n"
                            + "FROM `tb_karyawan` \n"
                            + "LEFT JOIN `tb_surat_lembur_detail` ON `tb_karyawan`.`id_pegawai` = `tb_surat_lembur_detail`.`id_pegawai` \n"
                            + "LEFT JOIN `tb_surat_lembur` ON `tb_surat_lembur_detail`.`nomor_surat` = `tb_surat_lembur`.`nomor_surat` \n"
                            + "WHERE `tb_karyawan`.`id_pegawai` = '" + result.getString("id_pegawai") + "' AND\n"
                            + "`tb_surat_lembur`.`tanggal_lembur` BETWEEN '" + dateFormat.format(Date_SPL_PEJUANG1.getDate()) + "' AND '" + dateFormat.format(Date_SPL_PEJUANG2.getDate()) + "'\n"
                            + "GROUP BY `tb_karyawan`.`id_pegawai`";
                } else {
                    sql = "SELECT `tb_karyawan`.`id_pegawai`, SUM(`tb_surat_lembur_detail`.`jumlah_jam`) AS 'jam', COUNT(`tb_surat_lembur_detail`.`jumlah_jam`) AS 'hari' \n"
                            + "FROM `tb_karyawan` \n"
                            + "LEFT JOIN `tb_surat_lembur_detail` ON `tb_karyawan`.`id_pegawai` = `tb_surat_lembur_detail`.`id_pegawai` \n"
                            + "LEFT JOIN `tb_surat_lembur` ON `tb_surat_lembur_detail`.`nomor_surat` = `tb_surat_lembur`.`nomor_surat` \n"
                            + "WHERE `tb_karyawan`.`id_pegawai` = '" + result.getString("id_pegawai") + "'\n"
                            + "GROUP BY `tb_karyawan`.`id_pegawai`";
                }
                pst = Utility.db.getConnection().prepareStatement(sql);
                rs = pst.executeQuery();
                if (rs.next()) {
                    row[6] = rs.getFloat("jam");
                    row[7] = rs.getInt("hari");
                    tot_jam = tot_jam + rs.getFloat("jam");
                } else {
                    row[6] = 0;
                    row[7] = 0;
                }
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(tabel_rekap_pegawai);
            label_total_jam.setText(String.valueOf(tot_jam));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Ijin_keluar.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_SPLKaryawan() {
        try {
            DefaultTableModel model = (DefaultTableModel) tabel_detail_lembur_rekap.getModel();
            model.setRowCount(0);
            int i = tabel_rekap_pegawai.getSelectedRow();
            sql = "SELECT * FROM `tb_surat_lembur` JOIN `tb_surat_lembur_detail` ON `tb_surat_lembur`.`nomor_surat` = `tb_surat_lembur_detail`.`nomor_surat`"
                    + "WHERE `tb_surat_lembur_detail`.`id_pegawai` = '" + tabel_rekap_pegawai.getValueAt(i, 0) + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[4];
            while (rs.next()) {
                row[0] = rs.getString("nomor_surat");
                row[1] = rs.getString("jenis_hari");
                row[2] = rs.getDate("tanggal_lembur");
                row[3] = rs.getFloat("jumlah_jam");

                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(tabel_detail_lembur_rekap);
            int rowCount = tabel_detail_lembur_rekap.getRowCount();
            label_total_SPL.setText(String.valueOf(rowCount));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_DataLembur.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_lemburMakan() {
        try {
            DefaultTableModel model = (DefaultTableModel) table_data_lembur_makan.getModel();
            model.setRowCount(0);
            sql = "SELECT `nomor_lembur`, `tb_surat_lembur_detail`.`id_pegawai`, `tb_karyawan`.`nama_pegawai`, `nama_bagian`, `tanggal_lembur`, `jenis_lembur`, `mulai_lembur`, `selesai_lembur`, `jumlah_jam`, `nomor_surat`, "
                    + "`absen_masuk`, `absen_pulang`, IF(`jenis_lembur` = 'Masuk', TIMESTAMPDIFF(minute, `absen_masuk`, `selesai_lembur`), TIMESTAMPDIFF(minute, `mulai_lembur`, `absen_pulang`)) AS 'menit_lembur' \n"
                    + "FROM `tb_surat_lembur_detail` "
                    + "LEFT JOIN `tb_karyawan` ON `tb_surat_lembur_detail`.`id_pegawai` = `tb_karyawan`.`id_pegawai`\n"
                    + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`\n"
                    + "LEFT JOIN (SELECT `pin`, MIN(TIME(`scan_date`)) AS 'absen_masuk', MAX(TIME(`scan_date`)) AS 'absen_pulang' FROM `att_log` WHERE DATE(`scan_date`) = '" + dateFormat.format(Date_lembur_makan.getDate()) + "' GROUP BY `pin`) attlog ON `tb_karyawan`.`pin_finger` = attlog.`pin`\n"
                    + "WHERE "
                    + "`jumlah_jam`+`menit_istirahat_lembur` >= 3 "
                    + "AND `tb_karyawan`.`nama_pegawai` LIKE '%" + txt_search_nama.getText() + "%' "
                    + "AND `tanggal_lembur` = '" + dateFormat.format(Date_lembur_makan.getDate()) + "' "
                    + "AND `nama_bagian` <> 'DRIVER' "
                    + "GROUP BY `tb_surat_lembur_detail`.`id_pegawai`";
//            System.out.println(sql);
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[15];
            while (rs.next()) {
                row[0] = rs.getString("id_pegawai");
                row[1] = rs.getString("nama_pegawai");
                row[2] = rs.getString("nama_bagian");
                row[3] = rs.getDate("tanggal_lembur");
                row[4] = rs.getString("mulai_lembur");
                row[5] = rs.getString("selesai_lembur");
                row[6] = rs.getFloat("jumlah_jam");
                if (rs.getString("jenis_lembur").equals("Masuk")) {
                    row[7] = rs.getString("absen_masuk");
                    row[8] = rs.getString("selesai_lembur");
                } else {
                    row[7] = rs.getString("mulai_lembur");
                    row[8] = rs.getString("absen_pulang");
                }
                row[9] = rs.getInt("menit_lembur");
                row[10] = Math.round(rs.getFloat("menit_lembur") / 60.f * 100.f) / 100.f;
                row[11] = rs.getString("jenis_lembur");
                model.addRow(row);
            }
            label_total_reproses.setText(Integer.toString(table_data_lembur_makan.getRowCount()));
            ColumnsAutoSizer.sizeColumnsToFit(table_data_lembur_makan);
            table_data_lembur_makan.setDefaultRenderer(Integer.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if ((int) table_data_lembur_makan.getValueAt(row, 9) < 180) {
                        if (isSelected) {
                            comp.setBackground(table_data_lembur_makan.getSelectionBackground());
                            comp.setForeground(table_data_lembur_makan.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.red);
                            comp.setForeground(Color.WHITE);
                        }
                    } else {
                        if (isSelected) {
                            comp.setBackground(table_data_lembur_makan.getSelectionBackground());
                            comp.setForeground(table_data_lembur_makan.getSelectionForeground());
                        } else {
                            comp.setBackground(table_data_lembur_makan.getBackground());
                            comp.setForeground(table_data_lembur_makan.getForeground());
                        }
                    }
                    return comp;
                }
            });
            table_data_lembur_makan.repaint();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_DataLembur.class.getName()).log(Level.SEVERE, null, ex);
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

        jPanel1 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel_SPL_pejuang = new javax.swing.JPanel();
        button_search_SPL_PEJUANG = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        ComboBox_departemen_SPL_PEJUANG = new javax.swing.JComboBox<>();
        Date_SPL_PEJUANG1 = new com.toedter.calendar.JDateChooser();
        Date_SPL_PEJUANG2 = new com.toedter.calendar.JDateChooser();
        button_export_SPL_PEJUANG = new javax.swing.JButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        Table_SPL_PEJUANG = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        label_total_SPL_PEJUANG = new javax.swing.JLabel();
        button_buat_SPL_PEJUANG = new javax.swing.JButton();
        button_edit_SPL_PEJUANG = new javax.swing.JButton();
        button_delete_SPL_PEJUANG = new javax.swing.JButton();
        button_disetujui_SPL_PEJUANG = new javax.swing.JButton();
        button_diketahui_SPL_PEJUANG = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabel_pegawai_lembur_SPL_PEJUANG = new javax.swing.JTable();
        jLabel8 = new javax.swing.JLabel();
        label_total_karyawan_SPL_PEJUANG = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        button_diketahui_semua_SPL_PEJUANG = new javax.swing.JButton();
        jPanel_SPL_staff = new javax.swing.JPanel();
        button_search_SPL_STAFF = new javax.swing.JButton();
        jLabel20 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        ComboBox_departemen_SPL_STAFF = new javax.swing.JComboBox<>();
        Date_SPL_STAFF1 = new com.toedter.calendar.JDateChooser();
        Date_SPL_STAFF2 = new com.toedter.calendar.JDateChooser();
        button_export_SPL_STAFF = new javax.swing.JButton();
        jScrollPane6 = new javax.swing.JScrollPane();
        Table_SPL_STAFF = new javax.swing.JTable();
        jLabel22 = new javax.swing.JLabel();
        label_total_SPL_STAFF = new javax.swing.JLabel();
        button_edit_SPL_STAFF = new javax.swing.JButton();
        button_delete_SPL_STAFF = new javax.swing.JButton();
        button_disetujui_SPL_STAFF = new javax.swing.JButton();
        button_diketahui_SPL_STAFF = new javax.swing.JButton();
        jScrollPane8 = new javax.swing.JScrollPane();
        tabel_pegawai_lembur_SPL_STAFF = new javax.swing.JTable();
        jLabel23 = new javax.swing.JLabel();
        label_total_karyawan_SPL_STAFF = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        button_diketahui_semua_SPL_STAFF = new javax.swing.JButton();
        button_buat_SPL_STAFF = new javax.swing.JButton();
        jPanel_data_lembur = new javax.swing.JPanel();
        jPanel_search_karyawan2 = new javax.swing.JPanel();
        txt_search_karyawan_data_lembur = new javax.swing.JTextField();
        button_search_karyawan_data_lembur = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        ComboBox_departemen_data_lembur = new javax.swing.JComboBox<>();
        ComboBox_bagian_data_lembur = new javax.swing.JComboBox<>();
        Date_data_lembur1 = new com.toedter.calendar.JDateChooser();
        Date_data_lembur2 = new com.toedter.calendar.JDateChooser();
        jLabel14 = new javax.swing.JLabel();
        ComboBox_kelamin_data_lembur = new javax.swing.JComboBox<>();
        ComboBox_status_karyawan_data_lembur = new javax.swing.JComboBox<>();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        ComboBox_jenis_lembur_data_lembur = new javax.swing.JComboBox<>();
        ComboBox_posisi_data_lembur = new javax.swing.JComboBox<>();
        jLabel17 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        button_export_data_lembur = new javax.swing.JButton();
        jScrollPane7 = new javax.swing.JScrollPane();
        tabel_data_lembur = new javax.swing.JTable();
        label_total_data_lembur = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jPanel_data_lembur_makan = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        txt_search_nama = new javax.swing.JTextField();
        button_search1 = new javax.swing.JButton();
        jLabel28 = new javax.swing.JLabel();
        label_total_reproses = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        table_data_lembur_makan = new javax.swing.JTable();
        jLabel31 = new javax.swing.JLabel();
        Date_lembur_makan = new com.toedter.calendar.JDateChooser();
        button_export_lembur_makan = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        button_print_laporan = new javax.swing.JButton();
        jPanel_rekap_per_karyawan = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabel_rekap_pegawai = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        tabel_detail_lembur_rekap = new javax.swing.JTable();
        button_export_rekap = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        label_total_SPL = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        label_total_jam = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        ComboBox_departemen_rekap = new javax.swing.JComboBox<>();
        ComboBox_bagian_rekap = new javax.swing.JComboBox<>();
        Date_lembur_rekap1 = new com.toedter.calendar.JDateChooser();
        Date_lembur_rekap2 = new com.toedter.calendar.JDateChooser();
        txt_search_karyawan_rekap = new javax.swing.JTextField();
        button_search_rekap = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Data Lembur Karyawan", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N
        jPanel1.setPreferredSize(new java.awt.Dimension(1366, 652));

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jPanel_SPL_pejuang.setBackground(new java.awt.Color(255, 255, 255));

        button_search_SPL_PEJUANG.setBackground(new java.awt.Color(255, 255, 255));
        button_search_SPL_PEJUANG.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_search_SPL_PEJUANG.setText("Search");
        button_search_SPL_PEJUANG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_search_SPL_PEJUANGActionPerformed(evt);
            }
        });

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel3.setText("Departemen :");

        jLabel35.setBackground(new java.awt.Color(255, 255, 255));
        jLabel35.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel35.setText("Tanggal Lembur :");

        ComboBox_departemen_SPL_PEJUANG.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        ComboBox_departemen_SPL_PEJUANG.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        Date_SPL_PEJUANG1.setBackground(new java.awt.Color(255, 255, 255));
        Date_SPL_PEJUANG1.setToolTipText("");
        Date_SPL_PEJUANG1.setDate(new Date());
        Date_SPL_PEJUANG1.setDateFormatString("dd MMMM yyyy");
        Date_SPL_PEJUANG1.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        Date_SPL_PEJUANG1.setMinSelectableDate(new java.util.Date(1483207315000L));

        Date_SPL_PEJUANG2.setBackground(new java.awt.Color(255, 255, 255));
        Date_SPL_PEJUANG2.setDate(new Date());
        Date_SPL_PEJUANG2.setDateFormatString("dd MMMM yyyy");
        Date_SPL_PEJUANG2.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N

        button_export_SPL_PEJUANG.setBackground(new java.awt.Color(255, 255, 255));
        button_export_SPL_PEJUANG.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_export_SPL_PEJUANG.setText("Export to Excel");
        button_export_SPL_PEJUANG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_SPL_PEJUANGActionPerformed(evt);
            }
        });

        jScrollPane5.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane5.setPreferredSize(new java.awt.Dimension(800, 550));

        Table_SPL_PEJUANG.setAutoCreateRowSorter(true);
        Table_SPL_PEJUANG.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Table_SPL_PEJUANG.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "Tgl Surat", "Departemen", "Hari", "Tgl Lembur", "Jenis Lembur", "Jumlah anak", "Diajukan", "Disetujui", "Diketahui", "Uraian Tugas"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
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
        Table_SPL_PEJUANG.getTableHeader().setReorderingAllowed(false);
        jScrollPane5.setViewportView(Table_SPL_PEJUANG);

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel1.setText("TOTAL :");

        label_total_SPL_PEJUANG.setBackground(new java.awt.Color(255, 255, 255));
        label_total_SPL_PEJUANG.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_SPL_PEJUANG.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        label_total_SPL_PEJUANG.setText("0");

        button_buat_SPL_PEJUANG.setBackground(new java.awt.Color(255, 255, 255));
        button_buat_SPL_PEJUANG.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_buat_SPL_PEJUANG.setText("Buat SPL PEJUANG");
        button_buat_SPL_PEJUANG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_buat_SPL_PEJUANGActionPerformed(evt);
            }
        });

        button_edit_SPL_PEJUANG.setBackground(new java.awt.Color(255, 255, 255));
        button_edit_SPL_PEJUANG.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_edit_SPL_PEJUANG.setText("Edit");
        button_edit_SPL_PEJUANG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_edit_SPL_PEJUANGActionPerformed(evt);
            }
        });

        button_delete_SPL_PEJUANG.setBackground(new java.awt.Color(255, 255, 255));
        button_delete_SPL_PEJUANG.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_delete_SPL_PEJUANG.setText("Delete");
        button_delete_SPL_PEJUANG.setEnabled(false);
        button_delete_SPL_PEJUANG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_delete_SPL_PEJUANGActionPerformed(evt);
            }
        });

        button_disetujui_SPL_PEJUANG.setBackground(new java.awt.Color(255, 255, 255));
        button_disetujui_SPL_PEJUANG.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_disetujui_SPL_PEJUANG.setText("Disetujui");
        button_disetujui_SPL_PEJUANG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_disetujui_SPL_PEJUANGActionPerformed(evt);
            }
        });

        button_diketahui_SPL_PEJUANG.setBackground(new java.awt.Color(255, 255, 255));
        button_diketahui_SPL_PEJUANG.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_diketahui_SPL_PEJUANG.setText("Diketahui");
        button_diketahui_SPL_PEJUANG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_diketahui_SPL_PEJUANGActionPerformed(evt);
            }
        });

        tabel_pegawai_lembur_SPL_PEJUANG.setAutoCreateRowSorter(true);
        tabel_pegawai_lembur_SPL_PEJUANG.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        tabel_pegawai_lembur_SPL_PEJUANG.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "ID Pegawai", "Nama", "Bagian", "Tgl Lembur", "Jam Mulai", "Jam Selesai", "Jam", "Istirahat"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Float.class, java.lang.Float.class
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
        tabel_pegawai_lembur_SPL_PEJUANG.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tabel_pegawai_lembur_SPL_PEJUANG);

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel8.setText("Jumlah Karyawan :");

        label_total_karyawan_SPL_PEJUANG.setBackground(new java.awt.Color(255, 255, 255));
        label_total_karyawan_SPL_PEJUANG.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_karyawan_SPL_PEJUANG.setText("0");

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel18.setText("Daftar Karyawan Lembur");

        button_diketahui_semua_SPL_PEJUANG.setBackground(new java.awt.Color(255, 255, 255));
        button_diketahui_semua_SPL_PEJUANG.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_diketahui_semua_SPL_PEJUANG.setText("Diketahui Semua");
        button_diketahui_semua_SPL_PEJUANG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_diketahui_semua_SPL_PEJUANGActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_SPL_pejuangLayout = new javax.swing.GroupLayout(jPanel_SPL_pejuang);
        jPanel_SPL_pejuang.setLayout(jPanel_SPL_pejuangLayout);
        jPanel_SPL_pejuangLayout.setHorizontalGroup(
            jPanel_SPL_pejuangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_SPL_pejuangLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_SPL_pejuangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 1331, Short.MAX_VALUE)
                    .addGroup(jPanel_SPL_pejuangLayout.createSequentialGroup()
                        .addGroup(jPanel_SPL_pejuangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_SPL_pejuangLayout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_departemen_SPL_PEJUANG, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel35)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_SPL_PEJUANG1, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_SPL_PEJUANG2, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_search_SPL_PEJUANG)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_export_SPL_PEJUANG))
                            .addGroup(jPanel_SPL_pejuangLayout.createSequentialGroup()
                                .addComponent(jLabel18)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_karyawan_SPL_PEJUANG))
                            .addGroup(jPanel_SPL_pejuangLayout.createSequentialGroup()
                                .addComponent(button_buat_SPL_PEJUANG)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_edit_SPL_PEJUANG)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_delete_SPL_PEJUANG)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_disetujui_SPL_PEJUANG)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_diketahui_SPL_PEJUANG)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_diketahui_semua_SPL_PEJUANG)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_SPL_PEJUANG)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel_SPL_pejuangLayout.setVerticalGroup(
            jPanel_SPL_pejuangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_SPL_pejuangLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_SPL_pejuangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(ComboBox_departemen_SPL_PEJUANG, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_SPL_PEJUANG1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_SPL_PEJUANG2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export_SPL_PEJUANG, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_search_SPL_PEJUANG, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_SPL_pejuangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(button_delete_SPL_PEJUANG, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_disetujui_SPL_PEJUANG, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_diketahui_SPL_PEJUANG, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_diketahui_semua_SPL_PEJUANG, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_buat_SPL_PEJUANG, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_edit_SPL_PEJUANG, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel_SPL_pejuangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(label_total_SPL_PEJUANG, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 299, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_SPL_pejuangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(label_total_karyawan_SPL_PEJUANG)
                    .addComponent(jLabel18))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 237, Short.MAX_VALUE)
                .addGap(15, 15, 15))
        );

        jTabbedPane1.addTab("Surat Perintah Lembur PEJUANG", jPanel_SPL_pejuang);

        jPanel_SPL_staff.setBackground(new java.awt.Color(255, 255, 255));

        button_search_SPL_STAFF.setBackground(new java.awt.Color(255, 255, 255));
        button_search_SPL_STAFF.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_search_SPL_STAFF.setText("Search");
        button_search_SPL_STAFF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_search_SPL_STAFFActionPerformed(evt);
            }
        });

        jLabel20.setBackground(new java.awt.Color(255, 255, 255));
        jLabel20.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel20.setText("Departemen :");

        jLabel41.setBackground(new java.awt.Color(255, 255, 255));
        jLabel41.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel41.setText("Tanggal Lembur :");

        ComboBox_departemen_SPL_STAFF.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        ComboBox_departemen_SPL_STAFF.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        Date_SPL_STAFF1.setBackground(new java.awt.Color(255, 255, 255));
        Date_SPL_STAFF1.setToolTipText("");
        Date_SPL_STAFF1.setDate(new Date());
        Date_SPL_STAFF1.setDateFormatString("dd MMMM yyyy");
        Date_SPL_STAFF1.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        Date_SPL_STAFF1.setMinSelectableDate(new java.util.Date(1483207315000L));

        Date_SPL_STAFF2.setBackground(new java.awt.Color(255, 255, 255));
        Date_SPL_STAFF2.setDate(new Date());
        Date_SPL_STAFF2.setDateFormatString("dd MMMM yyyy");
        Date_SPL_STAFF2.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N

        button_export_SPL_STAFF.setBackground(new java.awt.Color(255, 255, 255));
        button_export_SPL_STAFF.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_export_SPL_STAFF.setText("Export to Excel");
        button_export_SPL_STAFF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_SPL_STAFFActionPerformed(evt);
            }
        });

        jScrollPane6.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane6.setPreferredSize(new java.awt.Dimension(800, 550));

        Table_SPL_STAFF.setAutoCreateRowSorter(true);
        Table_SPL_STAFF.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Table_SPL_STAFF.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "Tgl Surat", "Departemen", "Hari", "Tgl Lembur", "Jenis Lembur", "Jumlah anak", "Diajukan", "Disetujui", "Diketahui", "Uraian Tugas"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
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
        Table_SPL_STAFF.getTableHeader().setReorderingAllowed(false);
        jScrollPane6.setViewportView(Table_SPL_STAFF);

        jLabel22.setBackground(new java.awt.Color(255, 255, 255));
        jLabel22.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel22.setText("TOTAL :");

        label_total_SPL_STAFF.setBackground(new java.awt.Color(255, 255, 255));
        label_total_SPL_STAFF.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_SPL_STAFF.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        label_total_SPL_STAFF.setText("0");

        button_edit_SPL_STAFF.setBackground(new java.awt.Color(255, 255, 255));
        button_edit_SPL_STAFF.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_edit_SPL_STAFF.setText("Edit");
        button_edit_SPL_STAFF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_edit_SPL_STAFFActionPerformed(evt);
            }
        });

        button_delete_SPL_STAFF.setBackground(new java.awt.Color(255, 255, 255));
        button_delete_SPL_STAFF.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_delete_SPL_STAFF.setText("Delete");
        button_delete_SPL_STAFF.setEnabled(false);
        button_delete_SPL_STAFF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_delete_SPL_STAFFActionPerformed(evt);
            }
        });

        button_disetujui_SPL_STAFF.setBackground(new java.awt.Color(255, 255, 255));
        button_disetujui_SPL_STAFF.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_disetujui_SPL_STAFF.setText("Disetujui");
        button_disetujui_SPL_STAFF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_disetujui_SPL_STAFFActionPerformed(evt);
            }
        });

        button_diketahui_SPL_STAFF.setBackground(new java.awt.Color(255, 255, 255));
        button_diketahui_SPL_STAFF.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_diketahui_SPL_STAFF.setText("Diketahui");
        button_diketahui_SPL_STAFF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_diketahui_SPL_STAFFActionPerformed(evt);
            }
        });

        tabel_pegawai_lembur_SPL_STAFF.setAutoCreateRowSorter(true);
        tabel_pegawai_lembur_SPL_STAFF.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        tabel_pegawai_lembur_SPL_STAFF.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "ID Pegawai", "Nama", "Bagian", "Tgl Lembur", "Jam Mulai", "Jam Selesai", "Jam", "Istirahat"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Float.class, java.lang.Float.class
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
        tabel_pegawai_lembur_SPL_STAFF.getTableHeader().setReorderingAllowed(false);
        jScrollPane8.setViewportView(tabel_pegawai_lembur_SPL_STAFF);

        jLabel23.setBackground(new java.awt.Color(255, 255, 255));
        jLabel23.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel23.setText("Jumlah Karyawan :");

        label_total_karyawan_SPL_STAFF.setBackground(new java.awt.Color(255, 255, 255));
        label_total_karyawan_SPL_STAFF.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_karyawan_SPL_STAFF.setText("0");

        jLabel24.setBackground(new java.awt.Color(255, 255, 255));
        jLabel24.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel24.setText("Daftar Karyawan Lembur");

        button_diketahui_semua_SPL_STAFF.setBackground(new java.awt.Color(255, 255, 255));
        button_diketahui_semua_SPL_STAFF.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_diketahui_semua_SPL_STAFF.setText("Diketahui Semua");
        button_diketahui_semua_SPL_STAFF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_diketahui_semua_SPL_STAFFActionPerformed(evt);
            }
        });

        button_buat_SPL_STAFF.setBackground(new java.awt.Color(255, 255, 255));
        button_buat_SPL_STAFF.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_buat_SPL_STAFF.setText("Buat SPL STAFF");
        button_buat_SPL_STAFF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_buat_SPL_STAFFActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_SPL_staffLayout = new javax.swing.GroupLayout(jPanel_SPL_staff);
        jPanel_SPL_staff.setLayout(jPanel_SPL_staffLayout);
        jPanel_SPL_staffLayout.setHorizontalGroup(
            jPanel_SPL_staffLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_SPL_staffLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_SPL_staffLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane8)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 1331, Short.MAX_VALUE)
                    .addGroup(jPanel_SPL_staffLayout.createSequentialGroup()
                        .addGroup(jPanel_SPL_staffLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_SPL_staffLayout.createSequentialGroup()
                                .addComponent(jLabel20)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_departemen_SPL_STAFF, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel41)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_SPL_STAFF1, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_SPL_STAFF2, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_search_SPL_STAFF)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_export_SPL_STAFF))
                            .addGroup(jPanel_SPL_staffLayout.createSequentialGroup()
                                .addComponent(jLabel24)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel23)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_karyawan_SPL_STAFF))
                            .addGroup(jPanel_SPL_staffLayout.createSequentialGroup()
                                .addComponent(button_buat_SPL_STAFF)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_edit_SPL_STAFF)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_delete_SPL_STAFF)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_disetujui_SPL_STAFF)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_diketahui_SPL_STAFF)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_diketahui_semua_SPL_STAFF)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel22)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_SPL_STAFF)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel_SPL_staffLayout.setVerticalGroup(
            jPanel_SPL_staffLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_SPL_staffLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_SPL_staffLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(ComboBox_departemen_SPL_STAFF, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_SPL_STAFF1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_SPL_STAFF2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export_SPL_STAFF, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_search_SPL_STAFF, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_SPL_staffLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(button_delete_SPL_STAFF, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_disetujui_SPL_STAFF, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_diketahui_SPL_STAFF, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_diketahui_semua_SPL_STAFF, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_edit_SPL_STAFF, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_buat_SPL_STAFF, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel_SPL_staffLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(label_total_SPL_STAFF, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 299, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_SPL_staffLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23)
                    .addComponent(label_total_karyawan_SPL_STAFF)
                    .addComponent(jLabel24))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 237, Short.MAX_VALUE)
                .addGap(15, 15, 15))
        );

        jTabbedPane1.addTab("Surat Perintah Lembur STAFF", jPanel_SPL_staff);

        jPanel_data_lembur.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_data_lembur.setPreferredSize(new java.awt.Dimension(1366, 652));

        jPanel_search_karyawan2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_search_karyawan2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));

        txt_search_karyawan_data_lembur.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_karyawan_data_lembur.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_karyawan_data_lemburKeyPressed(evt);
            }
        });

        button_search_karyawan_data_lembur.setBackground(new java.awt.Color(255, 255, 255));
        button_search_karyawan_data_lembur.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search_karyawan_data_lembur.setText("Search");
        button_search_karyawan_data_lembur.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_search_karyawan_data_lemburActionPerformed(evt);
            }
        });

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel10.setText("Nama Karyawan :");

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel11.setText("Departemen :");

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel13.setText("Bagian :");

        jLabel39.setBackground(new java.awt.Color(255, 255, 255));
        jLabel39.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        jLabel39.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel39.setText("-");

        jLabel40.setBackground(new java.awt.Color(255, 255, 255));
        jLabel40.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        jLabel40.setText("Date Filter (by Tanggal lembur) :");

        ComboBox_departemen_data_lembur.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_departemen_data_lembur.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));
        ComboBox_departemen_data_lembur.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ComboBox_departemen_data_lemburActionPerformed(evt);
            }
        });

        ComboBox_bagian_data_lembur.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_bagian_data_lembur.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        Date_data_lembur1.setBackground(new java.awt.Color(255, 255, 255));
        Date_data_lembur1.setToolTipText("");
        Date_data_lembur1.setDate(new Date());
        Date_data_lembur1.setDateFormatString("dd MMMM yyyy");
        Date_data_lembur1.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        Date_data_lembur1.setMinSelectableDate(new java.util.Date(1483207315000L));

        Date_data_lembur2.setBackground(new java.awt.Color(255, 255, 255));
        Date_data_lembur2.setDate(new Date());
        Date_data_lembur2.setDateFormatString("dd MMMM yyyy");
        Date_data_lembur2.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel14.setText("Jenis Kelamin  :");

        ComboBox_kelamin_data_lembur.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_kelamin_data_lembur.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Laki-Laki", "Perempuan" }));

        ComboBox_status_karyawan_data_lembur.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_status_karyawan_data_lembur.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "IN", "OUT" }));
        ComboBox_status_karyawan_data_lembur.setSelectedIndex(1);

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel15.setText("Status :");

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel16.setText("Jenis lembur :");

        ComboBox_jenis_lembur_data_lembur.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_jenis_lembur_data_lembur.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Masuk", "Pulang" }));

        ComboBox_posisi_data_lembur.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_posisi_data_lembur.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel17.setText("Posisi :");

        javax.swing.GroupLayout jPanel_search_karyawan2Layout = new javax.swing.GroupLayout(jPanel_search_karyawan2);
        jPanel_search_karyawan2.setLayout(jPanel_search_karyawan2Layout);
        jPanel_search_karyawan2Layout.setHorizontalGroup(
            jPanel_search_karyawan2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_search_karyawan2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_search_karyawan2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txt_search_karyawan_data_lembur)
                    .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_search_karyawan2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                    .addComponent(ComboBox_departemen_data_lembur, 0, 150, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_search_karyawan2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                    .addComponent(ComboBox_bagian_data_lembur, 0, 150, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_search_karyawan2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel14)
                    .addComponent(ComboBox_kelamin_data_lembur, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_search_karyawan2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel15)
                    .addComponent(ComboBox_status_karyawan_data_lembur, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_search_karyawan2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ComboBox_jenis_lembur_data_lembur, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_search_karyawan2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel17)
                    .addGroup(jPanel_search_karyawan2Layout.createSequentialGroup()
                        .addComponent(ComboBox_posisi_data_lembur, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_search_karyawan_data_lembur)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel_search_karyawan2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel40)
                    .addGroup(jPanel_search_karyawan2Layout.createSequentialGroup()
                        .addComponent(Date_data_lembur1, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_data_lembur2, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel_search_karyawan2Layout.setVerticalGroup(
            jPanel_search_karyawan2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_search_karyawan2Layout.createSequentialGroup()
                .addGroup(jPanel_search_karyawan2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_search_karyawan2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel_search_karyawan2Layout.createSequentialGroup()
                            .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel_search_karyawan2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(ComboBox_jenis_lembur_data_lembur, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(button_search_karyawan_data_lembur, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(ComboBox_posisi_data_lembur, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(jPanel_search_karyawan2Layout.createSequentialGroup()
                            .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(ComboBox_status_karyawan_data_lembur, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel_search_karyawan2Layout.createSequentialGroup()
                            .addGroup(jPanel_search_karyawan2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_search_karyawan2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel_search_karyawan2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel_search_karyawan2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(ComboBox_bagian_data_lembur, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(ComboBox_kelamin_data_lembur, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(ComboBox_departemen_data_lembur, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(Date_data_lembur1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(Date_data_lembur2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txt_search_karyawan_data_lembur, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 13, Short.MAX_VALUE))
        );

        jLabel19.setBackground(new java.awt.Color(255, 255, 255));
        jLabel19.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel19.setText("DATA LEMBUR");

        button_export_data_lembur.setBackground(new java.awt.Color(255, 255, 255));
        button_export_data_lembur.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_data_lembur.setText("Export to Excel");
        button_export_data_lembur.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_data_lemburActionPerformed(evt);
            }
        });

        tabel_data_lembur.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        tabel_data_lembur.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "ID Pegawai", "Nama", "Bagian", "Tgl Lembur", "Jam Mulai", "Jam Selesai", "Jam", "Jenis Lembur"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Float.class, java.lang.String.class
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
        tabel_data_lembur.getTableHeader().setReorderingAllowed(false);
        jScrollPane7.setViewportView(tabel_data_lembur);

        label_total_data_lembur.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_lembur.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_data_lembur.setText("0");

        jLabel21.setBackground(new java.awt.Color(255, 255, 255));
        jLabel21.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel21.setText("TOTAL :");

        javax.swing.GroupLayout jPanel_data_lemburLayout = new javax.swing.GroupLayout(jPanel_data_lembur);
        jPanel_data_lembur.setLayout(jPanel_data_lemburLayout);
        jPanel_data_lemburLayout.setHorizontalGroup(
            jPanel_data_lemburLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_data_lemburLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_data_lemburLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel_search_karyawan2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel_data_lemburLayout.createSequentialGroup()
                        .addComponent(jLabel19)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel21)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_data_lembur)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_export_data_lembur))
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 1331, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel_data_lemburLayout.setVerticalGroup(
            jPanel_data_lemburLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_data_lemburLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel_search_karyawan2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_data_lemburLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_export_data_lembur)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_data_lembur, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 529, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Data Lembur", jPanel_data_lembur);

        jPanel_data_lembur_makan.setBackground(new java.awt.Color(255, 255, 255));

        jLabel27.setBackground(new java.awt.Color(255, 255, 255));
        jLabel27.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel27.setText("Tanggal Lembur :");

        txt_search_nama.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_search_nama.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_namaKeyPressed(evt);
            }
        });

        button_search1.setBackground(new java.awt.Color(255, 255, 255));
        button_search1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_search1.setText("Search");
        button_search1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_search1ActionPerformed(evt);
            }
        });

        jLabel28.setBackground(new java.awt.Color(255, 255, 255));
        jLabel28.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel28.setText("Total Data :");

        label_total_reproses.setBackground(new java.awt.Color(255, 255, 255));
        label_total_reproses.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_reproses.setText("0");

        jLabel38.setBackground(new java.awt.Color(255, 255, 255));
        jLabel38.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel38.setText("Data Karyawan Lembur yang mendapat makan");

        table_data_lembur_makan.setAutoCreateRowSorter(true);
        table_data_lembur_makan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        table_data_lembur_makan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Pegawai", "Nama", "Bagian", "Tgl Lembur", "Mulai Lembur", "Selesai Lembur", "Jumlah Jam", "Mulai Lembur Real", "Selesai Lembur Real", "Lembur (menit)", "Lembur (Jam)", "Jenis Lembur"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Float.class, java.lang.String.class
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
        table_data_lembur_makan.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(table_data_lembur_makan);

        jLabel31.setBackground(new java.awt.Color(255, 255, 255));
        jLabel31.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel31.setText("Nama :");

        Date_lembur_makan.setBackground(new java.awt.Color(255, 255, 255));
        Date_lembur_makan.setDate(new Date());
        Date_lembur_makan.setDateFormatString("dd MMMM yyyy");
        Date_lembur_makan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        button_export_lembur_makan.setBackground(new java.awt.Color(255, 255, 255));
        button_export_lembur_makan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_lembur_makan.setText("Export to Excel");
        button_export_lembur_makan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_lembur_makanActionPerformed(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Arial", 2, 11)); // NOI18N
        jLabel12.setText("NB : Syarat untuk mendapatkan makan adalah jam lembur + istirahat >= 3 jam dan bukan driver.");

        button_print_laporan.setBackground(new java.awt.Color(255, 255, 255));
        button_print_laporan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_print_laporan.setText("Print Laporan");
        button_print_laporan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_print_laporanActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_data_lembur_makanLayout = new javax.swing.GroupLayout(jPanel_data_lembur_makan);
        jPanel_data_lembur_makan.setLayout(jPanel_data_lembur_makanLayout);
        jPanel_data_lembur_makanLayout.setHorizontalGroup(
            jPanel_data_lembur_makanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_data_lembur_makanLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_data_lembur_makanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4)
                    .addGroup(jPanel_data_lembur_makanLayout.createSequentialGroup()
                        .addComponent(jLabel31)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_nama, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel27)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_lembur_makan, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_search1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel28)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_reproses)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 508, Short.MAX_VALUE)
                        .addComponent(button_print_laporan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_export_lembur_makan))
                    .addGroup(jPanel_data_lembur_makanLayout.createSequentialGroup()
                        .addGroup(jPanel_data_lembur_makanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel38)
                            .addComponent(jLabel12))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel_data_lembur_makanLayout.setVerticalGroup(
            jPanel_data_lembur_makanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_data_lembur_makanLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_data_lembur_makanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_search_nama, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_search1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_reproses, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_lembur_makan, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export_lembur_makan)
                    .addComponent(button_print_laporan))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 550, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel12)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Data Lembur Dapat Makan", jPanel_data_lembur_makan);

        jPanel_rekap_per_karyawan.setBackground(new java.awt.Color(255, 255, 255));

        tabel_rekap_pegawai.setAutoCreateRowSorter(true);
        tabel_rekap_pegawai.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tabel_rekap_pegawai.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Pegawai", "Nama Pegawai", "Departemen", "Bagian", "Tgl Masuk", "Status", "Jam", "Hari"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.Float.class, java.lang.Integer.class
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
        tabel_rekap_pegawai.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(tabel_rekap_pegawai);

        tabel_detail_lembur_rekap.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tabel_detail_lembur_rekap.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No Surat", "Jenis Hari", "Tgl Lembur", "Jam"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Float.class
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
        tabel_detail_lembur_rekap.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(tabel_detail_lembur_rekap);

        button_export_rekap.setBackground(new java.awt.Color(255, 255, 255));
        button_export_rekap.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_rekap.setText("Export to Excel");
        button_export_rekap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_rekapActionPerformed(evt);
            }
        });

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel9.setText("DAFTAR KARYAWAN WALETA");

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel5.setText("TOTAL :");

        label_total_SPL.setBackground(new java.awt.Color(255, 255, 255));
        label_total_SPL.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_SPL.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        label_total_SPL.setText("0000");

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel6.setText("TOTAL JAM :");

        label_total_jam.setBackground(new java.awt.Color(255, 255, 255));
        label_total_jam.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_jam.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        label_total_jam.setText("0000");

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel7.setText("Bagian :");

        jLabel37.setBackground(new java.awt.Color(255, 255, 255));
        jLabel37.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        jLabel37.setText("Tanggal lembur :");

        ComboBox_departemen_rekap.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_departemen_rekap.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));
        ComboBox_departemen_rekap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ComboBox_departemen_rekapActionPerformed(evt);
            }
        });

        ComboBox_bagian_rekap.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_bagian_rekap.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        Date_lembur_rekap1.setBackground(new java.awt.Color(255, 255, 255));
        Date_lembur_rekap1.setToolTipText("");
        Date_lembur_rekap1.setDateFormatString("dd MMMM yyyy");
        Date_lembur_rekap1.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        Date_lembur_rekap1.setMinSelectableDate(new java.util.Date(1483207315000L));

        Date_lembur_rekap2.setBackground(new java.awt.Color(255, 255, 255));
        Date_lembur_rekap2.setDateFormatString("dd MMMM yyyy");
        Date_lembur_rekap2.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N

        txt_search_karyawan_rekap.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_karyawan_rekap.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_karyawan_rekapKeyPressed(evt);
            }
        });

        button_search_rekap.setBackground(new java.awt.Color(255, 255, 255));
        button_search_rekap.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search_rekap.setText("Search");
        button_search_rekap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_search_rekapActionPerformed(evt);
            }
        });

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("Nama Karyawan :");

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel4.setText("Departemen :");

        javax.swing.GroupLayout jPanel_rekap_per_karyawanLayout = new javax.swing.GroupLayout(jPanel_rekap_per_karyawan);
        jPanel_rekap_per_karyawan.setLayout(jPanel_rekap_per_karyawanLayout);
        jPanel_rekap_per_karyawanLayout.setHorizontalGroup(
            jPanel_rekap_per_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_rekap_per_karyawanLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_rekap_per_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_rekap_per_karyawanLayout.createSequentialGroup()
                        .addGroup(jPanel_rekap_per_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_rekap_per_karyawanLayout.createSequentialGroup()
                                .addGap(782, 782, 782)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_jam, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 900, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_rekap_per_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel_rekap_per_karyawanLayout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_SPL, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 425, Short.MAX_VALUE)))
                    .addGroup(jPanel_rekap_per_karyawanLayout.createSequentialGroup()
                        .addGroup(jPanel_rekap_per_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9)
                            .addGroup(jPanel_rekap_per_karyawanLayout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_karyawan_rekap, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_departemen_rekap, 0, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_bagian_rekap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel37)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_lembur_rekap1, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_lembur_rekap2, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_search_rekap)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_export_rekap)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel_rekap_per_karyawanLayout.setVerticalGroup(
            jPanel_rekap_per_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_rekap_per_karyawanLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_rekap_per_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_departemen_rekap, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_bagian_rekap, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_lembur_rekap1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_lembur_rekap2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_karyawan_rekap, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_search_rekap, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export_rekap, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_rekap_per_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel_rekap_per_karyawanLayout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 547, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_rekap_per_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_SPL, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel_rekap_per_karyawanLayout.createSequentialGroup()
                        .addComponent(jScrollPane2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_rekap_per_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_jam, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Rekap / Karyawan", jPanel_rekap_per_karyawan);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 700, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void button_export_SPL_PEJUANGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_SPL_PEJUANGActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Table_SPL_PEJUANG.getModel();
        ExportToExcel.writeToExcel(model, jPanel1);
    }//GEN-LAST:event_button_export_SPL_PEJUANGActionPerformed

    private void button_search_SPL_PEJUANGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_SPL_PEJUANGActionPerformed
        // TODO add your handling code here:
        refreshTable_SPL_PEJUANG();
    }//GEN-LAST:event_button_search_SPL_PEJUANGActionPerformed

    private void button_buat_SPL_PEJUANGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_buat_SPL_PEJUANGActionPerformed
        // TODO add your handling code here:
        JDialog_Add_SuratPerintahLembur Add = new JDialog_Add_SuratPerintahLembur(new javax.swing.JFrame(), true, null, "PEJUANG");
        Add.pack();
        Add.setLocationRelativeTo(this);
        Add.setVisible(true);
        Add.setEnabled(true);
        refreshTable_SPL_PEJUANG();
    }//GEN-LAST:event_button_buat_SPL_PEJUANGActionPerformed

    private void button_edit_SPL_PEJUANGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_edit_SPL_PEJUANGActionPerformed
        // TODO add your handling code here:
        try {
            int row = Table_SPL_PEJUANG.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan Pilih data yang akan di ubah");
            } else {
                String no_surat = Table_SPL_PEJUANG.getValueAt(row, 0).toString();
                JDialog_Add_SuratPerintahLembur Add = new JDialog_Add_SuratPerintahLembur(new javax.swing.JFrame(), true, no_surat, null);
                Add.pack();
                Add.setLocationRelativeTo(this);
                Add.setVisible(true);
                Add.setEnabled(true);
                refreshTable_SPL_PEJUANG();
            }
        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_DataLembur.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_edit_SPL_PEJUANGActionPerformed

    private void button_delete_SPL_PEJUANGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_delete_SPL_PEJUANGActionPerformed
        // TODO add your handling code here:
//        try {
//            int row = Table_surat_lembur.getSelectedRow();
//            if (row == -1) {
//                JOptionPane.showMessageDialog(this, "Silahkan pilih data yang ingin di hapus !");
//            } else {
//                int dialogResult = JOptionPane.showConfirmDialog(this, "Yakin hapus data ini?", "Warning", 0);
//                if (dialogResult == JOptionPane.YES_OPTION) {
//                    // delete code here
//                    String Query = "DELETE FROM `tb_surat_lembur` WHERE `nomor_surat` = '" + Table_surat_lembur.getValueAt(row, 0) + "'";
//                    Utility.db.getConnection().createStatement();
//                    if ((Utility.db.getStatement().executeUpdate(Query)) == 1) {
//                        refreshTable_SPL_PEJUANG();
//                        JOptionPane.showMessageDialog(this, "SUKSES!");
//                    } else {
//                        JOptionPane.showMessageDialog(this, "FAILED!");
//                    }
//                }
//            }
//        } catch (HeadlessException | SQLException e) {
//            JOptionPane.showMessageDialog(this, e);
//            Logger.getLogger(JPanel_DataLembur.class.getName()).log(Level.SEVERE, null, e);
//        }
    }//GEN-LAST:event_button_delete_SPL_PEJUANGActionPerformed

    private void txt_search_karyawan_rekapKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_karyawan_rekapKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_pegawai();
        }
    }//GEN-LAST:event_txt_search_karyawan_rekapKeyPressed

    private void button_search_rekapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_rekapActionPerformed
        // TODO add your handling code here:
        refreshTable_pegawai();
        DefaultTableModel model = (DefaultTableModel) tabel_detail_lembur_rekap.getModel();
        model.setRowCount(0);
    }//GEN-LAST:event_button_search_rekapActionPerformed

    private void ComboBox_departemen_rekapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ComboBox_departemen_rekapActionPerformed
        // TODO add your handling code here:
        try {
            ComboBox_bagian_rekap.removeAllItems();
            String query = "SELECT `nama_bagian` FROM `tb_bagian` ORDER BY `nama_bagian`";
            if (ComboBox_departemen_rekap.getSelectedItem() != "All") {
                query = "SELECT `nama_bagian` FROM `tb_bagian` WHERE `kode_departemen`='" + ComboBox_departemen_rekap.getSelectedItem() + "'";
            }
            rs = Utility.db.getStatement().executeQuery(query);
            ComboBox_bagian_rekap.addItem("All");
            while (rs.next()) {
                ComboBox_bagian_rekap.addItem(rs.getString("nama_bagian"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e);
        }
    }//GEN-LAST:event_ComboBox_departemen_rekapActionPerformed

    private void button_export_rekapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_rekapActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) tabel_detail_lembur_rekap.getModel();
        ExportToExcel.writeToExcel(model, jPanel1);
    }//GEN-LAST:event_button_export_rekapActionPerformed

    private void button_disetujui_SPL_PEJUANGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_disetujui_SPL_PEJUANGActionPerformed
        // TODO add your handling code here:
        int i = Table_SPL_PEJUANG.getSelectedRow();
        if (i == -1) {
            JOptionPane.showMessageDialog(this, "Silahkan pilih data yang akan di kirim !");
        } else {
            boolean check = true;
            if (!MainForm.Login_Posisi.equals("MANAGER") && !MainForm.Login_Posisi.equals("STAFF 5") && !MainForm.Login_Posisi.equals("STAFF 6")) {
                JOptionPane.showMessageDialog(this, "Hanya STAFF / MANAGER yang bisa menyetujui lembur !");
                check = false;
            } else if (Table_SPL_PEJUANG.getValueAt(i, 8) != null) {//sudah diketahui hr
                JOptionPane.showMessageDialog(this, "Data lembur sudah disetujui !");
                check = false;
            }
            if (check) {
                try {
                    sql = "UPDATE `tb_surat_lembur` SET "
                            + "`disetujui`='" + MainForm.Login_NamaPegawai + " " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()) + "' "
                            + "WHERE `nomor_surat` = '" + Table_SPL_PEJUANG.getValueAt(i, 0).toString() + "'";
                    Utility.db.getStatement().executeUpdate(sql);
                    refreshTable_SPL_PEJUANG();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, ex);
                    Logger.getLogger(JPanel_DataLembur.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_button_disetujui_SPL_PEJUANGActionPerformed

    private void button_diketahui_SPL_PEJUANGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_diketahui_SPL_PEJUANGActionPerformed
        // TODO add your handling code here:
        int i = Table_SPL_PEJUANG.getSelectedRow();
        if (i == -1) {
            JOptionPane.showMessageDialog(this, "Silahkan pilih data yang akan di kirim !");
        } else {
            boolean check = true;
            if (!MainForm.Login_Departemen.equals("HRGA")) {
                JOptionPane.showMessageDialog(this, "Harap login menggunakan user HRD !");
                check = false;
            } else if (Table_SPL_PEJUANG.getValueAt(i, 8) == null) {//kalau belum disetujui
                JOptionPane.showMessageDialog(this, "Data lembur belum disetujui !");
                check = false;
            } else if (Table_SPL_PEJUANG.getValueAt(i, 9) != null) {//sudah diketahui hr
                JOptionPane.showMessageDialog(this, "Data lembur sudah diketahui !");
                check = false;
            }
            if (check) {
                try {
                    sql = "UPDATE `tb_surat_lembur` SET "
                            + "`diketahui`='" + MainForm.Login_NamaPegawai + " " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()) + "' "
                            + "WHERE `nomor_surat` = '" + Table_SPL_PEJUANG.getValueAt(i, 0).toString() + "'";
                    Utility.db.getStatement().executeUpdate(sql);
                    refreshTable_SPL_PEJUANG();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, ex);
                    Logger.getLogger(JPanel_DataLembur.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_button_diketahui_SPL_PEJUANGActionPerformed

    private void txt_search_namaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_namaKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_lemburMakan();
        }
    }//GEN-LAST:event_txt_search_namaKeyPressed

    private void button_search1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search1ActionPerformed
        // TODO add your handling code here:
        refreshTable_lemburMakan();
    }//GEN-LAST:event_button_search1ActionPerformed

    private void txt_search_karyawan_data_lemburKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_karyawan_data_lemburKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_All();
        }
    }//GEN-LAST:event_txt_search_karyawan_data_lemburKeyPressed

    private void button_search_karyawan_data_lemburActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_karyawan_data_lemburActionPerformed
        // TODO add your handling code here:
        refreshTable_All();
    }//GEN-LAST:event_button_search_karyawan_data_lemburActionPerformed

    private void ComboBox_departemen_data_lemburActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ComboBox_departemen_data_lemburActionPerformed
        // TODO add your handling code here:
        try {
            ComboBox_bagian_data_lembur.removeAllItems();
            String query = "SELECT `nama_bagian` FROM `tb_bagian` ORDER BY `nama_bagian`";
            if (ComboBox_departemen_data_lembur.getSelectedItem() != "All") {
                query = "SELECT `nama_bagian` FROM `tb_bagian` WHERE `kode_departemen`='" + ComboBox_departemen_data_lembur.getSelectedItem() + "'";
            }
            rs = Utility.db.getStatement().executeQuery(query);
            ComboBox_bagian_data_lembur.addItem("All");
            while (rs.next()) {
                ComboBox_bagian_data_lembur.addItem(rs.getString("nama_bagian"));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_DataLembur.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_ComboBox_departemen_data_lemburActionPerformed

    private void button_export_data_lemburActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_data_lemburActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) tabel_data_lembur.getModel();
        ExportToExcel.writeToExcel(model, jPanel1);
    }//GEN-LAST:event_button_export_data_lemburActionPerformed

    private void button_export_lembur_makanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_lembur_makanActionPerformed
        // TODO add your handling code here:table_data_lembur_makan
        DefaultTableModel model = (DefaultTableModel) table_data_lembur_makan.getModel();
        ExportToExcel.writeToExcel(model, jPanel1);
    }//GEN-LAST:event_button_export_lembur_makanActionPerformed

    private void button_print_laporanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_print_laporanActionPerformed
        // TODO add your handling code here:
        try {
            JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Formulir_Karyawan_Lembur.jrxml");
            Map<String, Object> map = new HashMap<>();
            map.put("tgl_lembur_absen", dateFormat.format(Date_lembur_makan.getDate()));//parameter name should be like it was named inside your report.
            JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
            JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, map, Utility.db.getConnection());
            JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
        } catch (JRException ex) {
            Logger.getLogger(JPanel_Data_Karyawan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_print_laporanActionPerformed

    private void button_diketahui_semua_SPL_PEJUANGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_diketahui_semua_SPL_PEJUANGActionPerformed
        // TODO add your handling code here:
        boolean check = true;
        if (!MainForm.Login_Departemen.equals("HRGA")) {
            JOptionPane.showMessageDialog(this, "Harap login menggunakan user HRD !");
            check = false;
        }

        if (check) {
            int dialogResult = JOptionPane.showConfirmDialog(this, "Diketahui Semua?", "Warning", 0);
            if (dialogResult == JOptionPane.YES_OPTION) {
                try {
                    int data_masuk = 0;
                    Utility.db.getConnection().setAutoCommit(false);
                    for (int i = 0; i < Table_SPL_PEJUANG.getRowCount(); i++) {
                        if (Table_SPL_PEJUANG.getValueAt(i, 8) != null//sudah di setujui
                                && Table_SPL_PEJUANG.getValueAt(i, 9) == null) {//belum diketahui hr

                            sql = "UPDATE `tb_surat_lembur` SET "
                                    + "`diketahui`='" + MainForm.Login_NamaPegawai + " " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()) + "' "
                                    + "WHERE `nomor_surat` = '" + Table_SPL_PEJUANG.getValueAt(i, 0).toString() + "'";
                            Utility.db.getStatement().executeUpdate(sql);
                            data_masuk++;
                        }
                    }
                    Utility.db.getConnection().commit();
                    JOptionPane.showMessageDialog(this, data_masuk + " Data berhasil di setujui !");
                    refreshTable_SPL_PEJUANG();
                } catch (SQLException ex) {
                    try {
                        Utility.db.getConnection().rollback();
                    } catch (SQLException ex1) {
                        Logger.getLogger(JPanel_DataLembur.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                    JOptionPane.showMessageDialog(this, ex);
                    Logger.getLogger(JPanel_DataLembur.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    try {
                        Utility.db.getConnection().setAutoCommit(true);
                    } catch (SQLException ex) {
                        Logger.getLogger(JPanel_DataLembur.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }//GEN-LAST:event_button_diketahui_semua_SPL_PEJUANGActionPerformed

    private void button_search_SPL_STAFFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_SPL_STAFFActionPerformed
        // TODO add your handling code here:
        refreshTable_SPL_STAFF();
    }//GEN-LAST:event_button_search_SPL_STAFFActionPerformed

    private void button_export_SPL_STAFFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_SPL_STAFFActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Table_SPL_STAFF.getModel();
        ExportToExcel.writeToExcel(model, jPanel1);
    }//GEN-LAST:event_button_export_SPL_STAFFActionPerformed

    private void button_edit_SPL_STAFFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_edit_SPL_STAFFActionPerformed
        // TODO add your handling code here:
        try {
            int row = Table_SPL_STAFF.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan Pilih data yang akan di ubah");
            } else {
                String no_surat = Table_SPL_STAFF.getValueAt(row, 0).toString();
                JDialog_Add_SuratPerintahLembur Add = new JDialog_Add_SuratPerintahLembur(new javax.swing.JFrame(), true, no_surat, null);
                Add.pack();
                Add.setLocationRelativeTo(this);
                Add.setVisible(true);
                Add.setEnabled(true);
                refreshTable_SPL_STAFF();
            }
        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_DataLembur.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_edit_SPL_STAFFActionPerformed

    private void button_delete_SPL_STAFFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_delete_SPL_STAFFActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_button_delete_SPL_STAFFActionPerformed

    private void button_disetujui_SPL_STAFFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_disetujui_SPL_STAFFActionPerformed
        // TODO add your handling code here:
        int i = Table_SPL_STAFF.getSelectedRow();
        if (i == -1) {
            JOptionPane.showMessageDialog(this, "Silahkan pilih data yang akan di kirim !");
        } else {
            boolean check = true;
            if (!MainForm.Login_Posisi.equals("MANAGER") && !MainForm.Login_Posisi.equals("STAFF 5") && !MainForm.Login_Posisi.equals("STAFF 6")) {
                JOptionPane.showMessageDialog(this, "Hanya STAFF / MANAGER yang bisa menyetujui lembur !");
                check = false;
            } else if (Table_SPL_STAFF.getValueAt(i, 8) != null) {//sudah diketahui hr
                JOptionPane.showMessageDialog(this, "Data lembur sudah disetujui !");
                check = false;
            }
            if (check) {
                try {
                    sql = "UPDATE `tb_surat_lembur` SET "
                            + "`disetujui`='" + MainForm.Login_NamaPegawai + " " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()) + "' "
                            + "WHERE `nomor_surat` = '" + Table_SPL_STAFF.getValueAt(i, 0).toString() + "'";
                    Utility.db.getStatement().executeUpdate(sql);
                    refreshTable_SPL_STAFF();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, ex);
                    Logger.getLogger(JPanel_DataLembur.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_button_disetujui_SPL_STAFFActionPerformed

    private void button_diketahui_SPL_STAFFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_diketahui_SPL_STAFFActionPerformed
        // TODO add your handling code here:
        int i = Table_SPL_STAFF.getSelectedRow();
        if (i == -1) {
            JOptionPane.showMessageDialog(this, "Silahkan pilih data yang akan di kirim !");
        } else {
            boolean check = true;
            if (!MainForm.Login_Departemen.equals("HRGA")) {
                JOptionPane.showMessageDialog(this, "Harap login menggunakan user HRD !");
                check = false;
            } else if (Table_SPL_STAFF.getValueAt(i, 8) == null) {//kalau belum disetujui
                JOptionPane.showMessageDialog(this, "Data lembur belum disetujui !");
                check = false;
            } else if (Table_SPL_STAFF.getValueAt(i, 9) != null) {//sudah diketahui hr
                JOptionPane.showMessageDialog(this, "Data lembur sudah diketahui !");
                check = false;
            }
            if (check) {
                try {
                    sql = "UPDATE `tb_surat_lembur` SET "
                            + "`diketahui`='" + MainForm.Login_NamaPegawai + " " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()) + "' "
                            + "WHERE `nomor_surat` = '" + Table_SPL_STAFF.getValueAt(i, 0).toString() + "'";
                    Utility.db.getStatement().executeUpdate(sql);
                    refreshTable_SPL_PEJUANG();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, ex);
                    Logger.getLogger(JPanel_DataLembur.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_button_diketahui_SPL_STAFFActionPerformed

    private void button_diketahui_semua_SPL_STAFFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_diketahui_semua_SPL_STAFFActionPerformed
        // TODO add your handling code here:
        boolean check = true;
        if (!MainForm.Login_Departemen.equals("HRGA")) {
            JOptionPane.showMessageDialog(this, "Harap login menggunakan user HRD !");
            check = false;
        }

        if (check) {
            int dialogResult = JOptionPane.showConfirmDialog(this, "Diketahui Semua?", "Warning", 0);
            if (dialogResult == JOptionPane.YES_OPTION) {
                try {
                    int data_masuk = 0;
                    Utility.db.getConnection().setAutoCommit(false);
                    for (int i = 0; i < Table_SPL_STAFF.getRowCount(); i++) {
                        if (Table_SPL_STAFF.getValueAt(i, 8) != null//sudah di setujui
                                && Table_SPL_STAFF.getValueAt(i, 9) == null) {//belum diketahui hr

                            sql = "UPDATE `tb_surat_lembur` SET "
                                    + "`diketahui`='" + MainForm.Login_NamaPegawai + " " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()) + "' "
                                    + "WHERE `nomor_surat` = '" + Table_SPL_STAFF.getValueAt(i, 0).toString() + "'";
                            Utility.db.getStatement().executeUpdate(sql);
                            data_masuk++;
                        }
                    }
                    Utility.db.getConnection().commit();
                    JOptionPane.showMessageDialog(this, data_masuk + " Data berhasil di setujui !");
                    refreshTable_SPL_STAFF();
                } catch (SQLException ex) {
                    try {
                        Utility.db.getConnection().rollback();
                    } catch (SQLException ex1) {
                        Logger.getLogger(JPanel_DataLembur.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                    JOptionPane.showMessageDialog(this, ex);
                    Logger.getLogger(JPanel_DataLembur.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    try {
                        Utility.db.getConnection().setAutoCommit(true);
                    } catch (SQLException ex) {
                        Logger.getLogger(JPanel_DataLembur.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }//GEN-LAST:event_button_diketahui_semua_SPL_STAFFActionPerformed

    private void button_buat_SPL_STAFFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_buat_SPL_STAFFActionPerformed
        // TODO add your handling code here:
        JDialog_Add_SuratPerintahLembur Add = new JDialog_Add_SuratPerintahLembur(new javax.swing.JFrame(), true, null, "STAFF");
        Add.pack();
        Add.setLocationRelativeTo(this);
        Add.setVisible(true);
        Add.setEnabled(true);
        refreshTable_SPL_STAFF();
    }//GEN-LAST:event_button_buat_SPL_STAFFActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_bagian_data_lembur;
    private javax.swing.JComboBox<String> ComboBox_bagian_rekap;
    private javax.swing.JComboBox<String> ComboBox_departemen_SPL_PEJUANG;
    private javax.swing.JComboBox<String> ComboBox_departemen_SPL_STAFF;
    private javax.swing.JComboBox<String> ComboBox_departemen_data_lembur;
    private javax.swing.JComboBox<String> ComboBox_departemen_rekap;
    private javax.swing.JComboBox<String> ComboBox_jenis_lembur_data_lembur;
    private javax.swing.JComboBox<String> ComboBox_kelamin_data_lembur;
    private javax.swing.JComboBox<String> ComboBox_posisi_data_lembur;
    private javax.swing.JComboBox<String> ComboBox_status_karyawan_data_lembur;
    private com.toedter.calendar.JDateChooser Date_SPL_PEJUANG1;
    private com.toedter.calendar.JDateChooser Date_SPL_PEJUANG2;
    private com.toedter.calendar.JDateChooser Date_SPL_STAFF1;
    private com.toedter.calendar.JDateChooser Date_SPL_STAFF2;
    private com.toedter.calendar.JDateChooser Date_data_lembur1;
    private com.toedter.calendar.JDateChooser Date_data_lembur2;
    private com.toedter.calendar.JDateChooser Date_lembur_makan;
    private com.toedter.calendar.JDateChooser Date_lembur_rekap1;
    private com.toedter.calendar.JDateChooser Date_lembur_rekap2;
    public static javax.swing.JTable Table_SPL_PEJUANG;
    public static javax.swing.JTable Table_SPL_STAFF;
    private javax.swing.JButton button_buat_SPL_PEJUANG;
    private javax.swing.JButton button_buat_SPL_STAFF;
    private javax.swing.JButton button_delete_SPL_PEJUANG;
    private javax.swing.JButton button_delete_SPL_STAFF;
    private javax.swing.JButton button_diketahui_SPL_PEJUANG;
    private javax.swing.JButton button_diketahui_SPL_STAFF;
    private javax.swing.JButton button_diketahui_semua_SPL_PEJUANG;
    private javax.swing.JButton button_diketahui_semua_SPL_STAFF;
    private javax.swing.JButton button_disetujui_SPL_PEJUANG;
    private javax.swing.JButton button_disetujui_SPL_STAFF;
    private javax.swing.JButton button_edit_SPL_PEJUANG;
    private javax.swing.JButton button_edit_SPL_STAFF;
    private javax.swing.JButton button_export_SPL_PEJUANG;
    private javax.swing.JButton button_export_SPL_STAFF;
    private javax.swing.JButton button_export_data_lembur;
    private javax.swing.JButton button_export_lembur_makan;
    private javax.swing.JButton button_export_rekap;
    private javax.swing.JButton button_print_laporan;
    private javax.swing.JButton button_search1;
    private javax.swing.JButton button_search_SPL_PEJUANG;
    private javax.swing.JButton button_search_SPL_STAFF;
    public static javax.swing.JButton button_search_karyawan_data_lembur;
    public static javax.swing.JButton button_search_rekap;
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
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel_SPL_pejuang;
    private javax.swing.JPanel jPanel_SPL_staff;
    private javax.swing.JPanel jPanel_data_lembur;
    private javax.swing.JPanel jPanel_data_lembur_makan;
    private javax.swing.JPanel jPanel_rekap_per_karyawan;
    private javax.swing.JPanel jPanel_search_karyawan2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel label_total_SPL;
    private javax.swing.JLabel label_total_SPL_PEJUANG;
    private javax.swing.JLabel label_total_SPL_STAFF;
    private javax.swing.JLabel label_total_data_lembur;
    private javax.swing.JLabel label_total_jam;
    private javax.swing.JLabel label_total_karyawan_SPL_PEJUANG;
    private javax.swing.JLabel label_total_karyawan_SPL_STAFF;
    private javax.swing.JLabel label_total_reproses;
    private javax.swing.JTable tabel_data_lembur;
    private javax.swing.JTable tabel_detail_lembur_rekap;
    private javax.swing.JTable tabel_pegawai_lembur_SPL_PEJUANG;
    private javax.swing.JTable tabel_pegawai_lembur_SPL_STAFF;
    private javax.swing.JTable tabel_rekap_pegawai;
    private javax.swing.JTable table_data_lembur_makan;
    private javax.swing.JTextField txt_search_karyawan_data_lembur;
    private javax.swing.JTextField txt_search_karyawan_rekap;
    private javax.swing.JTextField txt_search_nama;
    // End of variables declaration//GEN-END:variables

}
