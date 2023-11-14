package waleta_system.SubWaleta;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import waleta_system.Browse_Karyawan;
import waleta_system.Class.Utility;

public class Insert_DataKaryawan_SUB extends javax.swing.JFrame {

    String sql = null;
    ResultSet rs;
    PreparedStatement pst;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    //JFileChooser chooser = new JFileChooser(); 

    public Insert_DataKaryawan_SUB(boolean modal) {
//        super(parent, modal);
        initComponents();
        //coding untuk menambahkan confirm dialog ketika default close operation di klik
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                e.getWindow().dispose();
            }
        });

        this.setResizable(false);

        try {
            Utility.db_sub.connect();
            ComboBox_bagian.removeAllItems();
            sql = "SELECT `kode_sub` FROM `tb_sub_waleta` WHERE 1";
            rs = Utility.db_sub.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_bagian.addItem(rs.getString("kode_sub"));
            }

            ComboBox_levelGaji.removeAllItems();
            sql = "SELECT `level_gaji` FROM `tb_level_gaji` WHERE 1 ORDER BY `upah_per_hari` DESC";
            rs = Utility.db_sub.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_levelGaji.addItem(rs.getString("level_gaji"));
            }

            String query = "SELECT MAX(RIGHT(`id_pegawai`,5)) AS last_id FROM `tb_karyawan` WHERE `id_pegawai` LIKE '00%' AND YEAR(`tanggal_masuk`) = YEAR(CURRENT_DATE)";
            ResultSet result = Utility.db_sub.getStatement().executeQuery(query);
            if (result.next()) {
                txt_id_pegawai.setText("00" + new SimpleDateFormat("yyMM").format(new Date()) + String.format("%05d", result.getInt("last_id") + 1));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(Insert_DataKaryawan_SUB.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void insert() {
        try {
            Boolean Check = true;
            String new_ID = "";
            if (txt_nama.getText() == null || txt_nama.getText().equals("")
                    || txt_id_pegawai.getText() == null || txt_id_pegawai.getText().equals("")
                    || txt_login_username.getText() == null || txt_login_username.getText().equals("")
                    || Date_tgl_lahir.getDate() == null
                    || Date_masuk.getDate() == null) {
                JOptionPane.showMessageDialog(this, "Silahkan lengkapi data diatas");
                Check = false;
            } else {
                if (txt_id_pegawai.getText().substring(0, 2).equals("00")) {
                    String query = "SELECT MAX(RIGHT(`id_pegawai`,5)) AS last_id FROM `tb_karyawan` WHERE `id_pegawai` LIKE '00%' AND YEAR(`tanggal_masuk`) = YEAR(CURRENT_DATE)";
                    ResultSet result = Utility.db_sub.getStatement().executeQuery(query);
                    if (result.next()) {
                        new_ID = "00" + new SimpleDateFormat("yyMM").format(new Date()) + String.format("%05d", result.getInt("last_id") + 1);
                    } else {
                        JOptionPane.showMessageDialog(this, "Gagal membuat id karyawan");
                        Check = false;
                    }
                }
            }

            if (Check) {
                Utility.db_sub.connect();
                String jenis_kelamin = "L";
                if (ComboBox_Kelamin.getSelectedIndex() == 0) {
                    jenis_kelamin = "L";
                } else {
                    jenis_kelamin = "P";
                }

                sql = "INSERT INTO `tb_karyawan`(`id_pegawai`, `nama_pegawai`, `bagian`, `email`, `tgl_lahir`, `jenis_kelamin`, `no_hp`, `level_gaji`, `status`, `tanggal_masuk`, `login_username`) "
                        + "VALUES ('" + txt_id_pegawai.getText() + "','" + txt_nama.getText() + "','" + ComboBox_bagian.getSelectedItem().toString() + "','" + txt_email.getText() + "','" + dateFormat.format(Date_tgl_lahir.getDate()) + "','" + jenis_kelamin + "','" + txt_no_telp.getText() + "','" + ComboBox_levelGaji.getSelectedItem().toString() + "','IN-SUB','" + dateFormat.format(Date_masuk.getDate()) + "', '" + txt_login_username.getText() + "')";
                Utility.db_sub.getConnection().prepareStatement(sql);
                if ((Utility.db_sub.getStatement().executeUpdate(sql)) > 0) {
                    JOptionPane.showMessageDialog(this, "INPUT ONLINE SUKSES");
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "INPUT ONLINE GAGAL!!!");
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(Insert_DataKaryawan_SUB.class.getName()).log(Level.SEVERE, null, e);
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

        jPanel2 = new javax.swing.JPanel();
        button_cancel = new javax.swing.JButton();
        label_title_op_karyawan = new javax.swing.JLabel();
        button_proceed_insert = new javax.swing.JButton();
        txt_no_telp = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        ComboBox_levelGaji = new javax.swing.JComboBox<>();
        ComboBox_Kelamin = new javax.swing.JComboBox<>();
        jLabel18 = new javax.swing.JLabel();
        Date_masuk = new com.toedter.calendar.JDateChooser();
        Date_tgl_lahir = new com.toedter.calendar.JDateChooser();
        txt_nama = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        ComboBox_posisi = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        txt_id_pegawai = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        txt_login_username = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        ComboBox_bagian = new javax.swing.JComboBox<>();
        txt_email = new javax.swing.JTextField();
        button_get_data_waleta = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Data Karyawan");

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        button_cancel.setBackground(new java.awt.Color(255, 255, 255));
        button_cancel.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N
        button_cancel.setText("Cancel");
        button_cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_cancelActionPerformed(evt);
            }
        });

        label_title_op_karyawan.setBackground(new java.awt.Color(255, 255, 255));
        label_title_op_karyawan.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        label_title_op_karyawan.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_title_op_karyawan.setText("Data Karyawan Sub");

        button_proceed_insert.setBackground(new java.awt.Color(255, 255, 255));
        button_proceed_insert.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N
        button_proceed_insert.setText("Save");
        button_proceed_insert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_proceed_insertActionPerformed(evt);
            }
        });

        txt_no_telp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_no_telp.setText("-");

        jLabel28.setBackground(new java.awt.Color(255, 255, 255));
        jLabel28.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N
        jLabel28.setText("Email :");

        jLabel27.setBackground(new java.awt.Color(255, 255, 255));
        jLabel27.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N
        jLabel27.setText("No Telp :");

        ComboBox_levelGaji.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        ComboBox_Kelamin.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_Kelamin.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "LAKI-LAKI", "PEREMPUAN" }));

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N
        jLabel18.setText("Level Gaji Sub");

        Date_masuk.setBackground(new java.awt.Color(255, 255, 255));
        Date_masuk.setDate(new Date());
        Date_masuk.setDateFormatString("dd MMMM yyyy");
        Date_masuk.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N

        Date_tgl_lahir.setBackground(new java.awt.Color(255, 255, 255));
        Date_tgl_lahir.setDateFormatString("dd MMMM yyyy");
        Date_tgl_lahir.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Date_tgl_lahir.setMaxSelectableDate(new Date());

        txt_nama.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N
        jLabel16.setText("Tanggal Masuk :");

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N
        jLabel4.setText("Jenis Kelamin :");

        ComboBox_posisi.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_posisi.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "KARYAWAN", "KEPALA" }));

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N
        jLabel3.setText("Nama Lengkap :");

        txt_id_pegawai.setEditable(false);
        txt_id_pegawai.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N
        jLabel6.setText("Tanggal Lahir :");

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N
        jLabel15.setText("Nama Bagian :");

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N
        jLabel14.setText("Posisi :");

        txt_login_username.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N
        jLabel13.setText("ID Pegawai :");

        jLabel19.setBackground(new java.awt.Color(255, 255, 255));
        jLabel19.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N
        jLabel19.setText("Login Username :");

        ComboBox_bagian.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        txt_email.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_email.setText("-");

        button_get_data_waleta.setBackground(new java.awt.Color(255, 255, 255));
        button_get_data_waleta.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N
        button_get_data_waleta.setText("Transfer dari Waleta");
        button_get_data_waleta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_get_data_waletaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(label_title_op_karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, 314, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(button_cancel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_proceed_insert))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(ComboBox_Kelamin, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txt_nama)
                                    .addComponent(Date_tgl_lahir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txt_no_telp)
                                    .addComponent(Date_masuk, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(ComboBox_posisi, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(ComboBox_bagian, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txt_id_pegawai, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                                    .addComponent(ComboBox_levelGaji, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txt_email)
                                    .addComponent(txt_login_username))))
                        .addContainerGap())))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(button_get_data_waleta)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label_title_op_karyawan)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_get_data_waleta)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_id_pegawai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_nama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_Kelamin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_tgl_lahir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_no_telp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_email, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_masuk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_posisi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_bagian, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_levelGaji, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_login_username, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_proceed_insert)
                    .addComponent(button_cancel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void button_proceed_insertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_proceed_insertActionPerformed
        if (label_title_op_karyawan.getText().equals("Tambah Data Karyawan Sub")) {
            insert();
        }
    }//GEN-LAST:event_button_proceed_insertActionPerformed

    private void button_cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_cancelActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_button_cancelActionPerformed

    private void button_get_data_waletaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_get_data_waletaActionPerformed
        // TODO add your handling code here:
        String filter_tgl = " AND `status` = 'IN' ";
        Browse_Karyawan dialog = new Browse_Karyawan(new javax.swing.JFrame(), true, filter_tgl);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);

        int x = Browse_Karyawan.table_list_karyawan.getSelectedRow();
        if (x != -1) {
            try {
                txt_id_pegawai.setText(Browse_Karyawan.table_list_karyawan.getValueAt(x, 0).toString());
                txt_nama.setText(Browse_Karyawan.table_list_karyawan.getValueAt(x, 1).toString());

                String query = "SELECT `id_pegawai`, `nama_pegawai`, `jenis_kelamin`, `tanggal_lahir`, `no_telp`, `email` \n"
                        + "FROM `tb_karyawan` WHERE `id_pegawai` = '" + Browse_Karyawan.table_list_karyawan.getValueAt(x, 0).toString() + "'";
                ResultSet result = Utility.db.getStatement().executeQuery(query);
                if (result.next()) {
                    ComboBox_Kelamin.setSelectedItem(result.getString("jenis_kelamin"));
                    Date_tgl_lahir.setDate(result.getDate("tanggal_lahir"));
                    txt_no_telp.setText(result.getString("no_telp"));
                    txt_email.setText(result.getString("email"));
                }
            } catch (SQLException ex) {
                Logger.getLogger(Insert_DataKaryawan_SUB.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_button_get_data_waletaActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    public static javax.swing.JComboBox<String> ComboBox_Kelamin;
    public static javax.swing.JComboBox<String> ComboBox_bagian;
    public static javax.swing.JComboBox<String> ComboBox_levelGaji;
    public static javax.swing.JComboBox<String> ComboBox_posisi;
    public static com.toedter.calendar.JDateChooser Date_masuk;
    public static com.toedter.calendar.JDateChooser Date_tgl_lahir;
    private javax.swing.JButton button_cancel;
    private javax.swing.JButton button_get_data_waleta;
    private javax.swing.JButton button_proceed_insert;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel2;
    public static javax.swing.JLabel label_title_op_karyawan;
    public static javax.swing.JTextField txt_email;
    public static javax.swing.JTextField txt_id_pegawai;
    public static javax.swing.JTextField txt_login_username;
    public static javax.swing.JTextField txt_nama;
    public static javax.swing.JTextField txt_no_telp;
    // End of variables declaration//GEN-END:variables
}
