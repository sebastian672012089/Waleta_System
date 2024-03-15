package waleta_system.BahanJadi;

import java.awt.HeadlessException;
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
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;
import waleta_system.Class.ExportToExcel;
import waleta_system.MainForm;

public class JPanel_BahanJadiMasuk extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();

    public JPanel_BahanJadiMasuk() {
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
                        label_kode_asal.setText(Table_GudangBahanJadi.getValueAt(i, 0).toString());
                        refreshTable_HasilGrading();
                        if (table_hasil_grading.getRowCount() == 0) {
                            button_add_grading.setEnabled(true);
                            button_delete_lp.setEnabled(true);
                            button_kembali_qc.setEnabled(true);
                            button_edit_grading.setEnabled(false);
                            button_delete_grading.setEnabled(false);
                        } else {
                            button_add_grading.setEnabled(false);
                            button_delete_lp.setEnabled(false);
                            button_kembali_qc.setEnabled(false);
                            button_edit_grading.setEnabled(true);
                            if (Table_GudangBahanJadi.getValueAt(i, 16) == null) {
                                button_delete_grading.setEnabled(true);
                            } else {
                                button_delete_grading.setEnabled(false);
                            }
                        }

                    }
                }
            }
        });
    }

    public void refreshTable() {
        try {
            float total_gram_basah_lp = 0;
            float total_kpg = 0, total_gram = 0;
            float total_kpg_grading = 0, total_gram_grading = 0;
            DefaultTableModel model = (DefaultTableModel) Table_GudangBahanJadi.getModel();
            model.setRowCount(0);
            String filter_status = "";
            switch (ComboBox_filter_status.getSelectedIndex()) {
                case 1:
                    filter_status = " AND `tanggal_grading` IS NULL ";
                    break;
                case 2:
                    filter_status = " AND `tanggal_grading` IS NOT NULL ";
                    break;
                case 3:
                    filter_status = " AND `kode_tutupan` IS NULL ";
                    break;
                case 4:
                    filter_status = " AND `kode_tutupan` IS NULL AND `tanggal_grading` IS NOT NULL ";
                    break;
                case 5:
                    filter_status = " AND `kode_tutupan` IS NOT NULL ";
                    break;
                default:
                    break;
            }
            String filter_tanggal = "";
            if (Date_Filter1.getDate() != null && Date_Filter2.getDate() != null) {
                if (ComboBox_filter_tanggal.getSelectedIndex() == 0) {
                    filter_tanggal = "AND `tanggal_masuk` BETWEEN '" + dateFormat.format(Date_Filter1.getDate()) + "' AND '" + dateFormat.format(Date_Filter2.getDate()) + "' ";
                } else {
                    filter_tanggal = "AND `tanggal_grading` BETWEEN '" + dateFormat.format(Date_Filter1.getDate()) + "' AND '" + dateFormat.format(Date_Filter2.getDate()) + "' ";
                }
            }
            sql = "SELECT `tb_bahan_jadi_masuk`.`kode_asal`, `tb_laporan_produksi`.`no_kartu_waleta`, `tb_bahan_baku_masuk`.`no_registrasi`, `nama_rumah_burung`, `ruangan`, `memo_lp`, `berat_basah`, `tanggal_masuk`, `diterima_oleh`, `pekerja_grading`, `tb_bahan_jadi_masuk`.`keping`, `berat`, `tanggal_grading`, `kode_tutupan`, "
                    + "`sesekan`, `hancuran`, `rontokan`, `bonggol`, `serabut`, "
                    + "SUM(`tb_grading_bahan_jadi`.`gram`) AS 'total_gram_grading', SUM(`tb_grading_bahan_jadi`.`keping`) AS 'total_kpg_grading', `tb_lab_laporan_produksi`.`status_akhir` "
                    + "FROM `tb_bahan_jadi_masuk` "
                    + "LEFT JOIN `tb_grading_bahan_jadi` ON `tb_bahan_jadi_masuk`.`kode_asal` = `tb_grading_bahan_jadi`.`kode_asal_bahan_jadi`"
                    + "LEFT JOIN `tb_finishing_2` ON `tb_bahan_jadi_masuk`.`kode_asal` = `tb_finishing_2`.`no_laporan_produksi`"
                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_bahan_jadi_masuk`.`kode_asal` = `tb_laporan_produksi`.`no_laporan_produksi`"
                    + "LEFT JOIN `tb_bahan_baku_masuk` ON `tb_laporan_produksi`.`no_kartu_waleta` = `tb_bahan_baku_masuk`.`no_kartu_waleta`"
                    + "LEFT JOIN `tb_rumah_burung` ON `tb_bahan_baku_masuk`.`no_registrasi` = `tb_rumah_burung`.`no_registrasi`"
                    + "LEFT JOIN `tb_lab_laporan_produksi` ON `tb_bahan_jadi_masuk`.`kode_asal` = `tb_lab_laporan_produksi`.`no_laporan_produksi`"
                    + "WHERE `tb_bahan_jadi_masuk`.`kode_asal` LIKE '%" + txt_search_kodeAsal.getText() + "%' "
                    + filter_status
                    + filter_tanggal
                    + "GROUP BY `tb_bahan_jadi_masuk`.`kode_asal` ORDER BY `tanggal_masuk` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[30];
            while (rs.next()) {
                row[0] = rs.getString("kode_asal");
                row[1] = rs.getString("no_kartu_waleta");
                row[2] = rs.getString("no_registrasi") + " - " + rs.getString("nama_rumah_burung");
                row[3] = rs.getString("ruangan");
                row[4] = rs.getString("memo_lp");
                row[5] = rs.getDate("tanggal_masuk");
                row[6] = rs.getString("diterima_oleh");
                row[7] = rs.getString("pekerja_grading");
                row[8] = rs.getInt("keping");
                row[9] = rs.getFloat("berat");
                row[10] = rs.getInt("sesekan");
                row[11] = rs.getInt("hancuran");
                row[12] = rs.getInt("rontokan");
                row[13] = rs.getInt("bonggol");
                row[14] = rs.getInt("serabut");
                row[15] = rs.getDate("tanggal_grading");
                row[16] = rs.getString("kode_tutupan");
                row[17] = rs.getInt("total_kpg_grading");
                row[18] = rs.getFloat("total_gram_grading");
                row[19] = rs.getString("status_akhir");
                row[20] = rs.getFloat("berat_basah");
                model.addRow(row);
                total_kpg = total_kpg + rs.getInt("keping");
                total_gram = total_gram + rs.getFloat("berat");
                total_kpg_grading = total_kpg_grading + rs.getInt("total_kpg_grading");
                total_gram_grading = total_gram_grading + rs.getFloat("total_gram_grading");
                total_gram_basah_lp = total_gram_basah_lp + rs.getFloat("berat_basah");
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_GudangBahanJadi);
            int rowData = Table_GudangBahanJadi.getRowCount();
            label_total_data.setText(Integer.toString(rowData));
            label_total_keping.setText(decimalFormat.format(total_kpg) + " Kpg");
            label_total_berat.setText(decimalFormat.format(total_gram) + " Gram");
            label_total_keping_grading.setText(decimalFormat.format(total_kpg_grading) + " Kpg");
            label_total_berat_grading.setText(decimalFormat.format(total_gram_grading) + " Gram");
            label_total_berat_basah_lp.setText(decimalFormat.format(total_gram_basah_lp) + " Gram");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_BahanJadiMasuk.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_HasilGrading() {
        try {
            int total_kpg = 0;
            float total_gram = 0;
            DefaultTableModel model = (DefaultTableModel) table_hasil_grading.getModel();
            model.setRowCount(0);
            sql = "SELECT `kode_asal_bahan_jadi`, `tb_grade_bahan_jadi`.`kode_grade`, `keping`, `gram` FROM `tb_grading_bahan_jadi` LEFT JOIN `tb_grade_bahan_jadi` ON `tb_grading_bahan_jadi`.`grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode` "
                    + "WHERE `kode_asal_bahan_jadi` = '" + label_kode_asal.getText() + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] baris = new Object[3];
            while (rs.next()) {
                baris[0] = rs.getString("kode_grade");
                baris[1] = rs.getInt("keping");
                baris[2] = rs.getInt("gram");
                model.addRow(baris);
                total_kpg = total_kpg + rs.getInt("keping");
                total_gram = total_gram + rs.getInt("gram");
            }
            ColumnsAutoSizer.sizeColumnsToFit(table_hasil_grading);

            int total_data = table_hasil_grading.getRowCount();
            label_total_hasil_grading.setText(Integer.toString(total_data));
            label_total_kpg_grading.setText(Integer.toString(total_kpg));
            label_total_gram_grading.setText(Float.toString(total_gram));
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_BahanJadiMasuk.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void executeSQLQuery(String query, String message) {
        Utility.db.getConnection();
        try {
            Utility.db.getConnection().createStatement();
            if ((Utility.db.getStatement().executeUpdate(query)) > 0) {
                JOptionPane.showMessageDialog(this, "data " + message + " Successfully");
            } else {
                JOptionPane.showMessageDialog(this, "data not " + message);
            }
        } catch (SQLException | HeadlessException e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, e);
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
        label_total_keping = new javax.swing.JLabel();
        label_total_berat = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        button_export = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        table_hasil_grading = new javax.swing.JTable();
        jLabel11 = new javax.swing.JLabel();
        label_total_hasil_grading = new javax.swing.JLabel();
        label_total_kpg_grading = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        label_total_gram_grading = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        label_kode_asal = new javax.swing.JLabel();
        button_delete_grading = new javax.swing.JButton();
        JLabel1 = new javax.swing.JLabel();
        button_add_grading = new javax.swing.JButton();
        button_edit_grading = new javax.swing.JButton();
        button_delete_lp = new javax.swing.JButton();
        button_terima_bj = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        ComboBox_filter_status = new javax.swing.JComboBox<>();
        ComboBox_filter_tanggal = new javax.swing.JComboBox<>();
        button_kembali_qc = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        label_total_berat_grading = new javax.swing.JLabel();
        label_total_keping_grading = new javax.swing.JLabel();
        label_total_berat_basah_lp = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Penerimaan & Grading Barang Jadi", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("Kode Asal :");

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
                "Kode Asal", "No Kartu", "RSB", "Ruangan", "Memo LP", "Tgl Masuk", "Diterima Oleh", "Pekerja", "Kpg", "Berat", "Sesekan", "Hancuran", "Ront", "Bonggol", "Serabut", "Tgl Grading", "No Tutupan", "Kpg Grading", "Gram Grading", "Status QC", "Berat Angin LP"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Object.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.String.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
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
        jLabel5.setText("Total Keping :");

        label_total_keping.setBackground(new java.awt.Color(255, 255, 255));
        label_total_keping.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_keping.setText("000000 Kpg");

        label_total_berat.setBackground(new java.awt.Color(255, 255, 255));
        label_total_berat.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_berat.setText("000000 Gram");

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel8.setText("Total Berat :");

        button_export.setBackground(new java.awt.Color(255, 255, 255));
        button_export.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export.setText("Export To Excel");
        button_export.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_exportActionPerformed(evt);
            }
        });

        table_hasil_grading.setAutoCreateRowSorter(true);
        table_hasil_grading.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_hasil_grading.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(table_hasil_grading);

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel11.setText("Total Data :");

        label_total_hasil_grading.setBackground(new java.awt.Color(255, 255, 255));
        label_total_hasil_grading.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_hasil_grading.setText("0");

        label_total_kpg_grading.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kpg_grading.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_kpg_grading.setText("0");

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel12.setText("Kpg");

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel13.setText("Grams");

        label_total_gram_grading.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_grading.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_gram_grading.setText("0");

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel14.setText("Total Berat :");

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel15.setText("Total Keping :");

        label_kode_asal.setBackground(new java.awt.Color(255, 255, 255));
        label_kode_asal.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_kode_asal.setText("No. Laporan Produksi");

        button_delete_grading.setBackground(new java.awt.Color(255, 255, 255));
        button_delete_grading.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_delete_grading.setText("Delete");
        button_delete_grading.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_delete_gradingActionPerformed(evt);
            }
        });

        JLabel1.setBackground(new java.awt.Color(255, 255, 255));
        JLabel1.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        JLabel1.setText("Tabel hasil Grading");

        button_add_grading.setBackground(new java.awt.Color(255, 255, 255));
        button_add_grading.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_add_grading.setText("Input Data Grading");
        button_add_grading.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_add_gradingActionPerformed(evt);
            }
        });

        button_edit_grading.setBackground(new java.awt.Color(255, 255, 255));
        button_edit_grading.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_edit_grading.setText("Edit");
        button_edit_grading.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_edit_gradingActionPerformed(evt);
            }
        });

        button_delete_lp.setBackground(new java.awt.Color(255, 255, 255));
        button_delete_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_delete_lp.setText("Delete");
        button_delete_lp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_delete_lpActionPerformed(evt);
            }
        });

        button_terima_bj.setBackground(new java.awt.Color(255, 255, 255));
        button_terima_bj.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_terima_bj.setText("Terima BJ");
        button_terima_bj.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_terima_bjActionPerformed(evt);
            }
        });

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel4.setText("Status :");

        ComboBox_filter_status.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_filter_status.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Belum Grading", "Sudah Grading", "Belum Tutupan", "Sudah Grading, Belum Tutupan", "Sudah Tutupan" }));

        ComboBox_filter_tanggal.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_filter_tanggal.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tanggal Masuk", "Tanggal Grading" }));

        button_kembali_qc.setBackground(new java.awt.Color(255, 255, 255));
        button_kembali_qc.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_kembali_qc.setText("Kembalikan ke QC");
        button_kembali_qc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_kembali_qcActionPerformed(evt);
            }
        });

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel6.setText("Total Keping Grading :");

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel18.setText("Total Berat Grading :");

        label_total_berat_grading.setBackground(new java.awt.Color(255, 255, 255));
        label_total_berat_grading.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_berat_grading.setText("000000 Gram");

        label_total_keping_grading.setBackground(new java.awt.Color(255, 255, 255));
        label_total_keping_grading.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_keping_grading.setText("000000 Kpg");

        label_total_berat_basah_lp.setBackground(new java.awt.Color(255, 255, 255));
        label_total_berat_basah_lp.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_berat_basah_lp.setText("000000 Gram");

        jLabel19.setBackground(new java.awt.Color(255, 255, 255));
        jLabel19.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel19.setText("Total Berat Basah LP :");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_keping, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_berat, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_keping_grading, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_berat_grading, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_berat_basah_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_kodeAsal, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_filter_tanggal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_Filter1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_Filter2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_filter_status, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_search))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_data))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(button_terima_bj)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_delete_lp)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_kembali_qc)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_export)))
                        .addGap(0, 196, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(JLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_kode_asal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_kpg_grading)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel12))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_gram_grading)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel13))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_hasil_grading))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(button_add_grading)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_edit_grading)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_delete_grading)))
                        .addGap(0, 55, Short.MAX_VALUE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(ComboBox_filter_status, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_search, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_search_kodeAsal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(ComboBox_filter_tanggal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(Date_Filter1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_Filter2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_export, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_delete_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_terima_bj, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_kode_asal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(JLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_kembali_qc, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 524, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(button_delete_grading, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_add_grading, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_edit_grading, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(6, 6, 6)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_hasil_grading, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_kpg_grading, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_gram_grading, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_data, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_total_keping, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_total_berat, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_total_keping_grading, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(label_total_berat_basah_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(label_total_berat_grading, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))))))
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

    private void button_delete_lpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_delete_lpActionPerformed
        // TODO add your handling code here:
        try {
            int j = Table_GudangBahanJadi.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih data yang ingin di hapus !");
            } else {
                int dialogResult = JOptionPane.showConfirmDialog(this, "Yakin hapus data ini?", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    // delete code here
                    String Query = "DELETE FROM `tb_bahan_jadi_masuk` WHERE `kode_asal` = '" + Table_GudangBahanJadi.getValueAt(j, 0).toString() + "'";
                    executeSQLQuery(Query, "deleted !");
                    refreshTable();
                }
            }
        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(this, e);
        }
    }//GEN-LAST:event_button_delete_lpActionPerformed

    private void button_exportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_exportActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Table_GudangBahanJadi.getModel();
        ExportToExcel.writeToExcel(model, jPanel1);
    }//GEN-LAST:event_button_exportActionPerformed

    private void button_delete_gradingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_delete_gradingActionPerformed
        // TODO add your handling code here:
        try {
            int i = Table_GudangBahanJadi.getSelectedRow();
            if (i > -1) {
                int dialogResult = JOptionPane.showConfirmDialog(this, "Apakah anda yakin akan menghapus data grading untuk LP " + Table_GudangBahanJadi.getValueAt(i, 0) + "?", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    String Query = "DELETE FROM `tb_grading_bahan_jadi` WHERE `kode_asal_bahan_jadi` = '" + label_kode_asal.getText() + "'";
                    executeSQLQuery(Query, "deleted !");
                    String update = "UPDATE `tb_bahan_jadi_masuk` SET `tanggal_grading`= NULL, `pekerja_grading` = '-' WHERE `kode_asal` = '" + label_kode_asal.getText() + "'";
                    Utility.db.getConnection().createStatement();
                    Utility.db.getStatement().executeUpdate(update);
                    refreshTable_HasilGrading();
                    refreshTable();
                }
            }
        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, e);
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_BahanJadiMasuk.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_delete_gradingActionPerformed

    private void button_add_gradingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_add_gradingActionPerformed
        // TODO add your handling code here:
        if (Table_GudangBahanJadi.getSelectedRow() > -1) {
            try {
                Date tgl_masuk = dateFormat.parse(Table_GudangBahanJadi.getValueAt(Table_GudangBahanJadi.getSelectedRow(), 5).toString());
                int keping_awal = (int) Table_GudangBahanJadi.getValueAt(Table_GudangBahanJadi.getSelectedRow(), 8);
                JDialog_GradingBahanJadi dialog = new JDialog_GradingBahanJadi(new javax.swing.JFrame(), true, label_kode_asal.getText(), keping_awal, "insert", tgl_masuk);
                dialog.pack();
                dialog.setLocationRelativeTo(this);
                dialog.setVisible(true);
                dialog.setEnabled(true);
                refreshTable_HasilGrading();
                refreshTable();
            } catch (ParseException ex) {
                Logger.getLogger(JPanel_BahanJadiMasuk.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_button_add_gradingActionPerformed

    private void button_edit_gradingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_edit_gradingActionPerformed
        // TODO add your handling code here:
        try {
            int hari = 0, count = 0;
            int x = Table_GudangBahanJadi.getSelectedRow();
            Date tgl_grading = dateFormat.parse(Table_GudangBahanJadi.getValueAt(x, 15).toString());
            while (hari < 2) {
                count++;
                sql = "SELECT `tanggal_libur` FROM `tb_libur` WHERE `tanggal_libur` = '" + dateFormat.format(new Date(tgl_grading.getTime() + (count * 24 * 60 * 60 * 1000))) + "'";
                rs = Utility.db.getStatement().executeQuery(sql);
                if (rs.next()) {
                } else {
                    hari++;
                }
            }
//            if (date.after(new Date(tgl_grading.getTime() + (count * 24 * 60 * 60 * 1000)))) {
//                JOptionPane.showMessageDialog(this, "Maaf, Batas tgl Edit anda : " + new SimpleDateFormat("dd MMMM yyyy").format(new Date(tgl_grading.getTime() + (count * 24 * 60 * 60 * 1000))));
//            } else {
            Date tgl_masuk = dateFormat.parse(Table_GudangBahanJadi.getValueAt(Table_GudangBahanJadi.getSelectedRow(), 5).toString());
            int keping_awal = (int) Table_GudangBahanJadi.getValueAt(x, 8);
            JDialog_GradingBahanJadi dialog = new JDialog_GradingBahanJadi(new javax.swing.JFrame(), true, label_kode_asal.getText(), keping_awal, "edit", tgl_masuk);
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            dialog.setEnabled(true);
            refreshTable_HasilGrading();
            refreshTable();
//            }
        } catch (NumberFormatException | ParseException | SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_BahanJadiMasuk.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_edit_gradingActionPerformed

    private void button_terima_bjActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_terima_bjActionPerformed
        // TODO add your handling code here:
        JDialog_Terima_BahanJadi dialog = new JDialog_Terima_BahanJadi(new javax.swing.JFrame(), true);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);
        refreshTable();
    }//GEN-LAST:event_button_terima_bjActionPerformed

    private void button_kembali_qcActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_kembali_qcActionPerformed
        // TODO add your handling code here:
        try {
            int j = Table_GudangBahanJadi.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih data yang ingin di hapus !");
            } else {
                int dialogResult = JOptionPane.showConfirmDialog(this, "kembali ke QC otomatis akan menghapus data LP di GBJ, apakah yakin?", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    // delete code here
                    String Query = "DELETE FROM `tb_bahan_jadi_masuk` WHERE `kode_asal` = '" + Table_GudangBahanJadi.getValueAt(j, 0).toString() + "'";
                    Utility.db.getConnection().createStatement();
                    if ((Utility.db.getStatement().executeUpdate(Query)) > 0) {
                        Query = "UPDATE `tb_lab_laporan_produksi` SET `tgl_selesai`=NULL, `status_akhir`=NULL WHERE `no_laporan_produksi` = '" + Table_GudangBahanJadi.getValueAt(j, 0).toString() + "'";
                        Utility.db.getConnection().createStatement();
                        if ((Utility.db.getStatement().executeUpdate(Query)) > 0) {
                            JOptionPane.showMessageDialog(this, "LP berhasil di kembalikan ke QC !");
                        } else {
                            JOptionPane.showMessageDialog(this, "QC Failed");
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed !");
                    }
                    refreshTable();
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_BahanJadiMasuk.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_kembali_qcActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_filter_status;
    private javax.swing.JComboBox<String> ComboBox_filter_tanggal;
    private com.toedter.calendar.JDateChooser Date_Filter1;
    private com.toedter.calendar.JDateChooser Date_Filter2;
    private javax.swing.JLabel JLabel1;
    private javax.swing.JTable Table_GudangBahanJadi;
    public javax.swing.JButton button_add_grading;
    public javax.swing.JButton button_delete_grading;
    public javax.swing.JButton button_delete_lp;
    public javax.swing.JButton button_edit_grading;
    private javax.swing.JButton button_export;
    public javax.swing.JButton button_kembali_qc;
    private javax.swing.JButton button_search;
    public javax.swing.JButton button_terima_bj;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel label_kode_asal;
    private javax.swing.JLabel label_total_berat;
    private javax.swing.JLabel label_total_berat_basah_lp;
    private javax.swing.JLabel label_total_berat_grading;
    private javax.swing.JLabel label_total_data;
    private javax.swing.JLabel label_total_gram_grading;
    private javax.swing.JLabel label_total_hasil_grading;
    private javax.swing.JLabel label_total_keping;
    private javax.swing.JLabel label_total_keping_grading;
    private javax.swing.JLabel label_total_kpg_grading;
    public static javax.swing.JTable table_hasil_grading;
    private javax.swing.JTextField txt_search_kodeAsal;
    // End of variables declaration//GEN-END:variables
}
