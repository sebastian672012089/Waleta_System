package waleta_system.QC;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import waleta_system.Class.Utility;

public class JDialog_InsertEdit_TreatmentLP extends javax.swing.JDialog {

    String sql = null;
    ResultSet rs;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DecimalFormat decimalFormat = new DecimalFormat();
    Date date= new Date();

    public JDialog_InsertEdit_TreatmentLP(java.awt.Frame parent, boolean modal, String kode_treatment, String no_lp) {
        super(parent, modal);
        initComponents();

        if (kode_treatment != null) {
            button_insert_treatment.setVisible(true);
            button_edit_treatment.setVisible(false);
            txt_no_lp.setText(no_lp);
            txt_kode_treatment.setText(kode_treatment_baru());

        } else {
            button_insert_treatment.setVisible(false);
            button_edit_treatment.setVisible(true);
            txt_kode_treatment.setText(kode_treatment);
            loadData_edit();
        }
    }

    public String kode_treatment_baru() {
        try {
            int last_number = 0, test_number = 0;
            int sub_count = 0;
            if (txt_no_lp.getText().length() == 12) {
                sub_count = 10;
            } else {
                sub_count = 16;
            }
            String last = "SELECT `kode_treatment` AS 'last' FROM `tb_lab_treatment_lp` WHERE `kode_treatment` LIKE '" + txt_no_lp.getText().substring(3) + "%'";
            ResultSet result_last = Utility.db.getStatement().executeQuery(last);
            while (result_last.next()) {
                test_number = Integer.valueOf(result_last.getString("last").substring(sub_count));
                if (test_number > last_number) {
                    last_number = test_number;
                }
            }
            String new_kode = txt_no_lp.getText().substring(3) + "/" + Integer.toString(last_number + 1);
            return new_kode;
        } catch (SQLException ex) {
            Logger.getLogger(JDialog_InsertEdit_TreatmentLP.class.getName()).log(Level.SEVERE, null, ex);
            return "ERROR";
        }
    }

    public void loadData_edit() {
        try {
            sql = "SELECT `kode_treatment`, `no_laporan_produksi`, `jenis_barang`, `tgl_treatment`, `waktu_treatment`, `nitrit_awal`, `nitrit_akhir`, `status`, `print_label` "
                    + "FROM `tb_lab_treatment_lp` WHERE `kode_treatment` = '" + txt_kode_treatment.getText() + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                txt_no_lp.setText(rs.getString("no_laporan_produksi"));
                ComboBox_jenisBarang.setSelectedItem(rs.getString("jenis_barang"));
                Date_tgl_treatment.setDate(rs.getDate("tgl_treatment"));
                txt_waktu_treatment.setText(rs.getString("waktu_treatment"));
                txt_nitrit_awal.setText(rs.getString("nitrit_awal"));
                txt_nitrit_akhir.setText(rs.getString("nitrit_akhir"));
                label_status.setText(rs.getString("status"));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JDialog_InsertEdit_TreatmentLP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel_treatment = new javax.swing.JPanel();
        label2 = new javax.swing.JLabel();
        label3 = new javax.swing.JLabel();
        label5 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        button_insert_treatment = new javax.swing.JButton();
        button_edit_treatment = new javax.swing.JButton();
        txt_kode_treatment = new javax.swing.JTextField();
        txt_waktu_treatment = new javax.swing.JTextField();
        Date_tgl_treatment = new com.toedter.calendar.JDateChooser();
        label6 = new javax.swing.JLabel();
        txt_nitrit_awal = new javax.swing.JTextField();
        label7 = new javax.swing.JLabel();
        txt_nitrit_akhir = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        label8 = new javax.swing.JLabel();
        label1 = new javax.swing.JLabel();
        txt_no_lp = new javax.swing.JTextField();
        label4 = new javax.swing.JLabel();
        ComboBox_jenisBarang = new javax.swing.JComboBox<>();
        label_status = new javax.swing.JLabel();
        label9 = new javax.swing.JLabel();
        txt_max_nitrit = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel_treatment.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_treatment.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Treatment LP", javax.swing.border.TitledBorder.LEADING, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Arial", 1, 14))); // NOI18N
        jPanel_treatment.setName("aah"); // NOI18N

        label2.setBackground(new java.awt.Color(255, 255, 255));
        label2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label2.setText("Kode Treatment :");

        label3.setBackground(new java.awt.Color(255, 255, 255));
        label3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label3.setText("Tanggal treatment :");

        label5.setBackground(new java.awt.Color(255, 255, 255));
        label5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label5.setText("Waktu Treatment :");

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel10.setText("Menit");

        button_insert_treatment.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_insert_treatment.setText("insert");
        button_insert_treatment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_insert_treatmentActionPerformed(evt);
            }
        });

        button_edit_treatment.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_edit_treatment.setText("Edit");
        button_edit_treatment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_edit_treatmentActionPerformed(evt);
            }
        });

        txt_kode_treatment.setEditable(false);
        txt_kode_treatment.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N

        txt_waktu_treatment.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N

        Date_tgl_treatment.setBackground(new java.awt.Color(255, 255, 255));
        Date_tgl_treatment.setDate(date);
        Date_tgl_treatment.setDateFormatString("dd MMMM yyyy");
        Date_tgl_treatment.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        label6.setBackground(new java.awt.Color(255, 255, 255));
        label6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label6.setText("Nitrit Awal :");

        txt_nitrit_awal.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        txt_nitrit_awal.setText("0");

        label7.setBackground(new java.awt.Color(255, 255, 255));
        label7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label7.setText("Nitrit Akhir :");

        txt_nitrit_akhir.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        txt_nitrit_akhir.setText("0");
        txt_nitrit_akhir.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_nitrit_akhirFocusLost(evt);
            }
        });

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel12.setText("ppm");

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel13.setText("ppm");

        label8.setBackground(new java.awt.Color(255, 255, 255));
        label8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label8.setText("Status :");

        label1.setBackground(new java.awt.Color(255, 255, 255));
        label1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label1.setText("No. Laporan Produksi :");

        txt_no_lp.setEditable(false);
        txt_no_lp.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N

        label4.setBackground(new java.awt.Color(255, 255, 255));
        label4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label4.setText("Jenis Barang :");

        ComboBox_jenisBarang.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_jenisBarang.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Utuh", "Flat", "Jidun" }));

        label_status.setBackground(new java.awt.Color(255, 255, 255));
        label_status.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_status.setText("PASSED");

        label9.setBackground(new java.awt.Color(255, 255, 255));
        label9.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label9.setText("Max :");

        txt_max_nitrit.setEditable(false);
        txt_max_nitrit.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        txt_max_nitrit.setText("21");

        javax.swing.GroupLayout jPanel_treatmentLayout = new javax.swing.GroupLayout(jPanel_treatment);
        jPanel_treatment.setLayout(jPanel_treatmentLayout);
        jPanel_treatmentLayout.setHorizontalGroup(
            jPanel_treatmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_treatmentLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_treatmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_treatmentLayout.createSequentialGroup()
                        .addComponent(label8, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4)
                        .addGroup(jPanel_treatmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_treatmentLayout.createSequentialGroup()
                                .addComponent(txt_nitrit_akhir, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 37, Short.MAX_VALUE)
                                .addComponent(label9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_max_nitrit, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel_treatmentLayout.createSequentialGroup()
                                .addComponent(txt_waktu_treatment, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel10))
                            .addGroup(jPanel_treatmentLayout.createSequentialGroup()
                                .addComponent(txt_nitrit_awal, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel12))
                            .addComponent(label_status)))
                    .addGroup(jPanel_treatmentLayout.createSequentialGroup()
                        .addGroup(jPanel_treatmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(label2, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label3, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label1, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_treatmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(Date_tgl_treatment, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txt_kode_treatment)
                            .addComponent(txt_no_lp)))
                    .addGroup(jPanel_treatmentLayout.createSequentialGroup()
                        .addGroup(jPanel_treatmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(label5, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label6, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label7, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel_treatmentLayout.createSequentialGroup()
                                .addComponent(label4, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_jenisBarang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_treatmentLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(button_edit_treatment)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_insert_treatment)))
                .addContainerGap())
        );
        jPanel_treatmentLayout.setVerticalGroup(
            jPanel_treatmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_treatmentLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_treatmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_kode_treatment, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_treatmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_no_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_treatmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_jenisBarang, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_treatmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(Date_tgl_treatment, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_treatmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_waktu_treatment, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_treatmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_nitrit_awal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_treatmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_treatmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_max_nitrit, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_treatmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_nitrit_akhir, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_treatmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_status, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_treatmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_edit_treatment, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_insert_treatment, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel_treatment, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel_treatment, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void button_insert_treatmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_insert_treatmentActionPerformed
        try {
            decimalFormat = Utility.DecimalFormatUS(decimalFormat);

            Float nitrit_awal = Float.valueOf(txt_nitrit_awal.getText());
            Float nitrit_akhir = Float.valueOf(txt_nitrit_akhir.getText());

            String Query = "INSERT INTO `tb_lab_treatment_lp`(`kode_treatment`, `no_laporan_produksi`, `jenis_barang`, `tgl_treatment`, `waktu_treatment`, `nitrit_awal`, `nitrit_akhir`, `status`) "
                    + "VALUES ('" + txt_kode_treatment.getText() + "','" + txt_no_lp.getText() + "','" + ComboBox_jenisBarang.getSelectedItem() + "','" + dateFormat.format(Date_tgl_treatment.getDate()) + "','" + txt_waktu_treatment.getText() + "','" + nitrit_awal + "','" + nitrit_akhir + "','" + label_status.getText() + "')";
            if ((Utility.db.getStatement().executeUpdate(Query)) == 1) {
                JOptionPane.showMessageDialog(this, "data SAVED !");
            } else {
                JOptionPane.showMessageDialog(this, "FAILED!");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Input angka salah, untuk bilangan desimal harap menggunakan tanda titik (.)");
            Logger.getLogger(JDialog_InsertEdit_TreatmentLP.class.getName()).log(Level.SEVERE, null, e);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JDialog_InsertEdit_TreatmentLP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_insert_treatmentActionPerformed

    private void button_edit_treatmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_edit_treatmentActionPerformed
        // TODO add your handling code here:
        decimalFormat.setMaximumFractionDigits(2);
        try {
            String Query = "UPDATE `tb_lab_treatment_lp` SET "
                    + "`jenis_barang`='" + ComboBox_jenisBarang.getSelectedItem() + "',"
                    + "`tgl_treatment`='" + dateFormat.format(Date_tgl_treatment.getDate()) + "',"
                    + "`waktu_treatment`='" + txt_waktu_treatment.getText() + "',"
                    + "`nitrit_awal`='" + txt_nitrit_awal.getText() + "',"
                    + "`nitrit_akhir`='" + txt_nitrit_akhir.getText() + "',"
                    + "`status`='" + label_status.getText() + "' "
                    + "WHERE `kode_treatment`='" + txt_kode_treatment.getText() + "' AND `no_laporan_produksi`='" + txt_no_lp.getText() + "'";
            if ((Utility.db.getStatement().executeUpdate(Query)) == 1) {
                JOptionPane.showMessageDialog(this, "data SAVED !");
            } else {
                JOptionPane.showMessageDialog(this, "FAILED!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(JDialog_InsertEdit_TreatmentLP.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_edit_treatmentActionPerformed

    private void txt_nitrit_akhirFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_nitrit_akhirFocusLost
        // TODO add your handling code here:
        try {
            int max = Integer.valueOf(txt_max_nitrit.getText());
            if (Float.valueOf(txt_nitrit_akhir.getText()) > max) {
                label_status.setText("HOLD/NON GNS");
            } else {
                label_status.setText("PASSED");
            }
        } catch (NullPointerException e) {
            txt_nitrit_akhir.setText("0");
            label_status.setText("PASSED");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "format angka salah");
        }
    }//GEN-LAST:event_txt_nitrit_akhirFocusLost


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_jenisBarang;
    private com.toedter.calendar.JDateChooser Date_tgl_treatment;
    public static javax.swing.JButton button_edit_treatment;
    public static javax.swing.JButton button_insert_treatment;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JPanel jPanel_treatment;
    private javax.swing.JLabel label1;
    private javax.swing.JLabel label2;
    private javax.swing.JLabel label3;
    private javax.swing.JLabel label4;
    private javax.swing.JLabel label5;
    private javax.swing.JLabel label6;
    private javax.swing.JLabel label7;
    private javax.swing.JLabel label8;
    private javax.swing.JLabel label9;
    private javax.swing.JLabel label_status;
    private javax.swing.JTextField txt_kode_treatment;
    private javax.swing.JTextField txt_max_nitrit;
    private javax.swing.JTextField txt_nitrit_akhir;
    private javax.swing.JTextField txt_nitrit_awal;
    private javax.swing.JTextField txt_no_lp;
    private javax.swing.JTextField txt_waktu_treatment;
    // End of variables declaration//GEN-END:variables
}
