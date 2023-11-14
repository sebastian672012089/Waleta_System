package waleta_system.HRD;

import waleta_system.Class.Utility;
import java.awt.HeadlessException;
import java.awt.event.KeyEvent;
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
import waleta_system.Panel_produksi.JPanel_DataCabut;

public class JPanel_DataPindahBagian extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    PreparedStatement pst;
    Date date = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();

    public JPanel_DataPindahBagian() {
        initComponents();
        tb_grup.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && tb_grup.getSelectedRow() != -1) {
                    int i = tb_grup.getSelectedRow();
                    label_kode_grup.setText(tb_grup.getValueAt(i, 0).toString());
                    label_ruang.setText(tb_grup.getValueAt(i, 1).toString());
                    refreshTable_detailGrup();
                }
            }
        });

        tabel_pindah_grup.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && tabel_pindah_grup.getSelectedRow() != -1) {
                    int i = tabel_pindah_grup.getSelectedRow();
                    if (tabel_pindah_grup.getValueAt(i, 11) == null) {//jam disetujui
                        Button_delete_permintaan_pindah.setEnabled(true);
                        Button_pindahGrup_disetujui.setEnabled(true);
                        Button_pindahGrup_diketahuiHR.setEnabled(false);
                        Button_pindahGrup_diketahuiKeu.setEnabled(false);
                    } else {
                        Button_delete_permintaan_pindah.setEnabled(false);
                        Button_pindahGrup_disetujui.setEnabled(false);
                        if (tabel_pindah_grup.getValueAt(i, 13) == null) {//jam diketahui hr
                            Button_pindahGrup_diketahuiHR.setEnabled(true);
                            Button_pindahGrup_diketahuiKeu.setEnabled(false);
                        } else {
                            Button_pindahGrup_diketahuiHR.setEnabled(false);
                            if (tabel_pindah_grup.getValueAt(i, 15) == null) {//jam diketahui keuangan
                                Button_pindahGrup_diketahuiKeu.setEnabled(true);
                            } else {
                                Button_pindahGrup_diketahuiKeu.setEnabled(false);
                            }
                        }
                    }
                }
            }
        });
    }

    public void init(String otorisasi) {
        try {
            ComboBox_ruangan.removeAllItems();
            ComboBox_ruangan.addItem("All");
            sql = "SELECT DISTINCT(`ruang_bagian`) AS ruangan FROM `tb_bagian` WHERE `status_bagian` = 1";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_ruangan.addItem(rs.getString("ruangan"));
            }
            if (otorisasi.equals("HRGA")) {
                jTabbedPane1.setSelectedIndex(0);
                jTabbedPane1.setEnabled(true);
                button_subsidi.setVisible(true);
            } else {
                if (MainForm.Login_Posisi.equals("STAFF 5")) {
                    jTabbedPane1.setSelectedIndex(1);
                    jTabbedPane1.setEnabled(true);
                } else {
                    jTabbedPane1.setSelectedIndex(0);
                    jTabbedPane1.setEnabled(false);
                }
                button_subsidi.setVisible(false);
            }
            refreshTable_Grup();
            refreshTable_dataKaryawan();
            refreshTable_PindahGrup();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_DataPindahBagian.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_Grup() {
        try {
            DefaultTableModel model = (DefaultTableModel) tb_grup.getModel();
            model.setRowCount(0);
            String filter_ruangan = "AND `tb_bagian`.`ruang_bagian` = '" + ComboBox_ruangan.getSelectedItem() + "'";
            if (ComboBox_ruangan.getSelectedItem() == "All") {
                filter_ruangan = "";
            }
            sql = "SELECT `grup`, `ruang_bagian`, COUNT(`id_pegawai`) AS 'jumlah_pegawai' "
                    + "FROM ("
                    + "SELECT `id_pegawai`, SUBSTRING_INDEX(`nama_bagian`, '-', -3) AS grup, `ruang_bagian` "
                    + "FROM `tb_karyawan` "
                    + "LEFT JOIN `tb_bagian` ON `tb_bagian`.`kode_bagian` = `tb_karyawan`.`kode_bagian`"
                    + "WHERE `tb_karyawan`.`status` = 'IN') DATA "
                    + "WHERE 1 "
                    + filter_ruangan
                    + "GROUP BY `grup`";
            System.out.println(sql);
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[4];
            while (rs.next()) {
                row[0] = rs.getString("grup");
                row[1] = rs.getString("ruang_bagian");
                row[2] = rs.getInt("jumlah_pegawai");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(tb_grup);
            label_total_grup.setText(Integer.toString(tb_grup.getRowCount()));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_DataPindahBagian.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_detailGrup() {
        try {
            DefaultTableModel model = (DefaultTableModel) tb_detail_grup.getModel();
            model.setRowCount(0);
            sql = "SELECT `id_pegawai`, `nama_pegawai`, `nama_bagian` \n"
                    + "FROM `tb_karyawan` "
                    + "LEFT JOIN `tb_bagian` ON `tb_bagian`.`kode_bagian` = `tb_karyawan`.`kode_bagian`\n"
                    + "WHERE SUBSTRING_INDEX(`nama_bagian`, '-', -3) = '" + label_kode_grup.getText() + "' "
                    + "AND `tb_karyawan`.`status` = 'IN'"
                    + "ORDER BY `nama_pegawai`";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[7];
            while (rs.next()) {
                row[0] = rs.getString("id_pegawai");
                row[1] = rs.getString("nama_pegawai");
                row[2] = rs.getString("nama_bagian");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(tb_detail_grup);
            int rowData = tb_detail_grup.getRowCount();
            label_total_pegawai.setText(Integer.toString(rowData));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_DataPindahBagian.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_dataKaryawan() {
        DefaultTableModel model = (DefaultTableModel) tb_data_karyawan.getModel();
        model.setRowCount(0);
        try {
            String status_bagian = "", filter_bagian = "";
            switch (ComboBox_status_karyawan.getSelectedIndex()) {
                case 0:
                    status_bagian = "";
                    break;
                case 1:
                    status_bagian = "AND `tb_karyawan`.`kode_bagian` IS NOT NULL ";
                    break;
                case 2:
                    status_bagian = " AND `tb_karyawan`.`kode_bagian` IS NULL";
                    break;
                default:
                    break;
            }
            if (txt_bagian.getText() != null && !txt_bagian.getText().equals("")) {
                filter_bagian = "AND `tb_bagian`.`nama_bagian` LIKE '%" + txt_bagian.getText() + "%'";
            }
            sql = "SELECT `id_pegawai`, `nama_pegawai`, `tb_bagian`.`nama_bagian`\n"
                    + "FROM `tb_karyawan` "
                    + "LEFT JOIN `tb_bagian` ON `tb_bagian`.`kode_bagian` = `tb_karyawan`.`kode_bagian`\n"
                    + "WHERE `tb_karyawan`.`nama_pegawai` LIKE '%" + txt_nama_karyawan.getText() + "%' "
                    + "AND `status` = 'IN' "
                    + filter_bagian
                    + status_bagian
                    + " ORDER BY `nama_pegawai`";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[6];
            while (rs.next()) {
                row[0] = rs.getString("id_pegawai");
                row[1] = rs.getString("nama_pegawai");
                row[2] = rs.getString("nama_bagian");
                model.addRow(row);
            }

            int rowData = tb_data_karyawan.getRowCount();
            label_total_pegawai1.setText(Integer.toString(rowData));
            ColumnsAutoSizer.sizeColumnsToFit(tb_data_karyawan);

        } catch (SQLException ex) {
            Logger.getLogger(JPanel_DataPindahBagian.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_PindahGrup() {
        try {
            DefaultTableModel model = (DefaultTableModel) tabel_pindah_grup.getModel();
            model.setRowCount(0);
            String filter_tanggal_pindahGrup = "";
            if (Date_filter_pindahGrup1.getDate() != null && Date_filter_pindahGrup2.getDate() != null) {
                if (ComboBox_Date_filter_pindahGrup.getSelectedIndex() == 0) {
                    filter_tanggal_pindahGrup = "AND DATE(`tanggal_input`) BETWEEN '" + dateFormat.format(Date_filter_pindahGrup1.getDate()) + "' AND '" + dateFormat.format(Date_filter_pindahGrup2.getDate()) + "' ";
                } else if (ComboBox_Date_filter_pindahGrup.getSelectedIndex() == 1) {
                    filter_tanggal_pindahGrup = "AND DATE(`jam_disetujui`) BETWEEN '" + dateFormat.format(Date_filter_pindahGrup1.getDate()) + "' AND '" + dateFormat.format(Date_filter_pindahGrup2.getDate()) + "' ";
                } else if (ComboBox_Date_filter_pindahGrup.getSelectedIndex() == 2) {
                    filter_tanggal_pindahGrup = "AND DATE(`jam_diketahui_hr`) BETWEEN '" + dateFormat.format(Date_filter_pindahGrup1.getDate()) + "' AND '" + dateFormat.format(Date_filter_pindahGrup2.getDate()) + "' ";
                } else if (ComboBox_Date_filter_pindahGrup.getSelectedIndex() == 3) {
                    filter_tanggal_pindahGrup = "AND DATE(`jam_diketahui_keu`) BETWEEN '" + dateFormat.format(Date_filter_pindahGrup1.getDate()) + "' AND '" + dateFormat.format(Date_filter_pindahGrup2.getDate()) + "' ";
                }
            }
            String filter_bagian_lama = "AND `bagian_lama` LIKE '%" + txt_search_bagian_lama.getText() + "%' ";
            if (txt_search_bagian_lama.getText() == null || txt_search_bagian_lama.getText().equals("")) {
                filter_bagian_lama = "";
            }
            String filter_bagian_baru = "AND `bagian_baru` LIKE '%" + txt_search_bagian_baru.getText() + "%' ";
            if (txt_search_bagian_baru.getText() == null || txt_search_bagian_baru.getText().equals("")) {
                filter_bagian_baru = "";
            }
            String filter_disetujui = "";
            if (ComboBox_filter_disetujui.getSelectedItem().toString().equals("BELUM")) {
                filter_disetujui = "AND `jam_disetujui` IS NULL ";
            } else if (ComboBox_filter_disetujui.getSelectedItem().toString().equals("SUDAH")) {
                filter_disetujui = "AND `jam_disetujui` IS NOT NULL ";
            }
            String filter_diketahui_HR = "";
            if (ComboBox_filter_diketahui_HR.getSelectedItem().toString().equals("BELUM")) {
                filter_diketahui_HR = "AND `jam_diketahui_hr` IS NULL ";
            } else if (ComboBox_filter_diketahui_HR.getSelectedItem().toString().equals("SUDAH")) {
                filter_diketahui_HR = "AND `jam_diketahui_hr` IS NOT NULL ";
            }
            String filter_diketahui_Keu = "";
            if (ComboBox_filter_diketahui_Keu.getSelectedItem().toString().equals("BELUM")) {
                filter_diketahui_Keu = "AND `jam_diketahui_keu` IS NULL ";
            } else if (ComboBox_filter_diketahui_Keu.getSelectedItem().toString().equals("SUDAH")) {
                filter_diketahui_Keu = "AND `jam_diketahui_keu` IS NOT NULL ";
            }
            sql = "SELECT `nomor`, `tanggal_input`, `tb_form_pindah_grup`.`id_pegawai`, `tb_karyawan`.`nama_pegawai`, "
                    + "`bagian_lama`, `bagian_baru`, `jamkerja_lama`, `jamkerja_baru`, `levelgaji_lama`, `levelgaji_baru`, "
                    + "`disetujui`, `jam_disetujui`, `diketahui_hr`, `jam_diketahui_hr`, `diketahui_keu`, `jam_diketahui_keu`, `tb_form_pindah_grup`.`keterangan` \n"
                    + "FROM `tb_form_pindah_grup` "
                    + "LEFT JOIN `tb_karyawan` ON `tb_form_pindah_grup`.`id_pegawai` = `tb_karyawan`.`id_pegawai`\n"
                    + "WHERE `tb_karyawan`.`nama_pegawai` LIKE '%" + txt_search_karyawan_pindahGrup.getText() + "%' "
                    + filter_bagian_lama
                    + filter_bagian_baru
                    + filter_tanggal_pindahGrup
                    + filter_disetujui
                    + filter_diketahui_HR
                    + filter_diketahui_Keu
                    + "ORDER BY `nomor` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[20];
            while (rs.next()) {
                row[0] = rs.getInt("nomor");
                row[1] = rs.getTimestamp("tanggal_input");
                row[2] = rs.getString("id_pegawai");
                row[3] = rs.getString("nama_pegawai");
                row[4] = rs.getString("bagian_lama");
                row[5] = rs.getString("bagian_baru");
                row[6] = rs.getString("jamkerja_lama");
                row[7] = rs.getString("jamkerja_baru");
                row[8] = rs.getString("levelgaji_lama");
                row[9] = rs.getString("levelgaji_baru");
                row[10] = rs.getString("disetujui");
                row[11] = rs.getTimestamp("jam_disetujui");
                row[12] = rs.getString("diketahui_hr");
                row[13] = rs.getTimestamp("jam_diketahui_hr");
                row[14] = rs.getString("diketahui_keu");
                row[15] = rs.getTimestamp("jam_diketahui_keu");
                row[16] = rs.getString("keterangan");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(tabel_pindah_grup);
            int rowData = tabel_pindah_grup.getRowCount();
            label_total_data_pindahGrup.setText(Integer.toString(rowData));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_DataPindahBagian.class.getName()).log(Level.SEVERE, null, ex);
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

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tabel_pindah_grup = new javax.swing.JTable();
        JLabel4 = new javax.swing.JLabel();
        txt_search_karyawan_pindahGrup = new javax.swing.JTextField();
        label_total_data_pindahGrup = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        JLabel5 = new javax.swing.JLabel();
        txt_search_bagian_lama = new javax.swing.JTextField();
        JLabel6 = new javax.swing.JLabel();
        txt_search_bagian_baru = new javax.swing.JTextField();
        Button_refresh_pindahGrup = new javax.swing.JButton();
        Button_pindahGrup_disetujui = new javax.swing.JButton();
        Button_pindahGrup_diketahuiHR = new javax.swing.JButton();
        Button_pindahGrup_diketahuiKeu = new javax.swing.JButton();
        Button_delete_permintaan_pindah = new javax.swing.JButton();
        ComboBox_Date_filter_pindahGrup = new javax.swing.JComboBox<>();
        Date_filter_pindahGrup1 = new com.toedter.calendar.JDateChooser();
        Date_filter_pindahGrup2 = new com.toedter.calendar.JDateChooser();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        Button_pindahGrup_disetujui_semua = new javax.swing.JButton();
        Button_pindahGrup_diketahuiHR_semua = new javax.swing.JButton();
        Button_pindahGrup_diketahuiKeu_semua = new javax.swing.JButton();
        JLabel7 = new javax.swing.JLabel();
        ComboBox_filter_disetujui = new javax.swing.JComboBox<>();
        JLabel8 = new javax.swing.JLabel();
        ComboBox_filter_diketahui_HR = new javax.swing.JComboBox<>();
        JLabel9 = new javax.swing.JLabel();
        ComboBox_filter_diketahui_Keu = new javax.swing.JComboBox<>();
        jPanel1 = new javax.swing.JPanel();
        jPanel_grup_cabut = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        ComboBox_ruangan = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        tb_grup = new javax.swing.JTable();
        jLabel11 = new javax.swing.JLabel();
        label_total_grup = new javax.swing.JLabel();
        button_laporan = new javax.swing.JButton();
        jPanel_detail_grup = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tb_detail_grup = new javax.swing.JTable();
        label_total_pegawai = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        label_kode_grup = new javax.swing.JLabel();
        label_ruang = new javax.swing.JLabel();
        JLabel1 = new javax.swing.JLabel();
        Jlabel2 = new javax.swing.JLabel();
        button_subsidi = new javax.swing.JButton();
        jPanel_detail_grup1 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tb_data_karyawan = new javax.swing.JTable();
        label_total_pegawai1 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        JLabel2 = new javax.swing.JLabel();
        Jlabel3 = new javax.swing.JLabel();
        txt_nama_karyawan = new javax.swing.JTextField();
        txt_bagian = new javax.swing.JTextField();
        button_search = new javax.swing.JButton();
        JLabel3 = new javax.swing.JLabel();
        ComboBox_status_karyawan = new javax.swing.JComboBox<>();
        button_export = new javax.swing.JButton();

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        tabel_pindah_grup.setAutoCreateRowSorter(true);
        tabel_pindah_grup.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nomor", "Tanggal Input", "ID Pegawai", "Nama", "Bagian Lama", "Bagian Baru", "Jam kerja lama", "Jam kerja baru", "Level Gaji lama", "Level Gaji baru", "Disetujui", "Waktu Disetujui", "Diketahui HR", "Waktu Diketahui HR", "Diketahui Keu", "Waktu Diketahui Keu", "Keterangan"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class
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
        tabel_pindah_grup.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(tabel_pindah_grup);

        JLabel4.setBackground(new java.awt.Color(255, 255, 255));
        JLabel4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        JLabel4.setText("Nama Karyawan :");

        txt_search_karyawan_pindahGrup.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_karyawan_pindahGrupKeyPressed(evt);
            }
        });

        label_total_data_pindahGrup.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_pindahGrup.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_data_pindahGrup.setText("0");

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel13.setText("TOTAL DATA :");

        JLabel5.setBackground(new java.awt.Color(255, 255, 255));
        JLabel5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        JLabel5.setText("Bagian Lama :");

        txt_search_bagian_lama.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_bagian_lamaKeyPressed(evt);
            }
        });

        JLabel6.setBackground(new java.awt.Color(255, 255, 255));
        JLabel6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        JLabel6.setText("Bagian Baru :");

        txt_search_bagian_baru.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_bagian_baruKeyPressed(evt);
            }
        });

        Button_refresh_pindahGrup.setBackground(new java.awt.Color(255, 255, 255));
        Button_refresh_pindahGrup.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Button_refresh_pindahGrup.setText("Refresh");
        Button_refresh_pindahGrup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_refresh_pindahGrupActionPerformed(evt);
            }
        });

        Button_pindahGrup_disetujui.setBackground(new java.awt.Color(255, 255, 255));
        Button_pindahGrup_disetujui.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Button_pindahGrup_disetujui.setText("Disetujui");
        Button_pindahGrup_disetujui.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_pindahGrup_disetujuiActionPerformed(evt);
            }
        });

        Button_pindahGrup_diketahuiHR.setBackground(new java.awt.Color(255, 255, 255));
        Button_pindahGrup_diketahuiHR.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Button_pindahGrup_diketahuiHR.setText("Diketahui HR");
        Button_pindahGrup_diketahuiHR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_pindahGrup_diketahuiHRActionPerformed(evt);
            }
        });

        Button_pindahGrup_diketahuiKeu.setBackground(new java.awt.Color(255, 255, 255));
        Button_pindahGrup_diketahuiKeu.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Button_pindahGrup_diketahuiKeu.setText("Diketahui Keuangan");
        Button_pindahGrup_diketahuiKeu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_pindahGrup_diketahuiKeuActionPerformed(evt);
            }
        });

        Button_delete_permintaan_pindah.setBackground(new java.awt.Color(255, 255, 255));
        Button_delete_permintaan_pindah.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Button_delete_permintaan_pindah.setText("Delete");
        Button_delete_permintaan_pindah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_delete_permintaan_pindahActionPerformed(evt);
            }
        });

        ComboBox_Date_filter_pindahGrup.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        ComboBox_Date_filter_pindahGrup.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tanggal Input", "Tanggal disetujui", "Tanggal diketahui HR", "Tanggal diketahui Keuangan" }));

        Date_filter_pindahGrup1.setBackground(new java.awt.Color(255, 255, 255));
        Date_filter_pindahGrup1.setDateFormatString("dd MMM yyyy");

        Date_filter_pindahGrup2.setBackground(new java.awt.Color(255, 255, 255));
        Date_filter_pindahGrup2.setDateFormatString("dd MMM yyyy");

        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jTextArea1.setRows(5);
        jTextArea1.setText("Note :\n1. Disetujui hanya bisa oleh login user STAFF 5.\n2. Diketahui HR hanya bisa oleh login user departemen HR.\n3. Diketahui Keuangan hanya bisa oleh login user departemen KEUANGAN.\n4. Fungsi DELETE hanya bisa ketika permintaan belum disetujui.");
        jScrollPane5.setViewportView(jTextArea1);

        Button_pindahGrup_disetujui_semua.setBackground(new java.awt.Color(255, 255, 255));
        Button_pindahGrup_disetujui_semua.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Button_pindahGrup_disetujui_semua.setText("Disetujui Semua");
        Button_pindahGrup_disetujui_semua.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_pindahGrup_disetujui_semuaActionPerformed(evt);
            }
        });

        Button_pindahGrup_diketahuiHR_semua.setBackground(new java.awt.Color(255, 255, 255));
        Button_pindahGrup_diketahuiHR_semua.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Button_pindahGrup_diketahuiHR_semua.setText("Diketahui HR Semua");
        Button_pindahGrup_diketahuiHR_semua.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_pindahGrup_diketahuiHR_semuaActionPerformed(evt);
            }
        });

        Button_pindahGrup_diketahuiKeu_semua.setBackground(new java.awt.Color(255, 255, 255));
        Button_pindahGrup_diketahuiKeu_semua.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Button_pindahGrup_diketahuiKeu_semua.setText("Diketahui Keuangan Semua");
        Button_pindahGrup_diketahuiKeu_semua.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_pindahGrup_diketahuiKeu_semuaActionPerformed(evt);
            }
        });

        JLabel7.setBackground(new java.awt.Color(255, 255, 255));
        JLabel7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        JLabel7.setText("Disetujui :");

        ComboBox_filter_disetujui.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        ComboBox_filter_disetujui.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ALL", "BELUM", "SUDAH" }));

        JLabel8.setBackground(new java.awt.Color(255, 255, 255));
        JLabel8.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        JLabel8.setText("Diketahui HR :");

        ComboBox_filter_diketahui_HR.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        ComboBox_filter_diketahui_HR.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ALL", "BELUM", "SUDAH" }));

        JLabel9.setBackground(new java.awt.Color(255, 255, 255));
        JLabel9.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        JLabel9.setText("Diketahui Keuangan :");

        ComboBox_filter_diketahui_Keu.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        ComboBox_filter_diketahui_Keu.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ALL", "BELUM", "SUDAH" }));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4)
                    .addComponent(jScrollPane5)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(Button_delete_permintaan_pindah)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Button_pindahGrup_disetujui)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Button_pindahGrup_disetujui_semua)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Button_pindahGrup_diketahuiHR)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Button_pindahGrup_diketahuiHR_semua)
                                .addGap(6, 6, 6)
                                .addComponent(Button_pindahGrup_diketahuiKeu)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Button_pindahGrup_diketahuiKeu_semua)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_data_pindahGrup))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(JLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_karyawan_pindahGrup, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(JLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_bagian_lama, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(JLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_bagian_baru, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_Date_filter_pindahGrup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_filter_pindahGrup1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_filter_pindahGrup2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Button_refresh_pindahGrup))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(JLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_filter_disetujui, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(JLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_filter_diketahui_HR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(JLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_filter_diketahui_Keu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 95, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(JLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_karyawan_pindahGrup, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(JLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_bagian_lama, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(JLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_bagian_baru, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Button_refresh_pindahGrup, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_Date_filter_pindahGrup, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_filter_pindahGrup1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_filter_pindahGrup2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(JLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_filter_disetujui, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(JLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_filter_diketahui_HR, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(JLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_filter_diketahui_Keu, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(label_total_data_pindahGrup, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Button_pindahGrup_disetujui, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Button_pindahGrup_diketahuiHR, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Button_pindahGrup_diketahuiKeu, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Button_delete_permintaan_pindah, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Button_pindahGrup_disetujui_semua, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Button_pindahGrup_diketahuiHR_semua, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Button_pindahGrup_diketahuiKeu_semua, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 478, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("PERMINTAAN PINDAH BAGIAN", jPanel2);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel_grup_cabut.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_grup_cabut.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "DATA GRUP", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 11))); // NOI18N

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("RUANG :");

        ComboBox_ruangan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_ruangan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "A", "B", "C", "D", "E", "-" }));
        ComboBox_ruangan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ComboBox_ruanganActionPerformed(evt);
            }
        });

        tb_grup.setAutoCreateRowSorter(true);
        tb_grup.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nama Grup", "Ruang", "Pegawai", "Status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class
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
        tb_grup.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tb_grup);

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel11.setText("TOTAL :");

        label_total_grup.setBackground(new java.awt.Color(255, 255, 255));
        label_total_grup.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_grup.setText("0");

        button_laporan.setBackground(new java.awt.Color(255, 255, 255));
        button_laporan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_laporan.setText("Laporan");
        button_laporan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_laporanActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_grup_cabutLayout = new javax.swing.GroupLayout(jPanel_grup_cabut);
        jPanel_grup_cabut.setLayout(jPanel_grup_cabutLayout);
        jPanel_grup_cabutLayout.setHorizontalGroup(
            jPanel_grup_cabutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_grup_cabutLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_grup_cabutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_grup_cabutLayout.createSequentialGroup()
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_grup)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_grup_cabutLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_ruangan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 140, Short.MAX_VALUE)
                        .addComponent(button_laporan)))
                .addContainerGap())
        );
        jPanel_grup_cabutLayout.setVerticalGroup(
            jPanel_grup_cabutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_grup_cabutLayout.createSequentialGroup()
                .addGroup(jPanel_grup_cabutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_laporan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_ruangan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_grup_cabutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_grup, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1)
                .addContainerGap())
        );

        jPanel_detail_grup.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_detail_grup.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "DETAIL GRUP", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 11))); // NOI18N

        tb_detail_grup.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Pegawai", "Nama", "Bagian"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
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
        tb_detail_grup.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(tb_detail_grup);

        label_total_pegawai.setBackground(new java.awt.Color(255, 255, 255));
        label_total_pegawai.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_pegawai.setText("0");

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel5.setText("TOTAL PEGAWAI :");

        label_kode_grup.setBackground(new java.awt.Color(255, 255, 255));
        label_kode_grup.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_kode_grup.setText("-");

        label_ruang.setBackground(new java.awt.Color(255, 255, 255));
        label_ruang.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_ruang.setText("-");

        JLabel1.setBackground(new java.awt.Color(255, 255, 255));
        JLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        JLabel1.setText("Grup :");

        Jlabel2.setBackground(new java.awt.Color(255, 255, 255));
        Jlabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Jlabel2.setText("RUANG :");

        button_subsidi.setBackground(new java.awt.Color(255, 255, 255));
        button_subsidi.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_subsidi.setText("Pindah Training");
        button_subsidi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_subsidiActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_detail_grupLayout = new javax.swing.GroupLayout(jPanel_detail_grup);
        jPanel_detail_grup.setLayout(jPanel_detail_grupLayout);
        jPanel_detail_grupLayout.setHorizontalGroup(
            jPanel_detail_grupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_detail_grupLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_detail_grupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 455, Short.MAX_VALUE)
                    .addGroup(jPanel_detail_grupLayout.createSequentialGroup()
                        .addComponent(JLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_kode_grup, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel_detail_grupLayout.createSequentialGroup()
                        .addComponent(Jlabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_ruang, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_pegawai, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_subsidi)))
                .addContainerGap())
        );
        jPanel_detail_grupLayout.setVerticalGroup(
            jPanel_detail_grupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_detail_grupLayout.createSequentialGroup()
                .addGroup(jPanel_detail_grupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(JLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_kode_grup, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_detail_grupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(Jlabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_ruang, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_pegawai, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_subsidi, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2)
                .addContainerGap())
        );

        jPanel_detail_grup1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_detail_grup1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "DATA KARYAWAN", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 11))); // NOI18N

        tb_data_karyawan.setAutoCreateRowSorter(true);
        tb_data_karyawan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Pegawai", "Nama", "Bagian"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
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
        tb_data_karyawan.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(tb_data_karyawan);

        label_total_pegawai1.setBackground(new java.awt.Color(255, 255, 255));
        label_total_pegawai1.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_pegawai1.setText("0");

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel7.setText("TOTAL PEGAWAI :");

        JLabel2.setBackground(new java.awt.Color(255, 255, 255));
        JLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        JLabel2.setText("Nama Karyawan :");

        Jlabel3.setBackground(new java.awt.Color(255, 255, 255));
        Jlabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Jlabel3.setText("Bagian :");

        txt_nama_karyawan.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        txt_nama_karyawan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_nama_karyawanKeyPressed(evt);
            }
        });

        txt_bagian.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        txt_bagian.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_bagianKeyPressed(evt);
            }
        });

        button_search.setText("Search");
        button_search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_searchActionPerformed(evt);
            }
        });

        JLabel3.setBackground(new java.awt.Color(255, 255, 255));
        JLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        JLabel3.setText("Status :");

        ComboBox_status_karyawan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_status_karyawan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Ada Dalam Bagian", "Tidak Ada Bagian" }));

        button_export.setBackground(new java.awt.Color(255, 255, 255));
        button_export.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export.setText("Export to Excel");
        button_export.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_exportActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_detail_grup1Layout = new javax.swing.GroupLayout(jPanel_detail_grup1);
        jPanel_detail_grup1.setLayout(jPanel_detail_grup1Layout);
        jPanel_detail_grup1Layout.setHorizontalGroup(
            jPanel_detail_grup1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_detail_grup1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_detail_grup1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 487, Short.MAX_VALUE)
                    .addGroup(jPanel_detail_grup1Layout.createSequentialGroup()
                        .addComponent(JLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_nama_karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Jlabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_bagian, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_search)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel_detail_grup1Layout.createSequentialGroup()
                        .addComponent(JLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_status_karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_pegawai1, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_export)))
                .addContainerGap())
        );
        jPanel_detail_grup1Layout.setVerticalGroup(
            jPanel_detail_grup1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_detail_grup1Layout.createSequentialGroup()
                .addGroup(jPanel_detail_grup1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(JLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_nama_karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Jlabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_bagian, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_search, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_detail_grup1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(JLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_status_karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_pegawai1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 560, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel_grup_cabut, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel_detail_grup, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel_detail_grup1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel_detail_grup1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel_grup_cabut, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel_detail_grup, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("DATA GRUP WALETA", jPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txt_search_karyawan_pindahGrupKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_karyawan_pindahGrupKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_PindahGrup();
        }
    }//GEN-LAST:event_txt_search_karyawan_pindahGrupKeyPressed

    private void txt_search_bagian_lamaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_bagian_lamaKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_PindahGrup();
        }
    }//GEN-LAST:event_txt_search_bagian_lamaKeyPressed

    private void txt_search_bagian_baruKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_bagian_baruKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_PindahGrup();
        }
    }//GEN-LAST:event_txt_search_bagian_baruKeyPressed

    private void Button_refresh_pindahGrupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_refresh_pindahGrupActionPerformed
        // TODO add your handling code here:
        refreshTable_PindahGrup();
    }//GEN-LAST:event_Button_refresh_pindahGrupActionPerformed

    private void Button_pindahGrup_disetujuiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_pindahGrup_disetujuiActionPerformed
        try {
            int i = tabel_pindah_grup.getSelectedRow();
            if (i >= 0) {
                if (MainForm.Login_Posisi.equals("STAFF 5")) {
                    String nomor = tabel_pindah_grup.getValueAt(i, 0).toString();
                    JDialog_PindahGrup_disetujui dialog = new JDialog_PindahGrup_disetujui(new javax.swing.JFrame(), true, nomor, "disetujui");
                    dialog.pack();
                    dialog.setLocationRelativeTo(this);
                    dialog.setVisible(true);
                    dialog.setEnabled(true);
                    dialog.setResizable(false);
                    refreshTable_PindahGrup();
                } else {
                    JOptionPane.showMessageDialog(this, "Maaf hanya login user STAFF 5 yang dapat menyetujui permintaan pindah grup!");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Anda belum memilih data!");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_DataPindahBagian.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_Button_pindahGrup_disetujuiActionPerformed

    private void Button_pindahGrup_diketahuiHRActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_pindahGrup_diketahuiHRActionPerformed
        // TODO add your handling code here:
        try {
            int i = tabel_pindah_grup.getSelectedRow();
            if (i >= 0) {
                boolean check = true;
                String login_departemen = "";
                sql = "SELECT `kode_departemen` FROM `tb_bagian` WHERE `kode_bagian` = '" + MainForm.Login_kodeBagian + "'";
                rs = Utility.db.getStatement().executeQuery(sql);
                if (rs.next()) {
                    login_departemen = rs.getString("kode_departemen");
                }
                if (!login_departemen.equals("HRGA") && !MainForm.Login_idPegawai.equals("20180102221")) {
                    JOptionPane.showMessageDialog(this, "Harap login menggunakan user HRD !");
                    check = false;
                } else {
                    sql = "SELECT COUNT(IF(`status`='OK', 1, 0)) AS 'status' FROM `tb_grup` WHERE 1 ";
                    rs = Utility.db.getStatement().executeQuery(sql);
                    if (rs.next()) {
                        if (rs.getInt("status") == 0) {
                            JOptionPane.showMessageDialog(this, "tidak bisa edit grup, fungsi edit grup masih terkunci !");
                            check = false;
                        }
                    }
                }

                if (check) {
                    String nomor = tabel_pindah_grup.getValueAt(tabel_pindah_grup.getSelectedRow(), 0).toString();
                    JDialog_PindahGrup_disetujui dialog = new JDialog_PindahGrup_disetujui(new javax.swing.JFrame(), true, nomor, "HR");
                    dialog.pack();
                    dialog.setLocationRelativeTo(this);
                    dialog.setVisible(true);
                    dialog.setEnabled(true);
                    dialog.setResizable(false);
                    refreshTable_PindahGrup();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Anda belum memilih data!");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_DataPindahBagian.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_Button_pindahGrup_diketahuiHRActionPerformed

    private void Button_pindahGrup_diketahuiKeuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_pindahGrup_diketahuiKeuActionPerformed
        // TODO add your handling code here:
        try {
            int i = tabel_pindah_grup.getSelectedRow();
            if (i >= 0) {
                String login_departemen = "";
                sql = "SELECT `kode_departemen` FROM `tb_bagian` WHERE `kode_bagian` = '" + MainForm.Login_kodeBagian + "'";
                rs = Utility.db.getStatement().executeQuery(sql);
                if (rs.next()) {
                    login_departemen = rs.getString("kode_departemen");
                }

                boolean check = true;
                if (!login_departemen.equals("KEUANGAN") && !MainForm.Login_idPegawai.equals("20180102221")) {
                    JOptionPane.showMessageDialog(this, "Harap login menggunakan user KEUANGAN !");
                    check = false;
                }

                if (check) {
                    String nomor = tabel_pindah_grup.getValueAt(tabel_pindah_grup.getSelectedRow(), 0).toString();
                    JDialog_PindahGrup_disetujui dialog = new JDialog_PindahGrup_disetujui(new javax.swing.JFrame(), true, nomor, "Keuangan");
                    dialog.pack();
                    dialog.setLocationRelativeTo(this);
                    dialog.setVisible(true);
                    dialog.setEnabled(true);
                    dialog.setResizable(false);
                    refreshTable_PindahGrup();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Anda belum memilih data!");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_DataPindahBagian.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_Button_pindahGrup_diketahuiKeuActionPerformed

    private void Button_delete_permintaan_pindahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_delete_permintaan_pindahActionPerformed
        // TODO add your handling code here:
        int i = tabel_pindah_grup.getSelectedRow();
        if (i >= 0) {
            String nomor = tabel_pindah_grup.getValueAt(i, 0).toString();
            int dialogResult = JOptionPane.showConfirmDialog(this, "Hapus data permintaan pindah grup " + tabel_pindah_grup.getValueAt(i, 3).toString() + "?", "Warning", 0);
            if (dialogResult == JOptionPane.YES_OPTION) {
                try {
                    sql = "DELETE FROM `tb_form_pindah_grup` WHERE `nomor` = '" + nomor + "'";
                    if (Utility.db.getStatement().executeUpdate(sql) > 0) {
                        JOptionPane.showMessageDialog(this, "Data permintaan pindah grup " + tabel_pindah_grup.getValueAt(i, 3).toString() + " telah di hapus !");
                        refreshTable_PindahGrup();
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, ex);
                    Logger.getLogger(JPanel_DataPindahBagian.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Anda belum memilih data!");
        }
    }//GEN-LAST:event_Button_delete_permintaan_pindahActionPerformed

    private void Button_pindahGrup_disetujui_semuaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_pindahGrup_disetujui_semuaActionPerformed
        // TODO add your handling code here:
        boolean check = true;
        if (!MainForm.Login_Posisi.equals("STAFF 5")) {
            check = false;
            JOptionPane.showMessageDialog(this, "Maaf hanya login user STAFF 5 yang dapat menyetujui permintaan pindah grup!");
        }

        int dialogResult = JOptionPane.showConfirmDialog(this, "Harap pastikan semua data sudah BENAR, lanjutkan?", "Warning", 0);
        if (dialogResult == JOptionPane.YES_OPTION && check) {
            try {
                int data_masuk = 0;
                Utility.db.getConnection().setAutoCommit(false);
                for (int i = 0; i < tabel_pindah_grup.getRowCount(); i++) {
                    if (tabel_pindah_grup.getValueAt(i, 10) == null) {//row yang belum disetujui
                        String nomor = tabel_pindah_grup.getValueAt(i, 0).toString();
                        String nama = tabel_pindah_grup.getValueAt(i, 3).toString();

                        boolean isPindahBagian = false;
                        String bagian_lama = "";
                        if (tabel_pindah_grup.getValueAt(i, 4) != null) {
                            bagian_lama = tabel_pindah_grup.getValueAt(i, 4).toString();
                        }
                        String check_bagian = bagian_lama;
                        if (tabel_pindah_grup.getValueAt(i, 5) != null) {
                            String bagian_baru = tabel_pindah_grup.getValueAt(i, 5).toString();
                            isPindahBagian = !bagian_lama.equals(bagian_baru);
                            check_bagian = tabel_pindah_grup.getValueAt(i, 5).toString();
                        }

                        boolean isPindahJamKerja = false;
                        String jamkerja_lama = "";
                        if (tabel_pindah_grup.getValueAt(i, 6) != null) {
                            jamkerja_lama = tabel_pindah_grup.getValueAt(i, 6).toString();
                        }
                        if (tabel_pindah_grup.getValueAt(i, 7) != null) {
                            String jamkerja_baru = tabel_pindah_grup.getValueAt(i, 7).toString();
                            isPindahJamKerja = !jamkerja_lama.equals(jamkerja_baru);
                        }

                        boolean isPindahLevelGaji = false;
                        String levelgaji_lama = "";
                        if (tabel_pindah_grup.getValueAt(i, 8) != null) {
                            levelgaji_lama = tabel_pindah_grup.getValueAt(i, 8).toString();
                        }
                        String check_levelgaji = levelgaji_lama;
                        if (tabel_pindah_grup.getValueAt(i, 9) != null) {
                            String levelgaji_baru = tabel_pindah_grup.getValueAt(i, 9).toString();
                            isPindahLevelGaji = !levelgaji_lama.equals(levelgaji_baru);
                            check_levelgaji = tabel_pindah_grup.getValueAt(i, 9).toString();
                        }

                        String jenis_levelgaji = "";
                        sql = "SELECT `bagian` FROM `tb_level_gaji` WHERE `level_gaji` = '" + check_levelgaji + "'";
                        rs = Utility.db.getStatement().executeQuery(sql);
                        if (rs.next()) {
                            jenis_levelgaji = rs.getString("bagian");
                        } else {
                            throw new Exception("Level gaji baru tidak ditemukan !");
                        }

                        if (!(isPindahBagian || isPindahJamKerja || isPindahLevelGaji)) {
                            throw new Exception("Tidak ada perubahan pada " + nama + ", data tidak bisa di simpan !");
                        } else if (jenis_levelgaji.equals("HARIAN") && (check_bagian.toUpperCase().contains("-CABUT-BORONG") || check_bagian.toUpperCase().contains("-CABUT-TRAINING"))) {
                            throw new Exception(nama + " adalah karyawan BORONG Cabut tidak dapat memakai level gaji HARIAN !");
                        } else if (jenis_levelgaji.equals("CABUT") && !(check_bagian.toUpperCase().contains("-CABUT-BORONG") || check_bagian.toUpperCase().contains("-CABUT-TRAINING"))) {
                            throw new Exception(nama + " adalah karyawan HARIAN tidak dapat memakai level gaji CABUT !");
                        }

                        sql = "UPDATE `tb_form_pindah_grup` SET "
                                + "`disetujui`='" + MainForm.Login_NamaPegawai + "', `jam_disetujui`=NOW() "
                                + "WHERE `nomor` = '" + nomor + "'";
                        Utility.db.getStatement().executeUpdate(sql);
                        data_masuk++;
                    }
                }
                Utility.db.getConnection().commit();
                JOptionPane.showMessageDialog(this, data_masuk + "Data pindah grup berhasil disetujui semua !");
            } catch (Exception ex) {
                try {
                    Utility.db.getConnection().rollback();
                } catch (SQLException ex1) {
                    Logger.getLogger(JPanel_DataPindahBagian.class.getName()).log(Level.SEVERE, null, ex1);
                }
                JOptionPane.showMessageDialog(this, ex);
                Logger.getLogger(JDialog_PindahGrup_disetujui.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    Utility.db.getConnection().setAutoCommit(true);
                } catch (SQLException ex) {
                    Logger.getLogger(JPanel_DataPindahBagian.class.getName()).log(Level.SEVERE, null, ex);
                }
                refreshTable_PindahGrup();
            }
        }
    }//GEN-LAST:event_Button_pindahGrup_disetujui_semuaActionPerformed

    private void Button_pindahGrup_diketahuiHR_semuaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_pindahGrup_diketahuiHR_semuaActionPerformed
        // TODO add your handling code here:
        boolean check = true;
        try {
            if (!MainForm.Login_Departemen.equals("HRGA")) {
                JOptionPane.showMessageDialog(this, "Harap login menggunakan user HRD !");
                check = false;
            } else {
                sql = "SELECT COUNT(IF(`status`='OK', 1, 0)) AS 'status' FROM `tb_grup` WHERE 1 ";
                rs = Utility.db.getStatement().executeQuery(sql);
                if (rs.next()) {
                    if (rs.getInt("status") == 0) {
                        JOptionPane.showMessageDialog(this, "tidak bisa edit grup, fungsi edit grup masih terkunci !");
                        check = false;
                    }
                }
            }
        } catch (SQLException ex) {
            check = false;
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_DataPindahBagian.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (check) {
            int dialogResult = JOptionPane.showConfirmDialog(this, "Harap pastikan semua data sudah BENAR\nSistem akan otomatis edit grup, bagian & jam Kerja, lanjutkan?", "Warning", 0);
            if (dialogResult == JOptionPane.YES_OPTION) {
                try {
                    int data_masuk = 0;
                    Utility.db.getConnection().setAutoCommit(false);
                    for (int i = 0; i < tabel_pindah_grup.getRowCount(); i++) {
                        if (tabel_pindah_grup.getValueAt(i, 10) != null
                                && tabel_pindah_grup.getValueAt(i, 12) == null
                                ) {//row yang belum diketahui hr & sudah disetujui
                            String nomor = tabel_pindah_grup.getValueAt(i, 0).toString();
                            String id_pegawai = tabel_pindah_grup.getValueAt(i, 2).toString();
                            String bagian_baru = "";
                            String jamkerja_baru = "";

                            String update_bagian_baru = "";
                            if (tabel_pindah_grup.getValueAt(i, 5) != null) {
                                bagian_baru = tabel_pindah_grup.getValueAt(i, 5).toString();
                                String query_update_bagian_baru = "UPDATE `tb_karyawan` SET `kode_bagian`=(SELECT `kode_bagian` FROM `tb_bagian` WHERE `nama_bagian` = '" + bagian_baru + "') "
                                        + "WHERE `id_pegawai` = '" + id_pegawai + "'";
                                Utility.db.getStatement().executeUpdate(query_update_bagian_baru);
                                update_bagian_baru = "`bagian_baru`='" + bagian_baru + "', ";
                            }

                            String update_jam_kerja_baru = "";
                            if (tabel_pindah_grup.getValueAt(i, 7) != null) {
                                jamkerja_baru = tabel_pindah_grup.getValueAt(i, 7).toString();
                                String query_update_jam_kerja_baru = "UPDATE `tb_karyawan` SET `jam_kerja` = '" + jamkerja_baru + "' WHERE `id_pegawai` = '" + id_pegawai + "'";
                                Utility.db.getStatement().executeUpdate(query_update_jam_kerja_baru);
                                update_jam_kerja_baru = "`jamkerja_baru`='" + jamkerja_baru + "', ";
                            }

                            sql = "UPDATE `tb_form_pindah_grup` SET "
                                    + update_bagian_baru
                                    + update_jam_kerja_baru
                                    + "`diketahui_hr`='" + MainForm.Login_NamaPegawai + "', "
                                    + "`jam_diketahui_hr`=NOW() "
                                    + "WHERE `nomor` = '" + nomor + "'";
                            Utility.db.getStatement().executeUpdate(sql);
                            data_masuk++;
                        }
                    }
                    Utility.db.getConnection().commit();
                    JOptionPane.showMessageDialog(this, data_masuk + " Data pindah grup berhasil diketahui HR semua !");
                } catch (SQLException ex) {
                    try {
                        Utility.db.getConnection().rollback();
                    } catch (SQLException ex1) {
                        Logger.getLogger(JPanel_DataPindahBagian.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                    JOptionPane.showMessageDialog(this, ex);
                    Logger.getLogger(JDialog_PindahGrup_disetujui.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    try {
                        Utility.db.getConnection().setAutoCommit(true);
                    } catch (SQLException ex) {
                        Logger.getLogger(JPanel_DataPindahBagian.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    refreshTable_PindahGrup();
                }
            }
        }
    }//GEN-LAST:event_Button_pindahGrup_diketahuiHR_semuaActionPerformed

    private void Button_pindahGrup_diketahuiKeu_semuaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_pindahGrup_diketahuiKeu_semuaActionPerformed
        // TODO add your handling code here:
        boolean check = true;
        try {
            sql = "SELECT `kode_departemen` FROM `tb_bagian` WHERE `kode_bagian` = '" + MainForm.Login_kodeBagian + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                if (!rs.getString("kode_departemen").equals("KEUANGAN")) {
                    JOptionPane.showMessageDialog(this, "Harap login menggunakan user KEUANGAN !");
                    check = false;
                }
            }
        } catch (SQLException ex) {
            check = false;
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_DataPindahBagian.class.getName()).log(Level.SEVERE, null, ex);
        }

        int dialogResult = JOptionPane.showConfirmDialog(this, "Harap pastikan semua data sudah BENAR!!\nSistem akan otomatis edit level gaji, lanjutkan?", "Warning", 0);
        if (dialogResult == JOptionPane.YES_OPTION && check) {
            try {
                int data_masuk = 0;
                Utility.db.getConnection().setAutoCommit(false);
                for (int i = 0; i < tabel_pindah_grup.getRowCount(); i++) {
                    if (tabel_pindah_grup.getValueAt(i, 10) != null 
                            && tabel_pindah_grup.getValueAt(i, 14) == null) {//row yang belum diketahui keuangan & sudah disetujui
                        String nomor = tabel_pindah_grup.getValueAt(i, 0).toString();
                        String id_pegawai = tabel_pindah_grup.getValueAt(i, 2).toString();
                        String nama = tabel_pindah_grup.getValueAt(i, 3).toString();

                        String bagian = tabel_pindah_grup.getValueAt(i, 4).toString();
                        if (tabel_pindah_grup.getValueAt(i, 5) != null) {
                            bagian = tabel_pindah_grup.getValueAt(i, 5).toString();
                        }
                        String levelgaji = tabel_pindah_grup.getValueAt(i, 8).toString();
                        if (tabel_pindah_grup.getValueAt(i, 9) != null) {
                            levelgaji = tabel_pindah_grup.getValueAt(i, 9).toString();
                        }

                        String jenis_levelgaji = "";
                        sql = "SELECT `bagian` FROM `tb_level_gaji` WHERE `level_gaji` = '" + levelgaji + "'";
                        rs = Utility.db.getStatement().executeQuery(sql);
                        if (rs.next()) {
                            jenis_levelgaji = rs.getString("bagian");
                        } else {
                            throw new Exception("Level gaji baru tidak ditemukan !");
                        }

                        if (jenis_levelgaji.equals("HARIAN") && (bagian.toUpperCase().contains("-CABUT-BORONG") || bagian.toUpperCase().contains("-CABUT-TRAINING"))) {
                            throw new Exception(nama + " adalah karyawan BORONG Cabut tidak dapat memakai level gaji HARIAN !");
                        } else if (jenis_levelgaji.equals("CABUT") && !(bagian.toUpperCase().contains("-CABUT-BORONG") || bagian.toUpperCase().contains("-CABUT-TRAINING"))) {
                            throw new Exception(nama + " adalah karyawan HARIAN tidak dapat memakai level gaji CABUT !");
                        }

                        if (tabel_pindah_grup.getValueAt(i, 9) != null) {
                            String query_update_levelgaji_baru = "UPDATE `tb_karyawan` SET `level_gaji`='" + tabel_pindah_grup.getValueAt(i, 9).toString() + "' "
                                    + "WHERE `id_pegawai` = '" + id_pegawai + "'";
                            Utility.db.getStatement().executeUpdate(query_update_levelgaji_baru);
                        }

                        sql = "UPDATE `tb_form_pindah_grup` SET "
                                + "`diketahui_keu`='" + MainForm.Login_NamaPegawai + "', "
                                + "`jam_diketahui_keu`=NOW() "
                                + "WHERE `nomor` = '" + nomor + "'";
                        Utility.db.getStatement().executeUpdate(sql);
                        data_masuk++;
                    }
                }
                Utility.db.getConnection().commit();
                JOptionPane.showMessageDialog(this, data_masuk + "Data pindah grup berhasil diketahui KEUANGAN semua !");
            } catch (Exception ex) {
                try {
                    Utility.db.getConnection().rollback();
                } catch (SQLException ex1) {
                    Logger.getLogger(JPanel_DataPindahBagian.class.getName()).log(Level.SEVERE, null, ex1);
                }
                JOptionPane.showMessageDialog(this, ex);
                Logger.getLogger(JDialog_PindahGrup_disetujui.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    Utility.db.getConnection().setAutoCommit(true);
                } catch (SQLException ex) {
                    Logger.getLogger(JPanel_DataPindahBagian.class.getName()).log(Level.SEVERE, null, ex);
                }
                refreshTable_PindahGrup();
            }
        }
    }//GEN-LAST:event_Button_pindahGrup_diketahuiKeu_semuaActionPerformed

    private void ComboBox_ruanganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ComboBox_ruanganActionPerformed
        // TODO add your handling code here:
        //        refreshTable_Grup();
    }//GEN-LAST:event_ComboBox_ruanganActionPerformed

    private void button_laporanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_laporanActionPerformed
        // TODO add your handling code here:
        try {
            String ruang = ComboBox_ruangan.getSelectedItem().toString();
            if (!ruang.equals("All")) {
                JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Data_Grup.jrxml");
                Map<String, Object> map = new HashMap<>();
                map.put("SUBREPORT_DIR", "Report\\");
                map.put("RUANGAN", ruang);//parameter name should be like it was named inside your report.
                JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
                JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, map, Utility.db.getConnection());
                JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
            } else {
                JOptionPane.showMessageDialog(this, "Laporan per ruangan, silahkan pilih ruangan terlebih dahulu");
            }
        } catch (JRException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_DataCabut.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_laporanActionPerformed

    private void button_subsidiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_subsidiActionPerformed
        // TODO add your handling code here:
        int row = tb_detail_grup.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Silahkan karyawan yang akan pindah");
        } else {
            if (tb_detail_grup.getValueAt(row, 2).toString().contains("TRAINING")) {
                String id = tb_detail_grup.getValueAt(row, 0).toString();
                JDialog_EditSubsidi dialog = new JDialog_EditSubsidi(new javax.swing.JFrame(), true, id);
                dialog.pack();
                dialog.setLocationRelativeTo(this);
                dialog.setVisible(true);
                dialog.setEnabled(true);
                int x = tb_grup.getSelectedRow();
                if (x > -1) {
                    refreshTable_detailGrup();
                    refreshTable_Grup();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Hanya bisa pindah bagian training di menu ini!");
            }
        }
    }//GEN-LAST:event_button_subsidiActionPerformed

    private void txt_nama_karyawanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_nama_karyawanKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_dataKaryawan();
        }
    }//GEN-LAST:event_txt_nama_karyawanKeyPressed

    private void txt_bagianKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_bagianKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_dataKaryawan();
        }
    }//GEN-LAST:event_txt_bagianKeyPressed

    private void button_searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_searchActionPerformed
        // TODO add your handling code here:
        refreshTable_dataKaryawan();
    }//GEN-LAST:event_button_searchActionPerformed

    private void button_exportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_exportActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) tb_data_karyawan.getModel();
        ExportToExcel.writeToExcel(model, this);
    }//GEN-LAST:event_button_exportActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Button_delete_permintaan_pindah;
    private javax.swing.JButton Button_pindahGrup_diketahuiHR;
    private javax.swing.JButton Button_pindahGrup_diketahuiHR_semua;
    private javax.swing.JButton Button_pindahGrup_diketahuiKeu;
    private javax.swing.JButton Button_pindahGrup_diketahuiKeu_semua;
    private javax.swing.JButton Button_pindahGrup_disetujui;
    private javax.swing.JButton Button_pindahGrup_disetujui_semua;
    private javax.swing.JButton Button_refresh_pindahGrup;
    private javax.swing.JComboBox<String> ComboBox_Date_filter_pindahGrup;
    private javax.swing.JComboBox<String> ComboBox_filter_diketahui_HR;
    private javax.swing.JComboBox<String> ComboBox_filter_diketahui_Keu;
    private javax.swing.JComboBox<String> ComboBox_filter_disetujui;
    private javax.swing.JComboBox<String> ComboBox_ruangan;
    private javax.swing.JComboBox<String> ComboBox_status_karyawan;
    private com.toedter.calendar.JDateChooser Date_filter_pindahGrup1;
    private com.toedter.calendar.JDateChooser Date_filter_pindahGrup2;
    private javax.swing.JLabel JLabel1;
    private javax.swing.JLabel JLabel2;
    private javax.swing.JLabel JLabel3;
    private javax.swing.JLabel JLabel4;
    private javax.swing.JLabel JLabel5;
    private javax.swing.JLabel JLabel6;
    private javax.swing.JLabel JLabel7;
    private javax.swing.JLabel JLabel8;
    private javax.swing.JLabel JLabel9;
    private javax.swing.JLabel Jlabel2;
    private javax.swing.JLabel Jlabel3;
    private javax.swing.JButton button_export;
    private javax.swing.JButton button_laporan;
    private javax.swing.JButton button_search;
    private javax.swing.JButton button_subsidi;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel_detail_grup;
    private javax.swing.JPanel jPanel_detail_grup1;
    private javax.swing.JPanel jPanel_grup_cabut;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JLabel label_kode_grup;
    private javax.swing.JLabel label_ruang;
    private javax.swing.JLabel label_total_data_pindahGrup;
    private javax.swing.JLabel label_total_grup;
    private javax.swing.JLabel label_total_pegawai;
    private javax.swing.JLabel label_total_pegawai1;
    private javax.swing.JTable tabel_pindah_grup;
    private javax.swing.JTable tb_data_karyawan;
    private javax.swing.JTable tb_detail_grup;
    private javax.swing.JTable tb_grup;
    private javax.swing.JTextField txt_bagian;
    private javax.swing.JTextField txt_nama_karyawan;
    private javax.swing.JTextField txt_search_bagian_baru;
    private javax.swing.JTextField txt_search_bagian_lama;
    private javax.swing.JTextField txt_search_karyawan_pindahGrup;
    // End of variables declaration//GEN-END:variables
}
