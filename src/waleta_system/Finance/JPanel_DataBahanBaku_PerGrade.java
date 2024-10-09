package waleta_system.Finance;

import waleta_system.Class.Utility;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import waleta_system.BahanBaku.JDialog_LaporanBaku_perGrade;
import waleta_system.Class.ColumnsAutoSizer;

import waleta_system.Class.ExportToExcel;
import waleta_system.Class.GradeBahanBaku;
import waleta_system.Class.StockBahanBaku;

public class JPanel_DataBahanBaku_PerGrade extends javax.swing.JPanel {

    
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    DecimalFormat decimalFormat = new DecimalFormat();
    String sql = null;
    ResultSet rs;
    Date today = new Date();
    int min_sisa = 0;

    public JPanel_DataBahanBaku_PerGrade() {
        initComponents();
    }

    public void init() {
        
        try {
            decimalFormat.setGroupingUsed(true);
            decimalFormat.setMaximumFractionDigits(5);
            
            refreshTable_StokPerGrade();
            table_StokPerGrade.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent event) {
                    if (!event.getValueIsAdjusting() && table_StokPerGrade.getSelectedRow() != -1) {
                        int i = table_StokPerGrade.getSelectedRow();
                        label_grade1.setText(table_StokPerGrade.getValueAt(i, 0).toString());
                        label_grade2.setText(table_StokPerGrade.getValueAt(i, 0).toString());
                        label_grade3.setText(table_StokPerGrade.getValueAt(i, 0).toString());
                        label_grade4.setText(table_StokPerGrade.getValueAt(i, 0).toString());
                        refreshTable_LP();
                        refreshTable_Keluar();
                        refreshTable_Stock_grade();
                        refreshTable_DataCMP();
                    }
                }
            });
        } catch (Exception ex) {
            Logger.getLogger(JPanel_DataBahanBaku_Finance.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ArrayList<GradeBahanBaku> GradeBakuList() {
        ArrayList<GradeBahanBaku> gradeList = new ArrayList<>();
        try {
            
            if (null == ComboBox_FilterBentuk.getSelectedItem().toString()) {
                sql = "SELECT * FROM `waleta_database`.`tb_grade_bahan_baku`";
            } else {
                switch (ComboBox_FilterBentuk.getSelectedItem().toString()) {
                    case "All":
                        sql = "SELECT * FROM `waleta_database`.`tb_grade_bahan_baku`";
                        break;
                    case "-":
                        sql = "SELECT * FROM `tb_grade_bahan_baku` WHERE `jenis_bentuk` NOT LIKE '%Pecah%' AND `jenis_bentuk` NOT LIKE '%Lubang%' AND `jenis_bentuk` NOT LIKE '%segitiga%' AND `jenis_bentuk` NOT LIKE '%mangkok%' AND `jenis_bentuk` NOT LIKE '%oval%'";
                        break;
                    case "Pch / Lbg":
                        sql = "SELECT * FROM `tb_grade_bahan_baku` WHERE `jenis_bentuk` LIKE '%Pecah%' OR `jenis_bentuk` LIKE '%Lubang%'";
                        break;
                    default:
                        sql = "SELECT * FROM `tb_grade_bahan_baku` WHERE `jenis_bentuk` LIKE '%" + ComboBox_FilterBentuk.getSelectedItem().toString() + "%'";
                        break;
                }
            }
            rs = Utility.db.getStatement().executeQuery(sql);
            GradeBahanBaku gradeBaku;
            while (rs.next()) {
                gradeBaku = new GradeBahanBaku(rs.getString("kode_grade"), rs.getString("jenis_bentuk"), rs.getString("jenis_bulu"), rs.getString("jenis_warna"), rs.getString("kategori"));
                gradeList.add(gradeBaku);
            }
        } catch (Exception ex) {
            Logger.getLogger(JPanel_DataBahanBaku_PerGrade.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
        return gradeList;
    }

    public void show_data_gradeBaku() {
        int kpg_masuk = 0, gram_masuk = 0, 
                kpg_lp = 0, gram_lp = 0, 
                kpg_jual = 0, gram_jual = 0,
                kpg_cmp = 0, gram_cmp = 0;
        int total_kpg_masuk = 0, total_berat_masuk = 0, 
                total_kpg_keluar = 0, total_berat_keluar = 0, 
                total_kpg_stok = 0, total_berat_stok = 0,
                total_kpg_cmp = 0, total_berat_cmp = 0;
        double nilai_masuk = 0, nilai_lp = 0, nilai_jual = 0, nilai_cmp = 0;
        double total_nilai_stok = 0, total_nilai_masuk = 0, total_nilai_keluar = 0;
        ResultSet rs_grading, rs_jual, rs_lp, rs_cmp;
        ArrayList<GradeBahanBaku> list = GradeBakuList();
        DefaultTableModel model = (DefaultTableModel) table_StokPerGrade.getModel();
        Object[] row = new Object[8];
        for (int i = 0; i < list.size(); i++) {
            try {
                row[0] = list.get(i).getKode_grade();
                String sql_grading = "SELECT SUM(`tb_grading_bahan_baku`.`jumlah_keping`) AS 'jumlah_keping', SUM(`tb_grading_bahan_baku`.`total_berat`) AS 'total_berat', SUM(`tb_grading_bahan_baku`.`harga_bahanbaku` * `tb_grading_bahan_baku`.`total_berat`) AS 'total_harga' "
                        + "FROM `tb_grading_bahan_baku` LEFT JOIN `tb_bahan_baku_masuk` ON `tb_grading_bahan_baku`.`no_kartu_waleta` = `tb_bahan_baku_masuk`.`no_kartu_waleta`"
                        + "WHERE `kode_grade` = '" + list.get(i).getKode_grade() + "' AND `tgl_masuk`<='" + dateFormat.format(Date_filter1.getDate()) + "'";
                rs_grading = Utility.db.getStatement().executeQuery(sql_grading);
                if (rs_grading.next()) {
                    kpg_masuk = rs_grading.getInt("jumlah_keping");
                    gram_masuk = rs_grading.getInt("total_berat");
                    nilai_masuk = rs_grading.getDouble("total_harga");
                }
                String sql_lp = "SELECT SUM(`tb_laporan_produksi`.`jumlah_keping`) AS 'keping', SUM(`tb_laporan_produksi`.`berat_basah`) AS 'berat', SUM(`tb_grading_bahan_baku`.`harga_bahanbaku` * `tb_laporan_produksi`.`berat_basah`) AS 'total_harga' FROM `tb_laporan_produksi` "
                        + "LEFT JOIN `tb_grading_bahan_baku` ON (`tb_grading_bahan_baku`.`no_kartu_waleta` = `tb_laporan_produksi`.`no_kartu_waleta` AND `tb_grading_bahan_baku`.`kode_grade` = `tb_laporan_produksi`.`kode_grade`)"
                        + "WHERE `tb_laporan_produksi`.`kode_grade` = '" + list.get(i).getKode_grade() + "'  AND `tanggal_lp`<='" + dateFormat.format(Date_filter1.getDate()) + "'";
                rs_lp = Utility.db.getStatement().executeQuery(sql_lp);
                if (rs_lp.next()) {
                    kpg_lp = rs_lp.getInt("keping");
                    gram_lp = rs_lp.getInt("berat");
                    nilai_lp = rs_lp.getDouble("total_harga");
                }
                String sql_jual = "SELECT SUM(`total_keping_keluar`) AS 'keping', SUM(`total_berat_keluar`) AS 'berat', SUM(`tb_grading_bahan_baku`.`harga_bahanbaku` * `tb_bahan_baku_keluar`.`total_berat_keluar`) AS 'total_harga' FROM `tb_bahan_baku_keluar` "
                        + "LEFT JOIN `tb_bahan_baku_keluar1` ON (`tb_bahan_baku_keluar`.`kode_pengeluaran` = `tb_bahan_baku_keluar1`.`kode_pengeluaran`)"
                        + "LEFT JOIN `tb_grading_bahan_baku` ON (`tb_grading_bahan_baku`.`no_kartu_waleta` = `tb_bahan_baku_keluar`.`no_kartu_waleta` AND `tb_grading_bahan_baku`.`kode_grade` = `tb_bahan_baku_keluar`.`kode_grade`)"
                        + "WHERE `tb_bahan_baku_keluar`.`kode_grade` = '" + list.get(i).getKode_grade() + "'  AND `tgl_keluar`<='" + dateFormat.format(Date_filter1.getDate()) + "'";
                rs_jual = Utility.db.getStatement().executeQuery(sql_jual);
                if (rs_jual.next()) {
                    kpg_jual = rs_jual.getInt("keping");
                    gram_jual = rs_jual.getInt("berat");
                    nilai_jual = rs_jual.getDouble("total_harga");
                }
                
                String sql_cmp = "SELECT SUM(`keping`) AS 'keping_cmp', SUM(`gram`) AS 'berat_cmp', SUM(`gram`*`tb_grading_bahan_baku`.`harga_bahanbaku`) AS 'total_harga' FROM `tb_kartu_cmp_detail`\n"
                        + "LEFT JOIN `tb_kartu_cmp` ON `tb_kartu_cmp_detail`.`kode_kartu_cmp` = `tb_kartu_cmp`.`kode_kartu_cmp`"
                        + "LEFT JOIN `tb_grading_bahan_baku` ON `tb_kartu_cmp_detail`.`no_grading` = `tb_grading_bahan_baku`.`no_grading`\n"
                        + "WHERE `tb_grading_bahan_baku`.`kode_grade` = '" + list.get(i).getKode_grade() + "' AND `tanggal`<='" + dateFormat.format(Date_filter1.getDate()) + "'";
                rs_cmp = Utility.db.getStatement().executeQuery(sql_cmp);
                if (rs_cmp.next()) {
                    kpg_cmp = rs_cmp.getInt("keping_cmp");
                    gram_cmp = rs_cmp.getInt("berat_cmp");
                    nilai_cmp = rs_cmp.getDouble("total_harga");
                }
                row[1] = kpg_masuk;
                row[2] = gram_masuk;
                row[3] = kpg_lp + kpg_jual + kpg_cmp;
                row[4] = gram_lp + gram_jual + gram_cmp;
                int stok_kpg = kpg_masuk - (kpg_lp + kpg_jual + kpg_cmp);
                row[5] = stok_kpg;
                int stok_gram = gram_masuk - (gram_lp + gram_jual + gram_cmp);
                row[6] = stok_gram;
                double nilai_stok = nilai_masuk - (nilai_lp + nilai_jual + nilai_cmp);
                row[7] = nilai_stok;

                total_kpg_masuk = total_kpg_masuk + kpg_masuk;
                total_berat_masuk = total_berat_masuk + gram_masuk;
                total_nilai_masuk = total_nilai_masuk + nilai_masuk;
                
                total_kpg_keluar = total_kpg_keluar + (kpg_lp + kpg_jual + kpg_cmp);
                total_berat_keluar = total_berat_keluar + (gram_lp + gram_jual + gram_cmp);
                total_nilai_keluar = total_nilai_keluar + (nilai_lp + nilai_jual + nilai_cmp);
                
                total_kpg_stok = total_kpg_stok + stok_kpg;
                total_berat_stok = total_berat_stok + stok_gram;
                total_nilai_stok = total_nilai_stok + nilai_stok;

                model.addRow(row);
            } catch (SQLException | NullPointerException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
                Logger.getLogger(JDialog_LaporanBaku_perGrade.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        label_total_kpg_masuk.setText(decimalFormat.format(total_kpg_masuk));
        label_total_berat_masuk.setText(decimalFormat.format(total_berat_masuk));
        label_total_kpg_keluar.setText(decimalFormat.format(total_kpg_keluar));
        label_total_berat_keluar.setText(decimalFormat.format(total_berat_keluar));
        label_total_kpg_stok.setText(decimalFormat.format(total_kpg_stok));
        label_total_berat_stok.setText(decimalFormat.format(total_berat_stok));
        label_total_nilai_baku_masuk.setText(decimalFormat.format(total_nilai_masuk));
        label_total_nilai_baku_keluar.setText(decimalFormat.format(total_nilai_keluar));
        label_total_nilai_stok.setText(decimalFormat.format(total_nilai_stok));
    }

    public void refreshTable_StokPerGrade() {
        DefaultTableModel model = (DefaultTableModel) table_StokPerGrade.getModel();
        model.setRowCount(0);
        show_data_gradeBaku();
        ColumnsAutoSizer.sizeColumnsToFit(table_StokPerGrade);
    }

    public ArrayList<StockBahanBaku> StockList() {
        ArrayList<StockBahanBaku> StockList = new ArrayList<>();
        try {
            int i = table_StokPerGrade.getSelectedRow();
            sql = "SELECT `tb_grading_bahan_baku`.`no_grading`, `tb_grading_bahan_baku`.`no_kartu_waleta`, `tb_bahan_baku_masuk`.`tgl_masuk`, `tb_grading_bahan_baku`.`kode_grade`, `tb_grading_bahan_baku`.`jumlah_keping`, `tb_grading_bahan_baku`.`total_berat`, `tb_grading_bahan_baku`.`harga_bahanbaku`\n"
                    + "FROM `tb_grading_bahan_baku` LEFT JOIN `tb_bahan_baku_masuk` ON `tb_grading_bahan_baku`.`no_kartu_waleta` = `tb_bahan_baku_masuk`.`no_kartu_waleta`\n"
                    + "WHERE `tb_grading_bahan_baku`.`kode_grade` = '" + table_StokPerGrade.getValueAt(i, 0) + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            StockBahanBaku Stock;
            while (rs.next()) {
                Stock = new StockBahanBaku(rs.getString("no_grading"), rs.getString("no_kartu_waleta"), rs.getDate("tgl_masuk"), rs.getString("kode_grade"), rs.getInt("jumlah_keping"), rs.getInt("total_berat"), rs.getDouble("harga_bahanbaku"));
                StockList.add(Stock);
            }
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_DataBahanBaku_PerGrade.class.getName()).log(Level.SEVERE, null, ex);
        }
        return StockList;
    }

    public void show_data_Stock() {
        DefaultTableModel model = (DefaultTableModel) tabel_KartuMasuk.getModel();
        ResultSet rs_jual, rs_lp, rs_cmp;
        String sql_lp = "sql_lp", sql_jual = "sql_jual", sql_cmp = "";
        int kpg_masuk = 0, gram_masuk = 0, 
                kpg_lp = 0, gram_lp = 0, 
                kpg_jual = 0, gram_keluar = 0,
                kpg_cmp = 0, gram_cmp = 0;
        double sisa_kpg = 0, sisa_gram = 0, harga_bahanbaku = 0;
        double total_sisa_kpg = 0, total_sisa_gram = 0, total_nilai_stok = 0;
        ArrayList<StockBahanBaku> list = StockList();
        Object[] row = new Object[11];
        for (int i = 0; i < list.size(); i++) {
            row[0] = list.get(i).getNo_kartu_waleta();
            row[1] = list.get(i).getKode_grade();
            row[2] = list.get(i).getTanggal_kartu();
            kpg_masuk = list.get(i).getJumlah_keping();
            gram_masuk = list.get(i).getTotal_berat();
            harga_bahanbaku = list.get(i).getHarga_bahanbaku();
            try {
                if (Date_filter1.getDate() != null) {
                    sql_lp = "SELECT SUM(`jumlah_keping`) AS 'keping', SUM(`berat_basah`) AS 'berat' FROM `tb_laporan_produksi` "
                            + "WHERE `tb_laporan_produksi`.`no_kartu_waleta` = '" + list.get(i).getNo_kartu_waleta() + "' AND `tb_laporan_produksi`.`kode_grade` = '" + list.get(i).getKode_grade() + "' AND `tanggal_lp`<='" + dateFormat.format(Date_filter1.getDate()) + "'";
                    sql_jual = "SELECT SUM(`total_keping_keluar`) AS 'keping', SUM(`total_berat_keluar`) AS 'berat' FROM `tb_bahan_baku_keluar` "
                            + "LEFT JOIN `tb_bahan_baku_keluar1` ON (`tb_bahan_baku_keluar`.`kode_pengeluaran` = `tb_bahan_baku_keluar1`.`kode_pengeluaran`)"
                            + "WHERE `tb_bahan_baku_keluar`.`no_kartu_waleta` = '" + list.get(i).getNo_kartu_waleta() + "' AND `tb_bahan_baku_keluar`.`kode_grade` = '" + list.get(i).getKode_grade() + "' AND `tgl_keluar`<='" + dateFormat.format(Date_filter1.getDate()) + "'";
                    sql_cmp = "SELECT SUM(`keping`) AS 'keping', SUM(`gram`) AS 'berat' FROM `tb_kartu_cmp_detail`\n"
                            + "LEFT JOIN `tb_kartu_cmp` ON `tb_kartu_cmp_detail`.`kode_kartu_cmp` = `tb_kartu_cmp`.`kode_kartu_cmp`"
                            + "LEFT JOIN `tb_grading_bahan_baku` ON `tb_kartu_cmp_detail`.`no_grading` = `tb_grading_bahan_baku`.`no_grading`\n"
                            + "WHERE `tb_grading_bahan_baku`.`no_kartu_waleta` = '" + list.get(i).getNo_kartu_waleta() + "' AND `tb_grading_bahan_baku`.`kode_grade` = '" + list.get(i).getKode_grade() + "' AND `tanggal`<='" + dateFormat.format(Date_filter1.getDate()) + "'";
                } else {
                    sql_lp = "SELECT SUM(`jumlah_keping`) AS 'keping', SUM(`berat_basah`) AS 'berat' FROM `tb_laporan_produksi` "
                            + "WHERE `no_kartu_waleta` = '" + list.get(i).getNo_kartu_waleta() + "' AND `kode_grade` = '" + list.get(i).getKode_grade() + "'";
                    sql_jual = "SELECT SUM(`total_keping_keluar`) AS 'keping', SUM(`total_berat_keluar`) AS 'berat' FROM `tb_bahan_baku_keluar` "
                            + "WHERE `no_kartu_waleta` = '" + list.get(i).getNo_kartu_waleta() + "' AND `kode_grade` = '" + list.get(i).getKode_grade() + "'";
                    sql_cmp = "SELECT SUM(`keping`) AS 'keping', SUM(`gram`) AS 'berat' FROM `tb_kartu_cmp_detail`\n"
                            + "LEFT JOIN `tb_grading_bahan_baku` ON `tb_kartu_cmp_detail`.`no_grading` = `tb_grading_bahan_baku`.`no_grading`\n"
                            + "WHERE `no_kartu_waleta` = '" + list.get(i).getNo_kartu_waleta() + "' AND `kode_grade` = '" + list.get(i).getKode_grade() + "'";
                }
                
                rs_lp = Utility.db.getStatement().executeQuery(sql_lp);
                if (rs_lp.next()) {
                    kpg_lp = rs_lp.getInt("keping");
                    gram_lp = rs_lp.getInt("berat");
                }
                rs_jual = Utility.db.getStatement().executeQuery(sql_jual);
                if (rs_jual.next()) {
                    kpg_jual = rs_jual.getInt("keping");
                    gram_keluar = rs_jual.getInt("berat");
                }
                rs_cmp = Utility.db.getStatement().executeQuery(sql_cmp);
                if (rs_cmp.next()) {
                    kpg_cmp = rs_cmp.getInt("keping");
                    gram_cmp = rs_cmp.getInt("berat");
                }
            } catch (SQLException e) {
                Logger.getLogger(JPanel_DataBahanBaku_PerGrade.class.getName()).log(Level.SEVERE, null, e);
            }

            row[3] = kpg_masuk;
            row[4] = gram_masuk;
            row[5] = kpg_lp + kpg_jual;
            row[6] = gram_lp + gram_keluar;
            sisa_kpg = kpg_masuk - (kpg_lp + kpg_jual + kpg_cmp);
            row[7] = (int) sisa_kpg;
            sisa_gram = gram_masuk - (gram_lp + gram_keluar + gram_cmp);
            row[8] = (int) sisa_gram;
            row[9] = decimalFormat.format(harga_bahanbaku);
            row[10] = decimalFormat.format(harga_bahanbaku * sisa_gram);

            total_sisa_kpg = total_sisa_kpg + sisa_kpg;
            total_sisa_gram = total_sisa_gram + sisa_gram;
            total_nilai_stok = total_nilai_stok + (harga_bahanbaku * sisa_gram);
            if (CheckBox_show_dataMasihSisa.isSelected()) {
                if (sisa_gram > 0) {
                    model.addRow(row);
                }
            } else {
                model.addRow(row);
            }
        }
        label_total_sisa_kpg.setText(decimalFormat.format(total_sisa_kpg));
        label_total_sisa_gram.setText(decimalFormat.format(total_sisa_gram));
        label_total_nilai.setText(decimalFormat.format(total_nilai_stok));
    }

    public void refreshTable_Stock_grade() {
        DefaultTableModel model = (DefaultTableModel) tabel_KartuMasuk.getModel();
        model.setRowCount(0);
        show_data_Stock();
        ColumnsAutoSizer.sizeColumnsToFit(tabel_KartuMasuk);

        TableAlignment.setHorizontalAlignment(JLabel.LEADING);
        //tabel Data Bahan Baku
        for (int i = 0; i < tabel_KartuMasuk.getColumnCount(); i++) {
            tabel_KartuMasuk.getColumnModel().getColumn(i).setCellRenderer(TableAlignment);
        }
    }

    public void show_data_LP() {
        try {
            double total_kpg = 0, total_gram = 0, total_nilai = 0;
            DefaultTableModel model = (DefaultTableModel) table_lp.getModel();
            int i = table_StokPerGrade.getSelectedRow();
            if (Date_filter1.getDate() == null) {
                sql = "SELECT `no_laporan_produksi`, `tb_laporan_produksi`.`no_kartu_waleta`, `tanggal_lp`, `ruangan`, `tb_laporan_produksi`.`kode_grade`, `memo_lp`, `tb_bahan_baku_masuk`.`kadar_air_bahan_baku`, `berat_basah`, `berat_kering`, `tb_laporan_produksi`.`jumlah_keping`, `jumlah_gumpil`, `hilang_kaki_lp`, `hilang_ujung_lp`, `jumlah_sobek`, `pecah_1_lp`, `tb_grading_bahan_baku`.`harga_bahanbaku` \n"
                        + "FROM `tb_laporan_produksi` LEFT JOIN `tb_bahan_baku_masuk` ON `tb_laporan_produksi`.`no_kartu_waleta` = `tb_bahan_baku_masuk`.`no_kartu_waleta`"
                        + "LEFT JOIN `tb_grading_bahan_baku` ON (`tb_grading_bahan_baku`.`no_kartu_waleta` = `tb_laporan_produksi`.`no_kartu_waleta` AND `tb_grading_bahan_baku`.`kode_grade` = `tb_laporan_produksi`.`kode_grade`)"
                        + "WHERE `tb_laporan_produksi`.`kode_grade` = '" + table_StokPerGrade.getValueAt(i, 0) + "'";
            } else {
                sql = "SELECT `no_laporan_produksi`, `tb_laporan_produksi`.`no_kartu_waleta`, `tanggal_lp`, `ruangan`, `tb_laporan_produksi`.`kode_grade`, `memo_lp`, `tb_bahan_baku_masuk`.`kadar_air_bahan_baku`, `berat_basah`, `berat_kering`, `tb_laporan_produksi`.`jumlah_keping`, `jumlah_gumpil`, `hilang_kaki_lp`, `hilang_ujung_lp`, `jumlah_sobek`, `pecah_1_lp`, `tb_grading_bahan_baku`.`harga_bahanbaku` \n"
                        + "FROM `tb_laporan_produksi` LEFT JOIN `tb_bahan_baku_masuk` ON `tb_laporan_produksi`.`no_kartu_waleta` = `tb_bahan_baku_masuk`.`no_kartu_waleta`"
                        + "LEFT JOIN `tb_grading_bahan_baku` ON (`tb_grading_bahan_baku`.`no_kartu_waleta` = `tb_laporan_produksi`.`no_kartu_waleta` AND `tb_grading_bahan_baku`.`kode_grade` = `tb_laporan_produksi`.`kode_grade`)"
                        + "WHERE `tb_laporan_produksi`.`kode_grade` = '" + table_StokPerGrade.getValueAt(i, 0) + "' AND `tanggal_lp`<='" + dateFormat.format(Date_filter1.getDate()) + "'";
            }
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[7];
            while (rs.next()) {
                row[0] = rs.getString("no_laporan_produksi");
                row[1] = rs.getString("kode_grade");
                row[2] = rs.getDate("tanggal_lp");
                row[3] = rs.getInt("jumlah_keping");
                row[4] = rs.getInt("berat_basah");
                row[5] = decimalFormat.format(rs.getDouble("harga_bahanbaku"));
                row[6] = decimalFormat.format(rs.getDouble("harga_bahanbaku") * rs.getDouble("berat_basah"));
                model.addRow(row);

                total_kpg = total_kpg + rs.getInt("jumlah_keping");
                total_gram = total_gram + rs.getInt("berat_basah");
                total_nilai = total_nilai + (rs.getDouble("harga_bahanbaku") * rs.getDouble("berat_basah"));
            }
            label_total_lp.setText(Integer.toString(table_lp.getRowCount()));
            label_keping_lp.setText(decimalFormat.format(total_kpg));
            label_berat_lp.setText(decimalFormat.format(total_gram));
            label_total_nilai_lp.setText(decimalFormat.format(total_nilai));
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_DataBahanBaku_Finance.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_LP() {
        DefaultTableModel model = (DefaultTableModel) table_lp.getModel();
        model.setRowCount(0);
        show_data_LP();
        ColumnsAutoSizer.sizeColumnsToFit(table_lp);

        TableAlignment.setHorizontalAlignment(JLabel.LEADING);
        //tabel Data Bahan Baku
        for (int i = 0; i < table_lp.getColumnCount(); i++) {
            table_lp.getColumnModel().getColumn(i).setCellRenderer(TableAlignment);
        }
    }

    public void show_data_Keluar() {
        try {
            DefaultTableModel model = (DefaultTableModel) table_keluar.getModel();
            int i = table_StokPerGrade.getSelectedRow();
            double total_keping = 0, total_berat = 0, total_nilai = 0;
            if (Date_filter1.getDate() != null) {
                sql = "SELECT *, `tb_grading_bahan_baku`.`harga_bahanbaku` FROM `tb_bahan_baku_keluar` "
                        + "LEFT JOIN `tb_grading_bahan_baku` ON (`tb_grading_bahan_baku`.`no_kartu_waleta` = `tb_bahan_baku_keluar`.`no_kartu_waleta` AND `tb_grading_bahan_baku`.`kode_grade` = `tb_bahan_baku_keluar`.`kode_grade`)"
                        + "LEFT JOIN `tb_bahan_baku_keluar1` ON (`tb_bahan_baku_keluar`.`kode_pengeluaran` = `tb_bahan_baku_keluar1`.`kode_pengeluaran`)"
                        + "WHERE `tb_grading_bahan_baku`.`kode_grade`='" + table_StokPerGrade.getValueAt(i, 0) + "' AND `tgl_keluar`<='" + dateFormat.format(Date_filter1.getDate()) + "'";
            } else {
                sql = "SELECT *, `tb_grading_bahan_baku`.`harga_bahanbaku` FROM `tb_bahan_baku_keluar` "
                        + "LEFT JOIN `tb_grading_bahan_baku` ON (`tb_grading_bahan_baku`.`no_kartu_waleta` = `tb_bahan_baku_keluar`.`no_kartu_waleta` AND `tb_grading_bahan_baku`.`kode_grade` = `tb_bahan_baku_keluar`.`kode_grade`)"
                        + "WHERE `tb_grading_bahan_baku`.`kode_grade`='" + table_StokPerGrade.getValueAt(i, 0) + "'";
            }

            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[8];
            while (rs.next()) {
                row[0] = rs.getString("kode_pengeluaran");
                row[1] = rs.getString("kode_grade");
                row[2] = rs.getDate("tgl_keluar");
                row[3] = rs.getString("customer_baku");
                row[4] = rs.getInt("total_keping_keluar");
                row[5] = rs.getInt("total_berat_keluar");
                row[6] = decimalFormat.format(rs.getDouble("harga_bahanbaku"));
                row[7] = decimalFormat.format(rs.getInt("total_berat_keluar") * rs.getDouble("harga_bahanbaku"));
                model.addRow(row);

                total_keping = total_keping + rs.getInt("total_keping_keluar");
                total_berat = total_berat + rs.getInt("total_berat_keluar");
                total_nilai = total_nilai + (rs.getInt("total_berat_keluar") * rs.getDouble("harga_bahanbaku"));
            }
            label_total_data_jual.setText(Integer.toString(table_keluar.getRowCount()));
            label_keping_jual.setText(decimalFormat.format(total_keping));
            label_berat_jual.setText(decimalFormat.format(total_berat));
            label_total_nilai_jual.setText(decimalFormat.format(total_nilai));
//        label_berat_jual.setText(Integer.toString(total_gram));
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_DataBahanBaku_Finance.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_Keluar() {
        DefaultTableModel model = (DefaultTableModel) table_keluar.getModel();
        model.setRowCount(0);
        show_data_Keluar();
        ColumnsAutoSizer.sizeColumnsToFit(table_keluar);

        TableAlignment.setHorizontalAlignment(JLabel.LEADING);
        //tabel Data Bahan Baku
        for (int i = 0; i < table_keluar.getColumnCount(); i++) {
            table_keluar.getColumnModel().getColumn(i).setCellRenderer(TableAlignment);
        }
    }
    
    public void show_data_CMP() {
        int total_gram = 0, total_keping = 0;
        double total_nilai = 0;
        try {
            DefaultTableModel model = (DefaultTableModel) tb_cmp.getModel();
            String grade = "";
            int i = table_StokPerGrade.getSelectedRow();
            grade = table_StokPerGrade.getValueAt(i, 0).toString();
            sql = "";
            if (Date_filter1.getDate() != null) {
                sql = "SELECT `tb_kartu_cmp_detail`.`kode_kartu_cmp`, `tb_grading_bahan_baku`.`kode_grade`, `tb_kartu_cmp`.`tanggal`, `keping`, `gram`, `tb_grading_bahan_baku`.`harga_bahanbaku` \n"
                        + "FROM `tb_kartu_cmp_detail` \n"
                        + "LEFT JOIN `tb_kartu_cmp` ON `tb_kartu_cmp_detail`.`kode_kartu_cmp` = `tb_kartu_cmp`.`kode_kartu_cmp`\n"
                        + "LEFT JOIN `tb_grading_bahan_baku` ON `tb_kartu_cmp_detail`.`no_grading` = `tb_grading_bahan_baku`.`no_grading`\n"
                        + "WHERE `tb_grading_bahan_baku`.`kode_grade` = '" + grade + "' AND `tb_grading_bahan_baku`.`kode_grade` LIKE '%" + grade + "%' AND `tb_kartu_cmp`.`tanggal` <= '" + dateFormat.format(Date_filter1.getDate()) + "'";
            } else {
                sql = "SELECT `tb_kartu_cmp_detail`.`kode_kartu_cmp`, `tb_grading_bahan_baku`.`kode_grade`, `tb_kartu_cmp`.`tanggal`, `keping`, `gram`, `tb_grading_bahan_baku`.`harga_bahanbaku` \n"
                        + "FROM `tb_kartu_cmp_detail` \n"
                        + "LEFT JOIN `tb_kartu_cmp` ON `tb_kartu_cmp_detail`.`kode_kartu_cmp` = `tb_kartu_cmp`.`kode_kartu_cmp`\n"
                        + "LEFT JOIN `tb_grading_bahan_baku` ON `tb_kartu_cmp_detail`.`no_grading` = `tb_grading_bahan_baku`.`no_grading`\n"
                        + "WHERE `tb_grading_bahan_baku`.`kode_grade` = '" + grade + "' AND `tb_grading_bahan_baku`.`kode_grade` LIKE '%" + grade + "%'";
            }
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[7];
            while (rs.next()) {
                row[0] = rs.getString("kode_kartu_cmp");
                row[1] = rs.getString("kode_grade");
                row[2] = rs.getDate("tanggal");
                row[3] = rs.getInt("keping");
                row[4] = rs.getInt("gram");
                row[5] = decimalFormat.format(rs.getDouble("harga_bahanbaku"));
                row[6] = decimalFormat.format(rs.getDouble("gram") * rs.getDouble("harga_bahanbaku"));
                total_keping = total_keping + rs.getInt("keping");
                total_gram = total_gram + rs.getInt("gram");
                total_nilai = total_nilai + (rs.getDouble("gram") * rs.getDouble("harga_bahanbaku"));
                model.addRow(row);
            }
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_DataBahanBaku_PerGrade.class.getName()).log(Level.SEVERE, null, ex);
        }

        int rowData = tb_cmp.getRowCount();
        label_total_cmp.setText(Integer.toString(rowData));
        label_keping_cmp.setText(Integer.toString(total_keping));
        label_berat_cmp.setText(Integer.toString(total_gram));
        label_total_harga_cmp.setText(decimalFormat.format(total_nilai));
    }

    public void refreshTable_DataCMP() {
        DefaultTableModel model = (DefaultTableModel) tb_cmp.getModel();
        model.setRowCount(0);
        show_data_CMP();
        ColumnsAutoSizer.sizeColumnsToFit(tb_cmp);

        TableAlignment.setHorizontalAlignment(JLabel.LEADING);
        //tabel Data Bahan Baku
        for (int i = 0; i < tb_cmp.getColumnCount(); i++) {
            tb_cmp.getColumnModel().getColumn(i).setCellRenderer(TableAlignment);
        }
    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        button_search = new javax.swing.JButton();
        button_export = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        table_StokPerGrade = new javax.swing.JTable();
        Date_filter1 = new com.toedter.calendar.JDateChooser();
        ComboBox_FilterBentuk = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        label_total_berat_keluar = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        label_total_kpg_keluar = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        label_total_kpg_masuk = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        label_total_berat_masuk = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        label_total_kpg_stok = new javax.swing.JLabel();
        label_total_berat_stok = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel_Data_LP = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        jScrollPane10 = new javax.swing.JScrollPane();
        table_lp = new javax.swing.JTable();
        jLabel31 = new javax.swing.JLabel();
        label_berat_lp = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        label_total_lp = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        label_keping_lp = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        label_total_nilai_lp = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        label_grade2 = new javax.swing.JLabel();
        jPanel_Data_Keluar = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        label_berat_jual = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        label_total_data_jual = new javax.swing.JLabel();
        jScrollPane11 = new javax.swing.JScrollPane();
        table_keluar = new javax.swing.JTable();
        jLabel38 = new javax.swing.JLabel();
        label_keping_jual = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        label_total_nilai_jual = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        label_grade3 = new javax.swing.JLabel();
        jPanel_Data_KartuCMP = new javax.swing.JPanel();
        jLabel28 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        label_berat_cmp = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        label_total_cmp = new javax.swing.JLabel();
        jScrollPane12 = new javax.swing.JScrollPane();
        tb_cmp = new javax.swing.JTable();
        jLabel48 = new javax.swing.JLabel();
        label_keping_cmp = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        label_grade4 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        label_total_harga_cmp = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel34 = new javax.swing.JLabel();
        label_total_sisa_kpg = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        label_total_sisa_gram = new javax.swing.JLabel();
        CheckBox_show_dataMasihSisa = new javax.swing.JCheckBox();
        jLabel36 = new javax.swing.JLabel();
        label_total_nilai = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabel_KartuMasuk = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        label_grade1 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        label_total_nilai_stok = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        label_total_nilai_baku_keluar = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        label_total_nilai_baku_masuk = new javax.swing.JLabel();

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("Data Pada Tanggal :");

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

        table_StokPerGrade.setAutoCreateRowSorter(true);
        table_StokPerGrade.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_StokPerGrade.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode Grade", "Kpg IN", "Berat IN", "Kpg OUT", "Berat OUT", "Kpg Stok", "Berat Stok", "Nilai Stok"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Double.class
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
        jScrollPane1.setViewportView(table_StokPerGrade);

        Date_filter1.setBackground(new java.awt.Color(255, 255, 255));
        Date_filter1.setDate(today);
        Date_filter1.setDateFormatString("dd MMMM yyyy");
        Date_filter1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        ComboBox_FilterBentuk.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_FilterBentuk.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Mangkok", "Segitiga", "Oval", "Pch / Lbg", "-" }));
        ComboBox_FilterBentuk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ComboBox_FilterBentukActionPerformed(evt);
            }
        });

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("Bentuk Grade :");

        label_total_berat_keluar.setBackground(new java.awt.Color(255, 255, 255));
        label_total_berat_keluar.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_berat_keluar.setText("0");

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel4.setText("Berat Keluar :");

        label_total_kpg_keluar.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kpg_keluar.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_kpg_keluar.setText("0");

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel6.setText("Keping Keluar :");

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel7.setText("Keping Masuk :");

        label_total_kpg_masuk.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kpg_masuk.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_kpg_masuk.setText("0");

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel9.setText("Berat Masuk :");

        label_total_berat_masuk.setBackground(new java.awt.Color(255, 255, 255));
        label_total_berat_masuk.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_berat_masuk.setText("0");

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel11.setText("Keping Sisa :");

        label_total_kpg_stok.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kpg_stok.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_kpg_stok.setText("0");

        label_total_berat_stok.setBackground(new java.awt.Color(255, 255, 255));
        label_total_berat_stok.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_berat_stok.setText("0");

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel14.setText("Berat Sisa :");

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jPanel_Data_LP.setBackground(new java.awt.Color(255, 255, 255));

        jLabel24.setBackground(new java.awt.Color(255, 255, 255));
        jLabel24.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel24.setText("DATA LAPORAN PRODUKSI");

        table_lp.setAutoCreateRowSorter(true);
        table_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_lp.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No LP", "Grade LP", "Tgl LP", "Kpg", "Berat", "Harga Satuan", "Nilai (Rp.)"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Double.class, java.lang.Double.class
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
        table_lp.setRowSelectionAllowed(false);
        jScrollPane10.setViewportView(table_lp);

        jLabel31.setBackground(new java.awt.Color(255, 255, 255));
        jLabel31.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel31.setText("Total Berat :");

        label_berat_lp.setBackground(new java.awt.Color(255, 255, 255));
        label_berat_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_berat_lp.setText("0");

        jLabel27.setBackground(new java.awt.Color(255, 255, 255));
        jLabel27.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel27.setText("Total Data :");

        label_total_lp.setBackground(new java.awt.Color(255, 255, 255));
        label_total_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_lp.setText("0");

        jLabel32.setBackground(new java.awt.Color(255, 255, 255));
        jLabel32.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel32.setText("Total Keping :");

        label_keping_lp.setBackground(new java.awt.Color(255, 255, 255));
        label_keping_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_keping_lp.setText("0");

        jLabel37.setBackground(new java.awt.Color(255, 255, 255));
        jLabel37.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel37.setText("Total Nilai LP :");

        label_total_nilai_lp.setBackground(new java.awt.Color(255, 255, 255));
        label_total_nilai_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_nilai_lp.setText("0");

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel10.setText("Grade :");

        label_grade2.setBackground(new java.awt.Color(255, 255, 255));
        label_grade2.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_grade2.setText("GRADE");

        javax.swing.GroupLayout jPanel_Data_LPLayout = new javax.swing.GroupLayout(jPanel_Data_LP);
        jPanel_Data_LP.setLayout(jPanel_Data_LPLayout);
        jPanel_Data_LPLayout.setHorizontalGroup(
            jPanel_Data_LPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Data_LPLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_Data_LPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_Data_LPLayout.createSequentialGroup()
                        .addComponent(jLabel24)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_grade2))
                    .addComponent(jScrollPane10, javax.swing.GroupLayout.DEFAULT_SIZE, 686, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_Data_LPLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel32)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_keping_lp)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel31)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_berat_lp)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel37)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_nilai_lp)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel27)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_lp)))
                .addContainerGap())
        );
        jPanel_Data_LPLayout.setVerticalGroup(
            jPanel_Data_LPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Data_LPLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_Data_LPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_grade2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane10, javax.swing.GroupLayout.DEFAULT_SIZE, 241, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_Data_LPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_total_lp)
                    .addComponent(jLabel27)
                    .addComponent(label_berat_lp)
                    .addComponent(jLabel31)
                    .addComponent(label_keping_lp)
                    .addComponent(jLabel32)
                    .addComponent(label_total_nilai_lp)
                    .addComponent(jLabel37))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Data Laporan Produksi", jPanel_Data_LP);

        jPanel_Data_Keluar.setBackground(new java.awt.Color(255, 255, 255));

        jLabel25.setBackground(new java.awt.Color(255, 255, 255));
        jLabel25.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel25.setText("DATA PENGELUARAN BAHAN BAKU");

        jLabel33.setBackground(new java.awt.Color(255, 255, 255));
        jLabel33.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel33.setText("Total Berat :");

        label_berat_jual.setBackground(new java.awt.Color(255, 255, 255));
        label_berat_jual.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_berat_jual.setText("0");

        jLabel29.setBackground(new java.awt.Color(255, 255, 255));
        jLabel29.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel29.setText("Total Data :");

        label_total_data_jual.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_jual.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_data_jual.setText("0");

        table_keluar.setAutoCreateRowSorter(true);
        table_keluar.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_keluar.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode Keluar", "Grade", "Tanggal Keluar", "Customer", "Jumlah Keping", "Total Berat", "Harga Satuan", "Nilai (Rp.)"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Long.class, java.lang.Long.class
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
        table_keluar.setRowSelectionAllowed(false);
        jScrollPane11.setViewportView(table_keluar);

        jLabel38.setBackground(new java.awt.Color(255, 255, 255));
        jLabel38.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel38.setText("Total Keping :");

        label_keping_jual.setBackground(new java.awt.Color(255, 255, 255));
        label_keping_jual.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_keping_jual.setText("0");

        jLabel39.setBackground(new java.awt.Color(255, 255, 255));
        jLabel39.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel39.setText("Total Nilai Keluar :");

        label_total_nilai_jual.setBackground(new java.awt.Color(255, 255, 255));
        label_total_nilai_jual.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_nilai_jual.setText("0");

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel12.setText("Grade :");

        label_grade3.setBackground(new java.awt.Color(255, 255, 255));
        label_grade3.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_grade3.setText("GRADE");

        javax.swing.GroupLayout jPanel_Data_KeluarLayout = new javax.swing.GroupLayout(jPanel_Data_Keluar);
        jPanel_Data_Keluar.setLayout(jPanel_Data_KeluarLayout);
        jPanel_Data_KeluarLayout.setHorizontalGroup(
            jPanel_Data_KeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Data_KeluarLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_Data_KeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_Data_KeluarLayout.createSequentialGroup()
                        .addComponent(jLabel25)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_grade3))
                    .addComponent(jScrollPane11, javax.swing.GroupLayout.DEFAULT_SIZE, 686, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_Data_KeluarLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel38)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_keping_jual)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel33)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_berat_jual)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel39)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_nilai_jual)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel29)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_data_jual)))
                .addContainerGap())
        );
        jPanel_Data_KeluarLayout.setVerticalGroup(
            jPanel_Data_KeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Data_KeluarLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_Data_KeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_Data_KeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_grade3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel25))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane11, javax.swing.GroupLayout.DEFAULT_SIZE, 241, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_Data_KeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_Data_KeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_total_nilai_jual)
                        .addComponent(jLabel39))
                    .addGroup(jPanel_Data_KeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_keping_jual)
                        .addComponent(jLabel38))
                    .addGroup(jPanel_Data_KeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_berat_jual)
                        .addComponent(jLabel33))
                    .addGroup(jPanel_Data_KeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_total_data_jual)
                        .addComponent(jLabel29)))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Data Jual Bahan Baku", jPanel_Data_Keluar);

        jPanel_Data_KartuCMP.setBackground(new java.awt.Color(255, 255, 255));

        jLabel28.setBackground(new java.awt.Color(255, 255, 255));
        jLabel28.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel28.setText("DATA KARTU CAMPURAN");

        jLabel47.setBackground(new java.awt.Color(255, 255, 255));
        jLabel47.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel47.setText("Total Berat :");

        label_berat_cmp.setBackground(new java.awt.Color(255, 255, 255));
        label_berat_cmp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_berat_cmp.setText("0");

        jLabel30.setBackground(new java.awt.Color(255, 255, 255));
        jLabel30.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel30.setText("Total Data :");

        label_total_cmp.setBackground(new java.awt.Color(255, 255, 255));
        label_total_cmp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_cmp.setText("0");

        tb_cmp.setAutoCreateRowSorter(true);
        tb_cmp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tb_cmp.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode Kartu", "Grade", "Tanggal Kartu", "Jumlah Keping", "Total Berat", "Harga (Rp.)", "Total (Rp.)"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Double.class, java.lang.Double.class
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
        tb_cmp.setRowSelectionAllowed(false);
        tb_cmp.getTableHeader().setReorderingAllowed(false);
        jScrollPane12.setViewportView(tb_cmp);

        jLabel48.setBackground(new java.awt.Color(255, 255, 255));
        jLabel48.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel48.setText("Total Keping :");

        label_keping_cmp.setBackground(new java.awt.Color(255, 255, 255));
        label_keping_cmp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_keping_cmp.setText("0");

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel5.setText("Grade :");

        label_grade4.setBackground(new java.awt.Color(255, 255, 255));
        label_grade4.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_grade4.setText("KARTU");

        jLabel49.setBackground(new java.awt.Color(255, 255, 255));
        jLabel49.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel49.setText("Total Harga :");

        label_total_harga_cmp.setBackground(new java.awt.Color(255, 255, 255));
        label_total_harga_cmp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_harga_cmp.setText("0");

        javax.swing.GroupLayout jPanel_Data_KartuCMPLayout = new javax.swing.GroupLayout(jPanel_Data_KartuCMP);
        jPanel_Data_KartuCMP.setLayout(jPanel_Data_KartuCMPLayout);
        jPanel_Data_KartuCMPLayout.setHorizontalGroup(
            jPanel_Data_KartuCMPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Data_KartuCMPLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_Data_KartuCMPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_Data_KartuCMPLayout.createSequentialGroup()
                        .addComponent(jLabel28)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_grade4))
                    .addComponent(jScrollPane12, javax.swing.GroupLayout.DEFAULT_SIZE, 686, Short.MAX_VALUE)
                    .addGroup(jPanel_Data_KartuCMPLayout.createSequentialGroup()
                        .addComponent(jLabel48)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_keping_cmp)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel47)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_berat_cmp)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel49)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_harga_cmp)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel30)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_cmp)))
                .addContainerGap())
        );
        jPanel_Data_KartuCMPLayout.setVerticalGroup(
            jPanel_Data_KartuCMPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Data_KartuCMPLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_Data_KartuCMPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_grade4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane12, javax.swing.GroupLayout.DEFAULT_SIZE, 241, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_Data_KartuCMPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_Data_KartuCMPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_berat_cmp)
                        .addComponent(jLabel47)
                        .addComponent(label_keping_cmp)
                        .addComponent(jLabel48))
                    .addGroup(jPanel_Data_KartuCMPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_total_harga_cmp)
                        .addComponent(jLabel49))
                    .addGroup(jPanel_Data_KartuCMPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_total_cmp)
                        .addComponent(jLabel30)))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Data Kartu Campuran", jPanel_Data_KartuCMP);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Data Bahan Baku Masuk", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 12))); // NOI18N

        jLabel34.setBackground(new java.awt.Color(255, 255, 255));
        jLabel34.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel34.setText("Total Sisa Keping :");

        label_total_sisa_kpg.setBackground(new java.awt.Color(255, 255, 255));
        label_total_sisa_kpg.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_sisa_kpg.setText("0");

        jLabel35.setBackground(new java.awt.Color(255, 255, 255));
        jLabel35.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel35.setText("Total Sisa Berat :");

        label_total_sisa_gram.setBackground(new java.awt.Color(255, 255, 255));
        label_total_sisa_gram.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_sisa_gram.setText("0");

        CheckBox_show_dataMasihSisa.setBackground(new java.awt.Color(255, 255, 255));
        CheckBox_show_dataMasihSisa.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        CheckBox_show_dataMasihSisa.setText("Data Stok Only");
        CheckBox_show_dataMasihSisa.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                CheckBox_show_dataMasihSisaItemStateChanged(evt);
            }
        });

        jLabel36.setBackground(new java.awt.Color(255, 255, 255));
        jLabel36.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel36.setText("Total Nilai Stok :");

        label_total_nilai.setBackground(new java.awt.Color(255, 255, 255));
        label_total_nilai.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_nilai.setText("0");

        tabel_KartuMasuk.setAutoCreateRowSorter(true);
        tabel_KartuMasuk.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tabel_KartuMasuk.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No Kartu", "Grade", "Tgl Masuk", "Kpg IN", "Berat IN", "Kpg OUT", "Berat OUT", "Kpg Stok", "Berat Stok", "Harga", "Nilai Stok"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Double.class, java.lang.Double.class
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
        tabel_KartuMasuk.setRowSelectionAllowed(false);
        tabel_KartuMasuk.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(tabel_KartuMasuk);

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel3.setText("Baku Masuk untuk grade :");

        label_grade1.setBackground(new java.awt.Color(255, 255, 255));
        label_grade1.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_grade1.setText("-");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_grade1, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(CheckBox_show_dataMasihSisa))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel34)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_sisa_kpg)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel35)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_sisa_gram)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel36)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_nilai)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(CheckBox_show_dataMasihSisa, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_grade1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 256, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_total_nilai)
                        .addComponent(jLabel36))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_total_sisa_gram)
                        .addComponent(jLabel35)
                        .addComponent(label_total_sisa_kpg)
                        .addComponent(jLabel34)))
                .addContainerGap())
        );

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel13.setText("Total Nilai Stok :");

        label_total_nilai_stok.setBackground(new java.awt.Color(255, 255, 255));
        label_total_nilai_stok.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_nilai_stok.setText("0");

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel15.setText("Total Nilai Baku Keluar :");

        label_total_nilai_baku_keluar.setBackground(new java.awt.Color(255, 255, 255));
        label_total_nilai_baku_keluar.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_nilai_baku_keluar.setText("0");

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel16.setText("Total Nilai Baku Masuk :");

        label_total_nilai_baku_masuk.setBackground(new java.awt.Color(255, 255, 255));
        label_total_nilai_baku_masuk.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_nilai_baku_masuk.setText("0");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_FilterBentuk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_filter1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_search)
                        .addGap(44, 44, 44)
                        .addComponent(button_export))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 629, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_berat_masuk, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_kpg_masuk, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_kpg_keluar, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_kpg_stok, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_berat_keluar, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_berat_stok, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_nilai_stok, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_nilai_baku_keluar, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_nilai_baku_masuk, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTabbedPane1))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(ComboBox_FilterBentuk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(Date_filter1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(button_search, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(button_export, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(label_total_kpg_masuk, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(label_total_berat_masuk, javax.swing.GroupLayout.Alignment.TRAILING)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(label_total_kpg_keluar)
                                    .addComponent(jLabel6)
                                    .addComponent(label_total_kpg_stok)
                                    .addComponent(jLabel11))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(label_total_berat_keluar)
                                    .addComponent(jLabel4)
                                    .addComponent(label_total_berat_stok)
                                    .addComponent(jLabel14))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel16)
                            .addComponent(label_total_nilai_baku_masuk))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel15)
                            .addComponent(label_total_nilai_baku_keluar))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel13)
                            .addComponent(label_total_nilai_stok)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTabbedPane1)))
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

    private void button_searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_searchActionPerformed
        // TODO add your handling code here:
        refreshTable_StokPerGrade();
    }//GEN-LAST:event_button_searchActionPerformed

    private void button_exportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_exportActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) table_StokPerGrade.getModel();
        ExportToExcel.writeToExcel(model, jPanel1);
    }//GEN-LAST:event_button_exportActionPerformed

    private void ComboBox_FilterBentukActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ComboBox_FilterBentukActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ComboBox_FilterBentukActionPerformed

    private void CheckBox_show_dataMasihSisaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_CheckBox_show_dataMasihSisaItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_CheckBox_show_dataMasihSisaItemStateChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox CheckBox_show_dataMasihSisa;
    private javax.swing.JComboBox<String> ComboBox_FilterBentuk;
    private com.toedter.calendar.JDateChooser Date_filter1;
    private javax.swing.JButton button_export;
    private javax.swing.JButton button_search;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
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
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel_Data_KartuCMP;
    private javax.swing.JPanel jPanel_Data_Keluar;
    private javax.swing.JPanel jPanel_Data_LP;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel label_berat_cmp;
    private javax.swing.JLabel label_berat_jual;
    private javax.swing.JLabel label_berat_lp;
    private javax.swing.JLabel label_grade1;
    private javax.swing.JLabel label_grade2;
    private javax.swing.JLabel label_grade3;
    private javax.swing.JLabel label_grade4;
    private javax.swing.JLabel label_keping_cmp;
    private javax.swing.JLabel label_keping_jual;
    private javax.swing.JLabel label_keping_lp;
    private javax.swing.JLabel label_total_berat_keluar;
    private javax.swing.JLabel label_total_berat_masuk;
    private javax.swing.JLabel label_total_berat_stok;
    private javax.swing.JLabel label_total_cmp;
    private javax.swing.JLabel label_total_data_jual;
    private javax.swing.JLabel label_total_harga_cmp;
    private javax.swing.JLabel label_total_kpg_keluar;
    private javax.swing.JLabel label_total_kpg_masuk;
    private javax.swing.JLabel label_total_kpg_stok;
    private javax.swing.JLabel label_total_lp;
    private javax.swing.JLabel label_total_nilai;
    private javax.swing.JLabel label_total_nilai_baku_keluar;
    private javax.swing.JLabel label_total_nilai_baku_masuk;
    private javax.swing.JLabel label_total_nilai_jual;
    private javax.swing.JLabel label_total_nilai_lp;
    private javax.swing.JLabel label_total_nilai_stok;
    private javax.swing.JLabel label_total_sisa_gram;
    private javax.swing.JLabel label_total_sisa_kpg;
    private javax.swing.JTable tabel_KartuMasuk;
    private javax.swing.JTable table_StokPerGrade;
    private javax.swing.JTable table_keluar;
    private javax.swing.JTable table_lp;
    private javax.swing.JTable tb_cmp;
    // End of variables declaration//GEN-END:variables
}
