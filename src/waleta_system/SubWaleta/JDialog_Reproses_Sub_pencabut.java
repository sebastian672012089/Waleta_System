package waleta_system.SubWaleta;

import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableCellRenderer;
import waleta_system.Class.Utility;

public class JDialog_Reproses_Sub_pencabut extends javax.swing.JDialog {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    String no_reproses = null, no = null;

    public JDialog_Reproses_Sub_pencabut(java.awt.Frame parent, boolean modal, String no_reproses, String no) {
        super(parent, modal);
        initComponents();
        this.no_reproses = no_reproses;
        if (no != null) {
            this.no = no;
            try {
                sql = "SELECT `no`, `no_reproses`, `tb_reproses_sub_pencabut`.`id_pegawai`, `tb_karyawan`.`nama_pegawai`, `tb_karyawan`.`bagian`, `tanggal_cabut`, `jumlah_cabut`, `jumlah_gram` \n"
                        + "FROM `tb_reproses_sub_pencabut` \n"
                        + "LEFT JOIN `tb_karyawan` ON `tb_reproses_sub_pencabut`.`id_pegawai` = `tb_karyawan`.`id_pegawai`\n"
                        + "WHERE `no` = '" + no + "'";
                rs = Utility.db_sub.getStatement().executeQuery(sql);
                if (rs.next()) {
                    txt_id_pegawai.setText(rs.getString("id_pegawai"));
                    txt_nama_pegawai.setText(rs.getString("nama_pegawai"));
                    txt_bagian.setText(rs.getString("bagian"));
                    Date_cabut.setDate(rs.getDate("tanggal_cabut"));
                    txt_jmlh_keping.setText(rs.getString("jumlah_cabut"));
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex);
                Logger.getLogger(JDialog_Reproses_Sub_pencabut.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void AddPencabut() {
        try {
            decimalFormat.setGroupingUsed(false);
            decimalFormat.setMaximumFractionDigits(2);
            float keping_cabut = Float.valueOf(txt_jmlh_keping.getText());
            float berat_box = 0, kpg_box = 0, gram_cabutan = 0;
            String query = "SELECT `keping`, `gram` FROM `tb_reproses_sub` WHERE `no_reproses` = '" + no_reproses + "'";
            ResultSet result = Utility.db_sub.getStatement().executeQuery(query);
            if (result.next()) {
                berat_box = result.getFloat("gram");
                kpg_box = result.getFloat("keping");
                if (kpg_box > 0) {
                    gram_cabutan = (berat_box / kpg_box) * keping_cabut;
                } else {
                    gram_cabutan = (berat_box / Math.round(berat_box / 6f)) * keping_cabut;
                }
            }

            sql = "INSERT INTO `tb_reproses_sub_pencabut`(`no_reproses`, `id_pegawai`, `tanggal_cabut`, `jumlah_cabut`, `jumlah_gram`) "
                    + "VALUES ("
                    + "'" + no_reproses + "',"
                    + "'" + txt_id_pegawai.getText() + "',"
                    + "'" + dateFormat.format(Date_cabut.getDate()) + "',"
                    + "'" + keping_cabut + "', "
                    + "" + decimalFormat.format(gram_cabutan) + " "
                    + ")";
            System.out.println(sql);
            Utility.db_sub.getConnection().createStatement();
            if ((Utility.db_sub.getStatement().executeUpdate(sql)) == 1) {
                JOptionPane.showMessageDialog(this, "data Saved !");
            } else {
                JOptionPane.showMessageDialog(this, "Failed !");
            }
            this.dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JDialog_Reproses_Sub_pencabut.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void EditPencabut() {
        try {
            decimalFormat.setGroupingUsed(false);
            decimalFormat.setMaximumFractionDigits(2);
            float keping_cabut = Float.valueOf(txt_jmlh_keping.getText());
            float berat_box = 0, kpg_box = 0, gram_cabutan = 0;
            String query = "SELECT `keping`, `gram` FROM `tb_reproses_sub` WHERE `no_reproses` = '" + no_reproses + "'";
            ResultSet result = Utility.db_sub.getStatement().executeQuery(query);
            if (result.next()) {
                berat_box = result.getFloat("gram");
                kpg_box = result.getFloat("keping");
                if (kpg_box > 0) {
                    gram_cabutan = (berat_box / kpg_box) * keping_cabut;
                } else {
                    gram_cabutan = (berat_box / Math.round(berat_box / 6)) * keping_cabut;
                }
            }

            sql = "UPDATE `tb_reproses_sub_pencabut` SET "
                    + "`id_pegawai`='" + txt_id_pegawai.getText() + "',"
                    + "`tanggal_cabut`='" + dateFormat.format(Date_cabut.getDate()) + "',"
                    + "`jumlah_cabut`='" + keping_cabut + "', "
                    + "`jumlah_gram`='" + decimalFormat.format(gram_cabutan) + "' "
                    + "WHERE "
                    + "`no`='" + no + "'";
            Utility.db_sub.getConnection().createStatement();
            if ((Utility.db_sub.getStatement().executeUpdate(sql)) == 1) {
                JOptionPane.showMessageDialog(this, "data Saved !");
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed !");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JDialog_Reproses_Sub_pencabut.class.getName()).log(Level.SEVERE, null, ex);
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
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txt_id_pegawai = new javax.swing.JTextField();
        txt_nama_pegawai = new javax.swing.JTextField();
        txt_jmlh_keping = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        Date_cabut = new com.toedter.calendar.JDateChooser();
        button_pick_pencabut = new javax.swing.JButton();
        button_clear_pencabut = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        txt_bagian = new javax.swing.JTextField();
        button_save = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Input Pencabut Reproses", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 12))); // NOI18N

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("ID Pegawai :");

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel4.setText("Nama Pegawai :");

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel5.setText("Tanggal :");

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel7.setText("Jumlah Cabutan :");

        txt_id_pegawai.setEditable(false);
        txt_id_pegawai.setBackground(new java.awt.Color(255, 255, 255));
        txt_id_pegawai.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        txt_nama_pegawai.setEditable(false);
        txt_nama_pegawai.setBackground(new java.awt.Color(255, 255, 255));
        txt_nama_pegawai.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        txt_jmlh_keping.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel8.setText("Keping (1 keping = 6gram)");

        Date_cabut.setBackground(new java.awt.Color(255, 255, 255));
        Date_cabut.setDate(new Date());
        Date_cabut.setDateFormatString("dd MMMM yyyy");
        Date_cabut.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        button_pick_pencabut.setBackground(new java.awt.Color(255, 255, 255));
        button_pick_pencabut.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_pick_pencabut.setText("...");
        button_pick_pencabut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_pick_pencabutActionPerformed(evt);
            }
        });

        button_clear_pencabut.setBackground(new java.awt.Color(255, 255, 255));
        button_clear_pencabut.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_clear_pencabut.setText("Cancel");
        button_clear_pencabut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_clear_pencabutActionPerformed(evt);
            }
        });

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel14.setText("Bagian :");

        txt_bagian.setEditable(false);
        txt_bagian.setBackground(new java.awt.Color(255, 255, 255));
        txt_bagian.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

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
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txt_nama_pegawai, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(txt_id_pegawai)
                        .addGap(0, 0, 0)
                        .addComponent(button_pick_pencabut, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(Date_cabut, javax.swing.GroupLayout.DEFAULT_SIZE, 266, Short.MAX_VALUE)
                    .addComponent(txt_bagian, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(txt_jmlh_keping, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel8))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(button_clear_pencabut)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_save)))
                        .addGap(0, 102, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_id_pegawai, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_pick_pencabut, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_nama_pegawai, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_bagian, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_cabut, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_jmlh_keping, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_clear_pencabut, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_save, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
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

    private void button_pick_pencabutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_pick_pencabutActionPerformed
        // TODO add your handling code here:
        JDialog_Browse_KaryawanSub dialog = new JDialog_Browse_KaryawanSub(new javax.swing.JFrame(), true, null);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);

        int x = JDialog_Browse_KaryawanSub.table_list_karyawan.getSelectedRow();
        if (x != -1) {
            txt_id_pegawai.setText(JDialog_Browse_KaryawanSub.table_list_karyawan.getValueAt(x, 0).toString());
            txt_nama_pegawai.setText(JDialog_Browse_KaryawanSub.table_list_karyawan.getValueAt(x, 1).toString());
            txt_bagian.setText(JDialog_Browse_KaryawanSub.table_list_karyawan.getValueAt(x, 2).toString());
        }
    }//GEN-LAST:event_button_pick_pencabutActionPerformed

    private void button_clear_pencabutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_clear_pencabutActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_button_clear_pencabutActionPerformed

    private void button_saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_saveActionPerformed
        // TODO add your handling code here:
        if (no == null) {
            AddPencabut();
        } else {
            EditPencabut();
        }
    }//GEN-LAST:event_button_saveActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser Date_cabut;
    private javax.swing.JButton button_clear_pencabut;
    private javax.swing.JButton button_pick_pencabut;
    public javax.swing.JButton button_save;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField txt_bagian;
    private javax.swing.JTextField txt_id_pegawai;
    private javax.swing.JTextField txt_jmlh_keping;
    private javax.swing.JTextField txt_nama_pegawai;
    // End of variables declaration//GEN-END:variables
}
