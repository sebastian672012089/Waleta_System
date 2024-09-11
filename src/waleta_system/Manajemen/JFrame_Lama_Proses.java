package waleta_system.Manajemen;

import waleta_system.*;
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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import waleta_system.Class.Utility;

public class JFrame_Lama_Proses extends javax.swing.JFrame {

    GraphicsDevice device = GraphicsEnvironment
            .getLocalGraphicsEnvironment().getScreenDevices()[0];
    String sql = null;
    ResultSet rs;
    PreparedStatement pst;
    Date today = new Date();//date berisikan tanggal hari ini
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    int[] bulan, tahun;
    float[] PembelianBaku, GradingBaku, Produksi, Packing, FullPayment;
    int jumlah_bulan = 12;

    public JFrame_Lama_Proses() {
        initComponents();
    }

    public void init() {
        bulan = new int[jumlah_bulan];
        tahun = new int[jumlah_bulan];
        PembelianBaku = new float[jumlah_bulan];
        GradingBaku = new float[jumlah_bulan];
        Produksi = new float[jumlah_bulan];
        Packing = new float[jumlah_bulan];
        FullPayment = new float[jumlah_bulan];

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

        Table_data.getTableHeader().setFont(new Font("Arial Narrow", Font.BOLD, 18));
        refresh_table();
    }

    public void refresh_table() {
        PembelianBaku();
        GradingBaku();
        Produksi();
        Packing();
        FullPayment();
        perhitungan();
//        ColumnsAutoSizer.sizeColumnsToFit(Table_data);
    }

    public void PembelianBaku() {
        try {
            sql = "SELECT "
                    + "AVG(DATEDIFF(`tgl_terima`, `tgl_kirim`)) AS 'hari', "
                    + "MONTH(`tgl_terima`) AS 'bulan', "
                    + "YEAR(`tgl_terima`) AS 'tahun' "
                    + "FROM `tb_pembelian_bahan_baku`"
                    + "WHERE "
                    + "`tgl_terima` BETWEEN '" + tahun[jumlah_bulan - 1] + "-" + bulan[jumlah_bulan - 1] + "-01' AND '" + tahun[0] + "-" + bulan[0] + "-31'"
                    + "GROUP BY MONTH(`tgl_terima`)";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                for (int i = 0; i < jumlah_bulan; i++) {
                    if (rs.getInt("bulan") == bulan[i]) {
                        PembelianBaku[i] = rs.getFloat("hari");
                    }
                }
            }
        } catch (SQLException e) {
            Logger.getLogger(JFrame_Lama_Proses.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void GradingBaku() {
        try {
            sql = "SELECT AVG(DATEDIFF(`tgl_timbang`, `tgl_masuk`)) AS 'hari', MONTH(`tgl_masuk`) AS 'bulan', YEAR(`tgl_masuk`) AS 'tahun' FROM `tb_bahan_baku_masuk`"
                    + "WHERE `no_kartu_waleta` NOT LIKE '%CMP%' AND `tgl_masuk` BETWEEN '" + tahun[jumlah_bulan - 1] + "-" + bulan[jumlah_bulan - 1] + "-01' AND '" + tahun[0] + "-" + bulan[0] + "-31'"
                    + "GROUP BY MONTH(`tgl_masuk`)";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                for (int i = 0; i < jumlah_bulan; i++) {
                    if (rs.getInt("bulan") == bulan[i]) {
                        GradingBaku[i] = rs.getFloat("hari");
                    }
                }
            }
        } catch (SQLException e) {
            Logger.getLogger(JFrame_Lama_Proses.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void Produksi() {
        try {
            sql = "SELECT AVG(DATEDIFF(`tgl_statusBox`, `tanggal_lp`)) AS 'hari', MONTH(`tanggal_lp`) AS 'bulan', YEAR(`tanggal_lp`) AS 'tahun' FROM `tb_laporan_produksi`"
                    + "LEFT JOIN `tb_bahan_jadi_masuk` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_bahan_jadi_masuk`.`kode_asal`\n"
                    + "LEFT JOIN `tb_tutupan_grading` ON `tb_bahan_jadi_masuk`.`kode_tutupan` = `tb_tutupan_grading`.`kode_tutupan`"
                    + "WHERE `tanggal_lp` BETWEEN '" + tahun[jumlah_bulan - 1] + "-" + bulan[jumlah_bulan - 1] + "-01' AND '" + tahun[0] + "-" + bulan[0] + "-31'"
                    + "GROUP BY MONTH(`tanggal_lp`)";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                for (int i = 0; i < jumlah_bulan; i++) {
                    if (rs.getInt("bulan") == bulan[i]) {
                        Produksi[i] = rs.getFloat("hari");
                    }
                }
            }
        } catch (SQLException e) {
            Logger.getLogger(JFrame_Lama_Proses.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void Packing() {
        try {
            sql = "SELECT AVG(DATEDIFF(`tanggal_pengiriman`, `tanggal_masuk`)) AS 'hari', MONTH(`tanggal_pengiriman`) AS 'bulan', YEAR(`tanggal_pengiriman`) AS 'tahun' FROM `tb_box_packing`"
                    + "LEFT JOIN `tb_pengiriman` ON `tb_box_packing`.`invoice_pengiriman` = `tb_pengiriman`.`invoice_no`\n"
                    + "WHERE `tb_box_packing`.`invoice_pengiriman` LIKE '%/INV/WALETA%' AND `tanggal_pengiriman` BETWEEN '" + tahun[jumlah_bulan - 1] + "-" + bulan[jumlah_bulan - 1] + "-01' AND '" + tahun[0] + "-" + bulan[0] + "-31'"
                    + "GROUP BY MONTH(`tanggal_pengiriman`)";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                for (int i = 0; i < jumlah_bulan; i++) {
                    if (rs.getInt("bulan") == bulan[i]) {
                        Packing[i] = rs.getFloat("hari");
                    }
                }
            }
        } catch (SQLException e) {
            Logger.getLogger(JFrame_Lama_Proses.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void FullPayment() {
        try {
            sql = "SELECT AVG(DATEDIFF(`date_payment2`, `awb_date`)) AS 'hari', MONTH(`awb_date`) AS 'bulan', YEAR(`awb_date`) AS 'tahun' FROM `tb_payment_report`"
                    + "WHERE `awb_date` BETWEEN '" + tahun[jumlah_bulan - 1] + "-" + bulan[jumlah_bulan - 1] + "-01' AND '" + tahun[0] + "-" + bulan[0] + "-31'"
                    + "GROUP BY MONTH(`awb_date`)";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                for (int i = 0; i < jumlah_bulan; i++) {
                    if (rs.getInt("bulan") == bulan[i]) {
                        FullPayment[i] = rs.getFloat("hari");
                    }
                }
            }
        } catch (SQLException e) {
            Logger.getLogger(JFrame_Lama_Proses.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void perhitungan() {
        DefaultTableModel model = (DefaultTableModel) Table_data.getModel();
        decimalFormat.setMaximumFractionDigits(2);
        for (int i = jumlah_bulan - 1; i > -1; i--) {
            model.setValueAt(decimalFormat.format(PembelianBaku[i]), 0, i + 1);
            model.setValueAt(decimalFormat.format(GradingBaku[i]), 1, i + 1);
            model.setValueAt("?", 2, i + 1);
            model.setValueAt(decimalFormat.format(Produksi[i]), 3, i + 1);
            model.setValueAt("?", 4, i + 1);
            model.setValueAt(decimalFormat.format(Packing[i]), 5, i + 1);
            model.setValueAt(decimalFormat.format(FullPayment[i]), 6, i + 1);
            float total = PembelianBaku[i] + GradingBaku[i] + Produksi[i] + Packing[i] + FullPayment[i];
            model.setValueAt(decimalFormat.format(total), 7, i + 1);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        Table_data = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("98% Price BJD");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        Table_data.setFont(new java.awt.Font("Arial Narrow", 0, 24)); // NOI18N
        Table_data.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Pembelian Baku", null, null, null, null, null, null, null, null, null, null, null, null},
                {"Grading Baku", null, null, null, null, null, null, null, null, null, null, null, null},
                {"Penyimpanan Baku", null, null, null, null, null, null, null, null, null, null, null, null},
                {"Proses Produksi - Box GBJ", null, null, null, null, null, null, null, null, null, null, null, null},
                {"Penyimpanan GBJ", null, null, null, null, null, null, null, null, null, null, null, null},
                {"Packing - Shipment", null, null, null, null, null, null, null, null, null, null, null, null},
                {"Full Payment", null, null, null, null, null, null, null, null, null, null, null, null},
                {"TOTAL", null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Section", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
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
            Table_data.getColumnModel().getColumn(0).setMinWidth(230);
        }

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel1.setText("Perhitungan Waktu (Pembelian Baku - Pelunasan) (days)");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1366, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 732, Short.MAX_VALUE))
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
            java.util.logging.Logger.getLogger(JFrame_Lama_Proses.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JFrame_Lama_Proses.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JFrame_Lama_Proses.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JFrame_Lama_Proses.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
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
                JFrame_Lama_Proses chart = new JFrame_Lama_Proses();
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
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables
}
