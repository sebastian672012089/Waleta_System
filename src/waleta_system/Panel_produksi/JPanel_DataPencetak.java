package waleta_system.Panel_produksi;

import waleta_system.Class.Utility;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import waleta_system.Class.ColumnsAutoSizer;

import waleta_system.Class.ExportToExcel;
import waleta_system.Interface.InterfacePanel;

public class JPanel_DataPencetak extends javax.swing.JPanel implements InterfacePanel {

    
    String sql = null;
    ResultSet rs;
    Date date = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    DecimalFormat decimalFormat = new DecimalFormat();
    
    
    @Override
    public void init() {
        try {
            
            
            refreshTable();
        } catch (Exception ex) {
            Logger.getLogger(JPanel_DataPencetak.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void show_data_pencetak() {
        try {
            float total_lp = 0;
            decimalFormat.setMaximumFractionDigits(2);
            DefaultTableModel model = (DefaultTableModel) table_data_pencetak.getModel();
            if ("All".equals(ComboBox_ruangan.getSelectedItem().toString())) {
                if (Date1.getDate() != null && Date2.getDate() != null) {
                    sql = "SELECT `tb_cetak`.`cetak_dikerjakan`, `tb_karyawan`.`nama_pegawai`, `tb_karyawan`.`desa`, `tb_bagian`.`nama_bagian`, COUNT(`tb_cetak`.`no_laporan_produksi`) AS 'total_lp', SUM(`tb_laporan_produksi`.`jumlah_keping`) AS 'total_keping', SUM(`tb_laporan_produksi`.`berat_basah`) AS 'total_gram'\n"
                            + "FROM `tb_cetak` \n"
                            + "LEFT JOIN `tb_karyawan` ON `tb_cetak`.`cetak_dikerjakan` = `tb_karyawan`.`id_pegawai`\n"
                            + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`\n"
                            + "LEFT JOIN `tb_laporan_produksi` ON `tb_cetak`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                            + "WHERE `tb_cetak`.`tgl_selesai_cetak` BETWEEN '" + dateFormat.format(Date1.getDate()) + "' AND '" + dateFormat.format(Date2.getDate()) + "' \n"
                            + "GROUP BY `tb_cetak`.`cetak_dikerjakan`";
                } else {
                    sql = "SELECT `tb_cetak`.`cetak_dikerjakan`, `tb_karyawan`.`nama_pegawai`, `tb_karyawan`.`desa`, `tb_bagian`.`nama_bagian`, COUNT(`tb_cetak`.`no_laporan_produksi`) AS 'total_lp', SUM(`tb_laporan_produksi`.`jumlah_keping`) AS 'total_keping', SUM(`tb_laporan_produksi`.`berat_basah`) AS 'total_gram'\n"
//                    sql = "SELECT `tb_cetak`.`cetak_dikerjakan`, COUNT(`tb_cetak`.`no_laporan_produksi`) AS 'total_lp', SUM(`tb_laporan_produksi`.`jumlah_keping`) AS 'total_keping', SUM(`tb_laporan_produksi`.`berat_basah`) AS 'total_gram'\n"
                            + "FROM `tb_cetak` \n"
                            + "LEFT JOIN `tb_karyawan` ON `tb_cetak`.`cetak_dikerjakan` = `tb_karyawan`.`id_pegawai`\n"
                            + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`\n"
                            + "LEFT JOIN `tb_laporan_produksi` ON `tb_cetak`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                            + "WHERE `tb_cetak`.`tgl_selesai_cetak` IS NOT NULL\n"
                            + "GROUP BY `tb_cetak`.`cetak_dikerjakan`";
                }
            } else {
                if (Date1.getDate() != null && Date2.getDate() != null) {
                    sql = "SELECT `tb_cetak`.`cetak_dikerjakan`, `tb_karyawan`.`nama_pegawai`, `tb_karyawan`.`desa`, `tb_bagian`.`nama_bagian`, COUNT(`tb_cetak`.`no_laporan_produksi`) AS 'total_lp', SUM(`tb_laporan_produksi`.`jumlah_keping`) AS 'total_keping', SUM(`tb_laporan_produksi`.`berat_basah`) AS 'total_gram'\n"
                            + "FROM `tb_cetak` \n"
                            + "LEFT JOIN `tb_karyawan` ON `tb_cetak`.`cetak_dikerjakan` = `tb_karyawan`.`id_pegawai`\n"
                            + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`\n"
                            + "LEFT JOIN `tb_laporan_produksi` ON `tb_cetak`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                            + "WHERE (`tb_cetak`.`tgl_selesai_cetak` BETWEEN '" + dateFormat.format(Date1.getDate()) + "' AND '" + dateFormat.format(Date2.getDate()) + "')"
                            + "AND `tb_laporan_produksi`.`ruangan` = '"+ComboBox_ruangan.getSelectedItem().toString()+"' \n"
                            + "GROUP BY `tb_cetak`.`cetak_dikerjakan`";
                } else {
                    sql = "SELECT `tb_cetak`.`cetak_dikerjakan`, `tb_karyawan`.`nama_pegawai`, `tb_karyawan`.`desa`, `tb_bagian`.`nama_bagian`, COUNT(`tb_cetak`.`no_laporan_produksi`) AS 'total_lp', SUM(`tb_laporan_produksi`.`jumlah_keping`) AS 'total_keping', SUM(`tb_laporan_produksi`.`berat_basah`) AS 'total_gram'\n"
                            + "FROM `tb_cetak` \n"
                            + "LEFT JOIN `tb_karyawan` ON `tb_cetak`.`cetak_dikerjakan` = `tb_karyawan`.`id_pegawai`\n"
                            + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`\n"
                            + "LEFT JOIN `tb_laporan_produksi` ON `tb_cetak`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                            + "WHERE `tb_cetak`.`tgl_selesai_cetak` IS NOT NULL AND `tb_laporan_produksi`.`ruangan` = '"+ComboBox_ruangan.getSelectedItem().toString()+"'\n"
                            + "GROUP BY `tb_cetak`.`cetak_dikerjakan`";
                }
            }
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[7];
            while (rs.next()) {
                row[0] = rs.getString("cetak_dikerjakan");
                row[1] = rs.getString("nama_pegawai");
                row[2] = rs.getString("desa");
                row[3] = rs.getString("nama_bagian");
                row[4] = rs.getInt("total_lp");
                total_lp = total_lp + rs.getFloat("total_lp");
                row[5] = rs.getInt("total_keping");
                row[6] = rs.getFloat("total_gram");
                model.addRow(row);
            }
            label_total_lp.setText(decimalFormat.format(total_lp));
            label_total_pencetak.setText(Integer.toString(table_data_pencetak.getRowCount()));
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_DataPencetak.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable() {
        DefaultTableModel model = (DefaultTableModel) table_data_pencetak.getModel();
        model.setRowCount(0);
        show_data_pencetak();
        ColumnsAutoSizer.sizeColumnsToFit(table_data_pencetak);

        TableAlignment.setHorizontalAlignment(JLabel.CENTER);
        //tabel Data Bahan Baku
        for (int i = 0; i < table_data_pencetak.getColumnCount(); i++) {
            table_data_pencetak.getColumnModel().getColumn(i).setCellRenderer(TableAlignment);
        }
    }

    public JPanel_DataPencetak() {
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
        ComboBox_ruangan = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        table_data_pencetak = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        label_total_lp = new javax.swing.JLabel();
        label_total_cabutan2 = new javax.swing.JLabel();
        label_total_pencetak = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Data Kinerja Cetak", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N

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

        ComboBox_ruangan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_ruangan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "A", "B", "C", "D" }));

        table_data_pencetak.setAutoCreateRowSorter(true);
        table_data_pencetak.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_data_pencetak.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Pegawai", "Nama", "Desa", "Bagian", "Jumlah LP", "Total Kpg", "Total gram"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(table_data_pencetak);

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

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
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
                        .addComponent(ComboBox_ruangan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_search)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 687, Short.MAX_VALUE)
                        .addComponent(button_export)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(button_search, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(ComboBox_ruangan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_export, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
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
        DefaultTableModel modelA = (DefaultTableModel) table_data_pencetak.getModel();
        ExportToExcel.writeToExcel(modelA, jPanel2);
    }//GEN-LAST:event_button_exportActionPerformed

    private void button_searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_searchActionPerformed
        // TODO add your handling code here:
        refreshTable();
    }//GEN-LAST:event_button_searchActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_ruangan;
    private com.toedter.calendar.JDateChooser Date1;
    private com.toedter.calendar.JDateChooser Date2;
    private javax.swing.JButton button_export;
    private javax.swing.JButton button_search;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel label_total_cabutan2;
    private javax.swing.JLabel label_total_lp;
    private javax.swing.JLabel label_total_pencetak;
    private javax.swing.JTable table_data_pencetak;
    // End of variables declaration//GEN-END:variables

}
