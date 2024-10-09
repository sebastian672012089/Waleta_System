package waleta_system.Packing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;

public class JFrame_TV_SPK2 extends javax.swing.JFrame {

    String sql = null;
    ResultSet rs;
    PreparedStatement pst;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    Thread clock, refreshTable_SPK;
    boolean clock_thread, refreshTable_SPK_thread;

    int[] R = new int[]{102, 153, 153, 204, 255, 255, 255, 255, 255, 204, 153, 102};
    int[] G = new int[]{255, 255, 255, 255, 255, 204, 153, 153, 153, 153, 204, 204};
    int[] B = new int[]{255, 204, 153, 153, 153, 153, 153, 204, 255, 255, 255, 255};
    int spk = 0, baris = -1, check = 0;

    public JFrame_TV_SPK2() {
        initComponents();
    }

    public void init() {
        Utility.db.connect();
        clock_thread = true;
        refreshTable_SPK_thread = true;
        clock = new Thread() {
            @Override
            public void run() {
                try {
                    while (true) {
                        refresh_JAM();
                        sleep(1000);
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(JFrame_TV_SPK2.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        clock.start();
        
        refreshTable_SPK = new Thread() {
            @Override
            public void run() {
                try {
                    while (true) {
                        refreshTable_SPK();
                        sleep(60000);
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(JFrame_TV_SPK2.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        refreshTable_SPK.start();
    }

    public void refresh_JAM() {
        LocalDateTime myDateObj = LocalDateTime.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss");
        String formattedDate = myDateObj.format(myFormatObj);
        label_jam.setText(formattedDate);
    }

    public void refreshTable_SPK() {
        try {
            DefaultTableModel model1 = (DefaultTableModel) Tabel_Detail_SPK.getModel();
            model1.setRowCount(0);
            DefaultTableModel model2 = (DefaultTableModel) Tabel_Detail_SPK1.getModel();
            model2.setRowCount(0);
            int row_counter1 = 0, row_counter2 = 0;
            sql = "SELECT `tb_spk`.`kode_spk`, `tb_buyer`.`nama`, `tanggal_spk`, `tanggal_tk`, `detail`, \n"
                    + "(SELECT SUM(`tb_spk_detail`.`berat`) FROM `tb_spk_detail` WHERE `kode_spk` = `tb_spk`.`kode_spk`) AS 'berat', \n"
                    + "SUM(`tb_box_bahan_jadi`.`berat`) AS 'progress'\n"
                    + "FROM `tb_spk` \n"
                    + "LEFT JOIN `tb_buyer` ON `tb_spk`.`buyer` = `tb_buyer`.`kode_buyer`\n"
                    + "LEFT JOIN `tb_spk_detail` ON `tb_spk`.`kode_spk` = `tb_spk_detail`.`kode_spk`\n"
                    + "LEFT JOIN `tb_box_packing` ON `tb_spk_detail`.`no` = `tb_box_packing`.`no_grade_spk` \n"
                    + "LEFT JOIN `tb_box_bahan_jadi` ON `tb_box_packing`.`no_box` = `tb_box_bahan_jadi`.`no_box` \n"
                    + "WHERE "
                    + "`tb_spk`.`status` = 'PROSES' \n"
                    + "AND `tb_spk`.`kode_spk` NOT IN ('Sample Internal')\n"
                    + "AND `tb_spk`.`kode_spk` NOT LIKE ('LOK-%')\n"
                    + "GROUP BY `kode_spk` \n"
                    + "ORDER BY `tanggal_tk` DESC";
//            System.out.println(sql);
            pst = Utility.db.getConnection().prepareStatement(sql);
            rs = pst.executeQuery();
            Object[] row = new Object[8];
            decimalFormat.setMaximumFractionDigits(1);
            while (rs.next()) {
                row[0] = rs.getString("kode_spk");
                row[1] = rs.getString("nama");
                row[2] = rs.getDate("tanggal_tk");
                row[3] = null;
                row[4] = decimalFormat.format(rs.getFloat("berat"));
                row[5] = null;
                row[6] = decimalFormat.format(rs.getFloat("progress"));
//                row[7] = Math.round((rs.getFloat("progress") / rs.getFloat("berat")) * 1000.f) / 10.f;
                row[7] = decimalFormat.format((rs.getFloat("progress") / rs.getFloat("berat")) * 100.d);
//                System.out.println(row_counter1);
                if (row_counter1 < 26) {
                    model1.addRow(row);
                    row_counter1++;
                } else {
                    model2.addRow(row);
                    row_counter2++;
                }

                String query_detail = "SELECT `no`, `grade_waleta`, `grade_buyer`, `berat_kemasan`, `jumlah_kemasan`, `tb_spk_detail`.`berat`, `harga_cny`, `keterangan`, `kode_spk`, "
                        + "SUM(IF((`tb_box_packing`.`status` <> 'PENDING' OR `tb_box_packing`.`status` IS NULL), `tb_box_bahan_jadi`.`berat`, 0)) AS 'progress'"
                        + "FROM `tb_spk_detail` "
                        + "LEFT JOIN `tb_box_packing` ON `tb_spk_detail`.`no` = `tb_box_packing`.`no_grade_spk` "
                        + "LEFT JOIN `tb_box_bahan_jadi` ON `tb_box_bahan_jadi`.`no_box` = `tb_box_packing`.`no_box` "
                        + "WHERE `kode_spk` = '" + rs.getString("kode_spk") + "' "
                        + "GROUP BY `no`";
//                System.out.println(query_detail);
                PreparedStatement psts = Utility.db.getConnection().prepareStatement(query_detail);
                ResultSet rst = psts.executeQuery();
                while (rst.next()) {
                    row[0] = rst.getString("grade_waleta");
                    row[1] = rst.getString("grade_buyer");
                    row[2] = rst.getInt("berat_kemasan");
                    row[3] = decimalFormat.format(rst.getFloat("jumlah_kemasan"));
                    row[4] = decimalFormat.format(rst.getFloat("berat"));
                    row[5] = rst.getString("keterangan");
                    row[6] = decimalFormat.format(rst.getFloat("progress"));
//                    row[7] = Math.round((rst.getFloat("progress") / rst.getFloat("berat")) * 1000.f) / 10.f;
                    if (rst.getFloat("berat") > 0) {
                        row[7] = decimalFormat.format((rst.getFloat("progress") / rst.getFloat("berat")) * 100.d);
                    } else {
                        row[7] = "-";
                    }
                    if (row_counter1 < 26) {
                        model1.addRow(row);
                        row_counter1++;
                    } else {
                        model2.addRow(row);
                        row_counter2++;
                    }
                }
//                if (row_counter1 < 27) {
//                    model1.addRow(new Object[]{});
//                    row_counter1++;
//                } else {
//                    model2.addRow(new Object[]{});
//                    row_counter2++;
//                }
            }

            Tabel_Detail_SPK.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if (row == 0) {
                        check = 1;
                    } else {
                        check = 0;
                    }
                    if (Tabel_Detail_SPK.getValueAt(row, 3) == null) {
                        if (baris != row) {
                            if (check == 1) {
                                spk = 0;
                            }
                            baris = row;
                            spk++;
//                            System.out.println("spk = " + spk);
//                            System.out.println("row = " + row);
                        }
                        comp.setFont(new Font("Arial", Font.BOLD, 16));
                    }
                    if (Tabel_Detail_SPK.getValueAt(row, 2) != null) {
                        if (isSelected) {
                            comp.setBackground(Tabel_Detail_SPK.getSelectionBackground());
                            comp.setForeground(Tabel_Detail_SPK.getSelectionForeground());
                        } else {
                            comp.setBackground(new Color(R[spk], G[spk], B[spk]));
                            comp.setForeground(Color.BLACK);
                        }
                    } else {
                        if (isSelected) {
                            comp.setBackground(Tabel_Detail_SPK.getSelectionBackground());
                            comp.setForeground(Tabel_Detail_SPK.getSelectionForeground());
                        } else {
                            comp.setBackground(Tabel_Detail_SPK.getBackground());
                            comp.setForeground(Tabel_Detail_SPK.getForeground());
                        }
                    }
                    return comp;
                }
            });
            Tabel_Detail_SPK.repaint();
            Tabel_Detail_SPK1.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if (row == 0) {
                        check = 1;
                    } else {
                        check = 0;
                    }
                    if (Tabel_Detail_SPK1.getValueAt(row, 3) == null) {
                        if (baris != row) {
                            if (check == 1) {
                                spk = 0;
                            }
                            baris = row;
                            spk++;
//                            System.out.println("spk = " + spk);
//                            System.out.println("row = " + row);
                        }
                        comp.setFont(new Font("Arial", Font.BOLD, 16));
                    }
                    if (Tabel_Detail_SPK1.getValueAt(row, 2) != null) {
                        if (isSelected) {
                            comp.setBackground(Tabel_Detail_SPK1.getSelectionBackground());
                            comp.setForeground(Tabel_Detail_SPK1.getSelectionForeground());
                        } else {
                            comp.setBackground(new Color(R[spk], G[spk], B[spk]));
                            comp.setForeground(Color.BLACK);
                        }
                    } else {
                        if (isSelected) {
                            comp.setBackground(Tabel_Detail_SPK1.getSelectionBackground());
                            comp.setForeground(Tabel_Detail_SPK1.getSelectionForeground());
                        } else {
                            comp.setBackground(Tabel_Detail_SPK1.getBackground());
                            comp.setForeground(Tabel_Detail_SPK1.getForeground());
                        }
                    }
                    return comp;
                }
            });
            Tabel_Detail_SPK1.repaint();

            ColumnsAutoSizer.sizeColumnsToFit(Tabel_Detail_SPK);
            ColumnsAutoSizer.sizeColumnsToFit(Tabel_Detail_SPK1);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JFrame_TV_SPK2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        Tabel_Detail_SPK = new javax.swing.JTable();
        label_jam = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        Tabel_Detail_SPK1 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Surat Perintah Kerja");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        Tabel_Detail_SPK.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        Tabel_Detail_SPK.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Grade WLT", "Grade Buyer", "Berat / Kemasan", "Pack", "Net Weight", "Ket.", "Turun", "%"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
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
        Tabel_Detail_SPK.setRowHeight(25);
        Tabel_Detail_SPK.setRowSelectionAllowed(false);
        Tabel_Detail_SPK.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(Tabel_Detail_SPK);
        if (Tabel_Detail_SPK.getColumnModel().getColumnCount() > 0) {
            Tabel_Detail_SPK.getColumnModel().getColumn(1).setMaxWidth(150);
        }

        label_jam.setBackground(new java.awt.Color(255, 255, 255));
        label_jam.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        label_jam.setText("Jam :");

        Tabel_Detail_SPK1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        Tabel_Detail_SPK1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Grade WLT", "Grade Buyer", "Berat / Kemasan", "Pack", "Net Weight", "Ket.", "Turun", "%"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
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
        Tabel_Detail_SPK1.setRowHeight(25);
        Tabel_Detail_SPK1.setRowSelectionAllowed(false);
        Tabel_Detail_SPK1.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(Tabel_Detail_SPK1);
        if (Tabel_Detail_SPK1.getColumnModel().getColumnCount() > 0) {
            Tabel_Detail_SPK1.getColumnModel().getColumn(1).setMaxWidth(150);
        }

        jButton1.setText("Refresh");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 701, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 686, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label_jam)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_jam, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 693, Short.MAX_VALUE)
                    .addComponent(jScrollPane2)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        // TODO add your handling code here:
        clock_thread = false;
        refreshTable_SPK_thread = false;
    }//GEN-LAST:event_formWindowClosed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        refreshTable_SPK();
    }//GEN-LAST:event_jButton1ActionPerformed

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JFrame_TV_SPK2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame_TV_SPK2 chart = new JFrame_TV_SPK2();
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
    private javax.swing.JTable Tabel_Detail_SPK;
    private javax.swing.JTable Tabel_Detail_SPK1;
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel label_jam;
    // End of variables declaration//GEN-END:variables
}
