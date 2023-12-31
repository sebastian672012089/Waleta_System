package waleta_system.Finance;

import waleta_system.Class.Utility;

import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import waleta_system.Class.ColumnsAutoSizer;

import waleta_system.Class.ExportToExcel;

public class JPanel_Data_ARAP_Esta extends javax.swing.JPanel {

    
    String sql = null;
    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();

    public JPanel_Data_ARAP_Esta() {
        initComponents();
    }

    public void init() {
        try {
            
            
            
            ComboBox_jenis.removeAllItems();
            ComboBox_jenis.addItem("All");
            sql = "SELECT DISTINCT(`jenis_hutang`) AS 'jenis' FROM `tb_arap_esta` WHERE 1";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_jenis.addItem(rs.getString("jenis"));
            }
            refreshTable();
        } catch (Exception ex) {
            Logger.getLogger(JPanel_Data_ARAP_Esta.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable() {
        try {
            String SearchCat = null;
            long total_debit = 0, total_kredit = 0;

            switch (ComboBox_Search.getSelectedIndex()) {
                case 0:
                    SearchCat = "no_sumber";
                    break;
                case 1:
                    SearchCat = "nama_akun";
                    break;
                case 2:
                    SearchCat = "keterangan";
                    break;
                default:
                    break;
            }

            String SearchJenis = " AND `jenis_hutang` = '" + ComboBox_jenis.getSelectedItem().toString() + "'";
            if (ComboBox_jenis.getSelectedItem() == "All") {
                SearchJenis = "";
            }

            DefaultTableModel model = (DefaultTableModel) tabel_data.getModel();
            model.setRowCount(0);
            if (Date_Search1.getDate() != null && Date_Search2.getDate() != null) {
                sql = "SELECT `kode`, `tanggal`, `no_sumber`, `nama_akun`, `keterangan`, `nilai_debit`, `nilai_kredit`, `jenis_hutang` FROM `tb_arap_esta`\n"
                        + "WHERE `" + SearchCat + "` LIKE '%" + txt_search_keywords.getText() + "%' "
                        + "AND (`tanggal` BETWEEN '" + dateFormat.format(Date_Search1.getDate()) + "' AND '" + dateFormat.format(Date_Search2.getDate()) + "')"
                        + SearchJenis
                        + " ORDER BY `tanggal` DESC";
            } else {
                sql = "SELECT `kode`, `tanggal`, `no_sumber`, `nama_akun`, `keterangan`, `nilai_debit`, `nilai_kredit`, `jenis_hutang` FROM `tb_arap_esta`\n"
                        + "WHERE `" + SearchCat + "` LIKE '%" + txt_search_keywords.getText() + "%' "
                        + SearchJenis
                        + " ORDER BY `tanggal` DESC";
            }
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[8];
            while (rs.next()) {
                row[0] = rs.getInt("kode");
                row[1] = rs.getDate("tanggal");
                row[2] = rs.getString("no_sumber");
                row[3] = rs.getString("nama_akun");
                row[4] = rs.getString("keterangan");
                row[5] = rs.getLong("nilai_debit");
                row[6] = rs.getLong("nilai_kredit");
                row[7] = rs.getString("jenis_hutang");
                total_debit = total_debit + rs.getLong("nilai_debit");
                total_kredit = total_kredit + rs.getLong("nilai_kredit");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(tabel_data);
            label_total_data.setText(decimalFormat.format(tabel_data.getRowCount()));
            label_total_debit.setText(decimalFormat.format(total_debit));
            label_total_kredit.setText(decimalFormat.format(total_kredit));
            label_total_selisih.setText(decimalFormat.format(total_debit - total_kredit));
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_Data_ARAP_Esta.class.getName()).log(Level.SEVERE, null, ex);
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
        jLabel7 = new javax.swing.JLabel();
        label_total_debit = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        label_total_data = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tabel_data = new javax.swing.JTable();
        jLabel6 = new javax.swing.JLabel();
        ComboBox_jenis = new javax.swing.JComboBox<>();
        button_export_dataBoxPengiriman1 = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        label_total_kredit = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        label_total_selisih = new javax.swing.JLabel();
        button_export_dataBoxPengiriman2 = new javax.swing.JButton();
        button_export_dataBoxPengiriman3 = new javax.swing.JButton();
        button_export_dataBoxPengiriman4 = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Data AR AP Esta", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N
        jPanel1.setPreferredSize(new java.awt.Dimension(1366, 700));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("Search By :");

        ComboBox_Search.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_Search.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "No. Sumber", "Nama Akun", "Keterangan" }));

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

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel7.setText("Total Debit :");

        label_total_debit.setBackground(new java.awt.Color(255, 255, 255));
        label_total_debit.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        label_total_debit.setText("0");

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel9.setText("Total Data :");

        label_total_data.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        label_total_data.setText("0");

        tabel_data.setAutoCreateRowSorter(true);
        tabel_data.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        tabel_data.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No.", "Tanggal", "No Sumber", "Nama Akun", "Keterangan", "Nilai Debit", "Nilai Kredit", "Jenis"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.Double.class, java.lang.String.class
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
        tabel_data.setRowHeight(18);
        tabel_data.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(tabel_data);
        if (tabel_data.getColumnModel().getColumnCount() > 0) {
            tabel_data.getColumnModel().getColumn(7).setMaxWidth(200);
        }

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel6.setText("Jenis :");

        ComboBox_jenis.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_jenis.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        button_export_dataBoxPengiriman1.setBackground(new java.awt.Color(255, 255, 255));
        button_export_dataBoxPengiriman1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_dataBoxPengiriman1.setText("Export");
        button_export_dataBoxPengiriman1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_dataBoxPengiriman1ActionPerformed(evt);
            }
        });

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel8.setText("Tanggal :");

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel10.setText("Total Kredit :");

        label_total_kredit.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kredit.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        label_total_kredit.setText("0");

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel11.setText("Selisih :");

        label_total_selisih.setBackground(new java.awt.Color(255, 255, 255));
        label_total_selisih.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        label_total_selisih.setText("0");

        button_export_dataBoxPengiriman2.setBackground(new java.awt.Color(255, 255, 255));
        button_export_dataBoxPengiriman2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_dataBoxPengiriman2.setText("New");
        button_export_dataBoxPengiriman2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_dataBoxPengiriman2ActionPerformed(evt);
            }
        });

        button_export_dataBoxPengiriman3.setBackground(new java.awt.Color(255, 255, 255));
        button_export_dataBoxPengiriman3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_dataBoxPengiriman3.setText("Edit");
        button_export_dataBoxPengiriman3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_dataBoxPengiriman3ActionPerformed(evt);
            }
        });

        button_export_dataBoxPengiriman4.setBackground(new java.awt.Color(255, 255, 255));
        button_export_dataBoxPengiriman4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_dataBoxPengiriman4.setText("Delete");
        button_export_dataBoxPengiriman4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_dataBoxPengiriman4ActionPerformed(evt);
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
                                .addComponent(label_total_data))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_debit))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_kredit))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_selisih))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_Search, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_keywords, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_jenis, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_Search1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_Search2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_search)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_export_dataBoxPengiriman1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_export_dataBoxPengiriman2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_export_dataBoxPengiriman3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_export_dataBoxPengiriman4)))
                        .addGap(0, 238, Short.MAX_VALUE)))
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
                    .addComponent(ComboBox_jenis, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export_dataBoxPengiriman1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export_dataBoxPengiriman2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export_dataBoxPengiriman3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export_dataBoxPengiriman4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 531, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(label_total_data))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(label_total_debit))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(label_total_kredit))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(label_total_selisih))
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
            refreshTable();
        }
    }//GEN-LAST:event_txt_search_keywordsKeyPressed

    private void button_searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_searchActionPerformed
        // TODO add your handling code here:
        refreshTable();
    }//GEN-LAST:event_button_searchActionPerformed

    private void button_export_dataBoxPengiriman1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_dataBoxPengiriman1ActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) tabel_data.getModel();
        ExportToExcel.writeToExcel(model, jPanel1);
    }//GEN-LAST:event_button_export_dataBoxPengiriman1ActionPerformed

    private void button_export_dataBoxPengiriman2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_dataBoxPengiriman2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_button_export_dataBoxPengiriman2ActionPerformed

    private void button_export_dataBoxPengiriman3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_dataBoxPengiriman3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_button_export_dataBoxPengiriman3ActionPerformed

    private void button_export_dataBoxPengiriman4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_dataBoxPengiriman4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_button_export_dataBoxPengiriman4ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_Search;
    private javax.swing.JComboBox<String> ComboBox_jenis;
    private com.toedter.calendar.JDateChooser Date_Search1;
    private com.toedter.calendar.JDateChooser Date_Search2;
    private javax.swing.JButton button_export_dataBoxPengiriman1;
    private javax.swing.JButton button_export_dataBoxPengiriman2;
    private javax.swing.JButton button_export_dataBoxPengiriman3;
    private javax.swing.JButton button_export_dataBoxPengiriman4;
    private javax.swing.JButton button_search;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel label_total_data;
    private javax.swing.JLabel label_total_debit;
    private javax.swing.JLabel label_total_kredit;
    private javax.swing.JLabel label_total_selisih;
    private javax.swing.JTable tabel_data;
    private javax.swing.JTextField txt_search_keywords;
    // End of variables declaration//GEN-END:variables
}
