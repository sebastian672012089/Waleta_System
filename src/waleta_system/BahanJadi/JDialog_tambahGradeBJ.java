package waleta_system.BahanJadi;

import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableCellRenderer;
import waleta_system.Class.Utility;

public class JDialog_tambahGradeBJ extends javax.swing.JDialog {

    String sql = null;
    ResultSet rs;
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    String status = null;

    public JDialog_tambahGradeBJ(java.awt.Frame parent, boolean modal, String status, String kode, String grade, String nama, String bentuk, String kategori1, String kategoriJual, String upah_reproses, String kategori_subbagian) {
        super(parent, modal);
        try {
            initComponents();
            
            this.status = status;
            ComboBox_kategoriJual.removeAllItems();
            sql = "SELECT DISTINCT(`bentuk_grade`) AS 'item' FROM `tb_grade_bahan_jadi` WHERE 1";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_bentukGrade.addItem(rs.getString("item"));
            }
            sql = "SELECT DISTINCT(`kategori_jual`) AS 'item' FROM `tb_grade_bahan_jadi` WHERE 1";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_kategoriJual.addItem(rs.getString("item"));
            }
            ComboBox_kategori1.removeAllItems();
            sql = "SELECT DISTINCT(`Kategori1`) AS 'item' FROM `tb_grade_bahan_jadi` WHERE `Kategori1` IS NOT NULL";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_kategori1.addItem(rs.getString("item"));
            }
            
            if (status.equals("insert")) {
                getNewCode_GNS();
                txt_grade.requestFocus();
            } else if (status.equals("edit")) {
                label_kode.setText(kode);
                txt_grade.setText(grade);
                txt_namaGrade.setText(nama);
                ComboBox_bentukGrade.setSelectedItem(bentuk);
                ComboBox_kategori1.setSelectedItem(kategori1);
                ComboBox_kategoriJual.setSelectedItem(kategoriJual);
                txt_upah_reproses.setText(upah_reproses);
                txt_kategori_subbagian.setText(kategori_subbagian);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JDialog_tambahGradeBJ.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void getNewCode_GNS() {
        try {
            int last_number = 0;
            sql = "SELECT MAX(`kode`)+1 AS 'last_code' FROM `tb_grade_bahan_jadi` WHERE `kode_grade` LIKE 'GNS%'";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                    last_number = rs.getInt("last_code");
            }
            label_kode.setText(String.format("%03d", last_number));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_BoxBahanJadi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void getNewCode_NS() {
        try {
            String new_code = null;
            sql = "SELECT MAX(`kode`)+1 AS 'last_code' FROM `tb_grade_bahan_jadi` WHERE `kode_grade` LIKE 'Non NS%'";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                new_code = rs.getString("kode");
            }
            label_kode.setText(new_code);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_BoxBahanJadi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void update() {
        try {
            String update = "UPDATE `tb_grade_bahan_jadi` SET "
                    + "`kode_grade`='" + label_grade.getText() + txt_grade.getText() + "',"
                    + "`nama_grade`='" + label_nama.getText() + txt_namaGrade.getText() + "',"
                    + "`bentuk_grade`='" + ComboBox_bentukGrade.getSelectedItem().toString() + "',"
                    + "`Kategori1`='" + ComboBox_kategori1.getSelectedItem().toString() + "',"
                    + "`kategori_jual`='" + ComboBox_kategoriJual.getSelectedItem().toString() + "',"
                    + "`upah_reproses`='" + txt_upah_reproses.getText() + "',"
                    + "`kategori_subbagian_gradebarangjadi`='" + txt_kategori_subbagian.getText() + "'"
                    + "WHERE `kode`='" + label_kode.getText() + "'";
            Utility.db.getConnection().createStatement();
            Utility.db.getStatement().executeUpdate(update);
            JOptionPane.showMessageDialog(this, "Perubahan data berhasil disimpan !");
            this.dispose();
        } catch (SQLException | NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JDialog_tambahGradeBJ.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void insert() {
        try {
            String insert = "INSERT INTO `tb_grade_bahan_jadi`(`kode`, `kode_grade`, `nama_grade`, `bentuk_grade`, `Kategori1`, `kategori_jual`, `upah_reproses`, `kategori_subbagian_gradebarangjadi`, `harga`) "
                    + "VALUES ("
                    + "'" + label_kode.getText() + "',"
                    + "'" + label_grade.getText() + txt_grade.getText() + "',"
                    + "'" + label_nama.getText() + txt_namaGrade.getText() + "', "
                    + "'" + ComboBox_bentukGrade.getSelectedItem().toString() + "', "
                    + "'" + ComboBox_kategori1.getSelectedItem().toString() + "', "
                    + "'" + ComboBox_kategoriJual.getSelectedItem().toString() + "',"
                    + "'" + txt_upah_reproses.getText() + "', "
                    + "'" + txt_kategori_subbagian.getText() + "', "
                    + "'" + txt_harga_jual.getText() + "')";
            Utility.db.getConnection().createStatement();
            Utility.db.getStatement().executeUpdate(insert);
            JOptionPane.showMessageDialog(this, "Data berhasil ditambahkan!");
            this.dispose();
        } catch (SQLException | NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JDialog_tambahGradeBJ.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jComboBox1 = new javax.swing.JComboBox<>();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        label_kode = new javax.swing.JLabel();
        txt_grade = new javax.swing.JTextField();
        txt_namaGrade = new javax.swing.JTextField();
        button_ok = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        ComboBox_bentukGrade = new javax.swing.JComboBox<>();
        ComboBox_jenis_grade = new javax.swing.JComboBox<>();
        label_grade = new javax.swing.JLabel();
        label_nama = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        ComboBox_kategoriJual = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        txt_upah_reproses = new javax.swing.JTextField();
        label_grade1 = new javax.swing.JLabel();
        label_grade2 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        ComboBox_kategori1 = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        txt_kategori_subbagian = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txt_harga_jual = new javax.swing.JTextField();
        label_grade3 = new javax.swing.JLabel();
        label_grade4 = new javax.swing.JLabel();

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Tambah Data Grade Bahan Jadi");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setText("KODE :");

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel3.setText("Grade :");

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel4.setText("Nama Grade :");

        label_kode.setBackground(new java.awt.Color(255, 255, 255));
        label_kode.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_kode.setText("000");

        txt_grade.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        txt_namaGrade.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        button_ok.setText("Save");
        button_ok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_okActionPerformed(evt);
            }
        });

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel5.setText("Bentuk Grade :");

        ComboBox_bentukGrade.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        ComboBox_jenis_grade.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_jenis_grade.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "GNS", "Non NS" }));
        ComboBox_jenis_grade.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ComboBox_jenis_gradeItemStateChanged(evt);
            }
        });

        label_grade.setBackground(new java.awt.Color(255, 255, 255));
        label_grade.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_grade.setText("GNS ");

        label_nama.setBackground(new java.awt.Color(255, 255, 255));
        label_nama.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_nama.setText("General Nitrit Safe ");

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel6.setText("Kategori :");

        ComboBox_kategoriJual.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel7.setText("Upah Reproses :");

        txt_upah_reproses.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_upah_reproses.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_upah_reprosesKeyTyped(evt);
            }
        });

        label_grade1.setBackground(new java.awt.Color(255, 255, 255));
        label_grade1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_grade1.setText("/ Gram");

        label_grade2.setBackground(new java.awt.Color(255, 255, 255));
        label_grade2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_grade2.setText("Rp.");

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel8.setText("Kategori Jual :");

        ComboBox_kategori1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel9.setText("Kategori Sub :");

        txt_kategori_subbagian.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel10.setText("Harga Jual :");

        txt_harga_jual.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_harga_jual.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_harga_jualKeyTyped(evt);
            }
        });

        label_grade3.setBackground(new java.awt.Color(255, 255, 255));
        label_grade3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_grade3.setText("/ Kg");

        label_grade4.setBackground(new java.awt.Color(255, 255, 255));
        label_grade4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_grade4.setText("CNY");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(label_kode)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 167, Short.MAX_VALUE)
                        .addComponent(ComboBox_jenis_grade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(label_grade)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_grade))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(label_nama)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_namaGrade))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(label_grade2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_upah_reproses)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_grade1))
                    .addComponent(txt_kategori_subbagian)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ComboBox_bentukGrade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ComboBox_kategoriJual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ComboBox_kategori1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(label_grade4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_harga_jual)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_grade3)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(308, Short.MAX_VALUE)
                .addComponent(button_ok)
                .addGap(10, 10, 10))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_kode, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(ComboBox_jenis_grade, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_grade, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txt_grade, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_nama, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(ComboBox_bentukGrade, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ComboBox_kategori1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ComboBox_kategoriJual, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_upah_reproses, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_grade1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_grade2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_kategori_subbagian, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(txt_namaGrade, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_harga_jual, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_grade3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_grade4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_ok, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
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

    private void button_okActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_okActionPerformed
        if (status.equals("insert")) {
            insert();
        } else if (status.equals("edit")) {
            update();
        }
    }//GEN-LAST:event_button_okActionPerformed

    private void ComboBox_jenis_gradeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ComboBox_jenis_gradeItemStateChanged
        // TODO add your handling code here:
        if (ComboBox_jenis_grade.getSelectedIndex() == 0) {
            if ("insert".equals(status)) {
                getNewCode_GNS();
            }
            label_grade.setText("GNS ");
            label_nama.setText("General Nitrit Safe ");
        } else if (ComboBox_jenis_grade.getSelectedIndex() == 1) {
            if ("insert".equals(status)) {
                getNewCode_NS();
            }
            label_grade.setText("Non NS ");
            label_nama.setText("Non Nitrit Safe ");
        }
    }//GEN-LAST:event_ComboBox_jenis_gradeItemStateChanged

    private void txt_upah_reprosesKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_upah_reprosesKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_upah_reprosesKeyTyped

    private void txt_harga_jualKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_harga_jualKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_harga_jualKeyTyped


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_bentukGrade;
    private javax.swing.JComboBox<String> ComboBox_jenis_grade;
    private javax.swing.JComboBox<String> ComboBox_kategori1;
    private javax.swing.JComboBox<String> ComboBox_kategoriJual;
    private javax.swing.JButton button_ok;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel label_grade;
    private javax.swing.JLabel label_grade1;
    private javax.swing.JLabel label_grade2;
    private javax.swing.JLabel label_grade3;
    private javax.swing.JLabel label_grade4;
    private javax.swing.JLabel label_kode;
    private javax.swing.JLabel label_nama;
    private javax.swing.JTextField txt_grade;
    private javax.swing.JTextField txt_harga_jual;
    private javax.swing.JTextField txt_kategori_subbagian;
    private javax.swing.JTextField txt_namaGrade;
    private javax.swing.JTextField txt_upah_reproses;
    // End of variables declaration//GEN-END:variables
}
