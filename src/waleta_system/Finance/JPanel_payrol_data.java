package waleta_system.Finance;

import java.awt.event.KeyEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.ExportToExcel;
import waleta_system.Class.Utility;
import waleta_system.MainForm;

public class JPanel_payrol_data extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    Date today = new Date();
    PreparedStatement pst;
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public JPanel_payrol_data() {
        initComponents();
    }

    public void init() {
        if (MainForm.Login_idPegawai.equals("20171201644")//indrika
                || MainForm.Login_idPegawai.equals("20230907768")//diyan
                || MainForm.Login_idPegawai.equals("20180102221")//bastian
                ) {
            button_slip_borong_cabut.setEnabled(true);
            button_slip_borong_cetak.setEnabled(true);
            button_slip_harian.setEnabled(true);
            button_slip_mandiri_cetak.setEnabled(true);
        }
        if (MainForm.Login_idPegawai.equals("20170100225")) {//priska
            button_slip_mandiri_cetak.setEnabled(true);
        }
    }

    public void refreshTable() {
        try {
            DefaultTableModel model = (DefaultTableModel) Tabel_data.getModel();
            model.setRowCount(0);
            boolean check = true;
            if (Date_penggajian1.getDate() == null || Date_penggajian2.getDate() == null) {
                check = false;
                JOptionPane.showMessageDialog(this, "Harap masukkan tanggal penggajian");
            }
//            else if (!(new SimpleDateFormat("EEEEE").format(Date_penggajian1.getDate()).toUpperCase().equals("THURSDAY") || new SimpleDateFormat("EEEEE").format(Date_penggajian1.getDate()).toUpperCase().equals("KAMIS"))) {
//                check = false;
//                JOptionPane.showMessageDialog(this, "Tanggal penggajian seharusnya hari kamis");
//            }

            if (check) {
                Date tanggal_mulai = new Date(Date_penggajian1.getDate().getTime() - (7 * 24 * 60 * 60 * 1000));
                Date tanggal_selesai = new Date(Date_penggajian1.getDate().getTime() - (1 * 24 * 60 * 60 * 1000));

                String search_bagian = "AND `bagian` LIKE '" + txt_search_bagian.getText() + "' ";
                if (txt_search_bagian.getText() == null || txt_search_bagian.getText().equals("")) {
                    search_bagian = "";
                }
                String level_gaji = "AND `tb_payrol_data`.`level_gaji` LIKE '" + txt_search_level_gaji.getText() + "' ";
                if (txt_search_level_gaji.getText() == null || txt_search_level_gaji.getText().equals("")) {
                    level_gaji = "";
                }
                String jam_kerja = "AND `tb_payrol_data`.`jam_kerja` LIKE '" + txt_search_jam_kerja.getText() + "' ";
                if (txt_search_jam_kerja.getText() == null || txt_search_jam_kerja.getText().equals("")) {
                    jam_kerja = "";
                }

                sql = "SELECT `tgl_penggajian`, `tb_payrol_data`.`id_pegawai`, `nama_pegawai`, `bagian`, `tb_payrol_data`.`level_gaji`, `tb_payrol_data`.`jam_kerja`, `tgl_penggajian`, `lembur`, `pot_terlambat`, `pot_ijin_keluar`, `pot_transport`, `pot_bpjs`, `pot_bpjs_tk`, `tunjangan_hadir`, `bonus_tbt`, `bonus_1`, `bonus_2`, `gaji_harian`, `gaji_borong`, `piutang`, `bonus_pencapaian_produksi`, `tb_payrol_data`.`keterangan` "
                        + "FROM `tb_payrol_data` "
                        + "LEFT JOIN `tb_karyawan` ON `tb_payrol_data`.`id_pegawai` = `tb_karyawan`.`id_pegawai`"
//                        + "LEFT JOIN (SELECT `id_pegawai`, MAX(`grup`) AS 'grup' FROM `tb_lembur_rekap` WHERE `tanggal` BETWEEN '" + dateFormat.format(tanggal_mulai) + "' AND '" + dateFormat.format(tanggal_selesai) + "' GROUP BY `id_pegawai`) "
//                        + "A ON `tb_karyawan`.`id_pegawai` = A.`id_pegawai` "
                        + "WHERE "
                        + "`nama_pegawai` LIKE '%" + txt_search_nama.getText() + "%' "
                        + "AND `tgl_penggajian` BETWEEN '" + dateFormat.format(Date_penggajian1.getDate()) + "' AND '" + dateFormat.format(Date_penggajian2.getDate()) + "' "
                        + search_bagian
                        + level_gaji
                        + jam_kerja
                        + "ORDER BY `nama_pegawai` ";

                pst = Utility.db.getConnection().prepareStatement(sql);
                rs = pst.executeQuery();
                Object[] row = new Object[30];
                double lembur = 0, pot_terlambat = 0, pot_ijin_keluar = 0, pot_transport = 0, pot_bpjs = 0, pot_bpjs_tk = 0, tunjangan_hadir = 0, gaji_harian = 0,
                        gaji_borong = 0, bonus_tbt = 0, bonus_1 = 0, bonus_2 = 0, bonus_pencapaian_produksi = 0, piutang = 0, tot_gaji = 0;
                while (rs.next()) {
                    row[0] = rs.getDate("tgl_penggajian");
                    row[1] = rs.getString("id_pegawai");
                    row[2] = rs.getString("nama_pegawai");
                    row[3] = rs.getString("bagian");
                    row[4] = rs.getDouble("lembur");
                    row[5] = rs.getDouble("pot_terlambat");
                    row[6] = rs.getDouble("pot_ijin_keluar");
                    row[7] = rs.getDouble("pot_transport");
                    row[8] = rs.getDouble("pot_bpjs");
                    row[9] = rs.getDouble("pot_bpjs_tk");
                    row[10] = rs.getDouble("tunjangan_hadir");
                    row[11] = rs.getDouble("gaji_harian");
                    row[12] = rs.getDouble("gaji_borong");
                    row[13] = rs.getDouble("bonus_tbt");
                    row[14] = rs.getDouble("bonus_1");
                    row[15] = rs.getDouble("bonus_2");
                    row[16] = rs.getDouble("bonus_pencapaian_produksi");
                    row[17] = rs.getDouble("piutang");
                    double gaji = rs.getDouble("lembur")
                            - rs.getDouble("pot_terlambat")
                            - rs.getDouble("pot_ijin_keluar")
                            - rs.getDouble("pot_transport")
                            - rs.getDouble("pot_bpjs")
                            - rs.getDouble("pot_bpjs_tk")
                            + rs.getDouble("tunjangan_hadir")
                            + rs.getDouble("gaji_harian")
                            + rs.getDouble("gaji_borong")
                            + rs.getDouble("bonus_tbt")
                            + rs.getDouble("bonus_1")
                            + rs.getDouble("bonus_2")
                            + rs.getDouble("bonus_pencapaian_produksi")
                            + rs.getDouble("piutang");
                    row[18] = gaji;
                    row[19] = rs.getString("keterangan");
                    row[20] = rs.getString("level_gaji");
                    row[21] = rs.getString("jam_kerja");
                    if (CheckBox_hide_Gaji_0.isSelected()) {
                        if (gaji > 0) {
                            model.addRow(row);
                            lembur = lembur + rs.getDouble("lembur");
                            pot_terlambat = pot_terlambat + rs.getDouble("pot_terlambat");
                            pot_ijin_keluar = pot_ijin_keluar + rs.getDouble("pot_ijin_keluar");
                            pot_transport = pot_transport + rs.getDouble("pot_transport");
                            pot_bpjs = pot_bpjs + rs.getDouble("pot_bpjs");
                            pot_bpjs_tk = pot_bpjs_tk + rs.getDouble("pot_bpjs_tk");
                            tunjangan_hadir = tunjangan_hadir + rs.getDouble("tunjangan_hadir");
                            gaji_harian = gaji_harian + rs.getDouble("gaji_harian");
                            gaji_borong = gaji_borong + rs.getDouble("gaji_borong");
                            bonus_tbt = bonus_tbt + rs.getDouble("bonus_tbt");
                            bonus_1 = bonus_1 + rs.getDouble("bonus_1");
                            bonus_2 = bonus_2 + rs.getDouble("bonus_2");
                            bonus_pencapaian_produksi = bonus_pencapaian_produksi + rs.getDouble("bonus_pencapaian_produksi");
                            piutang = piutang + rs.getDouble("piutang");
                            tot_gaji = tot_gaji + gaji;
                        }
                    } else {
                        model.addRow(row);
                        lembur = lembur + rs.getDouble("lembur");
                        pot_terlambat = pot_terlambat + rs.getDouble("pot_terlambat");
                        pot_ijin_keluar = pot_ijin_keluar + rs.getDouble("pot_ijin_keluar");
                        pot_transport = pot_transport + rs.getDouble("pot_transport");
                        pot_bpjs = pot_bpjs + rs.getDouble("pot_bpjs");
                        pot_bpjs_tk = pot_bpjs_tk + rs.getDouble("pot_bpjs_tk");
                        tunjangan_hadir = tunjangan_hadir + rs.getDouble("tunjangan_hadir");
                        gaji_harian = gaji_harian + rs.getDouble("gaji_harian");
                        gaji_borong = gaji_borong + rs.getDouble("gaji_borong");
                        bonus_tbt = bonus_tbt + rs.getDouble("bonus_tbt");
                        bonus_1 = bonus_1 + rs.getDouble("bonus_1");
                        bonus_2 = bonus_2 + rs.getDouble("bonus_2");
                        bonus_pencapaian_produksi = bonus_pencapaian_produksi + rs.getDouble("bonus_pencapaian_produksi");
                        piutang = piutang + rs.getDouble("piutang");
                        tot_gaji = tot_gaji + gaji;
                    }
                }
                row[0] = "TOTAL";
                row[1] = "";
                row[2] = "";
                row[3] = "";
                row[4] = lembur;
                row[5] = pot_terlambat;
                row[6] = pot_ijin_keluar;
                row[7] = pot_transport;
                row[8] = pot_bpjs;
                row[9] = pot_bpjs_tk;
                row[10] = tunjangan_hadir;
                row[11] = gaji_harian;
                row[12] = gaji_borong;
                row[13] = bonus_tbt;
                row[14] = bonus_1;
                row[15] = bonus_2;
                row[16] = bonus_pencapaian_produksi;
                row[17] = piutang;
                row[18] = tot_gaji;
                model.addRow(row);
                ColumnsAutoSizer.sizeColumnsToFit(Tabel_data);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_payrol_data.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        Date_penggajian1 = new com.toedter.calendar.JDateChooser();
        button_load = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        Tabel_data = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txt_search_nama = new javax.swing.JTextField();
        CheckBox_hide_Gaji_0 = new javax.swing.JCheckBox();
        button_export = new javax.swing.JButton();
        jLabel19 = new javax.swing.JLabel();
        txt_search_bagian = new javax.swing.JTextField();
        txt_search_level_gaji = new javax.swing.JTextField();
        Date_penggajian2 = new com.toedter.calendar.JDateChooser();
        button_slip_harian = new javax.swing.JButton();
        button_slip_borong_cabut = new javax.swing.JButton();
        button_slip_borong_cetak = new javax.swing.JButton();
        button_slip_mandiri_cetak = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        txt_search_jam_kerja = new javax.swing.JTextField();

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel1.setText("Data Payroll PEJUANG WALETA");

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setText("TGL penggajian :");

        Date_penggajian1.setBackground(new java.awt.Color(255, 255, 255));
        Date_penggajian1.setDateFormatString("dd MMMM yyyy");
        Date_penggajian1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        button_load.setBackground(new java.awt.Color(255, 255, 255));
        button_load.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_load.setText("Load Data");
        button_load.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_loadActionPerformed(evt);
            }
        });

        Tabel_data.setAutoCreateRowSorter(true);
        Tabel_data.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tgl Gajian", "ID", "Nama", "Bagian", "Lembur", "Pot. Terlambat", "Pot. Ijin Keluar", "Pot. Transport", "Pot. BPJS", "Pot. BPJS TK", "Tunj. hadir", "Gaji Harian", "Gaji Borong", "Bonus TBT", "Bonus 1", "Bonus 2", "Bonus Produksi", "Piutang", "Total Gaji Trasfer", "Ket.", "Level Gaji", "Jam Kerja"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Tabel_data.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(Tabel_data);

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel5.setText("Level Gaji :");

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel6.setText("Nama :");

        txt_search_nama.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_search_nama.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_namaKeyPressed(evt);
            }
        });

        CheckBox_hide_Gaji_0.setBackground(new java.awt.Color(255, 255, 255));
        CheckBox_hide_Gaji_0.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        CheckBox_hide_Gaji_0.setText("Hide Gaji 0");
        CheckBox_hide_Gaji_0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CheckBox_hide_Gaji_0ActionPerformed(evt);
            }
        });

        button_export.setText("Export to Excel");
        button_export.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_exportActionPerformed(evt);
            }
        });

        jLabel19.setBackground(new java.awt.Color(255, 255, 255));
        jLabel19.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel19.setText("Divisi-Bagian-Ruang :");

        txt_search_bagian.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_search_bagian.setText("%%");
        txt_search_bagian.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_bagianKeyPressed(evt);
            }
        });

        txt_search_level_gaji.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_search_level_gaji.setText("%%");
        txt_search_level_gaji.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_level_gajiKeyPressed(evt);
            }
        });

        Date_penggajian2.setBackground(new java.awt.Color(255, 255, 255));
        Date_penggajian2.setDateFormatString("dd MMMM yyyy");
        Date_penggajian2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        button_slip_harian.setText("Slip Harian");
        button_slip_harian.setEnabled(false);
        button_slip_harian.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_slip_harianActionPerformed(evt);
            }
        });

        button_slip_borong_cabut.setText("Slip Borong Cabut");
        button_slip_borong_cabut.setEnabled(false);
        button_slip_borong_cabut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_slip_borong_cabutActionPerformed(evt);
            }
        });

        button_slip_borong_cetak.setText("Slip Borong Cetak");
        button_slip_borong_cetak.setEnabled(false);
        button_slip_borong_cetak.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_slip_borong_cetakActionPerformed(evt);
            }
        });

        button_slip_mandiri_cetak.setText("Slip Mandiri Cetak");
        button_slip_mandiri_cetak.setEnabled(false);
        button_slip_mandiri_cetak.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_slip_mandiri_cetakActionPerformed(evt);
            }
        });

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel7.setText("Jam Kerja :");

        txt_search_jam_kerja.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_search_jam_kerja.setText("%%");
        txt_search_jam_kerja.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_jam_kerjaKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1346, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(button_slip_harian)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_slip_borong_cabut)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_slip_borong_cetak)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_slip_mandiri_cetak)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_export))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_penggajian1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_penggajian2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_nama, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel19)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_bagian, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_level_gaji, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_jam_kerja, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_load)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(CheckBox_hide_Gaji_0)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_penggajian1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_nama, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_bagian, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_load, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CheckBox_hide_Gaji_0)
                    .addComponent(Date_penggajian2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_level_gaji, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_jam_kerja, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(button_slip_borong_cabut)
                    .addComponent(button_slip_borong_cetak)
                    .addComponent(button_slip_mandiri_cetak)
                    .addComponent(button_slip_harian)
                    .addComponent(button_export))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 592, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void button_loadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_loadActionPerformed
        // TODO add your handling code here:
        refreshTable();
    }//GEN-LAST:event_button_loadActionPerformed

    private void txt_search_namaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_namaKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_search_namaKeyPressed

    private void CheckBox_hide_Gaji_0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CheckBox_hide_Gaji_0ActionPerformed
        // TODO add your handling code here:
        refreshTable();
    }//GEN-LAST:event_CheckBox_hide_Gaji_0ActionPerformed

    private void button_exportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_exportActionPerformed
        // TODO add your handling code here:
        DefaultTableModel table = (DefaultTableModel) Tabel_data.getModel();
        ExportToExcel.writeToExcel(table, this);
    }//GEN-LAST:event_button_exportActionPerformed

    private void txt_search_bagianKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_bagianKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_search_bagianKeyPressed

    private void txt_search_level_gajiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_level_gajiKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_search_level_gajiKeyPressed

    private void button_slip_harianActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_slip_harianActionPerformed
        // TODO add your handling code here:
        try {
            Date tanggal_mulai = new Date(dateFormat.parse(Tabel_data.getValueAt(0, 0).toString()).getTime() - (7 * 24 * 60 * 60 * 1000));
            Date tanggal_selesai = new Date(dateFormat.parse(Tabel_data.getValueAt(0, 0).toString()).getTime() - (1 * 24 * 60 * 60 * 1000));
            //            JRDesignQuery newQuery = new JRDesignQuery();
            //            newQuery.setText(sql);
            JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Slip_Gaji_karyawan_Harian.jrxml");
            //            JASP_DESIGN.setQuery(newQuery);
            Map<String, Object> map = new HashMap<>();
            map.put("HALAMAN", 1);
            map.put("PERIODE_AWAL", tanggal_mulai);
            map.put("PERIODE_AKHIR", tanggal_selesai);
            map.put("ID_PEGAWAI", Tabel_data.getValueAt(0, 1).toString());
            map.put("NAMA_PEGAWAI", Tabel_data.getValueAt(0, 2).toString());
            map.put("GRUP", Tabel_data.getValueAt(0, 3));
            map.put("LEMBUR", Double.valueOf(Tabel_data.getValueAt(0, 4).toString()));
            map.put("POT_TERLAMBAT", Double.valueOf(Tabel_data.getValueAt(0, 5).toString()));
            map.put("POT_IJIN", Double.valueOf(Tabel_data.getValueAt(0, 6).toString()));
            map.put("POT_TRANSPORT", Double.valueOf(Tabel_data.getValueAt(0, 7).toString()));
            map.put("POT_BPJS", Double.valueOf(Tabel_data.getValueAt(0, 8).toString()));
            map.put("POT_BPJS_TK", Double.valueOf(Tabel_data.getValueAt(0, 9).toString()));
            map.put("TUNJANGAN_HADIR", Double.valueOf(Tabel_data.getValueAt(0, 10).toString()));
            map.put("GAJI_HARIAN", Double.valueOf(Tabel_data.getValueAt(0, 11).toString()));
            map.put("GAJI_BORONG", Double.valueOf(Tabel_data.getValueAt(0, 12).toString()));
            map.put("BONUS_TBT", Double.valueOf(Tabel_data.getValueAt(0, 13).toString()));
            map.put("BONUS_LP", Double.valueOf(Tabel_data.getValueAt(0, 14).toString()));
            map.put("BONUS_MK_UTUH", Double.valueOf(Tabel_data.getValueAt(0, 15).toString()));
            map.put("BONUS_PENCAPAIAN_PRODUKSI", Double.valueOf(Tabel_data.getValueAt(0, 16).toString()));
            map.put("PIUTANG", Double.valueOf(Tabel_data.getValueAt(0, 17).toString()));
            map.put("KETERANGAN", Tabel_data.getValueAt(0, 19));
            JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
            JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, map, Utility.db.getConnection());
            for (int i = 1; i < Tabel_data.getRowCount() - 1; i++) {
                tanggal_mulai = new Date(dateFormat.parse(Tabel_data.getValueAt(i, 0).toString()).getTime() - (7 * 24 * 60 * 60 * 1000));
                tanggal_selesai = new Date(dateFormat.parse(Tabel_data.getValueAt(i, 0).toString()).getTime() - (1 * 24 * 60 * 60 * 1000));
                JasperDesign JASP_DESIGN2 = JRXmlLoader.load("Report\\Slip_Gaji_karyawan_Harian.jrxml");
                JasperReport JASP_REP2 = JasperCompileManager.compileReport(JASP_DESIGN2);
                Map<String, Object> map2 = new HashMap<>();
                map2.put("HALAMAN", i + 1);
                map2.put("PERIODE_AWAL", tanggal_mulai);
                map2.put("PERIODE_AKHIR", tanggal_selesai);
                map2.put("ID_PEGAWAI", Tabel_data.getValueAt(i, 1).toString());
                map2.put("NAMA_PEGAWAI", Tabel_data.getValueAt(i, 2).toString());
                map2.put("GRUP", Tabel_data.getValueAt(i, 3));
                map2.put("LEMBUR", Double.valueOf(Tabel_data.getValueAt(i, 4).toString()));
                map2.put("POT_TERLAMBAT", Double.valueOf(Tabel_data.getValueAt(i, 5).toString()));
                map2.put("POT_IJIN", Double.valueOf(Tabel_data.getValueAt(i, 6).toString()));
                map2.put("POT_TRANSPORT", Double.valueOf(Tabel_data.getValueAt(i, 7).toString()));
                map2.put("POT_BPJS", Double.valueOf(Tabel_data.getValueAt(i, 8).toString()));
                map2.put("POT_BPJS_TK", Double.valueOf(Tabel_data.getValueAt(i, 9).toString()));
                map2.put("TUNJANGAN_HADIR", Double.valueOf(Tabel_data.getValueAt(i, 10).toString()));
                map2.put("GAJI_HARIAN", Double.valueOf(Tabel_data.getValueAt(i, 11).toString()));
                map2.put("GAJI_BORONG", Double.valueOf(Tabel_data.getValueAt(i, 12).toString()));
                map2.put("BONUS_TBT", Double.valueOf(Tabel_data.getValueAt(i, 13).toString()));
                map2.put("BONUS_LP", Double.valueOf(Tabel_data.getValueAt(i, 14).toString()));
                map2.put("BONUS_MK_UTUH", Double.valueOf(Tabel_data.getValueAt(i, 15).toString()));
                map2.put("BONUS_PENCAPAIAN_PRODUKSI", Double.valueOf(Tabel_data.getValueAt(i, 16).toString()));
                map2.put("PIUTANG", Double.valueOf(Tabel_data.getValueAt(i, 17).toString()));
                map2.put("KETERANGAN", Tabel_data.getValueAt(i, 19));
                JasperPrint JASP_PRINT2 = JasperFillManager.fillReport(JASP_REP2, map2, Utility.db.getConnection());
                List pages = JASP_PRINT2.getPages();
                for (int k = 0; k < pages.size(); k++) {
                    JRPrintPage object = (JRPrintPage) pages.get(k);
                    JASP_PRINT.addPage(object);
                }
            }
            JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_payrol_harian.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_slip_harianActionPerformed

    private void button_slip_borong_cabutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_slip_borong_cabutActionPerformed
        // TODO add your handling code here:

        try {
            Date tanggal_mulai = new Date(dateFormat.parse(Tabel_data.getValueAt(0, 0).toString()).getTime() - (7 * 24 * 60 * 60 * 1000));
            Date tanggal_selesai = new Date(dateFormat.parse(Tabel_data.getValueAt(0, 0).toString()).getTime() - (1 * 24 * 60 * 60 * 1000));
            //            JRDesignQuery newQuery = new JRDesignQuery();
            //            newQuery.setText(sql);
            JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Slip_Gaji_karyawan_Cabut.jrxml");
            //            JASP_DESIGN.setQuery(newQuery);
            Map<String, Object> map = new HashMap<>();
            map.put("HALAMAN", 1);
            map.put("PERIODE_AWAL", tanggal_mulai);
            map.put("PERIODE_AKHIR", tanggal_selesai);
            map.put("ID_PEGAWAI", Tabel_data.getValueAt(0, 1).toString());
            map.put("NAMA_PEGAWAI", Tabel_data.getValueAt(0, 2).toString());
            map.put("GRUP", Tabel_data.getValueAt(0, 3));
            map.put("LEMBUR", Double.valueOf(Tabel_data.getValueAt(0, 4).toString()));
            map.put("POT_TERLAMBAT", Double.valueOf(Tabel_data.getValueAt(0, 5).toString()));
            map.put("POT_IJIN", Double.valueOf(Tabel_data.getValueAt(0, 6).toString()));
            map.put("POT_TRANSPORT", Double.valueOf(Tabel_data.getValueAt(0, 7).toString()));
            map.put("POT_BPJS", Double.valueOf(Tabel_data.getValueAt(0, 8).toString()));
            map.put("POT_BPJS_TK", Double.valueOf(Tabel_data.getValueAt(0, 9).toString()));
            map.put("TUNJANGAN_HADIR", Double.valueOf(Tabel_data.getValueAt(0, 10).toString()));
            map.put("GAJI_HARIAN", Double.valueOf(Tabel_data.getValueAt(0, 11).toString()));
            map.put("GAJI_BORONG", Double.valueOf(Tabel_data.getValueAt(0, 12).toString()));
            map.put("BONUS_TBT", Double.valueOf(Tabel_data.getValueAt(0, 13).toString()));
            map.put("BONUS_LP", Double.valueOf(Tabel_data.getValueAt(0, 14).toString()));
            map.put("BONUS_MK_UTUH", Double.valueOf(Tabel_data.getValueAt(0, 15).toString()));
            map.put("BONUS_PENCAPAIAN_PRODUKSI", Double.valueOf(Tabel_data.getValueAt(0, 16).toString()));
            map.put("PIUTANG", Double.valueOf(Tabel_data.getValueAt(0, 17).toString()));
            map.put("KETERANGAN", Tabel_data.getValueAt(0, 19));
            JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
            JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, map, Utility.db.getConnection());
            for (int i = 1; i < Tabel_data.getRowCount() - 1; i++) {
                tanggal_mulai = new Date(dateFormat.parse(Tabel_data.getValueAt(i, 0).toString()).getTime() - (7 * 24 * 60 * 60 * 1000));
                tanggal_selesai = new Date(dateFormat.parse(Tabel_data.getValueAt(i, 0).toString()).getTime() - (1 * 24 * 60 * 60 * 1000));
                JasperDesign JASP_DESIGN2 = JRXmlLoader.load("Report\\Slip_Gaji_karyawan_Cabut.jrxml");
                JasperReport JASP_REP2 = JasperCompileManager.compileReport(JASP_DESIGN2);
                Map<String, Object> map2 = new HashMap<>();
                map2.put("HALAMAN", i + 1);
                map2.put("PERIODE_AWAL", tanggal_mulai);
                map2.put("PERIODE_AKHIR", tanggal_selesai);
                map2.put("ID_PEGAWAI", Tabel_data.getValueAt(i, 1).toString());
                map2.put("NAMA_PEGAWAI", Tabel_data.getValueAt(i, 2).toString());
                map2.put("GRUP", Tabel_data.getValueAt(i, 3));
                map2.put("LEMBUR", Double.valueOf(Tabel_data.getValueAt(i, 4).toString()));
                map2.put("POT_TERLAMBAT", Double.valueOf(Tabel_data.getValueAt(i, 5).toString()));
                map2.put("POT_IJIN", Double.valueOf(Tabel_data.getValueAt(i, 6).toString()));
                map2.put("POT_TRANSPORT", Double.valueOf(Tabel_data.getValueAt(i, 7).toString()));
                map2.put("POT_BPJS", Double.valueOf(Tabel_data.getValueAt(i, 8).toString()));
                map2.put("POT_BPJS_TK", Double.valueOf(Tabel_data.getValueAt(i, 9).toString()));
                map2.put("TUNJANGAN_HADIR", Double.valueOf(Tabel_data.getValueAt(i, 10).toString()));
                map2.put("GAJI_HARIAN", Double.valueOf(Tabel_data.getValueAt(i, 11).toString()));
                map2.put("GAJI_BORONG", Double.valueOf(Tabel_data.getValueAt(i, 12).toString()));
                map2.put("BONUS_TBT", Double.valueOf(Tabel_data.getValueAt(i, 13).toString()));
                map2.put("BONUS_LP", Double.valueOf(Tabel_data.getValueAt(i, 14).toString()));
                map2.put("BONUS_MK_UTUH", Double.valueOf(Tabel_data.getValueAt(i, 15).toString()));
                map2.put("BONUS_PENCAPAIAN_PRODUKSI", Double.valueOf(Tabel_data.getValueAt(i, 16).toString()));
                map2.put("PIUTANG", Double.valueOf(Tabel_data.getValueAt(i, 17).toString()));
                map2.put("KETERANGAN", Tabel_data.getValueAt(i, 19));
                JasperPrint JASP_PRINT2 = JasperFillManager.fillReport(JASP_REP2, map2, Utility.db.getConnection());
                List pages = JASP_PRINT2.getPages();
                for (int k = 0; k < pages.size(); k++) {
                    JRPrintPage object = (JRPrintPage) pages.get(k);
                    JASP_PRINT.addPage(object);
                }
            }
            JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_payrol_harian.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_slip_borong_cabutActionPerformed

    private void button_slip_borong_cetakActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_slip_borong_cetakActionPerformed
        // TODO add your handling code here:

        try {
            Date tanggal_mulai = new Date(dateFormat.parse(Tabel_data.getValueAt(0, 0).toString()).getTime() - (7 * 24 * 60 * 60 * 1000));
            Date tanggal_selesai = new Date(dateFormat.parse(Tabel_data.getValueAt(0, 0).toString()).getTime() - (1 * 24 * 60 * 60 * 1000));
            //            JRDesignQuery newQuery = new JRDesignQuery();
            //            newQuery.setText(sql);
            JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Slip_Gaji_karyawan_Cetak.jrxml");
            //            JASP_DESIGN.setQuery(newQuery);
            Map<String, Object> map = new HashMap<>();
            map.put("HALAMAN", 1);
            map.put("PERIODE_AWAL", tanggal_mulai);
            map.put("PERIODE_AKHIR", tanggal_selesai);
            map.put("ID_PEGAWAI", Tabel_data.getValueAt(0, 1).toString());
            map.put("NAMA_PEGAWAI", Tabel_data.getValueAt(0, 2).toString());
            map.put("GRUP", Tabel_data.getValueAt(0, 3));
            map.put("LEMBUR", Double.valueOf(Tabel_data.getValueAt(0, 4).toString()));
            map.put("POT_TERLAMBAT", Double.valueOf(Tabel_data.getValueAt(0, 5).toString()));
            map.put("POT_IJIN", Double.valueOf(Tabel_data.getValueAt(0, 6).toString()));
            map.put("POT_TRANSPORT", Double.valueOf(Tabel_data.getValueAt(0, 7).toString()));
            map.put("POT_BPJS", Double.valueOf(Tabel_data.getValueAt(0, 8).toString()));
            map.put("POT_BPJS_TK", Double.valueOf(Tabel_data.getValueAt(0, 9).toString()));
            map.put("TUNJANGAN_HADIR", Double.valueOf(Tabel_data.getValueAt(0, 10).toString()));
            map.put("GAJI_HARIAN", Double.valueOf(Tabel_data.getValueAt(0, 11).toString()));
            map.put("GAJI_BORONG", Double.valueOf(Tabel_data.getValueAt(0, 12).toString()));
            map.put("BONUS_TBT", Double.valueOf(Tabel_data.getValueAt(0, 13).toString()));
            map.put("BONUS_LP", Double.valueOf(Tabel_data.getValueAt(0, 14).toString()));
            map.put("BONUS_MK_UTUH", Double.valueOf(Tabel_data.getValueAt(0, 15).toString()));
            map.put("BONUS_PENCAPAIAN_PRODUKSI", Double.valueOf(Tabel_data.getValueAt(0, 16).toString()));
            map.put("PIUTANG", Double.valueOf(Tabel_data.getValueAt(0, 17).toString()));
            map.put("KETERANGAN", Tabel_data.getValueAt(0, 19));
            JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
            JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, map, Utility.db.getConnection());
            for (int i = 1; i < Tabel_data.getRowCount() - 1; i++) {
                tanggal_mulai = new Date(dateFormat.parse(Tabel_data.getValueAt(i, 0).toString()).getTime() - (7 * 24 * 60 * 60 * 1000));
                tanggal_selesai = new Date(dateFormat.parse(Tabel_data.getValueAt(i, 0).toString()).getTime() - (1 * 24 * 60 * 60 * 1000));
                JasperDesign JASP_DESIGN2 = JRXmlLoader.load("Report\\Slip_Gaji_karyawan_Cetak.jrxml");
                JasperReport JASP_REP2 = JasperCompileManager.compileReport(JASP_DESIGN2);
                Map<String, Object> map2 = new HashMap<>();
                map2.put("HALAMAN", i + 1);
                map2.put("PERIODE_AWAL", tanggal_mulai);
                map2.put("PERIODE_AKHIR", tanggal_selesai);
                map2.put("ID_PEGAWAI", Tabel_data.getValueAt(i, 1).toString());
                map2.put("NAMA_PEGAWAI", Tabel_data.getValueAt(i, 2).toString());
                map2.put("GRUP", Tabel_data.getValueAt(i, 3));
                map2.put("LEMBUR", Double.valueOf(Tabel_data.getValueAt(i, 4).toString()));
                map2.put("POT_TERLAMBAT", Double.valueOf(Tabel_data.getValueAt(i, 5).toString()));
                map2.put("POT_IJIN", Double.valueOf(Tabel_data.getValueAt(i, 6).toString()));
                map2.put("POT_TRANSPORT", Double.valueOf(Tabel_data.getValueAt(i, 7).toString()));
                map2.put("POT_BPJS", Double.valueOf(Tabel_data.getValueAt(i, 8).toString()));
                map2.put("POT_BPJS_TK", Double.valueOf(Tabel_data.getValueAt(i, 9).toString()));
                map2.put("TUNJANGAN_HADIR", Double.valueOf(Tabel_data.getValueAt(i, 10).toString()));
                map2.put("GAJI_HARIAN", Double.valueOf(Tabel_data.getValueAt(i, 11).toString()));
                map2.put("GAJI_BORONG", Double.valueOf(Tabel_data.getValueAt(i, 12).toString()));
                map2.put("BONUS_TBT", Double.valueOf(Tabel_data.getValueAt(i, 13).toString()));
                map2.put("BONUS_LP", Double.valueOf(Tabel_data.getValueAt(i, 14).toString()));
                map2.put("BONUS_MK_UTUH", Double.valueOf(Tabel_data.getValueAt(i, 15).toString()));
                map2.put("BONUS_PENCAPAIAN_PRODUKSI", Double.valueOf(Tabel_data.getValueAt(i, 16).toString()));
                map2.put("PIUTANG", Double.valueOf(Tabel_data.getValueAt(i, 17).toString()));
                map2.put("KETERANGAN", Tabel_data.getValueAt(i, 19));
                JasperPrint JASP_PRINT2 = JasperFillManager.fillReport(JASP_REP2, map2, Utility.db.getConnection());
                List pages = JASP_PRINT2.getPages();
                for (int k = 0; k < pages.size(); k++) {
                    JRPrintPage object = (JRPrintPage) pages.get(k);
                    JASP_PRINT.addPage(object);
                }
            }
            JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_payrol_harian.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_slip_borong_cetakActionPerformed

    private void button_slip_mandiri_cetakActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_slip_mandiri_cetakActionPerformed
        // TODO add your handling code here:

        try {
            Date tanggal_mulai = new Date(dateFormat.parse(Tabel_data.getValueAt(0, 0).toString()).getTime() - (7 * 24 * 60 * 60 * 1000));
            Date tanggal_selesai = new Date(dateFormat.parse(Tabel_data.getValueAt(0, 0).toString()).getTime() - (1 * 24 * 60 * 60 * 1000));
            //            JRDesignQuery newQuery = new JRDesignQuery();
            //            newQuery.setText(sql);
            JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Slip_Gaji_karyawan_Cetak.jrxml");
            //            JASP_DESIGN.setQuery(newQuery);
            Map<String, Object> map = new HashMap<>();
            map.put("HALAMAN", 1);
            map.put("PERIODE_AWAL", tanggal_mulai);
            map.put("PERIODE_AKHIR", tanggal_selesai);
            map.put("ID_PEGAWAI", Tabel_data.getValueAt(0, 1).toString());
            map.put("NAMA_PEGAWAI", Tabel_data.getValueAt(0, 2).toString());
            map.put("GRUP", Tabel_data.getValueAt(0, 3));
            map.put("LEMBUR", Double.valueOf("0.0"));
            map.put("POT_TERLAMBAT", Double.valueOf(Tabel_data.getValueAt(0, 5).toString()));
            map.put("POT_IJIN", Double.valueOf(Tabel_data.getValueAt(0, 6).toString()));
            map.put("POT_TRANSPORT", Double.valueOf(Tabel_data.getValueAt(0, 7).toString()));
            map.put("POT_BPJS", Double.valueOf(Tabel_data.getValueAt(0, 8).toString()));
            map.put("POT_BPJS_TK", Double.valueOf(Tabel_data.getValueAt(0, 9).toString()));
            map.put("TUNJANGAN_HADIR", Double.valueOf(Tabel_data.getValueAt(0, 10).toString()));
            map.put("GAJI_HARIAN", Double.valueOf("0.0"));
            map.put("GAJI_BORONG", Double.valueOf(Tabel_data.getValueAt(0, 12).toString()));
            map.put("BONUS_TBT", Double.valueOf(Tabel_data.getValueAt(0, 13).toString()));
            map.put("BONUS_LP", Double.valueOf(Tabel_data.getValueAt(0, 14).toString()));
            map.put("BONUS_MK_UTUH", Double.valueOf(Tabel_data.getValueAt(0, 15).toString()));
            map.put("BONUS_PENCAPAIAN_PRODUKSI", Double.valueOf(Tabel_data.getValueAt(0, 16).toString()));
            map.put("PIUTANG", Double.valueOf(Tabel_data.getValueAt(0, 17).toString()));
            map.put("KETERANGAN", Tabel_data.getValueAt(0, 19));
            JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
            JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, map, Utility.db.getConnection());
            for (int i = 1; i < Tabel_data.getRowCount() - 1; i++) {
                tanggal_mulai = new Date(dateFormat.parse(Tabel_data.getValueAt(i, 0).toString()).getTime() - (7 * 24 * 60 * 60 * 1000));
                tanggal_selesai = new Date(dateFormat.parse(Tabel_data.getValueAt(i, 0).toString()).getTime() - (1 * 24 * 60 * 60 * 1000));
                JasperDesign JASP_DESIGN2 = JRXmlLoader.load("Report\\Slip_Gaji_karyawan_Cetak.jrxml");
                JasperReport JASP_REP2 = JasperCompileManager.compileReport(JASP_DESIGN2);
                Map<String, Object> map2 = new HashMap<>();
                map2.put("HALAMAN", i + 1);
                map2.put("PERIODE_AWAL", tanggal_mulai);
                map2.put("PERIODE_AKHIR", tanggal_selesai);
                map2.put("ID_PEGAWAI", Tabel_data.getValueAt(i, 1).toString());
                map2.put("NAMA_PEGAWAI", Tabel_data.getValueAt(i, 2).toString());
                map2.put("GRUP", Tabel_data.getValueAt(i, 3));
                map2.put("LEMBUR", Double.valueOf("0.0"));
                map2.put("POT_TERLAMBAT", Double.valueOf(Tabel_data.getValueAt(i, 5).toString()));
                map2.put("POT_IJIN", Double.valueOf(Tabel_data.getValueAt(i, 6).toString()));
                map2.put("POT_TRANSPORT", Double.valueOf(Tabel_data.getValueAt(i, 7).toString()));
                map2.put("POT_BPJS", Double.valueOf(Tabel_data.getValueAt(i, 8).toString()));
                map2.put("POT_BPJS_TK", Double.valueOf(Tabel_data.getValueAt(i, 9).toString()));
                map2.put("TUNJANGAN_HADIR", Double.valueOf(Tabel_data.getValueAt(i, 10).toString()));
                map2.put("GAJI_HARIAN", Double.valueOf("0.0"));
                map2.put("GAJI_BORONG", Double.valueOf(Tabel_data.getValueAt(i, 12).toString()));
                map2.put("BONUS_TBT", Double.valueOf(Tabel_data.getValueAt(i, 13).toString()));
                map2.put("BONUS_LP", Double.valueOf(Tabel_data.getValueAt(i, 14).toString()));
                map2.put("BONUS_MK_UTUH", Double.valueOf(Tabel_data.getValueAt(i, 15).toString()));
                map2.put("BONUS_PENCAPAIAN_PRODUKSI", Double.valueOf(Tabel_data.getValueAt(i, 16).toString()));
                map2.put("PIUTANG", Double.valueOf(Tabel_data.getValueAt(i, 17).toString()));
                map2.put("KETERANGAN", Tabel_data.getValueAt(i, 19));
                JasperPrint JASP_PRINT2 = JasperFillManager.fillReport(JASP_REP2, map2, Utility.db.getConnection());
                List pages = JASP_PRINT2.getPages();
                for (int k = 0; k < pages.size(); k++) {
                    JRPrintPage object = (JRPrintPage) pages.get(k);
                    JASP_PRINT.addPage(object);
                }
            }
            JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_payrol_harian.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_slip_mandiri_cetakActionPerformed

    private void txt_search_jam_kerjaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_jam_kerjaKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_search_jam_kerjaKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox CheckBox_hide_Gaji_0;
    private com.toedter.calendar.JDateChooser Date_penggajian1;
    private com.toedter.calendar.JDateChooser Date_penggajian2;
    private javax.swing.JTable Tabel_data;
    private javax.swing.JButton button_export;
    private javax.swing.JButton button_load;
    private javax.swing.JButton button_slip_borong_cabut;
    private javax.swing.JButton button_slip_borong_cetak;
    private javax.swing.JButton button_slip_harian;
    private javax.swing.JButton button_slip_mandiri_cetak;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField txt_search_bagian;
    private javax.swing.JTextField txt_search_jam_kerja;
    private javax.swing.JTextField txt_search_level_gaji;
    private javax.swing.JTextField txt_search_nama;
    // End of variables declaration//GEN-END:variables
}
