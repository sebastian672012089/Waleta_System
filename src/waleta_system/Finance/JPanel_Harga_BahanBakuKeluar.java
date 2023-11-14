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
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import waleta_system.BahanBaku.JPanel_GradeBahanBaku;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.ExportToExcel;

public class JPanel_Harga_BahanBakuKeluar extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    float kadar_air_bahan_baku = 0;

    public JPanel_Harga_BahanBakuKeluar() {
        initComponents();
    }

    public void init() {
        try {
            ComboBox_Search_grade.removeAllItems();
            ComboBox_Search_grade.addItem("All");
            String query = "SELECT `kode_grade` FROM `tb_grade_bahan_baku` ORDER BY `kode_grade` ASC";
            rs = Utility.db.getStatement().executeQuery(query);
            while (rs.next()) {
                ComboBox_Search_grade.addItem(rs.getString("kode_grade"));
            }
            refreshTable_detail_penjualan();
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_GradeBahanBaku.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_detail_penjualan() {
        try {
            decimalFormat.setMaximumFractionDigits(5);
            double total_gram = 0, total_kpg = 0, total_harga = 0;
            DefaultTableModel model = (DefaultTableModel) Table_BahanBakuKeluar.getModel();
            model.setRowCount(0);
            String filter_grade = "";
//            if (!"All".equals(ComboBox_Search_grade.getSelectedItem().toString())) {
//                filter_grade = "AND `tb_bahan_baku_keluar`.`kode_grade` = '" + ComboBox_Search_grade.getSelectedItem().toString() + "'";
//            }
            if (Date_Search_Penjualan_1.getDate() != null && Date_Search_Penjualan_2.getDate() != null) {
                sql = "SELECT `tb_bahan_baku_keluar`.`kode_pengeluaran`, `jenis_pengeluaran`, `tb_bahan_baku_keluar`.`no_kartu_waleta`, `tb_bahan_baku_keluar`.`kode_grade`, `tgl_keluar`, `nama_customer`, `total_keping_keluar`, `total_berat_keluar`, `tb_grading_bahan_baku`.`harga_bahanbaku`,  (`total_berat_keluar`*`tb_grading_bahan_baku`.`harga_bahanbaku`) AS 'nilai'\n"
                        + "FROM `tb_bahan_baku_keluar` LEFT JOIN `tb_grading_bahan_baku` ON `tb_bahan_baku_keluar`.`no_kartu_waleta` = `tb_grading_bahan_baku`.`no_kartu_waleta` AND `tb_bahan_baku_keluar`.`kode_grade` = `tb_grading_bahan_baku`.`kode_grade`\n"
                        + "LEFT JOIN `tb_bahan_baku_keluar1` ON (`tb_bahan_baku_keluar`.`kode_pengeluaran` = `tb_bahan_baku_keluar1`.`kode_pengeluaran`)"
                        + "LEFT JOIN `tb_customer_baku` ON (`tb_bahan_baku_keluar1`.`customer_baku` = `tb_customer_baku`.`kode_cust`)"
                        + "WHERE `tb_bahan_baku_keluar`.`no_kartu_waleta` LIKE '%" + txt_search_kartu.getText() + "%' "
                        + "AND `tb_bahan_baku_keluar`.`kode_pengeluaran` LIKE '%" + txt_search_kode.getText() + "%' " + filter_grade + " "
                        + "AND `tgl_keluar` BETWEEN '" + dateFormat.format(Date_Search_Penjualan_1.getDate()) + "' AND '" + dateFormat.format(Date_Search_Penjualan_2.getDate()) + "'";
            } else {
                sql = "SELECT `tb_bahan_baku_keluar`.`kode_pengeluaran`, `jenis_pengeluaran`, `tb_bahan_baku_keluar`.`no_kartu_waleta`, `tb_bahan_baku_keluar`.`kode_grade`, `tgl_keluar`, `nama_customer`, `total_keping_keluar`, `total_berat_keluar`, `tb_grading_bahan_baku`.`harga_bahanbaku`,  (`total_berat_keluar`*`tb_grading_bahan_baku`.`harga_bahanbaku`) AS 'nilai'\n"
                        + "FROM `tb_bahan_baku_keluar` LEFT JOIN `tb_grading_bahan_baku` ON `tb_bahan_baku_keluar`.`no_kartu_waleta` = `tb_grading_bahan_baku`.`no_kartu_waleta` AND `tb_bahan_baku_keluar`.`kode_grade` = `tb_grading_bahan_baku`.`kode_grade`\n"
                        + "LEFT JOIN `tb_bahan_baku_keluar1` ON (`tb_bahan_baku_keluar`.`kode_pengeluaran` = `tb_bahan_baku_keluar1`.`kode_pengeluaran`)"
                        + "LEFT JOIN `tb_customer_baku` ON (`tb_bahan_baku_keluar1`.`customer_baku` = `tb_customer_baku`.`kode_cust`)"
                        + "WHERE `tb_bahan_baku_keluar`.`no_kartu_waleta` LIKE '%" + txt_search_kartu.getText() + "%' "
                        + "AND `tb_bahan_baku_keluar`.`kode_pengeluaran` LIKE '%" + txt_search_kode.getText() + "%' " + filter_grade + "";
            }
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[10];
            while (rs.next()) {
                row[0] = rs.getString("kode_pengeluaran");
                row[1] = rs.getString("jenis_pengeluaran");
                row[2] = rs.getString("no_kartu_waleta");
                row[3] = rs.getString("kode_grade");
                row[4] = rs.getDate("tgl_keluar");
                row[5] = rs.getString("nama_customer");
                row[6] = rs.getInt("total_keping_keluar");
                total_kpg = total_kpg + rs.getInt("total_keping_keluar");
                row[7] = rs.getInt("total_berat_keluar");
                total_gram = total_gram + rs.getInt("total_berat_keluar");
                row[8] = decimalFormat.format(rs.getDouble("harga_bahanbaku"));
                row[9] = decimalFormat.format(rs.getDouble("nilai"));
                total_harga = total_harga + rs.getDouble("nilai");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_BahanBakuKeluar);

            int rowData = Table_BahanBakuKeluar.getRowCount();
            label_total_data_grade.setText(Integer.toString(rowData));
            label_total_keping_grade.setText(decimalFormat.format(total_kpg) + " Keping");
            label_total_gram_grade.setText(decimalFormat.format(total_gram) + " Grams");
            label_total_harga_beli_grade.setText(decimalFormat.format(total_harga));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Harga_BahanBakuKeluar.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_penjualan_baku() {
        try {
            decimalFormat.setMaximumFractionDigits(5);
            double total_gram = 0, total_kpg = 0, total_nilai_beli = 0, total_nilai_jual = 0;
            DefaultTableModel model = (DefaultTableModel) Table_BahanBakuKeluar2.getModel();
            model.setRowCount(0);
            String filter_tanggal = "";
            if (Date_Search_Penjualan_3.getDate() != null && Date_Search_Penjualan_4.getDate() != null) {
                filter_tanggal = "AND `tgl_keluar` BETWEEN '" + dateFormat.format(Date_Search_Penjualan_3.getDate()) + "' AND '" + dateFormat.format(Date_Search_Penjualan_4.getDate()) + "'";
            }
            sql = "SELECT `tb_bahan_baku_keluar1`.`kode_pengeluaran`, `jenis_pengeluaran`, `tgl_keluar`, `nama_customer`, `harga_penjualan_baku`, `tanggal_pelunasan`, "
                    + "SUM(`total_keping_keluar`) AS 'total_keping_keluar', SUM(`total_berat_keluar`) AS 'total_berat_keluar', SUM(`total_berat_keluar`*`tb_grading_bahan_baku`.`harga_bahanbaku`) AS 'nilai_beli'\n"
                    + "FROM `tb_bahan_baku_keluar1` "
                    + "LEFT JOIN `tb_bahan_baku_keluar` ON `tb_bahan_baku_keluar1`.`kode_pengeluaran` = `tb_bahan_baku_keluar`.`kode_pengeluaran` "
                    + "LEFT JOIN `tb_grading_bahan_baku` ON (`tb_bahan_baku_keluar`.`no_kartu_waleta` = `tb_grading_bahan_baku`.`no_kartu_waleta` AND `tb_bahan_baku_keluar`.`kode_grade` = `tb_grading_bahan_baku`.`kode_grade`) "
                    + "LEFT JOIN `tb_customer_baku` ON `tb_bahan_baku_keluar1`.`customer_baku` = `tb_customer_baku`.`kode_cust`"
                    + "WHERE"
                    + " `tb_bahan_baku_keluar`.`kode_pengeluaran` LIKE '%" + txt_search_kode.getText() + "%' "
                    + "AND `nama_customer` LIKE '%" + txt_search_nama_buyer.getText() + "%' "
                    + filter_tanggal
                    + " GROUP BY `tb_bahan_baku_keluar1`.`kode_pengeluaran`";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[15];
            while (rs.next()) {
                row[0] = rs.getString("kode_pengeluaran");
                row[1] = rs.getString("jenis_pengeluaran");
                row[2] = rs.getDate("tgl_keluar");
                row[3] = rs.getString("nama_customer");
                row[4] = rs.getInt("total_keping_keluar");
                row[5] = rs.getInt("total_berat_keluar");
                row[6] = rs.getDouble("nilai_beli");
                row[7] = rs.getDouble("harga_penjualan_baku");
                row[8] = rs.getDouble("harga_penjualan_baku") - rs.getDouble("nilai_beli");
                row[9] = rs.getDate("tanggal_pelunasan");
                model.addRow(row);
                total_kpg = total_kpg + rs.getInt("total_keping_keluar");
                total_gram = total_gram + rs.getInt("total_berat_keluar");
                total_nilai_beli = total_nilai_beli + rs.getDouble("nilai_beli");
                total_nilai_jual = total_nilai_jual + rs.getDouble("harga_penjualan_baku");
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_BahanBakuKeluar2);

            int rowData = Table_BahanBakuKeluar2.getRowCount();
            label_total_data_penjualan.setText(Integer.toString(rowData));
            label_total_keping_penjualan.setText(decimalFormat.format(total_kpg) + " Keping");
            label_total_gram_penjualan.setText(decimalFormat.format(total_gram) + " Grams");
            label_total_nilai_beli_penjualan.setText(decimalFormat.format(total_nilai_beli));
            label_total_nilai_jual_penjualan.setText(decimalFormat.format(total_nilai_jual));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Harga_BahanBakuKeluar.class.getName()).log(Level.SEVERE, null, ex);
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

        jPanel_laporan_produksi = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel28 = new javax.swing.JLabel();
        jPanel_search_laporan_produksi = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        txt_search_kode = new javax.swing.JTextField();
        button_refresh_data_detail_grade = new javax.swing.JButton();
        ComboBox_Search_grade = new javax.swing.JComboBox<>();
        Date_Search_Penjualan_1 = new com.toedter.calendar.JDateChooser();
        Date_Search_Penjualan_2 = new com.toedter.calendar.JDateChooser();
        jLabel16 = new javax.swing.JLabel();
        txt_search_kartu = new javax.swing.JTextField();
        button_export_LP = new javax.swing.JButton();
        jLabel27 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        Table_BahanBakuKeluar = new javax.swing.JTable();
        label_total_data_grade = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jScrollPane9 = new javax.swing.JScrollPane();
        Table_BahanBakuKeluar3 = new javax.swing.JTable();
        label_total_harga_beli_grade = new javax.swing.JLabel();
        label_total_gram_grade = new javax.swing.JLabel();
        label_total_keping_grade = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel29 = new javax.swing.JLabel();
        jPanel_search_laporan_produksi1 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        txt_search_kode_penjualan = new javax.swing.JTextField();
        button_refresh_data_penjualan_baku = new javax.swing.JButton();
        Date_Search_Penjualan_3 = new com.toedter.calendar.JDateChooser();
        Date_Search_Penjualan_4 = new com.toedter.calendar.JDateChooser();
        jLabel19 = new javax.swing.JLabel();
        txt_search_nama_buyer = new javax.swing.JTextField();
        button_export_penjualan_baku = new javax.swing.JButton();
        button_edit_harga = new javax.swing.JButton();
        button_edit_pelunasan = new javax.swing.JButton();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jScrollPane7 = new javax.swing.JScrollPane();
        Table_BahanBakuKeluar1 = new javax.swing.JTable();
        label_total_data_penjualan = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        Table_BahanBakuKeluar2 = new javax.swing.JTable();
        label_total_nilai_beli_penjualan = new javax.swing.JLabel();
        label_total_gram_penjualan = new javax.swing.JLabel();
        label_total_keping_penjualan = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        label_total_nilai_jual_penjualan = new javax.swing.JLabel();

        jPanel_laporan_produksi.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_laporan_produksi.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Harga Bahan Baku Keluar", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N
        jPanel_laporan_produksi.setPreferredSize(new java.awt.Dimension(1366, 700));

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel28.setBackground(new java.awt.Color(255, 255, 255));
        jLabel28.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel28.setText("Total Harga Beli :");

        jPanel_search_laporan_produksi.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_search_laporan_produksi.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        jPanel_search_laporan_produksi.setPreferredSize(new java.awt.Dimension(1334, 61));

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel13.setText("Kode Keluar :");

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel14.setText("Kode Grade :");

        jLabel34.setBackground(new java.awt.Color(255, 255, 255));
        jLabel34.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel34.setText("Sampai");

        jLabel35.setBackground(new java.awt.Color(255, 255, 255));
        jLabel35.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel35.setText("Date Filter :");

        txt_search_kode.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_kode.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_kodeKeyPressed(evt);
            }
        });

        button_refresh_data_detail_grade.setBackground(new java.awt.Color(255, 255, 255));
        button_refresh_data_detail_grade.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_refresh_data_detail_grade.setText("Search");
        button_refresh_data_detail_grade.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refresh_data_detail_gradeActionPerformed(evt);
            }
        });

        ComboBox_Search_grade.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_Search_grade.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));
        ComboBox_Search_grade.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ComboBox_Search_gradeItemStateChanged(evt);
            }
        });

        Date_Search_Penjualan_1.setBackground(new java.awt.Color(255, 255, 255));
        Date_Search_Penjualan_1.setToolTipText("");
        Date_Search_Penjualan_1.setDateFormatString("dd MMMM yyyy");
        Date_Search_Penjualan_1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Date_Search_Penjualan_1.setMinSelectableDate(new java.util.Date(1420048915000L));

        Date_Search_Penjualan_2.setBackground(new java.awt.Color(255, 255, 255));
        Date_Search_Penjualan_2.setDateFormatString("dd MMMM yyyy");
        Date_Search_Penjualan_2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel16.setText("No Kartu :");

        txt_search_kartu.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_kartu.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_kartuKeyPressed(evt);
            }
        });

        button_export_LP.setBackground(new java.awt.Color(255, 255, 255));
        button_export_LP.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_LP.setText("Export to Excel");
        button_export_LP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_LPActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_search_laporan_produksiLayout = new javax.swing.GroupLayout(jPanel_search_laporan_produksi);
        jPanel_search_laporan_produksi.setLayout(jPanel_search_laporan_produksiLayout);
        jPanel_search_laporan_produksiLayout.setHorizontalGroup(
            jPanel_search_laporan_produksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_search_laporan_produksiLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_search_kode, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_search_kartu, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(ComboBox_Search_grade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel35)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Date_Search_Penjualan_1, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel34)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Date_Search_Penjualan_2, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_refresh_data_detail_grade)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_export_LP)
                .addContainerGap(293, Short.MAX_VALUE))
        );
        jPanel_search_laporan_produksiLayout.setVerticalGroup(
            jPanel_search_laporan_produksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_search_laporan_produksiLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_search_laporan_produksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_Search_Penjualan_1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_Search_Penjualan_2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_refresh_data_detail_grade, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_kode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_Search_grade, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_kartu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export_LP, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel27.setBackground(new java.awt.Color(255, 255, 255));
        jLabel27.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel27.setText("Total Keping :");

        jLabel26.setBackground(new java.awt.Color(255, 255, 255));
        jLabel26.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel26.setText("Total Berat :");

        Table_BahanBakuKeluar.setAutoCreateRowSorter(true);
        Table_BahanBakuKeluar.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_BahanBakuKeluar.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode Keluar", "Jenis Pengeluaran", "No Kartu", "Grade", "Tgl Penjualan", "Buyer", "Kpg", "Berat", "Harga Satuan", "Nilai (Rp)"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Table_BahanBakuKeluar.getTableHeader().setReorderingAllowed(false);
        jScrollPane6.setViewportView(Table_BahanBakuKeluar);

        label_total_data_grade.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_grade.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        label_total_data_grade.setText("TOTAL");

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel15.setText("Total Data :");

        Table_BahanBakuKeluar3.setAutoCreateRowSorter(true);
        Table_BahanBakuKeluar3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_BahanBakuKeluar3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode Keluar", "Jenis Pengeluaran", "No Kartu", "Grade", "Tgl Penjualan", "Buyer", "Kpg", "Berat", "Harga Satuan", "Nilai (Rp)"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Table_BahanBakuKeluar3.getTableHeader().setReorderingAllowed(false);
        jScrollPane9.setViewportView(Table_BahanBakuKeluar3);
        if (Table_BahanBakuKeluar3.getColumnModel().getColumnCount() > 0) {
            Table_BahanBakuKeluar3.getColumnModel().getColumn(2).setHeaderValue("No Kartu");
            Table_BahanBakuKeluar3.getColumnModel().getColumn(3).setHeaderValue("Grade");
            Table_BahanBakuKeluar3.getColumnModel().getColumn(8).setHeaderValue("Harga Satuan");
        }

        label_total_harga_beli_grade.setBackground(new java.awt.Color(255, 255, 255));
        label_total_harga_beli_grade.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        label_total_harga_beli_grade.setText("TOTAL");

        label_total_gram_grade.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_grade.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        label_total_gram_grade.setText("TOTAL");

        label_total_keping_grade.setBackground(new java.awt.Color(255, 255, 255));
        label_total_keping_grade.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        label_total_keping_grade.setText("TOTAL");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane6)
                    .addComponent(jPanel_search_laporan_produksi, javax.swing.GroupLayout.DEFAULT_SIZE, 1346, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_data_grade)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel26)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gram_grade)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel27)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_keping_grade)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel28)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_harga_beli_grade)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jPanel_search_laporan_produksi, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(label_total_data_grade)
                    .addComponent(jLabel26)
                    .addComponent(label_total_gram_grade)
                    .addComponent(jLabel27)
                    .addComponent(label_total_keping_grade)
                    .addComponent(jLabel28)
                    .addComponent(label_total_harga_beli_grade))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 552, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Detail Grade Penjualan Baku", jPanel1);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel29.setBackground(new java.awt.Color(255, 255, 255));
        jLabel29.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel29.setText("Total Nilai Beli :");

        jPanel_search_laporan_produksi1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_search_laporan_produksi1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        jPanel_search_laporan_produksi1.setPreferredSize(new java.awt.Dimension(1334, 61));

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel17.setText("Kode Penjualan :");

        jLabel36.setBackground(new java.awt.Color(255, 255, 255));
        jLabel36.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel36.setText("Sampai");

        jLabel37.setBackground(new java.awt.Color(255, 255, 255));
        jLabel37.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel37.setText("Tanggal Penjualan Baku :");

        txt_search_kode_penjualan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_kode_penjualan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_kode_penjualanKeyPressed(evt);
            }
        });

        button_refresh_data_penjualan_baku.setBackground(new java.awt.Color(255, 255, 255));
        button_refresh_data_penjualan_baku.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_refresh_data_penjualan_baku.setText("Search");
        button_refresh_data_penjualan_baku.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refresh_data_penjualan_bakuActionPerformed(evt);
            }
        });

        Date_Search_Penjualan_3.setBackground(new java.awt.Color(255, 255, 255));
        Date_Search_Penjualan_3.setToolTipText("");
        Date_Search_Penjualan_3.setDateFormatString("dd MMMM yyyy");
        Date_Search_Penjualan_3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Date_Search_Penjualan_3.setMinSelectableDate(new java.util.Date(1420048915000L));

        Date_Search_Penjualan_4.setBackground(new java.awt.Color(255, 255, 255));
        Date_Search_Penjualan_4.setDateFormatString("dd MMMM yyyy");
        Date_Search_Penjualan_4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel19.setBackground(new java.awt.Color(255, 255, 255));
        jLabel19.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel19.setText("Nama Buyer :");

        txt_search_nama_buyer.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_nama_buyer.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_nama_buyerKeyPressed(evt);
            }
        });

        button_export_penjualan_baku.setBackground(new java.awt.Color(255, 255, 255));
        button_export_penjualan_baku.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_penjualan_baku.setText("Export to Excel");
        button_export_penjualan_baku.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_penjualan_bakuActionPerformed(evt);
            }
        });

        button_edit_harga.setBackground(new java.awt.Color(255, 255, 255));
        button_edit_harga.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_edit_harga.setText("Edit Harga jual");
        button_edit_harga.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_edit_hargaActionPerformed(evt);
            }
        });

        button_edit_pelunasan.setBackground(new java.awt.Color(255, 255, 255));
        button_edit_pelunasan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_edit_pelunasan.setText("Edit Tgl Pelunasan");
        button_edit_pelunasan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_edit_pelunasanActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_search_laporan_produksi1Layout = new javax.swing.GroupLayout(jPanel_search_laporan_produksi1);
        jPanel_search_laporan_produksi1.setLayout(jPanel_search_laporan_produksi1Layout);
        jPanel_search_laporan_produksi1Layout.setHorizontalGroup(
            jPanel_search_laporan_produksi1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_search_laporan_produksi1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_search_kode_penjualan, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_search_nama_buyer, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel37)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Date_Search_Penjualan_3, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel36)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Date_Search_Penjualan_4, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_refresh_data_penjualan_baku)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_edit_harga)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_edit_pelunasan)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_export_penjualan_baku)
                .addGap(80, 80, 80))
        );
        jPanel_search_laporan_produksi1Layout.setVerticalGroup(
            jPanel_search_laporan_produksi1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_search_laporan_produksi1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_search_laporan_produksi1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_Search_Penjualan_3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_Search_Penjualan_4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_refresh_data_penjualan_baku, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_kode_penjualan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_nama_buyer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export_penjualan_baku, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_edit_harga, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_edit_pelunasan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel30.setBackground(new java.awt.Color(255, 255, 255));
        jLabel30.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel30.setText("Total Keping :");

        jLabel31.setBackground(new java.awt.Color(255, 255, 255));
        jLabel31.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel31.setText("Total Berat :");

        Table_BahanBakuKeluar1.setAutoCreateRowSorter(true);
        Table_BahanBakuKeluar1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_BahanBakuKeluar1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode Keluar", "Jenis Pengeluaran", "No Kartu", "Grade", "Tgl Keluar", "Customer", "Kpg", "Berat", "Harga Satuan", "Nilai (Rp)"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Table_BahanBakuKeluar1.getTableHeader().setReorderingAllowed(false);
        jScrollPane7.setViewportView(Table_BahanBakuKeluar1);
        if (Table_BahanBakuKeluar1.getColumnModel().getColumnCount() > 0) {
            Table_BahanBakuKeluar1.getColumnModel().getColumn(2).setHeaderValue("No Kartu");
            Table_BahanBakuKeluar1.getColumnModel().getColumn(3).setHeaderValue("Grade");
            Table_BahanBakuKeluar1.getColumnModel().getColumn(8).setHeaderValue("Harga Satuan");
        }

        label_total_data_penjualan.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_penjualan.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        label_total_data_penjualan.setText("TOTAL");

        jLabel20.setBackground(new java.awt.Color(255, 255, 255));
        jLabel20.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel20.setText("Total Data :");

        Table_BahanBakuKeluar2.setAutoCreateRowSorter(true);
        Table_BahanBakuKeluar2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_BahanBakuKeluar2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode Penjualan", "Jenis Pengeluaran", "Tgl Keluar", "Buyer", "Kpg", "Berat", "Nilai Beli (Rp)", "Nilai Jual (Rp)", "Margin", "Pelunasan"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Table_BahanBakuKeluar2.getTableHeader().setReorderingAllowed(false);
        Table_BahanBakuKeluar2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Table_BahanBakuKeluar2MouseClicked(evt);
            }
        });
        jScrollPane8.setViewportView(Table_BahanBakuKeluar2);

        label_total_nilai_beli_penjualan.setBackground(new java.awt.Color(255, 255, 255));
        label_total_nilai_beli_penjualan.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        label_total_nilai_beli_penjualan.setText("TOTAL");

        label_total_gram_penjualan.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_penjualan.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        label_total_gram_penjualan.setText("TOTAL");

        label_total_keping_penjualan.setBackground(new java.awt.Color(255, 255, 255));
        label_total_keping_penjualan.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        label_total_keping_penjualan.setText("TOTAL");

        jLabel32.setBackground(new java.awt.Color(255, 255, 255));
        jLabel32.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel32.setText("Total Nilai Jual :");

        label_total_nilai_jual_penjualan.setBackground(new java.awt.Color(255, 255, 255));
        label_total_nilai_jual_penjualan.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        label_total_nilai_jual_penjualan.setText("TOTAL");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane8)
                    .addComponent(jPanel_search_laporan_produksi1, javax.swing.GroupLayout.DEFAULT_SIZE, 1346, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_data_penjualan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel31)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gram_penjualan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel30)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_keping_penjualan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel29)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_nilai_beli_penjualan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel32)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_nilai_jual_penjualan)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jPanel_search_laporan_produksi1, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(label_total_data_penjualan)
                    .addComponent(jLabel31)
                    .addComponent(label_total_gram_penjualan)
                    .addComponent(jLabel30)
                    .addComponent(label_total_keping_penjualan)
                    .addComponent(jLabel29)
                    .addComponent(label_total_nilai_beli_penjualan)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel32)
                        .addComponent(label_total_nilai_jual_penjualan)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 552, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Data Penjualan Bahan Baku", jPanel2);

        javax.swing.GroupLayout jPanel_laporan_produksiLayout = new javax.swing.GroupLayout(jPanel_laporan_produksi);
        jPanel_laporan_produksi.setLayout(jPanel_laporan_produksiLayout);
        jPanel_laporan_produksiLayout.setHorizontalGroup(
            jPanel_laporan_produksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        jPanel_laporan_produksiLayout.setVerticalGroup(
            jPanel_laporan_produksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jPanel_laporan_produksi, javax.swing.GroupLayout.DEFAULT_SIZE, 1381, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel_laporan_produksi, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txt_search_kodeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_kodeKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_detail_penjualan();
        }
    }//GEN-LAST:event_txt_search_kodeKeyPressed

    private void button_refresh_data_detail_gradeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refresh_data_detail_gradeActionPerformed
        // TODO add your handling code here:
        refreshTable_detail_penjualan();
    }//GEN-LAST:event_button_refresh_data_detail_gradeActionPerformed

    private void ComboBox_Search_gradeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ComboBox_Search_gradeItemStateChanged
        // TODO add your handling code here:
        refreshTable_detail_penjualan();
    }//GEN-LAST:event_ComboBox_Search_gradeItemStateChanged

    private void txt_search_kartuKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_kartuKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_detail_penjualan();
        }
    }//GEN-LAST:event_txt_search_kartuKeyPressed

    private void button_export_LPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_LPActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Table_BahanBakuKeluar.getModel();
        ExportToExcel.writeToExcel(model, this);
    }//GEN-LAST:event_button_export_LPActionPerformed

    private void txt_search_kode_penjualanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_kode_penjualanKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_penjualan_baku();
        }
    }//GEN-LAST:event_txt_search_kode_penjualanKeyPressed

    private void button_refresh_data_penjualan_bakuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refresh_data_penjualan_bakuActionPerformed
        // TODO add your handling code here:
        refreshTable_penjualan_baku();
    }//GEN-LAST:event_button_refresh_data_penjualan_bakuActionPerformed

    private void txt_search_nama_buyerKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_nama_buyerKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_penjualan_baku();
        }
    }//GEN-LAST:event_txt_search_nama_buyerKeyPressed

    private void button_export_penjualan_bakuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_penjualan_bakuActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Table_BahanBakuKeluar2.getModel();
        ExportToExcel.writeToExcel(model, this);
    }//GEN-LAST:event_button_export_penjualan_bakuActionPerformed

    private void button_edit_hargaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_edit_hargaActionPerformed
        // TODO add your handling code here:
        JOptionPane.showMessageDialog(this, "Klik 2x pada tabel untuk edit harga dan tanggal pelunasan");
    }//GEN-LAST:event_button_edit_hargaActionPerformed

    private void Table_BahanBakuKeluar2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Table_BahanBakuKeluar2MouseClicked
        // TODO add your handling code here:
        decimalFormat.setMaximumFractionDigits(5);
        decimalFormat.setGroupingUsed(false);
        int i = Table_BahanBakuKeluar2.getSelectedRow();
        if (evt.getClickCount() == 2) {
            try {
                String old_price = decimalFormat.format(Table_BahanBakuKeluar2.getValueAt(i, 7));
//                String price1 = "";
//                price1 = old_price.replace(",", "");
//                try {
//                    double price_2 = (double) decimalFormat.parse(price1);
//                } catch (ParseException e) {
//                    JOptionPane.showMessageDialog(this, "error");
//                    price1 = old_price.replace(".","");
//                    price1 = price1.replace(",", ".");
//                }

                String harga = JOptionPane.showInputDialog("Masukkan Harga : ", old_price);
                if (harga != null && !harga.equals("")) {
                    double HARGA_F = Double.valueOf(harga);
                    decimalFormat = Utility.DecimalFormatUS(decimalFormat);
                    sql = "UPDATE `tb_bahan_baku_keluar1` SET `harga_penjualan_baku`='" + decimalFormat.format(HARGA_F) + "' WHERE `kode_pengeluaran`='" + Table_BahanBakuKeluar2.getValueAt(i, 0).toString() + "'";
                    if ((Utility.db.getStatement().executeUpdate(sql)) == 1) {
                        refreshTable_penjualan_baku();
                        JOptionPane.showMessageDialog(this, "Update success!");
                    } else {
                        JOptionPane.showMessageDialog(this, "Update failed!");
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error Connection!");
                Logger.getLogger(JPanel_Harga_BahanBaku.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Input must be number!");
                Logger.getLogger(JPanel_Harga_BahanBaku.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_Table_BahanBakuKeluar2MouseClicked

    private void button_edit_pelunasanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_edit_pelunasanActionPerformed
        // TODO add your handling code here:
        int i = Table_BahanBakuKeluar2.getSelectedRow();
        if (i > -1) {
            String old_date = "";
            if (Table_BahanBakuKeluar2.getValueAt(i, 9) != null) {
                old_date = Table_BahanBakuKeluar2.getValueAt(i, 9).toString();
            }
            String tanggal_pelunasan = JOptionPane.showInputDialog("Masukkan Tanggal pelunasan (yyyy-MM-dd) : ", old_date);
            if (tanggal_pelunasan != null && !tanggal_pelunasan.equals("")) {
                try {
                    sql = "UPDATE `tb_bahan_baku_keluar1` SET `tanggal_pelunasan`='" + tanggal_pelunasan + "' WHERE `kode_pengeluaran`='" + Table_BahanBakuKeluar2.getValueAt(i, 0).toString() + "'";
                    if ((Utility.db.getStatement().executeUpdate(sql)) == 1) {
                        refreshTable_penjualan_baku();
                        JOptionPane.showMessageDialog(this, "Update success!");
                    } else {
                        JOptionPane.showMessageDialog(this, "Update failed!");
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, ex);
                    Logger.getLogger(JPanel_Harga_BahanBakuKeluar.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "pilih salah satu data");
        }
    }//GEN-LAST:event_button_edit_pelunasanActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_Search_grade;
    private com.toedter.calendar.JDateChooser Date_Search_Penjualan_1;
    private com.toedter.calendar.JDateChooser Date_Search_Penjualan_2;
    private com.toedter.calendar.JDateChooser Date_Search_Penjualan_3;
    private com.toedter.calendar.JDateChooser Date_Search_Penjualan_4;
    public static javax.swing.JTable Table_BahanBakuKeluar;
    public static javax.swing.JTable Table_BahanBakuKeluar1;
    public static javax.swing.JTable Table_BahanBakuKeluar2;
    public static javax.swing.JTable Table_BahanBakuKeluar3;
    private javax.swing.JButton button_edit_harga;
    private javax.swing.JButton button_edit_pelunasan;
    private javax.swing.JButton button_export_LP;
    private javax.swing.JButton button_export_penjualan_baku;
    private javax.swing.JButton button_refresh_data_detail_grade;
    private javax.swing.JButton button_refresh_data_penjualan_baku;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel_laporan_produksi;
    private javax.swing.JPanel jPanel_search_laporan_produksi;
    private javax.swing.JPanel jPanel_search_laporan_produksi1;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel label_total_data_grade;
    private javax.swing.JLabel label_total_data_penjualan;
    private javax.swing.JLabel label_total_gram_grade;
    private javax.swing.JLabel label_total_gram_penjualan;
    private javax.swing.JLabel label_total_harga_beli_grade;
    private javax.swing.JLabel label_total_keping_grade;
    private javax.swing.JLabel label_total_keping_penjualan;
    private javax.swing.JLabel label_total_nilai_beli_penjualan;
    private javax.swing.JLabel label_total_nilai_jual_penjualan;
    private javax.swing.JTextField txt_search_kartu;
    private javax.swing.JTextField txt_search_kode;
    private javax.swing.JTextField txt_search_kode_penjualan;
    private javax.swing.JTextField txt_search_nama_buyer;
    // End of variables declaration//GEN-END:variables
}
