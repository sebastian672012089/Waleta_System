package waleta_system.Finance;

import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.ExportToExcel;
import waleta_system.Class.Utility;

public class JPanel_Aset_UnitAset extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public JPanel_Aset_UnitAset() {
        initComponents();
    }

    public void init() {
        refreshTable();
        table_unit_aset.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && table_unit_aset.getSelectedRow() != -1) {
                    int i = table_unit_aset.getSelectedRow();
                }
            }
        });
    }

    public void refreshTable() {
        try {
            DefaultTableModel model = (DefaultTableModel) table_unit_aset.getModel();
            model.setRowCount(0);
            String printed = "";
            if (ComboBox_printed.getSelectedIndex() == 1) {
                printed = "AND `printed` IS NOT NULL \n";
            } else if (ComboBox_printed.getSelectedIndex() == 2) {
                printed = "AND `printed` IS NULL \n";
            }
            String keterangan = "";
            if (txt_search_keterangan.getText() != null && !txt_search_keterangan.getText().equals("")) {
                keterangan = "AND `keterangan_stok_aset` LIKE '%" + txt_search_keterangan.getText() + "%' \n";
            }
            String kondisi = "";
            if (ComboBox_Kondisi.getSelectedIndex() != 0) {
                kondisi = "AND `kondisi` = '" + ComboBox_Kondisi.getSelectedItem().toString() + "' \n";
            }
            String tgl_datang = "";
            if (Date1.getDate() != null && Date2.getDate() != null) {
                tgl_datang = "AND `tb_aset_nota_detail`.`tgl_datang` BETWEEN '" + dateFormat.format(Date1.getDate()) + "' AND '" + dateFormat.format(Date1.getDate()) + "' \n";
            }
            sql = "SELECT `kode_unit`, `tb_aset_unit`.`kode_nota_detail`, `kondisi`, `printed`, `keterangan_stok_aset`, \n"
                    + "`tb_aset_master`.`nama_aset`, `tb_aset_master`.`merk_aset`, `tb_aset_master`.`kategori_aset`, `tb_aset_master`.`depresiasi_aset`, DATEDIFF(CURRENT_DATE, `tgl_datang`)/30 AS 'umur', \n"
                    + "`tb_aset_nota_detail`.`tgl_datang`, `tb_aset_nota_detail`.`harga_satuan`, `tb_aset_nota_detail`.`departemen`, `spesifikasi_aset`,\n"
                    + "`tb_aset_nota_pembelian`.`kode_nota`, `tb_aset_nota_pembelian`.`no_voucher_keuangan`, `tb_aset_nota_pembelian`.`tanggal_debit`, `tb_aset_nota_pembelian`.`tanggal_nota`, `tb_aset_nota_pembelian`.`supplier_nota`\n"
                    + "FROM `tb_aset_unit` \n"
                    + "LEFT JOIN `tb_aset_nota_detail` ON `tb_aset_unit`.`kode_nota_detail` = `tb_aset_nota_detail`.`kode_nota_detail`\n"
                    + "LEFT JOIN `tb_aset_master` ON `tb_aset_nota_detail`.`kode_aset` = `tb_aset_master`.`kode_aset`\n"
                    + "LEFT JOIN `tb_aset_nota_pembelian` ON `tb_aset_nota_detail`.`kode_nota` = `tb_aset_nota_pembelian`.`kode_nota`\n"
                    + "WHERE "
                    + "`tb_aset_unit`.`kode_unit` LIKE '%" + txt_search_kodeUnit.getText() + "%' \n"
                    + "AND `tb_aset_master`.`nama_aset` LIKE '%" + txt_search_NamaAset.getText() + "%' \n"
                    + "AND `tb_aset_master`.`merk_aset` LIKE '%" + txt_search_merk.getText() + "%' \n"
                    + "AND `spesifikasi_aset` LIKE '%" + txt_search_spesifikasi.getText() + "%' \n"
                    + keterangan
                    + tgl_datang
                    + printed
                    + kondisi;
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[20];
            while (rs.next()) {
                row[0] = rs.getString("kode_unit");
                row[1] = rs.getString("nama_aset");
                row[2] = rs.getString("merk_aset");
                row[3] = rs.getString("spesifikasi_aset");
                row[4] = rs.getString("kategori_aset");
                row[5] = rs.getString("kondisi");
                row[6] = rs.getString("printed");
                row[7] = rs.getString("keterangan_stok_aset");
                row[8] = rs.getString("departemen");
                row[9] = rs.getInt("harga_satuan");
                row[10] = rs.getDate("tgl_datang");
                row[11] = Math.round(rs.getFloat("umur") * 100f) / 100f;//umur
                row[12] = rs.getInt("depresiasi_aset");
                row[13] = rs.getString("kode_nota");
                row[14] = rs.getDate("tanggal_nota");
                row[15] = rs.getString("supplier_nota");
                row[16] = rs.getString("kode_nota_detail");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(table_unit_aset);
            label_total_data.setText(Integer.toString(table_unit_aset.getRowCount()));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Aset_UnitAset.class.getName()).log(Level.SEVERE, null, ex);
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
        table_unit_aset = new javax.swing.JTable();
        button_new_stok_aset = new javax.swing.JButton();
        button_edit_kondisi_aset = new javax.swing.JButton();
        button_delete_stok_aset = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        txt_search_NamaAset = new javax.swing.JTextField();
        button_search_stok_aset = new javax.swing.JButton();
        button_export_stok_aset = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        button_print_label_1 = new javax.swing.JButton();
        button_print_label_All = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        label_total_data = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        txt_search_merk = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        txt_search_spesifikasi = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        Date1 = new com.toedter.calendar.JDateChooser();
        Date2 = new com.toedter.calendar.JDateChooser();
        jLabel14 = new javax.swing.JLabel();
        txt_search_kodeUnit = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        ComboBox_printed = new javax.swing.JComboBox<>();
        jLabel17 = new javax.swing.JLabel();
        ComboBox_Kondisi = new javax.swing.JComboBox<>();
        jLabel18 = new javax.swing.JLabel();
        txt_search_keterangan = new javax.swing.JTextField();

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        table_unit_aset.setAutoCreateRowSorter(true);
        table_unit_aset.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_unit_aset.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode Unit", "Nama Aset", "Merk", "Spesifikasi", "Kategori", "Kondisi", "Printed", "Keterangan Unit", "Departemen", "Harga (Rp.)", "Tgl Datang", "Umur (Bulan)", "Depresiasi (Bulan)", "Kode Nota", "Tgl Nota", "Supplier", "Kode Barang Nota"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Object.class, java.lang.Float.class, java.lang.Integer.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table_unit_aset.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(table_unit_aset);

        button_new_stok_aset.setBackground(new java.awt.Color(255, 255, 255));
        button_new_stok_aset.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_new_stok_aset.setText("New");
        button_new_stok_aset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_new_stok_asetActionPerformed(evt);
            }
        });

        button_edit_kondisi_aset.setBackground(new java.awt.Color(255, 255, 255));
        button_edit_kondisi_aset.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_edit_kondisi_aset.setText("Edit");
        button_edit_kondisi_aset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_edit_kondisi_asetActionPerformed(evt);
            }
        });

        button_delete_stok_aset.setBackground(new java.awt.Color(255, 255, 255));
        button_delete_stok_aset.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_delete_stok_aset.setText("Delete");
        button_delete_stok_aset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_delete_stok_asetActionPerformed(evt);
            }
        });

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel9.setText("Nama Aset :");

        txt_search_NamaAset.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_search_NamaAset.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_NamaAsetKeyPressed(evt);
            }
        });

        button_search_stok_aset.setBackground(new java.awt.Color(255, 255, 255));
        button_search_stok_aset.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_search_stok_aset.setText("Search");
        button_search_stok_aset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_search_stok_asetActionPerformed(evt);
            }
        });

        button_export_stok_aset.setBackground(new java.awt.Color(255, 255, 255));
        button_export_stok_aset.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_export_stok_aset.setText("Export To Excel");
        button_export_stok_aset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_stok_asetActionPerformed(evt);
            }
        });

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel16.setText("Data Stok Aset Waleta");

        button_print_label_1.setBackground(new java.awt.Color(255, 255, 255));
        button_print_label_1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_print_label_1.setText("Print Label 1");
        button_print_label_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_print_label_1ActionPerformed(evt);
            }
        });

        button_print_label_All.setBackground(new java.awt.Color(255, 255, 255));
        button_print_label_All.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_print_label_All.setText("Print Label ALL");
        button_print_label_All.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_print_label_AllActionPerformed(evt);
            }
        });

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel10.setText("Total Data :");

        label_total_data.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_data.setText("0");

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel11.setText("Merk :");

        txt_search_merk.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_search_merk.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_merkKeyPressed(evt);
            }
        });

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel12.setText("Tanggal Datang :");

        txt_search_spesifikasi.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_search_spesifikasi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_spesifikasiKeyPressed(evt);
            }
        });

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel13.setText("Spesifikasi :");

        Date1.setBackground(new java.awt.Color(255, 255, 255));
        Date1.setDateFormatString("dd MMM yyyy");
        Date1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        Date2.setBackground(new java.awt.Color(255, 255, 255));
        Date2.setDateFormatString("dd MMM yyyy");
        Date2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel14.setText("Kode Unit :");

        txt_search_kodeUnit.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_search_kodeUnit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_kodeUnitKeyPressed(evt);
            }
        });

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel15.setText("Printed :");

        ComboBox_printed.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        ComboBox_printed.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Sudah diprint", "Belum diprint" }));

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel17.setText("Kondisi :");

        ComboBox_Kondisi.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        ComboBox_Kondisi.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Baik", "Tidak ditemukan", "Diluar Waleta", "Dalam Perbaikan", "Tidak dapat diperbaiki", "Dibuang" }));

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel18.setText("Keterangan :");

        txt_search_keterangan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_search_keterangan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_keteranganKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1346, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel16)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(button_new_stok_aset)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_edit_kondisi_aset)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_delete_stok_aset)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_print_label_1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_print_label_All)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_export_stok_aset)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_data))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_printed, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel17)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_Kondisi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_search_stok_aset))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_kodeUnit, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_NamaAset, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_merk, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_spesifikasi, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel18)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_keterangan, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
                    .addComponent(txt_search_NamaAset, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_spesifikasi, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_merk, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_kodeUnit, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_keterangan, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ComboBox_printed, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ComboBox_Kondisi, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(button_new_stok_aset, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_edit_kondisi_aset, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_delete_stok_aset, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_export_stok_aset, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_print_label_1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_print_label_All, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_data, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 566, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Date2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Date1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_search_stok_aset, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
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

    private void button_new_stok_asetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_new_stok_asetActionPerformed
        // TODO add your handling code here:
        JDialog_Aset_LabelBarang_New dialog = new JDialog_Aset_LabelBarang_New(new javax.swing.JFrame(), true);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);
        refreshTable();
    }//GEN-LAST:event_button_new_stok_asetActionPerformed

    private void button_edit_kondisi_asetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_edit_kondisi_asetActionPerformed
        // TODO add your handling code here:
        try {
            int j = table_unit_aset.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih data yang mau diedit !");
            } else {
                String kode_unit = table_unit_aset.getValueAt(j, 0).toString();
                String nama_aset = table_unit_aset.getValueAt(j, 1).toString();
                String merk = table_unit_aset.getValueAt(j, 2).toString();
                String kondisi = table_unit_aset.getValueAt(j, 4).toString();
                String keterangan = (table_unit_aset.getValueAt(j, 6) == null) ? "" : table_unit_aset.getValueAt(j, 6).toString();
                JDialog_Aset_LabelBarang_Edit dialog = new JDialog_Aset_LabelBarang_Edit(new javax.swing.JFrame(), true, kode_unit, nama_aset, merk, kondisi, keterangan);
                dialog.pack();
                dialog.setLocationRelativeTo(this);
                dialog.setVisible(true);
                dialog.setEnabled(true);
                refreshTable();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Aset_UnitAset.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_edit_kondisi_asetActionPerformed

    private void button_delete_stok_asetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_delete_stok_asetActionPerformed
        // TODO add your handling code here:
        try {
            int j = table_unit_aset.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih data yang mau dihapus !");
            } else {
                int dialogResult = JOptionPane.showConfirmDialog(this, "Apakah yakin ingin menghapus data ini?", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    // delete code here
                    String Query = "DELETE FROM `tb_aset_unit` WHERE `kode_unit` = '" + table_unit_aset.getValueAt(j, 0).toString() + "'";
                    Utility.db.getConnection().createStatement();
                    if ((Utility.db.getStatement().executeUpdate(Query)) == 1) {
                        JOptionPane.showMessageDialog(this, "Data berhasil dihapus!");
                        refreshTable();
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Aset_UnitAset.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_delete_stok_asetActionPerformed

    private void button_search_stok_asetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_stok_asetActionPerformed
        // TODO add your handling code here:
        refreshTable();
    }//GEN-LAST:event_button_search_stok_asetActionPerformed

    private void button_export_stok_asetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_stok_asetActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) table_unit_aset.getModel();
        ExportToExcel.writeToExcel(model, jPanel1);
    }//GEN-LAST:event_button_export_stok_asetActionPerformed

    private void txt_search_NamaAsetKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_NamaAsetKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_search_NamaAsetKeyPressed

    private void button_print_label_1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_print_label_1ActionPerformed
        // TODO add your handling code here:
        int x = table_unit_aset.getSelectedRow();
        if (x < 0) {
            JOptionPane.showMessageDialog(this, "Pilih salah satu data LP yang akan di setor !");
        } else {
            try {
                String kode = table_unit_aset.getValueAt(x, 0).toString();
                String Query = "SELECT `kode_unit`, `tb_aset_master`.`nama_aset`, `merk_aset`, `tgl_datang`, `spesifikasi_aset` \n"
                        + "FROM `tb_aset_unit` \n"
                        + "LEFT JOIN `tb_aset_nota_detail` ON `tb_aset_unit`.`kode_nota_detail` = `tb_aset_nota_detail`.`kode_nota_detail`\n"
                        + "LEFT JOIN `tb_aset_master` ON `tb_aset_nota_detail`.`kode_aset` = `tb_aset_master`.`kode_aset`\n"
                        + "WHERE `kode_unit` = '" + kode + "' ";
                JRDesignQuery newQuery = new JRDesignQuery();
                newQuery.setText(Query);
                JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Label_Aset.jrxml");
                JASP_DESIGN.setQuery(newQuery);
                JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
                JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, null, Utility.db.getConnection());
                JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)

                String Query_update = "UPDATE `tb_aset_unit` SET `printed`=NOW() WHERE `kode_unit` IN ('" + kode + "')";
                Utility.db.getStatement().executeUpdate(Query_update);
                refreshTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex);
                Logger.getLogger(JPanel_Aset_UnitAset.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_button_print_label_1ActionPerformed

    private void button_print_label_AllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_print_label_AllActionPerformed
        // TODO add your handling code here:
        int x = table_unit_aset.getSelectedRow();
        if (table_unit_aset.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Tidak ada data pada tabel !");
        } else {
            try {
                String kode = "";
                for (int i = 0; i < table_unit_aset.getRowCount(); i++) {
                    if (i != 0) {
                        kode = kode + ", ";
                    }
                    kode = kode + "'" + table_unit_aset.getValueAt(i, 0).toString() + "'";
                }
                String Query = "SELECT `kode_unit`, `tb_aset_master`.`nama_aset`, `merk_aset`, `tgl_datang`, `spesifikasi_aset` \n"
                        + "FROM `tb_aset_unit` \n"
                        + "LEFT JOIN `tb_aset_nota_detail` ON `tb_aset_unit`.`kode_nota_detail` = `tb_aset_nota_detail`.`kode_nota_detail`\n"
                        + "LEFT JOIN `tb_aset_master` ON `tb_aset_nota_detail`.`kode_aset` = `tb_aset_master`.`kode_aset`\n"
                        + "WHERE `kode_unit` IN (" + kode + ") ";
                JRDesignQuery newQuery = new JRDesignQuery();
                newQuery.setText(Query);
                JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Label_Aset.jrxml");
                JASP_DESIGN.setQuery(newQuery);
                JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
                JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, null, Utility.db.getConnection());
                JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)

                String Query_update = "UPDATE `tb_aset_unit` SET `printed`=NOW() WHERE `kode_unit` IN (" + kode + ")";
                Utility.db.getStatement().executeUpdate(Query_update);
                refreshTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex);
                Logger.getLogger(JPanel_Aset_UnitAset.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_button_print_label_AllActionPerformed

    private void txt_search_merkKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_merkKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_search_merkKeyPressed

    private void txt_search_spesifikasiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_spesifikasiKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_search_spesifikasiKeyPressed

    private void txt_search_kodeUnitKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_kodeUnitKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_search_kodeUnitKeyPressed

    private void txt_search_keteranganKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_keteranganKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_search_keteranganKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_Kondisi;
    private javax.swing.JComboBox<String> ComboBox_printed;
    private com.toedter.calendar.JDateChooser Date1;
    private com.toedter.calendar.JDateChooser Date2;
    public javax.swing.JButton button_delete_stok_aset;
    public javax.swing.JButton button_edit_kondisi_aset;
    private javax.swing.JButton button_export_stok_aset;
    public javax.swing.JButton button_new_stok_aset;
    public javax.swing.JButton button_print_label_1;
    public javax.swing.JButton button_print_label_All;
    private javax.swing.JButton button_search_stok_aset;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel label_total_data;
    public static javax.swing.JTable table_unit_aset;
    private javax.swing.JTextField txt_search_NamaAset;
    private javax.swing.JTextField txt_search_keterangan;
    private javax.swing.JTextField txt_search_kodeUnit;
    private javax.swing.JTextField txt_search_merk;
    private javax.swing.JTextField txt_search_spesifikasi;
    // End of variables declaration//GEN-END:variables
}
