package waleta_system.Finance;

import waleta_system.Class.Utility;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import waleta_system.Class.ColumnsAutoSizer;

import waleta_system.Class.ExportToExcel;
import waleta_system.Class.Utility;

public class JPanel_BiayaTenagaKerja extends javax.swing.JPanel {

    
    String sql = null;
    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    String Departemen;
    int berat_basah = 0, total_berat_basah = 0;
    double harga_baku = 0, total_harga_baku = 0, berat12 = 0, total_berat12 = 0;
    double btk = 0, total_btk = 0;
    double btk_per_kg = 0, total_btk_per_kg = 0;

    public JPanel_BiayaTenagaKerja() {
        initComponents();
    }

    public void init() {
        try {
            
            
            decimalFormat = Utility.DecimalFormatUS(decimalFormat);
            decimalFormat.setMaximumFractionDigits(0);
            tabel_BTK.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent event) {
                    if (!event.getValueIsAdjusting() && tabel_BTK.getSelectedRow() != -1) {
                        int SelectedRow = tabel_BTK.getSelectedRow();
                        fungsi(SelectedRow);
                    }
                }
            });

            DefaultTableModel table_model = (DefaultTableModel) tabel_BTK.getModel();
            table_model.setRowCount(0);
            Object[] row = new Object[6];
            for (int i = 0; i < 8; i++) {
                row[0] = Departemen;
                row[1] = berat_basah;
                row[2] = berat12;
                row[3] = harga_baku;
                row[4] = btk;
                row[5] = btk_per_kg;
                table_model.addRow(row);
            }
            refresh_data();
        } catch (Exception ex) {
            Logger.getLogger(JPanel_LaporanProduksi_Keuangan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void fungsi(int n) {
        switch (n) {
            case 0:
                Support();
                label_detail_LP.setText("Detail Pengeluaran Baku");
                label_detail_TenagaKerja.setText("Detail Pekerja Support");
                break;
            case 1:
                gradingBaku();
                label_detail_LP.setText("Detail Pengeluaran Baku");
                label_detail_TenagaKerja.setText("Detail Pekerja Grading Baku");
                break;
            case 2:
                Cuci();
                label_detail_LP.setText("Detail Laporan Produksi Cuci");
                label_detail_TenagaKerja.setText("Detail Pekerja Cuci");
                break;
            case 3:
                Cabut();
                label_detail_LP.setText("Detail Laporan Produksi Cabut");
                label_detail_TenagaKerja.setText("Detail Pekerja Cabut");
                break;
            case 4:
                Cetak();
                label_detail_LP.setText("Detail Laporan Produksi Cetak");
                label_detail_TenagaKerja.setText("Detail Pekerja Cetak");
                break;
            case 5:
                f2();
                label_detail_LP.setText("Detail Laporan Produksi Finishing 2");
                label_detail_TenagaKerja.setText("Detail Pekerja Finishing 2");
                break;
            case 6:
                gradingJadi();
                label_detail_LP.setText("Detail Laporan Produksi Grading Barang Jadi");
                label_detail_TenagaKerja.setText("Detail Pekerja Grading Barang Jadi");
                break;
            case 7:
                packing();
                label_detail_LP.setText("Detail Laporan Produksi Packing");
                label_detail_TenagaKerja.setText("Detail Pekerja Packing");
                break;
            default:
                break;
        }
    }

    public void refresh_data() {
        decimalFormat.setMaximumFractionDigits(0);
        total_berat_basah = 0;
        total_berat12 = 0;
        total_harga_baku = 0;

        for (int i = 0; i < 8; i++) {
            berat_basah = 0;
            berat12 = 0;
            harga_baku = 0;
            fungsi(i);
            total_berat_basah = total_berat_basah + berat_basah;
            total_berat12 = total_berat12 + berat12;
            total_harga_baku = total_harga_baku + harga_baku;

            tabel_BTK.setValueAt(Departemen, i, 0);
            tabel_BTK.setValueAt((int) berat_basah, i, 1);
            tabel_BTK.setValueAt((long) berat12, i, 2);
            tabel_BTK.setValueAt((long) harga_baku, i, 3);
        }

        ColumnsAutoSizer.sizeColumnsToFit(tabel_BTK);
        label_total_beratbasah_BTK.setText(Integer.toString(total_berat_basah));
        label_total_berat12_BTK.setText(decimalFormat.format(total_berat12));
        label_total_hargaBaku_BTK.setText(decimalFormat.format(total_harga_baku));
    }

    public void count() {
        decimalFormat.setMaximumFractionDigits(0);
        total_btk = 0;
        total_btk_per_kg = 0;
        try {
            sql = "SELECT * FROM `tb_btk` WHERE 1";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                tabel_BTK.setValueAt(rs.getDouble("support"), 0, 4);
                tabel_BTK.setValueAt(rs.getDouble("grading_baku"), 1, 4);
                tabel_BTK.setValueAt(rs.getDouble("cuci"), 2, 4);
                tabel_BTK.setValueAt(rs.getDouble("cabut"), 3, 4);
                tabel_BTK.setValueAt(rs.getDouble("cetak"), 4, 4);
                tabel_BTK.setValueAt(rs.getDouble("f2"), 5, 4);
                tabel_BTK.setValueAt(rs.getDouble("grading_jadi"), 6, 4);
                tabel_BTK.setValueAt(rs.getDouble("packing"), 7, 4);
            }
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_BiayaTenagaKerja.class.getName()).log(Level.SEVERE, null, ex);
        }

        for (int i = 0; i < 8; i++) {
            btk = 0;
            btk_per_kg = 0;
//            btk = (double) tabel_BTK.getValueAt(i, 4);
//            berat12 = (double) tabel_BTK.getValueAt(i, 2);
            btk = Double.valueOf(tabel_BTK.getValueAt(i, 4).toString());
            berat12 = Double.valueOf(tabel_BTK.getValueAt(i, 2).toString());
            if (berat12 > 0) {
                btk_per_kg = (btk / berat12) * 1000;
            } else {
                btk_per_kg = 0;
            }
            total_btk = total_btk + btk;
            total_btk_per_kg = total_btk_per_kg + btk_per_kg;
//            tabel_BTK.setValueAt(btk, i, 4);
            tabel_BTK.setValueAt((long) btk_per_kg, i, 5);
        }
        ColumnsAutoSizer.sizeColumnsToFit(tabel_BTK);
        label_total_btk.setText(decimalFormat.format(total_btk));
        label_total_btk_kg.setText(decimalFormat.format(total_btk_per_kg));
        biaya_overhead(Float.valueOf(tabel_BTK.getValueAt(0, 2).toString()));
    }

    public void Support() {
        try {
            int total_berat_basah_lp = 0;
            double total_berat_12 = 0, total_harga_lp = 0;
            DefaultTableModel table_model = (DefaultTableModel) tabel_detail_LP.getModel();
            table_model.setRowCount(0);
            if (Date_1.getDate() != null && Date_2.getDate() != null) {
                sql = "SELECT `no_laporan_produksi`, `tanggal_lp`, `tb_laporan_produksi`.`kode_grade`, `memo_lp`, `berat_basah`, "
                        + "(`berat_kering`*1.12) AS 'berat12', (`tb_grading_bahan_baku`.`harga_bahanbaku`*`berat_basah`) AS 'hargaLP'"
                        + "FROM `tb_laporan_produksi`"
                        + "LEFT JOIN `tb_grading_bahan_baku` ON `tb_laporan_produksi`.`kode_grade` = `tb_grading_bahan_baku`.`kode_grade` AND `tb_laporan_produksi`.`no_kartu_waleta` = `tb_grading_bahan_baku`.`no_kartu_waleta` "
                        + "WHERE `tanggal_lp` BETWEEN '" + dateFormat.format(Date_1.getDate()) + "' AND '" + dateFormat.format(Date_2.getDate()) + "'";
//                System.out.println(sql);
                rs = Utility.db.getStatement().executeQuery(sql);
                Object[] row = new Object[8];
                while (rs.next()) {
                    row[0] = rs.getString("no_laporan_produksi");
                    row[1] = rs.getDate("tanggal_lp");
                    row[2] = rs.getString("kode_grade");
                    row[3] = rs.getString("memo_lp");
                    row[4] = rs.getInt("berat_basah");
                    row[5] = rs.getFloat("berat12");
                    row[6] = rs.getDouble("hargaLP");
                    row[7] = "Pekerja Support";
                    table_model.addRow(row);
                    total_berat_basah_lp = total_berat_basah_lp + rs.getInt("berat_basah");
                    total_berat_12 = total_berat_12 + rs.getDouble("berat12");
                    total_harga_lp = total_harga_lp + rs.getDouble("hargaLP");
                }
                ColumnsAutoSizer.sizeColumnsToFit(tabel_detail_LP);
                int x = tabel_detail_LP.getRowCount();
                label_total_lp.setText(Integer.toString(x));
                label_total_beratbasah_LP.setText(Integer.toString(total_berat_basah_lp));
                label_total_berat12_LP.setText(decimalFormat.format(total_berat_12));
                label_total_hargaLP.setText(decimalFormat.format(total_harga_lp));

                Departemen = "Support";
                berat_basah = total_berat_basah_lp;
                berat12 = total_berat_12;
                harga_baku = total_harga_lp;
            }
            PekerjaSupport();
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_BiayaTenagaKerja.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void PekerjaSupport() {
        if (Date_1.getDate() != null && Date_2.getDate() != null) {
            DefaultTableModel table_model = (DefaultTableModel) tabel_TenagaKerja.getModel();
            table_model.setRowCount(0);
            ColumnsAutoSizer.sizeColumnsToFit(tabel_TenagaKerja);
        }
    }

    public void gradingBaku() {
        try {
            int total_berat_basah_lp = 0;
            double total_berat_12 = 0, total_harga_lp = 0;
            DefaultTableModel table_model = (DefaultTableModel) tabel_detail_LP.getModel();
            table_model.setRowCount(0);
            if (Date_1.getDate() != null && Date_2.getDate() != null) {
                sql = "SELECT `no_laporan_produksi`, `tanggal_lp`, `tb_laporan_produksi`.`kode_grade`, `memo_lp`, `berat_basah`, "
                        + "(`berat_kering`*1.12) AS 'berat12', (`tb_grading_bahan_baku`.`harga_bahanbaku`*`berat_basah`) AS 'hargaLP'"
                        + "FROM `tb_laporan_produksi`"
                        + "LEFT JOIN `tb_grading_bahan_baku` ON `tb_laporan_produksi`.`kode_grade` = `tb_grading_bahan_baku`.`kode_grade` AND `tb_laporan_produksi`.`no_kartu_waleta` = `tb_grading_bahan_baku`.`no_kartu_waleta` "
                        + "WHERE `tanggal_lp` BETWEEN '" + dateFormat.format(Date_1.getDate()) + "' AND '" + dateFormat.format(Date_2.getDate()) + "'";
//                System.out.println(sql);
                rs = Utility.db.getStatement().executeQuery(sql);
                Object[] row = new Object[8];
                while (rs.next()) {
                    row[0] = rs.getString("no_laporan_produksi");
                    row[1] = rs.getDate("tanggal_lp");
                    row[2] = rs.getString("kode_grade");
                    row[3] = rs.getString("memo_lp");
                    row[4] = rs.getInt("berat_basah");
                    row[5] = rs.getFloat("berat12");
                    row[6] = rs.getDouble("hargaLP");
                    row[7] = "Pekerja Grading";
                    table_model.addRow(row);
                    total_berat_basah_lp = total_berat_basah_lp + rs.getInt("berat_basah");
                    total_berat_12 = total_berat_12 + rs.getDouble("berat12");
                    total_harga_lp = total_harga_lp + rs.getDouble("hargaLP");
                }
                ColumnsAutoSizer.sizeColumnsToFit(tabel_detail_LP);
                int x = tabel_detail_LP.getRowCount();
                label_total_lp.setText(Integer.toString(x));
                label_total_beratbasah_LP.setText(Integer.toString(total_berat_basah_lp));
                label_total_berat12_LP.setText(decimalFormat.format(total_berat_12));
                label_total_hargaLP.setText(decimalFormat.format(total_harga_lp));

                Departemen = "Grading Baku";
                berat_basah = total_berat_basah_lp;
                berat12 = total_berat_12;
                harga_baku = total_harga_lp;
            }
            PekerjaGradingBaku();
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_BiayaTenagaKerja.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void PekerjaGradingBaku() {
        if (Date_1.getDate() != null && Date_2.getDate() != null) {
            DefaultTableModel table_model = (DefaultTableModel) tabel_TenagaKerja.getModel();
            table_model.setRowCount(0);
            ColumnsAutoSizer.sizeColumnsToFit(tabel_TenagaKerja);
        }
    }

    public void Cuci() {
        try {
            int total_berat_basah_lp = 0;
            double total_berat_12 = 0, total_harga_lp = 0;
            DefaultTableModel table_model = (DefaultTableModel) tabel_detail_LP.getModel();
            table_model.setRowCount(0);
            if (Date_1.getDate() != null && Date_2.getDate() != null) {
                sql = "SELECT `tb_cuci`.`no_laporan_produksi`, `tgl_masuk_cuci`, `tb_laporan_produksi`.`kode_grade`, `memo_lp`, `berat_basah`, "
                        + "(`berat_kering`*1.12) AS 'berat12', (`tb_grading_bahan_baku`.`harga_bahanbaku`*`berat_basah`) AS 'hargaLP', `tb_karyawan`.`nama_pegawai` "
                        + "FROM `tb_cuci` "
                        + "LEFT JOIN `tb_laporan_produksi` ON `tb_cuci`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi` "
                        + "LEFT JOIN `tb_karyawan` ON `tb_cuci`.`id_pegawai` = `tb_karyawan`.`id_pegawai` "
                        + "LEFT JOIN `tb_grading_bahan_baku` ON `tb_laporan_produksi`.`kode_grade` = `tb_grading_bahan_baku`.`kode_grade` AND `tb_laporan_produksi`.`no_kartu_waleta` = `tb_grading_bahan_baku`.`no_kartu_waleta` "
                        + "WHERE `tgl_masuk_cuci` BETWEEN '" + dateFormat.format(Date_1.getDate()) + "' AND '" + dateFormat.format(Date_2.getDate()) + "' AND `tb_cuci`.`id_pegawai` IS NOT NULL";
//                System.out.println(sql);
                rs = Utility.db.getStatement().executeQuery(sql);
                Object[] row = new Object[8];
                while (rs.next()) {
                    row[0] = rs.getString("no_laporan_produksi");
                    row[1] = rs.getDate("tgl_masuk_cuci");
                    row[2] = rs.getString("kode_grade");
                    row[3] = rs.getString("memo_lp");
                    row[4] = rs.getInt("berat_basah");
                    row[5] = rs.getFloat("berat12");
                    row[6] = rs.getDouble("hargaLP");
                    row[7] = rs.getString("nama_pegawai");
                    table_model.addRow(row);
                    total_berat_basah_lp = total_berat_basah_lp + rs.getInt("berat_basah");
                    total_berat_12 = total_berat_12 + rs.getDouble("berat12");
                    total_harga_lp = total_harga_lp + rs.getDouble("hargaLP");
                }
                ColumnsAutoSizer.sizeColumnsToFit(tabel_detail_LP);
                int x = tabel_detail_LP.getRowCount();
                label_total_lp.setText(Integer.toString(x));
                label_total_beratbasah_LP.setText(Integer.toString(total_berat_basah_lp));
                label_total_berat12_LP.setText(decimalFormat.format(total_berat_12));
                label_total_hargaLP.setText(decimalFormat.format(total_harga_lp));

                Departemen = "Cuci";
                berat_basah = total_berat_basah_lp;
                berat12 = total_berat_12;
                harga_baku = total_harga_lp;
            }
            PekerjaCuci();
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_BiayaTenagaKerja.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void PekerjaCuci() {
        try {
            if (Date_1.getDate() != null && Date_2.getDate() != null) {
                DefaultTableModel table_model = (DefaultTableModel) tabel_TenagaKerja.getModel();
                table_model.setRowCount(0);
                sql = "SELECT `tgl_masuk_cuci`, `tb_karyawan`.`nama_pegawai`, COUNT(`tb_cuci`.`no_laporan_produksi`) AS 'jumlahLP', "
                        + "SUM(`tb_laporan_produksi`.`berat_basah`) AS 'totalBerat', 'level Gaji', '0' AS 'Upah / Hari' "
                        + "FROM `tb_cuci` "
                        + "LEFT JOIN `tb_laporan_produksi` ON `tb_cuci`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi` "
                        + "LEFT JOIN `tb_karyawan` ON `tb_cuci`.`id_pegawai` = `tb_karyawan`.`id_pegawai` "
                        + "WHERE `tgl_masuk_cuci` BETWEEN '" + dateFormat.format(Date_1.getDate()) + "' AND '" + dateFormat.format(Date_2.getDate()) + "' "
                        + "GROUP BY `tgl_masuk_cuci`, `tb_cuci`.`id_pegawai`";
                rs = Utility.db.getStatement().executeQuery(sql);
                tabel_TenagaKerja.getColumnModel().getColumn(0).setHeaderValue("Tgl Cuci");
                tabel_TenagaKerja.getColumnModel().getColumn(1).setHeaderValue("Nama");
                tabel_TenagaKerja.getColumnModel().getColumn(2).setHeaderValue("Jumlah LP");
                tabel_TenagaKerja.getColumnModel().getColumn(3).setHeaderValue("Gram");
                tabel_TenagaKerja.getColumnModel().getColumn(4).setHeaderValue("Level Gaji");
                tabel_TenagaKerja.getColumnModel().getColumn(5).setHeaderValue("Upah / Hari");
                Object[] row = new Object[6];
                while (rs.next()) {
                    row[0] = rs.getDate("tgl_masuk_cuci");
                    row[1] = rs.getString("nama_pegawai");
                    row[2] = rs.getInt("jumlahLP");
                    row[3] = rs.getInt("totalBerat");
                    row[4] = rs.getString("level Gaji");
                    row[5] = rs.getInt("Upah / Hari");
                    table_model.addRow(row);
                }
                ColumnsAutoSizer.sizeColumnsToFit(tabel_TenagaKerja);
            }
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_BiayaTenagaKerja.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void Cabut() {
        try {
            int total_berat_basah_lp = 0;
            double total_berat_12 = 0, total_harga_lp = 0;
            DefaultTableModel table_model = (DefaultTableModel) tabel_detail_LP.getModel();
            table_model.setRowCount(0);
            if (Date_1.getDate() != null && Date_2.getDate() != null) {
                sql = "SELECT `tb_cabut`.`no_laporan_produksi`, `tgl_setor_cabut`, `tb_laporan_produksi`.`kode_grade`, `memo_lp`, `berat_basah`, "
                        + "(`berat_kering`*1.12) AS 'berat12', (`tb_grading_bahan_baku`.`harga_bahanbaku`*`berat_basah`) AS 'hargaLP', `ketua_regu` "
                        + "FROM `tb_cabut` "
                        + "LEFT JOIN `tb_laporan_produksi` ON `tb_cabut`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi` "
                        + "LEFT JOIN `tb_grading_bahan_baku` ON `tb_laporan_produksi`.`kode_grade` = `tb_grading_bahan_baku`.`kode_grade` AND `tb_laporan_produksi`.`no_kartu_waleta` = `tb_grading_bahan_baku`.`no_kartu_waleta` "
                        + "WHERE `tgl_setor_cabut` BETWEEN '" + dateFormat.format(Date_1.getDate()) + "' AND '" + dateFormat.format(Date_2.getDate()) + "'";
//                System.out.println(sql);
                rs = Utility.db.getStatement().executeQuery(sql);
                Object[] row = new Object[8];
                while (rs.next()) {
                    row[0] = rs.getString("no_laporan_produksi");
                    row[1] = rs.getDate("tgl_setor_cabut");
                    row[2] = rs.getString("kode_grade");
                    row[3] = rs.getString("memo_lp");
                    row[4] = rs.getInt("berat_basah");
                    row[5] = rs.getFloat("berat12");
                    row[6] = rs.getDouble("hargaLP");
                    row[7] = rs.getString("ketua_regu");
                    table_model.addRow(row);
                    total_berat_basah_lp = total_berat_basah_lp + rs.getInt("berat_basah");
                    total_berat_12 = total_berat_12 + rs.getDouble("berat12");
                    total_harga_lp = total_harga_lp + rs.getDouble("hargaLP");
                }
                ColumnsAutoSizer.sizeColumnsToFit(tabel_detail_LP);
                int x = tabel_detail_LP.getRowCount();
                label_total_lp.setText(Integer.toString(x));
                label_total_beratbasah_LP.setText(Integer.toString(total_berat_basah_lp));
                label_total_berat12_LP.setText(decimalFormat.format(total_berat_12));
                label_total_hargaLP.setText(decimalFormat.format(total_harga_lp));

                Departemen = "Cabut";
                berat_basah = total_berat_basah_lp;
                berat12 = total_berat_12;
                harga_baku = total_harga_lp;
            }
            PekerjaCabut();
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_BiayaTenagaKerja.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void PekerjaCabut() {
        try {
            if (Date_1.getDate() != null && Date_2.getDate() != null) {
                DefaultTableModel table_model = (DefaultTableModel) tabel_TenagaKerja.getModel();
                table_model.setRowCount(0);
                sql = "SELECT `tanggal_cabut`, `tb_karyawan`.`nama_pegawai`, COUNT(`tb_detail_pencabut`.`jumlah_cabut`) AS 'jumlahCabut', "
                        + "SUM(`tb_detail_pencabut`.`jumlah_gram`) AS 'totalBerat', `grup_cabut` "
                        + "FROM `tb_detail_pencabut` "
                        + "LEFT JOIN `tb_cabut` ON `tb_detail_pencabut`.`no_laporan_produksi` = `tb_cabut`.`no_laporan_produksi` "
                        + "LEFT JOIN `tb_laporan_produksi` ON `tb_cabut`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi` "
                        + "LEFT JOIN `tb_karyawan` ON `tb_detail_pencabut`.`id_pegawai` = `tb_karyawan`.`id_pegawai` "
                        + "WHERE `tgl_setor_cabut` BETWEEN '" + dateFormat.format(Date_1.getDate()) + "' AND '" + dateFormat.format(Date_2.getDate()) + "' "
                        + "GROUP BY `tanggal_cabut`, `tb_detail_pencabut`.`id_pegawai`";
//                System.out.println(sql);
                rs = Utility.db.getStatement().executeQuery(sql);
//                String[] columnNames = {"Tanggal Cabut", "Nama Pegawai", "Type", "Date Modified", "Permissions"};
                tabel_TenagaKerja.getColumnModel().getColumn(0).setHeaderValue("Tgl Cabut");
                tabel_TenagaKerja.getColumnModel().getColumn(1).setHeaderValue("Nama");
                tabel_TenagaKerja.getColumnModel().getColumn(2).setHeaderValue("Kpg");
                tabel_TenagaKerja.getColumnModel().getColumn(3).setHeaderValue("Gram");
                tabel_TenagaKerja.getColumnModel().getColumn(4).setHeaderValue("Grup Cabut");
                tabel_TenagaKerja.getColumnModel().getColumn(5).setHeaderValue("");
                Object[] row = new Object[6];
                while (rs.next()) {
                    row[0] = rs.getDate("tanggal_cabut");
                    row[1] = rs.getString("nama_pegawai");
                    row[2] = rs.getInt("jumlahCabut");
                    row[3] = rs.getInt("totalBerat");
                    row[4] = rs.getString("grup_cabut");
                    row[5] = null;
                    table_model.addRow(row);
                }
                ColumnsAutoSizer.sizeColumnsToFit(tabel_TenagaKerja);
            }
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_BiayaTenagaKerja.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void Cetak() {
        try {
            int total_berat_basah_lp = 0;
            double total_berat_12 = 0, total_harga_lp = 0;
            DefaultTableModel table_model = (DefaultTableModel) tabel_detail_LP.getModel();
            table_model.setRowCount(0);
            if (Date_1.getDate() != null && Date_2.getDate() != null) {
                sql = "SELECT `tb_cetak`.`no_laporan_produksi`, `tgl_selesai_cetak`, `tb_laporan_produksi`.`kode_grade`, `memo_lp`, `berat_basah`, "
                        + "(`berat_kering`*1.12) AS 'berat12', (`tb_grading_bahan_baku`.`harga_bahanbaku`*`berat_basah`) AS 'hargaLP', `tb_karyawan`.`nama_pegawai` "
                        + "FROM `tb_cetak` "
                        + "LEFT JOIN `tb_laporan_produksi` ON `tb_cetak`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi` "
                        + "LEFT JOIN `tb_karyawan` ON `tb_cetak`.`cetak_dikerjakan` = `tb_karyawan`.`id_pegawai` "
                        + "LEFT JOIN `tb_grading_bahan_baku` ON `tb_laporan_produksi`.`kode_grade` = `tb_grading_bahan_baku`.`kode_grade` AND `tb_laporan_produksi`.`no_kartu_waleta` = `tb_grading_bahan_baku`.`no_kartu_waleta` "
                        + "WHERE `tgl_selesai_cetak` BETWEEN '" + dateFormat.format(Date_1.getDate()) + "' AND '" + dateFormat.format(Date_2.getDate()) + "' AND `tb_cetak`.`cetak_dikerjakan` IS NOT NULL";
//                System.out.println(sql);
                rs = Utility.db.getStatement().executeQuery(sql);
                Object[] row = new Object[8];
                while (rs.next()) {
                    row[0] = rs.getString("no_laporan_produksi");
                    row[1] = rs.getDate("tgl_selesai_cetak");
                    row[2] = rs.getString("kode_grade");
                    row[3] = rs.getString("memo_lp");
                    row[4] = rs.getInt("berat_basah");
                    row[5] = rs.getFloat("berat12");
                    row[6] = rs.getDouble("hargaLP");
                    row[7] = rs.getString("nama_pegawai");
                    table_model.addRow(row);
                    total_berat_basah_lp = total_berat_basah_lp + rs.getInt("berat_basah");
                    total_berat_12 = total_berat_12 + rs.getDouble("berat12");
                    total_harga_lp = total_harga_lp + rs.getDouble("hargaLP");
                }
                ColumnsAutoSizer.sizeColumnsToFit(tabel_detail_LP);
                int x = tabel_detail_LP.getRowCount();
                label_total_lp.setText(Integer.toString(x));
                label_total_beratbasah_LP.setText(Integer.toString(total_berat_basah_lp));
                label_total_berat12_LP.setText(decimalFormat.format(total_berat_12));
                label_total_hargaLP.setText(decimalFormat.format(total_harga_lp));

                Departemen = "Cetak";
                berat_basah = total_berat_basah_lp;
                berat12 = total_berat_12;
                harga_baku = total_harga_lp;
            }
            PekerjaCetak();
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_BiayaTenagaKerja.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void PekerjaCetak() {
        try {
            if (Date_1.getDate() != null && Date_2.getDate() != null) {
                DefaultTableModel table_model = (DefaultTableModel) tabel_TenagaKerja.getModel();
                table_model.setRowCount(0);
                sql = "SELECT `tgl_selesai_cetak`, `tb_karyawan`.`nama_pegawai`, COUNT(`tb_cetak`.`no_laporan_produksi`) AS 'jumlahLP', "
                        + "SUM(`tb_laporan_produksi`.`berat_basah`) AS 'totalBerat', 'level Gaji', '0' AS 'Upah / Hari' "
                        + "FROM `tb_cetak` "
                        + "LEFT JOIN `tb_laporan_produksi` ON `tb_cetak`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi` "
                        + "LEFT JOIN `tb_karyawan` ON `tb_cetak`.`cetak_dikerjakan` = `tb_karyawan`.`id_pegawai` "
                        + "WHERE `tgl_selesai_cetak` BETWEEN '" + dateFormat.format(Date_1.getDate()) + "' AND '" + dateFormat.format(Date_2.getDate()) + "' "
                        + "GROUP BY `tgl_selesai_cetak`, `tb_cetak`.`cetak_dikerjakan`";
                rs = Utility.db.getStatement().executeQuery(sql);
                tabel_TenagaKerja.getColumnModel().getColumn(0).setHeaderValue("Tgl Setor Cetak");
                tabel_TenagaKerja.getColumnModel().getColumn(1).setHeaderValue("Nama");
                tabel_TenagaKerja.getColumnModel().getColumn(2).setHeaderValue("Jumlah LP");
                tabel_TenagaKerja.getColumnModel().getColumn(3).setHeaderValue("Gram");
                tabel_TenagaKerja.getColumnModel().getColumn(4).setHeaderValue("Level Gaji");
                tabel_TenagaKerja.getColumnModel().getColumn(5).setHeaderValue("Upah / Hari");
                Object[] row = new Object[6];
                while (rs.next()) {
                    row[0] = rs.getDate("tgl_selesai_cetak");
                    row[1] = rs.getString("nama_pegawai");
                    row[2] = rs.getInt("jumlahLP");
                    row[3] = rs.getInt("totalBerat");
                    row[4] = rs.getString("level Gaji");
                    row[5] = rs.getInt("Upah / Hari");
                    table_model.addRow(row);
                }
                ColumnsAutoSizer.sizeColumnsToFit(tabel_TenagaKerja);
            }
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_BiayaTenagaKerja.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void f2() {
        try {
            int total_berat_basah_lp = 0;
            double total_berat_12 = 0, total_harga_lp = 0;
            DefaultTableModel table_model = (DefaultTableModel) tabel_detail_LP.getModel();
            table_model.setRowCount(0);
            if (Date_1.getDate() != null && Date_2.getDate() != null) {
                sql = "SELECT `tb_finishing_2`.`no_laporan_produksi`, `tgl_setor_f2`, `tb_laporan_produksi`.`kode_grade`, `memo_lp`, `berat_basah`, "
                        + "(`berat_kering`*1.12) AS 'berat12', (`tb_grading_bahan_baku`.`harga_bahanbaku`*`berat_basah`) AS 'hargaLP', `pekerja_koreksi_kering`, `pekerja_f1`, `pekerja_f2` "
                        + "FROM `tb_finishing_2` "
                        + "LEFT JOIN `tb_laporan_produksi` ON `tb_finishing_2`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi` "
                        + "LEFT JOIN `tb_grading_bahan_baku` ON `tb_laporan_produksi`.`kode_grade` = `tb_grading_bahan_baku`.`kode_grade` AND `tb_laporan_produksi`.`no_kartu_waleta` = `tb_grading_bahan_baku`.`no_kartu_waleta` "
                        + "WHERE `tgl_setor_f2` BETWEEN '" + dateFormat.format(Date_1.getDate()) + "' AND '" + dateFormat.format(Date_2.getDate()) + "' AND `tb_finishing_2`.`tgl_setor_f2` IS NOT NULL";
//                System.out.println(sql);
                rs = Utility.db.getStatement().executeQuery(sql);
                Object[] row = new Object[8];
                while (rs.next()) {
                    row[0] = rs.getString("no_laporan_produksi");
                    row[1] = rs.getDate("tgl_setor_f2");
                    row[2] = rs.getString("kode_grade");
                    row[3] = rs.getString("memo_lp");
                    row[4] = rs.getInt("berat_basah");
                    row[5] = rs.getFloat("berat12");
                    row[6] = rs.getDouble("hargaLP");
                    row[7] = rs.getString("pekerja_koreksi_kering") + ", " + rs.getString("pekerja_f1") + ", " + rs.getString("pekerja_f2");
                    table_model.addRow(row);
                    total_berat_basah_lp = total_berat_basah_lp + rs.getInt("berat_basah");
                    total_berat_12 = total_berat_12 + rs.getDouble("berat12");
                    total_harga_lp = total_harga_lp + rs.getDouble("hargaLP");
                }
                ColumnsAutoSizer.sizeColumnsToFit(tabel_detail_LP);
                int x = tabel_detail_LP.getRowCount();
                label_total_lp.setText(Integer.toString(x));
                label_total_beratbasah_LP.setText(Integer.toString(total_berat_basah_lp));
                label_total_berat12_LP.setText(decimalFormat.format(total_berat_12));
                label_total_hargaLP.setText(decimalFormat.format(total_harga_lp));

                Departemen = "Finishing 2";
                berat_basah = total_berat_basah_lp;
                berat12 = total_berat_12;
                harga_baku = total_harga_lp;
            }
            Pekerjaf2();
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_BiayaTenagaKerja.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void Pekerjaf2() {
        try {
            if (Date_1.getDate() != null && Date_2.getDate() != null) {
                DefaultTableModel table_model = (DefaultTableModel) tabel_TenagaKerja.getModel();
                table_model.setRowCount(0);
                sql = "SELECT `tgl_setor_f2`, 'Pejuang F2' AS 'karyawan', COUNT(`tb_finishing_2`.`no_laporan_produksi`) AS 'jumlahLP', "
                        + "SUM(`tb_laporan_produksi`.`berat_basah`) AS 'totalBerat', 'level Gaji', '0' AS 'Upah / Hari' "
                        + "FROM `tb_finishing_2` "
                        + "LEFT JOIN `tb_laporan_produksi` ON `tb_finishing_2`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi` "
                        + "WHERE `tgl_setor_f2` BETWEEN '" + dateFormat.format(Date_1.getDate()) + "' AND '" + dateFormat.format(Date_2.getDate()) + "' "
                        + "GROUP BY `tgl_setor_f2`";
                rs = Utility.db.getStatement().executeQuery(sql);
                tabel_TenagaKerja.getColumnModel().getColumn(0).setHeaderValue("Tgl Setor F2");
                tabel_TenagaKerja.getColumnModel().getColumn(1).setHeaderValue("Nama");
                tabel_TenagaKerja.getColumnModel().getColumn(2).setHeaderValue("Jumlah LP");
                tabel_TenagaKerja.getColumnModel().getColumn(3).setHeaderValue("Gram");
                tabel_TenagaKerja.getColumnModel().getColumn(4).setHeaderValue("Level Gaji");
                tabel_TenagaKerja.getColumnModel().getColumn(5).setHeaderValue("Upah / Hari");
                Object[] row = new Object[6];
                while (rs.next()) {
                    row[0] = rs.getDate("tgl_setor_f2");
                    row[1] = rs.getString("karyawan");
                    row[2] = rs.getInt("jumlahLP");
                    row[3] = rs.getInt("totalBerat");
                    row[4] = rs.getString("level Gaji");
                    row[5] = rs.getInt("Upah / Hari");
                    table_model.addRow(row);
                }
                ColumnsAutoSizer.sizeColumnsToFit(tabel_TenagaKerja);
            }
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_BiayaTenagaKerja.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void gradingJadi() {
        try {
            int total_berat_basah_lp = 0;
            double total_berat_12 = 0, total_harga_lp = 0;
            DefaultTableModel table_model = (DefaultTableModel) tabel_detail_LP.getModel();
            table_model.setRowCount(0);
            if (Date_1.getDate() != null && Date_2.getDate() != null) {
                sql = "SELECT `kode_asal`, `pekerja_grading`, `tb_laporan_produksi`.`berat_basah`, `tb_laporan_produksi`.`kode_grade`, `tb_laporan_produksi`.`memo_lp`, `tanggal_masuk`, `keping`, `berat`, (`tb_grading_bahan_baku`.`harga_bahanbaku`*`berat_basah`) AS 'hargaLP' "
                        + "FROM `tb_bahan_jadi_masuk` "
                        + "LEFT JOIN `tb_laporan_produksi` ON `tb_bahan_jadi_masuk`.`kode_asal` = `tb_laporan_produksi`.`no_laporan_produksi` "
                        + "LEFT JOIN `tb_grading_bahan_baku` ON `tb_laporan_produksi`.`kode_grade` = `tb_grading_bahan_baku`.`kode_grade` AND `tb_laporan_produksi`.`no_kartu_waleta` = `tb_grading_bahan_baku`.`no_kartu_waleta` "
                        + "WHERE `tanggal_masuk` BETWEEN '" + dateFormat.format(Date_1.getDate()) + "' AND '" + dateFormat.format(Date_2.getDate()) + "'";
//                System.out.println(sql);
                rs = Utility.db.getStatement().executeQuery(sql);
                Object[] row = new Object[8];
                while (rs.next()) {
                    row[0] = rs.getString("kode_asal");
                    row[1] = rs.getDate("tanggal_masuk");
                    row[2] = rs.getString("kode_grade");
                    row[3] = rs.getString("memo_lp");
                    row[4] = rs.getInt("berat_basah");
                    row[5] = rs.getFloat("berat");
                    row[6] = rs.getDouble("hargaLP");
                    row[7] = rs.getString("pekerja_grading");
                    table_model.addRow(row);
                    total_berat_basah_lp = total_berat_basah_lp + rs.getInt("berat_basah");
                    total_berat_12 = total_berat_12 + rs.getDouble("berat");
                    total_harga_lp = total_harga_lp + rs.getDouble("hargaLP");
                }
                ColumnsAutoSizer.sizeColumnsToFit(tabel_detail_LP);
                int x = tabel_detail_LP.getRowCount();
                label_total_lp.setText(Integer.toString(x));
                label_total_beratbasah_LP.setText(Integer.toString(total_berat_basah_lp));
                label_total_berat12_LP.setText(decimalFormat.format(total_berat_12));
                label_total_hargaLP.setText(decimalFormat.format(total_harga_lp));

                Departemen = "Grading Barang Jadi";
                berat_basah = total_berat_basah_lp;
                berat12 = total_berat_12;
                harga_baku = total_harga_lp;
            }
            PekerjaGradingJadi();
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_BiayaTenagaKerja.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void PekerjaGradingJadi() {
        try {
            if (Date_1.getDate() != null && Date_2.getDate() != null) {
                DefaultTableModel table_model = (DefaultTableModel) tabel_TenagaKerja.getModel();
                table_model.setRowCount(0);
                sql = "SELECT `tb_bahan_jadi_masuk`.`tanggal_masuk`, `pekerja_grading`, COUNT(`tb_bahan_jadi_masuk`.`kode_asal`) AS 'jumlahLP', "
                        + "SUM(`tb_bahan_jadi_masuk`.`berat`) AS 'totalBerat', 'level Gaji', '0' AS 'Upah / Hari' "
                        + "FROM `tb_bahan_jadi_masuk` "
                        + "LEFT JOIN `tb_laporan_produksi` ON `tb_bahan_jadi_masuk`.`kode_asal` = `tb_laporan_produksi`.`no_laporan_produksi` "
                        + "LEFT JOIN `tb_karyawan` ON `tb_bahan_jadi_masuk`.`pekerja_grading` = `tb_karyawan`.`nama_pegawai` "
                        + "WHERE `tb_bahan_jadi_masuk`.`tanggal_masuk` BETWEEN '" + dateFormat.format(Date_1.getDate()) + "' AND '" + dateFormat.format(Date_2.getDate()) + "' "
                        + "GROUP BY `tb_bahan_jadi_masuk`.`tanggal_masuk`, `tb_bahan_jadi_masuk`.`pekerja_grading`";
                rs = Utility.db.getStatement().executeQuery(sql);
                tabel_TenagaKerja.getColumnModel().getColumn(0).setHeaderValue("Tanggal Masuk Barang Jadi");
                tabel_TenagaKerja.getColumnModel().getColumn(1).setHeaderValue("Nama");
                tabel_TenagaKerja.getColumnModel().getColumn(2).setHeaderValue("Jumlah LP");
                tabel_TenagaKerja.getColumnModel().getColumn(3).setHeaderValue("Gram");
                tabel_TenagaKerja.getColumnModel().getColumn(4).setHeaderValue("Level Gaji");
                tabel_TenagaKerja.getColumnModel().getColumn(5).setHeaderValue("Upah / Hari");
                Object[] row = new Object[6];
                while (rs.next()) {
                    row[0] = rs.getDate("tanggal_masuk");
                    row[1] = rs.getString("pekerja_grading");
                    row[2] = rs.getInt("jumlahLP");
                    row[3] = rs.getInt("totalBerat");
                    row[4] = rs.getString("level Gaji");
                    row[5] = rs.getInt("Upah / Hari");
                    table_model.addRow(row);
                }
                ColumnsAutoSizer.sizeColumnsToFit(tabel_TenagaKerja);
            }
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_BiayaTenagaKerja.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void packing() {
        try {
            int total_berat_basah_lp = 0;
            double total_berat_12 = 0, total_harga_lp = 0;
            DefaultTableModel table_model = (DefaultTableModel) tabel_detail_LP.getModel();
            table_model.setRowCount(0);
            if (Date_1.getDate() != null && Date_2.getDate() != null) {
                sql = "SELECT `kode_asal`, `pekerja_grading`, `tb_laporan_produksi`.`berat_basah`, `tb_laporan_produksi`.`kode_grade`, `tb_laporan_produksi`.`memo_lp`, `tanggal_masuk`, `keping`, `berat`, (`tb_grading_bahan_baku`.`harga_bahanbaku`*`berat_basah`) AS 'hargaLP' "
                        + "FROM `tb_bahan_jadi_masuk` "
                        + "LEFT JOIN `tb_laporan_produksi` ON `tb_bahan_jadi_masuk`.`kode_asal` = `tb_laporan_produksi`.`no_laporan_produksi` "
                        + "LEFT JOIN `tb_grading_bahan_baku` ON `tb_laporan_produksi`.`kode_grade` = `tb_grading_bahan_baku`.`kode_grade` AND `tb_laporan_produksi`.`no_kartu_waleta` = `tb_grading_bahan_baku`.`no_kartu_waleta` "
                        + "WHERE `tanggal_masuk` BETWEEN '" + dateFormat.format(Date_1.getDate()) + "' AND '" + dateFormat.format(Date_2.getDate()) + "'";
//                System.out.println(sql);
                rs = Utility.db.getStatement().executeQuery(sql);
                Object[] row = new Object[8];
                while (rs.next()) {
                    row[0] = rs.getString("kode_asal");
                    row[1] = rs.getDate("tanggal_masuk");
                    row[2] = rs.getString("kode_grade");
                    row[3] = rs.getString("memo_lp");
                    row[4] = rs.getInt("berat_basah");
                    row[5] = rs.getFloat("berat");
                    row[6] = rs.getDouble("hargaLP");
                    row[7] = "Pekerja Packing";
                    table_model.addRow(row);
                    total_berat_basah_lp = total_berat_basah_lp + rs.getInt("berat_basah");
                    total_berat_12 = total_berat_12 + rs.getDouble("berat");
                    total_harga_lp = total_harga_lp + rs.getDouble("hargaLP");
                }
                ColumnsAutoSizer.sizeColumnsToFit(tabel_detail_LP);
                int x = tabel_detail_LP.getRowCount();
                label_total_lp.setText(Integer.toString(x));
                label_total_beratbasah_LP.setText(Integer.toString(total_berat_basah_lp));
                label_total_berat12_LP.setText(decimalFormat.format(total_berat_12));
                label_total_hargaLP.setText(decimalFormat.format(total_harga_lp));

                Departemen = "Packing";
                berat_basah = total_berat_basah_lp;
                berat12 = total_berat_12;
                harga_baku = total_harga_lp;
            }
            PekerjaPacking();
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_BiayaTenagaKerja.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void PekerjaPacking() {
        if (Date_1.getDate() != null && Date_2.getDate() != null) {
            DefaultTableModel table_model = (DefaultTableModel) tabel_TenagaKerja.getModel();
            table_model.setRowCount(0);
            ColumnsAutoSizer.sizeColumnsToFit(tabel_TenagaKerja);
        }
    }

    public void biaya_overhead(float berat_produksi12) {
        try {
            float biaya_overhead = 0;
            float overhead_kg = 0;
            sql = "SELECT SUM(`jumlah_pengeluaran`) AS 'overhead' FROM `tb_pengeluaran_keuangan` \n"
                    + "WHERE `tanggal_pengeluaran` BETWEEN '" + dateFormat.format(Date_1.getDate()) + "' AND '" + dateFormat.format(Date_2.getDate()) + "'";
//                System.out.println(sql);
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                biaya_overhead = rs.getFloat("overhead");
                label_total_overhead.setText(decimalFormat.format(biaya_overhead));
                overhead_kg = biaya_overhead / berat_produksi12;
                label_total_overhead_kg.setText(decimalFormat.format(overhead_kg));
            }
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_BiayaTenagaKerja.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        Date_1 = new com.toedter.calendar.JDateChooser();
        Date_2 = new com.toedter.calendar.JDateChooser();
        jLabel2 = new javax.swing.JLabel();
        ComboBox_ruangan = new javax.swing.JComboBox<>();
        button_refresh = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabel_BTK = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabel_detail_LP = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        label_total_lp = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        label_total_beratbasah_LP = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        label_total_berat12_LP = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        label_total_hargaLP = new javax.swing.JLabel();
        label_detail_TenagaKerja = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        label_total_hargaBaku_BTK = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        label_total_beratbasah_BTK = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        label_total_berat12_BTK = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        label_total_btk = new javax.swing.JLabel();
        label_total_btk_kg = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tabel_TenagaKerja = new javax.swing.JTable();
        label_detail_LP = new javax.swing.JLabel();
        button_inputBTK = new javax.swing.JButton();
        button_export = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        label_total_overhead = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        label_total_overhead_kg = new javax.swing.JLabel();

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Biaya Produksi", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel1.setText("Tanggal :");

        Date_1.setBackground(new java.awt.Color(255, 255, 255));
        Date_1.setDateFormatString("dd MMMM yyyy");
        Date_1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        Date_2.setBackground(new java.awt.Color(255, 255, 255));
        Date_2.setDateFormatString("dd MMMM yyyy");
        Date_2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setText("Ruangan :");

        ComboBox_ruangan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        ComboBox_ruangan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "A", "B", "C", "D" }));

        button_refresh.setBackground(new java.awt.Color(255, 255, 255));
        button_refresh.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_refresh.setText("Refresh");
        button_refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refreshActionPerformed(evt);
            }
        });

        jScrollPane1.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        tabel_BTK.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Dept", "Gr Basah", "Air Dry 12%", "Harga Baku", "BTK", "BTK / Kg"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabel_BTK.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tabel_BTK);

        jScrollPane2.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        tabel_detail_LP.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No LP", "Tgl Setor", "Grade", "Memo LP", "Berat Basah LP", "Berat Air Dry 12%", "Harga Baku", "Pekerja"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Float.class, java.lang.Double.class, java.lang.String.class
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
        tabel_detail_LP.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(tabel_detail_LP);

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel3.setText("Total LP :");

        label_total_lp.setBackground(new java.awt.Color(255, 255, 255));
        label_total_lp.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_lp.setText("0");

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel5.setText("Total Berat Basah :");

        label_total_beratbasah_LP.setBackground(new java.awt.Color(255, 255, 255));
        label_total_beratbasah_LP.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_beratbasah_LP.setText("0");

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel7.setText("Total Berat Air Dry (12%) :");

        label_total_berat12_LP.setBackground(new java.awt.Color(255, 255, 255));
        label_total_berat12_LP.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_berat12_LP.setText("0");

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel9.setText("Total Harga Baku :");

        label_total_hargaLP.setBackground(new java.awt.Color(255, 255, 255));
        label_total_hargaLP.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_hargaLP.setText("0");

        label_detail_TenagaKerja.setBackground(new java.awt.Color(255, 255, 255));
        label_detail_TenagaKerja.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_detail_TenagaKerja.setText("Detail Tenaga Kerja");

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel10.setText("Total Harga Baku :");

        label_total_hargaBaku_BTK.setBackground(new java.awt.Color(255, 255, 255));
        label_total_hargaBaku_BTK.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_hargaBaku_BTK.setText("0");

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel6.setText("Total Berat Basah :");

        label_total_beratbasah_BTK.setBackground(new java.awt.Color(255, 255, 255));
        label_total_beratbasah_BTK.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_beratbasah_BTK.setText("0");

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel8.setText("Total Berat Air Dry (12%) :");

        label_total_berat12_BTK.setBackground(new java.awt.Color(255, 255, 255));
        label_total_berat12_BTK.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_berat12_BTK.setText("0");

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel11.setText("Total Biaya Tenaga Kerja :");

        label_total_btk.setBackground(new java.awt.Color(255, 255, 255));
        label_total_btk.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_btk.setText("0");

        label_total_btk_kg.setBackground(new java.awt.Color(255, 255, 255));
        label_total_btk_kg.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_btk_kg.setText("0");

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel12.setText("Total BTK / Kg :");

        jScrollPane3.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        tabel_TenagaKerja.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tanggal", "Nama Pekerja", "Jumlah", "Berat Basah", "Level Gaji", "Upah / Hari"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabel_TenagaKerja.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(tabel_TenagaKerja);

        label_detail_LP.setBackground(new java.awt.Color(255, 255, 255));
        label_detail_LP.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_detail_LP.setText("Detail Laporan Produksi");

        button_inputBTK.setBackground(new java.awt.Color(255, 255, 255));
        button_inputBTK.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_inputBTK.setText("Input BTK");
        button_inputBTK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_inputBTKActionPerformed(evt);
            }
        });

        button_export.setBackground(new java.awt.Color(255, 255, 255));
        button_export.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_export.setText("Export");
        button_export.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_exportActionPerformed(evt);
            }
        });

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel13.setText("Total Biaya Overhead :");

        label_total_overhead.setBackground(new java.awt.Color(255, 255, 255));
        label_total_overhead.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_overhead.setText("0");

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel14.setText("Biaya Overhead / Kg :");

        label_total_overhead_kg.setBackground(new java.awt.Color(255, 255, 255));
        label_total_overhead_kg.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_overhead_kg.setText("0");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_lp)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_beratbasah_LP)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_berat12_LP)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_hargaLP))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jScrollPane1)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel1)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(Date_1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(Date_2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel2)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(ComboBox_ruangan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(button_refresh)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel8)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_berat12_BTK))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel6)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_beratbasah_BTK))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel10)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_hargaBaku_BTK))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel11)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_btk))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel12)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_btk_kg))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(button_inputBTK)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(button_export))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel14)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_overhead_kg))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel13)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_overhead))))
                            .addComponent(label_detail_LP))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 154, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 470, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_detail_TenagaKerja))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_ruangan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_refresh, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_detail_TenagaKerja, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_inputBTK, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_beratbasah_BTK, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_berat12_BTK, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_hargaBaku_BTK, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_btk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_btk_kg, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_overhead, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_overhead_kg, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(label_detail_LP, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 386, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_beratbasah_LP, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_berat12_LP, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_hargaLP, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void button_refreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refreshActionPerformed
        // TODO add your handling code here:
        refresh_data();
    }//GEN-LAST:event_button_refreshActionPerformed

    private void button_inputBTKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_inputBTKActionPerformed
        // TODO add your handling code here:
        JDialog_InputBTK btk = new JDialog_InputBTK(new javax.swing.JFrame(), true);
        btk.pack();
        btk.setLocationRelativeTo(this);
        btk.setVisible(true);
        btk.setEnabled(true);
        btk.setResizable(false);
        count();
    }//GEN-LAST:event_button_inputBTKActionPerformed

    private void button_exportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_exportActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) tabel_BTK.getModel();
        ExportToExcel.writeToExcel(model, jPanel1);
    }//GEN-LAST:event_button_exportActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_ruangan;
    private com.toedter.calendar.JDateChooser Date_1;
    private com.toedter.calendar.JDateChooser Date_2;
    private javax.swing.JButton button_export;
    private javax.swing.JButton button_inputBTK;
    private javax.swing.JButton button_refresh;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel label_detail_LP;
    private javax.swing.JLabel label_detail_TenagaKerja;
    private javax.swing.JLabel label_total_berat12_BTK;
    private javax.swing.JLabel label_total_berat12_LP;
    private javax.swing.JLabel label_total_beratbasah_BTK;
    private javax.swing.JLabel label_total_beratbasah_LP;
    private javax.swing.JLabel label_total_btk;
    private javax.swing.JLabel label_total_btk_kg;
    private javax.swing.JLabel label_total_hargaBaku_BTK;
    private javax.swing.JLabel label_total_hargaLP;
    private javax.swing.JLabel label_total_lp;
    private javax.swing.JLabel label_total_overhead;
    private javax.swing.JLabel label_total_overhead_kg;
    private javax.swing.JTable tabel_BTK;
    private javax.swing.JTable tabel_TenagaKerja;
    private javax.swing.JTable tabel_detail_LP;
    // End of variables declaration//GEN-END:variables
}
