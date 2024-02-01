package waleta_system.QC;

import java.awt.HeadlessException;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import waleta_system.Class.Utility;

public class JDialog_EditSetor_boxTreatment extends javax.swing.JDialog {

    String sql = null;
    ResultSet rs;
    String operand = null;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public JDialog_EditSetor_boxTreatment(java.awt.Frame parent, boolean modal, String kode, String no_box, String tgl_selesai, String nitrit1, String nitrit2, String kpg_akhir, String gram_akhir, String operand) {
        super(parent, modal);
        initComponents();

        this.operand = operand;
        label_kode.setText(kode);
        label_no_box.setText(no_box);
        if (operand.equals("setor")) {
            label_judul.setText("SETOR BOX TREATMENT BARANG JADI");
            button_save.setText("SETOR");
            jLabel1.setVisible(false);
            txt_nitrit_awal.setVisible(false);
            jLabel3.setVisible(false);
            txt_nitrit_akhir.setVisible(false);
            txt_keping_akhir.setText(kpg_akhir);
            txt_gram_akhir.setText(gram_akhir);
        } else if (operand.equals("edit")) {
            label_judul.setText("EDIT BOX TREATMENT BARANG JADI");
            button_save.setText("EDIT");
            try {
                Date_selesai.setDate(dateFormat.parse(tgl_selesai));
            } catch (ParseException ex) {
                Logger.getLogger(JDialog_EditSetor_boxTreatment.class.getName()).log(Level.SEVERE, null, ex);
            }
            txt_nitrit_awal.setText(nitrit1);
            txt_nitrit_akhir.setText(nitrit2);
            txt_keping_akhir.setText(kpg_akhir);
            txt_gram_akhir.setText(gram_akhir);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        label_judul = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        txt_nitrit_awal = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txt_nitrit_akhir = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txt_keping_akhir = new javax.swing.JTextField();
        txt_gram_akhir = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        Date_selesai = new com.toedter.calendar.JDateChooser();
        button_save = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        label_no_box = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        label_kode = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        label_judul.setBackground(new java.awt.Color(255, 255, 255));
        label_judul.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_judul.setText("EDIT TREATMENT BARANG JADI");

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel1.setText("Nitrit Awal : ");

        txt_nitrit_awal.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_nitrit_awal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_nitrit_awalKeyTyped(evt);
            }
        });

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setText("Tanggal Selesai :");

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel3.setText("Nitrit Akhir :");

        txt_nitrit_akhir.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_nitrit_akhir.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_nitrit_akhirKeyTyped(evt);
            }
        });

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel4.setText("Keping Akhir :");

        txt_keping_akhir.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_keping_akhir.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_keping_akhirKeyTyped(evt);
            }
        });

        txt_gram_akhir.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_gram_akhir.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_gram_akhirKeyTyped(evt);
            }
        });

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel5.setText("Gram AKhir :");

        Date_selesai.setBackground(new java.awt.Color(255, 255, 255));
        Date_selesai.setDate(new Date());
        Date_selesai.setDateFormatString("dd MMMM yyyy");
        Date_selesai.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        button_save.setBackground(new java.awt.Color(255, 255, 255));
        button_save.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_save.setText("Save");
        button_save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_saveActionPerformed(evt);
            }
        });

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel6.setText("Kode :");

        label_no_box.setBackground(new java.awt.Color(255, 255, 255));
        label_no_box.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_no_box.setText("No Box");

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel7.setText("No Box :");

        label_kode.setBackground(new java.awt.Color(255, 255, 255));
        label_kode.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_kode.setText("Kode");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(button_save)
                .addGap(10, 10, 10))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_keping_akhir, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_gram_akhir, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Date_selesai, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_nitrit_awal, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_no_box, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_kode, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_nitrit_akhir, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(label_judul)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label_judul)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_kode, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_no_box, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_nitrit_awal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_selesai, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_nitrit_akhir, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_keping_akhir, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_gram_akhir, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_save)
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
        // TODO add your handling code here:
        try {
            boolean check = true;
            if (check) {
                String nitrit_awal = "";
                String lokasi = "";
                Utility.db.getConnection().setAutoCommit(false);
                if (operand.equals("edit")) {
                    sql = "UPDATE `tb_lab_barang_jadi` SET "
                            + "`tgl_selesai`='" + dateFormat.format(Date_selesai.getDate()) + "',"
                            + "`nitrit_awal`='" + txt_nitrit_awal.getText() + "',"
                            + "`nitrit_akhir`='" + txt_nitrit_akhir.getText() + "',"
                            + "`kpg_akhir`='" + txt_keping_akhir.getText() + "',"
                            + "`gram_akhir`='" + txt_gram_akhir.getText() + "'"
                            + "WHERE `kode`='" + label_kode.getText() + "'";
                    Utility.db.getConnection().createStatement();
                    if ((Utility.db.getStatement().executeUpdate(sql)) == 1) {
                        sql = "UPDATE `tb_box_bahan_jadi` SET "
                                + "`keping`='" + txt_keping_akhir.getText() + "',"
                                + "`berat`='" + txt_gram_akhir.getText() + "'"
                                + "WHERE `no_box`='" + label_no_box.getText() + "'";
                        Utility.db.getConnection().createStatement();
                        if ((Utility.db.getStatement().executeUpdate(sql)) == 1) {
                            JOptionPane.showMessageDialog(this, "Data saved !");
                            this.dispose();
                        } else {
                            JOptionPane.showMessageDialog(this, "No Data Change !");
                        }
                    }
                } else if (operand.equals("setor")) {
                    sql = "UPDATE `tb_lab_barang_jadi` SET "
                            + "`tgl_selesai`='" + dateFormat.format(Date_selesai.getDate()) + "',"
                            + "`kpg_akhir`='" + txt_keping_akhir.getText() + "',"
                            + "`gram_akhir`='" + txt_gram_akhir.getText() + "'"
                            + "WHERE `kode`='" + label_kode.getText() + "'";
                    Utility.db.getConnection().createStatement();
                    if ((Utility.db.getStatement().executeUpdate(sql)) == 1) {
                        sql = "UPDATE `tb_box_bahan_jadi` SET "
                                + "`tgl_proses_terakhir`='" + dateFormat.format(Date_selesai.getDate()) + "',"
                                + "`lokasi_terakhir`='GRADING', "
                                + "`status_terakhir`='Treatment', "
                                + "`keping`='" + txt_keping_akhir.getText() + "',"
                                + "`berat`='" + txt_gram_akhir.getText() + "'"
                                + "WHERE `no_box`='" + label_no_box.getText() + "'";
                        Utility.db.getConnection().createStatement();
                        if ((Utility.db.getStatement().executeUpdate(sql)) == 1) {
                            JOptionPane.showMessageDialog(this, "Data saved !");
                            this.dispose();
                        } else {
                            JOptionPane.showMessageDialog(this, "No Data Change !");
                        }
                    }
                }
                Utility.db.getConnection().commit();
            }
        } catch (Exception e) {
            try {
                Utility.db.getConnection().rollback();
            } catch (SQLException ex) {
                Logger.getLogger(JDialog_EditSetor_boxTreatment.class.getName()).log(Level.SEVERE, null, ex);
            }
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JDialog_EditSetor_boxTreatment.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            try {
                Utility.db.getConnection().setAutoCommit(true);
            } catch (SQLException ex) {
                Logger.getLogger(JDialog_EditSetor_boxTreatment.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_button_saveActionPerformed

    private void txt_keping_akhirKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_keping_akhirKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_keping_akhirKeyTyped

    private void txt_gram_akhirKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_gram_akhirKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_gram_akhirKeyTyped

    private void txt_nitrit_awalKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_nitrit_awalKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
            && evt.getKeyChar() != '.'
            && evt.getKeyCode() != KeyEvent.VK_ENTER
            && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
            && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_nitrit_awalKeyTyped

    private void txt_nitrit_akhirKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_nitrit_akhirKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
            && evt.getKeyChar() != '.'
            && evt.getKeyCode() != KeyEvent.VK_ENTER
            && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
            && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_nitrit_akhirKeyTyped


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser Date_selesai;
    private javax.swing.JButton button_save;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel label_judul;
    private javax.swing.JLabel label_kode;
    private javax.swing.JLabel label_no_box;
    private javax.swing.JTextField txt_gram_akhir;
    private javax.swing.JTextField txt_keping_akhir;
    private javax.swing.JTextField txt_nitrit_akhir;
    private javax.swing.JTextField txt_nitrit_awal;
    // End of variables declaration//GEN-END:variables
}
