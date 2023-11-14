package waleta_system.Panel_produksi;

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
import waleta_system.Interface.InterfacePanel;

public class JPanel_DataKinerjaF2 extends javax.swing.JPanel implements InterfacePanel {

    
    String sql = null;
    ResultSet rs;
    PreparedStatement pst;
    Date date = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    DecimalFormat decimalFormat = new DecimalFormat();

    @Override
    public void init() {
        try {
            ComboBox_status_karyawan.removeAllItems();
            ComboBox_status_karyawan.addItem("All");
            sql = "SELECT DISTINCT(`status`) AS 'status' FROM `tb_karyawan` WHERE `status` IS NOT NULL";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_status_karyawan.addItem(rs.getString("status"));
            }
            ComboBox_status_karyawan.setSelectedItem("IN");
            
            ComboBox_Ruang_LP.removeAllItems();
            ComboBox_Ruang_LP.addItem("All");
            sql = "SELECT DISTINCT(`ruangan`) AS 'ruangan' FROM `tb_laporan_produksi` WHERE 1";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_Ruang_LP.addItem(rs.getString("ruangan"));
            }

//            refreshTable();
        } catch (Exception ex) {
            Logger.getLogger(JPanel_DataKinerjaF2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable() {
        try {
            float total_lp = 0;
            decimalFormat.setMaximumFractionDigits(2);
            DefaultTableModel model = (DefaultTableModel) table_data_kinerja_f2.getModel();
            model.setRowCount(0);
            String ruangan = " AND `tb_laporan_produksi`.`ruangan` = '" + ComboBox_Ruang_LP.getSelectedItem().toString() + "'";
            if ("All".equals(ComboBox_Ruang_LP.getSelectedItem().toString())) {
                ruangan = "";
            }

            String Status = "AND `tb_karyawan`.`status` = '" + ComboBox_status_karyawan.getSelectedItem().toString() + "'";
            if (Status.equals("All")) {
                Status = "";
            }

            if (Date1.getDate() != null && Date2.getDate() != null) {
                sql = "SELECT `id_pegawai`, `nama_pegawai`, `tb_bagian`.`nama_bagian`, `lp_koreksi`, `gram_koreksi`, `lp_f1`, `gram_f1`, `lp_f2`, `gram_f2`, `lp_setor`, `gram_setor`, `lp_timbang`, `gram_timbang`\n"
                        + "FROM `tb_karyawan` \n"
                        + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`\n"
                        + "LEFT JOIN \n"
                        + "(SELECT `pekerja_koreksi_kering`, COUNT(`tb_finishing_2`.`no_laporan_produksi`) AS 'lp_koreksi', SUM(`tb_laporan_produksi`.`berat_basah`) AS 'gram_koreksi'\n"
                        + "FROM `tb_finishing_2` LEFT JOIN `tb_laporan_produksi` ON `tb_finishing_2`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                        + "WHERE `tb_finishing_2`.`tgl_setor_f2` BETWEEN '" + dateFormat.format(Date1.getDate()) + "' AND '" + dateFormat.format(Date2.getDate()) + "' " + ruangan + "\n"
                        + "GROUP BY `pekerja_koreksi_kering`) \n"
                        + "koreksi ON `tb_karyawan`.`nama_pegawai` = koreksi.`pekerja_koreksi_kering`\n"
                        + "LEFT JOIN \n"
                        + "(SELECT `pekerja_f1`, COUNT(`tb_finishing_2`.`no_laporan_produksi`) AS 'lp_f1', SUM(`tb_laporan_produksi`.`berat_basah`) AS 'gram_f1'\n"
                        + "FROM `tb_finishing_2` LEFT JOIN `tb_laporan_produksi` ON `tb_finishing_2`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                        + "WHERE `tb_finishing_2`.`tgl_setor_f2` BETWEEN '" + dateFormat.format(Date1.getDate()) + "' AND '" + dateFormat.format(Date2.getDate()) + "' " + ruangan + "\n"
                        + "GROUP BY `pekerja_f1`) \n"
                        + "f1 ON `tb_karyawan`.`nama_pegawai` = f1.`pekerja_f1`\n"
                        + "LEFT JOIN \n"
                        + "(SELECT `pekerja_f2`, COUNT(`tb_finishing_2`.`no_laporan_produksi`) AS 'lp_f2', SUM(`tb_laporan_produksi`.`berat_basah`) AS 'gram_f2'\n"
                        + "FROM `tb_finishing_2` LEFT JOIN `tb_laporan_produksi` ON `tb_finishing_2`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                        + "WHERE `tb_finishing_2`.`tgl_setor_f2` BETWEEN '" + dateFormat.format(Date1.getDate()) + "' AND '" + dateFormat.format(Date2.getDate()) + "' " + ruangan + "\n"
                        + "GROUP BY `pekerja_f2`) \n"
                        + "f2 ON `tb_karyawan`.`nama_pegawai` = f2.`pekerja_f2`\n"
                        + "LEFT JOIN \n"
                        + "(SELECT `f2_disetor`, COUNT(`tb_finishing_2`.`no_laporan_produksi`) AS 'lp_setor', SUM(`tb_laporan_produksi`.`berat_basah`) AS 'gram_setor'\n"
                        + "FROM `tb_finishing_2` LEFT JOIN `tb_laporan_produksi` ON `tb_finishing_2`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                        + "WHERE `tb_finishing_2`.`tgl_setor_f2` BETWEEN '" + dateFormat.format(Date1.getDate()) + "' AND '" + dateFormat.format(Date2.getDate()) + "' " + ruangan + "\n"
                        + "GROUP BY `f2_disetor`) \n"
                        + "setor ON `tb_karyawan`.`nama_pegawai` = setor.`f2_disetor`\n"
                        + "LEFT JOIN \n"
                        + "(SELECT `f2_timbang`, COUNT(`tb_finishing_2`.`no_laporan_produksi`) AS 'lp_timbang', SUM(`tb_laporan_produksi`.`berat_basah`) AS 'gram_timbang'\n"
                        + "FROM `tb_finishing_2` LEFT JOIN `tb_laporan_produksi` ON `tb_finishing_2`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                        + "WHERE `tb_finishing_2`.`tgl_setor_f2` BETWEEN '" + dateFormat.format(Date1.getDate()) + "' AND '" + dateFormat.format(Date2.getDate()) + "' " + ruangan + "\n"
                        + "GROUP BY `f2_timbang`) \n"
                        + "timbang ON `tb_karyawan`.`nama_pegawai` = timbang.`f2_timbang`\n"
                        + "WHERE (`gram_koreksi` > 0 OR `gram_f1` > 0 OR `gram_f2` > 0 OR `gram_setor` > 0 OR `gram_timbang` > 0) AND "
                        + "`nama_pegawai` LIKE '%" + txt_search_nama.getText() + "%' " + Status;
                pst = Utility.db.getConnection().prepareStatement(sql);
                rs = pst.executeQuery();
                Object[] row = new Object[11];
                while (rs.next()) {
                    row[0] = rs.getString("id_pegawai");
                    row[1] = rs.getString("nama_pegawai");
                    row[2] = rs.getString("nama_bagian");
                    row[3] = rs.getInt("lp_koreksi");
                    row[4] = rs.getFloat("gram_koreksi");
                    row[5] = rs.getInt("lp_f1");
                    row[6] = rs.getFloat("gram_f1");
                    row[7] = rs.getInt("lp_f2");
                    row[8] = rs.getInt("gram_f2");
                    row[9] = rs.getInt("lp_setor");
                    row[10] = rs.getFloat("gram_setor");
                    model.addRow(row);
                    ColumnsAutoSizer.sizeColumnsToFit(table_data_kinerja_f2);
                }
                label_total_lp.setText(decimalFormat.format(total_lp));
                label_total_pencetak.setText(Integer.toString(table_data_kinerja_f2.getRowCount()));
            } else {
                JOptionPane.showMessageDialog(this, "Filter tanggal harus terisi!");
            }
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_DataKinerjaF2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public JPanel_DataKinerjaF2() {
        initComponents();
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
        Date1 = new com.toedter.calendar.JDateChooser();
        Date2 = new com.toedter.calendar.JDateChooser();
        ComboBox_Ruang_LP = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        table_data_kinerja_f2 = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        label_total_lp = new javax.swing.JLabel();
        label_total_cabutan2 = new javax.swing.JLabel();
        label_total_pencetak = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txt_search_nama = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        ComboBox_status_karyawan = new javax.swing.JComboBox<>();

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Data Kinerja F2", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N

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
        jLabel2.setText("Filter Tanggal :");

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("Sampai");

        button_export.setBackground(new java.awt.Color(255, 255, 255));
        button_export.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export.setText("Export to Excel");
        button_export.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_exportActionPerformed(evt);
            }
        });

        Date1.setBackground(new java.awt.Color(255, 255, 255));
        Date1.setDateFormatString("dd MMMM yyyy");
        Date1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Date2.setBackground(new java.awt.Color(255, 255, 255));
        Date2.setDateFormatString("dd MMMM yyyy");
        Date2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        ComboBox_Ruang_LP.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_Ruang_LP.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "A", "B", "C", "D" }));

        table_data_kinerja_f2.setAutoCreateRowSorter(true);
        table_data_kinerja_f2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_data_kinerja_f2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Pegawai", "Nama", "Bagian", "LP KK", "Gr KK", "LP F1", "Gr F1", "LP F2", "Gr F2", "LP Final", "Gr Final"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
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
        table_data_kinerja_f2.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(table_data_kinerja_f2);

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel1.setText("Total LP dicetak :");

        label_total_lp.setBackground(new java.awt.Color(255, 255, 255));
        label_total_lp.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_lp.setText("88");

        label_total_cabutan2.setBackground(new java.awt.Color(255, 255, 255));
        label_total_cabutan2.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_cabutan2.setText("Orang");

        label_total_pencetak.setBackground(new java.awt.Color(255, 255, 255));
        label_total_pencetak.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_pencetak.setText("88");

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel10.setText("Total Pekerja :");

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel4.setText("Ruangan :");

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel5.setText("Search Nama :");

        txt_search_nama.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel6.setText("Status :");

        ComboBox_status_karyawan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_status_karyawan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "IN", "OUT" }));
        ComboBox_status_karyawan.setSelectedIndex(1);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1336, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(label_total_lp)
                            .addComponent(label_total_pencetak))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_cabutan2, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_nama, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date1, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date2, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_Ruang_LP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_status_karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_search)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_export)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(ComboBox_status_karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(button_search, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(ComboBox_Ruang_LP, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_export, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_search_nama, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 576, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_cabutan2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_pencetak, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
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
        DefaultTableModel modelA = (DefaultTableModel) table_data_kinerja_f2.getModel();
        ExportToExcel.writeToExcel(modelA, jPanel2);
    }//GEN-LAST:event_button_exportActionPerformed

    private void button_searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_searchActionPerformed
        // TODO add your handling code here:
        refreshTable();
    }//GEN-LAST:event_button_searchActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_Ruang_LP;
    private javax.swing.JComboBox<String> ComboBox_status_karyawan;
    private com.toedter.calendar.JDateChooser Date1;
    private com.toedter.calendar.JDateChooser Date2;
    private javax.swing.JButton button_export;
    private javax.swing.JButton button_search;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel label_total_cabutan2;
    private javax.swing.JLabel label_total_lp;
    private javax.swing.JLabel label_total_pencetak;
    private javax.swing.JTable table_data_kinerja_f2;
    private javax.swing.JTextField txt_search_nama;
    // End of variables declaration//GEN-END:variables

}
