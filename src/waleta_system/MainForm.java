package waleta_system;

import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import waleta_system.SubWaleta.JFrame_TV_Sub;
import waleta_system.Manajemen.JFrame_TV_WIP;
import java.sql.*;
import java.text.DecimalFormat;
import javax.swing.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;
import waleta_system.Class.AksesMenu;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;
import waleta_system.RND.JFrame_Timer;

public class MainForm extends javax.swing.JFrame {

    public static String user, Login_idPegawai, Login_NamaPegawai, Login_Posisi, Login_namaBagian, Login_Departemen;
    public static int Login_kodeBagian;
    public static List<AksesMenu.Akses> dataMenu;

    public MainForm(String user, String Login_idPegawai, String Login_NamaPegawai, int Login_kodeBagian, String Login_namaBagian, String Login_Departemen, String Login_Posisi, List<AksesMenu.Akses> dataMenu) throws ClassNotFoundException {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        initComponents();
        jMenuBar1.remove(jMenu_User);
        jMenuBar1.add(Box.createHorizontalGlue());
        jMenuBar1.add(jMenu_User);
        MainForm.user = user;
        MainForm.Login_idPegawai = Login_idPegawai;
        MainForm.Login_NamaPegawai = Login_NamaPegawai;
        MainForm.Login_kodeBagian = Login_kodeBagian;
        MainForm.Login_namaBagian = Login_namaBagian;
        MainForm.Login_Departemen = Login_Departemen;
        MainForm.Login_Posisi = Login_Posisi;
        MainForm.dataMenu = dataMenu;
        jMenu_User.setText("User Login : " + user);

        refresh_StokReproses();
        Refresh_StokBaku_Unworkable();
        Refresh_StokQC_Hold();
        refreshTable_detailLP_WIP();
        refreshTable_data_master_dokumen();
        if (!MainForm.Login_Posisi.contains("STAFF")) {
            jMenuItem_Pengajuan_naikLevel.setVisible(false);
            jMenuItem_DataKaryawanViewOnly.setVisible(false);
        } else {
            jMenuItem_Pengajuan_naikLevel.setVisible(true);
            jMenuItem_DataKaryawanViewOnly.setVisible(true);
        }

        if (MainForm.Login_namaBagian.contains("KADEP") || MainForm.Login_namaBagian.contains("OPERATIONAL MANAGER") || MainForm.Login_idPegawai.equals("20180102221")) {
            jPanel_Butuh_Acc.setVisible(true);
            refresh_Butuh_Acc();
        } else {
            jPanel_Butuh_Acc.setVisible(false);
        }

        if (MainForm.Login_idPegawai.equals("20180102221")) {//SEBASTIAN
            jMenuItem_Keu_LemburStaff.setVisible(true);
        }

        if (label_warning_stok_baku.isVisible() || label_warning_stok_reproses.isVisible() || label_warning_stok_qchold.isVisible()) {
            jPanel_need_attention.setVisible(true);
        } else {
            jPanel_need_attention.setVisible(false);
        }

    }

    public void refresh_StokReproses() {
        try {
            float total_reproses = 0, total_repacking = 0;
            String sql = "SELECT SUM(`tb_box_bahan_jadi`.`berat`) AS 'stok_reproses', SUM(IF(`tb_reproses`.`status` = 'FINISHED', `tb_box_bahan_jadi`.`berat`, 0)) AS 'stok_repacking' "
                    + "FROM `tb_box_bahan_jadi` "
                    + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_grade_bahan_jadi`.`kode` = `tb_box_bahan_jadi`.`kode_grade_bahan_jadi` "
                    + "LEFT JOIN `tb_tutupan_grading` ON `tb_box_bahan_jadi`.`no_tutupan` = `tb_tutupan_grading`.`kode_tutupan`"
                    + "LEFT JOIN `tb_reproses` ON `tb_reproses`.`no_box` = `tb_box_bahan_jadi`.`no_box`\n"
                    + "WHERE `lokasi_terakhir` = 'GRADING' AND (`tb_tutupan_grading`.`status_box` = 'SELESAI' OR `tb_tutupan_grading`.`status_box` IS NULL) "
                    + "AND `kategori_jual` LIKE 'REPROSES%' ";
            PreparedStatement pst = Utility.db.getConnection().prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                total_reproses = rs.getFloat("stok_reproses");
                total_repacking = rs.getFloat("stok_repacking");
            }
            total_reproses = total_reproses - total_repacking;
            label_warning_stok_reproses.setText("Stok Reproses Barang Jadi > 200Kg : " + Math.round(total_reproses / 1000d) + " Kg");
            if (Math.round(total_reproses / 1000d) > 200) {
                label_warning_stok_reproses.setForeground(Color.red);
            } else {
                label_warning_stok_reproses.setForeground(Color.green);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void Refresh_StokBaku_Unworkable() {
        try {
            ResultSet rs_grading, rs_jual, rs_lp, rs_cmp;
            int gram_masuk = 0, gram_lp = 0, gram_keluar = 0, gram_cmp = 0;
            float stok_jual = 0;
            String sql = "SELECT `kode_grade`, `jenis_bentuk`, `jenis_bulu`, `kategori_proses` "
                    + "FROM `tb_grade_bahan_baku` "
                    + "WHERE `kategori_proses` = 'JUAL'";
            PreparedStatement pst = Utility.db.getConnection().prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                String sql_grading = "SELECT SUM(`tb_grading_bahan_baku`.`total_berat`) AS 'total_berat' "
                        + "FROM `tb_grading_bahan_baku` "
                        + "LEFT JOIN `tb_bahan_baku_masuk` ON `tb_grading_bahan_baku`.`no_kartu_waleta` = `tb_bahan_baku_masuk`.`no_kartu_waleta`"
                        + "WHERE `kode_grade` = '" + rs.getString("kode_grade") + "' AND `tgl_masuk`<=CURRENT_DATE()";
                rs_grading = Utility.db.getStatement().executeQuery(sql_grading);
                if (rs_grading.next()) {
                    gram_masuk = rs_grading.getInt("total_berat");
                }
                String sql_lp = "SELECT SUM(`tb_laporan_produksi`.`berat_basah`) AS 'berat' "
                        + "FROM `tb_laporan_produksi` "
                        + "LEFT JOIN `tb_grading_bahan_baku` ON `tb_laporan_produksi`.`kode_grade` = `tb_grading_bahan_baku`.`kode_grade` AND `tb_laporan_produksi`.`no_kartu_waleta` = `tb_grading_bahan_baku`.`no_kartu_waleta`"
                        + "WHERE `tb_laporan_produksi`.`kode_grade` = '" + rs.getString("kode_grade") + "'  AND `tanggal_lp`<=CURRENT_DATE()";
                rs_lp = Utility.db.getStatement().executeQuery(sql_lp);
                if (rs_lp.next()) {
                    gram_lp = rs_lp.getInt("berat");
                }
                String sql_jual = "SELECT SUM(`total_berat_keluar`) AS 'berat' "
                        + "FROM `tb_bahan_baku_keluar` "
                        + "LEFT JOIN `tb_bahan_baku_keluar1` ON `tb_bahan_baku_keluar`.`kode_pengeluaran` = `tb_bahan_baku_keluar1`.`kode_pengeluaran` "
                        + "LEFT JOIN `tb_grading_bahan_baku` ON (`tb_grading_bahan_baku`.`no_kartu_waleta` = `tb_bahan_baku_keluar`.`no_kartu_waleta` AND `tb_grading_bahan_baku`.`kode_grade` = `tb_bahan_baku_keluar`.`kode_grade`)"
                        + "WHERE `tb_bahan_baku_keluar`.`kode_grade` = '" + rs.getString("kode_grade") + "'  AND `tgl_keluar`<=CURRENT_DATE()";
                rs_jual = Utility.db.getStatement().executeQuery(sql_jual);
                if (rs_jual.next()) {
                    gram_keluar = rs_jual.getInt("berat");
                }
                String sql_cmp = "SELECT SUM(`tb_kartu_cmp_detail`.`gram`) AS 'berat_cmp' "
                        + "FROM `tb_kartu_cmp_detail`\n"
                        + "LEFT JOIN `tb_kartu_cmp` ON `tb_kartu_cmp_detail`.`kode_kartu_cmp` = `tb_kartu_cmp`.`kode_kartu_cmp`"
                        + "LEFT JOIN `tb_grading_bahan_baku` ON `tb_kartu_cmp_detail`.`no_grading` = `tb_grading_bahan_baku`.`no_grading`\n"
                        + "WHERE `tb_grading_bahan_baku`.`kode_grade` = '" + rs.getString("kode_grade") + "' AND `tanggal`<=CURRENT_DATE()";
                rs_cmp = Utility.db.getStatement().executeQuery(sql_cmp);
                if (rs_cmp.next()) {
                    gram_cmp = rs_cmp.getInt("berat_cmp");
                }

                float stok = gram_masuk - (gram_lp + gram_keluar + gram_cmp);
                stok_jual = stok_jual + stok;
            }
            label_warning_stok_baku.setText("Stok Unworkable Raw Material > 30Kg : " + Math.round(stok_jual / 1000d) + " Kg");
            if (Math.round(stok_jual / 1000d) > 30) {
                label_warning_stok_baku.setForeground(Color.red);
            } else {
                label_warning_stok_baku.setForeground(Color.green);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void Refresh_StokQC_Hold() {
        try {
            int qc_hold_lp = 0, qc_hold_gram = 0;
            String sql = "SELECT COUNT(`tb_laporan_produksi`.`no_laporan_produksi`) AS 'lp_hold', SUM(`berat_basah`) AS 'berat_hold' "
                    + "FROM `tb_laporan_produksi` "
                    + "LEFT JOIN `tb_lab_laporan_produksi` ON `tb_lab_laporan_produksi`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    + "WHERE `tgl_uji` IS NOT NULL AND `tgl_selesai` IS NULL AND `status` = 'HOLD/NON GNS'";
            PreparedStatement pst = Utility.db.getConnection().prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                qc_hold_lp = rs.getInt("lp_hold");
                qc_hold_gram = rs.getInt("berat_hold");
            }
            label_warning_stok_qchold.setText("Stok QC Hold > 30Kg : " + Math.round(qc_hold_gram / 1000d) + " Kg (" + qc_hold_lp + "LP)");
            if (Math.round(qc_hold_gram / 1000d) > 30) {
                label_warning_stok_qchold.setForeground(Color.red);
            } else {
                label_warning_stok_qchold.setForeground(Color.green);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refresh_Butuh_Acc() {
        String filter_departemen = MainForm.Login_Departemen == null ? "" : MainForm.Login_Departemen;
        try {
            DefaultTableModel model = (DefaultTableModel) Tabel_butuh_Acc_Lembur.getModel();
            model.setRowCount(0);
            String sql = "SELECT `tanggal_lembur`, `jenis_spl`, `kode_departemen`, `disetujui`, `diketahui` \n"
                    + "FROM `tb_surat_lembur` \n"
                    + "WHERE \n"
                    + "`tanggal_surat` > '2023-11-01'\n"
                    + "AND `kode_departemen` LIKE '%" + filter_departemen + "%'\n"
                    + "AND \n"
                    + "(`disetujui` IS NULL OR `diketahui` IS NULL)  \n"
                    + "ORDER BY `tb_surat_lembur`.`tanggal_lembur` DESC";
            ResultSet rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[5];
            while (rs.next()) {
                row[0] = rs.getDate("tanggal_lembur");
                row[1] = rs.getString("jenis_spl");
                row[2] = rs.getString("kode_departemen");
                row[3] = rs.getString("disetujui") != null;
                row[4] = rs.getString("diketahui") != null;
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(Tabel_butuh_Acc_Lembur);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            DefaultTableModel model = (DefaultTableModel) Tabel_butuh_Acc_PindahBagian.getModel();
            model.setRowCount(0);
            String sql = "SELECT `tanggal_input`, `tb_karyawan`.`nama_pegawai`, `tb_bagian`.`kode_departemen`, `jam_disetujui` \n"
                    + "FROM `tb_form_pindah_grup` \n"
                    + "LEFT JOIN `tb_karyawan` ON `tb_form_pindah_grup`.`id_pegawai` = `tb_karyawan`.`id_pegawai`\n"
                    + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`\n"
                    + "WHERE "
                    + "`jam_disetujui` IS NULL\n"
                    + "AND `kode_departemen` LIKE '%" + filter_departemen + "%'\n"
                    + "ORDER BY `tanggal_input` DESC";
            ResultSet rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[5];
            while (rs.next()) {
                row[0] = rs.getDate("tanggal_input");
                row[1] = rs.getString("nama_pegawai");
                row[2] = rs.getString("kode_departemen");
                row[3] = rs.getString("jam_disetujui") != null;
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(Tabel_butuh_Acc_PindahBagian);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            DefaultTableModel model = (DefaultTableModel) Tabel_butuh_Acc_PengajuanAlat.getModel();
            model.setRowCount(0);
            String sql = "SELECT `tanggal_pengajuan`, `nama_barang`, `departemen`, `diketahui_kadep`, `diketahui`\n"
                    + "FROM `tb_aset_pengajuan` \n"
                    + "LEFT JOIN `tb_karyawan` ON `tb_aset_pengajuan`.`diajukan` = `tb_karyawan`.`id_pegawai`\n"
                    + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`\n"
                    + "WHERE \n"
                    + "(`diketahui_kadep` IS NULL OR `diketahui` IS NULL)\n"
                    + "AND `disetujui` IS NULL \n"
                    + "AND `tb_bagian`.`kode_departemen` LIKE '%" + filter_departemen + "%'\n"
                    + "ORDER BY `tanggal_pengajuan` DESC";
            ResultSet rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[5];
            while (rs.next()) {
                row[0] = rs.getDate("tanggal_pengajuan");
                row[1] = rs.getString("nama_barang");
                row[2] = rs.getString("departemen");
                row[3] = rs.getString("diketahui_kadep") != null;
                row[4] = rs.getString("diketahui") != null;
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(Tabel_butuh_Acc_PengajuanAlat);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

//    public void refresh_TabelUltah() {
//        try {
//            DefaultTableModel model = (DefaultTableModel) Tabel_ultah.getModel();
//            model.setRowCount(0);
//            String sql = "SELECT `nama_pegawai`,`tanggal_lahir`, `tb_bagian`.`nama_bagian`, `posisi`, \n"
//                    + " FLOOR(DATEDIFF(DATE(NOW()),`tanggal_lahir`) / 365.25) AS age_now,\n"
//                    + " FLOOR(DATEDIFF(DATE_ADD(DATE(NOW()),INTERVAL 7 DAY),`tanggal_lahir`) / 365.25) AS age_future \n"
//                    + "FROM `tb_karyawan`\n"
//                    + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian` \n"
//                    + "WHERE 1 = (FLOOR(DATEDIFF(DATE_ADD(DATE(NOW()),INTERVAL 7 DAY),`tanggal_lahir`) / 365.25)) - (FLOOR(DATEDIFF(DATE(NOW()),`tanggal_lahir`) / 365.25))\n"
//                    + "AND `status` = 'IN'  \n"
//                    + "ORDER BY MONTH(`tanggal_lahir`), DAY(`tanggal_lahir`)";
//            ResultSet rs = Utility.db.getStatement().executeQuery(sql);
//            Object[] row = new Object[5];
//            while (rs.next()) {
//                row[0] = rs.getString("nama_pegawai");
//                row[1] = new SimpleDateFormat("dd-MMM").format(rs.getDate("tanggal_lahir"));
//                row[2] = rs.getString("nama_bagian");
//                row[3] = rs.getString("posisi");
//                row[4] = rs.getInt("age_future");
//                model.addRow(row);
//            }
//            ColumnsAutoSizer.sizeColumnsToFit(Tabel_ultah);
//        } catch (Exception ex) {
//            JOptionPane.showMessageDialog(this, ex);
//            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    public void refreshTable_detailLP_WIP() {
        try {
            DecimalFormat decimalFormat = new DecimalFormat();
            float total_keping = 0, total_gram = 0;
            DefaultTableModel model = (DefaultTableModel) tabel_data_LP_percobaan.getModel();
            model.setRowCount(0);
            String query = "SELECT `tb_laporan_produksi`.`no_laporan_produksi`, `kode_grade`, `ruangan`, `memo_lp`, `jumlah_keping`, `berat_basah`, "
                    + "`tanggal_lp`, `tanggal_rendam`, `tgl_masuk_cuci`, `tgl_mulai_cabut`, `tgl_setor_cabut`, `tgl_mulai_cetak`, `tgl_selesai_cetak`, `tgl_masuk_f2`, `tgl_setor_f2`, `tgl_masuk` AS 'tgl_masuk_qc', `tgl_selesai` AS 'tgl_selesai_qc', `tanggal_grading`, `tb_tutupan_grading`.`tgl_statusBox` "
                    + "FROM `tb_laporan_produksi` \n"
                    + "LEFT JOIN `tb_rendam` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_rendam`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_cuci` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_cuci`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_cabut` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_cabut`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_cetak` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_cetak`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_finishing_2` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_finishing_2`.`no_laporan_produksi`"
                    + "LEFT JOIN `tb_lab_laporan_produksi` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_lab_laporan_produksi`.`no_laporan_produksi`"
                    + "LEFT JOIN `tb_bahan_jadi_masuk` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_bahan_jadi_masuk`.`kode_asal`\n"
                    + "LEFT JOIN `tb_tutupan_grading` ON `tb_bahan_jadi_masuk`.`kode_tutupan` = `tb_tutupan_grading`.`kode_tutupan`\n"
                    + "WHERE YEAR(`tanggal_lp`)>=2018 AND `berat_basah` > 0 AND `memo_lp` LIKE '%PCB%' \n"
                    + "AND `tanggal_grading` IS NULL"
                    + " GROUP BY `tb_laporan_produksi`.`no_laporan_produksi`";
            ResultSet result = Utility.db.getStatement().executeQuery(query);
//            System.out.println(sql);
            Object[] row = new Object[10];
            while (result.next()) {
                row[0] = result.getString("no_laporan_produksi");
                row[1] = result.getString("ruangan");
                row[2] = result.getString("kode_grade");
                row[3] = result.getString("memo_lp");
                row[4] = result.getFloat("jumlah_keping");
                row[5] = result.getFloat("berat_basah");
                String posisi = "";
                if (result.getDate("tanggal_rendam") == null) {
                    posisi = "RENDAM";
                } else if (result.getDate("tgl_masuk_cuci") == null) {
                    posisi = "CUCI";
                } else if (result.getDate("tgl_mulai_cabut") == null) {
                    posisi = "SELESAI CUCI";
                } else if (result.getDate("tgl_setor_cabut") == null) {
                    posisi = "CABUT";
                } else if (result.getDate("tgl_mulai_cetak") == null) {
                    posisi = "SELESAI CABUT";
                } else if (result.getDate("tgl_selesai_cetak") == null) {
                    posisi = "CETAK";
                } else if (result.getDate("tgl_masuk_f2") == null) {
                    posisi = "SELESAI CETAK";
                } else if (result.getDate("tgl_setor_f2") == null) {
                    posisi = "FINISHING 2";
                } else if (result.getDate("tgl_masuk_qc") == null) {
                    posisi = "SELESAI FINISHING";
                } else if (result.getDate("tgl_selesai_qc") == null) {
                    posisi = "QC NITRIT / TREATMENT";
                } else if (result.getDate("tanggal_grading") == null) {
                    posisi = "SELESAI QC";
                } else if (result.getDate("tgl_statusBox") == null) {
                    posisi = "GRADING GBJ";
                }
                row[6] = posisi;
                model.addRow(row);
                total_keping = total_keping + result.getFloat("jumlah_keping");
                total_gram = total_gram + result.getFloat("berat_basah");
            }
            ColumnsAutoSizer.sizeColumnsToFit(tabel_data_LP_percobaan);
            label_total_keping_lp_percobaan.setText(decimalFormat.format(total_keping));
            label_total_gram_lp_percobaan.setText(decimalFormat.format(total_gram));
            label_total_lp_percobaan.setText(Integer.toString(tabel_data_LP_percobaan.getRowCount()));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void refreshTable_data_master_dokumen() {
        try {
            DefaultTableModel model = (DefaultTableModel) Table_dokumen_jatuh_tempo.getModel();
            model.setRowCount(0);
            String kode_departemen = MainForm.Login_Departemen;
            String filter_departemen = "";
            if (kode_departemen != null) {
                filter_departemen = "AND `kode_departemen` = '" + kode_departemen + "' \n";
            }
            String sql = "SELECT `tb_dokumen_qc`.`kode_dokumen`, `nama_dokumen`, `tempat_pengujian`, `jenis_dokumen`, `keterangan`, `kode_departemen`, `masa_berlaku`, \n"
                    + "MAX(`tanggal_kadaluarsa`) AS 'tanggal_kadaluarsa', DATEDIFF(MAX(`tanggal_kadaluarsa`), CURRENT_DATE()) AS 'hari_jatuh_tempo'"
                    + "FROM `tb_dokumen_qc` \n"
                    + "LEFT JOIN `tb_dokumen_qc_update` ON `tb_dokumen_qc`.`kode_dokumen` = `tb_dokumen_qc_update`.`kode_dokumen`\n"
                    + "WHERE "
                    + "`tb_dokumen_qc_update`.`kode_dokumen` IS NOT NULL \n"
                    + filter_departemen
                    + "GROUP BY `tb_dokumen_qc`.`kode_dokumen`\n"
                    + "HAVING `hari_jatuh_tempo` < 30 \n"
                    + "ORDER BY `hari_jatuh_tempo` ASC";
            ResultSet rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[5];
            while (rs.next()) {
                row[0] = rs.getString("kode_dokumen");
                row[1] = rs.getString("nama_dokumen");
                row[2] = rs.getDate("tanggal_kadaluarsa");
                row[3] = convertDays(rs.getInt("hari_jatuh_tempo"));
                row[4] = rs.getInt("hari_jatuh_tempo");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_dokumen_jatuh_tempo);

            Table_dokumen_jatuh_tempo.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if (isSelected) {
                        comp.setBackground(Table_dokumen_jatuh_tempo.getSelectionBackground());
                        comp.setForeground(Table_dokumen_jatuh_tempo.getSelectionForeground());
                    } else {
                        if ((int) Table_dokumen_jatuh_tempo.getValueAt(row, 4) < 0) {
                            comp.setBackground(Color.RED);
                            comp.setForeground(Color.WHITE);
                        } else if ((int) Table_dokumen_jatuh_tempo.getValueAt(row, 4) < 30) {
                            comp.setBackground(Color.ORANGE);
                            comp.setForeground(Color.BLACK);
                        } else {
                            comp.setBackground(Table_dokumen_jatuh_tempo.getBackground());
                            comp.setForeground(Table_dokumen_jatuh_tempo.getForeground());
                        }
                    }
                    return comp;
                }
            });

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static String convertDays(int totalDays) {
        boolean isNegative = totalDays < 0;
        totalDays = Math.abs(totalDays);

        int years = totalDays / 365;
        int remainingDays = totalDays % 365;
        int months = remainingDays / 30;
        int days = remainingDays % 30;

        StringBuilder result = new StringBuilder();
        if (years > 0) {
            result.append(years).append(" Tahun ");
        }
        if (months > 0) {
            result.append(months).append(" Bulan ");
        }
        if (days > 0) {
            result.append(days).append(" Hari");
        }

        // Trim trailing space
        String finalResult = result.toString().trim();
        
        if (isNegative) {
            finalResult = "-(" + finalResult + ")";
        }
        
        return finalResult;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        main_panel = new javax.swing.JPanel();
        jPanel_Home = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jPanel_need_attention = new javax.swing.JPanel();
        label_warning_stok_reproses = new javax.swing.JLabel();
        label_warning_stok_baku = new javax.swing.JLabel();
        label_warning_stok_qchold = new javax.swing.JLabel();
        label_need_attention = new javax.swing.JLabel();
        jPanel_Butuh_Acc = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        Tabel_butuh_Acc_Lembur = new javax.swing.JTable();
        jScrollPane4 = new javax.swing.JScrollPane();
        Tabel_butuh_Acc_PindahBagian = new javax.swing.JTable();
        jScrollPane5 = new javax.swing.JScrollPane();
        Tabel_butuh_Acc_PengajuanAlat = new javax.swing.JTable();
        Button_refresh_butuh_Acc = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tabel_data_LP_percobaan = new javax.swing.JTable();
        jLabel8 = new javax.swing.JLabel();
        label_total_lp_percobaan = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        label_total_keping_lp_percobaan = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        label_total_gram_lp_percobaan = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jPanel_Butuh_Acc1 = new javax.swing.JPanel();
        Button_refresh_dokumen_jatuh_tempo = new javax.swing.JButton();
        jScrollPane8 = new javax.swing.JScrollPane();
        Table_dokumen_jatuh_tempo = new javax.swing.JTable();
        jPanel_DataDepartemen1 = new waleta_system.HRD.JPanel_DataDepartemen();
        jPanel_Traceability21 = new waleta_system.JPanel_Traceability2();
        jPanel_Ijin_keluar1 = new waleta_system.HRD.JPanel_Ijin_keluar();
        jPanel_Data_Karyawan1 = new waleta_system.HRD.JPanel_Data_Karyawan();
        jPanel_Harga_BahanBaku1 = new waleta_system.Finance.JPanel_Harga_BahanBaku();
        jPanel_DataLembur1 = new waleta_system.HRD.JPanel_DataLembur();
        jPanel_DataCabut1 = new waleta_system.Panel_produksi.JPanel_DataCabut();
        jPanel_DataCetak1 = new waleta_system.Panel_produksi.JPanel_DataCetak();
        jPanel_DataRendam1 = new waleta_system.Panel_produksi.JPanel_DataRendam();
        jPanel_DataCuci1 = new waleta_system.Panel_produksi.JPanel_DataCuci();
        jPanel_ProgressLP1 = new waleta_system.Panel_produksi.JPanel_ProgressLP();
        jPanel_Supplier1 = new waleta_system.BahanBaku.JPanel_Supplier();
        jPanel_DataBahanBaku1 = new waleta_system.BahanBaku.JPanel_DataBahanBaku();
        jPanel_Customer1 = new waleta_system.BahanBaku.JPanel_Customer();
        jPanel_RumahBurung1 = new waleta_system.BahanBaku.JPanel_RumahBurung();
        jPanel_BahanBakuMasuk1 = new waleta_system.BahanBaku.JPanel_BahanBakuMasuk();
        jPanel_Laporan_Produksi1 = new waleta_system.BahanBaku.JPanel_Laporan_Produksi();
        jPanel_BahanBakuKeluar1 = new waleta_system.BahanBaku.JPanel_BahanBakuKeluar();
        jPanel_BahanJadiMasuk1 = new waleta_system.BahanJadi.JPanel_BahanJadiMasuk();
        jPanel_TutupanGradingBahanJadi1 = new waleta_system.BahanJadi.JPanel_TutupanGradingBahanJadi();
        jPanel_BonusMangkok1 = new waleta_system.Finance.JPanel_BonusMangkok();
        jPanel_BoxBahanJadi1 = new waleta_system.BahanJadi.JPanel_BoxBahanJadi();
        jPanel_DataPacking1 = new waleta_system.Packing.JPanel_DataPacking();
        jPanel_Data_Buyer1 = new waleta_system.Packing.JPanel_Data_Buyer();
        jPanel_StockBahanJadi1 = new waleta_system.BahanJadi.JPanel_StockBahanJadi();
        jPanel_pengiriman1 = new waleta_system.Packing.JPanel_pengiriman();
        jPanel_DataBahanBaku_Finance1 = new waleta_system.Finance.JPanel_DataBahanBaku_Finance();
        jPanel_DataBahanBaku_PerGrade1 = new waleta_system.Finance.JPanel_DataBahanBaku_PerGrade();
        jPanel_Harga_LaporanProduksi1 = new waleta_system.Finance.JPanel_Harga_LaporanProduksi();
        jPanel_BonusGrading1 = new waleta_system.BahanJadi.JPanel_BonusGrading();
        jPanel_LaporanProduksi_BahanJadi1 = new waleta_system.BahanJadi.JPanel_LaporanProduksi_BahanJadi();
        jPanel_PembelianBahanJadi1 = new waleta_system.BahanJadi.JPanel_PembelianBahanJadi();
        jPanel_Data_TglLibur1 = new waleta_system.HRD.JPanel_Data_TglLibur();
        jPanel_AdjustmentBaku1 = new waleta_system.BahanBaku.JPanel_AdjustmentBaku();
        jPanel_DataTemanBawaTeman1 = new waleta_system.HRD.JPanel_DataTemanBawaTeman();
        jPanel_DataPencetak1 = new waleta_system.Panel_produksi.JPanel_DataPencetak();
        jPanel_Harga_BahanBakuKeluar1 = new waleta_system.Finance.JPanel_Harga_BahanBakuKeluar();
        jPanel_Harga_PembelianBarangJadi1 = new waleta_system.Finance.JPanel_Harga_PembelianBarangJadi();
        jPanel_Kartu_Campuran1 = new waleta_system.BahanBaku.JPanel_Kartu_Campuran();
        jPanel_LaporanProduksi_Keuangan1 = new waleta_system.Finance.JPanel_LaporanProduksi_Keuangan();
        jPanel_Harga_Kartu_Campuran1 = new waleta_system.Finance.JPanel_Harga_Kartu_Campuran();
        jPanel_BiayaTenagaKerja1 = new waleta_system.Finance.JPanel_BiayaTenagaKerja();
        jPanel_Hari_kerja1 = new waleta_system.Finance.JPanel_Hari_kerja();
        jPanel_DataPencabut1 = new waleta_system.Finance.JPanel_DataPencabut();
        jPanel_DataKinerjaF21 = new waleta_system.Panel_produksi.JPanel_DataKinerjaF2();
        jPanel_Rekap_BiayaLP1 = new waleta_system.Manajemen.JPanel_Rekap_BiayaLP();
        jPanel_Biaya_Ekspor1 = new waleta_system.Finance.JPanel_Biaya_Ekspor();
        jPanel_Biaya1 = new waleta_system.Finance.JPanel_Biaya();
        jPanel_Harga_BahanBaku_Esta1 = new waleta_system.Finance.JPanel_Harga_BahanBaku_Esta();
        jPanel_SPK1 = new waleta_system.Packing.JPanel_SPK();
        jPanel_Data_Ekspor1 = new waleta_system.Finance.JPanel_Data_Ekspor();
        jPanel_DataScanKeping1 = new waleta_system.Packing.JPanel_DataScanKeping();
        jPanel_Payment_Report1 = new waleta_system.Finance.JPanel_Payment_Report();
        jPanel_PriceList_GradeBJ1 = new waleta_system.BahanJadi.JPanel_PriceList_GradeBJ();
        jPanel_DataCashOnBank1 = new waleta_system.Finance.JPanel_DataCashOnBank();
        jPanel_Neraca1 = new waleta_system.Finance.JPanel_Neraca();
        jPanel_StokOpnameGBJ1 = new waleta_system.BahanJadi.JPanel_StokOpnameGBJ();
        jPanel_Pembelian_Baku1 = new waleta_system.BahanBaku.JPanel_Pembelian_Baku();
        jPanel_Barcode_Pengiriman1 = new waleta_system.Packing.JPanel_Barcode_Pengiriman();
        jPanel_HargaBahanBaku1 = new waleta_system.Manajemen.JPanel_HargaBahanBaku();
        jPanel_Data_ARAP_Esta1 = new waleta_system.Finance.JPanel_Data_ARAP_Esta();
        jPanel_BonusPetikRSB1 = new waleta_system.Manajemen.JPanel_BonusPetikRSB();
        jPanel_Rekap_Biaya_per_KartuBaku1 = new waleta_system.Manajemen.JPanel_Rekap_Biaya_per_KartuBaku();
        jPanel_Reproses1 = new waleta_system.BahanJadi.JPanel_Reproses();
        jPanel_Data_Jam_Kerja1 = new waleta_system.HRD.JPanel_Data_Jam_Kerja();
        jPanel_Lembur_Karyawan1 = new waleta_system.Finance.JPanel_Lembur_Karyawan();
        jPanel_payrol_harian1 = new waleta_system.Finance.JPanel_payrol_harian();
        jPanel_Data_JalurJemputan1 = new waleta_system.HRD.JPanel_Data_JalurJemputan();
        jPanel_DataKaryawan1 = new waleta_system.Finance.JPanel_DataKaryawan();
        jPanel_BahanBakuMasuk_Cheat1 = new waleta_system.BahanBaku.JPanel_BahanBakuMasuk_Cheat();
        jPanel_Absen_Keuangan1 = new waleta_system.Finance.JPanel_Absen_Keuangan();
        jPanel_PiutangKaryawan1 = new waleta_system.Finance.JPanel_PiutangKaryawan();
        jPanel_TimBantu1 = new waleta_system.Finance.JPanel_TimBantu();
        jPanel_GradingLP_Tutupan1 = new waleta_system.Finance.JPanel_GradingLP_Tutupan();
        jPanel_Suhu_dan_Kelembapan1 = new waleta_system.Maintenance.JPanel_Suhu_dan_Kelembapan();
        jPanel_Data_User1 = new waleta_system.User.JPanel_Data_User();
        jPanel_Dokumen_KH1 = new waleta_system.BahanBaku.JPanel_Dokumen_KH();
        jPanel_Data_Karyawan_Sub1 = new waleta_system.SubWaleta.JPanel_Data_Karyawan_Sub();
        jPanel_DataCabutSub1 = new waleta_system.SubWaleta.JPanel_DataCabutSub();
        jPanel_StokOpnameGBJ_RekapGrade1 = new waleta_system.Finance.JPanel_StokOpnameGBJ_RekapGrade();
        jPanel_DataTreatment1 = new waleta_system.QC.JPanel_DataTreatment();
        jPanel_Lab_BahanBaku1 = new waleta_system.QC.JPanel_Lab_BahanBaku();
        jPanel_Lab_BarangJadi1 = new waleta_system.QC.JPanel_Lab_BarangJadi();
        jPanel_Lab_LaporanProduksi1 = new waleta_system.QC.JPanel_Lab_LaporanProduksi();
        jPanel_Lab_Uji_Pengiriman1 = new waleta_system.QC.JPanel_Lab_Uji_Pengiriman();
        jPanel_Sub_Waleta1 = new waleta_system.SubWaleta.JPanel_Sub_Waleta();
        jPanel_Dokumen_QC1 = new waleta_system.QC.JPanel_Dokumen_QC();
        jPanel_BonusKecepatanF21 = new waleta_system.Finance.JPanel_BonusKecepatanF2();
        jPanel_Traceability1 = new waleta_system.JPanel_Traceability();
        jPanel_DataCetakSub1 = new waleta_system.SubWaleta.JPanel_DataCetakSub();
        jPanel_Data_KaryawanLama_Masuk1 = new waleta_system.HRD.JPanel_Data_KaryawanLama_Masuk();
        jPanel_Absen_Cuti1 = new waleta_system.HRD.JPanel_Absen_Cuti();
        jPanel_Laporan_Produksi_Sesekan1 = new waleta_system.BahanBaku.JPanel_Laporan_Produksi_Sesekan();
        jPanel_StokOpname_WIP1 = new waleta_system.Panel_produksi.JPanel_StokOpname_WIP();
        jPanel_GradeBahanBaku1 = new waleta_system.BahanBaku.JPanel_GradeBahanBaku();
        jPanel_DataGradeBahanJadi1 = new waleta_system.BahanJadi.JPanel_DataGradeBahanJadi();
        jPanel_Tarif_upah_sub1 = new waleta_system.SubWaleta.JPanel_Tarif_upah_sub();
        jPanel_Isu_Produksi1 = new waleta_system.JPanel_Isu_Produksi();
        jPanel_PiutangKaryawan_sub1 = new waleta_system.SubWaleta.JPanel_PiutangKaryawan_sub();
        jPanel_payrol_data1 = new waleta_system.Finance.JPanel_payrol_data();
        jPanel_DataKinerjaPacking1 = new waleta_system.Packing.JPanel_DataKinerjaPacking();
        jPanel_DataLPSesekanSub1 = new waleta_system.SubWaleta.JPanel_DataLPSesekanSub();
        jPanel_MasterUpah1 = new waleta_system.BahanBaku.JPanel_MasterUpah();
        jPanel_Data_Karyawan_wltsub1 = new waleta_system.HRD.JPanel_Data_Karyawan_wltsub();
        jPanel_Laporan_Produksi_Sapon1 = new waleta_system.BahanBaku.JPanel_Laporan_Produksi_Sapon();
        jPanel_PenggajianSub1 = new waleta_system.SubWaleta.JPanel_PenggajianSub();
        jPanel_Laporan_Produksi11 = new waleta_system.BahanBaku.JPanel_Laporan_Produksi1();
        jPanel_Undian21 = new waleta_system.JPanel_Undian2();
        jPanel_Data_personalHygiene1 = new waleta_system.HRD.JPanel_Data_personalHygiene();
        jPanel_ijinToilet1 = new waleta_system.HRD.JPanel_ijinToilet();
        jPanel_DataTRSela1 = new waleta_system.Panel_produksi.JPanel_DataTRSela();
        jPanel_Data_Aset1 = new waleta_system.Maintenance.JPanel_Data_Aset();
        jPanel_SistemPenilaian_Karyawan1 = new waleta_system.HRD.JPanel_SistemPenilaian_Karyawan();
        jPanel_KinerjaKaryawanGrading1 = new waleta_system.BahanJadi.JPanel_KinerjaKaryawanGrading();
        jPanel_DataKaryawanATB1 = new waleta_system.RND.JPanel_DataKaryawanATB();
        jPanel_ProduksiATB1 = new waleta_system.RND.JPanel_ProduksiATB();
        jPanel_DataSaponSub1 = new waleta_system.SubWaleta.JPanel_DataSaponSub();
        jPanel_BonusCabut21 = new waleta_system.Finance.JPanel_BonusCabut2();
        jPanel_ProgressSPKPengiriman1 = new waleta_system.Packing.JPanel_ProgressSPKPengiriman();
        jPanel_Peramalan_gradingBaku1 = new waleta_system.BahanBaku.JPanel_Peramalan_gradingBaku();
        jPanel_LevelGaji1 = new waleta_system.Finance.JPanel_LevelGaji();
        jPanel_Suhu_dan_Kelembapan21 = new waleta_system.Maintenance.JPanel_Suhu_dan_Kelembapan2();
        jPanel_Aset_MasterDataAset1 = new waleta_system.Finance.JPanel_Aset_MasterDataAset();
        jPanel_Aset_NotaPembelian1 = new waleta_system.Finance.JPanel_Aset_NotaPembelian();
        jPanel_PengajuanKenaikanLevelGaji1 = new waleta_system.Finance.JPanel_PengajuanKenaikanLevelGaji();
        jPanel_Lab_Heat_Treatment1 = new waleta_system.QC.JPanel_Lab_Heat_Treatment();
        jPanel_PengajuanKenaikanLevelGaji_ViewOnly1 = new waleta_system.JPanel_PengajuanKenaikanLevelGaji_ViewOnly();
        jPanel_Aset_UnitAset1 = new waleta_system.Finance.JPanel_Aset_UnitAset();
        jPanel_Absensi_Karyawan_ViewOnly1 = new waleta_system.JPanel_Absensi_Karyawan_ViewOnly();
        jPanel_Lembur_Staff1 = new waleta_system.Finance.JPanel_Lembur_Staff();
        jPanel_Absensi_Karyawan1 = new waleta_system.HRD.JPanel_Absensi_Karyawan();
        jPanel_DataPindahBagian1 = new waleta_system.HRD.JPanel_DataPindahBagian();
        jPanel_DataKinerjaCabut1 = new waleta_system.Finance.JPanel_DataKinerjaCabut();
        jPanel_Aset_PengajuanPembelian1 = new waleta_system.Finance.JPanel_Aset_PengajuanPembelian();
        jPanel_Kinerja_Operator_ATB1 = new waleta_system.RND.JPanel_Kinerja_Operator_ATB();
        jPanel_Penilaian_BuluUpah_LP1 = new waleta_system.Panel_produksi.JPanel_Penilaian_BuluUpah_LP();
        jPanel_Data_Karyawan_ViewOnly1 = new waleta_system.JPanel_Data_Karyawan_ViewOnly();
        jPanel_DataBahanKimia1 = new waleta_system.QC.JPanel_DataBahanKimia();
        jPanel_StokOpname_WIP_keuangan1 = new waleta_system.Finance.JPanel_StokOpname_WIP_keuangan();
        jPanel_ProduksiATB_PenilaianLP1 = new waleta_system.RND.JPanel_ProduksiATB_PenilaianLP();
        jPanel_Absensi_Sub1 = new waleta_system.SubWaleta.JPanel_Absensi_Sub();
        jPanel_TutupanGradingBahanJadi_Keuangan1 = new waleta_system.Finance.JPanel_TutupanGradingBahanJadi_Keuangan();
        jPanel_GajiCABUTO1 = new waleta_system.Finance.JPanel_GajiCABUTO();
        jPanel_Lembur_Staff_new1 = new waleta_system.Finance.JPanel_Lembur_Staff_new();
        jPanel_Finishing21 = new waleta_system.Panel_produksi.JPanel_Finishing2();
        jPanel_GajiCetak1 = new waleta_system.Finance.JPanel_GajiCetak();
        jPanel_BoxBahanJadi_Keuangan1 = new waleta_system.Finance.JPanel_BoxBahanJadi_Keuangan();
        jPanel_Data_User_Cabuto1 = new waleta_system.Cabuto.JPanel_Data_User_Cabuto();
        jPanel_Laporan_Produksi_Cabuto1 = new waleta_system.Cabuto.JPanel_Laporan_Produksi_Cabuto();
        jPanel_Data_Order1 = new waleta_system.Cabuto.JPanel_Data_Order();
        jPanel_GradeBarangJadi_Cabuto1 = new waleta_system.Cabuto.JPanel_GradeBarangJadi_Cabuto();
        jPanel_Lembur_ShiftMalam1 = new waleta_system.Finance.JPanel_Lembur_ShiftMalam();
        jPanel_DataKinerjaCetak1 = new waleta_system.Finance.JPanel_DataKinerjaCetak();
        jPanel_Pengiriman_PickUp1 = new waleta_system.Packing.JPanel_Pengiriman_PickUp();
        jPanel_Peramalan_barangjadi21 = new waleta_system.Packing.JPanel_Peramalan_barangjadi2();
        jPanel_DataKinerjaCuci_HC_Kopyok1 = new waleta_system.Panel_produksi.JPanel_DataKinerjaCuci_HC_Kopyok();
        jPanel_Reproses_Sub1 = new waleta_system.SubWaleta.JPanel_Reproses_Sub();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu_File = new javax.swing.JMenu();
        jMenu_Home = new javax.swing.JMenuItem();
        jMenu_Traceability = new javax.swing.JMenuItem();
        jMenu_Traceability2 = new javax.swing.JMenuItem();
        jMenu_SistemUndian = new javax.swing.JMenuItem();
        jMenu_Input_Lembur = new javax.swing.JMenuItem();
        jMenu_pindah_grup = new javax.swing.JMenuItem();
        jMenu_produksi_isu = new javax.swing.JMenuItem();
        jMenu_TV_belumAbsen = new javax.swing.JMenuItem();
        jMenu_TV_IjinBelumKembali = new javax.swing.JMenuItem();
        jMenuItem_pengajuan_cuti_web = new javax.swing.JMenuItem();
        jMenuItem_DataKaryawanViewOnly = new javax.swing.JMenuItem();
        jMenuItem_AbsensiKaryawan = new javax.swing.JMenuItem();
        jMenuItem_FormTidakMasuk = new javax.swing.JMenuItem();
        jMenuItem_Pengajuan_naikLevel = new javax.swing.JMenuItem();
        jMenuItem_Keu_Pengajuan_Pembelian2 = new javax.swing.JMenuItem();
        jMenuItem13 = new javax.swing.JMenuItem();
        jMenuItem_SistemPrintSlip = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenu_Exit = new javax.swing.JMenuItem();
        jMenu_bahan_baku = new javax.swing.JMenu();
        jMenu_baku_supplier = new javax.swing.JMenuItem();
        jMenu_baku_Customer = new javax.swing.JMenuItem();
        jMenu_baku_rumahburung = new javax.swing.JMenuItem();
        jMenu_baku_grade = new javax.swing.JMenuItem();
        jMenu_baku_masuk = new javax.swing.JMenuItem();
        jMenu_baku_masuk_cheat = new javax.swing.JMenuItem();
        jMenu_baku_laporan_produksi = new javax.swing.JMenu();
        jMenu_baku_lp = new javax.swing.JMenuItem();
        jMenu_baku_lp_cheat = new javax.swing.JMenuItem();
        jMenu_baku_lp_sesekan_baku = new javax.swing.JMenuItem();
        jMenu_baku_lp_sapon_baku = new javax.swing.JMenuItem();
        jMenu_baku_keluar = new javax.swing.JMenuItem();
        jMenu_baku_DataBahanBaku = new javax.swing.JMenuItem();
        jMenu_baku_Adjustment = new javax.swing.JMenuItem();
        jMenu_baku_pembelian_baku = new javax.swing.JMenuItem();
        jMenu_baku_KartuCampuran = new javax.swing.JMenuItem();
        jMenu_baku_Bonus_Panen = new javax.swing.JMenuItem();
        jMenu_baku_data_KH = new javax.swing.JMenuItem();
        jMenu_baku_master_upah = new javax.swing.JMenuItem();
        jMenu_baku_TV_Treatment = new javax.swing.JMenuItem();
        jMenu_baku_peramalan_grading = new javax.swing.JMenuItem();
        jMenu_produksi = new javax.swing.JMenu();
        jMenu_produksi_TR_Sela = new javax.swing.JMenuItem();
        jMenu_produksi_rendam = new javax.swing.JMenuItem();
        jMenu_produksi_cuci = new javax.swing.JMenuItem();
        jMenu_produksi_cabut = new javax.swing.JMenuItem();
        jMenu_produksi_cetak = new javax.swing.JMenuItem();
        jMenu_produksi_f2 = new javax.swing.JMenuItem();
        jMenu_produksi_data_kinerja_cetak = new javax.swing.JMenuItem();
        jMenu_produksi_data_kinerja_f2 = new javax.swing.JMenuItem();
        jMenu_produksi_progress = new javax.swing.JMenuItem();
        jMenu_produksi_stokOpname_LP_WIP = new javax.swing.JMenuItem();
        jMenuItem_tv_wip2 = new javax.swing.JMenuItem();
        MenuItem_Penilaian_BuluUpah = new javax.swing.JMenuItem();
        jMenu_produksi_data_kinerja_hc_kopyok = new javax.swing.JMenuItem();
        jMenu_atb = new javax.swing.JMenu();
        jMenuItem_atb_dataKaryawan = new javax.swing.JMenuItem();
        jMenuItem_atb_dataProduksi = new javax.swing.JMenuItem();
        jMenuItem_atb_penilaian_lp = new javax.swing.JMenuItem();
        jMenuItem_atb_kinerja_operator = new javax.swing.JMenuItem();
        jMenu_sub = new javax.swing.JMenu();
        jMenu_baku_lp_sesekan = new javax.swing.JMenuItem();
        jMenu_baku_lp_sapon = new javax.swing.JMenuItem();
        jMenu_TV_KPI_Sub = new javax.swing.JMenuItem();
        jMenu_Sub_dataSub = new javax.swing.JMenuItem();
        jMenu_produksi_cabut_sub = new javax.swing.JMenuItem();
        jMenu_produksi_cetak_sub = new javax.swing.JMenuItem();
        jMenuItem_sesekan_sub = new javax.swing.JMenuItem();
        jMenuItem_sapon_sub = new javax.swing.JMenuItem();
        jMenu_hrd_karyawan_sub = new javax.swing.JMenuItem();
        jMenu_hrd_dataFinger_Sub = new javax.swing.JMenuItem();
        jMenu_Sub_penggajian = new javax.swing.JMenuItem();
        jMenu_Sub_Piutang_sub = new javax.swing.JMenuItem();
        jMenu_Sub_tarif_upah = new javax.swing.JMenuItem();
        jMenu_Sub_Reproses = new javax.swing.JMenuItem();
        jMenu_Cabuto = new javax.swing.JMenu();
        jMenuItem_Data_Mitra = new javax.swing.JMenuItem();
        jMenuItem_Data_LP_Cabuto = new javax.swing.JMenuItem();
        jMenuItem_Data_Order_Cabuto = new javax.swing.JMenuItem();
        jMenuItem_Data_GradeBJ = new javax.swing.JMenuItem();
        jMenuItem_perhitungan_upah = new javax.swing.JMenuItem();
        jMenu_BahanJadi = new javax.swing.JMenu();
        jMenuItem_gudang_bahan_jadi = new javax.swing.JMenuItem();
        jMenuItem_tutupan_grading = new javax.swing.JMenuItem();
        jMenuItem_Data_box_bahan_jadi = new javax.swing.JMenuItem();
        jMenuItem_data_reproses = new javax.swing.JMenuItem();
        jMenuItem_stock_bahan_jadi = new javax.swing.JMenuItem();
        jMenuItem_Stock_Opname = new javax.swing.JMenuItem();
        jMenuItem_GradeBahanJadi = new javax.swing.JMenuItem();
        jMenuItem_PriceList = new javax.swing.JMenuItem();
        jMenuItem_Bonus_Grading = new javax.swing.JMenuItem();
        jMenuItem_LaporanProduksi_BJ = new javax.swing.JMenuItem();
        jMenuItem_PembelianBJ = new javax.swing.JMenuItem();
        jMenuItem_kinerjaGrading = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem_tv_wip = new javax.swing.JMenuItem();
        jMenuItem_tv_gbj = new javax.swing.JMenuItem();
        jMenuItem_tv_spk = new javax.swing.JMenuItem();
        jMenuItem_tv_spk_lokal = new javax.swing.JMenuItem();
        jMenu_packing = new javax.swing.JMenu();
        JMenuItem_DataPacking = new javax.swing.JMenuItem();
        jMenuItem_DataPengiriman = new javax.swing.JMenuItem();
        jMenuItem_buyer = new javax.swing.JMenuItem();
        jMenuItem_SPK = new javax.swing.JMenuItem();
        jMenuItem_print_weight_label = new javax.swing.JMenuItem();
        jMenuItem_barcode_packing = new javax.swing.JMenuItem();
        jMenuItem_PaymentReport = new javax.swing.JMenuItem();
        jMenu_TV_SPK = new javax.swing.JMenu();
        jMenuItem_tv_spk2 = new javax.swing.JMenuItem();
        jMenuItem_tv_spk_lok = new javax.swing.JMenuItem();
        jMenuItem_kinerja_karyawan_packing = new javax.swing.JMenuItem();
        jMenuItem_pack_ProgressPengiriman = new javax.swing.JMenuItem();
        jMenuItem_peramalan_barangjadi = new javax.swing.JMenuItem();
        jMenuItem_Shipment_Pickup_Request = new javax.swing.JMenuItem();
        jMenu_QC = new javax.swing.JMenu();
        jMenu_qc_bahanbaku = new javax.swing.JMenuItem();
        jMenu_qc_lp = new javax.swing.JMenuItem();
        jMenu_qc_treatment = new javax.swing.JMenuItem();
        jMenu_qc_pemanasan_jadi = new javax.swing.JMenuItem();
        jMenu_qc_treatment_bj = new javax.swing.JMenuItem();
        jMenu_qc_pemeriksaan_pengiriman = new javax.swing.JMenuItem();
        jMenu_qc_heat_treatment = new javax.swing.JMenuItem();
        jMenu_qc_dokumen = new javax.swing.JMenuItem();
        jMenu_qc_BahanKimia = new javax.swing.JMenuItem();
        jMenu_qc_suhu_kelembapan_hmi = new javax.swing.JMenuItem();
        jMenuItem_tv_qc = new javax.swing.JMenuItem();
        jMenuItem_tv_qc_belumSetor = new javax.swing.JMenuItem();
        jMenuItem_tv_wip1 = new javax.swing.JMenuItem();
        jMenu_HRD = new javax.swing.JMenu();
        jMenu_hrd_karyawan = new javax.swing.JMenuItem();
        jMenu_hrd_karyawan_wltsub = new javax.swing.JMenuItem();
        jMenu_hrd_karyawan_lama_masuk = new javax.swing.JMenuItem();
        jMenu_hrd_ijinKeluar = new javax.swing.JMenuItem();
        jMenu_hrd_cuti = new javax.swing.JMenuItem();
        jMenu_hrd_lembur = new javax.swing.JMenuItem();
        jMenu_hrd_grupCabut = new javax.swing.JMenuItem();
        jMenu_hrd_departemen = new javax.swing.JMenuItem();
        jMenu_hrd_dataLibur = new javax.swing.JMenuItem();
        jMenu_hrd_dataTBT = new javax.swing.JMenuItem();
        jMenu_hrd_dataFinger = new javax.swing.JMenuItem();
        jMenu_hrd_dataJamKerja = new javax.swing.JMenuItem();
        jMenu_hrd_dataJalurJemputan = new javax.swing.JMenuItem();
        jMenu_hrd_dataPersonalHygiene = new javax.swing.JMenuItem();
        MenuItem_hrd_ijinToilet = new javax.swing.JMenuItem();
        jMenu_hrd_poin_karyawan = new javax.swing.JMenuItem();
        jMenu_hrd_dataLabelAset = new javax.swing.JMenuItem();
        jMenu_Keuangan = new javax.swing.JMenu();
        jMenu_Keu_Baku = new javax.swing.JMenu();
        jMenuItem_Keu_HargaBaku = new javax.swing.JMenuItem();
        jMenuItem_Keu_HargaEsta = new javax.swing.JMenuItem();
        jMenuItem_Keu_HargaLP = new javax.swing.JMenuItem();
        jMenuItem_Keu_HargaCMP = new javax.swing.JMenuItem();
        MenuItem_Keu_BakuKeluar = new javax.swing.JMenuItem();
        jMenuItem_Keu_StockBahanBaku_kartu = new javax.swing.JMenuItem();
        jMenuItem_Keu_StockBahanBaku_grade = new javax.swing.JMenuItem();
        jMenu_Keu_Produksi = new javax.swing.JMenu();
        jMenu_produksi_bonusM = new javax.swing.JMenuItem();
        jMenu_produksi_data_cabutan = new javax.swing.JMenuItem();
        jMenuItem_Keu_GajiCETAK = new javax.swing.JMenuItem();
        jMenuItem_Keu_GajiCABUTO = new javax.swing.JMenuItem();
        jMenuItem_Keu_BonusKecepatanF2 = new javax.swing.JMenuItem();
        jMenuItem_Keu_Bonus_ATB = new javax.swing.JMenuItem();
        jMenuItem_Keu_Lembur = new javax.swing.JMenuItem();
        jMenuItem_Keu_ShiftMalam = new javax.swing.JMenuItem();
        jMenuItem_Keu_SlipHarian = new javax.swing.JMenuItem();
        jMenuItem_Keu_dataPayroll = new javax.swing.JMenuItem();
        jMenuItem10 = new javax.swing.JMenuItem();
        jMenuItem_DataKinerjaCabut = new javax.swing.JMenuItem();
        jMenuItem_DataKinerjaCetak = new javax.swing.JMenuItem();
        jMenuItem_Keu_StokOpnameWIP = new javax.swing.JMenuItem();
        jMenu_Keu_BarangJadi = new javax.swing.JMenu();
        jMenuItem_Keu_HargaPembelianBarangJadi = new javax.swing.JMenuItem();
        jMenuItem_Keu_DataBarangJadi = new javax.swing.JMenuItem();
        jMenuItem_Keu_Tutupan = new javax.swing.JMenuItem();
        jMenuItem_Keu_Box_BarangJadi = new javax.swing.JMenuItem();
        jMenuItem_Keu_StockOpnameGBJ = new javax.swing.JMenuItem();
        jMenu_Keu_HR = new javax.swing.JMenu();
        jMenuItem_Keu_LevelGaji = new javax.swing.JMenuItem();
        jMenuItem_Keu_pengajuan_naik_level_gaji = new javax.swing.JMenuItem();
        jMenuItem_Keu_DataKaryawan = new javax.swing.JMenuItem();
        jMenuItem_Keu_HariKerja = new javax.swing.JMenuItem();
        jMenuItem_Keu_AbsenTidakMasuk = new javax.swing.JMenuItem();
        jMenuItem_Keu_PiutangKaryawan = new javax.swing.JMenuItem();
        jMenuItem_Keu_TimBantu = new javax.swing.JMenuItem();
        jMenu_Keu_purchasing = new javax.swing.JMenu();
        jMenuItem_Keu_Pengajuan_Pembelian = new javax.swing.JMenuItem();
        jMenuItem_Keu_MasterDataAset = new javax.swing.JMenuItem();
        jMenuItem_Keu_PembelianAset = new javax.swing.JMenuItem();
        jMenuItem_Keu_LabellingAset = new javax.swing.JMenuItem();
        jMenuItem_Keu_LemburStaff = new javax.swing.JMenuItem();
        jMenuItem_Keu_LemburStaff_baru = new javax.swing.JMenuItem();
        jMenuItem_Keu_DataEkspor = new javax.swing.JMenuItem();
        jMenuItem_Keu_Payment = new javax.swing.JMenuItem();
        jMenu_Keu_Rekap = new javax.swing.JMenu();
        jMenuItem_Keu_RekapBTK = new javax.swing.JMenuItem();
        jMenuItem_Keu_biaya = new javax.swing.JMenuItem();
        jMenuItem_Keu_BiayaEkspor = new javax.swing.JMenuItem();
        jMenuItem_Keu_CashOnBank = new javax.swing.JMenuItem();
        jMenuItem_Keu_Neraca = new javax.swing.JMenuItem();
        jMenuItem_Keu_ARAP_Esta = new javax.swing.JMenuItem();
        jMenuItem_Keu_Laporan = new javax.swing.JMenuItem();
        jMenu_manajemen = new javax.swing.JMenu();
        jMenuItem9 = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem11 = new javax.swing.JMenuItem();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenu_TV_KPI = new javax.swing.JMenuItem();
        jMenuItem_KPI_waleta = new javax.swing.JMenuItem();
        jMenuItem_BiayaLP = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem12 = new javax.swing.JMenuItem();
        jMenuItem_Keu_TV_keuangan1 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();
        jMenu_Maintenance = new javax.swing.JMenu();
        jMenuItem_suhu_kelembapan = new javax.swing.JMenuItem();
        jMenuItem_aset_maintenance = new javax.swing.JMenuItem();
        jMenuItem_Maintenance = new javax.swing.JMenuItem();
        jMenuItem_suhu_kelembapan_otomatis = new javax.swing.JMenuItem();
        jMenu_User = new javax.swing.JMenu();
        jMenuItem_user_new = new javax.swing.JMenuItem();
        jMenuItem_user_view = new javax.swing.JMenuItem();
        jMenuItem_user_change_pass = new javax.swing.JMenuItem();
        jMenuItem_user_LogOut = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("PT. WALETA ASIA JAYA");
        setBackground(new java.awt.Color(255, 255, 255));

        jScrollPane2.setPreferredSize(new java.awt.Dimension(1366, 700));

        main_panel.setPreferredSize(new java.awt.Dimension(1340, 670));
        main_panel.setLayout(new java.awt.CardLayout());

        jPanel_Home.setBackground(new java.awt.Color(162, 155, 254));
        jPanel_Home.setPreferredSize(new java.awt.Dimension(1366, 700));

        jLabel19.setBackground(new java.awt.Color(255, 255, 255));
        jLabel19.setFont(new java.awt.Font("Calibri Light", 1, 24)); // NOI18N
        jLabel19.setText("2.2.395");

        jLabel21.setBackground(new java.awt.Color(255, 255, 255));
        jLabel21.setFont(new java.awt.Font("Calibri Light", 1, 48)); // NOI18N
        jLabel21.setText("PT. WALETA ASIA JAYA");

        jPanel_need_attention.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_need_attention.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        label_warning_stok_reproses.setBackground(new java.awt.Color(255, 255, 255));
        label_warning_stok_reproses.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        label_warning_stok_reproses.setForeground(new java.awt.Color(255, 0, 0));
        label_warning_stok_reproses.setText("Stok Reproses Barang Jadi : 0 Kg");

        label_warning_stok_baku.setBackground(new java.awt.Color(255, 255, 255));
        label_warning_stok_baku.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        label_warning_stok_baku.setForeground(new java.awt.Color(255, 0, 0));
        label_warning_stok_baku.setText("Stok Unworkable Raw Material : 0 Kg");

        label_warning_stok_qchold.setBackground(new java.awt.Color(255, 255, 255));
        label_warning_stok_qchold.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        label_warning_stok_qchold.setForeground(new java.awt.Color(255, 0, 0));
        label_warning_stok_qchold.setText("Stok QC Hold : 0 Kg");

        label_need_attention.setBackground(new java.awt.Color(255, 255, 255));
        label_need_attention.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        label_need_attention.setForeground(new java.awt.Color(255, 0, 0));
        label_need_attention.setText("Need Attention");

        javax.swing.GroupLayout jPanel_need_attentionLayout = new javax.swing.GroupLayout(jPanel_need_attention);
        jPanel_need_attention.setLayout(jPanel_need_attentionLayout);
        jPanel_need_attentionLayout.setHorizontalGroup(
            jPanel_need_attentionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_need_attentionLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_need_attentionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label_warning_stok_baku)
                    .addComponent(label_warning_stok_reproses)
                    .addComponent(label_warning_stok_qchold)
                    .addComponent(label_need_attention))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel_need_attentionLayout.setVerticalGroup(
            jPanel_need_attentionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_need_attentionLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label_need_attention)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(label_warning_stok_baku)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(label_warning_stok_reproses)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(label_warning_stok_qchold)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel_Butuh_Acc.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_Butuh_Acc.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Approval Required", javax.swing.border.TitledBorder.LEADING, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Arial", 0, 18), new java.awt.Color(255, 0, 0))); // NOI18N

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        Tabel_butuh_Acc_Lembur.setAutoCreateRowSorter(true);
        Tabel_butuh_Acc_Lembur.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Tabel_butuh_Acc_Lembur.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tgl Lembur", "Jenis SPL", "Departemen", "Disetujui", "Diketahui"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.Boolean.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(Tabel_butuh_Acc_Lembur);
        if (Tabel_butuh_Acc_Lembur.getColumnModel().getColumnCount() > 0) {
            Tabel_butuh_Acc_Lembur.getColumnModel().getColumn(4).setHeaderValue("Diketahui");
        }

        jTabbedPane1.addTab("Lembur", jScrollPane1);

        Tabel_butuh_Acc_PindahBagian.setAutoCreateRowSorter(true);
        Tabel_butuh_Acc_PindahBagian.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Tabel_butuh_Acc_PindahBagian.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tgl Input", "Nama", "Departemen", "Disetujui"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane4.setViewportView(Tabel_butuh_Acc_PindahBagian);

        jTabbedPane1.addTab("Pindah Bagian", jScrollPane4);

        Tabel_butuh_Acc_PengajuanAlat.setAutoCreateRowSorter(true);
        Tabel_butuh_Acc_PengajuanAlat.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Tabel_butuh_Acc_PengajuanAlat.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tgl Pengajuan", "Nama Barang", "Departemen", "Diket Kadep", "Diket MO"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.Boolean.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane5.setViewportView(Tabel_butuh_Acc_PengajuanAlat);

        jTabbedPane1.addTab("Pengajuan Alat", jScrollPane5);

        Button_refresh_butuh_Acc.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Button_refresh_butuh_Acc.setText("Refresh");
        Button_refresh_butuh_Acc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_refresh_butuh_AccActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_Butuh_AccLayout = new javax.swing.GroupLayout(jPanel_Butuh_Acc);
        jPanel_Butuh_Acc.setLayout(jPanel_Butuh_AccLayout);
        jPanel_Butuh_AccLayout.setHorizontalGroup(
            jPanel_Butuh_AccLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 631, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_Butuh_AccLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(Button_refresh_butuh_Acc)
                .addContainerGap())
        );
        jPanel_Butuh_AccLayout.setVerticalGroup(
            jPanel_Butuh_AccLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_Butuh_AccLayout.createSequentialGroup()
                .addComponent(Button_refresh_butuh_Acc)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 308, Short.MAX_VALUE))
        );

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Posisi LP Percobaan", javax.swing.border.TitledBorder.LEADING, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Arial", 0, 18))); // NOI18N

        tabel_data_LP_percobaan.setAutoCreateRowSorter(true);
        tabel_data_LP_percobaan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tabel_data_LP_percobaan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No LP", "Ruang", "Grade", "Memo", "Kpg", "Gram", "Posisi"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabel_data_LP_percobaan.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(tabel_data_LP_percobaan);

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel8.setText("Total LP :");

        label_total_lp_percobaan.setBackground(new java.awt.Color(255, 255, 255));
        label_total_lp_percobaan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_lp_percobaan.setText("0");

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel9.setText("Total Keping :");

        label_total_keping_lp_percobaan.setBackground(new java.awt.Color(255, 255, 255));
        label_total_keping_lp_percobaan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_keping_lp_percobaan.setText("0");

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel7.setText("Total Gram :");

        label_total_gram_lp_percobaan.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_lp_percobaan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_gram_lp_percobaan.setText("0");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 640, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_lp_percobaan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_keping_lp_percobaan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gram_lp_percobaan)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9)
                    .addComponent(jLabel7)
                    .addComponent(label_total_keping_lp_percobaan)
                    .addComponent(label_total_gram_lp_percobaan)
                    .addComponent(label_total_lp_percobaan))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel20.setBackground(new java.awt.Color(255, 255, 255));
        jLabel20.setFont(new java.awt.Font("Calibri Light", 0, 24)); // NOI18N
        jLabel20.setText("System version :");

        jPanel_Butuh_Acc1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_Butuh_Acc1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Dokumen Jatuh Tempo", javax.swing.border.TitledBorder.LEADING, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Arial", 0, 18), new java.awt.Color(255, 0, 0))); // NOI18N

        Button_refresh_dokumen_jatuh_tempo.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Button_refresh_dokumen_jatuh_tempo.setText("Refresh");
        Button_refresh_dokumen_jatuh_tempo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_refresh_dokumen_jatuh_tempoActionPerformed(evt);
            }
        });

        Table_dokumen_jatuh_tempo.setAutoCreateRowSorter(true);
        Table_dokumen_jatuh_tempo.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode Dokumen", "Nama Dokumen", "Tgl kedaluarsa", "Jatuh Tempo", "Total Hari"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Table_dokumen_jatuh_tempo.getTableHeader().setReorderingAllowed(false);
        jScrollPane8.setViewportView(Table_dokumen_jatuh_tempo);
        if (Table_dokumen_jatuh_tempo.getColumnModel().getColumnCount() > 0) {
            Table_dokumen_jatuh_tempo.getColumnModel().getColumn(4).setMinWidth(0);
            Table_dokumen_jatuh_tempo.getColumnModel().getColumn(4).setPreferredWidth(0);
            Table_dokumen_jatuh_tempo.getColumnModel().getColumn(4).setMaxWidth(0);
        }

        javax.swing.GroupLayout jPanel_Butuh_Acc1Layout = new javax.swing.GroupLayout(jPanel_Butuh_Acc1);
        jPanel_Butuh_Acc1.setLayout(jPanel_Butuh_Acc1Layout);
        jPanel_Butuh_Acc1Layout.setHorizontalGroup(
            jPanel_Butuh_Acc1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_Butuh_Acc1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_Butuh_Acc1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 640, Short.MAX_VALUE)
                    .addComponent(Button_refresh_dokumen_jatuh_tempo))
                .addContainerGap())
        );
        jPanel_Butuh_Acc1Layout.setVerticalGroup(
            jPanel_Butuh_Acc1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_Butuh_Acc1Layout.createSequentialGroup()
                .addComponent(Button_refresh_dokumen_jatuh_tempo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel_HomeLayout = new javax.swing.GroupLayout(jPanel_Home);
        jPanel_Home.setLayout(jPanel_HomeLayout);
        jPanel_HomeLayout.setHorizontalGroup(
            jPanel_HomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_HomeLayout.createSequentialGroup()
                .addGroup(jPanel_HomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_HomeLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel_HomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel_need_attention, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel_Butuh_Acc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel_HomeLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(jPanel_HomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel21)
                            .addGroup(jPanel_HomeLayout.createSequentialGroup()
                                .addComponent(jLabel20)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel19)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_HomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel_Butuh_Acc1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel_HomeLayout.setVerticalGroup(
            jPanel_HomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_HomeLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_HomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_HomeLayout.createSequentialGroup()
                        .addGroup(jPanel_HomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel19)
                            .addComponent(jLabel20))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel21)
                        .addGap(11, 11, 11)
                        .addComponent(jPanel_need_attention, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel_Butuh_Acc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel_HomeLayout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel_Butuh_Acc1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        main_panel.add(jPanel_Home, "card1");
        jPanel_Home.getAccessibleContext().setAccessibleName("Home");

        main_panel.add(jPanel_DataDepartemen1, "card13");
        main_panel.add(jPanel_Traceability21, "card101");
        main_panel.add(jPanel_Ijin_keluar1, "card16");
        main_panel.add(jPanel_Data_Karyawan1, "card15");
        main_panel.add(jPanel_Harga_BahanBaku1, "card11");
        main_panel.add(jPanel_DataLembur1, "card14");
        main_panel.add(jPanel_DataCabut1, "card19");
        main_panel.add(jPanel_DataCetak1, "card20");
        main_panel.add(jPanel_DataRendam1, "card22");
        main_panel.add(jPanel_DataCuci1, "card23");
        main_panel.add(jPanel_ProgressLP1, "card25");
        main_panel.add(jPanel_Supplier1, "card27");
        main_panel.add(jPanel_DataBahanBaku1, "card26");
        main_panel.add(jPanel_Customer1, "card26");
        main_panel.add(jPanel_RumahBurung1, "card26");
        main_panel.add(jPanel_BahanBakuMasuk1, "card26");
        main_panel.add(jPanel_Laporan_Produksi1, "card26");
        main_panel.add(jPanel_BahanBakuKeluar1, "card26");
        main_panel.add(jPanel_BahanJadiMasuk1, "card27");
        main_panel.add(jPanel_TutupanGradingBahanJadi1, "card28");
        main_panel.add(jPanel_BonusMangkok1, "card30");
        main_panel.add(jPanel_BoxBahanJadi1, "card31");
        main_panel.add(jPanel_DataPacking1, "card32");
        main_panel.add(jPanel_Data_Buyer1, "card33");
        main_panel.add(jPanel_StockBahanJadi1, "card35");
        main_panel.add(jPanel_pengiriman1, "card36");
        main_panel.add(jPanel_DataBahanBaku_Finance1, "card37");
        main_panel.add(jPanel_DataBahanBaku_PerGrade1, "card38");
        main_panel.add(jPanel_Harga_LaporanProduksi1, "card39");
        main_panel.add(jPanel_BonusGrading1, "card40");
        main_panel.add(jPanel_LaporanProduksi_BahanJadi1, "card43");
        main_panel.add(jPanel_PembelianBahanJadi1, "card44");
        main_panel.add(jPanel_Data_TglLibur1, "card46");
        main_panel.add(jPanel_AdjustmentBaku1, "card47");
        main_panel.add(jPanel_DataTemanBawaTeman1, "card48");
        main_panel.add(jPanel_DataPencetak1, "card49");
        main_panel.add(jPanel_Harga_BahanBakuKeluar1, "card50");
        main_panel.add(jPanel_Harga_PembelianBarangJadi1, "card51");
        main_panel.add(jPanel_Kartu_Campuran1, "card52");
        main_panel.add(jPanel_LaporanProduksi_Keuangan1, "card53");
        main_panel.add(jPanel_Harga_Kartu_Campuran1, "card56");
        main_panel.add(jPanel_BiayaTenagaKerja1, "card58");
        main_panel.add(jPanel_Hari_kerja1, "card60");
        main_panel.add(jPanel_DataPencabut1, "card61");
        main_panel.add(jPanel_DataKinerjaF21, "card63");
        main_panel.add(jPanel_Rekap_BiayaLP1, "card66");
        main_panel.add(jPanel_Biaya_Ekspor1, "card67");
        main_panel.add(jPanel_Biaya1, "card65");
        main_panel.add(jPanel_Harga_BahanBaku_Esta1, "card67");
        main_panel.add(jPanel_SPK1, "card67");
        main_panel.add(jPanel_Data_Ekspor1, "card68");
        main_panel.add(jPanel_DataScanKeping1, "card69");
        main_panel.add(jPanel_Payment_Report1, "card70");
        main_panel.add(jPanel_PriceList_GradeBJ1, "card71");
        main_panel.add(jPanel_DataCashOnBank1, "card72");
        main_panel.add(jPanel_Neraca1, "card73");
        main_panel.add(jPanel_StokOpnameGBJ1, "card74");
        main_panel.add(jPanel_Pembelian_Baku1, "card76");
        main_panel.add(jPanel_Barcode_Pengiriman1, "card77");
        main_panel.add(jPanel_HargaBahanBaku1, "card78");
        main_panel.add(jPanel_Data_ARAP_Esta1, "card79");
        main_panel.add(jPanel_BonusPetikRSB1, "card81");
        main_panel.add(jPanel_Rekap_Biaya_per_KartuBaku1, "card82");
        main_panel.add(jPanel_Reproses1, "card83");
        main_panel.add(jPanel_Data_Jam_Kerja1, "card84");
        main_panel.add(jPanel_Lembur_Karyawan1, "card85");
        main_panel.add(jPanel_payrol_harian1, "card86");
        main_panel.add(jPanel_Data_JalurJemputan1, "card89");
        main_panel.add(jPanel_DataKaryawan1, "card89");
        main_panel.add(jPanel_BahanBakuMasuk_Cheat1, "card90");
        main_panel.add(jPanel_Absen_Keuangan1, "card91");
        main_panel.add(jPanel_PiutangKaryawan1, "card93");
        main_panel.add(jPanel_TimBantu1, "card94");
        main_panel.add(jPanel_GradingLP_Tutupan1, "card95");
        main_panel.add(jPanel_Suhu_dan_Kelembapan1, "card96");
        main_panel.add(jPanel_Data_User1, "card97");
        main_panel.add(jPanel_Dokumen_KH1, "card98");
        main_panel.add(jPanel_Data_Karyawan_Sub1, "card96");
        main_panel.add(jPanel_DataCabutSub1, "card97");
        main_panel.add(jPanel_StokOpnameGBJ_RekapGrade1, "card101");
        main_panel.add(jPanel_DataTreatment1, "card97");
        main_panel.add(jPanel_Lab_BahanBaku1, "card98");
        main_panel.add(jPanel_Lab_BarangJadi1, "card99");
        main_panel.add(jPanel_Lab_LaporanProduksi1, "card100");
        main_panel.add(jPanel_Lab_Uji_Pengiriman1, "card101");
        main_panel.add(jPanel_Sub_Waleta1, "card101");
        main_panel.add(jPanel_Dokumen_QC1, "card102");
        main_panel.add(jPanel_BonusKecepatanF21, "card103");
        main_panel.add(jPanel_Traceability1, "card103");
        main_panel.add(jPanel_DataCetakSub1, "card105");
        main_panel.add(jPanel_Data_KaryawanLama_Masuk1, "card106");
        main_panel.add(jPanel_Absen_Cuti1, "card106");
        main_panel.add(jPanel_Laporan_Produksi_Sesekan1, "card108");
        main_panel.add(jPanel_StokOpname_WIP1, "card107");
        main_panel.add(jPanel_GradeBahanBaku1, "card107");
        main_panel.add(jPanel_DataGradeBahanJadi1, "card108");
        main_panel.add(jPanel_Tarif_upah_sub1, "card109");
        main_panel.add(jPanel_Isu_Produksi1, "card109");
        main_panel.add(jPanel_PiutangKaryawan_sub1, "card110");
        main_panel.add(jPanel_payrol_data1, "card111");
        main_panel.add(jPanel_DataKinerjaPacking1, "card112");
        main_panel.add(jPanel_DataLPSesekanSub1, "card113");
        main_panel.add(jPanel_MasterUpah1, "card114");
        main_panel.add(jPanel_Data_Karyawan_wltsub1, "card115");
        main_panel.add(jPanel_Laporan_Produksi_Sapon1, "card116");
        main_panel.add(jPanel_PenggajianSub1, "card116");
        main_panel.add(jPanel_Laporan_Produksi11, "card117");
        main_panel.add(jPanel_Undian21, "card118");
        main_panel.add(jPanel_Data_personalHygiene1, "card119");
        main_panel.add(jPanel_ijinToilet1, "card120");
        main_panel.add(jPanel_DataTRSela1, "card121");
        main_panel.add(jPanel_Data_Aset1, "card122");
        main_panel.add(jPanel_SistemPenilaian_Karyawan1, "card123");
        main_panel.add(jPanel_KinerjaKaryawanGrading1, "card124");
        main_panel.add(jPanel_DataKaryawanATB1, "card125");
        main_panel.add(jPanel_ProduksiATB1, "card126");
        main_panel.add(jPanel_DataSaponSub1, "card127");
        main_panel.add(jPanel_BonusCabut21, "card128");
        main_panel.add(jPanel_ProgressSPKPengiriman1, "card130");
        main_panel.add(jPanel_Peramalan_gradingBaku1, "card131");
        main_panel.add(jPanel_LevelGaji1, "card132");
        main_panel.add(jPanel_Suhu_dan_Kelembapan21, "card133");
        main_panel.add(jPanel_Aset_MasterDataAset1, "card134");
        main_panel.add(jPanel_Aset_NotaPembelian1, "card135");
        main_panel.add(jPanel_PengajuanKenaikanLevelGaji1, "card137");
        main_panel.add(jPanel_Lab_Heat_Treatment1, "card137");
        main_panel.add(jPanel_PengajuanKenaikanLevelGaji_ViewOnly1, "card138");
        main_panel.add(jPanel_Aset_UnitAset1, "card138");
        main_panel.add(jPanel_Absensi_Karyawan_ViewOnly1, "card139");
        main_panel.add(jPanel_Lembur_Staff1, "card140");
        main_panel.add(jPanel_Absensi_Karyawan1, "card139");
        main_panel.add(jPanel_DataPindahBagian1, "card140");
        main_panel.add(jPanel_DataKinerjaCabut1, "card141");
        main_panel.add(jPanel_Aset_PengajuanPembelian1, "card143");
        main_panel.add(jPanel_Kinerja_Operator_ATB1, "card145");
        main_panel.add(jPanel_Penilaian_BuluUpah_LP1, "card146");
        main_panel.add(jPanel_Data_Karyawan_ViewOnly1, "card147");
        main_panel.add(jPanel_DataBahanKimia1, "card148");
        main_panel.add(jPanel_StokOpname_WIP_keuangan1, "card149");
        main_panel.add(jPanel_ProduksiATB_PenilaianLP1, "card149");
        main_panel.add(jPanel_Absensi_Sub1, "card149");
        main_panel.add(jPanel_TutupanGradingBahanJadi_Keuangan1, "card150");
        main_panel.add(jPanel_GajiCABUTO1, "card151");
        main_panel.add(jPanel_Lembur_Staff_new1, "card153");
        main_panel.add(jPanel_Finishing21, "card153");
        main_panel.add(jPanel_GajiCetak1, "card152");
        main_panel.add(jPanel_BoxBahanJadi_Keuangan1, "card153");
        main_panel.add(jPanel_Data_User_Cabuto1, "card154");
        main_panel.add(jPanel_Laporan_Produksi_Cabuto1, "card155");
        main_panel.add(jPanel_Data_Order1, "card156");
        main_panel.add(jPanel_GradeBarangJadi_Cabuto1, "card157");
        main_panel.add(jPanel_Lembur_ShiftMalam1, "card157");
        main_panel.add(jPanel_DataKinerjaCetak1, "card157");
        main_panel.add(jPanel_Pengiriman_PickUp1, "card158");
        main_panel.add(jPanel_Peramalan_barangjadi21, "card158");
        main_panel.add(jPanel_DataKinerjaCuci_HC_Kopyok1, "card159");
        main_panel.add(jPanel_Reproses_Sub1, "card160");

        jScrollPane2.setViewportView(main_panel);

        jMenuBar1.setBackground(new java.awt.Color(255, 255, 255));
        jMenuBar1.setFont(new java.awt.Font("Nirmala UI", 0, 12)); // NOI18N

        jMenu_File.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_File.setText("Main Menu");
        jMenu_File.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        jMenu_Home.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_Home.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_Home.setText("Home Page");
        jMenu_Home.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_HomeActionPerformed(evt);
            }
        });
        jMenu_File.add(jMenu_Home);

        jMenu_Traceability.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_Traceability.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_Traceability.setText("Traceability");
        jMenu_Traceability.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_TraceabilityActionPerformed(evt);
            }
        });
        jMenu_File.add(jMenu_Traceability);

        jMenu_Traceability2.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_Traceability2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_Traceability2.setText("Traceability Ver. II");
        jMenu_Traceability2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_Traceability2ActionPerformed(evt);
            }
        });
        jMenu_File.add(jMenu_Traceability2);

        jMenu_SistemUndian.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_SistemUndian.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_SistemUndian.setText("Sistem Undian");
        jMenu_SistemUndian.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_SistemUndianActionPerformed(evt);
            }
        });
        jMenu_File.add(jMenu_SistemUndian);

        jMenu_Input_Lembur.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_Input_Lembur.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_Input_Lembur.setText("Input Lembur");
        jMenu_Input_Lembur.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_Input_LemburActionPerformed(evt);
            }
        });
        jMenu_File.add(jMenu_Input_Lembur);

        jMenu_pindah_grup.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_pindah_grup.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_pindah_grup.setText("Pindah Bagian & Grup");
        jMenu_pindah_grup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_pindah_grupActionPerformed(evt);
            }
        });
        jMenu_File.add(jMenu_pindah_grup);

        jMenu_produksi_isu.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_produksi_isu.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_produksi_isu.setText("Isu / Temuan Produksi");
        jMenu_produksi_isu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_produksi_isuActionPerformed(evt);
            }
        });
        jMenu_File.add(jMenu_produksi_isu);

        jMenu_TV_belumAbsen.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_TV_belumAbsen.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_TV_belumAbsen.setText("TV Terlambat & Belum Absen");
        jMenu_TV_belumAbsen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_TV_belumAbsenActionPerformed(evt);
            }
        });
        jMenu_File.add(jMenu_TV_belumAbsen);

        jMenu_TV_IjinBelumKembali.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_TV_IjinBelumKembali.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_TV_IjinBelumKembali.setText("TV Ijin Belum Kembali");
        jMenu_TV_IjinBelumKembali.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_TV_IjinBelumKembaliActionPerformed(evt);
            }
        });
        jMenu_File.add(jMenu_TV_IjinBelumKembali);

        jMenuItem_pengajuan_cuti_web.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_pengajuan_cuti_web.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_pengajuan_cuti_web.setText("Pengajuan Cuti via Web");
        jMenuItem_pengajuan_cuti_web.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_pengajuan_cuti_webActionPerformed(evt);
            }
        });
        jMenu_File.add(jMenuItem_pengajuan_cuti_web);

        jMenuItem_DataKaryawanViewOnly.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_DataKaryawanViewOnly.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_DataKaryawanViewOnly.setText("Data Karyawan");
        jMenuItem_DataKaryawanViewOnly.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_DataKaryawanViewOnlyActionPerformed(evt);
            }
        });
        jMenu_File.add(jMenuItem_DataKaryawanViewOnly);

        jMenuItem_AbsensiKaryawan.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_AbsensiKaryawan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_AbsensiKaryawan.setText("Absensi Karyawan");
        jMenuItem_AbsensiKaryawan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_AbsensiKaryawanActionPerformed(evt);
            }
        });
        jMenu_File.add(jMenuItem_AbsensiKaryawan);

        jMenuItem_FormTidakMasuk.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_FormTidakMasuk.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_FormTidakMasuk.setText("Form Tidak Masuk");
        jMenuItem_FormTidakMasuk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_FormTidakMasukActionPerformed(evt);
            }
        });
        jMenu_File.add(jMenuItem_FormTidakMasuk);

        jMenuItem_Pengajuan_naikLevel.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_Pengajuan_naikLevel.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_Pengajuan_naikLevel.setText("Pengajuan Naik Level Gaji");
        jMenuItem_Pengajuan_naikLevel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_Pengajuan_naikLevelActionPerformed(evt);
            }
        });
        jMenu_File.add(jMenuItem_Pengajuan_naikLevel);

        jMenuItem_Keu_Pengajuan_Pembelian2.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_Keu_Pengajuan_Pembelian2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_Keu_Pengajuan_Pembelian2.setText("Pengajuan Pembelian Alat Kerja");
        jMenuItem_Keu_Pengajuan_Pembelian2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_Keu_Pengajuan_Pembelian2ActionPerformed(evt);
            }
        });
        jMenu_File.add(jMenuItem_Keu_Pengajuan_Pembelian2);

        jMenuItem13.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem13.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem13.setText("Data Dokumen");
        jMenuItem13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem13ActionPerformed(evt);
            }
        });
        jMenu_File.add(jMenuItem13);

        jMenuItem_SistemPrintSlip.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_SistemPrintSlip.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_SistemPrintSlip.setText("Sistem Print Slip Gaji");
        jMenuItem_SistemPrintSlip.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_SistemPrintSlipActionPerformed(evt);
            }
        });
        jMenu_File.add(jMenuItem_SistemPrintSlip);
        jMenu_File.add(jSeparator1);

        jMenu_Exit.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_Exit.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_Exit.setText("Exit");
        jMenu_Exit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_ExitActionPerformed(evt);
            }
        });
        jMenu_File.add(jMenu_Exit);

        jMenuBar1.add(jMenu_File);

        jMenu_bahan_baku.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_bahan_baku.setText("Bag. Bahan Baku");
        jMenu_bahan_baku.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        jMenu_baku_supplier.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_baku_supplier.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_baku_supplier.setText("Data Supplier");
        jMenu_baku_supplier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_baku_supplierActionPerformed(evt);
            }
        });
        jMenu_bahan_baku.add(jMenu_baku_supplier);

        jMenu_baku_Customer.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_baku_Customer.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_baku_Customer.setText("Data Customer");
        jMenu_baku_Customer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_baku_CustomerActionPerformed(evt);
            }
        });
        jMenu_bahan_baku.add(jMenu_baku_Customer);

        jMenu_baku_rumahburung.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_baku_rumahburung.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_baku_rumahburung.setText("Data Rumah Burung");
        jMenu_baku_rumahburung.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_baku_rumahburungActionPerformed(evt);
            }
        });
        jMenu_bahan_baku.add(jMenu_baku_rumahburung);

        jMenu_baku_grade.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_baku_grade.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_baku_grade.setText("Master Grade Baku");
        jMenu_baku_grade.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_baku_gradeActionPerformed(evt);
            }
        });
        jMenu_bahan_baku.add(jMenu_baku_grade);

        jMenu_baku_masuk.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_baku_masuk.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_baku_masuk.setText("Bahan Baku Masuk");
        jMenu_baku_masuk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_baku_masukActionPerformed(evt);
            }
        });
        jMenu_bahan_baku.add(jMenu_baku_masuk);

        jMenu_baku_masuk_cheat.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_baku_masuk_cheat.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_baku_masuk_cheat.setText("Bahan Baku Masuk CT");
        jMenu_baku_masuk_cheat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_baku_masuk_cheatActionPerformed(evt);
            }
        });
        jMenu_bahan_baku.add(jMenu_baku_masuk_cheat);

        jMenu_baku_laporan_produksi.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_baku_laporan_produksi.setText("Laporan Produksi");
        jMenu_baku_laporan_produksi.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jMenu_baku_lp.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_baku_lp.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_baku_lp.setText("Laporan Produksi");
        jMenu_baku_lp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_baku_lpActionPerformed(evt);
            }
        });
        jMenu_baku_laporan_produksi.add(jMenu_baku_lp);

        jMenu_baku_lp_cheat.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_baku_lp_cheat.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_baku_lp_cheat.setText("Laporan Produksi Cheat");
        jMenu_baku_lp_cheat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_baku_lp_cheatActionPerformed(evt);
            }
        });
        jMenu_baku_laporan_produksi.add(jMenu_baku_lp_cheat);

        jMenu_baku_lp_sesekan_baku.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_baku_lp_sesekan_baku.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_baku_lp_sesekan_baku.setText("Laporan Produksi Sesekan (Local)");
        jMenu_baku_lp_sesekan_baku.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_baku_lp_sesekan_bakuActionPerformed(evt);
            }
        });
        jMenu_baku_laporan_produksi.add(jMenu_baku_lp_sesekan_baku);

        jMenu_baku_lp_sapon_baku.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_baku_lp_sapon_baku.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_baku_lp_sapon_baku.setText("Laporan Produksi Sapon (Local)");
        jMenu_baku_lp_sapon_baku.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_baku_lp_sapon_bakuActionPerformed(evt);
            }
        });
        jMenu_baku_laporan_produksi.add(jMenu_baku_lp_sapon_baku);

        jMenu_bahan_baku.add(jMenu_baku_laporan_produksi);

        jMenu_baku_keluar.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_baku_keluar.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_baku_keluar.setText("Penjualan Bahan Baku");
        jMenu_baku_keluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_baku_keluarActionPerformed(evt);
            }
        });
        jMenu_bahan_baku.add(jMenu_baku_keluar);

        jMenu_baku_DataBahanBaku.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_baku_DataBahanBaku.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_baku_DataBahanBaku.setText("Rekapitulasi Bahan Baku");
        jMenu_baku_DataBahanBaku.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_baku_DataBahanBakuActionPerformed(evt);
            }
        });
        jMenu_bahan_baku.add(jMenu_baku_DataBahanBaku);

        jMenu_baku_Adjustment.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_baku_Adjustment.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_baku_Adjustment.setText("Adjustment Bahan Baku");
        jMenu_baku_Adjustment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_baku_AdjustmentActionPerformed(evt);
            }
        });
        jMenu_bahan_baku.add(jMenu_baku_Adjustment);

        jMenu_baku_pembelian_baku.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_baku_pembelian_baku.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_baku_pembelian_baku.setText("Pembelian Bahan Baku");
        jMenu_baku_pembelian_baku.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_baku_pembelian_bakuActionPerformed(evt);
            }
        });
        jMenu_bahan_baku.add(jMenu_baku_pembelian_baku);

        jMenu_baku_KartuCampuran.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_baku_KartuCampuran.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_baku_KartuCampuran.setText("Kartu Campuran");
        jMenu_baku_KartuCampuran.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_baku_KartuCampuranActionPerformed(evt);
            }
        });
        jMenu_bahan_baku.add(jMenu_baku_KartuCampuran);

        jMenu_baku_Bonus_Panen.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_baku_Bonus_Panen.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_baku_Bonus_Panen.setText("Bonus Panen RSB");
        jMenu_baku_Bonus_Panen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_baku_Bonus_PanenActionPerformed(evt);
            }
        });
        jMenu_bahan_baku.add(jMenu_baku_Bonus_Panen);

        jMenu_baku_data_KH.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_baku_data_KH.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_baku_data_KH.setText("Data Dokumen KH");
        jMenu_baku_data_KH.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_baku_data_KHActionPerformed(evt);
            }
        });
        jMenu_bahan_baku.add(jMenu_baku_data_KH);

        jMenu_baku_master_upah.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_baku_master_upah.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_baku_master_upah.setText("Master Bulu Upah");
        jMenu_baku_master_upah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_baku_master_upahActionPerformed(evt);
            }
        });
        jMenu_bahan_baku.add(jMenu_baku_master_upah);

        jMenu_baku_TV_Treatment.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_baku_TV_Treatment.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_baku_TV_Treatment.setText("TV Treatment");
        jMenu_baku_TV_Treatment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_baku_TV_TreatmentActionPerformed(evt);
            }
        });
        jMenu_bahan_baku.add(jMenu_baku_TV_Treatment);

        jMenu_baku_peramalan_grading.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_baku_peramalan_grading.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_baku_peramalan_grading.setText("Peramalan Grading Baku");
        jMenu_baku_peramalan_grading.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_baku_peramalan_gradingActionPerformed(evt);
            }
        });
        jMenu_bahan_baku.add(jMenu_baku_peramalan_grading);

        jMenuBar1.add(jMenu_bahan_baku);

        jMenu_produksi.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_produksi.setText("Produksi");
        jMenu_produksi.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        jMenu_produksi_TR_Sela.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_produksi_TR_Sela.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_produksi_TR_Sela.setText("TR Sela");
        jMenu_produksi_TR_Sela.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_produksi_TR_SelaActionPerformed(evt);
            }
        });
        jMenu_produksi.add(jMenu_produksi_TR_Sela);

        jMenu_produksi_rendam.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_produksi_rendam.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_produksi_rendam.setText("Rendam");
        jMenu_produksi_rendam.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_produksi_rendamActionPerformed(evt);
            }
        });
        jMenu_produksi.add(jMenu_produksi_rendam);

        jMenu_produksi_cuci.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_produksi_cuci.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_produksi_cuci.setText("Cuci");
        jMenu_produksi_cuci.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_produksi_cuciActionPerformed(evt);
            }
        });
        jMenu_produksi.add(jMenu_produksi_cuci);

        jMenu_produksi_cabut.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_produksi_cabut.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_produksi_cabut.setText("Cabut");
        jMenu_produksi_cabut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_produksi_cabutActionPerformed(evt);
            }
        });
        jMenu_produksi.add(jMenu_produksi_cabut);

        jMenu_produksi_cetak.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_produksi_cetak.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_produksi_cetak.setText("Pembentukan / Cetak");
        jMenu_produksi_cetak.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_produksi_cetakActionPerformed(evt);
            }
        });
        jMenu_produksi.add(jMenu_produksi_cetak);

        jMenu_produksi_f2.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_produksi_f2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_produksi_f2.setText("Finishing 2");
        jMenu_produksi_f2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_produksi_f2ActionPerformed(evt);
            }
        });
        jMenu_produksi.add(jMenu_produksi_f2);

        jMenu_produksi_data_kinerja_cetak.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_produksi_data_kinerja_cetak.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_produksi_data_kinerja_cetak.setText("Data Kinerja Cetak");
        jMenu_produksi_data_kinerja_cetak.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_produksi_data_kinerja_cetakActionPerformed(evt);
            }
        });
        jMenu_produksi.add(jMenu_produksi_data_kinerja_cetak);

        jMenu_produksi_data_kinerja_f2.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_produksi_data_kinerja_f2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_produksi_data_kinerja_f2.setText("Data Kinerja F2");
        jMenu_produksi_data_kinerja_f2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_produksi_data_kinerja_f2ActionPerformed(evt);
            }
        });
        jMenu_produksi.add(jMenu_produksi_data_kinerja_f2);

        jMenu_produksi_progress.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_produksi_progress.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_produksi_progress.setText("Progress LP");
        jMenu_produksi_progress.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_produksi_progressActionPerformed(evt);
            }
        });
        jMenu_produksi.add(jMenu_produksi_progress);

        jMenu_produksi_stokOpname_LP_WIP.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_produksi_stokOpname_LP_WIP.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_produksi_stokOpname_LP_WIP.setText("Stok Opname LP WIP");
        jMenu_produksi_stokOpname_LP_WIP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_produksi_stokOpname_LP_WIPActionPerformed(evt);
            }
        });
        jMenu_produksi.add(jMenu_produksi_stokOpname_LP_WIP);

        jMenuItem_tv_wip2.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_tv_wip2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_tv_wip2.setText("TV WIP");
        jMenuItem_tv_wip2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_tv_wip2ActionPerformed(evt);
            }
        });
        jMenu_produksi.add(jMenuItem_tv_wip2);

        MenuItem_Penilaian_BuluUpah.setBackground(new java.awt.Color(255, 255, 255));
        MenuItem_Penilaian_BuluUpah.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        MenuItem_Penilaian_BuluUpah.setText("Penilaian Bulu Upah");
        MenuItem_Penilaian_BuluUpah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuItem_Penilaian_BuluUpahActionPerformed(evt);
            }
        });
        jMenu_produksi.add(MenuItem_Penilaian_BuluUpah);

        jMenu_produksi_data_kinerja_hc_kopyok.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_produksi_data_kinerja_hc_kopyok.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_produksi_data_kinerja_hc_kopyok.setText("Data Kinerja HC Kopyok");
        jMenu_produksi_data_kinerja_hc_kopyok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_produksi_data_kinerja_hc_kopyokActionPerformed(evt);
            }
        });
        jMenu_produksi.add(jMenu_produksi_data_kinerja_hc_kopyok);

        jMenuBar1.add(jMenu_produksi);

        jMenu_atb.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_atb.setText("R&D");
        jMenu_atb.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        jMenuItem_atb_dataKaryawan.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_atb_dataKaryawan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_atb_dataKaryawan.setText("Data Karyawan ATB");
        jMenuItem_atb_dataKaryawan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_atb_dataKaryawanActionPerformed(evt);
            }
        });
        jMenu_atb.add(jMenuItem_atb_dataKaryawan);

        jMenuItem_atb_dataProduksi.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_atb_dataProduksi.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_atb_dataProduksi.setText("Data Produksi ATB");
        jMenuItem_atb_dataProduksi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_atb_dataProduksiActionPerformed(evt);
            }
        });
        jMenu_atb.add(jMenuItem_atb_dataProduksi);

        jMenuItem_atb_penilaian_lp.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_atb_penilaian_lp.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_atb_penilaian_lp.setText("Penilaian LP ATB");
        jMenuItem_atb_penilaian_lp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_atb_penilaian_lpActionPerformed(evt);
            }
        });
        jMenu_atb.add(jMenuItem_atb_penilaian_lp);

        jMenuItem_atb_kinerja_operator.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_atb_kinerja_operator.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_atb_kinerja_operator.setText("Kinerja Operator ATB");
        jMenuItem_atb_kinerja_operator.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_atb_kinerja_operatorActionPerformed(evt);
            }
        });
        jMenu_atb.add(jMenuItem_atb_kinerja_operator);

        jMenuBar1.add(jMenu_atb);

        jMenu_sub.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_sub.setText("Manajemen Sub (online)");
        jMenu_sub.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        jMenu_baku_lp_sesekan.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_baku_lp_sesekan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_baku_lp_sesekan.setText("Laporan Produksi Sesekan (Local)");
        jMenu_baku_lp_sesekan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_baku_lp_sesekanActionPerformed(evt);
            }
        });
        jMenu_sub.add(jMenu_baku_lp_sesekan);

        jMenu_baku_lp_sapon.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_baku_lp_sapon.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_baku_lp_sapon.setText("Laporan Produksi Sapon (Local)");
        jMenu_baku_lp_sapon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_baku_lp_saponActionPerformed(evt);
            }
        });
        jMenu_sub.add(jMenu_baku_lp_sapon);

        jMenu_TV_KPI_Sub.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_TV_KPI_Sub.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_TV_KPI_Sub.setText("TV Kapasitas Sub Waleta");
        jMenu_TV_KPI_Sub.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_TV_KPI_SubActionPerformed(evt);
            }
        });
        jMenu_sub.add(jMenu_TV_KPI_Sub);

        jMenu_Sub_dataSub.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_Sub_dataSub.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_Sub_dataSub.setText("Data Sub Waleta");
        jMenu_Sub_dataSub.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_Sub_dataSubActionPerformed(evt);
            }
        });
        jMenu_sub.add(jMenu_Sub_dataSub);

        jMenu_produksi_cabut_sub.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_produksi_cabut_sub.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_produksi_cabut_sub.setText("Cabut Sub Online");
        jMenu_produksi_cabut_sub.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_produksi_cabut_subActionPerformed(evt);
            }
        });
        jMenu_sub.add(jMenu_produksi_cabut_sub);

        jMenu_produksi_cetak_sub.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_produksi_cetak_sub.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_produksi_cetak_sub.setText("Cetak Sub Online");
        jMenu_produksi_cetak_sub.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_produksi_cetak_subActionPerformed(evt);
            }
        });
        jMenu_sub.add(jMenu_produksi_cetak_sub);

        jMenuItem_sesekan_sub.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_sesekan_sub.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_sesekan_sub.setText("Data Sesekan Sub");
        jMenuItem_sesekan_sub.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_sesekan_subActionPerformed(evt);
            }
        });
        jMenu_sub.add(jMenuItem_sesekan_sub);

        jMenuItem_sapon_sub.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_sapon_sub.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_sapon_sub.setText("Data Sapon Sub");
        jMenuItem_sapon_sub.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_sapon_subActionPerformed(evt);
            }
        });
        jMenu_sub.add(jMenuItem_sapon_sub);

        jMenu_hrd_karyawan_sub.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_hrd_karyawan_sub.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_hrd_karyawan_sub.setText("Data Karyawan Sub");
        jMenu_hrd_karyawan_sub.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_hrd_karyawan_subActionPerformed(evt);
            }
        });
        jMenu_sub.add(jMenu_hrd_karyawan_sub);

        jMenu_hrd_dataFinger_Sub.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_hrd_dataFinger_Sub.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_hrd_dataFinger_Sub.setText("Data Absensi Sub");
        jMenu_hrd_dataFinger_Sub.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_hrd_dataFinger_SubActionPerformed(evt);
            }
        });
        jMenu_sub.add(jMenu_hrd_dataFinger_Sub);

        jMenu_Sub_penggajian.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_Sub_penggajian.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_Sub_penggajian.setText("Penggajian Sub");
        jMenu_Sub_penggajian.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_Sub_penggajianActionPerformed(evt);
            }
        });
        jMenu_sub.add(jMenu_Sub_penggajian);

        jMenu_Sub_Piutang_sub.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_Sub_Piutang_sub.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_Sub_Piutang_sub.setText("Piutang Karyawan Sub");
        jMenu_Sub_Piutang_sub.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_Sub_Piutang_subActionPerformed(evt);
            }
        });
        jMenu_sub.add(jMenu_Sub_Piutang_sub);

        jMenu_Sub_tarif_upah.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_Sub_tarif_upah.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_Sub_tarif_upah.setText("Tarif Upah Sub & bobot LP");
        jMenu_Sub_tarif_upah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_Sub_tarif_upahActionPerformed(evt);
            }
        });
        jMenu_sub.add(jMenu_Sub_tarif_upah);

        jMenu_Sub_Reproses.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_Sub_Reproses.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_Sub_Reproses.setText("Reproses Sub");
        jMenu_Sub_Reproses.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_Sub_ReprosesActionPerformed(evt);
            }
        });
        jMenu_sub.add(jMenu_Sub_Reproses);

        jMenuBar1.add(jMenu_sub);

        jMenu_Cabuto.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_Cabuto.setText("Cabuto");
        jMenu_Cabuto.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        jMenuItem_Data_Mitra.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_Data_Mitra.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_Data_Mitra.setText("Data Mitra");
        jMenuItem_Data_Mitra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_Data_MitraActionPerformed(evt);
            }
        });
        jMenu_Cabuto.add(jMenuItem_Data_Mitra);

        jMenuItem_Data_LP_Cabuto.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_Data_LP_Cabuto.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_Data_LP_Cabuto.setText("Data LP Cabuto");
        jMenuItem_Data_LP_Cabuto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_Data_LP_CabutoActionPerformed(evt);
            }
        });
        jMenu_Cabuto.add(jMenuItem_Data_LP_Cabuto);

        jMenuItem_Data_Order_Cabuto.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_Data_Order_Cabuto.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_Data_Order_Cabuto.setText("Data Order");
        jMenuItem_Data_Order_Cabuto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_Data_Order_CabutoActionPerformed(evt);
            }
        });
        jMenu_Cabuto.add(jMenuItem_Data_Order_Cabuto);

        jMenuItem_Data_GradeBJ.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_Data_GradeBJ.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_Data_GradeBJ.setText("Grade Barang Jadi Cabuto");
        jMenuItem_Data_GradeBJ.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_Data_GradeBJActionPerformed(evt);
            }
        });
        jMenu_Cabuto.add(jMenuItem_Data_GradeBJ);

        jMenuItem_perhitungan_upah.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_perhitungan_upah.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_perhitungan_upah.setText("Perhitungan Upah Cabuto");
        jMenuItem_perhitungan_upah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_perhitungan_upahActionPerformed(evt);
            }
        });
        jMenu_Cabuto.add(jMenuItem_perhitungan_upah);

        jMenuBar1.add(jMenu_Cabuto);

        jMenu_BahanJadi.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_BahanJadi.setText("Barang Jadi");
        jMenu_BahanJadi.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        jMenuItem_gudang_bahan_jadi.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_gudang_bahan_jadi.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_gudang_bahan_jadi.setText("Gudang Barang Jadi");
        jMenuItem_gudang_bahan_jadi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_gudang_bahan_jadiActionPerformed(evt);
            }
        });
        jMenu_BahanJadi.add(jMenuItem_gudang_bahan_jadi);

        jMenuItem_tutupan_grading.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_tutupan_grading.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_tutupan_grading.setText("Tutupan Grading");
        jMenuItem_tutupan_grading.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_tutupan_gradingActionPerformed(evt);
            }
        });
        jMenu_BahanJadi.add(jMenuItem_tutupan_grading);

        jMenuItem_Data_box_bahan_jadi.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_Data_box_bahan_jadi.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_Data_box_bahan_jadi.setText("Data Box Barang Jadi");
        jMenuItem_Data_box_bahan_jadi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_Data_box_bahan_jadiActionPerformed(evt);
            }
        });
        jMenu_BahanJadi.add(jMenuItem_Data_box_bahan_jadi);

        jMenuItem_data_reproses.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_data_reproses.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_data_reproses.setText("Data Reproses");
        jMenuItem_data_reproses.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_data_reprosesActionPerformed(evt);
            }
        });
        jMenu_BahanJadi.add(jMenuItem_data_reproses);

        jMenuItem_stock_bahan_jadi.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_stock_bahan_jadi.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_stock_bahan_jadi.setText("Stock Barang Jadi");
        jMenuItem_stock_bahan_jadi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_stock_bahan_jadiActionPerformed(evt);
            }
        });
        jMenu_BahanJadi.add(jMenuItem_stock_bahan_jadi);

        jMenuItem_Stock_Opname.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_Stock_Opname.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_Stock_Opname.setText("Data Stock Opname");
        jMenuItem_Stock_Opname.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_Stock_OpnameActionPerformed(evt);
            }
        });
        jMenu_BahanJadi.add(jMenuItem_Stock_Opname);

        jMenuItem_GradeBahanJadi.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_GradeBahanJadi.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_GradeBahanJadi.setText("Master Grade Barang Jadi");
        jMenuItem_GradeBahanJadi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_GradeBahanJadiActionPerformed(evt);
            }
        });
        jMenu_BahanJadi.add(jMenuItem_GradeBahanJadi);

        jMenuItem_PriceList.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_PriceList.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_PriceList.setText("Price List Grade barang Jadi");
        jMenuItem_PriceList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_PriceListActionPerformed(evt);
            }
        });
        jMenu_BahanJadi.add(jMenuItem_PriceList);

        jMenuItem_Bonus_Grading.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_Bonus_Grading.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_Bonus_Grading.setText("Bonus Grading");
        jMenuItem_Bonus_Grading.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_Bonus_GradingActionPerformed(evt);
            }
        });
        jMenu_BahanJadi.add(jMenuItem_Bonus_Grading);

        jMenuItem_LaporanProduksi_BJ.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_LaporanProduksi_BJ.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_LaporanProduksi_BJ.setText("Laporan Produksi Barang Jadi");
        jMenuItem_LaporanProduksi_BJ.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_LaporanProduksi_BJActionPerformed(evt);
            }
        });
        jMenu_BahanJadi.add(jMenuItem_LaporanProduksi_BJ);

        jMenuItem_PembelianBJ.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_PembelianBJ.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_PembelianBJ.setText("Pembelian Barang Jadi");
        jMenuItem_PembelianBJ.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_PembelianBJActionPerformed(evt);
            }
        });
        jMenu_BahanJadi.add(jMenuItem_PembelianBJ);

        jMenuItem_kinerjaGrading.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_kinerjaGrading.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_kinerjaGrading.setText("Data Kinerja Karyawan BJ");
        jMenuItem_kinerjaGrading.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_kinerjaGradingActionPerformed(evt);
            }
        });
        jMenu_BahanJadi.add(jMenuItem_kinerjaGrading);

        jMenu3.setBackground(new java.awt.Color(255, 255, 255));
        jMenu3.setText("TV");
        jMenu3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jMenuItem_tv_wip.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_tv_wip.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_tv_wip.setText("TV WIP");
        jMenuItem_tv_wip.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_tv_wipActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem_tv_wip);

        jMenuItem_tv_gbj.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_tv_gbj.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_tv_gbj.setText("TV GBJ");
        jMenuItem_tv_gbj.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_tv_gbjActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem_tv_gbj);

        jMenuItem_tv_spk.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_tv_spk.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_tv_spk.setText("TV SPK");
        jMenuItem_tv_spk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_tv_spkActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem_tv_spk);

        jMenuItem_tv_spk_lokal.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_tv_spk_lokal.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_tv_spk_lokal.setText("TV SPK LOK");
        jMenuItem_tv_spk_lokal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_tv_spk_lokalActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem_tv_spk_lokal);

        jMenu_BahanJadi.add(jMenu3);

        jMenuBar1.add(jMenu_BahanJadi);

        jMenu_packing.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_packing.setText("Packing & Ekspor");
        jMenu_packing.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        JMenuItem_DataPacking.setBackground(new java.awt.Color(255, 255, 255));
        JMenuItem_DataPacking.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        JMenuItem_DataPacking.setText("Data Packing");
        JMenuItem_DataPacking.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                JMenuItem_DataPackingActionPerformed(evt);
            }
        });
        jMenu_packing.add(JMenuItem_DataPacking);

        jMenuItem_DataPengiriman.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_DataPengiriman.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_DataPengiriman.setText("Data Pengiriman");
        jMenuItem_DataPengiriman.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_DataPengirimanActionPerformed(evt);
            }
        });
        jMenu_packing.add(jMenuItem_DataPengiriman);

        jMenuItem_buyer.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_buyer.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_buyer.setText("List Buyer");
        jMenuItem_buyer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_buyerActionPerformed(evt);
            }
        });
        jMenu_packing.add(jMenuItem_buyer);

        jMenuItem_SPK.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_SPK.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_SPK.setText("Data SPK");
        jMenuItem_SPK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_SPKActionPerformed(evt);
            }
        });
        jMenu_packing.add(jMenuItem_SPK);

        jMenuItem_print_weight_label.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_print_weight_label.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_print_weight_label.setText("Data Print Weight Label");
        jMenuItem_print_weight_label.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_print_weight_labelActionPerformed(evt);
            }
        });
        jMenu_packing.add(jMenuItem_print_weight_label);

        jMenuItem_barcode_packing.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_barcode_packing.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_barcode_packing.setText("Data Barcode Packing");
        jMenuItem_barcode_packing.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_barcode_packingActionPerformed(evt);
            }
        });
        jMenu_packing.add(jMenuItem_barcode_packing);

        jMenuItem_PaymentReport.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_PaymentReport.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_PaymentReport.setText("Sales & Payment Report");
        jMenuItem_PaymentReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_PaymentReportActionPerformed(evt);
            }
        });
        jMenu_packing.add(jMenuItem_PaymentReport);

        jMenu_TV_SPK.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_TV_SPK.setText("TV SPK");
        jMenu_TV_SPK.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jMenuItem_tv_spk2.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_tv_spk2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_tv_spk2.setText("TV SPK Packing");
        jMenuItem_tv_spk2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_tv_spk2ActionPerformed(evt);
            }
        });
        jMenu_TV_SPK.add(jMenuItem_tv_spk2);

        jMenuItem_tv_spk_lok.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_tv_spk_lok.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_tv_spk_lok.setText("TV SPK Lokal");
        jMenuItem_tv_spk_lok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_tv_spk_lokActionPerformed(evt);
            }
        });
        jMenu_TV_SPK.add(jMenuItem_tv_spk_lok);

        jMenu_packing.add(jMenu_TV_SPK);

        jMenuItem_kinerja_karyawan_packing.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_kinerja_karyawan_packing.setText("Kinerja Karyawan Packing");
        jMenuItem_kinerja_karyawan_packing.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_kinerja_karyawan_packingActionPerformed(evt);
            }
        });
        jMenu_packing.add(jMenuItem_kinerja_karyawan_packing);

        jMenuItem_pack_ProgressPengiriman.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_pack_ProgressPengiriman.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_pack_ProgressPengiriman.setText("Progress SPK Pengiriman");
        jMenuItem_pack_ProgressPengiriman.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_pack_ProgressPengirimanActionPerformed(evt);
            }
        });
        jMenu_packing.add(jMenuItem_pack_ProgressPengiriman);

        jMenuItem_peramalan_barangjadi.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_peramalan_barangjadi.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_peramalan_barangjadi.setText("Peramalan Barang Jadi");
        jMenuItem_peramalan_barangjadi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_peramalan_barangjadiActionPerformed(evt);
            }
        });
        jMenu_packing.add(jMenuItem_peramalan_barangjadi);

        jMenuItem_Shipment_Pickup_Request.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_Shipment_Pickup_Request.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_Shipment_Pickup_Request.setText("Shipment Pickup Request");
        jMenuItem_Shipment_Pickup_Request.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_Shipment_Pickup_RequestActionPerformed(evt);
            }
        });
        jMenu_packing.add(jMenuItem_Shipment_Pickup_Request);

        jMenuBar1.add(jMenu_packing);

        jMenu_QC.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_QC.setText("Quality Control");
        jMenu_QC.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        jMenu_qc_bahanbaku.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_qc_bahanbaku.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_qc_bahanbaku.setText("Uji Bahan Baku");
        jMenu_qc_bahanbaku.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_qc_bahanbakuActionPerformed(evt);
            }
        });
        jMenu_QC.add(jMenu_qc_bahanbaku);

        jMenu_qc_lp.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_qc_lp.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_qc_lp.setText("Uji Laporan Produksi");
        jMenu_qc_lp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_qc_lpActionPerformed(evt);
            }
        });
        jMenu_QC.add(jMenu_qc_lp);

        jMenu_qc_treatment.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_qc_treatment.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_qc_treatment.setText("Data Treatment");
        jMenu_qc_treatment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_qc_treatmentActionPerformed(evt);
            }
        });
        jMenu_QC.add(jMenu_qc_treatment);

        jMenu_qc_pemanasan_jadi.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_qc_pemanasan_jadi.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_qc_pemanasan_jadi.setText("Data Pemanasan Barang Jadi");
        jMenu_qc_pemanasan_jadi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_qc_pemanasan_jadiActionPerformed(evt);
            }
        });
        jMenu_QC.add(jMenu_qc_pemanasan_jadi);

        jMenu_qc_treatment_bj.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_qc_treatment_bj.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_qc_treatment_bj.setText("Data Treatment Barang Jadi");
        jMenu_qc_treatment_bj.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_qc_treatment_bjActionPerformed(evt);
            }
        });
        jMenu_QC.add(jMenu_qc_treatment_bj);

        jMenu_qc_pemeriksaan_pengiriman.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_qc_pemeriksaan_pengiriman.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_qc_pemeriksaan_pengiriman.setText("Data Pemeriksaan Pengiriman");
        jMenu_qc_pemeriksaan_pengiriman.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_qc_pemeriksaan_pengirimanActionPerformed(evt);
            }
        });
        jMenu_QC.add(jMenu_qc_pemeriksaan_pengiriman);

        jMenu_qc_heat_treatment.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_qc_heat_treatment.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_qc_heat_treatment.setText("Data Heat Treatment");
        jMenu_qc_heat_treatment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_qc_heat_treatmentActionPerformed(evt);
            }
        });
        jMenu_QC.add(jMenu_qc_heat_treatment);

        jMenu_qc_dokumen.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_qc_dokumen.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_qc_dokumen.setText("Dokumen");
        jMenu_qc_dokumen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_qc_dokumenActionPerformed(evt);
            }
        });
        jMenu_QC.add(jMenu_qc_dokumen);

        jMenu_qc_BahanKimia.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_qc_BahanKimia.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_qc_BahanKimia.setText("Data Bahan Kimia");
        jMenu_qc_BahanKimia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_qc_BahanKimiaActionPerformed(evt);
            }
        });
        jMenu_QC.add(jMenu_qc_BahanKimia);

        jMenu_qc_suhu_kelembapan_hmi.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_qc_suhu_kelembapan_hmi.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_qc_suhu_kelembapan_hmi.setText("Suhu & Kelembapan HMI");
        jMenu_qc_suhu_kelembapan_hmi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_qc_suhu_kelembapan_hmiActionPerformed(evt);
            }
        });
        jMenu_QC.add(jMenu_qc_suhu_kelembapan_hmi);

        jMenuItem_tv_qc.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_tv_qc.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_tv_qc.setText("TV QC");
        jMenuItem_tv_qc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_tv_qcActionPerformed(evt);
            }
        });
        jMenu_QC.add(jMenuItem_tv_qc);

        jMenuItem_tv_qc_belumSetor.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_tv_qc_belumSetor.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_tv_qc_belumSetor.setText("TV Belum Setor QC");
        jMenuItem_tv_qc_belumSetor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_tv_qc_belumSetorActionPerformed(evt);
            }
        });
        jMenu_QC.add(jMenuItem_tv_qc_belumSetor);

        jMenuItem_tv_wip1.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_tv_wip1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_tv_wip1.setText("TV WIP");
        jMenuItem_tv_wip1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_tv_wip1ActionPerformed(evt);
            }
        });
        jMenu_QC.add(jMenuItem_tv_wip1);

        jMenuBar1.add(jMenu_QC);

        jMenu_HRD.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_HRD.setText("Human Resource");
        jMenu_HRD.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        jMenu_hrd_karyawan.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_hrd_karyawan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_hrd_karyawan.setText("Data Karyawan");
        jMenu_hrd_karyawan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_hrd_karyawanActionPerformed(evt);
            }
        });
        jMenu_HRD.add(jMenu_hrd_karyawan);

        jMenu_hrd_karyawan_wltsub.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_hrd_karyawan_wltsub.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_hrd_karyawan_wltsub.setText("Data Karyawan + Sub Online");
        jMenu_hrd_karyawan_wltsub.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_hrd_karyawan_wltsubActionPerformed(evt);
            }
        });
        jMenu_HRD.add(jMenu_hrd_karyawan_wltsub);

        jMenu_hrd_karyawan_lama_masuk.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_hrd_karyawan_lama_masuk.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_hrd_karyawan_lama_masuk.setText("Data Karyawan Lama Masuk");
        jMenu_hrd_karyawan_lama_masuk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_hrd_karyawan_lama_masukActionPerformed(evt);
            }
        });
        jMenu_HRD.add(jMenu_hrd_karyawan_lama_masuk);

        jMenu_hrd_ijinKeluar.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_hrd_ijinKeluar.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_hrd_ijinKeluar.setText("Data Ijin Keluar");
        jMenu_hrd_ijinKeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_hrd_ijinKeluarActionPerformed(evt);
            }
        });
        jMenu_HRD.add(jMenu_hrd_ijinKeluar);

        jMenu_hrd_cuti.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_hrd_cuti.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_hrd_cuti.setText("Data Ijin Absen / Cuti");
        jMenu_hrd_cuti.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_hrd_cutiActionPerformed(evt);
            }
        });
        jMenu_HRD.add(jMenu_hrd_cuti);

        jMenu_hrd_lembur.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_hrd_lembur.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_hrd_lembur.setText("Data Lembur");
        jMenu_hrd_lembur.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_hrd_lemburActionPerformed(evt);
            }
        });
        jMenu_HRD.add(jMenu_hrd_lembur);

        jMenu_hrd_grupCabut.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_hrd_grupCabut.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_hrd_grupCabut.setText("Data Pindah Bagian");
        jMenu_hrd_grupCabut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_hrd_grupCabutActionPerformed(evt);
            }
        });
        jMenu_HRD.add(jMenu_hrd_grupCabut);

        jMenu_hrd_departemen.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_hrd_departemen.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_hrd_departemen.setText("List Departemen & Bagian");
        jMenu_hrd_departemen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_hrd_departemenActionPerformed(evt);
            }
        });
        jMenu_HRD.add(jMenu_hrd_departemen);

        jMenu_hrd_dataLibur.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_hrd_dataLibur.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_hrd_dataLibur.setText("Data Tanggal Libur");
        jMenu_hrd_dataLibur.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_hrd_dataLiburActionPerformed(evt);
            }
        });
        jMenu_HRD.add(jMenu_hrd_dataLibur);

        jMenu_hrd_dataTBT.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_hrd_dataTBT.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_hrd_dataTBT.setText("Data Teman Bawa Teman");
        jMenu_hrd_dataTBT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_hrd_dataTBTActionPerformed(evt);
            }
        });
        jMenu_HRD.add(jMenu_hrd_dataTBT);

        jMenu_hrd_dataFinger.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_hrd_dataFinger.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_hrd_dataFinger.setText("Data Absensi Waleta");
        jMenu_hrd_dataFinger.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_hrd_dataFingerActionPerformed(evt);
            }
        });
        jMenu_HRD.add(jMenu_hrd_dataFinger);

        jMenu_hrd_dataJamKerja.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_hrd_dataJamKerja.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_hrd_dataJamKerja.setText("Data Jam Kerja");
        jMenu_hrd_dataJamKerja.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_hrd_dataJamKerjaActionPerformed(evt);
            }
        });
        jMenu_HRD.add(jMenu_hrd_dataJamKerja);

        jMenu_hrd_dataJalurJemputan.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_hrd_dataJalurJemputan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_hrd_dataJalurJemputan.setText("Data Jalur Jemputan");
        jMenu_hrd_dataJalurJemputan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_hrd_dataJalurJemputanActionPerformed(evt);
            }
        });
        jMenu_HRD.add(jMenu_hrd_dataJalurJemputan);

        jMenu_hrd_dataPersonalHygiene.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_hrd_dataPersonalHygiene.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_hrd_dataPersonalHygiene.setText("Data Personal Hygiene");
        jMenu_hrd_dataPersonalHygiene.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_hrd_dataPersonalHygieneActionPerformed(evt);
            }
        });
        jMenu_HRD.add(jMenu_hrd_dataPersonalHygiene);

        MenuItem_hrd_ijinToilet.setBackground(new java.awt.Color(255, 255, 255));
        MenuItem_hrd_ijinToilet.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        MenuItem_hrd_ijinToilet.setText("Ijin Toilet");
        MenuItem_hrd_ijinToilet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuItem_hrd_ijinToiletActionPerformed(evt);
            }
        });
        jMenu_HRD.add(MenuItem_hrd_ijinToilet);

        jMenu_hrd_poin_karyawan.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_hrd_poin_karyawan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_hrd_poin_karyawan.setText("Sistem Poin Karyawan");
        jMenu_hrd_poin_karyawan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_hrd_poin_karyawanActionPerformed(evt);
            }
        });
        jMenu_HRD.add(jMenu_hrd_poin_karyawan);

        jMenu_hrd_dataLabelAset.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_hrd_dataLabelAset.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_hrd_dataLabelAset.setText("Labeling Aset");
        jMenu_hrd_dataLabelAset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_hrd_dataLabelAsetActionPerformed(evt);
            }
        });
        jMenu_HRD.add(jMenu_hrd_dataLabelAset);

        jMenuBar1.add(jMenu_HRD);

        jMenu_Keuangan.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_Keuangan.setText("Keuangan");
        jMenu_Keuangan.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        jMenu_Keu_Baku.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_Keu_Baku.setText("Data Bahan Baku");
        jMenu_Keu_Baku.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jMenuItem_Keu_HargaBaku.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_Keu_HargaBaku.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_Keu_HargaBaku.setText("Harga Bahan Baku");
        jMenuItem_Keu_HargaBaku.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_Keu_HargaBakuActionPerformed(evt);
            }
        });
        jMenu_Keu_Baku.add(jMenuItem_Keu_HargaBaku);

        jMenuItem_Keu_HargaEsta.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_Keu_HargaEsta.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_Keu_HargaEsta.setText("Harga Rujukan Esta");
        jMenuItem_Keu_HargaEsta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_Keu_HargaEstaActionPerformed(evt);
            }
        });
        jMenu_Keu_Baku.add(jMenuItem_Keu_HargaEsta);

        jMenuItem_Keu_HargaLP.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_Keu_HargaLP.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_Keu_HargaLP.setText("Harga Laporan Produksi");
        jMenuItem_Keu_HargaLP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_Keu_HargaLPActionPerformed(evt);
            }
        });
        jMenu_Keu_Baku.add(jMenuItem_Keu_HargaLP);

        jMenuItem_Keu_HargaCMP.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_Keu_HargaCMP.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_Keu_HargaCMP.setText("Harga Kartu CMP");
        jMenuItem_Keu_HargaCMP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_Keu_HargaCMPActionPerformed(evt);
            }
        });
        jMenu_Keu_Baku.add(jMenuItem_Keu_HargaCMP);

        MenuItem_Keu_BakuKeluar.setBackground(new java.awt.Color(255, 255, 255));
        MenuItem_Keu_BakuKeluar.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        MenuItem_Keu_BakuKeluar.setText("Data Penjualan Baku");
        MenuItem_Keu_BakuKeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuItem_Keu_BakuKeluarActionPerformed(evt);
            }
        });
        jMenu_Keu_Baku.add(MenuItem_Keu_BakuKeluar);

        jMenuItem_Keu_StockBahanBaku_kartu.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_Keu_StockBahanBaku_kartu.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_Keu_StockBahanBaku_kartu.setText("Stok Bahan Baku Per Kartu");
        jMenuItem_Keu_StockBahanBaku_kartu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_Keu_StockBahanBaku_kartuActionPerformed(evt);
            }
        });
        jMenu_Keu_Baku.add(jMenuItem_Keu_StockBahanBaku_kartu);

        jMenuItem_Keu_StockBahanBaku_grade.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_Keu_StockBahanBaku_grade.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_Keu_StockBahanBaku_grade.setText("Stok Bahan Baku Per Grade");
        jMenuItem_Keu_StockBahanBaku_grade.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_Keu_StockBahanBaku_gradeActionPerformed(evt);
            }
        });
        jMenu_Keu_Baku.add(jMenuItem_Keu_StockBahanBaku_grade);

        jMenu_Keuangan.add(jMenu_Keu_Baku);

        jMenu_Keu_Produksi.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_Keu_Produksi.setText("Produksi");
        jMenu_Keu_Produksi.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jMenu_produksi_bonusM.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_produksi_bonusM.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_produksi_bonusM.setText("Bonus mangkok");
        jMenu_produksi_bonusM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_produksi_bonusMActionPerformed(evt);
            }
        });
        jMenu_Keu_Produksi.add(jMenu_produksi_bonusM);

        jMenu_produksi_data_cabutan.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_produksi_data_cabutan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenu_produksi_data_cabutan.setText("Penggajian Cabut");
        jMenu_produksi_data_cabutan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_produksi_data_cabutanActionPerformed(evt);
            }
        });
        jMenu_Keu_Produksi.add(jMenu_produksi_data_cabutan);

        jMenuItem_Keu_GajiCETAK.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_Keu_GajiCETAK.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_Keu_GajiCETAK.setText("Penggajian Cetak");
        jMenuItem_Keu_GajiCETAK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_Keu_GajiCETAKActionPerformed(evt);
            }
        });
        jMenu_Keu_Produksi.add(jMenuItem_Keu_GajiCETAK);

        jMenuItem_Keu_GajiCABUTO.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_Keu_GajiCABUTO.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_Keu_GajiCABUTO.setText("Penggajian CABUTO");
        jMenuItem_Keu_GajiCABUTO.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_Keu_GajiCABUTOActionPerformed(evt);
            }
        });
        jMenu_Keu_Produksi.add(jMenuItem_Keu_GajiCABUTO);

        jMenuItem_Keu_BonusKecepatanF2.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_Keu_BonusKecepatanF2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_Keu_BonusKecepatanF2.setText("Bonus Kecepatan F2");
        jMenuItem_Keu_BonusKecepatanF2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_Keu_BonusKecepatanF2ActionPerformed(evt);
            }
        });
        jMenu_Keu_Produksi.add(jMenuItem_Keu_BonusKecepatanF2);

        jMenuItem_Keu_Bonus_ATB.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_Keu_Bonus_ATB.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_Keu_Bonus_ATB.setText("Bonus ATB");
        jMenuItem_Keu_Bonus_ATB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_Keu_Bonus_ATBActionPerformed(evt);
            }
        });
        jMenu_Keu_Produksi.add(jMenuItem_Keu_Bonus_ATB);

        jMenuItem_Keu_Lembur.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_Keu_Lembur.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_Keu_Lembur.setText("Lembur Karyawan");
        jMenuItem_Keu_Lembur.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_Keu_LemburActionPerformed(evt);
            }
        });
        jMenu_Keu_Produksi.add(jMenuItem_Keu_Lembur);

        jMenuItem_Keu_ShiftMalam.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_Keu_ShiftMalam.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_Keu_ShiftMalam.setText("Perhitungan Shift Malam");
        jMenuItem_Keu_ShiftMalam.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_Keu_ShiftMalamActionPerformed(evt);
            }
        });
        jMenu_Keu_Produksi.add(jMenuItem_Keu_ShiftMalam);

        jMenuItem_Keu_SlipHarian.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_Keu_SlipHarian.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_Keu_SlipHarian.setText("Slip Harian");
        jMenuItem_Keu_SlipHarian.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_Keu_SlipHarianActionPerformed(evt);
            }
        });
        jMenu_Keu_Produksi.add(jMenuItem_Keu_SlipHarian);

        jMenuItem_Keu_dataPayroll.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_Keu_dataPayroll.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_Keu_dataPayroll.setText("Data Penggajian");
        jMenuItem_Keu_dataPayroll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_Keu_dataPayrollActionPerformed(evt);
            }
        });
        jMenu_Keu_Produksi.add(jMenuItem_Keu_dataPayroll);

        jMenuItem10.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem10.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem10.setText("Simulasi Bonus Cabut");
        jMenuItem10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem10ActionPerformed(evt);
            }
        });
        jMenu_Keu_Produksi.add(jMenuItem10);

        jMenuItem_DataKinerjaCabut.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_DataKinerjaCabut.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_DataKinerjaCabut.setText("Data Kinerja Cabut");
        jMenuItem_DataKinerjaCabut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_DataKinerjaCabutActionPerformed(evt);
            }
        });
        jMenu_Keu_Produksi.add(jMenuItem_DataKinerjaCabut);

        jMenuItem_DataKinerjaCetak.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_DataKinerjaCetak.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_DataKinerjaCetak.setText("Data Kinerja Cetak");
        jMenuItem_DataKinerjaCetak.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_DataKinerjaCetakActionPerformed(evt);
            }
        });
        jMenu_Keu_Produksi.add(jMenuItem_DataKinerjaCetak);

        jMenuItem_Keu_StokOpnameWIP.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_Keu_StokOpnameWIP.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_Keu_StokOpnameWIP.setText("Stok Opname WIP");
        jMenuItem_Keu_StokOpnameWIP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_Keu_StokOpnameWIPActionPerformed(evt);
            }
        });
        jMenu_Keu_Produksi.add(jMenuItem_Keu_StokOpnameWIP);

        jMenu_Keuangan.add(jMenu_Keu_Produksi);

        jMenu_Keu_BarangJadi.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_Keu_BarangJadi.setText("Data Barang Jadi");
        jMenu_Keu_BarangJadi.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jMenuItem_Keu_HargaPembelianBarangJadi.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_Keu_HargaPembelianBarangJadi.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_Keu_HargaPembelianBarangJadi.setText("Harga Pembelian Barang Jadi");
        jMenuItem_Keu_HargaPembelianBarangJadi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_Keu_HargaPembelianBarangJadiActionPerformed(evt);
            }
        });
        jMenu_Keu_BarangJadi.add(jMenuItem_Keu_HargaPembelianBarangJadi);

        jMenuItem_Keu_DataBarangJadi.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_Keu_DataBarangJadi.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_Keu_DataBarangJadi.setText("Data Grading LP & Tutupan");
        jMenuItem_Keu_DataBarangJadi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_Keu_DataBarangJadiActionPerformed(evt);
            }
        });
        jMenu_Keu_BarangJadi.add(jMenuItem_Keu_DataBarangJadi);

        jMenuItem_Keu_Tutupan.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_Keu_Tutupan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_Keu_Tutupan.setText("Tutupan Grading");
        jMenuItem_Keu_Tutupan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_Keu_TutupanActionPerformed(evt);
            }
        });
        jMenu_Keu_BarangJadi.add(jMenuItem_Keu_Tutupan);

        jMenuItem_Keu_Box_BarangJadi.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_Keu_Box_BarangJadi.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_Keu_Box_BarangJadi.setText("Data Box Barang Jadi");
        jMenuItem_Keu_Box_BarangJadi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_Keu_Box_BarangJadiActionPerformed(evt);
            }
        });
        jMenu_Keu_BarangJadi.add(jMenuItem_Keu_Box_BarangJadi);

        jMenuItem_Keu_StockOpnameGBJ.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_Keu_StockOpnameGBJ.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_Keu_StockOpnameGBJ.setText("Data Stock Opname Barang Jadi");
        jMenuItem_Keu_StockOpnameGBJ.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_Keu_StockOpnameGBJActionPerformed(evt);
            }
        });
        jMenu_Keu_BarangJadi.add(jMenuItem_Keu_StockOpnameGBJ);

        jMenu_Keuangan.add(jMenu_Keu_BarangJadi);

        jMenu_Keu_HR.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_Keu_HR.setText("Data Karyawan");
        jMenu_Keu_HR.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jMenuItem_Keu_LevelGaji.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_Keu_LevelGaji.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_Keu_LevelGaji.setText("Level Gaji");
        jMenuItem_Keu_LevelGaji.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_Keu_LevelGajiActionPerformed(evt);
            }
        });
        jMenu_Keu_HR.add(jMenuItem_Keu_LevelGaji);

        jMenuItem_Keu_pengajuan_naik_level_gaji.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_Keu_pengajuan_naik_level_gaji.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_Keu_pengajuan_naik_level_gaji.setText("Pengajuan Naik Level Gaji");
        jMenuItem_Keu_pengajuan_naik_level_gaji.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_Keu_pengajuan_naik_level_gajiActionPerformed(evt);
            }
        });
        jMenu_Keu_HR.add(jMenuItem_Keu_pengajuan_naik_level_gaji);

        jMenuItem_Keu_DataKaryawan.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_Keu_DataKaryawan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_Keu_DataKaryawan.setText("Data Karyawan");
        jMenuItem_Keu_DataKaryawan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_Keu_DataKaryawanActionPerformed(evt);
            }
        });
        jMenu_Keu_HR.add(jMenuItem_Keu_DataKaryawan);

        jMenuItem_Keu_HariKerja.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_Keu_HariKerja.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_Keu_HariKerja.setText("Perhitungan Hari Kerja");
        jMenuItem_Keu_HariKerja.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_Keu_HariKerjaActionPerformed(evt);
            }
        });
        jMenu_Keu_HR.add(jMenuItem_Keu_HariKerja);

        jMenuItem_Keu_AbsenTidakMasuk.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_Keu_AbsenTidakMasuk.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_Keu_AbsenTidakMasuk.setText("Data Absen & Tidak Masuk");
        jMenuItem_Keu_AbsenTidakMasuk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_Keu_AbsenTidakMasukActionPerformed(evt);
            }
        });
        jMenu_Keu_HR.add(jMenuItem_Keu_AbsenTidakMasuk);

        jMenuItem_Keu_PiutangKaryawan.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_Keu_PiutangKaryawan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_Keu_PiutangKaryawan.setText("Data Piutang Karyawan");
        jMenuItem_Keu_PiutangKaryawan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_Keu_PiutangKaryawanActionPerformed(evt);
            }
        });
        jMenu_Keu_HR.add(jMenuItem_Keu_PiutangKaryawan);

        jMenuItem_Keu_TimBantu.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_Keu_TimBantu.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_Keu_TimBantu.setText("Data Tim Bantu");
        jMenuItem_Keu_TimBantu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_Keu_TimBantuActionPerformed(evt);
            }
        });
        jMenu_Keu_HR.add(jMenuItem_Keu_TimBantu);

        jMenu_Keuangan.add(jMenu_Keu_HR);

        jMenu_Keu_purchasing.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_Keu_purchasing.setText("Purchasing");
        jMenu_Keu_purchasing.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jMenuItem_Keu_Pengajuan_Pembelian.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_Keu_Pengajuan_Pembelian.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_Keu_Pengajuan_Pembelian.setText("Pengajuan Pembelian Alat Kerja");
        jMenuItem_Keu_Pengajuan_Pembelian.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_Keu_Pengajuan_PembelianActionPerformed(evt);
            }
        });
        jMenu_Keu_purchasing.add(jMenuItem_Keu_Pengajuan_Pembelian);

        jMenuItem_Keu_MasterDataAset.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_Keu_MasterDataAset.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_Keu_MasterDataAset.setText("Master Data Aset");
        jMenuItem_Keu_MasterDataAset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_Keu_MasterDataAsetActionPerformed(evt);
            }
        });
        jMenu_Keu_purchasing.add(jMenuItem_Keu_MasterDataAset);

        jMenuItem_Keu_PembelianAset.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_Keu_PembelianAset.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_Keu_PembelianAset.setText("Pembelian Aset");
        jMenuItem_Keu_PembelianAset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_Keu_PembelianAsetActionPerformed(evt);
            }
        });
        jMenu_Keu_purchasing.add(jMenuItem_Keu_PembelianAset);

        jMenuItem_Keu_LabellingAset.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_Keu_LabellingAset.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_Keu_LabellingAset.setText("Labeling Aset");
        jMenuItem_Keu_LabellingAset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_Keu_LabellingAsetActionPerformed(evt);
            }
        });
        jMenu_Keu_purchasing.add(jMenuItem_Keu_LabellingAset);

        jMenu_Keuangan.add(jMenu_Keu_purchasing);

        jMenuItem_Keu_LemburStaff.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_Keu_LemburStaff.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_Keu_LemburStaff.setText("Lembur Staff");
        jMenuItem_Keu_LemburStaff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_Keu_LemburStaffActionPerformed(evt);
            }
        });
        jMenu_Keuangan.add(jMenuItem_Keu_LemburStaff);

        jMenuItem_Keu_LemburStaff_baru.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_Keu_LemburStaff_baru.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_Keu_LemburStaff_baru.setText("Lembur Staff Baru");
        jMenuItem_Keu_LemburStaff_baru.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_Keu_LemburStaff_baruActionPerformed(evt);
            }
        });
        jMenu_Keuangan.add(jMenuItem_Keu_LemburStaff_baru);

        jMenuItem_Keu_DataEkspor.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_Keu_DataEkspor.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_Keu_DataEkspor.setText("Data Pengiriman");
        jMenuItem_Keu_DataEkspor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_Keu_DataEksporActionPerformed(evt);
            }
        });
        jMenu_Keuangan.add(jMenuItem_Keu_DataEkspor);

        jMenuItem_Keu_Payment.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_Keu_Payment.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_Keu_Payment.setText("Sales & Payment Report");
        jMenuItem_Keu_Payment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_Keu_PaymentActionPerformed(evt);
            }
        });
        jMenu_Keuangan.add(jMenuItem_Keu_Payment);

        jMenu_Keu_Rekap.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_Keu_Rekap.setText("Data Biaya");
        jMenu_Keu_Rekap.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jMenuItem_Keu_RekapBTK.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_Keu_RekapBTK.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_Keu_RekapBTK.setText("Biaya Tenaga Kerja");
        jMenuItem_Keu_RekapBTK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_Keu_RekapBTKActionPerformed(evt);
            }
        });
        jMenu_Keu_Rekap.add(jMenuItem_Keu_RekapBTK);

        jMenuItem_Keu_biaya.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_Keu_biaya.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_Keu_biaya.setText("Biaya Keseluruhan");
        jMenuItem_Keu_biaya.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_Keu_biayaActionPerformed(evt);
            }
        });
        jMenu_Keu_Rekap.add(jMenuItem_Keu_biaya);

        jMenuItem_Keu_BiayaEkspor.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_Keu_BiayaEkspor.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_Keu_BiayaEkspor.setText("Biaya Ekspor");
        jMenuItem_Keu_BiayaEkspor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_Keu_BiayaEksporActionPerformed(evt);
            }
        });
        jMenu_Keu_Rekap.add(jMenuItem_Keu_BiayaEkspor);

        jMenu_Keuangan.add(jMenu_Keu_Rekap);

        jMenuItem_Keu_CashOnBank.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_Keu_CashOnBank.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_Keu_CashOnBank.setText("Cash On Bank");
        jMenuItem_Keu_CashOnBank.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_Keu_CashOnBankActionPerformed(evt);
            }
        });
        jMenu_Keuangan.add(jMenuItem_Keu_CashOnBank);

        jMenuItem_Keu_Neraca.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_Keu_Neraca.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_Keu_Neraca.setText("Neraca");
        jMenuItem_Keu_Neraca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_Keu_NeracaActionPerformed(evt);
            }
        });
        jMenu_Keuangan.add(jMenuItem_Keu_Neraca);

        jMenuItem_Keu_ARAP_Esta.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_Keu_ARAP_Esta.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_Keu_ARAP_Esta.setText("Data AR AP Esta");
        jMenuItem_Keu_ARAP_Esta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_Keu_ARAP_EstaActionPerformed(evt);
            }
        });
        jMenu_Keuangan.add(jMenuItem_Keu_ARAP_Esta);

        jMenuItem_Keu_Laporan.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_Keu_Laporan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_Keu_Laporan.setText("Laporan Keuangan");
        jMenuItem_Keu_Laporan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_Keu_LaporanActionPerformed(evt);
            }
        });
        jMenu_Keuangan.add(jMenuItem_Keu_Laporan);

        jMenuBar1.add(jMenu_Keuangan);

        jMenu_manajemen.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_manajemen.setText("Manajemen");
        jMenu_manajemen.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        jMenuItem9.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem9.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem9.setText("Bonus Panen RSB");
        jMenuItem9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem9ActionPerformed(evt);
            }
        });
        jMenu_manajemen.add(jMenuItem9);

        jMenu1.setBackground(new java.awt.Color(255, 255, 255));
        jMenu1.setText("Laporan Bahan Baku");
        jMenu1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jMenuItem1.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem1.setText("Stok & Ketahanan Baku");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem5.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem5.setText("Harga Baku WLT vs Esta");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem5);

        jMenuItem6.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem6.setText("Rekap Bahan Baku diProses");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem6);

        jMenuItem11.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem11.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem11.setText("Evaluasi Margin Kartu Baku");
        jMenuItem11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem11ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem11);

        jMenu_manajemen.add(jMenu1);

        jMenuItem8.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem8.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem8.setText("Nitrit Treatment Cuci");
        jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem8ActionPerformed(evt);
            }
        });
        jMenu_manajemen.add(jMenuItem8);

        jMenu_TV_KPI.setText("TV LP WIP");
        jMenu_TV_KPI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_TV_KPIActionPerformed(evt);
            }
        });
        jMenu_manajemen.add(jMenu_TV_KPI);

        jMenuItem_KPI_waleta.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_KPI_waleta.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_KPI_waleta.setText("KPI Produksi");
        jMenuItem_KPI_waleta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_KPI_waletaActionPerformed(evt);
            }
        });
        jMenu_manajemen.add(jMenuItem_KPI_waleta);

        jMenuItem_BiayaLP.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_BiayaLP.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_BiayaLP.setText("Gross Margin / LP");
        jMenuItem_BiayaLP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_BiayaLPActionPerformed(evt);
            }
        });
        jMenu_manajemen.add(jMenuItem_BiayaLP);

        jMenuItem2.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem2.setText("Stok Jual BJD");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu_manajemen.add(jMenuItem2);

        jMenuItem12.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem12.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem12.setText("Stok Packing");
        jMenuItem12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem12ActionPerformed(evt);
            }
        });
        jMenu_manajemen.add(jMenuItem12);

        jMenuItem_Keu_TV_keuangan1.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_Keu_TV_keuangan1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_Keu_TV_keuangan1.setText("KPI Keuangan");
        jMenuItem_Keu_TV_keuangan1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_Keu_TV_keuangan1ActionPerformed(evt);
            }
        });
        jMenu_manajemen.add(jMenuItem_Keu_TV_keuangan1);

        jMenuItem3.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem3.setText("AR AP Report");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu_manajemen.add(jMenuItem3);

        jMenuItem4.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem4.setText("Assets Report");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu_manajemen.add(jMenuItem4);

        jMenuItem7.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem7.setText("Siklus Modal");
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem7ActionPerformed(evt);
            }
        });
        jMenu_manajemen.add(jMenuItem7);

        jMenuBar1.add(jMenu_manajemen);

        jMenu_Maintenance.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_Maintenance.setText("Maintenance");
        jMenu_Maintenance.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        jMenuItem_suhu_kelembapan.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_suhu_kelembapan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_suhu_kelembapan.setText("Pengecekan Suhu & Kelembapan");
        jMenuItem_suhu_kelembapan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_suhu_kelembapanActionPerformed(evt);
            }
        });
        jMenu_Maintenance.add(jMenuItem_suhu_kelembapan);

        jMenuItem_aset_maintenance.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_aset_maintenance.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_aset_maintenance.setText("Aset Barang Maintenance");
        jMenuItem_aset_maintenance.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_aset_maintenanceActionPerformed(evt);
            }
        });
        jMenu_Maintenance.add(jMenuItem_aset_maintenance);

        jMenuItem_Maintenance.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_Maintenance.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_Maintenance.setText("Sistem Maintenance");
        jMenuItem_Maintenance.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_MaintenanceActionPerformed(evt);
            }
        });
        jMenu_Maintenance.add(jMenuItem_Maintenance);

        jMenuItem_suhu_kelembapan_otomatis.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_suhu_kelembapan_otomatis.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_suhu_kelembapan_otomatis.setText("Suhu & Kelembapan otomatis");
        jMenuItem_suhu_kelembapan_otomatis.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_suhu_kelembapan_otomatisActionPerformed(evt);
            }
        });
        jMenu_Maintenance.add(jMenuItem_suhu_kelembapan_otomatis);

        jMenuBar1.add(jMenu_Maintenance);

        jMenu_User.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_User.setText("Admin");
        jMenu_User.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        jMenuItem_user_new.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_user_new.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_user_new.setText("Create New User");
        jMenuItem_user_new.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_user_newActionPerformed(evt);
            }
        });
        jMenu_User.add(jMenuItem_user_new);

        jMenuItem_user_view.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_user_view.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_user_view.setText("View User");
        jMenuItem_user_view.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_user_viewActionPerformed(evt);
            }
        });
        jMenu_User.add(jMenuItem_user_view);

        jMenuItem_user_change_pass.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_user_change_pass.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_user_change_pass.setText("Change Password");
        jMenuItem_user_change_pass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_user_change_passActionPerformed(evt);
            }
        });
        jMenu_User.add(jMenuItem_user_change_pass);

        jMenuItem_user_LogOut.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_user_LogOut.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem_user_LogOut.setText("Log Out");
        jMenuItem_user_LogOut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_user_LogOutActionPerformed(evt);
            }
        });
        jMenu_User.add(jMenuItem_user_LogOut);

        jMenuBar1.add(jMenu_User);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 679, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jMenu_baku_gradeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_baku_gradeActionPerformed
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_GradeBahanBaku1.init();
        if ('0' == dataMenu.get(AksesMenu.searchMenuByName(dataMenu, AksesMenu.MENU_ITEM_GRADE_BAKU)).akses.charAt(1)) {
            jPanel_GradeBahanBaku1.button_insert_grade.setEnabled(false);
            jPanel_GradeBahanBaku1.button_insert_grade.setVisible(false);
        } else {
            jPanel_GradeBahanBaku1.button_insert_grade.setEnabled(true);
            jPanel_GradeBahanBaku1.button_insert_grade.setVisible(true);
        }
        if ('0' == dataMenu.get(AksesMenu.searchMenuByName(dataMenu, AksesMenu.MENU_ITEM_GRADE_BAKU)).akses.charAt(2)) {
            jPanel_GradeBahanBaku1.button_update_grade.setEnabled(false);
            jPanel_GradeBahanBaku1.button_update_grade.setVisible(false);
        } else {
            jPanel_GradeBahanBaku1.button_update_grade.setEnabled(true);
            jPanel_GradeBahanBaku1.button_update_grade.setVisible(true);
        }
        if ('0' == dataMenu.get(AksesMenu.searchMenuByName(dataMenu, AksesMenu.MENU_ITEM_GRADE_BAKU)).akses.charAt(3)) {
            jPanel_GradeBahanBaku1.button_delete_grade.setEnabled(false);
            jPanel_GradeBahanBaku1.button_delete_grade.setVisible(false);
        } else {
            jPanel_GradeBahanBaku1.button_delete_grade.setEnabled(true);
            jPanel_GradeBahanBaku1.button_delete_grade.setVisible(true);
        }
        main_panel.add(jPanel_GradeBahanBaku1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenu_baku_gradeActionPerformed

    private void jMenu_baku_rumahburungActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_baku_rumahburungActionPerformed
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_RumahBurung1.init();
        if ('0' == dataMenu.get(AksesMenu.searchMenuByName(dataMenu, AksesMenu.MENU_ITEM_RUMAH_BURUNG)).akses.charAt(1)) {
            jPanel_RumahBurung1.button_insert_rumah_burung.setEnabled(false);
            jPanel_RumahBurung1.button_insert_rumah_burung.setVisible(false);
        } else {
            jPanel_RumahBurung1.button_insert_rumah_burung.setEnabled(true);
            jPanel_RumahBurung1.button_insert_rumah_burung.setVisible(true);
        }
        if ('0' == dataMenu.get(AksesMenu.searchMenuByName(dataMenu, AksesMenu.MENU_ITEM_RUMAH_BURUNG)).akses.charAt(2)) {
            jPanel_RumahBurung1.button_update_rumah_burung.setEnabled(false);
            jPanel_RumahBurung1.button_update_rumah_burung.setVisible(false);
        } else {
            jPanel_RumahBurung1.button_update_rumah_burung.setEnabled(true);
            jPanel_RumahBurung1.button_update_rumah_burung.setVisible(true);
        }
        if ('0' == dataMenu.get(AksesMenu.searchMenuByName(dataMenu, AksesMenu.MENU_ITEM_RUMAH_BURUNG)).akses.charAt(3)) {
            jPanel_RumahBurung1.button_delete_rumah_burung.setEnabled(false);
            jPanel_RumahBurung1.button_delete_rumah_burung.setVisible(false);
        } else {
            jPanel_RumahBurung1.button_delete_rumah_burung.setEnabled(true);
            jPanel_RumahBurung1.button_delete_rumah_burung.setVisible(true);
        }
        main_panel.add(jPanel_RumahBurung1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenu_baku_rumahburungActionPerformed

    private void jMenu_baku_supplierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_baku_supplierActionPerformed
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        jPanel_Supplier1.init();
        if ('0' == dataMenu.get(AksesMenu.searchMenuByName(dataMenu, AksesMenu.MENU_ITEM_SUPPLIER)).akses.charAt(1)) {
            jPanel_Supplier1.button_insert_supplier.setEnabled(false);
            jPanel_Supplier1.button_insert_supplier.setVisible(false);
        } else {
            jPanel_Supplier1.button_insert_supplier.setEnabled(true);
            jPanel_Supplier1.button_insert_supplier.setVisible(true);
        }
        if ('0' == dataMenu.get(AksesMenu.searchMenuByName(dataMenu, AksesMenu.MENU_ITEM_SUPPLIER)).akses.charAt(2)) {
            jPanel_Supplier1.button_update_supplier.setEnabled(false);
            jPanel_Supplier1.button_update_supplier.setVisible(false);
        } else {
            jPanel_Supplier1.button_update_supplier.setEnabled(true);
            jPanel_Supplier1.button_update_supplier.setVisible(true);
        }
        if ('0' == dataMenu.get(AksesMenu.searchMenuByName(dataMenu, AksesMenu.MENU_ITEM_SUPPLIER)).akses.charAt(3)) {
            jPanel_Supplier1.button_delete_supplier.setEnabled(false);
            jPanel_Supplier1.button_delete_supplier.setVisible(false);
        } else {
            jPanel_Supplier1.button_delete_supplier.setEnabled(true);
            jPanel_Supplier1.button_delete_supplier.setVisible(true);
        }
        main_panel.add(jPanel_Supplier1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenu_baku_supplierActionPerformed

    private void jMenu_baku_masukActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_baku_masukActionPerformed
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        String akses = "";
        for (int i = 1; i < 4; i++) {
            akses = akses + dataMenu.get(AksesMenu.searchMenuByName(dataMenu, AksesMenu.MENU_ITEM_BAHAN_BAKU_MASUK)).akses.charAt(i);
        }
        jPanel_BahanBakuMasuk1.init(akses);
        if ('0' == dataMenu.get(AksesMenu.searchMenuByName(dataMenu, AksesMenu.MENU_ITEM_BAHAN_BAKU_MASUK)).akses.charAt(1)) {
            jPanel_BahanBakuMasuk1.button_insert_bahan_baku_masuk.setEnabled(false);
            jPanel_BahanBakuMasuk1.button_save_uji_kemasan.setEnabled(false);
            jPanel_BahanBakuMasuk1.button_save_timbang_bahan_baku.setEnabled(false);
        } else {
            jPanel_BahanBakuMasuk1.button_insert_bahan_baku_masuk.setEnabled(true);
            jPanel_BahanBakuMasuk1.button_save_uji_kemasan.setEnabled(true);
            jPanel_BahanBakuMasuk1.button_save_timbang_bahan_baku.setEnabled(true);
        }
        if ('0' == dataMenu.get(AksesMenu.searchMenuByName(dataMenu, AksesMenu.MENU_ITEM_BAHAN_BAKU_MASUK)).akses.charAt(2)) {
            jPanel_BahanBakuMasuk1.button_update_bahan_baku_masuk.setEnabled(false);
            jPanel_BahanBakuMasuk1.button_save_uji_kemasan.setEnabled(false);
        } else {
            jPanel_BahanBakuMasuk1.button_update_bahan_baku_masuk.setEnabled(true);
            jPanel_BahanBakuMasuk1.button_save_uji_kemasan.setEnabled(true);
        }
        if ('0' == dataMenu.get(AksesMenu.searchMenuByName(dataMenu, AksesMenu.MENU_ITEM_BAHAN_BAKU_MASUK)).akses.charAt(3)) {
            jPanel_BahanBakuMasuk1.button_delete_bahan_baku_masuk.setEnabled(false);
        } else {
            jPanel_BahanBakuMasuk1.button_delete_bahan_baku_masuk.setEnabled(true);
        }
        main_panel.add(jPanel_BahanBakuMasuk1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenu_baku_masukActionPerformed

    private void jMenu_baku_lpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_baku_lpActionPerformed
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_Laporan_Produksi1.init();
        if ('0' == dataMenu.get(AksesMenu.searchMenuByName(dataMenu, AksesMenu.MENU_ITEM_LAPORAN_PRODUKSI)).akses.charAt(1)) {
            jPanel_Laporan_Produksi1.button_insert_LP.setEnabled(false);
            jPanel_Laporan_Produksi1.button_insert_LP.setVisible(false);
        } else {
            jPanel_Laporan_Produksi1.button_insert_LP.setEnabled(true);
            jPanel_Laporan_Produksi1.button_insert_LP.setVisible(true);
        }
        if ('0' == dataMenu.get(AksesMenu.searchMenuByName(dataMenu, AksesMenu.MENU_ITEM_LAPORAN_PRODUKSI)).akses.charAt(2)) {
            jPanel_Laporan_Produksi1.button_update_LP.setEnabled(false);
            jPanel_Laporan_Produksi1.button_update_LP.setVisible(false);
        } else {
            jPanel_Laporan_Produksi1.button_update_LP.setEnabled(true);
            jPanel_Laporan_Produksi1.button_update_LP.setVisible(true);
        }
        if ('0' == dataMenu.get(AksesMenu.searchMenuByName(dataMenu, AksesMenu.MENU_ITEM_LAPORAN_PRODUKSI)).akses.charAt(3)) {
            jPanel_Laporan_Produksi1.button_delete_LP.setEnabled(false);
            jPanel_Laporan_Produksi1.button_delete_LP.setVisible(false);
        } else {
            jPanel_Laporan_Produksi1.button_delete_LP.setEnabled(true);
            jPanel_Laporan_Produksi1.button_delete_LP.setVisible(true);
        }
        main_panel.add(jPanel_Laporan_Produksi1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenu_baku_lpActionPerformed

    private void jMenu_baku_keluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_baku_keluarActionPerformed
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_BahanBakuKeluar1.init();
        if ('0' == dataMenu.get(AksesMenu.searchMenuByName(dataMenu, AksesMenu.MENU_ITEM_BAKU_KELUAR)).akses.charAt(1)) {
            jPanel_BahanBakuKeluar1.button_insert_bahan_baku_keluar.setEnabled(false);
            jPanel_BahanBakuKeluar1.button_insert_bahan_baku_keluar.setVisible(false);
        } else {
            jPanel_BahanBakuKeluar1.button_insert_bahan_baku_keluar.setEnabled(true);
            jPanel_BahanBakuKeluar1.button_insert_bahan_baku_keluar.setVisible(true);
        }
        if ('0' == dataMenu.get(AksesMenu.searchMenuByName(dataMenu, AksesMenu.MENU_ITEM_BAKU_KELUAR)).akses.charAt(2)) {
        } else {
        }
        if ('0' == dataMenu.get(AksesMenu.searchMenuByName(dataMenu, AksesMenu.MENU_ITEM_BAKU_KELUAR)).akses.charAt(3)) {
            jPanel_BahanBakuKeluar1.button_delete_bahan_baku_keluar.setEnabled(false);
            jPanel_BahanBakuKeluar1.button_delete_bahan_baku_keluar.setVisible(false);
        } else {
            jPanel_BahanBakuKeluar1.button_delete_bahan_baku_keluar.setEnabled(true);
            jPanel_BahanBakuKeluar1.button_delete_bahan_baku_keluar.setVisible(true);
        }
        main_panel.add(jPanel_BahanBakuKeluar1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenu_baku_keluarActionPerformed

    private void jMenu_baku_DataBahanBakuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_baku_DataBahanBakuActionPerformed
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_DataBahanBaku1.init();
        main_panel.add(jPanel_DataBahanBaku1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenu_baku_DataBahanBakuActionPerformed

    private void jMenu_hrd_karyawanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_hrd_karyawanActionPerformed
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_Data_Karyawan1.init();
        if ('0' == dataMenu.get(AksesMenu.searchMenuByName(dataMenu, AksesMenu.MENU_ITEM_DATA_KARYAWAN)).akses.charAt(1)) {
            jPanel_Data_Karyawan1.button_insert_karyawan.setEnabled(false);
            jPanel_Data_Karyawan1.button_insert_karyawan.setVisible(false);
        } else {
            jPanel_Data_Karyawan1.button_insert_karyawan.setEnabled(true);
            jPanel_Data_Karyawan1.button_insert_karyawan.setVisible(true);
        }
        if ('0' == dataMenu.get(AksesMenu.searchMenuByName(dataMenu, AksesMenu.MENU_ITEM_DATA_KARYAWAN)).akses.charAt(2)) {
            jPanel_Data_Karyawan1.button_edit_data_karyawan.setEnabled(false);
            jPanel_Data_Karyawan1.button_edit_data_karyawan.setVisible(false);
            jPanel_Data_Karyawan1.button_status_IN.setEnabled(false);
            jPanel_Data_Karyawan1.button_status_IN.setVisible(false);
            jPanel_Data_Karyawan1.button_status_OUT.setEnabled(false);
            jPanel_Data_Karyawan1.button_status_OUT.setVisible(false);
            jPanel_Data_Karyawan1.button_status_batal.setEnabled(false);
            jPanel_Data_Karyawan1.button_status_batal.setVisible(false);
            jPanel_Data_Karyawan1.button_status_absen.setEnabled(false);
            jPanel_Data_Karyawan1.button_status_absen.setVisible(false);
        } else {
            jPanel_Data_Karyawan1.button_edit_data_karyawan.setEnabled(true);
            jPanel_Data_Karyawan1.button_edit_data_karyawan.setVisible(true);
            jPanel_Data_Karyawan1.button_status_IN.setEnabled(true);
            jPanel_Data_Karyawan1.button_status_IN.setVisible(true);
            jPanel_Data_Karyawan1.button_status_OUT.setEnabled(true);
            jPanel_Data_Karyawan1.button_status_OUT.setVisible(true);
            jPanel_Data_Karyawan1.button_status_batal.setEnabled(true);
            jPanel_Data_Karyawan1.button_status_batal.setVisible(true);
            jPanel_Data_Karyawan1.button_status_absen.setEnabled(true);
            jPanel_Data_Karyawan1.button_status_absen.setVisible(true);
        }
        if ('0' == dataMenu.get(AksesMenu.searchMenuByName(dataMenu, AksesMenu.MENU_ITEM_DATA_KARYAWAN)).akses.charAt(3)) {
        } else {
        }
        main_panel.add(jPanel_Data_Karyawan1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenu_hrd_karyawanActionPerformed

    private void jMenu_baku_CustomerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_baku_CustomerActionPerformed
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_Customer1.init();
        if ('0' == dataMenu.get(AksesMenu.searchMenuByName(dataMenu, AksesMenu.MENU_ITEM_CUSTOMER)).akses.charAt(1)) {
            jPanel_Customer1.button_insert_customer_baku.setEnabled(false);
            jPanel_Customer1.button_insert_customer_baku.setVisible(false);
        } else {
            jPanel_Customer1.button_insert_customer_baku.setEnabled(true);
            jPanel_Customer1.button_insert_customer_baku.setVisible(true);
        }
        if ('0' == dataMenu.get(AksesMenu.searchMenuByName(dataMenu, AksesMenu.MENU_ITEM_CUSTOMER)).akses.charAt(2)) {
            jPanel_Customer1.button_update_customer_baku.setEnabled(false);
            jPanel_Customer1.button_update_customer_baku.setVisible(false);
        } else {
            jPanel_Customer1.button_update_customer_baku.setEnabled(true);
            jPanel_Customer1.button_update_customer_baku.setVisible(true);
        }
        if ('0' == dataMenu.get(AksesMenu.searchMenuByName(dataMenu, AksesMenu.MENU_ITEM_CUSTOMER)).akses.charAt(3)) {
            jPanel_Customer1.button_delete_customer_baku.setEnabled(false);
            jPanel_Customer1.button_delete_customer_baku.setVisible(false);
        } else {
            jPanel_Customer1.button_delete_customer_baku.setEnabled(true);
            jPanel_Customer1.button_delete_customer_baku.setVisible(true);
        }
        main_panel.add(jPanel_Customer1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenu_baku_CustomerActionPerformed

    private void jMenu_produksi_rendamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_produksi_rendamActionPerformed
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_DataRendam1.init();
        if ('0' == dataMenu.get(AksesMenu.searchMenuByName(dataMenu, AksesMenu.MENU_ITEM_RENDAM)).akses.charAt(1)) {
            jPanel_DataRendam1.button_insert_rendam.setEnabled(false);
            jPanel_DataRendam1.button_insert_rendam.setVisible(false);
        } else {
            jPanel_DataRendam1.button_insert_rendam.setEnabled(true);
            jPanel_DataRendam1.button_insert_rendam.setVisible(true);
        }
        if ('0' == dataMenu.get(AksesMenu.searchMenuByName(dataMenu, AksesMenu.MENU_ITEM_RENDAM)).akses.charAt(2)) {
            jPanel_DataRendam1.button_update_rendam.setEnabled(false);
            jPanel_DataRendam1.button_update_rendam.setVisible(false);
        } else {
            jPanel_DataRendam1.button_update_rendam.setEnabled(true);
            jPanel_DataRendam1.button_update_rendam.setVisible(true);
        }
        if ('0' == dataMenu.get(AksesMenu.searchMenuByName(dataMenu, AksesMenu.MENU_ITEM_RENDAM)).akses.charAt(3)) {
            jPanel_DataRendam1.button_delete_rendam.setEnabled(false);
            jPanel_DataRendam1.button_delete_rendam.setVisible(false);
        } else {
            jPanel_DataRendam1.button_delete_rendam.setEnabled(true);
            jPanel_DataRendam1.button_delete_rendam.setVisible(true);
        }
        main_panel.add(jPanel_DataRendam1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenu_produksi_rendamActionPerformed

    private void jMenu_produksi_cabutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_produksi_cabutActionPerformed
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_DataCabut1.init();
        if ('0' == dataMenu.get(AksesMenu.searchMenuByName(dataMenu, AksesMenu.MENU_ITEM_CABUT)).akses.charAt(1)) {
            jPanel_DataCabut1.button_terima_cabut.setVisible(false);
            jPanel_DataCabut1.button_setor_cabut.setVisible(false);
            jPanel_DataCabut1.button_add_pencabut.setVisible(false);
            jPanel_DataCabut1.button_delete_pencabut.setVisible(false);
        } else {
            jPanel_DataCabut1.button_terima_cabut.setVisible(true);
            jPanel_DataCabut1.button_setor_cabut.setVisible(true);
            jPanel_DataCabut1.button_add_pencabut.setVisible(true);
            jPanel_DataCabut1.button_delete_pencabut.setVisible(true);
        }
        if ('0' == dataMenu.get(AksesMenu.searchMenuByName(dataMenu, AksesMenu.MENU_ITEM_CABUT)).akses.charAt(2)) {
            jPanel_DataCabut1.button_edit_cabut.setVisible(false);
        } else {
            jPanel_DataCabut1.button_edit_cabut.setVisible(true);
        }
        if ('0' == dataMenu.get(AksesMenu.searchMenuByName(dataMenu, AksesMenu.MENU_ITEM_CABUT)).akses.charAt(3)) {
            jPanel_DataCabut1.button_delete_cabut.setVisible(false);
        } else {
            jPanel_DataCabut1.button_delete_cabut.setVisible(true);
        }
        main_panel.add(jPanel_DataCabut1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenu_produksi_cabutActionPerformed

    private void jMenu_produksi_cetakActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_produksi_cetakActionPerformed
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_DataCetak1.init();
        if ('0' == dataMenu.get(AksesMenu.searchMenuByName(dataMenu, AksesMenu.MENU_ITEM_CETAK)).akses.charAt(1)) {
            jPanel_DataCetak1.button_cetak_terima_lp.setEnabled(false);
            jPanel_DataCetak1.button_cetak_setor_lp.setEnabled(false);
        } else {
            jPanel_DataCetak1.button_cetak_terima_lp.setEnabled(true);
            jPanel_DataCetak1.button_cetak_setor_lp.setEnabled(true);
        }
        if ('0' == dataMenu.get(AksesMenu.searchMenuByName(dataMenu, AksesMenu.MENU_ITEM_CETAK)).akses.charAt(2)) {
            jPanel_DataCetak1.button_cetak_edit.setEnabled(false);
        } else {
            jPanel_DataCetak1.button_cetak_edit.setEnabled(true);
        }
        if ('0' == dataMenu.get(AksesMenu.searchMenuByName(dataMenu, AksesMenu.MENU_ITEM_CETAK)).akses.charAt(3)) {
            jPanel_DataCetak1.button_cetak_delete.setEnabled(false);
        } else {
            jPanel_DataCetak1.button_cetak_delete.setEnabled(true);
        }
        main_panel.add(jPanel_DataCetak1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenu_produksi_cetakActionPerformed

    private void jMenu_produksi_cuciActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_produksi_cuciActionPerformed
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_DataCuci1.init();
        if ('0' == dataMenu.get(AksesMenu.searchMenuByName(dataMenu, AksesMenu.MENU_ITEM_CUCI)).akses.charAt(1)) {
            jPanel_DataCuci1.button_terima_cuci.setEnabled(false);
            jPanel_DataCuci1.button_setor_cuci.setEnabled(false);
        } else {
            jPanel_DataCuci1.button_terima_cuci.setEnabled(true);
            jPanel_DataCuci1.button_setor_cuci.setEnabled(true);
        }
        if ('0' == dataMenu.get(AksesMenu.searchMenuByName(dataMenu, AksesMenu.MENU_ITEM_CUCI)).akses.charAt(2)) {
            jPanel_DataCuci1.button_edit_cuci.setEnabled(false);
        } else {
            jPanel_DataCuci1.button_edit_cuci.setEnabled(true);
        }
        if ('0' == dataMenu.get(AksesMenu.searchMenuByName(dataMenu, AksesMenu.MENU_ITEM_CUCI)).akses.charAt(3)) {
            jPanel_DataCuci1.button_delete_cuci.setEnabled(false);
        } else {
            jPanel_DataCuci1.button_delete_cuci.setEnabled(true);
        }
        main_panel.add(jPanel_DataCuci1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenu_produksi_cuciActionPerformed

    private void jMenu_produksi_data_cabutanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_produksi_data_cabutanActionPerformed
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_DataPencabut1.init();
        main_panel.add(jPanel_DataPencabut1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenu_produksi_data_cabutanActionPerformed

    private void jMenuItem_user_LogOutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_user_LogOutActionPerformed
        this.dispose();
        Login in = new Login();
        in.pack();
        in.setLocationRelativeTo(this);
        in.setVisible(true);
    }//GEN-LAST:event_jMenuItem_user_LogOutActionPerformed

    private void jMenuItem_user_newActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_user_newActionPerformed
        waleta_system.User.JDialog_CreateNew_User1 create_user = new waleta_system.User.JDialog_CreateNew_User1(new javax.swing.JFrame(), true);
        create_user.pack();
        create_user.setLocationRelativeTo(this);
        create_user.setVisible(true);
        create_user.setEnabled(true);
        create_user.setResizable(false);
    }//GEN-LAST:event_jMenuItem_user_newActionPerformed

    private void jMenuItem_user_viewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_user_viewActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_Data_User1.init();
        main_panel.add(jPanel_Data_User1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenuItem_user_viewActionPerformed

    private void jMenuItem_user_change_passActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_user_change_passActionPerformed
        // TODO add your handling code here:
        waleta_system.User.JDialog_change_password change_password = new waleta_system.User.JDialog_change_password(new javax.swing.JFrame(), true);
        change_password.pack();
        change_password.setLocationRelativeTo(this);
        change_password.setVisible(true);
        change_password.setEnabled(true);
    }//GEN-LAST:event_jMenuItem_user_change_passActionPerformed

    private void jMenu_qc_bahanbakuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_qc_bahanbakuActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_Lab_BahanBaku1.init();
        main_panel.add(jPanel_Lab_BahanBaku1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenu_qc_bahanbakuActionPerformed

    private void jMenuItem_Keu_HargaBakuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_Keu_HargaBakuActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_Harga_BahanBaku1.init();
        main_panel.add(jPanel_Harga_BahanBaku1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenuItem_Keu_HargaBakuActionPerformed

    private void jMenu_hrd_ijinKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_hrd_ijinKeluarActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_Ijin_keluar1.init();
        if ('0' == dataMenu.get(AksesMenu.searchMenuByName(dataMenu, AksesMenu.MENU_ITEM_DATA_IJIN_KELUAR)).akses.charAt(1)) {
            jPanel_Ijin_keluar1.button_print_KELUAR.setEnabled(false);
            jPanel_Ijin_keluar1.button_print_PULANG.setEnabled(false);
            jPanel_Ijin_keluar1.button_add_ijin_keluar.setEnabled(false);
        } else {
            jPanel_Ijin_keluar1.button_print_KELUAR.setEnabled(true);
            jPanel_Ijin_keluar1.button_print_PULANG.setEnabled(true);
            jPanel_Ijin_keluar1.button_add_ijin_keluar.setEnabled(true);
        }
        if ('0' == dataMenu.get(AksesMenu.searchMenuByName(dataMenu, AksesMenu.MENU_ITEM_DATA_IJIN_KELUAR)).akses.charAt(2)) {
            jPanel_Ijin_keluar1.button_edit_ijin1.setEnabled(false);
        } else {
            jPanel_Ijin_keluar1.button_edit_ijin1.setEnabled(true);
        }
        if ('0' == dataMenu.get(AksesMenu.searchMenuByName(dataMenu, AksesMenu.MENU_ITEM_DATA_IJIN_KELUAR)).akses.charAt(3)) {
            jPanel_Ijin_keluar1.button_delete_ijin_keluar.setEnabled(false);
        } else {
            jPanel_Ijin_keluar1.button_delete_ijin_keluar.setEnabled(true);
        }
        main_panel.add(jPanel_Ijin_keluar1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenu_hrd_ijinKeluarActionPerformed

    private void jMenu_produksi_progressActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_produksi_progressActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_ProgressLP1.init();
        main_panel.add(jPanel_ProgressLP1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenu_produksi_progressActionPerformed

    private void jMenu_hrd_cutiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_hrd_cutiActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_Absen_Cuti1.init();
        main_panel.add(jPanel_Absen_Cuti1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenu_hrd_cutiActionPerformed

    private void jMenu_hrd_departemenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_hrd_departemenActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_DataDepartemen1.init();
        main_panel.add(jPanel_DataDepartemen1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenu_hrd_departemenActionPerformed

    private void jMenu_hrd_lemburActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_hrd_lemburActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_DataLembur1.init("HRGA");
        main_panel.add(jPanel_DataLembur1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenu_hrd_lemburActionPerformed

    private void jMenu_HomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_HomeActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        main_panel.add(jPanel_Home);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenu_HomeActionPerformed

    private void jMenuItem_gudang_bahan_jadiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_gudang_bahan_jadiActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        if ('0' == dataMenu.get(AksesMenu.searchMenuByName(dataMenu, AksesMenu.MENU_ITEM_BAHAN_JADI_MASUK)).akses.charAt(1)) {
            jPanel_BahanJadiMasuk1.button_terima_bj.setEnabled(false);
            jPanel_BahanJadiMasuk1.button_add_grading.setEnabled(false);
        } else {
            jPanel_BahanJadiMasuk1.button_terima_bj.setEnabled(true);
            jPanel_BahanJadiMasuk1.button_add_grading.setEnabled(true);
        }
        if ('0' == dataMenu.get(AksesMenu.searchMenuByName(dataMenu, AksesMenu.MENU_ITEM_BAHAN_JADI_MASUK)).akses.charAt(2)) {
            jPanel_BahanJadiMasuk1.button_kembali_qc.setEnabled(false);
            jPanel_BahanJadiMasuk1.button_edit_grading.setEnabled(false);
        } else {
            jPanel_BahanJadiMasuk1.button_kembali_qc.setEnabled(true);
            jPanel_BahanJadiMasuk1.button_edit_grading.setEnabled(true);
        }
        if ('0' == dataMenu.get(AksesMenu.searchMenuByName(dataMenu, AksesMenu.MENU_ITEM_BAHAN_JADI_MASUK)).akses.charAt(3)) {
            jPanel_BahanJadiMasuk1.button_delete_grading.setEnabled(false);
            jPanel_BahanJadiMasuk1.button_delete_lp.setEnabled(false);
        } else {
            jPanel_BahanJadiMasuk1.button_delete_grading.setEnabled(true);
            jPanel_BahanJadiMasuk1.button_delete_lp.setEnabled(true);
        }
        jPanel_BahanJadiMasuk1.init();
        main_panel.add(jPanel_BahanJadiMasuk1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenuItem_gudang_bahan_jadiActionPerformed

    private void jMenuItem_tutupan_gradingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_tutupan_gradingActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_TutupanGradingBahanJadi1.init();
        main_panel.add(jPanel_TutupanGradingBahanJadi1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenuItem_tutupan_gradingActionPerformed

    private void jMenu_hrd_grupCabutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_hrd_grupCabutActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        main_panel.add(jPanel_DataPindahBagian1);
        jPanel_DataPindahBagian1.init("HRGA");
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenu_hrd_grupCabutActionPerformed

    private void jMenu_produksi_bonusMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_produksi_bonusMActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_BonusMangkok1.init();
        main_panel.add(jPanel_BonusMangkok1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenu_produksi_bonusMActionPerformed

    private void jMenu_produksi_f2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_produksi_f2ActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_Finishing21.init();
        if ('0' == dataMenu.get(AksesMenu.searchMenuByName(dataMenu, AksesMenu.MENU_ITEM_F2)).akses.charAt(1)) {
            jPanel_Finishing21.button_input_sesekan.setVisible(false);
            jPanel_Finishing21.button_input_byproduct.setVisible(false);
            jPanel_Finishing21.button_f2_terima_lp.setVisible(false);
            jPanel_Finishing21.button_input_koreksi.setVisible(false);
            jPanel_Finishing21.button_input_koreksi1.setVisible(false);
            jPanel_Finishing21.button_input_f1.setVisible(false);
            jPanel_Finishing21.button_input_f2.setVisible(false);
            jPanel_Finishing21.button_input_kaki.setVisible(false);
            jPanel_Finishing21.button_f2_setor_lp.setVisible(false);
        } else {
            jPanel_Finishing21.button_input_sesekan.setVisible(true);
            jPanel_Finishing21.button_input_byproduct.setVisible(true);
            jPanel_Finishing21.button_f2_terima_lp.setVisible(true);
            jPanel_Finishing21.button_input_koreksi.setVisible(true);
            jPanel_Finishing21.button_input_koreksi1.setVisible(true);
            jPanel_Finishing21.button_input_f1.setVisible(true);
            jPanel_Finishing21.button_input_f2.setVisible(true);
            jPanel_Finishing21.button_input_kaki.setVisible(true);
            jPanel_Finishing21.button_f2_setor_lp.setVisible(true);
        }
        if ('0' == dataMenu.get(AksesMenu.searchMenuByName(dataMenu, AksesMenu.MENU_ITEM_F2)).akses.charAt(2)) {
            jPanel_Finishing21.button_f2_edit.setVisible(false);
            jPanel_Finishing21.button_f2_edit_kaki.setVisible(false);
        } else {
            jPanel_Finishing21.button_f2_edit.setVisible(true);
            jPanel_Finishing21.button_f2_edit_kaki.setVisible(true);
        }
        if ('0' == dataMenu.get(AksesMenu.searchMenuByName(dataMenu, AksesMenu.MENU_ITEM_F2)).akses.charAt(3)) {
            jPanel_Finishing21.button_f2_delete.setVisible(false);
        } else {
            jPanel_Finishing21.button_f2_delete.setVisible(true);
        }

        main_panel.add(jPanel_Finishing21);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenu_produksi_f2ActionPerformed

    private void jMenu_qc_lpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_qc_lpActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_Lab_LaporanProduksi1.init();
        main_panel.add(jPanel_Lab_LaporanProduksi1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenu_qc_lpActionPerformed

    private void jMenuItem_Data_box_bahan_jadiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_Data_box_bahan_jadiActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_BoxBahanJadi1.init();
        if ('0' == dataMenu.get(AksesMenu.searchMenuByName(dataMenu, AksesMenu.MENU_ITEM_DATA_BOX_BAHAN_JADI)).akses.charAt(1)) {
            jPanel_BoxBahanJadi1.button_rePacking.setVisible(false);
            jPanel_BoxBahanJadi1.button_reProcess.setVisible(false);
            jPanel_BoxBahanJadi1.button_setor_Packing.setVisible(false);
            jPanel_BoxBahanJadi1.button_terima_retur.setVisible(false);
            jPanel_BoxBahanJadi1.button_out.setVisible(false);
            jPanel_BoxBahanJadi1.button_lp_suwir.setVisible(false);
            jPanel_BoxBahanJadi1.button_selesai_repacking.setVisible(false);
        } else {
            jPanel_BoxBahanJadi1.button_rePacking.setVisible(true);
            jPanel_BoxBahanJadi1.button_reProcess.setVisible(true);
            jPanel_BoxBahanJadi1.button_setor_Packing.setVisible(true);
            jPanel_BoxBahanJadi1.button_terima_retur.setVisible(true);
            jPanel_BoxBahanJadi1.button_out.setVisible(true);
            jPanel_BoxBahanJadi1.button_lp_suwir.setVisible(true);
            jPanel_BoxBahanJadi1.button_selesai_repacking.setVisible(true);
        }
        if ('0' == dataMenu.get(AksesMenu.searchMenuByName(dataMenu, AksesMenu.MENU_ITEM_DATA_BOX_BAHAN_JADI)).akses.charAt(2)) {
            jPanel_BoxBahanJadi1.button_edit_repacking.setVisible(false);
            jPanel_BoxBahanJadi1.button_editgram.setVisible(false);
        } else {
            jPanel_BoxBahanJadi1.button_edit_repacking.setVisible(true);
            jPanel_BoxBahanJadi1.button_editgram.setVisible(true);
        }
        if ('0' == dataMenu.get(AksesMenu.searchMenuByName(dataMenu, AksesMenu.MENU_ITEM_DATA_BOX_BAHAN_JADI)).akses.charAt(3)) {

        } else {

        }
        main_panel.add(jPanel_BoxBahanJadi1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenuItem_Data_box_bahan_jadiActionPerformed

    private void JMenuItem_DataPackingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_JMenuItem_DataPackingActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_DataPacking1.init();
        if ('0' == dataMenu.get(AksesMenu.searchMenuByName(dataMenu, AksesMenu.MENU_ITEM_DATA_PACKING)).akses.charAt(1)) {
            jPanel_DataPacking1.button_terima.setEnabled(false);
            jPanel_DataPacking1.button_terima.setVisible(false);
            jPanel_DataPacking1.button_retur.setEnabled(false);
            jPanel_DataPacking1.button_retur.setVisible(false);
        } else {
            jPanel_DataPacking1.button_terima.setEnabled(true);
            jPanel_DataPacking1.button_terima.setVisible(true);
            jPanel_DataPacking1.button_retur.setEnabled(true);
            jPanel_DataPacking1.button_retur.setVisible(true);
        }
        if ('0' == dataMenu.get(AksesMenu.searchMenuByName(dataMenu, AksesMenu.MENU_ITEM_DATA_PACKING)).akses.charAt(2)) {
            jPanel_DataPacking1.button_pindah_SPK.setEnabled(false);
            jPanel_DataPacking1.button_pindah_SPK.setVisible(false);
        } else {
            jPanel_DataPacking1.button_pindah_SPK.setEnabled(true);
            jPanel_DataPacking1.button_pindah_SPK.setVisible(true);
        }
        main_panel.add(jPanel_DataPacking1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_JMenuItem_DataPackingActionPerformed

    private void jMenuItem_buyerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_buyerActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_Data_Buyer1.init();
        main_panel.add(jPanel_Data_Buyer1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenuItem_buyerActionPerformed

    private void jMenuItem_GradeBahanJadiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_GradeBahanJadiActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_DataGradeBahanJadi1.init();
        main_panel.add(jPanel_DataGradeBahanJadi1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenuItem_GradeBahanJadiActionPerformed

    private void jMenuItem_stock_bahan_jadiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_stock_bahan_jadiActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_StockBahanJadi1.init();
        main_panel.add(jPanel_StockBahanJadi1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenuItem_stock_bahan_jadiActionPerformed

    private void jMenuItem_DataPengirimanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_DataPengirimanActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_pengiriman1.init();
        main_panel.add(jPanel_pengiriman1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenuItem_DataPengirimanActionPerformed

    private void jMenuItem_Keu_StockBahanBaku_kartuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_Keu_StockBahanBaku_kartuActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_DataBahanBaku_Finance1.init();
        main_panel.add(jPanel_DataBahanBaku_Finance1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenuItem_Keu_StockBahanBaku_kartuActionPerformed

    private void jMenuItem_Keu_StockBahanBaku_gradeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_Keu_StockBahanBaku_gradeActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_DataBahanBaku_PerGrade1.init();
        main_panel.add(jPanel_DataBahanBaku_PerGrade1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenuItem_Keu_StockBahanBaku_gradeActionPerformed

    private void jMenuItem_Keu_HargaLPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_Keu_HargaLPActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_Harga_LaporanProduksi1.init();
        main_panel.add(jPanel_Harga_LaporanProduksi1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenuItem_Keu_HargaLPActionPerformed

    private void jMenuItem_Bonus_GradingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_Bonus_GradingActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_BonusGrading1.init();
        main_panel.add(jPanel_BonusGrading1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenuItem_Bonus_GradingActionPerformed

    private void jMenu_qc_treatmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_qc_treatmentActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_DataTreatment1.init();
        main_panel.add(jPanel_DataTreatment1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenu_qc_treatmentActionPerformed

    private void jMenuItem_LaporanProduksi_BJActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_LaporanProduksi_BJActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_LaporanProduksi_BahanJadi1.init();
        main_panel.add(jPanel_LaporanProduksi_BahanJadi1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenuItem_LaporanProduksi_BJActionPerformed

    private void jMenuItem_PembelianBJActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_PembelianBJActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_PembelianBahanJadi1.init();
        main_panel.add(jPanel_PembelianBahanJadi1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenuItem_PembelianBJActionPerformed

    private void jMenu_TraceabilityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_TraceabilityActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_Traceability1.init();
        main_panel.add(jPanel_Traceability1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenu_TraceabilityActionPerformed

    private void jMenu_hrd_dataLiburActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_hrd_dataLiburActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_Data_TglLibur1.init();
        main_panel.add(jPanel_Data_TglLibur1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenu_hrd_dataLiburActionPerformed

    private void jMenu_baku_AdjustmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_baku_AdjustmentActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_AdjustmentBaku1.init();
        main_panel.add(jPanel_AdjustmentBaku1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenu_baku_AdjustmentActionPerformed

    private void jMenu_hrd_dataTBTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_hrd_dataTBTActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_DataTemanBawaTeman1.init();
        main_panel.add(jPanel_DataTemanBawaTeman1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenu_hrd_dataTBTActionPerformed

    private void jMenu_produksi_data_kinerja_cetakActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_produksi_data_kinerja_cetakActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_DataPencetak1.init();
        main_panel.add(jPanel_DataPencetak1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenu_produksi_data_kinerja_cetakActionPerformed

    private void MenuItem_Keu_BakuKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuItem_Keu_BakuKeluarActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_Harga_BahanBakuKeluar1.init();
        main_panel.add(jPanel_Harga_BahanBakuKeluar1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_MenuItem_Keu_BakuKeluarActionPerformed

    private void jMenu_produksi_isuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_produksi_isuActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_Isu_Produksi1.init();
        main_panel.add(jPanel_Isu_Produksi1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenu_produksi_isuActionPerformed

    private void jMenuItem_Keu_HargaPembelianBarangJadiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_Keu_HargaPembelianBarangJadiActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_Harga_PembelianBarangJadi1.init();
        main_panel.add(jPanel_Harga_PembelianBarangJadi1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenuItem_Keu_HargaPembelianBarangJadiActionPerformed

    private void jMenu_baku_KartuCampuranActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_baku_KartuCampuranActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_Kartu_Campuran1.init();
        main_panel.add(jPanel_Kartu_Campuran1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenu_baku_KartuCampuranActionPerformed

    private void jMenuItem_Keu_LaporanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_Keu_LaporanActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_LaporanProduksi_Keuangan1.init();
        main_panel.add(jPanel_LaporanProduksi_Keuangan1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenuItem_Keu_LaporanActionPerformed

    private void jMenu_hrd_dataFingerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_hrd_dataFingerActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_Absensi_Karyawan1.init();
        main_panel.add(jPanel_Absensi_Karyawan1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenu_hrd_dataFingerActionPerformed

    private void jMenuItem_Keu_HargaCMPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_Keu_HargaCMPActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_Harga_Kartu_Campuran1.init();
        main_panel.add(jPanel_Harga_Kartu_Campuran1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenuItem_Keu_HargaCMPActionPerformed

    private void jMenuItem_Keu_DataKaryawanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_Keu_DataKaryawanActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_DataKaryawan1.init();
        main_panel.add(jPanel_DataKaryawan1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenuItem_Keu_DataKaryawanActionPerformed

    private void jMenuItem_Keu_RekapBTKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_Keu_RekapBTKActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_BiayaTenagaKerja1.init();
        main_panel.add(jPanel_BiayaTenagaKerja1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenuItem_Keu_RekapBTKActionPerformed

    private void jMenuItem_Keu_HariKerjaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_Keu_HariKerjaActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_Hari_kerja1.init();
        main_panel.add(jPanel_Hari_kerja1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenuItem_Keu_HariKerjaActionPerformed

    private void jMenu_TV_KPIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_TV_KPIActionPerformed
        // TODO add your handling code here:
        JFrame_TV_WIP chart = new JFrame_TV_WIP();
        chart.pack();
        chart.setResizable(true);
        chart.setLocationRelativeTo(null);
        chart.setVisible(true);
        chart.setEnabled(true);
        chart.setExtendedState(JFrame.MAXIMIZED_BOTH);
        chart.init(1);
    }//GEN-LAST:event_jMenu_TV_KPIActionPerformed

    private void jMenu_produksi_data_kinerja_f2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_produksi_data_kinerja_f2ActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_DataKinerjaF21.init();
        main_panel.add(jPanel_DataKinerjaF21);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenu_produksi_data_kinerja_f2ActionPerformed

    private void jMenuItem_Keu_biayaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_Keu_biayaActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_Biaya1.init();
        main_panel.add(jPanel_Biaya1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenuItem_Keu_biayaActionPerformed

    private void jMenu_Sub_dataSubActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_Sub_dataSubActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_Sub_Waleta1.init();
        main_panel.add(jPanel_Sub_Waleta1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenu_Sub_dataSubActionPerformed

    private void jMenuItem_BiayaLPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_BiayaLPActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_Rekap_BiayaLP1.init();
        main_panel.add(jPanel_Rekap_BiayaLP1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenuItem_BiayaLPActionPerformed

    private void jMenuItem_Keu_BiayaEksporActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_Keu_BiayaEksporActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_Biaya_Ekspor1.init();
        main_panel.add(jPanel_Biaya_Ekspor1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenuItem_Keu_BiayaEksporActionPerformed

    private void jMenuItem_SPKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_SPKActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_SPK1.init();
        main_panel.add(jPanel_SPK1);
        main_panel.repaint();
        main_panel.revalidate();

        if ('0' == dataMenu.get(AksesMenu.searchMenuByName(dataMenu, AksesMenu.MENU_ITEM_DATA_SPK)).akses.charAt(1)) {
            jPanel_SPK1.button_spk_baru.setEnabled(false);
            jPanel_SPK1.button_fix.setEnabled(false);
            jPanel_SPK1.HidePriceColumn();
        } else {
            jPanel_SPK1.button_spk_baru.setEnabled(true);
            jPanel_SPK1.button_fix.setEnabled(true);
            jPanel_SPK1.ShowPriceColumn();

        }
        if ('0' == dataMenu.get(AksesMenu.searchMenuByName(dataMenu, AksesMenu.MENU_ITEM_DATA_SPK)).akses.charAt(2)) {
            jPanel_SPK1.button_edit_spk.setEnabled(false);
        } else {
            jPanel_SPK1.button_edit_spk.setEnabled(true);
        }
        if ('0' == dataMenu.get(AksesMenu.searchMenuByName(dataMenu, AksesMenu.MENU_ITEM_DATA_SPK)).akses.charAt(3)) {
            jPanel_SPK1.button_delete_spk.setEnabled(false);
        } else {
            jPanel_SPK1.button_delete_spk.setEnabled(true);
        }
    }//GEN-LAST:event_jMenuItem_SPKActionPerformed

    private void jMenu_TV_KPI_SubActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_TV_KPI_SubActionPerformed
        // TODO add your handling code here:
        JFrame_TV_Sub chart = new JFrame_TV_Sub();
        chart.pack();
        chart.setResizable(true);
        chart.setLocationRelativeTo(null);
        chart.setVisible(true);
        chart.setEnabled(true);
        chart.init();
        chart.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }//GEN-LAST:event_jMenu_TV_KPI_SubActionPerformed

    private void jMenuItem_Keu_HargaEstaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_Keu_HargaEstaActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_Harga_BahanBaku_Esta1.init();
        main_panel.add(jPanel_Harga_BahanBaku_Esta1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenuItem_Keu_HargaEstaActionPerformed

    private void jMenuItem_Keu_DataEksporActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_Keu_DataEksporActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_Data_Ekspor1.init();
        main_panel.add(jPanel_Data_Ekspor1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenuItem_Keu_DataEksporActionPerformed

    private void jMenuItem_print_weight_labelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_print_weight_labelActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_DataScanKeping1.init();
        main_panel.add(jPanel_DataScanKeping1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenuItem_print_weight_labelActionPerformed

    private void jMenuItem_KPI_waletaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_KPI_waletaActionPerformed
        // TODO add your handling code here:
        waleta_system.Manajemen.JFrame_KPI_Waleta chart = new waleta_system.Manajemen.JFrame_KPI_Waleta();
        chart.pack();
        chart.setResizable(true);
        chart.setLocationRelativeTo(null);
        chart.setVisible(true);
        chart.setEnabled(true);
        chart.setExtendedState(JFrame.MAXIMIZED_BOTH);
        chart.init();
    }//GEN-LAST:event_jMenuItem_KPI_waletaActionPerformed

    private void jMenuItem_Keu_PaymentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_Keu_PaymentActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_Payment_Report1.init();
        main_panel.add(jPanel_Payment_Report1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenuItem_Keu_PaymentActionPerformed

    private void jMenuItem_PaymentReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_PaymentReportActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_Payment_Report1.init();
        main_panel.add(jPanel_Payment_Report1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenuItem_PaymentReportActionPerformed

    private void jMenuItem_tv_wipActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_tv_wipActionPerformed
        // TODO add your handling code here:
        JFrame_TV_WIP chart = new JFrame_TV_WIP();
        chart.pack();
        chart.setResizable(true);
        chart.setLocationRelativeTo(null);
        chart.setVisible(true);
        chart.setEnabled(true);
        chart.setExtendedState(JFrame.MAXIMIZED_BOTH);
        chart.init(0);
    }//GEN-LAST:event_jMenuItem_tv_wipActionPerformed

    private void jMenuItem_tv_wip1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_tv_wip1ActionPerformed
        // TODO add your handling code here:
        JFrame_TV_WIP chart = new JFrame_TV_WIP();
        chart.pack();
        chart.setResizable(true);
        chart.setLocationRelativeTo(null);
        chart.setVisible(true);
        chart.setEnabled(true);
        chart.setExtendedState(JFrame.MAXIMIZED_BOTH);
        chart.init(0);
    }//GEN-LAST:event_jMenuItem_tv_wip1ActionPerformed

    private void jMenuItem_tv_wip2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_tv_wip2ActionPerformed
        // TODO add your handling code here:
        JFrame_TV_WIP chart = new JFrame_TV_WIP();
        chart.pack();
        chart.setResizable(true);
        chart.setLocationRelativeTo(null);
        chart.setVisible(true);
        chart.setEnabled(true);
        chart.setExtendedState(JFrame.MAXIMIZED_BOTH);
        chart.init(0);
    }//GEN-LAST:event_jMenuItem_tv_wip2ActionPerformed

    private void jMenuItem_PriceListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_PriceListActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_PriceList_GradeBJ1.init();
        main_panel.add(jPanel_PriceList_GradeBJ1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenuItem_PriceListActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
        waleta_system.Manajemen.JFrame_TV_StokBaku frame = new waleta_system.Manajemen.JFrame_TV_StokBaku();
        frame.pack();
        frame.setResizable(true);
        frame.setLocationRelativeTo(this);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
        frame.setEnabled(true);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        // TODO add your handling code here:
        waleta_system.Manajemen.JFrame_KategoriStokJualTV frame = new waleta_system.Manajemen.JFrame_KategoriStokJualTV();
        frame.pack();
        frame.setResizable(true);
        frame.setLocationRelativeTo(this);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
        frame.setEnabled(true);
        frame.init(1);
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem_Keu_CashOnBankActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_Keu_CashOnBankActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_DataCashOnBank1.init();
        main_panel.add(jPanel_DataCashOnBank1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenuItem_Keu_CashOnBankActionPerformed

    private void jMenuItem_Keu_TV_keuangan1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_Keu_TV_keuangan1ActionPerformed
        // TODO add your handling code here:
        waleta_system.Manajemen.JFrame_KPI_Finance Frame = new waleta_system.Manajemen.JFrame_KPI_Finance();
        Frame.pack();
        Frame.setResizable(true);
        Frame.setLocationRelativeTo(this);
        Frame.setVisible(true);
        Frame.setEnabled(true);
        Frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        Frame.init();
    }//GEN-LAST:event_jMenuItem_Keu_TV_keuangan1ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        // TODO add your handling code here:
        waleta_system.Manajemen.JFrame_ARAP_Report Frame = new waleta_system.Manajemen.JFrame_ARAP_Report();
        Frame.pack();
        Frame.setResizable(true);
        Frame.setLocationRelativeTo(this);
        Frame.setVisible(true);
        Frame.setEnabled(true);
        Frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        Frame.init();
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem_Keu_NeracaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_Keu_NeracaActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_Neraca1.init();
        main_panel.add(jPanel_Neraca1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenuItem_Keu_NeracaActionPerformed

    private void jMenuItem_tv_gbjActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_tv_gbjActionPerformed
        // TODO add your handling code here:
        waleta_system.BahanJadi.JFrame_TV_Grading frame = new waleta_system.BahanJadi.JFrame_TV_Grading();
        frame.pack();
        frame.setLocationRelativeTo(this);
        frame.setVisible(true);
        frame.setEnabled(true);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }//GEN-LAST:event_jMenuItem_tv_gbjActionPerformed

    private void jMenuItem_tv_spkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_tv_spkActionPerformed
        // TODO add your handling code here:
        waleta_system.Packing.JFrame_TV_SPK2 chart = new waleta_system.Packing.JFrame_TV_SPK2();
        chart.pack();
        chart.setResizable(true);
        chart.setLocationRelativeTo(null);
        chart.setVisible(true);
        chart.setEnabled(true);
        chart.setExtendedState(JFrame.MAXIMIZED_BOTH);
        chart.init();
    }//GEN-LAST:event_jMenuItem_tv_spkActionPerformed

    private void jMenuItem_tv_spk2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_tv_spk2ActionPerformed
        // TODO add your handling code here:
        waleta_system.Packing.JFrame_TV_SPK_packing chart = new waleta_system.Packing.JFrame_TV_SPK_packing();
        chart.pack();
        chart.setResizable(true);
        chart.setLocationRelativeTo(null);
        chart.setVisible(true);
        chart.setEnabled(true);
        chart.setExtendedState(JFrame.MAXIMIZED_BOTH);
        chart.init();
    }//GEN-LAST:event_jMenuItem_tv_spk2ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        // TODO add your handling code here:
        waleta_system.Manajemen.JFrame_Asset_Report frame = new waleta_system.Manajemen.JFrame_Asset_Report();
        frame.pack();
        frame.setResizable(true);
        frame.setLocationRelativeTo(this);
        frame.setVisible(true);
        frame.setEnabled(true);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.init();
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem_Stock_OpnameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_Stock_OpnameActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_StokOpnameGBJ1.init();
        main_panel.add(jPanel_StokOpnameGBJ1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenuItem_Stock_OpnameActionPerformed

    private void jMenu_qc_treatment_bjActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_qc_treatment_bjActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_Lab_BarangJadi1.init();
        main_panel.add(jPanel_Lab_BarangJadi1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenu_qc_treatment_bjActionPerformed

    private void jMenu_baku_pembelian_bakuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_baku_pembelian_bakuActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_Pembelian_Baku1.init();
        main_panel.add(jPanel_Pembelian_Baku1);
        main_panel.repaint();
        main_panel.revalidate();
        if ('0' == dataMenu.get(AksesMenu.searchMenuByName(dataMenu, AksesMenu.MENU_ITEM_PEMBELIAN_BAHAN_BAKU)).akses.charAt(1)) {
            jPanel_Pembelian_Baku1.button_insert.setEnabled(false);
        } else {
            jPanel_Pembelian_Baku1.button_insert.setEnabled(true);
        }
        if ('0' == dataMenu.get(AksesMenu.searchMenuByName(dataMenu, AksesMenu.MENU_ITEM_PEMBELIAN_BAHAN_BAKU)).akses.charAt(2)) {
            jPanel_Pembelian_Baku1.button_update.setEnabled(false);
        } else {
            jPanel_Pembelian_Baku1.button_update.setEnabled(true);
        }
        if ('0' == dataMenu.get(AksesMenu.searchMenuByName(dataMenu, AksesMenu.MENU_ITEM_PEMBELIAN_BAHAN_BAKU)).akses.charAt(3)) {
            jPanel_Pembelian_Baku1.button_delete.setEnabled(false);
        } else {
            jPanel_Pembelian_Baku1.button_delete.setEnabled(true);
        }
    }//GEN-LAST:event_jMenu_baku_pembelian_bakuActionPerformed

    private void jMenuItem_barcode_packingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_barcode_packingActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_Barcode_Pengiriman1.init();
        main_panel.add(jPanel_Barcode_Pengiriman1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenuItem_barcode_packingActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_HargaBahanBaku1.init();
        main_panel.add(jPanel_HargaBahanBaku1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        // TODO add your handling code here:
        waleta_system.Manajemen.JFrame_RekapPengeluaranBaku frame = new waleta_system.Manajemen.JFrame_RekapPengeluaranBaku();
        frame.pack();
        frame.setResizable(true);
        frame.setLocationRelativeTo(this);
        frame.setVisible(true);
        frame.setEnabled(true);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.init();
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
        // TODO add your handling code here:
        waleta_system.Manajemen.JFrame_Lama_Proses frame = new waleta_system.Manajemen.JFrame_Lama_Proses();
        frame.pack();
        frame.setResizable(true);
        frame.setLocationRelativeTo(this);
        frame.setVisible(true);
        frame.setEnabled(true);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.init();
    }//GEN-LAST:event_jMenuItem7ActionPerformed

    private void jMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem8ActionPerformed
        // TODO add your handling code here:
        waleta_system.Manajemen.JFrame_Treatment_Cuci frame = new waleta_system.Manajemen.JFrame_Treatment_Cuci();
        frame.pack();
        frame.setResizable(true);
        frame.setLocationRelativeTo(this);
        frame.setVisible(true);
        frame.setEnabled(true);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.init();
    }//GEN-LAST:event_jMenuItem8ActionPerformed

    private void jMenuItem_Keu_ARAP_EstaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_Keu_ARAP_EstaActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_Data_ARAP_Esta1.init();
        main_panel.add(jPanel_Data_ARAP_Esta1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenuItem_Keu_ARAP_EstaActionPerformed

    private void jMenuItem9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem9ActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_BonusPetikRSB1.init();
        main_panel.add(jPanel_BonusPetikRSB1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenuItem9ActionPerformed

    private void jMenu_baku_Bonus_PanenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_baku_Bonus_PanenActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_BonusPetikRSB1.init();
        main_panel.add(jPanel_BonusPetikRSB1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenu_baku_Bonus_PanenActionPerformed

    private void jMenuItem11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem11ActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_Rekap_Biaya_per_KartuBaku1.init();
        main_panel.add(jPanel_Rekap_Biaya_per_KartuBaku1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenuItem11ActionPerformed

    private void jMenuItem_data_reprosesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_data_reprosesActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_Reproses1.init();
        main_panel.add(jPanel_Reproses1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenuItem_data_reprosesActionPerformed

    private void jMenu_baku_TV_TreatmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_baku_TV_TreatmentActionPerformed
        // TODO add your handling code here:
        waleta_system.Panel_produksi.JFrame_Tampilan_Treatment chart = new waleta_system.Panel_produksi.JFrame_Tampilan_Treatment();
        chart.pack();
        chart.setResizable(true);
        chart.setLocationRelativeTo(null);
        chart.setVisible(true);
        chart.setEnabled(true);
        chart.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }//GEN-LAST:event_jMenu_baku_TV_TreatmentActionPerformed

    private void jMenu_hrd_dataJamKerjaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_hrd_dataJamKerjaActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_Data_Jam_Kerja1.init();
        main_panel.add(jPanel_Data_Jam_Kerja1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenu_hrd_dataJamKerjaActionPerformed

    private void jMenuItem_Keu_LemburActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_Keu_LemburActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_Lembur_Karyawan1.init();
        main_panel.add(jPanel_Lembur_Karyawan1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenuItem_Keu_LemburActionPerformed

    private void jMenuItem_Keu_SlipHarianActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_Keu_SlipHarianActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        main_panel.add(jPanel_payrol_harian1);
        main_panel.repaint();
        main_panel.revalidate();
        jPanel_payrol_harian1.init();
    }//GEN-LAST:event_jMenuItem_Keu_SlipHarianActionPerformed

    private void jMenu_hrd_karyawan_subActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_hrd_karyawan_subActionPerformed
//        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        main_panel.add(jPanel_Data_Karyawan_Sub1);
        main_panel.repaint();
        main_panel.revalidate();
        jPanel_Data_Karyawan_Sub1.init();
    }//GEN-LAST:event_jMenu_hrd_karyawan_subActionPerformed

    private void jMenu_produksi_cabut_subActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_produksi_cabut_subActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        main_panel.add(jPanel_DataCabutSub1);
        main_panel.repaint();
        main_panel.revalidate();
        jPanel_DataCabutSub1.init();
    }//GEN-LAST:event_jMenu_produksi_cabut_subActionPerformed

    private void jMenu_hrd_dataJalurJemputanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_hrd_dataJalurJemputanActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        main_panel.add(jPanel_Data_JalurJemputan1);
        main_panel.repaint();
        main_panel.revalidate();
        jPanel_Data_JalurJemputan1.init();
    }//GEN-LAST:event_jMenu_hrd_dataJalurJemputanActionPerformed

    private void jMenu_baku_masuk_cheatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_baku_masuk_cheatActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_BahanBakuMasuk_Cheat1.init();
        main_panel.add(jPanel_BahanBakuMasuk_Cheat1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenu_baku_masuk_cheatActionPerformed

    private void jMenuItem_Keu_AbsenTidakMasukActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_Keu_AbsenTidakMasukActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_Absen_Keuangan1.init();
        main_panel.add(jPanel_Absen_Keuangan1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenuItem_Keu_AbsenTidakMasukActionPerformed

    private void jMenu_qc_pemeriksaan_pengirimanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_qc_pemeriksaan_pengirimanActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_Lab_Uji_Pengiriman1.init();
        main_panel.add(jPanel_Lab_Uji_Pengiriman1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenu_qc_pemeriksaan_pengirimanActionPerformed

    private void jMenuItem_Keu_PiutangKaryawanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_Keu_PiutangKaryawanActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_PiutangKaryawan1.init();
        main_panel.add(jPanel_PiutangKaryawan1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenuItem_Keu_PiutangKaryawanActionPerformed

    private void jMenuItem_Keu_TimBantuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_Keu_TimBantuActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_TimBantu1.init();
        main_panel.add(jPanel_TimBantu1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenuItem_Keu_TimBantuActionPerformed

    private void jMenuItem_Keu_DataBarangJadiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_Keu_DataBarangJadiActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_GradingLP_Tutupan1.init();
        main_panel.add(jPanel_GradingLP_Tutupan1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenuItem_Keu_DataBarangJadiActionPerformed

    private void jMenuItem_suhu_kelembapanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_suhu_kelembapanActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_Suhu_dan_Kelembapan1.init();
        main_panel.add(jPanel_Suhu_dan_Kelembapan1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenuItem_suhu_kelembapanActionPerformed

    private void jMenu_hrd_dataFinger_SubActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_hrd_dataFinger_SubActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_Absensi_Sub1.init();
        main_panel.add(jPanel_Absensi_Sub1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenu_hrd_dataFinger_SubActionPerformed

    private void jMenu_baku_data_KHActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_baku_data_KHActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_Dokumen_KH1.init();
        main_panel.add(jPanel_Dokumen_KH1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenu_baku_data_KHActionPerformed

    private void jMenuItem12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem12ActionPerformed
        // TODO add your handling code here:
        waleta_system.Manajemen.JFrame_StokPacking frame = new waleta_system.Manajemen.JFrame_StokPacking();
        frame.pack();
        frame.setResizable(true);
        frame.setLocationRelativeTo(this);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
        frame.setEnabled(true);
        frame.init(1);
    }//GEN-LAST:event_jMenuItem12ActionPerformed

    private void jMenu_Traceability2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_Traceability2ActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_Traceability21.init();
        main_panel.add(jPanel_Traceability21);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenu_Traceability2ActionPerformed

    private void jMenu_Sub_penggajianActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_Sub_penggajianActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_PenggajianSub1.init();
        main_panel.add(jPanel_PenggajianSub1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenu_Sub_penggajianActionPerformed

    private void jMenuItem_Keu_StockOpnameGBJActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_Keu_StockOpnameGBJActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_StokOpnameGBJ_RekapGrade1.init();
        main_panel.add(jPanel_StokOpnameGBJ_RekapGrade1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenuItem_Keu_StockOpnameGBJActionPerformed

    private void jMenu_qc_pemanasan_jadiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_qc_pemanasan_jadiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenu_qc_pemanasan_jadiActionPerformed

    private void jMenu_qc_dokumenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_qc_dokumenActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_Dokumen_QC1.init();
        main_panel.add(jPanel_Dokumen_QC1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenu_qc_dokumenActionPerformed

    private void jMenuItem_Keu_BonusKecepatanF2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_Keu_BonusKecepatanF2ActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_BonusKecepatanF21.init();
        main_panel.add(jPanel_BonusKecepatanF21);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenuItem_Keu_BonusKecepatanF2ActionPerformed

    private void jMenuItem_tv_qcActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_tv_qcActionPerformed
        // TODO add your handling code here:
        waleta_system.QC.JFrame_TV_QC chart = new waleta_system.QC.JFrame_TV_QC();
        chart.pack();
        chart.setResizable(true);
        chart.setLocationRelativeTo(null);
        chart.setVisible(true);
        chart.setEnabled(true);
        chart.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }//GEN-LAST:event_jMenuItem_tv_qcActionPerformed

    private void jMenu_SistemUndianActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_SistemUndianActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
//        jPanel_Undian1.init();
        main_panel.add(jPanel_Undian21);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenu_SistemUndianActionPerformed

    private void jMenu_Input_LemburActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_Input_LemburActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_DataLembur1.init("UMUM");
        main_panel.add(jPanel_DataLembur1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenu_Input_LemburActionPerformed

    private void jMenu_pindah_grupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_pindah_grupActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        main_panel.add(jPanel_DataPindahBagian1);
        jPanel_DataPindahBagian1.init("View Only");
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenu_pindah_grupActionPerformed

    private void jMenu_TV_belumAbsenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_TV_belumAbsenActionPerformed
        // TODO add your handling code here:
        JFrame_Tampilan_TidakAbsen frame = new JFrame_Tampilan_TidakAbsen();
        frame.pack();
        frame.setLocationRelativeTo(this);
        frame.setVisible(true);
        frame.setEnabled(true);
        frame.init();
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }//GEN-LAST:event_jMenu_TV_belumAbsenActionPerformed

    private void jMenu_ExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_ExitActionPerformed
        // TODO add your handling code here:
        int dialogResult = JOptionPane.showConfirmDialog(this, "Are Sure You Want to Quit?", "Warning", 0);
        if (dialogResult == JOptionPane.YES_OPTION) {
            System.exit(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        }
    }//GEN-LAST:event_jMenu_ExitActionPerformed

    private void jMenu_produksi_cetak_subActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_produksi_cetak_subActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        main_panel.add(jPanel_DataCetakSub1);
        main_panel.repaint();
        main_panel.revalidate();
        jPanel_DataCetakSub1.init();
    }//GEN-LAST:event_jMenu_produksi_cetak_subActionPerformed

    private void jMenu_hrd_karyawan_lama_masukActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_hrd_karyawan_lama_masukActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        main_panel.add(jPanel_Data_KaryawanLama_Masuk1);
        main_panel.repaint();
        main_panel.revalidate();
        jPanel_Data_KaryawanLama_Masuk1.init();
    }//GEN-LAST:event_jMenu_hrd_karyawan_lama_masukActionPerformed

    private void jMenu_produksi_stokOpname_LP_WIPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_produksi_stokOpname_LP_WIPActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        main_panel.add(jPanel_StokOpname_WIP1);
        main_panel.repaint();
        main_panel.revalidate();
        jPanel_StokOpname_WIP1.init();
    }//GEN-LAST:event_jMenu_produksi_stokOpname_LP_WIPActionPerformed

    private void jMenu_baku_lp_sesekanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_baku_lp_sesekanActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        main_panel.add(jPanel_Laporan_Produksi_Sesekan1);
        main_panel.repaint();
        main_panel.revalidate();
        jPanel_Laporan_Produksi_Sesekan1.init();

        if ('0' == dataMenu.get(AksesMenu.searchMenuByName(dataMenu, AksesMenu.MENU_ITEM_LAPORAN_PRODUKSI_SESEKAN)).akses.charAt(1)) {
            jPanel_Laporan_Produksi_Sesekan1.button_create_lp_sesekan.setEnabled(false);
        } else {
            jPanel_Laporan_Produksi_Sesekan1.button_create_lp_sesekan.setEnabled(true);
        }
        if ('0' == dataMenu.get(AksesMenu.searchMenuByName(dataMenu, AksesMenu.MENU_ITEM_LAPORAN_PRODUKSI_SESEKAN)).akses.charAt(2)) {
            jPanel_Laporan_Produksi_Sesekan1.button_edit_lp_sesekan.setEnabled(false);
        } else {
            jPanel_Laporan_Produksi_Sesekan1.button_edit_lp_sesekan.setEnabled(true);
        }
        if ('0' == dataMenu.get(AksesMenu.searchMenuByName(dataMenu, AksesMenu.MENU_ITEM_LAPORAN_PRODUKSI_SESEKAN)).akses.charAt(3)) {
            jPanel_Laporan_Produksi_Sesekan1.button_delete_lp_sesekan.setEnabled(false);
        } else {
            jPanel_Laporan_Produksi_Sesekan1.button_delete_lp_sesekan.setEnabled(true);
        }
    }//GEN-LAST:event_jMenu_baku_lp_sesekanActionPerformed

    private void jMenu_Sub_tarif_upahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_Sub_tarif_upahActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        main_panel.add(jPanel_Tarif_upah_sub1);
        main_panel.repaint();
        main_panel.revalidate();
        jPanel_Tarif_upah_sub1.init();
    }//GEN-LAST:event_jMenu_Sub_tarif_upahActionPerformed

    private void jMenu_Sub_Piutang_subActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_Sub_Piutang_subActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        main_panel.add(jPanel_PiutangKaryawan_sub1);
        main_panel.repaint();
        main_panel.revalidate();
        jPanel_PiutangKaryawan_sub1.init();
    }//GEN-LAST:event_jMenu_Sub_Piutang_subActionPerformed

    private void jMenuItem_Keu_dataPayrollActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_Keu_dataPayrollActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        main_panel.add(jPanel_payrol_data1);
        main_panel.repaint();
        main_panel.revalidate();
        jPanel_payrol_data1.init();
    }//GEN-LAST:event_jMenuItem_Keu_dataPayrollActionPerformed

    private void jMenuItem_pack_ProgressPengirimanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_pack_ProgressPengirimanActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        main_panel.add(jPanel_ProgressSPKPengiriman1);
        main_panel.repaint();
        main_panel.revalidate();
        jPanel_ProgressSPKPengiriman1.init();
    }//GEN-LAST:event_jMenuItem_pack_ProgressPengirimanActionPerformed

    private void jMenuItem_sesekan_subActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_sesekan_subActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        main_panel.add(jPanel_DataLPSesekanSub1);
        main_panel.repaint();
        main_panel.revalidate();
        jPanel_DataLPSesekanSub1.init();
    }//GEN-LAST:event_jMenuItem_sesekan_subActionPerformed

    private void jMenu_baku_master_upahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_baku_master_upahActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        main_panel.add(jPanel_MasterUpah1);
        main_panel.repaint();
        main_panel.revalidate();
        jPanel_MasterUpah1.init();
    }//GEN-LAST:event_jMenu_baku_master_upahActionPerformed

    private void jMenu_hrd_karyawan_wltsubActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_hrd_karyawan_wltsubActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        main_panel.add(jPanel_Data_Karyawan_wltsub1);
        main_panel.repaint();
        main_panel.revalidate();
        jPanel_Data_Karyawan_wltsub1.init();
    }//GEN-LAST:event_jMenu_hrd_karyawan_wltsubActionPerformed

    private void jMenu_baku_lp_saponActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_baku_lp_saponActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        main_panel.add(jPanel_Laporan_Produksi_Sapon1);
        main_panel.repaint();
        main_panel.revalidate();
        jPanel_Laporan_Produksi_Sapon1.init();

        if ('0' == dataMenu.get(AksesMenu.searchMenuByName(dataMenu, AksesMenu.MENU_ITEM_LAPORAN_PRODUKSI_SAPON)).akses.charAt(1)) {
            jPanel_Laporan_Produksi_Sapon1.button_create_lp_Sapon.setEnabled(false);
        } else {
            jPanel_Laporan_Produksi_Sapon1.button_create_lp_Sapon.setEnabled(true);
        }
        if ('0' == dataMenu.get(AksesMenu.searchMenuByName(dataMenu, AksesMenu.MENU_ITEM_LAPORAN_PRODUKSI_SAPON)).akses.charAt(2)) {
            jPanel_Laporan_Produksi_Sapon1.button_edit_lp_Sapon.setEnabled(false);
        } else {
            jPanel_Laporan_Produksi_Sapon1.button_edit_lp_Sapon.setEnabled(true);
        }
        if ('0' == dataMenu.get(AksesMenu.searchMenuByName(dataMenu, AksesMenu.MENU_ITEM_LAPORAN_PRODUKSI_SAPON)).akses.charAt(3)) {
            jPanel_Laporan_Produksi_Sapon1.button_delete_lp_Sapon.setEnabled(false);
        } else {
            jPanel_Laporan_Produksi_Sapon1.button_delete_lp_Sapon.setEnabled(true);
        }
    }//GEN-LAST:event_jMenu_baku_lp_saponActionPerformed

    private void jMenu_baku_lp_cheatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_baku_lp_cheatActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        main_panel.add(jPanel_Laporan_Produksi11);
        main_panel.repaint();
        main_panel.revalidate();
        jPanel_Laporan_Produksi11.init();
    }//GEN-LAST:event_jMenu_baku_lp_cheatActionPerformed

    private void jMenu_hrd_dataPersonalHygieneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_hrd_dataPersonalHygieneActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_Data_personalHygiene1.init();
        main_panel.add(jPanel_Data_personalHygiene1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenu_hrd_dataPersonalHygieneActionPerformed

    private void MenuItem_hrd_ijinToiletActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuItem_hrd_ijinToiletActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_ijinToilet1.init();
        main_panel.add(jPanel_ijinToilet1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_MenuItem_hrd_ijinToiletActionPerformed

    private void jMenu_TV_IjinBelumKembaliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_TV_IjinBelumKembaliActionPerformed
        // TODO add your handling code here:
        JFrame_IjinBelumKembali Frame = new JFrame_IjinBelumKembali();
        Frame.pack();
        Frame.setResizable(false);
        Frame.setLocationRelativeTo(this);
        Frame.setVisible(true);
        Frame.setEnabled(true);
        Frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }//GEN-LAST:event_jMenu_TV_IjinBelumKembaliActionPerformed

    private void jMenuItem_peramalan_barangjadiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_peramalan_barangjadiActionPerformed
        // TODO add your handling code here:

        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_Peramalan_barangjadi21.init();
        main_panel.add(jPanel_Peramalan_barangjadi21);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenuItem_peramalan_barangjadiActionPerformed

    private void jMenuItem_MaintenanceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_MaintenanceActionPerformed
        // TODO add your handling code here:
        waleta_system.Maintenance.view.MainFrame_Maintenance Frame = new waleta_system.Maintenance.view.MainFrame_Maintenance();
        Frame.pack();
        Frame.setLocationRelativeTo(this);
        Frame.setVisible(true);
        Frame.setEnabled(true);
//        Frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }//GEN-LAST:event_jMenuItem_MaintenanceActionPerformed

    private void jMenuItem_pengajuan_cuti_webActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_pengajuan_cuti_webActionPerformed
        // TODO add your handling code here:
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            try {
                Desktop.getDesktop().browse(new URI("http://192.168.10.2:5050/waleta/admin/pengajuan_cuti"));
            } catch (URISyntaxException ex) {
                Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jMenuItem_pengajuan_cuti_webActionPerformed

    private void jMenu_produksi_TR_SelaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_produksi_TR_SelaActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_DataTRSela1.init();
        main_panel.add(jPanel_DataTRSela1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenu_produksi_TR_SelaActionPerformed

    private void jMenuItem_aset_maintenanceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_aset_maintenanceActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_Data_Aset1.init();
        main_panel.add(jPanel_Data_Aset1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenuItem_aset_maintenanceActionPerformed

    private void jMenu_hrd_poin_karyawanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_hrd_poin_karyawanActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_SistemPenilaian_Karyawan1.init();
        main_panel.add(jPanel_SistemPenilaian_Karyawan1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenu_hrd_poin_karyawanActionPerformed

    private void jMenuItem_kinerjaGradingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_kinerjaGradingActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_KinerjaKaryawanGrading1.init();
        main_panel.add(jPanel_KinerjaKaryawanGrading1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenuItem_kinerjaGradingActionPerformed

    private void jMenuItem_atb_dataKaryawanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_atb_dataKaryawanActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_DataKaryawanATB1.init();
        main_panel.add(jPanel_DataKaryawanATB1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenuItem_atb_dataKaryawanActionPerformed

    private void jMenuItem_atb_dataProduksiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_atb_dataProduksiActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_ProduksiATB1.init();
        main_panel.add(jPanel_ProduksiATB1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenuItem_atb_dataProduksiActionPerformed

    private void jMenuItem_sapon_subActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_sapon_subActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_DataSaponSub1.init();
        main_panel.add(jPanel_DataSaponSub1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenuItem_sapon_subActionPerformed

    private void jMenuItem10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem10ActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_BonusCabut21.init();
        main_panel.add(jPanel_BonusCabut21);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenuItem10ActionPerformed

    private void jMenuItem_Keu_ShiftMalamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_Keu_ShiftMalamActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_Lembur_ShiftMalam1.init();
        main_panel.add(jPanel_Lembur_ShiftMalam1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenuItem_Keu_ShiftMalamActionPerformed

    private void jMenuItem_kinerja_karyawan_packingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_kinerja_karyawan_packingActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        main_panel.add(jPanel_DataKinerjaPacking1);
        main_panel.repaint();
        main_panel.revalidate();
        jPanel_DataKinerjaPacking1.init();
    }//GEN-LAST:event_jMenuItem_kinerja_karyawan_packingActionPerformed

    private void jMenu_baku_peramalan_gradingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_baku_peramalan_gradingActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        main_panel.add(jPanel_Peramalan_gradingBaku1);
        main_panel.repaint();
        main_panel.revalidate();
        jPanel_Peramalan_gradingBaku1.init();
    }//GEN-LAST:event_jMenu_baku_peramalan_gradingActionPerformed

    private void jMenuItem_Keu_LevelGajiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_Keu_LevelGajiActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        main_panel.add(jPanel_LevelGaji1);
        main_panel.repaint();
        main_panel.revalidate();
        jPanel_LevelGaji1.init();
    }//GEN-LAST:event_jMenuItem_Keu_LevelGajiActionPerformed

    private void jMenuItem_suhu_kelembapan_otomatisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_suhu_kelembapan_otomatisActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        main_panel.add(jPanel_Suhu_dan_Kelembapan21);
        main_panel.repaint();
        main_panel.revalidate();
        jPanel_Suhu_dan_Kelembapan21.init();
    }//GEN-LAST:event_jMenuItem_suhu_kelembapan_otomatisActionPerformed

    private void jMenuItem_FormTidakMasukActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_FormTidakMasukActionPerformed
        // TODO add your handling code here:
        try {
            JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Form_tidak_masuk.jrxml");
            JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
            JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, null, Utility.db.getConnection());
            JasperViewer.viewReport(JASP_PRINT, false);
        } catch (JRException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jMenuItem_FormTidakMasukActionPerformed

    private void jMenuItem_Keu_MasterDataAsetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_Keu_MasterDataAsetActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        main_panel.add(jPanel_Aset_MasterDataAset1);
        main_panel.repaint();
        main_panel.revalidate();
        jPanel_Aset_MasterDataAset1.init();
    }//GEN-LAST:event_jMenuItem_Keu_MasterDataAsetActionPerformed

    private void jMenuItem_Keu_PembelianAsetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_Keu_PembelianAsetActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        main_panel.add(jPanel_Aset_NotaPembelian1);
        main_panel.repaint();
        main_panel.revalidate();
        jPanel_Aset_NotaPembelian1.init();
    }//GEN-LAST:event_jMenuItem_Keu_PembelianAsetActionPerformed

    private void jMenuItem_Keu_LabellingAsetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_Keu_LabellingAsetActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        main_panel.add(jPanel_Aset_UnitAset1);
        main_panel.repaint();
        main_panel.revalidate();
        jPanel_Aset_UnitAset1.init();
        jPanel_Aset_UnitAset1.button_new_stok_aset.setVisible(true);
        jPanel_Aset_UnitAset1.button_edit_kondisi_aset.setVisible(false);
        jPanel_Aset_UnitAset1.button_delete_stok_aset.setVisible(true);
        jPanel_Aset_UnitAset1.button_print_label_1.setVisible(false);
        jPanel_Aset_UnitAset1.button_print_label_All.setVisible(false);
    }//GEN-LAST:event_jMenuItem_Keu_LabellingAsetActionPerformed

    private void jMenuItem_Keu_pengajuan_naik_level_gajiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_Keu_pengajuan_naik_level_gajiActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        main_panel.add(jPanel_PengajuanKenaikanLevelGaji1);
        main_panel.repaint();
        main_panel.revalidate();
        jPanel_PengajuanKenaikanLevelGaji1.init();
    }//GEN-LAST:event_jMenuItem_Keu_pengajuan_naik_level_gajiActionPerformed

    private void jMenuItem_Pengajuan_naikLevelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_Pengajuan_naikLevelActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        main_panel.add(jPanel_PengajuanKenaikanLevelGaji_ViewOnly1);
        main_panel.repaint();
        main_panel.revalidate();
        jPanel_PengajuanKenaikanLevelGaji_ViewOnly1.init();
    }//GEN-LAST:event_jMenuItem_Pengajuan_naikLevelActionPerformed

    private void jMenu_qc_heat_treatmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_qc_heat_treatmentActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        main_panel.add(jPanel_Lab_Heat_Treatment1);
        main_panel.repaint();
        main_panel.revalidate();
        jPanel_Lab_Heat_Treatment1.init();
    }//GEN-LAST:event_jMenu_qc_heat_treatmentActionPerformed

    private void jMenuItem_AbsensiKaryawanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_AbsensiKaryawanActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        main_panel.add(jPanel_Absensi_Karyawan_ViewOnly1);
        main_panel.repaint();
        main_panel.revalidate();
        jPanel_Absensi_Karyawan_ViewOnly1.init();
    }//GEN-LAST:event_jMenuItem_AbsensiKaryawanActionPerformed

    private void jMenuItem_Keu_LemburStaffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_Keu_LemburStaffActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        main_panel.add(jPanel_Lembur_Staff1);
        main_panel.repaint();
        main_panel.revalidate();
        jPanel_Lembur_Staff1.init();
    }//GEN-LAST:event_jMenuItem_Keu_LemburStaffActionPerformed

    private void jMenu_hrd_dataLabelAsetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_hrd_dataLabelAsetActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        main_panel.add(jPanel_Aset_UnitAset1);
        main_panel.repaint();
        main_panel.revalidate();
        jPanel_Aset_UnitAset1.init();
        jPanel_Aset_UnitAset1.button_new_stok_aset.setVisible(false);
        jPanel_Aset_UnitAset1.button_edit_kondisi_aset.setVisible(true);
        jPanel_Aset_UnitAset1.button_delete_stok_aset.setVisible(false);
        jPanel_Aset_UnitAset1.button_print_label_1.setVisible(true);
        jPanel_Aset_UnitAset1.button_print_label_All.setVisible(true);
    }//GEN-LAST:event_jMenu_hrd_dataLabelAsetActionPerformed

    private void jMenuItem_DataKinerjaCabutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_DataKinerjaCabutActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        main_panel.add(jPanel_DataKinerjaCabut1);
        main_panel.repaint();
        main_panel.revalidate();
        jPanel_DataKinerjaCabut1.init();
    }//GEN-LAST:event_jMenuItem_DataKinerjaCabutActionPerformed

    private void jMenuItem_SistemPrintSlipActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_SistemPrintSlipActionPerformed
        // TODO add your handling code here:
        JFrame_SistemPrintSlip Frame = new JFrame_SistemPrintSlip();
        Frame.pack();
        Frame.setLocationRelativeTo(this);
        Frame.setVisible(true);
        Frame.setEnabled(true);
    }//GEN-LAST:event_jMenuItem_SistemPrintSlipActionPerformed

    private void jMenuItem_DataKinerjaCetakActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_DataKinerjaCetakActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        main_panel.add(jPanel_DataKinerjaCetak1);
        main_panel.repaint();
        main_panel.revalidate();
        jPanel_DataKinerjaCetak1.init();
    }//GEN-LAST:event_jMenuItem_DataKinerjaCetakActionPerformed

    private void jMenuItem_Keu_Pengajuan_PembelianActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_Keu_Pengajuan_PembelianActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        main_panel.add(jPanel_Aset_PengajuanPembelian1);
        main_panel.repaint();
        main_panel.revalidate();
        jPanel_Aset_PengajuanPembelian1.init("Keuangan");
    }//GEN-LAST:event_jMenuItem_Keu_Pengajuan_PembelianActionPerformed

    private void jMenuItem_Keu_Pengajuan_Pembelian2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_Keu_Pengajuan_Pembelian2ActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        main_panel.add(jPanel_Aset_PengajuanPembelian1);
        main_panel.repaint();
        main_panel.revalidate();
        jPanel_Aset_PengajuanPembelian1.init("MainMenu");
    }//GEN-LAST:event_jMenuItem_Keu_Pengajuan_Pembelian2ActionPerformed

    private void jMenuItem_tv_qc_belumSetorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_tv_qc_belumSetorActionPerformed
        // TODO add your handling code here:
        waleta_system.QC.JFrame_TV_BelumSetor_QC chart = new waleta_system.QC.JFrame_TV_BelumSetor_QC();
        chart.pack();
        chart.setResizable(true);
        chart.setLocationRelativeTo(null);
        chart.setVisible(true);
        chart.setEnabled(true);
        chart.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }//GEN-LAST:event_jMenuItem_tv_qc_belumSetorActionPerformed

    private void jMenuItem_atb_penilaian_lpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_atb_penilaian_lpActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        main_panel.add(jPanel_ProduksiATB_PenilaianLP1);
        main_panel.repaint();
        main_panel.revalidate();
        jPanel_ProduksiATB_PenilaianLP1.init();
    }//GEN-LAST:event_jMenuItem_atb_penilaian_lpActionPerformed

    private void jMenuItem_atb_kinerja_operatorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_atb_kinerja_operatorActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        main_panel.add(jPanel_Kinerja_Operator_ATB1);
        main_panel.repaint();
        main_panel.revalidate();
        jPanel_Kinerja_Operator_ATB1.init("rnd");
    }//GEN-LAST:event_jMenuItem_atb_kinerja_operatorActionPerformed

    private void MenuItem_Penilaian_BuluUpahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuItem_Penilaian_BuluUpahActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        main_panel.add(jPanel_Penilaian_BuluUpah_LP1);
        main_panel.repaint();
        main_panel.revalidate();
        jPanel_Penilaian_BuluUpah_LP1.init();
    }//GEN-LAST:event_MenuItem_Penilaian_BuluUpahActionPerformed

    private void jMenuItem_DataKaryawanViewOnlyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_DataKaryawanViewOnlyActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        main_panel.add(jPanel_Data_Karyawan_ViewOnly1);
        main_panel.repaint();
        main_panel.revalidate();
        jPanel_Data_Karyawan_ViewOnly1.init();
    }//GEN-LAST:event_jMenuItem_DataKaryawanViewOnlyActionPerformed

    private void jMenu_qc_BahanKimiaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_qc_BahanKimiaActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        main_panel.add(jPanel_DataBahanKimia1);
        main_panel.repaint();
        main_panel.revalidate();
        jPanel_DataBahanKimia1.init();
    }//GEN-LAST:event_jMenu_qc_BahanKimiaActionPerformed

    private void jMenuItem_Keu_StokOpnameWIPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_Keu_StokOpnameWIPActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        main_panel.add(jPanel_StokOpname_WIP_keuangan1);
        main_panel.repaint();
        main_panel.revalidate();
        jPanel_StokOpname_WIP_keuangan1.init();
    }//GEN-LAST:event_jMenuItem_Keu_StokOpnameWIPActionPerformed

    private void jMenu_baku_lp_sesekan_bakuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_baku_lp_sesekan_bakuActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        main_panel.add(jPanel_Laporan_Produksi_Sesekan1);
        main_panel.repaint();
        main_panel.revalidate();
        jPanel_Laporan_Produksi_Sesekan1.init();

        if ('0' == dataMenu.get(AksesMenu.searchMenuByName(dataMenu, AksesMenu.MENU_ITEM_LAPORAN_PRODUKSI_SESEKAN)).akses.charAt(1)) {
            jPanel_Laporan_Produksi_Sesekan1.button_create_lp_sesekan.setEnabled(false);
        } else {
            jPanel_Laporan_Produksi_Sesekan1.button_create_lp_sesekan.setEnabled(true);
        }
        if ('0' == dataMenu.get(AksesMenu.searchMenuByName(dataMenu, AksesMenu.MENU_ITEM_LAPORAN_PRODUKSI_SESEKAN)).akses.charAt(2)) {
            jPanel_Laporan_Produksi_Sesekan1.button_edit_lp_sesekan.setEnabled(false);
        } else {
            jPanel_Laporan_Produksi_Sesekan1.button_edit_lp_sesekan.setEnabled(true);
        }
        if ('0' == dataMenu.get(AksesMenu.searchMenuByName(dataMenu, AksesMenu.MENU_ITEM_LAPORAN_PRODUKSI_SESEKAN)).akses.charAt(3)) {
            jPanel_Laporan_Produksi_Sesekan1.button_delete_lp_sesekan.setEnabled(false);
        } else {
            jPanel_Laporan_Produksi_Sesekan1.button_delete_lp_sesekan.setEnabled(true);
        }
    }//GEN-LAST:event_jMenu_baku_lp_sesekan_bakuActionPerformed

    private void jMenu_baku_lp_sapon_bakuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_baku_lp_sapon_bakuActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        main_panel.add(jPanel_Laporan_Produksi_Sapon1);
        main_panel.repaint();
        main_panel.revalidate();
        jPanel_Laporan_Produksi_Sapon1.init();

        if ('0' == dataMenu.get(AksesMenu.searchMenuByName(dataMenu, AksesMenu.MENU_ITEM_LAPORAN_PRODUKSI_SAPON)).akses.charAt(1)) {
            jPanel_Laporan_Produksi_Sapon1.button_create_lp_Sapon.setEnabled(false);
        } else {
            jPanel_Laporan_Produksi_Sapon1.button_create_lp_Sapon.setEnabled(true);
        }
        if ('0' == dataMenu.get(AksesMenu.searchMenuByName(dataMenu, AksesMenu.MENU_ITEM_LAPORAN_PRODUKSI_SAPON)).akses.charAt(2)) {
            jPanel_Laporan_Produksi_Sapon1.button_edit_lp_Sapon.setEnabled(false);
        } else {
            jPanel_Laporan_Produksi_Sapon1.button_edit_lp_Sapon.setEnabled(true);
        }
        if ('0' == dataMenu.get(AksesMenu.searchMenuByName(dataMenu, AksesMenu.MENU_ITEM_LAPORAN_PRODUKSI_SAPON)).akses.charAt(3)) {
            jPanel_Laporan_Produksi_Sapon1.button_delete_lp_Sapon.setEnabled(false);
        } else {
            jPanel_Laporan_Produksi_Sapon1.button_delete_lp_Sapon.setEnabled(true);
        }
    }//GEN-LAST:event_jMenu_baku_lp_sapon_bakuActionPerformed

    private void jMenuItem_Keu_TutupanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_Keu_TutupanActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        main_panel.add(jPanel_TutupanGradingBahanJadi_Keuangan1);
        main_panel.repaint();
        main_panel.revalidate();
        jPanel_TutupanGradingBahanJadi_Keuangan1.init();
    }//GEN-LAST:event_jMenuItem_Keu_TutupanActionPerformed

    private void jMenuItem_Keu_Bonus_ATBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_Keu_Bonus_ATBActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        main_panel.add(jPanel_Kinerja_Operator_ATB1);
        main_panel.repaint();
        main_panel.revalidate();
        jPanel_Kinerja_Operator_ATB1.init("keuangan");
    }//GEN-LAST:event_jMenuItem_Keu_Bonus_ATBActionPerformed

    private void jMenuItem_Keu_GajiCABUTOActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_Keu_GajiCABUTOActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        main_panel.add(jPanel_GajiCABUTO1);
        main_panel.repaint();
        main_panel.revalidate();
        jPanel_GajiCABUTO1.init();
    }//GEN-LAST:event_jMenuItem_Keu_GajiCABUTOActionPerformed

    private void jMenuItem_Keu_GajiCETAKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_Keu_GajiCETAKActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        main_panel.add(jPanel_GajiCetak1);
        main_panel.repaint();
        main_panel.revalidate();
        jPanel_GajiCetak1.init();
    }//GEN-LAST:event_jMenuItem_Keu_GajiCETAKActionPerformed

    private void jMenuItem_Keu_LemburStaff_baruActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_Keu_LemburStaff_baruActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        main_panel.add(jPanel_Lembur_Staff_new1);
        main_panel.repaint();
        main_panel.revalidate();
        jPanel_Lembur_Staff_new1.init();
    }//GEN-LAST:event_jMenuItem_Keu_LemburStaff_baruActionPerformed

    private void jMenuItem_Keu_Box_BarangJadiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_Keu_Box_BarangJadiActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        main_panel.add(jPanel_BoxBahanJadi_Keuangan1);
        main_panel.repaint();
        main_panel.revalidate();
        jPanel_BoxBahanJadi_Keuangan1.init();
    }//GEN-LAST:event_jMenuItem_Keu_Box_BarangJadiActionPerformed

    private void jMenuItem_Data_MitraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_Data_MitraActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        main_panel.add(jPanel_Data_User_Cabuto1);
        main_panel.repaint();
        main_panel.revalidate();
        jPanel_Data_User_Cabuto1.init();
    }//GEN-LAST:event_jMenuItem_Data_MitraActionPerformed

    private void jMenuItem_Data_LP_CabutoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_Data_LP_CabutoActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        main_panel.add(jPanel_Laporan_Produksi_Cabuto1);
        main_panel.repaint();
        main_panel.revalidate();
        jPanel_Laporan_Produksi_Cabuto1.init();
    }//GEN-LAST:event_jMenuItem_Data_LP_CabutoActionPerformed

    private void jMenuItem_Data_Order_CabutoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_Data_Order_CabutoActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        main_panel.add(jPanel_Data_Order1);
        main_panel.repaint();
        main_panel.revalidate();
        jPanel_Data_Order1.init();
    }//GEN-LAST:event_jMenuItem_Data_Order_CabutoActionPerformed

    private void jMenuItem_Data_GradeBJActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_Data_GradeBJActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        main_panel.add(jPanel_GradeBarangJadi_Cabuto1);
        main_panel.repaint();
        main_panel.revalidate();
        jPanel_GradeBarangJadi_Cabuto1.init();
    }//GEN-LAST:event_jMenuItem_Data_GradeBJActionPerformed

    private void jMenuItem_perhitungan_upahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_perhitungan_upahActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        main_panel.add(jPanel_GajiCABUTO1);
        main_panel.repaint();
        main_panel.revalidate();
        jPanel_GajiCABUTO1.init();
    }//GEN-LAST:event_jMenuItem_perhitungan_upahActionPerformed

    private void Button_refresh_butuh_AccActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_refresh_butuh_AccActionPerformed
        // TODO add your handling code here:
        refresh_Butuh_Acc();
    }//GEN-LAST:event_Button_refresh_butuh_AccActionPerformed

    private void jMenuItem_Shipment_Pickup_RequestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_Shipment_Pickup_RequestActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        main_panel.add(jPanel_Pengiriman_PickUp1);
        main_panel.repaint();
        main_panel.revalidate();
        jPanel_Pengiriman_PickUp1.init();
    }//GEN-LAST:event_jMenuItem_Shipment_Pickup_RequestActionPerformed

    private void jMenu_produksi_data_kinerja_hc_kopyokActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_produksi_data_kinerja_hc_kopyokActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        main_panel.add(jPanel_DataKinerjaCuci_HC_Kopyok1);
        main_panel.repaint();
        main_panel.revalidate();
        jPanel_DataKinerjaCuci_HC_Kopyok1.init();
    }//GEN-LAST:event_jMenu_produksi_data_kinerja_hc_kopyokActionPerformed

    private void jMenu_Sub_ReprosesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_Sub_ReprosesActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        main_panel.add(jPanel_Reproses_Sub1);
        main_panel.repaint();
        main_panel.revalidate();
        jPanel_Reproses_Sub1.init();
    }//GEN-LAST:event_jMenu_Sub_ReprosesActionPerformed

    private void jMenu_qc_suhu_kelembapan_hmiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_qc_suhu_kelembapan_hmiActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        main_panel.add(jPanel_Suhu_dan_Kelembapan21);
        main_panel.repaint();
        main_panel.revalidate();
        jPanel_Suhu_dan_Kelembapan21.init();
    }//GEN-LAST:event_jMenu_qc_suhu_kelembapan_hmiActionPerformed

    private void jMenuItem13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem13ActionPerformed
        // TODO add your handling code here:
        main_panel.removeAll();
        main_panel.repaint();
        main_panel.revalidate();

        //add panel
        jPanel_Dokumen_QC1.init();
        main_panel.add(jPanel_Dokumen_QC1);
        main_panel.repaint();
        main_panel.revalidate();
    }//GEN-LAST:event_jMenuItem13ActionPerformed

    private void Button_refresh_dokumen_jatuh_tempoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_refresh_dokumen_jatuh_tempoActionPerformed
        // TODO add your handling code here:
        refreshTable_data_master_dokumen();
    }//GEN-LAST:event_Button_refresh_dokumen_jatuh_tempoActionPerformed

    private void jMenuItem_tv_spk_lokActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_tv_spk_lokActionPerformed
        // TODO add your handling code here:
        waleta_system.Packing.JFrame_TV_SPK_LOKAL chart = new waleta_system.Packing.JFrame_TV_SPK_LOKAL();
        chart.pack();
        chart.setResizable(true);
        chart.setLocationRelativeTo(null);
        chart.setVisible(true);
        chart.setEnabled(true);
        chart.setExtendedState(JFrame.MAXIMIZED_BOTH);
        chart.init();
    }//GEN-LAST:event_jMenuItem_tv_spk_lokActionPerformed

    private void jMenuItem_tv_spk_lokalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_tv_spk_lokalActionPerformed
        // TODO add your handling code here:
        waleta_system.Packing.JFrame_TV_SPK_LOKAL chart = new waleta_system.Packing.JFrame_TV_SPK_LOKAL();
        chart.pack();
        chart.setResizable(true);
        chart.setLocationRelativeTo(null);
        chart.setVisible(true);
        chart.setEnabled(true);
        chart.setExtendedState(JFrame.MAXIMIZED_BOTH);
        chart.init();
    }//GEN-LAST:event_jMenuItem_tv_spk_lokalActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Button_refresh_butuh_Acc;
    private javax.swing.JButton Button_refresh_dokumen_jatuh_tempo;
    public static javax.swing.JMenuItem JMenuItem_DataPacking;
    private javax.swing.JMenuItem MenuItem_Keu_BakuKeluar;
    private javax.swing.JMenuItem MenuItem_Penilaian_BuluUpah;
    private javax.swing.JMenuItem MenuItem_hrd_ijinToilet;
    private javax.swing.JTable Tabel_butuh_Acc_Lembur;
    private javax.swing.JTable Tabel_butuh_Acc_PengajuanAlat;
    private javax.swing.JTable Tabel_butuh_Acc_PindahBagian;
    private javax.swing.JTable Table_dokumen_jatuh_tempo;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu3;
    public static javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem11;
    private javax.swing.JMenuItem jMenuItem12;
    private javax.swing.JMenuItem jMenuItem13;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JMenuItem jMenuItem_AbsensiKaryawan;
    private javax.swing.JMenuItem jMenuItem_BiayaLP;
    public static javax.swing.JMenuItem jMenuItem_Bonus_Grading;
    private javax.swing.JMenuItem jMenuItem_DataKaryawanViewOnly;
    private javax.swing.JMenuItem jMenuItem_DataKinerjaCabut;
    private javax.swing.JMenuItem jMenuItem_DataKinerjaCetak;
    public static javax.swing.JMenuItem jMenuItem_DataPengiriman;
    private javax.swing.JMenuItem jMenuItem_Data_GradeBJ;
    private javax.swing.JMenuItem jMenuItem_Data_LP_Cabuto;
    private javax.swing.JMenuItem jMenuItem_Data_Mitra;
    private javax.swing.JMenuItem jMenuItem_Data_Order_Cabuto;
    public static javax.swing.JMenuItem jMenuItem_Data_box_bahan_jadi;
    private javax.swing.JMenuItem jMenuItem_FormTidakMasuk;
    public static javax.swing.JMenuItem jMenuItem_GradeBahanJadi;
    private javax.swing.JMenuItem jMenuItem_KPI_waleta;
    public static javax.swing.JMenuItem jMenuItem_Keu_ARAP_Esta;
    private javax.swing.JMenuItem jMenuItem_Keu_AbsenTidakMasuk;
    private javax.swing.JMenuItem jMenuItem_Keu_BiayaEkspor;
    private javax.swing.JMenuItem jMenuItem_Keu_BonusKecepatanF2;
    private javax.swing.JMenuItem jMenuItem_Keu_Bonus_ATB;
    private javax.swing.JMenuItem jMenuItem_Keu_Box_BarangJadi;
    public static javax.swing.JMenuItem jMenuItem_Keu_CashOnBank;
    private javax.swing.JMenuItem jMenuItem_Keu_DataBarangJadi;
    public static javax.swing.JMenuItem jMenuItem_Keu_DataEkspor;
    public static javax.swing.JMenuItem jMenuItem_Keu_DataKaryawan;
    private javax.swing.JMenuItem jMenuItem_Keu_GajiCABUTO;
    private javax.swing.JMenuItem jMenuItem_Keu_GajiCETAK;
    public static javax.swing.JMenuItem jMenuItem_Keu_HargaBaku;
    private javax.swing.JMenuItem jMenuItem_Keu_HargaCMP;
    public static javax.swing.JMenuItem jMenuItem_Keu_HargaEsta;
    public static javax.swing.JMenuItem jMenuItem_Keu_HargaLP;
    public static javax.swing.JMenuItem jMenuItem_Keu_HargaPembelianBarangJadi;
    public static javax.swing.JMenuItem jMenuItem_Keu_HariKerja;
    private javax.swing.JMenuItem jMenuItem_Keu_LabellingAset;
    public static javax.swing.JMenuItem jMenuItem_Keu_Laporan;
    private javax.swing.JMenuItem jMenuItem_Keu_Lembur;
    public static javax.swing.JMenuItem jMenuItem_Keu_LemburStaff;
    public static javax.swing.JMenuItem jMenuItem_Keu_LemburStaff_baru;
    private javax.swing.JMenuItem jMenuItem_Keu_LevelGaji;
    private javax.swing.JMenuItem jMenuItem_Keu_MasterDataAset;
    public static javax.swing.JMenuItem jMenuItem_Keu_Neraca;
    public static javax.swing.JMenuItem jMenuItem_Keu_Payment;
    private javax.swing.JMenuItem jMenuItem_Keu_PembelianAset;
    private javax.swing.JMenuItem jMenuItem_Keu_Pengajuan_Pembelian;
    private javax.swing.JMenuItem jMenuItem_Keu_Pengajuan_Pembelian2;
    private javax.swing.JMenuItem jMenuItem_Keu_PiutangKaryawan;
    private javax.swing.JMenuItem jMenuItem_Keu_RekapBTK;
    private javax.swing.JMenuItem jMenuItem_Keu_ShiftMalam;
    private javax.swing.JMenuItem jMenuItem_Keu_SlipHarian;
    public static javax.swing.JMenuItem jMenuItem_Keu_StockBahanBaku_grade;
    public static javax.swing.JMenuItem jMenuItem_Keu_StockBahanBaku_kartu;
    private javax.swing.JMenuItem jMenuItem_Keu_StockOpnameGBJ;
    private javax.swing.JMenuItem jMenuItem_Keu_StokOpnameWIP;
    private javax.swing.JMenuItem jMenuItem_Keu_TV_keuangan1;
    private javax.swing.JMenuItem jMenuItem_Keu_TimBantu;
    private javax.swing.JMenuItem jMenuItem_Keu_Tutupan;
    public static javax.swing.JMenuItem jMenuItem_Keu_biaya;
    private javax.swing.JMenuItem jMenuItem_Keu_dataPayroll;
    private javax.swing.JMenuItem jMenuItem_Keu_pengajuan_naik_level_gaji;
    public static javax.swing.JMenuItem jMenuItem_LaporanProduksi_BJ;
    private javax.swing.JMenuItem jMenuItem_Maintenance;
    public static javax.swing.JMenuItem jMenuItem_PaymentReport;
    public static javax.swing.JMenuItem jMenuItem_PembelianBJ;
    private javax.swing.JMenuItem jMenuItem_Pengajuan_naikLevel;
    public static javax.swing.JMenuItem jMenuItem_PriceList;
    public static javax.swing.JMenuItem jMenuItem_SPK;
    private javax.swing.JMenuItem jMenuItem_Shipment_Pickup_Request;
    private javax.swing.JMenuItem jMenuItem_SistemPrintSlip;
    private javax.swing.JMenuItem jMenuItem_Stock_Opname;
    private javax.swing.JMenuItem jMenuItem_aset_maintenance;
    public static javax.swing.JMenuItem jMenuItem_atb_dataKaryawan;
    public static javax.swing.JMenuItem jMenuItem_atb_dataProduksi;
    private javax.swing.JMenuItem jMenuItem_atb_kinerja_operator;
    private javax.swing.JMenuItem jMenuItem_atb_penilaian_lp;
    private javax.swing.JMenuItem jMenuItem_barcode_packing;
    public static javax.swing.JMenuItem jMenuItem_buyer;
    private javax.swing.JMenuItem jMenuItem_data_reproses;
    public static javax.swing.JMenuItem jMenuItem_gudang_bahan_jadi;
    private javax.swing.JMenuItem jMenuItem_kinerjaGrading;
    private javax.swing.JMenuItem jMenuItem_kinerja_karyawan_packing;
    private javax.swing.JMenuItem jMenuItem_pack_ProgressPengiriman;
    private javax.swing.JMenuItem jMenuItem_pengajuan_cuti_web;
    private javax.swing.JMenuItem jMenuItem_peramalan_barangjadi;
    private javax.swing.JMenuItem jMenuItem_perhitungan_upah;
    private javax.swing.JMenuItem jMenuItem_print_weight_label;
    private javax.swing.JMenuItem jMenuItem_sapon_sub;
    private javax.swing.JMenuItem jMenuItem_sesekan_sub;
    public static javax.swing.JMenuItem jMenuItem_stock_bahan_jadi;
    private javax.swing.JMenuItem jMenuItem_suhu_kelembapan;
    private javax.swing.JMenuItem jMenuItem_suhu_kelembapan_otomatis;
    public static javax.swing.JMenuItem jMenuItem_tutupan_grading;
    private javax.swing.JMenuItem jMenuItem_tv_gbj;
    private javax.swing.JMenuItem jMenuItem_tv_qc;
    private javax.swing.JMenuItem jMenuItem_tv_qc_belumSetor;
    private javax.swing.JMenuItem jMenuItem_tv_spk;
    private javax.swing.JMenuItem jMenuItem_tv_spk2;
    private javax.swing.JMenuItem jMenuItem_tv_spk_lok;
    private javax.swing.JMenuItem jMenuItem_tv_spk_lokal;
    private javax.swing.JMenuItem jMenuItem_tv_wip;
    private javax.swing.JMenuItem jMenuItem_tv_wip1;
    private javax.swing.JMenuItem jMenuItem_tv_wip2;
    private javax.swing.JMenuItem jMenuItem_user_LogOut;
    private javax.swing.JMenuItem jMenuItem_user_change_pass;
    public static javax.swing.JMenuItem jMenuItem_user_new;
    public static javax.swing.JMenuItem jMenuItem_user_view;
    public static javax.swing.JMenu jMenu_BahanJadi;
    public static javax.swing.JMenu jMenu_Cabuto;
    private javax.swing.JMenuItem jMenu_Exit;
    private javax.swing.JMenu jMenu_File;
    public static javax.swing.JMenu jMenu_HRD;
    private javax.swing.JMenuItem jMenu_Home;
    private javax.swing.JMenuItem jMenu_Input_Lembur;
    public static javax.swing.JMenu jMenu_Keu_Baku;
    public static javax.swing.JMenu jMenu_Keu_BarangJadi;
    public static javax.swing.JMenu jMenu_Keu_HR;
    public static javax.swing.JMenu jMenu_Keu_Produksi;
    public static javax.swing.JMenu jMenu_Keu_Rekap;
    public static javax.swing.JMenu jMenu_Keu_purchasing;
    public static javax.swing.JMenu jMenu_Keuangan;
    private javax.swing.JMenu jMenu_Maintenance;
    public static javax.swing.JMenu jMenu_QC;
    private javax.swing.JMenuItem jMenu_SistemUndian;
    public static javax.swing.JMenuItem jMenu_Sub_Piutang_sub;
    private javax.swing.JMenuItem jMenu_Sub_Reproses;
    public static javax.swing.JMenuItem jMenu_Sub_dataSub;
    public static javax.swing.JMenuItem jMenu_Sub_penggajian;
    public static javax.swing.JMenuItem jMenu_Sub_tarif_upah;
    private javax.swing.JMenuItem jMenu_TV_IjinBelumKembali;
    private javax.swing.JMenuItem jMenu_TV_KPI;
    private javax.swing.JMenuItem jMenu_TV_KPI_Sub;
    private javax.swing.JMenu jMenu_TV_SPK;
    public static javax.swing.JMenuItem jMenu_TV_belumAbsen;
    private javax.swing.JMenuItem jMenu_Traceability;
    private javax.swing.JMenuItem jMenu_Traceability2;
    public static javax.swing.JMenu jMenu_User;
    public static javax.swing.JMenu jMenu_atb;
    public static javax.swing.JMenu jMenu_bahan_baku;
    public static javax.swing.JMenuItem jMenu_baku_Adjustment;
    public static javax.swing.JMenuItem jMenu_baku_Bonus_Panen;
    public static javax.swing.JMenuItem jMenu_baku_Customer;
    public static javax.swing.JMenuItem jMenu_baku_DataBahanBaku;
    public static javax.swing.JMenuItem jMenu_baku_KartuCampuran;
    private javax.swing.JMenuItem jMenu_baku_TV_Treatment;
    private javax.swing.JMenuItem jMenu_baku_data_KH;
    public static javax.swing.JMenuItem jMenu_baku_grade;
    public static javax.swing.JMenuItem jMenu_baku_keluar;
    public static javax.swing.JMenu jMenu_baku_laporan_produksi;
    public static javax.swing.JMenuItem jMenu_baku_lp;
    public static javax.swing.JMenuItem jMenu_baku_lp_cheat;
    public static javax.swing.JMenuItem jMenu_baku_lp_sapon;
    public static javax.swing.JMenuItem jMenu_baku_lp_sapon_baku;
    public static javax.swing.JMenuItem jMenu_baku_lp_sesekan;
    public static javax.swing.JMenuItem jMenu_baku_lp_sesekan_baku;
    public static javax.swing.JMenuItem jMenu_baku_master_upah;
    public static javax.swing.JMenuItem jMenu_baku_masuk;
    public static javax.swing.JMenuItem jMenu_baku_masuk_cheat;
    public static javax.swing.JMenuItem jMenu_baku_pembelian_baku;
    private javax.swing.JMenuItem jMenu_baku_peramalan_grading;
    public static javax.swing.JMenuItem jMenu_baku_rumahburung;
    public static javax.swing.JMenuItem jMenu_baku_supplier;
    public static javax.swing.JMenuItem jMenu_hrd_cuti;
    public static javax.swing.JMenuItem jMenu_hrd_dataFinger;
    protected static javax.swing.JMenuItem jMenu_hrd_dataFinger_Sub;
    public static javax.swing.JMenuItem jMenu_hrd_dataJalurJemputan;
    public static javax.swing.JMenuItem jMenu_hrd_dataJamKerja;
    private javax.swing.JMenuItem jMenu_hrd_dataLabelAset;
    public static javax.swing.JMenuItem jMenu_hrd_dataLibur;
    private javax.swing.JMenuItem jMenu_hrd_dataPersonalHygiene;
    public static javax.swing.JMenuItem jMenu_hrd_dataTBT;
    public static javax.swing.JMenuItem jMenu_hrd_departemen;
    public static javax.swing.JMenuItem jMenu_hrd_grupCabut;
    public static javax.swing.JMenuItem jMenu_hrd_ijinKeluar;
    public static javax.swing.JMenuItem jMenu_hrd_karyawan;
    private javax.swing.JMenuItem jMenu_hrd_karyawan_lama_masuk;
    public static javax.swing.JMenuItem jMenu_hrd_karyawan_sub;
    public static javax.swing.JMenuItem jMenu_hrd_karyawan_wltsub;
    public static javax.swing.JMenuItem jMenu_hrd_lembur;
    private javax.swing.JMenuItem jMenu_hrd_poin_karyawan;
    public static javax.swing.JMenu jMenu_manajemen;
    public static javax.swing.JMenu jMenu_packing;
    private javax.swing.JMenuItem jMenu_pindah_grup;
    public static javax.swing.JMenu jMenu_produksi;
    public static javax.swing.JMenuItem jMenu_produksi_TR_Sela;
    public static javax.swing.JMenuItem jMenu_produksi_bonusM;
    public static javax.swing.JMenuItem jMenu_produksi_cabut;
    public static javax.swing.JMenuItem jMenu_produksi_cabut_sub;
    public static javax.swing.JMenuItem jMenu_produksi_cetak;
    private javax.swing.JMenuItem jMenu_produksi_cetak_sub;
    public static javax.swing.JMenuItem jMenu_produksi_cuci;
    public static javax.swing.JMenuItem jMenu_produksi_data_cabutan;
    private javax.swing.JMenuItem jMenu_produksi_data_kinerja_cetak;
    private javax.swing.JMenuItem jMenu_produksi_data_kinerja_f2;
    private javax.swing.JMenuItem jMenu_produksi_data_kinerja_hc_kopyok;
    public static javax.swing.JMenuItem jMenu_produksi_f2;
    public static javax.swing.JMenuItem jMenu_produksi_isu;
    public static javax.swing.JMenuItem jMenu_produksi_progress;
    public static javax.swing.JMenuItem jMenu_produksi_rendam;
    private javax.swing.JMenuItem jMenu_produksi_stokOpname_LP_WIP;
    private javax.swing.JMenuItem jMenu_qc_BahanKimia;
    public static javax.swing.JMenuItem jMenu_qc_bahanbaku;
    private javax.swing.JMenuItem jMenu_qc_dokumen;
    private javax.swing.JMenuItem jMenu_qc_heat_treatment;
    public static javax.swing.JMenuItem jMenu_qc_lp;
    public javax.swing.JMenuItem jMenu_qc_pemanasan_jadi;
    private javax.swing.JMenuItem jMenu_qc_pemeriksaan_pengiriman;
    private javax.swing.JMenuItem jMenu_qc_suhu_kelembapan_hmi;
    public static javax.swing.JMenuItem jMenu_qc_treatment;
    private javax.swing.JMenuItem jMenu_qc_treatment_bj;
    public static javax.swing.JMenu jMenu_sub;
    private javax.swing.JPanel jPanel1;
    private waleta_system.HRD.JPanel_Absen_Cuti jPanel_Absen_Cuti1;
    private waleta_system.Finance.JPanel_Absen_Keuangan jPanel_Absen_Keuangan1;
    private waleta_system.HRD.JPanel_Absensi_Karyawan jPanel_Absensi_Karyawan1;
    private waleta_system.JPanel_Absensi_Karyawan_ViewOnly jPanel_Absensi_Karyawan_ViewOnly1;
    private waleta_system.SubWaleta.JPanel_Absensi_Sub jPanel_Absensi_Sub1;
    private waleta_system.BahanBaku.JPanel_AdjustmentBaku jPanel_AdjustmentBaku1;
    private waleta_system.Finance.JPanel_Aset_MasterDataAset jPanel_Aset_MasterDataAset1;
    private waleta_system.Finance.JPanel_Aset_NotaPembelian jPanel_Aset_NotaPembelian1;
    private waleta_system.Finance.JPanel_Aset_PengajuanPembelian jPanel_Aset_PengajuanPembelian1;
    private waleta_system.Finance.JPanel_Aset_UnitAset jPanel_Aset_UnitAset1;
    private waleta_system.BahanBaku.JPanel_BahanBakuKeluar jPanel_BahanBakuKeluar1;
    private waleta_system.BahanBaku.JPanel_BahanBakuMasuk jPanel_BahanBakuMasuk1;
    private waleta_system.BahanBaku.JPanel_BahanBakuMasuk_Cheat jPanel_BahanBakuMasuk_Cheat1;
    private waleta_system.BahanJadi.JPanel_BahanJadiMasuk jPanel_BahanJadiMasuk1;
    private waleta_system.Packing.JPanel_Barcode_Pengiriman jPanel_Barcode_Pengiriman1;
    private waleta_system.Finance.JPanel_Biaya jPanel_Biaya1;
    private waleta_system.Finance.JPanel_BiayaTenagaKerja jPanel_BiayaTenagaKerja1;
    private waleta_system.Finance.JPanel_Biaya_Ekspor jPanel_Biaya_Ekspor1;
    private waleta_system.Finance.JPanel_BonusCabut2 jPanel_BonusCabut21;
    private waleta_system.BahanJadi.JPanel_BonusGrading jPanel_BonusGrading1;
    private waleta_system.Finance.JPanel_BonusKecepatanF2 jPanel_BonusKecepatanF21;
    private waleta_system.Finance.JPanel_BonusMangkok jPanel_BonusMangkok1;
    private waleta_system.Manajemen.JPanel_BonusPetikRSB jPanel_BonusPetikRSB1;
    private waleta_system.BahanJadi.JPanel_BoxBahanJadi jPanel_BoxBahanJadi1;
    private waleta_system.Finance.JPanel_BoxBahanJadi_Keuangan jPanel_BoxBahanJadi_Keuangan1;
    private javax.swing.JPanel jPanel_Butuh_Acc;
    private javax.swing.JPanel jPanel_Butuh_Acc1;
    private waleta_system.BahanBaku.JPanel_Customer jPanel_Customer1;
    private waleta_system.BahanBaku.JPanel_DataBahanBaku jPanel_DataBahanBaku1;
    private waleta_system.Finance.JPanel_DataBahanBaku_Finance jPanel_DataBahanBaku_Finance1;
    private waleta_system.Finance.JPanel_DataBahanBaku_PerGrade jPanel_DataBahanBaku_PerGrade1;
    private waleta_system.QC.JPanel_DataBahanKimia jPanel_DataBahanKimia1;
    private waleta_system.Panel_produksi.JPanel_DataCabut jPanel_DataCabut1;
    private waleta_system.SubWaleta.JPanel_DataCabutSub jPanel_DataCabutSub1;
    private waleta_system.Finance.JPanel_DataCashOnBank jPanel_DataCashOnBank1;
    private waleta_system.Panel_produksi.JPanel_DataCetak jPanel_DataCetak1;
    private waleta_system.SubWaleta.JPanel_DataCetakSub jPanel_DataCetakSub1;
    private waleta_system.Panel_produksi.JPanel_DataCuci jPanel_DataCuci1;
    private waleta_system.HRD.JPanel_DataDepartemen jPanel_DataDepartemen1;
    private waleta_system.BahanJadi.JPanel_DataGradeBahanJadi jPanel_DataGradeBahanJadi1;
    private waleta_system.Finance.JPanel_DataKaryawan jPanel_DataKaryawan1;
    private waleta_system.RND.JPanel_DataKaryawanATB jPanel_DataKaryawanATB1;
    private waleta_system.Finance.JPanel_DataKinerjaCabut jPanel_DataKinerjaCabut1;
    private waleta_system.Finance.JPanel_DataKinerjaCetak jPanel_DataKinerjaCetak1;
    private waleta_system.Panel_produksi.JPanel_DataKinerjaCuci_HC_Kopyok jPanel_DataKinerjaCuci_HC_Kopyok1;
    private waleta_system.Panel_produksi.JPanel_DataKinerjaF2 jPanel_DataKinerjaF21;
    private waleta_system.Packing.JPanel_DataKinerjaPacking jPanel_DataKinerjaPacking1;
    private waleta_system.SubWaleta.JPanel_DataLPSesekanSub jPanel_DataLPSesekanSub1;
    private waleta_system.HRD.JPanel_DataLembur jPanel_DataLembur1;
    private waleta_system.Packing.JPanel_DataPacking jPanel_DataPacking1;
    private waleta_system.Finance.JPanel_DataPencabut jPanel_DataPencabut1;
    private waleta_system.Panel_produksi.JPanel_DataPencetak jPanel_DataPencetak1;
    private waleta_system.HRD.JPanel_DataPindahBagian jPanel_DataPindahBagian1;
    private waleta_system.Panel_produksi.JPanel_DataRendam jPanel_DataRendam1;
    private waleta_system.SubWaleta.JPanel_DataSaponSub jPanel_DataSaponSub1;
    private waleta_system.Packing.JPanel_DataScanKeping jPanel_DataScanKeping1;
    private waleta_system.Panel_produksi.JPanel_DataTRSela jPanel_DataTRSela1;
    private waleta_system.HRD.JPanel_DataTemanBawaTeman jPanel_DataTemanBawaTeman1;
    private waleta_system.QC.JPanel_DataTreatment jPanel_DataTreatment1;
    private waleta_system.Finance.JPanel_Data_ARAP_Esta jPanel_Data_ARAP_Esta1;
    private waleta_system.Maintenance.JPanel_Data_Aset jPanel_Data_Aset1;
    private waleta_system.Packing.JPanel_Data_Buyer jPanel_Data_Buyer1;
    private waleta_system.Finance.JPanel_Data_Ekspor jPanel_Data_Ekspor1;
    private waleta_system.HRD.JPanel_Data_JalurJemputan jPanel_Data_JalurJemputan1;
    private waleta_system.HRD.JPanel_Data_Jam_Kerja jPanel_Data_Jam_Kerja1;
    private waleta_system.HRD.JPanel_Data_Karyawan jPanel_Data_Karyawan1;
    private waleta_system.HRD.JPanel_Data_KaryawanLama_Masuk jPanel_Data_KaryawanLama_Masuk1;
    private waleta_system.SubWaleta.JPanel_Data_Karyawan_Sub jPanel_Data_Karyawan_Sub1;
    private waleta_system.JPanel_Data_Karyawan_ViewOnly jPanel_Data_Karyawan_ViewOnly1;
    private waleta_system.HRD.JPanel_Data_Karyawan_wltsub jPanel_Data_Karyawan_wltsub1;
    private waleta_system.Cabuto.JPanel_Data_Order jPanel_Data_Order1;
    private waleta_system.HRD.JPanel_Data_TglLibur jPanel_Data_TglLibur1;
    private waleta_system.User.JPanel_Data_User jPanel_Data_User1;
    private waleta_system.Cabuto.JPanel_Data_User_Cabuto jPanel_Data_User_Cabuto1;
    private waleta_system.HRD.JPanel_Data_personalHygiene jPanel_Data_personalHygiene1;
    private waleta_system.BahanBaku.JPanel_Dokumen_KH jPanel_Dokumen_KH1;
    private waleta_system.QC.JPanel_Dokumen_QC jPanel_Dokumen_QC1;
    private waleta_system.Panel_produksi.JPanel_Finishing2 jPanel_Finishing21;
    private waleta_system.Finance.JPanel_GajiCABUTO jPanel_GajiCABUTO1;
    private waleta_system.Finance.JPanel_GajiCetak jPanel_GajiCetak1;
    private waleta_system.BahanBaku.JPanel_GradeBahanBaku jPanel_GradeBahanBaku1;
    private waleta_system.Cabuto.JPanel_GradeBarangJadi_Cabuto jPanel_GradeBarangJadi_Cabuto1;
    private waleta_system.Finance.JPanel_GradingLP_Tutupan jPanel_GradingLP_Tutupan1;
    private waleta_system.Manajemen.JPanel_HargaBahanBaku jPanel_HargaBahanBaku1;
    private waleta_system.Finance.JPanel_Harga_BahanBaku jPanel_Harga_BahanBaku1;
    private waleta_system.Finance.JPanel_Harga_BahanBakuKeluar jPanel_Harga_BahanBakuKeluar1;
    private waleta_system.Finance.JPanel_Harga_BahanBaku_Esta jPanel_Harga_BahanBaku_Esta1;
    private waleta_system.Finance.JPanel_Harga_Kartu_Campuran jPanel_Harga_Kartu_Campuran1;
    private waleta_system.Finance.JPanel_Harga_LaporanProduksi jPanel_Harga_LaporanProduksi1;
    private waleta_system.Finance.JPanel_Harga_PembelianBarangJadi jPanel_Harga_PembelianBarangJadi1;
    private waleta_system.Finance.JPanel_Hari_kerja jPanel_Hari_kerja1;
    private javax.swing.JPanel jPanel_Home;
    private waleta_system.HRD.JPanel_Ijin_keluar jPanel_Ijin_keluar1;
    private waleta_system.JPanel_Isu_Produksi jPanel_Isu_Produksi1;
    private waleta_system.BahanBaku.JPanel_Kartu_Campuran jPanel_Kartu_Campuran1;
    private waleta_system.BahanJadi.JPanel_KinerjaKaryawanGrading jPanel_KinerjaKaryawanGrading1;
    private waleta_system.RND.JPanel_Kinerja_Operator_ATB jPanel_Kinerja_Operator_ATB1;
    private waleta_system.QC.JPanel_Lab_BahanBaku jPanel_Lab_BahanBaku1;
    private waleta_system.QC.JPanel_Lab_BarangJadi jPanel_Lab_BarangJadi1;
    private waleta_system.QC.JPanel_Lab_Heat_Treatment jPanel_Lab_Heat_Treatment1;
    private waleta_system.QC.JPanel_Lab_LaporanProduksi jPanel_Lab_LaporanProduksi1;
    private waleta_system.QC.JPanel_Lab_Uji_Pengiriman jPanel_Lab_Uji_Pengiriman1;
    private waleta_system.BahanJadi.JPanel_LaporanProduksi_BahanJadi jPanel_LaporanProduksi_BahanJadi1;
    private waleta_system.Finance.JPanel_LaporanProduksi_Keuangan jPanel_LaporanProduksi_Keuangan1;
    private waleta_system.BahanBaku.JPanel_Laporan_Produksi jPanel_Laporan_Produksi1;
    private waleta_system.BahanBaku.JPanel_Laporan_Produksi1 jPanel_Laporan_Produksi11;
    private waleta_system.Cabuto.JPanel_Laporan_Produksi_Cabuto jPanel_Laporan_Produksi_Cabuto1;
    private waleta_system.BahanBaku.JPanel_Laporan_Produksi_Sapon jPanel_Laporan_Produksi_Sapon1;
    private waleta_system.BahanBaku.JPanel_Laporan_Produksi_Sesekan jPanel_Laporan_Produksi_Sesekan1;
    private waleta_system.Finance.JPanel_Lembur_Karyawan jPanel_Lembur_Karyawan1;
    private waleta_system.Finance.JPanel_Lembur_ShiftMalam jPanel_Lembur_ShiftMalam1;
    private waleta_system.Finance.JPanel_Lembur_Staff jPanel_Lembur_Staff1;
    private waleta_system.Finance.JPanel_Lembur_Staff_new jPanel_Lembur_Staff_new1;
    private waleta_system.Finance.JPanel_LevelGaji jPanel_LevelGaji1;
    private waleta_system.BahanBaku.JPanel_MasterUpah jPanel_MasterUpah1;
    private waleta_system.Finance.JPanel_Neraca jPanel_Neraca1;
    private waleta_system.Finance.JPanel_Payment_Report jPanel_Payment_Report1;
    private waleta_system.BahanJadi.JPanel_PembelianBahanJadi jPanel_PembelianBahanJadi1;
    private waleta_system.BahanBaku.JPanel_Pembelian_Baku jPanel_Pembelian_Baku1;
    private waleta_system.Finance.JPanel_PengajuanKenaikanLevelGaji jPanel_PengajuanKenaikanLevelGaji1;
    private waleta_system.JPanel_PengajuanKenaikanLevelGaji_ViewOnly jPanel_PengajuanKenaikanLevelGaji_ViewOnly1;
    private waleta_system.SubWaleta.JPanel_PenggajianSub jPanel_PenggajianSub1;
    private waleta_system.Packing.JPanel_Pengiriman_PickUp jPanel_Pengiriman_PickUp1;
    private waleta_system.Panel_produksi.JPanel_Penilaian_BuluUpah_LP jPanel_Penilaian_BuluUpah_LP1;
    private waleta_system.Packing.JPanel_Peramalan_barangjadi2 jPanel_Peramalan_barangjadi21;
    private waleta_system.BahanBaku.JPanel_Peramalan_gradingBaku jPanel_Peramalan_gradingBaku1;
    private waleta_system.Finance.JPanel_PiutangKaryawan jPanel_PiutangKaryawan1;
    private waleta_system.SubWaleta.JPanel_PiutangKaryawan_sub jPanel_PiutangKaryawan_sub1;
    private waleta_system.BahanJadi.JPanel_PriceList_GradeBJ jPanel_PriceList_GradeBJ1;
    private waleta_system.RND.JPanel_ProduksiATB jPanel_ProduksiATB1;
    private waleta_system.RND.JPanel_ProduksiATB_PenilaianLP jPanel_ProduksiATB_PenilaianLP1;
    private waleta_system.Panel_produksi.JPanel_ProgressLP jPanel_ProgressLP1;
    private waleta_system.Packing.JPanel_ProgressSPKPengiriman jPanel_ProgressSPKPengiriman1;
    private waleta_system.Manajemen.JPanel_Rekap_BiayaLP jPanel_Rekap_BiayaLP1;
    private waleta_system.Manajemen.JPanel_Rekap_Biaya_per_KartuBaku jPanel_Rekap_Biaya_per_KartuBaku1;
    private waleta_system.BahanJadi.JPanel_Reproses jPanel_Reproses1;
    private waleta_system.SubWaleta.JPanel_Reproses_Sub jPanel_Reproses_Sub1;
    private waleta_system.BahanBaku.JPanel_RumahBurung jPanel_RumahBurung1;
    private waleta_system.Packing.JPanel_SPK jPanel_SPK1;
    private waleta_system.HRD.JPanel_SistemPenilaian_Karyawan jPanel_SistemPenilaian_Karyawan1;
    private waleta_system.BahanJadi.JPanel_StockBahanJadi jPanel_StockBahanJadi1;
    private waleta_system.BahanJadi.JPanel_StokOpnameGBJ jPanel_StokOpnameGBJ1;
    private waleta_system.Finance.JPanel_StokOpnameGBJ_RekapGrade jPanel_StokOpnameGBJ_RekapGrade1;
    private waleta_system.Panel_produksi.JPanel_StokOpname_WIP jPanel_StokOpname_WIP1;
    private waleta_system.Finance.JPanel_StokOpname_WIP_keuangan jPanel_StokOpname_WIP_keuangan1;
    private waleta_system.SubWaleta.JPanel_Sub_Waleta jPanel_Sub_Waleta1;
    private waleta_system.Maintenance.JPanel_Suhu_dan_Kelembapan jPanel_Suhu_dan_Kelembapan1;
    private waleta_system.Maintenance.JPanel_Suhu_dan_Kelembapan2 jPanel_Suhu_dan_Kelembapan21;
    private waleta_system.BahanBaku.JPanel_Supplier jPanel_Supplier1;
    private waleta_system.SubWaleta.JPanel_Tarif_upah_sub jPanel_Tarif_upah_sub1;
    private waleta_system.Finance.JPanel_TimBantu jPanel_TimBantu1;
    private waleta_system.JPanel_Traceability jPanel_Traceability1;
    private waleta_system.JPanel_Traceability2 jPanel_Traceability21;
    private waleta_system.BahanJadi.JPanel_TutupanGradingBahanJadi jPanel_TutupanGradingBahanJadi1;
    private waleta_system.Finance.JPanel_TutupanGradingBahanJadi_Keuangan jPanel_TutupanGradingBahanJadi_Keuangan1;
    private waleta_system.JPanel_Undian2 jPanel_Undian21;
    private waleta_system.HRD.JPanel_ijinToilet jPanel_ijinToilet1;
    private javax.swing.JPanel jPanel_need_attention;
    private waleta_system.Finance.JPanel_payrol_data jPanel_payrol_data1;
    private waleta_system.Finance.JPanel_payrol_harian jPanel_payrol_harian1;
    private waleta_system.Packing.JPanel_pengiriman jPanel_pengiriman1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel label_need_attention;
    private javax.swing.JLabel label_total_gram_lp_percobaan;
    private javax.swing.JLabel label_total_keping_lp_percobaan;
    private javax.swing.JLabel label_total_lp_percobaan;
    private javax.swing.JLabel label_warning_stok_baku;
    private javax.swing.JLabel label_warning_stok_qchold;
    private javax.swing.JLabel label_warning_stok_reproses;
    private javax.swing.JPanel main_panel;
    private javax.swing.JTable tabel_data_LP_percobaan;
    // End of variables declaration//GEN-END:variables
}
