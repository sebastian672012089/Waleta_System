package waleta_system.BahanBaku;

import waleta_system.Class.Utility;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import waleta_system.MainForm;

public class JDialog_InputGramSesekanLP extends javax.swing.JDialog {

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public JDialog_InputGramSesekanLP(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setResizable(false);
        label_nama_admin.setText(MainForm.Login_NamaPegawai);
    }

    public void inputSesekanLP() {
        int total_baris = Tabel_dataInputSesekan.getRowCount();
        Boolean Check = true;

        if (Check) {
            try {
                // Start transaction
                Connection conn = Utility.db.getConnection();
                conn.setAutoCommit(false);

                // Prepare the SQL query using PreparedStatement
                String query = "UPDATE `tb_laporan_produksi` SET `gram_sesekan_lp` = ?, `pekerja_sesekan` = ? WHERE `no_laporan_produksi` = ?";

                try ( PreparedStatement pstmt = conn.prepareStatement(query)) {
                    // Loop through each row in the table and execute the query
                    for (int i = 0; i < total_baris; i++) {
                        String no_lp = Tabel_dataInputSesekan.getValueAt(i, 0).toString();
                        String id_admin = MainForm.Login_idPegawai;
                        float sesekan = Math.round(Float.valueOf(Tabel_dataInputSesekan.getValueAt(i, 1).toString()) * 10f) / 10f;

                        // Set the parameters for the query
                        pstmt.setFloat(1, sesekan);
                        pstmt.setString(2, id_admin);
                        pstmt.setString(3, no_lp);
                        pstmt.executeUpdate();
                    }

                    // Commit transaction
                    conn.commit();
                    JOptionPane.showMessageDialog(this, "Input Data Sukses");
                    this.dispose();

                } catch (SQLException e) {
                    // Rollback transaction in case of an error
                    conn.rollback();
                    JOptionPane.showMessageDialog(this, "Input Data Gagal: " + e.getMessage());
                    Logger.getLogger(JDialog_InputGramSesekanLP.class.getName()).log(Level.SEVERE, null, e);
                } finally {
                    // Always set auto-commit back to true
                    conn.setAutoCommit(true);
                }

            } catch (SQLException e) {
                Logger.getLogger(JDialog_InputGramSesekanLP.class.getName()).log(Level.SEVERE, null, e);
                JOptionPane.showMessageDialog(this, "Error setting auto-commit: " + e.getMessage());
            }

        }
    }

    public boolean CheckDuplicateLP(String no_lp) {
        int i = Tabel_dataInputSesekan.getRowCount();
        for (int j = 0; j < i; j++) {
            if (no_lp.equals(Tabel_dataInputSesekan.getValueAt(j, 0).toString())) {
                return true;
            }
        }
        return false;
    }

    public void count() {
        int rowCount = Tabel_dataInputSesekan.getRowCount();
        float total_gram = 0;
        for (int i = 0; i < rowCount; i++) {
            try {
                float gram = Float.valueOf(Tabel_dataInputSesekan.getValueAt(i, 1).toString());
                total_gram = total_gram + gram;
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Maaf Format Angka salah pada baris ke-" + i);
                break;
            }
        }
        label_total_sesekan.setText(Float.toString(total_gram));
        label_jumlahLP.setText(Integer.toString(rowCount));
    }

    private void addLP() {
        try {
            boolean check_no_LP = true;
            String query = "SELECT `no_laporan_produksi` FROM `tb_laporan_produksi` WHERE `no_laporan_produksi` = '" + txt_no_lp.getText() + "'";
            ResultSet result = Utility.db.getStatement().executeQuery(query);
            check_no_LP = !result.next();

            if (check_no_LP) {
                JOptionPane.showMessageDialog(this, "No LP salah, " + txt_no_lp.getText() + " tidak ditemukan di data LP !");
            } else if (CheckDuplicateLP(txt_no_lp.getText())) {
                JOptionPane.showMessageDialog(this, "No LP " + txt_no_lp.getText() + " sudah Masuk di Tabel");
            } else if (txt_gram.getText() == null || txt_gram.getText().equals("") || txt_gram.getText().equals("0")) {
                JOptionPane.showMessageDialog(this, "Gram tidak boleh 0 / Kosong!");
            } else {
                DefaultTableModel model = (DefaultTableModel) Tabel_dataInputSesekan.getModel();
                System.out.println("-" + txt_gram.getText() + "-");
                float gram = Float.valueOf(txt_gram.getText());
                model.addRow(new Object[]{txt_no_lp.getText().toUpperCase(), gram});
                txt_no_lp.setText("");
                txt_gram.setText("");
                txt_no_lp.requestFocus();
                count();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JDialog_InputGramSesekanLP.class.getName()).log(Level.SEVERE, null, ex);
        }
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
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        Tabel_dataInputSesekan = new javax.swing.JTable();
        txt_no_lp = new javax.swing.JTextField();
        button_delete = new javax.swing.JButton();
        label_title_terima_lp = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        label_nama_admin = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txt_gram = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        label_total_sesekan.setBackground(new java.awt.Color(255, 255, 255));
        label_total_sesekan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_sesekan.setText("0");
        label_total_sesekan.setFocusable(false);

        button_save.setBackground(new java.awt.Color(255, 255, 255));
        button_save.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_save.setText("Save");
        button_save.setFocusable(false);
        button_save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_saveActionPerformed(evt);
            }
        });

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setText("No. Laporan Produksi :");
        jLabel2.setFocusable(false);

        label_jumlahLP.setBackground(new java.awt.Color(255, 255, 255));
        label_jumlahLP.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_jumlahLP.setText("0");
        label_jumlahLP.setFocusable(false);

        button_cancel.setBackground(new java.awt.Color(255, 255, 255));
        button_cancel.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_cancel.setText("Cancel");
        button_cancel.setFocusable(false);
        button_cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_cancelActionPerformed(evt);
            }
        });

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel4.setText("Total Sesekan :");
        jLabel4.setFocusable(false);

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel3.setText("Jumlah LP :");
        jLabel3.setFocusable(false);

        jScrollPane1.setFocusable(false);

        Tabel_dataInputSesekan.setAutoCreateRowSorter(true);
        Tabel_dataInputSesekan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Tabel_dataInputSesekan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No LP", "Gram Sesekan"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Tabel_dataInputSesekan.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(Tabel_dataInputSesekan);

        txt_no_lp.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_no_lp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_no_lpKeyPressed(evt);
            }
        });

        button_delete.setBackground(new java.awt.Color(255, 255, 255));
        button_delete.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_delete.setText("Delete");
        button_delete.setFocusable(false);
        button_delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_deleteActionPerformed(evt);
            }
        });

        label_title_terima_lp.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        label_title_terima_lp.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_title_terima_lp.setText("Input Sesekan F2");
        label_title_terima_lp.setFocusable(false);

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel10.setText("Admin :");
        jLabel10.setFocusable(false);

        label_nama_admin.setBackground(new java.awt.Color(255, 255, 255));
        label_nama_admin.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_nama_admin.setText("-");
        label_nama_admin.setFocusable(false);

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel7.setText("Gram :");
        jLabel7.setFocusable(false);

        txt_gram.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_gram.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_gramKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_gramKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
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
                                .addComponent(button_cancel))))
                    .addComponent(label_title_terima_lp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_no_lp)
                            .addComponent(label_nama_admin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txt_gram))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(button_delete, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1))
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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_no_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_delete, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_gram, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_nama_admin, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
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
        count();
        inputSesekanLP();
    }//GEN-LAST:event_button_saveActionPerformed

    private void txt_no_lpKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_no_lpKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            txt_gram.requestFocus();
        }
    }//GEN-LAST:event_txt_no_lpKeyPressed

    private void button_deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_deleteActionPerformed
        // TODO add your handling code here:
        int i = Tabel_dataInputSesekan.getSelectedRow();
        DefaultTableModel model = (DefaultTableModel) Tabel_dataInputSesekan.getModel();
        if (i != -1) {
            model.removeRow(i);
        }
        count();
    }//GEN-LAST:event_button_deleteActionPerformed

    private void txt_gramKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_gramKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            addLP();
        }
    }//GEN-LAST:event_txt_gramKeyPressed

    private void txt_gramKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_gramKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_gramKeyTyped

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JDialog_InputGramSesekanLP.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JDialog_InputGramSesekanLP dialog = new JDialog_InputGramSesekanLP(new javax.swing.JFrame(), true);
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
    private javax.swing.JTable Tabel_dataInputSesekan;
    private javax.swing.JButton button_cancel;
    private javax.swing.JButton button_delete;
    private javax.swing.JButton button_save;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel label_jumlahLP;
    private javax.swing.JLabel label_nama_admin;
    private javax.swing.JLabel label_title_terima_lp;
    private javax.swing.JLabel label_total_sesekan;
    private javax.swing.JTextField txt_gram;
    private javax.swing.JTextField txt_no_lp;
    // End of variables declaration//GEN-END:variables
}
