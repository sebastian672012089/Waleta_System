package waleta_system.Packing;

import java.awt.HeadlessException;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;
import waleta_system.BahanBaku.JPanel_GradeBahanBaku;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;
import waleta_system.Class.ExportToExcel;
import waleta_system.MainForm;

public class JPanel_pengiriman extends javax.swing.JPanel {

    String sql = null;
    String sql2 = null;
    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();

    public JPanel_pengiriman() {
        initComponents();
    }

    public void init() {
        try {
            refreshTable_pengiriman();
            tabel_data_pengiriman.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent event) {
                    if (!event.getValueIsAdjusting() && tabel_data_pengiriman.getSelectedRow() != -1) {
                        int i = tabel_data_pengiriman.getSelectedRow();
                        label_invoice.setText(tabel_data_pengiriman.getValueAt(i, 0).toString());
                        refreshTable_Box();
                        refreshTable_RekapBox();

                        if (tabel_data_pengiriman.getValueAt(i, 8).toString().equals("DELIVERED")) {
                            button_kirim.setText("PROSES");
//                            button_kirim.setEnabled(false);
                            button_delete_pengiriman.setEnabled(false);
                            button_edit_pengiriman.setEnabled(false);
                        } else {
                            button_kirim.setText("KIRIM");
//                            button_kirim.setEnabled(true);
                            button_delete_pengiriman.setEnabled(true);
                            button_edit_pengiriman.setEnabled(true);
                        }
                    }
                }
            });
        } catch (Exception ex) {
            Logger.getLogger(JPanel_GradeBahanBaku.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_pengiriman() {
        try {
            String SearchCat = null, SearchJenisPengiriman = null;
            double total_kpg = 0;
            double total_gram = 0;
            if (ComboBox_Search.getSelectedItem() == "NO INVOICE") {
                SearchCat = "invoice_no";
            } else if (ComboBox_Search.getSelectedItem() == "NAMA BUYER") {
                SearchCat = "nama";
            } else if (ComboBox_Search.getSelectedItem() == "NO DOKUMEN") {
                SearchCat = "no_dokumen";
            }

            if (ComboBox_jenisPengiriman.getSelectedItem() == "All") {
                SearchJenisPengiriman = "";
            } else if (ComboBox_jenisPengiriman.getSelectedItem() == "Sampel Internal") {
                SearchJenisPengiriman = "AND `jenis_pengiriman` = 'Sampel Internal'";
            } else if (ComboBox_jenisPengiriman.getSelectedItem() == "Sampel Eksternal") {
                SearchJenisPengiriman = "AND `jenis_pengiriman` = 'Sampel Eksternal'";
            } else if (ComboBox_jenisPengiriman.getSelectedItem() == "Ekspor") {
                SearchJenisPengiriman = "AND `jenis_pengiriman` = 'Ekspor'";
            } else if (ComboBox_jenisPengiriman.getSelectedItem() == "Ekspor Esta") {
                SearchJenisPengiriman = "AND `jenis_pengiriman` = 'Ekspor Esta'";
            } else if (ComboBox_jenisPengiriman.getSelectedItem() == "Ekspor Sub") {
                SearchJenisPengiriman = "AND `jenis_pengiriman` = 'Ekspor Sub'";
            } else if (ComboBox_jenisPengiriman.getSelectedItem() == "Ekspor JT") {
                SearchJenisPengiriman = "AND `jenis_pengiriman` = 'Ekspor JT'";
            }
            if (ComboBox_jenisPengiriman.getSelectedItem() == "Lokal") {
                SearchJenisPengiriman = "AND `jenis_pengiriman` = 'Lokal'";
            }

            DefaultTableModel model = (DefaultTableModel) tabel_data_pengiriman.getModel();
            model.setRowCount(0);
            if (Date_Search1.getDate() != null && Date_Search2.getDate() != null) {
                sql = "SELECT `invoice_no`, `tanggal_invoice`, `tb_spk`.`tanggal_keluar`, `tb_spk`.`tanggal_awb`, `tb_pengiriman`.`kode_buyer`, `no_dokumen`, `jenis_pengiriman`, `status_pengiriman`, `tb_pengiriman`.`keterangan`, `nama`, COUNT(`tb_box_packing`.`no_box`) AS 'total_box', SUM(`keping`) AS 'total_keping', SUM(`berat`) AS 'total_berat', `tb_pengiriman`.`kode_spk` "
                        + "FROM `tb_pengiriman` "
                        + "LEFT JOIN `tb_spk` ON `tb_pengiriman`.`kode_spk` = `tb_spk`.`kode_spk`"
                        + "LEFT JOIN `tb_buyer` ON `tb_pengiriman`.`kode_buyer` = `tb_buyer`.`kode_buyer`"
                        + "LEFT JOIN `tb_box_packing` ON `tb_pengiriman`.`invoice_no` = `tb_box_packing`.`invoice_pengiriman`"
                        + "LEFT JOIN `tb_box_bahan_jadi` ON `tb_box_packing`.`no_box` = `tb_box_bahan_jadi`.`no_box`"
                        + "WHERE `" + SearchCat + "` LIKE '%" + txt_search_keywords.getText() + "%' "
                        + "AND (`tanggal_invoice` BETWEEN '" + dateFormat.format(Date_Search1.getDate()) + "' AND '" + dateFormat.format(Date_Search2.getDate()) + "')" + SearchJenisPengiriman
                        + "GROUP BY `tb_pengiriman`.`invoice_no`";
            } else {
                sql = "SELECT `invoice_no`, `tanggal_invoice`, `tb_spk`.`tanggal_keluar`, `tb_spk`.`tanggal_awb`, `tb_pengiriman`.`kode_buyer`, `no_dokumen`, `jenis_pengiriman`, `status_pengiriman`, `tb_pengiriman`.`keterangan`, `nama`, COUNT(`tb_box_packing`.`no_box`) AS 'total_box', SUM(`keping`) AS 'total_keping', SUM(`berat`) AS 'total_berat', `tb_pengiriman`.`kode_spk` "
                        + "FROM `tb_pengiriman` "
                        + "LEFT JOIN `tb_spk` ON `tb_pengiriman`.`kode_spk` = `tb_spk`.`kode_spk`"
                        + "LEFT JOIN `tb_buyer` ON `tb_pengiriman`.`kode_buyer` = `tb_buyer`.`kode_buyer`"
                        + "LEFT JOIN `tb_box_packing` ON `tb_pengiriman`.`invoice_no` = `tb_box_packing`.`invoice_pengiriman`"
                        + "LEFT JOIN `tb_box_bahan_jadi` ON `tb_box_packing`.`no_box` = `tb_box_bahan_jadi`.`no_box`"
                        + "WHERE `" + SearchCat + "` LIKE '%" + txt_search_keywords.getText() + "%' " + SearchJenisPengiriman
                        + "GROUP BY `tb_pengiriman`.`invoice_no`";
            }
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[15];
            while (rs.next()) {
                row[0] = rs.getString("invoice_no");
                row[1] = rs.getString("no_dokumen");
                row[2] = rs.getDate("tanggal_invoice");
                row[3] = rs.getDate("tanggal_keluar");
                row[4] = rs.getDate("tanggal_awb");
                row[5] = rs.getString("kode_buyer");
                row[6] = rs.getString("nama");
                row[7] = rs.getString("jenis_pengiriman");
                row[8] = rs.getString("status_pengiriman");
                row[9] = rs.getString("keterangan");
                row[10] = rs.getInt("total_box");
                row[11] = rs.getInt("total_keping");
                total_kpg = total_kpg + rs.getInt("total_keping");
                row[12] = rs.getDouble("total_berat");
                total_gram = total_gram + rs.getDouble("total_berat");
                row[13] = rs.getString("kode_spk");
                model.addRow(row);
            }
            label_total_data_pengiriman.setText(Integer.toString(tabel_data_pengiriman.getRowCount()));
            label_total_keping_pengiriman.setText(decimalFormat.format(total_kpg));
            label_total_gram_pengiriman.setText(decimalFormat.format(total_gram));
            ColumnsAutoSizer.sizeColumnsToFit(tabel_data_pengiriman);
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_pengiriman.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_Box() {
        try {
            float total_kpg = 0;
            float total_gram = 0;
            DefaultTableModel model = (DefaultTableModel) tabel_detail_box.getModel();
            model.setRowCount(0);
            sql2 = "SELECT `tb_pengiriman`.`invoice_no`, `tb_pengiriman`.`tanggal_pengiriman`, `tb_pengiriman`.`kode_buyer`, `tb_pengiriman`.`no_dokumen`, `tb_pengiriman`.`jenis_pengiriman`, `tb_pengiriman`.`status_pengiriman`, `tb_box_packing`.`no_box`, `tb_grade_bahan_jadi`.`kode_grade`,`keping`, `berat`, `tanggal_masuk`, `status`, `tb_box_packing`.`batch_number`, `invoice_pengiriman` \n"
                    + "FROM `tb_box_packing` LEFT JOIN `tb_box_bahan_jadi` ON `tb_box_packing`.`no_box` = `tb_box_bahan_jadi`.`no_box` "
                    + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_box_bahan_jadi`.`kode_grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode` "
                    + "LEFT JOIN `tb_pengiriman` ON `tb_pengiriman`.`invoice_no` = `tb_box_packing`.`invoice_pengiriman` "
                    + "WHERE `invoice_pengiriman` = '" + label_invoice.getText() + "' "
                    + "ORDER BY `tb_grade_bahan_jadi`.`kode`";
            rs = Utility.db.getStatement().executeQuery(sql2);
            Object[] row = new Object[6];
            while (rs.next()) {
                row[0] = rs.getString("no_box");
                row[1] = rs.getString("kode_grade");
                row[2] = rs.getDate("tanggal_masuk");
                row[3] = rs.getInt("keping");
                row[4] = rs.getFloat("berat");
                row[5] = rs.getString("batch_number");
                model.addRow(row);
                total_kpg = total_kpg + rs.getInt("keping");
                total_gram = total_gram + rs.getFloat("berat");
            }
            label_total_data_box.setText(Integer.toString(tabel_detail_box.getRowCount()));
            label_total_keping_box.setText(decimalFormat.format(total_kpg));
            label_total_gram_box.setText(decimalFormat.format(total_gram));
            ColumnsAutoSizer.sizeColumnsToFit(tabel_detail_box);
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_pengiriman.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_RekapBox() {
        try {
            DefaultTableModel model = (DefaultTableModel) tabel_rekap_box.getModel();
            model.setRowCount(0);
            String query = "SELECT `tb_box_packing`.`invoice_pengiriman`, `tb_pengiriman`.`tanggal_pengiriman`, `tb_pengiriman`.`kode_buyer`, `tb_buyer`.`nama`, `tb_grade_bahan_jadi`.`kode_grade`, SUM(`tb_box_bahan_jadi`.`keping`) AS 'total_keping', SUM(`tb_box_bahan_jadi`.`berat`) AS 'total_gram'\n"
                    + "FROM `tb_box_packing` LEFT JOIN `tb_box_bahan_jadi` ON `tb_box_packing`.`no_box` = `tb_box_bahan_jadi`.`no_box`\n"
                    + "LEFT JOIN `tb_pengiriman` ON `tb_box_packing`.`invoice_pengiriman` = `tb_pengiriman`.`invoice_no`\n"
                    + "LEFT JOIN `tb_buyer` ON `tb_buyer`.`kode_buyer` = `tb_pengiriman`.`kode_buyer`\n"
                    + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_box_bahan_jadi`.`kode_grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode`\n"
                    + "WHERE `invoice_pengiriman` = '" + label_invoice.getText() + "'\n"
                    + "GROUP BY `tb_grade_bahan_jadi`.`kode_grade`";
            rs = Utility.db.getStatement().executeQuery(query);
            Object[] row = new Object[7];
            while (rs.next()) {
                row[0] = rs.getString("kode_grade");
                row[1] = rs.getInt("total_keping");
                row[2] = rs.getFloat("total_gram");
                row[3] = rs.getString("invoice_pengiriman");
                row[4] = rs.getDate("tanggal_pengiriman");
                row[5] = rs.getString("kode_buyer");
                row[6] = rs.getString("nama");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(tabel_rekap_box);
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_pengiriman.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void kirim(String invoice) {
        try {
            Utility.db.getConnection().setAutoCommit(false);
            if (button_kirim.getText().equals("PROSES")) {
                sql = "UPDATE `tb_box_packing` SET `status`='PROSES' WHERE `invoice_pengiriman` = '" + invoice + "'";
                Utility.db.getConnection().createStatement();
                Utility.db.getStatement().executeUpdate(sql);

                sql = "UPDATE `tb_pengiriman` SET `status_pengiriman`='PROSES' WHERE `invoice_no`='" + invoice + "'";
                Utility.db.getConnection().createStatement();
                Utility.db.getStatement().executeUpdate(sql);
                Utility.db.getConnection().commit();
                JOptionPane.showMessageDialog(this, "Access Granted !, Status Pengiriman : PROSES");
            } else if (button_kirim.getText().equals("KIRIM")) {
                sql = "UPDATE `tb_box_packing` SET `status`='OUT' WHERE `invoice_pengiriman` = '" + invoice + "'";
                Utility.db.getConnection().createStatement();
                Utility.db.getStatement().executeUpdate(sql);

                sql = "UPDATE `tb_pengiriman` SET `status_pengiriman`='DELIVERED' WHERE `invoice_no`='" + invoice + "'";
                Utility.db.getConnection().createStatement();
                Utility.db.getStatement().executeUpdate(sql);
                Utility.db.getConnection().commit();
                JOptionPane.showMessageDialog(this, "Access Granted !, Status Pengiriman : DELIVERED\nSilahkan Update data Payment Report!");
            }
        } catch (SQLException ex) {
            try {
                Utility.db.getConnection().rollback();
            } catch (SQLException e) {
                Logger.getLogger(JDialog_pengiriman.class.getName()).log(Level.SEVERE, null, e);
            }
            Logger.getLogger(JPanel_pengiriman.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                Utility.db.getConnection().setAutoCommit(true);
            } catch (SQLException ex) {
                Logger.getLogger(JDialog_pengiriman.class.getName()).log(Level.SEVERE, null, ex);
            }
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
        ComboBox_Search = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        txt_search_keywords = new javax.swing.JTextField();
        button_search = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        Date_Search1 = new com.toedter.calendar.JDateChooser();
        Date_Search2 = new com.toedter.calendar.JDateChooser();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabel_data_pengiriman = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabel_detail_box = new javax.swing.JTable();
        jLabel7 = new javax.swing.JLabel();
        label_total_gram_pengiriman = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        label_total_data_pengiriman = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        label_total_keping_pengiriman = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        label_total_gram_box = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        label_total_data_box = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        label_total_keping_box = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        label_invoice = new javax.swing.JLabel();
        button_export_dataPengiriman = new javax.swing.JButton();
        button_export_dataBoxPengiriman = new javax.swing.JButton();
        button_print = new javax.swing.JButton();
        button_edit_pengiriman = new javax.swing.JButton();
        button_kirim = new javax.swing.JButton();
        button_pengiriman_baru = new javax.swing.JButton();
        button_delete_pengiriman = new javax.swing.JButton();
        button_detail = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        tabel_rekap_box = new javax.swing.JTable();
        jLabel6 = new javax.swing.JLabel();
        ComboBox_jenisPengiriman = new javax.swing.JComboBox<>();
        jLabel13 = new javax.swing.JLabel();
        label_invoice1 = new javax.swing.JLabel();
        button_export_dataBoxPengiriman1 = new javax.swing.JButton();
        button_pengeluaran_sampel_internal = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Data Pengiriman", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N
        jPanel1.setPreferredSize(new java.awt.Dimension(1366, 700));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("Search By :");

        ComboBox_Search.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_Search.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "NO INVOICE", "NAMA BUYER", "NO DOKUMEN" }));

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("Keywords :");

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

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("Tanggal Invoice :");

        Date_Search1.setBackground(new java.awt.Color(255, 255, 255));
        Date_Search1.setDateFormatString("dd MMMM yyyy");
        Date_Search1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Date_Search2.setBackground(new java.awt.Color(255, 255, 255));
        Date_Search2.setDateFormatString("dd MMMM yyyy");
        Date_Search2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        tabel_data_pengiriman.setAutoCreateRowSorter(true);
        tabel_data_pengiriman.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tabel_data_pengiriman.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No Invoice", "No Dokumen", "Tanggal Invoice", "Tanggal Pengiriman", "Tanggal AWB", "Kode Buyer", "Nama Buyer", "Jenis Pengiriman", "Status", "Keterangan", "Total Box", "Total Kpg", "Total Berat", "Kode SPK"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Float.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabel_data_pengiriman.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tabel_data_pengiriman);

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel4.setText("Tabel Data Pengiriman Waleta");

        tabel_detail_box.setAutoCreateRowSorter(true);
        tabel_detail_box.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tabel_detail_box.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No Box", "Grade", "Tanggal Masuk", "Keping", "Gram", "Batch No"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Float.class, java.lang.String.class
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
        tabel_detail_box.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(tabel_detail_box);

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel7.setText("Total Gram :");

        label_total_gram_pengiriman.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_pengiriman.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_gram_pengiriman.setText("0");

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel8.setText("Total Data :");

        label_total_data_pengiriman.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_pengiriman.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_data_pengiriman.setText("0");

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel9.setText("Total Keping :");

        label_total_keping_pengiriman.setBackground(new java.awt.Color(255, 255, 255));
        label_total_keping_pengiriman.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_keping_pengiriman.setText("0");

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel10.setText("Total Gram :");

        label_total_gram_box.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_box.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_gram_box.setText("0");

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel11.setText("Total Data :");

        label_total_data_box.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_box.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_data_box.setText("0");

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel12.setText("Total Keping :");

        label_total_keping_box.setBackground(new java.awt.Color(255, 255, 255));
        label_total_keping_box.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_keping_box.setText("0");

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel5.setText("Summary");

        label_invoice.setBackground(new java.awt.Color(255, 255, 255));
        label_invoice.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_invoice.setText("no. invoice");

        button_export_dataPengiriman.setBackground(new java.awt.Color(255, 255, 255));
        button_export_dataPengiriman.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_dataPengiriman.setText("Export");
        button_export_dataPengiriman.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_dataPengirimanActionPerformed(evt);
            }
        });

        button_export_dataBoxPengiriman.setBackground(new java.awt.Color(255, 255, 255));
        button_export_dataBoxPengiriman.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_dataBoxPengiriman.setText("Export");
        button_export_dataBoxPengiriman.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_dataBoxPengirimanActionPerformed(evt);
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

        button_edit_pengiriman.setBackground(new java.awt.Color(255, 255, 255));
        button_edit_pengiriman.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_edit_pengiriman.setText("Edit");
        button_edit_pengiriman.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_edit_pengirimanActionPerformed(evt);
            }
        });

        button_kirim.setBackground(new java.awt.Color(255, 255, 255));
        button_kirim.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_kirim.setText("KIRIM");
        button_kirim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_kirimActionPerformed(evt);
            }
        });

        button_pengiriman_baru.setBackground(new java.awt.Color(255, 255, 255));
        button_pengiriman_baru.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_pengiriman_baru.setText("Pengiriman Baru");
        button_pengiriman_baru.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_pengiriman_baruActionPerformed(evt);
            }
        });

        button_delete_pengiriman.setBackground(new java.awt.Color(255, 255, 255));
        button_delete_pengiriman.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_delete_pengiriman.setText("Delete");
        button_delete_pengiriman.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_delete_pengirimanActionPerformed(evt);
            }
        });

        button_detail.setBackground(new java.awt.Color(255, 255, 255));
        button_detail.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_detail.setText("Detail");
        button_detail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_detailActionPerformed(evt);
            }
        });

        tabel_rekap_box.setAutoCreateRowSorter(true);
        tabel_rekap_box.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tabel_rekap_box.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Grade", "Total Keping", "Total Gram", "No Invoice", "Tgl Kirim", "Buyer", "Nama"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.Float.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabel_rekap_box.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(tabel_rekap_box);

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel6.setText("Jenis Pengiriman :");

        ComboBox_jenisPengiriman.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_jenisPengiriman.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Sampel Internal", "Sampel Eksternal", "Ekspor", "Ekspor Esta", "Ekspor Sub", "Ekspor JT", "Lokal" }));

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel13.setText("Data Box Pengiriman");

        label_invoice1.setBackground(new java.awt.Color(255, 255, 255));
        label_invoice1.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_invoice1.setText("no. invoice");

        button_export_dataBoxPengiriman1.setBackground(new java.awt.Color(255, 255, 255));
        button_export_dataBoxPengiriman1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_dataBoxPengiriman1.setText("Export");
        button_export_dataBoxPengiriman1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_dataBoxPengiriman1ActionPerformed(evt);
            }
        });

        button_pengeluaran_sampel_internal.setBackground(new java.awt.Color(255, 255, 255));
        button_pengeluaran_sampel_internal.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_pengeluaran_sampel_internal.setText("Pengeluaran Sampel Internal");
        button_pengeluaran_sampel_internal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_pengeluaran_sampel_internalActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_data_pengiriman)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_keping_pengiriman)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gram_pengiriman)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_invoice)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(button_detail)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_export_dataBoxPengiriman))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_Search, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_keywords, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_jenisPengiriman, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_Search1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_Search2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_search)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(button_export_dataPengiriman)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_pengiriman_baru)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_pengeluaran_sampel_internal)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_delete_pengiriman)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_edit_pengiriman)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_kirim)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_print))
                            .addComponent(jScrollPane1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_data_box)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_keping_box)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_gram_box))
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_invoice1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(button_export_dataBoxPengiriman1)))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(button_search, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_Search2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_Search1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_keywords, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_Search, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_jenisPengiriman, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_invoice, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export_dataPengiriman, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export_dataBoxPengiriman, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_print, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_edit_pengiriman, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_kirim, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_pengiriman_baru, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_delete_pengiriman, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_detail, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_pengeluaran_sampel_internal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 581, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 361, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11)
                            .addComponent(label_total_data_box)
                            .addComponent(jLabel12)
                            .addComponent(label_total_keping_box)
                            .addComponent(jLabel10)
                            .addComponent(label_total_gram_box))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_invoice1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_export_dataBoxPengiriman1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(label_total_data_pengiriman)
                    .addComponent(jLabel9)
                    .addComponent(label_total_keping_pengiriman)
                    .addComponent(jLabel7)
                    .addComponent(label_total_gram_pengiriman))
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

    private void txt_search_keywordsKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_keywordsKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_pengiriman();
        }
    }//GEN-LAST:event_txt_search_keywordsKeyPressed

    private void button_searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_searchActionPerformed
        // TODO add your handling code here:
        refreshTable_pengiriman();
    }//GEN-LAST:event_button_searchActionPerformed

    private void button_export_dataPengirimanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_dataPengirimanActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) tabel_data_pengiriman.getModel();
        ExportToExcel.writeToExcel(model, jPanel1);
    }//GEN-LAST:event_button_export_dataPengirimanActionPerformed

    private void button_export_dataBoxPengirimanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_dataBoxPengirimanActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) tabel_detail_box.getModel();
        ExportToExcel.writeToExcel(model, jPanel1);
    }//GEN-LAST:event_button_export_dataBoxPengirimanActionPerformed

    private void button_printActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_printActionPerformed
        try {
            int j = tabel_data_pengiriman.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih nomor invoice yang akan dibuatkan laporan pengirimannya", "warning!", 1);
            } else {
                JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Laporan_Pengiriman.jrxml");
                JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("no_invoice", tabel_data_pengiriman.getValueAt(j, 0).toString());
                JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, params, Utility.db.getConnection());
                JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
            }
        } catch (JRException ex) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_printActionPerformed

    private void button_edit_pengirimanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_edit_pengirimanActionPerformed
        // TODO add your handling code here:
        int i = tabel_data_pengiriman.getSelectedRow();
        if (i == -1) {
            JOptionPane.showMessageDialog(this, "Silahkan pilih data yang akan di edit !");
        } else {
            String invoice = tabel_data_pengiriman.getValueAt(i, 0).toString();
            if (tabel_data_pengiriman.getValueAt(i, 6).toString().toLowerCase().equals("sample internal")) {
                JDialog_pengiriman_SI dialog = new JDialog_pengiriman_SI(new javax.swing.JFrame(), true, "edit", invoice);
                dialog.pack();
                dialog.setLocationRelativeTo(this);
                dialog.setVisible(true);
                dialog.setEnabled(true);
            } else {
                JDialog_pengiriman dialog = new JDialog_pengiriman(new javax.swing.JFrame(), true, "edit", invoice);
                dialog.pack();
                dialog.setLocationRelativeTo(this);
                dialog.setVisible(true);
                dialog.setEnabled(true);
            }
            refreshTable_pengiriman();
        }
    }//GEN-LAST:event_button_edit_pengirimanActionPerformed

    private void button_kirimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_kirimActionPerformed
        // TODO add your handling code here:
        int i = tabel_data_pengiriman.getSelectedRow();
        if (i == -1) {
            JOptionPane.showMessageDialog(this, "Silahkan pilih data yang akan di kirim !");
        } else {
            try {
                String invoice = tabel_data_pengiriman.getValueAt(i, 0).toString();
                String jenis_pengiriman = tabel_data_pengiriman.getValueAt(i, 7).toString();
                String query = "SELECT `nama_bagian` FROM `tb_bagian` WHERE `kode_bagian` = '" + MainForm.Login_kodeBagian + "'";
                rs = Utility.db.getStatement().executeQuery(query);
                if (rs.next()) {
                    if ((rs.getString("nama_bagian").equalsIgnoreCase("PENGAWAS-EKSPOR-PACKING--PACKING") || rs.getString("nama_bagian").equalsIgnoreCase("KADEP-EKSPOR---OFFICE"))
                            && (jenis_pengiriman.equalsIgnoreCase("sampel internal") || jenis_pengiriman.equalsIgnoreCase("sampel eksternal"))) {
                        kirim(invoice);
                        refreshTable_pengiriman();
                    } else if (rs.getString("nama_bagian").equalsIgnoreCase("KADEP-EKSPOR---OFFICE")) {
                        kirim(invoice);
                        refreshTable_pengiriman();
                    } else {
                        JOptionPane.showMessageDialog(this, "Silahkan menghubungi KEPALA DEPARTEMEN EKSPOR untuk melakukan pengiriman,\nPENGAWAS DIVISI PACKING untuk pengiriman Sample", "Access Denied !", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Silahkan menghubungi KEPALA DEPARTEMEN EKSPOR untuk melakukan pengiriman,\nPENGAWAS DIVISI PACKING untuk pengiriman Sample", "Access Denied !", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, ex);
                Logger.getLogger(JPanel_pengiriman.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_button_kirimActionPerformed

    private void button_pengiriman_baruActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_pengiriman_baruActionPerformed
        // TODO add your handling code here:
        JDialog_pengiriman dialog = new JDialog_pengiriman(new javax.swing.JFrame(), true, "new", null);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);
        refreshTable_pengiriman();
    }//GEN-LAST:event_button_pengiriman_baruActionPerformed

    private void button_delete_pengirimanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_delete_pengirimanActionPerformed
        // TODO add your handling code here:
        try {
            Utility.db.getConnection().setAutoCommit(false);
            int j = tabel_data_pengiriman.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih data yang mau di hapus !");
            } else {
                int dialogResult = JOptionPane.showConfirmDialog(this, "Yakin hapus data ini?", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    // delete code here
                    String Query = "DELETE FROM `tb_pengiriman` WHERE `invoice_no` = '" + tabel_data_pengiriman.getValueAt(j, 0).toString() + "'";
                    Utility.db.getConnection().createStatement();
                    if ((Utility.db.getStatement().executeUpdate(Query)) > 0) {
                        for (int i = 0; i < tabel_detail_box.getRowCount(); i++) {
                            sql = "UPDATE `tb_box_packing` SET `status`='STOK',`invoice_pengiriman`=NULL WHERE `no_box` = '" + tabel_detail_box.getValueAt(i, 0) + "'";
//                            System.out.println(sql);
                            Utility.db.getConnection().createStatement();
                            Utility.db.getStatement().executeUpdate(sql);
                        }
                        Utility.db.getConnection().commit();
                        JOptionPane.showMessageDialog(this, "Data terhapus !");
                        refreshTable_pengiriman();
                    }
                }
            }
        } catch (HeadlessException | SQLException ex) {
            try {
                Utility.db.getConnection().rollback();
            } catch (SQLException e) {
                Logger.getLogger(JDialog_pengiriman.class.getName()).log(Level.SEVERE, null, e);
            }
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JDialog_pengiriman.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                Utility.db.getConnection().setAutoCommit(true);
            } catch (SQLException ex) {
                Logger.getLogger(JDialog_pengiriman.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_button_delete_pengirimanActionPerformed

    private void button_detailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_detailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_button_detailActionPerformed

    private void button_export_dataBoxPengiriman1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_dataBoxPengiriman1ActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) tabel_rekap_box.getModel();
        ExportToExcel.writeToExcel(model, jPanel1);
    }//GEN-LAST:event_button_export_dataBoxPengiriman1ActionPerformed

    private void button_pengeluaran_sampel_internalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_pengeluaran_sampel_internalActionPerformed
        // TODO add your handling code here:
        JDialog_pengiriman_SI dialog = new JDialog_pengiriman_SI(new javax.swing.JFrame(), true, "new", null);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);
        refreshTable_pengiriman();
    }//GEN-LAST:event_button_pengeluaran_sampel_internalActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_Search;
    private javax.swing.JComboBox<String> ComboBox_jenisPengiriman;
    private com.toedter.calendar.JDateChooser Date_Search1;
    private com.toedter.calendar.JDateChooser Date_Search2;
    private javax.swing.JButton button_delete_pengiriman;
    private javax.swing.JButton button_detail;
    private javax.swing.JButton button_edit_pengiriman;
    private javax.swing.JButton button_export_dataBoxPengiriman;
    private javax.swing.JButton button_export_dataBoxPengiriman1;
    private javax.swing.JButton button_export_dataPengiriman;
    private javax.swing.JButton button_kirim;
    private javax.swing.JButton button_pengeluaran_sampel_internal;
    private javax.swing.JButton button_pengiriman_baru;
    private javax.swing.JButton button_print;
    private javax.swing.JButton button_search;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel label_invoice;
    private javax.swing.JLabel label_invoice1;
    private javax.swing.JLabel label_total_data_box;
    private javax.swing.JLabel label_total_data_pengiriman;
    private javax.swing.JLabel label_total_gram_box;
    private javax.swing.JLabel label_total_gram_pengiriman;
    private javax.swing.JLabel label_total_keping_box;
    private javax.swing.JLabel label_total_keping_pengiriman;
    public static javax.swing.JTable tabel_data_pengiriman;
    private javax.swing.JTable tabel_detail_box;
    private javax.swing.JTable tabel_rekap_box;
    private javax.swing.JTextField txt_search_keywords;
    // End of variables declaration//GEN-END:variables
}
