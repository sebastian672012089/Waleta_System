package waleta_system.HRD;

import waleta_system.Class.Utility;
import java.awt.HeadlessException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class JDialog_karyawan_keluar_masuk extends javax.swing.JDialog {

    String sql = null;
    ResultSet rs;
    PreparedStatement pst;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    String id = null;
    Date tanggal_masuk = null;

    public JDialog_karyawan_keluar_masuk(java.awt.Frame parent, boolean modal, String id, String status) {
        super(parent, modal);
        initComponents();
        this.id = id;
        label_tanggal.setText(status);

        try {
            sql = "SELECT `id_pegawai`, `nama_pegawai`, `tanggal_masuk`, `tanggal_keluar`, `kategori_keluar`, `keterangan` "
                    + "FROM `tb_karyawan` "
                    + "WHERE `id_pegawai` = '" + id + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                label_id.setText(rs.getString("id_pegawai"));
                label_nama.setText(rs.getString("nama_pegawai"));
                tanggal_masuk = rs.getDate("tanggal_masuk");
                if (status.equals("Tanggal Keluar :")) {
                    Date_Tanggal_Keluar_atau_Masuk.setDate(rs.getDate("tanggal_keluar"));
                    ComboBox_kategori_keluar.setSelectedItem(rs.getString("kategori_keluar"));
                    txt_keterangan_keluar.setText(rs.getString("keterangan"));
                } else {
                    Date_Tanggal_Keluar_atau_Masuk.setDate(rs.getDate("tanggal_masuk"));
                    label_kategori_keluar.setVisible(false);
                    ComboBox_kategori_keluar.setVisible(false);
                    label_keterangan.setVisible(false);
                    txt_keterangan_keluar.setVisible(false);
                }
            }
        } catch (SQLException | HeadlessException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JDialog_karyawan_keluar_masuk.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void keluar() {
        try {
            if (tanggal_masuk.after(Date_Tanggal_Keluar_atau_Masuk.getDate())) {
                JOptionPane.showMessageDialog(this, "Maaf tanggal keluar tidak bisa lebih muda dari tanggal masuk!");
            } else {
                String sql_update = "UPDATE `tb_karyawan` SET "
                        + "`tanggal_keluar`='" + dateFormat.format(Date_Tanggal_Keluar_atau_Masuk.getDate()) + "', "
                        + "`kategori_keluar`='" + ComboBox_kategori_keluar.getSelectedItem().toString() + "', "
                        + "`keterangan` = '" + txt_keterangan_keluar.getText() + "', "
                        + "`status`='OUT' "
                        + "WHERE `id_pegawai` = '" + id + "'";
                Utility.db.getConnection().createStatement();
                if ((Utility.db.getStatement().executeUpdate(sql_update)) == 1) {
                    JOptionPane.showMessageDialog(this, "Data Saved, Harap menghapus data user pada mesin absen fingerspot!");
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "failed");
                }
            }
        } catch (SQLException | HeadlessException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JDialog_karyawan_keluar_masuk.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void masuk() {
        try {
            sql = "UPDATE `tb_karyawan` SET "
                    + "`tanggal_masuk`='" + dateFormat.format(Date_Tanggal_Keluar_atau_Masuk.getDate()) + "', "
                    + "`tanggal_keluar`=NULL, "
                    + "`kategori_keluar`=NULL, "
                    + "`status`='IN' "
                    + "WHERE `id_pegawai` = '" + id + "'";
            Utility.db.getConnection().createStatement();
            if ((Utility.db.getStatement().executeUpdate(sql)) == 1) {
                JOptionPane.showMessageDialog(this, "success");
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "failed");
            }
        } catch (SQLException | HeadlessException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JDialog_karyawan_keluar_masuk.class.getName()).log(Level.SEVERE, null, ex);
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
        label_tanggal = new javax.swing.JLabel();
        Date_Tanggal_Keluar_atau_Masuk = new com.toedter.calendar.JDateChooser();
        button_ok = new javax.swing.JButton();
        label_keterangan = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txt_keterangan_keluar = new javax.swing.JTextArea();
        label_kategori_keluar = new javax.swing.JLabel();
        ComboBox_kategori_keluar = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        label_id = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        label_nama = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        label_tanggal.setBackground(new java.awt.Color(255, 255, 255));
        label_tanggal.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_tanggal.setText("Tanggal  Keluar :");

        Date_Tanggal_Keluar_atau_Masuk.setBackground(new java.awt.Color(255, 255, 255));
        Date_Tanggal_Keluar_atau_Masuk.setDate(new Date());
        Date_Tanggal_Keluar_atau_Masuk.setDateFormatString("dd MMM yyyy");
        Date_Tanggal_Keluar_atau_Masuk.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Date_Tanggal_Keluar_atau_Masuk.setMaxSelectableDate(new Date());

        button_ok.setBackground(new java.awt.Color(255, 255, 255));
        button_ok.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_ok.setText("SAVE");
        button_ok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_okActionPerformed(evt);
            }
        });

        label_keterangan.setBackground(new java.awt.Color(255, 255, 255));
        label_keterangan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_keterangan.setText("Keterangan Keluar :");

        txt_keterangan_keluar.setColumns(20);
        txt_keterangan_keluar.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_keterangan_keluar.setLineWrap(true);
        txt_keterangan_keluar.setRows(5);
        jScrollPane1.setViewportView(txt_keterangan_keluar);

        label_kategori_keluar.setBackground(new java.awt.Color(255, 255, 255));
        label_kategori_keluar.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_kategori_keluar.setText("Kategori Keluar :");

        ComboBox_kategori_keluar.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        ComboBox_kategori_keluar.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tanpa alasan", "Alasan kesehatan", "Pindah kerja", "Urusan keluarga", "Alasan pribadi", "Blacklist", "Lanjut studi", "Beban kerja", "Meninggal dunia", "SP3" }));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel1.setText("ID Pegawai :");

        label_id.setBackground(new java.awt.Color(255, 255, 255));
        label_id.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_id.setText("-");

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setText("Nama :");

        label_nama.setBackground(new java.awt.Color(255, 255, 255));
        label_nama.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_nama.setText("-");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(button_ok)
                .addGap(10, 10, 10))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label_keterangan, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_tanggal, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_kategori_keluar, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 254, Short.MAX_VALUE)
                    .addComponent(Date_Tanggal_Keluar_atau_Masuk, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ComboBox_kategori_keluar, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(label_id, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(label_nama, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_id, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_nama, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Date_Tanggal_Keluar_atau_Masuk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_tanggal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_kategori_keluar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_kategori_keluar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label_keterangan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(button_ok, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
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
        if (label_tanggal.getText().equals("Tanggal Masuk :")) {
            masuk();
        } else if (label_tanggal.getText().equals("Tanggal Keluar :")) {
            keluar();
        }
    }//GEN-LAST:event_button_okActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_kategori_keluar;
    private com.toedter.calendar.JDateChooser Date_Tanggal_Keluar_atau_Masuk;
    private javax.swing.JButton button_ok;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel label_id;
    private javax.swing.JLabel label_kategori_keluar;
    private javax.swing.JLabel label_keterangan;
    private javax.swing.JLabel label_nama;
    private javax.swing.JLabel label_tanggal;
    private javax.swing.JTextArea txt_keterangan_keluar;
    // End of variables declaration//GEN-END:variables
}
