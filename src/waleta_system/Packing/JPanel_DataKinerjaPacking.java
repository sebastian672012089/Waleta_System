package waleta_system.Packing;

import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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

public class JPanel_DataKinerjaPacking extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();

    public JPanel_DataKinerjaPacking() {
        initComponents();
    }

    public void init() {
        refreshTable_hotgun();
        refreshTable_vakum();
    }

    public void refreshTable_hotgun() {
        try {
            float total_kpg = 0, total_gram = 0;
            DefaultTableModel model = (DefaultTableModel) table_data_progress_hotgun.getModel();
            model.setRowCount(0);
            String status_spk = " AND `tb_spk`.`status` = '" + ComboBox_status_spk_hotgun.getSelectedItem().toString() + "' ";
            if (ComboBox_status_spk_hotgun.getSelectedItem() == "All") {
                status_spk = "";
            }
            String tanggal_hotgun = "";
            if (Date_hotgun1.getDate() != null && Date_hotgun2.getDate() != null) {
                tanggal_hotgun = " AND `tanggal_hotgun` BETWEEN '" + dateFormat.format(Date_hotgun1.getDate()) + "' AND '" + dateFormat.format(Date_hotgun2.getDate()) + "' ";
            }
            sql = "SELECT `tb_kinerja_packing_hotgun`.`no_box`, `tb_box_bahan_jadi`.`keping`, `tb_box_bahan_jadi`.`berat`, `tb_karyawan`.`nama_pegawai`, `tanggal_hotgun`, "
                    + "`tb_spk_detail`.`kode_spk`, `tb_spk`.`buyer`, `tb_spk`.`tanggal_awb`, `tb_spk`.`status`, `grade_waleta`, `grade_buyer`  "
                    + "FROM `tb_kinerja_packing_hotgun` "
                    + "LEFT JOIN `tb_box_bahan_jadi` ON `tb_kinerja_packing_hotgun`.`no_box` = `tb_box_bahan_jadi`.`no_box` "
                    + "LEFT JOIN `tb_box_packing` ON `tb_kinerja_packing_hotgun`.`no_box` = `tb_box_packing`.`no_box` "
                    + "LEFT JOIN `tb_spk_detail` ON `tb_box_packing`.`no_grade_spk` = `tb_spk_detail`.`no` "
                    + "LEFT JOIN `tb_spk` ON `tb_spk_detail`.`kode_spk` = `tb_spk`.`kode_spk` "
                    + "LEFT JOIN `tb_karyawan` ON `tb_kinerja_packing_hotgun`.`id_pegawai` = `tb_karyawan`.`id_pegawai` "
                    + "WHERE "
                    + "`tb_kinerja_packing_hotgun`.`no_box` LIKE '%" + txt_no_box_hotgun.getText() + "%' "
                    + "AND `tb_karyawan`.`nama_pegawai` LIKE '%" + txt_search_karyawan_hotgun.getText() + "%' "
                    + "AND `tb_spk_detail`.`kode_spk` LIKE '%" + txt_kode_spk_hotgun.getText() + "%' "
                    + "AND `grade_waleta` LIKE '%" + txt_grade_waleta_hotgun.getText() + "%' "
                    + "AND `grade_buyer` LIKE '%" + txt_grade_buyer_hotgun.getText() + "%' "
                    + tanggal_hotgun 
                    + status_spk;
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[15];
            while (rs.next()) {
                row[0] = rs.getString("no_box");
                row[1] = rs.getFloat("keping");
                row[2] = rs.getFloat("berat");
                row[3] = rs.getString("kode_spk");
                row[4] = rs.getString("buyer");
                row[5] = rs.getDate("tanggal_awb");
                row[6] = rs.getString("status");
                row[7] = rs.getString("grade_waleta");
                row[8] = rs.getString("grade_buyer");
                row[9] = rs.getString("nama_pegawai");
                row[10] = rs.getDate("tanggal_hotgun");
                model.addRow(row);
                total_kpg = total_kpg + rs.getFloat("keping");
                total_gram = total_gram + rs.getFloat("berat");
            }
            ColumnsAutoSizer.sizeColumnsToFit(table_data_progress_hotgun);

            int total_data = table_data_progress_hotgun.getRowCount();
            label_total_data.setText(Integer.toString(total_data));
            label_total_keping.setText(decimalFormat.format(total_kpg));
            label_total_gram.setText(decimalFormat.format(total_gram));
            
            DefaultTableModel model_rekap = (DefaultTableModel) tabel_rekap_pekerja_hotgun.getModel();
            model_rekap.setRowCount(0);
            sql = "SELECT `tb_karyawan`.`nama_pegawai`, SUM(`tb_box_bahan_jadi`.`berat`) AS 'total_berat'  "
                    + "FROM `tb_kinerja_packing_hotgun` "
                    + "LEFT JOIN `tb_box_bahan_jadi` ON `tb_kinerja_packing_hotgun`.`no_box` = `tb_box_bahan_jadi`.`no_box` "
                    + "LEFT JOIN `tb_box_packing` ON `tb_kinerja_packing_hotgun`.`no_box` = `tb_box_packing`.`no_box` "
                    + "LEFT JOIN `tb_spk_detail` ON `tb_box_packing`.`no_grade_spk` = `tb_spk_detail`.`no` "
                    + "LEFT JOIN `tb_spk` ON `tb_spk_detail`.`kode_spk` = `tb_spk`.`kode_spk` "
                    + "LEFT JOIN `tb_karyawan` ON `tb_kinerja_packing_hotgun`.`id_pegawai` = `tb_karyawan`.`id_pegawai` "
                    + "WHERE "
                    + "`tb_kinerja_packing_hotgun`.`no_box` LIKE '%" + txt_no_box_hotgun.getText() + "%' "
                    + "AND `tb_karyawan`.`nama_pegawai` LIKE '%" + txt_search_karyawan_hotgun.getText() + "%' "
                    + "AND `tb_spk_detail`.`kode_spk` LIKE '%" + txt_kode_spk_hotgun.getText() + "%' "
                    + "AND `grade_waleta` LIKE '%" + txt_grade_waleta_hotgun.getText() + "%' "
                    + "AND `grade_buyer` LIKE '%" + txt_grade_buyer_hotgun.getText() + "%' "
                    + tanggal_hotgun 
                    + status_spk
                    + " GROUP BY `tb_kinerja_packing_hotgun`.`id_pegawai`";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] baris = new Object[5];
            while (rs.next()) {
                baris[0] = rs.getString("nama_pegawai");
                baris[1] = rs.getFloat("total_berat");
                model_rekap.addRow(baris);
            }
            ColumnsAutoSizer.sizeColumnsToFit(tabel_rekap_pekerja_hotgun);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_DataKinerjaPacking.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    

    public void refreshTable_vakum() {
        try {
            float total_kpg = 0, total_gram = 0;
            DefaultTableModel model = (DefaultTableModel) table_data_progress_vakum.getModel();
            model.setRowCount(0);
            String status_spk = " AND `tb_spk`.`status` = '" + ComboBox_status_spk_vakum.getSelectedItem().toString() + "' ";
            if (ComboBox_status_spk_vakum.getSelectedItem() == "All") {
                status_spk = "";
            }
            String tanggal_vakum = "";
            if (Date_vakum1.getDate() != null && Date_vakum2.getDate() != null) {
                tanggal_vakum = " AND `tanggal_vakum` BETWEEN '" + dateFormat.format(Date_vakum1.getDate()) + "' AND '" + dateFormat.format(Date_vakum2.getDate()) + "' ";
            }
            sql = "SELECT `tb_kinerja_packing_vakum`.`no_box`, `tb_box_bahan_jadi`.`keping`, `tb_box_bahan_jadi`.`berat`, `tb_karyawan`.`nama_pegawai`, `tanggal_vakum`, "
                    + "`tb_spk_detail`.`kode_spk`, `tb_spk`.`buyer`, `tb_spk`.`tanggal_awb`, `tb_spk`.`status`, `grade_waleta`, `grade_buyer`  "
                    + "FROM `tb_kinerja_packing_vakum` "
                    + "LEFT JOIN `tb_box_bahan_jadi` ON `tb_kinerja_packing_vakum`.`no_box` = `tb_box_bahan_jadi`.`no_box` "
                    + "LEFT JOIN `tb_box_packing` ON `tb_kinerja_packing_vakum`.`no_box` = `tb_box_packing`.`no_box` "
                    + "LEFT JOIN `tb_spk_detail` ON `tb_box_packing`.`no_grade_spk` = `tb_spk_detail`.`no` "
                    + "LEFT JOIN `tb_spk` ON `tb_spk_detail`.`kode_spk` = `tb_spk`.`kode_spk` "
                    + "LEFT JOIN `tb_karyawan` ON `tb_kinerja_packing_vakum`.`id_pegawai` = `tb_karyawan`.`id_pegawai` "
                    + "WHERE "
                    + "`tb_kinerja_packing_vakum`.`no_box` LIKE '%" + txt_no_box_vakum.getText() + "%' "
                    + "AND `tb_karyawan`.`nama_pegawai` LIKE '%" + txt_search_karyawan_vakum.getText() + "%' "
                    + "AND `tb_spk_detail`.`kode_spk` LIKE '%" + txt_kode_spk_vakum.getText() + "%' "
                    + "AND `grade_waleta` LIKE '%" + txt_grade_waleta_vakum.getText() + "%' "
                    + "AND `grade_buyer` LIKE '%" + txt_grade_buyer_vakum.getText() + "%' "
                    + tanggal_vakum 
                    + status_spk;
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[15];
            while (rs.next()) {
                row[0] = rs.getString("no_box");
                row[1] = rs.getFloat("keping");
                row[2] = rs.getFloat("berat");
                row[3] = rs.getString("kode_spk");
                row[4] = rs.getString("buyer");
                row[5] = rs.getDate("tanggal_awb");
                row[6] = rs.getString("status");
                row[7] = rs.getString("grade_waleta");
                row[8] = rs.getString("grade_buyer");
                row[9] = rs.getString("nama_pegawai");
                row[10] = rs.getDate("tanggal_vakum");
                model.addRow(row);
                total_kpg = total_kpg + rs.getFloat("keping");
                total_gram = total_gram + rs.getFloat("berat");
            }
            ColumnsAutoSizer.sizeColumnsToFit(table_data_progress_vakum);

            int total_data = table_data_progress_vakum.getRowCount();
            label_total_data_vakum.setText(Integer.toString(total_data));
            label_total_keping_vakum.setText(decimalFormat.format(total_kpg));
            label_total_gram_vakum.setText(decimalFormat.format(total_gram));
            
            DefaultTableModel model_rekap = (DefaultTableModel) tabel_rekap_pekerja_vakum.getModel();
            model_rekap.setRowCount(0);
            sql = "SELECT `tb_karyawan`.`nama_pegawai`, SUM(`tb_box_bahan_jadi`.`berat`) AS 'total_berat'  "
                    + "FROM `tb_kinerja_packing_vakum` "
                    + "LEFT JOIN `tb_box_bahan_jadi` ON `tb_kinerja_packing_vakum`.`no_box` = `tb_box_bahan_jadi`.`no_box` "
                    + "LEFT JOIN `tb_box_packing` ON `tb_kinerja_packing_vakum`.`no_box` = `tb_box_packing`.`no_box` "
                    + "LEFT JOIN `tb_spk_detail` ON `tb_box_packing`.`no_grade_spk` = `tb_spk_detail`.`no` "
                    + "LEFT JOIN `tb_spk` ON `tb_spk_detail`.`kode_spk` = `tb_spk`.`kode_spk` "
                    + "LEFT JOIN `tb_karyawan` ON `tb_kinerja_packing_vakum`.`id_pegawai` = `tb_karyawan`.`id_pegawai` "
                    + "WHERE "
                    + "`tb_kinerja_packing_vakum`.`no_box` LIKE '%" + txt_no_box_vakum.getText() + "%' "
                    + "AND `tb_karyawan`.`nama_pegawai` LIKE '%" + txt_search_karyawan_vakum.getText() + "%' "
                    + "AND `tb_spk_detail`.`kode_spk` LIKE '%" + txt_kode_spk_vakum.getText() + "%' "
                    + "AND `grade_waleta` LIKE '%" + txt_grade_waleta_vakum.getText() + "%' "
                    + "AND `grade_buyer` LIKE '%" + txt_grade_buyer_vakum.getText() + "%' "
                    + tanggal_vakum 
                    + status_spk
                    + " GROUP BY `tb_kinerja_packing_vakum`.`id_pegawai`";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] baris = new Object[5];
            while (rs.next()) {
                baris[0] = rs.getString("nama_pegawai");
                baris[1] = rs.getFloat("total_berat");
                model_rekap.addRow(baris);
            }
            ColumnsAutoSizer.sizeColumnsToFit(tabel_rekap_pekerja_vakum);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_DataKinerjaPacking.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table_data_progress_hotgun = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        txt_kode_spk_hotgun = new javax.swing.JTextField();
        button_search_hotgun = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        label_total_data = new javax.swing.JLabel();
        label_total_keping = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        label_total_gram = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        ComboBox_status_spk_hotgun = new javax.swing.JComboBox<>();
        button_export_hotgun = new javax.swing.JButton();
        txt_grade_waleta_hotgun = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        Date_hotgun1 = new com.toedter.calendar.JDateChooser();
        Date_hotgun2 = new com.toedter.calendar.JDateChooser();
        jLabel9 = new javax.swing.JLabel();
        txt_grade_buyer_hotgun = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabel_rekap_pekerja_hotgun = new javax.swing.JTable();
        jLabel10 = new javax.swing.JLabel();
        txt_search_karyawan_hotgun = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txt_no_box_hotgun = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        table_data_progress_vakum = new javax.swing.JTable();
        jLabel11 = new javax.swing.JLabel();
        txt_kode_spk_vakum = new javax.swing.JTextField();
        button_search_vakum = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        label_total_data_vakum = new javax.swing.JLabel();
        label_total_keping_vakum = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        label_total_gram_vakum = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        ComboBox_status_spk_vakum = new javax.swing.JComboBox<>();
        button_export_vakum = new javax.swing.JButton();
        txt_grade_waleta_vakum = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        Date_vakum1 = new com.toedter.calendar.JDateChooser();
        Date_vakum2 = new com.toedter.calendar.JDateChooser();
        jLabel17 = new javax.swing.JLabel();
        txt_grade_buyer_vakum = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tabel_rekap_pekerja_vakum = new javax.swing.JTable();
        jLabel18 = new javax.swing.JLabel();
        txt_search_karyawan_vakum = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        txt_no_box_vakum = new javax.swing.JTextField();

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Data Kinerja Packing", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N

        jTabbedPane1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        table_data_progress_hotgun.setAutoCreateRowSorter(true);
        table_data_progress_hotgun.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_data_progress_hotgun.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No Box", "Kpg", "Gram", "Kode SPK", "Buyer", "Tgl AWB", "Status SPK", "Grade WLT", "Grade Buyer", "Pekerja HotGun", "Tgl HotGun"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Float.class, java.lang.Float.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class
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
        table_data_progress_hotgun.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(table_data_progress_hotgun);

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("Kode SPK :");

        txt_kode_spk_hotgun.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_kode_spk_hotgun.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_kode_spk_hotgunKeyPressed(evt);
            }
        });

        button_search_hotgun.setBackground(new java.awt.Color(255, 255, 255));
        button_search_hotgun.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search_hotgun.setText("Search");
        button_search_hotgun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_search_hotgunActionPerformed(evt);
            }
        });

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("Total Data :");

        label_total_data.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_data.setText("0");

        label_total_keping.setBackground(new java.awt.Color(255, 255, 255));
        label_total_keping.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_keping.setText("0");

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("Keping :");

        label_total_gram.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_gram.setText("0");

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel4.setText("Gram :");

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel6.setText("Status SPK :");

        ComboBox_status_spk_hotgun.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_status_spk_hotgun.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "PROSES", "SEND OUT" }));

        button_export_hotgun.setBackground(new java.awt.Color(255, 255, 255));
        button_export_hotgun.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_hotgun.setText("Export");
        button_export_hotgun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_hotgunActionPerformed(evt);
            }
        });

        txt_grade_waleta_hotgun.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_grade_waleta_hotgun.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_grade_waleta_hotgunKeyPressed(evt);
            }
        });

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel7.setText("Grade Buyer :");

        Date_hotgun1.setDateFormatString("dd MMMM yyyy");
        Date_hotgun1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Date_hotgun2.setDateFormatString("dd MMMM yyyy");
        Date_hotgun2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel9.setText("Grade WLT :");

        txt_grade_buyer_hotgun.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_grade_buyer_hotgun.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_grade_buyer_hotgunKeyPressed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Rekap per Karyawan", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 12))); // NOI18N

        tabel_rekap_pekerja_hotgun.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Pekerja HotGun", "Total Gram"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tabel_rekap_pekerja_hotgun);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 413, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2)
                .addContainerGap())
        );

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel10.setText("Tanggal HotGun :");

        txt_search_karyawan_hotgun.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_karyawan_hotgun.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_karyawan_hotgunKeyPressed(evt);
            }
        });

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel5.setText("Nama Karyawan :");

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel8.setText("No Box :");

        txt_no_box_hotgun.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_no_box_hotgun.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_no_box_hotgunKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_data)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_keping)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_gram))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel8)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txt_no_box_hotgun, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel5)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txt_search_karyawan_hotgun, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel10)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(Date_hotgun1, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(Date_hotgun2, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel1)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txt_kode_spk_hotgun, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel7)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txt_grade_waleta_hotgun, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel9)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txt_grade_buyer_hotgun, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel6)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(ComboBox_status_spk_hotgun, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(button_search_hotgun)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_export_hotgun)))
                        .addGap(0, 53, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_kode_spk_hotgun, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_grade_waleta_hotgun, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_grade_buyer_hotgun, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_export_hotgun, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ComboBox_status_spk_hotgun, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_search_hotgun, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txt_no_box_hotgun, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txt_search_karyawan_hotgun, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(Date_hotgun1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Date_hotgun2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 550, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(label_total_data)
                            .addComponent(jLabel3)
                            .addComponent(label_total_keping)
                            .addComponent(jLabel4)
                            .addComponent(label_total_gram))
                        .addContainerGap())))
        );

        jTabbedPane1.addTab("Hot Gun", jPanel2);

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        table_data_progress_vakum.setAutoCreateRowSorter(true);
        table_data_progress_vakum.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_data_progress_vakum.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No Box", "Kpg", "Gram", "Kode SPK", "Buyer", "Tgl AWB", "Status SPK", "Grade WLT", "Grade Buyer", "Pekerja HotGun", "Tgl HotGun"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Float.class, java.lang.Float.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class
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
        table_data_progress_vakum.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(table_data_progress_vakum);

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel11.setText("Kode SPK :");

        txt_kode_spk_vakum.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_kode_spk_vakum.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_kode_spk_vakumKeyPressed(evt);
            }
        });

        button_search_vakum.setBackground(new java.awt.Color(255, 255, 255));
        button_search_vakum.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search_vakum.setText("Search");
        button_search_vakum.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_search_vakumActionPerformed(evt);
            }
        });

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel12.setText("Total Data :");

        label_total_data_vakum.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_vakum.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_data_vakum.setText("0");

        label_total_keping_vakum.setBackground(new java.awt.Color(255, 255, 255));
        label_total_keping_vakum.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_keping_vakum.setText("0");

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel13.setText("Keping :");

        label_total_gram_vakum.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_vakum.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_gram_vakum.setText("0");

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel14.setText("Gram :");

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel15.setText("Status SPK :");

        ComboBox_status_spk_vakum.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_status_spk_vakum.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "PROSES", "SEND OUT" }));

        button_export_vakum.setBackground(new java.awt.Color(255, 255, 255));
        button_export_vakum.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_vakum.setText("Export");
        button_export_vakum.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_vakumActionPerformed(evt);
            }
        });

        txt_grade_waleta_vakum.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_grade_waleta_vakum.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_grade_waleta_vakumKeyPressed(evt);
            }
        });

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel16.setText("Grade Buyer :");

        Date_vakum1.setDateFormatString("dd MMMM yyyy");
        Date_vakum1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Date_vakum2.setDateFormatString("dd MMMM yyyy");
        Date_vakum2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel17.setText("Grade WLT :");

        txt_grade_buyer_vakum.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_grade_buyer_vakum.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_grade_buyer_vakumKeyPressed(evt);
            }
        });

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Rekap per Karyawan", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 12))); // NOI18N

        tabel_rekap_pekerja_vakum.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Pekerja HotGun", "Total Gram"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane4.setViewportView(tabel_rekap_pekerja_vakum);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 413, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4)
                .addContainerGap())
        );

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel18.setText("Tanggal Vakum :");

        txt_search_karyawan_vakum.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_karyawan_vakum.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_karyawan_vakumKeyPressed(evt);
            }
        });

        jLabel19.setBackground(new java.awt.Color(255, 255, 255));
        jLabel19.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel19.setText("Nama Karyawan :");

        jLabel20.setBackground(new java.awt.Color(255, 255, 255));
        jLabel20.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel20.setText("No Box :");

        txt_no_box_vakum.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_no_box_vakum.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_no_box_vakumKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_data_vakum)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_keping_vakum)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_gram_vakum))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                                        .addComponent(jLabel20)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txt_no_box_vakum, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel19)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txt_search_karyawan_vakum, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel18)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(Date_vakum1, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(Date_vakum2, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addComponent(jLabel11)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txt_kode_spk_vakum, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel16)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txt_grade_waleta_vakum, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel17)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txt_grade_buyer_vakum, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel15)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(ComboBox_status_spk_vakum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(button_search_vakum)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_export_vakum)))
                        .addGap(0, 53, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_kode_spk_vakum, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_grade_waleta_vakum, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_grade_buyer_vakum, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_export_vakum, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ComboBox_status_spk_vakum, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_search_vakum, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txt_no_box_vakum, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txt_search_karyawan_vakum, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(Date_vakum1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Date_vakum2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 550, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel12)
                            .addComponent(label_total_data_vakum)
                            .addComponent(jLabel13)
                            .addComponent(label_total_keping_vakum)
                            .addComponent(jLabel14)
                            .addComponent(label_total_gram_vakum))
                        .addContainerGap())))
        );

        jTabbedPane1.addTab("Vakum", jPanel4);

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

    private void button_search_hotgunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_hotgunActionPerformed
        // TODO add your handling code here:
        refreshTable_hotgun();
    }//GEN-LAST:event_button_search_hotgunActionPerformed

    private void button_export_hotgunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_hotgunActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) table_data_progress_hotgun.getModel();
        ExportToExcel.writeToExcel(model, this);
    }//GEN-LAST:event_button_export_hotgunActionPerformed

    private void txt_grade_buyer_hotgunKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_grade_buyer_hotgunKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_hotgun();
        }
    }//GEN-LAST:event_txt_grade_buyer_hotgunKeyPressed

    private void txt_grade_waleta_hotgunKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_grade_waleta_hotgunKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_hotgun();
        }
    }//GEN-LAST:event_txt_grade_waleta_hotgunKeyPressed

    private void txt_kode_spk_hotgunKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_kode_spk_hotgunKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_hotgun();
        }
    }//GEN-LAST:event_txt_kode_spk_hotgunKeyPressed

    private void txt_search_karyawan_hotgunKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_karyawan_hotgunKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_hotgun();
        }
    }//GEN-LAST:event_txt_search_karyawan_hotgunKeyPressed

    private void txt_no_box_hotgunKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_no_box_hotgunKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_vakum();
        }
    }//GEN-LAST:event_txt_no_box_hotgunKeyPressed

    private void txt_kode_spk_vakumKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_kode_spk_vakumKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_vakum();
        }
    }//GEN-LAST:event_txt_kode_spk_vakumKeyPressed

    private void button_search_vakumActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_vakumActionPerformed
        // TODO add your handling code here:
        refreshTable_vakum();
    }//GEN-LAST:event_button_search_vakumActionPerformed

    private void button_export_vakumActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_vakumActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) table_data_progress_vakum.getModel();
        ExportToExcel.writeToExcel(model, this);
    }//GEN-LAST:event_button_export_vakumActionPerformed

    private void txt_grade_waleta_vakumKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_grade_waleta_vakumKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_vakum();
        }
    }//GEN-LAST:event_txt_grade_waleta_vakumKeyPressed

    private void txt_grade_buyer_vakumKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_grade_buyer_vakumKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_vakum();
        }
    }//GEN-LAST:event_txt_grade_buyer_vakumKeyPressed

    private void txt_search_karyawan_vakumKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_karyawan_vakumKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_vakum();
        }
    }//GEN-LAST:event_txt_search_karyawan_vakumKeyPressed

    private void txt_no_box_vakumKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_no_box_vakumKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_vakum();
        }
    }//GEN-LAST:event_txt_no_box_vakumKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_status_spk_hotgun;
    private javax.swing.JComboBox<String> ComboBox_status_spk_vakum;
    private com.toedter.calendar.JDateChooser Date_hotgun1;
    private com.toedter.calendar.JDateChooser Date_hotgun2;
    private com.toedter.calendar.JDateChooser Date_vakum1;
    private com.toedter.calendar.JDateChooser Date_vakum2;
    private javax.swing.JButton button_export_hotgun;
    private javax.swing.JButton button_export_vakum;
    public static javax.swing.JButton button_search_hotgun;
    public static javax.swing.JButton button_search_vakum;
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
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel label_total_data;
    private javax.swing.JLabel label_total_data_vakum;
    private javax.swing.JLabel label_total_gram;
    private javax.swing.JLabel label_total_gram_vakum;
    private javax.swing.JLabel label_total_keping;
    private javax.swing.JLabel label_total_keping_vakum;
    private javax.swing.JTable tabel_rekap_pekerja_hotgun;
    private javax.swing.JTable tabel_rekap_pekerja_vakum;
    public static javax.swing.JTable table_data_progress_hotgun;
    public static javax.swing.JTable table_data_progress_vakum;
    private javax.swing.JTextField txt_grade_buyer_hotgun;
    private javax.swing.JTextField txt_grade_buyer_vakum;
    private javax.swing.JTextField txt_grade_waleta_hotgun;
    private javax.swing.JTextField txt_grade_waleta_vakum;
    private javax.swing.JTextField txt_kode_spk_hotgun;
    private javax.swing.JTextField txt_kode_spk_vakum;
    private javax.swing.JTextField txt_no_box_hotgun;
    private javax.swing.JTextField txt_no_box_vakum;
    private javax.swing.JTextField txt_search_karyawan_hotgun;
    private javax.swing.JTextField txt_search_karyawan_vakum;
    // End of variables declaration//GEN-END:variables
}
