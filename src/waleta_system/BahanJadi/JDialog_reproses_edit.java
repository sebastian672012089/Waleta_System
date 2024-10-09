package waleta_system.BahanJadi;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import waleta_system.BahanBaku.JDialog_otorisasi_grading;
import waleta_system.Class.Utility;
import waleta_system.Panel_produksi.JDialog_Choose_LPkaki;

public class JDialog_reproses_edit extends javax.swing.JDialog {

    
    String sql = null;
    ResultSet rs;
    Date date = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    String no_box, no_reproses;
    float gram_awal = 0;
    int keping_awal = 0;

    public JDialog_reproses_edit(java.awt.Frame parent, boolean modal, String no_box, String no_reproses) {
        super(parent, modal);
        try {
            this.setResizable(false);
            
            
            initComponents();
            Utility.db_sub.connect();
            sql = "SELECT `kode_sub` FROM `tb_sub_waleta` WHERE `tanggal_tutup` IS NULL";
            rs = Utility.db_sub.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_bagian.addItem(rs.getString("kode_sub"));
            }
            this.no_box = no_box;
            this.no_reproses = no_reproses;
            label_no_box.setText(no_box);
            loadData();
        } catch (Exception ex) {
            Logger.getLogger(JDialog_reproses_edit.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void loadData() {
        try {
            sql = "SELECT `tanggal_proses`, `keping`, `gram`, `bagian`, `tgl_selesai`, `kpg_akhir`, `gram_akhir`, "
                    + "`no_lp_suwir`, `gram_kaki`, `no_lp_suwir2`, `gram_kaki2`, `rend_bersih`, `ront_bersih`, `ront_kuning`, `ront_kotor`, `hancuran`, `bonggol`, `serabut`, `status` "
                    + "FROM `tb_reproses` WHERE `no_reproses` = '" + no_reproses + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                keping_awal = rs.getInt("keping");
                gram_awal = rs.getFloat("gram");
                ComboBox_bagian.setSelectedItem(rs.getString("bagian"));
                Date_reproses.setDate(rs.getDate("tanggal_proses"));
                Date_selesai.setDate(rs.getDate("tgl_selesai"));
                txt_keping1.setText(rs.getString("keping"));
                txt_gram1.setText(rs.getString("gram"));
                txt_keping2.setText(rs.getString("kpg_akhir"));
                txt_gram2.setText(rs.getString("gram_akhir"));
                txt_lp_susur_perut1.setText(rs.getString("no_lp_suwir"));
                txt_gram_susur_perut1.setText(rs.getString("gram_kaki"));
                txt_lp_susur_perut2.setText(rs.getString("no_lp_suwir2"));
                txt_gram_susur_perut2.setText(rs.getString("gram_kaki2"));
                txt_bersih.setText(rs.getString("rend_bersih"));
                txt_ront_bersih.setText(rs.getString("ront_bersih"));
                txt_ront_kng.setText(rs.getString("ront_kuning"));
                txt_ront_ktr.setText(rs.getString("ront_kotor"));
                txt_hancuran.setText(rs.getString("hancuran"));
                txt_bonggol.setText(rs.getString("bonggol"));
                txt_serabut.setText(rs.getString("serabut"));
            }
        } catch (SQLException e) {
            Logger.getLogger(JDialog_reproses_edit.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void count_sh() {
        float bersih = 0, ront_bersih = 0, ront_kuning = 0, ront_kotor = 0, hancuran = 0, bonggol = 0, serabut = 0, sh = 0;
        float kaki1 = 0, kaki2 = 0, berat_akhir = 0;
        try {
            kaki1 = Integer.valueOf(txt_gram_susur_perut1.getText());
        } catch (NumberFormatException e) {
            kaki1 = 0;
        }
        try {
            kaki2 = Integer.valueOf(txt_gram_susur_perut2.getText());
        } catch (NumberFormatException e) {
            kaki2 = 0;
        }
        try {
            bersih = Integer.valueOf(txt_bersih.getText());
        } catch (NumberFormatException e) {
            bersih = 0;
        }
        try {
            ront_bersih = Integer.valueOf(txt_ront_bersih.getText());
        } catch (NumberFormatException e) {
            ront_bersih = 0;
        }
        try {
            ront_kuning = Integer.valueOf(txt_ront_kng.getText());
        } catch (NumberFormatException e) {
            ront_kuning = 0;
        }
        try {
            ront_kotor = Integer.valueOf(txt_ront_ktr.getText());
        } catch (NumberFormatException e) {
            ront_kotor = 0;
        }
        try {
            hancuran = Integer.valueOf(txt_hancuran.getText());
        } catch (NumberFormatException e) {
            hancuran = 0;
        }
        try {
            bonggol = Integer.valueOf(txt_bonggol.getText());
        } catch (NumberFormatException e) {
            bonggol = 0;
        }
        try {
            serabut = Integer.valueOf(txt_serabut.getText());
        } catch (NumberFormatException e) {
            serabut = 0;
        }

        berat_akhir = bersih + ront_bersih + ront_kuning + ront_kotor + hancuran + bonggol + serabut;
        sh = gram_awal - ((bersih - (kaki1 + kaki2)) + ront_bersih + ront_kuning + ront_kotor + hancuran + bonggol + serabut);
        txt_gram2.setText(Float.toString(berat_akhir));
        txt_sh.setText(Float.toString(sh));
    }

    private void update() {
        try {
            Utility.db.getConnection().setAutoCommit(false);
            String update = "UPDATE `tb_box_bahan_jadi` SET `keping`='" + txt_keping2.getText() + "',`berat`='" + txt_gram2.getText() + "' "
                    + "WHERE `no_box` = '" + no_box + "'";
            Utility.db.getConnection().createStatement();
            Utility.db.getStatement().executeUpdate(update);

            String kaki1 = "`no_lp_suwir`='" + txt_lp_susur_perut1.getText() + "', `gram_kaki`='" + txt_gram_susur_perut1.getText() + "',";
            if (txt_lp_susur_perut1.getText().equals("") || txt_lp_susur_perut1.getText() == null) {
                kaki1 = "`no_lp_suwir`=NULL, `gram_kaki`=0,";
            }
            String kaki2 = "`no_lp_suwir2`='" + txt_lp_susur_perut2.getText() + "', `gram_kaki2`='" + txt_gram_susur_perut2.getText() + "',";
            if (txt_lp_susur_perut2.getText().equals("") || txt_lp_susur_perut2.getText() == null) {
                kaki2 = "`no_lp_suwir2`=NULL, `gram_kaki2`=0,";
            }
            String reproses = "UPDATE `tb_reproses` SET "
                    + "`bagian`='" + ComboBox_bagian.getSelectedItem().toString() + "',"
                    + "`tgl_selesai`='" + dateFormat.format(Date_reproses.getDate()) + "',"
                    + "`tgl_selesai`='" + dateFormat.format(Date_selesai.getDate()) + "',"
                    + "`kpg_akhir`='" + txt_keping2.getText() + "',"
                    + "`gram_akhir`='" + txt_gram2.getText() + "',"
                    + kaki1
                    + kaki2
                    + "`rend_bersih`='" + txt_bersih.getText() + "',"
                    + "`ront_bersih`='" + txt_ront_bersih.getText() + "',"
                    + "`ront_kuning`='" + txt_ront_kng.getText() + "',"
                    + "`ront_kotor`='" + txt_ront_ktr.getText() + "',"
                    + "`hancuran`='" + txt_hancuran.getText() + "',"
                    + "`bonggol`='" + txt_bonggol.getText() + "',"
                    + "`serabut`='" + txt_serabut.getText() + "',"
                    + "`status`='FINISHED' "
                    + "WHERE `no_reproses` = '" + no_reproses + "'";

            Utility.db.getConnection().createStatement();
            Utility.db.getStatement().executeUpdate(reproses);

            Utility.db.getConnection().commit();
            JOptionPane.showMessageDialog(this, "Data Berhasil diperbarui !");
        } catch (SQLException e) {
            try {
                Utility.db.getConnection().rollback();
            } catch (SQLException ex) {
                Logger.getLogger(JDialog_reProcess.class.getName()).log(Level.SEVERE, null, ex);
            }
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(JDialog_reProcess.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            this.dispose();
            try {
                Utility.db.getConnection().setAutoCommit(true);
            } catch (SQLException ex) {
                Logger.getLogger(JDialog_reProcess.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txt_keping2 = new javax.swing.JTextField();
        txt_gram2 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        button_ok = new javax.swing.JButton();
        label_no_box = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txt_lp_susur_perut1 = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txt_gram_susur_perut1 = new javax.swing.JTextField();
        button_pilih_lp1 = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txt_gram1 = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        txt_lp_susur_perut2 = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        txt_gram_susur_perut2 = new javax.swing.JTextField();
        button_pilih_lp2 = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        txt_sh = new javax.swing.JTextField();
        txt_hancuran = new javax.swing.JTextField();
        txt_ront_ktr = new javax.swing.JTextField();
        txt_ront_bersih = new javax.swing.JTextField();
        txt_bersih = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        txt_bonggol = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        txt_serabut = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        txt_ront_kng = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        txt_keping1 = new javax.swing.JTextField();
        jLabel32 = new javax.swing.JLabel();
        ComboBox_bagian = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        Date_reproses = new com.toedter.calendar.JDateChooser();
        jLabel33 = new javax.swing.JLabel();
        Date_selesai = new com.toedter.calendar.JDateChooser();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("SETOR HASIL RE-PROSES");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel1.setText("EDIT RE-PROSES");

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("Jumlah Keping setelah re-proses :");

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("Berat setelah re-proses :");

        txt_keping2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        txt_gram2.setEditable(false);
        txt_gram2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_gram2.setFocusable(false);

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel4.setText("Kpg");

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel5.setText("Gram");

        button_ok.setBackground(new java.awt.Color(255, 255, 255));
        button_ok.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_ok.setText("OK");
        button_ok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_okActionPerformed(evt);
            }
        });

        label_no_box.setBackground(new java.awt.Color(255, 255, 255));
        label_no_box.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_no_box.setText("NO-BOX");

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel6.setText("LP Susur Perut 1 :");

        txt_lp_susur_perut1.setEditable(false);
        txt_lp_susur_perut1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_lp_susur_perut1.setFocusable(false);

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel8.setText("Gram");

        txt_gram_susur_perut1.setEditable(false);
        txt_gram_susur_perut1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_gram_susur_perut1.setFocusable(false);

        button_pilih_lp1.setBackground(new java.awt.Color(255, 255, 255));
        button_pilih_lp1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_pilih_lp1.setText("Pilih LP Suwir");
        button_pilih_lp1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_pilih_lp1ActionPerformed(evt);
            }
        });

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel9.setText("Departemen Tujuan :");

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel10.setText("Berat sebelum re-proses :");

        txt_gram1.setEditable(false);
        txt_gram1.setBackground(new java.awt.Color(255, 255, 255));
        txt_gram1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel12.setText("Gram");

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel13.setText("LP Susur Perut 2 :");

        txt_lp_susur_perut2.setEditable(false);
        txt_lp_susur_perut2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_lp_susur_perut2.setFocusable(false);

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel14.setText("Gram");

        txt_gram_susur_perut2.setEditable(false);
        txt_gram_susur_perut2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_gram_susur_perut2.setFocusable(false);

        button_pilih_lp2.setBackground(new java.awt.Color(255, 255, 255));
        button_pilih_lp2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_pilih_lp2.setText("Pilih LP Suwir");
        button_pilih_lp2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_pilih_lp2ActionPerformed(evt);
            }
        });

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel15.setText("Rendemen Bersih :");

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel16.setText("Rontokan Bersih :");

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel17.setText("Rontokan Kotor :");

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel18.setText("Hancuran :");

        jLabel19.setBackground(new java.awt.Color(255, 255, 255));
        jLabel19.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel19.setText("Susut Hilang :");

        txt_sh.setEditable(false);
        txt_sh.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_sh.setFocusable(false);

        txt_hancuran.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_hancuran.setText("0");
        txt_hancuran.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_hancuranFocusLost(evt);
            }
        });

        txt_ront_ktr.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_ront_ktr.setText("0");
        txt_ront_ktr.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_ront_ktrFocusLost(evt);
            }
        });

        txt_ront_bersih.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_ront_bersih.setText("0");
        txt_ront_bersih.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_ront_bersihFocusLost(evt);
            }
        });

        txt_bersih.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_bersih.setText("0");
        txt_bersih.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_bersihFocusLost(evt);
            }
        });

        jLabel20.setBackground(new java.awt.Color(255, 255, 255));
        jLabel20.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel20.setText("Gram");

        jLabel21.setBackground(new java.awt.Color(255, 255, 255));
        jLabel21.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel21.setText("Gram");

        jLabel22.setBackground(new java.awt.Color(255, 255, 255));
        jLabel22.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel22.setText("Gram");

        jLabel23.setBackground(new java.awt.Color(255, 255, 255));
        jLabel23.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel23.setText("Gram");

        jLabel24.setBackground(new java.awt.Color(255, 255, 255));
        jLabel24.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel24.setText("Gram");

        jLabel25.setBackground(new java.awt.Color(255, 255, 255));
        jLabel25.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel25.setText("Bonggol  :");

        txt_bonggol.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_bonggol.setText("0");
        txt_bonggol.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_bonggolFocusLost(evt);
            }
        });

        jLabel26.setBackground(new java.awt.Color(255, 255, 255));
        jLabel26.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel26.setText("Gram");

        jLabel27.setBackground(new java.awt.Color(255, 255, 255));
        jLabel27.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel27.setText("Serabut :");

        jLabel28.setBackground(new java.awt.Color(255, 255, 255));
        jLabel28.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel28.setText("Gram");

        txt_serabut.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_serabut.setText("0");
        txt_serabut.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_serabutFocusLost(evt);
            }
        });

        jLabel29.setBackground(new java.awt.Color(255, 255, 255));
        jLabel29.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel29.setText("Rontokan Kuning :");

        txt_ront_kng.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_ront_kng.setText("0");
        txt_ront_kng.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_ront_kngFocusLost(evt);
            }
        });

        jLabel30.setBackground(new java.awt.Color(255, 255, 255));
        jLabel30.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel30.setText("Gram");

        jLabel31.setBackground(new java.awt.Color(255, 255, 255));
        jLabel31.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel31.setText("Jumlah Keping sebelum re-proses :");

        txt_keping1.setEditable(false);
        txt_keping1.setBackground(new java.awt.Color(255, 255, 255));
        txt_keping1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel32.setBackground(new java.awt.Color(255, 255, 255));
        jLabel32.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel32.setText("Kpg");

        ComboBox_bagian.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_bagian.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Finishing 2" }));

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel11.setText("Tanggal Reproses :");

        Date_reproses.setDateFormatString("dd MMMM yyyy");

        jLabel33.setBackground(new java.awt.Color(255, 255, 255));
        jLabel33.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel33.setText("Tanggal Selesai Reproses :");

        Date_selesai.setDateFormatString("dd MMMM yyyy");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel15, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel29, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txt_ront_bersih, javax.swing.GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel21))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txt_bersih, javax.swing.GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel20))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txt_ront_kng, javax.swing.GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel30))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_no_box)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_pilih_lp1)
                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_pilih_lp2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_gram_susur_perut1, javax.swing.GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE)
                            .addComponent(txt_lp_susur_perut1, javax.swing.GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE)
                            .addComponent(txt_gram2, javax.swing.GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE)
                            .addComponent(txt_keping2, javax.swing.GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE)
                            .addComponent(txt_lp_susur_perut2, javax.swing.GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE)
                            .addComponent(txt_gram_susur_perut2, javax.swing.GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel4)
                                .addComponent(jLabel5))
                            .addComponent(jLabel14)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel31, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel33, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txt_gram1, javax.swing.GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE)
                                    .addComponent(txt_keping1, javax.swing.GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel12)
                                    .addComponent(jLabel32)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(ComboBox_bagian, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(Date_reproses, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(Date_selesai, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(button_ok))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txt_sh, javax.swing.GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel24))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txt_ront_ktr, javax.swing.GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel23))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txt_hancuran, javax.swing.GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel22))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txt_bonggol, javax.swing.GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel26))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txt_serabut, javax.swing.GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel28)))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_no_box, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ComboBox_bagian, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(Date_reproses, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_selesai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_keping1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_gram1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_keping2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_gram2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_lp_susur_perut1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_gram_susur_perut1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_pilih_lp1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_lp_susur_perut2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_gram_susur_perut2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_pilih_lp2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_bersih, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_ront_bersih, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_ront_kng, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_ront_ktr, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_hancuran, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_bonggol, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_serabut, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_sh, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
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
        count_sh();
        int keping_akhir = Integer.valueOf(txt_keping2.getText());
        float gram_akhir = Float.valueOf(txt_gram2.getText());
        if (txt_gram2.getText() == null || txt_keping2.getText() == null) {
            JOptionPane.showMessageDialog(this, "Silahkan isi data diatas !");
        } else if (keping_awal == keping_akhir) {
            update();
        } else {
            int dialogResult = JOptionPane.showConfirmDialog(this, "Jumlah Keping awal dan akhir tidak sama, apakah ingin melanjutkan?", "Warning", 0);
            if (dialogResult == JOptionPane.YES_OPTION) {
                JDialog_otorisasi_grading dialog = new JDialog_otorisasi_grading(new javax.swing.JFrame(), true);
                dialog.pack();
                dialog.setLocationRelativeTo(this);
                dialog.setVisible(true);
                dialog.setEnabled(true);
                boolean Check = dialog.OK();
                if (Check) {
                    update();
                }
            }
        }

    }//GEN-LAST:event_button_okActionPerformed

    private void button_pilih_lp1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_pilih_lp1ActionPerformed
        // TODO add your handling code here:
        JDialog_Choose_LPkaki dialog = new JDialog_Choose_LPkaki(new javax.swing.JFrame(), true);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);
        txt_lp_susur_perut1.setText(dialog.get_lpKaki());
        txt_gram_susur_perut1.setText(Float.toString(dialog.get_gramKaki()));
    }//GEN-LAST:event_button_pilih_lp1ActionPerformed

    private void button_pilih_lp2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_pilih_lp2ActionPerformed
        // TODO add your handling code here:
        JDialog_Choose_LPkaki dialog = new JDialog_Choose_LPkaki(new javax.swing.JFrame(), true);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);
        txt_lp_susur_perut2.setText(dialog.get_lpKaki());
        txt_gram_susur_perut2.setText(Float.toString(dialog.get_gramKaki()));
    }//GEN-LAST:event_button_pilih_lp2ActionPerformed

    private void txt_bersihFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_bersihFocusLost
        // TODO add your handling code here:
        count_sh();
    }//GEN-LAST:event_txt_bersihFocusLost

    private void txt_ront_bersihFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_ront_bersihFocusLost
        // TODO add your handling code here:
        count_sh();
    }//GEN-LAST:event_txt_ront_bersihFocusLost

    private void txt_ront_ktrFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_ront_ktrFocusLost
        // TODO add your handling code here:
        count_sh();
    }//GEN-LAST:event_txt_ront_ktrFocusLost

    private void txt_hancuranFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_hancuranFocusLost
        // TODO add your handling code here:
        count_sh();
    }//GEN-LAST:event_txt_hancuranFocusLost

    private void txt_bonggolFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_bonggolFocusLost
        // TODO add your handling code here:
        count_sh();
    }//GEN-LAST:event_txt_bonggolFocusLost

    private void txt_serabutFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_serabutFocusLost
        // TODO add your handling code here:
        count_sh();
    }//GEN-LAST:event_txt_serabutFocusLost

    private void txt_ront_kngFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_ront_kngFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_ront_kngFocusLost

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_bagian;
    private com.toedter.calendar.JDateChooser Date_reproses;
    private com.toedter.calendar.JDateChooser Date_selesai;
    private javax.swing.JButton button_ok;
    private javax.swing.JButton button_pilih_lp1;
    private javax.swing.JButton button_pilih_lp2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel label_no_box;
    private javax.swing.JTextField txt_bersih;
    private javax.swing.JTextField txt_bonggol;
    private javax.swing.JTextField txt_gram1;
    private javax.swing.JTextField txt_gram2;
    private javax.swing.JTextField txt_gram_susur_perut1;
    private javax.swing.JTextField txt_gram_susur_perut2;
    private javax.swing.JTextField txt_hancuran;
    private javax.swing.JTextField txt_keping1;
    private javax.swing.JTextField txt_keping2;
    private javax.swing.JTextField txt_lp_susur_perut1;
    private javax.swing.JTextField txt_lp_susur_perut2;
    private javax.swing.JTextField txt_ront_bersih;
    private javax.swing.JTextField txt_ront_kng;
    private javax.swing.JTextField txt_ront_ktr;
    private javax.swing.JTextField txt_serabut;
    private javax.swing.JTextField txt_sh;
    // End of variables declaration//GEN-END:variables
}
