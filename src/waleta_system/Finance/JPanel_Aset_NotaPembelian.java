package waleta_system.Finance;

import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.ExportToExcel;
import waleta_system.Class.Utility;

public class JPanel_Aset_NotaPembelian extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    DecimalFormat decimalFormat = new DecimalFormat();

    public JPanel_Aset_NotaPembelian() {
        initComponents();
    }

    public void init() {
        refreshTable();
        table_nota.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && table_nota.getSelectedRow() != -1) {
                    int i = table_nota.getSelectedRow();
                    if (i > -1) {
                        refreshTable_detailNota(table_nota.getValueAt(i, 0).toString());
                        label_kode_nota.setText(table_nota.getValueAt(i, 0).toString());
                    }
                }
            }
        });
    }

    public void refreshTable() {
        try {
            int total_nilai = 0;
            DefaultTableModel model = (DefaultTableModel) table_nota.getModel();
            model.setRowCount(0);
            sql = "SELECT `kode_nota`, `no_voucher_keuangan`, `tanggal_debit`, `tanggal_nota`, `ongkir`, `nilai_nota`, `supplier_nota`, `keterangan_nota` "
                    + "FROM `tb_aset_nota_pembelian` "
                    + "WHERE "
                    + "`kode_nota` LIKE '%" + txt_search_keyword.getText() + "%' OR "
                    + "`no_voucher_keuangan` LIKE '%" + txt_search_keyword.getText() + "%' OR "
                    + "`supplier_nota` LIKE '%" + txt_search_keyword.getText() + "%' OR "
                    + "`keterangan_nota` LIKE '%" + txt_search_keyword.getText() + "%' ";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[10];
            while (rs.next()) {
                row[0] = rs.getString("kode_nota");
                row[1] = rs.getString("no_voucher_keuangan");
                row[2] = rs.getDate("tanggal_debit");
                row[3] = rs.getDate("tanggal_nota");
                row[4] = rs.getInt("ongkir");
                row[5] = rs.getInt("nilai_nota");
                row[6] = rs.getString("supplier_nota");
                row[7] = rs.getString("keterangan_nota");
                model.addRow(row);
                total_nilai += rs.getInt("nilai_nota");
            }
            ColumnsAutoSizer.sizeColumnsToFit(table_nota);
            label_total_data_nota.setText(decimalFormat.format(table_nota.getRowCount()));
            label_total_nilai_nota.setText("Rp. " + decimalFormat.format(total_nilai) + ",-");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Aset_NotaPembelian.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_detailNota(String kode_nota) {
        try {
            int total_nilai = 0;
            DefaultTableModel model = (DefaultTableModel) table_nota_detail.getModel();
            model.setRowCount(0);
            sql = "SELECT `kode_nota_detail`, `kode_nota`, `tb_aset_nota_detail`.`kode_aset`, `tgl_datang`, `jumlah`, `satuan`, `harga_satuan`, `diskon`, `pajak`, `departemen`, `keperluan`, "
                    + "`nama_aset`, `merk_aset`, `spesifikasi_aset` "
                    + "FROM `tb_aset_nota_detail` "
                    + "LEFT JOIN `tb_aset_master` ON `tb_aset_nota_detail`.`kode_aset` = `tb_aset_master`.`kode_aset` "
                    + "WHERE `kode_nota` = '" + kode_nota + "' ";
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
                row[9] = rs.getInt("harga_satuan");
                row[10] = rs.getInt("diskon");
                row[11] = rs.getInt("pajak");
                row[12] = (rs.getInt("jumlah") * rs.getInt("harga_satuan")) + rs.getInt("pajak") - rs.getInt("diskon");
                row[13] = rs.getString("departemen");
                row[14] = rs.getString("keperluan");
                model.addRow(row);
                total_nilai += (rs.getInt("jumlah") * rs.getInt("harga_satuan")) + rs.getInt("pajak") - rs.getInt("diskon");
            }
            ColumnsAutoSizer.sizeColumnsToFit(table_nota_detail);
            label_total_data_detailNota.setText(decimalFormat.format(table_nota_detail.getRowCount()));
            label_total_nilai_detailNota.setText("Rp. " + decimalFormat.format(total_nilai) + ",-");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Aset_NotaPembelian.class.getName()).log(Level.SEVERE, null, ex);
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
        jScrollPane1 = new javax.swing.JScrollPane();
        table_nota = new javax.swing.JTable();
        button_new = new javax.swing.JButton();
        button_edit = new javax.swing.JButton();
        button_delete = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        txt_search_keyword = new javax.swing.JTextField();
        button_search = new javax.swing.JButton();
        button_export = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        table_nota_detail = new javax.swing.JTable();
        jLabel17 = new javax.swing.JLabel();
        label_kode_nota = new javax.swing.JLabel();
        button_edit_detailNota = new javax.swing.JButton();
        label_total_nilai_detailNota = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        label_total_data_detailNota = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        button_new_detailNota = new javax.swing.JButton();
        button_delete_detailNota = new javax.swing.JButton();
        button_export_detailNota = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        label_total_data_nota = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        label_total_nilai_nota = new javax.swing.JLabel();

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        table_nota.setAutoCreateRowSorter(true);
        table_nota.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_nota.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode Nota", "No Voucher", "Tgl Debit", "Tgl Nota", "Ongkir", "Nilai Nota", "Supplier", "Keterangan"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class, java.lang.String.class, java.lang.String.class
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
        table_nota.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(table_nota);

        button_new.setBackground(new java.awt.Color(255, 255, 255));
        button_new.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_new.setText("New");
        button_new.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_newActionPerformed(evt);
            }
        });

        button_edit.setBackground(new java.awt.Color(255, 255, 255));
        button_edit.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_edit.setText("Edit");
        button_edit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_editActionPerformed(evt);
            }
        });

        button_delete.setBackground(new java.awt.Color(255, 255, 255));
        button_delete.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_delete.setText("Delete");
        button_delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_deleteActionPerformed(evt);
            }
        });

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel9.setText("Search Keyword :");

        txt_search_keyword.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_search_keyword.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_keywordKeyPressed(evt);
            }
        });

        button_search.setBackground(new java.awt.Color(255, 255, 255));
        button_search.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_search.setText("Search");
        button_search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_searchActionPerformed(evt);
            }
        });

        button_export.setBackground(new java.awt.Color(255, 255, 255));
        button_export.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_export.setText("Export To Excel");
        button_export.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_exportActionPerformed(evt);
            }
        });

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel16.setText("Nota Pembelian");

        table_nota_detail.setAutoCreateRowSorter(true);
        table_nota_detail.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_nota_detail.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode Nota", "Kode", "Kode Aset", "Nama Aset", "Merk", "Spesifikasi", "Tgl Datang", "Jumlah", "Satuan", "Harga Satuan (Rp.)", "Diskon (Rp.)", "Pajak (Rp.)", "Subtotal (Rp.)", "Departemen", "Keperluan"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Float.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table_nota_detail.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(table_nota_detail);

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel17.setText("Detail nota :");

        label_kode_nota.setBackground(new java.awt.Color(255, 255, 255));
        label_kode_nota.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_kode_nota.setForeground(new java.awt.Color(255, 0, 0));
        label_kode_nota.setText("kode_nota");

        button_edit_detailNota.setBackground(new java.awt.Color(255, 255, 255));
        button_edit_detailNota.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_edit_detailNota.setText("Edit");
        button_edit_detailNota.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_edit_detailNotaActionPerformed(evt);
            }
        });

        label_total_nilai_detailNota.setBackground(new java.awt.Color(255, 255, 255));
        label_total_nilai_detailNota.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_nilai_detailNota.setText("0000");

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel3.setText("Total Data :");

        label_total_data_detailNota.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_detailNota.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_data_detailNota.setText("0000");

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel18.setText("Total Nilai :");

        button_new_detailNota.setBackground(new java.awt.Color(255, 255, 255));
        button_new_detailNota.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_new_detailNota.setText("New");
        button_new_detailNota.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_new_detailNotaActionPerformed(evt);
            }
        });

        button_delete_detailNota.setBackground(new java.awt.Color(255, 255, 255));
        button_delete_detailNota.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_delete_detailNota.setText("Delete");
        button_delete_detailNota.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_delete_detailNotaActionPerformed(evt);
            }
        });

        button_export_detailNota.setBackground(new java.awt.Color(255, 255, 255));
        button_export_detailNota.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_export_detailNota.setText("Export To Excel");
        button_export_detailNota.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_detailNotaActionPerformed(evt);
            }
        });

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel4.setText("Total Data :");

        label_total_data_nota.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_nota.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_data_nota.setText("0000");

        jLabel19.setBackground(new java.awt.Color(255, 255, 255));
        jLabel19.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel19.setText("Total Nilai :");

        label_total_nilai_nota.setBackground(new java.awt.Color(255, 255, 255));
        label_total_nilai_nota.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_nilai_nota.setText("0000");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1346, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1346, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel16)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel17)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_kode_nota)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_data_detailNota)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel18)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_nilai_detailNota)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(button_new_detailNota)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_edit_detailNota)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_delete_detailNota)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_export_detailNota))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_keyword, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_search)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_data_nota)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel19)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_nilai_nota)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_new)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_edit)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_delete)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_export)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_search_keyword, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_search, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_data_nota, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_nilai_nota, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(button_new, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_edit, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_delete, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_export, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 331, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(button_new_detailNota, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_delete_detailNota, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_edit_detailNota, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_export_detailNota, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_kode_nota, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_data_detailNota, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_nilai_detailNota, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void button_newActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_newActionPerformed
        // TODO add your handling code here:
        Boolean Check = true;
        try {
            if (Check) {
                JDialog_Aset_Nota_NewEdit dialog = new JDialog_Aset_Nota_NewEdit(new javax.swing.JFrame(), true, null);
                dialog.pack();
                dialog.setLocationRelativeTo(this);
                dialog.setVisible(true);
                dialog.setEnabled(true);
                refreshTable();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Aset_NotaPembelian.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_newActionPerformed

    private void button_editActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_editActionPerformed
        // TODO add your handling code here:
        int j = table_nota.getSelectedRow();
        Boolean Check = true;
        try {
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan Pilih data yang mau di edit !");
            } else {
                if (Check) {
                    String kodeNota = table_nota.getValueAt(j, 0).toString();
                    JDialog_Aset_Nota_NewEdit dialog = new JDialog_Aset_Nota_NewEdit(new javax.swing.JFrame(), true, kodeNota);
                    dialog.pack();
                    dialog.setLocationRelativeTo(this);
                    dialog.setVisible(true);
                    dialog.setEnabled(true);
                    refreshTable();
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Aset_NotaPembelian.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_editActionPerformed

    private void button_deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_deleteActionPerformed
        // TODO add your handling code here:
        try {
            int j = table_nota.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih data yang mau di hapus !");
            } else {
                int dialogResult = JOptionPane.showConfirmDialog(this, "Apakah yakin ingin menghapus data ini?", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    // delete code here
                    String Query = "DELETE FROM `tb_aset_nota_pembelian` WHERE `kode_nota` = '" + table_nota.getValueAt(j, 0).toString() + "'";
                    Utility.db.getConnection().createStatement();
                    if ((Utility.db.getStatement().executeUpdate(Query)) == 1) {
                        JOptionPane.showMessageDialog(this, "Data berhasil dihapus!");
                        refreshTable();
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Aset_NotaPembelian.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_deleteActionPerformed

    private void button_searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_searchActionPerformed
        // TODO add your handling code here:
        refreshTable();
    }//GEN-LAST:event_button_searchActionPerformed

    private void button_exportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_exportActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) table_nota.getModel();
        ExportToExcel.writeToExcel(model, jPanel1);
    }//GEN-LAST:event_button_exportActionPerformed

    private void txt_search_keywordKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_keywordKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_search_keywordKeyPressed

    private void button_edit_detailNotaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_edit_detailNotaActionPerformed
        // TODO add your handling code here:
        int j = table_nota_detail.getSelectedRow();
        Boolean Check = true;
        try {
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan Pilih detail barang yang mau di edit !");
            } else {
                String kodeNota = table_nota_detail.getValueAt(j, 0).toString();
                String kode = table_nota_detail.getValueAt(j, 1).toString();
                sql = "SELECT * FROM `tb_aset_unit` WHERE `kode_nota_detail` = '" + kode + "'";
                rs = Utility.db.getStatement().executeQuery(sql);
                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "Maaf jika mau edit harap hapus label yang sudah terbuat terlebih dulu!");
                    Check = false;
                }
                if (Check) {
                    JDialog_Aset_NotaDetail_NewEdit dialog = new JDialog_Aset_NotaDetail_NewEdit(new javax.swing.JFrame(), true, kodeNota, kode);
                    dialog.pack();
                    dialog.setLocationRelativeTo(this);
                    dialog.setVisible(true);
                    dialog.setEnabled(true);
                    refreshTable_detailNota(label_kode_nota.getText());
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Aset_NotaPembelian.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_edit_detailNotaActionPerformed

    private void button_new_detailNotaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_new_detailNotaActionPerformed
        // TODO add your handling code here:
        int j = table_nota.getSelectedRow();
        Boolean Check = true;
        try {
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan Pilih salah satu nota diatas !");
            } else {
                if (Check) {
                    String kodeNota = table_nota.getValueAt(j, 0).toString();
                    JDialog_Aset_NotaDetail_NewEdit dialog = new JDialog_Aset_NotaDetail_NewEdit(new javax.swing.JFrame(), true, kodeNota, null);
                    dialog.pack();
                    dialog.setLocationRelativeTo(this);
                    dialog.setVisible(true);
                    dialog.setEnabled(true);
                    refreshTable_detailNota(label_kode_nota.getText());
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Aset_NotaPembelian.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_new_detailNotaActionPerformed

    private void button_delete_detailNotaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_delete_detailNotaActionPerformed
        // TODO add your handling code here:
        try {
            boolean Check = true;
            int j = table_nota_detail.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih data yang mau di hapus !");
            } else {
                String kode = table_nota_detail.getValueAt(j, 1).toString();
                sql = "SELECT * FROM `tb_aset_unit` WHERE `kode_nota_detail` = '" + kode + "'";
                rs = Utility.db.getStatement().executeQuery(sql);
                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "Maaf jika mau hapus harap hapus label yang sudah terbuat terlebih dulu!");
                    Check = false;
                }

                if (Check) {
                    int dialogResult = JOptionPane.showConfirmDialog(this, "Apakah yakin ingin menghapus data ini?", "Warning", 0);
                    if (dialogResult == JOptionPane.YES_OPTION) {
                        // delete code here
                        String Query = "DELETE FROM `tb_aset_nota_detail` WHERE `kode_nota_detail` = '" + kode + "'";
                        Utility.db.getConnection().createStatement();
                        if ((Utility.db.getStatement().executeUpdate(Query)) == 1) {
                            JOptionPane.showMessageDialog(this, "Data berhasil dihapus!");
                            refreshTable_detailNota(label_kode_nota.getText());
                        }
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Aset_NotaPembelian.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_delete_detailNotaActionPerformed

    private void button_export_detailNotaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_detailNotaActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) table_nota_detail.getModel();
        ExportToExcel.writeToExcel(model, jPanel1);
    }//GEN-LAST:event_button_export_detailNotaActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton button_delete;
    private javax.swing.JButton button_delete_detailNota;
    private javax.swing.JButton button_edit;
    private javax.swing.JButton button_edit_detailNota;
    private javax.swing.JButton button_export;
    private javax.swing.JButton button_export_detailNota;
    private javax.swing.JButton button_new;
    private javax.swing.JButton button_new_detailNota;
    private javax.swing.JButton button_search;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel label_kode_nota;
    private javax.swing.JLabel label_total_data_detailNota;
    private javax.swing.JLabel label_total_data_nota;
    private javax.swing.JLabel label_total_nilai_detailNota;
    private javax.swing.JLabel label_total_nilai_nota;
    public static javax.swing.JTable table_nota;
    public static javax.swing.JTable table_nota_detail;
    private javax.swing.JTextField txt_search_keyword;
    // End of variables declaration//GEN-END:variables
}
