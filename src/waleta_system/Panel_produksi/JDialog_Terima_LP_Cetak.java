package waleta_system.Panel_produksi;

import waleta_system.Class.Utility;
import java.awt.HeadlessException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import waleta_system.Browse_Karyawan;
import waleta_system.MainForm;

public class JDialog_Terima_LP_Cetak extends javax.swing.JDialog {

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    Date date = new Date();
    PreparedStatement pst;
    String sql = null;
    ResultSet rs;
    String pekerja_koreksi_level, pekerja_koreksi_grup;

    public JDialog_Terima_LP_Cetak(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        try {

            initComponents();
            this.setResizable(false);
        } catch (Exception ex) {
            Logger.getLogger(JDialog_Terima_LP_Cetak.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void input_cetak() {
        try {
            Boolean Check = true;
            if (txt_no_lp.getText() == null || "".equals(txt_no_lp.getText())) {
                JOptionPane.showMessageDialog(this, "Masukan No Laporan Produksi !!");
                Check = false;
            } else {
                String query = "SELECT `tb_laporan_produksi`.`ruangan`, `tb_cetak`.`no_laporan_produksi`\n"
                        + "FROM `tb_laporan_produksi`\n"
                        + "LEFT JOIN `tb_cetak` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_cetak`.`no_laporan_produksi`\n"
                        + "WHERE `tb_laporan_produksi`.`no_laporan_produksi` = '" + txt_no_lp.getText() + "'";
                ResultSet result = Utility.db.getStatement().executeQuery(query);
                if (!result.next()) {
                    JOptionPane.showMessageDialog(this, "No LP salah, " + txt_no_lp.getText() + " tidak ditemukan di data LP !");
                    Check = false;
                } else if (result.getString("no_laporan_produksi") != null) {
                    JOptionPane.showMessageDialog(this, "LP sudah masuk Cetak!");
                    Check = false;
                } else if (result.getString("ruangan").length() != 5) {
                    Date tgl_setor_cabut = null;
                    String cabut_diserahkan = null, no_lp = null;
                    sql = "SELECT `no_laporan_produksi`, `tgl_setor_cabut`, `cabut_diserahkan` FROM `tb_cabut` WHERE `no_laporan_produksi` = '" + txt_no_lp.getText() + "'";
                    rs = Utility.db.getStatement().executeQuery(sql);
                    if (rs.next()) {
                        no_lp = rs.getString("no_laporan_produksi");
                        tgl_setor_cabut = rs.getDate("tgl_setor_cabut");
                        cabut_diserahkan = rs.getString("cabut_diserahkan");
                    }
                    if (no_lp == null) {
                        JOptionPane.showMessageDialog(this, "LP belum Masuk Cabut !!");
                        Check = false;
                    } else if ("-".equals(cabut_diserahkan) || tgl_setor_cabut == null) {
                        JOptionPane.showMessageDialog(this, "LP Sedang di cabut, belum di setorkan!!");
                        Check = false;
                    } else if (Date_terima.getDate().before(tgl_setor_cabut)) {
                        JOptionPane.showMessageDialog(this, "Tanggal masuk cetak harus setelah tanggal setor cabut\ntanggal cabut LP " + txt_no_lp.getText() + " : " + new SimpleDateFormat("dd MMMM yyyy").format(tgl_setor_cabut));
                        Check = false;
                    }
                }
            }

            if (Check) {
                String Query = "INSERT INTO `tb_cetak`(`no_laporan_produksi`, `tgl_mulai_cetak`, `cetak_diterima`, `cetak_dikoreksi_id`, `cetak_dikoreksi`, `cetak_dikoreksi_grup`, `cetak_dikoreksi_level`) "
                        + "VALUES ('" + txt_no_lp.getText() + "',"
                        + "'" + dateFormat.format(Date_terima.getDate()) + "',"
                        + "'" + txt_diterima.getText() + "',"
                        + "'" + txt_pekerja_koreksi_id.getText() + "', "
                        + "'" + txt_pekerja_koreksi_nama.getText() + "', "
                        + "'" + pekerja_koreksi_grup + "', "
                        + "'" + pekerja_koreksi_level + "' "
                        + ")";
                Utility.db.getConnection().createStatement();
                if ((Utility.db.getStatement().executeUpdate(Query)) == 1) {
                    JPanel_DataCetak.button_search_cetak.doClick();
                    this.dispose();
                    JOptionPane.showMessageDialog(this, "Data Inserted !");
                }
            }
        } catch (HeadlessException | SQLException e) {
            JOptionPane.showMessageDialog(this, "LP belum di Cabut !!");
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, e);
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
        label_title_terima_lp = new javax.swing.JLabel();
        button_pick_pengoreksi = new javax.swing.JButton();
        txt_no_lp = new javax.swing.JTextField();
        button_pick_diserahkan = new javax.swing.JButton();
        button_save = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        txt_diterima = new javax.swing.JTextField();
        txt_pekerja_koreksi_id = new javax.swing.JTextField();
        Date_terima = new com.toedter.calendar.JDateChooser();
        jLabel2 = new javax.swing.JLabel();
        button_cancel = new javax.swing.JButton();
        txt_pekerja_koreksi_nama = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Terima LP Oleh Cabut");
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        label_title_terima_lp.setFont(new java.awt.Font("Candara", 1, 18)); // NOI18N
        label_title_terima_lp.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_title_terima_lp.setText("Terima LP Cetak");

        button_pick_pengoreksi.setBackground(new java.awt.Color(255, 255, 255));
        button_pick_pengoreksi.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_pick_pengoreksi.setText("...");
        button_pick_pengoreksi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_pick_pengoreksiActionPerformed(evt);
            }
        });

        txt_no_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_no_lp.setText("WL-");
        txt_no_lp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_no_lpKeyTyped(evt);
            }
        });

        button_pick_diserahkan.setBackground(new java.awt.Color(255, 255, 255));
        button_pick_diserahkan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_pick_diserahkan.setText("...");
        button_pick_diserahkan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_pick_diserahkanActionPerformed(evt);
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

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel5.setText("Tanggal Terima :");

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel18.setText("Dikoreksi Oleh :");

        txt_diterima.setEditable(false);
        txt_diterima.setBackground(new java.awt.Color(255, 255, 255));
        txt_diterima.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        txt_pekerja_koreksi_id.setEditable(false);
        txt_pekerja_koreksi_id.setBackground(new java.awt.Color(255, 255, 255));
        txt_pekerja_koreksi_id.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Date_terima.setBackground(new java.awt.Color(255, 255, 255));
        Date_terima.setDateFormatString("dd MMMM yyyy");
        Date_terima.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        Date_terima.setMaxSelectableDate(new Date());

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("No. Laporan Produksi :");

        button_cancel.setBackground(new java.awt.Color(255, 255, 255));
        button_cancel.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_cancel.setText("Cancel");
        button_cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_cancelActionPerformed(evt);
            }
        });

        txt_pekerja_koreksi_nama.setEditable(false);
        txt_pekerja_koreksi_nama.setBackground(new java.awt.Color(255, 255, 255));
        txt_pekerja_koreksi_nama.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel6.setText("Diterima Oleh :");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txt_diterima, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txt_pekerja_koreksi_id, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txt_pekerja_koreksi_nama, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, 0)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(button_pick_pengoreksi, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(button_pick_diserahkan, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(Date_terima, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_no_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_cancel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_save))
                    .addComponent(label_title_terima_lp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label_title_terima_lp)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_no_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_terima, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_pick_pengoreksi, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_pekerja_koreksi_id, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_pekerja_koreksi_nama, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_diterima, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_pick_diserahkan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_save, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_cancel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
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
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void button_cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_cancelActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_button_cancelActionPerformed

    private void button_saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_saveActionPerformed
        input_cetak();
    }//GEN-LAST:event_button_saveActionPerformed

    private void button_pick_diserahkanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_pick_diserahkanActionPerformed
        // TODO add your handling code here:
        String filter_tgl = "AND DATE(`att_log`.`scan_date`) = CURRENT_DATE()";
        Browse_Karyawan dialog = new Browse_Karyawan(new javax.swing.JFrame(), true, filter_tgl);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);
        int x = waleta_system.Browse_Karyawan.table_list_karyawan.getSelectedRow();
        if (x != -1) {
            txt_diterima.setText(waleta_system.Browse_Karyawan.table_list_karyawan.getValueAt(x, 1).toString());
        }
    }//GEN-LAST:event_button_pick_diserahkanActionPerformed

    private void txt_no_lpKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_no_lpKeyTyped
        // TODO add your handling code here:
        if (Character.isAlphabetic(evt.getKeyChar())) {
            char e = evt.getKeyChar();
            String c = String.valueOf(e);
            evt.consume();
            txt_no_lp.setText(txt_no_lp.getText() + c.toUpperCase());
        }
    }//GEN-LAST:event_txt_no_lpKeyTyped

    private void button_pick_pengoreksiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_pick_pengoreksiActionPerformed
        // TODO add your handling code here:
        String filter_tgl = "AND DATE(`att_log`.`scan_date`) = CURRENT_DATE()";
        Browse_Karyawan dialog = new Browse_Karyawan(new javax.swing.JFrame(), true, filter_tgl);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);

        int x = Browse_Karyawan.table_list_karyawan.getSelectedRow();
        if (x != -1) {
            txt_pekerja_koreksi_id.setText(Browse_Karyawan.table_list_karyawan.getValueAt(x, 0).toString());
            txt_pekerja_koreksi_nama.setText(Browse_Karyawan.table_list_karyawan.getValueAt(x, 1).toString());
            try {
                sql = "SELECT `level_gaji`, `tb_grup_cabut`.`kode_grup`\n"
                        + "FROM `tb_karyawan` "
                        + "LEFT JOIN `tb_grup_cabut` ON `tb_karyawan`.`id_pegawai` = `tb_grup_cabut`.`id_pegawai`\n"
                        + "WHERE `tb_karyawan`.`id_pegawai` = '" + Browse_Karyawan.table_list_karyawan.getValueAt(x, 0).toString() + "'";
                rs = Utility.db.getStatement().executeQuery(sql);
                if (rs.next()) {
                    pekerja_koreksi_level = rs.getString("level_gaji");
                    pekerja_koreksi_grup = rs.getString("kode_grup");
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, e);
                Logger.getLogger(JDialog_Setor_LP_Cetak.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }//GEN-LAST:event_button_pick_pengoreksiActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser Date_terima;
    private javax.swing.JButton button_cancel;
    private javax.swing.JButton button_pick_diserahkan;
    private javax.swing.JButton button_pick_pengoreksi;
    private javax.swing.JButton button_save;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel label_title_terima_lp;
    private javax.swing.JTextField txt_diterima;
    private javax.swing.JTextField txt_no_lp;
    private javax.swing.JTextField txt_pekerja_koreksi_id;
    private javax.swing.JTextField txt_pekerja_koreksi_nama;
    // End of variables declaration//GEN-END:variables
}
