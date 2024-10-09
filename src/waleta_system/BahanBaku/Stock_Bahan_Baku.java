package waleta_system.BahanBaku;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;

public class Stock_Bahan_Baku extends javax.swing.JDialog {

    String sql = null;
    ResultSet rs;
    String no_kartu, kode_grade;
    int min_kpg = 0, min_gram = 0;
    String akses = null;

    public Stock_Bahan_Baku(java.awt.Frame parent, boolean modal, String akses) {
        super(parent, modal);
        initComponents();
        this.setResizable(false);
        this.akses = akses;

        Table_stock_bahan_baku.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && Table_stock_bahan_baku.getSelectedRow() != -1) {
                    int i = Table_stock_bahan_baku.getSelectedRow();
                    if (i > -1) {
                        txt_kpg_diambil.setText(Table_stock_bahan_baku.getValueAt(i, 6).toString());
                        txt_gram_diambil.setText(Table_stock_bahan_baku.getValueAt(i, 7).toString());
                    }
                }
            }
        });

        try {
            sql = "SELECT `kode_grade` FROM `tb_grade_bahan_baku` ORDER BY `kode_grade`";
            ResultSet rs1 = Utility.db.getStatement().executeQuery(sql);
            while (rs1.next()) {
                ComboBox_Search_grade.addItem(rs1.getString("kode_grade"));
            }
            ComboBox_Search_grade.setSelectedIndex(1);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(Stock_Bahan_Baku.this, e);
        }
        refreshTable();
        AutoCompleteDecorator.decorate(ComboBox_Search_grade);
    }

    public void refreshTable() {
        try {
            DefaultTableModel model = (DefaultTableModel) Table_stock_bahan_baku.getModel();
            model.setRowCount(0);
            if ("".equals(txt_min_kpg.getText()) || txt_min_kpg.getText() == null) {
                min_kpg = 0;
            } else {
                min_kpg = Integer.parseInt(txt_min_kpg.getText());
            }
            if ("".equals(txt_min_gram.getText()) || txt_min_gram.getText() == null) {
                min_gram = 0;
            } else {
                min_gram = Integer.parseInt(txt_min_gram.getText());
            }
            String filter_CMP = "";
            if ("kartu campuran".equals(akses)) {
                filter_CMP = "AND `tb_grading_bahan_baku`.`no_kartu_waleta` NOT LIKE '%CMP%'";
            }

            String kode = ComboBox_Search_grade.getSelectedItem().toString();
            if (ComboBox_Search_grade.getSelectedItem().equals("All")) {
                kode = "";
            }
            sql = "SELECT `tb_grading_bahan_baku`.`no_kartu_waleta`, `tb_grading_bahan_baku`.`kode_grade`,  "
                    + "`tb_grading_bahan_baku`.`jumlah_keping` AS 'kpg_masuk', `tb_grading_bahan_baku`.`total_berat` AS 'gram_masuk', \n"
                    + "`kpg_lp`, `gram_lp`, `kpg_jual`, `gram_jual`, `kpg_cmp`, `gram_cmp`\n"
                    + "FROM `tb_grading_bahan_baku`\n"
                    + "LEFT JOIN `tb_bahan_baku_masuk` ON `tb_grading_bahan_baku`.`no_kartu_waleta` = `tb_bahan_baku_masuk`.`no_kartu_waleta`\n"
                    + "LEFT JOIN (SELECT `no_kartu_waleta`, `kode_grade`, SUM(`tb_laporan_produksi`.`jumlah_keping`) AS 'kpg_lp', SUM(`tb_laporan_produksi`.`berat_basah`) AS 'gram_lp' FROM `tb_laporan_produksi` GROUP BY `no_kartu_waleta`, `kode_grade`) LP "
                    + "ON `tb_grading_bahan_baku`.`no_kartu_waleta` = LP.`no_kartu_waleta` AND `tb_grading_bahan_baku`.`kode_grade` = LP.`kode_grade`\n"
                    + "LEFT JOIN (SELECT `no_kartu_waleta`, `kode_grade`, SUM(`total_keping_keluar`) AS 'kpg_jual', SUM(`total_berat_keluar`) AS 'gram_jual' FROM `tb_bahan_baku_keluar` GROUP BY `no_kartu_waleta`, `kode_grade`) JUAL "
                    + "ON `tb_grading_bahan_baku`.`no_kartu_waleta` = JUAL.`no_kartu_waleta` AND `tb_grading_bahan_baku`.`kode_grade` = JUAL.`kode_grade`\n"
                    + "LEFT JOIN (SELECT `no_grading`, SUM(`tb_kartu_cmp_detail`.`keping`) AS 'kpg_cmp', SUM(`tb_kartu_cmp_detail`.`gram`) AS 'gram_cmp' FROM `tb_kartu_cmp_detail` GROUP BY `no_grading`) CMP "
                    + "ON `tb_grading_bahan_baku`.`no_grading` = CMP.`no_grading`\n"
                    + "WHERE "
                    + "`tb_bahan_baku_masuk`.`tgl_timbang` IS NOT NULL "
                    + "AND `tb_bahan_baku_masuk`.`last_stok` > 0 "
                    + "AND `tb_grading_bahan_baku`.`kode_grade` LIKE '%" + kode + "%' "
                    + "AND `tb_grading_bahan_baku`.`no_kartu_waleta` LIKE '%" + txt_no_kartu.getText() + "%'"
                    + filter_CMP;
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[8];
            while (rs.next()) {
                row[0] = rs.getString("no_kartu_waleta");
                row[1] = rs.getString("kode_grade");
                row[2] = rs.getInt("kpg_masuk");
                row[3] = rs.getInt("gram_masuk");
                row[4] = (rs.getInt("kpg_lp") + rs.getInt("kpg_jual") + rs.getInt("kpg_cmp"));
                row[5] = (rs.getInt("gram_lp") + rs.getInt("gram_jual") + rs.getInt("gram_cmp"));
                row[6] = rs.getInt("kpg_masuk") - (rs.getInt("kpg_lp") + rs.getInt("kpg_jual") + rs.getInt("kpg_cmp"));
                row[7] = rs.getInt("gram_masuk") - (rs.getInt("gram_lp") + rs.getInt("gram_jual") + rs.getInt("gram_cmp"));
                model.addRow(row);
//                if (sisa_gram >= min_gram && sisa_kpg >= min_kpg) {
//                    model.addRow(row);
//                }
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_stock_bahan_baku);

            Table_stock_bahan_baku.setDefaultRenderer(Integer.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    switch (column) {
                        case 7:
                            if ((int) Table_stock_bahan_baku.getValueAt(row, column) > 0) {
                                if (isSelected) {
                                    comp.setBackground(Table_stock_bahan_baku.getSelectionBackground());
                                    comp.setForeground(Table_stock_bahan_baku.getSelectionForeground());
                                } else {
                                    comp.setBackground(Table_stock_bahan_baku.getBackground());
                                    comp.setForeground(Table_stock_bahan_baku.getForeground());
                                }
                            } else {
                                if (isSelected) {
                                    comp.setBackground(Table_stock_bahan_baku.getSelectionBackground());
                                    comp.setForeground(Color.red);
                                } else {
                                    comp.setBackground(Color.PINK);
                                    comp.setForeground(Color.red);
                                }
                            }
                            break;
                        default:
                            if (isSelected) {
                                comp.setBackground(Table_stock_bahan_baku.getSelectionBackground());
                                comp.setForeground(Table_stock_bahan_baku.getSelectionForeground());
                            } else {
                                comp.setBackground(Table_stock_bahan_baku.getBackground());
                                comp.setForeground(Table_stock_bahan_baku.getForeground());
                            }
                            break;
                    }
                    return comp;
                }
            });
            Table_stock_bahan_baku.repaint();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(Stock_Bahan_Baku.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        button_ok = new javax.swing.JButton();
        txt_kpg_diambil = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        Table_stock_bahan_baku = new javax.swing.JTable();
        txt_gram_diambil = new javax.swing.JTextField();
        txt_no_kartu = new javax.swing.JTextField();
        button_refresh = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        ComboBox_Search_grade = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txt_min_gram = new javax.swing.JTextField();
        button_cancel = new javax.swing.JButton();
        txt_min_kpg = new javax.swing.JTextField();
        button_scan_barcode = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Data Stock Bahan Baku");
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        button_ok.setBackground(new java.awt.Color(255, 255, 255));
        button_ok.setText("OK");
        button_ok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_okActionPerformed(evt);
            }
        });

        txt_kpg_diambil.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Table_stock_bahan_baku.setAutoCreateRowSorter(true);
        Table_stock_bahan_baku.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_stock_bahan_baku.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No Kartu", "Grade", "Kpg Masuk", "Berat Masuk", "Kpg Keluar", "Berat Keluar", "Stok Kpg", "Stok Gram"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class
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
        Table_stock_bahan_baku.getTableHeader().setReorderingAllowed(false);
        Table_stock_bahan_baku.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                Table_stock_bahan_bakuMousePressed(evt);
            }
        });
        jScrollPane1.setViewportView(Table_stock_bahan_baku);

        txt_gram_diambil.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_gram_diambil.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_gram_diambilKeyTyped(evt);
            }
        });

        txt_no_kartu.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_no_kartu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_no_kartuActionPerformed(evt);
            }
        });

        button_refresh.setBackground(new java.awt.Color(255, 255, 255));
        button_refresh.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_refresh.setText("Refresh");
        button_refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refreshActionPerformed(evt);
            }
        });

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel7.setText("Gram");

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("Kode Grade :");

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel4.setText("Sisa Gram Minimal :");

        ComboBox_Search_grade.setEditable(true);
        ComboBox_Search_grade.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_Search_grade.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("No Kartu :");

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("Sisa Keping Minimal :");

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel6.setText("Kpg");

        txt_min_gram.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_min_gram.setText("0");
        txt_min_gram.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_min_gramKeyReleased(evt);
            }
        });

        button_cancel.setBackground(new java.awt.Color(255, 255, 255));
        button_cancel.setText("Cancel");
        button_cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_cancelActionPerformed(evt);
            }
        });

        txt_min_kpg.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_min_kpg.setText("0");
        txt_min_kpg.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_min_kpgKeyReleased(evt);
            }
        });

        button_scan_barcode.setBackground(new java.awt.Color(255, 255, 255));
        button_scan_barcode.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_scan_barcode.setText("Scan Barcode");
        button_scan_barcode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_scan_barcodeActionPerformed(evt);
            }
        });

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel5.setText("Akan diambil sebanyak :");

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 2, 10)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(102, 102, 102));
        jLabel8.setText("Hanya menampilkan kartu yang sudah selesai grading (punya tanggal timbang)");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txt_kpg_diambil, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_gram_diambil, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_cancel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_ok))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_Search_grade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_no_kartu, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_refresh)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_scan_barcode))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_min_kpg, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_min_gram, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel8))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(ComboBox_Search_grade, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_no_kartu, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(button_refresh, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_scan_barcode, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_min_kpg, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_min_gram, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_kpg_diambil, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_ok)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_cancel)
                    .addComponent(txt_gram_diambil, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
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

    private void button_okActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_okActionPerformed
        // TODO add your handling code here:
        int i = Table_stock_bahan_baku.getSelectedRow();
        if (i == -1) {
            JOptionPane.showMessageDialog(this, "anda belum memilih data");
        } else {
            boolean ok = true;
            int keping_diambil = 0, gram_diambil = 0, keping_sisa = 0, gram_sisa = 0;
            try {
                keping_diambil = Integer.valueOf(txt_kpg_diambil.getText());
                gram_diambil = Integer.valueOf(txt_gram_diambil.getText().replace(" ", ""));
                keping_sisa = (int) Table_stock_bahan_baku.getValueAt(i, 6);
                gram_sisa = (int) Table_stock_bahan_baku.getValueAt(i, 7);
            } catch (NumberFormatException e) {
                System.out.println("error");
                ok = false;
            }

            if (gram_diambil == 0) {
                JOptionPane.showMessageDialog(this, "Maaf jumlah gram diambil tidak boleh 0 !");
            } else if (keping_diambil > gram_diambil) {
                JOptionPane.showMessageDialog(this, "Maaf jumlah keping diambil tidak bisa lebih besar dari jumlah gram diambil !");
            } else if (keping_diambil > keping_sisa) {
                JOptionPane.showMessageDialog(this, "Maaf jumlah keping diambil melebihi stok kpg yang tersisa !");
            } else if (gram_diambil > gram_sisa) {
                JOptionPane.showMessageDialog(this, "Maaf jumlah gram diambil melebihi stok gram yang tersisa !");
            } else if (ok) {
                no_kartu = Table_stock_bahan_baku.getValueAt(i, 0).toString();
                kode_grade = Table_stock_bahan_baku.getValueAt(i, 1).toString();
                if ("lp".equals(akses)) {
                    JDialog_Edit_Insert_LP.Label_no_kartu_LP.setText(no_kartu);
                    JDialog_Edit_Insert_LP.Label_kode_grade_lp.setText(kode_grade);
                    JDialog_Edit_Insert_LP.txt_jumlah_keping_lp.setText(txt_kpg_diambil.getText());
                    JDialog_Edit_Insert_LP.txt_berat_basah_lp.setText(txt_gram_diambil.getText());
                    if (keping_diambil == 0) {
                        int gram_per_kpg_lp = Math.round((float) gram_diambil / 8);
                        JDialog_Edit_Insert_LP.label_jumlah_kpg_lp.setText(Integer.toString(gram_per_kpg_lp));
                        JDialog_Edit_Insert_LP.txt_jumlah_keping_upah.setText(Integer.toString(gram_per_kpg_lp));
                    } else {
                        JDialog_Edit_Insert_LP.label_jumlah_kpg_lp.setText(txt_kpg_diambil.getText());
                        JDialog_Edit_Insert_LP.txt_jumlah_keping_upah.setText(txt_kpg_diambil.getText());
                    }
                } else if ("keluar".equals(akses)) {
                    JDialog_PengeluaranBahanBaku.label_no_kartu.setText(no_kartu);
                    JDialog_PengeluaranBahanBaku.Label_kode_grade.setText(kode_grade);
                    JDialog_PengeluaranBahanBaku.txt_keping.setText(txt_kpg_diambil.getText());
                    JDialog_PengeluaranBahanBaku.txt_berat.setText(txt_gram_diambil.getText());
                    JDialog_PengeluaranBahanBaku.button_insert.doClick();
                } else if ("kartu campuran".equals(akses)) {
                    try {
                        String kode_grading = null;
                        String query = "SELECT `no_grading` FROM `tb_grading_bahan_baku` WHERE `no_kartu_waleta` = '" + no_kartu + "' AND `kode_grade` = '" + kode_grade + "'";
                        ResultSet rs1 = Utility.db.getStatement().executeQuery(query);
                        while (rs1.next()) {
                            kode_grading = rs1.getString("no_grading");
                        }
                        JDialog_CreateKartuCampuran.label_kode.setText(kode_grading);
                        JDialog_CreateKartuCampuran.label_no_kartu.setText(no_kartu);
                        JDialog_CreateKartuCampuran.Label_kode_grade.setText(kode_grade);
                        JDialog_CreateKartuCampuran.txt_keping.setText(txt_kpg_diambil.getText());
                        JDialog_CreateKartuCampuran.txt_berat.setText(txt_gram_diambil.getText());
                        JDialog_CreateKartuCampuran.button_insert.doClick();
                    } catch (SQLException ex) {
                        Logger.getLogger(Stock_Bahan_Baku.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                this.dispose();
            }
        }
    }//GEN-LAST:event_button_okActionPerformed

    private void button_cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_cancelActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_button_cancelActionPerformed

    private void Table_stock_bahan_bakuMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Table_stock_bahan_bakuMousePressed
        int i = Table_stock_bahan_baku.getSelectedRow();
        if (i > -1) {
            txt_kpg_diambil.setText(Table_stock_bahan_baku.getValueAt(i, 6).toString());
            txt_gram_diambil.setText(Table_stock_bahan_baku.getValueAt(i, 7).toString());
        }
    }//GEN-LAST:event_Table_stock_bahan_bakuMousePressed

    private void txt_min_kpgKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_min_kpgKeyReleased
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_min_kpgKeyReleased

    private void txt_no_kartuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_no_kartuActionPerformed
        // TODO add your handling code here:
        refreshTable();
    }//GEN-LAST:event_txt_no_kartuActionPerformed

    private void button_refreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refreshActionPerformed
        // TODO add your handling code here:
        refreshTable();
    }//GEN-LAST:event_button_refreshActionPerformed

    private void txt_min_gramKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_min_gramKeyReleased
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_min_gramKeyReleased

    private void button_scan_barcodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_scan_barcodeActionPerformed
        // TODO add your handling code here:
        try {
            // TODO add your handling code here:
            String kode = JOptionPane.showInputDialog("Insert Barcode : ");
            if (!"".equals(kode)) {
                sql = "SELECT `no_grading`, `no_kartu_waleta`, `kode_grade`, `jumlah_keping`, `total_berat`, `harga_bahanbaku` "
                        + "FROM `tb_grading_bahan_baku` WHERE `no_grading` = '" + kode + "'";
                rs = Utility.db.getStatement().executeQuery(sql);
                if (rs.next()) {
                    txt_no_kartu.setText(rs.getString("no_kartu_waleta"));
                    ComboBox_Search_grade.setSelectedItem(rs.getString("kode_grade"));
                    refreshTable();
                } else {
                    JOptionPane.showMessageDialog(this, "Data not found !");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(JDialog_Edit_Insert_LP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_scan_barcodeActionPerformed

    private void txt_gram_diambilKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_gram_diambilKeyTyped
        // TODO add your handling code here:
        if (Character.isWhitespace(evt.getKeyChar())) {
            char e = evt.getKeyChar();
            String c = String.valueOf(e);
            evt.consume();
        }
    }//GEN-LAST:event_txt_gram_diambilKeyTyped


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_Search_grade;
    public static javax.swing.JTable Table_stock_bahan_baku;
    private javax.swing.JButton button_cancel;
    private javax.swing.JButton button_ok;
    private javax.swing.JButton button_refresh;
    private javax.swing.JButton button_scan_barcode;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField txt_gram_diambil;
    private javax.swing.JTextField txt_kpg_diambil;
    private javax.swing.JTextField txt_min_gram;
    private javax.swing.JTextField txt_min_kpg;
    private javax.swing.JTextField txt_no_kartu;
    // End of variables declaration//GEN-END:variables
}
