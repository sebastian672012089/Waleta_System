package waleta_system.BahanJadi;

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
import waleta_system.Class.Utility;

public class JDialog_terima_retur extends javax.swing.JDialog {

     
    String sql = null;
    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();

    public JDialog_terima_retur(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        refreshTable();
    }

    public void refreshTable() {
        try {
            
            int tot_kpg = 0, tot_gram = 0;
            DefaultTableModel model = (DefaultTableModel) table_box.getModel();
            model.setRowCount(0);
            sql = "SELECT `no_box`, `tb_grade_bahan_jadi`.`kode_grade`, `keping`, `berat` "
                    + "FROM `tb_box_bahan_jadi` LEFT JOIN `tb_grade_bahan_jadi` ON `tb_box_bahan_jadi`.`kode_grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode`"
                    + "WHERE `status_terakhir` = 'Retur dari Packing' AND `lokasi_terakhir` = 'PACKING'";
            rs = Utility.db.getStatement().executeQuery(sql);
            System.out.println(sql);
            Object[] row = new Object[4];
            while (rs.next()) {
                row[0] = rs.getString("no_box");
                row[1] = rs.getString("kode_grade");
                row[2] = rs.getInt("keping");
                tot_kpg = tot_kpg + rs.getInt("keping");
                row[3] = rs.getInt("berat");
                tot_gram = tot_gram + rs.getInt("berat");
                model.addRow(row);
            }
            label_total_box.setText(Integer.toString(table_box.getRowCount()));
            label_total_keping.setText(Integer.toString(tot_kpg));
            label_total_gram.setText(Integer.toString(tot_gram));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_BoxBahanJadi.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(JDialog_terima_retur.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table_box = new javax.swing.JTable();
        button_terima = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        CheckBox_selectAll = new javax.swing.JCheckBox();
        label_total_box = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        label_total_keping = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        label_total_gram = new javax.swing.JLabel();
        button_tidak_terima = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Bagian Packing");
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel1.setText("Terima BOX Bahan Jadi dari Packing");

        table_box.setAutoCreateRowSorter(true);
        table_box.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_box.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No Box", "Grade", "Keping", "Gram", ""
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(table_box);
        if (table_box.getColumnModel().getColumnCount() > 0) {
            table_box.getColumnModel().getColumn(4).setResizable(false);
            table_box.getColumnModel().getColumn(4).setPreferredWidth(30);
        }

        button_terima.setBackground(new java.awt.Color(255, 255, 255));
        button_terima.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        button_terima.setText("Terima BOX");
        button_terima.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_terimaActionPerformed(evt);
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

        button_tidak_terima.setBackground(new java.awt.Color(255, 255, 255));
        button_tidak_terima.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        button_tidak_terima.setForeground(new java.awt.Color(255, 0, 0));
        button_tidak_terima.setText("Tidak Terima BOX");
        button_tidak_terima.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_tidak_terimaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(CheckBox_selectAll))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
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
                        .addComponent(button_tidak_terima)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_terima)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(CheckBox_selectAll))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 574, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(label_total_box, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_keping, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_gram, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_terima, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_tidak_terima, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                table_box.setValueAt(true, i, 4);
            }
        } else {
            for (int i = 0; i < table_box.getRowCount(); i++) {
                table_box.setValueAt(false, i, 4);
            }
        }
    }//GEN-LAST:event_CheckBox_selectAllActionPerformed

    private void button_terimaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_terimaActionPerformed
        // TODO add your handling code here:
        boolean isChecked;
        int check = 0;
        try {
            Utility.db.getConnection().setAutoCommit(false);
            for (int i = 0; i < table_box.getRowCount(); i++) {
                if (table_box.getValueAt(i, 4) != null) {
                    isChecked = Boolean.valueOf(table_box.getValueAt(i, 4).toString());
                } else {
                    isChecked = false;
                }
                if (isChecked) {
                    check++;
                    String update = "UPDATE `tb_box_bahan_jadi` SET `lokasi_terakhir`='GRADING' WHERE `no_box` = '" + table_box.getValueAt(i, 0) + "'";
                    Utility.db.getConnection().createStatement();
                    Utility.db.getStatement().executeUpdate(update);

                    String delete = "DELETE FROM `tb_box_packing` WHERE `no_box` = '" + table_box.getValueAt(i, 0) + "'";
                    Utility.db.getConnection().createStatement();
                    Utility.db.getStatement().executeUpdate(delete);
                }
            }
            Utility.db.getConnection().commit();
        } catch (SQLException ex) {
            try {
                Utility.db.getConnection().rollback();
            } catch (SQLException ex1) {
                Logger.getLogger(JDialog_terima_retur.class.getName()).log(Level.SEVERE, null, ex1);
            }
            Logger.getLogger(JDialog_terima_retur.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (check > 0) {
                JOptionPane.showMessageDialog(this, check + " BOX retur diterima Bagian Grading");
            }
            this.dispose();
            try {
                Utility.db.getConnection().setAutoCommit(true);
            } catch (SQLException ex) {
                Logger.getLogger(JDialog_terima_retur.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_button_terimaActionPerformed

    private void button_tidak_terimaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_tidak_terimaActionPerformed
        // TODO add your handling code here:
        boolean isChecked;
        int check = 0;
        try {
            int dialogResult = JOptionPane.showConfirmDialog(this, "konfirmasi tidak menerima BOX?", "Warning", 0);
            if (dialogResult == JOptionPane.YES_OPTION) {
                Utility.db.getConnection().setAutoCommit(false);
                for (int i = 0; i < table_box.getRowCount(); i++) {
                    if (table_box.getValueAt(i, 4) != null) {
                        isChecked = Boolean.valueOf(table_box.getValueAt(i, 4).toString());
                    } else {
                        isChecked = false;
                    }
                    if (isChecked) {
                        check++;
                        String update = "UPDATE `tb_box_bahan_jadi` SET `status_terakhir`='Batal Retur' WHERE `no_box` = '" + table_box.getValueAt(i, 0) + "'";
                        Utility.db.getConnection().createStatement();
                        Utility.db.getStatement().executeUpdate(update);

                        String delete = "UPDATE `tb_box_packing` SET `status`='STOK' WHERE `no_box` = '" + table_box.getValueAt(i, 0) + "'";
                        Utility.db.getConnection().createStatement();
                        Utility.db.getStatement().executeUpdate(delete);
                    }
                }
                Utility.db.getConnection().commit();
            }
        } catch (SQLException ex) {
            try {
                Utility.db.getConnection().rollback();
            } catch (SQLException ex1) {
                Logger.getLogger(JDialog_terima_retur.class.getName()).log(Level.SEVERE, null, ex1);
            }
            Logger.getLogger(JDialog_terima_retur.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (check > 0) {
                JOptionPane.showMessageDialog(this, check + " BOX dikembalikan ke Bagian Packing");
            }
            this.dispose();
            try {
                Utility.db.getConnection().setAutoCommit(true);
            } catch (SQLException ex) {
                Logger.getLogger(JDialog_terima_retur.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_button_tidak_terimaActionPerformed

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JDialog_terima_retur.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JDialog_terima_retur dialog = new JDialog_terima_retur(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton button_terima;
    private javax.swing.JButton button_tidak_terima;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel label_total_box;
    private javax.swing.JLabel label_total_gram;
    private javax.swing.JLabel label_total_keping;
    private javax.swing.JTable table_box;
    // End of variables declaration//GEN-END:variables
}
