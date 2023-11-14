package waleta_system.BahanJadi;

import waleta_system.Panel_produksi.*;
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
import waleta_system.Class.Utility;

public class JDialog_Input_Kaki_reproses extends javax.swing.JDialog {

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    Date date = new Date();
    PreparedStatement pst;
    String sql = null;
    ResultSet rs;
    float total_gram = 0;

    public JDialog_Input_Kaki_reproses(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        try {

            initComponents();
//            label_title_terima_lp.setText("Input By Product F2");
            this.setResizable(false);
        } catch (Exception ex) {
            Logger.getLogger(JDialog_Input_Kaki_reproses.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void input_kaki() {
        int total_baris = Tabel_data.getRowCount();
        Boolean Check = true;
        count();
        float stok = Float.valueOf(label_stok.getText());
        if (total_gram > stok) {
            JOptionPane.showMessageDialog(this, "Maaf, Gram diambil melebihi stok tersisa");
            Check = false;
        }

        if (Tabel_data.getValueAt(0, 1) == null) {
            JOptionPane.showMessageDialog(this, "No LP Suwir tidak boleh kosong");
            Check = false;
        }

        if (Check) {
            try {
                Utility.db.getConnection().setAutoCommit(false);
                for (int i = 0; i < total_baris; i++) {
                    Connection con = Utility.db.getConnection();
                    String Query = "";
                    String a = "SELECT `gram_kaki`, `no_lp_suwir`, `gram_kaki2`, `no_lp_suwir2` "
                            + "FROM `tb_reproses` WHERE `no_box` = '" + Tabel_data.getValueAt(i, 0) + "'";
                    pst = con.prepareStatement(a);
                    rs = pst.executeQuery();
                    if (rs.next()) {
                        if (rs.getInt("gram_kaki") > 0) {
                            if (rs.getString("no_lp_suwir").equals(txt_lp_kaki.getText())) {
                                JOptionPane.showMessageDialog(this, "No Box " + Tabel_data.getValueAt(i, 0).toString() + " sudah menggunakan LP kaki " + txt_lp_kaki.getText() + ", data akan di tambahkan ke LP kaki 1");
                                int kaki = rs.getInt("gram_kaki") + Integer.valueOf(Tabel_data.getValueAt(i, 1).toString());
                                Query = "UPDATE `tb_reproses` SET `gram_kaki`='" + kaki + "'"
                                        + "WHERE `no_box`='" + Tabel_data.getValueAt(i, 0) + "' AND `tgl_selesai` IS NULL";
                            } else {
                                Query = "UPDATE `tb_reproses` SET "
                                        + "`gram_kaki2`='" + Tabel_data.getValueAt(i, 1) + "',"
                                        + "`no_lp_suwir2`='" + txt_lp_kaki.getText() + "'"
                                        + "WHERE `no_box`='" + Tabel_data.getValueAt(i, 0) + "' AND `tgl_selesai` IS NULL";
                            }
                        } else {
                            Query = "UPDATE `tb_reproses` SET "
                                    + "`gram_kaki`='" + Tabel_data.getValueAt(i, 1) + "',"
                                    + "`no_lp_suwir`='" + txt_lp_kaki.getText() + "'"
                                    + "WHERE `no_box`='" + Tabel_data.getValueAt(i, 0) + "' AND `tgl_selesai` IS NULL";
                        }
                    }
                    Utility.db.getConnection().createStatement();
                    Utility.db.getStatement().executeUpdate(Query);
                }
                Utility.db.getConnection().commit();
                JOptionPane.showMessageDialog(this, "Input Data Sukses");
                this.dispose();
                JPanel_Finishing2.button_search_f2.doClick();
            } catch (SQLException e) {
                try {
                    Utility.db.getConnection().rollback();
                    JOptionPane.showMessageDialog(this, "Input Data Gagal");
                } catch (SQLException ex) {
                    Logger.getLogger(JDialog_Input_Kaki_reproses.class.getName()).log(Level.SEVERE, null, ex);
                }
                JOptionPane.showMessageDialog(this, e);
                Logger.getLogger(JDialog_Input_Kaki_reproses.class.getName()).log(Level.SEVERE, null, e);
            } finally {
                try {
                    Utility.db.getConnection().setAutoCommit(true);
                } catch (SQLException ex) {
                    Logger.getLogger(JDialog_Input_Kaki_reproses.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public boolean CheckDuplicateLP(String no_box) {
        int i = Tabel_data.getRowCount();
        for (int j = 0; j < i; j++) {
            if (no_box.equals(Tabel_data.getValueAt(j, 0).toString())) {
                return true;
            }
        }
        return false;
    }

    public void count() {
        int a = Tabel_data.getRowCount();
        label_jumlahLP.setText(Integer.toString(a));
        total_gram = 0;
        for (int i = 0; i < Tabel_data.getRowCount(); i++) {
            total_gram = total_gram + Float.valueOf(Tabel_data.getValueAt(i, 1).toString());
        }
        label_total_gram.setText(Float.toString(total_gram));
    }

    public void insert() {
        try {
            boolean CHECK_F2 = true, checkKaki = true, checkSetor = true;
            String sql1 = "SELECT `gram_kaki`, `gram_kaki2`, `tgl_selesai`  FROM `tb_reproses` WHERE `no_box` = '" + txt_no_box.getText() + "'";
            ResultSet rs1 = Utility.db.getStatement().executeQuery(sql1);
            if (rs1.next()) {
                CHECK_F2 = false;
                checkKaki = (rs1.getInt("gram_kaki") > 0 && rs1.getInt("gram_kaki2") > 0);
                checkSetor = rs1.getDate("tgl_selesai") != null;
            } else {
                CHECK_F2 = true;
            }

            checkSetor = false;
            if (CHECK_F2) {
                JOptionPane.showMessageDialog(this, "No Box (" + txt_no_box.getText() + ") tidak ada dalam data reproses !");
            } else if (checkKaki) {
                JOptionPane.showMessageDialog(this, "No Box " + txt_no_box.getText() + " sudah menggunakan 2 LP Kaki");
            } else if (checkSetor) {
                JOptionPane.showMessageDialog(this, "No Box " + txt_no_box.getText() + " sudah di setorkan");
            } else if (CheckDuplicateLP(txt_no_box.getText())) {
                JOptionPane.showMessageDialog(this, "No Box " + txt_no_box.getText() + " sudah Masuk dalam Tabel");
            } else {
                DefaultTableModel model = (DefaultTableModel) Tabel_data.getModel();
                model.addRow(new Object[]{txt_no_box.getText(), null, null});
//                    ColumnsAutoSizer.sizeColumnsToFit(table_reproses);
                txt_no_box.setText("");
                txt_no_box.requestFocus();
            }
        } catch (SQLException ex) {
            Logger.getLogger(JDialog_Input_Kaki_reproses.class.getName()).log(Level.SEVERE, null, ex);
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
        txt_lp_kaki = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        Tabel_data = new javax.swing.JTable();
        label_title_terima_lp = new javax.swing.JLabel();
        button_save = new javax.swing.JButton();
        jLabel29 = new javax.swing.JLabel();
        button_delete = new javax.swing.JButton();
        label_stok = new javax.swing.JLabel();
        label_total_gram = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        button_insert = new javax.swing.JButton();
        button_cancel = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        label_jumlahLP = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        button_count = new javax.swing.JButton();
        txt_no_box = new javax.swing.JTextField();
        button_LPkaki1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        txt_lp_kaki.setEditable(false);
        txt_lp_kaki.setBackground(new java.awt.Color(255, 255, 255));
        txt_lp_kaki.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_lp_kaki.setText("-");
        txt_lp_kaki.setFocusable(false);

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        jLabel3.setText("Jumlah LP :");

        jLabel31.setBackground(new java.awt.Color(255, 255, 255));
        jLabel31.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel31.setText("Gram");
        jLabel31.setFocusable(false);

        Tabel_data.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Tabel_data.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No Box", "Gram"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class
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
        Tabel_data.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(Tabel_data);

        label_title_terima_lp.setFont(new java.awt.Font("Candara", 1, 18)); // NOI18N
        label_title_terima_lp.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_title_terima_lp.setText("Input Kaki Reproses");

        button_save.setBackground(new java.awt.Color(255, 255, 255));
        button_save.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_save.setText("Save");
        button_save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_saveActionPerformed(evt);
            }
        });

        jLabel29.setBackground(new java.awt.Color(255, 255, 255));
        jLabel29.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel29.setText("Sisa Stok :");
        jLabel29.setFocusable(false);

        button_delete.setBackground(new java.awt.Color(255, 255, 255));
        button_delete.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_delete.setText("Delete");
        button_delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_deleteActionPerformed(evt);
            }
        });

        label_stok.setBackground(new java.awt.Color(255, 255, 255));
        label_stok.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_stok.setText("0");
        label_stok.setFocusable(false);

        label_total_gram.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        label_total_gram.setText("0");

        jLabel28.setBackground(new java.awt.Color(255, 255, 255));
        jLabel28.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel28.setText("No LP Kaki :");
        jLabel28.setFocusable(false);

        button_insert.setBackground(new java.awt.Color(255, 255, 255));
        button_insert.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_insert.setText("Insert");
        button_insert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_insertActionPerformed(evt);
            }
        });

        button_cancel.setBackground(new java.awt.Color(255, 255, 255));
        button_cancel.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_cancel.setText("Cancel");
        button_cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_cancelActionPerformed(evt);
            }
        });

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("No. Box :");

        label_jumlahLP.setBackground(new java.awt.Color(255, 255, 255));
        label_jumlahLP.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        label_jumlahLP.setText("0");

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        jLabel4.setText("Total Gram :");

        button_count.setBackground(new java.awt.Color(255, 255, 255));
        button_count.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_count.setText("Count");
        button_count.setFocusable(false);
        button_count.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_countActionPerformed(evt);
            }
        });

        txt_no_box.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_no_box.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_no_boxKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_no_boxKeyTyped(evt);
            }
        });

        button_LPkaki1.setBackground(new java.awt.Color(255, 255, 255));
        button_LPkaki1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_LPkaki1.setText("LP kaki");
        button_LPkaki1.setFocusable(false);
        button_LPkaki1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_LPkaki1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label_title_terima_lp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 458, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(4, 4, 4)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(label_stok, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel31)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_save)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_cancel))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txt_no_box, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_insert, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_delete, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txt_lp_kaki, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_LPkaki1))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_jumlahLP)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gram)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_count)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label_title_terima_lp)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_gram, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_count, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_jumlahLP, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_no_box, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_insert, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_delete, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_lp_kaki, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_LPkaki1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_cancel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_save, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_stok, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
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
        input_kaki();
    }//GEN-LAST:event_button_saveActionPerformed

    private void txt_no_boxKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_no_boxKeyTyped
        // TODO add your handling code here:
        char e = evt.getKeyChar();
        if (Character.isAlphabetic(e)) {
            String c = String.valueOf(e);
            evt.consume();
            txt_no_box.setText(txt_no_box.getText() + c.toUpperCase());
        }
    }//GEN-LAST:event_txt_no_boxKeyTyped

    private void button_insertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_insertActionPerformed
        // TODO add your handling code here:
        insert();
    }//GEN-LAST:event_button_insertActionPerformed

    private void txt_no_boxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_no_boxKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            insert();
        }
    }//GEN-LAST:event_txt_no_boxKeyPressed

    private void button_deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_deleteActionPerformed
        // TODO add your handling code here:
        int i = Tabel_data.getSelectedRow();
        DefaultTableModel model = (DefaultTableModel) Tabel_data.getModel();
        if (i != -1) {
            model.removeRow(i);
        }
        count();
    }//GEN-LAST:event_button_deleteActionPerformed

    private void button_LPkaki1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_LPkaki1ActionPerformed
        // TODO add your handling code here:
        JDialog_Choose_LPkaki dialog = new JDialog_Choose_LPkaki(new javax.swing.JFrame(), true);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);
        txt_lp_kaki.setText(dialog.get_lpKaki());
        label_stok.setText(Float.toString(dialog.get_stok()));
    }//GEN-LAST:event_button_LPkaki1ActionPerformed

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
                JDialog_Input_Kaki_reproses dialog = new JDialog_Input_Kaki_reproses(new javax.swing.JFrame(), true);
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
    private javax.swing.JTable Tabel_data;
    private javax.swing.JButton button_LPkaki1;
    private javax.swing.JButton button_cancel;
    private javax.swing.JButton button_count;
    private javax.swing.JButton button_delete;
    private javax.swing.JButton button_insert;
    private javax.swing.JButton button_save;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel label_jumlahLP;
    private javax.swing.JLabel label_stok;
    private javax.swing.JLabel label_title_terima_lp;
    private javax.swing.JLabel label_total_gram;
    private javax.swing.JTextField txt_lp_kaki;
    private javax.swing.JTextField txt_no_box;
    // End of variables declaration//GEN-END:variables
}
