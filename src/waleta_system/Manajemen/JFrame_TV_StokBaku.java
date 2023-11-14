package waleta_system.Manajemen;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import waleta_system.BahanBaku.JDialog_LaporanBaku_perGrade;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;

public class JFrame_TV_StokBaku extends javax.swing.JFrame {

    String sql = null;
    ResultSet rs;
    Date today = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    DecimalFormat decimalFormat = new DecimalFormat();
    PreparedStatement pst;
    float stok_BRS_WLT = 0, stok_BR_WLT = 0, stok_BS_WLT = 0, stok_BuluLain_WLT = 0;

    public JFrame_TV_StokBaku() {
        initComponents();
        Thread thread = new Thread() {
            @Override
            public void run() {
                Refresh_TabelKetahanan();
                Load_Ketahanan_Waleta();
                Refresh_BP_Unworkable();
                Refresh_proses_grading();
                JOptionPane.showMessageDialog(null, "Proses Selesai");
            }
        };
        thread.start();
    }

    public void Refresh_TabelKetahanan() {
        decimalFormat.setMaximumFractionDigits(0);
        int kpg_masuk = 0, gram_masuk = 0, kpg_lp = 0, gram_lp = 0, kpg_jual = 0, gram_keluar = 0, kpg_cmp = 0, gram_cmp = 0;
        double nilai_masuk = 0, nilai_lp = 0, nilai_jual = 0, nilai_cmp = 0, total_nilai_stok_wlt = 0, total_nilai_stok_sub = 0;
        float stok_waleta = 0, stok_sub = 0;
        ResultSet rs_grading, rs_jual, rs_lp, rs_cmp;
        try {

            DefaultTableModel model_waleta = (DefaultTableModel) table_stok_for_waleta.getModel();
            model_waleta.setRowCount(0);
            DefaultTableModel model_sub = (DefaultTableModel) table_stok_for_sub.getModel();
            model_sub.setRowCount(0);
            sql = "SELECT `jenis_bentuk`, `jenis_bulu`, `kategori_proses` FROM `tb_grade_bahan_baku` "
                    + "WHERE `kategori_proses` IN ('WALETA', 'SUB') GROUP BY `jenis_bentuk`, `jenis_bulu`, `kategori_proses`";
            pst = Utility.db.getConnection().prepareStatement(sql);
            rs = pst.executeQuery();
            Object[] row = new Object[5];
            while (rs.next()) {
                String sql_grading = "SELECT SUM(`tb_grading_bahan_baku`.`jumlah_keping`) AS 'jumlah_keping', SUM(`tb_grading_bahan_baku`.`total_berat`) AS 'total_berat', SUM(`harga_bahanbaku` * `tb_grading_bahan_baku`.`total_berat`) AS 'harga' "
                        + "FROM `tb_grading_bahan_baku` "
                        + "LEFT JOIN `tb_bahan_baku_masuk` ON `tb_grading_bahan_baku`.`no_kartu_waleta` = `tb_bahan_baku_masuk`.`no_kartu_waleta`"
                        + "LEFT JOIN `tb_grade_bahan_baku` ON `tb_grading_bahan_baku`.`kode_grade` = `tb_grade_bahan_baku`.`kode_grade`"
                        + "WHERE `tb_grade_bahan_baku`.`jenis_bentuk` = '" + rs.getString("jenis_bentuk") + "' "
                        + "AND `tb_grade_bahan_baku`.`jenis_bulu` = '" + rs.getString("jenis_bulu") + "' "
                        + "AND `tb_grade_bahan_baku`.`kategori_proses` = '" + rs.getString("kategori_proses") + "' "
                        + "AND `tgl_masuk`<='" + dateFormat.format(today) + "'";
                rs_grading = Utility.db.getStatement().executeQuery(sql_grading);
                if (rs_grading.next()) {
                    kpg_masuk = rs_grading.getInt("jumlah_keping");
                    gram_masuk = rs_grading.getInt("total_berat");
                    nilai_masuk = rs_grading.getDouble("harga");
                }
                String sql_lp = "SELECT SUM(`tb_laporan_produksi`.`jumlah_keping`) AS 'keping', SUM(`tb_laporan_produksi`.`berat_basah`) AS 'berat', SUM(`tb_grading_bahan_baku`.`harga_bahanbaku` * `tb_laporan_produksi`.`berat_basah`) AS 'harga' "
                        + "FROM `tb_laporan_produksi` "
                        + "LEFT JOIN `tb_grade_bahan_baku` ON `tb_laporan_produksi`.`kode_grade` = `tb_grade_bahan_baku`.`kode_grade`"
                        + "LEFT JOIN `tb_grading_bahan_baku` ON `tb_laporan_produksi`.`kode_grade` = `tb_grading_bahan_baku`.`kode_grade` AND `tb_laporan_produksi`.`no_kartu_waleta` = `tb_grading_bahan_baku`.`no_kartu_waleta`"
                        + "WHERE `tb_grade_bahan_baku`.`jenis_bentuk` = '" + rs.getString("jenis_bentuk") + "' "
                        + "AND `tb_grade_bahan_baku`.`jenis_bulu` = '" + rs.getString("jenis_bulu") + "' "
                        + "AND `tb_grade_bahan_baku`.`kategori_proses` = '" + rs.getString("kategori_proses") + "' "
                        + "AND `tanggal_lp`<='" + dateFormat.format(today) + "'";
                rs_lp = Utility.db.getStatement().executeQuery(sql_lp);
                if (rs_lp.next()) {
                    kpg_lp = rs_lp.getInt("keping");
                    gram_lp = rs_lp.getInt("berat");
                    nilai_lp = rs_lp.getDouble("harga");
                }
                String sql_jual = "SELECT SUM(`total_keping_keluar`) AS 'keping', SUM(`total_berat_keluar`) AS 'berat', SUM(`tb_grading_bahan_baku`.`harga_bahanbaku` * `tb_bahan_baku_keluar`.`total_berat_keluar`) AS 'harga' "
                        + "FROM `tb_bahan_baku_keluar` LEFT JOIN `tb_bahan_baku_keluar1` ON `tb_bahan_baku_keluar`.`kode_pengeluaran` = `tb_bahan_baku_keluar1`.`kode_pengeluaran` "
                        + "LEFT JOIN `tb_grading_bahan_baku` ON (`tb_grading_bahan_baku`.`no_kartu_waleta` = `tb_bahan_baku_keluar`.`no_kartu_waleta` AND `tb_grading_bahan_baku`.`kode_grade` = `tb_bahan_baku_keluar`.`kode_grade`)"
                        + "LEFT JOIN `tb_grade_bahan_baku` ON `tb_bahan_baku_keluar`.`kode_grade` = `tb_grade_bahan_baku`.`kode_grade`"
                        + "WHERE `tb_grade_bahan_baku`.`jenis_bentuk` = '" + rs.getString("jenis_bentuk") + "' "
                        + "AND `tb_grade_bahan_baku`.`jenis_bulu` = '" + rs.getString("jenis_bulu") + "' "
                        + "AND `tb_grade_bahan_baku`.`kategori_proses` = '" + rs.getString("kategori_proses") + "' "
                        + "AND `tgl_keluar`<='" + dateFormat.format(today) + "'";
                rs_jual = Utility.db.getStatement().executeQuery(sql_jual);
                if (rs_jual.next()) {
                    kpg_jual = rs_jual.getInt("keping");
                    gram_keluar = rs_jual.getInt("berat");
                    nilai_jual = rs_jual.getDouble("harga");
                }
                String sql_cmp = "SELECT SUM(`tb_kartu_cmp_detail`.`keping`) AS 'keping_cmp', SUM(`tb_kartu_cmp_detail`.`gram`) AS 'berat_cmp', SUM(`tb_grading_bahan_baku`.`harga_bahanbaku`*`tb_kartu_cmp_detail`.`gram`) AS 'harga' FROM `tb_kartu_cmp_detail`\n"
                        + "LEFT JOIN `tb_kartu_cmp` ON `tb_kartu_cmp_detail`.`kode_kartu_cmp` = `tb_kartu_cmp`.`kode_kartu_cmp`"
                        + "LEFT JOIN `tb_grading_bahan_baku` ON `tb_kartu_cmp_detail`.`no_grading` = `tb_grading_bahan_baku`.`no_grading`\n"
                        + "LEFT JOIN `tb_grade_bahan_baku` ON `tb_grading_bahan_baku`.`kode_grade` = `tb_grade_bahan_baku`.`kode_grade`"
                        + "WHERE `tb_grade_bahan_baku`.`jenis_bentuk` = '" + rs.getString("jenis_bentuk") + "' "
                        + "AND `tb_grade_bahan_baku`.`jenis_bulu` = '" + rs.getString("jenis_bulu") + "' "
                        + "AND `tb_grade_bahan_baku`.`kategori_proses` = '" + rs.getString("kategori_proses") + "' "
                        + "AND `tanggal`<='" + dateFormat.format(today) + "'";
                rs_cmp = Utility.db.getStatement().executeQuery(sql_cmp);
                if (rs_cmp.next()) {
                    kpg_cmp = rs_cmp.getInt("keping_cmp");
                    gram_cmp = rs_cmp.getInt("berat_cmp");
                    nilai_cmp = rs_cmp.getDouble("harga");
                }

                float kpg_sisa = kpg_masuk - (kpg_lp + kpg_jual + kpg_cmp);
                float gram_sisa = gram_masuk - (gram_lp + gram_keluar + gram_cmp);
                double nilai_stok = nilai_masuk - (nilai_lp + nilai_jual + nilai_cmp);

                row[0] = rs.getString("jenis_bentuk");
                String jenis_bulu = rs.getString("jenis_bulu");
                switch (rs.getString("jenis_bulu")) {
                    case "Bulu Ringan Sekali/Bulu Ringan":
                        jenis_bulu = "BRS/BR";
                        break;
                    case "Bulu Ringan":
                        jenis_bulu = "BR";
                        break;
                    case "Bulu Sedang":
                        jenis_bulu = "BS";
                        break;
                    case "Bulu Berat":
                        jenis_bulu = "BB";
                        break;
                    case "Bulu Berat Sekali":
                        jenis_bulu = "BB2";
                        break;
                    default:
                        jenis_bulu = rs.getString("jenis_bulu");
                        break;
                }
                row[1] = jenis_bulu;
                row[2] = kpg_sisa;
                row[3] = gram_sisa;
                row[4] = Math.round(nilai_stok / 1000000.f);
                switch (rs.getString("kategori_proses")) {
                    case "WALETA":
                        switch (jenis_bulu) {
                            case "BRS/BR":
                                stok_BRS_WLT = stok_BRS_WLT + gram_sisa;
                                break;
                            case "BR":
                                stok_BR_WLT = stok_BR_WLT + gram_sisa;
                                break;
                            case "BS":
                                stok_BS_WLT = stok_BS_WLT + gram_sisa;
                                break;
                            default:
                                stok_BuluLain_WLT = stok_BuluLain_WLT + gram_sisa;
                                break;
                        }
                        total_nilai_stok_wlt = total_nilai_stok_wlt + nilai_stok;
                        stok_waleta = stok_waleta + gram_sisa;
                        model_waleta.addRow(row);
                        break;
                    case "SUB":
                        total_nilai_stok_sub = total_nilai_stok_sub + nilai_stok;
                        stok_sub = stok_sub + gram_sisa;
                        model_sub.addRow(row);
                        break;
                    default:
                        break;
                }
            }

            txt_total_stok_waleta.setText(decimalFormat.format(stok_waleta));
            txt_stok_sub.setText(decimalFormat.format(stok_sub));
            txt_nilai_stok_waleta.setText(" - Rp. " + decimalFormat.format(total_nilai_stok_wlt / 1000000) + " juta");
            txt_nilai_stok_sub.setText(" - Rp. " + decimalFormat.format(total_nilai_stok_sub / 1000000) + " juta");
            ColumnsAutoSizer.sizeColumnsToFit(table_stok_for_waleta);
            ColumnsAutoSizer.sizeColumnsToFit(table_stok_for_sub);

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, -1);
            Date result = cal.getTime();
            sql = "SELECT AVG(`jumlah_per_hari`) AS 'avg' FROM \n"
                    + "(SELECT SUM(`berat_basah`) AS 'jumlah_per_hari' FROM `tb_laporan_produksi` \n"
                    + "WHERE `no_laporan_produksi` LIKE 'WL.%' AND `tanggal_lp` BETWEEN '" + dateFormat.format(result) + "' AND '" + dateFormat.format(today) + "'\n"
                    + "GROUP BY `tanggal_lp`) AS T";
            rs = Utility.db.getStatement().executeQuery(sql);
            float rata2_pengeluaran_sub = 0;
            if (rs.next()) {
                rata2_pengeluaran_sub = rs.getInt("avg");
                txt_pengeluaran_sub.setText(decimalFormat.format(rata2_pengeluaran_sub));
            }
            float ketahanan_sub = stok_sub / rata2_pengeluaran_sub;
            txt_ketahanan_sub.setText(decimalFormat.format(ketahanan_sub));
            Date END_SUB = Utility.addDaysSkippingSundays(new Date(), Math.round(ketahanan_sub));
            txt_tanggal_end_sub.setText(new SimpleDateFormat("dd MMM yyyy").format(END_SUB));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JFrame_TV_StokBaku.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void Load_Ketahanan_Waleta() {
        try {
            DefaultTableModel model = (DefaultTableModel) table_ketahanan_stok_waleta.getModel();
            model.setRowCount(0);
            sql = "SELECT COUNT(`tb_karyawan`.`id_pegawai`) AS 'borong_cabut' "
                    + "FROM `tb_karyawan` "
                    + "LEFT JOIN `tb_grup_cabut` ON `tb_karyawan`.`id_pegawai` = `tb_grup_cabut`.`id_pegawai` "
                    + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian` "
                    + "WHERE `status` = 'IN' AND `nama_bagian` LIKE '%CABUT-BORONG%'";
            rs = Utility.db.getStatement().executeQuery(sql);
            int jumlah_cabut_borong = 0;
            if (rs.next()) {
                jumlah_cabut_borong = rs.getInt("borong_cabut");
            }

            float ketahanan_BRS = stok_BRS_WLT / (jumlah_cabut_borong * 20 * 8);
            float ketahanan_BR = stok_BR_WLT / (jumlah_cabut_borong * 20 * 8);
            float ketahanan_BS = stok_BS_WLT / (jumlah_cabut_borong * 13 * 8);
            float ketahanan_Other = stok_BuluLain_WLT / (jumlah_cabut_borong * 16 * 8);
            decimalFormat.setMaximumFractionDigits(2);
            model.addRow(new Object[]{"BRS/BR", stok_BRS_WLT, jumlah_cabut_borong, 20, 8, (jumlah_cabut_borong * 20 * 8), decimalFormat.format(ketahanan_BRS)});
            model.addRow(new Object[]{"BR", stok_BR_WLT, jumlah_cabut_borong, 20, 8, (jumlah_cabut_borong * 20 * 8), decimalFormat.format(ketahanan_BR)});
            model.addRow(new Object[]{"BS", stok_BS_WLT, jumlah_cabut_borong, 13, 8, (jumlah_cabut_borong * 13 * 8), decimalFormat.format(ketahanan_BS)});
            model.addRow(new Object[]{"Other", stok_BuluLain_WLT, jumlah_cabut_borong, 16, 8, (jumlah_cabut_borong * 16 * 8), decimalFormat.format(ketahanan_Other)});
            model.addRow(new Object[]{"TOTAL", (stok_BRS_WLT + stok_BR_WLT + stok_BS_WLT + stok_BuluLain_WLT), null, null, null, null, decimalFormat.format(ketahanan_BRS + ketahanan_BR + ketahanan_BS + ketahanan_Other)});

            float ketahanan_waleta = ketahanan_BRS + ketahanan_BR + ketahanan_BS + ketahanan_Other;
            Date END_WLT = Utility.addDaysSkippingSundays(new Date(), Math.round(ketahanan_waleta));
            txt_tanggal_end_waleta.setText(new SimpleDateFormat("dd MMM yyyy").format(END_WLT));
            ColumnsAutoSizer.sizeColumnsToFit(table_ketahanan_stok_waleta);
        } catch (Exception ex) {
            JOptionPane.showConfirmDialog(this, ex);
            Logger.getLogger(JFrame_TV_StokBaku.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void Refresh_BP_Unworkable() {
        try {
            ResultSet rs_grading, rs_jual, rs_lp, rs_cmp;
            int kpg_masuk = 0, gram_masuk = 0, kpg_lp = 0, gram_lp = 0, kpg_jual = 0, gram_keluar = 0, kpg_cmp = 0, gram_cmp = 0;
            double nilai_masuk = 0, nilai_lp = 0, nilai_jual = 0, nilai_cmp = 0, total_nilai_stok_bp = 0, total_nilai_stok_unwork = 0;
            float stok_bp = 0, stok_jual = 0;
            DefaultTableModel model_bp = (DefaultTableModel) table_stok_for_bp.getModel();
            model_bp.setRowCount(0);
            DefaultTableModel model_jual = (DefaultTableModel) table_stok_for_jual.getModel();
            model_jual.setRowCount(0);
            sql = "SELECT `kode_grade`, `jenis_bentuk`, `jenis_bulu`, `kategori_proses` FROM `tb_grade_bahan_baku` WHERE `kategori_proses` IN ('BP', 'JUAL')";
            pst = Utility.db.getConnection().prepareStatement(sql);
            rs = pst.executeQuery();
            Object[] row = new Object[4];
            while (rs.next()) {
                String sql_grading = "SELECT SUM(`tb_grading_bahan_baku`.`jumlah_keping`) AS 'jumlah_keping', SUM(`tb_grading_bahan_baku`.`total_berat`) AS 'total_berat', SUM(`harga_bahanbaku` * `tb_grading_bahan_baku`.`total_berat`) AS 'harga' "
                        + "FROM `tb_grading_bahan_baku` "
                        + "LEFT JOIN `tb_bahan_baku_masuk` ON `tb_grading_bahan_baku`.`no_kartu_waleta` = `tb_bahan_baku_masuk`.`no_kartu_waleta`"
                        + "WHERE `kode_grade` = '" + rs.getString("kode_grade") + "' "
                        + "AND `tgl_masuk`<='" + dateFormat.format(today) + "'";
                rs_grading = Utility.db.getStatement().executeQuery(sql_grading);
                if (rs_grading.next()) {
                    kpg_masuk = rs_grading.getInt("jumlah_keping");
                    gram_masuk = rs_grading.getInt("total_berat");
                    nilai_masuk = rs_grading.getDouble("harga");
                }
                String sql_lp = "SELECT SUM(`tb_laporan_produksi`.`jumlah_keping`) AS 'keping', SUM(`tb_laporan_produksi`.`berat_basah`) AS 'berat', SUM(`tb_grading_bahan_baku`.`harga_bahanbaku` * `tb_laporan_produksi`.`berat_basah`) AS 'harga' "
                        + "FROM `tb_laporan_produksi` "
                        + "LEFT JOIN `tb_grading_bahan_baku` ON `tb_laporan_produksi`.`kode_grade` = `tb_grading_bahan_baku`.`kode_grade` AND `tb_laporan_produksi`.`no_kartu_waleta` = `tb_grading_bahan_baku`.`no_kartu_waleta`"
                        + "WHERE `tb_laporan_produksi`.`kode_grade` = '" + rs.getString("kode_grade") + "' "
                        + "AND `tanggal_lp`<='" + dateFormat.format(today) + "'";
                rs_lp = Utility.db.getStatement().executeQuery(sql_lp);
                if (rs_lp.next()) {
                    kpg_lp = rs_lp.getInt("keping");
                    gram_lp = rs_lp.getInt("berat");
                    nilai_lp = rs_lp.getDouble("harga");
                }
                String sql_jual = "SELECT SUM(`total_keping_keluar`) AS 'keping', SUM(`total_berat_keluar`) AS 'berat', SUM(`tb_grading_bahan_baku`.`harga_bahanbaku` * `tb_bahan_baku_keluar`.`total_berat_keluar`) AS 'harga' "
                        + "FROM `tb_bahan_baku_keluar` "
                        + "LEFT JOIN `tb_bahan_baku_keluar1` ON `tb_bahan_baku_keluar`.`kode_pengeluaran` = `tb_bahan_baku_keluar1`.`kode_pengeluaran` "
                        + "LEFT JOIN `tb_grading_bahan_baku` ON (`tb_grading_bahan_baku`.`no_kartu_waleta` = `tb_bahan_baku_keluar`.`no_kartu_waleta` AND `tb_grading_bahan_baku`.`kode_grade` = `tb_bahan_baku_keluar`.`kode_grade`)"
                        + "WHERE `tb_bahan_baku_keluar`.`kode_grade` = '" + rs.getString("kode_grade") + "' "
                        + "AND `tgl_keluar`<='" + dateFormat.format(today) + "'";
                rs_jual = Utility.db.getStatement().executeQuery(sql_jual);
                if (rs_jual.next()) {
                    kpg_jual = rs_jual.getInt("keping");
                    gram_keluar = rs_jual.getInt("berat");
                    nilai_jual = rs_jual.getDouble("harga");
                }
                String sql_cmp = "SELECT SUM(`tb_kartu_cmp_detail`.`keping`) AS 'keping_cmp', SUM(`tb_kartu_cmp_detail`.`gram`) AS 'berat_cmp', SUM(`tb_grading_bahan_baku`.`harga_bahanbaku`*`tb_kartu_cmp_detail`.`gram`) AS 'harga' "
                        + "FROM `tb_kartu_cmp_detail`\n"
                        + "LEFT JOIN `tb_kartu_cmp` ON `tb_kartu_cmp_detail`.`kode_kartu_cmp` = `tb_kartu_cmp`.`kode_kartu_cmp`"
                        + "LEFT JOIN `tb_grading_bahan_baku` ON `tb_kartu_cmp_detail`.`no_grading` = `tb_grading_bahan_baku`.`no_grading`\n"
                        + "WHERE `tb_grading_bahan_baku`.`kode_grade` = '" + rs.getString("kode_grade") + "' "
                        + "AND `tanggal`<='" + dateFormat.format(today) + "'";
                rs_cmp = Utility.db.getStatement().executeQuery(sql_cmp);
                if (rs_cmp.next()) {
                    kpg_cmp = rs_cmp.getInt("keping_cmp");
                    gram_cmp = rs_cmp.getInt("berat_cmp");
                    nilai_cmp = rs_cmp.getDouble("harga");
                }
                float kpg_sisa = kpg_masuk - (kpg_lp + kpg_jual + kpg_cmp);
                float gram_sisa = gram_masuk - (gram_lp + gram_keluar + gram_cmp);
                double nilai_stok = nilai_masuk - (nilai_lp + nilai_jual + nilai_cmp);

                row[0] = rs.getString("kode_grade");
                row[1] = kpg_sisa;
                row[2] = gram_sisa;
                row[3] = Math.round(nilai_stok / 1000000.f);
                switch (rs.getString("kategori_proses")) {
                    case "BP":
                        total_nilai_stok_bp = total_nilai_stok_bp + nilai_stok;
                        stok_bp = stok_bp + gram_sisa;
                        model_bp.addRow(row);
                        break;
                    case "JUAL":
                        total_nilai_stok_unwork = total_nilai_stok_unwork + nilai_stok;
                        stok_jual = stok_jual + gram_sisa;
                        model_jual.addRow(row);
                        break;
                    default:
                        break;
                }
            }
            decimalFormat.setMaximumFractionDigits(0);
            txt_stok_bp.setText(decimalFormat.format(stok_bp));
            txt_stok_unworkable.setText(decimalFormat.format(stok_jual));
            txt_nilai_stok_bp.setText(" - Rp. " + decimalFormat.format(total_nilai_stok_bp / 1000000) + " juta");
            txt_nilai_stok_unworkable.setText(" - Rp. " + decimalFormat.format(total_nilai_stok_unwork / 1000000) + " juta");
            ColumnsAutoSizer.sizeColumnsToFit(table_stok_for_bp);
            ColumnsAutoSizer.sizeColumnsToFit(table_stok_for_jual);
        } catch (SQLException ex) {
            JOptionPane.showConfirmDialog(this, ex);
            Logger.getLogger(JDialog_LaporanBaku_perGrade.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void Refresh_proses_grading() {
        try {
            DefaultTableModel model_on_proses = (DefaultTableModel) table_on_proses.getModel();
            model_on_proses.setRowCount(0);
            int total_berat_onproses = 0;
            sql = "SELECT `no_kartu_waleta`, `tgl_masuk`, `tb_supplier`.`nama_supplier`, `berat_awal` \n"
                    + "FROM `tb_bahan_baku_masuk` "
                    + "LEFT JOIN `tb_supplier` ON `tb_bahan_baku_masuk`.`kode_supplier` = `tb_supplier`.`kode_supplier`\n"
                    + "WHERE `tb_bahan_baku_masuk`.`berat_real` = 0 AND `berat_awal` > 0";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[4];
            while (rs.next()) {
                row[0] = rs.getString("no_kartu_waleta");
                row[1] = rs.getDate("tgl_masuk");
                row[2] = rs.getString("nama_supplier");
                row[3] = rs.getFloat("berat_awal");
                total_berat_onproses = total_berat_onproses + rs.getInt("berat_awal");
                model_on_proses.addRow(row);
            }
            txt_total_proses_grading.setText(decimalFormat.format(total_berat_onproses));
        } catch (SQLException ex) {
            JOptionPane.showConfirmDialog(this, ex);
            Logger.getLogger(JDialog_LaporanBaku_perGrade.class.getName()).log(Level.SEVERE, null, ex);
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

        jPanel_ketahanan_stok = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        txt_nilai_stok_bp = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        table_stok_for_bp = new javax.swing.JTable();
        jLabel57 = new javax.swing.JLabel();
        txt_stok_bp = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        txt_nilai_stok_unworkable = new javax.swing.JLabel();
        jLabel58 = new javax.swing.JLabel();
        txt_stok_unworkable = new javax.swing.JLabel();
        jScrollPane7 = new javax.swing.JScrollPane();
        table_stok_for_jual = new javax.swing.JTable();
        jLabel53 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel64 = new javax.swing.JLabel();
        txt_tanggal_end_sub = new javax.swing.JLabel();
        txt_pengeluaran_sub = new javax.swing.JLabel();
        jLabel59 = new javax.swing.JLabel();
        txt_ketahanan_sub = new javax.swing.JLabel();
        txt_stok_sub = new javax.swing.JLabel();
        jLabel56 = new javax.swing.JLabel();
        jLabel61 = new javax.swing.JLabel();
        jLabel54 = new javax.swing.JLabel();
        jScrollPane13 = new javax.swing.JScrollPane();
        table_on_proses = new javax.swing.JTable();
        jLabel60 = new javax.swing.JLabel();
        txt_nilai_stok_sub = new javax.swing.JLabel();
        jLabel62 = new javax.swing.JLabel();
        txt_total_proses_grading = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        table_stok_for_sub = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        txt_total_stok_waleta = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        table_ketahanan_stok_waleta = new javax.swing.JTable();
        jScrollPane4 = new javax.swing.JScrollPane();
        table_stok_for_waleta = new javax.swing.JTable();
        jLabel55 = new javax.swing.JLabel();
        jLabel65 = new javax.swing.JLabel();
        jLabel63 = new javax.swing.JLabel();
        txt_tanggal_end_waleta = new javax.swing.JLabel();
        txt_nilai_stok_waleta = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel_ketahanan_stok.setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        txt_nilai_stok_bp.setBackground(new java.awt.Color(255, 255, 255));
        txt_nilai_stok_bp.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        txt_nilai_stok_bp.setText("0");

        table_stok_for_bp.setAutoCreateRowSorter(true);
        table_stok_for_bp.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        table_stok_for_bp.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Grade", "Stok Keping", "Stok Gram", "Nilai (Jt)"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
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
        table_stok_for_bp.setRowHeight(20);
        table_stok_for_bp.getTableHeader().setReorderingAllowed(false);
        jScrollPane6.setViewportView(table_stok_for_bp);

        jLabel57.setBackground(new java.awt.Color(255, 255, 255));
        jLabel57.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel57.setText("WORKABLE BY PRODUCT");

        txt_stok_bp.setBackground(new java.awt.Color(255, 255, 255));
        txt_stok_bp.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        txt_stok_bp.setText("0");

        jLabel51.setBackground(new java.awt.Color(255, 255, 255));
        jLabel51.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel51.setText("STOK BP :");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel57)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel51)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_stok_bp)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_nilai_stok_bp))
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel57, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_stok_bp)
                    .addComponent(jLabel51)
                    .addComponent(txt_nilai_stok_bp))
                .addContainerGap())
        );

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        txt_nilai_stok_unworkable.setBackground(new java.awt.Color(255, 255, 255));
        txt_nilai_stok_unworkable.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        txt_nilai_stok_unworkable.setText("0");

        jLabel58.setBackground(new java.awt.Color(255, 255, 255));
        jLabel58.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel58.setText("UNWORKABLE MATERIAL");

        txt_stok_unworkable.setBackground(new java.awt.Color(255, 255, 255));
        txt_stok_unworkable.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        txt_stok_unworkable.setText("0");

        table_stok_for_jual.setAutoCreateRowSorter(true);
        table_stok_for_jual.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        table_stok_for_jual.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Grade", "Stok Keping", "Stok Gram", "Nilai (Jt)"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
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
        table_stok_for_jual.setRowHeight(20);
        table_stok_for_jual.getTableHeader().setReorderingAllowed(false);
        jScrollPane7.setViewportView(table_stok_for_jual);

        jLabel53.setBackground(new java.awt.Color(255, 255, 255));
        jLabel53.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel53.setText("UNWORKABLE :");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 363, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel58)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel53)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_stok_unworkable)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_nilai_stok_unworkable)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel58, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_stok_unworkable)
                    .addComponent(jLabel53)
                    .addComponent(txt_nilai_stok_unworkable))
                .addContainerGap())
        );

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        jLabel64.setBackground(new java.awt.Color(255, 255, 255));
        jLabel64.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel64.setText("Total Berat sedang grading :");

        txt_tanggal_end_sub.setBackground(new java.awt.Color(255, 255, 255));
        txt_tanggal_end_sub.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        txt_tanggal_end_sub.setText("dd MMMM yyyy");

        txt_pengeluaran_sub.setBackground(new java.awt.Color(255, 255, 255));
        txt_pengeluaran_sub.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        txt_pengeluaran_sub.setText("0");

        jLabel59.setBackground(new java.awt.Color(255, 255, 255));
        jLabel59.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel59.setText("BAKU PROSES GRADING");

        txt_ketahanan_sub.setBackground(new java.awt.Color(255, 255, 255));
        txt_ketahanan_sub.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        txt_ketahanan_sub.setText("0");

        txt_stok_sub.setBackground(new java.awt.Color(255, 255, 255));
        txt_stok_sub.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        txt_stok_sub.setText("0");

        jLabel56.setBackground(new java.awt.Color(255, 255, 255));
        jLabel56.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel56.setText("WORKABLE MATERIAL FOR SUB");

        jLabel61.setBackground(new java.awt.Color(255, 255, 255));
        jLabel61.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel61.setText("Ketahanan Sub (Hari) :");

        jLabel54.setBackground(new java.awt.Color(255, 255, 255));
        jLabel54.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel54.setText("STOK SUB :");

        table_on_proses.setAutoCreateRowSorter(true);
        table_on_proses.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        table_on_proses.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No Kartu", "Tgl Masuk", "Supplier", "Berat (Gr)"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.Float.class
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
        table_on_proses.setRowHeight(20);
        table_on_proses.getTableHeader().setReorderingAllowed(false);
        jScrollPane13.setViewportView(table_on_proses);

        jLabel60.setBackground(new java.awt.Color(255, 255, 255));
        jLabel60.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel60.setText("Rata2 Pengeluaran Baku untuk SUB :");

        txt_nilai_stok_sub.setBackground(new java.awt.Color(255, 255, 255));
        txt_nilai_stok_sub.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        txt_nilai_stok_sub.setText("0");

        jLabel62.setBackground(new java.awt.Color(255, 255, 255));
        jLabel62.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel62.setText("Tanggal perkiraan stok habis untuk sub :");

        txt_total_proses_grading.setBackground(new java.awt.Color(255, 255, 255));
        txt_total_proses_grading.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        txt_total_proses_grading.setText("0");

        table_stok_for_sub.setAutoCreateRowSorter(true);
        table_stok_for_sub.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        table_stok_for_sub.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Jenis Bentuk", "Jenis Bulu", "Stok Keping", "Stok Gram", "Nilai (Jt)"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
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
        table_stok_for_sub.setRowHeight(20);
        table_stok_for_sub.getTableHeader().setReorderingAllowed(false);
        jScrollPane5.setViewportView(table_stok_for_sub);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane13, javax.swing.GroupLayout.DEFAULT_SIZE, 473, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel56)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel54)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_stok_sub)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_nilai_stok_sub))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel60)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_pengeluaran_sub))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel61)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_ketahanan_sub))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel62)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_tanggal_end_sub))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel64)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_total_proses_grading))
                            .addComponent(jLabel59))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane5))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel56, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_stok_sub)
                    .addComponent(jLabel54)
                    .addComponent(txt_nilai_stok_sub))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel60)
                    .addComponent(txt_pengeluaran_sub))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel61)
                    .addComponent(txt_ketahanan_sub))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel62)
                    .addComponent(txt_tanggal_end_sub))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel59, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane13, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_total_proses_grading)
                    .addComponent(jLabel64))
                .addContainerGap())
        );

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));

        txt_total_stok_waleta.setBackground(new java.awt.Color(255, 255, 255));
        txt_total_stok_waleta.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        txt_total_stok_waleta.setText("0");

        table_ketahanan_stok_waleta.setAutoCreateRowSorter(true);
        table_ketahanan_stok_waleta.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        table_ketahanan_stok_waleta.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Jenis Bulu", "Stok (Gram)", "Anak CBT", "Kpg/Anak", "Gr/Kpg", "Max Load/hari", "Hari"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Float.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Float.class, java.lang.Float.class, java.lang.Integer.class
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
        table_ketahanan_stok_waleta.setRowHeight(20);
        table_ketahanan_stok_waleta.getTableHeader().setReorderingAllowed(false);
        jScrollPane8.setViewportView(table_ketahanan_stok_waleta);

        table_stok_for_waleta.setAutoCreateRowSorter(true);
        table_stok_for_waleta.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        table_stok_for_waleta.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Jenis Bentuk", "Jenis Bulu", "Stok Keping", "Stok Gram", "Nilai (Jt)"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
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
        table_stok_for_waleta.setRowHeight(20);
        table_stok_for_waleta.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(table_stok_for_waleta);

        jLabel55.setBackground(new java.awt.Color(255, 255, 255));
        jLabel55.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel55.setText("WORKABLE MATERIAL FOR WALETA");

        jLabel65.setBackground(new java.awt.Color(255, 255, 255));
        jLabel65.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel65.setText("Total Stock for Waleta :");

        jLabel63.setBackground(new java.awt.Color(255, 255, 255));
        jLabel63.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel63.setText("Tanggal perkiraan stok habis Waleta :");

        txt_tanggal_end_waleta.setBackground(new java.awt.Color(255, 255, 255));
        txt_tanggal_end_waleta.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        txt_tanggal_end_waleta.setText("dd MMMM yyyy");

        txt_nilai_stok_waleta.setBackground(new java.awt.Color(255, 255, 255));
        txt_nilai_stok_waleta.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        txt_nilai_stok_waleta.setText("0");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 456, Short.MAX_VALUE)
                    .addComponent(jScrollPane4)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel55)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel63)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_tanggal_end_waleta))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel65)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_total_stok_waleta)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_nilai_stok_waleta)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel55, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 476, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel65)
                    .addComponent(txt_total_stok_waleta)
                    .addComponent(txt_nilai_stok_waleta))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel63)
                    .addComponent(txt_tanggal_end_waleta))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel_ketahanan_stokLayout = new javax.swing.GroupLayout(jPanel_ketahanan_stok);
        jPanel_ketahanan_stok.setLayout(jPanel_ketahanan_stokLayout);
        jPanel_ketahanan_stokLayout.setHorizontalGroup(
            jPanel_ketahanan_stokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_ketahanan_stokLayout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_ketahanan_stokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel_ketahanan_stokLayout.setVerticalGroup(
            jPanel_ketahanan_stokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_ketahanan_stokLayout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel_ketahanan_stok, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel_ketahanan_stok, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(JFrame_TV_StokBaku.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JFrame_TV_StokBaku.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JFrame_TV_StokBaku.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JFrame_TV_StokBaku.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JFrame_TV_StokBaku chart = new JFrame_TV_StokBaku();
                chart.pack();
                chart.setResizable(true);
                chart.setLocationRelativeTo(null);
                chart.setVisible(true);
                chart.setEnabled(true);
                chart.setExtendedState(JFrame.MAXIMIZED_BOTH);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel_ketahanan_stok;
    private javax.swing.JScrollPane jScrollPane13;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JTable table_ketahanan_stok_waleta;
    private javax.swing.JTable table_on_proses;
    private javax.swing.JTable table_stok_for_bp;
    private javax.swing.JTable table_stok_for_jual;
    private javax.swing.JTable table_stok_for_sub;
    private javax.swing.JTable table_stok_for_waleta;
    private javax.swing.JLabel txt_ketahanan_sub;
    private javax.swing.JLabel txt_nilai_stok_bp;
    private javax.swing.JLabel txt_nilai_stok_sub;
    private javax.swing.JLabel txt_nilai_stok_unworkable;
    private javax.swing.JLabel txt_nilai_stok_waleta;
    private javax.swing.JLabel txt_pengeluaran_sub;
    private javax.swing.JLabel txt_stok_bp;
    private javax.swing.JLabel txt_stok_sub;
    private javax.swing.JLabel txt_stok_unworkable;
    private javax.swing.JLabel txt_tanggal_end_sub;
    private javax.swing.JLabel txt_tanggal_end_waleta;
    private javax.swing.JLabel txt_total_proses_grading;
    private javax.swing.JLabel txt_total_stok_waleta;
    // End of variables declaration//GEN-END:variables
}
