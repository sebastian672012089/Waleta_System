package waleta_system.Finance;

import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import waleta_system.Browse_Karyawan;
import waleta_system.Class.Utility;
import waleta_system.MainForm;

public class JDialog_InsertEdit_Pengajuan_Kenaikan_LevelGaji extends javax.swing.JDialog {

    String sql = null;
    ResultSet rs;
    String jenis_dialog;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    String nomor = null;

    public JDialog_InsertEdit_Pengajuan_Kenaikan_LevelGaji(java.awt.Frame parent, boolean modal, String nomor, String ID_pegawai, String Level_baru, String catatan, String tanggal_pengajuan) {
        super(parent, modal);
        initComponents();
        this.setResizable(false);

        if (nomor == null) {
            jenis_dialog = "NEW";
            button_pilih_pegawai.setEnabled(true);
            label_admin.setText(MainForm.Login_NamaPegawai);
        } else {
            jenis_dialog = "EDIT";
            this.nomor = nomor;
            pilih_karyawan(ID_pegawai);
            button_pilih_pegawai.setEnabled(false);
            txt_catatan.setText(catatan);
            ComboBox_LevelGaji.setSelectedItem(Level_baru);
            try {
                Date_pengajuan.setDate(dateFormat.parse(tanggal_pengajuan));
            } catch (ParseException ex) {
                Logger.getLogger(JDialog_InsertEdit_Pengajuan_Kenaikan_LevelGaji.class.getName()).log(Level.SEVERE, null, ex);
            }
            jLabel8.setVisible(false);
            label_admin.setVisible(false);
        }
    }

    public void pilih_karyawan(String ID) {
        try {
            sql = "SELECT `id_pegawai`, `nama_pegawai`, `nama_bagian`, `level_gaji` "
                    + "FROM `tb_karyawan` "
                    + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian` "
                    + "WHERE `id_pegawai` = '" + ID + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                label_id.setText(rs.getString("id_pegawai"));
                label_nama.setText(rs.getString("nama_pegawai"));
                label_bagian.setText(rs.getString("nama_bagian"));
                label_level_gaji_lama.setText(rs.getString("level_gaji"));
            }

            String level_gaji_bagian = "HARIAN";
            if (label_bagian.getText().toUpperCase().contains("-CABUT-BORONG") || label_bagian.getText().toUpperCase().contains("-CABUT-TRAINING")) {
                level_gaji_bagian = "CABUT";
            }

            ComboBox_LevelGaji.removeAllItems();
            sql = "SELECT `level_gaji` FROM `tb_level_gaji` WHERE `bagian` = '" + level_gaji_bagian + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_LevelGaji.addItem(rs.getString("level_gaji"));
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JDialog_Edit_NoRek_LevelGaji.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private boolean ada_pengajuan_belum_selesai(String ID) {
        try {
            sql = "SELECT `no`, `tb_karyawan`.`nama_pegawai`\n"
                    + "FROM `tb_level_gaji_pengajuan_kenaikan` \n"
                    + "LEFT JOIN `tb_karyawan` ON `tb_level_gaji_pengajuan_kenaikan`.`id_pegawai` = `tb_karyawan`.`id_pegawai`\n"
                    + "WHERE `tb_level_gaji_pengajuan_kenaikan`.`id_pegawai` = '" + ID + "' "
                    + "AND `dibatalkan` IS NULL "
                    + "AND (`diketahui_manager` IS NULL OR `disetujui_direktur` IS NULL OR `diketahui_keuangan` IS NULL)";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Pengajuan kenaikan atas nama (" + rs.getString("nama_pegawai") + ") belum disetujui atau dibatalkan !");
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JDialog_Edit_NoRek_LevelGaji.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        button_save = new javax.swing.JButton();
        ComboBox_LevelGaji = new javax.swing.JComboBox<>();
        button_cancel = new javax.swing.JButton();
        label_id = new javax.swing.JLabel();
        label_nama = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        label_bagian = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        button_pilih_pegawai = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        label_level_gaji_lama = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txt_catatan = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        label_admin = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        Date_pengajuan = new com.toedter.calendar.JDateChooser();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Pengajuan Kenaikan Level Gaji");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel6.setText("Level Gaji baru :");

        button_save.setBackground(new java.awt.Color(255, 255, 255));
        button_save.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_save.setText("Save");
        button_save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_saveActionPerformed(evt);
            }
        });

        ComboBox_LevelGaji.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        button_cancel.setBackground(new java.awt.Color(255, 255, 255));
        button_cancel.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_cancel.setText("Cancel");
        button_cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_cancelActionPerformed(evt);
            }
        });

        label_id.setBackground(new java.awt.Color(255, 255, 255));
        label_id.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_id.setText("-");
        label_id.setToolTipText("");

        label_nama.setBackground(new java.awt.Color(255, 255, 255));
        label_nama.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_nama.setText("-");
        label_nama.setToolTipText("");

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setText("ID Pegawai :");

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel3.setText("Nama :");

        label_bagian.setBackground(new java.awt.Color(255, 255, 255));
        label_bagian.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_bagian.setText("-");
        label_bagian.setToolTipText("");

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel1.setText("Bagian :");

        button_pilih_pegawai.setBackground(new java.awt.Color(255, 255, 255));
        button_pilih_pegawai.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_pilih_pegawai.setText("Pilih Pegawai");
        button_pilih_pegawai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_pilih_pegawaiActionPerformed(evt);
            }
        });

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel4.setText("Level Gaji Lama :");

        label_level_gaji_lama.setBackground(new java.awt.Color(255, 255, 255));
        label_level_gaji_lama.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_level_gaji_lama.setText("-");
        label_level_gaji_lama.setToolTipText("");

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel7.setText("Catatan :");

        txt_catatan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel5.setText("Pengajuan Kenaikan Gaji");

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel8.setText("Admin :");

        label_admin.setBackground(new java.awt.Color(255, 255, 255));
        label_admin.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_admin.setText("-");
        label_admin.setToolTipText("");

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel9.setText("Tanggal pengajuan :");

        Date_pengajuan.setBackground(new java.awt.Color(255, 255, 255));
        Date_pengajuan.setDateFormatString("dd MMM yyyy");
        Date_pengajuan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Date_pengajuan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(label_level_gaji_lama, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ComboBox_LevelGaji, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txt_catatan)
                    .addComponent(label_id, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(label_nama, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(label_admin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(label_bagian, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(button_pilih_pegawai))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(354, Short.MAX_VALUE)
                .addComponent(button_cancel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_save)
                .addGap(10, 10, 10))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel5)
                .addContainerGap(316, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_id, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_nama, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_bagian, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_level_gaji_lama, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_pilih_pegawai, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_LevelGaji, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_catatan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(Date_pengajuan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_admin, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(button_save)
                            .addComponent(button_cancel))
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))))
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
        if (label_id.getText() == null || label_id.getText().equals("") || label_id.getText().equals("-")) {
            JOptionPane.showMessageDialog(this, "Silahkan pilih karyawan terlebih dulu!");
        } else if (label_level_gaji_lama.getText().equals(ComboBox_LevelGaji.getSelectedItem().toString())) {
            JOptionPane.showMessageDialog(this, "Level gaji lama dan level gaji baru harus berbeda");
        } else {
            try {
                String Query;
                if (jenis_dialog.equals("NEW")) {
                    Query = "INSERT INTO `tb_level_gaji_pengajuan_kenaikan`(`tanggal_pengajuan`, `catatan`, `id_pegawai`, `level_gaji_lama`, `level_gaji_baru`, `admin`) "
                            + "VALUES (CURRENT_DATE,'" + txt_catatan.getText() + "','" + label_id.getText() + "','" + label_level_gaji_lama.getText() + "','" + ComboBox_LevelGaji.getSelectedItem().toString() + "','" + label_admin.getText() + "')";
                } else {
                    Query = "UPDATE `tb_level_gaji_pengajuan_kenaikan` SET "
                            + "`catatan` = '" + txt_catatan.getText() + "', "
                            + "`level_gaji_lama` = '" + label_level_gaji_lama.getText() + "', "
                            + "`level_gaji_baru` = '" + ComboBox_LevelGaji.getSelectedItem().toString() + "', "
                            + "`tanggal_pengajuan` = '" + dateFormat.format(Date_pengajuan.getDate()) + "' "
                            + "WHERE `no` = '" + nomor + "'";
                }
                Utility.db.getConnection().createStatement();
                if ((Utility.db.getStatement().executeUpdate(Query)) == 1) {
                    JOptionPane.showMessageDialog(this, "data Saved");
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "data Failed");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex);
                Logger.getLogger(JDialog_InsertEdit_Pengajuan_Kenaikan_LevelGaji.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_button_saveActionPerformed

    private void button_cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_cancelActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_button_cancelActionPerformed

    private void button_pilih_pegawaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_pilih_pegawaiActionPerformed
        // TODO add your handling code here:
        Browse_Karyawan dialog = new Browse_Karyawan(new javax.swing.JFrame(), true, null);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);

        int x = Browse_Karyawan.table_list_karyawan.getSelectedRow();
        if (x != -1) {
            String id_terpilih = Browse_Karyawan.table_list_karyawan.getValueAt(x, 0).toString();
            String nama_terpilih = Browse_Karyawan.table_list_karyawan.getValueAt(x, 1).toString();
            if (!ada_pengajuan_belum_selesai(id_terpilih)) {
                label_id.setText(id_terpilih);
                label_nama.setText(nama_terpilih);
                pilih_karyawan(label_id.getText());
            }
        }
    }//GEN-LAST:event_button_pilih_pegawaiActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_LevelGaji;
    private com.toedter.calendar.JDateChooser Date_pengajuan;
    private javax.swing.JButton button_cancel;
    private javax.swing.JButton button_pilih_pegawai;
    private javax.swing.JButton button_save;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel label_admin;
    private javax.swing.JLabel label_bagian;
    private javax.swing.JLabel label_id;
    private javax.swing.JLabel label_level_gaji_lama;
    private javax.swing.JLabel label_nama;
    private javax.swing.JTextField txt_catatan;
    // End of variables declaration//GEN-END:variables
}
