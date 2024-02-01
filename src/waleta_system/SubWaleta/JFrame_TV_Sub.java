package waleta_system.SubWaleta;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Paint;
import java.awt.SystemColor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.LegendItemEntity;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;

public class JFrame_TV_Sub extends javax.swing.JFrame {

    Date today = new Date();
    GraphicsDevice device = GraphicsEnvironment
            .getLocalGraphicsEnvironment().getScreenDevices()[0];

    String sql = null;
    ResultSet rs;
    PreparedStatement pst;
    Date date = new Date();//date berisikan tanggal hari ini
    JFileChooser chooser = new JFileChooser();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    public DefaultCategoryDataset dataset1;
    JFreeChart chart1;

    public JFrame_TV_Sub() {
        initComponents();
        try {
            Utility.db_sub.connect();
            int year = Year.now().getValue();
            DateChooser1.setDate(dateFormat.parse(year + "-01-01"));
            DateChooser2.setDate(new Date());
        } catch (Exception ex) {
            Logger.getLogger(JFrame_TV_Sub.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void init() {
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        chart();
        refreshTable_kpi();
        Table_kpi_sub.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
    }

    public void chart() {
        dataset1 = new DefaultCategoryDataset();
        chart1 = ChartFactory.createBarChart("Jumlah PRODUKSI Sub", "", "", dataset1, PlotOrientation.VERTICAL, true, true, false);
        chart1.setBackgroundPaint(Color.WHITE);
        chart1.getTitle().setPaint(Color.RED);
        CategoryPlot cp4 = (CategoryPlot) chart1.getPlot();
        cp4.setBackgroundPaint(SystemColor.inactiveCaption);
        cp4.setRenderer(new BarRenderer() {
            @Override
            public Paint getItemPaint(final int row, final int column) {
                float max = 0;
                Color a = new Color(0, 0, 0);
                for (int i = 0; i < dataset1.getRowCount(); i++) {
                    for (int j = 0; j < dataset1.getColumnCount(); j++) {
                        if (dataset1.getValue(i, j).floatValue() > max) {
                            max = dataset1.getValue(i, j).floatValue();
                        }
                    }
                }
                switch (column) {
                    case 0: //CUCI
                        a = new Color(220, 220, 220);
                        break;
                    case 1://CABUT
                        a = new Color(220, 220, 220);
                        break;
                    case 2://CETAK
                        a = new Color(220, 220, 220);
                        break;
                    default:
                        a = new Color(220, 220, 220);
                        break;
                }

                return a;
            }
        });
        ((BarRenderer) cp4.getRenderer()).setBarPainter(new StandardBarPainter());
        BarRenderer renderer = (BarRenderer) chart1.getCategoryPlot().getRenderer();
        renderer.setDrawBarOutline(true);
        renderer.setShadowVisible(false);
        ChartPanel panelChartProgressLP = new ChartPanel(chart1);
        panelChartProgressLP.setLocation(0, 0);
        panelChartProgressLP.setSize(Panel_chart1.getSize());
        Panel_chart1.add(panelChartProgressLP);
        panelChartProgressLP.addChartMouseListener(new ChartMouseListener() {
            @Override
            public void chartMouseClicked(ChartMouseEvent cme) {
                if (cme.getEntity() != null) {
                    if (!(cme.getEntity() instanceof LegendItemEntity)) {

                    }
                }
            }

            @Override
            public void chartMouseMoved(ChartMouseEvent cme) {

            }
        });
    }

    public void refreshTable_kpi() {
        Utility.db_sub.connect();
        try {
            String 
                    filter_tanggal_lp = "", 
                    filter_tanggal_grading = " AND `tanggal_grading` IS NOT NULL ", 
                    filter_tanggal_lp_sesekan = "1";
            if (DateChooser1.getDate() != null && DateChooser2.getDate() != null) {
                filter_tanggal_lp = " AND `tanggal_lp` BETWEEN '" + dateFormat.format(DateChooser1.getDate()) + "' AND '" + dateFormat.format(DateChooser2.getDate()) + "' ";
                filter_tanggal_lp_sesekan = " `tanggal_lp` BETWEEN '" + dateFormat.format(DateChooser1.getDate()) + "' AND '" + dateFormat.format(DateChooser2.getDate()) + "' ";
                filter_tanggal_grading = " AND `tanggal_grading` BETWEEN '" + dateFormat.format(DateChooser1.getDate()) + "' AND '" + dateFormat.format(DateChooser2.getDate()) + "' ";
            }
            DefaultTableModel model = (DefaultTableModel) Table_kpi_sub.getModel();
            model.setRowCount(0);
            sql = "SELECT `tb_sub_waleta`.`kode_sub`, IF(`tanggal_tutup` IS NULL, 1, 0) AS 'sub_aktif'  "
                    + "FROM `tb_sub_waleta` "
                    + "WHERE 1 ";
            pst = Utility.db_sub.getConnection().prepareStatement(sql);
            rs = pst.executeQuery();
            Object[] row = new Object[15];
            int baris = 0;
            decimalFormat.setMaximumFractionDigits(0);
            while (rs.next()) {
                float total_baku = 0, rata2_baku_harian = 0;
                String query1 = "SELECT `ruangan`, SUM(`berat_per_hari`) AS 'total_baku', AVG(`berat_per_hari`) AS 'rata2_baku_harian' "
                        + "FROM "
                        + "(SELECT `ruangan`, `tanggal_lp`, SUM(`berat_basah`) AS 'berat_per_hari' "
                        + "FROM `tb_laporan_produksi` \n"
                        + "WHERE "
                        + "LENGTH(`ruangan`) = 5 "
                        + filter_tanggal_lp
                        + "GROUP BY `ruangan`, `tanggal_lp`) "
                        + "A "
                        + "WHERE `ruangan` = '" + rs.getString("kode_sub") + "' "
                        + "GROUP BY `ruangan`";
                ResultSet rs1 = Utility.db.getStatement().executeQuery(query1);
                if (rs1.next()) {
                    total_baku = rs1.getFloat("total_baku");
                    rata2_baku_harian = rs1.getFloat("rata2_baku_harian");
                }
                float total_bjd = 0, rata2_bjd_harian = 0;
                String query2 = "SELECT `ruangan`, SUM(`berat_per_hari`) AS 'total_bjd', "
                        + "AVG(`berat_per_hari`) AS 'rata2_bjd_harian' "
                        + "FROM "
                        + "(SELECT `ruangan`, `tanggal_grading`, SUM(`berat`) AS 'berat_per_hari' "
                        + "FROM `tb_bahan_jadi_masuk` "
                        + "LEFT JOIN `tb_laporan_produksi` ON `tb_bahan_jadi_masuk`.`kode_asal` = `tb_laporan_produksi`.`no_laporan_produksi` "
                        + "WHERE "
                        + "LENGTH(`ruangan`) = 5 "
                        + filter_tanggal_grading
                        + "GROUP BY `ruangan`, `tanggal_grading`) "
                        + "A "
                        + "WHERE `ruangan` = '" + rs.getString("kode_sub") + "' "
                        + "GROUP BY `ruangan`";
                ResultSet rs2 = Utility.db.getStatement().executeQuery(query2);
                if (rs2.next()) {
                    total_bjd = rs2.getFloat("total_bjd");
                    rata2_bjd_harian = rs2.getFloat("rata2_bjd_harian");
                }
                float gram_sesekan = 0;
                String query3 = "SELECT `sub`, SUM(`sesekan_bersih`) as 'gram_sesekan' "
                        + "FROM `tb_laporan_produksi_sesekan` "
                        + "WHERE " + filter_tanggal_lp_sesekan + " "
                        + "AND `sub` = '" + rs.getString("kode_sub") + "'"
                        + "GROUP BY `sub`";
                ResultSet rs3 = Utility.db.getStatement().executeQuery(query3);
                if (rs3.next()) {
                    gram_sesekan = rs3.getFloat("gram_sesekan");
                }
                row[0] = rs.getString("kode_sub");
                row[1] = decimalFormat.format(rata2_baku_harian);
                row[2] = decimalFormat.format(total_baku + gram_sesekan);
                row[3] = decimalFormat.format(total_bjd);
                row[10] = rs.getInt("sub_aktif");
                if ((total_baku + gram_sesekan + total_bjd) > 0) {
                    model.addRow(row);
                    dataset1.setValue((total_baku + gram_sesekan), "Berat", rs.getString("kode_sub"));
                    Rendemen(baris, rs.getString("kode_sub"));
                    Persentase_QC(baris, rs.getString("kode_sub"));
                    baris++;
                }
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_kpi_sub);
            Table_kpi_sub.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if ((int) Table_kpi_sub.getValueAt(row, 10) == 0) {
                        if (isSelected) {
                            comp.setBackground(Table_kpi_sub.getSelectionBackground());
                            comp.setForeground(Table_kpi_sub.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.LIGHT_GRAY);
                            comp.setForeground(Table_kpi_sub.getForeground());
                        }
                    } else {
                        if (isSelected) {
                            comp.setBackground(Table_kpi_sub.getSelectionBackground());
                            comp.setForeground(Table_kpi_sub.getSelectionForeground());
                        } else {
                            comp.setBackground(Table_kpi_sub.getBackground());
                            comp.setForeground(Table_kpi_sub.getForeground());
                        }
                    }
                    return comp;
                }
            });
            Table_kpi_sub.repaint();
            Table_kpi_sub.setDefaultRenderer(Float.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if ((int) Table_kpi_sub.getValueAt(row, 10) == 0) {
                        if (isSelected) {
                            comp.setBackground(Table_kpi_sub.getSelectionBackground());
                            comp.setForeground(Table_kpi_sub.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.LIGHT_GRAY);
                            comp.setForeground(Table_kpi_sub.getForeground());
                        }
                    } else {
                        if (isSelected) {
                            comp.setBackground(Table_kpi_sub.getSelectionBackground());
                            comp.setForeground(Table_kpi_sub.getSelectionForeground());
                        } else {
                            comp.setBackground(Table_kpi_sub.getBackground());
                            comp.setForeground(Table_kpi_sub.getForeground());
                        }
                    }
                    return comp;
                }
            });
            Table_kpi_sub.repaint();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JFrame_TV_Sub.class.getName()).log(Level.SEVERE, null, e);
        }
//        AVG_ProductionPerWeek();
//        Total_Production();
//        AVG_Lama_proses();
//        Persentase_QC();
//        Penjualan();
//        ColumnsAutoSizer.sizeColumnsToFit(jTable_kpi);
    }

    public void Rendemen(int row, String kode_sub) {
        try {
            String filter_tanggal = " AND `tgl_setor_f2` IS NOT NULL ";
            if (DateChooser1.getDate() != null && DateChooser2.getDate() != null) {
                filter_tanggal = " AND `tgl_setor_f2` BETWEEN '" + dateFormat.format(DateChooser1.getDate()) + "' AND '" + dateFormat.format(DateChooser2.getDate()) + "'";
            }
            String query = "SELECT "
                    + "ROUND((SUM(IF((`berat_fbonus`+`berat_fnol`)>0,(`berat_fbonus`+`berat_fnol`) - (`tambahan_kaki1`+`tambahan_kaki2`),0)) / SUM(`tb_laporan_produksi`.`berat_kering`)) * 100, 1) AS 'utuh', "
                    + "ROUND((SUM(IF((`berat_jidun`)>0,`berat_jidun` - (`tambahan_kaki1`+`tambahan_kaki2`),0)) / SUM(`tb_laporan_produksi`.`berat_kering`)) * 100, 1) AS 'jidun', "
                    + "ROUND((SUM(`berat_pecah`+`berat_flat`) / SUM(`tb_laporan_produksi`.`berat_kering`)) * 100, 1) AS 'PecahFlat', "
                    + "ROUND((SUM(`sesekan` + `hancuran` + `rontokan` + `bonggol` + `serabut`) / SUM(`tb_laporan_produksi`.`berat_kering`)) * 100, 1) AS 'ByProd' "
                    + "FROM `tb_finishing_2` "
                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_finishing_2`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    + "WHERE "
                    + "`tb_laporan_produksi`.`ruangan` = '" + kode_sub + "' "
                    + filter_tanggal;
            ResultSet Result = Utility.db.getStatement().executeQuery(query);
            if (Result.next()) {
                Table_kpi_sub.setValueAt(Result.getFloat("utuh"), row, 4);
                Table_kpi_sub.setValueAt(Result.getFloat("jidun"), row, 5);
                Table_kpi_sub.setValueAt(Result.getFloat("PecahFlat"), row, 6);
                Table_kpi_sub.setValueAt(Result.getFloat("ByProd"), row, 7);
                Table_kpi_sub.setValueAt(Math.round((100 - (Result.getFloat("utuh") + Result.getFloat("jidun") + Result.getFloat("PecahFlat") + Result.getFloat("ByProd"))) * 10f) / 10f, row, 8);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JFrame_TV_Sub.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void Persentase_QC(int row, String kode_sub) {
        try {
            String filter_tanggal = " AND `tgl_selesai` IS NOT NULL ";
            if (DateChooser1.getDate() != null && DateChooser2.getDate() != null) {
                filter_tanggal = " AND `tgl_selesai` BETWEEN '" + dateFormat.format(DateChooser1.getDate()) + "' AND '" + dateFormat.format(DateChooser2.getDate()) + "'";
            }
            String query = "SELECT COUNT(IF(`status_akhir` = 'PASSED', 1, 0)) AS 'pass', COUNT(`tb_lab_laporan_produksi`.`no_laporan_produksi`) AS 'total_lp' "
                    + "FROM `tb_lab_laporan_produksi` "
                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_lab_laporan_produksi`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi` "
                    + "WHERE "
                    + "`tb_laporan_produksi`.`ruangan` = '" + kode_sub + "' "
                    + filter_tanggal;
            ResultSet Result = Utility.db.getStatement().executeQuery(query);
            if (Result.next()) {
                float persen = Math.round((Result.getFloat("pass") / Result.getFloat("total_lp")) * 1000f) / 10f;
                Table_kpi_sub.setValueAt(persen, row, 9);
            }
        } catch (SQLException ex) {
            Logger.getLogger(JFrame_TV_Sub.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        Panel_chart1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        Table_kpi_sub = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        DateChooser1 = new com.toedter.calendar.JDateChooser();
        DateChooser2 = new com.toedter.calendar.JDateChooser();
        button_refresh = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        Panel_chart1.setBackground(new java.awt.Color(255, 255, 255));
        Panel_chart1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        javax.swing.GroupLayout Panel_chart1Layout = new javax.swing.GroupLayout(Panel_chart1);
        Panel_chart1.setLayout(Panel_chart1Layout);
        Panel_chart1Layout.setHorizontalGroup(
            Panel_chart1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        Panel_chart1Layout.setVerticalGroup(
            Panel_chart1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 272, Short.MAX_VALUE)
        );

        Table_kpi_sub.setFont(new java.awt.Font("Arial", 1, 20)); // NOI18N
        Table_kpi_sub.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"", null, null, null, null, null, null, null, null, null, null},
                {"", null, null, null, null, null, null, null, null, null, null},
                {"", null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Sub", "Avg Baku harian", "Tot Baku", "BJD", "Utuh", "JDN", "Flat", "BP", "SH", "% PASSED", ""
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Integer.class
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
        Table_kpi_sub.setRowHeight(26);
        Table_kpi_sub.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(Table_kpi_sub);

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel1.setText("Tanggal :");

        DateChooser1.setBackground(new java.awt.Color(255, 255, 255));
        DateChooser1.setDateFormatString("dd MMMM yyyy");
        DateChooser1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        DateChooser2.setBackground(new java.awt.Color(255, 255, 255));
        DateChooser2.setDateFormatString("dd MMMM yyyy");
        DateChooser2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        button_refresh.setBackground(new java.awt.Color(255, 255, 255));
        button_refresh.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_refresh.setText("Refresh");
        button_refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refreshActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1346, Short.MAX_VALUE)
                    .addComponent(Panel_chart1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(DateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(DateChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_refresh)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DateChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_refresh, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Panel_chart1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 421, Short.MAX_VALUE)
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

    private void button_refreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refreshActionPerformed
        // TODO add your handling code here:
        refreshTable_kpi();
    }//GEN-LAST:event_button_refreshActionPerformed

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(JFrame_TV_Sub.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JFrame_TV_Sub.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JFrame_TV_Sub.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JFrame_TV_Sub.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JFrame_TV_Sub chart = new JFrame_TV_Sub();
                chart.pack();
                chart.setResizable(true);
                chart.setLocationRelativeTo(null);
                chart.setVisible(true);
                chart.setEnabled(true);
                chart.init();
                chart.setExtendedState(JFrame.MAXIMIZED_BOTH);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser DateChooser1;
    private com.toedter.calendar.JDateChooser DateChooser2;
    private javax.swing.JPanel Panel_chart1;
    private javax.swing.JTable Table_kpi_sub;
    private javax.swing.JButton button_refresh;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables
}
