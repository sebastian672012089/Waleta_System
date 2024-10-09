package waleta_system.Finance;

import waleta_system.Class.Utility;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import waleta_system.BahanBaku.JPanel_GradeBahanBaku;
import waleta_system.Class.ColumnsAutoSizer;

import waleta_system.Class.ExportToExcel;
import waleta_system.Class.Utility;

public class JPanel_Harga_PembelianBarangJadi extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();

    public JPanel_Harga_PembelianBarangJadi() {
        initComponents();
    }

    public void init() {
        try {
            refreshTable();
        } catch (Exception ex) {
            Logger.getLogger(JPanel_GradeBahanBaku.class.getName()).log(Level.SEVERE, null, ex);
        }
        Table_pembelian_bahan_jadi.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && Table_pembelian_bahan_jadi.getSelectedRow() != -1) {
                    int i = Table_pembelian_bahan_jadi.getSelectedRow();
                }
            }
        });
    }

    public void refreshTable() {
        try {
            String SearchBy = "";
            int total_kpg = 0;
            double total_harga = 0, total_gram = 0;
            decimalFormat.setGroupingUsed(true);
            decimalFormat.setMaximumFractionDigits(5);
            if (null != ComboBox_SearchBy.getSelectedItem().toString()) {
                switch (ComboBox_SearchBy.getSelectedItem().toString()) {
                    case "Kode Pembelian":
                        SearchBy = "`kode_pembelian_bahan_jadi` LIKE '%" + txt_keyword.getText() + "%'";
                        break;
                    case "nama_supplier":
                        SearchBy = "`kode_pembelian_bahan_jadi` LIKE '%" + txt_keyword.getText() + "%'";
                        break;
                    default:
                        SearchBy = "";
                        break;
                }
            }

            DefaultTableModel model = (DefaultTableModel) Table_pembelian_bahan_jadi.getModel();
            model.setRowCount(0);
            sql = "SELECT `kode_pembelian_bahan_jadi`, `tanggal_pembelian`, `jumlah_keping`, `berat`, `harga`, `tb_supplier`.`nama_supplier`, `keterangan` \n"
                    + "FROM `tb_pembelian_barang_jadi` LEFT JOIN `tb_supplier` ON `tb_pembelian_barang_jadi`.`kode_supplier` = `tb_supplier`.`kode_supplier`\n"
                    + "WHERE " + SearchBy + "";
            if (Date_pembelian1.getDate() != null && Date_pembelian2.getDate() != null) {
                sql = "SELECT `kode_pembelian_bahan_jadi`, `tanggal_pembelian`, `jumlah_keping`, `berat`, `harga`, `tb_supplier`.`nama_supplier`, `keterangan` \n"
                        + "FROM `tb_pembelian_barang_jadi` LEFT JOIN `tb_supplier` ON `tb_pembelian_barang_jadi`.`kode_supplier` = `tb_supplier`.`kode_supplier`\n"
                        + "WHERE " + SearchBy + " AND (`tanggal_pembelian` BETWEEN '" + dateFormat.format(Date_pembelian1.getDate()) + "' AND '" + dateFormat.format(Date_pembelian2.getDate()) + "')";
            }
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[8];
            while (rs.next()) {
                row[0] = rs.getString("kode_pembelian_bahan_jadi");
                row[1] = rs.getDate("tanggal_pembelian");
                row[2] = rs.getInt("jumlah_keping");
                total_kpg = total_kpg + rs.getInt("jumlah_keping");
                row[3] = rs.getInt("berat");
                total_gram = total_gram + rs.getDouble("berat");
                row[4] = rs.getString("nama_supplier");
                row[5] = rs.getString("keterangan");
                row[6] = decimalFormat.format(rs.getDouble("harga"));
                double subtotal = rs.getDouble("harga") * rs.getDouble("berat");
                total_harga = total_harga + subtotal;
                row[7] = decimalFormat.format(subtotal);
                model.addRow(row);
            }

            int rowData = Table_pembelian_bahan_jadi.getRowCount();
            label_total_data1.setText(Integer.toString(rowData));
            label_total_kpg.setText(decimalFormat.format(total_kpg));
            label_total_gram.setText(decimalFormat.format(total_gram));
            label_total_harga.setText(decimalFormat.format(total_harga));

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_Harga_PembelianBarangJadi.class.getName()).log(Level.SEVERE, null, ex);
        }
        ColumnsAutoSizer.sizeColumnsToFit(Table_pembelian_bahan_jadi);
        TableAlignment.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < Table_pembelian_bahan_jadi.getColumnCount(); i++) {
            Table_pembelian_bahan_jadi.getColumnModel().getColumn(i).setCellRenderer(TableAlignment);
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

        jPanel_Bahan_Baku_Keluar = new javax.swing.JPanel();
        jPanel_search_baku_keluar = new javax.swing.JPanel();
        txt_keyword = new javax.swing.JTextField();
        button_search = new javax.swing.JButton();
        button_export = new javax.swing.JButton();
        Date_pembelian1 = new com.toedter.calendar.JDateChooser();
        label_tgl_kh2 = new javax.swing.JLabel();
        Date_pembelian2 = new com.toedter.calendar.JDateChooser();
        ComboBox_SearchBy = new javax.swing.JComboBox<>();
        jLabel18 = new javax.swing.JLabel();
        jScrollPane7 = new javax.swing.JScrollPane();
        Table_pembelian_bahan_jadi = new javax.swing.JTable();
        jLabel20 = new javax.swing.JLabel();
        label_total_kpg = new javax.swing.JLabel();
        label_total_harga = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        label_total_data1 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        label_total_gram = new javax.swing.JLabel();

        jPanel_Bahan_Baku_Keluar.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_Bahan_Baku_Keluar.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Pembelian Barang Jadi", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N
        jPanel_Bahan_Baku_Keluar.setPreferredSize(new java.awt.Dimension(1366, 700));

        jPanel_search_baku_keluar.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_search_baku_keluar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));

        txt_keyword.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_keyword.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_keywordKeyPressed(evt);
            }
        });

        button_search.setBackground(new java.awt.Color(255, 255, 255));
        button_search.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search.setText("Search");
        button_search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_searchActionPerformed(evt);
            }
        });

        button_export.setBackground(new java.awt.Color(255, 255, 255));
        button_export.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export.setText("Export To Excel");
        button_export.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_exportActionPerformed(evt);
            }
        });

        Date_pembelian1.setBackground(new java.awt.Color(255, 255, 255));
        Date_pembelian1.setDateFormatString("dd MMMM yyyy");
        Date_pembelian1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        label_tgl_kh2.setBackground(new java.awt.Color(255, 255, 255));
        label_tgl_kh2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_tgl_kh2.setText("Tanggal Pembelian :");

        Date_pembelian2.setBackground(new java.awt.Color(255, 255, 255));
        Date_pembelian2.setDateFormatString("dd MMMM yyyy");
        Date_pembelian2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        ComboBox_SearchBy.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_SearchBy.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Kode Pembelian", "Supplier" }));

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel18.setText("Search By ");

        javax.swing.GroupLayout jPanel_search_baku_keluarLayout = new javax.swing.GroupLayout(jPanel_search_baku_keluar);
        jPanel_search_baku_keluar.setLayout(jPanel_search_baku_keluarLayout);
        jPanel_search_baku_keluarLayout.setHorizontalGroup(
            jPanel_search_baku_keluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_search_baku_keluarLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ComboBox_SearchBy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_keyword, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(label_tgl_kh2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Date_pembelian1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Date_pembelian2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_search)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 401, Short.MAX_VALUE)
                .addComponent(button_export)
                .addContainerGap())
        );
        jPanel_search_baku_keluarLayout.setVerticalGroup(
            jPanel_search_baku_keluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_search_baku_keluarLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel_search_baku_keluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_keyword, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_pembelian1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_tgl_kh2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_pembelian2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_SearchBy, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_search, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        Table_pembelian_bahan_jadi.setAutoCreateRowSorter(true);
        Table_pembelian_bahan_jadi.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode Pembelian", "Tgl Masuk", "Keping", "Gram", "Supplier", "Keterangan", "Harga per Gram", "SubTotal"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.Double.class
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
        Table_pembelian_bahan_jadi.getTableHeader().setReorderingAllowed(false);
        Table_pembelian_bahan_jadi.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Table_pembelian_bahan_jadiMouseClicked(evt);
            }
        });
        jScrollPane7.setViewportView(Table_pembelian_bahan_jadi);

        jLabel20.setBackground(new java.awt.Color(255, 255, 255));
        jLabel20.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel20.setText("Total Keping :");

        label_total_kpg.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kpg.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        label_total_kpg.setText("TOTAL");

        label_total_harga.setBackground(new java.awt.Color(255, 255, 255));
        label_total_harga.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        label_total_harga.setText("TOTAL");

        jLabel21.setBackground(new java.awt.Color(255, 255, 255));
        jLabel21.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel21.setText("Total Harga :");

        jLabel22.setBackground(new java.awt.Color(255, 255, 255));
        jLabel22.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel22.setText("Total Data :");

        label_total_data1.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data1.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        label_total_data1.setText("TOTAL");

        jLabel23.setBackground(new java.awt.Color(255, 255, 255));
        jLabel23.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel23.setText("Total Gram :");

        label_total_gram.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        label_total_gram.setText("TOTAL");

        javax.swing.GroupLayout jPanel_Bahan_Baku_KeluarLayout = new javax.swing.GroupLayout(jPanel_Bahan_Baku_Keluar);
        jPanel_Bahan_Baku_Keluar.setLayout(jPanel_Bahan_Baku_KeluarLayout);
        jPanel_Bahan_Baku_KeluarLayout.setHorizontalGroup(
            jPanel_Bahan_Baku_KeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Bahan_Baku_KeluarLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_Bahan_Baku_KeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel_search_baku_keluar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane7)
                    .addGroup(jPanel_Bahan_Baku_KeluarLayout.createSequentialGroup()
                        .addGroup(jPanel_Bahan_Baku_KeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_Bahan_Baku_KeluarLayout.createSequentialGroup()
                                .addComponent(jLabel20)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_kpg))
                            .addGroup(jPanel_Bahan_Baku_KeluarLayout.createSequentialGroup()
                                .addComponent(jLabel21)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_harga))
                            .addGroup(jPanel_Bahan_Baku_KeluarLayout.createSequentialGroup()
                                .addComponent(jLabel22)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_data1))
                            .addGroup(jPanel_Bahan_Baku_KeluarLayout.createSequentialGroup()
                                .addComponent(jLabel23)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_gram)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel_Bahan_Baku_KeluarLayout.setVerticalGroup(
            jPanel_Bahan_Baku_KeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Bahan_Baku_KeluarLayout.createSequentialGroup()
                .addComponent(jPanel_search_baku_keluar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 523, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_Bahan_Baku_KeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_total_data1)
                    .addComponent(jLabel22))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_Bahan_Baku_KeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_total_kpg)
                    .addComponent(jLabel20))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_Bahan_Baku_KeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_total_gram)
                    .addComponent(jLabel23))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_Bahan_Baku_KeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_total_harga)
                    .addComponent(jLabel21))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jPanel_Bahan_Baku_Keluar, javax.swing.GroupLayout.DEFAULT_SIZE, 1364, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel_Bahan_Baku_Keluar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txt_keywordKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_keywordKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_keywordKeyPressed

    private void button_searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_searchActionPerformed
        // TODO add your handling code here:
        refreshTable();
    }//GEN-LAST:event_button_searchActionPerformed

    private void button_exportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_exportActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Table_pembelian_bahan_jadi.getModel();
        ExportToExcel.writeToExcel(model, this);
    }//GEN-LAST:event_button_exportActionPerformed

    private void Table_pembelian_bahan_jadiMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Table_pembelian_bahan_jadiMouseClicked
        // TODO add your handling code here:
        decimalFormat.setMaximumFractionDigits(5);
        decimalFormat.setGroupingUsed(false);
        int i = Table_pembelian_bahan_jadi.getSelectedRow();
        if (evt.getClickCount() == 2) {
            try {
                String old_price = Table_pembelian_bahan_jadi.getValueAt(i, 6).toString();
                String price1 = "";
                price1 = old_price.replace(",", "");
                String harga = JOptionPane.showInputDialog("Masukkan Harga : ", price1);
                System.out.println(harga);
                double HARGA_F = Double.valueOf(harga);
                decimalFormat = Utility.DecimalFormatUS(decimalFormat);
                System.out.println(decimalFormat.format(HARGA_F));
                sql = "UPDATE `tb_pembelian_barang_jadi` SET `harga`='" + decimalFormat.format(HARGA_F) + "' WHERE `kode_pembelian_bahan_jadi`='" + Table_pembelian_bahan_jadi.getValueAt(i, 0).toString() + "'";
                if ((Utility.db.getStatement().executeUpdate(sql)) == 1) {
                    refreshTable();
                    JOptionPane.showMessageDialog(this, "Update success!");
                } else {
                    JOptionPane.showMessageDialog(this, "Update failed!");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
                Logger.getLogger(JPanel_Harga_PembelianBarangJadi.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Input must be number!");
                Logger.getLogger(JPanel_Harga_PembelianBarangJadi.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_Table_pembelian_bahan_jadiMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_SearchBy;
    private com.toedter.calendar.JDateChooser Date_pembelian1;
    private com.toedter.calendar.JDateChooser Date_pembelian2;
    public static javax.swing.JTable Table_pembelian_bahan_jadi;
    public static javax.swing.JButton button_export;
    private javax.swing.JButton button_search;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JPanel jPanel_Bahan_Baku_Keluar;
    private javax.swing.JPanel jPanel_search_baku_keluar;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JLabel label_tgl_kh2;
    private javax.swing.JLabel label_total_data1;
    private javax.swing.JLabel label_total_gram;
    private javax.swing.JLabel label_total_harga;
    private javax.swing.JLabel label_total_kpg;
    private javax.swing.JTextField txt_keyword;
    // End of variables declaration//GEN-END:variables
}
