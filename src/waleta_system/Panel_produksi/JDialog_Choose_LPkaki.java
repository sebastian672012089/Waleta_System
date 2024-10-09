package waleta_system.Panel_produksi;

import waleta_system.Class.Utility;
import java.awt.event.KeyEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import waleta_system.BahanJadi.JPanel_BoxBahanJadi;

public class JDialog_Choose_LPkaki extends javax.swing.JDialog {

     
    String sql = null;
    ResultSet rs;
    Date date = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    int row = 0;
    String no_lp_kaki = "-";
    float get_stok = 0;
    float gram_kaki = 0;

    public JDialog_Choose_LPkaki(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        tabel_data_lp_kaki.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && tabel_data_lp_kaki.getSelectedRow() != -1) {
                    row = tabel_data_lp_kaki.getSelectedRow();
                    label_no_lp.setText(tabel_data_lp_kaki.getValueAt(row, 0).toString());
                    txt_gram.setText(tabel_data_lp_kaki.getValueAt(row, 3).toString());
                }
            }
        });
        refreshTable_LP_suwir();
    }

    public void refreshTable_LP_suwir() {
        try {
            
            DefaultTableModel model = (DefaultTableModel) tabel_data_lp_kaki.getModel();
            model.setRowCount(0);
            sql = "SELECT `tb_lp_suwir`.`no_lp_suwir`, `tgl_lp_suwir`, `keping`, `gram_akhir`\n"
                    + "FROM `tb_lp_suwir` WHERE `tb_lp_suwir`.`no_lp_suwir` LIKE '%" + txt_search.getText() + "%' "
                    + "AND YEAR(`tgl_lp_suwir`) >= (YEAR(CURRENT_DATE)-1)";
            PreparedStatement pst = Utility.db.getConnection().prepareStatement(sql);
            rs = pst.executeQuery();
            Object[] baris = new Object[4];
            while (rs.next()) {
                baris[0] = rs.getString("no_lp_suwir");
                baris[1] = rs.getInt("keping");
                baris[2] = rs.getFloat("gram_akhir");
                String lp_kaki = rs.getString("no_lp_suwir");
                float keluar_f2 = 0;
//                String sql1 = "SELECT `no_laporan_produksi`, `tambahan_kaki1`, `lp_kaki1`, `tambahan_kaki2`, `lp_kaki2` FROM `tb_finishing_2` "
//                        + "WHERE `lp_kaki1` = '" + lp_kaki + "' OR `lp_kaki2` = '" + lp_kaki + "'";
                String sql1 = "SELECT SUM(IF(`lp_kaki1` = '" + lp_kaki + "', `tambahan_kaki1`, 0)) AS 'tambahan_kaki1', "
                        + "SUM(IF(`lp_kaki2` = '" + lp_kaki + "', `tambahan_kaki2`, 0)) AS 'tambahan_kaki2' "
                        + "FROM `tb_finishing_2` WHERE `lp_kaki1` = '" + lp_kaki + "' OR `lp_kaki2` = '" + lp_kaki + "'";
                pst = Utility.db.getConnection().prepareStatement(sql1);
                ResultSet rs_keluar1 = pst.executeQuery();
                if (rs_keluar1.next()) {
                    keluar_f2 = rs_keluar1.getFloat("tambahan_kaki1") + rs_keluar1.getFloat("tambahan_kaki2");
//                    if (rs_keluar1.getString("lp_kaki1").equals(lp_kaki)) {
//                        keluar_f2 = keluar_f2 + rs_keluar1.getFloat("tambahan_kaki1");
//                    } else if (rs_keluar1.getString("lp_kaki2").equals(lp_kaki)) {
//                        keluar_f2 = keluar_f2 + rs_keluar1.getFloat("tambahan_kaki2");
//                    }
                }
                float keluar_reproses = 0;
//                String sql2 = "SELECT `no_lp_suwir`, `no_lp_suwir2`, `gram_kaki`, `gram_kaki2` FROM `tb_reproses` "
//                        + "WHERE `no_lp_suwir` = '" + lp_kaki + "' OR `no_lp_suwir2` = '" + lp_kaki + "'";
                String sql2 = "SELECT SUM(IF(`no_lp_suwir` = '" + lp_kaki + "', `gram_kaki`, 0)) AS 'keluar_reproses1', "
                        + "SUM(IF(`no_lp_suwir2` = '" + lp_kaki + "', `gram_kaki2`, 0)) AS 'keluar_reproses2' "
                        + "FROM `tb_reproses` WHERE `no_lp_suwir` = '" + lp_kaki + "' OR `no_lp_suwir2` = '" + lp_kaki + "'";
                pst = Utility.db.getConnection().prepareStatement(sql2);
                ResultSet rs_keluar2 = pst.executeQuery();
                if (rs_keluar2.next()) {
                    keluar_reproses = rs_keluar2.getFloat("keluar_reproses1") + rs_keluar2.getFloat("keluar_reproses2");
//                    if (rs_keluar2.getString("no_lp_suwir").equals(lp_kaki)) {
//                        keluar_reproses = keluar_reproses + rs_keluar2.getFloat("gram_kaki");
//                    } else if (rs_keluar2.getString("no_lp_suwir2").equals(lp_kaki)) {
//                        keluar_reproses = keluar_reproses + rs_keluar2.getFloat("gram_kaki2");
//                    }
                }
                float stok = rs.getFloat("gram_akhir") - (keluar_f2 + keluar_reproses);
                baris[3] = stok;
                if (stok > 0) {
                    model.addRow(baris);
                }
            }
//            ColumnsAutoSizer.sizeColumnsToFit(tabel_data_noLP);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_BoxBahanJadi.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(JDialog_Choose_LPkaki.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String get_lpKaki() {
        return no_lp_kaki;
    }

    public float get_stok() {
        return get_stok;
    }

    public float get_gramKaki() {
        return gram_kaki;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabel_data_lp_kaki = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        txt_search = new javax.swing.JTextField();
        button_search = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        label_no_lp = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txt_gram = new javax.swing.JTextField();
        button_save = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Daftar LP Kaki", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 12))); // NOI18N

        tabel_data_lp_kaki.setAutoCreateRowSorter(true);
        tabel_data_lp_kaki.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No LP", "Kpg", "Gram", "Stok"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.Float.class, java.lang.Float.class
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
        tabel_data_lp_kaki.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tabel_data_lp_kaki);

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("Search no LP :");

        txt_search.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_searchKeyPressed(evt);
            }
        });

        button_search.setBackground(new java.awt.Color(255, 255, 255));
        button_search.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search.setText("Go");
        button_search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_searchActionPerformed(evt);
            }
        });

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("NO LP Kaki :");

        label_no_lp.setBackground(new java.awt.Color(255, 255, 255));
        label_no_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_no_lp.setText("-");

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel4.setText("Gram :");

        txt_gram.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_gram.setText("0");

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
        jLabel3.setText("Note : Hanya menampilkan LP Suwir tahun lalu dan tahun ini dan stok > 0");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_search)
                        .addGap(56, 56, 56))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_no_lp, javax.swing.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_gram, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_save)
                        .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_search, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 323, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_no_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_gram, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_save, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
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
        refreshTable_LP_suwir();
    }//GEN-LAST:event_button_searchActionPerformed

    private void button_saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_saveActionPerformed
        // TODO add your handling code here:
//        JOptionPane.showMessageDialog(this, get_gramKaki());
        try {
            int i = tabel_data_lp_kaki.getSelectedRow();
            float stok = Float.valueOf(tabel_data_lp_kaki.getValueAt(i, 3).toString());
            float ambil = Float.valueOf(txt_gram.getText());
            if (ambil > stok) {
                JOptionPane.showMessageDialog(this, "Maaf anda mengambil melebihi stok yang ada !");
            } else {
                no_lp_kaki = label_no_lp.getText();
                get_stok = stok;
                gram_kaki = ambil;
                this.dispose();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }//GEN-LAST:event_button_saveActionPerformed

    private void txt_searchKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_searchKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_LP_suwir();
        }
    }//GEN-LAST:event_txt_searchKeyPressed

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JDialog_Choose_LPkaki.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JDialog_Choose_LPkaki dialog = new JDialog_Choose_LPkaki(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton button_save;
    private javax.swing.JButton button_search;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel label_no_lp;
    private javax.swing.JTable tabel_data_lp_kaki;
    private javax.swing.JTextField txt_gram;
    private javax.swing.JTextField txt_search;
    // End of variables declaration//GEN-END:variables
}
