package waleta_system;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;

public class Browse_Karyawan extends javax.swing.JDialog {

    String sql = null;
    ResultSet rs;
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    private String id = null;
    String filter = null;

    public Browse_Karyawan(java.awt.Frame parent, boolean modal, String filter) {
//    public Browse_Karyawan(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        if (filter == null) {
            filter = "";
        }
        this.filter = filter;
        table_list_karyawan.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "Enter");
        table_list_karyawan.getActionMap().put("Enter", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                //do something on JTable enter pressed
                dispose();
            }
        });
        this.setResizable(false);

        try {
            sql = "SELECT DISTINCT(`posisi`) AS 'posisi' FROM `tb_karyawan` "
                    + "WHERE `posisi` IS NOT NULL AND `status` = 'IN' ORDER BY `posisi`";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_posisi.addItem(rs.getString("posisi"));
            }
            ComboBox_posisi.setSelectedItem("PEJUANG");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
        refreshTable();
    }

    public void refreshTable() {
        try {
            DefaultTableModel model = (DefaultTableModel) table_list_karyawan.getModel();
            model.setRowCount(0);
            String bagian = " AND `tb_bagian`.`nama_bagian` LIKE '%" + txt_search_bagian.getText() + "%' ";
            if (txt_search_bagian.getText() == null || txt_search_bagian.getText().equals("")) {
                bagian = "";
            }
            String posisi = " AND `posisi` = '" + ComboBox_posisi.getSelectedItem().toString() + "' ";
            if (ComboBox_posisi.getSelectedItem().toString().equals("All")) {
                posisi = "";
            }
            sql = "SELECT `tb_karyawan`.`id_pegawai`, `nama_pegawai`, `tb_bagian`.`nama_bagian`, `tb_karyawan`.`status`,  `tb_grup_cabut`.`kode_grup`\n"
                    + "FROM `tb_karyawan` \n"
                    + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`\n"
                    + "LEFT JOIN `att_log` ON `tb_karyawan`.`pin_finger` = `att_log`.`pin`\n"
                    + "LEFT JOIN `tb_grup_cabut` ON `tb_karyawan`.`id_pegawai` = `tb_grup_cabut`.`id_pegawai`\n"
                    + "WHERE `nama_pegawai` LIKE '%" + txt_search_nama_pegawai.getText() + "%' \n"
                    + "AND `status` = 'IN' \n"
                    + bagian
                    + posisi
                    + filter
                    + "GROUP BY `tb_karyawan`.`id_pegawai` "
                    + "ORDER BY `tb_karyawan`.`id_pegawai` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[5];
            while (rs.next()) {
                row[0] = rs.getString("id_pegawai");
                row[1] = rs.getString("nama_pegawai");
                row[2] = rs.getString("nama_bagian");
                row[3] = rs.getString("status");
                row[4] = rs.getString("kode_grup");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(table_list_karyawan);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getID() {
        return id;
    }

    public void setID(String id) {
        this.id = id;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        button_select = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table_list_karyawan = new javax.swing.JTable();
        button_search = new javax.swing.JButton();
        txt_search_nama_pegawai = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        ComboBox_posisi = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        txt_search_bagian = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        button_select.setBackground(new java.awt.Color(255, 255, 255));
        button_select.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_select.setText("Select");
        button_select.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_selectActionPerformed(evt);
            }
        });

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("Nama :");

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("Posisi :");

        table_list_karyawan.setAutoCreateRowSorter(true);
        table_list_karyawan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_list_karyawan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Pegawai", "Nama Pegawai", "Bagian", "Status"
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
        table_list_karyawan.getTableHeader().setReorderingAllowed(false);
        table_list_karyawan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                table_list_karyawanMousePressed(evt);
            }
        });
        jScrollPane1.setViewportView(table_list_karyawan);

        button_search.setBackground(new java.awt.Color(255, 255, 255));
        button_search.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search.setText("Search");
        button_search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_searchActionPerformed(evt);
            }
        });

        txt_search_nama_pegawai.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_nama_pegawai.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_nama_pegawaiKeyPressed(evt);
            }
        });

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("Bagian :");

        ComboBox_posisi.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_posisi.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));
        ComboBox_posisi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ComboBox_posisiActionPerformed(evt);
            }
        });

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel4.setText("Status = IN");

        txt_search_bagian.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_bagian.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_bagianKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(button_select))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 730, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_nama_pegawai, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_bagian, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_posisi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_search)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_search_nama_pegawai, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_search, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(ComboBox_posisi, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_search_bagian, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_select)
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

    private void button_searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_searchActionPerformed
        // TODO add your handling code here:
        refreshTable();
    }//GEN-LAST:event_button_searchActionPerformed

    private void txt_search_nama_pegawaiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_nama_pegawaiKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
            if (table_list_karyawan.getRowCount() == 1) {
                table_list_karyawan.setRowSelectionInterval(0, 0);
                this.dispose();
            }
        }
    }//GEN-LAST:event_txt_search_nama_pegawaiKeyPressed

    private void button_selectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_selectActionPerformed
        // TODO add your handling code here:
        int x = Browse_Karyawan.table_list_karyawan.getSelectedRow();
        if (x == -1) {
            JOptionPane.showMessageDialog(this, "No Data Selected !");
        } else {
            setID(table_list_karyawan.getValueAt(table_list_karyawan.getSelectedRow(), 0).toString());
            this.dispose();
        }
    }//GEN-LAST:event_button_selectActionPerformed

    private void table_list_karyawanMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_table_list_karyawanMousePressed
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            setID(table_list_karyawan.getValueAt(table_list_karyawan.getSelectedRow(), 0).toString());
            this.dispose();
        }
    }//GEN-LAST:event_table_list_karyawanMousePressed

    private void ComboBox_posisiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ComboBox_posisiActionPerformed
        // TODO add your handling code here:
        refreshTable();
    }//GEN-LAST:event_ComboBox_posisiActionPerformed

    private void txt_search_bagianKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_bagianKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
            if (table_list_karyawan.getRowCount() == 1) {
                table_list_karyawan.setRowSelectionInterval(0, 0);
                this.dispose();
            }
        }
    }//GEN-LAST:event_txt_search_bagianKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JComboBox<String> ComboBox_posisi;
    private javax.swing.JButton button_search;
    private javax.swing.JButton button_select;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    public static javax.swing.JTable table_list_karyawan;
    private javax.swing.JTextField txt_search_bagian;
    private javax.swing.JTextField txt_search_nama_pegawai;
    // End of variables declaration//GEN-END:variables
}
