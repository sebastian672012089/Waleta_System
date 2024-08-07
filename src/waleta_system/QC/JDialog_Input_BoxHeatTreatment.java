package waleta_system.QC;

import java.awt.HeadlessException;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import waleta_system.Browse_Karyawan;
import waleta_system.Class.Utility;

public class JDialog_Input_BoxHeatTreatment extends javax.swing.JDialog {

    String sql = null;
    String no_lp = null;
    ResultSet rs;
    Date date = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public JDialog_Input_BoxHeatTreatment(java.awt.Frame parent, boolean modal, String no) {
        super(parent, modal);
        initComponents();
        label_no.setText(no);
        get_data_edit();
    }

    public void get_data_edit() {
        try {
            sql = "SELECT `tb_heat_treatment_pengiriman`.`no`, `tb_heat_treatment_pengiriman`.`no_box`, `tb_heat_treatment_pengiriman`.`keping`, `tb_heat_treatment_pengiriman`.`gram`, `operator_heat_treatment`, `nama_pegawai`, "
                    + "`suhu_ruang`, `suhu_sarang_awal`, `waktu_preheat`, `suhu_preheat`, `suhu_akhir`, `waktu_heat_treatment`, `tb_heat_treatment_pengiriman`.`keterangan`, `waktu_input`, "
                    + "`tb_grade_bahan_jadi`.`kode_grade`, `tb_box_bahan_jadi`.`keping`, `tb_box_bahan_jadi`.`berat`, "
                    + "`invoice_pengiriman`, `tb_spk_detail`.`kode_spk`, `tb_spk_detail`.`grade_buyer` "
                    + "FROM `tb_heat_treatment_pengiriman` "
                    + "LEFT JOIN `tb_karyawan` ON `tb_heat_treatment_pengiriman`.`operator_heat_treatment` = `tb_karyawan`.`id_pegawai`\n"
                    + "LEFT JOIN `tb_box_packing` ON `tb_box_packing`.`no_box` = `tb_heat_treatment_pengiriman`.`no_box`\n"
                    + "LEFT JOIN `tb_box_bahan_jadi` ON `tb_box_packing`.`no_box` = `tb_box_bahan_jadi`.`no_box`\n"
                    + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_box_bahan_jadi`.`kode_grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode`"
                    + "LEFT JOIN `tb_spk_detail` ON `tb_box_packing`.`no_grade_spk` = `tb_spk_detail`.`no`"
                    + "WHERE `tb_heat_treatment_pengiriman`.`no` = '" + label_no.getText() + "' ";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                txt_no_box.setText(rs.getString("no_box"));
                txt_keping.setText(rs.getString("keping"));
                txt_gram.setText(rs.getString("gram"));
                txt_operator_id.setText(rs.getString("operator_heat_treatment"));
                txt_operator_nama.setText(rs.getString("nama_pegawai"));
                label_grade_buyer.setText(rs.getString("grade_buyer"));
                label_grade_waleta.setText(rs.getString("kode_grade"));
                label_no_invoice.setText(rs.getString("invoice_pengiriman"));
                txt_suhu_ruang.setText(rs.getString("suhu_ruang"));
                txt_suhu_sarang_awal.setText(rs.getString("suhu_sarang_awal"));
                if (rs.getString("waktu_preheat") != null) {
                    Spinner_preheat_jam.setValue(Integer.valueOf(rs.getString("waktu_preheat").split(":")[0]));
                    Spinner_preheat_menit.setValue(Integer.valueOf(rs.getString("waktu_preheat").split(":")[1]));
                    Spinner_preheat_detik.setValue(Integer.valueOf(rs.getString("waktu_preheat").split(":")[2]));
                }
                txt_suhu_preheat.setText(rs.getString("suhu_preheat"));
                txt_suhu_akhir.setText(rs.getString("suhu_akhir"));
                if (rs.getString("waktu_heat_treatment") != null) {
                    Spinner_heat_treatment_jam.setValue(Integer.valueOf(rs.getString("waktu_heat_treatment").split(":")[0]));
                    Spinner_heat_treatment_menit.setValue(Integer.valueOf(rs.getString("waktu_heat_treatment").split(":")[1]));
                    Spinner_heat_treatment_detik.setValue(Integer.valueOf(rs.getString("waktu_heat_treatment").split(":")[2]));
                }
                txt_keterangan.setText(rs.getString("keterangan"));
                if (rs.getString("waktu_input") != null) {
                    Spinner_waktu_input_jam.setValue(Integer.valueOf(rs.getString("waktu_input").split(":")[0]));
                    Spinner_waktu_input_menit.setValue(Integer.valueOf(rs.getString("waktu_input").split(":")[1]));
                    Spinner_waktu_input_detik.setValue(Integer.valueOf(rs.getString("waktu_input").split(":")[2]));
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JDialog_Input_BoxHeatTreatment.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void edit() {
        try {
            if (txt_keping.getText() == null || txt_keping.getText().equals("")
                    || txt_gram.getText() == null || txt_gram.getText().equals("")
                    || txt_operator_id.getText() == null || txt_operator_id.getText().equals("")
                    || txt_suhu_ruang.getText() == null || txt_suhu_ruang.getText().equals("")
                    || txt_suhu_sarang_awal.getText() == null || txt_suhu_sarang_awal.getText().equals("")
                    || txt_suhu_preheat.getText() == null || txt_suhu_preheat.getText().equals("")
                    || txt_suhu_akhir.getText() == null || txt_suhu_akhir.getText().equals("")) {
                JOptionPane.showMessageDialog(this, "Silahkan lengkapi data diatas!!");
            } else {
                String waktu_preheat = String.format("%02d", Spinner_preheat_jam.getValue()) + ":" + String.format("%02d", Spinner_preheat_menit.getValue()) + ":" + String.format("%02d", Spinner_preheat_detik.getValue());
                String waktu_heat_treatment = String.format("%02d", Spinner_heat_treatment_jam.getValue()) + ":" + String.format("%02d", Spinner_heat_treatment_menit.getValue()) + ":" + String.format("%02d", Spinner_heat_treatment_detik.getValue());
                String waktu_input = String.format("%02d", Spinner_waktu_input_jam.getValue()) + ":" + String.format("%02d", Spinner_waktu_input_menit.getValue()) + ":" + String.format("%02d", Spinner_waktu_input_detik.getValue());
                sql = "UPDATE `tb_heat_treatment_pengiriman` SET "
                        + "`keping`=" + Float.valueOf(txt_keping.getText()) + ","
                        + "`gram`=" + Float.valueOf(txt_gram.getText()) + ", "
                        + "`operator_heat_treatment`='" + txt_operator_id.getText() + "', "
                        + "`suhu_ruang`=" + Float.valueOf(txt_suhu_ruang.getText()) + ", "
                        + "`suhu_sarang_awal`=" + Float.valueOf(txt_suhu_sarang_awal.getText()) + ", "
                        + "`waktu_preheat`='" + waktu_preheat + "', "
                        + "`suhu_preheat`=" + Float.valueOf(txt_suhu_preheat.getText()) + ", "
                        + "`suhu_akhir`=" + Float.valueOf(txt_suhu_akhir.getText()) + ", "
                        + "`waktu_heat_treatment`='" + waktu_heat_treatment + "', "
                        + "`keterangan`='" + txt_keterangan.getText() + "', "
                        + "`waktu_input`='" + waktu_input + "' "
                        + "WHERE `no`='" + label_no.getText() + "'";
                Utility.db.getConnection().createStatement();
                Utility.db.getStatement().executeUpdate(sql);
                JOptionPane.showMessageDialog(this, "Data Berhasil disimpan");
                this.dispose();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JDialog_Input_BoxHeatTreatment.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        label_no_spk = new javax.swing.JLabel();
        label_grade_buyer = new javax.swing.JLabel();
        label_grade_waleta = new javax.swing.JLabel();
        label_no_invoice = new javax.swing.JLabel();
        txt_suhu_ruang = new javax.swing.JTextField();
        txt_suhu_sarang_awal = new javax.swing.JTextField();
        button_save = new javax.swing.JButton();
        button_cancel = new javax.swing.JButton();
        jLabel18 = new javax.swing.JLabel();
        button_pick_diserahkan = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        txt_operator_id = new javax.swing.JTextField();
        txt_operator_nama = new javax.swing.JTextField();
        txt_suhu_preheat = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        txt_suhu_akhir = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        txt_keterangan = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        Spinner_preheat_jam = new javax.swing.JSpinner();
        Spinner_preheat_menit = new javax.swing.JSpinner();
        Spinner_preheat_detik = new javax.swing.JSpinner();
        Spinner_heat_treatment_jam = new javax.swing.JSpinner();
        Spinner_heat_treatment_menit = new javax.swing.JSpinner();
        Spinner_heat_treatment_detik = new javax.swing.JSpinner();
        jLabel14 = new javax.swing.JLabel();
        txt_no_box = new javax.swing.JTextField();
        label_no = new javax.swing.JLabel();
        txt_gram = new javax.swing.JTextField();
        txt_keping = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        Spinner_waktu_input_jam = new javax.swing.JSpinner();
        Spinner_waktu_input_menit = new javax.swing.JSpinner();
        Spinner_waktu_input_detik = new javax.swing.JSpinner();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Bagian Lab");
        setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel1.setText("EDIT Data Heat Treatment");

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel9.setText("No :");

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel10.setText("Keping :");

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel11.setText("Grade Buyer :");

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel12.setText("Grade WLT :");

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel13.setText("No Invoice :");

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel15.setText("Suhu Ruang °C :");

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel16.setText("Suhu Sarang Awal °C :");

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel17.setText("No SPK :");

        label_no_spk.setBackground(new java.awt.Color(255, 255, 255));
        label_no_spk.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_no_spk.setText("-");

        label_grade_buyer.setBackground(new java.awt.Color(255, 255, 255));
        label_grade_buyer.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_grade_buyer.setText("-");

        label_grade_waleta.setBackground(new java.awt.Color(255, 255, 255));
        label_grade_waleta.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_grade_waleta.setText("-");

        label_no_invoice.setBackground(new java.awt.Color(255, 255, 255));
        label_no_invoice.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_no_invoice.setText("-");

        txt_suhu_ruang.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_suhu_ruang.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_suhu_ruangKeyTyped(evt);
            }
        });

        txt_suhu_sarang_awal.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_suhu_sarang_awal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_suhu_sarang_awalKeyTyped(evt);
            }
        });

        button_save.setBackground(new java.awt.Color(255, 255, 255));
        button_save.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_save.setText("Save");
        button_save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_saveActionPerformed(evt);
            }
        });

        button_cancel.setBackground(new java.awt.Color(255, 255, 255));
        button_cancel.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_cancel.setText("Cancel");
        button_cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_cancelActionPerformed(evt);
            }
        });

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel18.setText("Gram :");

        button_pick_diserahkan.setBackground(new java.awt.Color(255, 255, 255));
        button_pick_diserahkan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_pick_diserahkan.setText("...");
        button_pick_diserahkan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_pick_diserahkanActionPerformed(evt);
            }
        });

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel6.setText("Operator :");

        txt_operator_id.setEditable(false);
        txt_operator_id.setBackground(new java.awt.Color(255, 255, 255));
        txt_operator_id.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        txt_operator_nama.setEditable(false);
        txt_operator_nama.setBackground(new java.awt.Color(255, 255, 255));
        txt_operator_nama.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        txt_suhu_preheat.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_suhu_preheat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_suhu_preheatKeyTyped(evt);
            }
        });

        jLabel19.setBackground(new java.awt.Color(255, 255, 255));
        jLabel19.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel19.setText("Suhu Preheat °C :");

        jLabel20.setBackground(new java.awt.Color(255, 255, 255));
        jLabel20.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel20.setText("Suhu Akhir °C :");

        txt_suhu_akhir.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_suhu_akhir.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_suhu_akhirKeyTyped(evt);
            }
        });

        jLabel21.setBackground(new java.awt.Color(255, 255, 255));
        jLabel21.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel21.setText("Waktu Preheat :");

        jLabel22.setBackground(new java.awt.Color(255, 255, 255));
        jLabel22.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel22.setText("Keterangan :");

        txt_keterangan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel23.setBackground(new java.awt.Color(255, 255, 255));
        jLabel23.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel23.setText("Waktu Heat Treatment :");

        Spinner_preheat_jam.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Spinner_preheat_jam.setModel(new javax.swing.SpinnerNumberModel(0, 0, null, 1));

        Spinner_preheat_menit.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Spinner_preheat_menit.setModel(new javax.swing.SpinnerNumberModel(0, 0, 59, 1));

        Spinner_preheat_detik.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Spinner_preheat_detik.setModel(new javax.swing.SpinnerNumberModel(0, 0, 59, 1));

        Spinner_heat_treatment_jam.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Spinner_heat_treatment_jam.setModel(new javax.swing.SpinnerNumberModel(0, 0, null, 1));

        Spinner_heat_treatment_menit.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Spinner_heat_treatment_menit.setModel(new javax.swing.SpinnerNumberModel(0, 0, 59, 1));

        Spinner_heat_treatment_detik.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Spinner_heat_treatment_detik.setModel(new javax.swing.SpinnerNumberModel(0, 0, 59, 1));

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel14.setText("No Box :");

        txt_no_box.setEditable(false);
        txt_no_box.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_no_box.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_no_boxFocusLost(evt);
            }
        });
        txt_no_box.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_no_boxKeyPressed(evt);
            }
        });

        label_no.setBackground(new java.awt.Color(255, 255, 255));
        label_no.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_no.setText("0");

        txt_gram.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        txt_keping.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel24.setBackground(new java.awt.Color(255, 255, 255));
        jLabel24.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel24.setText("Waktu Input :");

        Spinner_waktu_input_jam.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Spinner_waktu_input_jam.setModel(new javax.swing.SpinnerNumberModel(0, 0, null, 1));

        Spinner_waktu_input_menit.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Spinner_waktu_input_menit.setModel(new javax.swing.SpinnerNumberModel(0, 0, 59, 1));

        Spinner_waktu_input_detik.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Spinner_waktu_input_detik.setModel(new javax.swing.SpinnerNumberModel(0, 0, 59, 1));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_gram)
                            .addComponent(txt_keping)
                            .addComponent(label_no_spk, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(label_grade_buyer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(label_grade_waleta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(label_no_invoice, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txt_no_box)
                            .addComponent(label_no, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txt_suhu_ruang)
                            .addComponent(txt_suhu_sarang_awal)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txt_operator_id)
                                .addGap(0, 0, 0)
                                .addComponent(button_pick_diserahkan, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txt_operator_nama)
                            .addComponent(txt_suhu_preheat)
                            .addComponent(txt_suhu_akhir)
                            .addComponent(txt_keterangan)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(Spinner_preheat_jam, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(Spinner_preheat_menit, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(Spinner_preheat_detik, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(Spinner_heat_treatment_jam, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(Spinner_heat_treatment_menit, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(Spinner_heat_treatment_detik, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(Spinner_waktu_input_jam, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(Spinner_waktu_input_menit, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(Spinner_waktu_input_detik, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(button_cancel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_save, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addComponent(txt_no_box, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txt_keping, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_gram, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_no, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(label_no_spk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_grade_buyer, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_grade_waleta, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_no_invoice, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_operator_id, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_pick_diserahkan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_operator_nama, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_suhu_ruang, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txt_suhu_sarang_awal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Spinner_preheat_jam, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Spinner_preheat_menit, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Spinner_preheat_detik, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txt_suhu_preheat, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txt_suhu_akhir, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Spinner_heat_treatment_jam, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Spinner_heat_treatment_menit, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Spinner_heat_treatment_detik, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txt_keterangan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Spinner_waktu_input_jam, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Spinner_waktu_input_menit, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Spinner_waktu_input_detik, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_save)
                    .addComponent(button_cancel))
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
        edit();
    }//GEN-LAST:event_button_saveActionPerformed

    private void button_cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_cancelActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_button_cancelActionPerformed

    private void button_pick_diserahkanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_pick_diserahkanActionPerformed
        // TODO add your handling code here:
        String filter_tgl = "";
        Browse_Karyawan dialog = new Browse_Karyawan(new javax.swing.JFrame(), true, filter_tgl);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);
        int x = waleta_system.Browse_Karyawan.table_list_karyawan.getSelectedRow();
        if (x != -1) {
            txt_operator_id.setText(waleta_system.Browse_Karyawan.table_list_karyawan.getValueAt(x, 0).toString());
            txt_operator_nama.setText(waleta_system.Browse_Karyawan.table_list_karyawan.getValueAt(x, 1).toString());
        }
    }//GEN-LAST:event_button_pick_diserahkanActionPerformed

    private void txt_no_boxFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_no_boxFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_no_boxFocusLost

    private void txt_no_boxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_no_boxKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_no_boxKeyPressed

    private void txt_suhu_ruangKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_suhu_ruangKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_suhu_ruangKeyTyped

    private void txt_suhu_sarang_awalKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_suhu_sarang_awalKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_suhu_sarang_awalKeyTyped

    private void txt_suhu_preheatKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_suhu_preheatKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_suhu_preheatKeyTyped

    private void txt_suhu_akhirKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_suhu_akhirKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_suhu_akhirKeyTyped

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSpinner Spinner_heat_treatment_detik;
    private javax.swing.JSpinner Spinner_heat_treatment_jam;
    private javax.swing.JSpinner Spinner_heat_treatment_menit;
    private javax.swing.JSpinner Spinner_preheat_detik;
    private javax.swing.JSpinner Spinner_preheat_jam;
    private javax.swing.JSpinner Spinner_preheat_menit;
    private javax.swing.JSpinner Spinner_waktu_input_detik;
    private javax.swing.JSpinner Spinner_waktu_input_jam;
    private javax.swing.JSpinner Spinner_waktu_input_menit;
    private javax.swing.JButton button_cancel;
    private javax.swing.JButton button_pick_diserahkan;
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
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel label_grade_buyer;
    private javax.swing.JLabel label_grade_waleta;
    private javax.swing.JLabel label_no;
    private javax.swing.JLabel label_no_invoice;
    private javax.swing.JLabel label_no_spk;
    private javax.swing.JTextField txt_gram;
    private javax.swing.JTextField txt_keping;
    private javax.swing.JTextField txt_keterangan;
    private javax.swing.JTextField txt_no_box;
    private javax.swing.JTextField txt_operator_id;
    private javax.swing.JTextField txt_operator_nama;
    private javax.swing.JTextField txt_suhu_akhir;
    private javax.swing.JTextField txt_suhu_preheat;
    private javax.swing.JTextField txt_suhu_ruang;
    private javax.swing.JTextField txt_suhu_sarang_awal;
    // End of variables declaration//GEN-END:variables
}
