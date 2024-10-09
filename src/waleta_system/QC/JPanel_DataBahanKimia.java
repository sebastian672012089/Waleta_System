package waleta_system.QC;

import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
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

public class JPanel_DataBahanKimia extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    DecimalFormat decimalFormat = new DecimalFormat();

    public JPanel_DataBahanKimia() {
        initComponents();
        Table_PembelianBahanKimia.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && Table_PembelianBahanKimia.getSelectedRow() != -1) {
                    int i = Table_PembelianBahanKimia.getSelectedRow();
                    refreshTable_DataPemakaian_perPembelian((int) Table_PembelianBahanKimia.getValueAt(i, 0));
                }
            }
        });
    }

    public void init() {
        try {
            ComboBox_Filter_NamaBahanKimia_Pembelian.removeAllItems();
            ComboBox_Filter_NamaBahanKimia_Pembelian.addItem("Pilih Semua");
            sql = "SELECT `kode_bahan_kimia`, `nama_bahan_kimia` FROM `tb_lab_bahan_kimia` WHERE 1 ORDER BY `nama_bahan_kimia`";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_Filter_NamaBahanKimia_Pembelian.addItem(rs.getString("kode_bahan_kimia") + "-" + rs.getString("nama_bahan_kimia"));
            }
            refreshTable_PembelianBahanKimia();
            refreshTable_MasterBahanKimia();
            refreshTable_PemakaianBahanKimia();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_DataBahanKimia.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_PembelianBahanKimia() {
        try {
            float total_pembelian = 0, total_pemakaian = 0;
            DefaultTableModel model = (DefaultTableModel) Table_PembelianBahanKimia.getModel();
            model.setRowCount(0);
            String Filter_NamaBahanKimia = "1\n";
            if (ComboBox_Filter_NamaBahanKimia_Pembelian.getSelectedIndex() > 0) {
                Filter_NamaBahanKimia = "`nama_bahan_kimia` = '" + ComboBox_Filter_NamaBahanKimia_Pembelian.getSelectedItem().toString().split("-", 2)[1] + "' \n";
            }
            String filter_tanggal_pembelian = "";
            if (Date_PembelianBahanKimia1.getDate() != null && Date_PembelianBahanKimia2.getDate() != null) {
                filter_tanggal_pembelian = " AND (`tanggal_pembelian` BETWEEN '" + dateFormat.format(Date_PembelianBahanKimia1.getDate()) + "' AND '" + dateFormat.format(Date_PembelianBahanKimia2.getDate()) + "') \n";
            }
            String filter_tanggal_kadaluarsa = "";
            if (Date_KadaluarsaBahanKimia1.getDate() != null && Date_KadaluarsaBahanKimia2.getDate() != null) {
                filter_tanggal_kadaluarsa = " AND (`tanggal_expired` BETWEEN '" + dateFormat.format(Date_KadaluarsaBahanKimia1.getDate()) + "' AND '" + dateFormat.format(Date_KadaluarsaBahanKimia2.getDate()) + "') \n";
            }
            sql = "SELECT `tb_lab_bahan_kimia_pembelian`.`id_pembelian`, `tanggal_pembelian`, `tanggal_expired`, `tb_lab_bahan_kimia_pembelian`.`kode_bahan_kimia`, `tb_lab_bahan_kimia`.`nama_bahan_kimia`, `jumlah_pembelian`, `tb_lab_bahan_kimia`.`satuan`, \n"
                    + "SUM(`jumlah_pemakaian`) AS 'jumlah_pemakaian'"
                    + "FROM `tb_lab_bahan_kimia_pembelian` \n"
                    + "LEFT JOIN `tb_lab_bahan_kimia` ON `tb_lab_bahan_kimia_pembelian`.`kode_bahan_kimia` = `tb_lab_bahan_kimia`.`kode_bahan_kimia`\n"
                    + "LEFT JOIN `tb_lab_bahan_kimia_pemakaian` ON `tb_lab_bahan_kimia_pembelian`.`id_pembelian` = `tb_lab_bahan_kimia_pemakaian`.`id_pembelian`\n"
                    + "WHERE \n"
                    + Filter_NamaBahanKimia
                    + filter_tanggal_pembelian
                    + filter_tanggal_kadaluarsa
                    + "GROUP BY `tb_lab_bahan_kimia_pembelian`.`id_pembelian` "
                    + "ORDER BY `id_pembelian` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[15];
            while (rs.next()) {
                row[0] = rs.getInt("id_pembelian");
                row[1] = rs.getDate("tanggal_pembelian");
                row[2] = rs.getDate("tanggal_expired");
                row[3] = rs.getInt("kode_bahan_kimia");
                row[4] = rs.getString("nama_bahan_kimia");
                row[5] = rs.getFloat("jumlah_pembelian");
                row[6] = rs.getString("satuan");
                row[7] = rs.getFloat("jumlah_pembelian") - rs.getFloat("jumlah_pemakaian");
                model.addRow(row);
                total_pembelian = total_pembelian + rs.getFloat("jumlah_pembelian");
                total_pemakaian = total_pemakaian + rs.getFloat("jumlah_pemakaian");
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_PembelianBahanKimia);
            int rowData = Table_PembelianBahanKimia.getRowCount();
            label_total_data_PembelianBahanKimia.setText(decimalFormat.format(rowData));
            label_total_jumlah_PembelianBahanKimia.setText(decimalFormat.format(total_pembelian));
            label_total_stok_PembelianBahanKimia.setText(decimalFormat.format(total_pembelian - total_pemakaian));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_DataBahanKimia.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_DataPemakaian_perPembelian(int id_pembelian) {
        try {
            float total_pemakaian = 0;
            DefaultTableModel model = (DefaultTableModel) Table_DataPemakaian_perPembelian.getModel();
            model.setRowCount(0);
            sql = "SELECT `id_pemakaian`, `waktu_ambil`, `jumlah_pemakaian`, `tb_lab_bahan_kimia_pemakaian`.`id_pegawai`, `tb_karyawan`.`nama_pegawai`, `id_pembelian` \n"
                    + "FROM `tb_lab_bahan_kimia_pemakaian` \n"
                    + "LEFT JOIN `tb_karyawan` ON `tb_lab_bahan_kimia_pemakaian`.`id_pegawai` = `tb_karyawan`.`id_pegawai`\n"
                    + "WHERE "
                    + "`id_pembelian` = '" + id_pembelian + "' ";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[10];
            while (rs.next()) {
                row[0] = rs.getTimestamp("waktu_ambil");
                row[1] = rs.getString("id_pegawai");
                row[2] = rs.getString("nama_pegawai");
                row[3] = rs.getInt("jumlah_pemakaian");
                model.addRow(row);
                total_pemakaian = total_pemakaian + rs.getFloat("jumlah_pemakaian");
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_DataPemakaian_perPembelian);
            int rowData = Table_DataPemakaian_perPembelian.getRowCount();
            label_total_data_Pemakaian_perPembelian.setText(decimalFormat.format(rowData));
            label_total_pemakaian_PembelianBahanKimia.setText(decimalFormat.format(total_pemakaian));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_DataBahanKimia.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_MasterBahanKimia() {
        try {
            DefaultTableModel model = (DefaultTableModel) table_MasterBahanKimia.getModel();
            model.setRowCount(0);
            sql = "SELECT "
                    + "`kode_bahan_kimia`, `nama_bahan_kimia`, `satuan`, `kondisi`, `supplier`, `penggunaan`, `aturan_pemakaian`, `msds`, `no_dokumen`, `no_revisi`, `tanggal_dokumen`, `menggantikan_no_dokumen` "
                    + "FROM `tb_lab_bahan_kimia` "
                    + "WHERE "
                    + "`nama_bahan_kimia` LIKE '%" + txt_search_MasterBahanKimia_NamaBahanKimia.getText() + "%' ";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[15];
            while (rs.next()) {
                row[0] = rs.getInt("kode_bahan_kimia");
                row[1] = rs.getString("nama_bahan_kimia");
                row[2] = rs.getString("satuan");
                row[3] = rs.getString("kondisi");
                row[4] = rs.getString("supplier");
                row[5] = rs.getString("penggunaan");
                row[6] = rs.getString("aturan_pemakaian");
                row[7] = rs.getString("msds");
                row[8] = rs.getString("no_dokumen");
                row[9] = rs.getInt("no_revisi");
                row[10] = rs.getDate("tanggal_dokumen");
                row[11] = rs.getString("menggantikan_no_dokumen");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(table_MasterBahanKimia);
            int rowData = table_MasterBahanKimia.getRowCount();
            label_total_data_MasterBahanKimia.setText(decimalFormat.format(rowData));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_DataBahanKimia.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_PemakaianBahanKimia() {
        try {
            float total_pemakaian = 0;
            DefaultTableModel model = (DefaultTableModel) table_PemakaianBahanKimia.getModel();
            model.setRowCount(0);
            String filter_tanggal_pemakaian = "";
            if (Date_PemakaianBahanKimia1.getDate() != null && Date_PemakaianBahanKimia2.getDate() != null) {
                filter_tanggal_pemakaian = " AND (DATE(`waktu_ambil`) BETWEEN '" + dateFormat.format(Date_PemakaianBahanKimia1.getDate()) + "' AND '" + dateFormat.format(Date_PemakaianBahanKimia2.getDate()) + "') \n";
            }
            sql = "SELECT `waktu_ambil`, `tb_karyawan`.`nama_pegawai`, `nama_bahan_kimia`, `jumlah_pemakaian`, `satuan` \n"
                    + "FROM `tb_lab_bahan_kimia_pemakaian` \n"
                    + "LEFT JOIN `tb_karyawan` ON `tb_lab_bahan_kimia_pemakaian`.`id_pegawai` = `tb_karyawan`.`id_pegawai`\n"
                    + "LEFT JOIN `tb_lab_bahan_kimia_pembelian` ON `tb_lab_bahan_kimia_pemakaian`.`id_pembelian` = `tb_lab_bahan_kimia_pembelian`.`id_pembelian`\n"
                    + "LEFT JOIN `tb_lab_bahan_kimia` ON `tb_lab_bahan_kimia_pembelian`.`kode_bahan_kimia` = `tb_lab_bahan_kimia`.`kode_bahan_kimia`\n"
                    + "WHERE "
                    + "`nama_bahan_kimia` LIKE '%" + txt_search_PemakaianBahanKimia_NamaBahanKimia.getText() + "%' "
                    + filter_tanggal_pemakaian;
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[10];
            while (rs.next()) {
                row[0] = rs.getDate("waktu_ambil");
                row[1] = rs.getTime("waktu_ambil");
                row[2] = rs.getString("nama_pegawai");
                row[3] = rs.getString("nama_bahan_kimia");
                row[4] = rs.getFloat("jumlah_pemakaian");
                row[5] = rs.getString("satuan");
                model.addRow(row);
                total_pemakaian = total_pemakaian + rs.getFloat("jumlah_pemakaian");
            }
            ColumnsAutoSizer.sizeColumnsToFit(table_PemakaianBahanKimia);
            int rowData = table_PemakaianBahanKimia.getRowCount();
            label_total_data_PemakaianBahanKimia.setText(decimalFormat.format(rowData));
            label_total_PemakaianBahanKimia.setText(decimalFormat.format(total_pemakaian));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_DataBahanKimia.class.getName()).log(Level.SEVERE, null, ex);
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
        jPanel_PembelianBahanKimia = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        Table_PembelianBahanKimia = new javax.swing.JTable();
        jLabel12 = new javax.swing.JLabel();
        label_total_data_PembelianBahanKimia = new javax.swing.JLabel();
        button_PembelianBahanKimia = new javax.swing.JButton();
        button_export_PembelianBahanKimia = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        Date_PembelianBahanKimia1 = new com.toedter.calendar.JDateChooser();
        Date_PembelianBahanKimia2 = new com.toedter.calendar.JDateChooser();
        jLabel3 = new javax.swing.JLabel();
        Date_KadaluarsaBahanKimia1 = new com.toedter.calendar.JDateChooser();
        Date_KadaluarsaBahanKimia2 = new com.toedter.calendar.JDateChooser();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        Table_DataPemakaian_perPembelian = new javax.swing.JTable();
        jLabel14 = new javax.swing.JLabel();
        label_total_data_Pemakaian_perPembelian = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        label_total_pemakaian_PembelianBahanKimia = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        label_total_jumlah_PembelianBahanKimia = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        label_total_stok_PembelianBahanKimia = new javax.swing.JLabel();
        button_catatan_pemakaian_BahanKimia = new javax.swing.JButton();
        ComboBox_Filter_NamaBahanKimia_Pembelian = new javax.swing.JComboBox<>();
        jPanel_MasterBahanKimia = new javax.swing.JPanel();
        jScrollPane11 = new javax.swing.JScrollPane();
        table_MasterBahanKimia = new javax.swing.JTable();
        button_search_MasterBahanKimia = new javax.swing.JButton();
        txt_search_MasterBahanKimia_NamaBahanKimia = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        button_edit_MasterBahanKimia = new javax.swing.JButton();
        button_tambah_MasterBahanKimia = new javax.swing.JButton();
        button_delete_MasterBahanKimia = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        label_total_data_MasterBahanKimia = new javax.swing.JLabel();
        jPanel_PemakaianBahanKimia = new javax.swing.JPanel();
        jScrollPane12 = new javax.swing.JScrollPane();
        table_PemakaianBahanKimia = new javax.swing.JTable();
        button_search_PemakaianBahanKimia = new javax.swing.JButton();
        txt_search_PemakaianBahanKimia_NamaBahanKimia = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        Date_PemakaianBahanKimia1 = new com.toedter.calendar.JDateChooser();
        Date_PemakaianBahanKimia2 = new com.toedter.calendar.JDateChooser();
        jLabel19 = new javax.swing.JLabel();
        label_total_data_PemakaianBahanKimia = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        label_total_PemakaianBahanKimia = new javax.swing.JLabel();
        button_export_PemakaianBahanKimia = new javax.swing.JButton();

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N

        jPanel_PembelianBahanKimia.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_PembelianBahanKimia.setPreferredSize(new java.awt.Dimension(1366, 701));

        Table_PembelianBahanKimia.setAutoCreateRowSorter(true);
        Table_PembelianBahanKimia.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Table_PembelianBahanKimia.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Tgl Pembelian", "Tgl kadaluarsa", "Kode Bahan Kimia", "Nama Bahan Kimia", "Jumlah", "Satuan", "Stok"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, java.lang.Integer.class, java.lang.String.class, java.lang.Float.class, java.lang.String.class, java.lang.Float.class
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
        Table_PembelianBahanKimia.setRowHeight(20);
        Table_PembelianBahanKimia.getTableHeader().setReorderingAllowed(false);
        jScrollPane8.setViewportView(Table_PembelianBahanKimia);

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel12.setText("Total Data :");

        label_total_data_PembelianBahanKimia.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_PembelianBahanKimia.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_data_PembelianBahanKimia.setText("TOTAL");

        button_PembelianBahanKimia.setBackground(new java.awt.Color(255, 255, 255));
        button_PembelianBahanKimia.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_PembelianBahanKimia.setText("Search");
        button_PembelianBahanKimia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_PembelianBahanKimiaActionPerformed(evt);
            }
        });

        button_export_PembelianBahanKimia.setBackground(new java.awt.Color(255, 255, 255));
        button_export_PembelianBahanKimia.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_export_PembelianBahanKimia.setText("Export To Excel");
        button_export_PembelianBahanKimia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_PembelianBahanKimiaActionPerformed(evt);
            }
        });

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel1.setText("Nama Bahan Kimia :");

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setText("Tanggal Pembelian :");

        Date_PembelianBahanKimia1.setBackground(new java.awt.Color(255, 255, 255));
        Date_PembelianBahanKimia1.setDateFormatString("dd MMM yyyy");
        Date_PembelianBahanKimia1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        Date_PembelianBahanKimia2.setBackground(new java.awt.Color(255, 255, 255));
        Date_PembelianBahanKimia2.setDateFormatString("dd MMM yyyy");
        Date_PembelianBahanKimia2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel3.setText("Tanggal Kadaluarsa :");

        Date_KadaluarsaBahanKimia1.setBackground(new java.awt.Color(255, 255, 255));
        Date_KadaluarsaBahanKimia1.setDateFormatString("dd MMM yyyy");
        Date_KadaluarsaBahanKimia1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        Date_KadaluarsaBahanKimia2.setBackground(new java.awt.Color(255, 255, 255));
        Date_KadaluarsaBahanKimia2.setDateFormatString("dd MMM yyyy");
        Date_KadaluarsaBahanKimia2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Data Pemakaian", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 12))); // NOI18N

        Table_DataPemakaian_perPembelian.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Table_DataPemakaian_perPembelian.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Waktu Ambil", "ID Petugas", "Nama Petugas", "Jumlah"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class
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
        Table_DataPemakaian_perPembelian.setRowHeight(20);
        jScrollPane1.setViewportView(Table_DataPemakaian_perPembelian);

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel14.setText("Total Data :");

        label_total_data_Pemakaian_perPembelian.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_Pemakaian_perPembelian.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_data_Pemakaian_perPembelian.setText("0,000");

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel17.setText("Total Pemakaian :");

        label_total_pemakaian_PembelianBahanKimia.setBackground(new java.awt.Color(255, 255, 255));
        label_total_pemakaian_PembelianBahanKimia.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_pemakaian_PembelianBahanKimia.setText("000,000,000");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_data_Pemakaian_perPembelian, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_pemakaian_PembelianBahanKimia, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(label_total_data_Pemakaian_perPembelian)
                    .addComponent(jLabel14)
                    .addComponent(label_total_pemakaian_PembelianBahanKimia)
                    .addComponent(jLabel17))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 509, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel15.setText("Total Pembelian :");

        label_total_jumlah_PembelianBahanKimia.setBackground(new java.awt.Color(255, 255, 255));
        label_total_jumlah_PembelianBahanKimia.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_jumlah_PembelianBahanKimia.setText("TOTAL");

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel16.setText("Total Stok :");

        label_total_stok_PembelianBahanKimia.setBackground(new java.awt.Color(255, 255, 255));
        label_total_stok_PembelianBahanKimia.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_stok_PembelianBahanKimia.setText("TOTAL");

        button_catatan_pemakaian_BahanKimia.setBackground(new java.awt.Color(255, 255, 255));
        button_catatan_pemakaian_BahanKimia.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_catatan_pemakaian_BahanKimia.setText("Catatan Pemakaian Bahan Kimia");
        button_catatan_pemakaian_BahanKimia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_catatan_pemakaian_BahanKimiaActionPerformed(evt);
            }
        });

        ComboBox_Filter_NamaBahanKimia_Pembelian.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        ComboBox_Filter_NamaBahanKimia_Pembelian.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Pilih Semua" }));

        javax.swing.GroupLayout jPanel_PembelianBahanKimiaLayout = new javax.swing.GroupLayout(jPanel_PembelianBahanKimia);
        jPanel_PembelianBahanKimia.setLayout(jPanel_PembelianBahanKimiaLayout);
        jPanel_PembelianBahanKimiaLayout.setHorizontalGroup(
            jPanel_PembelianBahanKimiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_PembelianBahanKimiaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_PembelianBahanKimiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_PembelianBahanKimiaLayout.createSequentialGroup()
                        .addGroup(jPanel_PembelianBahanKimiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_PembelianBahanKimiaLayout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_Filter_NamaBahanKimia_Pembelian, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_PembelianBahanKimia1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_PembelianBahanKimia2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_KadaluarsaBahanKimia1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_KadaluarsaBahanKimia2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_PembelianBahanKimia)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_export_PembelianBahanKimia))
                            .addGroup(jPanel_PembelianBahanKimiaLayout.createSequentialGroup()
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_data_PembelianBahanKimia, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_jumlah_PembelianBahanKimia, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel16)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_stok_PembelianBahanKimia, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_catatan_pemakaian_BahanKimia)))
                        .addContainerGap(175, Short.MAX_VALUE))
                    .addGroup(jPanel_PembelianBahanKimiaLayout.createSequentialGroup()
                        .addComponent(jScrollPane8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );
        jPanel_PembelianBahanKimiaLayout.setVerticalGroup(
            jPanel_PembelianBahanKimiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_PembelianBahanKimiaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_PembelianBahanKimiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_PembelianBahanKimia1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_PembelianBahanKimia2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_KadaluarsaBahanKimia1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_KadaluarsaBahanKimia2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_PembelianBahanKimia, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export_PembelianBahanKimia, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_Filter_NamaBahanKimia_Pembelian, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_PembelianBahanKimiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel12)
                    .addComponent(label_total_data_PembelianBahanKimia)
                    .addComponent(jLabel15)
                    .addComponent(label_total_jumlah_PembelianBahanKimia)
                    .addComponent(jLabel16)
                    .addComponent(label_total_stok_PembelianBahanKimia)
                    .addComponent(button_catatan_pemakaian_BahanKimia, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_PembelianBahanKimiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel_PembelianBahanKimiaLayout.createSequentialGroup()
                        .addComponent(jScrollPane8)
                        .addContainerGap())))
        );

        jTabbedPane1.addTab("Pembelian Bahan Kimia", jPanel_PembelianBahanKimia);

        jPanel_MasterBahanKimia.setBackground(new java.awt.Color(255, 255, 255));

        table_MasterBahanKimia.setAutoCreateRowSorter(true);
        table_MasterBahanKimia.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        table_MasterBahanKimia.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode Bahan Kimia", "Nama Bahan Kimia", "Satuan", "Kondisi", "Supplier", "Penggunaan", "Aturan Pemakaian", "MSDS", "No Dokumen", "No Revisi", "Tgl Dokumen", "Menggantikan No Dokumen"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Object.class, java.lang.String.class
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
        table_MasterBahanKimia.setRowHeight(20);
        table_MasterBahanKimia.getTableHeader().setReorderingAllowed(false);
        jScrollPane11.setViewportView(table_MasterBahanKimia);

        button_search_MasterBahanKimia.setBackground(new java.awt.Color(255, 255, 255));
        button_search_MasterBahanKimia.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_search_MasterBahanKimia.setText("Refresh");
        button_search_MasterBahanKimia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_search_MasterBahanKimiaActionPerformed(evt);
            }
        });

        txt_search_MasterBahanKimia_NamaBahanKimia.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_search_MasterBahanKimia_NamaBahanKimia.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_MasterBahanKimia_NamaBahanKimiaKeyPressed(evt);
            }
        });

        jLabel23.setBackground(new java.awt.Color(255, 255, 255));
        jLabel23.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel23.setText("Nama Bahan Kimia :");

        button_edit_MasterBahanKimia.setBackground(new java.awt.Color(255, 255, 255));
        button_edit_MasterBahanKimia.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_edit_MasterBahanKimia.setText("Edit");
        button_edit_MasterBahanKimia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_edit_MasterBahanKimiaActionPerformed(evt);
            }
        });

        button_tambah_MasterBahanKimia.setBackground(new java.awt.Color(255, 255, 255));
        button_tambah_MasterBahanKimia.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_tambah_MasterBahanKimia.setText("Tambah Baru");
        button_tambah_MasterBahanKimia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_tambah_MasterBahanKimiaActionPerformed(evt);
            }
        });

        button_delete_MasterBahanKimia.setBackground(new java.awt.Color(255, 255, 255));
        button_delete_MasterBahanKimia.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_delete_MasterBahanKimia.setText("Delete");
        button_delete_MasterBahanKimia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_delete_MasterBahanKimiaActionPerformed(evt);
            }
        });

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel13.setText("Total Data :");

        label_total_data_MasterBahanKimia.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_MasterBahanKimia.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_data_MasterBahanKimia.setText("TOTAL");

        javax.swing.GroupLayout jPanel_MasterBahanKimiaLayout = new javax.swing.GroupLayout(jPanel_MasterBahanKimia);
        jPanel_MasterBahanKimia.setLayout(jPanel_MasterBahanKimiaLayout);
        jPanel_MasterBahanKimiaLayout.setHorizontalGroup(
            jPanel_MasterBahanKimiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_MasterBahanKimiaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_MasterBahanKimiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane11)
                    .addGroup(jPanel_MasterBahanKimiaLayout.createSequentialGroup()
                        .addComponent(jLabel23)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_MasterBahanKimia_NamaBahanKimia, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_search_MasterBahanKimia)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_tambah_MasterBahanKimia)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_edit_MasterBahanKimia)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_delete_MasterBahanKimia)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_data_MasterBahanKimia)
                        .addGap(0, 604, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel_MasterBahanKimiaLayout.setVerticalGroup(
            jPanel_MasterBahanKimiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_MasterBahanKimiaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_MasterBahanKimiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel_MasterBahanKimiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel_MasterBahanKimiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(button_edit_MasterBahanKimia, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_tambah_MasterBahanKimia, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_delete_MasterBahanKimia, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel_MasterBahanKimiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_search_MasterBahanKimia_NamaBahanKimia, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_search_MasterBahanKimia, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel_MasterBahanKimiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(label_total_data_MasterBahanKimia, javax.swing.GroupLayout.Alignment.CENTER, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.CENTER, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane11, javax.swing.GroupLayout.DEFAULT_SIZE, 605, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Data Bahan Kimia", jPanel_MasterBahanKimia);

        jPanel_PemakaianBahanKimia.setBackground(new java.awt.Color(255, 255, 255));

        table_PemakaianBahanKimia.setAutoCreateRowSorter(true);
        table_PemakaianBahanKimia.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        table_PemakaianBahanKimia.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tanggal AMbil", "Waktu Ambil", "Petugas", "Nama Bahan Kimia", "Jumlah", "Satuan"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.String.class
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
        table_PemakaianBahanKimia.setRowHeight(20);
        table_PemakaianBahanKimia.getTableHeader().setReorderingAllowed(false);
        jScrollPane12.setViewportView(table_PemakaianBahanKimia);

        button_search_PemakaianBahanKimia.setBackground(new java.awt.Color(255, 255, 255));
        button_search_PemakaianBahanKimia.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_search_PemakaianBahanKimia.setText("Refresh");
        button_search_PemakaianBahanKimia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_search_PemakaianBahanKimiaActionPerformed(evt);
            }
        });

        txt_search_PemakaianBahanKimia_NamaBahanKimia.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_search_PemakaianBahanKimia_NamaBahanKimia.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_PemakaianBahanKimia_NamaBahanKimiaKeyPressed(evt);
            }
        });

        jLabel24.setBackground(new java.awt.Color(255, 255, 255));
        jLabel24.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel24.setText("Nama Bahan Kimia :");

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel4.setText("Tanggal Pemakaian :");

        Date_PemakaianBahanKimia1.setBackground(new java.awt.Color(255, 255, 255));
        Date_PemakaianBahanKimia1.setDateFormatString("dd MMM yyyy");
        Date_PemakaianBahanKimia1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        Date_PemakaianBahanKimia2.setBackground(new java.awt.Color(255, 255, 255));
        Date_PemakaianBahanKimia2.setDateFormatString("dd MMM yyyy");
        Date_PemakaianBahanKimia2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel19.setBackground(new java.awt.Color(255, 255, 255));
        jLabel19.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel19.setText("Total Data :");

        label_total_data_PemakaianBahanKimia.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_PemakaianBahanKimia.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_data_PemakaianBahanKimia.setText("0,000");

        jLabel20.setBackground(new java.awt.Color(255, 255, 255));
        jLabel20.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel20.setText("Total Pemakaian :");

        label_total_PemakaianBahanKimia.setBackground(new java.awt.Color(255, 255, 255));
        label_total_PemakaianBahanKimia.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_PemakaianBahanKimia.setText("000,000,000");

        button_export_PemakaianBahanKimia.setBackground(new java.awt.Color(255, 255, 255));
        button_export_PemakaianBahanKimia.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_export_PemakaianBahanKimia.setText("Export To Excel");
        button_export_PemakaianBahanKimia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_PemakaianBahanKimiaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_PemakaianBahanKimiaLayout = new javax.swing.GroupLayout(jPanel_PemakaianBahanKimia);
        jPanel_PemakaianBahanKimia.setLayout(jPanel_PemakaianBahanKimiaLayout);
        jPanel_PemakaianBahanKimiaLayout.setHorizontalGroup(
            jPanel_PemakaianBahanKimiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_PemakaianBahanKimiaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_PemakaianBahanKimiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane12, javax.swing.GroupLayout.DEFAULT_SIZE, 1341, Short.MAX_VALUE)
                    .addGroup(jPanel_PemakaianBahanKimiaLayout.createSequentialGroup()
                        .addComponent(jLabel24)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_PemakaianBahanKimia_NamaBahanKimia, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_PemakaianBahanKimia1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_PemakaianBahanKimia2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_search_PemakaianBahanKimia)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_data_PemakaianBahanKimia, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_PemakaianBahanKimia, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_export_PemakaianBahanKimia)))
                .addContainerGap())
        );
        jPanel_PemakaianBahanKimiaLayout.setVerticalGroup(
            jPanel_PemakaianBahanKimiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_PemakaianBahanKimiaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_PemakaianBahanKimiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_PemakaianBahanKimia1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_PemakaianBahanKimia2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_PemakaianBahanKimia_NamaBahanKimia, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_data_PemakaianBahanKimia, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_PemakaianBahanKimia, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_search_PemakaianBahanKimia, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(button_export_PemakaianBahanKimia, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane12, javax.swing.GroupLayout.DEFAULT_SIZE, 618, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Data Pemakaian Bahan Kimia", jPanel_PemakaianBahanKimia);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1366, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 700, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void button_PembelianBahanKimiaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_PembelianBahanKimiaActionPerformed
        // TODO add your handling code here:
        refreshTable_PembelianBahanKimia();
    }//GEN-LAST:event_button_PembelianBahanKimiaActionPerformed

    private void button_export_PembelianBahanKimiaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_PembelianBahanKimiaActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Table_PembelianBahanKimia.getModel();
        ExportToExcel.writeToExcel(model, jPanel_PembelianBahanKimia);
    }//GEN-LAST:event_button_export_PembelianBahanKimiaActionPerformed

    private void button_search_MasterBahanKimiaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_MasterBahanKimiaActionPerformed
        // TODO add your handling code here:
        refreshTable_MasterBahanKimia();
    }//GEN-LAST:event_button_search_MasterBahanKimiaActionPerformed

    private void txt_search_MasterBahanKimia_NamaBahanKimiaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_MasterBahanKimia_NamaBahanKimiaKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_MasterBahanKimia();
        }
    }//GEN-LAST:event_txt_search_MasterBahanKimia_NamaBahanKimiaKeyPressed

    private void button_tambah_MasterBahanKimiaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_tambah_MasterBahanKimiaActionPerformed
        // TODO add your handling code here:
        JDialog_MasterBahanKimia_Tambah_Edit dialog = new JDialog_MasterBahanKimia_Tambah_Edit(new javax.swing.JFrame(), true, null);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);
        dialog.setResizable(false);
        refreshTable_MasterBahanKimia();
    }//GEN-LAST:event_button_tambah_MasterBahanKimiaActionPerformed

    private void button_edit_MasterBahanKimiaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_edit_MasterBahanKimiaActionPerformed
        // TODO add your handling code here:
        int j = table_MasterBahanKimia.getSelectedRow();
        if (j == -1) {
            JOptionPane.showMessageDialog(this, "Silahkan klik data pada tabel !");
        } else {
            String kode = table_MasterBahanKimia.getValueAt(j, 0).toString();
            JDialog_MasterBahanKimia_Tambah_Edit dialog = new JDialog_MasterBahanKimia_Tambah_Edit(new javax.swing.JFrame(), true, kode);
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            dialog.setEnabled(true);
            dialog.setResizable(false);
            refreshTable_MasterBahanKimia();
        }
    }//GEN-LAST:event_button_edit_MasterBahanKimiaActionPerformed

    private void button_delete_MasterBahanKimiaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_delete_MasterBahanKimiaActionPerformed
        // TODO add your handling code here:
        try {
            int j = table_MasterBahanKimia.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan klik data pada tabel !");
            } else {
                int dialogResult = JOptionPane.showConfirmDialog(this, "Yakin akan hapus data ini?", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    sql = "DELETE FROM `tb_lab_bahan_kimia` WHERE `kode_bahan_kimia` = '" + table_MasterBahanKimia.getValueAt(j, 0).toString() + "'";
                    Utility.db.getConnection().createStatement();
                    if ((Utility.db.getStatement().executeUpdate(sql)) == 1) {
                        JOptionPane.showMessageDialog(this, "data Deleted !");
                        refreshTable_MasterBahanKimia();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed!");
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(JPanel_DataBahanKimia.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_delete_MasterBahanKimiaActionPerformed

    private void button_search_PemakaianBahanKimiaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_PemakaianBahanKimiaActionPerformed
        // TODO add your handling code here:
        refreshTable_PemakaianBahanKimia();
    }//GEN-LAST:event_button_search_PemakaianBahanKimiaActionPerformed

    private void txt_search_PemakaianBahanKimia_NamaBahanKimiaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_PemakaianBahanKimia_NamaBahanKimiaKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_PemakaianBahanKimia();
        }
    }//GEN-LAST:event_txt_search_PemakaianBahanKimia_NamaBahanKimiaKeyPressed

    private void button_export_PemakaianBahanKimiaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_PemakaianBahanKimiaActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) table_PemakaianBahanKimia.getModel();
        ExportToExcel.writeToExcel(model, jPanel_PembelianBahanKimia);
    }//GEN-LAST:event_button_export_PemakaianBahanKimiaActionPerformed

    private void button_catatan_pemakaian_BahanKimiaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_catatan_pemakaian_BahanKimiaActionPerformed
        // TODO add your handling code here:
        if (ComboBox_Filter_NamaBahanKimia_Pembelian.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Silahkan pilih bahan kimia !");
        } else if (Date_PembelianBahanKimia1.getDate() == null || Date_PembelianBahanKimia2.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Silahkan pilih tanggal pembelian !");
        } else {
            try {
                String kode_bahan_kimia = ComboBox_Filter_NamaBahanKimia_Pembelian.getSelectedItem().toString().split("-")[0];
                sql = "SELECT `tb_lab_bahan_kimia_pembelian`.`id_pembelian`, `tb_lab_bahan_kimia_pembelian`.`jumlah_pembelian`, `tanggal_pembelian`,\n"
                        + "`tb_lab_bahan_kimia`.`nama_bahan_kimia`, `satuan`, `no_dokumen`, `no_revisi`, `tanggal_dokumen`, `menggantikan_no_dokumen`,\n"
                        + "`waktu_ambil`, DATE(`waktu_ambil`) AS 'tanggal_ambil', TIME(`waktu_ambil`) AS 'jam_ambil', `jumlah_pemakaian`, `tb_karyawan`.`nama_pegawai`\n"
                        + "FROM `tb_lab_bahan_kimia_pembelian` \n"
                        + "LEFT JOIN `tb_lab_bahan_kimia` ON `tb_lab_bahan_kimia_pembelian`.`kode_bahan_kimia` = `tb_lab_bahan_kimia`.`kode_bahan_kimia`\n"
                        + "LEFT JOIN `tb_lab_bahan_kimia_pemakaian` ON `tb_lab_bahan_kimia_pemakaian`.`id_pembelian` = `tb_lab_bahan_kimia_pembelian`.`id_pembelian`\n"
                        + "LEFT JOIN `tb_karyawan` ON `tb_lab_bahan_kimia_pemakaian`.`id_pegawai` = `tb_karyawan`.`id_pegawai`\n"
                        + "WHERE \n"
                        + "`tb_lab_bahan_kimia`.`kode_bahan_kimia` = '" + kode_bahan_kimia + "'\n"
                        + "AND `tanggal_pembelian` BETWEEN '" + dateFormat.format(Date_PembelianBahanKimia1.getDate()) + "' AND '" + dateFormat.format(Date_PembelianBahanKimia2.getDate()) + "'\n"
                        + "ORDER BY `tb_lab_bahan_kimia_pembelian`.`id_pembelian`, `waktu_ambil`";
                JRDesignQuery newQuery = new JRDesignQuery();
                newQuery.setText(sql);
                JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Catatan_Pemakaian_Bahan_Kimia.jrxml");
                JASP_DESIGN.setQuery(newQuery);

                JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
                JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, null, Utility.db.getConnection());
                JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
            } catch (JRException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
                Logger.getLogger(JPanel_Lab_LaporanProduksi.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_button_catatan_pemakaian_BahanKimiaActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_Filter_NamaBahanKimia_Pembelian;
    private com.toedter.calendar.JDateChooser Date_KadaluarsaBahanKimia1;
    private com.toedter.calendar.JDateChooser Date_KadaluarsaBahanKimia2;
    private com.toedter.calendar.JDateChooser Date_PemakaianBahanKimia1;
    private com.toedter.calendar.JDateChooser Date_PemakaianBahanKimia2;
    private com.toedter.calendar.JDateChooser Date_PembelianBahanKimia1;
    private com.toedter.calendar.JDateChooser Date_PembelianBahanKimia2;
    private javax.swing.JTable Table_DataPemakaian_perPembelian;
    private javax.swing.JTable Table_PembelianBahanKimia;
    private javax.swing.JButton button_PembelianBahanKimia;
    public javax.swing.JButton button_catatan_pemakaian_BahanKimia;
    public javax.swing.JButton button_delete_MasterBahanKimia;
    public javax.swing.JButton button_edit_MasterBahanKimia;
    private javax.swing.JButton button_export_PemakaianBahanKimia;
    private javax.swing.JButton button_export_PembelianBahanKimia;
    private javax.swing.JButton button_search_MasterBahanKimia;
    private javax.swing.JButton button_search_PemakaianBahanKimia;
    public javax.swing.JButton button_tambah_MasterBahanKimia;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel_MasterBahanKimia;
    private javax.swing.JPanel jPanel_PemakaianBahanKimia;
    private javax.swing.JPanel jPanel_PembelianBahanKimia;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel label_total_PemakaianBahanKimia;
    private javax.swing.JLabel label_total_data_MasterBahanKimia;
    private javax.swing.JLabel label_total_data_PemakaianBahanKimia;
    private javax.swing.JLabel label_total_data_Pemakaian_perPembelian;
    private javax.swing.JLabel label_total_data_PembelianBahanKimia;
    private javax.swing.JLabel label_total_jumlah_PembelianBahanKimia;
    private javax.swing.JLabel label_total_pemakaian_PembelianBahanKimia;
    private javax.swing.JLabel label_total_stok_PembelianBahanKimia;
    private javax.swing.JTable table_MasterBahanKimia;
    private javax.swing.JTable table_PemakaianBahanKimia;
    private javax.swing.JTextField txt_search_MasterBahanKimia_NamaBahanKimia;
    private javax.swing.JTextField txt_search_PemakaianBahanKimia_NamaBahanKimia;
    // End of variables declaration//GEN-END:variables
}
