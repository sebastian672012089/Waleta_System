package waleta_system.Packing;

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
import waleta_system.BahanJadi.JPanel_BoxBahanJadi;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;

public class JDialog_terima_box extends javax.swing.JDialog {

     
    String sql = null;
    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();

    public JDialog_terima_box(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        refreshTable();
        refreshTable_Rekap();
    }

    public void refreshTable() {
        try {
            
            int tot_kpg = 0, tot_gram = 0;
            DefaultTableModel model = (DefaultTableModel) table_box.getModel();
            model.setRowCount(0);
            sql = "SELECT `tb_box_bahan_jadi`.`no_box`, `tanggal_box`, `tb_grade_bahan_jadi`.`kode_grade`, `tb_box_bahan_jadi`.`keping`, `tb_box_bahan_jadi`.`berat`, `no_tutupan`, `status_terakhir`, `lokasi_terakhir`, `tgl_proses_terakhir`, `tb_spk_detail`.`kode_spk`, `tb_spk_detail`.`grade_buyer` FROM `tb_box_bahan_jadi` "
                    + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_box_bahan_jadi`.`kode_grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode`"
                    + "LEFT JOIN `tb_box_packing` ON `tb_box_bahan_jadi`.`no_box` = `tb_box_packing`.`no_box`"
                    + "LEFT JOIN `tb_spk_detail` ON `tb_box_packing`.`no_grade_spk` = `tb_spk_detail`.`no`"
                    + "WHERE `tb_box_packing`.`status` = 'PENDING'"
                    + "ORDER BY `tgl_proses_terakhir` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[8];
            while (rs.next()) {
                row[1] = rs.getString("no_box");
                row[2] = rs.getString("kode_grade");
                row[3] = rs.getInt("keping");
                tot_kpg = tot_kpg + rs.getInt("keping");
                row[4] = rs.getInt("berat");
                tot_gram = tot_gram + rs.getInt("berat");
                row[5] = rs.getDate("tgl_proses_terakhir");
                row[6] = rs.getString("kode_spk");
                row[7] = rs.getString("grade_buyer");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(table_box);
            label_total_box.setText(Integer.toString(table_box.getRowCount()));
            label_total_keping.setText(Integer.toString(tot_kpg));
            label_total_gram.setText(Integer.toString(tot_gram));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_BoxBahanJadi.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(JDialog_terima_box.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_Rekap() {
        try {
            DefaultTableModel model = (DefaultTableModel) table_rekap.getModel();
            model.setRowCount(0);
            sql = "SELECT `tb_spk_detail`.`kode_spk`, `grade_waleta`, `grade_buyer`, `tb_box_packing`.`no_grade_spk`, `tb_spk_detail`.`berat`, COUNT(`tb_box_bahan_jadi`.`no_box`) AS 'box_turun', SUM(`tb_box_bahan_jadi`.`keping`) AS 'kpg_turun', SUM(`tb_box_bahan_jadi`.`berat`) AS 'gram_turun' "
                    + "FROM `tb_box_bahan_jadi` "
                    + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_box_bahan_jadi`.`kode_grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode`"
                    + "LEFT JOIN `tb_box_packing` ON `tb_box_bahan_jadi`.`no_box` = `tb_box_packing`.`no_box`"
                    + "LEFT JOIN `tb_spk_detail` ON `tb_box_packing`.`no_grade_spk` = `tb_spk_detail`.`no`"
                    + "WHERE `tb_box_packing`.`status` = 'PENDING'"
                    + "GROUP BY `tb_box_packing`.`no_grade_spk` "
                    + "ORDER BY `tgl_proses_terakhir` DESC";
            System.out.println(sql);
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[8];
            while (rs.next()) {
                row[0] = rs.getString("kode_spk");
                row[1] = rs.getString("grade_waleta");
                row[2] = rs.getString("grade_buyer");
                row[3] = rs.getInt("box_turun");
                row[4] = rs.getFloat("kpg_turun");
                row[5] = rs.getFloat("gram_turun");
                String query_detail = "SELECT SUM(`tb_box_bahan_jadi`.`berat`) AS 'progress'"
                        + "FROM `tb_box_packing` "
                        + "LEFT JOIN `tb_box_bahan_jadi` ON `tb_box_bahan_jadi`.`no_box` = `tb_box_packing`.`no_box` "
                        + "WHERE `no_grade_spk` = '" + rs.getString("no_grade_spk") + "' AND `tb_box_packing`.`status` <> 'PENDING'";
//                System.out.println(query_detail);
                PreparedStatement psts = Utility.db.getConnection().prepareStatement(query_detail);
                ResultSet rst = psts.executeQuery();
                if (rst.next()) {
                    row[6] = rst.getFloat("progress");
                }
                row[7] = rs.getFloat("berat");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(table_rekap);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_BoxBahanJadi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table_box = new javax.swing.JTable();
        button_ok = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        CheckBox_selectAll = new javax.swing.JCheckBox();
        label_total_box = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        label_total_keping = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        label_total_gram = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        table_rekap = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Bagian Packing");
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel1.setText("Terima BOX Bahan Jadi dari Grading");

        table_box.setAutoCreateRowSorter(true);
        table_box.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_box.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "", "No Box", "Grade", "Keping", "Gram", "GBJ Setor", "Kode SPK", "Grade SPK"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table_box.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(table_box);
        if (table_box.getColumnModel().getColumnCount() > 0) {
            table_box.getColumnModel().getColumn(0).setMinWidth(30);
        }

        button_ok.setBackground(new java.awt.Color(255, 255, 255));
        button_ok.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_ok.setText("SAVE");
        button_ok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_okActionPerformed(evt);
            }
        });

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        jLabel2.setText("Tabel Daftar Box yang di setor dari Grading Bahan jadi");

        CheckBox_selectAll.setBackground(new java.awt.Color(255, 255, 255));
        CheckBox_selectAll.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        CheckBox_selectAll.setText("Select All");
        CheckBox_selectAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CheckBox_selectAllActionPerformed(evt);
            }
        });

        label_total_box.setBackground(new java.awt.Color(255, 255, 255));
        label_total_box.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_box.setText("0");

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel4.setText("Total Box :");

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel5.setText("Total Kpg :");

        label_total_keping.setBackground(new java.awt.Color(255, 255, 255));
        label_total_keping.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_keping.setText("0");

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel7.setText("Total Gram :");

        label_total_gram.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_gram.setText("0");

        table_rekap.setAutoCreateRowSorter(true);
        table_rekap.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_rekap.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode SPK", "Grade WLT", "Grade Buyer", "Tot Box Turun", "Kpg Turun", "Gr Turun", "Gram Progress", "Gram SPK"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
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
        table_rekap.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(table_rekap);

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel3.setText("Rekap BOX Turun dari GBJ");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 570, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(CheckBox_selectAll)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_box)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_keping)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gram)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_ok)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 590, Short.MAX_VALUE)
                    .addComponent(jLabel3))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(button_ok, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_gram)
                    .addComponent(jLabel7)
                    .addComponent(label_total_keping)
                    .addComponent(jLabel5)
                    .addComponent(jLabel4)
                    .addComponent(label_total_box)
                    .addComponent(CheckBox_selectAll)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 573, Short.MAX_VALUE)
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void CheckBox_selectAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CheckBox_selectAllActionPerformed
        // TODO add your handling code here:
        if (CheckBox_selectAll.isSelected()) {
            for (int i = 0; i < table_box.getRowCount(); i++) {
                table_box.setValueAt(true, i, 0);
            }
        } else {
            for (int i = 0; i < table_box.getRowCount(); i++) {
                table_box.setValueAt(false, i, 0);
            }
        }
    }//GEN-LAST:event_CheckBox_selectAllActionPerformed

    private void button_okActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_okActionPerformed
        // TODO add your handling code here:
        boolean isChecked;
        try {
            Utility.db.getConnection().setAutoCommit(false);
            for (int i = 0; i < table_box.getRowCount(); i++) {
                if (table_box.getValueAt(i, 0) != null) {
                    isChecked = Boolean.valueOf(table_box.getValueAt(i, 0).toString());
                } else {
                    isChecked = false;
                }
                if (isChecked) {
                    String update_box_pacing = "UPDATE `tb_box_packing` SET `status`='STOK', `tanggal_masuk` = '" + dateFormat.format(date) + "' WHERE `no_box` = '" + table_box.getValueAt(i, 1) + "'";
                    Utility.db.getConnection().createStatement();
                    Utility.db.getStatement().executeUpdate(update_box_pacing);
                }
            }
            Utility.db.getConnection().commit();
            JOptionPane.showMessageDialog(this, "LP diterima Bagian Packing");
        } catch (SQLException ex) {
            try {
                Utility.db.getConnection().rollback();
            } catch (SQLException ex1) {
                Logger.getLogger(JDialog_terima_box.class.getName()).log(Level.SEVERE, null, ex1);
            }
            Logger.getLogger(JDialog_terima_box.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            this.dispose();
            try {
                Utility.db.getConnection().setAutoCommit(true);
            } catch (SQLException ex) {
                Logger.getLogger(JDialog_terima_box.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_button_okActionPerformed

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JDialog_terima_box.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JDialog_terima_box dialog = new JDialog_terima_box(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox CheckBox_selectAll;
    private javax.swing.JButton button_ok;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel label_total_box;
    private javax.swing.JLabel label_total_gram;
    private javax.swing.JLabel label_total_keping;
    private javax.swing.JTable table_box;
    private javax.swing.JTable table_rekap;
    // End of variables declaration//GEN-END:variables
}
