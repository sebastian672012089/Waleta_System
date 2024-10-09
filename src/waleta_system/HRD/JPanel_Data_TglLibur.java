package waleta_system.HRD;

import waleta_system.Class.Utility;

import java.awt.HeadlessException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
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

public class JPanel_Data_TglLibur extends javax.swing.JPanel {

    
    String sql = null;
    ResultSet rs;
    Date date = new Date();
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public JPanel_Data_TglLibur() {
        initComponents();
        
    }

    public void init() {
        try {
            button_delete.setIcon(Utility.ResizeImageIcon(new javax.swing.ImageIcon(getClass().getResource("/waleta_system/Images/delete-icon.png")), button_delete.getWidth(), button_delete.getHeight()));
            
            
            refreshTable_Libur();

            Table_Libur.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent event) {
                    if (!event.getValueIsAdjusting() && Table_Libur.getSelectedRow() != -1) {
                        int i = Table_Libur.getSelectedRow();
                        if (i >= 0) {
                        }
                    }
                }
            });
        } catch (Exception ex) {
            Logger.getLogger(JPanel_Data_TglLibur.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_Libur() {
        try {
            DefaultTableModel model = (DefaultTableModel) Table_Libur.getModel();
            model.setRowCount(0);
            sql = "SELECT YEAR(`tanggal_libur`) AS 'year', DATE_FORMAT(`tanggal_libur`,'%M') AS 'month', `tanggal_libur`, `keterangan`, DAYNAME(`tanggal_libur`) AS 'Hari' "
                    + "FROM `tb_libur` WHERE 1 ORDER BY `tanggal_libur` DESC";
            if (CheckBox_filter.isSelected()) {
                sql = "SELECT YEAR(`tanggal_libur`) AS 'year', DATE_FORMAT(`tanggal_libur`,'%M') AS 'month', `tanggal_libur`, `keterangan`, DAYNAME(`tanggal_libur`) AS 'Hari' "
                        + "FROM `tb_libur` Where Month(`tanggal_libur`)='" + (MonthChooser1.getMonth() + 1) + "' && YEAR(`tanggal_libur`)='" + YearChooser1.getYear() + "'"
                        + "ORDER BY `tanggal_libur` DESC";
            }
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[5];
            while (rs.next()) {
                row[0] = rs.getString("year");
                row[1] = rs.getString("month");
                row[2] = rs.getDate("tanggal_libur");
                row[3] = rs.getString("Hari");
                row[4] = rs.getString("keterangan");
                model.addRow(row);
            }
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_Data_TglLibur.class.getName()).log(Level.SEVERE, null, ex);
        }

        int rowData = Table_Libur.getRowCount();
        Label_total.setText(Integer.toString(rowData));

        ColumnsAutoSizer.sizeColumnsToFit(Table_Libur);
        TableAlignment.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < Table_Libur.getColumnCount(); i++) {
            Table_Libur.getColumnModel().getColumn(i).setCellRenderer(TableAlignment);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jCalendar1 = new com.toedter.calendar.JCalendar();
        jLabel1 = new javax.swing.JLabel();
        Label_tanggal = new javax.swing.JLabel();
        Button_AddDate = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        Table_Libur = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        MonthChooser1 = new com.toedter.calendar.JMonthChooser();
        YearChooser1 = new com.toedter.calendar.JYearChooser();
        jLabel3 = new javax.swing.JLabel();
        Label_total = new javax.swing.JLabel();
        CheckBox_filter = new javax.swing.JCheckBox();
        jButton1 = new javax.swing.JButton();
        Label_hari = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        button_delete = new javax.swing.JButton();
        Button_AddDate1 = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Data Tanggal Libur PT.Waleta", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N

        jCalendar1.setBackground(new java.awt.Color(255, 255, 255));
        jCalendar1.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jCalendar1PropertyChange(evt);
            }
        });

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel1.setText("Tanggal :");

        Label_tanggal.setBackground(new java.awt.Color(255, 255, 255));
        Label_tanggal.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        Label_tanggal.setText("-");

        Button_AddDate.setBackground(new java.awt.Color(255, 255, 255));
        Button_AddDate.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Button_AddDate.setText("Tentukan Sebagai hari libur");
        Button_AddDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_AddDateActionPerformed(evt);
            }
        });

        Table_Libur.setAutoCreateRowSorter(true);
        Table_Libur.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tahun", "Bulan", "Tanggal Libur", "Hari", "Keterangan"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class
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
        jScrollPane1.setViewportView(Table_Libur);

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel2.setText("Tabel Daftar Libur PT. Waleta Asia Jaya");

        MonthChooser1.setEnabled(false);
        MonthChooser1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        YearChooser1.setBackground(new java.awt.Color(255, 255, 255));
        YearChooser1.setEnabled(false);

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel3.setText("Total Libur :");

        Label_total.setBackground(new java.awt.Color(255, 255, 255));
        Label_total.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Label_total.setText("-");

        CheckBox_filter.setBackground(new java.awt.Color(255, 255, 255));
        CheckBox_filter.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        CheckBox_filter.setText("(ON/OFF) Filter");
        CheckBox_filter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CheckBox_filterActionPerformed(evt);
            }
        });

        jButton1.setBackground(new java.awt.Color(255, 255, 255));
        jButton1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jButton1.setText("Refresh");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        Label_hari.setBackground(new java.awt.Color(255, 255, 255));
        Label_hari.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        Label_hari.setText("hari");

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel4.setText(",");

        button_delete.setBackground(new java.awt.Color(255, 255, 255));
        button_delete.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_deleteActionPerformed(evt);
            }
        });

        Button_AddDate1.setBackground(new java.awt.Color(255, 255, 255));
        Button_AddDate1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Button_AddDate1.setText("Tentukan Sebagai hari Cuti Bersama");
        Button_AddDate1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_AddDate1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(MonthChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(YearChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(CheckBox_filter)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_delete, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(25, 25, 25)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Label_total))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 864, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jCalendar1, javax.swing.GroupLayout.PREFERRED_SIZE, 288, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Label_hari)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Label_tanggal))
                            .addComponent(Button_AddDate)
                            .addComponent(Button_AddDate1)))
                    .addComponent(jLabel2))
                .addGap(115, 188, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(MonthChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(YearChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(Label_total, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(CheckBox_filter, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(button_delete, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jCalendar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(Label_tanggal)
                            .addComponent(Label_hari)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Button_AddDate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Button_AddDate1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 597, Short.MAX_VALUE))
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
            // TODO add your handling code here:
            String Keterangan = JOptionPane.showInputDialog("Keterangan : ");
            if (Keterangan != null && !Keterangan.equals("")) {
                sql = "INSERT INTO `tb_libur`(`tanggal_libur`, `keterangan`) VALUES ('" + dateFormat.format(jCalendar1.getDate()) + "','" + Keterangan + "')";
                Utility.db.getConnection().createStatement();
                if ((Utility.db.getStatement().executeUpdate(sql)) > 0) {
                    JOptionPane.showMessageDialog(this, "Tanggal " + Label_tanggal.getText() + " telah di tentukan sebagai hari libur");
                    refreshTable_Libur();
                } else {
                    JOptionPane.showMessageDialog(this, "Input Error");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_Data_TglLibur.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_Button_AddDateActionPerformed

    private void jCalendar1PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jCalendar1PropertyChange
        // TODO add your handling code here:
        Label_tanggal.setText(new SimpleDateFormat("dd MMMM yyyy").format(jCalendar1.getDate()));
        Label_hari.setText(new SimpleDateFormat("EEEE", Locale.ENGLISH).format(jCalendar1.getDate()));
    }//GEN-LAST:event_jCalendar1PropertyChange

    private void CheckBox_filterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CheckBox_filterActionPerformed
        // TODO add your handling code here:
        if (CheckBox_filter.isSelected()) {
            YearChooser1.setEnabled(true);
            MonthChooser1.setEnabled(true);
            refreshTable_Libur();
        } else {
            YearChooser1.setEnabled(false);
            MonthChooser1.setEnabled(false);
            refreshTable_Libur();
        }
    }//GEN-LAST:event_CheckBox_filterActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        refreshTable_Libur();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void button_deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_deleteActionPerformed
        // TODO add your handling code here:
        try {
            int j = Table_Libur.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih data yang ingin di hapus !");
            } else {
                int dialogResult = JOptionPane.showConfirmDialog(this, "Yakin hapus data ini?", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    // delete code here
                    try {
                        String Query = "DELETE FROM `tb_libur` WHERE `tanggal_libur` = '" + Table_Libur.getValueAt(j, 2) + "'";
                        Utility.db.getConnection().createStatement();
                        Utility.db.getStatement().executeUpdate(Query);
                        refreshTable_Libur();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(this, ex.getMessage());
                        Logger.getLogger(JPanel_Data_TglLibur.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }//GEN-LAST:event_button_deleteActionPerformed

    private void Button_AddDate1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_AddDate1ActionPerformed
        // TODO add your handling code here:
        try {
            // TODO add your handling code here:
            String Keterangan = JOptionPane.showInputDialog("Keterangan Cuti Bersama : ", "Cuti Bersama ");
            if (Keterangan != null && !Keterangan.equals("")) {
                sql = "INSERT INTO `tb_libur`(`tanggal_libur`, `keterangan`) VALUES ('" + dateFormat.format(jCalendar1.getDate()) + "','" + Keterangan + "')";
                Utility.db.getConnection().createStatement();
                if ((Utility.db.getStatement().executeUpdate(sql)) > 0) {
                    JOptionPane.showMessageDialog(this, "Tanggal " + Label_tanggal.getText() + " telah di tentukan sebagai hari libur");
                    refreshTable_Libur();
                } else {
                    JOptionPane.showMessageDialog(this, "Input Error");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_Data_TglLibur.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_Button_AddDate1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Button_AddDate;
    private javax.swing.JButton Button_AddDate1;
    private javax.swing.JCheckBox CheckBox_filter;
    private javax.swing.JLabel Label_hari;
    private javax.swing.JLabel Label_tanggal;
    private javax.swing.JLabel Label_total;
    private com.toedter.calendar.JMonthChooser MonthChooser1;
    private javax.swing.JTable Table_Libur;
    private com.toedter.calendar.JYearChooser YearChooser1;
    private javax.swing.JButton button_delete;
    private javax.swing.JButton jButton1;
    private com.toedter.calendar.JCalendar jCalendar1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
