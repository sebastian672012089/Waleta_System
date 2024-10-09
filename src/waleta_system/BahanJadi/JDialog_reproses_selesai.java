package waleta_system.BahanJadi;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import waleta_system.BahanBaku.JDialog_otorisasi_grading;
import waleta_system.Browse_Karyawan;
import waleta_system.Class.Utility;
import waleta_system.Panel_produksi.JDialog_Choose_LPkaki;

public class JDialog_reproses_selesai extends javax.swing.JDialog {

    
    String sql = null;
    ResultSet rs;
    Date date = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    String no_box, no_reproses;
    int keping_awal = 0;
    float gram_awal = 0;
    String tujuan = null;

    public JDialog_reproses_selesai(java.awt.Frame parent, boolean modal, String no_box, String no_reproses, int kpg_awal, float gram_awal, Object kaki1, float gram_kaki1, Object kaki2, float gram_kaki2, String tujuan) {
        super(parent, modal);
        try {
            this.no_box = no_box;
            this.no_reproses = no_reproses;
            this.gram_awal = gram_awal;
            this.tujuan = tujuan;
            this.setResizable(false);
            
            
            initComponents();
            keping_awal = kpg_awal;
            txt_keping1.setText(Integer.toString(kpg_awal));
            txt_gram1.setText(Float.toString(gram_awal));
            label_no_box.setText(no_box);
            label_tujuan.setText(tujuan);
            txt_lp_susur_perut1.setText((String) kaki1);
            txt_lp_susur_perut2.setText((String) kaki2);
            txt_gram_susur_perut1.setText(Float.toString(gram_kaki1));
            txt_gram_susur_perut2.setText(Float.toString(gram_kaki2));
        } catch (Exception ex) {
            Logger.getLogger(JDialog_reproses_selesai.class.getName()).log(Level.SEVERE, null, ex);
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
            String lokasi_terakhir = "";
            if (tujuan.equals("GBJ")) {
                lokasi_terakhir = "GRADING";
            } else if (tujuan.equals("QC")) {
                lokasi_terakhir = "TREATMENT";
                String qc = "INSERT INTO `tb_lab_barang_jadi`(`no_box`, `tgl_masuk`) "
                        + "VALUES ('" + no_box + "',CURRENT_DATE)";
                Utility.db.getConnection().createStatement();
                Utility.db.getStatement().executeUpdate(qc);
            }
            String update_box = "UPDATE `tb_box_bahan_jadi` SET `status_terakhir`='Re-Processing',`lokasi_terakhir`='" + lokasi_terakhir + "', `keping`='" + txt_keping2.getText() + "',`berat`='" + txt_gram2.getText() + "',`tgl_proses_terakhir`=CURRENT_DATE "
                    + "WHERE `no_box` = '" + no_box + "'";
            Utility.db.getConnection().createStatement();
            Utility.db.getStatement().executeUpdate(update_box);

            String kaki1 = "`no_lp_suwir`='" + txt_lp_susur_perut1.getText() + "', `gram_kaki`='" + txt_gram_susur_perut1.getText() + "',";
            if (txt_lp_susur_perut1.getText().equals("") || txt_lp_susur_perut1.getText() == null) {
                kaki1 = "`no_lp_suwir`=NULL, `gram_kaki`=0,";
            }
            String kaki2 = "`no_lp_suwir2`='" + txt_lp_susur_perut2.getText() + "', `gram_kaki2`='" + txt_gram_susur_perut2.getText() + "',";
            if (txt_lp_susur_perut2.getText().equals("") || txt_lp_susur_perut2.getText() == null) {
                kaki2 = "`no_lp_suwir2`=NULL, `gram_kaki2`=0,";
            }
            String reproses = "UPDATE `tb_reproses` SET "
                    + "`tgl_selesai`='" + dateFormat.format(date) + "',"
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
                    + "`pekerja_timbang`='" + txt_id.getText() + "',"
                    + "`status`='FINISHED' "
                    + "WHERE `no_reproses` = '" + no_reproses + "'";

            Utility.db.getConnection().createStatement();
            Utility.db.getStatement().executeUpdate(reproses);

            Utility.db.getConnection().commit();
            JOptionPane.showMessageDialog(this, "Data Berhasil disetor ke bagian " + tujuan);
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
            } catch (Exception ex) {
                Logger.getLogger(JDialog_reproses_selesai.class.getName()).log(Level.SEVERE, null, ex);
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
        jLabel7 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txt_lp_susur_perut1 = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txt_gram_susur_perut1 = new javax.swing.JTextField();
        button_pilih_lp1 = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txt_gram1 = new javax.swing.JTextField();
        txt_keping1 = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
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
        label_tujuan = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        button_pilih_pekerja = new javax.swing.JButton();
        txt_nama = new javax.swing.JTextField();
        txt_id = new javax.swing.JTextField();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("SETOR HASIL RE-PROSES");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel1.setText("RE-PROSES");

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("Keping setelah re-proses :");

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
        label_no_box.setForeground(new java.awt.Color(255, 0, 0));
        label_no_box.setText("NO-BOX");

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel7.setText("SELESAI, DI SETOR KE");

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
        jLabel9.setText("Keping sebelum re-proses :");

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel10.setText("Berat sebelum re-proses :");

        txt_gram1.setEditable(false);
        txt_gram1.setBackground(new java.awt.Color(255, 255, 255));
        txt_gram1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        txt_keping1.setEditable(false);
        txt_keping1.setBackground(new java.awt.Color(255, 255, 255));
        txt_keping1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel11.setText("Kpg");

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

        label_tujuan.setBackground(new java.awt.Color(255, 255, 255));
        label_tujuan.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_tujuan.setForeground(new java.awt.Color(255, 0, 0));
        label_tujuan.setText("TUJUAN");

        jLabel31.setBackground(new java.awt.Color(255, 255, 255));
        jLabel31.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel31.setText("Pekerja Timbang :");

        button_pilih_pekerja.setBackground(new java.awt.Color(255, 255, 255));
        button_pilih_pekerja.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_pilih_pekerja.setText("Pilih Pekerja");
        button_pilih_pekerja.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_pilih_pekerjaActionPerformed(evt);
            }
        });

        txt_nama.setEditable(false);
        txt_nama.setBackground(new java.awt.Color(255, 255, 255));
        txt_nama.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_nama.setFocusable(false);

        txt_id.setEditable(false);
        txt_id.setBackground(new java.awt.Color(255, 255, 255));
        txt_id.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_id.setFocusable(false);

        jLabel32.setBackground(new java.awt.Color(255, 255, 255));
        jLabel32.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel32.setText("ID");

        jLabel33.setBackground(new java.awt.Color(255, 255, 255));
        jLabel33.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel33.setText("Nama");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_no_box)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_tujuan))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(button_pilih_pekerja)
                                    .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(162, 162, 162))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(button_ok)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(button_pilih_lp1)
                                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(button_pilih_lp2)
                                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(txt_serabut)
                                            .addComponent(txt_bonggol)
                                            .addComponent(txt_hancuran)
                                            .addComponent(txt_ront_ktr)
                                            .addComponent(txt_ront_kng)
                                            .addComponent(txt_ront_bersih)
                                            .addComponent(txt_bersih)
                                            .addComponent(txt_gram_susur_perut2)
                                            .addComponent(txt_lp_susur_perut2)
                                            .addComponent(txt_gram_susur_perut1)
                                            .addComponent(txt_lp_susur_perut1)
                                            .addComponent(txt_gram2)
                                            .addComponent(txt_keping2)
                                            .addComponent(txt_gram1)
                                            .addComponent(txt_keping1)
                                            .addComponent(txt_id, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(txt_sh)
                                            .addComponent(txt_nama, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addGap(8, 8, 8)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel21)
                            .addComponent(jLabel20)
                            .addComponent(jLabel30)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel8)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel5))
                                .addComponent(jLabel14))
                            .addComponent(jLabel11)
                            .addComponent(jLabel12)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel23)
                                .addComponent(jLabel22)
                                .addComponent(jLabel26)
                                .addComponent(jLabel28)
                                .addComponent(jLabel24, javax.swing.GroupLayout.Alignment.TRAILING))
                            .addComponent(jLabel32)
                            .addComponent(jLabel33))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_no_box, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_tujuan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_keping1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_id, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_nama, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_pilih_pekerja, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
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

    private void button_pilih_pekerjaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_pilih_pekerjaActionPerformed
        // TODO add your handling code here:
        Browse_Karyawan dialog = new Browse_Karyawan(new javax.swing.JFrame(), true, null);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);

        int x = Browse_Karyawan.table_list_karyawan.getSelectedRow();
        if (x != -1) {
            txt_id.setText(Browse_Karyawan.table_list_karyawan.getValueAt(x, 0).toString());
            txt_nama.setText(Browse_Karyawan.table_list_karyawan.getValueAt(x, 1).toString());
        }
    }//GEN-LAST:event_button_pilih_pekerjaActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton button_ok;
    private javax.swing.JButton button_pilih_lp1;
    private javax.swing.JButton button_pilih_lp2;
    private javax.swing.JButton button_pilih_pekerja;
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
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel label_no_box;
    private javax.swing.JLabel label_tujuan;
    private javax.swing.JTextField txt_bersih;
    private javax.swing.JTextField txt_bonggol;
    private javax.swing.JTextField txt_gram1;
    private javax.swing.JTextField txt_gram2;
    private javax.swing.JTextField txt_gram_susur_perut1;
    private javax.swing.JTextField txt_gram_susur_perut2;
    private javax.swing.JTextField txt_hancuran;
    private javax.swing.JTextField txt_id;
    private javax.swing.JTextField txt_keping1;
    private javax.swing.JTextField txt_keping2;
    private javax.swing.JTextField txt_lp_susur_perut1;
    private javax.swing.JTextField txt_lp_susur_perut2;
    private javax.swing.JTextField txt_nama;
    private javax.swing.JTextField txt_ront_bersih;
    private javax.swing.JTextField txt_ront_kng;
    private javax.swing.JTextField txt_ront_ktr;
    private javax.swing.JTextField txt_serabut;
    private javax.swing.JTextField txt_sh;
    // End of variables declaration//GEN-END:variables
}
