package waleta_system.HRD;

import waleta_system.Class.Utility;
import java.awt.HeadlessException;
import java.awt.event.KeyEvent;
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
import waleta_system.Class.Utility;

public class JPanel_DataTemanBawaTeman extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DecimalFormat decimalFormat = new DecimalFormat();
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();

    public void init() {
        decimalFormat = Utility.DecimalFormatUS(decimalFormat);
        refreshTable_tbt();
        refreshTable_top10();
    }

    public JPanel_DataTemanBawaTeman() {
        initComponents();
    }

    public void refreshTable_tbt() {
        try {
            String search_nama = "";
            if (ComboBox_SearchBy.getSelectedIndex() == 1) {
                search_nama = "C.`nama_pegawai` LIKE '%" + txt_search_nama.getText() + "%' ";
            } else if (ComboBox_SearchBy.getSelectedIndex() == 0) {
                search_nama = "B.`nama_pegawai` LIKE '%" + txt_search_nama.getText() + "%' ";
            }
            String filter_penggajian = "";
            if (Date_penggajian.getDate() != null) {
                filter_penggajian = "AND DATEDIFF(IF(C.`tanggal_keluar` IS NULL, '" + dateFormat.format(Date_penggajian.getDate()) + "', IF('" + dateFormat.format(Date_penggajian.getDate()) + "' > C.`tanggal_keluar`, C.`tanggal_keluar`, '" + dateFormat.format(Date_penggajian.getDate()) + "')), C.`tanggal_masuk`) > 20 "
                        + "AND DATEDIFF(IF(C.`tanggal_keluar` IS NULL, '" + dateFormat.format(Date_penggajian.getDate()) + "', IF('" + dateFormat.format(Date_penggajian.getDate()) + "' > C.`tanggal_keluar`, C.`tanggal_keluar`, '" + dateFormat.format(Date_penggajian.getDate()) + "')), C.`tanggal_masuk`) < 28 ";
            }
            DefaultTableModel model = (DefaultTableModel) Tabel_TBT.getModel();
            model.setRowCount(0);
            sql = "SELECT A.`id_pegawai` AS 'id1', B.`nama_pegawai` AS 'nama1', D.`nama_bagian` AS 'bagian1', "
                    + "A.`id_teman` AS 'id2', C.`nama_pegawai` AS 'nama2', E.`nama_bagian` AS 'bagian2', "
                    + "C.`tanggal_masuk`, C.`tanggal_keluar`, C.`status`, DATEDIFF(IF(C.`tanggal_keluar` IS NULL, CURRENT_DATE(), C.`tanggal_keluar`), C.`tanggal_masuk`) AS 'hari_kerja'\n"
                    + "FROM `tb_temanbawateman` AS A "
                    + "LEFT JOIN `tb_karyawan` AS B ON A.`id_pegawai` = B.`id_pegawai`\n"
                    + "LEFT JOIN `tb_karyawan` AS C ON A.`id_teman` = C.`id_pegawai`\n"
                    + "LEFT JOIN `tb_bagian` AS D ON D.`kode_bagian` = B.`kode_bagian`\n"
                    + "LEFT JOIN `tb_bagian` AS E ON E.`kode_bagian` = C.`kode_bagian`\n"
                    + "WHERE "
                    + search_nama
                    + filter_penggajian
                    + "ORDER BY C.`tanggal_masuk` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[10];
            while (rs.next()) {
                row[0] = rs.getString("id1");
                row[1] = rs.getString("nama1");
                row[2] = rs.getString("bagian1");
                row[3] = rs.getString("id2");
                row[4] = rs.getString("nama2");
                row[5] = rs.getString("bagian2");
                row[6] = rs.getDate("tanggal_masuk");
                row[7] = rs.getDate("tanggal_keluar");
                row[8] = rs.getString("status");
                row[9] = rs.getInt("hari_kerja");
                model.addRow(row);
            }
            int rowData = Tabel_TBT.getRowCount();
            label_total_data.setText(Integer.toString(rowData));
            ColumnsAutoSizer.sizeColumnsToFit(Tabel_TBT);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_Data_TglLibur.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_top10() {
        try {
            int peringkat = 1;
            DefaultTableModel model = (DefaultTableModel) Tabel_Top10.getModel();
            model.setRowCount(0);
            sql = "SELECT `tb_temanbawateman`.`id_pegawai`, `tb_karyawan`.`nama_pegawai`, COUNT(`tb_temanbawateman`.`id_teman`) AS 'jumlah'\n"
                    + "FROM `tb_temanbawateman` "
                    + "LEFT JOIN `tb_karyawan` ON `tb_temanbawateman`.`id_pegawai` = `tb_karyawan`.`id_pegawai`\n"
                    + "GROUP BY `tb_temanbawateman`.`id_pegawai` ORDER BY COUNT(`tb_temanbawateman`.`id_teman`) DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[9];
            while (rs.next()) {
                row[0] = peringkat;
                row[1] = rs.getString("id_pegawai");
                row[2] = rs.getString("nama_pegawai");
                row[3] = rs.getInt("jumlah");
                model.addRow(row);
                peringkat++;
            }

            ColumnsAutoSizer.sizeColumnsToFit(Tabel_Top10);

            TableAlignment.setHorizontalAlignment(JLabel.CENTER);
            //tabel Data Bahan Baku
            for (int i = 0; i < Tabel_Top10.getColumnCount(); i++) {
                Tabel_Top10.getColumnModel().getColumn(i).setCellRenderer(TableAlignment);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_Data_TglLibur.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        txt_search_nama = new javax.swing.JTextField();
        button_search = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        Tabel_TBT = new javax.swing.JTable();
        ComboBox_SearchBy = new javax.swing.JComboBox<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        Tabel_Top10 = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        label_total_data = new javax.swing.JLabel();
        button_export = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        button_new = new javax.swing.JButton();
        button_delete = new javax.swing.JButton();
        button_refreshTop10 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel3 = new javax.swing.JLabel();
        Date_penggajian = new com.toedter.calendar.JDateChooser();

        setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Data Teman Bawa Teman", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N

        txt_search_nama.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_nama.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_namaKeyPressed(evt);
            }
        });

        button_search.setBackground(new java.awt.Color(255, 255, 255));
        button_search.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search.setText("Refresh");
        button_search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_searchActionPerformed(evt);
            }
        });

        Tabel_TBT.setAutoCreateRowSorter(true);
        Tabel_TBT.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Tabel_TBT.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id Karyawan", "Nama", "Bagian", "Id Teman", "Nama Teman", "Bagian", "Tgl Masuk", "Tgl Keluar", "Status", "Hari"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Tabel_TBT.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(Tabel_TBT);

        ComboBox_SearchBy.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_SearchBy.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Nama Karyawan", "Nama Teman" }));

        Tabel_Top10.setAutoCreateRowSorter(true);
        Tabel_Top10.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Tabel_Top10.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Peringkat", "ID Pegawai", "Nama", "Jumlah TBT"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(Tabel_Top10);

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel1.setText("Total Data :");

        label_total_data.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_data.setText("0");

        button_export.setBackground(new java.awt.Color(255, 255, 255));
        button_export.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export.setText("Export");
        button_export.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_exportActionPerformed(evt);
            }
        });

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel2.setText("Top 10 TBT Terbanyak");

        button_new.setBackground(new java.awt.Color(255, 255, 255));
        button_new.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_new.setText("New");
        button_new.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_newActionPerformed(evt);
            }
        });

        button_delete.setBackground(new java.awt.Color(255, 255, 255));
        button_delete.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_delete.setText("Delete");
        button_delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_deleteActionPerformed(evt);
            }
        });

        button_refreshTop10.setBackground(new java.awt.Color(255, 255, 255));
        button_refreshTop10.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_refreshTop10.setText("Refresh");
        button_refreshTop10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refreshTop10ActionPerformed(evt);
            }
        });

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setText("TBT RULES :\n1. PIHAK PERTAMA adalah karyawan aktif bekerja di waleta (status IN), dapat membawa atau mengajak orang lain untuk bekerja di waleta, yang mana selanjutnya akan disebutkan sebagai PIHAK KEDUA.\n2. PIHAK KEDUA harus mendaftarkan nama PIHAK PERTAMA ke staff recruitment pada saat interview tahap pertama.\n3. PIHAK KEDUA mengikuti semua proses recruitment perusahaan secara normal tanpa ada tahapan yang dilewati dan dinyatakan diterima sebagai karyawan PT. WALETA.\n4. PIHAK KEDUA sekurang-kurangnya telah bekerja selama 3 minggu (21 hari).\n5. PIHAK PERTAMA akan mendapatkan bonus TBT di periode penggajian ketika PIHAK KEDUA telah genap bekerja 21 hari.\n6. BONUS TBT sebesar 20.000 rupiah akan diberikan ke PIHAK PERTAMA untuk setiap teman yang dibawa.\n7. BONUS TBT hanya berlaku jika teman yang dibawa masuk sebagai karyawan CABUT / CETAK.");
        jScrollPane3.setViewportView(jTextArea1);

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel3.setText("Tanggal penggajian :");

        Date_penggajian.setBackground(new java.awt.Color(255, 255, 255));
        Date_penggajian.setDateFormatString("dd MMM yyyy");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 878, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(ComboBox_SearchBy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_nama, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_penggajian, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_search)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_export)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_new)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_delete))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_data)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_refreshTop10))
                    .addComponent(jScrollPane3))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(txt_search_nama, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ComboBox_SearchBy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)
                            .addComponent(button_refreshTop10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Date_penggajian, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_search, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_export, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_new, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_delete, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(label_total_data))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 604, Short.MAX_VALUE)))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txt_search_namaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_namaKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_tbt();
        }
    }//GEN-LAST:event_txt_search_namaKeyPressed

    private void button_searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_searchActionPerformed
        refreshTable_tbt();
    }//GEN-LAST:event_button_searchActionPerformed

    private void button_exportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_exportActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Tabel_TBT.getModel();
        ExportToExcel.writeToExcel(model, jPanel1);
    }//GEN-LAST:event_button_exportActionPerformed

    private void button_newActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_newActionPerformed
        // TODO add your handling code here:
        JDialog_InsertTBT dialog = new JDialog_InsertTBT(new javax.swing.JFrame(), true);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);
        refreshTable_tbt();
        refreshTable_top10();
    }//GEN-LAST:event_button_newActionPerformed

    private void button_deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_deleteActionPerformed
        // TODO add your handling code here:
        try {
            int j = Tabel_TBT.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih data yang ingin di hapus !");
            } else {
                int dialogResult = JOptionPane.showConfirmDialog(this, "Yakin hapus data ini?", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    // delete code here
                    String Query = "DELETE FROM `tb_temanbawateman` WHERE `tb_temanbawateman`.`id_teman` = '" + Tabel_TBT.getValueAt(j, 3).toString() + "'";
                    Utility.db.getConnection().createStatement();
                    if ((Utility.db.getStatement().executeUpdate(Query)) == 1) {
                        JOptionPane.showMessageDialog(this, "data DELETE Successfully");
                    } else {
                        JOptionPane.showMessageDialog(this, "Delete FAILED");
                    }
                    refreshTable_tbt();
                    refreshTable_top10();
                }
            }
        } catch (SQLException | HeadlessException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(JPanel_DataTemanBawaTeman.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_deleteActionPerformed

    private void button_refreshTop10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refreshTop10ActionPerformed
        // TODO add your handling code here:
        refreshTable_top10();
    }//GEN-LAST:event_button_refreshTop10ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_SearchBy;
    private com.toedter.calendar.JDateChooser Date_penggajian;
    private javax.swing.JTable Tabel_TBT;
    private javax.swing.JTable Tabel_Top10;
    public static javax.swing.JButton button_delete;
    public static javax.swing.JButton button_export;
    public static javax.swing.JButton button_new;
    private javax.swing.JButton button_refreshTop10;
    public static javax.swing.JButton button_search;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JLabel label_total_data;
    private javax.swing.JTextField txt_search_nama;
    // End of variables declaration//GEN-END:variables
}
