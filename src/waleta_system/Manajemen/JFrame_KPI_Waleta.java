package waleta_system.Manajemen;

import java.awt.Color;
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

public class JFrame_KPI_Waleta extends javax.swing.JFrame {

    GraphicsDevice device = GraphicsEnvironment
            .getLocalGraphicsEnvironment().getScreenDevices()[0];
    String sql = null;
    ResultSet rs;
    PreparedStatement pst;
    Date today = new Date();//date berisikan tanggal hari ini
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    int[] bulan, tahun, RawMatProcessed, berat_jastip, Kurs;
    double[] Cost_KG, MarginProduksi;
    long[] margin_jastip;
    int jumlah_bulan = 12;

    public JFrame_KPI_Waleta() {
        initComponents();
        try {
            Utility.db.connect();
        } catch (Exception ex) {
            Logger.getLogger(JFrame_KPI_Waleta.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void init() {
        bulan = new int[jumlah_bulan];
        tahun = new int[jumlah_bulan];
        RawMatProcessed = new int[jumlah_bulan];
        berat_jastip = new int[jumlah_bulan];
        Kurs = new int[jumlah_bulan];
        Cost_KG = new double[jumlah_bulan];
        MarginProduksi = new double[jumlah_bulan];
        margin_jastip = new long[jumlah_bulan];

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
        Table_data.getTableHeader().setFont(new Font("Arial", Font.BOLD, 18));
    }

    public void refresh_table() {
        RawMatProcessed();
        FinishedGoods();
        JasTip();
        Rendemen();
        Kurs();
        Cost_per_KG();
        MarginProduksi();
        Cup_per_day();
        PickingPersonel();
        Nitrit();
        Kpg_per_AllKaryawan();

        Table_data.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                float x = 0;
                switch (row) {
                    case 8:
                    case 9:
                        comp.setBackground(Table_data.getBackground());
                        comp.setFont(this.getFont().deriveFont(Font.BOLD));
                        try {
                            x = Float.valueOf(Table_data.getValueAt(row, column).toString());
                            if (x > 4) {
                                comp.setBackground(new Color(255, 0, 0));
                            } else if (x > 3.5f) {
                                comp.setBackground(new Color(255, 255, 51));
                            } else if (x <= 3.5f) {
                                comp.setBackground(new Color(102, 255, 102));
                            } else {
                                comp.setBackground(Table_data.getBackground());
                            }
                        } catch (NumberFormatException e) {
                        }
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
            DefaultTableModel model = (DefaultTableModel) Table_data.getModel();
            sql = "SELECT `jumlah_keping`, `berat_basah`, MONTH(`tanggal_lp`) AS 'bulan', YEAR(`tanggal_lp`) AS 'tahun' FROM `tb_laporan_produksi`"
                    + "WHERE `tanggal_lp` BETWEEN '" + tahun[jumlah_bulan - 1] + "-" + bulan[jumlah_bulan - 1] + "-01' AND '" + tahun[0] + "-" + bulan[0] + "-31'";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                for (int i = 0; i < jumlah_bulan; i++) {
                    if (rs.getInt("bulan") == bulan[i]) {
                        RawMatProcessed[i] = RawMatProcessed[i] + rs.getInt("berat_basah");
                    }
                }
            }

            for (int i = 0; i < jumlah_bulan; i++) {
                model.setValueAt(decimalFormat.format(Math.round(RawMatProcessed[i] / 1000.f)), 0, i + 1);
            }
        } catch (SQLException e) {
            Logger.getLogger(JFrame_KPI_Waleta.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void FinishedGoods() {
        try {
            DefaultTableModel model = (DefaultTableModel) Table_data.getModel();
            decimalFormat.setMaximumFractionDigits(0);
            sql = "SELECT `berat`, MONTH(`tanggal_grading`) AS 'bulan', YEAR(`tanggal_grading`) AS 'tahun' FROM `tb_bahan_jadi_masuk`"
                    + "WHERE `kode_asal` LIKE 'WL%' AND `tanggal_grading` BETWEEN '" + tahun[jumlah_bulan - 1] + "-" + bulan[jumlah_bulan - 1] + "-01' AND '" + tahun[0] + "-" + bulan[0] + "-31'";
            rs = Utility.db.getStatement().executeQuery(sql);
            int[] FinishedGoods = new int[jumlah_bulan];
            while (rs.next()) {
                for (int i = 0; i < jumlah_bulan; i++) {
                    if (rs.getInt("bulan") == bulan[i]) {
                        FinishedGoods[i] = FinishedGoods[i] + rs.getInt("berat");
                    }
                }
            }

            for (int i = 0; i < jumlah_bulan; i++) {
                model.setValueAt(decimalFormat.format(Math.round(FinishedGoods[i] / 1000.f)), 1, i + 1);
            }
        } catch (SQLException e) {
            Logger.getLogger(JFrame_KPI_Waleta.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void JasTip() {
        try {
            DefaultTableModel model = (DefaultTableModel) Table_data.getModel();
            decimalFormat.setMaximumFractionDigits(0);
            sql = "SELECT `tb_box_packing`.`no_box`, `tb_box_bahan_jadi`.`berat`, `tb_pengiriman`.`invoice_no`, `tb_pengiriman`.`jenis_pengiriman`, `tb_pengiriman`.`tanggal_invoice`, MONTH(`tanggal_invoice`) AS 'bulan', YEAR(`tanggal_invoice`) AS 'tahun'\n"
                    + "FROM `tb_box_packing` LEFT JOIN `tb_box_bahan_jadi` ON `tb_box_packing`.`no_box` = `tb_box_bahan_jadi`.`no_box`\n"
                    + "LEFT JOIN `tb_pengiriman` ON `tb_box_packing`.`invoice_pengiriman` = `tb_pengiriman`.`invoice_no`"
                    + "WHERE `tb_pengiriman`.`tanggal_invoice` BETWEEN '" + tahun[jumlah_bulan - 1] + "-" + bulan[jumlah_bulan - 1] + "-01' AND '" + tahun[0] + "-" + bulan[0] + "-31'";
            rs = Utility.db.getStatement().executeQuery(sql);
            int[] sales = new int[jumlah_bulan];

            while (rs.next()) {
                for (int i = 0; i < jumlah_bulan; i++) {
                    if (rs.getInt("bulan") == bulan[i]) {
                        if (rs.getString("jenis_pengiriman").equals("Ekspor JT")) {
                            berat_jastip[i] = berat_jastip[i] + rs.getInt("berat");
                        } else {
                            sales[i] = sales[i] + rs.getInt("berat");
                        }
                    }
                }
            }

            for (int i = 0; i < jumlah_bulan; i++) {
                model.setValueAt(decimalFormat.format(Math.round(berat_jastip[i] / 1000.f)), 2, i + 1);
                model.setValueAt(decimalFormat.format(Math.round(sales[i] / 1000.f)), 3, i + 1);
            }
        } catch (SQLException e) {
            Logger.getLogger(JFrame_KPI_Waleta.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void Rendemen() {
        try {
            DefaultTableModel model = (DefaultTableModel) Table_data.getModel();

            sql = "SELECT `tb_finishing_2`.`no_laporan_produksi`, `tb_laporan_produksi`.`berat_basah`, `tb_laporan_produksi`.`berat_kering`, `berat_fbonus`,  `berat_fnol`, `berat_pecah`, `berat_flat`, `berat_jidun`, `sesekan`, `hancuran`, `rontokan`, `bonggol`, `serabut`, `tambahan_kaki1`, `tambahan_kaki2` \n"
                    + ", MONTH(`tgl_setor_f2`) AS 'bulan', YEAR(`tgl_setor_f2`) AS 'tahun'"
                    + "FROM `tb_finishing_2` "
                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_finishing_2`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`"
                    + "WHERE `tgl_setor_f2` BETWEEN '" + tahun[jumlah_bulan - 1] + "-" + bulan[jumlah_bulan - 1] + "-01' AND '" + tahun[0] + "-" + bulan[0] + "-31'";
            rs = Utility.db.getStatement().executeQuery(sql);
            float[] Berat_basah = new float[jumlah_bulan];
            float[] Berat_kering = new float[jumlah_bulan];
            float[] MK = new float[jumlah_bulan];
            float[] PchFlat = new float[jumlah_bulan];
            float[] JDN = new float[jumlah_bulan];
            float[] BP = new float[jumlah_bulan];
            float[] RendemenSub = new float[jumlah_bulan];
            float[] Berat_LP_SUB = new float[jumlah_bulan];
            while (rs.next()) {
                float utuh = rs.getInt("berat_fbonus") + rs.getInt("berat_fnol");
                float kaki = rs.getInt("tambahan_kaki1") + rs.getInt("tambahan_kaki2");
                float jidun = rs.getInt("berat_jidun");
                float pchFlat = rs.getInt("berat_pecah") + rs.getInt("berat_flat");
                float ByProduct = rs.getInt("sesekan") + rs.getInt("hancuran") + rs.getInt("rontokan") + rs.getInt("bonggol") + rs.getInt("serabut");
                for (int i = 0; i < jumlah_bulan; i++) {
                    if (rs.getInt("bulan") == bulan[i]) {
                        if (rs.getString("no_laporan_produksi").substring(0, 3).equals("WL-")) {
                            if (utuh >= jidun) {
                                MK[i] = MK[i] + (utuh - kaki);
                            } else {
                                JDN[i] = JDN[i] + (jidun - kaki);
                            }
                            PchFlat[i] = PchFlat[i] + pchFlat;
                            BP[i] = BP[i] + ByProduct;
                            Berat_basah[i] = Berat_basah[i] + rs.getInt("berat_basah");
                            Berat_kering[i] = Berat_kering[i] + rs.getInt("berat_kering");
                        } else {
                            RendemenSub[i] = RendemenSub[i] + ((utuh + jidun + pchFlat + ByProduct) - kaki);
                            Berat_LP_SUB[i] = Berat_LP_SUB[i] + rs.getInt("berat_basah");
                        }
                    }
                }
            }

//            int row = 2;
            for (int i = 0; i < jumlah_bulan; i++) {
                float mk = (MK[i] / Berat_basah[i]) * 100.f;
                float flat = (PchFlat[i] / Berat_basah[i]) * 100.f;
                float jdn = (JDN[i] / Berat_basah[i]) * 100.f;
                float bp = (BP[i] / Berat_basah[i]) * 100.f;
                float mk_0 = (MK[i] / Berat_kering[i]) * 100.f;
                float flat_0 = (PchFlat[i] / Berat_kering[i]) * 100.f;
                float jdn_0 = (JDN[i] / Berat_kering[i]) * 100.f;
                float bp_0 = (BP[i] / Berat_kering[i]) * 100.f;
                decimalFormat.setMaximumFractionDigits(0);
                model.setValueAt(decimalFormat.format(mk) + "/" + decimalFormat.format(flat) + "/" + decimalFormat.format(jdn) + "/" + decimalFormat.format(bp), 4, i + 1);
                decimalFormat.setMaximumFractionDigits(2);
                model.setValueAt(decimalFormat.format(mk + flat + jdn + bp) + "%", 5, i + 1);
                if (RendemenSub[i] > 0) {
                    model.setValueAt(decimalFormat.format(RendemenSub[i] / Berat_LP_SUB[i] * 100.f) + "%", 6, i + 1);
                }
                model.setValueAt(decimalFormat.format(mk_0 + flat_0 + jdn_0 + bp_0) + "%", 7, i + 1);
            }
        } catch (SQLException e) {
            Logger.getLogger(JFrame_KPI_Waleta.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void Cost_per_KG() {
        try {
            DefaultTableModel model = (DefaultTableModel) Table_data.getModel();
            decimalFormat.setMaximumFractionDigits(2);
            long[] Biaya = new long[jumlah_bulan];
            long[] Biaya_ekspor = new long[jumlah_bulan];
            long[] KG_ekspor = new long[jumlah_bulan];

            //BIAYA KESELURUHAN
            sql = "SELECT MONTH(`bulan`) AS 'bulan', YEAR(`bulan`) AS 'tahun', `nilai` FROM `tb_biaya_pabrik`"
                    + "WHERE `Kategori1` NOT IN ('Laporan Keuangan') "
                    + "AND `tb_biaya_pabrik`.`bulan` BETWEEN '" + tahun[jumlah_bulan - 1] + "-" + bulan[jumlah_bulan - 1] + "-01' AND '" + tahun[0] + "-" + bulan[0] + "-31'";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                for (int i = 0; i < jumlah_bulan; i++) {
                    if (rs.getInt("bulan") == bulan[i]) {
                        Biaya[i] = Biaya[i] + rs.getLong("nilai");
                    }
                }
            }
            //BIAYA EKSPOR
            sql = "SELECT MONTH(`tgl_invoice`) AS 'bulan', YEAR(`tgl_invoice`) AS 'tahun', (`berat_waleta` + `berat_esta` + `berat_jastip`) AS 'net_weight', (`biaya_admin` + `biaya_coo` + `biaya_sertif` + `biaya_fumigasi` + `biaya_asuransi` + `biaya_cargo`) AS 'eskpor' "
                    + "FROM `tb_biaya_expor` LEFT JOIN `tb_payment_report` ON `tb_biaya_expor`.`invoice_no` = `tb_payment_report`.`invoice`"
                    + "WHERE `biaya_admin`>0 AND `biaya_coo`>0 AND `biaya_sertif`>0 AND `biaya_fumigasi`>0 AND `biaya_asuransi`>0 AND `biaya_cargo`>0 AND "
                    + "`tb_payment_report`.`tgl_invoice` BETWEEN '" + tahun[jumlah_bulan - 1] + "-" + bulan[jumlah_bulan - 1] + "-01' AND '" + tahun[0] + "-" + bulan[0] + "-31'";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                for (int i = 0; i < jumlah_bulan; i++) {
                    if (rs.getInt("bulan") == bulan[i]) {
                        Biaya_ekspor[i] = Biaya_ekspor[i] + rs.getLong("eskpor");
                        KG_ekspor[i] = KG_ekspor[i] + rs.getLong("net_weight");
                    }
                }
            }

            //MARGIN JASTIP
            sql = "SELECT MONTH(`tgl_invoice`) AS 'bulan', YEAR(`tgl_invoice`) AS 'tahun', `value_from_jtp` FROM `tb_payment_report`"
                    + "WHERE `tgl_invoice` BETWEEN '" + tahun[jumlah_bulan - 1] + "-" + bulan[jumlah_bulan - 1] + "-01' AND '" + tahun[0] + "-" + bulan[0] + "-31'";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                for (int i = 0; i < jumlah_bulan; i++) {
                    if (rs.getInt("bulan") == bulan[i]) {
                        margin_jastip[i] = margin_jastip[i] + rs.getLong("value_from_jtp");
                    }
                }
            }

            float ekspor_per_kg_baku = 0;
            for (int i = jumlah_bulan - 1; i > -1; i--) {
                float biaya_per_kg = ((float) Biaya[i] / 1000.f) / (float) RawMatProcessed[i];

                if (Biaya_ekspor[i] > 0 && KG_ekspor[i] > 0) {
                    ekspor_per_kg_baku = (float) Biaya_ekspor[i] / (float) KG_ekspor[i];
                    ekspor_per_kg_baku = ((float) Biaya_ekspor[i] / 1000.f) / ((float) KG_ekspor[i] / 0.825f); //82.5% rendemen
                }

                Cost_KG[i] = biaya_per_kg + ekspor_per_kg_baku;
                model.setValueAt(decimalFormat.format(Cost_KG[i]), 8, i + 1);

//                float jastip_KG = ((float) berat_jastip[i] * 0.8f) / (float) RawMatProcessed[i];
                float jastip_KG = ((float) (margin_jastip[i] * Kurs[i]) / 1000.f) / (float) RawMatProcessed[i];
                model.setValueAt(decimalFormat.format(Math.round((margin_jastip[i] * Kurs[i]) / 1000000.f)), 12, i + 1);
                model.setValueAt(decimalFormat.format(Cost_KG[i] - jastip_KG), 9, i + 1);
            }
        } catch (SQLException e) {
            Logger.getLogger(JFrame_KPI_Waleta.class.getName()).log(Level.SEVERE, null, e);
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
                    model.setValueAt(decimalFormat.format(Kurs[i]), 10, i + 1);
                }
            }
        } catch (SQLException e) {
            Logger.getLogger(JFrame_KPI_Waleta.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void MarginProduksi() {
        try {
            DefaultTableModel model = (DefaultTableModel) Table_data.getModel();
            decimalFormat.setMaximumFractionDigits(0);
            //BIAYA KESELURUHAN
            sql = "SELECT `tb_laporan_produksi`.`no_laporan_produksi`, MONTH(`tanggal_lp`) AS 'bulan', YEAR(`tanggal_lp`) AS 'tahun', `berat_basah`, (`tb_finishing_2`.`tambahan_kaki1` + `tb_finishing_2`.`tambahan_kaki2`) AS 'kaki', `tb_grading_bahan_baku`.`harga_bahanbaku`, SUM(`tb_grading_bahan_jadi`.`gram` * `tb_grade_bahan_jadi`.`harga`) AS 'cny'\n"
                    + "FROM `tb_laporan_produksi` LEFT JOIN `tb_grading_bahan_jadi` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_grading_bahan_jadi`.`kode_asal_bahan_jadi`\n"
                    + "LEFT JOIN `tb_grading_bahan_baku` ON `tb_grading_bahan_baku`.`no_kartu_waleta` = `tb_laporan_produksi`.`no_kartu_waleta` AND `tb_grading_bahan_baku`.`kode_grade` = `tb_laporan_produksi`.`kode_grade`\n"
                    + "LEFT JOIN `tb_finishing_2` ON `tb_finishing_2`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_grading_bahan_jadi`.`grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode`\n"
                    + "WHERE `tanggal_lp` BETWEEN '" + tahun[jumlah_bulan - 1] + "-" + bulan[jumlah_bulan - 1] + "-01' AND '" + tahun[0] + "-" + bulan[0] + "-31'\n"
                    + "GROUP BY `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    + "ORDER BY `tb_laporan_produksi`.`no_laporan_produksi` ASC";
            rs = Utility.db.getStatement().executeQuery(sql);
            double[] Biaya = new double[jumlah_bulan];
            double[] harga_bjd = new double[jumlah_bulan];
            while (rs.next()) {
                for (int i = 0; i < jumlah_bulan; i++) {
                    if (rs.getInt("bulan") == bulan[i]) {
                        if (rs.getFloat("harga_bahanbaku") > 0 && rs.getFloat("cny") > 0) {
                            double harga_baku = rs.getDouble("berat_basah") * rs.getDouble("harga_bahanbaku");
                            double cny = rs.getDouble("cny") / 1000 * 0.98 * Kurs[i];
                            double biaya = Cost_KG[i] * 1000 * rs.getFloat("berat_basah");
                            double kaki = 2 * Kurs[i] * rs.getDouble("kaki"); // asumsi harga kaki 2000 RMB per kG
//                            double margin = cny - (harga_baku + biaya + kaki);
                            Biaya[i] = Biaya[i] + (harga_baku + biaya + kaki);
                            harga_bjd[i] = harga_bjd[i] + cny;
//                            MarginProduksi[i] = MarginProduksi[i] + margin;
                        }
                    }
                }
            }

            for (int i = jumlah_bulan - 1; i > -1; i--) {
                MarginProduksi[i] = harga_bjd[i] - Biaya[i];
                double persen_margin = 0;
                persen_margin = (MarginProduksi[i] / Biaya[i]) * 100.f;
                double Margin = Math.round(MarginProduksi[i] / 1000000.f);
                model.setValueAt(decimalFormat.format(Margin) + "/ " + decimalFormat.format(persen_margin) + "%", 11, i + 1);
                model.setValueAt(decimalFormat.format(Math.round(((margin_jastip[i] * Kurs[i]) + MarginProduksi[i]) / 1000000.f)), 13, i + 1);
            }
        } catch (SQLException e) {
            Logger.getLogger(JFrame_KPI_Waleta.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void Cup_per_day() {
        try {
            DefaultTableModel model = (DefaultTableModel) Table_data.getModel();
            decimalFormat.setMaximumFractionDigits(0);
            sql = "SELECT `no_laporan_produksi`, MONTH(`tanggal_lp`) AS 'bulan', YEAR(`tanggal_lp`) AS 'tahun', `keping_upah`, `berat_basah`, `ruangan` "
                    + "FROM `tb_laporan_produksi` "
                    + "WHERE `tanggal_lp` BETWEEN '" + tahun[jumlah_bulan - 1] + "-" + bulan[jumlah_bulan - 1] + "-01' AND '" + tahun[0] + "-" + bulan[0] + "-31'";
            rs = Utility.db.getStatement().executeQuery(sql);
            float[] Cup_per_day_WLT = new float[jumlah_bulan];
            float[] Cup_per_day_SUB = new float[jumlah_bulan];
            while (rs.next()) {
                for (int i = 0; i < jumlah_bulan; i++) {
                    if (rs.getInt("bulan") == bulan[i]) {
                        float keping = rs.getFloat("keping_upah");
                        if (rs.getInt("keping_upah") == 0) {
                            keping = rs.getFloat("berat_basah") / 8f;
                        }
                        if (rs.getString("ruangan").length() == 5) {
                            Cup_per_day_SUB[i] = Cup_per_day_SUB[i] + keping;
                        } else {
                            Cup_per_day_WLT[i] = Cup_per_day_WLT[i] + keping;
                        }
                    }
                }
            }

            for (int i = 0; i < jumlah_bulan; i++) {
                float jumlah_hari = 0;
                sql = "SELECT COUNT(DISTINCT(`tanggal_cabut`)) AS 'jumlah_hari' FROM `tb_detail_pencabut` "
                        + "WHERE MONTH(`tanggal_cabut`) = '" + bulan[i] + "' AND YEAR(`tanggal_cabut`) = '" + tahun[i] + "' "
                        + "AND `tanggal_cabut` BETWEEN '" + tahun[jumlah_bulan - 1] + "-" + bulan[jumlah_bulan - 1] + "-01' AND '" + tahun[0] + "-" + bulan[0] + "-31'";
                rs = Utility.db.getStatement().executeQuery(sql);
                if (rs.next()) {
                    jumlah_hari = rs.getFloat("jumlah_hari");
                }
                Cup_per_day_WLT[i] = Math.round(Cup_per_day_WLT[i] / jumlah_hari);
                Cup_per_day_SUB[i] = Math.round(Cup_per_day_SUB[i] / jumlah_hari);
                model.setValueAt(decimalFormat.format(Cup_per_day_WLT[i]), 14, i + 1);
                model.setValueAt(decimalFormat.format(Cup_per_day_SUB[i]), 15, i + 1);
            }
        } catch (SQLException e) {
            Logger.getLogger(JFrame_KPI_Waleta.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void PickingPersonel() {
        try {
            DefaultTableModel model = (DefaultTableModel) Table_data.getModel();
            sql = "SELECT MONTH(`scan_date`) AS 'bulan', YEAR(`scan_date`) AS 'tahun', COUNT(DISTINCT(`pin`)) AS 'jumlah_karyawan' "
                    + "FROM `att_log` \n"
                    + "LEFT JOIN `tb_karyawan` ON `att_log`.`pin` = `tb_karyawan`.`pin_finger`\n"
                    + "WHERE `tb_karyawan`.`kode_bagian` IN (SELECT `kode_bagian` FROM `tb_bagian` WHERE `nama_bagian` LIKE '%CABUT-BORONG%'  OR `nama_bagian` LIKE '%CABUT-TRAINING%') "
                    + "AND DATE(`scan_date`) BETWEEN '" + tahun[jumlah_bulan - 1] + "-" + bulan[jumlah_bulan - 1] + "-01' AND '" + tahun[0] + "-" + bulan[0] + "-31'\n"
                    + "GROUP BY DATE(`scan_date`)";
            rs = Utility.db.getStatement().executeQuery(sql);

            int[] PickingPersonel = new int[jumlah_bulan];
            while (rs.next()) {
                for (int i = 0; i < jumlah_bulan; i++) {
                    if (rs.getInt("bulan") == bulan[i]) {
                        if (PickingPersonel[i] < rs.getInt("jumlah_karyawan")) {
                            PickingPersonel[i] = rs.getInt("jumlah_karyawan");
                        }
                    }
                }
            }

            for (int i = 0; i < jumlah_bulan; i++) {
                model.setValueAt(decimalFormat.format(PickingPersonel[i]), 16, i + 1);
            }
        } catch (SQLException e) {
            Logger.getLogger(JFrame_KPI_Waleta.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void Nitrit() {
        try {
            DefaultTableModel model = (DefaultTableModel) Table_data.getModel();
            sql = "SELECT MONTH(`tgl_selesai`) AS 'bulan', YEAR(`tgl_selesai`) AS 'tahun', `nitrit_utuh`, `nitrit_flat`, `jidun`, `status`, `status_akhir` FROM `tb_lab_laporan_produksi`"
                    + "WHERE `tgl_selesai` BETWEEN '" + tahun[jumlah_bulan - 1] + "-" + bulan[jumlah_bulan - 1] + "-01' AND '" + tahun[0] + "-" + bulan[0] + "-31'";
            rs = Utility.db.getStatement().executeQuery(sql);

            float[] NitritPass1 = new float[jumlah_bulan];
            float[] NitritPassAll = new float[jumlah_bulan];
            float[] NitritLevel1_Utuh = new float[jumlah_bulan];
            float[] NitritLevel1_Flat = new float[jumlah_bulan];
            float[] NitritLevel1_Jdn = new float[jumlah_bulan];
            float[] jumlah_data = new float[jumlah_bulan];
            float[] jumlah_utuh = new float[jumlah_bulan];
            float[] jumlah_flat = new float[jumlah_bulan];
            float[] jumlah_jdn = new float[jumlah_bulan];
            while (rs.next()) {
                for (int i = 0; i < jumlah_bulan; i++) {
                    if (rs.getInt("bulan") == bulan[i]) {
                        jumlah_data[i]++;
                        if (rs.getString("status").equals("PASSED")) {
                            NitritPass1[i]++;
                        }
                        if (rs.getString("status_akhir").equals("PASSED")) {
                            NitritPassAll[i]++;
                        }
                        if (rs.getFloat("nitrit_utuh") > 0) {
                            NitritLevel1_Utuh[i] = NitritLevel1_Utuh[i] + rs.getFloat("nitrit_utuh");
                            jumlah_utuh[i]++;
                        }
                        if (rs.getFloat("nitrit_flat") > 0) {
                            NitritLevel1_Flat[i] = NitritLevel1_Flat[i] + rs.getFloat("nitrit_flat");
                            jumlah_flat[i]++;
                        }
                        if (rs.getFloat("jidun") > 0) {
                            NitritLevel1_Jdn[i] = NitritLevel1_Jdn[i] + rs.getFloat("jidun");
                            jumlah_jdn[i]++;
                        }
                    }
                }
            }

            for (int i = 0; i < jumlah_bulan; i++) {
//                System.out.println(i + " jumlah jidun : " + jumlah_jdn[i] + " nitrit : " + NitritLevel1_Jdn[i]);
                NitritPass1[i] = NitritPass1[i] / jumlah_data[i] * 100;
                NitritPassAll[i] = NitritPassAll[i] / jumlah_data[i] * 100;
                NitritLevel1_Utuh[i] = NitritLevel1_Utuh[i] / jumlah_utuh[i];
                NitritLevel1_Flat[i] = NitritLevel1_Flat[i] / jumlah_flat[i];
                NitritLevel1_Jdn[i] = NitritLevel1_Jdn[i] / jumlah_jdn[i];
                decimalFormat.setMaximumFractionDigits(1);
                model.setValueAt(decimalFormat.format(NitritPass1[i]) + "%", 17, i + 1);
                model.setValueAt(decimalFormat.format(NitritPassAll[i]) + "%", 18, i + 1);
                decimalFormat.setMaximumFractionDigits(0);
                model.setValueAt(decimalFormat.format(NitritLevel1_Utuh[i]) + ", "
                        + decimalFormat.format(NitritLevel1_Flat[i]) + ", "
                        + decimalFormat.format(NitritLevel1_Jdn[i]), 19, i + 1);
            }
        } catch (SQLException e) {
            Logger.getLogger(JFrame_KPI_Waleta.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void Kpg_per_AllKaryawan() {
        try {
            DefaultTableModel model = (DefaultTableModel) Table_data.getModel();
            float[] jumlah_karyawan = new float[jumlah_bulan];
            float[] avg_per_day = new float[jumlah_bulan];
            for (int i = 0; i < jumlah_bulan; i++) {
                sql = "SELECT SUM(`jumlah_keping`) / COUNT(DISTINCT(`tanggal_lp`)) AS 'avg_per_day' FROM `tb_laporan_produksi` WHERE `tanggal_lp` BETWEEN '" + tahun[i] + "-" + bulan[i] + "-01' AND '" + tahun[i] + "-" + bulan[i] + "-31' ";
                rs = Utility.db.getStatement().executeQuery(sql);
                if (rs.next()) {
                    avg_per_day[i] = rs.getFloat("avg_per_day");
                }

                sql = "SELECT COUNT(`id_pegawai`) AS 'jumlah_karyawan' "
                        + "FROM `tb_karyawan` "
                        + "WHERE `status` NOT LIKE '%SUB%' "
                        + "AND `tanggal_masuk` < '" + tahun[i] + "-" + bulan[i] + "-31'"
                        + "AND (`tanggal_keluar` > '" + tahun[i] + "-" + bulan[i] + "-31' OR `tanggal_keluar` IS NULL)";
                rs = Utility.db.getStatement().executeQuery(sql);
                if (rs.next()) {
                    jumlah_karyawan[i] = rs.getFloat("jumlah_karyawan");
                }
            }

            decimalFormat.setMaximumFractionDigits(2);
            for (int i = 0; i < jumlah_bulan; i++) {
                model.setValueAt(decimalFormat.format(avg_per_day[i] / jumlah_karyawan[i]), 20, i + 1);
            }
        } catch (SQLException e) {
            Logger.getLogger(JFrame_KPI_Waleta.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        Table_data = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("98% Price, Biaya ekspor / Kg baku dengan rendemen 82.5%");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        Table_data.setFont(new java.awt.Font("Arial Narrow", 0, 24)); // NOI18N
        Table_data.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Raw Mat Processed", null, null, null, null, null, null, null, null, null, null, null, null},
                {"Finished Goods", null, null, null, null, null, null, null, null, null, null, null, null},
                {"JTP", null, null, null, null, null, null, null, null, null, null, null, null},
                {"Sales WLT", null, null, null, null, null, null, null, null, null, null, null, null},
                {"Cup/FL/JDN/BP", null, null, null, null, null, null, null, null, null, null, null, null},
                {"Rendemen WLT", null, null, null, null, null, null, null, null, null, null, null, null},
                {"Rendemen Sub", null, null, null, null, null, null, null, null, null, null, null, null},
                {"Rendemen Esta 0%", null, null, null, null, null, null, null, null, null, null, null, null},
                {"Cost/Kg", null, null, null, null, null, null, null, null, null, null, null, null},
                {"Cost/Kg -JTP", null, null, null, null, null, null, null, null, null, null, null, null},
                {"Kurs", null, null, null, null, null, null, null, null, null, null, null, null},
                {"G. Margin Prod", null, null, null, null, null, null, null, null, null, null, null, null},
                {"G. Margin JTP", null, null, null, null, null, null, null, null, null, null, null, null},
                {"G. Margin TOTAL", null, null, null, null, null, null, null, null, null, null, null, null},
                {"Picking/Day WLT", null, null, null, null, null, null, null, null, null, null, null, null},
                {"Picking/Day SUB", null, null, null, null, null, null, null, null, null, null, null, null},
                {"Picking Personel", null, null, null, null, null, null, null, null, null, null, null, null},
                {"Nitrit Pass 1", null, null, null, null, null, null, null, null, null, null, null, null},
                {"Nitrit Pass All", null, null, null, null, null, null, null, null, null, null, null, null},
                {"Nitrit (Utuh, FL, Jdn)", null, null, null, null, null, null, null, null, null, null, null, null},
                {"Pcs/Day/All Employee", null, null, null, null, null, null, null, null, null, null, null, null}
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
        Table_data.setFocusable(false);
        Table_data.setRowHeight(35);
        Table_data.setRowSelectionAllowed(false);
        Table_data.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(Table_data);
        if (Table_data.getColumnModel().getColumnCount() > 0) {
            Table_data.getColumnModel().getColumn(0).setMinWidth(160);
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
            java.util.logging.Logger.getLogger(JFrame_KPI_Waleta.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JFrame_KPI_Waleta.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JFrame_KPI_Waleta.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JFrame_KPI_Waleta.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JFrame_KPI_Waleta chart = new JFrame_KPI_Waleta();
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
