package waleta_system.Finance;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;

public class JDialog_Aset_LabelBarang_New extends javax.swing.JDialog {

    String sql = null;
    ResultSet rs;
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();

    public JDialog_Aset_LabelBarang_New(java.awt.Frame parent, boolean modal) {
//    public Browse_Karyawan(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        table_nota_detail.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "Enter");
        table_nota_detail.getActionMap().put("Enter", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                //do something on JTable enter pressed
                save();
            }
        });
        this.setResizable(false);
        refreshTable();
    }

    public void refreshTable() {
        try {
            DefaultTableModel model = (DefaultTableModel) table_nota_detail.getModel();
            model.setRowCount(0);
            sql = "SELECT `tb_aset_nota_detail`.`kode_nota_detail`, `kode_nota`, `tb_aset_nota_detail`.`kode_aset`, `tgl_datang`, `jumlah`, `satuan`, `harga_satuan`, `diskon`, `pajak`, `departemen`, `keperluan`, "
                    + "`nama_aset`, `merk_aset`, `spesifikasi_aset` "
                    + "FROM `tb_aset_nota_detail` "
                    + "LEFT JOIN `tb_aset_master` ON `tb_aset_nota_detail`.`kode_aset` = `tb_aset_master`.`kode_aset` "
                    + "LEFT JOIN `tb_aset_unit` ON `tb_aset_nota_detail`.`kode_nota_detail` = `tb_aset_unit`.`kode_nota_detail` "
                    + "WHERE "
                    + "`tb_aset_unit`.`kode_nota_detail` IS NULL "
                    + "AND `kode_nota` LIKE '%" + txt_search_kodeNota.getText() + "%' "
                    + "AND `nama_aset` LIKE '%" + txt_search_namaAset.getText() + "%'"
                    + "GROUP BY `tb_aset_nota_detail`.`kode_nota_detail`";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[20];
            while (rs.next()) {
                row[0] = rs.getString("kode_nota");
                row[1] = rs.getString("kode_nota_detail");
                row[2] = rs.getString("kode_aset");
                row[3] = rs.getString("nama_aset");
                row[4] = rs.getString("merk_aset");
                row[5] = rs.getString("spesifikasi_aset");
                row[6] = rs.getDate("tgl_datang");
                row[7] = rs.getInt("jumlah");
                row[8] = rs.getString("satuan");
                row[9] = rs.getString("departemen");
                row[10] = rs.getString("keperluan");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(table_nota_detail);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JDialog_Aset_LabelBarang_New.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String newKodeUnit(String kode_aset) throws SQLException {
        String kode_baru = "";
        sql = "SELECT MAX(RIGHT(`kode_unit`, 5)) AS 'kode_terakhir' FROM `tb_aset_unit` "
                + "WHERE `kode_unit` LIKE '%-" + kode_aset + "-%' ";
        rs = Utility.db.getStatement().executeQuery(sql);
        if (rs.next()) {
            kode_baru = new SimpleDateFormat("yy").format(new Date()) + "-" + kode_aset + "-" + String.format("%05d", rs.getInt("kode_terakhir") + 1);
        }
        return kode_baru;
    }

    public void save() {
        try {
            Utility.db.getConnection().setAutoCommit(false);
            boolean check = true;
            int row = table_nota_detail.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan Pilih data pada tabel!");
                check = false;
            }

            if (check) {
                int jumlah = (int) table_nota_detail.getValueAt(row, 7);
                String nama_aset = table_nota_detail.getValueAt(row, 3).toString();
                String merk_aset = table_nota_detail.getValueAt(row, 4).toString();
                int dialogResult = JOptionPane.showConfirmDialog(this, "Membuat label untuk " + nama_aset + " " + merk_aset + " sebanyak " + jumlah + " Unit?", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    for (int i = 0; i < jumlah; i++) {
                        sql = "INSERT INTO `tb_aset_unit`(`kode_unit`, `kode_nota_detail`) "
                                + "VALUES ("
                                + "'" + newKodeUnit(table_nota_detail.getValueAt(row, 2).toString()) + "',"
                                + "'" + table_nota_detail.getValueAt(row, 1).toString() + "'"
                                + ")";
                        Utility.db.getConnection().createStatement();
                        Utility.db.getStatement().executeUpdate(sql);
                    }
                    Utility.db.getConnection().commit();
                    this.dispose();
                    JOptionPane.showMessageDialog(this, "Data Berhasil simpan");

                }
            }
        } catch (Exception ex) {
            try {
                Utility.db.getConnection().rollback();
            } catch (SQLException ex1) {
                Logger.getLogger(JDialog_Aset_LabelBarang_New.class.getName()).log(Level.SEVERE, null, ex1);
            }
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JDialog_Aset_Nota_NewEdit.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                Utility.db.getConnection().setAutoCommit(true);
            } catch (SQLException ex) {
                Logger.getLogger(JDialog_Aset_LabelBarang_New.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        button_search = new javax.swing.JButton();
        txt_search_kodeNota = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txt_search_namaAset = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        table_nota_detail = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel1.setText("Kode Nota :");

        button_search.setBackground(new java.awt.Color(255, 255, 255));
        button_search.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search.setText("Search");
        button_search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_searchActionPerformed(evt);
            }
        });

        txt_search_kodeNota.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_search_kodeNota.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_kodeNotaKeyPressed(evt);
            }
        });

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setText("Nama Aset :");

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 2, 12)); // NOI18N
        jLabel3.setText("Klik 2x pada tabel untuk memilih");

        txt_search_namaAset.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_search_namaAset.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_namaAsetKeyPressed(evt);
            }
        });

        table_nota_detail.setAutoCreateRowSorter(true);
        table_nota_detail.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_nota_detail.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode Nota", "Kode", "Kode Aset", "Nama Aset", "Merk", "Spesifikasi", "Tgl Datang", "Jumlah", "Satuan", "Departemen", "Keperluan"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Float.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table_nota_detail.getTableHeader().setReorderingAllowed(false);
        table_nota_detail.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                table_nota_detailMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(table_nota_detail);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_kodeNota, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_namaAset, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_search)))
                        .addGap(0, 464, Short.MAX_VALUE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 980, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(button_search, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_kodeNota, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_namaAset, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 420, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
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
        refreshTable();
    }//GEN-LAST:event_button_searchActionPerformed

    private void txt_search_kodeNotaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_kodeNotaKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_search_kodeNotaKeyPressed

    private void txt_search_namaAsetKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_namaAsetKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_search_namaAsetKeyPressed

    private void table_nota_detailMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_table_nota_detailMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            save();
        }
    }//GEN-LAST:event_table_nota_detailMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton button_search;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    public static javax.swing.JTable table_nota_detail;
    private javax.swing.JTextField txt_search_kodeNota;
    private javax.swing.JTextField txt_search_namaAset;
    // End of variables declaration//GEN-END:variables
}
