package waleta_system.HRD;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import waleta_system.Class.Utility;
import waleta_system.MainForm;

public class JDialog_PindahGrup_disetujui extends javax.swing.JDialog {

    String sql = null;
    ResultSet rs;
    String nomor_pindah_grup, jenis_input;
    String bagian_baru_sebelumnya;
    String jamKerja_baru_sebelumnya;
    String levelGaji_baru_sebelumnya;

    public JDialog_PindahGrup_disetujui(java.awt.Frame parent, boolean modal, String nomor, String jenis_input) {
        super(parent, modal);
        initComponents();
        this.nomor_pindah_grup = nomor;
        this.jenis_input = jenis_input;
        try {
            ComboBox_bagian_baru.removeAllItems();
            ComboBox_bagian_baru.addItem(null);
            sql = "SELECT `nama_bagian`, `kode_bagian` FROM `tb_bagian` WHERE `status_bagian` = 1 AND `kode_departemen` NOT IN ('SUB', 'PIMPINAN')"
                    + "ORDER BY `nama_bagian`";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_bagian_baru.addItem(rs.getString("nama_bagian"));
            }

            ComboBox_jamKerja_baru.removeAllItems();
            ComboBox_jamKerja_baru.addItem(null);
            sql = "SELECT `jam_kerja` FROM `tb_jam_kerja` WHERE 1  ORDER BY `jam_kerja`";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_jamKerja_baru.addItem(rs.getString("jam_kerja"));
            }

            ComboBox_levelGaji_baru.removeAllItems();
            ComboBox_levelGaji_baru.addItem(null);
            sql = "SELECT `level_gaji`, `bagian` FROM `tb_level_gaji` WHERE 1  ORDER BY `bagian`, `level_gaji`";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_levelGaji_baru.addItem(rs.getString("level_gaji") + "-" + rs.getString("bagian"));
            }

            sql = "SELECT `nomor`, `tanggal_input`, `tb_form_pindah_grup`.`keterangan`, `tb_form_pindah_grup`.`id_pegawai`, `tb_karyawan`.`nama_pegawai`, "
                    + "`bagian_lama`, `bagian_baru`, "
                    + "`jamkerja_lama`, `jamkerja_baru`, "
                    + "`levelgaji_lama`, (SELECT `bagian` FROM `tb_level_gaji` WHERE `level_gaji` = `tb_form_pindah_grup`.`levelgaji_lama`) AS 'bagian_levelgaji_lama', "
                    + "`levelgaji_baru`, (SELECT `bagian` FROM `tb_level_gaji` WHERE `level_gaji` = `tb_form_pindah_grup`.`levelgaji_baru`) AS 'bagian_levelgaji_baru' "
                    + "FROM `tb_form_pindah_grup` "
                    + "LEFT JOIN `tb_karyawan` ON `tb_form_pindah_grup`.`id_pegawai` = `tb_karyawan`.`id_pegawai` "
                    + "WHERE `nomor` = '" + nomor + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                label_id_pegawai.setText(rs.getString("id_pegawai"));
                label_nama.setText(rs.getString("nama_pegawai"));
                label_bagian_lama.setText(rs.getString("bagian_lama"));
                label_jamKerja_lama.setText(rs.getString("jamkerja_lama"));
                label_levelGaji_lama.setText(rs.getString("levelgaji_lama") + "-" + rs.getString("bagian_levelgaji_lama"));
                ComboBox_bagian_baru.setSelectedItem(rs.getString("bagian_baru"));
                ComboBox_jamKerja_baru.setSelectedItem(rs.getString("jamkerja_baru"));
                ComboBox_levelGaji_baru.setSelectedItem(rs.getString("levelgaji_baru") + "-" + rs.getString("bagian_levelgaji_baru"));
                txt_keterangan.setText(rs.getString("keterangan"));

                bagian_baru_sebelumnya = rs.getString("bagian_baru");
                jamKerja_baru_sebelumnya = rs.getString("jamkerja_baru");
                levelGaji_baru_sebelumnya = rs.getString("levelgaji_baru");
            }

            if (jenis_input.equals("HR")) {
                label_title.setText("Pindah Bagian Diketahui HRD");
                button_disetujui.setText("Diketahui HRD");
                label_levelGaji_lama.setEnabled(false);
                ComboBox_levelGaji_baru.setEnabled(false);
            } else if (jenis_input.equals("Keuangan")) {
                label_title.setText("Pindah Bagian Diketahui Keuangan");
                button_disetujui.setText("Diketahui Keuangan");
                label_bagian_lama.setEnabled(false);
                ComboBox_bagian_baru.setEnabled(false);
                label_jamKerja_lama.setEnabled(false);
                ComboBox_jamKerja_baru.setEnabled(false);
            } else if (jenis_input.equals("disetujui")) {
                label_title.setText("Pindah Bagian Disetujui");
                button_disetujui.setText("Disetujui");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JDialog_PindahGrup_disetujui.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void disetujui() {
        boolean check = true, isEdited = false;

        String bagian_baru = "`bagian_baru`=NULL, ";
        String check_bagian = label_bagian_lama.getText();
        if (ComboBox_bagian_baru.getSelectedItem() != null) {
            bagian_baru = "`bagian_baru`='" + ComboBox_bagian_baru.getSelectedItem().toString() + "', ";
            check_bagian = ComboBox_bagian_baru.getSelectedItem().toString();
            if (bagian_baru_sebelumnya == null || !bagian_baru_sebelumnya.equals(ComboBox_bagian_baru.getSelectedItem().toString())) {
                isEdited = true;
            }
        }

        String jamkerja_baru = "`jamkerja_baru`=NULL, ";
        if (ComboBox_jamKerja_baru.getSelectedItem() != null) {
            jamkerja_baru = "`jamkerja_baru`='" + ComboBox_jamKerja_baru.getSelectedItem().toString() + "', ";
            if (jamKerja_baru_sebelumnya == null || !jamKerja_baru_sebelumnya.equals(ComboBox_jamKerja_baru.getSelectedItem().toString())) {
                isEdited = true;
            }
        }

        String levelgaji_baru = "`levelgaji_baru`=NULL, ";
        String check_levelgaji = label_levelGaji_lama.getText().split("-")[1];
        if (ComboBox_levelGaji_baru.getSelectedItem() != null) {
            levelgaji_baru = "`levelgaji_baru`='" + ComboBox_levelGaji_baru.getSelectedItem().toString().split("-")[0] + "', ";
            check_levelgaji = ComboBox_levelGaji_baru.getSelectedItem().toString().split("-")[1];
            if (levelGaji_baru_sebelumnya == null || !levelGaji_baru_sebelumnya.equals(ComboBox_levelGaji_baru.getSelectedItem().toString().split("-")[1])) {
                isEdited = true;
            }
        }

//        if (check_levelgaji.equals("HARIAN") && check_bagian.length() > 5 && check_bagian.substring(0, 5).equals("CABUT")) {
//            JOptionPane.showMessageDialog(this, "Karyawan BORONG Cabut tidak dapat memakai level gaji HARIAN !");
//            check = false;
//        } else if (check_levelgaji.equals("CABUT") && !(check_bagian.length() > 5 && check_bagian.substring(0, 5).equals("CABUT"))) {
//            JOptionPane.showMessageDialog(this, "Karyawan HARIAN tidak dapat memakai level gaji CABUT !");
//            check = false;
//        }
        if (check_levelgaji.equals("HARIAN") && (check_bagian.toUpperCase().contains("-CABUT-BORONG") || check_bagian.toUpperCase().contains("-CABUT-TRAINING"))) {
            JOptionPane.showMessageDialog(this, "Karyawan BORONG Cabut tidak dapat memakai level gaji HARIAN !");
            check = false;
        } else if (check_levelgaji.equals("CABUT") && !(check_bagian.toUpperCase().contains("-CABUT-BORONG") || check_bagian.toUpperCase().contains("-CABUT-TRAINING"))) {
            JOptionPane.showMessageDialog(this, "Karyawan HARIAN tidak dapat memakai level gaji CABUT !");
            check = false;
        }

        if (check) {
            int dialogResult = JOptionPane.showConfirmDialog(this, "Harap pastikan semua data sudah BENAR, lanjutkan?", "Warning", 0);
            if (dialogResult == JOptionPane.YES_OPTION) {
                try {
                    String Edited = "";
                    if (isEdited) {
                        Edited = "E:";
                    }
                    sql = "UPDATE `tb_form_pindah_grup` SET "
                            + "`keterangan`='" + txt_keterangan.getText() + "', "
                            + bagian_baru
                            + jamkerja_baru
                            + levelgaji_baru
                            + "`disetujui`='" + Edited + MainForm.Login_NamaPegawai + "', "
                            + "`jam_disetujui`=NOW() "
                            + "WHERE `nomor` = '" + nomor_pindah_grup + "'";
                    if (Utility.db.getStatement().executeUpdate(sql) > 0) {
                        JOptionPane.showMessageDialog(this, "Data pindah Bagian disetujui !");
                        this.dispose();
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, ex);
                    Logger.getLogger(JDialog_PindahGrup_disetujui.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public void diketahui_HR() {
        try {
            boolean check = true, isEdited = false;
            String id_pegawai = label_id_pegawai.getText();
            String nama_pegawai = label_nama.getText();

            if (check) {
                int dialogResult = JOptionPane.showConfirmDialog(this, "Harap pastikan semua data sudah BENAR\nSistem akan otomatis edit bagian & jam Kerja, lanjutkan?", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    try {
                        Utility.db.getConnection().setAutoCommit(false);

                        String bagian_baru = "`bagian_baru`=NULL, ";
                        if (ComboBox_bagian_baru.getSelectedItem() != null) {
                            bagian_baru = "`bagian_baru`='" + ComboBox_bagian_baru.getSelectedItem().toString() + "', ";

                            String query_update_bagian_baru = "UPDATE `tb_karyawan` "
                                    + "SET `kode_bagian`=(SELECT `kode_bagian` FROM `tb_bagian` WHERE `nama_bagian` = '" + ComboBox_bagian_baru.getSelectedItem().toString() + "') "
                                    + "WHERE `id_pegawai` = '" + id_pegawai + "'";
                            Utility.db.getStatement().executeUpdate(query_update_bagian_baru);

                            if (bagian_baru_sebelumnya == null || !bagian_baru_sebelumnya.equals(ComboBox_bagian_baru.getSelectedItem().toString())) {
                                isEdited = true;
                            }
                        }

                        String jamkerja_baru = "`jamkerja_baru`=NULL, ";
                        if (ComboBox_jamKerja_baru.getSelectedItem() != null) {
                            String query_update_jam_kerja_baru = "UPDATE `tb_karyawan` "
                                    + "SET `jam_kerja` = '" + ComboBox_jamKerja_baru.getSelectedItem().toString() + "' "
                                    + "WHERE `id_pegawai` = '" + id_pegawai + "'";
                            Utility.db.getStatement().executeUpdate(query_update_jam_kerja_baru);

                            jamkerja_baru = "`jamkerja_baru`='" + ComboBox_jamKerja_baru.getSelectedItem().toString() + "', ";
                            if (jamKerja_baru_sebelumnya == null || !jamKerja_baru_sebelumnya.equals(ComboBox_jamKerja_baru.getSelectedItem().toString())) {
                                isEdited = true;
                            }
                        }

                        String Edited = "";
                        if (isEdited) {
                            Edited = "E:";
                        }

                        sql = "UPDATE `tb_form_pindah_grup` SET "
                                + "`keterangan`='" + txt_keterangan.getText() + "', "
                                + bagian_baru
                                + jamkerja_baru
                                + "`diketahui_hr`='" + Edited + MainForm.Login_NamaPegawai + "', "
                                + "`jam_diketahui_hr`=NOW() "
                                + "WHERE `nomor` = '" + nomor_pindah_grup + "'";
                        Utility.db.getStatement().executeUpdate(sql);
                        Utility.db.getConnection().commit();
                        JOptionPane.showMessageDialog(this, "DATA SAVED !\nNama : " + nama_pegawai + "\nBagian baru : " + bagian_baru + "\nJam Kerja baru : " + jamkerja_baru);
                        this.dispose();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(this, ex);
                        Logger.getLogger(JDialog_PindahGrup.class.getName()).log(Level.SEVERE, null, ex);
                        try {
                            Utility.db.getConnection().rollback();
                        } catch (SQLException e) {
                            Logger.getLogger(JDialog_PindahGrup.class.getName()).log(Level.SEVERE, null, e);
                        }
                    } finally {
                        try {
                            Utility.db.getConnection().setAutoCommit(true);
                        } catch (SQLException ex) {
                            Logger.getLogger(JDialog_PindahGrup.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JDialog_PindahGrup.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void diketahui_Keuangan() {
        try {
            boolean check = true;
            String id_pegawai = label_id_pegawai.getText();
            String nama_pegawai = label_nama.getText();
            String check_bagian = label_bagian_lama.getText();
            if (ComboBox_bagian_baru.getSelectedItem() != null) {
                check_bagian = ComboBox_bagian_baru.getSelectedItem().toString();
            }

            String check_levelgaji = label_levelGaji_lama.getText().split("-")[1];
            if (ComboBox_levelGaji_baru.getSelectedItem() != null) {
                check_levelgaji = ComboBox_levelGaji_baru.getSelectedItem().toString().split("-")[1];
            }

            if (check_levelgaji.equals("HARIAN") && (check_bagian.toUpperCase().contains("-CABUT-BORONG") || check_bagian.toUpperCase().contains("-CABUT-TRAINING"))) {
                JOptionPane.showMessageDialog(this, "Karyawan BORONG Cabut tidak dapat memakai level gaji HARIAN !");
                check = false;
            } else if (check_levelgaji.equals("CABUT") && !(check_bagian.toUpperCase().contains("-CABUT-BORONG") || check_bagian.toUpperCase().contains("-CABUT-TRAINING"))) {
                JOptionPane.showMessageDialog(this, "Karyawan HARIAN tidak dapat memakai level gaji CABUT !");
                check = false;
            }

            if (check) {
                int dialogResult = JOptionPane.showConfirmDialog(this, "Harap pastikan semua data sudah BENAR!!\nSistem akan otomatis edit level gaji, lanjutkan?", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    try {
                        Utility.db.getConnection().setAutoCommit(false);
                        String levelgaji_baru = "NULL";
                        if (ComboBox_levelGaji_baru.getSelectedItem() != null) {
                            levelgaji_baru = "'" + ComboBox_levelGaji_baru.getSelectedItem().toString().split("-")[0] + "'";
                            String query_update_levelgaji_baru = "UPDATE `tb_karyawan` SET `level_gaji`=" + levelgaji_baru + " WHERE `id_pegawai` = '" + id_pegawai + "'";
                            Utility.db.getStatement().executeUpdate(query_update_levelgaji_baru);
                        }

                        sql = "UPDATE `tb_form_pindah_grup` SET "
                                + "`keterangan`='" + txt_keterangan.getText() + "', "
                                + "`levelgaji_baru`=" + levelgaji_baru + ", "
                                + "`diketahui_keu`='" + MainForm.Login_NamaPegawai + "', "
                                + "`jam_diketahui_keu`=NOW() "
                                + "WHERE `nomor` = '" + nomor_pindah_grup + "'";
                        Utility.db.getStatement().executeUpdate(sql);
                        Utility.db.getConnection().commit();
                        JOptionPane.showMessageDialog(this, "DATA SAVED !\nNama : " + nama_pegawai + "\nLevel gaji baru : " + levelgaji_baru);
                        this.dispose();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(this, ex);
                        Logger.getLogger(JDialog_PindahGrup.class.getName()).log(Level.SEVERE, null, ex);
                        try {
                            Utility.db.getConnection().rollback();
                        } catch (SQLException e) {
                            Logger.getLogger(JDialog_PindahGrup.class.getName()).log(Level.SEVERE, null, e);
                        }
                    } finally {
                        try {
                            Utility.db.getConnection().setAutoCommit(true);
                        } catch (SQLException ex) {
                            Logger.getLogger(JDialog_PindahGrup.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JDialog_PindahGrup.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        label_title = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        label_id_pegawai = new javax.swing.JLabel();
        label_nama = new javax.swing.JLabel();
        label_bagian_lama = new javax.swing.JLabel();
        label_jamKerja_lama = new javax.swing.JLabel();
        button_disetujui = new javax.swing.JButton();
        ComboBox_bagian_baru = new javax.swing.JComboBox<>();
        ComboBox_jamKerja_baru = new javax.swing.JComboBox<>();
        ComboBox_levelGaji_baru = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        label_levelGaji_lama = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel12 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txt_keterangan = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        label_title.setBackground(new java.awt.Color(255, 255, 255));
        label_title.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_title.setText("Pindah Grup Disetujui");

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setText("ID Pegawai :");

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel3.setText("Nama : ");

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel6.setText("Bagian Lama :");

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel7.setText("Bagian Baru :");

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel8.setText("Jam Kerja Lama :");

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel9.setText("Jam Kerja Baru :");

        label_id_pegawai.setBackground(new java.awt.Color(255, 255, 255));
        label_id_pegawai.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_id_pegawai.setText("-");

        label_nama.setBackground(new java.awt.Color(255, 255, 255));
        label_nama.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_nama.setText("-");

        label_bagian_lama.setBackground(new java.awt.Color(255, 255, 255));
        label_bagian_lama.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_bagian_lama.setText("-");

        label_jamKerja_lama.setBackground(new java.awt.Color(255, 255, 255));
        label_jamKerja_lama.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_jamKerja_lama.setText("-");

        button_disetujui.setBackground(new java.awt.Color(255, 255, 255));
        button_disetujui.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_disetujui.setText("Disetujui");
        button_disetujui.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_disetujuiActionPerformed(evt);
            }
        });

        ComboBox_bagian_baru.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        ComboBox_jamKerja_baru.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        ComboBox_levelGaji_baru.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel10.setText("Level Gaji Lama :");

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel11.setText("Level Gaji Baru :");

        label_levelGaji_lama.setBackground(new java.awt.Color(255, 255, 255));
        label_levelGaji_lama.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_levelGaji_lama.setText("-");

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(3);
        jTextArea1.setText("Note : Jika tidak dipilih maka dianggap tidak ada perubahan, untuk grup baru harus berubah");
        jTextArea1.setWrapStyleWord(true);
        jScrollPane1.setViewportView(jTextArea1);

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel12.setText("Keterangan :");

        txt_keterangan.setColumns(20);
        txt_keterangan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_keterangan.setLineWrap(true);
        txt_keterangan.setRows(3);
        txt_keterangan.setWrapStyleWord(true);
        jScrollPane2.setViewportView(txt_keterangan);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ComboBox_levelGaji_baru, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ComboBox_jamKerja_baru, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ComboBox_bagian_baru, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(label_jamKerja_lama, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2)
                    .addComponent(label_bagian_lama, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(label_nama, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(label_id_pegawai, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(label_levelGaji_lama, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(button_disetujui)
                .addGap(10, 10, 10))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jScrollPane1)
                .addGap(10, 10, 10))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(label_title)
                .addGap(237, 237, 237))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label_title)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_id_pegawai, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_nama, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_bagian_lama, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_bagian_baru, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_jamKerja_lama, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_jamKerja_baru, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_levelGaji_lama, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_levelGaji_baru, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_disetujui)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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

    private void button_disetujuiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_disetujuiActionPerformed
        // TODO add your handling code here:
        boolean isPindahBagian = false;
        if (ComboBox_bagian_baru.getSelectedItem() != null && label_bagian_lama.getText() != null) {
            isPindahBagian = !label_bagian_lama.getText().equals(ComboBox_bagian_baru.getSelectedItem().toString());
        }

        boolean isPindahJamKerja = false;
        if (ComboBox_jamKerja_baru.getSelectedItem() != null && label_jamKerja_lama.getText() != null) {
            isPindahJamKerja = !label_jamKerja_lama.getText().equals(ComboBox_jamKerja_baru.getSelectedItem().toString());
        }

        boolean isPindahLevelGaji = false;
        if (ComboBox_levelGaji_baru.getSelectedItem() != null && label_levelGaji_lama.getText() != null) {
            isPindahLevelGaji = !label_levelGaji_lama.getText().equals(ComboBox_levelGaji_baru.getSelectedItem().toString());
        }

        if (!(isPindahBagian || isPindahJamKerja || isPindahLevelGaji)) {
            JOptionPane.showMessageDialog(this, "Tidak ada perubahan pada data sebelumnya, tidak bisa di simpan !");
        } else if (jenis_input.equals("HR")) {
            diketahui_HR();
        } else if (jenis_input.equals("Keuangan")) {
            diketahui_Keuangan();
        } else if (jenis_input.equals("disetujui")) {
            disetujui();
        }
    }//GEN-LAST:event_button_disetujuiActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_bagian_baru;
    private javax.swing.JComboBox<String> ComboBox_jamKerja_baru;
    private javax.swing.JComboBox<String> ComboBox_levelGaji_baru;
    private javax.swing.JButton button_disetujui;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JLabel label_bagian_lama;
    private javax.swing.JLabel label_id_pegawai;
    private javax.swing.JLabel label_jamKerja_lama;
    private javax.swing.JLabel label_levelGaji_lama;
    private javax.swing.JLabel label_nama;
    private javax.swing.JLabel label_title;
    private javax.swing.JTextArea txt_keterangan;
    // End of variables declaration//GEN-END:variables
}
