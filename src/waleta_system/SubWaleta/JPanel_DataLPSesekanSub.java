package waleta_system.SubWaleta;

import java.awt.event.KeyEvent;
import java.sql.ResultSet;
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
import waleta_system.Class.Utility;

public class JPanel_DataLPSesekanSub extends javax.swing.JPanel {

    String sql = null;
    String sql2 = null;
    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    float kadar_air_bahan_baku = 0;

    public JPanel_DataLPSesekanSub() {
        initComponents();
    }

    public void init() {
        try {
            Utility.db_sub.connect();
            ComboBox_sub_lp_sesekan.removeAllItems();
            ComboBox_sub_lp_sesekan.addItem("All");
            ComboBox_sub.removeAllItems();
            ComboBox_sub.addItem("All");
            sql = "SELECT DISTINCT(`sub`) AS 'sub' FROM `tb_laporan_produksi_sesekan` WHERE 1";
            rs = Utility.db_sub.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_sub_lp_sesekan.addItem(rs.getString("sub"));
                ComboBox_sub.addItem(rs.getString("sub"));
            }
            refreshTable_lp_sesekan();
            Table_LP_Sesekan.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent event) {
                    if (!event.getValueIsAdjusting() && Table_LP_Sesekan.getSelectedRow() != -1) {
                        int i = Table_LP_Sesekan.getSelectedRow();
                        String no_lp_sesekan = Table_LP_Sesekan.getValueAt(i, 0).toString();
                        label_no_lp_detail.setText(no_lp_sesekan);
                        refreshTable_pekerja_sesekan(no_lp_sesekan);
                    }
                }
            });
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_DataLPSesekanSub.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_lp_sesekan() {
        try {
            Utility.db_sub.connect();
            double total_kpg = 0, total_ssk_bersih = 0, total_ssk_kuning = 0, total_ssk_pasir = 0;
            DefaultTableModel model = (DefaultTableModel) Table_LP_Sesekan.getModel();
            model.setRowCount(0);
            String sub = "AND `sub` = '" + ComboBox_sub_lp_sesekan.getSelectedItem().toString() + "' ";
            if ("All".equals(ComboBox_sub_lp_sesekan.getSelectedItem().toString())) {
                sub = "";
            }
            String filter_tanggal_lp = "";
            if (Date_Search_LP_Sesekan1.getDate() != null && Date_Search_LP_Sesekan2.getDate() != null) {
                filter_tanggal_lp = "AND `tanggal_lp` BETWEEN '" + dateFormat.format(Date_Search_LP_Sesekan1.getDate()) + "' AND '" + dateFormat.format(Date_Search_LP_Sesekan2.getDate()) + "' ";
            }
            sql = "SELECT `tb_laporan_produksi_sesekan`.`no_lp_sesekan`, `sub`, `tanggal_lp`, `kode_grade`, `bulu_upah`, `memo`, `keping`, "
                    + "`sesekan_bersih`, `sesekan_kuning`, `sesekan_pasir`, `berat_setelah_cuci`, `nilai_lp`, SUM(`tb_detail_penyesek`.`nilai_sesekan`) AS 'progress', `waktu_terima_lp`, `waktu_setor_lp` "
                    + "FROM `tb_laporan_produksi_sesekan` "
                    + "LEFT JOIN `tb_detail_penyesek` ON `tb_laporan_produksi_sesekan`.`no_lp_sesekan` = `tb_detail_penyesek`.`no_lp_sesekan` "
                    + "WHERE `tb_laporan_produksi_sesekan`.`no_lp_sesekan` LIKE '%" + txt_search_no_lp_sesekan.getText() + "%' "
                    + sub + filter_tanggal_lp
                    + "GROUP BY `tb_laporan_produksi_sesekan`.`no_lp_sesekan` "
                    + "ORDER BY `tb_laporan_produksi_sesekan`.`no_lp_sesekan` DESC";
            rs = Utility.db_sub.getStatement().executeQuery(sql);
            Object[] row = new Object[30];
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
                row[10] = Math.round(rs.getFloat("berat_setelah_cuci") * 100f / rs.getFloat("sesekan_bersih")) / 100f;
                row[11] = rs.getInt("nilai_lp");
                row[12] = rs.getInt("progress");
                row[13] = rs.getTimestamp("waktu_terima_lp");
                row[14] = rs.getTimestamp("waktu_setor_lp");
                total_kpg = total_kpg + rs.getInt("keping");
                total_ssk_bersih = total_ssk_bersih + rs.getInt("sesekan_bersih");
                total_ssk_kuning = total_ssk_kuning + rs.getInt("sesekan_kuning");
                total_ssk_pasir = total_ssk_pasir + rs.getInt("sesekan_pasir");

                String query = "SELECT `tb_laporan_produksi_sesekan`.`no_lp_sesekan`, `tanggal_timbang`, `rendemen_bersih`, `hancuran`, `rontokan_kotor`, `rontokan_kuning`, SUM(`gram_sesekan_lp`) AS 'gram_sesekan_lp' "
                        + "FROM `tb_laporan_produksi_sesekan` "
                        + "LEFT JOIN `tb_laporan_produksi` ON `tb_laporan_produksi_sesekan`.`no_lp_sesekan` = `tb_laporan_produksi`.`no_lp_sesekan` "
                        + "WHERE `tb_laporan_produksi_sesekan`.`no_lp_sesekan` = '" + rs.getString("no_lp_sesekan") + "' ";
                ResultSet rst = Utility.db.getStatement().executeQuery(query);
                if (rst.next()) {
                    row[15] = rst.getDate("tanggal_timbang");
                    row[16] = rst.getFloat("gram_sesekan_lp");
                    row[17] = rst.getFloat("rendemen_bersih");
                    row[18] = rst.getFloat("hancuran");
                    row[19] = rst.getFloat("rontokan_kotor");
                    row[20] = rst.getFloat("rontokan_kuning");
                    float gram_sh = rst.getFloat("gram_sesekan_lp") - (rst.getFloat("rendemen_bersih") + rst.getFloat("hancuran") + rst.getFloat("rontokan_kotor") + rst.getFloat("rontokan_kuning"));
                    row[21] = gram_sh;
                    row[22] = gram_sh / rst.getFloat("gram_sesekan_lp") * 100f;
                }
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_LP_Sesekan);
            int rowData = Table_LP_Sesekan.getRowCount();
            label_total_lp_sesekan.setText(Integer.toString(rowData));
            label_total_keping_LP_sesekan.setText(decimalFormat.format(total_kpg) + " Keping");
            label_total_sesekan_bersih.setText(decimalFormat.format(total_ssk_bersih) + " Gram");
            label_total_sesekan_kuning.setText(decimalFormat.format(total_ssk_kuning) + " Gram");
            label_total_sesekan_pasir.setText(decimalFormat.format(total_ssk_pasir) + " Gram");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_DataLPSesekanSub.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_pekerja_sesekan(String no_lp) {
        try {
            Utility.db_sub.connect();
            float total_nilai_dikerjakan = 0;
            DefaultTableModel model = (DefaultTableModel) tabel_pekerja_sesekan.getModel();
            model.setRowCount(0);
            sql = "SELECT `nomor`, `no_lp_sesekan`, `tb_detail_penyesek`.`id_pegawai`, `tb_karyawan`.`nama_pegawai`, `tanggal_input`, `nilai_sesekan`, `gram_sesekan` \n"
                    + "FROM `tb_detail_penyesek` LEFT JOIN `tb_karyawan` ON `tb_detail_penyesek`.`id_pegawai` = `tb_karyawan`.`id_pegawai`\n"
                    + "WHERE `no_lp_sesekan` = '" + no_lp + "'";
            rs = Utility.db_sub.getStatement().executeQuery(sql);
            Object[] row = new Object[10];
            while (rs.next()) {
                row[0] = rs.getInt("nomor");
                row[1] = rs.getString("id_pegawai");
                row[2] = rs.getString("nama_pegawai");
                row[3] = rs.getDate("tanggal_input");
                row[4] = rs.getFloat("nilai_sesekan");
                row[5] = rs.getFloat("gram_sesekan");
                model.addRow(row);
                total_nilai_dikerjakan = total_nilai_dikerjakan + rs.getFloat("nilai_sesekan");;
            }
            ColumnsAutoSizer.sizeColumnsToFit(tabel_pekerja_sesekan);
            int rowData = tabel_pekerja_sesekan.getRowCount();
            label_total_data_pekerja_sesek.setText(decimalFormat.format(rowData));
            label_total_nilai_dikerjakan.setText(decimalFormat.format(total_nilai_dikerjakan));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_DataLPSesekanSub.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_kinerja_pekerja_sesekan() {
        try {
            Utility.db_sub.connect();
            float total_nilai_dikerjakan = 0;
            DefaultTableModel model = (DefaultTableModel) table_data_kinerja_sesekan.getModel();
            model.setRowCount(0);
            String sub = "AND `sub` = '" + ComboBox_sub.getSelectedItem().toString() + "' ";
            if ("All".equals(ComboBox_sub.getSelectedItem().toString())) {
                sub = "";
            }
            String filter_tanggal_lp = "";
            if (Date_Setor1.getDate() != null && Date_Setor2.getDate() != null) {
                filter_tanggal_lp = "AND `tanggal_lp` BETWEEN '" + dateFormat.format(Date_Setor1.getDate()) + "' AND '" + dateFormat.format(Date_Setor2.getDate()) + "' ";
            }
            sql = "SELECT `tb_detail_penyesek`.`id_pegawai`, `tb_karyawan`.`nama_pegawai`, `bagian`, `tb_detail_penyesek`.`no_lp_sesekan`, `bulu_upah`, `waktu_setor_lp`, `tanggal_input`, `gram_sesekan`, `nilai_sesekan`  "
                    + "FROM `tb_detail_penyesek` "
                    + "LEFT JOIN `tb_karyawan` ON `tb_detail_penyesek`.`id_pegawai` = `tb_karyawan`.`id_pegawai`\n"
                    + "LEFT JOIN `tb_laporan_produksi_sesekan` ON `tb_detail_penyesek`.`no_lp_sesekan` = `tb_laporan_produksi_sesekan`.`no_lp_sesekan`\n"
                    + "WHERE `tb_detail_penyesek`.`no_lp_sesekan` LIKE '%" + txt_search_no_lp.getText() + "%'"
                    + "AND `tb_karyawan`.`nama_pegawai` LIKE '%" + txt_search_nama.getText() + "%'"
                    + sub + filter_tanggal_lp;
            rs = Utility.db_sub.getStatement().executeQuery(sql);
            Object[] row = new Object[10];
            while (rs.next()) {
                row[0] = rs.getString("id_pegawai");
                row[1] = rs.getString("nama_pegawai");
                row[2] = rs.getString("bagian");
                row[3] = rs.getString("no_lp_sesekan");
                row[4] = rs.getString("bulu_upah");
                row[5] = rs.getObject("waktu_setor_lp");
                row[6] = rs.getDate("tanggal_input");
                row[7] = rs.getFloat("gram_sesekan");
                row[8] = rs.getFloat("nilai_sesekan");
                model.addRow(row);
                total_nilai_dikerjakan = total_nilai_dikerjakan + rs.getFloat("nilai_sesekan");;
            }
            ColumnsAutoSizer.sizeColumnsToFit(table_data_kinerja_sesekan);
            int rowData = tabel_pekerja_sesekan.getRowCount();
            label_total_data_pekerja_sesek.setText(decimalFormat.format(rowData));
            label_total_nilai_dikerjakan.setText(decimalFormat.format(total_nilai_dikerjakan));

            decimalFormat.setMaximumFractionDigits(2);
            DefaultTableModel model_tabel_rekap = (DefaultTableModel) table_data_rekap.getModel();
            model_tabel_rekap.setRowCount(0);
            String qry = "SELECT DATE(`waktu_setor_lp`) AS 'tgl_setor', `id_pegawai`, `nama_pegawai`, `bagian`, SUM(`gram_sesekan`) AS 'jumlah_gram', SUM(`nilai_sesekan`) AS 'upah_sesekan' \n"
                    + "FROM (" + sql + ") detail "
                    + "GROUP BY `id_pegawai`, DATE(`waktu_setor_lp`)";
            rs = Utility.db_sub.getStatement().executeQuery(qry);
            Object[] baris = new Object[10];
            while (rs.next()) {
                baris[0] = rs.getDate("tgl_setor");
                baris[1] = rs.getString("id_pegawai");
                baris[2] = rs.getString("nama_pegawai");
                baris[3] = rs.getString("bagian");
                baris[4] = rs.getFloat("jumlah_gram");
                baris[5] = rs.getFloat("upah_sesekan");
                model_tabel_rekap.addRow(baris);
            }
            ColumnsAutoSizer.sizeColumnsToFit(table_data_rekap);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_DataLPSesekanSub.class.getName()).log(Level.SEVERE, null, ex);
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
        jPanel2 = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        label_total_keping_LP_sesekan = new javax.swing.JLabel();
        ComboBox_sub_lp_sesekan = new javax.swing.JComboBox<>();
        jLabel28 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        Table_LP_Sesekan = new javax.swing.JTable();
        button_export_lp_sesekan = new javax.swing.JButton();
        txt_search_no_lp_sesekan = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        button_search_lp_sesekan = new javax.swing.JButton();
        label_total_lp_sesekan = new javax.swing.JLabel();
        Date_Search_LP_Sesekan1 = new com.toedter.calendar.JDateChooser();
        jLabel38 = new javax.swing.JLabel();
        label_total_sesekan_bersih = new javax.swing.JLabel();
        label_total_sesekan_pasir = new javax.swing.JLabel();
        Date_Search_LP_Sesekan2 = new com.toedter.calendar.JDateChooser();
        jLabel33 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        label_total_sesekan_kuning = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabel_pekerja_sesekan = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        label_no_lp_detail = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        label_total_data_pekerja_sesek = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        label_total_nilai_dikerjakan = new javax.swing.JLabel();
        button_edit_pekerja_sesekan = new javax.swing.JButton();
        button_input_rendemen = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        button_refresh_allData = new javax.swing.JButton();
        Date_Setor2 = new com.toedter.calendar.JDateChooser();
        Date_Setor1 = new com.toedter.calendar.JDateChooser();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txt_search_nama = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        ComboBox_sub = new javax.swing.JComboBox<>();
        jPanel4 = new javax.swing.JPanel();
        label_total_gram_cabut = new javax.swing.JLabel();
        button_export_kinerja_sesekan = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        table_data_kinerja_sesekan = new javax.swing.JTable();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        label_total_upah = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        table_data_rekap = new javax.swing.JTable();
        button_export1 = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txt_search_no_lp = new javax.swing.JTextField();

        jPanel_laporan_produksi.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_laporan_produksi.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Laporan Produksi Sesekan Sub", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N
        jPanel_laporan_produksi.setPreferredSize(new java.awt.Dimension(1366, 700));

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel23.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel23.setText("Sub :");

        label_total_keping_LP_sesekan.setBackground(new java.awt.Color(255, 255, 255));
        label_total_keping_LP_sesekan.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        label_total_keping_LP_sesekan.setText("TOTAL");

        ComboBox_sub_lp_sesekan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_sub_lp_sesekan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        jLabel28.setBackground(new java.awt.Color(255, 255, 255));
        jLabel28.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel28.setText("Total Keping :");

        jLabel21.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel21.setText("No LP :");

        jLabel37.setBackground(new java.awt.Color(255, 255, 255));
        jLabel37.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel37.setText("Sampai");

        jLabel36.setBackground(new java.awt.Color(255, 255, 255));
        jLabel36.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel36.setText("Tgl LP Sesekan :");

        Table_LP_Sesekan.setAutoCreateRowSorter(true);
        Table_LP_Sesekan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_LP_Sesekan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No LP Sesekan", "SUB", "Tanggal LP", "Grade", "Bulu Upah", "Keping", "Sesekan Bersih", "Sesekan Kuning", "Sesekan Pasir", "Berat Setelah Cuci", "Pengembangan", "Nilai LP", "Progress", "Tgl Terima", "Tgl Setor", "Tgl Timbang", "Gram SSK", "Rend Bersih", "Hc", "Ront Kotor", "Ront Kng", "SH", "%SH"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
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
        Table_LP_Sesekan.getTableHeader().setReorderingAllowed(false);
        jScrollPane8.setViewportView(Table_LP_Sesekan);

        button_export_lp_sesekan.setBackground(new java.awt.Color(255, 255, 255));
        button_export_lp_sesekan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_lp_sesekan.setText("Export Excel");
        button_export_lp_sesekan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_lp_sesekanActionPerformed(evt);
            }
        });

        txt_search_no_lp_sesekan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_no_lp_sesekan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_no_lp_sesekanKeyPressed(evt);
            }
        });

        jLabel24.setBackground(new java.awt.Color(255, 255, 255));
        jLabel24.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel24.setText("Total Data :");

        button_search_lp_sesekan.setBackground(new java.awt.Color(255, 255, 255));
        button_search_lp_sesekan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search_lp_sesekan.setText("Search");
        button_search_lp_sesekan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_search_lp_sesekanActionPerformed(evt);
            }
        });

        label_total_lp_sesekan.setBackground(new java.awt.Color(255, 255, 255));
        label_total_lp_sesekan.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        label_total_lp_sesekan.setText("TOTAL");

        Date_Search_LP_Sesekan1.setBackground(new java.awt.Color(255, 255, 255));
        Date_Search_LP_Sesekan1.setToolTipText("");
        Date_Search_LP_Sesekan1.setDate(new Date(new Date().getTime()-(7 * 24 * 60 * 60 * 1000)));
        Date_Search_LP_Sesekan1.setDateFormatString("dd MMMM yyyy");
        Date_Search_LP_Sesekan1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Date_Search_LP_Sesekan1.setMinSelectableDate(new java.util.Date(1420048915000L));

        jLabel38.setBackground(new java.awt.Color(255, 255, 255));
        jLabel38.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel38.setText("Tot SSK Pasir :");

        label_total_sesekan_bersih.setBackground(new java.awt.Color(255, 255, 255));
        label_total_sesekan_bersih.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        label_total_sesekan_bersih.setText("TOTAL");

        label_total_sesekan_pasir.setBackground(new java.awt.Color(255, 255, 255));
        label_total_sesekan_pasir.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        label_total_sesekan_pasir.setText("TOTAL");

        Date_Search_LP_Sesekan2.setBackground(new java.awt.Color(255, 255, 255));
        Date_Search_LP_Sesekan2.setDate(new Date());
        Date_Search_LP_Sesekan2.setDateFormatString("dd MMMM yyyy");
        Date_Search_LP_Sesekan2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel33.setBackground(new java.awt.Color(255, 255, 255));
        jLabel33.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel33.setText("Tot SSK Kuning :");

        jLabel29.setBackground(new java.awt.Color(255, 255, 255));
        jLabel29.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel29.setText("Tot SSK Bersih :");

        label_total_sesekan_kuning.setBackground(new java.awt.Color(255, 255, 255));
        label_total_sesekan_kuning.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        label_total_sesekan_kuning.setText("TOTAL");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Pekerja Sesekan LP Terpilih", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 12))); // NOI18N

        tabel_pekerja_sesekan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tabel_pekerja_sesekan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nomor", "ID", "Nama", "Tgl Input", "Nilai Sesekan", "Gram"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Float.class, java.lang.Float.class
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
        jScrollPane1.setViewportView(tabel_pekerja_sesekan);

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel1.setText("No LP :");

        label_no_lp_detail.setBackground(new java.awt.Color(255, 255, 255));
        label_no_lp_detail.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_no_lp_detail.setText("XXX");

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel3.setText("Total data :");

        label_total_data_pekerja_sesek.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_pekerja_sesek.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_data_pekerja_sesek.setText("XXX");

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel4.setText("nilai dikerjakan :");

        label_total_nilai_dikerjakan.setBackground(new java.awt.Color(255, 255, 255));
        label_total_nilai_dikerjakan.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_nilai_dikerjakan.setText("XXX");

        button_edit_pekerja_sesekan.setBackground(new java.awt.Color(255, 255, 255));
        button_edit_pekerja_sesekan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_edit_pekerja_sesekan.setText("Edit");
        button_edit_pekerja_sesekan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_edit_pekerja_sesekanActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 492, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_no_lp_detail)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_data_pekerja_sesek)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_nilai_dikerjakan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_edit_pekerja_sesekan)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel1)
                    .addComponent(label_no_lp_detail)
                    .addComponent(jLabel3)
                    .addComponent(label_total_data_pekerja_sesek)
                    .addComponent(jLabel4)
                    .addComponent(label_total_nilai_dikerjakan)
                    .addComponent(button_edit_pekerja_sesekan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 553, Short.MAX_VALUE)
                .addContainerGap())
        );

        button_input_rendemen.setBackground(new java.awt.Color(255, 255, 255));
        button_input_rendemen.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_input_rendemen.setText("Input Rendemen");
        button_input_rendemen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_input_rendemenActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane8)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
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
                                .addComponent(label_total_sesekan_pasir))
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
                                .addComponent(button_search_lp_sesekan)))
                        .addGap(0, 99, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(button_input_rendemen)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_export_lp_sesekan)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Date_Search_LP_Sesekan1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Date_Search_LP_Sesekan2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_search_lp_sesekan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_search_no_lp_sesekan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ComboBox_sub_lp_sesekan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(9, 9, 9)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel28, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(label_total_keping_LP_sesekan, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel29, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(label_total_sesekan_kuning, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel33, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(label_total_sesekan_bersih, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(label_total_sesekan_pasir, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel38, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(label_total_lp_sesekan, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel24, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(button_export_lp_sesekan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_input_rendemen, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane8))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Data LP BP", jPanel2);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        button_refresh_allData.setBackground(new java.awt.Color(255, 255, 255));
        button_refresh_allData.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_refresh_allData.setText("Refresh");
        button_refresh_allData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refresh_allDataActionPerformed(evt);
            }
        });

        Date_Setor2.setBackground(new java.awt.Color(255, 255, 255));
        Date_Setor2.setDate(new Date());
        Date_Setor2.setDateFormatString("dd MMMM yyyy");
        Date_Setor2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Date_Setor1.setBackground(new java.awt.Color(255, 255, 255));
        Date_Setor1.setDate(new Date());
        Date_Setor1.setDateFormatString("dd MMMM yyyy");
        Date_Setor1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel5.setText("-");

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel6.setText("Tanggal Setor :");

        txt_search_nama.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_nama.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_namaKeyPressed(evt);
            }
        });

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel7.setText("Nama :");

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel8.setText("Sub :");

        ComboBox_sub.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_sub.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        label_total_gram_cabut.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_cabut.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_gram_cabut.setText("88");

        button_export_kinerja_sesekan.setBackground(new java.awt.Color(255, 255, 255));
        button_export_kinerja_sesekan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_kinerja_sesekan.setText("Export to Excel");
        button_export_kinerja_sesekan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_kinerja_sesekanActionPerformed(evt);
            }
        });

        table_data_kinerja_sesekan.setAutoCreateRowSorter(true);
        table_data_kinerja_sesekan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_data_kinerja_sesekan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Pegawai", "Nama", "Bagian", "No LP", "Jenis Blu Upah", "Tgl Setor LP", "Tgl Input", "Gram", "Nilai Kerjaan (Rp.)"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(table_data_kinerja_sesekan);

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel10.setText("Total Gram Dikerjakan :");

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel11.setText("Total Upah :");

        label_total_upah.setBackground(new java.awt.Color(255, 255, 255));
        label_total_upah.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_upah.setText("88");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gram_cabut)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_upah)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_export_kinerja_sesekan))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 753, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_upah, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_gram_cabut, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_export_kinerja_sesekan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 560, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));

        table_data_rekap.setAutoCreateRowSorter(true);
        table_data_rekap.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_data_rekap.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tgl Setor", "ID Pegawai", "Nama", "Bagian", "Gram", "Total Upah (Rp.)"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class
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
        table_data_rekap.getTableHeader().setReorderingAllowed(false);
        jScrollPane5.setViewportView(table_data_rekap);

        button_export1.setBackground(new java.awt.Color(255, 255, 255));
        button_export1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export1.setText("Export to Excel");
        button_export1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export1ActionPerformed(evt);
            }
        });

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel14.setText("Rekap /Karyawan /hari");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_export1))
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 540, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 560, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel9.setText("No LP :");

        txt_search_no_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_no_lp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_no_lpKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_search_nama, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_search_no_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ComboBox_sub, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Date_Setor1, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Date_Setor2, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_refresh_allData)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_search_no_lp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_search_nama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ComboBox_sub, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_Setor1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_Setor2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_refresh_allData, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        jTabbedPane1.addTab("Data Kinerja LP BP", jPanel3);

        javax.swing.GroupLayout jPanel_laporan_produksiLayout = new javax.swing.GroupLayout(jPanel_laporan_produksi);
        jPanel_laporan_produksi.setLayout(jPanel_laporan_produksiLayout);
        jPanel_laporan_produksiLayout.setHorizontalGroup(
            jPanel_laporan_produksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        jPanel_laporan_produksiLayout.setVerticalGroup(
            jPanel_laporan_produksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING)
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

    private void button_export_lp_sesekanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_lp_sesekanActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Table_LP_Sesekan.getModel();
        ExportToExcel.writeToExcel(model, this);
    }//GEN-LAST:event_button_export_lp_sesekanActionPerformed

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

    private void button_refresh_allDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refresh_allDataActionPerformed
        // TODO add your handling code here:
        refreshTable_kinerja_pekerja_sesekan();
    }//GEN-LAST:event_button_refresh_allDataActionPerformed

    private void txt_search_namaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_namaKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_kinerja_pekerja_sesekan();
        }
    }//GEN-LAST:event_txt_search_namaKeyPressed

    private void button_export_kinerja_sesekanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_kinerja_sesekanActionPerformed
        // TODO add your handling code here:
        DefaultTableModel modelA = (DefaultTableModel) table_data_kinerja_sesekan.getModel();
        ExportToExcel.writeToExcel(modelA, this);
    }//GEN-LAST:event_button_export_kinerja_sesekanActionPerformed

    private void button_export1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export1ActionPerformed
        // TODO add your handling code here:
        DefaultTableModel modelA = (DefaultTableModel) table_data_rekap.getModel();
        ExportToExcel.writeToExcel(modelA, this);
    }//GEN-LAST:event_button_export1ActionPerformed

    private void txt_search_no_lpKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_no_lpKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_lp_sesekan();
        }
    }//GEN-LAST:event_txt_search_no_lpKeyPressed

    private void button_edit_pekerja_sesekanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_edit_pekerja_sesekanActionPerformed
        // TODO add your handling code here:
        try {
            int j = tabel_pekerja_sesekan.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih data yang ingin di ubah !");
            } else {
                String nomor = tabel_pekerja_sesekan.getValueAt(j, 0).toString();
                JDialog_Edit_Data_PekerjaSesek dialog_edit = new JDialog_Edit_Data_PekerjaSesek(new javax.swing.JFrame(), true, nomor);
                dialog_edit.pack();
                dialog_edit.setLocationRelativeTo(this);
                dialog_edit.setVisible(true);
                dialog_edit.setEnabled(true);
                refreshTable_pekerja_sesekan(label_no_lp_detail.getText());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_DataLPSesekanSub.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_edit_pekerja_sesekanActionPerformed

    private void button_input_rendemenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_input_rendemenActionPerformed
        // TODO add your handling code here:
        try {
            int j = Table_LP_Sesekan.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih data di tabel !");
            } else {
                String no_lp = Table_LP_Sesekan.getValueAt(j, 0).toString();
                JDialog_Input_Rendemen_LP_SSK dialog_edit = new JDialog_Input_Rendemen_LP_SSK(new javax.swing.JFrame(), true, no_lp);
                dialog_edit.pack();
                dialog_edit.setLocationRelativeTo(this);
                dialog_edit.setVisible(true);
                dialog_edit.setEnabled(true);
                refreshTable_lp_sesekan();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_DataLPSesekanSub.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_input_rendemenActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_sub;
    private javax.swing.JComboBox<String> ComboBox_sub_lp_sesekan;
    private com.toedter.calendar.JDateChooser Date_Search_LP_Sesekan1;
    private com.toedter.calendar.JDateChooser Date_Search_LP_Sesekan2;
    private com.toedter.calendar.JDateChooser Date_Setor1;
    private com.toedter.calendar.JDateChooser Date_Setor2;
    public static javax.swing.JTable Table_LP_Sesekan;
    private javax.swing.JButton button_edit_pekerja_sesekan;
    private javax.swing.JButton button_export1;
    private javax.swing.JButton button_export_kinerja_sesekan;
    private javax.swing.JButton button_export_lp_sesekan;
    private javax.swing.JButton button_input_rendemen;
    private javax.swing.JButton button_refresh_allData;
    private javax.swing.JButton button_search_lp_sesekan;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel_laporan_produksi;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel label_no_lp_detail;
    private javax.swing.JLabel label_total_data_pekerja_sesek;
    private javax.swing.JLabel label_total_gram_cabut;
    private javax.swing.JLabel label_total_keping_LP_sesekan;
    private javax.swing.JLabel label_total_lp_sesekan;
    private javax.swing.JLabel label_total_nilai_dikerjakan;
    private javax.swing.JLabel label_total_sesekan_bersih;
    private javax.swing.JLabel label_total_sesekan_kuning;
    private javax.swing.JLabel label_total_sesekan_pasir;
    private javax.swing.JLabel label_total_upah;
    private javax.swing.JTable tabel_pekerja_sesekan;
    private javax.swing.JTable table_data_kinerja_sesekan;
    private javax.swing.JTable table_data_rekap;
    private javax.swing.JTextField txt_search_nama;
    private javax.swing.JTextField txt_search_no_lp;
    private javax.swing.JTextField txt_search_no_lp_sesekan;
    // End of variables declaration//GEN-END:variables
}
