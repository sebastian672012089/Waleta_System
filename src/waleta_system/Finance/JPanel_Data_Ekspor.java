package waleta_system.Finance;

import waleta_system.Class.Utility;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import waleta_system.BahanBaku.JPanel_GradeBahanBaku;
import waleta_system.Class.ColumnsAutoSizer;

import waleta_system.Class.ExportToExcel;

public class JPanel_Data_Ekspor extends javax.swing.JPanel {

    
    String sql = null;
    String sql2 = null;
    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();

    public JPanel_Data_Ekspor() {
        initComponents();
    }

    public void init() {
        try {
            
            
            refreshTable_pengiriman();
        } catch (Exception ex) {
            Logger.getLogger(JPanel_GradeBahanBaku.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_pengiriman() {
        try {
            String SearchCat = null, SearchJenisPengiriman = null, filter_tanggal = null;
            double total_kpg = 0;
            double total_gram = 0;
            if (ComboBox_Search.getSelectedItem() == "NO INVOICE") {
                SearchCat = "invoice_no";
            } else if (ComboBox_Search.getSelectedItem() == "NAMA BUYER") {
                SearchCat = "nama";
            } else if (ComboBox_Search.getSelectedItem() == "NO DOKUMEN") {
                SearchCat = "no_dokumen";
            }

            if (ComboBox_jenisPengiriman.getSelectedItem() == "All") {
                SearchJenisPengiriman = "";
            } else if (ComboBox_jenisPengiriman.getSelectedItem() == "Sampel Internal") {
                SearchJenisPengiriman = "AND `jenis_pengiriman` = 'Sampel Internal'";
            } else if (ComboBox_jenisPengiriman.getSelectedItem() == "Sampel Eksternal") {
                SearchJenisPengiriman = "AND `jenis_pengiriman` = 'Sampel Eksternal'";
            } else if (ComboBox_jenisPengiriman.getSelectedItem() == "Ekspor") {
                SearchJenisPengiriman = "AND `jenis_pengiriman` = 'Ekspor'";
            } else if (ComboBox_jenisPengiriman.getSelectedItem() == "Ekspor Esta") {
                SearchJenisPengiriman = "AND `jenis_pengiriman` = 'Ekspor Esta'";
            } else if (ComboBox_jenisPengiriman.getSelectedItem() == "Ekspor Sub") {
                SearchJenisPengiriman = "AND `jenis_pengiriman` = 'Ekspor Sub'";
            } else if (ComboBox_jenisPengiriman.getSelectedItem() == "Ekspor JT") {
                SearchJenisPengiriman = "AND `jenis_pengiriman` = 'Ekspor JT'";
            } else if (ComboBox_jenisPengiriman.getSelectedItem() == "Lokal") {
                SearchJenisPengiriman = "AND `jenis_pengiriman` = 'Lokal'";
            }

            if (ComboBox_Filter_tanggal.getSelectedItem() == "TGL INVOICE") {
                filter_tanggal = "`tb_pengiriman`.`tanggal_invoice`";
            } else {
                filter_tanggal = "`tb_pengiriman`.`tanggal_pengiriman`";
            }

            DefaultTableModel model = (DefaultTableModel) tabel_rekap_box.getModel();
            model.setRowCount(0);
            if (Date_Search1.getDate() != null && Date_Search2.getDate() != null) {
                sql2 = "SELECT `tb_box_packing`.`invoice_pengiriman`, `tb_pengiriman`.`tanggal_invoice`, `tb_pengiriman`.`tanggal_pengiriman`, `tb_pengiriman`.`kode_buyer`, `tb_buyer`.`nama`, `keterangan`, `jenis_pengiriman`, `tb_grade_bahan_jadi`.`kode_grade`, SUM(`tb_box_bahan_jadi`.`keping`) AS 'total_keping', SUM(`tb_box_bahan_jadi`.`berat`) AS 'total_gram'\n"
                        + "FROM `tb_box_packing` LEFT JOIN `tb_box_bahan_jadi` ON `tb_box_packing`.`no_box` = `tb_box_bahan_jadi`.`no_box`\n"
                        + "LEFT JOIN `tb_pengiriman` ON `tb_box_packing`.`invoice_pengiriman` = `tb_pengiriman`.`invoice_no`\n"
                        + "LEFT JOIN `tb_buyer` ON `tb_buyer`.`kode_buyer` = `tb_pengiriman`.`kode_buyer`\n"
                        + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_box_bahan_jadi`.`kode_grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode`\n"
                        + "WHERE `" + SearchCat + "` LIKE '%" + txt_search_keywords.getText() + "%' AND (" + filter_tanggal + " BETWEEN '" + dateFormat.format(Date_Search1.getDate()) + "' AND '" + dateFormat.format(Date_Search2.getDate()) + "')" + SearchJenisPengiriman
                        + "GROUP BY `tb_pengiriman`.`invoice_no`, `tb_grade_bahan_jadi`.`kode_grade`";
            } else {
                sql2 = "SELECT `tb_box_packing`.`invoice_pengiriman`, `tb_pengiriman`.`tanggal_invoice`, `tb_pengiriman`.`tanggal_pengiriman`, `tb_pengiriman`.`kode_buyer`, `tb_buyer`.`nama`, `keterangan`, `jenis_pengiriman`, `tb_grade_bahan_jadi`.`kode_grade`, SUM(`tb_box_bahan_jadi`.`keping`) AS 'total_keping', SUM(`tb_box_bahan_jadi`.`berat`) AS 'total_gram'\n"
                        + "FROM `tb_box_packing` LEFT JOIN `tb_box_bahan_jadi` ON `tb_box_packing`.`no_box` = `tb_box_bahan_jadi`.`no_box`\n"
                        + "LEFT JOIN `tb_pengiriman` ON `tb_box_packing`.`invoice_pengiriman` = `tb_pengiriman`.`invoice_no`\n"
                        + "LEFT JOIN `tb_buyer` ON `tb_buyer`.`kode_buyer` = `tb_pengiriman`.`kode_buyer`\n"
                        + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_box_bahan_jadi`.`kode_grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode`\n"
                        + "WHERE `" + SearchCat + "` LIKE '%" + txt_search_keywords.getText() + "%' " + SearchJenisPengiriman
                        + "GROUP BY `tb_pengiriman`.`invoice_no`, `tb_grade_bahan_jadi`.`kode_grade`";
            }
            rs = Utility.db.getStatement().executeQuery(sql2);
            Object[] row = new Object[10];
            while (rs.next()) {
                row[0] = rs.getString("invoice_pengiriman");
                row[1] = rs.getDate("tanggal_invoice");
                row[2] = rs.getDate("tanggal_pengiriman");
                row[3] = rs.getString("kode_buyer");
                row[4] = rs.getString("nama");
                row[5] = rs.getString("jenis_pengiriman");
                row[6] = rs.getString("keterangan");
                row[7] = rs.getString("kode_grade");
                row[8] = rs.getInt("total_keping");
                row[9] = rs.getFloat("total_gram");
                total_kpg = total_kpg + rs.getInt("total_keping");
                total_gram = total_gram + rs.getDouble("total_gram");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(tabel_rekap_box);
            label_total_keping_pengiriman.setText(decimalFormat.format(total_kpg));
            label_total_gram_pengiriman.setText(decimalFormat.format(total_gram));
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_Data_Ekspor.class.getName()).log(Level.SEVERE, null, ex);
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
        jLabel1 = new javax.swing.JLabel();
        ComboBox_Search = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        txt_search_keywords = new javax.swing.JTextField();
        button_search = new javax.swing.JButton();
        Date_Search1 = new com.toedter.calendar.JDateChooser();
        Date_Search2 = new com.toedter.calendar.JDateChooser();
        jLabel4 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        label_total_gram_pengiriman = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        label_total_keping_pengiriman = new javax.swing.JLabel();
        button_print = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        tabel_rekap_box = new javax.swing.JTable();
        jLabel6 = new javax.swing.JLabel();
        ComboBox_jenisPengiriman = new javax.swing.JComboBox<>();
        button_export_dataBoxPengiriman1 = new javax.swing.JButton();
        ComboBox_Filter_tanggal = new javax.swing.JComboBox<>();
        button_invoice = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Data Pengiriman", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N
        jPanel1.setPreferredSize(new java.awt.Dimension(1366, 700));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("Search By :");

        ComboBox_Search.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_Search.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "NO INVOICE", "NAMA BUYER", "NO DOKUMEN" }));

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("Keywords :");

        txt_search_keywords.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_keywords.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_keywordsKeyPressed(evt);
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

        Date_Search1.setBackground(new java.awt.Color(255, 255, 255));
        Date_Search1.setDateFormatString("dd MMMM yyyy");
        Date_Search1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Date_Search2.setBackground(new java.awt.Color(255, 255, 255));
        Date_Search2.setDateFormatString("dd MMMM yyyy");
        Date_Search2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel4.setText("Tabel Data Pengiriman Waleta");

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel7.setText("Total Gram :");

        label_total_gram_pengiriman.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_pengiriman.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_gram_pengiriman.setText("0");

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel9.setText("Total Keping :");

        label_total_keping_pengiriman.setBackground(new java.awt.Color(255, 255, 255));
        label_total_keping_pengiriman.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_keping_pengiriman.setText("0");

        button_print.setBackground(new java.awt.Color(255, 255, 255));
        button_print.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_print.setText("Print");
        button_print.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_printActionPerformed(evt);
            }
        });

        tabel_rekap_box.setAutoCreateRowSorter(true);
        tabel_rekap_box.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tabel_rekap_box.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No Invoice", "Tgl Invoice", "Tgl Kirim", "Kode Buyer", "Buyer", "Jenis Pengiriman", "Keterangan", "Grade", "Keping", "Gram"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Float.class
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
        tabel_rekap_box.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(tabel_rekap_box);
        if (tabel_rekap_box.getColumnModel().getColumnCount() > 0) {
            tabel_rekap_box.getColumnModel().getColumn(6).setMaxWidth(200);
        }

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel6.setText("Jenis Pengiriman :");

        ComboBox_jenisPengiriman.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_jenisPengiriman.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Sampel Internal", "Sampel Eksternal", "Ekspor", "Ekspor Esta", "Ekspor Sub", "Ekspor JT", "Lokal" }));

        button_export_dataBoxPengiriman1.setBackground(new java.awt.Color(255, 255, 255));
        button_export_dataBoxPengiriman1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_dataBoxPengiriman1.setText("Export");
        button_export_dataBoxPengiriman1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_dataBoxPengiriman1ActionPerformed(evt);
            }
        });

        ComboBox_Filter_tanggal.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_Filter_tanggal.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "TGL INVOICE", "TGL KIRIM" }));

        button_invoice.setBackground(new java.awt.Color(255, 255, 255));
        button_invoice.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        button_invoice.setText("Scan Invoice");
        button_invoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_invoiceActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_keping_pengiriman)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_gram_pengiriman))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel1)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(ComboBox_Search, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel2)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txt_search_keywords, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel6))
                                    .addComponent(jLabel4))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_jenisPengiriman, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_Filter_tanggal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(7, 7, 7)
                                .addComponent(Date_Search1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_Search2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_search)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_print)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_invoice)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_export_dataBoxPengiriman1)))
                        .addGap(0, 76, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(button_search, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_Search2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_Search1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_keywords, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_Search, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_jenisPengiriman, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_print, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export_dataBoxPengiriman1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_Filter_tanggal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_invoice, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 581, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(label_total_keping_pengiriman)
                    .addComponent(jLabel7)
                    .addComponent(label_total_gram_pengiriman))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txt_search_keywordsKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_keywordsKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_pengiriman();
        }
    }//GEN-LAST:event_txt_search_keywordsKeyPressed

    private void button_searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_searchActionPerformed
        // TODO add your handling code here:
        refreshTable_pengiriman();
    }//GEN-LAST:event_button_searchActionPerformed

    private void button_printActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_printActionPerformed
//        try {
//            int j = tabel_data_pengiriman.getSelectedRow();
//            if (j == -1) {
//                JOptionPane.showMessageDialog(this, "Please select data from the table first", "warning!", 1);
//            } else {
//                JRDesignQuery newQuery = new JRDesignQuery();
//                newQuery.setText(sql2);
//                System.out.println(sql2);
//                JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Laporan_Pengiriman.jrxml");
//                JASP_DESIGN.setQuery(newQuery);
//
//                JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
//                JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, null, Utility.db.getConnection());
//                JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
//            }
//        } catch (JRException ex) {
//            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }//GEN-LAST:event_button_printActionPerformed

    private void button_export_dataBoxPengiriman1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_dataBoxPengiriman1ActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) tabel_rekap_box.getModel();
        ExportToExcel.writeToExcel(model, jPanel1);
    }//GEN-LAST:event_button_export_dataBoxPengiriman1ActionPerformed

    private void button_invoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_invoiceActionPerformed
        // TODO add your handling code here:
        int x = tabel_rekap_box.getSelectedRow();
        if (x < 0) {
            JOptionPane.showMessageDialog(this, "Pilih salah satu data BOX !");
        } else {
            String file_name = tabel_rekap_box.getValueAt(x, 0).toString().replace("/", "_");
            try {
                Runtime rt = Runtime.getRuntime();
                Process pr = rt.exec("explorer.exe \\\\192.168.10.2\\Shared Folder\\DOKUMEN SISTEM\\6_Invoice\\" + file_name + ".pdf");
                } catch (IOException ex) {
                    Logger.getLogger(JPanel_Data_Ekspor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
    }//GEN-LAST:event_button_invoiceActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_Filter_tanggal;
    private javax.swing.JComboBox<String> ComboBox_Search;
    private javax.swing.JComboBox<String> ComboBox_jenisPengiriman;
    private com.toedter.calendar.JDateChooser Date_Search1;
    private com.toedter.calendar.JDateChooser Date_Search2;
    private javax.swing.JButton button_export_dataBoxPengiriman1;
    private javax.swing.JButton button_invoice;
    private javax.swing.JButton button_print;
    private javax.swing.JButton button_search;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel label_total_gram_pengiriman;
    private javax.swing.JLabel label_total_keping_pengiriman;
    private javax.swing.JTable tabel_rekap_box;
    private javax.swing.JTextField txt_search_keywords;
    // End of variables declaration//GEN-END:variables
}
