package waleta_system.Finance; 

import waleta_system.Class.Utility;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class JDialog_Input_CashOnBank extends javax.swing.JDialog {

    
    String sql = null;
    ResultSet rs;
    Date date = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DecimalFormat decimalFormat = new DecimalFormat();

    public JDialog_Input_CashOnBank(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txt_nominal = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txt_keterangan = new javax.swing.JTextField();
        button_print = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        ComboBox_currency = new javax.swing.JComboBox<>();
        Date_input = new com.toedter.calendar.JDateChooser();
        SpinField_jam = new com.toedter.components.JSpinField();
        SpinField_menit = new com.toedter.components.JSpinField();
        SpinField_detik = new com.toedter.components.JSpinField();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        Slider_jam = new javax.swing.JSlider();
        Slider_menit = new javax.swing.JSlider();
        Slider_detik = new javax.swing.JSlider();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Input Cash On Bank");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Timbang Keping", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 12))); // NOI18N

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel7.setText("Nominal :");
        jLabel7.setFocusable(false);

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel5.setText("Tanggal :");
        jLabel5.setFocusable(false);

        txt_nominal.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_nominal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_nominalKeyReleased(evt);
            }
        });

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel9.setText("Keterangan :");
        jLabel9.setFocusable(false);

        txt_keterangan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_keterangan.setText("-");

        button_print.setBackground(new java.awt.Color(255, 255, 255));
        button_print.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_print.setText("Save");
        button_print.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_printActionPerformed(evt);
            }
        });

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel11.setText("Currency :");

        ComboBox_currency.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        ComboBox_currency.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "BCA168-CNY", "BCA972-IDR", "BCA817-IDR", "BCA889-USD", "CIMB100-IDR", "CIMB200-IDR" }));

        Date_input.setBackground(new java.awt.Color(255, 255, 255));

        SpinField_jam.setBackground(new java.awt.Color(255, 255, 255));
        SpinField_jam.setMaximum(23);
        SpinField_jam.setMinimum(0);
        SpinField_jam.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                SpinField_jamPropertyChange(evt);
            }
        });

        SpinField_menit.setBackground(new java.awt.Color(255, 255, 255));
        SpinField_menit.setMaximum(59);
        SpinField_menit.setMinimum(0);
        SpinField_menit.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                SpinField_menitPropertyChange(evt);
            }
        });

        SpinField_detik.setBackground(new java.awt.Color(255, 255, 255));
        SpinField_detik.setMaximum(59);
        SpinField_detik.setMinimum(0);
        SpinField_detik.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                SpinField_detikPropertyChange(evt);
            }
        });

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel6.setText("Menit :");
        jLabel6.setFocusable(false);

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel8.setText("Jam :");
        jLabel8.setFocusable(false);

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel10.setText("Detik :");
        jLabel10.setFocusable(false);

        Slider_jam.setBackground(new java.awt.Color(255, 255, 255));
        Slider_jam.setMaximum(23);
        Slider_jam.setValue(0);
        Slider_jam.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                Slider_jamStateChanged(evt);
            }
        });

        Slider_menit.setBackground(new java.awt.Color(255, 255, 255));
        Slider_menit.setMaximum(59);
        Slider_menit.setValue(0);
        Slider_menit.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                Slider_menitStateChanged(evt);
            }
        });

        Slider_detik.setBackground(new java.awt.Color(255, 255, 255));
        Slider_detik.setMaximum(59);
        Slider_detik.setValue(0);
        Slider_detik.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                Slider_detikStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Date_input, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txt_nominal)
                    .addComponent(txt_keterangan)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(SpinField_menit, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Slider_menit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(SpinField_detik, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Slider_detik, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(SpinField_jam, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Slider_jam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(button_print)
                            .addComponent(ComboBox_currency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ComboBox_currency, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(5, 5, 5)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Date_input, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(SpinField_jam, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Slider_jam, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(SpinField_menit, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(Slider_menit, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(SpinField_detik, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Slider_detik, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_nominal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_keterangan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_print, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

    private void button_printActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_printActionPerformed
        // TODO add your handling code here:
        decimalFormat.setMaximumFractionDigits(1);
        boolean check = true;
        long nominal = 0;
        Date tanggal_input = Date_input.getDate();
        try {
            String amount = txt_nominal.getText().replace(",", "");
            nominal = Long.valueOf(amount);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Inputan Angka Salah !");
            check = false;
            Logger.getLogger(JPanel_DataCashOnBank.class.getName()).log(Level.SEVERE, null, e);
        }
        
        if (Date_input.getDate() == null) {
            tanggal_input = date;
        }
        
        if (check) {
            try {
                
                String waktu = Integer.toString(SpinField_jam.getValue()) + ":" + Integer.toString(SpinField_menit.getValue()) + ":" + Integer.toString(SpinField_detik.getValue());
                String Query = "INSERT INTO `tb_cash_on_bank`(`waktu_input`, `currency`, `nominal`, `keterangan`) "
                        + "VALUES ('" + dateFormat.format(tanggal_input) + " " + waktu + "','" + ComboBox_currency.getSelectedItem().toString() + "','" + nominal + "','" + txt_keterangan.getText() + "')";
                Utility.db.getConnection().createStatement();
                if ((Utility.db.getStatement().executeUpdate(Query)) > 0) {
                    JOptionPane.showMessageDialog(this, "data saved !");
                } else {
                    JOptionPane.showMessageDialog(this, "data failed !");
                }
                this.dispose();
            } catch (SQLException ex) {
                Logger.getLogger(JDialog_Input_CashOnBank.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(JDialog_Input_CashOnBank.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_button_printActionPerformed

    private void Slider_jamStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_Slider_jamStateChanged
        // TODO add your handling code here:
        SpinField_jam.setValue(Slider_jam.getValue());
    }//GEN-LAST:event_Slider_jamStateChanged

    private void Slider_menitStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_Slider_menitStateChanged
        // TODO add your handling code here:
        SpinField_menit.setValue(Slider_menit.getValue());
    }//GEN-LAST:event_Slider_menitStateChanged

    private void Slider_detikStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_Slider_detikStateChanged
        // TODO add your handling code here:
        SpinField_detik.setValue(Slider_detik.getValue());
    }//GEN-LAST:event_Slider_detikStateChanged

    private void SpinField_jamPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_SpinField_jamPropertyChange
        // TODO add your handling code here:
        Slider_jam.setValue(SpinField_jam.getValue());
    }//GEN-LAST:event_SpinField_jamPropertyChange

    private void SpinField_menitPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_SpinField_menitPropertyChange
        // TODO add your handling code here:
        Slider_menit.setValue(SpinField_menit.getValue());
    }//GEN-LAST:event_SpinField_menitPropertyChange

    private void SpinField_detikPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_SpinField_detikPropertyChange
        // TODO add your handling code here:
        Slider_detik.setValue(SpinField_detik.getValue());
    }//GEN-LAST:event_SpinField_detikPropertyChange

    private void txt_nominalKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_nominalKeyReleased
        // TODO add your handling code here:
        String nominal = txt_nominal.getText();
        nominal = nominal.replace(",", "");
        int digit = nominal.length();
        ArrayList<String> text_nominal = new ArrayList<>();
        for (int i = 0; i < (int) (digit/3); i++) {
            text_nominal.add(nominal.substring(digit-(3 * (i + 1)), digit-(3 * i)));
        }
        if (digit%3 > 0) {
            text_nominal.add(nominal.substring(0, digit%3));
        }
        
        String x = "";
        for (int i = text_nominal.size()-1; i >= 0; i--) {
            x = x + text_nominal.get(i);
            if (i > 0) {
                x = x + ",";
            }
        }
        txt_nominal.setText(x);
    }//GEN-LAST:event_txt_nominalKeyReleased

    /**
     * @param args the command line arguments
     */
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
            java.util.logging.Logger.getLogger(JDialog_Input_CashOnBank.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JDialog_Input_CashOnBank.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JDialog_Input_CashOnBank.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JDialog_Input_CashOnBank.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JDialog_Input_CashOnBank dialog = new JDialog_Input_CashOnBank(new javax.swing.JFrame(), true);
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
    private javax.swing.JComboBox<String> ComboBox_currency;
    private com.toedter.calendar.JDateChooser Date_input;
    private javax.swing.JSlider Slider_detik;
    private javax.swing.JSlider Slider_jam;
    private javax.swing.JSlider Slider_menit;
    private com.toedter.components.JSpinField SpinField_detik;
    private com.toedter.components.JSpinField SpinField_jam;
    private com.toedter.components.JSpinField SpinField_menit;
    private javax.swing.JButton button_print;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField txt_keterangan;
    private javax.swing.JTextField txt_nominal;
    // End of variables declaration//GEN-END:variables
}
