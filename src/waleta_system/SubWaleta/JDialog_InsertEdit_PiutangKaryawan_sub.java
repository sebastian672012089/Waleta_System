package waleta_system.SubWaleta;

import java.awt.event.KeyEvent;
import waleta_system.Class.Utility;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import waleta_system.Class.ColumnsAutoSizer;

public class JDialog_InsertEdit_PiutangKaryawan_sub extends javax.swing.JDialog {

    String sql = null;
    ResultSet rs;
    PreparedStatement pst;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Date today = new Date();
    String status = "";

    public JDialog_InsertEdit_PiutangKaryawan_sub(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setResizable(false);
        refreshTable_karyawan();
    }

    public void refreshTable_karyawan() {
        try {
            Utility.db_sub.connect();
            DefaultTableModel model = (DefaultTableModel) table_list_karyawan.getModel();
            model.setRowCount(0);
            String bagian = " AND `bagian` = '" + ComboBox_sub.getSelectedItem().toString() + "' ";
            if (ComboBox_sub.getSelectedItem().toString().equals("All")) {
                bagian = "";
            }
            sql = "SELECT `id_pegawai`, `nama_pegawai`, `bagian`, `jenis_pegawai`, `status` "
                    + "FROM `tb_karyawan` "
                    + "WHERE `nama_pegawai` LIKE '%" + txt_search_nama_pegawai.getText() + "%' "
                    + " AND `status` = '" + ComboBox_status.getSelectedItem().toString() + "' "
                    + bagian
                    + "GROUP BY `tb_karyawan`.`id_pegawai` "
                    + "ORDER BY `tb_karyawan`.`id_pegawai` DESC";
            rs = Utility.db_sub.getStatement().executeQuery(sql);
            Object[] row = new Object[5];
            while (rs.next()) {
                row[0] = rs.getString("id_pegawai");
                row[1] = rs.getString("nama_pegawai");
                row[2] = rs.getString("bagian");
                row[3] = rs.getString("jenis_pegawai");
                row[4] = rs.getString("status");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(table_list_karyawan);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(JDialog_input_absen.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public boolean duplicate_id(String input_id) {
        boolean output = false;
        for (int i = 0; i < table_list_karyawan_terpilih.getRowCount(); i++) {
            if (input_id.equals(table_list_karyawan_terpilih.getValueAt(i, 0).toString())) {
                output = true;
                break;
            }
        }
        ColumnsAutoSizer.sizeColumnsToFit(table_list_karyawan_terpilih);
        return output;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        ComboBox_sub = new javax.swing.JComboBox<>();
        txt_search_id_pegawai = new javax.swing.JTextField();
        txt_search_nama_pegawai = new javax.swing.JTextField();
        ComboBox_status = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table_list_karyawan = new javax.swing.JTable();
        jLabel11 = new javax.swing.JLabel();
        button_search = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        button_save = new javax.swing.JButton();
        button_cancel = new javax.swing.JButton();
        Date_piutang = new com.toedter.calendar.JDateChooser();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        table_list_karyawan_terpilih = new javax.swing.JTable();
        label_title = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Input Piutang");

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        ComboBox_sub.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_sub.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));
        ComboBox_sub.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ComboBox_subActionPerformed(evt);
            }
        });

        txt_search_id_pegawai.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_id_pegawai.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_id_pegawaiKeyPressed(evt);
            }
        });

        txt_search_nama_pegawai.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_nama_pegawai.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_nama_pegawaiKeyPressed(evt);
            }
        });

        ComboBox_status.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_status.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "IN-SUB", "OUT-SUB" }));
        ComboBox_status.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ComboBox_statusActionPerformed(evt);
            }
        });

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel4.setText("Status :");

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel8.setText("Sub :");

        table_list_karyawan.setAutoCreateRowSorter(true);
        table_list_karyawan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_list_karyawan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Nama", "Sub", "Posisi", "Status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
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
        table_list_karyawan.getTableHeader().setReorderingAllowed(false);
        table_list_karyawan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                table_list_karyawanMousePressed(evt);
            }
        });
        jScrollPane1.setViewportView(table_list_karyawan);

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel11.setText("Nama :");

        button_search.setBackground(new java.awt.Color(255, 255, 255));
        button_search.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search.setText("Search");
        button_search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_searchActionPerformed(evt);
            }
        });

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel12.setText("ID :");

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 2, 11)); // NOI18N
        jLabel13.setText("Note : klik 2x untuk memilih");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_id_pegawai, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_nama_pegawai, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_sub, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_status, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_search)
                        .addGap(0, 102, Short.MAX_VALUE))
                    .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(ComboBox_status, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_search, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_search_id_pegawai, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_search_nama_pegawai, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(ComboBox_sub, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel6.setText("Tgl Piutang :");

        button_save.setBackground(new java.awt.Color(255, 255, 255));
        button_save.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_save.setText("Save");
        button_save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_saveActionPerformed(evt);
            }
        });

        button_cancel.setBackground(new java.awt.Color(255, 255, 255));
        button_cancel.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_cancel.setText("Cancel");
        button_cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_cancelActionPerformed(evt);
            }
        });

        Date_piutang.setDate(new Date());
        Date_piutang.setDateFormatString("dd MMM yyyy");

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 51, 51));
        jLabel9.setText("*) angka positif untuk menambah gaji");

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 51, 51));
        jLabel10.setText("*) angka negatif untuk mengurangi gaji");

        table_list_karyawan_terpilih.setAutoCreateRowSorter(true);
        table_list_karyawan_terpilih.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_list_karyawan_terpilih.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Nama", "Nominal", "Keterangan"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table_list_karyawan_terpilih.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(table_list_karyawan_terpilih);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_piutang, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_save)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_cancel)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 440, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_piutang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_cancel)
                    .addComponent(button_save))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 339, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel10)
                .addContainerGap())
        );

        label_title.setBackground(new java.awt.Color(255, 255, 255));
        label_title.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_title.setText("Input Piutang Karyawan Sub");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label_title, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label_title, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void button_saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_saveActionPerformed
        try {
            // TODO add your handling code here:
            boolean cek = true;
            if (Date_piutang.getDate() == null) {
                JOptionPane.showMessageDialog(this, "Tanggal tidak boleh kosong");
                cek = false;
            } 

            if (cek) {
                Utility.db_sub.connect();
                Utility.db_sub.getConnection().setAutoCommit(false);
                int jumlah_update = 0;
                for (int i = 0; i < table_list_karyawan_terpilih.getRowCount(); i++) {
                    int piutang = Integer.valueOf(table_list_karyawan_terpilih.getValueAt(i, 2).toString());
                    String Query = "INSERT INTO `tb_piutang_karyawan`(`id_pegawai`, `tanggal_piutang`, `tanggal_input`, `nominal_piutang`, `keterangan`) "
                            + "VALUES ('" + table_list_karyawan_terpilih.getValueAt(i, 0).toString() + "','" + dateFormat.format(Date_piutang.getDate()) + "',CURRENT_DATE(),'" + piutang + "','" + table_list_karyawan_terpilih.getValueAt(i, 3).toString() + "')";
                    Utility.db_sub.getConnection().createStatement();
                    if ((Utility.db_sub.getStatement().executeUpdate(Query)) == 1) {
                        jumlah_update++;
                    }
                }
                Utility.db_sub.getConnection().commit();
                JOptionPane.showMessageDialog(this, "Sukses input " + jumlah_update + " data");
                this.dispose();
            }
        } catch (Exception ex) {
            try {
                Utility.db_sub.getConnection().rollback();
            } catch (SQLException ex1) {
                Logger.getLogger(JDialog_input_absen.class.getName()).log(Level.SEVERE, null, ex1);
            }
            JOptionPane.showMessageDialog(this, "failed : " + ex);
            Logger.getLogger(JDialog_input_absen.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                Utility.db_sub.getConnection().setAutoCommit(true);
            } catch (SQLException ex) {
                Logger.getLogger(JDialog_input_absen.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_button_saveActionPerformed

    private void button_cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_cancelActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_button_cancelActionPerformed

    private void ComboBox_subActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ComboBox_subActionPerformed
        // TODO add your handling code here:
        refreshTable_karyawan();
    }//GEN-LAST:event_ComboBox_subActionPerformed

    private void txt_search_id_pegawaiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_id_pegawaiKeyPressed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) table_list_karyawan_terpilih.getModel();
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_karyawan();
            if (table_list_karyawan.getRowCount() == 1) {
                if (duplicate_id(table_list_karyawan.getValueAt(0, 0).toString())) {
                    JOptionPane.showMessageDialog(this, "Karyawan sudah masuk dalam list");
                } else {
                    model.addRow(new Object[]{table_list_karyawan.getValueAt(0, 0).toString(), table_list_karyawan.getValueAt(0, 1).toString(), null, '-'});
                }
            }
        }
    }//GEN-LAST:event_txt_search_id_pegawaiKeyPressed

    private void txt_search_nama_pegawaiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_nama_pegawaiKeyPressed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) table_list_karyawan_terpilih.getModel();
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_karyawan();
            if (table_list_karyawan.getRowCount() == 1) {
                if (duplicate_id(table_list_karyawan.getValueAt(0, 0).toString())) {
                    JOptionPane.showMessageDialog(this, "Karyawan sudah masuk dalam list");
                } else {
                    model.addRow(new Object[]{table_list_karyawan.getValueAt(0, 0).toString(), table_list_karyawan.getValueAt(0, 1).toString(), null, '-'});
                }
            }
        }
    }//GEN-LAST:event_txt_search_nama_pegawaiKeyPressed

    private void ComboBox_statusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ComboBox_statusActionPerformed
        // TODO add your handling code here:
        refreshTable_karyawan();
    }//GEN-LAST:event_ComboBox_statusActionPerformed

    private void table_list_karyawanMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_table_list_karyawanMousePressed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) table_list_karyawan_terpilih.getModel();
        int i = table_list_karyawan.getSelectedRow();
        if (evt.getClickCount() == 2) {
            if (duplicate_id(table_list_karyawan.getValueAt(0, 0).toString())) {
                JOptionPane.showMessageDialog(this, "Karyawan sudah masuk dalam list");
            } else {
                model.addRow(new Object[]{table_list_karyawan.getValueAt(i, 0).toString(), table_list_karyawan.getValueAt(i, 1).toString(), null, '-'});
            }
        }
    }//GEN-LAST:event_table_list_karyawanMousePressed

    private void button_searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_searchActionPerformed
        // TODO add your handling code here:
        refreshTable_karyawan();
    }//GEN-LAST:event_button_searchActionPerformed

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(JDialog_InsertEdit_PiutangKaryawan_sub.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JDialog_InsertEdit_PiutangKaryawan_sub.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JDialog_InsertEdit_PiutangKaryawan_sub.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JDialog_InsertEdit_PiutangKaryawan_sub.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JDialog_InsertEdit_PiutangKaryawan_sub dialog = new JDialog_InsertEdit_PiutangKaryawan_sub(new javax.swing.JFrame(), true);
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
    public static javax.swing.JComboBox<String> ComboBox_status;
    public static javax.swing.JComboBox<String> ComboBox_sub;
    private com.toedter.calendar.JDateChooser Date_piutang;
    private javax.swing.JButton button_cancel;
    private javax.swing.JButton button_save;
    private javax.swing.JButton button_search;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel label_title;
    public static javax.swing.JTable table_list_karyawan;
    public static javax.swing.JTable table_list_karyawan_terpilih;
    private javax.swing.JTextField txt_search_id_pegawai;
    private javax.swing.JTextField txt_search_nama_pegawai;
    // End of variables declaration//GEN-END:variables
}
