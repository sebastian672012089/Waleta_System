package waleta_system.SubWaleta;

import waleta_system.Class.Utility;
import java.awt.HeadlessException;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.ExportToExcel;

public class JPanel_PiutangKaryawan_sub extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();

    public JPanel_PiutangKaryawan_sub() {
        initComponents();
    }

    public void init() {
        try {
            Utility.db_sub.connect();
            ComboBox_status_karyawan.removeAllItems();
            ComboBox_status_karyawan.addItem("All");
            sql = "SELECT DISTINCT(`status`) AS 'status' FROM `tb_karyawan` WHERE `status` IS NOT NULL";
            rs = Utility.db_sub.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_status_karyawan.addItem(rs.getString("status"));
            }
            ComboBox_status_karyawan.setSelectedItem("IN-SUB");

            ComboBox_bagian.removeAllItems();
            ComboBox_bagian.addItem("All");
            sql = "SELECT `kode_sub` FROM `tb_sub_waleta` WHERE 1";
            rs = Utility.db_sub.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_bagian.addItem(rs.getString("kode_sub"));
            }
            refreshTable();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_PiutangKaryawan_sub.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable() {
        try {
            String bagian = "AND `bagian` = '" + ComboBox_bagian.getSelectedItem().toString() + "' ";
            if ("All".equals(ComboBox_bagian.getSelectedItem().toString())) {
                bagian = "";
            }

            String Status = "AND `tb_karyawan`.`status` = '" + ComboBox_status_karyawan.getSelectedItem().toString() + "' ";
            if ("All".equals(ComboBox_status_karyawan.getSelectedItem().toString())) {
                Status = "";
            }

            DefaultTableModel model = (DefaultTableModel) Table_PiutangKaryawan.getModel();
            model.setRowCount(0);
            sql = "SELECT `no_piutang`, `tb_piutang_karyawan`.`id_pegawai`, `tanggal_piutang`, `tanggal_input`, `nominal_piutang`, `tb_piutang_karyawan`.`status` AS 'status_piutang', `tgl_lunas`, `tb_piutang_karyawan`.`keterangan`, "
                    + " `nama_pegawai`, `bagian`, `tb_karyawan`.`status`, `jenis_pegawai` "
                    + "FROM `tb_piutang_karyawan` "
                    + "LEFT JOIN `tb_karyawan` ON `tb_piutang_karyawan`.`id_pegawai` = `tb_karyawan`.`id_pegawai` "
                    + "WHERE `nama_pegawai` LIKE '%" + txt_nama.getText() + "%' "
                    + "AND `tb_piutang_karyawan`.`id_pegawai` LIKE '%" + txt_id.getText() + "%' "
                    + bagian
                    + Status
                    + "ORDER BY `tanggal_piutang` DESC";
            rs = Utility.db_sub.getStatement().executeQuery(sql);
            Object[] row = new Object[14];
            while (rs.next()) {
                row[0] = rs.getInt("no_piutang");
                row[1] = rs.getDate("tanggal_input");
                row[2] = rs.getString("id_pegawai");
                row[3] = rs.getString("nama_pegawai");
                row[4] = rs.getString("bagian");
                row[5] = rs.getString("jenis_pegawai");
                row[6] = rs.getString("status");
                row[7] = rs.getDate("tanggal_piutang");
                row[8] = rs.getInt("nominal_piutang");
                row[9] = rs.getString("keterangan");
                row[10] = rs.getString("status_piutang");
                row[11] = rs.getDate("tgl_lunas");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_PiutangKaryawan);

            int rowData = Table_PiutangKaryawan.getRowCount();
            label_total_data.setText(Integer.toString(rowData));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_PiutangKaryawan_sub.class.getName()).log(Level.SEVERE, null, ex);
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

        jPanel_Customer_baku = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        Table_PiutangKaryawan = new javax.swing.JTable();
        jPanel_search_karyawan = new javax.swing.JPanel();
        button_search = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        ComboBox_status_karyawan = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        txt_id = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txt_nama = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        ComboBox_bagian = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jDateChooser2 = new com.toedter.calendar.JDateChooser();
        button_insert_customer_baku = new javax.swing.JButton();
        button_delete_customer_baku = new javax.swing.JButton();
        button_export = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        label_total_data = new javax.swing.JLabel();
        button_input_csv = new javax.swing.JButton();

        jPanel_Customer_baku.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_Customer_baku.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Data Piutang Karyawan Sub", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N
        jPanel_Customer_baku.setPreferredSize(new java.awt.Dimension(1366, 701));

        Table_PiutangKaryawan.setAutoCreateRowSorter(true);
        Table_PiutangKaryawan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No Piutang", "Tgl Input", "ID Pegawai", "Nama", "Bagian", "Posisi", "Status", "Tgl Piutang", "Nominal", "Keterangan", "Status Pembayaran", "Tgl Pelunasan"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Float.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Table_PiutangKaryawan.getTableHeader().setReorderingAllowed(false);
        jScrollPane8.setViewportView(Table_PiutangKaryawan);

        jPanel_search_karyawan.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_search_karyawan.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));

        button_search.setBackground(new java.awt.Color(255, 255, 255));
        button_search.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search.setText("Search");
        button_search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_searchActionPerformed(evt);
            }
        });

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel11.setText("Status Karyawan :");

        ComboBox_status_karyawan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_status_karyawan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel8.setText("ID Pegawai :");

        txt_id.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_id.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_idKeyPressed(evt);
            }
        });

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("Nama :");

        txt_nama.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_nama.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_namaKeyPressed(evt);
            }
        });

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel6.setText("Bagian :");

        ComboBox_bagian.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_bagian.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel4.setText("Tanggal Piutang :");

        jDateChooser1.setBackground(new java.awt.Color(255, 255, 255));
        jDateChooser1.setDateFormatString("dd MMM yyyy");
        jDateChooser1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jDateChooser2.setBackground(new java.awt.Color(255, 255, 255));
        jDateChooser2.setDateFormatString("dd MMM yyyy");
        jDateChooser2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        javax.swing.GroupLayout jPanel_search_karyawanLayout = new javax.swing.GroupLayout(jPanel_search_karyawan);
        jPanel_search_karyawan.setLayout(jPanel_search_karyawanLayout);
        jPanel_search_karyawanLayout.setHorizontalGroup(
            jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_search_karyawanLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_id, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_nama, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jDateChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ComboBox_status_karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ComboBox_bagian, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_search)
                .addContainerGap(330, Short.MAX_VALUE))
        );
        jPanel_search_karyawanLayout.setVerticalGroup(
            jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_search_karyawanLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_id, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_nama, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jDateChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_status_karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ComboBox_bagian, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_search, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        button_insert_customer_baku.setBackground(new java.awt.Color(255, 255, 255));
        button_insert_customer_baku.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_insert_customer_baku.setText("NEW");
        button_insert_customer_baku.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_insert_customer_bakuActionPerformed(evt);
            }
        });

        button_delete_customer_baku.setBackground(new java.awt.Color(255, 255, 255));
        button_delete_customer_baku.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_delete_customer_baku.setText("DELETE");
        button_delete_customer_baku.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_delete_customer_bakuActionPerformed(evt);
            }
        });

        button_export.setBackground(new java.awt.Color(255, 255, 255));
        button_export.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export.setText("Export To Excel");
        button_export.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_exportActionPerformed(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel12.setText("Total Data :");

        label_total_data.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        label_total_data.setText("TOTAL");

        button_input_csv.setBackground(new java.awt.Color(255, 255, 255));
        button_input_csv.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_input_csv.setText("Input .CSV");
        button_input_csv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_input_csvActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_Customer_bakuLayout = new javax.swing.GroupLayout(jPanel_Customer_baku);
        jPanel_Customer_baku.setLayout(jPanel_Customer_bakuLayout);
        jPanel_Customer_bakuLayout.setHorizontalGroup(
            jPanel_Customer_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Customer_bakuLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_Customer_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel_search_karyawan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane8)
                    .addGroup(jPanel_Customer_bakuLayout.createSequentialGroup()
                        .addComponent(button_insert_customer_baku)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_delete_customer_baku)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_export)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_input_csv)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_data)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel_Customer_bakuLayout.setVerticalGroup(
            jPanel_Customer_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Customer_bakuLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel_search_karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_Customer_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(button_input_csv, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_data, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_delete_customer_baku, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_insert_customer_baku, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 578, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jPanel_Customer_baku, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jPanel_Customer_baku, javax.swing.GroupLayout.DEFAULT_SIZE, 700, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void button_insert_customer_bakuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_insert_customer_bakuActionPerformed
        // TODO add your handling code here:
        JDialog_InsertEdit_PiutangKaryawan_sub dialog = new JDialog_InsertEdit_PiutangKaryawan_sub(new javax.swing.JFrame(), true);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);
        dialog.setResizable(false);
        refreshTable();
    }//GEN-LAST:event_button_insert_customer_bakuActionPerformed

    private void button_delete_customer_bakuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_delete_customer_bakuActionPerformed
        // TODO add your handling code here:
        try {
            int j = Table_PiutangKaryawan.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih data yang ingin di hapus !");
            } else {
                int dialogResult = JOptionPane.showConfirmDialog(this, "yakin akan menghapus data ini?", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    // delete code here
                    String Query = "DELETE FROM `tb_piutang_karyawan` WHERE `no_piutang` = '" + Table_PiutangKaryawan.getValueAt(j, 0).toString() + "'";
                    Utility.db_sub.getConnection().createStatement();
                    if ((Utility.db_sub.getStatement().executeUpdate(Query)) == 1) {
                        JOptionPane.showMessageDialog(this, "data Saved !");
                        refreshTable();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed !");
                    }
                }
            }
        } catch (HeadlessException | SQLException e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_PiutangKaryawan_sub.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_delete_customer_bakuActionPerformed

    private void button_exportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_exportActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Table_PiutangKaryawan.getModel();
        ExportToExcel.writeToExcel(model, jPanel_Customer_baku);
    }//GEN-LAST:event_button_exportActionPerformed

    private void txt_namaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_namaKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_namaKeyPressed

    private void button_searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_searchActionPerformed
        // TODO add your handling code here:
        refreshTable();
    }//GEN-LAST:event_button_searchActionPerformed

    private void txt_idKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_idKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_idKeyPressed

    private void button_input_csvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_input_csvActionPerformed
        // TODO add your handling code here:
        try {
            int n = 0;
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Select CSV file to import!\n"
                    + "ID, Nama, Bagian, Tanggal, Nominal, Keterangan\n"
                    + "Pemisah kolom (koma ,)");
            int result = chooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                String filename1 = file.getAbsolutePath();
                try (BufferedReader br = new BufferedReader(new FileReader(filename1))) {
                    String line;
                    String Query = null;
                    try {
                        Utility.db_sub.connect();
                        Utility.db_sub.getConnection().setAutoCommit(false);
                        while ((line = br.readLine()) != null) {
                            String[] value = line.split(",");
                            Query = "INSERT INTO `tb_piutang_karyawan`(`id_pegawai`, `tanggal_piutang`, `tanggal_input`, `nominal_piutang`, `keterangan`) "
                                    + "VALUES ('" + Utility.removeAnonymousCharacters(value[0]) + "','" + value[3] + "',CURRENT_DATE(),'" + value[4] + "','" + value[5] + "')";
                            System.out.println(Query);
                            Utility.db_sub.getConnection().prepareStatement(Query);
                            if ((Utility.db_sub.getStatement().executeUpdate(Query)) == 1) {
                                n++;
                            } else {
                                JOptionPane.showMessageDialog(this, "Failed insert " + value[0]);
                            }
                        }
                        Utility.db_sub.getConnection().commit();
                        JOptionPane.showMessageDialog(this, "Data Berhasil Masuk : " + n);
                    } catch (Exception ex) {
                        try {
                            Utility.db_sub.getConnection().rollback();
                        } catch (SQLException ex1) {
                            Logger.getLogger(JPanel_PiutangKaryawan_sub.class.getName()).log(Level.SEVERE, null, ex1);
                        }
                        JOptionPane.showMessageDialog(this, ex);
                        Logger.getLogger(JPanel_PiutangKaryawan_sub.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {
                        try {
                            Utility.db_sub.getConnection().setAutoCommit(true);
                        } catch (SQLException ex) {
                            Logger.getLogger(JPanel_PiutangKaryawan_sub.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        refreshTable();
                    }
                }
            }
        } catch (HeadlessException | IOException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_PiutangKaryawan_sub.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_input_csvActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_bagian;
    private javax.swing.JComboBox<String> ComboBox_status_karyawan;
    private javax.swing.JTable Table_PiutangKaryawan;
    public javax.swing.JButton button_delete_customer_baku;
    private javax.swing.JButton button_export;
    private javax.swing.JButton button_input_csv;
    public javax.swing.JButton button_insert_customer_baku;
    public static javax.swing.JButton button_search;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private com.toedter.calendar.JDateChooser jDateChooser2;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel_Customer_baku;
    private javax.swing.JPanel jPanel_search_karyawan;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JLabel label_total_data;
    private javax.swing.JTextField txt_id;
    private javax.swing.JTextField txt_nama;
    // End of variables declaration//GEN-END:variables
}
