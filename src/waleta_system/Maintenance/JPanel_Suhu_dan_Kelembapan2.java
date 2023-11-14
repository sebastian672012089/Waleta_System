package waleta_system.Maintenance;

import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;
import waleta_system.Class.ExportToExcel;
import waleta_system.MainForm;

public class JPanel_Suhu_dan_Kelembapan2 extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();

    public JPanel_Suhu_dan_Kelembapan2() {
        initComponents();
        table_thermohygro.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && table_thermohygro.getSelectedRow() != -1) {
                    int x = table_thermohygro.getSelectedRow();
                    if (x > -1) {
                        txt_device_id.setText(table_thermohygro.getValueAt(x, 0).toString());
                        txt_edit_ruangan.setText(table_thermohygro.getValueAt(x, 1).toString());
                    }
                }
            }
        });
    }

    public void init() {
        refreshTable();
        refreshTable_thermohygro();
    }

    public void refreshTable() {
        try {

            DefaultTableModel model = (DefaultTableModel) Table_suhu_dan_kelembapan.getModel();
            model.setRowCount(0);
            String filter_tanggal = "";
            if (Date1.getDate() != null && Date2.getDate() != null) {
                filter_tanggal = " AND DATE(`log_time`) BETWEEN '" + dateFormat.format(Date1.getDate()) + "' AND '" + dateFormat.format(Date2.getDate()) + "' ";
            }
            sql = "SELECT `thermohygro_log`.`device_id`, `thermohygro_log`.`location`, `log_time`, `temperature`, `humidity`, `battery` \n"
                    + "FROM `thermohygro_log` \n"
                    + "LEFT JOIN `thermohygro_device` ON `thermohygro_log`.`device_id` = `thermohygro_device`.`device_id`\n"
                    + "WHERE `thermohygro_log`.`location` LIKE '%" + txt_search_ruang.getText() + "%' "
                    + filter_tanggal
                    + "ORDER BY `log_time` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[10];
            while (rs.next()) {
                row[0] = rs.getDate("log_time");
                row[1] = rs.getTime("log_time");
                row[2] = rs.getString("device_id");
                row[3] = rs.getString("location");
                row[4] = rs.getFloat("temperature");
                row[5] = rs.getFloat("humidity");
                row[6] = rs.getInt("battery");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_suhu_dan_kelembapan);
            int rowData = Table_suhu_dan_kelembapan.getRowCount();
            label_total_data.setText(Integer.toString(rowData));

            TableAlignment.setHorizontalAlignment(JLabel.CENTER);
            for (int i = 0; i < Table_suhu_dan_kelembapan.getColumnCount(); i++) {
                Table_suhu_dan_kelembapan.getColumnModel().getColumn(i).setCellRenderer(TableAlignment);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Suhu_dan_Kelembapan2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_thermohygro() {
        try {
            DefaultTableModel model = (DefaultTableModel) table_thermohygro.getModel();
            model.setRowCount(0);
            String filter_tanggal = "";
            sql = "SELECT `device_id`, `location`, `product_name`, `activation_time`, `active_status` FROM `thermohygro_device` WHERE 1 "
                    + filter_tanggal;
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[10];
            while (rs.next()) {
                row[0] = rs.getString("device_id");
                row[1] = rs.getString("location");
                row[2] = rs.getString("product_name");
                row[3] = rs.getTimestamp("activation_time");
                row[4] = rs.getInt("active_status");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(table_thermohygro);
            int rowData = table_thermohygro.getRowCount();
            label_total_thermohygro.setText(Integer.toString(rowData));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Suhu_dan_Kelembapan2.class.getName()).log(Level.SEVERE, null, ex);
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

        jPanel_SuhuKelembapan = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        Table_suhu_dan_kelembapan = new javax.swing.JTable();
        jLabel12 = new javax.swing.JLabel();
        label_total_data = new javax.swing.JLabel();
        txt_search_ruang = new javax.swing.JTextField();
        button_search = new javax.swing.JButton();
        button_export_customer = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        Date1 = new com.toedter.calendar.JDateChooser();
        Date2 = new com.toedter.calendar.JDateChooser();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table_thermohygro = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        txt_device_id = new javax.swing.JTextField();
        txt_edit_ruangan = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        button_edit_thermohygro = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        label_total_thermohygro = new javax.swing.JLabel();

        jPanel_SuhuKelembapan.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_SuhuKelembapan.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Data Suhu dan Kelembapan OTOMATIS", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N
        jPanel_SuhuKelembapan.setPreferredSize(new java.awt.Dimension(1366, 701));

        Table_suhu_dan_kelembapan.setAutoCreateRowSorter(true);
        Table_suhu_dan_kelembapan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tanggal", "Jam", "Device ID", "Location", "Suhu", "Kelembapan", "Battery Level"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Integer.class
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
        Table_suhu_dan_kelembapan.getTableHeader().setReorderingAllowed(false);
        jScrollPane8.setViewportView(Table_suhu_dan_kelembapan);

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel12.setText("Total Data :");

        label_total_data.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        label_total_data.setText("TOTAL");

        txt_search_ruang.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_ruang.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_ruangKeyPressed(evt);
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

        button_export_customer.setBackground(new java.awt.Color(255, 255, 255));
        button_export_customer.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_customer.setText("Export To Excel");
        button_export_customer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_customerActionPerformed(evt);
            }
        });

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("Location :");

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("Tanggal : ");

        Date1.setBackground(new java.awt.Color(255, 255, 255));
        Date1.setDate(new Date());
        Date1.setDateFormatString("dd MMM yyyy");
        Date1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Date2.setBackground(new java.awt.Color(255, 255, 255));
        Date2.setDate(new Date());
        Date2.setDateFormatString("dd MMM yyyy");
        Date2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Data Thermohygro", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 12))); // NOI18N

        table_thermohygro.setAutoCreateRowSorter(true);
        table_thermohygro.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Device ID", "Location", "Product Name", "Activation Time", "Status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class
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
        table_thermohygro.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(table_thermohygro);

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("Device ID :");

        txt_device_id.setEditable(false);
        txt_device_id.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        txt_edit_ruangan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel5.setText("Total Data :");

        button_edit_thermohygro.setBackground(new java.awt.Color(255, 255, 255));
        button_edit_thermohygro.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_edit_thermohygro.setText("Edit");
        button_edit_thermohygro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_edit_thermohygroActionPerformed(evt);
            }
        });

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel6.setText("Ruangan :");

        label_total_thermohygro.setBackground(new java.awt.Color(255, 255, 255));
        label_total_thermohygro.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_thermohygro.setText("0");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_device_id, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_edit_ruangan, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_edit_thermohygro)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_thermohygro)
                        .addGap(0, 181, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_device_id, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txt_edit_ruangan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_edit_thermohygro, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_thermohygro, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel_SuhuKelembapanLayout = new javax.swing.GroupLayout(jPanel_SuhuKelembapan);
        jPanel_SuhuKelembapan.setLayout(jPanel_SuhuKelembapanLayout);
        jPanel_SuhuKelembapanLayout.setHorizontalGroup(
            jPanel_SuhuKelembapanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_SuhuKelembapanLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_SuhuKelembapanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_SuhuKelembapanLayout.createSequentialGroup()
                        .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 642, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel_SuhuKelembapanLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_ruang, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_search)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_export_customer)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_data)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel_SuhuKelembapanLayout.setVerticalGroup(
            jPanel_SuhuKelembapanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_SuhuKelembapanLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_SuhuKelembapanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export_customer, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_search, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_ruang, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_data, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_SuhuKelembapanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 605, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jPanel_SuhuKelembapan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jPanel_SuhuKelembapan, javax.swing.GroupLayout.DEFAULT_SIZE, 677, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txt_search_ruangKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_ruangKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_search_ruangKeyPressed

    private void button_searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_searchActionPerformed
        // TODO add your handling code here:
        refreshTable();
    }//GEN-LAST:event_button_searchActionPerformed

    private void button_export_customerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_customerActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Table_suhu_dan_kelembapan.getModel();
        ExportToExcel.writeToExcel(model, this);
    }//GEN-LAST:event_button_export_customerActionPerformed

    private void button_edit_thermohygroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_edit_thermohygroActionPerformed
        try {
            // TODO add your handling code here:
            String Query = "UPDATE `thermohygro_device` SET "
                    + "`location` = '" + txt_edit_ruangan.getText() + "' "
                    + "WHERE `device_id` = '" + txt_device_id.getText() + "'";
            if ((Utility.db.getStatement().executeUpdate(Query)) > 0) {
                refreshTable_thermohygro();
            } else {
                JOptionPane.showMessageDialog(this, "tidak ada perubahan data!");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Suhu_dan_Kelembapan2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_edit_thermohygroActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser Date1;
    private com.toedter.calendar.JDateChooser Date2;
    private javax.swing.JTable Table_suhu_dan_kelembapan;
    private javax.swing.JButton button_edit_thermohygro;
    private javax.swing.JButton button_export_customer;
    private javax.swing.JButton button_search;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel_SuhuKelembapan;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JLabel label_total_data;
    private javax.swing.JLabel label_total_thermohygro;
    private javax.swing.JTable table_thermohygro;
    private javax.swing.JTextField txt_device_id;
    private javax.swing.JTextField txt_edit_ruangan;
    private javax.swing.JTextField txt_search_ruang;
    // End of variables declaration//GEN-END:variables
}
