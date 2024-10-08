package waleta_system.SubWaleta;

import java.awt.event.KeyEvent;
import waleta_system.Class.Utility;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class JDialog_Input_Rendemen_LP_SSK extends javax.swing.JDialog {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public JDialog_Input_Rendemen_LP_SSK(java.awt.Frame parent, boolean modal, String no_lp) {
        super(parent, modal);
        this.setResizable(false);
        initComponents();
        label_no_lp.setText(no_lp);
        getdata();
    }

    public void getdata() {
        try {
            String query = "SELECT `tb_laporan_produksi_sesekan`.`no_lp_sesekan`, `tanggal_timbang`, `rendemen_bersih`, `hancuran`, `rontokan_kotor`, `rontokan_kuning`, SUM(`gram_sesekan_lp`) AS 'gram_sesekan_lp' "
                    + "FROM `tb_laporan_produksi_sesekan` "
                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_laporan_produksi_sesekan`.`no_lp_sesekan` = `tb_laporan_produksi`.`no_lp_sesekan` "
                    + "WHERE `tb_laporan_produksi_sesekan`.`no_lp_sesekan` = '" + label_no_lp.getText() + "' ";
            ResultSet rst = Utility.db.getStatement().executeQuery(query);
            if (rst.next()) {
                Date_timbang.setDate(rst.getDate("tanggal_timbang"));
                label_gram_ssk.setText(rst.getString("gram_sesekan_lp"));
                txt_rendemen_bersih.setText(rst.getString("rendemen_bersih"));
                txt_hancuran.setText(rst.getString("hancuran"));
                txt_rontokan_kotor.setText(rst.getString("rontokan_kotor"));
                txt_rontokan_kuning.setText(rst.getString("rontokan_kuning"));
                float gram_sh = rst.getFloat("gram_sesekan_lp") - (rst.getFloat("rendemen_bersih") + rst.getFloat("hancuran") + rst.getFloat("rontokan_kotor") + rst.getFloat("rontokan_kuning"));
                float persen_sh = gram_sh / rst.getFloat("gram_sesekan_lp") * 100f;
                label_susut_hilang.setText(gram_sh + " Gram (" + persen_sh + "%)");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JDialog_Input_Rendemen_LP_SSK.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void hitung_sh() {
        try {
            float gram_ssk = Float.valueOf(label_gram_ssk.getText());
            float rendemen_bersih = 0;
            if (txt_rendemen_bersih.getText() != null && !txt_rendemen_bersih.getText().equals("")) {
                rendemen_bersih = Float.valueOf(txt_rendemen_bersih.getText());
            }
            float hancuran = 0;
            if (txt_hancuran.getText() != null && !txt_hancuran.getText().equals("")) {
                hancuran = Float.valueOf(txt_hancuran.getText());
            }
            float rontokan_kotor = 0;
            if (txt_rontokan_kotor.getText() != null && !txt_rontokan_kotor.getText().equals("")) {
                rontokan_kotor = Float.valueOf(txt_rontokan_kotor.getText());
            }
            float rontokan_kuning = 0;
            if (txt_rontokan_kuning.getText() != null && !txt_rontokan_kuning.getText().equals("")) {
                rontokan_kuning = Float.valueOf(txt_rontokan_kuning.getText());
            }
            float gram_sh = gram_ssk - (rendemen_bersih + hancuran + rontokan_kotor + rontokan_kuning);
            float persen_sh = (gram_sh / gram_ssk) * 100f;
            label_susut_hilang.setText(gram_sh + " Gram (" + persen_sh + "%)");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(JDialog_Input_Rendemen_LP_SSK.class.getName()).log(Level.SEVERE, null, e);
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
        button_save = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        Date_timbang = new com.toedter.calendar.JDateChooser();
        jLabel1 = new javax.swing.JLabel();
        label_no_lp = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txt_rontokan_kotor = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        txt_hancuran = new javax.swing.JTextField();
        txt_rontokan_kuning = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        label_susut_hilang = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        txt_rendemen_bersih = new javax.swing.JTextField();
        label_gram_ssk = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Edit Data Cabut");
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        button_save.setBackground(new java.awt.Color(255, 255, 255));
        button_save.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_save.setText("Save");
        button_save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_saveActionPerformed(evt);
            }
        });

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("Tanggal Timbang :");

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel11.setText("Gram SSK :");

        Date_timbang.setBackground(new java.awt.Color(255, 255, 255));
        Date_timbang.setDate(new Date());
        Date_timbang.setDateFormatString("dd MMM yyyy");
        Date_timbang.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Date_timbang.setMaxSelectableDate(new Date());

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel1.setText("Input Rendemen LP SSK");

        label_no_lp.setBackground(new java.awt.Color(255, 255, 255));
        label_no_lp.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        label_no_lp.setText("NO. Laporan Produksi");

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel12.setText("Rontokan Kotor :");

        txt_rontokan_kotor.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_rontokan_kotor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_rontokan_kotorKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_rontokan_kotorKeyTyped(evt);
            }
        });

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel13.setText("Hancuran :");

        txt_hancuran.setBackground(new java.awt.Color(255, 255, 255));
        txt_hancuran.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_hancuran.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_hancuranKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_hancuranKeyTyped(evt);
            }
        });

        txt_rontokan_kuning.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_rontokan_kuning.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_rontokan_kuningKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_rontokan_kuningKeyTyped(evt);
            }
        });

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel14.setText("Rontokan Kuning :");

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel15.setText("Susut Hilang :");

        label_susut_hilang.setBackground(new java.awt.Color(255, 255, 255));
        label_susut_hilang.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_susut_hilang.setText("0 Gram (0%)");

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel16.setText("Rendemen Bersih :");

        txt_rendemen_bersih.setBackground(new java.awt.Color(255, 255, 255));
        txt_rendemen_bersih.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_rendemen_bersih.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_rendemen_bersihKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_rendemen_bersihKeyTyped(evt);
            }
        });

        label_gram_ssk.setBackground(new java.awt.Color(255, 255, 255));
        label_gram_ssk.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_gram_ssk.setText("0");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_rontokan_kotor)
                            .addComponent(label_susut_hilang, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(label_gram_ssk, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txt_rendemen_bersih)
                            .addComponent(txt_rontokan_kuning)
                            .addComponent(Date_timbang, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_hancuran))
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(label_no_lp)
                            .addComponent(jLabel1)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(106, 106, 106)
                                .addComponent(button_save)))
                        .addGap(129, 129, 129))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(label_no_lp)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_timbang, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_gram_ssk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_rendemen_bersih, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_hancuran, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_rontokan_kotor, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_rontokan_kuning, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_susut_hilang, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_save)
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

    private void button_saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_saveActionPerformed
        // TODO add your handling code here:
        try {
            boolean check = true;
            if (Date_timbang.getDate() == null) {
                JOptionPane.showMessageDialog(this, "Tanggal tidak boleh kosong");
                check = false;
            }
            if (check) {
                float rendemen_bersih = 0;
                if (txt_rendemen_bersih.getText() != null && !txt_rendemen_bersih.getText().equals("")) {
                    rendemen_bersih = Float.valueOf(txt_rendemen_bersih.getText());
                }
                float hancuran = 0;
                if (txt_hancuran.getText() != null && !txt_hancuran.getText().equals("")) {
                    hancuran = Float.valueOf(txt_hancuran.getText());
                }
                float rontokan_kotor = 0;
                if (txt_rontokan_kotor.getText() != null && !txt_rontokan_kotor.getText().equals("")) {
                    rontokan_kotor = Float.valueOf(txt_rontokan_kotor.getText());
                }
                float rontokan_kuning = 0;
                if (txt_rontokan_kuning.getText() != null && !txt_rontokan_kuning.getText().equals("")) {
                    rontokan_kuning = Float.valueOf(txt_rontokan_kuning.getText());
                }
                String Query = "UPDATE `tb_laporan_produksi_sesekan` SET "
                        + "`tanggal_timbang`='" + dateFormat.format(Date_timbang.getDate()) + "',"
                        + "`rendemen_bersih`='" + rendemen_bersih + "',"
                        + "`hancuran`='" + hancuran + "',"
                        + "`rontokan_kotor`='" + rontokan_kotor + "', "
                        + "`rontokan_kuning`='" + rontokan_kuning + "' "
                        + "WHERE `no_lp_sesekan`='" + label_no_lp.getText() + "'";
                Utility.db.getConnection().createStatement();
                if ((Utility.db.getStatement().executeUpdate(Query)) == 1) {
                    JOptionPane.showMessageDialog(this, "sukses input data rendemen");
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "input data gagal");
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(JDialog_Input_Rendemen_LP_SSK.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_saveActionPerformed

    private void txt_rendemen_bersihKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_rendemen_bersihKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_rendemen_bersihKeyTyped

    private void txt_hancuranKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_hancuranKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_hancuranKeyTyped

    private void txt_rontokan_kotorKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_rontokan_kotorKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_rontokan_kotorKeyTyped

    private void txt_rontokan_kuningKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_rontokan_kuningKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_rontokan_kuningKeyTyped

    private void txt_rendemen_bersihKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_rendemen_bersihKeyReleased
        // TODO add your handling code here:
        hitung_sh();
    }//GEN-LAST:event_txt_rendemen_bersihKeyReleased

    private void txt_hancuranKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_hancuranKeyReleased
        // TODO add your handling code here:
        hitung_sh();
    }//GEN-LAST:event_txt_hancuranKeyReleased

    private void txt_rontokan_kotorKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_rontokan_kotorKeyReleased
        // TODO add your handling code here:
        hitung_sh();
    }//GEN-LAST:event_txt_rontokan_kotorKeyReleased

    private void txt_rontokan_kuningKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_rontokan_kuningKeyReleased
        // TODO add your handling code here:
        hitung_sh();
    }//GEN-LAST:event_txt_rontokan_kuningKeyReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser Date_timbang;
    private javax.swing.JButton button_save;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel label_gram_ssk;
    private javax.swing.JLabel label_no_lp;
    private javax.swing.JLabel label_susut_hilang;
    private javax.swing.JTextField txt_hancuran;
    private javax.swing.JTextField txt_rendemen_bersih;
    private javax.swing.JTextField txt_rontokan_kotor;
    private javax.swing.JTextField txt_rontokan_kuning;
    // End of variables declaration//GEN-END:variables
}
