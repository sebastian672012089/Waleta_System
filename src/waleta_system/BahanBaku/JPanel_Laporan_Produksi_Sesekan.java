package waleta_system.BahanBaku;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JRException;
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
import waleta_system.MainForm;

public class JPanel_Laporan_Produksi_Sesekan extends javax.swing.JPanel {

    String sql = null;
    String sql2 = null;
    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    float kadar_air_bahan_baku = 0;

    public JPanel_Laporan_Produksi_Sesekan() {
        initComponents();
    }

    public void init() {
        try {
            ComboBox_Ruang_LP.removeAllItems();
            ComboBox_Ruang_LP.addItem("All");
            sql = "SELECT DISTINCT(`ruangan`) AS 'ruangan' FROM `tb_laporan_produksi` WHERE 1";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_Ruang_LP.addItem(rs.getString("ruangan"));
            }

            ComboBox_sub_lp_sesekan.removeAllItems();
            ComboBox_sub_lp_sesekan.addItem("All");
            sql = "SELECT DISTINCT(`sub`) AS 'sub' FROM `tb_laporan_produksi_sesekan` WHERE 1";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_sub_lp_sesekan.addItem(rs.getString("sub"));
            }
            refreshTable_lp();
            refreshTable_lp_sesekan();

            Table_LP_Sesekan.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent event) {
                    if (!event.getValueIsAdjusting() && Table_LP_Sesekan.getSelectedRow() != -1) {
                        int i = Table_LP_Sesekan.getSelectedRow();
                        String no_lp_sesekan = Table_LP_Sesekan.getValueAt(i, 0).toString();
                        refreshTable_lp_sesekan_detail(no_lp_sesekan);
                    }
                }
            });
        } catch (Exception ex) {
            Logger.getLogger(JPanel_GradeBahanBaku.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_lp() {
        try {
            double total_gram = 0, total_kpg = 0, total_gram_sesekan = 0;
            DefaultTableModel model = (DefaultTableModel) Table_laporan_produksi.getModel();
            model.setRowCount(0);
            String ruang = "AND `ruangan` = '" + ComboBox_Ruang_LP.getSelectedItem().toString() + "' ";
            if ("All".equals(ComboBox_Ruang_LP.getSelectedItem().toString())) {
                ruang = "";
            }
            String filter_tanggal_lp = "";
            if (Date_Search_LP_1.getDate() != null && Date_Search_LP_2.getDate() != null) {
                filter_tanggal_lp = "AND `tanggal_lp` BETWEEN '" + dateFormat.format(Date_Search_LP_1.getDate()) + "' AND '" + dateFormat.format(Date_Search_LP_2.getDate()) + "' ";
            }
            sql = "SELECT `no_laporan_produksi`, `tanggal_lp`, `no_kartu_waleta`, `kode_grade`, `ruangan`, `jenis_bulu_lp`, `jumlah_keping`, `berat_basah`, "
                    + "`no_lp_sesekan`, `gram_sesekan_lp`, `tb_karyawan`.`nama_pegawai` "
                    + "FROM `tb_laporan_produksi` "
                    + "LEFT JOIN `tb_karyawan` ON `tb_laporan_produksi`.`pekerja_sesekan` = `tb_karyawan`.`id_pegawai` "
                    + "WHERE  `no_kartu_waleta` LIKE '%" + txt_search_no_kartu.getText() + "%' "
                    + "AND `no_laporan_produksi` LIKE '%" + txt_search_lp.getText() + "%' "
                    + "AND `kode_grade` LIKE '%" + txt_search_grade.getText() + "%'  "
                    + "AND `memo_lp` LIKE '%" + txt_search_memo.getText() + "%' "
                    + ruang + filter_tanggal_lp
                    + "ORDER BY `no_laporan_produksi` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[20];
            while (rs.next()) {
                row[0] = rs.getString("no_laporan_produksi");
                row[1] = rs.getDate("tanggal_lp");
                row[2] = rs.getString("no_kartu_waleta");
                row[3] = rs.getString("kode_grade");
                row[4] = rs.getString("ruangan");
                row[5] = rs.getString("jenis_bulu_lp");
                row[6] = rs.getInt("jumlah_keping");
                row[7] = rs.getInt("berat_basah");
                row[8] = rs.getString("no_lp_sesekan");
                row[9] = rs.getFloat("gram_sesekan_lp");
                row[10] = Math.round(rs.getFloat("gram_sesekan_lp") / rs.getFloat("berat_basah") * 1000d) / 10d;
                row[11] = rs.getString("nama_pegawai");
                model.addRow(row);
                total_gram = total_gram + rs.getInt("berat_basah");
                total_kpg = total_kpg + rs.getInt("jumlah_keping");
                total_gram_sesekan = total_gram_sesekan + rs.getFloat("gram_sesekan_lp");
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_laporan_produksi);
            int rowData = Table_laporan_produksi.getRowCount();
            label_total_lp.setText(Integer.toString(rowData));
            label_total_keping_LP.setText(decimalFormat.format(total_kpg) + " Keping");
            label_total_gram_LP.setText(decimalFormat.format(total_gram) + " Grams");
            label_total_gram_SesekanLP.setText(decimalFormat.format(total_gram_sesekan) + " Grams");

            Table_laporan_produksi.setDefaultRenderer(Double.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                    if (column == 10) {
                        if ((Double) Table_laporan_produksi.getValueAt(row, column) > 5) {
                            if (isSelected) {
                                comp.setBackground(Color.gray);
                                comp.setForeground(Color.red);
                            } else {
                                comp.setBackground(Color.PINK);
                                comp.setForeground(Color.red);
                            }
                        } else {
                            if (isSelected) {
                                comp.setBackground(Table_laporan_produksi.getSelectionBackground());
                                comp.setForeground(Table_laporan_produksi.getSelectionForeground());
                            } else {
                                comp.setBackground(Table_laporan_produksi.getBackground());
                                comp.setForeground(Table_laporan_produksi.getForeground());
                            }
                        }
                    } else {
                        if (isSelected) {
                            comp.setBackground(Table_laporan_produksi.getSelectionBackground());
                            comp.setForeground(Table_laporan_produksi.getSelectionForeground());
                        } else {
                            comp.setBackground(Table_laporan_produksi.getBackground());
                            comp.setForeground(Table_laporan_produksi.getForeground());
                        }
                    }
                    return comp;
                }
            });
            Table_laporan_produksi.repaint();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Laporan_Produksi_Sesekan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_lp_sesekan() {
        try {
            double total_kpg = 0, total_ssk_bersih = 0, total_ssk_kuning = 0, total_ssk_pasir = 0;
            DefaultTableModel model = (DefaultTableModel) Table_LP_Sesekan.getModel();
            model.setRowCount(0);
            String sub = "AND `sub` = '" + ComboBox_sub_lp_sesekan.getSelectedItem().toString() + "' ";
            if ("All".equals(ComboBox_sub_lp_sesekan.getSelectedItem().toString())) {
                sub = "";
            }
            String filter_tanggal_lp = "";
            if (Date_Search_LP_Sesekan1.getDate() != null && Date_Search_LP_Sesekan2.getDate() != null) {
                filter_tanggal_lp = "AND `tb_laporan_produksi_sesekan`.`tanggal_lp` BETWEEN '" + dateFormat.format(Date_Search_LP_Sesekan1.getDate()) + "' AND '" + dateFormat.format(Date_Search_LP_Sesekan2.getDate()) + "' ";
            }
            sql = "SELECT `tb_laporan_produksi_sesekan`.`no_lp_sesekan`, `sub`, `tb_laporan_produksi_sesekan`.`tanggal_lp`, `tb_laporan_produksi_sesekan`.`kode_grade`, `tb_laporan_produksi_sesekan`.`bulu_upah`, `memo`, `keping`, "
                    + "`sesekan_bersih`, `sesekan_kuning`, `sesekan_pasir`, `berat_setelah_cuci`, `nilai_lp`, SUM(`gram_sesekan_lp`) AS 'gram_sesekan_lp' "
                    + "FROM `tb_laporan_produksi_sesekan` "
                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_laporan_produksi_sesekan`.`no_lp_sesekan` = `tb_laporan_produksi`.`no_lp_sesekan` "
                    + "WHERE `tb_laporan_produksi_sesekan`.`no_lp_sesekan` LIKE '%" + txt_search_no_lp_sesekan.getText() + "%' "
                    + sub + filter_tanggal_lp
                    + "GROUP BY `tb_laporan_produksi_sesekan`.`no_lp_sesekan` "
                    + "ORDER BY `tb_laporan_produksi_sesekan`.`no_lp_sesekan` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[15];
            while (rs.next()) {
                row[0] = rs.getString("no_lp_sesekan");
                row[1] = rs.getString("sub");
                row[2] = rs.getDate("tanggal_lp");
                row[3] = rs.getString("kode_grade");
                row[4] = rs.getString("bulu_upah");
                row[5] = rs.getInt("keping");
                row[6] = rs.getInt("sesekan_bersih");
                row[7] = rs.getInt("sesekan_kuning");
                row[8] = rs.getInt("sesekan_pasir");
                row[9] = rs.getInt("berat_setelah_cuci");
                row[10] = rs.getInt("nilai_lp");
                row[11] = Math.round(rs.getFloat("berat_setelah_cuci") * 100f / rs.getFloat("sesekan_bersih")) / 100f;
                row[12] = rs.getFloat("gram_sesekan_lp");
                model.addRow(row);
                total_kpg = total_kpg + rs.getInt("keping");
                total_ssk_bersih = total_ssk_bersih + rs.getInt("sesekan_bersih");
                total_ssk_kuning = total_ssk_kuning + rs.getInt("sesekan_kuning");
                total_ssk_pasir = total_ssk_pasir + rs.getInt("sesekan_pasir");
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_LP_Sesekan);
            int rowData = Table_LP_Sesekan.getRowCount();
            label_total_lp_sesekan.setText(Integer.toString(rowData));
            label_total_keping_LP_sesekan.setText(decimalFormat.format(total_kpg) + " Keping");
            label_total_sesekan_bersih.setText(decimalFormat.format(total_ssk_bersih) + " Gram");
            label_total_sesekan_kuning.setText(decimalFormat.format(total_ssk_kuning) + " Gram");
            label_total_sesekan_pasir.setText(decimalFormat.format(total_ssk_pasir) + " Gram");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Laporan_Produksi_Sesekan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_lp_sesekan_detail(String no_lp_sesekan) {
        try {
            double total_gram_lp = 0, total_gram_sesekan = 0;
            DefaultTableModel model = (DefaultTableModel) Table_LP_Sesekan_detail.getModel();
            model.setRowCount(0);
            sql = "SELECT `no_laporan_produksi`, `kode_grade`, `jenis_bulu_lp`, `berat_basah`, `no_lp_sesekan`, `gram_sesekan_lp` "
                    + "FROM `tb_laporan_produksi` "
                    + "WHERE  `no_lp_sesekan` = '" + no_lp_sesekan + "' "
                    + "ORDER BY `no_laporan_produksi` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[10];
            while (rs.next()) {
                row[0] = rs.getString("no_laporan_produksi");
                row[1] = rs.getString("kode_grade");
                row[2] = rs.getString("jenis_bulu_lp");
                row[3] = rs.getInt("berat_basah");
                row[4] = rs.getFloat("gram_sesekan_lp");
                model.addRow(row);
                total_gram_lp = total_gram_lp + rs.getInt("berat_basah");
                total_gram_sesekan = total_gram_sesekan + rs.getFloat("gram_sesekan_lp");
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_LP_Sesekan_detail);
            int rowData = Table_LP_Sesekan_detail.getRowCount();
            label_total_lp_sesekan_detail.setText(Integer.toString(rowData));
            label_total_keping_LP_sesekan_detail.setText(decimalFormat.format(total_gram_lp) + " Keping");
            label_total_gram_LP_sesekan_detail.setText(decimalFormat.format(total_gram_sesekan) + " Grams");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Laporan_Produksi_Sesekan.class.getName()).log(Level.SEVERE, null, ex);
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
        jScrollPane6 = new javax.swing.JScrollPane();
        Table_laporan_produksi = new javax.swing.JTable();
        jLabel27 = new javax.swing.JLabel();
        label_total_keping_LP = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        label_total_gram_LP = new javax.swing.JLabel();
        label_total_lp = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        txt_search_grade = new javax.swing.JTextField();
        jLabel34 = new javax.swing.JLabel();
        txt_search_no_kartu = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        txt_search_memo = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        ComboBox_Ruang_LP = new javax.swing.JComboBox<>();
        jLabel16 = new javax.swing.JLabel();
        Date_Search_LP_2 = new com.toedter.calendar.JDateChooser();
        Date_Search_LP_1 = new com.toedter.calendar.JDateChooser();
        button_search_lp = new javax.swing.JButton();
        txt_search_lp = new javax.swing.JTextField();
        button_export_lp = new javax.swing.JButton();
        jLabel30 = new javax.swing.JLabel();
        label_total_gram_SesekanLP = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        Table_LP_Sesekan = new javax.swing.JTable();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        ComboBox_sub_lp_sesekan = new javax.swing.JComboBox<>();
        label_total_keping_LP_sesekan = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        Date_Search_LP_Sesekan2 = new com.toedter.calendar.JDateChooser();
        label_total_sesekan_bersih = new javax.swing.JLabel();
        Date_Search_LP_Sesekan1 = new com.toedter.calendar.JDateChooser();
        label_total_lp_sesekan = new javax.swing.JLabel();
        button_search_lp_sesekan = new javax.swing.JButton();
        jLabel24 = new javax.swing.JLabel();
        txt_search_no_lp_sesekan = new javax.swing.JTextField();
        button_create_lp_sesekan = new javax.swing.JButton();
        button_edit_lp_sesekan = new javax.swing.JButton();
        button_print_lp1 = new javax.swing.JButton();
        button_print_lp_semua = new javax.swing.JButton();
        button_export_lp_sesekan = new javax.swing.JButton();
        button_delete_lp_sesekan = new javax.swing.JButton();
        jScrollPane9 = new javax.swing.JScrollPane();
        Table_LP_Sesekan_detail = new javax.swing.JTable();
        label_total_keping_LP_sesekan_detail = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        label_total_gram_LP_sesekan_detail = new javax.swing.JLabel();
        label_total_lp_sesekan_detail = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        label_total_sesekan_kuning = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        label_total_sesekan_pasir = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();

        jPanel_laporan_produksi.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_laporan_produksi.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Laporan Produksi Sesekan", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N
        jPanel_laporan_produksi.setPreferredSize(new java.awt.Dimension(1366, 700));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        Table_laporan_produksi.setAutoCreateRowSorter(true);
        Table_laporan_produksi.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_laporan_produksi.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No LP", "Tanggal LP", "No Kartu", "Kode Grade", "Ruang", "Bulu Upah", "Keping", "Berat Basah", "No LP Sesekan", "Gram Sesekan", "%", "Pekerja Sesekan"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.String.class, java.lang.Float.class, java.lang.Double.class, java.lang.String.class
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
        Table_laporan_produksi.getTableHeader().setReorderingAllowed(false);
        Table_laporan_produksi.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Table_laporan_produksiMouseClicked(evt);
            }
        });
        jScrollPane6.setViewportView(Table_laporan_produksi);

        jLabel27.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel27.setText("Total Keping :");

        label_total_keping_LP.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        label_total_keping_LP.setText("TOTAL");

        jLabel26.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel26.setText("Total Berat :");

        label_total_gram_LP.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        label_total_gram_LP.setText("TOTAL");

        label_total_lp.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        label_total_lp.setText("TOTAL");

        jLabel15.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel15.setText("Total Data :");

        jLabel35.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel35.setText("Tgl LP :");

        txt_search_grade.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_grade.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_gradeKeyPressed(evt);
            }
        });

        jLabel34.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel34.setText("Sampai");

        txt_search_no_kartu.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_no_kartu.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_no_kartuKeyPressed(evt);
            }
        });

        jLabel14.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel14.setText("Grade :");

        jLabel17.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel17.setText("No Kartu :");

        txt_search_memo.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_memo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_memoKeyPressed(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel13.setText("No LP :");

        jLabel20.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel20.setText("Memo LP :");

        ComboBox_Ruang_LP.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_Ruang_LP.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        jLabel16.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel16.setText("Ruangan :");

        Date_Search_LP_2.setDate(new Date());
        Date_Search_LP_2.setDateFormatString("dd MMMM yyyy");
        Date_Search_LP_2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Date_Search_LP_1.setToolTipText("");
        Date_Search_LP_1.setDate(new Date(new Date().getTime()-(7 * 24 * 60 * 60 * 1000)));
        Date_Search_LP_1.setDateFormatString("dd MMMM yyyy");
        Date_Search_LP_1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Date_Search_LP_1.setMinSelectableDate(new java.util.Date(1420048915000L));

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

        button_export_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_lp.setText("Export Excel");
        button_export_lp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_lpActionPerformed(evt);
            }
        });

        jLabel30.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel30.setText("Total Berat Sesekan :");

        label_total_gram_SesekanLP.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        label_total_gram_SesekanLP.setText("TOTAL");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane6)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_lp)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel27)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_keping_LP)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel26)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_gram_LP)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel30)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_gram_SesekanLP))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel17)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_no_kartu, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel20)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_memo, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_grade, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel16)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_Ruang_LP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel35)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_Search_LP_1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel34)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_Search_LP_2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_search_lp)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_export_lp)))
                        .addGap(0, 142, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_Search_LP_1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_Search_LP_2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_search_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_no_kartu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_lp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_memo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_Ruang_LP, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_grade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel26)
                    .addComponent(label_total_keping_LP)
                    .addComponent(jLabel27)
                    .addComponent(jLabel15)
                    .addComponent(label_total_lp)
                    .addComponent(label_total_gram_LP)
                    .addComponent(jLabel30)
                    .addComponent(label_total_gram_SesekanLP))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 577, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Input Gram Sesekan LP", jPanel1);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        Table_LP_Sesekan.setAutoCreateRowSorter(true);
        Table_LP_Sesekan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_LP_Sesekan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No LP Sesekan", "SUB", "Tanggal LP", "Grade", "Bulu Upah", "Keping", "Sesekan Bersih", "Sesekan Kuning", "Sesekan Pasir", "Berat Setelah Cuci", "Nilai LP", "Pengembangan", "Gram SSK"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Table_LP_Sesekan.getTableHeader().setReorderingAllowed(false);
        jScrollPane8.setViewportView(Table_LP_Sesekan);

        jLabel36.setBackground(new java.awt.Color(255, 255, 255));
        jLabel36.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel36.setText("Tgl LP Sesekan :");

        jLabel37.setBackground(new java.awt.Color(255, 255, 255));
        jLabel37.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel37.setText("Sampai");

        jLabel21.setBackground(new java.awt.Color(255, 255, 255));
        jLabel21.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel21.setText("No LP :");

        jLabel28.setBackground(new java.awt.Color(255, 255, 255));
        jLabel28.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel28.setText("Total Keping :");

        ComboBox_sub_lp_sesekan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_sub_lp_sesekan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        label_total_keping_LP_sesekan.setBackground(new java.awt.Color(255, 255, 255));
        label_total_keping_LP_sesekan.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        label_total_keping_LP_sesekan.setText("TOTAL");

        jLabel23.setBackground(new java.awt.Color(255, 255, 255));
        jLabel23.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel23.setText("Sub :");

        jLabel29.setBackground(new java.awt.Color(255, 255, 255));
        jLabel29.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel29.setText("Tot SSK Bersih :");

        Date_Search_LP_Sesekan2.setBackground(new java.awt.Color(255, 255, 255));
        Date_Search_LP_Sesekan2.setDate(new Date());
        Date_Search_LP_Sesekan2.setDateFormatString("dd MMMM yyyy");
        Date_Search_LP_Sesekan2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        label_total_sesekan_bersih.setBackground(new java.awt.Color(255, 255, 255));
        label_total_sesekan_bersih.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        label_total_sesekan_bersih.setText("TOTAL");

        Date_Search_LP_Sesekan1.setBackground(new java.awt.Color(255, 255, 255));
        Date_Search_LP_Sesekan1.setToolTipText("");
        Date_Search_LP_Sesekan1.setDate(new Date(new Date().getTime()-(7 * 24 * 60 * 60 * 1000)));
        Date_Search_LP_Sesekan1.setDateFormatString("dd MMMM yyyy");
        Date_Search_LP_Sesekan1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Date_Search_LP_Sesekan1.setMinSelectableDate(new java.util.Date(1420048915000L));

        label_total_lp_sesekan.setBackground(new java.awt.Color(255, 255, 255));
        label_total_lp_sesekan.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        label_total_lp_sesekan.setText("TOTAL");

        button_search_lp_sesekan.setBackground(new java.awt.Color(255, 255, 255));
        button_search_lp_sesekan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search_lp_sesekan.setText("Search");
        button_search_lp_sesekan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_search_lp_sesekanActionPerformed(evt);
            }
        });

        jLabel24.setBackground(new java.awt.Color(255, 255, 255));
        jLabel24.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel24.setText("Total Data :");

        txt_search_no_lp_sesekan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_no_lp_sesekan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_no_lp_sesekanKeyPressed(evt);
            }
        });

        button_create_lp_sesekan.setBackground(new java.awt.Color(255, 255, 255));
        button_create_lp_sesekan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_create_lp_sesekan.setText("Buat LP Sesekan");
        button_create_lp_sesekan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_create_lp_sesekanActionPerformed(evt);
            }
        });

        button_edit_lp_sesekan.setBackground(new java.awt.Color(255, 255, 255));
        button_edit_lp_sesekan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_edit_lp_sesekan.setText("Edit LP Sesekan");
        button_edit_lp_sesekan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_edit_lp_sesekanActionPerformed(evt);
            }
        });

        button_print_lp1.setBackground(new java.awt.Color(255, 255, 255));
        button_print_lp1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_print_lp1.setText("Print 1 LP");
        button_print_lp1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_print_lp1ActionPerformed(evt);
            }
        });

        button_print_lp_semua.setBackground(new java.awt.Color(255, 255, 255));
        button_print_lp_semua.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_print_lp_semua.setText("Print Semua LP");
        button_print_lp_semua.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_print_lp_semuaActionPerformed(evt);
            }
        });

        button_export_lp_sesekan.setBackground(new java.awt.Color(255, 255, 255));
        button_export_lp_sesekan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_lp_sesekan.setText("Export Excel");
        button_export_lp_sesekan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_lp_sesekanActionPerformed(evt);
            }
        });

        button_delete_lp_sesekan.setBackground(new java.awt.Color(255, 255, 255));
        button_delete_lp_sesekan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_delete_lp_sesekan.setText("Delete LP Sesekan");
        button_delete_lp_sesekan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_delete_lp_sesekanActionPerformed(evt);
            }
        });

        Table_LP_Sesekan_detail.setAutoCreateRowSorter(true);
        Table_LP_Sesekan_detail.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_LP_Sesekan_detail.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No LP", "Grade", "Bulu Upah", "Gram LP", "Gr Sesekan"
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
        Table_LP_Sesekan_detail.getTableHeader().setReorderingAllowed(false);
        jScrollPane9.setViewportView(Table_LP_Sesekan_detail);

        label_total_keping_LP_sesekan_detail.setBackground(new java.awt.Color(255, 255, 255));
        label_total_keping_LP_sesekan_detail.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        label_total_keping_LP_sesekan_detail.setText("TOTAL");

        jLabel31.setBackground(new java.awt.Color(255, 255, 255));
        jLabel31.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel31.setText("Total Sesekan :");

        label_total_gram_LP_sesekan_detail.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_LP_sesekan_detail.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        label_total_gram_LP_sesekan_detail.setText("TOTAL");

        label_total_lp_sesekan_detail.setBackground(new java.awt.Color(255, 255, 255));
        label_total_lp_sesekan_detail.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        label_total_lp_sesekan_detail.setText("TOTAL");

        jLabel25.setBackground(new java.awt.Color(255, 255, 255));
        jLabel25.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel25.setText("Total Data :");

        jLabel32.setBackground(new java.awt.Color(255, 255, 255));
        jLabel32.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel32.setText("Total Keping :");

        label_total_sesekan_kuning.setBackground(new java.awt.Color(255, 255, 255));
        label_total_sesekan_kuning.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        label_total_sesekan_kuning.setText("TOTAL");

        jLabel33.setBackground(new java.awt.Color(255, 255, 255));
        jLabel33.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel33.setText("Tot SSK Kuning :");

        label_total_sesekan_pasir.setBackground(new java.awt.Color(255, 255, 255));
        label_total_sesekan_pasir.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        label_total_sesekan_pasir.setText("TOTAL");

        jLabel38.setBackground(new java.awt.Color(255, 255, 255));
        jLabel38.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel38.setText("Tot SSK Pasir :");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel21)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_no_lp_sesekan, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel23)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_sub_lp_sesekan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel36)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_Search_LP_Sesekan1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel37)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_Search_LP_Sesekan2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_search_lp_sesekan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_create_lp_sesekan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_edit_lp_sesekan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_delete_lp_sesekan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_print_lp1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_print_lp_semua)
                        .addGap(0, 68, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel24)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_lp_sesekan)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel28)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_keping_LP_sesekan)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel29)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_sesekan_bersih)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel33)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_sesekan_kuning)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel38)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_sesekan_pasir)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(button_export_lp_sesekan))
                            .addComponent(jScrollPane8))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel25)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_lp_sesekan_detail)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel32)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_keping_LP_sesekan_detail)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel31)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_gram_LP_sesekan_detail)))))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_Search_LP_Sesekan1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_Search_LP_Sesekan2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_search_lp_sesekan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_no_lp_sesekan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_sub_lp_sesekan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_create_lp_sesekan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_edit_lp_sesekan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_delete_lp_sesekan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_print_lp1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_print_lp_semua, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label_total_keping_LP_sesekan_detail, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel31, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel28, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(label_total_gram_LP_sesekan_detail, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(label_total_lp_sesekan_detail, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(label_total_keping_LP_sesekan, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel25, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel32, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel29, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(label_total_sesekan_kuning, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel33, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(label_total_sesekan_bersih, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(label_total_sesekan_pasir, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel38, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(button_export_lp_sesekan, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_lp_sesekan, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel24, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 574, Short.MAX_VALUE)
                    .addComponent(jScrollPane9))
                .addContainerGap())
        );

        jTabbedPane1.addTab("LP Sesekan SUB", jPanel2);

        javax.swing.GroupLayout jPanel_laporan_produksiLayout = new javax.swing.GroupLayout(jPanel_laporan_produksi);
        jPanel_laporan_produksi.setLayout(jPanel_laporan_produksiLayout);
        jPanel_laporan_produksiLayout.setHorizontalGroup(
            jPanel_laporan_produksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        jPanel_laporan_produksiLayout.setVerticalGroup(
            jPanel_laporan_produksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jPanel_laporan_produksi, javax.swing.GroupLayout.DEFAULT_SIZE, 1364, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel_laporan_produksi, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void button_delete_lp_sesekanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_delete_lp_sesekanActionPerformed
        // TODO add your handling code here:
        try {
            Utility.db_sub.connect();
            //            DefaultTableModel Table = (DefaultTableModel)Table_laporan_produksi.getModel();
            int j = Table_LP_Sesekan.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih data yang ingin di hapus !");
            } else {
                int dialogResult = JOptionPane.showConfirmDialog(this, "Yakin hapus data ini?", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    // delete code here
                    JOptionPane.showMessageDialog(this, "Fungsi delete belum tersedia");
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_delete_lp_sesekanActionPerformed

    private void button_search_lpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_lpActionPerformed
        // TODO add your handling code here:
        refreshTable_lp();
    }//GEN-LAST:event_button_search_lpActionPerformed

    private void txt_search_lpKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_lpKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_lp();
        }
    }//GEN-LAST:event_txt_search_lpKeyPressed

    private void txt_search_memoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_memoKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_lp();
        }
    }//GEN-LAST:event_txt_search_memoKeyPressed

    private void button_export_lp_sesekanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_lp_sesekanActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Table_LP_Sesekan.getModel();
        ExportToExcel.writeToExcel(model, this);
    }//GEN-LAST:event_button_export_lp_sesekanActionPerformed

    private void button_create_lp_sesekanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_create_lp_sesekanActionPerformed
        // TODO add your handling code here:
        JDialog_Edit_Insert_LP_Sesekan dialog = new JDialog_Edit_Insert_LP_Sesekan(new javax.swing.JFrame(), true, null);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);
        refreshTable_lp_sesekan();
    }//GEN-LAST:event_button_create_lp_sesekanActionPerformed

    private void button_edit_lp_sesekanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_edit_lp_sesekanActionPerformed
        // TODO add your handling code here:
        int x = Table_LP_Sesekan.getSelectedRow();
        if (x > -1) {
            try {
                String no_lp_sesekan = Table_LP_Sesekan.getValueAt(x, 0).toString();
                Utility.db_sub.connect();
                sql = "SELECT * FROM `tb_detail_penyesek` WHERE `no_lp_sesekan` = '" + no_lp_sesekan + "' ";
                rs = Utility.db_sub.getStatement().executeQuery(sql);
                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "Maaf " + no_lp_sesekan + " sudah dikerjakan oleh sub, tidak bisa melakukan edit!");
                } else {
                    JDialog_Edit_Insert_LP_Sesekan dialog = new JDialog_Edit_Insert_LP_Sesekan(new javax.swing.JFrame(), true, no_lp_sesekan);
                    dialog.pack();
                    dialog.setLocationRelativeTo(this);
                    dialog.setVisible(true);
                    dialog.setEnabled(true);
                    refreshTable_lp_sesekan();
                }
            } catch (Exception ex) {
                Logger.getLogger(JPanel_Laporan_Produksi_Sesekan.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_button_edit_lp_sesekanActionPerformed

    private void txt_search_no_kartuKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_no_kartuKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_lp();
        }
    }//GEN-LAST:event_txt_search_no_kartuKeyPressed

    private void button_print_lp_semuaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_print_lp_semuaActionPerformed
        // TODO add your handling code here:
        try {
            if (Table_LP_Sesekan.getRowCount() > 0) {
                refreshTable_lp_sesekan();
                JRDesignQuery newQuery = new JRDesignQuery();
                newQuery.setText(sql);
                JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Laporan_Produksi_Sesekan_Sub.jrxml");
                JASP_DESIGN.setQuery(newQuery);
                JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
                JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, null, Utility.db.getConnection());
                JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
            } else {
                JOptionPane.showMessageDialog(this, "Tidak ada data pada tabel !", "warning!", 1);
            }
        } catch (JRException ex) {
            JOptionPane.showMessageDialog(this, ex.getLocalizedMessage());
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_print_lp_semuaActionPerformed

    private void txt_search_gradeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_gradeKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_lp();
        }
    }//GEN-LAST:event_txt_search_gradeKeyPressed

    private void button_print_lp1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_print_lp1ActionPerformed
        try {
            int j = Table_LP_Sesekan.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Pilih LP yang mau di print", "warning!", 1);
            } else {
                String no_lp = Table_LP_Sesekan.getValueAt(j, 0).toString();
                String query = "SELECT `no_lp_sesekan`, `sub`, `tanggal_lp`, `kode_grade`, `bulu_upah`, `memo`, `keping`, `sesekan_bersih`, `sesekan_kuning`, `sesekan_pasir`, `berat_setelah_cuci`, `nilai_lp` "
                        + "FROM `tb_laporan_produksi_sesekan` WHERE `no_lp_sesekan` = '" + no_lp + "'";
                JRDesignQuery newQuery = new JRDesignQuery();
                newQuery.setText(query);
                JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Laporan_Produksi_Sesekan_Sub.jrxml");
                JASP_DESIGN.setQuery(newQuery);
                JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
//                Map<String, Object> params = new HashMap<String, Object>();
//                params.put("CHEAT", 1);
                JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, null, Utility.db.getConnection());
                JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
            }
        } catch (JRException ex) {
            JOptionPane.showMessageDialog(this, ex.getLocalizedMessage());
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_print_lp1ActionPerformed

    private void button_search_lp_sesekanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_lp_sesekanActionPerformed
        // TODO add your handling code here:
        refreshTable_lp_sesekan();
    }//GEN-LAST:event_button_search_lp_sesekanActionPerformed

    private void txt_search_no_lp_sesekanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_no_lp_sesekanKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_lp_sesekan();
        }
    }//GEN-LAST:event_txt_search_no_lp_sesekanKeyPressed

    private void button_export_lpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_lpActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Table_LP_Sesekan.getModel();
        ExportToExcel.writeToExcel(model, this);
    }//GEN-LAST:event_button_export_lpActionPerformed

    private void Table_laporan_produksiMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Table_laporan_produksiMouseClicked
        // TODO add your handling code here:
        decimalFormat.setMaximumFractionDigits(5);
        decimalFormat.setGroupingUsed(false);
        int i = Table_laporan_produksi.getSelectedRow();
        if (evt.getClickCount() == 2) {
            if (Table_laporan_produksi.getValueAt(i, 8) == null || Table_laporan_produksi.getValueAt(i, 8).toString().equals("")) {
                JDialog_input_sesekanLP dialog = new JDialog_input_sesekanLP(new javax.swing.JFrame(), true, Table_laporan_produksi.getValueAt(i, 0).toString());
                dialog.pack();
                dialog.setLocationRelativeTo(this);
                dialog.setVisible(true);
                dialog.setEnabled(true);
                refreshTable_lp();
//                    String gram_sesekan_sebelumnya = Table_laporan_produksi.getValueAt(i, 9).toString();
//                    String gram_sebelumnya = "";
//                    gram_sebelumnya = gram_sesekan_sebelumnya.replace(",", "");
//                    String gram_sesekan = JOptionPane.showInputDialog("Masukkan Gram Sesekan : ", gram_sebelumnya);
//                    if (gram_sesekan != null && !gram_sesekan.equals("")) {
//                        double GRAM_SSK = Double.valueOf(gram_sesekan);
//                        decimalFormat = Utility.DecimalFormatUS(decimalFormat);
//                        sql = "UPDATE `tb_laporan_produksi` SET `gram_sesekan_lp`='" + decimalFormat.format(GRAM_SSK) + "' "
//                                + "WHERE `no_laporan_produksi`='" + Table_laporan_produksi.getValueAt(i, 0).toString() + "' ";
//                        if ((Utility.db.getStatement().executeUpdate(sql)) == 1) {
//                            refreshTable_lp();
//                            JOptionPane.showMessageDialog(this, "Update success!");
//                        } else {
//                            JOptionPane.showMessageDialog(this, "Update failed!");
//                        }
//                    }
            } else {
                JOptionPane.showMessageDialog(this, "Maaf LP sudah menjadi LP sub, tidak bisa edit gram sesekan !");
            }
        }
    }//GEN-LAST:event_Table_laporan_produksiMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_Ruang_LP;
    private javax.swing.JComboBox<String> ComboBox_sub_lp_sesekan;
    private com.toedter.calendar.JDateChooser Date_Search_LP_1;
    private com.toedter.calendar.JDateChooser Date_Search_LP_2;
    private com.toedter.calendar.JDateChooser Date_Search_LP_Sesekan1;
    private com.toedter.calendar.JDateChooser Date_Search_LP_Sesekan2;
    public static javax.swing.JTable Table_LP_Sesekan;
    public static javax.swing.JTable Table_LP_Sesekan_detail;
    public static javax.swing.JTable Table_laporan_produksi;
    public javax.swing.JButton button_create_lp_sesekan;
    public javax.swing.JButton button_delete_lp_sesekan;
    public javax.swing.JButton button_edit_lp_sesekan;
    private javax.swing.JButton button_export_lp;
    private javax.swing.JButton button_export_lp_sesekan;
    private javax.swing.JButton button_print_lp1;
    private javax.swing.JButton button_print_lp_semua;
    private javax.swing.JButton button_search_lp;
    private javax.swing.JButton button_search_lp_sesekan;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel_laporan_produksi;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel label_total_gram_LP;
    private javax.swing.JLabel label_total_gram_LP_sesekan_detail;
    private javax.swing.JLabel label_total_gram_SesekanLP;
    private javax.swing.JLabel label_total_keping_LP;
    private javax.swing.JLabel label_total_keping_LP_sesekan;
    private javax.swing.JLabel label_total_keping_LP_sesekan_detail;
    private javax.swing.JLabel label_total_lp;
    private javax.swing.JLabel label_total_lp_sesekan;
    private javax.swing.JLabel label_total_lp_sesekan_detail;
    private javax.swing.JLabel label_total_sesekan_bersih;
    private javax.swing.JLabel label_total_sesekan_kuning;
    private javax.swing.JLabel label_total_sesekan_pasir;
    private javax.swing.JTextField txt_search_grade;
    private javax.swing.JTextField txt_search_lp;
    private javax.swing.JTextField txt_search_memo;
    private javax.swing.JTextField txt_search_no_kartu;
    private javax.swing.JTextField txt_search_no_lp_sesekan;
    // End of variables declaration//GEN-END:variables
}
