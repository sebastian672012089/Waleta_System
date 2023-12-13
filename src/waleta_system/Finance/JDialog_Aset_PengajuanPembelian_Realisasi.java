package waleta_system.Finance;

import java.awt.event.KeyEvent;
import waleta_system.Class.Utility;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class JDialog_Aset_PengajuanPembelian_Realisasi extends javax.swing.JDialog {

    private String sql = null;
    private ResultSet rs;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DecimalFormat decimalFormat = new DecimalFormat();

    public JDialog_Aset_PengajuanPembelian_Realisasi(java.awt.Frame parent, boolean modal, String no) {
        super(parent, modal);
        initComponents();
        try {
            sql = "SELECT `no`, `keperluan`, `nama_barang`, `jumlah`, `satuan`, `realisasi_harga_satuan`, `ppn`, `biaya_lain`, `keterangan_biaya_lain`, `no_rekening`, `nama_rekening` \n"
                    + "FROM `tb_aset_pengajuan` \n"
                    + "LEFT JOIN `tb_karyawan` ON `tb_aset_pengajuan`.`diajukan` = `tb_karyawan`.`id_pegawai`\n"
                    + "WHERE `no` = '" + no + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                txt_no.setText(rs.getString("no"));
                txt_keperluan.setText(rs.getString("keperluan"));
                txt_nama_barang.setText(rs.getString("nama_barang"));
                txt_jumlah.setText(rs.getString("jumlah"));
                txt_satuan.setText(rs.getString("satuan"));
                txt_realisasi_harga_satuan.setText(rs.getString("realisasi_harga_satuan"));
                CheckBox_ppn.setSelected(rs.getInt("ppn") > 0);
                txt_biaya_lain2.setText(rs.getString("biaya_lain"));
                txt_keterangan_biaya_lain.setText(rs.getString("keterangan_biaya_lain"));
                if (rs.getString("no_rekening") != null) {
                    //TRANSFER
                    ComboBox_metode_bayar.setSelectedIndex(0);
                    jLabel22.setEnabled(true);
                    jLabel21.setEnabled(true);
                    txt_no_rekening.setEnabled(true);
                    txt_nama_rekening.setEnabled(true);
                    txt_no_rekening.setText(rs.getString("no_rekening"));
                    txt_nama_rekening.setText(rs.getString("nama_rekening"));
                } else {
                    //TUNAI
                    ComboBox_metode_bayar.setSelectedIndex(1);
                    jLabel22.setEnabled(false);
                    jLabel21.setEnabled(false);
                    txt_no_rekening.setEnabled(false);
                    txt_nama_rekening.setEnabled(false);
                }
            }
            hitung_total_harga();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JDialog_Aset_PengajuanPembelian_Realisasi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void hitung_total_harga() {
        try {
            int jumlah = Integer.valueOf(txt_jumlah.getText());
            int harga = txt_realisasi_harga_satuan.getText() == null || txt_realisasi_harga_satuan.getText().equals("") ? 0 : Integer.valueOf(txt_realisasi_harga_satuan.getText());
            int ppn = CheckBox_ppn.isSelected() ? Math.round((jumlah * harga) * 0.11f) : 0;
            int biaya_lain = txt_biaya_lain2.getText() == null || txt_biaya_lain2.getText().equals("") ? 0 : Integer.valueOf(txt_biaya_lain2.getText());
            int total = (jumlah * harga) + ppn + biaya_lain;
            txt_realisasi_total.setText("Rp. " + decimalFormat.format(total));
        } catch (Exception ex) {
            Logger.getLogger(JDialog_Aset_PengajuanPembelian_Realisasi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void edit() {
        try {
            String ppn = CheckBox_ppn.isSelected() ? "11" : "0";
            String no_rekening = "NULL";
            String nama_rekening = "NULL";
            if (ComboBox_metode_bayar.getSelectedIndex() == 0) {
                no_rekening = "'" + txt_no_rekening.getText() + "'";
                nama_rekening = "'" + txt_nama_rekening.getText() + "'";
            }
            sql = "UPDATE `tb_aset_pengajuan` SET "
                    + "`satuan`='" + txt_satuan.getText() + "',"
                    + "`realisasi_harga_satuan`='" + txt_realisasi_harga_satuan.getText() + "',"
                    + "`ppn`='" + ppn + "',"
                    + "`biaya_lain`='" + txt_biaya_lain2.getText() + "',"
                    + "`keterangan_biaya_lain`='" + txt_keterangan_biaya_lain.getText() + "',"
                    + "`no_rekening`=" + no_rekening + ","
                    + "`nama_rekening`=" + nama_rekening + " "
                    + "WHERE `no`='" + txt_no.getText() + "'";
            Utility.db.getConnection().createStatement();
            if ((Utility.db.getStatement().executeUpdate(sql)) == 1) {
                this.dispose();
                JOptionPane.showMessageDialog(this, "Data Berhasil simpan");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JDialog_Aset_PengajuanPembelian_Realisasi.class.getName()).log(Level.SEVERE, null, ex);
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
        label_title = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        button_save = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txt_nama_barang = new javax.swing.JTextField();
        txt_no = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        txt_keperluan = new javax.swing.JTextArea();
        jLabel15 = new javax.swing.JLabel();
        txt_realisasi_harga_satuan = new javax.swing.JTextField();
        txt_realisasi_total = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        txt_jumlah = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        txt_biaya_lain2 = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        CheckBox_ppn = new javax.swing.JCheckBox();
        jLabel19 = new javax.swing.JLabel();
        txt_keterangan_biaya_lain = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        ComboBox_metode_bayar = new javax.swing.JComboBox<>();
        txt_nama_rekening = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        txt_no_rekening = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txt_satuan = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        label_title.setBackground(new java.awt.Color(255, 255, 255));
        label_title.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_title.setText("Realisasi Pembelian Alat Kerja");

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setText("No :");

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel5.setText("Keperluan :");

        button_save.setBackground(new java.awt.Color(255, 255, 255));
        button_save.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_save.setText("Save");
        button_save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_saveActionPerformed(evt);
            }
        });

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel7.setText("Nama Barang :");

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel8.setText("Jumlah :");

        txt_nama_barang.setEditable(false);
        txt_nama_barang.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        txt_no.setEditable(false);
        txt_no.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        txt_keperluan.setEditable(false);
        txt_keperluan.setColumns(20);
        txt_keperluan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_keperluan.setLineWrap(true);
        txt_keperluan.setRows(3);
        jScrollPane1.setViewportView(txt_keperluan);

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel15.setText("Realisasi Harga Satuan :");

        txt_realisasi_harga_satuan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_realisasi_harga_satuan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_realisasi_harga_satuanKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_realisasi_harga_satuanKeyTyped(evt);
            }
        });

        txt_realisasi_total.setEditable(false);
        txt_realisasi_total.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel16.setText("Realisasi Total :");

        txt_jumlah.setEditable(false);
        txt_jumlah.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_jumlah.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_jumlahKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_jumlahKeyTyped(evt);
            }
        });

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel17.setText("PPN 11% :");

        txt_biaya_lain2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_biaya_lain2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_biaya_lain2KeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_biaya_lain2KeyTyped(evt);
            }
        });

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel18.setText("Biaya Lain - lain :");

        CheckBox_ppn.setBackground(new java.awt.Color(255, 255, 255));
        CheckBox_ppn.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        CheckBox_ppn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CheckBox_ppnActionPerformed(evt);
            }
        });

        jLabel19.setBackground(new java.awt.Color(255, 255, 255));
        jLabel19.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel19.setText("Ket. Biaya Lain :");

        txt_keterangan_biaya_lain.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel20.setBackground(new java.awt.Color(255, 255, 255));
        jLabel20.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel20.setText("Metode Pembayaran :");

        ComboBox_metode_bayar.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        ComboBox_metode_bayar.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Transfer", "Tunai" }));
        ComboBox_metode_bayar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ComboBox_metode_bayarActionPerformed(evt);
            }
        });

        txt_nama_rekening.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel21.setBackground(new java.awt.Color(255, 255, 255));
        jLabel21.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel21.setText("Nama Rekening :");

        jLabel22.setBackground(new java.awt.Color(255, 255, 255));
        jLabel22.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel22.setText("No Rekening :");

        txt_no_rekening.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel9.setText("Satuan :");

        txt_satuan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txt_keterangan_biaya_lain)
                    .addComponent(CheckBox_ppn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txt_no_rekening)
                    .addComponent(jScrollPane1)
                    .addComponent(txt_nama_rekening)
                    .addComponent(ComboBox_metode_bayar, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txt_realisasi_harga_satuan)
                    .addComponent(txt_biaya_lain2)
                    .addComponent(txt_jumlah)
                    .addComponent(txt_no)
                    .addComponent(txt_nama_barang)
                    .addComponent(txt_realisasi_total)
                    .addComponent(txt_satuan))
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(label_title)
                .addGap(179, 179, 179))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(button_save)
                .addGap(10, 10, 10))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label_title, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_no, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_nama_barang, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_jumlah, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_satuan, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_realisasi_harga_satuan, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CheckBox_ppn, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_biaya_lain2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_keterangan_biaya_lain, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_realisasi_total, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_metode_bayar, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_no_rekening, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_nama_rekening, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
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
        // TODO add your handling code here:
        if (ComboBox_metode_bayar.getSelectedIndex() == 0
                && (txt_no_rekening.getText() == null || txt_no_rekening.getText().equals(""))) {
            JOptionPane.showMessageDialog(this, "Silahkan lengkapi data diatas!");
        } else {
            edit();
        }
    }//GEN-LAST:event_button_saveActionPerformed

    private void txt_realisasi_harga_satuanKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_realisasi_harga_satuanKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_realisasi_harga_satuanKeyTyped

    private void txt_realisasi_harga_satuanKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_realisasi_harga_satuanKeyReleased
        // TODO add your handling code here:
        hitung_total_harga();
    }//GEN-LAST:event_txt_realisasi_harga_satuanKeyReleased

    private void txt_jumlahKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_jumlahKeyReleased
        // TODO add your handling code here:
        hitung_total_harga();
    }//GEN-LAST:event_txt_jumlahKeyReleased

    private void txt_jumlahKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_jumlahKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_jumlahKeyTyped

    private void txt_biaya_lain2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_biaya_lain2KeyReleased
        // TODO add your handling code here:
        hitung_total_harga();
    }//GEN-LAST:event_txt_biaya_lain2KeyReleased

    private void txt_biaya_lain2KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_biaya_lain2KeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_biaya_lain2KeyTyped

    private void CheckBox_ppnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CheckBox_ppnActionPerformed
        // TODO add your handling code here:
        hitung_total_harga();
    }//GEN-LAST:event_CheckBox_ppnActionPerformed

    private void ComboBox_metode_bayarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ComboBox_metode_bayarActionPerformed
        // TODO add your handling code here:
        if (ComboBox_metode_bayar.getSelectedIndex() == 0) {
            //TRANSFER
            jLabel22.setEnabled(true);
            jLabel21.setEnabled(true);
            txt_no_rekening.setEnabled(true);
            txt_nama_rekening.setEnabled(true);
        } else {
            //TUNAI
            jLabel22.setEnabled(false);
            jLabel21.setEnabled(false);
            txt_no_rekening.setEnabled(false);
            txt_nama_rekening.setEnabled(false);
            txt_no_rekening.setText(null);
            txt_nama_rekening.setText(null);
        }
    }//GEN-LAST:event_ComboBox_metode_bayarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox CheckBox_ppn;
    private javax.swing.JComboBox<String> ComboBox_metode_bayar;
    private javax.swing.JButton button_save;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel label_title;
    private javax.swing.JTextField txt_biaya_lain2;
    private javax.swing.JTextField txt_jumlah;
    private javax.swing.JTextArea txt_keperluan;
    private javax.swing.JTextField txt_keterangan_biaya_lain;
    private javax.swing.JTextField txt_nama_barang;
    private javax.swing.JTextField txt_nama_rekening;
    private javax.swing.JTextField txt_no;
    private javax.swing.JTextField txt_no_rekening;
    private javax.swing.JTextField txt_realisasi_harga_satuan;
    private javax.swing.JTextField txt_realisasi_total;
    private javax.swing.JTextField txt_satuan;
    // End of variables declaration//GEN-END:variables
}
