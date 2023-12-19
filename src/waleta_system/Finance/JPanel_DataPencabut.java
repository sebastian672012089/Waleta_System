package waleta_system.Finance;

import java.awt.event.KeyEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

public class JPanel_DataPencabut extends javax.swing.JPanel implements InterfacePanel {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    DecimalFormat decimalFormat = new DecimalFormat();
    double total_bonus_lp_rekap_karyawan = 0, total_bonus_lp2_rekap_karyawan = 0, total_gram_cabutan;

    public JPanel_DataPencabut() {
        initComponents();
    }

    @Override
    public void init() {
        refreshTable_lp_cabut();
        refreshTable_reproses_cabut();
    }

    public void refreshTable_lp_cabut() {
        try {
            int total_kpg = 0;
            decimalFormat.setMaximumFractionDigits(2);
            DefaultTableModel model = (DefaultTableModel) table_data_pencabut.getModel();
            model.setRowCount(0);
            String search = "1";
            switch (ComboBox_Search.getSelectedIndex()) {
                case 0:
                    search = "`tb_karyawan`.`nama_pegawai` LIKE '%" + txt_search.getText() + "%'";
                    break;
                case 1:
                    search = "`tb_bagian`.`nama_bagian` LIKE '%" + txt_search.getText() + "%'";
                    break;
                case 2:
                    search = "`tb_detail_pencabut`.`no_laporan_produksi` LIKE '%" + txt_search.getText() + "%'";
                    break;
                default:
                    search = "1";
                    break;
            }
            String filter_tgl_setor_cabut = "";
            if (Date1.getDate() != null && Date2.getDate() != null) {
                filter_tgl_setor_cabut = "AND (`tgl_setor_cabut` BETWEEN '" + dateFormat.format(Date1.getDate()) + "' AND '" + dateFormat.format(Date2.getDate()) + "') ";
            }

            sql = "SELECT `tb_detail_pencabut`.`no_laporan_produksi`, `no_kartu_waleta`, `kode_grade`, `tb_laporan_produksi`.`jenis_bulu_lp`, `tb_detail_pencabut`.`id_pegawai`, `tb_karyawan`.`nama_pegawai`, `tb_bagian`.`nama_bagian`, `tb_cabut`.`tgl_setor_cabut`, `tb_detail_pencabut`.`tanggal_cabut`, "
                    + "`berat_basah`, `jumlah_cabut`, `jumlah_gram`, `tb_tarif_cabut`.`tarif_gram` AS 'upah_per_gram', `nominal_bonus_lp`, `nominal_bonus_lp_tambahan` \n"
                    + "FROM `tb_detail_pencabut` \n"
                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_detail_pencabut`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_tarif_cabut` ON `tb_laporan_produksi`.`jenis_bulu_lp` = `tb_tarif_cabut`.`bulu_upah` \n"
                    + "LEFT JOIN `tb_cabut` ON `tb_detail_pencabut`.`no_laporan_produksi` = `tb_cabut`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_karyawan` ON `tb_detail_pencabut`.`id_pegawai` = `tb_karyawan`.`id_pegawai`\n"
                    + "LEFT JOIN `tb_bagian` ON `tb_detail_pencabut`.`kode_bagian` = `tb_bagian`.`kode_bagian`\n"
                    + "WHERE " + search + " \n"
                    + filter_tgl_setor_cabut;

            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[20];
            while (rs.next()) {
                row[0] = rs.getString("id_pegawai");
                row[1] = rs.getString("nama_pegawai");
                row[2] = rs.getString("nama_bagian");
                row[3] = rs.getString("no_laporan_produksi");
                row[4] = rs.getString("no_kartu_waleta");
                row[5] = rs.getString("kode_grade");
                row[6] = rs.getString("jenis_bulu_lp");
                row[7] = rs.getDate("tanggal_cabut");
                row[8] = rs.getDate("tgl_setor_cabut");
                row[9] = rs.getInt("jumlah_cabut");
                int jumlah_cabut = 0;
                if (rs.getInt("jumlah_cabut") == 0) {
                    jumlah_cabut = Math.round(rs.getFloat("jumlah_gram") / 8);
                } else {
                    jumlah_cabut = rs.getInt("jumlah_cabut");
                }

                total_kpg = total_kpg + jumlah_cabut;
                row[10] = jumlah_cabut;
                row[11] = rs.getFloat("jumlah_gram");
                row[12] = rs.getInt("upah_per_gram");
                row[13] = Math.round(rs.getFloat("jumlah_gram") * rs.getInt("upah_per_gram"));
                float bonus_lp_per_lp_per_karyawan = (rs.getFloat("jumlah_gram") / rs.getFloat("berat_basah")) * rs.getFloat("nominal_bonus_lp");
                row[14] = bonus_lp_per_lp_per_karyawan;
                float bonus_lp_tambahan_per_lp_per_karyawan = (rs.getFloat("jumlah_gram") / rs.getFloat("berat_basah")) * rs.getFloat("nominal_bonus_lp_tambahan");
                row[15] = bonus_lp_tambahan_per_lp_per_karyawan;
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(table_data_pencabut);
            label_total_kpg.setText(decimalFormat.format(total_kpg));

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_DataPencabut.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_reproses_cabut() {
        try {
            int total_kpg = 0;
            decimalFormat.setMaximumFractionDigits(2);
            DefaultTableModel model = (DefaultTableModel) table_data_reproses_cabut.getModel();
            model.setRowCount(0);
            String search = "1", filter_tanggal = "";
            switch (ComboBox_Search.getSelectedIndex()) {
                case 0:
                    search = "`tb_karyawan`.`nama_pegawai` LIKE '%" + txt_search.getText() + "%'";
                    break;
                case 1:
                    search = "`tb_bagian`.`nama_bagian` LIKE '%" + txt_search.getText() + "%'";
                    break;
                case 2:
                    search = "`tb_reproses`.`no_box` LIKE '%" + txt_search.getText() + "%'";
                    break;
                default:
                    search = "1";
                    break;
            }
            if (Date1.getDate() != null && Date2.getDate() != null) {
                filter_tanggal = " AND (`tgl_cabut_selesai` BETWEEN '" + dateFormat.format(Date1.getDate()) + "' AND '" + dateFormat.format(Date2.getDate()) + "')";
            }

            sql = "SELECT `tb_reproses`.`no_box`, `tb_grade_bahan_jadi`.`kode_grade`, `tb_reproses_pencabut`.`id_pegawai`, `tb_karyawan`.`nama_pegawai`, `tb_bagian`.`nama_bagian`, `tb_reproses`.`tgl_cabut_selesai`, `tb_reproses`.`tgl_cabut`, `jumlah_cabut`, `jumlah_gram`, \n"
                    + "`tb_grade_bahan_jadi`.`upah_reproses` AS 'upah_per_gram', ROUND(`jumlah_gram` * `tb_grade_bahan_jadi`.`upah_reproses`) AS 'upah_cabut' \n"
                    + "FROM `tb_reproses_pencabut` \n"
                    + "LEFT JOIN `tb_reproses` ON `tb_reproses_pencabut`.`no_reproses` = `tb_reproses`.`no_reproses`\n"
                    + "LEFT JOIN `tb_box_bahan_jadi` ON `tb_reproses`.`no_box` = `tb_box_bahan_jadi`.`no_box` \n"
                    + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_box_bahan_jadi`.`kode_grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode`\n"
                    + "LEFT JOIN `tb_karyawan` ON `tb_reproses_pencabut`.`id_pegawai` = `tb_karyawan`.`id_pegawai`\n"
                    + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`\n"
                    + "WHERE " + search + filter_tanggal;
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[15];
            while (rs.next()) {
                row[0] = rs.getString("id_pegawai");
                row[1] = rs.getString("nama_pegawai");
                row[2] = rs.getString("nama_bagian");
                row[3] = rs.getString("no_box");
                row[4] = rs.getString("kode_grade");
                row[5] = rs.getDate("tgl_cabut");
                row[6] = rs.getDate("tgl_cabut_selesai");
                row[7] = rs.getInt("jumlah_cabut");
                int jumlah_cabut = 0;
                if (rs.getInt("jumlah_cabut") == 0) {
                    jumlah_cabut = Math.round(rs.getFloat("jumlah_gram") / 8);
                } else {
                    jumlah_cabut = rs.getInt("jumlah_cabut");
                }

                total_kpg = total_kpg + jumlah_cabut;
                row[8] = jumlah_cabut;
                row[9] = rs.getFloat("jumlah_gram");
                row[10] = rs.getInt("upah_per_gram");
                row[11] = rs.getInt("upah_cabut");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(table_data_reproses_cabut);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_DataPencabut.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_Rekap() {
        try {
            decimalFormat.setMaximumFractionDigits(2);
            DefaultTableModel model = (DefaultTableModel) table_data_rekap.getModel();
            model.setRowCount(0);
            String filter = "FILTER : Nama bagian 'CABUT'";
            String search = "1";
            switch (ComboBox_Search.getSelectedIndex()) {
                case 0:
                    search = "`tb_karyawan`.`nama_pegawai` LIKE '%" + txt_search.getText() + "%'";
                    filter = filter + ", Nama Karyawan '" + txt_search.getText() + "'";
                    break;
                case 1:
                    search = "`tb_bagian`.`nama_bagian` LIKE '%" + txt_search.getText() + "%'";
                    filter = filter + ", Nama Bagian '" + txt_search.getText() + "'";
                    break;
                default:
                    search = "1";
                    break;
            }
            if (Date1.getDate() != null && Date2.getDate() != null) {
                filter = filter + ", Tanggal '" + dateFormat.format(Date1.getDate()) + "' - '" + dateFormat.format(Date2.getDate()) + "'";
                sql = "SELECT tabel.`id_pegawai`, `nama_pegawai`, `nama_bagian`, `grup_cabut`, tabel.`tgl_setor_cabut`, SUM(`jumlah_cabut`) AS 'jumlah_cabut', SUM(`jumlah_gram`) AS 'jumlah_gram', SUM(`upah_cabut`) AS 'upah_cabut', DATA_LEMBUR.`bonus1_kecepatan` "
                        + "FROM ("
                        + "(SELECT `tb_detail_pencabut`.`no_laporan_produksi` AS 'no_box', `tb_laporan_produksi`.`jenis_bulu_lp` AS 'grade', `tb_detail_pencabut`.`id_pegawai`, `tb_karyawan`.`nama_pegawai`, `tb_bagian`.`nama_bagian`, `tb_cabut`.`tgl_setor_cabut`, `tb_detail_pencabut`.`tanggal_cabut`, `jumlah_cabut`, `jumlah_gram`, `grup_cabut`, "
                        + "`tb_tarif_cabut`.`tarif_gram` AS 'upah_per_gram', ROUND(`jumlah_gram` * `tb_tarif_cabut`.`tarif_gram`) AS 'upah_cabut'\n"
                        + "FROM `tb_detail_pencabut` \n"
                        + "LEFT JOIN `tb_laporan_produksi` ON `tb_detail_pencabut`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                        + "LEFT JOIN `tb_tarif_cabut` ON `tb_laporan_produksi`.`jenis_bulu_lp` = `tb_tarif_cabut`.`bulu_upah` \n"
                        + "LEFT JOIN `tb_cabut` ON `tb_detail_pencabut`.`no_laporan_produksi` = `tb_cabut`.`no_laporan_produksi`\n"
                        + "LEFT JOIN `tb_karyawan` ON `tb_detail_pencabut`.`id_pegawai` = `tb_karyawan`.`id_pegawai`\n"
                        + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`\n"
                        + "WHERE " + search + " AND (`tgl_setor_cabut` BETWEEN '" + dateFormat.format(Date1.getDate()) + "' AND '" + dateFormat.format(Date2.getDate()) + "'))\n"
                        + "UNION ALL\n"
                        + "(SELECT `tb_reproses`.`no_box`, `tb_grade_bahan_jadi`.`kode_grade` AS 'grade', `tb_reproses_pencabut`.`id_pegawai`, `tb_karyawan`.`nama_pegawai`, `tb_bagian`.`nama_bagian`, `tb_reproses`.`tgl_cabut_selesai` AS 'tgl_setor_cabut', `tb_reproses`.`tgl_cabut` AS 'tanggal_cabut', `jumlah_cabut`, `jumlah_gram`, `grup_cabut`, \n"
                        + "`tb_grade_bahan_jadi`.`upah_reproses` AS 'upah_per_gram', ROUND(`jumlah_gram` * `tb_grade_bahan_jadi`.`upah_reproses`) AS 'upah_cabut' \n"
                        + "FROM `tb_reproses_pencabut` \n"
                        + "LEFT JOIN `tb_reproses` ON `tb_reproses_pencabut`.`no_reproses` = `tb_reproses`.`no_reproses`\n"
                        + "LEFT JOIN `tb_box_bahan_jadi` ON `tb_reproses`.`no_box` = `tb_box_bahan_jadi`.`no_box` \n"
                        + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_box_bahan_jadi`.`kode_grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode`\n"
                        + "LEFT JOIN `tb_karyawan` ON `tb_reproses_pencabut`.`id_pegawai` = `tb_karyawan`.`id_pegawai`\n"
                        + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`\n"
                        + "WHERE " + search + " AND (`tgl_cabut_selesai` BETWEEN '" + dateFormat.format(Date1.getDate()) + "' AND '" + dateFormat.format(Date2.getDate()) + "'))"
                        + ") AS tabel \n"
                        + "LEFT JOIN (SELECT `bonus1_kecepatan`, `id_pegawai`, `tanggal` FROM `tb_lembur_rekap`) AS DATA_LEMBUR ON DATA_LEMBUR.`id_pegawai` = tabel.`id_pegawai` AND DATA_LEMBUR.`tanggal` = tabel.`tgl_setor_cabut`\n"
                        + "WHERE "
                        + "`nama_bagian` LIKE '%CABUT-BORONG%' "
                        + "OR `nama_bagian` LIKE '%CABUT-TRAINING%' \n"
                        + "GROUP BY `tgl_setor_cabut`, `id_pegawai`\n"
                        + "ORDER BY `tgl_setor_cabut`, `nama_pegawai`";
//                System.out.println(sql);
//                sql = "SELECT KINERJA.`id_pegawai`, `tb_karyawan`.`nama_pegawai`, `tb_bagian`.`nama_bagian`, CABUT.`tgl_setor_cabut`, KINERJA.`tanggal_cabut`, SUM(`jumlah_cabut`) AS 'jumlah_cabut', SUM(`jumlah_gram`) AS 'jumlah_gram', `grup_cabut`, "
//                        + "`bonus1_kecepatan`, "
//                        + "SUM(ROUND(`jumlah_gram` * `tb_tarif_cabut`.`tarif_gram`)) AS 'upah_borong_cabut' \n"
//                        + "FROM `tb_detail_pencabut` KINERJA \n"
//                        + "LEFT JOIN `tb_laporan_produksi` ON KINERJA.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
//                        + "LEFT JOIN `tb_tarif_cabut` ON `tb_laporan_produksi`.`jenis_bulu_lp` = `tb_tarif_cabut`.`bulu_upah` \n"
//                        + "LEFT JOIN `tb_cabut` CABUT ON KINERJA.`no_laporan_produksi` = CABUT.`no_laporan_produksi`\n"
//                        + "LEFT JOIN `tb_karyawan` ON KINERJA.`id_pegawai` = `tb_karyawan`.`id_pegawai`\n"
//                        + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`\n"
//                        + "LEFT JOIN (SELECT `bonus1_kecepatan`, `id_pegawai`, `tanggal` FROM `tb_lembur_rekap`) DATA_LEMBUR ON DATA_LEMBUR.`id_pegawai` = KINERJA.`id_pegawai` AND DATA_LEMBUR.`tanggal` = CABUT.`tgl_setor_cabut`\n"
//                        + "WHERE " + search + " AND (`tgl_setor_cabut` BETWEEN '" + dateFormat.format(Date1.getDate()) + "' AND '" + dateFormat.format(Date2.getDate()) + "') "
//                        + "AND `tb_bagian`.`nama_bagian` LIKE '%CABUT%' "
//                        + "GROUP BY KINERJA.`id_pegawai`, CABUT.`tgl_setor_cabut`";
                rs = Utility.db.getStatement().executeQuery(sql);
                Object[] row = new Object[15];
                while (rs.next()) {
                    row[0] = rs.getDate("tgl_setor_cabut");
                    row[1] = rs.getString("id_pegawai");
                    row[2] = rs.getString("nama_pegawai");
                    row[3] = rs.getString("nama_bagian");
                    row[4] = rs.getInt("jumlah_cabut");
                    int jumlah_cabut = 0;
                    if (rs.getInt("jumlah_cabut") == 0) {
                        jumlah_cabut = Math.round(rs.getFloat("jumlah_gram") / 8);
                    } else {
                        jumlah_cabut = rs.getInt("jumlah_cabut");
                    }

                    row[5] = jumlah_cabut;
                    row[6] = rs.getFloat("jumlah_gram");
                    row[7] = rs.getInt("upah_cabut");
                    row[8] = rs.getInt("bonus1_kecepatan");
                    model.addRow(row);
                }
                ColumnsAutoSizer.sizeColumnsToFit(table_data_rekap);
                label_filter_rekap.setText(filter);
            } else {
                JOptionPane.showMessageDialog(this, "Maaf filter tanggal setor cabut tidak boleh kosong !");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_DataPencabut.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_bonusLP() {
        try {
            Date tanggal_mulai = new Date(Date_penggajian.getDate().getTime() - (7 * 24 * 60 * 60 * 1000));
            Date tanggal_selesai = new Date(Date_penggajian.getDate().getTime() - (1 * 24 * 60 * 60 * 1000));
//            int total_kpg = 0;
            DefaultTableModel tb_bonus_kecepatan = (DefaultTableModel) table_bonus_kecepatan.getModel();
            tb_bonus_kecepatan.setRowCount(0);
            DefaultTableModel model_rekap1 = (DefaultTableModel) table_data_rekap_bonus1.getModel();
            model_rekap1.setRowCount(0);
            DefaultTableModel model_rekap2 = (DefaultTableModel) table_data_rekap_bonus2.getModel();
            model_rekap2.setRowCount(0);
            int bonus_kecepatan_per_LP = Integer.valueOf(txt_bonus_kecepatan_lp.getText());
            int total_bonus_lp_per_bagian = 0;
            total_bonus_lp_rekap_karyawan = 0;
            total_bonus_lp2_rekap_karyawan = 0;
            total_gram_cabutan = 0;
            sql = "SELECT y.`nama_bagian`,  y.`count_bagian`, y.`jumlah_pencabut`, `tb_laporan_produksi`.`no_laporan_produksi`, `jenis_bulu_lp`, `keping_upah`, `berat_basah`, "
                    + "cast((`keping_upah`/`kpg_lp`) as decimal(8, 6)) AS 'bobot_lp', "
                    + "(`berat_basah` * `tarif_gram`) AS 'upah_lp', "
                    + "`tb_cuci`.`tgl_masuk_cuci`, `tb_cabut`.`tgl_cabut`, `tb_cabut`.`tgl_setor_cabut`, `tb_cetak`.`tgl_mulai_cetak`, DATEDIFF(`tb_cetak`.`tgl_mulai_cetak`, `tb_cuci`.`tgl_masuk_cuci`) AS 'cuci_ke_cetak'\n"
                    + "FROM `tb_laporan_produksi`\n"
                    + "LEFT JOIN `tb_cabut` ON `tb_laporan_produksi`.`no_laporan_produksi` =`tb_cabut`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_cuci` ON `tb_laporan_produksi`.`no_laporan_produksi` =`tb_cuci`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_cetak` ON `tb_laporan_produksi`.`no_laporan_produksi` =`tb_cetak`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_tarif_cabut` ON `tb_laporan_produksi`.`jenis_bulu_lp` =`tb_tarif_cabut`.`bulu_upah`\n"
                    + "LEFT JOIN "
                    + "(SELECT `no_laporan_produksi`, `tb_bagian`.`nama_bagian`, COUNT(DISTINCT(`tb_detail_pencabut`.`kode_bagian`)) AS 'count_bagian', COUNT(DISTINCT(`id_pegawai`)) AS 'jumlah_pencabut' \n"
                    + "FROM `tb_detail_pencabut` LEFT JOIN `tb_bagian` ON `tb_detail_pencabut`.`kode_bagian` = `tb_bagian`.`kode_bagian` GROUP BY `no_laporan_produksi`) "
                    + "y ON `tb_laporan_produksi`.`no_laporan_produksi` = y.`no_laporan_produksi`\n"
                    + "WHERE `tb_laporan_produksi`.`no_laporan_produksi` LIKE 'WL-%'"
                    + "AND `tb_cabut`.`tgl_setor_cabut` BETWEEN '" + dateFormat.format(tanggal_mulai) + "' AND '" + dateFormat.format(tanggal_selesai) + "' \n"
                    + "AND `tb_cetak`.`tgl_mulai_cetak` IS NOT NULL \n"
                    + "AND y.`count_bagian` = 1 "
                    + "AND  y.`jumlah_pencabut` < 11 "
                    + "AND `tb_cabut`.`tgl_cabut` = `tb_cabut`.`tgl_setor_cabut` "
                    + "AND y.`nama_bagian` LIKE '%CABUT-BORONG%' "
                    + "AND y.`nama_bagian` LIKE '%" + txt_filter_bagian_bonus_lp.getText() + "%' "
                    + "ORDER BY `tb_cabut`.`tgl_setor_cabut`, y.`nama_bagian`";
//            System.out.println(sql);
            PreparedStatement pst = Utility.db.getConnection().prepareStatement(sql);
            rs = pst.executeQuery();
            Object[] row = new Object[15];
            Object[] row_rekap1 = new Object[10];
            String tgl = "", bagian = "";
            double bobot_lp_dikerjakan_grup = 0, upah_grup = 0;
            ArrayList<Integer> hari_bonus = new ArrayList();
            ArrayList<String> nama_bagian = new ArrayList();
            decimalFormat.setMaximumFractionDigits(7);
            while (rs.next()) {
                row[0] = rs.getString("nama_bagian");
                row[1] = rs.getString("no_laporan_produksi");
                row[2] = rs.getString("jenis_bulu_lp");
                row[3] = rs.getInt("keping_upah");
                row[4] = rs.getInt("berat_basah");
                row[5] = rs.getString("bobot_lp");
                row[6] = rs.getInt("jumlah_pencabut");
                row[7] = rs.getDate("tgl_masuk_cuci");
                row[8] = rs.getDate("tgl_cabut");
                row[9] = rs.getDate("tgl_setor_cabut");
                row[10] = rs.getDate("tgl_mulai_cetak");
                int cuci_ke_cabut = Utility.countDaysWithoutFreeDays(rs.getDate("tgl_masuk_cuci"), rs.getDate("tgl_cabut")) + 1;
                int cuci_ke_cetak = Utility.countDaysWithoutFreeDays(rs.getDate("tgl_masuk_cuci"), rs.getDate("tgl_mulai_cetak")) + 1;
                row[11] = cuci_ke_cabut;
                row[12] = cuci_ke_cetak;
                row[13] = rs.getInt("upah_lp");

                if (cuci_ke_cetak <= 3 && cuci_ke_cabut <= 2) {
                    tb_bonus_kecepatan.addRow(row);
                    if (tgl.equals(rs.getString("tgl_setor_cabut")) && bagian.equals(rs.getString("nama_bagian"))) {
                        bobot_lp_dikerjakan_grup = bobot_lp_dikerjakan_grup + rs.getDouble("bobot_lp");
                        upah_grup = upah_grup + rs.getDouble("upah_lp");
                    } else {
                        if (!tgl.equals("") && !bagian.equals("")) {
                            row_rekap1[0] = bagian;
                            row_rekap1[1] = tgl;
                            double bonus_per_bagian = bonus_kecepatan_per_LP;
                            bobot_lp_dikerjakan_grup = Math.round(bobot_lp_dikerjakan_grup * 100000d) / 100000d;
                            if (bobot_lp_dikerjakan_grup < 1) {
                                bonus_per_bagian = 0;
                            } else if (bobot_lp_dikerjakan_grup >= 1.1d) {
                                bonus_per_bagian = bobot_lp_dikerjakan_grup * bonus_kecepatan_per_LP;
                            } else {
                                bonus_per_bagian = bonus_kecepatan_per_LP;
                            }
                            row_rekap1[2] = decimalFormat.format(bobot_lp_dikerjakan_grup);
                            row_rekap1[3] = Math.round(bonus_per_bagian * 1000d) / 1000d;
                            row_rekap1[6] = upah_grup;
                            total_bonus_lp_per_bagian = (int) (total_bonus_lp_per_bagian + Math.round(bonus_per_bagian));
                            if (!nama_bagian.contains(bagian)) {
                                nama_bagian.add(bagian);
                                hari_bonus.add(0);
                            }
                            if (bonus_per_bagian > 0) {
                                int index = nama_bagian.indexOf(bagian);
                                hari_bonus.set(index, hari_bonus.get(index) + 1);
                            }
                            model_rekap1.addRow(row_rekap1);
                        }
                        tgl = rs.getString("tgl_setor_cabut");
                        bagian = rs.getString("nama_bagian");
                        bobot_lp_dikerjakan_grup = rs.getDouble("bobot_lp");
                        upah_grup = rs.getDouble("upah_lp");
                    }
                }
            }
            row_rekap1[0] = bagian;
            row_rekap1[1] = tgl;
            bobot_lp_dikerjakan_grup = Math.round(bobot_lp_dikerjakan_grup * 100000d) / 100000d;
            double bonus_per_bagian = bonus_kecepatan_per_LP;
            if (bobot_lp_dikerjakan_grup < 1) {
                bonus_per_bagian = 0;
            } else if (bobot_lp_dikerjakan_grup >= 1.1d) {
                bonus_per_bagian = bobot_lp_dikerjakan_grup * bonus_kecepatan_per_LP;
            } else {
                bonus_per_bagian = bonus_kecepatan_per_LP;
            }
            row_rekap1[2] = decimalFormat.format(bobot_lp_dikerjakan_grup);
            row_rekap1[3] = Math.round(bonus_per_bagian * 1000d) / 1000d;
            row_rekap1[6] = upah_grup;
            total_bonus_lp_per_bagian = (int) (total_bonus_lp_per_bagian + Math.round(bonus_per_bagian));
            if (!nama_bagian.contains(bagian)) {
                nama_bagian.add(bagian);
                hari_bonus.add(0);
            }
            if (bonus_per_bagian > 0) {
                int index = nama_bagian.indexOf(bagian);
                hari_bonus.set(index, hari_bonus.get(index) + 1);
            }
            model_rekap1.addRow(row_rekap1);

            //MENGHITUNG BONUS BERTURUT-TURUT PER GRUP
            jProgressBar1.setMaximum(table_data_rekap_bonus1.getRowCount());
            int hari_kerja = Utility.countDaysWithoutFreeDays(tanggal_mulai, tanggal_selesai) + 1;
            label_total_hariKerja.setText("Hari kerja : " + hari_kerja);
            double bonus_berturut2 = Double.valueOf(txt_bonus_berturut2.getText());
            double total_bonus_tambahan_per_bagian = 0;
            for (int i = 0; i < table_data_rekap_bonus1.getRowCount(); i++) {
                jProgressBar1.setValue(jProgressBar1.getValue() + 1);
                String bagian1 = table_data_rekap_bonus1.getValueAt(i, 0).toString();
                String tanggal1 = table_data_rekap_bonus1.getValueAt(i, 1).toString();
                double bobot = Double.valueOf(table_data_rekap_bonus1.getValueAt(i, 2).toString());
                double bonus_tambahan = 0;
                if (hari_bonus.get(nama_bagian.indexOf(bagian1)) >= hari_kerja) {
                    bonus_tambahan = bobot * bonus_berturut2;
                }
                table_data_rekap_bonus1.setValueAt(bonus_tambahan, i, 4);
                table_data_rekap_bonus1.setValueAt(hari_bonus.get(nama_bagian.indexOf(bagian1)), i, 5);
                total_bonus_tambahan_per_bagian = total_bonus_tambahan_per_bagian + bonus_tambahan;
            }
            //--------------------------------------------

            //MENGHITUNG BONUS LP SETIAP LP
            ArrayList<String> list_lp_dapat_bonus = new ArrayList();
            double total_bonus_lp_per_lp = 0, total_bonus_lp_tambahan_per_lp = 0;
            for (int j = 0; j < table_data_rekap_bonus1.getRowCount(); j++) {
                String grup = table_data_rekap_bonus1.getValueAt(j, 0).toString();
                String tanggal_setor_cabut = table_data_rekap_bonus1.getValueAt(j, 1).toString();
                double bonus_lp_per_grup = Double.valueOf(table_data_rekap_bonus1.getValueAt(j, 3).toString());
                double bonus_tambahan_per_grup = Double.valueOf(table_data_rekap_bonus1.getValueAt(j, 4).toString());
                double upah_per_grup = Double.valueOf(table_data_rekap_bonus1.getValueAt(j, 6).toString());
                if (bonus_lp_per_grup > 0) {
                    for (int i = 0; i < table_bonus_kecepatan.getRowCount(); i++) {
                        if (table_bonus_kecepatan.getValueAt(i, 0).toString().equals(grup)
                                && table_bonus_kecepatan.getValueAt(i, 9).toString().equals(tanggal_setor_cabut)) {
                            String no_lp = table_bonus_kecepatan.getValueAt(i, 1).toString();
                            double upah_lp = Double.valueOf(table_bonus_kecepatan.getValueAt(i, 13).toString());
                            double nominal_bonus_lp_per_lp = (upah_lp / upah_per_grup) * bonus_lp_per_grup;
                            double nominal_bonus_tambahan_per_lp = (upah_lp / upah_per_grup) * bonus_tambahan_per_grup;
                            table_bonus_kecepatan.setValueAt(Math.round(nominal_bonus_lp_per_lp * 1000d) / 1000d, i, 14);
                            table_bonus_kecepatan.setValueAt(Math.round(nominal_bonus_tambahan_per_lp * 1000d) / 1000d, i, 15);
                            total_bonus_lp_per_lp = total_bonus_lp_per_lp + nominal_bonus_lp_per_lp;
                            total_bonus_lp_tambahan_per_lp = total_bonus_lp_tambahan_per_lp + nominal_bonus_tambahan_per_lp;

                            list_lp_dapat_bonus.add(no_lp);
                            String Query = "UPDATE `tb_laporan_produksi` SET "
                                    + "`dapat_bonus_lp`=1, "
                                    + "`nominal_bonus_lp`=" + nominal_bonus_lp_per_lp + ", \n"
                                    + "`nominal_bonus_lp_tambahan`=" + nominal_bonus_tambahan_per_lp + " \n"
                                    + "WHERE `no_laporan_produksi`='" + no_lp + "'";
                            Utility.db.getConnection().createStatement();
                            Utility.db.getStatement().executeUpdate(Query);
                        }
                    }
                }
            }
            //--------------------------------------------
            if (list_lp_dapat_bonus.size() > 0) {
                refreshTabel_pembagian_bonus_lp_per_karyawan(list_lp_dapat_bonus, model_rekap2);
            }

            ColumnsAutoSizer.sizeColumnsToFit(table_bonus_kecepatan);
            ColumnsAutoSizer.sizeColumnsToFit(table_data_rekap_bonus1);

            decimalFormat.setMaximumFractionDigits(2);
            label_jumlah_lp_bonus.setText("Jumlah LP Bonus : " + decimalFormat.format(table_bonus_kecepatan.getRowCount()));
            label_total_bonus_lp.setText("Total Bonus : Rp. " + decimalFormat.format(total_bonus_lp_per_lp));
            label_total_bonus_lp2.setText("Total Bonus+ : Rp. " + decimalFormat.format(total_bonus_lp_tambahan_per_lp));
            label_total_bonus_lp_rekapgrup.setText("Total Bonus : Rp. " + decimalFormat.format(total_bonus_lp_per_bagian));
            label_total_bonus_lp2_rekapgrup.setText("Total Bonus+ : Rp. " + decimalFormat.format(total_bonus_tambahan_per_bagian));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_DataPencabut.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void refreshTabel_pembagian_bonus_lp_per_karyawan(ArrayList<String> list_lp_dapat_bonus, DefaultTableModel model_rekap2) {
        try {
            double total_bonus_lp_per_karyawan = 0, total_bonus_lp_tambahan_per_karyawan = 0;
            String no_lp = "";
            for (int i = 0; i < list_lp_dapat_bonus.size(); i++) {
                if (i != 0) {
                    no_lp = no_lp + ", ";
                }
                no_lp = no_lp + "'" + list_lp_dapat_bonus.get(i) + "'";
            }
            String query = "SELECT `nama_bagian`, `tgl_setor_cabut`, `id_pegawai`, `nama_pegawai`, SUM(`upah_cabut`) AS 'upah_cabut', SUM(`bonus_lp_per_lp_per_karyawan`) AS 'bonus_lp_per_lp_per_karyawan', SUM(`bonus_lp_tambahan_per_lp_per_karyawan`) AS 'bonus_lp_tambahan_per_lp_per_karyawan' "
                    + "FROM "
                    + "(SELECT `tb_detail_pencabut`.`no_laporan_produksi`, `tb_cabut`.`tgl_setor_cabut`, \n"
                    + "`tb_detail_pencabut`.`id_pegawai`, `tb_karyawan`.`nama_pegawai`, `tb_bagian`.`nama_bagian`, \n"
                    + "`tb_laporan_produksi`.`berat_basah`, `tarif_gram`, `nominal_bonus_lp`, `nominal_bonus_lp_tambahan`, \n"
                    + "(`jumlah_gram` * `tarif_gram`) AS 'upah_cabut', "
                    + "((`jumlah_gram` / `berat_basah`) * `nominal_bonus_lp`) AS 'bonus_lp_per_lp_per_karyawan', "
                    + "((`jumlah_gram` / `berat_basah`) * `nominal_bonus_lp_tambahan`) AS 'bonus_lp_tambahan_per_lp_per_karyawan' "
                    + "FROM `tb_detail_pencabut` \n"
                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_detail_pencabut`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi` \n"
                    + "LEFT JOIN `tb_cabut` ON `tb_detail_pencabut`.`no_laporan_produksi` = `tb_cabut`.`no_laporan_produksi` \n"
                    + "LEFT JOIN `tb_karyawan` ON `tb_detail_pencabut`.`id_pegawai` = `tb_karyawan`.`id_pegawai` \n"
                    + "LEFT JOIN `tb_bagian` ON `tb_detail_pencabut`.`kode_bagian` = `tb_bagian`.`kode_bagian` \n"
                    + "LEFT JOIN `tb_tarif_cabut` ON `tb_laporan_produksi`.`jenis_bulu_lp` = `tb_tarif_cabut`.`bulu_upah` \n"
                    + "WHERE \n"
                    + "`tb_detail_pencabut`.`no_laporan_produksi` IN (" + no_lp + ")"
                    + ") DATA "
                    + "WHERE 1 "
                    + "GROUP BY `tgl_setor_cabut`, `id_pegawai` "
                    + "ORDER BY `nama_bagian`, `tgl_setor_cabut`, `nama_pegawai` ";
            ResultSet result = Utility.db.getStatement().executeQuery(query);
            while (result.next()) {
                Object[] row = new Object[15];
                row[0] = result.getString("nama_bagian");
                row[1] = result.getDate("tgl_setor_cabut");
                row[2] = result.getString("id_pegawai");
                row[3] = result.getString("nama_pegawai");
                row[4] = Math.round(result.getDouble("upah_cabut") * 1000d) / 1000d;
                row[5] = Math.round(result.getDouble("bonus_lp_per_lp_per_karyawan") * 1000d) / 1000d;
                row[6] = Math.round(result.getDouble("bonus_lp_tambahan_per_lp_per_karyawan") * 1000d) / 1000d;
                row[7] = Math.round((result.getDouble("bonus_lp_per_lp_per_karyawan") + result.getDouble("bonus_lp_tambahan_per_lp_per_karyawan")) * 1000d) / 1000d;
                model_rekap2.addRow(row);
                total_bonus_lp_per_karyawan = total_bonus_lp_per_karyawan + result.getDouble("bonus_lp_per_lp_per_karyawan");
                total_bonus_lp_tambahan_per_karyawan = total_bonus_lp_tambahan_per_karyawan + result.getDouble("bonus_lp_tambahan_per_lp_per_karyawan");
            }
            ColumnsAutoSizer.sizeColumnsToFit(table_data_rekap_bonus2);

            decimalFormat.setMaximumFractionDigits(2);
            label_total_bonus_lp_rekap_karyawan.setText("Total Bonus : Rp. " + decimalFormat.format(total_bonus_lp_per_karyawan));
            label_total_bonus_lp2_rekap_karyawan.setText("Total Bonus+ : Rp. " + decimalFormat.format(total_bonus_lp_tambahan_per_karyawan));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_DataPencabut.class.getName()).log(Level.SEVERE, null, ex);
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
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table_data_pencabut = new javax.swing.JTable();
        button_refresh_allData = new javax.swing.JButton();
        button_refresh_rekap = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        label_total_kpg = new javax.swing.JLabel();
        ComboBox_Search = new javax.swing.JComboBox<>();
        Date2 = new com.toedter.calendar.JDateChooser();
        Date1 = new com.toedter.calendar.JDateChooser();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txt_search = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        button_export = new javax.swing.JButton();
        jScrollPane7 = new javax.swing.JScrollPane();
        table_data_reproses_cabut = new javax.swing.JTable();
        jLabel14 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        button_saveData = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        table_data_rekap = new javax.swing.JTable();
        button_export_bonus_LP1 = new javax.swing.JButton();
        label_filter_rekap = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        table_bonus_kecepatan = new javax.swing.JTable();
        button_refresh_bonus = new javax.swing.JButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel6 = new javax.swing.JLabel();
        Date_penggajian = new com.toedter.calendar.JDateChooser();
        jLabel8 = new javax.swing.JLabel();
        txt_bonus_kecepatan_lp = new javax.swing.JTextField();
        label_jumlah_lp_bonus = new javax.swing.JLabel();
        jProgressBar1 = new javax.swing.JProgressBar();
        jLabel9 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        txt_filter_bagian_bonus_lp = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        txt_bonus_berturut2 = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        button_save_data_bonus_kecepatan = new javax.swing.JButton();
        jScrollPane6 = new javax.swing.JScrollPane();
        table_data_rekap_bonus2 = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        label_total_bonus_lp_rekap_karyawan = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        table_data_rekap_bonus1 = new javax.swing.JTable();
        label_total_bonus_lp2_rekapgrup = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        label_total_hariKerja = new javax.swing.JLabel();
        label_total_bonus_lp_rekapgrup = new javax.swing.JLabel();
        button_export_bonus_LP = new javax.swing.JButton();
        label_total_bonus_lp2_rekap_karyawan = new javax.swing.JLabel();
        label_total_bonus_lp2 = new javax.swing.JLabel();
        label_total_bonus_lp = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Data Kinerja Cabut", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        table_data_pencabut.setAutoCreateRowSorter(true);
        table_data_pencabut.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_data_pencabut.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Pegawai", "Nama", "Bagian", "No LP", "No kartu", "Grade", "Bulu Upah", "Tanggal Cabut", "Tanggal Setor Cabut", "Kpg", "Kpg Upah", "Gram", "Upah (Rp.)/Gr", "Total Upah (Rp.)", "Bonus LP", "Bonus LP+"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(table_data_pencabut);

        button_refresh_allData.setBackground(new java.awt.Color(255, 255, 255));
        button_refresh_allData.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_refresh_allData.setText("Refresh");
        button_refresh_allData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refresh_allDataActionPerformed(evt);
            }
        });

        button_refresh_rekap.setBackground(new java.awt.Color(255, 255, 255));
        button_refresh_rekap.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_refresh_rekap.setText("Rekap /Karyawan /Hari");
        button_refresh_rekap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refresh_rekapActionPerformed(evt);
            }
        });

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel10.setText("Total Keping Upah Cabut :");

        label_total_kpg.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kpg.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_kpg.setText("88");

        ComboBox_Search.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_Search.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Nama Pejuang", "Bagian", "No LP / No Box" }));

        Date2.setBackground(new java.awt.Color(255, 255, 255));
        Date2.setDate(new Date());
        Date2.setDateFormatString("dd MMMM yyyy");
        Date2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Date1.setBackground(new java.awt.Color(255, 255, 255));
        Date1.setDate(new Date(new Date().getTime()- (7 * 24 * 60 * 60 * 1000)));
        Date1.setDateFormatString("dd MMMM yyyy");
        Date1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("Sampai");

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("Filter Tanggal Setor :");

        txt_search.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_searchKeyPressed(evt);
            }
        });

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel5.setText("Keywords :");

        button_export.setBackground(new java.awt.Color(255, 255, 255));
        button_export.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export.setText("Export to Excel");
        button_export.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_exportActionPerformed(evt);
            }
        });

        table_data_reproses_cabut.setAutoCreateRowSorter(true);
        table_data_reproses_cabut.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_data_reproses_cabut.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Pegawai", "Nama", "Bagian", "No Box", "Grade", "Tgl Cabut", "Tgl Cabut Selesai", "Kpg", "Kpg Upah", "Gram", "Upah (Rp.)/Gr", "Total Upah (Rp.)"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane7.setViewportView(table_data_reproses_cabut);

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel14.setText("Reproses Cabut");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_Search, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date1, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date2, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_refresh_allData)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_refresh_rekap)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 275, Short.MAX_VALUE)
                        .addComponent(button_export))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_kpg)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jScrollPane1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane7)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel14)
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_search, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(ComboBox_Search, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(button_export, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_refresh_allData, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_refresh_rekap, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_kpg, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 571, Short.MAX_VALUE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("All Data", jPanel3);

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        button_saveData.setBackground(new java.awt.Color(255, 255, 255));
        button_saveData.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_saveData.setText("Save Data");
        button_saveData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_saveDataActionPerformed(evt);
            }
        });

        table_data_rekap.setAutoCreateRowSorter(true);
        table_data_rekap.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_data_rekap.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tgl Setor Cabut", "ID Pegawai", "Nama", "Bagian", "Kpg", "Kpg Upah", "Gram", "Total Upah (Rp.)", "Bonus LP"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table_data_rekap.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(table_data_rekap);

        button_export_bonus_LP1.setBackground(new java.awt.Color(255, 255, 255));
        button_export_bonus_LP1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_bonus_LP1.setText("Export");
        button_export_bonus_LP1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_bonus_LP1ActionPerformed(evt);
            }
        });

        label_filter_rekap.setBackground(new java.awt.Color(255, 255, 255));
        label_filter_rekap.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_filter_rekap.setText("FILTER : Nama bagian LIKE \"%CABUT-BORONG%\" / \"%CABUT-TRAINING%\"");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1332, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(label_filter_rekap)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_export_bonus_LP1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_saveData)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_saveData, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export_bonus_LP1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_filter_rekap))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 597, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Rekap /Karyawan /Hari", jPanel4);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        table_bonus_kecepatan.setAutoCreateRowSorter(true);
        table_bonus_kecepatan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_bonus_kecepatan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nama Grup", "No LP", "Bulu", "Kpg Upah", "Gram", "Bobot", "Pencabut", "Tgl Cuci", "Tgl Cabut", "Setor CBT", "Terima CTK", "Cuci-Cbt", "Cuci-Ctk", "Upah LP", "Bonus LP", "Bonus +"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Float.class, java.lang.String.class, java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
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

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setText("NOTES :\n1. LP dikerjakan max 10 orang.\n2. LP dikerjakan oleh 1 grup yang sama\n3. LP di kerjakan 1 hari (tgl cabut = tgl setor cabut)\n4. LP bukan LP Tandon (Selisih tgl cuci dan tgl mulai cabut MAX 2 hari kerja) -- intinya cucian hari ini besok harus dicabut, bukan dicabut 2 hari lagi\n5. Hari Cuci - Cetak MAX 3 hari kerja (tidak terhitung hari libur / HARI MINGGU) contoh : cuci tgl 10 - tgl 13 Minggu - terima cetak tgl 14 = 4 Hari.\n6. Harus load seminggu supaya tambahan bonus berturut-turutnya benar.\n7. Total bobot LP dikerjakan dalam 1 hari > 1 maka bonus LP 155.000, jika bobot >= 1.1 maka 155.000 x bobot LP di kerjakan.\n8. Bobot setiap LP akan dibulatkan normal ke 6 angka desimal, dan total bobot LP per grup per hari dibulatkan normal ke 5 angka desimal.\n9. bonus LP hanya menampilkan dan diberikan kepada bagian CABUT BORONG.");
        jScrollPane5.setViewportView(jTextArea1);

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel6.setText("Tgl Penggajian :");

        Date_penggajian.setBackground(new java.awt.Color(255, 255, 255));
        Date_penggajian.setDate(new Date(new Date().getTime()- (7 * 24 * 60 * 60 * 1000)));
        Date_penggajian.setDateFormatString("dd MMM yyyy");
        Date_penggajian.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel8.setText("Bonus / LP : ");

        txt_bonus_kecepatan_lp.setEditable(false);
        txt_bonus_kecepatan_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_bonus_kecepatan_lp.setText("155000");

        label_jumlah_lp_bonus.setBackground(new java.awt.Color(255, 255, 255));
        label_jumlah_lp_bonus.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_jumlah_lp_bonus.setText("Jumlah LP Bonus : ");

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel9.setText("LOADING PROGRESS :");

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel11.setText("Bagian");

        txt_filter_bagian_bonus_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel13.setText("Bonus Berturut2 / LP : ");

        txt_bonus_berturut2.setEditable(false);
        txt_bonus_berturut2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_bonus_berturut2.setText("10000");

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));

        button_save_data_bonus_kecepatan.setBackground(new java.awt.Color(255, 255, 255));
        button_save_data_bonus_kecepatan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_save_data_bonus_kecepatan.setText("Save Data Bonus");
        button_save_data_bonus_kecepatan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_save_data_bonus_kecepatanActionPerformed(evt);
            }
        });

        table_data_rekap_bonus2.setAutoCreateRowSorter(true);
        table_data_rekap_bonus2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_data_rekap_bonus2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Grup", "Tgl Setor Cabut", "ID karyawan", "Nama Karyawan", "Gaji Cabutan", "Bonus LP", "Bonus berturut2", "Tot Bonus"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table_data_rekap_bonus2.getTableHeader().setReorderingAllowed(false);
        jScrollPane6.setViewportView(table_data_rekap_bonus2);

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel4.setText("Bonus /karyawan /Hari");

        label_total_bonus_lp_rekap_karyawan.setBackground(new java.awt.Color(255, 255, 255));
        label_total_bonus_lp_rekap_karyawan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_bonus_lp_rekap_karyawan.setText("Total");

        table_data_rekap_bonus1.setAutoCreateRowSorter(true);
        table_data_rekap_bonus1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_data_rekap_bonus1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Grup", "Tgl Setor Cabut", "Bobot LP Grup", "Bonus (Rp.)", "+Bonus", "h", "Upah Grup"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Integer.class, java.lang.Float.class
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
        table_data_rekap_bonus1.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(table_data_rekap_bonus1);

        label_total_bonus_lp2_rekapgrup.setBackground(new java.awt.Color(255, 255, 255));
        label_total_bonus_lp2_rekapgrup.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_bonus_lp2_rekapgrup.setText("Total Bonus LP+");

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel1.setText("Rekap /Grup /Hari");

        label_total_hariKerja.setBackground(new java.awt.Color(255, 255, 255));
        label_total_hariKerja.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_hariKerja.setText("Total Hari Kerja");

        label_total_bonus_lp_rekapgrup.setBackground(new java.awt.Color(255, 255, 255));
        label_total_bonus_lp_rekapgrup.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_bonus_lp_rekapgrup.setText("Total Bonus LP");

        button_export_bonus_LP.setBackground(new java.awt.Color(255, 255, 255));
        button_export_bonus_LP.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_bonus_LP.setText("Export");
        button_export_bonus_LP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_bonus_LPActionPerformed(evt);
            }
        });

        label_total_bonus_lp2_rekap_karyawan.setBackground(new java.awt.Color(255, 255, 255));
        label_total_bonus_lp2_rekap_karyawan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_bonus_lp2_rekap_karyawan.setText("Total+");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_bonus_lp_rekapgrup)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(label_total_bonus_lp2_rekapgrup)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_hariKerja)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 579, Short.MAX_VALUE)
                            .addComponent(jScrollPane6)))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_bonus_lp_rekap_karyawan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_bonus_lp2_rekap_karyawan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_export_bonus_LP)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_save_data_bonus_kecepatan)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(label_total_bonus_lp_rekapgrup)
                    .addComponent(label_total_hariKerja)
                    .addComponent(label_total_bonus_lp2_rekapgrup))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(label_total_bonus_lp_rekap_karyawan)
                    .addComponent(button_save_data_bonus_kecepatan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export_bonus_LP, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_bonus_lp2_rekap_karyawan))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        label_total_bonus_lp2.setBackground(new java.awt.Color(255, 255, 255));
        label_total_bonus_lp2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_bonus_lp2.setText("Total Bonus LP+");

        label_total_bonus_lp.setBackground(new java.awt.Color(255, 255, 255));
        label_total_bonus_lp.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_bonus_lp.setText("Total Bonus LP");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel9)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(button_refresh_bonus)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(label_jumlah_lp_bonus, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel8)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txt_bonus_kecepatan_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jLabel13)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txt_bonus_berturut2, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jLabel6)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(Date_penggajian, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jLabel11)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txt_filter_bagian_bonus_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jScrollPane5)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 736, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(label_total_bonus_lp)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(label_total_bonus_lp2)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(Date_penggajian, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_bonus_kecepatan_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_filter_bagian_bonus_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_bonus_berturut2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_jumlah_lp_bonus, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_refresh_bonus, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_total_bonus_lp)
                    .addComponent(label_total_bonus_lp2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 393, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Bonus Kecepatan", jPanel1);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1357, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
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

    private void button_exportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_exportActionPerformed
        // TODO add your handling code here:
        DefaultTableModel modelA = (DefaultTableModel) table_data_pencabut.getModel();
        ExportToExcel.writeToExcel(modelA, jPanel2);
    }//GEN-LAST:event_button_exportActionPerformed

    private void button_refresh_allDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refresh_allDataActionPerformed
        // TODO add your handling code here:
        refreshTable_lp_cabut();
        refreshTable_reproses_cabut();
    }//GEN-LAST:event_button_refresh_allDataActionPerformed

    private void txt_searchKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_searchKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_lp_cabut();
            refreshTable_reproses_cabut();
        }
    }//GEN-LAST:event_txt_searchKeyPressed

    private void button_saveDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_saveDataActionPerformed
        // TODO add your handling code here:
        int dialogResult = JOptionPane.showConfirmDialog(this, "Save " + table_data_rekap.getRowCount() + " data ?", "Warning", 0);
        if (dialogResult == JOptionPane.YES_OPTION) {
            try {
                Utility.db.getConnection().setAutoCommit(false);
                for (int i = 0; i < table_data_rekap.getRowCount(); i++) {
                    String Query = "INSERT INTO `tb_lembur_rekap`(`id_pegawai`, `tanggal`, `gaji_borong`) "
                            + "VALUES ("
                            + "'" + table_data_rekap.getValueAt(i, 1).toString() + "',"
                            + "'" + table_data_rekap.getValueAt(i, 0) + "',"
                            + table_data_rekap.getValueAt(i, 7) + ") "
                            + "ON DUPLICATE KEY UPDATE "
                            + "`gaji_borong`=" + table_data_rekap.getValueAt(i, 7);
                    Utility.db.getConnection().createStatement();
                    Utility.db.getStatement().executeUpdate(Query);
                }
                Utility.db.getConnection().commit();
                JOptionPane.showMessageDialog(this, "Data Saved Successfully");
            } catch (Exception e) {
                try {
                    Utility.db.getConnection().rollback();
                } catch (SQLException ex) {
                    Logger.getLogger(JPanel_DataPencabut.class.getName()).log(Level.SEVERE, null, ex);
                }
                JOptionPane.showMessageDialog(this, "Save Failed !" + e);
                Logger.getLogger(JPanel_DataPencabut.class.getName()).log(Level.SEVERE, null, e);
            } finally {
                try {
                    Utility.db.getConnection().setAutoCommit(true);
                } catch (SQLException ex) {
                    Logger.getLogger(JPanel_DataPencabut.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_button_saveDataActionPerformed

    private void button_refresh_rekapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refresh_rekapActionPerformed
        // TODO add your handling code here:
        jTabbedPane1.setSelectedIndex(1);
        refreshTable_Rekap();
    }//GEN-LAST:event_button_refresh_rekapActionPerformed

    private void button_export_bonus_LP1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_bonus_LP1ActionPerformed
        // TODO add your handling code here:
        DefaultTableModel table = (DefaultTableModel) table_data_rekap.getModel();
        ExportToExcel.writeToExcel(table, this);
    }//GEN-LAST:event_button_export_bonus_LP1ActionPerformed

    private void button_refresh_bonusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refresh_bonusActionPerformed
        // TODO add your handling code here:
        boolean check = true;
        try {
            double bonus_berturut2 = Double.valueOf(txt_bonus_berturut2.getText());
            int bonus_kecepatan_per_LP = Integer.valueOf(txt_bonus_kecepatan_lp.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Nominal bonus salah !");
            check = false;
        }
        if (Date_penggajian.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Silahkan pilih tanggal penggajian !");
            check = false;
        }
        if (check) {
            txt_bonus_kecepatan_lp.setEnabled(false);
            txt_bonus_berturut2.setEnabled(false);
            Date_penggajian.setEnabled(false);
            txt_filter_bagian_bonus_lp.setEnabled(false);
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
                        JOptionPane.showMessageDialog(jPanel1, "Proses Selesai !");
                        txt_bonus_kecepatan_lp.setEnabled(true);
                        txt_bonus_berturut2.setEnabled(true);
                        Date_penggajian.setEnabled(true);
                        txt_filter_bagian_bonus_lp.setEnabled(true);
                        button_refresh_bonus.setEnabled(true);
                        button_save_data_bonus_kecepatan.setEnabled(true);
                    }
                };
                thread.start();
            } catch (Exception e) {
                Logger.getLogger(JPanel_DataPencabut.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }//GEN-LAST:event_button_refresh_bonusActionPerformed

    private void button_save_data_bonus_kecepatanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_save_data_bonus_kecepatanActionPerformed
        // TODO add your handling code here:
        int dialogResult = JOptionPane.showConfirmDialog(this, "Save " + table_data_rekap_bonus2.getRowCount() + " data ?", "Warning", 0);
        if (dialogResult == JOptionPane.YES_OPTION) {
            try {
                Utility.db.getConnection().setAutoCommit(false);
                for (int i = 0; i < table_data_rekap_bonus2.getRowCount(); i++) {
                    String Query = "INSERT INTO `tb_lembur_rekap`(`id_pegawai`, `tanggal`, `bonus1_kecepatan`) "
                            + "VALUES ("
                            + "'" + table_data_rekap_bonus2.getValueAt(i, 2).toString() + "',"
                            + "'" + table_data_rekap_bonus2.getValueAt(i, 1).toString() + "',"
                            + table_data_rekap_bonus2.getValueAt(i, 7).toString() + ") "
                            + "ON DUPLICATE KEY UPDATE "
                            + "`bonus1_kecepatan`=" + table_data_rekap_bonus2.getValueAt(i, 7).toString();
                    Utility.db.getConnection().createStatement();
                    Utility.db.getStatement().executeUpdate(Query);
                }
                Utility.db.getConnection().commit();
                JOptionPane.showMessageDialog(this, "Data Saved Successfully");
            } catch (Exception e) {
                try {
                    Utility.db.getConnection().rollback();
                } catch (SQLException ex) {
                    Logger.getLogger(JPanel_DataPencabut.class.getName()).log(Level.SEVERE, null, ex);
                }
                JOptionPane.showMessageDialog(this, "Save Failed !" + e);
                Logger.getLogger(JPanel_DataPencabut.class.getName()).log(Level.SEVERE, null, e);
            } finally {
                try {
                    Utility.db.getConnection().setAutoCommit(true);
                } catch (SQLException ex) {
                    Logger.getLogger(JPanel_DataPencabut.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_button_save_data_bonus_kecepatanActionPerformed

    private void button_export_bonus_LPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_bonus_LPActionPerformed
        // TODO add your handling code here:
        DefaultTableModel table = (DefaultTableModel) table_data_rekap_bonus2.getModel();
        ExportToExcel.writeToExcel(table, jPanel1);
    }//GEN-LAST:event_button_export_bonus_LPActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_Search;
    private com.toedter.calendar.JDateChooser Date1;
    private com.toedter.calendar.JDateChooser Date2;
    private com.toedter.calendar.JDateChooser Date_penggajian;
    private javax.swing.JButton button_export;
    private javax.swing.JButton button_export_bonus_LP;
    private javax.swing.JButton button_export_bonus_LP1;
    private javax.swing.JButton button_refresh_allData;
    private javax.swing.JButton button_refresh_bonus;
    private javax.swing.JButton button_refresh_rekap;
    private javax.swing.JButton button_saveData;
    private javax.swing.JButton button_save_data_bonus_kecepatan;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JLabel label_filter_rekap;
    private javax.swing.JLabel label_jumlah_lp_bonus;
    private javax.swing.JLabel label_total_bonus_lp;
    private javax.swing.JLabel label_total_bonus_lp2;
    private javax.swing.JLabel label_total_bonus_lp2_rekap_karyawan;
    private javax.swing.JLabel label_total_bonus_lp2_rekapgrup;
    private javax.swing.JLabel label_total_bonus_lp_rekap_karyawan;
    private javax.swing.JLabel label_total_bonus_lp_rekapgrup;
    private javax.swing.JLabel label_total_hariKerja;
    private javax.swing.JLabel label_total_kpg;
    private javax.swing.JTable table_bonus_kecepatan;
    private javax.swing.JTable table_data_pencabut;
    private javax.swing.JTable table_data_rekap;
    private javax.swing.JTable table_data_rekap_bonus1;
    private javax.swing.JTable table_data_rekap_bonus2;
    private javax.swing.JTable table_data_reproses_cabut;
    private javax.swing.JTextField txt_bonus_berturut2;
    private javax.swing.JTextField txt_bonus_kecepatan_lp;
    private javax.swing.JTextField txt_filter_bagian_bonus_lp;
    private javax.swing.JTextField txt_search;
    // End of variables declaration//GEN-END:variables

}
