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

public class JPanel_ProgressSPKPengiriman extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();

    public JPanel_ProgressSPKPengiriman() {
        initComponents();
        table_data_progress_packing.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && table_data_progress_packing.getSelectedRow() != -1) {
                    int i = table_data_progress_packing.getSelectedRow();
                    String no_grade_spk = table_data_progress_packing.getValueAt(i, 0).toString();
                    label_no_grade_spk.setText(no_grade_spk);
                    refreshTable_detail_harian();
                }
            }
        });
    }

    public void init() {
        refreshTable();
    }

    public void refreshTable() {
        try {
            int total_kpg = 0;
            float total_gram = 0;
            DefaultTableModel model = (DefaultTableModel) table_data_progress_packing.getModel();
            model.setRowCount(0);
            String status_spk = " AND `tb_spk`.`status` = '" + ComboBox_search_status_spk.getSelectedItem().toString() + "' ";
            if (ComboBox_search_status_spk.getSelectedItem() == "All") {
                status_spk = "";
            }
            String tanggal_AWB = "";
            if (Date_filterAWB1.getDate() != null && Date_filterAWB2.getDate() != null) {
                tanggal_AWB = " AND `tb_spk`.`tanggal_awb` BETWEEN '" + dateFormat.format(Date_filterAWB1.getDate()) + "' AND '" + dateFormat.format(Date_filterAWB2.getDate()) + "' ";
            }
            sql = "SELECT `no`, `tb_spk_detail`.`kode_spk`, `tb_spk`.`buyer`, `tb_spk`.`status`, `tb_spk`.`tanggal_awb`, `grade_waleta`, `grade_buyer`, `berat_kemasan`, `jumlah_kemasan`, `tb_spk_detail`.`berat`, `harga_cny`, `keterangan`, "
                    + "`progress_turun`, "
                    //                    + "TB_HOTGUN.`hotgun`, "
                    //                    + "TB_VAKUM.`vakum`, "
                    + "DETAIL_PROGRESS.`hotgun`, "
                    + "DETAIL_PROGRESS.`vakum`, "
                    + "SCAN_QR.`gram_scan_qr`, "
                    + "SCAN_FINAL.`jumlah_box`, "
                    + "SCAN_FINAL.`jumlah_selesai` "
                    + "FROM `tb_spk_detail` "
                    + "LEFT JOIN `tb_spk` ON `tb_spk_detail`.`kode_spk` = `tb_spk`.`kode_spk` "
                    //------------------------------------------------------------------------------------------------------------------------------
                    + "LEFT JOIN ("
                    + "SELECT `no_grade_spk`, SUM(`turun_barang`) AS 'turun_barang', SUM(`hot_gun`) AS 'hotgun', SUM(`vakum`) AS 'vakum', SUM(`scan`) AS 'scan' "
                    + "FROM `tb_spk_detail_progress` "
                    + "WHERE 1 "
                    + "GROUP BY `no_grade_spk`"
                    + ") DETAIL_PROGRESS ON `tb_spk_detail`.`no` = DETAIL_PROGRESS.`no_grade_spk` \n"
                    //------------------------------------------------------------------------------------------------------------------------------
                    + "LEFT JOIN ("
                    + "SELECT `no_grade_spk`, SUM(`tb_box_bahan_jadi`.`berat`) AS 'progress_turun' \n"
                    + "FROM `tb_box_packing` \n"
                    + "LEFT JOIN `tb_box_bahan_jadi` ON `tb_box_packing`.`no_box` = `tb_box_bahan_jadi`.`no_box`\n"
                    + "WHERE "
                    + "`no_grade_spk` IS NOT NULL "
                    + "AND `tb_box_bahan_jadi`.`lokasi_terakhir` = 'PACKING'\n"
                    + "GROUP BY `no_grade_spk`"
                    + ") TURUN ON `tb_spk_detail`.`no` = TURUN.`no_grade_spk` \n"
                    //------------------------------------------------------------------------------------------------------------------------------
                    //                    + "LEFT JOIN ("
                    //                    + "SELECT `tb_box_packing`.`no_grade_spk`, `tb_kinerja_packing_vakum`.`no_box`, SUM(`tb_box_bahan_jadi`.`berat`) AS 'vakum' \n"
                    //                    + "FROM `tb_kinerja_packing_vakum` \n"
                    //                    + "LEFT JOIN `tb_box_bahan_jadi` ON `tb_kinerja_packing_vakum`.`no_box` = `tb_box_bahan_jadi`.`no_box`\n"
                    //                    + "LEFT JOIN `tb_box_packing` ON `tb_kinerja_packing_vakum`.`no_box` = `tb_box_packing`.`no_box`\n"
                    //                    + "WHERE 1 GROUP BY `no_grade_spk`"
                    //                    + ") TB_VAKUM "
                    //                    + "ON `tb_spk_detail`.`no` = TB_VAKUM.`no_grade_spk` "
                    //------------------------------------------------------------------------------------------------------------------------------
                    //                    + "LEFT JOIN ("
                    //                    + "SELECT `tb_box_packing`.`no_grade_spk`, `tb_kinerja_packing_hotgun`.`no_box`, SUM(`tb_box_bahan_jadi`.`berat`) AS 'hotgun' \n"
                    //                    + "FROM `tb_kinerja_packing_hotgun` \n"
                    //                    + "LEFT JOIN `tb_box_bahan_jadi` ON `tb_kinerja_packing_hotgun`.`no_box` = `tb_box_bahan_jadi`.`no_box`\n"
                    //                    + "LEFT JOIN `tb_box_packing` ON `tb_kinerja_packing_hotgun`.`no_box` = `tb_box_packing`.`no_box`\n"
                    //                    + "WHERE 1 GROUP BY `no_grade_spk`"
                    //                    + ") TB_HOTGUN "
                    //                    + "ON `tb_spk_detail`.`no` = TB_HOTGUN.`no_grade_spk` "
                    //------------------------------------------------------------------------------------------------------------------------------
                    + "LEFT JOIN ("
                    + "SELECT `no_grade_spk`, SUM(`gram`) AS 'gram_scan_qr' FROM `tb_scan_qr_packing` WHERE 1 GROUP BY `no_grade_spk`"
                    + ") SCAN_QR ON `tb_spk_detail`.`no` = SCAN_QR.`no_grade_spk`"
                    //------------------------------------------------------------------------------------------------------------------------------
                    + "LEFT JOIN ("
                    + "SELECT `no_grade_spk`, COUNT(`no_barcode`) AS 'jumlah_box', SUM(IF(`final_packing` = 'SELESAI', 1, 0)) AS 'jumlah_selesai' "
                    + "FROM `tb_barcode_pengiriman` "
                    + "WHERE 1 "
                    + "GROUP BY `no_grade_spk` "
                    + ") SCAN_FINAL ON `tb_spk_detail`.`no` = SCAN_FINAL.`no_grade_spk`"
                    //------------------------------------------------------------------------------------------------------------------------------
                    + "WHERE `tb_spk_detail`.`kode_spk` LIKE '%" + txt_kode_spk.getText() + "%' "
                    + "AND `grade_waleta` LIKE '%" + txt_grade_waleta.getText() + "%' "
                    + "AND `grade_buyer` LIKE '%" + txt_grade_buyer.getText() + "%' "
                    + tanggal_AWB + status_spk
                    + "GROUP BY `no`";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[20];
            while (rs.next()) {
                row[0] = rs.getInt("no");
                row[1] = rs.getString("kode_spk");
                row[2] = rs.getString("buyer");
                row[3] = rs.getDate("tanggal_awb");
                row[4] = rs.getString("status");
                row[5] = rs.getString("grade_waleta");
                row[6] = rs.getString("grade_buyer");
                row[7] = rs.getFloat("berat");
                row[8] = rs.getFloat("progress_turun");
                float persen_turun = 0;
                float persen_hotgun = 0;
                float persen_vakum = 0;
                float persen_scan_qr = 0;
                if (rs.getFloat("berat") > 0) {
                    persen_turun = Math.round((rs.getFloat("progress_turun") / rs.getFloat("berat")) * 1000f) / 10f;
                    persen_hotgun = Math.round((rs.getFloat("hotgun") / rs.getFloat("berat")) * 1000f) / 10f;
                    persen_vakum = Math.round((rs.getFloat("vakum") / rs.getFloat("berat")) * 1000f) / 10f;
                    persen_scan_qr = Math.round((rs.getFloat("gram_scan_qr") / rs.getFloat("berat")) * 1000f) / 10f;
                }
                row[9] = persen_turun;
                row[10] = rs.getFloat("hotgun");
                row[11] = persen_hotgun;
                row[12] = rs.getFloat("vakum");
                row[13] = persen_vakum;
                row[14] = rs.getFloat("gram_scan_qr");
                row[15] = persen_scan_qr;
                row[16] = Math.round((rs.getFloat("jumlah_selesai") / rs.getFloat("jumlah_box")) * 1000f) / 10f;
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(table_data_progress_packing);

            int total_data = table_data_progress_packing.getRowCount();
            label_total_data.setText(decimalFormat.format(total_data));
            label_total_keping.setText(decimalFormat.format(total_kpg));
            label_total_gram.setText(decimalFormat.format(total_gram));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_ProgressSPKPengiriman.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_detail_harian() {
        try {
            DefaultTableModel model = (DefaultTableModel) tabel_detail_update_harian.getModel();
            model.setRowCount(0);
            sql = "SELECT `no`, `no_grade_spk`, `tanggal_input`, `turun_barang`, `hot_gun`, `vakum`, `scan` "
                    + "FROM `tb_spk_detail_progress` "
                    + "WHERE `no_grade_spk` = '" + label_no_grade_spk.getText() + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[10];
            while (rs.next()) {
                row[0] = rs.getInt("no");
                row[1] = rs.getDate("tanggal_input");
                row[2] = rs.getFloat("turun_barang");
                row[3] = rs.getFloat("hot_gun");
                row[4] = rs.getFloat("vakum");
                row[5] = rs.getFloat("scan");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(tabel_detail_update_harian);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_ProgressSPKPengiriman.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table_data_progress_packing = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        txt_kode_spk = new javax.swing.JTextField();
        button_search = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        label_total_data = new javax.swing.JLabel();
        label_total_keping = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        label_total_gram = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        button_terima = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        ComboBox_search_status_spk = new javax.swing.JComboBox<>();
        button_export = new javax.swing.JButton();
        txt_grade_waleta = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        Date_filterAWB1 = new com.toedter.calendar.JDateChooser();
        Date_filterAWB2 = new com.toedter.calendar.JDateChooser();
        jLabel9 = new javax.swing.JLabel();
        txt_grade_buyer = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        button_edit_data_kinerja = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabel_detail_update_harian = new javax.swing.JTable();
        label_no_grade_spk = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        button_delete_data_kinerja = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Progress SPK Pengiriman", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        table_data_progress_packing.setAutoCreateRowSorter(true);
        table_data_progress_packing.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_data_progress_packing.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "Kode SPK", "Buyer", "Tgl AWB", "Status SPK", "Grade WLT", "Grade Buyer", "Gr Pesanan", "Turun Pck", "%", "Hot Gun", "%", "Vakum", "%", "Scan QR", "%", "% Final Scan"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
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
        table_data_progress_packing.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(table_data_progress_packing);

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("Kode SPK :");

        txt_kode_spk.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_kode_spk.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_kode_spkKeyPressed(evt);
            }
        });

        button_search.setBackground(new java.awt.Color(255, 255, 255));
        button_search.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search.setText("Search");
        button_search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_searchActionPerformed(evt);
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

        button_terima.setBackground(new java.awt.Color(255, 255, 255));
        button_terima.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_terima.setText("Input Data Hari ini");
        button_terima.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_terimaActionPerformed(evt);
            }
        });

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel6.setText("Status SPK :");

        ComboBox_search_status_spk.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_search_status_spk.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "PROSES", "SEND OUT" }));
        ComboBox_search_status_spk.setSelectedIndex(1);

        button_export.setBackground(new java.awt.Color(255, 255, 255));
        button_export.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export.setText("Export");
        button_export.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_exportActionPerformed(evt);
            }
        });

        txt_grade_waleta.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_grade_waleta.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_grade_waletaKeyPressed(evt);
            }
        });

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel7.setText("Grade Buyer :");

        Date_filterAWB1.setDateFormatString("dd MMMM yyyy");
        Date_filterAWB1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Date_filterAWB2.setDateFormatString("dd MMMM yyyy");
        Date_filterAWB2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel9.setText("Grade WLT :");

        txt_grade_buyer.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_grade_buyer.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_grade_buyerKeyPressed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Detail Data Update Harian", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 12))); // NOI18N

        button_edit_data_kinerja.setBackground(new java.awt.Color(255, 255, 255));
        button_edit_data_kinerja.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_edit_data_kinerja.setText("Edit");
        button_edit_data_kinerja.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_edit_data_kinerjaActionPerformed(evt);
            }
        });

        tabel_detail_update_harian.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "Tanggal", "Turun", "Hot Gun", "Vakum", "Scan"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
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
        jScrollPane2.setViewportView(tabel_detail_update_harian);

        label_no_grade_spk.setBackground(new java.awt.Color(255, 255, 255));
        label_no_grade_spk.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_no_grade_spk.setText("0");

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel11.setText("No Grade SPK :");

        button_delete_data_kinerja.setBackground(new java.awt.Color(255, 255, 255));
        button_delete_data_kinerja.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_delete_data_kinerja.setText("Delete");
        button_delete_data_kinerja.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_delete_data_kinerjaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 409, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_no_grade_spk)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_delete_data_kinerja)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_edit_data_kinerja)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_edit_data_kinerja)
                    .addComponent(label_no_grade_spk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_delete_data_kinerja))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 563, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel10.setText("Tanggal AWB :");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_kode_spk, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_grade_waleta, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_grade_buyer, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_search_status_spk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_filterAWB1, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_filterAWB2, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_search)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_terima)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_export))
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
                                .addComponent(label_total_gram)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 896, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(ComboBox_search_status_spk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_grade_buyer, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_grade_waleta, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_kode_spk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(Date_filterAWB1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_filterAWB2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(button_search, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_terima, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_export, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(label_total_data)
                            .addComponent(jLabel3)
                            .addComponent(label_total_keping)
                            .addComponent(jLabel4)
                            .addComponent(label_total_gram))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(3, 3, 3))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void button_searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_searchActionPerformed
        // TODO add your handling code here:
        refreshTable();
    }//GEN-LAST:event_button_searchActionPerformed

    private void button_terimaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_terimaActionPerformed
        // TODO add your handling code here:
        int j = table_data_progress_packing.getSelectedRow();
        if (j == -1) {
            JOptionPane.showMessageDialog(this, "Silahkan Pilih data yang mau di edit !");
        } else {
            JDialog_input_kinerjaPacking dialog = new JDialog_input_kinerjaPacking(new javax.swing.JFrame(), true, table_data_progress_packing.getValueAt(j, 0).toString(), null);
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            dialog.setEnabled(true);
            dialog.setResizable(false);
            refreshTable_detail_harian();
            refreshTable();
        }
    }//GEN-LAST:event_button_terimaActionPerformed

    private void button_exportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_exportActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) table_data_progress_packing.getModel();
        ExportToExcel.writeToExcel(model, this);
    }//GEN-LAST:event_button_exportActionPerformed

    private void txt_grade_buyerKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_grade_buyerKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_grade_buyerKeyPressed

    private void txt_grade_waletaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_grade_waletaKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_grade_waletaKeyPressed

    private void txt_kode_spkKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_kode_spkKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_kode_spkKeyPressed

    private void button_edit_data_kinerjaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_edit_data_kinerjaActionPerformed
        // TODO add your handling code here:
        int j = tabel_detail_update_harian.getSelectedRow();
        if (j == -1) {
            JOptionPane.showMessageDialog(this, "Silahkan Pilih data yang mau di edit !");
        } else {
            JDialog_input_kinerjaPacking dialog = new JDialog_input_kinerjaPacking(new javax.swing.JFrame(), true, null, tabel_detail_update_harian.getValueAt(j, 0).toString());
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            dialog.setEnabled(true);
            dialog.setResizable(false);
            refreshTable_detail_harian();
            refreshTable();
        }
    }//GEN-LAST:event_button_edit_data_kinerjaActionPerformed

    private void button_delete_data_kinerjaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_delete_data_kinerjaActionPerformed
        // TODO add your handling code here:
        try {
            int i = tabel_detail_update_harian.getSelectedRow();
            if (i > -1) {
                String delete = "DELETE FROM `tb_spk_detail_progress` WHERE `no` = '" + tabel_detail_update_harian.getValueAt(i, 0).toString() + "'";
                Utility.db.getConnection().createStatement();
                if (Utility.db.getStatement().executeUpdate(delete) == 1) {
                    JOptionPane.showMessageDialog(this, "Data berhasil dihapus !");
                    refreshTable_detail_harian();
                    refreshTable();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed !");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Silahkan Klik data yang akan di hapus pada tabel !");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(JDialog_ScanQR.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_delete_data_kinerjaActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_search_status_spk;
    private com.toedter.calendar.JDateChooser Date_filterAWB1;
    private com.toedter.calendar.JDateChooser Date_filterAWB2;
    private javax.swing.JButton button_delete_data_kinerja;
    private javax.swing.JButton button_edit_data_kinerja;
    private javax.swing.JButton button_export;
    public static javax.swing.JButton button_search;
    public javax.swing.JButton button_terima;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel label_no_grade_spk;
    private javax.swing.JLabel label_total_data;
    private javax.swing.JLabel label_total_gram;
    private javax.swing.JLabel label_total_keping;
    private javax.swing.JTable tabel_detail_update_harian;
    public static javax.swing.JTable table_data_progress_packing;
    private javax.swing.JTextField txt_grade_buyer;
    private javax.swing.JTextField txt_grade_waleta;
    private javax.swing.JTextField txt_kode_spk;
    // End of variables declaration//GEN-END:variables
}
