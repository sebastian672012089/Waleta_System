package waleta_system.Finance;

import waleta_system.Class.Utility;
import java.awt.HeadlessException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;


public class JDialog_Invoice_Payment extends javax.swing.JDialog {

    
    String sql = null;
    ResultSet rs;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    DecimalFormat decimalFormat = new DecimalFormat();

    public JDialog_Invoice_Payment(java.awt.Frame parent, boolean modal, String invoice) {
        super(parent, modal);
        initComponents();
        try {
            decimalFormat.setGroupingUsed(true);
            
            
            label_invoice.setText(invoice);
            refreshTable();
        } catch (Exception ex) {
            Logger.getLogger(JDialog_Invoice_Payment.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable() {
        DefaultTableModel model = (DefaultTableModel) table_payment.getModel();
        model.setRowCount(0);
        try {
            sql = "SELECT `invoice`, `tgl_invoice`, `term_payment`, "
                    + "`currency`, `value_waleta`, `value_esta`, `value_from_jtp`, `value_to_jtp`, "
                    + "`date_payment1`, `payment1`, `date_payment2`, `payment2` "
                    + "FROM `tb_payment_report` WHERE `invoice` = '" + label_invoice.getText() + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                label_invoice_date.setText(new SimpleDateFormat("dd MMMM yyyy").format(rs.getDate("tgl_invoice")));
                label_currency.setText(rs.getString("currency"));
                label_term_of_payment.setText(rs.getString("term_payment"));
                double invoice_amount = rs.getDouble("value_waleta") + rs.getDouble("value_esta") + rs.getDouble("value_from_jtp") + rs.getDouble("value_to_jtp");
                double invoice_payment = rs.getDouble("payment1") + rs.getDouble("payment2");
                double invoice_outstanding = invoice_amount - invoice_payment;
                label_outstanding.setText(decimalFormat.format(invoice_outstanding));
                label_invoice_amount.setText(decimalFormat.format(invoice_amount));
                String date_payment1 = "", date_payment2 = "";
                if (rs.getDate("date_payment1") != null) {
                    date_payment1 = new SimpleDateFormat("dd MMMM yyyy").format(rs.getDate("date_payment1"));
                }
                if (rs.getDate("date_payment2") != null) {
                    date_payment2 = new SimpleDateFormat("dd MMMM yyyy").format(rs.getDate("date_payment2"));
                }
                model.addRow(new Object[]{"I", date_payment1, decimalFormat.format(rs.getDouble("payment1"))});
                model.addRow(new Object[]{"II", date_payment2, decimalFormat.format(rs.getDouble("payment2"))});
                model.addRow(new Object[]{"TOTAL", "", decimalFormat.format(rs.getDouble("payment1") + rs.getDouble("payment2"))});
            }

            TableAlignment.setHorizontalAlignment(JLabel.CENTER);
            for (int i = 0; i < table_payment.getColumnCount(); i++) {
                table_payment.getColumnModel().getColumn(i).setCellRenderer(TableAlignment);
            }
        } catch (SQLException ex) {
            Logger.getLogger(JDialog_Invoice_Payment.class.getName()).log(Level.SEVERE, null, ex);
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

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table_payment = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        txt_payment_amount = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        button_close = new javax.swing.JButton();
        button_pay1 = new javax.swing.JButton();
        button_delete = new javax.swing.JButton();
        label_invoice = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        label_invoice_date = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        label_currency = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        label_outstanding = new javax.swing.JLabel();
        Date_payment = new com.toedter.calendar.JDateChooser();
        button_pay2 = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        label_term_of_payment = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        label_invoice_amount = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Invoice Payment", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N

        table_payment.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        table_payment.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"I", null, null},
                {"II", null, null}
            },
            new String [] {
                "Payment", "Tanggal", "Amount"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table_payment.setFocusable(false);
        table_payment.setRowHeight(25);
        table_payment.setRowSelectionAllowed(false);
        table_payment.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(table_payment);
        if (table_payment.getColumnModel().getColumnCount() > 0) {
            table_payment.getColumnModel().getColumn(0).setMaxWidth(100);
        }

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("Payment Date :");

        txt_payment_amount.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("Payment Amount :");

        button_close.setBackground(new java.awt.Color(255, 255, 255));
        button_close.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_close.setText("Close");
        button_close.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_closeActionPerformed(evt);
            }
        });

        button_pay1.setBackground(new java.awt.Color(255, 255, 255));
        button_pay1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_pay1.setText("Save Payment 1");
        button_pay1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_pay1ActionPerformed(evt);
            }
        });

        button_delete.setBackground(new java.awt.Color(255, 255, 255));
        button_delete.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_delete.setText("Delete");
        button_delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_deleteActionPerformed(evt);
            }
        });

        label_invoice.setBackground(new java.awt.Color(255, 255, 255));
        label_invoice.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_invoice.setText("-");

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel9.setText("No Invoice :");

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel10.setText("Invoice Date :");

        label_invoice_date.setBackground(new java.awt.Color(255, 255, 255));
        label_invoice_date.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_invoice_date.setText("-");

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel12.setText("Currency :");

        label_currency.setBackground(new java.awt.Color(255, 255, 255));
        label_currency.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_currency.setText("-");

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel14.setText("AR Outstanding :");

        label_outstanding.setBackground(new java.awt.Color(255, 255, 255));
        label_outstanding.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_outstanding.setText("0");

        Date_payment.setBackground(new java.awt.Color(255, 255, 255));
        Date_payment.setDateFormatString("dd MMMM yyyy");
        Date_payment.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        button_pay2.setBackground(new java.awt.Color(255, 255, 255));
        button_pay2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_pay2.setText("Save Payment 2");
        button_pay2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_pay2ActionPerformed(evt);
            }
        });

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel13.setText("Term of Payment :");

        label_term_of_payment.setBackground(new java.awt.Color(255, 255, 255));
        label_term_of_payment.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_term_of_payment.setText("-");

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel15.setText("Invoice Amount :");

        label_invoice_amount.setBackground(new java.awt.Color(255, 255, 255));
        label_invoice_amount.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_invoice_amount.setText("0");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(button_pay1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_pay2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_delete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_close))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_payment, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_payment_amount, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_invoice, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_invoice_date, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_outstanding, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_invoice_amount, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_currency, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_term_of_payment, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_invoice, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_invoice_date, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_term_of_payment, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_currency, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_invoice_amount, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_outstanding, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(txt_payment_amount, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(Date_payment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_close, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_pay1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_delete, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_pay2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
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

    private void button_closeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_closeActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_button_closeActionPerformed

    private void button_pay1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_pay1ActionPerformed
        // TODO add your handling code here:
        Boolean Check = true;
        try {
            double payment_amount = 0;
            try {
                payment_amount = Double.valueOf(txt_payment_amount.getText());
            } catch (NumberFormatException e) {
                Check = false;
                JOptionPane.showMessageDialog(this, "Maaf Nominal yang anda masukkan salah !");
            }

            if (Check) {
                sql = "UPDATE `tb_payment_report` SET "
                        + "`date_payment1` = '" + dateFormat.format(Date_payment.getDate()) + "', "
                        + "`payment1` = '" + payment_amount + "' "
                        + "WHERE `tb_payment_report`.`invoice` = '" + label_invoice.getText() + "'";
                Utility.db.getConnection().createStatement();
                if ((Utility.db.getStatement().executeUpdate(sql)) == 1) {
                    refreshTable();
                    JOptionPane.showMessageDialog(this, "Outstanding Updated : " + label_outstanding.getText());
                } else {
                    JOptionPane.showMessageDialog(this, "data not updated");
                }
            }
        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(this, e);
        } catch (SQLException ex) {
            Logger.getLogger(JDialog_Invoice_Payment.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_pay1ActionPerformed

    private void button_deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_deleteActionPerformed
        // TODO add your handling code here:
        try {
            int dialogResult = JOptionPane.showConfirmDialog(this, "Delete payment record?", "Warning", 0);
            if (dialogResult == JOptionPane.YES_OPTION) {
                // delete code here
                sql = "UPDATE `tb_payment_report` SET "
                        + "`date_payment1` = NULL, "
                        + "`payment1` = 0, "
                        + "`date_payment2` = NULL, "
                        + "`payment2` = 0 "
                        + "WHERE `tb_payment_report`.`invoice` = '" + label_invoice.getText() + "'";
                if ((Utility.db.getStatement().executeUpdate(sql)) == 1) {
                    refreshTable();
                    JOptionPane.showMessageDialog(this, "Data deleted");
                } else {
                    JOptionPane.showMessageDialog(this, "data not updated");
                }
            }
        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(this, e);
        } catch (SQLException ex) {
            Logger.getLogger(JDialog_Invoice_Payment.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_deleteActionPerformed

    private void button_pay2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_pay2ActionPerformed
        // TODO add your handling code here:
        Boolean Check = true;
        try {
            double payment_amount = 0;
            try {
                payment_amount = Double.valueOf(txt_payment_amount.getText());
            } catch (NumberFormatException e) {
                Check = false;
                JOptionPane.showMessageDialog(this, "Maaf Nominal yang anda masukkan salah !");
            }

            if (Check) {
                sql = "UPDATE `tb_payment_report` SET "
                        + "`date_payment2` = '" + dateFormat.format(Date_payment.getDate()) + "', "
                        + "`payment2` = '" + payment_amount + "' "
                        + "WHERE `tb_payment_report`.`invoice` = '" + label_invoice.getText() + "'";
                Utility.db.getConnection().createStatement();
                if ((Utility.db.getStatement().executeUpdate(sql)) == 1) {
                    refreshTable();
                    JOptionPane.showMessageDialog(this, "Outstanding Updated : " + label_outstanding.getText());
                } else {
                    JOptionPane.showMessageDialog(this, "data not updated");
                }
            }
        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(this, e);
        } catch (SQLException ex) {
            Logger.getLogger(JDialog_Invoice_Payment.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_pay2ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser Date_payment;
    public static javax.swing.JButton button_close;
    private javax.swing.JButton button_delete;
    private javax.swing.JButton button_pay1;
    private javax.swing.JButton button_pay2;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel label_currency;
    private javax.swing.JLabel label_invoice;
    private javax.swing.JLabel label_invoice_amount;
    private javax.swing.JLabel label_invoice_date;
    private javax.swing.JLabel label_outstanding;
    private javax.swing.JLabel label_term_of_payment;
    public static javax.swing.JTable table_payment;
    private javax.swing.JTextField txt_payment_amount;
    // End of variables declaration//GEN-END:variables
}
