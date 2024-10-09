package waleta_system.HRD;

import java.awt.HeadlessException;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;
import waleta_system.Class.AksesMenu;
import waleta_system.MainForm;

public class JPanel_Data_JalurJemputan extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public JPanel_Data_JalurJemputan() {
        initComponents();
    }

    public void init() {
        if ('0' == MainForm.dataMenu.get(AksesMenu.searchMenuByName(MainForm.dataMenu, AksesMenu.MENU_ITEM_JAM_KERJA)).akses.charAt(1)) {
            Button_AddDate.setEnabled(false);
        } else {
            Button_AddDate.setEnabled(true);
        }
//            if ('0' == MainForm.dataMenu.get(AksesMenu.searchMenuByName(MainForm.dataMenu, AksesMenu.MENU_ITEM_JAM_KERJA)).akses.charAt(2)) {
//                Button_AddDate.setEnabled(false);
//            } else {
//                Button_AddDate.setEnabled(true);
//            }
        if ('0' == MainForm.dataMenu.get(AksesMenu.searchMenuByName(MainForm.dataMenu, AksesMenu.MENU_ITEM_JAM_KERJA)).akses.charAt(3)) {
            button_delete.setEnabled(false);
        } else {
            button_delete.setEnabled(true);
        }
        refreshTable_Jalur();
        Table_jalur_jemputan.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && Table_jalur_jemputan.getSelectedRow() != -1) {
                    int i = Table_jalur_jemputan.getSelectedRow();
                    if (i >= 0) {
                        txt_kode_jalur.setText(Table_jalur_jemputan.getValueAt(i, 1).toString());
                        txt_rute.setText(Table_jalur_jemputan.getValueAt(i, 2).toString());
                        txt_driver.setText(Table_jalur_jemputan.getValueAt(i, 3).toString());
                        label_nama_jalur.setText("Jalur : " + Table_jalur_jemputan.getValueAt(i, 1).toString());
                        refreshTable_karyawan(Table_jalur_jemputan.getValueAt(i, 1).toString());
                    }
                }
            }
        });
    }

    public void refreshTable_Jalur() {
        try {
            DefaultTableModel model = (DefaultTableModel) Table_jalur_jemputan.getModel();
            model.setRowCount(0);
            sql = "SELECT `nama_jalur`, `rute`, `nama_driver`, "
                    + "(SELECT COUNT(`tb_karyawan`.`id_pegawai`) FROM `tb_karyawan` WHERE  `tb_karyawan`.`jalur_jemputan` = `tb_jalur_jemputan`.`nama_jalur` AND `status` = 'IN') AS 'jumlah'\n"
                    + "FROM `tb_jalur_jemputan` \n"
                    + "WHERE"
                    + "(`nama_jalur` LIKE '%" + txt_search_keyword.getText() + "%' OR "
                    + "`rute` LIKE '%" + txt_search_keyword.getText() + "%' OR "
                    + "`nama_driver` LIKE '%" + txt_search_keyword.getText() + "%') "
                    + "GROUP BY `nama_jalur` ORDER BY `nama_jalur`";
            System.out.println(sql);
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[5];
            int no = 0;
            while (rs.next()) {
                no++;
                row[0] = no;
                row[1] = rs.getString("nama_jalur");
                row[2] = rs.getString("rute");
                row[3] = rs.getString("nama_driver");
                row[4] = rs.getInt("jumlah");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_jalur_jemputan);
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_Data_JalurJemputan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_karyawan(String kode_jalur) {
        try {
            DefaultTableModel model = (DefaultTableModel) tabel_karyawan_jemputan.getModel();
            model.setRowCount(0);
            sql = "SELECT `id_pegawai`, `nama_pegawai`, `nama_bagian`, `status` "
                    + "FROM `tb_karyawan` LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian` "
                    + "WHERE `jalur_jemputan` = '" + kode_jalur + "' AND `status` = 'IN'";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[5];
            while (rs.next()) {
                row[0] = rs.getString("id_pegawai");
                row[1] = rs.getString("nama_pegawai");
                row[2] = rs.getString("nama_bagian");
                row[3] = rs.getString("status");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(tabel_karyawan_jemputan);
            label_jumlah_jemputan.setText("(" + tabel_karyawan_jemputan.getRowCount() + " Karyawan)");
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_Data_JalurJemputan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        Table_jalur_jemputan = new javax.swing.JTable();
        button_refresh = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        txt_search_keyword = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabel_karyawan_jemputan = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txt_kode_jalur = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txt_rute = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txt_driver = new javax.swing.JTextField();
        button_clear = new javax.swing.JButton();
        button_delete = new javax.swing.JButton();
        Button_AddDate = new javax.swing.JButton();
        label_nama_jalur = new javax.swing.JLabel();
        label_jumlah_jemputan = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Data Jalur Jemputan", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N

        Table_jalur_jemputan.setAutoCreateRowSorter(true);
        Table_jalur_jemputan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Table_jalur_jemputan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "Kode Jalur", "Rute", "Driver", "Jumlah Karyawan"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class
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
        Table_jalur_jemputan.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(Table_jalur_jemputan);

        button_refresh.setBackground(new java.awt.Color(255, 255, 255));
        button_refresh.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_refresh.setText("Refresh");
        button_refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refreshActionPerformed(evt);
            }
        });

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel5.setText("Keywords :");

        txt_search_keyword.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_search_keyword.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_keywordKeyPressed(evt);
            }
        });

        tabel_karyawan_jemputan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        tabel_karyawan_jemputan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Pegawai", "Nama", "Bagian", "Status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
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
        tabel_karyawan_jemputan.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(tabel_karyawan_jemputan);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel1.setText("Kode Jalur :");

        txt_kode_jalur.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_kode_jalur.setToolTipText("");

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setText("Rute :");

        txt_rute.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_rute.setToolTipText("");

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel3.setText("Driver :");

        txt_driver.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_driver.setToolTipText("");

        button_clear.setBackground(null);
        button_clear.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_clear.setText("Clear");
        button_clear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_clearActionPerformed(evt);
            }
        });

        button_delete.setBackground(null);
        button_delete.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_delete.setText("Delete");
        button_delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_deleteActionPerformed(evt);
            }
        });

        Button_AddDate.setBackground(null);
        Button_AddDate.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Button_AddDate.setText("Save");
        Button_AddDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_AddDateActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_kode_jalur, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_rute, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_driver, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(Button_AddDate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_clear)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_delete)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_kode_jalur, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_rute, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_driver, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_clear, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_delete, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Button_AddDate))
                .addContainerGap())
        );

        label_nama_jalur.setBackground(new java.awt.Color(255, 255, 255));
        label_nama_jalur.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_nama_jalur.setText("NAMA JALUR");

        label_jumlah_jemputan.setBackground(new java.awt.Color(255, 255, 255));
        label_jumlah_jemputan.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_jumlah_jemputan.setText("JUMLAH JEMPUTAN");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 600, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_keyword, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_refresh)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(label_nama_jalur)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_jumlah_jemputan)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 730, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_search_keyword, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_refresh, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_nama_jalur, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_jumlah_jemputan, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 483, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
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

    private void Button_AddDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_AddDateActionPerformed
        try {
            sql = "INSERT INTO `tb_jalur_jemputan` "
                    + "VALUES ('" + txt_kode_jalur.getText() + "','" + txt_rute.getText() + "','" + txt_driver.getText() + "') "
                    + "ON DUPLICATE KEY UPDATE "
                    + "`nama_jalur`='" + txt_kode_jalur.getText() + "', "
                    + "`rute`='" + txt_rute.getText() + "', "
                    + "`nama_driver`='" + txt_driver.getText() + "'";
            Utility.db.getConnection().createStatement();
            if ((Utility.db.getStatement().executeUpdate(sql)) > 0) {
                JOptionPane.showMessageDialog(this, "Data Saved");
                refreshTable_Jalur();
            } else {
                JOptionPane.showMessageDialog(this, "Input Error");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(JPanel_Data_JalurJemputan.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_Button_AddDateActionPerformed

    private void button_refreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refreshActionPerformed
        // TODO add your handling code here:
        refreshTable_Jalur();
    }//GEN-LAST:event_button_refreshActionPerformed

    private void button_clearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_clearActionPerformed
        // TODO add your handling code here:
        txt_kode_jalur.setText(null);
        txt_rute.setText(null);
        txt_driver.setText(null);
    }//GEN-LAST:event_button_clearActionPerformed

    private void button_deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_deleteActionPerformed
        // TODO add your handling code here:
        try {
            int j = Table_jalur_jemputan.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih data yang ingin di hapus !");
            } else {
                int dialogResult = JOptionPane.showConfirmDialog(this, "Yakin hapus data ini?", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    // delete code here
                    sql = "DELETE FROM `tb_jalur_jemputan` WHERE `nama_jalur` = '" + txt_kode_jalur.getText() + "'";
                    Utility.db.getConnection().createStatement();
                    if ((Utility.db.getStatement().executeUpdate(sql)) > 0) {
                        JOptionPane.showMessageDialog(this, "Data Deleted!");
                        refreshTable_Jalur();
                    } else {
                        JOptionPane.showMessageDialog(this, "Delete Error");
                    }
                }
            }
        } catch (HeadlessException | SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(JPanel_Data_JalurJemputan.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_deleteActionPerformed

    private void txt_search_keywordKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_keywordKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_Jalur();
        }
    }//GEN-LAST:event_txt_search_keywordKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Button_AddDate;
    private javax.swing.JTable Table_jalur_jemputan;
    private javax.swing.JButton button_clear;
    private javax.swing.JButton button_delete;
    private javax.swing.JButton button_refresh;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel label_jumlah_jemputan;
    private javax.swing.JLabel label_nama_jalur;
    private javax.swing.JTable tabel_karyawan_jemputan;
    private javax.swing.JTextField txt_driver;
    private javax.swing.JTextField txt_kode_jalur;
    private javax.swing.JTextField txt_rute;
    private javax.swing.JTextField txt_search_keyword;
    // End of variables declaration//GEN-END:variables
}
