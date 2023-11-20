package waleta_system.Finance;

import waleta_system.Class.Utility;

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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import waleta_system.Class.ColumnsAutoSizer;

import waleta_system.Class.ExportToExcel;
import waleta_system.Class.Utility;

public class JPanel_StokOpnameGBJ_RekapGrade extends javax.swing.JPanel {

    
    String sql = null;
    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();

    public JPanel_StokOpnameGBJ_RekapGrade() {
        initComponents();
    }

    public void init() {
        try {
            ComboBox_lokasi.removeAllItems();
            ComboBox_lokasi.addItem("All");
            sql = "SELECT DISTINCT(`lokasi_terakhir`) AS 'lokasi_terakhir' FROM `tb_stokopname_gbj`";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_lokasi.addItem(rs.getString("lokasi_terakhir"));
            }
            sql = "SELECT `nilai` FROM `tb_kurs` WHERE 1 ORDER BY `tanggal` DESC LIMIT 1";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                txt_kurs_cnyidr.setText(rs.getString("nilai"));
            }
            refreshTable_DataSO();
            tabel_stockOpname.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent event) {
                    if (!event.getValueIsAdjusting() && tabel_stockOpname.getSelectedRow() != -1) {
                        int x = tabel_stockOpname.getSelectedRow();
                        if (x > -1) {
                            label_tanggal_SO.setText(tabel_stockOpname.getValueAt(x, 0).toString());
                            refreshTable_detailBox(tabel_stockOpname.getValueAt(x, 0).toString());
                        }
                    }
                }
            });
        } catch (Exception ex) {
            Logger.getLogger(JPanel_StokOpnameGBJ_RekapGrade.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_DataSO() {
        try {
            DefaultTableModel model = (DefaultTableModel) tabel_stockOpname.getModel();
            model.setRowCount(0);
            sql = "SELECT `tb_stokopname`.`tgl_stok_opname`, `nilai_baku`, COUNT(`no_box`) AS 'box', SUM(`berat`) AS 'berat', SUM(`keping`) AS 'kpg' \n"
                    + "FROM `tb_stokopname` LEFT JOIN `tb_stokopname_gbj` ON `tb_stokopname`.`tgl_stok_opname` = `tb_stokopname_gbj`.`tgl_stok_opname` "
                    + "WHERE 1 GROUP BY `tb_stokopname`.`tgl_stok_opname`";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[5];
            while (rs.next()) {
                row[0] = rs.getDate("tgl_stok_opname");
                row[1] = rs.getDouble("nilai_baku");
                row[2] = rs.getDouble("kpg");
                row[3] = rs.getDouble("berat");
                row[4] = rs.getInt("box");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(tabel_stockOpname);
            label_total_data_SO.setText(Integer.toString(tabel_stockOpname.getRowCount()));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_StokOpnameGBJ_RekapGrade.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_detailBox(String tgl_SO) {
        try {
            String lokasi = " AND `tb_stokopname_gbj`.`lokasi_terakhir` = '" + ComboBox_lokasi.getSelectedItem().toString() + "' ";
            if (ComboBox_lokasi.getSelectedItem().toString().equals("All")) {
                lokasi = "";
            }

            double total_keping = 0, total_gram = 0, total_nilai_jual = 0;
            double kurs_cnyidr = Double.valueOf(txt_kurs_cnyidr.getText());
            DefaultTableModel model = (DefaultTableModel) tabel_detailBox.getModel();
            model.setRowCount(0);
            sql = "SELECT `tgl_stok_opname`, `tb_grade_bahan_jadi`.`kode_grade`, `bentuk_grade`, `Kategori1`, SUM(`keping`) AS 'total_kpg', SUM(`berat`) AS 'total_gram', `harga_cny_kg`, SUM(`harga_cny_kg` * `berat` / 1000) AS 'total_nilai_jual' \n"
                    + "FROM `tb_stokopname_gbj` "
                    + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_stokopname_gbj`.`kode_grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode`\n"
                    + "WHERE `tgl_stok_opname` = '" + tgl_SO + "' AND `tb_grade_bahan_jadi`.`kode_grade` LIKE '%" + txt_search_grade.getText() + "%' "
                    + lokasi
                    + "GROUP BY `tb_grade_bahan_jadi`.`kode_grade` "
                    + "ORDER BY `tb_grade_bahan_jadi`.`kode_grade`";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[10];
            while (rs.next()) {
                row[0] = rs.getString("kode_grade");
                row[1] = rs.getString("bentuk_grade");
                row[2] = rs.getString("Kategori1");
                row[3] = rs.getInt("total_kpg");
                total_keping = total_keping + rs.getInt("total_kpg");
                row[4] = rs.getFloat("total_gram");
                row[5] = rs.getFloat("harga_cny_kg");
                row[6] = rs.getFloat("total_nilai_jual");
                total_gram = total_gram + rs.getFloat("total_gram");
                row[7] = Math.round(rs.getDouble("total_nilai_jual") * kurs_cnyidr);
                total_nilai_jual = total_nilai_jual + Math.round(rs.getDouble("total_nilai_jual") * kurs_cnyidr);
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(tabel_detailBox);
            label_total_box.setText(Integer.toString(tabel_detailBox.getRowCount()));
            label_total_keping_box.setText(decimalFormat.format(total_keping));
            label_total_gram_box.setText(decimalFormat.format(total_gram));
            label_total_nilai_jual.setText(decimalFormat.format(total_nilai_jual));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_StokOpnameGBJ_RekapGrade.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        txt_search_grade = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        button_search = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        ComboBox_lokasi = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tabel_stockOpname = new javax.swing.JTable();
        jLabel14 = new javax.swing.JLabel();
        label_total_data_SO = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabel_detailBox = new javax.swing.JTable();
        button_Export_tabelbox = new javax.swing.JButton();
        label_total_box = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        label_total_gram_box = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        label_total_keping_box = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        label_tanggal_SO = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        label_total_nilai_jual = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txt_kurs_cnyidr = new javax.swing.JTextField();

        setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Stock Barang Jadi", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N

        txt_search_grade.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_grade.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_gradeKeyPressed(evt);
            }
        });

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("Search Grade :");

        button_search.setBackground(new java.awt.Color(255, 255, 255));
        button_search.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search.setText("Refresh");
        button_search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_searchActionPerformed(evt);
            }
        });

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel11.setText("Stock Box Barang Jadi");

        ComboBox_lokasi.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_lokasi.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel12.setText("Lokasi :");

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel13.setText("Data Stock Opname");

        tabel_stockOpname.setAutoCreateRowSorter(true);
        tabel_stockOpname.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tabel_stockOpname.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tanggal Stok Opname", "Nilai Baku", "Total Stok Kpg", "Total Stok Gram", "Jumlah Box"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabel_stockOpname.getTableHeader().setReorderingAllowed(false);
        tabel_stockOpname.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabel_stockOpnameMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tabel_stockOpname);

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel14.setText("Total Data :");

        label_total_data_SO.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_SO.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_data_SO.setText("0");

        tabel_detailBox.setAutoCreateRowSorter(true);
        tabel_detailBox.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tabel_detailBox.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Grade", "Bentuk", "Kategori", "Kpg", "Gram", "Satuan CNY/Kg", "Nilai Jual CNY", "Nilai Jual IDR"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Float.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
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
        tabel_detailBox.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(tabel_detailBox);

        button_Export_tabelbox.setBackground(new java.awt.Color(255, 255, 255));
        button_Export_tabelbox.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_Export_tabelbox.setText("Export to Excel");
        button_Export_tabelbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_Export_tabelboxActionPerformed(evt);
            }
        });

        label_total_box.setBackground(new java.awt.Color(255, 255, 255));
        label_total_box.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_box.setText("0");

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel8.setText("Total Grade :");

        label_total_gram_box.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_box.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_gram_box.setText("0");

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel7.setText("Total Gram :");

        label_total_keping_box.setBackground(new java.awt.Color(255, 255, 255));
        label_total_keping_box.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_keping_box.setText("0");

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel9.setText("Total Keping :");

        label_tanggal_SO.setBackground(new java.awt.Color(255, 255, 255));
        label_tanggal_SO.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_tanggal_SO.setForeground(new java.awt.Color(255, 0, 0));
        label_tanggal_SO.setText("dd MMM yyyy");

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel10.setText("Total Nilai Jual : Rp.");

        label_total_nilai_jual.setBackground(new java.awt.Color(255, 255, 255));
        label_total_nilai_jual.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_nilai_jual.setText("0");

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("CNY/IDR :");

        txt_kurs_cnyidr.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_kurs_cnyidr.setText("1");
        txt_kurs_cnyidr.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_kurs_cnyidrKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 414, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_data_SO)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 916, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_tanggal_SO)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_box)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_keping_box)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_gram_box)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_nilai_jual)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(button_Export_tabelbox))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_grade, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_kurs_cnyidr, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_lokasi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_search)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_grade, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_kurs_cnyidr, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_search, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_lokasi, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14)
                            .addComponent(label_total_data_SO))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 599, Short.MAX_VALUE)
                            .addComponent(jScrollPane3)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                .addComponent(jLabel10)
                                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(label_total_box)
                                .addComponent(jLabel8)
                                .addComponent(label_total_gram_box)
                                .addComponent(jLabel7)
                                .addComponent(label_total_keping_box)
                                .addComponent(jLabel9)
                                .addComponent(label_tanggal_SO, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(label_total_nilai_jual))
                            .addComponent(button_Export_tabelbox, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
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

    private void button_searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_searchActionPerformed
        // TODO add your handling code here:
        refreshTable_detailBox(label_tanggal_SO.getText());
    }//GEN-LAST:event_button_searchActionPerformed

    private void button_Export_tabelboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_Export_tabelboxActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) tabel_detailBox.getModel();
        ExportToExcel.writeToExcel(model, jPanel1);
    }//GEN-LAST:event_button_Export_tabelboxActionPerformed

    private void txt_search_gradeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_gradeKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_detailBox(label_tanggal_SO.getText());
        }
    }//GEN-LAST:event_txt_search_gradeKeyPressed

    private void txt_kurs_cnyidrKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_kurs_cnyidrKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_kurs_cnyidrKeyPressed

    private void tabel_stockOpnameMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabel_stockOpnameMouseClicked
        
        decimalFormat.setMaximumFractionDigits(5);
        decimalFormat.setGroupingUsed(false);
        int i = tabel_stockOpname.getSelectedRow();
        if (evt.getClickCount() == 2) {
            try {
                String old_price = tabel_stockOpname.getValueAt(i, 1).toString();
                String price1 = "";
                price1 = old_price.replace(",", "");
                
                String harga = JOptionPane.showInputDialog("Masukkan Harga : ", price1);
                double HARGA_F = Double.valueOf(harga);
                decimalFormat = Utility.DecimalFormatUS(decimalFormat);
                sql = "UPDATE `tb_stokopname` SET `nilai_baku`='" + decimalFormat.format(HARGA_F) + "' WHERE `tgl_stok_opname`='" + tabel_stockOpname.getValueAt(i, 0).toString() + "'";
                if ((Utility.db.getStatement().executeUpdate(sql)) == 1) {
                    refreshTable_DataSO();
                    JOptionPane.showMessageDialog(this, "Update success!");
                } else {
                    JOptionPane.showMessageDialog(this, "Update failed!");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, ex);
                Logger.getLogger(JPanel_StokOpnameGBJ_RekapGrade.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Input must be number!");
                Logger.getLogger(JPanel_StokOpnameGBJ_RekapGrade.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_tabel_stockOpnameMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_lokasi;
    private javax.swing.JButton button_Export_tabelbox;
    private javax.swing.JButton button_search;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel label_tanggal_SO;
    private javax.swing.JLabel label_total_box;
    private javax.swing.JLabel label_total_data_SO;
    private javax.swing.JLabel label_total_gram_box;
    private javax.swing.JLabel label_total_keping_box;
    private javax.swing.JLabel label_total_nilai_jual;
    private javax.swing.JTable tabel_detailBox;
    private javax.swing.JTable tabel_stockOpname;
    private javax.swing.JTextField txt_kurs_cnyidr;
    private javax.swing.JTextField txt_search_grade;
    // End of variables declaration//GEN-END:variables
}
