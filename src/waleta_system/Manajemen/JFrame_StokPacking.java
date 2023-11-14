package waleta_system.Manajemen;

import java.awt.Color;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

public class JFrame_StokPacking extends javax.swing.JFrame {

    
    String sql = null;
    ResultSet rs;
    PreparedStatement pst;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    public DefaultPieDataset PieChart_dataset;
//    JFreeChart chart1;

    public JFrame_StokPacking() {
        initComponents();
    }

    public void init(int show_value) {
        try {
            
            
            PieChart();
            refreshTable_DataStok_per_grade();
            refreshTable_DataStok_per_spk();
            refresh_JAM();
            if (show_value == 0) {
                jLabel4.setVisible(false);
                label_total_harga_cny.setVisible(false);
                jLabel5.setVisible(false);
                label_kurs.setVisible(false);
                jLabel18.setVisible(false);
                label_total_harga_idr.setVisible(false);
            } else if (show_value == 1) {
                jLabel4.setVisible(true);
                label_total_harga_cny.setVisible(true);
                jLabel5.setVisible(true);
                label_kurs.setVisible(true);
                jLabel18.setVisible(true);
                label_total_harga_idr.setVisible(true);
            }
        } catch (Exception ex) {
            Logger.getLogger(JFrame_StokPacking.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refresh_JAM() {
        LocalDateTime myDateObj = LocalDateTime.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        String formattedDate = myDateObj.format(myFormatObj);
        label_jam.setText(formattedDate);
    }

    public void refreshTable_DataStok_per_grade() {
        try {
            float total_gram_Stok_Packing = 0, total_nilai_bjd = 0;
            DefaultTableModel model_stok_per_grade = (DefaultTableModel) tabel_stok_per_grade.getModel();
            model_stok_per_grade.setRowCount(0);

            sql = "SELECT `tb_grade_bahan_jadi`.`kode`, `tb_grade_bahan_jadi`.`kode_grade`, SUM(`berat`) AS 'berat', `harga`, `kategori_jual` "
                    + "FROM `tb_grade_bahan_jadi` "
                    + "LEFT JOIN `tb_box_bahan_jadi` ON `tb_grade_bahan_jadi`.`kode` = `tb_box_bahan_jadi`.`kode_grade_bahan_jadi` "
                    + "LEFT JOIN `tb_box_packing` ON `tb_box_packing`.`no_box` = `tb_box_bahan_jadi`.`no_box` "
                    + "WHERE `tb_box_packing`.`status` = 'STOK' "
                    + "GROUP BY `tb_grade_bahan_jadi`.`kode` ORDER BY `kode_grade` ASC";
            pst = Utility.db.getConnection().prepareStatement(sql);
            rs = pst.executeQuery();
            Object[] row = new Object[6];
            while (rs.next()) {
                float gram_stok = rs.getInt("berat");
                row[0] = rs.getString("kode_grade").substring(4);
                row[1] = rs.getString("kategori_jual");
                row[2] = gram_stok;
                total_gram_Stok_Packing = total_gram_Stok_Packing + gram_stok;
                total_nilai_bjd = total_nilai_bjd + (gram_stok * (rs.getFloat("harga") * 0.98f / 1000.f));
                model_stok_per_grade.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(tabel_stok_per_grade);

            //GET KURS TERAKHIR
            float kurs = 0;
            sql = "SELECT `tanggal`, `nilai` FROM `tb_kurs` WHERE 1 ORDER BY `tb_kurs`.`tanggal` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                kurs = rs.getFloat("nilai");
            }

            decimalFormat.setMaximumFractionDigits(0);
            label_total_harga_cny.setText("¥" + decimalFormat.format(total_nilai_bjd));
            label_kurs.setText(decimalFormat.format(kurs));
            label_total_harga_idr.setText("Rp. " + decimalFormat.format(total_nilai_bjd * kurs));
            label_total_gram_Stok.setText(decimalFormat.format(total_gram_Stok_Packing / 1000.f) + " Kg");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JFrame_StokPacking.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_DataStok_per_spk() {
        try {
            DefaultTableModel model_stok_per_spk = (DefaultTableModel) tabel_stok_per_spk.getModel();
            model_stok_per_spk.setRowCount(0);
            sql = "SELECT `tb_spk`.`kode_spk`, `tb_buyer`.`nama`, SUM(`tb_box_bahan_jadi`.`berat`) AS 'berat' "
                    + "FROM `tb_box_bahan_jadi` "
                    + "LEFT JOIN `tb_box_packing` ON `tb_box_packing`.`no_box` = `tb_box_bahan_jadi`.`no_box` "
                    + "LEFT JOIN `tb_spk_detail` ON `tb_box_packing`.`no_grade_spk` = `tb_spk_detail`.`no` "
                    + "LEFT JOIN `tb_spk` ON `tb_spk`.`kode_spk` = `tb_spk_detail`.`kode_spk` "
                    + "LEFT JOIN `tb_buyer` ON `tb_spk`.`buyer` = `tb_buyer`.`kode_buyer` "
                    + "WHERE `tb_box_packing`.`status` = 'STOK' "
                    + "GROUP BY `tb_spk`.`kode_spk` "
                    + "ORDER BY `tb_spk`.`buyer` ASC";
            pst = Utility.db.getConnection().prepareStatement(sql);
            rs = pst.executeQuery();
            Object[] row = new Object[6];
            while (rs.next()) {
                String spk = "-", nama = "-";
                if (rs.getString("kode_spk") != null) {
                    spk = rs.getString("kode_spk");
                    nama = rs.getString("nama");
                }
                row[0] = spk;
                row[1] = nama;
                row[2] = rs.getInt("berat");
                model_stok_per_spk.addRow(row);
                PieChart_dataset.setValue(nama, rs.getInt("berat"));
            }
            ColumnsAutoSizer.sizeColumnsToFit(tabel_stok_per_spk);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JFrame_StokPacking.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void PieChart() {
        PieChart_dataset = new DefaultPieDataset();
        JFreeChart chart7 = ChartFactory.createPieChart("Stok PACKING", PieChart_dataset, true, true, false);
        chart7.setBackgroundPaint(Color.WHITE);
        chart7.getTitle().setPaint(Color.red);
        PiePlot pp2 = (PiePlot) chart7.getPlot();
//        pp2.setSectionPaint("JUAL", Color.cyan);
//        pp2.setSectionPaint("REPROSES", Color.orange);
//        pp2.setSectionPaint("RESIDU", Color.GREEN);
//        pp2.setSectionPaint("SUSAH JUAL", Color.red);
//        pp2.setSectionPaint("SERABUT", new Color(44, 199, 204));
//        pp2.setSectionPaint("NON NS & NON AKTIF", Color.black);
        ChartPanel panelChartStokBahanBaku = new ChartPanel(chart7);
        panelChartStokBahanBaku.setLocation(0, 0);
        panelChartStokBahanBaku.setSize(Panel_Pie_Chart.getWidth(), Panel_Pie_Chart.getHeight());
        Panel_Pie_Chart.add(panelChartStokBahanBaku);
        panelChartStokBahanBaku.addChartMouseListener(new ChartMouseListener() {
            @Override
            public void chartMouseClicked(ChartMouseEvent cme) {
                if (cme.getEntity() != null) {
                    if (!(cme.getEntity() instanceof LegendItemEntity)) {
                        PieSectionEntity entity = (PieSectionEntity) cme.getEntity();
                        label_detail_chart.setText(entity.getToolTipText());
//                        float total = 0;
//                        for (int i = 0; i < PieChart_dataset.getItemCount(); i++) {
//                            total = total + entity.getDataset().getValue(i).intValue();
//                        }
//                        System.out.println(total);
                    }
                }
            }

            @Override
            public void chartMouseMoved(ChartMouseEvent cme) {

            }
        });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        label_total_gram_Stok = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        Button_Refresh = new javax.swing.JButton();
        label_jam = new javax.swing.JLabel();
        Panel_Pie_Chart = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabel_stok_per_grade = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabel_stok_per_spk = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        label_total_harga_cny = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        label_kurs = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        label_total_harga_idr = new javax.swing.JLabel();
        label_detail_chart = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        label_total_gram_Stok.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_Stok.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        label_total_gram_Stok.setForeground(new java.awt.Color(255, 0, 0));
        label_total_gram_Stok.setText("0");

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel3.setText("Finished Goods Inventory :");

        Button_Refresh.setBackground(new java.awt.Color(255, 255, 255));
        Button_Refresh.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        Button_Refresh.setText("REFRESH");
        Button_Refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_RefreshActionPerformed(evt);
            }
        });

        label_jam.setBackground(new java.awt.Color(255, 255, 255));
        label_jam.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_jam.setText("Jam :");

        Panel_Pie_Chart.setBackground(new java.awt.Color(255, 255, 255));
        Panel_Pie_Chart.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        javax.swing.GroupLayout Panel_Pie_ChartLayout = new javax.swing.GroupLayout(Panel_Pie_Chart);
        Panel_Pie_Chart.setLayout(Panel_Pie_ChartLayout);
        Panel_Pie_ChartLayout.setHorizontalGroup(
            Panel_Pie_ChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        Panel_Pie_ChartLayout.setVerticalGroup(
            Panel_Pie_ChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 675, Short.MAX_VALUE)
        );

        tabel_stok_per_grade.setAutoCreateRowSorter(true);
        tabel_stok_per_grade.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        tabel_stok_per_grade.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Grade", "Kategori", "Gram"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabel_stok_per_grade.setRequestFocusEnabled(false);
        tabel_stok_per_grade.setRowSelectionAllowed(false);
        tabel_stok_per_grade.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tabel_stok_per_grade);
        if (tabel_stok_per_grade.getColumnModel().getColumnCount() > 0) {
            tabel_stok_per_grade.getColumnModel().getColumn(1).setMinWidth(80);
        }

        tabel_stok_per_spk.setAutoCreateRowSorter(true);
        tabel_stok_per_spk.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        tabel_stok_per_spk.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "SPK", "Buyer", "Gram"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabel_stok_per_spk.setRequestFocusEnabled(false);
        tabel_stok_per_spk.setRowSelectionAllowed(false);
        tabel_stok_per_spk.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(tabel_stok_per_spk);
        if (tabel_stok_per_spk.getColumnModel().getColumnCount() > 0) {
            tabel_stok_per_spk.getColumnModel().getColumn(0).setMinWidth(80);
        }

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel4.setText("Stock Value :");

        label_total_harga_cny.setBackground(new java.awt.Color(255, 255, 255));
        label_total_harga_cny.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        label_total_harga_cny.setForeground(new java.awt.Color(255, 0, 0));
        label_total_harga_cny.setText("0");

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel5.setText("x");

        label_kurs.setBackground(new java.awt.Color(255, 255, 255));
        label_kurs.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        label_kurs.setForeground(new java.awt.Color(255, 0, 0));
        label_kurs.setText("0");

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel18.setText("=");

        label_total_harga_idr.setBackground(new java.awt.Color(255, 255, 255));
        label_total_harga_idr.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        label_total_harga_idr.setForeground(new java.awt.Color(255, 0, 0));
        label_total_harga_idr.setText("0");

        label_detail_chart.setBackground(new java.awt.Color(255, 255, 255));
        label_detail_chart.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_detail_chart.setText("DETAIL");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 524, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Panel_Pie_Chart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gram_Stok)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_harga_cny)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(label_kurs)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(label_total_harga_idr)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 692, Short.MAX_VALUE)
                        .addComponent(label_detail_chart)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_jam)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Button_Refresh)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jLabel3)
                            .addComponent(label_total_gram_Stok)
                            .addComponent(jLabel4)
                            .addComponent(label_total_harga_cny)
                            .addComponent(jLabel5)
                            .addComponent(label_kurs)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel18)
                                .addComponent(label_total_harga_idr)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1)
                            .addComponent(jScrollPane2)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(label_jam, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(label_detail_chart))
                            .addComponent(Button_Refresh, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Panel_Pie_Chart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
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

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
//        t.cancel();
//        System.out.println("STOP");
    }//GEN-LAST:event_formWindowClosing

    private void Button_RefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_RefreshActionPerformed
        // TODO add your handling code here:
        refreshTable_DataStok_per_grade();
        refreshTable_DataStok_per_spk();
        refresh_JAM();
    }//GEN-LAST:event_Button_RefreshActionPerformed

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
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JFrame_StokPacking.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame_StokPacking Stok = new JFrame_StokPacking();
                Stok.setVisible(true);
                Stok.setLocationRelativeTo(null);
                Stok.setExtendedState(MAXIMIZED_BOTH);
                Stok.init(1);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Button_Refresh;
    private javax.swing.JPanel Panel_Pie_Chart;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel label_detail_chart;
    private javax.swing.JLabel label_jam;
    private javax.swing.JLabel label_kurs;
    private javax.swing.JLabel label_total_gram_Stok;
    private javax.swing.JLabel label_total_harga_cny;
    private javax.swing.JLabel label_total_harga_idr;
    private javax.swing.JTable tabel_stok_per_grade;
    private javax.swing.JTable tabel_stok_per_spk;
    // End of variables declaration//GEN-END:variables
}
