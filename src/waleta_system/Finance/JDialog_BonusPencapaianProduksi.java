package waleta_system.Finance;

import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;

public class JDialog_BonusPencapaianProduksi extends javax.swing.JDialog {

    String sql = null;
    ResultSet rs;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    DecimalFormat decimalFormat = new DecimalFormat();

    public JDialog_BonusPencapaianProduksi(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        decimalFormat.setGroupingUsed(true);
        refreshTable();
    }

    public void refreshTable() {
        DefaultTableModel model = (DefaultTableModel) tabel_bonus_pencapaian_produksi.getModel();
        model.setRowCount(0);
        try {
            String filter_tanggal = "";
            if (Date1.getDate() != null && Date2.getDate() != null) {
                filter_tanggal = " AND `tanggal` BETWEEN '" + dateFormat.format(Date1.getDate()) + "' AND '" + dateFormat.format(Date2.getDate()) + "'";
            }
            sql = "SELECT `tb_bonus_pencapaian_produksi`.`id_pegawai`, `tb_karyawan`.`nama_pegawai`, `tanggal`, `bonus_produksi`, `tb_bonus_pencapaian_produksi`.`keterangan` \n"
                    + "FROM `tb_bonus_pencapaian_produksi` "
                    + "LEFT JOIN `tb_karyawan` ON `tb_bonus_pencapaian_produksi`.`id_pegawai` = `tb_karyawan`.`id_pegawai`\n"
                    + "WHERE `tb_bonus_pencapaian_produksi`.`id_pegawai` LIKE '%" + txt_id_pegawai.getText() + "%' "
                    + "AND `tb_karyawan`.`nama_pegawai` LIKE '%" + txt_nama.getText() + "%'"
                    + filter_tanggal;
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[5];
            double total_bonus = 0;
            while (rs.next()) {
                row[0] = rs.getDate("tanggal");
                row[1] = rs.getString("id_pegawai");
                row[2] = rs.getString("nama_pegawai");
                row[3] = rs.getFloat("bonus_produksi");
                row[4] = rs.getString("keterangan");
                total_bonus = total_bonus + rs.getFloat("bonus_produksi");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(tabel_bonus_pencapaian_produksi);
            label_total_data.setText(decimalFormat.format(tabel_bonus_pencapaian_produksi.getRowCount()));
            label_total_nominal.setText(decimalFormat.format(total_bonus));
        } catch (SQLException ex) {
            Logger.getLogger(JDialog_BonusPencapaianProduksi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txt_id_pegawai = new javax.swing.JTextField();
        txt_nama = new javax.swing.JTextField();
        Date1 = new com.toedter.calendar.JDateChooser();
        Date2 = new com.toedter.calendar.JDateChooser();
        button_refresh = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabel_bonus_pencapaian_produksi = new javax.swing.JTable();
        button_input_bonus_pencapaian = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        label_total_data = new javax.swing.JLabel();
        label_total_nominal = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        button_delete = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("DATA BONUS PENCAPAIAN PRODUKSI");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel1.setText("ID :");

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setText("Nama :");

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel3.setText("Total Data :");

        txt_id_pegawai.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_id_pegawai.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_id_pegawaiKeyPressed(evt);
            }
        });

        txt_nama.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_nama.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_namaKeyPressed(evt);
            }
        });

        Date1.setBackground(new java.awt.Color(255, 255, 255));
        Date1.setDateFormatString("dd MMM yyyy");
        Date1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        Date2.setBackground(new java.awt.Color(255, 255, 255));
        Date2.setDateFormatString("dd MMM yyyy");
        Date2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        button_refresh.setBackground(new java.awt.Color(255, 255, 255));
        button_refresh.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_refresh.setText("Refresh");
        button_refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refreshActionPerformed(evt);
            }
        });

        tabel_bonus_pencapaian_produksi.setAutoCreateRowSorter(true);
        tabel_bonus_pencapaian_produksi.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tanggal", "ID", "Nama", "Bonus", "Keterangan"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabel_bonus_pencapaian_produksi.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tabel_bonus_pencapaian_produksi);

        button_input_bonus_pencapaian.setBackground(new java.awt.Color(255, 255, 255));
        button_input_bonus_pencapaian.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_input_bonus_pencapaian.setText("Input CSV Bonus Pencapaian Produksi");
        button_input_bonus_pencapaian.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_input_bonus_pencapaianActionPerformed(evt);
            }
        });

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel4.setText("Tanggal :");

        label_total_data.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_data.setText("0");

        label_total_nominal.setBackground(new java.awt.Color(255, 255, 255));
        label_total_nominal.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_nominal.setText("0");

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel7.setText("Total Bonus :");

        button_delete.setBackground(new java.awt.Color(255, 255, 255));
        button_delete.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_delete.setText("Delete");
        button_delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_deleteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_id_pegawai, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_nama, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_refresh))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(button_input_bonus_pencapaian)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_delete)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_data)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_nominal)))
                        .addGap(0, 113, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_id_pegawai, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_nama, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_refresh, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_input_bonus_pencapaian)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_data, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_nominal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_delete))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 423, Short.MAX_VALUE)
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

    private void button_input_bonus_pencapaianActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_input_bonus_pencapaianActionPerformed
        // TODO add your handling code here:
        try {
            JOptionPane.showMessageDialog(this, "Format csv (ID Pegawai,Tanggal(yyyy-mm-dd),BONUS,Keterangan)\npemisah koma (,)");
            int n = 0;
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Select CSV file to import!");
            int result = chooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                Utility.db.getConnection();
                File file = chooser.getSelectedFile();
                String filename1 = file.getAbsolutePath();
                try (BufferedReader br = new BufferedReader(new FileReader(filename1))) {
                    String line;
                    try {
                        Utility.db.getConnection().setAutoCommit(false);
                        while ((line = br.readLine()) != null) {
                            String[] value = line.split(",");
                            String Query = "INSERT INTO `tb_bonus_pencapaian_produksi`(`id_pegawai`, `tanggal`, `bonus_produksi`, `keterangan`) "
                                    + "VALUES ('" + value[0] + "','" + value[1] + "'," + value[2] + ",'" + value[3] + "') "
                                    + "ON DUPLICATE KEY UPDATE `bonus_produksi`=" + value[2];
                            Utility.db.getConnection().prepareStatement(Query);
                            if ((Utility.db.getStatement().executeUpdate(Query)) > 0) {
                                n++;
                            }
                        }
                        Utility.db.getConnection().commit();
                    } catch (Exception ex) {
                        Utility.db.getConnection().rollback();
                        JOptionPane.showMessageDialog(this, ex);
                        Logger.getLogger(JDialog_BonusPencapaianProduksi.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {
                        Utility.db.getConnection().setAutoCommit(true);
                        JOptionPane.showMessageDialog(this, "Data Berhasil Masuk : " + n);
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JDialog_BonusPencapaianProduksi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_input_bonus_pencapaianActionPerformed

    private void button_refreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refreshActionPerformed
        // TODO add your handling code here:
        refreshTable();
    }//GEN-LAST:event_button_refreshActionPerformed

    private void button_deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_deleteActionPerformed
        // TODO add your handling code here:
        try {
            int j = tabel_bonus_pencapaian_produksi.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Please Select Data that you want to Delete !");
            } else {
                int dialogResult = JOptionPane.showConfirmDialog(this, "Are Sure You Want to Delete this Data?", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    sql = "DELETE FROM `tb_bonus_pencapaian_produksi` WHERE `tanggal` = '" + tabel_bonus_pencapaian_produksi.getValueAt(j, 0).toString() + "' "
                            + "AND `id_pegawai` = '" + tabel_bonus_pencapaian_produksi.getValueAt(j, 1).toString() + "'";
                    Utility.db.getConnection().createStatement();
                    if ((Utility.db.getStatement().executeUpdate(sql)) == 1) {
                        JOptionPane.showMessageDialog(this, "data Deleted !");
                        refreshTable();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed!");
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
        }
    }//GEN-LAST:event_button_deleteActionPerformed

    private void txt_id_pegawaiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_id_pegawaiKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_id_pegawaiKeyPressed

    private void txt_namaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_namaKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_namaKeyPressed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(JDialog_BonusPencapaianProduksi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JDialog_BonusPencapaianProduksi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JDialog_BonusPencapaianProduksi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JDialog_BonusPencapaianProduksi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JDialog_BonusPencapaianProduksi dialog = new JDialog_BonusPencapaianProduksi(new javax.swing.JFrame(), true);
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
    private com.toedter.calendar.JDateChooser Date1;
    private com.toedter.calendar.JDateChooser Date2;
    public javax.swing.JButton button_delete;
    private javax.swing.JButton button_input_bonus_pencapaian;
    private javax.swing.JButton button_refresh;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel label_total_data;
    private javax.swing.JLabel label_total_nominal;
    private javax.swing.JTable tabel_bonus_pencapaian_produksi;
    private javax.swing.JTextField txt_id_pegawai;
    private javax.swing.JTextField txt_nama;
    // End of variables declaration//GEN-END:variables
}
