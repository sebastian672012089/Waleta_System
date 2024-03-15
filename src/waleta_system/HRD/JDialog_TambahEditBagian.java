package waleta_system.HRD;

import waleta_system.Class.Utility;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class JDialog_TambahEditBagian extends javax.swing.JDialog {

    String sql = null;
    ResultSet rs;
    List<Integer> kode_atasan = new ArrayList<>();
    String kode_bagian = null;
    String namaBagianLama = null;

    public JDialog_TambahEditBagian(java.awt.Frame parent, boolean modal, String kode_bagian) {
        super(parent, modal);
        try {
            initComponents();
            ComboBox_departemen.removeAllItems();
            sql = "SELECT `kode_dep` AS 'value' FROM `tb_departemen` WHERE 1";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_departemen.addItem(rs.getString("value"));
            }
            ComboBox_atasan.removeAllItems();
            sql = "SELECT `kode_bagian`, `nama_bagian` FROM `tb_bagian` WHERE `tb_bagian`.`status_bagian` = 1";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                kode_atasan.add(rs.getInt("kode_bagian"));
                ComboBox_atasan.addItem(rs.getString("nama_bagian"));
            }
            if (kode_bagian != null) {
                this.kode_bagian = kode_bagian;
                getDataEdit();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JDialog_TambahEditBagian.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void getDataEdit() {
        try {
            sql = "SELECT `kode_bagian`, `nama_bagian`, `posisi_bagian`, `kode_departemen`, `divisi_bagian`, `bagian_bagian`, `ruang_bagian`, `grup`, `status_bagian`, `kepala_bagian` "
                    + "FROM `tb_bagian` WHERE `kode_bagian` = '" + kode_bagian + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                namaBagianLama = rs.getString("nama_bagian");
                label_nama_bagian.setText(rs.getString("nama_bagian"));
                txt_posisi.setText(rs.getString("posisi_bagian"));
                ComboBox_departemen.setSelectedItem(rs.getString("kode_departemen"));
                txt_divisi.setText(rs.getString("divisi_bagian"));
                txt_bagian.setText(rs.getString("bagian_bagian"));
                txt_ruang.setText(rs.getString("ruang_bagian"));
                txt_Grup.setText(rs.getString("grup"));
                ComboBox_atasan.setSelectedIndex(kode_atasan.indexOf(rs.getInt("kepala_bagian")));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JDialog_TambahEditBagian.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void NamaBagian() {
        txt_posisi.setText(txt_posisi.getText().toUpperCase());
        txt_divisi.setText(txt_divisi.getText().toUpperCase());
        txt_bagian.setText(txt_bagian.getText().toUpperCase());
        txt_ruang.setText(txt_ruang.getText().toUpperCase());
        String posisi = txt_posisi.getText();
        String departemen = ComboBox_departemen.getSelectedItem() == null ? "" : ComboBox_departemen.getSelectedItem().toString();
        String divisi = txt_divisi.getText();
        String bagian = txt_bagian.getText();
        String ruang = txt_ruang.getText();
        String nama_bagian = posisi + "-" + departemen + "-" + divisi + "-" + bagian + "-" + ruang;
        label_nama_bagian.setText(nama_bagian.toUpperCase());
    }

    private void tambah() {
        try {
            NamaBagian();
            String divisi = txt_divisi.getText() == null || txt_divisi.getText().equals("") ? "NULL" : "'" + txt_divisi.getText() + "'";
            String bagian = txt_bagian.getText() == null || txt_bagian.getText().equals("") ? "NULL" : "'" + txt_bagian.getText() + "'";
            String grup = txt_Grup.getText() == null || txt_Grup.getText().equals("") ? "-" : txt_Grup.getText();
//                String kode = null;
//                String qry = "SELECT MAX(`kode_bagian`)+1 AS 'kode' FROM `tb_bagian`";
//                rs = Utility.db.getStatement().executeQuery(qry);
//                if (rs.next()) {
//                    kode = rs.getString("kode");
//                }
            sql = "INSERT INTO `tb_bagian`(`kode_bagian`, `nama_bagian`, `posisi_bagian`, `kode_departemen`, `divisi_bagian`, `bagian_bagian`, `ruang_bagian`, `grup`, `kepala_bagian`) "
                    + "VALUES ("
                    + "(SELECT MAX(`kode_bagian`)+1 AS 'kode' FROM `tb_bagian` A),"
                    + "'" + label_nama_bagian.getText() + "',"
                    + "'" + txt_posisi.getText() + "',"
                    + "'" + ComboBox_departemen.getSelectedItem().toString() + "',"
                    + "" + divisi + ","
                    + "" + bagian + ","
                    + "'" + txt_ruang.getText() + "',"
                    + "'" + grup + "',"
                    + "'" + kode_atasan.get(ComboBox_atasan.getSelectedIndex()) + "'"
                    + ")";
            if ((Utility.db.getStatement().executeUpdate(sql)) > 0) {
                JOptionPane.showMessageDialog(this, "Data berhasil disimpan !");
                this.dispose();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JDialog_TambahEditBagian.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void edit() {
        try {
            Utility.db.getConnection().setAutoCommit(false);
            NamaBagian();
            String departemen = ComboBox_departemen.getSelectedItem() == null ? "NULL" : "'" + ComboBox_departemen.getSelectedItem().toString() + "'";
            String divisi = txt_divisi.getText() == null || txt_divisi.getText().equals("") ? "NULL" : "'" + txt_divisi.getText() + "'";
            String bagian = txt_bagian.getText() == null || txt_bagian.getText().equals("") ? "NULL" : "'" + txt_bagian.getText() + "'";
            String grup = txt_Grup.getText() == null || txt_Grup.getText().equals("") ? "-" : txt_Grup.getText();

            sql = "UPDATE `tb_bagian` SET "
                    + "`nama_bagian` = '" + label_nama_bagian.getText() + "', "
                    + "`posisi_bagian` = '" + txt_posisi.getText() + "', "
                    + "`kode_departemen` = " + departemen + ", "
                    + "`divisi_bagian` = " + divisi + ", "
                    + "`bagian_bagian` = " + bagian + ", "
                    + "`ruang_bagian` = '" + txt_ruang.getText() + "', "
                    + "`grup` = '" + grup + "', "
                    + "`kepala_bagian` = '" + kode_atasan.get(ComboBox_atasan.getSelectedIndex()) + "' "
                    + "WHERE `tb_bagian`.`kode_bagian` = '" + kode_bagian + "'";
            Utility.db.getStatement().executeUpdate(sql);
            
            String insert = "INSERT INTO `tb_bagian_edit_log`(`nama_bagian_lama`, `nama_bagian_baru`) VALUES ('" + namaBagianLama + "','" + label_nama_bagian.getText() + "')";
            Utility.db.getStatement().executeUpdate(insert);

            Utility.db.getConnection().commit();
            JOptionPane.showMessageDialog(this, "Data berhasil disimpan !");
            this.dispose();
        } catch (Exception ex) {
            try {
                Utility.db.getConnection().rollback();
            } catch (SQLException ex1) {
                Logger.getLogger(JDialog_TambahEditBagian.class.getName()).log(Level.SEVERE, null, ex1);
            }
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JDialog_TambahEditBagian.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                Utility.db.getConnection().setAutoCommit(false);
            } catch (SQLException ex) {
                Logger.getLogger(JDialog_TambahEditBagian.class.getName()).log(Level.SEVERE, null, ex);
            }
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
        jLabel2 = new javax.swing.JLabel();
        button_simpan = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        ComboBox_atasan = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txt_Grup = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txt_posisi = new javax.swing.JTextField();
        label_nama_bagian = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        txt_divisi = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        txt_bagian = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        txt_ruang = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        ComboBox_departemen = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Edit Grup");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setText("Atasan :");

        button_simpan.setBackground(new java.awt.Color(255, 255, 255));
        button_simpan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_simpan.setText("SIMPAN");
        button_simpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_simpanActionPerformed(evt);
            }
        });

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel1.setText("Tambah/Edit bagian");

        ComboBox_atasan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel3.setText("Nama Bagian :");

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel4.setText("Grup :");

        txt_Grup.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_Grup.setText("-");

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel9.setText("Posisi :");

        txt_posisi.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_posisi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_posisiKeyReleased(evt);
            }
        });

        label_nama_bagian.setBackground(new java.awt.Color(255, 255, 255));
        label_nama_bagian.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_nama_bagian.setText("----");

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel11.setText("Departemen :");

        txt_divisi.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_divisi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_divisiKeyReleased(evt);
            }
        });

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel12.setText("Divisi :");

        txt_bagian.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_bagian.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_bagianKeyReleased(evt);
            }
        });

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel13.setText("Bagian :");

        txt_ruang.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_ruang.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_ruangKeyReleased(evt);
            }
        });

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel14.setText("Ruang :");

        ComboBox_departemen.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        ComboBox_departemen.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ComboBox_departemenItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_divisi, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_simpan)
                            .addComponent(txt_bagian, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_ruang, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ComboBox_atasan, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ComboBox_departemen, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_posisi, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_Grup, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_nama_bagian, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel1)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_nama_bagian, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_posisi, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_departemen, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_divisi, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_bagian, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_ruang, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_Grup, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_atasan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_simpan)
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

    private void button_simpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_simpanActionPerformed
        // TODO add your handling code here:
        if (txt_posisi.getText() == null || txt_posisi.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Posisi tidak boleh kosong !");
        } else if (txt_ruang.getText() == null || txt_ruang.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Ruang tidak boleh kosong !");
        } else {
            if (kode_bagian == null) {
                tambah();
            } else {
                edit();
            }
        }
    }//GEN-LAST:event_button_simpanActionPerformed

    private void txt_posisiKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_posisiKeyReleased
        // TODO add your handling code here:
        NamaBagian();
    }//GEN-LAST:event_txt_posisiKeyReleased

    private void txt_divisiKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_divisiKeyReleased
        // TODO add your handling code here:
        NamaBagian();
    }//GEN-LAST:event_txt_divisiKeyReleased

    private void txt_bagianKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_bagianKeyReleased
        // TODO add your handling code here:
        NamaBagian();
    }//GEN-LAST:event_txt_bagianKeyReleased

    private void txt_ruangKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_ruangKeyReleased
        // TODO add your handling code here:
        NamaBagian();
    }//GEN-LAST:event_txt_ruangKeyReleased

    private void ComboBox_departemenItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ComboBox_departemenItemStateChanged
        // TODO add your handling code here:
        NamaBagian();
    }//GEN-LAST:event_ComboBox_departemenItemStateChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_atasan;
    private javax.swing.JComboBox<String> ComboBox_departemen;
    private javax.swing.JButton button_simpan;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel label_nama_bagian;
    private javax.swing.JTextField txt_Grup;
    private javax.swing.JTextField txt_bagian;
    private javax.swing.JTextField txt_divisi;
    private javax.swing.JTextField txt_posisi;
    private javax.swing.JTextField txt_ruang;
    // End of variables declaration//GEN-END:variables
}
