package waleta_system.Finance;

import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.ExportToExcel;
import waleta_system.Class.Utility;
import waleta_system.MainForm;

public class JPanel_payrol_harian extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    Date today = new Date();
    PreparedStatement pst;
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    List<Payrol_Harian_Model> listPayrolHarian = new ArrayList<>();
    String judulTgl1 = "";
    String judulTgl2 = "";
    String judulTgl3 = "";
    String judulTgl4 = "";
    String judulTgl5 = "";
    String judulTgl6 = "";
    String judulTgl7 = "";

    public JPanel_payrol_harian() {
        initComponents();
    }

    public void init() {
        try {
            if (MainForm.Login_idPegawai.equals("20171201644")//indrika
                    || MainForm.Login_idPegawai.equals("20230907768")//diyan
                    || MainForm.Login_idPegawai.equals("20180102221")//bastian
                    ) {
                button_input_bonus_pencapaian.setEnabled(true);
                button_print_slip_per_grup.setEnabled(true);
                button_print_slip_per_grup2.setEnabled(true);
                button_save_data_fix.setEnabled(true);
                button_slip_borong_cabut.setEnabled(true);
                button_slip_cabut.setEnabled(true);
                button_slip_borong_cetak2.setEnabled(true);
                button_slip_harian.setEnabled(true);
                button_slip_mandiri_cetak.setEnabled(true);
            }
            if (MainForm.Login_idPegawai.equals("20170100225")) {//priska
                button_input_bonus_pencapaian.setEnabled(true);
                button_slip_mandiri_cetak.setEnabled(true);
            }
        } catch (Exception ex) {
            Logger.getLogger(JPanel_payrol_harian.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable() {
        try {
            DefaultTableModel model = (DefaultTableModel) Tabel_data.getModel();
            model.setRowCount(0);
            boolean check = true;
            if (Date_penggajian.getDate() == null) {
                check = false;
                JOptionPane.showMessageDialog(this, "Harap masukkan tanggal penggajian");
            } else if (!(new SimpleDateFormat("EEEEE").format(Date_penggajian.getDate()).toUpperCase().equals("THURSDAY") || new SimpleDateFormat("EEEEE").format(Date_penggajian.getDate()).toUpperCase().equals("KAMIS"))) {
                check = false;
                JOptionPane.showMessageDialog(this, "Tanggal penggajian seharusnya hari kamis");
            }

            if (check) {
                Date tanggal_mulai = new Date(Date_penggajian.getDate().getTime() - (7 * 24 * 60 * 60 * 1000));
                Date tanggal_selesai = new Date(Date_penggajian.getDate().getTime() - (1 * 24 * 60 * 60 * 1000));
                JTableHeader th = Tabel_data.getTableHeader();
                TableColumnModel tcm = th.getColumnModel();
                for (int i = 0; i < 7; i++) {
                    Date header = new Date(tanggal_mulai.getTime() + (i * 24 * 60 * 60 * 1000));
                    TableColumn tc = tcm.getColumn(i + 3);
                    tc.setHeaderValue(new SimpleDateFormat("dd MMM").format(header));
                }
                judulTgl1 = tcm.getColumn(3).getHeaderValue().toString();
                judulTgl2 = tcm.getColumn(4).getHeaderValue().toString();
                judulTgl3 = tcm.getColumn(5).getHeaderValue().toString();
                judulTgl4 = tcm.getColumn(6).getHeaderValue().toString();
                judulTgl5 = tcm.getColumn(7).getHeaderValue().toString();
                judulTgl6 = tcm.getColumn(8).getHeaderValue().toString();
                judulTgl7 = tcm.getColumn(9).getHeaderValue().toString();
                th.repaint();

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

                String search_bagian = "AND A.`grup` LIKE '" + txt_search_bagian.getText() + "' ";
                if (txt_search_bagian.getText() == null || txt_search_bagian.getText().equals("") || txt_search_bagian.getText().equals("%%")) {
                    search_bagian = "";
                }

                String search_level_gaji = "AND A.`level_gaji` LIKE '%" + txt_search_level_gaji.getText() + "%' ";
                if (txt_search_level_gaji.getText() == null || txt_search_level_gaji.getText().equals("")) {
                    search_level_gaji = "";
                }

                String filter_tanggal_masuk = "";
                if (Date_Masuk1.getDate() != null && Date_Masuk2.getDate() != null) {
                    filter_tanggal_masuk = "AND `tanggal_masuk` BETWEEN '" + dateFormat.format(Date_Masuk1.getDate()) + "' AND '" + dateFormat.format(Date_Masuk2.getDate()) + "' \n";
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
                        + "WHERE `nama_pegawai` LIKE '%" + txt_search_karyawan.getText() + "%' "
                        + filter_tanggal_masuk
                        + "AND `tb_karyawan`.`posisi` IN ('PEJUANG') "
                        + "AND `tb_bagian`.`kode_departemen` <> 'SUB' "
                        + "AND `tb_karyawan`.`jam_kerja` IS NOT NULL "
                        + "AND `tb_karyawan`.`jam_kerja` <> 'SHIFT_MALAM'"
                        + "AND `status` IN ('IN', 'OUT', 'ABSEN')"
                        + "AND (A.`id_pegawai` IS NOT NULL OR B.`id_pegawai` IS NOT NULL OR C.`id_pegawai` IS NOT NULL) "
                        + search_bagian
                        + search_level_gaji
                        + "GROUP BY `tb_karyawan`.`id_pegawai` "
                        + "ORDER BY A.`grup` ";
//                        + "AND (`tanggal_keluar` IS NULL OR `tanggal_keluar` >= '" + dateFormat.format(tanggal_mulai) + "')";

                pst = Utility.db.getConnection().prepareStatement(sql);
                rs = pst.executeQuery();
                Object[] row = new Object[40];
                int no = 1;
                double total_upah_lembur = 0, total_tunjangan_hadir = 0, total_gaji = 0;
                double total_bonus_cabut = 0, total_upah_cabut = 0, total_upah_non_cabut = 0;
                listPayrolHarian = new ArrayList<>();
                while (rs.next()) {
                    double total_jam_lembur = 0, total_menit_terlambat = 0, jumlah_hari_terlambat = 0;
                    double upah_lembur_seminggu = 0, total_potongan_terlambat = 0, total_potongan_ijin = 0;
                    double total_gaji_borong = 0, total_bonus1 = 0, total_bonus2 = 0;
                    double hari_masuk_premi = 0, libur_dapat_premi = 0, hari_masuk_kerja = 0, hari_kerja_normal = 0;
                    double dapat_premi = 1, premi_hadir_per_hari = 0, denda_terlambat_per_hari = 0;
                    double hari_transport = 0, potongan_bpjs = 0, potongan_bpjs_tk = 0;
                    double upah_per_hari = 0, total_upah_harian = 0;
                    double lembur_kali = 0;
                    String keterangan_premi = "";
                    String kode_grup = "", posisi = rs.getString("posisi");
                    String level_gaji = rs.getString("level_gaji_current");
                    String jam_kerja = rs.getString("jam_kerja_current");
                    if (rs.getString("level_gaji") != null) {
                        level_gaji = rs.getString("level_gaji");
                    }
                    if (rs.getString("jam_kerja") != null) {
                        jam_kerja = rs.getString("jam_kerja");
                    }
                    boolean ikut_jemputan = false;
                    row[0] = no;
                    row[1] = rs.getString("id_pegawai");
                    row[2] = rs.getString("nama_pegawai");
                    if (rs.getInt("potongan_bpjs") == 1) {
                        potongan_bpjs = Integer.valueOf(txt_potongan_bpjs.getText());
                    }
                    if (rs.getInt("potongan_bpjs_tk") == 1) {
                        potongan_bpjs_tk = Integer.valueOf(txt_potongan_bpjs_tk.getText());
                    }
                    for (int i = 0; i < 7; i++) {
                        Date header = new Date(tanggal_mulai.getTime() + (i * 24 * 60 * 60 * 1000));
                        Calendar c = Calendar.getInstance();
                        c.setTime(header);
                        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
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
                                + "WHERE `id_pegawai` = '" + rs.getString("id_pegawai") + "' "
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
//                    System.out.println("denda_terlambat = " + denda_terlambat + ", jumlah_hari_terlambat = " + jumlah_hari_terlambat + ", denda_terlambat_per_hari = " + denda_terlambat_per_hari);

                    double premi_hadir = 0;
                    if (dapat_premi > 0 && hari_masuk_premi >= hari_kerja_normal) {
                        premi_hadir = (hari_masuk_premi + libur_dapat_premi) * premi_hadir_per_hari;
                        premi_hadir = premi_hadir - denda_terlambat;
                    }
                    
                    

                    String sql_tbt = "SELECT A.`id_pegawai` AS 'id1', B.`nama_pegawai` AS 'nama1', D.`nama_bagian` AS 'bagian1', "
                            + "A.`id_teman` AS 'id2', C.`nama_pegawai` AS 'nama2', E.`nama_bagian` AS 'bagian2', "
                            + "C.`tanggal_masuk`, C.`tanggal_keluar`, C.`status`, DATEDIFF(IF(C.`tanggal_keluar` IS NULL, '" + dateFormat.format(Date_penggajian.getDate()) + "', C.`tanggal_keluar`), C.`tanggal_masuk`) AS 'hari_kerja' \n"
                            + "FROM `tb_temanbawateman` AS A "
                            + "LEFT JOIN `tb_karyawan` AS B ON A.`id_pegawai` = B.`id_pegawai`\n"
                            + "LEFT JOIN `tb_karyawan` AS C ON A.`id_teman` = C.`id_pegawai`\n"
                            + "LEFT JOIN `tb_bagian` AS D ON D.`kode_bagian` = B.`kode_bagian`\n"
                            + "LEFT JOIN `tb_bagian` AS E ON E.`kode_bagian` = C.`kode_bagian`\n"
                            + "WHERE A.`id_pegawai` = '" + rs.getString("id_pegawai") + "' "
                            + "AND DATEDIFF(IF(C.`tanggal_keluar` IS NULL, '" + dateFormat.format(Date_penggajian.getDate()) + "', C.`tanggal_keluar`), C.`tanggal_masuk`) > 20 "
                            + "AND DATEDIFF(IF(C.`tanggal_keluar` IS NULL, '" + dateFormat.format(Date_penggajian.getDate()) + "', C.`tanggal_keluar`), C.`tanggal_masuk`) < 28 "
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
//                    row[14] = premi_hadir;//
//                    row[15] = jumlah_hari_terlambat; //
//                    row[16] = denda_terlambat_per_hari; //
//                    row[17] = denda_terlambat; //
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

                    if (CheckBox_hide_Gaji_0.isSelected()) {
                        if (gaji > 0) {
                            model.addRow(row);
                            no++;
                            listPayrolHarian.add(new Payrol_Harian_Model(
                                    Objects.toString(row[1], "-"),
                                    Objects.toString(row[2], "-"),
                                    Objects.toString(row[3], "-"),
                                    Objects.toString(row[4], "-"),
                                    Objects.toString(row[5], "-"),
                                    Objects.toString(row[6], "-"),
                                    Objects.toString(row[7], "-"),
                                    Objects.toString(row[8], "-"),
                                    Objects.toString(row[9], "-"),
                                    Objects.toString(row[10], "-"),
                                    Objects.toString(row[11], "-"),
                                    Objects.toString(row[12], "-"),
                                    Objects.toString(row[13], "-"),
                                    Objects.toString(row[14], "-"),
                                    Objects.toString(row[15], "-"),
                                    Objects.toString(row[16], "-"),
                                    Objects.toString(row[17], "-"),
                                    Objects.toString(row[18], "-"),
                                    Objects.toString(row[19], "-"),
                                    Objects.toString(row[20], "-"),
                                    Objects.toString(row[21], "-"),
                                    Objects.toString(row[22], "-"),
                                    Objects.toString(row[23], "-"),
                                    Objects.toString(row[24], "-"),
                                    Objects.toString(row[25], "-"),
                                    Objects.toString(row[26], "-"),
                                    Objects.toString(row[27], "-"),
                                    Objects.toString(row[28], "-")));
                            total_upah_lembur = total_upah_lembur + Math.round(upah_lembur_seminggu);
                            total_tunjangan_hadir = total_tunjangan_hadir + Math.round(premi_hadir);
                            total_gaji = total_gaji + Math.round(gaji);
                            if (kode_grup.toUpperCase().contains("CABUT")) {
                                total_bonus_cabut = total_bonus_cabut + Math.round(total_bonus1);
                                total_upah_cabut = total_upah_cabut + Math.round(gaji);
                            } else {
                                total_upah_non_cabut = total_upah_non_cabut + Math.round(gaji);
                            }
                        }
                    } else {
                        model.addRow(row);
                        no++;
                        listPayrolHarian.add(new Payrol_Harian_Model(
                                Objects.toString(row[1], "-"),
                                Objects.toString(row[2], "-"),
                                Objects.toString(row[3], "-"),
                                Objects.toString(row[4], "-"),
                                Objects.toString(row[5], "-"),
                                Objects.toString(row[6], "-"),
                                Objects.toString(row[7], "-"),
                                Objects.toString(row[8], "-"),
                                Objects.toString(row[9], "-"),
                                Objects.toString(row[10], "-"),
                                Objects.toString(row[11], "-"),
                                Objects.toString(row[12], "-"),
                                Objects.toString(row[13], "-"),
                                Objects.toString(row[14], "-"),
                                Objects.toString(row[15], "-"),
                                Objects.toString(row[16], "-"),
                                Objects.toString(row[17], "-"),
                                Objects.toString(row[18], "-"),
                                Objects.toString(row[19], "-"),
                                Objects.toString(row[20], "-"),
                                Objects.toString(row[21], "-"),
                                Objects.toString(row[22], "-"),
                                Objects.toString(row[23], "-"),
                                Objects.toString(row[24], "-"),
                                Objects.toString(row[25], "-"),
                                Objects.toString(row[26], "-"),
                                Objects.toString(row[27], "-"),
                                Objects.toString(row[28], "-")));
                        total_upah_lembur = total_upah_lembur + Math.round(upah_lembur_seminggu);
                        total_tunjangan_hadir = total_tunjangan_hadir + Math.round(premi_hadir);
                        total_gaji = total_gaji + Math.round(gaji);
                        if (kode_grup.toUpperCase().contains("CABUT")) {
                            total_bonus_cabut = total_bonus_cabut + Math.round(total_bonus1);
                            total_upah_cabut = total_upah_cabut + Math.round(gaji);
                        } else {
                            total_upah_non_cabut = total_upah_non_cabut + Math.round(gaji);
                        }
                    }
                    //listPayrolHarian.add(new Payrol_Harian_Model((String)row[1], (String)row[2], (float)row[3], (float)row[4], (float)row[5], (float)row[6], (float)row[7], (float)row[8], (float)row[9], (float)row[10], (int)row[11], (int)row[12], (int)row[13], (float)row[14], (float)row[15], (float)row[16], (int)row[17], (float)row[18], (float)row[19], (float)row[20], (float)row[21], (float)row[22], (float)row[23], (String)row[24], (String)row[25]));
                }
                ColumnsAutoSizer.sizeColumnsToFit(Tabel_data);
                label_total_upah_lembur.setText(decimalFormat.format(total_upah_lembur));
                label_total_tunjangan_hadir.setText(decimalFormat.format(total_tunjangan_hadir));
                label_total_bonus_lp.setText(decimalFormat.format(total_bonus_cabut));
                label_total_upah_cabut.setText(decimalFormat.format(total_upah_cabut));
                label_total_upah_non_cabut.setText(decimalFormat.format(total_upah_non_cabut));
                label_total_gaji.setText(decimalFormat.format(total_gaji));
            }
        } catch (Exception ex) {
            Logger.getLogger(JPanel_payrol_harian.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String convertNullWithZero(Object a) {
        if (a == null || a.toString().equals("")) {
            return "0";
        } else {
            return a.toString();
        }
//        return (a == null || a.toString().equals("")) ? "0" : a.toString();
    }

    public void saveDataPenggajian() {
        try {
            boolean data_sudah_pernah_simpan = false;
            sql = "SELECT `tgl_penggajian`, `id_pegawai` FROM `tb_payrol_data` "
                    + "WHERE `tgl_penggajian` = '" + dateFormat.format(Date_penggajian.getDate()) + "' AND `jam_kerja` <> 'SHIFT_MALAM'";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                data_sudah_pernah_simpan = true;
            }

            String message = "";
            if (data_sudah_pernah_simpan) {
                message = "Data periode " + dateFormat.format(Date_penggajian.getDate()) + " sudah disimpan!\n"
                        + "Simpan ulang data akan mengubah data sesuai data yang tersimpan terakhir saat ini!\n"
                        + "Simpan ulang data tidak akan otomatis menaikan level gaji CABUT!\n"
                        + "Lanjutkan?";
            } else {
                if (!txt_search_karyawan.getText().equals("") || !txt_search_bagian.getText().equals("")) {
                    JOptionPane.showMessageDialog(this, "Untuk simpan data penggajian yang pertama sebaiknya tanpa filter nama & bagian!");
                }
                message = "Save data penggajian?\n"
                        + "- Otomatis naik level gaji CABUT\n"
                        + "- Status BPJS TK & KS menjadi 'Lunas'\n"
                        + "- Status piutang menjadi 'Lunas'";
            }
            int dialogResult = JOptionPane.showConfirmDialog(this, message, "Warning", 0);
            if (dialogResult == JOptionPane.YES_OPTION) {
                try {
                    Utility.db.getConnection().setAutoCommit(false);
                    int max_progress = Tabel_data.getRowCount();
                    jProgressBar1.setMaximum(max_progress);
                    int jumlah_karyawan_dipotong_bpjs = 0, jumlah_karyawan_dipotong_bpjs_tk = 0, jumlah_karyawan_membayar_piutang = 0, jumlah_karyawan_naik_level = 0, saved_data_payroll = 0;
                    for (int i = 0; i < max_progress; i++) {
                        if ((double) Tabel_data.getValueAt(i, 16) > 0) {
                            sql = "UPDATE `tb_karyawan` SET "
                                    + "`potongan_bpjs`=2 "
                                    + "WHERE `id_pegawai`='" + Tabel_data.getValueAt(i, 1).toString() + "'";
                            Utility.db.getConnection().createStatement();
                            if (Utility.db.getStatement().executeUpdate(sql) == 1) {
                                jumlah_karyawan_dipotong_bpjs++;
                            }
                        }
                        if ((double) Tabel_data.getValueAt(i, 17) > 0) {
                            sql = "UPDATE `tb_karyawan` SET "
                                    + "`potongan_bpjs_tk`=2 "
                                    + "WHERE `id_pegawai`='" + Tabel_data.getValueAt(i, 1).toString() + "'";
                            Utility.db.getConnection().createStatement();
                            if (Utility.db.getStatement().executeUpdate(sql) == 1) {
                                jumlah_karyawan_dipotong_bpjs_tk++;
                            }
                        }
                        if ((double) Tabel_data.getValueAt(i, 25) != 0) {
                            sql = "UPDATE `tb_piutang_karyawan` "
                                    + "SET `status`=1, `tgl_lunas`='" + dateFormat.format(Date_penggajian.getDate()) + "' "
                                    + "WHERE `status`=0 AND `id_pegawai`='" + Tabel_data.getValueAt(i, 1).toString() + "' AND `tanggal_piutang` < '" + dateFormat.format(Date_penggajian.getDate()) + "'";
                            Utility.db.getConnection().createStatement();
                            if (Utility.db.getStatement().executeUpdate(sql) == 1) {
                                jumlah_karyawan_membayar_piutang++;
                            }
                        }

                        String bagian = "NULL";
                        if (Tabel_data.getValueAt(i, 28) != null) {
                            bagian = "'" + Tabel_data.getValueAt(i, 28).toString() + "'";
                        }

                        if (Tabel_data.getValueAt(i, 29) != null && !data_sudah_pernah_simpan) {
                            String level_gaji_old = Tabel_data.getValueAt(i, 29).toString();
                            String level_gaji_new = "";
                            if (level_gaji_old.equalsIgnoreCase("Training Borong Hijau")) {
                                level_gaji_new = "Training Borong Kuning";
                            } else if (level_gaji_old.equalsIgnoreCase("Training Borong Kuning")) {
                                level_gaji_new = "Training Borong Oranye";
                            } else if (level_gaji_old.equalsIgnoreCase("Training Borong Oranye")) {
                                level_gaji_new = "Training Borong Merah";
                            } else if (level_gaji_old.equalsIgnoreCase("Training Borong Merah")) {
                                level_gaji_new = "BORONG HIJAU";
                            } else if (level_gaji_old.equalsIgnoreCase("BORONG HIJAU")) {
                                level_gaji_new = "BORONG KUNING";
                            } else if (level_gaji_old.equalsIgnoreCase("BORONG KUNING")) {
                                level_gaji_new = "BORONG ORANYE";
                            } else if (level_gaji_old.equalsIgnoreCase("BORONG ORANYE")) {
                                level_gaji_new = "BORONG MERAH";
                            } else if (level_gaji_old.equalsIgnoreCase("BORONG MERAH")) {
                                level_gaji_new = "BORONG";
                            } else if (level_gaji_old.equalsIgnoreCase("OM HIJAU")) {
                                level_gaji_new = "OM KUNING";
                            } else if (level_gaji_old.equalsIgnoreCase("OM KUNING")) {
                                level_gaji_new = "OM ORANYE";
                            } else if (level_gaji_old.equalsIgnoreCase("OM ORANYE")) {
                                level_gaji_new = "OM MERAH";
                            } else if (level_gaji_old.equalsIgnoreCase("OM MERAH")) {
                                level_gaji_new = "OM";
                            }

                            String bagian_lama = "PEJUANG-PRODUKSI-" + Tabel_data.getValueAt(i, 28).toString();
                            String bagian_baru = "";
                            if (bagian_lama.equalsIgnoreCase("PEJUANG-PRODUKSI-CABUT-TRAINING HIJAU-A")) {
                                bagian_baru = "PEJUANG-PRODUKSI-CABUT-TRAINING KUNING-A";
                            } else if (bagian_lama.equalsIgnoreCase("PEJUANG-PRODUKSI-CABUT-TRAINING KUNING-A")) {
                                bagian_baru = "PEJUANG-PRODUKSI-CABUT-TRAINING ORANYE-A";
                            } else if (bagian_lama.equalsIgnoreCase("PEJUANG-PRODUKSI-CABUT-TRAINING ORANYE-A")) {
                                bagian_baru = "PEJUANG-PRODUKSI-CABUT-TRAINING MERAH-A";
                            } else if (bagian_lama.equalsIgnoreCase("PEJUANG-PRODUKSI-CABUT-TRAINING MERAH-A")) {
                                bagian_baru = "";
                            } else if (bagian_lama.equalsIgnoreCase("PEJUANG-PRODUKSI-CABUT-TRAINING HIJAU-B")) {
                                bagian_baru = "PEJUANG-PRODUKSI-CABUT-TRAINING KUNING-B";
                            } else if (bagian_lama.equalsIgnoreCase("PEJUANG-PRODUKSI-CABUT-TRAINING KUNING-B")) {
                                bagian_baru = "PEJUANG-PRODUKSI-CABUT-TRAINING ORANYE-B";
                            } else if (bagian_lama.equalsIgnoreCase("PEJUANG-PRODUKSI-CABUT-TRAINING ORANYE-B")) {
                                bagian_baru = "PEJUANG-PRODUKSI-CABUT-TRAINING MERAH-B";
                            } else if (bagian_lama.equalsIgnoreCase("PEJUANG-PRODUKSI-CABUT-TRAINING MERAH-B")) {
                                bagian_baru = "";
                            } else if (bagian_lama.equalsIgnoreCase("PEJUANG-PRODUKSI-CABUT-TRAINING HIJAU-C")) {
                                bagian_baru = "PEJUANG-PRODUKSI-CABUT-TRAINING KUNING-C";
                            } else if (bagian_lama.equalsIgnoreCase("PEJUANG-PRODUKSI-CABUT-TRAINING KUNING-C")) {
                                bagian_baru = "PEJUANG-PRODUKSI-CABUT-TRAINING ORANYE-C";
                            } else if (bagian_lama.equalsIgnoreCase("PEJUANG-PRODUKSI-CABUT-TRAINING ORANYE-C")) {
                                bagian_baru = "PEJUANG-PRODUKSI-CABUT-TRAINING MERAH-C";
                            } else if (bagian_lama.equalsIgnoreCase("PEJUANG-PRODUKSI-CABUT-TRAINING MERAH-C")) {
                                bagian_baru = "";
                            } else if (bagian_lama.equalsIgnoreCase("PEJUANG-PRODUKSI-CABUT-TRAINING HIJAU-D")) {
                                bagian_baru = "PEJUANG-PRODUKSI-CABUT-TRAINING KUNING-D";
                            } else if (bagian_lama.equalsIgnoreCase("PEJUANG-PRODUKSI-CABUT-TRAINING KUNING-D")) {
                                bagian_baru = "PEJUANG-PRODUKSI-CABUT-TRAINING ORANYE-D";
                            } else if (bagian_lama.equalsIgnoreCase("PEJUANG-PRODUKSI-CABUT-TRAINING ORANYE-D")) {
                                bagian_baru = "PEJUANG-PRODUKSI-CABUT-TRAINING MERAH-D";
                            } else if (bagian_lama.equalsIgnoreCase("PEJUANG-PRODUKSI-CABUT-TRAINING MERAH-D")) {
                                bagian_baru = "";
                            }

                            if (!level_gaji_new.equals("")) {
                                String pindah_bagian = "";
                                if (!bagian_baru.equals("")) {
                                    pindah_bagian = ", `kode_bagian`=(SELECT `kode_bagian` FROM `tb_bagian` WHERE `nama_bagian` = '" + bagian_baru + "') ";
                                }
                                sql = "UPDATE `tb_karyawan` SET "
                                        + "`level_gaji`='" + level_gaji_new + "' \n"
                                        + pindah_bagian
                                        + "WHERE `id_pegawai`='" + Tabel_data.getValueAt(i, 1).toString() + "'";
                                Utility.db.getConnection().createStatement();
                                if (Utility.db.getStatement().executeUpdate(sql) == 1) {
                                    jumlah_karyawan_naik_level++;
                                }
                            }
                        }
                        String insert_payroll_data = "INSERT INTO `tb_payrol_data`(`tgl_penggajian`, `id_pegawai`, `total_jam_lembur`, `lembur`, `pot_terlambat`, `pot_ijin_keluar`, `tunjangan_hadir`, `pot_transport`, `pot_bpjs`, `pot_bpjs_tk`, `bonus_tbt`, `gaji_harian`, `gaji_borong`, `bonus_1`, `bonus_2`, `bonus_pencapaian_produksi`, `piutang`, `keterangan`, `bagian`, `level_gaji`, `jam_kerja`) "
                                + "VALUES ('" + dateFormat.format(Date_penggajian.getDate()) + "',"
                                + "'" + Tabel_data.getValueAt(i, 1).toString() + "'," //ID PEGAWAI
                                + "'" + convertNullWithZero(Tabel_data.getValueAt(i, 10)) + "'," //TOTAL JAM LEMBUR
                                + "'" + convertNullWithZero(Tabel_data.getValueAt(i, 11)) + "'," //LEMBUR
                                + "'" + convertNullWithZero(Tabel_data.getValueAt(i, 12)) + "'," //POT TERLAMBAT
                                + "'" + convertNullWithZero(Tabel_data.getValueAt(i, 13)) + "'," //POT IJIN KELUAR
                                + "'" + convertNullWithZero(Tabel_data.getValueAt(i, 14)) + "'," //TUNJANGAN HADIR
                                + "'" + convertNullWithZero(Tabel_data.getValueAt(i, 15)) + "'," //POT TRANSPORT
                                + "'" + convertNullWithZero(Tabel_data.getValueAt(i, 16)) + "'," //POT BPJS
                                + "'" + convertNullWithZero(Tabel_data.getValueAt(i, 17)) + "'," //POT BPJS TK
                                + "'" + convertNullWithZero(Tabel_data.getValueAt(i, 18)) + "'," //BONUS TBT
                                + "'" + convertNullWithZero(Tabel_data.getValueAt(i, 20)) + "'," //GAJI HARIAN
                                + "'" + convertNullWithZero(Tabel_data.getValueAt(i, 21)) + "'," //GAJI BORONG
                                + "'" + convertNullWithZero(Tabel_data.getValueAt(i, 22)) + "'," //BONUS 1
                                + "'" + convertNullWithZero(Tabel_data.getValueAt(i, 23)) + "'," //BONUS 2
                                + "'" + convertNullWithZero(Tabel_data.getValueAt(i, 24)) + "'," //bonus pencapaian produksi
                                + "'" + convertNullWithZero(Tabel_data.getValueAt(i, 25)) + "'," //piutang
                                + "'" + Tabel_data.getValueAt(i, 27).toString() + "'," //keterangan
                                + "" + bagian + "," //bagian
                                + "'" + Tabel_data.getValueAt(i, 29).toString() + "'," //level gaji
                                + "'" + Tabel_data.getValueAt(i, 30).toString() + "'" //jam_kerja
                                + ") "
                                + "ON DUPLICATE KEY UPDATE "
                                + "`total_jam_lembur`='" + convertNullWithZero(Tabel_data.getValueAt(i, 10)) + "',"
                                + "`lembur`='" + convertNullWithZero(Tabel_data.getValueAt(i, 11)) + "',"
                                + "`pot_terlambat`='" + convertNullWithZero(Tabel_data.getValueAt(i, 12)) + "',"
                                + "`pot_ijin_keluar`='" + convertNullWithZero(Tabel_data.getValueAt(i, 13)) + "',"
                                + "`tunjangan_hadir`='" + convertNullWithZero(Tabel_data.getValueAt(i, 14)) + "',"
                                + "`pot_transport`='" + convertNullWithZero(Tabel_data.getValueAt(i, 15)) + "',"
                                + "`pot_bpjs`='" + convertNullWithZero(Tabel_data.getValueAt(i, 16)) + "',"
                                + "`pot_bpjs_tk`='" + convertNullWithZero(Tabel_data.getValueAt(i, 17)) + "',"
                                + "`bonus_tbt`='" + convertNullWithZero(Tabel_data.getValueAt(i, 18)) + "',"
                                + "`gaji_harian`='" + convertNullWithZero(Tabel_data.getValueAt(i, 20)) + "',"
                                + "`gaji_borong`='" + convertNullWithZero(Tabel_data.getValueAt(i, 21)) + "',"
                                + "`bonus_1`='" + convertNullWithZero(Tabel_data.getValueAt(i, 22)) + "',"
                                + "`bonus_2`='" + convertNullWithZero(Tabel_data.getValueAt(i, 23)) + "',"
                                + "`bonus_pencapaian_produksi`='" + convertNullWithZero(Tabel_data.getValueAt(i, 24)) + "',"
                                + "`piutang`='" + convertNullWithZero(Tabel_data.getValueAt(i, 25)) + "',"
                                + "`keterangan`='" + Tabel_data.getValueAt(i, 27).toString() + "',"
                                + "`bagian`=" + bagian + ","
                                + "`level_gaji`='" + Tabel_data.getValueAt(i, 29).toString() + "',"
                                + "`jam_kerja`='" + Tabel_data.getValueAt(i, 30).toString() + "'";
                        Utility.db.getConnection().createStatement();
                        if (Utility.db.getStatement().executeUpdate(insert_payroll_data) == 1) {
                            saved_data_payroll++;
                        }

                        jProgressBar1.setValue(i + 1);
                        jProgressBar1.setString((i + 1) + "/" + max_progress);
                    }
                    Utility.db.getConnection().commit();

                    JOptionPane.showMessageDialog(this, "Jumlah Karyawan yang telah:\n"
                            + "1. Dipotong BPJS Kesehatan = " + jumlah_karyawan_dipotong_bpjs + "\n"
                            + "2. Dipotong BPJS TK = " + jumlah_karyawan_dipotong_bpjs_tk + "\n"
                            + "3. Membayar piutang = " + jumlah_karyawan_membayar_piutang + "\n"
                            + "4. Karyawan otomatis naik level = " + jumlah_karyawan_naik_level + "\n"
                            + "5. Data Payrol di simpan = " + saved_data_payroll);
                } catch (SQLException ex) {
                    try {
                        Utility.db.getConnection().rollback();
                    } catch (SQLException e) {
                        Logger.getLogger(JPanel_payrol_harian.class.getName()).log(Level.SEVERE, null, e);
                    }
                    JOptionPane.showMessageDialog(this, ex.getMessage());
                    Logger.getLogger(JPanel_payrol_harian.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    try {
                        Utility.db.getConnection().setAutoCommit(true);
                    } catch (SQLException ex) {
                        Logger.getLogger(JPanel_payrol_harian.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(JPanel_payrol_harian.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void rekap_jam_kerja_kurang() {
        try {
            ArrayList<String> id_pegawai = new ArrayList<>();
            ArrayList<String> tanggal = new ArrayList<>();
            Date tanggal_mulai = new Date(Date_penggajian.getDate().getTime() - (7 * 24 * 60 * 60 * 1000));
            Date tanggal_selesai = new Date(Date_penggajian.getDate().getTime() - (1 * 24 * 60 * 60 * 1000));

            sql = "SELECT `tb_lembur_rekap`.`id_pegawai`, `tb_karyawan`.`nama_pegawai`, CONCAT(`divisi_bagian`, '-', `ruang_bagian`) AS 'nama_bagian',\n"
                    + "DAYNAME(`tanggal`) AS 'hari', `tanggal`, `premi_hadir` \n"
                    + "FROM `tb_lembur_rekap` \n"
                    + "LEFT JOIN `tb_karyawan` ON `tb_lembur_rekap`.`id_pegawai` = `tb_karyawan`.`id_pegawai`\n"
                    + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`\n"
                    + "WHERE `premi_hadir` = 0\n"
                    + "AND `jam_kerja` <> 'SHIFT_MALAM'\n"
                    + "AND `tanggal` BETWEEN '" + tanggal_mulai + "' AND '" + tanggal_selesai + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                id_pegawai.add(rs.getString("id_pegawai"));
                tanggal.add(rs.getString("tanggal"));
            }

            if (id_pegawai.size() > 0) {
                int dialogResult = JOptionPane.showConfirmDialog(this, id_pegawai.size() + " karyawan kurang jam kerja, input ke data cuti?", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    int count = 0;
                    for (int i = 0; i < id_pegawai.size(); i++) {
                        String Query = "INSERT INTO `tb_cuti`(`id_pegawai`, `tanggal_cuti`, `jenis_cuti`, `kategori_cuti`, `keterangan`) "
                                + "SELECT * FROM (SELECT '" + id_pegawai.get(i) + "','" + tanggal.get(i) + "', 'Absen', 'Jam Kerja Kurang', '-' AS 'keterangan') AS tmp\n"
                                + "WHERE NOT EXISTS (SELECT `kode_cuti` FROM `tb_cuti` WHERE `id_pegawai` = '" + id_pegawai.get(i) + "' AND `tanggal_cuti` = '" + tanggal.get(i) + "')";
                        Utility.db.getConnection().createStatement();
                        if (Utility.db.getStatement().executeUpdate(Query) == 1) {
                            count++;
                        }
                    }
                    JOptionPane.showMessageDialog(this, count + " data berhasil di input ke data cuti/absen !");
                }
            }

//        JDialog_Input_JamKerjaKurang dialog = new JDialog_Input_JamKerjaKurang(new javax.swing.JFrame(), true, tanggal_mulai, tanggal_selesai);
//        dialog.setResizable(false);
//        dialog.setLocationRelativeTo(this);
//        dialog.setEnabled(true);
//        dialog.setVisible(true);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_payrol_harian.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        Date_penggajian = new com.toedter.calendar.JDateChooser();
        button_load = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        Tabel_data = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        label_total_upah_lembur = new javax.swing.JLabel();
        label_total_tunjangan_hadir = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        label_total_gaji = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txt_search_karyawan = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        txt_bonus_tbt = new javax.swing.JTextField();
        CheckBox_hide_Gaji_0 = new javax.swing.JCheckBox();
        jLabel13 = new javax.swing.JLabel();
        txt_potongan_transport = new javax.swing.JTextField();
        txt_potongan_bpjs = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        button_save_data_fix = new javax.swing.JButton();
        button_print_slip_per_grup = new javax.swing.JButton();
        button_input_bonus_pencapaian = new javax.swing.JButton();
        button_slip_borong_cabut = new javax.swing.JButton();
        jLabel18 = new javax.swing.JLabel();
        txt_potongan_bpjs_tk = new javax.swing.JTextField();
        button_export = new javax.swing.JButton();
        jLabel19 = new javax.swing.JLabel();
        txt_search_bagian = new javax.swing.JTextField();
        button_slip_mandiri_cetak = new javax.swing.JButton();
        button_slip_harian = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        button_print_slip_per_grup2 = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        label_total_bonus_lp = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        label_total_upah_cabut = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        label_total_upah_non_cabut = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextArea3 = new javax.swing.JTextArea();
        jProgressBar1 = new javax.swing.JProgressBar();
        button_slip_cabut = new javax.swing.JButton();
        jLabel20 = new javax.swing.JLabel();
        txt_jumlah_karyawan_terlambat = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        Date_Masuk1 = new com.toedter.calendar.JDateChooser();
        Date_Masuk2 = new com.toedter.calendar.JDateChooser();
        button_input_gaji_borong_OM = new javax.swing.JButton();
        button_slip_borong_cetak2 = new javax.swing.JButton();
        jLabel23 = new javax.swing.JLabel();
        txt_search_level_gaji = new javax.swing.JTextField();

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel1.setText("Perhitungan Payroll PEJUANG WALETA");

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setText("TGL penggajian :");

        Date_penggajian.setBackground(new java.awt.Color(255, 255, 255));
        Date_penggajian.setDateFormatString("dd MMMM yyyy");
        Date_penggajian.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

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
                "No", "ID", "Nama", "Tgl_1", "Tgl_2", "Tgl_3", "Tgl_4", "Tgl_5", "Tgl_6", "Tgl_7", "Total Lembur", "Lembur (Rp.)", "Terlambat (Rp.)", "Ijin Keluar (Rp.)", "Tunj. hadir", "Transport", "BPJS", "BPJS TK", "Bonus TBT", "Hari Kerja", "Gaji Harian", "Gaji Borong", "Bonus 1", "Bonus 2", "Bonus Pencapaian Produksi", "Piutang", "Total Gaji Trasfer", "Ket.", "Bagian", "Level Gaji", "Jam Kerja"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Double.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
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
        if (Tabel_data.getColumnModel().getColumnCount() > 0) {
            Tabel_data.getColumnModel().getColumn(3).setMinWidth(50);
            Tabel_data.getColumnModel().getColumn(4).setMinWidth(50);
            Tabel_data.getColumnModel().getColumn(5).setMinWidth(50);
            Tabel_data.getColumnModel().getColumn(6).setMinWidth(50);
            Tabel_data.getColumnModel().getColumn(7).setMinWidth(50);
            Tabel_data.getColumnModel().getColumn(8).setMinWidth(50);
            Tabel_data.getColumnModel().getColumn(9).setMinWidth(50);
        }

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel3.setText("Total Upah Lembur :");

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel4.setText("Total Tunjangan hadir :");

        label_total_upah_lembur.setBackground(new java.awt.Color(255, 255, 255));
        label_total_upah_lembur.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_upah_lembur.setText("0");

        label_total_tunjangan_hadir.setBackground(new java.awt.Color(255, 255, 255));
        label_total_tunjangan_hadir.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_tunjangan_hadir.setText("0");

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel7.setText("Total Transfer :");

        label_total_gaji.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gaji.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_gaji.setText("0");

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel6.setText("Nama :");

        txt_search_karyawan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_search_karyawan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_karyawanKeyPressed(evt);
            }
        });

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel11.setText("Bonus TBT / org :");

        txt_bonus_tbt.setEditable(false);
        txt_bonus_tbt.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_bonus_tbt.setText("20000");

        CheckBox_hide_Gaji_0.setBackground(new java.awt.Color(255, 255, 255));
        CheckBox_hide_Gaji_0.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        CheckBox_hide_Gaji_0.setText("Hide Gaji 0");
        CheckBox_hide_Gaji_0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CheckBox_hide_Gaji_0ActionPerformed(evt);
            }
        });

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel13.setText("Pot. Transport :");

        txt_potongan_transport.setEditable(false);
        txt_potongan_transport.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_potongan_transport.setText("7500");

        txt_potongan_bpjs.setEditable(false);
        txt_potongan_bpjs.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_potongan_bpjs.setText("23790");

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel14.setText("Pot. BPJS KS :");

        button_save_data_fix.setBackground(new java.awt.Color(255, 255, 255));
        button_save_data_fix.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_save_data_fix.setText("Simpan data FIX penggajian");
        button_save_data_fix.setEnabled(false);
        button_save_data_fix.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_save_data_fixActionPerformed(evt);
            }
        });

        button_print_slip_per_grup.setBackground(new java.awt.Color(255, 255, 255));
        button_print_slip_per_grup.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_print_slip_per_grup.setText("Slip / Grup");
        button_print_slip_per_grup.setEnabled(false);
        button_print_slip_per_grup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_print_slip_per_grupActionPerformed(evt);
            }
        });

        button_input_bonus_pencapaian.setBackground(new java.awt.Color(255, 255, 255));
        button_input_bonus_pencapaian.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_input_bonus_pencapaian.setText("Bonus Pencapaian Produksi");
        button_input_bonus_pencapaian.setEnabled(false);
        button_input_bonus_pencapaian.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_input_bonus_pencapaianActionPerformed(evt);
            }
        });

        button_slip_borong_cabut.setBackground(new java.awt.Color(255, 255, 255));
        button_slip_borong_cabut.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_slip_borong_cabut.setText("Slip Borong Cabut");
        button_slip_borong_cabut.setEnabled(false);
        button_slip_borong_cabut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_slip_borong_cabutActionPerformed(evt);
            }
        });

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel18.setText("Pot. BPJS TK :");

        txt_potongan_bpjs_tk.setEditable(false);
        txt_potongan_bpjs_tk.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_potongan_bpjs_tk.setText("71370");

        button_export.setBackground(new java.awt.Color(255, 255, 255));
        button_export.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
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

        button_slip_mandiri_cetak.setBackground(new java.awt.Color(255, 255, 255));
        button_slip_mandiri_cetak.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_slip_mandiri_cetak.setText("Slip Mandiri Cetak");
        button_slip_mandiri_cetak.setEnabled(false);
        button_slip_mandiri_cetak.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_slip_mandiri_cetakActionPerformed(evt);
            }
        });

        button_slip_harian.setBackground(new java.awt.Color(255, 255, 255));
        button_slip_harian.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_slip_harian.setText("Slip Harian");
        button_slip_harian.setEnabled(false);
        button_slip_harian.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_slip_harianActionPerformed(evt);
            }
        });

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        jTextArea1.setRows(5);
        jTextArea1.setText("1. jika potongan tidak lebih dari 60 menit, maka terhitung masuk dan dapat premi.\n2. premi hadir diberikan tambahan jika masuk di hari libur lebih dari 4 jam.\n3. JIKA Gaji < 0, maka potongan BPJS di 0 kan, dan akan di potong minggu depan.\n4. jika level gaji mengandung BORONG, maka lembur = 0\n5. Untuk bagian \"SECURITY\",  upah lembur 10.000/jam\n6. Potongan ijin / terlambat = 1/7 x upah/hari x jam terlambat.\n7. jika hari tsb TIDAK terlambat maka premi kedisiplinan diberikan (dihitung harian).");
        jScrollPane2.setViewportView(jTextArea1);

        jTextArea2.setEditable(false);
        jTextArea2.setColumns(20);
        jTextArea2.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        jTextArea2.setRows(5);
        jTextArea2.setText("1. Bonus 1 adalah Bonus LP / Bonus Kecepatan.\n2. Bonus 2 adalah Bonus Mk Utuh");
        jScrollPane3.setViewportView(jTextArea2);

        button_print_slip_per_grup2.setBackground(new java.awt.Color(255, 255, 255));
        button_print_slip_per_grup2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_print_slip_per_grup2.setText("Slip / Grup 2");
        button_print_slip_per_grup2.setEnabled(false);
        button_print_slip_per_grup2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_print_slip_per_grup2ActionPerformed(evt);
            }
        });

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel8.setText("Total Bonus LP :");

        label_total_bonus_lp.setBackground(new java.awt.Color(255, 255, 255));
        label_total_bonus_lp.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_bonus_lp.setText("0");

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel9.setText("Total Upah Cabut :");

        label_total_upah_cabut.setBackground(new java.awt.Color(255, 255, 255));
        label_total_upah_cabut.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_upah_cabut.setText("0");

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel10.setText("Total Upah Non Cabut :");

        label_total_upah_non_cabut.setBackground(new java.awt.Color(255, 255, 255));
        label_total_upah_non_cabut.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_upah_non_cabut.setText("0");

        jTextArea3.setEditable(false);
        jTextArea3.setColumns(20);
        jTextArea3.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        jTextArea3.setRows(5);
        jTextArea3.setText("Otomatis naik level gaji dan bagian ketika save data:\nTraining Borong Hijau -> Training Borong Kuning\nTraining Borong Kuning -> Training Borong Oranye\nTraining Borong Oranye -> Training Borong Merah\nTraining Borong Merah -> BORONG HIJAU\nBORONG HIJAU -> BORONG KUNING\nBORONG KUNING -> BORONG ORANYE\nBORONG ORANYE -> BORONG MERAH\nBORONG MERAH -> BORONG");
        jScrollPane4.setViewportView(jTextArea3);

        jProgressBar1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jProgressBar1.setString("0/0");
        jProgressBar1.setStringPainted(true);

        button_slip_cabut.setBackground(new java.awt.Color(255, 255, 255));
        button_slip_cabut.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_slip_cabut.setText("Slip Cabut");
        button_slip_cabut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_slip_cabutActionPerformed(evt);
            }
        });

        jLabel20.setBackground(new java.awt.Color(255, 255, 255));
        jLabel20.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel20.setText("Jumlah Karyawan Terlambat :");

        txt_jumlah_karyawan_terlambat.setEditable(false);
        txt_jumlah_karyawan_terlambat.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_jumlah_karyawan_terlambat.setText("0");

        jLabel21.setBackground(new java.awt.Color(255, 255, 255));
        jLabel21.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(255, 0, 0));
        jLabel21.setText("MAX 30");

        jLabel22.setBackground(new java.awt.Color(255, 255, 255));
        jLabel22.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel22.setText("Tgl Masuk :");

        Date_Masuk1.setBackground(new java.awt.Color(255, 255, 255));
        Date_Masuk1.setDateFormatString("dd MMM yyyy");
        Date_Masuk1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        Date_Masuk2.setBackground(new java.awt.Color(255, 255, 255));
        Date_Masuk2.setDateFormatString("dd MMM yyyy");
        Date_Masuk2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        button_input_gaji_borong_OM.setBackground(new java.awt.Color(255, 255, 255));
        button_input_gaji_borong_OM.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_input_gaji_borong_OM.setText("Input Gaji Borong OM");
        button_input_gaji_borong_OM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_input_gaji_borong_OMActionPerformed(evt);
            }
        });

        button_slip_borong_cetak2.setBackground(new java.awt.Color(255, 255, 255));
        button_slip_borong_cetak2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_slip_borong_cetak2.setText("Slip Borong Cetak");
        button_slip_borong_cetak2.setEnabled(false);
        button_slip_borong_cetak2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_slip_borong_cetak2ActionPerformed(evt);
            }
        });

        jLabel23.setBackground(new java.awt.Color(255, 255, 255));
        jLabel23.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel23.setText("Level Gaji :");

        txt_search_level_gaji.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_search_level_gaji.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_level_gajiKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 416, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 416, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 267, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel1)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_upah_lembur)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_tunjangan_hadir)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_bonus_lp)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_upah_cabut)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_upah_non_cabut)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_gaji))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_bonus_tbt, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_potongan_transport, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_potongan_bpjs, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel18)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_potongan_bpjs_tk, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel20)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_jumlah_karyawan_terlambat, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel21)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(button_save_data_fix)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(button_input_bonus_pencapaian)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_print_slip_per_grup)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_print_slip_per_grup2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_slip_harian)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_slip_borong_cabut)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_slip_cabut)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_slip_borong_cetak2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_slip_mandiri_cetak)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_export)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_input_gaji_borong_OM))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_penggajian, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel19)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_bagian, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel23)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_level_gaji, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel22)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_Masuk1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_Masuk2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_load)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(CheckBox_hide_Gaji_0)))
                        .addGap(0, 4, Short.MAX_VALUE)))
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
                    .addComponent(Date_penggajian, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_bagian, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_load, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CheckBox_hide_Gaji_0)
                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_Masuk1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_Masuk2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_level_gaji, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_bonus_tbt, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_potongan_transport, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_potongan_bpjs, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_potongan_bpjs_tk, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_jumlah_karyawan_terlambat, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_save_data_fix, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_gaji, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_upah_non_cabut, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_upah_cabut, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_bonus_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_upah_lembur, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_tunjangan_hadir, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(button_print_slip_per_grup)
                    .addComponent(button_slip_borong_cabut)
                    .addComponent(button_slip_mandiri_cetak)
                    .addComponent(button_slip_harian)
                    .addComponent(button_print_slip_per_grup2)
                    .addComponent(button_input_bonus_pencapaian)
                    .addComponent(button_export)
                    .addComponent(button_slip_cabut)
                    .addComponent(button_input_gaji_borong_OM)
                    .addComponent(button_slip_borong_cetak2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 403, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)
                    .addComponent(jScrollPane3)
                    .addComponent(jScrollPane2))
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

    private void txt_search_karyawanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_karyawanKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_search_karyawanKeyPressed

    private void CheckBox_hide_Gaji_0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CheckBox_hide_Gaji_0ActionPerformed
        // TODO add your handling code here:
        refreshTable();
    }//GEN-LAST:event_CheckBox_hide_Gaji_0ActionPerformed

    private void button_save_data_fixActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_save_data_fixActionPerformed
        // TODO add your handling code here:
        LocalDate dt = LocalDate.now();
//        if (dt.with(TemporalAdjusters.previous(DayOfWeek.THURSDAY)).toString().equals(dateFormat.format(Date_penggajian.getDate()))
//                || Date_penggajian.getDate() == today) {

        Date_penggajian.setEnabled(false);
        txt_search_karyawan.setEnabled(false);
        txt_search_bagian.setEnabled(false);
        button_load.setEnabled(false);
        CheckBox_hide_Gaji_0.setEnabled(false);
        button_input_bonus_pencapaian.setEnabled(false);
        button_print_slip_per_grup.setEnabled(false);
        button_print_slip_per_grup2.setEnabled(false);
        button_save_data_fix.setEnabled(false);
        button_slip_borong_cabut.setEnabled(false);
        button_slip_harian.setEnabled(false);
        button_slip_mandiri_cetak.setEnabled(false);

        Thread thread = new Thread() {
            @Override
            public void run() {
                saveDataPenggajian();
                Date_penggajian.setEnabled(true);
                txt_search_karyawan.setEnabled(true);
                txt_search_bagian.setEnabled(true);
                button_load.setEnabled(true);
                CheckBox_hide_Gaji_0.setEnabled(true);
                button_input_bonus_pencapaian.setEnabled(true);
                button_print_slip_per_grup.setEnabled(true);
                button_print_slip_per_grup2.setEnabled(true);
                button_save_data_fix.setEnabled(true);
                button_slip_borong_cabut.setEnabled(true);
                button_slip_harian.setEnabled(true);
                button_slip_mandiri_cetak.setEnabled(true);
            }
        };
        thread.start();

        rekap_jam_kerja_kurang();
//        } else {
//            JOptionPane.showMessageDialog(this, "Maaf, tidak bisa save data periode penggajian yang sudah lama!");
//        }

    }//GEN-LAST:event_button_save_data_fixActionPerformed

    private void button_print_slip_per_grupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_print_slip_per_grupActionPerformed
        try {
            JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Slip_Gaji_Karyawan_per_Grup.jrxml");
            JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
            Map<String, Object> params = new HashMap<>();
            params.put("tgl1", judulTgl1);
            params.put("tgl2", judulTgl2);
            params.put("tgl3", judulTgl3);
            params.put("tgl4", judulTgl4);
            params.put("tgl5", judulTgl5);
            params.put("tgl6", judulTgl6);
            params.put("tgl7", judulTgl7);
            /*params.entrySet().forEach((entry) -> {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        });*/
            JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, params, new JRBeanCollectionDataSource(listPayrolHarian));
            JasperViewer.viewReport(JASP_PRINT, false);
        } catch (JRException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_payrol_harian.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_print_slip_per_grupActionPerformed

    private void button_input_bonus_pencapaianActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_input_bonus_pencapaianActionPerformed
        // TODO add your handling code here:
        JDialog_BonusPencapaianProduksi dialog = new JDialog_BonusPencapaianProduksi(new javax.swing.JFrame(), true);
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(this);
        dialog.setEnabled(true);
        dialog.setVisible(true);
    }//GEN-LAST:event_button_input_bonus_pencapaianActionPerformed

    private void button_slip_borong_cabutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_slip_borong_cabutActionPerformed
        // TODO add your handling code here:
        Date tanggal_mulai = new Date(Date_penggajian.getDate().getTime() - (7 * 24 * 60 * 60 * 1000));
        Date tanggal_selesai = new Date(Date_penggajian.getDate().getTime() - (1 * 24 * 60 * 60 * 1000));
        try {
            double gaji_harian = Double.valueOf(Tabel_data.getValueAt(0, 20).toString());
            double potongan_terlambat = Double.valueOf(Tabel_data.getValueAt(0, 12).toString());
            double potongan_ijin = Double.valueOf(Tabel_data.getValueAt(0, 13).toString());
            gaji_harian = gaji_harian - (potongan_terlambat + potongan_ijin);
            JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Slip_Gaji_karyawan_Cabut.jrxml");
            Map<String, Object> map = new HashMap<>();
            map.put("HALAMAN", 1);
            map.put("PERIODE_AWAL", tanggal_mulai);
            map.put("PERIODE_AKHIR", tanggal_selesai);
            map.put("ID_PEGAWAI", Tabel_data.getValueAt(0, 1).toString());
            map.put("NAMA_PEGAWAI", Tabel_data.getValueAt(0, 2).toString());
            map.put("LEMBUR", Double.valueOf(Tabel_data.getValueAt(0, 11).toString()));
            map.put("TUNJANGAN_HADIR", Double.valueOf(Tabel_data.getValueAt(0, 14).toString()));
            map.put("POT_TRANSPORT", Double.valueOf(Tabel_data.getValueAt(0, 15).toString()));
            map.put("POT_BPJS", Double.valueOf(Tabel_data.getValueAt(0, 16).toString()));
            map.put("POT_BPJS_TK", Double.valueOf(Tabel_data.getValueAt(0, 17).toString()));
            map.put("BONUS_TBT", Double.valueOf(Tabel_data.getValueAt(0, 18).toString()));
            map.put("GAJI_HARIAN", gaji_harian);
            map.put("GAJI_BORONG", Double.valueOf(Tabel_data.getValueAt(0, 21).toString()));
            map.put("BONUS_LP", Double.valueOf(Tabel_data.getValueAt(0, 22).toString()));
            map.put("BONUS_MK_UTUH", Double.valueOf(Tabel_data.getValueAt(0, 23).toString()));
            map.put("BONUS_PENCAPAIAN_PRODUKSI", Double.valueOf(Tabel_data.getValueAt(0, 24).toString()));
            map.put("PIUTANG", Double.valueOf(Tabel_data.getValueAt(0, 25).toString()));
            map.put("KETERANGAN", Tabel_data.getValueAt(0, 27).toString());
            map.put("GRUP", Tabel_data.getValueAt(0, 28));
            JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
            JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, map, Utility.db.getConnection());
            for (int i = 1; i < Tabel_data.getRowCount(); i++) {
                double gaji_harian2 = Double.valueOf(Tabel_data.getValueAt(i, 20).toString());
                double potongan_terlambat2 = Double.valueOf(Tabel_data.getValueAt(i, 12).toString());
                double potongan_ijin2 = Double.valueOf(Tabel_data.getValueAt(i, 13).toString());
                gaji_harian2 = gaji_harian2 - (potongan_terlambat2 + potongan_ijin2);
                JasperDesign JASP_DESIGN2 = JRXmlLoader.load("Report\\Slip_Gaji_karyawan_Cabut.jrxml");
                JasperReport JASP_REP2 = JasperCompileManager.compileReport(JASP_DESIGN2);
                Map<String, Object> map2 = new HashMap<>();
                map2.put("HALAMAN", i + 1);
                map2.put("PERIODE_AWAL", tanggal_mulai);
                map2.put("PERIODE_AKHIR", tanggal_selesai);
                map2.put("ID_PEGAWAI", Tabel_data.getValueAt(i, 1).toString());
                map2.put("NAMA_PEGAWAI", Tabel_data.getValueAt(i, 2).toString());
                map2.put("LEMBUR", Double.valueOf(Tabel_data.getValueAt(i, 11).toString()));
                map2.put("TUNJANGAN_HADIR", Double.valueOf(Tabel_data.getValueAt(i, 14).toString()));
                map2.put("POT_TRANSPORT", Double.valueOf(Tabel_data.getValueAt(i, 15).toString()));
                map2.put("POT_BPJS", Double.valueOf(Tabel_data.getValueAt(i, 16).toString()));
                map2.put("POT_BPJS_TK", Double.valueOf(Tabel_data.getValueAt(i, 17).toString()));
                map2.put("BONUS_TBT", Double.valueOf(Tabel_data.getValueAt(i, 18).toString()));
                map2.put("GAJI_HARIAN", gaji_harian2);
                map2.put("GAJI_BORONG", Double.valueOf(Tabel_data.getValueAt(i, 21).toString()));
                map2.put("BONUS_LP", Double.valueOf(Tabel_data.getValueAt(i, 22).toString()));
                map2.put("BONUS_MK_UTUH", Double.valueOf(Tabel_data.getValueAt(i, 23).toString()));
                map2.put("BONUS_PENCAPAIAN_PRODUKSI", Double.valueOf(Tabel_data.getValueAt(i, 24).toString()));
                map2.put("PIUTANG", Double.valueOf(Tabel_data.getValueAt(i, 25).toString()));
                map2.put("KETERANGAN", Tabel_data.getValueAt(i, 27).toString());
                map2.put("GRUP", Tabel_data.getValueAt(i, 28));
                JasperPrint JASP_PRINT2 = JasperFillManager.fillReport(JASP_REP2, map2, Utility.db.getConnection());
                List pages = JASP_PRINT2.getPages();
                for (int k = 0; k < pages.size(); k++) {
                    JRPrintPage object = (JRPrintPage) pages.get(k);
                    JASP_PRINT.addPage(object);
                }
            }
            JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
        } catch (JRException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_payrol_harian.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_slip_borong_cabutActionPerformed

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

    private void button_slip_mandiri_cetakActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_slip_mandiri_cetakActionPerformed
        // TODO add your handling code here:
        Date tanggal_mulai = new Date(Date_penggajian.getDate().getTime() - (7 * 24 * 60 * 60 * 1000));
        Date tanggal_selesai = new Date(Date_penggajian.getDate().getTime() - (1 * 24 * 60 * 60 * 1000));
        try {
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
            map.put("LEMBUR", Double.valueOf("0.0"));
            map.put("TUNJANGAN_HADIR", Double.valueOf(Tabel_data.getValueAt(0, 14).toString()));
            map.put("POT_TRANSPORT", Double.valueOf(Tabel_data.getValueAt(0, 15).toString()));
            map.put("POT_BPJS", Double.valueOf(Tabel_data.getValueAt(0, 16).toString()));
            map.put("POT_BPJS_TK", Double.valueOf(Tabel_data.getValueAt(0, 17).toString()));
            map.put("BONUS_TBT", Double.valueOf(Tabel_data.getValueAt(0, 18).toString()));
            map.put("GAJI_HARIAN", Double.valueOf("0.0"));
            map.put("GAJI_BORONG", Double.valueOf(Tabel_data.getValueAt(0, 21).toString()));
            map.put("BONUS_LP", Double.valueOf(Tabel_data.getValueAt(0, 22).toString()));
            map.put("BONUS_MK_UTUH", Double.valueOf(Tabel_data.getValueAt(0, 23).toString()));
            map.put("BONUS_PENCAPAIAN_PRODUKSI", Double.valueOf(Tabel_data.getValueAt(0, 24).toString()));
            map.put("PIUTANG", Double.valueOf(Tabel_data.getValueAt(0, 25).toString()));
            map.put("KETERANGAN", Tabel_data.getValueAt(0, 27).toString());
            map.put("GRUP", Tabel_data.getValueAt(0, 28));
            JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
            JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, map, Utility.db.getConnection());
            for (int i = 1; i < Tabel_data.getRowCount(); i++) {
                JasperDesign JASP_DESIGN2 = JRXmlLoader.load("Report\\Slip_Gaji_karyawan_Cetak.jrxml");
                JasperReport JASP_REP2 = JasperCompileManager.compileReport(JASP_DESIGN2);
                Map<String, Object> map2 = new HashMap<>();
                map2.put("HALAMAN", i + 1);
                map2.put("PERIODE_AWAL", tanggal_mulai);
                map2.put("PERIODE_AKHIR", tanggal_selesai);
                map2.put("ID_PEGAWAI", Tabel_data.getValueAt(i, 1).toString());
                map2.put("NAMA_PEGAWAI", Tabel_data.getValueAt(i, 2).toString());
                map2.put("LEMBUR", Double.valueOf("0.0"));
                map2.put("TUNJANGAN_HADIR", Double.valueOf(Tabel_data.getValueAt(i, 14).toString()));
                map2.put("POT_TRANSPORT", Double.valueOf(Tabel_data.getValueAt(i, 15).toString()));
                map2.put("POT_BPJS", Double.valueOf(Tabel_data.getValueAt(i, 16).toString()));
                map2.put("POT_BPJS_TK", Double.valueOf(Tabel_data.getValueAt(i, 17).toString()));
                map2.put("BONUS_TBT", Double.valueOf(Tabel_data.getValueAt(i, 18).toString()));
                map2.put("GAJI_HARIAN", Double.valueOf("0.0"));
                map2.put("GAJI_BORONG", Double.valueOf(Tabel_data.getValueAt(i, 21).toString()));
                map2.put("BONUS_LP", Double.valueOf(Tabel_data.getValueAt(i, 22).toString()));
                map2.put("BONUS_MK_UTUH", Double.valueOf(Tabel_data.getValueAt(i, 23).toString()));
                map2.put("BONUS_PENCAPAIAN_PRODUKSI", Double.valueOf(Tabel_data.getValueAt(i, 24).toString()));
                map2.put("PIUTANG", Double.valueOf(Tabel_data.getValueAt(i, 25).toString()));
                map2.put("KETERANGAN", Tabel_data.getValueAt(i, 27).toString());
                map2.put("GRUP", Tabel_data.getValueAt(i, 28));
                JasperPrint JASP_PRINT2 = JasperFillManager.fillReport(JASP_REP2, map2, Utility.db.getConnection());
                List pages = JASP_PRINT2.getPages();
                for (int k = 0; k < pages.size(); k++) {
                    JRPrintPage object = (JRPrintPage) pages.get(k);
                    JASP_PRINT.addPage(object);
                }
            }
            JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
        } catch (JRException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_payrol_harian.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_slip_mandiri_cetakActionPerformed

    private void button_slip_harianActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_slip_harianActionPerformed
        // TODO add your handling code here:
        Date tanggal_mulai = new Date(Date_penggajian.getDate().getTime() - (7 * 24 * 60 * 60 * 1000));
        Date tanggal_selesai = new Date(Date_penggajian.getDate().getTime() - (1 * 24 * 60 * 60 * 1000));
        try {
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
            map.put("LEMBUR", Double.valueOf(Tabel_data.getValueAt(0, 11).toString()));
            map.put("POT_TERLAMBAT", Double.valueOf(Tabel_data.getValueAt(0, 12).toString()));
            map.put("POT_IJIN", Double.valueOf(Tabel_data.getValueAt(0, 13).toString()));
            map.put("TUNJANGAN_HADIR", Double.valueOf(Tabel_data.getValueAt(0, 14).toString()));
            map.put("POT_TRANSPORT", Double.valueOf(Tabel_data.getValueAt(0, 15).toString()));
            map.put("POT_BPJS", Double.valueOf(Tabel_data.getValueAt(0, 16).toString()));
            map.put("POT_BPJS_TK", Double.valueOf(Tabel_data.getValueAt(0, 17).toString()));
            map.put("BONUS_TBT", Double.valueOf(Tabel_data.getValueAt(0, 18).toString()));
            map.put("GAJI_HARIAN", Double.valueOf(Tabel_data.getValueAt(0, 20).toString()));
            map.put("GAJI_BORONG", Double.valueOf(Tabel_data.getValueAt(0, 21).toString()));
            map.put("BONUS_LP", Double.valueOf(Tabel_data.getValueAt(0, 22).toString()));
            map.put("BONUS_MK_UTUH", Double.valueOf(Tabel_data.getValueAt(0, 23).toString()));
            map.put("BONUS_PENCAPAIAN_PRODUKSI", Double.valueOf(Tabel_data.getValueAt(0, 24).toString()));
            map.put("PIUTANG", Double.valueOf(Tabel_data.getValueAt(0, 25).toString()));
            map.put("KETERANGAN", Tabel_data.getValueAt(0, 27).toString());
            map.put("GRUP", Tabel_data.getValueAt(0, 28));
            JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
            JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, map, Utility.db.getConnection());
            for (int i = 1; i < Tabel_data.getRowCount(); i++) {
                JasperDesign JASP_DESIGN2 = JRXmlLoader.load("Report\\Slip_Gaji_karyawan_Harian.jrxml");
                JasperReport JASP_REP2 = JasperCompileManager.compileReport(JASP_DESIGN2);
                Map<String, Object> map2 = new HashMap<>();
                map2.put("HALAMAN", i + 1);
                map2.put("PERIODE_AWAL", tanggal_mulai);
                map2.put("PERIODE_AKHIR", tanggal_selesai);
                map2.put("ID_PEGAWAI", Tabel_data.getValueAt(i, 1).toString());
                map2.put("NAMA_PEGAWAI", Tabel_data.getValueAt(i, 2).toString());
                map2.put("LEMBUR", Double.valueOf(Tabel_data.getValueAt(i, 11).toString()));
                map2.put("POT_TERLAMBAT", Double.valueOf(Tabel_data.getValueAt(i, 12).toString()));
                map2.put("POT_IJIN", Double.valueOf(Tabel_data.getValueAt(i, 13).toString()));
                map2.put("TUNJANGAN_HADIR", Double.valueOf(Tabel_data.getValueAt(i, 14).toString()));
                map2.put("POT_TRANSPORT", Double.valueOf(Tabel_data.getValueAt(i, 15).toString()));
                map2.put("POT_BPJS", Double.valueOf(Tabel_data.getValueAt(i, 16).toString()));
                map2.put("POT_BPJS_TK", Double.valueOf(Tabel_data.getValueAt(i, 17).toString()));
                map2.put("BONUS_TBT", Double.valueOf(Tabel_data.getValueAt(i, 18).toString()));
                map2.put("GAJI_HARIAN", Double.valueOf(Tabel_data.getValueAt(i, 20).toString()));
                map2.put("GAJI_BORONG", Double.valueOf(Tabel_data.getValueAt(i, 21).toString()));
                map2.put("BONUS_LP", Double.valueOf(Tabel_data.getValueAt(i, 22).toString()));
                map2.put("BONUS_MK_UTUH", Double.valueOf(Tabel_data.getValueAt(i, 23).toString()));
                map2.put("BONUS_PENCAPAIAN_PRODUKSI", Double.valueOf(Tabel_data.getValueAt(i, 24).toString()));
                map2.put("PIUTANG", Double.valueOf(Tabel_data.getValueAt(i, 25).toString()));
                map2.put("KETERANGAN", Tabel_data.getValueAt(i, 27).toString());
                map2.put("GRUP", Tabel_data.getValueAt(i, 28));
                JasperPrint JASP_PRINT2 = JasperFillManager.fillReport(JASP_REP2, map2, Utility.db.getConnection());
                List pages = JASP_PRINT2.getPages();
                for (int k = 0; k < pages.size(); k++) {
                    JRPrintPage object = (JRPrintPage) pages.get(k);
                    JASP_PRINT.addPage(object);
                }
            }
            JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
        } catch (JRException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_payrol_harian.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_slip_harianActionPerformed

    private void button_print_slip_per_grup2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_print_slip_per_grup2ActionPerformed
        // TODO add your handling code here:
        try {
            JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Slip_Gaji_Karyawan_per_Grup2.jrxml");
            JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
            Map<String, Object> params = new HashMap<>();
            params.put("tgl1", judulTgl1);
            params.put("tgl2", judulTgl2);
            params.put("tgl3", judulTgl3);
            params.put("tgl4", judulTgl4);
            params.put("tgl5", judulTgl5);
            params.put("tgl6", judulTgl6);
            params.put("tgl7", judulTgl7);
            /*params.entrySet().forEach((entry) -> {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        });*/
            JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, params, new JRBeanCollectionDataSource(listPayrolHarian));
            JasperViewer.viewReport(JASP_PRINT, false);
        } catch (JRException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_payrol_harian.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_print_slip_per_grup2ActionPerformed

    private void button_slip_cabutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_slip_cabutActionPerformed
        // TODO add your handling code here:

        Date tanggal_mulai = new Date(Date_penggajian.getDate().getTime() - (7 * 24 * 60 * 60 * 1000));
        Date tanggal_selesai = new Date(Date_penggajian.getDate().getTime() - (1 * 24 * 60 * 60 * 1000));
        try {
            JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\SlipRekapCabut2.jrxml");
            Map<String, Object> map = new HashMap<>();
            map.put("PERIODE_AWAL", tanggal_mulai);
            map.put("PERIODE_AKHIR", tanggal_selesai);
            map.put("SUBREPORT_DIR", "Report\\");
            JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
            JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, map, Utility.db.getConnection());
            JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
        } catch (JRException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_payrol_harian.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_slip_cabutActionPerformed

    private void button_input_gaji_borong_OMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_input_gaji_borong_OMActionPerformed
        // TODO add your handling code here:
        JDialog_DataGajiBorongOM_dariCSV dialog = new JDialog_DataGajiBorongOM_dariCSV(new javax.swing.JFrame(), true);
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(this);
        dialog.setEnabled(true);
        dialog.setVisible(true);
    }//GEN-LAST:event_button_input_gaji_borong_OMActionPerformed

    private void button_slip_borong_cetak2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_slip_borong_cetak2ActionPerformed
        // TODO add your handling code here:
        Date tanggal_mulai = new Date(Date_penggajian.getDate().getTime() - (7 * 24 * 60 * 60 * 1000));
        Date tanggal_selesai = new Date(Date_penggajian.getDate().getTime() - (1 * 24 * 60 * 60 * 1000));
        try {
            double gaji_harian = Double.valueOf(Tabel_data.getValueAt(0, 20).toString());
            double potongan_terlambat = Double.valueOf(Tabel_data.getValueAt(0, 12).toString());
            double potongan_ijin = Double.valueOf(Tabel_data.getValueAt(0, 13).toString());
            gaji_harian = gaji_harian - (potongan_terlambat + potongan_ijin);
            JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Slip_Gaji_karyawan_Cetak2.jrxml");
            Map<String, Object> map = new HashMap<>();
            map.put("HALAMAN", 1);
            map.put("PERIODE_AWAL", tanggal_mulai);
            map.put("PERIODE_AKHIR", tanggal_selesai);
            map.put("ID_PEGAWAI", Tabel_data.getValueAt(0, 1).toString());
            map.put("NAMA_PEGAWAI", Tabel_data.getValueAt(0, 2).toString());
            map.put("LEMBUR", Double.valueOf(Tabel_data.getValueAt(0, 11).toString()));
            map.put("TUNJANGAN_HADIR", Double.valueOf(Tabel_data.getValueAt(0, 14).toString()));
            map.put("POT_TRANSPORT", Double.valueOf(Tabel_data.getValueAt(0, 15).toString()));
            map.put("POT_BPJS", Double.valueOf(Tabel_data.getValueAt(0, 16).toString()));
            map.put("POT_BPJS_TK", Double.valueOf(Tabel_data.getValueAt(0, 17).toString()));
            map.put("BONUS_TBT", Double.valueOf(Tabel_data.getValueAt(0, 18).toString()));
            map.put("GAJI_HARIAN", gaji_harian);
            map.put("GAJI_BORONG", Double.valueOf(Tabel_data.getValueAt(0, 21).toString()));
            map.put("BONUS_LP", Double.valueOf(Tabel_data.getValueAt(0, 22).toString()));
            map.put("BONUS_MK_UTUH", Double.valueOf(Tabel_data.getValueAt(0, 23).toString()));
            map.put("BONUS_PENCAPAIAN_PRODUKSI", Double.valueOf(Tabel_data.getValueAt(0, 24).toString()));
            map.put("PIUTANG", Double.valueOf(Tabel_data.getValueAt(0, 25).toString()));
            map.put("KETERANGAN", Tabel_data.getValueAt(0, 27).toString());
            map.put("GRUP", Tabel_data.getValueAt(0, 28));
            JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
            JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, map, Utility.db.getConnection());
            for (int i = 1; i < Tabel_data.getRowCount(); i++) {
                double gaji_harian2 = Double.valueOf(Tabel_data.getValueAt(i, 20).toString());
                double potongan_terlambat2 = Double.valueOf(Tabel_data.getValueAt(i, 12).toString());
                double potongan_ijin2 = Double.valueOf(Tabel_data.getValueAt(i, 13).toString());
                gaji_harian2 = gaji_harian2 - (potongan_terlambat2 + potongan_ijin2);
                JasperDesign JASP_DESIGN2 = JRXmlLoader.load("Report\\Slip_Gaji_karyawan_Cetak2.jrxml");
                JasperReport JASP_REP2 = JasperCompileManager.compileReport(JASP_DESIGN2);
                Map<String, Object> map2 = new HashMap<>();
                map2.put("HALAMAN", i + 1);
                map2.put("PERIODE_AWAL", tanggal_mulai);
                map2.put("PERIODE_AKHIR", tanggal_selesai);
                map2.put("ID_PEGAWAI", Tabel_data.getValueAt(i, 1).toString());
                map2.put("NAMA_PEGAWAI", Tabel_data.getValueAt(i, 2).toString());
                map2.put("LEMBUR", Double.valueOf(Tabel_data.getValueAt(i, 11).toString()));
                map2.put("TUNJANGAN_HADIR", Double.valueOf(Tabel_data.getValueAt(i, 14).toString()));
                map2.put("POT_TRANSPORT", Double.valueOf(Tabel_data.getValueAt(i, 15).toString()));
                map2.put("POT_BPJS", Double.valueOf(Tabel_data.getValueAt(i, 16).toString()));
                map2.put("POT_BPJS_TK", Double.valueOf(Tabel_data.getValueAt(i, 17).toString()));
                map2.put("BONUS_TBT", Double.valueOf(Tabel_data.getValueAt(i, 18).toString()));
                map2.put("GAJI_HARIAN", gaji_harian2);
                map2.put("GAJI_BORONG", Double.valueOf(Tabel_data.getValueAt(i, 21).toString()));
                map2.put("BONUS_LP", Double.valueOf(Tabel_data.getValueAt(i, 22).toString()));
                map2.put("BONUS_MK_UTUH", Double.valueOf(Tabel_data.getValueAt(i, 23).toString()));
                map2.put("BONUS_PENCAPAIAN_PRODUKSI", Double.valueOf(Tabel_data.getValueAt(i, 24).toString()));
                map2.put("PIUTANG", Double.valueOf(Tabel_data.getValueAt(i, 25).toString()));
                map2.put("KETERANGAN", Tabel_data.getValueAt(i, 27).toString());
                map2.put("GRUP", Tabel_data.getValueAt(i, 28));
                JasperPrint JASP_PRINT2 = JasperFillManager.fillReport(JASP_REP2, map2, Utility.db.getConnection());
                List pages = JASP_PRINT2.getPages();
                for (int k = 0; k < pages.size(); k++) {
                    JRPrintPage object = (JRPrintPage) pages.get(k);
                    JASP_PRINT.addPage(object);
                }
            }
            JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
        } catch (JRException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_payrol_harian.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_slip_borong_cetak2ActionPerformed

    private void txt_search_level_gajiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_level_gajiKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_search_level_gajiKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox CheckBox_hide_Gaji_0;
    private com.toedter.calendar.JDateChooser Date_Masuk1;
    private com.toedter.calendar.JDateChooser Date_Masuk2;
    private com.toedter.calendar.JDateChooser Date_penggajian;
    private javax.swing.JTable Tabel_data;
    private javax.swing.JButton button_export;
    private javax.swing.JButton button_input_bonus_pencapaian;
    private javax.swing.JButton button_input_gaji_borong_OM;
    private javax.swing.JButton button_load;
    private javax.swing.JButton button_print_slip_per_grup;
    private javax.swing.JButton button_print_slip_per_grup2;
    private javax.swing.JButton button_save_data_fix;
    private javax.swing.JButton button_slip_borong_cabut;
    private javax.swing.JButton button_slip_borong_cetak2;
    private javax.swing.JButton button_slip_cabut;
    private javax.swing.JButton button_slip_harian;
    private javax.swing.JButton button_slip_mandiri_cetak;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTextArea jTextArea3;
    private javax.swing.JLabel label_total_bonus_lp;
    private javax.swing.JLabel label_total_gaji;
    private javax.swing.JLabel label_total_tunjangan_hadir;
    private javax.swing.JLabel label_total_upah_cabut;
    private javax.swing.JLabel label_total_upah_lembur;
    private javax.swing.JLabel label_total_upah_non_cabut;
    private javax.swing.JTextField txt_bonus_tbt;
    private javax.swing.JTextField txt_jumlah_karyawan_terlambat;
    private javax.swing.JTextField txt_potongan_bpjs;
    private javax.swing.JTextField txt_potongan_bpjs_tk;
    private javax.swing.JTextField txt_potongan_transport;
    private javax.swing.JTextField txt_search_bagian;
    private javax.swing.JTextField txt_search_karyawan;
    private javax.swing.JTextField txt_search_level_gaji;
    // End of variables declaration//GEN-END:variables
}
