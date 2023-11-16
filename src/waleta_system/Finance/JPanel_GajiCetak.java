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

public class JPanel_GajiCetak extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DecimalFormat decimalFormat = new DecimalFormat();
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();

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
                        refreshTable_detail_lp_cetak1(id_pegawai);
                        refreshTable_detail_lp_cetak2(id_pegawai);
                        refreshTable_detail_box_reproses(id_pegawai);
                    }
                }
            });

            table_data_pegawai_cetak_baru.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent event) {
                    if (!event.getValueIsAdjusting() && table_data_pegawai_cetak_baru.getSelectedRow() != -1) {
                        int i = table_data_pegawai_cetak_baru.getSelectedRow();
                        String id_pegawai = table_data_pegawai_cetak_baru.getValueAt(i, 1).toString();
                        String nama_pegawai = table_data_pegawai_cetak_baru.getValueAt(i, 2).toString();
                        label_nama_detail_lp_baru.setText(nama_pegawai);
                        refreshTable_detail_lp_cetak_baru(id_pegawai);
                        refreshTable_detail_box_reproses_baru(id_pegawai);
                    }
                }
            });

            table_data_pegawai_koreksi.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent event) {
                    if (!event.getValueIsAdjusting() && table_data_pegawai_koreksi.getSelectedRow() != -1) {
                        int i = table_data_pegawai_koreksi.getSelectedRow();
                        String id_pegawai = table_data_pegawai_koreksi.getValueAt(i, 1).toString();
                        String nama_pegawai = table_data_pegawai_koreksi.getValueAt(i, 2).toString();
                        label_nama_detail_lp_koreksi.setText(nama_pegawai);
                        refreshTable_detail_lp_koreksi(nama_pegawai);
                    }
                }
            });
        } catch (Exception ex) {
            Logger.getLogger(JPanel_GajiCetak.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public JPanel_GajiCetak() {
        initComponents();
    }

    public void refreshTable_cetak() {
        try {
            float total_gaji = 0;
            double total_bonus = 0, total_gram = 0, total_kpg = 0;
            int nomor = 0;

            DefaultTableModel model = (DefaultTableModel) table_data_pegawai_cetak.getModel();
            model.setRowCount(0);

            float min_lp_dikerjakan = Integer.valueOf(txt_minimal_lp_dikerjakan.getText());
            float upah_reproses_utuh = Integer.valueOf(txt_upah_reproses.getText());
            float upah_reproses_pch = upah_reproses_utuh / 2;
            float upah_reproses_flat = upah_reproses_pch / 2;
//            float upah_reproses_jdn = upah_reproses_flat / 2;
            float upah_reproses_jdn = 800;

            Date date1 = DateFilter_SetorCetak1.getDate();
            Date date2 = DateFilter_SetorCetak2.getDate();
            float hari_kerja = Utility.countDaysWithoutFreeDays(date1, date2) + 1;
            float min_lp_dikerjakan_float = (hari_kerja / 6f) * min_lp_dikerjakan;
            decimalFormat.setMaximumFractionDigits(2);
            txt_hari_kerja.setText(Float.toString(hari_kerja));
            txt_target_lp_cetak.setText(decimalFormat.format(min_lp_dikerjakan_float));

            sql = "SELECT `cetak_dikerjakan`, `nama_pegawai`, `nama_bagian`, `cetak_dikerjakan_level`, \n"
                    + "SUM(`Jumlah_LP`) AS 'Jumlah_LP', SUM(`total_kpg`) AS 'total_kpg', SUM(`total_gram`) AS 'total_gram', SUM(`mku`) AS 'mku', SUM(`mpch`) AS 'mpch', SUM(`flat`) AS 'flat', SUM(`jdn`) AS 'jdn', "
                    + "SUM(`bonus_lp_cetak1`) AS 'bonus_lp_cetak1', SUM(`bonus_lp_cetak2`) AS 'bonus_lp_cetak2' \n"
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
                    + "SUM(IF(DATEDIFF(`tgl_selesai_cetak`, `tgl_mulai_cetak`) < 4 AND ((`cetak_mangkok`/`keping_upah`)*100)>=`target_ctk_mku`, (IF((`cetak_mangkok`+`cetak_pecah`+`cetak_flat`)=0,`cetak_jidun_real` / `kpg_lp_jidun`, `keping_upah` / IF(`tb_grade_bahan_baku`.`kategori`='FLAT',`kpg_lp_flat`, `kpg_lp`))), 0)) AS 'bonus_lp_cetak1', \n"
                    + "0 AS 'bonus_lp_cetak2' \n"
                    + "FROM `tb_cetak` \n"
                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_cetak`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_grade_bahan_baku` ON `tb_laporan_produksi`.`kode_grade` = `tb_grade_bahan_baku`.`kode_grade`\n"
                    + "LEFT JOIN `tb_tarif_cabut` ON `tb_laporan_produksi`.`jenis_bulu_lp` = `tb_tarif_cabut`.`bulu_upah`\n"
                    + "LEFT JOIN `tb_karyawan` ON `tb_cetak`.`cetak_dikerjakan1` = `tb_karyawan`.`id_pegawai`\n"
                    + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`\n"
                    + "WHERE \n"
                    + "`tb_karyawan`.`nama_pegawai` LIKE '%" + txt_search_nama_cetak.getText() + "%' \n"
                    + "AND `tgl_selesai_cetak` BETWEEN '" + dateFormat.format(DateFilter_SetorCetak1.getDate()) + "' AND '" + dateFormat.format(DateFilter_SetorCetak2.getDate()) + "'\n"
                    + "AND `cetak_dikerjakan1` IS NOT NULL \n"
                    + "GROUP BY `cetak_dikerjakan1` \n"
                    + "UNION ALL \n"
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
                    + "0 AS 'bonus_lp_cetak1', \n"
                    + "SUM(IF(DATEDIFF(`tgl_selesai_cetak`, `tgl_mulai_cetak`) < 4 AND ((`cetak_mangkok`/`keping_upah`)*100)>=`target_ctk_mku`, (IF((`cetak_mangkok`+`cetak_pecah`+`cetak_flat`)=0,`cetak_jidun_real` / `kpg_lp_jidun`, `keping_upah` / IF(`tb_grade_bahan_baku`.`kategori`='FLAT',`kpg_lp_flat`, `kpg_lp`))), 0)) AS 'bonus_lp_cetak2' \n"
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
                    + "GROUP BY `cetak_dikerjakan` \n"
                    + ") DATA \n"
                    + "WHERE `nama_bagian` LIKE '%" + txt_search_bagian.getText() + "%'"
                    + "GROUP BY `cetak_dikerjakan` "
                    + "ORDER BY `nama_bagian`, `nama_pegawai`";
//                System.out.println(sql);
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
                float total_bobot_lp_bonus = rs.getFloat("bonus_lp_cetak2");
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
                    row[9] = 0d;
                    row[10] = 0d;
                    row[11] = 0d;
                } else {
                    int bonus_per_lp_cetak1 = 0;
                    int bonus_per_lp_cetak2 = 0;
                    if (rs.getString("cetak_dikerjakan_level") != null && rs.getString("cetak_dikerjakan_level").toLowerCase().contains("brg")) {
                        bonus_per_lp_cetak1 = Integer.valueOf(txt_bonus_per_lp_borong_cetak1.getText());
                        bonus_per_lp_cetak2 = Integer.valueOf(txt_bonus_per_lp_borong_cetak2.getText());
                    } else if ((rs.getString("cetak_dikerjakan_level") != null && rs.getString("cetak_dikerjakan_level").toLowerCase().contains("training"))
                            || rs.getString("nama_bagian").toLowerCase().contains("trainer")
                            || rs.getString("nama_bagian").toLowerCase().contains("pengawas")) {
                        bonus_per_lp_cetak1 = 0;
                        bonus_per_lp_cetak2 = 0;
                    } else {
                        bonus_per_lp_cetak1 = Integer.valueOf(txt_bonus_per_lp_harian_cetak1.getText());
                        bonus_per_lp_cetak2 = Integer.valueOf(txt_bonus_per_lp_harian_cetak2.getText());
                    }
                    double bonus_ctk1 = (Math.floor(rs.getDouble("bonus_lp_cetak1") / 0.5d) * 0.5d) * bonus_per_lp_cetak1;
                    row[9] = bonus_ctk1;
                    total_bonus = total_bonus + bonus_ctk1;
                    double bonus_ctk2 = (Math.floor(rs.getDouble("bonus_lp_cetak2") / 0.5d) * 0.5d) * bonus_per_lp_cetak2;
                    row[10] = bonus_ctk2;
                    total_bonus = total_bonus + bonus_ctk2;
                    row[11] = bonus_ctk2;
                }
                row[12] = rs.getString("cetak_dikerjakan_level");
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
            Logger.getLogger(JPanel_GajiCetak.class.getName()).log(Level.SEVERE, null, ex);
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
                        + "DATEDIFF(`tgl_selesai_cetak`, `tgl_mulai_cetak`) AS 'lama_inap',\n"
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
                        + "WHERE `cetak_dikerjakan1` = '" + id + "' \n"
                        + "AND `tgl_selesai_cetak` BETWEEN '" + dateFormat.format(DateFilter_SetorCetak1.getDate()) + "' AND '" + dateFormat.format(DateFilter_SetorCetak2.getDate()) + "'\n";
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
//                float gaji = rs.getFloat("mku") + rs.getFloat("mpch") + rs.getFloat("flat") + rs.getFloat("jdn");
                float gaji = 0;
                row[6] = gaji;
                total_gaji = total_gaji + gaji;
                row[7] = rs.getInt("lama_inap");
//                float keping = rs.getFloat("jumlah_keping");
//                if (rs.getFloat("jumlah_keping") == 0) {
//                    keping = rs.getFloat("berat_basah") / 8;
//                }
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
            Logger.getLogger(JPanel_GajiCetak.class.getName()).log(Level.SEVERE, null, ex);
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
                        + "DATEDIFF(`tgl_selesai_cetak`, `tgl_mulai_cetak`) AS 'lama_inap',\n"
                        + "(`cetak_mangkok` * `ctk_mku` * IF(LOCATE('JUMBO', `tb_laporan_produksi`.`kode_grade`)>0 OR LOCATE('BPU', `tb_laporan_produksi`.`kode_grade`)>0, 1.05, 1)) AS 'mku', \n"
                        + "(`cetak_pecah` * `ctk_mpch` * IF(LOCATE('JUMBO', `tb_laporan_produksi`.`kode_grade`)>0 OR LOCATE('BPU', `tb_laporan_produksi`.`kode_grade`)>0, 1.05, 1)) AS 'mpch', \n"
                        + "(`cetak_flat` * `ctk_flat` * IF(LOCATE('JUMBO', `tb_laporan_produksi`.`kode_grade`)>0 OR LOCATE('BPU', `tb_laporan_produksi`.`kode_grade`)>0, 1.05, 1)) AS 'flat', \n"
                        + "(`cetak_jidun_real` * `ctk_jdn`) AS 'jdn', \n"
                        + "(IF((`cetak_mangkok`+`cetak_pecah`+`cetak_flat`)=0,`cetak_jidun_real` / `kpg_lp_jidun`, `keping_upah` / IF(`tb_grade_bahan_baku`.`kategori`='FLAT',`kpg_lp_flat`, `kpg_lp`))) AS 'bobot',\n"
                        + "`tb_grade_bahan_baku`.`target_ctk_mku`\n"
                        + "FROM `tb_cetak` \n"
                        + "LEFT JOIN `tb_laporan_produksi` ON `tb_cetak`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                        + "LEFT JOIN `tb_grade_bahan_baku` ON `tb_laporan_produksi`.`kode_grade` = `tb_grade_bahan_baku`.`kode_grade`\n"
                        + "LEFT JOIN `tb_tarif_cabut` ON `tb_laporan_produksi`.`jenis_bulu_lp` = `tb_tarif_cabut`.`bulu_upah`\n"
                        + "WHERE `cetak_dikerjakan` = '" + id + "' \n"
                        + "AND `tgl_selesai_cetak` BETWEEN '" + dateFormat.format(DateFilter_SetorCetak1.getDate()) + "' AND '" + dateFormat.format(DateFilter_SetorCetak2.getDate()) + "'\n";
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
//                float keping = rs.getFloat("jumlah_keping");
//                if (rs.getFloat("jumlah_keping") == 0) {
//                    keping = rs.getFloat("berat_basah") / 8;
//                }
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
            Logger.getLogger(JPanel_GajiCetak.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_detail_box_reproses(String id) {
        try {
            float total_gaji = 0;
            float upah_reproses_utuh = Integer.valueOf(txt_upah_reproses.getText());
            float upah_reproses_pch = upah_reproses_utuh / 2;
            float upah_reproses_flat = upah_reproses_pch / 2;
//            float upah_reproses_jdn = upah_reproses_flat / 2;
            float upah_reproses_jdn = 800;
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
            Logger.getLogger(JPanel_GajiCetak.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_koreksi() {
        try {
            double total_bonus = 0, total_gram = 0, total_kpg = 0;
            int nomor = 0;
            decimalFormat.setMaximumFractionDigits(0);
            DefaultTableModel model = (DefaultTableModel) table_data_pegawai_koreksi.getModel();
            model.setRowCount(0);
            int bonus_per_lp = 0, min_lp_dikerjakan = 0;

            try {
                min_lp_dikerjakan = Integer.valueOf(txt_minimal_lp_koreksi_dikerjakan.getText());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Format angka salah !");
            }
            Date date1 = DateFilter_TerimaCetak1.getDate();
            Date date2 = DateFilter_TerimaCetak2.getDate();
            int hari_kerja = Utility.countDaysWithoutFreeDays(date1, date2) + 1;
            float min_lp_dikerjakan_float = ((float) hari_kerja / 6.f) * (float) min_lp_dikerjakan;
            decimalFormat.setMaximumFractionDigits(2);
            txt_hari_kerja_koreksi.setText(Integer.toString(hari_kerja));
            txt_target_lp_koreksi.setText(decimalFormat.format(min_lp_dikerjakan_float));

            if (DateFilter_TerimaCetak1.getDate() != null && DateFilter_TerimaCetak2.getDate() != null) {
                sql = "SELECT `cetak_dikoreksi`, `cetak_dikoreksi_id`, `tb_karyawan`.`nama_pegawai`, `tb_bagian`.`nama_bagian`, `cetak_dikoreksi_grup`, `cetak_dikoreksi_level`, \n"
                        + "SUM(`tb_laporan_produksi`.`jumlah_keping` / `kpg_lp`) AS 'Jumlah_LP', "
                        + "SUM(`tb_laporan_produksi`.`jumlah_keping`) AS 'total_kpg', "
                        + "SUM(`tb_laporan_produksi`.`berat_basah`) AS 'total_gram', \n"
                        + "SUM(IF(DATEDIFF(`tgl_mulai_cetak`, `tb_cabut`.`tgl_setor_cabut`) < 3, (IF(`tb_laporan_produksi`.`jumlah_keping`>0, `tb_laporan_produksi`.`jumlah_keping`, `tb_laporan_produksi`.`berat_basah`/8) / `kpg_lp`), 0)) AS 'lp_bonus'\n"
                        + "FROM `tb_cetak` \n"
                        + "LEFT JOIN `tb_laporan_produksi` ON `tb_cetak`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                        + "LEFT JOIN `tb_cabut` ON `tb_cetak`.`no_laporan_produksi` = `tb_cabut`.`no_laporan_produksi`\n"
                        + "LEFT JOIN `tb_tarif_cabut` ON `tb_laporan_produksi`.`jenis_bulu_lp` = `tb_tarif_cabut`.`bulu_upah`\n"
                        + "LEFT JOIN `tb_karyawan` ON `tb_cetak`.`cetak_dikoreksi_id` = `tb_karyawan`.`id_pegawai`\n"
                        + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`\n"
                        + "WHERE `cetak_dikoreksi` LIKE '%" + txt_search_nama_koreksi.getText() + "%' AND `tb_karyawan`.`status` = 'IN' "
                        + "AND `tgl_mulai_cetak` BETWEEN '" + dateFormat.format(DateFilter_TerimaCetak1.getDate()) + "' AND '" + dateFormat.format(DateFilter_TerimaCetak2.getDate()) + "'\n"
                        + "GROUP BY `cetak_dikoreksi`";
            }
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[10];
            while (rs.next()) {
                nomor++;
                row[0] = nomor;
                row[1] = rs.getString("cetak_dikoreksi_id");
                row[2] = rs.getString("nama_pegawai");
                row[3] = rs.getString("nama_bagian");
                row[4] = Math.round(rs.getFloat("lp_bonus") * 100.f) / 100.f;
                row[5] = rs.getFloat("total_kpg");
                total_kpg = total_kpg + rs.getInt("total_kpg");
                row[6] = rs.getFloat("total_gram");
                total_gram = total_gram + rs.getInt("total_gram");
                if (rs.getInt("lp_bonus") < min_lp_dikerjakan_float) {
                    row[7] = 0d;
                } else {
                    try {
                        if (rs.getString("cetak_dikoreksi_level").contains("MANDIRI") && rs.getString("nama_bagian").contains("CABUT-KOREKSI")) {
                            bonus_per_lp = Integer.valueOf(txt_bonus_koreksi.getText());
                        } else {
                            bonus_per_lp = 0;
                        }
                    } catch (NumberFormatException | NullPointerException e) {
                        System.out.println("error");
//                        JOptionPane.showMessageDialog(this, "Format angka salah !");
                    }
                    double bonus = (Math.floor(rs.getDouble("lp_bonus") / 0.5d) * 0.5d) * bonus_per_lp;
                    row[7] = bonus;
                    total_bonus = total_bonus + bonus;
                }
                row[8] = rs.getString("cetak_dikoreksi_level");
                model.addRow(row);
            }

            label_total_data_koreksi.setText(decimalFormat.format(table_data_pegawai_koreksi.getRowCount()));
            label_total_bonus_koreksi.setText("Rp. " + decimalFormat.format(total_bonus));
            label_total_gram_koreksi.setText(decimalFormat.format(total_gram) + " Gr");
            label_total_kpg_koreksi.setText(decimalFormat.format(total_kpg) + " Kpg");

            ColumnsAutoSizer.sizeColumnsToFit(table_data_pegawai_koreksi);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_GajiCetak.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_detail_lp_koreksi(String nama) {
        try {
            float total_lp = 0;
            decimalFormat.setMaximumFractionDigits(0);
            DefaultTableModel model = (DefaultTableModel) Tabel_detail_LP_koreksi.getModel();
            model.setRowCount(0);
            sql = "";
            if (DateFilter_TerimaCetak1.getDate() != null && DateFilter_TerimaCetak2.getDate() != null) {
                sql = "SELECT `tb_cetak`.`no_laporan_produksi`, `tgl_selesai_cetak`, `tb_laporan_produksi`.`kode_grade`, `jenis_bulu_lp`, `jumlah_keping`, `berat_basah`, `cetak_dikerjakan`, `kpg_lp`, `cetak_mangkok`, "
                        + "DATEDIFF(`tgl_mulai_cetak`, `tb_cabut`.`tgl_setor_cabut`) AS 'lama_inap',\n"
                        + "`tb_grade_bahan_baku`.`target_ctk_mku`\n"
                        + "FROM `tb_cetak` \n"
                        + "LEFT JOIN `tb_laporan_produksi` ON `tb_cetak`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                        + "LEFT JOIN `tb_cabut` ON `tb_cabut`.`no_laporan_produksi` = `tb_cetak`.`no_laporan_produksi`\n"
                        + "LEFT JOIN `tb_grade_bahan_baku` ON `tb_laporan_produksi`.`kode_grade` = `tb_grade_bahan_baku`.`kode_grade`\n"
                        + "LEFT JOIN `tb_tarif_cabut` ON `tb_laporan_produksi`.`jenis_bulu_lp` = `tb_tarif_cabut`.`bulu_upah`\n"
                        + "WHERE `cetak_dikoreksi` = '" + nama + "' \n"
                        + "AND `tgl_mulai_cetak` BETWEEN '" + dateFormat.format(DateFilter_TerimaCetak1.getDate()) + "' AND '" + dateFormat.format(DateFilter_TerimaCetak2.getDate()) + "'\n";
            }
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[11];
            while (rs.next()) {
                row[0] = rs.getString("no_laporan_produksi");
                row[1] = rs.getString("kode_grade");
                row[2] = rs.getString("jenis_bulu_lp");
                row[3] = rs.getInt("jumlah_keping");
                row[4] = rs.getInt("berat_basah");
                row[5] = rs.getInt("lama_inap");
                float kpg = rs.getFloat("jumlah_keping");
                if (kpg == 0) {
                    kpg = rs.getFloat("berat_basah") / 8;
                }
                row[6] = Math.round((kpg / rs.getFloat("kpg_lp")) * 100.f) / 100.f;
                total_lp = total_lp + (Math.round((kpg / rs.getFloat("kpg_lp")) * 100.f) / 100.f);
                model.addRow(row);
            }
            label_total_detail_lp_koreksi.setText(Integer.toString(Tabel_detail_LP_koreksi.getRowCount()));

            ColumnsAutoSizer.sizeColumnsToFit(Tabel_detail_LP_koreksi);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_GajiCetak.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_cetak_baru() {
        try {
            float total_gaji = 0;
            double total_bonus = 0, total_gram = 0, total_kpg = 0;
            int nomor = 0;

            DefaultTableModel model = (DefaultTableModel) table_data_pegawai_cetak_baru.getModel();
            model.setRowCount(0);

            int bonus_per_lp = 0, min_lp_dikerjakan = 0;
            float upah_reproses_utuh = Integer.valueOf(txt_upah_reproses_baru.getText());
            float upah_reproses_pch = upah_reproses_utuh / 2;
            float upah_reproses_flat = upah_reproses_pch / 2;
//            float upah_reproses_jdn = upah_reproses_flat / 2;
            float upah_reproses_jdn = 800;

            try {
                min_lp_dikerjakan = Integer.valueOf(txt_minimal_lp_dikerjakan_baru.getText());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Format angka salah !");
            }
            Date date1 = DateFilter_SetorCetak1_baru.getDate();
            Date date2 = DateFilter_SetorCetak2_baru.getDate();
            int hari_kerja = Utility.countDaysWithoutFreeDays(date1, date2) + 1;
            float min_lp_dikerjakan_float = ((float) hari_kerja / 6.f) * (float) min_lp_dikerjakan;
            decimalFormat.setMaximumFractionDigits(2);
            txt_hari_kerja_baru.setText(Long.toString(hari_kerja));
            txt_target_lp_cetak_baru.setText(decimalFormat.format(min_lp_dikerjakan_float));

            if (DateFilter_SetorCetak1_baru.getDate() != null && DateFilter_SetorCetak2_baru.getDate() != null) {
                sql = "SELECT `tb_detail_pencetak`.`id_pegawai` AS 'cetak_dikerjakan', `tb_karyawan`.`nama_pegawai`, `tb_bagian`.`nama_bagian`, `tb_karyawan`.`level_gaji` AS 'cetak_dikerjakan_level', \n"
                        + "SUM(IF((`tb_detail_pencetak`.`cetak_mangkok`+`tb_detail_pencetak`.`cetak_pecah`+`tb_detail_pencetak`.`cetak_flat`)=0,`tb_detail_pencetak`.`cetak_jidun_real` / `kpg_lp_jidun`, `kpg_cetak` / IF(`tb_grade_bahan_baku`.`kategori`='FLAT', `kpg_lp_flat`, `kpg_lp`))) AS 'Jumlah_LP', \n"
                        + "SUM(`tb_laporan_produksi`.`keping_upah`) AS 'total_kpg', \n"
                        + "SUM(`tb_laporan_produksi`.`berat_basah`) AS 'total_gram', \n"
                        + "SUM(`tb_detail_pencetak`.`cetak_mangkok` * `ctk_mku` * IF(LOCATE('JUMBO', `tb_laporan_produksi`.`kode_grade`)>0 OR LOCATE('BPU', `tb_laporan_produksi`.`kode_grade`)>0, 1.05, 1)) AS 'mku', \n"
                        + "SUM(`tb_detail_pencetak`.`cetak_pecah` * `ctk_mpch` * IF(LOCATE('JUMBO', `tb_laporan_produksi`.`kode_grade`)>0 OR LOCATE('BPU', `tb_laporan_produksi`.`kode_grade`)>0, 1.05, 1)) AS 'mpch', \n"
                        + "SUM(`tb_detail_pencetak`.`cetak_flat` * `ctk_flat` * IF(LOCATE('JUMBO', `tb_laporan_produksi`.`kode_grade`)>0 OR LOCATE('BPU', `tb_laporan_produksi`.`kode_grade`)>0, 1.05, 1)) AS 'flat', \n"
                        + "SUM(`tb_detail_pencetak`.`cetak_jidun_real` * `ctk_jdn`) AS 'jdn', \n"
                        + "SUM("
                        + "IF(DATEDIFF(`tgl_selesai_cetak`, `tgl_mulai_cetak`) < 4 AND ((`tb_cetak`.`cetak_mangkok`/`keping_upah`)*100)>=`target_ctk_mku`, "
                        + "(IF((`tb_detail_pencetak`.`cetak_mangkok`+`tb_detail_pencetak`.`cetak_pecah`+`tb_detail_pencetak`.`cetak_flat`)=0,`tb_detail_pencetak`.`cetak_jidun_real` / `kpg_lp_jidun`, `kpg_cetak` / IF(`tb_grade_bahan_baku`.`kategori`='FLAT',`kpg_lp_flat`, `kpg_lp`))), "
                        + "0)"
                        + ") AS 'lp_bonus'\n"
                        + "FROM `tb_detail_pencetak` \n"
                        + "LEFT JOIN `tb_cetak` ON `tb_detail_pencetak`.`no_laporan_produksi` = `tb_cetak`.`no_laporan_produksi`\n"
                        + "LEFT JOIN `tb_laporan_produksi` ON `tb_detail_pencetak`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                        + "LEFT JOIN `tb_grade_bahan_baku` ON `tb_laporan_produksi`.`kode_grade` = `tb_grade_bahan_baku`.`kode_grade`\n"
                        + "LEFT JOIN `tb_tarif_cabut` ON `tb_laporan_produksi`.`jenis_bulu_lp` = `tb_tarif_cabut`.`bulu_upah`\n"
                        + "LEFT JOIN `tb_karyawan` ON `tb_detail_pencetak`.`id_pegawai` = `tb_karyawan`.`id_pegawai`\n"
                        + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`\n"
                        + "WHERE `tb_karyawan`.`nama_pegawai` LIKE '%" + txt_search_nama_cetak_baru.getText() + "%' \n"
                        + "AND `tb_bagian`.`nama_bagian` LIKE '%" + txt_search_bagian_baru.getText() + "%'\n"
                        + "AND `tgl_selesai_cetak` BETWEEN '" + dateFormat.format(DateFilter_SetorCetak1_baru.getDate()) + "' AND '" + dateFormat.format(DateFilter_SetorCetak2_baru.getDate()) + "'\n"
                        + "GROUP BY `tb_detail_pencetak`.`id_pegawai` \n"
                        + "ORDER BY `tb_bagian`.`nama_bagian`, `tb_karyawan`.`nama_pegawai`";
                System.out.println(sql);
            }
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
                        + "AND `tgl_cetak` BETWEEN '" + dateFormat.format(DateFilter_SetorCetak1_baru.getDate()) + "' AND '" + dateFormat.format(DateFilter_SetorCetak2_baru.getDate()) + "'\n";
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
                row[4] = Math.round(rs.getFloat("lp_bonus") * 100.f) / 100.f;
                row[5] = rs.getFloat("total_kpg");
                total_kpg = total_kpg + rs.getInt("total_kpg");
                row[6] = rs.getFloat("total_gram");
                total_gram = total_gram + rs.getInt("total_gram");
                row[7] = kpg_reproses;
                float gaji = rs.getFloat("mku") + rs.getFloat("mpch") + rs.getFloat("flat") + rs.getFloat("jdn");
                if (CheckBox_BorongOnly_baru.isSelected() && !rs.getString("cetak_dikerjakan_level").equals("MANDIRI CTK BRG")) {
                    gaji = 0;
                    gaji_reproses = 0;
                }
                row[8] = gaji + gaji_reproses;
                total_gaji = total_gaji + gaji + gaji_reproses;
                if (rs.getFloat("lp_bonus") < min_lp_dikerjakan_float) {
                    row[9] = 0d;
                } else {
                    try {
                        if (rs.getString("cetak_dikerjakan_level").toLowerCase().contains("brg")) {
                            bonus_per_lp = Integer.valueOf(txt_bonus_per_lp_borong_baru.getText());
                        } else if (rs.getString("cetak_dikerjakan_level").toLowerCase().contains("training")
                                || rs.getString("nama_bagian").toLowerCase().contains("trainer") || rs.getString("nama_bagian").toLowerCase().contains("pengawas")) {
                            bonus_per_lp = 0;
                        } else {
                            bonus_per_lp = Integer.valueOf(txt_bonus_per_lp_harian_baru.getText());
                        }
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(this, "Format angka salah !");
                        Logger.getLogger(JPanel_GajiCetak.class.getName()).log(Level.SEVERE, null, e);
                    }
                    double bonus = (Math.floor(rs.getDouble("lp_bonus") / 0.5d) * 0.5d) * bonus_per_lp;
                    row[9] = bonus;
                    total_bonus = total_bonus + bonus;
                }
                row[10] = rs.getString("cetak_dikerjakan_level");
                model.addRow(row);
            }

            decimalFormat.setMaximumFractionDigits(0);
            label_total_data_cetak_baru.setText(decimalFormat.format(table_data_pegawai_cetak_baru.getRowCount()));
            label_total_gaji_cetak_baru.setText("Rp. " + decimalFormat.format(total_gaji));
            label_total_bonus_kec_cetak_baru.setText("Rp. " + decimalFormat.format(total_bonus));
            label_total_gram_cetak_baru.setText(decimalFormat.format(total_gram) + " Gr");
            label_total_kpg_cetak_baru.setText(decimalFormat.format(total_kpg) + " Kpg");

            ColumnsAutoSizer.sizeColumnsToFit(table_data_pegawai_cetak_baru);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_GajiCetak.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_detail_lp_cetak_baru(String id) {
        try {
            float total_gaji = 0;
            float total_lp = 0;
            decimalFormat.setMaximumFractionDigits(0);
            DefaultTableModel model = (DefaultTableModel) Tabel_detail_LP_cetak_baru.getModel();
            model.setRowCount(0);

            if (DateFilter_SetorCetak1_baru.getDate() != null && DateFilter_SetorCetak2_baru.getDate() != null) {
                sql = "SELECT `tb_detail_pencetak`.`no_laporan_produksi`, `tgl_selesai_cetak`, `tb_laporan_produksi`.`kode_grade`, `jenis_bulu_lp`, `jumlah_keping`, `berat_basah`, `cetak_dikerjakan`, `kpg_lp`, `tb_detail_pencetak`.`cetak_mangkok`, `tb_grade_bahan_baku`.`kategori`, "
                        + "IF((`tb_detail_pencetak`.`cetak_mangkok`+`tb_detail_pencetak`.`cetak_pecah`+`tb_detail_pencetak`.`cetak_flat`)=0,`kpg_lp_jidun`, IF(`tb_grade_bahan_baku`.`kategori`='FLAT',`kpg_lp_flat`, `kpg_lp`)) AS 'kpg_lp_besar', "
                        + "DATEDIFF(`tgl_selesai_cetak`, `tgl_mulai_cetak`) AS 'lama_inap',\n"
                        + "(`tb_detail_pencetak`.`cetak_mangkok` * `ctk_mku` * IF(LOCATE('JUMBO', `tb_laporan_produksi`.`kode_grade`)>0 OR LOCATE('BPU', `tb_laporan_produksi`.`kode_grade`)>0, 1.05, 1)) AS 'mku', \n"
                        + "(`tb_detail_pencetak`.`cetak_pecah` * `ctk_mpch` * IF(LOCATE('JUMBO', `tb_laporan_produksi`.`kode_grade`)>0 OR LOCATE('BPU', `tb_laporan_produksi`.`kode_grade`)>0, 1.05, 1)) AS 'mpch', \n"
                        + "(`tb_detail_pencetak`.`cetak_flat` * `ctk_flat` * IF(LOCATE('JUMBO', `tb_laporan_produksi`.`kode_grade`)>0 OR LOCATE('BPU', `tb_laporan_produksi`.`kode_grade`)>0, 1.05, 1)) AS 'flat', \n"
                        + "(`tb_detail_pencetak`.`cetak_jidun_real` * `ctk_jdn`) AS 'jdn', \n"
                        + "(IF((`tb_detail_pencetak`.`cetak_mangkok`+`tb_detail_pencetak`.`cetak_pecah`+`tb_detail_pencetak`.`cetak_flat`)=0,`tb_detail_pencetak`.`cetak_jidun_real` / `kpg_lp_jidun`, `kpg_cetak` / IF(`tb_grade_bahan_baku`.`kategori`='FLAT',`kpg_lp_flat`, `kpg_lp`))) AS 'bobot',\n"
                        + "`tb_grade_bahan_baku`.`target_ctk_mku`\n"
                        + "FROM `tb_detail_pencetak` \n"
                        + "LEFT JOIN `tb_cetak` ON `tb_detail_pencetak`.`no_laporan_produksi` = `tb_cetak`.`no_laporan_produksi`\n"
                        + "LEFT JOIN `tb_laporan_produksi` ON `tb_detail_pencetak`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                        + "LEFT JOIN `tb_grade_bahan_baku` ON `tb_laporan_produksi`.`kode_grade` = `tb_grade_bahan_baku`.`kode_grade`\n"
                        + "LEFT JOIN `tb_tarif_cabut` ON `tb_laporan_produksi`.`jenis_bulu_lp` = `tb_tarif_cabut`.`bulu_upah`\n"
                        + "WHERE `id_pegawai` = '" + id + "' \n"
                        + "AND `tgl_selesai_cetak` BETWEEN '" + dateFormat.format(DateFilter_SetorCetak1_baru.getDate()) + "' AND '" + dateFormat.format(DateFilter_SetorCetak2_baru.getDate()) + "'\n";
            }
            System.out.println(sql);
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
//                float keping = rs.getFloat("jumlah_keping");
//                if (rs.getFloat("jumlah_keping") == 0) {
//                    keping = rs.getFloat("berat_basah") / 8;
//                }
                row[8] = Math.round(rs.getFloat("bobot") * 100.f) / 100.f;
                total_lp = total_lp + (Math.round(rs.getFloat("bobot") * 100.f) / 100.f);
                row[9] = rs.getInt("target_ctk_mku");
                row[10] = Math.round((rs.getFloat("cetak_mangkok") / rs.getFloat("jumlah_keping")) * 1000.f) / 10.f;
                row[11] = rs.getString("kategori");
                row[12] = rs.getInt("kpg_lp_besar");
                model.addRow(row);
            }
            label_total_gaji_detail_lp_baru.setText("Rp. " + decimalFormat.format(total_gaji));
            label_total_detail_lp_baru.setText(Float.toString(Math.round(total_lp * 100.f) / 100.f));

            ColumnsAutoSizer.sizeColumnsToFit(Tabel_detail_LP_cetak_baru);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_GajiCetak.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_detail_box_reproses_baru(String id) {
        try {
            float total_gaji = 0;
            float upah_reproses_utuh = Integer.valueOf(txt_upah_reproses_baru.getText());
            float upah_reproses_pch = upah_reproses_utuh / 2;
            float upah_reproses_flat = upah_reproses_pch / 2;
//            float upah_reproses_jdn = upah_reproses_flat / 2;
            float upah_reproses_jdn = 800;
            decimalFormat.setMaximumFractionDigits(0);
            DefaultTableModel model = (DefaultTableModel) Tabel_detail_Box_reproses_baru.getModel();
            model.setRowCount(0);

            if (DateFilter_SetorCetak1_baru.getDate() != null && DateFilter_SetorCetak2_baru.getDate() != null) {
                sql = "SELECT `tb_reproses`.`no_box`, `tb_grade_bahan_jadi`.`kode_grade`, `tgl_cetak`, `tb_reproses`.`keping`, `tb_reproses`.`gram`, "
                        + "(`mk` * " + upah_reproses_utuh + ") AS 'mku', \n"
                        + "(`pch` * " + upah_reproses_pch + ") AS 'mpch', \n"
                        + "(`flat` * " + upah_reproses_flat + ") AS 'flat', \n"
                        + "(`jdn` * " + upah_reproses_jdn + ") AS 'jdn' \n"
                        + "FROM `tb_reproses` \n"
                        + "LEFT JOIN `tb_box_bahan_jadi` ON `tb_reproses`.`no_box` = `tb_box_bahan_jadi`.`no_box`\n"
                        + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_grade_bahan_jadi`.`kode` = `tb_box_bahan_jadi`.`kode_grade_bahan_jadi`\n"
                        + "WHERE `pekerja_cetak` = '" + id + "' \n"
                        + "AND `tgl_cetak` BETWEEN '" + dateFormat.format(DateFilter_SetorCetak1_baru.getDate()) + "' AND '" + dateFormat.format(DateFilter_SetorCetak2_baru.getDate()) + "'\n";
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

            label_total_detail_reproses_baru.setText(Integer.toString(Tabel_detail_Box_reproses_baru.getRowCount()));
            label_total_gaji_detail_reproses_baru.setText("Rp. " + decimalFormat.format(total_gaji));

            ColumnsAutoSizer.sizeColumnsToFit(Tabel_detail_Box_reproses_baru);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_GajiCetak.class.getName()).log(Level.SEVERE, null, ex);
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
        txt_max_lama_inap = new javax.swing.JTextField();
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
        jPanel11 = new javax.swing.JPanel();
        label_total_gaji_detail_reproses = new javax.swing.JLabel();
        label_total_detail_reproses = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        Tabel_detail_Box_reproses = new javax.swing.JTable();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        txt_search_nama_cetak_baru = new javax.swing.JTextField();
        button_search_baru = new javax.swing.JButton();
        jLabel37 = new javax.swing.JLabel();
        DateFilter_SetorCetak1_baru = new com.toedter.calendar.JDateChooser();
        DateFilter_SetorCetak2_baru = new com.toedter.calendar.JDateChooser();
        jLabel38 = new javax.swing.JLabel();
        label_total_data_cetak_baru = new javax.swing.JLabel();
        button_export_baru = new javax.swing.JButton();
        jLabel39 = new javax.swing.JLabel();
        label_total_gaji_cetak_baru = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jScrollPane7 = new javax.swing.JScrollPane();
        table_data_pegawai_cetak_baru = new javax.swing.JTable();
        jPanel9 = new javax.swing.JPanel();
        jLabel41 = new javax.swing.JLabel();
        label_total_gaji_detail_lp_baru = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        label_nama_detail_lp_baru = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        Tabel_detail_LP_cetak_baru = new javax.swing.JTable();
        jLabel43 = new javax.swing.JLabel();
        label_total_detail_lp_baru = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jScrollPane9 = new javax.swing.JScrollPane();
        Tabel_detail_Box_reproses_baru = new javax.swing.JTable();
        jLabel45 = new javax.swing.JLabel();
        label_total_gaji_detail_reproses_baru = new javax.swing.JLabel();
        label_total_detail_reproses_baru = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        txt_minimal_lp_dikerjakan_baru = new javax.swing.JTextField();
        jLabel49 = new javax.swing.JLabel();
        txt_max_lama_inap_baru = new javax.swing.JTextField();
        jLabel50 = new javax.swing.JLabel();
        txt_bonus_per_lp_borong_baru = new javax.swing.JTextField();
        txt_bonus_per_lp_harian_baru = new javax.swing.JTextField();
        txt_search_bagian_baru = new javax.swing.JTextField();
        jLabel51 = new javax.swing.JLabel();
        CheckBox_BorongOnly_baru = new javax.swing.JCheckBox();
        jLabel52 = new javax.swing.JLabel();
        label_total_bonus_kec_cetak_baru = new javax.swing.JLabel();
        jLabel53 = new javax.swing.JLabel();
        label_total_gram_cetak_baru = new javax.swing.JLabel();
        label_total_kpg_cetak_baru = new javax.swing.JLabel();
        jLabel54 = new javax.swing.JLabel();
        jLabel55 = new javax.swing.JLabel();
        txt_upah_reproses_baru = new javax.swing.JTextField();
        jLabel56 = new javax.swing.JLabel();
        txt_hari_kerja_baru = new javax.swing.JTextField();
        jLabel57 = new javax.swing.JLabel();
        txt_target_lp_cetak_baru = new javax.swing.JTextField();
        jLabel58 = new javax.swing.JLabel();
        txt_kenaikan_jumbo_baru = new javax.swing.JTextField();
        button_saveDataCetak_baru = new javax.swing.JButton();
        jScrollPane10 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        DateFilter_TerimaCetak2 = new com.toedter.calendar.JDateChooser();
        DateFilter_TerimaCetak1 = new com.toedter.calendar.JDateChooser();
        jLabel13 = new javax.swing.JLabel();
        button_search_koreksi = new javax.swing.JButton();
        txt_search_nama_koreksi = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        button_export_gaji_koreksi = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();
        txt_bonus_koreksi = new javax.swing.JTextField();
        jScrollPane4 = new javax.swing.JScrollPane();
        table_data_pegawai_koreksi = new javax.swing.JTable();
        label_total_data_koreksi = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        label_total_bonus_koreksi = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        label_nama_detail_lp_koreksi = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        Tabel_detail_LP_koreksi = new javax.swing.JTable();
        jLabel20 = new javax.swing.JLabel();
        label_total_detail_lp_koreksi = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        txt_minimal_lp_koreksi_dikerjakan = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        txt_max_lama_inap_koreksi = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        label_total_gram_koreksi = new javax.swing.JLabel();
        label_total_kpg_koreksi = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        txt_hari_kerja_koreksi = new javax.swing.JTextField();
        jLabel35 = new javax.swing.JLabel();
        txt_target_lp_koreksi = new javax.swing.JTextField();
        button_saveDataKoreksi = new javax.swing.JButton();

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
                "No", "ID Pegawai", "Nama", "Bagian", "Tot LP", "Kpg Upah", "Gram", "Kpg Rprs", "Gaji", "Bonus CTK1", "Bonus CTK2", "Total Bonus", "Level"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.String.class
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
        table_data_pegawai_cetak.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(table_data_pegawai_cetak);

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("Bonus Kecepatan / LP Harian (Rp.) Cetak 2 :");

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel4.setText("Min LP dikerjakan :");

        txt_minimal_lp_dikerjakan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_minimal_lp_dikerjakan.setText("4");
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
        jLabel9.setText("Max lama inap (hari) :");

        txt_max_lama_inap.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_max_lama_inap.setText("3");

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel12.setText("Bonus Kecepatan / LP Borong (Rp.) Cetak 2 :");

        txt_bonus_per_lp_borong_cetak2.setEditable(false);
        txt_bonus_per_lp_borong_cetak2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_bonus_per_lp_borong_cetak2.setText("8000");
        txt_bonus_per_lp_borong_cetak2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_bonus_per_lp_borong_cetak2KeyTyped(evt);
            }
        });

        txt_bonus_per_lp_harian_cetak2.setEditable(false);
        txt_bonus_per_lp_harian_cetak2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_bonus_per_lp_harian_cetak2.setText("6000");
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
        jLabel33.setText("Target LP :");

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
        jTextArea1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setText("Notes : \n1. Bonus kecepatan diberikan hanya untuk grup Mandiri / Borong, jika LP yang dikerjakan mencapai target LP minimal\n2. Trainer / Asisten Pengawas tidak dapat bonus kecepatan\n3. Jika level gaji = Training = tidak dapat bonus kecepatan\n4. bobot LP dibulatkan ke bawah kelipatan 0.5.\n5. Gaji Borong hanya untuk level \"MANDIRI CTK BRG\".");
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
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 492, Short.MAX_VALUE))
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
                    .addComponent(jScrollPane11, javax.swing.GroupLayout.DEFAULT_SIZE, 492, Short.MAX_VALUE))
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
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 492, Short.MAX_VALUE))
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
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_minimal_lp_dikerjakan, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_max_lama_inap, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel31)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_upah_reproses, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(CheckBox_BorongOnly)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_search))
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
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_bonus_per_lp_borong_cetak2, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_bonus_per_lp_harian_cetak2, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                                .addComponent(jLabel60)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_bonus_per_lp_borong_cetak1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel59)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_bonus_per_lp_harian_cetak1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel23, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel24, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel27, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(label_total_kpg_cetak)
                                    .addComponent(label_total_data_cetak)
                                    .addComponent(label_total_gaji_cetak)
                                    .addComponent(label_total_bonus_kec_cetak)
                                    .addComponent(label_total_gram_cetak))))
                        .addGap(0, 171, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 475, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_minimal_lp_dikerjakan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_max_lama_inap, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(button_search, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_upah_reproses, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(CheckBox_BorongOnly))
                    .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel60, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_bonus_per_lp_borong_cetak1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel59, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_bonus_per_lp_harian_cetak1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_bonus_per_lp_borong_cetak2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_bonus_per_lp_harian_cetak2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                            .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane1))
                .addContainerGap())
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

        jTabbedPane1.addTab("PENGGAJIAN CETAK", jPanel1);

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));

        txt_search_nama_cetak_baru.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_nama_cetak_baru.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_nama_cetak_baruKeyPressed(evt);
            }
        });

        button_search_baru.setBackground(new java.awt.Color(255, 255, 255));
        button_search_baru.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search_baru.setText("Refresh");
        button_search_baru.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_search_baruActionPerformed(evt);
            }
        });

        jLabel37.setBackground(new java.awt.Color(255, 255, 255));
        jLabel37.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel37.setText("Nama :");

        DateFilter_SetorCetak1_baru.setBackground(new java.awt.Color(255, 255, 255));
        DateFilter_SetorCetak1_baru.setDateFormatString("dd MMMM yyyy");
        DateFilter_SetorCetak1_baru.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        DateFilter_SetorCetak2_baru.setBackground(new java.awt.Color(255, 255, 255));
        DateFilter_SetorCetak2_baru.setDateFormatString("dd MMMM yyyy");
        DateFilter_SetorCetak2_baru.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel38.setBackground(new java.awt.Color(255, 255, 255));
        jLabel38.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel38.setText("Total Data :");

        label_total_data_cetak_baru.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_cetak_baru.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_data_cetak_baru.setText("TOTAL");

        button_export_baru.setBackground(new java.awt.Color(255, 255, 255));
        button_export_baru.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_baru.setText("Export to Excel");
        button_export_baru.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_baruActionPerformed(evt);
            }
        });

        jLabel39.setBackground(new java.awt.Color(255, 255, 255));
        jLabel39.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel39.setText("Total Gaji :");

        label_total_gaji_cetak_baru.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gaji_cetak_baru.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_gaji_cetak_baru.setText("TOTAL");

        jLabel40.setBackground(new java.awt.Color(255, 255, 255));
        jLabel40.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel40.setText("Tgl Setor Cetak :");

        table_data_pegawai_cetak_baru.setAutoCreateRowSorter(true);
        table_data_pegawai_cetak_baru.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "ID Pegawai", "Nama", "Bagian", "Tot LP", "Kpg Upah", "Gram", "Kpg Rprs", "Gaji", "bonus Kec.", "Level"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table_data_pegawai_cetak_baru.getTableHeader().setReorderingAllowed(false);
        jScrollPane7.setViewportView(table_data_pegawai_cetak_baru);

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));

        jLabel41.setBackground(new java.awt.Color(255, 255, 255));
        jLabel41.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel41.setText("Di kerjakan Oleh :");

        label_total_gaji_detail_lp_baru.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gaji_detail_lp_baru.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_gaji_detail_lp_baru.setText("0");

        jLabel42.setBackground(new java.awt.Color(255, 255, 255));
        jLabel42.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel42.setText("Total :");

        label_nama_detail_lp_baru.setBackground(new java.awt.Color(255, 255, 255));
        label_nama_detail_lp_baru.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_nama_detail_lp_baru.setText("Nama");

        Tabel_detail_LP_cetak_baru.setAutoCreateRowSorter(true);
        Tabel_detail_LP_cetak_baru.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Tabel_detail_LP_cetak_baru.setModel(new javax.swing.table.DefaultTableModel(
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
        Tabel_detail_LP_cetak_baru.getTableHeader().setReorderingAllowed(false);
        jScrollPane8.setViewportView(Tabel_detail_LP_cetak_baru);

        jLabel43.setBackground(new java.awt.Color(255, 255, 255));
        jLabel43.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel43.setText("Total LP :");

        label_total_detail_lp_baru.setBackground(new java.awt.Color(255, 255, 255));
        label_total_detail_lp_baru.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_detail_lp_baru.setText("0");

        jLabel44.setBackground(new java.awt.Color(255, 255, 255));
        jLabel44.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel44.setText("Total Box :");

        Tabel_detail_Box_reproses_baru.setAutoCreateRowSorter(true);
        Tabel_detail_Box_reproses_baru.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Tabel_detail_Box_reproses_baru.setModel(new javax.swing.table.DefaultTableModel(
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
        Tabel_detail_Box_reproses_baru.getTableHeader().setReorderingAllowed(false);
        jScrollPane9.setViewportView(Tabel_detail_Box_reproses_baru);

        jLabel45.setBackground(new java.awt.Color(255, 255, 255));
        jLabel45.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel45.setText("Total :");

        label_total_gaji_detail_reproses_baru.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gaji_detail_reproses_baru.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_gaji_detail_reproses_baru.setText("0");

        label_total_detail_reproses_baru.setBackground(new java.awt.Color(255, 255, 255));
        label_total_detail_reproses_baru.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_detail_reproses_baru.setText("0");

        jLabel46.setBackground(new java.awt.Color(255, 255, 255));
        jLabel46.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel46.setText("Box Reproses");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel41)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_nama_detail_lp_baru)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel43)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_detail_lp_baru)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel42)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gaji_detail_lp_baru))
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 663, Short.MAX_VALUE)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel46)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel44)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_detail_reproses_baru)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel45)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gaji_detail_reproses_baru))
                    .addComponent(jScrollPane9))
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_nama_detail_lp_baru, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel42, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_gaji_detail_lp_baru, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_detail_lp_baru, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 351, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel45, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_gaji_detail_reproses_baru, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_detail_reproses_baru, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel46, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel47.setBackground(new java.awt.Color(255, 255, 255));
        jLabel47.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel47.setText("Bonus Kecepatan / LP Harian (Rp.) :");

        jLabel48.setBackground(new java.awt.Color(255, 255, 255));
        jLabel48.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel48.setText("Min LP dikerjakan :");

        txt_minimal_lp_dikerjakan_baru.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_minimal_lp_dikerjakan_baru.setText("4");
        txt_minimal_lp_dikerjakan_baru.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_minimal_lp_dikerjakan_baruKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_minimal_lp_dikerjakan_baruKeyTyped(evt);
            }
        });

        jLabel49.setBackground(new java.awt.Color(255, 255, 255));
        jLabel49.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel49.setText("Max lama inap (hari) :");

        txt_max_lama_inap_baru.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_max_lama_inap_baru.setText("3");
        txt_max_lama_inap_baru.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_max_lama_inap_baruKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_max_lama_inap_baruKeyTyped(evt);
            }
        });

        jLabel50.setBackground(new java.awt.Color(255, 255, 255));
        jLabel50.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel50.setText("Bonus Kecepatan / LP Borong (Rp.) :");

        txt_bonus_per_lp_borong_baru.setEditable(false);
        txt_bonus_per_lp_borong_baru.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_bonus_per_lp_borong_baru.setText("8000");

        txt_bonus_per_lp_harian_baru.setEditable(false);
        txt_bonus_per_lp_harian_baru.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_bonus_per_lp_harian_baru.setText("6000");

        txt_search_bagian_baru.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_bagian_baru.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_bagian_baruKeyPressed(evt);
            }
        });

        jLabel51.setBackground(new java.awt.Color(255, 255, 255));
        jLabel51.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel51.setText("Bagian :");

        CheckBox_BorongOnly_baru.setBackground(new java.awt.Color(255, 255, 255));
        CheckBox_BorongOnly_baru.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        CheckBox_BorongOnly_baru.setSelected(true);
        CheckBox_BorongOnly_baru.setText("Borong Only");

        jLabel52.setBackground(new java.awt.Color(255, 255, 255));
        jLabel52.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel52.setText("Total Bonus Kec :");

        label_total_bonus_kec_cetak_baru.setBackground(new java.awt.Color(255, 255, 255));
        label_total_bonus_kec_cetak_baru.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_bonus_kec_cetak_baru.setText("TOTAL");

        jLabel53.setBackground(new java.awt.Color(255, 255, 255));
        jLabel53.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel53.setText("Total Gram :");

        label_total_gram_cetak_baru.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_cetak_baru.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_gram_cetak_baru.setText("TOTAL");

        label_total_kpg_cetak_baru.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kpg_cetak_baru.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_kpg_cetak_baru.setText("TOTAL");

        jLabel54.setBackground(new java.awt.Color(255, 255, 255));
        jLabel54.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel54.setText("Total Kpg :");

        jLabel55.setBackground(new java.awt.Color(255, 255, 255));
        jLabel55.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel55.setText("Upah Reproses utuh :");

        txt_upah_reproses_baru.setEditable(false);
        txt_upah_reproses_baru.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_upah_reproses_baru.setText("650");

        jLabel56.setBackground(new java.awt.Color(255, 255, 255));
        jLabel56.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel56.setText("Hari Kerja :");

        txt_hari_kerja_baru.setEditable(false);
        txt_hari_kerja_baru.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel57.setBackground(new java.awt.Color(255, 255, 255));
        jLabel57.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel57.setText("Target LP :");

        txt_target_lp_cetak_baru.setEditable(false);
        txt_target_lp_cetak_baru.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel58.setBackground(new java.awt.Color(255, 255, 255));
        jLabel58.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel58.setText("Grade JUMBO, BPU naik :");

        txt_kenaikan_jumbo_baru.setEditable(false);
        txt_kenaikan_jumbo_baru.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_kenaikan_jumbo_baru.setText("5%");

        button_saveDataCetak_baru.setBackground(new java.awt.Color(255, 255, 255));
        button_saveDataCetak_baru.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_saveDataCetak_baru.setText("Save Data");
        button_saveDataCetak_baru.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_saveDataCetak_baruActionPerformed(evt);
            }
        });

        jTextArea2.setEditable(false);
        jTextArea2.setColumns(20);
        jTextArea2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jTextArea2.setLineWrap(true);
        jTextArea2.setRows(5);
        jTextArea2.setText("Notes : \n1. Bonus kecepatan diberikan hanya untuk grup Mandiri / Borong, jika LP yang dikerjakan mencapai target LP minimal\n2. Trainer / Asisten Pengawas tidak dapat bonus kecepatan\n3. Jika level gaji = Training = tidak dapat bonus kecepatan\n4. bobot LP dibulatkan ke bawah kelipatan 0.5.");
        jScrollPane10.setViewportView(jTextArea2);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane7)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addComponent(jLabel48)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_minimal_lp_dikerjakan_baru, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel49)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_max_lama_inap_baru, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel55)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_upah_reproses_baru, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_export_baru)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_search_baru))
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addComponent(jLabel37)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_nama_cetak_baru, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel51)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_bagian_baru, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel40)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(DateFilter_SetorCetak1_baru, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(DateFilter_SetorCetak2_baru, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addComponent(jLabel50)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_bonus_per_lp_borong_baru, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel47)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_bonus_per_lp_harian_baru, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(CheckBox_BorongOnly_baru))
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addComponent(jLabel56)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_hari_kerja_baru, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel57)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_target_lp_cetak_baru, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel58)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_kenaikan_jumbo_baru, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_saveDataCetak_baru))
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabel38, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel39, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel52, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel53, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel54, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(label_total_kpg_cetak_baru)
                                    .addComponent(label_total_data_cetak_baru)
                                    .addComponent(label_total_gaji_cetak_baru)
                                    .addComponent(label_total_bonus_kec_cetak_baru)
                                    .addComponent(label_total_gram_cetak_baru))))
                        .addGap(0, 5, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 345, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_nama_cetak_baru, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel51, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_bagian_baru, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DateFilter_SetorCetak1_baru, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DateFilter_SetorCetak2_baru, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel48, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_minimal_lp_dikerjakan_baru, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel49, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_max_lama_inap_baru, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(button_export_baru, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_search_baru, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_upah_reproses_baru, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel55, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel50, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_bonus_per_lp_borong_baru, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel47, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_bonus_per_lp_harian_baru, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CheckBox_BorongOnly_baru))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel56, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_hari_kerja_baru, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel57, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_target_lp_cetak_baru, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel58, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_kenaikan_jumbo_baru, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_saveDataCetak_baru, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 415, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_total_data_cetak_baru, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_total_gaji_cetak_baru, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_total_bonus_kec_cetak_baru, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel52, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_total_gram_cetak_baru, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel53, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_total_kpg_cetak_baru, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel54, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane10))
                .addContainerGap())
            .addComponent(jPanel9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("PENGGAJIAN CETAK BARU", jPanel7);

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));

        DateFilter_TerimaCetak2.setBackground(new java.awt.Color(255, 255, 255));
        DateFilter_TerimaCetak2.setDateFormatString("dd MMMM yyyy");
        DateFilter_TerimaCetak2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        DateFilter_TerimaCetak1.setBackground(new java.awt.Color(255, 255, 255));
        DateFilter_TerimaCetak1.setDateFormatString("dd MMMM yyyy");
        DateFilter_TerimaCetak1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel13.setText("Nama :");

        button_search_koreksi.setBackground(new java.awt.Color(255, 255, 255));
        button_search_koreksi.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search_koreksi.setText("Refresh");
        button_search_koreksi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_search_koreksiActionPerformed(evt);
            }
        });

        txt_search_nama_koreksi.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_nama_koreksi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_nama_koreksiKeyPressed(evt);
            }
        });

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel14.setText("Tgl Terima Cetak :");

        button_export_gaji_koreksi.setBackground(new java.awt.Color(255, 255, 255));
        button_export_gaji_koreksi.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_gaji_koreksi.setText("Export to Excel");
        button_export_gaji_koreksi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_gaji_koreksiActionPerformed(evt);
            }
        });

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel15.setText("Bonus Kecepatan / LP (Rp.) :");

        txt_bonus_koreksi.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_bonus_koreksi.setText("3000");
        txt_bonus_koreksi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_bonus_koreksiKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_bonus_koreksiKeyTyped(evt);
            }
        });

        table_data_pegawai_koreksi.setAutoCreateRowSorter(true);
        table_data_pegawai_koreksi.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "ID Pegawai", "Nama", "Bagian", "Tot LP", "Kpg", "Gram", "bonus Kec.", "Level Gaji"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.String.class
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
        table_data_pegawai_koreksi.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(table_data_pegawai_koreksi);

        label_total_data_koreksi.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_koreksi.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_data_koreksi.setText("TOTAL");

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel16.setText("Total Data :");

        label_total_bonus_koreksi.setBackground(new java.awt.Color(255, 255, 255));
        label_total_bonus_koreksi.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_bonus_koreksi.setText("TOTAL");

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel17.setText("Total Bonus Kec. :");

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel18.setText("LP di kerjakan Oleh :");

        label_nama_detail_lp_koreksi.setBackground(new java.awt.Color(255, 255, 255));
        label_nama_detail_lp_koreksi.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_nama_detail_lp_koreksi.setText("Nama");

        Tabel_detail_LP_koreksi.setAutoCreateRowSorter(true);
        Tabel_detail_LP_koreksi.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Tabel_detail_LP_koreksi.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No LP", "Grade", "Bulu Upah", "Kpg", "Gram", "Lama Inap", "Bobot LP"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Integer.class, java.lang.Float.class
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
        Tabel_detail_LP_koreksi.getTableHeader().setReorderingAllowed(false);
        jScrollPane5.setViewportView(Tabel_detail_LP_koreksi);

        jLabel20.setBackground(new java.awt.Color(255, 255, 255));
        jLabel20.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel20.setText("Total LP :");

        label_total_detail_lp_koreksi.setBackground(new java.awt.Color(255, 255, 255));
        label_total_detail_lp_koreksi.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_detail_lp_koreksi.setText("0");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_nama_detail_lp_koreksi)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_detail_lp_koreksi))
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 644, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_nama_detail_lp_koreksi, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_detail_lp_koreksi, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 623, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel21.setBackground(new java.awt.Color(255, 255, 255));
        jLabel21.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel21.setText("Min LP dikerjakan :");

        txt_minimal_lp_koreksi_dikerjakan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_minimal_lp_koreksi_dikerjakan.setText("6");
        txt_minimal_lp_koreksi_dikerjakan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_minimal_lp_koreksi_dikerjakanKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_minimal_lp_koreksi_dikerjakanKeyTyped(evt);
            }
        });

        jLabel22.setBackground(new java.awt.Color(255, 255, 255));
        jLabel22.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel22.setText("Max lama inap (hari) :");

        txt_max_lama_inap_koreksi.setEditable(false);
        txt_max_lama_inap_koreksi.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_max_lama_inap_koreksi.setText("2");

        jLabel25.setBackground(new java.awt.Color(255, 255, 255));
        jLabel25.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel25.setText("Total Gram :");

        label_total_gram_koreksi.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_koreksi.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_gram_koreksi.setText("TOTAL");

        label_total_kpg_koreksi.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kpg_koreksi.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_kpg_koreksi.setText("TOTAL");

        jLabel26.setBackground(new java.awt.Color(255, 255, 255));
        jLabel26.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel26.setText("Total Kpg :");

        jLabel34.setBackground(new java.awt.Color(255, 255, 255));
        jLabel34.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel34.setText("Hari Kerja :");

        txt_hari_kerja_koreksi.setEditable(false);
        txt_hari_kerja_koreksi.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel35.setBackground(new java.awt.Color(255, 255, 255));
        jLabel35.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel35.setText("Target LP :");

        txt_target_lp_koreksi.setEditable(false);
        txt_target_lp_koreksi.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        button_saveDataKoreksi.setBackground(new java.awt.Color(255, 255, 255));
        button_saveDataKoreksi.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_saveDataKoreksi.setText("Save Data");
        button_saveDataKoreksi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_saveDataKoreksiActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 681, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_nama_koreksi, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(DateFilter_TerimaCetak1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(DateFilter_TerimaCetak2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_search_koreksi))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel17, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel25, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel26, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(label_total_data_koreksi)
                                    .addComponent(label_total_bonus_koreksi)
                                    .addComponent(label_total_gram_koreksi)
                                    .addComponent(label_total_kpg_koreksi)))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_bonus_koreksi, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel21)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_minimal_lp_koreksi_dikerjakan, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel22)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_max_lama_inap_koreksi, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_export_gaji_koreksi))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel34)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_hari_kerja_koreksi, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel35)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_target_lp_koreksi, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_saveDataKoreksi)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_nama_koreksi, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_search_koreksi, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DateFilter_TerimaCetak1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DateFilter_TerimaCetak2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_bonus_koreksi, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_minimal_lp_koreksi_dikerjakan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_max_lama_inap_koreksi, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_export_gaji_koreksi, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_hari_kerja_koreksi, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_target_lp_koreksi, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_saveDataKoreksi, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_total_data_koreksi, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_total_bonus_koreksi, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_total_gram_koreksi, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_total_kpg_koreksi, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("BONUS KECEPATAN TIM KOREKSI", jPanel4);

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

    private void button_search_koreksiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_koreksiActionPerformed
        // TODO add your handling code here:
        if (DateFilter_TerimaCetak1.getDate() != null && DateFilter_TerimaCetak2.getDate() != null) {
            refreshTable_koreksi();
        } else {
            JOptionPane.showMessageDialog(this, "Harap masukkan Range Tanggal penggajian");
        }
    }//GEN-LAST:event_button_search_koreksiActionPerformed

    private void txt_search_nama_koreksiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_nama_koreksiKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (DateFilter_TerimaCetak1.getDate() != null && DateFilter_TerimaCetak2.getDate() != null) {
                refreshTable_koreksi();
            } else {
                JOptionPane.showMessageDialog(this, "Harap masukkan Range Tanggal penggajian");
            }
        }
    }//GEN-LAST:event_txt_search_nama_koreksiKeyPressed

    private void button_export_gaji_koreksiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_gaji_koreksiActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) table_data_pegawai_koreksi.getModel();
        ExportToExcel.writeToExcel(model, jPanel2);
    }//GEN-LAST:event_button_export_gaji_koreksiActionPerformed

    private void txt_bonus_koreksiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_bonus_koreksiKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (DateFilter_TerimaCetak1.getDate() != null && DateFilter_TerimaCetak2.getDate() != null) {
                refreshTable_koreksi();
            } else {
                JOptionPane.showMessageDialog(this, "Harap masukkan Range Tanggal penggajian");
            }
        }
    }//GEN-LAST:event_txt_bonus_koreksiKeyPressed

    private void txt_bonus_koreksiKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_bonus_koreksiKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_bonus_koreksiKeyTyped

    private void txt_minimal_lp_koreksi_dikerjakanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_minimal_lp_koreksi_dikerjakanKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (DateFilter_TerimaCetak1.getDate() != null && DateFilter_TerimaCetak2.getDate() != null) {
                refreshTable_koreksi();
            } else {
                JOptionPane.showMessageDialog(this, "Harap masukkan Range Tanggal penggajian");
            }
        }
    }//GEN-LAST:event_txt_minimal_lp_koreksi_dikerjakanKeyPressed

    private void txt_minimal_lp_koreksi_dikerjakanKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_minimal_lp_koreksi_dikerjakanKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_minimal_lp_koreksi_dikerjakanKeyTyped

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
                            + table_data_pegawai_cetak.getValueAt(i, 11) + ") "
                            + "ON DUPLICATE KEY UPDATE "
                            + "`gaji_borong`=" + table_data_pegawai_cetak.getValueAt(i, 8) + ", "
                            + "`bonus1_kecepatan`=" + table_data_pegawai_cetak.getValueAt(i, 11);
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

    private void button_saveDataKoreksiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_saveDataKoreksiActionPerformed
        // TODO add your handling code here:
        int dialogResult = JOptionPane.showConfirmDialog(this, "Save " + table_data_pegawai_koreksi.getRowCount() + " data ?", "Warning", 0);
        if (dialogResult == JOptionPane.YES_OPTION) {
            try {
                for (int i = 0; i < table_data_pegawai_koreksi.getRowCount(); i++) {
                    String Query = "INSERT INTO `tb_lembur_rekap`(`id_pegawai`, `tanggal`, `bonus1_kecepatan`) "
                            + "VALUES ("
                            + "'" + table_data_pegawai_koreksi.getValueAt(i, 1).toString() + "',"
                            + "'" + dateFormat.format(DateFilter_TerimaCetak2.getDate()) + "',"
                            + table_data_pegawai_koreksi.getValueAt(i, 7) + ") "
                            + "ON DUPLICATE KEY UPDATE "
                            + "`bonus1_kecepatan`=" + table_data_pegawai_koreksi.getValueAt(i, 7);
                    Utility.db.getConnection().createStatement();
                    Utility.db.getStatement().executeUpdate(Query);
                }
                JOptionPane.showMessageDialog(this, "Data Saved Successfully");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Save Failed !" + e);
                Logger.getLogger(JPanel_GajiCetak.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }//GEN-LAST:event_button_saveDataKoreksiActionPerformed

    private void txt_search_nama_cetak_baruKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_nama_cetak_baruKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (DateFilter_SetorCetak1_baru.getDate() != null && DateFilter_SetorCetak2_baru.getDate() != null) {
                refreshTable_cetak_baru();
            } else {
                JOptionPane.showMessageDialog(this, "Harap masukkan Range Tanggal penggajian");
            }
        }
    }//GEN-LAST:event_txt_search_nama_cetak_baruKeyPressed

    private void button_search_baruActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_baruActionPerformed
        // TODO add your handling code here:
        if (DateFilter_SetorCetak1_baru.getDate() != null && DateFilter_SetorCetak2_baru.getDate() != null) {
            refreshTable_cetak_baru();
        } else {
            JOptionPane.showMessageDialog(this, "Harap masukkan Range Tanggal penggajian");
        }
    }//GEN-LAST:event_button_search_baruActionPerformed

    private void button_export_baruActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_baruActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) table_data_pegawai_cetak_baru.getModel();
        ExportToExcel.writeToExcel(model, jPanel2);
    }//GEN-LAST:event_button_export_baruActionPerformed

    private void txt_minimal_lp_dikerjakan_baruKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_minimal_lp_dikerjakan_baruKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (DateFilter_SetorCetak1_baru.getDate() != null && DateFilter_SetorCetak2_baru.getDate() != null) {
                refreshTable_cetak_baru();
            } else {
                JOptionPane.showMessageDialog(this, "Harap masukkan Range Tanggal penggajian");
            }
        }
    }//GEN-LAST:event_txt_minimal_lp_dikerjakan_baruKeyPressed

    private void txt_minimal_lp_dikerjakan_baruKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_minimal_lp_dikerjakan_baruKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_minimal_lp_dikerjakan_baruKeyTyped

    private void txt_search_bagian_baruKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_bagian_baruKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (DateFilter_SetorCetak1_baru.getDate() != null && DateFilter_SetorCetak2_baru.getDate() != null) {
                refreshTable_cetak_baru();
            } else {
                JOptionPane.showMessageDialog(this, "Harap masukkan Range Tanggal penggajian");
            }
        }
    }//GEN-LAST:event_txt_search_bagian_baruKeyPressed

    private void button_saveDataCetak_baruActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_saveDataCetak_baruActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_button_saveDataCetak_baruActionPerformed

    private void txt_max_lama_inap_baruKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_max_lama_inap_baruKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (DateFilter_SetorCetak1_baru.getDate() != null && DateFilter_SetorCetak2_baru.getDate() != null) {
                refreshTable_cetak_baru();
            } else {
                JOptionPane.showMessageDialog(this, "Harap masukkan Range Tanggal penggajian");
            }
        }
    }//GEN-LAST:event_txt_max_lama_inap_baruKeyPressed

    private void txt_max_lama_inap_baruKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_max_lama_inap_baruKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_max_lama_inap_baruKeyTyped

    private void txt_bonus_per_lp_borong_cetak1KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_bonus_per_lp_borong_cetak1KeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_bonus_per_lp_borong_cetak1KeyTyped

    private void txt_bonus_per_lp_harian_cetak1KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_bonus_per_lp_harian_cetak1KeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_bonus_per_lp_harian_cetak1KeyTyped


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox CheckBox_BorongOnly;
    private javax.swing.JCheckBox CheckBox_BorongOnly_baru;
    private com.toedter.calendar.JDateChooser DateFilter_SetorCetak1;
    private com.toedter.calendar.JDateChooser DateFilter_SetorCetak1_baru;
    private com.toedter.calendar.JDateChooser DateFilter_SetorCetak2;
    private com.toedter.calendar.JDateChooser DateFilter_SetorCetak2_baru;
    private com.toedter.calendar.JDateChooser DateFilter_TerimaCetak1;
    private com.toedter.calendar.JDateChooser DateFilter_TerimaCetak2;
    private javax.swing.JTable Tabel_detail_Box_reproses;
    private javax.swing.JTable Tabel_detail_Box_reproses_baru;
    private javax.swing.JTable Tabel_detail_LP_cetak1;
    private javax.swing.JTable Tabel_detail_LP_cetak2;
    private javax.swing.JTable Tabel_detail_LP_cetak_baru;
    private javax.swing.JTable Tabel_detail_LP_koreksi;
    private javax.swing.JButton button_export;
    private javax.swing.JButton button_export_baru;
    private javax.swing.JButton button_export_gaji_koreksi;
    public static javax.swing.JButton button_saveDataCetak;
    public static javax.swing.JButton button_saveDataCetak_baru;
    public static javax.swing.JButton button_saveDataKoreksi;
    public static javax.swing.JButton button_search;
    public static javax.swing.JButton button_search_baru;
    public static javax.swing.JButton button_search_koreksi;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JLabel label_nama_detail_lp1;
    private javax.swing.JLabel label_nama_detail_lp2;
    private javax.swing.JLabel label_nama_detail_lp_baru;
    private javax.swing.JLabel label_nama_detail_lp_koreksi;
    private javax.swing.JLabel label_total_bonus_kec_cetak;
    private javax.swing.JLabel label_total_bonus_kec_cetak_baru;
    private javax.swing.JLabel label_total_bonus_koreksi;
    private javax.swing.JLabel label_total_data_cetak;
    private javax.swing.JLabel label_total_data_cetak_baru;
    private javax.swing.JLabel label_total_data_koreksi;
    private javax.swing.JLabel label_total_detail_lp1;
    private javax.swing.JLabel label_total_detail_lp2;
    private javax.swing.JLabel label_total_detail_lp_baru;
    private javax.swing.JLabel label_total_detail_lp_koreksi;
    private javax.swing.JLabel label_total_detail_reproses;
    private javax.swing.JLabel label_total_detail_reproses_baru;
    private javax.swing.JLabel label_total_gaji_cetak;
    private javax.swing.JLabel label_total_gaji_cetak_baru;
    private javax.swing.JLabel label_total_gaji_detail_lp1;
    private javax.swing.JLabel label_total_gaji_detail_lp2;
    private javax.swing.JLabel label_total_gaji_detail_lp_baru;
    private javax.swing.JLabel label_total_gaji_detail_reproses;
    private javax.swing.JLabel label_total_gaji_detail_reproses_baru;
    private javax.swing.JLabel label_total_gram_cetak;
    private javax.swing.JLabel label_total_gram_cetak_baru;
    private javax.swing.JLabel label_total_gram_koreksi;
    private javax.swing.JLabel label_total_kpg_cetak;
    private javax.swing.JLabel label_total_kpg_cetak_baru;
    private javax.swing.JLabel label_total_kpg_koreksi;
    private javax.swing.JTable table_data_pegawai_cetak;
    private javax.swing.JTable table_data_pegawai_cetak_baru;
    private javax.swing.JTable table_data_pegawai_koreksi;
    private javax.swing.JTextField txt_bonus_koreksi;
    private javax.swing.JTextField txt_bonus_per_lp_borong_baru;
    private javax.swing.JTextField txt_bonus_per_lp_borong_cetak1;
    private javax.swing.JTextField txt_bonus_per_lp_borong_cetak2;
    private javax.swing.JTextField txt_bonus_per_lp_harian_baru;
    private javax.swing.JTextField txt_bonus_per_lp_harian_cetak1;
    private javax.swing.JTextField txt_bonus_per_lp_harian_cetak2;
    private javax.swing.JTextField txt_hari_kerja;
    private javax.swing.JTextField txt_hari_kerja_baru;
    private javax.swing.JTextField txt_hari_kerja_koreksi;
    private javax.swing.JTextField txt_kenaikan_jumbo;
    private javax.swing.JTextField txt_kenaikan_jumbo_baru;
    private javax.swing.JTextField txt_max_lama_inap;
    private javax.swing.JTextField txt_max_lama_inap_baru;
    private javax.swing.JTextField txt_max_lama_inap_koreksi;
    private javax.swing.JTextField txt_minimal_lp_dikerjakan;
    private javax.swing.JTextField txt_minimal_lp_dikerjakan_baru;
    private javax.swing.JTextField txt_minimal_lp_koreksi_dikerjakan;
    private javax.swing.JTextField txt_search_bagian;
    private javax.swing.JTextField txt_search_bagian_baru;
    private javax.swing.JTextField txt_search_nama_cetak;
    private javax.swing.JTextField txt_search_nama_cetak_baru;
    private javax.swing.JTextField txt_search_nama_koreksi;
    private javax.swing.JTextField txt_target_lp_cetak;
    private javax.swing.JTextField txt_target_lp_cetak_baru;
    private javax.swing.JTextField txt_target_lp_koreksi;
    private javax.swing.JTextField txt_upah_reproses;
    private javax.swing.JTextField txt_upah_reproses_baru;
    // End of variables declaration//GEN-END:variables
}
