package waleta_system.Finance;

import waleta_system.Class.Utility;
import java.awt.event.KeyEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.ExportToExcel;

public class JPanel_Harga_LaporanProduksi extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    float kadar_air_bahan_baku = 0;

    public JPanel_Harga_LaporanProduksi() {
        initComponents();
    }

    public void init() {
        refreshTable();
    }

    public void refreshTable() {
        try {
            double total_gram = 0, total_keping = 0, total_harga = 0;
            double total_biaya_baku_tambahan = 0, total_biaya_tenaga_kerja = 0, total_biaya_overhead = 0;
            DefaultTableModel model = (DefaultTableModel) Table_laporan_produksi.getModel();
            model.setRowCount(0);
            String filter_status = "";
            switch (ComboBox_Status_Grading.getSelectedIndex()) {
                case 0:
                    if (Date_Search_LP_1.getDate() != null && Date_Search_LP_2.getDate() != null) {
                        filter_status = "AND `tanggal_lp` BETWEEN '" + dateFormat.format(Date_Search_LP_1.getDate()) + "' and '" + dateFormat.format(Date_Search_LP_2.getDate()) + "'";
                    }
                    break;
                case 1:
                    if (Date_Search_LP_1.getDate() != null && Date_Search_LP_2.getDate() != null) {
                        filter_status = "AND (`tanggal_lp` BETWEEN '" + dateFormat.format(Date_Search_LP_1.getDate()) + "' AND '" + dateFormat.format(Date_Search_LP_2.getDate()) + "')"
                                + "AND (`tb_tutupan_grading`.`tgl_statusBox` IS NULL OR `tb_tutupan_grading`.`tgl_statusBox` > '" + dateFormat.format(Date_Search_LP_2.getDate()) + "')";
                    } else {
                        filter_status = "AND (`tb_tutupan_grading`.`tgl_statusBox` IS NULL)";
                    }
                    break;
                case 2:
                    if (Date_Search_LP_1.getDate() != null && Date_Search_LP_2.getDate() != null) {
                        filter_status = "AND `tb_tutupan_grading`.`status_box` IS NOT NULL AND (`tanggal_lp` BETWEEN '" + dateFormat.format(Date_Search_LP_1.getDate()) + "' and '" + dateFormat.format(Date_Search_LP_2.getDate()) + "')";
                    } else {
                        filter_status = "AND `tb_tutupan_grading`.`status_box` IS NOT NULL ";
                    }
                    break;
                default:
                    break;
            }
            String filter_ruangan = "";
            if (txt_search_ruangan.getText() != null && !txt_search_ruangan.getText().equals("")) {
                filter_ruangan = "AND `ruangan` = '" + txt_search_ruangan.getText() + "' ";
            }

            sql = "SELECT `no_laporan_produksi`, `tb_laporan_produksi`.`no_kartu_waleta`, `tanggal_lp`, `tb_laporan_produksi`.`kode_grade`, `berat_basah`, `tb_laporan_produksi`.`jumlah_keping`, "
                    + "`harga_bahanbaku`, `biaya_baku_tambahan_lp`, `biaya_tenaga_kerja_lp`, `biaya_overhead_lp`, "
                    + "BIAYA.`biaya_baku_tambahan_sebulan`, BIAYA.`biaya_tenaga_kerja_sebulan`, BIAYA.`biaya_overhead_sebulan`, LP.`total_pengeluaran_lp`, "
                    + "`tanggal_grading`, `tb_tutupan_grading`.`tgl_statusBox`, `tb_bahan_jadi_masuk`.`kode_tutupan` "
                    + "FROM `tb_laporan_produksi` "
                    + "LEFT JOIN `tb_bahan_jadi_masuk` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_bahan_jadi_masuk`.`kode_asal`"
                    + "LEFT JOIN `tb_tutupan_grading` ON `tb_bahan_jadi_masuk`.`kode_tutupan` = `tb_tutupan_grading`.`kode_tutupan` "
                    + "LEFT JOIN `tb_grading_bahan_baku` ON `tb_laporan_produksi`.`no_kartu_waleta` = `tb_grading_bahan_baku`.`no_kartu_waleta` AND `tb_laporan_produksi`.`kode_grade` = `tb_grading_bahan_baku`.`kode_grade` "
                    + "LEFT JOIN "
                    + "(SELECT YEAR(`bulan`) AS 'tahun', MONTH(`bulan`) AS 'bulan', "
                    + "SUM(IF(`Kategori1` = 'Biaya Bahan Baku', `nilai`, 0)) AS 'biaya_baku_tambahan_sebulan', \n"
                    + "SUM(IF(`Kategori1` = 'Biaya Tenaga Kerja Langsung', `nilai`, 0)) AS 'biaya_tenaga_kerja_sebulan', \n"
                    + "SUM(IF(`Kategori1` = 'Biaya Overhead Pabrik', `nilai`, 0)) AS 'biaya_overhead_sebulan' \n"
                    + "FROM `tb_biaya_pabrik` \n"
                    + "WHERE 1 GROUP BY YEAR(`bulan`), MONTH(`bulan`)) "
                    + "BIAYA ON YEAR(`tb_laporan_produksi`.`tanggal_lp`) = BIAYA.`tahun` AND MONTH(`tb_laporan_produksi`.`tanggal_lp`) = BIAYA.`bulan` "
                    + "LEFT JOIN "
                    + "(SELECT YEAR(`tanggal_lp`) AS 'tahun', MONTH(`tanggal_lp`) AS 'bulan', SUM(`berat_basah`) AS 'total_pengeluaran_lp'\n"
                    + "FROM `tb_laporan_produksi` \n"
                    + "WHERE 1 GROUP BY YEAR(`tanggal_lp`), MONTH(`tanggal_lp`)) "
                    + "LP ON YEAR(`tb_laporan_produksi`.`tanggal_lp`) = LP.`tahun` AND MONTH(`tb_laporan_produksi`.`tanggal_lp`) = LP.`bulan` "
                    + "WHERE "
                    + "`no_laporan_produksi` LIKE '%" + txt_search_lp.getText() + "%' "
                    + "AND `tb_laporan_produksi`.`kode_grade` LIKE '%" + txt_search_grade.getText() + "%' "
                    + filter_ruangan
                    + filter_status
                    + " ORDER BY `no_laporan_produksi` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[20];
            while (rs.next()) {
                row[0] = rs.getString("no_laporan_produksi");
                row[1] = rs.getDate("tanggal_lp");
                row[2] = rs.getDate("tgl_statusBox");
                row[3] = rs.getString("kode_tutupan");
                row[4] = rs.getString("no_kartu_waleta");
                row[5] = rs.getString("kode_grade");
                row[6] = rs.getDouble("jumlah_keping");
                row[7] = rs.getDouble("berat_basah");
                row[8] = rs.getDouble("harga_bahanbaku");
                double harga = rs.getDouble("berat_basah") * rs.getDouble("harga_bahanbaku");
                double biaya_baku_tambahan_per_gram = rs.getDouble("biaya_baku_tambahan_sebulan") / rs.getDouble("total_pengeluaran_lp");
                double biaya_tenaga_kerja_per_gram = rs.getDouble("biaya_tenaga_kerja_sebulan") / rs.getDouble("total_pengeluaran_lp");
                double biaya_overhead_per_gram = rs.getDouble("biaya_overhead_sebulan") / rs.getDouble("total_pengeluaran_lp");
                row[9] = Math.round(harga * 10d) / 10d;
                row[10] = Math.round(biaya_baku_tambahan_per_gram * 10d) / 10d;
                row[11] = rs.getDouble("biaya_baku_tambahan_lp");
                row[12] = Math.round(biaya_tenaga_kerja_per_gram * 10d) / 10d;
                row[13] = rs.getDouble("biaya_tenaga_kerja_lp");
                row[14] = Math.round(biaya_overhead_per_gram * 10d) / 10d;
                row[15] = rs.getDouble("biaya_overhead_lp");
                model.addRow(row);
                total_gram = total_gram + rs.getFloat("berat_basah");
                total_keping = total_keping + rs.getInt("jumlah_keping");
                total_harga = total_harga + harga;
                total_biaya_baku_tambahan = total_biaya_baku_tambahan + rs.getDouble("biaya_baku_tambahan_lp");
                total_biaya_tenaga_kerja = total_biaya_tenaga_kerja + rs.getDouble("biaya_tenaga_kerja_lp");
                total_biaya_overhead = total_biaya_overhead + rs.getDouble("biaya_overhead_lp");
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_laporan_produksi);
            decimalFormat.setMaximumFractionDigits(5);
            int rowData = Table_laporan_produksi.getRowCount();
            label_total_data_laporan_produksi.setText(Integer.toString(rowData));
            label_total_gram_LP.setText(decimalFormat.format(total_gram) + " Grams");
            label_total_keping_LP.setText(decimalFormat.format(total_keping) + " Keping");
            label_total_harga_baku.setText("Rp. " + decimalFormat.format(total_harga) + ",-");
            label_total_biaya_baku_tambahan.setText("Rp. " + decimalFormat.format(total_biaya_baku_tambahan) + ",-");
            label_total_biaya_tenaga_kerja.setText("Rp. " + decimalFormat.format(total_biaya_tenaga_kerja) + ",-");
            label_total_biaya_overhead.setText("Rp. " + decimalFormat.format(total_biaya_overhead) + ",-");

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_Harga_LaporanProduksi.class.getName()).log(Level.SEVERE, null, ex);
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
        jScrollPane6 = new javax.swing.JScrollPane();
        Table_laporan_produksi = new javax.swing.JTable();
        jLabel15 = new javax.swing.JLabel();
        label_total_data_laporan_produksi = new javax.swing.JLabel();
        label_total_gram_LP = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        label_total_keping_LP = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        button_export = new javax.swing.JButton();
        jLabel28 = new javax.swing.JLabel();
        label_total_harga_baku = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        label_total_biaya_tenaga_kerja = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        label_total_biaya_overhead = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        label_total_biaya_baku_tambahan = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        Date_Search_LP_2 = new com.toedter.calendar.JDateChooser();
        button_search_lp = new javax.swing.JButton();
        txt_search_lp = new javax.swing.JTextField();
        jLabel35 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        txt_search_ruangan = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        txt_search_grade = new javax.swing.JTextField();
        ComboBox_Status_Grading = new javax.swing.JComboBox<>();
        jLabel17 = new javax.swing.JLabel();
        Date_Search_LP_1 = new com.toedter.calendar.JDateChooser();
        button_input_biaya_lp = new javax.swing.JButton();

        jPanel_laporan_produksi.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_laporan_produksi.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Harga Laporan Produksi", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N
        jPanel_laporan_produksi.setPreferredSize(new java.awt.Dimension(1366, 700));

        Table_laporan_produksi.setAutoCreateRowSorter(true);
        Table_laporan_produksi.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_laporan_produksi.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No LP", "Tgl LP", "Tgl Selesai Box", "No Tutupan", "No Kartu", "Grade", "Kpg", "Berat", "Harga Satuan", "Nilai Baku (Rp)", "Biaya Baku+/Gr", "Biaya Baku+", "Biaya TK/Gr", "Biaya TK", "Biaya Overhead/Gr", "Biaya Overhead"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Table_laporan_produksi.getTableHeader().setReorderingAllowed(false);
        jScrollPane6.setViewportView(Table_laporan_produksi);

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel15.setText("Total Data :");

        label_total_data_laporan_produksi.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_laporan_produksi.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        label_total_data_laporan_produksi.setText("TOTAL");

        label_total_gram_LP.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_LP.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        label_total_gram_LP.setText("TOTAL");

        jLabel26.setBackground(new java.awt.Color(255, 255, 255));
        jLabel26.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel26.setText("Total Berat :");

        label_total_keping_LP.setBackground(new java.awt.Color(255, 255, 255));
        label_total_keping_LP.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        label_total_keping_LP.setText("TOTAL");

        jLabel27.setBackground(new java.awt.Color(255, 255, 255));
        jLabel27.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel27.setText("Total Keping :");

        button_export.setBackground(new java.awt.Color(255, 255, 255));
        button_export.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export.setText("Export to excel");
        button_export.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_exportActionPerformed(evt);
            }
        });

        jLabel28.setBackground(new java.awt.Color(255, 255, 255));
        jLabel28.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel28.setText("Total Harga Baku :");

        label_total_harga_baku.setBackground(new java.awt.Color(255, 255, 255));
        label_total_harga_baku.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        label_total_harga_baku.setText("TOTAL");

        jLabel29.setBackground(new java.awt.Color(255, 255, 255));
        jLabel29.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel29.setText("Total Bi Tenaga Kerja :");

        label_total_biaya_tenaga_kerja.setBackground(new java.awt.Color(255, 255, 255));
        label_total_biaya_tenaga_kerja.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        label_total_biaya_tenaga_kerja.setText("TOTAL");

        jLabel30.setBackground(new java.awt.Color(255, 255, 255));
        jLabel30.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel30.setText("Total Bi. Overhead :");

        label_total_biaya_overhead.setBackground(new java.awt.Color(255, 255, 255));
        label_total_biaya_overhead.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        label_total_biaya_overhead.setText("TOTAL");

        jLabel31.setBackground(new java.awt.Color(255, 255, 255));
        jLabel31.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel31.setText("Total Bi.Baku Tambahan :");

        label_total_biaya_baku_tambahan.setBackground(new java.awt.Color(255, 255, 255));
        label_total_biaya_baku_tambahan.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        label_total_biaya_baku_tambahan.setText("TOTAL");

        jLabel16.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel16.setText("Ruangan :");

        Date_Search_LP_2.setBackground(new java.awt.Color(255, 255, 255));
        Date_Search_LP_2.setDate(new Date());
        Date_Search_LP_2.setDateFormatString("dd MMM yyyy");
        Date_Search_LP_2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        button_search_lp.setBackground(new java.awt.Color(255, 255, 255));
        button_search_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search_lp.setText("Search");
        button_search_lp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_search_lpActionPerformed(evt);
            }
        });

        txt_search_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_lp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_lpKeyPressed(evt);
            }
        });

        jLabel35.setBackground(new java.awt.Color(255, 255, 255));
        jLabel35.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel35.setText("Tanggal :");

        jLabel34.setBackground(new java.awt.Color(255, 255, 255));
        jLabel34.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel34.setText("Sampai");

        jLabel14.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel14.setText("Grade :");

        txt_search_ruangan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_ruangan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_ruanganKeyPressed(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel13.setText("No LP :");

        txt_search_grade.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_grade.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_gradeKeyPressed(evt);
            }
        });

        ComboBox_Status_Grading.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_Status_Grading.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "WIP", "BARANG JADI" }));
        ComboBox_Status_Grading.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ComboBox_Status_GradingItemStateChanged(evt);
            }
        });

        jLabel17.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel17.setText("Status LP :");

        Date_Search_LP_1.setBackground(new java.awt.Color(255, 255, 255));
        Date_Search_LP_1.setToolTipText("");
        Date_Search_LP_1.setDate(new Date(new Date().getTime()-(7 * 24 * 60 * 60 * 1000)));
        Date_Search_LP_1.setDateFormatString("dd MMM yyyy");
        Date_Search_LP_1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Date_Search_LP_1.setMinSelectableDate(new java.util.Date(1420048915000L));

        button_input_biaya_lp.setBackground(new java.awt.Color(255, 255, 255));
        button_input_biaya_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_input_biaya_lp.setText("Input Biaya LP");
        button_input_biaya_lp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_input_biaya_lpActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_laporan_produksiLayout = new javax.swing.GroupLayout(jPanel_laporan_produksi);
        jPanel_laporan_produksi.setLayout(jPanel_laporan_produksiLayout);
        jPanel_laporan_produksiLayout.setHorizontalGroup(
            jPanel_laporan_produksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_laporan_produksiLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jScrollPane6)
                .addGap(10, 10, 10))
            .addGroup(jPanel_laporan_produksiLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_laporan_produksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_laporan_produksiLayout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_grade, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_ruangan, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_Status_Grading, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel35)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_Search_LP_1, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel34)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_Search_LP_2, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_search_lp)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_export)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_input_biaya_lp))
                    .addGroup(jPanel_laporan_produksiLayout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_data_laporan_produksi)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel26)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gram_LP)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel27)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_keping_LP)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel28)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_harga_baku)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel31)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_biaya_baku_tambahan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel29)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_biaya_tenaga_kerja)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel30)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_biaya_overhead)))
                .addContainerGap(96, Short.MAX_VALUE))
        );
        jPanel_laporan_produksiLayout.setVerticalGroup(
            jPanel_laporan_produksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_laporan_produksiLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_laporan_produksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_Status_Grading, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_Search_LP_1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_Search_LP_2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_search_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_grade, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_ruangan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_input_biaya_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_laporan_produksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel26)
                    .addComponent(label_total_gram_LP)
                    .addComponent(label_total_data_laporan_produksi)
                    .addComponent(jLabel15)
                    .addComponent(label_total_harga_baku)
                    .addComponent(jLabel28)
                    .addComponent(jLabel27)
                    .addComponent(label_total_keping_LP)
                    .addComponent(label_total_biaya_baku_tambahan)
                    .addComponent(jLabel31)
                    .addComponent(label_total_biaya_tenaga_kerja)
                    .addComponent(jLabel29)
                    .addComponent(label_total_biaya_overhead)
                    .addComponent(jLabel30))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 605, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel_laporan_produksi, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel_laporan_produksi, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txt_search_lpKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_lpKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_search_lpKeyPressed

    private void button_search_lpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_lpActionPerformed
        // TODO add your handling code here:
        refreshTable();
    }//GEN-LAST:event_button_search_lpActionPerformed

    private void ComboBox_Status_GradingItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ComboBox_Status_GradingItemStateChanged
        // TODO add your handling code here:
        refreshTable();
    }//GEN-LAST:event_ComboBox_Status_GradingItemStateChanged

    private void txt_search_gradeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_gradeKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_search_gradeKeyPressed

    private void txt_search_ruanganKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_ruanganKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_search_ruanganKeyPressed

    private void button_exportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_exportActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Table_laporan_produksi.getModel();
        ExportToExcel.writeToExcel(model, this);
    }//GEN-LAST:event_button_exportActionPerformed

    private void button_input_biaya_lpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_input_biaya_lpActionPerformed
        // TODO add your handling code here:
        try {
            int dialogResult = JOptionPane.showConfirmDialog(this, "Input biaya LP untuk yang data biaya sudah masuk, Lanjutkan??", "Warning", 0);
            if (dialogResult == JOptionPane.YES_OPTION) {
                int rowCount = Table_laporan_produksi.getRowCount();
                final JDialog_ProgressDialog dialog = new JDialog_ProgressDialog();
                dialog.pack();
                dialog.setLocationRelativeTo(this);
                dialog.setVisible(true);
                dialog.setEnabled(true);
                dialog.Label_title.setText("Mohon tunggu sebentar");
                dialog.jProgressBar.setMinimum(0);
                dialog.jProgressBar.setMaximum(rowCount);

                Thread updateThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            for (int i = 0; i < Table_laporan_produksi.getRowCount(); i++) {
                                String noLP = Table_laporan_produksi.getValueAt(i, 0).toString();
                                double beratBasahLP = (double) Table_laporan_produksi.getValueAt(i, 7);
                                double biayaBakuTambahan = (double) Table_laporan_produksi.getValueAt(i, 10) * beratBasahLP;
                                double biayaTK = (double) Table_laporan_produksi.getValueAt(i, 12) * beratBasahLP;
                                double biayaOverhead = (double) Table_laporan_produksi.getValueAt(i, 14) * beratBasahLP;

                                String query = "UPDATE `tb_laporan_produksi` SET "
                                        + "`biaya_baku_tambahan_lp` = ?, "
                                        + "`biaya_tenaga_kerja_lp` = ?, "
                                        + "`biaya_overhead_lp` = ? "
                                        + "WHERE `no_laporan_produksi` = ?";

                                PreparedStatement statement = Utility.db.getConnection().prepareStatement(query);
                                statement.setDouble(1, biayaBakuTambahan);
                                statement.setDouble(2, biayaTK);
                                statement.setDouble(3, biayaOverhead);
                                statement.setString(4, noLP);

                                statement.executeUpdate();
                                dialog.jProgressBar.setValue(i);
//                                Thread.sleep(30);
                            }

                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.dispose();
                                    refreshTable();
                                    JOptionPane.showMessageDialog(jPanel_laporan_produksi, "Data berhasil diubah");
                                }
                            });
                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(jPanel_laporan_produksi, ex);
                            Logger.getLogger(JPanel_Harga_LaporanProduksi.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                });

                updateThread.start();
            }
        } catch (Exception ex) {
            Logger.getLogger(JPanel_Harga_LaporanProduksi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_input_biaya_lpActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_Status_Grading;
    private com.toedter.calendar.JDateChooser Date_Search_LP_1;
    private com.toedter.calendar.JDateChooser Date_Search_LP_2;
    public static javax.swing.JTable Table_laporan_produksi;
    private javax.swing.JButton button_export;
    private javax.swing.JButton button_input_biaya_lp;
    private javax.swing.JButton button_search_lp;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JPanel jPanel_laporan_produksi;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JLabel label_total_biaya_baku_tambahan;
    private javax.swing.JLabel label_total_biaya_overhead;
    private javax.swing.JLabel label_total_biaya_tenaga_kerja;
    private javax.swing.JLabel label_total_data_laporan_produksi;
    private javax.swing.JLabel label_total_gram_LP;
    private javax.swing.JLabel label_total_harga_baku;
    private javax.swing.JLabel label_total_keping_LP;
    private javax.swing.JTextField txt_search_grade;
    private javax.swing.JTextField txt_search_lp;
    private javax.swing.JTextField txt_search_ruangan;
    // End of variables declaration//GEN-END:variables
}
