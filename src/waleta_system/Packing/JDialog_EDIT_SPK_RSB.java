package waleta_system.Packing;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableCellRenderer;
import waleta_system.Class.Utility;

public class JDialog_EDIT_SPK_RSB extends javax.swing.JDialog {

    Date date = new Date();

    String sql = null;
    ResultSet rs;
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    String kode_spk = null, jenis_edit = null;

    public JDialog_EDIT_SPK_RSB(java.awt.Frame parent, boolean modal, String kode, String jenis_edit) {
        super(parent, modal);
        try {
            initComponents();

            ComboBox_kh.removeAllItems();
            sql = "SELECT `kode_kh` FROM `tb_dokumen_kh` WHERE 1";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_kh.addItem(rs.getString("kode_kh"));
            }
            sql = "SELECT `kode_spk`, `grade_waleta`, `grade_buyer`, `berat_kemasan`, `jumlah_kemasan`, `berat`, `prod_date`, `kode_kh` "
                    + "FROM `tb_spk_detail` WHERE `no` = '" + kode + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                label_nomor.setText(kode);
                label_kode_spk.setText(rs.getString("kode_spk"));
                label_grade_waleta.setText(rs.getString("grade_waleta"));
                label_grade_buyer.setText(rs.getString("grade_buyer"));
                label_berat_kemasan.setText(rs.getString("berat_kemasan"));
                label_jumlah_kemasan.setText(rs.getString("jumlah_kemasan"));
                label_total_net_weight.setText(rs.getString("berat"));
                Date_produksi.setDate(rs.getDate("prod_date"));
                if (rs.getString("kode_kh") != null) {
                    ComboBox_kh.setSelectedItem(rs.getString("kode_kh"));
                }
            }
            this.jenis_edit = jenis_edit;
            if (jenis_edit.equals("kh")) {
                Date_produksi.setVisible(false);
                ComboBox_kh.setVisible(true);
            } else {
                Date_produksi.setVisible(true);
                ComboBox_kh.setVisible(false);
            }
        } catch (Exception ex) {
            Logger.getLogger(JDialog_EDIT_SPK_RSB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        Date_produksi = new com.toedter.calendar.JDateChooser();
        jLabel5 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        label_kode_spk = new javax.swing.JLabel();
        label_grade_waleta = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        ComboBox_kh = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        label_berat_kemasan = new javax.swing.JLabel();
        label_grade_buyer = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        label_jumlah_kemasan = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        label_total_net_weight = new javax.swing.JLabel();
        button_save = new javax.swing.JButton();
        label_nomor = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("SansSerif", 1, 16)); // NOI18N
        jLabel1.setText("EDIT RSB & Tanggal Produksi");

        Date_produksi.setBackground(new java.awt.Color(255, 255, 255));
        Date_produksi.setDate(new Date());
        Date_produksi.setDateFormatString("dd MMM yyyy");
        Date_produksi.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel5.setText("Tanggal Produksi :");

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel3.setText("Kode KH :");

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel6.setText("Kode SPK :");

        label_kode_spk.setBackground(new java.awt.Color(255, 255, 255));
        label_kode_spk.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        label_kode_spk.setText("-");

        label_grade_waleta.setBackground(new java.awt.Color(255, 255, 255));
        label_grade_waleta.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        label_grade_waleta.setText("-");

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel9.setText("Grade Waleta :");

        ComboBox_kh.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel10.setText("Berat Kemasan :");

        label_berat_kemasan.setBackground(new java.awt.Color(255, 255, 255));
        label_berat_kemasan.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        label_berat_kemasan.setText("-");

        label_grade_buyer.setBackground(new java.awt.Color(255, 255, 255));
        label_grade_buyer.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        label_grade_buyer.setText("-");

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel13.setText("Grade Buyer :");

        label_jumlah_kemasan.setBackground(new java.awt.Color(255, 255, 255));
        label_jumlah_kemasan.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        label_jumlah_kemasan.setText("-");

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel15.setText("Jumlah Kemasan :");

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel16.setText("Total Net Weight :");

        label_total_net_weight.setBackground(new java.awt.Color(255, 255, 255));
        label_total_net_weight.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        label_total_net_weight.setText("-");

        button_save.setBackground(new java.awt.Color(255, 255, 255));
        button_save.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_save.setText("Save");
        button_save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_saveActionPerformed(evt);
            }
        });

        label_nomor.setBackground(new java.awt.Color(255, 255, 255));
        label_nomor.setFont(new java.awt.Font("SansSerif", 1, 16)); // NOI18N
        label_nomor.setText("00");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label_jumlah_kemasan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(label_grade_buyer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(label_berat_kemasan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Date_produksi, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ComboBox_kh, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(label_grade_waleta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(label_kode_spk, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(label_total_net_weight, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(button_save)
                .addGap(10, 10, 10))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 131, Short.MAX_VALUE)
                .addComponent(label_nomor)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(label_nomor))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_kode_spk, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_grade_waleta, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_grade_buyer, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_berat_kemasan, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_jumlah_kemasan, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_net_weight, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_kh, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_produksi, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_save, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
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
            Utility.db.getConnection().setAutoCommit(false);
            String kode_kh = ComboBox_kh.getSelectedItem().toString();
            String[] no_registrasi = ComboBox_kh.getSelectedItem().toString().split("-");
            if (jenis_edit.equals("kh")) {
                String Query = "UPDATE `tb_spk_detail` SET "
                        + "`kode_kh`='" + kode_kh + "' "
                        + "WHERE `no` = '" + label_nomor.getText() + "'";
                Utility.db.getConnection().createStatement();
                Utility.db.getStatement().executeUpdate(Query);

                if (!no_registrasi[0].equals("000")) {
                    String update_box = "UPDATE `tb_box_bahan_jadi` SET "
                            + "`kode_rsb`='" + no_registrasi[0] + "', `kode_kh`='" + kode_kh + "' "
                            + "WHERE `no_box` IN (SELECT `no_box`  FROM `tb_box_packing` WHERE `no_grade_spk` = '" + label_nomor.getText() + "')";
                    Utility.db.getStatement().executeUpdate(update_box);
                }
            } else {
                String Query = "UPDATE `tb_spk_detail` SET "
                        + "`prod_date`='" + dateFormat.format(Date_produksi.getDate()) + "' "
                        + "WHERE `no` = '" + label_nomor.getText() + "'";
                Utility.db.getConnection().createStatement();
                Utility.db.getStatement().executeUpdate(Query);
            }

            String update_batch_number = "UPDATE `tb_box_packing` SET "
                    + "`batch_number` = (SELECT CONCAT(SUBSTRING_INDEX(`kode_kh`, '-', 1), '-', DATE_FORMAT(`prod_date`, '%y%m%d')) AS 'batch_number' FROM `tb_spk_detail` WHERE `no` = '" + label_nomor.getText() + "') "
                    + "WHERE `no_grade_spk` = '" + label_nomor.getText() + "'";
            Utility.db.getStatement().executeUpdate(update_batch_number);

            Utility.db.getConnection().commit();
            JOptionPane.showMessageDialog(this, "Data Saved !");
            this.dispose();
        } catch (SQLException ex) {
            try {
                Utility.db.getConnection().rollback();
            } catch (SQLException ex1) {
                Logger.getLogger(JDialog_EDIT_SPK_RSB.class.getName()).log(Level.SEVERE, null, ex1);
            }
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JDialog_EDIT_SPK_RSB.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                Utility.db.getConnection().setAutoCommit(true);
            } catch (SQLException ex) {
                Logger.getLogger(JDialog_EDIT_SPK_RSB.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_button_saveActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_kh;
    private com.toedter.calendar.JDateChooser Date_produksi;
    private javax.swing.JButton button_save;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel label_berat_kemasan;
    private javax.swing.JLabel label_grade_buyer;
    private javax.swing.JLabel label_grade_waleta;
    private javax.swing.JLabel label_jumlah_kemasan;
    private javax.swing.JLabel label_kode_spk;
    private javax.swing.JLabel label_nomor;
    private javax.swing.JLabel label_total_net_weight;
    // End of variables declaration//GEN-END:variables
}
