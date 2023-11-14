package waleta_system.Manajemen;

import java.awt.Component;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;

public class JFrame_KPI_Finance extends javax.swing.JFrame {

    GraphicsDevice device = GraphicsEnvironment
            .getLocalGraphicsEnvironment().getScreenDevices()[0];
    String sql = null;
    ResultSet rs;
    PreparedStatement pst;
    Date today = new Date();//date berisikan tanggal hari ini
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    int[] bulan, tahun, RawMatProcessed, berat_jastip, Kurs, berat_sales, berat_kuota_used, berat_lp_berharga;
    double[] MarginProduksi, harga_baku, berat_bjd_lengkap, berat_bjd_tidak_lengkap;
    double[] harga_bjd2;
    long[] margin_jastip, Total_Biaya;
    int jumlah_bulan = 12;
    long[] Biaya_baku_non_lp;
    long[] Biaya_btkl;
    long[] Biaya_overhead;
    long[] Biaya_payroll_staff;
    long[] Biaya_umum;
    long[] Biaya_ekspor;

    public JFrame_KPI_Finance() {
        initComponents();
        try {
            Utility.db.connect();
        } catch (Exception ex) {
            Logger.getLogger(JFrame_KPI_Finance.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void init() {
        bulan = new int[jumlah_bulan];
        tahun = new int[jumlah_bulan];

        RawMatProcessed = new int[jumlah_bulan];
        berat_jastip = new int[jumlah_bulan];
        Kurs = new int[jumlah_bulan];
        berat_sales = new int[jumlah_bulan];
        berat_kuota_used = new int[jumlah_bulan];
        berat_lp_berharga = new int[jumlah_bulan];

        MarginProduksi = new double[jumlah_bulan];
        harga_baku = new double[jumlah_bulan];
//            harga_bjd = new double[jumlah_bulan];
        harga_bjd2 = new double[jumlah_bulan];
        berat_bjd_lengkap = new double[jumlah_bulan];
        berat_bjd_tidak_lengkap = new double[jumlah_bulan];

        margin_jastip = new long[jumlah_bulan];
        Total_Biaya = new long[jumlah_bulan];

        Biaya_baku_non_lp = new long[jumlah_bulan];
        Biaya_btkl = new long[jumlah_bulan];
        Biaya_overhead = new long[jumlah_bulan];
        Biaya_payroll_staff = new long[jumlah_bulan];
        Biaya_umum = new long[jumlah_bulan];
        Biaya_ekspor = new long[jumlah_bulan];

        decimalFormat.setGroupingUsed(true);
        decimalFormat.setMaximumFractionDigits(0);

        bulan[0] = Integer.valueOf(new SimpleDateFormat("MM").format(today));
        tahun[0] = Integer.valueOf(new SimpleDateFormat("yyyy").format(today));
        for (int x = 1; x < jumlah_bulan; x++) {
            bulan[x] = bulan[0] - x;
            tahun[x] = tahun[0];
            if (bulan[x] < 1) {
                bulan[x] = bulan[x] + 12;
                tahun[x] = tahun[x] - 1;
            }
        }

        JTableHeader TableHeader = Table_data.getTableHeader();
        TableColumnModel TColumnModel = TableHeader.getColumnModel();
        String header_bulan = "";
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();

        for (int x = 0; x < jumlah_bulan; x++) {
            header_bulan = months[bulan[x] - 1].substring(0, 3) + " " + tahun[x];
            TableColumn tc = TColumnModel.getColumn(x + 1);
            tc.setHeaderValue(header_bulan);
        }

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);

        refresh_table();
        Table_data.getTableHeader().setFont(new Font("Arial Narrow", Font.BOLD, 16));
    }

    public void refresh_table() {
        Kurs();
        RawMatProcessed();
        MarginJasTip_sales();
        Biaya();
        MarginProduksi_harga_bjd2();
        MarginProduksi_harga_bjd();
        perhitungan();

        Table_data.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                float x = 0;
                switch (row) {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 13:
                    case 14:
                    case 15:
                        comp.setBackground(Table_data.getBackground());
                        comp.setFont(this.getFont().deriveFont(Font.BOLD));
//                        try {
//                            x = Float.valueOf(Table_data.getValueAt(row, column).toString());
//                            if (x > 4) {
//                                comp.setBackground(new Color(255, 0, 0));
//                            } else if (x > 3.5f) {
//                                comp.setBackground(new Color(255, 255, 51));
//                            } else if (x <= 3.5f) {
//                                comp.setBackground(new Color(102, 255, 102));
//                            } else {
//                                comp.setBackground(Table_data.getBackground());
//                            }
//                        } catch (NumberFormatException e) {
//                        }
                        break;
                    default:
                        comp.setBackground(Table_data.getBackground());
                        break;
                }
                return comp;
            }
        });

        ColumnsAutoSizer.sizeColumnsToFit(Table_data);
    }

    public void RawMatProcessed() {
        try {
            sql = "SELECT `berat_basah`, `tb_grading_bahan_baku`.`harga_bahanbaku`, MONTH(`tanggal_lp`) AS 'bulan', YEAR(`tanggal_lp`) AS 'tahun' "
                    + "FROM `tb_laporan_produksi`"
                    + "LEFT JOIN `tb_grading_bahan_baku` ON `tb_grading_bahan_baku`.`no_kartu_waleta` = `tb_laporan_produksi`.`no_kartu_waleta` AND `tb_grading_bahan_baku`.`kode_grade` = `tb_laporan_produksi`.`kode_grade`\n"
                    + "WHERE `tanggal_lp` BETWEEN '" + tahun[jumlah_bulan - 1] + "-" + bulan[jumlah_bulan - 1] + "-01' AND '" + tahun[0] + "-" + bulan[0] + "-31'";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                for (int i = 0; i < jumlah_bulan; i++) {
                    if (rs.getInt("bulan") == bulan[i]) {
                        RawMatProcessed[i] = RawMatProcessed[i] + rs.getInt("berat_basah");
                        harga_baku[i] = harga_baku[i] + (rs.getDouble("berat_basah") * rs.getDouble("harga_bahanbaku"));
                        if (rs.getDouble("harga_bahanbaku") > 0) {
                            berat_lp_berharga[i] = berat_lp_berharga[i] + rs.getInt("berat_basah");
                        }
                    }
                }
            }
        } catch (SQLException e) {
            Logger.getLogger(JFrame_KPI_Finance.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void MarginJasTip_sales() {
        try {
            sql = "SELECT MONTH(`tgl_invoice`) AS 'bulan', YEAR(`tgl_invoice`) AS 'tahun', `buyer`, `value_from_jtp`, "
                    + "IFNULL(`berat_waleta`, 0) AS 'berat_waleta', "
                    + "IFNULL(`berat_esta`, 0) AS 'berat_esta', "
                    + "IFNULL(`berat_jastip`, 0) AS 'berat_jastip' "
                    + "FROM `tb_payment_report` \n"
                    + "LEFT JOIN `tb_buyer` ON `tb_payment_report`.`buyer` = `tb_buyer`.`kode_buyer`\n"
                    + "WHERE `tgl_invoice` BETWEEN '" + tahun[jumlah_bulan - 1] + "-" + bulan[jumlah_bulan - 1] + "-01' AND '" + tahun[0] + "-" + bulan[0] + "-31'";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                for (int i = 0; i < jumlah_bulan; i++) {
                    if (rs.getInt("bulan") == bulan[i]) {
                        margin_jastip[i] = margin_jastip[i] + rs.getLong("value_from_jtp");
                        berat_jastip[i] = berat_jastip[i] + rs.getInt("berat_jastip");
                        berat_sales[i] = berat_sales[i] + rs.getInt("berat_waleta");
                        if (rs.getString("buyer").substring(0, 2).equals("E-")) {//buyer ekspor china
                            berat_kuota_used[i] = berat_kuota_used[i] + (rs.getInt("berat_waleta") + rs.getInt("berat_esta") + rs.getInt("berat_jastip"));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            Logger.getLogger(JFrame_KPI_Finance.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void Biaya() {
        try {
            DefaultTableModel model = (DefaultTableModel) Table_data.getModel();
            decimalFormat.setMaximumFractionDigits(0);

            //BIAYA KESELURUHAN
            sql = "SELECT MONTH(`bulan`) AS 'bulan', YEAR(`bulan`) AS 'tahun', `nilai`, `Kategori1`, `jenis_pengeluaran` FROM `tb_biaya_pabrik`"
                    + "WHERE `tb_biaya_pabrik`.`bulan` BETWEEN '" + tahun[jumlah_bulan - 1] + "-" + bulan[jumlah_bulan - 1] + "-01' AND '" + tahun[0] + "-" + bulan[0] + "-31' "
                    + "AND `Kategori1` <> 'Laporan Keuangan'";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                for (int i = 0; i < jumlah_bulan; i++) {
                    if (rs.getInt("bulan") == bulan[i]) {
                        Total_Biaya[i] = Total_Biaya[i] + rs.getLong("nilai");
                        switch (rs.getString("Kategori1")) {
                            case "Biaya Bahan Baku":
                                Biaya_baku_non_lp[i] = Biaya_baku_non_lp[i] + rs.getLong("nilai");
                                break;
                            case "Biaya Tenaga Kerja Langsung":
                                Biaya_btkl[i] = Biaya_btkl[i] + rs.getLong("nilai");
                                break;
                            case "Biaya Overhead Pabrik":
                                Biaya_overhead[i] = Biaya_overhead[i] + rs.getLong("nilai");
                                break;
                            case "Biaya Umum":
                                switch (rs.getString("jenis_pengeluaran")) {
                                    case "Biaya Gaji, Lembur & THR":
                                    case "Biaya Bonus Pesangon & Kompensasi":
                                    case "Biaya Upah & Honorer":
                                    case "Biaya Tunjangan BPJS Kesehatan":
                                    case "Biaya Asuransi Karyawan":
                                    case "Biaya Tunjangan BPJS Ketenagakerjaan-JKK":
                                    case "Biaya Tunjangan BPJS Ketenagakerjaan-JKM":
                                    case "Biaya Tunjangan BPJS Ketenagakerjaan-JHT":
                                    case "Biaya Tunjangan BPJS Ketenagakerjaan-JP":
                                        Biaya_payroll_staff[i] = Biaya_payroll_staff[i] + rs.getLong("nilai");
                                        break;
                                    case "Biaya Fumigasi":
                                    case "Biaya Perijinan Ekspor":
                                    case "Biaya Asuransi Ekspor":
                                    case "Biaya Cargo":
                                        Biaya_ekspor[i] = Biaya_ekspor[i] + rs.getLong("nilai");
                                        break;
                                    default:
                                        Biaya_umum[i] = Biaya_umum[i] + rs.getLong("nilai");
                                        break;
                                }
                            default:
                                break;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            Logger.getLogger(JFrame_KPI_Finance.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void Kurs() {
        try {
            DefaultTableModel model = (DefaultTableModel) Table_data.getModel();
            sql = "SELECT `nilai`, MONTH(`tanggal`) AS 'bulan', YEAR(`tanggal`) AS 'tahun' FROM `tb_kurs`"
                    + "WHERE `tanggal` BETWEEN '" + tahun[jumlah_bulan - 1] + "-" + bulan[jumlah_bulan - 1] + "-01' AND '" + tahun[0] + "-" + bulan[0] + "-31'";
            rs = Utility.db.getStatement().executeQuery(sql);
            int[] count = new int[jumlah_bulan];
            while (rs.next()) {
                for (int i = 0; i < jumlah_bulan; i++) {
                    if (rs.getInt("bulan") == bulan[i]) {
                        Kurs[i] = Kurs[i] + rs.getInt("nilai");
                        count[i]++;
                    }
                }
            }

            for (int i = 0; i < jumlah_bulan; i++) {
                if (count[i] > 0) {
                    Kurs[i] = Kurs[i] / count[i];
                }
            }
        } catch (SQLException e) {
            Logger.getLogger(JFrame_KPI_Finance.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void MarginProduksi_harga_bjd() {
        try {
            decimalFormat.setMaximumFractionDigits(0);
            sql = "SELECT `tb_laporan_produksi`.`no_laporan_produksi`, MONTH(`tanggal_lp`) AS 'bulan', YEAR(`tanggal_lp`) AS 'tahun', `berat_basah`, "
                    + "SUM(`tb_grading_bahan_jadi`.`gram`) AS 'berat_grading', SUM((`tb_grading_bahan_jadi`.`gram`/1000) * `tb_grade_bahan_jadi`.`harga` ) AS 'cny'\n"
                    + "FROM `tb_laporan_produksi` "
                    + "LEFT JOIN `tb_grading_bahan_jadi` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_grading_bahan_jadi`.`kode_asal_bahan_jadi`\n"
                    + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_grading_bahan_jadi`.`grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode`\n"
                    + "WHERE `tanggal_lp` BETWEEN '" + tahun[jumlah_bulan - 1] + "-" + bulan[jumlah_bulan - 1] + "-01' AND '" + tahun[0] + "-" + bulan[0] + "-31'\n"
                    + "GROUP BY `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    + "ORDER BY `tb_laporan_produksi`.`no_laporan_produksi` ASC";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                for (int i = 0; i < jumlah_bulan; i++) {
                    if (rs.getInt("bulan") == bulan[i]) {
                        if (rs.getFloat("cny") > 0) {
                            double cny = rs.getDouble("cny") * 0.98 * Kurs[i];
//                            harga_bjd[i] = harga_bjd[i] + cny;
                        }
                        if (rs.getFloat("berat_grading") > 0) {
                            berat_bjd_lengkap[i] = berat_bjd_lengkap[i] + rs.getDouble("berat_basah");
                        } else {
                            berat_bjd_tidak_lengkap[i] = berat_bjd_tidak_lengkap[i] + rs.getDouble("berat_basah");
                        }
                    }
                }
            }

        } catch (SQLException e) {
            Logger.getLogger(JFrame_KPI_Finance.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void MarginProduksi_harga_bjd2() {
        try {
            decimalFormat.setMaximumFractionDigits(0);
            for (int i = 0; i < jumlah_bulan; i++) {
                sql = "SELECT SUM(`tb_grading_bahan_jadi`.`gram`) AS 'berat_grading', "
                        + "SUM(`tb_grading_bahan_jadi`.`gram` * "
                        + "IFNULL(("
                        + "SELECT `cny_kg` "
                        + "FROM `tb_grade_bahan_jadi_harga` "
                        + "WHERE `grade` = `tb_grading_bahan_jadi`.`grade_bahan_jadi` "
                        + "AND YEAR(`tanggal`) = '" + tahun[i] + "' AND MONTH(`tanggal`) = '" + bulan[i] + "' "
                        + "ORDER BY `tanggal` DESC "
                        + "LIMIT 1"
                        + "), `tb_grade_bahan_jadi`.`harga`)"
                        + " / 1000) AS 'cny'"
                        + "FROM `tb_grading_bahan_jadi` "
                        + "LEFT JOIN `tb_laporan_produksi` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_grading_bahan_jadi`.`kode_asal_bahan_jadi`\n"
                        + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_grading_bahan_jadi`.`grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode`\n"
                        + "WHERE YEAR(`tanggal_lp`) = '" + tahun[i] + "' AND MONTH(`tanggal_lp`) = '" + bulan[i] + "'\n";
                rs = Utility.db.getStatement().executeQuery(sql);
                if (rs.next()) {
                    if (rs.getDouble("cny") > 0d) {
                        double cny = rs.getDouble("cny") * 0.98 * Kurs[i];
                        harga_bjd2[i] = cny;
                    }
                }
            }
        } catch (SQLException e) {
            Logger.getLogger(JFrame_KPI_Finance.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void perhitungan() {
        DefaultTableModel model = (DefaultTableModel) Table_data.getModel();
//        int[] bulan, tahun, RawMatProcessed, berat_jastip, Kurs, kg_sales, berat_lp_berharga;
//        double[] MarginProduksi, harga_baku, harga_bjd;
//        long[] margin_jastip, Total_Biaya;

        int kuota = 25000;
        for (int i = jumlah_bulan - 1; i > -1; i--) {
            double cost_JTP = berat_jastip[i] * 850.f;
            double A_persen_berharga = (berat_bjd_lengkap[i] / (berat_bjd_lengkap[i] + berat_bjd_tidak_lengkap[i])) * 100.f;
            double B_biaya_hargabaku = Total_Biaya[i] + harga_baku[i] - cost_JTP;
//            double C_Margin = harga_bjd[i] - B_biaya_hargabaku;
            double C_Margin = harga_bjd2[i] - B_biaya_hargabaku;
            double C_persen_margin = (C_Margin / B_biaya_hargabaku) * 100.f;
            double D_margin_JTP = (margin_jastip[i] * Kurs[i]) - cost_JTP;
            double D_persen_margin_JTP = (D_margin_JTP / B_biaya_hargabaku) * 100.f;
            double E_total_margin = D_margin_JTP + C_Margin;
            double E_persen_margin_total = (E_total_margin / B_biaya_hargabaku) * 100.f;
            double F_margin_WLT_per_total = (C_Margin / E_total_margin) * 100.f;
            double F_margin_JTP_per_total = (D_margin_JTP / E_total_margin) * 100.f;

            double B1_raw_mat_cost = (Biaya_baku_non_lp[i] + harga_baku[i]) / 1000000.f;
            double B1_raw_mat_cost_kg = (B1_raw_mat_cost / RawMatProcessed[i]) * 1000.f; //Raw Mat Processed dalam gram
            double B1_raw_mat_persen_berharga = ((double) berat_lp_berharga[i] / (double) RawMatProcessed[i]) * 100.f; //Raw Mat Processed dalam gram
            double B2_labor_cost = Biaya_btkl[i] / 1000000.f;
            double B2_labor_cost_percent = (B2_labor_cost / RawMatProcessed[i]) * 1000.f; //
            double B3_overhead = Biaya_overhead[i] / 1000000.f;
            double B3_overhead_percent = (B3_overhead / RawMatProcessed[i]) * 1000.f; //
            double B4_payroll = Biaya_payroll_staff[i] / 1000000.f;
            double B4_payroll_percent = (B4_payroll / RawMatProcessed[i]) * 1000.f; //
            double B5_umum = Biaya_umum[i] / 1000000.f;
            double B5_umum_percent = (B5_umum / RawMatProcessed[i]) * 1000.f; //
            double B6_ekspor = Biaya_ekspor[i] / 1000000.f;
            double B6_ekspor_percent = (B6_ekspor / RawMatProcessed[i]) * 1000.f; //
            long total = Biaya_btkl[i] + Biaya_overhead[i] + Biaya_payroll_staff[i] + Biaya_umum[i] + Biaya_ekspor[i];
            double B7_total_cost_kg = (total / RawMatProcessed[i]) / 1000.f; //Raw Mat Processed dalam gram
            double cost_WLT_only = Total_Biaya[i] - cost_JTP;
            double cost_WLT_kg = (cost_WLT_only / RawMatProcessed[i]) / 1000.f; //Raw Mat Processed dalam gram

            decimalFormat.setMaximumFractionDigits(1);
            model.setValueAt(decimalFormat.format(Math.round(harga_bjd2[i] / 1000000.f)) + "/" + decimalFormat.format(A_persen_berharga) + "%", 0, i + 1);
//            model.setValueAt(decimalFormat.format(Math.round(harga_bjd[i] / 1000000.f)) + "/" + decimalFormat.format(A_persen_berharga) + "%", 1, i + 1);
            model.setValueAt(decimalFormat.format(Math.round(B_biaya_hargabaku / 1000000.f)), 1, i + 1);
            model.setValueAt(decimalFormat.format(Math.round(C_Margin / 1000000.f)) + "/" + decimalFormat.format(C_persen_margin) + "%", 2, i + 1);
            model.setValueAt(decimalFormat.format(Math.round(D_margin_JTP / 1000000.f)) + "/" + decimalFormat.format(D_persen_margin_JTP) + "%", 3, i + 1);
            model.setValueAt(decimalFormat.format(Math.round(E_total_margin / 1000000.f)) + "/" + decimalFormat.format(E_persen_margin_total) + "%", 4, i + 1);
            model.setValueAt(decimalFormat.format(F_margin_WLT_per_total) + "% /" + decimalFormat.format(F_margin_JTP_per_total) + "%", 5, i + 1);

            decimalFormat.setMaximumFractionDigits(2);
            model.setValueAt(decimalFormat.format(Math.round(B1_raw_mat_cost)) + "/" + decimalFormat.format(B1_raw_mat_cost_kg) + "jt/" + Math.round(B1_raw_mat_persen_berharga * 10d) / 10d + "%", 6, i + 1);
            model.setValueAt(decimalFormat.format(Math.round(B2_labor_cost)) + "/" + decimalFormat.format(B2_labor_cost_percent) + "jt", 7, i + 1);
            model.setValueAt(decimalFormat.format(Math.round(B3_overhead)) + "/" + decimalFormat.format(B3_overhead_percent) + "jt", 8, i + 1);
            model.setValueAt(decimalFormat.format(Math.round(B4_payroll)) + "/" + decimalFormat.format(B4_payroll_percent) + "jt", 9, i + 1);
            model.setValueAt(decimalFormat.format(Math.round(B5_umum)) + "/" + decimalFormat.format(B5_umum_percent) + "jt", 10, i + 1);
            model.setValueAt(decimalFormat.format(Math.round(B6_ekspor)) + "/" + decimalFormat.format(B6_ekspor_percent) + "jt", 11, i + 1);
            model.setValueAt(decimalFormat.format(Math.round(total / 1000000.f)) + "/" + decimalFormat.format(B7_total_cost_kg) + "jt", 12, i + 1);
            model.setValueAt(decimalFormat.format(Math.round(cost_JTP / 1000000.f)) + "/0.85", 13, i + 1);
            model.setValueAt(decimalFormat.format(Math.round(cost_WLT_only / 1000000.f)) + "/" + decimalFormat.format(cost_WLT_kg) + "jt", 14, i + 1);
            model.setValueAt(decimalFormat.format(Kurs[i]), 15, i + 1);
            model.setValueAt(decimalFormat.format(Math.round(RawMatProcessed[i] / 1000.f)), 16, i + 1);
            model.setValueAt(decimalFormat.format(Math.round(berat_jastip[i] / 1000.f)), 17, i + 1);
            model.setValueAt(decimalFormat.format(Math.round(berat_sales[i] / 1000.f)), 18, i + 1);
            model.setValueAt(decimalFormat.format(Math.round(berat_kuota_used[i] / 1000.f)), 19, i + 1);
            
            if (tahun[i] != tahun[(i + 1) == jumlah_bulan? i : (i + 1)]) {
                kuota = 25000;
            }
            model.setValueAt(decimalFormat.format(kuota - Math.round(berat_kuota_used[i] / 1000.f)), 20, i + 1);
            kuota = kuota - Math.round(berat_kuota_used[i] / 1000.f);
//            model.setValueAt(decimalFormat.format(C_persen_margin), 4, i + 1);
//            model.setValueAt(decimalFormat.format(Math.round((margin_jastip[i] * Kurs[i]) / 1000000.f)), 12, i + 1);
//            model.setValueAt(decimalFormat.format(Margin) + "/ " + decimalFormat.format(persen_margin) + "%", 11, i + 1);
//            model.setValueAt(decimalFormat.format(Math.round(((margin_jastip[i] * Kurs[i]) + MarginProduksi[i]) / 1000000.f)), 13, i + 1);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        Table_data = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("98% Price BJD");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        Table_data.setFont(new java.awt.Font("Arial Narrow", 0, 14)); // NOI18N
        Table_data.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"A. BJD Value", "", null, null, null, null, null, null, null, null, null, null, null},
                {"B. Tot Cost -JTP / Kg", null, null, null, null, null, null, null, null, null, null, null, null},
                {"C. Margin (A-B/B)", null, null, null, null, null, null, null, null, null, null, null, null},
                {"D. Margin JTP -cost (D/B)", null, null, null, null, null, null, null, null, null, null, null, null},
                {"E. Total Margin (C+D/B)", null, null, null, null, null, null, null, null, null, null, null, null},
                {"F. Margin WLT:JTP (%)", null, null, null, null, null, null, null, null, null, null, null, null},
                {"B1. Raw Mat Cost", null, null, null, null, null, null, null, null, null, null, null, null},
                {"B2. Labor Cost", null, null, null, null, null, null, null, null, null, null, null, null},
                {"B3. Overhead", null, null, null, null, null, null, null, null, null, null, null, null},
                {"B4. Payroll Staff", null, null, null, null, null, null, null, null, null, null, null, null},
                {"B5. General Cost", null, null, null, null, null, null, null, null, null, null, null, null},
                {"B6. Export", null, null, null, null, null, null, null, null, null, null, null, null},
                {"B7. Total (B2-B6)", null, null, null, null, null, null, null, null, null, null, null, null},
                {"B8. JTP Cost", null, null, null, null, null, null, null, null, null, null, null, null},
                {"Cost WLT (B7-B8)", null, null, null, null, null, null, null, null, null, null, null, null},
                {"Kurs", null, null, null, null, null, null, null, null, null, null, null, null},
                {"KG LP", null, null, null, null, null, null, null, null, null, null, null, null},
                {"KG JTP", null, null, null, null, null, null, null, null, null, null, null, null},
                {"KG SALES", null, null, null, null, null, null, null, null, null, null, null, null},
                {"Quota Used (Kg)", null, null, null, null, null, null, null, null, null, null, null, null},
                {"Remaining Quota (Kg)", null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Deskripsi", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
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
        Table_data.setRowHeight(35);
        Table_data.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(Table_data);
        if (Table_data.getColumnModel().getColumnCount() > 0) {
            Table_data.getColumnModel().getColumn(0).setMinWidth(140);
        }

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1366, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 771, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(JFrame_KPI_Finance.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JFrame_KPI_Finance.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JFrame_KPI_Finance.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JFrame_KPI_Finance.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JFrame_KPI_Finance chart = new JFrame_KPI_Finance();
                chart.pack();
                chart.setResizable(true);
                chart.setLocationRelativeTo(null);
                chart.setVisible(true);
                chart.setEnabled(true);
                chart.setExtendedState(JFrame.MAXIMIZED_BOTH);
                chart.init();
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable Table_data;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables
}
