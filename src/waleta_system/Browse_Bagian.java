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


public class Browse_Bagian extends javax.swing.JDialog {

     
    String sql = null;
    ResultSet rs;
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    private String id = null;

    public Browse_Bagian(java.awt.Frame parent, boolean modal) {
//    public Browse_Karyawan(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        table_bagian.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "Enter");
        table_bagian.getActionMap().put("Enter", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                //do something on JTable enter pressed
                dispose();
            }
        });

        try {
            Utility.db.connect();
            ComboBox_posisi.removeAllItems();
            ComboBox_posisi.addItem("All");
            sql = "SELECT DISTINCT(`posisi_bagian`) AS 'posisi_bagian' "
                    + "FROM `tb_bagian` "
                    + "WHERE `status_bagian` = 1 AND `posisi_bagian` IS NOT NULL "
                    + "ORDER BY `posisi_bagian`";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_posisi.addItem(rs.getString("posisi_bagian"));
            }
            
            ComboBox_departemen.removeAllItems();
            ComboBox_departemen.addItem("All");
            sql = "SELECT DISTINCT(`kode_departemen`) AS 'kode_departemen' "
                    + "FROM `tb_bagian` "
                    + "WHERE `status_bagian` = 1 AND `kode_departemen` IS NOT NULL "
                    + "ORDER BY `kode_departemen`";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_departemen.addItem(rs.getString("kode_departemen"));
            }
            
            ComboBox_divisi.removeAllItems();
            ComboBox_divisi.addItem("All");
            sql = "SELECT DISTINCT(`divisi_bagian`) AS 'divisi_bagian' "
                    + "FROM `tb_bagian` "
                    + "WHERE `status_bagian` = 1  AND `divisi_bagian` IS NOT NULL "
                    + "ORDER BY `divisi_bagian`";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_divisi.addItem(rs.getString("divisi_bagian"));
            }
            
            ComboBox_bagian.removeAllItems();
            ComboBox_bagian.addItem("All");
            sql = "SELECT DISTINCT(`bagian_bagian`) AS 'bagian_bagian' "
                    + "FROM `tb_bagian` "
                    + "WHERE `status_bagian` = 1  AND `bagian_bagian` IS NOT NULL "
                    + "ORDER BY `bagian_bagian`";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_bagian.addItem(rs.getString("bagian_bagian"));
            }
            
            ComboBox_ruang.removeAllItems();
            ComboBox_ruang.addItem("All");
            sql = "SELECT DISTINCT(`ruang_bagian`) AS 'ruang_bagian' "
                    + "FROM `tb_bagian` "
                    + "WHERE `status_bagian` = 1  AND `ruang_bagian` IS NOT NULL "
                    + "ORDER BY `ruang_bagian`";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_ruang.addItem(rs.getString("ruang_bagian"));
            }
            refreshTable();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(Browse_Bagian.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void refreshTable() {
        try {
            DefaultTableModel model = (DefaultTableModel) table_bagian.getModel();
            model.setRowCount(0);
            String posisi = "";
            if (ComboBox_posisi.getSelectedItem() != null && !ComboBox_posisi.getSelectedItem().toString().equals("All")) {
                posisi = " AND `posisi_bagian` = '" + ComboBox_posisi.getSelectedItem().toString() + "' ";
            }
            String departemen = "";
            if (ComboBox_departemen.getSelectedItem() != null && !ComboBox_departemen.getSelectedItem().toString().equals("All")) {
                departemen = " AND `kode_departemen` = '" + ComboBox_departemen.getSelectedItem().toString() + "' ";
            }
            String divisi = "";
            if (ComboBox_divisi.getSelectedItem() != null && !ComboBox_divisi.getSelectedItem().toString().equals("All")) {
                divisi = " AND `divisi_bagian` = '" + ComboBox_divisi.getSelectedItem().toString() + "' ";
            }
            String bagian = "";
            if (ComboBox_bagian.getSelectedItem() != null && !ComboBox_bagian.getSelectedItem().toString().equals("All")) {
                bagian = " AND `bagian_bagian` = '" + ComboBox_bagian.getSelectedItem().toString() + "' ";
            }
            String ruang = "";
            if (ComboBox_ruang.getSelectedItem() != null && !ComboBox_ruang.getSelectedItem().toString().equals("All")) {
                ruang = " AND `ruang_bagian` = '" + ComboBox_ruang.getSelectedItem().toString() + "' ";
            }
            sql = "SELECT `kode_bagian`, `nama_bagian`, `posisi_bagian`, `kode_departemen`, `divisi_bagian`, `bagian_bagian`, `ruang_bagian` "
                    + "FROM `tb_bagian` "
                    + "WHERE `status_bagian` = 1 "
                    + "AND `nama_bagian` LIKE '%"+txt_search_nama_bagian.getText()+"%'"
                    + posisi
                    + departemen
                    + divisi
                    + bagian
                    + ruang
                    + "ORDER BY `kode_bagian`";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[10];
            while (rs.next()) {
                row[0] = rs.getString("kode_bagian");
                row[1] = rs.getString("posisi_bagian");
                row[2] = rs.getString("kode_departemen");
                row[3] = rs.getString("divisi_bagian");
                row[4] = rs.getString("bagian_bagian");
                row[5] = rs.getString("ruang_bagian");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(table_bagian);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(Browse_Bagian.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Browse_Bagian(null, true).setVisible(true);
            }
        });

    }

//    public String getID() {
//        return id;
//    }
//
//    public void setID(String id) {
//        this.id = id;
//    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        button_select = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table_bagian = new javax.swing.JTable();
        ComboBox_departemen = new javax.swing.JComboBox<>();
        button_search = new javax.swing.JButton();
        txt_search_nama_bagian = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        ComboBox_posisi = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        ComboBox_divisi = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        ComboBox_bagian = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        ComboBox_ruang = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();

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
        jLabel1.setText("Search Bagian :");

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("Posisi :");

        table_bagian.setAutoCreateRowSorter(true);
        table_bagian.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_bagian.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode Bagian", "Posisi", "Departemen", "Divisi", "Bagian", "Ruang"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
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
        table_bagian.getTableHeader().setReorderingAllowed(false);
        table_bagian.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                table_bagianMousePressed(evt);
            }
        });
        jScrollPane1.setViewportView(table_bagian);

        ComboBox_departemen.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_departemen.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));
        ComboBox_departemen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ComboBox_departemenActionPerformed(evt);
            }
        });

        button_search.setBackground(new java.awt.Color(255, 255, 255));
        button_search.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search.setText("Search");
        button_search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_searchActionPerformed(evt);
            }
        });

        txt_search_nama_bagian.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_nama_bagian.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_nama_bagianKeyPressed(evt);
            }
        });

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("Departemen :");

        ComboBox_posisi.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_posisi.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));
        ComboBox_posisi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ComboBox_posisiActionPerformed(evt);
            }
        });

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel4.setText("Divisi :");

        ComboBox_divisi.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_divisi.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));
        ComboBox_divisi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ComboBox_divisiActionPerformed(evt);
            }
        });

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel5.setText("Bagian :");

        ComboBox_bagian.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_bagian.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));
        ComboBox_bagian.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ComboBox_bagianActionPerformed(evt);
            }
        });

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel6.setText("Ruang :");

        ComboBox_ruang.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_ruang.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));
        ComboBox_ruang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ComboBox_ruangActionPerformed(evt);
            }
        });

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel7.setText("klik 2x pada table untuk memilih data");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_select))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 738, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_nama_bagian, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_search))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_posisi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_ruang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_departemen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_divisi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_bagian, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_nama_bagian, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_search, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_posisi, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_ruang, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_departemen, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_divisi, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_bagian, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 311, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_select)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
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

    private void txt_search_nama_bagianKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_nama_bagianKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
            if (table_bagian.getRowCount() == 1) {
                table_bagian.setRowSelectionInterval(0, 0);
                this.dispose();
            }
        }
    }//GEN-LAST:event_txt_search_nama_bagianKeyPressed

    private void button_selectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_selectActionPerformed
        // TODO add your handling code here:
        int x = Browse_Bagian.table_bagian.getSelectedRow();
        if (x == -1) {
            JOptionPane.showMessageDialog(this, "No Data Selected !");
        } else {
//            setID(table_bagian.getValueAt(table_bagian.getSelectedRow(), 0).toString());
            this.dispose();
        }
    }//GEN-LAST:event_button_selectActionPerformed

    private void table_bagianMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_table_bagianMousePressed
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
//            setID(table_bagian.getValueAt(table_bagian.getSelectedRow(), 0).toString());
            this.dispose();
        }
    }//GEN-LAST:event_table_bagianMousePressed

    private void ComboBox_departemenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ComboBox_departemenActionPerformed
        // TODO add your handling code here:
        refreshTable();
    }//GEN-LAST:event_ComboBox_departemenActionPerformed

    private void ComboBox_posisiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ComboBox_posisiActionPerformed
        // TODO add your handling code here:
        refreshTable();
    }//GEN-LAST:event_ComboBox_posisiActionPerformed

    private void ComboBox_divisiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ComboBox_divisiActionPerformed
        // TODO add your handling code here:
        refreshTable();
    }//GEN-LAST:event_ComboBox_divisiActionPerformed

    private void ComboBox_bagianActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ComboBox_bagianActionPerformed
        // TODO add your handling code here:
        refreshTable();
    }//GEN-LAST:event_ComboBox_bagianActionPerformed

    private void ComboBox_ruangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ComboBox_ruangActionPerformed
        // TODO add your handling code here:
        refreshTable();
    }//GEN-LAST:event_ComboBox_ruangActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    public static javax.swing.JComboBox<String> ComboBox_bagian;
    public static javax.swing.JComboBox<String> ComboBox_departemen;
    public static javax.swing.JComboBox<String> ComboBox_divisi;
    public static javax.swing.JComboBox<String> ComboBox_posisi;
    public static javax.swing.JComboBox<String> ComboBox_ruang;
    private javax.swing.JButton button_search;
    private javax.swing.JButton button_select;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    public static javax.swing.JTable table_bagian;
    private javax.swing.JTextField txt_search_nama_bagian;
    // End of variables declaration//GEN-END:variables
}
