package waleta_system.Finance;

import java.awt.event.KeyEvent;
import waleta_system.Class.Utility;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import waleta_system.Browse_Karyawan;

public class JDialog_InsertEdit_PiutangKaryawan extends javax.swing.JDialog {

    String sql = null;
    ResultSet rs;
    PreparedStatement pst;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Date today = new Date();
    String status = "";

    public JDialog_InsertEdit_PiutangKaryawan(java.awt.Frame parent, boolean modal, String kode) {
        super(parent, modal);
        initComponents();
        this.setResizable(false);

        try {
            if (kode == null) {
                this.status = "NEW";
                label_title.setText("Input Piutang Karyawan");
            } else {
                this.status = "edit";
                label_title.setText("Edit Piutang Karyawan");
                sql = "SELECT `no_piutang`, `tb_piutang_karyawan`.`id_pegawai`, `tb_karyawan`.`nama_pegawai`, `tanggal_piutang`, `tanggal_input`, `nominal_piutang`, `tb_piutang_karyawan`.`keterangan`, `tb_piutang_karyawan`.`status`, `tgl_lunas` "
                        + "FROM `tb_piutang_karyawan` LEFT JOIN `tb_karyawan` ON `tb_piutang_karyawan`.`id_pegawai` = `tb_karyawan`.`id_pegawai`"
                        + "WHERE `no_piutang` = '" + kode + "'";
                rs = Utility.db.getStatement().executeQuery(sql);
                if (rs.next()) {
                    label_no_piutang.setText(rs.getString("no_piutang"));
                    label_id.setText(rs.getString("id_pegawai"));
                    label_nama.setText(rs.getString("nama_pegawai"));
                    Date_piutang.setDate(rs.getDate("tanggal_piutang"));
                    txt_piutang.setText(rs.getString("nominal_piutang"));
                    txt_keterangan.setText(rs.getString("keterangan"));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(JDialog_InsertEdit_PiutangKaryawan.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }

    public void insert() {
        try {
            // TODO add your handling code here:
            int piutang = Integer.valueOf(txt_piutang.getText());
            String Query = "INSERT INTO `tb_piutang_karyawan`(`id_pegawai`, `tanggal_piutang`, `tanggal_input`, `nominal_piutang`, `keterangan`) "
                    + "VALUES ('" + label_id.getText() + "','" + dateFormat.format(Date_piutang.getDate()) + "',CURRENT_DATE(),'" + piutang + "','" + txt_keterangan.getText() + "')";
            Utility.db.getConnection().createStatement();
            if ((Utility.db.getStatement().executeUpdate(Query)) == 1) {
                JOptionPane.showMessageDialog(this, "data Saved");
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "data Failed");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JDialog_InsertEdit_PiutangKaryawan.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Maaf format angka salah");
            Logger.getLogger(JDialog_InsertEdit_PiutangKaryawan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void edit() {
        try {
            // TODO add your handling code here:
            int piutang = Integer.valueOf(txt_piutang.getText());
            String Query = "UPDATE `tb_piutang_karyawan` SET "
                    + "`id_pegawai`='" + label_id.getText() + "',"
                    + "`tanggal_piutang`='" + dateFormat.format(Date_piutang.getDate()) + "',"
                    + "`tanggal_input`=CURRENT_DATE(),"
                    + "`nominal_piutang`='" + piutang + "',"
                    + "`keterangan`='" + txt_keterangan.getText() + "' "
                    + "WHERE `no_piutang`='" + label_no_piutang.getText() + "'";
            Utility.db.getConnection().createStatement();
            if ((Utility.db.getStatement().executeUpdate(Query)) == 1) {
                JOptionPane.showMessageDialog(this, "data Saved");
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "data Failed");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JDialog_InsertEdit_PiutangKaryawan.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Maaf format angka salah");
            Logger.getLogger(JDialog_InsertEdit_PiutangKaryawan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        label_no_piutang = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        label_nama = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txt_piutang = new javax.swing.JTextField();
        button_save = new javax.swing.JButton();
        button_cancel = new javax.swing.JButton();
        label_title = new javax.swing.JLabel();
        button_pilih_pegawai = new javax.swing.JButton();
        Date_piutang = new com.toedter.calendar.JDateChooser();
        txt_keterangan = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        label_id = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Input Piutang");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel1.setText("No piutang :");

        label_no_piutang.setBackground(new java.awt.Color(255, 255, 255));
        label_no_piutang.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_no_piutang.setText("-");
        label_no_piutang.setToolTipText("");

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel3.setText("Nama :");

        label_nama.setBackground(new java.awt.Color(255, 255, 255));
        label_nama.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_nama.setText("-");
        label_nama.setToolTipText("");

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel5.setText("Piutang :");

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel6.setText("Tgl Piutang :");

        txt_piutang.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_piutang.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_piutangKeyTyped(evt);
            }
        });

        button_save.setBackground(new java.awt.Color(255, 255, 255));
        button_save.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_save.setText("Save");
        button_save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_saveActionPerformed(evt);
            }
        });

        button_cancel.setBackground(new java.awt.Color(255, 255, 255));
        button_cancel.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_cancel.setText("Cancel");
        button_cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_cancelActionPerformed(evt);
            }
        });

        label_title.setBackground(new java.awt.Color(255, 255, 255));
        label_title.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_title.setText("Input Piutang Karyawan");

        button_pilih_pegawai.setBackground(new java.awt.Color(255, 255, 255));
        button_pilih_pegawai.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_pilih_pegawai.setText("Pilih Pegawai");
        button_pilih_pegawai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_pilih_pegawaiActionPerformed(evt);
            }
        });

        Date_piutang.setDate(new Date());
        Date_piutang.setDateFormatString("dd MMM yyyy");

        txt_keterangan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_keterangan.setText("-");

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel7.setText("Keterangan :");

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setText("ID Pegawai :");

        label_id.setBackground(new java.awt.Color(255, 255, 255));
        label_id.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_id.setText("-");
        label_id.setToolTipText("");

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 51, 51));
        jLabel9.setText("*) angka positif untuk menambah gaji");

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 51, 51));
        jLabel10.setText("*) angka negatif untuk mengurangi gaji");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label_id, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txt_piutang)
                    .addComponent(label_nama, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txt_keterangan)
                    .addComponent(Date_piutang, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(label_no_piutang, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(button_cancel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_save)
                .addGap(10, 10, 10))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(label_title, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(10, 10, 10))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(button_pilih_pegawai)
                .addGap(200, 200, 200))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label_title, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_pilih_pegawai, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_no_piutang, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_id, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_nama, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_piutang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_piutang, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_keterangan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_save)
                    .addComponent(button_cancel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel10)
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
        if (label_id.getText().equals("-")) {
            JOptionPane.showMessageDialog(this, "Anda belum memilih karyawan");
        } else {
            if (status.equals("NEW")) {
                insert();
            } else if (status.equals("edit")) {
                edit();
            }
        }
    }//GEN-LAST:event_button_saveActionPerformed

    private void button_cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_cancelActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_button_cancelActionPerformed

    private void button_pilih_pegawaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_pilih_pegawaiActionPerformed
        // TODO add your handling code here:
        String filter_tgl = null;
        Browse_Karyawan dialog = new Browse_Karyawan(new javax.swing.JFrame(), true, filter_tgl);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);

        int x = Browse_Karyawan.table_list_karyawan.getSelectedRow();
        if (x != -1) {
            label_id.setText(Browse_Karyawan.table_list_karyawan.getValueAt(x, 0).toString());
            label_nama.setText(Browse_Karyawan.table_list_karyawan.getValueAt(x, 1).toString());
        }
    }//GEN-LAST:event_button_pilih_pegawaiActionPerformed

    private void txt_piutangKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_piutangKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '-'
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_piutangKeyTyped

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(JDialog_InsertEdit_PiutangKaryawan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JDialog_InsertEdit_PiutangKaryawan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JDialog_InsertEdit_PiutangKaryawan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JDialog_InsertEdit_PiutangKaryawan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JDialog_InsertEdit_PiutangKaryawan dialog = new JDialog_InsertEdit_PiutangKaryawan(new javax.swing.JFrame(), true, "1");
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
    private com.toedter.calendar.JDateChooser Date_piutang;
    private javax.swing.JButton button_cancel;
    private javax.swing.JButton button_pilih_pegawai;
    private javax.swing.JButton button_save;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel label_id;
    private javax.swing.JLabel label_nama;
    private javax.swing.JLabel label_no_piutang;
    private javax.swing.JLabel label_title;
    private javax.swing.JTextField txt_keterangan;
    private javax.swing.JTextField txt_piutang;
    // End of variables declaration//GEN-END:variables
}
