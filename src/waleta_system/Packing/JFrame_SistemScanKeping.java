package waleta_system.Packing;

import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;
import waleta_system.Class.Utility;

public class JFrame_SistemScanKeping extends javax.swing.JFrame {

    DecimalFormat decimalFormat = new DecimalFormat();
    
    public JFrame_SistemScanKeping() {
        initComponents();
        this.setResizable(false);
    }
    
    public void SavePrint() {
        try {
            decimalFormat.setMaximumFractionDigits(1);
            boolean check = true;
            float berat_plastik = 0, gram_timbang = 0, gram_print = 0;
            String print = "", plus_minus = "", NW = "", GW = "";
            try {
                if (!(txt_berat_timbangan.getText().equals("") || txt_berat_timbangan.getText() == null)) {
                    berat_plastik = Float.valueOf(txt_berat_plastik.getText());
                    gram_timbang = Float.valueOf(txt_berat_timbangan.getText());
                    gram_print = gram_timbang - berat_plastik;
                    print = decimalFormat.format(gram_print) + " g";
                    NW = "净重/NW:" + decimalFormat.format(gram_print) + " g ±";
                    GW = "毛重/GW:" + decimalFormat.format(gram_print + 0.3f) + " g ±";
                } else {
                    check = false;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Inputan Angka Salah !");
                check = false;
                Logger.getLogger(JPanel_DataScanKeping.class.getName()).log(Level.SEVERE, null, e);
            }

            if (check) {
                if (RadioButton1.isSelected()) {
                    plus_minus = "";
                    JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\weight_label.jrxml");
                    Map<String, Object> map = new HashMap<>();
                    map.put("GRAM_KEPING", print);//parameter name should be like it was named inside your report.
                    map.put("PLUS_MINUS", plus_minus);//parameter name should be like it was named inside your report.
                    JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
                    JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, map, Utility.db.getConnection());
                    if (CheckBox_print.isSelected()) {
                        JasperPrintManager.printReport(JASP_PRINT, false);//langsung print
                    } else {
                        JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
                    }
                } else if (RadioButton2.isSelected()) {
                    if (gram_print > 5) {
                        plus_minus = "(± 0.3 g)";
                    } else {
                        plus_minus = "(± 0.2 g)";
                    }
                    JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\weight_label.jrxml");
                    Map<String, Object> map = new HashMap<>();
                    map.put("GRAM_KEPING", print);//parameter name should be like it was named inside your report.
                    map.put("PLUS_MINUS", plus_minus);//parameter name should be like it was named inside your report.
                    JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
                    JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, map, Utility.db.getConnection());
                    if (CheckBox_print.isSelected()) {
                        JasperPrintManager.printReport(JASP_PRINT, false);//langsung print
                    } else {
                        JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
                    }
                } else if (RadioButton3.isSelected()) {
                    JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\weight_label_imperial.jrxml");
                    Map<String, Object> map = new HashMap<>();
                    map.put("NW", NW);//parameter name should be like it was named inside your report.
                    map.put("GW", GW);//parameter name should be like it was named inside your report.
                    JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
                    JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, map, Utility.db.getConnection());
                    if (CheckBox_print.isSelected()) {
                        JasperPrintManager.printReport(JASP_PRINT, false);//langsung print
                    } else {
                        JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
                    }
                } else if (RadioButton4.isSelected()) {
                    plus_minus = "";
                    JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\weight_label_QR.jrxml");
                    Map<String, Object> map = new HashMap<>();
                    map.put("QR", "http://waleta019.com/sementara");
                    map.put("GRAM_KEPING", print);//parameter name should be like it was named inside your report.
                    map.put("PLUS_MINUS", plus_minus);//parameter name should be like it was named inside your report.
                    JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
                    JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, map, Utility.db.getConnection());
                    if (CheckBox_print.isSelected()) {
                        JasperPrintManager.printReport(JASP_PRINT, false);//langsung print
                    } else {
                        JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
                    }
                } else if (RadioButton5.isSelected()) {
                    if (gram_print > 5) {
                        plus_minus = "(± 0.3 g)";
                    } else {
                        plus_minus = "(± 0.2 g)";
                    }
                    JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\weight_label_QR.jrxml");
                    Map<String, Object> map = new HashMap<>();
                    map.put("QR", "http://waleta019.com/sementara");
                    map.put("GRAM_KEPING", print);//parameter name should be like it was named inside your report.
                    map.put("PLUS_MINUS", plus_minus);//parameter name should be like it was named inside your report.
                    JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
                    JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, map, Utility.db.getConnection());
                    if (CheckBox_print.isSelected()) {
                        JasperPrintManager.printReport(JASP_PRINT, false);//langsung print
                    } else {
                        JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
                    }
                } else if (RadioButton6.isSelected()) {
                    JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\weight_label_imperial_QR.jrxml");
                    Map<String, Object> map = new HashMap<>();
                    map.put("QR", "http://waleta019.com/sementara");
                    map.put("GRAM_KEPING_CHINESE", NW.split("/")[0]);//parameter name should be like it was named inside your report.
                    map.put("PLUS_MINUS_CHINESE", GW.split("/")[0]);//parameter name should be like it was named inside your report.
                    map.put("GRAM_KEPING", NW.split("/")[1]);//parameter name should be like it was named inside your report.
                    map.put("PLUS_MINUS", GW.split("/")[1]);//parameter name should be like it was named inside your report.
                    JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
                    JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, map, Utility.db.getConnection());
                    if (CheckBox_print.isSelected()) {
                        JasperPrintManager.printReport(JASP_PRINT, false);//langsung print
                    } else {
                        JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
                    }
                }

                txt_berat_timbangan.setText(null);
                txt_berat_timbangan.requestFocus();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_DataScanKeping.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txt_berat_plastik = new javax.swing.JTextField();
        txt_berat_timbangan = new javax.swing.JTextField();
        button_print = new javax.swing.JButton();
        CheckBox_print = new javax.swing.JCheckBox();
        RadioButton6 = new javax.swing.JRadioButton();
        RadioButton5 = new javax.swing.JRadioButton();
        RadioButton4 = new javax.swing.JRadioButton();
        RadioButton3 = new javax.swing.JRadioButton();
        RadioButton2 = new javax.swing.JRadioButton();
        RadioButton1 = new javax.swing.JRadioButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Weight Label System", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel7.setText("Berat timbang (Gram) :");
        jLabel7.setFocusable(false);

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel5.setText("Berat Plastik (Gram) :");
        jLabel5.setFocusable(false);

        txt_berat_plastik.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_berat_plastik.setText("0");

        txt_berat_timbangan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_berat_timbangan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_berat_timbanganKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_berat_timbanganKeyTyped(evt);
            }
        });

        button_print.setBackground(new java.awt.Color(255, 255, 255));
        button_print.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_print.setText("Print");
        button_print.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_printActionPerformed(evt);
            }
        });

        CheckBox_print.setBackground(new java.awt.Color(255, 255, 255));
        CheckBox_print.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        CheckBox_print.setSelected(true);
        CheckBox_print.setText("Langsung Print");

        RadioButton6.setBackground(new java.awt.Color(255, 255, 255));
        RadioButton6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        RadioButton6.setText("Label Imperial +QR");

        RadioButton5.setBackground(new java.awt.Color(255, 255, 255));
        RadioButton5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        RadioButton5.setText("Label dengan (± 0.2 gr) +QR");

        RadioButton4.setBackground(new java.awt.Color(255, 255, 255));
        RadioButton4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        RadioButton4.setText("Label tanpa (± 0.2 gr) +QR");

        RadioButton3.setBackground(new java.awt.Color(255, 255, 255));
        RadioButton3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        RadioButton3.setText("Label Imperial");

        RadioButton2.setBackground(new java.awt.Color(255, 255, 255));
        RadioButton2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        RadioButton2.setText("Label dengan (± 0.2 gr)");

        RadioButton1.setBackground(new java.awt.Color(255, 255, 255));
        RadioButton1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        RadioButton1.setText("Label tanpa (± 0.2 gr)");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(CheckBox_print)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_print))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_berat_plastik, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_berat_timbangan, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE))
                    .addComponent(RadioButton1)
                    .addComponent(RadioButton2)
                    .addComponent(RadioButton3)
                    .addComponent(RadioButton4)
                    .addComponent(RadioButton5)
                    .addComponent(RadioButton6))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_berat_plastik, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_berat_timbangan, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CheckBox_print, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_print, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(RadioButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(RadioButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(RadioButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(RadioButton4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(RadioButton5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(RadioButton6)
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

    private void txt_berat_timbanganKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_berat_timbanganKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            SavePrint();
        }
    }//GEN-LAST:event_txt_berat_timbanganKeyPressed

    private void txt_berat_timbanganKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_berat_timbanganKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_berat_timbanganKeyTyped

    private void button_printActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_printActionPerformed
        // TODO add your handling code here:
        SavePrint();
    }//GEN-LAST:event_button_printActionPerformed

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
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(JFrame_SistemScanKeping.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JFrame_SistemScanKeping.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JFrame_SistemScanKeping.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JFrame_SistemScanKeping.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new JFrame_SistemScanKeping().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox CheckBox_print;
    private javax.swing.JRadioButton RadioButton1;
    private javax.swing.JRadioButton RadioButton2;
    private javax.swing.JRadioButton RadioButton3;
    private javax.swing.JRadioButton RadioButton4;
    private javax.swing.JRadioButton RadioButton5;
    private javax.swing.JRadioButton RadioButton6;
    private javax.swing.JButton button_print;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField txt_berat_plastik;
    private javax.swing.JTextField txt_berat_timbangan;
    // End of variables declaration//GEN-END:variables
}
