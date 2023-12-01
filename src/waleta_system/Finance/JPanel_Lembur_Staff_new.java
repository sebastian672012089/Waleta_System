package waleta_system.Finance;

import waleta_system.Class.Utility;
import java.awt.event.KeyEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import waleta_system.BahanJadi.JPanel_BoxBahanJadi;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.ExportToExcel;
import waleta_system.MainForm;

public class JPanel_Lembur_Staff_new extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    PreparedStatement pst;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Date today = new Date();
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    String potensi_karyawan_kabur = "";
    ArrayList<String> id_pegawai_jam_kerja_kurang = new ArrayList<>();
    ArrayList<String> tanggal_jam_kerja_kurang = new ArrayList<>();

    public JPanel_Lembur_Staff_new() {
        initComponents();
    }

    public void init() {
        if (MainForm.Login_idPegawai.equals("20171201644")//indrika
                || MainForm.Login_idPegawai.equals("20230907768")//diyan
                || MainForm.Login_idPegawai.equals("20180102221")) {//sebastian
            button_input_jam_pulang.setEnabled(true);
            button_input_absen_manual.setEnabled(true);
            button_input_cuti_jam_kerja_kurang.setEnabled(true);
        }
        refreshTable_Lembur();
    }

    public void refreshTable_Lembur() {
        try {
            label_data_ijin_tidak_valid.setVisible(false);
            label_data_lembur_tidak_valid.setVisible(false);
            DefaultTableModel model = (DefaultTableModel) tabel_data_lembur.getModel();
            model.setRowCount(0);

            if (Date_Search_Lembur1.getDate() != null && Date_Search_Lembur2.getDate() != null) {
                sql = "SELECT UPPER(DAYNAME(DATE(`scan_date`))) AS 'Hari',DATE(`scan_date`) AS 'tanggal', `att_log`.`pin`, `tb_karyawan`.`id_pegawai`, `tb_karyawan`.`nama_pegawai`, `jenis_kelamin`, `tb_karyawan`.`posisi`, `tb_karyawan`.`status`,`tb_bagian`.`nama_bagian`, `tb_bagian`.`kode_departemen`, `tb_karyawan`.`jam_kerja`, `tb_karyawan`.`level_gaji`, `tb_karyawan`.`jalur_jemputan`, `tb_karyawan`.`potongan_bpjs`, `potongan_bpjs_tk`, "
                        + "`tb_level_gaji`.`upah_per_hari`, `lembur_per_jam`, `lembur_x_hari_kerja`, `lembur_x_hari_libur`, \n"
                        + "DATE_FORMAT(MIN(TIME(`scan_date`)), '%H:%i') AS 'Masuk', \n"
                        + "DATE_FORMAT(MAX(TIME(`scan_date`)), '%H:%i') AS 'Pulang', \n"
                        + "`tb_surat_lembur_detail`.`nomor_surat`, `jumlah_jam`, `mulai_lembur`\n"
                        + "FROM `att_log` \n"
                        + "LEFT JOIN `att_mesin_finger` ON `att_log`.`sn` = `att_mesin_finger`.`sn`\n"
                        + "LEFT JOIN `tb_karyawan` ON `att_log`.`pin` = `tb_karyawan`.`pin_finger`\n"
                        + "LEFT JOIN `tb_level_gaji` ON `tb_level_gaji`.`level_gaji` = `tb_karyawan`.`level_gaji`\n"
                        + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`"
                        + "LEFT JOIN `tb_surat_lembur_detail` ON `tb_surat_lembur_detail`.`id_pegawai` = `tb_karyawan`.`id_pegawai` AND DATE(`att_log`.`scan_date`) = `tb_surat_lembur_detail`.`tanggal_lembur`\n"
                        + "WHERE \n"
                        + "`pin` LIKE '%" + txt_search_pin1.getText() + "%' \n"
                        + "AND `tb_karyawan`.`nama_pegawai` LIKE '%" + txt_search_NamaKaryawan1.getText() + "%' \n"
                        + "AND `tb_bagian`.`nama_bagian` LIKE '%" + txt_search_bagian.getText() + "%' \n"
                        + "AND `posisi` LIKE 'STAFF%' \n"
                        + "AND `tb_bagian`.`nama_bagian` NOT LIKE '%-SECURITY-%' \n"
                        + "AND `tb_karyawan`.`jam_kerja` <> 'SHIFT_MALAM' \n"
                        + "AND `tb_karyawan`.`level_gaji` IS NOT NULL \n"
                        + "AND (DATE(`scan_date`) BETWEEN '" + dateFormat.format(Date_Search_Lembur1.getDate()) + "' AND '" + dateFormat.format(Date_Search_Lembur2.getDate()) + "') \n"
                        + "GROUP BY `att_log`.`pin`, DATE(`scan_date`) \n"
                        + "ORDER BY `scan_date` DESC, `tb_karyawan`.`nama_pegawai` ASC";
                pst = Utility.db.getConnection().prepareStatement(sql);
                rs = pst.executeQuery();
                Object[] row = new Object[40];
                while (rs.next()) {
                    String hari = rs.getString("Hari");
                    row[0] = hari;
                    row[1] = rs.getDate("tanggal");
                    row[2] = rs.getString("id_pegawai");
                    row[3] = rs.getString("nama_pegawai");
                    row[4] = rs.getString("kode_departemen");
                    row[5] = rs.getString("nama_bagian");
                    row[6] = rs.getString("posisi");
                    row[7] = rs.getString("status");
                    int premi_hadir = 0;
                    String keterangan_hari_libur = "";
                    String query_tgl_libur = "SELECT `tanggal_libur`, `keterangan` "
                            + "FROM `tb_libur`"
                            + "WHERE `tanggal_libur` = '" + rs.getDate("tanggal") + "' ";
                    ResultSet rs_tgl_libur = Utility.db.getStatement().executeQuery(query_tgl_libur);
                    if (rs_tgl_libur.next()) {
                        keterangan_hari_libur = rs_tgl_libur.getString("keterangan");
                    }
                    row[8] = keterangan_hari_libur;

//                    String query_pengampunan_terlambat = "SELECT * FROM `tb_pengampunan_terlambat`"
//                            + "WHERE `tanggal_pengampunan` = '" + rs.getDate("tanggal") + "' ";
//                    ResultSet rs_pengampunan_terlambat = Utility.db.getStatement().executeQuery(query_pengampunan_terlambat);
//                    boolean pengampunan_terlambat = rs_pengampunan_terlambat.next();
                    SimpleDateFormat Timeformat = new SimpleDateFormat("HH:mm:ss");
                    Date jam_masuk = null, jam_pulang = null;
                    long jam_kerja = 0, menit_istirahat_kerja = 0;
                    if (rs.getString("jam_kerja") != null && (keterangan_hari_libur.equals("") || keterangan_hari_libur.toLowerCase().contains("cuti bersama"))) {
                        int hari_ke = DayOfWeek.valueOf(rs.getString("Hari")).getValue();
                        String query_jam_kerja = "SELECT `masuk" + hari_ke + "`, `pulang" + hari_ke + "`, `istirahat" + hari_ke + "` FROM `tb_jam_kerja` "
                                + "WHERE `jam_kerja` = '" + rs.getString("jam_kerja") + "'";
                        ResultSet rs_jam_kerja = Utility.db.getStatement().executeQuery(query_jam_kerja);
                        if (rs_jam_kerja.next()) {
                            menit_istirahat_kerja = rs_jam_kerja.getInt("istirahat" + Integer.toString(hari_ke));
                            if (rs_jam_kerja.getString("masuk" + Integer.toString(hari_ke)) != null) {
                                row[9] = rs_jam_kerja.getString("masuk" + hari_ke);
                                row[10] = rs_jam_kerja.getString("pulang" + Integer.toString(hari_ke));
                                jam_masuk = Timeformat.parse(rs_jam_kerja.getString("masuk" + Integer.toString(hari_ke)));
                                jam_pulang = Timeformat.parse(rs_jam_kerja.getString("pulang" + Integer.toString(hari_ke)));
                                jam_kerja = (jam_pulang.getTime() - jam_masuk.getTime());
                            } else {
                                row[9] = null;
                                row[10] = null;
                            }
                        }
                    } else {
                        row[9] = null;
                        row[10] = null;
                    }

                    boolean is_ijin_pulang = false;
                    String query_ijin = "SELECT `jam_keluar`, `jam_kembali`, `keterangan`, `pengawas` "
                            + "FROM `tb_ijin_keluar` "
                            + "WHERE `id_pegawai` = '" + rs.getString("id_pegawai") + "' "
                            + "AND `tanggal_keluar` = '" + rs.getDate("tanggal") + "' ";
                    ResultSet rs_ijin = Utility.db.getStatement().executeQuery(query_ijin);
                    long ijin_keluar = 0, ijin_pulang = 0;
                    String jam_ijin_pulang = "";
                    if (rs_ijin.next()) {
                        if (rs_ijin.getString("jam_keluar") == null) {
                            label_data_ijin_tidak_valid.setVisible(true);
                        } else if (rs_ijin.getString("jam_kembali") == null) {
                            is_ijin_pulang = true;
                            jam_ijin_pulang = rs_ijin.getString("jam_keluar");
                            Date jam_keluar = Timeformat.parse(rs_ijin.getString("jam_keluar"));
                        } else {
                            Date jam_keluar = Timeformat.parse(rs_ijin.getString("jam_keluar"));
                            Date jam_kembali = Timeformat.parse(rs_ijin.getString("jam_kembali"));
                            ijin_keluar = ijin_keluar + (jam_kembali.getTime() - jam_keluar.getTime());
                        }
                    }

                    long durasi_kerja = 0, durasi_lembur = 0, total_menit_lembur = 0, total_menit_kerja = 0, minimal_durasi_kerja = 0, jam_kerja_full = 0;
                    String no_spl_masuk = null, text_jam_mulai_lembur_masuk = "", text_jam_selesai_lembur_masuk = "";
                    Date jam_mulai_lembur_masuk = null, jam_selesai_lembur_masuk = null;
                    String no_spl_pulang = null, text_jam_mulai_lembur_pulang = "", text_jam_selesai_lembur_pulang = "";
                    Date jam_mulai_lembur_pulang = null, jam_selesai_lembur_pulang = null;
                    int menit_istirahat_lembur_masuk = 0, menit_istirahat_lembur_pulang = 0, total_menit_istirahat_lembur = 0;
                    int jumlah_jam_spl_masuk = 0, jumlah_jam_spl_pulang = 0, total_jam_lembur_spl = 0, total_menit_lembur_spl = 0;
                    String query_spl = "SELECT `mulai_lembur`, `selesai_lembur`, `jumlah_jam`, `menit_istirahat_lembur`, `jenis_spl`, `jenis_lembur`, `tb_surat_lembur_detail`.`nomor_surat`, `tb_libur`.`keterangan`, `tb_surat_lembur`.`disetujui`, `tb_surat_lembur`.`diketahui` "
                            + "FROM `tb_surat_lembur_detail` "
                            + "LEFT JOIN `tb_surat_lembur` ON `tb_surat_lembur_detail`.`nomor_surat` = `tb_surat_lembur`.`nomor_surat`"
                            + "LEFT JOIN `tb_libur` ON `tb_surat_lembur_detail`.`tanggal_lembur` = `tb_libur`.`tanggal_libur`"
                            + "WHERE "
                            + "`id_pegawai` = '" + rs.getString("id_pegawai") + "' "
                            + "AND `jenis_spl` = 'STAFF' \n"
                            + "AND `tb_surat_lembur_detail`.`tanggal_lembur` = '" + dateFormat.format(rs.getDate("tanggal")) + "' ";
                    ResultSet rs_spl = Utility.db.getStatement().executeQuery(query_spl);
                    while (rs_spl.next()) {
                        if (rs_spl.getString("disetujui") == null || rs_spl.getString("diketahui") == null) {
                            label_data_lembur_tidak_valid.setVisible(true);
                        }
                        if (rs_spl.getString("jenis_lembur").equals("Masuk")) {
                            no_spl_masuk = rs_spl.getString("nomor_surat");
                            jam_mulai_lembur_masuk = Timeformat.parse(rs_spl.getString("mulai_lembur"));
                            jam_selesai_lembur_masuk = Timeformat.parse(rs_spl.getString("selesai_lembur"));
                            text_jam_mulai_lembur_masuk = rs_spl.getString("mulai_lembur");
                            text_jam_selesai_lembur_masuk = rs_spl.getString("selesai_lembur");
                            jumlah_jam_spl_masuk = rs_spl.getInt("jumlah_jam");
                            menit_istirahat_lembur_masuk = rs_spl.getInt("menit_istirahat_lembur");
                        }
                        if (rs_spl.getString("jenis_lembur").equals("Pulang")) {
                            no_spl_pulang = rs_spl.getString("nomor_surat");
                            jam_mulai_lembur_pulang = Timeformat.parse(rs_spl.getString("mulai_lembur"));
                            jam_selesai_lembur_pulang = Timeformat.parse(rs_spl.getString("selesai_lembur"));
                            text_jam_mulai_lembur_pulang = rs_spl.getString("mulai_lembur");
                            text_jam_selesai_lembur_pulang = rs_spl.getString("selesai_lembur");
                            jumlah_jam_spl_pulang = rs_spl.getInt("jumlah_jam");
                            menit_istirahat_lembur_pulang = rs_spl.getInt("menit_istirahat_lembur");
                        }
                        total_jam_lembur_spl = jumlah_jam_spl_masuk + jumlah_jam_spl_pulang;
                        total_menit_lembur_spl = total_jam_lembur_spl * 60;
                        total_menit_istirahat_lembur = menit_istirahat_lembur_masuk + menit_istirahat_lembur_pulang;
                    }

                    long menit_terlambat = 0;
//                    Date absen_masuk = Timeformat.parse(rs.getString("Masuk"));
//                    Date absen_pulang = Timeformat.parse(rs.getString("Pulang"));
                    Date absen_masuk = new SimpleDateFormat("HH:mm").parse(rs.getString("Masuk"));
                    Date absen_pulang = new SimpleDateFormat("HH:mm").parse(rs.getString("Pulang"));
                    boolean tidak_absen_masuk = false, tidak_absen_pulang = false;
                    if (jam_masuk != null) {//hari kerja
                        if (absen_masuk.after(new Date(jam_masuk.getTime() + (4 * 60 * 60 * 1000)))) {
                            absen_masuk = jam_masuk;
                            tidak_absen_masuk = true;
                        } else if (absen_masuk.after(new Date(jam_masuk.getTime() + (2 * 60 * 1000)))) { //toleransi 1 menit
                            menit_terlambat = (absen_masuk.getTime() - jam_masuk.getTime()) / (60 * 1000);
                        } else if (jam_mulai_lembur_masuk != null) {
                            if (absen_masuk.before(jam_mulai_lembur_masuk)) {
                                absen_masuk = jam_mulai_lembur_masuk;
                            }
                        } else {
                            absen_masuk = jam_masuk;
                        }

                        if (jam_pulang != null) {
                            if (!is_ijin_pulang && absen_pulang.before(new Date(jam_pulang.getTime() - (4 * 60 * 60 * 1000)))) {
                                absen_pulang = jam_pulang;
                                tidak_absen_pulang = true;
                            } else if (absen_pulang.before(jam_pulang)) {
                                ijin_pulang = jam_pulang.getTime() - absen_pulang.getTime();
                            } else if (absen_pulang.after(jam_pulang)) {
//                                if (jam_selesai_lembur_pulang != null) {// ada SPL pulang
//                                    if (absen_pulang.after(jam_selesai_lembur_pulang)) {
//                                        absen_pulang = jam_selesai_lembur_pulang;
//                                    }
//                                } else {
//                                    absen_pulang = jam_pulang;
//                                }

                                //absen_pulang = new Date((long) Math.floor(absen_pulang.getTime() / (1 * 60 * 1000)) * (1 * 60 * 1000));
                                //pembulatan kebawah jam pulang ke kelipatan 1 menit
                            }
                        }

                        durasi_kerja = (absen_pulang.getTime() - absen_masuk.getTime()) - (ijin_keluar);
                        total_menit_kerja = (durasi_kerja / (60 * 1000));
                        durasi_lembur = (absen_pulang.getTime() - absen_masuk.getTime()) - (jam_kerja + ijin_keluar);
                        total_menit_lembur = (durasi_lembur / (60 * 1000)); //total lembur dalam menit

//                        jam_kerja_full = (jam_kerja / (60 * 1000)) - menit_istirahat;//jam kerja full dalam menit
                        jam_kerja_full = (jam_kerja / (60 * 1000));//jam kerja full dalam menit
                        if (jam_kerja_full <= 360) {//jam kerja di bawah 6 jam
                            minimal_durasi_kerja = 285;//min 4 jam 45 menit
                        } else {
                            minimal_durasi_kerja = 360;//minimal 6 jam
                        }
                    } else if (no_spl_masuk != null && jam_mulai_lembur_masuk != null) {// hari libur dengan spl masuk
//                        if (absen_masuk.before(jam_mulai_lembur_masuk)) {
//                            absen_masuk = jam_mulai_lembur_masuk;
//                        }
//                        if (absen_pulang.after(jam_selesai_lembur_masuk)) {
//                            absen_pulang = jam_selesai_lembur_masuk;
//                        }
                        durasi_kerja = (absen_pulang.getTime() - absen_masuk.getTime()) - (ijin_keluar);
                        total_menit_kerja = (durasi_kerja / (60 * 1000));
                        durasi_lembur = (absen_pulang.getTime() - absen_masuk.getTime()) - (ijin_keluar);
                        total_menit_lembur = (durasi_lembur / (60 * 1000)); //total lembur dalam menit
                    } else {//hari libur tanpa spl

                    }

                    String query_pengurangan_lembur = "SELECT `pengurangan` FROM `tb_lembur_pengurangan` "
                            + "WHERE `id_pegawai` = '" + rs.getString("id_pegawai") + "' AND `tgl_lembur` = '" + rs.getDate("tanggal") + "'";
                    ResultSet rs_pengurangan_lembur = Utility.db.getStatement().executeQuery(query_pengurangan_lembur);
                    if (rs_pengurangan_lembur.next()) {
                        total_menit_lembur = total_menit_lembur - rs_pengurangan_lembur.getInt("pengurangan");
                    }
                    if (total_menit_lembur > total_menit_lembur_spl) {
                        total_menit_lembur = total_menit_lembur_spl;
                    }

                    if (total_menit_kerja < minimal_durasi_kerja) {//jam kerja kurang dari 6 jam
                        premi_hadir = 0;
                    } else if (total_menit_kerja >= minimal_durasi_kerja && total_menit_kerja < (jam_kerja_full - 15)) {//jam kerja tidak full, antara 6 - 8.45 jam
                        premi_hadir = -1;
                    } else if (tidak_absen_masuk) {
                        premi_hadir = 1;
                    } else if (tidak_absen_pulang) {
                        premi_hadir = 2;
                    } else if (jam_kerja > 0) {
                        premi_hadir = 3;
                    } else if (total_menit_lembur >= 240) {
                        premi_hadir = 4;//lembur di hari libur, dapat premi
                    } else {
                        premi_hadir = 5;//Lembur di hari libur, tapi kurang dari 4 jam, tidak dapat premi
                    }

                    if (keterangan_hari_libur.toLowerCase().contains("cuti bersama")) {
                        if (total_menit_kerja < minimal_durasi_kerja) {
                            premi_hadir = 6;
                        } else if (tidak_absen_masuk) {
                            premi_hadir = 7;
                        } else if (tidak_absen_pulang) {
                            premi_hadir = 8;
                        } else if (jam_kerja > 0) {
                            premi_hadir = 9;
                        }
                    }

                    row[11] = rs.getString("Masuk");//absen scan finger masuk
                    row[12] = Timeformat.format(absen_masuk);//absen scan finger masuk
                    row[13] = rs.getString("Pulang");//absen scan finger pulang
                    if (absen_pulang != null) {
                        row[14] = Timeformat.format(absen_pulang);//absen scan finger pulang
                    } else {
                        row[14] = null;
                    }
                    row[15] = jam_ijin_pulang;
                    row[16] = no_spl_masuk;
                    row[17] = text_jam_mulai_lembur_masuk;
                    row[18] = text_jam_selesai_lembur_masuk;
                    row[19] = menit_istirahat_lembur_masuk;
                    row[20] = jumlah_jam_spl_masuk;
                    row[21] = no_spl_pulang;
                    row[22] = text_jam_mulai_lembur_pulang;
                    row[23] = text_jam_selesai_lembur_pulang;
                    row[24] = menit_istirahat_lembur_pulang;
                    row[25] = jumlah_jam_spl_pulang;

                    String level_gaji = rs.getString("level_gaji");

                    long menit_ijin_keluar = ijin_keluar / (60 * 1000);
                    long menit_ijin_pulang = ijin_pulang / (60 * 1000);

                    if (rs.getString("nama_bagian").contains("SECURITY")) {
                        if (total_menit_lembur >= 480) {//lembur lebih dari 8 jam
                            total_menit_lembur = total_menit_lembur - 60;//dikurangi istirahat 60 menit
                            total_menit_lembur = (int) Math.floor(total_menit_lembur / 30) * 30;
                        } else if (total_menit_lembur > 120) {//kelipatan 30 menit di hitung setelah jam ke 2
                            total_menit_lembur = (int) Math.floor(total_menit_lembur / 30) * 30;
                        } else if (total_menit_lembur > 0) {
                            total_menit_lembur = (int) Math.floor(total_menit_lembur / 60) * 60;
                        } else {
//                            total_menit_lembur = 0;
                        }
                    } else { //selain bagian security
                        if (total_menit_lembur > 0) {
                            total_menit_lembur = (int) Math.floor(total_menit_lembur / 60) * 60;//pembulatan kelipatan 1 jam / 60 menit
                        } else {
//                            total_menit_lembur = 0;
                        }
//                        if (total_menit_lembur >= 510) {//lembur lebih dari 8.5 jam
//                            total_menit_lembur = total_menit_lembur - 60;
//                            total_menit_lembur = (int) Math.floor(total_menit_lembur / 60) * 60;
//                        } else if (total_menit_lembur >= 210) {//lembur lebih dari 3.5 jam
//                            total_menit_lembur = total_menit_lembur - 30;
//                            total_menit_lembur = (int) Math.floor(total_menit_lembur / 60) * 60;
//                        } else if (total_menit_lembur > 120) {//kelipatan 30 menit di hitung setelah jam ke 2
//                            total_menit_lembur = (int) Math.floor(total_menit_lembur / 60) * 60;
//                        } else if (total_menit_lembur > 0) {
//                            total_menit_lembur = (int) Math.floor(total_menit_lembur / 60) * 60;
//                        } else {
//                            total_menit_lembur = 0;
//                        }
                    }

                    row[26] = menit_istirahat_kerja;
                    row[27] = total_menit_kerja;
                    row[28] = Math.round(menit_terlambat);
                    row[29] = Math.round(menit_ijin_keluar + menit_ijin_pulang);// menit ijin pulang / keluar
                    row[30] = total_menit_lembur;
                    row[31] = total_menit_lembur / 60f;
                    row[32] = level_gaji;
                    row[33] = rs.getString("jam_kerja");
                    row[34] = premi_hadir;
                    String keterangan_premi = "";
                    switch (premi_hadir) {
                        case -1:
                            keterangan_premi = "jam kerja tidak full";
                            break;
                        case 0:
                            keterangan_premi = "Jam kerja kurang";
                            id_pegawai_jam_kerja_kurang.add(rs.getString("id_pegawai"));
                            tanggal_jam_kerja_kurang.add(rs.getString("tanggal"));
                            break;
                        case 1:
                            keterangan_premi = "tidak absen masuk";
                            break;
                        case 2:
                            keterangan_premi = "tidak absen pulang";
                            break;
                        case 3:
                            keterangan_premi = "dapat premi";
                            break;
                        case 4:
                            keterangan_premi = "hari libur, lembur >= 4jam, dapat premi";
                            break;
                        case 5:
                            keterangan_premi = "hari libur tidak dapat premi";
                            break;
                        case 6:
                            keterangan_premi = "cuti bersama, jam kerja kurang";
                            break;
                        case 7:
                            keterangan_premi = "cuti bersama, tidak absen masuk";
                            break;
                        case 8:
                            keterangan_premi = "cuti bersama, tidak absen pulang";
                            break;
                        case 9:
                            keterangan_premi = "cuti bersama, masuk full, dapat premi";
                            break;
                        default:
                            break;
                    }
                    row[35] = keterangan_premi;
                    model.addRow(row);
                }
            }

            ColumnsAutoSizer.sizeColumnsToFit(tabel_data_lembur);
            label_total_data.setText(Integer.toString(tabel_data_lembur.getRowCount()));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_BoxBahanJadi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void rekap_jam_kerja_kurang() {
        try {
            int dialogResult = JOptionPane.showConfirmDialog(this, id_pegawai_jam_kerja_kurang.size() + " karyawan kurang jam kerja, input ke data cuti?", "Warning", 0);
            if (dialogResult == JOptionPane.YES_OPTION) {
                int count = 0;
                for (int i = 0; i < id_pegawai_jam_kerja_kurang.size(); i++) {
                    String Query = "INSERT INTO `tb_cuti`(`id_pegawai`, `tanggal_cuti`, `jenis_cuti`, `kategori_cuti`, `keterangan`) "
                            + "SELECT * FROM (SELECT '" + id_pegawai_jam_kerja_kurang.get(i) + "','" + tanggal_jam_kerja_kurang.get(i) + "', 'Absen', 'Jam Kerja Kurang','-' AS 'keterangan') AS tmp\n"
                            + "WHERE NOT EXISTS (SELECT `kode_cuti` FROM `tb_cuti` WHERE `id_pegawai` = '" + id_pegawai_jam_kerja_kurang.get(i) + "' AND `tanggal_cuti` = '" + tanggal_jam_kerja_kurang.get(i) + "')";
                    Utility.db.getConnection().createStatement();
                    if (Utility.db.getStatement().executeUpdate(Query) == 1) {
                        count++;
                    }
                }
                JOptionPane.showMessageDialog(this, count + " data berhasil di input ke data cuti/absen !");
            }

//        JDialog_Input_JamKerjaKurang dialog = new JDialog_Input_JamKerjaKurang(new javax.swing.JFrame(), true, tanggal_mulai, tanggal_selesai);
//        dialog.setResizable(false);
//        dialog.setLocationRelativeTo(this);
//        dialog.setEnabled(true);
//        dialog.setVisible(true);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_payrol_harian.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        txt_search_NamaKaryawan1 = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txt_search_pin1 = new javax.swing.JTextField();
        jLabel36 = new javax.swing.JLabel();
        Date_Search_Lembur1 = new com.toedter.calendar.JDateChooser();
        Date_Search_Lembur2 = new com.toedter.calendar.JDateChooser();
        button_refresh1 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabel_data_lembur = new javax.swing.JTable();
        jLabel14 = new javax.swing.JLabel();
        label_total_data = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        button_export_lembur = new javax.swing.JButton();
        jLabel24 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        label_data_ijin_tidak_valid = new javax.swing.JLabel();
        button_input_jam_pulang = new javax.swing.JButton();
        label_data_lembur_tidak_valid = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        button_input_absen_manual = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jLabel25 = new javax.swing.JLabel();
        txt_search_bagian = new javax.swing.JTextField();
        button_input_cuti_jam_kerja_kurang = new javax.swing.JButton();

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("Nama Karyawan :");

        txt_search_NamaKaryawan1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_NamaKaryawan1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_NamaKaryawan1KeyPressed(evt);
            }
        });

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel9.setText("PIN :");

        txt_search_pin1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_pin1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_pin1KeyPressed(evt);
            }
        });

        jLabel36.setBackground(new java.awt.Color(255, 255, 255));
        jLabel36.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        jLabel36.setText("Tanggal Absen :");

        Date_Search_Lembur1.setBackground(new java.awt.Color(255, 255, 255));
        Date_Search_Lembur1.setToolTipText("");
        Date_Search_Lembur1.setDate(new Date());
        Date_Search_Lembur1.setDateFormatString("dd MMM yyyy");
        Date_Search_Lembur1.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        Date_Search_Lembur1.setMinSelectableDate(new java.util.Date(1483207315000L));

        Date_Search_Lembur2.setBackground(new java.awt.Color(255, 255, 255));
        Date_Search_Lembur2.setDate(new Date());
        Date_Search_Lembur2.setDateFormatString("dd MMM yyyy");
        Date_Search_Lembur2.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N

        button_refresh1.setBackground(new java.awt.Color(255, 255, 255));
        button_refresh1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_refresh1.setText("Refresh");
        button_refresh1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refresh1ActionPerformed(evt);
            }
        });

        tabel_data_lembur.setAutoCreateRowSorter(true);
        tabel_data_lembur.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tabel_data_lembur.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Hari", "Tanggal", "ID", "Nama", "Departemen", "Bagian", "Posisi", "Status", "Ket. Libur", "Jam Masuk", "Jam Pulang", "Absen Masuk", "Absen Masuk'", "Absen Pulang", "Absen Pulang'", "Ijin Pulang", "SPL Masuk", "Mulai Lembur Masuk", "Selesai Lembur Masuk", "Istirahat Lembur Masuk", "Jam SPL Masuk", "SPL Pulang", "Mulai Lembur Pulang", "Selesai Lembur Pulang", "Istirahat Lembur Pulang", "Jam SPL Pulang", "Menit Istirahat Kerja", "Menit Kerja", "Menit Terlambat", "Menit Ijin", "Menit Lembur", "Jam Lembur", "Level Gaji", "Jam Kerja", "Premi", "Keterangan"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Float.class, java.lang.Float.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabel_data_lembur.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(tabel_data_lembur);

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setText("Total Data :");

        label_total_data.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data.setText("0");

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel7.setText("Bagian :");

        button_export_lembur.setBackground(new java.awt.Color(255, 255, 255));
        button_export_lembur.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_lembur.setText("Export");
        button_export_lembur.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_lemburActionPerformed(evt);
            }
        });

        jLabel24.setBackground(new java.awt.Color(255, 255, 255));
        jLabel24.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel24.setText("Posisi :");

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel5.setText("Perhitungan Lembur Staff");

        label_data_ijin_tidak_valid.setBackground(new java.awt.Color(255, 255, 255));
        label_data_ijin_tidak_valid.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_data_ijin_tidak_valid.setForeground(new java.awt.Color(255, 0, 0));
        label_data_ijin_tidak_valid.setText("*Ada data IJIN yang tidak valid");

        button_input_jam_pulang.setBackground(new java.awt.Color(255, 255, 255));
        button_input_jam_pulang.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_input_jam_pulang.setText("Input Jam Pulang Manual");
        button_input_jam_pulang.setEnabled(false);
        button_input_jam_pulang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_input_jam_pulangActionPerformed(evt);
            }
        });

        label_data_lembur_tidak_valid.setBackground(new java.awt.Color(255, 255, 255));
        label_data_lembur_tidak_valid.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_data_lembur_tidak_valid.setForeground(new java.awt.Color(255, 0, 0));
        label_data_lembur_tidak_valid.setText("*Ada data LEMBUR yang tidak valid");

        jScrollPane1.setBackground(new java.awt.Color(255, 255, 255));

        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        jTextArea1.setRows(5);
        jTextArea1.setText("Notes : \n0. Bagian, Posisi, Jam Kerja, Level gaji tidak boleh kosong, jam kerja selain \"SHIFT MALAM\".\n1. Absen terakhir (absen pulang) 4 jam sebelum jam pulang kerja = tidak absen pulang. absen awal (absen masuk) 4jam setelah jam masuk kerja = tidak absen masuk. (Lemburan hilang, berpotensi kurang jam kerja)\n2. Jika ada ijin pulang, maka absen terakhir = absen pulang.\n3. Lembur pagi akan terhitung jika ada surat perintah lembur, jika tidak ada SPL, maka absen masuk akan terhitung dari jam masuk kerja seharusnya. Dan SPL harus sudah disetujui dan diketahui.\n4. Lembur dihari libur akan terhitung jika ada absen masuk, absen pulang dan surat perintah lembur (sudah disetujui dan diketahui), jika syarat tsb tidak terpenuhi, maka lembur tidak akan di hitung. \n5. lembur dibulatkan ke bawah, kelipatan 60 menit. (lembur 59 menit 59 detik tidak terhitung).\n6. Khusus bagian yg mengandung \"SECURITY\", diatas (>=) 8 jam akan dikurangi 1 jam istirahat.\n7. - Jika jumlah jam kerja > 6 jam, maka minimal jam kerja adalah 6 jam. (termasuk istirahat)\n    - Jika jumlah jam kerja <= 6 jam, maka minimal jam kerja adalah 4 jam 45 menit. (termasuk istirahat jika ada)\n    - Jam kerja full = total jam kerja - 15 menit.\n8. Kerja kurang dari (<) minimal jam kerja akan terhitung 'kurang jam kerja'. Kerja >= minimal jam kerja dan < jam kerja full, maka terhitung 'Kerja tidak Full' / Ijin.\n9. Tidak ada pengampunan terlambat. Fitur pengampunan terlambat hanya untuk PEJUANG.");
        jScrollPane1.setViewportView(jTextArea1);

        button_input_absen_manual.setBackground(new java.awt.Color(255, 255, 255));
        button_input_absen_manual.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_input_absen_manual.setText("Input Absen Manual");
        button_input_absen_manual.setEnabled(false);
        button_input_absen_manual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_input_absen_manualActionPerformed(evt);
            }
        });

        jScrollPane3.setBackground(new java.awt.Color(255, 255, 255));

        jTextArea2.setColumns(20);
        jTextArea2.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        jTextArea2.setRows(5);
        jTextArea2.setText("Kode Premi :\n-1 : Jam Kerja Kurang (Ijin)\n0 : Jam Kerja Kurang (Cuti)\n1 : tidak absen masuk\n2 : tidak absen pulang\n3 : dapat premi\n4 : hari libur, lembur > 4jam, dapat premi\n5 : hari libur tidak dapat premi\n6 : cuti bersama, jam kerja kurang\n7 : cuti bersama, tidak absen masuk\n8 : cuti bersama, tidak absen pulang\n9 : cuti bersama, masuk full, dapat premi");
        jScrollPane3.setViewportView(jTextArea2);

        jLabel25.setBackground(new java.awt.Color(255, 255, 255));
        jLabel25.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel25.setText("STAFF 5 & 6");

        txt_search_bagian.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_bagian.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_bagianKeyPressed(evt);
            }
        });

        button_input_cuti_jam_kerja_kurang.setBackground(new java.awt.Color(255, 255, 255));
        button_input_cuti_jam_kerja_kurang.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_input_cuti_jam_kerja_kurang.setText("Input Cuti Jam kerja Kurang");
        button_input_cuti_jam_kerja_kurang.setEnabled(false);
        button_input_cuti_jam_kerja_kurang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_input_cuti_jam_kerja_kurangActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1083, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 257, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_data_ijin_tidak_valid)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_data_lembur_tidak_valid))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(button_export_lembur)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_input_jam_pulang)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_input_absen_manual)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_input_cuti_jam_kerja_kurang))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_NamaKaryawan1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_pin1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel36)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_Search_Lembur1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_Search_Lembur2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_bagian, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel24)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel25)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_refresh1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_data)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_data_ijin_tidak_valid, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_data_lembur_tidak_valid, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_total_data, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_refresh1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_search_bagian, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(Date_Search_Lembur1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_Search_Lembur2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_pin1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_NamaKaryawan1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_input_jam_pulang, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export_lembur, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_input_absen_manual, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_input_cuti_jam_kerja_kurang, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 413, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addComponent(jScrollPane3)))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txt_search_NamaKaryawan1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_NamaKaryawan1KeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_Lembur();
        }
    }//GEN-LAST:event_txt_search_NamaKaryawan1KeyPressed

    private void txt_search_pin1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_pin1KeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_Lembur();
        }
    }//GEN-LAST:event_txt_search_pin1KeyPressed

    private void button_refresh1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refresh1ActionPerformed
        // TODO add your handling code here:
        refreshTable_Lembur();
    }//GEN-LAST:event_button_refresh1ActionPerformed

    private void button_export_lemburActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_lemburActionPerformed
        // TODO add your handling code here:
        DefaultTableModel table = (DefaultTableModel) tabel_data_lembur.getModel();
        ExportToExcel.writeToExcel(table, this);
    }//GEN-LAST:event_button_export_lemburActionPerformed

    private void button_input_jam_pulangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_input_jam_pulangActionPerformed
        // TODO add your handling code here:
        int i = tabel_data_lembur.getSelectedRow();
        if (i < 0) {
            JOptionPane.showMessageDialog(this, "Silahkan Klik data yang akan diinput jam pulang nya");
        } else {
            int dialogResult = JOptionPane.showConfirmDialog(this, "Adjustment untuk jam pulang " + tabel_data_lembur.getValueAt(i, 3).toString() + "?", "Warning", 0);
            if (dialogResult == JOptionPane.YES_OPTION) {
                String id = tabel_data_lembur.getValueAt(i, 2).toString();
                String nama = tabel_data_lembur.getValueAt(i, 3).toString();
                String tanggal = tabel_data_lembur.getValueAt(i, 1).toString();
                String scan_terakhir = tabel_data_lembur.getValueAt(i, 11) == null? "Tidak ada absen masuk!" : tabel_data_lembur.getValueAt(i, 11).toString();
                JDialog_adjustment_absen_pulang dialog = new JDialog_adjustment_absen_pulang(new javax.swing.JFrame(), true, id, nama, tanggal, scan_terakhir);
                dialog.pack();
                dialog.setLocationRelativeTo(this);
                dialog.setVisible(true);
                dialog.setEnabled(true);
                refreshTable_Lembur();
            }
        }
    }//GEN-LAST:event_button_input_jam_pulangActionPerformed

    private void button_input_absen_manualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_input_absen_manualActionPerformed
        // TODO add your handling code here:
        JDialog_InputAbsenManual dialog = new JDialog_InputAbsenManual(new javax.swing.JFrame(), true);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);
        refreshTable_Lembur();
    }//GEN-LAST:event_button_input_absen_manualActionPerformed

    private void txt_search_bagianKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_bagianKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_Lembur();
        }
    }//GEN-LAST:event_txt_search_bagianKeyPressed

    private void button_input_cuti_jam_kerja_kurangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_input_cuti_jam_kerja_kurangActionPerformed
        // TODO add your handling code here:
        rekap_jam_kerja_kurang();
    }//GEN-LAST:event_button_input_cuti_jam_kerja_kurangActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser Date_Search_Lembur1;
    private com.toedter.calendar.JDateChooser Date_Search_Lembur2;
    private javax.swing.JButton button_export_lembur;
    private javax.swing.JButton button_input_absen_manual;
    private javax.swing.JButton button_input_cuti_jam_kerja_kurang;
    private javax.swing.JButton button_input_jam_pulang;
    private javax.swing.JButton button_refresh1;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JLabel label_data_ijin_tidak_valid;
    private javax.swing.JLabel label_data_lembur_tidak_valid;
    private javax.swing.JLabel label_total_data;
    private javax.swing.JTable tabel_data_lembur;
    private javax.swing.JTextField txt_search_NamaKaryawan1;
    private javax.swing.JTextField txt_search_bagian;
    private javax.swing.JTextField txt_search_pin1;
    // End of variables declaration//GEN-END:variables
}
