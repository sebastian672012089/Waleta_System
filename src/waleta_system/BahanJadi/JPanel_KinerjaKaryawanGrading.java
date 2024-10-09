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
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;
import waleta_system.Class.ExportToExcel;

public class JPanel_KinerjaKaryawanGrading extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();

    public JPanel_KinerjaKaryawanGrading() {
        initComponents();
    }

    public void init() {
        refreshTable_kinerja_grading();
        Table_DataKinerjaGrading.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && Table_DataKinerjaGrading.getSelectedRow() != -1) {
                    int i = Table_DataKinerjaGrading.getSelectedRow();
                    if (i > -1) {
                        refreshTable_LP_Dikerjakan(Table_DataKinerjaGrading.getValueAt(i, 1).toString());
                    }
                }
            }
        });
        Table_DataKinerjaRepacking.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && Table_DataKinerjaRepacking.getSelectedRow() != -1) {
                    int i = Table_DataKinerjaRepacking.getSelectedRow();
                    if (i > -1) {
                        refreshTable_BoxRepacking_Dikerjakan(Table_DataKinerjaRepacking.getValueAt(i, 0).toString());
                    }
                }
            }
        });
    }

    public void refreshTable_kinerja_grading() {
        try {
            if (Date_Filter_grading1.getDate() == null || Date_Filter_grading2.getDate() == null) {
                JOptionPane.showMessageDialog(this, "Range tanggal harus dipilih !");
            } else {
                double total_lp = 0, total_kpg = 0, total_gram = 0;
                DefaultTableModel model = (DefaultTableModel) Table_DataKinerjaGrading.getModel();
                model.setRowCount(0);
                sql = "SELECT `tb_bahan_jadi_masuk`.`pekerja_grading`,\n"
                        + "(SELECT `id_pegawai` FROM `tb_karyawan` WHERE `nama_pegawai` = `tb_bahan_jadi_masuk`.`pekerja_grading` ORDER BY `tanggal_masuk` DESC LIMIT 1) AS `id_pegawai`, \n"
                        + "(SELECT `nama_bagian` FROM `tb_karyawan` LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian` WHERE `nama_pegawai` = `tb_bahan_jadi_masuk`.`pekerja_grading` ORDER BY `tanggal_masuk` DESC LIMIT 1) AS `kode_bagian`,\n"
                        + "(SELECT `status` FROM `tb_karyawan` WHERE `nama_pegawai` = `tb_bahan_jadi_masuk`.`pekerja_grading` ORDER BY `tanggal_masuk` DESC LIMIT 1) AS `status`, \n"
                        + "(SELECT `tanggal_masuk` FROM `tb_karyawan` WHERE `nama_pegawai` = `tb_bahan_jadi_masuk`.`pekerja_grading` ORDER BY `tanggal_masuk` DESC LIMIT 1) AS `tanggal_masuk`, \n"
                        + "COUNT(DISTINCT(GRADING.`kode_asal_bahan_jadi`)) AS 'jumlah_lp',\n"
                        + "SUM(IF(`tb_grade_bahan_jadi`.`bentuk_grade` = 'Utuh', GRADING.`keping`, 0)) AS 'kpgUtuh',\n"
                        + "SUM(IF(`tb_grade_bahan_jadi`.`bentuk_grade` = 'Utuh', GRADING.`gram`, 0)) AS 'gramUtuh',\n"
                        + "SUM(IF(`tb_grade_bahan_jadi`.`bentuk_grade` = 'Flat', GRADING.`keping`, 0)) AS 'kpgFlat',\n"
                        + "SUM(IF(`tb_grade_bahan_jadi`.`bentuk_grade` = 'Flat', GRADING.`gram`, 0)) AS 'gramFlat',\n"
                        + "SUM(IF(`tb_grade_bahan_jadi`.`bentuk_grade` = 'Pecah', GRADING.`keping`, 0)) AS 'kpgPecah',\n"
                        + "SUM(IF(`tb_grade_bahan_jadi`.`bentuk_grade` = 'Pecah', GRADING.`gram`, 0)) AS 'gramPecah',\n"
                        + "SUM(IF(`tb_grade_bahan_jadi`.`bentuk_grade` = 'Jidun', GRADING.`keping`, 0)) AS 'kpgJidun',\n"
                        + "SUM(IF(`tb_grade_bahan_jadi`.`bentuk_grade` = 'Jidun', GRADING.`gram`, 0)) AS 'gramJidun',\n"
                        + "SUM(IF(`tb_grade_bahan_jadi`.`bentuk_grade` = 'By Product', GRADING.`keping`, 0)) AS 'kpgByProduct',\n"
                        + "SUM(IF(`tb_grade_bahan_jadi`.`bentuk_grade` = 'By Product', GRADING.`gram`, 0)) AS 'gramByProduct',\n"
                        + "SUM(IF(`tb_grade_bahan_jadi`.`bentuk_grade` NOT IN ('Utuh', 'Flat', 'Pecah', 'Jidun', 'By Product'), GRADING.`keping`, 0)) AS 'kpgLain',\n"
                        + "SUM(IF(`tb_grade_bahan_jadi`.`bentuk_grade` NOT IN ('Utuh', 'Flat', 'Pecah', 'Jidun', 'By Product'), GRADING.`gram`, 0)) AS 'gramLain',\n"
                        + "`avg_lp`, `avg_kpg`, `avg_gram` \n"
                        + "FROM `tb_grading_bahan_jadi` GRADING\n"
                        + "LEFT JOIN `tb_grade_bahan_jadi` ON GRADING.`grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode`\n"
                        + "LEFT JOIN `tb_bahan_jadi_masuk` ON GRADING.`kode_asal_bahan_jadi` = `tb_bahan_jadi_masuk`.`kode_asal`\n"
                        + "LEFT JOIN (SELECT `pekerja_grading`, AVG(`jumlah_lp`) AS 'avg_lp', AVG(`jumlah_kpg`) AS 'avg_kpg', AVG(`jumlah_gram`) AS 'avg_gram' \n"
                        + "FROM (SELECT `pekerja_grading`, COUNT(`kode_asal`) AS 'jumlah_lp', SUM(`keping`) AS 'jumlah_kpg', SUM(`berat`) AS 'jumlah_gram', `tanggal_grading` "
                        + "FROM `tb_bahan_jadi_masuk` WHERE `tb_bahan_jadi_masuk`.`tanggal_grading` BETWEEN '" + dateFormat.format(Date_Filter_grading1.getDate()) + "' AND '" + dateFormat.format(Date_Filter_grading2.getDate()) + "'\n"
                        + "GROUP BY `pekerja_grading`, `tanggal_grading`) count_avg WHERE `pekerja_grading` LIKE '%" + txt_search_nama_grading.getText() + "%' GROUP BY `pekerja_grading`) avg "
                        + "ON `tb_bahan_jadi_masuk`.`pekerja_grading` = avg.`pekerja_grading` "
                        + "WHERE `tb_bahan_jadi_masuk`.`pekerja_grading` LIKE '%" + txt_search_nama_grading.getText() + "%' \n"
                        + "AND `tb_bahan_jadi_masuk`.`tanggal_grading` BETWEEN '" + dateFormat.format(Date_Filter_grading1.getDate()) + "' AND '" + dateFormat.format(Date_Filter_grading2.getDate()) + "' "
                        + "GROUP BY `tb_bahan_jadi_masuk`.`pekerja_grading`";
//                System.out.println(sql);
                rs = Utility.db.getStatement().executeQuery(sql);
                Object[] row = new Object[25];
                while (rs.next()) {
                    double jumlah_kpg = rs.getFloat("kpgUtuh") + rs.getFloat("kpgFlat") + rs.getFloat("kpgPecah") + rs.getFloat("kpgJidun") + rs.getFloat("kpgByProduct") + rs.getFloat("kpgLain");
                    double jumlah_gram = rs.getFloat("gramUtuh") + rs.getFloat("gramFlat") + rs.getFloat("gramPecah") + rs.getFloat("gramJidun") + rs.getFloat("gramByProduct") + rs.getFloat("gramLain");
                    row[0] = rs.getString("id_pegawai");
                    row[1] = rs.getString("pekerja_grading");
                    row[2] = rs.getString("kode_bagian");
                    row[3] = rs.getString("status");
                    row[4] = rs.getString("tanggal_masuk");
                    row[5] = rs.getFloat("jumlah_lp");
                    row[6] = jumlah_kpg;
                    row[7] = jumlah_gram;
                    row[8] = rs.getFloat("kpgUtuh");
                    row[9] = rs.getFloat("gramUtuh");
                    row[10] = rs.getFloat("kpgFlat");
                    row[11] = rs.getFloat("gramFlat");
                    row[12] = rs.getFloat("kpgPecah");
                    row[13] = rs.getFloat("gramPecah");
                    row[14] = rs.getFloat("kpgJidun");
                    row[15] = rs.getFloat("gramJidun");
                    row[16] = rs.getFloat("kpgByProduct");
                    row[17] = rs.getFloat("gramByProduct");
                    row[18] = rs.getFloat("kpgLain");
                    row[19] = rs.getFloat("gramLain");
                    row[20] = Math.round(rs.getFloat("avg_lp") * 10f) / 10f;
                    row[21] = Math.round(rs.getFloat("avg_kpg") * 10f) / 10f;
                    row[22] = Math.round(rs.getFloat("avg_gram") * 10f) / 10f;
                    model.addRow(row);
                    total_lp = total_lp + rs.getFloat("jumlah_lp");
                    total_kpg = total_kpg + jumlah_kpg;
                    total_gram = total_gram + jumlah_gram;
                }
                ColumnsAutoSizer.sizeColumnsToFit(Table_DataKinerjaGrading);
                int rowData = Table_DataKinerjaGrading.getRowCount();
                label_total_pekerja_grading.setText(Integer.toString(rowData));
                label_total_keping_grading.setText(decimalFormat.format(total_kpg));
                label_total_gram_grading.setText(decimalFormat.format(total_gram));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_KinerjaKaryawanGrading.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_LP_Dikerjakan(String pekerjaGrading) {
        try {
            if (Date_Filter_grading1.getDate() == null || Date_Filter_grading2.getDate() == null) {
                JOptionPane.showMessageDialog(this, "Range tanggal harus dipilih !");
            } else {
                float total_kpg = 0;
                float total_gram = 0;
                DefaultTableModel model = (DefaultTableModel) table_lp_dikerjakan.getModel();
                model.setRowCount(0);
                sql = "SELECT `kode_asal`, `keping`, `berat`, `tanggal_grading` FROM `tb_bahan_jadi_masuk` "
                        + "WHERE `pekerja_grading` = '" + pekerjaGrading + "'"
                        + "AND `tb_bahan_jadi_masuk`.`tanggal_grading` BETWEEN '" + dateFormat.format(Date_Filter_grading1.getDate()) + "' AND '" + dateFormat.format(Date_Filter_grading2.getDate()) + "' ";
                rs = Utility.db.getStatement().executeQuery(sql);
                Object[] baris = new Object[5];
                while (rs.next()) {
                    baris[0] = rs.getString("kode_asal");
                    baris[1] = rs.getFloat("keping");
                    baris[2] = rs.getFloat("berat");
                    baris[3] = rs.getDate("tanggal_grading");
                    model.addRow(baris);
                    total_kpg = total_kpg + rs.getFloat("keping");
                    total_gram = total_gram + rs.getFloat("berat");
                }
                ColumnsAutoSizer.sizeColumnsToFit(table_lp_dikerjakan);

                int total_data = table_lp_dikerjakan.getRowCount();
                label_total_detail_lp.setText(decimalFormat.format(total_data));
                label_total_kpg_lp.setText(decimalFormat.format(total_kpg));
                label_total_gram_lp.setText(decimalFormat.format(total_gram));
            }
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_KinerjaKaryawanGrading.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_kinerja_repacking() {
        try {
            if (Date_Filter_repacking1.getDate() == null || Date_Filter_repacking2.getDate() == null) {
                JOptionPane.showMessageDialog(this, "Range tanggal harus dipilih !");
            } else {
                double total_box = 0, total_kpg = 0, total_gram = 0;
                DefaultTableModel model = (DefaultTableModel) Table_DataKinerjaRepacking.getModel();
                model.setRowCount(0);
                sql = "SELECT `pekerja_repacking`, `tb_karyawan`.`nama_pegawai`, `tb_bagian`.`nama_bagian`, "
                        + "COUNT(`no_box`) AS 'jumlah_box', SUM(`keping`) AS 'jumlah_keping', SUM(`berat`) AS 'jumlah_gram', COUNT(DISTINCT(`tanggal_repacking`)) AS 'jumlah_hari'\n"
                        + "FROM `tb_box_bahan_jadi` \n"
                        + "LEFT JOIN `tb_karyawan` ON `tb_box_bahan_jadi`.`pekerja_repacking` = `tb_karyawan`.`id_pegawai`\n"
                        + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`\n"
                        + "WHERE `tanggal_repacking` BETWEEN '" + dateFormat.format(Date_Filter_repacking1.getDate()) + "' AND '" + dateFormat.format(Date_Filter_repacking2.getDate()) + "'\n"
                        + "GROUP BY `pekerja_repacking`";
//                System.out.println(sql);
                rs = Utility.db.getStatement().executeQuery(sql);
                Object[] row = new Object[25];
                while (rs.next()) {
                    row[0] = rs.getString("pekerja_repacking");
                    row[1] = rs.getString("nama_pegawai");
                    row[2] = rs.getString("nama_bagian");
                    row[3] = rs.getFloat("jumlah_box");
                    row[4] = rs.getFloat("jumlah_keping");
                    row[5] = rs.getFloat("jumlah_gram");
                    float avg_gram_per_hari = Math.round(rs.getFloat("jumlah_gram") / rs.getFloat("jumlah_hari"));
                    row[6] = avg_gram_per_hari;
                    model.addRow(row);
                    total_box = total_box + rs.getFloat("jumlah_box");
                    total_kpg = total_kpg + rs.getFloat("jumlah_keping");
                    total_gram = total_gram + rs.getFloat("jumlah_gram");
                }
                ColumnsAutoSizer.sizeColumnsToFit(Table_DataKinerjaRepacking);
                int rowData = Table_DataKinerjaRepacking.getRowCount();
                label_total_pekerja_repacking.setText(Integer.toString(rowData));
                label_total_keping_repacking.setText(decimalFormat.format(total_kpg));
                label_total_gram_repacking.setText(decimalFormat.format(total_gram));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_KinerjaKaryawanGrading.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void refreshTable_BoxRepacking_Dikerjakan(String pekerjaRepacking) {
        try {
            if (Date_Filter_repacking1.getDate() == null || Date_Filter_repacking2.getDate() == null) {
                JOptionPane.showMessageDialog(this, "Range tanggal harus dipilih !");
            } else {
                float total_kpg = 0;
                float total_gram = 0;
                DefaultTableModel model = (DefaultTableModel) table_detail_box_repacking.getModel();
                model.setRowCount(0);
                sql = "SELECT `no_box`, `keping`, `berat`, `tanggal_repacking` "
                        + "FROM `tb_box_bahan_jadi` "
                        + "WHERE `pekerja_repacking` = '" + pekerjaRepacking + "'"
                        + "AND `tb_box_bahan_jadi`.`tanggal_repacking` BETWEEN '" + dateFormat.format(Date_Filter_repacking1.getDate()) + "' AND '" + dateFormat.format(Date_Filter_repacking2.getDate()) + "' ";
                rs = Utility.db.getStatement().executeQuery(sql);
                Object[] baris = new Object[5];
                while (rs.next()) {
                    baris[0] = rs.getString("no_box");
                    baris[1] = rs.getFloat("keping");
                    baris[2] = rs.getFloat("berat");
                    baris[3] = rs.getDate("tanggal_repacking");
                    model.addRow(baris);
                    total_kpg = total_kpg + rs.getFloat("keping");
                    total_gram = total_gram + rs.getFloat("berat");
                }
                ColumnsAutoSizer.sizeColumnsToFit(table_detail_box_repacking);

                int total_data = table_detail_box_repacking.getRowCount();
                label_total_detail_box.setText(decimalFormat.format(total_data));
                label_total_kpg_box.setText(decimalFormat.format(total_kpg));
                label_total_gram_box.setText(decimalFormat.format(total_gram));
            }
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_KinerjaKaryawanGrading.class.getName()).log(Level.SEVERE, null, ex);
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
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txt_search_nama_grading = new javax.swing.JTextField();
        button_search_grading = new javax.swing.JButton();
        Date_Filter_grading1 = new com.toedter.calendar.JDateChooser();
        Date_Filter_grading2 = new com.toedter.calendar.JDateChooser();
        jScrollPane1 = new javax.swing.JScrollPane();
        Table_DataKinerjaGrading = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        label_total_pekerja_grading = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        label_total_keping_grading = new javax.swing.JLabel();
        label_total_gram_grading = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        button_export_grading = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        table_lp_dikerjakan = new javax.swing.JTable();
        jLabel11 = new javax.swing.JLabel();
        label_total_detail_lp = new javax.swing.JLabel();
        label_total_kpg_lp = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        label_total_gram_lp = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        JLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        label_total_lp_grading = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        txt_search_nama_repacking = new javax.swing.JTextField();
        button_search_repacking = new javax.swing.JButton();
        Date_Filter_repacking1 = new com.toedter.calendar.JDateChooser();
        Date_Filter_repacking2 = new com.toedter.calendar.JDateChooser();
        jScrollPane3 = new javax.swing.JScrollPane();
        Table_DataKinerjaRepacking = new javax.swing.JTable();
        jLabel16 = new javax.swing.JLabel();
        label_total_pekerja_repacking = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        label_total_keping_repacking = new javax.swing.JLabel();
        label_total_gram_repacking = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        button_export_repacking = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        table_detail_box_repacking = new javax.swing.JTable();
        jLabel21 = new javax.swing.JLabel();
        label_total_detail_box = new javax.swing.JLabel();
        label_total_kpg_box = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        label_total_gram_box = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        JLabel2 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        label_total_box_repacking = new javax.swing.JLabel();

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("Nama Pekerja :");

        txt_search_nama_grading.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_nama_grading.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_nama_gradingKeyPressed(evt);
            }
        });

        button_search_grading.setBackground(new java.awt.Color(255, 255, 255));
        button_search_grading.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search_grading.setText("Search");
        button_search_grading.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_search_gradingActionPerformed(evt);
            }
        });

        Date_Filter_grading1.setBackground(new java.awt.Color(255, 255, 255));
        Date_Filter_grading1.setDate(new Date(new Date().getTime()- (7 * 24 * 60 * 60 * 1000)));
        Date_Filter_grading1.setDateFormatString("dd MMMM yyyy");
        Date_Filter_grading1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Date_Filter_grading2.setBackground(new java.awt.Color(255, 255, 255));
        Date_Filter_grading2.setDate(new Date());
        Date_Filter_grading2.setDateFormatString("dd MMMM yyyy");
        Date_Filter_grading2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Table_DataKinerjaGrading.setAutoCreateRowSorter(true);
        Table_DataKinerjaGrading.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_DataKinerjaGrading.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Karyawan", "Nama", "Bagian", "Status", "Tgl Masuk", "Jumlah LP", "Total Kpg", "Total Gram", "Kpg Utuh", "Gram Utuh", "Kpg FLAT", "Gram FLAT", "Kpg PCH", "Gram PCH", "Kpg JDN", "Gram JDN", "Kpg BP", "Gram BP", "Kpg Lain", "Gram Lain", "LP Daily AVG", "Kpg Daily AVG", "Gram Daily AVG"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
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
        Table_DataKinerjaGrading.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(Table_DataKinerjaGrading);

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("Total Data :");

        label_total_pekerja_grading.setBackground(new java.awt.Color(255, 255, 255));
        label_total_pekerja_grading.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_pekerja_grading.setText("0");

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel5.setText("Total Keping :");

        label_total_keping_grading.setBackground(new java.awt.Color(255, 255, 255));
        label_total_keping_grading.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_keping_grading.setText("0");

        label_total_gram_grading.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_grading.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_gram_grading.setText("0");

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel8.setText("Total Berat :");

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel9.setText("Grams");

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel10.setText("Kpg");

        button_export_grading.setBackground(new java.awt.Color(255, 255, 255));
        button_export_grading.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_grading.setText("Export To Excel");
        button_export_grading.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_gradingActionPerformed(evt);
            }
        });

        table_lp_dikerjakan.setAutoCreateRowSorter(true);
        table_lp_dikerjakan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_lp_dikerjakan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode Asal", "Kpg", "Gram"
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
        table_lp_dikerjakan.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(table_lp_dikerjakan);

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel11.setText("Total Data :");

        label_total_detail_lp.setBackground(new java.awt.Color(255, 255, 255));
        label_total_detail_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_detail_lp.setText("0");

        label_total_kpg_lp.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kpg_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_kpg_lp.setText("0");

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel12.setText("Kpg");

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel13.setText("Grams");

        label_total_gram_lp.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_gram_lp.setText("0");

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel14.setText("Total Berat :");

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel15.setText("Total Keping :");

        JLabel1.setBackground(new java.awt.Color(255, 255, 255));
        JLabel1.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        JLabel1.setText("Detail Barang dikerjakan");

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("Tanggal grading :");

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel4.setText("Note : Pemisahan Grade berdasarkan hasil bentuk grade barang jadi. Grade LAIN adalah gabungan dari bentuk Campuran, Kaki, dan Mess.");

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel6.setText("Total LP :");

        label_total_lp_grading.setBackground(new java.awt.Color(255, 255, 255));
        label_total_lp_grading.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_lp_grading.setText("0");

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
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_pekerja_grading)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_lp_grading)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_keping_grading)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_gram_grading)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel9))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_nama_grading, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_Filter_grading1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_Filter_grading2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_search_grading)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_export_grading))
                            .addComponent(jLabel4))
                        .addGap(0, 319, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 298, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(JLabel1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_kpg_lp)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel12))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gram_lp)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel13))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_detail_lp)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(button_search_grading, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_export_grading, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_search_nama_grading, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(Date_Filter_grading1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_Filter_grading2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_keping_grading, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_gram_grading, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_lp_grading, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_pekerja_grading, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(JLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 517, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_detail_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_kpg_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_gram_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Kinerja Karyawan Grading BJ", jPanel1);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel7.setText("Nama Pekerja :");

        txt_search_nama_repacking.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_nama_repacking.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_nama_repackingKeyPressed(evt);
            }
        });

        button_search_repacking.setBackground(new java.awt.Color(255, 255, 255));
        button_search_repacking.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search_repacking.setText("Search");
        button_search_repacking.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_search_repackingActionPerformed(evt);
            }
        });

        Date_Filter_repacking1.setBackground(new java.awt.Color(255, 255, 255));
        Date_Filter_repacking1.setDate(new Date(new Date().getTime()- (7 * 24 * 60 * 60 * 1000)));
        Date_Filter_repacking1.setDateFormatString("dd MMMM yyyy");
        Date_Filter_repacking1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Date_Filter_repacking2.setBackground(new java.awt.Color(255, 255, 255));
        Date_Filter_repacking2.setDate(new Date());
        Date_Filter_repacking2.setDateFormatString("dd MMMM yyyy");
        Date_Filter_repacking2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Table_DataKinerjaRepacking.setAutoCreateRowSorter(true);
        Table_DataKinerjaRepacking.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_DataKinerjaRepacking.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Karyawan", "Nama", "Bagian", "Jumlah Box", "Total Kpg", "Total Gram", "Rata2 Gr / Hari"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
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
        Table_DataKinerjaRepacking.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(Table_DataKinerjaRepacking);

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel16.setText("Total Data :");

        label_total_pekerja_repacking.setBackground(new java.awt.Color(255, 255, 255));
        label_total_pekerja_repacking.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_pekerja_repacking.setText("0");

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel17.setText("Total Keping :");

        label_total_keping_repacking.setBackground(new java.awt.Color(255, 255, 255));
        label_total_keping_repacking.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_keping_repacking.setText("0");

        label_total_gram_repacking.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_repacking.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_gram_repacking.setText("0");

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel18.setText("Total Berat :");

        jLabel19.setBackground(new java.awt.Color(255, 255, 255));
        jLabel19.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel19.setText("Grams");

        jLabel20.setBackground(new java.awt.Color(255, 255, 255));
        jLabel20.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel20.setText("Kpg");

        button_export_repacking.setBackground(new java.awt.Color(255, 255, 255));
        button_export_repacking.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_repacking.setText("Export To Excel");
        button_export_repacking.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_repackingActionPerformed(evt);
            }
        });

        table_detail_box_repacking.setAutoCreateRowSorter(true);
        table_detail_box_repacking.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_detail_box_repacking.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No Box", "Kpg", "Gram"
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
        table_detail_box_repacking.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(table_detail_box_repacking);

        jLabel21.setBackground(new java.awt.Color(255, 255, 255));
        jLabel21.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel21.setText("Total Data :");

        label_total_detail_box.setBackground(new java.awt.Color(255, 255, 255));
        label_total_detail_box.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_detail_box.setText("0");

        label_total_kpg_box.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kpg_box.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_kpg_box.setText("0");

        jLabel22.setBackground(new java.awt.Color(255, 255, 255));
        jLabel22.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel22.setText("Kpg");

        jLabel23.setBackground(new java.awt.Color(255, 255, 255));
        jLabel23.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel23.setText("Grams");

        label_total_gram_box.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_box.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_gram_box.setText("0");

        jLabel24.setBackground(new java.awt.Color(255, 255, 255));
        jLabel24.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel24.setText("Total Berat :");

        jLabel25.setBackground(new java.awt.Color(255, 255, 255));
        jLabel25.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel25.setText("Total Keping :");

        JLabel2.setBackground(new java.awt.Color(255, 255, 255));
        JLabel2.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        JLabel2.setText("Detail Barang dikerjakan");

        jLabel26.setBackground(new java.awt.Color(255, 255, 255));
        jLabel26.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel26.setText("Tanggal Repacking :");

        jLabel27.setBackground(new java.awt.Color(255, 255, 255));
        jLabel27.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel27.setText("Note : Pemisahan Grade berdasarkan hasil bentuk grade barang jadi. Grade LAIN adalah gabungan dari bentuk Campuran, Kaki, dan Mess.");

        jLabel28.setBackground(new java.awt.Color(255, 255, 255));
        jLabel28.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel28.setText("Total LP :");

        label_total_box_repacking.setBackground(new java.awt.Color(255, 255, 255));
        label_total_box_repacking.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_box_repacking.setText("0");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_pekerja_repacking)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel28)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_box_repacking)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_keping_repacking)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel20)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_gram_repacking)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel19))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_nama_repacking, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel26)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_Filter_repacking1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_Filter_repacking2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_search_repacking)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_export_repacking))
                            .addComponent(jLabel27))
                        .addGap(0, 305, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 298, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(JLabel2)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_kpg_box)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel22))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gram_box)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel23))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_detail_box)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(button_search_repacking, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_export_repacking, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_search_nama_repacking, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(Date_Filter_repacking1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_Filter_repacking2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_keping_repacking, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_gram_repacking, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_box_repacking, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_pekerja_repacking, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(JLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 517, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_detail_box, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_kpg_box, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_gram_box, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Kinerja Karyawan Repacking", jPanel2);

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

    private void button_search_gradingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_gradingActionPerformed
        // TODO add your handling code here:
        refreshTable_kinerja_grading();
    }//GEN-LAST:event_button_search_gradingActionPerformed

    private void txt_search_nama_gradingKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_nama_gradingKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_kinerja_grading();
        }
    }//GEN-LAST:event_txt_search_nama_gradingKeyPressed

    private void button_export_gradingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_gradingActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Table_DataKinerjaGrading.getModel();
        ExportToExcel.writeToExcel(model, jPanel1);
    }//GEN-LAST:event_button_export_gradingActionPerformed

    private void txt_search_nama_repackingKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_nama_repackingKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_kinerja_repacking();
        }
    }//GEN-LAST:event_txt_search_nama_repackingKeyPressed

    private void button_search_repackingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_repackingActionPerformed
        // TODO add your handling code here:
        refreshTable_kinerja_repacking();
    }//GEN-LAST:event_button_search_repackingActionPerformed

    private void button_export_repackingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_repackingActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Table_DataKinerjaRepacking.getModel();
        ExportToExcel.writeToExcel(model, jPanel2);
    }//GEN-LAST:event_button_export_repackingActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser Date_Filter_grading1;
    private com.toedter.calendar.JDateChooser Date_Filter_grading2;
    private com.toedter.calendar.JDateChooser Date_Filter_repacking1;
    private com.toedter.calendar.JDateChooser Date_Filter_repacking2;
    private javax.swing.JLabel JLabel1;
    private javax.swing.JLabel JLabel2;
    private javax.swing.JTable Table_DataKinerjaGrading;
    private javax.swing.JTable Table_DataKinerjaRepacking;
    private javax.swing.JButton button_export_grading;
    private javax.swing.JButton button_export_repacking;
    private javax.swing.JButton button_search_grading;
    private javax.swing.JButton button_search_repacking;
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
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel label_total_box_repacking;
    private javax.swing.JLabel label_total_detail_box;
    private javax.swing.JLabel label_total_detail_lp;
    private javax.swing.JLabel label_total_gram_box;
    private javax.swing.JLabel label_total_gram_grading;
    private javax.swing.JLabel label_total_gram_lp;
    private javax.swing.JLabel label_total_gram_repacking;
    private javax.swing.JLabel label_total_keping_grading;
    private javax.swing.JLabel label_total_keping_repacking;
    private javax.swing.JLabel label_total_kpg_box;
    private javax.swing.JLabel label_total_kpg_lp;
    private javax.swing.JLabel label_total_lp_grading;
    private javax.swing.JLabel label_total_pekerja_grading;
    private javax.swing.JLabel label_total_pekerja_repacking;
    public static javax.swing.JTable table_detail_box_repacking;
    public static javax.swing.JTable table_lp_dikerjakan;
    private javax.swing.JTextField txt_search_nama_grading;
    private javax.swing.JTextField txt_search_nama_repacking;
    // End of variables declaration//GEN-END:variables
}
