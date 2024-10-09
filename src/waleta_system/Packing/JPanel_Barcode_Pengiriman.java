package waleta_system.Packing;

import java.awt.HeadlessException;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import waleta_system.BahanBaku.JPanel_GradeBahanBaku;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;
import waleta_system.Class.ExportToExcel;
import waleta_system.Class.TableColumnHider;

public class JPanel_Barcode_Pengiriman extends javax.swing.JPanel {

    
    String sql = null;
    String sql2 = null;
    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    PreparedStatement pst;
//    static TableColumnHider hider;

    public JPanel_Barcode_Pengiriman() {
        initComponents();
    }

    public void HidePriceColumn() {
        TableColumnHider hider1 = new TableColumnHider(tabel_data_spk);
//        hider = new TableColumnHider(tabel_data_spk);
        hider1.hide("Harga");
        TableColumnHider hider2 = new TableColumnHider(tabel_detail_grade);
        hider2.hide("CNY / Kg");
        hider2.hide("CNY");
    }

    public void ShowPriceColumn() {
        TableColumnHider hider1 = new TableColumnHider(tabel_data_spk);
//        hider = new TableColumnHider(tabel_data_spk);
        hider1.show("Harga");
        TableColumnHider hider2 = new TableColumnHider(tabel_detail_grade);
        hider2.show("CNY / Kg");
        hider2.show("CNY");
    }

    public void init() {
        try {
            
            
            refreshTable_spk();
            tabel_data_spk.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent event) {
                    if (!event.getValueIsAdjusting() && tabel_data_spk.getSelectedRow() != -1) {
                        int i = tabel_data_spk.getSelectedRow();
                        label_no_spk.setText(tabel_data_spk.getValueAt(i, 0).toString());
                        refreshTable_detail();

                    }
                }
            });

            tabel_detail_grade.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent event) {
                    if (!event.getValueIsAdjusting() && tabel_detail_grade.getSelectedRow() != -1) {
                        int i = tabel_detail_grade.getSelectedRow();
                        label_grade_buyer.setText(tabel_detail_grade.getValueAt(i, 2).toString());
                        refreshTable_barcode();
                    }
                }
            });
        } catch (Exception ex) {
            Logger.getLogger(JPanel_GradeBahanBaku.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_spk() {
        try {
            String filter_status = "";
            switch (ComboBox_Search_status.getSelectedIndex()) {
                case 0:
                    filter_status = "";
                    break;
                case 1:
                    filter_status = " AND `tb_spk`.`fix` = 'FIXED'";
                    break;
                case 2:
                    filter_status = " AND `tb_spk`.`fix` = 'Not Fix'";
                    break;
                default:
                    filter_status = "";
                    break;
            }

            DefaultTableModel model = (DefaultTableModel) tabel_data_spk.getModel();
            model.setRowCount(0);
            sql = "SELECT `tb_spk`.`kode_spk`, `tb_buyer`.`nama`, `no_revisi`, `tanggal_spk`, `tanggal_keluar`, `tanggal_fix`, `detail`, `status`, `fix` "
                    + "FROM `tb_spk` LEFT JOIN `tb_buyer` ON `tb_spk`.`buyer` = `tb_buyer`.`kode_buyer` "
                    + "WHERE `status` = 'PROSES' AND `tb_spk`.`kode_spk` LIKE '%" + txt_search_kode_spk.getText() + "%' AND `tb_buyer`.`nama` LIKE '%" + txt_search_nama_buyer.getText() + "%'" + filter_status
                    + "GROUP BY `tb_spk`.`kode_spk` ORDER BY `tanggal_keluar` DESC";
            Connection con = Utility.db.getConnection();
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            Object[] row = new Object[6];
            while (rs.next()) {
                row[0] = rs.getString("kode_spk");
                row[1] = rs.getString("no_revisi");
                row[2] = rs.getString("nama");
                row[3] = rs.getDate("tanggal_spk");
                row[4] = rs.getString("fix");
                row[5] = rs.getString("status");

                model.addRow(row);
            }
            label_total_data_spk.setText(Integer.toString(tabel_data_spk.getRowCount()));
            ColumnsAutoSizer.sizeColumnsToFit(tabel_data_spk);
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_Barcode_Pengiriman.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_detail() {
        try {
            DefaultTableModel model = (DefaultTableModel) tabel_detail_grade.getModel();
            model.setRowCount(0);
            sql2 = "SELECT `no`, `grade_waleta`, `grade_buyer`, `berat_kemasan`, `jumlah_kemasan`, `berat`, `harga_cny`, `keterangan`, COUNT(`tb_barcode_pengiriman`.`no_barcode`) AS 'barcode' \n"
                    + "FROM `tb_spk_detail` LEFT JOIN `tb_barcode_pengiriman` ON `tb_spk_detail`.`no` = `tb_barcode_pengiriman`.`no_grade_spk`"
                    + "WHERE `kode_spk` = '" + label_no_spk.getText() + "' GROUP BY `no`";
            Connection con = Utility.db.getConnection();
            pst = con.prepareStatement(sql2);
            rs = pst.executeQuery();
            Object[] row = new Object[8];
            while (rs.next()) {
                row[0] = rs.getString("no");
                row[1] = rs.getString("grade_waleta");
                row[2] = rs.getString("grade_buyer");
                row[3] = rs.getInt("berat_kemasan");
                row[4] = rs.getInt("jumlah_kemasan");
                row[5] = rs.getInt("berat");
                row[6] = rs.getString("keterangan");
                row[7] = rs.getInt("barcode");
                model.addRow(row);
            }
            label_total_grade_spk.setText(Integer.toString(tabel_detail_grade.getRowCount()));
            ColumnsAutoSizer.sizeColumnsToFit(tabel_detail_grade);
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_Barcode_Pengiriman.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_barcode() {
        try {
            DefaultTableModel model = (DefaultTableModel) tabel_barcode.getModel();
            model.setRowCount(0);
            int noIndex = tabel_detail_grade.getSelectedRow();
            sql = "SELECT `no_barcode`, `no_grade_spk`, `final_packing`, `no_urut` FROM `tb_barcode_pengiriman`"
                    + "WHERE `no_grade_spk` = '" + tabel_detail_grade.getValueAt(noIndex, 0).toString() + "'";
            Connection con = Utility.db.getConnection();
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            Object[] row = new Object[5];
            while (rs.next()) {
                row[0] = rs.getInt("no_urut");
                row[1] = rs.getString("no_barcode");
                row[2] = rs.getString("final_packing");
                model.addRow(row);
            }
            label_total_barcode.setText(Integer.toString(tabel_barcode.getRowCount()));
            ColumnsAutoSizer.sizeColumnsToFit(tabel_barcode);
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_Barcode_Pengiriman.class.getName()).log(Level.SEVERE, null, ex);
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
        jLabel1 = new javax.swing.JLabel();
        txt_search_kode_spk = new javax.swing.JTextField();
        button_search = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabel_data_spk = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabel_detail_grade = new javax.swing.JTable();
        jLabel8 = new javax.swing.JLabel();
        label_total_data_spk = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        label_total_grade_spk = new javax.swing.JLabel();
        label_no_spk = new javax.swing.JLabel();
        button_export_dataSPK = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        ComboBox_Search_status = new javax.swing.JComboBox<>();
        jScrollPane3 = new javax.swing.JScrollPane();
        tabel_barcode = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        txt_search_nama_buyer = new javax.swing.JTextField();
        label_total_barcode = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        label_grade_buyer = new javax.swing.JLabel();
        button_input_barcode = new javax.swing.JButton();
        button_refresh_detail_spk = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Barcode Pengiriman", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N
        jPanel1.setPreferredSize(new java.awt.Dimension(1366, 700));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("Kode SPK :");

        txt_search_kode_spk.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_kode_spk.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_kode_spkKeyPressed(evt);
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

        tabel_data_spk.setAutoCreateRowSorter(true);
        tabel_data_spk.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tabel_data_spk.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No SPK", "Revisi", "Buyer", "Tgl SPK", "Status Fix", "Status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
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
        tabel_data_spk.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tabel_data_spk);

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel4.setText("Tabel Data SPK Waleta");

        tabel_detail_grade.setAutoCreateRowSorter(true);
        tabel_detail_grade.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tabel_detail_grade.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "019 Grade", "Grade Buyer", "Gr / Pack", "Tot Pack", "Gram", "Keterangan", "Total Barcode"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.String.class, java.lang.Integer.class
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
        tabel_detail_grade.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(tabel_detail_grade);

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel8.setText("Total SPK :");

        label_total_data_spk.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_spk.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_data_spk.setText("0");

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel11.setText("Total Grade :");

        label_total_grade_spk.setBackground(new java.awt.Color(255, 255, 255));
        label_total_grade_spk.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_grade_spk.setText("0");

        label_no_spk.setBackground(new java.awt.Color(255, 255, 255));
        label_no_spk.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_no_spk.setText("No SPK");

        button_export_dataSPK.setBackground(new java.awt.Color(255, 255, 255));
        button_export_dataSPK.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_dataSPK.setText("Export");
        button_export_dataSPK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_dataSPKActionPerformed(evt);
            }
        });

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel13.setText("Detail Grade SPK");

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel5.setText("Status :");

        ComboBox_Search_status.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_Search_status.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "FIXED", "Not Fix" }));

        tabel_barcode.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "NO", "BARCODE BOX", "Status Packing"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabel_barcode.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(tabel_barcode);

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("Nama Buyer :");

        txt_search_nama_buyer.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_nama_buyer.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_nama_buyerKeyPressed(evt);
            }
        });

        label_total_barcode.setBackground(new java.awt.Color(255, 255, 255));
        label_total_barcode.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_barcode.setText("0");

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel12.setText("Total Barcode :");

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel14.setText("Barcode");

        label_grade_buyer.setBackground(new java.awt.Color(255, 255, 255));
        label_grade_buyer.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_grade_buyer.setText("GRADE BUYER");

        button_input_barcode.setBackground(new java.awt.Color(255, 255, 255));
        button_input_barcode.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_input_barcode.setText("Input Barcode");
        button_input_barcode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_input_barcodeActionPerformed(evt);
            }
        });

        button_refresh_detail_spk.setBackground(new java.awt.Color(255, 255, 255));
        button_refresh_detail_spk.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_refresh_detail_spk.setText("Refresh");
        button_refresh_detail_spk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refresh_detail_spkActionPerformed(evt);
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
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_kode_spk, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_nama_buyer, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_Search_status, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_search)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel8)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_data_spk))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel4)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(button_export_dataSPK)))
                                .addGap(0, 409, Short.MAX_VALUE))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 595, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_grade_spk)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_no_spk)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(button_refresh_detail_spk)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_input_barcode))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 503, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_grade_buyer))
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_barcode)))
                        .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_search_kode_spk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_nama_buyer, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_search, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_Search_status, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export_dataSPK, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_grade_buyer, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_no_spk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_input_barcode, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_refresh_detail_spk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 581, Short.MAX_VALUE)
                    .addComponent(jScrollPane2)
                    .addComponent(jScrollPane3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(label_total_data_spk)
                    .addComponent(jLabel11)
                    .addComponent(label_total_grade_spk)
                    .addComponent(jLabel12)
                    .addComponent(label_total_barcode))
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

    private void txt_search_kode_spkKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_kode_spkKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_spk();
        }
    }//GEN-LAST:event_txt_search_kode_spkKeyPressed

    private void button_searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_searchActionPerformed
        // TODO add your handling code here:
        refreshTable_spk();
    }//GEN-LAST:event_button_searchActionPerformed

    private void button_export_dataSPKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_dataSPKActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) tabel_data_spk.getModel();
        ExportToExcel.writeToExcel(model, jPanel1);
    }//GEN-LAST:event_button_export_dataSPKActionPerformed

    private void txt_search_nama_buyerKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_nama_buyerKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_spk();
        }
    }//GEN-LAST:event_txt_search_nama_buyerKeyPressed

    private void button_input_barcodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_input_barcodeActionPerformed
        // TODO add your handling code here:
        JFileChooser chooser = new JFileChooser();
        int i = tabel_detail_grade.getSelectedRow();
        if (i > -1) {
            try {
                int n = 0;
                chooser.setDialogTitle("Select CSV file to import!");
                int result = chooser.showOpenDialog(this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    Utility.db.getConnection();
                    File file = chooser.getSelectedFile();
                    String filename1 = file.getAbsolutePath();
                    try (BufferedReader br = new BufferedReader(new FileReader(filename1))) {
                        String line;
                        String Query = null;
                        try {
                            Utility.db.getConnection().setAutoCommit(false);
                            Query = "DELETE FROM `tb_barcode_pengiriman` WHERE `no_grade_spk` = '" + tabel_detail_grade.getValueAt(i, 0) + "'";
                            Utility.db.getConnection().prepareStatement(Query);
                            Utility.db.getStatement().executeUpdate(Query);
                            int no_urut_barcode = 0;
                            while ((line = br.readLine()) != null) {
                                no_urut_barcode++;
                                String[] value = line.split(";");
                                Query = "INSERT INTO `tb_barcode_pengiriman`(`no_barcode`, `no_grade_spk`, `no_urut`) "
                                        + "VALUES ('" + value[0] + "', '" + tabel_detail_grade.getValueAt(i, 0) + "', '" + no_urut_barcode + "')";
                                Utility.db.getConnection().prepareStatement(Query);
                                if ((Utility.db.getStatement().executeUpdate(Query)) == 1) {
                                    n++;
                                } else {
                                    JOptionPane.showMessageDialog(this, "Failed insert " + value[0]);
                                }
                            }
                            Utility.db.getConnection().commit();
                        } catch (SQLException ex) {
                            try {
                                Utility.db.getConnection().rollback();
                            } catch (SQLException e) {
                                Logger.getLogger(JPanel_Barcode_Pengiriman.class.getName()).log(Level.SEVERE, null, e);
                            }
                            JOptionPane.showMessageDialog(this, ex.getMessage());
                            Logger.getLogger(JPanel_Barcode_Pengiriman.class.getName()).log(Level.SEVERE, null, ex);
                        } finally {
                            try {
                                Utility.db.getConnection().setAutoCommit(true);
                            } catch (SQLException ex) {
                                Logger.getLogger(JPanel_Barcode_Pengiriman.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            JOptionPane.showMessageDialog(this, "Data Berhasil Masuk : " + n);
                            refreshTable_detail();
                        }
                    }
                }
            } catch (HeadlessException | IOException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Silahkan pilih grade terlebih dahulu !");
        }

    }//GEN-LAST:event_button_input_barcodeActionPerformed

    private void button_refresh_detail_spkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refresh_detail_spkActionPerformed
        // TODO add your handling code here:
        refreshTable_detail();
    }//GEN-LAST:event_button_refresh_detail_spkActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_Search_status;
    private javax.swing.JButton button_export_dataSPK;
    private javax.swing.JButton button_input_barcode;
    private javax.swing.JButton button_refresh_detail_spk;
    private javax.swing.JButton button_search;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel label_grade_buyer;
    private javax.swing.JLabel label_no_spk;
    private javax.swing.JLabel label_total_barcode;
    private javax.swing.JLabel label_total_data_spk;
    private javax.swing.JLabel label_total_grade_spk;
    private javax.swing.JTable tabel_barcode;
    public static javax.swing.JTable tabel_data_spk;
    private javax.swing.JTable tabel_detail_grade;
    private javax.swing.JTextField txt_search_kode_spk;
    private javax.swing.JTextField txt_search_nama_buyer;
    // End of variables declaration//GEN-END:variables
}
