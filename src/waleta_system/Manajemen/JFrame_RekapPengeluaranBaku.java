package waleta_system.Manajemen;

import java.awt.Color;
import static java.awt.Frame.MAXIMIZED_BOTH;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.LegendItemEntity;
import org.jfree.chart.entity.PieSectionEntity;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;

public class JFrame_RekapPengeluaranBaku extends javax.swing.JFrame {

     
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    String sql = null;
    ResultSet rs;
    Date today = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    public DefaultPieDataset PieChart_dataset_bentuk;
    public DefaultPieDataset PieChart_dataset_bulu;
    public DefaultPieDataset PieChart_dataset_warna;

    public JFrame_RekapPengeluaranBaku() {
        initComponents();
    }

    public void init() {
        try {
            
            PieChart_bentuk();
            PieChart_bulu();
            PieChart_warna();
            decimalFormat.setMaximumFractionDigits(0);
            decimalFormat.setGroupingUsed(true);
            if (Date1.getDate() != null && Date2.getDate() != null) {
                refreshData_bentuk();
                refreshData_bulu();
                refreshData_warna();
            } else {
                JOptionPane.showMessageDialog(this, "Please select fill the date");
            }
        } catch (Exception ex) {
            Logger.getLogger(JFrame_RekapPengeluaranBaku.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void PieChart_bentuk() {
        PieChart_dataset_bentuk = new DefaultPieDataset();
        JFreeChart chart_bentuk = ChartFactory.createPieChart("Pengeluaran Bahan Baku", PieChart_dataset_bentuk, true, true, false);
        chart_bentuk.setBackgroundPaint(Color.WHITE);
        chart_bentuk.getTitle().setPaint(Color.red);
        ChartPanel panelChart = new ChartPanel(chart_bentuk);
        panelChart.setLocation(0, 0);
        panelChart.setSize(panel_chart_bentuk.getWidth(), panel_chart_bentuk.getHeight());
        panel_chart_bentuk.add(panelChart);
        panelChart.addChartMouseListener(new ChartMouseListener() {
            @Override
            public void chartMouseClicked(ChartMouseEvent cme) {
                if (cme.getEntity() != null) {
                    if (!(cme.getEntity() instanceof LegendItemEntity)) {
                        PieSectionEntity entity = (PieSectionEntity) cme.getEntity();
//                        label_detail_chart.setText(entity.getToolTipText());
                    }
                }
            }

            @Override
            public void chartMouseMoved(ChartMouseEvent cme) {

            }
        });
    }

    public void PieChart_bulu() {
        PieChart_dataset_bulu = new DefaultPieDataset();
        JFreeChart chart_bentuk = ChartFactory.createPieChart("Pengeluaran Bahan Baku", PieChart_dataset_bulu, true, true, false);
        chart_bentuk.setBackgroundPaint(Color.WHITE);
        chart_bentuk.getTitle().setPaint(Color.red);
        ChartPanel panelChart = new ChartPanel(chart_bentuk);
        panelChart.setLocation(0, 0);
        panelChart.setSize(panel_chart_bulu.getWidth(), panel_chart_bulu.getHeight());
        panel_chart_bulu.add(panelChart);
        panelChart.addChartMouseListener(new ChartMouseListener() {
            @Override
            public void chartMouseClicked(ChartMouseEvent cme) {
                if (cme.getEntity() != null) {
                    if (!(cme.getEntity() instanceof LegendItemEntity)) {
                        PieSectionEntity entity = (PieSectionEntity) cme.getEntity();
//                        label_detail_chart.setText(entity.getToolTipText());
                    }
                }
            }

            @Override
            public void chartMouseMoved(ChartMouseEvent cme) {

            }
        });
    }

    public void PieChart_warna() {
        PieChart_dataset_warna = new DefaultPieDataset();
        JFreeChart chart_bentuk = ChartFactory.createPieChart("Pengeluaran Bahan Baku", PieChart_dataset_warna, true, true, false);
        chart_bentuk.setBackgroundPaint(Color.WHITE);
        chart_bentuk.getTitle().setPaint(Color.red);
        ChartPanel panelChart = new ChartPanel(chart_bentuk);
        panelChart.setLocation(0, 0);
        panelChart.setSize(panel_chart_warna.getWidth(), panel_chart_warna.getHeight());
        panel_chart_warna.add(panelChart);
        panelChart.addChartMouseListener(new ChartMouseListener() {
            @Override
            public void chartMouseClicked(ChartMouseEvent cme) {
                if (cme.getEntity() != null) {
                    if (!(cme.getEntity() instanceof LegendItemEntity)) {
                        PieSectionEntity entity = (PieSectionEntity) cme.getEntity();
//                        label_detail_chart.setText(entity.getToolTipText());
                    }
                }
            }

            @Override
            public void chartMouseMoved(ChartMouseEvent cme) {

            }
        });
    }

    public void refreshData_bentuk() {
        try {
            int total_gram = 0;
            double total_harga = 0;
            DefaultTableModel model = (DefaultTableModel) Tabel_bentuk.getModel();
            model.setRowCount(0);
            PieChart_dataset_bentuk.clear();
            sql = "SELECT `jenis_bentuk`, COUNT(`no_laporan_produksi`) AS 'lp', SUM(`tb_laporan_produksi`.`jumlah_keping`) AS 'kpg', SUM(`tb_laporan_produksi`.`berat_basah`) AS 'gram', SUM(`harga_bahanbaku` * `berat_basah`) AS 'harga'"
                    + "FROM `tb_laporan_produksi` "
                    + "LEFT JOIN `tb_grade_bahan_baku` ON `tb_laporan_produksi`.`kode_grade` = `tb_grade_bahan_baku`.`kode_grade` "
                    + "LEFT JOIN `tb_grading_bahan_baku` ON `tb_laporan_produksi`.`no_kartu_waleta` = `tb_grading_bahan_baku`.`no_kartu_waleta` AND `tb_laporan_produksi`.`kode_grade` = `tb_grading_bahan_baku`.`kode_grade` "
                    + "WHERE `tanggal_lp` BETWEEN '" + dateFormat.format(Date1.getDate()) + "' AND '" + dateFormat.format(Date2.getDate()) + "' "
                    + "GROUP BY `jenis_bentuk`";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[5];
            while (rs.next()) {
                row[0] = rs.getString("jenis_bentuk");
                row[1] = rs.getFloat("lp");
                row[2] = rs.getFloat("kpg");
                row[3] = rs.getFloat("gram");
                row[4] = Math.round(rs.getDouble("harga"));
                model.addRow(row);
                total_gram = total_gram + rs.getInt("gram");
                total_harga = total_harga + rs.getDouble("harga");
                PieChart_dataset_bentuk.setValue(rs.getString("jenis_bentuk"), Math.round(rs.getFloat("gram")));
            }
            ColumnsAutoSizer.sizeColumnsToFit(Tabel_bentuk);
            label_total_bentuk.setText("Total gram : " + decimalFormat.format(total_gram) + " - Rp. " + decimalFormat.format(total_harga));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JFrame_RekapPengeluaranBaku.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshData_bulu() {
        try {
            int total_gram = 0;
            double total_harga = 0;
            DefaultTableModel model = (DefaultTableModel) Tabel_bulu.getModel();
            model.setRowCount(0);
            PieChart_dataset_bulu.clear();
            sql = "SELECT `jenis_bulu`, COUNT(`no_laporan_produksi`) AS 'lp', SUM(`tb_laporan_produksi`.`jumlah_keping`) AS 'kpg', SUM(`tb_laporan_produksi`.`berat_basah`) AS 'gram', SUM(`harga_bahanbaku` * `berat_basah`) AS 'harga'"
                    + "FROM `tb_laporan_produksi` "
                    + "LEFT JOIN `tb_grade_bahan_baku` ON `tb_laporan_produksi`.`kode_grade` = `tb_grade_bahan_baku`.`kode_grade` "
                    + "LEFT JOIN `tb_grading_bahan_baku` ON `tb_laporan_produksi`.`no_kartu_waleta` = `tb_grading_bahan_baku`.`no_kartu_waleta` AND `tb_laporan_produksi`.`kode_grade` = `tb_grading_bahan_baku`.`kode_grade` "
                    + "WHERE `tanggal_lp` BETWEEN '" + dateFormat.format(Date1.getDate()) + "' AND '" + dateFormat.format(Date2.getDate()) + "' "
                    + "GROUP BY `jenis_bulu`";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[5];
            while (rs.next()) {
                String bulu = "";
                switch (rs.getString("jenis_bulu")) {
                    case "Bulu Ringan":
                        bulu = "BR";
                        break;
                    case "Bulu Ringan Sekali/Bulu Ringan":
                        bulu = "BRS/BR";
                        break;
                    case "Bulu Sedang":
                        bulu = "BS";
                        break;
                    case "Bulu Berat":
                        bulu = "BB";
                        break;
                    case "Bulu Berat Sekali":
                        bulu = "BB2";
                        break;
                    default:
                        bulu = "-";
                        break;
                }
                row[0] = bulu;
                row[1] = rs.getFloat("lp");
                row[2] = rs.getFloat("kpg");
                row[3] = rs.getFloat("gram");
                row[4] = Math.round(rs.getDouble("harga"));
                model.addRow(row);
                total_gram = total_gram + rs.getInt("gram");
                total_harga = total_harga + rs.getDouble("harga");
                PieChart_dataset_bulu.setValue(bulu, Math.round(rs.getFloat("gram")));
            }
            ColumnsAutoSizer.sizeColumnsToFit(Tabel_bulu);
            label_total_bulu.setText("Total gram : " + decimalFormat.format(total_gram) + " - Rp. " + decimalFormat.format(total_harga));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JFrame_RekapPengeluaranBaku.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshData_warna() {
        try {
            int total_gram = 0;
            double total_harga = 0;
            DefaultTableModel model = (DefaultTableModel) Tabel_warna.getModel();
            model.setRowCount(0);
            PieChart_dataset_warna.clear();
            sql = "SELECT `jenis_warna`, COUNT(`no_laporan_produksi`) AS 'lp', SUM(`tb_laporan_produksi`.`jumlah_keping`) AS 'kpg', SUM(`tb_laporan_produksi`.`berat_basah`) AS 'gram', SUM(`harga_bahanbaku` * `berat_basah`) AS 'harga'"
                    + "FROM `tb_laporan_produksi` "
                    + "LEFT JOIN `tb_grade_bahan_baku` ON `tb_laporan_produksi`.`kode_grade` = `tb_grade_bahan_baku`.`kode_grade` "
                    + "LEFT JOIN `tb_grading_bahan_baku` ON `tb_laporan_produksi`.`no_kartu_waleta` = `tb_grading_bahan_baku`.`no_kartu_waleta` AND `tb_laporan_produksi`.`kode_grade` = `tb_grading_bahan_baku`.`kode_grade` "
                    + "WHERE `tanggal_lp` BETWEEN '" + dateFormat.format(Date1.getDate()) + "' AND '" + dateFormat.format(Date2.getDate()) + "' "
                    + "GROUP BY `jenis_warna`";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[5];
            while (rs.next()) {
                row[0] = rs.getString("jenis_warna");
                row[1] = rs.getFloat("lp");
                row[2] = rs.getFloat("kpg");
                row[3] = rs.getFloat("gram");
                row[4] = Math.round(rs.getDouble("harga"));
                model.addRow(row);
                total_gram = total_gram + rs.getInt("gram");
                total_harga = total_harga + rs.getDouble("harga");
                PieChart_dataset_warna.setValue(rs.getString("jenis_warna"), Math.round(rs.getFloat("gram")));
            }
            ColumnsAutoSizer.sizeColumnsToFit(Tabel_warna);
            label_total_warna.setText("Total gram : " + decimalFormat.format(total_gram) + " - Rp. " + decimalFormat.format(total_harga));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JFrame_RekapPengeluaranBaku.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        Tabel_bentuk = new javax.swing.JTable();
        label_total_bentuk = new javax.swing.JLabel();
        panel_chart_bentuk = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        Tabel_bulu = new javax.swing.JTable();
        label_total_bulu = new javax.swing.JLabel();
        panel_chart_bulu = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        Tabel_warna = new javax.swing.JTable();
        label_total_warna = new javax.swing.JLabel();
        panel_chart_warna = new javax.swing.JPanel();
        Date1 = new com.toedter.calendar.JDateChooser();
        jLabel1 = new javax.swing.JLabel();
        Date2 = new com.toedter.calendar.JDateChooser();
        Button_refresh = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel3.setText("Raw Material Processed Summary");

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Jenis Bentuk", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 12))); // NOI18N

        Tabel_bentuk.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        Tabel_bentuk.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Bentuk", "Tot LP", "Kpg", "Gram", "Harga"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Double.class
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
        Tabel_bentuk.setRowHeight(20);
        Tabel_bentuk.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(Tabel_bentuk);

        label_total_bentuk.setBackground(new java.awt.Color(255, 255, 255));
        label_total_bentuk.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_total_bentuk.setText("TOTAL");

        javax.swing.GroupLayout panel_chart_bentukLayout = new javax.swing.GroupLayout(panel_chart_bentuk);
        panel_chart_bentuk.setLayout(panel_chart_bentukLayout);
        panel_chart_bentukLayout.setHorizontalGroup(
            panel_chart_bentukLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panel_chart_bentukLayout.setVerticalGroup(
            panel_chart_bentukLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 381, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 373, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(label_total_bentuk)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(panel_chart_bentuk, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(label_total_bentuk)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel_chart_bentuk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(37, Short.MAX_VALUE))
        );

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Jenis Bulu", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 12))); // NOI18N

        Tabel_bulu.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        Tabel_bulu.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Bentuk", "Tot LP", "Kpg", "Gram", "Harga"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Double.class
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
        Tabel_bulu.setRowHeight(20);
        Tabel_bulu.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(Tabel_bulu);

        label_total_bulu.setBackground(new java.awt.Color(255, 255, 255));
        label_total_bulu.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_total_bulu.setText("TOTAL");

        javax.swing.GroupLayout panel_chart_buluLayout = new javax.swing.GroupLayout(panel_chart_bulu);
        panel_chart_bulu.setLayout(panel_chart_buluLayout);
        panel_chart_buluLayout.setHorizontalGroup(
            panel_chart_buluLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panel_chart_buluLayout.setVerticalGroup(
            panel_chart_buluLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 381, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 373, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(label_total_bulu)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(panel_chart_bulu, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(label_total_bulu)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel_chart_bulu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Jenis Warna", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 12))); // NOI18N

        Tabel_warna.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        Tabel_warna.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Bentuk", "Tot LP", "Kpg", "Gram", "Harga"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Double.class
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
        Tabel_warna.setRowHeight(20);
        Tabel_warna.getTableHeader().setReorderingAllowed(false);
        jScrollPane5.setViewportView(Tabel_warna);

        label_total_warna.setBackground(new java.awt.Color(255, 255, 255));
        label_total_warna.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_total_warna.setText("TOTAL");

        javax.swing.GroupLayout panel_chart_warnaLayout = new javax.swing.GroupLayout(panel_chart_warna);
        panel_chart_warna.setLayout(panel_chart_warnaLayout);
        panel_chart_warnaLayout.setHorizontalGroup(
            panel_chart_warnaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panel_chart_warnaLayout.setVerticalGroup(
            panel_chart_warnaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 381, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 373, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(label_total_warna)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(panel_chart_warna, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(label_total_warna)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel_chart_warna, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        Date1.setBackground(new java.awt.Color(255, 255, 255));
        Date1.setDate(new Date(new Date().getTime()-(7 * 24 * 60 * 60 * 1000)));
        Date1.setDateFormatString("dd MMMM yyyy");
        Date1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel1.setText("-");

        Date2.setBackground(new java.awt.Color(255, 255, 255));
        Date2.setDate(new Date());
        Date2.setDateFormatString("dd MMMM yyyy");
        Date2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        Button_refresh.setBackground(new java.awt.Color(255, 255, 255));
        Button_refresh.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Button_refresh.setText("Refresh");
        Button_refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_refreshActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(Date1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Button_refresh)))
                .addContainerGap(135, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(Date1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Button_refresh, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

    private void Button_refreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_refreshActionPerformed
        // TODO add your handling code here:
        if (Date1.getDate() != null && Date2.getDate() != null) {
            refreshData_bentuk();
            refreshData_bulu();
            refreshData_warna();
        } else {
            JOptionPane.showMessageDialog(this, "Please select fill the date");
        }
    }//GEN-LAST:event_Button_refreshActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(JFrame_RekapPengeluaranBaku.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JFrame_RekapPengeluaranBaku.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JFrame_RekapPengeluaranBaku.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JFrame_RekapPengeluaranBaku.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JFrame_RekapPengeluaranBaku Stok = new JFrame_RekapPengeluaranBaku();
                Stok.setVisible(true);
                Stok.setLocationRelativeTo(null);
                Stok.setExtendedState(MAXIMIZED_BOTH);
                Stok.init();
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Button_refresh;
    private com.toedter.calendar.JDateChooser Date1;
    private com.toedter.calendar.JDateChooser Date2;
    private javax.swing.JTable Tabel_bentuk;
    private javax.swing.JTable Tabel_bulu;
    private javax.swing.JTable Tabel_warna;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JLabel label_total_bentuk;
    private javax.swing.JLabel label_total_bulu;
    private javax.swing.JLabel label_total_warna;
    private javax.swing.JPanel panel_chart_bentuk;
    private javax.swing.JPanel panel_chart_bulu;
    private javax.swing.JPanel panel_chart_warna;
    // End of variables declaration//GEN-END:variables
}
