package waleta_system;

import java.awt.event.KeyEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.ExportToExcel;
import waleta_system.Class.Utility;

public class JPanel_Absensi_Karyawan_ViewOnly extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    PreparedStatement pst;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Date today = new Date();
    List<String> Subordinates = null;

    public JPanel_Absensi_Karyawan_ViewOnly() {
        initComponents();
    }

    public void init() {
        try {
            Subordinates = Utility.GetListBawahanFromKodeBagian(Integer.toString(MainForm.Login_kodeBagian));
            refreshTable_absen();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_Absensi_Karyawan_ViewOnly.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void refreshTable_absen() {
        try {
            DefaultTableModel model = (DefaultTableModel) tabel_data_absen.getModel();
            model.setRowCount(0);

            String kodeBagian = "";
            for (int i = 0; i < Subordinates.size(); i++) {
                if (i != 0) {
                    kodeBagian = kodeBagian + ", ";
                }
                kodeBagian = kodeBagian + "'" + Subordinates.get(i) + "'";
            }
            String filter_Subordinates = "";
            if (Subordinates.isEmpty()) {
                filter_Subordinates = "AND `tb_karyawan`.`id_pegawai` = '" + MainForm.Login_idPegawai + "' \n";
                txt_search_NamaKaryawan.setEnabled(false);
                txt_search_pin.setEnabled(false);
                txt_search_bagian.setEnabled(false);
            } else {
                filter_Subordinates = "AND (`tb_karyawan`.`kode_bagian` IN (" + kodeBagian + ") OR `tb_karyawan`.`id_pegawai` = '" + MainForm.Login_idPegawai + "') \n";
                txt_search_NamaKaryawan.setEnabled(true);
                txt_search_pin.setEnabled(true);
                txt_search_bagian.setEnabled(true);
            }

            String nama_pegawai = "AND `tb_karyawan`.`nama_pegawai` LIKE '%" + txt_search_NamaKaryawan.getText() + "%'\n";
            if (txt_search_NamaKaryawan.getText() == null || txt_search_NamaKaryawan.getText().equals("")) {
                nama_pegawai = "";
            }
            String bagian = "AND `tb_bagian`.`nama_bagian` LIKE '%" + txt_search_bagian.getText() + "%'\n";
            if (txt_search_bagian.getText() == null || txt_search_bagian.getText().equals("")) {
                bagian = "";
            }
            String filter_tanggal = "";
            if (Date_Search1.getDate() != null && Date_Search2.getDate() != null) {
                filter_tanggal = " AND (DATE(`scan_date`) BETWEEN '" + dateFormat.format(Date_Search1.getDate()) + "' AND '" + dateFormat.format(Date_Search2.getDate()) + "') \n";
            }
            sql = "SELECT `att_log`.`pin`, `tb_karyawan`.`id_pegawai`, `tb_karyawan`.`nama_pegawai`,`tb_bagian`.`nama_bagian`, `tb_bagian`.`kode_departemen`, `posisi`, DATE(`scan_date`) AS 'tanggal', TIME(`scan_date`) AS 'waktu',`att_mesin_finger`.`nama_mesin`, `verifymode`, `tb_surat_lembur`.`nomor_surat`, `jumlah_jam`, `tanggal_masuk`, `tb_surat_lembur_detail`.`mulai_lembur`\n"
                    + "FROM `att_log` "
                    + "LEFT JOIN `att_mesin_finger` ON `att_log`.`sn` = `att_mesin_finger`.`sn`\n"
                    + "LEFT JOIN `tb_karyawan` ON `att_log`.`pin` = `tb_karyawan`.`pin_finger`\n"
                    + "LEFT JOIN `tb_surat_lembur_detail` ON `tb_surat_lembur_detail`.`id_pegawai` = `tb_karyawan`.`id_pegawai` AND DATE(`att_log`.`scan_date`) = `tb_surat_lembur_detail`.`tanggal_lembur`\n"
                    + "LEFT JOIN `tb_surat_lembur` ON `tb_surat_lembur_detail`.`nomor_surat` = `tb_surat_lembur`.`nomor_surat`\n"
                    + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian` \n"
                    + "WHERE `pin` LIKE '%" + txt_search_pin.getText() + "%' "
                    + filter_Subordinates
                    + nama_pegawai
                    + bagian
                    + filter_tanggal
                    + " ORDER BY `tb_karyawan`.`nama_pegawai` ASC, `scan_date` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[13];
            while (rs.next()) {
                row[0] = rs.getString("pin");
                row[1] = rs.getString("id_pegawai");
                row[2] = rs.getString("nama_pegawai");
                row[3] = rs.getString("nama_bagian");
                row[4] = rs.getString("kode_departemen");
                row[5] = rs.getString("posisi");
                row[6] = rs.getDate("tanggal");
                row[7] = new SimpleDateFormat("HH:mm").format(rs.getTime("waktu"));
                row[8] = rs.getString("nama_mesin");
                switch (rs.getInt("verifymode")) {
                    case 1:
                        row[9] = "Fingerprint Scan";
                        break;
                    case 20:
                        row[9] = "Face Scan";
                        break;
                    default:
                        row[9] = "Unidentified";
                        break;
                }
                row[10] = rs.getString("nomor_surat");
                row[11] = rs.getInt("jumlah_jam");
                row[12] = rs.getString("mulai_lembur");

                if (CheckBox_hanya_absen_mesin.isSelected()) {
                    if (rs.getInt("verifymode") > 0) {
                        model.addRow(row);
                    }
                } else {
                    model.addRow(row);
                }
            }
            label_total_data_absen.setText(Integer.toString(tabel_data_absen.getRowCount()));
            ColumnsAutoSizer.sizeColumnsToFit(tabel_data_absen);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_Absensi_Karyawan_ViewOnly.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txt_search_NamaKaryawan = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txt_search_pin = new javax.swing.JTextField();
        jLabel35 = new javax.swing.JLabel();
        Date_Search1 = new com.toedter.calendar.JDateChooser();
        Date_Search2 = new com.toedter.calendar.JDateChooser();
        button_refresh = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabel_data_absen = new javax.swing.JTable();
        jLabel13 = new javax.swing.JLabel();
        label_total_data_absen = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        button_export_mentah = new javax.swing.JButton();
        CheckBox_hanya_absen_mesin = new javax.swing.JCheckBox();
        txt_search_bagian = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setText("Nama :");

        txt_search_NamaKaryawan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_search_NamaKaryawan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_NamaKaryawanKeyPressed(evt);
            }
        });

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel8.setText("PIN :");

        txt_search_pin.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_search_pin.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_pinKeyPressed(evt);
            }
        });

        jLabel35.setBackground(new java.awt.Color(255, 255, 255));
        jLabel35.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel35.setText("Tanggal :");

        Date_Search1.setBackground(new java.awt.Color(255, 255, 255));
        Date_Search1.setToolTipText("");
        Date_Search1.setDate(new Date());
        Date_Search1.setDateFormatString("dd MMM yyyy");
        Date_Search1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Date_Search1.setMinSelectableDate(new java.util.Date(1483207315000L));

        Date_Search2.setBackground(new java.awt.Color(255, 255, 255));
        Date_Search2.setDate(new Date());
        Date_Search2.setDateFormatString("dd MMM yyyy");
        Date_Search2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        button_refresh.setBackground(new java.awt.Color(255, 255, 255));
        button_refresh.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_refresh.setText("Refresh");
        button_refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refreshActionPerformed(evt);
            }
        });

        tabel_data_absen.setAutoCreateRowSorter(true);
        tabel_data_absen.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tabel_data_absen.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "PIN", "ID Pegawai", "Nama", "Bagian", "Departemen", "Posisi", "Tanggal", "Waktu Absen", "Mesin Absen", "Verifikasi", "SPL", "Jumlah Jam", "Jam Mulai"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tabel_data_absen);
        if (tabel_data_absen.getColumnModel().getColumnCount() > 0) {
            tabel_data_absen.getColumnModel().getColumn(10).setHeaderValue("SPL");
            tabel_data_absen.getColumnModel().getColumn(11).setHeaderValue("Jumlah Jam");
            tabel_data_absen.getColumnModel().getColumn(12).setHeaderValue("Jam Mulai");
        }

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel13.setText("Total Data :");

        label_total_data_absen.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_absen.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_data_absen.setText("0");

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel6.setText("Bagian :");

        button_export_mentah.setBackground(new java.awt.Color(255, 255, 255));
        button_export_mentah.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_export_mentah.setText("Export");
        button_export_mentah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_mentahActionPerformed(evt);
            }
        });

        CheckBox_hanya_absen_mesin.setBackground(new java.awt.Color(255, 255, 255));
        CheckBox_hanya_absen_mesin.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        CheckBox_hanya_absen_mesin.setText("Hanya Menampilkan Absen dari Mesin Finger");
        CheckBox_hanya_absen_mesin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CheckBox_hanya_absen_mesinActionPerformed(evt);
            }
        });

        txt_search_bagian.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_search_bagian.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_bagianKeyPressed(evt);
            }
        });

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel3.setText("Data Absensi Karyawan");

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 2, 12)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(102, 102, 102));
        jLabel14.setText("Note : Hanya menampilkan absen dari bagian dibawahnya dan absen user.");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(CheckBox_hanya_absen_mesin)
                            .addComponent(jLabel3)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_NamaKaryawan, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_pin, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_bagian, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel35)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_Search1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_Search2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_refresh)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_export_mentah)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_data_absen))
                            .addComponent(jLabel14))
                        .addGap(0, 315, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txt_search_bagian, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_NamaKaryawan, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_pin, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel35, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_Search1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_Search2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(button_refresh, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_export_mentah, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_data_absen, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(CheckBox_hanya_absen_mesin)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 562, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txt_search_NamaKaryawanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_NamaKaryawanKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_absen();
        }
    }
//GEN-LAST:event_txt_search_NamaKaryawanKeyPressed

    private void txt_search_pinKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_pinKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_absen();
        }
    }//GEN-LAST:event_txt_search_pinKeyPressed

    private void button_refreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refreshActionPerformed
        // TODO add your handling code here:
        refreshTable_absen();
    }//GEN-LAST:event_button_refreshActionPerformed

    private void button_export_mentahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_mentahActionPerformed
        // TODO add your handling code here:
        DefaultTableModel table = (DefaultTableModel) tabel_data_absen.getModel();
        ExportToExcel.writeToExcel(table, this);
    }//GEN-LAST:event_button_export_mentahActionPerformed

    private void CheckBox_hanya_absen_mesinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CheckBox_hanya_absen_mesinActionPerformed
        // TODO add your handling code here:
        refreshTable_absen();
    }//GEN-LAST:event_CheckBox_hanya_absen_mesinActionPerformed

    private void txt_search_bagianKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_bagianKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_absen();
        }
    }//GEN-LAST:event_txt_search_bagianKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox CheckBox_hanya_absen_mesin;
    private com.toedter.calendar.JDateChooser Date_Search1;
    private com.toedter.calendar.JDateChooser Date_Search2;
    private javax.swing.JButton button_export_mentah;
    private javax.swing.JButton button_refresh;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel label_total_data_absen;
    private javax.swing.JTable tabel_data_absen;
    private javax.swing.JTextField txt_search_NamaKaryawan;
    private javax.swing.JTextField txt_search_bagian;
    private javax.swing.JTextField txt_search_pin;
    // End of variables declaration//GEN-END:variables
}
