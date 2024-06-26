package waleta_system.BahanJadi;

import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;
import waleta_system.Class.ExportToExcel;
import waleta_system.Manajemen.JFrame_KategoriStokJualTV;
import waleta_system.Manajemen.JFrame_KategoriStokJualTV_New;

public class JPanel_StockBahanJadi extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();

    public JPanel_StockBahanJadi() {
        initComponents();
    }

    public void init() {
        try {

            ComboBox_bentuk.removeAllItems();
            ComboBox_bentuk.addItem("All");
            sql = "SELECT DISTINCT(`bentuk_grade`) AS 'bentuk' FROM `tb_grade_bahan_jadi`";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_bentuk.addItem(rs.getString("bentuk"));
            }
            refreshTable_StockBJ();
            refreshTable_StockBoxBJ();
            tabel_stockBoxBJ.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent event) {
                    if (!event.getValueIsAdjusting() && tabel_stockBoxBJ.getSelectedRow() != -1) {
                        int x = tabel_stockBoxBJ.getSelectedRow();
                        if (x > -1) {
                            label_grade.setText(tabel_stockBoxBJ.getValueAt(x, 1).toString());
                            refreshTable_detailBox();
                        }
                    }
                }
            });
        } catch (Exception ex) {
            Logger.getLogger(JPanel_StockBahanJadi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_StockBJ() {
        try {
            String bentuk = "AND `bentuk_grade` = '" + ComboBox_bentuk.getSelectedItem().toString() + "'";
            if (ComboBox_bentuk.getSelectedItem().toString().equals("All")) {
                bentuk = "";
            }
            float total_keping = 0, total_gram = 0;
            DefaultTableModel model = (DefaultTableModel) tabel_stockBJ.getModel();
            model.setRowCount(0);
            Object[] row = new Object[5];
            sql = "SELECT `grade_bahan_jadi`, `tb_grade_bahan_jadi`.`kode_grade`, `bentuk_grade`, SUM(`tb_grading_bahan_jadi`.`keping`) AS 'kpg', SUM(`tb_grading_bahan_jadi`.`gram`) AS 'gram' "
                    + "FROM `tb_grading_bahan_jadi` LEFT JOIN `tb_bahan_jadi_masuk` ON `tb_grading_bahan_jadi`.`kode_asal_bahan_jadi` = `tb_bahan_jadi_masuk`.`kode_asal` "
                    + "LEFT JOIN `tb_tutupan_grading` ON `tb_bahan_jadi_masuk`.`kode_tutupan` = `tb_tutupan_grading`.`kode_tutupan` "
                    + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_grading_bahan_jadi`.`grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode` "
                    + "WHERE `tb_grade_bahan_jadi`.`kode_grade` LIKE '%" + txt_search_grade.getText() + "%' AND (`tb_tutupan_grading`.`status_box` = 'PROSES' OR `tb_tutupan_grading`.`status_box` IS NULL) AND `tanggal_grading` IS NOT NULL "
                    + bentuk
                    + "GROUP BY `grade_bahan_jadi`";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                row[0] = rs.getString("grade_bahan_jadi");
                row[1] = rs.getString("kode_grade");
                row[2] = rs.getString("bentuk_grade");
                row[3] = rs.getInt("kpg");
                total_keping = total_keping + rs.getInt("kpg");
                row[4] = rs.getInt("gram");
                total_gram = total_gram + rs.getInt("gram");
                model.addRow(row);
            }

            ColumnsAutoSizer.sizeColumnsToFit(tabel_stockBJ);
            label_total_kpg_BJ.setText(decimalFormat.format(total_keping));
            label_total_gram_BJ.setText(decimalFormat.format(total_gram));
            label_total_data_BJ.setText(Integer.toString(tabel_stockBJ.getRowCount()));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_StockBahanJadi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_StockBoxBJ() {
        try {
            float total_keping = 0, total_gram = 0;
            String lokasi = "";
            if (null != ComboBox_lokasi.getSelectedItem().toString()) {
                switch (ComboBox_lokasi.getSelectedItem().toString()) {
                    case "All":
                        lokasi = " AND `lokasi_terakhir` IN ('GRADING', 'PACKING', 'RE-PROSES', 'TREATMENT', 'DIPINJAM') AND (`tb_box_packing`.`status` IN ('PENDING', 'STOK', 'RETUR') OR `tb_box_packing`.`status` IS NULL)";
                        break;
                    case "GRADING":
                        lokasi = " AND `lokasi_terakhir` = 'GRADING'";
                        break;
                    case "PACKING":
                        lokasi = " AND `lokasi_terakhir` = 'PACKING' AND (`tb_box_packing`.`status` IN ('PENDING', 'STOK', 'RETUR') OR `tb_box_packing`.`status` IS NULL)";
                        break;
                    case "RE-PROSES":
                        lokasi = " AND `lokasi_terakhir` = 'RE-PROSES'";
                        break;
                    case "TREATMENT":
                        lokasi = " AND `lokasi_terakhir` = 'TREATMENT'";
                        break;
                    case "DIPINJAM":
                        lokasi = " AND `lokasi_terakhir` = 'DIPINJAM'";
                        break;
                    default:
                        break;
                }
            }

            String bentuk = " AND `bentuk_grade` = '" + ComboBox_bentuk.getSelectedItem().toString() + "'";
            if (ComboBox_bentuk.getSelectedItem().toString().equals("All")) {
                bentuk = "";
            }

            DefaultTableModel model = (DefaultTableModel) tabel_stockBoxBJ.getModel();
            model.setRowCount(0);
            Object[] row = new Object[6];
            sql = "SELECT `tb_grade_bahan_jadi`.`kode`, `tb_grade_bahan_jadi`.`kode_grade`, `tb_grade_bahan_jadi`.`bentuk_grade`, SUM(`keping`) AS 'keping', SUM(`berat`) AS 'berat', COUNT(`tb_box_bahan_jadi`.`no_box`) AS 'jumlah_box' "
                    + "FROM `tb_box_bahan_jadi` "
                    + "LEFT JOIN `tb_box_packing` ON `tb_box_bahan_jadi`.`no_box` = `tb_box_packing`.`no_box`"
                    + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_box_bahan_jadi`.`kode_grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode`"
                    + "LEFT JOIN `tb_tutupan_grading` ON `tb_box_bahan_jadi`.`no_tutupan` = `tb_tutupan_grading`.`kode_tutupan`"
                    + "WHERE `tb_grade_bahan_jadi`.`kode_grade` LIKE '%" + txt_search_grade.getText() + "%' " + lokasi + bentuk + " AND (`tb_tutupan_grading`.`status_box` = 'SELESAI' OR `tb_tutupan_grading`.`status_box` IS NULL) "
                    + "GROUP BY `tb_box_bahan_jadi`.`kode_grade_bahan_jadi`";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                row[0] = rs.getString("kode");
                row[1] = rs.getString("kode_grade");
                row[2] = rs.getString("bentuk_grade");
                row[3] = rs.getInt("keping");
                row[4] = rs.getFloat("berat");
                row[5] = rs.getInt("jumlah_box");

                total_keping = total_keping + rs.getInt("keping");
                total_gram = total_gram + rs.getFloat("berat");

                if (rs.getFloat("berat") > 0) {
                    model.addRow(row);
                }
            }
            ColumnsAutoSizer.sizeColumnsToFit(tabel_stockBoxBJ);
            label_total_kpg_BoxBJ.setText(decimalFormat.format(total_keping));
            label_total_gram_BoxBJ.setText(decimalFormat.format(total_gram));
            label_total_data_BoxBJ.setText(Integer.toString(tabel_stockBoxBJ.getRowCount()));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_StockBahanJadi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_detailBox() {
        try {
            String lokasi = "";
            if (null != ComboBox_lokasi.getSelectedItem().toString()) {
                switch (ComboBox_lokasi.getSelectedItem().toString()) {
                    case "All":
                        lokasi = " AND `lokasi_terakhir` IN ('GRADING', 'PACKING', 'RE-PROSES', 'TREATMENT', 'DIPINJAM') AND (`tb_box_packing`.`status` IN ('PENDING', 'STOK', 'RETUR') OR `tb_box_packing`.`status` IS NULL)";
                        break;
                    case "GRADING":
                        lokasi = " AND `lokasi_terakhir` = 'GRADING'";
                        break;
                    case "PACKING":
                        lokasi = " AND `lokasi_terakhir` = 'PACKING' AND (`tb_box_packing`.`status` IN ('PENDING', 'STOK', 'RETUR') OR `tb_box_packing`.`status` IS NULL)";
                        break;
                    case "RE-PROSES":
                        lokasi = " AND `lokasi_terakhir` = 'RE-PROSES'";
                        break;
                    case "TREATMENT":
                        lokasi = " AND `lokasi_terakhir` = 'TREATMENT'";
                        break;
                    case "DIPINJAM":
                        lokasi = " AND `lokasi_terakhir` = 'DIPINJAM'";
                        break;
                    default:
                        break;
                }
            }
            float total_keping = 0, total_gram = 0;
            DefaultTableModel model = (DefaultTableModel) tabel_detailBox.getModel();
            model.setRowCount(0);
            sql = "SELECT `tb_box_bahan_jadi`.`no_box`, `keping`, `berat`, `lokasi_terakhir`, `status_terakhir`, `tb_box_packing`.`status`, `no_tutupan`, `memo_box_bj` "
                    + "FROM `tb_box_bahan_jadi` "
                    + "LEFT JOIN `tb_tutupan_grading` ON `tb_box_bahan_jadi`.`no_tutupan` = `tb_tutupan_grading`.`kode_tutupan`"
                    + "LEFT JOIN `tb_box_packing` ON `tb_box_bahan_jadi`.`no_box` = `tb_box_packing`.`no_box` "
                    + "WHERE "
                    + "`tb_box_bahan_jadi`.`no_box` LIKE '%" + txt_search_box.getText() + "%' "
                    + "AND `no_tutupan` LIKE '%" + txt_search_tutupan.getText() + "%'"
                    + " AND `kode_grade_bahan_jadi` = '" + tabel_stockBoxBJ.getValueAt(tabel_stockBoxBJ.getSelectedRow(), 0) + "' "
                    + lokasi + " AND (`tb_tutupan_grading`.`status_box` = 'SELESAI' OR `tb_tutupan_grading`.`status_box` IS NULL)";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[15];
            while (rs.next()) {
                row[0] = rs.getString("no_box");
                row[1] = rs.getInt("keping");
                total_keping = total_keping + rs.getInt("keping");
                row[2] = rs.getFloat("berat");
                total_gram = total_gram + rs.getFloat("berat");
                row[3] = rs.getString("lokasi_terakhir");
                row[4] = rs.getString("status_terakhir");
                row[5] = rs.getString("status");
                row[6] = rs.getString("no_tutupan");
                row[7] = rs.getString("memo_box_bj");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(tabel_detailBox);
            label_total_keping_box.setText(decimalFormat.format(total_keping));
            label_total_gram_box.setText(decimalFormat.format(total_gram));
            label_total_box.setText(Integer.toString(tabel_detailBox.getRowCount()));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_StockBahanJadi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        txt_search_grade = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        button_search = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabel_stockBoxBJ = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        label_total_data_BoxBJ = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        label_total_kpg_BoxBJ = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        label_total_gram_BoxBJ = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        button_export_StockBoxBJ = new javax.swing.JButton();
        ComboBox_lokasi = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        button_tv = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tabel_stockBJ = new javax.swing.JTable();
        label_total_kpg_BJ = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        label_total_gram_BJ = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        label_total_data_BJ = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        button_export_StockBJ = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        ComboBox_bentuk = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        label_total_box = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        txt_search_box = new javax.swing.JTextField();
        label_total_gram_box = new javax.swing.JLabel();
        txt_search_tutupan = new javax.swing.JTextField();
        label_total_keping_box = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        label_grade = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabel_detailBox = new javax.swing.JTable();
        jLabel8 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        button_Export_tabelbox = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        button_input_memo = new javax.swing.JButton();
        button_save_stok = new javax.swing.JButton();

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
        jLabel1.setText("Grade :");

        button_search.setBackground(new java.awt.Color(255, 255, 255));
        button_search.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search.setText("Search");
        button_search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_searchActionPerformed(evt);
            }
        });

        tabel_stockBoxBJ.setAutoCreateRowSorter(true);
        tabel_stockBoxBJ.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tabel_stockBoxBJ.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode", "Grade", "Bentuk", "Keping", "Gram", "Box"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Float.class, java.lang.Float.class
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
        tabel_stockBoxBJ.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tabel_stockBoxBJ);

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setText("Total Data :");

        label_total_data_BoxBJ.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_BoxBJ.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_data_BoxBJ.setText("0");

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel3.setText("Total Keping :");

        label_total_kpg_BoxBJ.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kpg_BoxBJ.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_kpg_BoxBJ.setText("0");

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel4.setText("Total Gram :");

        label_total_gram_BoxBJ.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_BoxBJ.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_gram_BoxBJ.setText("0");

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel11.setText("Stock Box Barang Jadi");

        button_export_StockBoxBJ.setBackground(new java.awt.Color(255, 255, 255));
        button_export_StockBoxBJ.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_StockBoxBJ.setText("Export to Excel");
        button_export_StockBoxBJ.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_StockBoxBJActionPerformed(evt);
            }
        });

        ComboBox_lokasi.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_lokasi.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "GRADING", "PACKING", "RE-PROSES", "DIPINJAM", "TREATMENT" }));

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel12.setText("Lokasi :");

        button_tv.setBackground(new java.awt.Color(255, 255, 255));
        button_tv.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_tv.setText("TV Stok");
        button_tv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_tvActionPerformed(evt);
            }
        });

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel13.setText("Stock Barang Jadi Belum jadi Box");

        tabel_stockBJ.setAutoCreateRowSorter(true);
        tabel_stockBJ.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tabel_stockBJ.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode", "Grade", "Bentuk", "Keping", "Gram"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class
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
        tabel_stockBJ.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(tabel_stockBJ);

        label_total_kpg_BJ.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kpg_BJ.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_kpg_BJ.setText("0");

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel6.setText("Total Gram :");

        label_total_gram_BJ.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_BJ.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_gram_BJ.setText("0");

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel14.setText("Total Data :");

        label_total_data_BJ.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_BJ.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_data_BJ.setText("0");

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel15.setText("Total Keping :");

        button_export_StockBJ.setBackground(new java.awt.Color(255, 255, 255));
        button_export_StockBJ.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_StockBJ.setText("Export to Excel");
        button_export_StockBJ.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_StockBJActionPerformed(evt);
            }
        });

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel16.setText("Bentuk Grade :");

        ComboBox_bentuk.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_bentuk.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        label_total_box.setBackground(new java.awt.Color(255, 255, 255));
        label_total_box.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_box.setText("0");

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel17.setText("Search Box :");

        txt_search_box.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_box.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_boxKeyPressed(evt);
            }
        });

        label_total_gram_box.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_box.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_gram_box.setText("0");

        txt_search_tutupan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_tutupan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_tutupanKeyPressed(evt);
            }
        });

        label_total_keping_box.setBackground(new java.awt.Color(255, 255, 255));
        label_total_keping_box.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_keping_box.setText("0");

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel18.setText("Search Tutupan :");

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel9.setText("Total Keping :");

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel10.setText("Grade :");

        label_grade.setBackground(new java.awt.Color(255, 255, 255));
        label_grade.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_grade.setText("-");

        tabel_detailBox.setAutoCreateRowSorter(true);
        tabel_detailBox.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tabel_detailBox.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No Box", "Kpg", "Gram", "Lokasi", "Status Terakhir Box", "Status Packing", "No Tutupan", "Memo"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.Float.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
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
        tabel_detailBox.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabel_detailBoxMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tabel_detailBox);

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel8.setText("Total Data :");

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel5.setText("Tabel Detail Box");

        button_Export_tabelbox.setBackground(new java.awt.Color(255, 255, 255));
        button_Export_tabelbox.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_Export_tabelbox.setText("Export to Excel");
        button_Export_tabelbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_Export_tabelboxActionPerformed(evt);
            }
        });

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel7.setText("Total Gram :");

        button_input_memo.setBackground(new java.awt.Color(255, 255, 255));
        button_input_memo.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_input_memo.setText("Input Memo");
        button_input_memo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_input_memoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 584, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
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
                                .addComponent(label_total_gram_box))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel17)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_box, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel18)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_tutupan, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_grade, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_input_memo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_Export_tabelbox)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_search_box, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_tutupan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_grade, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_Export_tabelbox, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_input_memo, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 578, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel7)
                    .addComponent(label_total_gram_box)
                    .addComponent(jLabel8)
                    .addComponent(label_total_box)
                    .addComponent(jLabel9)
                    .addComponent(label_total_keping_box))
                .addContainerGap())
        );

        button_save_stok.setBackground(new java.awt.Color(255, 255, 255));
        button_save_stok.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_save_stok.setText("Save Data Stok");
        button_save_stok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_save_stokActionPerformed(evt);
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
                        .addComponent(txt_search_grade, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_search)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_tv)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_lokasi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_bentuk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_save_stok))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_kpg_BJ))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_data_BJ))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_gram_BJ))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_export_StockBJ))
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 333, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_data_BoxBJ))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_kpg_BoxBJ))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_gram_BoxBJ))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 387, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_export_StockBoxBJ)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_grade, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_search, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_tv, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_lokasi, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_bentuk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_save_stok, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_export_StockBoxBJ, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_export_StockBJ, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel14)
                    .addComponent(label_total_data_BJ)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(jLabel2)
                        .addComponent(label_total_data_BoxBJ)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel15)
                    .addComponent(label_total_kpg_BJ)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(jLabel3)
                        .addComponent(label_total_kpg_BoxBJ)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel6)
                    .addComponent(label_total_gram_BJ)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(jLabel4)
                        .addComponent(label_total_gram_BoxBJ)))
                .addContainerGap())
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
        refreshTable_StockBJ();
        refreshTable_StockBoxBJ();
    }//GEN-LAST:event_button_searchActionPerformed

    private void txt_search_gradeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_gradeKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_StockBJ();
            refreshTable_StockBoxBJ();
        }
    }//GEN-LAST:event_txt_search_gradeKeyPressed

    private void button_export_StockBoxBJActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_StockBoxBJActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) tabel_stockBoxBJ.getModel();
        ExportToExcel.writeToExcel(model, jPanel1);
    }//GEN-LAST:event_button_export_StockBoxBJActionPerformed

    private void button_Export_tabelboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_Export_tabelboxActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) tabel_detailBox.getModel();
        ExportToExcel.writeToExcel(model, jPanel1);
    }//GEN-LAST:event_button_Export_tabelboxActionPerformed

    private void button_tvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_tvActionPerformed
        // TODO add your handling code here:
        JFrame_KategoriStokJualTV_New Stok = new JFrame_KategoriStokJualTV_New();
        Stok.pack();
        Stok.setLocationRelativeTo(this);
        Stok.setVisible(true);
        Stok.setEnabled(true);
        Stok.setExtendedState(JFrame.MAXIMIZED_BOTH);
        Stok.init(0);
    }//GEN-LAST:event_button_tvActionPerformed

    private void button_export_StockBJActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_StockBJActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) tabel_stockBJ.getModel();
        ExportToExcel.writeToExcel(model, jPanel1);
    }//GEN-LAST:event_button_export_StockBJActionPerformed

    private void button_save_stokActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_save_stokActionPerformed
        // TODO add your handling code here:
        JDialog_SaveStokOpname dialog = new JDialog_SaveStokOpname(new javax.swing.JFrame(), true);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);
        dialog.setResizable(false);
    }//GEN-LAST:event_button_save_stokActionPerformed

    private void button_input_memoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_input_memoActionPerformed
        // TODO add your handling code here:
        JOptionPane.showMessageDialog(this, "Klik 2x pada tabel untuk edit memo box");
    }//GEN-LAST:event_button_input_memoActionPerformed

    private void tabel_detailBoxMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabel_detailBoxMouseClicked
        // TODO add your handling code here:
        decimalFormat.setMaximumFractionDigits(5);
        decimalFormat.setGroupingUsed(false);
        int i = tabel_detailBox.getSelectedRow();
        if (evt.getClickCount() == 2) {
            try {
                String memo_lama = "";
                if (tabel_detailBox.getValueAt(i, 7) != null) {
                    memo_lama = tabel_detailBox.getValueAt(i, 7).toString();
                }
                String memo_baru = JOptionPane.showInputDialog("Masukkan Memo Box BJ : ", memo_lama);
                if (memo_baru != null) {
                    sql = "UPDATE `tb_box_bahan_jadi` SET `memo_box_bj`='" + memo_baru + "' WHERE `no_box`='" + tabel_detailBox.getValueAt(i, 0).toString() + "' ";
                    if ((Utility.db.getStatement().executeUpdate(sql)) == 1) {
                        refreshTable_detailBox();
                        JOptionPane.showMessageDialog(this, "Update success!");
                    } else {
                        JOptionPane.showMessageDialog(this, "Update failed!");
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex);
                Logger.getLogger(JPanel_StockBahanJadi.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_tabel_detailBoxMouseClicked

    private void txt_search_boxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_boxKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_detailBox();
        }
    }//GEN-LAST:event_txt_search_boxKeyPressed

    private void txt_search_tutupanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_tutupanKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_detailBox();
        }
    }//GEN-LAST:event_txt_search_tutupanKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_bentuk;
    private javax.swing.JComboBox<String> ComboBox_lokasi;
    private javax.swing.JButton button_Export_tabelbox;
    private javax.swing.JButton button_export_StockBJ;
    private javax.swing.JButton button_export_StockBoxBJ;
    private javax.swing.JButton button_input_memo;
    private javax.swing.JButton button_save_stok;
    private javax.swing.JButton button_search;
    private javax.swing.JButton button_tv;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel label_grade;
    private javax.swing.JLabel label_total_box;
    private javax.swing.JLabel label_total_data_BJ;
    private javax.swing.JLabel label_total_data_BoxBJ;
    private javax.swing.JLabel label_total_gram_BJ;
    private javax.swing.JLabel label_total_gram_BoxBJ;
    private javax.swing.JLabel label_total_gram_box;
    private javax.swing.JLabel label_total_keping_box;
    private javax.swing.JLabel label_total_kpg_BJ;
    private javax.swing.JLabel label_total_kpg_BoxBJ;
    private javax.swing.JTable tabel_detailBox;
    private javax.swing.JTable tabel_stockBJ;
    private javax.swing.JTable tabel_stockBoxBJ;
    private javax.swing.JTextField txt_search_box;
    private javax.swing.JTextField txt_search_grade;
    private javax.swing.JTextField txt_search_tutupan;
    // End of variables declaration//GEN-END:variables
}
