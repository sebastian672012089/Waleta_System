package waleta_system.Panel_produksi;

import java.awt.event.KeyEvent;
import waleta_system.Class.Utility;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.ExportToExcel;

public class JPanel_DataKinerjaCuci_HC_Kopyok extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    PreparedStatement pst;
    Date date = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    DecimalFormat decimalFormat = new DecimalFormat();

    public JPanel_DataKinerjaCuci_HC_Kopyok() {
        initComponents();
    }

    public void init() {
        refreshTable();
    }

    public void refreshTable() {
        try {
            if (Date_SetorCabut1.getDate() != null && Date_SetorCabut2.getDate() != null) {
                decimalFormat.setMaximumFractionDigits(2);
                float hari_kerja = Utility.countDaysWithoutFreeDays(Date_SetorCabut1.getDate(), Date_SetorCabut2.getDate()) + 1;
                label_hari_kerja.setText(decimalFormat.format(hari_kerja));

                DefaultTableModel model = (DefaultTableModel) table_data.getModel();
                model.setRowCount(0);

                sql = "SELECT \n"
                        + "    `nama_pekerja`, \n"
                        + "    SUM(IF(`jenis` = 'HC', `bobot_lp`, 0)) AS 'bobot_lp_hc',\n"
                        + "    SUM(IF(`jenis` = 'KOPYOK', `bobot_lp`, 0)) AS 'bobot_lp_kopyok'\n"
                        + "FROM \n"
                        + "    (\n"
                        + "        SELECT \n"
                        + "            `pekerja_hancuran` AS 'nama_pekerja', \n"
                        + "            SUM(`tb_laporan_produksi`.`keping_upah` / `tb_tarif_cabut`.`kpg_lp`) AS 'bobot_lp', 'HC' AS 'jenis' \n"
                        + "        FROM \n"
                        + "            `tb_cabut` \n"
                        + "        LEFT JOIN \n"
                        + "            `tb_laporan_produksi` ON `tb_cabut`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                        + "        LEFT JOIN \n"
                        + "            `tb_tarif_cabut` ON `tb_laporan_produksi`.`jenis_bulu_lp` = `tb_tarif_cabut`.`bulu_upah`\n"
                        + "        WHERE \n"
                        + "            `tgl_setor_cabut` IS NOT NULL \n"
                        + "            AND `tgl_setor_cabut` BETWEEN '" + dateFormat.format(Date_SetorCabut1.getDate()) + "' AND '" + dateFormat.format(Date_SetorCabut2.getDate()) + "' \n"
                        + "            AND `pekerja_hancuran` IS NOT NULL \n"
                        + "            AND `pekerja_hancuran` NOT IN ('', '-')\n"
                        + "        GROUP BY \n"
                        + "            `pekerja_hancuran`\n"
                        + "UNION ALL\n"
                        + "        SELECT \n"
                        + "            `pekerja_kopyok` AS 'nama_pekerja', \n"
                        + "            SUM(`tb_laporan_produksi`.`keping_upah` / `tb_tarif_cabut`.`kpg_lp`) AS 'bobot_lp', 'KOPYOK' AS 'jenis'  \n"
                        + "        FROM \n"
                        + "            `tb_cabut` \n"
                        + "        LEFT JOIN \n"
                        + "            `tb_laporan_produksi` ON `tb_cabut`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                        + "        LEFT JOIN \n"
                        + "            `tb_tarif_cabut` ON `tb_laporan_produksi`.`jenis_bulu_lp` = `tb_tarif_cabut`.`bulu_upah`\n"
                        + "        WHERE \n"
                        + "            `tgl_setor_cabut` IS NOT NULL \n"
                        + "            AND `tgl_setor_cabut` BETWEEN '" + dateFormat.format(Date_SetorCabut1.getDate()) + "' AND '" + dateFormat.format(Date_SetorCabut2.getDate()) + "' \n"
                        + "            AND `pekerja_kopyok` IS NOT NULL \n"
                        + "            AND `pekerja_kopyok` NOT IN ('', '-')\n"
                        + "        GROUP BY \n"
                        + "            `pekerja_kopyok`\n"
                        + "    ) data_hc_kopyok\n"
                        + "WHERE \n"
                        + "`nama_pekerja` LIKE '%" + txt_search_nama.getText() + "%' \n"
                        + "GROUP BY `nama_pekerja`";
                pst = Utility.db.getConnection().prepareStatement(sql);
                rs = pst.executeQuery();
                while (rs.next()) {
                    Object[] row = new Object[11];
                    String Query = "SELECT `id_pegawai`, `nama_pegawai`, `nama_bagian`, `level_gaji` "
                            + "FROM `tb_karyawan` "
                            + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian` "
                            + "WHERE "
                            + "`nama_pegawai` = '" + rs.getString("nama_pekerja") + "'"
                            + "AND `nama_bagian` LIKE '%" + txt_search_bagian.getText() + "%'"
                            + "ORDER BY `tanggal_masuk` DESC LIMIT 1";
                    ResultSet result = Utility.db.getStatement().executeQuery(Query);
                    while (result.next()) {
                        row[0] = result.getString("id_pegawai");
                        row[1] = result.getString("nama_pegawai");
                        row[2] = result.getString("nama_bagian");
                        row[3] = Math.round(rs.getDouble("bobot_lp_hc") * 100d) / 100d;
                        row[4] = Math.round(rs.getDouble("bobot_lp_kopyok") * 100d) / 100d;
                        row[5] = result.getString("level_gaji");
                        model.addRow(row);
                    }
                }
                ColumnsAutoSizer.sizeColumnsToFit(table_data);
                label_total_data.setText(decimalFormat.format(table_data.getRowCount()));
            } else {
                JOptionPane.showMessageDialog(this, "Range tanggal harus dipilih!");
            }
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_DataKinerjaCuci_HC_Kopyok.class.getName()).log(Level.SEVERE, null, ex);
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
        button_search = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        button_export = new javax.swing.JButton();
        Date_SetorCabut1 = new com.toedter.calendar.JDateChooser();
        Date_SetorCabut2 = new com.toedter.calendar.JDateChooser();
        jScrollPane1 = new javax.swing.JScrollPane();
        table_data = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        label_total_data = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txt_search_nama = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txt_search_bagian = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        label_hari_kerja = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        button_search.setBackground(new java.awt.Color(255, 255, 255));
        button_search.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search.setText("Search");
        button_search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_searchActionPerformed(evt);
            }
        });

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("Tanggal Setor Cabut :");

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("-");

        button_export.setBackground(new java.awt.Color(255, 255, 255));
        button_export.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export.setText("Export to Excel");
        button_export.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_exportActionPerformed(evt);
            }
        });

        Date_SetorCabut1.setBackground(new java.awt.Color(255, 255, 255));
        Date_SetorCabut1.setDate(new Date());
        Date_SetorCabut1.setDateFormatString("dd MMMM yyyy");
        Date_SetorCabut1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Date_SetorCabut2.setBackground(new java.awt.Color(255, 255, 255));
        Date_SetorCabut2.setDate(new Date());
        Date_SetorCabut2.setDateFormatString("dd MMMM yyyy");
        Date_SetorCabut2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        table_data.setAutoCreateRowSorter(true);
        table_data.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_data.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Pegawai", "Nama", "Bagian", "Tot LP HC", "Tot LP Kopyok", "Level"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table_data.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(table_data);

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("Total Data :");

        label_total_data.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_data.setText("88");

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel5.setText("Nama :");

        txt_search_nama.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_nama.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_namaKeyPressed(evt);
            }
        });

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel7.setText("Bagian :");

        txt_search_bagian.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_bagian.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_bagianKeyPressed(evt);
            }
        });

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel6.setText("Data Kinerja Cuci");

        label_hari_kerja.setBackground(new java.awt.Color(255, 255, 255));
        label_hari_kerja.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_hari_kerja.setText("88");

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel4.setText("Hari Kerja :");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1346, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_data)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_hari_kerja))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_nama, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_bagian, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_SetorCabut1, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_SetorCabut2, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_search)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_export))
                            .addComponent(jLabel6))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(button_search, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_export, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_search_nama, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_search_bagian, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_SetorCabut1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_SetorCabut2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_hari_kerja, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_data, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void button_exportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_exportActionPerformed
        // TODO add your handling code here:
        DefaultTableModel modelA = (DefaultTableModel) table_data.getModel();
        ExportToExcel.writeToExcel(modelA, jPanel2);
    }//GEN-LAST:event_button_exportActionPerformed

    private void button_searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_searchActionPerformed
        // TODO add your handling code here:
        refreshTable();
    }//GEN-LAST:event_button_searchActionPerformed

    private void txt_search_namaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_namaKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_search_namaKeyPressed

    private void txt_search_bagianKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_bagianKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_search_bagianKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser Date_SetorCabut1;
    private com.toedter.calendar.JDateChooser Date_SetorCabut2;
    private javax.swing.JButton button_export;
    private javax.swing.JButton button_search;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel label_hari_kerja;
    private javax.swing.JLabel label_total_data;
    private javax.swing.JTable table_data;
    private javax.swing.JTextField txt_search_bagian;
    private javax.swing.JTextField txt_search_nama;
    // End of variables declaration//GEN-END:variables

}
