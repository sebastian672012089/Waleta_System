package waleta_system.Manajemen;

import java.awt.event.KeyEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;
import waleta_system.Class.ExportToExcel;

public class JPanel_Rekap_Biaya_per_KartuBaku extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public JPanel_Rekap_Biaya_per_KartuBaku() {
        initComponents();
    }

    public void init() {
        try {
            String this_year = new SimpleDateFormat("yyyy").format(new Date());
            Calendar calendar = Calendar.getInstance();
            calendar.set(Integer.valueOf(this_year), 0, 1);
            Date first_date = calendar.getTime();
            Date_1.setDate(first_date);
            Date_2.setDate(new Date());

//            refresh();
            Table_KartuBaku.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent event) {
                    if (!event.getValueIsAdjusting() && Table_KartuBaku.getSelectedRow() != -1) {
                        int i = Table_KartuBaku.getSelectedRow();
                        if (i > -1) {
                            String no_kartu = Table_KartuBaku.getValueAt(i, 1).toString();
                            refreshTableGradingBaku(no_kartu);
                            refreshTableGrading_supplier(no_kartu);
                            refreshTableGradingJadi(no_kartu);
                        }
                    }
                }
            });
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_Rekap_Biaya_per_KartuBaku.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void refreshTable_Kartu() {
        try {
            DefaultTableModel model = (DefaultTableModel) Table_KartuBaku.getModel();
            model.setRowCount(0);
            decimalFormat.setMaximumFractionDigits(0);
            double total_nilai_baku = 0, total_biaya_produksi = 0, total_biaya_kaki = 0, total_harga_jual = 0, total_margin = 0;
            double biaya_produksi_per_gr = 0, kurs = 0;
            float harga_kaki = 0;
            boolean check = true;
            try {
                harga_kaki = Integer.valueOf(txt_harga_kaki.getText());
            } catch (NumberFormatException e) {
                check = false;
                JOptionPane.showMessageDialog(this, "Harga kaki yang di masukkan salah");
            }
            try {
                biaya_produksi_per_gr = Float.valueOf(txt_biaya_produksi.getText());
            } catch (NumberFormatException e) {
                check = false;
                JOptionPane.showMessageDialog(this, "Biaya Produksi yang di masukkan salah");
            }
            try {
                kurs = Double.valueOf(txt_kurs.getText());
            } catch (NumberFormatException e) {
                check = false;
                JOptionPane.showMessageDialog(this, "Nilai Kurs yang di masukkan salah");
            }

            String tanggal = "";
            if (Date_1.getDate() != null && Date_2.getDate() != null) {
                tanggal = "AND `tgl_masuk` BETWEEN '" + dateFormat.format(Date_1.getDate()) + "' AND '" + dateFormat.format(Date_2.getDate()) + "'";
            } else {
                check = false;
                JOptionPane.showMessageDialog(this, "Silahkan masukkan filter tanggal");
            }

            sql = "SELECT `no_kartu_waleta`, `tgl_masuk`, `nama_supplier`, `nama_rumah_burung`, `berat_real`, SUM(`berat_lp`) AS 'berat_lp', SUM(`kaki`) AS 'kaki', SUM(`nilai_baku`) AS 'nilai_baku', SUM(`berat_grading`) AS 'berat_grading', SUM(`rmb_jual`) AS 'rmb_jual'"
                    + "FROM (SELECT `tb_laporan_produksi`.`no_kartu_waleta`, `tgl_masuk`, `nama_supplier`, `nama_rumah_burung`, `berat_real`, "
                    + "(`berat_basah`) AS 'berat_lp', "
                    + "(`tambahan_kaki1` + `tambahan_kaki2`) AS 'kaki',  "
                    + "ROUND(`berat_basah` * `tb_grading_bahan_baku`.`harga_bahanbaku`, 0) AS 'nilai_baku', "
                    + "SUM(`tb_grading_bahan_jadi`.`gram`) AS 'berat_grading', "
                    + "SUM((`tb_grading_bahan_jadi`.`gram`/1000) * `tb_grade_bahan_jadi`.`harga`) AS 'rmb_jual' "
                    + "FROM `tb_laporan_produksi` "
                    + "LEFT JOIN `tb_bahan_baku_masuk` ON `tb_laporan_produksi`.`no_kartu_waleta` = `tb_bahan_baku_masuk`.`no_kartu_waleta`"
                    + "LEFT JOIN `tb_supplier` ON `tb_bahan_baku_masuk`.`kode_supplier` = `tb_supplier`.`kode_supplier`"
                    + "LEFT JOIN `tb_rumah_burung` ON `tb_bahan_baku_masuk`.`no_registrasi` = `tb_rumah_burung`.`no_registrasi`"
                    + "LEFT JOIN `tb_finishing_2` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_finishing_2`.`no_laporan_produksi`"
                    + "LEFT JOIN `tb_grading_bahan_baku` ON `tb_laporan_produksi`.`no_kartu_waleta` = `tb_grading_bahan_baku`.`no_kartu_waleta` AND `tb_laporan_produksi`.`kode_grade` = `tb_grading_bahan_baku`.`kode_grade` "
                    + "LEFT JOIN `tb_grading_bahan_jadi` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_grading_bahan_jadi`.`kode_asal_bahan_jadi` "
                    + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_grading_bahan_jadi`.`grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode` "
                    + "WHERE `nama_supplier` LIKE '%" + txt_search_supplier.getText() + "%' "
                    + "AND `tb_laporan_produksi`.`no_kartu_waleta` LIKE '%" + txt_no_kartu.getText() + "%' "
                    + "AND `nama_rumah_burung` LIKE '%" + txt_search_rsb.getText() + "%' "
                    + "AND `tb_laporan_produksi`.`no_kartu_waleta` NOT LIKE '%CMP%'"
                    + "AND (`berat_basah` * `tb_grading_bahan_baku`.`harga_bahanbaku`) > 0 "
                    + tanggal
                    + "GROUP BY `tb_laporan_produksi`.`no_laporan_produksi` "
                    + "HAVING `rmb_jual` > 0) tabel "
                    + "GROUP BY `no_kartu_waleta` "
                    + "ORDER BY `no_kartu_waleta`";
//            System.out.println(sql);
            PreparedStatement pst = Utility.db.getConnection().prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = pst.executeQuery();
            Object[] row = new Object[20];
            int total_data = 0;
            while (rs.next()) {
                total_data++;
            }
            jProgressBar1.setMaximum(total_data);
            rs.beforeFirst();
            while (rs.next()) {
                jProgressBar1.setValue(jProgressBar1.getValue() + 1);
                row[0] = rs.getString("tgl_masuk");
                row[1] = rs.getString("no_kartu_waleta");
                row[2] = rs.getString("nama_supplier");
                row[3] = rs.getString("nama_rumah_burung");
                row[4] = rs.getInt("berat_real");
                row[5] = rs.getInt("berat_lp");
                row[6] = rs.getFloat("nilai_baku");
                row[7] = rs.getInt("kaki");
                double biaya_kaki = (harga_kaki / 1000) * kurs * rs.getFloat("kaki");
                row[8] = biaya_kaki;
                double biaya_produksi = biaya_produksi_per_gr * rs.getInt("berat_lp");
                row[9] = Math.round(biaya_produksi);
                row[10] = rs.getInt("berat_grading");
                double harga_jual = rs.getDouble("rmb_jual") * kurs * 0.98f;
                row[11] = Math.round(harga_jual);
                double gross_margin = harga_jual - (biaya_produksi + rs.getDouble("nilai_baku") + biaya_kaki);
                row[12] = Math.round(gross_margin);
                float persen_margin = ((float) gross_margin / (rs.getFloat("nilai_baku") + (float) biaya_produksi)) * 100f;
                row[13] = Math.round(persen_margin * 100f) / 100f;

                String sql_stok = "SELECT `no_kartu_waleta`, SUM(`kpg_masuk`) AS 'kpg_masuk', SUM(`gram_masuk`) AS 'gram_masuk', "
                        + "SUM(`kpg_lp`) AS 'kpg_lp', SUM(`gram_lp`) AS 'gram_lp', "
                        + "SUM(`kpg_jual`) AS 'kpg_jual', SUM(`gram_jual`) AS 'gram_jual', "
                        + "SUM(`kpg_cmp`) AS 'kpg_cmp', SUM(`gram_cmp`) AS 'gram_cmp' "
                        + "FROM"
                        + "(SELECT `tb_grading_bahan_baku`.`no_kartu_waleta`, `tb_grading_bahan_baku`.`kode_grade`,  "
                        + "`tb_grading_bahan_baku`.`jumlah_keping` AS 'kpg_masuk', `tb_grading_bahan_baku`.`total_berat` AS 'gram_masuk', \n"
                        + "`kpg_lp`, `gram_lp`, `kpg_jual`, `gram_jual`, `kpg_cmp`, `gram_cmp`\n"
                        + "FROM `tb_grading_bahan_baku`\n"
                        + "LEFT JOIN `tb_bahan_baku_masuk` ON `tb_grading_bahan_baku`.`no_kartu_waleta` = `tb_bahan_baku_masuk`.`no_kartu_waleta`\n"
                        + "LEFT JOIN (SELECT `no_kartu_waleta`, `kode_grade`, SUM(`tb_laporan_produksi`.`jumlah_keping`) AS 'kpg_lp', SUM(`tb_laporan_produksi`.`berat_basah`) AS 'gram_lp' FROM `tb_laporan_produksi` GROUP BY `no_kartu_waleta`, `kode_grade`) LP "
                        + "ON `tb_grading_bahan_baku`.`no_kartu_waleta` = LP.`no_kartu_waleta` AND `tb_grading_bahan_baku`.`kode_grade` = LP.`kode_grade`\n"
                        + "LEFT JOIN (SELECT `no_kartu_waleta`, `kode_grade`, SUM(`total_keping_keluar`) AS 'kpg_jual', SUM(`total_berat_keluar`) AS 'gram_jual' FROM `tb_bahan_baku_keluar` GROUP BY `no_kartu_waleta`, `kode_grade`) JUAL "
                        + "ON `tb_grading_bahan_baku`.`no_kartu_waleta` = JUAL.`no_kartu_waleta` AND `tb_grading_bahan_baku`.`kode_grade` = JUAL.`kode_grade`\n"
                        + "LEFT JOIN (SELECT `no_grading`, SUM(`tb_kartu_cmp_detail`.`keping`) AS 'kpg_cmp', SUM(`tb_kartu_cmp_detail`.`gram`) AS 'gram_cmp' FROM `tb_kartu_cmp_detail` GROUP BY `no_grading`) CMP "
                        + "ON `tb_grading_bahan_baku`.`no_grading` = CMP.`no_grading`\n"
                        + "WHERE `tb_grading_bahan_baku`.`no_kartu_waleta` = '" + rs.getString("no_kartu_waleta") + "'"
                        + "GROUP BY `tb_grading_bahan_baku`.`no_grading`) data ";
                ResultSet rs_stok = Utility.db.getStatement().executeQuery(sql_stok);
                if (rs_stok.next()) {
                    row[14] = rs_stok.getInt("kpg_masuk") - (rs_stok.getInt("kpg_lp") + rs_stok.getInt("kpg_jual") + rs_stok.getInt("kpg_cmp"));
                    row[15] = rs_stok.getInt("gram_masuk") - (rs_stok.getInt("gram_lp") + rs_stok.getInt("gram_jual") + rs_stok.getInt("gram_cmp"));
                }
                model.addRow(row);
                total_nilai_baku = total_nilai_baku + rs.getInt("nilai_baku");
                total_biaya_produksi = total_biaya_produksi + biaya_produksi;
                total_biaya_kaki = total_biaya_kaki + biaya_kaki;
                total_harga_jual = total_harga_jual + harga_jual;
                total_margin = total_margin + gross_margin;
            }

            label_total_Lengkap.setText(decimalFormat.format(total_data));
            label_total_baku.setText(decimalFormat.format(total_nilai_baku));
            label_total_produksi.setText(decimalFormat.format(total_biaya_produksi));
            label_total_kaki.setText(decimalFormat.format(total_biaya_kaki));
            label_total_jual.setText(decimalFormat.format(total_harga_jual));
            label_total_margin.setText(decimalFormat.format(total_margin));
            double persen_margin = (total_margin / (total_nilai_baku + total_biaya_produksi + total_biaya_kaki)) * 100;
            decimalFormat.setMaximumFractionDigits(1);
            label_total_margin_persen.setText("(" + decimalFormat.format(persen_margin) + "%)");

            ColumnsAutoSizer.sizeColumnsToFit(Table_KartuBaku);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_Rekap_Biaya_per_KartuBaku.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void refresh() {
        try {
            txt_no_kartu.setEnabled(false);
            txt_search_supplier.setEnabled(false);
            txt_search_rsb.setEnabled(false);
            txt_kurs.setEnabled(false);
            txt_biaya_produksi.setEnabled(false);
            txt_harga_kaki.setEnabled(false);
            Date_1.setEnabled(false);
            Date_2.setEnabled(false);
            button_refresh.setEnabled(false);
            jProgressBar1.setMinimum(0);
            jProgressBar1.setValue(0);
            jProgressBar1.setStringPainted(true);
            Thread thread = new Thread() {
                @Override
                public void run() {
                    refreshTable_Kartu();
                    jProgressBar1.setValue(jProgressBar1.getMaximum());
                    JOptionPane.showMessageDialog(null, "Proses Selesai !");
                    txt_no_kartu.setEnabled(true);
                    txt_search_supplier.setEnabled(true);
                    txt_search_rsb.setEnabled(true);
                    txt_kurs.setEnabled(true);
                    txt_biaya_produksi.setEnabled(true);
                    txt_harga_kaki.setEnabled(true);
                    Date_1.setEnabled(true);
                    Date_2.setEnabled(true);
                    button_refresh.setEnabled(true);
                }
            };
            thread.start();
        } catch (Exception e) {
            Logger.getLogger(JPanel_Rekap_Biaya_per_KartuBaku.class.getName()).log(Level.SEVERE, null, e);
            txt_no_kartu.setEnabled(true);
            txt_search_supplier.setEnabled(true);
            txt_search_rsb.setEnabled(true);
            txt_kurs.setEnabled(true);
            txt_biaya_produksi.setEnabled(true);
            txt_harga_kaki.setEnabled(true);
            Date_1.setEnabled(true);
            Date_2.setEnabled(true);
            button_refresh.setEnabled(true);
        }
    }

    public void refreshTableGrading_supplier(String no_kartu) {
        try {
            DefaultTableModel model = (DefaultTableModel) tabel_grade_supplier.getModel();
            model.setRowCount(0);
            decimalFormat.setMaximumFractionDigits(0);
            int total_gram = 0, total_nilai = 0;
            sql = "SELECT `grade_supplier`, `gram`, `harga_kg`, (`gram` * `harga_kg` / 1000) AS 'total'\n"
                    + "FROM `tb_grading_baku_supplier` WHERE `no_kartu_waleta` = '" + no_kartu + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[5];
            while (rs.next()) {
                row[0] = rs.getString("grade_supplier");
                row[1] = rs.getInt("gram");
                row[2] = rs.getInt("harga_kg");
                row[3] = rs.getInt("total");
                model.addRow(row);
                total_gram = total_gram + rs.getInt("gram");
                total_nilai = total_nilai + rs.getInt("total");
            }
            ColumnsAutoSizer.sizeColumnsToFit(tabel_grade_supplier);
            label_total_data_grading_supplier.setText(Integer.toString(tabel_grade_supplier.getRowCount()));
            label_total_grading_supplier.setText("Total : " + decimalFormat.format(total_gram) + "Gr  Rp." + decimalFormat.format(total_nilai));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_Rekap_Biaya_per_KartuBaku.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void refreshTableGradingBaku(String no_kartu) {
        try {
            DefaultTableModel model = (DefaultTableModel) tabel_hasilGrading_baku.getModel();
            model.setRowCount(0);
            decimalFormat.setMaximumFractionDigits(0);
            int total_gram = 0, total_nilai = 0;
            sql = "SELECT `kode_grade`, `jumlah_keping`, `total_berat`, `harga_bahanbaku`, (`total_berat` * `harga_bahanbaku`) AS 'total' \n"
                    + "FROM `tb_grading_bahan_baku` \n"
                    + "WHERE `no_kartu_waleta` = '" + no_kartu + "' ORDER BY `harga_bahanbaku` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[5];
            while (rs.next()) {
                row[0] = rs.getString("kode_grade");
                row[1] = rs.getInt("total_berat");
                row[2] = rs.getInt("harga_bahanbaku");
                row[3] = rs.getInt("total");
                model.addRow(row);
                total_gram = total_gram + rs.getInt("total_berat");
                total_nilai = total_nilai + rs.getInt("total");
            }
            ColumnsAutoSizer.sizeColumnsToFit(tabel_hasilGrading_baku);
            label_total_data_grading_baku.setText(Integer.toString(tabel_hasilGrading_baku.getRowCount()));
            label_total_grading_baku.setText("Total : " + decimalFormat.format(total_gram) + "Gr  Rp." + decimalFormat.format(total_nilai));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_Rekap_Biaya_per_KartuBaku.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void refreshTableGradingJadi(String no_kartu) {
        try {
            DefaultTableModel model = (DefaultTableModel) tabel_hasilGrading_jadi.getModel();
            model.setRowCount(0);
            decimalFormat.setMaximumFractionDigits(0);
            long total_gram = 0, total_nilai = 0;
            double kurs = Double.valueOf(txt_kurs.getText());
            sql = "SELECT `tb_grading_bahan_jadi`.`grade_bahan_jadi`, `tb_grade_bahan_jadi`.`kode_grade`, `tb_grading_bahan_jadi`.`gram`, `tb_grade_bahan_jadi`.`harga` AS 'cny_kg',  "
                    + "SUM((`tb_grading_bahan_jadi`.`gram`/1000) * `tb_grade_bahan_jadi`.`harga`) AS 'rmb_jual' "
                    + "FROM `tb_grading_bahan_jadi` "
                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_grading_bahan_jadi`.`kode_asal_bahan_jadi` "
                    + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_grading_bahan_jadi`.`grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode` "
                    + "WHERE `tb_laporan_produksi`.`no_kartu_waleta` = '" + no_kartu + "'"
                    + "GROUP BY `tb_grading_bahan_jadi`.`grade_bahan_jadi` ";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[5];
            while (rs.next()) {
                row[0] = rs.getString("kode_grade");
                row[1] = rs.getInt("gram");
                row[2] = rs.getInt("cny_kg");
                double harga_jual = rs.getDouble("rmb_jual") * kurs * 0.98d;
                row[3] = Math.round(harga_jual);
                model.addRow(row);
                total_gram = total_gram + rs.getInt("gram");
                total_nilai = total_nilai + Math.round(harga_jual);
            }
            ColumnsAutoSizer.sizeColumnsToFit(tabel_hasilGrading_jadi);
            label_total_data_grading_jadi.setText(Integer.toString(tabel_hasilGrading_jadi.getRowCount()));
            label_total_grading_jadi.setText("Total : " + decimalFormat.format(total_gram) + "Gr  Rp." + decimalFormat.format(total_nilai));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_Rekap_Biaya_per_KartuBaku.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void refreshTablePembelian() {
        try {
            DefaultTableModel model = (DefaultTableModel) Table_PembelianBaku.getModel();
            model.setRowCount(0);
            decimalFormat.setMaximumFractionDigits(0);

            String filter_tanggal = "";
            if (Date_3.getDate() != null && Date_4.getDate() != null) {
                filter_tanggal = "AND `tb_bahan_baku_masuk`.`tgl_masuk` BETWEEN '" + dateFormat.format(Date_3.getDate()) + "' AND '" + dateFormat.format(Date_4.getDate()) + "'";
            }
            
            sql = "SELECT `no_grading`, `tb_grading_baku_supplier`.`no_kartu_waleta`, `tb_supplier`.`nama_supplier`, `tb_bahan_baku_masuk`.`tgl_masuk`, `grade_supplier`, `gram`, `harga_kg` \n"
                    + "FROM `tb_grading_baku_supplier` \n"
                    + "LEFT JOIN `tb_bahan_baku_masuk` ON `tb_grading_baku_supplier`.`no_kartu_waleta` = `tb_bahan_baku_masuk`.`no_kartu_waleta`\n"
                    + "LEFT JOIN `tb_supplier` ON `tb_bahan_baku_masuk`.`kode_supplier` = `tb_supplier`.`kode_supplier`\n"
                    + "WHERE "
                    + "`tb_supplier`.`nama_supplier` <> 'Panen' "
                    + "AND `tb_grading_baku_supplier`.`no_kartu_waleta` LIKE '%" + txt_no_kartu1.getText() + "%' "
                    + "AND `tb_supplier`.`nama_supplier` LIKE '%" + txt_search_supplier1.getText() + "%' "
                    + "AND `grade_supplier` LIKE '%" + txt_search_grade_supplier.getText() + "%' "
                    + filter_tanggal
                    + "ORDER BY `tb_bahan_baku_masuk`.`tgl_masuk` DESC, `grade_supplier`";

            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[10];
            while (rs.next()) {
                row[0] = rs.getString("tgl_masuk");
                row[1] = rs.getString("no_kartu_waleta");
                row[2] = rs.getString("nama_supplier");
                row[3] = rs.getString("grade_supplier");
                row[4] = rs.getInt("gram");
                row[5] = rs.getInt("harga_kg");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_PembelianBaku);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_Rekap_Biaya_per_KartuBaku.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel_search_laporan_produksi = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        txt_search_supplier = new javax.swing.JTextField();
        button_refresh = new javax.swing.JButton();
        Date_1 = new com.toedter.calendar.JDateChooser();
        Date_2 = new com.toedter.calendar.JDateChooser();
        jLabel15 = new javax.swing.JLabel();
        txt_no_kartu = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        txt_kurs = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        txt_biaya_produksi = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        txt_harga_kaki = new javax.swing.JTextField();
        txt_search_rsb = new javax.swing.JTextField();
        jScrollPane6 = new javax.swing.JScrollPane();
        Table_KartuBaku = new javax.swing.JTable();
        button_export = new javax.swing.JButton();
        jLabel22 = new javax.swing.JLabel();
        label_total_Lengkap = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        label_total_baku = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        label_total_produksi = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        label_total_jual = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        label_total_margin = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        label_total_kaki = new javax.swing.JLabel();
        label_total_margin_persen = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel4 = new javax.swing.JPanel();
        label_total_data_grading_supplier = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tabel_grade_supplier = new javax.swing.JTable();
        label_total_grading_supplier = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabel_hasilGrading_baku = new javax.swing.JTable();
        label_total_data_grading_baku = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        label_total_grading_baku = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tabel_hasilGrading_jadi = new javax.swing.JTable();
        label_total_data_grading_jadi = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        label_total_grading_jadi = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jProgressBar1 = new javax.swing.JProgressBar();
        jPanel2 = new javax.swing.JPanel();
        jPanel_search_laporan_produksi1 = new javax.swing.JPanel();
        jLabel32 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        txt_search_supplier1 = new javax.swing.JTextField();
        button_refresh1 = new javax.swing.JButton();
        Date_3 = new com.toedter.calendar.JDateChooser();
        Date_4 = new com.toedter.calendar.JDateChooser();
        jLabel33 = new javax.swing.JLabel();
        txt_no_kartu1 = new javax.swing.JTextField();
        jLabel37 = new javax.swing.JLabel();
        button_export1 = new javax.swing.JButton();
        jLabel39 = new javax.swing.JLabel();
        txt_search_grade_supplier = new javax.swing.JTextField();
        jScrollPane7 = new javax.swing.JScrollPane();
        Table_PembelianBaku = new javax.swing.JTable();

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel_search_laporan_produksi.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_search_laporan_produksi.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        jPanel_search_laporan_produksi.setPreferredSize(new java.awt.Dimension(1334, 61));

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel13.setText("Supplier :");

        jLabel34.setBackground(new java.awt.Color(255, 255, 255));
        jLabel34.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel34.setText("Sampai");

        txt_search_supplier.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_supplier.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_supplierKeyPressed(evt);
            }
        });

        button_refresh.setBackground(new java.awt.Color(255, 255, 255));
        button_refresh.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_refresh.setText("Refresh");
        button_refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refreshActionPerformed(evt);
            }
        });

        Date_1.setBackground(new java.awt.Color(255, 255, 255));
        Date_1.setToolTipText("");
        Date_1.setDateFormatString("dd MMMM yyyy");
        Date_1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Date_1.setMinSelectableDate(new java.util.Date(1420048915000L));

        Date_2.setBackground(new java.awt.Color(255, 255, 255));
        Date_2.setDateFormatString("dd MMMM yyyy");
        Date_2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel15.setText("No Kartu :");

        txt_no_kartu.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_no_kartu.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_no_kartuKeyPressed(evt);
            }
        });

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel16.setText("Rumah Burung :");

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel17.setText("Tanggal Masuk :");

        jLabel20.setBackground(new java.awt.Color(255, 255, 255));
        jLabel20.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel20.setText("Kurs RMB to IDR :");

        txt_kurs.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_kurs.setText("2250");
        txt_kurs.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_kursKeyPressed(evt);
            }
        });

        jLabel21.setBackground(new java.awt.Color(255, 255, 255));
        jLabel21.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel21.setText("Biaya Produksi / Gr (Rp) :");

        txt_biaya_produksi.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_biaya_produksi.setText("3550");
        txt_biaya_produksi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_biaya_produksiKeyPressed(evt);
            }
        });

        jLabel23.setBackground(new java.awt.Color(255, 255, 255));
        jLabel23.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel23.setText("Harga Kaki (RMB/Kg)");

        txt_harga_kaki.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_harga_kaki.setText("2000");
        txt_harga_kaki.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_harga_kakiKeyPressed(evt);
            }
        });

        txt_search_rsb.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_rsb.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_rsbKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_search_laporan_produksiLayout = new javax.swing.GroupLayout(jPanel_search_laporan_produksi);
        jPanel_search_laporan_produksi.setLayout(jPanel_search_laporan_produksiLayout);
        jPanel_search_laporan_produksiLayout.setHorizontalGroup(
            jPanel_search_laporan_produksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_search_laporan_produksiLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_search_laporan_produksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_search_laporan_produksiLayout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_no_kartu, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_supplier, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_rsb, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_1, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel34)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_2, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_refresh))
                    .addGroup(jPanel_search_laporan_produksiLayout.createSequentialGroup()
                        .addComponent(jLabel20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_kurs, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel21)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_biaya_produksi, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel23)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_harga_kaki, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(303, Short.MAX_VALUE))
        );
        jPanel_search_laporan_produksiLayout.setVerticalGroup(
            jPanel_search_laporan_produksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_search_laporan_produksiLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_search_laporan_produksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_supplier, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_refresh, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_rsb, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_no_kartu, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_search_laporan_produksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_kurs, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_biaya_produksi, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_harga_kaki, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        Table_KartuBaku.setAutoCreateRowSorter(true);
        Table_KartuBaku.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_KartuBaku.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tgl Masuk", "No Kartu", "Supplier", "RSB", "Berat Grading", "Jadi LP", "Baku (Rp)", "Kaki (Gr)", "Nilai Kaki", "Biaya Prod.", "BJD", "Nilai BJD", "Gross Margin", "%", "Stok Kpg", "Stok Gram"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Table_KartuBaku.getTableHeader().setReorderingAllowed(false);
        jScrollPane6.setViewportView(Table_KartuBaku);

        button_export.setBackground(new java.awt.Color(255, 255, 255));
        button_export.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export.setText("Export");
        button_export.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_exportActionPerformed(evt);
            }
        });

        jLabel22.setBackground(new java.awt.Color(255, 255, 255));
        jLabel22.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel22.setText("Jumlah Kartu :");

        label_total_Lengkap.setBackground(new java.awt.Color(255, 255, 255));
        label_total_Lengkap.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_Lengkap.setText("0");

        jLabel24.setBackground(new java.awt.Color(255, 255, 255));
        jLabel24.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel24.setText("Total Biaya Baku :");

        label_total_baku.setBackground(new java.awt.Color(255, 255, 255));
        label_total_baku.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_baku.setText("0");

        jLabel26.setBackground(new java.awt.Color(255, 255, 255));
        jLabel26.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel26.setText("Total Biaya Produksi :");

        label_total_produksi.setBackground(new java.awt.Color(255, 255, 255));
        label_total_produksi.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_produksi.setText("0");

        jLabel28.setBackground(new java.awt.Color(255, 255, 255));
        jLabel28.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel28.setText("Total Harga Jual :");

        label_total_jual.setBackground(new java.awt.Color(255, 255, 255));
        label_total_jual.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_jual.setText("0");

        jLabel30.setBackground(new java.awt.Color(255, 255, 255));
        jLabel30.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel30.setText("Total Gross Margin :");

        label_total_margin.setBackground(new java.awt.Color(255, 255, 255));
        label_total_margin.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_margin.setText("0");

        jLabel27.setBackground(new java.awt.Color(255, 255, 255));
        jLabel27.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel27.setText("Total Biaya Kaki :");

        label_total_kaki.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kaki.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_kaki.setText("0");

        label_total_margin_persen.setBackground(new java.awt.Color(255, 255, 255));
        label_total_margin_persen.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_margin_persen.setText("0");

        jLabel29.setBackground(new java.awt.Color(255, 255, 255));
        jLabel29.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel29.setText("Note : Price list x 98%, menggunakan price list terakhir, LP yang belum selesai di grading BJD tidak ikut dalam perhitungan");

        jTabbedPane2.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        label_total_data_grading_supplier.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_grading_supplier.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_data_grading_supplier.setText("0");

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel14.setText("Grading Supplier");

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel18.setText("Grade");

        tabel_grade_supplier.setAutoCreateRowSorter(true);
        tabel_grade_supplier.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tabel_grade_supplier.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Grade", "Gram", "Harga / Kg", "Total"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Double.class
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
        tabel_grade_supplier.setRowSelectionAllowed(false);
        jScrollPane3.setViewportView(tabel_grade_supplier);

        label_total_grading_supplier.setBackground(new java.awt.Color(255, 255, 255));
        label_total_grading_supplier.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_grading_supplier.setText("Total Gram & Nilai");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(label_total_data_grading_supplier)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel18))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 362, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(label_total_grading_supplier)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_total_data_grading_supplier, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 479, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(label_total_grading_supplier, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane2.addTab("Grade Supplier", jPanel4);

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));

        jLabel19.setBackground(new java.awt.Color(255, 255, 255));
        jLabel19.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel19.setText("Hasil Grading Baku");

        tabel_hasilGrading_baku.setAutoCreateRowSorter(true);
        tabel_hasilGrading_baku.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tabel_hasilGrading_baku.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Grade", "Gram", "Harga / Gr", "Total"
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
        tabel_hasilGrading_baku.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(tabel_hasilGrading_baku);

        label_total_data_grading_baku.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_grading_baku.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_data_grading_baku.setText("0");

        jLabel31.setBackground(new java.awt.Color(255, 255, 255));
        jLabel31.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel31.setText("Grade");

        label_total_grading_baku.setBackground(new java.awt.Color(255, 255, 255));
        label_total_grading_baku.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_grading_baku.setText("Total Gram & Nilai");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 362, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(label_total_data_grading_baku)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel31))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(label_total_grading_baku)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_data_grading_baku, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 479, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(label_total_grading_baku, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane2.addTab("Hasil Grading Baku", jPanel5);

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));

        jLabel25.setBackground(new java.awt.Color(255, 255, 255));
        jLabel25.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel25.setText("Hasil Grading Jadi");

        tabel_hasilGrading_jadi.setAutoCreateRowSorter(true);
        tabel_hasilGrading_jadi.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tabel_hasilGrading_jadi.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Grade", "Gram", "CNY / Kg", "Total Rp."
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
        tabel_hasilGrading_jadi.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(tabel_hasilGrading_jadi);

        label_total_data_grading_jadi.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_grading_jadi.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_data_grading_jadi.setText("0");

        jLabel36.setBackground(new java.awt.Color(255, 255, 255));
        jLabel36.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel36.setText("Grade");

        label_total_grading_jadi.setBackground(new java.awt.Color(255, 255, 255));
        label_total_grading_jadi.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_grading_jadi.setText("Total Gram & Nilai");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel25)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 220, Short.MAX_VALUE)
                        .addComponent(label_total_data_grading_jadi)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel36))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(label_total_grading_jadi)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_data_grading_jadi, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 479, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(label_total_grading_jadi, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane2.addTab("Hasil Grading Jadi", jPanel6);

        jLabel38.setBackground(new java.awt.Color(255, 255, 255));
        jLabel38.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel38.setText("Loading :");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel_search_laporan_produksi, javax.swing.GroupLayout.DEFAULT_SIZE, 1341, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane6)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel29)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(button_export)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel38)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel22)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_Lengkap))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel24)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_baku)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLabel26)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_produksi)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLabel27)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_kaki))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel28)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_jual)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel30)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_margin)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_margin_persen)))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 387, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel_search_laporan_produksi, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(button_export, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_Lengkap, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(label_total_baku, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(label_total_kaki, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(label_total_produksi, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_jual, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_margin, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_margin_persen, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap())
                    .addComponent(jTabbedPane2)))
        );

        jTabbedPane1.addTab("Margin Kartu", jPanel1);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jPanel_search_laporan_produksi1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_search_laporan_produksi1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        jPanel_search_laporan_produksi1.setPreferredSize(new java.awt.Dimension(1334, 61));

        jLabel32.setBackground(new java.awt.Color(255, 255, 255));
        jLabel32.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel32.setText("Supplier :");

        jLabel35.setBackground(new java.awt.Color(255, 255, 255));
        jLabel35.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel35.setText("Sampai");

        txt_search_supplier1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_supplier1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_supplier1KeyPressed(evt);
            }
        });

        button_refresh1.setBackground(new java.awt.Color(255, 255, 255));
        button_refresh1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_refresh1.setText("Refresh");
        button_refresh1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refresh1ActionPerformed(evt);
            }
        });

        Date_3.setBackground(new java.awt.Color(255, 255, 255));
        Date_3.setToolTipText("");
        Date_3.setDateFormatString("dd MMMM yyyy");
        Date_3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Date_3.setMinSelectableDate(new java.util.Date(1420048915000L));

        Date_4.setBackground(new java.awt.Color(255, 255, 255));
        Date_4.setDateFormatString("dd MMMM yyyy");
        Date_4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel33.setBackground(new java.awt.Color(255, 255, 255));
        jLabel33.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel33.setText("No Kartu :");

        txt_no_kartu1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_no_kartu1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_no_kartu1KeyPressed(evt);
            }
        });

        jLabel37.setBackground(new java.awt.Color(255, 255, 255));
        jLabel37.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel37.setText("Tanggal Masuk :");

        button_export1.setBackground(new java.awt.Color(255, 255, 255));
        button_export1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export1.setText("Export");
        button_export1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export1ActionPerformed(evt);
            }
        });

        jLabel39.setBackground(new java.awt.Color(255, 255, 255));
        jLabel39.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel39.setText("Grade :");

        txt_search_grade_supplier.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_grade_supplier.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_grade_supplierKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_search_laporan_produksi1Layout = new javax.swing.GroupLayout(jPanel_search_laporan_produksi1);
        jPanel_search_laporan_produksi1.setLayout(jPanel_search_laporan_produksi1Layout);
        jPanel_search_laporan_produksi1Layout.setHorizontalGroup(
            jPanel_search_laporan_produksi1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_search_laporan_produksi1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel33)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_no_kartu1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel32)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_search_supplier1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel39)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_search_grade_supplier, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel37)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Date_3, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel35)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Date_4, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_refresh1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_export1)
                .addContainerGap(275, Short.MAX_VALUE))
        );
        jPanel_search_laporan_produksi1Layout.setVerticalGroup(
            jPanel_search_laporan_produksi1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_search_laporan_produksi1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_search_laporan_produksi1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_supplier1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_no_kartu1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_grade_supplier, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_refresh1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        Table_PembelianBaku.setAutoCreateRowSorter(true);
        Table_PembelianBaku.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_PembelianBaku.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tgl Masuk", "No Kartu", "Supplier", "Grade Supplier", "Berat (Gram)", "Harga (Rp)"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class
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
        Table_PembelianBaku.getTableHeader().setReorderingAllowed(false);
        jScrollPane7.setViewportView(Table_PembelianBaku);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel_search_laporan_produksi1, javax.swing.GroupLayout.DEFAULT_SIZE, 1341, Short.MAX_VALUE)
                    .addComponent(jScrollPane7))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel_search_laporan_produksi1, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 597, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Pembelian Baku", jPanel2);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void button_refreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refreshActionPerformed
        // TODO add your handling code here:
        refresh();
    }//GEN-LAST:event_button_refreshActionPerformed

    private void txt_no_kartuKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_no_kartuKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refresh();
        }
    }//GEN-LAST:event_txt_no_kartuKeyPressed

    private void txt_kursKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_kursKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refresh();
        }
    }//GEN-LAST:event_txt_kursKeyPressed

    private void txt_biaya_produksiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_biaya_produksiKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refresh();
        }
    }//GEN-LAST:event_txt_biaya_produksiKeyPressed

    private void button_exportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_exportActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Table_KartuBaku.getModel();
        ExportToExcel.writeToExcel(model, this);
    }//GEN-LAST:event_button_exportActionPerformed

    private void txt_harga_kakiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_harga_kakiKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refresh();
        }
    }//GEN-LAST:event_txt_harga_kakiKeyPressed

    private void txt_search_supplierKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_supplierKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refresh();
        }
    }//GEN-LAST:event_txt_search_supplierKeyPressed

    private void txt_search_rsbKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_rsbKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refresh();
        }
    }//GEN-LAST:event_txt_search_rsbKeyPressed

    private void txt_search_supplier1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_supplier1KeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTablePembelian();
        }
    }//GEN-LAST:event_txt_search_supplier1KeyPressed

    private void button_refresh1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refresh1ActionPerformed
        // TODO add your handling code here:
        refreshTablePembelian();
    }//GEN-LAST:event_button_refresh1ActionPerformed

    private void txt_no_kartu1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_no_kartu1KeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTablePembelian();
        }
    }//GEN-LAST:event_txt_no_kartu1KeyPressed

    private void button_export1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export1ActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Table_PembelianBaku.getModel();
        ExportToExcel.writeToExcel(model, this);
    }//GEN-LAST:event_button_export1ActionPerformed

    private void txt_search_grade_supplierKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_grade_supplierKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTablePembelian();
        }
    }//GEN-LAST:event_txt_search_grade_supplierKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser Date_1;
    private com.toedter.calendar.JDateChooser Date_2;
    private com.toedter.calendar.JDateChooser Date_3;
    private com.toedter.calendar.JDateChooser Date_4;
    public static javax.swing.JTable Table_KartuBaku;
    public static javax.swing.JTable Table_PembelianBaku;
    private javax.swing.JButton button_export;
    private javax.swing.JButton button_export1;
    private javax.swing.JButton button_refresh;
    private javax.swing.JButton button_refresh1;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
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
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel_search_laporan_produksi;
    private javax.swing.JPanel jPanel_search_laporan_produksi1;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JLabel label_total_Lengkap;
    private javax.swing.JLabel label_total_baku;
    private javax.swing.JLabel label_total_data_grading_baku;
    private javax.swing.JLabel label_total_data_grading_jadi;
    private javax.swing.JLabel label_total_data_grading_supplier;
    private javax.swing.JLabel label_total_grading_baku;
    private javax.swing.JLabel label_total_grading_jadi;
    private javax.swing.JLabel label_total_grading_supplier;
    private javax.swing.JLabel label_total_jual;
    private javax.swing.JLabel label_total_kaki;
    private javax.swing.JLabel label_total_margin;
    private javax.swing.JLabel label_total_margin_persen;
    private javax.swing.JLabel label_total_produksi;
    private javax.swing.JTable tabel_grade_supplier;
    private javax.swing.JTable tabel_hasilGrading_baku;
    private javax.swing.JTable tabel_hasilGrading_jadi;
    private javax.swing.JTextField txt_biaya_produksi;
    private javax.swing.JTextField txt_harga_kaki;
    private javax.swing.JTextField txt_kurs;
    private javax.swing.JTextField txt_no_kartu;
    private javax.swing.JTextField txt_no_kartu1;
    private javax.swing.JTextField txt_search_grade_supplier;
    private javax.swing.JTextField txt_search_rsb;
    private javax.swing.JTextField txt_search_supplier;
    private javax.swing.JTextField txt_search_supplier1;
    // End of variables declaration//GEN-END:variables
}
