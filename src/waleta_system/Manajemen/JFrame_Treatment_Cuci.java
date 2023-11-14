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

public class JFrame_Treatment_Cuci extends javax.swing.JFrame {

    GraphicsDevice device = GraphicsEnvironment
            .getLocalGraphicsEnvironment().getScreenDevices()[0];
    String sql = null;
    ResultSet rs;
    PreparedStatement pst;
    Date today = new Date();//date berisikan tanggal hari ini
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    int[] bulan, tahun;
    int jumlah_bulan = 12;

    public JFrame_Treatment_Cuci() {
        initComponents();
        try {
            
        } catch (Exception e) {
        }
    }

    public void init() {
        bulan = new int[jumlah_bulan];
        tahun = new int[jumlah_bulan];
        decimalFormat.setGroupingUsed(true);

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
        try {
            decimalFormat.setMaximumFractionDigits(1);
            DefaultTableModel model = (DefaultTableModel) Table_data.getModel();
            model.setRowCount(0);
            sql = "SELECT DISTINCT(`jenis_treatment`) AS 'jenis_treatment' FROM `tb_rendam` "
                    + "WHERE `tanggal_rendam` BETWEEN DATE_ADD(CURRENT_DATE, INTERVAL -365 DAY) AND CURRENT_DATE ORDER BY `jenis_treatment`";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                model.addRow(new Object[]{rs.getString("jenis_treatment")});
            }
            sql = "SELECT `jenis_treatment`, FORMAT(AVG(`nitrit_utuh`), 1) AS 'nitrit_utuh', FORMAT(AVG(`nitrit_flat`), 1) AS 'nitrit_flat', "
                    + "MONTH(`tb_lab_laporan_produksi`.`tgl_uji`) AS 'bulan', YEAR(`tb_lab_laporan_produksi`.`tgl_uji`) AS 'tahun' "
                    + "FROM `tb_rendam` "
                    + "LEFT JOIN `tb_lab_laporan_produksi` ON `tb_rendam`.`no_laporan_produksi` = `tb_lab_laporan_produksi`.`no_laporan_produksi` "
                    + "WHERE `tb_lab_laporan_produksi`.`tgl_uji` BETWEEN '" + tahun[jumlah_bulan - 1] + "-" + bulan[jumlah_bulan - 1] + "-01' AND '" + tahun[0] + "-" + bulan[0] + "-31'"
                    + "GROUP BY MONTH(`tb_lab_laporan_produksi`.`tgl_uji`), YEAR(`tb_lab_laporan_produksi`.`tgl_uji`), `jenis_treatment` "
                    + "ORDER BY `tb_lab_laporan_produksi`.`tgl_uji` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            int i = 0;
            while (rs.next()) {
                if (bulan[i] != rs.getInt("bulan")) {
                    i++;
                }
                for (int j = 0; j < Table_data.getRowCount(); j++) {
                    if (rs.getString("jenis_treatment").equals(Table_data.getValueAt(j, 0).toString())) {
                        float nitrit = (rs.getFloat("nitrit_utuh") + rs.getFloat("nitrit_flat")) / 2.f;
                        model.setValueAt(decimalFormat.format(nitrit), j, i + 1);
                    }
                }
            }
        } catch (SQLException e) {
            Logger.getLogger(JFrame_Treatment_Cuci.class.getName()).log(Level.SEVERE, null, e);
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
                {"W1", null, null, null, null, null, null, null, null, null, null, null, null},
                {"KRT W1", null, null, null, null, null, null, null, null, null, null, null, null},
                {"W2", null, null, null, null, null, null, null, null, null, null, null, null},
                {"KRT W2", null, null, null, null, null, null, null, null, null, null, null, null},
                {"W3", null, null, null, null, null, null, null, null, null, null, null, null},
                {"W3+", null, null, null, null, null, null, null, null, null, null, null, null},
                {"Lain2", null, null, null, null, null, null, null, null, null, null, null, null},
                {"-", null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Jenis Treatment", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"
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
            Table_data.getColumnModel().getColumn(0).setMinWidth(120);
        }

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel1.setText("Average Nitrite Levels Based on Washing Treatment (ppm)");

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
            java.util.logging.Logger.getLogger(JFrame_Treatment_Cuci.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JFrame_Treatment_Cuci.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JFrame_Treatment_Cuci.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JFrame_Treatment_Cuci.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JFrame_Treatment_Cuci chart = new JFrame_Treatment_Cuci();
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
