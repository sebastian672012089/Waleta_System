package waleta_system.HRD;

import waleta_system.Class.Utility;
import java.awt.HeadlessException;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.ExportToExcel;
import waleta_system.MainForm;

public class JPanel_Absen_Cuti extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    PreparedStatement pst;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Date today = new Date();
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    DefaultTableModel dm = new DefaultTableModel();
    JFileChooser chooser = new JFileChooser();

    public JPanel_Absen_Cuti() {
        initComponents();
    }

    public void init() {
        try {
            String tahun_ini = new SimpleDateFormat("yyyy").format(today);
            Date filter1 = dateFormat.parse(tahun_ini + "-01-01");
            Date_Ijin_absen1.setDate(filter1);
            Date_Ijin_absen2.setDate(today);
            Date_Cuti_keseluruhan1.setDate(filter1);
            Date_Cuti_keseluruhan2.setDate(today);

            ComboBox_departemen.removeAllItems();
            ComboBox_departemen.addItem("All");
            sql = "SELECT `kode_dep` FROM `tb_departemen` ORDER BY `kode_dep`";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_departemen.addItem(rs.getString("kode_dep"));
            }

            ComboBox_posisi1.removeAllItems();
            ComboBox_posisi1.addItem("All");
            sql = "SELECT DISTINCT(`posisi`) AS 'posisi' FROM `tb_karyawan` WHERE `posisi` IS NOT NULL";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_posisi1.addItem(rs.getString("posisi"));
            }

            ComboBox_status_karyawan.removeAllItems();
            ComboBox_status_karyawan.addItem("All");
            sql = "SELECT DISTINCT(`status`) AS 'status' FROM `tb_karyawan` WHERE `status` IS NOT NULL";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_status_karyawan.addItem(rs.getString("status"));
            }
            ComboBox_status_karyawan.setSelectedItem("IN");

            Table_karyawan.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent event) {
                    if (!event.getValueIsAdjusting() && Table_karyawan.getSelectedRow() != -1) {
                        int x = Table_karyawan.getSelectedRow();
                        refreshTable_Cuti();
                        label_nama.setText(Table_karyawan.getValueAt(x, 1).toString());
                    }
                }
            });

            Table_pengajuan.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent event) {
                    if (!event.getValueIsAdjusting() && Table_pengajuan.getSelectedRow() != -1) {
                        int x = Table_pengajuan.getSelectedRow();
                        if (Table_pengajuan.getValueAt(x, 11) == null) {//disetujui
                            button_disetujui_pengajuan.setEnabled(true);
                            button_diketahui_pengajuan.setEnabled(false);
                        } else {
                            if (Table_pengajuan.getValueAt(x, 12) == null) {//diketahui
                                button_diketahui_pengajuan.setEnabled(true);
                            } else {
                                button_diketahui_pengajuan.setEnabled(false);
                            }
                        }
                    }
                }
            });
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_Absen_Cuti.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void refreshTable_rekap_cuti_karyawan() {
        try {
            int tot_absen = 0, tot_cuti_sakit = 0, tot_cuti_ijin = 0, tot_cuti_tahunan = 0;
            DefaultTableModel model = (DefaultTableModel) Table_karyawan.getModel();
            model.setRowCount(0);

            String departemen = "";
            if (!"All".equals(ComboBox_departemen.getSelectedItem().toString())) {
                departemen = "AND `tb_bagian`.`kode_departemen` = '" + ComboBox_departemen.getSelectedItem().toString() + "' ";
            }
            String bagian = "";
            if (txt_search_bagian_rekap.getText() != null && !txt_search_bagian_rekap.getText().equals("")) {
                bagian = "AND `tb_bagian`.`nama_bagian` LIKE '%" + txt_search_bagian_rekap.getText() + "%' ";
            }
            String posisi = "";
            if (ComboBox_posisi.getSelectedIndex() == 1) {
                posisi = "AND `tb_karyawan`.`posisi` = 'PEJUANG' ";
            } else if (ComboBox_posisi.getSelectedIndex() == 2) {
                posisi = "AND `tb_karyawan`.`posisi` LIKE 'STAFF%' ";
            }
            String status = "";
            if (!"All".equals(ComboBox_status_karyawan.getSelectedItem().toString())) {
                status = "AND `tb_karyawan`.`status` = '" + ComboBox_status_karyawan.getSelectedItem().toString() + "' ";
            }

            sql = "SELECT *, "
                    + "IF("
                    + "YEAR(`tanggal_masuk`) = YEAR(CURRENT_DATE()), "
                    + "TIMESTAMPDIFF(MONTH, `tanggal_masuk`, CONCAT(YEAR(`tanggal_masuk`), '-12-31')) + 1,"
                    + " 12) AS jatah_cuti "
                    + "FROM "
                    + "(SELECT `id_pegawai`, `nama_pegawai`, `tb_bagian`.`nama_bagian`,`tb_bagian`.`kode_departemen`, `posisi`, `status`, "
                    + "IF("
                    + "(SELECT DATE(`jam_diketahui_keu`) FROM `tb_form_pindah_grup` WHERE \n"
                    + "`tb_form_pindah_grup`.`id_pegawai` = `tb_karyawan`.`id_pegawai` AND `levelgaji_baru` = 'STAFF'\n"
                    + "ORDER BY `jam_diketahui_keu` DESC LIMIT 1) IS NULL, "
                    + "`tanggal_masuk`, "
                    + "(SELECT DATE(`jam_diketahui_keu`) FROM `tb_form_pindah_grup` WHERE \n"
                    + "`tb_form_pindah_grup`.`id_pegawai` = `tb_karyawan`.`id_pegawai` AND `levelgaji_baru` = 'STAFF'\n"
                    + "ORDER BY `jam_diketahui_keu` DESC LIMIT 1))"
                    + " AS 'tanggal_masuk' "
                    + "FROM `tb_karyawan` "
                    + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`"
                    + "WHERE `nama_pegawai` LIKE '%" + txt_search_karyawan.getText() + "%' "
                    + bagian
                    + departemen
                    + posisi
                    + status
                    + "ORDER BY `id_pegawai` DESC) DATA ";
            pst = Utility.db.getConnection().prepareStatement(sql);
            rs = pst.executeQuery();
            Object[] row = new Object[20];
            while (rs.next()) {
                row[0] = rs.getString("id_pegawai");
                row[1] = rs.getString("nama_pegawai");
                row[2] = rs.getString("posisi");
                row[3] = rs.getString("nama_bagian");
                row[4] = rs.getString("status");
                row[5] = rs.getDate("tanggal_masuk");
                row[6] = 0;
                row[7] = 0;
                row[8] = 0;
                row[9] = 0;
                int jatah_cuti = rs.getInt("jatah_cuti");
                int cuti_terpakai = 0;
                int sisa_cuti = 0;

                String query = "SELECT `jenis_cuti`, COUNT(`kode_cuti`) AS 'hari'\n"
                        + "FROM `tb_cuti` "
                        + "WHERE "
                        + "`id_pegawai` = '" + rs.getString("id_pegawai") + "'\n"
                        + "AND `tb_cuti`.`tanggal_cuti` >= '" + dateFormat.format(rs.getDate("tanggal_masuk")) + "'"
                        + "GROUP BY `jenis_cuti`";
                if (Date_Ijin_absen1.getDate() != null && Date_Ijin_absen2.getDate() != null) {
                    query = "SELECT `jenis_cuti`, COUNT(`kode_cuti`) AS 'hari'\n"
                            + "FROM `tb_cuti` "
                            + "WHERE "
                            + "`id_pegawai` = '" + rs.getString("id_pegawai") + "' "
                            + "AND `tb_cuti`.`tanggal_cuti` >= '" + dateFormat.format(rs.getDate("tanggal_masuk")) + "'"
                            + "AND (`tb_cuti`.`tanggal_cuti` BETWEEN '" + dateFormat.format(Date_Ijin_absen1.getDate()) + "' AND '" + dateFormat.format(Date_Ijin_absen2.getDate()) + "') \n"
                            + "GROUP BY `jenis_cuti`";
                }
                ResultSet result = Utility.db.getStatement().executeQuery(query);
                while (result.next()) {
                    switch (result.getString("jenis_cuti")) {
                        case "Cuti Khusus":
                            row[6] = result.getInt("hari");
                            tot_cuti_ijin = tot_cuti_ijin + result.getInt("hari");
                            break;
                        case "Cuti Sakit":
                            row[7] = result.getInt("hari");
                            tot_cuti_sakit = tot_cuti_sakit + result.getInt("hari");
                            break;
                        case "Cuti Tahunan":
                            row[8] = result.getInt("hari");
                            tot_cuti_tahunan = tot_cuti_tahunan + result.getInt("hari");
                            cuti_terpakai = cuti_terpakai + result.getInt("hari");
                            break;
                        case "Absen":
                            row[9] = result.getInt("hari");
                            tot_absen = tot_absen + result.getInt("hari");
                            cuti_terpakai = cuti_terpakai + result.getInt("hari");
                            break;
                        default:
                            break;
                    }
                }
                sisa_cuti = jatah_cuti - cuti_terpakai;
                row[10] = jatah_cuti;
                row[11] = cuti_terpakai;
                row[12] = sisa_cuti;
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_karyawan);
            label_total_ijin.setText(Integer.toString(tot_cuti_ijin));
            label_total_sakit.setText(Integer.toString(tot_cuti_sakit));
            label_total_cuti_tahunan.setText(Integer.toString(tot_cuti_tahunan));
            label_total_absen.setText(Integer.toString(tot_absen));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Absen_Cuti.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_Cuti() {
        try {
            DefaultTableModel model = (DefaultTableModel) Table_Cuti_per_karyawan.getModel();
            model.setRowCount(0);
            int i = Table_karyawan.getSelectedRow();
            String filter_tanggal = "";
            if (Date_Ijin_absen1.getDate() != null && Date_Ijin_absen2.getDate() != null) {
                filter_tanggal = "AND `tb_cuti`.`tanggal_cuti` BETWEEN '" + dateFormat.format(Date_Ijin_absen1.getDate()) + "' AND '" + dateFormat.format(Date_Ijin_absen2.getDate()) + "' ";
            }
            sql = "SELECT * FROM `tb_cuti` "
                    + "LEFT JOIN `tb_karyawan` ON `tb_cuti`.`pengganti` = `tb_karyawan`.`id_pegawai` "
                    + "WHERE `tb_cuti`.`id_pegawai` = '" + Table_karyawan.getValueAt(i, 0) + "'"
                    + filter_tanggal;
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[10];
            while (rs.next()) {
                row[0] = rs.getString("kode_cuti");
                row[1] = rs.getString("id_pegawai");
                row[2] = rs.getDate("tanggal_cuti");
                row[3] = rs.getString("jenis_cuti");
                row[4] = rs.getString("kategori_cuti");
                row[5] = rs.getString("keterangan");
                row[6] = rs.getString("pengganti");
                row[7] = rs.getString("nama_pegawai");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_Cuti_per_karyawan);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Absen_Cuti.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_keseluruhan() {
        try {
            DefaultTableModel model = (DefaultTableModel) Table_Data_Cuti.getModel();
            model.setRowCount(0);

            String bagian = "";
            if (txt_search_bagian.getText() != null && !txt_search_bagian.getText().equals("")) {
                bagian = "AND `tb_bagian`.`nama_bagian` LIKE '%" + txt_search_bagian.getText() + "%' ";
            }

            String kelamin = "";
            if (!"All".equals(ComboBox_kelamin1.getSelectedItem().toString())) {
                kelamin = "AND A.`jenis_kelamin` = '" + ComboBox_kelamin1.getSelectedItem().toString() + "' ";
            }
            String status = "";
            if (!"All".equals(ComboBox_status1.getSelectedItem().toString())) {
                status = "AND A.`status` = '" + ComboBox_status1.getSelectedItem().toString() + "' ";
            }
            String posisi = "";
            if (!"All".equals(ComboBox_posisi1.getSelectedItem().toString())) {
                posisi = "AND A.`posisi` = '" + ComboBox_posisi1.getSelectedItem().toString() + "' ";
            }

            String filter_tanggal = "";
            String filter_tanggal_absen = "";
            if (Date_Cuti_keseluruhan1.getDate() != null && Date_Cuti_keseluruhan2.getDate() != null) {
                filter_tanggal = "AND `tb_cuti`.`tanggal_cuti` BETWEEN '" + dateFormat.format(Date_Cuti_keseluruhan1.getDate()) + "' AND '" + dateFormat.format(Date_Cuti_keseluruhan2.getDate()) + "' ";
                filter_tanggal_absen = "DATE(`scan_date`) BETWEEN '" + dateFormat.format(Date_Cuti_keseluruhan1.getDate()) + "' AND '" + dateFormat.format(Date_Cuti_keseluruhan2.getDate()) + "' ";
            }

            Map<String, String> data_absen = new HashMap<>();
            String query = "SELECT `att_log`.`pin`, DATE(`scan_date`) AS 'tanggal', `scan_date` FROM `att_log` "
                    + "WHERE " + filter_tanggal_absen;
            ResultSet result = Utility.db.getStatement().executeQuery(query);
            while (result.next()) {
                data_absen.put(String.format("%05d", result.getInt("pin")) + result.getString("tanggal"), result.getString("scan_date"));
            }

            sql = "SELECT `kode_cuti`, `tb_cuti`.`id_pegawai`, A.`nama_pegawai`, A.`posisi`, `tb_bagian`.`nama_bagian`, `tanggal_cuti`, `kategori_cuti`, `jenis_cuti`, `tb_cuti`.`keterangan`, `pengganti`, B.`nama_pegawai` AS 'nama_pengganti' "
                    + "FROM `tb_cuti` "
                    + "LEFT JOIN `tb_karyawan` A ON `tb_cuti`.`id_pegawai` = A.`id_pegawai` "
                    + "LEFT JOIN `tb_bagian` ON A.`kode_bagian` = `tb_bagian`.`kode_bagian` "
                    + "LEFT JOIN `tb_karyawan` B ON `tb_cuti`.`pengganti` = B.`id_pegawai` "
                    + "WHERE A.`nama_pegawai` LIKE '%" + txt_search_karyawan1.getText() + "%' "
                    + bagian
                    + kelamin
                    + status
                    + posisi
                    + filter_tanggal;
            pst = Utility.db.getConnection().prepareStatement(sql);
            rs = pst.executeQuery();
            Object[] row = new Object[15];
            while (rs.next()) {
                row[0] = rs.getString("kode_cuti");
                row[1] = rs.getString("nama_pegawai");
                row[2] = rs.getString("nama_bagian");
                row[3] = rs.getString("posisi");
                row[4] = rs.getDate("tanggal_cuti");
                row[5] = 1;
                row[6] = rs.getString("jenis_cuti");
                row[7] = rs.getString("kategori_cuti");
                row[8] = rs.getString("keterangan");
                row[9] = rs.getString("pengganti");
                row[10] = rs.getString("nama_pengganti");
                row[11] = data_absen.get(rs.getString("id_pegawai").substring(6) + rs.getDate("tanggal_cuti"));
                if (ComboBox_realisasi_cuti.getSelectedItem().toString().equals("BATAL")) {
                    if (data_absen.get(rs.getString("id_pegawai").substring(6) + rs.getDate("tanggal_cuti")) != null) {
                        model.addRow(row);
                    }
                } else {
                    model.addRow(row);
                }
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_Data_Cuti);
            String total_data = Integer.toString(Table_Data_Cuti.getRowCount());
            label_total_data_absen_keseluruhan.setText(total_data);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Absen_Cuti.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_pengajuan_cuti() {
        try {
            DefaultTableModel model = (DefaultTableModel) Table_pengajuan.getModel();
            model.setRowCount(0);
            String bagian = ComboBox_bagian_pengajuan.getSelectedItem().toString();
            if ("All".equals(ComboBox_bagian_pengajuan.getSelectedItem().toString())) {
                bagian = "";
            }

            String departemen = ComboBox_departemen_pengajuan.getSelectedItem().toString();
            if ("All".equals(ComboBox_departemen_pengajuan.getSelectedItem().toString())) {
                departemen = "";
            }

            String kelamin = "AND A.`jenis_kelamin` = '" + ComboBox_kelamin_pengajuan.getSelectedItem().toString() + "' ";
            if ("All".equals(ComboBox_kelamin_pengajuan.getSelectedItem().toString())) {
                kelamin = "";
            }

            String status = "AND A.`status` = '" + ComboBox_status_pengajuan.getSelectedItem().toString() + "' ";
            if ("All".equals(ComboBox_status_pengajuan.getSelectedItem().toString())) {
                status = "";
            }

            String posisi = "AND A.`posisi` = '" + ComboBox_posisi_pengajuan.getSelectedItem().toString() + "' ";
            if ("All".equals(ComboBox_posisi_pengajuan.getSelectedItem().toString())) {
                posisi = "";
            }
            String filter_tanggal = "";
            if (Date_pengajuan1.getDate() != null && Date_pengajuan2.getDate() != null) {
                filter_tanggal = "AND `tgl_pengajuan` BETWEEN '" + dateFormat.format(Date_pengajuan1.getDate()) + "' AND '" + dateFormat.format(Date_pengajuan2.getDate()) + "' ";
            }
            sql = "SELECT `kode_pengajuan`, `tgl_input_pengajuan_cuti`, `tb_cuti_pengajuan`.`id_pegawai`, A.`nama_pegawai`, `tb_bagian`.`nama_bagian`, A.`posisi`, `tgl_pengajuan`, "
                    + "`jumlah_hari`, `jenis_cuti`, `kategori_cuti`, `tb_cuti_pengajuan`.`keterangan`, B.`nama_pegawai` AS 'pengganti', `disetujui`, `diketahui`, `dibatalkan` \n"
                    + "FROM `tb_cuti_pengajuan` \n"
                    + "LEFT JOIN `tb_karyawan` A ON `tb_cuti_pengajuan`.`id_pegawai` = A.`id_pegawai`\n"
                    + "LEFT JOIN `tb_bagian` ON A.`kode_bagian` = `tb_bagian`.`kode_bagian` "
                    + "LEFT JOIN `tb_karyawan` B ON `tb_cuti_pengajuan`.`pengganti` = B.`id_pegawai` "
                    + "WHERE A.`nama_pegawai` LIKE '%" + txt_search_karyawan_pengajuan.getText() + "%' "
                    + "AND `tb_bagian`.`nama_bagian` LIKE '%" + bagian + "%' "
                    + "AND `tb_bagian`.`kode_departemen` LIKE '%" + departemen + "%' "
                    + kelamin
                    + status
                    + posisi
                    + filter_tanggal
                    + "ORDER BY `tgl_pengajuan` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[15];
            while (rs.next()) {
                row[0] = rs.getInt("kode_pengajuan");
                row[1] = rs.getString("nama_pegawai");
                row[2] = rs.getString("nama_bagian");
                row[3] = rs.getString("posisi");
                row[4] = rs.getDate("tgl_input_pengajuan_cuti");
                row[5] = rs.getDate("tgl_pengajuan");
                row[6] = rs.getInt("jumlah_hari");
                row[7] = rs.getString("jenis_cuti");
                row[8] = rs.getString("kategori_cuti");
                row[9] = rs.getString("keterangan");
                row[10] = rs.getString("pengganti");
                row[11] = rs.getString("disetujui");
                row[12] = rs.getString("diketahui");
                row[13] = rs.getString("dibatalkan");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_pengajuan);
            String total_data = Integer.toString(Table_pengajuan.getRowCount());
            label_total_data_pengajuan.setText(total_data);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Absen_Cuti.class.getName()).log(Level.SEVERE, null, ex);
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
        jPanel4 = new javax.swing.JPanel();
        jPanel_search_karyawan2 = new javax.swing.JPanel();
        txt_search_karyawan_pengajuan = new javax.swing.JTextField();
        button_refresh_data_pengajuan = new javax.swing.JButton();
        jLabel20 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        ComboBox_departemen_pengajuan = new javax.swing.JComboBox<>();
        ComboBox_bagian_pengajuan = new javax.swing.JComboBox<>();
        Date_pengajuan1 = new com.toedter.calendar.JDateChooser();
        Date_pengajuan2 = new com.toedter.calendar.JDateChooser();
        jLabel25 = new javax.swing.JLabel();
        ComboBox_kelamin_pengajuan = new javax.swing.JComboBox<>();
        ComboBox_status_pengajuan = new javax.swing.JComboBox<>();
        jLabel26 = new javax.swing.JLabel();
        ComboBox_posisi_pengajuan = new javax.swing.JComboBox<>();
        jLabel27 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        Table_pengajuan = new javax.swing.JTable();
        jLabel28 = new javax.swing.JLabel();
        label_total_data_pengajuan = new javax.swing.JLabel();
        button_diketahui_pengajuan = new javax.swing.JButton();
        button_disetujui_pengajuan = new javax.swing.JButton();
        button_export_pengajuan = new javax.swing.JButton();
        button_add_ijin_cuti = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        Table_Data_Cuti = new javax.swing.JTable();
        button_delete_ijin_cuti = new javax.swing.JButton();
        jLabel21 = new javax.swing.JLabel();
        label_total_data_absen_keseluruhan = new javax.swing.JLabel();
        button_edit_ijin_cuti = new javax.swing.JButton();
        button_export1 = new javax.swing.JButton();
        ComboBox_posisi1 = new javax.swing.JComboBox<>();
        jLabel37 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        ComboBox_realisasi_cuti = new javax.swing.JComboBox<>();
        Date_Cuti_keseluruhan1 = new com.toedter.calendar.JDateChooser();
        Date_Cuti_keseluruhan2 = new com.toedter.calendar.JDateChooser();
        txt_search_karyawan1 = new javax.swing.JTextField();
        txt_search_bagian = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        button_refresh_data_keseluruhan = new javax.swing.JButton();
        ComboBox_kelamin1 = new javax.swing.JComboBox<>();
        jLabel15 = new javax.swing.JLabel();
        ComboBox_status1 = new javax.swing.JComboBox<>();
        jLabel19 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        label_nama = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        Table_karyawan = new javax.swing.JTable();
        label_total_ijin = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        label_total_absen = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        Table_Cuti_per_karyawan = new javax.swing.JTable();
        label_total_sakit = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        label_total_cuti_tahunan = new javax.swing.JLabel();
        button_export = new javax.swing.JButton();
        Date_Ijin_absen1 = new com.toedter.calendar.JDateChooser();
        Date_Ijin_absen2 = new com.toedter.calendar.JDateChooser();
        txt_search_karyawan = new javax.swing.JTextField();
        txt_search_bagian_rekap = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        button_refresh_data_cuti = new javax.swing.JButton();
        ComboBox_posisi = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        ComboBox_status_karyawan = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        ComboBox_departemen = new javax.swing.JComboBox<>();

        setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Data Cuti & Absen Karyawan", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N
        jPanel1.setPreferredSize(new java.awt.Dimension(1366, 652));

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        jPanel_search_karyawan2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_search_karyawan2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));

        txt_search_karyawan_pengajuan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_karyawan_pengajuan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_karyawan_pengajuanKeyPressed(evt);
            }
        });

        button_refresh_data_pengajuan.setBackground(new java.awt.Color(255, 255, 255));
        button_refresh_data_pengajuan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_refresh_data_pengajuan.setText("Search");
        button_refresh_data_pengajuan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refresh_data_pengajuanActionPerformed(evt);
            }
        });

        jLabel20.setBackground(new java.awt.Color(255, 255, 255));
        jLabel20.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel20.setText("Nama Karyawan :");

        jLabel23.setBackground(new java.awt.Color(255, 255, 255));
        jLabel23.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel23.setText("Departemen :");

        jLabel24.setBackground(new java.awt.Color(255, 255, 255));
        jLabel24.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel24.setText("Bagian :");

        jLabel38.setBackground(new java.awt.Color(255, 255, 255));
        jLabel38.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        jLabel38.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel38.setText("-");

        jLabel39.setBackground(new java.awt.Color(255, 255, 255));
        jLabel39.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        jLabel39.setText("Tanggal Mulai");

        ComboBox_departemen_pengajuan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_departemen_pengajuan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));
        ComboBox_departemen_pengajuan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ComboBox_departemen_pengajuanActionPerformed(evt);
            }
        });

        ComboBox_bagian_pengajuan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_bagian_pengajuan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        Date_pengajuan1.setBackground(new java.awt.Color(255, 255, 255));
        Date_pengajuan1.setToolTipText("");
        Date_pengajuan1.setDateFormatString("dd MMMM yyyy");
        Date_pengajuan1.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        Date_pengajuan1.setMinSelectableDate(new java.util.Date(1483207315000L));

        Date_pengajuan2.setBackground(new java.awt.Color(255, 255, 255));
        Date_pengajuan2.setDateFormatString("dd MMMM yyyy");
        Date_pengajuan2.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N

        jLabel25.setBackground(new java.awt.Color(255, 255, 255));
        jLabel25.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel25.setText("Jenis Kelamin  :");

        ComboBox_kelamin_pengajuan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_kelamin_pengajuan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Laki-Laki", "Perempuan" }));

        ComboBox_status_pengajuan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_status_pengajuan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "IN", "OUT" }));

        jLabel26.setBackground(new java.awt.Color(255, 255, 255));
        jLabel26.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel26.setText("Status  :");

        ComboBox_posisi_pengajuan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_posisi_pengajuan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        jLabel27.setBackground(new java.awt.Color(255, 255, 255));
        jLabel27.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel27.setText("Posisi :");

        javax.swing.GroupLayout jPanel_search_karyawan2Layout = new javax.swing.GroupLayout(jPanel_search_karyawan2);
        jPanel_search_karyawan2.setLayout(jPanel_search_karyawan2Layout);
        jPanel_search_karyawan2Layout.setHorizontalGroup(
            jPanel_search_karyawan2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_search_karyawan2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_search_karyawan2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txt_search_karyawan_pengajuan)
                    .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_search_karyawan2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel23, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                    .addComponent(ComboBox_departemen_pengajuan, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_search_karyawan2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel24, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                    .addComponent(ComboBox_bagian_pengajuan, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_search_karyawan2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel25)
                    .addComponent(ComboBox_kelamin_pengajuan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_search_karyawan2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel26)
                    .addComponent(ComboBox_status_pengajuan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel_search_karyawan2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel27)
                    .addGroup(jPanel_search_karyawan2Layout.createSequentialGroup()
                        .addComponent(ComboBox_posisi_pengajuan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_refresh_data_pengajuan)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 312, Short.MAX_VALUE)
                .addGroup(jPanel_search_karyawan2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel39)
                    .addGroup(jPanel_search_karyawan2Layout.createSequentialGroup()
                        .addComponent(Date_pengajuan1, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_pengajuan2, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel_search_karyawan2Layout.setVerticalGroup(
            jPanel_search_karyawan2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_search_karyawan2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_search_karyawan2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel_search_karyawan2Layout.createSequentialGroup()
                        .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_search_karyawan2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(button_refresh_data_pengajuan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ComboBox_posisi_pengajuan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel_search_karyawan2Layout.createSequentialGroup()
                        .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_status_pengajuan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_search_karyawan2Layout.createSequentialGroup()
                        .addGroup(jPanel_search_karyawan2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_search_karyawan2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_search_karyawan2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_search_karyawan2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(ComboBox_bagian_pengajuan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(ComboBox_kelamin_pengajuan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(ComboBox_departemen_pengajuan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Date_pengajuan1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Date_pengajuan2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_search_karyawan_pengajuan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        Table_pengajuan.setAutoCreateRowSorter(true);
        Table_pengajuan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Table_pengajuan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode Pengajuan", "Nama", "Bagian", "Posisi", "Tgl Pengajuan", "Tgl Mulai Cuti", "Hari", "Jenis Cuti", "Ketegori", "Keterangan", "Pengganti", "Disetujui", "Diketahui", "Dibatalkan"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
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
        Table_pengajuan.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(Table_pengajuan);
        if (Table_pengajuan.getColumnModel().getColumnCount() > 0) {
            Table_pengajuan.getColumnModel().getColumn(0).setMinWidth(0);
            Table_pengajuan.getColumnModel().getColumn(0).setPreferredWidth(0);
            Table_pengajuan.getColumnModel().getColumn(0).setMaxWidth(0);
        }

        jLabel28.setBackground(new java.awt.Color(255, 255, 255));
        jLabel28.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel28.setText("TOTAL DATA :");

        label_total_data_pengajuan.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_pengajuan.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_data_pengajuan.setText("0");

        button_diketahui_pengajuan.setBackground(new java.awt.Color(255, 255, 255));
        button_diketahui_pengajuan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_diketahui_pengajuan.setText("Diketahui");
        button_diketahui_pengajuan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_diketahui_pengajuanActionPerformed(evt);
            }
        });

        button_disetujui_pengajuan.setBackground(new java.awt.Color(255, 255, 255));
        button_disetujui_pengajuan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_disetujui_pengajuan.setText("Disetujui");
        button_disetujui_pengajuan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_disetujui_pengajuanActionPerformed(evt);
            }
        });

        button_export_pengajuan.setBackground(new java.awt.Color(255, 255, 255));
        button_export_pengajuan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_pengajuan.setText("Export to Excel");
        button_export_pengajuan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_pengajuanActionPerformed(evt);
            }
        });

        button_add_ijin_cuti.setBackground(new java.awt.Color(255, 255, 255));
        button_add_ijin_cuti.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_add_ijin_cuti.setText("Tambah Pengajuan Cuti");
        button_add_ijin_cuti.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_add_ijin_cutiActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel_search_karyawan2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane3)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(button_add_ijin_cuti)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_disetujui_pengajuan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_diketahui_pengajuan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_export_pengajuan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel28)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_data_pengajuan)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel_search_karyawan2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel28)
                    .addComponent(label_total_data_pengajuan)
                    .addComponent(button_diketahui_pengajuan)
                    .addComponent(button_disetujui_pengajuan)
                    .addComponent(button_export_pengajuan)
                    .addComponent(button_add_ijin_cuti))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 646, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("DATA PENGAJUAN CUTI", jPanel4);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        Table_Data_Cuti.setAutoCreateRowSorter(true);
        Table_Data_Cuti.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Table_Data_Cuti.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "Nama", "Bagian", "Posisi", "Tgl Ijin", "Hari", "Jenis", "Ijin", "Keterangan", "ID Pengganti", "Pengganti", "Realisasi Absen"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
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
        Table_Data_Cuti.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(Table_Data_Cuti);
        if (Table_Data_Cuti.getColumnModel().getColumnCount() > 0) {
            Table_Data_Cuti.getColumnModel().getColumn(0).setMinWidth(0);
            Table_Data_Cuti.getColumnModel().getColumn(0).setPreferredWidth(0);
            Table_Data_Cuti.getColumnModel().getColumn(0).setMaxWidth(0);
        }

        button_delete_ijin_cuti.setBackground(new java.awt.Color(255, 255, 255));
        button_delete_ijin_cuti.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_delete_ijin_cuti.setText("Delete");
        button_delete_ijin_cuti.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_delete_ijin_cutiActionPerformed(evt);
            }
        });

        jLabel21.setBackground(new java.awt.Color(255, 255, 255));
        jLabel21.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel21.setText("TOTAL DATA :");

        label_total_data_absen_keseluruhan.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_absen_keseluruhan.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_data_absen_keseluruhan.setText("0");

        button_edit_ijin_cuti.setBackground(new java.awt.Color(255, 255, 255));
        button_edit_ijin_cuti.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_edit_ijin_cuti.setText("Edit");
        button_edit_ijin_cuti.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_edit_ijin_cutiActionPerformed(evt);
            }
        });

        button_export1.setBackground(new java.awt.Color(255, 255, 255));
        button_export1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export1.setText("Export to Excel");
        button_export1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export1ActionPerformed(evt);
            }
        });

        ComboBox_posisi1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_posisi1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        jLabel37.setBackground(new java.awt.Color(255, 255, 255));
        jLabel37.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        jLabel37.setText("Tgl Cuti :");

        jLabel22.setBackground(new java.awt.Color(255, 255, 255));
        jLabel22.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel22.setText("Posisi :");

        jLabel29.setBackground(new java.awt.Color(255, 255, 255));
        jLabel29.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel29.setText("Realisasi :");

        ComboBox_realisasi_cuti.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_realisasi_cuti.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "BATAL" }));

        Date_Cuti_keseluruhan1.setBackground(new java.awt.Color(255, 255, 255));
        Date_Cuti_keseluruhan1.setToolTipText("");
        Date_Cuti_keseluruhan1.setDateFormatString("dd MMMM yyyy");
        Date_Cuti_keseluruhan1.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        Date_Cuti_keseluruhan1.setMinSelectableDate(new java.util.Date(1483207315000L));

        Date_Cuti_keseluruhan2.setBackground(new java.awt.Color(255, 255, 255));
        Date_Cuti_keseluruhan2.setDateFormatString("dd MMMM yyyy");
        Date_Cuti_keseluruhan2.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N

        txt_search_karyawan1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_karyawan1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_karyawan1KeyPressed(evt);
            }
        });

        txt_search_bagian.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_bagian.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_bagianKeyPressed(evt);
            }
        });

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel18.setText("Jenis Kelamin  :");

        button_refresh_data_keseluruhan.setBackground(new java.awt.Color(255, 255, 255));
        button_refresh_data_keseluruhan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_refresh_data_keseluruhan.setText("Search");
        button_refresh_data_keseluruhan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refresh_data_keseluruhanActionPerformed(evt);
            }
        });

        ComboBox_kelamin1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_kelamin1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Laki-Laki", "Perempuan" }));

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel15.setText("Nama :");

        ComboBox_status1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_status1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "IN", "OUT" }));

        jLabel19.setBackground(new java.awt.Color(255, 255, 255));
        jLabel19.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel19.setText("Status  :");

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel17.setText("Bagian :");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_karyawan1, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel17)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_bagian, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel18)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_kelamin1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel19)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_status1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel22)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_posisi1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel29)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_realisasi_cuti, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel37)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_Cuti_keseluruhan1, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_Cuti_keseluruhan2, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_refresh_data_keseluruhan))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(button_edit_ijin_cuti)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_delete_ijin_cuti)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_export1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel21)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_data_absen_keseluruhan)))
                        .addGap(0, 136, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(button_refresh_data_keseluruhan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_search_karyawan1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_search_bagian, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(ComboBox_realisasi_cuti, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(ComboBox_posisi1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(ComboBox_status1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(ComboBox_kelamin1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(Date_Cuti_keseluruhan1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_Cuti_keseluruhan2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(button_delete_ijin_cuti)
                    .addComponent(jLabel21)
                    .addComponent(label_total_data_absen_keseluruhan)
                    .addComponent(button_edit_ijin_cuti)
                    .addComponent(button_export1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 695, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("DATA CUTI", jPanel3);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        label_nama.setBackground(new java.awt.Color(255, 255, 255));
        label_nama.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_nama.setForeground(new java.awt.Color(0, 0, 204));
        label_nama.setText("NAMA PEGAWAI");

        Table_karyawan.setAutoCreateRowSorter(true);
        Table_karyawan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Table_karyawan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Pegawai", "Nama Pegawai", "Posisi", "Bagian", "Status", "Tgl Masuk", "Cuti Khusus", "Cuti Sakit", "Cuti Tahunan", "Absen", "Jatah Cuti", "Terpakai", "Sisa Cuti"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class
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
        Table_karyawan.getTableHeader().setReorderingAllowed(false);
        jScrollPane5.setViewportView(Table_karyawan);

        label_total_ijin.setBackground(new java.awt.Color(255, 255, 255));
        label_total_ijin.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_ijin.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        label_total_ijin.setText("0");

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel13.setText("Total Sakit :");

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("Total Cuti Khusus :");

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel10.setText("DATA IJIN CUTI / ABSEN");

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel9.setText("DAFTAR KARYAWAN WALETA");

        label_total_absen.setBackground(new java.awt.Color(255, 255, 255));
        label_total_absen.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_absen.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        label_total_absen.setText("0");

        Table_Cuti_per_karyawan.setAutoCreateRowSorter(true);
        Table_Cuti_per_karyawan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Table_Cuti_per_karyawan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "ID Pegawai", "Tgl Ijin", "Jenis", "Ijin", "Keterangan", "ID Pengganti", "Pengganti"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
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
        jScrollPane1.setViewportView(Table_Cuti_per_karyawan);

        label_total_sakit.setBackground(new java.awt.Color(255, 255, 255));
        label_total_sakit.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_sakit.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        label_total_sakit.setText("0");

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel5.setText("Total Absen :");

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel6.setText("Total Cuti Tahunan :");

        label_total_cuti_tahunan.setBackground(new java.awt.Color(255, 255, 255));
        label_total_cuti_tahunan.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_cuti_tahunan.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        label_total_cuti_tahunan.setText("0");

        button_export.setBackground(new java.awt.Color(255, 255, 255));
        button_export.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export.setText("Export to Excel");
        button_export.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_exportActionPerformed(evt);
            }
        });

        Date_Ijin_absen1.setBackground(new java.awt.Color(255, 255, 255));
        Date_Ijin_absen1.setToolTipText("");
        Date_Ijin_absen1.setDateFormatString("dd MMMM yyyy");
        Date_Ijin_absen1.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        Date_Ijin_absen1.setMinSelectableDate(new java.util.Date(1483207315000L));

        Date_Ijin_absen2.setBackground(new java.awt.Color(255, 255, 255));
        Date_Ijin_absen2.setDateFormatString("dd MMMM yyyy");
        Date_Ijin_absen2.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N

        txt_search_karyawan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_karyawan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_karyawanKeyPressed(evt);
            }
        });

        txt_search_bagian_rekap.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_bagian_rekap.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_bagian_rekapKeyPressed(evt);
            }
        });

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel8.setText("Posisi :");

        button_refresh_data_cuti.setBackground(new java.awt.Color(255, 255, 255));
        button_refresh_data_cuti.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_refresh_data_cuti.setText("Search");
        button_refresh_data_cuti.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refresh_data_cutiActionPerformed(evt);
            }
        });

        ComboBox_posisi.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_posisi.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "PEJUANG", "STAFF" }));

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("Nama :");

        ComboBox_status_karyawan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_status_karyawan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "IN", "OUT" }));
        ComboBox_status_karyawan.setSelectedIndex(1);

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("Departemen :");

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel11.setText("Status  :");

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel7.setText("Bagian :");

        jLabel35.setBackground(new java.awt.Color(255, 255, 255));
        jLabel35.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        jLabel35.setText("Tanggal :");

        ComboBox_departemen.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_departemen.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1331, Short.MAX_VALUE)
                    .addComponent(jScrollPane5)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_nama, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_ijin)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_sakit)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_cuti_tahunan)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_absen))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_departemen, 0, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_bagian_rekap, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_posisi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_status_karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel35)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_Ijin_absen1, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_Ijin_absen2, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_refresh_data_cuti)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_export)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(Date_Ijin_absen1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_Ijin_absen2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_bagian_rekap, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_refresh_data_cuti, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_posisi, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_status_karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_departemen, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_ijin, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_sakit, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_cuti_tahunan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_absen, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 443, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_nama, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("REKAP CUTI KARYAWAN", jPanel2);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING)
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
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 826, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void button_exportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_exportActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Table_Cuti_per_karyawan.getModel();
        ExportToExcel.writeToExcel(model, jPanel1);
    }//GEN-LAST:event_button_exportActionPerformed

    private void txt_search_karyawanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_karyawanKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_rekap_cuti_karyawan();
        }
    }//GEN-LAST:event_txt_search_karyawanKeyPressed

    private void button_refresh_data_cutiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refresh_data_cutiActionPerformed
        // TODO add your handling code here:
        refreshTable_rekap_cuti_karyawan();
        DefaultTableModel model_ijin = (DefaultTableModel) Table_Cuti_per_karyawan.getModel();
        model_ijin.setRowCount(0);
    }//GEN-LAST:event_button_refresh_data_cutiActionPerformed

    private void button_add_ijin_cutiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_add_ijin_cutiActionPerformed
        // TODO add your handling code here:
        JDialog_Add_Cuti Add = new JDialog_Add_Cuti(new javax.swing.JFrame(), true);
        Add.pack();
        Add.setLocationRelativeTo(this);
        Add.setVisible(true);
        Add.setEnabled(true);
    }//GEN-LAST:event_button_add_ijin_cutiActionPerformed

    private void button_delete_ijin_cutiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_delete_ijin_cutiActionPerformed
        // TODO add your handling code here:
        try {
            int row = Table_Data_Cuti.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih data yang ingin di hapus !");
            } else {
                int dialogResult = JOptionPane.showConfirmDialog(this, "Yakin hapus data ini?", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    // delete code here
                    String Query = "DELETE FROM `tb_cuti` WHERE `kode_cuti` = '" + Table_Data_Cuti.getValueAt(row, 0) + "'";
                    Utility.db.getStatement().executeUpdate(Query);
                    refreshTable_keseluruhan();
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_Absen_Cuti.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_delete_ijin_cutiActionPerformed

    private void txt_search_karyawan1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_karyawan1KeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_keseluruhan();
        }
    }//GEN-LAST:event_txt_search_karyawan1KeyPressed

    private void button_refresh_data_keseluruhanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refresh_data_keseluruhanActionPerformed
        // TODO add your handling code here:
        refreshTable_keseluruhan();
    }//GEN-LAST:event_button_refresh_data_keseluruhanActionPerformed

    private void button_export1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export1ActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Table_Data_Cuti.getModel();
        ExportToExcel.writeToExcel(model, jPanel1);
    }//GEN-LAST:event_button_export1ActionPerformed

    private void button_edit_ijin_cutiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_edit_ijin_cutiActionPerformed
        // TODO add your handling code here:
        int row = Table_Data_Cuti.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Silahkan pilih satu data yang akan di edit !");
        } else {
            String no = Table_Data_Cuti.getValueAt(row, 0).toString();
            JDialog_Edit_Cuti Add = new JDialog_Edit_Cuti(new javax.swing.JFrame(), true, no);
            Add.pack();
            Add.setLocationRelativeTo(this);
            Add.setVisible(true);
            Add.setEnabled(true);
            refreshTable_keseluruhan();
        }
    }//GEN-LAST:event_button_edit_ijin_cutiActionPerformed

    private void txt_search_karyawan_pengajuanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_karyawan_pengajuanKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_pengajuan_cuti();
        }
    }//GEN-LAST:event_txt_search_karyawan_pengajuanKeyPressed

    private void button_refresh_data_pengajuanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refresh_data_pengajuanActionPerformed
        // TODO add your handling code here:
        refreshTable_pengajuan_cuti();
    }//GEN-LAST:event_button_refresh_data_pengajuanActionPerformed

    private void ComboBox_departemen_pengajuanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ComboBox_departemen_pengajuanActionPerformed
        // TODO add your handling code here:
        try {
            ComboBox_bagian_pengajuan.removeAllItems();
            String query = "SELECT `nama_bagian` FROM `tb_bagian` ORDER BY `nama_bagian`";
            if (ComboBox_departemen_pengajuan.getSelectedItem() != "All") {
                query = "SELECT `nama_bagian` FROM `tb_bagian` WHERE `kode_departemen`='" + ComboBox_departemen_pengajuan.getSelectedItem() + "'";
            }
            rs = Utility.db.getStatement().executeQuery(query);
            ComboBox_bagian_pengajuan.addItem("All");
            while (rs.next()) {
                ComboBox_bagian_pengajuan.addItem(rs.getString("nama_bagian"));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Absen_Cuti.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_ComboBox_departemen_pengajuanActionPerformed

    private void button_export_pengajuanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_pengajuanActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Table_pengajuan.getModel();
        ExportToExcel.writeToExcel(model, jPanel1);
    }//GEN-LAST:event_button_export_pengajuanActionPerformed

    private void button_diketahui_pengajuanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_diketahui_pengajuanActionPerformed
        // TODO add your handling code here:
        int row = Table_pengajuan.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Silahkan pilih satu data pada tabel !");
        } else {
            String nama_cuti = Table_pengajuan.getValueAt(row, 1).toString();
            String kode_pengajuan = Table_pengajuan.getValueAt(row, 0).toString();
            int dialogResult = JOptionPane.showConfirmDialog(this, "Mengetahui pengajuan cuti " + nama_cuti + "?", "Warning", 0);
            if (dialogResult == JOptionPane.YES_OPTION) {
                try {
                    String login_departemen = "";
                    sql = "SELECT `kode_departemen` FROM `tb_bagian` WHERE `kode_bagian` = '" + MainForm.Login_kodeBagian + "'";
                    rs = Utility.db.getStatement().executeQuery(sql);
                    if (rs.next()) {
                        login_departemen = rs.getString("kode_departemen");
                    }

                    if (login_departemen.equals("HRGA")) {
                        sql = "UPDATE `tb_cuti_pengajuan` SET "
                                + "`diketahui`='DIKETAHUI OLEH: " + MainForm.Login_NamaPegawai + " " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()) + "' "
                                + "WHERE `kode_pengajuan`='" + kode_pengajuan + "'";
                        if ((Utility.db.getStatement().executeUpdate(sql)) == 1) {
                            refreshTable_pengajuan_cuti();
                            JOptionPane.showMessageDialog(this, "DIKETAHUI OLEH: " + MainForm.Login_NamaPegawai);
                        } else {
                            JOptionPane.showMessageDialog(this, "Failed!");
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Maaf hanya login user HRD yang dapat mengetahui pengajuan Cuti!");
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, ex);
                    Logger.getLogger(JPanel_Absen_Cuti.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_button_diketahui_pengajuanActionPerformed

    private void button_disetujui_pengajuanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_disetujui_pengajuanActionPerformed
        // TODO add your handling code here:
        int row = Table_pengajuan.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Silahkan pilih satu data pada tabel !");
        } else {
            String kode_pengajuan = Table_pengajuan.getValueAt(row, 0).toString();
            String nama_cuti = Table_pengajuan.getValueAt(row, 1).toString();
            String nama_bagian = Table_pengajuan.getValueAt(row, 2).toString();
            int dialogResult = JOptionPane.showConfirmDialog(this, "Menyetujui pengajuan cuti " + nama_cuti + "?", "Warning", 0);
            if (dialogResult == JOptionPane.YES_OPTION) {
                try {
                    String kode_bagian = "";
                    String query = "SELECT `kode_bagian` FROM `tb_bagian` WHERE `nama_bagian` = '" + nama_bagian + "'";
                    PreparedStatement preparedStatement = Utility.db.getConnection().prepareStatement(query);
                    ResultSet result = preparedStatement.executeQuery();
                    if (result.next()) {
                        kode_bagian = result.getString("kode_bagian");
                    }
                    ArrayList<String> Atasan = Utility.GetListAtasanFromKodeBagian(kode_bagian);
                    if (Atasan.contains(Integer.toString(MainForm.Login_kodeBagian))) {
                        sql = "UPDATE `tb_cuti_pengajuan` SET "
                                + "`disetujui`='DISETUJUI OLEH: " + MainForm.Login_NamaPegawai + " " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()) + "' "
                                + "WHERE `kode_pengajuan`='" + kode_pengajuan + "'";
                        if ((Utility.db.getStatement().executeUpdate(sql)) == 1) {
                            refreshTable_pengajuan_cuti();
                            JOptionPane.showMessageDialog(this, "DISETUJUI OLEH: " + MainForm.Login_NamaPegawai);
                        } else {
                            JOptionPane.showMessageDialog(this, "Failed!");
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Maaf hanya atasan dari " + nama_cuti + " yang dapat menyetujui pengajuan Cuti!");
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex);
                    Logger.getLogger(JPanel_Absen_Cuti.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_button_disetujui_pengajuanActionPerformed

    private void txt_search_bagianKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_bagianKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_keseluruhan();
        }
    }//GEN-LAST:event_txt_search_bagianKeyPressed

    private void txt_search_bagian_rekapKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_bagian_rekapKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_keseluruhan();
        }
    }//GEN-LAST:event_txt_search_bagian_rekapKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_bagian_pengajuan;
    private javax.swing.JComboBox<String> ComboBox_departemen;
    private javax.swing.JComboBox<String> ComboBox_departemen_pengajuan;
    private javax.swing.JComboBox<String> ComboBox_kelamin1;
    private javax.swing.JComboBox<String> ComboBox_kelamin_pengajuan;
    private javax.swing.JComboBox<String> ComboBox_posisi;
    private javax.swing.JComboBox<String> ComboBox_posisi1;
    private javax.swing.JComboBox<String> ComboBox_posisi_pengajuan;
    private javax.swing.JComboBox<String> ComboBox_realisasi_cuti;
    private javax.swing.JComboBox<String> ComboBox_status1;
    private javax.swing.JComboBox<String> ComboBox_status_karyawan;
    private javax.swing.JComboBox<String> ComboBox_status_pengajuan;
    private com.toedter.calendar.JDateChooser Date_Cuti_keseluruhan1;
    private com.toedter.calendar.JDateChooser Date_Cuti_keseluruhan2;
    private com.toedter.calendar.JDateChooser Date_Ijin_absen1;
    private com.toedter.calendar.JDateChooser Date_Ijin_absen2;
    private com.toedter.calendar.JDateChooser Date_pengajuan1;
    private com.toedter.calendar.JDateChooser Date_pengajuan2;
    public javax.swing.JTable Table_Cuti_per_karyawan;
    public javax.swing.JTable Table_Data_Cuti;
    public static javax.swing.JTable Table_karyawan;
    public javax.swing.JTable Table_pengajuan;
    private javax.swing.JButton button_add_ijin_cuti;
    private javax.swing.JButton button_delete_ijin_cuti;
    private javax.swing.JButton button_diketahui_pengajuan;
    private javax.swing.JButton button_disetujui_pengajuan;
    private javax.swing.JButton button_edit_ijin_cuti;
    private javax.swing.JButton button_export;
    private javax.swing.JButton button_export1;
    private javax.swing.JButton button_export_pengajuan;
    public static javax.swing.JButton button_refresh_data_cuti;
    public static javax.swing.JButton button_refresh_data_keseluruhan;
    public static javax.swing.JButton button_refresh_data_pengajuan;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
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
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel_search_karyawan2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel label_nama;
    private javax.swing.JLabel label_total_absen;
    private javax.swing.JLabel label_total_cuti_tahunan;
    private javax.swing.JLabel label_total_data_absen_keseluruhan;
    private javax.swing.JLabel label_total_data_pengajuan;
    private javax.swing.JLabel label_total_ijin;
    private javax.swing.JLabel label_total_sakit;
    private javax.swing.JTextField txt_search_bagian;
    private javax.swing.JTextField txt_search_bagian_rekap;
    private javax.swing.JTextField txt_search_karyawan;
    private javax.swing.JTextField txt_search_karyawan1;
    private javax.swing.JTextField txt_search_karyawan_pengajuan;
    // End of variables declaration//GEN-END:variables

}
