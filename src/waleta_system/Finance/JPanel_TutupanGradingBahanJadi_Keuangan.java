package waleta_system.Finance;

import java.awt.event.KeyEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
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

public class JPanel_TutupanGradingBahanJadi_Keuangan extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();

    public JPanel_TutupanGradingBahanJadi_Keuangan() {
        initComponents();
    }

    public void init() {
        try {

            refreshTable_Tutupan();
            Table_TutupanGrading.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent event) {
                    if (!event.getValueIsAdjusting() && Table_TutupanGrading.getSelectedRow() != -1) {
                        int row = Table_TutupanGrading.getSelectedRow();
                        String kode_tutupan = Table_TutupanGrading.getValueAt(row, 0).toString();
                        label_KodeAsal.setText(kode_tutupan);
                        label_KodeAsal1.setText(kode_tutupan);
                        refreshTable_Asal(kode_tutupan);
                        refreshTable_rincianBox(kode_tutupan);
                    }
                }
            });
        } catch (Exception ex) {
            Logger.getLogger(JPanel_GradeBahanBaku.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_Tutupan() {
        try {
            double total_biaya_tutupan = 0;
            DefaultTableModel model = (DefaultTableModel) Table_TutupanGrading.getModel();
            model.setRowCount(0);

            HashMap<String, Integer> gramBox_per_tutupan = new HashMap<>();
            HashMap<String, Double> HppBox_per_tutupan = new HashMap<>();
            sql = "SELECT `no_tutupan`, SUM(`berat`) AS 'berat_box', SUM(`hpp_box`) AS 'hpp_box' "
                    + "FROM `tb_box_bahan_jadi` WHERE 1 GROUP BY `no_tutupan`";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                gramBox_per_tutupan.put(rs.getString("no_tutupan"), rs.getInt("berat_box"));
                HppBox_per_tutupan.put(rs.getString("no_tutupan"), rs.getDouble("hpp_box"));
            }

            String filter_tanggal = "";
            if (Date_filter1.getDate() != null && Date_filter2.getDate() != null) {
                filter_tanggal = "AND `tb_tutupan_grading`.`tgl_statusBox` BETWEEN '" + dateFormat.format(Date_filter1.getDate()) + "' AND '" + dateFormat.format(Date_filter2.getDate()) + "' ";
            }
            sql = "SELECT `tb_bahan_jadi_masuk`.`kode_tutupan`, `tb_tutupan_grading`.`tgl_statusBox`,\n"
                    + "COUNT(`tb_laporan_produksi`.`no_laporan_produksi`) AS 'jumlah_lp',\n"
                    + "SUM(IF(`tb_laporan_produksi`.`harga_baku_lp`>0 AND (`biaya_baku_tambahan_lp`>0 OR `biaya_tenaga_kerja_lp`>0 OR `biaya_overhead_lp`>0),1,0)) AS 'jumlah_lengkap',\n"
                    + "SUM(`tb_laporan_produksi`.`harga_baku_lp`) AS 'harga_baku_lp',\n"
                    + "SUM(`tb_laporan_produksi`.`biaya_baku_tambahan_lp`) AS 'biaya_baku_tambahan_lp',\n"
                    + "SUM(`tb_laporan_produksi`.`biaya_tenaga_kerja_lp`) AS 'biaya_tenaga_kerja_lp',\n"
                    + "SUM(`tb_laporan_produksi`.`biaya_overhead_lp`) AS 'biaya_overhead_lp'\n"
                    + "FROM `tb_bahan_jadi_masuk` \n"
                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_bahan_jadi_masuk`.`kode_asal` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_tutupan_grading` ON `tb_bahan_jadi_masuk`.`kode_tutupan` = `tb_tutupan_grading`.`kode_tutupan`\n"
                    + "WHERE \n"
                    + "`tb_laporan_produksi`.`no_laporan_produksi` IS NOT NULL \n"
                    + "AND `tb_bahan_jadi_masuk`.`kode_tutupan` IS NOT NULL "
                    + "AND `tb_bahan_jadi_masuk`.`kode_tutupan` LIKE '%" + txt_search_kodeTutupan.getText() + "%' "
                    + filter_tanggal
                    + "GROUP BY `tb_bahan_jadi_masuk`.`kode_tutupan` "
                    + "ORDER BY `tb_tutupan_grading`.`tgl_statusBox` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] baris = new Object[15];
            while (rs.next()) {
                baris[0] = rs.getString("kode_tutupan");
                baris[1] = rs.getDate("tgl_statusBox");
                baris[2] = rs.getDouble("harga_baku_lp");
                baris[3] = rs.getDouble("biaya_baku_tambahan_lp");
                baris[4] = rs.getDouble("biaya_tenaga_kerja_lp");
                baris[5] = rs.getDouble("biaya_overhead_lp");
                double persentase_lengkap = rs.getDouble("jumlah_lengkap") / rs.getDouble("jumlah_lp") * 100d;
                baris[6] = Math.round(persentase_lengkap * 100d) / 100d;
                double total_hpp_tutupan = rs.getDouble("harga_baku_lp") + rs.getDouble("biaya_baku_tambahan_lp") + rs.getDouble("biaya_tenaga_kerja_lp") + rs.getDouble("biaya_overhead_lp");
                baris[7] = Math.round(total_hpp_tutupan * 100000d) / 100000d;
                double total_gram_box = gramBox_per_tutupan.getOrDefault(rs.getString("kode_tutupan"), 0);
                baris[8] = total_gram_box;
                double hpp_per_gram_box = total_hpp_tutupan / total_gram_box;
                baris[9] = Math.round(hpp_per_gram_box * 100000d) / 100000d;
                baris[10] = HppBox_per_tutupan.getOrDefault(rs.getString("kode_tutupan"), 0d);

                total_biaya_tutupan = total_biaya_tutupan + total_hpp_tutupan;
                model.addRow(baris);
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_TutupanGrading);

            decimalFormat.setMaximumFractionDigits(0);
            int rowData = Table_TutupanGrading.getRowCount();
            label_total_data.setText(decimalFormat.format(rowData));
            label_total_hpp_tutupan.setText(decimalFormat.format(total_biaya_tutupan));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_TutupanGradingBahanJadi_Keuangan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_Asal(String kode_tutupan) {
        try {
            int total_keping = 0, total_berat = 0;
            DefaultTableModel model = (DefaultTableModel) Table_Detail_Asal.getModel();
            model.setRowCount(0);
            sql = "SELECT `kode_asal`, `tanggal_masuk`, `keping`, `berat`, `tanggal_grading`, `kode_tutupan` "
                    + "FROM `tb_bahan_jadi_masuk` "
                    + "WHERE `kode_tutupan` = '" + kode_tutupan + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[6];
            while (rs.next()) {
                row[0] = rs.getString("kode_asal");
                row[1] = rs.getInt("keping");
                row[2] = rs.getInt("berat");
                model.addRow(row);
                total_keping = total_keping + rs.getInt("keping");
                total_berat = total_berat + rs.getInt("berat");
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_Detail_Asal);
            int total_data = Table_Detail_Asal.getRowCount();
            label_total_asal_detail_asal.setText(decimalFormat.format(total_data));
            label_total_kpg_detail_asal.setText(decimalFormat.format(total_keping));
            label_total_gram_detail_asal.setText(decimalFormat.format(total_berat));

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_TutupanGradingBahanJadi_Keuangan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_rincianBox(String kode_tutupan) {
        try {
            int total_kpg = 0;
            float total_gram = 0;
            DefaultTableModel model = (DefaultTableModel) Table_RincianBox.getModel();
            model.setRowCount(0);
            sql = "SELECT `no_box`, `kode_grade`, `keping`, `berat`, `lokasi_terakhir`, `hpp_box` "
                    + "FROM `tb_box_bahan_jadi` "
                    + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_box_bahan_jadi`.`kode_grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode` \n"
                    + "WHERE `no_tutupan` = '" + kode_tutupan + "' ";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                Object[] row = new Object[10];
                row[0] = rs.getString("no_box");
                row[1] = rs.getString("kode_grade");
                row[2] = rs.getInt("keping");
                row[3] = rs.getFloat("berat");
                row[4] = rs.getString("lokasi_terakhir");
                row[5] = rs.getDouble("hpp_box");
                model.addRow(row);

                total_kpg += rs.getInt("keping");
                total_gram += rs.getFloat("berat");
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_RincianBox);
            int total_data = Table_RincianBox.getRowCount();
            label_total_rincian_box.setText(decimalFormat.format(total_data));
            label_total_kpg_rincianBox.setText(decimalFormat.format(total_kpg));
            label_total_gram_rincianBox.setText(decimalFormat.format(total_gram));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_TutupanGradingBahanJadi_Keuangan.class.getName()).log(Level.SEVERE, null, ex);
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
        jPanel_Data_Tutupan = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txt_search_kodeTutupan = new javax.swing.JTextField();
        button_search = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        Date_filter1 = new com.toedter.calendar.JDateChooser();
        Date_filter2 = new com.toedter.calendar.JDateChooser();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        Table_TutupanGrading = new javax.swing.JTable();
        label_total_data = new javax.swing.JLabel();
        label_total_hpp_tutupan = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        button_export_Tutupan = new javax.swing.JButton();
        button_set_hpp_box = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        Table_RincianBox = new javax.swing.JTable();
        label_total_gram_rincianBox = new javax.swing.JLabel();
        label_total_kpg_rincianBox = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        label_total_rincian_box = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        label_KodeAsal1 = new javax.swing.JLabel();
        button_export_hasil_box = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        label_KodeAsal = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        Table_Detail_Asal = new javax.swing.JTable();
        label_total_asal_detail_asal = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        label_total_kpg_detail_asal = new javax.swing.JLabel();
        label_total_gram_detail_asal = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        button_export_LPTutupan = new javax.swing.JButton();

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Data Tutupan Grading", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N

        jPanel_Data_Tutupan.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("Kode Tutupan Grading :");

        txt_search_kodeTutupan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_kodeTutupan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_kodeTutupanKeyPressed(evt);
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

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("Tanggal selesai Box :");

        Date_filter1.setBackground(new java.awt.Color(255, 255, 255));
        Date_filter1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Date_filter2.setBackground(new java.awt.Color(255, 255, 255));
        Date_filter2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("Total Data Tutupan :");

        Table_TutupanGrading.setAutoCreateRowSorter(true);
        Table_TutupanGrading.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_TutupanGrading.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode Tutupan", "Tgl Selesai Box", "Harga Baku", "Biaya Tambahan Baku", "Biaya TK", "Biaya Overhead", "% Lengkap", "Total Hpp Tutupan", "Total Gram Box", "Hpp / Gr", "Total Hpp Box"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Table_TutupanGrading.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(Table_TutupanGrading);

        label_total_data.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_data.setText("0");

        label_total_hpp_tutupan.setBackground(new java.awt.Color(255, 255, 255));
        label_total_hpp_tutupan.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_hpp_tutupan.setText("0");

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel7.setText("Total Biaya :");

        button_export_Tutupan.setBackground(new java.awt.Color(255, 255, 255));
        button_export_Tutupan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_Tutupan.setText("Export");
        button_export_Tutupan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_TutupanActionPerformed(evt);
            }
        });

        button_set_hpp_box.setBackground(new java.awt.Color(255, 255, 255));
        button_set_hpp_box.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_set_hpp_box.setText("Set HPP Box");
        button_set_hpp_box.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_set_hpp_boxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_Data_TutupanLayout = new javax.swing.GroupLayout(jPanel_Data_Tutupan);
        jPanel_Data_Tutupan.setLayout(jPanel_Data_TutupanLayout);
        jPanel_Data_TutupanLayout.setHorizontalGroup(
            jPanel_Data_TutupanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Data_TutupanLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_Data_TutupanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel_Data_TutupanLayout.createSequentialGroup()
                        .addGroup(jPanel_Data_TutupanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_Data_TutupanLayout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_kodeTutupan, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_filter1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_filter2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_search))
                            .addGroup(jPanel_Data_TutupanLayout.createSequentialGroup()
                                .addComponent(button_set_hpp_box)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_export_Tutupan))
                            .addGroup(jPanel_Data_TutupanLayout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_data)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_hpp_tutupan)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel_Data_TutupanLayout.setVerticalGroup(
            jPanel_Data_TutupanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_Data_TutupanLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_Data_TutupanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_kodeTutupan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_filter1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_filter2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_search, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(jPanel_Data_TutupanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_export_Tutupan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_set_hpp_box, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_Data_TutupanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_Data_TutupanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_hpp_tutupan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_Data_TutupanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_data, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        Table_RincianBox.setAutoCreateRowSorter(true);
        Table_RincianBox.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_RincianBox.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No Box", "Grade", "Keping", "Gram", "Lokasi", "HPP"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.String.class, java.lang.Double.class
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
        jScrollPane8.setViewportView(Table_RincianBox);

        label_total_gram_rincianBox.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_rincianBox.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_gram_rincianBox.setText("0");

        label_total_kpg_rincianBox.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kpg_rincianBox.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_kpg_rincianBox.setText("0");

        jLabel50.setBackground(new java.awt.Color(255, 255, 255));
        jLabel50.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel50.setText("Keping :");

        label_total_rincian_box.setBackground(new java.awt.Color(255, 255, 255));
        label_total_rincian_box.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_rincian_box.setText("0");

        jLabel51.setBackground(new java.awt.Color(255, 255, 255));
        jLabel51.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel51.setText("Gram :");

        jLabel30.setBackground(new java.awt.Color(255, 255, 255));
        jLabel30.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel30.setText("Total Box :");

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel15.setText("Kode Tutupan Grading :");

        label_KodeAsal1.setBackground(new java.awt.Color(255, 255, 255));
        label_KodeAsal1.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_KodeAsal1.setText("KODE");

        button_export_hasil_box.setBackground(new java.awt.Color(255, 255, 255));
        button_export_hasil_box.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_hasil_box.setText("Export");
        button_export_hasil_box.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_hasil_boxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 542, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel30)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_rincian_box)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel50)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_kpg_rincianBox)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel51)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gram_rincianBox)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_KodeAsal1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_export_hasil_box)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_KodeAsal1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export_hasil_box, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 579, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel30)
                    .addComponent(label_total_rincian_box)
                    .addComponent(jLabel50)
                    .addComponent(jLabel51)
                    .addComponent(label_total_kpg_rincianBox)
                    .addComponent(label_total_gram_rincianBox))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Data Hasil Box", jPanel4);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel14.setText("Kode Tutupan Grading :");

        label_KodeAsal.setBackground(new java.awt.Color(255, 255, 255));
        label_KodeAsal.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_KodeAsal.setText("KODE");

        Table_Detail_Asal.setAutoCreateRowSorter(true);
        Table_Detail_Asal.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_Detail_Asal.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No LP / Kode Pembelian", "Kpg", "Gram"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Float.class, java.lang.Float.class
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
        Table_Detail_Asal.setSelectionBackground(new java.awt.Color(153, 204, 255));
        Table_Detail_Asal.getTableHeader().setReorderingAllowed(false);
        jScrollPane5.setViewportView(Table_Detail_Asal);

        label_total_asal_detail_asal.setBackground(new java.awt.Color(255, 255, 255));
        label_total_asal_detail_asal.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_asal_detail_asal.setText("0");

        jLabel36.setBackground(new java.awt.Color(255, 255, 255));
        jLabel36.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel36.setText("Keping :");

        label_total_kpg_detail_asal.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kpg_detail_asal.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_kpg_detail_asal.setText("0");

        label_total_gram_detail_asal.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_detail_asal.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_gram_detail_asal.setText("0");

        jLabel28.setBackground(new java.awt.Color(255, 255, 255));
        jLabel28.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel28.setText("Total LP / Beli :");

        jLabel37.setBackground(new java.awt.Color(255, 255, 255));
        jLabel37.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel37.setText("Gram :");

        button_export_LPTutupan.setBackground(new java.awt.Color(255, 255, 255));
        button_export_LPTutupan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_LPTutupan.setText("Export");
        button_export_LPTutupan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_LPTutupanActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 542, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel28)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_asal_detail_asal)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel36)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_kpg_detail_asal)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel37)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gram_detail_asal)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_KodeAsal)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_export_LPTutupan)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_KodeAsal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export_LPTutupan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 579, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(label_total_asal_detail_asal)
                    .addComponent(jLabel36)
                    .addComponent(label_total_kpg_detail_asal)
                    .addComponent(label_total_gram_detail_asal)
                    .addComponent(jLabel28)
                    .addComponent(jLabel37))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Data Asal Tutupan", jPanel2);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jPanel_Data_Tutupan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1)
                .addGap(3, 3, 3))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel_Data_Tutupan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jTabbedPane1)
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
        refreshTable_Tutupan();
    }//GEN-LAST:event_button_searchActionPerformed

    private void txt_search_kodeTutupanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_kodeTutupanKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_Tutupan();
        }
    }//GEN-LAST:event_txt_search_kodeTutupanKeyPressed

    private void button_export_TutupanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_TutupanActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Table_TutupanGrading.getModel();
        ExportToExcel.writeToExcel(model, jPanel1);
    }//GEN-LAST:event_button_export_TutupanActionPerformed

    private void button_export_LPTutupanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_LPTutupanActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Table_Detail_Asal.getModel();
        ExportToExcel.writeToExcel(model, jPanel1);
    }//GEN-LAST:event_button_export_LPTutupanActionPerformed

    private void button_set_hpp_boxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_set_hpp_boxActionPerformed
        // TODO add your handling code here:
        try {
            boolean check = true;
            int j = Table_TutupanGrading.getSelectedRow();

            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih salah satu kode Tutupan pada tabel !");
                check = false;
            } else if ((double) Table_TutupanGrading.getValueAt(j, 6) < 100) {
                JOptionPane.showMessageDialog(this, "Maaf tutupan ini biayanya belum lengkap masuk semua !");
//                check = false;
            }

            if (check) {
                String kode_tutupan = Table_TutupanGrading.getValueAt(j, 0).toString();
                int dialogResult = JOptionPane.showConfirmDialog(this, "Set HPP box untuk tutupan " + kode_tutupan + " ??", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    double hpp_per_gram = (double) Table_TutupanGrading.getValueAt(j, 9);
                    sql = "UPDATE `tb_box_bahan_jadi` SET "
                            + "`hpp_box`=`berat`*" + hpp_per_gram + " "
                            + "WHERE `no_tutupan` = '" + kode_tutupan + "'";
                    if (Utility.db.getStatement().executeUpdate(sql) > 0) {
                        JOptionPane.showMessageDialog(this, "Berhasil UPDATE HPP box!");
                        refreshTable_rincianBox(kode_tutupan);
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_TutupanGradingBahanJadi_Keuangan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_set_hpp_boxActionPerformed

    private void button_export_hasil_boxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_hasil_boxActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Table_RincianBox.getModel();
        ExportToExcel.writeToExcel(model, jPanel1);
    }//GEN-LAST:event_button_export_hasil_boxActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser Date_filter1;
    private com.toedter.calendar.JDateChooser Date_filter2;
    private javax.swing.JTable Table_Detail_Asal;
    public static javax.swing.JTable Table_RincianBox;
    private javax.swing.JTable Table_TutupanGrading;
    private javax.swing.JButton button_export_LPTutupan;
    private javax.swing.JButton button_export_Tutupan;
    private javax.swing.JButton button_export_hasil_box;
    public static javax.swing.JButton button_search;
    private javax.swing.JButton button_set_hpp_box;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel_Data_Tutupan;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel label_KodeAsal;
    private javax.swing.JLabel label_KodeAsal1;
    private javax.swing.JLabel label_total_asal_detail_asal;
    private javax.swing.JLabel label_total_data;
    private javax.swing.JLabel label_total_gram_detail_asal;
    private javax.swing.JLabel label_total_gram_rincianBox;
    private javax.swing.JLabel label_total_hpp_tutupan;
    private javax.swing.JLabel label_total_kpg_detail_asal;
    private javax.swing.JLabel label_total_kpg_rincianBox;
    private javax.swing.JLabel label_total_rincian_box;
    private javax.swing.JTextField txt_search_kodeTutupan;
    // End of variables declaration//GEN-END:variables
}
