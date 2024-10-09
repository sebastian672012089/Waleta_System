package waleta_system.Finance;

import waleta_system.Class.Utility;

import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.DataBahanBaku;
import waleta_system.Class.ExportToExcel;
import waleta_system.Class.StockBahanBaku;
import waleta_system.MainForm;

public class JPanel_DataBahanBaku_Finance extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    DecimalFormat decimalFormat = new DecimalFormat();

    public JPanel_DataBahanBaku_Finance() {
        initComponents();
    }

    public void init() {
        try {
            decimalFormat.setGroupingUsed(true);
            decimalFormat.setMaximumFractionDigits(5);

            refreshTable_BahanBaku();
//            refreshTable_Stock_grade();
            sql = "SELECT `kode_grade` FROM `tb_grade_bahan_baku` ORDER BY `kode_grade`";
            ResultSet rs1 = Utility.db.getStatement().executeQuery(sql);
            while (rs1.next()) {
                ComboBox_Search_grade.addItem(rs1.getString("kode_grade"));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_DataBahanBaku_Finance.class.getName()).log(Level.SEVERE, null, ex);
        }
        AutoCompleteDecorator.decorate(ComboBox_Search_grade);
        table_data_bahan_baku.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && table_data_bahan_baku.getSelectedRow() != -1) {
                    Table_stock_bahan_baku.clearSelection();
                    int i = table_data_bahan_baku.getSelectedRow();
                    label_no_kartu.setText(table_data_bahan_baku.getValueAt(i, 0).toString());
                    label_no_kartu1.setText(table_data_bahan_baku.getValueAt(i, 0).toString());
                    label_no_kartu2.setText(table_data_bahan_baku.getValueAt(i, 0).toString());
                    label_no_kartu3.setText(table_data_bahan_baku.getValueAt(i, 0).toString());
                    refreshTable_LP();
                    refreshTable_Keluar();
                    refreshTable_Stock_grade();
                    refreshTable_DataCMP();
                }
            }
        });
        Table_stock_bahan_baku.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && Table_stock_bahan_baku.getSelectedRow() != -1) {
                    table_data_bahan_baku.clearSelection();
                    refreshTable_LP();
                    refreshTable_Keluar();
                }
            }
        });
    }

    public ArrayList<DataBahanBaku> BahanBakuList() {
        ArrayList<DataBahanBaku> BahanBakuList = new ArrayList<>();
        try {

            if (Date_FilterStok.getDate() != null) {
                sql = "SELECT `tb_bahan_baku_masuk`.`no_kartu_waleta`, `tb_supplier`.`nama_supplier`, `tb_rumah_burung`.`nama_rumah_burung`, `tgl_masuk`, `tgl_panen`, `tgl_grading`, `tgl_timbang`, `tb_lab_bahan_baku`.`nitrit_bm_w3`, `kadar_air_bahan_baku`, `berat_awal`, `berat_real`, `keping_real`, SUM(`tb_grading_bahan_baku`.`harga_bahanbaku` * `tb_grading_bahan_baku`.`total_berat`) AS 'total_harga', `last_stok` \n"
                        + "FROM `tb_bahan_baku_masuk` \n"
                        + "LEFT JOIN `tb_rumah_burung` ON `tb_bahan_baku_masuk`.`no_registrasi`=`tb_rumah_burung`.`no_registrasi`\n"
                        + "LEFT JOIN `tb_supplier` ON `tb_bahan_baku_masuk`.`kode_supplier`=`tb_supplier`.`kode_supplier` \n"
                        + "LEFT JOIN `tb_lab_bahan_baku` ON `tb_bahan_baku_masuk`.`no_kartu_waleta` = `tb_lab_bahan_baku`.`no_kartu_waleta`\n"
                        + "LEFT JOIN `tb_grading_bahan_baku` ON `tb_bahan_baku_masuk`.`no_kartu_waleta` = `tb_grading_bahan_baku`.`no_kartu_waleta`\n"
                        + "WHERE `tb_bahan_baku_masuk`.`no_kartu_waleta` LIKE '%" + txt_search_data_bahan_baku.getText() + "%' AND `tgl_masuk`<='" + dateFormat.format(Date_FilterStok.getDate()) + "'\n"
                        + "GROUP BY(`tb_bahan_baku_masuk`.`no_kartu_waleta`)";
            } else {
                sql = "SELECT `tb_bahan_baku_masuk`.`no_kartu_waleta`, `tb_supplier`.`nama_supplier`, `tb_rumah_burung`.`nama_rumah_burung`, `tgl_masuk`, `tgl_panen`, `tgl_grading`, `tgl_timbang`, `tb_lab_bahan_baku`.`nitrit_bm_w3`, `kadar_air_bahan_baku`, `berat_awal`, `berat_real`, `keping_real`, SUM(`tb_grading_bahan_baku`.`harga_bahanbaku` * `tb_grading_bahan_baku`.`total_berat`) AS 'total_harga', `last_stok` \n"
                        + "FROM `tb_bahan_baku_masuk` \n"
                        + "LEFT JOIN `tb_rumah_burung` ON `tb_bahan_baku_masuk`.`no_registrasi`=`tb_rumah_burung`.`no_registrasi`\n"
                        + "LEFT JOIN `tb_supplier` ON `tb_bahan_baku_masuk`.`kode_supplier`=`tb_supplier`.`kode_supplier` \n"
                        + "LEFT JOIN `tb_lab_bahan_baku` ON `tb_bahan_baku_masuk`.`no_kartu_waleta` = `tb_lab_bahan_baku`.`no_kartu_waleta`\n"
                        + "LEFT JOIN `tb_grading_bahan_baku` ON `tb_bahan_baku_masuk`.`no_kartu_waleta` = `tb_grading_bahan_baku`.`no_kartu_waleta`\n"
                        + "WHERE `tb_bahan_baku_masuk`.`no_kartu_waleta` LIKE '%" + txt_search_data_bahan_baku.getText() + "%'\n"
                        + "GROUP BY(`tb_bahan_baku_masuk`.`no_kartu_waleta`)";
            }
            rs = Utility.db.getStatement().executeQuery(sql);
            DataBahanBaku BahanBaku;
            while (rs.next()) {
                BahanBaku = new DataBahanBaku(rs.getString("no_kartu_waleta"),
                        rs.getString("nama_supplier"),
                        rs.getString("nama_rumah_burung"),
                        rs.getDate("tgl_masuk"),
                        rs.getDate("tgl_panen"),
                        rs.getDate("tgl_grading"),
                        rs.getDate("tgl_timbang"),
                        rs.getInt("nitrit_bm_w3"),
                        rs.getFloat("kadar_air_bahan_baku"),
                        rs.getInt("berat_awal"),
                        rs.getInt("berat_real"),
                        rs.getInt("keping_real"),
                        rs.getDouble("total_harga"),
                        rs.getInt("last_stok")
                );
                BahanBakuList.add(BahanBaku);
            }
        } catch (Exception ex) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
        return BahanBakuList;
    }

    public void refreshTable_BahanBaku() {
        try {
            String sql_lp = null, sql_jual = null, sql_cmp = null;
            double total_keping_masuk = 0, total_gram_masuk = 0;
            double total_keping_keluar = 0, total_gram_keluar = 0;
            double total_keping_stok = 0, total_gram_stok = 0;
            double total_harga_kartu = 0;
            int berat_lp = 0, keping_lp = 0, berat_keluar = 0, keping_keluar = 0, berat_cmp = 0, keping_cmp = 0;
            double harga_lp = 0, harga_jual = 0, harga_cmp = 0;
            ResultSet rs_lp, rs_keluar, rs_cmp;
            ArrayList<DataBahanBaku> list = BahanBakuList();
            DefaultTableModel model = (DefaultTableModel) table_data_bahan_baku.getModel();
        model.setRowCount(0);
            Object[] row = new Object[11];
            for (int i = 0; i < list.size(); i++) {
                if (Date_FilterStok.getDate() != null) {
                    sql_lp = "SELECT SUM(`tb_laporan_produksi`.`berat_basah`) AS 'berat_lp', SUM(`tb_laporan_produksi`.`jumlah_keping`) AS 'keping_lp', SUM(`tb_grading_bahan_baku`.`harga_bahanbaku` * `tb_laporan_produksi`.`berat_basah`) AS 'total_harga' FROM `tb_laporan_produksi` "
                            + "LEFT JOIN `tb_grading_bahan_baku` ON (`tb_grading_bahan_baku`.`no_kartu_waleta` = `tb_laporan_produksi`.`no_kartu_waleta` AND `tb_grading_bahan_baku`.`kode_grade` = `tb_laporan_produksi`.`kode_grade`)"
                            + "WHERE `tb_laporan_produksi`.`no_kartu_waleta` = '" + list.get(i).getNo_kartu_waleta() + "' AND `tanggal_lp`<='" + dateFormat.format(Date_FilterStok.getDate()) + "'";
                    sql_jual = "SELECT SUM(`tb_bahan_baku_keluar`.`total_berat_keluar`) AS 'berat_keluar', SUM(`tb_bahan_baku_keluar`.`total_keping_keluar`) AS 'keping_keluar', SUM(`tb_grading_bahan_baku`.`harga_bahanbaku` * `tb_bahan_baku_keluar`.`total_berat_keluar`) AS 'total_harga' FROM `tb_bahan_baku_keluar` "
                            + "LEFT JOIN `tb_grading_bahan_baku` ON (`tb_grading_bahan_baku`.`no_kartu_waleta` = `tb_bahan_baku_keluar`.`no_kartu_waleta` AND `tb_grading_bahan_baku`.`kode_grade` = `tb_bahan_baku_keluar`.`kode_grade`)"
                            + "LEFT JOIN `tb_bahan_baku_keluar1` ON `tb_bahan_baku_keluar`.`kode_pengeluaran` = `tb_bahan_baku_keluar1`.`kode_pengeluaran`"
                            + "WHERE `tb_bahan_baku_keluar`.`no_kartu_waleta` = '" + list.get(i).getNo_kartu_waleta() + "' AND `tgl_keluar`<='" + dateFormat.format(Date_FilterStok.getDate()) + "'";
                    sql_cmp = "SELECT SUM(`tb_kartu_cmp_detail`.`keping`) AS 'keping_cmp', SUM(`tb_kartu_cmp_detail`.`gram`) AS 'berat_cmp', SUM(`tb_grading_bahan_baku`.`harga_bahanbaku`*`tb_kartu_cmp_detail`.`gram`) AS 'harga_cmp'\n"
                            + "FROM `tb_kartu_cmp_detail` LEFT JOIN `tb_kartu_cmp` ON `tb_kartu_cmp_detail`.`kode_kartu_cmp` = `tb_kartu_cmp`.`kode_kartu_cmp`"
                            + "LEFT JOIN `tb_grading_bahan_baku` ON `tb_kartu_cmp_detail`.`no_grading` = `tb_grading_bahan_baku`.`no_grading`\n"
                            + "WHERE `tb_grading_bahan_baku`.`no_kartu_waleta` = '" + list.get(i).getNo_kartu_waleta() + "' AND `tanggal`<='" + dateFormat.format(Date_FilterStok.getDate()) + "'";
                } else {
                    sql_lp = "SELECT SUM(`tb_laporan_produksi`.`berat_basah`) AS 'berat_lp', SUM(`tb_laporan_produksi`.`jumlah_keping`) AS 'keping_lp', SUM(`tb_grading_bahan_baku`.`harga_bahanbaku` * `tb_laporan_produksi`.`berat_basah`) AS 'total_harga' FROM `tb_laporan_produksi` "
                            + "LEFT JOIN `tb_grading_bahan_baku` ON (`tb_grading_bahan_baku`.`no_kartu_waleta` = `tb_laporan_produksi`.`no_kartu_waleta` AND `tb_grading_bahan_baku`.`kode_grade` = `tb_laporan_produksi`.`kode_grade`)"
                            + "WHERE `tb_laporan_produksi`.`no_kartu_waleta` = '" + list.get(i).getNo_kartu_waleta() + "'";
                    sql_jual = "SELECT SUM(`tb_bahan_baku_keluar`.`total_berat_keluar`) AS 'berat_keluar', SUM(`tb_bahan_baku_keluar`.`total_keping_keluar`) AS 'keping_keluar', SUM(`tb_grading_bahan_baku`.`harga_bahanbaku` * `tb_bahan_baku_keluar`.`total_berat_keluar`) AS 'total_harga' FROM `tb_bahan_baku_keluar` "
                            + "LEFT JOIN `tb_grading_bahan_baku` ON (`tb_grading_bahan_baku`.`no_kartu_waleta` = `tb_bahan_baku_keluar`.`no_kartu_waleta` AND `tb_grading_bahan_baku`.`kode_grade` = `tb_bahan_baku_keluar`.`kode_grade`)"
                            + "WHERE `tb_bahan_baku_keluar`.`no_kartu_waleta` = '" + list.get(i).getNo_kartu_waleta() + "'";
                    sql_cmp = "SELECT SUM(`tb_kartu_cmp_detail`.`keping`) AS 'keping_cmp', SUM(`tb_kartu_cmp_detail`.`gram`) AS 'berat_cmp', SUM(`tb_grading_bahan_baku`.`harga_bahanbaku`*`tb_kartu_cmp_detail`.`gram`) AS 'harga_cmp' FROM `tb_kartu_cmp_detail`\n"
                            + "LEFT JOIN `tb_grading_bahan_baku` ON `tb_kartu_cmp_detail`.`no_grading` = `tb_grading_bahan_baku`.`no_grading`\n"
                            + "WHERE `tb_grading_bahan_baku`.`no_kartu_waleta` = '" + list.get(i).getNo_kartu_waleta() + "'";
                }
                rs_lp = Utility.db.getStatement().executeQuery(sql_lp);
                if (rs_lp.next()) {
                    berat_lp = rs_lp.getInt("berat_lp");
                    keping_lp = rs_lp.getInt("keping_lp");
                    harga_lp = rs_lp.getDouble("total_harga");
                } else {
                    berat_lp = 0;
                    keping_lp = 0;
                    harga_lp = 0;
                }

                rs_keluar = Utility.db.getStatement().executeQuery(sql_jual);
                if (rs_keluar.next()) {
                    berat_keluar = rs_keluar.getInt("berat_keluar");
                    keping_keluar = rs_keluar.getInt("keping_keluar");
                    harga_jual = rs_keluar.getDouble("total_harga");
                } else {
                    berat_keluar = 0;
                    keping_keluar = 0;
                    harga_jual = 0;
                }

                rs_cmp = Utility.db.getStatement().executeQuery(sql_cmp);
                if (rs_cmp.next()) {
                    berat_cmp = rs_cmp.getInt("berat_cmp");
                    keping_cmp = rs_cmp.getInt("keping_cmp");
                    harga_cmp = rs_cmp.getDouble("harga_cmp");
                } else {
                    berat_cmp = 0;
                    keping_cmp = 0;
                    harga_cmp = 0;
                }

                row[0] = list.get(i).getNo_kartu_waleta();
                row[1] = list.get(i).getNama_supplier();
                row[2] = list.get(i).getNama_rumah_burung();
                row[3] = list.get(i).getTgl_masuk();
                row[4] = list.get(i).getKeping_real();
                row[5] = list.get(i).getBerat_real();
                row[6] = keping_lp + keping_keluar + keping_cmp;
                row[7] = berat_lp + berat_keluar + berat_cmp;
                row[8] = list.get(i).getKeping_real() - (keping_lp + keping_keluar + keping_cmp);
                row[9] = list.get(i).getBerat_real() - (berat_lp + berat_keluar + berat_cmp);
                row[10] = list.get(i).getHarga_kartu() - (harga_lp + harga_jual + harga_cmp);
                model.addRow(row);
                total_keping_masuk = total_keping_masuk + list.get(i).getKeping_real();
                total_gram_masuk = total_gram_masuk + list.get(i).getBerat_real();
                total_keping_keluar = total_keping_keluar + (keping_lp + keping_keluar);
                total_gram_keluar = total_gram_keluar + (berat_lp + berat_keluar);
                total_keping_stok = total_keping_stok + (list.get(i).getKeping_real() - (keping_lp + keping_keluar + keping_cmp));
                total_gram_stok = total_gram_stok + (list.get(i).getBerat_real() - (berat_lp + berat_keluar + berat_cmp));
                total_harga_kartu = total_harga_kartu + (list.get(i).getHarga_kartu() - (harga_lp + harga_jual + harga_cmp));
            }
        ColumnsAutoSizer.sizeColumnsToFit(table_data_bahan_baku);

            label_keping_masuk.setText(decimalFormat.format(total_keping_masuk));
            label_berat_masuk.setText(decimalFormat.format(total_gram_masuk));
            label_keping_keluar.setText(decimalFormat.format(total_keping_keluar));
            label_berat_keluar.setText(decimalFormat.format(total_gram_keluar));
            label_keping_stok.setText(decimalFormat.format(total_keping_stok));
            label_berat_stok.setText(decimalFormat.format(total_gram_stok));
            label_total_harga_kartu.setText(decimalFormat.format(total_harga_kartu));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(JPanel_DataBahanBaku_Finance.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public ArrayList<StockBahanBaku> StockList() {
        ArrayList<StockBahanBaku> StockList = new ArrayList<>();
        try {
            String no_kartu;
            int i = table_data_bahan_baku.getSelectedRow();
            if (i == -1) {
                no_kartu = null;
            } else {
                no_kartu = table_data_bahan_baku.getValueAt(i, 0).toString();
            }
            String kode = ComboBox_Search_grade.getSelectedItem().toString();
            if (ComboBox_Search_grade.getSelectedItem().equals("All")) {
                kode = "";
            }
//            sql = "SELECT `no_grading`, `tb_grading_bahan_baku`.`no_kartu_waleta`, `tb_grading_bahan_baku`.`kode_grade`, `tb_grading_bahan_baku`.`jumlah_keping`, `total_berat`, `harga_bahanbaku` \n"
//                    + "FROM `tb_grading_bahan_baku`\n"
//                    + "WHERE `tb_grading_bahan_baku`.`kode_grade` LIKE '%" + kode + "%' AND `tb_grading_bahan_baku`.`no_kartu_waleta`='" + no_kartu + "'\n"
//                    + "GROUP BY `no_grading`";
            if (Date_FilterStok.getDate() != null) {
                sql = "SELECT `tb_grading_bahan_baku`.`no_grading`, `tb_grading_bahan_baku`.`no_kartu_waleta`, `tb_grading_bahan_baku`.`kode_grade`, `tb_grading_bahan_baku`.`jumlah_keping`, `total_berat`, `harga_bahanbaku` \n"
                        + "FROM `tb_grading_bahan_baku` LEFT JOIN `tb_bahan_baku_masuk` ON `tb_grading_bahan_baku`.`no_kartu_waleta` = `tb_bahan_baku_masuk`.`no_kartu_waleta`\n"
                        + "WHERE `tb_grading_bahan_baku`.`kode_grade` LIKE '%" + kode + "%' AND `tb_grading_bahan_baku`.`no_kartu_waleta`='" + no_kartu + "' AND `tb_bahan_baku_masuk`.`tgl_masuk`<='" + dateFormat.format(Date_FilterStok.getDate()) + "' \n"
                        + "GROUP BY `no_grading`";
            } else {
                sql = "SELECT `tb_grading_bahan_baku`.`no_grading`, `tb_grading_bahan_baku`.`no_kartu_waleta`, `tb_grading_bahan_baku`.`kode_grade`, `tb_grading_bahan_baku`.`jumlah_keping`, `total_berat`, `harga_bahanbaku` \n"
                        + "FROM `tb_grading_bahan_baku`\n"
                        + "WHERE `tb_grading_bahan_baku`.`kode_grade` LIKE '%" + kode + "%' AND `tb_grading_bahan_baku`.`no_kartu_waleta`='" + no_kartu + "'\n"
                        + "GROUP BY `no_grading`";
            }
            rs = Utility.db.getStatement().executeQuery(sql);
            StockBahanBaku Stock;
            while (rs.next()) {
                Stock = new StockBahanBaku(rs.getString("no_grading"), rs.getString("no_kartu_waleta"), null, rs.getString("kode_grade"), rs.getInt("jumlah_keping"), rs.getInt("total_berat"), rs.getDouble("harga_bahanbaku"));
                StockList.add(Stock);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
        return StockList;
    }

    public void show_data_Stock() {
        ResultSet rs_jual, rs_lp, rs_cmp;
        String sql_lp = null, sql_jual = null, sql_cmp = null;
        int kpg_lp = 0, gram_lp = 0, kpg_jual = 0, gram_keluar = 0, kpg_cmp = 0, gram_cmp = 0;
        double sisa_kpg = 0, sisa_gram = 0;
        double total_sisa_kpg = 0, total_sisa_gram = 0;
        double total_nilai_stok = 0;
        ArrayList<StockBahanBaku> list = StockList();
        DefaultTableModel model = (DefaultTableModel) Table_stock_bahan_baku.getModel();
        Object[] row = new Object[9];
        for (int i = 0; i < list.size(); i++) {
            try {
                if (Date_FilterStok.getDate() != null) {
                    sql_lp = "SELECT SUM(`jumlah_keping`) AS 'keping', SUM(`berat_basah`) AS 'berat' FROM `tb_laporan_produksi` "
                            + "WHERE `kode_grade` = '" + list.get(i).getKode_grade() + "'  AND `no_kartu_waleta` = '" + list.get(i).getNo_kartu_waleta() + "' AND `tanggal_lp`<='" + dateFormat.format(Date_FilterStok.getDate()) + "'";
                    sql_jual = "SELECT SUM(`total_keping_keluar`) AS 'keping', SUM(`total_berat_keluar`) AS 'berat' FROM `tb_bahan_baku_keluar` LEFT JOIN `tb_bahan_baku_keluar1` ON `tb_bahan_baku_keluar`.`kode_pengeluaran` = `tb_bahan_baku_keluar1`.`kode_pengeluaran`"
                            + "WHERE `kode_grade` = '" + list.get(i).getKode_grade() + "'  AND `no_kartu_waleta` = '" + list.get(i).getNo_kartu_waleta() + "' AND `tgl_keluar`<='" + dateFormat.format(Date_FilterStok.getDate()) + "'";
                    sql_cmp = "SELECT SUM(`keping`) AS 'keping', SUM(`gram`) AS 'berat' FROM `tb_kartu_cmp_detail` LEFT JOIN `tb_kartu_cmp` ON `tb_kartu_cmp_detail`.`kode_kartu_cmp` = `tb_kartu_cmp`.`kode_kartu_cmp`"
                            + "WHERE `no_grading` = '" + list.get(i).getNo_grading() + "' AND `tb_kartu_cmp`.`tanggal`<='" + dateFormat.format(Date_FilterStok.getDate()) + "'";
                } else {
                    sql_lp = "SELECT SUM(`jumlah_keping`) AS 'keping', SUM(`berat_basah`) AS 'berat' FROM `tb_laporan_produksi` "
                            + "WHERE `kode_grade` = '" + list.get(i).getKode_grade() + "'  AND `no_kartu_waleta` = '" + list.get(i).getNo_kartu_waleta() + "'";
                    sql_jual = "SELECT SUM(`total_keping_keluar`) AS 'keping', SUM(`total_berat_keluar`) AS 'berat' FROM `tb_bahan_baku_keluar`  LEFT JOIN `tb_bahan_baku_keluar1` ON `tb_bahan_baku_keluar`.`kode_pengeluaran` = `tb_bahan_baku_keluar1`.`kode_pengeluaran`"
                            + "WHERE `kode_grade` = '" + list.get(i).getKode_grade() + "'  AND `no_kartu_waleta` = '" + list.get(i).getNo_kartu_waleta() + "'";
                    sql_cmp = "SELECT SUM(`keping`) AS 'keping', SUM(`gram`) AS 'berat' FROM `tb_kartu_cmp_detail` LEFT JOIN `tb_kartu_cmp` ON `tb_kartu_cmp_detail`.`kode_kartu_cmp` = `tb_kartu_cmp`.`kode_kartu_cmp`"
                            + "WHERE `no_grading` = '" + list.get(i).getNo_grading() + "'";
                }
//                System.out.println(sql_cmp);
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
                Logger.getLogger(JPanel_DataBahanBaku_Finance.class.getName()).log(Level.SEVERE, null, e);
            }
            row[0] = list.get(i).getNo_kartu_waleta();
            row[1] = list.get(i).getKode_grade();
            row[2] = list.get(i).getJumlah_keping();
            row[3] = list.get(i).getTotal_berat();
            row[4] = kpg_lp + kpg_jual + kpg_cmp;
            row[5] = gram_lp + gram_keluar + gram_cmp;
            sisa_kpg = list.get(i).getJumlah_keping() - (kpg_lp + kpg_jual + kpg_cmp);
            row[6] = (int) sisa_kpg;
            sisa_gram = list.get(i).getTotal_berat() - (gram_lp + gram_keluar + gram_cmp);
            row[7] = (int) sisa_gram;
            row[8] = decimalFormat.format(list.get(i).getHarga_bahanbaku() * sisa_gram);

            total_sisa_kpg = total_sisa_kpg + sisa_kpg;
            total_sisa_gram = total_sisa_gram + sisa_gram;
            total_nilai_stok = total_nilai_stok + (list.get(i).getHarga_bahanbaku() * sisa_gram);
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
        label_total_nilai_stok.setText(decimalFormat.format(total_nilai_stok));
    }

    public void refreshTable_Stock_grade() {
        DefaultTableModel model = (DefaultTableModel) Table_stock_bahan_baku.getModel();
        model.setRowCount(0);
        show_data_Stock();
        ColumnsAutoSizer.sizeColumnsToFit(Table_stock_bahan_baku);

        TableAlignment.setHorizontalAlignment(JLabel.LEADING);
        //tabel Data Bahan Baku
        for (int i = 0; i < Table_stock_bahan_baku.getColumnCount(); i++) {
            Table_stock_bahan_baku.getColumnModel().getColumn(i).setCellRenderer(TableAlignment);
        }
    }

    public void refreshTable_LP() {
        try {
            DefaultTableModel model = (DefaultTableModel) tb_LP.getModel();
            model.setRowCount(0);
            int i = table_data_bahan_baku.getSelectedRow();
            int j = Table_stock_bahan_baku.getSelectedRow();
            double total_kpg = 0, total_gram = 0, total_nilai = 0;
            String no_kartu = "", grade = "";
            sql = "";
            if (i == -1) {
                no_kartu = Table_stock_bahan_baku.getValueAt(j, 0).toString();
                grade = Table_stock_bahan_baku.getValueAt(j, 1).toString();
            } else if (j == -1) {
                no_kartu = table_data_bahan_baku.getValueAt(i, 0).toString();
            }
            if (Date_FilterLP1.getDate() != null && Date_FilterLP2.getDate() != null && Date_FilterStok.getDate() == null) {
                sql = "SELECT `no_laporan_produksi`, `tb_laporan_produksi`.`no_kartu_waleta`, `tanggal_lp`, `ruangan`, `tb_laporan_produksi`.`kode_grade`, `memo_lp`, `tb_bahan_baku_masuk`.`kadar_air_bahan_baku`, `berat_basah`, `berat_kering`, `tb_laporan_produksi`.`jumlah_keping`, `jumlah_gumpil`, `hilang_kaki_lp`, `hilang_ujung_lp`, `jumlah_sobek`, `pecah_1_lp`, `tb_grading_bahan_baku`.`harga_bahanbaku` \n"
                        + "FROM `tb_laporan_produksi` "
                        + "LEFT JOIN `tb_bahan_baku_masuk` ON `tb_laporan_produksi`.`no_kartu_waleta` = `tb_bahan_baku_masuk`.`no_kartu_waleta`"
                        + "LEFT JOIN `tb_grading_bahan_baku` ON (`tb_grading_bahan_baku`.`no_kartu_waleta` = `tb_laporan_produksi`.`no_kartu_waleta` AND `tb_grading_bahan_baku`.`kode_grade` = `tb_laporan_produksi`.`kode_grade`)"
                        + "WHERE "
                        + "`tb_laporan_produksi`.`no_kartu_waleta` LIKE '%" + no_kartu + "%' "
                        + "AND `tb_laporan_produksi`.`kode_grade` LIKE '%" + grade + "%' "
                        + "AND (`tanggal_lp` BETWEEN '" + dateFormat.format(Date_FilterLP1.getDate()) + "' AND '" + dateFormat.format(Date_FilterLP2.getDate()) + "')";
            } else if ((Date_FilterLP1.getDate() == null || Date_FilterLP2.getDate() == null) && Date_FilterStok.getDate() != null) {
                sql = "SELECT `no_laporan_produksi`, `tb_laporan_produksi`.`no_kartu_waleta`, `tanggal_lp`, `ruangan`, `tb_laporan_produksi`.`kode_grade`, `memo_lp`, `tb_bahan_baku_masuk`.`kadar_air_bahan_baku`, `berat_basah`, `berat_kering`, `tb_laporan_produksi`.`jumlah_keping`, `jumlah_gumpil`, `hilang_kaki_lp`, `hilang_ujung_lp`, `jumlah_sobek`, `pecah_1_lp`, `tb_grading_bahan_baku`.`harga_bahanbaku` \n"
                        + "FROM `tb_laporan_produksi` "
                        + "LEFT JOIN `tb_bahan_baku_masuk` ON `tb_laporan_produksi`.`no_kartu_waleta` = `tb_bahan_baku_masuk`.`no_kartu_waleta`"
                        + "LEFT JOIN `tb_grading_bahan_baku` ON (`tb_grading_bahan_baku`.`no_kartu_waleta` = `tb_laporan_produksi`.`no_kartu_waleta` AND `tb_grading_bahan_baku`.`kode_grade` = `tb_laporan_produksi`.`kode_grade`)"
                        + "WHERE "
                        + "`tb_laporan_produksi`.`no_kartu_waleta` LIKE '%" + no_kartu + "%' "
                        + "AND `tb_laporan_produksi`.`kode_grade` LIKE '%" + grade + "%' "
                        + "AND `tanggal_lp`<='" + dateFormat.format(Date_FilterStok.getDate()) + "'";
            } else if (Date_FilterLP1.getDate() == null && Date_FilterLP2.getDate() == null && Date_FilterStok.getDate() == null) {
                sql = "SELECT `no_laporan_produksi`, `tb_laporan_produksi`.`no_kartu_waleta`, `tanggal_lp`, `ruangan`, `tb_laporan_produksi`.`kode_grade`, `memo_lp`, `tb_bahan_baku_masuk`.`kadar_air_bahan_baku`, `berat_basah`, `berat_kering`, `tb_laporan_produksi`.`jumlah_keping`, `jumlah_gumpil`, `hilang_kaki_lp`, `hilang_ujung_lp`, `jumlah_sobek`, `pecah_1_lp`, `tb_grading_bahan_baku`.`harga_bahanbaku` \n"
                        + "FROM `tb_laporan_produksi` "
                        + "LEFT JOIN `tb_bahan_baku_masuk` ON `tb_laporan_produksi`.`no_kartu_waleta` = `tb_bahan_baku_masuk`.`no_kartu_waleta`"
                        + "LEFT JOIN `tb_grading_bahan_baku` ON (`tb_grading_bahan_baku`.`no_kartu_waleta` = `tb_laporan_produksi`.`no_kartu_waleta` AND `tb_grading_bahan_baku`.`kode_grade` = `tb_laporan_produksi`.`kode_grade`)"
                        + "WHERE "
                        + "`tb_laporan_produksi`.`no_kartu_waleta` LIKE '%" + no_kartu + "%' "
                        + "AND `tb_laporan_produksi`.`kode_grade` LIKE '%" + grade + "%'";
            } else if (Date_FilterLP1.getDate() != null && Date_FilterLP2.getDate() != null && Date_FilterStok.getDate() != null) {
                JOptionPane.showMessageDialog(this, "Maaf untuk filter tanggal harap di pilih salah satu");
                Date_FilterLP1.setDate(null);
                Date_FilterLP2.setDate(null);
                sql = "SELECT `no_laporan_produksi`, `tb_laporan_produksi`.`no_kartu_waleta`, `tanggal_lp`, `ruangan`, `tb_laporan_produksi`.`kode_grade`, `memo_lp`, `tb_bahan_baku_masuk`.`kadar_air_bahan_baku`, `berat_basah`, `berat_kering`, `tb_laporan_produksi`.`jumlah_keping`, `jumlah_gumpil`, `hilang_kaki_lp`, `hilang_ujung_lp`, `jumlah_sobek`, `pecah_1_lp`, `tb_grading_bahan_baku`.`harga_bahanbaku` \n"
                        + "FROM `tb_laporan_produksi` "
                        + "LEFT JOIN `tb_bahan_baku_masuk` ON `tb_laporan_produksi`.`no_kartu_waleta` = `tb_bahan_baku_masuk`.`no_kartu_waleta`"
                        + "LEFT JOIN `tb_grading_bahan_baku` ON (`tb_grading_bahan_baku`.`no_kartu_waleta` = `tb_laporan_produksi`.`no_kartu_waleta` AND `tb_grading_bahan_baku`.`kode_grade` = `tb_laporan_produksi`.`kode_grade`)"
                        + "WHERE "
                        + "`tb_laporan_produksi`.`no_kartu_waleta` LIKE '%" + no_kartu + "%' "
                        + "AND `tb_laporan_produksi`.`kode_grade` LIKE '%" + grade + "%' "
                        + "AND `tanggal_lp`<='" + dateFormat.format(Date_FilterStok.getDate()) + "'";
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
            ColumnsAutoSizer.sizeColumnsToFit(tb_LP);
            label_total_lp.setText(Integer.toString(tb_LP.getRowCount()));
            label_keping_lp.setText(decimalFormat.format(total_kpg));
            label_berat_lp.setText(decimalFormat.format(total_gram));
            label_total_nilai_lp.setText(decimalFormat.format(total_nilai));
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_DataBahanBaku_Finance.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_Keluar() {
        try {
            String no_kartu = "", grade = "";
            DefaultTableModel model = (DefaultTableModel) tb_keluar.getModel();
            model.setRowCount(0);
            int i = table_data_bahan_baku.getSelectedRow();
            int j = Table_stock_bahan_baku.getSelectedRow();
            double total_keping = 0, total_berat = 0, total_nilai = 0;
            if (i == -1) {
                no_kartu = Table_stock_bahan_baku.getValueAt(j, 0).toString();
                grade = Table_stock_bahan_baku.getValueAt(j, 1).toString();
            } else if (j == -1) {
                no_kartu = table_data_bahan_baku.getValueAt(i, 0).toString();
            }
            if (Date_FilterStok.getDate() != null) {
                sql = "SELECT *, `tb_grading_bahan_baku`.`harga_bahanbaku` FROM `tb_bahan_baku_keluar` "
                        + "LEFT JOIN `tb_bahan_baku_keluar1` ON `tb_bahan_baku_keluar`.`kode_pengeluaran` = `tb_bahan_baku_keluar1`.`kode_pengeluaran`"
                        + "LEFT JOIN `tb_grading_bahan_baku` ON (`tb_grading_bahan_baku`.`no_kartu_waleta` = `tb_bahan_baku_keluar`.`no_kartu_waleta` AND `tb_grading_bahan_baku`.`kode_grade` = `tb_bahan_baku_keluar`.`kode_grade`)"
                        + "WHERE `tb_bahan_baku_keluar`.`no_kartu_waleta`='" + no_kartu + "' AND `tb_grading_bahan_baku`.`kode_grade` LIKE '%" + grade + "%' AND `tgl_keluar`<='" + dateFormat.format(Date_FilterStok.getDate()) + "'";
            } else {
                sql = "SELECT *, `tb_grading_bahan_baku`.`harga_bahanbaku` FROM `tb_bahan_baku_keluar` "
                        + "LEFT JOIN `tb_bahan_baku_keluar1` ON `tb_bahan_baku_keluar`.`kode_pengeluaran` = `tb_bahan_baku_keluar1`.`kode_pengeluaran`"
                        + "LEFT JOIN `tb_grading_bahan_baku` ON (`tb_grading_bahan_baku`.`no_kartu_waleta` = `tb_bahan_baku_keluar`.`no_kartu_waleta` AND `tb_grading_bahan_baku`.`kode_grade` = `tb_bahan_baku_keluar`.`kode_grade`)"
                        + "WHERE `tb_bahan_baku_keluar`.`no_kartu_waleta`='" + no_kartu + "' AND `tb_grading_bahan_baku`.`kode_grade` LIKE '%" + grade + "%'";
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
            ColumnsAutoSizer.sizeColumnsToFit(tb_keluar);

            label_total_data_jual.setText(Integer.toString(tb_keluar.getRowCount()));
            label_keping_jual.setText(decimalFormat.format(total_keping));
            label_berat_jual.setText(decimalFormat.format(total_berat));
            label_total_nilai_jual.setText(decimalFormat.format(total_nilai));
//        label_berat_jual.setText(Integer.toString(total_gram));
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_DataBahanBaku_Finance.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void show_data_CMP() {
        int total_gram = 0, total_keping = 0;
        double total_nilai = 0;
        try {
            DefaultTableModel model = (DefaultTableModel) tb_cmp.getModel();
            String no_kartu = "", grade = "";
            int i = table_data_bahan_baku.getSelectedRow();
            int j = Table_stock_bahan_baku.getSelectedRow();
            sql = "";
            if (i == -1) {
                no_kartu = Table_stock_bahan_baku.getValueAt(j, 0).toString();
                grade = Table_stock_bahan_baku.getValueAt(j, 1).toString();
            } else if (j == -1) {
                no_kartu = table_data_bahan_baku.getValueAt(i, 0).toString();
            }
            if (Date_FilterStok.getDate() != null) {
                sql = "SELECT `tb_kartu_cmp_detail`.`kode_kartu_cmp`, `tb_grading_bahan_baku`.`kode_grade`, `tb_kartu_cmp`.`tanggal`, `keping`, `gram`, `tb_grading_bahan_baku`.`harga_bahanbaku` \n"
                        + "FROM `tb_kartu_cmp_detail` \n"
                        + "LEFT JOIN `tb_kartu_cmp` ON `tb_kartu_cmp_detail`.`kode_kartu_cmp` = `tb_kartu_cmp`.`kode_kartu_cmp`\n"
                        + "LEFT JOIN `tb_grading_bahan_baku` ON `tb_kartu_cmp_detail`.`no_grading` = `tb_grading_bahan_baku`.`no_grading`\n"
                        + "WHERE `tb_grading_bahan_baku`.`no_kartu_waleta` = '" + no_kartu + "' AND `tb_grading_bahan_baku`.`kode_grade` LIKE '%" + grade + "%' AND `tb_kartu_cmp`.`tanggal` <= '" + dateFormat.format(Date_FilterStok.getDate()) + "'";
            } else {
                sql = "SELECT `tb_kartu_cmp_detail`.`kode_kartu_cmp`, `tb_grading_bahan_baku`.`kode_grade`, `tb_kartu_cmp`.`tanggal`, `keping`, `gram`, `tb_grading_bahan_baku`.`harga_bahanbaku` \n"
                        + "FROM `tb_kartu_cmp_detail` \n"
                        + "LEFT JOIN `tb_kartu_cmp` ON `tb_kartu_cmp_detail`.`kode_kartu_cmp` = `tb_kartu_cmp`.`kode_kartu_cmp`\n"
                        + "LEFT JOIN `tb_grading_bahan_baku` ON `tb_kartu_cmp_detail`.`no_grading` = `tb_grading_bahan_baku`.`no_grading`\n"
                        + "WHERE `tb_grading_bahan_baku`.`no_kartu_waleta` = '" + no_kartu + "' AND `tb_grading_bahan_baku`.`kode_grade` LIKE '%" + grade + "%'";
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
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane9 = new javax.swing.JScrollPane();
        table_data_bahan_baku = new javax.swing.JTable();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel_Data_LP = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        jScrollPane10 = new javax.swing.JScrollPane();
        tb_LP = new javax.swing.JTable();
        jLabel31 = new javax.swing.JLabel();
        label_berat_lp = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        label_total_lp = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        label_keping_lp = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        label_total_nilai_lp = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        label_no_kartu1 = new javax.swing.JLabel();
        Date_FilterLP1 = new com.toedter.calendar.JDateChooser();
        Date_FilterLP2 = new com.toedter.calendar.JDateChooser();
        button_search_lp = new javax.swing.JButton();
        jPanel_Data_Keluar = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        label_berat_jual = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        label_total_data_jual = new javax.swing.JLabel();
        jScrollPane11 = new javax.swing.JScrollPane();
        tb_keluar = new javax.swing.JTable();
        jLabel38 = new javax.swing.JLabel();
        label_keping_jual = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        label_total_nilai_jual = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        label_no_kartu2 = new javax.swing.JLabel();
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
        label_no_kartu3 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        label_total_harga_cmp = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        Table_stock_bahan_baku = new javax.swing.JTable();
        ComboBox_Search_grade = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        label_no_kartu = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        label_total_sisa_kpg = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        label_total_sisa_gram = new javax.swing.JLabel();
        CheckBox_show_dataMasihSisa = new javax.swing.JCheckBox();
        jLabel36 = new javax.swing.JLabel();
        label_total_nilai_stok = new javax.swing.JLabel();
        button_search_data_bahan_baku = new javax.swing.JButton();
        txt_search_data_bahan_baku = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        Date_FilterStok = new com.toedter.calendar.JDateChooser();
        jLabel40 = new javax.swing.JLabel();
        label_keping_masuk = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        label_berat_masuk = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        label_berat_keluar = new javax.swing.JLabel();
        label_keping_keluar = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        label_berat_stok = new javax.swing.JLabel();
        label_keping_stok = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        label_total_harga_kartu = new javax.swing.JLabel();
        button_export1 = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Data Bahan Baku", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N

        table_data_bahan_baku.setAutoCreateRowSorter(true);
        table_data_bahan_baku.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_data_bahan_baku.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No Kartu Waleta", "Nama Supplier", "Rumah Burung", "Tgl Masuk", "Kpg Real", "Berat Real", "Kpg Keluar", "Gram Keluar", "Stok Kpg", "Stok Gram", "Nilai Stok"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Double.class
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
        jScrollPane9.setViewportView(table_data_bahan_baku);

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jPanel_Data_LP.setBackground(new java.awt.Color(255, 255, 255));

        jLabel24.setBackground(new java.awt.Color(255, 255, 255));
        jLabel24.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel24.setText("DATA LAPORAN PRODUKSI");

        tb_LP.setAutoCreateRowSorter(true);
        tb_LP.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tb_LP.setModel(new javax.swing.table.DefaultTableModel(
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
        tb_LP.setRowSelectionAllowed(false);
        jScrollPane10.setViewportView(tb_LP);

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

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("No. Kartu :");

        label_no_kartu1.setBackground(new java.awt.Color(255, 255, 255));
        label_no_kartu1.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_no_kartu1.setText("KARTU");

        Date_FilterLP1.setBackground(new java.awt.Color(255, 255, 255));
        Date_FilterLP1.setDateFormatString("dd/MM/yyyy");
        Date_FilterLP1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Date_FilterLP2.setBackground(new java.awt.Color(255, 255, 255));
        Date_FilterLP2.setDateFormatString("dd/MM/yyyy");
        Date_FilterLP2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        button_search_lp.setBackground(new java.awt.Color(255, 255, 255));
        button_search_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search_lp.setText("Go");
        button_search_lp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_search_lpActionPerformed(evt);
            }
        });

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
                        .addComponent(Date_FilterLP1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_FilterLP2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_search_lp)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_no_kartu1))
                    .addComponent(jScrollPane10, javax.swing.GroupLayout.DEFAULT_SIZE, 525, Short.MAX_VALUE)
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
                .addGroup(jPanel_Data_LPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_Data_LPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel24)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_no_kartu1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_search_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(Date_FilterLP1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_FilterLP2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane10, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
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

        tb_keluar.setAutoCreateRowSorter(true);
        tb_keluar.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tb_keluar.setModel(new javax.swing.table.DefaultTableModel(
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
        tb_keluar.setRowSelectionAllowed(false);
        jScrollPane11.setViewportView(tb_keluar);

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

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel4.setText("No. Kartu :");

        label_no_kartu2.setBackground(new java.awt.Color(255, 255, 255));
        label_no_kartu2.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_no_kartu2.setText("KARTU");

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
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_no_kartu2))
                    .addComponent(jScrollPane11, javax.swing.GroupLayout.DEFAULT_SIZE, 525, Short.MAX_VALUE)
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
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_no_kartu2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel25))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane11, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
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
        jLabel5.setText("No. Kartu :");

        label_no_kartu3.setBackground(new java.awt.Color(255, 255, 255));
        label_no_kartu3.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_no_kartu3.setText("KARTU");

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
                        .addComponent(label_no_kartu3))
                    .addComponent(jScrollPane12, javax.swing.GroupLayout.DEFAULT_SIZE, 525, Short.MAX_VALUE)
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
                    .addComponent(label_no_kartu3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane12, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
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

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Data Grading Bahan Baku", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 12))); // NOI18N

        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("Grade Bahan Baku :");

        Table_stock_bahan_baku.setAutoCreateRowSorter(true);
        Table_stock_bahan_baku.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No Kartu", "Grade", "Kpg IN", "Gram IN", "Kpg OUT", "Gram OUT", "Kpg Stok", "Gram Stok", "Nilai Stok"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Double.class
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
        jScrollPane1.setViewportView(Table_stock_bahan_baku);

        ComboBox_Search_grade.setEditable(true);
        ComboBox_Search_grade.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N
        ComboBox_Search_grade.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));
        ComboBox_Search_grade.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ComboBox_Search_gradeItemStateChanged(evt);
            }
        });
        ComboBox_Search_grade.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ComboBox_Search_gradeActionPerformed(evt);
            }
        });

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("No. Kartu :");

        label_no_kartu.setBackground(new java.awt.Color(255, 255, 255));
        label_no_kartu.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_no_kartu.setText("KARTU");

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

        label_total_nilai_stok.setBackground(new java.awt.Color(255, 255, 255));
        label_total_nilai_stok.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_nilai_stok.setText("0");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_Search_grade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_no_kartu, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(CheckBox_show_dataMasihSisa))
                    .addGroup(jPanel1Layout.createSequentialGroup()
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
                        .addComponent(label_total_nilai_stok)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_Search_grade, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_no_kartu, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CheckBox_show_dataMasihSisa, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_total_nilai_stok)
                        .addComponent(jLabel36))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_total_sisa_gram)
                        .addComponent(jLabel35)
                        .addComponent(label_total_sisa_kpg)
                        .addComponent(jLabel34)))
                .addContainerGap())
        );

        button_search_data_bahan_baku.setBackground(new java.awt.Color(255, 255, 255));
        button_search_data_bahan_baku.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search_data_bahan_baku.setText("Search");
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
        jLabel23.setText("By No Kartu Waleta :");

        Date_FilterStok.setBackground(new java.awt.Color(255, 255, 255));
        Date_FilterStok.setDateFormatString("dd MMMM yyyy");
        Date_FilterStok.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel40.setBackground(new java.awt.Color(255, 255, 255));
        jLabel40.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel40.setText("Total Keping Masuk :");

        label_keping_masuk.setBackground(new java.awt.Color(255, 255, 255));
        label_keping_masuk.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_keping_masuk.setText("0");

        jLabel41.setBackground(new java.awt.Color(255, 255, 255));
        jLabel41.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel41.setText("Total Berat Masuk :");

        label_berat_masuk.setBackground(new java.awt.Color(255, 255, 255));
        label_berat_masuk.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_berat_masuk.setText("0");

        jLabel42.setBackground(new java.awt.Color(255, 255, 255));
        jLabel42.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel42.setText("Total Keping Keluar :");

        jLabel43.setBackground(new java.awt.Color(255, 255, 255));
        jLabel43.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel43.setText("Total Berat Keluar :");

        label_berat_keluar.setBackground(new java.awt.Color(255, 255, 255));
        label_berat_keluar.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_berat_keluar.setText("0");

        label_keping_keluar.setBackground(new java.awt.Color(255, 255, 255));
        label_keping_keluar.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_keping_keluar.setText("0");

        jLabel44.setBackground(new java.awt.Color(255, 255, 255));
        jLabel44.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel44.setText("Total Keping Stok :");

        jLabel45.setBackground(new java.awt.Color(255, 255, 255));
        jLabel45.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel45.setText("Total Berat Stok :");

        label_berat_stok.setBackground(new java.awt.Color(255, 255, 255));
        label_berat_stok.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_berat_stok.setText("0");

        label_keping_stok.setBackground(new java.awt.Color(255, 255, 255));
        label_keping_stok.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_keping_stok.setText("0");

        jLabel26.setBackground(new java.awt.Color(255, 255, 255));
        jLabel26.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel26.setText("Data per Tanggal :");

        jLabel46.setBackground(new java.awt.Color(255, 255, 255));
        jLabel46.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel46.setText("Total Nilai Stok :");

        label_total_harga_kartu.setBackground(new java.awt.Color(255, 255, 255));
        label_total_harga_kartu.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_harga_kartu.setText("0");

        button_export1.setBackground(new java.awt.Color(255, 255, 255));
        button_export1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export1.setText("Export");
        button_export1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel23)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_data_bahan_baku, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel26)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_FilterStok, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_search_data_bahan_baku)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_export1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_keping_masuk, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_berat_masuk, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel42, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_keping_keluar, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_berat_keluar, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel45, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_berat_stok, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_keping_stok, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel46)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_harga_kartu, javax.swing.GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE)))
                        .addGap(6, 6, 6))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTabbedPane1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txt_search_data_bahan_baku, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(button_search_data_bahan_baku, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(button_export1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(Date_FilterStok, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(9, 9, 9)
                        .addComponent(jScrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 596, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(label_keping_masuk)
                                        .addComponent(jLabel40))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(label_berat_masuk)
                                        .addComponent(jLabel41)))
                                .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(label_keping_keluar)
                                        .addComponent(jLabel42))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(label_berat_keluar)
                                        .addComponent(jLabel43))))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(label_keping_stok)
                                    .addComponent(jLabel44)
                                    .addComponent(label_total_harga_kartu)
                                    .addComponent(jLabel46))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(label_berat_stok)
                                    .addComponent(jLabel45)))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txt_search_data_bahan_bakuKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_data_bahan_bakuKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_BahanBaku();
            DefaultTableModel model_lp = (DefaultTableModel) tb_LP.getModel();
            model_lp.setRowCount(0);
            DefaultTableModel model_keluar = (DefaultTableModel) tb_keluar.getModel();
            model_keluar.setRowCount(0);
        }
    }//GEN-LAST:event_txt_search_data_bahan_bakuKeyPressed

    private void button_search_data_bahan_bakuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_data_bahan_bakuActionPerformed
        // TODO add your handling code here:
        refreshTable_BahanBaku();
        DefaultTableModel model_lp = (DefaultTableModel) tb_LP.getModel();
        model_lp.setRowCount(0);
        DefaultTableModel model_keluar = (DefaultTableModel) tb_keluar.getModel();
        model_keluar.setRowCount(0);
    }//GEN-LAST:event_button_search_data_bahan_bakuActionPerformed

    private void ComboBox_Search_gradeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ComboBox_Search_gradeItemStateChanged
        // TODO add your handling code here:
        refreshTable_Stock_grade();
    }//GEN-LAST:event_ComboBox_Search_gradeItemStateChanged

    private void ComboBox_Search_gradeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ComboBox_Search_gradeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ComboBox_Search_gradeActionPerformed

    private void CheckBox_show_dataMasihSisaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_CheckBox_show_dataMasihSisaItemStateChanged
        // TODO add your handling code here:
        refreshTable_Stock_grade();
    }//GEN-LAST:event_CheckBox_show_dataMasihSisaItemStateChanged

    private void button_search_lpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_lpActionPerformed
        // TODO add your handling code here:
        refreshTable_LP();
    }//GEN-LAST:event_button_search_lpActionPerformed

    private void button_export1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export1ActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) table_data_bahan_baku.getModel();
        ExportToExcel.writeToExcel(model, jPanel1);
    }//GEN-LAST:event_button_export1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox CheckBox_show_dataMasihSisa;
    private javax.swing.JComboBox<String> ComboBox_Search_grade;
    private com.toedter.calendar.JDateChooser Date_FilterLP1;
    private com.toedter.calendar.JDateChooser Date_FilterLP2;
    private com.toedter.calendar.JDateChooser Date_FilterStok;
    public static javax.swing.JTable Table_stock_bahan_baku;
    private javax.swing.JButton button_export1;
    private javax.swing.JButton button_search_data_bahan_baku;
    private javax.swing.JButton button_search_lp;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
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
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel_Data_KartuCMP;
    private javax.swing.JPanel jPanel_Data_Keluar;
    private javax.swing.JPanel jPanel_Data_LP;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel label_berat_cmp;
    private javax.swing.JLabel label_berat_jual;
    private javax.swing.JLabel label_berat_keluar;
    private javax.swing.JLabel label_berat_lp;
    private javax.swing.JLabel label_berat_masuk;
    private javax.swing.JLabel label_berat_stok;
    private javax.swing.JLabel label_keping_cmp;
    private javax.swing.JLabel label_keping_jual;
    private javax.swing.JLabel label_keping_keluar;
    private javax.swing.JLabel label_keping_lp;
    private javax.swing.JLabel label_keping_masuk;
    private javax.swing.JLabel label_keping_stok;
    private javax.swing.JLabel label_no_kartu;
    private javax.swing.JLabel label_no_kartu1;
    private javax.swing.JLabel label_no_kartu2;
    private javax.swing.JLabel label_no_kartu3;
    private javax.swing.JLabel label_total_cmp;
    private javax.swing.JLabel label_total_data_jual;
    private javax.swing.JLabel label_total_harga_cmp;
    private javax.swing.JLabel label_total_harga_kartu;
    private javax.swing.JLabel label_total_lp;
    private javax.swing.JLabel label_total_nilai_jual;
    private javax.swing.JLabel label_total_nilai_lp;
    private javax.swing.JLabel label_total_nilai_stok;
    private javax.swing.JLabel label_total_sisa_gram;
    private javax.swing.JLabel label_total_sisa_kpg;
    private javax.swing.JTable table_data_bahan_baku;
    private javax.swing.JTable tb_LP;
    private javax.swing.JTable tb_cmp;
    private javax.swing.JTable tb_keluar;
    private javax.swing.JTextField txt_search_data_bahan_baku;
    // End of variables declaration//GEN-END:variables

}
