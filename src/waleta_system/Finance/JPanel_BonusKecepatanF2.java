package waleta_system.Finance;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.ExportToExcel;
import waleta_system.Class.Utility;
import waleta_system.Interface.InterfacePanel;

public class JPanel_BonusKecepatanF2 extends javax.swing.JPanel implements InterfacePanel {

    
    String sql = null;
    ResultSet rs;
    Date date = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    DecimalFormat decimalFormat = new DecimalFormat();

    public JPanel_BonusKecepatanF2() {
        initComponents();
    }

    @Override
    public void init() {
        try {
            
            
        } catch (Exception ex) {
            Logger.getLogger(JPanel_BonusKecepatanF2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String count_minggu_query(String tgl1, String tgl2) {
        String count_minggu_query = "select COUNT(`selected_date`) AS 'hari_minggu' from \n"
                + "(select adddate('1970-01-01',t4.i*10000 + t3.i*1000 + t2.i*100 + t1.i*10 + t0.i) AS 'selected_date' from\n"
                + " (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t0,\n"
                + " (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t1,\n"
                + " (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t2,\n"
                + " (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t3,\n"
                + " (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t4) v\n"
                + "where `selected_date` between " + tgl1 + " and " + tgl2 + "\n"
                + "AND DAYNAME(`selected_date`) = 'SUNDAY'";
        return count_minggu_query;
    }

    public String count_libur_query(String tgl1, String tgl2) {
        String count_libur_query = "SELECT COUNT(`tanggal_libur`) AS 'hari_libur' FROM `tb_libur` "
                + "WHERE `tanggal_libur` BETWEEN " + tgl1 + " and " + tgl2;
        return count_libur_query;
    }

    public void refreshTable_bonusLP() {
        try {
            DefaultTableModel tabel_model_bonus = (DefaultTableModel) table_bonus_kecepatan.getModel();
            tabel_model_bonus.setRowCount(0);
            double bonus_KK_per_LP = Integer.valueOf(txt_bonus_kk.getText());
            double bonus_F1_per_LP = Integer.valueOf(txt_bonus_f1.getText());
            double bonus_F2_per_LP = Integer.valueOf(txt_bonus_f2.getText());
            double bonus_FC_per_LP = Integer.valueOf(txt_bonus_fc.getText());
            double total_bonus_kk = 0, total_bonus_f1 = 0, total_bonus_f2 = 0, total_bonus_fc = 0;
            String tgl_penggajian = dateFormat.format(Date_Penggajian.getDate());
            sql = "SELECT `tb_finishing_2`.`no_laporan_produksi`, `ruangan`, `jenis_bulu_lp`, `tb_cetak`.`cetak_mangkok`, `tb_cetak`.`cetak_flat`, `tb_tarif_cabut`.`kpg_lp`, `tgl_masuk_f2`, "
                    + "`pekerja_koreksi_kering`, `tgl_dikerjakan_f2`, IF(`tgl_dikerjakan_f2` BETWEEN DATE_ADD('" + tgl_penggajian + "', INTERVAL -7 DAY) AND DATE_ADD('" + tgl_penggajian + "', INTERVAL -1 DAY), `tgl_dikerjakan_f2`, NULL) AS 'tanggal_kk', "
                    + "`pekerja_f1`, `tgl_f1`, IF(`tgl_f1` BETWEEN DATE_ADD('" + tgl_penggajian + "', INTERVAL -7 DAY) AND DATE_ADD('" + tgl_penggajian + "', INTERVAL -1 DAY), `tgl_f1`, NULL) AS 'tanggal_f1', "
                    + "`pekerja_f2`, `tgl_f2`, IF(`tgl_f2` BETWEEN DATE_ADD('" + tgl_penggajian + "', INTERVAL -7 DAY) AND DATE_ADD('" + tgl_penggajian + "', INTERVAL -1 DAY), `tgl_f2`, NULL) AS 'tanggal_f2', "
                    + "`f2_disetor`, `tgl_setor_f2`, IF(`tgl_setor_f2` BETWEEN DATE_ADD('" + tgl_penggajian + "', INTERVAL -7 DAY) AND DATE_ADD('" + tgl_penggajian + "', INTERVAL -1 DAY), `tgl_setor_f2`, NULL) AS 'tanggal_fc' \n"
                    + "FROM `tb_finishing_2` \n"
                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_finishing_2`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_tarif_cabut` ON `tb_laporan_produksi`.`jenis_bulu_lp` = `tb_tarif_cabut`.`bulu_upah`\n"
                    + "LEFT JOIN `tb_cetak` ON `tb_finishing_2`.`no_laporan_produksi` = `tb_cetak`.`no_laporan_produksi`\n"
                    + "WHERE "
                    + "`tb_finishing_2`.`no_laporan_produksi` LIKE '%" + txt_search_lp.getText() + "%' "
                    + "AND `tb_cetak`.`cetak_mangkok` > 0 "
                    + "AND (`pekerja_koreksi_kering` LIKE '%"+txt_search_nama.getText()+"%' OR `pekerja_f1` LIKE '%"+txt_search_nama.getText()+"%' OR  "
                    + "`pekerja_f2` LIKE '%"+txt_search_nama.getText()+"%' OR `f2_disetor` LIKE '%"+txt_search_nama.getText()+"%') "
                    + "AND (`tgl_dikerjakan_f2` BETWEEN DATE_ADD('" + tgl_penggajian + "', INTERVAL -7 DAY) AND DATE_ADD('" + tgl_penggajian + "', INTERVAL -1 DAY) OR "
                    + "`tgl_f1` BETWEEN DATE_ADD('" + tgl_penggajian + "', INTERVAL -7 DAY) AND DATE_ADD('" + tgl_penggajian + "', INTERVAL -1 DAY) OR "
                    + "`tgl_f2` BETWEEN DATE_ADD('" + tgl_penggajian + "', INTERVAL -7 DAY) AND DATE_ADD('" + tgl_penggajian + "', INTERVAL -1 DAY) OR "
                    + "`tgl_setor_f2` BETWEEN DATE_ADD('" + tgl_penggajian + "', INTERVAL -7 DAY) AND DATE_ADD('" + tgl_penggajian + "', INTERVAL -1 DAY))"
                    + "ORDER BY `tb_finishing_2`.`tgl_masuk_f2` DESC";
//            System.out.println(sql);
            PreparedStatement pst = Utility.db.getConnection().prepareStatement(sql);
            rs = pst.executeQuery();
            Object[] row = new Object[25];
            rs.last();
            jProgressBar1.setMaximum(rs.getRow());
            rs.beforeFirst();
            while (rs.next()) {
                jProgressBar1.setValue(jProgressBar1.getValue() + 1);
                double bonus_kk = 0, bonus_f1 = 0, bonus_f2 = 0, bonus_fc = 0;
                row[0] = rs.getString("no_laporan_produksi");
                row[1] = rs.getString("ruangan");
                row[2] = rs.getString("jenis_bulu_lp");
                row[3] = rs.getInt("cetak_mangkok");
                row[4] = rs.getInt("cetak_flat");
                row[5] = rs.getInt("kpg_lp");
                double bobot_lp = Math.round(rs.getFloat("cetak_mangkok") / rs.getFloat("kpg_lp") * 10000d) / 10000d;
                double bobot_lp_kk = Math.round((rs.getFloat("cetak_mangkok") + rs.getFloat("cetak_flat")) / rs.getFloat("kpg_lp") * 10000d) / 10000d;
                row[6] = bobot_lp;
                row[7] = rs.getDate("tgl_masuk_f2");
                row[8] = rs.getString("pekerja_koreksi_kering");
                row[9] = rs.getDate("tgl_dikerjakan_f2");
                if (rs.getDate("tgl_masuk_f2") != null && rs.getDate("tanggal_kk") != null) {
                    int hari = Utility.countDaysWithoutFreeDays(rs.getDate("tgl_masuk_f2"), rs.getDate("tgl_dikerjakan_f2"));
                    row[10] = hari;
                    if (hari <= 3) {
                        bonus_kk = bobot_lp_kk * bonus_KK_per_LP;
                        total_bonus_kk = total_bonus_kk + Math.floor(bonus_kk);
                        row[11] = Math.floor(bonus_kk);
                    } else {
                        bonus_kk = 0;
                        row[11] = 0;
                    }
                } else {
                    row[10] = null;
                    row[11] = 0;
                }
                row[12] = rs.getString("pekerja_f1");
                row[13] = rs.getDate("tgl_f1");
                if (rs.getDate("tgl_dikerjakan_f2") != null && rs.getDate("tanggal_f1") != null) {
                    int hari = Utility.countDaysWithoutFreeDays(rs.getDate("tgl_dikerjakan_f2"), rs.getDate("tgl_f1"));
                    row[14] = hari;
                    if (hari <= 3) {
                        bonus_f1 = bobot_lp * bonus_F1_per_LP;
                        total_bonus_f1 = total_bonus_f1 + Math.floor(bonus_f1);
                        row[15] = Math.floor(bonus_f1);
                    } else {
                        bonus_f1 = 0;
                        row[15] = 0;
                    }
                } else {
                    row[14] = null;
                    row[15] = 0;
                }
                row[16] = rs.getString("pekerja_f2");
                row[17] = rs.getDate("tgl_f2");
                if (rs.getDate("tgl_f1") != null && rs.getDate("tanggal_f2") != null) {
                    int hari = Utility.countDaysWithoutFreeDays(rs.getDate("tgl_f1"), rs.getDate("tgl_f2"));
                    row[18] = hari;
                    if (hari <= 3) {
                        bonus_f2 = bobot_lp * bonus_F2_per_LP;
                        total_bonus_f2 = total_bonus_f2 + Math.floor(bonus_f2);
                        row[19] = Math.floor(bonus_f2);
                    } else {
                        bonus_f2 = 0;
                        row[19] = 0;
                    }
                } else {
                    row[18] = null;
                    row[19] = 0;
                }
                row[20] = rs.getString("f2_disetor");
                row[21] = rs.getDate("tgl_setor_f2");
                if (rs.getDate("tgl_f2") != null && rs.getDate("tanggal_fc") != null) {
                    int hari = Utility.countDaysWithoutFreeDays(rs.getDate("tgl_f2"), rs.getDate("tgl_setor_f2"));
                    row[22] = hari;
                    if (hari <= 3) {
                        bonus_fc = bobot_lp * bonus_FC_per_LP;
                        total_bonus_fc = total_bonus_fc + Math.floor(bonus_fc);
                        row[23] = Math.floor(bonus_fc);
                    } else {
                        bonus_fc = 0;
                        row[23] = 0;
                    }
                } else {
                    row[22] = null;
                    row[23] = 0;
                }
                tabel_model_bonus.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(table_bonus_kecepatan);
            decimalFormat.setMaximumFractionDigits(2);
            label_total_lp.setText("Total LP : " + decimalFormat.format(table_bonus_kecepatan.getRowCount()));
            label_total_bonus_KK.setText("Total Bonus KK : " + decimalFormat.format(total_bonus_kk));
            label_total_bonus_F1.setText("Total Bonus F1 : " + decimalFormat.format(total_bonus_f1));
            label_total_bonus_F2.setText("Total Bonus F2 : " + decimalFormat.format(total_bonus_f2));
            label_total_bonus_FC.setText("Total Bonus FC : " + decimalFormat.format(total_bonus_fc));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_BonusKecepatanF2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_rekap() {
        try {
            DefaultTableModel tabel_model_rekap = (DefaultTableModel) table_rekap.getModel();
            tabel_model_rekap.setRowCount(0);
            double bonus_KK_per_LP = Integer.valueOf(txt_bonus_kk.getText());
            double bonus_F1_per_LP = Integer.valueOf(txt_bonus_f1.getText());
            double bonus_F2_per_LP = Integer.valueOf(txt_bonus_f2.getText());
            double bonus_FC_per_LP = Integer.valueOf(txt_bonus_fc.getText());
            double total_bonus = 0;
            String tgl_penggajian = dateFormat.format(Date_Penggajian.getDate());
//            sql = "SELECT `id_pegawai`, `pekerja`, `bobot_lp` AS 'bobot_lp', FLOOR(`bonus_lp`) AS 'bonus_lp' FROM ("
//                    + "SELECT (SELECT `id_pegawai` FROM `tb_karyawan` WHERE `nama_pegawai` = F2.`pekerja_koreksi_kering` ORDER BY `id_pegawai` DESC LIMIT 1) AS 'id_pegawai', `pekerja_koreksi_kering` AS 'pekerja',\n"
//                    + "IF(DATEDIFF(`tgl_dikerjakan_f2`, `tgl_masuk_f2`)<3, DATEDIFF(`tgl_dikerjakan_f2`, `tgl_masuk_f2`), "
//                    + "DATEDIFF(`tgl_dikerjakan_f2`, `tgl_masuk_f2`) - (" + count_minggu_query("F2.`tgl_masuk_f2`", "F2.`tgl_dikerjakan_f2`") + ") - (" + count_libur_query("F2.`tgl_masuk_f2`", "F2.`tgl_dikerjakan_f2`") + ")) AS 'hari', "
//                    + "(`tb_cetak`.`cetak_mangkok`/`tb_tarif_cabut`.`kpg_lp`) AS 'bobot_lp', "
//                    + "(`tb_cetak`.`cetak_mangkok`/`tb_tarif_cabut`.`kpg_lp`) * " + bonus_KK_per_LP + " AS 'bonus_lp' "
//                    + "FROM `tb_finishing_2` F2\n"
//                    + "LEFT JOIN `tb_laporan_produksi` ON F2.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
//                    + "LEFT JOIN `tb_tarif_cabut` ON `tb_laporan_produksi`.`jenis_bulu_lp` = `tb_tarif_cabut`.`bulu_upah`\n"
//                    + "LEFT JOIN `tb_cetak` ON F2.`no_laporan_produksi` = `tb_cetak`.`no_laporan_produksi`\n"
//                    + "WHERE `tb_cetak`.`cetak_mangkok` > 0 AND (`tgl_dikerjakan_f2` BETWEEN DATE_ADD('" + tgl_penggajian + "', INTERVAL -7 DAY) AND DATE_ADD('" + tgl_penggajian + "', INTERVAL -1 DAY)) "
//                    + ") DATA "
//                    + "WHERE `hari` < 3";
            sql = "SELECT `id_pegawai`, `level_pegawai`, `bagian_pegawai`, `pekerja`, SUM(`bobot_lp`) AS 'bobot_lp', SUM(FLOOR(`bonus_lp`)) AS 'bonus_lp' "
                    + "FROM ("
                    + "SELECT (SELECT `id_pegawai` FROM `tb_karyawan` WHERE `nama_pegawai` = F2.`pekerja_koreksi_kering` ORDER BY `id_pegawai` DESC LIMIT 1) AS 'id_pegawai', "
                    + "(SELECT `level_gaji` FROM `tb_karyawan` WHERE `nama_pegawai` = F2.`pekerja_koreksi_kering` ORDER BY `id_pegawai` DESC LIMIT 1) AS 'level_pegawai', "
                    + "(SELECT `nama_bagian` FROM `tb_karyawan` LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian` WHERE `nama_pegawai` = F2.`pekerja_koreksi_kering` ORDER BY `id_pegawai` DESC LIMIT 1) AS 'bagian_pegawai', "
                    + "`pekerja_koreksi_kering` AS 'pekerja',\n"
                    + "IF(DATEDIFF(`tgl_dikerjakan_f2`, `tgl_masuk_f2`)<3, DATEDIFF(`tgl_dikerjakan_f2`, `tgl_masuk_f2`), "
                    + "DATEDIFF(`tgl_dikerjakan_f2`, `tgl_masuk_f2`) - (" + count_minggu_query("F2.`tgl_masuk_f2`", "F2.`tgl_dikerjakan_f2`") + ") - (" + count_libur_query("F2.`tgl_masuk_f2`", "F2.`tgl_dikerjakan_f2`") + ")) AS 'hari', "
                    + "((`cetak_mangkok`+`cetak_flat`)/`tb_tarif_cabut`.`kpg_lp`) AS 'bobot_lp', "
                    + "((`cetak_mangkok`+`cetak_flat`)/`tb_tarif_cabut`.`kpg_lp`) * " + bonus_KK_per_LP + " AS 'bonus_lp' "
                    + "FROM `tb_finishing_2` F2\n"
                    + "LEFT JOIN `tb_laporan_produksi` ON F2.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_tarif_cabut` ON `tb_laporan_produksi`.`jenis_bulu_lp` = `tb_tarif_cabut`.`bulu_upah`\n"
                    + "LEFT JOIN `tb_cetak` ON F2.`no_laporan_produksi` = `tb_cetak`.`no_laporan_produksi`\n"
                    + "WHERE `tb_cetak`.`cetak_mangkok` > 0 AND (`tgl_dikerjakan_f2` BETWEEN DATE_ADD('" + tgl_penggajian + "', INTERVAL -7 DAY) AND DATE_ADD('" + tgl_penggajian + "', INTERVAL -1 DAY)) "
                    + "UNION ALL "
                    + "SELECT (SELECT `id_pegawai` FROM `tb_karyawan` WHERE `nama_pegawai` = F2.`pekerja_f1` ORDER BY `id_pegawai` DESC LIMIT 1) AS 'id_pegawai', "
                    + "(SELECT `level_gaji` FROM `tb_karyawan` WHERE `nama_pegawai` = F2.`pekerja_f1` ORDER BY `id_pegawai` DESC LIMIT 1) AS 'level_pegawai', "
                    + "(SELECT `nama_bagian` FROM `tb_karyawan` LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian` WHERE `nama_pegawai` = F2.`pekerja_f1` ORDER BY `id_pegawai` DESC LIMIT 1) AS 'bagian_pegawai', "
                    + "`pekerja_f1` AS 'pekerja',\n"
                    + "IF(DATEDIFF(`tgl_f1`, `tgl_dikerjakan_f2`)<3, DATEDIFF(`tgl_f1`, `tgl_dikerjakan_f2`), "
                    + "DATEDIFF(`tgl_f1`, `tgl_dikerjakan_f2`) - (" + count_minggu_query("F2.`tgl_dikerjakan_f2`", "F2.`tgl_f1`") + ") - (" + count_libur_query("F2.`tgl_dikerjakan_f2`", "F2.`tgl_f1`") + ")) AS 'hari', "
                    + "(`tb_cetak`.`cetak_mangkok`/`tb_tarif_cabut`.`kpg_lp`) AS 'bobot_lp', "
                    + "(`tb_cetak`.`cetak_mangkok`/`tb_tarif_cabut`.`kpg_lp`) * " + bonus_F1_per_LP + " AS 'bonus_lp' "
                    + "FROM `tb_finishing_2` F2\n"
                    + "LEFT JOIN `tb_laporan_produksi` ON F2.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_tarif_cabut` ON `tb_laporan_produksi`.`jenis_bulu_lp` = `tb_tarif_cabut`.`bulu_upah`\n"
                    + "LEFT JOIN `tb_cetak` ON F2.`no_laporan_produksi` = `tb_cetak`.`no_laporan_produksi`\n"
                    + "WHERE `tb_cetak`.`cetak_mangkok` > 0 AND (`tgl_f1` BETWEEN DATE_ADD('" + tgl_penggajian + "', INTERVAL -7 DAY) AND DATE_ADD('" + tgl_penggajian + "', INTERVAL -1 DAY)) "
                    + "UNION ALL "
                    + "SELECT (SELECT `id_pegawai` FROM `tb_karyawan` WHERE `nama_pegawai` = F2.`pekerja_f2` ORDER BY `id_pegawai` DESC LIMIT 1) AS 'id_pegawai', "
                    + "(SELECT `level_gaji` FROM `tb_karyawan` WHERE `nama_pegawai` = F2.`pekerja_f2` ORDER BY `id_pegawai` DESC LIMIT 1) AS 'level_pegawai', "
                    + "(SELECT `nama_bagian` FROM `tb_karyawan` LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian` WHERE `nama_pegawai` = F2.`pekerja_f2` ORDER BY `id_pegawai` DESC LIMIT 1) AS 'bagian_pegawai', "
                    + "`pekerja_f2` AS 'pekerja',\n"
                    + "IF(DATEDIFF(`tgl_f2`, `tgl_f1`)<3, DATEDIFF(`tgl_f2`, `tgl_f1`), "
                    + "DATEDIFF(`tgl_f2`, `tgl_f1`) - (" + count_minggu_query("F2.`tgl_f1`", "F2.`tgl_f2`") + ") - (" + count_libur_query("F2.`tgl_f1`", "F2.`tgl_f2`") + ")) AS 'hari', "
                    + "(`tb_cetak`.`cetak_mangkok`/`tb_tarif_cabut`.`kpg_lp`) AS 'bobot_lp', "
                    + "(`tb_cetak`.`cetak_mangkok`/`tb_tarif_cabut`.`kpg_lp`) * " + bonus_F2_per_LP + " AS 'bonus_lp' "
                    + "FROM `tb_finishing_2` F2\n"
                    + "LEFT JOIN `tb_laporan_produksi` ON F2.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_tarif_cabut` ON `tb_laporan_produksi`.`jenis_bulu_lp` = `tb_tarif_cabut`.`bulu_upah`\n"
                    + "LEFT JOIN `tb_cetak` ON F2.`no_laporan_produksi` = `tb_cetak`.`no_laporan_produksi`\n"
                    + "WHERE `tb_cetak`.`cetak_mangkok` > 0 AND (`tgl_f2` BETWEEN DATE_ADD('" + tgl_penggajian + "', INTERVAL -7 DAY) AND DATE_ADD('" + tgl_penggajian + "', INTERVAL -1 DAY)) "
                    + "UNION ALL "
                    + "SELECT (SELECT `id_pegawai` FROM `tb_karyawan` WHERE `nama_pegawai` = F2.`f2_disetor` ORDER BY `id_pegawai` DESC LIMIT 1) AS 'id_pegawai', "
                    + "(SELECT `level_gaji` FROM `tb_karyawan` WHERE `nama_pegawai` = F2.`f2_disetor` ORDER BY `id_pegawai` DESC LIMIT 1) AS 'level_pegawai', "
                    + "(SELECT `nama_bagian` FROM `tb_karyawan` LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian` WHERE `nama_pegawai` = F2.`f2_disetor` ORDER BY `id_pegawai` DESC LIMIT 1) AS 'bagian_pegawai', "
                    + "`f2_disetor` AS 'pekerja',\n"
                    + "IF(DATEDIFF(`tgl_setor_f2`, `tgl_f2`)<3, DATEDIFF(`tgl_setor_f2`, `tgl_f2`), "
                    + "DATEDIFF(`tgl_setor_f2`, `tgl_f2`) - (" + count_minggu_query("F2.`tgl_f2`", "F2.`tgl_setor_f2`") + ") - (" + count_libur_query("F2.`tgl_f2`", "F2.`tgl_setor_f2`") + ")) AS 'hari', "
                    + "(`tb_cetak`.`cetak_mangkok`/`tb_tarif_cabut`.`kpg_lp`) AS 'bobot_lp', "
                    + "(`tb_cetak`.`cetak_mangkok`/`tb_tarif_cabut`.`kpg_lp`) * " + bonus_FC_per_LP + " AS 'bonus_lp' "
                    + "FROM `tb_finishing_2` F2\n"
                    + "LEFT JOIN `tb_laporan_produksi` ON F2.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_tarif_cabut` ON `tb_laporan_produksi`.`jenis_bulu_lp` = `tb_tarif_cabut`.`bulu_upah`\n"
                    + "LEFT JOIN `tb_cetak` ON F2.`no_laporan_produksi` = `tb_cetak`.`no_laporan_produksi`\n"
                    + "WHERE `tb_cetak`.`cetak_mangkok` > 0 AND (`tgl_setor_f2` BETWEEN DATE_ADD('" + tgl_penggajian + "', INTERVAL -7 DAY) AND DATE_ADD('" + tgl_penggajian + "', INTERVAL -1 DAY)) "
                    + ") DATA "
                    + "WHERE `hari` <= 3 AND `id_pegawai` IS NOT NULL \n"
                    + "GROUP BY `id_pegawai`";
//            System.out.println(sql);
            PreparedStatement pst = Utility.db.getConnection().prepareStatement(sql);
            rs = pst.executeQuery();
            Object[] row = new Object[10];
            rs.last();
            jProgressBar_rekap.setMaximum(rs.getRow());
            rs.beforeFirst();
            int no = 0;
            while (rs.next()) {
                jProgressBar_rekap.setValue(jProgressBar_rekap.getValue() + 1);
                no++;
                row[0] = no;
                row[1] = rs.getString("id_pegawai");
                row[2] = rs.getString("pekerja");
                row[3] = Math.floor(rs.getDouble("bobot_lp") * 100d) / 100d;
                if (rs.getString("level_pegawai") != null && rs.getString("level_pegawai").toUpperCase().contains("TRAINING") && CheckBox_bonusTraining0.isSelected()) {
                    row[4] = 0;
                } else if (rs.getDouble("bobot_lp") > 5.99) {
                    row[4] = Math.floor(rs.getDouble("bonus_lp"));
                    total_bonus = total_bonus + Math.floor(rs.getDouble("bonus_lp"));
                } else {
                    row[4] = 0;
                }
                if (rs.getString("bagian_pegawai") != null && rs.getString("bagian_pegawai").toUpperCase().contains("TRAINER")) {
                    row[4] = 0;
                }
                row[5] = rs.getString("level_pegawai");
                row[6] = rs.getString("bagian_pegawai");
                tabel_model_rekap.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(table_rekap);
            label_total_bonus_rekap.setText("Total Bonus : " + decimalFormat.format(total_bonus));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_BonusKecepatanF2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        table_bonus_kecepatan = new javax.swing.JTable();
        button_refresh_bonus = new javax.swing.JButton();
        Date_Penggajian = new com.toedter.calendar.JDateChooser();
        jLabel8 = new javax.swing.JLabel();
        txt_bonus_kk = new javax.swing.JTextField();
        label_total_bonus_KK = new javax.swing.JLabel();
        jProgressBar1 = new javax.swing.JProgressBar();
        jLabel9 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        txt_bonus_f1 = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txt_bonus_f2 = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        txt_bonus_fc = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        label_total_bonus_F1 = new javax.swing.JLabel();
        label_total_bonus_F2 = new javax.swing.JLabel();
        label_total_bonus_FC = new javax.swing.JLabel();
        label_total_lp = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jProgressBar_rekap = new javax.swing.JProgressBar();
        button_save_data_bonus_kecepatan = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        label_total_bonus_rekap = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        button_export_bonus_LP = new javax.swing.JButton();
        jScrollPane6 = new javax.swing.JScrollPane();
        table_rekap = new javax.swing.JTable();
        button_refresh_rekap = new javax.swing.JButton();
        CheckBox_bonusTraining0 = new javax.swing.JCheckBox();
        jLabel12 = new javax.swing.JLabel();
        txt_search_lp = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        txt_search_nama = new javax.swing.JTextField();

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Data Bonus Kecepatan F2", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        table_bonus_kecepatan.setAutoCreateRowSorter(true);
        table_bonus_kecepatan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_bonus_kecepatan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No LP", "Ruang", "Bulu", "Kpg MK", "Kpg Flat", "Kpg Besar", "Bobot", "Tgl Masuk", "Pekerja Koreksi", "Tgl KK", "Hari", "Bonus KK", "Pekerja F1", "Tgl F1", "Hari", "Bonus F1", "Pekerja F2", "Tgl F2", "Hari", "Bonus F2", "Pekerja Final C", "Tgl Setor", "Hari", "Bonus FC"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Float.class, java.lang.Object.class, java.lang.String.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Float.class, java.lang.String.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Float.class, java.lang.String.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Float.class, java.lang.String.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table_bonus_kecepatan.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(table_bonus_kecepatan);

        button_refresh_bonus.setBackground(new java.awt.Color(255, 255, 255));
        button_refresh_bonus.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_refresh_bonus.setText("Refresh");
        button_refresh_bonus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refresh_bonusActionPerformed(evt);
            }
        });

        Date_Penggajian.setBackground(new java.awt.Color(255, 255, 255));
        Date_Penggajian.setDate(new Date());
        Date_Penggajian.setDateFormatString("dd MMM yyyy");
        Date_Penggajian.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel8.setText("Bonus KK / LP : ");

        txt_bonus_kk.setEditable(false);
        txt_bonus_kk.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_bonus_kk.setText("1250");

        label_total_bonus_KK.setBackground(new java.awt.Color(255, 255, 255));
        label_total_bonus_KK.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_bonus_KK.setText("Total Bonus KK");

        jProgressBar1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel9.setText("LOADING  :");

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel13.setText("Bonus F1 / LP : ");

        txt_bonus_f1.setEditable(false);
        txt_bonus_f1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_bonus_f1.setText("1750");

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel10.setText("Bonus F2 / LP : ");

        txt_bonus_f2.setEditable(false);
        txt_bonus_f2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_bonus_f2.setText("1750");

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel14.setText("Bonus Final Check / LP : ");

        txt_bonus_fc.setEditable(false);
        txt_bonus_fc.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_bonus_fc.setText("250");

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel11.setText("Tgl Penggajian :");

        label_total_bonus_F1.setBackground(new java.awt.Color(255, 255, 255));
        label_total_bonus_F1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_bonus_F1.setText("Total Bonus F1");

        label_total_bonus_F2.setBackground(new java.awt.Color(255, 255, 255));
        label_total_bonus_F2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_bonus_F2.setText("Total Bonus F2");

        label_total_bonus_FC.setBackground(new java.awt.Color(255, 255, 255));
        label_total_bonus_FC.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_bonus_FC.setText("Total Bonus FC");

        label_total_lp.setBackground(new java.awt.Color(255, 255, 255));
        label_total_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_lp.setText("Total LP");

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        jProgressBar_rekap.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        button_save_data_bonus_kecepatan.setBackground(new java.awt.Color(255, 255, 255));
        button_save_data_bonus_kecepatan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_save_data_bonus_kecepatan.setText("Save Data Bonus");
        button_save_data_bonus_kecepatan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_save_data_bonus_kecepatanActionPerformed(evt);
            }
        });

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel4.setText("Bonus karyawan /Minggu");

        label_total_bonus_rekap.setBackground(new java.awt.Color(255, 255, 255));
        label_total_bonus_rekap.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        label_total_bonus_rekap.setText("Total");

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setText("NOTES :\n1. Tergabung dalam kelompok Finishing ruang.\n2. Lama inap MAX 3 hari kerja (tidak terhitung hari libur / HARI MINGGU). contoh : tgl 1 sampai 3 = 2 Hari.\n3. LP di kerjakan > 5.99 Bobot LP.\n4. Bobot LP = Keping Mangkok / Kpg LP Besar. \n5. Bobot LP khusus pekerja koreksi = (Kpg Mangkok + Kpg Flat) / Kpg LP Besar.\n6. Bonus Kecepatan = Bobot LP dikerjakan x Tarif Bonus.\n7. tidak termasuk TRAINER");
        jScrollPane5.setViewportView(jTextArea1);

        button_export_bonus_LP.setBackground(new java.awt.Color(255, 255, 255));
        button_export_bonus_LP.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_bonus_LP.setText("Export");
        button_export_bonus_LP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_bonus_LPActionPerformed(evt);
            }
        });

        table_rekap.setAutoCreateRowSorter(true);
        table_rekap.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_rekap.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "ID karyawan", "Nama Karyawan", "Jumlah LP", "Bonus Kec.", "Level Gaji", "Bagian"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Float.class, java.lang.String.class, java.lang.String.class
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
        table_rekap.getTableHeader().setReorderingAllowed(false);
        jScrollPane6.setViewportView(table_rekap);

        button_refresh_rekap.setBackground(new java.awt.Color(255, 255, 255));
        button_refresh_rekap.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_refresh_rekap.setText("Refresh");
        button_refresh_rekap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refresh_rekapActionPerformed(evt);
            }
        });

        CheckBox_bonusTraining0.setBackground(new java.awt.Color(255, 255, 255));
        CheckBox_bonusTraining0.setSelected(true);
        CheckBox_bonusTraining0.setText("Bonus Training 0");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 550, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jProgressBar_rekap, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(CheckBox_bonusTraining0)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_refresh_rekap)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_export_bonus_LP)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_save_data_bonus_kecepatan))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(label_total_bonus_rekap)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(label_total_bonus_rekap))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jProgressBar_rekap, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(button_save_data_bonus_kecepatan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_export_bonus_LP, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_refresh_rekap, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(CheckBox_bonusTraining0, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 472, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel12.setText("No LP :");

        txt_search_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel15.setText("Nama :");

        txt_search_nama.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_bonus_kk, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_bonus_f1, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_bonus_f2, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_bonus_fc, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(label_total_lp)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(label_total_bonus_KK)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(label_total_bonus_F1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(label_total_bonus_F2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(label_total_bonus_FC))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_Penggajian, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_nama, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_refresh_bonus)))
                        .addGap(0, 48, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(Date_Penggajian, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_search_nama, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_search_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(button_refresh_bonus, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_bonus_kk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_bonus_f1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_bonus_f2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_bonus_fc, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_total_bonus_KK, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_bonus_F1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_bonus_F2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_bonus_FC, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 576, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

    private void button_refresh_bonusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refresh_bonusActionPerformed
        // TODO add your handling code here:
        boolean check = true;
        try {
            double bonus_KK_per_LP = Integer.valueOf(txt_bonus_kk.getText());
            double bonus_F1_per_LP = Integer.valueOf(txt_bonus_f1.getText());
            double bonus_F2_per_LP = Integer.valueOf(txt_bonus_f2.getText());
            double bonus_FC_per_LP = Integer.valueOf(txt_bonus_fc.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Nominal bonus salah !");
            check = false;
        }
        if (Date_Penggajian.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Filter Tanggal tidak boleh kosong !");
            check = false;
        } else if (!(new SimpleDateFormat("EEEEE").format(Date_Penggajian.getDate()).toUpperCase().equals("THURSDAY") || new SimpleDateFormat("EEEEE").format(Date_Penggajian.getDate()).toUpperCase().equals("KAMIS"))) {
            check = false;
            JOptionPane.showMessageDialog(this, "Tanggal penggajian seharusnya hari kamis");
        }

        if (check) {
            Date_Penggajian.setEnabled(false);
            button_refresh_bonus.setEnabled(false);
            button_save_data_bonus_kecepatan.setEnabled(false);
            try {
                jProgressBar1.setMinimum(0);
                jProgressBar1.setValue(0);
                jProgressBar1.setStringPainted(true);
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        refreshTable_bonusLP();
                        jProgressBar1.setValue(jProgressBar1.getMaximum());
                        JOptionPane.showMessageDialog(null, "Proses Selesai !");
                        Date_Penggajian.setEnabled(true);
                        button_refresh_bonus.setEnabled(true);
                        button_save_data_bonus_kecepatan.setEnabled(true);
                    }
                };
                thread.start();
            } catch (Exception e) {
                Logger.getLogger(JPanel_BonusKecepatanF2.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }//GEN-LAST:event_button_refresh_bonusActionPerformed

    private void button_save_data_bonus_kecepatanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_save_data_bonus_kecepatanActionPerformed
        // TODO add your handling code here:
        int dialogResult = JOptionPane.showConfirmDialog(this, "Save " + table_rekap.getRowCount() + " data ?", "Warning", 0);
        if (dialogResult == JOptionPane.YES_OPTION) {
            try {
                for (int i = 0; i < table_rekap.getRowCount(); i++) {
                    String Query = "INSERT INTO `tb_lembur_rekap`(`id_pegawai`, `tanggal`, `bonus1_kecepatan`) "
                            + "VALUES ("
                            + "'" + table_rekap.getValueAt(i, 1).toString() + "',"
                            + "DATE_ADD('" + dateFormat.format(Date_Penggajian.getDate()) + "', INTERVAL -1 DAY),"
                            + table_rekap.getValueAt(i, 4).toString() + ") "
                            + "ON DUPLICATE KEY UPDATE "
                            + "`bonus1_kecepatan`=" + table_rekap.getValueAt(i, 4).toString();
//                    System.out.println(Query);
                    Utility.db.getConnection().createStatement();
                    Utility.db.getStatement().executeUpdate(Query);
                }
                JOptionPane.showMessageDialog(this, "Data Saved Successfully");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Save Failed !" + e);
                Logger.getLogger(JPanel_BonusKecepatanF2.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }//GEN-LAST:event_button_save_data_bonus_kecepatanActionPerformed

    private void button_export_bonus_LPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_bonus_LPActionPerformed
        // TODO add your handling code here:
        DefaultTableModel table = (DefaultTableModel) table_rekap.getModel();
        ExportToExcel.writeToExcel(table, this);
    }//GEN-LAST:event_button_export_bonus_LPActionPerformed

    private void button_refresh_rekapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refresh_rekapActionPerformed
        // TODO add your handling code here:
        boolean check = true;
        try {
            double bonus_KK_per_LP = Integer.valueOf(txt_bonus_kk.getText());
            double bonus_F1_per_LP = Integer.valueOf(txt_bonus_f1.getText());
            double bonus_F2_per_LP = Integer.valueOf(txt_bonus_f2.getText());
            double bonus_FC_per_LP = Integer.valueOf(txt_bonus_fc.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Nominal bonus salah !");
            check = false;
        }
        if (Date_Penggajian.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Filter Tanggal tidak boleh kosong !");
            check = false;
        } else if (!(new SimpleDateFormat("EEEEE").format(Date_Penggajian.getDate()).toUpperCase().equals("THURSDAY") || new SimpleDateFormat("EEEEE").format(Date_Penggajian.getDate()).toUpperCase().equals("KAMIS"))) {
            check = false;
            JOptionPane.showMessageDialog(this, "Tanggal penggajian seharusnya hari kamis");
        }
        if (check) {
            Date_Penggajian.setEnabled(false);
            button_refresh_bonus.setEnabled(false);
            button_refresh_rekap.setEnabled(false);
            button_save_data_bonus_kecepatan.setEnabled(false);
            try {
                jProgressBar_rekap.setMinimum(0);
                jProgressBar_rekap.setValue(0);
                jProgressBar_rekap.setStringPainted(true);
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        refreshTable_rekap();
                        jProgressBar_rekap.setValue(jProgressBar_rekap.getMaximum());
                        JOptionPane.showMessageDialog(null, "Proses Selesai !");
                        Date_Penggajian.setEnabled(true);
                        button_refresh_bonus.setEnabled(true);
                        button_refresh_rekap.setEnabled(true);
                        button_save_data_bonus_kecepatan.setEnabled(true);
                    }
                };
                thread.start();
            } catch (Exception e) {
                Logger.getLogger(JPanel_BonusKecepatanF2.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }//GEN-LAST:event_button_refresh_rekapActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox CheckBox_bonusTraining0;
    private com.toedter.calendar.JDateChooser Date_Penggajian;
    private javax.swing.JButton button_export_bonus_LP;
    private javax.swing.JButton button_refresh_bonus;
    private javax.swing.JButton button_refresh_rekap;
    private javax.swing.JButton button_save_data_bonus_kecepatan;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JProgressBar jProgressBar_rekap;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JLabel label_total_bonus_F1;
    private javax.swing.JLabel label_total_bonus_F2;
    private javax.swing.JLabel label_total_bonus_FC;
    private javax.swing.JLabel label_total_bonus_KK;
    private javax.swing.JLabel label_total_bonus_rekap;
    private javax.swing.JLabel label_total_lp;
    private javax.swing.JTable table_bonus_kecepatan;
    private javax.swing.JTable table_rekap;
    private javax.swing.JTextField txt_bonus_f1;
    private javax.swing.JTextField txt_bonus_f2;
    private javax.swing.JTextField txt_bonus_fc;
    private javax.swing.JTextField txt_bonus_kk;
    private javax.swing.JTextField txt_search_lp;
    private javax.swing.JTextField txt_search_nama;
    // End of variables declaration//GEN-END:variables

}
