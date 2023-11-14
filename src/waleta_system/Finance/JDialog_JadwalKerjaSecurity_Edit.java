package waleta_system.Finance;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import waleta_system.Class.Utility;

public class JDialog_JadwalKerjaSecurity_Edit extends javax.swing.JDialog {

    String sql = null;
    ResultSet rs;
    PreparedStatement pst;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Date today = new Date();

    public JDialog_JadwalKerjaSecurity_Edit(java.awt.Frame parent, boolean modal, String ID, String tanggal) {
        super(parent, modal);
        initComponents();
        this.setResizable(false);

        try {
            sql = "SELECT `tb_jadwal_kerja_sc`.`id_pegawai`, `tb_karyawan`.`nama_pegawai`, `jenis_hari`, `tanggal`, `jadwal_masuk`, `jadwal_pulang`, `durasi_istirahat` "
                    + "FROM `tb_jadwal_kerja_sc` LEFT JOIN `tb_karyawan` ON `tb_jadwal_kerja_sc`.`id_pegawai` = `tb_karyawan`.`id_pegawai` "
                    + "WHERE `tb_jadwal_kerja_sc`.`id_pegawai` = '" + ID + "' AND `tanggal` = '"+tanggal+"'";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                label_id.setText(rs.getString("id_pegawai"));
                label_nama.setText(rs.getString("nama_pegawai"));
                label_tanggal.setText(rs.getString("tanggal"));
                ComboBox_jenisHari.setSelectedItem(rs.getString("jenis_hari"));
//                System.out.println("jadwal masuk : " + rs.getString("jadwal_masuk").split(":")[0] + ":" + rs.getString("jadwal_masuk").split(":")[1]);
//                System.out.println("jadwal pulang : " + rs.getString("jadwal_pulang").split(":")[0] + ":" + rs.getString("jadwal_pulang").split(":")[1]);
                Spinner_jam_masuk.setValue(Integer.valueOf(rs.getString("jadwal_masuk").split(":")[0]));
                Spinner_menit_masuk.setValue(Integer.valueOf(rs.getString("jadwal_masuk").split(":")[1]));
                Spinner_jam_pulang.setValue(Integer.valueOf(rs.getString("jadwal_pulang").split(":")[0]));
                Spinner_menit_pulang.setValue(Integer.valueOf(rs.getString("jadwal_pulang").split(":")[1]));
                txt_istirahat.setText(rs.getString("durasi_istirahat"));
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JDialog_JadwalKerjaSecurity_Edit.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        label_id = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        button_save = new javax.swing.JButton();
        button_cancel = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        label_tanggal = new javax.swing.JLabel();
        Spinner_jam_masuk = new javax.swing.JSpinner();
        jLabel10 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        Spinner_menit_pulang = new javax.swing.JSpinner();
        Spinner_jam_pulang = new javax.swing.JSpinner();
        jLabel14 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        Spinner_menit_masuk = new javax.swing.JSpinner();
        label_nama = new javax.swing.JLabel();
        ComboBox_jenisHari = new javax.swing.JComboBox<>();
        txt_istirahat = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Edit No Rek & Level Gaji");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel1.setText("ID Pegawai :");

        label_id.setBackground(new java.awt.Color(255, 255, 255));
        label_id.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_id.setText("-");
        label_id.setToolTipText("");

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel3.setText("Nama :");

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel5.setText("Jenis Hari :");

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel6.setText("Istirahat (Menit) :");

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

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel4.setText("Tanggal :");

        label_tanggal.setBackground(new java.awt.Color(255, 255, 255));
        label_tanggal.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_tanggal.setText("-");
        label_tanggal.setToolTipText("");

        Spinner_jam_masuk.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Spinner_jam_masuk.setModel(new javax.swing.SpinnerNumberModel(0, 0, 23, 1));
        Spinner_jam_masuk.setEditor(new javax.swing.JSpinner.NumberEditor(Spinner_jam_masuk, ""));

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel10.setText("Jadwal Masuk :");

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel15.setText(":");

        Spinner_menit_pulang.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Spinner_menit_pulang.setModel(new javax.swing.SpinnerNumberModel(0, 0, 59, 1));

        Spinner_jam_pulang.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Spinner_jam_pulang.setModel(new javax.swing.SpinnerNumberModel(0, 0, 23, 1));
        Spinner_jam_pulang.setEditor(new javax.swing.JSpinner.NumberEditor(Spinner_jam_pulang, ""));

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel14.setText("Jadwal Pulang :");

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel11.setText(":");

        Spinner_menit_masuk.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Spinner_menit_masuk.setModel(new javax.swing.SpinnerNumberModel(0, 0, 59, 1));

        label_nama.setBackground(new java.awt.Color(255, 255, 255));
        label_nama.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_nama.setText("-");
        label_nama.setToolTipText("");

        ComboBox_jenisHari.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        ComboBox_jenisHari.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Hari Kerja", "Hari Libur" }));

        txt_istirahat.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_istirahat.setText("60");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label_id, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(label_tanggal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(label_nama, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ComboBox_jenisHari, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(Spinner_jam_masuk, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Spinner_menit_masuk, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(Spinner_jam_pulang, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Spinner_menit_pulang, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txt_istirahat, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(175, Short.MAX_VALUE)
                .addComponent(button_cancel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_save)
                .addGap(10, 10, 10))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_id, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_nama, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_tanggal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_jenisHari, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Spinner_jam_masuk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Spinner_menit_masuk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Spinner_jam_pulang, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Spinner_menit_pulang, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_istirahat, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
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
        try {
            String Query = "UPDATE `tb_jadwal_kerja_sc` SET "
                    + "`jenis_hari`='" + ComboBox_jenisHari.getSelectedItem() + "',"
                    + "`jadwal_masuk`='" + Spinner_jam_masuk.getValue() + ":" + Spinner_menit_masuk.getValue() + ":00',"
                    + "`jadwal_pulang`='" + Spinner_jam_pulang.getValue() + ":" + Spinner_menit_pulang.getValue() + ":00',"
                    + "`durasi_istirahat`='" + txt_istirahat.getText() + "' "
                    + "WHERE `id_pegawai`='" + label_id.getText() + "' "
                    + "AND `tanggal`='" + label_tanggal.getText() + "'";
            Utility.db.getConnection().createStatement();
            if ((Utility.db.getStatement().executeUpdate(Query)) == 1) {
                JOptionPane.showMessageDialog(this, "data Saved");
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "data Failed");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JDialog_JadwalKerjaSecurity_Edit.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_saveActionPerformed

    private void button_cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_cancelActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_button_cancelActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_jenisHari;
    private javax.swing.JSpinner Spinner_jam_masuk;
    private javax.swing.JSpinner Spinner_jam_pulang;
    private javax.swing.JSpinner Spinner_menit_masuk;
    private javax.swing.JSpinner Spinner_menit_pulang;
    private javax.swing.JButton button_cancel;
    private javax.swing.JButton button_save;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel label_id;
    private javax.swing.JLabel label_nama;
    private javax.swing.JLabel label_tanggal;
    private javax.swing.JTextField txt_istirahat;
    // End of variables declaration//GEN-END:variables
}
