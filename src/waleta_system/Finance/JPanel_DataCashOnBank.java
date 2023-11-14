package waleta_system.Finance;

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
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import waleta_system.BahanBaku.JPanel_GradeBahanBaku;
import waleta_system.Class.ColumnsAutoSizer;

import waleta_system.Class.ExportToExcel;

public class JPanel_DataCashOnBank extends javax.swing.JPanel {

    
    String sql = null;
    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();

    public JPanel_DataCashOnBank() {
        initComponents();
    }

    public void init() {
        try {
            
            
            refreshTable_DataCash();
        } catch (Exception ex) {
            Logger.getLogger(JPanel_GradeBahanBaku.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_DataCash() {
        try {
            decimalFormat.setMaximumFractionDigits(1);
            DefaultTableModel model = (DefaultTableModel) table_data.getModel();
            model.setRowCount(0);
            String filter_tanggal = "";
            if (Date_filter1.getDate() != null && Date_filter2.getDate() != null) {
                filter_tanggal = " AND DATE(`waktu_input`) BETWEEN '" + dateFormat.format(Date_filter1.getDate()) + "' AND '" + dateFormat.format(Date_filter2.getDate()) + "'";
            }
            sql = "SELECT `kode`, `waktu_input`, `currency`, `nominal`, `keterangan` FROM `tb_cash_on_bank` WHERE "
                    + "`keterangan` LIKE '%" + txt_search_keterangan.getText() + "%' " + filter_tanggal + " ORDER BY `waktu_input` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[5];
            while (rs.next()) {
                row[0] = rs.getInt("kode");
                row[1] = rs.getTimestamp("waktu_input");
                row[2] = rs.getString("currency");
                row[3] = rs.getDouble("nominal");
                row[4] = rs.getString("keterangan");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(table_data);
//            label_total_netto.setText(decimalFormat.format(total_netto));
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_DataCashOnBank.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup_jenis_label = new javax.swing.ButtonGroup();
        jScrollPane1 = new javax.swing.JScrollPane();
        table_data = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        txt_search_keterangan = new javax.swing.JTextField();
        button_search = new javax.swing.JButton();
        button_export = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        Date_filter1 = new com.toedter.calendar.JDateChooser();
        Date_filter2 = new com.toedter.calendar.JDateChooser();
        button_delete = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        button_input = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Data Cash On Bank", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N

        table_data.setAutoCreateRowSorter(true);
        table_data.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_data.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "kode", "Scan Time (WIB)", "Currency", "Nominal", "Keterangan"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.Double.class, java.lang.String.class
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
        table_data.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(table_data);

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel1.setText("Keterangan :");

        txt_search_keterangan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_keterangan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_keteranganKeyPressed(evt);
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

        button_export.setBackground(new java.awt.Color(255, 255, 255));
        button_export.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export.setText("Export to Excel");
        button_export.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_exportActionPerformed(evt);
            }
        });

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel8.setText("Tanggal :");

        Date_filter1.setDate(new Date());
        Date_filter1.setDateFormatString("dd MMMM yyyy");
        Date_filter1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Date_filter2.setDate(new Date());
        Date_filter2.setDateFormatString("dd MMMM yyyy");
        Date_filter2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        button_delete.setBackground(new java.awt.Color(255, 255, 255));
        button_delete.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_delete.setText("Detele");
        button_delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_deleteActionPerformed(evt);
            }
        });

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel10.setText("Currency :");

        jComboBox1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "BCA168-CNY", "BCA972-IDR", "BCA817-IDR", "BCA889-USD", "CIMB100-IDR", "CIMB200-IDR" }));

        button_input.setBackground(new java.awt.Color(255, 255, 255));
        button_input.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_input.setText("Input");
        button_input.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_inputActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_keterangan, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_filter1, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_filter2, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_search)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_export)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_delete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_input)
                        .addGap(0, 333, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_filter1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_keterangan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_filter2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_delete, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_search, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_input, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 627, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void button_searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_searchActionPerformed
        // TODO add your handling code here:
        refreshTable_DataCash();
    }//GEN-LAST:event_button_searchActionPerformed

    private void txt_search_keteranganKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_keteranganKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_DataCash();
        }
    }//GEN-LAST:event_txt_search_keteranganKeyPressed

    private void button_exportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_exportActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) table_data.getModel();
        ExportToExcel.writeToExcel(model, this);
    }//GEN-LAST:event_button_exportActionPerformed

    private void button_deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_deleteActionPerformed
        // TODO add your handling code here:
        try {
            int j = table_data.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Please Select Row Data that you want to Delete !");
            } else {
                int dialogResult = JOptionPane.showConfirmDialog(this, "Yakin hapus data ini?", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    // delete code here
                    sql = "DELETE FROM `tb_cash_on_bank` WHERE `kode` = '" + table_data.getValueAt(j, 0).toString() + "'";
                    if ((Utility.db.getStatement().executeUpdate(sql)) == 1) {
                        JOptionPane.showMessageDialog(this, "data deleted Successfully");
                        refreshTable_DataCash();
                    } else {
                        JOptionPane.showMessageDialog(this, "delete failed !");
                    }
                }
            }
        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(this, e);
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_DataCashOnBank.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_deleteActionPerformed

    private void button_inputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_inputActionPerformed
        // TODO add your handling code here:
        JDialog_Input_CashOnBank dialog = new JDialog_Input_CashOnBank(new javax.swing.JFrame(), true);
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);
        refreshTable_DataCash();
    }//GEN-LAST:event_button_inputActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser Date_filter1;
    private com.toedter.calendar.JDateChooser Date_filter2;
    private javax.swing.ButtonGroup buttonGroup_jenis_label;
    public static javax.swing.JButton button_delete;
    private javax.swing.JButton button_export;
    public static javax.swing.JButton button_input;
    public static javax.swing.JButton button_search;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    public static javax.swing.JTable table_data;
    private javax.swing.JTextField txt_search_keterangan;
    // End of variables declaration//GEN-END:variables
}
