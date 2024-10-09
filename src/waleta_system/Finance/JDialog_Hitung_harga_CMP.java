package waleta_system.Finance;

import waleta_system.Class.Utility;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import waleta_system.Class.ColumnsAutoSizer;

import waleta_system.Class.Utility;

public class JDialog_Hitung_harga_CMP extends javax.swing.JDialog {

    
    String sql = null;
    ResultSet rs;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    java.util.Date today = new java.util.Date();//date berisikan tanggal hari ini
    DecimalFormat decimalFormat = new DecimalFormat();

    public JDialog_Hitung_harga_CMP(java.awt.Frame parent, boolean modal, String no_kartu) {
        super(parent, modal);
        initComponents();
        
        label_no_kartu.setText(no_kartu);
        try {
            
            decimalFormat.setMaximumFractionDigits(5);
            decimalFormat.setGroupingUsed(false);
            decimalFormat = Utility.DecimalFormatUS(decimalFormat);
            DefaultTableModel model = (DefaultTableModel) Tabel_data.getModel();
            model.setRowCount(0);
            sql = "SELECT `kode_kartu_cmp`, A.`no_kartu_waleta`, A.`kode_grade`, A.`harga_bahanbaku`, `keping`, `gram`, B.`harga_bahanbaku` AS 'harga_cmp' \n"
                    + "FROM `tb_kartu_cmp_detail` "
                    + "LEFT JOIN `tb_grading_bahan_baku` A ON `tb_kartu_cmp_detail`.`no_grading` = A.`no_grading`\n"
                    + "LEFT JOIN `tb_grading_bahan_baku` B ON `tb_kartu_cmp_detail`.`kode_kartu_cmp` = B.`no_kartu_waleta`\n"
                    + "WHERE A.`no_kartu_waleta` = '" + no_kartu + "'"
                    + "GROUP BY `kode_kartu_cmp`";
            PreparedStatement pst = Utility.db.getConnection().prepareStatement(sql);
            rs = pst.executeQuery();
            Object[] row = new Object[9];
            while (rs.next()) {
                row[0] = rs.getString("kode_kartu_cmp");
                row[1] = decimalFormat.format(rs.getDouble("harga_cmp"));
                row[2] = rs.getString("no_kartu_waleta");
                row[3] = rs.getString("kode_grade");
                row[4] = rs.getInt("keping");
                row[5] = rs.getInt("gram");
                row[6] = decimalFormat.format(rs.getDouble("harga_bahanbaku"));

                String b = "SELECT (SUM(`gram`*`harga_bahanbaku`) / SUM(`gram`)) AS 'harga_baru' \n"
                        + "FROM `tb_kartu_cmp_detail` JOIN `tb_grading_bahan_baku` ON `tb_kartu_cmp_detail`.`no_grading` =  `tb_grading_bahan_baku`.`no_grading`\n"
                        + "WHERE `kode_kartu_cmp` = '" + rs.getString("kode_kartu_cmp") + "'";
                PreparedStatement pst1 = Utility.db.getConnection().prepareStatement(b);
                ResultSet rs1 = pst1.executeQuery();
                if (rs1.next()) {
                    row[7] = decimalFormat.format(rs1.getDouble("harga_baru"));
                }
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(Tabel_data);
        } catch (Exception ex) {
            Logger.getLogger(JDialog_Hitung_harga_CMP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        label_no_kartu = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        Tabel_data = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel1.setText("Kartu Baku :");

        label_no_kartu.setBackground(new java.awt.Color(255, 255, 255));
        label_no_kartu.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_no_kartu.setText("-----");

        Tabel_data.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Tabel_data.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "CMP", "Harga CMP", "No Kartu", "Grade", "Keping", "Gram", "Harga", "Harga CMP baru"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.String.class, java.lang.String.class
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
        Tabel_data.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(Tabel_data);

        jButton1.setBackground(new java.awt.Color(255, 255, 255));
        jButton1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jButton1.setText("SAVE");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 2, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(153, 153, 153));
        jLabel2.setText("Hasil perhitungan akan salah jika hasil grading dari 1 kartu CMP lebih dari 1");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_no_kartu)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton1))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 625, Short.MAX_VALUE))
                        .addGap(15, 15, 15))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(label_no_kartu)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 339, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
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

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        int berhasil = 0, gagal = 0;
        try {
            for (int i = 0; i < Tabel_data.getRowCount(); i++) {
                String cmp = Tabel_data.getValueAt(i, 0).toString();
                float harga = Float.valueOf(Tabel_data.getValueAt(i, 7).toString());
//                System.out.println(harga);
                String b = "UPDATE `tb_grading_bahan_baku` SET "
                        + "`harga_bahanbaku`=" + harga + ""
                        + "WHERE `no_kartu_waleta` = '" + cmp + "'";
                Utility.db.getConnection().createStatement();
                if ((Utility.db.getStatement().executeUpdate(b)) > 0) {
                    System.out.println("OKE");
                    berhasil++;
                } else {
                    System.out.println("FAIL");
                    gagal++;
                }
            }
        } catch (NumberFormatException | SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JDialog_Hitung_harga_CMP.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            JOptionPane.showMessageDialog(this, "berhasil masuk : " + berhasil + ", gagal : " + gagal);
            this.dispose();
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(JDialog_Hitung_harga_CMP.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JDialog_Hitung_harga_CMP.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JDialog_Hitung_harga_CMP.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JDialog_Hitung_harga_CMP.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JDialog_Hitung_harga_CMP dialog = new JDialog_Hitung_harga_CMP(new javax.swing.JFrame(), true, "21P039");
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
    private javax.swing.JTable Tabel_data;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel label_no_kartu;
    // End of variables declaration//GEN-END:variables
}
