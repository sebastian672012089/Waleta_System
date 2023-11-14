package waleta_system.BahanJadi;

import java.awt.event.KeyEvent;
import java.sql.PreparedStatement;
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
import waleta_system.Class.Utility;

public class JPanel_PriceList_GradeBJ extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();

    public JPanel_PriceList_GradeBJ() {
        initComponents();
    }

    public void init() {
        refreshTable();
        refreshTable_history();
    }

    public void refreshTable() {
        try {
            DefaultTableModel modelGNS = (DefaultTableModel) Table_PriceList.getModel();
            modelGNS.setRowCount(0);
            String filter_gns = "";
            if (ComboBox_filterGNS.getSelectedIndex() != 0) {
                filter_gns = " AND `kode_grade` LIKE '" + ComboBox_filterGNS.getSelectedItem().toString() + "%'";
            }
            sql = "SELECT `kode`, `kode_grade`, `bentuk_grade`, `Kategori1`, `kategori_jual`, `harga`, `status_grade` "
                    + "FROM `tb_grade_bahan_jadi` "
                    + "WHERE "
                    + "(`kode` LIKE '%" + txt_search_keywords.getText() + "%' OR "
                    + "`kode_grade` LIKE '%" + txt_search_keywords.getText() + "%' OR "
                    + "`nama_grade` LIKE '%" + txt_search_keywords.getText() + "%')"
                    + " AND `status_grade` = 'AKTIF'"
                    + filter_gns;
            PreparedStatement pst = Utility.db.getConnection().prepareStatement(sql);
            rs = pst.executeQuery();
            Object[] row = new Object[10];
            while (rs.next()) {
                row[0] = rs.getString("kode");
                row[1] = rs.getString("kode_grade");
                row[2] = rs.getString("bentuk_grade");
                row[3] = rs.getString("kategori1");
                row[4] = rs.getString("status_grade");
                row[5] = rs.getInt("harga");
                String grade = rs.getString("kode");
                String query = "SELECT \n"
                        + "(SELECT `cny_kg` FROM `tb_grade_bahan_jadi_harga` WHERE `tanggal` <= CURRENT_DATE AND `grade` = '" + grade + "' ORDER BY `tanggal` DESC LIMIT 1) AS 'bulan_1', \n"
                        + "(SELECT `cny_kg` FROM `tb_grade_bahan_jadi_harga` WHERE `tanggal` <= CONCAT(LEFT(NOW() - INTERVAL 1 MONTH,7),'-31') AND `grade` = '" + grade + "' ORDER BY `tanggal` DESC LIMIT 1) AS 'bulan_2', \n"
                        + "(SELECT `cny_kg` FROM `tb_grade_bahan_jadi_harga` WHERE `tanggal` <= CONCAT(LEFT(NOW() - INTERVAL 2 MONTH,7),'-31') AND `grade` = '" + grade + "' ORDER BY `tanggal` DESC LIMIT 1) AS 'bulan_3', \n"
                        + "(SELECT `cny_kg` FROM `tb_grade_bahan_jadi_harga` WHERE `tanggal` <= CONCAT(LEFT(NOW() - INTERVAL 3 MONTH,7),'-31') AND `grade` = '" + grade + "' ORDER BY `tanggal` DESC LIMIT 1) AS 'bulan_4', \n"
                        + "(SELECT `cny_kg` FROM `tb_grade_bahan_jadi_harga` WHERE `tanggal` <= CONCAT(LEFT(NOW() - INTERVAL 4 MONTH,7),'-31') AND `grade` = '" + grade + "' ORDER BY `tanggal` DESC LIMIT 1) AS 'bulan_5', \n"
                        + "(SELECT `cny_kg` FROM `tb_grade_bahan_jadi_harga` WHERE `tanggal` <= CONCAT(LEFT(NOW() - INTERVAL 5 MONTH,7),'-31') AND `grade` = '" + grade + "' ORDER BY `tanggal` DESC LIMIT 1) AS 'bulan_6' \n"
                        + "FROM DUAL";
                ResultSet result = Utility.db.getStatement().executeQuery(query);
                if (result.next()) {
                    row[6] = result.getInt("bulan_1");
                }
                modelGNS.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_PriceList);
            label_total_GNS.setText(Integer.toString(Table_PriceList.getRowCount()));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_BoxBahanJadi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_history() {
        try {
            DefaultTableModel model = (DefaultTableModel) Table_History.getModel();
            model.setRowCount(0);
            String filter_gns = "";
            if (ComboBox_filterGNS.getSelectedIndex() != 0) {
                filter_gns = " AND `kode_grade` LIKE '" + ComboBox_filterGNS.getSelectedItem().toString() + "%'";
            }
            sql = "SELECT `kode`, `kode_grade`, `harga` FROM `tb_grade_bahan_jadi` "
                    + "WHERE "
                    + "(`kode` LIKE '%" + txt_search_keywords.getText() + "%' OR "
                    + "`kode_grade` LIKE '%" + txt_search_keywords.getText() + "%' OR "
                    + "`nama_grade` LIKE '%" + txt_search_keywords.getText() + "%')"
                    + " AND `status_grade` = 'AKTIF'"
                    + filter_gns;
            PreparedStatement pst = Utility.db.getConnection().prepareStatement(sql);
            rs = pst.executeQuery();
            Object[] row = new Object[8];
            while (rs.next()) {
                row[0] = rs.getString("kode");
                row[1] = rs.getString("kode_grade");
                String grade = rs.getString("kode");
                String query = "SELECT \n"
                        + "(SELECT `cny_kg` FROM `tb_grade_bahan_jadi_harga` WHERE `tanggal` <= CURRENT_DATE AND `grade` = '" + grade + "' ORDER BY `tanggal` DESC LIMIT 1) AS 'bulan_1', \n"
                        + "(SELECT `cny_kg` FROM `tb_grade_bahan_jadi_harga` WHERE `tanggal` <= CONCAT(LEFT(NOW() - INTERVAL 1 MONTH,7),'-31') AND `grade` = '" + grade + "' ORDER BY `tanggal` DESC LIMIT 1) AS 'bulan_2', \n"
                        + "(SELECT `cny_kg` FROM `tb_grade_bahan_jadi_harga` WHERE `tanggal` <= CONCAT(LEFT(NOW() - INTERVAL 2 MONTH,7),'-31') AND `grade` = '" + grade + "' ORDER BY `tanggal` DESC LIMIT 1) AS 'bulan_3', \n"
                        + "(SELECT `cny_kg` FROM `tb_grade_bahan_jadi_harga` WHERE `tanggal` <= CONCAT(LEFT(NOW() - INTERVAL 3 MONTH,7),'-31') AND `grade` = '" + grade + "' ORDER BY `tanggal` DESC LIMIT 1) AS 'bulan_4', \n"
                        + "(SELECT `cny_kg` FROM `tb_grade_bahan_jadi_harga` WHERE `tanggal` <= CONCAT(LEFT(NOW() - INTERVAL 4 MONTH,7),'-31') AND `grade` = '" + grade + "' ORDER BY `tanggal` DESC LIMIT 1) AS 'bulan_5', \n"
                        + "(SELECT `cny_kg` FROM `tb_grade_bahan_jadi_harga` WHERE `tanggal` <= CONCAT(LEFT(NOW() - INTERVAL 5 MONTH,7),'-31') AND `grade` = '" + grade + "' ORDER BY `tanggal` DESC LIMIT 1) AS 'bulan_6' \n"
                        + "FROM DUAL";
                ResultSet result = Utility.db.getStatement().executeQuery(query);
                if (result.next()) {
                    row[2] = result.getInt("bulan_1");
                    row[3] = result.getInt("bulan_2");
                    row[4] = result.getInt("bulan_3");
                    row[5] = result.getInt("bulan_4");
                    row[6] = result.getInt("bulan_5");
                    row[7] = result.getInt("bulan_6");
                }
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_History);
            label_total_GNS.setText(Integer.toString(Table_History.getRowCount()));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_BoxBahanJadi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel_grade_bahan_baku = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        label_total_GNS = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        button_export_GradeBahanBaku = new javax.swing.JButton();
        txt_search_keywords = new javax.swing.JTextField();
        button_search = new javax.swing.JButton();
        ComboBox_filterGNS = new javax.swing.JComboBox<>();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        Table_PriceList = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        Table_History = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));

        jPanel_grade_bahan_baku.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_grade_bahan_baku.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Price List Grade Barang Jadi", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N
        jPanel_grade_bahan_baku.setMaximumSize(new java.awt.Dimension(1306, 736));
        jPanel_grade_bahan_baku.setPreferredSize(new java.awt.Dimension(1366, 700));

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jLabel6.setText("Total Grade AKTIF :");

        label_total_GNS.setBackground(new java.awt.Color(255, 255, 255));
        label_total_GNS.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        label_total_GNS.setText("TOTAL");

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("Keywords :");

        button_export_GradeBahanBaku.setBackground(new java.awt.Color(255, 255, 255));
        button_export_GradeBahanBaku.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_GradeBahanBaku.setText("Export To Excel");
        button_export_GradeBahanBaku.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_GradeBahanBakuActionPerformed(evt);
            }
        });

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

        ComboBox_filterGNS.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_filterGNS.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "GNS", "NON NS" }));

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N

        Table_PriceList.setAutoCreateRowSorter(true);
        Table_PriceList.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Table_PriceList.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode", "Kode Grade", "Bentuk Grade", "Kategori", "Status", "RMB / Kg", "Cek"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Table_PriceList.getTableHeader().setReorderingAllowed(false);
        Table_PriceList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Table_PriceListMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(Table_PriceList);

        jTabbedPane1.addTab("TERBARU", jScrollPane1);

        Table_History.setAutoCreateRowSorter(true);
        Table_History.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        Table_History.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode", "Kode Grade", "1", "2", "3", "4", "5", "6"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
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
        Table_History.setRowHeight(20);
        Table_History.getTableHeader().setReorderingAllowed(false);
        Table_History.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Table_HistoryMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(Table_History);

        jTabbedPane1.addTab("HISTORY", jScrollPane2);

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("Note : Price list dalam satuan RMB / Kg");

        javax.swing.GroupLayout jPanel_grade_bahan_bakuLayout = new javax.swing.GroupLayout(jPanel_grade_bahan_baku);
        jPanel_grade_bahan_baku.setLayout(jPanel_grade_bahan_bakuLayout);
        jPanel_grade_bahan_bakuLayout.setHorizontalGroup(
            jPanel_grade_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_grade_bahan_bakuLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_grade_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_grade_bahan_bakuLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_keywords, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_filterGNS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_search)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_GNS)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 705, Short.MAX_VALUE)
                        .addComponent(button_export_GradeBahanBaku))
                    .addComponent(jTabbedPane1)
                    .addGroup(jPanel_grade_bahan_bakuLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel_grade_bahan_bakuLayout.setVerticalGroup(
            jPanel_grade_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_grade_bahan_bakuLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel_grade_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(button_export_GradeBahanBaku, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_search, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_keywords, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_filterGNS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel_grade_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_total_GNS)
                        .addComponent(jLabel6)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 611, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jPanel_grade_bahan_baku, javax.swing.GroupLayout.DEFAULT_SIZE, 1364, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel_grade_bahan_baku, javax.swing.GroupLayout.DEFAULT_SIZE, 706, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void button_export_GradeBahanBakuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_GradeBahanBakuActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Table_PriceList.getModel();
        ExportToExcel.writeToExcel(model, this);
    }//GEN-LAST:event_button_export_GradeBahanBakuActionPerformed

    private void txt_search_keywordsKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_keywordsKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
            refreshTable_history();
        }
    }//GEN-LAST:event_txt_search_keywordsKeyPressed

    private void button_searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_searchActionPerformed
        // TODO add your handling code here:
        refreshTable();
        refreshTable_history();
    }//GEN-LAST:event_button_searchActionPerformed

    private void Table_PriceListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Table_PriceListMouseClicked
        // TODO add your handling code here:
        decimalFormat.setMaximumFractionDigits(5);
        decimalFormat.setGroupingUsed(false);
        int i = Table_PriceList.getSelectedRow();
        if (evt.getClickCount() == 2) {
            try {
                String kode = Table_PriceList.getValueAt(i, 0).toString();
                String old_price = Table_PriceList.getValueAt(i, 5).toString();
                String price1 = "";
                price1 = old_price.replace(",", "");
                String harga = JOptionPane.showInputDialog("Masukkan Harga : ", price1);
                if (harga != null && !harga.equals("")) {
                    double HARGA_F = Double.valueOf(harga);
                    decimalFormat = Utility.DecimalFormatUS(decimalFormat);
                    sql = "UPDATE `tb_grade_bahan_jadi` SET `harga`='" + decimalFormat.format(HARGA_F) + "' "
                            + "WHERE `kode`='" + kode + "'";
                    if ((Utility.db.getStatement().executeUpdate(sql)) == 1) {
                        String insert_history = "INSERT INTO `tb_grade_bahan_jadi_harga`(`tanggal`, `grade`, `cny_kg`) VALUES (CURRENT_DATE,'"+kode+"','" + decimalFormat.format(HARGA_F) + "')";
                        if ((Utility.db.getStatement().executeUpdate(insert_history)) == 1) {
                            refreshTable();
                            JOptionPane.showMessageDialog(this, "Update success!");
                        } else {
                            JOptionPane.showMessageDialog(this, "Update failed!");
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Update failed!");
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error Connection!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Input must be number!");
            }
        }
    }//GEN-LAST:event_Table_PriceListMouseClicked

    private void Table_HistoryMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Table_HistoryMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_Table_HistoryMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_filterGNS;
    private javax.swing.JTable Table_History;
    private javax.swing.JTable Table_PriceList;
    private javax.swing.JButton button_export_GradeBahanBaku;
    private javax.swing.JButton button_search;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel_grade_bahan_baku;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel label_total_GNS;
    private javax.swing.JTextField txt_search_keywords;
    // End of variables declaration//GEN-END:variables
}
