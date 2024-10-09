package waleta_system.Finance;

import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.ExportToExcel;
import waleta_system.Class.Utility;

public class JPanel_Payment_Report extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    PreparedStatement pst;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Date today = new Date();
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    DefaultTableModel dm = new DefaultTableModel();
    JFileChooser chooser = new JFileChooser();
    DecimalFormat decimalFormat = new DecimalFormat();
    double total_berat_waleta = 0, total_berat_esta = 0, total_berat_jastip = 0;
    double waleta_cny = 0, waleta_idr = 0, waleta_usd = 0, esta_cny = 0, jastip_cny = 0, jastip_margin_cny = 0;
    double total_ar_outstanding = 0, total_ap_outstanding = 0;

    public JPanel_Payment_Report() {
        initComponents();
    }

    public void init() {
        try {
            ComboBox_KategoriBuyer.removeAllItems();
            ComboBox_KategoriBuyer.addItem("All");
            sql = "SELECT DISTINCT(`tb_buyer`.`kategori`) AS 'kategori'"
                    + " FROM `tb_payment_report` LEFT JOIN `tb_buyer` ON `tb_payment_report`.`buyer` = `tb_buyer`.`kode_buyer`";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_KategoriBuyer.addItem(rs.getString("kategori"));
            }

            tabel_payment.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent event) {
                    if (!event.getValueIsAdjusting() && tabel_payment.getSelectedRow() != -1) {
                        int x = tabel_payment.getSelectedRow();
                        if (x > -1) {

                        }
                    }
                }
            });

            refreshTable();
            tabel_payment.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 14));
            table_summary.getTableHeader().setFont(new Font("Arial Narrow", Font.PLAIN, 14));
            refreshTable_hutangExim();
        } catch (Exception ex) {
            Logger.getLogger(JPanel_Payment_Report.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Double zerotonull(double value) {
        return value == 0 ? null : value;
    }

    private void setRowValues(ResultSet rs, Object[] row) throws SQLException {
        row[3] = rs.getString("invoice");
        row[4] = rs.getString("nama");
        row[5] = rs.getDate("tgl_invoice");
        row[6] = rs.getDate("awb_date");
        row[7] = rs.getString("no_peb");
        row[8] = rs.getString("term_payment");
        row[9] = rs.getDouble("berat_waleta") + rs.getDouble("berat_esta") + rs.getDouble("berat_jastip");
        row[10] = zerotonull(rs.getDouble("berat_waleta"));
        row[11] = zerotonull(rs.getDouble("berat_esta"));
        row[12] = zerotonull(rs.getDouble("berat_jastip"));
        row[13] = rs.getString("currency");
        double total_value = rs.getDouble("value_waleta") + rs.getDouble("value_esta") + rs.getDouble("value_from_jtp") + rs.getDouble("value_to_jtp");
        row[14] = total_value;
        row[15] = zerotonull(rs.getDouble("value_waleta"));
        row[16] = zerotonull(rs.getDouble("value_esta"));
        row[17] = zerotonull(rs.getDouble("value_to_jtp"));
        row[18] = zerotonull(rs.getDouble("value_from_jtp"));
        row[19] = rs.getDouble("payment1") + rs.getDouble("payment2");
        double ar_outstanding = total_value - (rs.getDouble("payment1") + rs.getDouble("payment2"));
        row[20] = ar_outstanding;
        if (rs.getDouble("value_to_jtp") > 0) {
            row[21] = rs.getDouble("transfer_jastip1") + rs.getDouble("transfer_jastip2");
            double ap_outstanding = rs.getDouble("value_to_jtp") - (rs.getDouble("transfer_jastip1") + rs.getDouble("transfer_jastip2"));
            total_ap_outstanding = total_ap_outstanding + ap_outstanding;
            row[22] = ap_outstanding;
        } else {
            row[21] = null;
            row[22] = null;
        }

        total_berat_waleta = total_berat_waleta + rs.getDouble("berat_waleta");
        total_berat_esta = total_berat_esta + rs.getDouble("berat_esta");
        total_berat_jastip = total_berat_jastip + rs.getDouble("berat_jastip");

        if (rs.getString("currency") != null) {
            switch (rs.getString("currency")) {
                case "CNY":
                    total_ar_outstanding = total_ar_outstanding + ar_outstanding;
                    waleta_cny = waleta_cny + rs.getDouble("value_waleta");
                    break;
                case "USD":
                    waleta_usd = waleta_usd + rs.getDouble("value_waleta");
                    break;
                case "IDR":
                    waleta_idr = waleta_idr + rs.getDouble("value_waleta");
                    break;
                default:
                    break;
            }
        }
        esta_cny = esta_cny + rs.getDouble("value_esta");
        jastip_cny = jastip_cny + rs.getDouble("value_to_jtp");
        jastip_margin_cny = jastip_margin_cny + rs.getDouble("value_from_jtp");
    }

    public void refreshTable() {
        try {
            DefaultTableModel model = (DefaultTableModel) tabel_payment.getModel();
            model.setRowCount(0);

            total_berat_waleta = 0;
            total_berat_esta = 0;
            total_berat_jastip = 0;
            waleta_cny = 0;
            waleta_idr = 0;
            waleta_usd = 0;
            esta_cny = 0;
            jastip_cny = 0;
            jastip_margin_cny = 0;
            total_ar_outstanding = 0;
            total_ap_outstanding = 0;

            int waleta = 0, esta = 0, jastip = 0, kode = 0;
            if (CheckBox_waleta.isSelected()) {
                waleta = 1;
            }
            if (CheckBox_esta.isSelected()) {
                esta = 2;
            }
            if (CheckBox_jastip.isSelected()) {
                jastip = 4;
            }
            kode = waleta + esta + jastip;

            String kategori_pengiriman = "AND `tb_buyer`.`kategori` = '" + ComboBox_KategoriBuyer.getSelectedItem().toString() + "' ";
            String tanggal = "";

            if ("All".equals(ComboBox_KategoriBuyer.getSelectedItem().toString())) {
                kategori_pengiriman = "";
            }
            if (Date1.getDate() != null && Date2.getDate() != null) {
                tanggal = "AND `tgl_invoice` BETWEEN '" + dateFormat.format(Date1.getDate()) + "' AND '" + dateFormat.format(Date2.getDate()) + "' ";
            }
            sql = "SELECT `invoice`, `tb_buyer`.`nama`, `tb_buyer`.`kategori`, `tgl_invoice`, `awb_date`, `no_peb`,"
                    + " `term_payment`, `berat_waleta`, `berat_esta`, `berat_jastip`, "
                    + "`currency`, `value_waleta`, `value_esta`, `value_from_jtp`, `value_to_jtp`, `date_payment2`, `payment2`, `date_payment1`, `payment1`, `beneficiary_name_jastip`, `date_transfer_jastip1`, `transfer_jastip1`, `date_transfer_jastip2`, `transfer_jastip2` "
                    + "FROM `tb_payment_report` "
                    + "LEFT JOIN `tb_buyer` ON `tb_payment_report`.`buyer` = `tb_buyer`.`kode_buyer`\n"
                    + "WHERE `invoice` LIKE '%" + txt_search_no_invoice.getText() + "%' "
                    + "AND (`tb_buyer`.`nama` LIKE '%" + txt_search_buyer.getText() + "%' OR `tb_payment_report`.`buyer` LIKE '%" + txt_search_buyer.getText() + "%') "
                    + kategori_pengiriman
                    + tanggal
                    + "ORDER BY `tgl_invoice` DESC";
//            System.out.println(sql);
            rs = Utility.db.getStatement().executeQuery(sql);
            int nomor = 1;
            while (rs.next()) {
                Object[] row = new Object[23];
                row[0] = nomor;
                row[1] = rs.getString("kategori");
                String pemilik_barang = "";
                if (rs.getFloat("berat_waleta") > 0) {
                    pemilik_barang = pemilik_barang + "Waleta";
                }
                if (rs.getFloat("berat_esta") > 0) {
                    if (!pemilik_barang.equals("")) {
                        pemilik_barang = pemilik_barang + "-";
                    }
                    pemilik_barang = pemilik_barang + "Esta";
                }
                if (rs.getFloat("berat_jastip") > 0) {
                    if (!pemilik_barang.equals("")) {
                        pemilik_barang = pemilik_barang + "-";
                    }
                    pemilik_barang = pemilik_barang + "Jastip";
                }
                row[2] = pemilik_barang;

                if (kode == 1 && pemilik_barang.equals("Waleta")) {
                    setRowValues(rs, row);
                    model.addRow(row);
                    nomor++;
                } else if (kode == 2 && pemilik_barang.equals("Esta")) {
                    setRowValues(rs, row);
                    model.addRow(row);
                    nomor++;
                } else if (kode == 3 && pemilik_barang.equals("Waleta-Esta")) {
                    setRowValues(rs, row);
                    model.addRow(row);
                    nomor++;
                } else if (kode == 4 && pemilik_barang.equals("Jastip")) {
                    setRowValues(rs, row);
                    model.addRow(row);
                    nomor++;
                } else if (kode == 5 && pemilik_barang.equals("Waleta-Jastip")) {
                    setRowValues(rs, row);
                    model.addRow(row);
                    nomor++;
                } else if (kode == 6 && pemilik_barang.equals("Esta-Jastip")) {
                    setRowValues(rs, row);
                    model.addRow(row);
                    nomor++;
                } else if (kode == 7 && pemilik_barang.equals("Waleta-Esta-Jastip")) {
                    setRowValues(rs, row);
                    model.addRow(row);
                    nomor++;
                } else if (kode == 0) {
                    setRowValues(rs, row);
                    model.addRow(row);
                    nomor++;
                }
            }
            ColumnsAutoSizer.sizeColumnsToFit(tabel_payment);

            table_summary.setValueAt((total_berat_waleta + total_berat_esta + total_berat_jastip), 0, 0);
            table_summary.setValueAt(total_berat_waleta, 0, 1);
            table_summary.setValueAt(total_berat_esta, 0, 2);
            table_summary.setValueAt(total_berat_jastip, 0, 3);
            table_summary.setValueAt(waleta_idr, 0, 4);
            table_summary.setValueAt(waleta_usd, 0, 5);
            table_summary.setValueAt((waleta_cny + esta_cny + jastip_cny + jastip_margin_cny), 0, 6);
            table_summary.setValueAt(waleta_cny, 0, 7);
            table_summary.setValueAt(esta_cny, 0, 8);
            table_summary.setValueAt(jastip_cny, 0, 9);
            table_summary.setValueAt(jastip_margin_cny, 0, 10);
            table_summary.setValueAt(total_ar_outstanding, 0, 11);
            table_summary.setValueAt(total_ap_outstanding, 0, 12);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_Payment_Report.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_hutangExim() {
        try {
            double total_hutang = 0, total_belum_terbayar = 0;
            DefaultTableModel model = (DefaultTableModel) tabel_hutangExim.getModel();
            model.setRowCount(0);

            String kategori_buyer = "AND `tb_buyer`.`kategori` = '" + ComboBox_KategoriBuyer_hutangExim.getSelectedItem().toString() + "' ";
            String tanggal = "";

            if ("All".equals(ComboBox_KategoriBuyer_hutangExim.getSelectedItem().toString())) {
                kategori_buyer = "";
            }
            if (Date_hutangExim1.getDate() != null && Date_hutangExim2.getDate() != null) {
                tanggal = "AND `tgl_invoice` BETWEEN '" + dateFormat.format(Date_hutangExim1.getDate()) + "' AND '" + dateFormat.format(Date_hutangExim2.getDate()) + "' ";
            }
            sql = "SELECT `tb_hutang_exim`.`invoice`, `tanggal_hutang`, `jumlah_hutang`, `tb_hutang_exim`.`currency` AS 'currency_hutang', `tanggal_pelunasan`, "
                    + "`tb_buyer`.`nama`, `tb_buyer`.`kategori`, `tgl_invoice`, `awb_date`, `no_peb`,"
                    + " `term_payment`, `berat_waleta`, `berat_esta`, `berat_jastip`, "
                    + "`tb_payment_report`.`currency` AS 'currency_invoice', `value_waleta`, `value_esta`, `value_from_jtp`, `value_to_jtp`, `date_payment2`, `payment2`, `date_payment1`, `payment1`, `beneficiary_name_jastip`, `date_transfer_jastip1`, `transfer_jastip1`, `date_transfer_jastip2`, `transfer_jastip2` "
                    + "FROM `tb_hutang_exim` "
                    + "LEFT JOIN `tb_payment_report` ON `tb_hutang_exim`.`invoice` = `tb_payment_report`.`invoice`\n"
                    + "LEFT JOIN `tb_buyer` ON `tb_payment_report`.`buyer` = `tb_buyer`.`kode_buyer`\n"
                    + "WHERE `tb_hutang_exim`.`invoice` LIKE '%" + txt_search_no_invoice_hutangExim.getText() + "%' "
                    + "AND (`tb_buyer`.`nama` LIKE '%" + txt_search_buyer_hutangExim.getText() + "%' OR `tb_payment_report`.`buyer` LIKE '%" + txt_search_buyer_hutangExim.getText() + "%') "
                    + kategori_buyer
                    + tanggal
                    + "ORDER BY `tanggal_hutang` DESC";
//            System.out.println(sql);
            rs = Utility.db.getStatement().executeQuery(sql);
            int nomor = 0;
            while (rs.next()) {
                Object[] row = new Object[15];
                nomor++;
                row[0] = nomor;
                row[1] = rs.getString("invoice");
                row[2] = rs.getString("nama");
                row[3] = rs.getDate("tgl_invoice");
                row[4] = rs.getDouble("berat_waleta") + rs.getDouble("berat_esta") + rs.getDouble("berat_jastip");
                row[5] = rs.getString("currency_invoice");
                row[6] = rs.getDouble("value_waleta") + rs.getDouble("value_esta") + rs.getDouble("value_from_jtp") + rs.getDouble("value_to_jtp");
                row[7] = rs.getDate("tanggal_hutang");
                row[8] = rs.getDouble("jumlah_hutang");
                row[9] = rs.getDate("tanggal_pelunasan");

                total_hutang = total_hutang + rs.getDouble("jumlah_hutang");
                if (rs.getDate("tanggal_pelunasan") == null) {
                    total_belum_terbayar = total_belum_terbayar + rs.getDouble("jumlah_hutang");
                }

                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(tabel_hutangExim);
            label_total_hutang.setText(decimalFormat.format(total_hutang));
            label_total_hutang_belum_terbayar.setText(decimalFormat.format(total_belum_terbayar));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_Payment_Report.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void ImportFromCSV() throws ParseException {
        try {
            int n = 0;
            JOptionPane.showMessageDialog(this,
                    "Pastikan \n"
                    + "- tanggal dalam format 'yyyy-MM-dd'\n"
                    + "- 6 Kategori Biaya (Bahan Baku, Tenaga Kerja Langsung, Overhead, Umum, Payroll Staff, Ekspor)",
                    "Warning Message !", JOptionPane.INFORMATION_MESSAGE);
            chooser.setDialogTitle("Select CSV file to import!");
            int result = chooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                Utility.db.getConnection();
                File file = chooser.getSelectedFile();
                String filename1 = file.getAbsolutePath();
                try (BufferedReader br = new BufferedReader(new FileReader(filename1))) {
                    String line;
                    String Query = null;
                    try {
                        Utility.db.getConnection().setAutoCommit(false);
                        while ((line = br.readLine()) != null) {
                            String[] value = line.split(";");
                            Query = "INSERT INTO `tb_biaya_pabrik`(`bulan`, `Kategori1`, `jenis_pengeluaran`, `nilai`) "
                                    + "VALUES ('" + value[0] + "','" + value[1] + "','" + value[2] + "','" + value[3] + "')";
                            Utility.db.getConnection().prepareStatement(Query);
                            if ((Utility.db.getStatement().executeUpdate(Query)) > 0) {
//                                System.out.println(value[0] + "','" + value[1] + "','" + value[2] + "','" + value[3]);
                                n++;
                            } else {
                                JOptionPane.showMessageDialog(this, "Failed insert : " + value[3]);
                            }
                        }
                        Utility.db.getConnection().commit();
                    } catch (SQLException ex) {
                        try {
                            Utility.db.getConnection().rollback();
                        } catch (SQLException x) {
                            Logger.getLogger(JPanel_Payment_Report.class.getName()).log(Level.SEVERE, null, "BBB : " + x);
                        }
                        JOptionPane.showMessageDialog(this, ex.getMessage());
                        Logger.getLogger(JPanel_Payment_Report.class.getName()).log(Level.SEVERE, null, "AAA : " + ex);
                    } finally {
                        JOptionPane.showMessageDialog(this, "Data Berhasil Masuk : " + n);
                    }
                }
            }
        } catch (HeadlessException | IOException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
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

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabel_payment = new javax.swing.JTable();
        jPanel_search_karyawan = new javax.swing.JPanel();
        txt_search_no_invoice = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        Date1 = new com.toedter.calendar.JDateChooser();
        jLabel34 = new javax.swing.JLabel();
        Date2 = new com.toedter.calendar.JDateChooser();
        button_refresh = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        ComboBox_KategoriBuyer = new javax.swing.JComboBox<>();
        button_export = new javax.swing.JButton();
        CheckBox_waleta = new javax.swing.JCheckBox();
        CheckBox_esta = new javax.swing.JCheckBox();
        CheckBox_jastip = new javax.swing.JCheckBox();
        jLabel8 = new javax.swing.JLabel();
        txt_search_buyer = new javax.swing.JTextField();
        button_edit = new javax.swing.JButton();
        button_delete = new javax.swing.JButton();
        button_view_payment = new javax.swing.JButton();
        button_view_transfer = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        table_summary = new javax.swing.JTable();
        jLabel10 = new javax.swing.JLabel();
        button_input_invoice = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        txt_search_no_invoice_hutangExim = new javax.swing.JTextField();
        button_export_hutangExim = new javax.swing.JButton();
        ComboBox_KategoriBuyer_hutangExim = new javax.swing.JComboBox<>();
        jLabel14 = new javax.swing.JLabel();
        button_refresh_hutangExim = new javax.swing.JButton();
        Date_hutangExim2 = new com.toedter.calendar.JDateChooser();
        jLabel35 = new javax.swing.JLabel();
        Date_hutangExim1 = new com.toedter.calendar.JDateChooser();
        txt_search_buyer_hutangExim = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tabel_hutangExim = new javax.swing.JTable();
        button_input_hutangExim = new javax.swing.JButton();
        button_edit_hutangExim = new javax.swing.JButton();
        button_delete_hutangExim = new javax.swing.JButton();
        jLabel17 = new javax.swing.JLabel();
        label_total_hutang = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        label_total_hutang_belum_terbayar = new javax.swing.JLabel();

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        tabel_payment.setAutoCreateRowSorter(true);
        tabel_payment.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        tabel_payment.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "Jenis", "Pemilik Barang", "No invoice", "Buyer Name", "Invoice Date", "AWB Date", "PEB no", "Term of Payment", "Net Weight", "Waleta", "Esta", "JTP", "Currency", "Value", "Waleta", "Esta", "JTP", "Margin JTP", "Total Payment", "AR Outstanding", "Transfered to JTP", "AP Outstanding"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.String.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabel_payment.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tabel_payment);
        if (tabel_payment.getColumnModel().getColumnCount() > 0) {
            tabel_payment.getColumnModel().getColumn(1).setHeaderValue("Jenis");
            tabel_payment.getColumnModel().getColumn(2).setHeaderValue("Pemilik Barang");
            tabel_payment.getColumnModel().getColumn(6).setHeaderValue("AWB Date");
            tabel_payment.getColumnModel().getColumn(7).setHeaderValue("PEB no");
            tabel_payment.getColumnModel().getColumn(8).setHeaderValue("Term of Payment");
            tabel_payment.getColumnModel().getColumn(10).setHeaderValue("Waleta");
            tabel_payment.getColumnModel().getColumn(11).setHeaderValue("Esta");
            tabel_payment.getColumnModel().getColumn(12).setHeaderValue("JTP");
            tabel_payment.getColumnModel().getColumn(15).setHeaderValue("Waleta");
            tabel_payment.getColumnModel().getColumn(16).setHeaderValue("Esta");
            tabel_payment.getColumnModel().getColumn(17).setHeaderValue("JTP");
            tabel_payment.getColumnModel().getColumn(18).setHeaderValue("Margin JTP");
            tabel_payment.getColumnModel().getColumn(21).setHeaderValue("Transfered to JTP");
            tabel_payment.getColumnModel().getColumn(22).setHeaderValue("AP Outstanding");
        }

        jPanel_search_karyawan.setBackground(new java.awt.Color(255, 255, 255));

        txt_search_no_invoice.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_no_invoice.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_no_invoiceKeyPressed(evt);
            }
        });

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel7.setText("No Invoice :");

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel9.setText("Pemilik Barang :");

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel13.setText("Invoice Date :");

        Date1.setBackground(new java.awt.Color(255, 255, 255));
        Date1.setToolTipText("");
        Date1.setDateFormatString("dd MMMM yyyy");
        Date1.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        Date1.setMinSelectableDate(new java.util.Date(1483207315000L));

        jLabel34.setBackground(new java.awt.Color(255, 255, 255));
        jLabel34.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        jLabel34.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel34.setText("-");

        Date2.setBackground(new java.awt.Color(255, 255, 255));
        Date2.setDateFormatString("dd MMMM yyyy");
        Date2.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N

        button_refresh.setBackground(new java.awt.Color(255, 255, 255));
        button_refresh.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_refresh.setText("Refresh");
        button_refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refreshActionPerformed(evt);
            }
        });

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel12.setText("Kategori Buyer :");

        ComboBox_KategoriBuyer.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_KategoriBuyer.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        button_export.setBackground(new java.awt.Color(255, 255, 255));
        button_export.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        button_export.setText("Export to Excel");
        button_export.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_exportActionPerformed(evt);
            }
        });

        CheckBox_waleta.setBackground(new java.awt.Color(255, 255, 255));
        CheckBox_waleta.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        CheckBox_waleta.setText("Waleta");

        CheckBox_esta.setBackground(new java.awt.Color(255, 255, 255));
        CheckBox_esta.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        CheckBox_esta.setText("Esta");

        CheckBox_jastip.setBackground(new java.awt.Color(255, 255, 255));
        CheckBox_jastip.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        CheckBox_jastip.setText("Jastip");

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel8.setText("Kode Buyer / Buyer Name :");

        txt_search_buyer.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_buyer.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_buyerKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_search_karyawanLayout = new javax.swing.GroupLayout(jPanel_search_karyawan);
        jPanel_search_karyawan.setLayout(jPanel_search_karyawanLayout);
        jPanel_search_karyawanLayout.setHorizontalGroup(
            jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_search_karyawanLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_search_karyawanLayout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(CheckBox_waleta)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(CheckBox_esta)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(CheckBox_jastip))
                    .addGroup(jPanel_search_karyawanLayout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_no_invoice, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_buyer, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_KategoriBuyer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date1, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel34)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date2, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_refresh)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_export)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel_search_karyawanLayout.setVerticalGroup(
            jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_search_karyawanLayout.createSequentialGroup()
                .addGroup(jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_search_no_invoice, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_buyer, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_KategoriBuyer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_refresh, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CheckBox_waleta, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CheckBox_esta, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CheckBox_jastip, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 5, Short.MAX_VALUE))
        );

        button_edit.setBackground(new java.awt.Color(255, 255, 255));
        button_edit.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_edit.setText("Edit data Invoice");
        button_edit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_editActionPerformed(evt);
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

        button_view_payment.setBackground(new java.awt.Color(255, 255, 255));
        button_view_payment.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_view_payment.setText("View Payment data");
        button_view_payment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_view_paymentActionPerformed(evt);
            }
        });

        button_view_transfer.setBackground(new java.awt.Color(255, 255, 255));
        button_view_transfer.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_view_transfer.setText("View Transfer to JTP data");
        button_view_transfer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_view_transferActionPerformed(evt);
            }
        });

        table_summary.setFont(new java.awt.Font("Arial Narrow", 1, 18)); // NOI18N
        table_summary.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Total Net Weight", "Waleta", "Esta", "Jasa Titip", "Total IDR", "Total USD", "Total CNY", "Waleta", "Esta", "Jasa Titip", "Margin JTP", "AR Outstanding", "AP Outstanding"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
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
        table_summary.setRowHeight(25);
        jScrollPane2.setViewportView(table_summary);

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel10.setText("Summary");

        button_input_invoice.setBackground(new java.awt.Color(255, 255, 255));
        button_input_invoice.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_input_invoice.setText("Input Invoice");
        button_input_invoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_input_invoiceActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 709, Short.MAX_VALUE)
                        .addComponent(button_input_invoice)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_edit)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_delete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_view_payment)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_view_transfer)))
                .addContainerGap())
            .addComponent(jPanel_search_karyawan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel_search_karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(button_delete, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(button_edit, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_input_invoice, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(button_view_payment, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_view_transfer, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 517, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Sales & Payment Report", jPanel2);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        txt_search_no_invoice_hutangExim.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_no_invoice_hutangExim.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_no_invoice_hutangEximKeyPressed(evt);
            }
        });

        button_export_hutangExim.setBackground(new java.awt.Color(255, 255, 255));
        button_export_hutangExim.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        button_export_hutangExim.setText("Export to Excel");
        button_export_hutangExim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_hutangEximActionPerformed(evt);
            }
        });

        ComboBox_KategoriBuyer_hutangExim.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_KategoriBuyer_hutangExim.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel14.setText("Kategori Buyer :");

        button_refresh_hutangExim.setBackground(new java.awt.Color(255, 255, 255));
        button_refresh_hutangExim.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_refresh_hutangExim.setText("Refresh");
        button_refresh_hutangExim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refresh_hutangEximActionPerformed(evt);
            }
        });

        Date_hutangExim2.setBackground(new java.awt.Color(255, 255, 255));
        Date_hutangExim2.setDateFormatString("dd MMMM yyyy");
        Date_hutangExim2.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N

        jLabel35.setBackground(new java.awt.Color(255, 255, 255));
        jLabel35.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        jLabel35.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel35.setText("-");

        Date_hutangExim1.setBackground(new java.awt.Color(255, 255, 255));
        Date_hutangExim1.setToolTipText("");
        Date_hutangExim1.setDateFormatString("dd MMMM yyyy");
        Date_hutangExim1.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        Date_hutangExim1.setMinSelectableDate(new java.util.Date(1483207315000L));

        txt_search_buyer_hutangExim.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_buyer_hutangExim.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_buyer_hutangEximKeyPressed(evt);
            }
        });

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel15.setText("Invoice Date :");

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel11.setText("Kode Buyer / Buyer Name :");

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel16.setText("No Invoice :");

        tabel_hutangExim.setAutoCreateRowSorter(true);
        tabel_hutangExim.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        tabel_hutangExim.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "No invoice", "Buyer Name", "Invoice Date", "Net Weight", "Currency", "Value", "Tgl Hutang", "Hutang USD", "Tgl Pelunasan"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Float.class, java.lang.String.class, java.lang.Double.class, java.lang.Object.class, java.lang.Double.class, java.lang.Object.class
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
        tabel_hutangExim.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(tabel_hutangExim);

        button_input_hutangExim.setBackground(new java.awt.Color(255, 255, 255));
        button_input_hutangExim.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_input_hutangExim.setText("Input Baru");
        button_input_hutangExim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_input_hutangEximActionPerformed(evt);
            }
        });

        button_edit_hutangExim.setBackground(new java.awt.Color(255, 255, 255));
        button_edit_hutangExim.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_edit_hutangExim.setText("Edit");
        button_edit_hutangExim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_edit_hutangEximActionPerformed(evt);
            }
        });

        button_delete_hutangExim.setBackground(new java.awt.Color(255, 255, 255));
        button_delete_hutangExim.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_delete_hutangExim.setText("Delete");
        button_delete_hutangExim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_delete_hutangEximActionPerformed(evt);
            }
        });

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel17.setText("Total Hutang :");

        label_total_hutang.setBackground(new java.awt.Color(255, 255, 255));
        label_total_hutang.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_hutang.setText("000");

        jLabel19.setBackground(new java.awt.Color(255, 255, 255));
        jLabel19.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel19.setText("Total Hutang Belum terbayar :");

        label_total_hutang_belum_terbayar.setBackground(new java.awt.Color(255, 255, 255));
        label_total_hutang_belum_terbayar.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_hutang_belum_terbayar.setText("000");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel16)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_no_invoice_hutangExim, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_buyer_hutangExim, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_KategoriBuyer_hutangExim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_hutangExim1, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel35)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_hutangExim2, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_refresh_hutangExim)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_export_hutangExim))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(button_input_hutangExim)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_edit_hutangExim)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_delete_hutangExim)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel17)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_hutang)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel19)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_hutang_belum_terbayar)))
                        .addGap(0, 214, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_search_no_invoice_hutangExim, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_buyer_hutangExim, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_KategoriBuyer_hutangExim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_refresh_hutangExim, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export_hutangExim, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_hutangExim1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_hutangExim2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(button_delete_hutangExim, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(button_edit_hutangExim, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_input_hutangExim, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_hutang, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_hutang_belum_terbayar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 612, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Hutang Exim", jPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txt_search_no_invoiceKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_no_invoiceKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_search_no_invoiceKeyPressed

    private void button_refreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refreshActionPerformed
        // TODO add your handling code here:
        refreshTable();
    }//GEN-LAST:event_button_refreshActionPerformed

    private void button_editActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_editActionPerformed
        // TODO add your handling code here:
        int x = tabel_payment.getSelectedRow();
        if (x > -1) {
            String no_invoice = tabel_payment.getValueAt(x, 3).toString();
            JDialog_Input_InvoicePayment dialog = new JDialog_Input_InvoicePayment(new javax.swing.JFrame(), true, no_invoice);
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            dialog.setEnabled(true);
            refreshTable();
        } else {
            JOptionPane.showMessageDialog(this, "Silahkan klik pada data yang akan di Edit");
        }
    }//GEN-LAST:event_button_editActionPerformed

    private void button_exportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_exportActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) tabel_payment.getModel();
        ExportToExcel.writeToExcel(model, jPanel2);
    }//GEN-LAST:event_button_exportActionPerformed

    private void button_deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_deleteActionPerformed
        // TODO add your handling code here:
        int x = tabel_payment.getSelectedRow();
        if (x > -1) {
            String no_invoice = tabel_payment.getValueAt(x, 3).toString();
            int dialogResult = JOptionPane.showConfirmDialog(this, "Yakin hapus data ini?", "Warning", 0);
            if (dialogResult == JOptionPane.YES_OPTION) {
                try {
                    // delete code here
                    String Query = "DELETE FROM `tb_payment_report` WHERE `tb_payment_report`.`invoice` = '" + no_invoice + "'";
                    Utility.db.getConnection().createStatement();
                    if ((Utility.db.getStatement().executeUpdate(Query)) == 1) {
                        JOptionPane.showMessageDialog(this, "data Deleted Successfully");
                        refreshTable();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(JPanel_Payment_Report.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Silahkan klik pada data yang akan di Delete");
        }
    }//GEN-LAST:event_button_deleteActionPerformed

    private void button_view_paymentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_view_paymentActionPerformed
        // TODO add your handling code here:
        int x = tabel_payment.getSelectedRow();
        if (x > -1) {
            String no_invoice = tabel_payment.getValueAt(x, 3).toString();
            JDialog_Invoice_Payment dialog = new JDialog_Invoice_Payment(new javax.swing.JFrame(), true, no_invoice);
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            dialog.setEnabled(true);
            refreshTable();
        } else {
            JOptionPane.showMessageDialog(this, "Silahkan klik pada data yang akan di Edit");
        }
    }//GEN-LAST:event_button_view_paymentActionPerformed

    private void button_view_transferActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_view_transferActionPerformed
        // TODO add your handling code here:
        int x = tabel_payment.getSelectedRow();
        if (x > -1) {
            String no_invoice = tabel_payment.getValueAt(x, 3).toString();
            JDialog_Invoice_Payment_to_JTP dialog = new JDialog_Invoice_Payment_to_JTP(new javax.swing.JFrame(), true, no_invoice);
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            dialog.setEnabled(true);
            refreshTable();
        } else {
            JOptionPane.showMessageDialog(this, "Silahkan klik pada data yang akan di Edit");
        }
    }//GEN-LAST:event_button_view_transferActionPerformed

    private void button_input_invoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_input_invoiceActionPerformed
        // TODO add your handling code here:
        JDialog_Input_InvoicePayment dialog = new JDialog_Input_InvoicePayment(new javax.swing.JFrame(), true, null);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);
        refreshTable();
    }//GEN-LAST:event_button_input_invoiceActionPerformed

    private void txt_search_buyerKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_buyerKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_search_buyerKeyPressed

    private void txt_search_no_invoice_hutangEximKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_no_invoice_hutangEximKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_hutangExim();
        }
    }//GEN-LAST:event_txt_search_no_invoice_hutangEximKeyPressed

    private void button_export_hutangEximActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_hutangEximActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) tabel_hutangExim.getModel();
        ExportToExcel.writeToExcel(model, jPanel1);
    }//GEN-LAST:event_button_export_hutangEximActionPerformed

    private void button_refresh_hutangEximActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refresh_hutangEximActionPerformed
        // TODO add your handling code here:
        refreshTable_hutangExim();
    }//GEN-LAST:event_button_refresh_hutangEximActionPerformed

    private void txt_search_buyer_hutangEximKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_buyer_hutangEximKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_hutangExim();
        }
    }//GEN-LAST:event_txt_search_buyer_hutangEximKeyPressed

    private void button_input_hutangEximActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_input_hutangEximActionPerformed
        // TODO add your handling code here:
        JDialog_Input_InvoiceHutangExim dialog = new JDialog_Input_InvoiceHutangExim(new javax.swing.JFrame(), true, null);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);
        refreshTable_hutangExim();
    }//GEN-LAST:event_button_input_hutangEximActionPerformed

    private void button_edit_hutangEximActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_edit_hutangEximActionPerformed
        // TODO add your handling code here:
        int x = tabel_hutangExim.getSelectedRow();
        if (x > -1) {
            String invoice = tabel_hutangExim.getValueAt(x, 1).toString();
            JDialog_Input_InvoiceHutangExim dialog = new JDialog_Input_InvoiceHutangExim(new javax.swing.JFrame(), true, invoice);
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            dialog.setEnabled(true);
            refreshTable_hutangExim();
        } else {
            JOptionPane.showMessageDialog(this, "Silahkan klik pada data yang akan di Edit");
        }
    }//GEN-LAST:event_button_edit_hutangEximActionPerformed

    private void button_delete_hutangEximActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_delete_hutangEximActionPerformed
        // TODO add your handling code here:
        int x = tabel_hutangExim.getSelectedRow();
        if (x > -1) {
            String no_invoice = tabel_hutangExim.getValueAt(x, 1).toString();
            int dialogResult = JOptionPane.showConfirmDialog(this, "Yakin hapus data ini?", "Warning", 0);
            if (dialogResult == JOptionPane.YES_OPTION) {
                try {
                    // delete code here
                    String Query = "DELETE FROM `tb_hutang_exim` WHERE `tb_hutang_exim`.`invoice` = '" + no_invoice + "'";
                    Utility.db.getConnection().createStatement();
                    if ((Utility.db.getStatement().executeUpdate(Query)) == 1) {
                        JOptionPane.showMessageDialog(this, "data Deleted Successfully");
                        refreshTable_hutangExim();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(JPanel_Payment_Report.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Silahkan klik pada data yang akan di Delete");
        }
    }//GEN-LAST:event_button_delete_hutangEximActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox CheckBox_esta;
    private javax.swing.JCheckBox CheckBox_jastip;
    private javax.swing.JCheckBox CheckBox_waleta;
    private javax.swing.JComboBox<String> ComboBox_KategoriBuyer;
    private javax.swing.JComboBox<String> ComboBox_KategoriBuyer_hutangExim;
    private com.toedter.calendar.JDateChooser Date1;
    private com.toedter.calendar.JDateChooser Date2;
    private com.toedter.calendar.JDateChooser Date_hutangExim1;
    private com.toedter.calendar.JDateChooser Date_hutangExim2;
    public static javax.swing.JButton button_delete;
    public static javax.swing.JButton button_delete_hutangExim;
    public static javax.swing.JButton button_edit;
    public static javax.swing.JButton button_edit_hutangExim;
    private javax.swing.JButton button_export;
    private javax.swing.JButton button_export_hutangExim;
    public static javax.swing.JButton button_input_hutangExim;
    public static javax.swing.JButton button_input_invoice;
    public static javax.swing.JButton button_refresh;
    public static javax.swing.JButton button_refresh_hutangExim;
    public static javax.swing.JButton button_view_payment;
    public static javax.swing.JButton button_view_transfer;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel_search_karyawan;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel label_total_hutang;
    private javax.swing.JLabel label_total_hutang_belum_terbayar;
    private javax.swing.JTable tabel_hutangExim;
    private javax.swing.JTable tabel_payment;
    private javax.swing.JTable table_summary;
    private javax.swing.JTextField txt_search_buyer;
    private javax.swing.JTextField txt_search_buyer_hutangExim;
    private javax.swing.JTextField txt_search_no_invoice;
    private javax.swing.JTextField txt_search_no_invoice_hutangExim;
    // End of variables declaration//GEN-END:variables
}
