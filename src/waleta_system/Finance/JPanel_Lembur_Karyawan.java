package waleta_system.Finance;

import waleta_system.Class.Utility;
import java.awt.event.KeyEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
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

public class JPanel_Lembur_Karyawan extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    PreparedStatement pst;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Date today = new Date();
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    String potensi_karyawan_kabur = "";

    public JPanel_Lembur_Karyawan() {
        initComponents();
    }

    public void init() {
        if (MainForm.Login_idPegawai.equals("20171201644")//indrika
                || MainForm.Login_idPegawai.equals("20230907768")//diyan
                || MainForm.Login_idPegawai.equals("20180102221")) {//sebastian
            button_save_data.setEnabled(true);
            button_pengampunan_terlambat.setEnabled(true);
            button_input_jam_pulang.setEnabled(true);
            button_input_absen_manual.setEnabled(true);
            button_input_pengurang_lembur.setEnabled(true);
        }
        refreshTable_Lembur();
    }

    public void refreshTable_Lembur() {
        try {
            button_berpotensi_kabur.setEnabled(false);
            label_data_ijin_tidak_valid.setVisible(false);
            label_data_lembur_tidak_valid.setVisible(false);
            DefaultTableModel model = (DefaultTableModel) tabel_data_lembur.getModel();
            model.setRowCount(0);
            if (Date_Search_Lembur1.getDate() != null && Date_Search_Lembur2.getDate() != null) {
                sql = "SELECT UPPER(DAYNAME(DATE(`scan_date`))) AS 'Hari',DATE(`scan_date`) AS 'tanggal', `att_log`.`pin`, `tb_karyawan`.`id_pegawai`, `tb_karyawan`.`nama_pegawai`, `jenis_kelamin`, `tb_karyawan`.`posisi`, `tb_karyawan`.`status`,`tb_bagian`.`nama_bagian`, `tb_bagian`.`kode_departemen`, `tb_karyawan`.`jam_kerja`, `tb_karyawan`.`level_gaji`, `tb_karyawan`.`jalur_jemputan`, `tb_karyawan`.`potongan_bpjs`, `potongan_bpjs_tk`, "
                        + "`tb_level_gaji`.`upah_per_hari`, `lembur_per_jam`, `lembur_x_hari_kerja`, `lembur_x_hari_libur`, "
                        + "CONCAT(IFNULL(`divisi_bagian`,''), '-', IFNULL(`bagian_bagian`,''), '-', IFNULL(`ruang_bagian`,'')) AS 'kode_grup', "
                        + "MIN(TIME(`scan_date`)) AS 'Masuk', "
                        + "MAX(TIME(`scan_date`)) AS 'Pulang', "
                        + "`tb_surat_lembur_detail`.`nomor_surat`, `jumlah_jam`, `mulai_lembur`\n"
                        + "FROM `att_log` "
                        + "LEFT JOIN `att_mesin_finger` ON `att_log`.`sn` = `att_mesin_finger`.`sn`\n"
                        + "LEFT JOIN `tb_karyawan` ON `att_log`.`pin` = `tb_karyawan`.`pin_finger`\n"
                        //                        + "LEFT JOIN `tb_grup_cabut` ON `tb_karyawan`.`id_pegawai` = `tb_grup_cabut`.`id_pegawai`\n"
                        + "LEFT JOIN `tb_level_gaji` ON `tb_level_gaji`.`level_gaji` = `tb_karyawan`.`level_gaji`\n"
                        + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`\n"
                        + "LEFT JOIN `tb_surat_lembur_detail` ON `tb_surat_lembur_detail`.`id_pegawai` = `tb_karyawan`.`id_pegawai` AND DATE(`att_log`.`scan_date`) = `tb_surat_lembur_detail`.`tanggal_lembur`\n"
                        + "WHERE "
                        + "`pin` LIKE '%" + txt_search_pin1.getText() + "%' "
                        + "AND `tb_karyawan`.`kode_bagian` IS NOT NULL "
                        + "AND `tb_karyawan`.`nama_pegawai` LIKE '%" + txt_search_NamaKaryawan1.getText() + "%' "
                        + "AND `tb_bagian`.`nama_bagian` LIKE '%" + txt_search_bagian.getText() + "%' \n"
                        + "AND `posisi` = 'PEJUANG' \n"
                        + "AND `tb_karyawan`.`jam_kerja` <> 'SHIFT_MALAM' "
                        + "AND `tb_karyawan`.`level_gaji` IS NOT NULL "
                        + "AND (DATE(`scan_date`) BETWEEN '" + dateFormat.format(Date_Search_Lembur1.getDate()) + "' AND '" + dateFormat.format(Date_Search_Lembur2.getDate()) + "')"
                        + " GROUP BY `att_log`.`pin`, DATE(`scan_date`) "
                        + "ORDER BY `scan_date` DESC, `tb_karyawan`.`nama_pegawai` ASC";
                pst = Utility.db.getConnection().prepareStatement(sql);
                rs = pst.executeQuery();
                while (rs.next()) {
                    Object[] row = new Object[35];
                    String jenis_kelamin = rs.getString("jenis_kelamin");
                    String hari = rs.getString("Hari");
                    row[0] = hari;
                    row[1] = rs.getDate("tanggal");
                    row[2] = rs.getString("id_pegawai");
                    row[3] = rs.getString("nama_pegawai");
                    row[4] = rs.getString("kode_departemen");
                    row[5] = rs.getString("nama_bagian");
                    row[6] = rs.getString("posisi");
                    row[7] = rs.getString("status");
                    int premi_hadir = 0, gaji_borong_cabut = 0;
                    String keterangan_hari_libur = "";
                    String query_tgl_libur = "SELECT `tanggal_libur`, `keterangan` "
                            + "FROM `tb_libur`"
                            + "WHERE `tanggal_libur` = '" + rs.getDate("tanggal") + "' ";
                    ResultSet rs_tgl_libur = Utility.db.getStatement().executeQuery(query_tgl_libur);
                    if (rs_tgl_libur.next()) {
                        keterangan_hari_libur = rs_tgl_libur.getString("keterangan");
                        row[8] = keterangan_hari_libur;
                    }

                    String query_pengampunan_terlambat = "SELECT * FROM `tb_pengampunan_terlambat`"
                            + "WHERE `tanggal_pengampunan` = '" + rs.getDate("tanggal") + "' ";
                    ResultSet rs_pengampunan_terlambat = Utility.db.getStatement().executeQuery(query_pengampunan_terlambat);
                    boolean pengampunan_terlambat = rs_pengampunan_terlambat.next();

                    SimpleDateFormat Timeformat = new SimpleDateFormat("HH:mm:ss");
                    Date jam_masuk = null, jam_pulang = null;
                    long jam_kerja = 0;
                    if (rs.getString("jam_kerja") != null && (keterangan_hari_libur.equals("") || keterangan_hari_libur.toLowerCase().contains("cuti bersama"))) {
                        int hari_ke = DayOfWeek.valueOf(rs.getString("Hari")).getValue();
                        String query_jam_kerja = "SELECT `masuk" + hari_ke + "`, `pulang" + hari_ke + "`, `istirahat" + hari_ke + "` FROM `tb_jam_kerja` "
                                + "WHERE `jam_kerja` = '" + rs.getString("jam_kerja") + "'";
                        ResultSet rs_jam_kerja = Utility.db.getStatement().executeQuery(query_jam_kerja);
                        if (rs_jam_kerja.next()) {
//                        istirahat = rs_jam_kerja.getLong("istirahat" + Integer.toString(hari_ke)) * 60 * 1000;
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
//                        if (rs_ijin.getString("keterangan").toLowerCase().contains("terlambat")) {
//                        if (StringUtils.containsIgnoreCase(rs_ijin.getString("keterangan"), "terlambat")) {
//                            if (rs_ijin.getString("pengawas") != null && rs_ijin.getString("pengawas").equals("") && rs_ijin.getString("pengawas").equals("-")) {
//                                ijin_terlambat = true;
//                            }
//                        } else 
                        if (rs_ijin.getString("jam_keluar") == null) {
                            label_data_ijin_tidak_valid.setVisible(true);
//                            button_save_data.setEnabled(false);
                        } else if (rs_ijin.getString("jam_kembali") == null) {
                            is_ijin_pulang = true;
                            jam_ijin_pulang = rs_ijin.getString("jam_keluar");
                            Date jam_keluar = Timeformat.parse(rs_ijin.getString("jam_keluar"));
                            if (jam_pulang != null && jam_keluar.before(jam_pulang)) { //jam pulang normal tidak null artinya hari itu bukan hari libur
                                ijin_pulang = jam_pulang.getTime() - jam_keluar.getTime();
                            }
                        } else {
                            Date jam_keluar = Timeformat.parse(rs_ijin.getString("jam_keluar"));
                            Date jam_kembali = Timeformat.parse(rs_ijin.getString("jam_kembali"));
                            ijin_keluar = ijin_keluar + (jam_kembali.getTime() - jam_keluar.getTime());
                        }
                    }

                    long jam_lembur = 0, menit_lembur = 0, durasi_kerja = 0, durasi_lembur_masuk = 0, durasi_lembur_pulang = 0, durasi_lembur = 0, total_menit_lembur = 0, total_menit_kerja = 0, menit_jatah_ijin = 0;
                    String text_jam_mulai_lembur_masuk = "", no_spl = null;
                    Date jam_mulai_lembur_masuk = null;
                    String query_spl = "SELECT `mulai_lembur`, `jenis_spl`, `jenis_lembur`, `tb_surat_lembur_detail`.`nomor_surat`, `tb_libur`.`keterangan`, `tb_surat_lembur`.`disetujui`, `tb_surat_lembur`.`diketahui` "
                            + "FROM `tb_surat_lembur_detail` "
                            + "LEFT JOIN `tb_surat_lembur` ON `tb_surat_lembur_detail`.`nomor_surat` = `tb_surat_lembur`.`nomor_surat`"
                            + "LEFT JOIN `tb_libur` ON `tb_surat_lembur_detail`.`tanggal_lembur` = `tb_libur`.`tanggal_libur`"
                            + "WHERE "
                            + "`id_pegawai` = '" + rs.getString("id_pegawai") + "' "
                            + "AND `tb_surat_lembur_detail`.`tanggal_lembur` = '" + rs.getDate("tanggal") + "' "
                            + "AND `jenis_lembur` = 'Masuk' "
                            + "AND `tb_surat_lembur`.`disetujui` IS NOT NULL ";
                    ResultSet rs_spl = Utility.db.getStatement().executeQuery(query_spl);
                    if (rs_spl.next()) {
//                        jam_masuk = Timeformat.parse(rs_spl.getString("mulai_lembur"));
                        jam_mulai_lembur_masuk = Timeformat.parse(rs_spl.getString("mulai_lembur"));
                        text_jam_mulai_lembur_masuk = rs_spl.getString("mulai_lembur");
                        no_spl = rs_spl.getString("nomor_surat");
                        if (rs_spl.getString("diketahui") == null && rs_spl.getString("jenis_spl").equals("PEJUANG")) {
                            label_data_lembur_tidak_valid.setVisible(true);
                        }
                    }

                    float lembur_kali = rs.getFloat("lembur_x_hari_kerja");
                    long menit_terlambat = 0;
                    Date absen_masuk = Timeformat.parse(rs.getString("Masuk"));
                    Date absen_pulang = Timeformat.parse(rs.getString("Pulang"));
                    boolean tidak_absen_masuk = false, tidak_absen_pulang = false;
                    if (jam_masuk != null) {
                        if (absen_masuk.after(new Date(jam_masuk.getTime() + (4 * 60 * 60 * 1000)))) {
                            absen_masuk = jam_masuk;
                            tidak_absen_masuk = true;
                        } else if (absen_masuk.after(new Date(jam_masuk.getTime() + (2 * 60 * 1000)))) { //toleransi 2 menit 00 detik
                            menit_terlambat = (absen_masuk.getTime() - jam_masuk.getTime()) / (60 * 1000);
                            if (pengampunan_terlambat) {
                                menit_terlambat = 0;
                                absen_masuk = jam_masuk;
                            }
                        } else if (jam_mulai_lembur_masuk != null) {
                            if (absen_masuk.before(jam_mulai_lembur_masuk)) {
                                absen_masuk = jam_mulai_lembur_masuk;
                            }
                            durasi_lembur_masuk = jam_masuk.getTime() - jam_mulai_lembur_masuk.getTime();
                        } else {
                            absen_masuk = jam_masuk;
                        }

                        if (jam_pulang != null) {
                            if (!is_ijin_pulang && absen_pulang.before(new Date(jam_pulang.getTime() - (4 * 60 * 60 * 1000)))) {
                                absen_pulang = jam_pulang;
                                tidak_absen_pulang = true;
                                if (hari.equals("FRIDAY") && jenis_kelamin.equals("LAKI-LAKI")) {
                                    potensi_karyawan_kabur = potensi_karyawan_kabur + "tgl:" + row[1] + ", nama:" + row[3] + ", bagian:" + row[5] + "\n";
                                    button_berpotensi_kabur.setEnabled(true);
                                }
                            } else if (absen_pulang.after(jam_pulang)) {
                                absen_pulang = new Date((long) Math.floor(absen_pulang.getTime() / (30 * 60 * 1000)) * (30 * 60 * 1000));
                                durasi_lembur_pulang = (absen_pulang.getTime() - jam_pulang.getTime());
                                //pembulatan kebawah jam pulang ke kelipatan 30 menit
                            } else if (absen_pulang.before(jam_pulang)) {
                                ijin_pulang = jam_pulang.getTime() - absen_pulang.getTime();
                            }
                        }

                        durasi_kerja = (absen_pulang.getTime() - absen_masuk.getTime()) - (jam_kerja + ijin_keluar);
                        durasi_lembur = durasi_lembur_masuk + durasi_lembur_pulang;
                        total_menit_kerja = (durasi_kerja / (60 * 1000));
                        total_menit_lembur = (durasi_lembur / (60 * 1000)); //total lembur dalam menit
                        jam_lembur = (durasi_lembur / (60 * 60 * 1000)); //lembur sebelum di kurangi istirahat dan pembulatan
                        menit_lembur = (durasi_lembur / (60 * 1000)) % 60; //lembur sebelum di kurangi istirahat dan pembulatan
                        String query_pengurangan_lembur = "SELECT `pengurangan` FROM `tb_lembur_pengurangan` "
                                + "WHERE `id_pegawai` = '" + rs.getString("id_pegawai") + "' AND `tgl_lembur` = '" + rs.getDate("tanggal") + "'";
                        ResultSet rs_pengurangan_lembur = Utility.db.getStatement().executeQuery(query_pengurangan_lembur);
                        if (rs_pengurangan_lembur.next()) {
                            total_menit_lembur = total_menit_lembur - rs_pengurangan_lembur.getInt("pengurangan");
                        }
                        menit_jatah_ijin = -60;
                        if ((jam_kerja / (60 * 60 * 1000)) <= 5) {
                            menit_jatah_ijin = -15;
                        }
                    } else if (no_spl != null) {
                        durasi_lembur = absen_pulang.getTime() - jam_mulai_lembur_masuk.getTime();
                        total_menit_lembur = (durasi_lembur / (60 * 1000)); //total lembur dalam menit
                        jam_lembur = (durasi_lembur / (60 * 60 * 1000)); //lembur sebelum di kurangi istirahat dan pembulatan
                        menit_lembur = (durasi_lembur / (60 * 1000)) % 60; //lembur sebelum di kurangi istirahat dan pembulatan
                        String query_pengurangan_lembur = "SELECT `pengurangan` FROM `tb_lembur_pengurangan` "
                                + "WHERE `id_pegawai` = '" + rs.getString("id_pegawai") + "' AND `tgl_lembur` = '" + rs.getDate("tanggal") + "'";
                        ResultSet rs_pengurangan_lembur = Utility.db.getStatement().executeQuery(query_pengurangan_lembur);
                        if (rs_pengurangan_lembur.next()) {
                            total_menit_lembur = total_menit_lembur - rs_pengurangan_lembur.getInt("pengurangan");
                        }
                    } else {
                        jam_lembur = 0;
                        menit_lembur = 0;
                    }

                    if (rs.getString("nama_bagian").contains("SECURITY")) {
                        if (total_menit_lembur >= 480) {//lembur lebih dari 8 jam
                            total_menit_lembur = total_menit_lembur - 60;
                        }
                        
                        if (total_menit_lembur > 120) {//kelipatan 30 menit di hitung setelah jam ke 2
                            total_menit_lembur = (int) Math.floor(total_menit_lembur / 30) * 30;
                        } else if (total_menit_lembur > 0) {
                            total_menit_lembur = (int) Math.floor(total_menit_lembur / 60) * 60;
                        } else {
                            total_menit_lembur = 0;
                        }
                    } else { //selain bagian security
                        if (total_menit_lembur >= 510) {//lembur lebih dari 8.5 jam
                            total_menit_lembur = total_menit_lembur - 60;
                        } else if (total_menit_lembur >= 210) {//lembur lebih dari 3.5 jam
                            total_menit_lembur = total_menit_lembur - 30;
                        }
                        
                        if (total_menit_lembur > 120) {//kelipatan 30 menit di hitung setelah jam ke 2
                            total_menit_lembur = (int) Math.floor(total_menit_lembur / 30) * 30;
                        } else if (total_menit_lembur > 0) {
                            total_menit_lembur = (int) Math.floor(total_menit_lembur / 60) * 60;
                        } else {
                            total_menit_lembur = 0;
                        }
                    }

                    if (total_menit_kerja < menit_jatah_ijin) {
                        premi_hadir = 0;
                    } else if (tidak_absen_masuk) {
                        premi_hadir = 1;
                    } else if (tidak_absen_pulang) {
                        premi_hadir = 2;
                    } else if (jam_kerja > 0) {
                        premi_hadir = 3;
                    } else if (total_menit_lembur >= 240) {
                        premi_hadir = 4;//lembur di hari libur
                        lembur_kali = rs.getFloat("lembur_x_hari_libur");
                    } else {
                        premi_hadir = 5;//Lembur di hari libur, tapi kurang dari 4 jam, tidak dapat premi
                        lembur_kali = rs.getFloat("lembur_x_hari_libur");
                    }

                    if (keterangan_hari_libur.toLowerCase().contains("cuti bersama")) {
                        if (total_menit_kerja < menit_jatah_ijin) {
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
                    row[16] = no_spl;
                    row[17] = text_jam_mulai_lembur_masuk;
                    row[18] = jam_lembur;
                    row[19] = menit_lembur;

                    String level_gaji = rs.getString("level_gaji");
                    float upah_per_hari = rs.getInt("upah_per_hari");
                    float upah_per_menit = rs.getFloat("upah_per_hari") / (7 * 60);
                    float upah_lembur_per_menit = rs.getFloat("lembur_per_jam") / 60f;
                    String tim_bantu = "SELECT `id_pegawai`, `tanggal_bantu`, `tb_tim_bantu`.`level_gaji_bantu`, `tb_level_gaji`.`upah_per_hari`, `lembur_per_jam`, `lembur_x_hari_kerja`, `lembur_x_hari_libur` \n"
                            + "FROM `tb_tim_bantu` "
                            + "LEFT JOIN `tb_level_gaji` ON `tb_tim_bantu`.`level_gaji_bantu` = `tb_level_gaji`.`level_gaji`\n"
                            + "WHERE `id_pegawai` = '" + rs.getString("id_pegawai") + "' "
                            + "AND `tanggal_bantu` = '" + rs.getDate("tanggal") + "' ";
                    ResultSet rs_tim_bantu = Utility.db.getStatement().executeQuery(tim_bantu);
                    if (rs_tim_bantu.next()) {
                        level_gaji = rs_tim_bantu.getString("level_gaji_bantu");
                        upah_per_hari = rs_tim_bantu.getFloat("upah_per_hari");
                        upah_per_menit = rs_tim_bantu.getFloat("upah_per_hari") / (7 * 60);
                        upah_lembur_per_menit = rs_tim_bantu.getFloat("lembur_per_jam") / 60f;
                    }

                    long menit_ijin_keluar = ijin_keluar / (60 * 1000);
                    long menit_ijin_pulang = ijin_pulang / (60 * 1000);
                    float output_lembur = 0, potongan_terlambat = 0, potongan_ijin_keluar = 0, potongan_ijin_pulang = 0;
                    if (menit_terlambat > 0) {
                        potongan_terlambat = (float) menit_terlambat * upah_per_menit;
                    }
                    if (menit_ijin_keluar > 0) {
                        potongan_ijin_keluar = (float) menit_ijin_keluar * upah_per_menit;
                    }
                    if (menit_ijin_pulang > 0) {
                        potongan_ijin_pulang = (float) menit_ijin_pulang * upah_per_menit;
                    }
                    
                    output_lembur = ((int) total_menit_lembur * lembur_kali * upah_lembur_per_menit);

                    if (level_gaji != null && level_gaji.toUpperCase().contains("BORONG")) {
                        output_lembur = 0;
                        total_menit_lembur = 0;
                    }

//                        if (lembur > 0 && lembur < 60) { //perhitungan 1.5x dan 2x
//                            output = (int) (30 * 1.5 * upah_per_menit);
//                        } else if (lembur >= 60) {
//                            output = (int) (60 * 1.5 * upah_per_menit) + ((lembur - 60) * 2 * upah_per_menit);
//                        }
                    row[20] = total_menit_lembur;
                    row[21] = Math.round(menit_terlambat);
                    row[22] = Math.round(potongan_terlambat * 1.5);//rupiah potongan terlambat
                    row[23] = Math.round(menit_ijin_keluar + menit_ijin_pulang);// menit ijin pulang / keluar
                    row[24] = Math.round((potongan_ijin_keluar + potongan_ijin_pulang) * 1.5);//rupiah potongan ijin
                    row[25] = Math.round(output_lembur);
                    row[26] = upah_per_hari;
                    row[27] = premi_hadir;
                    row[28] = level_gaji;
                    row[29] = rs.getString("kode_grup");
                    row[30] = rs.getString("jalur_jemputan");
                    row[31] = rs.getString("jam_kerja");
//                    row[31] = rs.getInt("potongan_bpjs");
//                    row[32] = rs.getInt("potongan_bpjs_tk");
                    model.addRow(row);
                }
            }

            label_total_data.setText(Integer.toString(tabel_data_lembur.getRowCount()));
            ColumnsAutoSizer.sizeColumnsToFit(tabel_data_lembur);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_BoxBahanJadi.class.getName()).log(Level.SEVERE, null, ex);
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
        button_save_data = new javax.swing.JButton();
        label_data_ijin_tidak_valid = new javax.swing.JLabel();
        button_input_jam_pulang = new javax.swing.JButton();
        label_data_lembur_tidak_valid = new javax.swing.JLabel();
        button_pengampunan_terlambat = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        button_input_absen_manual = new javax.swing.JButton();
        button_berpotensi_kabur = new javax.swing.JButton();
        button_input_pengurang_lembur = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jLabel25 = new javax.swing.JLabel();
        txt_search_bagian = new javax.swing.JTextField();

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
                "Hari", "Tanggal", "ID", "Nama", "Departemen", "Bagian", "Posisi", "Status", "Ket. Libur", "Jam Masuk", "Jam Pulang", "Absen Masuk", "Absen Masuk'", "Absen Pulang", "Absen Pulang'", "Ijin Pulang", "No SPL", "Mulai Lembur", "Lembur (Jam)", "Lembur (Menit)", "Tot lembur (Menit)", "Menit Terlambat", "Pot Terlambat", "Menit Ijin", "Potongan Ijin", "Lembur (Rp.)", "Upah/hari", "Premi hadir", "Level Gaji", "Grup", "Jalur Jemputan", "Jam Kerja"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
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
        jLabel5.setText("Perhitungan Lembur Karyawan");

        button_save_data.setBackground(new java.awt.Color(255, 255, 255));
        button_save_data.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_save_data.setText("Save Data");
        button_save_data.setEnabled(false);
        button_save_data.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_save_dataActionPerformed(evt);
            }
        });

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

        button_pengampunan_terlambat.setBackground(new java.awt.Color(255, 255, 255));
        button_pengampunan_terlambat.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_pengampunan_terlambat.setText("Daftar Tanggal Pengampunan Terlambat");
        button_pengampunan_terlambat.setEnabled(false);
        button_pengampunan_terlambat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_pengampunan_terlambatActionPerformed(evt);
            }
        });

        jScrollPane1.setBackground(new java.awt.Color(255, 255, 255));

        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        jTextArea1.setRows(5);
        jTextArea1.setText("Notes : \n1. Lembur pagi akan terhitung jika ada surat perintah lembur, jika tidak ada SPL, maka absen masuk akan terhitung dari jam masuk kerja seharusnya. Dan SPL harus sudah disetujui dan diketahui.\n2. Lembur dihari libur akan terhitung jika ada absen masuk, absen pulang dan surat perintah lembur (sudah disetujui dan diketahui), jika syarat tsb tidak terpenuhi, maka lembur tidak akan di hitung. \n3. potongan jam kerja = kekurangan jam kerja x upah/menit ------> upah/menit = upah/hari / (7 x 60).\n4. jam pulang dibulatkan ke bawah, kelipatan 30 menit.\n5. lembur dibulatkan ke bawah, kelipatan 30 menit setelah lembur > 2jam. Lembur = (jam masuk normal - absen pulang) - (jam kerja + ijin) x 1.5.\n6. lembur diatas 3.5jam, akan di kurangi istirahat 30 menit. lembur diatas 8.5jam, akan dikurangi 1 jam istirahat. khusus bagian mengandung \"SECURITY\", diatas (>=) 8 jam akan dikurangi 1 jam istirahat. \n7. Upah Lembur di hari libur = upah lembur per jam x 2. (selain bagian yang mengandung \"DRIVER\" dan \"SECURITY\")\n8. terlambat akan dikenakan potongan keterlambatan rumus: menit terlambat x upah/menit x 1.5, terlambat adalah absen masuk > jam masuk normal + 2 menit\n9. jika ada ijin pulang, maka absen terakhir = absen pulang.\n10. absen terakhir (absen pulang) 4 jam sebelum jam pulang kerja = tidak absen pulang. absen awal (absen masuk) 4jam setelah jam masuk kerja = tidak absen masuk. (premi hangus).\n11. Jika di tanggal tsb terdaftar jadi tanggal pengampunan terlambat, maka terlambat = 0 dan absen masuk terhitung jam masuk normal di hari itu.\n12. Minimal jam kerja adalah jam kerja normal dikurangi 1 jam. khusus hari kerja dengan total jam kerja 5 jam, minimal jam kerjanya 4 jam 45 menit. kerja kurang dari minimal jam kerja akan terhitung absen.");
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

        button_berpotensi_kabur.setBackground(new java.awt.Color(255, 255, 255));
        button_berpotensi_kabur.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_berpotensi_kabur.setText("Potensi Karyawan Kabur");
        button_berpotensi_kabur.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_berpotensi_kaburActionPerformed(evt);
            }
        });

        button_input_pengurang_lembur.setBackground(new java.awt.Color(255, 255, 255));
        button_input_pengurang_lembur.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_input_pengurang_lembur.setText("Input Pengurang Lembur");
        button_input_pengurang_lembur.setEnabled(false);
        button_input_pengurang_lembur.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_input_pengurang_lemburActionPerformed(evt);
            }
        });

        jScrollPane3.setBackground(new java.awt.Color(255, 255, 255));

        jTextArea2.setColumns(20);
        jTextArea2.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        jTextArea2.setRows(5);
        jTextArea2.setText("Kode Premi :\n0 : Jam kerja kurang\n1 : tidak absen masuk\n2 : tidak absen pulang\n3 : dapat premi\n4 : hari libur, lembur > 4jam, dapat premi\n5 : hari libur tidak dapat premi\n6 : cuti bersama, jam kerja kurang\n7 : cuti bersama, tidak absen masuk\n8 : cuti bersama, tidak absen pulang\n9 : cuti bersama, masuk full, dapat premi");
        jScrollPane3.setViewportView(jTextArea2);

        jLabel25.setBackground(new java.awt.Color(255, 255, 255));
        jLabel25.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel25.setText("PEJUANG");

        txt_search_bagian.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_bagian.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_bagianKeyPressed(evt);
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
                                .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel25)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_refresh1))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_data_ijin_tidak_valid)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_data_lembur_tidak_valid))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(button_export_lembur)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_save_data)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_pengampunan_terlambat)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_input_jam_pulang)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_input_absen_manual)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_input_pengurang_lembur)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_berpotensi_kabur)
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
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_search_bagian, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(button_refresh1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(Date_Search_Lembur1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_Search_Lembur2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_pin1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_NamaKaryawan1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_total_data, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_berpotensi_kabur, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(button_save_data, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_pengampunan_terlambat, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_input_jam_pulang, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_export_lembur, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_input_absen_manual, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_input_pengurang_lembur, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 413, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE))
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

    private void button_save_dataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_save_dataActionPerformed
        // TODO add your handling code here:
//        int valid_data = 0;
//        for (int i = 0; i < tabel_data_lembur.getRowCount(); i++) {
//            if (tabel_data_lembur.getValueAt(i, 20) != null) {
//                valid_data++;
//            }
//        }
        int dialogResult = JOptionPane.showConfirmDialog(this, "Save data (Hanya save data yang ada level gajinya) ?", "Warning", 0);
        if (dialogResult == JOptionPane.YES_OPTION) {
            try {
                Utility.db.getConnection().setAutoCommit(false);
                for (int i = 0; i < tabel_data_lembur.getRowCount(); i++) {
                    if (tabel_data_lembur.getValueAt(i, 14) != null && tabel_data_lembur.getValueAt(i, 28) != null) {//absen pulang tidak null dan level gaji tidak null
                        String jam_kerja = "NULL";
                        String jam_masuk = "NULL";
                        String jam_pulang = "NULL";
                        String level_gaji = "NULL";
                        String grup = "NULL";
                        String jalur = "NULL";
                        String jumlah_lembur_menit = tabel_data_lembur.getValueAt(i, 20).toString();
                        String menit_terlambat = tabel_data_lembur.getValueAt(i, 21).toString();
                        String menit_ijin = tabel_data_lembur.getValueAt(i, 23).toString();
                        if (tabel_data_lembur.getValueAt(i, 9) != null && !tabel_data_lembur.getValueAt(i, 9).toString().equals("")) {
                            jam_masuk = "'" + tabel_data_lembur.getValueAt(i, 9) + "'";
                        }
                        if (tabel_data_lembur.getValueAt(i, 10) != null && !tabel_data_lembur.getValueAt(i, 10).toString().equals("")) {
                            jam_pulang = "'" + tabel_data_lembur.getValueAt(i, 10) + "'";
                        }
                        if (tabel_data_lembur.getValueAt(i, 28) != null && !tabel_data_lembur.getValueAt(i, 28).toString().equals("")) {
                            level_gaji = "'" + tabel_data_lembur.getValueAt(i, 28) + "'";
                        }
                        if (tabel_data_lembur.getValueAt(i, 29) != null && !tabel_data_lembur.getValueAt(i, 29).toString().equals("")) {
                            grup = "'" + tabel_data_lembur.getValueAt(i, 29) + "'";
                        }
                        if (tabel_data_lembur.getValueAt(i, 30) != null && !tabel_data_lembur.getValueAt(i, 30).toString().equals("")) {
                            jalur = "'" + tabel_data_lembur.getValueAt(i, 30) + "'";
                        }
                        if (tabel_data_lembur.getValueAt(i, 31) != null && !tabel_data_lembur.getValueAt(i, 31).toString().equals("")) {
                            jam_kerja = "'" + tabel_data_lembur.getValueAt(i, 31) + "'";
                        }

                        int premi_hadir = (int) tabel_data_lembur.getValueAt(i, 27);
                        if (premi_hadir == 0 || premi_hadir == 6) {
                            jumlah_lembur_menit = "0";
                            menit_terlambat = "0";
                            menit_ijin = "0";
                        }

                        String Query = "INSERT INTO `tb_lembur_rekap`(`id_pegawai`, `tanggal`, `jumlah_lembur_menit`, `menit_terlambat`, `menit_ijin`, `premi_hadir`, `level_gaji`, `grup`, `jalur_jemputan`, `jam_kerja`, `jam_masuk`, `jam_pulang`) "
                                + "VALUES ("
                                + "'" + tabel_data_lembur.getValueAt(i, 2) + "',"
                                + "'" + tabel_data_lembur.getValueAt(i, 1) + "',"
                                + "'" + jumlah_lembur_menit + "',"
                                + "'" + menit_terlambat + "',"
                                + "'" + menit_ijin + "',"
                                + "'" + premi_hadir + "',"
                                + level_gaji + ","
                                + grup + ","
                                + jalur + ","
                                + jam_kerja + ","
                                + jam_masuk + ","
                                + jam_pulang + ")"
                                + "ON DUPLICATE KEY UPDATE "
                                + "`jumlah_lembur_menit`='" + jumlah_lembur_menit + "',"
                                + "`menit_terlambat`='" + menit_terlambat + "',"
                                + "`menit_ijin`='" + menit_ijin + "',"
                                + "`premi_hadir`='" + premi_hadir + "',"
                                + "`level_gaji`=" + level_gaji + ","
                                + "`grup`=" + grup + ","
                                + "`jalur_jemputan`=" + jalur + ","
                                + "`jam_kerja`=" + jam_kerja + ","
                                + "`jam_masuk`=" + jam_masuk + ","
                                + "`jam_pulang`=" + jam_pulang;
                        Utility.db.getConnection().createStatement();
                        Utility.db.getStatement().executeUpdate(Query);
                    }
                }
                Utility.db.getConnection().commit();
                JOptionPane.showMessageDialog(this, "Data Saved Successfully");
            } catch (Exception e) {
                try {
                    Utility.db.getConnection().rollback();
                } catch (SQLException ex) {
                    Logger.getLogger(JPanel_Lembur_Karyawan.class.getName()).log(Level.SEVERE, null, ex);
                }
                JOptionPane.showMessageDialog(this, "Save Failed !" + e);
                Logger.getLogger(JPanel_Lembur_Karyawan.class.getName()).log(Level.SEVERE, null, e);
            } finally {
                try {
                    Utility.db.getConnection().setAutoCommit(true);
                } catch (SQLException ex) {
                    Logger.getLogger(JPanel_Lembur_Karyawan.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }//GEN-LAST:event_button_save_dataActionPerformed

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
                String scan_terakhir = tabel_data_lembur.getValueAt(i, 11) == null ? "Tidak ada absen masuk!" : tabel_data_lembur.getValueAt(i, 11).toString();
                JDialog_adjustment_absen_pulang dialog = new JDialog_adjustment_absen_pulang(new javax.swing.JFrame(), true, id, nama, tanggal, scan_terakhir);
                dialog.pack();
                dialog.setLocationRelativeTo(this);
                dialog.setVisible(true);
                dialog.setEnabled(true);
                refreshTable_Lembur();
            }
        }
    }//GEN-LAST:event_button_input_jam_pulangActionPerformed

    private void button_pengampunan_terlambatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_pengampunan_terlambatActionPerformed
        // TODO add your handling code here:
        JDialog_pengampunan_terlambat dialog = new JDialog_pengampunan_terlambat(new javax.swing.JFrame(), true);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);
    }//GEN-LAST:event_button_pengampunan_terlambatActionPerformed

    private void button_input_absen_manualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_input_absen_manualActionPerformed
        // TODO add your handling code here:
        JDialog_InputAbsenManual dialog = new JDialog_InputAbsenManual(new javax.swing.JFrame(), true);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);
        refreshTable_Lembur();
    }//GEN-LAST:event_button_input_absen_manualActionPerformed

    private void button_berpotensi_kaburActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_berpotensi_kaburActionPerformed
        // TODO add your handling code here:
        JOptionPane.showMessageDialog(this, potensi_karyawan_kabur);
    }//GEN-LAST:event_button_berpotensi_kaburActionPerformed

    private void button_input_pengurang_lemburActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_input_pengurang_lemburActionPerformed
        // TODO add your handling code here:
        JDialog_pengurangan_lembur dialog = new JDialog_pengurangan_lembur(new javax.swing.JFrame(), true);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);
    }//GEN-LAST:event_button_input_pengurang_lemburActionPerformed

    private void txt_search_bagianKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_bagianKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_search_bagianKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser Date_Search_Lembur1;
    private com.toedter.calendar.JDateChooser Date_Search_Lembur2;
    private javax.swing.JButton button_berpotensi_kabur;
    private javax.swing.JButton button_export_lembur;
    private javax.swing.JButton button_input_absen_manual;
    private javax.swing.JButton button_input_jam_pulang;
    private javax.swing.JButton button_input_pengurang_lembur;
    private javax.swing.JButton button_pengampunan_terlambat;
    private javax.swing.JButton button_refresh1;
    private javax.swing.JButton button_save_data;
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
