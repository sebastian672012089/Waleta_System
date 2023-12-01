package waleta_system.BahanJadi;

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
import waleta_system.BahanBaku.JPanel_GradeBahanBaku;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;
import waleta_system.Class.ExportToExcel;

public class JPanel_DataGradeBahanJadi extends javax.swing.JPanel {

    
    String sql = null;
    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();

    public JPanel_DataGradeBahanJadi() {
        initComponents();
    }

    public void init() {
        try {
            ComboBox_bentuk.removeAllItems();
            ComboBox_bentuk.addItem("All");
            sql = "SELECT DISTINCT(`bentuk_grade`) AS 'item' FROM `tb_grade_bahan_jadi` WHERE 1";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_bentuk.addItem(rs.getString("item"));
            }
            ComboBox_kategori.removeAllItems();
            ComboBox_kategori.addItem("All");
            sql = "SELECT DISTINCT(`Kategori1`) AS 'item' FROM `tb_grade_bahan_jadi` WHERE 1";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_kategori.addItem(rs.getString("item"));
            }
            ComboBox_kategori_jual.removeAllItems();
            ComboBox_kategori_jual.addItem("All");
            sql = "SELECT DISTINCT(`kategori_jual`) AS 'item' FROM `tb_grade_bahan_jadi` WHERE 1";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_kategori_jual.addItem(rs.getString("item"));
            }
            
            
            refreshTable();

            Table_GradeGNS.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent event) {
                    if (!event.getValueIsAdjusting() && Table_GradeGNS.getSelectedRow() != -1) {
                        int i = Table_GradeGNS.getSelectedRow();
                        if (i > -1) {
                            button_ubah_status.setEnabled(true);
                            String status_GNS = Table_GradeGNS.getValueAt(i, 6).toString();
                            if (status_GNS.equals("AKTIF")) {
                                button_ubah_status.setText("NON-AKTIFKAN");
                            } else if (status_GNS.equals("NON-AKTIF")) {
                                button_ubah_status.setText("AKTIFKAN");
                            }
                        }
                    }
                }
            });
            Table_GradeNS.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent event) {
                    if (!event.getValueIsAdjusting() && Table_GradeNS.getSelectedRow() != -1) {
                        int j = Table_GradeNS.getSelectedRow();
                        if (j > -1) {
                            button_ubah_status_NS.setEnabled(true);
                            String status_NS = Table_GradeNS.getValueAt(j, 6).toString();
                            if (status_NS.equals("AKTIF")) {
                                button_ubah_status_NS.setText("NON-AKTIFKAN");
                            } else if (status_NS.equals("NON-AKTIF")) {
                                button_ubah_status_NS.setText("AKTIFKAN");
                            }
                        }
                    }
                }
            });
        } catch (Exception ex) {
            Logger.getLogger(JPanel_GradeBahanBaku.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable() {
        try {
            String filter_bentuk = " AND `bentuk_grade` = '" + ComboBox_bentuk.getSelectedItem().toString() + "'";
            if (ComboBox_bentuk.getSelectedItem().toString().equals("All")) {
                filter_bentuk = "";
            }
            String filter_kategori = " AND `Kategori1` = '" + ComboBox_kategori.getSelectedItem().toString() + "'";
            if (ComboBox_kategori.getSelectedItem().toString().equals("All")) {
                filter_kategori = "";
            }
            String filter_kategori_jual = " AND `kategori_jual` = '" + ComboBox_kategori_jual.getSelectedItem().toString() + "'";
            if (ComboBox_kategori_jual.getSelectedItem().toString().equals("All")) {
                filter_kategori_jual = "";
            }
            DefaultTableModel modelGNS = (DefaultTableModel) Table_GradeGNS.getModel();
            DefaultTableModel modelNS = (DefaultTableModel) Table_GradeNS.getModel();
            modelGNS.setRowCount(0);
            modelNS.setRowCount(0);
            sql = "SELECT * FROM `tb_grade_bahan_jadi` WHERE "
                    + "(`kode` LIKE '%" + txt_search_keywords.getText() + "%' OR "
                    + "`kode_grade` LIKE '%" + txt_search_keywords.getText() + "%' OR "
                    + "`nama_grade` LIKE '%" + txt_search_keywords.getText() + "%')" 
                    + filter_bentuk + filter_kategori + filter_kategori_jual;
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[15];
            while (rs.next()) {
                row[0] = rs.getString("kode");
                row[1] = rs.getString("kode_grade");
                row[2] = rs.getString("nama_grade");
                row[3] = rs.getString("bentuk_grade");
                row[4] = rs.getString("kategori1");
                row[5] = rs.getString("kategori_jual");
                row[6] = rs.getString("status_grade");
                row[7] = rs.getInt("upah_reproses");
                row[8] = rs.getString("kategori_subbagian_gradebarangjadi");

                if (rs.getString("kode_grade").contains("GNS")) {
                    modelGNS.addRow(row);
                } else {
                    modelNS.addRow(row);
                }
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_GradeGNS);
            ColumnsAutoSizer.sizeColumnsToFit(Table_GradeNS);
            Table_GradeGNS.getColumnModel().getColumn(2).setPreferredWidth(100);
            Table_GradeNS.getColumnModel().getColumn(2).setPreferredWidth(100);
            label_total_GNS.setText(Integer.toString(Table_GradeGNS.getRowCount()));
            label_total_NS.setText(Integer.toString(Table_GradeNS.getRowCount()));
            button_ubah_status.setEnabled(false);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_BoxBahanJadi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel_grade_bahan_baku = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        Table_GradeGNS = new javax.swing.JTable();
        jLabel6 = new javax.swing.JLabel();
        label_total_GNS = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        button_export_GradeBahanBaku = new javax.swing.JButton();
        txt_search_keywords = new javax.swing.JTextField();
        button_search = new javax.swing.JButton();
        button_TambahGrade = new javax.swing.JButton();
        button_delete = new javax.swing.JButton();
        button_ubah_status = new javax.swing.JButton();
        button_EditGrade = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        Table_GradeNS = new javax.swing.JTable();
        label_total_NS = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        ComboBox_bentuk = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        ComboBox_kategori = new javax.swing.JComboBox<>();
        button_EditGrade_NS = new javax.swing.JButton();
        button_delete_NS = new javax.swing.JButton();
        button_ubah_status_NS = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        ComboBox_kategori_jual = new javax.swing.JComboBox<>();

        setBackground(new java.awt.Color(255, 255, 255));

        jPanel_grade_bahan_baku.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_grade_bahan_baku.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Grade Barang Jadi", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N
        jPanel_grade_bahan_baku.setMaximumSize(new java.awt.Dimension(1306, 736));
        jPanel_grade_bahan_baku.setPreferredSize(new java.awt.Dimension(1366, 700));

        Table_GradeGNS.setAutoCreateRowSorter(true);
        Table_GradeGNS.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_GradeGNS.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode", "Kode Grade", "Nama Grade", "Bentuk Grade", "Kategori", "Kategori Jual", "Status", "Upah Reproses", "Kategori Sub"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class
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
        Table_GradeGNS.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(Table_GradeGNS);

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jLabel6.setText("Total Data :");

        label_total_GNS.setBackground(new java.awt.Color(255, 255, 255));
        label_total_GNS.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        label_total_GNS.setText("TOTAL");

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("Keywords :");

        button_export_GradeBahanBaku.setBackground(new java.awt.Color(255, 255, 255));
        button_export_GradeBahanBaku.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_GradeBahanBaku.setText("Export To Excel");
        button_export_GradeBahanBaku.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_GradeBahanBakuActionPerformed(evt);
            }
        });

        txt_search_keywords.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_keywords.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_keywordsKeyPressed(evt);
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

        button_TambahGrade.setBackground(new java.awt.Color(255, 255, 255));
        button_TambahGrade.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_TambahGrade.setText("Tambah Grade");
        button_TambahGrade.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_TambahGradeActionPerformed(evt);
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

        button_ubah_status.setBackground(new java.awt.Color(255, 255, 255));
        button_ubah_status.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_ubah_status.setText("AKTIFKAN");
        button_ubah_status.setEnabled(false);
        button_ubah_status.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_ubah_statusActionPerformed(evt);
            }
        });

        button_EditGrade.setBackground(new java.awt.Color(255, 255, 255));
        button_EditGrade.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_EditGrade.setText("Edit Grade");
        button_EditGrade.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_EditGradeActionPerformed(evt);
            }
        });

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel2.setText("Grade Barang Jadi GNS");

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel3.setText("Grade Barang Jadi Non NS");

        Table_GradeNS.setAutoCreateRowSorter(true);
        Table_GradeNS.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_GradeNS.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode", "Kode Grade", "Nama Grade", "Bentuk Grade", "Kategori", "Kategori Jual", "Status", "Upah Reproses", "Kategori Sub"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class
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
        Table_GradeNS.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(Table_GradeNS);

        label_total_NS.setBackground(new java.awt.Color(255, 255, 255));
        label_total_NS.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        label_total_NS.setText("TOTAL");

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jLabel7.setText("Total Data :");

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel4.setText("Bentuk : ");

        ComboBox_bentuk.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_bentuk.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel5.setText("Kategori : ");

        ComboBox_kategori.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_kategori.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        button_EditGrade_NS.setBackground(new java.awt.Color(255, 255, 255));
        button_EditGrade_NS.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_EditGrade_NS.setText("Edit Grade");
        button_EditGrade_NS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_EditGrade_NSActionPerformed(evt);
            }
        });

        button_delete_NS.setBackground(new java.awt.Color(255, 255, 255));
        button_delete_NS.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_delete_NS.setText("Delete");
        button_delete_NS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_delete_NSActionPerformed(evt);
            }
        });

        button_ubah_status_NS.setBackground(new java.awt.Color(255, 255, 255));
        button_ubah_status_NS.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_ubah_status_NS.setText("AKTIFKAN");
        button_ubah_status_NS.setEnabled(false);
        button_ubah_status_NS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_ubah_status_NSActionPerformed(evt);
            }
        });

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel8.setText("Kategori Jual : ");

        ComboBox_kategori_jual.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_kategori_jual.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        javax.swing.GroupLayout jPanel_grade_bahan_bakuLayout = new javax.swing.GroupLayout(jPanel_grade_bahan_baku);
        jPanel_grade_bahan_baku.setLayout(jPanel_grade_bahan_bakuLayout);
        jPanel_grade_bahan_bakuLayout.setHorizontalGroup(
            jPanel_grade_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_grade_bahan_bakuLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_grade_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_grade_bahan_bakuLayout.createSequentialGroup()
                        .addGroup(jPanel_grade_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_grade_bahan_bakuLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_GNS))
                            .addGroup(jPanel_grade_bahan_bakuLayout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(button_TambahGrade)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_EditGrade)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_delete)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_ubah_status))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 660, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_grade_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 668, Short.MAX_VALUE)
                            .addGroup(jPanel_grade_bahan_bakuLayout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(button_EditGrade_NS)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_delete_NS)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_ubah_status_NS))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_grade_bahan_bakuLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_NS))))
                    .addGroup(jPanel_grade_bahan_bakuLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_keywords, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_bentuk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_kategori, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_kategori_jual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_search)
                        .addGap(115, 115, 115)
                        .addComponent(button_export_GradeBahanBaku)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel_grade_bahan_bakuLayout.setVerticalGroup(
            jPanel_grade_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_grade_bahan_bakuLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(jPanel_grade_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(button_export_GradeBahanBaku, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_keywords, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_bentuk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_kategori, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_kategori_jual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_search, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_grade_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_grade_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(button_delete_NS, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_ubah_status_NS, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_EditGrade_NS, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_grade_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(button_delete, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_ubah_status, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_EditGrade, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_TambahGrade, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_grade_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_grade_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 578, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_grade_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_grade_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_total_NS)
                        .addComponent(jLabel7))
                    .addGroup(jPanel_grade_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_total_GNS)
                        .addComponent(jLabel6)))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jPanel_grade_bahan_baku, javax.swing.GroupLayout.DEFAULT_SIZE, 1364, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel_grade_bahan_baku, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void button_export_GradeBahanBakuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_GradeBahanBakuActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Table_GradeGNS.getModel();
        ExportToExcel.writeToExcel(model, this);
    }//GEN-LAST:event_button_export_GradeBahanBakuActionPerformed

    private void txt_search_keywordsKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_keywordsKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_search_keywordsKeyPressed

    private void button_searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_searchActionPerformed
        // TODO add your handling code here:
        refreshTable();
    }//GEN-LAST:event_button_searchActionPerformed

    private void button_TambahGradeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_TambahGradeActionPerformed
        // TODO add your handling code here:
        JDialog_tambahGradeBJ dialog = new JDialog_tambahGradeBJ(new javax.swing.JFrame(), true, "insert", null, null, null, null, null, null, "0", null);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);
        refreshTable();
    }//GEN-LAST:event_button_TambahGradeActionPerformed

    private void button_deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_deleteActionPerformed
        // TODO add your handling code here:
        try {
            int j = Table_GradeGNS.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih data yang ingin di hapus !");
            } else {
                int dialogResult = JOptionPane.showConfirmDialog(this, "Yakin hapus data ini?", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    // delete code here
                    String Query = "DELETE FROM `tb_grade_bahan_jadi` WHERE `kode` = '" + Table_GradeGNS.getValueAt(j, 0) + "'";
                    Utility.db.getConnection().createStatement();
                    if ((Utility.db.getStatement().executeUpdate(Query)) == 1) {
                        JOptionPane.showMessageDialog(this, "data DELETED !!");
                        refreshTable();
                    } else {
                        JOptionPane.showMessageDialog(this, "data not DELETED !!");
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_DataGradeBahanJadi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_deleteActionPerformed

    private void button_ubah_statusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_ubah_statusActionPerformed
        // TODO add your handling code here:
        try {
            int i = Table_GradeGNS.getSelectedRow();
            String status = null;
            if (button_ubah_status.getText().equals("AKTIFKAN")) {
                status = "AKTIF";
            } else if (button_ubah_status.getText().equals("NON-AKTIFKAN")) {
                status = "NON-AKTIF";
            }
            String Query = "UPDATE `tb_grade_bahan_jadi` SET `status_grade`='" + status + "' WHERE `kode` = '" + Table_GradeGNS.getValueAt(i, 0) + "'";
            Utility.db.getConnection().createStatement();
            if ((Utility.db.getStatement().executeUpdate(Query)) == 1) {
                refreshTable();
                JOptionPane.showMessageDialog(this, "Grade " + Table_GradeGNS.getValueAt(i, 1).toString() + " telah DI" + status + "KAN");
            } else {
                JOptionPane.showMessageDialog(this, "Failed !!");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_DataGradeBahanJadi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_ubah_statusActionPerformed

    private void button_EditGradeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_EditGradeActionPerformed
        // TODO add your handling code here:
        int i = Table_GradeGNS.getSelectedRow();
        if (i > -1) {
            String kode = Table_GradeGNS.getValueAt(i, 0).toString();
            String grade = Table_GradeGNS.getValueAt(i, 1).toString().substring(4);
            String nama = Table_GradeGNS.getValueAt(i, 2).toString().substring(20);
            String bentuk = Table_GradeGNS.getValueAt(i, 3).toString();
            String kategori1 = Table_GradeGNS.getValueAt(i, 4).toString();
            String KategoriJual = Table_GradeGNS.getValueAt(i, 5).toString();
            String upah_reproses = Table_GradeGNS.getValueAt(i, 7).toString();
            String kategori_subbagian = Table_GradeGNS.getValueAt(i, 8).toString();
            JDialog_tambahGradeBJ dialog = new JDialog_tambahGradeBJ(new javax.swing.JFrame(), true, "edit", kode, grade, nama, bentuk, kategori1, KategoriJual, upah_reproses, kategori_subbagian);
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            dialog.setEnabled(true);
            refreshTable();
        } else {
            JOptionPane.showMessageDialog(this, "klik / pilih data yang akan di edit");
        }
    }//GEN-LAST:event_button_EditGradeActionPerformed

    private void button_EditGrade_NSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_EditGrade_NSActionPerformed
        // TODO add your handling code here:
        int i = Table_GradeNS.getSelectedRow();
        if (i > -1) {
            String kode = Table_GradeNS.getValueAt(i, 0).toString();
            String grade = Table_GradeNS.getValueAt(i, 1).toString().substring(4);
            String nama = Table_GradeNS.getValueAt(i, 2).toString().substring(20);
            String bentuk = Table_GradeNS.getValueAt(i, 3).toString();
            String Kategori1 = Table_GradeNS.getValueAt(i, 4).toString();
            String KategoriJual = Table_GradeNS.getValueAt(i, 5).toString();
            String upah_reproses = Table_GradeGNS.getValueAt(i, 7).toString();
            String kategori_subbagian = Table_GradeGNS.getValueAt(i, 8).toString();
            JDialog_tambahGradeBJ dialog = new JDialog_tambahGradeBJ(new javax.swing.JFrame(), true, "edit", kode, grade, nama, bentuk, Kategori1, KategoriJual, upah_reproses, kategori_subbagian);
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            dialog.setEnabled(true);
            refreshTable();
        } else {
            JOptionPane.showMessageDialog(this, "klik / pilih data yang akan di edit");
        }
    }//GEN-LAST:event_button_EditGrade_NSActionPerformed

    private void button_delete_NSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_delete_NSActionPerformed
        // TODO add your handling code here:
        try {
            int j = Table_GradeNS.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih data yang ingin di hapus !");
            } else {
                int dialogResult = JOptionPane.showConfirmDialog(this, "Yakin hapus data ini?", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    // delete code here
                    String Query = "DELETE FROM `tb_grade_bahan_jadi` WHERE `kode` = '" + Table_GradeNS.getValueAt(j, 0) + "'";
                    Utility.db.getConnection().createStatement();
                    if ((Utility.db.getStatement().executeUpdate(Query)) == 1) {
                        JOptionPane.showMessageDialog(this, "data DELETED !!");
                        refreshTable();
                    } else {
                        JOptionPane.showMessageDialog(this, "data not DELETED !!");
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_DataGradeBahanJadi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_delete_NSActionPerformed

    private void button_ubah_status_NSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_ubah_status_NSActionPerformed
        // TODO add your handling code here:
        try {
            int i = Table_GradeNS.getSelectedRow();
            String status = null;
            if (button_ubah_status_NS.getText().equals("AKTIFKAN")) {
                status = "AKTIF";
            } else if (button_ubah_status_NS.getText().equals("NON-AKTIFKAN")) {
                status = "NON-AKTIF";
            }
            String Query = "UPDATE `tb_grade_bahan_jadi` SET `status_grade`='" + status + "' WHERE `kode` = '" + Table_GradeNS.getValueAt(i, 0) + "'";
            Utility.db.getConnection().createStatement();
            if ((Utility.db.getStatement().executeUpdate(Query)) == 1) {
                refreshTable();
                JOptionPane.showMessageDialog(this, "Grade " + Table_GradeNS.getValueAt(i, 1).toString() + " telah DI" + status + "KAN");
            } else {
                JOptionPane.showMessageDialog(this, "Failed !!");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_DataGradeBahanJadi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_ubah_status_NSActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_bentuk;
    private javax.swing.JComboBox<String> ComboBox_kategori;
    private javax.swing.JComboBox<String> ComboBox_kategori_jual;
    private javax.swing.JTable Table_GradeGNS;
    private javax.swing.JTable Table_GradeNS;
    private javax.swing.JButton button_EditGrade;
    private javax.swing.JButton button_EditGrade_NS;
    private javax.swing.JButton button_TambahGrade;
    private javax.swing.JButton button_delete;
    private javax.swing.JButton button_delete_NS;
    private javax.swing.JButton button_export_GradeBahanBaku;
    private javax.swing.JButton button_search;
    private javax.swing.JButton button_ubah_status;
    private javax.swing.JButton button_ubah_status_NS;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel_grade_bahan_baku;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel label_total_GNS;
    private javax.swing.JLabel label_total_NS;
    private javax.swing.JTextField txt_search_keywords;
    // End of variables declaration//GEN-END:variables
}
