package waleta_system.BahanJadi;

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

import waleta_system.Class.Utility;

public class JDialog_EditBox extends javax.swing.JDialog {

    
    String sql = null;
    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    String kode_tutupan, grade;
    int keping = 0, tot_keping = 0;
    float tot_gram = 0, gram = 0;

    public JDialog_EditBox(java.awt.Frame parent, boolean modal, String kode_tutupan, String grade, int keping, float gram) {
        super(parent, modal);
        initComponents();
        this.kode_tutupan = kode_tutupan;
        this.grade = grade;
        this.keping = keping;
        this.gram = gram;
        try {
            label_no_tutupan.setText(kode_tutupan);
            label_grade.setText(grade);
            label_keping.setText(Integer.toString(keping));
            label_gram.setText(Float.toString(gram));
            
            
            getDataEdit();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JDialog_EditBox.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void getDataEdit() {
        DefaultTableModel model = (DefaultTableModel) table_daftar_box.getModel();
        int i = JPanel_TutupanGradingBahanJadi.Table_RincianBox.getRowCount();
        for (int j = 0; j < i; j++) {
            model.addRow(new Object[]{
                JPanel_TutupanGradingBahanJadi.Table_RincianBox.getValueAt(j, 0),
                JPanel_TutupanGradingBahanJadi.Table_RincianBox.getValueAt(j, 1),
                JPanel_TutupanGradingBahanJadi.Table_RincianBox.getValueAt(j, 2)});
        }
    }

    public void count() {
        tot_keping = 0;
        tot_gram = 0;
        int x = table_daftar_box.getRowCount();
        for (int i = 0; i < x; i++) {
            try {
                tot_keping = tot_keping + Integer.valueOf(table_daftar_box.getValueAt(i, 1).toString());
                tot_gram = tot_gram + Float.valueOf(table_daftar_box.getValueAt(i, 2).toString());
            } catch (NullPointerException e) {
                JOptionPane.showMessageDialog(this, "Tidak Boleh ada data yang kosong !");
            }
        }
        label_total.setText(Integer.toString(x));
        label_total_kpg.setText(Integer.toString(tot_keping));
        label_total_gram.setText(decimalFormat.format(tot_gram));
    }

    public String newBox(String grade) {
        String no_box_baru = null;
        try {
            //TODO add your handling code here:
            
            

            String kode_grade = null;
            String nomor_box = null;
            int total_box = 0;
            sql = "SELECT COUNT(`no_box`) AS 'total_box', `tb_grade_bahan_jadi`.`kode` "
                    + "FROM `tb_box_bahan_jadi` LEFT JOIN `tb_grade_bahan_jadi` ON `tb_box_bahan_jadi`.`kode_grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode`"
                    + "WHERE YEAR(`tanggal_box`) = '" + new SimpleDateFormat("yyyy").format(new Date()) + "' AND `tb_grade_bahan_jadi`.`kode_grade` = '" + grade + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                total_box = rs.getInt("total_box") + 1;
                kode_grade = rs.getString("kode");
            }

            if (total_box < 10) {
                nomor_box = "0000" + Integer.toString(total_box);
            } else if (total_box < 100) {
                nomor_box = "000" + Integer.toString(total_box);
            } else if (total_box < 1000) {
                nomor_box = "00" + Integer.toString(total_box);
            } else if (total_box < 10000) {
                nomor_box = "0" + Integer.toString(total_box);
            } else if (total_box < 100000) {
                nomor_box = Integer.toString(total_box);
            }

            no_box_baru = "BOX" + kode_grade + "-" + new SimpleDateFormat("yyMM").format(new Date()) + nomor_box;

        } catch (SQLException ex) {
            Logger.getLogger(JDialog_EditBox.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(JDialog_EditBox.class.getName()).log(Level.SEVERE, null, ex);
        }
        return no_box_baru;
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
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table_daftar_box = new javax.swing.JTable();
        label_no_tutupan = new javax.swing.JLabel();
        label_gram = new javax.swing.JLabel();
        label_status = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        label_total_gram = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        label_total_kpg = new javax.swing.JLabel();
        label_grade = new javax.swing.JLabel();
        button_count = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        label_total = new javax.swing.JLabel();
        label_keping = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        button_save = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Add Box");
        setBackground(new java.awt.Color(255, 255, 255));
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel3.setText("Total :");

        table_daftar_box.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_daftar_box.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No Box", "Keping", "Gram"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table_daftar_box.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(table_daftar_box);
        if (table_daftar_box.getColumnModel().getColumnCount() > 0) {
            table_daftar_box.getColumnModel().getColumn(0).setPreferredWidth(150);
        }

        label_no_tutupan.setBackground(new java.awt.Color(255, 255, 255));
        label_no_tutupan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_no_tutupan.setText("-");

        label_gram.setBackground(new java.awt.Color(255, 255, 255));
        label_gram.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_gram.setText("0");

        label_status.setBackground(new java.awt.Color(255, 255, 255));
        label_status.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_status.setText("Edit Box");

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel6.setText("Gram Asal");

        label_total_gram.setText("0");

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("Grade Barang Jadi :");

        label_total_kpg.setText("0");

        label_grade.setBackground(new java.awt.Color(255, 255, 255));
        label_grade.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_grade.setText("-");

        button_count.setBackground(new java.awt.Color(255, 255, 255));
        button_count.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_count.setText("Count");
        button_count.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_countActionPerformed(evt);
            }
        });

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("No. Tutupan :");

        jLabel5.setText("Keping :");

        label_total.setText("0");

        label_keping.setBackground(new java.awt.Color(255, 255, 255));
        label_keping.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_keping.setText("0");

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel4.setText("Kpg Asal");

        jLabel8.setText("Gram :");

        button_save.setBackground(new java.awt.Color(255, 255, 255));
        button_save.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_save.setText("Save");
        button_save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_saveActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_no_tutupan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_grade, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(label_status)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_kpg)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gram)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 85, Short.MAX_VALUE)
                        .addComponent(button_count))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(label_keping)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_gram)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel6))
                            .addComponent(button_save, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label_status)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(label_no_tutupan))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(label_grade))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel6)
                        .addComponent(label_gram))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4)
                        .addComponent(label_keping)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 295, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_kpg, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_gram, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_count, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_save, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void button_countActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_countActionPerformed
        // TODO add your handling code here:
        count();
    }//GEN-LAST:event_button_countActionPerformed

    private void button_saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_saveActionPerformed
        // TODO add your handling code here:
        count();
        if (tot_keping != keping) {
            JOptionPane.showMessageDialog(this, "Maaf Data Keping dan Gram Belum Cocok !\nSilahkan Semuanya dibagi kedalam box");
        } else {
            try {
                Utility.db.getConnection().setAutoCommit(false);
                for (int i = 0; i < table_daftar_box.getRowCount(); i++) {
                    sql = "UPDATE `tb_box_bahan_jadi` SET `keping`='" + table_daftar_box.getValueAt(i, 1) + "',`berat`='" + table_daftar_box.getValueAt(i, 2) + "' WHERE `no_box` = '" + table_daftar_box.getValueAt(i, 0) + "'";
                    Utility.db.getConnection().createStatement();
                    Utility.db.getStatement().executeUpdate(sql);
                }
                Utility.db.getConnection().commit();
                JOptionPane.showMessageDialog(this, "Data BOX berhasil Diubah");
                this.dispose();
            } catch (SQLException e) {
                try {
                    Utility.db.getConnection().rollback();
                } catch (SQLException ex) {
                    Logger.getLogger(JDialog_reProcess.class.getName()).log(Level.SEVERE, null, ex);
                }
                JOptionPane.showMessageDialog(this, e.getMessage());
                Logger.getLogger(JDialog_reProcess.class.getName()).log(Level.SEVERE, null, e);
            } finally {
                try {
                    Utility.db.getConnection().setAutoCommit(true);
                } catch (SQLException ex) {
                    Logger.getLogger(JDialog_reProcess.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_button_saveActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton button_count;
    private javax.swing.JButton button_save;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel label_grade;
    private javax.swing.JLabel label_gram;
    private javax.swing.JLabel label_keping;
    private javax.swing.JLabel label_no_tutupan;
    private javax.swing.JLabel label_status;
    private javax.swing.JLabel label_total;
    private javax.swing.JLabel label_total_gram;
    private javax.swing.JLabel label_total_kpg;
    private javax.swing.JTable table_daftar_box;
    // End of variables declaration//GEN-END:variables
}
