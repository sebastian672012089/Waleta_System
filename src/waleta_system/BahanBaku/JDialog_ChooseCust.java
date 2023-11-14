package waleta_system.BahanBaku;

import waleta_system.*;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;


public class JDialog_ChooseCust extends javax.swing.JDialog {

     
    String sql = null;
    ResultSet rs;
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();

    public JDialog_ChooseCust(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setResizable(false);
        refreshTable();
    }

    public void refreshTable() {
        Object[] row = new Object[5];
        DefaultTableModel model = (DefaultTableModel) table_list_customer.getModel();
        model.setRowCount(0);
        try {
            
            sql = "SELECT * FROM `tb_customer_baku` WHERE `nama_customer` LIKE '%" + txt_nama.getText() + "%'";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                row[0] = rs.getString("kode_cust");
                row[1] = rs.getString("nama_customer");
                row[2] = rs.getString("alamat_customer");
                row[3] = rs.getString("no_telp_customer");
                model.addRow(row);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(JDialog_ChooseCust.class.getName()).log(Level.SEVERE, null, ex);
        }
        ColumnsAutoSizer.sizeColumnsToFit(table_list_customer);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        txt_nama = new javax.swing.JTextField();
        button_search = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        table_list_customer = new javax.swing.JTable();
        button_select = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("Nama :");

        txt_nama.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_nama.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_namaKeyPressed(evt);
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

        table_list_customer.setAutoCreateRowSorter(true);
        table_list_customer.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_list_customer.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode Cust", "Nama", "Alamat", "No Telp"
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
        table_list_customer.getTableHeader().setReorderingAllowed(false);
        table_list_customer.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                table_list_customerMousePressed(evt);
            }
        });
        table_list_customer.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                table_list_customerKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(table_list_customer);

        button_select.setBackground(new java.awt.Color(255, 255, 255));
        button_select.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_select.setText("Select");
        button_select.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_selectActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(button_select))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_nama, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_search)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 700, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_nama, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_search, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_select)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void button_searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_searchActionPerformed
        refreshTable();
    }//GEN-LAST:event_button_searchActionPerformed

    private void txt_namaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_namaKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_namaKeyPressed

    private void button_selectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_selectActionPerformed
        int x = JDialog_ChooseCust.table_list_customer.getSelectedRow();
        if (x == -1) {
            JOptionPane.showMessageDialog(this, "No Data Selected !");
        }
        this.dispose();
    }//GEN-LAST:event_button_selectActionPerformed

    private void table_list_customerMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_table_list_customerMousePressed
        if (evt.getClickCount() == 2) {
            this.dispose();
        }
    }//GEN-LAST:event_table_list_customerMousePressed

    private void table_list_customerKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_table_list_customerKeyPressed
        int i = table_list_customer.getSelectedRow();
        try {
            if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
//                table_list_karyawan.setRowSelectionInterval(i - 1, i - 1);
//                table_list_karyawan.getSelectionModel().setSelectionInterval(0, 0);
                table_list_customer.changeSelection(i - 1, 1, false, false);
                this.dispose();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }//GEN-LAST:event_table_list_customerKeyPressed

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
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JDialog_ChooseCust.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JDialog_ChooseCust dialog = new JDialog_ChooseCust(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton button_search;
    private javax.swing.JButton button_select;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    public static javax.swing.JTable table_list_customer;
    private javax.swing.JTextField txt_nama;
    // End of variables declaration//GEN-END:variables
}
