package waleta_system.QC;

import java.awt.HeadlessException;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import waleta_system.Class.Utility;

public class JDialog_SetorEdit_DataLabLP extends javax.swing.JDialog {

    String sql = null;
    String no_lp = null;
    ResultSet rs;
    Date date = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    String status;
    String status_akhir_qc = "";
//    int selectedRow = 0;

    public JDialog_SetorEdit_DataLabLP(java.awt.Frame parent, boolean modal, String no_lp, String status, String tgl_masuk, String status_awal) {
        super(parent, modal);
        initComponents();
        this.no_lp = no_lp;
        label_lp.setText(this.no_lp);
        this.status = status;
        try {
            Date_masuk.setMinSelectableDate(dateFormat.parse(tgl_masuk));
            Date_uji.setMinSelectableDate(dateFormat.parse(tgl_masuk));
            Date_selesai.setMinSelectableDate(dateFormat.parse(tgl_masuk));
        } catch (ParseException ex) {
            Logger.getLogger(JPanel_Lab_LaporanProduksi.class.getName()).log(Level.SEVERE, null, ex);
        }

        if ("edit".equals(status)) {
            label_judul.setText("Edit Data Lab Laporan Produksi");
            label_tanggal_masuk.setVisible(true);
            Date_masuk.setVisible(true);
            status_akhir_qc = getStatusAkhir(status_awal, no_lp);
            ComboBox_status_Akhir.setSelectedItem(status_akhir_qc);
            getDataEdit(no_lp);
        } else if ("setor".equals(status)) {
            label_judul.setText("Setor Laporan Produksi");
            label_tanggal_masuk.setVisible(false);
            Date_masuk.setVisible(false);
            label_tgl_uji.setVisible(false);
            Date_uji.setVisible(false);
            label_nitrit_utuh.setVisible(false);
            txt_nitrit_utuh.setVisible(false);
            label_nitrit_flat.setVisible(false);
            txt_nitrit_flat.setVisible(false);
            label_jidun.setText("Keping Jidun");
            txt_nitrit_jidun.setEditable(false);
            label_ka.setVisible(false);
            txt_kadar_air.setVisible(false);
            label_kadar_aluminium.setVisible(false);
            txt_kadar_aluminium.setVisible(false);
            label_status.setVisible(false);
            label_status_awal.setVisible(false);
            getDataSetor();
            status_akhir_qc = getStatusAkhir(status_awal, no_lp);
            ComboBox_status_Akhir.setSelectedItem(status_akhir_qc);
        } else if ("input".equals(status)) {
            label_judul.setText("Input Data Lab Laporan Produksi");
            label_tanggal_masuk.setVisible(false);
            Date_masuk.setVisible(false);
            label_tgl_selesai.setVisible(false);
            Date_selesai.setVisible(false);
            label_utuh.setVisible(false);
            txt_utuh.setVisible(false);
            label_pecah.setVisible(false);
            txt_pecah.setVisible(false);
            label_flat.setVisible(false);
            txt_flat.setVisible(false);
            label_kpg_akhir.setVisible(false);
            txt_kpg_akhir.setVisible(false);
            label_gram_akhir.setVisible(false);
            txt_gram_akhir.setVisible(false);
            label_status_akhir.setVisible(false);
            ComboBox_status_Akhir.setVisible(false);
        }
    }

    public void getDataEdit(String no_lp) {
        try {
            sql = "SELECT `no_laporan_produksi`, `tgl_masuk`, `tgl_uji`, `tgl_selesai`, `nitrit_utuh`, `nitrit_flat`, `jidun`, `kadar_air_bahan_jadi`, `utuh`, `pecah`, `flat`, `kpg_akhir`, `gram_akhir`, `status`, `status_akhir`, `kadar_aluminium` "
                    + "FROM `tb_lab_laporan_produksi` "
                    + "WHERE `no_laporan_produksi` = '" + no_lp + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                Date_masuk.setDate(rs.getDate("tgl_masuk"));
                Date_uji.setDate(rs.getDate("tgl_uji"));
                Date_selesai.setDate(rs.getDate("tgl_selesai"));
                txt_nitrit_utuh.setText(rs.getString("nitrit_utuh"));
                txt_nitrit_flat.setText(rs.getString("nitrit_flat"));
                txt_nitrit_jidun.setText(rs.getString("jidun"));
                txt_kadar_air.setText(rs.getString("kadar_air_bahan_jadi"));
                txt_utuh.setText(rs.getString("utuh"));
                txt_pecah.setText(rs.getString("pecah"));
                txt_flat.setText(rs.getString("flat"));
                txt_kpg_akhir.setText(rs.getString("kpg_akhir"));
                txt_gram_akhir.setText(rs.getString("gram_akhir"));
                label_status_awal.setText(rs.getString("status"));
                ComboBox_status_Akhir.setSelectedItem(rs.getString("status_akhir"));
                txt_kadar_aluminium.setText(rs.getString("kadar_aluminium"));
                if (rs.getDate("tgl_selesai") == null) {
                    label_tgl_selesai.setVisible(false);
                    Date_selesai.setVisible(false);
                    label_utuh.setVisible(false);
                    txt_utuh.setVisible(false);
                    label_pecah.setVisible(false);
                    txt_pecah.setVisible(false);
                    label_flat.setVisible(false);
                    txt_flat.setVisible(false);
                    label_kpg_akhir.setVisible(false);
                    txt_kpg_akhir.setVisible(false);
                    label_gram_akhir.setVisible(false);
                    txt_gram_akhir.setVisible(false);
                    label_status_akhir.setVisible(false);
                    ComboBox_status_Akhir.setVisible(false);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JDialog_SetorEdit_DataLabLP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void getDataSetor() {
        try {
//            boolean treatment = true;
//            String a = "SELECT `kode_treatment`, `no_laporan_produksi` FROM `tb_lab_treatment_lp` WHERE `no_laporan_produksi` = '" + no_lp + "'";
//            ResultSet b = Utility.db.getStatement().executeQuery(a);
//            treatment = b.next();
//
//            if (treatment == false) {
            String f2 = "SELECT * FROM `tb_finishing_2` WHERE `no_laporan_produksi` = '" + no_lp + "'";
            ResultSet rs_f2 = Utility.db.getStatement().executeQuery(f2);
            if (rs_f2.next()) {
                int utuh = rs_f2.getInt("fbonus_f2") + rs_f2.getInt("fnol_f2");
                int kpg_akhir = utuh + rs_f2.getInt("pecah_f2") + rs_f2.getInt("flat_f2")
                        + rs_f2.getInt("jidun_utuh_f2") + rs_f2.getInt("jidun_pecah_f2");
                float gram_akhir = rs_f2.getFloat("berat_fbonus") + rs_f2.getFloat("berat_fnol")
                        + rs_f2.getFloat("berat_pecah") + rs_f2.getFloat("berat_flat") + rs_f2.getFloat("berat_jidun");
                txt_utuh.setText(Integer.toString(utuh));
                txt_pecah.setText(Integer.toString(rs_f2.getInt("pecah_f2")));
                txt_flat.setText(Integer.toString(rs_f2.getInt("flat_f2")));
                txt_nitrit_jidun.setText(Integer.toString(rs_f2.getInt("jidun_utuh_f2") + rs_f2.getInt("jidun_pecah_f2")));
                txt_kpg_akhir.setText(Integer.toString(kpg_akhir));
                txt_gram_akhir.setText(Float.toString(gram_akhir));
            }
//            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JDialog_SetorEdit_DataLabLP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getStatusAkhir(String statusAwal, String no_LP) {
        String StatusAkhir = null;
        switch (statusAwal) {
            case "PASSED":
                StatusAkhir = "PASSED";
                break;
            case "HOLD/NON GNS":
                String status_treatment_utuh = "HOLD/NON GNS";
                String status_treatment_flat = "HOLD/NON GNS";
                String status_treatment_jidun = "HOLD/NON GNS";
                try {
                    String c = "SELECT\n"
                            + "(SELECT `status` FROM `tb_lab_treatment_lp` WHERE `no_laporan_produksi` = '$no_lp_cek' AND `jenis_barang` = 'Utuh' ORDER BY `nitrit_akhir` LIMIT 1) AS 'status_utuh',\n"
                            + "(SELECT `status` FROM `tb_lab_treatment_lp` WHERE `no_laporan_produksi` = '$no_lp_cek' AND `jenis_barang` = 'Flat' ORDER BY `nitrit_akhir` LIMIT 1) AS 'status_flat',\n"
                            + "(SELECT `status` FROM `tb_lab_treatment_lp` WHERE `no_laporan_produksi` = '$no_lp_cek' AND `jenis_barang` = 'Jidun' ORDER BY `nitrit_akhir` LIMIT 1) AS 'status_jidun'\n"
                            + "FROM DUAL";
                    rs = Utility.db.getStatement().executeQuery(c);
                    if (rs.next()) {
                        if (rs.getString("status_utuh") != null) {
                            status_treatment_utuh = rs.getString("status_utuh");
                        }
                        if (rs.getString("status_flat") != null) {
                            status_treatment_flat = rs.getString("status_flat");
                        }
                        if (rs.getString("status_jidun") != null) {
                            status_treatment_jidun = rs.getString("status_jidun");
                        }
                    }

                    if ("HOLD/NON GNS".equals(status_treatment_flat) || "HOLD/NON GNS".equals(status_treatment_utuh) || "HOLD/NON GNS".equals(status_treatment_jidun)) {
                        StatusAkhir = "HOLD/NON GNS";
                    } else {
                        StatusAkhir = "PASSED";
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(JPanel_Lab_LaporanProduksi.class.getName()).log(Level.SEVERE, null, ex);
                }

                break;
            case "":
                StatusAkhir = null;
                break;
            default:
                JOptionPane.showMessageDialog(this, "ERROR");
                break;
        }
        return StatusAkhir;
    }

    public void setor() {
        try {
            boolean check = true;
            float utuh = Float.valueOf(txt_utuh.getText());
            float flat = Float.valueOf(txt_flat.getText());
            float pecah = Float.valueOf(txt_pecah.getText());
            int kpg_akhir = Integer.valueOf(txt_kpg_akhir.getText());
            float gram_akhir = Float.valueOf(txt_gram_akhir.getText());
            if (Date_selesai.getDate() == null
                    || txt_kpg_akhir.getText() == null || txt_kpg_akhir.getText().equals("")
                    || txt_gram_akhir.getText() == null || txt_gram_akhir.getText().equals("")) {
                JOptionPane.showMessageDialog(this, "Silahkan lengkapi data diatas!");
                check = false;
            } else if (Integer.valueOf(txt_kpg_akhir.getText()) > Float.valueOf(txt_gram_akhir.getText())) {
                JOptionPane.showMessageDialog(this, "inputan keping dan gram salah, silahkan cek kembali");
                check = false;
            } else if (!ComboBox_status_Akhir.getSelectedItem().toString().equals(status_akhir_qc)) {
                JOptionPane.showMessageDialog(this, "Mengganti manual status akhir memerlukan otorisasi!");
                JDialog_otorisasi_QC dialog = new JDialog_otorisasi_QC(new javax.swing.JFrame(), true, "Staff / Manager", "AND (`tb_karyawan`.`posisi` LIKE 'STAFF%' OR `tb_karyawan`.`posisi` = 'MANAGER') ");
                dialog.pack();
                dialog.setLocationRelativeTo(this);
                dialog.setVisible(true);
                dialog.setEnabled(true);
                check = dialog.akses();
            }

            if (check) {
                sql = "UPDATE `tb_lab_laporan_produksi` SET "
                        + "`tgl_selesai`='" + dateFormat.format(Date_selesai.getDate()) + "',"
                        + "`utuh`='" + utuh + "',"
                        + "`pecah`='" + pecah + "',"
                        + "`flat`='" + flat + "',"
                        + "`kpg_akhir`='" + kpg_akhir + "',"
                        + "`gram_akhir`='" + gram_akhir + "',"
                        + "`status_akhir`='" + ComboBox_status_Akhir.getSelectedItem().toString() + "'"
                        + "WHERE `no_laporan_produksi`='" + label_lp.getText() + "'";

                Utility.db.getConnection().createStatement();
                if ((Utility.db.getStatement().executeUpdate(sql)) == 1) {
                    JOptionPane.showMessageDialog(this, "LP berhasil disetorkan");
                    this.dispose();
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JDialog_SetorEdit_DataLabLP.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void input() {
        try {
            String Query = null;
            String tgl_uji = dateFormat.format(Date_uji.getDate());
            String nitrit_utuh = txt_nitrit_utuh.getText();
            String nitrit_flat = txt_nitrit_flat.getText();
            String kadar_air = txt_kadar_air.getText();
            String jidun = txt_nitrit_jidun.getText();
            String kadar_aluminium = txt_kadar_aluminium.getText();
            if ("".equals(txt_nitrit_utuh.getText()) || txt_nitrit_utuh.getText() == null || txt_nitrit_utuh.getText().equals("0")) {
                nitrit_utuh = "NULL";
            }
            if ("".equals(txt_nitrit_flat.getText()) || txt_nitrit_flat.getText() == null || txt_nitrit_flat.getText().equals("0")) {
                nitrit_flat = "NULL";
            }
            if ("".equals(txt_kadar_air.getText()) || txt_kadar_air.getText() == null || txt_kadar_air.getText().equals("0")) {
                kadar_air = "NULL";
            }
            if ("".equals(txt_nitrit_jidun.getText()) || txt_nitrit_jidun.getText() == null || txt_nitrit_jidun.getText().equals("0")) {
                jidun = "NULL";
            }
            if (txt_kadar_aluminium.getText() == null || "".equals(txt_kadar_aluminium.getText())) {
                kadar_aluminium = "0";
            }
            Query = "UPDATE `tb_lab_laporan_produksi` SET "
                    + "`tgl_uji`='" + tgl_uji + "',"
                    + "`nitrit_utuh`=" + nitrit_utuh + ","
                    + "`nitrit_flat`=" + nitrit_flat + ","
                    + "`kadar_air_bahan_jadi`=" + kadar_air + ","
                    + "`jidun`=" + jidun + ","
                    + "`status`='" + label_status_awal.getText() + "',"
                    + "`kadar_aluminium`=" + kadar_aluminium + ""
                    + "WHERE `no_laporan_produksi`='" + label_lp.getText() + "'";

            Utility.db.getConnection().createStatement();
            if ((Utility.db.getStatement().executeUpdate(Query)) == 1) {
                JOptionPane.showMessageDialog(this, "Data Berhasil disimpan");
                this.dispose();
            }
        } catch (SQLException | HeadlessException | NumberFormatException | NullPointerException e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JDialog_SetorEdit_DataLabLP.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void edit() {
        try {
            boolean check = true;

            String tgl_masuk = dateFormat.format(Date_masuk.getDate());
            String tgl_uji = dateFormat.format(Date_uji.getDate());

            String tgl_selesai = "";
            if (Date_selesai.getDate() != null) {
                tgl_selesai = "`tgl_selesai`='" + dateFormat.format(Date_selesai.getDate()) + "',";
            }
            String utuh = "";
            if (txt_utuh.getText() != null && !"".equals(txt_utuh.getText())) {
                utuh = "`utuh`=" + Float.valueOf(txt_utuh.getText()) + ",";
            }
            String flat = "";
            if (txt_flat.getText() != null && !"".equals(txt_flat.getText())) {
                flat = "`flat`=" + Float.valueOf(txt_flat.getText()) + ",";
            }
            String pecah = "";
            if (txt_pecah.getText() != null && !"".equals(txt_pecah.getText())) {
                pecah = "`pecah`=" + Float.valueOf(txt_pecah.getText()) + ",";
            }
            String kpg_akhir = "";
            if (txt_kpg_akhir.getText() != null && !"".equals(txt_kpg_akhir.getText())) {
                kpg_akhir = "`kpg_akhir`=" + Integer.valueOf(txt_kpg_akhir.getText()) + ",";
            }
            String gram_akhir = "";
            if (txt_gram_akhir.getText() != null && !"".equals(txt_gram_akhir.getText())) {
                gram_akhir = "`gram_akhir`=" + Float.valueOf(txt_gram_akhir.getText()) + ",";
            }

            String nitrit_utuh = txt_nitrit_utuh.getText();
            if ("".equals(txt_nitrit_utuh.getText()) || txt_nitrit_utuh.getText() == null || "-".equals(txt_nitrit_utuh.getText())) {
                nitrit_utuh = "NULL";
            }
            String nitrit_flat = txt_nitrit_flat.getText();
            if ("".equals(txt_nitrit_flat.getText()) || txt_nitrit_flat.getText() == null || "-".equals(txt_nitrit_flat.getText())) {
                nitrit_flat = "NULL";
            }
            String nitrit_jidun = txt_nitrit_jidun.getText();
            if ("".equals(txt_nitrit_jidun.getText()) || txt_nitrit_jidun.getText() == null || "-".equals(txt_nitrit_jidun.getText())) {
                nitrit_jidun = "NULL";
            }
            String kadar_air = txt_kadar_air.getText();
            if ("".equals(txt_kadar_air.getText()) || txt_kadar_air.getText() == null || "-".equals(txt_kadar_air.getText())) {
                kadar_air = "NULL";
            }
            String kadar_aluminium = txt_kadar_aluminium.getText();
            if (txt_kadar_aluminium.getText() == null || "".equals(txt_kadar_aluminium.getText())) {
                kadar_aluminium = "0";
            }

            String status_Akhir = "";
            if (!Date_selesai.isVisible()) {
                tgl_selesai = "";
                utuh = "";
                flat = "";
                pecah = "";
                kpg_akhir = "";
                gram_akhir = "";
                status_Akhir = "";
                if (Date_uji.getDate() == null || Date_masuk.getDate() == null) {
                    JOptionPane.showMessageDialog(this, "Silahkan lengkapi data diatas!");
                    check = false;
                }
            } else {
                status_Akhir = "`status_akhir`='" + ComboBox_status_Akhir.getSelectedItem().toString() + "',";
                if (Date_uji.getDate() == null || Date_masuk.getDate() == null) {
                    JOptionPane.showMessageDialog(this, "Silahkan lengkapi data diatas!");
                    check = false;
                } else if (!ComboBox_status_Akhir.getSelectedItem().toString().equals(status_akhir_qc)) {
                    JOptionPane.showMessageDialog(this, "Mengganti manual status akhir memerlukan otorisasi!");
                    JDialog_otorisasi_QC dialog = new JDialog_otorisasi_QC(new javax.swing.JFrame(), true, "Staff / Manager", "AND (`tb_karyawan`.`posisi` LIKE 'STAFF%' OR `tb_karyawan`.`posisi` = 'MANAGER') ");
                    dialog.pack();
                    dialog.setLocationRelativeTo(this);
                    dialog.setVisible(true);
                    dialog.setEnabled(true);
                    check = dialog.akses();
                }
            }

            if (check) {
                String Query = null;

                Query = "UPDATE `tb_lab_laporan_produksi` SET "
                        + "`tgl_masuk`='" + tgl_masuk + "',"
                        + "`tgl_uji`='" + tgl_uji + "',"
                        + tgl_selesai
                        + "`nitrit_utuh`=" + nitrit_utuh + ","
                        + "`nitrit_flat`=" + nitrit_flat + ","
                        + "`jidun`=" + nitrit_jidun + ","
                        + "`kadar_air_bahan_jadi`=" + kadar_air + ","
                        + utuh
                        + flat
                        + pecah
                        + kpg_akhir
                        + gram_akhir
                        + "`status`='" + label_status_awal.getText() + "',"
                        + status_Akhir
                        + "`kadar_aluminium`=" + kadar_aluminium + " "
                        + "WHERE `no_laporan_produksi`='" + label_lp.getText() + "'";

                Utility.db.getConnection().createStatement();
                if ((Utility.db.getStatement().executeUpdate(Query)) == 1) {
                    JOptionPane.showMessageDialog(this, "LP berhasil diubah");
                    this.dispose();
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JDialog_SetorEdit_DataLabLP.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        Date_selesai = new com.toedter.calendar.JDateChooser();
        label_lp = new javax.swing.JLabel();
        Date_uji = new com.toedter.calendar.JDateChooser();
        jLabel9 = new javax.swing.JLabel();
        label_pecah = new javax.swing.JLabel();
        txt_utuh = new javax.swing.JTextField();
        label_status_awal1 = new javax.swing.JLabel();
        button_save = new javax.swing.JButton();
        ComboBox_status_Akhir = new javax.swing.JComboBox<>();
        txt_nitrit_utuh = new javax.swing.JTextField();
        txt_kpg_akhir = new javax.swing.JTextField();
        label_status_awal = new javax.swing.JLabel();
        label_status_akhir1 = new javax.swing.JLabel();
        label_utuh = new javax.swing.JLabel();
        label_tgl_selesai = new javax.swing.JLabel();
        Date_masuk = new com.toedter.calendar.JDateChooser();
        label_ka = new javax.swing.JLabel();
        txt_pecah = new javax.swing.JTextField();
        txt_flat = new javax.swing.JTextField();
        label_nitrit_flat = new javax.swing.JLabel();
        label_tgl_uji = new javax.swing.JLabel();
        txt_nitrit_flat = new javax.swing.JTextField();
        label_gram_akhir = new javax.swing.JLabel();
        label_status_akhir = new javax.swing.JLabel();
        label_flat = new javax.swing.JLabel();
        txt_kadar_air = new javax.swing.JTextField();
        label_status = new javax.swing.JLabel();
        label_nitrit_utuh = new javax.swing.JLabel();
        label_jidun = new javax.swing.JLabel();
        label_kpg_akhir = new javax.swing.JLabel();
        label_tanggal_masuk = new javax.swing.JLabel();
        txt_gram_akhir = new javax.swing.JTextField();
        txt_nitrit_jidun = new javax.swing.JTextField();
        label_judul = new javax.swing.JLabel();
        label_kadar_aluminium = new javax.swing.JLabel();
        txt_kadar_aluminium = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Bagian Lab");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        Date_selesai.setBackground(new java.awt.Color(255, 255, 255));
        Date_selesai.setDateFormatString("dd MMMM yyyy");
        Date_selesai.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Date_selesai.setMaxSelectableDate(new Date());

        label_lp.setBackground(new java.awt.Color(255, 255, 255));
        label_lp.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_lp.setText("-");

        Date_uji.setBackground(new java.awt.Color(255, 255, 255));
        Date_uji.setDateFormatString("dd MMMM yyyy");
        Date_uji.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Date_uji.setMaxSelectableDate(new Date());

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel9.setText("No Laporan Produksi :");

        label_pecah.setBackground(new java.awt.Color(255, 255, 255));
        label_pecah.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_pecah.setText("Keping Pecah :");

        txt_utuh.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_utuh.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_utuhKeyTyped(evt);
            }
        });

        label_status_awal1.setBackground(new java.awt.Color(255, 255, 255));
        label_status_awal1.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_status_awal1.setText("21");

        button_save.setBackground(new java.awt.Color(255, 255, 255));
        button_save.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_save.setText("Save");
        button_save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_saveActionPerformed(evt);
            }
        });

        ComboBox_status_Akhir.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "PASSED", "HOLD/NON GNS" }));

        txt_nitrit_utuh.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_nitrit_utuh.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_nitrit_utuhFocusLost(evt);
            }
        });
        txt_nitrit_utuh.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_nitrit_utuhKeyTyped(evt);
            }
        });

        txt_kpg_akhir.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_kpg_akhir.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_kpg_akhirKeyTyped(evt);
            }
        });

        label_status_awal.setBackground(new java.awt.Color(255, 255, 255));
        label_status_awal.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_status_awal.setText("-");

        label_status_akhir1.setBackground(new java.awt.Color(255, 255, 255));
        label_status_akhir1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_status_akhir1.setText("MAX Nitrit :");

        label_utuh.setBackground(new java.awt.Color(255, 255, 255));
        label_utuh.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_utuh.setText("Keping Utuh :");

        label_tgl_selesai.setBackground(new java.awt.Color(255, 255, 255));
        label_tgl_selesai.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_tgl_selesai.setText("Tanggal Selesai :");

        Date_masuk.setBackground(new java.awt.Color(255, 255, 255));
        Date_masuk.setDateFormatString("dd MMMM yyyy");
        Date_masuk.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Date_masuk.setMaxSelectableDate(new Date());

        label_ka.setBackground(new java.awt.Color(255, 255, 255));
        label_ka.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_ka.setText("Kadar air bahan jadi :");

        txt_pecah.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_pecah.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_pecahKeyTyped(evt);
            }
        });

        txt_flat.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_flat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_flatKeyTyped(evt);
            }
        });

        label_nitrit_flat.setBackground(new java.awt.Color(255, 255, 255));
        label_nitrit_flat.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_nitrit_flat.setText("Nitrit Flat :");

        label_tgl_uji.setBackground(new java.awt.Color(255, 255, 255));
        label_tgl_uji.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_tgl_uji.setText("Tanggal Uji :");

        txt_nitrit_flat.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_nitrit_flat.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_nitrit_flatFocusLost(evt);
            }
        });
        txt_nitrit_flat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_nitrit_flatKeyTyped(evt);
            }
        });

        label_gram_akhir.setBackground(new java.awt.Color(255, 255, 255));
        label_gram_akhir.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_gram_akhir.setText("Gram Akhir :");

        label_status_akhir.setBackground(new java.awt.Color(255, 255, 255));
        label_status_akhir.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_status_akhir.setText("Status terakhir :");

        label_flat.setBackground(new java.awt.Color(255, 255, 255));
        label_flat.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_flat.setText("Keping Flat :");

        txt_kadar_air.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_kadar_air.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_kadar_airKeyTyped(evt);
            }
        });

        label_status.setBackground(new java.awt.Color(255, 255, 255));
        label_status.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_status.setText("Status :");

        label_nitrit_utuh.setBackground(new java.awt.Color(255, 255, 255));
        label_nitrit_utuh.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_nitrit_utuh.setText("Nitrit Utuh :");

        label_jidun.setBackground(new java.awt.Color(255, 255, 255));
        label_jidun.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_jidun.setText("Nitrit Jidun :");

        label_kpg_akhir.setBackground(new java.awt.Color(255, 255, 255));
        label_kpg_akhir.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_kpg_akhir.setText("Keping Akhir :");

        label_tanggal_masuk.setBackground(new java.awt.Color(255, 255, 255));
        label_tanggal_masuk.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_tanggal_masuk.setText("Tanggal Masuk :");

        txt_gram_akhir.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_gram_akhir.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_gram_akhirKeyTyped(evt);
            }
        });

        txt_nitrit_jidun.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_nitrit_jidun.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_nitrit_jidunFocusLost(evt);
            }
        });
        txt_nitrit_jidun.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_nitrit_jidunKeyTyped(evt);
            }
        });

        label_judul.setBackground(new java.awt.Color(255, 255, 255));
        label_judul.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_judul.setText("Setor Laporan Produksi ke Bagian Grading");

        label_kadar_aluminium.setBackground(new java.awt.Color(255, 255, 255));
        label_kadar_aluminium.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_kadar_aluminium.setText("Kadar Aluminium :");

        txt_kadar_aluminium.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_kadar_aluminium.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_kadar_aluminiumKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(label_kadar_aluminium, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txt_kadar_aluminium, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(label_pecah, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_gram_akhir, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_tgl_uji, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_nitrit_flat, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_ka, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_tgl_selesai, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_utuh, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_jidun, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_nitrit_utuh, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_status, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_flat, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_status_akhir1, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_status_akhir, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_tanggal_masuk, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_kpg_akhir, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txt_nitrit_utuh, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(ComboBox_status_Akhir, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txt_utuh, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(Date_uji, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(Date_selesai, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txt_nitrit_flat, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txt_flat, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txt_pecah, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(Date_masuk, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txt_kadar_air, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_status_awal, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txt_kpg_akhir, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txt_nitrit_jidun, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txt_gram_akhir, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(label_judul)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(182, 182, 182)
                                .addComponent(label_status_awal1)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(button_save, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label_judul, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(Date_uji, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_tgl_uji, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(Date_masuk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_tanggal_masuk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_nitrit_utuh, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_nitrit_utuh, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_nitrit_flat, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_nitrit_flat, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(label_ka, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_kadar_air, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(label_jidun, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_nitrit_jidun, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(label_status, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_status_awal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(Date_selesai, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_tgl_selesai, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_utuh, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_utuh, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(label_pecah, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_pecah, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_flat, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_flat, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_kpg_akhir, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_kpg_akhir, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(label_gram_akhir, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_gram_akhir, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(ComboBox_status_Akhir, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_status_akhir, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(label_status_awal1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_status_akhir1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_kadar_aluminium, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_kadar_aluminium, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_save, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
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
        if (null != status) {
            switch (status) {
                case "edit":
                    edit();
                    break;
                case "setor":
                    setor();
                    break;
                case "input":
                    input();
                    break;
                default:
                    break;
            }
        }
    }//GEN-LAST:event_button_saveActionPerformed

    private void txt_nitrit_utuhFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_nitrit_utuhFocusLost
        // TODO add your handling code here:
        float nitrit_utuh = 0, nitrit_flat = 0, nitrit_jidun = 0;
        try {
            nitrit_utuh = Float.valueOf(txt_nitrit_utuh.getText());
        } catch (NumberFormatException e) {
        }
        try {
            nitrit_flat = Float.valueOf(txt_nitrit_flat.getText());
        } catch (NumberFormatException e) {
        }
        try {
            nitrit_jidun = Float.valueOf(txt_nitrit_jidun.getText());
        } catch (NumberFormatException e) {
        }
        if (nitrit_utuh > 25 || nitrit_flat > 25 || nitrit_jidun > 25) {
            label_status_awal.setText("HOLD/NON GNS");
        }
    }//GEN-LAST:event_txt_nitrit_utuhFocusLost

    private void txt_nitrit_flatFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_nitrit_flatFocusLost
        // TODO add your handling code here:
        float nitrit_utuh = 0, nitrit_flat = 0, nitrit_jidun = 0;
        try {
            nitrit_utuh = Float.valueOf(txt_nitrit_utuh.getText());
        } catch (NumberFormatException e) {
        }
        try {
            nitrit_flat = Float.valueOf(txt_nitrit_flat.getText());
        } catch (NumberFormatException e) {
        }
        try {
            nitrit_jidun = Float.valueOf(txt_nitrit_jidun.getText());
        } catch (NumberFormatException e) {
        }
        if (nitrit_utuh > 25 || nitrit_flat > 25 || nitrit_jidun > 25) {
            label_status_awal.setText("HOLD/NON GNS");
        }
    }//GEN-LAST:event_txt_nitrit_flatFocusLost

    private void txt_nitrit_jidunFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_nitrit_jidunFocusLost
        // TODO add your handling code here:
        float nitrit_utuh = 0, nitrit_flat = 0, nitrit_jidun = 0;
        try {
            nitrit_utuh = Float.valueOf(txt_nitrit_utuh.getText());
        } catch (NumberFormatException e) {
        }
        try {
            nitrit_flat = Float.valueOf(txt_nitrit_flat.getText());
        } catch (NumberFormatException e) {
        }
        try {
            nitrit_jidun = Float.valueOf(txt_nitrit_jidun.getText());
        } catch (NumberFormatException e) {
        }
        if (nitrit_utuh > 25 || nitrit_flat > 25 || nitrit_jidun > 25) {
            label_status_awal.setText("HOLD/NON GNS");
        }
    }//GEN-LAST:event_txt_nitrit_jidunFocusLost

    private void txt_nitrit_utuhKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_nitrit_utuhKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_nitrit_utuhKeyTyped

    private void txt_nitrit_flatKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_nitrit_flatKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_nitrit_flatKeyTyped

    private void txt_kadar_airKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_kadar_airKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_kadar_airKeyTyped

    private void txt_nitrit_jidunKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_nitrit_jidunKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_nitrit_jidunKeyTyped

    private void txt_utuhKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_utuhKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_utuhKeyTyped

    private void txt_pecahKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_pecahKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_pecahKeyTyped

    private void txt_flatKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_flatKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_flatKeyTyped

    private void txt_kpg_akhirKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_kpg_akhirKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_kpg_akhirKeyTyped

    private void txt_gram_akhirKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_gram_akhirKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_gram_akhirKeyTyped

    private void txt_kadar_aluminiumKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_kadar_aluminiumKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_kadar_aluminiumKeyTyped

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_status_Akhir;
    private com.toedter.calendar.JDateChooser Date_masuk;
    private com.toedter.calendar.JDateChooser Date_selesai;
    private com.toedter.calendar.JDateChooser Date_uji;
    private javax.swing.JButton button_save;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel label_flat;
    private javax.swing.JLabel label_gram_akhir;
    private javax.swing.JLabel label_jidun;
    private javax.swing.JLabel label_judul;
    private javax.swing.JLabel label_ka;
    private javax.swing.JLabel label_kadar_aluminium;
    private javax.swing.JLabel label_kpg_akhir;
    private javax.swing.JLabel label_lp;
    private javax.swing.JLabel label_nitrit_flat;
    private javax.swing.JLabel label_nitrit_utuh;
    private javax.swing.JLabel label_pecah;
    private javax.swing.JLabel label_status;
    private javax.swing.JLabel label_status_akhir;
    private javax.swing.JLabel label_status_akhir1;
    private javax.swing.JLabel label_status_awal;
    private javax.swing.JLabel label_status_awal1;
    private javax.swing.JLabel label_tanggal_masuk;
    private javax.swing.JLabel label_tgl_selesai;
    private javax.swing.JLabel label_tgl_uji;
    private javax.swing.JLabel label_utuh;
    private javax.swing.JTextField txt_flat;
    private javax.swing.JTextField txt_gram_akhir;
    private javax.swing.JTextField txt_kadar_air;
    private javax.swing.JTextField txt_kadar_aluminium;
    private javax.swing.JTextField txt_kpg_akhir;
    private javax.swing.JTextField txt_nitrit_flat;
    private javax.swing.JTextField txt_nitrit_jidun;
    private javax.swing.JTextField txt_nitrit_utuh;
    private javax.swing.JTextField txt_pecah;
    private javax.swing.JTextField txt_utuh;
    // End of variables declaration//GEN-END:variables
}
