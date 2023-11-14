package waleta_system.BahanBaku;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.ParseException;
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
import waleta_system.Class.Utility;
import waleta_system.Class.ExportToExcel;

public class JPanel_Dokumen_KH extends javax.swing.JPanel {

    
    String sql = null;
    ResultSet rs;
    Date date = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    DecimalFormat decimalFormat = new DecimalFormat();

    public JPanel_Dokumen_KH() {
        initComponents();
    }

    public void init() {
        try {
            
            
            refreshTable_dataKH();
            ComboBox_rsb.removeAllItems();
            sql = "SELECT `no_registrasi`, `nama_rumah_burung` FROM `tb_rumah_burung` WHERE CHAR_LENGTH(`no_registrasi`) IN (3, 4)";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_rsb.addItem(rs.getString("no_registrasi"));
            }
            Table_dokumen_KH.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent event) {
                    if (!event.getValueIsAdjusting() && Table_dokumen_KH.getSelectedRow() != -1) {
                        int i = Table_dokumen_KH.getSelectedRow();
                        txt_kode.setText(Table_dokumen_KH.getValueAt(i, 0).toString());
                        ComboBox_rsb.setSelectedItem(Table_dokumen_KH.getValueAt(i, 1).toString());
                        try {
                            Date_KH.setDate(dateFormat.parse(Table_dokumen_KH.getValueAt(i, 3).toString()));
                        } catch (ParseException ex) {
                            Logger.getLogger(JPanel_Dokumen_KH.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        txt_kapasitas.setText(Table_dokumen_KH.getValueAt(i, 4).toString());
                        try {
                            txt_kh12.setText(Table_dokumen_KH.getValueAt(i, 5).toString());
                        } catch (NullPointerException ex) {
                            txt_kh12.setText(null);
                        }
                        try {
                            txt_kh14.setText(Table_dokumen_KH.getValueAt(i, 6).toString());
                        } catch (NullPointerException ex) {
                            txt_kh14.setText(null);
                        }
                    }
                }
            });
        } catch (Exception ex) {
            Logger.getLogger(JPanel_Dokumen_KH.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_dataKH() {
        try {
            
            DefaultTableModel model = (DefaultTableModel) Table_dokumen_KH.getModel();
            model.setRowCount(0);
            sql = "SELECT TB_KH.`kode_kh`, `no_registrasi_rsb`, `tb_rumah_burung`.`nama_rumah_burung`, `tanggal_kh`, `kapasitas`, `kh12`, `kh14`, TB_BAKU.`berat_grading`, TB_BAKU.`berat_lp`, TB_BAKU.`berat_cmp`, TB_BAKU.`berat_jual_baku`, TB_BAKU.`stok_kartu`, TB_STOK_GBJ.`stok_gbj`, TB_STOK_TERJUAL.`stok_terjual`, ((`kapasitas`*1000)-(IFNULL(TB_BAKU.`berat_jual_baku`, 0)+IFNULL(TB_STOK_TERJUAL.`stok_terjual`, 0))) AS 'sisa_kapasitas'\n"
                    + "FROM `tb_dokumen_kh` TB_KH \n"
                    + "LEFT JOIN (SELECT `kode_kh`, SUM(`berat_real`) AS 'berat_grading', SUM(TB_LP.`berat_lp`) AS 'berat_lp', SUM(TB_JUAL_BAKU.`berat_jual_baku`) AS 'berat_jual_baku', SUM(TB_CMP.`berat_cmp`) AS 'berat_cmp', SUM(`berat_real`-(IFNULL(TB_LP.`berat_lp`, 0)+IFNULL(TB_JUAL_BAKU.`berat_jual_baku`, 0)+IFNULL(TB_CMP.`berat_cmp`, 0))) AS 'stok_kartu'\n"
                    + "FROM `tb_bahan_baku_masuk_cheat` \n"
                    + "LEFT JOIN (SELECT `no_kartu_waleta`, SUM(`berat_basah`) AS 'berat_lp' FROM `tb_laporan_produksi` GROUP BY `no_kartu_waleta`) TB_LP ON `tb_bahan_baku_masuk_cheat`.`no_kartu_waleta` = TB_LP.`no_kartu_waleta` \n"
                    + "LEFT JOIN (SELECT `no_kartu_waleta`, SUM(`total_berat_keluar`) AS 'berat_jual_baku' FROM `tb_bahan_baku_keluar` WHERE 1 GROUP BY `no_kartu_waleta`) TB_JUAL_BAKU ON `tb_bahan_baku_masuk_cheat`.`no_kartu_waleta` = TB_JUAL_BAKU.`no_kartu_waleta` \n"
                    + "LEFT JOIN (SELECT `tb_grading_bahan_baku`.`no_kartu_waleta`, SUM(`gram`) AS 'berat_cmp' \n"
                    + "FROM `tb_kartu_cmp_detail` LEFT JOIN `tb_grading_bahan_baku` ON `tb_kartu_cmp_detail`.`no_grading` = `tb_grading_bahan_baku`.`no_grading`\n"
                    + "WHERE 1 GROUP BY `tb_grading_bahan_baku`.`no_kartu_waleta`) TB_CMP ON `tb_bahan_baku_masuk_cheat`.`no_kartu_waleta` = TB_CMP.`no_kartu_waleta` \n"
                    + "WHERE `kode_kh` IS NOT NULL GROUP BY `kode_kh`) TB_BAKU ON TB_KH.`kode_kh` = TB_BAKU.`kode_kh`\n"
                    + "LEFT JOIN (SELECT `kode_kh`, SUM(`berat`) AS 'stok_gbj' FROM `tb_box_bahan_jadi` LEFT JOIN `tb_box_packing` ON `tb_box_bahan_jadi`.`no_box` = `tb_box_packing`.`no_box` WHERE `lokasi_terakhir` IN ('GRADING', 'PACKING', 'RE-PROSES', 'TREATMENT', 'DIPINJAM') AND (`tb_box_packing`.`status` IN ('PENDING', 'STOK', 'RETUR') OR `tb_box_packing`.`status` IS NULL) AND `kode_kh` IS NOT NULL\n"
                    + "GROUP BY `kode_kh`) TB_STOK_GBJ ON TB_KH.`kode_kh` = TB_STOK_GBJ.`kode_kh`\n"
                    + "LEFT JOIN (SELECT `kode_kh`, SUM(`berat`) AS 'stok_terjual'\n"
                    + "FROM `tb_box_bahan_jadi` \n"
                    + "LEFT JOIN `tb_box_packing` ON `tb_box_bahan_jadi`.`no_box` = `tb_box_packing`.`no_box`\n"
                    + "WHERE `tb_box_packing`.`status` = 'OUT' AND `kode_kh` IS NOT NULL\n"
                    + "GROUP BY `kode_kh`) TB_STOK_TERJUAL ON TB_KH.`kode_kh` = TB_STOK_TERJUAL.`kode_kh`\n"
                    + "LEFT JOIN `tb_rumah_burung` ON TB_KH.`no_registrasi_rsb` = `tb_rumah_burung`.`no_registrasi`\n"
                    + "WHERE (TB_KH.`kode_kh` LIKE '%" + txt_search_kh.getText() + "%' OR `kh12` LIKE '%" + txt_search_kh.getText() + "%' OR `kh14` LIKE '%" + txt_search_kh.getText() + "%') AND `no_registrasi_rsb` LIKE '%" + txt_search_rsb.getText() + "%' ";
            if (DateFilter1.getDate() != null && DateFilter2.getDate() != null) {
                sql = "SELECT TB_KH.`kode_kh`, `no_registrasi_rsb`, `tb_rumah_burung`.`nama_rumah_burung`, `tanggal_kh`, `kapasitas`, `kh12`, `kh14`, TB_BAKU.`berat_grading`, TB_BAKU.`berat_lp`, TB_BAKU.`berat_cmp`, TB_BAKU.`berat_jual_baku`, TB_BAKU.`stok_kartu`, TB_STOK_GBJ.`stok_gbj`, TB_STOK_TERJUAL.`stok_terjual`, ((`kapasitas`*1000)-(IFNULL(TB_BAKU.`berat_jual_baku`, 0)+IFNULL(TB_STOK_TERJUAL.`stok_terjual`, 0))) AS 'sisa_kapasitas'\n"
                        + "FROM `tb_dokumen_kh` TB_KH \n"
                        + "LEFT JOIN (SELECT `kode_kh`, SUM(`berat_real`) AS 'berat_grading', SUM(TB_LP.`berat_lp`) AS 'berat_lp', SUM(TB_JUAL_BAKU.`berat_jual_baku`) AS 'berat_jual_baku', SUM(TB_CMP.`berat_cmp`) AS 'berat_cmp', SUM(`berat_real`-(IFNULL(TB_LP.`berat_lp`, 0)+IFNULL(TB_JUAL_BAKU.`berat_jual_baku`, 0)+IFNULL(TB_CMP.`berat_cmp`, 0))) AS 'stok_kartu'\n"
                        + "FROM `tb_bahan_baku_masuk_cheat` \n"
                        + "LEFT JOIN (SELECT `no_kartu_waleta`, SUM(`berat_basah`) AS 'berat_lp' FROM `tb_laporan_produksi` GROUP BY `no_kartu_waleta`) TB_LP ON `tb_bahan_baku_masuk_cheat`.`no_kartu_waleta` = TB_LP.`no_kartu_waleta` \n"
                        + "LEFT JOIN (SELECT `no_kartu_waleta`, SUM(`total_berat_keluar`) AS 'berat_jual_baku' FROM `tb_bahan_baku_keluar` WHERE 1 GROUP BY `no_kartu_waleta`) TB_JUAL_BAKU ON `tb_bahan_baku_masuk_cheat`.`no_kartu_waleta` = TB_JUAL_BAKU.`no_kartu_waleta` \n"
                        + "LEFT JOIN (SELECT `tb_grading_bahan_baku`.`no_kartu_waleta`, SUM(`gram`) AS 'berat_cmp' \n"
                        + "FROM `tb_kartu_cmp_detail` LEFT JOIN `tb_grading_bahan_baku` ON `tb_kartu_cmp_detail`.`no_grading` = `tb_grading_bahan_baku`.`no_grading`\n"
                        + "WHERE 1 GROUP BY `tb_grading_bahan_baku`.`no_kartu_waleta`) TB_CMP ON `tb_bahan_baku_masuk_cheat`.`no_kartu_waleta` = TB_CMP.`no_kartu_waleta` \n"
                        + "WHERE `kode_kh` IS NOT NULL GROUP BY `kode_kh`) TB_BAKU ON TB_KH.`kode_kh` = TB_BAKU.`kode_kh`\n"
                        + "LEFT JOIN (SELECT `kode_kh`, SUM(`berat`) AS 'stok_gbj' FROM `tb_box_bahan_jadi` LEFT JOIN `tb_box_packing` ON `tb_box_bahan_jadi`.`no_box` = `tb_box_packing`.`no_box` WHERE `lokasi_terakhir` IN ('GRADING', 'PACKING', 'RE-PROSES', 'TREATMENT', 'DIPINJAM') AND (`tb_box_packing`.`status` IN ('PENDING', 'STOK', 'RETUR') OR `tb_box_packing`.`status` IS NULL) AND `kode_kh` IS NOT NULL\n"
                        + "GROUP BY `kode_kh`) TB_STOK_GBJ ON TB_KH.`kode_kh` = TB_STOK_GBJ.`kode_kh`\n"
                        + "LEFT JOIN (SELECT `kode_kh`, SUM(`berat`) AS 'stok_terjual'\n"
                        + "FROM `tb_box_bahan_jadi` \n"
                        + "LEFT JOIN `tb_box_packing` ON `tb_box_bahan_jadi`.`no_box` = `tb_box_packing`.`no_box`\n"
                        + "WHERE `tb_box_packing`.`status` = 'OUT' AND `kode_kh` IS NOT NULL\n"
                        + "GROUP BY `kode_kh`) TB_STOK_TERJUAL ON TB_KH.`kode_kh` = TB_STOK_TERJUAL.`kode_kh`\n"
                        + "LEFT JOIN `tb_rumah_burung` ON TB_KH.`no_registrasi_rsb` = `tb_rumah_burung`.`no_registrasi`\n"
                        + "WHERE (TB_KH.`kode_kh` LIKE '%" + txt_search_kh.getText() + "%' OR `kh12` LIKE '%" + txt_search_kh.getText() + "%' OR `kh14` LIKE '%" + txt_search_kh.getText() + "%') AND `no_registrasi_rsb` LIKE '%" + txt_search_rsb.getText() + "%' "
                        + "AND `tanggal_kh` BETWEEN '" + dateFormat.format(DateFilter1.getDate()) + "' AND '" + dateFormat.format(DateFilter2.getDate()) + "'";
            }

            rs = Utility.db.getStatement().executeQuery(sql);
//            System.out.println(sql);
            Object[] row = new Object[15];
            double total_berat_kh = 0, total_berat_grading = 0, total_berat_lp_kcmp = 0, total_berat_baku_jual = 0, total_berat_penjualan_bjd = 0, total_berat_stok = 0;
            while (rs.next()) {
                row[0] = rs.getString("kode_kh");
                row[1] = rs.getString("no_registrasi_rsb");
                row[2] = rs.getString("nama_rumah_burung");
                row[3] = rs.getDate("tanggal_kh");
                row[4] = rs.getFloat("kapasitas");
                total_berat_kh = total_berat_kh + rs.getDouble("kapasitas");
                row[5] = rs.getString("kh12");
                row[6] = rs.getString("kh14");
                row[7] = Math.round(rs.getDouble("berat_grading") / 100d) / 10d;
                total_berat_grading = total_berat_grading + rs.getDouble("berat_grading");
                row[8] = Math.round((rs.getDouble("berat_lp") + rs.getDouble("berat_cmp")) / 100d) / 10d;
                total_berat_lp_kcmp = total_berat_lp_kcmp + rs.getDouble("berat_lp") + rs.getDouble("berat_cmp");
                row[9] = Math.round(rs.getDouble("berat_jual_baku") / 100d) / 10d;
                total_berat_baku_jual = total_berat_baku_jual + rs.getDouble("berat_jual_baku");
                row[10] = Math.round(rs.getDouble("stok_gbj") / 100d) / 10d;
                row[11] = Math.round(rs.getDouble("stok_terjual") / 100d) / 10d;
                total_berat_penjualan_bjd = total_berat_penjualan_bjd + rs.getDouble("stok_terjual");
                row[12] = rs.getDouble("kapasitas") - (Math.round((rs.getDouble("stok_terjual") + rs.getDouble("berat_jual_baku")) / 100d) / 10d);
                row[13] = Math.round(rs.getDouble("stok_kartu") / 100d) / 10d;
                total_berat_stok = total_berat_stok + rs.getDouble("stok_kartu");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_dokumen_KH);
            decimalFormat.setMaximumFractionDigits(1);
            int rowData = Table_dokumen_KH.getRowCount();
            label_total_data.setText(Integer.toString(rowData));
            label_total_berat_kh.setText(decimalFormat.format(total_berat_kh) + " Kg");
            label_total_berat_grading.setText(decimalFormat.format(total_berat_grading / 1000d) + " Kg");
            label_total_berat_lp_kcmp.setText(decimalFormat.format(total_berat_lp_kcmp / 1000d) + " Kg");
            label_total_berat_baku_jual.setText(decimalFormat.format(total_berat_baku_jual / 1000d) + " Kg");
            label_total_berat_penjualan_bjd.setText(decimalFormat.format(total_berat_penjualan_bjd / 1000d) + " Kg");
            label_total_berat_stok.setText(decimalFormat.format(total_berat_stok / 1000d) + " Kg");
        } catch (Exception ex) {
            Logger.getLogger(JPanel_Dokumen_KH.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_LaporanStok() {
        try {
            String sql_lp = null, sql_jual = null, sql_cmp = null;
            int berat_lp = 0, keping_lp = 0, berat_keluar = 0, keping_keluar = 0, berat_cmp = 0, keping_cmp = 0;
            int total_berat_real = 0, total_berat_awal = 0, total_lp_kcmp = 0, total_jual_baku = 0, total_stok = 0;
            ResultSet rs_lp, rs_keluar, rs_cmp;
            DefaultTableModel model = (DefaultTableModel) table_laporan_stok_baku.getModel();
            model.setRowCount(0);
            String data_stok_per_tgl = "";
            if (Date_FilterStok.getDate() != null) {
                data_stok_per_tgl = " AND `tgl_masuk`<='" + dateFormat.format(Date_FilterStok.getDate()) + "' ";
            }
            String filter_tgl_masuk_stok = "";
            if (Date_tgl_masuk_stok1.getDate() != null && Date_tgl_masuk_stok2.getDate() != null) {
                filter_tgl_masuk_stok = "AND `tgl_masuk` BETWEEN '" + dateFormat.format(Date_tgl_masuk_stok1.getDate()) + "' AND '" + dateFormat.format(Date_tgl_masuk_stok2.getDate()) + "' ";
            }
            String showcmp = " AND `tb_bahan_baku_masuk_cheat`.`no_kartu_waleta` NOT LIKE '%CMP%' ";
            if (CheckBox_showCMP.isSelected()) {
                showcmp = "";
            }
            String kode_kh = " AND `tb_bahan_baku_masuk_cheat`.`kode_kh` LIKE '%" + txt_search_kode_kh.getText() + "%' ";
            if (txt_search_kode_kh.getText() == null || txt_search_kode_kh.getText().equals("")) {
                kode_kh = "";
            }
            sql = "SELECT `tb_bahan_baku_masuk_cheat`.`no_kartu_waleta`, `tb_bahan_baku_masuk_cheat`.`no_registrasi`, `tb_supplier`.`nama_supplier`, `tb_rumah_burung`.`nama_rumah_burung`, `tanggal_kh`, `tgl_masuk`, `tgl_panen`, `tgl_grading`, `tgl_timbang`, `kadar_air_bahan_baku`, `berat_awal`, `berat_real`, `tb_bahan_baku_masuk_cheat`.`kode_kh`, `tb_dokumen_kh`.`kapasitas` \n"
                    + "FROM `tb_bahan_baku_masuk_cheat` \n"
                    + "LEFT JOIN `tb_rumah_burung` ON `tb_bahan_baku_masuk_cheat`.`no_registrasi`=`tb_rumah_burung`.`no_registrasi`\n"
                    + "LEFT JOIN `tb_dokumen_kh` ON `tb_bahan_baku_masuk_cheat`.`kode_kh`=`tb_dokumen_kh`.`kode_kh`\n"
                    + "LEFT JOIN `tb_supplier` ON `tb_bahan_baku_masuk_cheat`.`kode_supplier`=`tb_supplier`.`kode_supplier` \n"
                    + "LEFT JOIN `tb_grading_bahan_baku_cheat` ON `tb_bahan_baku_masuk_cheat`.`no_kartu_waleta` = `tb_grading_bahan_baku_cheat`.`no_kartu_waleta`\n"
                    + "WHERE `tb_bahan_baku_masuk_cheat`.`no_kartu_waleta` LIKE '%" + txt_search_data_bahan_baku.getText() + "%' "
                    + showcmp
                    + data_stok_per_tgl
                    + kode_kh
                    + filter_tgl_masuk_stok
                    + "GROUP BY `tb_bahan_baku_masuk_cheat`.`no_kartu_waleta`"
                    + "ORDER BY `tgl_masuk`";

            PreparedStatement pst = Utility.db.getConnection().prepareStatement(sql);
            rs = pst.executeQuery();
            Object[] row = new Object[20];
            while (rs.next()) {
                if (Date_FilterStok.getDate() != null) {
                    sql_lp = "SELECT SUM(`tb_laporan_produksi`.`berat_basah`) AS 'berat_lp', SUM(`tb_laporan_produksi`.`jumlah_keping`) AS 'keping_lp' FROM `tb_laporan_produksi` "
                            + "WHERE `tb_laporan_produksi`.`no_kartu_waleta` = '" + rs.getString("no_kartu_waleta") + "' AND `tanggal_lp`<='" + dateFormat.format(Date_FilterStok.getDate()) + "'";
                    sql_jual = "SELECT SUM(`tb_bahan_baku_keluar`.`total_berat_keluar`) AS 'berat_keluar', SUM(`tb_bahan_baku_keluar`.`total_keping_keluar`) AS 'keping_keluar' FROM `tb_bahan_baku_keluar` "
                            + "LEFT JOIN `tb_bahan_baku_keluar1` ON `tb_bahan_baku_keluar`.`kode_pengeluaran` = `tb_bahan_baku_keluar1`.`kode_pengeluaran`"
                            + "WHERE `tb_bahan_baku_keluar`.`no_kartu_waleta` = '" + rs.getString("no_kartu_waleta") + "' AND `tgl_keluar`<='" + dateFormat.format(Date_FilterStok.getDate()) + "'";
                    sql_cmp = "SELECT SUM(`keping`) AS 'keping_cmp', SUM(`gram`) AS 'berat_cmp' \n"
                            + "FROM `tb_kartu_cmp_detail` LEFT JOIN `tb_kartu_cmp` ON `tb_kartu_cmp_detail`.`kode_kartu_cmp` = `tb_kartu_cmp`.`kode_kartu_cmp`"
                            + "LEFT JOIN `tb_grading_bahan_baku` ON `tb_kartu_cmp_detail`.`no_grading` = `tb_grading_bahan_baku`.`no_grading`\n"
                            + "WHERE `tb_grading_bahan_baku`.`no_kartu_waleta` = '" + rs.getString("no_kartu_waleta") + "' AND `tanggal`<='" + dateFormat.format(Date_FilterStok.getDate()) + "'";
                } else {
                    sql_lp = "SELECT SUM(`tb_laporan_produksi`.`berat_basah`) AS 'berat_lp', SUM(`tb_laporan_produksi`.`jumlah_keping`) AS 'keping_lp' FROM `tb_laporan_produksi` "
                            + "WHERE `tb_laporan_produksi`.`no_kartu_waleta` = '" + rs.getString("no_kartu_waleta") + "'";
                    sql_jual = "SELECT SUM(`tb_bahan_baku_keluar`.`total_berat_keluar`) AS 'berat_keluar', SUM(`tb_bahan_baku_keluar`.`total_keping_keluar`) AS 'keping_keluar' FROM `tb_bahan_baku_keluar` "
                            + "WHERE `tb_bahan_baku_keluar`.`no_kartu_waleta` = '" + rs.getString("no_kartu_waleta") + "'";
                    sql_cmp = "SELECT SUM(`keping`) AS 'keping_cmp', SUM(`gram`) AS 'berat_cmp' FROM `tb_kartu_cmp_detail`\n"
                            + "LEFT JOIN `tb_grading_bahan_baku` ON `tb_kartu_cmp_detail`.`no_grading` = `tb_grading_bahan_baku`.`no_grading`\n"
                            + "WHERE `tb_grading_bahan_baku`.`no_kartu_waleta` = '" + rs.getString("no_kartu_waleta") + "'";
                }

                rs_lp = Utility.db.getStatement().executeQuery(sql_lp);
                if (rs_lp.next()) {
                    berat_lp = rs_lp.getInt("berat_lp");
                    keping_lp = rs_lp.getInt("keping_lp");
                } else {
                    berat_lp = 0;
                    keping_lp = 0;
                }

                rs_keluar = Utility.db.getStatement().executeQuery(sql_jual);
                if (rs_keluar.next()) {
                    berat_keluar = rs_keluar.getInt("berat_keluar");
                    keping_keluar = rs_keluar.getInt("keping_keluar");
                } else {
                    berat_keluar = 0;
                    keping_keluar = 0;
                }

                rs_cmp = Utility.db.getStatement().executeQuery(sql_cmp);
                if (rs_cmp.next()) {
                    berat_cmp = rs_cmp.getInt("berat_cmp");
                    keping_cmp = rs_cmp.getInt("keping_cmp");
                } else {
                    berat_cmp = 0;
                    keping_cmp = 0;
                }

                row[0] = rs.getString("no_kartu_waleta");
                row[1] = rs.getString("nama_rumah_burung") + " / " + rs.getString("no_registrasi");
                row[2] = rs.getDate("tanggal_kh");
                row[3] = rs.getDate("tgl_masuk");
                row[4] = rs.getDate("tgl_panen");
                row[5] = rs.getDate("tgl_grading");
                row[6] = rs.getDate("tgl_timbang");
                row[7] = rs.getInt("berat_awal");
                row[8] = rs.getInt("berat_real");
                row[9] = (berat_lp + berat_cmp);
                row[10] = berat_keluar;
                row[11] = rs.getInt("berat_real") - (berat_lp + berat_keluar + berat_cmp);
                row[12] = rs.getFloat("kadar_air_bahan_baku");
                row[13] = rs.getFloat("kapasitas");
                row[14] = rs.getString("kode_kh");
                model.addRow(row);
                total_berat_awal = total_berat_awal + rs.getInt("berat_awal");
                total_berat_real = total_berat_real + rs.getInt("berat_real");
                total_lp_kcmp = total_lp_kcmp + (berat_lp + berat_cmp);
                total_jual_baku = total_jual_baku + berat_keluar;

            }
            total_stok = total_berat_real - (total_lp_kcmp + total_jual_baku);
            ColumnsAutoSizer.sizeColumnsToFit(table_laporan_stok_baku);
            label_total_berat_awal1.setText(decimalFormat.format(total_berat_awal / 1000d) + " Kg");
            label_total_berat_grading1.setText(decimalFormat.format(total_berat_real / 1000d) + " Kg");
            label_total_berat_lp_kcmp1.setText(decimalFormat.format(total_lp_kcmp / 1000d) + " Kg");
            label_total_berat_baku_jual1.setText(decimalFormat.format(total_jual_baku / 1000d) + " Kg");
            label_total_berat_stok1.setText(decimalFormat.format(total_stok / 1000d) + " Kg");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Dokumen_KH.class.getName()).log(Level.SEVERE, null, ex);
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
        jPanel_Dokumen_KH = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        Table_dokumen_KH = new javax.swing.JTable();
        jPanel_operation_customer = new javax.swing.JPanel();
        button_insert = new javax.swing.JButton();
        button_update = new javax.swing.JButton();
        button_clear = new javax.swing.JButton();
        button_delete = new javax.swing.JButton();
        txt_kh12 = new javax.swing.JTextField();
        txt_kapasitas = new javax.swing.JTextField();
        label_nama_customer_baku = new javax.swing.JLabel();
        label_alamat_customer_baku = new javax.swing.JLabel();
        label_noTelp_customer_baku = new javax.swing.JLabel();
        label_nama_customer_baku1 = new javax.swing.JLabel();
        txt_kode = new javax.swing.JTextField();
        ComboBox_rsb = new javax.swing.JComboBox<>();
        label_noTelp_customer_baku1 = new javax.swing.JLabel();
        label_noTelp_customer_baku2 = new javax.swing.JLabel();
        txt_kh14 = new javax.swing.JTextField();
        Date_KH = new com.toedter.calendar.JDateChooser();
        jLabel12 = new javax.swing.JLabel();
        label_total_data = new javax.swing.JLabel();
        txt_search_kh = new javax.swing.JTextField();
        button_search = new javax.swing.JButton();
        button_export = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        DateFilter1 = new com.toedter.calendar.JDateChooser();
        DateFilter2 = new com.toedter.calendar.JDateChooser();
        jPanel1 = new javax.swing.JPanel();
        label_noTelp_customer_baku3 = new javax.swing.JLabel();
        label_noTelp_customer_baku4 = new javax.swing.JLabel();
        label_noTelp_customer_baku5 = new javax.swing.JLabel();
        label_noTelp_customer_baku6 = new javax.swing.JLabel();
        label_noTelp_customer_baku7 = new javax.swing.JLabel();
        label_noTelp_customer_baku8 = new javax.swing.JLabel();
        label_total_berat_stok = new javax.swing.JLabel();
        label_total_berat_penjualan_bjd = new javax.swing.JLabel();
        label_total_berat_baku_jual = new javax.swing.JLabel();
        label_total_berat_lp_kcmp = new javax.swing.JLabel();
        label_total_berat_grading = new javax.swing.JLabel();
        label_total_berat_kh = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txt_search_rsb = new javax.swing.JTextField();
        button_lihat_dokumen_kh14 = new javax.swing.JButton();
        button_lihat_dokumen_kh12 = new javax.swing.JButton();
        jPanel_Laporan_Stok = new javax.swing.JPanel();
        jScrollPane11 = new javax.swing.JScrollPane();
        table_laporan_stok_baku = new javax.swing.JTable();
        button_search_data_bahan_baku = new javax.swing.JButton();
        txt_search_data_bahan_baku = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        Date_FilterStok = new com.toedter.calendar.JDateChooser();
        jLabel27 = new javax.swing.JLabel();
        Date_tgl_masuk_stok1 = new com.toedter.calendar.JDateChooser();
        Date_tgl_masuk_stok2 = new com.toedter.calendar.JDateChooser();
        CheckBox_showCMP = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        label_noTelp_customer_baku10 = new javax.swing.JLabel();
        label_noTelp_customer_baku11 = new javax.swing.JLabel();
        label_noTelp_customer_baku12 = new javax.swing.JLabel();
        label_noTelp_customer_baku13 = new javax.swing.JLabel();
        label_total_berat_stok1 = new javax.swing.JLabel();
        label_total_berat_baku_jual1 = new javax.swing.JLabel();
        label_total_berat_lp_kcmp1 = new javax.swing.JLabel();
        label_total_berat_grading1 = new javax.swing.JLabel();
        label_noTelp_customer_baku14 = new javax.swing.JLabel();
        label_total_berat_awal1 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        txt_search_kode_kh = new javax.swing.JTextField();

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N

        jPanel_Dokumen_KH.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_Dokumen_KH.setPreferredSize(new java.awt.Dimension(1366, 701));

        Table_dokumen_KH.setAutoCreateRowSorter(true);
        Table_dokumen_KH.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode", "No registrasi RSB", "Nama RSB", "Tgl KH", "Kapasitas (Kg)", "No KH 12", "No KH 14", "Kg Grading Baku", "Kg LP & KCMP", "Kg Jual Baku", "Kg Stok BJD", "Kg Terjual", "Sisa Kapasitas KH", "Kg Stok Baku"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Float.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
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
        Table_dokumen_KH.getTableHeader().setReorderingAllowed(false);
        jScrollPane8.setViewportView(Table_dokumen_KH);

        jPanel_operation_customer.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_operation_customer.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Operation", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Yu Gothic", 0, 12))); // NOI18N
        jPanel_operation_customer.setName("aah"); // NOI18N

        button_insert.setBackground(new java.awt.Color(255, 255, 255));
        button_insert.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_insert.setText("insert");
        button_insert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_insertActionPerformed(evt);
            }
        });

        button_update.setBackground(new java.awt.Color(255, 255, 255));
        button_update.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_update.setText("Update");
        button_update.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_updateActionPerformed(evt);
            }
        });

        button_clear.setBackground(new java.awt.Color(255, 255, 255));
        button_clear.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_clear.setText("Clear Text");
        button_clear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_clearActionPerformed(evt);
            }
        });

        button_delete.setBackground(new java.awt.Color(255, 255, 255));
        button_delete.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_delete.setText("Delete");
        button_delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_deleteActionPerformed(evt);
            }
        });

        txt_kh12.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        txt_kapasitas.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        label_nama_customer_baku.setBackground(new java.awt.Color(255, 255, 255));
        label_nama_customer_baku.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_nama_customer_baku.setText("Kode :");

        label_alamat_customer_baku.setBackground(new java.awt.Color(255, 255, 255));
        label_alamat_customer_baku.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_alamat_customer_baku.setText("Tanggal KH :");

        label_noTelp_customer_baku.setBackground(new java.awt.Color(255, 255, 255));
        label_noTelp_customer_baku.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_noTelp_customer_baku.setText("Kapasitas (Kg) :");

        label_nama_customer_baku1.setBackground(new java.awt.Color(255, 255, 255));
        label_nama_customer_baku1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_nama_customer_baku1.setText("No Registrasi RB :");

        txt_kode.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        label_noTelp_customer_baku1.setBackground(new java.awt.Color(255, 255, 255));
        label_noTelp_customer_baku1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_noTelp_customer_baku1.setText("No KH-12 :");

        label_noTelp_customer_baku2.setBackground(new java.awt.Color(255, 255, 255));
        label_noTelp_customer_baku2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_noTelp_customer_baku2.setText("No KH-14 :");

        txt_kh14.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Date_KH.setDateFormatString("dd MMM yyyy");

        javax.swing.GroupLayout jPanel_operation_customerLayout = new javax.swing.GroupLayout(jPanel_operation_customer);
        jPanel_operation_customer.setLayout(jPanel_operation_customerLayout);
        jPanel_operation_customerLayout.setHorizontalGroup(
            jPanel_operation_customerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_operation_customerLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_operation_customerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_operation_customerLayout.createSequentialGroup()
                        .addComponent(label_nama_customer_baku, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_kode, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_operation_customerLayout.createSequentialGroup()
                        .addGroup(jPanel_operation_customerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(label_nama_customer_baku1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_alamat_customer_baku, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_operation_customerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ComboBox_rsb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Date_KH, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel_operation_customerLayout.createSequentialGroup()
                        .addComponent(label_noTelp_customer_baku, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_kapasitas, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_operation_customerLayout.createSequentialGroup()
                        .addComponent(label_noTelp_customer_baku1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_kh12, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_operation_customerLayout.createSequentialGroup()
                        .addComponent(label_noTelp_customer_baku2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_kh14, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_operation_customerLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(button_update)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_insert)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_delete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_clear)))
                .addContainerGap())
        );
        jPanel_operation_customerLayout.setVerticalGroup(
            jPanel_operation_customerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_operation_customerLayout.createSequentialGroup()
                .addGroup(jPanel_operation_customerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_nama_customer_baku, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_kode, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_customerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_nama_customer_baku1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_rsb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_customerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(label_alamat_customer_baku, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_KH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_customerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_noTelp_customer_baku, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_kapasitas, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_customerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_noTelp_customer_baku1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_kh12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_customerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_noTelp_customer_baku2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_kh14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel_operation_customerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_update, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_insert, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_delete, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_clear, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jLabel12.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel12.setText("Total Data :");

        label_total_data.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        label_total_data.setText("TOTAL");

        txt_search_kh.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_kh.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_khKeyPressed(evt);
            }
        });

        button_search.setBackground(new java.awt.Color(255, 255, 255));
        button_search.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search.setText("Search");
        button_search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_searchActionPerformed(evt);
            }
        });

        button_export.setBackground(new java.awt.Color(255, 255, 255));
        button_export.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export.setText("Export To Excel");
        button_export.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_exportActionPerformed(evt);
            }
        });

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("No Dokumen :");

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("Tanggal KH :");

        DateFilter1.setBackground(new java.awt.Color(255, 255, 255));
        DateFilter1.setDateFormatString("dd MMM yyyy");
        DateFilter1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        DateFilter2.setBackground(new java.awt.Color(255, 255, 255));
        DateFilter2.setDateFormatString("dd MMM yyyy");
        DateFilter2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "TOTAL", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 12))); // NOI18N

        label_noTelp_customer_baku3.setBackground(new java.awt.Color(255, 255, 255));
        label_noTelp_customer_baku3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_noTelp_customer_baku3.setText("Kepasitas KH :");

        label_noTelp_customer_baku4.setBackground(new java.awt.Color(255, 255, 255));
        label_noTelp_customer_baku4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_noTelp_customer_baku4.setText("Berat Grading :");

        label_noTelp_customer_baku5.setBackground(new java.awt.Color(255, 255, 255));
        label_noTelp_customer_baku5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_noTelp_customer_baku5.setText("Berat LP & KCMP :");

        label_noTelp_customer_baku6.setBackground(new java.awt.Color(255, 255, 255));
        label_noTelp_customer_baku6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_noTelp_customer_baku6.setText("Berat Stok :");

        label_noTelp_customer_baku7.setBackground(new java.awt.Color(255, 255, 255));
        label_noTelp_customer_baku7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_noTelp_customer_baku7.setText("Berat Baku Terjual :");

        label_noTelp_customer_baku8.setBackground(new java.awt.Color(255, 255, 255));
        label_noTelp_customer_baku8.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_noTelp_customer_baku8.setText("Berat Penjualan BJD :");

        label_total_berat_stok.setBackground(new java.awt.Color(255, 255, 255));
        label_total_berat_stok.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_berat_stok.setText("0");

        label_total_berat_penjualan_bjd.setBackground(new java.awt.Color(255, 255, 255));
        label_total_berat_penjualan_bjd.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_berat_penjualan_bjd.setText("0");

        label_total_berat_baku_jual.setBackground(new java.awt.Color(255, 255, 255));
        label_total_berat_baku_jual.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_berat_baku_jual.setText("0");

        label_total_berat_lp_kcmp.setBackground(new java.awt.Color(255, 255, 255));
        label_total_berat_lp_kcmp.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_berat_lp_kcmp.setText("0");

        label_total_berat_grading.setBackground(new java.awt.Color(255, 255, 255));
        label_total_berat_grading.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_berat_grading.setText("0");

        label_total_berat_kh.setBackground(new java.awt.Color(255, 255, 255));
        label_total_berat_kh.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_berat_kh.setText("0");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(label_noTelp_customer_baku3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_berat_kh))
                            .addComponent(label_noTelp_customer_baku8))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_berat_penjualan_bjd))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(label_noTelp_customer_baku6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_berat_stok))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(label_noTelp_customer_baku7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_berat_baku_jual))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(label_noTelp_customer_baku5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_berat_lp_kcmp))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(label_noTelp_customer_baku4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_berat_grading)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_noTelp_customer_baku3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_berat_kh, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_noTelp_customer_baku4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_berat_grading, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_noTelp_customer_baku5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_berat_lp_kcmp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_noTelp_customer_baku7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_berat_baku_jual, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_noTelp_customer_baku8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_berat_penjualan_bjd, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_noTelp_customer_baku6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_berat_stok, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("RSB :");

        txt_search_rsb.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_rsb.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_rsbKeyPressed(evt);
            }
        });

        button_lihat_dokumen_kh14.setBackground(new java.awt.Color(255, 255, 255));
        button_lihat_dokumen_kh14.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_lihat_dokumen_kh14.setText("Lihat Dokumen KH14");
        button_lihat_dokumen_kh14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_lihat_dokumen_kh14ActionPerformed(evt);
            }
        });

        button_lihat_dokumen_kh12.setBackground(new java.awt.Color(255, 255, 255));
        button_lihat_dokumen_kh12.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_lihat_dokumen_kh12.setText("Lihat Dokumen KH12");
        button_lihat_dokumen_kh12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_lihat_dokumen_kh12ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_Dokumen_KHLayout = new javax.swing.GroupLayout(jPanel_Dokumen_KH);
        jPanel_Dokumen_KH.setLayout(jPanel_Dokumen_KHLayout);
        jPanel_Dokumen_KHLayout.setHorizontalGroup(
            jPanel_Dokumen_KHLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Dokumen_KHLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_Dokumen_KHLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_Dokumen_KHLayout.createSequentialGroup()
                        .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 991, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_Dokumen_KHLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel_operation_customer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel_Dokumen_KHLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_kh, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_rsb, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(DateFilter1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(DateFilter2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_search)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_lihat_dokumen_kh14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_lihat_dokumen_kh12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_export)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_data)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel_Dokumen_KHLayout.setVerticalGroup(
            jPanel_Dokumen_KHLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Dokumen_KHLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_Dokumen_KHLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DateFilter1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_kh, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_search, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DateFilter2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12)
                    .addComponent(label_total_data)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_rsb, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_lihat_dokumen_kh14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_lihat_dokumen_kh12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_Dokumen_KHLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 598, Short.MAX_VALUE)
                    .addGroup(jPanel_Dokumen_KHLayout.createSequentialGroup()
                        .addComponent(jPanel_operation_customer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Data Dokumen Karantina Hewan", jPanel_Dokumen_KH);

        jPanel_Laporan_Stok.setBackground(new java.awt.Color(255, 255, 255));

        table_laporan_stok_baku.setAutoCreateRowSorter(true);
        table_laporan_stok_baku.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_laporan_stok_baku.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No Kartu Waleta", "RSB", "Tgl KH", "Tgl Masuk", "Tgl Panen", "Tgl Grading", "Tgl Timbang", "Berat Awal", "Berat Real", "Gram LP/KCMP", "Gram Jual Baku", "Stok Gram", "Kadar Air (%)", "Kapasitas KH", "KH"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Object.class, java.lang.Float.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table_laporan_stok_baku.getTableHeader().setReorderingAllowed(false);
        jScrollPane11.setViewportView(table_laporan_stok_baku);

        button_search_data_bahan_baku.setBackground(new java.awt.Color(255, 255, 255));
        button_search_data_bahan_baku.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search_data_bahan_baku.setText("Refresh");
        button_search_data_bahan_baku.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_search_data_bahan_bakuActionPerformed(evt);
            }
        });

        txt_search_data_bahan_baku.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_data_bahan_baku.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_data_bahan_bakuKeyPressed(evt);
            }
        });

        jLabel23.setBackground(new java.awt.Color(255, 255, 255));
        jLabel23.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel23.setText("No Kartu Waleta :");

        jLabel26.setBackground(new java.awt.Color(255, 255, 255));
        jLabel26.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel26.setText("Data per Tanggal :");

        Date_FilterStok.setBackground(new java.awt.Color(255, 255, 255));
        Date_FilterStok.setDate(new Date());
        Date_FilterStok.setDateFormatString("dd MMMM yyyy");
        Date_FilterStok.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel27.setBackground(new java.awt.Color(255, 255, 255));
        jLabel27.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel27.setText("Tgl Masuk :");

        Date_tgl_masuk_stok1.setBackground(new java.awt.Color(255, 255, 255));
        Date_tgl_masuk_stok1.setDateFormatString("dd MMMM yyyy");
        Date_tgl_masuk_stok1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Date_tgl_masuk_stok2.setBackground(new java.awt.Color(255, 255, 255));
        Date_tgl_masuk_stok2.setDateFormatString("dd MMMM yyyy");
        Date_tgl_masuk_stok2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        CheckBox_showCMP.setBackground(new java.awt.Color(255, 255, 255));
        CheckBox_showCMP.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        CheckBox_showCMP.setText("Show CMP");

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "TOTAL", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 12))); // NOI18N

        label_noTelp_customer_baku10.setBackground(new java.awt.Color(255, 255, 255));
        label_noTelp_customer_baku10.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_noTelp_customer_baku10.setText("Berat Awal :");

        label_noTelp_customer_baku11.setBackground(new java.awt.Color(255, 255, 255));
        label_noTelp_customer_baku11.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_noTelp_customer_baku11.setText("Berat LP & KCMP :");

        label_noTelp_customer_baku12.setBackground(new java.awt.Color(255, 255, 255));
        label_noTelp_customer_baku12.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_noTelp_customer_baku12.setText("Berat Stok :");

        label_noTelp_customer_baku13.setBackground(new java.awt.Color(255, 255, 255));
        label_noTelp_customer_baku13.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_noTelp_customer_baku13.setText("Berat Baku Terjual :");

        label_total_berat_stok1.setBackground(new java.awt.Color(255, 255, 255));
        label_total_berat_stok1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_berat_stok1.setText("0");

        label_total_berat_baku_jual1.setBackground(new java.awt.Color(255, 255, 255));
        label_total_berat_baku_jual1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_berat_baku_jual1.setText("0");

        label_total_berat_lp_kcmp1.setBackground(new java.awt.Color(255, 255, 255));
        label_total_berat_lp_kcmp1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_berat_lp_kcmp1.setText("0");

        label_total_berat_grading1.setBackground(new java.awt.Color(255, 255, 255));
        label_total_berat_grading1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_berat_grading1.setText("0");

        label_noTelp_customer_baku14.setBackground(new java.awt.Color(255, 255, 255));
        label_noTelp_customer_baku14.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_noTelp_customer_baku14.setText("Berat Grading :");

        label_total_berat_awal1.setBackground(new java.awt.Color(255, 255, 255));
        label_total_berat_awal1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_berat_awal1.setText("0");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(label_noTelp_customer_baku12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_berat_stok1))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(label_noTelp_customer_baku13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_berat_baku_jual1))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(label_noTelp_customer_baku11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_berat_lp_kcmp1))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(label_noTelp_customer_baku10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_berat_awal1))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(label_noTelp_customer_baku14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_berat_grading1)))
                .addContainerGap(1199, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_noTelp_customer_baku10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_berat_awal1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_noTelp_customer_baku14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_berat_grading1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_noTelp_customer_baku11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_berat_lp_kcmp1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_noTelp_customer_baku13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_berat_baku_jual1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_noTelp_customer_baku12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_berat_stok1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel24.setBackground(new java.awt.Color(255, 255, 255));
        jLabel24.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel24.setText("Kode KH :");

        txt_search_kode_kh.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_kode_kh.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_kode_khKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_Laporan_StokLayout = new javax.swing.GroupLayout(jPanel_Laporan_Stok);
        jPanel_Laporan_Stok.setLayout(jPanel_Laporan_StokLayout);
        jPanel_Laporan_StokLayout.setHorizontalGroup(
            jPanel_Laporan_StokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Laporan_StokLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_Laporan_StokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_Laporan_StokLayout.createSequentialGroup()
                        .addComponent(jLabel23)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_data_bahan_baku, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel24)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_kode_kh, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel27)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_tgl_masuk_stok1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_tgl_masuk_stok2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel26)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_FilterStok, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(CheckBox_showCMP)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_search_data_bahan_baku)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane11))
                .addContainerGap())
        );
        jPanel_Laporan_StokLayout.setVerticalGroup(
            jPanel_Laporan_StokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Laporan_StokLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_Laporan_StokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_Laporan_StokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_search_data_bahan_baku, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_FilterStok, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel_Laporan_StokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_search_kode_kh, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_tgl_masuk_stok1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_tgl_masuk_stok2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel_Laporan_StokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(button_search_data_bahan_baku, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(CheckBox_showCMP, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane11, javax.swing.GroupLayout.DEFAULT_SIZE, 419, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Laporan Stok Bahan Baku", jPanel_Laporan_Stok);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1364, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 677, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void button_insertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_insertActionPerformed
        // TODO add your handling code here:
        Boolean Check = true;
        try {
            String kapasitas = txt_kapasitas.getText();
            if (txt_kapasitas.getText().equals("") || txt_kapasitas.getText() == null) {
                kapasitas = "0";
            }
            String kh12 = "'" + txt_kh12.getText() + "'";
            if (txt_kh12.getText().equals("") || txt_kh12.getText() == null) {
                kh12 = "'-'";
            }
            String kh14 = "'" + txt_kh14.getText() + "'";
            if (txt_kh14.getText().equals("") || txt_kh14.getText() == null) {
                kh14 = "'-'";
            }
            if (Date_KH.getDate() == null) {
                JOptionPane.showMessageDialog(this, "Tanggal tidak boleh kosong");
            } else if (kh12.equals("NULL") && kh14.equals("NULL")) {
                JOptionPane.showMessageDialog(this, "no KH harus di isi minimal 1 (KH12 / KH14)");
            } else if (kapasitas.equals("0")) {
                JOptionPane.showMessageDialog(this, "Jumlah kapasitas harus diatas 0 kg");
            } else {
                String kode = ComboBox_rsb.getSelectedItem().toString() + "-" + new SimpleDateFormat("yyMMdd").format(Date_KH.getDate());
                sql = "INSERT INTO `tb_dokumen_kh`(`kode_kh`, `no_registrasi_rsb`, `tanggal_kh`, `kapasitas`, `kh12`, `kh14`) "
                        + "VALUES ('" + kode + "','" + ComboBox_rsb.getSelectedItem().toString() + "','" + dateFormat.format(Date_KH.getDate()) + "','" + kapasitas + "'," + kh12 + "," + kh14 + ")";
                Utility.db.getConnection().createStatement();
                if ((Utility.db.getStatement().executeUpdate(sql)) == 1) {
                    JOptionPane.showMessageDialog(this, "data Saved !");
                    refreshTable_dataKH();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed!");
                }
                button_clear.doClick();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_Dokumen_KH.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_insertActionPerformed

    private void button_updateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_updateActionPerformed
        // TODO add your handling code here:
        int j = Table_dokumen_KH.getSelectedRow();
        try {
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Please Select Row Data that you want to Update !");
            } else {
                String kapasitas = txt_kapasitas.getText();
                if (txt_kapasitas.getText().equals("") || txt_kapasitas.getText() == null) {
                    kapasitas = "0";
                }
                String kh12 = "'" + txt_kh12.getText() + "'";
                if (txt_kh12.getText().equals("") || txt_kh12.getText() == null) {
                    kh12 = "'-'";
                }
                String kh14 = "'" + txt_kh14.getText() + "'";
                if (txt_kh14.getText().equals("") || txt_kh14.getText() == null) {
                    kh14 = "'-'";
                }
                if (Date_KH.getDate() == null) {
                    JOptionPane.showMessageDialog(this, "Tanggal tidak boleh kosong");
                } else if (kh12.equals("NULL") && kh14.equals("NULL")) {
                    JOptionPane.showMessageDialog(this, "no KH harus di isi minimal 1 (KH12 / KH14)");
                } else if (kapasitas.equals("0")) {
                    JOptionPane.showMessageDialog(this, "Jumlah kapasitas harus diatas 0 kg");
                } else {
                    String kode = ComboBox_rsb.getSelectedItem().toString() + "-" + new SimpleDateFormat("yyMMdd").format(Date_KH.getDate());
                    sql = "UPDATE `tb_dokumen_kh` SET "
                            + "`kode_kh` = '" + kode + "', "
                            + "`no_registrasi_rsb` = '" + ComboBox_rsb.getSelectedItem().toString() + "', "
                            + "`tanggal_kh` = '" + dateFormat.format(Date_KH.getDate()) + "', "
                            + "`kapasitas` = " + kapasitas + ", "
                            + "`kh12` = " + kh12 + ", "
                            + "`kh14` = " + kh14 + " "
                            + "WHERE `kode_kh` = '" + Table_dokumen_KH.getValueAt(j, 0).toString() + "'";
                    Utility.db.getConnection().createStatement();
                    if ((Utility.db.getStatement().executeUpdate(sql)) == 1) {
                        JOptionPane.showMessageDialog(this, "data Saved !");
                        refreshTable_dataKH();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed!");
                    }
                    button_clear.doClick();
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_Dokumen_KH.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_updateActionPerformed

    private void button_clearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_clearActionPerformed
        // TODO add your handling code here:
        txt_kode.setText(null);
        ComboBox_rsb.setSelectedIndex(0);
        Date_KH.setDate(null);
        txt_kapasitas.setText(null);
        txt_kh12.setText(null);
        txt_kh14.setText(null);
    }//GEN-LAST:event_button_clearActionPerformed

    private void button_deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_deleteActionPerformed
        // TODO add your handling code here:
        try {
            int j = Table_dokumen_KH.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Please Select Data that you want to Delete !");
            } else {
                int dialogResult = JOptionPane.showConfirmDialog(this, "Are Sure You Want to Delete this Data?", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    sql = "DELETE FROM `tb_dokumen_kh` WHERE `kode_kh` = '" + txt_kode.getText() + "'";
                    Utility.db.getConnection().createStatement();
                    if ((Utility.db.getStatement().executeUpdate(sql)) == 1) {
                        JOptionPane.showMessageDialog(this, "data Deleted !");
                        refreshTable_dataKH();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed!");
                    }
                    button_clear.doClick();
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_Dokumen_KH.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_deleteActionPerformed

    private void txt_search_khKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_khKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_dataKH();
        }
    }//GEN-LAST:event_txt_search_khKeyPressed

    private void button_searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_searchActionPerformed
        // TODO add your handling code here:
        refreshTable_dataKH();
    }//GEN-LAST:event_button_searchActionPerformed

    private void button_exportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_exportActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Table_dokumen_KH.getModel();
        ExportToExcel.writeToExcel(model, jPanel_Dokumen_KH);
    }//GEN-LAST:event_button_exportActionPerformed

    private void button_search_data_bahan_bakuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_data_bahan_bakuActionPerformed
        // TODO add your handling code here:
        refreshTable_LaporanStok();
    }//GEN-LAST:event_button_search_data_bahan_bakuActionPerformed

    private void txt_search_data_bahan_bakuKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_data_bahan_bakuKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_LaporanStok();
        }
    }//GEN-LAST:event_txt_search_data_bahan_bakuKeyPressed

    private void txt_search_rsbKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_rsbKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_search_rsbKeyPressed

    private void txt_search_kode_khKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_kode_khKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_search_kode_khKeyPressed

    private void button_lihat_dokumen_kh14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_lihat_dokumen_kh14ActionPerformed
        // TODO add your handling code here:
        int x = Table_dokumen_KH.getSelectedRow();
        if (x < 0) {
            JOptionPane.showMessageDialog(this, "Pilih salah satu data !");
        } else {
            String file_name = Table_dokumen_KH.getValueAt(x, 0).toString();
            try {
                Runtime rt = Runtime.getRuntime();
                Process pr = rt.exec("explorer.exe \\\\192.168.10.2\\Shared Folder\\DOKUMEN SISTEM\\2_(KH-14)_Sertifikat_Pelepasan_Karantina_Hewan_Baku\\" + file_name + ".pdf");
            } catch (IOException ex) {
                Logger.getLogger(JPanel_Dokumen_KH.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_button_lihat_dokumen_kh14ActionPerformed

    private void button_lihat_dokumen_kh12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_lihat_dokumen_kh12ActionPerformed
        // TODO add your handling code here:
        int x = Table_dokumen_KH.getSelectedRow();
        if (x < 0) {
            JOptionPane.showMessageDialog(this, "Pilih salah satu data !");
        } else {
            String file_name = Table_dokumen_KH.getValueAt(x, 0).toString();
            try {
                Runtime rt = Runtime.getRuntime();
                Process pr = rt.exec("explorer.exe \\\\192.168.10.2\\Shared Folder\\DOKUMEN SISTEM\\8_(KH-12)_Sertifikat_Sanitasi_Produk_Hewan_Baku\\" + file_name + ".pdf");
            } catch (IOException ex) {
                Logger.getLogger(JPanel_Dokumen_KH.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_button_lihat_dokumen_kh12ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox CheckBox_showCMP;
    private javax.swing.JComboBox<String> ComboBox_rsb;
    private com.toedter.calendar.JDateChooser DateFilter1;
    private com.toedter.calendar.JDateChooser DateFilter2;
    private com.toedter.calendar.JDateChooser Date_FilterStok;
    private com.toedter.calendar.JDateChooser Date_KH;
    private com.toedter.calendar.JDateChooser Date_tgl_masuk_stok1;
    private com.toedter.calendar.JDateChooser Date_tgl_masuk_stok2;
    private javax.swing.JTable Table_dokumen_KH;
    private javax.swing.JButton button_clear;
    public javax.swing.JButton button_delete;
    private javax.swing.JButton button_export;
    public javax.swing.JButton button_insert;
    private javax.swing.JButton button_lihat_dokumen_kh12;
    private javax.swing.JButton button_lihat_dokumen_kh14;
    private javax.swing.JButton button_search;
    private javax.swing.JButton button_search_data_bahan_baku;
    public javax.swing.JButton button_update;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel_Dokumen_KH;
    private javax.swing.JPanel jPanel_Laporan_Stok;
    private javax.swing.JPanel jPanel_operation_customer;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel label_alamat_customer_baku;
    private javax.swing.JLabel label_nama_customer_baku;
    private javax.swing.JLabel label_nama_customer_baku1;
    private javax.swing.JLabel label_noTelp_customer_baku;
    private javax.swing.JLabel label_noTelp_customer_baku1;
    private javax.swing.JLabel label_noTelp_customer_baku10;
    private javax.swing.JLabel label_noTelp_customer_baku11;
    private javax.swing.JLabel label_noTelp_customer_baku12;
    private javax.swing.JLabel label_noTelp_customer_baku13;
    private javax.swing.JLabel label_noTelp_customer_baku14;
    private javax.swing.JLabel label_noTelp_customer_baku2;
    private javax.swing.JLabel label_noTelp_customer_baku3;
    private javax.swing.JLabel label_noTelp_customer_baku4;
    private javax.swing.JLabel label_noTelp_customer_baku5;
    private javax.swing.JLabel label_noTelp_customer_baku6;
    private javax.swing.JLabel label_noTelp_customer_baku7;
    private javax.swing.JLabel label_noTelp_customer_baku8;
    private javax.swing.JLabel label_total_berat_awal1;
    private javax.swing.JLabel label_total_berat_baku_jual;
    private javax.swing.JLabel label_total_berat_baku_jual1;
    private javax.swing.JLabel label_total_berat_grading;
    private javax.swing.JLabel label_total_berat_grading1;
    private javax.swing.JLabel label_total_berat_kh;
    private javax.swing.JLabel label_total_berat_lp_kcmp;
    private javax.swing.JLabel label_total_berat_lp_kcmp1;
    private javax.swing.JLabel label_total_berat_penjualan_bjd;
    private javax.swing.JLabel label_total_berat_stok;
    private javax.swing.JLabel label_total_berat_stok1;
    private javax.swing.JLabel label_total_data;
    private javax.swing.JTable table_laporan_stok_baku;
    private javax.swing.JTextField txt_kapasitas;
    private javax.swing.JTextField txt_kh12;
    private javax.swing.JTextField txt_kh14;
    private javax.swing.JTextField txt_kode;
    private javax.swing.JTextField txt_search_data_bahan_baku;
    private javax.swing.JTextField txt_search_kh;
    private javax.swing.JTextField txt_search_kode_kh;
    private javax.swing.JTextField txt_search_rsb;
    // End of variables declaration//GEN-END:variables
}
