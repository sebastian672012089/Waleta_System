package waleta_system.BahanJadi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import waleta_system.Class.Utility;

public class JDialog_SaveStokOpname extends javax.swing.JDialog {

    String sql = null;
    ResultSet rs;
    PreparedStatement pst;
    Connection con;
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Date today = new Date();

    public JDialog_SaveStokOpname(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        try {
            initComponents();
            con = Utility.db.getConnection();
            sql = "SELECT SUM(`keping`) AS 'keping', SUM(`berat`) AS 'berat', `lokasi_terakhir`\n"
                    + "FROM `tb_box_bahan_jadi` \n"
                    + "LEFT JOIN `tb_box_packing` ON `tb_box_bahan_jadi`.`no_box` = `tb_box_packing`.`no_box`\n"
                    + "LEFT JOIN `tb_tutupan_grading` ON `tb_box_bahan_jadi`.`no_tutupan` = `tb_tutupan_grading`.`kode_tutupan`\n"
                    + "WHERE `lokasi_terakhir` IN ('GRADING', 'PACKING', 'RE-PROSES', 'TREATMENT', 'DIPINJAM') AND (`tb_box_packing`.`status` IN ('PENDING', 'STOK', 'RETUR') OR `tb_box_packing`.`status` IS NULL) \n"
                    + "AND (`tb_tutupan_grading`.`status_box` = 'SELESAI' OR `tb_tutupan_grading`.`status_box` IS NULL) "
                    + "GROUP BY `lokasi_terakhir`";
            rs = Utility.db.getStatement().executeQuery(sql);
            double total = 0;
            while (rs.next()) {
                switch (rs.getString("lokasi_terakhir")) {
                    case "GRADING":
                        label_stok_grading.setText(decimalFormat.format(rs.getDouble("berat")) + " Gram");
                        break;
                    case "PACKING":
                        label_stok_packing.setText(decimalFormat.format(rs.getDouble("berat")) + " Gram");
                        break;
                    case "RE-PROSES":
                        label_stok_reproses.setText(decimalFormat.format(rs.getDouble("berat")) + " Gram");
                        break;
                    case "TREATMENT":
                        label_stok_treatment.setText(decimalFormat.format(rs.getDouble("berat")) + " Gram");
                        break;
                    case "DIPINJAM":
                        label_stok_dipinjam.setText(decimalFormat.format(rs.getDouble("berat")) + " Gram");
                        break;
                    default:
                        break;
                }
                total = total + rs.getDouble("berat");
            }
            label_stok_total.setText(decimalFormat.format(total) + " Gram");
        } catch (Exception ex) {
            Logger.getLogger(JDialog_SaveStokOpname.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        label_stok_grading = new javax.swing.JLabel();
        label_stok_packing = new javax.swing.JLabel();
        label_stok_reproses = new javax.swing.JLabel();
        label_stok_total = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        label_stok_treatment = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        label_stok_dipinjam = new javax.swing.JLabel();
        Date_stokOpname = new com.toedter.calendar.JDateChooser();
        jProgressBar1 = new javax.swing.JProgressBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Save");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel1.setText("Save Data Stok Opname Barang Jadi");

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setText("Tanggal Stok Opname :");

        jButton1.setText("Save");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Detail Stok Opname", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 11))); // NOI18N

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel3.setText("Stok Grading BJ :");

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel4.setText("Stok Packing :");

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel5.setText("Stok Reproses :");

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel6.setText("Total :");

        label_stok_grading.setBackground(new java.awt.Color(255, 255, 255));
        label_stok_grading.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_stok_grading.setText("0 Gram");

        label_stok_packing.setBackground(new java.awt.Color(255, 255, 255));
        label_stok_packing.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_stok_packing.setText("0 Gram");

        label_stok_reproses.setBackground(new java.awt.Color(255, 255, 255));
        label_stok_reproses.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_stok_reproses.setText("0 Gram");

        label_stok_total.setBackground(new java.awt.Color(255, 255, 255));
        label_stok_total.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_stok_total.setText("0 Gram");

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel7.setText("Stok Treatment :");

        label_stok_treatment.setBackground(new java.awt.Color(255, 255, 255));
        label_stok_treatment.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_stok_treatment.setText("0 Gram");

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel8.setText("Stok Dipinjam :");

        label_stok_dipinjam.setBackground(new java.awt.Color(255, 255, 255));
        label_stok_dipinjam.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_stok_dipinjam.setText("0 Gram");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label_stok_grading)
                    .addComponent(label_stok_packing)
                    .addComponent(label_stok_reproses)
                    .addComponent(label_stok_total)
                    .addComponent(label_stok_treatment)
                    .addComponent(label_stok_dipinjam))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(label_stok_grading)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_stok_packing)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_stok_reproses)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_stok_treatment)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_stok_dipinjam)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_stok_total))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        Date_stokOpname.setBackground(new java.awt.Color(255, 255, 255));
        Date_stokOpname.setDate(today);
        Date_stokOpname.setDateFormatString("dd MMMM yyyy");
        Date_stokOpname.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton1))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_stokOpname, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 79, Short.MAX_VALUE))
                    .addComponent(jProgressBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_stokOpname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        try {
            boolean date_exist = true;
            sql = "SELECT `tgl_stok_opname` FROM `tb_stokopname_gbj` WHERE `tgl_stok_opname` = '" + dateFormat.format(Date_stokOpname.getDate()) + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            date_exist = rs.next();

            if (date_exist) {
                JOptionPane.showMessageDialog(this, "data Stok Opname pada tanggal " + new SimpleDateFormat("dd MMMM yyyy").format(Date_stokOpname.getDate()) + " sudah tersimpan !");
            } else {
                sql = "SELECT `tb_box_bahan_jadi` .`no_box`, `tanggal_box`, `kode_grade_bahan_jadi`, `tb_grade_bahan_jadi`.`kode_grade`,  `keping`, `berat`, `no_tutupan`, `status_terakhir`, `lokasi_terakhir`, `tb_grade_bahan_jadi`.`harga`\n"
                        + "FROM `tb_box_bahan_jadi` \n"
                        + "LEFT JOIN `tb_box_packing` ON `tb_box_bahan_jadi`.`no_box` = `tb_box_packing`.`no_box`\n"
                        + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_box_bahan_jadi`.`kode_grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode` "
                        + "LEFT JOIN `tb_tutupan_grading` ON `tb_box_bahan_jadi`.`no_tutupan` = `tb_tutupan_grading`.`kode_tutupan`\n"
                        + "WHERE `lokasi_terakhir` IN ('GRADING', 'PACKING', 'RE-PROSES', 'TREATMENT', 'DIPINJAM') AND (`tb_box_packing`.`status` IN ('PENDING', 'STOK', 'RETUR') OR `tb_box_packing`.`status` IS NULL) \n"
                        + "AND (`tb_tutupan_grading`.`status_box` = 'SELESAI' OR `tb_tutupan_grading`.`status_box` IS NULL) ";
                pst = con.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                rs = pst.executeQuery();

                int total_data = 0;
                while (rs.next()) {
                    total_data++;
                }
                rs.beforeFirst();

                jProgressBar1.setMinimum(0);
                jProgressBar1.setMaximum(total_data);
                jProgressBar1.setValue(0);
                jProgressBar1.setStringPainted(true);

                String insert_tgl = "INSERT INTO `tb_stokopname`(`tgl_stok_opname`) VALUES ('" + dateFormat.format(Date_stokOpname.getDate()) + "')";
                Utility.db.getConnection().createStatement();
                Utility.db.getStatement().executeUpdate(insert_tgl);

                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        try {
                            int jumlah_input = 0;
                            while (rs.next()) {
                                String insert = "INSERT INTO `tb_stokopname_gbj`(`tgl_stok_opname`, `no_box`, `tanggal_box`, `kode_grade_bahan_jadi`, `keping`, `berat`, `no_tutupan`, `lokasi_terakhir`, `harga_cny_kg`) "
                                        + "VALUES ("
                                        + "'" + dateFormat.format(Date_stokOpname.getDate()) + "',"
                                        + "'" + rs.getString("no_box") + "',"
                                        + "'" + dateFormat.format(rs.getDate("tanggal_box")) + "',"
                                        + "'" + rs.getString("kode_grade_bahan_jadi") + "',"
                                        + "'" + rs.getString("keping") + "',"
                                        + "'" + rs.getString("berat") + "',"
                                        + "'" + rs.getString("no_tutupan") + "',"
                                        + "'" + rs.getString("lokasi_terakhir") + "',"
                                        + "'" + rs.getString("harga") + "')";
                                Utility.db.getConnection().createStatement();
                                if ((Utility.db.getStatement().executeUpdate(insert)) > 0) {
                                    jumlah_input++;
                                }
                                jProgressBar1.setValue(jProgressBar1.getValue() + 1);
                            }
                            jProgressBar1.setValue(jProgressBar1.getMaximum());
                            JOptionPane.showMessageDialog(JDialog_SaveStokOpname.this, "Jumlah box berhasil di simpan : " + jumlah_input + "\nSilahkan mulai Scan semua BOX Stok tsb menggunakan aplikasi");
                            JDialog_SaveStokOpname.this.dispose();
                        } catch (SQLException ex) {
                            Logger.getLogger(JDialog_SaveStokOpname.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                };
                thread.start();
            }
        } catch (SQLException ex) {
            Logger.getLogger(JDialog_SaveStokOpname.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(JDialog_SaveStokOpname.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JDialog_SaveStokOpname.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JDialog_SaveStokOpname.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JDialog_SaveStokOpname.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JDialog_SaveStokOpname dialog = new JDialog_SaveStokOpname(new javax.swing.JFrame(), true);
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
    private com.toedter.calendar.JDateChooser Date_stokOpname;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JLabel label_stok_dipinjam;
    private javax.swing.JLabel label_stok_grading;
    private javax.swing.JLabel label_stok_packing;
    private javax.swing.JLabel label_stok_reproses;
    private javax.swing.JLabel label_stok_total;
    private javax.swing.JLabel label_stok_treatment;
    // End of variables declaration//GEN-END:variables
}
