package waleta_system.Panel_produksi;

import waleta_system.Class.Utility;

import java.awt.Color;
import java.awt.Font;
import java.awt.SystemColor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.LegendItemEntity;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.ExportToExcel;

public class JFrame_Rank_Cuci extends javax.swing.JFrame {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    DecimalFormat decimalFormat = new DecimalFormat();
    public DefaultCategoryDataset dataset;

    static int loop = 1;
    static Timer t;

    public JFrame_Rank_Cuci() {
        initComponents();
    }

    public void init() {
        initChart();
        refreshTable();
    }

    public void initChart() {
        dataset = new DefaultCategoryDataset();
        JFreeChart chart = ChartFactory.createBarChart("5 Pejuang Cuci Teladan", "Nama Pegawai", "Keping Cucian", dataset, PlotOrientation.HORIZONTAL, true, true, false);
        chart.setBackgroundPaint(Color.WHITE);
        chart.getTitle().setPaint(Color.red);
        CategoryPlot categoryPlot = (CategoryPlot) chart.getPlot();
        CategoryAxis axis = categoryPlot.getDomainAxis();
        ValueAxis axis2 = categoryPlot.getRangeAxis();

        axis.setTickLabelFont(new Font("Calibri", Font.BOLD, 18));
//        axis2.setTickLabelFont(new Font("Verdana", Font.BOLD, 12));

        categoryPlot.setBackgroundPaint(SystemColor.inactiveCaption);
        ((BarRenderer) categoryPlot.getRenderer()).setBarPainter(new StandardBarPainter());
        BarRenderer barRenderer = (BarRenderer) chart.getCategoryPlot().getRenderer();
        barRenderer.setSeriesPaint(0, new Color(0, 100, 0));
        barRenderer.setBaseLegendTextFont(new Font("Calibri", Font.BOLD, 14));
//        barRenderer.setSeriesPaint(1, new Color(0,128,0));
//        barRenderer.setSeriesPaint(2, new Color(34,139,34));
//        barRenderer.setSeriesPaint(3, new Color(0,180,0));
//        barRenderer.setSeriesPaint(4, new Color(50,205,50));
//        barRenderer.setSeriesPaint(5, new Color(0,255,0));
//        barRenderer.setSeriesPaint(6, new Color(124,252,0));
//        barRenderer.setSeriesPaint(7, new Color(127,255,0));
//        barRenderer.setSeriesPaint(8, new Color(173,255,47));
//        barRenderer.setSeriesPaint(9, new Color(173,255,47));
//        barRenderer.setSeriesPaint(1, Color.blue);
//        barRenderer.setSeriesPaint(2, Color.green);
//        barRenderer.setSeriesPaint(3, Color.yellow);
//        barRenderer.setSeriesPaint(4, Color.cyan);
//        barRenderer.setSeriesPaint(5, Color.magenta);
        ChartPanel panelChart = new ChartPanel(chart);
        panelChart.setLocation(0, 0);
        panelChart.setSize(jPanel_chart.getSize());
        jPanel_chart.add(panelChart);
        panelChart.addChartMouseListener(new ChartMouseListener() {
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

    public void refreshTable() {
        if (Date1.getDate() == null || Date2.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Maaf tanggal tidak boleh kosong!");
        } else {
            try {
                dataset.clear();
                decimalFormat.setMaximumFractionDigits(2);
                float total_kpg = 0;
                DefaultTableModel model = (DefaultTableModel) table_data_rank_cuci.getModel();
                model.setRowCount(0);
                sql = "SELECT `tb_cuci`.`id_pegawai`, `tb_karyawan`.`nama_pegawai`, `tb_bagian`.`nama_bagian`, SUM(`tb_laporan_produksi`.`jumlah_keping`) AS 'keping'\n"
                        + "FROM `tb_cuci` \n"
                        + "LEFT JOIN `tb_laporan_produksi` ON `tb_cuci`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                        + "LEFT JOIN `tb_karyawan` ON `tb_cuci`.`id_pegawai` = `tb_karyawan`.`id_pegawai`\n"
                        + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`\n"
                        + "WHERE `tb_cuci`.`id_pegawai` IS NOT NULL AND `tgl_masuk_cuci` BETWEEN '" + dateFormat.format(Date1.getDate()) + "' AND '" + dateFormat.format(Date2.getDate()) + "' \n"
                        + "GROUP BY `tb_cuci`.`id_pegawai` ORDER BY `keping` DESC \n"
                        + "LIMIT " + Integer.valueOf(ComboBox_tampilkan.getSelectedItem().toString());

                rs = Utility.db.getStatement().executeQuery(sql);
                Object[] row = new Object[6];
                int no_urut = 0;
                while (rs.next()) {
                    no_urut++;
                    row[0] = no_urut;
//                row[1] = rs.getString("id_pegawai");
                    row[1] = rs.getString("nama_pegawai");
                    row[2] = rs.getString("nama_bagian");
                    row[3] = rs.getInt("keping");
                    if (no_urut <= 5) {
                        dataset.setValue(rs.getInt("keping"), "Keping Cucian", rs.getString("nama_pegawai"));
                        dataset.getColumnKey(0);
                    }
                    model.addRow(row);
                    total_kpg = total_kpg + rs.getFloat("keping");
                }
                ColumnsAutoSizer.sizeColumnsToFit(table_data_rank_cuci);
                int rowData = table_data_rank_cuci.getRowCount();
                label_total_pekerja.setText(Integer.toString(rowData));
                label_total_gram.setText(decimalFormat.format(total_kpg));
            } catch (SQLException ex) {
                Logger.getLogger(JFrame_Rank_Cuci.class.getName()).log(Level.SEVERE, null, ex);
            }
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

        jPanel2 = new javax.swing.JPanel();
        button_refresh = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        button_export = new javax.swing.JButton();
        Date1 = new com.toedter.calendar.JDateChooser();
        Date2 = new com.toedter.calendar.JDateChooser();
        jScrollPane1 = new javax.swing.JScrollPane();
        table_data_rank_cuci = new javax.swing.JTable();
        label_total_cabutan2 = new javax.swing.JLabel();
        label_total_pekerja = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        label_total_gram = new javax.swing.JLabel();
        label_total_cabutan3 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        ComboBox_tampilkan = new javax.swing.JComboBox<>();
        jPanel_chart = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "PEJUANG CUCI TELADAN", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Arial", 1, 20))); // NOI18N

        button_refresh.setBackground(new java.awt.Color(255, 255, 255));
        button_refresh.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_refresh.setText("Refresh");
        button_refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refreshActionPerformed(evt);
            }
        });

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel2.setText("Periode :");

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel3.setText("-");

        button_export.setBackground(new java.awt.Color(255, 255, 255));
        button_export.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export.setText("Export to Excel");
        button_export.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_exportActionPerformed(evt);
            }
        });

        Date1.setBackground(new java.awt.Color(255, 255, 255));
        Date1.setDate(new Date(new Date().getTime()-(6 * 24 * 60 * 60 * 1000)));
        Date1.setDateFormatString("dd MMMM yyyy");
        Date1.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N

        Date2.setBackground(new java.awt.Color(255, 255, 255));
        Date2.setDate(new Date());
        Date2.setDateFormatString("dd MMMM yyyy");
        Date2.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N

        table_data_rank_cuci.setAutoCreateRowSorter(true);
        table_data_rank_cuci.setFont(new java.awt.Font("Arial Narrow", 1, 36)); // NOI18N
        table_data_rank_cuci.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Rank", "Nama", "Bagian", "gram"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class
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
        table_data_rank_cuci.setRowHeight(32);
        table_data_rank_cuci.setRowSelectionAllowed(false);
        table_data_rank_cuci.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(table_data_rank_cuci);

        label_total_cabutan2.setBackground(new java.awt.Color(255, 255, 255));
        label_total_cabutan2.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_cabutan2.setText("Orang");

        label_total_pekerja.setBackground(new java.awt.Color(255, 255, 255));
        label_total_pekerja.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_pekerja.setText("88");

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel10.setText("Total Pekerja :");

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel5.setText("Total Gram :");

        label_total_gram.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_gram.setText("88");

        label_total_cabutan3.setBackground(new java.awt.Color(255, 255, 255));
        label_total_cabutan3.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_cabutan3.setText("Gram");

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel6.setText("Menampilkan :");

        ComboBox_tampilkan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_tampilkan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "5", "10", "20", "50" }));
        ComboBox_tampilkan.setSelectedIndex(1);

        jPanel_chart.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_chart.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout jPanel_chartLayout = new javax.swing.GroupLayout(jPanel_chart);
        jPanel_chart.setLayout(jPanel_chartLayout);
        jPanel_chartLayout.setHorizontalGroup(
            jPanel_chartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 728, Short.MAX_VALUE)
        );
        jPanel_chartLayout.setVerticalGroup(
            jPanel_chartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel7.setText("Data");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(label_total_pekerja)
                            .addComponent(label_total_gram))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(label_total_cabutan2, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_cabutan3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_tampilkan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_refresh)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_export))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date1, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date2, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 600, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel_chart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(Date1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Date2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ComboBox_tampilkan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_refresh, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_export, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 544, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_cabutan3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_gram, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_cabutan2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_pekerja, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jPanel_chart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void button_refreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refreshActionPerformed
        // TODO add your handling code here:
        refreshTable();
    }//GEN-LAST:event_button_refreshActionPerformed

    private void button_exportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_exportActionPerformed
        // TODO add your handling code here:
        DefaultTableModel modelA = (DefaultTableModel) table_data_rank_cuci.getModel();
        ExportToExcel.writeToExcel(modelA, jPanel2);
    }//GEN-LAST:event_button_exportActionPerformed

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
            java.util.logging.Logger.getLogger(JFrame_Rank_Cuci.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JFrame_Rank_Cuci.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JFrame_Rank_Cuci.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JFrame_Rank_Cuci.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JFrame_Rank_Cuci frame = new JFrame_Rank_Cuci();
                frame.pack();
                frame.setResizable(true);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
                frame.setEnabled(true);
                frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                frame.init();
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_tampilkan;
    private com.toedter.calendar.JDateChooser Date1;
    private com.toedter.calendar.JDateChooser Date2;
    private javax.swing.JButton button_export;
    private javax.swing.JButton button_refresh;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel_chart;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel label_total_cabutan2;
    private javax.swing.JLabel label_total_cabutan3;
    private javax.swing.JLabel label_total_gram;
    private javax.swing.JLabel label_total_pekerja;
    private javax.swing.JTable table_data_rank_cuci;
    // End of variables declaration//GEN-END:variables
}
