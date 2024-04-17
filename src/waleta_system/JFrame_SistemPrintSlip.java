package waleta_system;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;
import waleta_system.Class.ACR122U_ReaderHelper;
import waleta_system.Class.Utility;
import static org.apache.xmlbeans.impl.util.HexBin.bytesToString;

public class JFrame_SistemPrintSlip extends javax.swing.JFrame {

    private static List<Exception> exceptionList = new ArrayList<>();
    String sql;
    ResultSet rs;
    PreparedStatement pst;
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    ACR122U_ReaderHelper reader;
    Thread thread;

    public JFrame_SistemPrintSlip() {
        initComponents();
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        // Create a new JPanel with a centered layout
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(Color.WHITE);
//
//        // Create GridBagConstraints to center the jPanel2 within centerPanel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.CENTER;
//
//        // Add jPanel2 to centerPanel with the center alignment constraint
        centerPanel.add(jPanel2, gbc);

        // Add centerPanel to jPanel1, your main container
        jPanel1.setLayout(new BorderLayout());
        jPanel1.add(centerPanel, BorderLayout.CENTER);

        thread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        reader = ACR122U_ReaderHelper.getInstance();
                        if (reader.connectReader()) {
                            label_reader_status.setText("NFC Reader Ready !");
                            label_reader_status.setForeground(new Color(0, 220, 0));//dark green
                            reader.waitForCardPresent(0);
                            reader.connectCard(null);
                            byte[] byteUID = reader.getUID();
                            PasswordField.setText(bytesToString(reader.getUID()).substring(0, 8));
                            ENTER();
                            reader.waitForCardAbsent(0);
                        } else {
                            reader.clearReaderList();
                            label_reader_status.setText("NFC Reader is not Ready !");
                            label_reader_status.setForeground(Color.red);//dark blue
                            Thread.sleep(2000);
                        }
                    } catch (Exception e) {
                        exceptionList.add(e);
                        Logger.getLogger(JFrame_SistemPrintSlip.class.getName()).log(Level.SEVERE, null, e);
                    }
                }
            }
        };
        thread.start();
    }

    public void getDataSlip(String id_karyawan, Date tanggal_penggajian) {
        try {
            Date tanggal_mulai = new Date(tanggal_penggajian.getTime() - (7 * 24 * 60 * 60 * 1000));
            Date tanggal_selesai = new Date(tanggal_penggajian.getTime() - (1 * 24 * 60 * 60 * 1000));

            sql = "SELECT COUNT(*) AS 'jumlah_terlambat'\n"
                    + "FROM (\n"
                    + "SELECT `id_pegawai`, COUNT(IF(`menit_terlambat` > 0, 1, NULL)) AS `terlambat`\n"
                    + "FROM `tb_lembur_rekap` \n"
                    + "WHERE `tanggal` BETWEEN '" + dateFormat.format(tanggal_mulai) + "' AND '" + dateFormat.format(tanggal_selesai) + "'\n"
                    + "AND `grup` NOT LIKE '%BORONG%'\n"
                    + "GROUP BY `id_pegawai`\n"
                    + "HAVING `terlambat` > 0\n"
                    + ") AS subquery;";
            ResultSet result = Utility.db.getStatement().executeQuery(sql);
            if (result.next()) {
                txt_jumlah_karyawan_terlambat.setText(result.getString("jumlah_terlambat"));
            }

            sql = "SELECT `tb_karyawan`.`id_pegawai`,`pin_finger`,`nama_pegawai`,`nama_bagian`,`kode_departemen`, `posisi`, `potongan_bpjs`, `potongan_bpjs_tk`, A.`grup`, `tb_karyawan`.`jam_kerja` AS 'jam_kerja_current', `tb_karyawan`.`level_gaji` AS 'level_gaji_current', A.`jam_kerja`, A.`level_gaji`, `bonus_pencapaian_produksi`, `piutang` "
                    + "FROM `tb_karyawan` "
                    + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`"
                    + "LEFT JOIN (SELECT `id_pegawai`, `jam_kerja`, `level_gaji`, MAX(`grup`) AS 'grup' "
                    + "FROM `tb_lembur_rekap` "
                    + "WHERE `tanggal` BETWEEN '" + dateFormat.format(tanggal_mulai) + "' AND '" + dateFormat.format(tanggal_selesai) + "' GROUP BY `id_pegawai`) "
                    + "A ON `tb_karyawan`.`id_pegawai` = A.`id_pegawai` "
                    + "LEFT JOIN (SELECT `id_pegawai`, SUM(`bonus_produksi`) AS 'bonus_pencapaian_produksi' "
                    + "FROM `tb_bonus_pencapaian_produksi` "
                    + "WHERE `tanggal` BETWEEN '" + dateFormat.format(tanggal_mulai) + "' AND '" + dateFormat.format(tanggal_selesai) + "' GROUP BY `id_pegawai`) "
                    + "B ON `tb_karyawan`.`id_pegawai` = B.`id_pegawai` "
                    + "LEFT JOIN (SELECT `id_pegawai`, SUM(`nominal_piutang`) AS 'piutang' "
                    + "FROM `tb_piutang_karyawan` "
                    + "WHERE `tanggal_piutang` <= '" + dateFormat.format(tanggal_selesai) + "' AND `status` = '0' "
                    + "GROUP BY `id_pegawai`) "
                    + "C ON `tb_karyawan`.`id_pegawai` = C.`id_pegawai` "
                    + "WHERE `tb_karyawan`.`id_pegawai` = '" + id_karyawan + "' "
                    + "AND `posisi` IN ('PEJUANG') "
                    + "AND (A.`jam_kerja` <> 'SHIFT_MALAM' OR A.`jam_kerja` IS NULL) "
                    + "AND `kode_departemen` <> 'SUB' "
                    + "AND `status` IN ('IN', 'OUT', 'ABSEN')"
                    + "AND (A.`id_pegawai` IS NOT NULL OR B.`id_pegawai` IS NOT NULL OR C.`id_pegawai` IS NOT NULL) "
                    + "GROUP BY `tb_karyawan`.`id_pegawai` "
                    + "ORDER BY A.`grup` ";
            pst = Utility.db.getConnection().prepareStatement(sql);
            rs = pst.executeQuery();
            Object[] row = new Object[40];
            int no = 1;
            while (rs.next()) {
                double total_jam_lembur = 0, total_menit_terlambat = 0, jumlah_hari_terlambat = 0;
                double upah_lembur_seminggu = 0, total_potongan_terlambat = 0, total_potongan_ijin = 0;
                double total_gaji_borong = 0, total_bonus1 = 0, total_bonus2 = 0;
                double hari_masuk_premi = 0, libur_dapat_premi = 0, hari_masuk_kerja = 0, hari_kerja_normal = 0;
                double dapat_premi = 1, premi_hadir_per_hari = 0, denda_terlambat_per_hari = 0;
                double hari_transport = 0, potongan_bpjs = 0, potongan_bpjs_tk = 0;
                double upah_per_hari = 0, total_upah_harian = 0;
                double lembur_kali = 0;
                String id_pegawai = rs.getString("id_pegawai");
                String nama_pegawai = rs.getString("nama_pegawai");
                String posisi = rs.getString("posisi");
                String level_gaji = rs.getString("level_gaji_current");
                String jam_kerja = rs.getString("jam_kerja_current");
                String keterangan_premi = "", kode_grup = "";
                if (rs.getString("level_gaji") != null) {
                    level_gaji = rs.getString("level_gaji");
                }
                if (rs.getString("jam_kerja") != null) {
                    jam_kerja = rs.getString("jam_kerja");
                }
                boolean ikut_jemputan = false;
                row[0] = no;
                row[1] = id_pegawai;
                row[2] = nama_pegawai;
                if (rs.getInt("potongan_bpjs") == 1) {
                    potongan_bpjs = Integer.valueOf(txt_potongan_bpjs.getText());
                }
                if (rs.getInt("potongan_bpjs_tk") == 1) {
                    potongan_bpjs_tk = Integer.valueOf(txt_potongan_bpjs_tk.getText());
                }
                for (int i = 0; i < 7; i++) {
                    Date header = new Date(tanggal_mulai.getTime() + (i * 24 * 60 * 60 * 1000));
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(header);
                    int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
                    if (dayOfWeek == 0) {
                        dayOfWeek = 7;
                    }
                    String query_jam_kerja = "SELECT `masuk" + dayOfWeek + "` FROM `tb_jam_kerja` WHERE `jam_kerja` = '" + jam_kerja + "'";
                    PreparedStatement pst_jam_kerja = Utility.db.getConnection().prepareStatement(query_jam_kerja);
                    ResultSet rs_jam_kerja = pst_jam_kerja.executeQuery();
                    if (rs_jam_kerja.next()) {
                        if (rs_jam_kerja.getString("masuk" + dayOfWeek) != null) {
                            String query_cek_libur = "SELECT * FROM `tb_libur` WHERE `tanggal_libur` = '" + dateFormat.format(header) + "'";
                            PreparedStatement pst_cek_libur = Utility.db.getConnection().prepareStatement(query_cek_libur);
                            ResultSet rs_cek_libur = pst_cek_libur.executeQuery();
                            if (!rs_cek_libur.next()) {
                                hari_kerja_normal++;
                            }
                        }
                    }
                    String sql1 = "SELECT `id_pegawai`, `tanggal`, `jumlah_lembur_menit`, `menit_terlambat`, `menit_ijin`, `tb_lembur_rekap`.`premi_hadir`, `grup`, `tb_lembur_rekap`.`level_gaji`, "
                            + "`tb_level_gaji`.`upah_per_hari`, `lembur_per_jam`, `lembur_x_hari_kerja`, `lembur_x_hari_libur`, `tb_level_gaji`.`premi_hadir` AS 'nominal_premi_per_hari', `denda_terlambat`, `jalur_jemputan`, `potongan_bpjs`, `potongan_bpjs_tk`, `gaji_borong`, `bonus1_kecepatan`, `bonus2` "
                            + "FROM `tb_lembur_rekap` \n"
                            + "LEFT JOIN `tb_level_gaji` ON `tb_lembur_rekap`.`level_gaji` = `tb_level_gaji`.`level_gaji`\n"
                            + "WHERE `id_pegawai` = '" + id_pegawai + "' "
                            + "AND `tanggal` = '" + dateFormat.format(header) + "' ";
                    PreparedStatement pst1 = Utility.db.getConnection().prepareStatement(sql1);
                    ResultSet rs1 = pst1.executeQuery();
                    if (rs1.next()) {
                        lembur_kali = rs1.getFloat("lembur_x_hari_kerja");
                        boolean masuk_kerja = false;
                        if (rs1.getObject("premi_hadir") != null) {
                            switch (rs1.getInt("premi_hadir")) {
                                case 0:
                                    dapat_premi = 0;
                                    hari_transport++;
                                    keterangan_premi = keterangan_premi + dateFormat.format(header) + "(Jam Kerja Kurang);";
                                    break;
                                case 1:
                                    masuk_kerja = true;
                                    hari_masuk_kerja++;
                                    dapat_premi = 0;
                                    hari_transport++;
                                    keterangan_premi = keterangan_premi + dateFormat.format(header) + "(Tidak Absen Masuk);";
                                    break;
                                case 2:
                                    masuk_kerja = true;
                                    hari_masuk_kerja++;
                                    dapat_premi = 0;
                                    hari_transport++;
                                    keterangan_premi = keterangan_premi + dateFormat.format(header) + "(Tidak Absen Pulang);";
                                    break;
                                case 3:
                                    masuk_kerja = true;
                                    hari_masuk_kerja++;
                                    hari_masuk_premi++;
                                    hari_transport++;
                                    break;
                                case 4:
                                    libur_dapat_premi++;
                                    lembur_kali = rs1.getFloat("lembur_x_hari_libur");
                                    break;
                                case 5://lembur di hari libur, tapi tidak dapat premi
                                    lembur_kali = rs1.getFloat("lembur_x_hari_libur");
                                    break;
                                case 6://kode cuti bersama
                                    hari_transport++;
                                    keterangan_premi = keterangan_premi + dateFormat.format(header) + "(Jam Kerja Kurang di Cuti bersama);";
                                    break;
                                case 7:
                                    masuk_kerja = true;
                                    hari_masuk_kerja++;
                                    hari_transport++;
                                    keterangan_premi = keterangan_premi + dateFormat.format(header) + "(Tidak Absen Masuk di Cuti bersama);";
                                    break;
                                case 8:
                                    masuk_kerja = true;
                                    hari_masuk_kerja++;
                                    hari_transport++;
                                    keterangan_premi = keterangan_premi + dateFormat.format(header) + "(Tidak Absen Pulang di Cuti bersama);";
                                    break;
                                case 9:
                                    masuk_kerja = true;
                                    hari_masuk_kerja++;
                                    hari_masuk_premi++;
                                    hari_transport++;
                                    break;
                                default:
                                    break;
                            }
                        }

                        upah_per_hari = rs1.getDouble("upah_per_hari");
                        if (rs1.getDouble("upah_per_hari") > 0 && masuk_kerja) {
                            total_upah_harian = total_upah_harian + upah_per_hari;
                        }
                        if (rs1.getDouble("nominal_premi_per_hari") > 0) {
                            premi_hadir_per_hari = rs1.getDouble("nominal_premi_per_hari");
                        }
                        if (rs1.getDouble("denda_terlambat") > 0) {
                            denda_terlambat_per_hari = rs1.getDouble("denda_terlambat");
                        }
                        if (rs1.getString("jalur_jemputan") != null && !rs1.getString("jalur_jemputan").equals("")) {
                            ikut_jemputan = true;
                        }
                        if (rs1.getString("grup") != null) {
                            kode_grup = rs1.getString("grup");
                        }
                        double menit_terlambat = 0;
                        if (rs1.getInt("menit_terlambat") > 0) {
                            menit_terlambat = rs1.getInt("menit_terlambat");
                            total_menit_terlambat = total_menit_terlambat + rs1.getInt("menit_terlambat");
                            jumlah_hari_terlambat++;
                        }
                        double menit_ijin = 0;
                        if (rs1.getInt("menit_ijin") > 0) {
                            menit_ijin = rs1.getInt("menit_ijin");
                        }
                        if (rs1.getDouble("gaji_borong") > 0) {
                            total_gaji_borong = total_gaji_borong + rs1.getDouble("gaji_borong");
                        }
                        if (rs1.getDouble("bonus1_kecepatan") > 0) {
                            total_bonus1 = total_bonus1 + rs1.getDouble("bonus1_kecepatan");
                        }
                        if (rs1.getDouble("bonus2") > 0) {
                            total_bonus2 = total_bonus2 + rs1.getDouble("bonus2");
                        }
                        if (rs1.getInt("potongan_bpjs") == 1) {
                            potongan_bpjs = Integer.valueOf(txt_potongan_bpjs.getText());
                        }
                        if (rs1.getInt("potongan_bpjs_tk") == 1) {
                            potongan_bpjs_tk = Integer.valueOf(txt_potongan_bpjs_tk.getText());
                        }

                        double lembur_jam = 0, jumlah_lembur_menit = rs1.getDouble("jumlah_lembur_menit");
                        if (rs1.getString("level_gaji") != null) {
                            level_gaji = rs1.getString("level_gaji");
                            if (level_gaji.toUpperCase().contains("BORONG")) {
                                jumlah_lembur_menit = 0;
                                menit_terlambat = 0;
                                menit_ijin = 0;
                                jumlah_hari_terlambat = 0;
                            }
                        }

                        if (jumlah_lembur_menit > 0) {
                            lembur_jam = jumlah_lembur_menit / 60d;//hanya disederhanakan dari tampilan menit ke tampilan jam
                            total_jam_lembur = total_jam_lembur + lembur_jam;
                        }

                        double upah_per_jam = upah_per_hari / 7d;
                        double upah_lembur_per_jam = rs1.getFloat("lembur_per_jam");

//                            if (rs1.getInt("premi_hadir") == 4 || rs1.getInt("premi_hadir") == 5) {
//                                if (rs.getString("nama_bagian").contains("SECURITY") || rs.getString("nama_bagian").contains("DRIVER")) {
//                                    lembur_kali = 1.5d;
//                                } else {
//                                    lembur_kali = 2d;
//                                }
//                            }
                        double upah_lembur = lembur_jam * upah_lembur_per_jam * lembur_kali;
//                            if (rs.getString("nama_bagian").contains("SECURITY")) {
//                                upah_lembur = lembur_jam * 10000;
//                            }
                        upah_lembur_seminggu = upah_lembur_seminggu + upah_lembur;
                        double potongan_terlambat = menit_terlambat * (upah_per_jam / 60.d) * 1.5d;
                        total_potongan_terlambat = total_potongan_terlambat + potongan_terlambat;
                        double potongan_ijin = menit_ijin * (upah_per_jam / 60.d) * 1.5d;
                        total_potongan_ijin = total_potongan_ijin + potongan_ijin;
//                    if (total_lembur > 1) {
//                        upah_lembur = (upah_per_jam * 1.5f) + ((total_lembur - 1) * upah_per_jam * 2);
//                    } else {
//                        upah_lembur = total_lembur * upah_per_jam * 1.5f;
//                    }
                        row[i + 3] = lembur_jam;
                    } else {
                        row[i + 3] = null;
                    }
                }

                if (total_menit_terlambat > 0) {
                    keterangan_premi = keterangan_premi + "Total Menit terlambat:" + Math.round(total_menit_terlambat) + ";";
                }

                double potongan_transport = 0;
                if (ikut_jemputan) {
                    potongan_transport = hari_transport * Integer.valueOf(txt_potongan_transport.getText());
                }

                double denda_terlambat = jumlah_hari_terlambat * denda_terlambat_per_hari;
                if (Integer.valueOf(txt_jumlah_karyawan_terlambat.getText()) <= 30) {
                    denda_terlambat = 0;
                }

                double premi_hadir = 0;
                if (dapat_premi > 0 && hari_masuk_premi >= hari_kerja_normal) {
                    premi_hadir = (hari_masuk_premi + libur_dapat_premi) * premi_hadir_per_hari;
                    premi_hadir = premi_hadir - denda_terlambat;
                }

                String sql_tbt = "SELECT A.`id_pegawai` AS 'id1', B.`nama_pegawai` AS 'nama1', D.`nama_bagian` AS 'bagian1', "
                        + "A.`id_teman` AS 'id2', C.`nama_pegawai` AS 'nama2', E.`nama_bagian` AS 'bagian2', "
                        + "C.`tanggal_masuk`, C.`tanggal_keluar`, C.`status`, DATEDIFF(IF(C.`tanggal_keluar` IS NULL, '" + dateFormat.format(tanggal_penggajian) + "', C.`tanggal_keluar`), C.`tanggal_masuk`) AS 'hari_kerja' \n"
                        + "FROM `tb_temanbawateman` AS A "
                        + "LEFT JOIN `tb_karyawan` AS B ON A.`id_pegawai` = B.`id_pegawai`\n"
                        + "LEFT JOIN `tb_karyawan` AS C ON A.`id_teman` = C.`id_pegawai`\n"
                        + "LEFT JOIN `tb_bagian` AS D ON D.`kode_bagian` = B.`kode_bagian`\n"
                        + "LEFT JOIN `tb_bagian` AS E ON E.`kode_bagian` = C.`kode_bagian`\n"
                        + "WHERE A.`id_pegawai` = '" + id_pegawai + "' "
                        + "AND DATEDIFF(IF(C.`tanggal_keluar` IS NULL, '" + dateFormat.format(tanggal_penggajian) + "', C.`tanggal_keluar`), C.`tanggal_masuk`) > 20 "
                        + "AND DATEDIFF(IF(C.`tanggal_keluar` IS NULL, '" + dateFormat.format(tanggal_penggajian) + "', C.`tanggal_keluar`), C.`tanggal_masuk`) < 28 "
                        + "AND (C.`tanggal_keluar` IS NULL OR C.`tanggal_keluar` BETWEEN '" + dateFormat.format(tanggal_mulai) + "' AND '" + dateFormat.format(tanggal_selesai) + "')";
                PreparedStatement pst_tbt = Utility.db.getConnection().prepareStatement(sql_tbt);
                ResultSet rs_tbt = pst_tbt.executeQuery();
                int jumlah_teman = 0;
                while (rs_tbt.next()) {
                    jumlah_teman++;
                }
                double Bonus_TBT = jumlah_teman * Integer.valueOf(txt_bonus_tbt.getText());

                double piutang_karyawan = rs.getInt("piutang");

                double bonus_pencapaian_produksi = rs.getInt("bonus_pencapaian_produksi");

                double gaji = total_upah_harian //gaji harian
                        + premi_hadir //premi
                        + upah_lembur_seminggu //upah lembur
                        + total_gaji_borong //upah borong
                        + total_bonus1 //bonus kecepatan
                        + total_bonus2 //bonus 2
                        + bonus_pencapaian_produksi //bonus pencapaian produksi
                        + Bonus_TBT //bonus TBT
                        + piutang_karyawan //piutang karyawan
                        - potongan_bpjs //potongan bpjs kesehatan
                        - potongan_bpjs_tk //potongan bpjs ketenagakerjaan
                        - potongan_transport //potongan transport
                        - total_potongan_terlambat //potongan terlambat
                        - total_potongan_ijin; //potongan ijin
                if (gaji < 0) {
                    gaji = total_upah_harian //gaji harian
                            + premi_hadir //premi
                            + upah_lembur_seminggu //upah lembur
                            + total_gaji_borong //upah borong
                            + total_bonus1 //bonus kecepatan
                            + total_bonus2 //bonus 2
                            + bonus_pencapaian_produksi //bonus pencapaian produksi
                            + Bonus_TBT //bonus TBT
                            + piutang_karyawan //piutang karyawan
                            - potongan_transport //potongan transport
                            - total_potongan_terlambat //potongan terlambat
                            - total_potongan_ijin; //potongan ijin
                    potongan_bpjs = 0;//potongan bpjs dipotongkan di minggu depannya
                    potongan_bpjs_tk = 0;//potongan bpjs dipotongkan di minggu depannya
                    keterangan_premi = keterangan_premi + " Potongan BPJS 0, dipotong minggu depan;";
                }

                row[10] = total_jam_lembur;
                row[11] = Math.round(upah_lembur_seminggu * 1000.d) / 1000.d;
                row[12] = Math.round(total_potongan_terlambat * 1000.d) / 1000.d;
                row[13] = Math.round(total_potongan_ijin * 1000.d) / 1000.d;
                row[14] = premi_hadir; //premi
                row[15] = potongan_transport; //potongan transport
                row[16] = potongan_bpjs; //potongan bpjs
                row[17] = potongan_bpjs_tk; //potongan bpjs
                row[18] = Bonus_TBT; //bonus TBT
                row[19] = hari_masuk_kerja; //hari kerja
                row[20] = total_upah_harian; //gaji harian
                row[21] = total_gaji_borong; //gaji borong
                row[22] = total_bonus1; //bonus 1 kecepatan
                row[23] = total_bonus2; //bonus 2 mk utuh
                row[24] = bonus_pencapaian_produksi; //bonus pencapaian
                row[25] = piutang_karyawan; //piutang
                row[26] = Math.round(gaji * 1000.d) / 1000.d;//total gaji
                row[27] = keterangan_premi;
                row[28] = kode_grup;
                row[29] = level_gaji;
                row[30] = jam_kerja;

                JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Slip_Gaji_Karyawan_Struk.jrxml");
                Map<String, Object> map = new HashMap<>();
                map.put("PERIODE_AWAL", tanggal_mulai);
                map.put("PERIODE_AKHIR", tanggal_selesai);
                map.put("ID_PEGAWAI", id_pegawai);
                map.put("NAMA_PEGAWAI", nama_pegawai);
                map.put("LEMBUR", upah_lembur_seminggu);
                map.put("POT_TERLAMBAT", total_potongan_terlambat);
                map.put("POT_IJIN", total_potongan_ijin);
                map.put("TUNJANGAN_HADIR", premi_hadir);
                map.put("POT_TRANSPORT", potongan_transport);
                map.put("POT_BPJS", potongan_bpjs);
                map.put("POT_BPJS_TK", potongan_bpjs_tk);
                map.put("BONUS_TBT", Bonus_TBT);
                map.put("GAJI_HARIAN", total_upah_harian);
                map.put("GAJI_BORONG", total_gaji_borong);
                map.put("BONUS_LP", total_bonus1);
                map.put("BONUS_MK_UTUH", total_bonus2);
                map.put("BONUS_PENCAPAIAN_PRODUKSI", bonus_pencapaian_produksi);
                map.put("PIUTANG", piutang_karyawan);
                map.put("KETERANGAN", keterangan_premi);
                map.put("GRUP", kode_grup);
                JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
                JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, map, Utility.db.getConnection());
                JasperPrintManager.printReport(JASP_PRINT, false);//langsung print
//                JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)

                String Query = "INSERT INTO `tb_payrol_data_print_slip`(`id_pegawai`, `tanggal_penggajian`, `jumlah_print`, `print_time`) "
                        + "VALUES ('" + id_pegawai + "','" + dateFormat.format(tanggal_penggajian) + "', 1, NOW())"
                        + "ON DUPLICATE KEY UPDATE `jumlah_print` = `jumlah_print` + 1, `print_time` = NOW()";
                Utility.db.getConnection().createStatement();
                Utility.db.getStatement().executeUpdate(Query);
            }

        } catch (Exception ex) {
            exceptionList.add(ex);
            Logger.getLogger(JFrame_SistemPrintSlip.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Timestamp getAbsenMasuk(String pin, String jadwal_masuk) {
        Timestamp return_value = null;
        try {
            String get_absen_query = "SELECT `scan_date` FROM `att_log` "
                    + "WHERE `pin` = '" + pin + "' "
                    + "AND `scan_date` > DATE_ADD('" + jadwal_masuk + "', INTERVAL -2 HOUR) "
                    + "AND `scan_date` < DATE_ADD('" + jadwal_masuk + "', INTERVAL 2 HOUR) "
                    + "ORDER BY `scan_date` LIMIT 1";
            PreparedStatement get_absen_pst = Utility.db.getConnection().prepareStatement(get_absen_query);
            ResultSet get_absen_result = get_absen_pst.executeQuery();
            if (get_absen_result.next()) {
                return_value = get_absen_result.getTimestamp("scan_date");
            }
        } catch (SQLException ex) {
            Logger.getLogger(JFrame_SistemPrintSlip.class.getName()).log(Level.SEVERE, null, ex);
        }
        return return_value;
    }

    private Timestamp getAbsenPulang(String pin, String jadwal_pulang) {
        Timestamp return_value = null;
        try {
            String get_absen_query = "SELECT `scan_date` FROM `att_log` "
                    + "WHERE `pin` = '" + pin + "' "
                    + "AND `scan_date` > DATE_ADD('" + jadwal_pulang + "', INTERVAL -2 HOUR) "
                    + "AND `scan_date` < DATE_ADD('" + jadwal_pulang + "', INTERVAL 12 HOUR) "
                    + "ORDER BY `scan_date` ASC LIMIT 1";
            PreparedStatement get_absen_pst = Utility.db.getConnection().prepareStatement(get_absen_query);
            ResultSet get_absen_result = get_absen_pst.executeQuery();
            if (get_absen_result.next()) {
                return_value = get_absen_result.getTimestamp("scan_date");
            }
        } catch (SQLException ex) {
            Logger.getLogger(JFrame_SistemPrintSlip.class.getName()).log(Level.SEVERE, null, ex);
        }
        return return_value;
    }

    private String[] getSPLMasuk(String id, String tanggal) {
        String[] return_value = new String[2];
        try {
            String query_spl = "SELECT `mulai_lembur`, `tb_surat_lembur_detail`.`nomor_surat`, `tb_surat_lembur_detail`.`tanggal_lembur`, `tb_surat_lembur`.`disetujui`, `tb_surat_lembur`.`diketahui` "
                    + "FROM `tb_surat_lembur_detail` "
                    + "LEFT JOIN `tb_surat_lembur` ON `tb_surat_lembur_detail`.`nomor_surat` = `tb_surat_lembur`.`nomor_surat`"
                    + "WHERE `id_pegawai` = '" + id + "' "
                    + "AND `tb_surat_lembur_detail`.`tanggal_lembur` = '" + tanggal + "' "
                    + "AND `jenis_lembur` = 'Masuk' AND `tb_surat_lembur`.`disetujui` IS NOT NULL ";
            ResultSet rs_spl = Utility.db.getStatement().executeQuery(query_spl);
            if (rs_spl.next()) {
                return_value[0] = rs_spl.getString("nomor_surat");
                return_value[1] = rs_spl.getString("tanggal_lembur") + " " + rs_spl.getString("mulai_lembur");
                if (rs_spl.getString("diketahui") == null) {
//                    label_data_lembur_tidak_valid.setVisible(true);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(JFrame_SistemPrintSlip.class.getName()).log(Level.SEVERE, null, ex);
        }
        return return_value;
    }

    private boolean is_pengampunan_terlambat(String tanggal) {
        try {
            String query_pengampunan_terlambat = "SELECT * FROM `tb_pengampunan_terlambat` WHERE `tanggal_pengampunan` = '" + tanggal + "' ";
            ResultSet rs_pengampunan_terlambat = Utility.db.getStatement().executeQuery(query_pengampunan_terlambat);
            return rs_pengampunan_terlambat.next();
        } catch (SQLException ex) {
            Logger.getLogger(JFrame_SistemPrintSlip.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public void getDataSlip_SHIFT_MALAM(String id, Date tanggal_penggajian) {
        Date tanggal_mulai = new Date(tanggal_penggajian.getTime() - (7 * 24 * 60 * 60 * 1000));
        Date tanggal_selesai = new Date(tanggal_penggajian.getTime() - (1 * 24 * 60 * 60 * 1000));
        List<String> list_tanggal = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            Date header = new Date(tanggal_mulai.getTime() + (i * 24 * 60 * 60 * 1000));
            list_tanggal.add(new SimpleDateFormat("yyyy-MM-dd").format(header));
        }

        List<Object[]> data_lembur_sc = new ArrayList<>();
        try {
            SimpleDateFormat Timeformat = new SimpleDateFormat("HH:mm:ss");
            SimpleDateFormat TimeStampformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            sql = "SELECT `tb_jadwal_kerja_sc`.`id_pegawai`, `pin_finger`, `tb_karyawan`.`nama_pegawai`, "
                    + "CONCAT(IFNULL(`divisi_bagian`,''), '-', IFNULL(`bagian_bagian`,''), '-', IFNULL(`ruang_bagian`,'')) AS 'nama_bagian', `tb_karyawan`.`posisi`, `tb_karyawan`.`status`, `tb_karyawan`.`level_gaji`, `jam_kerja`, `upah_per_hari`, "
                    + "DAYNAME(`tanggal`) AS 'hari', `jenis_hari`, `tanggal`, `jadwal_masuk`, `jadwal_pulang`, IF(`jadwal_masuk`>`jadwal_pulang`, 'malam', 'pagi') AS 'jenis_shift', `durasi_istirahat` \n"
                    + "FROM `tb_jadwal_kerja_sc` \n"
                    + "LEFT JOIN `tb_karyawan` ON `tb_jadwal_kerja_sc`.`id_pegawai` = `tb_karyawan`.`id_pegawai`\n"
                    + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`\n"
                    + "LEFT JOIN `tb_level_gaji` ON `tb_karyawan`.`level_gaji` = `tb_level_gaji`.`level_gaji`\n"
                    + "WHERE "
                    + "`tb_jadwal_kerja_sc`.`id_pegawai` = '" + id + "' "
                    + "AND `tanggal` BETWEEN '" + dateFormat.format(tanggal_mulai) + "' AND '" + dateFormat.format(tanggal_selesai) + "' "
                    + "ORDER BY `tb_karyawan`.`nama_pegawai`, `tanggal`";
            pst = Utility.db.getConnection().prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                Object[] row = new Object[35];

                String id_pegawai = rs.getString("id_pegawai");
                String nama_pegawai = rs.getString("nama_pegawai");
                String pin_finger = rs.getString("pin_finger");
                String nama_bagian = rs.getString("nama_bagian");
                String level_gaji = rs.getString("level_gaji");
                String tanggal_jadwal = rs.getString("tanggal");
                String jenis_hari = rs.getString("jenis_hari");

                row[0] = id_pegawai;
                row[1] = nama_pegawai;
                row[2] = nama_bagian;
                row[3] = rs.getString("posisi");
                row[4] = rs.getString("jam_kerja");
                row[5] = level_gaji;
                row[6] = rs.getString("hari");
                row[7] = rs.getDate("tanggal");
                row[8] = rs.getString("jenis_hari");

                int premi_hadir = 0;
                double upah_per_hari = rs.getFloat("upah_per_hari");
                double upah_per_jam = rs.getFloat("upah_per_hari") / 7f;
                double upah_per_menit = rs.getFloat("upah_per_hari") / (7f * 60f);

                double durasi_istirahat = rs.getInt("durasi_istirahat");
                double menit_jam_kerja_normal = 0, menit_durasi_kerja = 0;
                Timestamp jadwal_masuk = null, jadwal_pulang = null;
                Timestamp absen_masuk = null, absen_pulang = null;
                boolean tidak_absen_masuk = false, tidak_absen_pulang = false, tidak_masuk_kerja = false;

                Date parsedDate = null;
                if (rs.getString("jenis_shift").equals("pagi")) {
                    parsedDate = TimeStampformat.parse(tanggal_jadwal + " " + rs.getString("jadwal_masuk"));
                    jadwal_masuk = new Timestamp(parsedDate.getTime());

                    parsedDate = TimeStampformat.parse(tanggal_jadwal + " " + rs.getString("jadwal_pulang"));
                    jadwal_pulang = new Timestamp(parsedDate.getTime());
                } else if (rs.getString("jenis_shift").equals("malam")) {
                    parsedDate = TimeStampformat.parse(tanggal_jadwal + " " + rs.getString("jadwal_masuk"));
                    jadwal_masuk = new Timestamp(parsedDate.getTime());

                    parsedDate = TimeStampformat.parse(tanggal_jadwal + " " + rs.getString("jadwal_pulang"));
                    jadwal_pulang = new Timestamp(parsedDate.getTime() + (1 * 24 * 60 * 60 * 1000));
                }

                if (jadwal_masuk != null && jadwal_pulang != null) {
                    row[9] = TimeStampformat.format(jadwal_masuk);
                    row[10] = TimeStampformat.format(jadwal_pulang);
                    menit_jam_kerja_normal = TimeUnit.MILLISECONDS.toMinutes(jadwal_pulang.getTime() - jadwal_masuk.getTime());
                }

                String[] data_spl = getSPLMasuk(id_pegawai, tanggal_jadwal);
                if (data_spl[0] != null) {
                    row[11] = data_spl[0];//no_spl
                    row[12] = data_spl[1];//jam mulai lembur pagi
                    jadwal_masuk = new Timestamp(TimeStampformat.parse(data_spl[1]).getTime());
                }

                double menit_terlambat = 0, potongan_terlambat = 0;
                if (jadwal_masuk != null) {
                    absen_masuk = getAbsenMasuk(pin_finger, TimeStampformat.format(jadwal_masuk));
                    absen_pulang = getAbsenPulang(pin_finger, TimeStampformat.format(jadwal_pulang));
                    if (absen_masuk == null && absen_pulang == null) {
                        tidak_masuk_kerja = true;
                    } else {
                        if (absen_masuk != null) {
                            row[13] = TimeStampformat.format(absen_masuk);
                            if (absen_masuk.after(new Timestamp(jadwal_masuk.getTime() + (2 * 60 * 1000)))) { //toleransi 2 menit 0 detik
                                menit_terlambat = (absen_masuk.getTime() - jadwal_masuk.getTime()) / (60 * 1000);
                                if (is_pengampunan_terlambat(tanggal_jadwal)) {
                                    menit_terlambat = 0;
                                }
                                absen_masuk = jadwal_masuk;
                                potongan_terlambat = upah_per_menit * menit_terlambat * 1.5d;
                            } else {
                                absen_masuk = jadwal_masuk;
                            }
                            row[15] = TimeStampformat.format(absen_masuk);
                        } else {
                            absen_masuk = jadwal_masuk;
                            tidak_absen_masuk = true;
                            row[15] = TimeStampformat.format(absen_masuk);
                        }
                        if (absen_pulang != null) {
                            row[14] = TimeStampformat.format(absen_pulang);
                            absen_pulang = new Timestamp((long) Math.floor(absen_pulang.getTime() / (30 * 60 * 1000)) * (30 * 60 * 1000));
                            row[16] = TimeStampformat.format(absen_pulang);
                        } else {
                            absen_pulang = jadwal_pulang;
                            tidak_absen_pulang = true;
                            absen_pulang = new Timestamp((long) Math.floor(absen_pulang.getTime() / (30 * 60 * 1000)) * (30 * 60 * 1000));
                            row[16] = TimeStampformat.format(absen_pulang);
                        }
                    }
                }

                double menit_ijin_keluar = 0, menit_ijin_pulang = 0, potongan_ijin = 0;
                Timestamp jam_ijin_pulang = null, jam_ijin_keluar = null, jam_ijin_kembali = null;
                String query_ijin = "SELECT `tanggal_keluar`, `jam_keluar`, `jam_kembali` "
                        + "FROM `tb_ijin_keluar` "
                        + "WHERE `id_pegawai` = '" + id_pegawai + "' "
                        + "AND `tanggal_keluar` BETWEEN '" + tanggal_jadwal + "' AND DATE_ADD('" + tanggal_jadwal + "', INTERVAL 1 DAY)";
                ResultSet rs_ijin = Utility.db.getStatement().executeQuery(query_ijin);
                while (rs_ijin.next()) {
                    if (rs_ijin.getString("jam_keluar") == null) {
//                        label_data_ijin_tidak_valid.setVisible(true);
                    } else if (rs_ijin.getString("jam_kembali") == null) {
                        jam_ijin_pulang = new Timestamp(TimeStampformat.parse(rs_ijin.getString("tanggal_keluar") + " " + rs_ijin.getString("jam_keluar")).getTime());
                        if (!jenis_hari.toLowerCase().equals("hari libur") && jadwal_pulang != null && jam_ijin_pulang.before(jadwal_pulang)) { //jam pulang normal tidak null artinya hari itu bukan hari libur
                            menit_ijin_pulang = TimeUnit.MILLISECONDS.toMinutes(jadwal_pulang.getTime() - jam_ijin_pulang.getTime());
                        }
                    } else {
                        Date jam_keluar = TimeStampformat.parse(rs_ijin.getString("tanggal_keluar") + " " + rs_ijin.getString("jam_keluar"));
                        Date jam_kembali = TimeStampformat.parse(rs_ijin.getString("tanggal_keluar") + " " + rs_ijin.getString("jam_kembali"));
                        jam_ijin_keluar = new Timestamp(jam_keluar.getTime());
                        jam_ijin_kembali = new Timestamp(jam_kembali.getTime());
                        menit_ijin_keluar = menit_ijin_keluar + TimeUnit.MILLISECONDS.toMinutes(jam_kembali.getTime() - jam_keluar.getTime());
                    }
                }
                potongan_ijin = (upah_per_menit * (menit_ijin_pulang + menit_ijin_keluar));

                String keterangan = "";
                int menit_jatah_ijin = 60;
                if ((menit_jam_kerja_normal / 60) <= 5) {
                    menit_jatah_ijin = 15;
                }

                double menit_lembur = 0, lembur_jam = 0, upah_lembur = 0;
                if (absen_masuk != null && absen_pulang != null) {
                    menit_durasi_kerja = TimeUnit.MILLISECONDS.toMinutes(absen_pulang.getTime() - absen_masuk.getTime());
//                    System.out.println("absen_masuk:" + absen_masuk + ", absen_pulang:" + absen_pulang + "");
//                    System.out.println("menit_durasi_kerja:" + menit_durasi_kerja );
                    if (menit_durasi_kerja > 0) {

                        if (jenis_hari.toLowerCase().equals("hari libur")) {
                            menit_lembur = menit_durasi_kerja;
                        } else if (jenis_hari.toLowerCase().equals("hari kerja")) {
                            menit_lembur = menit_durasi_kerja - menit_jam_kerja_normal;
                        }

                        String query_pengurangan_lembur = "SELECT `pengurangan` FROM `tb_lembur_pengurangan` "
                                + "WHERE `id_pegawai` = '" + id_pegawai + "' AND `tgl_lembur` = '" + rs.getDate("tanggal") + "'";
                        ResultSet rs_pengurangan_lembur = Utility.db.getStatement().executeQuery(query_pengurangan_lembur);
                        if (rs_pengurangan_lembur.next()) {
                            menit_lembur = menit_lembur - rs_pengurangan_lembur.getInt("pengurangan");
                        }

                        double lembur_kali = 1.5d;
                        if (jenis_hari.toLowerCase().equals("hari libur")) {
                            if (!nama_bagian.contains("DRIVER")) {//selain driver dapet 2x
                                lembur_kali = 2d;
                            }
                        }

                        if (nama_bagian.contains("SECURITY")) {
                            upah_per_jam = 10000d;
                            lembur_kali = 1d;
                        }

                        if (nama_bagian.contains("SECURITY")) {
                            if (menit_lembur >= 480) {//lembur lebih dari 8 jam
                                menit_lembur = menit_lembur - 60;
                                menit_lembur = (int) Math.floor(menit_lembur / 30) * 30;
                            } else if (menit_lembur > 120) {//kelipatan 30 menit di hitung setelah jam ke 2
                                menit_lembur = (int) Math.floor(menit_lembur / 30) * 30;
                            } else if (menit_lembur > 0) {
                                menit_lembur = (int) Math.floor(menit_lembur / 60) * 60;
                            } else {
                                menit_lembur = 0;
                            }
                        } else { //selain bagian security
                            if (menit_lembur >= 510) {//lembur lebih dari 8.5 jam
                                menit_lembur = menit_lembur - 60;
                                menit_lembur = (int) Math.floor(menit_lembur / 30) * 30;
                            } else if (menit_lembur >= 210) {//lembur lebih dari 3.5 jam
                                menit_lembur = menit_lembur - 30;
                                menit_lembur = (int) Math.floor(menit_lembur / 30) * 30;
                            } else if (menit_lembur > 120) {//kelipatan 30 menit di hitung setelah jam ke 2
                                menit_lembur = (int) Math.floor(menit_lembur / 30) * 30;
                            } else if (menit_lembur > 0) {
                                menit_lembur = (int) Math.floor(menit_lembur / 60) * 60;
                            } else {
                                menit_lembur = 0;
                            }
                        }

                        lembur_jam = Math.round(menit_lembur / 60d * 10d) / 10d;
                        upah_lembur = lembur_jam * upah_per_jam * lembur_kali;
                    }
                }

//                System.out.println("menit_jam_kerja_normal:" + menit_jam_kerja_normal + ", menit_jatah_ijin:" + menit_jatah_ijin + "");
                double minimal_jam_kerja = menit_jam_kerja_normal - menit_jatah_ijin;
                double menit_durasi_kerja_real = menit_durasi_kerja - (menit_terlambat + menit_ijin_keluar);
                if (menit_durasi_kerja_real < minimal_jam_kerja) {
                    premi_hadir = 0;
                    keterangan = tanggal_jadwal + "(Jam Kerja Kurang); ";
                } else if (tidak_absen_masuk) {
                    premi_hadir = 1;
                    keterangan = tanggal_jadwal + "(Tidak Absen Masuk); ";
                } else if (tidak_absen_pulang) {
                    premi_hadir = 2;
                    keterangan = tanggal_jadwal + "(Tidak Absen Pulang);";
                } else if (jenis_hari.toLowerCase().equals("hari kerja")) {
                    premi_hadir = 3;
                } else if (menit_durasi_kerja >= 240 && jenis_hari.toLowerCase().equals("hari libur")) {
                    premi_hadir = 4;//+Premi (lembur lebih dari 4jam di hari libur)
                } else {
                    premi_hadir = 5;//Lembur di hari libur, tapi kurang dari 4 jam, tidak dapat premi
                }

                if (premi_hadir == 0) {
                    menit_lembur = 0;
                    upah_lembur = 0;
                    menit_terlambat = 0;
                    potongan_terlambat = 0;
                    potongan_ijin = 0;
                    upah_per_hari = 0;
                } else if (premi_hadir == 0) {
                    upah_per_hari = 0;
                }

                row[17] = menit_lembur;//lembur dalam satuan menit
                row[18] = upah_lembur;//upah lembur flat 10.000 per jam
                row[19] = menit_terlambat;
                row[20] = potongan_terlambat;//potongan terlambat
                row[21] = jam_ijin_pulang;//jam ijin pulang
                row[22] = menit_ijin_pulang + menit_ijin_keluar;//menit ijin
                row[23] = potongan_ijin;//potongan ijin
                row[24] = upah_per_hari;
                row[25] = premi_hadir;
                row[26] = keterangan;
                double total_gaji = (upah_per_hari + upah_lembur) - (potongan_terlambat + potongan_ijin);
                row[27] = total_gaji;
                row[28] = menit_durasi_kerja_real;
                row[29] = minimal_jam_kerja;
                data_lembur_sc.add(row);
            }
        } catch (Exception ex) {
            exceptionList.add(ex);
            Logger.getLogger(JFrame_SistemPrintSlip.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (data_lembur_sc.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Silahkan jalankan perhitungan lembur di sheet 1 terlebih dulu!");
        } else {
            try {
                sql = "SELECT `tb_jadwal_kerja_sc`.`id_pegawai`, `pin_finger`, `nama_pegawai`, CONCAT(IFNULL(`divisi_bagian`,''), '-', IFNULL(`bagian_bagian`,''), '-', IFNULL(`ruang_bagian`,'')) AS 'nama_bagian', `posisi`, `tb_karyawan`.`status`, `tb_karyawan`.`level_gaji`"
                        + ", `jam_kerja`, `jalur_jemputan`, `upah_per_hari`, `premi_hadir`, `potongan_bpjs`, `potongan_bpjs_tk`, SUM(IF(`jenis_hari` = 'Hari Kerja', 1, 0)) AS 'hari_kerja_normal' "
                        + "FROM `tb_jadwal_kerja_sc` \n"
                        + "LEFT JOIN `tb_karyawan` ON `tb_jadwal_kerja_sc`.`id_pegawai` = `tb_karyawan`.`id_pegawai`\n"
                        + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`\n"
                        + "LEFT JOIN `tb_level_gaji` ON `tb_karyawan`.`level_gaji` = `tb_level_gaji`.`level_gaji`\n"
                        + "WHERE "
                        + "`tb_jadwal_kerja_sc`.`id_pegawai` = '" + id + "' "
                        + "AND `tanggal` BETWEEN '" + dateFormat.format(tanggal_mulai) + "' AND '" + dateFormat.format(tanggal_selesai) + "' "
                        + "GROUP BY `tb_jadwal_kerja_sc`.`id_pegawai` "
                        + "ORDER BY `tb_karyawan`.`nama_pegawai`";
                pst = Utility.db.getConnection().prepareStatement(sql);
                rs = pst.executeQuery();

                while (rs.next()) {
                    Object[] row = new Object[35];
                    String id_pegawai = rs.getString("id_pegawai");
                    row[0] = 0;
                    row[1] = id_pegawai;
                    row[2] = rs.getString("nama_pegawai");

                    long tanggal_ke = 0;
                    double total_jam_lembur = 0, lembur_jam = 0, total_upah_lembur = 0, total_pot_terlambat = 0, total_pot_ijin = 0;
                    double hari_masuk_premi = 0, libur_dapat_premi = 0, hari_kerja_normal = rs.getDouble("hari_kerja_normal"), hari_transport = 0, hari_masuk_kerja = 0;
                    double potongan_transport = 0;
                    double dapat_premi = 1, premi_hadir = 0, nominal_premi_per_hari = rs.getDouble("premi_hadir");
                    double potongan_bpjs = 0, potongan_bpjs_tk = 0, piutang_karyawan = 0;
                    double upah_per_hari = rs.getDouble("upah_per_hari"), total_upah_harian = 0;
                    String keterangan_premi = "";
//                    row[4] = rs.getString("hari_kerja_normal");

                    for (int i = 0; i < data_lembur_sc.size(); i++) {
                        Object[] data_data_lembur_sc = new Object[35];
                        data_data_lembur_sc = data_lembur_sc.get(i);
                        if (id_pegawai.equals(data_data_lembur_sc[0].toString())) {
                            tanggal_ke = list_tanggal.indexOf(data_data_lembur_sc[7].toString());

                            lembur_jam = Math.round((double) data_data_lembur_sc[17] / 60d * 10d) / 10d;
                            total_jam_lembur = total_jam_lembur + lembur_jam;
                            row[3 + (int) tanggal_ke] = lembur_jam;

                            total_upah_lembur = total_upah_lembur + (double) data_data_lembur_sc[18];//total upah lembur
                            total_pot_terlambat = total_pot_terlambat + (double) data_data_lembur_sc[20];//total pot terlambat
                            total_pot_ijin = total_pot_ijin + (double) data_data_lembur_sc[23];//total pot ijin keluar / pulang
                            int kode_premi = (int) data_data_lembur_sc[25];

                            switch (kode_premi) {
                                case 0:
                                    dapat_premi = 0;
                                    hari_transport++;
                                    keterangan_premi = keterangan_premi + dateFormat.format(new Date(tanggal_mulai.getTime() + (tanggal_ke * 24 * 60 * 60 * 1000))) + "(Jam Kerja Kurang); ";
                                    break;
                                case 1:
                                    dapat_premi = 0;
                                    hari_masuk_kerja++;
                                    hari_transport++;
                                    keterangan_premi = keterangan_premi + dateFormat.format(new Date(tanggal_mulai.getTime() + (tanggal_ke * 24 * 60 * 60 * 1000))) + "(Tidak Absen Masuk); ";
                                    break;
                                case 2:
                                    dapat_premi = 0;
                                    hari_masuk_kerja++;
                                    hari_transport++;
                                    keterangan_premi = keterangan_premi + dateFormat.format(new Date(tanggal_mulai.getTime() + (tanggal_ke * 24 * 60 * 60 * 1000))) + "(Tidak Absen Pulang); ";
                                    break;
                                case 3:
                                    hari_masuk_kerja++;
                                    hari_masuk_premi++;
                                    hari_transport++;
                                    break;
                                case 4:
                                    libur_dapat_premi++;
                                    break;
                                default:
                                    break;
                            }

                            tanggal_ke++;
                        }
                    }
                    if (rs.getString("jalur_jemputan") != null && !rs.getString("jalur_jemputan").equals("")) {
                        potongan_transport = hari_transport * Double.valueOf(txt_potongan_transport.getText());
                    }
                    if (dapat_premi > 0 && hari_masuk_premi >= hari_kerja_normal) {
                        premi_hadir = (hari_masuk_premi + libur_dapat_premi) * nominal_premi_per_hari;
                    }
                    System.out.println("potongan_bpjs : " + rs.getInt("potongan_bpjs"));
                    System.out.println("potongan_bpjs_tk : " + rs.getInt("potongan_bpjs_tk"));
                    if (rs.getInt("potongan_bpjs") == 1) {
                        potongan_bpjs = Integer.valueOf(txt_potongan_bpjs.getText());
                    }
                    if (rs.getInt("potongan_bpjs_tk") == 1) {
                        potongan_bpjs_tk = Integer.valueOf(txt_potongan_bpjs_tk.getText());
                    }
                    if (upah_per_hari > 0) {
                        total_upah_harian = hari_masuk_kerja * upah_per_hari;
                    }

                    String query_piutang = "SELECT SUM(`nominal_piutang`) AS 'piutang' "
                            + "FROM `tb_piutang_karyawan` "
                            + "WHERE `status` = '0' "
                            + "AND `id_pegawai` = '" + id_pegawai + "' ";
                    PreparedStatement pst_piutang = Utility.db.getConnection().prepareStatement(query_piutang);
                    ResultSet rs_piutang = pst_piutang.executeQuery();
                    if (rs_piutang.next()) {
                        piutang_karyawan = rs_piutang.getDouble("piutang");
                    }

                    String sql_bonus_pencapaian_produksi = "SELECT SUM(`bonus_produksi`) AS 'bonus_pencapaian_produksi' "
                            + "FROM `tb_bonus_pencapaian_produksi` "
                            + "WHERE `id_pegawai` = '" + id_pegawai + "' "
                            + "AND `tanggal` BETWEEN '" + dateFormat.format(tanggal_mulai) + "' AND '" + dateFormat.format(tanggal_selesai) + "' ";
                    PreparedStatement pst_bonus_pencapaian_produksi = Utility.db.getConnection().prepareStatement(sql_bonus_pencapaian_produksi);
                    ResultSet rs_bonus_pencapaian_produksi = pst_bonus_pencapaian_produksi.executeQuery();
                    double bonus_pencapaian_produksi = 0;
                    if (rs_bonus_pencapaian_produksi.next()) {
                        bonus_pencapaian_produksi = rs_bonus_pencapaian_produksi.getInt("bonus_pencapaian_produksi");
                    }

                    double gaji = total_upah_harian //gaji harian
                            + premi_hadir //premi hadir
                            + total_upah_lembur //upah lembur
                            + 0 //upah borong
                            + 0 //bonus kecepatan
                            + 0 //bonus 2
                            + bonus_pencapaian_produksi //bonus pencapaian produksi
                            + 0 //bonus TBT
                            + piutang_karyawan //piutang karyawan
                            - potongan_bpjs //potongan bpjs kesehatan
                            - potongan_bpjs_tk //potongan bpjs ketenagakerjaan
                            - potongan_transport //potongan transport
                            - total_pot_terlambat //potongan terlambat
                            - total_pot_ijin; //potongan ijin
                    System.out.println("potongan_bpjs : " + potongan_bpjs);
                    System.out.println("potongan_bpjs_tk : " + potongan_bpjs_tk);
                    System.out.println("gaji : " + gaji);
                    if (gaji < (potongan_bpjs + potongan_bpjs_tk)) {
                        gaji = total_upah_harian //gaji harian
                                + premi_hadir //premi hadir
                                + total_upah_lembur //upah lembur
                                + 0 //upah borong
                                + 0 //bonus kecepatan
                                + 0 //bonus 2
                                + bonus_pencapaian_produksi //bonus pencapaian produksi
                                + 0 //bonus TBT
                                + piutang_karyawan //piutang karyawan
                                - potongan_transport //potongan transport
                                - total_pot_terlambat //potongan terlambat
                                - total_pot_ijin; //potongan ijin
                        potongan_bpjs = 0;//potongan bpjs dipotongkan di minggu depannya
                        potongan_bpjs_tk = 0;//potongan bpjs dipotongkan di minggu depannya
                        keterangan_premi = keterangan_premi + " Potongan BPJS 0, dipotong minggu depan;";
                    }

                    row[10] = total_jam_lembur;
                    row[11] = total_upah_lembur;
                    row[12] = total_pot_terlambat;
                    row[13] = total_pot_ijin;
                    row[14] = premi_hadir; //premi hadir
                    row[15] = potongan_transport; //potongan transport
                    row[16] = potongan_bpjs; //potongan bpjs
                    row[17] = potongan_bpjs_tk; //potongan bpjs
                    row[18] = 0; //bonus TBT
                    row[19] = hari_masuk_kerja; //hari kerja
                    row[20] = total_upah_harian; //gaji harian
                    row[21] = 0; //gaji borong
                    row[22] = 0; //bonus 1 kecepatan
                    row[23] = 0; //bonus 2 mk utuh
                    row[24] = bonus_pencapaian_produksi; //bonus pencapaian produksi
                    row[25] = piutang_karyawan; //piutang
                    row[26] = Math.round(gaji * 1000.d) / 1000.d;//total gaji
                    row[27] = keterangan_premi;
                    row[28] = rs.getString("nama_bagian");
                    row[29] = rs.getString("level_gaji");
                    row[30] = rs.getString("jam_kerja");

                    JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Slip_Gaji_Karyawan_Struk.jrxml");
                    Map<String, Object> map = new HashMap<>();
                    map.put("PERIODE_AWAL", tanggal_mulai);
                    map.put("PERIODE_AKHIR", tanggal_selesai);
                    map.put("ID_PEGAWAI", id_pegawai);
                    map.put("NAMA_PEGAWAI", rs.getString("nama_pegawai"));
                    map.put("LEMBUR", total_upah_lembur);
                    map.put("POT_TERLAMBAT", total_pot_terlambat);
                    map.put("POT_IJIN", total_pot_ijin);
                    map.put("TUNJANGAN_HADIR", premi_hadir);
                    map.put("POT_TRANSPORT", potongan_transport);
                    map.put("POT_BPJS", potongan_bpjs);
                    map.put("POT_BPJS_TK", potongan_bpjs_tk);
                    map.put("BONUS_TBT", 0d);
                    map.put("GAJI_HARIAN", total_upah_harian);
                    map.put("GAJI_BORONG", 0d);
                    map.put("BONUS_LP", 0d);
                    map.put("BONUS_MK_UTUH", 0d);
                    map.put("BONUS_PENCAPAIAN_PRODUKSI", bonus_pencapaian_produksi);
                    map.put("PIUTANG", piutang_karyawan);
                    map.put("KETERANGAN", keterangan_premi);
                    map.put("GRUP", rs.getString("nama_bagian"));
                    JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
                    JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, map, Utility.db.getConnection());
                    JasperPrintManager.printReport(JASP_PRINT, false);//langsung print
//                    JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)

                    String Query = "INSERT INTO `tb_payrol_data_print_slip`(`id_pegawai`, `tanggal_penggajian`, `jumlah_print`, `print_time`) "
                            + "VALUES ('" + id_pegawai + "','" + dateFormat.format(tanggal_penggajian) + "', 1, NOW())"
                            + "ON DUPLICATE KEY UPDATE `jumlah_print` = `jumlah_print` + 1, `print_time` = NOW()";
                    Utility.db.getConnection().createStatement();
                    Utility.db.getStatement().executeUpdate(Query);
                }
            } catch (Exception e) {
                exceptionList.add(e);
                Logger.getLogger(JFrame_SistemPrintSlip.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }

    public void ENTER() {
        try {
            // Get the current date
            Date today = new Date();

            // Create a Calendar instance and set it to the current date
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(today);

            // Find the day of the week for today
            int dayOfToday = calendar.get(Calendar.DAY_OF_WEEK);

            // Calculate the number of days to subtract to get to the last Thursday (Calendar.THURSDAY represents Thursday)
            int daysToSubtract = (dayOfToday - Calendar.THURSDAY + 7) % 7;

            // Subtract the number of days to get the date of the last Thursday
            calendar.add(Calendar.DAY_OF_MONTH, -daysToSubtract);
            Date tanggal_penggajian = calendar.getTime();

            sql = "SELECT `tb_karyawan`.`id_pegawai`, `nama_pegawai`, `jam_kerja`, `jumlah_print`, `print_time` "
                    + "FROM `tb_karyawan` "
                    + "LEFT JOIN `tb_payrol_data_print_slip` ON (`tb_karyawan`.`id_pegawai` = `tb_payrol_data_print_slip`.`id_pegawai` AND `tb_payrol_data_print_slip`.`tanggal_penggajian` = '" + dateFormat.format(tanggal_penggajian) + "')"
                    //                    + "WHERE `tb_karyawan`.`id_pegawai` = '20180102221'";
                    + "WHERE `uid_card` = '" + PasswordField.getText() + "'";
            Connection con = Utility.db.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet result = ps.executeQuery();
            if (result.next()) {
                label_id.setText(result.getString("id_pegawai"));
                label_nama.setText(result.getString("nama_pegawai"));

                // Get the current time
                long currentTimeMillis = System.currentTimeMillis();

                if (result.getString("jumlah_print") != null && result.getInt("jumlah_print") > 1) {
                    label_status.setText("Maaf print slip maksimal 2x / penggajian !!");
                    label_status.setForeground(Color.red);
                } else if (result.getInt("jumlah_print") == 1 && currentTimeMillis >= result.getTimestamp("print_time").getTime() && currentTimeMillis <= result.getTimestamp("print_time").getTime() + (5 * 60 * 1000)) {
                    label_status.setText("Maaf silahkan print slip anda 5 menit kemudian !!");
                    label_status.setForeground(Color.red);
                } else {
                    int jumlah_print = result.getString("jumlah_print") == null ? 0 : result.getInt("jumlah_print");
                    label_status.setText("Print ke : " + jumlah_print);
                    label_status.setForeground(Color.black);

                    if (result.getString("jam_kerja").equals("SHIFT_MALAM")) {
                        getDataSlip_SHIFT_MALAM(result.getString("id_pegawai"), tanggal_penggajian);
                    } else {
                        getDataSlip(result.getString("id_pegawai"), tanggal_penggajian);
                    }
                }
            } else {
                label_id.setText("-");
                label_nama.setText("-");
                label_status.setText("ID card belum terdaftar!!");
                label_status.setForeground(Color.red);
            }
            PasswordField.setText(null);
            PasswordField.requestFocus();
        } catch (Exception ex) {
            exceptionList.add(ex);
            Logger.getLogger(JFrame_SistemPrintSlip.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void showLogDialog() {
        JDialog logDialog = new JDialog();
        logDialog.setLocationRelativeTo(null);
        logDialog.setTitle("Log Messages");
        logDialog.setSize(600, 400);
        logDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        final JTextArea logTextArea = new JTextArea();
        logTextArea.setEditable(false);

        for (Exception ex : exceptionList) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logTextArea.append(sw.toString() + "\n\n");
        }

        JScrollPane scrollPane = new JScrollPane(logTextArea);
        logDialog.add(scrollPane);
        logDialog.setVisible(true);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        PasswordField = new javax.swing.JPasswordField();
        label_id = new javax.swing.JLabel();
        label_nama = new javax.swing.JLabel();
        Jlabel3 = new javax.swing.JLabel();
        Jlabel2 = new javax.swing.JLabel();
        label_status = new javax.swing.JLabel();
        label_reader_status = new javax.swing.JLabel();
        txt_potongan_bpjs = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        txt_potongan_bpjs_tk = new javax.swing.JTextField();
        txt_jumlah_karyawan_terlambat = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        txt_bonus_tbt = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        txt_potongan_transport = new javax.swing.JTextField();
        Button_close = new javax.swing.JButton();
        Button_show_log = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Print Slip Gaji");
        setUndecorated(true);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Scan ID Card", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 24))); // NOI18N

        PasswordField.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        PasswordField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                PasswordFieldKeyPressed(evt);
            }
        });

        label_id.setBackground(new java.awt.Color(255, 255, 255));
        label_id.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        label_id.setText("-");
        label_id.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        label_id.setMaximumSize(new java.awt.Dimension(280, 15));

        label_nama.setBackground(new java.awt.Color(255, 255, 255));
        label_nama.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        label_nama.setText("-");
        label_nama.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        label_nama.setMaximumSize(new java.awt.Dimension(280, 15));

        Jlabel3.setBackground(new java.awt.Color(255, 255, 255));
        Jlabel3.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        Jlabel3.setText("ID Pegawai :");
        Jlabel3.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        Jlabel3.setMaximumSize(new java.awt.Dimension(280, 15));

        Jlabel2.setBackground(new java.awt.Color(255, 255, 255));
        Jlabel2.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        Jlabel2.setText("Nama : ");
        Jlabel2.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        Jlabel2.setMaximumSize(new java.awt.Dimension(280, 15));

        label_status.setBackground(new java.awt.Color(255, 255, 255));
        label_status.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        label_status.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_status.setText("--");
        label_status.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        label_status.setMaximumSize(new java.awt.Dimension(280, 15));

        label_reader_status.setBackground(new java.awt.Color(255, 255, 255));
        label_reader_status.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        label_reader_status.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_reader_status.setText("--");
        label_reader_status.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        label_reader_status.setMaximumSize(new java.awt.Dimension(280, 15));

        txt_potongan_bpjs.setEditable(false);
        txt_potongan_bpjs.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_potongan_bpjs.setText("23790");

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel14.setText("Pot. BPJS KS :");

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel18.setText("Pot. BPJS TK :");

        jLabel20.setBackground(new java.awt.Color(255, 255, 255));
        jLabel20.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel20.setText("Jumlah Karyawan Terlambat :");

        txt_potongan_bpjs_tk.setEditable(false);
        txt_potongan_bpjs_tk.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_potongan_bpjs_tk.setText("71370");

        txt_jumlah_karyawan_terlambat.setEditable(false);
        txt_jumlah_karyawan_terlambat.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_jumlah_karyawan_terlambat.setText("0");

        jLabel21.setBackground(new java.awt.Color(255, 255, 255));
        jLabel21.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(255, 0, 0));
        jLabel21.setText("MAX 30");

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel11.setText("Bonus TBT / org :");

        txt_bonus_tbt.setEditable(false);
        txt_bonus_tbt.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_bonus_tbt.setText("20000");

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel13.setText("Pot. Transport :");

        txt_potongan_transport.setEditable(false);
        txt_potongan_transport.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_potongan_transport.setText("7500");

        Button_close.setBackground(new java.awt.Color(255, 255, 255));
        Button_close.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Button_close.setText("Close");
        Button_close.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_closeActionPerformed(evt);
            }
        });

        Button_show_log.setBackground(new java.awt.Color(255, 255, 255));
        Button_show_log.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Button_show_log.setText("Show Log");
        Button_show_log.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_show_logActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(label_status, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(PasswordField, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label_reader_status, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_bonus_tbt, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_potongan_transport, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_potongan_bpjs, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_potongan_bpjs_tk, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_jumlah_karyawan_terlambat, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel21)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 143, Short.MAX_VALUE)
                        .addComponent(Button_show_log)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Button_close))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addComponent(Jlabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_id, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addComponent(Jlabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_nama, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(label_reader_status, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Jlabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_id, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Jlabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_nama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(label_status, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_bonus_tbt, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_potongan_transport, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_potongan_bpjs, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_potongan_bpjs_tk, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_jumlah_karyawan_terlambat, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Button_close)
                    .addComponent(Button_show_log))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(751, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(410, Short.MAX_VALUE))
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

    private void PasswordFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_PasswordFieldKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            ENTER();
        }
    }//GEN-LAST:event_PasswordFieldKeyPressed

    private void Button_closeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_closeActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_Button_closeActionPerformed

    private void Button_show_logActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_show_logActionPerformed
        // TODO add your handling code here:
        showLogDialog();
    }//GEN-LAST:event_Button_show_logActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        // TODO add your handling code here:
        thread.stop();
    }//GEN-LAST:event_formWindowClosed

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(JFrame_SistemPrintSlip.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JFrame_SistemPrintSlip.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JFrame_SistemPrintSlip.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JFrame_SistemPrintSlip.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new JFrame_SistemPrintSlip().setVisible(true);

            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Button_close;
    private javax.swing.JButton Button_show_log;
    private javax.swing.JLabel Jlabel2;
    private javax.swing.JLabel Jlabel3;
    private javax.swing.JPasswordField PasswordField;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel label_id;
    private javax.swing.JLabel label_nama;
    private javax.swing.JLabel label_reader_status;
    private javax.swing.JLabel label_status;
    private javax.swing.JTextField txt_bonus_tbt;
    private javax.swing.JTextField txt_jumlah_karyawan_terlambat;
    private javax.swing.JTextField txt_potongan_bpjs;
    private javax.swing.JTextField txt_potongan_bpjs_tk;
    private javax.swing.JTextField txt_potongan_transport;
    // End of variables declaration//GEN-END:variables
}
