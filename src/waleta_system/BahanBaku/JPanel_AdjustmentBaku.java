package waleta_system.BahanBaku;

import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;
import waleta_system.Class.ExportToExcel;

public class JPanel_AdjustmentBaku extends javax.swing.JPanel {

    
    String sql = null;
    ResultSet rs;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();

    public JPanel_AdjustmentBaku() {
        initComponents();
    }
    
    public void init() {
        
        try {
            
            sql = "SELECT `kode_grade` FROM `tb_grade_bahan_baku` ORDER BY `kode_grade`";
            ResultSet rs1 = Utility.db.getStatement().executeQuery(sql);
            while (rs1.next()) {
                ComboBox_Search_grade.addItem(rs1.getString("kode_grade"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e);
        } catch (Exception ex) {
            Logger.getLogger(Stock_Bahan_Baku.class.getName()).log(Level.SEVERE, null, ex);
        }
        AutoCompleteDecorator.decorate(ComboBox_Search_grade);
        refreshTable_adjustment();
    }

    public void refreshTable_adjustment() {
        try {
            int keping_reproses = 0;
            float gram_reproses = 0;
            
            String kode = ComboBox_Search_grade.getSelectedItem().toString();
            if (ComboBox_Search_grade.getSelectedItem().equals("All")) {
                kode = "";
            }
            
            DefaultTableModel model = (DefaultTableModel) table_adjustment.getModel();
            model.setRowCount(0);
            if (Date_1.getDate() != null && Date_2.getDate() != null) {
                sql = "SELECT `nomor_adjustment`, `jenis_adjustment`, `no_kartu_waleta`, `grade_bahan_baku`, `keping`, `gram`, `tgl_adjustment`, `tgl_stok_opname`, `kpg_awal`, `gram_awal`, `kpg_akhir`, `gram_akhir` FROM `tb_adjustment_baku` "
                        + "WHERE `no_kartu_waleta` LIKE '%" + txt_search_noKartu.getText() + "%' AND `grade_bahan_baku` LIKE '%" + kode + "%' AND (`tgl_adjustment` BETWEEN '" + dateFormat.format(Date_1.getDate()) + "' AND '" + dateFormat.format(Date_2.getDate()) + "')";
            } else {
                sql = "SELECT `nomor_adjustment`, `jenis_adjustment`, `no_kartu_waleta`, `grade_bahan_baku`, `keping`, `gram`, `tgl_adjustment`, `tgl_stok_opname`, `kpg_awal`, `gram_awal`, `kpg_akhir`, `gram_akhir` FROM `tb_adjustment_baku` "
                        + "WHERE `no_kartu_waleta` LIKE '%" + txt_search_noKartu.getText() + "%' AND `grade_bahan_baku` LIKE '%" + kode + "%'";
            }
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[12];
            while (rs.next()) {  
                row[0] = rs.getString("nomor_adjustment");
                row[1] = rs.getString("jenis_adjustment");
                row[2] = rs.getString("no_kartu_waleta");
                row[3] = rs.getString("grade_bahan_baku");
                row[4] = rs.getInt("keping");
                row[5] = rs.getFloat("gram");
                row[6] = rs.getDate("tgl_adjustment");
                row[7] = rs.getDate("tgl_stok_opname");
                row[8] = rs.getInt("kpg_awal");
                row[9] = rs.getFloat("gram_awal");
                row[10] = rs.getInt("kpg_akhir");
                row[11] = rs.getFloat("gram_akhir");
                keping_reproses = keping_reproses + rs.getInt("keping");
                gram_reproses = gram_reproses + rs.getFloat("gram");
                model.addRow(row);
            }
            label_total_data.setText(Integer.toString(table_adjustment.getRowCount()));
//            label_total_keping_reproses.setText(Integer.toString(keping_reproses));
//            label_total_gram_reproses.setText(Float.toString(gram_reproses));
            ColumnsAutoSizer.sizeColumnsToFit(table_adjustment);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_AdjustmentBaku.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table_adjustment = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        txt_search_noKartu = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        ComboBox_Search_grade = new javax.swing.JComboBox<>();
        button_search = new javax.swing.JButton();
        button_new = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        label_total_data = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        Date_1 = new com.toedter.calendar.JDateChooser();
        Date_2 = new com.toedter.calendar.JDateChooser();
        button_export = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Data Adjustment Baku", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N

        table_adjustment.setAutoCreateRowSorter(true);
        table_adjustment.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_adjustment.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "Jenis Adjustment", "No Kartu", "Grade", "Keping", "Gram", "Tgl Adjustment", "Tgl Stok Opname", "Kpg Awal", "Gram Awal", "Kpg Akhir", "Gram Akhir"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Float.class, java.lang.Object.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Float.class, java.lang.Integer.class, java.lang.Float.class
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
        table_adjustment.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(table_adjustment);

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("No Kartu :");

        txt_search_noKartu.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_noKartu.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_noKartuKeyPressed(evt);
            }
        });

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("Grade :");

        ComboBox_Search_grade.setEditable(true);
        ComboBox_Search_grade.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_Search_grade.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        button_search.setBackground(new java.awt.Color(255, 255, 255));
        button_search.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search.setText("Search");
        button_search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_searchActionPerformed(evt);
            }
        });

        button_new.setBackground(new java.awt.Color(255, 255, 255));
        button_new.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_new.setText("New Adjustment");
        button_new.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_newActionPerformed(evt);
            }
        });

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel3.setText("Total Data :");

        label_total_data.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_data.setText("0");

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel5.setText("Tanggal Adjustment :");

        Date_1.setBackground(new java.awt.Color(255, 255, 255));
        Date_1.setDateFormatString("dd MMMM yyyy");
        Date_1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Date_2.setBackground(new java.awt.Color(255, 255, 255));
        Date_2.setDateFormatString("dd MMMM yyyy");
        Date_2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        button_export.setBackground(new java.awt.Color(255, 255, 255));
        button_export.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export.setText("Export");
        button_export.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_exportActionPerformed(evt);
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
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_data))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_noKartu, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_Search_grade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_search)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_new)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_export)))
                        .addGap(0, 426, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_new, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_Search_grade, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_noKartu, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_search, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 602, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_data, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
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

    private void txt_search_noKartuKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_noKartuKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_adjustment();
        }
    }//GEN-LAST:event_txt_search_noKartuKeyPressed

    private void button_searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_searchActionPerformed
        // TODO add your handling code here:
        refreshTable_adjustment();
    }//GEN-LAST:event_button_searchActionPerformed

    private void button_exportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_exportActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) table_adjustment.getModel();
        ExportToExcel.writeToExcel(model, this);
    }//GEN-LAST:event_button_exportActionPerformed

    private void button_newActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_newActionPerformed
        // TODO add your handling code here:
        JDialog_AdjustmentBaku dialog = new JDialog_AdjustmentBaku(new javax.swing.JFrame(), true);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);
        refreshTable_adjustment();
    }//GEN-LAST:event_button_newActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_Search_grade;
    private com.toedter.calendar.JDateChooser Date_1;
    private com.toedter.calendar.JDateChooser Date_2;
    private javax.swing.JButton button_export;
    private javax.swing.JButton button_new;
    private javax.swing.JButton button_search;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel label_total_data;
    private javax.swing.JTable table_adjustment;
    private javax.swing.JTextField txt_search_noKartu;
    // End of variables declaration//GEN-END:variables
}
