package waleta_system.Panel_produksi;

import waleta_system.Class.Utility;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import waleta_system.Browse_Karyawan;
import waleta_system.MainForm;

public class JDialog_Setor_LP_F2 extends javax.swing.JDialog {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    float keping_cetak = 0, jidun_cetak = 0, berat_kering_lp = 0;
    Date tgl_masuk = null;
    String ruangan = "";

    public JDialog_Setor_LP_F2(java.awt.Frame parent, boolean modal, Object tgl_masuk) {
        super(parent, modal);
        this.setResizable(false);
        initComponents();
        getData();

        try {
            this.tgl_masuk = dateFormat.parse(tgl_masuk.toString());
            Date_Setor.setMinSelectableDate(this.tgl_masuk);
        } catch (ParseException ex) {
            Logger.getLogger(JDialog_Setor_LP_Cabut.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void getData() {
        try {
            label_tanggal_selesai.setText(new SimpleDateFormat("dd MMMM yyyy").format(date));
            int i = JPanel_Finishing2.Table_Data_f2.getSelectedRow();
            label_no_lp.setText(JPanel_Finishing2.Table_Data_f2.getValueAt(i, 0).toString());
            if (JPanel_Finishing2.Table_Data_f2.getValueAt(i, 8) != null) {
                Date_koreksi.setDate(dateFormat.parse(JPanel_Finishing2.Table_Data_f2.getValueAt(i, 8).toString()));
            }
            txt_pengoreksi.setText(JPanel_Finishing2.Table_Data_f2.getValueAt(i, 9).toString());
            if (JPanel_Finishing2.Table_Data_f2.getValueAt(i, 10) != null) {
                Date_f1.setDate(dateFormat.parse(JPanel_Finishing2.Table_Data_f2.getValueAt(i, 10).toString()));
            }
            if (JPanel_Finishing2.Table_Data_f2.getValueAt(i, 11) != null) {
                txt_pekerja_f1.setText(JPanel_Finishing2.Table_Data_f2.getValueAt(i, 11).toString());
            }
            if (JPanel_Finishing2.Table_Data_f2.getValueAt(i, 12) != null) {
                Date_f2.setDate(dateFormat.parse(JPanel_Finishing2.Table_Data_f2.getValueAt(i, 12).toString()));
            }
            if (JPanel_Finishing2.Table_Data_f2.getValueAt(i, 13) != null) {
                txt_pekerja_f2.setText(JPanel_Finishing2.Table_Data_f2.getValueAt(i, 13).toString());
            }

            txt_sesekan.setText(JPanel_Finishing2.Table_Data_f2.getValueAt(i, 28).toString());
            txt_hancuran.setText(JPanel_Finishing2.Table_Data_f2.getValueAt(i, 29).toString());
            txt_rontokan.setText(JPanel_Finishing2.Table_Data_f2.getValueAt(i, 30).toString());
            txt_bonggol.setText(JPanel_Finishing2.Table_Data_f2.getValueAt(i, 31).toString());
            txt_serabut.setText(JPanel_Finishing2.Table_Data_f2.getValueAt(i, 32).toString());

            txt_tambah_kaki1.setText(JPanel_Finishing2.Table_Data_f2.getValueAt(i, 33).toString());
            txt_lp_kaki1.setText(JPanel_Finishing2.Table_Data_f2.getValueAt(i, 34).toString());
            txt_tambah_kaki2.setText(JPanel_Finishing2.Table_Data_f2.getValueAt(i, 35).toString());
            txt_lp_kaki2.setText(JPanel_Finishing2.Table_Data_f2.getValueAt(i, 36).toString());

            txt_admin.setText(MainForm.Login_NamaPegawai);

            sql = "SELECT "
                    + "`ruangan`, `tb_laporan_produksi`.`berat_kering`, "
                    + "`jumlah_keping` AS 'keping_lp', "
                    + "(`cetak_mangkok` + `cetak_pecah` + `cetak_flat`) AS 'keping_cetak', "
                    + "`cetak_jidun_real` "
                    + "FROM `tb_cetak` "
                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_cetak`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi` "
                    + "WHERE `tb_cetak`.`no_laporan_produksi` = '" + JPanel_Finishing2.Table_Data_f2.getValueAt(i, 0).toString() + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                keping_cetak = rs.getInt("keping_lp");
                jidun_cetak = rs.getInt("cetak_jidun_real");
                berat_kering_lp = rs.getInt("berat_kering");
                ruangan = rs.getString("ruangan");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JDialog_Setor_LP_F2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        label_no_lp = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        button_save = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        button_pick_diserahkan = new javax.swing.JButton();
        label_tanggal_selesai = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txt_fbonus = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        txt_diserahkan = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txt_flat = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        txt_mk_pecah = new javax.swing.JTextField();
        txt_bk_flat = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        txt_bk_fbonus = new javax.swing.JTextField();
        txt_bk_pecah = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        Date_Setor = new com.toedter.calendar.JDateChooser();
        Date_koreksi = new com.toedter.calendar.JDateChooser();
        jLabel33 = new javax.swing.JLabel();
        txt_fnol = new javax.swing.JTextField();
        jLabel34 = new javax.swing.JLabel();
        txt_bk_fnol = new javax.swing.JTextField();
        jLabel35 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        txt_pengoreksi = new javax.swing.JTextField();
        button_pick_pengoreksi = new javax.swing.JButton();
        jLabel40 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        txt_pekerja_f1 = new javax.swing.JTextField();
        Date_f1 = new com.toedter.calendar.JDateChooser();
        button_pick_f1 = new javax.swing.JButton();
        button_pick_f2 = new javax.swing.JButton();
        jLabel42 = new javax.swing.JLabel();
        Date_f2 = new com.toedter.calendar.JDateChooser();
        txt_pekerja_f2 = new javax.swing.JTextField();
        jLabel43 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        txt_jidun_utuh = new javax.swing.JTextField();
        jLabel45 = new javax.swing.JLabel();
        txt_bk_jidun = new javax.swing.JTextField();
        jLabel46 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        txt_jidun_pch = new javax.swing.JTextField();
        jLabel49 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        txt_tambah_kaki1 = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        txt_lp_kaki1 = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        txt_lp_kaki2 = new javax.swing.JTextField();
        txt_tambah_kaki2 = new javax.swing.JTextField();
        jLabel32 = new javax.swing.JLabel();
        button_LPkaki1 = new javax.swing.JButton();
        txt_admin = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        button_LPkaki2 = new javax.swing.JButton();
        jLabel37 = new javax.swing.JLabel();
        label_netto = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        button_LPkaki3 = new javax.swing.JButton();
        jLabel38 = new javax.swing.JLabel();
        txt_netto = new javax.swing.JTextField();
        jLabel36 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txt_sesekan = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        txt_hancuran = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        txt_rontokan = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        txt_bonggol = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        txt_serabut = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        button_pilih_pekerja_timbang = new javax.swing.JButton();
        txt_pekerja_timbang = new javax.swing.JTextField();
        jLabel50 = new javax.swing.JLabel();
        button_pilih_pekerja_cabut_OM = new javax.swing.JButton();
        txt_pekerja_cabut_OM_id = new javax.swing.JTextField();
        button_pilih_pekerja_cetak_OM = new javax.swing.JButton();
        txt_pekerja_cetak_OM_id = new javax.swing.JTextField();
        jLabel51 = new javax.swing.JLabel();
        txt_pekerja_cabut_OM_nama = new javax.swing.JTextField();
        txt_pekerja_cetak_OM_nama = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel1.setText("Setor");
        jLabel1.setFocusable(false);

        label_no_lp.setBackground(new java.awt.Color(255, 255, 255));
        label_no_lp.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        label_no_lp.setText("NO. Laporan Produksi");
        label_no_lp.setFocusable(false);

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel2.setText("ke Grading Bahan Jadi");
        jLabel2.setFocusable(false);

        button_save.setBackground(new java.awt.Color(255, 255, 255));
        button_save.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_save.setText("Save");
        button_save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_saveActionPerformed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(204, 204, 255));

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("Tanggal Selesai :");
        jLabel3.setFocusable(false);

        button_pick_diserahkan.setBackground(new java.awt.Color(255, 255, 255));
        button_pick_diserahkan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_pick_diserahkan.setText("...");
        button_pick_diserahkan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_pick_diserahkanActionPerformed(evt);
            }
        });

        label_tanggal_selesai.setBackground(new java.awt.Color(255, 255, 255));
        label_tanggal_selesai.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_tanggal_selesai.setText("Hari ini");

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel9.setText("F. Bonus :");
        jLabel9.setFocusable(false);

        txt_fbonus.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_fbonus.setText("0");
        txt_fbonus.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_fbonusKeyTyped(evt);
            }
        });

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel15.setText("Keping");
        jLabel15.setFocusable(false);

        txt_diserahkan.setEditable(false);
        txt_diserahkan.setBackground(new java.awt.Color(255, 255, 255));
        txt_diserahkan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_diserahkan.setFocusable(false);

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel5.setText("Diserahkan Oleh :");
        jLabel5.setFocusable(false);

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel7.setText("Pecah :");
        jLabel7.setFocusable(false);

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel8.setText("Flat :");
        jLabel8.setFocusable(false);

        txt_flat.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_flat.setText("0");
        txt_flat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_flatKeyTyped(evt);
            }
        });

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel14.setText("Keping");
        jLabel14.setFocusable(false);

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel13.setText("Keping");
        jLabel13.setFocusable(false);

        txt_mk_pecah.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_mk_pecah.setText("0");
        txt_mk_pecah.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_mk_pecahKeyTyped(evt);
            }
        });

        txt_bk_flat.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_bk_flat.setText("0");
        txt_bk_flat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_bk_flatKeyTyped(evt);
            }
        });

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel16.setText("Grams");
        jLabel16.setFocusable(false);

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel17.setText("Grams");
        jLabel17.setFocusable(false);

        txt_bk_fbonus.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_bk_fbonus.setText("0");
        txt_bk_fbonus.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_bk_fbonusKeyTyped(evt);
            }
        });

        txt_bk_pecah.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_bk_pecah.setText("0");
        txt_bk_pecah.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_bk_pecahKeyTyped(evt);
            }
        });

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel18.setText("Grams");
        jLabel18.setFocusable(false);

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel4.setText("Tanggal Selesai :");
        jLabel4.setFocusable(false);

        Date_Setor.setBackground(new java.awt.Color(255, 255, 255));
        Date_Setor.setDate(date);
        Date_Setor.setDateFormatString("dd MMMM yyyy");
        Date_Setor.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Date_Setor.setMaxSelectableDate(new Date());

        Date_koreksi.setBackground(new java.awt.Color(255, 255, 255));
        Date_koreksi.setDateFormatString("dd MMMM yyyy");
        Date_koreksi.setEnabled(false);
        Date_koreksi.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Date_koreksi.setMaxSelectableDate(new Date());

        jLabel33.setBackground(new java.awt.Color(255, 255, 255));
        jLabel33.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel33.setText("Faktor 0 :");
        jLabel33.setFocusable(false);

        txt_fnol.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_fnol.setText("0");
        txt_fnol.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_fnolKeyTyped(evt);
            }
        });

        jLabel34.setBackground(new java.awt.Color(255, 255, 255));
        jLabel34.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel34.setText("Keping");
        jLabel34.setFocusable(false);

        txt_bk_fnol.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_bk_fnol.setText("0");
        txt_bk_fnol.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_bk_fnolKeyTyped(evt);
            }
        });

        jLabel35.setBackground(new java.awt.Color(255, 255, 255));
        jLabel35.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel35.setText("Grams");
        jLabel35.setFocusable(false);

        jLabel30.setBackground(new java.awt.Color(255, 255, 255));
        jLabel30.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel30.setText("Tanggal koreksi :");
        jLabel30.setFocusable(false);

        jLabel31.setBackground(new java.awt.Color(255, 255, 255));
        jLabel31.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel31.setText("Pengoreksi :");
        jLabel31.setFocusable(false);

        txt_pengoreksi.setEditable(false);
        txt_pengoreksi.setBackground(new java.awt.Color(255, 255, 255));
        txt_pengoreksi.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_pengoreksi.setFocusable(false);

        button_pick_pengoreksi.setBackground(new java.awt.Color(255, 255, 255));
        button_pick_pengoreksi.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_pick_pengoreksi.setText("...");
        button_pick_pengoreksi.setEnabled(false);
        button_pick_pengoreksi.setFocusable(false);
        button_pick_pengoreksi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_pick_pengoreksiActionPerformed(evt);
            }
        });

        jLabel40.setBackground(new java.awt.Color(255, 255, 255));
        jLabel40.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel40.setText("Tanggal F1 :");
        jLabel40.setFocusable(false);

        jLabel41.setBackground(new java.awt.Color(255, 255, 255));
        jLabel41.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel41.setText("Pekerja F1 :");
        jLabel41.setFocusable(false);

        txt_pekerja_f1.setEditable(false);
        txt_pekerja_f1.setBackground(new java.awt.Color(255, 255, 255));
        txt_pekerja_f1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_pekerja_f1.setFocusable(false);

        Date_f1.setBackground(new java.awt.Color(255, 255, 255));
        Date_f1.setDateFormatString("dd MMMM yyyy");
        Date_f1.setEnabled(false);
        Date_f1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Date_f1.setMaxSelectableDate(new Date());

        button_pick_f1.setBackground(new java.awt.Color(255, 255, 255));
        button_pick_f1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_pick_f1.setText("...");
        button_pick_f1.setEnabled(false);
        button_pick_f1.setFocusable(false);
        button_pick_f1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_pick_f1ActionPerformed(evt);
            }
        });

        button_pick_f2.setBackground(new java.awt.Color(255, 255, 255));
        button_pick_f2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_pick_f2.setText("...");
        button_pick_f2.setEnabled(false);
        button_pick_f2.setFocusable(false);
        button_pick_f2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_pick_f2ActionPerformed(evt);
            }
        });

        jLabel42.setBackground(new java.awt.Color(255, 255, 255));
        jLabel42.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel42.setText("Pekerja F2 :");
        jLabel42.setFocusable(false);

        Date_f2.setBackground(new java.awt.Color(255, 255, 255));
        Date_f2.setDateFormatString("dd MMMM yyyy");
        Date_f2.setEnabled(false);
        Date_f2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Date_f2.setMaxSelectableDate(new Date());

        txt_pekerja_f2.setEditable(false);
        txt_pekerja_f2.setBackground(new java.awt.Color(255, 255, 255));
        txt_pekerja_f2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_pekerja_f2.setFocusable(false);

        jLabel43.setBackground(new java.awt.Color(255, 255, 255));
        jLabel43.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel43.setText("Tanggal F2 :");
        jLabel43.setFocusable(false);

        jLabel44.setBackground(new java.awt.Color(255, 255, 255));
        jLabel44.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel44.setText("Jidun Utuh :");
        jLabel44.setFocusable(false);

        txt_jidun_utuh.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_jidun_utuh.setText("0");
        txt_jidun_utuh.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_jidun_utuhKeyTyped(evt);
            }
        });

        jLabel45.setBackground(new java.awt.Color(255, 255, 255));
        jLabel45.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel45.setText("Keping");
        jLabel45.setFocusable(false);

        txt_bk_jidun.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_bk_jidun.setText("0");
        txt_bk_jidun.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_bk_jidunKeyTyped(evt);
            }
        });

        jLabel46.setBackground(new java.awt.Color(255, 255, 255));
        jLabel46.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel46.setText("Grams");
        jLabel46.setFocusable(false);

        jLabel48.setBackground(new java.awt.Color(255, 255, 255));
        jLabel48.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel48.setText("Jidun Pecah :");
        jLabel48.setFocusable(false);

        txt_jidun_pch.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_jidun_pch.setText("0");
        txt_jidun_pch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_jidun_pchKeyTyped(evt);
            }
        });

        jLabel49.setBackground(new java.awt.Color(255, 255, 255));
        jLabel49.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel49.setText("Keping");
        jLabel49.setFocusable(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel42, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel48, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(Date_Setor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Date_koreksi, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Date_f1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Date_f2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_flat, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(txt_mk_pecah, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txt_fbonus, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txt_fnol, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txt_jidun_utuh, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_jidun_pch, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel14)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel13)
                                .addComponent(jLabel15)
                                .addComponent(jLabel34))
                            .addComponent(jLabel45)
                            .addComponent(jLabel49))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_bk_flat, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(txt_bk_pecah, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txt_bk_fbonus, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txt_bk_fnol, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txt_bk_jidun, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel16)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel17)
                                .addComponent(jLabel18)
                                .addComponent(jLabel35))
                            .addComponent(jLabel46)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txt_diserahkan, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(button_pick_diserahkan, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txt_pengoreksi, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(button_pick_pengoreksi, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txt_pekerja_f1, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(button_pick_f1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txt_pekerja_f2, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(button_pick_f2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(label_tanggal_selesai, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_tanggal_selesai, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_Setor, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_diserahkan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_pick_diserahkan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_koreksi, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_pengoreksi, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_pick_pengoreksi, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_f1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_pekerja_f1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_pick_f1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_f2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel42, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_pekerja_f2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_pick_f2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_fbonus, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_fnol, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_mk_pecah, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_bk_fbonus, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_bk_fnol, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_bk_pecah, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_flat, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_jidun_utuh, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel45, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel48, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_jidun_pch, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel49, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_bk_flat, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_bk_jidun, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel46, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(77, Short.MAX_VALUE))
        );

        jPanel2.setBackground(new java.awt.Color(204, 204, 255));

        txt_tambah_kaki1.setEditable(false);
        txt_tambah_kaki1.setBackground(new java.awt.Color(255, 255, 255));
        txt_tambah_kaki1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_tambah_kaki1.setText("0");
        txt_tambah_kaki1.setFocusable(false);

        jLabel28.setBackground(new java.awt.Color(255, 255, 255));
        jLabel28.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel28.setText("Grams");
        jLabel28.setFocusable(false);

        txt_lp_kaki1.setEditable(false);
        txt_lp_kaki1.setBackground(new java.awt.Color(255, 255, 255));
        txt_lp_kaki1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_lp_kaki1.setText("-");
        txt_lp_kaki1.setFocusable(false);

        jLabel29.setBackground(new java.awt.Color(255, 255, 255));
        jLabel29.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel29.setText("Tambah Kaki 2 :");
        jLabel29.setFocusable(false);

        txt_lp_kaki2.setEditable(false);
        txt_lp_kaki2.setBackground(new java.awt.Color(255, 255, 255));
        txt_lp_kaki2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_lp_kaki2.setText("-");
        txt_lp_kaki2.setFocusable(false);

        txt_tambah_kaki2.setEditable(false);
        txt_tambah_kaki2.setBackground(new java.awt.Color(255, 255, 255));
        txt_tambah_kaki2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_tambah_kaki2.setText("0");
        txt_tambah_kaki2.setFocusable(false);

        jLabel32.setBackground(new java.awt.Color(255, 255, 255));
        jLabel32.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel32.setText("Grams");
        jLabel32.setFocusable(false);

        button_LPkaki1.setBackground(new java.awt.Color(255, 255, 255));
        button_LPkaki1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_LPkaki1.setText("LP kaki 1");
        button_LPkaki1.setFocusable(false);
        button_LPkaki1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_LPkaki1ActionPerformed(evt);
            }
        });

        txt_admin.setEditable(false);
        txt_admin.setBackground(new java.awt.Color(255, 255, 255));
        txt_admin.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_admin.setFocusable(false);

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel11.setText("Admin :");
        jLabel11.setFocusable(false);

        button_LPkaki2.setBackground(new java.awt.Color(255, 255, 255));
        button_LPkaki2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_LPkaki2.setText("LP kaki 2");
        button_LPkaki2.setFocusable(false);
        button_LPkaki2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_LPkaki2ActionPerformed(evt);
            }
        });

        jLabel37.setBackground(new java.awt.Color(255, 255, 255));
        jLabel37.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel37.setText("Netto :");
        jLabel37.setFocusable(false);

        label_netto.setBackground(new java.awt.Color(255, 255, 255));
        label_netto.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_netto.setText("0");

        jLabel39.setBackground(new java.awt.Color(255, 255, 255));
        jLabel39.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel39.setText("Grams");

        button_LPkaki3.setBackground(new java.awt.Color(255, 255, 255));
        button_LPkaki3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_LPkaki3.setText("Count");
        button_LPkaki3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_LPkaki3ActionPerformed(evt);
            }
        });

        jLabel38.setBackground(new java.awt.Color(255, 255, 255));
        jLabel38.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel38.setText("Netto :");
        jLabel38.setFocusable(false);

        txt_netto.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_netto.setText("0");
        txt_netto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_nettoKeyTyped(evt);
            }
        });

        jLabel36.setBackground(new java.awt.Color(255, 255, 255));
        jLabel36.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel36.setText("Grams");

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel10.setText("Sesekan :");
        jLabel10.setFocusable(false);

        txt_sesekan.setEditable(false);
        txt_sesekan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_sesekan.setText("0");
        txt_sesekan.setFocusable(false);
        txt_sesekan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_sesekanKeyTyped(evt);
            }
        });

        jLabel19.setBackground(new java.awt.Color(255, 255, 255));
        jLabel19.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel19.setText("Grams");
        jLabel19.setFocusable(false);

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel12.setText("Hancuran :");
        jLabel12.setFocusable(false);

        jLabel20.setBackground(new java.awt.Color(255, 255, 255));
        jLabel20.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel20.setText("Grams");
        jLabel20.setFocusable(false);

        txt_hancuran.setEditable(false);
        txt_hancuran.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_hancuran.setText("0");
        txt_hancuran.setFocusable(false);
        txt_hancuran.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_hancuranKeyTyped(evt);
            }
        });

        jLabel21.setBackground(new java.awt.Color(255, 255, 255));
        jLabel21.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel21.setText("Rontokan :");
        jLabel21.setFocusable(false);

        txt_rontokan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_rontokan.setText("0");
        txt_rontokan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_rontokanKeyTyped(evt);
            }
        });

        jLabel22.setBackground(new java.awt.Color(255, 255, 255));
        jLabel22.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel22.setText("Grams");
        jLabel22.setFocusable(false);

        txt_bonggol.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_bonggol.setText("0");
        txt_bonggol.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_bonggolKeyTyped(evt);
            }
        });

        jLabel23.setBackground(new java.awt.Color(255, 255, 255));
        jLabel23.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel23.setText("Grams");
        jLabel23.setFocusable(false);

        jLabel24.setBackground(new java.awt.Color(255, 255, 255));
        jLabel24.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel24.setText("Bonggol :");
        jLabel24.setFocusable(false);

        jLabel25.setBackground(new java.awt.Color(255, 255, 255));
        jLabel25.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel25.setText("Grams");
        jLabel25.setFocusable(false);

        txt_serabut.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_serabut.setText("0");
        txt_serabut.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_serabutKeyTyped(evt);
            }
        });

        jLabel26.setBackground(new java.awt.Color(255, 255, 255));
        jLabel26.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel26.setText("Serabut :");
        jLabel26.setFocusable(false);

        jLabel27.setBackground(new java.awt.Color(255, 255, 255));
        jLabel27.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel27.setText("Tambah Kaki 1 :");
        jLabel27.setFocusable(false);

        jLabel47.setBackground(new java.awt.Color(255, 255, 255));
        jLabel47.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel47.setText("Pekerja Timbang :");
        jLabel47.setFocusable(false);

        button_pilih_pekerja_timbang.setBackground(new java.awt.Color(255, 255, 255));
        button_pilih_pekerja_timbang.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_pilih_pekerja_timbang.setText("...");
        button_pilih_pekerja_timbang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_pilih_pekerja_timbangActionPerformed(evt);
            }
        });

        txt_pekerja_timbang.setEditable(false);
        txt_pekerja_timbang.setBackground(new java.awt.Color(255, 255, 255));
        txt_pekerja_timbang.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_pekerja_timbang.setFocusable(false);

        jLabel50.setBackground(new java.awt.Color(255, 255, 255));
        jLabel50.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel50.setText("Pekerja Cabut OM :");
        jLabel50.setFocusable(false);

        button_pilih_pekerja_cabut_OM.setBackground(new java.awt.Color(255, 255, 255));
        button_pilih_pekerja_cabut_OM.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_pilih_pekerja_cabut_OM.setText("...");
        button_pilih_pekerja_cabut_OM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_pilih_pekerja_cabut_OMActionPerformed(evt);
            }
        });

        txt_pekerja_cabut_OM_id.setEditable(false);
        txt_pekerja_cabut_OM_id.setBackground(new java.awt.Color(255, 255, 255));
        txt_pekerja_cabut_OM_id.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_pekerja_cabut_OM_id.setFocusable(false);

        button_pilih_pekerja_cetak_OM.setBackground(new java.awt.Color(255, 255, 255));
        button_pilih_pekerja_cetak_OM.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_pilih_pekerja_cetak_OM.setText("...");
        button_pilih_pekerja_cetak_OM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_pilih_pekerja_cetak_OMActionPerformed(evt);
            }
        });

        txt_pekerja_cetak_OM_id.setEditable(false);
        txt_pekerja_cetak_OM_id.setBackground(new java.awt.Color(255, 255, 255));
        txt_pekerja_cetak_OM_id.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_pekerja_cetak_OM_id.setFocusable(false);

        jLabel51.setBackground(new java.awt.Color(255, 255, 255));
        jLabel51.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel51.setText("Pekerja Cetak OM :");
        jLabel51.setFocusable(false);

        txt_pekerja_cabut_OM_nama.setEditable(false);
        txt_pekerja_cabut_OM_nama.setBackground(new java.awt.Color(255, 255, 255));
        txt_pekerja_cabut_OM_nama.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_pekerja_cabut_OM_nama.setFocusable(false);

        txt_pekerja_cetak_OM_nama.setEditable(false);
        txt_pekerja_cetak_OM_nama.setBackground(new java.awt.Color(255, 255, 255));
        txt_pekerja_cetak_OM_nama.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_pekerja_cetak_OM_nama.setFocusable(false);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel47, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel50, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel51, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txt_netto, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel36))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(label_netto)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel39)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_LPkaki3))
                    .addComponent(txt_admin)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txt_lp_kaki2, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_tambah_kaki2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel32))
                    .addComponent(button_LPkaki2)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txt_lp_kaki1, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_tambah_kaki1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel28))
                    .addComponent(button_LPkaki1)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_serabut, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_bonggol, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_rontokan, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_hancuran, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_sesekan, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel25)
                            .addComponent(jLabel23)
                            .addComponent(jLabel22)
                            .addComponent(jLabel20)
                            .addComponent(jLabel19)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txt_pekerja_timbang, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(button_pilih_pekerja_timbang, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txt_pekerja_cabut_OM_id, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(button_pilih_pekerja_cabut_OM, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txt_pekerja_cetak_OM_id, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(button_pilih_pekerja_cetak_OM, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txt_pekerja_cabut_OM_nama, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_pekerja_cetak_OM_nama, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_sesekan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_hancuran, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_rontokan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_bonggol, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_serabut, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_LPkaki1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_tambah_kaki1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_lp_kaki1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_LPkaki2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_tambah_kaki2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_lp_kaki2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_admin, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_netto, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_LPkaki3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txt_netto, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_pekerja_timbang, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel47, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_pilih_pekerja_timbang, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_pekerja_cabut_OM_id, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel50, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_pilih_pekerja_cabut_OM, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_pekerja_cabut_OM_nama, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_pekerja_cetak_OM_id, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel51, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_pilih_pekerja_cetak_OM, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_pekerja_cetak_OM_nama, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_no_lp))
                    .addComponent(jLabel2)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(button_save)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(label_no_lp))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_save)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void button_saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_saveActionPerformed
        // TODO add your handling code here:
        Utility.db.getConnection();
        String nama_otorisasi = "";
        String keterangan = "";
        try {
            boolean check = true;
            Utility.db.getConnection().createStatement();

            float netto_sistem = Float.valueOf(label_netto.getText());
            float netto_manual = Float.valueOf(txt_netto.getText());

            float bk = berat_kering_lp;
            float bk_12 = bk * 1.12f;
            float fbonus = Float.valueOf(txt_bk_fbonus.getText());
            float fnol = Float.valueOf(txt_bk_fnol.getText());
            float pecah_flat = Float.valueOf(txt_bk_pecah.getText()) + Float.valueOf(txt_bk_flat.getText());
            float jidun = Float.valueOf(txt_bk_jidun.getText());
            float kpg_jidun = Float.valueOf(txt_jidun_utuh.getText()) + Float.valueOf(txt_jidun_pch.getText());
            float kaki = Float.valueOf(txt_tambah_kaki1.getText()) + Float.valueOf(txt_tambah_kaki2.getText());
            float keping_akhir = Float.valueOf(txt_fbonus.getText()) + Float.valueOf(txt_fnol.getText()) + Float.valueOf(txt_mk_pecah.getText()) + Float.valueOf(txt_flat.getText());
            float utuh = fbonus + fnol;
            float netto_utuh = 0;
            float netto_jidun = 0;
            if (jidun > utuh) {
                netto_jidun = jidun - kaki;
                netto_utuh = utuh;
            } else {
                netto_utuh = utuh - kaki;
                netto_jidun = jidun;
            }

            float sp = Float.valueOf(txt_sesekan.getText()) + Float.valueOf(txt_hancuran.getText()) + Float.valueOf(txt_rontokan.getText()) + Float.valueOf(txt_bonggol.getText()) + Float.valueOf(txt_serabut.getText());
            float sh = bk - (netto_utuh + netto_jidun + pecah_flat + sp);
            float persen_sh = (sh / bk) * 100;

//            System.out.println("keping awal : " + keping_awal);
//            System.out.println("keping akhir : " + keping_akhir);
            if (netto_sistem != netto_manual) {
                check = false;
                JOptionPane.showMessageDialog(this, "Maaf Netto Manual dan Netto Sistem belum cocok !");
            } else if (persen_sh < 0f || persen_sh > 20f) {
                int dialogResult = JOptionPane.showConfirmDialog(this, "Susut hilang(" + Math.round(sh) + "gr " + Math.round(persen_sh) + "%) diluar batas yang ditentukan (0-15%), apakah ingin melanjutkan?", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
//                    JDialog_otorisasi_f2 dialog = new JDialog_otorisasi_f2(new javax.swing.JFrame(), true, "Susut hilang diluar batas yang ditentukan (0-15%)");
//                    dialog.pack();
//                    dialog.setLocationRelativeTo(this);
//                    dialog.setVisible(true);
//                    dialog.setEnabled(true);
////                    System.out.println(dialog.akses());
//                    check = dialog.akses();
//                    nama_otorisasi = dialog.getNama();
//                    keterangan = dialog.getKeterangan();
                } else {
                    check = false;
                }
            } 
//            else if (ruangan.length() == 5) {
//                JOptionPane.showMessageDialog(this, "untuk LP sub tidak ada pengecekan keping");
//            } else if (keping_cetak != keping_akhir) {
//                int dialogResult = JOptionPane.showConfirmDialog(this, "Maaf Jumlah keping CETAK(" + Math.round(keping_cetak) + ") dan F2(" + Math.round(keping_akhir) + ") tidak sama, apakah ingin melanjutkan?", "Warning", 0);
//                if (dialogResult == JOptionPane.YES_OPTION) {
//                    JDialog_otorisasi_f2 dialog = new JDialog_otorisasi_f2(new javax.swing.JFrame(), true, "Jumlah keping CETAK dan F2 tidak sama");
//                    dialog.pack();
//                    dialog.setLocationRelativeTo(this);
//                    dialog.setVisible(true);
//                    dialog.setEnabled(true);
////                    System.out.println(dialog.akses());
//                    check = dialog.akses();
//                    nama_otorisasi = dialog.getNama();
//                    keterangan = dialog.getKeterangan();
//                } else {
//                    check = false;
//                }
//            } else if (jidun_cetak != kpg_jidun) {
//                int dialogResult = JOptionPane.showConfirmDialog(this, "Maaf Jumlah jidun CETAK(" + Math.round(jidun_cetak) + ") dan F2(" + Math.round(kpg_jidun) + ") tidak sama, apakah ingin melanjutkan?", "Warning", 0);
//                if (dialogResult == JOptionPane.YES_OPTION) {
//                    JDialog_otorisasi_f2 dialog = new JDialog_otorisasi_f2(new javax.swing.JFrame(), true, "Jumlah jidun CETAK dan F2 tidak sama");
//                    dialog.pack();
//                    dialog.setLocationRelativeTo(this);
//                    dialog.setVisible(true);
//                    dialog.setEnabled(true);
////                    System.out.println(dialog.akses());
//                    check = dialog.akses();
//                    nama_otorisasi = dialog.getNama();
//                    keterangan = dialog.getKeterangan();
//                } else {
//                    check = false;
//                }
//            }
            Utility.db.getConnection().setAutoCommit(false);
            if (check) {
                String edit_pekerja_cabut_OM = "";
                if (txt_pekerja_cabut_OM_id.getText() != null && !txt_pekerja_cabut_OM_id.getText().equals("")) {
                    edit_pekerja_cabut_OM = "`id_cabut_om`='" + txt_pekerja_cabut_OM_id.getText() + "',";
                }
                String edit_pekerja_cetak_OM = "";
                if (txt_pekerja_cabut_OM_id.getText() != null && !txt_pekerja_cabut_OM_id.getText().equals("")) {
                    edit_pekerja_cetak_OM = "`id_cetak_om`='" + txt_pekerja_cabut_OM_id.getText() + "',";
                }
                String Query = "UPDATE `tb_finishing_2` SET "
                        + "`tgl_setor_f2`='" + dateFormat.format(Date_Setor.getDate()) + "',"
                        + "`f2_disetor`='" + txt_diserahkan.getText() + "',"
                        + "`f2_timbang`='" + txt_pekerja_timbang.getText() + "',"
                        + "`fbonus_f2`='" + txt_fbonus.getText() + "',"
                        + "`berat_fbonus`='" + txt_bk_fbonus.getText() + "',"
                        + "`fnol_f2`='" + txt_fnol.getText() + "',"
                        + "`berat_fnol`='" + txt_bk_fnol.getText() + "',"
                        + "`pecah_f2`='" + txt_mk_pecah.getText() + "',"
                        + "`berat_pecah`='" + txt_bk_pecah.getText() + "',"
                        + "`flat_f2`='" + txt_flat.getText() + "',"
                        + "`berat_flat`='" + txt_bk_flat.getText() + "',"
                        + "`jidun_utuh_f2`='" + txt_jidun_utuh.getText() + "',"
                        + "`jidun_pecah_f2`='" + txt_jidun_pch.getText() + "',"
                        + "`berat_jidun`='" + txt_bk_jidun.getText() + "',"
                        + "`sesekan`='" + txt_sesekan.getText() + "',"
                        + "`hancuran`='" + txt_hancuran.getText() + "',"
                        + "`rontokan`='" + txt_rontokan.getText() + "',"
                        + "`bonggol`='" + txt_bonggol.getText() + "',"
                        + "`serabut`='" + txt_serabut.getText() + "',"
                        + "`tambahan_kaki1`='" + txt_tambah_kaki1.getText() + "',"
                        + "`lp_kaki1`='" + txt_lp_kaki1.getText() + "',"
                        + "`tambahan_kaki2`='" + txt_tambah_kaki2.getText() + "',"
                        + "`lp_kaki2`='" + txt_lp_kaki2.getText() + "',"
                        + "`admin_f2`='" + txt_admin.getText() + "', "
                        + "`otorisasi`='" + nama_otorisasi + "', "
                        + edit_pekerja_cabut_OM
                        + edit_pekerja_cetak_OM
                        + "`keterangan`='" + keterangan + "' "
                        + "WHERE `no_laporan_produksi`='" + label_no_lp.getText() + "'";
                Utility.db.getStatement().executeUpdate(Query);

                sql = "INSERT INTO `tb_lab_laporan_produksi`(`no_laporan_produksi`, `tgl_masuk`) "
                        + "VALUES ('" + label_no_lp.getText() + "','" + dateFormat.format(Date_Setor.getDate()) + "')";
                Utility.db.getStatement().executeUpdate(sql);

                Utility.db.getConnection().commit();
                JOptionPane.showMessageDialog(this, "LP berhasil disetorkan ke QC");
                this.dispose();
            }
        } catch (Exception e) {
            try {
                Utility.db.getConnection().rollback();
            } catch (SQLException ex) {
                Logger.getLogger(JDialog_Setor_LP_F2.class.getName()).log(Level.SEVERE, null, ex);
            }
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(JDialog_Setor_LP_F2.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            try {
                Utility.db.getConnection().setAutoCommit(true);
            } catch (SQLException ex) {
                Logger.getLogger(JDialog_Setor_LP_F2.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_button_saveActionPerformed

    private void button_pick_diserahkanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_pick_diserahkanActionPerformed
        // TODO add your handling code here:
        String filter_tgl = "";
        Browse_Karyawan dialog = new Browse_Karyawan(new javax.swing.JFrame(), true, filter_tgl);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);

        int x = Browse_Karyawan.table_list_karyawan.getSelectedRow();
        if (x != -1) {
            txt_diserahkan.setText(Browse_Karyawan.table_list_karyawan.getValueAt(x, 1).toString());
        }
    }//GEN-LAST:event_button_pick_diserahkanActionPerformed

    private void button_LPkaki1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_LPkaki1ActionPerformed
        // TODO add your handling code here:
        JDialog_Choose_LPkaki dialog = new JDialog_Choose_LPkaki(new javax.swing.JFrame(), true);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);
        txt_lp_kaki1.setText(dialog.get_lpKaki());
        txt_tambah_kaki1.setText(Float.toString(dialog.get_gramKaki()));
    }//GEN-LAST:event_button_LPkaki1ActionPerformed

    private void button_LPkaki2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_LPkaki2ActionPerformed
        // TODO add your handling code here:
        JDialog_Choose_LPkaki dialog = new JDialog_Choose_LPkaki(new javax.swing.JFrame(), true);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);
        txt_lp_kaki2.setText(dialog.get_lpKaki());
        txt_tambah_kaki2.setText(Float.toString(dialog.get_gramKaki()));
    }//GEN-LAST:event_button_LPkaki2ActionPerformed

    private void button_LPkaki3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_LPkaki3ActionPerformed
        // TODO add your handling code here:
        try {
            float fbonus = Float.valueOf(txt_bk_fbonus.getText());
            float fnol = Float.valueOf(txt_bk_fnol.getText());
            float pch = Float.valueOf(txt_bk_pecah.getText());
            float flat = Float.valueOf(txt_bk_flat.getText());
            float jidun = Float.valueOf(txt_bk_jidun.getText());
            float kaki = Float.valueOf(txt_tambah_kaki1.getText()) + Float.valueOf(txt_tambah_kaki2.getText());

            float netto = (fbonus + fnol + pch + flat + jidun) - kaki;
            label_netto.setText(Float.toString(netto));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid Number !");
        }
    }//GEN-LAST:event_button_LPkaki3ActionPerformed

    private void button_pick_pengoreksiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_pick_pengoreksiActionPerformed
        // TODO add your handling code here:
        String filter_tgl = "AND DATE(`att_log`.`scan_date`) = CURRENT_DATE()";
        Browse_Karyawan dialog = new Browse_Karyawan(new javax.swing.JFrame(), true, filter_tgl);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);

        int x = Browse_Karyawan.table_list_karyawan.getSelectedRow();
        if (x != -1) {
            txt_pengoreksi.setText(Browse_Karyawan.table_list_karyawan.getValueAt(x, 1).toString());
        }
    }//GEN-LAST:event_button_pick_pengoreksiActionPerformed

    private void button_pick_f1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_pick_f1ActionPerformed
        // TODO add your handling code here:
        String filter_tgl = "AND DATE(`att_log`.`scan_date`) = CURRENT_DATE()";
        Browse_Karyawan dialog = new Browse_Karyawan(new javax.swing.JFrame(), true, filter_tgl);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);

        int x = Browse_Karyawan.table_list_karyawan.getSelectedRow();
        if (x != -1) {
            txt_pekerja_f1.setText(Browse_Karyawan.table_list_karyawan.getValueAt(x, 1).toString());
        }
    }//GEN-LAST:event_button_pick_f1ActionPerformed

    private void button_pick_f2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_pick_f2ActionPerformed
        // TODO add your handling code here:
        String filter_tgl = "AND DATE(`att_log`.`scan_date`) = CURRENT_DATE()";
        Browse_Karyawan dialog = new Browse_Karyawan(new javax.swing.JFrame(), true, filter_tgl);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);

        int x = Browse_Karyawan.table_list_karyawan.getSelectedRow();
        if (x != -1) {
            txt_pekerja_f2.setText(Browse_Karyawan.table_list_karyawan.getValueAt(x, 1).toString());
        }
    }//GEN-LAST:event_button_pick_f2ActionPerformed

    private void button_pilih_pekerja_timbangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_pilih_pekerja_timbangActionPerformed
        // TODO add your handling code here:
        String filter_tgl = "AND DATE(`att_log`.`scan_date`) = CURRENT_DATE()";
        Browse_Karyawan dialog = new Browse_Karyawan(new javax.swing.JFrame(), true, filter_tgl);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);

        int x = Browse_Karyawan.table_list_karyawan.getSelectedRow();
        if (x != -1) {
            txt_pekerja_timbang.setText(Browse_Karyawan.table_list_karyawan.getValueAt(x, 1).toString());
        }
    }//GEN-LAST:event_button_pilih_pekerja_timbangActionPerformed

    private void txt_fbonusKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_fbonusKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_fbonusKeyTyped

    private void txt_bk_fbonusKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_bk_fbonusKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_bk_fbonusKeyTyped

    private void txt_fnolKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_fnolKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_fnolKeyTyped

    private void txt_bk_fnolKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_bk_fnolKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_bk_fnolKeyTyped

    private void txt_mk_pecahKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_mk_pecahKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_mk_pecahKeyTyped

    private void txt_bk_pecahKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_bk_pecahKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_bk_pecahKeyTyped

    private void txt_flatKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_flatKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_flatKeyTyped

    private void txt_bk_flatKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_bk_flatKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_bk_flatKeyTyped

    private void txt_jidun_utuhKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_jidun_utuhKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_jidun_utuhKeyTyped

    private void txt_bk_jidunKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_bk_jidunKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_bk_jidunKeyTyped

    private void txt_jidun_pchKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_jidun_pchKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_jidun_pchKeyTyped

    private void txt_sesekanKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_sesekanKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_sesekanKeyTyped

    private void txt_hancuranKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_hancuranKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_hancuranKeyTyped

    private void txt_rontokanKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_rontokanKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_rontokanKeyTyped

    private void txt_bonggolKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_bonggolKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_bonggolKeyTyped

    private void txt_serabutKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_serabutKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_serabutKeyTyped

    private void txt_nettoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_nettoKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_nettoKeyTyped

    private void button_pilih_pekerja_cabut_OMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_pilih_pekerja_cabut_OMActionPerformed
        // TODO add your handling code here:
        String filter_tgl = "";
        Browse_Karyawan dialog = new Browse_Karyawan(new javax.swing.JFrame(), true, filter_tgl);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);

        int x = Browse_Karyawan.table_list_karyawan.getSelectedRow();
        if (x != -1) {
            txt_pekerja_cabut_OM_id.setText(Browse_Karyawan.table_list_karyawan.getValueAt(x, 0).toString());
            txt_pekerja_cabut_OM_nama.setText(Browse_Karyawan.table_list_karyawan.getValueAt(x, 1).toString());
        }
    }//GEN-LAST:event_button_pilih_pekerja_cabut_OMActionPerformed

    private void button_pilih_pekerja_cetak_OMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_pilih_pekerja_cetak_OMActionPerformed
        // TODO add your handling code here:
        String filter_tgl = "";
        Browse_Karyawan dialog = new Browse_Karyawan(new javax.swing.JFrame(), true, filter_tgl);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);

        int x = Browse_Karyawan.table_list_karyawan.getSelectedRow();
        if (x != -1) {
            txt_pekerja_cetak_OM_id.setText(Browse_Karyawan.table_list_karyawan.getValueAt(x, 0).toString());
            txt_pekerja_cetak_OM_nama.setText(Browse_Karyawan.table_list_karyawan.getValueAt(x, 1).toString());
        }
    }//GEN-LAST:event_button_pilih_pekerja_cetak_OMActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser Date_Setor;
    private com.toedter.calendar.JDateChooser Date_f1;
    private com.toedter.calendar.JDateChooser Date_f2;
    private com.toedter.calendar.JDateChooser Date_koreksi;
    private javax.swing.JButton button_LPkaki1;
    private javax.swing.JButton button_LPkaki2;
    private javax.swing.JButton button_LPkaki3;
    private javax.swing.JButton button_pick_diserahkan;
    private javax.swing.JButton button_pick_f1;
    private javax.swing.JButton button_pick_f2;
    private javax.swing.JButton button_pick_pengoreksi;
    private javax.swing.JButton button_pilih_pekerja_cabut_OM;
    private javax.swing.JButton button_pilih_pekerja_cetak_OM;
    private javax.swing.JButton button_pilih_pekerja_timbang;
    private javax.swing.JButton button_save;
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
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel label_netto;
    private javax.swing.JLabel label_no_lp;
    private javax.swing.JLabel label_tanggal_selesai;
    private javax.swing.JTextField txt_admin;
    private javax.swing.JTextField txt_bk_fbonus;
    private javax.swing.JTextField txt_bk_flat;
    private javax.swing.JTextField txt_bk_fnol;
    private javax.swing.JTextField txt_bk_jidun;
    private javax.swing.JTextField txt_bk_pecah;
    private javax.swing.JTextField txt_bonggol;
    private javax.swing.JTextField txt_diserahkan;
    private javax.swing.JTextField txt_fbonus;
    private javax.swing.JTextField txt_flat;
    private javax.swing.JTextField txt_fnol;
    private javax.swing.JTextField txt_hancuran;
    private javax.swing.JTextField txt_jidun_pch;
    private javax.swing.JTextField txt_jidun_utuh;
    private javax.swing.JTextField txt_lp_kaki1;
    private javax.swing.JTextField txt_lp_kaki2;
    private javax.swing.JTextField txt_mk_pecah;
    private javax.swing.JTextField txt_netto;
    private javax.swing.JTextField txt_pekerja_cabut_OM_id;
    private javax.swing.JTextField txt_pekerja_cabut_OM_nama;
    private javax.swing.JTextField txt_pekerja_cetak_OM_id;
    private javax.swing.JTextField txt_pekerja_cetak_OM_nama;
    private javax.swing.JTextField txt_pekerja_f1;
    private javax.swing.JTextField txt_pekerja_f2;
    private javax.swing.JTextField txt_pekerja_timbang;
    private javax.swing.JTextField txt_pengoreksi;
    private javax.swing.JTextField txt_rontokan;
    private javax.swing.JTextField txt_serabut;
    private javax.swing.JTextField txt_sesekan;
    private javax.swing.JTextField txt_tambah_kaki1;
    private javax.swing.JTextField txt_tambah_kaki2;
    // End of variables declaration//GEN-END:variables
}
