package waleta_system.BahanJadi;

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
import waleta_system.Class.Utility;
import waleta_system.Class.ExportToExcel;

public class JPanel_StokOpnameGBJ extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();

    public JPanel_StokOpnameGBJ() {
        initComponents();
    }

    public void init() {
        try {
            ComboBox_lokasi.removeAllItems();
            ComboBox_lokasi.addItem("All");
            ComboBox_lokasi_boxScan.removeAllItems();
            ComboBox_lokasi_boxScan.addItem("All");
            sql = "SELECT DISTINCT(`lokasi_terakhir`) AS 'lokasi_terakhir' FROM `tb_stokopname_gbj`";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_lokasi.addItem(rs.getString("lokasi_terakhir"));
                ComboBox_lokasi_boxScan.addItem(rs.getString("lokasi_terakhir"));
            }
            refreshTable_DataSO();
            tabel_stockOpname.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent event) {
                    if (!event.getValueIsAdjusting() && tabel_stockOpname.getSelectedRow() != -1) {
                        int x = tabel_stockOpname.getSelectedRow();
                        if (x > -1) {
                            label_tanggal_SO.setText(tabel_stockOpname.getValueAt(x, 0).toString());
                            label_tanggal_SO_rekapGrade.setText(tabel_stockOpname.getValueAt(x, 0).toString());
                            refreshTable_detail_boxStok(tabel_stockOpname.getValueAt(x, 0).toString());
                            label_tanggal_SO1.setText(tabel_stockOpname.getValueAt(x, 0).toString());
                            refreshTable_detail_boxScan(tabel_stockOpname.getValueAt(x, 0).toString());
                        }
                    }
                }
            });
        } catch (Exception ex) {
            Logger.getLogger(JPanel_StokOpnameGBJ.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_DataSO() {
        try {
            DefaultTableModel model = (DefaultTableModel) tabel_stockOpname.getModel();
            model.setRowCount(0);
            Object[] row = new Object[10];
            sql = "SELECT `tb_stokopname`.`tgl_stok_opname`, COUNT(`no_box`) AS 'box', SUM(`berat`) AS 'berat', SUM(`keping`) AS 'kpg', `status_stok_opname_bj` \n"
                    + "FROM `tb_stokopname` "
                    + "LEFT JOIN `tb_stokopname_gbj` ON `tb_stokopname`.`tgl_stok_opname` = `tb_stokopname_gbj`.`tgl_stok_opname` "
                    + "WHERE 1 "
                    + "GROUP BY `tb_stokopname`.`tgl_stok_opname` "
                    + "ORDER BY `tb_stokopname`.`tgl_stok_opname` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                row[0] = rs.getDate("tgl_stok_opname");
                row[1] = rs.getDouble("kpg");
                row[2] = rs.getDouble("berat");
                row[3] = rs.getInt("box");
                if (rs.getBoolean("status_stok_opname_bj")) {
                    row[4] = "SELESAI";
                } else {
                    row[4] = "PROSES";
                }
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(tabel_stockOpname);
            label_total_data_SO.setText(Integer.toString(tabel_stockOpname.getRowCount()));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_StokOpnameGBJ.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_detail_boxStok(String tgl_SO) {
        try {
            String lokasi = "AND `lokasi_terakhir` = '" + ComboBox_lokasi.getSelectedItem().toString() + "' ";
            if (ComboBox_lokasi.getSelectedItem().toString().equals("All")) {
                lokasi = "";
            }
            float total_keping = 0, total_gram = 0;
            DefaultTableModel model = (DefaultTableModel) tabel_detailBox_stok.getModel();
            model.setRowCount(0);
            sql = "SELECT `tgl_stok_opname`, `no_box`, `tanggal_box`, `tb_grade_bahan_jadi`.`kode_grade`, `bentuk_grade`, `Kategori1`, `keping`, `berat`, `no_tutupan`, `lokasi_terakhir`, `harga_cny_kg`, data_scan.`no_box_scan` AS 'status' \n"
                    + "FROM `tb_stokopname_gbj` "
                    + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_stokopname_gbj`.`kode_grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode`\n"
                    + "LEFT JOIN (SELECT `no_box_scan` FROM `tb_stokopname_gbj_scan` WHERE `tgl_stok_opname` = '" + tgl_SO + "') data_scan "
                    + "ON `tb_stokopname_gbj`.`no_box` = data_scan.`no_box_scan`\n"
                    + "WHERE `tgl_stok_opname` = '" + tgl_SO + "' "
                    + "AND `tb_grade_bahan_jadi`.`kode_grade` LIKE '%" + txt_search_grade.getText() + "%' "
                    + "AND `no_box` LIKE '%" + txt_search_box.getText() + "%' "
                    + "AND `no_tutupan` LIKE '%" + txt_search_tutupan.getText() + "%'"
                    + lokasi;
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[10];
            while (rs.next()) {
                row[0] = rs.getString("no_box");
                row[1] = rs.getDate("tanggal_box");
                row[2] = rs.getString("kode_grade");
                row[3] = rs.getString("bentuk_grade");
                row[4] = rs.getString("Kategori1");
                row[5] = rs.getInt("keping");
                total_keping = total_keping + rs.getInt("keping");
                row[6] = rs.getFloat("berat");
                total_gram = total_gram + rs.getFloat("berat");
                row[7] = rs.getString("no_tutupan");
                row[8] = rs.getString("lokasi_terakhir");
                row[9] = rs.getString("status");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(tabel_detailBox_stok);
            label_total_keping_box.setText(decimalFormat.format(total_keping));
            label_total_gram_box.setText(decimalFormat.format(total_gram));
            label_total_box.setText(Integer.toString(tabel_detailBox_stok.getRowCount()));

            int total_keping_rekap = 0;
            float total_gram_rekap = 0;
            DefaultTableModel model_rekap = (DefaultTableModel) tabel_stok_rekap.getModel();
            model_rekap.setRowCount(0);
            sql = "SELECT `tb_grade_bahan_jadi`.`kode_grade`, `bentuk_grade`, `Kategori1`, SUM(`keping`) AS 'keping', SUM(`berat`) AS 'berat' \n"
                    + "FROM `tb_stokopname_gbj` "
                    + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_stokopname_gbj`.`kode_grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode`\n"
                    + "WHERE `tgl_stok_opname` = '" + tgl_SO + "' "
                    + "AND `tb_grade_bahan_jadi`.`kode_grade` LIKE '%" + txt_search_grade.getText() + "%' "
                    + lokasi
                    + " GROUP BY `tb_grade_bahan_jadi`.`kode_grade`";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] baris = new Object[10];
            while (rs.next()) {
                baris[0] = rs.getString("kode_grade");
                baris[1] = rs.getString("bentuk_grade");
                baris[2] = rs.getString("Kategori1");
                baris[3] = rs.getInt("keping");
                baris[4] = rs.getFloat("berat");
                model_rekap.addRow(baris);
                total_keping_rekap = total_keping_rekap + rs.getInt("keping");
                total_gram_rekap = total_gram_rekap + rs.getFloat("berat");
            }
            ColumnsAutoSizer.sizeColumnsToFit(tabel_stok_rekap);
            label_total_keping_rekap.setText(decimalFormat.format(total_keping_rekap));
            label_total_gram_rekap.setText(decimalFormat.format(total_gram_rekap));
            label_total_grade_rekap.setText(Integer.toString(tabel_stok_rekap.getRowCount()));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_StokOpnameGBJ.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_detail_boxScan(String tgl_SO) {
        try {
            String lokasi = "AND `tb_stokopname_gbj_scan`.`lokasi` = '" + ComboBox_lokasi_boxScan.getSelectedItem().toString() + "' ";
            if (ComboBox_lokasi_boxScan.getSelectedItem().toString().equals("All")) {
                lokasi = "";
            }
            float total_keping = 0, total_gram = 0;
            DefaultTableModel model = (DefaultTableModel) tabel_detailBox_scan.getModel();
            model.setRowCount(0);
            sql = "SELECT `tgl_stok_opname`, `no_box_scan`, `tanggal_box`, `tb_grade_bahan_jadi`.`kode_grade`, `bentuk_grade`, `Kategori1`, `tb_stokopname_gbj_scan`.`kpg`, `tb_stokopname_gbj_scan`.`berat`, `no_tutupan`, `tb_stokopname_gbj_scan`.`lokasi`, data_so.`no_box` AS 'status' \n"
                    + "FROM `tb_stokopname_gbj_scan` "
                    + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_stokopname_gbj_scan`.`kode_grade` = `tb_grade_bahan_jadi`.`kode`\n"
                    + "LEFT JOIN `tb_box_bahan_jadi` ON `tb_stokopname_gbj_scan`.`no_box_scan` = `tb_box_bahan_jadi`.`no_box`\n"
                    + "LEFT JOIN (SELECT `no_box` FROM `tb_stokopname_gbj` WHERE `tgl_stok_opname` = '" + tgl_SO + "') data_so "
                    + "ON `tb_stokopname_gbj_scan`.`no_box_scan` = data_so.`no_box`\n"
                    + "WHERE `tgl_stok_opname` = '" + tgl_SO + "' "
                    + "AND `tb_grade_bahan_jadi`.`kode_grade` LIKE '%" + txt_search_grade_boxScan.getText() + "%' "
                    + "AND `no_box_scan` LIKE '%" + txt_search_boxScan.getText() + "%' "
                    + "AND `no_tutupan` LIKE '%" + txt_search_tutupan_boxScan.getText() + "%'"
                    + lokasi;
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[10];
            while (rs.next()) {
                row[0] = rs.getString("no_box_scan");
                row[1] = rs.getDate("tanggal_box");
                row[2] = rs.getString("kode_grade");
                row[3] = rs.getString("bentuk_grade");
                row[4] = rs.getString("Kategori1");
                row[5] = rs.getInt("kpg");
                total_keping = total_keping + rs.getInt("kpg");
                row[6] = rs.getFloat("berat");
                total_gram = total_gram + rs.getFloat("berat");
                row[7] = rs.getString("no_tutupan");
                row[8] = rs.getString("lokasi");
                row[9] = rs.getString("status");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(tabel_detailBox_scan);
            label_total_keping_boxScan.setText(decimalFormat.format(total_keping));
            label_total_gram_boxScan.setText(decimalFormat.format(total_gram));
            label_total_boxScan.setText(Integer.toString(tabel_detailBox_scan.getRowCount()));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_StokOpnameGBJ.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tabel_stockOpname = new javax.swing.JTable();
        jLabel14 = new javax.swing.JLabel();
        label_total_data_SO = new javax.swing.JLabel();
        button_save_data_stokOpname = new javax.swing.JButton();
        button_refresh_data_stokOpname = new javax.swing.JButton();
        button_detele_data_stokOpname = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        label_tanggal_SO = new javax.swing.JLabel();
        button_Export_tabelbox_stok = new javax.swing.JButton();
        label_total_keping_box = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        label_total_box = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        label_total_gram_box = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabel_detailBox_stok = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        txt_search_grade = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        txt_search_box = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        txt_search_tutupan = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        ComboBox_lokasi = new javax.swing.JComboBox<>();
        button_search = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        label_tanggal_SO_rekapGrade = new javax.swing.JLabel();
        button_Export_rekap_grade = new javax.swing.JButton();
        label_total_keping_rekap = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        label_total_grade_rekap = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        label_total_gram_rekap = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        tabel_stok_rekap = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        label_tanggal_SO1 = new javax.swing.JLabel();
        button_Export_tabel_boxScan = new javax.swing.JButton();
        label_total_keping_boxScan = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        label_total_boxScan = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        label_total_gram_boxScan = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tabel_detailBox_scan = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        txt_search_grade_boxScan = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        txt_search_boxScan = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        txt_search_tutupan_boxScan = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        ComboBox_lokasi_boxScan = new javax.swing.JComboBox<>();
        button_refresh_boxScan = new javax.swing.JButton();
        button_delete_boxScan = new javax.swing.JButton();
        button_stokOpname_selesai = new javax.swing.JButton();
        button_print = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Stock Barang Jadi", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel13.setText("Data Stock Opname");

        tabel_stockOpname.setAutoCreateRowSorter(true);
        tabel_stockOpname.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tabel_stockOpname.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tanggal Stok Opname", "Total Stok Kpg", "Total Stok Gram", "Jumlah Box", "Status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Double.class, java.lang.Double.class, java.lang.Float.class, java.lang.String.class
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
        tabel_stockOpname.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(tabel_stockOpname);

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel14.setText("Total Data :");

        label_total_data_SO.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_SO.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_data_SO.setText("0");

        button_save_data_stokOpname.setBackground(new java.awt.Color(255, 255, 255));
        button_save_data_stokOpname.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_save_data_stokOpname.setText("Save Data Stok");
        button_save_data_stokOpname.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_save_data_stokOpnameActionPerformed(evt);
            }
        });

        button_refresh_data_stokOpname.setBackground(new java.awt.Color(255, 255, 255));
        button_refresh_data_stokOpname.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_refresh_data_stokOpname.setText("Refresh");
        button_refresh_data_stokOpname.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refresh_data_stokOpnameActionPerformed(evt);
            }
        });

        button_detele_data_stokOpname.setBackground(new java.awt.Color(255, 255, 255));
        button_detele_data_stokOpname.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_detele_data_stokOpname.setText("Delete");
        button_detele_data_stokOpname.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_detele_data_stokOpnameActionPerformed(evt);
            }
        });

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel11.setText("Stock Box Barang Jadi");

        label_tanggal_SO.setBackground(new java.awt.Color(255, 255, 255));
        label_tanggal_SO.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_tanggal_SO.setForeground(new java.awt.Color(255, 0, 0));
        label_tanggal_SO.setText("dd MMM yyyy");

        button_Export_tabelbox_stok.setBackground(new java.awt.Color(255, 255, 255));
        button_Export_tabelbox_stok.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_Export_tabelbox_stok.setText("Export to Excel");
        button_Export_tabelbox_stok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_Export_tabelbox_stokActionPerformed(evt);
            }
        });

        label_total_keping_box.setBackground(new java.awt.Color(255, 255, 255));
        label_total_keping_box.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_keping_box.setText("0");

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel9.setText("Total Keping :");

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel7.setText("Total Gram :");

        label_total_box.setBackground(new java.awt.Color(255, 255, 255));
        label_total_box.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_box.setText("0");

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel8.setText("Total Box :");

        label_total_gram_box.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_box.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_gram_box.setText("0");

        tabel_detailBox_stok.setAutoCreateRowSorter(true);
        tabel_detailBox_stok.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tabel_detailBox_stok.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No Box", "Tgl BOX", "Grade", "Bentuk", "Kategori", "Kpg", "Gram", "No Tutupan", "Lokasi", "Status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Float.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
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
        tabel_detailBox_stok.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(tabel_detailBox_stok);
        if (tabel_detailBox_stok.getColumnModel().getColumnCount() > 0) {
            tabel_detailBox_stok.getColumnModel().getColumn(0).setHeaderValue("No Box");
            tabel_detailBox_stok.getColumnModel().getColumn(1).setHeaderValue("Tgl BOX");
            tabel_detailBox_stok.getColumnModel().getColumn(7).setHeaderValue("No Tutupan");
            tabel_detailBox_stok.getColumnModel().getColumn(8).setHeaderValue("Lokasi");
            tabel_detailBox_stok.getColumnModel().getColumn(9).setHeaderValue("Status");
        }

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("Grade:");

        txt_search_grade.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_grade.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_gradeKeyPressed(evt);
            }
        });

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel17.setText("No Box :");

        txt_search_box.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_box.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_boxKeyPressed(evt);
            }
        });

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel18.setText("Tutupan :");

        txt_search_tutupan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_tutupan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_tutupanKeyPressed(evt);
            }
        });

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel12.setText("Lokasi :");

        ComboBox_lokasi.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_lokasi.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        button_search.setBackground(new java.awt.Color(255, 255, 255));
        button_search.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search.setText("Refresh");
        button_search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_searchActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 912, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_tanggal_SO))
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
                                .addComponent(label_total_gram_box)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_grade, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_box, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_tutupan, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_lokasi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_search)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_Export_tabelbox_stok)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_tanggal_SO, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_grade, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_lokasi, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_box, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_tutupan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_search, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_Export_tabelbox_stok, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 549, Short.MAX_VALUE)
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

        jTabbedPane1.addTab("Data Box Stok Barang Jadi", jPanel2);

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        jLabel23.setBackground(new java.awt.Color(255, 255, 255));
        jLabel23.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel23.setText("Stock Box Barang Jadi");

        label_tanggal_SO_rekapGrade.setBackground(new java.awt.Color(255, 255, 255));
        label_tanggal_SO_rekapGrade.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_tanggal_SO_rekapGrade.setForeground(new java.awt.Color(255, 0, 0));
        label_tanggal_SO_rekapGrade.setText("dd MMM yyyy");

        button_Export_rekap_grade.setBackground(new java.awt.Color(255, 255, 255));
        button_Export_rekap_grade.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_Export_rekap_grade.setText("Export to Excel");
        button_Export_rekap_grade.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_Export_rekap_gradeActionPerformed(evt);
            }
        });

        label_total_keping_rekap.setBackground(new java.awt.Color(255, 255, 255));
        label_total_keping_rekap.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_keping_rekap.setText("0");

        jLabel24.setBackground(new java.awt.Color(255, 255, 255));
        jLabel24.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel24.setText("Total Keping :");

        jLabel25.setBackground(new java.awt.Color(255, 255, 255));
        jLabel25.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel25.setText("Total Gram :");

        label_total_grade_rekap.setBackground(new java.awt.Color(255, 255, 255));
        label_total_grade_rekap.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_grade_rekap.setText("0");

        jLabel26.setBackground(new java.awt.Color(255, 255, 255));
        jLabel26.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel26.setText("Total Grade :");

        label_total_gram_rekap.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_rekap.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_gram_rekap.setText("0");

        tabel_stok_rekap.setAutoCreateRowSorter(true);
        tabel_stok_rekap.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tabel_stok_rekap.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Grade", "Bentuk", "Kategori", "Kpg", "Gram"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Float.class
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
        tabel_stok_rekap.getTableHeader().setReorderingAllowed(false);
        jScrollPane5.setViewportView(tabel_stok_rekap);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 912, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel23)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_tanggal_SO_rekapGrade))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel26)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_grade_rekap)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel24)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_keping_rekap)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel25)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_gram_rekap))
                            .addComponent(button_Export_rekap_grade))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_tanggal_SO_rekapGrade, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_Export_rekap_grade, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 549, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel25)
                    .addComponent(label_total_gram_rekap)
                    .addComponent(jLabel26)
                    .addComponent(label_total_grade_rekap)
                    .addComponent(jLabel24)
                    .addComponent(label_total_keping_rekap))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Rekap / Grade", jPanel4);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel15.setText("Stock Box Barang Jadi");

        label_tanggal_SO1.setBackground(new java.awt.Color(255, 255, 255));
        label_tanggal_SO1.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_tanggal_SO1.setForeground(new java.awt.Color(255, 0, 0));
        label_tanggal_SO1.setText("dd MMM yyyy");

        button_Export_tabel_boxScan.setBackground(new java.awt.Color(255, 255, 255));
        button_Export_tabel_boxScan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_Export_tabel_boxScan.setText("Export to Excel");
        button_Export_tabel_boxScan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_Export_tabel_boxScanActionPerformed(evt);
            }
        });

        label_total_keping_boxScan.setBackground(new java.awt.Color(255, 255, 255));
        label_total_keping_boxScan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_keping_boxScan.setText("0");

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel10.setText("Total Keping :");

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel16.setText("Total Gram :");

        label_total_boxScan.setBackground(new java.awt.Color(255, 255, 255));
        label_total_boxScan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_boxScan.setText("0");

        jLabel19.setBackground(new java.awt.Color(255, 255, 255));
        jLabel19.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel19.setText("Total Box :");

        label_total_gram_boxScan.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_boxScan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_gram_boxScan.setText("0");

        tabel_detailBox_scan.setAutoCreateRowSorter(true);
        tabel_detailBox_scan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tabel_detailBox_scan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No Box", "Tgl BOX", "Grade", "Bentuk", "Kategori", "Kpg", "Gram", "No Tutupan", "Lokasi", "Status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Float.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
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
        tabel_detailBox_scan.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(tabel_detailBox_scan);

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("Grade:");

        txt_search_grade_boxScan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_grade_boxScan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_grade_boxScanKeyPressed(evt);
            }
        });

        jLabel20.setBackground(new java.awt.Color(255, 255, 255));
        jLabel20.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel20.setText("No Box :");

        txt_search_boxScan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_boxScan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_boxScanKeyPressed(evt);
            }
        });

        jLabel21.setBackground(new java.awt.Color(255, 255, 255));
        jLabel21.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel21.setText("Tutupan :");

        txt_search_tutupan_boxScan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_tutupan_boxScan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_tutupan_boxScanKeyPressed(evt);
            }
        });

        jLabel22.setBackground(new java.awt.Color(255, 255, 255));
        jLabel22.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel22.setText("Lokasi :");

        ComboBox_lokasi_boxScan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_lokasi_boxScan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        button_refresh_boxScan.setBackground(new java.awt.Color(255, 255, 255));
        button_refresh_boxScan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_refresh_boxScan.setText("Refresh");
        button_refresh_boxScan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refresh_boxScanActionPerformed(evt);
            }
        });

        button_delete_boxScan.setBackground(new java.awt.Color(255, 255, 255));
        button_delete_boxScan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_delete_boxScan.setText("Del");
        button_delete_boxScan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_delete_boxScanActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 912, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_grade_boxScan, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel20)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_boxScan, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel21)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_tutupan_boxScan, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel22)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_lokasi_boxScan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_tanggal_SO1))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel19)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_boxScan)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_keping_boxScan)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel16)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_gram_boxScan)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_refresh_boxScan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_delete_boxScan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_Export_tabel_boxScan)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_tanggal_SO1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(button_Export_tabel_boxScan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_delete_boxScan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_search_grade_boxScan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(ComboBox_lokasi_boxScan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_search_boxScan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_search_tutupan_boxScan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_refresh_boxScan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 549, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel16)
                    .addComponent(label_total_gram_boxScan)
                    .addComponent(jLabel19)
                    .addComponent(label_total_boxScan)
                    .addComponent(jLabel10)
                    .addComponent(label_total_keping_boxScan))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Data Box Scan", jPanel3);

        button_stokOpname_selesai.setBackground(new java.awt.Color(255, 255, 255));
        button_stokOpname_selesai.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_stokOpname_selesai.setText("SO Selesai");
        button_stokOpname_selesai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_stokOpname_selesaiActionPerformed(evt);
            }
        });

        button_print.setBackground(new java.awt.Color(255, 255, 255));
        button_print.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_print.setText("Print");
        button_print.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_printActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel13)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_data_SO))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(button_refresh_data_stokOpname)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_detele_data_stokOpname)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_save_data_stokOpname)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_stokOpname_selesai)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_print))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_refresh_data_stokOpname, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_detele_data_stokOpname, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_save_data_stokOpname, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_stokOpname_selesai, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_print, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel14)
                    .addComponent(label_total_data_SO))
                .addContainerGap())
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
        refreshTable_detail_boxStok(label_tanggal_SO.getText());
    }//GEN-LAST:event_button_searchActionPerformed

    private void button_Export_tabelbox_stokActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_Export_tabelbox_stokActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) tabel_detailBox_stok.getModel();
        ExportToExcel.writeToExcel(model, jPanel1);
    }//GEN-LAST:event_button_Export_tabelbox_stokActionPerformed

    private void button_save_data_stokOpnameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_save_data_stokOpnameActionPerformed
        // TODO add your handling code here:
        try {
            sql = "SELECT `tgl_stok_opname` FROM `tb_stokopname` WHERE `status_stok_opname_bj` = 0";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Maaf Stok Opname tanggal " + rs.getString("tgl_stok_opname") + " belum selesai\ntidak bisa memulai stok opname baru!");
            } else {
                JDialog_SaveStokOpname dialog = new JDialog_SaveStokOpname(new javax.swing.JFrame(), true);
                dialog.pack();
                dialog.setLocationRelativeTo(this);
                dialog.setVisible(true);
                dialog.setEnabled(true);
                dialog.setResizable(false);
                refreshTable_DataSO();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_StokOpnameGBJ.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_save_data_stokOpnameActionPerformed

    private void txt_search_gradeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_gradeKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_detail_boxStok(label_tanggal_SO.getText());
        }
    }//GEN-LAST:event_txt_search_gradeKeyPressed

    private void txt_search_boxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_boxKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_detail_boxStok(label_tanggal_SO.getText());
        }
    }//GEN-LAST:event_txt_search_boxKeyPressed

    private void txt_search_tutupanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_tutupanKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_detail_boxStok(label_tanggal_SO.getText());
        }
    }//GEN-LAST:event_txt_search_tutupanKeyPressed

    private void button_refresh_data_stokOpnameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refresh_data_stokOpnameActionPerformed
        // TODO add your handling code here:
        refreshTable_DataSO();
    }//GEN-LAST:event_button_refresh_data_stokOpnameActionPerformed

    private void button_detele_data_stokOpnameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_detele_data_stokOpnameActionPerformed
        // TODO add your handling code here:
        try {
            int j = tabel_stockOpname.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih data tanggal stok opname yang akan di hapus !");
            } else {
                int dialogResult = JOptionPane.showConfirmDialog(this, "yakin akan menghapus data ini?", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    // delete code here
                    String Query = "DELETE FROM `tb_stokopname` WHERE `tgl_stok_opname` = '" + tabel_stockOpname.getValueAt(j, 0).toString() + "'";
                    Utility.db.getConnection().createStatement();
                    if ((Utility.db.getStatement().executeUpdate(Query)) > 0) {
                        JOptionPane.showMessageDialog(this, "data DELETE Successfully");
                        refreshTable_DataSO();
                    } else {
                        JOptionPane.showMessageDialog(this, "data not DELETE");
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_StokOpnameGBJ.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_detele_data_stokOpnameActionPerformed

    private void button_Export_tabel_boxScanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_Export_tabel_boxScanActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) tabel_detailBox_scan.getModel();
        ExportToExcel.writeToExcel(model, jPanel1);
    }//GEN-LAST:event_button_Export_tabel_boxScanActionPerformed

    private void txt_search_grade_boxScanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_grade_boxScanKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_detail_boxScan(label_tanggal_SO1.getText());
        }
    }//GEN-LAST:event_txt_search_grade_boxScanKeyPressed

    private void txt_search_boxScanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_boxScanKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_detail_boxScan(label_tanggal_SO1.getText());
        }
    }//GEN-LAST:event_txt_search_boxScanKeyPressed

    private void txt_search_tutupan_boxScanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_tutupan_boxScanKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_detail_boxScan(label_tanggal_SO1.getText());
        }
    }//GEN-LAST:event_txt_search_tutupan_boxScanKeyPressed

    private void button_refresh_boxScanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refresh_boxScanActionPerformed
        // TODO add your handling code here:
        refreshTable_detail_boxScan(label_tanggal_SO1.getText());
    }//GEN-LAST:event_button_refresh_boxScanActionPerformed

    private void button_delete_boxScanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_delete_boxScanActionPerformed
        // TODO add your handling code here:
        try {
            int j = tabel_detailBox_scan.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih data hasil scan yang ingin dihapus !");
            } else {
                int dialogResult = JOptionPane.showConfirmDialog(this, "Hapus data??", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    String Query = "DELETE FROM `tb_stokopname_gbj_scan` WHERE `tgl_stok_opname` = '" + label_tanggal_SO1.getText() + "' AND `no_box_scan` = '" + tabel_detailBox_scan.getValueAt(j, 0).toString() + "'";
                    Utility.db.getConnection().createStatement();
                    if ((Utility.db.getStatement().executeUpdate(Query)) > 0) {
                        JOptionPane.showMessageDialog(this, "data SAVED");
                        refreshTable_DataSO();
                    } else {
                        JOptionPane.showMessageDialog(this, "FAILED !!");
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_StokOpnameGBJ.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_delete_boxScanActionPerformed

    private void button_stokOpname_selesaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_stokOpname_selesaiActionPerformed
        // TODO add your handling code here:
        try {
            int j = tabel_stockOpname.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih tanggal stok opname yang sudah selesai !");
            } else {
                int dialogResult = JOptionPane.showConfirmDialog(this, "Stok Opname Barang Jadi sudah selesai??", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    String Query = "UPDATE `tb_stokopname` SET `status_stok_opname_bj`=1 WHERE `tgl_stok_opname` = '" + tabel_stockOpname.getValueAt(j, 0).toString() + "'";
                    Utility.db.getConnection().createStatement();
                    if ((Utility.db.getStatement().executeUpdate(Query)) > 0) {
                        JOptionPane.showMessageDialog(this, "data SAVED");
                        refreshTable_DataSO();
                    } else {
                        JOptionPane.showMessageDialog(this, "FAILED !!");
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_StokOpnameGBJ.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_stokOpname_selesaiActionPerformed

    private void button_Export_rekap_gradeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_Export_rekap_gradeActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) tabel_stok_rekap.getModel();
        ExportToExcel.writeToExcel(model, jPanel1);
    }//GEN-LAST:event_button_Export_rekap_gradeActionPerformed

    private void button_printActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_printActionPerformed
        // TODO add your handling code here:
        try {
            int j = tabel_stockOpname.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih tanggal SO di tabel!", "warning!", 1);
            } else {
                String query = "SELECT `tb_grade_bahan_jadi`.`kode_grade`, `bentuk_grade`, `Kategori1`, COUNT(`no_box`) AS 'jumlah_box', SUM(`keping`) AS 'keping', SUM(`berat`) AS 'berat' \n"
                        + "FROM `tb_stokopname_gbj` "
                        + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_stokopname_gbj`.`kode_grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode`\n"
                        + "WHERE `tgl_stok_opname` = '" + tabel_stockOpname.getValueAt(j, 0).toString() + "' "
                        + " GROUP BY `tb_grade_bahan_jadi`.`kode_grade`"
                        + "ORDER BY `Kategori1`";
                JRDesignQuery newQuery = new JRDesignQuery();
                newQuery.setText(query);
                JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Laporan_StokOpname_GBJ.jrxml");
                JASP_DESIGN.setQuery(newQuery);

                JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
                JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, null, Utility.db.getConnection());
                JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
            }
        } catch (JRException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_StokOpnameGBJ.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_printActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_lokasi;
    private javax.swing.JComboBox<String> ComboBox_lokasi_boxScan;
    private javax.swing.JButton button_Export_rekap_grade;
    private javax.swing.JButton button_Export_tabel_boxScan;
    private javax.swing.JButton button_Export_tabelbox_stok;
    private javax.swing.JButton button_delete_boxScan;
    private javax.swing.JButton button_detele_data_stokOpname;
    private javax.swing.JButton button_print;
    private javax.swing.JButton button_refresh_boxScan;
    private javax.swing.JButton button_refresh_data_stokOpname;
    private javax.swing.JButton button_save_data_stokOpname;
    private javax.swing.JButton button_search;
    private javax.swing.JButton button_stokOpname_selesai;
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
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel label_tanggal_SO;
    private javax.swing.JLabel label_tanggal_SO1;
    private javax.swing.JLabel label_tanggal_SO_rekapGrade;
    private javax.swing.JLabel label_total_box;
    private javax.swing.JLabel label_total_boxScan;
    private javax.swing.JLabel label_total_data_SO;
    private javax.swing.JLabel label_total_grade_rekap;
    private javax.swing.JLabel label_total_gram_box;
    private javax.swing.JLabel label_total_gram_boxScan;
    private javax.swing.JLabel label_total_gram_rekap;
    private javax.swing.JLabel label_total_keping_box;
    private javax.swing.JLabel label_total_keping_boxScan;
    private javax.swing.JLabel label_total_keping_rekap;
    private javax.swing.JTable tabel_detailBox_scan;
    private javax.swing.JTable tabel_detailBox_stok;
    private javax.swing.JTable tabel_stockOpname;
    private javax.swing.JTable tabel_stok_rekap;
    private javax.swing.JTextField txt_search_box;
    private javax.swing.JTextField txt_search_boxScan;
    private javax.swing.JTextField txt_search_grade;
    private javax.swing.JTextField txt_search_grade_boxScan;
    private javax.swing.JTextField txt_search_tutupan;
    private javax.swing.JTextField txt_search_tutupan_boxScan;
    // End of variables declaration//GEN-END:variables
}
