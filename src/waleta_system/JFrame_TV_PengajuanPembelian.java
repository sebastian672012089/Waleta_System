package waleta_system;

import java.awt.Color;
import java.awt.Component;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;

public class JFrame_TV_PengajuanPembelian extends javax.swing.JFrame {

    String sql = null;
    ResultSet rs;
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    DecimalFormat decimalFormat = new DecimalFormat();
    static Timer t;

    public JFrame_TV_PengajuanPembelian() {
        initComponents();
        try {
            Utility.db.connect();
            TimerTask timer;
            timer = new TimerTask() {
                @Override
                public void run() {
                    refreshTable();
                    label_waktu_update.setText(new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").format(new Date()));
                }
            };
            t = new Timer();
            t.schedule(timer, 100, 120000);//120 detik / 2 menit
        } catch (Exception ex) {
            Logger.getLogger(JFrame_TV_PengajuanPembelian.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable() {
        try {
            int jumlah_Diajukan = 0, jumlah_Diketahui = 0, jumlah_Disetujui = 0, jumlah_Proses = 0, jumlah_Dikirim = 0, jumlah_Sampai = 0;
            DefaultTableModel model = (DefaultTableModel) table_pengajuan.getModel();
            model.setRowCount(0);
            sql = "SELECT `no`, `tanggal_pengajuan`, `tb_aset_pengajuan`.`departemen`, `keperluan`, `nama_barang`, `jumlah`, `link_pembelian`, `dibutuhkan_tanggal`, `nama_pegawai` AS 'diajukan', `diketahui_kadep`, `diketahui`, `disetujui`, `diproses`, `jenis_pembelian`, `tb_aset_pengajuan`.`status`, `tb_aset_pengajuan`.`keterangan`, "
                    + "DATEDIFF(CURRENT_DATE, `tanggal_pengajuan`) AS 'lama_proses' "
                    + "FROM `tb_aset_pengajuan` \n"
                    + "LEFT JOIN `tb_karyawan` ON `tb_aset_pengajuan`.`diajukan` = `tb_karyawan`.`id_pegawai` \n"
                    + "WHERE \n"
                    + "`tb_aset_pengajuan`.`status` NOT IN ('Selesai', 'dibatalkan') "
                    + "ORDER BY `tanggal_pengajuan` ";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[10];
            while (rs.next()) {
                row[0] = rs.getDate("tanggal_pengajuan");
                row[1] = rs.getString("departemen");
                row[2] = rs.getString("keperluan");
                row[3] = rs.getString("nama_barang");
                row[4] = rs.getInt("jumlah");
                row[5] = rs.getDate("dibutuhkan_tanggal");
                row[6] = rs.getString("diajukan");
                row[7] = rs.getString("status");
                row[8] = rs.getInt("lama_proses");
                model.addRow(row);
                if (rs.getString("status").equals("Diajukan")) {
                    jumlah_Diajukan++;
                } else if (rs.getString("status").equals("Diketahui")) {
                    jumlah_Diketahui++;
                } else if (rs.getString("status").equals("Disetujui")) {
                    jumlah_Disetujui++;
                } else if (rs.getString("status").equals("Proses")) {
                    jumlah_Proses++;
                } else if (rs.getString("status").equals("Dikirim")) {
                    jumlah_Dikirim++;
                } else if (rs.getString("status").equals("Sampai")) {
                    jumlah_Sampai++;
                }
            }
            ColumnsAutoSizer.sizeColumnsToFit(table_pengajuan);
            label_total_data_nota.setText(decimalFormat.format(table_pengajuan.getRowCount()));
            label_jumlah_diajukan.setText(decimalFormat.format(jumlah_Diajukan));
            label_jumlah_diketahui.setText(decimalFormat.format(jumlah_Diketahui));
            label_jumlah_disetujui.setText(decimalFormat.format(jumlah_Disetujui));
            label_jumlah_proses.setText(decimalFormat.format(jumlah_Proses));
            label_jumlah_dikirim.setText(decimalFormat.format(jumlah_Dikirim));
            label_jumlah_sampai.setText(decimalFormat.format(jumlah_Sampai));
            
            table_pengajuan.setDefaultRenderer(Integer.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if ((int) table_pengajuan.getValueAt(row, 8) > 20) {
                        if (isSelected) {
                            comp.setBackground(table_pengajuan.getSelectionBackground());
                            comp.setForeground(table_pengajuan.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.red);
                            comp.setForeground(Color.WHITE);
                        }
                    } else if ((int) table_pengajuan.getValueAt(row, 8) > 13) {
                        if (isSelected) {
                            comp.setBackground(table_pengajuan.getSelectionBackground());
                            comp.setForeground(table_pengajuan.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.orange);
                            comp.setForeground(Color.BLACK);
                        }
                    } else if ((int) table_pengajuan.getValueAt(row, 8) > 6) {
                        if (isSelected) {
                            comp.setBackground(table_pengajuan.getSelectionBackground());
                            comp.setForeground(table_pengajuan.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.yellow);
                            comp.setForeground(Color.BLACK);
                        }
                    } else {
                        if (isSelected) {
                            comp.setBackground(table_pengajuan.getSelectionBackground());
                            comp.setForeground(table_pengajuan.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.cyan);
                            comp.setForeground(Color.BLACK);
                        }
                    }
                    return comp;
                }
            });
            table_pengajuan.repaint();
            
            table_pengajuan.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if ((int) table_pengajuan.getValueAt(row, 8) > 20) {
                        if (isSelected) {
                            comp.setBackground(table_pengajuan.getSelectionBackground());
                            comp.setForeground(table_pengajuan.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.red);
                            comp.setForeground(Color.WHITE);
                        }
                    } else if ((int) table_pengajuan.getValueAt(row, 8) > 13) {
                        if (isSelected) {
                            comp.setBackground(table_pengajuan.getSelectionBackground());
                            comp.setForeground(table_pengajuan.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.orange);
                            comp.setForeground(Color.BLACK);
                        }
                    } else if ((int) table_pengajuan.getValueAt(row, 8) > 6) {
                        if (isSelected) {
                            comp.setBackground(table_pengajuan.getSelectionBackground());
                            comp.setForeground(table_pengajuan.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.yellow);
                            comp.setForeground(Color.BLACK);
                        }
                    } else {
                        if (isSelected) {
                            comp.setBackground(table_pengajuan.getSelectionBackground());
                            comp.setForeground(table_pengajuan.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.cyan);
                            comp.setForeground(Color.BLACK);
                        }
                    }
                    return comp;
                }
            });
            table_pengajuan.repaint();
            
            table_pengajuan.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if ((int) table_pengajuan.getValueAt(row, 8) > 20) {
                        if (isSelected) {
                            comp.setBackground(table_pengajuan.getSelectionBackground());
                            comp.setForeground(table_pengajuan.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.red);
                            comp.setForeground(Color.WHITE);
                        }
                    } else if ((int) table_pengajuan.getValueAt(row, 8) > 13) {
                        if (isSelected) {
                            comp.setBackground(table_pengajuan.getSelectionBackground());
                            comp.setForeground(table_pengajuan.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.orange);
                            comp.setForeground(Color.BLACK);
                        }
                    } else if ((int) table_pengajuan.getValueAt(row, 8) > 6) {
                        if (isSelected) {
                            comp.setBackground(table_pengajuan.getSelectionBackground());
                            comp.setForeground(table_pengajuan.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.yellow);
                            comp.setForeground(Color.BLACK);
                        }
                    } else {
                        if (isSelected) {
                            comp.setBackground(table_pengajuan.getSelectionBackground());
                            comp.setForeground(table_pengajuan.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.cyan);
                            comp.setForeground(Color.BLACK);
                        }
                    }
                    return comp;
                }
            });
            table_pengajuan.repaint();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JFrame_TV_PengajuanPembelian.class.getName()).log(Level.SEVERE, null, ex);
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

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table_pengajuan = new javax.swing.JTable();
        jLabel16 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        label_total_data_nota = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        label_waktu_update = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        label_jumlah_diketahui = new javax.swing.JLabel();
        label_jumlah_disetujui = new javax.swing.JLabel();
        label_jumlah_proses = new javax.swing.JLabel();
        label_jumlah_dikirim = new javax.swing.JLabel();
        label_jumlah_sampai = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        label_jumlah_diajukan = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        table_pengajuan.setAutoCreateRowSorter(true);
        table_pengajuan.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        table_pengajuan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tgl Pengajuan", "Departemen", "Keperluan", "Nama Barang", "Jumlah", "Dibutuhkan Tgl", "Diajukan Oleh", "Status", "Keterangan"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table_pengajuan.setRowHeight(24);
        table_pengajuan.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(table_pengajuan);

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel16.setText("Pengajuan Pembelian Alat Kerja");

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel4.setText("Total Data :");

        label_total_data_nota.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_nota.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_total_data_nota.setText("0000");

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel5.setText("Update time :");

        label_waktu_update.setBackground(new java.awt.Color(255, 255, 255));
        label_waktu_update.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_waktu_update.setText("<time>");

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel6.setText("Sampai :");

        label_jumlah_diketahui.setBackground(new java.awt.Color(255, 255, 255));
        label_jumlah_diketahui.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_jumlah_diketahui.setText("00");

        label_jumlah_disetujui.setBackground(new java.awt.Color(255, 255, 255));
        label_jumlah_disetujui.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_jumlah_disetujui.setText("00");

        label_jumlah_proses.setBackground(new java.awt.Color(255, 255, 255));
        label_jumlah_proses.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_jumlah_proses.setText("00");

        label_jumlah_dikirim.setBackground(new java.awt.Color(255, 255, 255));
        label_jumlah_dikirim.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_jumlah_dikirim.setText("00");

        label_jumlah_sampai.setBackground(new java.awt.Color(255, 255, 255));
        label_jumlah_sampai.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_jumlah_sampai.setText("00");

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel7.setText("Diketahui :");

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel8.setText("Disetujui :");

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel9.setText("Proses :");

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel10.setText("Dikirim :");

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel11.setText("Diajukan :");

        label_jumlah_diajukan.setBackground(new java.awt.Color(255, 255, 255));
        label_jumlah_diajukan.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_jumlah_diajukan.setText("00");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1346, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_waktu_update))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_data_nota)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_jumlah_diajukan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_jumlah_diketahui)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_jumlah_disetujui)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_jumlah_proses)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_jumlah_dikirim)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_jumlah_sampai)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_waktu_update, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(label_jumlah_dikirim, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_jumlah_proses, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_jumlah_diajukan, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_jumlah_disetujui, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_jumlah_diketahui, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_jumlah_sampai, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_data_nota, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 620, Short.MAX_VALUE)
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
        t.cancel();
    }//GEN-LAST:event_formWindowClosed

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
            java.util.logging.Logger.getLogger(JFrame_TV_PengajuanPembelian.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JFrame_TV_PengajuanPembelian.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JFrame_TV_PengajuanPembelian.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JFrame_TV_PengajuanPembelian.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new JFrame_TV_PengajuanPembelian().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel label_jumlah_diajukan;
    private javax.swing.JLabel label_jumlah_diketahui;
    private javax.swing.JLabel label_jumlah_dikirim;
    private javax.swing.JLabel label_jumlah_disetujui;
    private javax.swing.JLabel label_jumlah_proses;
    private javax.swing.JLabel label_jumlah_sampai;
    private javax.swing.JLabel label_total_data_nota;
    private javax.swing.JLabel label_waktu_update;
    public static javax.swing.JTable table_pengajuan;
    // End of variables declaration//GEN-END:variables
}
