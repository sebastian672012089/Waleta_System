package waleta_system.SubWaleta;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;

public class JDialog_Browse_KaryawanSub extends javax.swing.JDialog {

    String sql = null;
    ResultSet rs;
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    private String id = "", nama = "";
    String filter = null;

    public JDialog_Browse_KaryawanSub(java.awt.Frame parent, boolean modal, String filter) {
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
            Utility.db_sub.connect();
            sql = "SELECT `kode_sub` FROM `tb_sub_waleta` WHERE 1";
            rs = Utility.db_sub.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_sub.addItem(rs.getString("kode_sub"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JDialog_Browse_KaryawanSub.class.getName()).log(Level.SEVERE, null, e);
        }
        refreshTable();
        AutoCompleteDecorator.decorate(ComboBox_sub);
    }

    public void refreshTable() {
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
                    + " AND `status` = 'IN-SUB' "
                    + bagian
                    + filter
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
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JDialog_Browse_KaryawanSub.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public String getID() {
        return id;
    }

    public void setID(String id) {
        this.id = id;
    }
    
    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        button_select = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table_list_karyawan = new javax.swing.JTable();
        ComboBox_sub = new javax.swing.JComboBox<>();
        button_search = new javax.swing.JButton();
        txt_search_nama_pegawai = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txt_search_id_pegawai = new javax.swing.JTextField();

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

        ComboBox_sub.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_sub.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));
        ComboBox_sub.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ComboBox_subActionPerformed(evt);
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

        txt_search_nama_pegawai.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_nama_pegawai.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_nama_pegawaiKeyPressed(evt);
            }
        });

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("Sub :");

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel4.setText("Status : IN-SUB");

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("ID :");

        txt_search_id_pegawai.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_id_pegawai.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_id_pegawaiKeyPressed(evt);
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
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_sub, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel4))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_id_pegawai, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_nama_pegawai, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_search)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_search_id_pegawai, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_search_nama_pegawai, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_search, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(ComboBox_sub, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE)
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
                setID(table_list_karyawan.getValueAt(0, 0).toString());
                setNama(table_list_karyawan.getValueAt(0, 1).toString());
                this.dispose();
            }
        }
    }//GEN-LAST:event_txt_search_nama_pegawaiKeyPressed

    private void button_selectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_selectActionPerformed
        // TODO add your handling code here:
        int x = JDialog_Browse_KaryawanSub.table_list_karyawan.getSelectedRow();
        if (x == -1) {
            JOptionPane.showMessageDialog(this, "No Data Selected !");
        } else {
            setID(table_list_karyawan.getValueAt(table_list_karyawan.getSelectedRow(), 0).toString());
            setNama(table_list_karyawan.getValueAt(table_list_karyawan.getSelectedRow(), 1).toString());
            this.dispose();
        }
    }//GEN-LAST:event_button_selectActionPerformed

    private void table_list_karyawanMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_table_list_karyawanMousePressed
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            setID(table_list_karyawan.getValueAt(table_list_karyawan.getSelectedRow(), 0).toString());
            setNama(table_list_karyawan.getValueAt(table_list_karyawan.getSelectedRow(), 1).toString());
            this.dispose();
        }
    }//GEN-LAST:event_table_list_karyawanMousePressed

    private void ComboBox_subActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ComboBox_subActionPerformed
        // TODO add your handling code here:
        refreshTable();
    }//GEN-LAST:event_ComboBox_subActionPerformed

    private void txt_search_id_pegawaiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_id_pegawaiKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
            if (table_list_karyawan.getRowCount() == 1) {
                table_list_karyawan.setRowSelectionInterval(0, 0);
                setID(table_list_karyawan.getValueAt(0, 0).toString());
                setNama(table_list_karyawan.getValueAt(0, 1).toString());
                this.dispose();
            }
        }
    }//GEN-LAST:event_txt_search_id_pegawaiKeyPressed

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(JDialog_input_absen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JDialog_input_absen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JDialog_input_absen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JDialog_input_absen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JDialog_Browse_KaryawanSub dialog = new JDialog_Browse_KaryawanSub(new javax.swing.JFrame(), true, "");
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
    public static javax.swing.JComboBox<String> ComboBox_sub;
    private javax.swing.JButton button_search;
    private javax.swing.JButton button_select;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    public static javax.swing.JTable table_list_karyawan;
    private javax.swing.JTextField txt_search_id_pegawai;
    private javax.swing.JTextField txt_search_nama_pegawai;
    // End of variables declaration//GEN-END:variables
}
