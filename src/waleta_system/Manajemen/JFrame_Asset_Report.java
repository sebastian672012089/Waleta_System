package waleta_system.Manajemen;

import java.awt.Font;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;

public class JFrame_Asset_Report extends javax.swing.JFrame {

    String sql = null;
    ResultSet rs;
    PreparedStatement pst;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    int jumlah_bulan = 4;
    int[] bulan, tahun;
    int[] CNY_IDR, USD_IDR;
    double[] NilaiGBM, NilaiWIP, NilaiGBJ, AR, AP, CashOnBank, Asset, TOTAL_ASSET;

    public JFrame_Asset_Report() {
        initComponents();
    }

    public void init() {
        refresh_JAM();
        decimalFormat.setGroupingUsed(true);
        decimalFormat.setMaximumFractionDigits(0);
        bulan = new int[jumlah_bulan];
        tahun = new int[jumlah_bulan];

        CNY_IDR = new int[jumlah_bulan];
        USD_IDR = new int[jumlah_bulan];
        NilaiGBM = new double[jumlah_bulan];
        NilaiWIP = new double[jumlah_bulan];
        NilaiGBJ = new double[jumlah_bulan];
        AR = new double[jumlah_bulan];
        AP = new double[jumlah_bulan];
        CashOnBank = new double[jumlah_bulan];
        Asset = new double[jumlah_bulan];
        TOTAL_ASSET = new double[jumlah_bulan];

        bulan[0] = Integer.valueOf(new SimpleDateFormat("MM").format(new Date()));
        tahun[0] = Integer.valueOf(new SimpleDateFormat("yyyy").format(new Date()));
        for (int x = 1; x < jumlah_bulan; x++) {
            bulan[x] = bulan[0] - x;
            tahun[x] = tahun[0];
            if (bulan[x] < 1) {
                bulan[x] = bulan[x] + 12;
                tahun[x] = tahun[x] - 1;
            }
        }

        JTableHeader TableHeader = jTable_Asset.getTableHeader();
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

        jTable_Asset.getTableHeader().setFont(new Font("Arial Narrow", Font.BOLD, 18));
        refresh_table();
    }

    public void refresh_table() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Kurs();
                StokTerkirim();
                StokGBM();
                StokWIP();
                StokGBJ();
                ARAP();
                Cash();
                Asset();
                LongTermLiability();
                Total();
                ColumnsAutoSizer.sizeColumnsToFit(jTable_Asset);
                JOptionPane.showMessageDialog(null, "Proses Selesai");
            }
        };
        thread.start();
    }

    public void refresh_JAM() {
        LocalDateTime myDateObj = LocalDateTime.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm:ss");

        String formattedDate = myDateObj.format(myFormatObj);
        label_waktu.setText(formattedDate);
    }

    public void StokTerkirim() {
        try {
            DefaultTableModel model = (DefaultTableModel) jTable_Asset.getModel();
            sql = "SELECT (`berat` * `harga_gram`) AS 'harga', `status` FROM `tb_pembelian_bahan_baku` WHERE `status` IN ('OTW', 'PROSES')";
            rs = Utility.db.getStatement().executeQuery(sql);
            double otw = 0, proses = 0;
            while (rs.next()) {
                if (rs.getString("status").equals("OTW")) {
                    otw = otw + rs.getDouble("harga");
                } else if (rs.getString("status").equals("PROSES")) {
                    proses = proses + rs.getDouble("harga");
                }
            }
            model.setValueAt(Math.round(otw / 1000000.f), 0, 1);
            model.setValueAt(Math.round(proses / 1000000.f), 1, 1);
        } catch (SQLException ex) {
            Logger.getLogger(JFrame_Asset_Report.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void StokGBM() {
        try {
            DefaultTableModel model = (DefaultTableModel) jTable_Asset.getModel();
            for (int i = 0; i < jumlah_bulan; i++) {
                double stok = 0;
                sql = "SELECT SUM(`tb_grading_bahan_baku`.`total_berat` * `harga_bahanbaku`) AS 'Berat_real' FROM `tb_bahan_baku_masuk` \n"
                        + "LEFT JOIN `tb_grading_bahan_baku` ON `tb_bahan_baku_masuk`.`no_kartu_waleta` = `tb_grading_bahan_baku`.`no_kartu_waleta` \n"
                        + "WHERE `tgl_masuk` <= '" + tahun[i] + "-" + bulan[i] + "-31' AND `tgl_masuk` <= CURRENT_DATE ";
                rs = Utility.db.getStatement().executeQuery(sql);
                if (rs.next()) {
                    stok = rs.getDouble("Berat_real");
                }
                //NILAI LP
                sql = "SELECT SUM(`berat_basah` * `tb_grading_bahan_baku`.`harga_bahanbaku`) AS 'nilaiLP' FROM `tb_laporan_produksi` "
                        + "LEFT JOIN `tb_grading_bahan_baku` ON `tb_laporan_produksi`.`no_kartu_waleta` = `tb_grading_bahan_baku`.`no_kartu_waleta` AND `tb_laporan_produksi`.`kode_grade` = `tb_grading_bahan_baku`.`kode_grade`"
                        + "WHERE `tanggal_lp` <= '" + tahun[i] + "-" + bulan[i] + "-31' AND `tanggal_lp` <= CURRENT_DATE";
                rs = Utility.db.getStatement().executeQuery(sql);
                if (rs.next()) {
                    stok = stok - rs.getDouble("nilaiLP");
                }
                //NILAI KELUAR
                sql = "SELECT SUM(`total_berat_keluar` * `tb_grading_bahan_baku`.`harga_bahanbaku`) AS 'nilaiLP' FROM `tb_bahan_baku_keluar` \n"
                        + "LEFT JOIN `tb_grading_bahan_baku` ON `tb_bahan_baku_keluar`.`no_kartu_waleta` = `tb_grading_bahan_baku`.`no_kartu_waleta` AND `tb_bahan_baku_keluar`.`kode_grade` = `tb_grading_bahan_baku`.`kode_grade`\n"
                        + "LEFT JOIN `tb_bahan_baku_keluar1` ON `tb_bahan_baku_keluar`.`kode_pengeluaran` = `tb_bahan_baku_keluar1`.`kode_pengeluaran`"
                        + "WHERE `tgl_keluar` <= '" + tahun[i] + "-" + bulan[i] + "-31' AND `tgl_keluar` <= CURRENT_DATE";
                rs = Utility.db.getStatement().executeQuery(sql);
                if (rs.next()) {
                    stok = stok - rs.getDouble("nilaiLP");
                }
                //NILAI CMP
                sql = "SELECT SUM(`gram` * `tb_grading_bahan_baku`.`harga_bahanbaku`) AS 'nilaiLP' FROM `tb_kartu_cmp_detail` \n"
                        + "LEFT JOIN `tb_grading_bahan_baku` ON `tb_kartu_cmp_detail`.`no_grading` = `tb_grading_bahan_baku`.`no_grading`\n"
                        + "LEFT JOIN `tb_kartu_cmp` ON `tb_kartu_cmp_detail`.`kode_kartu_cmp` = `tb_kartu_cmp`.`kode_kartu_cmp`"
                        + "WHERE `tanggal` <= '" + tahun[i] + "-" + bulan[i] + "-31' AND `tanggal` <= CURRENT_DATE";
                rs = Utility.db.getStatement().executeQuery(sql);
                if (rs.next()) {
                    stok = stok - rs.getDouble("nilaiLP");
                }
                NilaiGBM[i] = stok;
                model.setValueAt(Math.round(NilaiGBM[i] / 1000000.f), 2, i + 1);
            }
        } catch (SQLException e) {
            Logger.getLogger(JFrame_Asset_Report.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void StokWIP() {
        try {
            DefaultTableModel model = (DefaultTableModel) jTable_Asset.getModel();
            for (int i = 0; i < jumlah_bulan; i++) {
                sql = "SELECT SUM(`berat_basah` * `tb_grading_bahan_baku`.`harga_bahanbaku`) AS 'nilai_WIP' FROM `tb_laporan_produksi` "
                        + "LEFT JOIN `tb_grading_bahan_baku` ON `tb_laporan_produksi`.`no_kartu_waleta` = `tb_grading_bahan_baku`.`no_kartu_waleta` AND `tb_laporan_produksi`.`kode_grade` = `tb_grading_bahan_baku`.`kode_grade`"
                        + "LEFT JOIN `tb_bahan_jadi_masuk` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_bahan_jadi_masuk`.`kode_asal` "
                        + "LEFT JOIN `tb_tutupan_grading` ON `tb_bahan_jadi_masuk`.`kode_tutupan` = `tb_tutupan_grading`.`kode_tutupan` "
                        + "WHERE `tanggal_lp` <= '" + tahun[i] + "-" + bulan[i] + "-31'  AND `tanggal_lp` <= CURRENT_DATE "
                        + "AND (`tb_tutupan_grading`.`status_box` = 'PROSES' OR `tb_tutupan_grading`.`status_box` IS NULL OR `tgl_statusBox` >= '" + tahun[i] + "-" + bulan[i] + "-31')";
                rs = Utility.db.getStatement().executeQuery(sql);
                if (rs.next()) {
                    NilaiWIP[i] = rs.getDouble("nilai_WIP");
                }
                model.setValueAt(Math.round(NilaiWIP[i] / 1000000.f), 3, i + 1);
            }
        } catch (SQLException e) {
            Logger.getLogger(JFrame_Asset_Report.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void StokGBJ() {
        try {
            DefaultTableModel model = (DefaultTableModel) jTable_Asset.getModel();
            double GBJ = 0;
            sql = "SELECT SUM((`tb_box_bahan_jadi`.`berat`/1000) * `tb_grade_bahan_jadi`.`harga`) AS 'NilaiGBJ' "
                    + "FROM `tb_box_bahan_jadi` "
                    + "LEFT JOIN `tb_box_packing` ON `tb_box_bahan_jadi`.`no_box` = `tb_box_packing`.`no_box` "
                    + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_grade_bahan_jadi`.`kode` = `tb_box_bahan_jadi`.`kode_grade_bahan_jadi` "
                    + "LEFT JOIN `tb_tutupan_grading` ON `tb_box_bahan_jadi`.`no_tutupan` = `tb_tutupan_grading`.`kode_tutupan`"
                    + "WHERE `lokasi_terakhir` IN ('GRADING', 'PACKING', 'RE-PROSES', 'TREATMENT', 'DIPINJAM')  AND (`tb_box_packing`.`status` IN ('PENDING', 'STOK', 'RETUR') OR `tb_box_packing`.`status` IS NULL)"
                    + "AND (`tb_tutupan_grading`.`status_box` = 'SELESAI' OR `tb_tutupan_grading`.`status_box` IS NULL)";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                GBJ = rs.getDouble("NilaiGBJ");
            }
//            System.out.println(GBJ);
            for (int i = 0; i < jumlah_bulan; i++) {
                NilaiGBJ[i] = GBJ * CNY_IDR[i];
                model.setValueAt(Math.round(NilaiGBJ[i] / 1000000.f), 4, i + 1);
            }
        } catch (SQLException e) {
            Logger.getLogger(JFrame_Asset_Report.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void ARAP() {
        try {
            DefaultTableModel model = (DefaultTableModel) jTable_Asset.getModel();
            for (int i = 0; i < jumlah_bulan; i++) {
                double AR_CNY = 0, AR_USD = 0, AP_CNY = 0;
                sql = "SELECT `currency`, `value_waleta`, `value_esta`, `value_from_jtp`, `value_to_jtp`, "
                        + "IF(`date_payment1` <= '" + tahun[i] + "-" + bulan[i] + "-31', `payment1`, 0) AS 'payment1', IF(`date_payment2` <= '" + tahun[i] + "-" + bulan[i] + "-31', `payment2`, 0) AS 'payment2', "
                        + "IF(`date_transfer_jastip1` <= '" + tahun[i] + "-" + bulan[i] + "-31', `transfer_jastip1`, 0) AS 'transfer_jastip1', IF(`date_transfer_jastip2` <= '" + tahun[i] + "-" + bulan[i] + "-31', `transfer_jastip2`, 0) AS 'transfer_jastip2' "
                        + "FROM `tb_payment_report` LEFT JOIN `tb_buyer` ON `tb_payment_report`.`buyer` = `tb_buyer`.`kode_buyer`"
                        + "WHERE `tb_buyer`.`kategori` IN ('Ekspor', 'Other') AND `awb_date` <= '" + tahun[i] + "-" + bulan[i] + "-31'";
                rs = Utility.db.getStatement().executeQuery(sql);
                while (rs.next()) {
                    if (rs.getString("currency").equals("CNY")) {
                        double A = ((rs.getDouble("value_waleta") + rs.getDouble("value_from_jtp") + rs.getDouble("value_to_jtp")) - (rs.getDouble("payment1") + rs.getDouble("payment2")));
                        AP_CNY = AP_CNY + (rs.getDouble("value_to_jtp") - (rs.getDouble("transfer_jastip1") + rs.getDouble("transfer_jastip2")));
                        if (A > 0) {
                            AR_CNY = AR_CNY + A;
                        }
                    } else if (rs.getString("currency").equals("USD")) {
                        AR_USD = AR_USD + ((rs.getDouble("value_waleta") + rs.getDouble("value_from_jtp") + rs.getDouble("value_to_jtp")) - (rs.getDouble("payment1") + rs.getDouble("payment2")));
                    }
                }
                AR[i] = (AR_CNY * CNY_IDR[i]) + (AR_USD * USD_IDR[i]);
                AP[i] = AP_CNY * CNY_IDR[i];
                model.setValueAt(Math.round(AR[i] / 1000000.f), 5, i + 1);
                model.setValueAt(Math.round(AP[i] / 1000000.f), 6, i + 1);
            }
        } catch (SQLException e) {
            Logger.getLogger(JFrame_Asset_Report.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void Cash() {
        try {
            DefaultTableModel model = (DefaultTableModel) jTable_Asset.getModel();
            for (int i = 0; i < jumlah_bulan; i++) {
                double Cash_CNY = 0, Cash_USD = 0, Cash_IDR = 0;
                sql = "SELECT MAX(`waktu_input`), `currency`, `nominal` FROM `tb_cash_on_bank` "
                        + "WHERE DATE(`waktu_input`) <= '" + tahun[i] + "-" + bulan[i] + "-31' GROUP BY `currency`";
                rs = Utility.db.getStatement().executeQuery(sql);
                while (rs.next()) {
                    if (rs.getString("currency").contains("CNY")) {
                        Cash_CNY = Cash_CNY + rs.getDouble("nominal");
                    } else if (rs.getString("currency").contains("USD")) {
                        Cash_USD = Cash_USD + rs.getDouble("nominal");
                    } else if (rs.getString("currency").contains("IDR")) {
                        Cash_IDR = Cash_IDR + rs.getDouble("nominal");
                    }
                }
                CashOnBank[i] = (Cash_CNY * CNY_IDR[i]) + (Cash_USD * USD_IDR[i]) + Cash_IDR;
                model.setValueAt(Math.round(CashOnBank[i] / 1000000.f), 7, i + 1);
            }
        } catch (SQLException e) {
            Logger.getLogger(JFrame_Asset_Report.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void Asset() {
        try {
            DefaultTableModel model = (DefaultTableModel) jTable_Asset.getModel();
            for (int i = 0; i < jumlah_bulan; i++) {
                sql = "SELECT SUM(`nilai`) AS 'nilai' \n"
                        + "FROM `tb_neraca` WHERE `bulan` <= '" + tahun[i] + "-" + bulan[i] + "-31' AND `kategori` LIKE '%Aktiva Tetap%'\n"
                        + "GROUP BY `bulan` ORDER BY `bulan` DESC LIMIT 1";
                rs = Utility.db.getStatement().executeQuery(sql);
                if (rs.next()) {
                    Asset[i] = rs.getDouble("nilai");
                }
                model.setValueAt(Math.round(Asset[i] / 1000000.f), 8, i + 1);
            }
        } catch (SQLException e) {
            Logger.getLogger(JFrame_Asset_Report.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void LongTermLiability() {
        DefaultTableModel model = (DefaultTableModel) jTable_Asset.getModel();
        for (int i = 0; i < jumlah_bulan; i++) {
            model.setValueAt(0, 9, i + 1);
        }
    }

    public void Kurs() {
        try {
            DefaultTableModel model = (DefaultTableModel) jTable_Asset.getModel();
            sql = "SELECT `nilai`, MONTH(`tanggal`) AS 'bulan', YEAR(`tanggal`) AS 'tahun' FROM `tb_kurs`"
                    + "WHERE `tanggal` BETWEEN '" + tahun[jumlah_bulan - 1] + "-" + bulan[jumlah_bulan - 1] + "-01' AND '" + tahun[0] + "-" + bulan[0] + "-31'";
            rs = Utility.db.getStatement().executeQuery(sql);
            int[] count = new int[jumlah_bulan];
            while (rs.next()) {
                for (int i = 0; i < jumlah_bulan; i++) {
                    if (rs.getInt("bulan") == bulan[i]) {
                        CNY_IDR[i] = CNY_IDR[i] + rs.getInt("nilai");
                        count[i]++;
                    }
                }
            }

            for (int i = 0; i < jumlah_bulan; i++) {
                if (count[i] > 0) {
                    CNY_IDR[i] = CNY_IDR[i] / count[i];
                    model.setValueAt(CNY_IDR[i], 10, i + 1);
                }
                USD_IDR[i] = 14880;
                model.setValueAt(USD_IDR[i], 11, i + 1);
            }
        } catch (SQLException e) {
            Logger.getLogger(JFrame_Asset_Report.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void Total() {
        DefaultTableModel model = (DefaultTableModel) jTable_Asset.getModel();
//        double AVG = 0;
        for (int i = 0; i < jumlah_bulan; i++) {
            TOTAL_ASSET[i] = NilaiGBM[i] + NilaiWIP[i] + NilaiGBJ[i] + AR[i] + CashOnBank[i] + Asset[i] - AP[i];
            model.setValueAt(Math.round(TOTAL_ASSET[i] / 1000000.f), 12, i + 1);
//            AVG = AVG + TOTAL_ASSET[i];
        }
//        AVG = AVG / jumlah_bulan;
//        label_avg_asset.setText("Rp. " + decimalFormat.format(Math.round(AVG / 1000000.f)) + " Juta");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable_Asset = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable_ARAP_lokal = new javax.swing.JTable();
        label_waktu = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel1.setText("ASSET Report");

        jTable_Asset.setAutoCreateRowSorter(true);
        jTable_Asset.setFont(new java.awt.Font("Arial Narrow", 0, 24)); // NOI18N
        jTable_Asset.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Raw Materials on Way", null, null, null, null},
                {"Raw Materials on Grading", null, null, null, null},
                {"Stock GBM", null, null, null, null},
                {"Stock WIP", null, null, null, null},
                {"Stock GBJ", null, null, null, null},
                {"Acc Receivable", null, null, null, null},
                {"Acc Payable", null, null, null, null},
                {"Cash on Bank", null, null, null, null},
                {"Fixed Asset", null, null, null, null},
                {"Longterm Liability", null, null, null, null},
                {"Kurs CNY/IDR", null, null, null, null},
                {"Kurs USD/IDR", null, null, null, null},
                {"Total Assets", null, null, null, null}
            },
            new String [] {
                "Deskripsi", "-", "-", "-", "-"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable_Asset.setRowHeight(30);
        jTable_Asset.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jTable_Asset);
        if (jTable_Asset.getColumnModel().getColumnCount() > 0) {
            jTable_Asset.getColumnModel().getColumn(1).setMinWidth(90);
            jTable_Asset.getColumnModel().getColumn(2).setMinWidth(90);
            jTable_Asset.getColumnModel().getColumn(3).setMinWidth(90);
            jTable_Asset.getColumnModel().getColumn(4).setMinWidth(90);
        }

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel3.setText("Other");

        jTable_ARAP_lokal.setAutoCreateRowSorter(true);
        jTable_ARAP_lokal.setFont(new java.awt.Font("Arial Narrow", 0, 18)); // NOI18N
        jTable_ARAP_lokal.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Balance", null, null, null, null, null, null, null},
                {"IN - Pelunasan", null, null, null, null, null, null, null},
                {"IN - Margin JTP", null, null, null, null, null, null, null},
                {"IN - DP Penjualan", null, null, null, null, null, null, null},
                {"IN - Lain2", null, null, null, null, null, null, null},
                {"OUT - Non Baku", null, null, null, null, null, null, null},
                {"OUT - Baku", null, null, null, null, null, null, null},
                {"Ending Balance", null, null, null, null, null, null, null}
            },
            new String [] {
                "-", "Bulan 1", "Bulan 2", "Bulan 3", "Bulan 4", "Bulan 5", "Bulan 6", "Bulan 7"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
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
        jTable_ARAP_lokal.setRowHeight(23);
        jTable_ARAP_lokal.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(jTable_ARAP_lokal);

        label_waktu.setBackground(new java.awt.Color(255, 255, 255));
        label_waktu.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_waktu.setText("(TIME)");

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel8.setText("*) Data diatas adalah data perhitungan setiap akhir bulan. (data dalam satuan Juta Rupiah)");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(18, 18, 18)
                                .addComponent(label_waktu))
                            .addComponent(jLabel8))
                        .addGap(0, 847, Short.MAX_VALUE))
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(label_waktu))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 411, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
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
            java.util.logging.Logger.getLogger(JFrame_Asset_Report.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JFrame_Asset_Report.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JFrame_Asset_Report.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JFrame_Asset_Report.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame_Asset_Report chart = new JFrame_Asset_Report();
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
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable_ARAP_lokal;
    private javax.swing.JTable jTable_Asset;
    private javax.swing.JLabel label_waktu;
    // End of variables declaration//GEN-END:variables
}
