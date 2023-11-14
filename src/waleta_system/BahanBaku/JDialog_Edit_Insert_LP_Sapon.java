package waleta_system.BahanBaku;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import waleta_system.Class.Utility;

public class JDialog_Edit_Insert_LP_Sapon extends javax.swing.JDialog {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    String no_lp_sesekan_lama = null;

    public JDialog_Edit_Insert_LP_Sapon(java.awt.Frame parent, boolean modal, String no_lp_sesekan) {
        super(parent, modal);
        try {
            initComponents();
            this.setResizable(false);
            Utility.db_sub.connect();
            sql = "SELECT `kode_sub` FROM `tb_sub_waleta` WHERE `tanggal_tutup` IS NULL";
            rs = Utility.db_sub.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_SUB_tujuan.addItem(rs.getString("kode_sub"));
            }
            sql = "SELECT `bulu_upah` FROM `tb_tarif_cabut` WHERE 1";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_bulu_upah.addItem(rs.getString("bulu_upah"));
            }

            if (no_lp_sesekan != null) {
                this.no_lp_sesekan_lama = no_lp_sesekan;
                load_data_edit(no_lp_sesekan);
                ComboBox_SUB_tujuan.setEnabled(false);
            }
        } catch (Exception ex) {
            Logger.getLogger(JDialog_Edit_Insert_LP_Sapon.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void load_data_edit(String no_lp_sesekan) {
        try {
            sql = "SELECT `no_lp_sapon`, `sub`, `tanggal_lp`, `kode_grade`, `tb_laporan_produksi_sapon`.`bulu_upah`, `memo`, `keping`, `gram_sapon`, `berat_setelah_cuci`, `nilai_lp`, `tb_tarif_cabut`.`tarif_sapon_sub` "
                    + "FROM `tb_laporan_produksi_sapon` "
                    + "LEFT JOIN `tb_tarif_cabut` ON `tb_laporan_produksi_sapon`.`bulu_upah` = `tb_tarif_cabut`.`bulu_upah` "
                    + "WHERE `no_lp_sapon` = '" + no_lp_sesekan + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                ComboBox_SUB_tujuan.setSelectedItem(rs.getString("sub"));
                ComboBox_bulu_upah.setSelectedItem(rs.getString("bulu_upah"));
                txt_upah_per_gram.setText(rs.getString("tarif_sapon_sub"));
                Date_LP.setDate(rs.getDate("tanggal_lp"));
                txt_memo_lp.setText(rs.getString("memo"));
                txt_grade_lp_sesekan.setText(rs.getString("kode_grade"));
                txt_gram_sapon.setText(rs.getString("gram_sapon"));
                txt_nilai_lp.setText(rs.getString("nilai_lp"));
                txt_gram_setelah_cuci.setText(rs.getString("berat_setelah_cuci"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(JDialog_Edit_Insert_LP_Sapon.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void hitung_nilai_lp() {
        try {
            float nilai_lp = 0;
            float upah_per_gram = Float.valueOf(txt_upah_per_gram.getText());
            int gram = Integer.valueOf(txt_gram_sapon.getText());

            nilai_lp = upah_per_gram * gram;
            txt_nilai_lp.setText(decimalFormat.format(nilai_lp));
        } catch (Exception e) {
//            Logger.getLogger(JDialog_Edit_Insert_LP_Sapon.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void lp_baru() {
        try {
            Utility.db_sub.connect();
            Utility.db.getConnection().setAutoCommit(false);
            Utility.db_sub.getConnection().setAutoCommit(false);
            String no_lp_sapon = "";
            sql = "SELECT MAX(SUBSTRING(`no_lp_sapon`, 14, 5)+0) AS 'last_num' FROM `tb_laporan_produksi_sapon` "
                    + "WHERE `no_lp_sapon` LIKE 'SP.%' "
                    + "AND YEAR(`tanggal_lp`) = YEAR('" + dateFormat.format(Date_LP.getDate()) + "') "
                    + "AND `sub` = '" + ComboBox_SUB_tujuan.getSelectedItem().toString() + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                int last_num = rs.getInt("last_num");
                no_lp_sapon = "SP." + ComboBox_SUB_tujuan.getSelectedItem().toString() + "-" + new SimpleDateFormat("YYMM").format(Date_LP.getDate()) + String.format("%05d", last_num + 1);
            }
            float gram_sapon = Float.valueOf(txt_gram_sapon.getText());
            float berat_setelah_cuci = Float.valueOf(txt_gram_setelah_cuci.getText());
            float nilai_lp = 0;
            sql = "SELECT `tarif_sapon_sub` FROM `tb_tarif_cabut` WHERE `bulu_upah` = '" + ComboBox_bulu_upah.getSelectedItem().toString() + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                nilai_lp = gram_sapon * rs.getFloat("tarif_sapon_sub");
            }

            String insert_lp_baru = "INSERT INTO `tb_laporan_produksi_sapon`(`no_lp_sapon`, `sub`, `tanggal_lp`, `kode_grade`, `bulu_upah`, `memo`, `keping`, `gram_sapon`, `berat_setelah_cuci`, `nilai_lp`) "
                    + "VALUES ('" + no_lp_sapon + "','" + ComboBox_SUB_tujuan.getSelectedItem().toString() + "','" + dateFormat.format(Date_LP.getDate()) + "','" + txt_grade_lp_sesekan.getText() + "','" + ComboBox_bulu_upah.getSelectedItem().toString() + "','" + txt_memo_lp.getText() + "',0,'" + gram_sapon + "','" + berat_setelah_cuci + "','" + nilai_lp + "')";
            Utility.db.getConnection().createStatement();
            Utility.db.getStatement().executeUpdate(insert_lp_baru);
            Utility.db_sub.getConnection().createStatement();
            Utility.db_sub.getStatement().executeUpdate(insert_lp_baru);
            Utility.db.getConnection().commit();
            Utility.db_sub.getConnection().commit();
            JOptionPane.showMessageDialog(this, "LP SAPON SUB " + ComboBox_SUB_tujuan.getSelectedItem() + " telah berhasil dibuat!");
            this.dispose();
        } catch (Exception e) {
            try {
                Utility.db.getConnection().rollback();
                Utility.db_sub.getConnection().rollback();
            } catch (SQLException ex) {
                Logger.getLogger(JDialog_Edit_Insert_LP_Sapon.class.getName()).log(Level.SEVERE, null, ex);
            }
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JDialog_Edit_Insert_LP_Sapon.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            try {
                Utility.db.getConnection().setAutoCommit(true);
                Utility.db_sub.getConnection().setAutoCommit(true);
            } catch (SQLException ex) {
                Logger.getLogger(JDialog_Edit_Insert_LP_Sapon.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void lp_edit() {
        try {
            Utility.db_sub.connect();
            Utility.db.getConnection().setAutoCommit(false);
            Utility.db_sub.getConnection().setAutoCommit(false);
            float gram_sapon = Float.valueOf(txt_gram_sapon.getText());
            float berat_setelah_cuci = Float.valueOf(txt_gram_setelah_cuci.getText());
            float nilai_lp = 0;
            sql = "SELECT `tarif_sapon_sub` FROM `tb_tarif_cabut` WHERE `bulu_upah` = '" + ComboBox_bulu_upah.getSelectedItem().toString() + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                nilai_lp = gram_sapon * rs.getFloat("tarif_sapon_sub");
            }
            String query_update = "UPDATE `tb_laporan_produksi_sapon` SET "
                    + "`tanggal_lp`='" + dateFormat.format(Date_LP.getDate()) + "',"
                    + "`kode_grade`='" + txt_grade_lp_sesekan.getText() + "',"
                    + "`bulu_upah`='" + ComboBox_bulu_upah.getSelectedItem().toString() + "',"
                    + "`memo`='" + txt_memo_lp.getText() + "',"
                    + "`keping`=0,"
                    + "`gram_sapon`='" + gram_sapon + "',"
                    + "`berat_setelah_cuci`='" + berat_setelah_cuci + "',"
                    + "`nilai_lp`='" + nilai_lp + "' "
                    + "WHERE `no_lp_sapon`='" + no_lp_sesekan_lama + "'";
            Utility.db.getConnection().createStatement();
            Utility.db.getStatement().executeUpdate(query_update);
            Utility.db_sub.getConnection().createStatement();
            Utility.db_sub.getStatement().executeUpdate(query_update);
            Utility.db.getConnection().commit();
            Utility.db_sub.getConnection().commit();
            JOptionPane.showMessageDialog(this, "LP Sapon SUB " + ComboBox_SUB_tujuan.getSelectedItem() + " telah berhasil diedit!");
            this.dispose();
        } catch (Exception e) {
            try {
                Utility.db.getConnection().rollback();
                Utility.db_sub.getConnection().rollback();
            } catch (SQLException ex) {
                Logger.getLogger(JDialog_Edit_Insert_LP_Sapon.class.getName()).log(Level.SEVERE, null, ex);
            }
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JDialog_Edit_Insert_LP_Sapon.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            try {
                Utility.db.getConnection().setAutoCommit(true);
                Utility.db_sub.getConnection().setAutoCommit(true);
            } catch (SQLException ex) {
                Logger.getLogger(JDialog_Edit_Insert_LP_Sapon.class.getName()).log(Level.SEVERE, null, ex);
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
        button_save = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        ComboBox_SUB_tujuan = new javax.swing.JComboBox<>();
        jLabel19 = new javax.swing.JLabel();
        txt_gram_sapon = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        txt_grade_lp_sesekan = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        ComboBox_bulu_upah = new javax.swing.JComboBox<>();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        txt_gram_setelah_cuci = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txt_memo_lp = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        txt_nilai_lp = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        txt_upah_per_gram = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        Date_LP = new com.toedter.calendar.JDateChooser();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Pengeluaran Bahan Jadi");
        setBackground(new java.awt.Color(255, 255, 255));
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText(new SimpleDateFormat("dd MMMM yyyy").format(date));

        button_save.setBackground(new java.awt.Color(255, 255, 255));
        button_save.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_save.setText("Save");
        button_save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_saveActionPerformed(evt);
            }
        });

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel5.setText("Buat LP Sapon");

        jLabel1.setBackground(java.awt.Color.white);
        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("SUB Tujuan :");

        ComboBox_SUB_tujuan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel19.setBackground(new java.awt.Color(255, 255, 255));
        jLabel19.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel19.setText("Gram setelah Cuci :");

        txt_gram_sapon.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_gram_sapon.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_gram_saponFocusLost(evt);
            }
        });

        jLabel20.setBackground(new java.awt.Color(255, 255, 255));
        jLabel20.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel20.setText("Grade :");

        txt_grade_lp_sesekan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_grade_lp_sesekan.setText("-");

        jLabel3.setBackground(java.awt.Color.white);
        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("Bulu Upah :");

        ComboBox_bulu_upah.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_bulu_upah.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                ComboBox_bulu_upahFocusLost(evt);
            }
        });

        jLabel21.setBackground(new java.awt.Color(255, 255, 255));
        jLabel21.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        jLabel21.setText("Nilai LP = berat bersih x tarif sesuai bulu upah (data di master grade baku)");

        jLabel22.setBackground(new java.awt.Color(255, 255, 255));
        jLabel22.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel22.setText("Berat Sapon (Gram) :");

        txt_gram_setelah_cuci.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel8.setBackground(java.awt.Color.white);
        jLabel8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel8.setText("Memo LP :");

        txt_memo_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_memo_lp.setText("-");

        jLabel23.setBackground(new java.awt.Color(255, 255, 255));
        jLabel23.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel23.setText("Nilai LP :");

        txt_nilai_lp.setEditable(false);
        txt_nilai_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel25.setBackground(new java.awt.Color(255, 255, 255));
        jLabel25.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel25.setText("Upah / Gram :");

        txt_upah_per_gram.setEditable(false);
        txt_upah_per_gram.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel4.setBackground(java.awt.Color.white);
        jLabel4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel4.setText("Tanggal LP :");

        Date_LP.setBackground(new java.awt.Color(255, 255, 255));
        Date_LP.setDate(new Date());
        Date_LP.setDateFormatString("dd MMM yyyy");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(txt_gram_setelah_cuci, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                                        .addComponent(txt_nilai_lp, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                                        .addComponent(txt_gram_sapon, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                                        .addComponent(txt_memo_lp, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                                        .addComponent(txt_grade_lp_sesekan, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                                        .addComponent(ComboBox_SUB_tujuan, 0, 250, Short.MAX_VALUE)
                                        .addComponent(Date_LP, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGap(10, 10, 10)
                                    .addComponent(jLabel5)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel2))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGap(134, 134, 134)
                                    .addComponent(ComboBox_bulu_upah, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jLabel25)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(txt_upah_per_gram, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel21)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(button_save)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_SUB_tujuan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(ComboBox_bulu_upah, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                .addComponent(txt_upah_per_gram, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(txt_grade_lp_sesekan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_memo_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_gram_sapon, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_nilai_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(txt_gram_setelah_cuci, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_save, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(Date_LP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
        boolean cek = true;
        if (txt_gram_setelah_cuci.getText() == null || txt_gram_setelah_cuci.getText().equals("") || txt_gram_setelah_cuci.getText().equals("0")) {
            JOptionPane.showMessageDialog(this, "Silahkan masukkan jumlah gram setelah cuci !");
            cek = false;
        } else if (txt_gram_sapon.getText() == null || txt_gram_sapon.getText().equals("") || txt_gram_sapon.getText().equals("0")) {
            JOptionPane.showMessageDialog(this, "Silahkan masukkan jumlah gram sesekan bersih !");
            cek = false;
        } else if (txt_grade_lp_sesekan.getText() == null || txt_grade_lp_sesekan.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Silahkan masukkan jenis grade yang akan diproses!");
            cek = false;
        }
        if (cek) {
            if (no_lp_sesekan_lama == null) {
                lp_baru();
            } else {
                lp_edit();
            }
        }


    }//GEN-LAST:event_button_saveActionPerformed

    private void txt_gram_saponFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_gram_saponFocusLost
        // TODO add your handling code here:
        hitung_nilai_lp();
    }//GEN-LAST:event_txt_gram_saponFocusLost

    private void ComboBox_bulu_upahFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ComboBox_bulu_upahFocusLost
        // TODO add your handling code here:
        try {
            if (ComboBox_bulu_upah.getSelectedIndex() > -1 && ComboBox_bulu_upah.getSelectedItem() != null) {
                sql = "SELECT `tarif_sapon_sub` FROM `tb_tarif_cabut` WHERE `bulu_upah` =  '" + ComboBox_bulu_upah.getSelectedItem().toString() + "'";
                rs = Utility.db.getStatement().executeQuery(sql);
                if (rs.next()) {
                    txt_upah_per_gram.setText(rs.getString("tarif_sapon_sub"));
                }
                hitung_nilai_lp();
            }
        } catch (Exception e) {
            Logger.getLogger(JDialog_Edit_Insert_LP_Sapon.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_ComboBox_bulu_upahFocusLost


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_SUB_tujuan;
    private javax.swing.JComboBox<String> ComboBox_bulu_upah;
    private com.toedter.calendar.JDateChooser Date_LP;
    private javax.swing.JButton button_save;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField txt_grade_lp_sesekan;
    private javax.swing.JTextField txt_gram_sapon;
    private javax.swing.JTextField txt_gram_setelah_cuci;
    private javax.swing.JTextField txt_memo_lp;
    private javax.swing.JTextField txt_nilai_lp;
    private javax.swing.JTextField txt_upah_per_gram;
    // End of variables declaration//GEN-END:variables
}
