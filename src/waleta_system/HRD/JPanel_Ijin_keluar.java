package waleta_system.HRD;

import waleta_system.Class.Utility;

import java.awt.HeadlessException;
import java.awt.event.KeyEvent;
import java.net.InetAddress;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
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

public class JPanel_Ijin_keluar extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    PreparedStatement pst;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Date today = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    DefaultTableModel dm = new DefaultTableModel();
    JFileChooser chooser = new JFileChooser();

    public JPanel_Ijin_keluar() {
        initComponents();
    }

    public void init() {
        try {
            ComboBox_status_karyawan_rekap.removeAllItems();
            ComboBox_status_karyawan_ijin_keluar.removeAllItems();
            ComboBox_status_karyawan_rekap.addItem("All");
            ComboBox_status_karyawan_ijin_keluar.addItem("All");
            sql = "SELECT DISTINCT(`status`) AS 'status' FROM `tb_karyawan` WHERE `status` IS NOT NULL";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_status_karyawan_rekap.addItem(rs.getString("status"));
                ComboBox_status_karyawan_ijin_keluar.addItem(rs.getString("status"));
            }
            ComboBox_status_karyawan_rekap.setSelectedItem("IN");
            ComboBox_status_karyawan_ijin_keluar.setSelectedItem("IN");

            ComboBox_departemen_rekap.removeAllItems();
            ComboBox_departemen_ijin_keluar.removeAllItems();
            ComboBox_departemen_rekap.addItem("All");
            ComboBox_departemen_ijin_keluar.addItem("All");
            sql = "SELECT `kode_dep` FROM `tb_departemen`";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_departemen_rekap.addItem(rs.getString("kode_dep"));
                ComboBox_departemen_ijin_keluar.addItem(rs.getString("kode_dep"));
            }

            ComboBox_posisi_ijin_keluar.removeAllItems();
            ComboBox_posisi_ijin_keluar.addItem("All");
            sql = "SELECT DISTINCT(`posisi`) AS 'posisi' FROM `tb_karyawan` WHERE `status` = 'IN' AND `posisi` <> 'DIREKTUR' AND `posisi` IS NOT NULL";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_posisi_ijin_keluar.addItem(rs.getString("posisi"));
            }
            refreshTable_karyawan();
            refreshTable_Data_ijin();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, e);
        }
        Table_rekap.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && Table_rekap.getSelectedRow() != -1) {
                    int x = Table_rekap.getSelectedRow();
                    refreshTable_Ijin_karyawan();
                    label_nama.setText(Table_rekap.getValueAt(x, 1).toString());
                }
            }
        });
        Table_Ijin_Keluar.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && Table_Ijin_Keluar.getSelectedRow() != -1) {
                    int x = Table_Ijin_Keluar.getSelectedRow();
                    if (Table_Ijin_Keluar.getValueAt(x, 6) == null) {
                        button_edit_ijin1.setEnabled(true);
                        button_delete_ijin_keluar.setEnabled(true);
                    } else {
                        button_edit_ijin1.setEnabled(false);
                        button_delete_ijin_keluar.setEnabled(false);
                    }
                }
            }
        });
    }

    public void refreshTable_karyawan() {
        try {
            int karyawan_ijin = 0;
            DefaultTableModel model = (DefaultTableModel) Table_rekap.getModel();
            model.setRowCount(0);
            String bagian = "";
            if (txt_search_bagian_rekap.getText() != null && !txt_search_bagian_rekap.getText().equals("")) {
                bagian = "AND `nama_bagian` LIKE '%" + txt_search_bagian_rekap.getText() + "%' ";
            }
            String departemen = "AND `kode_departemen` = '" + ComboBox_departemen_rekap.getSelectedItem().toString() + "' ";
            if ("All".equals(ComboBox_departemen_rekap.getSelectedItem().toString())) {
                departemen = "";
            }
            String kelamin = "AND `jenis_kelamin` = '" + ComboBox_kelamin_rekap.getSelectedItem().toString() + "' ";
            if ("All".equals(ComboBox_kelamin_rekap.getSelectedItem().toString())) {
                kelamin = "";
            }
            String status = "AND `status` = '" + ComboBox_status_karyawan_rekap.getSelectedItem().toString() + "' ";
            if ("All".equals(ComboBox_status_karyawan_rekap.getSelectedItem().toString())) {
                status = "";
            }

            String filter_tanggal = "";
            if (Date_Ijin_Keluar_rekap1.getDate() != null && Date_Ijin_Keluar_rekap2.getDate() != null) {
                filter_tanggal = "AND (`tb_ijin_keluar`.`tanggal_keluar` BETWEEN '" + dateFormat.format(Date_Ijin_Keluar_rekap1.getDate()) + "' AND '" + dateFormat.format(Date_Ijin_Keluar_rekap2.getDate()) + "' "
                        + "OR `tb_ijin_keluar`.`tanggal_keluar` IS NULL)";
            }

            sql = "SELECT `tb_karyawan`.`id_pegawai`, `nama_pegawai`, `tb_bagian`.`nama_bagian`,`tb_bagian`.`kode_departemen`, `posisi`, `desa`, `status`,  COUNT(`tb_ijin_keluar`.`id_pegawai`) AS 'jumlah' "
                    + "FROM `tb_karyawan` "
                    + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian` "
                    + "LEFT JOIN `tb_ijin_keluar` ON `tb_karyawan`.`id_pegawai` = `tb_ijin_keluar`.`id_pegawai` "
                    + "WHERE `nama_pegawai` LIKE '%" + txt_search_nama_karyawan_rekap.getText() + "%' "
                    + bagian
                    + departemen
                    + kelamin
                    + status
                    + filter_tanggal
                    + "GROUP BY `tb_karyawan`.`id_pegawai`";

            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[9];
            while (rs.next()) {
                row[0] = rs.getString("id_pegawai");
                row[1] = rs.getString("nama_pegawai");
                row[2] = rs.getString("desa");
                row[3] = rs.getString("posisi");
                row[4] = rs.getString("kode_departemen");
                row[5] = rs.getString("nama_bagian");
                row[6] = rs.getString("status");
                row[7] = rs.getInt("jumlah");
                if (rs.getInt("jumlah") > 0) {
                    karyawan_ijin++;
                }
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_rekap);
            label_total_karyawan_ijin.setText(String.valueOf(karyawan_ijin));
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_Ijin_keluar.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_Ijin_karyawan() {
        try {
            DefaultTableModel model = (DefaultTableModel) Table_detail_rekap.getModel();
            model.setRowCount(0);
            int i = Table_rekap.getSelectedRow();
            sql = "SELECT * FROM `tb_ijin_keluar` WHERE `id_pegawai` = '" + Table_rekap.getValueAt(i, 0) + "'";
            if (Date_Ijin_Keluar_rekap1.getDate() != null && Date_Ijin_Keluar_rekap2.getDate() != null) {
                sql = "SELECT * FROM `tb_ijin_keluar` WHERE `id_pegawai` = '" + Table_rekap.getValueAt(i, 0) + "' "
                        + "AND `tb_ijin_keluar`.`tanggal_keluar` BETWEEN '" + dateFormat.format(Date_Ijin_Keluar_rekap1.getDate()) + "' AND '" + dateFormat.format(Date_Ijin_Keluar_rekap2.getDate()) + "' ";
            }
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[6];
            while (rs.next()) {
                row[0] = rs.getString("kode_keluar");
                row[1] = rs.getString("id_pegawai");
                row[2] = rs.getDate("tanggal_keluar");
                row[3] = rs.getString("jam_keluar");
                row[4] = rs.getString("jam_kembali");
                row[5] = rs.getString("keterangan");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_detail_rekap);
            int rowData = Table_detail_rekap.getRowCount();
            label_total.setText(Integer.toString(rowData));
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_Ijin_keluar.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_Data_ijin() {
        try {
            decimalFormat.setMaximumFractionDigits(1);
            DefaultTableModel model = (DefaultTableModel) Table_Ijin_Keluar.getModel();
            model.setRowCount(0);
            String bagian = "";
            if (txt_search_bagian_ijin_keluar.getText() != null && !txt_search_bagian_ijin_keluar.getText().equals("")) {
                bagian = "AND `nama_bagian` LIKE '%" + txt_search_bagian_ijin_keluar.getText() + "%' ";
            }
            String departemen = "AND `kode_departemen` = '" + ComboBox_departemen_ijin_keluar.getSelectedItem().toString() + "' ";
            if ("All".equals(ComboBox_departemen_ijin_keluar.getSelectedItem().toString())) {
                departemen = "";
            }
            String kelamin = "AND `jenis_kelamin` = '" + ComboBox_kelamin_ijin_keluar.getSelectedItem().toString() + "' ";
            if ("All".equals(ComboBox_kelamin_ijin_keluar.getSelectedItem().toString())) {
                kelamin = "";
            }
            String status = "AND `status` = '" + ComboBox_status_karyawan_ijin_keluar.getSelectedItem().toString() + "' ";
            if ("All".equals(ComboBox_status_karyawan_ijin_keluar.getSelectedItem().toString())) {
                status = "";
            }
            String posisi = "AND `posisi` = '" + ComboBox_posisi_ijin_keluar.getSelectedItem().toString() + "' ";
            if ("All".equals(ComboBox_posisi_ijin_keluar.getSelectedItem().toString())) {
                posisi = "";
            }

            String filter_tanggal = "";
            if (Date_Ijin_Keluar1.getDate() != null && Date_Ijin_Keluar2.getDate() != null) {
                filter_tanggal = "AND (`tb_ijin_keluar`.`tanggal_keluar` BETWEEN '" + dateFormat.format(Date_Ijin_Keluar1.getDate()) + "' AND '" + dateFormat.format(Date_Ijin_Keluar2.getDate()) + "' "
                        + "OR `tb_ijin_keluar`.`tanggal_keluar` IS NULL)";
            }

            String jenis_ijin = "";
            if (ComboBox_jenis_ijin_ijin_keluar.getSelectedIndex() == 1) {
                jenis_ijin = " AND `jam_keluar` IS NOT NULL AND `jam_kembali` IS NOT NULL ";
            } else if (ComboBox_jenis_ijin_ijin_keluar.getSelectedIndex() == 2) {
                jenis_ijin = " AND `jam_keluar` IS NOT NULL AND `jam_kembali` IS NULL ";
            } else if (ComboBox_jenis_ijin_ijin_keluar.getSelectedIndex() == 3) {
                jenis_ijin = " AND `jam_keluar` IS NULL AND `jam_kembali` IS NULL ";
            }

            sql = "SELECT `kode_keluar`, `tb_ijin_keluar`.`tanggal_keluar`, `jam_keluar`, `jam_kembali`, `tb_ijin_keluar`.`keterangan`, `tb_ijin_keluar`.`id_pegawai`, `nama_pegawai`, `tb_bagian`.`nama_bagian`, `posisi`, `pengawas`, `print_by`, TIMESTAMPDIFF(MINUTE, `jam_keluar`, `jam_kembali`) AS 'lama' "
                    + "FROM `tb_ijin_keluar` "
                    + "LEFT JOIN `tb_karyawan` ON `tb_karyawan`.`id_pegawai` = `tb_ijin_keluar`.`id_pegawai` "
                    + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian` "
                    + "WHERE `nama_pegawai` LIKE '%" + txt_search_nama_karyawan_ijin_keluar.getText() + "%' "
                    + bagian
                    + departemen
                    + kelamin
                    + status
                    + posisi
                    + jenis_ijin
                    + filter_tanggal
                    + "ORDER BY `tb_ijin_keluar`.`tanggal_keluar` DESC";
            
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[15];
            while (rs.next()) {
                row[0] = rs.getString("kode_keluar");
                row[1] = rs.getString("tanggal_keluar");
                row[2] = rs.getString("id_pegawai");
                row[3] = rs.getString("nama_pegawai");
                row[4] = rs.getString("nama_bagian");
                row[5] = rs.getString("posisi");
                row[6] = rs.getString("jam_keluar");
                row[7] = rs.getString("jam_kembali");
                row[8] = decimalFormat.format(rs.getFloat("lama") / 60.f);
                row[9] = rs.getString("keterangan");
                row[10] = rs.getString("pengawas");
                row[11] = rs.getString("print_by");
                row[12] = decimalFormat.format(Math.floor(rs.getFloat("lama") / 30.f) * 30.f / 60.f);
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_Ijin_Keluar);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Ijin_keluar.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jPanel_search_karyawan1 = new javax.swing.JPanel();
        txt_search_nama_karyawan_ijin_keluar = new javax.swing.JTextField();
        button_search_ijin_keluar = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        ComboBox_departemen_ijin_keluar = new javax.swing.JComboBox<>();
        Date_Ijin_Keluar1 = new com.toedter.calendar.JDateChooser();
        Date_Ijin_Keluar2 = new com.toedter.calendar.JDateChooser();
        jLabel14 = new javax.swing.JLabel();
        ComboBox_kelamin_ijin_keluar = new javax.swing.JComboBox<>();
        ComboBox_status_karyawan_ijin_keluar = new javax.swing.JComboBox<>();
        jLabel15 = new javax.swing.JLabel();
        ComboBox_posisi_ijin_keluar = new javax.swing.JComboBox<>();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        ComboBox_jenis_ijin_ijin_keluar = new javax.swing.JComboBox<>();
        txt_search_bagian_ijin_keluar = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        Table_Ijin_Keluar = new javax.swing.JTable();
        jLabel19 = new javax.swing.JLabel();
        button_export_ijin_keluar = new javax.swing.JButton();
        button_add_ijin_keluar = new javax.swing.JButton();
        button_edit_ijin1 = new javax.swing.JButton();
        button_delete_ijin_keluar = new javax.swing.JButton();
        button_print_PULANG = new javax.swing.JButton();
        button_print_KELUAR = new javax.swing.JButton();
        jLabel20 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jPanel_search_karyawan = new javax.swing.JPanel();
        txt_search_nama_karyawan_rekap = new javax.swing.JTextField();
        button_search_karyawan_rekap = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        ComboBox_departemen_rekap = new javax.swing.JComboBox<>();
        Date_Ijin_Keluar_rekap1 = new com.toedter.calendar.JDateChooser();
        Date_Ijin_Keluar_rekap2 = new com.toedter.calendar.JDateChooser();
        jLabel8 = new javax.swing.JLabel();
        ComboBox_kelamin_rekap = new javax.swing.JComboBox<>();
        ComboBox_status_karyawan_rekap = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        txt_search_bagian_rekap = new javax.swing.JTextField();
        jScrollPane5 = new javax.swing.JScrollPane();
        Table_rekap = new javax.swing.JTable();
        jScrollPane1 = new javax.swing.JScrollPane();
        Table_detail_rekap = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        label_total_karyawan_ijin = new javax.swing.JLabel();
        label_total = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        button_export = new javax.swing.JButton();
        label_nama = new javax.swing.JLabel();

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setPreferredSize(new java.awt.Dimension(1366, 652));

        jPanel_search_karyawan1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_search_karyawan1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));

        txt_search_nama_karyawan_ijin_keluar.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_nama_karyawan_ijin_keluar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_nama_karyawan_ijin_keluarKeyPressed(evt);
            }
        });

        button_search_ijin_keluar.setBackground(new java.awt.Color(255, 255, 255));
        button_search_ijin_keluar.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search_ijin_keluar.setText("Search");
        button_search_ijin_keluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_search_ijin_keluarActionPerformed(evt);
            }
        });

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel4.setText("Nama Karyawan :");

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel5.setText("Departemen :");

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel13.setText("Bagian :");

        jLabel36.setBackground(new java.awt.Color(255, 255, 255));
        jLabel36.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        jLabel36.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel36.setText("-");

        jLabel37.setBackground(new java.awt.Color(255, 255, 255));
        jLabel37.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        jLabel37.setText("Tanggal Ijin Keluar :");

        ComboBox_departemen_ijin_keluar.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_departemen_ijin_keluar.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        Date_Ijin_Keluar1.setBackground(new java.awt.Color(255, 255, 255));
        Date_Ijin_Keluar1.setToolTipText("");
        Date_Ijin_Keluar1.setDate(new Date());
        Date_Ijin_Keluar1.setDateFormatString("dd MMM yyyy");
        Date_Ijin_Keluar1.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        Date_Ijin_Keluar1.setMinSelectableDate(new java.util.Date(1483207315000L));

        Date_Ijin_Keluar2.setBackground(new java.awt.Color(255, 255, 255));
        Date_Ijin_Keluar2.setDate(new Date());
        Date_Ijin_Keluar2.setDateFormatString("dd MMM yyyy");
        Date_Ijin_Keluar2.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel14.setText("Jenis Kelamin  :");

        ComboBox_kelamin_ijin_keluar.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_kelamin_ijin_keluar.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Laki-Laki", "Perempuan" }));

        ComboBox_status_karyawan_ijin_keluar.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_status_karyawan_ijin_keluar.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "IN", "OUT" }));
        ComboBox_status_karyawan_ijin_keluar.setSelectedIndex(1);

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel15.setText("Status :");

        ComboBox_posisi_ijin_keluar.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_posisi_ijin_keluar.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel16.setText("Posisi :");

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel17.setText("Jenis Ijin :");

        ComboBox_jenis_ijin_ijin_keluar.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_jenis_ijin_ijin_keluar.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Ijin Keluar", "Ijin Pulang", "Ijin Terlambat" }));

        txt_search_bagian_ijin_keluar.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_bagian_ijin_keluar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_bagian_ijin_keluarKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_search_karyawan1Layout = new javax.swing.GroupLayout(jPanel_search_karyawan1);
        jPanel_search_karyawan1.setLayout(jPanel_search_karyawan1Layout);
        jPanel_search_karyawan1Layout.setHorizontalGroup(
            jPanel_search_karyawan1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_search_karyawan1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_search_karyawan1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txt_search_nama_karyawan_ijin_keluar)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_search_karyawan1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                    .addComponent(ComboBox_departemen_ijin_keluar, 0, 150, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_search_karyawan1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_bagian_ijin_keluar, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_search_karyawan1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel14)
                    .addComponent(ComboBox_kelamin_ijin_keluar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_search_karyawan1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel15)
                    .addComponent(ComboBox_status_karyawan_ijin_keluar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_search_karyawan1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel16)
                    .addComponent(ComboBox_posisi_ijin_keluar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel_search_karyawan1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel17)
                    .addGroup(jPanel_search_karyawan1Layout.createSequentialGroup()
                        .addComponent(ComboBox_jenis_ijin_ijin_keluar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_search_ijin_keluar)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 229, Short.MAX_VALUE)
                .addGroup(jPanel_search_karyawan1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel37)
                    .addGroup(jPanel_search_karyawan1Layout.createSequentialGroup()
                        .addComponent(Date_Ijin_Keluar1, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_Ijin_Keluar2, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel_search_karyawan1Layout.setVerticalGroup(
            jPanel_search_karyawan1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_search_karyawan1Layout.createSequentialGroup()
                .addGroup(jPanel_search_karyawan1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel_search_karyawan1Layout.createSequentialGroup()
                        .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_search_karyawan1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ComboBox_jenis_ijin_ijin_keluar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_search_ijin_keluar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel_search_karyawan1Layout.createSequentialGroup()
                        .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_posisi_ijin_keluar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_search_karyawan1Layout.createSequentialGroup()
                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_status_karyawan_ijin_keluar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_search_karyawan1Layout.createSequentialGroup()
                        .addGroup(jPanel_search_karyawan1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_search_karyawan1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_search_karyawan1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ComboBox_kelamin_ijin_keluar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel_search_karyawan1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(ComboBox_departemen_ijin_keluar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txt_search_bagian_ijin_keluar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(Date_Ijin_Keluar1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Date_Ijin_Keluar2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_search_nama_karyawan_ijin_keluar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(6, 13, Short.MAX_VALUE))
        );

        Table_Ijin_Keluar.setAutoCreateRowSorter(true);
        Table_Ijin_Keluar.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Table_Ijin_Keluar.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "Tgl Ijin", "ID Pegawai", "Nama", "Bagian", "Posisi", "Jam Keluar", "Jam Kembali", "Lama", "Keterangan", "Pengawas", "Print By", "Pembulatan"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
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
        jScrollPane2.setViewportView(Table_Ijin_Keluar);

        jLabel19.setBackground(new java.awt.Color(255, 255, 255));
        jLabel19.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel19.setText("DATA IJIN KELUAR");

        button_export_ijin_keluar.setBackground(new java.awt.Color(255, 255, 255));
        button_export_ijin_keluar.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_ijin_keluar.setText("Export to Excel");
        button_export_ijin_keluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_ijin_keluarActionPerformed(evt);
            }
        });

        button_add_ijin_keluar.setBackground(new java.awt.Color(255, 255, 255));
        button_add_ijin_keluar.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_add_ijin_keluar.setText("+ Add");
        button_add_ijin_keluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_add_ijin_keluarActionPerformed(evt);
            }
        });

        button_edit_ijin1.setBackground(new java.awt.Color(255, 255, 255));
        button_edit_ijin1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_edit_ijin1.setText("Edit");
        button_edit_ijin1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_edit_ijin1ActionPerformed(evt);
            }
        });

        button_delete_ijin_keluar.setBackground(new java.awt.Color(255, 255, 255));
        button_delete_ijin_keluar.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_delete_ijin_keluar.setText("Delete");
        button_delete_ijin_keluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_delete_ijin_keluarActionPerformed(evt);
            }
        });

        button_print_PULANG.setBackground(new java.awt.Color(255, 255, 255));
        button_print_PULANG.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_print_PULANG.setText("PRINT IJIN PULANG*");
        button_print_PULANG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_print_PULANGActionPerformed(evt);
            }
        });

        button_print_KELUAR.setBackground(new java.awt.Color(255, 255, 255));
        button_print_KELUAR.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_print_KELUAR.setText("PRINT IJIN KELUAR");
        button_print_KELUAR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_print_KELUARActionPerformed(evt);
            }
        });

        jLabel20.setBackground(new java.awt.Color(255, 255, 255));
        jLabel20.setFont(new java.awt.Font("Arial", 2, 10)); // NOI18N
        jLabel20.setText("Note : *Untuk Ijin Pulang, jika dalam 1 jam terakhir tidak ada absen pulang karyawan, maka sistem otomatis akan input absen pulang ke data absen.");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel_search_karyawan1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_print_KELUAR)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_print_PULANG)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_add_ijin_keluar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_edit_ijin1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_delete_ijin_keluar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_export_ijin_keluar))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel20)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel_search_karyawan1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_export_ijin_keluar)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_add_ijin_keluar)
                    .addComponent(button_edit_ijin1)
                    .addComponent(button_delete_ijin_keluar)
                    .addComponent(button_print_PULANG)
                    .addComponent(button_print_KELUAR))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 515, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel20)
                .addContainerGap())
        );

        jTabbedPane1.addTab("DATA IJIN KELUAR", jPanel2);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setPreferredSize(new java.awt.Dimension(1366, 652));

        jPanel_search_karyawan.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_search_karyawan.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));

        txt_search_nama_karyawan_rekap.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_nama_karyawan_rekap.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_nama_karyawan_rekapKeyPressed(evt);
            }
        });

        button_search_karyawan_rekap.setBackground(new java.awt.Color(255, 255, 255));
        button_search_karyawan_rekap.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search_karyawan_rekap.setText("Search");
        button_search_karyawan_rekap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_search_karyawan_rekapActionPerformed(evt);
            }
        });

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("Nama Karyawan :");

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("Departemen :");

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel7.setText("Bagian :");

        jLabel34.setBackground(new java.awt.Color(255, 255, 255));
        jLabel34.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        jLabel34.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel34.setText("-");

        jLabel35.setBackground(new java.awt.Color(255, 255, 255));
        jLabel35.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        jLabel35.setText("Date Filter (by Tanggal Ijin Keluar) :");

        ComboBox_departemen_rekap.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_departemen_rekap.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        Date_Ijin_Keluar_rekap1.setBackground(new java.awt.Color(255, 255, 255));
        Date_Ijin_Keluar_rekap1.setToolTipText("");
        Date_Ijin_Keluar_rekap1.setDateFormatString("dd MMMM yyyy");
        Date_Ijin_Keluar_rekap1.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        Date_Ijin_Keluar_rekap1.setMinSelectableDate(new java.util.Date(1483207315000L));

        Date_Ijin_Keluar_rekap2.setBackground(new java.awt.Color(255, 255, 255));
        Date_Ijin_Keluar_rekap2.setDateFormatString("dd MMMM yyyy");
        Date_Ijin_Keluar_rekap2.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel8.setText("Jenis Kelamin  :");

        ComboBox_kelamin_rekap.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_kelamin_rekap.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Laki-Laki", "Perempuan" }));

        ComboBox_status_karyawan_rekap.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_status_karyawan_rekap.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "IN", "OUT" }));
        ComboBox_status_karyawan_rekap.setSelectedIndex(1);

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel12.setText("Status :");

        txt_search_bagian_rekap.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_bagian_rekap.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_bagian_rekapKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_search_karyawanLayout = new javax.swing.GroupLayout(jPanel_search_karyawan);
        jPanel_search_karyawan.setLayout(jPanel_search_karyawanLayout);
        jPanel_search_karyawanLayout.setHorizontalGroup(
            jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_search_karyawanLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txt_search_nama_karyawan_rekap)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                    .addComponent(ComboBox_departemen_rekap, 0, 150, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_bagian_rekap, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addComponent(ComboBox_kelamin_rekap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12)
                    .addGroup(jPanel_search_karyawanLayout.createSequentialGroup()
                        .addComponent(ComboBox_status_karyawan_rekap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_search_karyawan_rekap)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 369, Short.MAX_VALUE)
                .addGroup(jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel35)
                    .addGroup(jPanel_search_karyawanLayout.createSequentialGroup()
                        .addComponent(Date_Ijin_Keluar_rekap1, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_Ijin_Keluar_rekap2, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel_search_karyawanLayout.setVerticalGroup(
            jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_search_karyawanLayout.createSequentialGroup()
                .addGroup(jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel_search_karyawanLayout.createSequentialGroup()
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ComboBox_status_karyawan_rekap, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_search_karyawan_rekap, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel_search_karyawanLayout.createSequentialGroup()
                        .addGroup(jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ComboBox_kelamin_rekap, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(ComboBox_departemen_rekap, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txt_search_bagian_rekap, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(Date_Ijin_Keluar_rekap1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Date_Ijin_Keluar_rekap2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_search_nama_karyawan_rekap, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(6, 12, Short.MAX_VALUE))
        );

        Table_rekap.setAutoCreateRowSorter(true);
        Table_rekap.setFont(new java.awt.Font("SansSerif", 0, 11)); // NOI18N
        Table_rekap.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Pegawai", "Nama Pegawai", "Desa", "Posisi", "Departemen", "Bagian", "Status", "Ijin"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class
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
        Table_rekap.getTableHeader().setReorderingAllowed(false);
        jScrollPane5.setViewportView(Table_rekap);

        Table_detail_rekap.setAutoCreateRowSorter(true);
        Table_detail_rekap.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Table_detail_rekap.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "ID Pegawai", "Tgl Ijin", "Jam Keluar", "Jam Kembali", "Keterangan"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class
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
        jScrollPane1.setViewportView(Table_detail_rekap);

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel1.setText("TOTAL :");

        label_total_karyawan_ijin.setBackground(new java.awt.Color(255, 255, 255));
        label_total_karyawan_ijin.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_karyawan_ijin.setText("0");

        label_total.setBackground(new java.awt.Color(255, 255, 255));
        label_total.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        label_total.setText("0000");

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("TOTAL KARYAWAN YANG IJIN :");

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel9.setText("DAFTAR KARYAWAN WALETA");

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel10.setText("DATA IJIN KELUAR");

        button_export.setBackground(new java.awt.Color(255, 255, 255));
        button_export.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export.setText("Export to Excel");
        button_export.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_exportActionPerformed(evt);
            }
        });

        label_nama.setBackground(new java.awt.Color(255, 255, 255));
        label_nama.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_nama.setForeground(new java.awt.Color(0, 0, 204));
        label_nama.setText("NAMA PEGAWAI");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel_search_karyawan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_karyawan_ijin))
                            .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 812, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_nama)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(button_export))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 523, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel_search_karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_nama, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_karyawan_ijin, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 511, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane5))
                .addContainerGap())
        );

        jTabbedPane1.addTab("REKAP / KARYAWAN", jPanel1);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1366, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void button_exportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_exportActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Table_detail_rekap.getModel();
        ExportToExcel.writeToExcel(model, jPanel1);
    }//GEN-LAST:event_button_exportActionPerformed

    private void txt_search_nama_karyawan_rekapKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_nama_karyawan_rekapKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_karyawan();
        }
    }//GEN-LAST:event_txt_search_nama_karyawan_rekapKeyPressed

    private void button_search_karyawan_rekapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_karyawan_rekapActionPerformed
        // TODO add your handling code here:
        refreshTable_karyawan();
    }//GEN-LAST:event_button_search_karyawan_rekapActionPerformed

    private void txt_search_nama_karyawan_ijin_keluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_nama_karyawan_ijin_keluarKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_Data_ijin();
        }
    }//GEN-LAST:event_txt_search_nama_karyawan_ijin_keluarKeyPressed

    private void button_search_ijin_keluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_ijin_keluarActionPerformed
        // TODO add your handling code here:
        refreshTable_Data_ijin();
    }//GEN-LAST:event_button_search_ijin_keluarActionPerformed

    private void button_export_ijin_keluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_ijin_keluarActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Table_Ijin_Keluar.getModel();
        ExportToExcel.writeToExcel(model, jPanel1);
    }//GEN-LAST:event_button_export_ijin_keluarActionPerformed

    private void button_add_ijin_keluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_add_ijin_keluarActionPerformed
        // TODO add your handling code here:
        JDialog_Add_Ijin_Keluar Add = new JDialog_Add_Ijin_Keluar(new javax.swing.JFrame(), true, null, null);
        Add.pack();
        Add.setLocationRelativeTo(this);
        Add.setVisible(true);
        Add.setEnabled(true);
        refreshTable_Data_ijin();
    }//GEN-LAST:event_button_add_ijin_keluarActionPerformed

    private void button_edit_ijin1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_edit_ijin1ActionPerformed
        // TODO add your handling code here:
        int row = Table_Ijin_Keluar.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please Select Row Data that you want to Edit !");
        } else {
            String kode = Table_Ijin_Keluar.getValueAt(row, 0).toString();
            JDialog_Edit_Ijin_Keluar edit = new JDialog_Edit_Ijin_Keluar(new javax.swing.JFrame(), true, kode);
            edit.pack();
            edit.setLocationRelativeTo(this);
            edit.setVisible(true);
            edit.setEnabled(true);
            refreshTable_Data_ijin();
        }
    }//GEN-LAST:event_button_edit_ijin1ActionPerformed

    private void button_delete_ijin_keluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_delete_ijin_keluarActionPerformed
        // TODO add your handling code here:
        try {
            int row = Table_Ijin_Keluar.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please Select Row Data that you want to Delete !");
            } else if (Table_Ijin_Keluar.getValueAt(row, 6) != null) {
                JOptionPane.showMessageDialog(this, "Maaf sudah ada jam keluarnya tidak bisa hapus data !");
            } else {
                int dialogResult = JOptionPane.showConfirmDialog(this, "Yakin hapus data ini?", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    // delete code here
                    String Query = "DELETE FROM `tb_ijin_keluar` WHERE `kode_keluar` = '" + Table_Ijin_Keluar.getValueAt(row, 0) + "'";
                    Utility.db.getConnection().createStatement();
                    if ((Utility.db.getStatement().executeUpdate(Query)) == 1) {
                        refreshTable_Data_ijin();
                        JOptionPane.showMessageDialog(this, "data deleted Successfully");
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed!");
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_Ijin_keluar.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_delete_ijin_keluarActionPerformed

    private void button_print_PULANGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_print_PULANGActionPerformed
        try {
            int x = Table_Ijin_Keluar.getSelectedRow();
            if (x > -1) {
                if (Table_Ijin_Keluar.getValueAt(x, 10) == null) {
                    JOptionPane.showMessageDialog(this, "Silahkan minta persetujuan dari pengawas terlebih dahulu");
                } else {
                    String kode = Table_Ijin_Keluar.getValueAt(x, 0).toString();
                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                    String print_by = MainForm.Login_NamaPegawai + ":" + new SimpleDateFormat("dd/MM/yy HH:mm:ss").format(timestamp);

                    String Query = "UPDATE `tb_ijin_keluar` SET `print_by`='" + print_by + "', `jam_keluar`=NOW() "
                            + "WHERE `kode_keluar` = '" + kode + "' AND `print_by` IS NULL";
                    Utility.db.getConnection().createStatement();
                    if ((Utility.db.getStatement().executeUpdate(Query)) == 1) {
                        JOptionPane.showMessageDialog(this, "jam keluar disimpan!");
                        String insert_absen = "INSERT INTO `att_log` (`sn`, `scan_date`, `pin`, `verifymode`, `device_ip`) "
                                + "SELECT * FROM (SELECT '000', NOW(), (SELECT SUBSTRING(`id_pegawai`, 7, 5) + 0 AS 'pin' FROM `tb_ijin_keluar` WHERE `kode_keluar` = '" + kode + "'), 0, NULL) AS tmp "
                                + "WHERE NOT EXISTS (SELECT * FROM `att_log` WHERE `pin` = (SELECT SUBSTRING(`id_pegawai`, 7, 5) + 0 AS 'pin' FROM `tb_ijin_keluar` WHERE `kode_keluar` = '" + kode + "') "
                                + "AND (`scan_date` BETWEEN DATE_SUB(NOW(), INTERVAL '1' HOUR) AND NOW())) LIMIT 1";
                        Utility.db.getConnection().createStatement();
                        if ((Utility.db.getStatement().executeUpdate(insert_absen)) == 1) {
                            JOptionPane.showMessageDialog(this, "absen pulang otomatis oleh sistem!");
                        } else {
                            JOptionPane.showMessageDialog(this, "karyawan sudah melakukan absen di mesin absen!");
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "jam keluar sudah ada");
                    }

                    JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\label_ijin_keluar.jrxml");
                    Map<String, Object> map = new HashMap<>();
                    map.put("KODE", kode);//parameter name should be like it was named inside your report.
                    map.put("JENIS_IJIN", "IJIN PULANG");//parameter name should be like it was named inside your report.
                    JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
                    JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, map, Utility.db.getConnection());
                    JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
                        refreshTable_Data_ijin();
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Ijin_keluar.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_button_print_PULANGActionPerformed

    private void button_print_KELUARActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_print_KELUARActionPerformed
        // TODO add your handling code here:
        try {
            int x = Table_Ijin_Keluar.getSelectedRow();
            if (x > -1) {
                if (Table_Ijin_Keluar.getValueAt(x, 10) == null) {
                    JOptionPane.showMessageDialog(this, "Silahkan minta persetujuan dari pengawas terlebih dahulu");
                } else {
                    String kode = Table_Ijin_Keluar.getValueAt(x, 0).toString();
                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                    String print_by = MainForm.Login_NamaPegawai + ":" + new SimpleDateFormat("dd/MM/yy HH:mm:ss").format(timestamp);

                    String Query = "UPDATE `tb_ijin_keluar` SET "
                            + "`print_by`='" + print_by + "', "
                            + "`jam_keluar`=NOW() "
                            + "WHERE `kode_keluar` = '" + kode + "' AND `print_by` IS NULL";
                    Utility.db.getConnection().createStatement();
                    if ((Utility.db.getStatement().executeUpdate(Query)) == 1) {
                        JOptionPane.showMessageDialog(this, "jam keluar disimpan!");
                    } else {
                        JOptionPane.showMessageDialog(this, "jam keluar sudah ada");
                    }

                    JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\label_ijin_keluar.jrxml");
                    Map<String, Object> map = new HashMap<>();
                    map.put("KODE", kode);//parameter name should be like it was named inside your report.
                    map.put("JENIS_IJIN", "IJIN KELUAR");//parameter name should be like it was named inside your report.
                    JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
                    JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, map, Utility.db.getConnection());
                    JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
                        refreshTable_Data_ijin();
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Ijin_keluar.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_print_KELUARActionPerformed

    private void txt_search_bagian_ijin_keluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_bagian_ijin_keluarKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_Data_ijin();
        }
    }//GEN-LAST:event_txt_search_bagian_ijin_keluarKeyPressed

    private void txt_search_bagian_rekapKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_bagian_rekapKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_karyawan();
        }
    }//GEN-LAST:event_txt_search_bagian_rekapKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_departemen_ijin_keluar;
    private javax.swing.JComboBox<String> ComboBox_departemen_rekap;
    private javax.swing.JComboBox<String> ComboBox_jenis_ijin_ijin_keluar;
    private javax.swing.JComboBox<String> ComboBox_kelamin_ijin_keluar;
    private javax.swing.JComboBox<String> ComboBox_kelamin_rekap;
    private javax.swing.JComboBox<String> ComboBox_posisi_ijin_keluar;
    private javax.swing.JComboBox<String> ComboBox_status_karyawan_ijin_keluar;
    private javax.swing.JComboBox<String> ComboBox_status_karyawan_rekap;
    private com.toedter.calendar.JDateChooser Date_Ijin_Keluar1;
    private com.toedter.calendar.JDateChooser Date_Ijin_Keluar2;
    private com.toedter.calendar.JDateChooser Date_Ijin_Keluar_rekap1;
    private com.toedter.calendar.JDateChooser Date_Ijin_Keluar_rekap2;
    public static javax.swing.JTable Table_Ijin_Keluar;
    public static javax.swing.JTable Table_detail_rekap;
    public static javax.swing.JTable Table_rekap;
    public javax.swing.JButton button_add_ijin_keluar;
    public javax.swing.JButton button_delete_ijin_keluar;
    public javax.swing.JButton button_edit_ijin1;
    private javax.swing.JButton button_export;
    private javax.swing.JButton button_export_ijin_keluar;
    public javax.swing.JButton button_print_KELUAR;
    public javax.swing.JButton button_print_PULANG;
    public static javax.swing.JButton button_search_ijin_keluar;
    public static javax.swing.JButton button_search_karyawan_rekap;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel34;
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
    private javax.swing.JPanel jPanel_search_karyawan;
    private javax.swing.JPanel jPanel_search_karyawan1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel label_nama;
    private javax.swing.JLabel label_total;
    private javax.swing.JLabel label_total_karyawan_ijin;
    private javax.swing.JTextField txt_search_bagian_ijin_keluar;
    private javax.swing.JTextField txt_search_bagian_rekap;
    private javax.swing.JTextField txt_search_nama_karyawan_ijin_keluar;
    private javax.swing.JTextField txt_search_nama_karyawan_rekap;
    // End of variables declaration//GEN-END:variables

}
