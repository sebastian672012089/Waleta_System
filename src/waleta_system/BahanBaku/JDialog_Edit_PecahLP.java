package waleta_system.BahanBaku;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;

public class JDialog_Edit_PecahLP extends javax.swing.JDialog {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    float total_kpg = 0, total_kpg_upah = 0, total_gram = 0;

    public JDialog_Edit_PecahLP(java.awt.Frame parent, boolean modal, DefaultTableModel model, int SelectedRow) {
        super(parent, modal);
        initComponents();

        try {
            ComboBox_buluUpah.removeAllItems();
            sql = "SELECT `bulu_upah` FROM `tb_tarif_cabut` WHERE `status` = 'AKTIF'";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_buluUpah.addItem(rs.getString("bulu_upah"));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JDialog_Edit_PecahLP.class.getName()).log(Level.SEVERE, null, ex);
        }

        DefaultTableModel model_asal = (DefaultTableModel) Table_pecah_asal.getModel();
        model_asal.setRowCount(0);
        Object[] row = new Object[25];
        for (int i = 0; i < model.getColumnCount(); i++) {
            row[i] = model.getValueAt(SelectedRow, i);
        }
        model_asal.addRow(row);
        ColumnsAutoSizer.sizeColumnsToFit(Table_pecah_asal);
    }

    private void count() {
        try {
            total_kpg = 0;
            total_kpg_upah = 0;
            total_gram = 0;
            for (int i = 0; i < Table_pecah_hasil.getRowCount(); i++) {
                total_kpg += (float) Table_pecah_hasil.getValueAt(i, 4);
                total_kpg_upah += (float) Table_pecah_hasil.getValueAt(i, 5);
                total_gram += (float) Table_pecah_hasil.getValueAt(i, 6);
            }
            label_total_kpg.setText(decimalFormat.format(total_kpg));
            label_total_kpg_upah.setText(decimalFormat.format(total_kpg_upah));
            label_total_gram.setText(decimalFormat.format(total_gram));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ada angka yang salah, silahkan cek kembali inputan di tabel");
        }
    }

    private void insert() {
        try {
            Utility.db.getConnection().setAutoCommit(false);
            String DEL_Query = "DELETE FROM `tb_bahan_baku_pecah_kartu` WHERE `kode_pecah_kartu` = '" + Table_pecah_asal.getValueAt(0, 0).toString() + "'";
            Utility.db.getStatement().executeUpdate(DEL_Query);
            for (int i = 0; i < Table_pecah_hasil.getRowCount(); i++) {
                String INSERT_Query = "INSERT INTO `tb_bahan_baku_pecah_kartu`(`no_grading`, `jumlah_keping`, `keping_upah`, `berat_basah`, `jenis_bulu_lp`, `memo_lp`, `kaki_besar_lp`, `kaki_kecil_lp`, `hilang_kaki_lp`, `ada_susur_lp`, `ada_susur_besar_lp`, `tanpa_susur_lp`, `utuh_lp`, `hilang_ujung_lp`, `pecah_1_lp`, `pecah_2`, `jumlah_sobek`, `sobek_lepas`, `jumlah_gumpil`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement statement = Utility.db.getConnection().prepareStatement(INSERT_Query);
                // Set the parameters of the prepared statement.
                statement.setString(1, Table_pecah_hasil.getValueAt(i, 1).toString());
                statement.setString(2, Table_pecah_hasil.getValueAt(i, 4) == null ? "0" : Table_pecah_hasil.getValueAt(i, 4).toString());
                statement.setString(3, Table_pecah_hasil.getValueAt(i, 5) == null ? "0" : Table_pecah_hasil.getValueAt(i, 5).toString());
                statement.setString(4, Table_pecah_hasil.getValueAt(i, 6) == null ? "0" : Table_pecah_hasil.getValueAt(i, 6).toString());
                statement.setString(5, Table_pecah_hasil.getValueAt(i, 7).toString());
                statement.setString(6, (Table_pecah_hasil.getValueAt(i, 8) == null ? "" : Table_pecah_hasil.getValueAt(i, 8).toString()));
                statement.setString(7, Table_pecah_hasil.getValueAt(i, 9) == null ? "0" : Table_pecah_hasil.getValueAt(i, 9).toString());
                statement.setString(8, Table_pecah_hasil.getValueAt(i, 10) == null ? "0" : Table_pecah_hasil.getValueAt(i, 10).toString());
                statement.setString(9, Table_pecah_hasil.getValueAt(i, 11) == null ? "0" : Table_pecah_hasil.getValueAt(i, 11).toString());
                statement.setString(10, Table_pecah_hasil.getValueAt(i, 12) == null ? "0" : Table_pecah_hasil.getValueAt(i, 12).toString());
                statement.setString(11, Table_pecah_hasil.getValueAt(i, 13) == null ? "0" : Table_pecah_hasil.getValueAt(i, 13).toString());
                statement.setString(12, Table_pecah_hasil.getValueAt(i, 14) == null ? "0" : Table_pecah_hasil.getValueAt(i, 14).toString());
                statement.setString(13, Table_pecah_hasil.getValueAt(i, 15) == null ? "0" : Table_pecah_hasil.getValueAt(i, 15).toString());
                statement.setString(14, Table_pecah_hasil.getValueAt(i, 16) == null ? "0" : Table_pecah_hasil.getValueAt(i, 16).toString());
                statement.setString(15, Table_pecah_hasil.getValueAt(i, 17) == null ? "0" : Table_pecah_hasil.getValueAt(i, 17).toString());
                statement.setString(16, Table_pecah_hasil.getValueAt(i, 18) == null ? "0" : Table_pecah_hasil.getValueAt(i, 18).toString());
                statement.setString(17, Table_pecah_hasil.getValueAt(i, 19) == null ? "0" : Table_pecah_hasil.getValueAt(i, 19).toString());
                statement.setString(18, Table_pecah_hasil.getValueAt(i, 20) == null ? "0" : Table_pecah_hasil.getValueAt(i, 20).toString());
                statement.setString(19, Table_pecah_hasil.getValueAt(i, 21) == null ? "0" : Table_pecah_hasil.getValueAt(i, 21).toString());
                System.out.println(INSERT_Query);
                statement.executeUpdate();
//                Utility.db.getStatement().executeUpdate(INSERT_Query);
            }

            Utility.db.getConnection().commit();
            this.dispose();
            JOptionPane.showMessageDialog(this, "Data berhasil ditambahkan!");
        } catch (Exception e) {
            try {
                Utility.db.getConnection().rollback();
            } catch (SQLException ex) {
                Logger.getLogger(JDialog_Edit_PecahLP.class.getName()).log(Level.SEVERE, null, ex);
            }
            JOptionPane.showMessageDialog(this, "data not inserted" + e);
            Logger.getLogger(JDialog_Edit_PecahLP.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            try {
                Utility.db.getConnection().setAutoCommit(true);
            } catch (SQLException ex) {
                Logger.getLogger(JDialog_Edit_PecahLP.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel_operation_bahan_baku = new javax.swing.JPanel();
        button_SAVE = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        button_tambah = new javax.swing.JButton();
        button_hapus = new javax.swing.JButton();
        jLabel23 = new javax.swing.JLabel();
        ComboBox_buluUpah = new javax.swing.JComboBox<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        Table_pecah_hasil = new javax.swing.JTable();
        jLabel26 = new javax.swing.JLabel();
        label_total_gram = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        label_total_kpg_upah = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        label_total_kpg = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        Table_pecah_asal = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel_operation_bahan_baku.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_operation_bahan_baku.setName("aah"); // NOI18N

        button_SAVE.setBackground(new java.awt.Color(255, 255, 255));
        button_SAVE.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        button_SAVE.setText("SAVE");
        button_SAVE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_SAVEActionPerformed(evt);
            }
        });

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel1.setText("EDIT PECAH LP");

        jPanel1.setBackground(new java.awt.Color(204, 204, 255));

        jLabel22.setBackground(new java.awt.Color(255, 255, 255));
        jLabel22.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel22.setText("Data Pecah yang baru");

        button_tambah.setBackground(new java.awt.Color(255, 255, 255));
        button_tambah.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_tambah.setText("+ Baris");
        button_tambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_tambahActionPerformed(evt);
            }
        });

        button_hapus.setBackground(new java.awt.Color(255, 255, 255));
        button_hapus.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_hapus.setText("Hapus");
        button_hapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_hapusActionPerformed(evt);
            }
        });

        jLabel23.setBackground(new java.awt.Color(255, 255, 255));
        jLabel23.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel23.setText("Bulu Upah :");

        ComboBox_buluUpah.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        Table_pecah_hasil.setAutoCreateRowSorter(true);
        Table_pecah_hasil.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Table_pecah_hasil.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode", "No Grading", "No Kartu", "Grade", "Kpg", "Kpg Upah", "Gram", "Jenis Bulu", "Memo LP", "kk BESAR", "kk KECIL", "Tanpa kk", "Susur BESAR", "Susur KECIL", "Tanpa Susur", "Utuh", "Hlg Ujung", "Pch 1", "Pch 2", "Sobek", "Sobek Lepas", "Gumpil"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, true, true, true, false, true, true, true, true, true, true, true, true, true, true, true, true, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Table_pecah_hasil.setRowHeight(20);
        Table_pecah_hasil.getTableHeader().setReorderingAllowed(false);
        Table_pecah_hasil.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Table_pecah_hasilMouseClicked(evt);
            }
        });
        Table_pecah_hasil.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Table_pecah_hasilKeyPressed(evt);
            }
        });
        jScrollPane2.setViewportView(Table_pecah_hasil);

        jLabel26.setBackground(new java.awt.Color(255, 255, 255));
        jLabel26.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel26.setText("Total Gram :");

        label_total_gram.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_gram.setText("000");

        jLabel28.setBackground(new java.awt.Color(255, 255, 255));
        jLabel28.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel28.setText("Total Kpg Upah :");

        label_total_kpg_upah.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kpg_upah.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_kpg_upah.setText("000");

        jLabel30.setBackground(new java.awt.Color(255, 255, 255));
        jLabel30.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel30.setText("Total Kpg :");

        label_total_kpg.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kpg.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_kpg.setText("000");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel22)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel23)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_buluUpah, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_tambah)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_hapus)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 632, Short.MAX_VALUE)
                        .addComponent(jLabel30)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_kpg)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel28)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_kpg_upah)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel26)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gram)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel22)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(button_tambah, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_hapus, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_buluUpah, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_kpg_upah, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_gram, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_kpg, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE)
                .addContainerGap())
        );

        Table_pecah_asal.setAutoCreateRowSorter(true);
        Table_pecah_asal.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Table_pecah_asal.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Kode", "No Grading", "No Kartu", "Grade", "Kpg", "Kpg Upah", "Gram", "Jenis Bulu", "Memo LP", "kk BESAR", "kk KECIL", "Tanpa kk", "Susur BESAR", "Susur KECIL", "Tanpa Susur", "Utuh", "Hlg Ujung", "Pch 1", "Pch 2", "Sobek", "Sobek Lepas", "Gumpil"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Table_pecah_asal.setRowHeight(20);
        Table_pecah_asal.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(Table_pecah_asal);

        javax.swing.GroupLayout jPanel_operation_bahan_bakuLayout = new javax.swing.GroupLayout(jPanel_operation_bahan_baku);
        jPanel_operation_bahan_baku.setLayout(jPanel_operation_bahan_bakuLayout);
        jPanel_operation_bahan_bakuLayout.setHorizontalGroup(
            jPanel_operation_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_operation_bahan_bakuLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_operation_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3)
                    .addGroup(jPanel_operation_bahan_bakuLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(button_SAVE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel_operation_bahan_bakuLayout.setVerticalGroup(
            jPanel_operation_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_operation_bahan_bakuLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_operation_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(button_SAVE, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel_operation_bahan_baku, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel_operation_bahan_baku, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void button_SAVEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_SAVEActionPerformed
        // TODO add your handling code here:
        count();
        Boolean Check = true;
        float kpg_asal = (float) Table_pecah_asal.getValueAt(0, 4);
        float kpg_upah_asal = (float) Table_pecah_asal.getValueAt(0, 5);
        float gram_asal = (float) Table_pecah_asal.getValueAt(0, 6);
        if (Table_pecah_hasil.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Data pecah yang baru belum dimasukkan !");
            Check = false;
        } else if (Table_pecah_hasil.getRowCount() < 2) {
            JOptionPane.showMessageDialog(this, "Jumlah pecah baru minimal 2 !");
            Check = false;
        } else if (kpg_asal != total_kpg) {
            JOptionPane.showMessageDialog(this, "Jumlah keping asal dan hasil harus sama !");
            Check = false;
        } else if (kpg_upah_asal != total_kpg_upah) {
            JOptionPane.showMessageDialog(this, "Jumlah keping upah asal dan hasil harus sama !");
            Check = false;
        } else if (gram_asal != total_gram) {
            JOptionPane.showMessageDialog(this, "Jumlah gram asal dan hasil harus sama !");
            Check = false;
        }

        if (Check) {
            insert();
        }
    }//GEN-LAST:event_button_SAVEActionPerformed

    private void button_tambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_tambahActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Table_pecah_hasil.getModel();
        Object[] row = new Object[25];
        row[0] = null;
        row[1] = Table_pecah_asal.getValueAt(0, 1);
        row[2] = Table_pecah_asal.getValueAt(0, 2);
        row[3] = Table_pecah_asal.getValueAt(0, 3);
        row[4] = Table_pecah_asal.getValueAt(0, 4);
        row[5] = Table_pecah_asal.getValueAt(0, 5);
        row[6] = Table_pecah_asal.getValueAt(0, 6);
//        row[4] = 0;
//        row[5] = 0;
//        row[6] = 0;
        row[7] = ComboBox_buluUpah.getSelectedItem().toString();
        row[8] = Table_pecah_asal.getValueAt(0, 8);
        row[9] = Table_pecah_asal.getValueAt(0, 9);
        row[10] = Table_pecah_asal.getValueAt(0, 10);
        row[11] = Table_pecah_asal.getValueAt(0, 11);
        row[12] = Table_pecah_asal.getValueAt(0, 12);
        row[13] = Table_pecah_asal.getValueAt(0, 13);
        row[14] = Table_pecah_asal.getValueAt(0, 14);
        row[15] = Table_pecah_asal.getValueAt(0, 15);
        row[16] = Table_pecah_asal.getValueAt(0, 16);
        row[17] = Table_pecah_asal.getValueAt(0, 17);
        row[18] = Table_pecah_asal.getValueAt(0, 18);
        row[19] = Table_pecah_asal.getValueAt(0, 19);
        row[20] = Table_pecah_asal.getValueAt(0, 20);
        row[21] = Table_pecah_asal.getValueAt(0, 21);
        model.addRow(row);
        ColumnsAutoSizer.sizeColumnsToFit(Table_pecah_hasil);
        count();
    }//GEN-LAST:event_button_tambahActionPerformed

    private void button_hapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_hapusActionPerformed
        // TODO add your handling code here:
        int i = Table_pecah_hasil.getSelectedRow();
        if (i > -1) {
            DefaultTableModel model = (DefaultTableModel) Table_pecah_hasil.getModel();
            model.removeRow(i);
            ColumnsAutoSizer.sizeColumnsToFit(Table_pecah_hasil);
            count();
        }
    }//GEN-LAST:event_button_hapusActionPerformed

    private void Table_pecah_hasilKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Table_pecah_hasilKeyPressed
        // TODO add your handling code here:
        count();
    }//GEN-LAST:event_Table_pecah_hasilKeyPressed

    private void Table_pecah_hasilMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Table_pecah_hasilMouseClicked
        // TODO add your handling code here:
        count();
    }//GEN-LAST:event_Table_pecah_hasilMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_buluUpah;
    private javax.swing.JTable Table_pecah_asal;
    private javax.swing.JTable Table_pecah_hasil;
    public javax.swing.JButton button_SAVE;
    public javax.swing.JButton button_hapus;
    public javax.swing.JButton button_tambah;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel_operation_bahan_baku;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel label_total_gram;
    private javax.swing.JLabel label_total_kpg;
    private javax.swing.JLabel label_total_kpg_upah;
    // End of variables declaration//GEN-END:variables
}
