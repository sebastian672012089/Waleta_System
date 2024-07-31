package waleta_system.QC;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import waleta_system.Class.Utility;

public class JDialog_MasterBahanKimia_Tambah_Edit extends javax.swing.JDialog {

    String kode = null;
    String sql = null;
    ResultSet rs;
    Date date = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public JDialog_MasterBahanKimia_Tambah_Edit(java.awt.Frame parent, boolean modal, String kode) {
        super(parent, modal);
        initComponents();
        this.kode = kode;
        txt_kode_BahanKimia.setText(kode);
        if (kode != null) {
            try {
                sql = "SELECT `kode_bahan_kimia`, `nama_bahan_kimia`, `satuan`, `kondisi`, `supplier`, `penggunaan`, `aturan_pemakaian`, `msds`, `no_dokumen`, `no_revisi`, `tanggal_dokumen`, `menggantikan_no_dokumen` "
                        + "FROM `tb_lab_bahan_kimia` "
                        + "WHERE `kode_bahan_kimia` = '" + kode + "' ";
                rs = Utility.db.getStatement().executeQuery(sql);
                if (rs.next()) {
                    txt_nama_BahanKimia.setText(rs.getString("nama_bahan_kimia"));
                    txt_satuan_BahanKimia.setText(rs.getString("satuan"));
                    txt_kondisi_BahanKimia.setText(rs.getString("kondisi"));
                    txt_supplier_BahanKimia.setText(rs.getString("supplier"));
                    txt_penggunaan_BahanKimia.setText(rs.getString("penggunaan"));
                    txt_aturanPemakaian_BahanKimia.setText(rs.getString("aturan_pemakaian"));
                    CheckBox_msds.setSelected(rs.getString("msds").equals("Ada"));
                    txt_noDokumen.setText(rs.getString("no_dokumen"));
                    Spinner_no_revisi.setValue(rs.getInt("no_revisi"));
                    Date_Dokumen.setDate(rs.getDate("tanggal_dokumen"));
                    txt_menggantikan_no_dokumen.setText(rs.getString("menggantikan_no_dokumen"));
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, ex);
                Logger.getLogger(JDialog_MasterBahanKimia_Tambah_Edit.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void tambah() {
        try {
            String msds = CheckBox_msds.isSelected() ? "Ada" : "Tidak Ada";
            String tgl_dokumen = "NULL";
            if (Date_Dokumen.getDate() == null) {
                tgl_dokumen = "'" + dateFormat.format(Date_Dokumen.getDate()) + "'";
            }
            sql = "INSERT INTO `tb_lab_bahan_kimia`(`nama_bahan_kimia`, `satuan`, `kondisi`, `supplier`, `penggunaan`, `aturan_pemakaian`, `msds`, `no_dokumen`, `no_revisi`, `tanggal_dokumen`, `menggantikan_no_dokumen`) "
                    + "VALUES ("
                    + "'" + txt_nama_BahanKimia.getText() + "',"
                    + "'" + txt_satuan_BahanKimia.getText() + "',"
                    + "'" + txt_kondisi_BahanKimia.getText() + "',"
                    + "'" + txt_supplier_BahanKimia.getText() + "',"
                    + "'" + txt_penggunaan_BahanKimia.getText() + "',"
                    + "'" + txt_aturanPemakaian_BahanKimia.getText() + "',"
                    + "'" + msds + "',"
                    + "'" + txt_noDokumen.getText() + "',"
                    + "'" + Spinner_no_revisi.getValue() + "',"
                    + "" + tgl_dokumen + ","
                    + "'" + txt_menggantikan_no_dokumen.getText() + "'"
                    + ")";
            Utility.db.getConnection().createStatement();
            if ((Utility.db.getStatement().executeUpdate(sql)) == 1) {
                JOptionPane.showMessageDialog(this, "Data Berhasil disimpan");
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Tambah Data Gagal!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JDialog_Input_UjiPengiriman.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private void edit() {
        try {
            String msds = CheckBox_msds.isSelected() ? "Ada" : "Tidak Ada";
            String tgl_dokumen = "NULL";
            if (Date_Dokumen.getDate() != null) {
                tgl_dokumen = "'" + dateFormat.format(Date_Dokumen.getDate()) + "'";
            }
            sql = "UPDATE `tb_lab_bahan_kimia` SET "
                    + "`nama_bahan_kimia`='" + txt_nama_BahanKimia.getText() + "',"
                    + "`satuan`='" + txt_satuan_BahanKimia.getText() + "',"
                    + "`kondisi`='" + txt_kondisi_BahanKimia.getText() + "',"
                    + "`supplier`='" + txt_supplier_BahanKimia.getText() + "',"
                    + "`penggunaan`='" + txt_penggunaan_BahanKimia.getText() + "',"
                    + "`aturan_pemakaian`='" + txt_aturanPemakaian_BahanKimia.getText() + "',"
                    + "`msds`='" + msds + "', "
                    + "`no_dokumen`='" + txt_noDokumen.getText() + "',"
                    + "`no_revisi`='" + Spinner_no_revisi.getValue() + "',"
                    + "`tanggal_dokumen`=" + tgl_dokumen + ","
                    + "`menggantikan_no_dokumen`='" + txt_menggantikan_no_dokumen.getText() + "'"
                    + "WHERE `kode_bahan_kimia`='" + txt_kode_BahanKimia.getText() + "'";
            Utility.db.getConnection().createStatement();
            Utility.db.getStatement().executeUpdate(sql);
            JOptionPane.showMessageDialog(this, "Data Berhasil disimpan");
            this.dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JDialog_Input_UjiPengiriman.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        label_noTelp_customer_baku6 = new javax.swing.JLabel();
        label_noTelp_customer_baku4 = new javax.swing.JLabel();
        txt_kode_BahanKimia = new javax.swing.JTextField();
        label_noTelp_customer_baku3 = new javax.swing.JLabel();
        label_nama_customer_baku2 = new javax.swing.JLabel();
        txt_nama_BahanKimia = new javax.swing.JTextField();
        label_noTelp_customer_baku7 = new javax.swing.JLabel();
        txt_satuan_BahanKimia = new javax.swing.JTextField();
        txt_kondisi_BahanKimia = new javax.swing.JTextField();
        txt_supplier_BahanKimia = new javax.swing.JTextField();
        label_noTelp_customer_baku8 = new javax.swing.JLabel();
        txt_penggunaan_BahanKimia = new javax.swing.JTextField();
        label_noTelp_customer_baku9 = new javax.swing.JLabel();
        txt_aturanPemakaian_BahanKimia = new javax.swing.JTextField();
        label_noTelp_customer_baku10 = new javax.swing.JLabel();
        CheckBox_msds = new javax.swing.JCheckBox();
        Button_Save = new javax.swing.JButton();
        label_noTelp_customer_baku5 = new javax.swing.JLabel();
        txt_noDokumen = new javax.swing.JTextField();
        label_noTelp_customer_baku11 = new javax.swing.JLabel();
        label_noTelp_customer_baku12 = new javax.swing.JLabel();
        txt_menggantikan_no_dokumen = new javax.swing.JTextField();
        label_noTelp_customer_baku13 = new javax.swing.JLabel();
        Date_Dokumen = new com.toedter.calendar.JDateChooser();
        Spinner_no_revisi = new javax.swing.JSpinner();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Bahan Kimia");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel1.setText("Tambah Data Bahan Kimia");

        label_noTelp_customer_baku6.setBackground(new java.awt.Color(255, 255, 255));
        label_noTelp_customer_baku6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_noTelp_customer_baku6.setText("Satuan :");

        label_noTelp_customer_baku4.setBackground(new java.awt.Color(255, 255, 255));
        label_noTelp_customer_baku4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_noTelp_customer_baku4.setText("Nama Bahan Kimia :");

        txt_kode_BahanKimia.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_kode_BahanKimia.setEnabled(false);

        label_noTelp_customer_baku3.setBackground(new java.awt.Color(255, 255, 255));
        label_noTelp_customer_baku3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_noTelp_customer_baku3.setText("MSDS :");

        label_nama_customer_baku2.setBackground(new java.awt.Color(255, 255, 255));
        label_nama_customer_baku2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_nama_customer_baku2.setText("Kode Bahan Kimia :");

        txt_nama_BahanKimia.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        label_noTelp_customer_baku7.setBackground(new java.awt.Color(255, 255, 255));
        label_noTelp_customer_baku7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_noTelp_customer_baku7.setText("Kondisi :");

        txt_satuan_BahanKimia.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        txt_kondisi_BahanKimia.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        txt_supplier_BahanKimia.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        label_noTelp_customer_baku8.setBackground(new java.awt.Color(255, 255, 255));
        label_noTelp_customer_baku8.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_noTelp_customer_baku8.setText("Supplier :");

        txt_penggunaan_BahanKimia.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        label_noTelp_customer_baku9.setBackground(new java.awt.Color(255, 255, 255));
        label_noTelp_customer_baku9.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_noTelp_customer_baku9.setText("Penggunaan :");

        txt_aturanPemakaian_BahanKimia.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        label_noTelp_customer_baku10.setBackground(new java.awt.Color(255, 255, 255));
        label_noTelp_customer_baku10.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_noTelp_customer_baku10.setText("Aturan Pemakaian :");

        CheckBox_msds.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        CheckBox_msds.setText("Ada");

        Button_Save.setBackground(new java.awt.Color(255, 255, 255));
        Button_Save.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Button_Save.setText("Simpan");
        Button_Save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_SaveActionPerformed(evt);
            }
        });

        label_noTelp_customer_baku5.setBackground(new java.awt.Color(255, 255, 255));
        label_noTelp_customer_baku5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_noTelp_customer_baku5.setText("No Dokumen :");

        txt_noDokumen.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        label_noTelp_customer_baku11.setBackground(new java.awt.Color(255, 255, 255));
        label_noTelp_customer_baku11.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_noTelp_customer_baku11.setText("No Revisi :");

        label_noTelp_customer_baku12.setBackground(new java.awt.Color(255, 255, 255));
        label_noTelp_customer_baku12.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_noTelp_customer_baku12.setText("Tanggal Dokumen :");

        txt_menggantikan_no_dokumen.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        label_noTelp_customer_baku13.setBackground(new java.awt.Color(255, 255, 255));
        label_noTelp_customer_baku13.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_noTelp_customer_baku13.setText("Menggantikan No Dokumen :");

        Date_Dokumen.setBackground(new java.awt.Color(255, 255, 255));
        Date_Dokumen.setDateFormatString("dd MMM yyyy");
        Date_Dokumen.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        Spinner_no_revisi.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Spinner_no_revisi.setModel(new javax.swing.SpinnerNumberModel(0, null, 20, 1));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel1)
                .addContainerGap(211, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(label_nama_customer_baku2, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_noTelp_customer_baku3, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_noTelp_customer_baku9, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_noTelp_customer_baku4, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_noTelp_customer_baku8, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_noTelp_customer_baku6, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_noTelp_customer_baku7, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_noTelp_customer_baku10, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_noTelp_customer_baku5, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_noTelp_customer_baku11, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_noTelp_customer_baku12, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_noTelp_customer_baku13, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_aturanPemakaian_BahanKimia)
                            .addComponent(txt_kode_BahanKimia)
                            .addComponent(txt_penggunaan_BahanKimia)
                            .addComponent(txt_supplier_BahanKimia)
                            .addComponent(txt_kondisi_BahanKimia)
                            .addComponent(txt_satuan_BahanKimia)
                            .addComponent(txt_nama_BahanKimia)
                            .addComponent(CheckBox_msds, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txt_noDokumen)
                            .addComponent(txt_menggantikan_no_dokumen)
                            .addComponent(Date_Dokumen, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(Spinner_no_revisi)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(Button_Save)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_nama_customer_baku2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_kode_BahanKimia, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_noTelp_customer_baku4, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_nama_BahanKimia, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_noTelp_customer_baku6, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_satuan_BahanKimia, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_noTelp_customer_baku7, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_kondisi_BahanKimia, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_noTelp_customer_baku8, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_supplier_BahanKimia, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_noTelp_customer_baku9, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_penggunaan_BahanKimia, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_noTelp_customer_baku10, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_aturanPemakaian_BahanKimia, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_noTelp_customer_baku3, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CheckBox_msds, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_noTelp_customer_baku5, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_noDokumen, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_noTelp_customer_baku11, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Spinner_no_revisi, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(label_noTelp_customer_baku12, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_Dokumen, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_noTelp_customer_baku13, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_menggantikan_no_dokumen, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Button_Save)
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

    private void Button_SaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_SaveActionPerformed
        // TODO add your handling code here:
        if (kode == null) {
            tambah();
        } else {
            edit();
        }
    }//GEN-LAST:event_Button_SaveActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Button_Save;
    private javax.swing.JCheckBox CheckBox_msds;
    private com.toedter.calendar.JDateChooser Date_Dokumen;
    private javax.swing.JSpinner Spinner_no_revisi;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel label_nama_customer_baku2;
    private javax.swing.JLabel label_noTelp_customer_baku10;
    private javax.swing.JLabel label_noTelp_customer_baku11;
    private javax.swing.JLabel label_noTelp_customer_baku12;
    private javax.swing.JLabel label_noTelp_customer_baku13;
    private javax.swing.JLabel label_noTelp_customer_baku3;
    private javax.swing.JLabel label_noTelp_customer_baku4;
    private javax.swing.JLabel label_noTelp_customer_baku5;
    private javax.swing.JLabel label_noTelp_customer_baku6;
    private javax.swing.JLabel label_noTelp_customer_baku7;
    private javax.swing.JLabel label_noTelp_customer_baku8;
    private javax.swing.JLabel label_noTelp_customer_baku9;
    private javax.swing.JTextField txt_aturanPemakaian_BahanKimia;
    private javax.swing.JTextField txt_kode_BahanKimia;
    private javax.swing.JTextField txt_kondisi_BahanKimia;
    private javax.swing.JTextField txt_menggantikan_no_dokumen;
    private javax.swing.JTextField txt_nama_BahanKimia;
    private javax.swing.JTextField txt_noDokumen;
    private javax.swing.JTextField txt_penggunaan_BahanKimia;
    private javax.swing.JTextField txt_satuan_BahanKimia;
    private javax.swing.JTextField txt_supplier_BahanKimia;
    // End of variables declaration//GEN-END:variables
}
