package waleta_system.Finance;

import waleta_system.Class.Utility;
import waleta_system.HRD.*;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import waleta_system.BahanJadi.JPanel_BoxBahanJadi;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.ExportToExcel;
import waleta_system.MainForm;

public class JPanel_Absen_Keuangan extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    PreparedStatement pst;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Date today = new Date();
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    String lihat_absen_staf = "";

    public JPanel_Absen_Keuangan() {
        initComponents();
    }

    public void init() {
        try {
            try {
                lihat_absen_staf = " AND `posisi` NOT IN ('STAFF', 'STAFF 5', 'STAFF 6')";
                if (MainForm.Login_idPegawai.equals("20171201644")//indri
                        || MainForm.Login_idPegawai.equals("20230907768")//diyan
                        || MainForm.Login_idPegawai.equals("20180102221")//bastian
                        || MainForm.Login_idPegawai.equals("20161200110")//yani
                        || MainForm.Login_idPegawai.equals("20170100225")//priska07
                        || MainForm.Login_idPegawai.equals("2")//johan.hartono07
                        || MainForm.Login_idPegawai.equals("20170300469")//Martin
                        || MainForm.Login_idPegawai.equals("20211006504")) {//veny019
                    lihat_absen_staf = "";
                }

                sql = "SELECT DISTINCT(`posisi`) AS 'posisi' FROM `tb_karyawan` WHERE `status` = 'IN' AND `posisi` IS NOT NULL";
                rs = Utility.db.getStatement().executeQuery(sql);
                while (rs.next()) {
                    ComboBox_posisi.addItem(rs.getString("posisi"));
                    ComboBox_posisi2.addItem(rs.getString("posisi"));
                    ComboBox_posisi_bin.addItem(rs.getString("posisi"));
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, e);
                Logger.getLogger(JPanel_BoxBahanJadi.class.getName()).log(Level.SEVERE, null, e);
            }
            tabel_data_absen.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent event) {
                    if (!event.getValueIsAdjusting() && tabel_data_absen.getSelectedRow() != -1) {
                        int i = tabel_data_absen.getSelectedRow();
                    }
                }
            });

            refreshTable_absen();
            refreshTable_absen_recycle_bin();
        } catch (Exception ex) {
            Logger.getLogger(JPanel_BoxBahanJadi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_absen() {
        try {
            DefaultTableModel model = (DefaultTableModel) tabel_data_absen.getModel();
            model.setRowCount(0);

            String karyawan = "AND `tb_karyawan`.`nama_pegawai` LIKE '%" + txt_search_NamaKaryawan.getText() + "%'";
            if (txt_search_NamaKaryawan.getText() == null || txt_search_NamaKaryawan.getText().equals("")) {
                karyawan = "";
            }

            String bagian = "AND `tb_bagian`.`nama_bagian` LIKE '%" + txt_search_bagian.getText() + "%' ";
            if (txt_search_bagian.getText() == null || txt_search_bagian.getText().equals("")) {
                bagian = "";
            }

            String posisi = "AND `posisi` = '" + ComboBox_posisi.getSelectedItem().toString() + "' ";
            if ("All".equals(ComboBox_posisi.getSelectedItem().toString())) {
                posisi = "";
            }

            String filter_tanggal = "";
            if (Date_Search1.getDate() != null && Date_Search2.getDate() != null) {
                filter_tanggal = "AND (DATE(`scan_date`) BETWEEN '" + dateFormat.format(Date_Search1.getDate()) + "' AND '" + dateFormat.format(Date_Search2.getDate()) + "')";
            }

            sql = "SELECT `att_log`.`pin`, `tb_karyawan`.`id_pegawai`, `tb_karyawan`.`nama_pegawai`,`tb_bagian`.`nama_bagian`, `tb_bagian`.`kode_departemen`, `posisi`, "
                    + "DATE(`scan_date`) AS 'tanggal', TIME(`scan_date`) AS 'waktu', `att_log`.`sn`, `att_mesin_finger`.`nama_mesin`, `verifymode`, "
                    + "`tb_surat_lembur`.`nomor_surat`, `jumlah_jam`, `tanggal_masuk`, `tb_surat_lembur_detail`.`mulai_lembur`\n"
                    + "FROM `att_log` "
                    + "LEFT JOIN `att_mesin_finger` ON `att_log`.`sn` = `att_mesin_finger`.`sn`\n"
                    + "LEFT JOIN `tb_karyawan` ON `att_log`.`pin` = `tb_karyawan`.`pin_finger`\n"
                    + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`"
                    + "LEFT JOIN `tb_surat_lembur_detail` ON `tb_surat_lembur_detail`.`id_pegawai` = `tb_karyawan`.`id_pegawai` AND DATE(`att_log`.`scan_date`) = `tb_surat_lembur_detail`.`tanggal_lembur`\n"
                    + "LEFT JOIN `tb_surat_lembur` ON `tb_surat_lembur_detail`.`nomor_surat` = `tb_surat_lembur`.`nomor_surat`\n"
                    + "WHERE `pin` LIKE '%" + txt_search_pin.getText() + "%' "
                    + lihat_absen_staf
                    + karyawan
                    + bagian
                    + posisi
                    + filter_tanggal
                    + " ORDER BY `tb_karyawan`.`nama_pegawai` ASC, `scan_date` ASC";

            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[15];
            while (rs.next()) {
                row[0] = rs.getString("pin");
                row[1] = rs.getString("id_pegawai");
                row[2] = rs.getString("nama_pegawai");
                row[3] = rs.getString("nama_bagian");
                row[4] = rs.getString("kode_departemen");
                row[5] = rs.getString("posisi");
                row[6] = rs.getDate("tanggal");
                row[7] = new SimpleDateFormat("HH:mm:ss").format(rs.getTime("waktu"));
                switch (rs.getInt("verifymode")) {
                    case 1:
                        row[8] = "Fingerprint Scan";
                        break;
                    case 20:
                        row[8] = "Face Scan";
                        break;
                    default:
                        row[8] = "Unidentified";
                        break;
                }
                row[9] = rs.getString("sn");
                row[10] = rs.getString("nama_mesin");
                row[11] = rs.getString("nomor_surat");
                row[12] = rs.getInt("jumlah_jam");
                row[13] = rs.getString("mulai_lembur");

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
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_BoxBahanJadi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_TidakMasuk() {
        try {
            Connection con = Utility.db.getConnection();
            DefaultTableModel model = (DefaultTableModel) tabel_data_TidakMasuk.getModel();
            model.setRowCount(0);

            String karyawan = "AND `tb_karyawan`.`nama_pegawai` LIKE '%" + txt_search_NamaKaryawan2.getText() + "%'";
            if (txt_search_NamaKaryawan2.getText() == null || txt_search_NamaKaryawan2.getText().equals("")) {
                karyawan = "";
            }

            String bagian = "AND `tb_bagian`.`nama_bagian` LIKE '%" + txt_search_bagian2.getText() + "%' ";
            if (txt_search_bagian2.getText() == null || txt_search_bagian2.getText().equals("")) {
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
                    String nama_hari = new SimpleDateFormat("EEEE").format(date);
                    boolean filter_hari = false;
                    if (CheckBox_seninJumat.isSelected() && (nama_hari.equals("Monday") || nama_hari.equals("Tuesday") || nama_hari.equals("Wednesday") || nama_hari.equals("Thursday") || nama_hari.equals("Friday"))) {
                        filter_hari = true;
                    } else if (CheckBox_sabtu.isSelected() && nama_hari.equals("Saturday")) {
                        filter_hari = true;
                    } else if (CheckBox_minggu.isSelected() && nama_hari.equals("Sunday")) {
                        filter_hari = true;
                    } else {
                        //Tidak di tampilkan
                    }
                    if (filter_hari) {
                        sql = "SELECT `id_pegawai`, `pin_finger`, `nik_ktp`, `nama_pegawai`, `tb_bagian`.`nama_bagian`, `tb_bagian`.`kode_departemen`, `posisi`, `tanggal_masuk`"
                                + "FROM `tb_karyawan` "
                                + "LEFT JOIN `tb_bagian` ON `tb_bagian`.`kode_bagian` = `tb_karyawan`.`kode_bagian` "
                                + "WHERE `tanggal_masuk` <= '" + dateFormat.format(date) + "'"
                                + "AND `status` = 'IN' \n" + lihat_absen_staf
                                + "AND `pin_finger` LIKE '%" + txt_search_pin2.getText() + "%'\n"
                                + karyawan
                                + bagian + posisi;
//                    System.out.println(sql);
                        pst = con.prepareStatement(sql);
                        rs = pst.executeQuery();
                        while (rs.next()) {
                            Object[] row = new Object[20];
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
                                    + "AND HOUR(`scan_date`) BETWEEN '" + jam1 + "' AND '" + jam2 + "' "
                                    + "ORDER BY `scan_date`";
                            PreparedStatement pst1 = con.prepareStatement(b);
                            ResultSet rs1 = pst1.executeQuery();
                            if (rs1.next()) {
                                row[7] = rs1.getDate("tanggal");
                                row[8] = rs1.getTime("jam");
                            } else {
                                row[7] = "Tidak ada Data";
                                row[8] = "Tidak ada Data";
                            }

                            String c = "SELECT `kategori_cuti`, `keterangan` FROM `tb_cuti` "
                                    + "WHERE `id_pegawai` = '" + rs.getString("id_pegawai") + "' "
                                    + "AND tanggal_cuti = '" + dateFormat.format(date) + "' ";
                            PreparedStatement pst2 = con.prepareStatement(c);
                            ResultSet rs2 = pst2.executeQuery();
                            if (rs2.next()) {
                                row[9] = rs2.getString("kategori_cuti") + " : " + rs2.getString("keterangan");
                            }

                            String keterangan_hari_libur = "";
                            String query_tgl_libur = "SELECT `tanggal_libur`, `keterangan` "
                                    + "FROM `tb_libur`"
                                    + "WHERE `tanggal_libur` = '" + dateFormat.format(date) + "' ";
                            ResultSet rs_tgl_libur = Utility.db.getStatement().executeQuery(query_tgl_libur);
                            if (rs_tgl_libur.next()) {
                                keterangan_hari_libur = rs_tgl_libur.getString("keterangan");
                                row[10] = keterangan_hari_libur;
                            }

                            if (CheckBox_showAll.isSelected()) {
                                model.addRow(row);
                            } else {
                                if (row[7] == "Tidak ada Data") {
                                    model.addRow(row);
                                }
                            }
                        }
                    }
                    date = new Date(date.getTime() + (1 * 24 * 60 * 60 * 1000));
                }
            } else {
                JOptionPane.showMessageDialog(this, "Tanggal tidak boleh kosong !");
            }
            label_total_data.setText(Integer.toString(tabel_data_TidakMasuk.getRowCount()));
            ColumnsAutoSizer.sizeColumnsToFit(tabel_data_TidakMasuk);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_BoxBahanJadi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_absen_recycle_bin() {
        try {
            DefaultTableModel model = (DefaultTableModel) tabel_data_absen_bin.getModel();
            model.setRowCount(0);

            String karyawan = "AND `tb_karyawan`.`nama_pegawai` LIKE '%" + txt_search_NamaKaryawan_bin.getText() + "%'";
            if (txt_search_NamaKaryawan_bin.getText() == null || txt_search_NamaKaryawan_bin.getText().equals("")) {
                karyawan = "";
            }

            String bagian = "AND `tb_bagian`.`nama_bagian` LIKE '%" + txt_search_bagian_bin.getText() + "%' ";
            if (txt_search_bagian_bin.getText() == null || txt_search_bagian_bin.getText().equals("")) {
                bagian = "";
            }

            String posisi = "AND `posisi` = '" + ComboBox_posisi_bin.getSelectedItem().toString() + "' ";
            if ("All".equals(ComboBox_posisi_bin.getSelectedItem().toString())) {
                posisi = "";
            }

            String filter_tanggal = "";
            if (Date_Search1_bin.getDate() != null && Date_Search2_bin.getDate() != null) {
                filter_tanggal = "AND (DATE(`scan_date`) BETWEEN '" + dateFormat.format(Date_Search1_bin.getDate()) + "' AND '" + dateFormat.format(Date_Search2_bin.getDate()) + "')";
            }

            sql = "SELECT `att_log_deleted`.`pin`, `tb_karyawan`.`id_pegawai`, `tb_karyawan`.`nama_pegawai`,`tb_bagian`.`nama_bagian`, `tb_bagian`.`kode_departemen`, `posisi`, "
                    + "DATE(`scan_date`) AS 'tanggal', TIME(`scan_date`) AS 'waktu', `att_log_deleted`.`sn`,`att_mesin_finger`.`nama_mesin`, `verifymode`, `keterangan_delete`\n"
                    + "FROM `att_log_deleted` "
                    + "LEFT JOIN `att_mesin_finger` ON `att_log_deleted`.`sn` = `att_mesin_finger`.`sn`\n"
                    + "LEFT JOIN `tb_karyawan` ON `att_log_deleted`.`pin` = `tb_karyawan`.`pin_finger`\n"
                    + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`"
                    + "WHERE `pin` LIKE '%" + txt_search_pin_bin.getText() + "%' "
                    + karyawan
                    + bagian
                    + posisi
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
                row[7] = new SimpleDateFormat("HH:mm:ss").format(rs.getTime("waktu"));
                switch (rs.getInt("verifymode")) {
                    case 1:
                        row[8] = "Fingerprint Scan";
                        break;
                    case 20:
                        row[8] = "Face Scan";
                        break;
                    default:
                        row[8] = "Unidentified";
                        break;
                }
                row[9] = rs.getString("sn");
                row[10] = rs.getString("nama_mesin");
                row[11] = rs.getString("keterangan_delete");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(tabel_data_absen_bin);
            label_total_data_absen_bin.setText(Integer.toString(tabel_data_absen_bin.getRowCount()));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_BoxBahanJadi.class.getName()).log(Level.SEVERE, null, ex);
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
        button_export_absen = new javax.swing.JButton();
        button_mesin = new javax.swing.JButton();
        jLabel23 = new javax.swing.JLabel();
        ComboBox_posisi = new javax.swing.JComboBox<>();
        CheckBox_hanya_absen_mesin = new javax.swing.JCheckBox();
        jLabel14 = new javax.swing.JLabel();
        txt_search_bagian = new javax.swing.JTextField();
        button_delete_absen = new javax.swing.JButton();
        button_input_absen_manual = new javax.swing.JButton();
        button_input_bonus_pencapaian = new javax.swing.JButton();
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
        jLabel25 = new javax.swing.JLabel();
        ComboBox_posisi2 = new javax.swing.JComboBox<>();
        CheckBox_showAll = new javax.swing.JCheckBox();
        CheckBox_seninJumat = new javax.swing.JCheckBox();
        jLabel38 = new javax.swing.JLabel();
        CheckBox_sabtu = new javax.swing.JCheckBox();
        CheckBox_minggu = new javax.swing.JCheckBox();
        txt_search_bagian2 = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        txt_search_NamaKaryawan_bin = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txt_search_pin_bin = new javax.swing.JTextField();
        jLabel36 = new javax.swing.JLabel();
        Date_Search1_bin = new com.toedter.calendar.JDateChooser();
        Date_Search2_bin = new com.toedter.calendar.JDateChooser();
        button_refresh_bin = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabel_data_absen_bin = new javax.swing.JTable();
        jLabel21 = new javax.swing.JLabel();
        label_total_data_absen_bin = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        button_export_bin = new javax.swing.JButton();
        jLabel24 = new javax.swing.JLabel();
        ComboBox_posisi_bin = new javax.swing.JComboBox<>();
        txt_search_bagian_bin = new javax.swing.JTextField();
        button_restore_absen = new javax.swing.JButton();

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

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
                "PIN", "ID Pegawai", "Nama", "Bagian", "Departemen", "Posisi", "Tanggal", "Waktu Absen", "Verifikasi", "SN Mesin", "Mesin Absen", "SPL", "Jumlah Jam", "Jam Mulai"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class
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
        jScrollPane1.setViewportView(tabel_data_absen);

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("Total Data :");

        label_total_data_absen.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_absen.setText("0");

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel6.setText("Bagian :");

        button_export_absen.setBackground(new java.awt.Color(255, 255, 255));
        button_export_absen.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_absen.setText("Export");
        button_export_absen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_absenActionPerformed(evt);
            }
        });

        button_mesin.setBackground(new java.awt.Color(255, 255, 255));
        button_mesin.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_mesin.setText("Mesin Absen");
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

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setText("Status = IN");

        txt_search_bagian.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_bagian.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_bagianKeyPressed(evt);
            }
        });

        button_delete_absen.setBackground(new java.awt.Color(255, 255, 255));
        button_delete_absen.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_delete_absen.setText("Delete");
        button_delete_absen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_delete_absenActionPerformed(evt);
            }
        });

        button_input_absen_manual.setBackground(new java.awt.Color(255, 255, 255));
        button_input_absen_manual.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_input_absen_manual.setText("Input Absen Manual");
        button_input_absen_manual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_input_absen_manualActionPerformed(evt);
            }
        });

        button_input_bonus_pencapaian.setBackground(new java.awt.Color(255, 255, 255));
        button_input_bonus_pencapaian.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_input_bonus_pencapaian.setText("Input CSV Absen");
        button_input_bonus_pencapaian.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_input_bonus_pencapaianActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txt_search_NamaKaryawan, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txt_search_pin, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel35)
                                    .addComponent(Date_Search1, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_Search2, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txt_search_bagian, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(ComboBox_posisi, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_refresh)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_export_absen)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_mesin))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_data_absen)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel14))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(CheckBox_hanya_absen_mesin)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(button_input_absen_manual)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_input_bonus_pencapaian)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_delete_absen)))
                        .addGap(0, 194, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(button_refresh, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_posisi, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_NamaKaryawan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_pin, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_Search1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_bagian, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export_absen, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_Search2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_mesin, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(button_input_bonus_pencapaian, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_input_absen_manual, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CheckBox_hanya_absen_mesin)
                    .addComponent(button_delete_absen, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 540, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_total_data_absen, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("DATA ABSENSI SELURUH KARYAWAN", jPanel1);

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
        Date_TidakMasuk1.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        Date_TidakMasuk1.setMinSelectableDate(new java.util.Date(1483207315000L));

        Date_TidakMasuk2.setBackground(new java.awt.Color(255, 255, 255));
        Date_TidakMasuk2.setDate(new Date());
        Date_TidakMasuk2.setDateFormatString("dd MMMM yyyy");
        Date_TidakMasuk2.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N

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
                "Tanggal", "ID Pegawai", "PIN", "Nama", "Bagian", "Departemen", "Posisi", "Tanggal", "Jam Absen", "Keterangan", "Ket Hari libur"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class
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
        jLabel16.setText("Antara Jam :");

        Spinner_jam1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Spinner_jam1.setModel(new javax.swing.SpinnerNumberModel(0, 0, 23, 1));
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

        jLabel25.setBackground(new java.awt.Color(255, 255, 255));
        jLabel25.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel25.setText("Posisi :");

        ComboBox_posisi2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_posisi2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        CheckBox_showAll.setBackground(new java.awt.Color(204, 255, 255));
        CheckBox_showAll.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        CheckBox_showAll.setText("Show All Data");

        CheckBox_seninJumat.setBackground(new java.awt.Color(204, 255, 255));
        CheckBox_seninJumat.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        CheckBox_seninJumat.setSelected(true);
        CheckBox_seninJumat.setText("Senin-Jumat");

        jLabel38.setBackground(new java.awt.Color(255, 255, 255));
        jLabel38.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel38.setText("Tampilkan hanya hari :");

        CheckBox_sabtu.setBackground(new java.awt.Color(204, 255, 255));
        CheckBox_sabtu.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        CheckBox_sabtu.setSelected(true);
        CheckBox_sabtu.setText("Sabtu");

        CheckBox_minggu.setBackground(new java.awt.Color(204, 255, 255));
        CheckBox_minggu.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        CheckBox_minggu.setSelected(true);
        CheckBox_minggu.setText("Minggu");

        txt_search_bagian2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_bagian2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_bagian2KeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_data)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(CheckBox_showAll)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel38)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(CheckBox_seninJumat)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(CheckBox_sabtu)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(CheckBox_minggu))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txt_search_NamaKaryawan2)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txt_search_pin2)
                                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addComponent(Date_TidakMasuk1, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(Date_TidakMasuk2, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel37))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txt_search_bagian2, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(ComboBox_posisi2, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel16)
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
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addComponent(Spinner_menit2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(button_refresh_data_TidakMasuk)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(button_export_tdkMasuk)))))
                        .addGap(0, 146, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
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
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(26, 26, 26))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(ComboBox_posisi2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txt_search_bagian2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(Spinner_jam1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Spinner_menit1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(Spinner_jam2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Spinner_menit2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(button_refresh_data_TidakMasuk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_export_tdkMasuk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_total_data, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CheckBox_showAll, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CheckBox_seninJumat, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CheckBox_sabtu, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CheckBox_minggu, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 568, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("DATA TIDAK MASUK", jPanel3);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("Nama Karyawan :");

        txt_search_NamaKaryawan_bin.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_NamaKaryawan_bin.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_NamaKaryawan_binKeyPressed(evt);
            }
        });

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel9.setText("PIN :");

        txt_search_pin_bin.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_pin_bin.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_pin_binKeyPressed(evt);
            }
        });

        jLabel36.setBackground(new java.awt.Color(255, 255, 255));
        jLabel36.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        jLabel36.setText("Tanggal Absen :");

        Date_Search1_bin.setBackground(new java.awt.Color(255, 255, 255));
        Date_Search1_bin.setToolTipText("");
        Date_Search1_bin.setDateFormatString("dd MMMM yyyy");
        Date_Search1_bin.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        Date_Search1_bin.setMinSelectableDate(new java.util.Date(1483207315000L));

        Date_Search2_bin.setBackground(new java.awt.Color(255, 255, 255));
        Date_Search2_bin.setDateFormatString("dd MMMM yyyy");
        Date_Search2_bin.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N

        button_refresh_bin.setBackground(new java.awt.Color(255, 255, 255));
        button_refresh_bin.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_refresh_bin.setText("Refresh");
        button_refresh_bin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refresh_binActionPerformed(evt);
            }
        });

        tabel_data_absen_bin.setAutoCreateRowSorter(true);
        tabel_data_absen_bin.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tabel_data_absen_bin.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "PIN", "ID Pegawai", "Nama", "Bagian", "Departemen", "Posisi", "Tanggal", "Waktu Absen", "Verifikasi", "SN Mesin", "Mesin Absen", "Ket."
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
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
        jScrollPane2.setViewportView(tabel_data_absen_bin);

        jLabel21.setBackground(new java.awt.Color(255, 255, 255));
        jLabel21.setText("Total Data :");

        label_total_data_absen_bin.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_absen_bin.setText("0");

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel7.setText("Bagian :");

        button_export_bin.setBackground(new java.awt.Color(255, 255, 255));
        button_export_bin.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_bin.setText("Export");
        button_export_bin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_binActionPerformed(evt);
            }
        });

        jLabel24.setBackground(new java.awt.Color(255, 255, 255));
        jLabel24.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel24.setText("Posisi :");

        ComboBox_posisi_bin.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_posisi_bin.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        txt_search_bagian_bin.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_bagian_bin.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_bagian_binKeyPressed(evt);
            }
        });

        button_restore_absen.setBackground(new java.awt.Color(255, 255, 255));
        button_restore_absen.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_restore_absen.setText("Restore");
        button_restore_absen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_restore_absenActionPerformed(evt);
            }
        });

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
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txt_search_NamaKaryawan_bin)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txt_search_pin_bin)
                                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel36)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(Date_Search1_bin, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(Date_Search2_bin, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txt_search_bagian_bin, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel24)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(ComboBox_posisi_bin, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(button_refresh_bin)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(button_export_bin)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(button_restore_absen))))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel21)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_data_absen_bin)))
                        .addGap(0, 258, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_search_NamaKaryawan_bin, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_pin_bin, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_Search1_bin, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_Search2_bin, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_refresh_bin, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export_bin, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_posisi_bin, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_bagian_bin, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_restore_absen, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 571, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_total_data_absen_bin, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("KERANJANG SAMPAH DATA ABSENSI", jPanel2);

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

    private void button_export_absenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_absenActionPerformed
        // TODO add your handling code here:
        DefaultTableModel table = (DefaultTableModel) tabel_data_absen.getModel();
        ExportToExcel.writeToExcel(table, this);
    }//GEN-LAST:event_button_export_absenActionPerformed

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

    private void CheckBox_hanya_absen_mesinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CheckBox_hanya_absen_mesinActionPerformed
        // TODO add your handling code here:
        refreshTable_absen();
    }//GEN-LAST:event_CheckBox_hanya_absen_mesinActionPerformed

    private void txt_search_NamaKaryawan_binKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_NamaKaryawan_binKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_absen_recycle_bin();
        }
    }//GEN-LAST:event_txt_search_NamaKaryawan_binKeyPressed

    private void txt_search_pin_binKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_pin_binKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_absen_recycle_bin();
        }
    }//GEN-LAST:event_txt_search_pin_binKeyPressed

    private void button_refresh_binActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refresh_binActionPerformed
        // TODO add your handling code here:
        refreshTable_absen_recycle_bin();
    }//GEN-LAST:event_button_refresh_binActionPerformed

    private void button_export_binActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_binActionPerformed
        // TODO add your handling code here:
        DefaultTableModel table = (DefaultTableModel) tabel_data_absen_bin.getModel();
        ExportToExcel.writeToExcel(table, this);
    }//GEN-LAST:event_button_export_binActionPerformed

    private void txt_search_bagian_binKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_bagian_binKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_absen_recycle_bin();
        }
    }//GEN-LAST:event_txt_search_bagian_binKeyPressed

    private void txt_search_bagianKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_bagianKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_absen();
        }
    }//GEN-LAST:event_txt_search_bagianKeyPressed

    private void txt_search_bagian2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_bagian2KeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_TidakMasuk();
        }
    }//GEN-LAST:event_txt_search_bagian2KeyPressed

    private void button_delete_absenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_delete_absenActionPerformed
        // TODO add your handling code here:
        int row = tabel_data_absen.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih dulu data data absen yang mau di hapus di tabel!");
        } else {
            String keterangan = JOptionPane.showInputDialog("Keterangan :(tidak boleh kosong!)");
            if (keterangan != null && !keterangan.equals("")) {
                try {
                    Utility.db.getConnection().setAutoCommit(false);
                    String sn = tabel_data_absen.getValueAt(row, 9).toString();
                    String String_Tanggal = tabel_data_absen.getValueAt(row, 6).toString();
                    String String_Waktu = tabel_data_absen.getValueAt(row, 7).toString();
                    Date tanggal = new SimpleDateFormat("yyyy-MM-dd").parse(String_Tanggal);
                    Date waktu = new SimpleDateFormat("HH:mm:ss").parse(String_Waktu);
                    String scan_date = new SimpleDateFormat("yyyy-MM-dd").format(tanggal) + " " + new SimpleDateFormat("HH:mm:ss").format(waktu);
                    String pin = tabel_data_absen.getValueAt(row, 0).toString();

                    String Query_insert = "INSERT INTO `att_log_deleted`(`sn`, `scan_date`, `pin`, `verifymode`, `inoutmode`, `device_ip`, `keterangan_delete`) \n"
                            + "SELECT `sn`, `scan_date`, `pin`, `verifymode`, `inoutmode`, `device_ip`, '" + keterangan + "' FROM `att_log` "
                            + "WHERE `pin` = '" + pin + "' AND `sn` = '" + sn + "' AND `scan_date` = '" + scan_date + "'";
                    Utility.db.getConnection().createStatement();
                    Utility.db.getStatement().executeUpdate(Query_insert);
                    String Query_delete = "DELETE FROM `att_log` WHERE `pin` = '" + pin + "' AND `sn` = '" + sn + "' AND `scan_date` = '" + scan_date + "'";
                    Utility.db.getConnection().createStatement();
                    Utility.db.getStatement().executeUpdate(Query_delete);
                    Utility.db.getConnection().commit();
                    JOptionPane.showMessageDialog(this, "Data berhasil di pindahkan ke recycle bin!");
                    refreshTable_absen();
                    refreshTable_absen_recycle_bin();
                } catch (Exception ex) {
                    try {
                        Utility.db.getConnection().rollback();
                    } catch (SQLException ex1) {
                        Logger.getLogger(JPanel_Absen_Keuangan.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                    JOptionPane.showMessageDialog(this, ex);
                    Logger.getLogger(JPanel_Absen_Keuangan.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    try {
                        Utility.db.getConnection().setAutoCommit(true);
                    } catch (SQLException ex) {
                        Logger.getLogger(JPanel_Absen_Keuangan.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }//GEN-LAST:event_button_delete_absenActionPerformed

    private void button_restore_absenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_restore_absenActionPerformed
        // TODO add your handling code here:
        int row = tabel_data_absen_bin.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih dulu data data absen yang mau di hapus di tabel!");
        } else {
            int dialogResult = JOptionPane.showConfirmDialog(this, "Restore absen?", "Warning", 0);
            if (dialogResult == JOptionPane.YES_OPTION) {
                try {
                    Utility.db.getConnection().setAutoCommit(false);
                    String sn = tabel_data_absen_bin.getValueAt(row, 9).toString();
                    String String_Tanggal = tabel_data_absen_bin.getValueAt(row, 6).toString();
                    String String_Waktu = tabel_data_absen_bin.getValueAt(row, 7).toString();
                    Date tanggal = new SimpleDateFormat("yyyy-MM-dd").parse(String_Tanggal);
                    Date waktu = new SimpleDateFormat("HH:mm:ss").parse(String_Waktu);
                    String scan_date = new SimpleDateFormat("yyyy-MM-dd").format(tanggal) + " " + new SimpleDateFormat("HH:mm:ss").format(waktu);
                    String pin = tabel_data_absen_bin.getValueAt(row, 0).toString();

                    String Query_insert = "INSERT INTO `att_log`(`sn`, `scan_date`, `pin`, `verifymode`, `inoutmode`, `device_ip`) \n"
                            + "SELECT `sn`, `scan_date`, `pin`, `verifymode`, `inoutmode`, `device_ip` FROM `att_log_deleted` "
                            + "WHERE `pin` = '" + pin + "' AND `sn` = '" + sn + "' AND `scan_date` = '" + scan_date + "'";
                    Utility.db.getConnection().createStatement();
                    Utility.db.getStatement().executeUpdate(Query_insert);
                    String Query_delete = "DELETE FROM `att_log_deleted` WHERE `pin` = '" + pin + "' AND `sn` = '" + sn + "' AND `scan_date` = '" + scan_date + "'";
                    Utility.db.getConnection().createStatement();
                    Utility.db.getStatement().executeUpdate(Query_delete);
                    Utility.db.getConnection().commit();
                    refreshTable_absen();
                    refreshTable_absen_recycle_bin();
                } catch (Exception ex) {
                    try {
                        Utility.db.getConnection().rollback();
                    } catch (SQLException ex1) {
                        Logger.getLogger(JPanel_Absen_Keuangan.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                    JOptionPane.showMessageDialog(this, ex);
                    Logger.getLogger(JPanel_Absen_Keuangan.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    try {
                        Utility.db.getConnection().setAutoCommit(true);
                    } catch (SQLException ex) {
                        Logger.getLogger(JPanel_Absen_Keuangan.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }//GEN-LAST:event_button_restore_absenActionPerformed

    private void button_input_absen_manualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_input_absen_manualActionPerformed
        // TODO add your handling code here:
        JDialog_InputAbsenManual dialog = new JDialog_InputAbsenManual(new javax.swing.JFrame(), true);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);
        refreshTable_absen();
    }//GEN-LAST:event_button_input_absen_manualActionPerformed

    private void button_input_bonus_pencapaianActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_input_bonus_pencapaianActionPerformed
        // TODO add your handling code here:
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            JOptionPane.showMessageDialog(this, "Format csv dengan pemisah koma (,):\nID PEGAWAI, TANGGAL(yyyy-mm-dd), JAM(HH:mm:ss)");
            int n = 0;
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Select CSV file to import!");
            int result = chooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                Utility.db.getConnection();
                File file = chooser.getSelectedFile();
                String filename1 = file.getAbsolutePath();
                try (BufferedReader br = new BufferedReader(new FileReader(filename1))) {
                    String line;
                    try {
                        Utility.db.getConnection().setAutoCommit(false);
                        while ((line = br.readLine()) != null) {
                            String[] value = line.split(",");
                            int pin = Integer.valueOf(value[0].substring(6));
                            Date tanggal = dateFormat.parse(value[1]);
                            Date waktu = new SimpleDateFormat("HH:mm:ss").parse(value[2]);
                            String Query = "INSERT IGNORE INTO `att_log`(`sn`, `scan_date`, `pin`, `verifymode`, `inoutmode`, `device_ip`) "
                                    + "VALUES ('" + inetAddress.getHostName() + "','" + dateFormat.format(tanggal) + " " + new SimpleDateFormat("HH:mm:ss").format(waktu) + "','" + pin + "', '0', '0', '" + inetAddress.getHostAddress() + "') ";
                            Utility.db.getConnection().prepareStatement(Query);
                            if ((Utility.db.getStatement().executeUpdate(Query)) > 0) {
                                System.out.println(value[0]);
                                n++;
                                System.out.println(n);
                            } else {
                                System.out.println("gagal:" + Query);
                            }
                        }
                        Utility.db.getConnection().commit();
                    } catch (Exception ex) {
                        Utility.db.getConnection().rollback();
                        JOptionPane.showMessageDialog(this, ex);
                        Logger.getLogger(JDialog_BonusPencapaianProduksi.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {
                        JOptionPane.showMessageDialog(this, "Data Berhasil Masuk : " + n);
                        Utility.db.getConnection().setAutoCommit(false);
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JDialog_BonusPencapaianProduksi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_input_bonus_pencapaianActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox CheckBox_hanya_absen_mesin;
    private javax.swing.JCheckBox CheckBox_minggu;
    private javax.swing.JCheckBox CheckBox_sabtu;
    private javax.swing.JCheckBox CheckBox_seninJumat;
    private javax.swing.JCheckBox CheckBox_showAll;
    private javax.swing.JComboBox<String> ComboBox_posisi;
    private javax.swing.JComboBox<String> ComboBox_posisi2;
    private javax.swing.JComboBox<String> ComboBox_posisi_bin;
    private com.toedter.calendar.JDateChooser Date_Search1;
    private com.toedter.calendar.JDateChooser Date_Search1_bin;
    private com.toedter.calendar.JDateChooser Date_Search2;
    private com.toedter.calendar.JDateChooser Date_Search2_bin;
    private com.toedter.calendar.JDateChooser Date_TidakMasuk1;
    private com.toedter.calendar.JDateChooser Date_TidakMasuk2;
    private javax.swing.JSpinner Spinner_jam1;
    private javax.swing.JSpinner Spinner_jam2;
    private javax.swing.JSpinner Spinner_menit1;
    private javax.swing.JSpinner Spinner_menit2;
    private javax.swing.JButton button_delete_absen;
    private javax.swing.JButton button_export_absen;
    private javax.swing.JButton button_export_bin;
    private javax.swing.JButton button_export_tdkMasuk;
    private javax.swing.JButton button_input_absen_manual;
    private javax.swing.JButton button_input_bonus_pencapaian;
    private javax.swing.JButton button_mesin;
    private javax.swing.JButton button_refresh;
    private javax.swing.JButton button_refresh_bin;
    private javax.swing.JButton button_refresh_data_TidakMasuk;
    private javax.swing.JButton button_restore_absen;
    private javax.swing.JLabel jLabel10;
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
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
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
    private javax.swing.JLabel label_total_data_absen_bin;
    private javax.swing.JTable tabel_data_TidakMasuk;
    private javax.swing.JTable tabel_data_absen;
    private javax.swing.JTable tabel_data_absen_bin;
    private javax.swing.JTextField txt_search_NamaKaryawan;
    private javax.swing.JTextField txt_search_NamaKaryawan2;
    private javax.swing.JTextField txt_search_NamaKaryawan_bin;
    private javax.swing.JTextField txt_search_bagian;
    private javax.swing.JTextField txt_search_bagian2;
    private javax.swing.JTextField txt_search_bagian_bin;
    private javax.swing.JTextField txt_search_pin;
    private javax.swing.JTextField txt_search_pin2;
    private javax.swing.JTextField txt_search_pin_bin;
    // End of variables declaration//GEN-END:variables
}
