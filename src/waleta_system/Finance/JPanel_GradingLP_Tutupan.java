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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.ExportToExcel;

public class JPanel_GradingLP_Tutupan extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();

    public JPanel_GradingLP_Tutupan() {
        initComponents();
    }

    public void init() {
        refreshTable();
        Table_GudangBahanJadi.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && Table_GudangBahanJadi.getSelectedRow() != -1) {
                    int i = Table_GudangBahanJadi.getSelectedRow();
                    if (i > -1) {
                    }
                }
            }
        });
    }

    public void refreshTable() {
        try {
            DefaultTableModel model = (DefaultTableModel) Table_GudangBahanJadi.getModel();
            model.setRowCount(0);
            float total_kpg_lp = 0, total_gram_lp = 0;
            float total_kpg_gbj = 0, total_gram_gbj = 0;
            float total_kpg_grading = 0, total_gram_grading = 0;
            String filter_tanggal = "";
            if (Date_Filter1.getDate() != null && Date_Filter2.getDate() != null) {
                switch (ComboBox_filter_tanggal.getSelectedIndex()) {
                    case 0:
                        filter_tanggal = "AND `tanggal_lp` BETWEEN '" + dateFormat.format(Date_Filter1.getDate()) + "' AND '" + dateFormat.format(Date_Filter2.getDate()) + "' ";
                        break;
                    case 1:
                        filter_tanggal = "AND `tanggal_grading` BETWEEN '" + dateFormat.format(Date_Filter1.getDate()) + "' AND '" + dateFormat.format(Date_Filter2.getDate()) + "' ";
                        break;
                    default:
                        filter_tanggal = "AND `tgl_statusBox` BETWEEN '" + dateFormat.format(Date_Filter1.getDate()) + "' AND '" + dateFormat.format(Date_Filter2.getDate()) + "' ";
                        break;
                }
            }
            sql = "SELECT `tb_bahan_jadi_masuk`.`kode_asal`, `tb_laporan_produksi`.`no_kartu_waleta`, `tb_laporan_produksi`.`tanggal_lp`, `tb_laporan_produksi`.`jumlah_keping`, `tb_laporan_produksi`.`berat_basah`, "
                    + "`tb_bahan_jadi_masuk`.`keping`, `tb_bahan_jadi_masuk`.`berat`, `tanggal_grading`, `tb_bahan_jadi_masuk`.`kode_tutupan`, `tb_tutupan_grading`.`tgl_statusBox`, "
                    + "SUM(`tb_grading_bahan_jadi`.`keping`) AS 'kpg_grading', SUM(`tb_grading_bahan_jadi`.`gram`) AS 'gram_grading' "
                    + "FROM `tb_bahan_jadi_masuk` "
                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_bahan_jadi_masuk`.`kode_asal` = `tb_laporan_produksi`.`no_laporan_produksi`"
                    + "LEFT JOIN `tb_grading_bahan_jadi` ON `tb_bahan_jadi_masuk`.`kode_asal` = `tb_grading_bahan_jadi`.`kode_asal_bahan_jadi`"
                    + "LEFT JOIN `tb_tutupan_grading` ON `tb_bahan_jadi_masuk`.`kode_tutupan` = `tb_tutupan_grading`.`kode_tutupan`"
                    + "WHERE `tb_bahan_jadi_masuk`.`kode_asal` LIKE '%" + txt_search_kodeAsal.getText() + "%' "
                    + "AND `tb_bahan_jadi_masuk`.`kode_tutupan` LIKE '%" + txt_search_no_tutupan.getText() + "%' "
                    + filter_tanggal
                    + "GROUP BY `tb_bahan_jadi_masuk`.`kode_asal` "
                    + "ORDER BY `tb_bahan_jadi_masuk`.`kode_asal`";

            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[18];
            while (rs.next()) {
                row[0] = rs.getString("kode_asal");
                row[1] = rs.getString("no_kartu_waleta");
                row[2] = rs.getDate("tanggal_lp");
                row[3] = rs.getFloat("jumlah_keping");
                row[4] = rs.getFloat("berat_basah");
                row[5] = rs.getFloat("keping");
                row[6] = rs.getFloat("berat");
                row[7] = rs.getFloat("kpg_grading");
                row[8] = rs.getFloat("gram_grading");
                row[9] = rs.getDate("tanggal_grading");
                row[10] = rs.getString("kode_tutupan");
                row[11] = rs.getDate("tgl_statusBox");
                model.addRow(row);

                total_kpg_lp = total_kpg_lp + rs.getFloat("jumlah_keping");
                total_gram_lp = total_gram_lp + rs.getFloat("berat_basah");
                total_kpg_gbj = total_kpg_gbj + rs.getFloat("keping");
                total_gram_gbj = total_gram_gbj + rs.getFloat("berat");
                total_kpg_grading = total_kpg_grading + rs.getFloat("kpg_grading");
                total_gram_grading = total_gram_grading + rs.getFloat("gram_grading");
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_GudangBahanJadi);
            int rowData = Table_GudangBahanJadi.getRowCount();
            label_total_data.setText(Integer.toString(rowData));
            label_total_keping_lp.setText(decimalFormat.format(total_kpg_lp));
            label_total_berat_lp.setText(decimalFormat.format(total_gram_lp));
            label_total_keping_gbj.setText(decimalFormat.format(total_kpg_gbj));
            label_total_berat_gbj.setText(decimalFormat.format(total_gram_gbj));
            label_total_keping_grading.setText(decimalFormat.format(total_kpg_grading));
            label_total_berat_grading.setText(decimalFormat.format(total_gram_grading));

            sql = "SELECT `grade_bahan_jadi`, `tb_grade_bahan_jadi`.`kode_grade`, SUM(`tb_grading_bahan_jadi`.`keping`) AS 'kpg_grading', SUM(`tb_grading_bahan_jadi`.`gram`) AS 'gram_grading' "
                    + "FROM `tb_bahan_jadi_masuk` "
                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_bahan_jadi_masuk`.`kode_asal` = `tb_laporan_produksi`.`no_laporan_produksi` "
                    + "LEFT JOIN `tb_grading_bahan_jadi` ON `tb_bahan_jadi_masuk`.`kode_asal` = `tb_grading_bahan_jadi`.`kode_asal_bahan_jadi` "
                    + "LEFT JOIN `tb_tutupan_grading` ON `tb_bahan_jadi_masuk`.`kode_tutupan` = `tb_tutupan_grading`.`kode_tutupan` "
                    + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_grade_bahan_jadi`.`kode` = `tb_grading_bahan_jadi`.`grade_bahan_jadi` "
                    + "WHERE `tb_bahan_jadi_masuk`.`kode_asal` LIKE '%" + txt_search_kodeAsal.getText() + "%' "
                    + "AND `tb_bahan_jadi_masuk`.`kode_tutupan` LIKE '%" + txt_search_no_tutupan.getText() + "%' "
                    + filter_tanggal
                    + "GROUP BY `grade_bahan_jadi` "
                    + "ORDER BY `kode_grade`";
            refreshTable_RekapHasilGrading(sql);

            sql = "SELECT `tb_bahan_jadi_masuk`.`kode_tutupan`, `tb_tutupan_grading`.`tgl_statusBox`, SUM(`tb_grading_bahan_jadi`.`keping`) AS 'kpg_grading', SUM(`tb_grading_bahan_jadi`.`gram`) AS 'gram_grading' "
                    + "FROM `tb_bahan_jadi_masuk` "
                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_bahan_jadi_masuk`.`kode_asal` = `tb_laporan_produksi`.`no_laporan_produksi` "
                    + "LEFT JOIN `tb_grading_bahan_jadi` ON `tb_bahan_jadi_masuk`.`kode_asal` = `tb_grading_bahan_jadi`.`kode_asal_bahan_jadi` "
                    + "LEFT JOIN `tb_tutupan_grading` ON `tb_bahan_jadi_masuk`.`kode_tutupan` = `tb_tutupan_grading`.`kode_tutupan` "
                    + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_grade_bahan_jadi`.`kode` = `tb_grading_bahan_jadi`.`grade_bahan_jadi` "
                    + "WHERE `tb_bahan_jadi_masuk`.`kode_asal` LIKE '%" + txt_search_kodeAsal.getText() + "%' "
                    + "AND `tb_bahan_jadi_masuk`.`kode_tutupan` LIKE '%" + txt_search_no_tutupan.getText() + "%' "
                    + filter_tanggal
                    + "GROUP BY `tb_bahan_jadi_masuk`.`kode_tutupan` "
                    + "ORDER BY `tb_bahan_jadi_masuk`.`kode_tutupan`";
            refreshTable_RekapPerTutupan(sql);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_GradingLP_Tutupan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_RekapHasilGrading(String query) {
        try {
            int total_kpg = 0;
            float total_gram = 0;
            DefaultTableModel model = (DefaultTableModel) table_rekap_grading.getModel();
            model.setRowCount(0);
            rs = Utility.db.getStatement().executeQuery(query);
            Object[] baris = new Object[3];
            while (rs.next()) {
                baris[0] = rs.getString("kode_grade");
                baris[1] = rs.getInt("kpg_grading");
                baris[2] = rs.getInt("gram_grading");
                model.addRow(baris);
                total_kpg = total_kpg + rs.getInt("kpg_grading");
                total_gram = total_gram + rs.getInt("gram_grading");
            }
            ColumnsAutoSizer.sizeColumnsToFit(table_rekap_grading);

            int total_data = table_rekap_grading.getRowCount();
            label_total_hasil_grading.setText(decimalFormat.format(total_data));
            label_total_kpg_rekap1.setText(decimalFormat.format(total_kpg));
            label_total_gram_rekap1.setText(decimalFormat.format(total_gram));
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_GradingLP_Tutupan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_RekapPerTutupan(String query) {
        try {
            int total_kpg = 0;
            float total_gram = 0;
            DefaultTableModel model = (DefaultTableModel) table_rekap_per_tutupan.getModel();
            model.setRowCount(0);
            rs = Utility.db.getStatement().executeQuery(query);
            Object[] baris = new Object[4];
            while (rs.next()) {
                baris[0] = rs.getString("kode_tutupan");
                baris[1] = rs.getDate("tgl_statusBox");
                baris[2] = rs.getInt("kpg_grading");
                baris[3] = rs.getInt("gram_grading");
                model.addRow(baris);
                total_kpg = total_kpg + rs.getInt("kpg_grading");
                total_gram = total_gram + rs.getInt("gram_grading");
            }
            ColumnsAutoSizer.sizeColumnsToFit(table_rekap_per_tutupan);
            label_total_kpg_rekap2.setText(decimalFormat.format(total_kpg));
            label_total_gram_rekap2.setText(decimalFormat.format(total_gram));

        } catch (SQLException ex) {
            Logger.getLogger(JPanel_GradingLP_Tutupan.class.getName()).log(Level.SEVERE, null, ex);
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
        txt_search_kodeAsal = new javax.swing.JTextField();
        button_search = new javax.swing.JButton();
        Date_Filter1 = new com.toedter.calendar.JDateChooser();
        Date_Filter2 = new com.toedter.calendar.JDateChooser();
        jScrollPane1 = new javax.swing.JScrollPane();
        Table_GudangBahanJadi = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        label_total_data = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        label_total_keping_lp = new javax.swing.JLabel();
        label_total_berat_lp = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        button_export = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        table_rekap_grading = new javax.swing.JTable();
        jLabel11 = new javax.swing.JLabel();
        label_total_hasil_grading = new javax.swing.JLabel();
        label_total_kpg_rekap1 = new javax.swing.JLabel();
        label_total_gram_rekap1 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        JLabel1 = new javax.swing.JLabel();
        ComboBox_filter_tanggal = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        label_total_berat_gbj = new javax.swing.JLabel();
        label_total_keping_gbj = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        label_total_berat_grading = new javax.swing.JLabel();
        label_total_keping_grading = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txt_search_no_tutupan = new javax.swing.JTextField();
        JLabel2 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        table_rekap_per_tutupan = new javax.swing.JTable();
        jLabel16 = new javax.swing.JLabel();
        label_total_gram_rekap2 = new javax.swing.JLabel();
        label_total_kpg_rekap2 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Penerimaan & Grading Barang Jadi", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("Kode Asal / No LP :");

        txt_search_kodeAsal.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_kodeAsal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_kodeAsalKeyPressed(evt);
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

        Date_Filter1.setBackground(new java.awt.Color(255, 255, 255));
        Date_Filter1.setDate(new Date(new Date().getTime()- (7 * 24 * 60 * 60 * 1000)));
        Date_Filter1.setDateFormatString("dd MMMM yyyy");
        Date_Filter1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Date_Filter2.setBackground(new java.awt.Color(255, 255, 255));
        Date_Filter2.setDate(new Date());
        Date_Filter2.setDateFormatString("dd MMMM yyyy");
        Date_Filter2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Table_GudangBahanJadi.setAutoCreateRowSorter(true);
        Table_GudangBahanJadi.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_GudangBahanJadi.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode Asal", "No Kartu", "Tgl LP", "Kpg LP", "Berat LP", "Kpg GBJ", "Berat GBJ", "Kpg Grading", "Gram Grading", "Tgl Grading", "No Tutupan", "Tgl Selesai Box"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Object.class, java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Table_GudangBahanJadi.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(Table_GudangBahanJadi);

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("Total Data :");

        label_total_data.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_data.setText("0");

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel5.setText("Total Keping LP :");

        label_total_keping_lp.setBackground(new java.awt.Color(255, 255, 255));
        label_total_keping_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_keping_lp.setText("0");

        label_total_berat_lp.setBackground(new java.awt.Color(255, 255, 255));
        label_total_berat_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_berat_lp.setText("0");

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel8.setText("Total Berat LP :");

        button_export.setBackground(new java.awt.Color(255, 255, 255));
        button_export.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export.setText("Export To Excel");
        button_export.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_exportActionPerformed(evt);
            }
        });

        table_rekap_grading.setAutoCreateRowSorter(true);
        table_rekap_grading.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_rekap_grading.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Grade BJ", "Kpg", "Gram"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class
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
        jScrollPane2.setViewportView(table_rekap_grading);

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel11.setText("Total Data :");

        label_total_hasil_grading.setBackground(new java.awt.Color(255, 255, 255));
        label_total_hasil_grading.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_hasil_grading.setText("0");

        label_total_kpg_rekap1.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kpg_rekap1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_kpg_rekap1.setText("0");

        label_total_gram_rekap1.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_rekap1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_gram_rekap1.setText("0");

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel14.setText("Total Berat :");

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel15.setText("Total Keping :");

        JLabel1.setBackground(new java.awt.Color(255, 255, 255));
        JLabel1.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        JLabel1.setText("Rekap per Tutupan");

        ComboBox_filter_tanggal.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_filter_tanggal.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tanggal LP", "Tanggal Grading", "Tanggal Selesai Box" }));

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel6.setText("Total Keping GBJ :");

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel9.setText("Total Berat GBJ :");

        label_total_berat_gbj.setBackground(new java.awt.Color(255, 255, 255));
        label_total_berat_gbj.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_berat_gbj.setText("0");

        label_total_keping_gbj.setBackground(new java.awt.Color(255, 255, 255));
        label_total_keping_gbj.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_keping_gbj.setText("0");

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel7.setText("Total Keping Grading :");

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel10.setText("Total Berat Grading :");

        label_total_berat_grading.setBackground(new java.awt.Color(255, 255, 255));
        label_total_berat_grading.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_berat_grading.setText("0");

        label_total_keping_grading.setBackground(new java.awt.Color(255, 255, 255));
        label_total_keping_grading.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_keping_grading.setText("0");

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("No Tutupan :");

        txt_search_no_tutupan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_no_tutupan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_no_tutupanKeyPressed(evt);
            }
        });

        JLabel2.setBackground(new java.awt.Color(255, 255, 255));
        JLabel2.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        JLabel2.setText("Rekap hasil Grading");

        table_rekap_per_tutupan.setAutoCreateRowSorter(true);
        table_rekap_per_tutupan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_rekap_per_tutupan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No Tutupan", "Tgl Selesai Box", "Kpg", "Gram"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(table_rekap_per_tutupan);

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel16.setText("Total Berat :");

        label_total_gram_rekap2.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_rekap2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_gram_rekap2.setText("0");

        label_total_kpg_rekap2.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kpg_rekap2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_kpg_rekap2.setText("0");

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel17.setText("Total Keping :");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_kodeAsal, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_no_tutupan, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_filter_tanggal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_Filter1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_Filter2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_search)
                        .addGap(0, 166, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_data)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_keping_lp))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_berat_lp)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_keping_gbj))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_berat_gbj)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_keping_grading))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_berat_grading)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(button_export)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(JLabel1)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel11)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(label_total_hasil_grading))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel15)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(label_total_kpg_rekap1)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jLabel14)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(label_total_gram_rekap1))
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(JLabel2)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_kpg_rekap2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gram_rekap2)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_search_kodeAsal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(ComboBox_filter_tanggal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_search_no_tutupan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(Date_Filter1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_Filter2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(button_search, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(JLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_data, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_keping_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_berat_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_keping_gbj, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_berat_gbj, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_keping_grading, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_hasil_grading, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_berat_grading, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_kpg_rekap1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_gram_rekap1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_export, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 592, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(JLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_kpg_rekap2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_gram_rekap2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)))
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

    private void button_searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_searchActionPerformed
        // TODO add your handling code here:
        refreshTable();
    }//GEN-LAST:event_button_searchActionPerformed

    private void txt_search_kodeAsalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_kodeAsalKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_search_kodeAsalKeyPressed

    private void button_exportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_exportActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Table_GudangBahanJadi.getModel();
        ExportToExcel.writeToExcel(model, jPanel1);
    }//GEN-LAST:event_button_exportActionPerformed

    private void txt_search_no_tutupanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_no_tutupanKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_search_no_tutupanKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_filter_tanggal;
    private com.toedter.calendar.JDateChooser Date_Filter1;
    private com.toedter.calendar.JDateChooser Date_Filter2;
    private javax.swing.JLabel JLabel1;
    private javax.swing.JLabel JLabel2;
    private javax.swing.JTable Table_GudangBahanJadi;
    private javax.swing.JButton button_export;
    private javax.swing.JButton button_search;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel label_total_berat_gbj;
    private javax.swing.JLabel label_total_berat_grading;
    private javax.swing.JLabel label_total_berat_lp;
    private javax.swing.JLabel label_total_data;
    private javax.swing.JLabel label_total_gram_rekap1;
    private javax.swing.JLabel label_total_gram_rekap2;
    private javax.swing.JLabel label_total_hasil_grading;
    private javax.swing.JLabel label_total_keping_gbj;
    private javax.swing.JLabel label_total_keping_grading;
    private javax.swing.JLabel label_total_keping_lp;
    private javax.swing.JLabel label_total_kpg_rekap1;
    private javax.swing.JLabel label_total_kpg_rekap2;
    public static javax.swing.JTable table_rekap_grading;
    public static javax.swing.JTable table_rekap_per_tutupan;
    private javax.swing.JTextField txt_search_kodeAsal;
    private javax.swing.JTextField txt_search_no_tutupan;
    // End of variables declaration//GEN-END:variables
}
