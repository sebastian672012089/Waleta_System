package waleta_system.Packing;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
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
import javax.swing.table.DefaultTableCellRenderer;
import waleta_system.Class.Utility;
import waleta_system.MainForm;

public class JDialog_pengiriman_PickUp_insert_edit extends javax.swing.JDialog {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    String id_request = null;

    public JDialog_pengiriman_PickUp_insert_edit(java.awt.Frame parent, boolean modal, String id_request) {
        super(parent, modal);
        initComponents();
        this.setResizable(false);
        button_tambah_spk.setIcon(Utility.ResizeImageIcon(new javax.swing.ImageIcon(getClass().getResource("/waleta_system/Images/add_green.png")), button_tambah_spk.getWidth(), button_tambah_spk.getHeight()));
        button_hapus_spk.setIcon(Utility.ResizeImageIcon(new javax.swing.ImageIcon(getClass().getResource("/waleta_system/Images/delete-icon.png")), button_hapus_spk.getWidth(), button_hapus_spk.getHeight()));
        try {
            label_hari_tanggal.setText(new SimpleDateFormat("EEEEE, dd MMMM yyyy").format(new Date()));
            label_admin.setText(MainForm.Login_NamaPegawai);
            sql = "SELECT `id_ekspedisi` FROM `tb_ekspedisi` WHERE 1";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_ekspedisi.addItem(rs.getString("id_ekspedisi"));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JDialog_pengiriman_PickUp_insert_edit.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (id_request != null) {
            LoadDataEdit(id_request);
            label_id_request.setText(id_request);
            this.id_request = id_request;
        }
    }

    public void LoadDataEdit(String id_request) {
        try {
            String query = "SELECT `id_request_pick_up`, `tb_pengiriman_request_pick_up`.`id_ekspedisi`, "
                    + "`tb_pengiriman_request_pick_up`.`nopol` AS 'no_polisi', `tb_ekspedisi_armada`.`jenis_armada`, "
                    + "`tb_pengiriman_request_pick_up`.`id_sopir`, `tb_ekspedisi_sopir`.`nama`, "
                    + "`kode_spk` "
                    + "FROM `tb_pengiriman_request_pick_up` "
                    + "LEFT JOIN `tb_ekspedisi_armada` ON `tb_pengiriman_request_pick_up`.`nopol` = `tb_ekspedisi_armada`.`nopol` "
                    + "LEFT JOIN `tb_ekspedisi_sopir` ON `tb_pengiriman_request_pick_up`.`id_sopir` = `tb_ekspedisi_sopir`.`id_sopir` "
                    + "WHERE "
                    + "`id_request_pick_up` = ? ";
            Connection connection = Utility.db.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, id_request);
            ResultSet result = preparedStatement.executeQuery();
            if (result.next()) {
                ComboBox_ekspedisi.setSelectedItem(result.getString("id_ekspedisi"));
                ComboBox_armada.setSelectedItem(result.getString("no_polisi") + "-" + result.getString("jenis_armada"));
                ComboBox_sopir.setSelectedItem(result.getString("id_sopir") + "-" + result.getString("nama"));
                label_spk.setText(result.getString("kode_spk"));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JDialog_pengiriman_PickUp_insert_edit.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void tambah() {
        try {
            String admin = label_admin.getText();
            String ekspedisi = ComboBox_ekspedisi.getSelectedItem().toString();
            String armada = ComboBox_armada.getSelectedItem().toString().split("-")[0];
            String sopir = ComboBox_sopir.getSelectedItem().toString().split("-")[0];
            String kodeSPK = label_spk.getText();

            String query = "INSERT INTO `tb_pengiriman_request_pick_up` "
                    + "(`admin`, `id_ekspedisi`, `nopol`, `id_sopir`, `nomor_tiket`, `kode_spk`) "
                    + "VALUES (?, ?, ?, ?, CONCAT(DATE_FORMAT(NOW(), '%y%m%d%H%i%s'), LPAD(FLOOR(RAND() * 10000), 4, '0')), ?)";

            Connection connection = Utility.db.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, admin);
            preparedStatement.setString(2, ekspedisi);
            preparedStatement.setString(3, armada);
            preparedStatement.setString(4, sopir);
            preparedStatement.setString(5, kodeSPK);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 1) {
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int ID = generatedKeys.getInt(1); // Assuming nomor_tiket is an integer
                    String selectQuery = "SELECT `nomor_tiket`, `nopol`, `tb_ekspedisi_sopir`.`nama` "
                            + "FROM tb_pengiriman_request_pick_up "
                            + "LEFT JOIN `tb_ekspedisi_sopir` ON `tb_pengiriman_request_pick_up`.`id_sopir` = `tb_ekspedisi_sopir`.`id_sopir` "
                            + "WHERE `id_request_pick_up` = ?";
                    PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
                    selectStatement.setInt(1, ID);
                    ResultSet resultSet = selectStatement.executeQuery();
                    if (resultSet.next()) {
                        String nomor_tiket = resultSet.getString("nomor_tiket");
                        String nopol = resultSet.getString("nopol");
                        String nama_sopir = resultSet.getString("nama");
                        String downloadFolderPath = Utility.getDownloadFolderPath();
                        if (downloadFolderPath != null) {
                            String imageName = "QR_" + ID + "_" + new SimpleDateFormat("yyMMdd").format(date) + ".png";
                            String imagePath = downloadFolderPath + File.separator + imageName;
                            Utility.generateQRCodeImage(imagePath, nomor_tiket, 400, 400);
                            JOptionPane.showMessageDialog(this, "Data berhasil disimpan ! QR Code Berhasil dibuat !");
                            Desktop.getDesktop().open(new File(downloadFolderPath));
                        } else {
                            JOptionPane.showMessageDialog(this, "Data berhasil disimpan ! Tidak dapat menemukan folder Download.");
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Data berhasil disimpan ! Gagal mendapatkan Nomor Tiket!");
                    }
                }
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Insert failed!");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JDialog_pengiriman_PickUp_insert_edit.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JDialog_pengiriman_PickUp_insert_edit.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void edit() {
        try {
            String admin = label_admin.getText();
            String ekspedisi = ComboBox_ekspedisi.getSelectedItem().toString();
            String armada = ComboBox_armada.getSelectedItem().toString().split("-")[0];
            String sopir = ComboBox_sopir.getSelectedItem().toString().split("-")[0];
            String kodeSPK = label_spk.getText();

            String query = "UPDATE `tb_pengiriman_request_pick_up` SET \n"
                    + "`admin`=?,\n"
                    + "`id_ekspedisi`=?,\n"
                    + "`nopol`=?,\n"
                    + "`id_sopir`=?,\n"
                    + "`kode_spk`=? \n"
                    + "WHERE "
                    + "`id_request_pick_up`=?";

            Connection connection = Utility.db.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, admin);
            preparedStatement.setString(2, ekspedisi);
            preparedStatement.setString(3, armada);
            preparedStatement.setString(4, sopir);
            preparedStatement.setString(5, kodeSPK);
            preparedStatement.setString(6, id_request);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 1) {
                JOptionPane.showMessageDialog(this, "Data berhasil disimpan !");
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Update failed!");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JDialog_pengiriman_PickUp_insert_edit.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel0 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        label_admin = new javax.swing.JLabel();
        label_spk = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        button_save = new javax.swing.JButton();
        label_hari_tanggal = new javax.swing.JLabel();
        ComboBox_ekspedisi = new javax.swing.JComboBox<>();
        button_tambah_spk = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        label_id_request = new javax.swing.JLabel();
        ComboBox_armada = new javax.swing.JComboBox<>();
        ComboBox_sopir = new javax.swing.JComboBox<>();
        button_hapus_spk = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Bagian Packing");
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel3.setText("Admin :");
        jLabel3.setFocusable(false);

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel1.setText("Hari, Tanggal :");
        jLabel1.setFocusable(false);

        jLabel0.setBackground(new java.awt.Color(255, 255, 255));
        jLabel0.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jLabel0.setText("Permintaan Pengambilan Barang");
        jLabel0.setFocusable(false);

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel5.setText("Armada :");
        jLabel5.setFocusable(false);

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel6.setText("Sopir :");
        jLabel6.setFocusable(false);

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel7.setText("SPK :");
        jLabel7.setFocusable(false);

        label_admin.setBackground(new java.awt.Color(255, 255, 255));
        label_admin.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_admin.setText("-");
        label_admin.setFocusable(false);

        label_spk.setBackground(new java.awt.Color(255, 255, 255));
        label_spk.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_spk.setFocusable(false);

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel9.setText("Ekspedisi :");
        jLabel9.setFocusable(false);

        button_save.setBackground(new java.awt.Color(255, 255, 255));
        button_save.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_save.setText("Simpan");
        button_save.setFocusable(false);
        button_save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_saveActionPerformed(evt);
            }
        });

        label_hari_tanggal.setBackground(new java.awt.Color(255, 255, 255));
        label_hari_tanggal.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_hari_tanggal.setText("-");
        label_hari_tanggal.setFocusable(false);

        ComboBox_ekspedisi.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        ComboBox_ekspedisi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ComboBox_ekspedisiActionPerformed(evt);
            }
        });

        button_tambah_spk.setBackground(new java.awt.Color(255, 255, 255));
        button_tambah_spk.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_tambah_spk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_tambah_spkActionPerformed(evt);
            }
        });

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setText("ID Request :");
        jLabel2.setFocusable(false);

        label_id_request.setBackground(new java.awt.Color(255, 255, 255));
        label_id_request.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_id_request.setText("-");
        label_id_request.setFocusable(false);

        ComboBox_armada.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        ComboBox_sopir.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        button_hapus_spk.setBackground(new java.awt.Color(255, 255, 255));
        button_hapus_spk.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_hapus_spk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_hapus_spkActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel0))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_save))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(label_spk, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_tambah_spk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_hapus_spk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(ComboBox_ekspedisi, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_hari_tanggal, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(ComboBox_sopir, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(ComboBox_armada, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_id_request, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_admin, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel0, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(label_hari_tanggal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(label_id_request, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_admin, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(ComboBox_ekspedisi, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_armada, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_sopir, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(label_spk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_tambah_spk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_hapus_spk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_save)
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
        boolean check = true;
        if (label_spk.getText() == null || label_spk.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "kode SPK belum dipilih, minimal 1 kode SPK");
            check = false;
        }
        if (check) {
            if (id_request == null) {
                tambah();
            } else {
                edit();
            }
        }
    }//GEN-LAST:event_button_saveActionPerformed

    private void button_tambah_spkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_tambah_spkActionPerformed
        // TODO add your handling code here:
        JDialog_ChooseSPK dialog = new JDialog_ChooseSPK(new javax.swing.JFrame(), true);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);

        int i = JDialog_ChooseSPK.table_kode_spk.getSelectedRow();
        if (i >= 0) {
            String kode_spk = JDialog_ChooseSPK.table_kode_spk.getValueAt(i, 0).toString();
            if (label_spk.getText().contains(kode_spk)) {
                JOptionPane.showMessageDialog(this, kode_spk + " sudah masuk dalam daftar");
            } else {
                label_spk.setText(label_spk.getText() + kode_spk + ",");
            }
        }
    }//GEN-LAST:event_button_tambah_spkActionPerformed

    private void ComboBox_ekspedisiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ComboBox_ekspedisiActionPerformed
        // TODO add your handling code here:
        try {
            ComboBox_armada.removeAllItems();
            sql = "SELECT `nopol`, `jenis_armada` FROM `tb_ekspedisi_armada` WHERE `id_ekspedisi` = '" + ComboBox_ekspedisi.getSelectedItem().toString() + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_armada.addItem(rs.getString("nopol") + "-" + rs.getString("jenis_armada"));
            }

            ComboBox_sopir.removeAllItems();
            sql = "SELECT `id_sopir`, `nama` FROM `tb_ekspedisi_sopir` WHERE `id_ekspedisi` = '" + ComboBox_ekspedisi.getSelectedItem().toString() + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_sopir.addItem(rs.getString("id_sopir") + "-" + rs.getString("nama"));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JDialog_pengiriman_PickUp_insert_edit.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_ComboBox_ekspedisiActionPerformed

    private void button_hapus_spkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_hapus_spkActionPerformed
        // TODO add your handling code here:
        label_spk.setText("");
    }//GEN-LAST:event_button_hapus_spkActionPerformed

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JDialog_pengiriman_PickUp_insert_edit.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JDialog_pengiriman_PickUp_insert_edit dialog = new JDialog_pengiriman_PickUp_insert_edit(new javax.swing.JFrame(), true, null);
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
    private javax.swing.JComboBox<String> ComboBox_armada;
    private javax.swing.JComboBox<String> ComboBox_ekspedisi;
    private javax.swing.JComboBox<String> ComboBox_sopir;
    private javax.swing.JButton button_hapus_spk;
    private javax.swing.JButton button_save;
    private javax.swing.JButton button_tambah_spk;
    private javax.swing.JLabel jLabel0;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel label_admin;
    private javax.swing.JLabel label_hari_tanggal;
    private javax.swing.JLabel label_id_request;
    private javax.swing.JLabel label_spk;
    // End of variables declaration//GEN-END:variables
}
