package waleta_system.Finance;

import waleta_system.Class.Utility;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableCellRenderer;
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
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.ExportToExcel;
import waleta_system.MainForm;

public class JPanel_Lembur_ShiftMalam extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    PreparedStatement pst;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Date today = new Date();
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    DecimalFormat decimalFormat = new DecimalFormat();
    String potensi_karyawan_kabur = "";
    Connection con = Utility.db.getConnection();
    Date tanggal_mulai, tanggal_selesai;
    List<String> list_tanggal = new ArrayList<>();

    public JPanel_Lembur_ShiftMalam() {
        initComponents();
    }

    public void init() {
        label_data_ijin_tidak_valid.setVisible(false);
        label_data_lembur_tidak_valid.setVisible(false);
        if (MainForm.Login_idPegawai.equals("20171201644")//indrika
                || MainForm.Login_idPegawai.equals("20230907768")//diyan
                || MainForm.Login_idPegawai.equals("20180102221")//sebastian
                ) {
            button_save_data_shift_malam.setEnabled(true);
            button_input_jam_pulang.setEnabled(true);
            button_input_absen_manual.setEnabled(true);
            button_input_pengurang_lembur.setEnabled(true);
            button_save_data_fix.setEnabled(true);
            button_slip_harian.setEnabled(true);
        }
        refreshTabel_jadwalKerja();
    }

    private String cekHariKerja(Date tanggal, String posisi) {
        Connection con = Utility.db.getConnection();
        String return_value = "";
        try {
            String get_hari_libur_query = "SELECT * FROM `tb_libur` WHERE `tanggal_libur` = '" + dateFormat.format(tanggal) + "' ";
            PreparedStatement get_hari_libur_pst = con.prepareStatement(get_hari_libur_query);
            ResultSet get_hari_libur_result = get_hari_libur_pst.executeQuery();
            if (get_hari_libur_result.next()) {
                if (get_hari_libur_result.getString("keterangan").toUpperCase().contains("CUTI BERSAMA")) {
                    if (posisi.toUpperCase().contains("STAFF")) {
                        return_value = "Hari Kerja";
                    } else {
                        return_value = "Cuti Bersama";
                    }
                } else {
                    return_value = "Tanggal Merah";
                }
            } else {
                return_value = "Hari Kerja";
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Lembur_ShiftMalam.class.getName()).log(Level.SEVERE, null, ex);
        }
        return return_value;
    }

    private Timestamp getAbsenMasuk(String pin, String jadwal_masuk) {
        Timestamp return_value = null;
        try {
            String get_absen_query = "SELECT `scan_date` FROM `att_log` "
                    + "WHERE `pin` = '" + pin + "' "
                    + "AND `scan_date` > DATE_ADD('" + jadwal_masuk + "', INTERVAL -2 HOUR) "
                    + "AND `scan_date` < DATE_ADD('" + jadwal_masuk + "', INTERVAL 4 HOUR) "
                    + "ORDER BY `scan_date` LIMIT 1";
            PreparedStatement get_absen_pst = con.prepareStatement(get_absen_query);
            ResultSet get_absen_result = get_absen_pst.executeQuery();
            if (get_absen_result.next()) {
                return_value = get_absen_result.getTimestamp("scan_date");
            }
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_Lembur_ShiftMalam.class.getName()).log(Level.SEVERE, null, ex);
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
            PreparedStatement get_absen_pst = con.prepareStatement(get_absen_query);
            ResultSet get_absen_result = get_absen_pst.executeQuery();
            if (get_absen_result.next()) {
                return_value = get_absen_result.getTimestamp("scan_date");
            }
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_Lembur_ShiftMalam.class.getName()).log(Level.SEVERE, null, ex);
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
                    label_data_lembur_tidak_valid.setVisible(true);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_Lembur_ShiftMalam.class.getName()).log(Level.SEVERE, null, ex);
        }
        return return_value;
    }

    private boolean is_pengampunan_terlambat(String tanggal) {
        try {
            String query_pengampunan_terlambat = "SELECT * FROM `tb_pengampunan_terlambat` WHERE `tanggal_pengampunan` = '" + tanggal + "' ";
            ResultSet rs_pengampunan_terlambat = Utility.db.getStatement().executeQuery(query_pengampunan_terlambat);
            return rs_pengampunan_terlambat.next();
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_Lembur_ShiftMalam.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    private void refreshTabel_jadwalKerja() {
        try {
            DefaultTableModel model = (DefaultTableModel) tabel_data_jadwal.getModel();
            model.setRowCount(0);
            String filter_tanggal = "";
            if (Date_Jadwal1.getDate() != null && Date_Jadwal2.getDate() != null) {
                filter_tanggal = "AND `tanggal` BETWEEN '" + dateFormat.format(Date_Jadwal1.getDate()) + "' AND '" + dateFormat.format(Date_Jadwal2.getDate()) + "' ";
            }
            sql = "SELECT `tb_jadwal_kerja_sc`.`id_pegawai`, `tb_karyawan`.`nama_pegawai`, `tb_bagian`.`nama_bagian`, `tb_karyawan`.`posisi`, `tb_karyawan`.`status`, `tb_karyawan`.`level_gaji`, "
                    + "DAYNAME(`tanggal`) AS 'hari', `jenis_hari`, `tanggal`, `jadwal_masuk`, `jadwal_pulang`, IF(`jadwal_masuk`>`jadwal_pulang`, 'malam', 'pagi') AS 'jenis_shift', `durasi_istirahat` \n"
                    + "FROM `tb_jadwal_kerja_sc` \n"
                    + "LEFT JOIN `tb_karyawan` ON `tb_jadwal_kerja_sc`.`id_pegawai` = `tb_karyawan`.`id_pegawai`\n"
                    + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`\n"
                    + "WHERE "
                    + "`tb_karyawan`.`nama_pegawai` LIKE '%" + txt_search_jadwal_byNamaKaryawan.getText() + "%' "
                    + filter_tanggal;
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            Object[] row = new Object[15];
            while (rs.next()) {
                row[0] = rs.getString("id_pegawai");
                row[1] = rs.getString("nama_pegawai");
                row[2] = rs.getString("nama_bagian");
                row[3] = rs.getString("posisi");
                row[4] = rs.getString("status");
                row[5] = rs.getString("level_gaji");
                row[6] = rs.getString("hari");
                row[7] = rs.getString("jenis_hari");
                row[8] = rs.getDate("tanggal");
                row[9] = rs.getTime("jadwal_masuk");
                row[10] = rs.getTime("jadwal_pulang");
                row[11] = rs.getString("jenis_shift");
                row[12] = rs.getString("durasi_istirahat");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(tabel_data_jadwal);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Lembur_ShiftMalam.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void refreshData_lembur() {
        boolean check = true;
        if (Date_penggajian.getDate() == null) {
            check = false;
            JOptionPane.showMessageDialog(this, "Harap masukkan tanggal penggajian");
        } else if (!(new SimpleDateFormat("EEEEE").format(Date_penggajian.getDate()).toUpperCase().equals("THURSDAY") || new SimpleDateFormat("EEEEE").format(Date_penggajian.getDate()).toUpperCase().equals("KAMIS"))) {
            check = false;
            JOptionPane.showMessageDialog(this, "Tanggal penggajian seharusnya hari kamis");
        }

        if (check) {
            tanggal_mulai = new Date(Date_penggajian.getDate().getTime() - (7 * 24 * 60 * 60 * 1000));
            tanggal_selesai = new Date(Date_penggajian.getDate().getTime() - (1 * 24 * 60 * 60 * 1000));

            JTableHeader th = Tabel_data_payslip.getTableHeader();
            TableColumnModel tcm = th.getColumnModel();
            for (int i = 0; i < 7; i++) {
                Date header = new Date(tanggal_mulai.getTime() + (i * 24 * 60 * 60 * 1000));
                TableColumn tc = tcm.getColumn(i + 3);
                tc.setHeaderValue(new SimpleDateFormat("dd MMM").format(header));
                list_tanggal.add(new SimpleDateFormat("yyyy-MM-dd").format(header));
            }
            th.repaint();
            load_data_lembur_sc();
        }
    }

    public void load_data_lembur_sc() {
        try {
            DefaultTableModel model = (DefaultTableModel) tabel_data_lembur_security.getModel();
            model.setRowCount(0);

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
                    + "`tb_karyawan`.`nama_pegawai` LIKE '%" + txt_search_NamaKaryawan.getText() + "%' "
                    + "AND `tb_karyawan`.`pin_finger` LIKE '%" + txt_search_pin.getText() + "%' "
                    + "AND `tanggal` BETWEEN '" + dateFormat.format(tanggal_mulai) + "' AND '" + dateFormat.format(tanggal_selesai) + "' "
                    + "ORDER BY `tb_karyawan`.`nama_pegawai`, `tanggal`";
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                Object[] row = new Object[35];
                row[0] = rs.getString("id_pegawai");
                row[1] = rs.getString("nama_pegawai");
                row[2] = rs.getString("nama_bagian");
                row[3] = rs.getString("posisi");
                row[4] = rs.getString("jam_kerja");
                row[5] = rs.getString("level_gaji");
                row[6] = rs.getString("hari");
                row[7] = rs.getDate("tanggal");
                row[8] = rs.getString("jenis_hari");

                String id_Pegawai = rs.getString("id_pegawai");
                String pin_finger = rs.getString("pin_finger");
                String nama_bagian = rs.getString("nama_bagian");
                String level_gaji = rs.getString("level_gaji");
                String tanggal_jadwal = rs.getString("tanggal");
                String jenis_hari = rs.getString("jenis_hari");
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

                String[] data_spl = getSPLMasuk(id_Pegawai, tanggal_jadwal);
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
                        + "WHERE `id_pegawai` = '" + id_Pegawai + "' "
                        + "AND `tanggal_keluar` BETWEEN '" + tanggal_jadwal + "' AND DATE_ADD('" + tanggal_jadwal + "', INTERVAL 1 DAY)";
                ResultSet rs_ijin = Utility.db.getStatement().executeQuery(query_ijin);
                while (rs_ijin.next()) {
                    if (rs_ijin.getString("jam_keluar") == null) {
                        label_data_ijin_tidak_valid.setVisible(true);
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
                                + "WHERE `id_pegawai` = '" + rs.getString("id_pegawai") + "' AND `tgl_lembur` = '" + rs.getDate("tanggal") + "'";
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
                } else if (premi_hadir == 4 || premi_hadir == 5) {
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
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(tabel_data_lembur_security);
            label_total_data.setText(Integer.toString(tabel_data_lembur_security.getRowCount()));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Lembur_ShiftMalam.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refresh_payslip() {
        if (tabel_data_lembur_security.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Silahkan jalankan perhitungan lembur di sheet 1 terlebih dulu!");
        } else {
            try {
                double total_lembur_all = 0, total_premi_hadir_all = 0, total_transfer_all = 0;
                DefaultTableModel model = (DefaultTableModel) Tabel_data_payslip.getModel();
                model.setRowCount(0);
                sql = "SELECT `tb_jadwal_kerja_sc`.`id_pegawai`, `pin_finger`, `nama_pegawai`, "
                        + "CONCAT(IFNULL(`divisi_bagian`,''), '-', IFNULL(`bagian_bagian`,''), '-', IFNULL(`ruang_bagian`,'')) AS 'nama_bagian', "
                        + "`posisi`, `tb_karyawan`.`status`, `tb_karyawan`.`level_gaji`, `jam_kerja`, `jalur_jemputan`, `upah_per_hari`, `premi_hadir`, `potongan_bpjs`, `potongan_bpjs_tk`, "
                        + "SUM(IF(`jenis_hari` = 'Hari Kerja', 1, 0)) AS 'hari_kerja_normal' "
                        + "FROM `tb_jadwal_kerja_sc` \n"
                        + "LEFT JOIN `tb_karyawan` ON `tb_jadwal_kerja_sc`.`id_pegawai` = `tb_karyawan`.`id_pegawai`\n"
                        + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`\n"
                        + "LEFT JOIN `tb_level_gaji` ON `tb_karyawan`.`level_gaji` = `tb_level_gaji`.`level_gaji`\n"
                        + "WHERE "
                        + "`tb_karyawan`.`nama_pegawai` LIKE '%" + txt_search_karyawan.getText() + "%' "
                        + "AND `tanggal` BETWEEN '" + dateFormat.format(tanggal_mulai) + "' AND '" + dateFormat.format(tanggal_selesai) + "' "
                        + "GROUP BY `tb_jadwal_kerja_sc`.`id_pegawai` "
                        + "ORDER BY `tb_karyawan`.`nama_pegawai`";
                pst = con.prepareStatement(sql);
                rs = pst.executeQuery();
                int nomor_urut = 1;

                while (rs.next()) {
                    Object[] row = new Object[35];
                    String id_pegawai = rs.getString("id_pegawai");
                    row[0] = nomor_urut;
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

                    for (int i = 0; i < tabel_data_lembur_security.getRowCount(); i++) {
                        if (id_pegawai.equals(tabel_data_lembur_security.getValueAt(i, 0).toString())) {
                            tanggal_ke = list_tanggal.indexOf(tabel_data_lembur_security.getValueAt(i, 7).toString());

                            lembur_jam = Math.round((double) tabel_data_lembur_security.getValueAt(i, 17) / 60d * 10d) / 10d;
                            total_jam_lembur = total_jam_lembur + lembur_jam;
                            row[3 + (int) tanggal_ke] = lembur_jam;

                            total_upah_lembur = total_upah_lembur + (double) tabel_data_lembur_security.getValueAt(i, 18);//total upah lembur
                            total_pot_terlambat = total_pot_terlambat + (double) tabel_data_lembur_security.getValueAt(i, 20);//total pot terlambat
                            total_pot_ijin = total_pot_ijin + (double) tabel_data_lembur_security.getValueAt(i, 23);//total pot ijin keluar / pulang
                            int kode_premi = (int) tabel_data_lembur_security.getValueAt(i, 25);

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
                            + "WHERE `id_pegawai` = '" + rs.getString("id_pegawai") + "' "
                            + "AND `tanggal` BETWEEN '" + dateFormat.format(tanggal_mulai) + "' AND '" + dateFormat.format(tanggal_selesai) + "' ";
                    PreparedStatement pst_bonus_pencapaian_produksi = Utility.db.getConnection().prepareStatement(sql_bonus_pencapaian_produksi);
                    ResultSet rs_bonus_pencapaian_produksi = pst_bonus_pencapaian_produksi.executeQuery();
                    double bonus_pencapaian_produksi = 0;
                    if (rs_bonus_pencapaian_produksi.next()) {
                        bonus_pencapaian_produksi = rs_bonus_pencapaian_produksi.getInt("bonus_pencapaian_produksi");
                    }

                    String sql_bonus_operator_atb = "SELECT SUM(`bonus_operator_atb`) AS 'bonus_operator_atb' "
                            + "FROM `tb_bonus_operator_atb` "
                            + "WHERE `id_pegawai` = '" + rs.getString("id_pegawai") + "' "
                            + "AND `tanggal` BETWEEN '" + dateFormat.format(tanggal_mulai) + "' AND '" + dateFormat.format(tanggal_selesai) + "' ";
                    PreparedStatement pst_bonus_operator_atb = Utility.db.getConnection().prepareStatement(sql_bonus_operator_atb);
                    ResultSet rs_bonus_operator_atb = pst_bonus_operator_atb.executeQuery();
                    double bonus_operator_atb = 0;
                    if (rs_bonus_operator_atb.next()) {
                        bonus_operator_atb = rs_bonus_operator_atb.getInt("bonus_operator_atb");
                    }

                    double gaji = total_upah_harian //gaji harian
                            + premi_hadir //premi hadir
                            + total_upah_lembur //upah lembur
                            + 0 //upah borong
                            + bonus_operator_atb //bonus kecepatan
                            + 0 //bonus 2
                            + bonus_pencapaian_produksi //bonus pencapaian produksi
                            + 0 //bonus TBT
                            + piutang_karyawan //piutang karyawan
                            - potongan_bpjs //potongan bpjs kesehatan
                            - potongan_bpjs_tk //potongan bpjs ketenagakerjaan
                            - potongan_transport //potongan transport
                            - total_pot_terlambat //potongan terlambat
                            - total_pot_ijin; //potongan ijin
                    if (gaji < 0) {
                        gaji = total_upah_harian //gaji harian
                                + premi_hadir //premi hadir
                                + total_upah_lembur //upah lembur
                                + 0 //upah borong
                                + bonus_operator_atb //bonus kecepatan
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
                    row[22] = bonus_operator_atb; //bonus 1 kecepatan
                    row[23] = 0; //bonus 2 mk utuh
                    row[24] = bonus_pencapaian_produksi; //bonus pencapaian produksi
                    row[25] = piutang_karyawan; //piutang
                    row[26] = Math.round(gaji * 1000.d) / 1000.d;//total gaji
                    row[27] = keterangan_premi;
                    row[28] = rs.getString("nama_bagian");
                    row[29] = rs.getString("level_gaji");
                    row[30] = rs.getString("jam_kerja");

                    total_lembur_all = total_lembur_all + total_upah_lembur;
                    total_premi_hadir_all = total_premi_hadir_all + premi_hadir;
                    total_transfer_all = total_transfer_all + Math.round(gaji * 1000.d) / 1000.d;
                    model.addRow(row);
                    nomor_urut++;
                }
                ColumnsAutoSizer.sizeColumnsToFit(Tabel_data_payslip);

                decimalFormat.setGroupingUsed(true);
                label_total_upah_lembur.setText(decimalFormat.format(total_lembur_all));
                label_total_tunjangan_hadir.setText(decimalFormat.format(total_premi_hadir_all));
                label_total_gaji.setText(decimalFormat.format(total_transfer_all));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e);
                Logger.getLogger(JPanel_Lembur_ShiftMalam.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }

    public void rekap_jam_kerja_kurang() {
        try {
            ArrayList<String> id_pegawai = new ArrayList<>();
            ArrayList<String> tanggal = new ArrayList<>();

            sql = "SELECT `tb_lembur_rekap`.`id_pegawai`, `tb_karyawan`.`nama_pegawai`, CONCAT(`divisi_bagian`, '-', `ruang_bagian`) AS 'nama_bagian',\n"
                    + "DAYNAME(`tanggal`) AS 'hari', `tanggal`, `premi_hadir` \n"
                    + "FROM `tb_lembur_rekap` \n"
                    + "LEFT JOIN `tb_karyawan` ON `tb_lembur_rekap`.`id_pegawai` = `tb_karyawan`.`id_pegawai`\n"
                    + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`\n"
                    + "WHERE `premi_hadir` = 0\n"
                    + "AND `jam_kerja` = 'SHIFT_MALAM'\n"
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
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Lembur_ShiftMalam.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        txt_search_NamaKaryawan = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txt_search_pin = new javax.swing.JTextField();
        button_refresh = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabel_data_lembur_security = new javax.swing.JTable();
        jLabel14 = new javax.swing.JLabel();
        label_total_data = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        button_export_lembur = new javax.swing.JButton();
        button_input_jam_pulang = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        button_input_absen_manual = new javax.swing.JButton();
        button_input_pengurang_lembur = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        label_data_lembur_tidak_valid = new javax.swing.JLabel();
        label_data_ijin_tidak_valid = new javax.swing.JLabel();
        txt_search_bagian = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        Date_penggajian = new com.toedter.calendar.JDateChooser();
        button_save_data_shift_malam = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        button_load = new javax.swing.JButton();
        jScrollPane6 = new javax.swing.JScrollPane();
        Tabel_data_payslip = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        label_total_upah_lembur = new javax.swing.JLabel();
        label_total_tunjangan_hadir = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        label_total_gaji = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txt_search_karyawan = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        txt_bonus_tbt = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        txt_potongan_transport = new javax.swing.JTextField();
        txt_potongan_bpjs = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        button_save_data_fix = new javax.swing.JButton();
        jLabel18 = new javax.swing.JLabel();
        txt_potongan_bpjs_tk = new javax.swing.JTextField();
        button_export = new javax.swing.JButton();
        button_slip_harian = new javax.swing.JButton();
        jScrollPane7 = new javax.swing.JScrollPane();
        jTextArea4 = new javax.swing.JTextArea();
        jPanel1 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        txt_search_jadwal_byNamaKaryawan = new javax.swing.JTextField();
        jScrollPane4 = new javax.swing.JScrollPane();
        tabel_data_jadwal = new javax.swing.JTable();
        button_refresh1 = new javax.swing.JButton();
        jLabel37 = new javax.swing.JLabel();
        Date_Jadwal1 = new com.toedter.calendar.JDateChooser();
        Date_Jadwal2 = new com.toedter.calendar.JDateChooser();
        button_export_jadwal = new javax.swing.JButton();
        button_insert_jadwal = new javax.swing.JButton();
        button_edit_jadwal = new javax.swing.JButton();
        button_delete_jadwal = new javax.swing.JButton();
        jLabel38 = new javax.swing.JLabel();
        label_total_data_jadwal = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTextArea3 = new javax.swing.JTextArea();
        button_insert_jadwal1 = new javax.swing.JButton();

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel3.setText("Nama Karyawan :");

        txt_search_NamaKaryawan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_search_NamaKaryawan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_NamaKaryawanKeyPressed(evt);
            }
        });

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel9.setText("PIN :");

        txt_search_pin.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_search_pin.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_pinKeyPressed(evt);
            }
        });

        button_refresh.setBackground(new java.awt.Color(255, 255, 255));
        button_refresh.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_refresh.setText("Refresh");
        button_refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refreshActionPerformed(evt);
            }
        });

        tabel_data_lembur_security.setAutoCreateRowSorter(true);
        tabel_data_lembur_security.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tabel_data_lembur_security.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Nama", "Bagian", "Posisi", "Jam Kerja", "Level Gaji", "Hari", "Tanggal", "Jenis Hari", "Jam Masuk", "Jam Pulang", "No SPL", "Jam SPL", "Absen Masuk", "Absen Pulang", "Absen Masuk'", "Absen Pulang'", "Lembur (Menit)", "Lembur (Rp.)", "Menit Terlambat", "Pot Terlambat", "Ijin Pulang", "Menit Ijin", "Potongan Ijin", "Upah/hari", "Premi hadir", "Ket.", "Gaji", "Menit Durasi Kerja", "Minimal Jam Kerja"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Integer.class, java.lang.String.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabel_data_lembur_security.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(tabel_data_lembur_security);

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setText("Total Data :");

        label_total_data.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data.setText("0");

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel7.setText("Bagian :");

        button_export_lembur.setBackground(new java.awt.Color(255, 255, 255));
        button_export_lembur.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_lembur.setText("Export");
        button_export_lembur.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_lemburActionPerformed(evt);
            }
        });

        button_input_jam_pulang.setBackground(new java.awt.Color(255, 255, 255));
        button_input_jam_pulang.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_input_jam_pulang.setText("Input Jam Pulang Manual");
        button_input_jam_pulang.setEnabled(false);
        button_input_jam_pulang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_input_jam_pulangActionPerformed(evt);
            }
        });

        jScrollPane1.setBackground(new java.awt.Color(255, 255, 255));

        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        jTextArea1.setRows(5);
        jTextArea1.setText("Notes : \n1. Range pencarian absen masuk adalah -2jam dan +4jam, jika tidak ada absen pada range tsb, maka dianggap tidak absen. (max terlambat 4jam)\n2. Range pencarian absen pulang adalah -2jam dan +12jam, jika tidak ada absen pada range tsb, maka dianggap tidak absen. (max lembur pulang 12jam)\n3. Lembur pagi akan terhitung jika ada surat perintah lembur, jika tidak ada SPL, maka absen masuk akan terhitung dari jam masuk kerja seharusnya. Dan SPL harus sudah disetujui dan diketahui.\n4. Jika Lembur di hari libur tidak perlu ada SPL, hanya harus ada pada jadwal kerja.\n5. jam pulang dibulatkan ke bawah, kelipatan 30 menit.\n6. Upah per menit = upah per hari / (7jam x 60menit). Upah per jam = upah per hari / 7jam.\n7. Potongan terlambat, ijin keluar, dan ijin pulang = menit potongan x upah / menit.\n8. jika potongan tidak lebih dari 60 menit, maka terhitung masuk dan dapat premi.\n9. upah lembur = 1.5x upah per jam, jika libur di hari libur maka upah lembur = 2x upah per jam.\n10. Khusus upah lembur DRIVER 1.5x upah per jam walaupun hari libur.\n11. Khusus BAGIAN SECURITY upah lembur = FLAT 10.000/jam.");
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
        jTextArea2.setText("Kode Premi :\n0 : Jam kerja kurang\n1 : tidak absen masuk\n2 : tidak absen pulang\n3 : dapat premi\n4 : hari libur, lembur > 4jam, dapat premi\n5 : hari libur tidak dapat premi");
        jScrollPane3.setViewportView(jTextArea2);

        label_data_lembur_tidak_valid.setBackground(new java.awt.Color(255, 255, 255));
        label_data_lembur_tidak_valid.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_data_lembur_tidak_valid.setForeground(new java.awt.Color(255, 0, 0));
        label_data_lembur_tidak_valid.setText("*Ada data LEMBUR yang tidak valid");

        label_data_ijin_tidak_valid.setBackground(new java.awt.Color(255, 255, 255));
        label_data_ijin_tidak_valid.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_data_ijin_tidak_valid.setForeground(new java.awt.Color(255, 0, 0));
        label_data_ijin_tidak_valid.setText("*Ada data IJIN yang tidak valid");

        txt_search_bagian.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_search_bagian.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_bagianKeyPressed(evt);
            }
        });

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setText("Tanggal penggajian :");

        Date_penggajian.setBackground(new java.awt.Color(255, 255, 255));
        Date_penggajian.setDateFormatString("dd MMMM yyyy");
        Date_penggajian.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        button_save_data_shift_malam.setBackground(new java.awt.Color(255, 255, 255));
        button_save_data_shift_malam.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_save_data_shift_malam.setText("Save Data");
        button_save_data_shift_malam.setEnabled(false);
        button_save_data_shift_malam.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_save_data_shift_malamActionPerformed(evt);
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
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 252, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_NamaKaryawan, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_pin, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_penggajian, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_bagian, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_refresh))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(button_export_lembur)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_save_data_shift_malam)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_input_jam_pulang)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_input_absen_manual)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_input_pengurang_lembur)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_data)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_data_ijin_tidak_valid)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_data_lembur_tidak_valid)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_pin, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_NamaKaryawan, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_penggajian, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_refresh, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_bagian, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_data_ijin_tidak_valid, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_data_lembur_tidak_valid, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(button_input_jam_pulang, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_export_lembur, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_input_absen_manual, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_input_pengurang_lembur, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_data, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_save_data_shift_malam, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 419, Short.MAX_VALUE)
                .addGap(9, 9, 9)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Perhitungan Lembur Shift", jPanel2);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        button_load.setBackground(new java.awt.Color(255, 255, 255));
        button_load.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_load.setText("Refresh");
        button_load.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_loadActionPerformed(evt);
            }
        });

        Tabel_data_payslip.setAutoCreateRowSorter(true);
        Tabel_data_payslip.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "ID", "Nama", "Tgl_1", "Tgl_2", "Tgl_3", "Tgl_4", "Tgl_5", "Tgl_6", "Tgl_7", "Total Lembur", "Lembur (Rp.)", "Terlambat (Rp.)", "Ijin Keluar (Rp.)", "Tunj. hadir", "Transport", "BPJS", "BPJS TK", "Bonus TBT", "Hari Kerja", "Gaji Harian", "Gaji Borong", "Bonus 1", "Bonus 2", "Bonus Pencapaian Produksi", "Piutang", "Total Gaji Trasfer", "Ket.", "Bagian", "Level Gaji", "Jam Kerja"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
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
        Tabel_data_payslip.getTableHeader().setReorderingAllowed(false);
        jScrollPane6.setViewportView(Tabel_data_payslip);
        if (Tabel_data_payslip.getColumnModel().getColumnCount() > 0) {
            Tabel_data_payslip.getColumnModel().getColumn(3).setMinWidth(45);
            Tabel_data_payslip.getColumnModel().getColumn(4).setMinWidth(50);
            Tabel_data_payslip.getColumnModel().getColumn(5).setMinWidth(50);
            Tabel_data_payslip.getColumnModel().getColumn(6).setMinWidth(50);
            Tabel_data_payslip.getColumnModel().getColumn(7).setMinWidth(50);
            Tabel_data_payslip.getColumnModel().getColumn(8).setMinWidth(50);
            Tabel_data_payslip.getColumnModel().getColumn(9).setMinWidth(50);
        }

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel4.setText("Total Upah Lembur :");

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel6.setText("Total Tunjangan hadir :");

        label_total_upah_lembur.setBackground(new java.awt.Color(255, 255, 255));
        label_total_upah_lembur.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_upah_lembur.setText("0");

        label_total_tunjangan_hadir.setBackground(new java.awt.Color(255, 255, 255));
        label_total_tunjangan_hadir.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_tunjangan_hadir.setText("0");

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel8.setText("Total Transfer :");

        label_total_gaji.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gaji.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_gaji.setText("0");

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel12.setText("Nama :");

        txt_search_karyawan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_search_karyawan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_karyawanKeyPressed(evt);
            }
        });

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel13.setText("Bonus TBT / org :");

        txt_bonus_tbt.setEditable(false);
        txt_bonus_tbt.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_bonus_tbt.setText("20000");

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel15.setText("Pot. Transport :");

        txt_potongan_transport.setEditable(false);
        txt_potongan_transport.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_potongan_transport.setText("7500");

        txt_potongan_bpjs.setEditable(false);
        txt_potongan_bpjs.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_potongan_bpjs.setText("22842");

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel16.setText("Pot. BPJS KS :");

        button_save_data_fix.setBackground(new java.awt.Color(255, 255, 255));
        button_save_data_fix.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_save_data_fix.setText("save data FIX penggajian");
        button_save_data_fix.setEnabled(false);
        button_save_data_fix.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_save_data_fixActionPerformed(evt);
            }
        });

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel18.setText("Pot. BPJS TK :");

        txt_potongan_bpjs_tk.setEditable(false);
        txt_potongan_bpjs_tk.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_potongan_bpjs_tk.setText("68526");

        button_export.setText("Export to Excel");
        button_export.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_exportActionPerformed(evt);
            }
        });

        button_slip_harian.setText("Slip Harian");
        button_slip_harian.setEnabled(false);
        button_slip_harian.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_slip_harianActionPerformed(evt);
            }
        });

        jTextArea4.setColumns(20);
        jTextArea4.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        jTextArea4.setRows(5);
        jTextArea4.setText("1. premi hadir diberikan tambahan jika masuk di hari libur lebih dari 4 jam.\n2. JIKA Gaji < 0, maka potongan BPJS di 0 kan, dan akan di potong minggu depan.\n3. JIKA Gaji < 0, maka potongan Piutang di 0 kan, dan akan di potong minggu depan.\n4. utk SECURITY, jika (menit_lembur >= 480), maka menit_lembur -60 menit istirahat.\n5. utk selain SECURITY, jika (menit_lembur >= 510), maka menit_lembur -60 menit istirahat.\n6. utk selain SECURITY, jika (menit_lembur >= 210), maka menit_lembur -30 menit istirahat.");
        jScrollPane7.setViewportView(jTextArea4);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane6)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 696, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 540, Short.MAX_VALUE)
                        .addComponent(button_export))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_bonus_tbt, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_potongan_transport, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel16)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_potongan_bpjs, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel18)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_potongan_bpjs_tk, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_upah_lembur)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_tunjangan_hadir)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_gaji))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_load)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_save_data_fix)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_slip_harian)))
                        .addGap(0, 739, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_load, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_save_data_fix, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_slip_harian))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_bonus_tbt, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_potongan_transport, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_potongan_bpjs, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_potongan_bpjs_tk, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_upah_lembur, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_tunjangan_hadir, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_gaji, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 454, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Perhitungan Payroll Shift", jPanel3);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel5.setText("Nama Karyawan :");

        txt_search_jadwal_byNamaKaryawan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_jadwal_byNamaKaryawan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_jadwal_byNamaKaryawanKeyPressed(evt);
            }
        });

        tabel_data_jadwal.setAutoCreateRowSorter(true);
        tabel_data_jadwal.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tabel_data_jadwal.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Nama", "Bagian", "Posisi", "Status", "Level Gaji", "Hari", "Jenis Hari", "Tanggal", "Jam Masuk", "Jam Pulang", "Jenis Shift", "Istirahat (Menit)"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabel_data_jadwal.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(tabel_data_jadwal);

        button_refresh1.setBackground(new java.awt.Color(255, 255, 255));
        button_refresh1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_refresh1.setText("Refresh");
        button_refresh1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refresh1ActionPerformed(evt);
            }
        });

        jLabel37.setBackground(new java.awt.Color(255, 255, 255));
        jLabel37.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel37.setText("Tanggal :");

        Date_Jadwal1.setBackground(new java.awt.Color(255, 255, 255));
        Date_Jadwal1.setToolTipText("");
        Date_Jadwal1.setDateFormatString("dd MMMM yyyy");
        Date_Jadwal1.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        Date_Jadwal1.setMinSelectableDate(new java.util.Date(1483207315000L));

        Date_Jadwal2.setBackground(new java.awt.Color(255, 255, 255));
        Date_Jadwal2.setDateFormatString("dd MMMM yyyy");
        Date_Jadwal2.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N

        button_export_jadwal.setBackground(new java.awt.Color(255, 255, 255));
        button_export_jadwal.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_jadwal.setText("Export");
        button_export_jadwal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_jadwalActionPerformed(evt);
            }
        });

        button_insert_jadwal.setBackground(new java.awt.Color(255, 255, 255));
        button_insert_jadwal.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_insert_jadwal.setText("Input CSV");
        button_insert_jadwal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_insert_jadwalActionPerformed(evt);
            }
        });

        button_edit_jadwal.setBackground(new java.awt.Color(255, 255, 255));
        button_edit_jadwal.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_edit_jadwal.setText("Edit");
        button_edit_jadwal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_edit_jadwalActionPerformed(evt);
            }
        });

        button_delete_jadwal.setBackground(new java.awt.Color(255, 255, 255));
        button_delete_jadwal.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_delete_jadwal.setText("Delete");
        button_delete_jadwal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_delete_jadwalActionPerformed(evt);
            }
        });

        jLabel38.setBackground(new java.awt.Color(255, 255, 255));
        jLabel38.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel38.setText("Total Data :");

        label_total_data_jadwal.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_jadwal.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_data_jadwal.setText("0");

        jScrollPane5.setBackground(new java.awt.Color(255, 255, 255));

        jTextArea3.setColumns(20);
        jTextArea3.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        jTextArea3.setRows(2);
        jTextArea3.setText("Notes : \n1. Jika jam masuk > jam pulang, maka akan dianggap Shift MALAM, maka jadwal pulang tanggal + 1 hari.");
        jScrollPane5.setViewportView(jTextArea3);

        button_insert_jadwal1.setBackground(new java.awt.Color(255, 255, 255));
        button_insert_jadwal1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_insert_jadwal1.setText("Insert");
        button_insert_jadwal1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_insert_jadwal1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 1341, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_jadwal_byNamaKaryawan, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel37)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_Jadwal1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_Jadwal2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_refresh1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_insert_jadwal)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_insert_jadwal1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_edit_jadwal)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_delete_jadwal)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_export_jadwal)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel38)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_data_jadwal)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane5))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(label_total_data_jadwal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_delete_jadwal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_jadwal_byNamaKaryawan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_edit_jadwal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_insert_jadwal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export_jadwal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_Jadwal2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_Jadwal1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_refresh1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_insert_jadwal1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 570, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Jadwal Kerja Shift", jPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txt_search_NamaKaryawanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_NamaKaryawanKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshData_lembur();
        }
    }//GEN-LAST:event_txt_search_NamaKaryawanKeyPressed

    private void txt_search_pinKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_pinKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshData_lembur();
        }
    }//GEN-LAST:event_txt_search_pinKeyPressed

    private void button_refreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refreshActionPerformed
        // TODO add your handling code here:
        refreshData_lembur();
    }//GEN-LAST:event_button_refreshActionPerformed

    private void button_export_lemburActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_lemburActionPerformed
        // TODO add your handling code here:
        DefaultTableModel table = (DefaultTableModel) tabel_data_lembur_security.getModel();
        ExportToExcel.writeToExcel(table, this);
    }//GEN-LAST:event_button_export_lemburActionPerformed

    private void button_input_jam_pulangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_input_jam_pulangActionPerformed
        // TODO add your handling code here:
        int i = tabel_data_lembur_security.getSelectedRow();
        if (i < 0) {
            JOptionPane.showMessageDialog(this, "Silahkan Klik data yang akan diinput jam pulang nya");
        } else {
            int dialogResult = JOptionPane.showConfirmDialog(this, "Adjustment untuk jam pulang " + tabel_data_lembur_security.getValueAt(i, 3).toString() + "?", "Warning", 0);
            if (dialogResult == JOptionPane.YES_OPTION) {
                String id = tabel_data_lembur_security.getValueAt(i, 0).toString();
                String nama = tabel_data_lembur_security.getValueAt(i, 1).toString();
                String tanggal = tabel_data_lembur_security.getValueAt(i, 7).toString();
                String scan_terakhir = tabel_data_lembur_security.getValueAt(i, 13) == null ? "Tidak ada absen masuk!" : tabel_data_lembur_security.getValueAt(i, 13).toString();
                JDialog_adjustment_absen_pulang dialog = new JDialog_adjustment_absen_pulang(new javax.swing.JFrame(), true, id, nama, tanggal, scan_terakhir);
                dialog.pack();
                dialog.setLocationRelativeTo(this);
                dialog.setVisible(true);
                dialog.setEnabled(true);
                refreshData_lembur();
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
        refreshData_lembur();
    }//GEN-LAST:event_button_input_absen_manualActionPerformed

    private void button_input_pengurang_lemburActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_input_pengurang_lemburActionPerformed
        // TODO add your handling code here:
        JDialog_pengurangan_lembur dialog = new JDialog_pengurangan_lembur(new javax.swing.JFrame(), true);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);
    }//GEN-LAST:event_button_input_pengurang_lemburActionPerformed

    private void txt_search_jadwal_byNamaKaryawanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_jadwal_byNamaKaryawanKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTabel_jadwalKerja();
        }
    }//GEN-LAST:event_txt_search_jadwal_byNamaKaryawanKeyPressed

    private void button_refresh1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refresh1ActionPerformed
        // TODO add your handling code here:
        refreshTabel_jadwalKerja();
    }//GEN-LAST:event_button_refresh1ActionPerformed

    private void button_export_jadwalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_jadwalActionPerformed
        // TODO add your handling code here:
        DefaultTableModel table = (DefaultTableModel) tabel_data_jadwal.getModel();
        ExportToExcel.writeToExcel(table, this);
    }//GEN-LAST:event_button_export_jadwalActionPerformed

    private void button_insert_jadwalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_insert_jadwalActionPerformed
        // TODO add your handling code here:
        try {
            JOptionPane.showMessageDialog(this, "Format csv : ID PEGAWAI , (Hari Libur/Hari Kerja) , TANGGAL(yyyy-mm-dd) , JADWAL MASUK (HH:mm:ss) , JADWAL PULANG (HH:mm:ss) , MENIT ISTIRAHAT\n Pemisah Koma (,)");
            int n = 0;
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Select CSV file to import!");
            int result = chooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                Utility.db.getConnection();
                File file = chooser.getSelectedFile();
                String filename1 = file.getAbsolutePath();
                try (BufferedReader br = new BufferedReader(new FileReader(filename1))) {
                    String line;
                    try {
                        Utility.db.getConnection().setAutoCommit(false);
                        while ((line = br.readLine()) != null) {
                            String[] value = line.split(",");
                            String Query = "INSERT INTO `tb_jadwal_kerja_sc`(`id_pegawai`, `jenis_hari`, `tanggal`, `jadwal_masuk`, `jadwal_pulang`, `durasi_istirahat`) "
                                    + "VALUES ('" + value[0] + "','" + value[1] + "','" + value[2] + "','" + value[3] + "','" + value[4] + "','" + value[5] + "') "
                                    + "ON DUPLICATE KEY UPDATE "
                                    + "`jenis_hari` = '" + value[1] + "', "
                                    + "`jadwal_masuk` = '" + value[3] + "', "
                                    + "`jadwal_pulang` = '" + value[4] + "', "
                                    + "`durasi_istirahat` = '" + value[5] + "' ";
                            Utility.db.getConnection().prepareStatement(Query);
                            if ((Utility.db.getStatement().executeUpdate(Query)) > 0) {
                                System.out.println(value[0]);
                                n++;
                                System.out.println(n);
                            } else {
                                System.out.println("gagal");
                            }
                        }
                        Utility.db.getConnection().commit();
                    } catch (Exception ex) {
                        Utility.db.getConnection().rollback();
                        JOptionPane.showMessageDialog(this, ex);
                        Logger.getLogger(JPanel_Lembur_ShiftMalam.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {
                        JOptionPane.showMessageDialog(this, "Data Berhasil Masuk : " + n);
                        Utility.db.getConnection().setAutoCommit(true);
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Lembur_ShiftMalam.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_insert_jadwalActionPerformed

    private void button_edit_jadwalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_edit_jadwalActionPerformed
        // TODO add your handling code here:
        int j = tabel_data_jadwal.getSelectedRow();
        if (j == -1) {
            JOptionPane.showMessageDialog(this, "Silahkan pilih 1 data !");
        } else {
            String id = tabel_data_jadwal.getValueAt(j, 0).toString();
            String tanggal = tabel_data_jadwal.getValueAt(j, 8).toString();
            JDialog_JadwalKerjaSecurity_Edit dialog = new JDialog_JadwalKerjaSecurity_Edit(new javax.swing.JFrame(), true, id, tanggal);
            dialog.setResizable(false);
            dialog.setLocationRelativeTo(this);
            dialog.setEnabled(true);
            dialog.setVisible(true);
            refreshTabel_jadwalKerja();
        }
    }//GEN-LAST:event_button_edit_jadwalActionPerformed

    private void button_delete_jadwalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_delete_jadwalActionPerformed
        // TODO add your handling code here:
        try {
            int j = tabel_data_jadwal.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih 1 data !");
            } else {
                int dialogResult = JOptionPane.showConfirmDialog(this, "Yakin akan hapus data ini?", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    // delete code here
                    String Query = "DELETE FROM `tb_jadwal_kerja_sc` WHERE "
                            + "`id_pegawai` = '" + tabel_data_jadwal.getValueAt(j, 0).toString() + "'"
                            + "AND `tanggal` = '" + tabel_data_jadwal.getValueAt(j, 8).toString() + "'";
                    Utility.db.getConnection().createStatement();
                    if ((Utility.db.getStatement().executeUpdate(Query)) == 1) {
                        JOptionPane.showMessageDialog(this, "data deleted Successfully");
                        refreshTabel_jadwalKerja();
                    } else {
                        JOptionPane.showMessageDialog(this, "delete failed!");
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_Lembur_ShiftMalam.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_delete_jadwalActionPerformed

    private void txt_search_bagianKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_bagianKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshData_lembur();
        }
    }//GEN-LAST:event_txt_search_bagianKeyPressed

    private void button_loadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_loadActionPerformed
        // TODO add your handling code here:
        refresh_payslip();
    }//GEN-LAST:event_button_loadActionPerformed

    private void txt_search_karyawanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_karyawanKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refresh_payslip();
        }
    }//GEN-LAST:event_txt_search_karyawanKeyPressed

    private void button_save_data_fixActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_save_data_fixActionPerformed
        // TODO add your handling code here:

        boolean data_sudah_pernah_simpan = false;
        try {
            sql = "SELECT `tgl_penggajian`, `id_pegawai` FROM `tb_payrol_data` "
                    + "WHERE `tgl_penggajian` = '" + dateFormat.format(Date_penggajian.getDate()) + "' AND `jam_kerja` = 'SHIFT_MALAM'";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                data_sudah_pernah_simpan = true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_Lembur_ShiftMalam.class.getName()).log(Level.SEVERE, null, ex);
        }

        String message = "";
        if (data_sudah_pernah_simpan) {
            message = "Data periode " + dateFormat.format(Date_penggajian.getDate()) + " sudah disimpan!\n"
                    + "Simpan ulang data akan mengubah data sesuai data yang tersimpan terakhir saat ini!\n"
                    + "Lanjutkan?";
        } else {
            if (!txt_search_karyawan.getText().equals("") || !txt_search_bagian.getText().equals("")) {
                JOptionPane.showMessageDialog(this, "Untuk simpan data penggajian yang pertama sebaiknya tanpa filter!");
            }
            message = "Save data penggajian?\n"
                    + "- Status BPJS TK & KS menjadi 'Lunas'\n"
                    + "- Status piutang menjadi 'Lunas'";
        }

        int dialogResult = JOptionPane.showConfirmDialog(this, message, "Warning", 0);
        if (dialogResult == JOptionPane.YES_OPTION) {
            try {
                Utility.db.getConnection().setAutoCommit(false);
                int jumlah_karyawan_dipotong_bpjs = 0, jumlah_karyawan_dipotong_bpjs_tk = 0, jumlah_karyawan_membayar_piutang = 0, saved_data_payroll = 0;
                for (int i = 0; i < Tabel_data_payslip.getRowCount(); i++) {
                    if ((double) Tabel_data_payslip.getValueAt(i, 16) > 0) {
                        sql = "UPDATE `tb_karyawan` SET "
                                + "`potongan_bpjs`=2 "
                                + "WHERE `id_pegawai`='" + Tabel_data_payslip.getValueAt(i, 1).toString() + "'";
                        Utility.db.getConnection().createStatement();
                        if (Utility.db.getStatement().executeUpdate(sql) == 1) {
                            jumlah_karyawan_dipotong_bpjs++;
                        }
                    }
                    if ((double) Tabel_data_payslip.getValueAt(i, 17) > 0) {
                        sql = "UPDATE `tb_karyawan` SET "
                                + "`potongan_bpjs_tk`=2 "
                                + "WHERE `id_pegawai`='" + Tabel_data_payslip.getValueAt(i, 1).toString() + "'";
                        Utility.db.getConnection().createStatement();
                        if (Utility.db.getStatement().executeUpdate(sql) == 1) {
                            jumlah_karyawan_dipotong_bpjs_tk++;
                        }
                    }
                    if ((double) Tabel_data_payslip.getValueAt(i, 25) > 0) {
                        sql = "UPDATE `tb_piutang_karyawan` SET `status`=1, `tgl_lunas`='" + dateFormat.format(Date_penggajian.getDate()) + "' "
                                + "WHERE `status`=0 AND `id_pegawai`='" + Tabel_data_payslip.getValueAt(i, 1).toString() + "'";
                        Utility.db.getConnection().createStatement();
                        if (Utility.db.getStatement().executeUpdate(sql) == 1) {
                            jumlah_karyawan_membayar_piutang++;
                        }
                    }
                    String bagian = "NULL";
                    if (Tabel_data_payslip.getValueAt(i, 28) != null) {
                        bagian = "'" + Tabel_data_payslip.getValueAt(i, 28).toString() + "'";
                    }
                    String insert_payroll_data = "INSERT INTO `tb_payrol_data`(`tgl_penggajian`, `id_pegawai`, `total_jam_lembur`, `lembur`, `pot_terlambat`, `pot_ijin_keluar`, `tunjangan_hadir`, `pot_transport`, `pot_bpjs`, `pot_bpjs_tk`, `bonus_tbt`, `gaji_harian`, `gaji_borong`, `bonus_1`, `bonus_2`, `bonus_pencapaian_produksi`, `piutang`, `keterangan`, `bagian`, `level_gaji`, `jam_kerja`) "
                            + "VALUES ("
                            + "'" + dateFormat.format(Date_penggajian.getDate()) + "',"
                            + "'" + Tabel_data_payslip.getValueAt(i, 1).toString() + "'," //ID PEGAWAI
                            + "'" + Tabel_data_payslip.getValueAt(i, 10).toString() + "'," //TOTAL JAM LEMBUR
                            + "'" + Tabel_data_payslip.getValueAt(i, 11).toString() + "'," //LEMBUR
                            + "'" + Tabel_data_payslip.getValueAt(i, 12).toString() + "'," //POT TERLAMBAT
                            + "'" + Tabel_data_payslip.getValueAt(i, 13).toString() + "'," //POT IJIN KELUAR
                            + "'" + Tabel_data_payslip.getValueAt(i, 14).toString() + "'," //TUNJANGAN HADIR
                            + "'" + Tabel_data_payslip.getValueAt(i, 15).toString() + "'," //POT TRANSPORT
                            + "'" + Tabel_data_payslip.getValueAt(i, 16).toString() + "'," //POT BPJS
                            + "'" + Tabel_data_payslip.getValueAt(i, 17).toString() + "'," //POT BPJS TK
                            + "'" + Tabel_data_payslip.getValueAt(i, 18).toString() + "'," //BONUS TBT
                            + "'" + Tabel_data_payslip.getValueAt(i, 20).toString() + "'," //GAJI HARIAN
                            + "'" + Tabel_data_payslip.getValueAt(i, 21).toString() + "'," //GAJI BORONG
                            + "'" + Tabel_data_payslip.getValueAt(i, 22).toString() + "'," //BONUS 1
                            + "'" + Tabel_data_payslip.getValueAt(i, 23).toString() + "'," //BONUS 2
                            + "'" + Tabel_data_payslip.getValueAt(i, 24).toString() + "'," //bonus pencapaian produksi
                            + "'" + Tabel_data_payslip.getValueAt(i, 25).toString() + "'," //piutang
                            + "'" + Tabel_data_payslip.getValueAt(i, 27).toString() + "'," //keterangan
                            + "" + bagian + "," //bagian
                            + "'" + Tabel_data_payslip.getValueAt(i, 29).toString() + "'," //level gaji
                            + "'" + Tabel_data_payslip.getValueAt(i, 30).toString() + "'" //jam kerja
                            + ") "
                            + "ON DUPLICATE KEY UPDATE "
                            + "`total_jam_lembur`='" + Tabel_data_payslip.getValueAt(i, 10).toString() + "',"
                            + "`lembur`='" + Tabel_data_payslip.getValueAt(i, 11).toString() + "',"
                            + "`pot_terlambat`='" + Tabel_data_payslip.getValueAt(i, 12).toString() + "',"
                            + "`pot_ijin_keluar`='" + Tabel_data_payslip.getValueAt(i, 13).toString() + "',"
                            + "`tunjangan_hadir`='" + Tabel_data_payslip.getValueAt(i, 14).toString() + "',"
                            + "`pot_transport`='" + Tabel_data_payslip.getValueAt(i, 15).toString() + "',"
                            + "`pot_bpjs`='" + Tabel_data_payslip.getValueAt(i, 16).toString() + "',"
                            + "`pot_bpjs_tk`='" + Tabel_data_payslip.getValueAt(i, 17).toString() + "',"
                            + "`bonus_tbt`='" + Tabel_data_payslip.getValueAt(i, 18).toString() + "',"
                            + "`gaji_harian`='" + Tabel_data_payslip.getValueAt(i, 20).toString() + "',"
                            + "`gaji_borong`='" + Tabel_data_payslip.getValueAt(i, 21).toString() + "',"
                            + "`bonus_1`='" + Tabel_data_payslip.getValueAt(i, 22).toString() + "',"
                            + "`bonus_2`='" + Tabel_data_payslip.getValueAt(i, 23).toString() + "',"
                            + "`bonus_pencapaian_produksi`='" + Tabel_data_payslip.getValueAt(i, 24).toString() + "',"
                            + "`piutang`='" + Tabel_data_payslip.getValueAt(i, 25).toString() + "',"
                            + "`keterangan`='" + Tabel_data_payslip.getValueAt(i, 27).toString() + "',"
                            + "`bagian`=" + bagian + ","
                            + "`level_gaji`='" + Tabel_data_payslip.getValueAt(i, 29).toString() + "',"
                            + "`jam_kerja`='" + Tabel_data_payslip.getValueAt(i, 30).toString() + "'";
                    Utility.db.getConnection().createStatement();
                    if (Utility.db.getStatement().executeUpdate(insert_payroll_data) == 1) {
                        saved_data_payroll++;
                    }
                }
                Utility.db.getConnection().commit();
                JOptionPane.showMessageDialog(this, "Jumlah Karyawan yang telah:\n"
                        + "1. Dipotong BPJS Kesehatan = " + jumlah_karyawan_dipotong_bpjs + "\n"
                        + "2. Dipotong BPJS TK = " + jumlah_karyawan_dipotong_bpjs_tk + "\n"
                        + "3. Membayar piutang = " + jumlah_karyawan_membayar_piutang + "\n"
                        + "4. Data Payrol di simpan = " + saved_data_payroll);
            } catch (Exception ex) {
                try {
                    Utility.db.getConnection().rollback();
                } catch (SQLException e) {
                    Logger.getLogger(JPanel_Lembur_ShiftMalam.class.getName()).log(Level.SEVERE, null, e);
                }
                JOptionPane.showMessageDialog(this, ex);
                Logger.getLogger(JPanel_Lembur_ShiftMalam.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    Utility.db.getConnection().setAutoCommit(true);
                    rekap_jam_kerja_kurang();
                } catch (SQLException ex) {
                    Logger.getLogger(JPanel_Lembur_ShiftMalam.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_button_save_data_fixActionPerformed

    private void button_exportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_exportActionPerformed
        // TODO add your handling code here:
        DefaultTableModel table = (DefaultTableModel) Tabel_data_payslip.getModel();
        ExportToExcel.writeToExcel(table, this);
    }//GEN-LAST:event_button_exportActionPerformed

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
            map.put("ID_PEGAWAI", Tabel_data_payslip.getValueAt(0, 1).toString());
            map.put("NAMA_PEGAWAI", Tabel_data_payslip.getValueAt(0, 2).toString());
            map.put("LEMBUR", Double.valueOf(Tabel_data_payslip.getValueAt(0, 11).toString()));
            map.put("POT_TERLAMBAT", Double.valueOf(Tabel_data_payslip.getValueAt(0, 12).toString()));
            map.put("POT_IJIN", Double.valueOf(Tabel_data_payslip.getValueAt(0, 13).toString()));
            map.put("TUNJANGAN_HADIR", Double.valueOf(Tabel_data_payslip.getValueAt(0, 14).toString()));
            map.put("POT_TRANSPORT", Double.valueOf(Tabel_data_payslip.getValueAt(0, 15).toString()));
            map.put("POT_BPJS", Double.valueOf(Tabel_data_payslip.getValueAt(0, 16).toString()));
            map.put("POT_BPJS_TK", Double.valueOf(Tabel_data_payslip.getValueAt(0, 17).toString()));
            map.put("BONUS_TBT", Double.valueOf(Tabel_data_payslip.getValueAt(0, 18).toString()));
            map.put("GAJI_HARIAN", Double.valueOf(Tabel_data_payslip.getValueAt(0, 20).toString()));
            map.put("GAJI_BORONG", Double.valueOf(Tabel_data_payslip.getValueAt(0, 21).toString()));
            map.put("BONUS_LP", Double.valueOf(Tabel_data_payslip.getValueAt(0, 22).toString()));
            map.put("BONUS_MK_UTUH", Double.valueOf(Tabel_data_payslip.getValueAt(0, 23).toString()));
            map.put("BONUS_PENCAPAIAN_PRODUKSI", Double.valueOf(Tabel_data_payslip.getValueAt(0, 24).toString()));
            map.put("PIUTANG", Double.valueOf(Tabel_data_payslip.getValueAt(0, 25).toString()));
            map.put("KETERANGAN", Tabel_data_payslip.getValueAt(0, 27).toString());
            map.put("GRUP", Tabel_data_payslip.getValueAt(0, 28));
            JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
            JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, map, Utility.db.getConnection());
            for (int i = 1; i < Tabel_data_payslip.getRowCount(); i++) {
                JasperDesign JASP_DESIGN2 = JRXmlLoader.load("Report\\Slip_Gaji_karyawan_Harian.jrxml");
                JasperReport JASP_REP2 = JasperCompileManager.compileReport(JASP_DESIGN2);
                Map<String, Object> map2 = new HashMap<>();
                map2.put("HALAMAN", i + 1);
                map2.put("PERIODE_AWAL", tanggal_mulai);
                map2.put("PERIODE_AKHIR", tanggal_selesai);
                map2.put("ID_PEGAWAI", Tabel_data_payslip.getValueAt(i, 1).toString());
                map2.put("NAMA_PEGAWAI", Tabel_data_payslip.getValueAt(i, 2).toString());
                map2.put("LEMBUR", Double.valueOf(Tabel_data_payslip.getValueAt(i, 11).toString()));
                map2.put("POT_TERLAMBAT", Double.valueOf(Tabel_data_payslip.getValueAt(i, 12).toString()));
                map2.put("POT_IJIN", Double.valueOf(Tabel_data_payslip.getValueAt(i, 13).toString()));
                map2.put("TUNJANGAN_HADIR", Double.valueOf(Tabel_data_payslip.getValueAt(i, 14).toString()));
                map2.put("POT_TRANSPORT", Double.valueOf(Tabel_data_payslip.getValueAt(i, 15).toString()));
                map2.put("POT_BPJS", Double.valueOf(Tabel_data_payslip.getValueAt(i, 16).toString()));
                map2.put("POT_BPJS_TK", Double.valueOf(Tabel_data_payslip.getValueAt(i, 17).toString()));
                map2.put("BONUS_TBT", Double.valueOf(Tabel_data_payslip.getValueAt(i, 18).toString()));
                map2.put("GAJI_HARIAN", Double.valueOf(Tabel_data_payslip.getValueAt(i, 20).toString()));
                map2.put("GAJI_BORONG", Double.valueOf(Tabel_data_payslip.getValueAt(i, 21).toString()));
                map2.put("BONUS_LP", Double.valueOf(Tabel_data_payslip.getValueAt(i, 22).toString()));
                map2.put("BONUS_MK_UTUH", Double.valueOf(Tabel_data_payslip.getValueAt(i, 23).toString()));
                map2.put("BONUS_PENCAPAIAN_PRODUKSI", Double.valueOf(Tabel_data_payslip.getValueAt(i, 24).toString()));
                map2.put("PIUTANG", Double.valueOf(Tabel_data_payslip.getValueAt(i, 25).toString()));
                map2.put("KETERANGAN", Tabel_data_payslip.getValueAt(i, 27).toString());
                map2.put("GRUP", Tabel_data_payslip.getValueAt(i, 28));
                JasperPrint JASP_PRINT2 = JasperFillManager.fillReport(JASP_REP2, map2, Utility.db.getConnection());
                List pages = JASP_PRINT2.getPages();
                for (int k = 0; k < pages.size(); k++) {
                    JRPrintPage object = (JRPrintPage) pages.get(k);
                    JASP_PRINT.addPage(object);
                }
            }
            JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
        } catch (JRException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Lembur_ShiftMalam.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_slip_harianActionPerformed

    private void button_insert_jadwal1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_insert_jadwal1ActionPerformed
        // TODO add your handling code here:
        JDialog_JadwalKerjaSecurity_Tambah dialog = new JDialog_JadwalKerjaSecurity_Tambah(new javax.swing.JFrame(), true);
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(this);
        dialog.setEnabled(true);
        dialog.setVisible(true);
        refreshTabel_jadwalKerja();
    }//GEN-LAST:event_button_insert_jadwal1ActionPerformed

    private void button_save_data_shift_malamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_save_data_shift_malamActionPerformed
        // TODO add your handling code here:
        //        int valid_data = 0;
        //        for (int i = 0; i < tabel_data_lembur.getRowCount(); i++) {
        //            if (tabel_data_lembur.getValueAt(i, 20) != null) {
        //                valid_data++;
        //            }
        //        }
        int dialogResult = JOptionPane.showConfirmDialog(this, "Save data (Hanya save data yang ada gajinya) ?", "Warning", 0);
        if (dialogResult == JOptionPane.YES_OPTION) {
            try {
                Utility.db.getConnection().setAutoCommit(false);
                for (int i = 0; i < tabel_data_lembur_security.getRowCount(); i++) {
                    if (tabel_data_lembur_security.getValueAt(i, 27) != null && (double) tabel_data_lembur_security.getValueAt(i, 27) > 0) {//gaji tidak null dan lebih dari 0

                        String bagian = "NULL";
                        String jam_kerja = "NULL";
                        String level_gaji = "NULL";
                        String jam_masuk = "NULL";
                        String jam_pulang = "NULL";
                        String jalur_jemputan = "NULL";
                        String jumlah_lembur_menit = tabel_data_lembur_security.getValueAt(i, 17).toString();
                        String menit_terlambat = tabel_data_lembur_security.getValueAt(i, 19).toString();
                        String menit_ijin = tabel_data_lembur_security.getValueAt(i, 22).toString();
                        if (tabel_data_lembur_security.getValueAt(i, 2) != null && !tabel_data_lembur_security.getValueAt(i, 2).toString().equals("")) {
                            bagian = "'" + tabel_data_lembur_security.getValueAt(i, 2).toString() + "'";
                        }
                        if (tabel_data_lembur_security.getValueAt(i, 4) != null && !tabel_data_lembur_security.getValueAt(i, 4).toString().equals("")) {
                            jam_kerja = "'" + tabel_data_lembur_security.getValueAt(i, 4).toString() + "'";
                        }
                        if (tabel_data_lembur_security.getValueAt(i, 5) != null && !tabel_data_lembur_security.getValueAt(i, 5).toString().equals("")) {
                            level_gaji = "'" + tabel_data_lembur_security.getValueAt(i, 5).toString() + "'";
                        }
                        if (tabel_data_lembur_security.getValueAt(i, 9) != null && !tabel_data_lembur_security.getValueAt(i, 9).toString().equals("")) {
                            jam_masuk = "'" + tabel_data_lembur_security.getValueAt(i, 9).toString() + "'";
                        }
                        if (tabel_data_lembur_security.getValueAt(i, 10) != null && !tabel_data_lembur_security.getValueAt(i, 10).toString().equals("")) {
                            jam_pulang = "'" + tabel_data_lembur_security.getValueAt(i, 10).toString() + "'";
                        }
                        if (tabel_data_lembur_security.getValueAt(i, 25) != null && tabel_data_lembur_security.getValueAt(i, 25).toString().equals("0")) {
                            jumlah_lembur_menit = "0";
                            menit_terlambat = "0";
                            menit_ijin = "0";
                        }
                        String Query = "INSERT INTO `tb_lembur_rekap`(`id_pegawai`, `tanggal`, `jumlah_lembur_menit`, `menit_terlambat`, `menit_ijin`, `premi_hadir`, `level_gaji`, `grup`, `jalur_jemputan`, `jam_kerja`, `jam_masuk`, `jam_pulang`) "
                                + "VALUES ("
                                + "'" + tabel_data_lembur_security.getValueAt(i, 0) + "',"
                                + "'" + tabel_data_lembur_security.getValueAt(i, 7) + "',"
                                + "'" + jumlah_lembur_menit + "',"
                                + "'" + menit_terlambat + "',"
                                + "'" + menit_ijin + "',"
                                + "'" + tabel_data_lembur_security.getValueAt(i, 25) + "',"
                                + level_gaji + ","
                                + bagian + ","
                                + jalur_jemputan + ","
                                + jam_kerja + ","
                                + jam_masuk + ","
                                + jam_pulang + ")"
                                + "ON DUPLICATE KEY UPDATE "
                                + "`jumlah_lembur_menit`='" + jumlah_lembur_menit + "',"
                                + "`menit_terlambat`='" + menit_terlambat + "',"
                                + "`menit_ijin`='" + menit_ijin + "',"
                                + "`premi_hadir`='" + tabel_data_lembur_security.getValueAt(i, 25) + "',"
                                + "`level_gaji`=" + level_gaji + ","
                                + "`grup`=" + bagian + ","
                                + "`jalur_jemputan`=" + jalur_jemputan + ","
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
    }//GEN-LAST:event_button_save_data_shift_malamActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser Date_Jadwal1;
    private com.toedter.calendar.JDateChooser Date_Jadwal2;
    private com.toedter.calendar.JDateChooser Date_penggajian;
    private javax.swing.JTable Tabel_data_payslip;
    private javax.swing.JButton button_delete_jadwal;
    private javax.swing.JButton button_edit_jadwal;
    private javax.swing.JButton button_export;
    private javax.swing.JButton button_export_jadwal;
    private javax.swing.JButton button_export_lembur;
    private javax.swing.JButton button_input_absen_manual;
    private javax.swing.JButton button_input_jam_pulang;
    private javax.swing.JButton button_input_pengurang_lembur;
    private javax.swing.JButton button_insert_jadwal;
    private javax.swing.JButton button_insert_jadwal1;
    private javax.swing.JButton button_load;
    private javax.swing.JButton button_refresh;
    private javax.swing.JButton button_refresh1;
    private javax.swing.JButton button_save_data_fix;
    private javax.swing.JButton button_save_data_shift_malam;
    private javax.swing.JButton button_slip_harian;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTextArea jTextArea3;
    private javax.swing.JTextArea jTextArea4;
    private javax.swing.JLabel label_data_ijin_tidak_valid;
    private javax.swing.JLabel label_data_lembur_tidak_valid;
    private javax.swing.JLabel label_total_data;
    private javax.swing.JLabel label_total_data_jadwal;
    private javax.swing.JLabel label_total_gaji;
    private javax.swing.JLabel label_total_tunjangan_hadir;
    private javax.swing.JLabel label_total_upah_lembur;
    private javax.swing.JTable tabel_data_jadwal;
    private javax.swing.JTable tabel_data_lembur_security;
    private javax.swing.JTextField txt_bonus_tbt;
    private javax.swing.JTextField txt_potongan_bpjs;
    private javax.swing.JTextField txt_potongan_bpjs_tk;
    private javax.swing.JTextField txt_potongan_transport;
    private javax.swing.JTextField txt_search_NamaKaryawan;
    private javax.swing.JTextField txt_search_bagian;
    private javax.swing.JTextField txt_search_jadwal_byNamaKaryawan;
    private javax.swing.JTextField txt_search_karyawan;
    private javax.swing.JTextField txt_search_pin;
    // End of variables declaration//GEN-END:variables
}
