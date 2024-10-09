package waleta_system.BahanJadi;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import waleta_system.Browse_Karyawan;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;

public class JDialog_Terima_BahanJadi extends javax.swing.JDialog {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();

    public JDialog_Terima_BahanJadi(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        try {
            initComponents();
            refreshTable();
            tabel_belum_terimaGBJ.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent event) {
                    if (!event.getValueIsAdjusting() && tabel_belum_terimaGBJ.getSelectedRow() != -1) {
                        int i = tabel_belum_terimaGBJ.getSelectedRow();
                        if (i > -1) {
                            txt_kode_asal.setText(tabel_belum_terimaGBJ.getValueAt(i, 0).toString());
                            try {
                                Date tanggal = dateFormat.parse(tabel_belum_terimaGBJ.getValueAt(i, 1).toString());
                                Date_Masuk.setDate(tanggal);
                            } catch (ParseException ex) {
                                Logger.getLogger(JDialog_Terima_BahanJadi.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                }
            });
        } catch (Exception ex) {
            Logger.getLogger(JDialog_Terima_BahanJadi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable() {
        try {
            DefaultTableModel model = (DefaultTableModel) tabel_belum_terimaGBJ.getModel();
            model.setRowCount(0);
            Object[] row = new Object[2];
            sql = "SELECT `no_laporan_produksi`, `tgl_selesai`\n"
                    + "FROM `tb_lab_laporan_produksi` LEFT JOIN `tb_bahan_jadi_masuk` ON `tb_lab_laporan_produksi`.`no_laporan_produksi` = `tb_bahan_jadi_masuk`.`kode_asal`\n"
                    + "WHERE `tgl_selesai` IS NOT NULL AND `tb_bahan_jadi_masuk`.`tanggal_masuk` IS NULL ORDER BY `tgl_selesai`";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                row[0] = rs.getString("no_laporan_produksi");
                row[1] = rs.getDate("tgl_selesai");
                model.addRow(row);
            }
            sql = "SELECT `kode_pembelian_bahan_jadi`, `tanggal_pembelian` "
                    + "FROM `tb_pembelian_barang_jadi` LEFT JOIN `tb_bahan_jadi_masuk` ON `tb_pembelian_barang_jadi`.`kode_pembelian_bahan_jadi` = `tb_bahan_jadi_masuk`.`kode_asal` "
                    + "WHERE `tb_bahan_jadi_masuk`.`kode_asal` IS NULL "
                    + "ORDER BY `tb_pembelian_barang_jadi`.`tanggal_pembelian` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                row[0] = rs.getString("kode_pembelian_bahan_jadi");
                row[1] = rs.getDate("tanggal_pembelian");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(tabel_belum_terimaGBJ);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JDialog_Terima_BahanJadi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txt_kode_asal = new javax.swing.JTextField();
        txt_penerima = new javax.swing.JTextField();
        Date_Masuk = new com.toedter.calendar.JDateChooser();
        button_penerima = new javax.swing.JButton();
        button_insert = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabel_belum_terimaGBJ = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Bahan Jadi Masuk");

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel4.setText("Kode_Asal :");

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel6.setText("Tanggal Masuk :");

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel7.setText("Diterima Oleh :");

        txt_kode_asal.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_kode_asal.setText("WL-");

        txt_penerima.setEditable(false);
        txt_penerima.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Date_Masuk.setBackground(new java.awt.Color(255, 255, 255));
        Date_Masuk.setDateFormatString("dd MMMM yyyy");
        Date_Masuk.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Date_Masuk.setMaxSelectableDate(new Date());

        button_penerima.setBackground(new java.awt.Color(255, 255, 255));
        button_penerima.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_penerima.setText("...");
        button_penerima.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_penerimaActionPerformed(evt);
            }
        });

        button_insert.setBackground(new java.awt.Color(255, 255, 255));
        button_insert.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_insert.setText("Insert");
        button_insert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_insertActionPerformed(evt);
            }
        });

        tabel_belum_terimaGBJ.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tabel_belum_terimaGBJ.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No LP", "Setor QC"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabel_belum_terimaGBJ.getTableHeader().setReorderingAllowed(false);
        tabel_belum_terimaGBJ.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabel_belum_terimaGBJMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tabel_belum_terimaGBJ);

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel5.setText("LP Selesai QC, Belum Terima GBJ");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(button_insert))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txt_kode_asal)
                            .addComponent(Date_Masuk, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(txt_penerima, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(button_penerima, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_kode_asal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_Masuk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_penerima, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_penerima, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_insert, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void button_penerimaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_penerimaActionPerformed
        // TODO add your handling code here:
        Browse_Karyawan dialog = new Browse_Karyawan(new javax.swing.JFrame(), true, null);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);

        int x = Browse_Karyawan.table_list_karyawan.getSelectedRow();
        if (x != -1) {
            txt_penerima.setText(Browse_Karyawan.table_list_karyawan.getValueAt(x, 1).toString());
        }
    }//GEN-LAST:event_button_penerimaActionPerformed

    private void button_insertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_insertActionPerformed
        // TODO add your handling code here:
        String kode_asal = null;
        int keping = 0, berat = 0;
        Date tgl_setor = null;
        Boolean Check = true;
        try {
            String CHECK_1 = "SELECT `kode_pembelian_bahan_jadi`, `jumlah_keping`, `berat`, `tanggal_pembelian` "
                    + "FROM `tb_pembelian_barang_jadi` "
                    + "WHERE `kode_pembelian_bahan_jadi` = '" + txt_kode_asal.getText() + "'";
            ResultSet rst_pembelian = Utility.db.getStatement().executeQuery(CHECK_1);
            if (rst_pembelian.next()) {
                kode_asal = rst_pembelian.getString("kode_pembelian_bahan_jadi");
                keping = rst_pembelian.getInt("jumlah_keping");
                berat = rst_pembelian.getInt("berat");
                tgl_setor = rst_pembelian.getDate("tanggal_pembelian");
            } else {
                String CHECK_2 = "SELECT `tb_finishing_2`.`no_laporan_produksi`, `tgl_setor_f2`, "
                        + "`fnol_f2`+`fbonus_f2`+`pecah_f2`+`flat_f2`+`jidun_utuh_f2`+`jidun_pecah_f2` AS 'jumlah_keping', "
                        + "`sesekan`+`hancuran`+`rontokan`+`bonggol`+`serabut` AS 'berat_BP', "
                        + "`harga_baku_lp` "
                        + "FROM `tb_finishing_2` "
                        + "LEFT JOIN `tb_laporan_produksi` ON `tb_finishing_2`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi` "
                        + "WHERE `tb_finishing_2`.`no_laporan_produksi` = '" + txt_kode_asal.getText() + "'";
                PreparedStatement pst = Utility.db.getConnection().prepareStatement(CHECK_2);
                ResultSet rst_lp = pst.executeQuery();
                if (rst_lp.next()) {
                    kode_asal = rst_lp.getString("no_laporan_produksi");
                    keping = rst_lp.getInt("jumlah_keping");
                    berat = rst_lp.getInt("berat_BP");
                    tgl_setor = rst_lp.getDate("tgl_setor_f2");

                    String qc = "SELECT `tgl_selesai`, `gram_akhir` FROM `tb_lab_laporan_produksi` WHERE `no_laporan_produksi` = '" + kode_asal + "'";
                    ResultSet rst_qc = Utility.db.getStatement().executeQuery(qc);
                    if (rst_qc.next()) {
                        if (rst_qc.getDate("tgl_selesai") == null) {
                            JOptionPane.showMessageDialog(this, "Maaf, LP belum selesai QC");
                            Check = false;
                        } else {
                            berat = berat + rst_qc.getInt("gram_akhir");
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Maaf, LP belum masuk QC");
                        Check = false;
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Maaf, Data LP atau Data Pembelian Barang Jadi tidak ditemukan !");
                    Check = false;
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_BahanJadiMasuk.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (Check) {
            if (tgl_setor.after(Date_Masuk.getDate())) {
                JOptionPane.showMessageDialog(this, "Tanggal masuk harus setelah tanggal setor f2 (LP) atau tanggal pembelian Bahan Jadi");
            } else {
                try {
                    String Query = "INSERT INTO `tb_bahan_jadi_masuk`(`kode_asal`, `tanggal_masuk`, `diterima_oleh`, `keping`, `berat`) "
                            + "VALUES ('" + kode_asal + "','" + dateFormat.format(Date_Masuk.getDate()) + "','" + txt_penerima.getText() + "','" + keping + "','" + berat + "')";
                    Utility.db.getConnection().createStatement();
                    if ((Utility.db.getStatement().executeUpdate(Query)) > 0) {
                        JOptionPane.showMessageDialog(this, "data insert Successfully");
                        this.dispose();
                    } else {
                        JOptionPane.showMessageDialog(this, "data failed");
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(JDialog_Terima_BahanJadi.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_button_insertActionPerformed

    private void tabel_belum_terimaGBJMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabel_belum_terimaGBJMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            int i = tabel_belum_terimaGBJ.getSelectedRow();
            txt_kode_asal.setText(tabel_belum_terimaGBJ.getValueAt(i, 0).toString());
        }
    }//GEN-LAST:event_tabel_belum_terimaGBJMouseClicked

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JDialog_Terima_BahanJadi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JDialog_Terima_BahanJadi dialog = new JDialog_Terima_BahanJadi(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser Date_Masuk;
    private javax.swing.JButton button_insert;
    private javax.swing.JButton button_penerima;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tabel_belum_terimaGBJ;
    private javax.swing.JTextField txt_kode_asal;
    private javax.swing.JTextField txt_penerima;
    // End of variables declaration//GEN-END:variables
}
