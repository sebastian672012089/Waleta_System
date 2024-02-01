package waleta_system.Panel_produksi;

import waleta_system.Class.Utility;
import java.awt.HeadlessException;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class JDialog_Input_Sesekan_F2_v2 extends javax.swing.JDialog {

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Date date = new Date();
    PreparedStatement pst;
    String sql = null;
    ResultSet rs;

    public JDialog_Input_Sesekan_F2_v2(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setResizable(false);
    }

    public void input_f2() {
        int total_baris = Tabel_data.getRowCount();
        Boolean Check = true;

        if (Date_input.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Tanggal Input tidak boleh kosong");
            Check = false;
        }

        if (Check) {
            try {
                Utility.db.getConnection().setAutoCommit(false);
                for (int i = 0; i < total_baris; i++) {
                    int sesekan = Math.round(Float.valueOf(Tabel_data.getValueAt(i, 2).toString()));
                    if (Tabel_data.getValueAt(i, 3) != null && !Tabel_data.getValueAt(i, 3).equals("") && !Tabel_data.getValueAt(i, 3).equals("0")) {
                        sesekan = Math.round(Float.valueOf(Tabel_data.getValueAt(i, 3).toString()));
                    }
                    String Query = "INSERT INTO `tb_finishing_2`(`no_laporan_produksi`, `tgl_input_sesekan`, `sesekan`) "
                            + "VALUES ("
                            + "'" + Tabel_data.getValueAt(i, 1) + "',"
                            + "'" + dateFormat.format(Date_input.getDate()) + "',"
                            + "'" + sesekan + "') "
                            + "ON DUPLICATE KEY UPDATE "
                            + "`tgl_input_sesekan`='" + dateFormat.format(Date_input.getDate()) + "', `sesekan`='" + sesekan + "' ";
                    Utility.db.getConnection().createStatement();
                    Utility.db.getStatement().executeUpdate(Query);
                }
                Utility.db.getConnection().commit();
                JOptionPane.showMessageDialog(this, "Input Data Sukses");
                this.dispose();
            } catch (SQLException e) {
                try {
                    Utility.db.getConnection().rollback();
                    JOptionPane.showMessageDialog(this, "Input Data Gagal");
                } catch (SQLException ex) {
                    Logger.getLogger(JDialog_Input_Sesekan_F2_v2.class.getName()).log(Level.SEVERE, null, ex);
                }
                JOptionPane.showMessageDialog(this, e);
                Logger.getLogger(JDialog_Input_Sesekan_F2_v2.class.getName()).log(Level.SEVERE, null, e);
            } finally {
                try {
                    Utility.db.getConnection().setAutoCommit(true);
                } catch (SQLException ex) {
                    Logger.getLogger(JDialog_Input_Sesekan_F2_v2.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public boolean CheckDuplicateLP(String no_box) {
        int i = Tabel_data.getRowCount();
        for (int j = 0; j < i; j++) {
            if (no_box.equals(Tabel_data.getValueAt(j, 1).toString())) {
                return true;
            }
        }
        return false;
    }

    public void count() {
        int a = Tabel_data.getRowCount();
        float total_gram = 0;
        for (int i = 0; i < a; i++) {
            try {
                float gram = Float.valueOf(Tabel_data.getValueAt(i, 2).toString());
                total_gram = total_gram + gram;
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Maaf Format Angka salah pada baris ke-" + i);
            }
        }
        label_total_sesekan.setText(Float.toString(total_gram));
        label_jumlahLP.setText(Integer.toString(a));
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        label_total_sesekan = new javax.swing.JLabel();
        button_save = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        label_jumlahLP = new javax.swing.JLabel();
        button_cancel = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        Date_input = new com.toedter.calendar.JDateChooser();
        jLabel9 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        button_insert = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        Tabel_data = new javax.swing.JTable();
        button_count = new javax.swing.JButton();
        txt_no_lp = new javax.swing.JTextField();
        button_delete = new javax.swing.JButton();
        label_title_terima_lp = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        label_total_sesekan.setBackground(new java.awt.Color(255, 255, 255));
        label_total_sesekan.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        label_total_sesekan.setText("0");

        button_save.setBackground(new java.awt.Color(255, 255, 255));
        button_save.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_save.setText("Save");
        button_save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_saveActionPerformed(evt);
            }
        });

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("No. Laporan Produksi :");

        label_jumlahLP.setBackground(new java.awt.Color(255, 255, 255));
        label_jumlahLP.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        label_jumlahLP.setText("0");

        button_cancel.setBackground(new java.awt.Color(255, 255, 255));
        button_cancel.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_cancel.setText("Cancel");
        button_cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_cancelActionPerformed(evt);
            }
        });

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        jLabel4.setText("Total Sesekan :");

        Date_input.setBackground(new java.awt.Color(255, 255, 255));
        Date_input.setDate(new Date());
        Date_input.setDateFormatString("dd MMMM yyyy");
        Date_input.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Date_input.setMaxSelectableDate(new Date());

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel9.setText("Tanggal Input :");

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        jLabel3.setText("Jumlah LP :");

        button_insert.setBackground(new java.awt.Color(255, 255, 255));
        button_insert.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_insert.setText("Insert");
        button_insert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_insertActionPerformed(evt);
            }
        });

        Tabel_data.setAutoCreateRowSorter(true);
        Tabel_data.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Tabel_data.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No.", "No LP", "Sesekan", "Edit SSK"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Tabel_data.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(Tabel_data);
        if (Tabel_data.getColumnModel().getColumnCount() > 0) {
            Tabel_data.getColumnModel().getColumn(0).setMaxWidth(30);
        }

        button_count.setBackground(new java.awt.Color(255, 255, 255));
        button_count.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_count.setText("Count");
        button_count.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_countActionPerformed(evt);
            }
        });

        txt_no_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_no_lp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_no_lpKeyPressed(evt);
            }
        });

        button_delete.setBackground(new java.awt.Color(255, 255, 255));
        button_delete.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_delete.setText("Delete");
        button_delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_deleteActionPerformed(evt);
            }
        });

        label_title_terima_lp.setFont(new java.awt.Font("Candara", 1, 18)); // NOI18N
        label_title_terima_lp.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_title_terima_lp.setText("Input Sesekan F2");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label_title_terima_lp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(Date_input, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txt_no_lp))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_insert, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(button_delete, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_count, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 306, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_sesekan)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_jumlahLP))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(button_save)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_cancel)))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label_title_terima_lp)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_jumlahLP, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_sesekan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_no_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_insert, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_delete, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_input, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_count, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_cancel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_save, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
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

    private void button_cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_cancelActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_button_cancelActionPerformed

    private void button_saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_saveActionPerformed
        boolean isEdited = false;
        for (int i = 0; i < Tabel_data.getRowCount(); i++) {
            if (Tabel_data.getValueAt(i, 3) != null && !Tabel_data.getValueAt(i, 3).equals("") && !Tabel_data.getValueAt(i, 3).equals("0")) {
                isEdited = true;
            }
        }
        if (isEdited) {
            int dialogResult = JOptionPane.showConfirmDialog(this, "Edit jumlah sesekan memerlukan otorisasi, lanjutkan?", "Warning", 0);
            if (dialogResult == JOptionPane.YES_OPTION) {
                JDialog_otorisasi_f2 dialog = new JDialog_otorisasi_f2(new javax.swing.JFrame(), true, "");
                dialog.pack();
                dialog.setLocationRelativeTo(this);
                dialog.setVisible(true);
                dialog.setEnabled(true);
                if (dialog.akses()) {
                    count();
                    input_f2();
                }
            }
        } else {
            count();
            input_f2();
        }
    }//GEN-LAST:event_button_saveActionPerformed

    private void button_insertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_insertActionPerformed
        // TODO add your handling code here:
        try {
            boolean check_no_LP = true;
            String query = "SELECT `tb_laporan_produksi`.`ruangan`, `tb_cetak`.`no_laporan_produksi`\n"
                    + "FROM `tb_laporan_produksi`\n"
                    + "LEFT JOIN `tb_cetak` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_cetak`.`no_laporan_produksi`\n"
                    + "WHERE `tb_laporan_produksi`.`no_laporan_produksi` = '" + txt_no_lp.getText() + "'";
            ResultSet result = Utility.db.getStatement().executeQuery(query);
            check_no_LP = !result.next();

            if (check_no_LP) {
                JOptionPane.showMessageDialog(this, "No LP salah, " + txt_no_lp.getText() + " tidak ditemukan di data LP !");
            } else if (result.getString("ruangan").length() != 5 && result.getString("no_laporan_produksi") == null) {
                JOptionPane.showMessageDialog(this, "No LP (" + txt_no_lp.getText() + ") belum masuk Cetak !");
            } else if (CheckDuplicateLP(txt_no_lp.getText())) {
                JOptionPane.showMessageDialog(this, "No LP " + txt_no_lp.getText() + " sudah Masuk di Tabel");
            } else {
                String sql2 = "SELECT `no_lp_sesekan`, `gram_sesekan_lp` FROM `tb_laporan_produksi` WHERE `no_laporan_produksi` = '" + txt_no_lp.getText() + "'";
                ResultSet rs2 = Utility.db.getStatement().executeQuery(sql2);
                if (rs2.next()) {
//                    if ((rs2.getString("no_lp_sesekan") == null || rs2.getString("no_lp_sesekan").equals("")) && rs2.getInt("gram_sesekan_lp") > 0) {
//                        JOptionPane.showMessageDialog(this, "LP belum di proses di SUB");
//                    } else {
                        DefaultTableModel model = (DefaultTableModel) Tabel_data.getModel();
                        model.addRow(new Object[]{Tabel_data.getRowCount() + 1, txt_no_lp.getText().toUpperCase(), rs2.getInt("gram_sesekan_lp"), null});
                        txt_no_lp.setText("");
                        txt_no_lp.requestFocus();
                        count();
                    }
//                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(JDialog_Input_Sesekan_F2_v2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_insertActionPerformed

    private void txt_no_lpKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_no_lpKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            button_insert.doClick();
        }
    }//GEN-LAST:event_txt_no_lpKeyPressed

    private void button_deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_deleteActionPerformed
        // TODO add your handling code here:
        int i = Tabel_data.getSelectedRow();
        DefaultTableModel model = (DefaultTableModel) Tabel_data.getModel();
        if (i != -1) {
            model.removeRow(i);
        }
        count();
    }//GEN-LAST:event_button_deleteActionPerformed

    private void button_countActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_countActionPerformed
        // TODO add your handling code here:
        count();
    }//GEN-LAST:event_button_countActionPerformed

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JDialog_Choose_LPkaki.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JDialog_Input_Sesekan_F2_v2 dialog = new JDialog_Input_Sesekan_F2_v2(new javax.swing.JFrame(), true);
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
    private com.toedter.calendar.JDateChooser Date_input;
    private javax.swing.JTable Tabel_data;
    private javax.swing.JButton button_cancel;
    private javax.swing.JButton button_count;
    private javax.swing.JButton button_delete;
    private javax.swing.JButton button_insert;
    private javax.swing.JButton button_save;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel label_jumlahLP;
    private javax.swing.JLabel label_title_terima_lp;
    private javax.swing.JLabel label_total_sesekan;
    private javax.swing.JTextField txt_no_lp;
    // End of variables declaration//GEN-END:variables
}
