package waleta_system.BahanBaku;

import java.awt.event.KeyEvent;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;
import waleta_system.MainForm;

public class JDialog_Pilih_PecahLP extends javax.swing.JDialog {

    String sql = null;
    ResultSet rs;
    String no_kartu, kode_grade;
    int min_kpg = 0, min_gram = 0;
    JDialog_Edit_Insert_LP dialog_edit_insert_lp;

    public JDialog_Pilih_PecahLP(java.awt.Frame parent, boolean modal, JDialog_Edit_Insert_LP dialog) {
        super(parent, modal);
        initComponents();
        this.setResizable(false);
        this.dialog_edit_insert_lp = dialog;

        Table_pecah_lp.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && Table_pecah_lp.getSelectedRow() != -1) {
                    int i = Table_pecah_lp.getSelectedRow();
                    if (i > -1) {
                    }
                }
            }
        });

        try {
            sql = "SELECT `kode_grade` FROM `tb_grade_bahan_baku` ORDER BY `kode_grade`";
            ResultSet rs1 = Utility.db.getStatement().executeQuery(sql);
            while (rs1.next()) {
                ComboBox_Search_grade.addItem(rs1.getString("kode_grade"));
            }
            AutoCompleteDecorator.decorate(ComboBox_Search_grade);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(JDialog_Pilih_PecahLP.this, e);
        }
        refreshTable();
    }

    public void refreshTable() {
        try {
            DefaultTableModel model = (DefaultTableModel) Table_pecah_lp.getModel();
            model.setRowCount(0);

            String filter_grade = "AND `tb_grading_bahan_baku`.`kode_grade` = '" + ComboBox_Search_grade.getSelectedItem().toString() + "' ";
            if (ComboBox_Search_grade.getSelectedItem().equals("All")) {
                filter_grade = "";
            }
            sql = "SELECT `tb_bahan_baku_pecah_kartu`.`kode_pecah_kartu`, `tb_grading_bahan_baku`.`no_kartu_waleta`, `tb_grading_bahan_baku`.`kode_grade`, `tb_bahan_baku_pecah_kartu`.`jenis_bulu_lp`, `tb_grading_bahan_baku`.`jumlah_keping`, `tb_bahan_baku_pecah_kartu`.`keping_upah`, `tb_bahan_baku_pecah_kartu`.`berat_basah`, `tb_bahan_baku_pecah_kartu`.`berat_riil`, `tb_bahan_baku_pecah_kartu`.`memo_lp`, `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    + "FROM `tb_bahan_baku_pecah_kartu` \n"
                    + "LEFT JOIN `tb_grading_bahan_baku` ON `tb_bahan_baku_pecah_kartu`.`no_grading` = `tb_grading_bahan_baku`.`no_grading`\n"
                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_bahan_baku_pecah_kartu`.`kode_pecah_kartu` = `tb_laporan_produksi`.`kode_pecah_lp`\n"
                    + "WHERE `tb_grading_bahan_baku`.`no_kartu_waleta` LIKE '%" + txt_no_kartu.getText() + "%' "
                    + filter_grade
                    + "AND (`kode_pecah_kartu` LIKE '%" + txt_kode_pecah.getText() + "%' OR `kode_pecah_kartu` = 1)"
                    + "AND `tb_bahan_baku_pecah_kartu`.`memo_lp` IS NOT NULL "
                    + "AND (`tb_laporan_produksi`.`no_laporan_produksi` IS NULL OR `kode_pecah_kartu` = 1) "
                    + "ORDER BY `tb_grading_bahan_baku`.`no_kartu_waleta`, `tb_grading_bahan_baku`.`kode_grade`";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[8];
            while (rs.next()) {
                row[0] = rs.getInt("kode_pecah_kartu");
                row[1] = rs.getString("no_kartu_waleta");
                row[2] = rs.getString("kode_grade");
                row[3] = rs.getString("jenis_bulu_lp");
                row[4] = rs.getInt("jumlah_keping");
                row[5] = rs.getInt("keping_upah");
                row[6] = rs.getInt("berat_basah");
                row[7] = rs.getInt("berat_riil");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_pecah_lp);

            if (Table_pecah_lp.getRowCount() == 1) {
                setData(Table_pecah_lp.getValueAt(0, 0).toString());
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setData(String kode_pecah) {
        try {
            sql = "SELECT `kode_pecah_kartu`, `tb_grading_bahan_baku`.`no_kartu_waleta`, `tb_grading_bahan_baku`.`kode_grade`, `tb_bahan_baku_pecah_kartu`.`jumlah_keping`, `keping_upah`, `berat_basah`, `jenis_bulu_lp`, `memo_lp`, "
                    + "`kaki_besar_lp`, `kaki_kecil_lp`, `hilang_kaki_lp`, `ada_susur_lp`, `ada_susur_besar_lp`, `tanpa_susur_lp`, `utuh_lp`, `hilang_ujung_lp`, `pecah_1_lp`, `pecah_2`, `jumlah_sobek`, `sobek_lepas`, `jumlah_gumpil`, `gram_sesekan_lp`, `pekerja_sesekan` \n"
                    + "FROM `tb_bahan_baku_pecah_kartu` \n"
                    + "LEFT JOIN `tb_grading_bahan_baku` ON `tb_bahan_baku_pecah_kartu`.`no_grading` = `tb_grading_bahan_baku`.`no_grading`\n"
                    + "WHERE `kode_pecah_kartu` = '" + kode_pecah + "' ";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                dialog_edit_insert_lp.label_kode_pecah_lp.setVisible(true);
                dialog_edit_insert_lp.label_kode_pecah_lp.setText(rs.getString("kode_pecah_kartu"));
                dialog_edit_insert_lp.Label_no_kartu_LP.setText(rs.getString("no_kartu_waleta"));
                dialog_edit_insert_lp.Label_kode_grade_lp.setText(rs.getString("kode_grade"));
                dialog_edit_insert_lp.button_LP_select_kartu.setEnabled(false);
                dialog_edit_insert_lp.ComboBox_jenisBulu.setSelectedItem(rs.getString("jenis_bulu_lp"));
//                dialog_edit_insert_lp.ComboBox_jenisBulu.setEnabled(false);
                dialog_edit_insert_lp.txt_jumlah_keping_lp.setText(rs.getString("jumlah_keping"));
                dialog_edit_insert_lp.txt_jumlah_keping_lp.setEditable(false);
                dialog_edit_insert_lp.txt_jumlah_keping_upah.setText(rs.getString("keping_upah"));
                dialog_edit_insert_lp.txt_jumlah_keping_upah.setEditable(false);
                dialog_edit_insert_lp.txt_berat_basah_lp.setText(rs.getString("berat_basah"));
                dialog_edit_insert_lp.txt_berat_basah_lp.setEditable(false);
                dialog_edit_insert_lp.txt_memo.setText(rs.getString("memo_lp"));
//                dialog_edit_insert_lp.txt_memo.setEditable(false);
                dialog_edit_insert_lp.txt_kaki_besar_lp.setText(rs.getString("kaki_besar_lp"));
                dialog_edit_insert_lp.txt_kaki_besar_lp.setEditable(false);
                dialog_edit_insert_lp.txt_kaki_kecil_lp.setText(rs.getString("kaki_kecil_lp"));
                dialog_edit_insert_lp.txt_kaki_kecil_lp.setEditable(false);
                dialog_edit_insert_lp.txt_hilang_kaki_lp.setText(rs.getString("hilang_kaki_lp"));
                dialog_edit_insert_lp.txt_hilang_kaki_lp.setEditable(false);
                dialog_edit_insert_lp.txt_ada_susur_lp.setText(rs.getString("ada_susur_lp"));
                dialog_edit_insert_lp.txt_ada_susur_lp.setEditable(false);
                dialog_edit_insert_lp.txt_ada_susur_besar_lp.setText(rs.getString("ada_susur_besar_lp"));
                dialog_edit_insert_lp.txt_ada_susur_besar_lp.setEditable(false);
                dialog_edit_insert_lp.txt_tanpa_susur_lp.setText(rs.getString("tanpa_susur_lp"));
                dialog_edit_insert_lp.txt_tanpa_susur_lp.setEditable(false);
                dialog_edit_insert_lp.txt_utuh_lp.setText(rs.getString("utuh_lp"));
                dialog_edit_insert_lp.txt_utuh_lp.setEditable(false);
                dialog_edit_insert_lp.txt_hilang_ujung_lp.setText(rs.getString("hilang_ujung_lp"));
                dialog_edit_insert_lp.txt_hilang_ujung_lp.setEditable(false);
                dialog_edit_insert_lp.txt_pecah_1_lp.setText(rs.getString("pecah_1_lp"));
                dialog_edit_insert_lp.txt_pecah_1_lp.setEditable(false);
                dialog_edit_insert_lp.txt_pecah_2_lp.setText(rs.getString("pecah_2"));
                dialog_edit_insert_lp.txt_pecah_2_lp.setEditable(false);
                dialog_edit_insert_lp.txt_sobek_lp.setText(rs.getString("jumlah_sobek"));
                dialog_edit_insert_lp.txt_sobek_lp.setEditable(false);
                dialog_edit_insert_lp.txt_sobek_lepas_lp.setText(rs.getString("sobek_lepas"));
                dialog_edit_insert_lp.txt_sobek_lepas_lp.setEditable(false);
                dialog_edit_insert_lp.txt_gumpil_lp.setText(rs.getString("jumlah_gumpil"));
                dialog_edit_insert_lp.txt_gumpil_lp.setEditable(false);

                if (rs.getInt("jumlah_keping") == 0) {
                    int gram_per_kpg_lp = Math.round(rs.getFloat("berat_basah") / 8f);
                    JDialog_Edit_Insert_LP.label_jumlah_kpg_lp.setText(Integer.toString(gram_per_kpg_lp));
                } else {
                    JDialog_Edit_Insert_LP.label_jumlah_kpg_lp.setText(rs.getString("jumlah_keping"));
                }
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Kode pecah tidak di temukan !");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JDialog_Edit_Insert_LP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        button_ok = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        Table_pecah_lp = new javax.swing.JTable();
        txt_no_kartu = new javax.swing.JTextField();
        button_refresh = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        ComboBox_Search_grade = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        button_cancel = new javax.swing.JButton();
        button_scan_barcode = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        txt_kode_pecah = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Data Stock Bahan Baku");
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        button_ok.setBackground(new java.awt.Color(255, 255, 255));
        button_ok.setText("OK");
        button_ok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_okActionPerformed(evt);
            }
        });

        Table_pecah_lp.setAutoCreateRowSorter(true);
        Table_pecah_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_pecah_lp.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode Pecah LP", "No Kartu", "Grade", "Bulu Upah", "Kpg", "Kpg Upah", "Berat Basah", "Berat Riil"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Table_pecah_lp.getTableHeader().setReorderingAllowed(false);
        Table_pecah_lp.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Table_pecah_lpMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(Table_pecah_lp);

        txt_no_kartu.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_no_kartu.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_no_kartuKeyPressed(evt);
            }
        });

        button_refresh.setBackground(new java.awt.Color(255, 255, 255));
        button_refresh.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_refresh.setText("Refresh");
        button_refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refreshActionPerformed(evt);
            }
        });

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("Kode Grade :");

        ComboBox_Search_grade.setEditable(true);
        ComboBox_Search_grade.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_Search_grade.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("No Kartu :");

        button_cancel.setBackground(new java.awt.Color(255, 255, 255));
        button_cancel.setText("Cancel");
        button_cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_cancelActionPerformed(evt);
            }
        });

        button_scan_barcode.setBackground(new java.awt.Color(255, 255, 255));
        button_scan_barcode.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_scan_barcode.setText("Scan Barcode");
        button_scan_barcode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_scan_barcodeActionPerformed(evt);
            }
        });

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel4.setText("Kode Pecah :");

        txt_kode_pecah.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_kode_pecah.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_kode_pecahKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(button_cancel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_ok))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_no_kartu, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_Search_grade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_kode_pecah, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_refresh)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_scan_barcode)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_no_kartu, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_Search_grade, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_kode_pecah, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_refresh, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_scan_barcode, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 306, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(button_ok)
                    .addComponent(button_cancel))
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

    private void button_okActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_okActionPerformed
        // TODO add your handling code here:
        int i = Table_pecah_lp.getSelectedRow();
        if (i > -1) {
            setData(Table_pecah_lp.getValueAt(i, 0).toString());
        } else {
            JOptionPane.showMessageDialog(this, "anda belum memilih data");
        }
    }//GEN-LAST:event_button_okActionPerformed

    private void button_cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_cancelActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_button_cancelActionPerformed

    private void button_refreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refreshActionPerformed
        // TODO add your handling code here:
        refreshTable();
    }//GEN-LAST:event_button_refreshActionPerformed

    private void button_scan_barcodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_scan_barcodeActionPerformed
        // TODO add your handling code here:
        String kode = JOptionPane.showInputDialog("Scan QR pecah LP : ");
        if (kode != null && !kode.equals("")) {
            txt_kode_pecah.setText(kode);
            refreshTable();
        }
    }//GEN-LAST:event_button_scan_barcodeActionPerformed

    private void txt_no_kartuKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_no_kartuKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_no_kartuKeyPressed

    private void txt_kode_pecahKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_kode_pecahKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_kode_pecahKeyPressed

    private void Table_pecah_lpMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Table_pecah_lpMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            int i = Table_pecah_lp.getSelectedRow();
            if (i > -1) {
                setData(Table_pecah_lp.getValueAt(i, 0).toString());
            } else {
                JOptionPane.showMessageDialog(this, "anda belum memilih data");
            }
        }
    }//GEN-LAST:event_Table_pecah_lpMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_Search_grade;
    public static javax.swing.JTable Table_pecah_lp;
    private javax.swing.JButton button_cancel;
    private javax.swing.JButton button_ok;
    private javax.swing.JButton button_refresh;
    private javax.swing.JButton button_scan_barcode;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField txt_kode_pecah;
    private javax.swing.JTextField txt_no_kartu;
    // End of variables declaration//GEN-END:variables
}
