package waleta_system.SubWaleta;

import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
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
import waleta_system.Class.Utility;
import waleta_system.Class.ExportToExcel;

public class JPanel_Sub_Waleta extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();

    public JPanel_Sub_Waleta() {
        initComponents();
    }

    public void init() {
        try {
            refreshTable();
            Table_Sub_Waleta.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent event) {
                    if (!event.getValueIsAdjusting() && Table_Sub_Waleta.getSelectedRow() != -1) {
                        int i = Table_Sub_Waleta.getSelectedRow();
                        txt_kode_sub.setText(Table_Sub_Waleta.getValueAt(i, 0).toString());
                        String alamat = Table_Sub_Waleta.getValueAt(i, 1) == null ? "" : Table_Sub_Waleta.getValueAt(i, 1).toString();
                        txt_alamat.setText(alamat);
                        String id_penanggungjawab = Table_Sub_Waleta.getValueAt(i, 2) == null ? "" : Table_Sub_Waleta.getValueAt(i, 2).toString();
                        txt_id_penanggungjawab.setText(id_penanggungjawab);
                        String nama_penanggungjawab = Table_Sub_Waleta.getValueAt(i, 3) == null ? "" : Table_Sub_Waleta.getValueAt(i, 3).toString();
                        txt_nama_penanggungjawab.setText(nama_penanggungjawab);
                        try {
                            if (Table_Sub_Waleta.getValueAt(i, 4) == null) {
                                Date_berdiri.setDate(null);
                            } else {
                                Date_berdiri.setDate(dateFormat.parse(Table_Sub_Waleta.getValueAt(i, 4).toString()));
                            }
                            if (Table_Sub_Waleta.getValueAt(i, 5) == null) {
                                Date_tutup.setDate(null);
                            } else {
                                Date_tutup.setDate(dateFormat.parse(Table_Sub_Waleta.getValueAt(i, 5).toString()));
                            }
                        } catch (ParseException ex) {
                            Logger.getLogger(JPanel_Sub_Waleta.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            });
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_Sub_Waleta.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void refreshTable() {
        try {
            Utility.db_sub.connect();
            DefaultTableModel model = (DefaultTableModel) Table_Sub_Waleta.getModel();
            model.setRowCount(0);
            sql = "SELECT `tb_sub_waleta`.`kode_sub`, `tb_sub_waleta`.`alamat`, `tanggal_berdiri`, `tanggal_tutup`, `id_pegawai`, `nama_pegawai` "
                    + "FROM `tb_sub_waleta` "
                    + "LEFT JOIN `tb_karyawan` ON `tb_sub_waleta`.`kode_sub` = `tb_karyawan`.`bagian` AND `tb_karyawan`.`jenis_pegawai` = 'KEPALA' "
                    + "WHERE `tb_sub_waleta`.`kode_sub` LIKE '%" + txt_search.getText() + "%' "
                    + "OR `tb_sub_waleta`.`alamat` LIKE '%" + txt_search.getText() + "%' "
                    + "OR `penanggungjawab` LIKE '%" + txt_search.getText() + "%'"
                    + "GROUP BY `tb_sub_waleta`.`kode_sub`"
                    + "ORDER BY `tb_sub_waleta`.`kode_sub`";
            rs = Utility.db_sub.getStatement().executeQuery(sql);
            Object[] row = new Object[6];
            while (rs.next()) {
                row[0] = rs.getString("kode_sub");
                row[1] = rs.getString("alamat");
                row[2] = rs.getString("id_pegawai");
                row[3] = rs.getString("nama_pegawai");
                row[4] = rs.getDate("tanggal_berdiri");
                row[5] = rs.getDate("tanggal_tutup");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_Sub_Waleta);
            int rowData = Table_Sub_Waleta.getRowCount();
            label_total_data.setText(Integer.toString(rowData));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_Sub_Waleta.class.getName()).log(Level.SEVERE, null, e);
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

        jPanel_Supplier = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        Table_Sub_Waleta = new javax.swing.JTable();
        jPanel_operation_supplier = new javax.swing.JPanel();
        button_insert = new javax.swing.JButton();
        button_update = new javax.swing.JButton();
        button_clear = new javax.swing.JButton();
        button_delete = new javax.swing.JButton();
        txt_kode_sub = new javax.swing.JTextField();
        label_supplier_1 = new javax.swing.JLabel();
        label_supplier_2 = new javax.swing.JLabel();
        label_supplier_3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txt_alamat = new javax.swing.JTextArea();
        label_supplier_4 = new javax.swing.JLabel();
        Date_berdiri = new com.toedter.calendar.JDateChooser();
        label_supplier_5 = new javax.swing.JLabel();
        Date_tutup = new com.toedter.calendar.JDateChooser();
        txt_nama_penanggungjawab = new javax.swing.JTextField();
        button_pick_penanggungjawab = new javax.swing.JButton();
        txt_id_penanggungjawab = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        label_total_data = new javax.swing.JLabel();
        txt_search = new javax.swing.JTextField();
        button_search = new javax.swing.JButton();
        button_export = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        jPanel_Supplier.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_Supplier.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Data Sub Waleta", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N
        jPanel_Supplier.setPreferredSize(new java.awt.Dimension(1366, 700));

        Table_Sub_Waleta.setAutoCreateRowSorter(true);
        Table_Sub_Waleta.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Table_Sub_Waleta.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode", "Alamat", "ID Penanggungjawab", "Nama Penanggungjawab", "Tgl Berdiri", "Tgl Tutup"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Table_Sub_Waleta.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(Table_Sub_Waleta);

        jPanel_operation_supplier.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_operation_supplier.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Operation", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Yu Gothic", 0, 12))); // NOI18N
        jPanel_operation_supplier.setName("aah"); // NOI18N

        button_insert.setBackground(new java.awt.Color(255, 255, 255));
        button_insert.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_insert.setText("insert");
        button_insert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_insertActionPerformed(evt);
            }
        });

        button_update.setBackground(new java.awt.Color(255, 255, 255));
        button_update.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_update.setText("Update");
        button_update.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_updateActionPerformed(evt);
            }
        });

        button_clear.setBackground(new java.awt.Color(255, 255, 255));
        button_clear.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_clear.setText("Clear Text");
        button_clear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_clearActionPerformed(evt);
            }
        });

        button_delete.setBackground(new java.awt.Color(255, 255, 255));
        button_delete.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_delete.setText("Delete");
        button_delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_deleteActionPerformed(evt);
            }
        });

        txt_kode_sub.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        label_supplier_1.setBackground(new java.awt.Color(255, 255, 255));
        label_supplier_1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_supplier_1.setText("Kode Sub Waleta :");

        label_supplier_2.setBackground(new java.awt.Color(255, 255, 255));
        label_supplier_2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_supplier_2.setText("Nama Penanggungjawab :");

        label_supplier_3.setBackground(new java.awt.Color(255, 255, 255));
        label_supplier_3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_supplier_3.setText("Alamat :");

        txt_alamat.setColumns(20);
        txt_alamat.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        txt_alamat.setLineWrap(true);
        txt_alamat.setRows(3);
        jScrollPane1.setViewportView(txt_alamat);

        label_supplier_4.setBackground(new java.awt.Color(255, 255, 255));
        label_supplier_4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_supplier_4.setText("Tanggal Berdiri :");

        Date_berdiri.setBackground(new java.awt.Color(255, 255, 255));
        Date_berdiri.setDateFormatString("dd MMMM yyyy");
        Date_berdiri.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        label_supplier_5.setBackground(new java.awt.Color(255, 255, 255));
        label_supplier_5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_supplier_5.setText("Tanggal Tutup :");

        Date_tutup.setBackground(new java.awt.Color(255, 255, 255));
        Date_tutup.setDateFormatString("dd MMMM yyyy");
        Date_tutup.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        txt_nama_penanggungjawab.setEditable(false);
        txt_nama_penanggungjawab.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        button_pick_penanggungjawab.setBackground(new java.awt.Color(255, 255, 255));
        button_pick_penanggungjawab.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_pick_penanggungjawab.setText("...");
        button_pick_penanggungjawab.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_pick_penanggungjawabActionPerformed(evt);
            }
        });

        txt_id_penanggungjawab.setEditable(false);
        txt_id_penanggungjawab.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        javax.swing.GroupLayout jPanel_operation_supplierLayout = new javax.swing.GroupLayout(jPanel_operation_supplier);
        jPanel_operation_supplier.setLayout(jPanel_operation_supplierLayout);
        jPanel_operation_supplierLayout.setHorizontalGroup(
            jPanel_operation_supplierLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_operation_supplierLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_operation_supplierLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel_operation_supplierLayout.createSequentialGroup()
                        .addComponent(button_update)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_insert)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_delete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_clear))
                    .addGroup(jPanel_operation_supplierLayout.createSequentialGroup()
                        .addGroup(jPanel_operation_supplierLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(label_supplier_4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(label_supplier_5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(label_supplier_1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(label_supplier_2, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                            .addComponent(label_supplier_3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_operation_supplierLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 364, Short.MAX_VALUE)
                            .addComponent(Date_tutup, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(Date_berdiri, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txt_kode_sub)
                            .addGroup(jPanel_operation_supplierLayout.createSequentialGroup()
                                .addComponent(txt_id_penanggungjawab)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_pick_penanggungjawab, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txt_nama_penanggungjawab))))
                .addContainerGap())
        );
        jPanel_operation_supplierLayout.setVerticalGroup(
            jPanel_operation_supplierLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_operation_supplierLayout.createSequentialGroup()
                .addGroup(jPanel_operation_supplierLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_kode_sub, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_supplier_1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_supplierLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_supplier_2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_pick_penanggungjawab, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_id_penanggungjawab, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_nama_penanggungjawab, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_supplierLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label_supplier_4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_berdiri, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_supplierLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label_supplier_5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_tutup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_supplierLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label_supplier_3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_supplierLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_update, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_insert, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_delete, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_clear, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel7.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jLabel7.setText("Total Data :");

        label_total_data.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        label_total_data.setText("TOTAL");

        txt_search.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_searchKeyPressed(evt);
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

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("Search :");

        javax.swing.GroupLayout jPanel_SupplierLayout = new javax.swing.GroupLayout(jPanel_Supplier);
        jPanel_Supplier.setLayout(jPanel_SupplierLayout);
        jPanel_SupplierLayout.setHorizontalGroup(
            jPanel_SupplierLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_SupplierLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_SupplierLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4)
                    .addGroup(jPanel_SupplierLayout.createSequentialGroup()
                        .addGroup(jPanel_SupplierLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_SupplierLayout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_search)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_export)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_data))
                            .addComponent(jPanel_operation_supplier, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 773, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel_SupplierLayout.setVerticalGroup(
            jPanel_SupplierLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_SupplierLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel_SupplierLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_search, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_search, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_data)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 374, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel_operation_supplier, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jPanel_Supplier, javax.swing.GroupLayout.DEFAULT_SIZE, 1364, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel_Supplier, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void button_insertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_insertActionPerformed
        // TODO add your handling code here:
        try {
            Boolean Check = true;
            Utility.db_sub.connect();
            Utility.db_sub.getConnection().setAutoCommit(false);
            sql = "SELECT `tb_sub_waleta`.`kode_sub` FROM `tb_sub_waleta` WHERE `kode_sub` = '" + txt_kode_sub.getText() + "' ";
            rs = Utility.db_sub.getStatement().executeQuery(sql);
            if ("".equals(txt_kode_sub.getText())) {
                JOptionPane.showMessageDialog(this, "Maaf kode Sub harus diisi !");
                txt_kode_sub.requestFocus();
                Check = false;
            } else if (txt_kode_sub.getText().length() != 5) {
                JOptionPane.showMessageDialog(this, "Maaf kode Sub harus 5 HURUF/ANGKA !");
                txt_kode_sub.requestFocus();
                Check = false;
            } else if (txt_id_penanggungjawab.getText() == null || txt_id_penanggungjawab.getText().equals("")) {
                JOptionPane.showMessageDialog(this, "Maaf penangungjawab tidak bisa kosong !");
                Check = false;
            } else if (Date_berdiri.getDate() == null) {
                JOptionPane.showMessageDialog(this, "Tanggal berdiri harus ada");
                Check = false;
            } else if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Kode Sub Waleta (" + txt_kode_sub.getText() + ") sudah terpakai, tidak boleh ada Kode Sub Waleta yang sama");
                Check = false;
            }

            if (Check) {
                String Query1 = "INSERT INTO `tb_sub_waleta` (`kode_sub`, `penanggungjawab`, `alamat`, `tanggal_berdiri`) "
                        + "VALUES ('" + txt_kode_sub.getText() + "', '" + txt_id_penanggungjawab.getText() + "', '" + txt_alamat.getText() + "', '" + dateFormat.format(Date_berdiri.getDate()) + "')";
                Utility.db_sub.getConnection().createStatement();
                Utility.db_sub.getStatement().executeUpdate(Query1);
                String Query2 = "UPDATE `tb_karyawan` SET `jenis_pegawai`='KEPALA' WHERE '" + txt_id_penanggungjawab.getText() + "'";
                Utility.db_sub.getConnection().createStatement();
                Utility.db_sub.getStatement().executeUpdate(Query2);
                Utility.db_sub.getConnection().commit();
                refreshTable();
                JOptionPane.showMessageDialog(this, "data insert Successfully");
                button_clear.doClick();
            }
        } catch (Exception e) {
            try {
                Utility.db_sub.getConnection().rollback();
            } catch (SQLException ex) {
                Logger.getLogger(JPanel_Sub_Waleta.class.getName()).log(Level.SEVERE, null, ex);
            }
            JOptionPane.showMessageDialog(this, "INPUT FAILED : " + e);
            Logger.getLogger(JPanel_Sub_Waleta.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            try {
                Utility.db_sub.getConnection().setAutoCommit(true);
            } catch (SQLException ex) {
                Logger.getLogger(JPanel_Sub_Waleta.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_button_insertActionPerformed

    private void button_updateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_updateActionPerformed
        // TODO add your handling code here:
        int j = Table_Sub_Waleta.getSelectedRow();
        if (j == -1) {
            JOptionPane.showMessageDialog(this, "Silahkan klik pada data di tabel yang akan di edit");
        } else {
            try {
                Boolean Check = true;
                Utility.db_sub.connect();
                Utility.db_sub.getConnection().setAutoCommit(false);
                if (txt_kode_sub.getText() == null || txt_kode_sub.getText().equals("")) {
                    JOptionPane.showMessageDialog(this, "Maaf kode Sub harus diisi !");
                    txt_kode_sub.requestFocus();
                    Check = false;
                } else if (txt_kode_sub.getText().length() != 5) {
                    JOptionPane.showMessageDialog(this, "Maaf kode Sub harus 5 HURUF/ANGKA !");
                    txt_kode_sub.requestFocus();
                    Check = false;
                } else if (Date_berdiri.getDate() == null) {
                    JOptionPane.showMessageDialog(this, "Tanggal berdiri harus ada");
                    Check = false;
                } else if (txt_id_penanggungjawab.getText() == null || txt_id_penanggungjawab.getText().equals("")) {
                    JOptionPane.showMessageDialog(this, "Maaf penangungjawab tidak bisa kosong !");
                    Check = false;
                } else if (!txt_kode_sub.getText().equals(Table_Sub_Waleta.getValueAt(j, 0).toString())) {
                    JOptionPane.showMessageDialog(this, "Sangat tidak disarankan untuk mengganti kode Sub, karena tidak akan otomatis mengubah semua kode LP dan tujuan reproses\n"
                            + "Harap menghubungi tim IT untuk melakukan edit kode SUB");
                    Check = false;
                }
                if (Check) {
                    String update_tgl_tutup = "`tanggal_tutup` = NULL,";
                    if (Date_tutup.getDate() != null) {
                        update_tgl_tutup = "`tanggal_tutup` = '" + dateFormat.format(Date_tutup.getDate()) + "', ";
                    }

                    String Query1 = "UPDATE `tb_sub_waleta` SET "
                            + "`penanggungjawab` = '" + txt_id_penanggungjawab.getText() + "', "
                            + "`alamat` = '" + txt_alamat.getText() + "', "
                            + update_tgl_tutup
                            + "`tanggal_berdiri` = '" + dateFormat.format(Date_berdiri.getDate()) + "' "
                            + "WHERE `kode_sub` = '" + Table_Sub_Waleta.getValueAt(j, 0).toString() + "'";
                    Utility.db_sub.getConnection().createStatement();
                    Utility.db_sub.getStatement().executeUpdate(Query1);

                    if (!txt_id_penanggungjawab.getText().equals(Table_Sub_Waleta.getValueAt(j, 2).toString())) {
                        String Query2 = "UPDATE `tb_karyawan` SET `jenis_pegawai`='KEPALA' WHERE '" + txt_id_penanggungjawab.getText() + "'";
                        Utility.db_sub.getConnection().createStatement();
                        Utility.db_sub.getStatement().executeUpdate(Query2);
                        String Query3 = "UPDATE `tb_karyawan` SET `jenis_pegawai`='KARYAWAN' WHERE '" + Table_Sub_Waleta.getValueAt(j, 2).toString() + "'";
                        Utility.db_sub.getConnection().createStatement();
                        Utility.db_sub.getStatement().executeUpdate(Query3);
                    }

                    Utility.db_sub.getConnection().commit();
                    refreshTable();
                    JOptionPane.showMessageDialog(this, "edit sukses");
                    button_clear.doClick();
                }
            } catch (Exception e) {
                try {
                    Utility.db_sub.getConnection().rollback();
                } catch (SQLException ex) {
                    Logger.getLogger(JPanel_Sub_Waleta.class.getName()).log(Level.SEVERE, null, ex);
                }
                JOptionPane.showMessageDialog(this, e);
                Logger.getLogger(JPanel_Sub_Waleta.class.getName()).log(Level.SEVERE, null, e);
            } finally {
                try {
                    Utility.db_sub.getConnection().setAutoCommit(true);
                } catch (SQLException ex) {
                    Logger.getLogger(JPanel_Sub_Waleta.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_button_updateActionPerformed

    private void button_clearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_clearActionPerformed
        // TODO add your handling code here:
        txt_kode_sub.setText(null);
        txt_nama_penanggungjawab.setText(null);
        txt_alamat.setText(null);
        Date_berdiri.setDate(null);
        Date_tutup.setDate(null);
    }//GEN-LAST:event_button_clearActionPerformed

    private void button_deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_deleteActionPerformed
        // TODO add your handling code here:
        int j = Table_Sub_Waleta.getSelectedRow();
        if (j == -1) {
            JOptionPane.showMessageDialog(this, "Please Select Row Data that you want to Delete !");
        } else {
            try {
                Utility.db_sub.connect();
                Utility.db_sub.getConnection().setAutoCommit(false);
                int dialogResult = JOptionPane.showConfirmDialog(this, "Yakin hapus data ini?", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    // delete code here
                    String Query = "DELETE FROM `tb_sub_waleta` WHERE `tb_sub_waleta`.`kode_sub` = '" + Table_Sub_Waleta.getValueAt(j, 0).toString() + "'";
                    Utility.db_sub.getConnection().createStatement();
                    Utility.db_sub.getStatement().executeUpdate(Query);
                    Utility.db_sub.getConnection().commit();
                    refreshTable();
                    JOptionPane.showMessageDialog(this, "delete sukses");
                    button_clear.doClick();
                }
            } catch (Exception e) {
                try {
                    Utility.db_sub.getConnection().rollback();
                } catch (SQLException ex) {
                    Logger.getLogger(JPanel_Sub_Waleta.class.getName()).log(Level.SEVERE, null, ex);
                }
                JOptionPane.showMessageDialog(this, e);
                Logger.getLogger(JPanel_Sub_Waleta.class.getName()).log(Level.SEVERE, null, e);
            } finally {
                try {
                    Utility.db_sub.getConnection().setAutoCommit(true);
                } catch (SQLException ex) {
                    Logger.getLogger(JPanel_Sub_Waleta.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_button_deleteActionPerformed

    private void txt_searchKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_searchKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_searchKeyPressed

    private void button_searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_searchActionPerformed
        // TODO add your handling code here:
        refreshTable();
    }//GEN-LAST:event_button_searchActionPerformed

    private void button_exportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_exportActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Table_Sub_Waleta.getModel();
        ExportToExcel.writeToExcel(model, jPanel_Supplier);
    }//GEN-LAST:event_button_exportActionPerformed

    private void button_pick_penanggungjawabActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_pick_penanggungjawabActionPerformed
        // TODO add your handling code here:
        JDialog_Browse_KaryawanSub dialog = new JDialog_Browse_KaryawanSub(new javax.swing.JFrame(), true, null);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);
        txt_nama_penanggungjawab.setText(dialog.getNama());
        txt_id_penanggungjawab.setText(dialog.getID());
    }//GEN-LAST:event_button_pick_penanggungjawabActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser Date_berdiri;
    private com.toedter.calendar.JDateChooser Date_tutup;
    private javax.swing.JTable Table_Sub_Waleta;
    private javax.swing.JButton button_clear;
    public javax.swing.JButton button_delete;
    private javax.swing.JButton button_export;
    public javax.swing.JButton button_insert;
    private javax.swing.JButton button_pick_penanggungjawab;
    private javax.swing.JButton button_search;
    public javax.swing.JButton button_update;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel_Supplier;
    private javax.swing.JPanel jPanel_operation_supplier;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel label_supplier_1;
    private javax.swing.JLabel label_supplier_2;
    private javax.swing.JLabel label_supplier_3;
    private javax.swing.JLabel label_supplier_4;
    private javax.swing.JLabel label_supplier_5;
    private javax.swing.JLabel label_total_data;
    private javax.swing.JTextArea txt_alamat;
    private javax.swing.JTextField txt_id_penanggungjawab;
    private javax.swing.JTextField txt_kode_sub;
    private javax.swing.JTextField txt_nama_penanggungjawab;
    private javax.swing.JTextField txt_search;
    // End of variables declaration//GEN-END:variables
}
