package waleta_system.Finance;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import waleta_system.Class.Utility;

public class JPanel_LaporanProduksi_Keuangan extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public JPanel_LaporanProduksi_Keuangan() {
        initComponents();
        decimalFormat = Utility.DecimalFormatUS(decimalFormat);
    }

    public void init() {
    }

    public void refresh_data() {
        try {
            //BAHAN BAKU---------------------------------------------------------------------------------------------------------------------------------------------------------------
            int kpg_persediaan_awal_bahan_baku = 0, gram_persediaan_awal_bahan_baku = 0;
            double harga_persediaan_awal_bahan_baku = 0;
            int kpg_pembelian_bahan_baku = 0, gram_pembelian_bahan_baku = 0;
            double harga_pembelian_bahan_baku = 0;
            int kpg_penjualan_bahan_baku = 0, gram_penjualan_bahan_baku = 0;
            double harga_penjualan_bahan_baku = 0;
            int kpg_bahan_baku_untuk_produksi = 0, gram_bahan_baku_untuk_produksi = 0;
            double harga_bahan_baku_untuk_produksi = 0;
            int kpg_bahan_baku_LP = 0, gram_bahan_baku_LP = 0;
            double harga_bahan_baku_LP = 0;
            int kpg_stok_bahan_baku = 0, gram_stok_bahan_baku = 0;
            double harga_stok_bahan_baku = 0;
            double biaya_tenagakerja_baku = 0, biaya_overhead_baku = 0, total_biaya_baku = 0;
            //PERSEDIAAN AWAL BAHAN BAKU
            sql = "SELECT SUM(`jumlah_keping`) AS 'keping', SUM(`total_berat`) AS 'berat', SUM(`harga_bahanbaku`*`total_berat`) AS 'harga' \n"
                    + "FROM `tb_grading_bahan_baku` "
                    + "LEFT JOIN `tb_bahan_baku_masuk` ON `tb_grading_bahan_baku`.`no_kartu_waleta` = `tb_bahan_baku_masuk`.`no_kartu_waleta`\n"
                    + "LEFT JOIN `tb_kartu_cmp` ON `tb_grading_bahan_baku`.`no_kartu_waleta`=`tb_kartu_cmp`.`kode_kartu_cmp`"
                    + "WHERE `tb_kartu_cmp`.`kode_kartu_cmp` IS NULL "
                    + "AND `tb_bahan_baku_masuk`.`tgl_masuk` < '" + dateFormat.format(Date_filter1.getDate()) + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                kpg_pembelian_bahan_baku = rs.getInt("keping");
                gram_pembelian_bahan_baku = rs.getInt("berat");
                harga_pembelian_bahan_baku = rs.getDouble("harga");
            }

            sql = "SELECT SUM(`total_keping_keluar`) AS 'keping', SUM(`total_berat_keluar`) AS 'berat', SUM(`harga_bahanbaku`*`total_berat_keluar`) AS 'harga' \n"
                    + "FROM `tb_bahan_baku_keluar` "
                    + "LEFT JOIN `tb_grading_bahan_baku` ON (`tb_grading_bahan_baku`.`no_kartu_waleta` = `tb_bahan_baku_keluar`.`no_kartu_waleta` AND `tb_grading_bahan_baku`.`kode_grade` = `tb_bahan_baku_keluar`.`kode_grade`)\n"
                    + "LEFT JOIN `tb_bahan_baku_keluar1` ON `tb_bahan_baku_keluar`.`kode_pengeluaran` = `tb_bahan_baku_keluar1`.`kode_pengeluaran`\n"
                    + "WHERE `tb_bahan_baku_keluar1`.`tgl_keluar` < '" + dateFormat.format(Date_filter1.getDate()) + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                kpg_penjualan_bahan_baku = rs.getInt("keping");
                gram_penjualan_bahan_baku = rs.getInt("berat");
                harga_penjualan_bahan_baku = rs.getDouble("harga");
            }
            sql = "SELECT SUM(`berat_basah`) AS 'berat', SUM(`tb_laporan_produksi`.`jumlah_keping`) AS 'keping', SUM(`berat_basah`*`tb_grading_bahan_baku`.`harga_bahanbaku`) AS 'harga' FROM `tb_laporan_produksi` \n"
                    + "LEFT JOIN `tb_grading_bahan_baku` ON (`tb_laporan_produksi`.`no_kartu_waleta` = `tb_grading_bahan_baku`.`no_kartu_waleta` AND `tb_laporan_produksi`.`kode_grade` = `tb_grading_bahan_baku`.`kode_grade`)\n"
                    + "WHERE `tanggal_lp` < '" + dateFormat.format(Date_filter1.getDate()) + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                kpg_bahan_baku_LP = rs.getInt("keping");
                gram_bahan_baku_LP = rs.getInt("berat");
                harga_bahan_baku_LP = rs.getDouble("harga");
            }
            kpg_persediaan_awal_bahan_baku = kpg_pembelian_bahan_baku - (kpg_penjualan_bahan_baku + kpg_bahan_baku_LP);
            gram_persediaan_awal_bahan_baku = gram_pembelian_bahan_baku - (gram_penjualan_bahan_baku + gram_bahan_baku_LP);
            harga_persediaan_awal_bahan_baku = harga_pembelian_bahan_baku - (harga_penjualan_bahan_baku + harga_bahan_baku_LP);

            label_kpg_1.setText(decimalFormat.format(kpg_persediaan_awal_bahan_baku));
            label_gram_1.setText(decimalFormat.format(gram_persediaan_awal_bahan_baku));
            label_harga_1.setText(decimalFormat.format(harga_persediaan_awal_bahan_baku));

            //BIAYA KIRIM BAKU, BTK BAKU, OVERHEAD BAKU
            sql = "SELECT SUM(IF((`Kategori1` = 'Biaya Bahan Baku' AND `jenis_pengeluaran` = 'Biaya Bahan Baku'), `nilai`, 0)) AS 'biaya_kirim_baku', "
                    + "SUM(IF((`Kategori1` = 'Biaya Overhead Pabrik'), `nilai`, 0)) AS 'biaya_overhead_baku', "
                    + "SUM(IF((`Kategori1` = 'Biaya Tenaga Kerja Langsung'), `nilai`, 0)) AS 'biaya_tenagakerja_baku' "
                    + "FROM `tb_biaya_pabrik` "
                    + "WHERE `bulan` BETWEEN '" + dateFormat.format(Date_filter1.getDate()) + "' AND '" + dateFormat.format(Date_filter2.getDate()) + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                harga_pembelian_bahan_baku = rs.getDouble("biaya_kirim_baku");
                harga_penjualan_bahan_baku = rs.getDouble("biaya_kirim_baku");
                biaya_tenagakerja_baku = rs.getDouble("biaya_tenagakerja_baku");
                biaya_overhead_baku = rs.getDouble("biaya_overhead_baku");
                label_tenagakerja_baku.setText(decimalFormat.format(biaya_tenagakerja_baku));
                label_overhead_baku.setText(decimalFormat.format(biaya_overhead_baku));
            }

            //PEMBELIAN BAHAN BAKU
            sql = "SELECT SUM(`jumlah_keping`) AS 'keping', SUM(`total_berat`) AS 'berat', SUM(`harga_bahanbaku`*`total_berat`) AS 'harga' \n"
                    + "FROM `tb_grading_bahan_baku` "
                    + "LEFT JOIN `tb_bahan_baku_masuk` ON `tb_grading_bahan_baku`.`no_kartu_waleta` = `tb_bahan_baku_masuk`.`no_kartu_waleta`\n"
                    + "LEFT JOIN `tb_kartu_cmp` ON `tb_grading_bahan_baku`.`no_kartu_waleta`=`tb_kartu_cmp`.`kode_kartu_cmp`"
                    + "WHERE `tb_kartu_cmp`.`kode_kartu_cmp` IS NULL "
                    + "AND `tb_bahan_baku_masuk`.`tgl_masuk` BETWEEN '" + dateFormat.format(Date_filter1.getDate()) + "' AND '" + dateFormat.format(Date_filter2.getDate()) + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                kpg_pembelian_bahan_baku = rs.getInt("keping");
                gram_pembelian_bahan_baku = rs.getInt("berat");
                harga_pembelian_bahan_baku = harga_pembelian_bahan_baku + rs.getDouble("harga");
                label_kpg_2.setText(decimalFormat.format(kpg_pembelian_bahan_baku));
                label_gram_2.setText(decimalFormat.format(gram_pembelian_bahan_baku));
                label_harga_2.setText(decimalFormat.format(harga_pembelian_bahan_baku));
            }

            //PENJUALAN BAHAN BAKU
            sql = "SELECT SUM(`total_keping_keluar`) AS 'keping', SUM(`total_berat_keluar`) AS 'berat', SUM(`harga_bahanbaku`*`total_berat_keluar`) AS 'harga' \n"
                    + "FROM `tb_bahan_baku_keluar` "
                    + "LEFT JOIN `tb_grading_bahan_baku` ON (`tb_grading_bahan_baku`.`no_kartu_waleta` = `tb_bahan_baku_keluar`.`no_kartu_waleta` AND `tb_grading_bahan_baku`.`kode_grade` = `tb_bahan_baku_keluar`.`kode_grade`)\n"
                    + "LEFT JOIN `tb_bahan_baku_keluar1` ON `tb_bahan_baku_keluar`.`kode_pengeluaran` = `tb_bahan_baku_keluar1`.`kode_pengeluaran`\n"
                    + "WHERE `tb_bahan_baku_keluar1`.`tgl_keluar` BETWEEN '" + dateFormat.format(Date_filter1.getDate()) + "' AND '" + dateFormat.format(Date_filter2.getDate()) + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                kpg_penjualan_bahan_baku = rs.getInt("keping");
                gram_penjualan_bahan_baku = rs.getInt("berat");
                harga_penjualan_bahan_baku = harga_penjualan_bahan_baku + rs.getDouble("harga");
                label_kpg_3.setText(decimalFormat.format(kpg_penjualan_bahan_baku));
                label_gram_3.setText(decimalFormat.format(gram_penjualan_bahan_baku));
                label_harga_3.setText(decimalFormat.format(harga_penjualan_bahan_baku));
            }

            //BAHAN BAKU TERSEDIA UNTUK DI PRODUKSI
            kpg_bahan_baku_untuk_produksi = (kpg_pembelian_bahan_baku + kpg_persediaan_awal_bahan_baku) - kpg_penjualan_bahan_baku;
            gram_bahan_baku_untuk_produksi = (gram_pembelian_bahan_baku + gram_persediaan_awal_bahan_baku) - gram_penjualan_bahan_baku;
            harga_bahan_baku_untuk_produksi = (harga_pembelian_bahan_baku + harga_persediaan_awal_bahan_baku) - harga_penjualan_bahan_baku;
            label_kpg_4.setText(decimalFormat.format(kpg_bahan_baku_untuk_produksi));
            label_gram_4.setText(decimalFormat.format(gram_bahan_baku_untuk_produksi));
            label_harga_4.setText(decimalFormat.format(harga_bahan_baku_untuk_produksi));

            //BAHAN BAKU DI PROSES PRODUKSI
            sql = "SELECT SUM(`berat_basah`) AS 'berat', SUM(`tb_laporan_produksi`.`jumlah_keping`) AS 'keping', SUM(`berat_basah`*`tb_grading_bahan_baku`.`harga_bahanbaku`) AS 'harga' "
                    + "FROM `tb_laporan_produksi` \n"
                    + "LEFT JOIN `tb_grading_bahan_baku` ON (`tb_laporan_produksi`.`no_kartu_waleta` = `tb_grading_bahan_baku`.`no_kartu_waleta` AND `tb_laporan_produksi`.`kode_grade` = `tb_grading_bahan_baku`.`kode_grade`)\n"
                    + "WHERE `tanggal_lp` BETWEEN '" + dateFormat.format(Date_filter1.getDate()) + "' AND '" + dateFormat.format(Date_filter2.getDate()) + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                kpg_bahan_baku_LP = rs.getInt("keping");
                gram_bahan_baku_LP = rs.getInt("berat");
                harga_bahan_baku_LP = rs.getDouble("harga");
                label_kpg_6.setText(decimalFormat.format(kpg_bahan_baku_LP));
                label_gram_6.setText(decimalFormat.format(gram_bahan_baku_LP));
                label_harga_6.setText(decimalFormat.format(harga_bahan_baku_LP));
            }

            //SALDO AKHIR BAHAN BAKU
            kpg_stok_bahan_baku = kpg_bahan_baku_untuk_produksi - kpg_bahan_baku_LP;
            gram_stok_bahan_baku = gram_bahan_baku_untuk_produksi - gram_bahan_baku_LP;
            harga_stok_bahan_baku = harga_bahan_baku_untuk_produksi - harga_bahan_baku_LP;
            label_kpg_5.setText(decimalFormat.format(kpg_stok_bahan_baku));
            label_gram_5.setText(decimalFormat.format(gram_stok_bahan_baku));
            label_harga_5.setText(decimalFormat.format(harga_stok_bahan_baku));
            total_biaya_baku = harga_bahan_baku_LP + biaya_tenagakerja_baku + biaya_overhead_baku;
            label_total_biaya_baku.setText(decimalFormat.format(total_biaya_baku));

            //WORK IN PROGRESS---------------------------------------------------------------------------------------------------------------------------------------------------------------
            int kpg_persediaan_awal_wip = 0, gram_persediaan_awal_wip = 0;
            double harga_persediaan_awal_wip = 0;
            int kpg_persediaan_akhir_wip = 0, gram_persediaan_akhir_wip = 0;
            double harga_persediaan_akhir_wip = 0;
            int kpg_wip_barang_jadi = 0, gram_wip_barang_jadi = 0;
            double harga_wip_barang_jadi = 0;
            //PERSEDIAAN AWAL WIP
            sql = "SELECT SUM(`berat_basah`) AS 'berat', SUM(`tb_laporan_produksi`.`jumlah_keping`) AS 'keping', SUM(`berat_basah`*`tb_grading_bahan_baku`.`harga_bahanbaku`) AS 'harga'\n"
                    + "FROM `tb_laporan_produksi` \n"
                    + "LEFT JOIN `tb_grading_bahan_baku` ON (`tb_laporan_produksi`.`no_kartu_waleta` = `tb_grading_bahan_baku`.`no_kartu_waleta` AND `tb_laporan_produksi`.`kode_grade` = `tb_grading_bahan_baku`.`kode_grade`)\n"
                    + "LEFT JOIN `tb_bahan_jadi_masuk` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_bahan_jadi_masuk`.`kode_asal`\n"
                    + "LEFT JOIN `tb_tutupan_grading` ON `tb_bahan_jadi_masuk`.`kode_tutupan` = `tb_tutupan_grading`.`kode_tutupan`\n"
                    + "WHERE (`tb_laporan_produksi`.`tanggal_lp` < '" + dateFormat.format(Date_filter1.getDate()) + "') AND (`tb_tutupan_grading`.`tgl_statusBox` >= '" + dateFormat.format(Date_filter1.getDate()) + "' OR `tb_tutupan_grading`.`tgl_statusBox` IS NULL)";
//                sql = "SELECT SUM(`berat_basah`) AS 'berat', SUM(`tb_laporan_produksi`.`jumlah_keping`) AS 'keping', SUM(`berat_basah`*`tb_grading_bahan_baku`.`harga_bahanbaku`) AS 'harga'\n"
//                        + "FROM `tb_laporan_produksi` \n"
//                        + "LEFT JOIN `tb_grading_bahan_baku` ON (`tb_laporan_produksi`.`no_kartu_waleta` = `tb_grading_bahan_baku`.`no_kartu_waleta` AND `tb_laporan_produksi`.`kode_grade` = `tb_grading_bahan_baku`.`kode_grade`)\n"
//                        + "LEFT JOIN `tb_bahan_jadi_masuk` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_bahan_jadi_masuk`.`kode_asal`\n"
//                        + "WHERE `tb_laporan_produksi`.`tanggal_lp` < '" + dateFormat.format(Date_filter1.getDate()) + "' AND (`tb_bahan_jadi_masuk`.`tanggal_grading` >= '" + dateFormat.format(Date_filter1.getDate()) + "' OR `tb_bahan_jadi_masuk`.`tanggal_grading` IS NULL)";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                kpg_persediaan_awal_wip = rs.getInt("keping");
                gram_persediaan_awal_wip = rs.getInt("berat");
                harga_persediaan_awal_wip = rs.getDouble("harga");
            }

            label_kpg_wip1.setText(decimalFormat.format(kpg_persediaan_awal_wip));
            label_gram_wip1.setText(decimalFormat.format(gram_persediaan_awal_wip));
            label_harga_wip1.setText(decimalFormat.format(harga_persediaan_awal_wip));

            //KENAIKAN WIP
            label_kpg_wip2.setText(decimalFormat.format(kpg_bahan_baku_LP));
            label_gram_wip2.setText(decimalFormat.format(gram_bahan_baku_LP));
            label_harga_wip2.setText(decimalFormat.format(harga_bahan_baku_LP));

            //PERSEDIAAN AKHIR WIP
            sql = "SELECT SUM(`berat_basah`) AS 'berat', SUM(`tb_laporan_produksi`.`jumlah_keping`) AS 'keping', SUM(`berat_basah`*`tb_grading_bahan_baku`.`harga_bahanbaku`) AS 'harga'\n"
                    + "FROM `tb_laporan_produksi` \n"
                    + "LEFT JOIN `tb_grading_bahan_baku` ON (`tb_laporan_produksi`.`no_kartu_waleta` = `tb_grading_bahan_baku`.`no_kartu_waleta` AND `tb_laporan_produksi`.`kode_grade` = `tb_grading_bahan_baku`.`kode_grade`)\n"
                    + "LEFT JOIN `tb_bahan_jadi_masuk` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_bahan_jadi_masuk`.`kode_asal`\n"
                    + "LEFT JOIN `tb_tutupan_grading` ON `tb_bahan_jadi_masuk`.`kode_tutupan` = `tb_tutupan_grading`.`kode_tutupan`\n"
                    + "WHERE `tb_laporan_produksi`.`tanggal_lp` <= '" + dateFormat.format(Date_filter2.getDate()) + "' AND (`tb_tutupan_grading`.`tgl_statusBox` > '" + dateFormat.format(Date_filter2.getDate()) + "' OR `tb_tutupan_grading`.`tgl_statusBox` IS NULL)";
//                        + "WHERE `tb_laporan_produksi`.`tanggal_lp` <= '" + dateFormat.format(Date_filter2.getDate()) + "' AND (`tb_bahan_jadi_masuk`.`tanggal_grading` > '" + dateFormat.format(Date_filter2.getDate()) + "' OR `tb_bahan_jadi_masuk`.`tanggal_grading` IS NULL)";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                kpg_persediaan_akhir_wip = rs.getInt("keping");
                gram_persediaan_akhir_wip = rs.getInt("berat");
                harga_persediaan_akhir_wip = rs.getDouble("harga");
                label_kpg_wip3.setText(decimalFormat.format(kpg_persediaan_akhir_wip));
                label_gram_wip3.setText(decimalFormat.format(gram_persediaan_akhir_wip));
                label_harga_wip3.setText(decimalFormat.format(harga_persediaan_akhir_wip));
            }

            //WIP MENJADI BARANG JADI
            sql = "SELECT SUM(`berat_basah`) AS 'berat', SUM(`tb_laporan_produksi`.`jumlah_keping`) AS 'keping', SUM(`berat_basah`*`tb_grading_bahan_baku`.`harga_bahanbaku`) AS 'harga'\n"
                    + "FROM `tb_laporan_produksi` \n"
                    + "LEFT JOIN `tb_grading_bahan_baku` ON (`tb_laporan_produksi`.`no_kartu_waleta` = `tb_grading_bahan_baku`.`no_kartu_waleta` AND `tb_laporan_produksi`.`kode_grade` = `tb_grading_bahan_baku`.`kode_grade`)\n"
                    + "LEFT JOIN `tb_bahan_jadi_masuk` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_bahan_jadi_masuk`.`kode_asal`\n"
                    + "LEFT JOIN `tb_tutupan_grading` ON `tb_bahan_jadi_masuk`.`kode_tutupan` = `tb_tutupan_grading`.`kode_tutupan`\n"
                    + "WHERE `tb_tutupan_grading`.`tgl_statusBox` BETWEEN '" + dateFormat.format(Date_filter1.getDate()) + "' AND '" + dateFormat.format(Date_filter2.getDate()) + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                kpg_wip_barang_jadi = rs.getInt("keping");
                gram_wip_barang_jadi = rs.getInt("berat");
                harga_wip_barang_jadi = rs.getDouble("harga");
                label_kpg_wip4.setText(decimalFormat.format(kpg_wip_barang_jadi));
                label_gram_wip4.setText(decimalFormat.format(gram_wip_barang_jadi));
                label_harga_wip4.setText(decimalFormat.format(harga_wip_barang_jadi));
            }

            //BARANG JADI---------------------------------------------------------------------------------------------------------------------------------------------------------------
            double kpg_persediaan_awal_bjd = 0, gram_persediaan_awal_bjd = 0, nilaibaku_persediaan_awal_bjd = 0;
            double kpg_pembelian_bjd = 0, gram_pembelian_bjd = 0, nilaibaku_pembelian_bjd = 0;
            double kpg_kenaikan_bjd = 0, gram_kenaikan_bjd = 0, nilaibaku_kenaikan_bjd = 0;
            double kpg_barang_siap_jual_bjd = 0, gram_barang_siap_jual_bjd = 0, nilaibaku_barang_siap_jual_bjd = 0;
            double kpg_penjualan_bjd = 0, gram_penjualan_bjd = 0, nilaibaku_penjualan_bjd = 0;
            double kpg_saldo_akhir_bjd = 0, gram_saldo_akhir_bjd = 0, nilaibaku_saldo_akhir_bjd = 0;
            //PERSEDIAAN AWAL BARANG JADI BARANG JADI
            sql = "SELECT SUM(`keping`) AS 'keping', SUM(`berat`) AS 'berat' "
                    + "FROM `tb_stokopname_gbj` LEFT JOIN `tb_grade_bahan_jadi` ON `tb_stokopname_gbj`.`kode_grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode`\n"
                    + "WHERE "
                    //                        + "`tb_grade_bahan_jadi`.`Kategori1` <> 'residu' AND "
                    + "`tgl_stok_opname` = (SELECT MAX(`tgl_stok_opname`) FROM `tb_stokopname_gbj` \n"
                    + "WHERE `tgl_stok_opname` <= '" + dateFormat.format(Date_filter1.getDate()) + "')";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                kpg_persediaan_awal_bjd = rs.getInt("keping");
                gram_persediaan_awal_bjd = rs.getInt("berat");
                label_kpg_persediaan_awal_BJ.setText(decimalFormat.format(kpg_persediaan_awal_bjd));
                label_gram_persediaan_awal_BJ.setText(decimalFormat.format(gram_persediaan_awal_bjd));
            }
            //PEMBELIAN BARANG JADI
            sql = "SELECT SUM(`jumlah_keping`) AS 'keping', SUM(`berat`) AS 'berat', SUM(`harga`*`berat`) AS 'harga' \n"
                    + "FROM `tb_pembelian_barang_jadi` WHERE `kode_supplier` <> 'E'"
                    + "AND `tanggal_pembelian` BETWEEN '" + dateFormat.format(Date_filter1.getDate()) + "' AND '" + dateFormat.format(Date_filter2.getDate()) + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                kpg_pembelian_bjd = rs.getInt("keping");
                gram_pembelian_bjd = rs.getInt("berat");
                nilaibaku_pembelian_bjd = rs.getDouble("harga");
                label_kpg_Pembelian_BJ.setText(decimalFormat.format(kpg_pembelian_bjd));
                label_gram_Pembelian_BJ.setText(decimalFormat.format(gram_pembelian_bjd));
                label_harga_Pembelian_BJ.setText(decimalFormat.format(nilaibaku_pembelian_bjd));
            }
            //PENJUALAN BARANG JADI
            sql = "SELECT SUM(`tb_box_bahan_jadi`.`keping`) AS 'keping', SUM(`tb_box_bahan_jadi`.`berat`) AS 'berat', `invoice_pengiriman`, `tb_pengiriman`.`tanggal_invoice` \n"
                    + "FROM `tb_box_packing` \n"
                    + "LEFT JOIN `tb_box_bahan_jadi` ON `tb_box_packing`.`no_box` = `tb_box_bahan_jadi`.`no_box`\n"
                    + "LEFT JOIN `tb_pengiriman` ON `tb_box_packing`.`invoice_pengiriman` = `tb_pengiriman`.`invoice_no`\n"
                    + "WHERE `jenis_pengiriman` <> 'Ekspor Esta' AND `tb_pengiriman`.`tanggal_invoice` BETWEEN '" + dateFormat.format(Date_filter1.getDate()) + "' AND '" + dateFormat.format(Date_filter2.getDate()) + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                kpg_penjualan_bjd = rs.getInt("keping");
                gram_penjualan_bjd = rs.getInt("berat");
                label_kpg_penjualan_BJ.setText(decimalFormat.format(kpg_penjualan_bjd));
                label_gram_penjualan_BJ.setText(decimalFormat.format(gram_penjualan_bjd));
            }

            //SALDO AKHIR BARANG JADI
            sql = "SELECT SUM(`keping`) AS 'keping', SUM(`berat`) AS 'berat' "
                    + "FROM `tb_stokopname_gbj` LEFT JOIN `tb_grade_bahan_jadi` ON `tb_stokopname_gbj`.`kode_grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode` \n"
                    + "WHERE "
                    + "`tb_grade_bahan_jadi`.`Kategori1` <> 'residu' "
                    + "AND `tgl_stok_opname` = (SELECT MAX(`tgl_stok_opname`) FROM `tb_stokopname_gbj` \n"
                    + "WHERE `tgl_stok_opname` <= '" + dateFormat.format(Date_filter2.getDate()) + "')";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                kpg_saldo_akhir_bjd = rs.getInt("keping");
                gram_saldo_akhir_bjd = rs.getInt("berat");
                label_kpg_saldoAkhir_BJ.setText(decimalFormat.format(kpg_saldo_akhir_bjd));
                label_gram_saldoAkhir_BJ.setText(decimalFormat.format(gram_saldo_akhir_bjd));
            }
            sql = "SELECT `tgl_stok_opname`, `nilai_baku` FROM `tb_stokopname` WHERE `tgl_stok_opname` <= '" + dateFormat.format(Date_filter1.getDate()) + "' ORDER BY `tgl_stok_opname` DESC LIMIT 1";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                nilaibaku_persediaan_awal_bjd = rs.getDouble("nilai_baku");
                label_harga_persediaan_awal_BJ.setText(decimalFormat.format(nilaibaku_persediaan_awal_bjd));
            }
            //KENAIKAN BARANG JADI
            kpg_kenaikan_bjd = (kpg_saldo_akhir_bjd + kpg_penjualan_bjd) - (kpg_persediaan_awal_bjd + kpg_pembelian_bjd);
            gram_kenaikan_bjd = (gram_saldo_akhir_bjd + gram_penjualan_bjd) - (gram_persediaan_awal_bjd + gram_pembelian_bjd);
            nilaibaku_kenaikan_bjd = harga_wip_barang_jadi;
            label_kpg_kenaikan_BJ.setText(decimalFormat.format(kpg_kenaikan_bjd));
            label_gram_kenaikan_BJ.setText(decimalFormat.format(gram_kenaikan_bjd));
            label_harga_kenaikan_BJ.setText(decimalFormat.format(nilaibaku_kenaikan_bjd));
            //BARANG JADI BARANG JADI SIAP DI JUAL
            kpg_barang_siap_jual_bjd = kpg_persediaan_awal_bjd + kpg_pembelian_bjd + kpg_kenaikan_bjd;
            gram_barang_siap_jual_bjd = gram_persediaan_awal_bjd + gram_pembelian_bjd + gram_kenaikan_bjd;
            nilaibaku_barang_siap_jual_bjd = nilaibaku_persediaan_awal_bjd + nilaibaku_pembelian_bjd + nilaibaku_kenaikan_bjd;
            label_kpg_siapjual_BJ.setText(decimalFormat.format(kpg_barang_siap_jual_bjd));
            label_gram_siapjual_BJ.setText(decimalFormat.format(gram_barang_siap_jual_bjd));
            label_harga_siapjual_BJ.setText(decimalFormat.format(nilaibaku_barang_siap_jual_bjd));

            nilaibaku_penjualan_bjd = gram_penjualan_bjd * (nilaibaku_barang_siap_jual_bjd / gram_barang_siap_jual_bjd);
            label_harga_penjualan_BJ.setText(decimalFormat.format(nilaibaku_penjualan_bjd));
            nilaibaku_saldo_akhir_bjd = gram_saldo_akhir_bjd * (nilaibaku_barang_siap_jual_bjd / gram_barang_siap_jual_bjd);
            label_harga_saldoAkhir_BJ.setText(decimalFormat.format(nilaibaku_saldo_akhir_bjd));

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_LaporanProduksi_Keuangan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        Date_filter1 = new com.toedter.calendar.JDateChooser();
        Date_filter2 = new com.toedter.calendar.JDateChooser();
        button_refresh = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        label_harga_1 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        label_harga_2 = new javax.swing.JLabel();
        label_gram_5 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        label_gram_4 = new javax.swing.JLabel();
        label_harga_4 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        label_kpg_5 = new javax.swing.JLabel();
        label_overhead_baku = new javax.swing.JLabel();
        label_gram_6 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        label_harga_5 = new javax.swing.JLabel();
        label_gram_1 = new javax.swing.JLabel();
        label_harga_6 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        label_kpg_1 = new javax.swing.JLabel();
        label_gram_3 = new javax.swing.JLabel();
        label_kpg_6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        label_tenagakerja_baku = new javax.swing.JLabel();
        label_kpg_2 = new javax.swing.JLabel();
        label_kpg_3 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        label_gram_2 = new javax.swing.JLabel();
        label_kpg_4 = new javax.swing.JLabel();
        label_harga_3 = new javax.swing.JLabel();
        label_total_biaya_baku = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        label_overhead_wip3 = new javax.swing.JLabel();
        label_total_wip2 = new javax.swing.JLabel();
        label_harga_wip4 = new javax.swing.JLabel();
        label_overhead_wip1 = new javax.swing.JLabel();
        label_tenagakerja_wip4 = new javax.swing.JLabel();
        label_overhead_wip2 = new javax.swing.JLabel();
        label_tenagakerja_wip1 = new javax.swing.JLabel();
        label_kpg_wip4 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        label_harga_wip3 = new javax.swing.JLabel();
        label_harga_wip1 = new javax.swing.JLabel();
        label_gram_wip3 = new javax.swing.JLabel();
        label_kpg_wip1 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        label_total_wip3 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        label_gram_wip4 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        label_total_wip4 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        label_gram_wip2 = new javax.swing.JLabel();
        label_harga_wip2 = new javax.swing.JLabel();
        label_overhead_wip4 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        label_tenagakerja_wip3 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        label_total_wip1 = new javax.swing.JLabel();
        label_gram_wip1 = new javax.swing.JLabel();
        label_tenagakerja_wip2 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        label_kpg_wip2 = new javax.swing.JLabel();
        label_kpg_wip3 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        label_total_saldoAkhir_BJ = new javax.swing.JLabel();
        label_gram_persediaan_awal_BJ = new javax.swing.JLabel();
        label_overhead_siapjual_BJ = new javax.swing.JLabel();
        label_overhead_penjualan_BJ = new javax.swing.JLabel();
        label_tenagakerja_kenaikan_BJ = new javax.swing.JLabel();
        label_harga_Pembelian_BJ = new javax.swing.JLabel();
        label_gram_Pembelian_BJ = new javax.swing.JLabel();
        label_overhead_Pembelian_BJ = new javax.swing.JLabel();
        label_total_kenaikan_BJ = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        label_harga_saldoAkhir_BJ = new javax.swing.JLabel();
        label_kpg_Pembelian_BJ = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        label_kpg_kenaikan_BJ = new javax.swing.JLabel();
        label_kpg_penjualan_BJ = new javax.swing.JLabel();
        label_kpg_saldoAkhir_BJ = new javax.swing.JLabel();
        label_total_Pembelian_BJ = new javax.swing.JLabel();
        jLabel59 = new javax.swing.JLabel();
        label_kpg_persediaan_awal_BJ = new javax.swing.JLabel();
        label_tenagakerja_penjualan_BJ = new javax.swing.JLabel();
        label_kpg_siapjual_BJ = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        jLabel60 = new javax.swing.JLabel();
        label_total_persediaan_awal_BJ = new javax.swing.JLabel();
        label_overhead_kenaikan_BJ = new javax.swing.JLabel();
        label_harga_persediaan_awal_BJ = new javax.swing.JLabel();
        label_tenagakerja_Pembelian_BJ = new javax.swing.JLabel();
        label_gram_siapjual_BJ = new javax.swing.JLabel();
        jLabel54 = new javax.swing.JLabel();
        jLabel52 = new javax.swing.JLabel();
        label_total_siapjual_BJ = new javax.swing.JLabel();
        label_gram_saldoAkhir_BJ = new javax.swing.JLabel();
        label_gram_penjualan_BJ = new javax.swing.JLabel();
        label_harga_siapjual_BJ = new javax.swing.JLabel();
        label_tenagakerja_siapjual_BJ = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        label_harga_kenaikan_BJ = new javax.swing.JLabel();
        label_tenagakerja_persediaan_awal_BJ = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        label_overhead_saldoAkhir_BJ = new javax.swing.JLabel();
        label_overhead_persediaan_awal_BJ = new javax.swing.JLabel();
        jLabel53 = new javax.swing.JLabel();
        label_harga_penjualan_BJ = new javax.swing.JLabel();
        label_gram_kenaikan_BJ = new javax.swing.JLabel();
        label_tenagakerja_saldoAkhir_BJ = new javax.swing.JLabel();
        label_total_penjualan_BJ = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setPreferredSize(new java.awt.Dimension(1366, 700));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "LAPORAN PRODUKSI", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N
        jPanel1.setPreferredSize(new java.awt.Dimension(1125, 650));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("PERIODE :");

        Date_filter1.setBackground(new java.awt.Color(255, 255, 255));
        Date_filter1.setDateFormatString("dd MMMM yyyy");
        Date_filter1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Date_filter2.setBackground(new java.awt.Color(255, 255, 255));
        Date_filter2.setDateFormatString("dd MMMM yyyy");
        Date_filter2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        button_refresh.setBackground(new java.awt.Color(255, 255, 255));
        button_refresh.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_refresh.setText("Refresh");
        button_refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refreshActionPerformed(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel2.setText("BAHAN BAKU");

        jLabel38.setBackground(new java.awt.Color(255, 255, 255));
        jLabel38.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel38.setText("Bi. Overhead");

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel4.setText("PEMBELIAN BAHAN BAKU");

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel5.setText("PENJUALAN BAHAN BAKU");

        label_harga_1.setBackground(new java.awt.Color(255, 255, 255));
        label_harga_1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_harga_1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_harga_1.setText("0");
        label_harga_1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel6.setText("BAHAN BAKU TERSEDIA UNTUK DIPRODUKSI");

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("PERSEDIAAN AWAL BAHAN BAKU");

        label_harga_2.setBackground(new java.awt.Color(255, 255, 255));
        label_harga_2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_harga_2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_harga_2.setText("0");
        label_harga_2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        label_gram_5.setBackground(new java.awt.Color(255, 255, 255));
        label_gram_5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_gram_5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_gram_5.setText("0");
        label_gram_5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel16.setText("gram");

        label_gram_4.setBackground(new java.awt.Color(255, 255, 255));
        label_gram_4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_gram_4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_gram_4.setText("0");
        label_gram_4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        label_harga_4.setBackground(new java.awt.Color(255, 255, 255));
        label_harga_4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_harga_4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_harga_4.setText("0");
        label_harga_4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel15.setText("keping");

        label_kpg_5.setBackground(new java.awt.Color(255, 255, 255));
        label_kpg_5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_kpg_5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_kpg_5.setText("0");
        label_kpg_5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        label_overhead_baku.setBackground(new java.awt.Color(255, 255, 255));
        label_overhead_baku.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_overhead_baku.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_overhead_baku.setText("0");
        label_overhead_baku.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        label_gram_6.setBackground(new java.awt.Color(255, 255, 255));
        label_gram_6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_gram_6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_gram_6.setText("0");
        label_gram_6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel39.setBackground(new java.awt.Color(255, 255, 255));
        jLabel39.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel39.setText("Total");

        label_harga_5.setBackground(new java.awt.Color(255, 255, 255));
        label_harga_5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_harga_5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_harga_5.setText("0");
        label_harga_5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        label_gram_1.setBackground(new java.awt.Color(255, 255, 255));
        label_gram_1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_gram_1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_gram_1.setText("0");
        label_gram_1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        label_harga_6.setBackground(new java.awt.Color(255, 255, 255));
        label_harga_6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_harga_6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_harga_6.setText("0");
        label_harga_6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel29.setBackground(new java.awt.Color(255, 255, 255));
        jLabel29.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel29.setText("Bi. Bahan Baku");

        label_kpg_1.setBackground(new java.awt.Color(255, 255, 255));
        label_kpg_1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_kpg_1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_kpg_1.setText("0");
        label_kpg_1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        label_gram_3.setBackground(new java.awt.Color(255, 255, 255));
        label_gram_3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_gram_3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_gram_3.setText("0");
        label_gram_3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        label_kpg_6.setBackground(new java.awt.Color(255, 255, 255));
        label_kpg_6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_kpg_6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_kpg_6.setText("0");
        label_kpg_6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel7.setText("SALDO AKHIR BAHAN BAKU");

        label_tenagakerja_baku.setBackground(new java.awt.Color(255, 255, 255));
        label_tenagakerja_baku.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_tenagakerja_baku.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_tenagakerja_baku.setText("0");
        label_tenagakerja_baku.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        label_kpg_2.setBackground(new java.awt.Color(255, 255, 255));
        label_kpg_2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_kpg_2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_kpg_2.setText("0");
        label_kpg_2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        label_kpg_3.setBackground(new java.awt.Color(255, 255, 255));
        label_kpg_3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_kpg_3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_kpg_3.setText("0");
        label_kpg_3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel37.setBackground(new java.awt.Color(255, 255, 255));
        jLabel37.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel37.setText("Bi. Tenaga Kerja");

        label_gram_2.setBackground(new java.awt.Color(255, 255, 255));
        label_gram_2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_gram_2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_gram_2.setText("0");
        label_gram_2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        label_kpg_4.setBackground(new java.awt.Color(255, 255, 255));
        label_kpg_4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_kpg_4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_kpg_4.setText("0");
        label_kpg_4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        label_harga_3.setBackground(new java.awt.Color(255, 255, 255));
        label_harga_3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_harga_3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_harga_3.setText("0");
        label_harga_3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        label_total_biaya_baku.setBackground(new java.awt.Color(255, 255, 255));
        label_total_biaya_baku.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_biaya_baku.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_total_biaya_baku.setText("0");
        label_total_biaya_baku.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel8.setText("BAHAN BAKU DIPROSES PRODUKSI (LP)");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label_kpg_4, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_kpg_3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_kpg_2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_kpg_5, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_kpg_1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_kpg_6, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label_gram_6, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_gram_1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_gram_2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_gram_3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_gram_4, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_gram_5, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_harga_6, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_harga_1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_harga_2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_harga_3, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_harga_4, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_harga_5, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_tenagakerja_baku, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(label_overhead_baku, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_biaya_baku, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(label_harga_1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(label_harga_2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(label_harga_3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(label_harga_4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(label_harga_5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_harga_6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_tenagakerja_baku, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_overhead_baku, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_biaya_baku, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(label_gram_1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(label_gram_2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(label_gram_3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(label_gram_4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(label_gram_5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(label_gram_6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(label_kpg_1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(label_kpg_2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(label_kpg_3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(label_kpg_4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(label_kpg_5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(label_kpg_6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(186, 186, 186)))
                .addContainerGap())
        );

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        label_overhead_wip3.setBackground(new java.awt.Color(255, 255, 255));
        label_overhead_wip3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_overhead_wip3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_overhead_wip3.setText("0");
        label_overhead_wip3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        label_total_wip2.setBackground(new java.awt.Color(255, 255, 255));
        label_total_wip2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_wip2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_total_wip2.setText("0");
        label_total_wip2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        label_harga_wip4.setBackground(new java.awt.Color(255, 255, 255));
        label_harga_wip4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_harga_wip4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_harga_wip4.setText("0");
        label_harga_wip4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        label_overhead_wip1.setBackground(new java.awt.Color(255, 255, 255));
        label_overhead_wip1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_overhead_wip1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_overhead_wip1.setText("0");
        label_overhead_wip1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        label_tenagakerja_wip4.setBackground(new java.awt.Color(255, 255, 255));
        label_tenagakerja_wip4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_tenagakerja_wip4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_tenagakerja_wip4.setText("0");
        label_tenagakerja_wip4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        label_overhead_wip2.setBackground(new java.awt.Color(255, 255, 255));
        label_overhead_wip2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_overhead_wip2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_overhead_wip2.setText("0");
        label_overhead_wip2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        label_tenagakerja_wip1.setBackground(new java.awt.Color(255, 255, 255));
        label_tenagakerja_wip1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_tenagakerja_wip1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_tenagakerja_wip1.setText("0");
        label_tenagakerja_wip1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        label_kpg_wip4.setBackground(new java.awt.Color(255, 255, 255));
        label_kpg_wip4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_kpg_wip4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_kpg_wip4.setText("0");
        label_kpg_wip4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel46.setBackground(new java.awt.Color(255, 255, 255));
        jLabel46.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel46.setText("Bi. Overhead");

        jLabel44.setBackground(new java.awt.Color(255, 255, 255));
        jLabel44.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel44.setText("gram");

        jLabel31.setBackground(new java.awt.Color(255, 255, 255));
        jLabel31.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel31.setText("PERSEDIAAN AKHIR WIP");

        label_harga_wip3.setBackground(new java.awt.Color(255, 255, 255));
        label_harga_wip3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_harga_wip3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_harga_wip3.setText("0");
        label_harga_wip3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        label_harga_wip1.setBackground(new java.awt.Color(255, 255, 255));
        label_harga_wip1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_harga_wip1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_harga_wip1.setText("0");
        label_harga_wip1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        label_gram_wip3.setBackground(new java.awt.Color(255, 255, 255));
        label_gram_wip3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_gram_wip3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_gram_wip3.setText("0");
        label_gram_wip3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        label_kpg_wip1.setBackground(new java.awt.Color(255, 255, 255));
        label_kpg_wip1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_kpg_wip1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_kpg_wip1.setText("0");
        label_kpg_wip1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel35.setBackground(new java.awt.Color(255, 255, 255));
        jLabel35.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel35.setText("WORK IN PROCESS");

        label_total_wip3.setBackground(new java.awt.Color(255, 255, 255));
        label_total_wip3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_wip3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_total_wip3.setText("0");
        label_total_wip3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel30.setBackground(new java.awt.Color(255, 255, 255));
        jLabel30.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel30.setText("KENAIKAN WIP");

        label_gram_wip4.setBackground(new java.awt.Color(255, 255, 255));
        label_gram_wip4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_gram_wip4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_gram_wip4.setText("0");
        label_gram_wip4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel36.setBackground(new java.awt.Color(255, 255, 255));
        jLabel36.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel36.setText("PERSEDIAAN AWAL WIP");

        label_total_wip4.setBackground(new java.awt.Color(255, 255, 255));
        label_total_wip4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_wip4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_total_wip4.setText("0");
        label_total_wip4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel43.setBackground(new java.awt.Color(255, 255, 255));
        jLabel43.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel43.setText("keping");

        label_gram_wip2.setBackground(new java.awt.Color(255, 255, 255));
        label_gram_wip2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_gram_wip2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_gram_wip2.setText("0");
        label_gram_wip2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        label_harga_wip2.setBackground(new java.awt.Color(255, 255, 255));
        label_harga_wip2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_harga_wip2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_harga_wip2.setText("0");
        label_harga_wip2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        label_overhead_wip4.setBackground(new java.awt.Color(255, 255, 255));
        label_overhead_wip4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_overhead_wip4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_overhead_wip4.setText("0");
        label_overhead_wip4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel47.setBackground(new java.awt.Color(255, 255, 255));
        jLabel47.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel47.setText("Total");

        label_tenagakerja_wip3.setBackground(new java.awt.Color(255, 255, 255));
        label_tenagakerja_wip3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_tenagakerja_wip3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_tenagakerja_wip3.setText("0");
        label_tenagakerja_wip3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel40.setBackground(new java.awt.Color(255, 255, 255));
        jLabel40.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel40.setText("Bi. Bahan Baku");

        label_total_wip1.setBackground(new java.awt.Color(255, 255, 255));
        label_total_wip1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_wip1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_total_wip1.setText("0");
        label_total_wip1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        label_gram_wip1.setBackground(new java.awt.Color(255, 255, 255));
        label_gram_wip1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_gram_wip1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_gram_wip1.setText("0");
        label_gram_wip1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        label_tenagakerja_wip2.setBackground(new java.awt.Color(255, 255, 255));
        label_tenagakerja_wip2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_tenagakerja_wip2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_tenagakerja_wip2.setText("0");
        label_tenagakerja_wip2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel45.setBackground(new java.awt.Color(255, 255, 255));
        jLabel45.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel45.setText("Bi. Tenaga Kerja");

        label_kpg_wip2.setBackground(new java.awt.Color(255, 255, 255));
        label_kpg_wip2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_kpg_wip2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_kpg_wip2.setText("0");
        label_kpg_wip2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        label_kpg_wip3.setBackground(new java.awt.Color(255, 255, 255));
        label_kpg_wip3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_kpg_wip3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_kpg_wip3.setText("0");
        label_kpg_wip3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel32.setBackground(new java.awt.Color(255, 255, 255));
        jLabel32.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel32.setText("WIP MENJADI BARANG JADI");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel35)
                    .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_kpg_wip4, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_kpg_wip3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_kpg_wip2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_kpg_wip1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label_gram_wip1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_gram_wip2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_gram_wip3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_gram_wip4, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(label_harga_wip1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_harga_wip2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_harga_wip3, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_harga_wip4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(label_tenagakerja_wip1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_tenagakerja_wip2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_tenagakerja_wip3, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_tenagakerja_wip4, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(label_overhead_wip1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_overhead_wip2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_overhead_wip3, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_overhead_wip4, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(label_total_wip1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_wip2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_wip3, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_wip4, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel45, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel46, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel47, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel47, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel46, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel45, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(label_gram_wip1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_kpg_wip1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_overhead_wip1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_tenagakerja_wip1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_harga_wip1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_wip1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_gram_wip2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_kpg_wip2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_tenagakerja_wip2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_harga_wip2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_wip2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_overhead_wip2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_kpg_wip3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_tenagakerja_wip3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_harga_wip3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_gram_wip3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_wip3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_overhead_wip3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(label_kpg_wip4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_tenagakerja_wip4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_harga_wip4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_gram_wip4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_wip4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_overhead_wip4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        label_total_saldoAkhir_BJ.setBackground(new java.awt.Color(255, 255, 255));
        label_total_saldoAkhir_BJ.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_saldoAkhir_BJ.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_total_saldoAkhir_BJ.setText("0");
        label_total_saldoAkhir_BJ.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        label_gram_persediaan_awal_BJ.setBackground(new java.awt.Color(255, 255, 255));
        label_gram_persediaan_awal_BJ.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_gram_persediaan_awal_BJ.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_gram_persediaan_awal_BJ.setText("0");
        label_gram_persediaan_awal_BJ.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        label_overhead_siapjual_BJ.setBackground(new java.awt.Color(255, 255, 255));
        label_overhead_siapjual_BJ.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_overhead_siapjual_BJ.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_overhead_siapjual_BJ.setText("0");
        label_overhead_siapjual_BJ.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        label_overhead_penjualan_BJ.setBackground(new java.awt.Color(255, 255, 255));
        label_overhead_penjualan_BJ.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_overhead_penjualan_BJ.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_overhead_penjualan_BJ.setText("0");
        label_overhead_penjualan_BJ.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        label_tenagakerja_kenaikan_BJ.setBackground(new java.awt.Color(255, 255, 255));
        label_tenagakerja_kenaikan_BJ.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_tenagakerja_kenaikan_BJ.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_tenagakerja_kenaikan_BJ.setText("0");
        label_tenagakerja_kenaikan_BJ.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        label_harga_Pembelian_BJ.setBackground(new java.awt.Color(255, 255, 255));
        label_harga_Pembelian_BJ.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_harga_Pembelian_BJ.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_harga_Pembelian_BJ.setText("0");
        label_harga_Pembelian_BJ.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        label_gram_Pembelian_BJ.setBackground(new java.awt.Color(255, 255, 255));
        label_gram_Pembelian_BJ.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_gram_Pembelian_BJ.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_gram_Pembelian_BJ.setText("0");
        label_gram_Pembelian_BJ.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        label_overhead_Pembelian_BJ.setBackground(new java.awt.Color(255, 255, 255));
        label_overhead_Pembelian_BJ.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_overhead_Pembelian_BJ.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_overhead_Pembelian_BJ.setText("0");
        label_overhead_Pembelian_BJ.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        label_total_kenaikan_BJ.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kenaikan_BJ.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_kenaikan_BJ.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_total_kenaikan_BJ.setText("0");
        label_total_kenaikan_BJ.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel42.setBackground(new java.awt.Color(255, 255, 255));
        jLabel42.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel42.setText("BARANG JADI");

        label_harga_saldoAkhir_BJ.setBackground(new java.awt.Color(255, 255, 255));
        label_harga_saldoAkhir_BJ.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_harga_saldoAkhir_BJ.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_harga_saldoAkhir_BJ.setText("0");
        label_harga_saldoAkhir_BJ.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        label_kpg_Pembelian_BJ.setBackground(new java.awt.Color(255, 255, 255));
        label_kpg_Pembelian_BJ.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_kpg_Pembelian_BJ.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_kpg_Pembelian_BJ.setText("0");
        label_kpg_Pembelian_BJ.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel34.setBackground(new java.awt.Color(255, 255, 255));
        jLabel34.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel34.setText("PEMBELIAN BARANG JADI");

        label_kpg_kenaikan_BJ.setBackground(new java.awt.Color(255, 255, 255));
        label_kpg_kenaikan_BJ.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_kpg_kenaikan_BJ.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_kpg_kenaikan_BJ.setText("0");
        label_kpg_kenaikan_BJ.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        label_kpg_penjualan_BJ.setBackground(new java.awt.Color(255, 255, 255));
        label_kpg_penjualan_BJ.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_kpg_penjualan_BJ.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_kpg_penjualan_BJ.setText("0");
        label_kpg_penjualan_BJ.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        label_kpg_saldoAkhir_BJ.setBackground(new java.awt.Color(255, 255, 255));
        label_kpg_saldoAkhir_BJ.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_kpg_saldoAkhir_BJ.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_kpg_saldoAkhir_BJ.setText("0");
        label_kpg_saldoAkhir_BJ.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        label_total_Pembelian_BJ.setBackground(new java.awt.Color(255, 255, 255));
        label_total_Pembelian_BJ.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_Pembelian_BJ.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_total_Pembelian_BJ.setText("0");
        label_total_Pembelian_BJ.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel59.setBackground(new java.awt.Color(255, 255, 255));
        jLabel59.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel59.setText("keping");

        label_kpg_persediaan_awal_BJ.setBackground(new java.awt.Color(255, 255, 255));
        label_kpg_persediaan_awal_BJ.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_kpg_persediaan_awal_BJ.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_kpg_persediaan_awal_BJ.setText("0");
        label_kpg_persediaan_awal_BJ.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        label_tenagakerja_penjualan_BJ.setBackground(new java.awt.Color(255, 255, 255));
        label_tenagakerja_penjualan_BJ.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_tenagakerja_penjualan_BJ.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_tenagakerja_penjualan_BJ.setText("0");
        label_tenagakerja_penjualan_BJ.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        label_kpg_siapjual_BJ.setBackground(new java.awt.Color(255, 255, 255));
        label_kpg_siapjual_BJ.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_kpg_siapjual_BJ.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_kpg_siapjual_BJ.setText("0");
        label_kpg_siapjual_BJ.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel51.setBackground(new java.awt.Color(255, 255, 255));
        jLabel51.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel51.setText("Bi. Overhead");

        jLabel60.setBackground(new java.awt.Color(255, 255, 255));
        jLabel60.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel60.setText("gram");

        label_total_persediaan_awal_BJ.setBackground(new java.awt.Color(255, 255, 255));
        label_total_persediaan_awal_BJ.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_persediaan_awal_BJ.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_total_persediaan_awal_BJ.setText("0");
        label_total_persediaan_awal_BJ.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        label_overhead_kenaikan_BJ.setBackground(new java.awt.Color(255, 255, 255));
        label_overhead_kenaikan_BJ.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_overhead_kenaikan_BJ.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_overhead_kenaikan_BJ.setText("0");
        label_overhead_kenaikan_BJ.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        label_harga_persediaan_awal_BJ.setBackground(new java.awt.Color(255, 255, 255));
        label_harga_persediaan_awal_BJ.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_harga_persediaan_awal_BJ.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_harga_persediaan_awal_BJ.setText("0");
        label_harga_persediaan_awal_BJ.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        label_tenagakerja_Pembelian_BJ.setBackground(new java.awt.Color(255, 255, 255));
        label_tenagakerja_Pembelian_BJ.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_tenagakerja_Pembelian_BJ.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_tenagakerja_Pembelian_BJ.setText("0");
        label_tenagakerja_Pembelian_BJ.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        label_gram_siapjual_BJ.setBackground(new java.awt.Color(255, 255, 255));
        label_gram_siapjual_BJ.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_gram_siapjual_BJ.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_gram_siapjual_BJ.setText("0");
        label_gram_siapjual_BJ.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel54.setBackground(new java.awt.Color(255, 255, 255));
        jLabel54.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel54.setText("BARANG JADI SIAP DI JUAL");

        jLabel52.setBackground(new java.awt.Color(255, 255, 255));
        jLabel52.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel52.setText("Total");

        label_total_siapjual_BJ.setBackground(new java.awt.Color(255, 255, 255));
        label_total_siapjual_BJ.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_siapjual_BJ.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_total_siapjual_BJ.setText("0");
        label_total_siapjual_BJ.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        label_gram_saldoAkhir_BJ.setBackground(new java.awt.Color(255, 255, 255));
        label_gram_saldoAkhir_BJ.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_gram_saldoAkhir_BJ.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_gram_saldoAkhir_BJ.setText("0");
        label_gram_saldoAkhir_BJ.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        label_gram_penjualan_BJ.setBackground(new java.awt.Color(255, 255, 255));
        label_gram_penjualan_BJ.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_gram_penjualan_BJ.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_gram_penjualan_BJ.setText("0");
        label_gram_penjualan_BJ.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        label_harga_siapjual_BJ.setBackground(new java.awt.Color(255, 255, 255));
        label_harga_siapjual_BJ.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_harga_siapjual_BJ.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_harga_siapjual_BJ.setText("0");
        label_harga_siapjual_BJ.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        label_tenagakerja_siapjual_BJ.setBackground(new java.awt.Color(255, 255, 255));
        label_tenagakerja_siapjual_BJ.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_tenagakerja_siapjual_BJ.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_tenagakerja_siapjual_BJ.setText("0");
        label_tenagakerja_siapjual_BJ.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel48.setBackground(new java.awt.Color(255, 255, 255));
        jLabel48.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel48.setText("Bi. Tenaga Kerja");

        jLabel33.setBackground(new java.awt.Color(255, 255, 255));
        jLabel33.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel33.setText("KENAIKAN BARANG JADI");

        jLabel50.setBackground(new java.awt.Color(255, 255, 255));
        jLabel50.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel50.setText("PENJUALAN BARANG JADI");

        label_harga_kenaikan_BJ.setBackground(new java.awt.Color(255, 255, 255));
        label_harga_kenaikan_BJ.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_harga_kenaikan_BJ.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_harga_kenaikan_BJ.setText("0");
        label_harga_kenaikan_BJ.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        label_tenagakerja_persediaan_awal_BJ.setBackground(new java.awt.Color(255, 255, 255));
        label_tenagakerja_persediaan_awal_BJ.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_tenagakerja_persediaan_awal_BJ.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_tenagakerja_persediaan_awal_BJ.setText("0");
        label_tenagakerja_persediaan_awal_BJ.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel41.setBackground(new java.awt.Color(255, 255, 255));
        jLabel41.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel41.setText("SALDO AKHIR BARANG JADI");

        label_overhead_saldoAkhir_BJ.setBackground(new java.awt.Color(255, 255, 255));
        label_overhead_saldoAkhir_BJ.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_overhead_saldoAkhir_BJ.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_overhead_saldoAkhir_BJ.setText("0");
        label_overhead_saldoAkhir_BJ.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        label_overhead_persediaan_awal_BJ.setBackground(new java.awt.Color(255, 255, 255));
        label_overhead_persediaan_awal_BJ.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_overhead_persediaan_awal_BJ.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_overhead_persediaan_awal_BJ.setText("0");
        label_overhead_persediaan_awal_BJ.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel53.setBackground(new java.awt.Color(255, 255, 255));
        jLabel53.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel53.setText("Bi. Bahan Baku");

        label_harga_penjualan_BJ.setBackground(new java.awt.Color(255, 255, 255));
        label_harga_penjualan_BJ.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_harga_penjualan_BJ.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_harga_penjualan_BJ.setText("0");
        label_harga_penjualan_BJ.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        label_gram_kenaikan_BJ.setBackground(new java.awt.Color(255, 255, 255));
        label_gram_kenaikan_BJ.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_gram_kenaikan_BJ.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_gram_kenaikan_BJ.setText("0");
        label_gram_kenaikan_BJ.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        label_tenagakerja_saldoAkhir_BJ.setBackground(new java.awt.Color(255, 255, 255));
        label_tenagakerja_saldoAkhir_BJ.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_tenagakerja_saldoAkhir_BJ.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_tenagakerja_saldoAkhir_BJ.setText("0");
        label_tenagakerja_saldoAkhir_BJ.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        label_total_penjualan_BJ.setBackground(new java.awt.Color(255, 255, 255));
        label_total_penjualan_BJ.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_penjualan_BJ.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_total_penjualan_BJ.setText("0");
        label_total_penjualan_BJ.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel49.setBackground(new java.awt.Color(255, 255, 255));
        jLabel49.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel49.setText("PERSEDIAAN AWAL BARANG JADI");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel42)
                    .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel49, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel50, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel54, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label_kpg_Pembelian_BJ, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel59, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_kpg_kenaikan_BJ, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_kpg_persediaan_awal_BJ, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_kpg_saldoAkhir_BJ, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_kpg_penjualan_BJ, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_kpg_siapjual_BJ, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(label_gram_Pembelian_BJ, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_gram_saldoAkhir_BJ, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_gram_penjualan_BJ, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_gram_siapjual_BJ, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(label_harga_saldoAkhir_BJ, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_tenagakerja_saldoAkhir_BJ, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_overhead_saldoAkhir_BJ, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_saldoAkhir_BJ, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(label_harga_Pembelian_BJ, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_harga_penjualan_BJ, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_harga_siapjual_BJ, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(label_tenagakerja_Pembelian_BJ, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_tenagakerja_penjualan_BJ, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_tenagakerja_siapjual_BJ, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(label_overhead_Pembelian_BJ, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_overhead_penjualan_BJ, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_overhead_siapjual_BJ, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(label_total_Pembelian_BJ, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_total_penjualan_BJ, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_total_siapjual_BJ, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(label_gram_persediaan_awal_BJ, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_gram_kenaikan_BJ, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel60, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel53, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel48, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel51, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel52, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(label_harga_persediaan_awal_BJ, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_harga_kenaikan_BJ, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(label_tenagakerja_persediaan_awal_BJ, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_tenagakerja_kenaikan_BJ, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(label_overhead_persediaan_awal_BJ, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_overhead_kenaikan_BJ, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(label_total_persediaan_awal_BJ, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_total_kenaikan_BJ, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel42, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel59, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel60, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(label_kpg_persediaan_awal_BJ, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(label_kpg_kenaikan_BJ, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel49, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel53, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel48, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel51, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel52, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(11, 11, 11)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(label_gram_persediaan_awal_BJ, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(label_gram_kenaikan_BJ, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(label_harga_persediaan_awal_BJ, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_tenagakerja_persediaan_awal_BJ, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_overhead_persediaan_awal_BJ, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_total_persediaan_awal_BJ, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(label_harga_kenaikan_BJ, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_tenagakerja_kenaikan_BJ, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_overhead_kenaikan_BJ, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_total_kenaikan_BJ, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jLabel54, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jLabel50, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addComponent(label_kpg_Pembelian_BJ, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(label_kpg_siapjual_BJ, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(label_kpg_penjualan_BJ, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(label_kpg_saldoAkhir_BJ, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(label_gram_Pembelian_BJ, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(label_gram_siapjual_BJ, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(label_gram_penjualan_BJ, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(label_harga_Pembelian_BJ, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_tenagakerja_Pembelian_BJ, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_overhead_Pembelian_BJ, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_total_Pembelian_BJ, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(label_harga_siapjual_BJ, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_tenagakerja_siapjual_BJ, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_overhead_siapjual_BJ, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_total_siapjual_BJ, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(label_harga_penjualan_BJ, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_tenagakerja_penjualan_BJ, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_overhead_penjualan_BJ, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_total_penjualan_BJ, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(label_gram_saldoAkhir_BJ, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_harga_saldoAkhir_BJ, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(11, 11, 11)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(label_total_saldoAkhir_BJ, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_tenagakerja_saldoAkhir_BJ, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_overhead_saldoAkhir_BJ, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel1)
                        .addGap(6, 6, 6)
                        .addComponent(Date_filter1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_filter2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_refresh))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(238, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Date_filter1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Date_filter2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addComponent(button_refresh, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jScrollPane2.setViewportView(jPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1366, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 715, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void button_refreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refreshActionPerformed
        // TODO add your handling code here:
        if (Date_filter1.getDate() != null && Date_filter2.getDate() != null) {
            refresh_data();
        } else {
            JOptionPane.showMessageDialog(this, "Silahkan masukkan tanggal periode laporan !");
        }
    }//GEN-LAST:event_button_refreshActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser Date_filter1;
    private com.toedter.calendar.JDateChooser Date_filter2;
    private javax.swing.JButton button_refresh;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
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
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel label_gram_1;
    private javax.swing.JLabel label_gram_2;
    private javax.swing.JLabel label_gram_3;
    private javax.swing.JLabel label_gram_4;
    private javax.swing.JLabel label_gram_5;
    private javax.swing.JLabel label_gram_6;
    private javax.swing.JLabel label_gram_Pembelian_BJ;
    private javax.swing.JLabel label_gram_kenaikan_BJ;
    private javax.swing.JLabel label_gram_penjualan_BJ;
    private javax.swing.JLabel label_gram_persediaan_awal_BJ;
    private javax.swing.JLabel label_gram_saldoAkhir_BJ;
    private javax.swing.JLabel label_gram_siapjual_BJ;
    private javax.swing.JLabel label_gram_wip1;
    private javax.swing.JLabel label_gram_wip2;
    private javax.swing.JLabel label_gram_wip3;
    private javax.swing.JLabel label_gram_wip4;
    private javax.swing.JLabel label_harga_1;
    private javax.swing.JLabel label_harga_2;
    private javax.swing.JLabel label_harga_3;
    private javax.swing.JLabel label_harga_4;
    private javax.swing.JLabel label_harga_5;
    private javax.swing.JLabel label_harga_6;
    private javax.swing.JLabel label_harga_Pembelian_BJ;
    private javax.swing.JLabel label_harga_kenaikan_BJ;
    private javax.swing.JLabel label_harga_penjualan_BJ;
    private javax.swing.JLabel label_harga_persediaan_awal_BJ;
    private javax.swing.JLabel label_harga_saldoAkhir_BJ;
    private javax.swing.JLabel label_harga_siapjual_BJ;
    private javax.swing.JLabel label_harga_wip1;
    private javax.swing.JLabel label_harga_wip2;
    private javax.swing.JLabel label_harga_wip3;
    private javax.swing.JLabel label_harga_wip4;
    private javax.swing.JLabel label_kpg_1;
    private javax.swing.JLabel label_kpg_2;
    private javax.swing.JLabel label_kpg_3;
    private javax.swing.JLabel label_kpg_4;
    private javax.swing.JLabel label_kpg_5;
    private javax.swing.JLabel label_kpg_6;
    private javax.swing.JLabel label_kpg_Pembelian_BJ;
    private javax.swing.JLabel label_kpg_kenaikan_BJ;
    private javax.swing.JLabel label_kpg_penjualan_BJ;
    private javax.swing.JLabel label_kpg_persediaan_awal_BJ;
    private javax.swing.JLabel label_kpg_saldoAkhir_BJ;
    private javax.swing.JLabel label_kpg_siapjual_BJ;
    private javax.swing.JLabel label_kpg_wip1;
    private javax.swing.JLabel label_kpg_wip2;
    private javax.swing.JLabel label_kpg_wip3;
    private javax.swing.JLabel label_kpg_wip4;
    private javax.swing.JLabel label_overhead_Pembelian_BJ;
    private javax.swing.JLabel label_overhead_baku;
    private javax.swing.JLabel label_overhead_kenaikan_BJ;
    private javax.swing.JLabel label_overhead_penjualan_BJ;
    private javax.swing.JLabel label_overhead_persediaan_awal_BJ;
    private javax.swing.JLabel label_overhead_saldoAkhir_BJ;
    private javax.swing.JLabel label_overhead_siapjual_BJ;
    private javax.swing.JLabel label_overhead_wip1;
    private javax.swing.JLabel label_overhead_wip2;
    private javax.swing.JLabel label_overhead_wip3;
    private javax.swing.JLabel label_overhead_wip4;
    private javax.swing.JLabel label_tenagakerja_Pembelian_BJ;
    private javax.swing.JLabel label_tenagakerja_baku;
    private javax.swing.JLabel label_tenagakerja_kenaikan_BJ;
    private javax.swing.JLabel label_tenagakerja_penjualan_BJ;
    private javax.swing.JLabel label_tenagakerja_persediaan_awal_BJ;
    private javax.swing.JLabel label_tenagakerja_saldoAkhir_BJ;
    private javax.swing.JLabel label_tenagakerja_siapjual_BJ;
    private javax.swing.JLabel label_tenagakerja_wip1;
    private javax.swing.JLabel label_tenagakerja_wip2;
    private javax.swing.JLabel label_tenagakerja_wip3;
    private javax.swing.JLabel label_tenagakerja_wip4;
    private javax.swing.JLabel label_total_Pembelian_BJ;
    private javax.swing.JLabel label_total_biaya_baku;
    private javax.swing.JLabel label_total_kenaikan_BJ;
    private javax.swing.JLabel label_total_penjualan_BJ;
    private javax.swing.JLabel label_total_persediaan_awal_BJ;
    private javax.swing.JLabel label_total_saldoAkhir_BJ;
    private javax.swing.JLabel label_total_siapjual_BJ;
    private javax.swing.JLabel label_total_wip1;
    private javax.swing.JLabel label_total_wip2;
    private javax.swing.JLabel label_total_wip3;
    private javax.swing.JLabel label_total_wip4;
    // End of variables declaration//GEN-END:variables
}
