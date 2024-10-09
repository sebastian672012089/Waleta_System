package waleta_system.QC;

import java.awt.HeadlessException;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import waleta_system.Class.Utility;

public class JDialog_Input_UjiPengiriman extends javax.swing.JDialog {

    String sql = null;
    String no_lp = null;
    ResultSet rs;
    Date date = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    String status;

    public JDialog_Input_UjiPengiriman(java.awt.Frame parent, boolean modal, String no_box) {
        super(parent, modal);
        initComponents();
        if (no_box == null) {
            this.status = "input";
        } else {
            this.status = "edit";
            txt_no_box.setEditable(false);
            txt_no_box.setText(no_box);
            get_data_edit();
        }
    }

    public void get_data_box() {
        try {
            if (txt_no_box.getText() != null && txt_no_box.getText().equals("")) {
                sql = "SELECT `tb_spk_detail`.`kode_spk`, `tb_spk_detail`.`grade_buyer`, `tb_grade_bahan_jadi`.`kode_grade`, `tb_box_packing`.`no_box`, `tb_box_bahan_jadi`.`berat`, `invoice_pengiriman` "
                        + "FROM `tb_box_packing` "
                        + "LEFT JOIN `tb_box_bahan_jadi` ON `tb_box_packing`.`no_box` = `tb_box_bahan_jadi`.`no_box`\n"
                        + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_box_bahan_jadi`.`kode_grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode`"
                        + "LEFT JOIN `tb_spk_detail` ON `tb_box_packing`.`no_grade_spk` = `tb_spk_detail`.`no`"
                        + "WHERE `tb_box_packing`.`no_box` = '" + txt_no_box.getText() + "' ";
                rs = Utility.db.getStatement().executeQuery(sql);
                if (rs.next()) {
                    label_gram.setText(rs.getString("berat"));
                    label_no_spk.setText(rs.getString("kode_spk"));
                    label_grade_buyer.setText(rs.getString("grade_buyer"));
                    label_grade_waleta.setText(rs.getString("kode_grade"));
                    label_no_invoice.setText(rs.getString("invoice_pengiriman"));
                } else {
                    JOptionPane.showMessageDialog(this, "Maaf data box tidak ditemukan di data packing");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(JDialog_Input_UjiPengiriman.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void get_data_edit() {
        try {
            if (txt_no_box.getText() != null && txt_no_box.getText().equals("")) {
                sql = "SELECT `tb_spk_detail`.`kode_spk`, `tb_spk_detail`.`grade_buyer`, `tb_grade_bahan_jadi`.`kode_grade`, `tb_lab_pemeriksaan_pengiriman`.`no_box`, `tb_box_bahan_jadi`.`keping`, `tb_box_bahan_jadi`.`berat`, `tb_lab_pemeriksaan_pengiriman`.`tgl_uji`, `kadar_air`, `nitrit`, `invoice_pengiriman` "
                        + "FROM `tb_lab_pemeriksaan_pengiriman` "
                        + "LEFT JOIN `tb_box_packing` ON `tb_box_packing`.`no_box` = `tb_lab_pemeriksaan_pengiriman`.`no_box`\n"
                        + "LEFT JOIN `tb_box_bahan_jadi` ON `tb_box_packing`.`no_box` = `tb_box_bahan_jadi`.`no_box`\n"
                        + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_box_bahan_jadi`.`kode_grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode`"
                        + "LEFT JOIN `tb_spk_detail` ON `tb_box_packing`.`no_grade_spk` = `tb_spk_detail`.`no`"
                        + "WHERE `tb_box_packing`.`no_box` = '" + txt_no_box.getText() + "' ";
                rs = Utility.db.getStatement().executeQuery(sql);
                if (rs.next()) {
                    label_gram.setText(rs.getString("berat"));
                    label_no_spk.setText(rs.getString("kode_spk"));
                    label_grade_buyer.setText(rs.getString("grade_buyer"));
                    label_grade_waleta.setText(rs.getString("kode_grade"));
                    label_no_invoice.setText(rs.getString("invoice_pengiriman"));
                    Date_uji.setDate(rs.getDate("tgl_uji"));
                    txt_nitrit.setText(rs.getString("nitrit"));
                    txt_kadar_air.setText(rs.getString("kadar_air"));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(JDialog_Input_UjiPengiriman.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void input() {
        try {
            String tgl_uji = dateFormat.format(Date_uji.getDate());
            float nitrit = Float.valueOf(txt_nitrit.getText());
            float kadar_air = Float.valueOf(txt_kadar_air.getText());

            sql = "INSERT INTO `tb_lab_pemeriksaan_pengiriman`(`no_box`, `tgl_uji`, `kadar_air`, `nitrit`) "
                    + "VALUES ('" + txt_no_box.getText() + "','" + tgl_uji + "'," + kadar_air + "," + nitrit + ")";
            Utility.db.getConnection().createStatement();
            if ((Utility.db.getStatement().executeUpdate(sql)) == 1) {
                JOptionPane.showMessageDialog(this, "Data Berhasil disimpan");
                this.dispose();
            }
        } catch (HeadlessException | SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(JDialog_Input_UjiPengiriman.class.getName()).log(Level.SEVERE, null, e);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Maaf data yang anda input salah, silahkan cek kembali");
            Logger.getLogger(JDialog_Input_UjiPengiriman.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void edit() {
        try {
            String tgl_uji = dateFormat.format(Date_uji.getDate());
            float nitrit = Float.valueOf(txt_nitrit.getText());
            float kadar_air = Float.valueOf(txt_kadar_air.getText());

            sql = "UPDATE `tb_lab_pemeriksaan_pengiriman` SET "
                    + "`tgl_uji`='" + tgl_uji + "',"
                    + "`kadar_air`=" + kadar_air + ","
                    + "`nitrit`=" + nitrit + " "
                    + "WHERE `no_box`='" + txt_no_box.getText() + "'";
            Utility.db.getConnection().createStatement();
            Utility.db.getStatement().executeUpdate(sql);
            JOptionPane.showMessageDialog(this, "Data Berhasil disimpan");
            this.dispose();

        } catch (HeadlessException | SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(JDialog_Input_UjiPengiriman.class.getName()).log(Level.SEVERE, null, e);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Maaf data yang anda input salah, silahkan cek kembali");
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        txt_no_box = new javax.swing.JTextField();
        label_gram = new javax.swing.JLabel();
        label_no_spk = new javax.swing.JLabel();
        label_grade_buyer = new javax.swing.JLabel();
        label_grade_waleta = new javax.swing.JLabel();
        label_no_invoice = new javax.swing.JLabel();
        Date_uji = new com.toedter.calendar.JDateChooser();
        txt_nitrit = new javax.swing.JTextField();
        txt_kadar_air = new javax.swing.JTextField();
        button_save = new javax.swing.JButton();
        button_cancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Bagian Lab");
        setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel1.setText("Input Pemeriksaan Produk Jadi");

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel9.setText("No Box :");

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel10.setText("Gram :");

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel11.setText("Grade Buyer :");

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel12.setText("Grade WLT :");

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel13.setText("No Invoice :");

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel14.setText("Tanggal Uji :");

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel15.setText("Nitrit (ppm) :");

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel16.setText("Kadar air (%) :");

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel17.setText("No SPK :");

        txt_no_box.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_no_box.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_no_boxFocusLost(evt);
            }
        });
        txt_no_box.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_no_boxKeyPressed(evt);
            }
        });

        label_gram.setBackground(new java.awt.Color(255, 255, 255));
        label_gram.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_gram.setText("-");

        label_no_spk.setBackground(new java.awt.Color(255, 255, 255));
        label_no_spk.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_no_spk.setText("-");

        label_grade_buyer.setBackground(new java.awt.Color(255, 255, 255));
        label_grade_buyer.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_grade_buyer.setText("-");

        label_grade_waleta.setBackground(new java.awt.Color(255, 255, 255));
        label_grade_waleta.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_grade_waleta.setText("-");

        label_no_invoice.setBackground(new java.awt.Color(255, 255, 255));
        label_no_invoice.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_no_invoice.setText("-");

        Date_uji.setBackground(new java.awt.Color(255, 255, 255));
        Date_uji.setDate(new Date());
        Date_uji.setDateFormatString("dd MMM yyyy");
        Date_uji.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Date_uji.setMaxSelectableDate(new Date());

        txt_nitrit.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_nitrit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_nitritKeyTyped(evt);
            }
        });

        txt_kadar_air.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_kadar_air.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_kadar_airKeyTyped(evt);
            }
        });

        button_save.setBackground(new java.awt.Color(255, 255, 255));
        button_save.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_save.setText("Save");
        button_save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_saveActionPerformed(evt);
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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel1))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(button_cancel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_save, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txt_no_box, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_no_spk, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(Date_uji, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_gram, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_grade_buyer, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_grade_waleta, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txt_nitrit, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_no_invoice, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txt_kadar_air, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txt_no_box, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_gram, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_no_spk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_grade_buyer, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_grade_waleta, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_no_invoice, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_uji, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_nitrit, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_kadar_air, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_save)
                    .addComponent(button_cancel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

    private void button_saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_saveActionPerformed
        if (status.equals("input")) {
            input();
        } else {
            edit();
        }
    }//GEN-LAST:event_button_saveActionPerformed

    private void txt_no_boxFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_no_boxFocusLost
        // TODO add your handling code here:
        get_data_box();
    }//GEN-LAST:event_txt_no_boxFocusLost

    private void txt_no_boxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_no_boxKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            get_data_box();
        }
    }//GEN-LAST:event_txt_no_boxKeyPressed

    private void button_cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_cancelActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_button_cancelActionPerformed

    private void txt_nitritKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_nitritKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_nitritKeyTyped

    private void txt_kadar_airKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_kadar_airKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_kadar_airKeyTyped

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser Date_uji;
    private javax.swing.JButton button_cancel;
    private javax.swing.JButton button_save;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel label_grade_buyer;
    private javax.swing.JLabel label_grade_waleta;
    private javax.swing.JLabel label_gram;
    private javax.swing.JLabel label_no_invoice;
    private javax.swing.JLabel label_no_spk;
    private javax.swing.JTextField txt_kadar_air;
    private javax.swing.JTextField txt_nitrit;
    private javax.swing.JTextField txt_no_box;
    // End of variables declaration//GEN-END:variables
}
