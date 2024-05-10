package waleta_system;

import java.awt.Color;
import java.awt.Component;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;

public class JFrame_IjinBelumKembali extends javax.swing.JFrame {

    String sql;
    ResultSet rs;
    Thread thread;
    boolean time_thread = true;

    public JFrame_IjinBelumKembali() {
        this.setUndecorated(true);
        initComponents();
        refreshTable();

        time_thread = true;
        thread = new Thread() {
            @Override
            public void run() {
                while (time_thread) {
                    try {
                        refreshTable();
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(JFrame_IjinBelumKembali.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        };
        thread.start();
    }

    public void refreshTable() {
        try {
            LocalDateTime myDateObj = LocalDateTime.now();
            DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss");

            String formattedDate = myDateObj.format(myFormatObj);
            label_jam.setText(formattedDate);

            DefaultTableModel model_kanan = (DefaultTableModel) Table_Belum_Kembali.getModel();
            model_kanan.setRowCount(0);
            sql = "SELECT `no`, `tb_ijin_keluar_ruangan`.`id_pegawai`, `tb_karyawan`.`nama_pegawai`, `nama_bagian`, `waktu_ijin`, `waktu_kembali`, `keterangan_ijin`,"
                    + "TIME_TO_SEC(TIMEDIFF(NOW(), `waktu_ijin`)) AS 'detik' \n"
                    + "FROM `tb_ijin_keluar_ruangan` \n"
                    + "LEFT JOIN `tb_karyawan` ON `tb_ijin_keluar_ruangan`.`id_pegawai` = `tb_karyawan`.`id_pegawai`\n"
                    + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`\n"
                    + "WHERE DATE(`waktu_ijin`) = CURRENT_DATE AND `waktu_kembali` IS NULL ORDER BY `waktu_ijin` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[10];
            while (rs.next()) {
                row[0] = rs.getString("waktu_ijin");
                row[1] = rs.getString("nama_pegawai");
                row[2] = rs.getString("nama_bagian");
                row[3] = rs.getString("keterangan_ijin");
                row[4] = rs.getFloat("detik") / 60f;
                model_kanan.addRow(row);
            }
            Table_Belum_Kembali.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if ((float) Table_Belum_Kembali.getValueAt(row, 4) > 10f) {
                        if (isSelected) {
                            comp.setBackground(Color.gray);
                            comp.setForeground(Color.red);
                        } else {
                            comp.setBackground(Color.red);
                            comp.setForeground(Color.white);
                        }
                    } else {
                        if (isSelected) {
                            comp.setBackground(Table_Belum_Kembali.getSelectionBackground());
                            comp.setForeground(Table_Belum_Kembali.getSelectionForeground());
                        } else {
                            comp.setBackground(Table_Belum_Kembali.getBackground());
                            comp.setForeground(Table_Belum_Kembali.getForeground());
                        }
                    }
                    return comp;
                }
            });
            Table_Belum_Kembali.repaint();
            ColumnsAutoSizer.sizeColumnsToFit(Table_Belum_Kembali);
            label_total_data_belum_kembali.setText(Integer.toString(Table_Belum_Kembali.getRowCount()));
        } catch (SQLException ex) {
            Logger.getLogger(JFrame_IjinBelumKembali.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        Table_Belum_Kembali = new javax.swing.JTable();
        Jlabel6 = new javax.swing.JLabel();
        label_total_data_belum_kembali = new javax.swing.JLabel();
        Jlabel7 = new javax.swing.JLabel();
        label_jam = new javax.swing.JLabel();
        Button_close = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Scan Ijin ke Toilet");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Table_Belum_Kembali.setFont(new java.awt.Font("Arial Narrow", 1, 42)); // NOI18N
        Table_Belum_Kembali.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Jam Keluar", "Nama", "Bagian", "Keterangan", "Menit"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class
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
        Table_Belum_Kembali.setRowHeight(38);
        jScrollPane2.setViewportView(Table_Belum_Kembali);
        if (Table_Belum_Kembali.getColumnModel().getColumnCount() > 0) {
            Table_Belum_Kembali.getColumnModel().getColumn(1).setMaxWidth(500);
            Table_Belum_Kembali.getColumnModel().getColumn(2).setMaxWidth(500);
        }

        Jlabel6.setBackground(new java.awt.Color(255, 255, 255));
        Jlabel6.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        Jlabel6.setText("Data Belum Kembali");
        Jlabel6.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        Jlabel6.setMaximumSize(new java.awt.Dimension(280, 15));

        label_total_data_belum_kembali.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_belum_kembali.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        label_total_data_belum_kembali.setText("0");
        label_total_data_belum_kembali.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        label_total_data_belum_kembali.setMaximumSize(new java.awt.Dimension(280, 15));

        Jlabel7.setBackground(new java.awt.Color(255, 255, 255));
        Jlabel7.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        Jlabel7.setText("Total Data :");
        Jlabel7.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        Jlabel7.setMaximumSize(new java.awt.Dimension(280, 15));

        label_jam.setBackground(new java.awt.Color(255, 255, 255));
        label_jam.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        label_jam.setText("dd MMMM yyyy HH:mm");
        label_jam.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        label_jam.setMaximumSize(new java.awt.Dimension(280, 15));

        Button_close.setBackground(new java.awt.Color(255, 255, 255));
        Button_close.setFont(new java.awt.Font("Arial Narrow", 1, 12)); // NOI18N
        Button_close.setText("CLOSE");
        Button_close.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_closeActionPerformed(evt);
            }
        });

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 0, 0));
        jLabel1.setText("Warna merah (untuk durasi lebih dari 10 menit)");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1346, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(Jlabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Jlabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_data_belum_kembali, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(label_jam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Button_close))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(Jlabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_data_belum_kembali, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_jam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(Button_close))
                    .addComponent(Jlabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 656, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
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

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        // TODO add your handling code here:
        time_thread = false;
    }//GEN-LAST:event_formWindowClosed

    private void Button_closeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_closeActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_Button_closeActionPerformed

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(JFrame_IjinBelumKembali.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JFrame_IjinBelumKembali.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JFrame_IjinBelumKembali.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JFrame_IjinBelumKembali.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new JFrame_IjinBelumKembali().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Button_close;
    private javax.swing.JLabel Jlabel6;
    private javax.swing.JLabel Jlabel7;
    private javax.swing.JTable Table_Belum_Kembali;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel label_jam;
    private javax.swing.JLabel label_total_data_belum_kembali;
    // End of variables declaration//GEN-END:variables
}
