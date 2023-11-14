package waleta_system.BahanBaku;

import java.awt.HeadlessException;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;
import waleta_system.Class.ExportToExcel;

public class JPanel_Pembelian_Baku extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    ArrayList kode_Supplier;

    public JPanel_Pembelian_Baku() {
        initComponents();
    }

    public void init() {
        try {
            refreshTable();
            Table_pembelian.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent event) {
                    if (!event.getValueIsAdjusting() && Table_pembelian.getSelectedRow() != -1) {
                        int i = Table_pembelian.getSelectedRow();
                        if (i > -1) {
                            txt_no.setText(Table_pembelian.getValueAt(i, 0).toString());
                            ComboBox_supplier.setSelectedItem(Table_pembelian.getValueAt(i, 1).toString());
                            txt_grade.setText(Table_pembelian.getValueAt(i, 2).toString());
                            txt_berat.setText(Table_pembelian.getValueAt(i, 3).toString());
                            txt_harga.setText(Table_pembelian.getValueAt(i, 4).toString());
                            try {
                                Date_kirim.setDate(dateFormat.parse(Table_pembelian.getValueAt(i, 6).toString()));
                            } catch (ParseException | NullPointerException ex) {
//                                Logger.getLogger(JPanel_Pembelian_Baku.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            try {
                                Date_terima.setDate(dateFormat.parse(Table_pembelian.getValueAt(i, 7).toString()));
                            } catch (ParseException | NullPointerException ex) {
//                                Logger.getLogger(JPanel_Pembelian_Baku.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            txt_status.setText(Table_pembelian.getValueAt(i, 8).toString());
                        }
                    }
                }
            });
            kode_Supplier = new ArrayList();
            sql = "SELECT `kode_supplier`, `nama_supplier` FROM `tb_supplier` ORDER BY `nama_supplier` ASC";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                kode_Supplier.add(rs.getString("kode_Supplier"));
                ComboBox_supplier.addItem(rs.getString("nama_supplier"));
            }
        } catch (Exception ex) {
            Logger.getLogger(JPanel_Pembelian_Baku.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable() {
        try {
            DefaultTableModel model = (DefaultTableModel) Table_pembelian.getModel();
            model.setRowCount(0);
            String status_grading = ComboBox_status_grading.getSelectedItem().toString();
            if (ComboBox_status_grading.getSelectedItem().toString().equals("All")) {
                status_grading = "";
            }
            sql = "SELECT `no_nota`, `tb_supplier`.`nama_supplier`, `grade`, `berat`, `harga_gram`, `tgl_kirim`, `tgl_terima`, `status` "
                    + "FROM `tb_pembelian_bahan_baku`  LEFT JOIN `tb_supplier` ON `tb_pembelian_bahan_baku`.`kode_supplier` = `tb_supplier`.`kode_supplier`"
                    + "WHERE `nama_supplier` LIKE '%" + txt_search_supplier.getText() + "%' AND `status` LIKE '%" + status_grading + "%'ORDER BY `tgl_kirim` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[9];
            while (rs.next()) {
                row[0] = rs.getInt("no_nota");
                row[1] = rs.getString("nama_supplier");
                row[2] = rs.getString("grade");
                row[3] = rs.getInt("berat");
                row[4] = rs.getInt("harga_gram");
                row[5] = rs.getInt("berat") * rs.getInt("harga_gram");
                row[6] = dateFormat.format(rs.getDate("tgl_kirim"));
                row[7] = rs.getDate("tgl_terima");
                row[8] = rs.getString("status");
                model.addRow(row);
            }

            ColumnsAutoSizer.sizeColumnsToFit(Table_pembelian);
            int rowData = Table_pembelian.getRowCount();
            label_total_data_supplier.setText(Integer.toString(rowData));
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_Pembelian_Baku.class.getName()).log(Level.SEVERE, null, ex);
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

        jPanel_Pembelian_baku = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        Table_pembelian = new javax.swing.JTable();
        jPanel_operation_supplier = new javax.swing.JPanel();
        button_insert = new javax.swing.JButton();
        button_update = new javax.swing.JButton();
        button_clear = new javax.swing.JButton();
        button_delete = new javax.swing.JButton();
        txt_harga = new javax.swing.JTextField();
        txt_no = new javax.swing.JTextField();
        txt_grade = new javax.swing.JTextField();
        label_supplier_1 = new javax.swing.JLabel();
        label_supplier_3 = new javax.swing.JLabel();
        label_supplier_4 = new javax.swing.JLabel();
        label_supplier_5 = new javax.swing.JLabel();
        ComboBox_supplier = new javax.swing.JComboBox<>();
        label_supplier_6 = new javax.swing.JLabel();
        label_supplier_7 = new javax.swing.JLabel();
        Date_kirim = new com.toedter.calendar.JDateChooser();
        Date_terima = new com.toedter.calendar.JDateChooser();
        label_supplier_8 = new javax.swing.JLabel();
        txt_berat = new javax.swing.JTextField();
        label_supplier_9 = new javax.swing.JLabel();
        txt_status = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        label_total_data_supplier = new javax.swing.JLabel();
        txt_search_supplier = new javax.swing.JTextField();
        button_search_supplier = new javax.swing.JButton();
        button_export_supplier = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        button_selesai_grading = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        ComboBox_status_grading = new javax.swing.JComboBox<>();

        jPanel_Pembelian_baku.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_Pembelian_baku.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Data Pembelian Bahan Baku", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N
        jPanel_Pembelian_baku.setPreferredSize(new java.awt.Dimension(1366, 700));

        Table_pembelian.setAutoCreateRowSorter(true);
        Table_pembelian.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "Supplier", "Grade", "Berat Gr", "Harga/Gr", "Total Harga", "Tgl Kirim", "Tgl Terima", "Status Grading"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Double.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Table_pembelian.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(Table_pembelian);

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

        txt_harga.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_harga.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_hargaKeyTyped(evt);
            }
        });

        txt_no.setEditable(false);
        txt_no.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        txt_grade.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        label_supplier_1.setBackground(new java.awt.Color(255, 255, 255));
        label_supplier_1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_supplier_1.setText("No :");

        label_supplier_3.setBackground(new java.awt.Color(255, 255, 255));
        label_supplier_3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_supplier_3.setText("Grade :");

        label_supplier_4.setBackground(new java.awt.Color(255, 255, 255));
        label_supplier_4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_supplier_4.setText("Harga :");

        label_supplier_5.setBackground(new java.awt.Color(255, 255, 255));
        label_supplier_5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_supplier_5.setText("Nama Supplier :");

        ComboBox_supplier.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        label_supplier_6.setBackground(new java.awt.Color(255, 255, 255));
        label_supplier_6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_supplier_6.setText("Tanggal Kirim :");

        label_supplier_7.setBackground(new java.awt.Color(255, 255, 255));
        label_supplier_7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_supplier_7.setText("Tanggal Terima :");

        Date_kirim.setBackground(new java.awt.Color(255, 255, 255));
        Date_kirim.setDateFormatString("dd MMMM yyyy");

        Date_terima.setBackground(new java.awt.Color(255, 255, 255));
        Date_terima.setDateFormatString("dd MMMM yyyy");

        label_supplier_8.setBackground(new java.awt.Color(255, 255, 255));
        label_supplier_8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_supplier_8.setText("Berat :");

        txt_berat.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_berat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_beratKeyTyped(evt);
            }
        });

        label_supplier_9.setBackground(new java.awt.Color(255, 255, 255));
        label_supplier_9.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_supplier_9.setText("Status :");

        txt_status.setEditable(false);
        txt_status.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_status.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_statusKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout jPanel_operation_supplierLayout = new javax.swing.GroupLayout(jPanel_operation_supplier);
        jPanel_operation_supplier.setLayout(jPanel_operation_supplierLayout);
        jPanel_operation_supplierLayout.setHorizontalGroup(
            jPanel_operation_supplierLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_operation_supplierLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_operation_supplierLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label_supplier_3, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_supplier_4, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_supplier_1, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_supplier_5, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_supplier_6, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_supplier_7, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_supplier_8, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_supplier_9, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_supplierLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txt_grade)
                    .addComponent(txt_harga, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txt_no)
                    .addComponent(ComboBox_supplier, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Date_kirim, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Date_terima, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txt_berat)
                    .addComponent(txt_status, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_operation_supplierLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(button_update)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_insert)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_delete)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_clear)
                .addGap(10, 10, 10))
        );
        jPanel_operation_supplierLayout.setVerticalGroup(
            jPanel_operation_supplierLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_operation_supplierLayout.createSequentialGroup()
                .addGroup(jPanel_operation_supplierLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_no, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_supplier_1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_supplierLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_supplier_5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_supplier, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_supplierLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_supplier_3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_grade, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_supplierLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_supplier_8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_berat, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_supplierLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_harga, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_supplier_4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_supplierLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label_supplier_6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_kirim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_supplierLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_operation_supplierLayout.createSequentialGroup()
                        .addComponent(label_supplier_7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_operation_supplierLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_supplier_9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_status, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(Date_terima, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel_operation_supplierLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_update, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_insert, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_delete, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_clear, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10))
        );

        jLabel7.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jLabel7.setText("Total Data :");

        label_total_data_supplier.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        label_total_data_supplier.setText("TOTAL");

        txt_search_supplier.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_supplier.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_supplierKeyPressed(evt);
            }
        });

        button_search_supplier.setBackground(new java.awt.Color(255, 255, 255));
        button_search_supplier.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search_supplier.setText("Search");
        button_search_supplier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_search_supplierActionPerformed(evt);
            }
        });

        button_export_supplier.setBackground(new java.awt.Color(255, 255, 255));
        button_export_supplier.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_supplier.setText("Export To Excel");
        button_export_supplier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_supplierActionPerformed(evt);
            }
        });

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("Search By Nama Supplier :");

        button_selesai_grading.setBackground(new java.awt.Color(255, 255, 255));
        button_selesai_grading.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_selesai_grading.setText("Selesai Grading");
        button_selesai_grading.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_selesai_gradingActionPerformed(evt);
            }
        });

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("Status Grading :");

        ComboBox_status_grading.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_status_grading.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "PROSES", "SELESAI" }));

        javax.swing.GroupLayout jPanel_Pembelian_bakuLayout = new javax.swing.GroupLayout(jPanel_Pembelian_baku);
        jPanel_Pembelian_baku.setLayout(jPanel_Pembelian_bakuLayout);
        jPanel_Pembelian_bakuLayout.setHorizontalGroup(
            jPanel_Pembelian_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Pembelian_bakuLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_Pembelian_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_Pembelian_bakuLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_supplier, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_status_grading, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_search_supplier)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_selesai_grading)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_export_supplier))
                    .addGroup(jPanel_Pembelian_bakuLayout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_data_supplier)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel_Pembelian_bakuLayout.createSequentialGroup()
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 1010, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel_operation_supplier, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel_Pembelian_bakuLayout.setVerticalGroup(
            jPanel_Pembelian_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Pembelian_bakuLayout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addGroup(jPanel_Pembelian_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_search_supplier, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_search_supplier, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export_supplier)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_selesai_grading, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_status_grading, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_Pembelian_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_Pembelian_bakuLayout.createSequentialGroup()
                        .addComponent(jPanel_operation_supplier, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_Pembelian_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_total_data_supplier)
                    .addComponent(jLabel7))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jPanel_Pembelian_baku, javax.swing.GroupLayout.DEFAULT_SIZE, 1364, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel_Pembelian_baku, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void button_insertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_insertActionPerformed
        // TODO add your handling code here:
        Boolean Check = true;
        try {
            if (txt_grade.getText() == null || txt_grade.getText().equals("")
                    || txt_berat.getText() == null || txt_berat.getText().equals("")
                    || txt_harga.getText() == null || txt_harga.getText().equals("")
                    || Date_kirim.getDate() == null) {
                JOptionPane.showMessageDialog(this, "Silahkan lengkapi data diatas !");
                Check = false;
            }

            if (Check) {
                String tgl_terima = "NULL", status = "OTW";
                if (Date_terima.getDate() != null) {
                    tgl_terima = "'" + dateFormat.format(Date_terima.getDate()) + "'";
                    status = "PROSES";
                }
                sql = "INSERT INTO `tb_pembelian_bahan_baku` (`kode_supplier`, `grade`, `berat`, `harga_gram`, `tgl_kirim`, `tgl_terima`, `status`) "
                        + "VALUES ('" + kode_Supplier.get(ComboBox_supplier.getSelectedIndex()) + "', '" + txt_grade.getText() + "', '" + txt_berat.getText() + "', '" + txt_harga.getText() + "', '" + dateFormat.format(Date_kirim.getDate()) + "', " + tgl_terima + ", '" + status + "')";
                Utility.db.getConnection().createStatement();
                if ((Utility.db.getStatement().executeUpdate(sql)) == 1) {
                    JOptionPane.showMessageDialog(this, "data insert Successfully !");
                    refreshTable();
                } else {
                    JOptionPane.showMessageDialog(this, "insert failed !");
                }
                button_clear.doClick();
            }
        } catch (HeadlessException | SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Pembelian_Baku.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_insertActionPerformed

    private void button_updateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_updateActionPerformed
        // TODO add your handling code here:
        int j = Table_pembelian.getSelectedRow();
        Boolean Check = true;
        TableModel model = Table_pembelian.getModel();
        try {
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Please Select Row Data that you want to Update !");
            } else {
                if (txt_grade.getText() == null || txt_grade.getText().equals("")
                        || txt_berat.getText() == null || txt_berat.getText().equals("")
                        || txt_harga.getText() == null || txt_harga.getText().equals("")
                        || Date_kirim.getDate() == null) {
                    JOptionPane.showMessageDialog(this, "Silahkan lengkapi data diatas !");
                    Check = false;
                }
                if (Check) {
                    String tgl_terima = "", status = "";
                    if (Date_terima.getDate() != null) {
                        tgl_terima = "`tgl_terima` = '" + dateFormat.format(Date_terima.getDate()) + "', ";
                        if (txt_status.getText().equals("OTW")) {
                            status = "`status` = 'PROSES', ";
                        }
                    }
                    sql = "UPDATE `tb_pembelian_bahan_baku` SET "
                            + "`kode_supplier` = '" + kode_Supplier.get(ComboBox_supplier.getSelectedIndex()) + "', "
                            + "`grade` = '" + txt_grade.getText() + "', "
                            + "`berat` = '" + txt_berat.getText() + "', "
                            + "`harga_gram` = '" + txt_harga.getText() + "', "
                            + tgl_terima + status
                            + "`tgl_kirim` = '" + dateFormat.format(Date_kirim.getDate()) + "' "
                            + "WHERE `no_nota` = '" + txt_no.getText() + "'";
                    Utility.db.getConnection().createStatement();
                    if ((Utility.db.getStatement().executeUpdate(sql)) == 1) {
                        JOptionPane.showMessageDialog(this, "data Edit Successfully !");
                        refreshTable();
                    } else {
                        JOptionPane.showMessageDialog(this, "Edit failed !");
                    }
                    button_clear.doClick();
                }
            }
        } catch (HeadlessException | SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Pembelian_Baku.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_updateActionPerformed

    private void button_clearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_clearActionPerformed
        // TODO add your handling code here:
        txt_no.setText(null);
        ComboBox_supplier.setSelectedIndex(0);
        txt_grade.setText(null);
        txt_berat.setText(null);
        txt_harga.setText(null);
        Date_kirim.setDate(null);
        Date_terima.setDate(null);
    }//GEN-LAST:event_button_clearActionPerformed

    private void button_deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_deleteActionPerformed
        // TODO add your handling code here:
        try {
            int j = Table_pembelian.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Please Select Row Data that you want to Delete !");
            } else {
                int dialogResult = JOptionPane.showConfirmDialog(this, "Yakin hapus data ini?", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    // delete code here
                    sql = "DELETE FROM `tb_pembelian_bahan_baku` WHERE `tb_pembelian_bahan_baku`.`no_nota` = '" + txt_no.getText() + "'";
                    Utility.db.getConnection().createStatement();
                    if ((Utility.db.getStatement().executeUpdate(sql)) == 1) {
                        JOptionPane.showMessageDialog(this, "data detele Successfully !");
                        refreshTable();
                    } else {
                        JOptionPane.showMessageDialog(this, "detele failed !");
                    }
                    button_clear.doClick();
                }
            }
        } catch (HeadlessException | SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Pembelian_Baku.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_deleteActionPerformed

    private void txt_search_supplierKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_supplierKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_search_supplierKeyPressed

    private void button_search_supplierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_supplierActionPerformed
        // TODO add your handling code here:
        refreshTable();
    }//GEN-LAST:event_button_search_supplierActionPerformed

    private void button_export_supplierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_supplierActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Table_pembelian.getModel();
        ExportToExcel.writeToExcel(model, this.jPanel_Pembelian_baku);
    }//GEN-LAST:event_button_export_supplierActionPerformed

    private void txt_beratKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_beratKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_beratKeyTyped

    private void txt_hargaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_hargaKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_hargaKeyTyped

    private void button_selesai_gradingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_selesai_gradingActionPerformed
        // TODO add your handling code here:
        int i = Table_pembelian.getSelectedRow();
        if (i == -1) {
            JOptionPane.showMessageDialog(this, "Silahkan pilih data !");
        } else {
            try {
                String no_nota = Table_pembelian.getValueAt(i, 0).toString();
                String Query = "UPDATE `tb_pembelian_bahan_baku` SET `status`='SELESAI' WHERE `no_nota` = '" + no_nota + "'";
                Utility.db.getConnection().createStatement();
                if ((Utility.db.getStatement().executeUpdate(Query)) > 0) {
                    JOptionPane.showMessageDialog(this, no_nota + " Sudah selesai proses grading !");
                    refreshTable();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex);
                Logger.getLogger(JPanel_Pembelian_Baku.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_button_selesai_gradingActionPerformed

    private void txt_statusKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_statusKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_statusKeyTyped


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_status_grading;
    private javax.swing.JComboBox<String> ComboBox_supplier;
    private com.toedter.calendar.JDateChooser Date_kirim;
    private com.toedter.calendar.JDateChooser Date_terima;
    private javax.swing.JTable Table_pembelian;
    private javax.swing.JButton button_clear;
    public javax.swing.JButton button_delete;
    private javax.swing.JButton button_export_supplier;
    public javax.swing.JButton button_insert;
    private javax.swing.JButton button_search_supplier;
    private javax.swing.JButton button_selesai_grading;
    public javax.swing.JButton button_update;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel_Pembelian_baku;
    private javax.swing.JPanel jPanel_operation_supplier;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel label_supplier_1;
    private javax.swing.JLabel label_supplier_3;
    private javax.swing.JLabel label_supplier_4;
    private javax.swing.JLabel label_supplier_5;
    private javax.swing.JLabel label_supplier_6;
    private javax.swing.JLabel label_supplier_7;
    private javax.swing.JLabel label_supplier_8;
    private javax.swing.JLabel label_supplier_9;
    private javax.swing.JLabel label_total_data_supplier;
    private javax.swing.JTextField txt_berat;
    private javax.swing.JTextField txt_grade;
    private javax.swing.JTextField txt_harga;
    private javax.swing.JTextField txt_no;
    private javax.swing.JTextField txt_search_supplier;
    private javax.swing.JTextField txt_status;
    // End of variables declaration//GEN-END:variables
}
