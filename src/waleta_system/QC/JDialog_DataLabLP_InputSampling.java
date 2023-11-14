package waleta_system.QC;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import waleta_system.Browse_Karyawan;
import waleta_system.Class.Utility;

public class JDialog_DataLabLP_InputSampling extends javax.swing.JDialog {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public JDialog_DataLabLP_InputSampling(java.awt.Frame parent, boolean modal, String no_lp) {
        super(parent, modal);
        initComponents();
        label_lp.setText(no_lp);
        try {
            sql = "SELECT `no_laporan_produksi`, `tgl_sampling`, `pekerja_sampling`, `tb_karyawan`.`nama_pegawai`\n"
                    + "FROM `tb_lab_laporan_produksi` \n"
                    + "LEFT JOIN `tb_karyawan` ON `tb_lab_laporan_produksi`.`pekerja_sampling` = `tb_karyawan`.`id_pegawai`\n"
                    + "WHERE`no_laporan_produksi` = '" + no_lp + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                if (rs.getDate("tgl_sampling") != null) {
                    Date_Sampling.setDate(rs.getDate("tgl_sampling"));
                } else {
                    Date_Sampling.setDate(new Date());
                }
                txt_pekerja_sampling_id.setText(rs.getString("pekerja_sampling"));
                txt_pekerja_sampling_nama.setText(rs.getString("nama_pegawai"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(JDialog_DataLabLP_InputSampling.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void edit() {
        try {
            boolean check = true;
            if (Date_Sampling.getDate() == null) {
                JOptionPane.showMessageDialog(this, "Tanggal sampling salah!");
                check = false;
            } else if (txt_pekerja_sampling_id.getText() == null || txt_pekerja_sampling_id.getText().equals("")) {
                JOptionPane.showMessageDialog(this, "pekerja sampling harus dipilih");
                check = false;
            }

            if (check) {
                String Query = null;
                Query = "UPDATE `tb_lab_laporan_produksi` SET "
                        + "`tgl_sampling`='" + dateFormat.format(Date_Sampling.getDate()) + "',"
                        + "`pekerja_sampling`='" + txt_pekerja_sampling_id.getText() + "' "
                        + "WHERE `no_laporan_produksi`='" + label_lp.getText() + "'";

                Utility.db.getConnection().createStatement();
                Utility.db.getStatement().executeUpdate(Query);
                JOptionPane.showMessageDialog(this, "Data berhasil disimpan!");
                this.dispose();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JDialog_DataLabLP_InputSampling.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        label_lp = new javax.swing.JLabel();
        Date_Sampling = new com.toedter.calendar.JDateChooser();
        jLabel9 = new javax.swing.JLabel();
        button_save = new javax.swing.JButton();
        label_tgl_uji = new javax.swing.JLabel();
        label_tanggal_masuk = new javax.swing.JLabel();
        label_judul = new javax.swing.JLabel();
        button_pick_timbang = new javax.swing.JButton();
        txt_pekerja_sampling_id = new javax.swing.JTextField();
        txt_pekerja_sampling_nama = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Bagian Lab");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        label_lp.setBackground(new java.awt.Color(255, 255, 255));
        label_lp.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_lp.setText("-");

        Date_Sampling.setBackground(new java.awt.Color(255, 255, 255));
        Date_Sampling.setDate(new Date());
        Date_Sampling.setDateFormatString("dd MMMM yyyy");
        Date_Sampling.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Date_Sampling.setMaxSelectableDate(new Date());

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel9.setText("No Laporan Produksi :");

        button_save.setBackground(new java.awt.Color(255, 255, 255));
        button_save.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_save.setText("Simpan");
        button_save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_saveActionPerformed(evt);
            }
        });

        label_tgl_uji.setBackground(new java.awt.Color(255, 255, 255));
        label_tgl_uji.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_tgl_uji.setText("Tanggal Sampling :");

        label_tanggal_masuk.setBackground(new java.awt.Color(255, 255, 255));
        label_tanggal_masuk.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_tanggal_masuk.setText("Pekerja Sampling :");

        label_judul.setBackground(new java.awt.Color(255, 255, 255));
        label_judul.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_judul.setText("Input Data Sampling");

        button_pick_timbang.setBackground(new java.awt.Color(255, 255, 255));
        button_pick_timbang.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_pick_timbang.setText("...");
        button_pick_timbang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_pick_timbangActionPerformed(evt);
            }
        });

        txt_pekerja_sampling_id.setEditable(false);
        txt_pekerja_sampling_id.setBackground(new java.awt.Color(255, 255, 255));
        txt_pekerja_sampling_id.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_pekerja_sampling_id.setFocusable(false);

        txt_pekerja_sampling_nama.setEditable(false);
        txt_pekerja_sampling_nama.setBackground(new java.awt.Color(255, 255, 255));
        txt_pekerja_sampling_nama.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_pekerja_sampling_nama.setFocusable(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(label_judul)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(label_tgl_uji, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_tanggal_masuk, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(label_lp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(txt_pekerja_sampling_id)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_pick_timbang, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(Date_Sampling, javax.swing.GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE)
                            .addComponent(txt_pekerja_sampling_nama)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(button_save)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label_judul, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_tgl_uji, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(label_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_Sampling, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_tanggal_masuk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_pekerja_sampling_id, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_pick_timbang, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_pekerja_sampling_nama, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_save)
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

    private void button_saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_saveActionPerformed
        edit();
    }//GEN-LAST:event_button_saveActionPerformed

    private void button_pick_timbangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_pick_timbangActionPerformed
        // TODO add your handling code here:
        Browse_Karyawan dialog = new Browse_Karyawan(new javax.swing.JFrame(), true, null);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);

        int x = Browse_Karyawan.table_list_karyawan.getSelectedRow();
        if (x != -1) {
            txt_pekerja_sampling_id.setText(Browse_Karyawan.table_list_karyawan.getValueAt(x, 0).toString());
            txt_pekerja_sampling_nama.setText(Browse_Karyawan.table_list_karyawan.getValueAt(x, 1).toString());
        }
    }//GEN-LAST:event_button_pick_timbangActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser Date_Sampling;
    private javax.swing.JButton button_pick_timbang;
    private javax.swing.JButton button_save;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel label_judul;
    private javax.swing.JLabel label_lp;
    private javax.swing.JLabel label_tanggal_masuk;
    private javax.swing.JLabel label_tgl_uji;
    private javax.swing.JTextField txt_pekerja_sampling_id;
    private javax.swing.JTextField txt_pekerja_sampling_nama;
    // End of variables declaration//GEN-END:variables
}
