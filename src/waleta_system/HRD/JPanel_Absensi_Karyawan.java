package waleta_system.HRD;

import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.ExportToExcel;
import waleta_system.Class.Utility;
import waleta_system.MainForm;
import waleta_system.Rekap_Model;

public class JPanel_Absensi_Karyawan extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    PreparedStatement pst;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Date today = new Date();
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    String lihat_absen_staf = "";

    public JPanel_Absensi_Karyawan() {
        initComponents();
    }

    public void init() {
        try {
            try {
                lihat_absen_staf = " AND (`posisi` NOT LIKE 'STAFF%' OR `tb_karyawan`.`id_pegawai` = '" + MainForm.Login_idPegawai + "') ";
                if (MainForm.Login_idPegawai.equals("20171201644")//indri
                        || MainForm.Login_idPegawai.equals("20230907768")//diyan
                        || MainForm.Login_idPegawai.equals("20180102221")//bastian
                        || MainForm.Login_idPegawai.equals("20170100225")//priska07
                        || MainForm.Login_idPegawai.equals("2")//johan.hartono07
                        || MainForm.Login_idPegawai.equals("20170300469")//Martin
                        || MainForm.Login_idPegawai.equals("20211006504")//veny019
                        || MainForm.Login_idPegawai.equals("20230607648")//novista
                        ) {
                    lihat_absen_staf = "";
                }

                ComboBox_departemen_karyawan1.removeAllItems();
                ComboBox_departemen_karyawan1.addItem("All");
                sql = "SELECT `kode_dep` FROM `tb_departemen` ORDER BY `kode_dep`";
                rs = Utility.db.getStatement().executeQuery(sql);
                while (rs.next()) {
                    ComboBox_departemen_karyawan1.addItem(rs.getString("kode_dep"));
                }

                ComboBox_posisi.removeAllItems();
                ComboBox_posisi1.removeAllItems();
                ComboBox_posisi2.removeAllItems();
                ComboBox_posisi.addItem("All");
                ComboBox_posisi1.addItem("All");
                ComboBox_posisi2.addItem("All");
                sql = "SELECT DISTINCT(`posisi`) AS 'posisi' FROM `tb_karyawan` WHERE `status` = 'IN' AND `posisi` IS NOT NULL";
                rs = Utility.db.getStatement().executeQuery(sql);
                while (rs.next()) {
                    ComboBox_posisi.addItem(rs.getString("posisi"));
                    ComboBox_posisi1.addItem(rs.getString("posisi"));
                    ComboBox_posisi2.addItem(rs.getString("posisi"));
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
                Logger.getLogger(JPanel_Absensi_Karyawan.class.getName()).log(Level.SEVERE, null, e);
            }
//            tabel_data_absen.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
//                @Override
//                public void valueChanged(ListSelectionEvent event) {
//                    if (!event.getValueIsAdjusting() && tabel_data_absen.getSelectedRow() != -1) {
//                        int i = tabel_data_absen.getSelectedRow();
//                    }
//                }
//            });

            refreshTable_absen();
            refreshTable_Lembur();
        } catch (Exception ex) {
            Logger.getLogger(JPanel_Absensi_Karyawan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_absen() {
        try {
            DefaultTableModel model = (DefaultTableModel) tabel_data_absen.getModel();
            model.setRowCount(0);

            String nama_pegawai = "";
            if (!txt_search_NamaKaryawan.getText().equals("")) {
                nama_pegawai = "AND `tb_karyawan`.`nama_pegawai` LIKE '%" + txt_search_NamaKaryawan.getText() + "%'\n";
            }
            String bagian = "AND `tb_bagian`.`nama_bagian` LIKE '%" + txt_search_bagian.getText() + "%'\n";
            if (txt_search_bagian.getText() == null || txt_search_bagian.getText().equals("")) {
                bagian = "";
            }
            String filter_mesin = "AND `att_mesin_finger`.`nama_mesin` LIKE '%" + txt_search_mesin_absen.getText() + "%' \n";
            if (txt_search_mesin_absen.getText() == null || txt_search_mesin_absen.getText().equals("")) {
                filter_mesin = "";
            }
            String posisi = "AND `posisi` = '" + ComboBox_posisi.getSelectedItem().toString() + "' \n";
            if ("All".equals(ComboBox_posisi.getSelectedItem().toString())) {
                posisi = "";
            }
            String filter_tanggal = "";
            if (Date_Search1.getDate() != null && Date_Search2.getDate() != null) {
                filter_tanggal = " AND (DATE(`scan_date`) BETWEEN '" + dateFormat.format(Date_Search1.getDate()) + "' AND '" + dateFormat.format(Date_Search2.getDate()) + "') \n";
            }
            sql = "SELECT `att_log`.`pin`, `tb_karyawan`.`id_pegawai`, `tb_karyawan`.`nama_pegawai`,`tb_bagian`.`nama_bagian`, `tb_bagian`.`kode_departemen`, `posisi`, DATE(`scan_date`) AS 'tanggal', TIME(`scan_date`) AS 'waktu',`att_mesin_finger`.`nama_mesin`, `verifymode`, `tb_surat_lembur`.`nomor_surat`, `jumlah_jam`, `tanggal_masuk`, `tb_surat_lembur_detail`.`mulai_lembur`\n"
                    + "FROM `att_log` "
                    + "LEFT JOIN `att_mesin_finger` ON `att_log`.`sn` = `att_mesin_finger`.`sn`\n"
                    + "LEFT JOIN `tb_karyawan` ON `att_log`.`pin` = `tb_karyawan`.`pin_finger`\n"
                    + "LEFT JOIN `tb_surat_lembur_detail` ON `tb_surat_lembur_detail`.`id_pegawai` = `tb_karyawan`.`id_pegawai` AND DATE(`att_log`.`scan_date`) = `tb_surat_lembur_detail`.`tanggal_lembur`\n"
                    + "LEFT JOIN `tb_surat_lembur` ON `tb_surat_lembur_detail`.`nomor_surat` = `tb_surat_lembur`.`nomor_surat`\n"
                    + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`"
                    + "WHERE `pin` LIKE '%" + txt_search_pin.getText() + "%' "
                    + nama_pegawai
                    + lihat_absen_staf
                    + bagian + filter_mesin + posisi
                    + filter_tanggal
                    + " ORDER BY `tb_karyawan`.`nama_pegawai` ASC, `scan_date` ASC";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[13];
            while (rs.next()) {
                row[0] = rs.getString("pin");
                row[1] = rs.getString("id_pegawai");
                row[2] = rs.getString("nama_pegawai");
                row[3] = rs.getString("nama_bagian");
                row[4] = rs.getString("kode_departemen");
                row[5] = rs.getString("posisi");
                row[6] = rs.getDate("tanggal");
                row[7] = new SimpleDateFormat("HH:mm").format(rs.getTime("waktu"));
                row[8] = rs.getString("nama_mesin");
                switch (rs.getInt("verifymode")) {
                    case 1:
                        row[9] = "Fingerprint Scan";
                        break;
                    case 20:
                        row[9] = "Face Scan";
                        break;
                    default:
                        row[9] = "Unidentified";
                        break;
                }
                row[10] = rs.getString("nomor_surat");
                row[11] = rs.getInt("jumlah_jam");
                row[12] = rs.getString("mulai_lembur");

                if (CheckBox_hanya_absen_mesin.isSelected()) {
                    if (rs.getInt("verifymode") > 0) {
                        model.addRow(row);
                    }
                } else {
                    model.addRow(row);
                }
            }
            label_total_data_absen.setText(Integer.toString(tabel_data_absen.getRowCount()));
            ColumnsAutoSizer.sizeColumnsToFit(tabel_data_absen);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_Absensi_Karyawan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_Lembur() {
        try {
            DefaultTableModel model = (DefaultTableModel) tabel_data_lembur.getModel();
            model.setRowCount(0);
            String bagian = "AND `tb_bagian`.`nama_bagian` = '" + ComboBox_bagian_karyawan1.getSelectedItem().toString() + "' ";
            String departemen = "AND `tb_bagian`.`kode_departemen` = '" + ComboBox_departemen_karyawan1.getSelectedItem().toString() + "' ";
            if ("All".equals(ComboBox_bagian_karyawan1.getSelectedItem().toString())) {
                bagian = "";
            }
            if ("All".equals(ComboBox_departemen_karyawan1.getSelectedItem().toString())) {
                departemen = "";
            }
            String posisi = "AND `posisi` = '" + ComboBox_posisi1.getSelectedItem().toString() + "' ";
            if ("All".equals(ComboBox_posisi1.getSelectedItem().toString())) {
                posisi = "";
            }
            if (Date_Search_Lembur1.getDate() != null && Date_Search_Lembur2.getDate() != null) {
                sql = "SELECT DAYNAME(DATE(`scan_date`)) AS 'Hari',DATE(`scan_date`) AS 'tanggal', `att_log`.`pin`, `tb_karyawan`.`id_pegawai`, `tb_karyawan`.`nama_pegawai`, `tb_karyawan`.`posisi`, `tb_karyawan`.`status`,`tb_bagian`.`nama_bagian`, `tb_bagian`.`kode_departemen`, MIN(TIME(`scan_date`)) AS 'Masuk', MAX(TIME(`scan_date`)) AS 'Pulang', `att_mesin_finger`.`nama_mesin`, `verifymode`, `tb_surat_lembur`.`nomor_surat`, `jumlah_jam`, `mulai_lembur`\n"
                        + "FROM `att_log` LEFT JOIN `att_mesin_finger` ON `att_log`.`sn` = `att_mesin_finger`.`sn`\n"
                        + "LEFT JOIN `tb_karyawan` ON `att_log`.`pin` = `tb_karyawan`.`pin_finger`\n"
                        + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`"
                        + "LEFT JOIN `tb_surat_lembur_detail` ON `tb_surat_lembur_detail`.`id_pegawai` = `tb_karyawan`.`id_pegawai` AND DATE(`att_log`.`scan_date`) = `tb_surat_lembur_detail`.`tanggal_lembur`\n"
                        + "LEFT JOIN `tb_surat_lembur` ON `tb_surat_lembur_detail`.`nomor_surat` = `tb_surat_lembur`.`nomor_surat`\n"
                        + "WHERE `pin` LIKE '%" + txt_search_pin1.getText() + "%' AND `tb_karyawan`.`nama_pegawai` LIKE '%" + txt_search_NamaKaryawan1.getText() + "%' " + bagian + departemen + lihat_absen_staf + posisi + " AND (DATE(`scan_date`) BETWEEN '" + dateFormat.format(Date_Search_Lembur1.getDate()) + "' AND '" + dateFormat.format(Date_Search_Lembur2.getDate()) + "')"
                        + " GROUP BY `att_log`.`pin`, DATE(`scan_date`) "
                        + "ORDER BY `tb_karyawan`.`nama_pegawai` ASC, `scan_date` ASC";
            } else {
                sql = "SELECT DAYNAME(DATE(`scan_date`)) AS 'Hari',DATE(`scan_date`) AS 'tanggal', `att_log`.`pin`, `tb_karyawan`.`id_pegawai`, `tb_karyawan`.`nama_pegawai`, `tb_karyawan`.`posisi`, `tb_karyawan`.`status`, `tb_bagian`.`nama_bagian`, `tb_bagian`.`kode_departemen`, MIN(TIME(`scan_date`)) AS 'Masuk', MAX(TIME(`scan_date`)) AS 'Pulang', `att_mesin_finger`.`nama_mesin`, `verifymode`, `tb_surat_lembur`.`nomor_surat`, `jumlah_jam`, `mulai_lembur`\n"
                        + "FROM `att_log` LEFT JOIN `att_mesin_finger` ON `att_log`.`sn` = `att_mesin_finger`.`sn`\n"
                        + "LEFT JOIN `tb_karyawan` ON `att_log`.`pin` = `tb_karyawan`.`pin_finger`\n"
                        + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`"
                        + "LEFT JOIN `tb_surat_lembur_detail` ON `tb_surat_lembur_detail`.`id_pegawai` = `tb_karyawan`.`id_pegawai` AND DATE(`att_log`.`scan_date`) = `tb_surat_lembur_detail`.`tanggal_lembur`\n"
                        + "LEFT JOIN `tb_surat_lembur` ON `tb_surat_lembur_detail`.`nomor_surat` = `tb_surat_lembur`.`nomor_surat`\n"
                        + "WHERE `pin` LIKE '%" + txt_search_pin1.getText() + "%' AND `tb_karyawan`.`nama_pegawai` LIKE '%" + txt_search_NamaKaryawan1.getText() + "%' " + bagian + departemen + lihat_absen_staf + posisi
                        + " GROUP BY `att_log`.`pin`, DATE(`scan_date`)"
                        + "ORDER BY `tb_karyawan`.`nama_pegawai` ASC, `scan_date` ASC";
            }
            pst = Utility.db.getConnection().prepareStatement(sql);
            rs = pst.executeQuery();
            Object[] row = new Object[20];
            while (rs.next()) {
                row[0] = rs.getString("Hari");
                row[1] = rs.getDate("tanggal");
                row[2] = rs.getString("pin");
                row[3] = rs.getString("id_pegawai");
                row[4] = rs.getString("nama_pegawai");
                row[5] = rs.getString("nama_bagian");
                row[6] = rs.getString("kode_departemen");
                row[7] = rs.getString("posisi");
                row[8] = rs.getString("status");
                row[9] = rs.getString("Masuk");
                row[10] = rs.getString("Pulang");
                row[11] = rs.getString("nama_mesin");
                SimpleDateFormat Timeformat = new SimpleDateFormat("HH:mm:ss");
                Date masuk = Timeformat.parse(rs.getString("Masuk"));
                Date pulang = Timeformat.parse(rs.getString("Pulang"));
                long lembur = pulang.getTime() - masuk.getTime();
                if (null == rs.getString("Hari")) {
                    lembur = lembur - (9 * 60 * 60 * 1000);
                } else {
                    switch (rs.getString("Hari")) {
                        case "Saturday":
                            if (!rs.getString("posisi").equals("STAFF 5")) {
                                lembur = lembur - (5 * 60 * 60 * 1000);
                            }
                            break;
                        case "Sunday":
                            break;
                        default://Senin - jumat
                            if (!rs.getString("posisi").equals("STAFF 5")) {
                                lembur = lembur - (8 * 60 * 60 * 1000);
                            } else {
                                lembur = lembur - (9 * 60 * 60 * 1000);
                            }
                            break;
                    }
                }
                row[12] = lembur / (60 * 60 * 1000) % 24;
                row[13] = lembur / (60 * 1000) % 60;
                row[14] = rs.getString("nomor_surat");
                row[15] = rs.getInt("jumlah_jam");
                row[16] = rs.getString("mulai_lembur");

                String query_get_ijin = "SELECT `id_pegawai`, `tanggal_keluar`, `jam_keluar`, `jam_kembali` FROM `tb_ijin_keluar` "
                        + "WHERE `jam_keluar` IS NOT NULL AND `jam_kembali` IS NOT NULL AND `id_pegawai` = '" + rs.getString("id_pegawai") + "' AND  `tanggal_keluar` = '" + dateFormat.format(rs.getDate("tanggal")) + "'";
                ResultSet result_ijin = Utility.db.getStatement().executeQuery(query_get_ijin);
                if (result_ijin.next()) {
                    Date ijin_keluar = Timeformat.parse(result_ijin.getString("jam_keluar"));
                    Date ijin_masuk = Timeformat.parse(result_ijin.getString("jam_kembali"));
                    long ijin = ijin_masuk.getTime() - ijin_keluar.getTime();
                    row[17] = ijin / (60 * 1000);
                    row[18] = (lembur - ijin) / (60 * 60 * 1000) % 24;
                    row[19] = (lembur - ijin) / (60 * 1000) % 60;
                } else {
                    row[17] = null;
                    row[18] = null;
                    row[19] = null;
                }
                model.addRow(row);
            }
            label_total_data_absen1.setText(Integer.toString(tabel_data_lembur.getRowCount()));
            ColumnsAutoSizer.sizeColumnsToFit(tabel_data_lembur);
        } catch (SQLException | ParseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_Absensi_Karyawan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_TidakMasuk() {
        try {
            int jumlah_tidak_masuk = 0, jumlah_masuk = 0;
            Connection con = Utility.db.getConnection();
            DefaultTableModel model = (DefaultTableModel) tabel_data_TidakMasuk.getModel();
            model.setRowCount(0);
            String bagian = "AND `tb_bagian`.`nama_bagian` LIKE '%" + txt_search_bagian_data_tidak_masuk.getText() + "%' ";
            if (txt_search_bagian_data_tidak_masuk.getText() == null || txt_search_bagian_data_tidak_masuk.getText().equals("")) {
                bagian = "";
            }
            String filter_posisi = "AND `posisi` = '" + ComboBox_posisi2.getSelectedItem().toString() + "' ";
            if ("All".equals(ComboBox_posisi2.getSelectedItem().toString())) {
                filter_posisi = "";
            }
            String jam1 = String.format("%02d", Spinner_jam1.getValue()) + ":" + String.format("%02d", Spinner_menit1.getValue()) + ":00";
            String jam2 = String.format("%02d", Spinner_jam2.getValue()) + ":" + String.format("%02d", Spinner_menit2.getValue()) + ":00";

            if (Date_TidakMasuk1.getDate() != null && Date_TidakMasuk2.getDate() != null) {
                Date date = Date_TidakMasuk1.getDate();
                while (date.before(new Date(Date_TidakMasuk2.getDate().getTime() + (1 * 24 * 60 * 60 * 1000)))) {
                    sql = "SELECT `id_pegawai`, `pin_finger`, `nik_ktp`, `nama_pegawai`, `tb_bagian`.`nama_bagian`, `tb_bagian`.`kode_departemen`, `posisi`, `tanggal_masuk`, `jam_kerja`"
                            + "FROM `tb_karyawan` "
                            + "LEFT JOIN `tb_bagian` ON `tb_bagian`.`kode_bagian` = `tb_karyawan`.`kode_bagian` "
                            + "WHERE `tanggal_masuk` <= '" + dateFormat.format(date) + "'"
                            + "AND `status` = 'IN' \n"
                            + "AND `pin_finger` LIKE '%" + txt_search_pin2.getText() + "%'\n"
                            + "AND `nama_pegawai` LIKE '%" + txt_search_NamaKaryawan2.getText() + "%'\n"
                            + bagian
                            + filter_posisi
                            + lihat_absen_staf;
//                    System.out.println(sql);
                    pst = con.prepareStatement(sql);
                    rs = pst.executeQuery();
                    Object[] row = new Object[15];
                    while (rs.next()) {
                        row[0] = dateFormat.format(date);
                        row[1] = rs.getString("id_pegawai");
                        row[2] = rs.getString("pin_finger");
                        row[3] = rs.getString("nama_pegawai");
                        row[4] = rs.getString("nama_bagian");
                        row[5] = rs.getString("kode_departemen");
                        row[6] = rs.getString("posisi");

                        String b = "SELECT DATE(`scan_date`) AS 'tanggal', TIME(`scan_date`) AS 'jam' FROM `att_log` "
                                + "WHERE `pin` = '" + rs.getString("pin_finger") + "' "
                                + "AND DATE(`scan_date`) = '" + dateFormat.format(date) + "' "
                                + "AND TIME(`scan_date`) BETWEEN '" + jam1 + "' AND '" + jam2 + "' "
                                + "ORDER BY `scan_date`";
                        PreparedStatement pst1 = con.prepareStatement(b);
                        ResultSet rs1 = pst1.executeQuery();
                        if (rs1.next()) {
                            row[7] = rs1.getDate("tanggal");
                            row[8] = rs1.getTime("jam");
                            jumlah_masuk++;
                        } else {
                            row[7] = "Tidak ada Data";
                            row[8] = "Tidak ada Data";
                            jumlah_tidak_masuk++;
                        }
                        row[9] = rs.getDate("tanggal_masuk");
                        row[10] = rs.getString("jam_kerja");
                        row[11] = "-";
                        String c = "SELECT * FROM `tb_jam_kerja` WHERE `jam_kerja` = '" + rs.getString("jam_kerja") + "' ";
                        PreparedStatement pst2 = con.prepareStatement(c);
                        ResultSet rs2 = pst2.executeQuery();
                        if (rs2.next()) {
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(date);
                            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1; //1=Sunday, 2=Monday, 3=Tuesday, 4=Wednesday, 5=Thursday, 6=Friday, 7=Saturday.
                            if (dayOfWeek == 0) {
                                dayOfWeek = 7;
                            }
                            if (rs2.getDate("masuk" + dayOfWeek) == null) {
                                row[11] = "Hari Libur";
                            } else {
                                row[11] = "Hari Kerja";
                                String posisi = rs.getString("posisi");
                                String query_hari_libur = "SELECT * FROM `tb_libur` WHERE `tanggal_libur` = '" + dateFormat.format(date) + "' ";
                                PreparedStatement pst_hari_libur = con.prepareStatement(query_hari_libur);
                                ResultSet rs_hari_libur = pst_hari_libur.executeQuery();
                                if (rs_hari_libur.next()) {
                                    if (rs_hari_libur.getString("keterangan").toUpperCase().contains("CUTI BERSAMA")) {
                                        row[11] = "Cuti Bersama";
//                                        if (posisi.toUpperCase().contains("STAFF")) {
//                                            row[11] = "Hari Kerja";
//                                        } else {
//                                            row[11] = "Cuti Bersama";
//                                        }
                                    } else {
                                        row[11] = "Tanggal Merah";
                                    }
                                }
                            }
                        }

                        switch (ComboBox_filter_absen.getSelectedIndex()) {
                            case 0:
                                switch (ComboBox_filter_jenis_hari.getSelectedIndex()) {
                                    case 0:
                                        model.addRow(row);
                                        break;
                                    case 1:
                                        if (row[11] == "Hari Kerja") {
                                            model.addRow(row);
                                        }
                                        break;
                                    case 2:
                                        if (row[11] == "Hari Libur") {
                                            model.addRow(row);
                                        }
                                        break;
                                    case 3:
                                        if (row[11] == "Cuti Bersama") {
                                            model.addRow(row);
                                        }
                                        break;
                                    case 4:
                                        if (row[11] == "Tanggal Merah") {
                                            model.addRow(row);
                                        }
                                        break;
                                    default:
                                        break;
                                }
                                break;
                            case 1:
                                if (row[8] != "Tidak ada Data") {
                                    switch (ComboBox_filter_jenis_hari.getSelectedIndex()) {
                                        case 0:
                                            model.addRow(row);
                                            break;
                                        case 1:
                                            if (row[11] == "Hari Kerja") {
                                                model.addRow(row);
                                            }
                                            break;
                                        case 2:
                                            if (row[11] == "Hari Libur") {
                                                model.addRow(row);
                                            }
                                            break;
                                        case 3:
                                            if (row[11] == "Cuti Bersama") {
                                                model.addRow(row);
                                            }
                                            break;
                                        case 4:
                                            if (row[11] == "Tanggal Merah") {
                                                model.addRow(row);
                                            }
                                            break;
                                        default:
                                            break;
                                    }
                                }
                                break;
                            case 2:
                                if (row[8] == "Tidak ada Data") {
                                    switch (ComboBox_filter_jenis_hari.getSelectedIndex()) {
                                        case 0:
                                            model.addRow(row);
                                            break;
                                        case 1:
                                            if (row[11] == "Hari Kerja") {
                                                model.addRow(row);
                                            }
                                            break;
                                        case 2:
                                            if (row[11] == "Hari Libur") {
                                                model.addRow(row);
                                            }
                                            break;
                                        case 3:
                                            if (row[11] == "Cuti Bersama") {
                                                model.addRow(row);
                                            }
                                            break;
                                        case 4:
                                            if (row[11] == "Tanggal Merah") {
                                                model.addRow(row);
                                            }
                                            break;
                                        default:
                                            break;
                                    }
                                }
                                break;
                            default:
                                break;
                        }
                    }
                    date = new Date(date.getTime() + (1 * 24 * 60 * 60 * 1000));
                }
            } else {
                JOptionPane.showMessageDialog(this, "Tanggal tidak boleh kosong !");
            }
            label_total_data.setText(Integer.toString(tabel_data_TidakMasuk.getRowCount()));
            label_total_tidak_masuk.setText(Integer.toString(jumlah_tidak_masuk));
            label_total_masuk.setText(Integer.toString(jumlah_masuk));
            ColumnsAutoSizer.sizeColumnsToFit(tabel_data_TidakMasuk);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_Absensi_Karyawan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void old_rekap_tidak_masuk() {
        int jumlah = 0;
        for (int i = 0; i < tabel_data_TidakMasuk.getRowCount(); i++) {
            if (tabel_data_TidakMasuk.getValueAt(i, 8).toString().equals("Tidak ada Data")) {
                jumlah++;
            }
        }
        int dialogResult = JOptionPane.showConfirmDialog(this, "Rekap " + jumlah + " data tidak masuk?", "Warning", 0);
        if (dialogResult == JOptionPane.YES_OPTION) {
            try {
                int count = 0;
                Connection con = Utility.db.getConnection();
                String bagian = "AND `tb_bagian`.`nama_bagian` LIKE '%" + txt_search_bagian_data_tidak_masuk.getText() + "%' ";
                if (txt_search_bagian_data_tidak_masuk.getText() == null || txt_search_bagian_data_tidak_masuk.getText().equals("")) {
                    bagian = "";
                }
                String posisi = "AND `posisi` = '" + ComboBox_posisi2.getSelectedItem().toString() + "' ";
                if ("All".equals(ComboBox_posisi2.getSelectedItem().toString())) {
                    posisi = "";
                }
                String jam1 = Spinner_jam1.getValue().toString() + ":" + Spinner_menit1.getValue().toString();
                String jam2 = Spinner_jam2.getValue().toString() + ":" + Spinner_menit2.getValue().toString();

                if (Date_TidakMasuk1.getDate() != null && Date_TidakMasuk2.getDate() != null) {

                    Date date = Date_TidakMasuk1.getDate();
                    while (date.before(Date_TidakMasuk2.getDate())) {
                        sql = "SELECT `id_pegawai`, `pin_finger`, `tanggal_masuk`"
                                + "FROM `tb_karyawan` "
                                + "LEFT JOIN `tb_bagian` ON `tb_bagian`.`kode_bagian` = `tb_karyawan`.`kode_bagian` "
                                + "WHERE `tanggal_masuk` <= '" + dateFormat.format(date) + "'"
                                + "AND `status` = 'IN' \n" + lihat_absen_staf
                                + "AND `pin_finger` LIKE '%" + txt_search_pin2.getText() + "%'\n"
                                + "AND `nama_pegawai` LIKE '%" + txt_search_NamaKaryawan2.getText() + "%'\n"
                                + bagian
                                + posisi;
//                    System.out.println(sql);
                        pst = con.prepareStatement(sql);
                        rs = pst.executeQuery();
                        while (rs.next()) {
                            String b = "SELECT DATE(`scan_date`) AS 'tanggal', TIME(`scan_date`) AS 'jam' FROM `att_log` "
                                    + "WHERE `pin` = '" + rs.getString("pin_finger") + "' "
                                    + "AND DATE(`scan_date`) = '" + dateFormat.format(date) + "' "
                                    + "AND HOUR(`scan_date`) BETWEEN '" + jam1 + "' AND '" + jam2 + "' "
                                    + "ORDER BY `scan_date`";
                            PreparedStatement pst1 = con.prepareStatement(b);
                            ResultSet rs1 = pst1.executeQuery();
                            if (!rs1.next()) {
//                                String Query = "INSERT INTO `tb_cuti`(`id_pegawai`, `tanggal_cuti`, `tanggal_masuk`, `kategori_cuti`, `keterangan`) "
//                                        + "VALUES ('" + rs.getString("id_pegawai") + "','" + dateFormat.format(date) + "',NULL,'Tanpa Keterangan','','')";
                                String Query = "INSERT INTO `tb_cuti`(`id_pegawai`, `tanggal_cuti`, `tanggal_masuk`, `kategori_cuti`, `keterangan`) "
                                        + "SELECT * FROM (SELECT '" + rs.getString("id_pegawai") + "','" + dateFormat.format(date) + "',NULL,'Tanpa Keterangan','' AS 'keterangan')AS tmp\n"
                                        + "WHERE NOT EXISTS (SELECT `kode_cuti` FROM `tb_cuti` WHERE `id_pegawai` = '" + rs.getString("id_pegawai") + "' AND `tanggal_cuti` = '" + dateFormat.format(date) + "')";
                                Utility.db.getConnection().createStatement();
                                if (Utility.db.getStatement().executeUpdate(Query) == 1) {
                                    count++;
                                }
                            }
                        }
                        date = new Date(date.getTime() + (1 * 24 * 60 * 60 * 1000));
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Tanggal tidak boleh kosong !");
                }
                JOptionPane.showMessageDialog(this, count + " Saved !");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
                Logger.getLogger(JPanel_Absensi_Karyawan.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void new_rekap_tidak_masuk() {
        int jumlah = 0;
        for (int i = 0; i < tabel_data_TidakMasuk.getRowCount(); i++) {
            System.out.println(tabel_data_TidakMasuk.getValueAt(i, 6).toString());
            System.out.println(tabel_data_TidakMasuk.getValueAt(i, 8).toString());
            System.out.println(tabel_data_TidakMasuk.getValueAt(i, 11).toString());
            if (tabel_data_TidakMasuk.getValueAt(i, 8) != null && tabel_data_TidakMasuk.getValueAt(i, 8).toString().equals("Tidak ada Data")) {
                if (tabel_data_TidakMasuk.getValueAt(i, 11).toString().equals("Hari Kerja")) {
                    jumlah++;
                } else if (tabel_data_TidakMasuk.getValueAt(i, 11).toString().equals("Cuti Bersama")
                        && tabel_data_TidakMasuk.getValueAt(i, 6).toString().toLowerCase().contains("staff")) {
                    jumlah++;
                }
            }
        }
        int dialogResult = JOptionPane.showConfirmDialog(this, "Simpan " + jumlah + " data tidak masuk?\nNote:Jika tidak ada data jam kerja, maka data tidak ikut tersimpan!", "Warning", 0);
        if (dialogResult == JOptionPane.YES_OPTION) {
            try {
                int count = 0;
                if (Date_TidakMasuk1.getDate() != null && Date_TidakMasuk2.getDate() != null) {
                    for (int i = 0; i < tabel_data_TidakMasuk.getRowCount(); i++) {
                        if (tabel_data_TidakMasuk.getValueAt(i, 8) != null && tabel_data_TidakMasuk.getValueAt(i, 8).toString().equals("Tidak ada Data")) {
                            if (tabel_data_TidakMasuk.getValueAt(i, 11).toString().equals("Hari Kerja")) {
                                String tanggal = tabel_data_TidakMasuk.getValueAt(i, 0).toString();
                                String id_pegawai = tabel_data_TidakMasuk.getValueAt(i, 1).toString();
                                String Query = "INSERT INTO `tb_cuti`(`id_pegawai`, `tanggal_cuti`, `jenis_cuti`, `kategori_cuti`, `keterangan`) "
                                        + "SELECT * FROM (SELECT '" + id_pegawai + "','" + tanggal + "', 'Absen', 'Tanpa Keterangan','-' AS 'keterangan')AS tmp\n"
                                        + "WHERE NOT EXISTS (SELECT `kode_cuti` FROM `tb_cuti` WHERE `id_pegawai` = '" + id_pegawai + "' AND `tanggal_cuti` = '" + tanggal + "')";
                                Utility.db.getConnection().createStatement();
                                if (Utility.db.getStatement().executeUpdate(Query) == 1) {
                                    count++;
                                }
                            } else if (tabel_data_TidakMasuk.getValueAt(i, 11).toString().equals("Cuti Bersama")
                                    && tabel_data_TidakMasuk.getValueAt(i, 6).toString().toLowerCase().contains("staff")) {
                                String tanggal = tabel_data_TidakMasuk.getValueAt(i, 0).toString();
                                String id_pegawai = tabel_data_TidakMasuk.getValueAt(i, 1).toString();
                                String Query = "INSERT INTO `tb_cuti`(`id_pegawai`, `tanggal_cuti`, `jenis_cuti`, `kategori_cuti`, `keterangan`) "
                                        + "SELECT * FROM (SELECT '" + id_pegawai + "','" + tanggal + "', 'Cuti Tahunan', '-', (SELECT `keterangan` FROM `tb_libur` WHERE `tanggal_libur` = '" + tanggal + "'))AS tmp\n"
                                        + "WHERE NOT EXISTS (SELECT `kode_cuti` FROM `tb_cuti` WHERE `id_pegawai` = '" + id_pegawai + "' AND `tanggal_cuti` = '" + tanggal + "')";
                                Utility.db.getConnection().createStatement();
                                if (Utility.db.getStatement().executeUpdate(Query) == 1) {
                                    count++;
                                }
                            }
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Tanggal tidak boleh kosong !");
                }
                JOptionPane.showMessageDialog(this, count + " Saved !");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
                Logger.getLogger(JPanel_Absensi_Karyawan.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txt_search_NamaKaryawan = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txt_search_pin = new javax.swing.JTextField();
        jLabel35 = new javax.swing.JLabel();
        Date_Search1 = new com.toedter.calendar.JDateChooser();
        Date_Search2 = new com.toedter.calendar.JDateChooser();
        button_refresh = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabel_data_absen = new javax.swing.JTable();
        jLabel13 = new javax.swing.JLabel();
        label_total_data_absen = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        button_export_mentah = new javax.swing.JButton();
        button_mesin = new javax.swing.JButton();
        jLabel23 = new javax.swing.JLabel();
        ComboBox_posisi = new javax.swing.JComboBox<>();
        CheckBox_hanya_absen_mesin = new javax.swing.JCheckBox();
        button_export_rekap = new javax.swing.JButton();
        txt_search_bagian = new javax.swing.JTextField();
        button_export_rekap_ct = new javax.swing.JButton();
        button_jumlah_absen_karyawan_CBT_CTK = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        txt_search_mesin_absen = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        txt_search_NamaKaryawan1 = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txt_search_pin1 = new javax.swing.JTextField();
        jLabel36 = new javax.swing.JLabel();
        Date_Search_Lembur1 = new com.toedter.calendar.JDateChooser();
        Date_Search_Lembur2 = new com.toedter.calendar.JDateChooser();
        button_refresh1 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabel_data_lembur = new javax.swing.JTable();
        jLabel14 = new javax.swing.JLabel();
        label_total_data_absen1 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        ComboBox_departemen_karyawan1 = new javax.swing.JComboBox<>();
        ComboBox_bagian_karyawan1 = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        button_export_lembur = new javax.swing.JButton();
        ComboBox_posisi1 = new javax.swing.JComboBox<>();
        jLabel24 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        txt_search_NamaKaryawan2 = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txt_search_pin2 = new javax.swing.JTextField();
        jLabel37 = new javax.swing.JLabel();
        Date_TidakMasuk1 = new com.toedter.calendar.JDateChooser();
        Date_TidakMasuk2 = new com.toedter.calendar.JDateChooser();
        button_refresh_data_TidakMasuk = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        tabel_data_TidakMasuk = new javax.swing.JTable();
        jLabel15 = new javax.swing.JLabel();
        label_total_data = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        Spinner_jam1 = new javax.swing.JSpinner();
        Spinner_menit1 = new javax.swing.JSpinner();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        Spinner_jam2 = new javax.swing.JSpinner();
        Spinner_menit2 = new javax.swing.JSpinner();
        button_export_tdkMasuk = new javax.swing.JButton();
        jLabel21 = new javax.swing.JLabel();
        label_total_tidak_masuk = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        label_total_masuk = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        ComboBox_posisi2 = new javax.swing.JComboBox<>();
        button_rekap = new javax.swing.JButton();
        button_form_tidak_masuk = new javax.swing.JButton();
        txt_search_bagian_data_tidak_masuk = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        ComboBox_filter_absen = new javax.swing.JComboBox<>();
        jLabel27 = new javax.swing.JLabel();
        ComboBox_filter_jenis_hari = new javax.swing.JComboBox<>();

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("Nama Karyawan :");

        txt_search_NamaKaryawan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_NamaKaryawan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_NamaKaryawanKeyPressed(evt);
            }
        });

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel8.setText("PIN :");

        txt_search_pin.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_pin.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_pinKeyPressed(evt);
            }
        });

        jLabel35.setBackground(new java.awt.Color(255, 255, 255));
        jLabel35.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        jLabel35.setText("Tanggal Absen :");

        Date_Search1.setBackground(new java.awt.Color(255, 255, 255));
        Date_Search1.setToolTipText("");
        Date_Search1.setDate(new Date());
        Date_Search1.setDateFormatString("dd MMMM yyyy");
        Date_Search1.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        Date_Search1.setMinSelectableDate(new java.util.Date(1483207315000L));

        Date_Search2.setBackground(new java.awt.Color(255, 255, 255));
        Date_Search2.setDate(new Date());
        Date_Search2.setDateFormatString("dd MMMM yyyy");
        Date_Search2.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N

        button_refresh.setBackground(new java.awt.Color(255, 255, 255));
        button_refresh.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_refresh.setText("Refresh");
        button_refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refreshActionPerformed(evt);
            }
        });

        tabel_data_absen.setAutoCreateRowSorter(true);
        tabel_data_absen.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tabel_data_absen.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "PIN", "ID Pegawai", "Nama", "Bagian", "Departemen", "Posisi", "Tanggal", "Waktu Absen", "Mesin Absen", "Verifikasi", "SPL", "Jumlah Jam", "Jam Mulai"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tabel_data_absen);
        if (tabel_data_absen.getColumnModel().getColumnCount() > 0) {
            tabel_data_absen.getColumnModel().getColumn(10).setHeaderValue("SPL");
            tabel_data_absen.getColumnModel().getColumn(11).setHeaderValue("Jumlah Jam");
            tabel_data_absen.getColumnModel().getColumn(12).setHeaderValue("Jam Mulai");
        }

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("Total Data :");

        label_total_data_absen.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_absen.setText("0");

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel6.setText("Bagian :");

        button_export_mentah.setBackground(new java.awt.Color(255, 255, 255));
        button_export_mentah.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_mentah.setText("Export");
        button_export_mentah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_mentahActionPerformed(evt);
            }
        });

        button_mesin.setBackground(new java.awt.Color(255, 255, 255));
        button_mesin.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_mesin.setText("Data Mesin Absen");
        button_mesin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_mesinActionPerformed(evt);
            }
        });

        jLabel23.setBackground(new java.awt.Color(255, 255, 255));
        jLabel23.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel23.setText("Posisi :");

        ComboBox_posisi.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_posisi.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        CheckBox_hanya_absen_mesin.setBackground(new java.awt.Color(255, 255, 255));
        CheckBox_hanya_absen_mesin.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        CheckBox_hanya_absen_mesin.setText("Hanya Menampilkan Absen dari Mesin Finger");
        CheckBox_hanya_absen_mesin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CheckBox_hanya_absen_mesinActionPerformed(evt);
            }
        });

        button_export_rekap.setBackground(new java.awt.Color(255, 255, 255));
        button_export_rekap.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_rekap.setText("Rekap Absen");
        button_export_rekap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_rekapActionPerformed(evt);
            }
        });

        txt_search_bagian.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_bagian.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_bagianKeyPressed(evt);
            }
        });

        button_export_rekap_ct.setBackground(new java.awt.Color(255, 255, 255));
        button_export_rekap_ct.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_rekap_ct.setText("Rekap Absen CT");
        button_export_rekap_ct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_rekap_ctActionPerformed(evt);
            }
        });

        button_jumlah_absen_karyawan_CBT_CTK.setBackground(new java.awt.Color(255, 255, 255));
        button_jumlah_absen_karyawan_CBT_CTK.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_jumlah_absen_karyawan_CBT_CTK.setText("Jumlah Absen Karyawan Cabut & Cetak");
        button_jumlah_absen_karyawan_CBT_CTK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_jumlah_absen_karyawan_CBT_CTKActionPerformed(evt);
            }
        });

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel11.setText("Mesin Absen :");

        txt_search_mesin_absen.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_mesin_absen.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_mesin_absenKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_data_absen))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(CheckBox_hanya_absen_mesin)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_export_rekap)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_export_rekap_ct)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_jumlah_absen_karyawan_CBT_CTK)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_mesin)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_export_mentah)))
                        .addGap(0, 465, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txt_search_NamaKaryawan)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txt_search_pin)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel35)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(Date_Search1, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(Date_Search2, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txt_search_mesin_absen, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txt_search_bagian, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel23)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(ComboBox_posisi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(button_refresh)))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_search_mesin_absen, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_Search2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_posisi, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_bagian, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_NamaKaryawan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_pin, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_Search1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_refresh, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(button_export_rekap_ct, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CheckBox_hanya_absen_mesin)
                    .addComponent(button_export_rekap, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_jumlah_absen_karyawan_CBT_CTK, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_mesin, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export_mentah, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 541, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_total_data_absen, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("DATA ABSENSI SELURUH KARYAWAN", jPanel1);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("Nama Karyawan :");

        txt_search_NamaKaryawan1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_NamaKaryawan1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_NamaKaryawan1KeyPressed(evt);
            }
        });

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel9.setText("PIN :");

        txt_search_pin1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_pin1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_pin1KeyPressed(evt);
            }
        });

        jLabel36.setBackground(new java.awt.Color(255, 255, 255));
        jLabel36.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        jLabel36.setText("Tanggal Absen :");

        Date_Search_Lembur1.setBackground(new java.awt.Color(255, 255, 255));
        Date_Search_Lembur1.setToolTipText("");
        Date_Search_Lembur1.setDate(new Date());
        Date_Search_Lembur1.setDateFormatString("dd MMMM yyyy");
        Date_Search_Lembur1.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        Date_Search_Lembur1.setMinSelectableDate(new java.util.Date(1483207315000L));

        Date_Search_Lembur2.setBackground(new java.awt.Color(255, 255, 255));
        Date_Search_Lembur2.setDate(new Date());
        Date_Search_Lembur2.setDateFormatString("dd MMMM yyyy");
        Date_Search_Lembur2.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N

        button_refresh1.setBackground(new java.awt.Color(255, 255, 255));
        button_refresh1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_refresh1.setText("Refresh");
        button_refresh1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refresh1ActionPerformed(evt);
            }
        });

        tabel_data_lembur.setAutoCreateRowSorter(true);
        tabel_data_lembur.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tabel_data_lembur.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Hari", "Tanggal", "PIN", "ID Pegawai", "Nama", "Bagian", "Departemen", "Posisi", "Status", "Jam Masuk", "Jam Pulang", "Mesin Absen", "Jam Lembur", "Menit", "SPL", "Jumlah Jam", "Mulai Lembur", "Menit Ijin", "Jam Lembur", "Menit"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.Float.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabel_data_lembur.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(tabel_data_lembur);

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setText("Total Data :");

        label_total_data_absen1.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_absen1.setText("0");

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel4.setText("Departemen :");

        ComboBox_departemen_karyawan1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_departemen_karyawan1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));
        ComboBox_departemen_karyawan1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ComboBox_departemen_karyawan1ActionPerformed(evt);
            }
        });

        ComboBox_bagian_karyawan1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_bagian_karyawan1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel7.setText("Bagian :");

        button_export_lembur.setBackground(new java.awt.Color(255, 255, 255));
        button_export_lembur.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_lembur.setText("Export");
        button_export_lembur.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_lemburActionPerformed(evt);
            }
        });

        ComboBox_posisi1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_posisi1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        jLabel24.setBackground(new java.awt.Color(255, 255, 255));
        jLabel24.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel24.setText("Posisi :");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_data_absen1))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txt_search_NamaKaryawan1)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txt_search_pin1)
                                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel36)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(Date_Search_Lembur1, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(Date_Search_Lembur2, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(ComboBox_departemen_karyawan1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(ComboBox_bagian_karyawan1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(ComboBox_posisi1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_refresh1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_export_lembur)))
                        .addGap(0, 179, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(txt_search_NamaKaryawan1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_search_pin1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Date_Search_Lembur1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Date_Search_Lembur2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ComboBox_departemen_karyawan1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ComboBox_bagian_karyawan1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_posisi1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(button_refresh1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_export_lembur, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 569, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_total_data_absen1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("DATA LEMBUR KARYAWAN", jPanel2);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel5.setText("Nama Karyawan :");

        txt_search_NamaKaryawan2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_NamaKaryawan2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_NamaKaryawan2KeyPressed(evt);
            }
        });

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel10.setText("PIN :");

        txt_search_pin2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_pin2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_pin2KeyPressed(evt);
            }
        });

        jLabel37.setBackground(new java.awt.Color(255, 255, 255));
        jLabel37.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        jLabel37.setText("Tanggal Absen :");

        Date_TidakMasuk1.setBackground(new java.awt.Color(255, 255, 255));
        Date_TidakMasuk1.setToolTipText("");
        Date_TidakMasuk1.setDate(new Date(new Date().getTime()-(7 * 24 * 60 * 60 * 1000)));
        Date_TidakMasuk1.setDateFormatString("dd MMMM yyyy");
        Date_TidakMasuk1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Date_TidakMasuk1.setMinSelectableDate(new java.util.Date(1483207315000L));

        Date_TidakMasuk2.setBackground(new java.awt.Color(255, 255, 255));
        Date_TidakMasuk2.setDate(new Date());
        Date_TidakMasuk2.setDateFormatString("dd MMMM yyyy");
        Date_TidakMasuk2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Date_TidakMasuk2.setMaxSelectableDate(new Date());

        button_refresh_data_TidakMasuk.setBackground(new java.awt.Color(255, 255, 255));
        button_refresh_data_TidakMasuk.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_refresh_data_TidakMasuk.setText("Refresh");
        button_refresh_data_TidakMasuk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refresh_data_TidakMasukActionPerformed(evt);
            }
        });

        tabel_data_TidakMasuk.setAutoCreateRowSorter(true);
        tabel_data_TidakMasuk.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tabel_data_TidakMasuk.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tanggal", "ID Pegawai", "PIN", "Nama", "Bagian", "Departemen", "Posisi", "Tanggal", "Jam Absen", "Tgl Masuk", "Jam Kerja", "Kerja/Libur"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class
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
        tabel_data_TidakMasuk.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(tabel_data_TidakMasuk);

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel15.setText("Total Data :");

        label_total_data.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        label_total_data.setText("0");

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel12.setText("Bagian :");

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel16.setText("Jam :");

        Spinner_jam1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Spinner_jam1.setModel(new javax.swing.SpinnerNumberModel(5, 0, 23, 1));
        Spinner_jam1.setEditor(new javax.swing.JSpinner.NumberEditor(Spinner_jam1, ""));

        Spinner_menit1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Spinner_menit1.setModel(new javax.swing.SpinnerNumberModel(0, 0, 59, 1));
        Spinner_menit1.setEditor(new javax.swing.JSpinner.NumberEditor(Spinner_menit1, ""));

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel17.setText("Jam");

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel18.setText("Jam");

        jLabel19.setBackground(new java.awt.Color(255, 255, 255));
        jLabel19.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel19.setText("Menit");

        jLabel20.setBackground(new java.awt.Color(255, 255, 255));
        jLabel20.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel20.setText("Menit");

        Spinner_jam2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Spinner_jam2.setModel(new javax.swing.SpinnerNumberModel(23, 0, 23, 1));
        Spinner_jam2.setEditor(new javax.swing.JSpinner.NumberEditor(Spinner_jam2, ""));

        Spinner_menit2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Spinner_menit2.setModel(new javax.swing.SpinnerNumberModel(0, 0, 59, 1));
        Spinner_menit2.setEditor(new javax.swing.JSpinner.NumberEditor(Spinner_menit2, ""));

        button_export_tdkMasuk.setBackground(new java.awt.Color(255, 255, 255));
        button_export_tdkMasuk.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_tdkMasuk.setText("Export");
        button_export_tdkMasuk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_tdkMasukActionPerformed(evt);
            }
        });

        jLabel21.setBackground(new java.awt.Color(255, 255, 255));
        jLabel21.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(255, 0, 0));
        jLabel21.setText("Jumlah Tidak Masuk:");

        label_total_tidak_masuk.setBackground(new java.awt.Color(255, 255, 255));
        label_total_tidak_masuk.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        label_total_tidak_masuk.setForeground(new java.awt.Color(255, 0, 0));
        label_total_tidak_masuk.setText("0");

        jLabel22.setBackground(new java.awt.Color(255, 255, 255));
        jLabel22.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(0, 255, 0));
        jLabel22.setText("Jumlah Masuk:");

        label_total_masuk.setBackground(new java.awt.Color(255, 255, 255));
        label_total_masuk.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        label_total_masuk.setForeground(new java.awt.Color(0, 255, 0));
        label_total_masuk.setText("0");

        jLabel25.setBackground(new java.awt.Color(255, 255, 255));
        jLabel25.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel25.setText("Posisi :");

        ComboBox_posisi2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_posisi2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        button_rekap.setBackground(new java.awt.Color(255, 255, 255));
        button_rekap.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_rekap.setText("Simpan data tidak masuk");
        button_rekap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_rekapActionPerformed(evt);
            }
        });

        button_form_tidak_masuk.setBackground(new java.awt.Color(255, 255, 255));
        button_form_tidak_masuk.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_form_tidak_masuk.setText("Form Tidak Masuk");
        button_form_tidak_masuk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_form_tidak_masukActionPerformed(evt);
            }
        });

        txt_search_bagian_data_tidak_masuk.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_bagian_data_tidak_masuk.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_bagian_data_tidak_masukKeyPressed(evt);
            }
        });

        jLabel26.setBackground(new java.awt.Color(255, 255, 255));
        jLabel26.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel26.setText("Absensi :");

        ComboBox_filter_absen.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_filter_absen.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Ada Data Absen", "Tidak ada data" }));

        jLabel27.setBackground(new java.awt.Color(255, 255, 255));
        jLabel27.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel27.setText("Jenis Hari :");

        ComboBox_filter_jenis_hari.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_filter_jenis_hari.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Hari Kerja", "Hari Libur", "Cuti Bersama", "Tanggal Merah" }));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(button_rekap)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_form_tidak_masuk)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_export_tdkMasuk)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane3)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addComponent(jLabel15)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_data)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel21)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_tidak_masuk)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel22)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_masuk))
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(txt_search_NamaKaryawan2)
                                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(txt_search_pin2)
                                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addGroup(jPanel3Layout.createSequentialGroup()
                                                .addComponent(Date_TidakMasuk1, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(Date_TidakMasuk2, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(jPanel3Layout.createSequentialGroup()
                                                .addComponent(jLabel37)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 201, Short.MAX_VALUE)
                                                .addComponent(jLabel16)))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel17)
                                            .addComponent(jLabel18))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(Spinner_jam1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(Spinner_jam2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel19)
                                            .addComponent(jLabel20))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(Spinner_menit1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(Spinner_menit2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txt_search_bagian_data_tidak_masuk, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel12))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jLabel25)
                                            .addComponent(ComboBox_posisi2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jLabel26)
                                            .addComponent(ComboBox_filter_absen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jLabel27)
                                            .addComponent(ComboBox_filter_jenis_hari, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(button_refresh_data_TidakMasuk)))
                                .addGap(0, 135, Short.MAX_VALUE)))
                        .addContainerGap())))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                    .addComponent(txt_search_NamaKaryawan2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txt_search_pin2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(Date_TidakMasuk1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(Date_TidakMasuk2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(Spinner_jam1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(Spinner_menit1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(Spinner_jam2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(Spinner_menit2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(ComboBox_posisi2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txt_search_bagian_data_tidak_masuk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(ComboBox_filter_absen, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ComboBox_filter_jenis_hari, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_refresh_data_TidakMasuk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_rekap, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_form_tidak_masuk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export_tdkMasuk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 543, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_total_masuk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_total_data, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_tidak_masuk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jTabbedPane1.addTab("DATA TIDAK MASUK / BELUM ABSEN", jPanel3);

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

    private void txt_search_NamaKaryawanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_NamaKaryawanKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_absen();
        }
    }
//GEN-LAST:event_txt_search_NamaKaryawanKeyPressed

    private void txt_search_pinKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_pinKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_absen();
        }
    }//GEN-LAST:event_txt_search_pinKeyPressed

    private void button_refreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refreshActionPerformed
        // TODO add your handling code here:
        refreshTable_absen();
    }//GEN-LAST:event_button_refreshActionPerformed

    private void txt_search_NamaKaryawan1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_NamaKaryawan1KeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_Lembur();
        }
    }//GEN-LAST:event_txt_search_NamaKaryawan1KeyPressed

    private void txt_search_pin1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_pin1KeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_Lembur();
        }
    }//GEN-LAST:event_txt_search_pin1KeyPressed

    private void button_refresh1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refresh1ActionPerformed
        // TODO add your handling code here:
        refreshTable_Lembur();
    }//GEN-LAST:event_button_refresh1ActionPerformed

    private void ComboBox_departemen_karyawan1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ComboBox_departemen_karyawan1ActionPerformed
        // TODO add your handling code here:
        try {
            ComboBox_bagian_karyawan1.removeAllItems();
            String query = "SELECT `nama_bagian` FROM `tb_bagian` ORDER BY `nama_bagian`";
            if (ComboBox_departemen_karyawan1.getSelectedItem() != "All") {
                query = "SELECT `nama_bagian` FROM `tb_bagian` WHERE `kode_departemen`='" + ComboBox_departemen_karyawan1.getSelectedItem() + "'";
            }
            rs = Utility.db.getStatement().executeQuery(query);
            ComboBox_bagian_karyawan1.addItem("All");
            while (rs.next()) {
                ComboBox_bagian_karyawan1.addItem(rs.getString("nama_bagian"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }//GEN-LAST:event_ComboBox_departemen_karyawan1ActionPerformed

    private void txt_search_NamaKaryawan2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_NamaKaryawan2KeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_TidakMasuk();
        }
    }//GEN-LAST:event_txt_search_NamaKaryawan2KeyPressed

    private void txt_search_pin2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_pin2KeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_TidakMasuk();
        }
    }//GEN-LAST:event_txt_search_pin2KeyPressed

    private void button_refresh_data_TidakMasukActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refresh_data_TidakMasukActionPerformed
        // TODO add your handling code here:
        refreshTable_TidakMasuk();
    }//GEN-LAST:event_button_refresh_data_TidakMasukActionPerformed

    private void button_export_mentahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_mentahActionPerformed
        // TODO add your handling code here:
        DefaultTableModel table = (DefaultTableModel) tabel_data_absen.getModel();
        ExportToExcel.writeToExcel(table, this);
    }//GEN-LAST:event_button_export_mentahActionPerformed

    private void button_export_lemburActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_lemburActionPerformed
        // TODO add your handling code here:
        DefaultTableModel table = (DefaultTableModel) tabel_data_lembur.getModel();
        ExportToExcel.writeToExcel(table, this);
    }//GEN-LAST:event_button_export_lemburActionPerformed

    private void button_export_tdkMasukActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_tdkMasukActionPerformed
        // TODO add your handling code here:
        DefaultTableModel table = (DefaultTableModel) tabel_data_TidakMasuk.getModel();
        ExportToExcel.writeToExcel(table, this);
    }//GEN-LAST:event_button_export_tdkMasukActionPerformed

    private void button_mesinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_mesinActionPerformed
        // TODO add your handling code here:
        JDialog_Data_MesinAbsen dialog = new JDialog_Data_MesinAbsen(new javax.swing.JFrame(), true);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);
        dialog.setResizable(false);
    }//GEN-LAST:event_button_mesinActionPerformed

    private void button_rekapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_rekapActionPerformed
        // TODO add your handling code here:
        new_rekap_tidak_masuk();
    }//GEN-LAST:event_button_rekapActionPerformed

    private void CheckBox_hanya_absen_mesinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CheckBox_hanya_absen_mesinActionPerformed
        // TODO add your handling code here:
        refreshTable_absen();
    }//GEN-LAST:event_CheckBox_hanya_absen_mesinActionPerformed

    private void button_export_rekapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_rekapActionPerformed
        List<Rekap_Model> listRekap = new ArrayList<>();
        try {

            String karyawan = "";
            if (!txt_search_NamaKaryawan.getText().equals("")) {
                karyawan = "AND `tb_karyawan`.`nama_pegawai` LIKE '%" + txt_search_NamaKaryawan.getText() + "%'";
            }
            String bagian = "AND `tb_bagian`.`nama_bagian` LIKE '%" + txt_search_bagian.getText() + "%'";
            if (txt_search_bagian.getText() == null || txt_search_bagian.getText().equals("")) {
                bagian = "";
            }
            String filter_mesin = "AND `att_mesin_finger`.`nama_mesin` LIKE '%" + txt_search_mesin_absen.getText() + "%' \n";
            if (txt_search_mesin_absen.getText() == null || txt_search_mesin_absen.getText().equals("")) {
                filter_mesin = "";
            }
            String posisi = "AND `posisi` = '" + ComboBox_posisi.getSelectedItem().toString() + "' ";
            if ("All".equals(ComboBox_posisi.getSelectedItem().toString())) {
                posisi = "";
            }
            sql = "SELECT DATE_FORMAT(`scan_date`, '%d') AS 'tgl', `pin`, `tb_karyawan`.`nama_pegawai`, `status`, `nama_bagian` \n"
                    + "FROM `att_log` "
                    + "LEFT JOIN `tb_karyawan` ON `att_log`.`pin` = `tb_karyawan`.`pin_finger`\n"
                    + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`"
                    //                  + "WHERE YEAR(`scan_date`) = 2021 AND MONTH(`scan_date`) = 7 "
                    + "WHERE `pin` LIKE '%" + txt_search_pin.getText() + "%' "
                    + karyawan + bagian + filter_mesin + posisi
                    + "AND (DATE(`scan_date`) BETWEEN '" + dateFormat.format(Date_Search1.getDate()) + "' AND '" + dateFormat.format(Date_Search2.getDate()) + "')"
                    + "GROUP BY DATE_FORMAT(`scan_date`, '%d'), `pin` "
                    + "ORDER BY `nama_pegawai`, DATE_FORMAT(`scan_date`, '%d')";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                String currPin = rs.getString("pin");
                String currNama = rs.getString("nama_pegawai");
                String currBagian = rs.getString("nama_bagian");
                int currTgl = rs.getInt("tgl");
                boolean founded = false;
                for (int i = 0; i < listRekap.size(); i++) {
                    if (listRekap.get(i).getPin().equalsIgnoreCase(currPin)) {
                        switch (currTgl) {
                            case 1:
                                listRekap.get(i).setTgl1(1);
                                break;
                            case 2:
                                listRekap.get(i).setTgl2(1);
                                break;
                            case 3:
                                listRekap.get(i).setTgl3(1);
                                break;
                            case 4:
                                listRekap.get(i).setTgl4(1);
                                break;
                            case 5:
                                listRekap.get(i).setTgl5(1);
                                break;
                            case 6:
                                listRekap.get(i).setTgl6(1);
                                break;
                            case 7:
                                listRekap.get(i).setTgl7(1);
                                break;
                            case 8:
                                listRekap.get(i).setTgl8(1);
                                break;
                            case 9:
                                listRekap.get(i).setTgl9(1);
                                break;
                            case 10:
                                listRekap.get(i).setTgl10(1);
                                break;
                            case 11:
                                listRekap.get(i).setTgl11(1);
                                break;
                            case 12:
                                listRekap.get(i).setTgl12(1);
                                break;
                            case 13:
                                listRekap.get(i).setTgl13(1);
                                break;
                            case 14:
                                listRekap.get(i).setTgl14(1);
                                break;
                            case 15:
                                listRekap.get(i).setTgl15(1);
                                break;
                            case 16:
                                listRekap.get(i).setTgl16(1);
                                break;
                            case 17:
                                listRekap.get(i).setTgl17(1);
                                break;
                            case 18:
                                listRekap.get(i).setTgl18(1);
                                break;
                            case 19:
                                listRekap.get(i).setTgl19(1);
                                break;
                            case 20:
                                listRekap.get(i).setTgl20(1);
                                break;
                            case 21:
                                listRekap.get(i).setTgl21(1);
                                break;
                            case 22:
                                listRekap.get(i).setTgl22(1);
                                break;
                            case 23:
                                listRekap.get(i).setTgl23(1);
                                break;
                            case 24:
                                listRekap.get(i).setTgl24(1);
                                break;
                            case 25:
                                listRekap.get(i).setTgl25(1);
                                break;
                            case 26:
                                listRekap.get(i).setTgl26(1);
                                break;
                            case 27:
                                listRekap.get(i).setTgl27(1);
                                break;
                            case 28:
                                listRekap.get(i).setTgl28(1);
                                break;
                            case 29:
                                listRekap.get(i).setTgl29(1);
                                break;
                            case 30:
                                listRekap.get(i).setTgl30(1);
                                break;
                            case 31:
                                listRekap.get(i).setTgl31(1);
                                break;
                        }
                        founded = true;
                        break;
                    }
                }
                if (!founded) {
                    Rekap_Model newRekap = new Rekap_Model(currPin, currNama, currBagian);
                    switch (currTgl) {
                        case 1:
                            newRekap.setTgl1(1);
                            break;
                        case 2:
                            newRekap.setTgl2(1);
                            break;
                        case 3:
                            newRekap.setTgl3(1);
                            break;
                        case 4:
                            newRekap.setTgl4(1);
                            break;
                        case 5:
                            newRekap.setTgl5(1);
                            break;
                        case 6:
                            newRekap.setTgl6(1);
                            break;
                        case 7:
                            newRekap.setTgl7(1);
                            break;
                        case 8:
                            newRekap.setTgl8(1);
                            break;
                        case 9:
                            newRekap.setTgl9(1);
                            break;
                        case 10:
                            newRekap.setTgl10(1);
                            break;
                        case 11:
                            newRekap.setTgl11(1);
                            break;
                        case 12:
                            newRekap.setTgl12(1);
                            break;
                        case 13:
                            newRekap.setTgl13(1);
                            break;
                        case 14:
                            newRekap.setTgl14(1);
                            break;
                        case 15:
                            newRekap.setTgl15(1);
                            break;
                        case 16:
                            newRekap.setTgl16(1);
                            break;
                        case 17:
                            newRekap.setTgl17(1);
                            break;
                        case 18:
                            newRekap.setTgl18(1);
                            break;
                        case 19:
                            newRekap.setTgl19(1);
                            break;
                        case 20:
                            newRekap.setTgl20(1);
                            break;
                        case 21:
                            newRekap.setTgl21(1);
                            break;
                        case 22:
                            newRekap.setTgl22(1);
                            break;
                        case 23:
                            newRekap.setTgl23(1);
                            break;
                        case 24:
                            newRekap.setTgl24(1);
                            break;
                        case 25:
                            newRekap.setTgl25(1);
                            break;
                        case 26:
                            newRekap.setTgl26(1);
                            break;
                        case 27:
                            newRekap.setTgl27(1);
                            break;
                        case 28:
                            newRekap.setTgl28(1);
                            break;
                        case 29:
                            newRekap.setTgl29(1);
                            break;
                        case 30:
                            newRekap.setTgl30(1);
                            break;
                        case 31:
                            newRekap.setTgl31(1);
                            break;
                    }
                    listRekap.add(newRekap);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(JPanel_Absensi_Karyawan.class.getName()).log(Level.SEVERE, null, ex);
        }

        /*for (int i = 0; i < listRekap.size(); i++) {
            System.out.print("PIN = "+listRekap.get(i).getPin());
            System.out.print(", Nama = "+listRekap.get(i).getNama());
            System.out.print(", Tgl1 = "+listRekap.get(i).getTgl1());
            System.out.print(", Tgl2 = "+listRekap.get(i).getTgl2());
            System.out.print(", Tgl3 = "+listRekap.get(i).getTgl3());
            System.out.print(", Tgl4 = "+listRekap.get(i).getTgl4());
            System.out.print(", Tgl5 = "+listRekap.get(i).getTgl5());
            System.out.println("");
        }*/
        try {
            JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Rekap_Absensi_Karyawan.jrxml");
            JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
            Map<String, Object> params = new HashMap<>();
            params.put("tanggal_1", dateFormat.format(Date_Search1.getDate()));
            params.put("tanggal_2", dateFormat.format(Date_Search2.getDate()));
            params.put("hari_kerja", Utility.countDaysWithoutFreeDays(Date_Search1.getDate(), Date_Search2.getDate()) + 1);
            /*params.entrySet().forEach((entry) -> {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        });*/
            JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, params, new JRBeanCollectionDataSource(listRekap));
            JasperViewer.viewReport(JASP_PRINT, false);
        } catch (JRException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_Absensi_Karyawan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_export_rekapActionPerformed

    private void txt_search_bagianKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_bagianKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_absen();
        }
    }//GEN-LAST:event_txt_search_bagianKeyPressed

    private void button_export_rekap_ctActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_rekap_ctActionPerformed
        // TODO add your handling code here:
        List<Rekap_Model> listRekap = new ArrayList<>();
        List<Rekap_Model> listRekap_ct = new ArrayList<>();
        try {

            sql = "SELECT DATE_FORMAT(`scan_date`, '%d') AS 'tgl', `pin`, `tb_karyawan`.`nama_pegawai`, `status`, `nama_bagian` \n"
                    + "FROM `att_log` "
                    + "LEFT JOIN `tb_karyawan` ON `att_log`.`pin` = `tb_karyawan`.`pin_finger`\n"
                    + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`"
                    //                  + "WHERE YEAR(`scan_date`) = 2021 AND MONTH(`scan_date`) = 7 "
                    + "WHERE (DATE(`scan_date`) BETWEEN '" + dateFormat.format(Date_Search1.getDate()) + "' AND '" + dateFormat.format(Date_Search2.getDate()) + "')"
                    + "GROUP BY DATE_FORMAT(`scan_date`, '%d'), `pin` "
                    + "ORDER BY `pin`, DATE_FORMAT(`scan_date`, '%d')";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                String currPin = rs.getString("pin");
                String currNama = rs.getString("nama_pegawai");
                String currBagian = rs.getString("nama_bagian");
                int currTgl = rs.getInt("tgl");
                boolean founded = false;
                for (int i = 0; i < listRekap.size(); i++) {
                    if (listRekap.get(i).getPin().equalsIgnoreCase(currPin)) {
                        switch (currTgl) {
                            case 1:
                                listRekap.get(i).setTgl1(1);
                                break;
                            case 2:
                                listRekap.get(i).setTgl2(1);
                                break;
                            case 3:
                                listRekap.get(i).setTgl3(1);
                                break;
                            case 4:
                                listRekap.get(i).setTgl4(1);
                                break;
                            case 5:
                                listRekap.get(i).setTgl5(1);
                                break;
                            case 6:
                                listRekap.get(i).setTgl6(1);
                                break;
                            case 7:
                                listRekap.get(i).setTgl7(1);
                                break;
                            case 8:
                                listRekap.get(i).setTgl8(1);
                                break;
                            case 9:
                                listRekap.get(i).setTgl9(1);
                                break;
                            case 10:
                                listRekap.get(i).setTgl10(1);
                                break;
                            case 11:
                                listRekap.get(i).setTgl11(1);
                                break;
                            case 12:
                                listRekap.get(i).setTgl12(1);
                                break;
                            case 13:
                                listRekap.get(i).setTgl13(1);
                                break;
                            case 14:
                                listRekap.get(i).setTgl14(1);
                                break;
                            case 15:
                                listRekap.get(i).setTgl15(1);
                                break;
                            case 16:
                                listRekap.get(i).setTgl16(1);
                                break;
                            case 17:
                                listRekap.get(i).setTgl17(1);
                                break;
                            case 18:
                                listRekap.get(i).setTgl18(1);
                                break;
                            case 19:
                                listRekap.get(i).setTgl19(1);
                                break;
                            case 20:
                                listRekap.get(i).setTgl20(1);
                                break;
                            case 21:
                                listRekap.get(i).setTgl21(1);
                                break;
                            case 22:
                                listRekap.get(i).setTgl22(1);
                                break;
                            case 23:
                                listRekap.get(i).setTgl23(1);
                                break;
                            case 24:
                                listRekap.get(i).setTgl24(1);
                                break;
                            case 25:
                                listRekap.get(i).setTgl25(1);
                                break;
                            case 26:
                                listRekap.get(i).setTgl26(1);
                                break;
                            case 27:
                                listRekap.get(i).setTgl27(1);
                                break;
                            case 28:
                                listRekap.get(i).setTgl28(1);
                                break;
                            case 29:
                                listRekap.get(i).setTgl29(1);
                                break;
                            case 30:
                                listRekap.get(i).setTgl30(1);
                                break;
                            case 31:
                                listRekap.get(i).setTgl31(1);
                                break;
                        }
                        founded = true;
                        break;
                    }
                }
                if (!founded) {
                    Rekap_Model newRekap = new Rekap_Model(currPin, currNama, currBagian);
                    switch (currTgl) {
                        case 1:
                            newRekap.setTgl1(1);
                            break;
                        case 2:
                            newRekap.setTgl2(1);
                            break;
                        case 3:
                            newRekap.setTgl3(1);
                            break;
                        case 4:
                            newRekap.setTgl4(1);
                            break;
                        case 5:
                            newRekap.setTgl5(1);
                            break;
                        case 6:
                            newRekap.setTgl6(1);
                            break;
                        case 7:
                            newRekap.setTgl7(1);
                            break;
                        case 8:
                            newRekap.setTgl8(1);
                            break;
                        case 9:
                            newRekap.setTgl9(1);
                            break;
                        case 10:
                            newRekap.setTgl10(1);
                            break;
                        case 11:
                            newRekap.setTgl11(1);
                            break;
                        case 12:
                            newRekap.setTgl12(1);
                            break;
                        case 13:
                            newRekap.setTgl13(1);
                            break;
                        case 14:
                            newRekap.setTgl14(1);
                            break;
                        case 15:
                            newRekap.setTgl15(1);
                            break;
                        case 16:
                            newRekap.setTgl16(1);
                            break;
                        case 17:
                            newRekap.setTgl17(1);
                            break;
                        case 18:
                            newRekap.setTgl18(1);
                            break;
                        case 19:
                            newRekap.setTgl19(1);
                            break;
                        case 20:
                            newRekap.setTgl20(1);
                            break;
                        case 21:
                            newRekap.setTgl21(1);
                            break;
                        case 22:
                            newRekap.setTgl22(1);
                            break;
                        case 23:
                            newRekap.setTgl23(1);
                            break;
                        case 24:
                            newRekap.setTgl24(1);
                            break;
                        case 25:
                            newRekap.setTgl25(1);
                            break;
                        case 26:
                            newRekap.setTgl26(1);
                            break;
                        case 27:
                            newRekap.setTgl27(1);
                            break;
                        case 28:
                            newRekap.setTgl28(1);
                            break;
                        case 29:
                            newRekap.setTgl29(1);
                            break;
                        case 30:
                            newRekap.setTgl30(1);
                            break;
                        case 31:
                            newRekap.setTgl31(1);
                            break;
                    }
                    listRekap.add(newRekap);
                }
            }

            String karyawan = "";
            if (!txt_search_NamaKaryawan.getText().equals("")) {
                karyawan = "AND `tb_karyawan`.`nama_pegawai` LIKE '%" + txt_search_NamaKaryawan.getText() + "%'";
            }
            String bagian = "AND `tb_bagian`.`nama_bagian` LIKE '%" + txt_search_bagian.getText() + "%'";
            if (txt_search_bagian.getText() == null || txt_search_bagian.getText().equals("")) {
                bagian = "";
            }
            String filter_mesin = "AND `att_mesin_finger`.`nama_mesin` LIKE '%" + txt_search_mesin_absen.getText() + "%' \n";
            if (txt_search_mesin_absen.getText() == null || txt_search_mesin_absen.getText().equals("")) {
                filter_mesin = "";
            }
            String posisi = "AND `posisi` = '" + ComboBox_posisi.getSelectedItem().toString() + "' ";
            if ("All".equals(ComboBox_posisi.getSelectedItem().toString())) {
                posisi = "";
            }
            sql = "SELECT DATE_FORMAT(`scan_date`, '%d') AS 'tgl', `pin`, `tb_karyawan`.`nama_pegawai`, `status`, `nama_bagian` \n"
                    + "FROM `att_log` "
                    + "LEFT JOIN `tb_karyawan` ON `att_log`.`pin` = `tb_karyawan`.`pin_finger`\n"
                    + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`"
                    //                  + "WHERE YEAR(`scan_date`) = 2021 AND MONTH(`scan_date`) = 7 "
                    + "WHERE `pin` LIKE '%" + txt_search_pin.getText() + "%' "
                    + karyawan + bagian + filter_mesin + posisi
                    + "AND (DATE(`scan_date`) BETWEEN '" + dateFormat.format(Date_Search1.getDate()) + "' AND '" + dateFormat.format(Date_Search2.getDate()) + "')"
                    + "GROUP BY DATE_FORMAT(`scan_date`, '%d'), `pin` "
                    + "ORDER BY `pin`, DATE_FORMAT(`scan_date`, '%d')";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                String currPin = rs.getString("pin");
                String currNama = rs.getString("nama_pegawai");
                String currBagian = rs.getString("nama_bagian");
                int currTgl = rs.getInt("tgl");
                boolean founded = false;
                for (int i = 0; i < listRekap_ct.size(); i++) {
                    if (listRekap_ct.get(i).getPin().equalsIgnoreCase(currPin)) {
                        switch (currTgl) {
                            case 1:
                                listRekap_ct.get(i).setTgl1(1);
                                break;
                            case 2:
                                listRekap_ct.get(i).setTgl2(1);
                                break;
                            case 3:
                                listRekap_ct.get(i).setTgl3(1);
                                break;
                            case 4:
                                listRekap_ct.get(i).setTgl4(1);
                                break;
                            case 5:
                                listRekap_ct.get(i).setTgl5(1);
                                break;
                            case 6:
                                listRekap_ct.get(i).setTgl6(1);
                                break;
                            case 7:
                                listRekap_ct.get(i).setTgl7(1);
                                break;
                            case 8:
                                listRekap_ct.get(i).setTgl8(1);
                                break;
                            case 9:
                                listRekap_ct.get(i).setTgl9(1);
                                break;
                            case 10:
                                listRekap_ct.get(i).setTgl10(1);
                                break;
                            case 11:
                                listRekap_ct.get(i).setTgl11(1);
                                break;
                            case 12:
                                listRekap_ct.get(i).setTgl12(1);
                                break;
                            case 13:
                                listRekap_ct.get(i).setTgl13(1);
                                break;
                            case 14:
                                listRekap_ct.get(i).setTgl14(1);
                                break;
                            case 15:
                                listRekap_ct.get(i).setTgl15(1);
                                break;
                            case 16:
                                listRekap_ct.get(i).setTgl16(1);
                                break;
                            case 17:
                                listRekap_ct.get(i).setTgl17(1);
                                break;
                            case 18:
                                listRekap_ct.get(i).setTgl18(1);
                                break;
                            case 19:
                                listRekap_ct.get(i).setTgl19(1);
                                break;
                            case 20:
                                listRekap_ct.get(i).setTgl20(1);
                                break;
                            case 21:
                                listRekap_ct.get(i).setTgl21(1);
                                break;
                            case 22:
                                listRekap_ct.get(i).setTgl22(1);
                                break;
                            case 23:
                                listRekap_ct.get(i).setTgl23(1);
                                break;
                            case 24:
                                listRekap_ct.get(i).setTgl24(1);
                                break;
                            case 25:
                                listRekap_ct.get(i).setTgl25(1);
                                break;
                            case 26:
                                listRekap_ct.get(i).setTgl26(1);
                                break;
                            case 27:
                                listRekap_ct.get(i).setTgl27(1);
                                break;
                            case 28:
                                listRekap_ct.get(i).setTgl28(1);
                                break;
                            case 29:
                                listRekap_ct.get(i).setTgl29(1);
                                break;
                            case 30:
                                listRekap_ct.get(i).setTgl30(1);
                                break;
                            case 31:
                                listRekap_ct.get(i).setTgl31(1);
                                break;
                        }
                        founded = true;
                        break;
                    }
                }
                if (!founded) {
                    Rekap_Model newRekap = new Rekap_Model(currPin, currNama, currBagian);
                    switch (currTgl) {
                        case 1:
                            newRekap.setTgl1(1);
                            break;
                        case 2:
                            newRekap.setTgl2(1);
                            break;
                        case 3:
                            newRekap.setTgl3(1);
                            break;
                        case 4:
                            newRekap.setTgl4(1);
                            break;
                        case 5:
                            newRekap.setTgl5(1);
                            break;
                        case 6:
                            newRekap.setTgl6(1);
                            break;
                        case 7:
                            newRekap.setTgl7(1);
                            break;
                        case 8:
                            newRekap.setTgl8(1);
                            break;
                        case 9:
                            newRekap.setTgl9(1);
                            break;
                        case 10:
                            newRekap.setTgl10(1);
                            break;
                        case 11:
                            newRekap.setTgl11(1);
                            break;
                        case 12:
                            newRekap.setTgl12(1);
                            break;
                        case 13:
                            newRekap.setTgl13(1);
                            break;
                        case 14:
                            newRekap.setTgl14(1);
                            break;
                        case 15:
                            newRekap.setTgl15(1);
                            break;
                        case 16:
                            newRekap.setTgl16(1);
                            break;
                        case 17:
                            newRekap.setTgl17(1);
                            break;
                        case 18:
                            newRekap.setTgl18(1);
                            break;
                        case 19:
                            newRekap.setTgl19(1);
                            break;
                        case 20:
                            newRekap.setTgl20(1);
                            break;
                        case 21:
                            newRekap.setTgl21(1);
                            break;
                        case 22:
                            newRekap.setTgl22(1);
                            break;
                        case 23:
                            newRekap.setTgl23(1);
                            break;
                        case 24:
                            newRekap.setTgl24(1);
                            break;
                        case 25:
                            newRekap.setTgl25(1);
                            break;
                        case 26:
                            newRekap.setTgl26(1);
                            break;
                        case 27:
                            newRekap.setTgl27(1);
                            break;
                        case 28:
                            newRekap.setTgl28(1);
                            break;
                        case 29:
                            newRekap.setTgl29(1);
                            break;
                        case 30:
                            newRekap.setTgl30(1);
                            break;
                        case 31:
                            newRekap.setTgl31(1);
                            break;
                    }
                    listRekap_ct.add(newRekap);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(JPanel_Absensi_Karyawan.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            ArrayList<Rekap_Model> newList = new ArrayList<>(listRekap);
            newList.addAll(listRekap_ct);
            JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Rekap_Absensi_Karyawan.jrxml");
            JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
            Map<String, Object> params = new HashMap<>();
            params.put("tanggal_1", dateFormat.format(Date_Search1.getDate()));
            params.put("tanggal_2", dateFormat.format(Date_Search2.getDate()));
            params.put("hari_kerja", Utility.countDaysWithoutFreeDays(Date_Search1.getDate(), Date_Search2.getDate()) + 1);
            JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, params, new JRBeanCollectionDataSource(newList));
            JasperViewer.viewReport(JASP_PRINT, false);
        } catch (JRException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_Absensi_Karyawan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_export_rekap_ctActionPerformed

    private void button_jumlah_absen_karyawan_CBT_CTKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_jumlah_absen_karyawan_CBT_CTKActionPerformed
        // TODO add your handling code here:
        JDialog_Rekap_CabutCetakAbsen dialog = new JDialog_Rekap_CabutCetakAbsen(new javax.swing.JFrame(), true);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);
        dialog.setResizable(false);
    }//GEN-LAST:event_button_jumlah_absen_karyawan_CBT_CTKActionPerformed

    private void button_form_tidak_masukActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_form_tidak_masukActionPerformed
        // TODO add your handling code here:
        try {
            JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Form_tidak_masuk.jrxml");
            JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
            JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, null, Utility.db.getConnection());
            JasperViewer.viewReport(JASP_PRINT, false);
        } catch (JRException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_form_tidak_masukActionPerformed

    private void txt_search_bagian_data_tidak_masukKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_bagian_data_tidak_masukKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_TidakMasuk();
        }
    }//GEN-LAST:event_txt_search_bagian_data_tidak_masukKeyPressed

    private void txt_search_mesin_absenKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_mesin_absenKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_absen();
        }
    }//GEN-LAST:event_txt_search_mesin_absenKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox CheckBox_hanya_absen_mesin;
    private javax.swing.JComboBox<String> ComboBox_bagian_karyawan1;
    private javax.swing.JComboBox<String> ComboBox_departemen_karyawan1;
    private javax.swing.JComboBox<String> ComboBox_filter_absen;
    private javax.swing.JComboBox<String> ComboBox_filter_jenis_hari;
    private javax.swing.JComboBox<String> ComboBox_posisi;
    private javax.swing.JComboBox<String> ComboBox_posisi1;
    private javax.swing.JComboBox<String> ComboBox_posisi2;
    private com.toedter.calendar.JDateChooser Date_Search1;
    private com.toedter.calendar.JDateChooser Date_Search2;
    private com.toedter.calendar.JDateChooser Date_Search_Lembur1;
    private com.toedter.calendar.JDateChooser Date_Search_Lembur2;
    private com.toedter.calendar.JDateChooser Date_TidakMasuk1;
    private com.toedter.calendar.JDateChooser Date_TidakMasuk2;
    private javax.swing.JSpinner Spinner_jam1;
    private javax.swing.JSpinner Spinner_jam2;
    private javax.swing.JSpinner Spinner_menit1;
    private javax.swing.JSpinner Spinner_menit2;
    private javax.swing.JButton button_export_lembur;
    private javax.swing.JButton button_export_mentah;
    private javax.swing.JButton button_export_rekap;
    private javax.swing.JButton button_export_rekap_ct;
    private javax.swing.JButton button_export_tdkMasuk;
    private javax.swing.JButton button_form_tidak_masuk;
    private javax.swing.JButton button_jumlah_absen_karyawan_CBT_CTK;
    private javax.swing.JButton button_mesin;
    private javax.swing.JButton button_refresh;
    private javax.swing.JButton button_refresh1;
    private javax.swing.JButton button_refresh_data_TidakMasuk;
    private javax.swing.JButton button_rekap;
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
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel label_total_data;
    private javax.swing.JLabel label_total_data_absen;
    private javax.swing.JLabel label_total_data_absen1;
    private javax.swing.JLabel label_total_masuk;
    private javax.swing.JLabel label_total_tidak_masuk;
    private javax.swing.JTable tabel_data_TidakMasuk;
    private javax.swing.JTable tabel_data_absen;
    private javax.swing.JTable tabel_data_lembur;
    private javax.swing.JTextField txt_search_NamaKaryawan;
    private javax.swing.JTextField txt_search_NamaKaryawan1;
    private javax.swing.JTextField txt_search_NamaKaryawan2;
    private javax.swing.JTextField txt_search_bagian;
    private javax.swing.JTextField txt_search_bagian_data_tidak_masuk;
    private javax.swing.JTextField txt_search_mesin_absen;
    private javax.swing.JTextField txt_search_pin;
    private javax.swing.JTextField txt_search_pin1;
    private javax.swing.JTextField txt_search_pin2;
    // End of variables declaration//GEN-END:variables
}
