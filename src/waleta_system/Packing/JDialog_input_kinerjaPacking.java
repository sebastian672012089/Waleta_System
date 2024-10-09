package waleta_system.Packing;

import java.awt.event.KeyEvent;
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

public class JDialog_input_kinerjaPacking extends javax.swing.JDialog {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    String no_kinerja = null;

    public JDialog_input_kinerjaPacking(java.awt.Frame parent, boolean modal, String no_grade_spk, String no_kinerja) {
        super(parent, modal);
        initComponents();
        this.setResizable(false);
        if (no_grade_spk != null) {
            LoadDataSPK(no_grade_spk);
        } else if (no_kinerja != null) {
            LoadDataKinerja(no_kinerja);
            this.no_kinerja = no_kinerja;
        }
    }

    public void LoadDataSPK(String no_grade_spk) {
        try {
            label_tanggal_hari_ini.setText("Tanggal : " + new SimpleDateFormat("dd MMMM yyyy").format(new Date()));
            sql = "SELECT `no`, `tb_spk_detail`.`kode_spk`, `tb_spk`.`buyer`, `grade_waleta`, `grade_buyer`, `tb_spk_detail`.`berat` "
                    + "FROM `tb_spk_detail` "
                    + "LEFT JOIN `tb_spk` ON `tb_spk_detail`.`kode_spk` = `tb_spk`.`kode_spk` "
                    + "WHERE `no` = '" + no_grade_spk + "' "
                    + "GROUP BY `no`";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                label_kode_spk.setText(rs.getString("kode_spk"));
                label_no_grade_spk.setText(rs.getString("no"));
                label_gram_pesanan.setText(rs.getString("berat"));
                label_grade_buyer.setText(rs.getString("grade_buyer"));
                label_grade_waleta.setText(rs.getString("grade_waleta"));
                label_buyer.setText(rs.getString("buyer"));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JDialog_input_kinerjaPacking.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void LoadDataKinerja(String no_kinerja) {
        try {
            sql = "SELECT `no_grade_spk`, `tanggal_input`, `turun_barang`, `hot_gun`, `vakum`, `scan`, \n"
                    + "`tb_spk_detail`.`no`, `tb_spk_detail`.`kode_spk`, `tb_spk`.`buyer`, `grade_waleta`, `grade_buyer`, `tb_spk_detail`.`berat`\n"
                    + "FROM `tb_spk_detail_progress` \n"
                    + "LEFT JOIN `tb_spk_detail` ON `tb_spk_detail_progress`.`no_grade_spk` = `tb_spk_detail`.`no`\n"
                    + "LEFT JOIN `tb_spk` ON `tb_spk_detail`.`kode_spk` = `tb_spk`.`kode_spk` "
                    + "WHERE `tb_spk_detail_progress`.`no` = '" + no_kinerja + "' ";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                label_kode_spk.setText(rs.getString("kode_spk"));
                label_no_grade_spk.setText(rs.getString("no"));
                label_gram_pesanan.setText(rs.getString("berat"));
                label_grade_buyer.setText(rs.getString("grade_buyer"));
                label_grade_waleta.setText(rs.getString("grade_waleta"));
                label_buyer.setText(rs.getString("buyer"));

                label_tanggal_hari_ini.setText("Tanggal : " + new SimpleDateFormat("dd MMMM yyyy").format(rs.getDate("tanggal_input")));
                txt_barang_turun.setText(rs.getString("turun_barang"));
                txt_hot_gun.setText(rs.getString("hot_gun"));
                txt_vakum.setText(rs.getString("vakum"));
                txt_scan.setText(rs.getString("scan"));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JDialog_input_kinerjaPacking.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        label_tanggal_hari_ini = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txt_barang_turun = new javax.swing.JTextField();
        label_kode_spk = new javax.swing.JLabel();
        label_buyer = new javax.swing.JLabel();
        label_gram_pesanan = new javax.swing.JLabel();
        label_grade_buyer = new javax.swing.JLabel();
        label_grade_waleta = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        txt_hot_gun = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        txt_vakum = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        txt_scan = new javax.swing.JTextField();
        label_no_grade_spk = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        button_save = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Bagian Packing");
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel3.setText("Kode SPK :");
        jLabel3.setFocusable(false);

        label_tanggal_hari_ini.setBackground(new java.awt.Color(255, 255, 255));
        label_tanggal_hari_ini.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        label_tanggal_hari_ini.setText("TANGGAL :");
        label_tanggal_hari_ini.setFocusable(false);

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("SansSerif", 1, 16)); // NOI18N
        jLabel1.setText("INPUT KINERJA PACKING");
        jLabel1.setFocusable(false);

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel4.setText("Buyer :");
        jLabel4.setFocusable(false);

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel5.setText("Gram Pesanan :");
        jLabel5.setFocusable(false);

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel6.setText("Grade  Buyer :");
        jLabel6.setFocusable(false);

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel7.setText("Grade Waleta :");
        jLabel7.setFocusable(false);

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel8.setText("Barang Turun :");
        jLabel8.setFocusable(false);

        txt_barang_turun.setEditable(false);
        txt_barang_turun.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_barang_turun.setText("0");
        txt_barang_turun.setFocusable(false);
        txt_barang_turun.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_barang_turunKeyTyped(evt);
            }
        });

        label_kode_spk.setBackground(new java.awt.Color(255, 255, 255));
        label_kode_spk.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        label_kode_spk.setText("-");
        label_kode_spk.setFocusable(false);

        label_buyer.setBackground(new java.awt.Color(255, 255, 255));
        label_buyer.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        label_buyer.setText("-");
        label_buyer.setFocusable(false);

        label_gram_pesanan.setBackground(new java.awt.Color(255, 255, 255));
        label_gram_pesanan.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        label_gram_pesanan.setText("-");
        label_gram_pesanan.setFocusable(false);

        label_grade_buyer.setBackground(new java.awt.Color(255, 255, 255));
        label_grade_buyer.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        label_grade_buyer.setText("-");
        label_grade_buyer.setFocusable(false);

        label_grade_waleta.setBackground(new java.awt.Color(255, 255, 255));
        label_grade_waleta.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        label_grade_waleta.setText("-");
        label_grade_waleta.setFocusable(false);

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel14.setText("Hot Gun :");
        jLabel14.setFocusable(false);

        txt_hot_gun.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_hot_gun.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_hot_gunKeyTyped(evt);
            }
        });

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel15.setText("Vakum :");
        jLabel15.setFocusable(false);

        txt_vakum.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_vakum.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_vakumKeyTyped(evt);
            }
        });

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel16.setText("Scan :");
        jLabel16.setFocusable(false);

        txt_scan.setEditable(false);
        txt_scan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_scan.setText("0");
        txt_scan.setFocusable(false);
        txt_scan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_scanKeyTyped(evt);
            }
        });

        label_no_grade_spk.setBackground(new java.awt.Color(255, 255, 255));
        label_no_grade_spk.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        label_no_grade_spk.setText("-");
        label_no_grade_spk.setFocusable(false);

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel9.setText("No Grade SPK :");
        jLabel9.setFocusable(false);

        button_save.setBackground(new java.awt.Color(255, 255, 255));
        button_save.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        button_save.setText("SAVE");
        button_save.setFocusable(false);
        button_save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_saveActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_save))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(label_no_grade_spk, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(label_kode_spk, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(label_gram_pesanan, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(label_grade_buyer, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(label_grade_waleta, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txt_barang_turun, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(txt_scan, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(txt_vakum, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(txt_hot_gun, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addComponent(label_buyer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(label_tanggal_hari_ini)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(button_save))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(label_tanggal_hari_ini)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_barang_turun, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_kode_spk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_hot_gun, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_no_grade_spk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_gram_pesanan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_vakum, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_grade_buyer, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_scan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_grade_waleta, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_buyer, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
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
            // TODO add your handling code here:
            float turun_barang = Float.valueOf(txt_barang_turun.getText());
            float hot_gun = Float.valueOf(txt_hot_gun.getText());
            float vakum = Float.valueOf(txt_vakum.getText());
            float scan = Float.valueOf(txt_scan.getText());
            if (no_kinerja == null) {
                sql = "INSERT INTO `tb_spk_detail_progress`(`no_grade_spk`, `tanggal_input`, `turun_barang`, `hot_gun`, `vakum`, `scan`) "
                        + "VALUES ('" + label_no_grade_spk.getText() + "', CURRENT_DATE," + turun_barang + "," + hot_gun + "," + vakum + "," + scan + ")";
            } else {
                sql = "UPDATE `tb_spk_detail_progress` SET "
                        + "`turun_barang`=" + turun_barang + ","
                        + "`hot_gun`=" + hot_gun + ","
                        + "`vakum`=" + vakum + ","
                        + "`scan`=" + scan + " "
                        + "WHERE `no`=" + no_kinerja + "";
            }

            Utility.db.getConnection().createStatement();
            Utility.db.getStatement().executeUpdate(sql);
            JOptionPane.showMessageDialog(this, "Data disimpan !");
            this.dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JDialog_input_kinerjaPacking.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_saveActionPerformed

    private void txt_barang_turunKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_barang_turunKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_barang_turunKeyTyped

    private void txt_hot_gunKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_hot_gunKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_hot_gunKeyTyped

    private void txt_vakumKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_vakumKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_vakumKeyTyped

    private void txt_scanKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_scanKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_scanKeyTyped

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton button_save;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel label_buyer;
    private javax.swing.JLabel label_grade_buyer;
    private javax.swing.JLabel label_grade_waleta;
    private javax.swing.JLabel label_gram_pesanan;
    private javax.swing.JLabel label_kode_spk;
    private javax.swing.JLabel label_no_grade_spk;
    private javax.swing.JLabel label_tanggal_hari_ini;
    private javax.swing.JTextField txt_barang_turun;
    private javax.swing.JTextField txt_hot_gun;
    private javax.swing.JTextField txt_scan;
    private javax.swing.JTextField txt_vakum;
    // End of variables declaration//GEN-END:variables
}
