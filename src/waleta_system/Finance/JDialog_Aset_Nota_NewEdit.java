package waleta_system.Finance;

import java.awt.event.KeyEvent;
import waleta_system.Class.Utility;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class JDialog_Aset_Nota_NewEdit extends javax.swing.JDialog {

    private String sql = null;
    private ResultSet rs;
    private String operand = null;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DecimalFormat decimalFormat = new DecimalFormat();

    public JDialog_Aset_Nota_NewEdit(java.awt.Frame parent, boolean modal, String kode) {
        super(parent, modal);
        initComponents();
        this.setResizable(false);
        try {
            ComboBox_supplier.removeAllItems();
            sql = "SELECT `nama_supplier`, `keterangan` FROM `tb_aset_supplier` WHERE 1";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_supplier.addItem(rs.getString("nama_supplier"));
            }
            
            if (kode == null) {
                this.operand = "tambah";
            } else {
                this.operand = "edit";
                txt_kode_nota.setText(kode);

                sql = "SELECT `kode_nota`, `no_voucher_keuangan`, `tanggal_debit`, `tanggal_nota`, `ongkir`, `nilai_nota`, `supplier_nota`, `keterangan_nota` "
                        + "FROM `tb_aset_nota_pembelian` WHERE `kode_nota` = '" + kode + "'";
                rs = Utility.db.getStatement().executeQuery(sql);
                if (rs.next()) {
                    txt_kode_nota.setText(rs.getString("kode_nota"));
                    txt_no_voucher_keuangan.setText(rs.getString("no_voucher_keuangan"));
                    Date_debit.setDate(rs.getDate("tanggal_debit"));
                    Date_nota.setDate(rs.getDate("tanggal_nota"));
                    txt_ongkir.setText(rs.getString("ongkir"));
                    txt_nilai_nota.setText(rs.getString("nilai_nota"));
                    ComboBox_supplier.setSelectedItem(rs.getString("supplier_nota"));
                    txt_keterangan.setText(rs.getString("keterangan_nota"));
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JDialog_Aset_Nota_NewEdit.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String newKodeNota() {
        try {
            String kode_baru = "";
            sql = "SELECT MAX(RIGHT(`kode_nota`, 5)) AS 'kode_terakhir' FROM `tb_aset_nota_pembelian` "
                    + "WHERE YEAR(`tanggal_nota`) = " + new SimpleDateFormat("yyyy").format(Date_nota.getDate());
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                kode_baru = new SimpleDateFormat("yy").format(Date_nota.getDate()) + "-" + String.format("%05d", rs.getInt("kode_terakhir") + 1);
            }
            return kode_baru;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JDialog_Aset_Nota_NewEdit.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
    }

    public void newNota() {
        try {
            boolean check = true;
            int ongkir = Integer.valueOf(txt_ongkir.getText());
            int nilai_nota = Integer.valueOf(txt_nilai_nota.getText());

            if (Date_nota.getDate() == null) {
                JOptionPane.showMessageDialog(this, "Maaf tanggal nota salah!");
                check = true;
            }

            String tanggal_debit = "NULL, ";
            if (Date_debit.getDate() != null) {
                tanggal_debit = "'" + dateFormat.format(Date_debit.getDate()) + "', ";
            }
            String no_voucher_keuangan = "'" + txt_no_voucher_keuangan.getText() + "', ";
            if (txt_no_voucher_keuangan.getText() == null || txt_no_voucher_keuangan.getText().equals("")) {
                no_voucher_keuangan = "NULL, ";
            }

            if (check) {
                String newKodeNota = newKodeNota();
                sql = "INSERT INTO `tb_aset_nota_pembelian`(`kode_nota`, `no_voucher_keuangan`, `tanggal_debit`, `tanggal_nota`, `ongkir`, `nilai_nota`, `supplier_nota`, `keterangan_nota`) "
                        + "VALUES ("
                        + "'" + newKodeNota + "',"
                        + no_voucher_keuangan
                        + tanggal_debit
                        + "'" + dateFormat.format(Date_nota.getDate()) + "',"
                        + "'" + ongkir + "',"
                        + "'" + nilai_nota + "',"
                        + "'" + ComboBox_supplier.getSelectedItem().toString() + "',"
                        + "'" + txt_keterangan.getText() + "'"
                        + ")";
                Utility.db.getConnection().createStatement();
                if ((Utility.db.getStatement().executeUpdate(sql)) == 1) {
                    this.dispose();
                    JOptionPane.showMessageDialog(this, "Data Berhasil simpan");
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JDialog_Aset_Nota_NewEdit.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void editNota() {
        try {
            boolean check = true;
            int ongkir = Integer.valueOf(txt_ongkir.getText());
            int nilai_nota = Integer.valueOf(txt_nilai_nota.getText());

            if (Date_nota.getDate() == null) {
                JOptionPane.showMessageDialog(this, "Maaf tanggal nota salah!");
                check = true;
            }

            String tanggal_debit = "NULL";
            if (Date_debit.getDate() != null) {
                tanggal_debit = "'" + dateFormat.format(Date_debit.getDate()) + "'";
            }
            String no_voucher_keuangan = "'" + txt_no_voucher_keuangan.getText() + "'";
            if (txt_no_voucher_keuangan.getText() == null || txt_no_voucher_keuangan.getText().equals("")) {
                no_voucher_keuangan = "NULL";
            }

            if (check) {
                sql = "UPDATE `tb_aset_nota_pembelian` SET "
                        + "`no_voucher_keuangan`=" + no_voucher_keuangan + ","
                        + "`tanggal_debit`=" + tanggal_debit + ","
                        + "`tanggal_nota`='" + dateFormat.format(Date_nota.getDate()) + "',"
                        + "`ongkir`='" + txt_ongkir.getText() + "',"
                        + "`nilai_nota`='" + txt_nilai_nota.getText() + "',"
                        + "`supplier_nota`='" + ComboBox_supplier.getSelectedItem().toString() + "',"
                        + "`keterangan_nota`='" + txt_keterangan.getText() + "' "
                        + "WHERE `kode_nota`='" + txt_kode_nota.getText() + "'";
                Utility.db.getConnection().createStatement();
                if ((Utility.db.getStatement().executeUpdate(sql)) == 1) {
                    this.dispose();
                    JOptionPane.showMessageDialog(this, "Data Berhasil simpan");
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JDialog_Aset_Nota_NewEdit.class.getName()).log(Level.SEVERE, null, ex);
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
        label_title = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txt_no_voucher_keuangan = new javax.swing.JTextField();
        button_save = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txt_ongkir = new javax.swing.JTextField();
        txt_nilai_nota = new javax.swing.JTextField();
        txt_kode_nota = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        txt_keterangan = new javax.swing.JTextField();
        Date_debit = new com.toedter.calendar.JDateChooser();
        Date_nota = new com.toedter.calendar.JDateChooser();
        ComboBox_supplier = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        label_title.setBackground(new java.awt.Color(255, 255, 255));
        label_title.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_title.setText("Nota Pembelian Barang");

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setText("Kode Nota :");

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel4.setText("Tanggal Nota :");

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel5.setText("No Voucher Keuangan :");

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel6.setText("Tanggal Debit :");

        txt_no_voucher_keuangan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        button_save.setBackground(new java.awt.Color(255, 255, 255));
        button_save.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_save.setText("Save");
        button_save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_saveActionPerformed(evt);
            }
        });

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel7.setText("Ongkir (Rp.) :");

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel8.setText("Nilai Nota (Rp.) :");

        txt_ongkir.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_ongkir.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_ongkirKeyTyped(evt);
            }
        });

        txt_nilai_nota.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_nilai_nota.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_nilai_notaKeyTyped(evt);
            }
        });

        txt_kode_nota.setEditable(false);
        txt_kode_nota.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel10.setText("Supplier :");

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel11.setText("Keterangan :");

        txt_keterangan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        Date_debit.setBackground(new java.awt.Color(255, 255, 255));
        Date_debit.setDateFormatString("dd MMM yyyy");

        Date_nota.setBackground(new java.awt.Color(255, 255, 255));
        Date_nota.setDateFormatString("dd MMM yyyy");

        ComboBox_supplier.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(label_title)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(button_save))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_kode_nota, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                            .addComponent(txt_nilai_nota, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                            .addComponent(Date_nota, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(Date_debit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txt_keterangan, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                            .addComponent(txt_no_voucher_keuangan, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                            .addComponent(txt_ongkir, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                            .addComponent(ComboBox_supplier, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(label_title, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_kode_nota, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_no_voucher_keuangan, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(Date_debit, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_nota, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_ongkir, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_nilai_nota, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_supplier, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_keterangan, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(button_save, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
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

    private void button_saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_saveActionPerformed
        // TODO add your handling code here:
        if (operand.equals("tambah")) {
            newNota();
        } else if (operand.equals("edit")) {
            editNota();
        }
    }//GEN-LAST:event_button_saveActionPerformed

    private void txt_nilai_notaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_nilai_notaKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_nilai_notaKeyTyped

    private void txt_ongkirKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_ongkirKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_ongkirKeyTyped


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_supplier;
    private com.toedter.calendar.JDateChooser Date_debit;
    private com.toedter.calendar.JDateChooser Date_nota;
    private javax.swing.JButton button_save;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel label_title;
    private javax.swing.JTextField txt_keterangan;
    private javax.swing.JTextField txt_kode_nota;
    private javax.swing.JTextField txt_nilai_nota;
    private javax.swing.JTextField txt_no_voucher_keuangan;
    private javax.swing.JTextField txt_ongkir;
    // End of variables declaration//GEN-END:variables
}
