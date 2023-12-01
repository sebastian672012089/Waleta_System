package waleta_system.Finance;

import java.awt.event.KeyEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.ExportToExcel;
import waleta_system.Class.Utility;

public class JPanel_GajiCetak_percobaan extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DecimalFormat decimalFormat = new DecimalFormat();
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    float min_lp_dikerjakan = 0f;
    int max_lama_inap_t1 = 0;
    int max_lama_inap_t2 = 0;
    int max_lama_inap_t12 = 0;
    float upah_cetak_t2_utuh = 0f;
    float upah_cetak_t2_pch = 0f;
    float upah_cetak_t2_flat = 0f;
    float upah_cetak_t2_jdn = 0f;
    float upah_reproses_utuh = 0f;
    float upah_reproses_pch = 0f;
    float upah_reproses_flat = 0f;
    float upah_reproses_jdn = 0f;

    public JPanel_GajiCetak_percobaan() {
        initComponents();
    }

    public void init() {
        try {
            decimalFormat = Utility.DecimalFormatUS(decimalFormat);
            table_data_pegawai_cetak.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent event) {
                    if (!event.getValueIsAdjusting() && table_data_pegawai_cetak.getSelectedRow() != -1) {
                        int i = table_data_pegawai_cetak.getSelectedRow();
                        String id_pegawai = table_data_pegawai_cetak.getValueAt(i, 1).toString();
                        String nama_pegawai = table_data_pegawai_cetak.getValueAt(i, 2).toString();
                        label_nama_detail_lp1.setText(nama_pegawai);
                        label_nama_detail_lp2.setText(nama_pegawai);
                        label_nama_detail_lp3.setText(nama_pegawai);
                        refreshTable_detail_lp_cetak1(id_pegawai);
                        refreshTable_detail_lp_cetak2(id_pegawai);
                        refreshTable_detail_lp_cetak3(id_pegawai);
                        refreshTable_detail_box_reproses(id_pegawai);
                    }
                }
            });
        } catch (Exception ex) {
            Logger.getLogger(JPanel_GajiCetak_percobaan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_cetak() {
        try {
            float total_gaji = 0f;
            double total_bonus = 0d, total_gram = 0d, total_kpg = 0d;
            int nomor = 0;

            DefaultTableModel model = (DefaultTableModel) table_data_pegawai_cetak.getModel();
            model.setRowCount(0);

            min_lp_dikerjakan = Integer.valueOf(txt_minimal_lp_dikerjakan.getText());
            max_lama_inap_t1 = Integer.valueOf(txt_max_lama_inap_t1.getText());
            max_lama_inap_t2 = Integer.valueOf(txt_max_lama_inap_t2.getText());
            max_lama_inap_t12 = max_lama_inap_t1 + max_lama_inap_t2;
            upah_cetak_t2_utuh = Integer.valueOf(txt_upah_cetak_t2.getText());
            upah_cetak_t2_pch = upah_cetak_t2_utuh / 2f;
            upah_cetak_t2_flat = upah_cetak_t2_pch / 2f;
            upah_cetak_t2_jdn = 0f;
            upah_reproses_utuh = Integer.valueOf(txt_upah_reproses.getText());
            upah_reproses_pch = upah_reproses_utuh / 2f;
            upah_reproses_flat = upah_reproses_pch / 2f;
            upah_reproses_jdn = 800f;

            Date date1 = DateFilter_SetorCetak1.getDate();
            Date date2 = DateFilter_SetorCetak2.getDate();
            float hari_kerja = Utility.countDaysWithoutFreeDays(date1, date2) + 1;
            float min_lp_dikerjakan_float = (hari_kerja / 6f) * min_lp_dikerjakan;
            decimalFormat.setMaximumFractionDigits(2);
            txt_hari_kerja.setText(Float.toString(hari_kerja));
            txt_target_lp_cetak.setText(decimalFormat.format(min_lp_dikerjakan_float));

            sql = "SELECT `cetak_dikerjakan`, `nama_pegawai`, `nama_bagian`, `cetak_dikerjakan_level`, \n"
                    + "SUM(`Jumlah_LP`) AS 'Jumlah_LP', SUM(`total_kpg`) AS 'total_kpg', SUM(`total_gram`) AS 'total_gram', SUM(`mku`) AS 'mku', SUM(`mpch`) AS 'mpch', SUM(`flat`) AS 'flat', SUM(`jdn`) AS 'jdn', \n"
                    + "SUM(`bobot_bonus_lp_t1`) AS 'bobot_bonus_lp_t1', \n"
                    + "SUM(`bobot_bonus_lp_t2`) AS 'bobot_bonus_lp_t2', \n"
                    + "(SUM(`bobot_bonus_lp_t12`) * 2) AS 'bobot_bonus_lp_t12' \n"
                    + "FROM \n"
                    + "(\n"
                    + "SELECT `cetak_dikerjakan1` AS 'cetak_dikerjakan', `tb_karyawan`.`nama_pegawai`, `tb_bagian`.`nama_bagian`, \n"
                    + "(SELECT `cetak_dikerjakan_level1` FROM `tb_cetak` CTK WHERE `tgl_selesai_cetak` BETWEEN '" + dateFormat.format(DateFilter_SetorCetak1.getDate()) + "' AND '" + dateFormat.format(DateFilter_SetorCetak2.getDate()) + "' "
                    + "AND `cetak_dikerjakan1` = `tb_cetak`.`cetak_dikerjakan1` ORDER BY `tgl_selesai_cetak` DESC LIMIT 1) AS 'cetak_dikerjakan_level', \n"
                    + "SUM(IF((`cetak_mangkok`+`cetak_pecah`+`cetak_flat`)=0,`cetak_jidun_real` / `kpg_lp_jidun`, `keping_upah` / IF(`tb_grade_bahan_baku`.`kategori`='FLAT',`kpg_lp_flat`, `kpg_lp`))) AS 'Jumlah_LP', \n"
                    + "SUM(`tb_laporan_produksi`.`keping_upah`) AS 'total_kpg', "
                    + "SUM(`tb_laporan_produksi`.`berat_basah`) AS 'total_gram', \n"
                    + "0 AS 'mku', \n"
                    + "0 AS 'mpch', \n"
                    + "0 AS 'flat', \n"
                    + "0 AS 'jdn', \n"
                    + "SUM(IF(DATEDIFF(`tgl_cetak_dikerjakan1`, `tgl_mulai_cetak`) <= " + max_lama_inap_t1 + " AND ((`cetak_mangkok`/`keping_upah`)*100)>=`target_ctk_mku`, (IF((`cetak_mangkok`+`cetak_pecah`+`cetak_flat`)=0,`cetak_jidun_real` / `kpg_lp_jidun`, `keping_upah` / IF(`tb_grade_bahan_baku`.`kategori`='FLAT',`kpg_lp_flat`, `kpg_lp`))), 0)) AS 'bobot_bonus_lp_t1', \n"
                    + "0 AS 'bobot_bonus_lp_t2', \n"
                    + "0 AS 'bobot_bonus_lp_t12' \n"
                    + "FROM `tb_cetak` \n"
                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_cetak`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_grade_bahan_baku` ON `tb_laporan_produksi`.`kode_grade` = `tb_grade_bahan_baku`.`kode_grade`\n"
                    + "LEFT JOIN `tb_tarif_cabut` ON `tb_laporan_produksi`.`jenis_bulu_lp` = `tb_tarif_cabut`.`bulu_upah`\n"
                    + "LEFT JOIN `tb_karyawan` ON `tb_cetak`.`cetak_dikerjakan1` = `tb_karyawan`.`id_pegawai`\n"
                    + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`\n"
                    + "WHERE \n"
                    + "`tb_karyawan`.`nama_pegawai` LIKE '%" + txt_search_nama_cetak.getText() + "%' \n"
                    + "AND `tgl_selesai_cetak` BETWEEN '" + dateFormat.format(DateFilter_SetorCetak1.getDate()) + "' AND '" + dateFormat.format(DateFilter_SetorCetak2.getDate()) + "'\n"
                    + "AND `cetak_dikerjakan` IS NOT NULL \n"
                    + "AND `tgl_selesai_cetak` IS NOT NULL \n"
                    + "AND `cetak_dikerjakan1` IS NOT NULL \n"
                    + "AND `tgl_cetak_dikerjakan1` IS NOT NULL \n"
                    + "GROUP BY `cetak_dikerjakan1` \n"
                    + ""
                    + "UNION ALL \n"
                    + ""
                    + "SELECT `cetak_dikerjakan`, `tb_karyawan`.`nama_pegawai`, `tb_bagian`.`nama_bagian`, \n"
                    + "(SELECT `cetak_dikerjakan_level` FROM `tb_cetak` CTK WHERE `tgl_selesai_cetak` BETWEEN '" + dateFormat.format(DateFilter_SetorCetak1.getDate()) + "' AND '" + dateFormat.format(DateFilter_SetorCetak2.getDate()) + "' "
                    + "AND `cetak_dikerjakan` = `tb_cetak`.`cetak_dikerjakan` ORDER BY `tgl_selesai_cetak` DESC LIMIT 1) AS 'cetak_dikerjakan_level', \n"
                    + "SUM(IF((`cetak_mangkok`+`cetak_pecah`+`cetak_flat`)=0,`cetak_jidun_real` / `kpg_lp_jidun`, `keping_upah` / IF(`tb_grade_bahan_baku`.`kategori`='FLAT',`kpg_lp_flat`, `kpg_lp`))) AS 'Jumlah_LP', \n"
                    + "SUM(`tb_laporan_produksi`.`keping_upah`) AS 'total_kpg', "
                    + "SUM(`tb_laporan_produksi`.`berat_basah`) AS 'total_gram', \n"
                    + "SUM(`cetak_mangkok` * " + upah_cetak_t2_utuh + " * IF(LOCATE('JUMBO', `tb_laporan_produksi`.`kode_grade`)>0 OR LOCATE('BPU', `tb_laporan_produksi`.`kode_grade`)>0, 1.05, 1)) AS 'mku', \n"
                    + "SUM(`cetak_pecah` * " + upah_cetak_t2_pch + " * IF(LOCATE('JUMBO', `tb_laporan_produksi`.`kode_grade`)>0 OR LOCATE('BPU', `tb_laporan_produksi`.`kode_grade`)>0, 1.05, 1)) AS 'mpch', \n"
                    + "SUM(`cetak_flat` * " + upah_cetak_t2_flat + " * IF(LOCATE('JUMBO', `tb_laporan_produksi`.`kode_grade`)>0 OR LOCATE('BPU', `tb_laporan_produksi`.`kode_grade`)>0, 1.05, 1)) AS 'flat', \n"
                    + "SUM(`cetak_jidun_real` * " + upah_cetak_t2_jdn + ") AS 'jdn', \n"
                    + "0 AS 'bobot_bonus_lp_t1', \n"
                    + "SUM(IF(DATEDIFF(`tgl_selesai_cetak`, IF(`tgl_cetak_dikerjakan1` IS NULL, `tgl_mulai_cetak`, `tgl_cetak_dikerjakan1`)) <= " + max_lama_inap_t2 + " AND ((`cetak_mangkok`/`keping_upah`)*100)>=`target_ctk_mku`, (IF((`cetak_mangkok`+`cetak_pecah`+`cetak_flat`)=0,`cetak_jidun_real` / `kpg_lp_jidun`, `keping_upah` / IF(`tb_grade_bahan_baku`.`kategori`='FLAT',`kpg_lp_flat`, `kpg_lp`))), 0)) AS 'bobot_bonus_lp_t2', \n"
                    + "0 AS 'bobot_bonus_lp_t12' \n"
                    + "FROM `tb_cetak` \n"
                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_cetak`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_grade_bahan_baku` ON `tb_laporan_produksi`.`kode_grade` = `tb_grade_bahan_baku`.`kode_grade`\n"
                    + "LEFT JOIN `tb_tarif_cabut` ON `tb_laporan_produksi`.`jenis_bulu_lp` = `tb_tarif_cabut`.`bulu_upah`\n"
                    + "LEFT JOIN `tb_karyawan` ON `tb_cetak`.`cetak_dikerjakan` = `tb_karyawan`.`id_pegawai`\n"
                    + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`\n"
                    + "WHERE \n"
                    + "`tb_karyawan`.`nama_pegawai` LIKE '%" + txt_search_nama_cetak.getText() + "%' \n"
                    + "AND `tgl_selesai_cetak` BETWEEN '" + dateFormat.format(DateFilter_SetorCetak1.getDate()) + "' AND '" + dateFormat.format(DateFilter_SetorCetak2.getDate()) + "'\n"
                    + "AND `cetak_dikerjakan` IS NOT NULL \n"
                    + "AND `tgl_selesai_cetak` IS NOT NULL \n"
                    + "AND (`cetak_dikerjakan1`<>`cetak_dikerjakan` OR `cetak_dikerjakan1` IS NULL) \n"
                    + "GROUP BY `cetak_dikerjakan` \n"
                    + ""
                    + "UNION ALL \n"
                    + ""
                    + "SELECT `cetak_dikerjakan`, `tb_karyawan`.`nama_pegawai`, `tb_bagian`.`nama_bagian`, \n"
                    + "(SELECT `cetak_dikerjakan_level` FROM `tb_cetak` CTK WHERE `tgl_selesai_cetak` BETWEEN '" + dateFormat.format(DateFilter_SetorCetak1.getDate()) + "' AND '" + dateFormat.format(DateFilter_SetorCetak2.getDate()) + "' "
                    + "AND `cetak_dikerjakan` = `tb_cetak`.`cetak_dikerjakan` ORDER BY `tgl_selesai_cetak` DESC LIMIT 1) AS 'cetak_dikerjakan_level', \n"
                    + "SUM(IF((`cetak_mangkok`+`cetak_pecah`+`cetak_flat`)=0,`cetak_jidun_real` / `kpg_lp_jidun`, `keping_upah` / IF(`tb_grade_bahan_baku`.`kategori`='FLAT',`kpg_lp_flat`, `kpg_lp`))) AS 'Jumlah_LP', \n"
                    + "SUM(`tb_laporan_produksi`.`keping_upah`) AS 'total_kpg', "
                    + "SUM(`tb_laporan_produksi`.`berat_basah`) AS 'total_gram', \n"
                    + "SUM(`cetak_mangkok` * `ctk_mku` * IF(LOCATE('JUMBO', `tb_laporan_produksi`.`kode_grade`)>0 OR LOCATE('BPU', `tb_laporan_produksi`.`kode_grade`)>0, 1.05, 1)) AS 'mku', \n"
                    + "SUM(`cetak_pecah` * `ctk_mpch` * IF(LOCATE('JUMBO', `tb_laporan_produksi`.`kode_grade`)>0 OR LOCATE('BPU', `tb_laporan_produksi`.`kode_grade`)>0, 1.05, 1)) AS 'mpch', \n"
                    + "SUM(`cetak_flat` * `ctk_flat` * IF(LOCATE('JUMBO', `tb_laporan_produksi`.`kode_grade`)>0 OR LOCATE('BPU', `tb_laporan_produksi`.`kode_grade`)>0, 1.05, 1)) AS 'flat', \n"
                    + "SUM(`cetak_jidun_real` * `ctk_jdn`) AS 'jdn', \n"
                    + "0 AS 'bobot_bonus_lp_t1', \n"
                    + "0 AS 'bobot_bonus_lp_t2', \n"
                    + "SUM(IF(DATEDIFF(`tgl_selesai_cetak`, `tgl_mulai_cetak`) <= " + max_lama_inap_t12 + " AND ((`cetak_mangkok`/`keping_upah`)*100)>=`target_ctk_mku`, (IF((`cetak_mangkok`+`cetak_pecah`+`cetak_flat`)=0,`cetak_jidun_real` / `kpg_lp_jidun`, `keping_upah` / IF(`tb_grade_bahan_baku`.`kategori`='FLAT',`kpg_lp_flat`, `kpg_lp`))), 0)) AS 'bobot_bonus_lp_t12' \n"
                    + "FROM `tb_cetak` \n"
                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_cetak`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_grade_bahan_baku` ON `tb_laporan_produksi`.`kode_grade` = `tb_grade_bahan_baku`.`kode_grade`\n"
                    + "LEFT JOIN `tb_tarif_cabut` ON `tb_laporan_produksi`.`jenis_bulu_lp` = `tb_tarif_cabut`.`bulu_upah`\n"
                    + "LEFT JOIN `tb_karyawan` ON `tb_cetak`.`cetak_dikerjakan` = `tb_karyawan`.`id_pegawai`\n"
                    + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`\n"
                    + "WHERE \n"
                    + "`tb_karyawan`.`nama_pegawai` LIKE '%" + txt_search_nama_cetak.getText() + "%' \n"
                    + "AND `tgl_selesai_cetak` BETWEEN '" + dateFormat.format(DateFilter_SetorCetak1.getDate()) + "' AND '" + dateFormat.format(DateFilter_SetorCetak2.getDate()) + "'\n"
                    + "AND `cetak_dikerjakan` IS NOT NULL \n"
                    + "AND `tgl_selesai_cetak` IS NOT NULL \n"
                    + "AND `cetak_dikerjakan1` IS NOT NULL \n"
                    + "AND `cetak_dikerjakan1`=`cetak_dikerjakan` \n"
                    + "AND `tgl_cetak_dikerjakan1` IS NULL \n"
                    + "GROUP BY `cetak_dikerjakan` \n"
                    + ""
                    + ") DATA \n"
                    + "WHERE `nama_bagian` LIKE '%" + txt_search_bagian.getText() + "%'"
                    + "GROUP BY `cetak_dikerjakan` "
                    + "ORDER BY `nama_bagian`, `nama_pegawai`";
            PreparedStatement pst = Utility.db.getConnection().prepareStatement(sql);
            rs = pst.executeQuery();
            Object[] row = new Object[14];
            while (rs.next()) {
                float gaji_reproses = 0, kpg_reproses = 0;
                String query = "SELECT (`mk` + `pch` + `flat` + `jdn`) AS 'kpg_reproses', "
                        + "((`mk` * " + upah_reproses_utuh + ") + \n"
                        + "(`pch` * " + upah_reproses_pch + ") + \n"
                        + "(`flat` * " + upah_reproses_flat + ") + \n"
                        + "(`jdn` * " + upah_reproses_jdn + ")) AS 'gaji' \n"
                        + "FROM `tb_reproses` \n"
                        + "LEFT JOIN `tb_box_bahan_jadi` ON `tb_reproses`.`no_box` = `tb_box_bahan_jadi`.`no_box`\n"
                        + "WHERE `pekerja_cetak` = '" + rs.getString("cetak_dikerjakan") + "' \n"
                        + "AND `tgl_cetak` BETWEEN '" + dateFormat.format(DateFilter_SetorCetak1.getDate()) + "' AND '" + dateFormat.format(DateFilter_SetorCetak2.getDate()) + "'\n";
                ResultSet result = Utility.db.getStatement().executeQuery(query);
                while (result.next()) {
                    kpg_reproses = kpg_reproses + result.getFloat("kpg_reproses");
                    gaji_reproses = gaji_reproses + result.getFloat("gaji");
                }
                nomor++;
                row[0] = nomor;
                row[1] = rs.getString("cetak_dikerjakan");
                row[2] = rs.getString("nama_pegawai");
                row[3] = rs.getString("nama_bagian");
                float total_bobot_lp_bonus = rs.getFloat("bobot_bonus_lp_t1") + rs.getFloat("bobot_bonus_lp_t2") + rs.getFloat("bobot_bonus_lp_t12");
                row[4] = Math.round(total_bobot_lp_bonus * 100.f) / 100.f;
                row[5] = rs.getFloat("total_kpg");
                total_kpg = total_kpg + rs.getInt("total_kpg");
                row[6] = rs.getFloat("total_gram");
                total_gram = total_gram + rs.getInt("total_gram");
                row[7] = kpg_reproses;
                float gaji = rs.getFloat("mku") + rs.getFloat("mpch") + rs.getFloat("flat") + rs.getFloat("jdn");
                if (CheckBox_BorongOnly.isSelected() && (rs.getString("cetak_dikerjakan_level") == null || !rs.getString("cetak_dikerjakan_level").equals("MANDIRI CTK BRG"))) {
                    gaji = 0;
                    gaji_reproses = 0;
                }
                row[8] = gaji + gaji_reproses;
                total_gaji = total_gaji + gaji + gaji_reproses;

                if (total_bobot_lp_bonus < min_lp_dikerjakan_float) {
                    row[9] = 0;
                    row[10] = 0;
                    row[11] = 0;
                    row[12] = 0;
                } else {
                    int bonus_per_lp_cetak1 = 0;
                    int bonus_per_lp_cetak2 = 0;
                    int bonus_per_lp_cetak12 = 0;
                    if (rs.getString("cetak_dikerjakan_level") != null && rs.getString("cetak_dikerjakan_level").toLowerCase().contains("brg")) {
                        bonus_per_lp_cetak1 = Integer.valueOf(txt_bonus_per_lp_borong_cetak1.getText());
                        bonus_per_lp_cetak2 = Integer.valueOf(txt_bonus_per_lp_borong_cetak2.getText());
                        bonus_per_lp_cetak12 = Integer.valueOf(txt_bonus_per_lp_borong_cetak12.getText());
                    } else if ((rs.getString("cetak_dikerjakan_level") != null && rs.getString("cetak_dikerjakan_level").toLowerCase().contains("training"))
                            || rs.getString("nama_bagian").toLowerCase().contains("trainer")
                            || rs.getString("nama_bagian").toLowerCase().contains("pengawas")) {
                        bonus_per_lp_cetak1 = 0;
                        bonus_per_lp_cetak2 = 0;
                        bonus_per_lp_cetak12 = 0;
                    } else {
                        bonus_per_lp_cetak1 = Integer.valueOf(txt_bonus_per_lp_harian_cetak1.getText());
                        bonus_per_lp_cetak2 = Integer.valueOf(txt_bonus_per_lp_harian_cetak2.getText());
                        bonus_per_lp_cetak12 = Integer.valueOf(txt_bonus_per_lp_harian_cetak12.getText());
                    }
                    double bonus_ctk1 = (Math.floor(rs.getDouble("bobot_bonus_lp_t1") / 0.5d) * 0.5d) * bonus_per_lp_cetak1;
                    row[9] = bonus_ctk1;
                    total_bonus = total_bonus + bonus_ctk1;
                    double bonus_ctk2 = (Math.floor(rs.getDouble("bobot_bonus_lp_t2") / 0.5d) * 0.5d) * bonus_per_lp_cetak2;
                    row[10] = bonus_ctk2;
                    total_bonus = total_bonus + bonus_ctk2;
                    double bonus_ctk12 = (Math.floor(rs.getDouble("bobot_bonus_lp_t12") / 0.5d) * 0.5d) * bonus_per_lp_cetak12;
                    row[11] = bonus_ctk12;
                    total_bonus = total_bonus + bonus_ctk12;
                    row[12] = bonus_ctk1 + bonus_ctk2 + bonus_ctk12;
                }
                row[13] = rs.getString("cetak_dikerjakan_level");
                model.addRow(row);
            }

            decimalFormat.setMaximumFractionDigits(0);
            label_total_data_cetak.setText(decimalFormat.format(table_data_pegawai_cetak.getRowCount()));
            label_total_gaji_cetak.setText("Rp. " + decimalFormat.format(total_gaji));
            label_total_bonus_kec_cetak.setText("Rp. " + decimalFormat.format(total_bonus));
            label_total_gram_cetak.setText(decimalFormat.format(total_gram) + " Gr");
            label_total_kpg_cetak.setText(decimalFormat.format(total_kpg) + " Kpg");

            ColumnsAutoSizer.sizeColumnsToFit(table_data_pegawai_cetak);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_GajiCetak_percobaan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_detail_lp_cetak1(String id) {
        try {
            float total_gaji = 0;
            float total_lp = 0;
            decimalFormat.setMaximumFractionDigits(0);
            DefaultTableModel model = (DefaultTableModel) Tabel_detail_LP_cetak1.getModel();
            model.setRowCount(0);

            if (DateFilter_SetorCetak1.getDate() != null && DateFilter_SetorCetak2.getDate() != null) {
                sql = "SELECT `tb_cetak`.`no_laporan_produksi`, `tgl_selesai_cetak`, `tb_laporan_produksi`.`kode_grade`, `jenis_bulu_lp`, `jumlah_keping`, `berat_basah`, `cetak_dikerjakan`, `kpg_lp`, `cetak_mangkok`, `tb_grade_bahan_baku`.`kategori`, "
                        + "IF((`cetak_mangkok`+`cetak_pecah`+`cetak_flat`)=0,`kpg_lp_jidun`, IF(`tb_grade_bahan_baku`.`kategori`='FLAT',`kpg_lp_flat`, `kpg_lp`)) AS 'kpg_lp_besar', "
                        + "DATEDIFF(`tgl_cetak_dikerjakan1`, `tgl_mulai_cetak`) AS 'lama_inap',\n"
                        + "0 AS 'mku', \n"
                        + "0 AS 'mpch', \n"
                        + "0 AS 'flat', \n"
                        + "0 AS 'jdn', \n"
                        + "(IF((`cetak_mangkok`+`cetak_pecah`+`cetak_flat`)=0,`cetak_jidun_real` / `kpg_lp_jidun`, `keping_upah` / IF(`tb_grade_bahan_baku`.`kategori`='FLAT',`kpg_lp_flat`, `kpg_lp`))) AS 'bobot',\n"
                        + "`tb_grade_bahan_baku`.`target_ctk_mku`\n"
                        + "FROM `tb_cetak` \n"
                        + "LEFT JOIN `tb_laporan_produksi` ON `tb_cetak`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                        + "LEFT JOIN `tb_grade_bahan_baku` ON `tb_laporan_produksi`.`kode_grade` = `tb_grade_bahan_baku`.`kode_grade`\n"
                        + "LEFT JOIN `tb_tarif_cabut` ON `tb_laporan_produksi`.`jenis_bulu_lp` = `tb_tarif_cabut`.`bulu_upah`\n"
                        + "WHERE "
                        + "`cetak_dikerjakan1` = '" + id + "' \n"
                        + "AND `tgl_selesai_cetak` BETWEEN '" + dateFormat.format(DateFilter_SetorCetak1.getDate()) + "' AND '" + dateFormat.format(DateFilter_SetorCetak2.getDate()) + "'\n"
                        + "AND `cetak_dikerjakan` IS NOT NULL \n"
                        + "AND `tgl_selesai_cetak` IS NOT NULL \n"
                        + "AND `cetak_dikerjakan1` IS NOT NULL \n"
                        + "AND `tgl_cetak_dikerjakan1` IS NOT NULL \n";
            }
//            System.out.println(sql);
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[13];
            while (rs.next()) {
                row[0] = rs.getString("no_laporan_produksi");
                row[1] = rs.getString("tgl_selesai_cetak");
                row[2] = rs.getString("kode_grade");
                row[3] = rs.getString("jenis_bulu_lp");
                row[4] = rs.getInt("jumlah_keping");
                row[5] = rs.getInt("berat_basah");
                float gaji = rs.getFloat("mku") + rs.getFloat("mpch") + rs.getFloat("flat") + rs.getFloat("jdn");
                row[6] = gaji;
                total_gaji = total_gaji + gaji;
                row[7] = rs.getInt("lama_inap");
                row[8] = Math.round(rs.getFloat("bobot") * 100.f) / 100.f;
                total_lp = total_lp + (Math.round(rs.getFloat("bobot") * 100.f) / 100.f);
                row[9] = rs.getInt("target_ctk_mku");
                row[10] = Math.round((rs.getFloat("cetak_mangkok") / rs.getFloat("jumlah_keping")) * 1000.f) / 10.f;
                row[11] = rs.getString("kategori");
                row[12] = rs.getInt("kpg_lp_besar");
                model.addRow(row);
            }
            label_total_gaji_detail_lp1.setText("Rp. " + decimalFormat.format(total_gaji));
            label_total_detail_lp1.setText(Float.toString(Math.round(total_lp * 100.f) / 100.f));

            ColumnsAutoSizer.sizeColumnsToFit(Tabel_detail_LP_cetak1);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_GajiCetak_percobaan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_detail_lp_cetak2(String id) {
        try {
            float total_gaji = 0;
            float total_lp = 0;
            decimalFormat.setMaximumFractionDigits(0);
            DefaultTableModel model = (DefaultTableModel) Tabel_detail_LP_cetak2.getModel();
            model.setRowCount(0);

            if (DateFilter_SetorCetak1.getDate() != null && DateFilter_SetorCetak2.getDate() != null) {
                sql = "SELECT `tb_cetak`.`no_laporan_produksi`, `tgl_selesai_cetak`, `tb_laporan_produksi`.`kode_grade`, `jenis_bulu_lp`, `jumlah_keping`, `berat_basah`, `cetak_dikerjakan`, `kpg_lp`, `cetak_mangkok`, `tb_grade_bahan_baku`.`kategori`, "
                        + "IF((`cetak_mangkok`+`cetak_pecah`+`cetak_flat`)=0,`kpg_lp_jidun`, IF(`tb_grade_bahan_baku`.`kategori`='FLAT',`kpg_lp_flat`, `kpg_lp`)) AS 'kpg_lp_besar', "
                        + "DATEDIFF(`tgl_selesai_cetak`, IF(`tgl_cetak_dikerjakan1` IS NULL, `tgl_mulai_cetak`, `tgl_cetak_dikerjakan1`)) AS 'lama_inap',\n"
                        + "(`cetak_mangkok` * " + upah_cetak_t2_utuh + " * IF(LOCATE('JUMBO', `tb_laporan_produksi`.`kode_grade`)>0 OR LOCATE('BPU', `tb_laporan_produksi`.`kode_grade`)>0, 1.05, 1)) AS 'mku', \n"
                        + "(`cetak_pecah` * " + upah_cetak_t2_pch + " * IF(LOCATE('JUMBO', `tb_laporan_produksi`.`kode_grade`)>0 OR LOCATE('BPU', `tb_laporan_produksi`.`kode_grade`)>0, 1.05, 1)) AS 'mpch', \n"
                        + "(`cetak_flat` * " + upah_cetak_t2_flat + " * IF(LOCATE('JUMBO', `tb_laporan_produksi`.`kode_grade`)>0 OR LOCATE('BPU', `tb_laporan_produksi`.`kode_grade`)>0, 1.05, 1)) AS 'flat', \n"
                        + "(`cetak_jidun_real` * " + upah_cetak_t2_jdn + ") AS 'jdn', \n"
                        + "(IF((`cetak_mangkok`+`cetak_pecah`+`cetak_flat`)=0,`cetak_jidun_real` / `kpg_lp_jidun`, `keping_upah` / IF(`tb_grade_bahan_baku`.`kategori`='FLAT',`kpg_lp_flat`, `kpg_lp`))) AS 'bobot',\n"
                        + "`tb_grade_bahan_baku`.`target_ctk_mku`\n"
                        + "FROM `tb_cetak` \n"
                        + "LEFT JOIN `tb_laporan_produksi` ON `tb_cetak`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                        + "LEFT JOIN `tb_grade_bahan_baku` ON `tb_laporan_produksi`.`kode_grade` = `tb_grade_bahan_baku`.`kode_grade`\n"
                        + "LEFT JOIN `tb_tarif_cabut` ON `tb_laporan_produksi`.`jenis_bulu_lp` = `tb_tarif_cabut`.`bulu_upah`\n"
                        + "WHERE "
                        + "`cetak_dikerjakan` = '" + id + "' \n"
                        + "AND `tgl_selesai_cetak` BETWEEN '" + dateFormat.format(DateFilter_SetorCetak1.getDate()) + "' AND '" + dateFormat.format(DateFilter_SetorCetak2.getDate()) + "'\n"
                        + "AND `cetak_dikerjakan` IS NOT NULL \n"
                        + "AND `tgl_selesai_cetak` IS NOT NULL \n"
                        + "AND (`cetak_dikerjakan1`<>`cetak_dikerjakan` OR `cetak_dikerjakan1` IS NULL) \n";
            }
//            System.out.println(sql);
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[13];
            while (rs.next()) {
                row[0] = rs.getString("no_laporan_produksi");
                row[1] = rs.getString("tgl_selesai_cetak");
                row[2] = rs.getString("kode_grade");
                row[3] = rs.getString("jenis_bulu_lp");
                row[4] = rs.getInt("jumlah_keping");
                row[5] = rs.getInt("berat_basah");
                float gaji = rs.getFloat("mku") + rs.getFloat("mpch") + rs.getFloat("flat") + rs.getFloat("jdn");
                row[6] = gaji;
                total_gaji = total_gaji + gaji;
                row[7] = rs.getInt("lama_inap");
                row[8] = Math.round(rs.getFloat("bobot") * 100.f) / 100.f;
                total_lp = total_lp + (Math.round(rs.getFloat("bobot") * 100.f) / 100.f);
                row[9] = rs.getInt("target_ctk_mku");
                row[10] = Math.round((rs.getFloat("cetak_mangkok") / rs.getFloat("jumlah_keping")) * 1000.f) / 10.f;
                row[11] = rs.getString("kategori");
                row[12] = rs.getInt("kpg_lp_besar");
                model.addRow(row);
            }
            label_total_gaji_detail_lp2.setText("Rp. " + decimalFormat.format(total_gaji));
            label_total_detail_lp2.setText(Float.toString(Math.round(total_lp * 100.f) / 100.f));

            ColumnsAutoSizer.sizeColumnsToFit(Tabel_detail_LP_cetak2);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_GajiCetak_percobaan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_detail_lp_cetak3(String id) {
        try {
            float total_gaji = 0;
            float total_lp = 0;
            decimalFormat.setMaximumFractionDigits(0);
            DefaultTableModel model = (DefaultTableModel) Tabel_detail_LP_cetak3.getModel();
            model.setRowCount(0);

            if (DateFilter_SetorCetak1.getDate() != null && DateFilter_SetorCetak2.getDate() != null) {
                sql = "SELECT `tb_cetak`.`no_laporan_produksi`, `tgl_selesai_cetak`, `tb_laporan_produksi`.`kode_grade`, `jenis_bulu_lp`, `jumlah_keping`, `berat_basah`, `cetak_dikerjakan`, `kpg_lp`, `cetak_mangkok`, `tb_grade_bahan_baku`.`kategori`, "
                        + "IF((`cetak_mangkok`+`cetak_pecah`+`cetak_flat`)=0,`kpg_lp_jidun`, IF(`tb_grade_bahan_baku`.`kategori`='FLAT',`kpg_lp_flat`, `kpg_lp`)) AS 'kpg_lp_besar', "
                        + "DATEDIFF(`tgl_selesai_cetak`, `tgl_mulai_cetak`) AS 'lama_inap',\n"
                        + "(`cetak_mangkok` * `ctk_mku` * IF(LOCATE('JUMBO', `tb_laporan_produksi`.`kode_grade`)>0 OR LOCATE('BPU', `tb_laporan_produksi`.`kode_grade`)>0, 1.05, 1)) AS 'mku', \n"
                        + "(`cetak_pecah` * `ctk_mpch` * IF(LOCATE('JUMBO', `tb_laporan_produksi`.`kode_grade`)>0 OR LOCATE('BPU', `tb_laporan_produksi`.`kode_grade`)>0, 1.05, 1)) AS 'mpch', \n"
                        + "(`cetak_flat` * `ctk_flat` * IF(LOCATE('JUMBO', `tb_laporan_produksi`.`kode_grade`)>0 OR LOCATE('BPU', `tb_laporan_produksi`.`kode_grade`)>0, 1.05, 1)) AS 'flat', \n"
                        + "(`cetak_jidun_real` * `ctk_jdn`) AS 'jdn', \n"
                        + "(IF((`cetak_mangkok`+`cetak_pecah`+`cetak_flat`)=0,`cetak_jidun_real` / `kpg_lp_jidun`, `keping_upah` / IF(`tb_grade_bahan_baku`.`kategori`='FLAT',`kpg_lp_flat`, `kpg_lp`))) * 2 AS 'bobot',\n"
                        + "`tb_grade_bahan_baku`.`target_ctk_mku`\n"
                        + "FROM `tb_cetak` \n"
                        + "LEFT JOIN `tb_laporan_produksi` ON `tb_cetak`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                        + "LEFT JOIN `tb_grade_bahan_baku` ON `tb_laporan_produksi`.`kode_grade` = `tb_grade_bahan_baku`.`kode_grade`\n"
                        + "LEFT JOIN `tb_tarif_cabut` ON `tb_laporan_produksi`.`jenis_bulu_lp` = `tb_tarif_cabut`.`bulu_upah`\n"
                        + "WHERE "
                        + "`cetak_dikerjakan` = '" + id + "' \n"
                        + "AND `tgl_selesai_cetak` BETWEEN '" + dateFormat.format(DateFilter_SetorCetak1.getDate()) + "' AND '" + dateFormat.format(DateFilter_SetorCetak2.getDate()) + "'\n"
                        + "AND `cetak_dikerjakan` IS NOT NULL \n"
                        + "AND `tgl_selesai_cetak` IS NOT NULL \n"
                        + "AND `cetak_dikerjakan1` IS NOT NULL \n"
                        + "AND `cetak_dikerjakan1`=`cetak_dikerjakan` \n"
                        + "AND `tgl_cetak_dikerjakan1` IS NULL \n";
            }
//            System.out.println(sql);
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[13];
            while (rs.next()) {
                row[0] = rs.getString("no_laporan_produksi");
                row[1] = rs.getString("tgl_selesai_cetak");
                row[2] = rs.getString("kode_grade");
                row[3] = rs.getString("jenis_bulu_lp");
                row[4] = rs.getInt("jumlah_keping");
                row[5] = rs.getInt("berat_basah");
                float gaji = rs.getFloat("mku") + rs.getFloat("mpch") + rs.getFloat("flat") + rs.getFloat("jdn");
                row[6] = gaji;
                total_gaji = total_gaji + gaji;
                row[7] = rs.getInt("lama_inap");
                row[8] = Math.round(rs.getFloat("bobot") * 100.f) / 100.f;
                total_lp = total_lp + (Math.round(rs.getFloat("bobot") * 100.f) / 100.f);
                row[9] = rs.getInt("target_ctk_mku");
                row[10] = Math.round((rs.getFloat("cetak_mangkok") / rs.getFloat("jumlah_keping")) * 1000.f) / 10.f;
                row[11] = rs.getString("kategori");
                row[12] = rs.getInt("kpg_lp_besar");
                model.addRow(row);
            }
            label_total_gaji_detail_lp3.setText("Rp. " + decimalFormat.format(total_gaji));
            label_total_detail_lp3.setText(Float.toString(Math.round(total_lp * 100.f) / 100.f));

            ColumnsAutoSizer.sizeColumnsToFit(Tabel_detail_LP_cetak3);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_GajiCetak_percobaan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_detail_box_reproses(String id) {
        try {
            float total_gaji = 0;
            decimalFormat.setMaximumFractionDigits(0);
            DefaultTableModel model = (DefaultTableModel) Tabel_detail_Box_reproses.getModel();
            model.setRowCount(0);

            if (DateFilter_SetorCetak1.getDate() != null && DateFilter_SetorCetak2.getDate() != null) {
                sql = "SELECT `tb_reproses`.`no_box`, `tb_grade_bahan_jadi`.`kode_grade`, `tgl_cetak`, `tb_reproses`.`keping`, `tb_reproses`.`gram`, "
                        + "(`mk` * " + upah_reproses_utuh + ") AS 'mku', \n"
                        + "(`pch` * " + upah_reproses_pch + ") AS 'mpch', \n"
                        + "(`flat` * " + upah_reproses_flat + ") AS 'flat', \n"
                        + "(`jdn` * " + upah_reproses_jdn + ") AS 'jdn' \n"
                        + "FROM `tb_reproses` \n"
                        + "LEFT JOIN `tb_box_bahan_jadi` ON `tb_reproses`.`no_box` = `tb_box_bahan_jadi`.`no_box`\n"
                        + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_grade_bahan_jadi`.`kode` = `tb_box_bahan_jadi`.`kode_grade_bahan_jadi`\n"
                        + "WHERE `pekerja_cetak` = '" + id + "' \n"
                        + "AND `tgl_cetak` BETWEEN '" + dateFormat.format(DateFilter_SetorCetak1.getDate()) + "' AND '" + dateFormat.format(DateFilter_SetorCetak2.getDate()) + "'\n";
            }
//            System.out.println(sql);
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[10];
            while (rs.next()) {
                row[0] = rs.getString("no_box");
                row[1] = rs.getString("tgl_cetak");
                row[2] = rs.getString("kode_grade");
                row[3] = rs.getFloat("keping");
                row[4] = rs.getFloat("gram");
                row[5] = rs.getFloat("mku");
                row[6] = rs.getFloat("mpch");
                row[7] = rs.getFloat("flat");
                row[8] = rs.getFloat("jdn");
                double total = rs.getFloat("mku") + rs.getFloat("mpch") + rs.getFloat("flat") + rs.getFloat("jdn");
                row[9] = total;
                float gaji = rs.getFloat("mku") + rs.getFloat("mpch") + rs.getFloat("flat") + rs.getFloat("jdn");
                total_gaji = total_gaji + gaji;
                model.addRow(row);
            }

            label_total_detail_reproses.setText(Integer.toString(Tabel_detail_Box_reproses.getRowCount()));
            label_total_gaji_detail_reproses.setText("Rp. " + decimalFormat.format(total_gaji));

            ColumnsAutoSizer.sizeColumnsToFit(Tabel_detail_Box_reproses);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_GajiCetak_percobaan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        txt_search_nama_cetak = new javax.swing.JTextField();
        button_search = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        DateFilter_SetorCetak1 = new com.toedter.calendar.JDateChooser();
        DateFilter_SetorCetak2 = new com.toedter.calendar.JDateChooser();
        jLabel6 = new javax.swing.JLabel();
        label_total_data_cetak = new javax.swing.JLabel();
        button_export = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        label_total_gaji_cetak = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        table_data_pegawai_cetak = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txt_minimal_lp_dikerjakan = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txt_max_lama_inap_t1 = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        txt_bonus_per_lp_borong_cetak2 = new javax.swing.JTextField();
        txt_bonus_per_lp_harian_cetak2 = new javax.swing.JTextField();
        txt_search_bagian = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        CheckBox_BorongOnly = new javax.swing.JCheckBox();
        jLabel23 = new javax.swing.JLabel();
        label_total_bonus_kec_cetak = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        label_total_gram_cetak = new javax.swing.JLabel();
        label_total_kpg_cetak = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        txt_upah_reproses = new javax.swing.JTextField();
        jLabel32 = new javax.swing.JLabel();
        txt_hari_kerja = new javax.swing.JTextField();
        jLabel33 = new javax.swing.JLabel();
        txt_target_lp_cetak = new javax.swing.JTextField();
        jLabel36 = new javax.swing.JLabel();
        txt_kenaikan_jumbo = new javax.swing.JTextField();
        button_saveDataCetak = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel59 = new javax.swing.JLabel();
        jLabel60 = new javax.swing.JLabel();
        txt_bonus_per_lp_borong_cetak1 = new javax.swing.JTextField();
        txt_bonus_per_lp_harian_cetak1 = new javax.swing.JTextField();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel10 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        label_nama_detail_lp1 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        Tabel_detail_LP_cetak1 = new javax.swing.JTable();
        label_total_detail_lp1 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        label_total_gaji_detail_lp1 = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        jLabel61 = new javax.swing.JLabel();
        label_nama_detail_lp2 = new javax.swing.JLabel();
        jScrollPane11 = new javax.swing.JScrollPane();
        Tabel_detail_LP_cetak2 = new javax.swing.JTable();
        label_total_detail_lp2 = new javax.swing.JLabel();
        jLabel62 = new javax.swing.JLabel();
        jLabel63 = new javax.swing.JLabel();
        label_total_gaji_detail_lp2 = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        jLabel64 = new javax.swing.JLabel();
        label_nama_detail_lp3 = new javax.swing.JLabel();
        jScrollPane12 = new javax.swing.JScrollPane();
        Tabel_detail_LP_cetak3 = new javax.swing.JTable();
        label_total_detail_lp3 = new javax.swing.JLabel();
        jLabel65 = new javax.swing.JLabel();
        jLabel66 = new javax.swing.JLabel();
        label_total_gaji_detail_lp3 = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        label_total_gaji_detail_reproses = new javax.swing.JLabel();
        label_total_detail_reproses = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        Tabel_detail_Box_reproses = new javax.swing.JTable();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        txt_max_lama_inap_t2 = new javax.swing.JTextField();
        jLabel34 = new javax.swing.JLabel();
        txt_upah_cetak_t2 = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        txt_bonus_per_lp_borong_cetak12 = new javax.swing.JTextField();
        txt_bonus_per_lp_harian_cetak12 = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        txt_max_lama_inap_t12 = new javax.swing.JTextField();

        setBackground(new java.awt.Color(255, 255, 255));

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        txt_search_nama_cetak.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_nama_cetak.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_nama_cetakKeyPressed(evt);
            }
        });

        button_search.setBackground(new java.awt.Color(255, 255, 255));
        button_search.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search.setText("Refresh");
        button_search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_searchActionPerformed(evt);
            }
        });

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("Nama :");

        DateFilter_SetorCetak1.setBackground(new java.awt.Color(255, 255, 255));
        DateFilter_SetorCetak1.setDateFormatString("dd MMMM yyyy");
        DateFilter_SetorCetak1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        DateFilter_SetorCetak2.setBackground(new java.awt.Color(255, 255, 255));
        DateFilter_SetorCetak2.setDateFormatString("dd MMMM yyyy");
        DateFilter_SetorCetak2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel6.setText("Total Data :");

        label_total_data_cetak.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_cetak.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_data_cetak.setText("TOTAL");

        button_export.setBackground(new java.awt.Color(255, 255, 255));
        button_export.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export.setText("Export to Excel");
        button_export.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_exportActionPerformed(evt);
            }
        });

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel7.setText("Total Gaji :");

        label_total_gaji_cetak.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gaji_cetak.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_gaji_cetak.setText("TOTAL");

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel5.setText("Tgl Setor Cetak :");

        table_data_pegawai_cetak.setAutoCreateRowSorter(true);
        table_data_pegawai_cetak.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "ID Pegawai", "Nama", "Bagian", "Tot LP", "Kpg Upah", "Gram", "Kpg Rprs", "Gaji", "Bonus T1", "Bonus T2", "Bonus T12", "Total Bonus", "Level"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table_data_pegawai_cetak.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(table_data_pegawai_cetak);

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("Bonus Kecepatan / LP Harian (Rp.) Cetak 2 :");

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel4.setText("Target LP dikerjakan (T1 + T2) :");

        txt_minimal_lp_dikerjakan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_minimal_lp_dikerjakan.setText("8");
        txt_minimal_lp_dikerjakan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_minimal_lp_dikerjakanKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_minimal_lp_dikerjakanKeyTyped(evt);
            }
        });

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel9.setText("Max lama inap (hari) T1 :");

        txt_max_lama_inap_t1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_max_lama_inap_t1.setText("2");

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel12.setText("Bonus Kecepatan / LP Borong (Rp.) Cetak 2 :");

        txt_bonus_per_lp_borong_cetak2.setEditable(false);
        txt_bonus_per_lp_borong_cetak2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_bonus_per_lp_borong_cetak2.setText("4000");
        txt_bonus_per_lp_borong_cetak2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_bonus_per_lp_borong_cetak2KeyTyped(evt);
            }
        });

        txt_bonus_per_lp_harian_cetak2.setEditable(false);
        txt_bonus_per_lp_harian_cetak2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_bonus_per_lp_harian_cetak2.setText("3000");
        txt_bonus_per_lp_harian_cetak2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_bonus_per_lp_harian_cetak2KeyTyped(evt);
            }
        });

        txt_search_bagian.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_bagian.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_bagianKeyPressed(evt);
            }
        });

        jLabel19.setBackground(new java.awt.Color(255, 255, 255));
        jLabel19.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel19.setText("Bagian :");

        CheckBox_BorongOnly.setBackground(new java.awt.Color(255, 255, 255));
        CheckBox_BorongOnly.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        CheckBox_BorongOnly.setSelected(true);
        CheckBox_BorongOnly.setText("Borong Only");

        jLabel23.setBackground(new java.awt.Color(255, 255, 255));
        jLabel23.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel23.setText("Total Bonus Kec :");

        label_total_bonus_kec_cetak.setBackground(new java.awt.Color(255, 255, 255));
        label_total_bonus_kec_cetak.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_bonus_kec_cetak.setText("TOTAL");

        jLabel24.setBackground(new java.awt.Color(255, 255, 255));
        jLabel24.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel24.setText("Total Gram :");

        label_total_gram_cetak.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_cetak.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_gram_cetak.setText("TOTAL");

        label_total_kpg_cetak.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kpg_cetak.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_kpg_cetak.setText("TOTAL");

        jLabel27.setBackground(new java.awt.Color(255, 255, 255));
        jLabel27.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel27.setText("Total Kpg :");

        jLabel31.setBackground(new java.awt.Color(255, 255, 255));
        jLabel31.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel31.setText("Upah Reproses utuh :");

        txt_upah_reproses.setEditable(false);
        txt_upah_reproses.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_upah_reproses.setText("650");
        txt_upah_reproses.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_upah_reprosesKeyTyped(evt);
            }
        });

        jLabel32.setBackground(new java.awt.Color(255, 255, 255));
        jLabel32.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel32.setText("Hari Kerja :");

        txt_hari_kerja.setEditable(false);
        txt_hari_kerja.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel33.setBackground(new java.awt.Color(255, 255, 255));
        jLabel33.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel33.setText("Target LP dikerjakan (T1 + T2) :");

        txt_target_lp_cetak.setEditable(false);
        txt_target_lp_cetak.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel36.setBackground(new java.awt.Color(255, 255, 255));
        jLabel36.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel36.setText("Grade JUMBO, BPU naik :");

        txt_kenaikan_jumbo.setEditable(false);
        txt_kenaikan_jumbo.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_kenaikan_jumbo.setText("5%");

        button_saveDataCetak.setBackground(new java.awt.Color(255, 255, 255));
        button_saveDataCetak.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_saveDataCetak.setText("Save Data");
        button_saveDataCetak.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_saveDataCetakActionPerformed(evt);
            }
        });

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setText("Notes : \n1. Bonus kecepatan diberikan hanya untuk grup Mandiri / Borong, jika LP yang dikerjakan mencapai target LP\n2. Trainer / Asisten Pengawas tidak dapat bonus kecepatan\n3. Jika level gaji = Training = tidak dapat bonus kecepatan\n4. bobot LP dibulatkan ke bawah kelipatan 0.5.\n5. Gaji Borong hanya untuk level \"MANDIRI CTK BRG\".\n6. Lama inap T1 = tgl masuk cetak - tgl t1\n7. Lama inap T2 = tgl t1 - tgl setor cetak. kalau tgl t1 kosong, maka tgl masuk - tgl setor\n8. Lama inap T12 = tgl masuk - tgl setor");
        jScrollPane1.setViewportView(jTextArea1);

        jLabel59.setBackground(new java.awt.Color(255, 255, 255));
        jLabel59.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel59.setText("Bonus Kecepatan / LP Harian (Rp.) Cetak 1 :");

        jLabel60.setBackground(new java.awt.Color(255, 255, 255));
        jLabel60.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel60.setText("Bonus Kecepatan / LP Borong (Rp.) Cetak 1 :");

        txt_bonus_per_lp_borong_cetak1.setEditable(false);
        txt_bonus_per_lp_borong_cetak1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_bonus_per_lp_borong_cetak1.setText("4000");
        txt_bonus_per_lp_borong_cetak1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_bonus_per_lp_borong_cetak1KeyTyped(evt);
            }
        });

        txt_bonus_per_lp_harian_cetak1.setEditable(false);
        txt_bonus_per_lp_harian_cetak1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_bonus_per_lp_harian_cetak1.setText("3000");
        txt_bonus_per_lp_harian_cetak1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_bonus_per_lp_harian_cetak1KeyTyped(evt);
            }
        });

        jTabbedPane2.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jPanel10.setBackground(new java.awt.Color(255, 255, 255));

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel10.setText("Total Upah Borong :");

        label_nama_detail_lp1.setBackground(new java.awt.Color(255, 255, 255));
        label_nama_detail_lp1.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_nama_detail_lp1.setText("Nama");

        Tabel_detail_LP_cetak1.setAutoCreateRowSorter(true);
        Tabel_detail_LP_cetak1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Tabel_detail_LP_cetak1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No LP", "Setor Cetak", "Grade", "Bulu", "Kpg", "Gram", "Total", "Hari", "Bobot LP", "T. MKU", "MKU %", "Kategori", "Kpg LP Bsr"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Integer.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.String.class, java.lang.Integer.class
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
        Tabel_detail_LP_cetak1.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(Tabel_detail_LP_cetak1);

        label_total_detail_lp1.setBackground(new java.awt.Color(255, 255, 255));
        label_total_detail_lp1.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_detail_lp1.setText("0");

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel11.setText("Total LP :");

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel8.setText("Di kerjakan Oleh :");

        label_total_gaji_detail_lp1.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gaji_detail_lp1.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_gaji_detail_lp1.setText("0");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_nama_detail_lp1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_detail_lp1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gaji_detail_lp1))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 476, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_nama_detail_lp1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_gaji_detail_lp1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_detail_lp1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 594, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane2.addTab("Cetak 1", jPanel10);

        jPanel12.setBackground(new java.awt.Color(255, 255, 255));

        jLabel61.setBackground(new java.awt.Color(255, 255, 255));
        jLabel61.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel61.setText("Total Upah Borong :");

        label_nama_detail_lp2.setBackground(new java.awt.Color(255, 255, 255));
        label_nama_detail_lp2.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_nama_detail_lp2.setText("Nama");

        Tabel_detail_LP_cetak2.setAutoCreateRowSorter(true);
        Tabel_detail_LP_cetak2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Tabel_detail_LP_cetak2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No LP", "Setor Cetak", "Grade", "Bulu", "Kpg", "Gram", "Total", "Hari", "Bobot LP", "T. MKU", "MKU %", "Kategori", "Kpg LP Bsr"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Integer.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.String.class, java.lang.Integer.class
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
        Tabel_detail_LP_cetak2.getTableHeader().setReorderingAllowed(false);
        jScrollPane11.setViewportView(Tabel_detail_LP_cetak2);

        label_total_detail_lp2.setBackground(new java.awt.Color(255, 255, 255));
        label_total_detail_lp2.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_detail_lp2.setText("0");

        jLabel62.setBackground(new java.awt.Color(255, 255, 255));
        jLabel62.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel62.setText("Total LP :");

        jLabel63.setBackground(new java.awt.Color(255, 255, 255));
        jLabel63.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel63.setText("Di kerjakan Oleh :");

        label_total_gaji_detail_lp2.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gaji_detail_lp2.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_gaji_detail_lp2.setText("0");

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(jLabel63)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_nama_detail_lp2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel62)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_detail_lp2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel61)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gaji_detail_lp2))
                    .addComponent(jScrollPane11, javax.swing.GroupLayout.DEFAULT_SIZE, 476, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel63, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_nama_detail_lp2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel61, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_gaji_detail_lp2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel62, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_detail_lp2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane11, javax.swing.GroupLayout.DEFAULT_SIZE, 594, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane2.addTab("Cetak 2", jPanel12);

        jPanel13.setBackground(new java.awt.Color(255, 255, 255));

        jLabel64.setBackground(new java.awt.Color(255, 255, 255));
        jLabel64.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel64.setText("Total Upah Borong :");

        label_nama_detail_lp3.setBackground(new java.awt.Color(255, 255, 255));
        label_nama_detail_lp3.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_nama_detail_lp3.setText("Nama");

        Tabel_detail_LP_cetak3.setAutoCreateRowSorter(true);
        Tabel_detail_LP_cetak3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Tabel_detail_LP_cetak3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No LP", "Setor Cetak", "Grade", "Bulu", "Kpg", "Gram", "Total", "Hari", "Bobot LP", "T. MKU", "MKU %", "Kategori", "Kpg LP Bsr"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Integer.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.String.class, java.lang.Integer.class
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
        Tabel_detail_LP_cetak3.getTableHeader().setReorderingAllowed(false);
        jScrollPane12.setViewportView(Tabel_detail_LP_cetak3);

        label_total_detail_lp3.setBackground(new java.awt.Color(255, 255, 255));
        label_total_detail_lp3.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_detail_lp3.setText("0");

        jLabel65.setBackground(new java.awt.Color(255, 255, 255));
        jLabel65.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel65.setText("Total LP :");

        jLabel66.setBackground(new java.awt.Color(255, 255, 255));
        jLabel66.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel66.setText("Di kerjakan Oleh :");

        label_total_gaji_detail_lp3.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gaji_detail_lp3.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_gaji_detail_lp3.setText("0");

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addComponent(jLabel66)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_nama_detail_lp3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel65)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_detail_lp3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel64)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gaji_detail_lp3))
                    .addComponent(jScrollPane12, javax.swing.GroupLayout.DEFAULT_SIZE, 476, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel66, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_nama_detail_lp3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel64, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_gaji_detail_lp3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel65, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_detail_lp3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane12, javax.swing.GroupLayout.DEFAULT_SIZE, 594, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane2.addTab("Cetak 12", jPanel13);

        jPanel11.setBackground(new java.awt.Color(255, 255, 255));

        label_total_gaji_detail_reproses.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gaji_detail_reproses.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_gaji_detail_reproses.setText("0");

        label_total_detail_reproses.setBackground(new java.awt.Color(255, 255, 255));
        label_total_detail_reproses.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_detail_reproses.setText("0");

        jLabel30.setBackground(new java.awt.Color(255, 255, 255));
        jLabel30.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel30.setText("Box Reproses");

        Tabel_detail_Box_reproses.setAutoCreateRowSorter(true);
        Tabel_detail_Box_reproses.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Tabel_detail_Box_reproses.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No Box", "Tgl Cetak", "Grade", "Kpg", "Gram", "MK", "Pch", "Flat", "Jdn", "Total"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Tabel_detail_Box_reproses.getTableHeader().setReorderingAllowed(false);
        jScrollPane6.setViewportView(Tabel_detail_Box_reproses);

        jLabel28.setBackground(new java.awt.Color(255, 255, 255));
        jLabel28.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel28.setText("Total Box :");

        jLabel29.setBackground(new java.awt.Color(255, 255, 255));
        jLabel29.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel29.setText("Total :");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jLabel30)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel28)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_detail_reproses)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel29)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gaji_detail_reproses))
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 476, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_gaji_detail_reproses, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_detail_reproses, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 594, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane2.addTab("Reproses", jPanel11);

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel13.setText("Max lama inap (hari) T2 :");

        txt_max_lama_inap_t2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_max_lama_inap_t2.setText("2");

        jLabel34.setBackground(new java.awt.Color(255, 255, 255));
        jLabel34.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel34.setText("Upah Cetak T2 Utuh :");

        txt_upah_cetak_t2.setEditable(false);
        txt_upah_cetak_t2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_upah_cetak_t2.setText("700");
        txt_upah_cetak_t2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_upah_cetak_t2KeyTyped(evt);
            }
        });

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel14.setText("Bonus Kecepatan / LP Harian (Rp.) Cetak 12 :");

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel15.setText("Bonus Kecepatan / LP Borong (Rp.) Cetak 12 :");

        txt_bonus_per_lp_borong_cetak12.setEditable(false);
        txt_bonus_per_lp_borong_cetak12.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_bonus_per_lp_borong_cetak12.setText("4000");
        txt_bonus_per_lp_borong_cetak12.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_bonus_per_lp_borong_cetak12KeyTyped(evt);
            }
        });

        txt_bonus_per_lp_harian_cetak12.setEditable(false);
        txt_bonus_per_lp_harian_cetak12.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_bonus_per_lp_harian_cetak12.setText("3000");
        txt_bonus_per_lp_harian_cetak12.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_bonus_per_lp_harian_cetak12KeyTyped(evt);
            }
        });

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel16.setText("Max lama inap (hari) T12 :");

        txt_max_lama_inap_t12.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_max_lama_inap_t12.setText("4");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_nama_cetak, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel19)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_bagian, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(DateFilter_SetorCetak1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(DateFilter_SetorCetak2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel32)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_hari_kerja, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel33)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_target_lp_cetak, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel36)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_kenaikan_jumbo, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_saveDataCetak)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_export))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel23, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel24, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel27, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(label_total_kpg_cetak)
                                    .addComponent(label_total_data_cetak)
                                    .addComponent(label_total_gaji_cetak)
                                    .addComponent(label_total_bonus_kec_cetak)
                                    .addComponent(label_total_gram_cetak)))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel60)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_bonus_per_lp_borong_cetak1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel59)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_bonus_per_lp_harian_cetak1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_max_lama_inap_t1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_bonus_per_lp_borong_cetak2, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_bonus_per_lp_harian_cetak2, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_max_lama_inap_t2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_minimal_lp_dikerjakan, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel31)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_upah_reproses, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel34)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_upah_cetak_t2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(CheckBox_BorongOnly)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_search))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_bonus_per_lp_borong_cetak12, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_bonus_per_lp_harian_cetak12, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel16)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_max_lama_inap_t12, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 108, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 544, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane2))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_nama_cetak, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_bagian, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DateFilter_SetorCetak1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DateFilter_SetorCetak2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(button_search, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CheckBox_BorongOnly)
                    .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_upah_reproses, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_upah_cetak_t2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_minimal_lp_dikerjakan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel60, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_bonus_per_lp_borong_cetak1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel59, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_bonus_per_lp_harian_cetak1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_max_lama_inap_t1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_bonus_per_lp_borong_cetak2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_bonus_per_lp_harian_cetak2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_max_lama_inap_t2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_bonus_per_lp_borong_cetak12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_bonus_per_lp_harian_cetak12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_max_lama_inap_t12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_hari_kerja, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_target_lp_cetak, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_kenaikan_jumbo, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_saveDataCetak, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_export, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_total_data_cetak, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_total_gaji_cetak, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_total_bonus_kec_cetak, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_total_gram_cetak, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_total_kpg_cetak, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap())
                    .addComponent(jScrollPane1)))
            .addComponent(jTabbedPane2, javax.swing.GroupLayout.Alignment.TRAILING)
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("PERCOBAAN BONUS CETAK", jPanel1);

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

    private void txt_search_nama_cetakKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_nama_cetakKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (DateFilter_SetorCetak1.getDate() != null && DateFilter_SetorCetak2.getDate() != null) {
                refreshTable_cetak();
            } else {
                JOptionPane.showMessageDialog(this, "Harap masukkan Range Tanggal penggajian");
            }
        }
    }//GEN-LAST:event_txt_search_nama_cetakKeyPressed

    private void button_searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_searchActionPerformed
        if (DateFilter_SetorCetak1.getDate() != null && DateFilter_SetorCetak2.getDate() != null) {
            refreshTable_cetak();
        } else {
            JOptionPane.showMessageDialog(this, "Harap masukkan Range Tanggal penggajian");
        }
    }//GEN-LAST:event_button_searchActionPerformed

    private void button_exportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_exportActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) table_data_pegawai_cetak.getModel();
        ExportToExcel.writeToExcel(model, jPanel2);
    }//GEN-LAST:event_button_exportActionPerformed

    private void txt_minimal_lp_dikerjakanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_minimal_lp_dikerjakanKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (DateFilter_SetorCetak1.getDate() != null && DateFilter_SetorCetak2.getDate() != null) {
                refreshTable_cetak();
            } else {
                JOptionPane.showMessageDialog(this, "Harap masukkan Range Tanggal penggajian");
            }
        }
    }//GEN-LAST:event_txt_minimal_lp_dikerjakanKeyPressed

    private void txt_minimal_lp_dikerjakanKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_minimal_lp_dikerjakanKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_minimal_lp_dikerjakanKeyTyped

    private void txt_bonus_per_lp_borong_cetak2KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_bonus_per_lp_borong_cetak2KeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_bonus_per_lp_borong_cetak2KeyTyped

    private void txt_bonus_per_lp_harian_cetak2KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_bonus_per_lp_harian_cetak2KeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_bonus_per_lp_harian_cetak2KeyTyped

    private void txt_search_bagianKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_bagianKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (DateFilter_SetorCetak1.getDate() != null && DateFilter_SetorCetak2.getDate() != null) {
                refreshTable_cetak();
            } else {
                JOptionPane.showMessageDialog(this, "Harap masukkan Range Tanggal penggajian");
            }
        }
    }//GEN-LAST:event_txt_search_bagianKeyPressed

    private void txt_upah_reprosesKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_upah_reprosesKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_upah_reprosesKeyTyped

    private void button_saveDataCetakActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_saveDataCetakActionPerformed
        // TODO add your handling code here:
        int dialogResult = JOptionPane.showConfirmDialog(this, "Save " + table_data_pegawai_cetak.getRowCount() + " data ?", "Warning", 0);
        if (dialogResult == JOptionPane.YES_OPTION) {
            try {
                Utility.db.getConnection().setAutoCommit(false);
                for (int i = 0; i < table_data_pegawai_cetak.getRowCount(); i++) {
                    String update = "UPDATE `tb_lembur_rekap` SET `gaji_borong`=0, `bonus1_kecepatan`=0 "
                            + "WHERE `id_pegawai`='" + table_data_pegawai_cetak.getValueAt(i, 1).toString() + "' "
                            + "AND `tanggal` BETWEEN '" + dateFormat.format(DateFilter_SetorCetak1.getDate()) + "' AND '" + dateFormat.format(DateFilter_SetorCetak2.getDate()) + "'";
                    Utility.db.getConnection().createStatement();
                    Utility.db.getStatement().executeUpdate(update);
                    String Query = "INSERT INTO `tb_lembur_rekap`(`id_pegawai`, `tanggal`, `gaji_borong`, `bonus1_kecepatan`) "
                            + "VALUES ("
                            + "'" + table_data_pegawai_cetak.getValueAt(i, 1).toString() + "',"
                            + "'" + dateFormat.format(DateFilter_SetorCetak2.getDate()) + "',"
                            + table_data_pegawai_cetak.getValueAt(i, 8) + ","
                            + table_data_pegawai_cetak.getValueAt(i, 12) + ") "
                            + "ON DUPLICATE KEY UPDATE "
                            + "`gaji_borong`=" + table_data_pegawai_cetak.getValueAt(i, 8) + ", "
                            + "`bonus1_kecepatan`=" + table_data_pegawai_cetak.getValueAt(i, 12);
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
    }//GEN-LAST:event_button_saveDataCetakActionPerformed

    private void txt_bonus_per_lp_borong_cetak1KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_bonus_per_lp_borong_cetak1KeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_bonus_per_lp_borong_cetak1KeyTyped

    private void txt_bonus_per_lp_harian_cetak1KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_bonus_per_lp_harian_cetak1KeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_bonus_per_lp_harian_cetak1KeyTyped

    private void txt_upah_cetak_t2KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_upah_cetak_t2KeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_upah_cetak_t2KeyTyped

    private void txt_bonus_per_lp_borong_cetak12KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_bonus_per_lp_borong_cetak12KeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_bonus_per_lp_borong_cetak12KeyTyped

    private void txt_bonus_per_lp_harian_cetak12KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_bonus_per_lp_harian_cetak12KeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_bonus_per_lp_harian_cetak12KeyTyped


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox CheckBox_BorongOnly;
    private com.toedter.calendar.JDateChooser DateFilter_SetorCetak1;
    private com.toedter.calendar.JDateChooser DateFilter_SetorCetak2;
    private javax.swing.JTable Tabel_detail_Box_reproses;
    private javax.swing.JTable Tabel_detail_LP_cetak1;
    private javax.swing.JTable Tabel_detail_LP_cetak2;
    private javax.swing.JTable Tabel_detail_LP_cetak3;
    private javax.swing.JButton button_export;
    public static javax.swing.JButton button_saveDataCetak;
    public static javax.swing.JButton button_search;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JLabel label_nama_detail_lp1;
    private javax.swing.JLabel label_nama_detail_lp2;
    private javax.swing.JLabel label_nama_detail_lp3;
    private javax.swing.JLabel label_total_bonus_kec_cetak;
    private javax.swing.JLabel label_total_data_cetak;
    private javax.swing.JLabel label_total_detail_lp1;
    private javax.swing.JLabel label_total_detail_lp2;
    private javax.swing.JLabel label_total_detail_lp3;
    private javax.swing.JLabel label_total_detail_reproses;
    private javax.swing.JLabel label_total_gaji_cetak;
    private javax.swing.JLabel label_total_gaji_detail_lp1;
    private javax.swing.JLabel label_total_gaji_detail_lp2;
    private javax.swing.JLabel label_total_gaji_detail_lp3;
    private javax.swing.JLabel label_total_gaji_detail_reproses;
    private javax.swing.JLabel label_total_gram_cetak;
    private javax.swing.JLabel label_total_kpg_cetak;
    private javax.swing.JTable table_data_pegawai_cetak;
    private javax.swing.JTextField txt_bonus_per_lp_borong_cetak1;
    private javax.swing.JTextField txt_bonus_per_lp_borong_cetak12;
    private javax.swing.JTextField txt_bonus_per_lp_borong_cetak2;
    private javax.swing.JTextField txt_bonus_per_lp_harian_cetak1;
    private javax.swing.JTextField txt_bonus_per_lp_harian_cetak12;
    private javax.swing.JTextField txt_bonus_per_lp_harian_cetak2;
    private javax.swing.JTextField txt_hari_kerja;
    private javax.swing.JTextField txt_kenaikan_jumbo;
    private javax.swing.JTextField txt_max_lama_inap_t1;
    private javax.swing.JTextField txt_max_lama_inap_t12;
    private javax.swing.JTextField txt_max_lama_inap_t2;
    private javax.swing.JTextField txt_minimal_lp_dikerjakan;
    private javax.swing.JTextField txt_search_bagian;
    private javax.swing.JTextField txt_search_nama_cetak;
    private javax.swing.JTextField txt_target_lp_cetak;
    private javax.swing.JTextField txt_upah_cetak_t2;
    private javax.swing.JTextField txt_upah_reproses;
    // End of variables declaration//GEN-END:variables
}
