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

public class JPanel_DataSaponSub extends javax.swing.JPanel {

    String sql = null;
    String sql2 = null;
    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    float kadar_air_bahan_baku = 0;

    public JPanel_DataSaponSub() {
        initComponents();
    }

    public void init() {
        try {
            Utility.db_sub.connect();
            ComboBox_sub_lp.removeAllItems();
            ComboBox_sub_lp.addItem("All");
            sql = "SELECT DISTINCT(`sub`) AS 'sub' FROM `tb_laporan_produksi_sapon` WHERE 1";
            rs = Utility.db_sub.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_sub_lp.addItem(rs.getString("sub"));
            }
            refreshTable_lp_sapon();
            Table_LP_Sapon.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent event) {
                    if (!event.getValueIsAdjusting() && Table_LP_Sapon.getSelectedRow() != -1) {
                        int i = Table_LP_Sapon.getSelectedRow();
                        String no_lp_sapon = Table_LP_Sapon.getValueAt(i, 0).toString();
                        label_no_lp_detail.setText(no_lp_sapon);
                        refreshTable_pekerja_sapon(no_lp_sapon);
                    }
                }
            });
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_DataSaponSub.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_lp_sapon() {
        try {
            Utility.db_sub.connect();
            double total_kpg = 0, total_gram_sapon = 0;
            DefaultTableModel model = (DefaultTableModel) Table_LP_Sapon.getModel();
            model.setRowCount(0);
            String sub = "AND `sub` = '" + ComboBox_sub_lp.getSelectedItem().toString() + "' ";
            if ("All".equals(ComboBox_sub_lp.getSelectedItem().toString())) {
                sub = "";
            }
            String filter_tanggal_lp = "";
            if (Date_Search1.getDate() != null && Date_Search2.getDate() != null) {
                filter_tanggal_lp = "AND `tanggal_lp` BETWEEN '" + dateFormat.format(Date_Search1.getDate()) + "' AND '" + dateFormat.format(Date_Search2.getDate()) + "' ";
            }
            sql = "SELECT `tb_laporan_produksi_sapon`.`no_lp_sapon`, `sub`, `tanggal_lp`, `kode_grade`, `bulu_upah`, `memo`, `keping`, `tb_laporan_produksi_sapon`.`gram_sapon`, `berat_setelah_cuci`, `nilai_lp`, SUM(`tb_detail_pekerja_sapon`.`nilai_sapon`) AS 'progress', `waktu_terima_lp`, `waktu_setor_lp` "
                    + "FROM `tb_laporan_produksi_sapon` "
                    + "LEFT JOIN `tb_detail_pekerja_sapon` ON `tb_laporan_produksi_sapon`.`no_lp_sapon` = `tb_detail_pekerja_sapon`.`no_lp_sapon` "
                    + "WHERE `tb_laporan_produksi_sapon`.`no_lp_sapon` LIKE '%" + txt_search_no_lp_sapon.getText() + "%' "
                    + sub + filter_tanggal_lp
                    + "GROUP BY `tb_laporan_produksi_sapon`.`no_lp_sapon` "
                    + "ORDER BY `tb_laporan_produksi_sapon`.`no_lp_sapon` DESC";
            rs = Utility.db_sub.getStatement().executeQuery(sql);
            Object[] row = new Object[30];
            while (rs.next()) {
                row[0] = rs.getString("no_lp_sapon");
                row[1] = rs.getString("sub");
                row[2] = rs.getDate("tanggal_lp");
                row[3] = rs.getString("kode_grade");
                row[4] = rs.getString("bulu_upah");
                row[5] = rs.getInt("keping");
                row[6] = rs.getInt("gram_sapon");
                row[7] = rs.getInt("berat_setelah_cuci");
                row[8] = rs.getInt("nilai_lp");
                row[9] = Math.round(rs.getFloat("berat_setelah_cuci") * 100f / rs.getFloat("gram_sapon")) / 100f;
                row[10] = rs.getInt("progress");
                row[11] = rs.getTimestamp("waktu_terima_lp");
                row[12] = rs.getTimestamp("waktu_setor_lp");
                total_kpg = total_kpg + rs.getInt("keping");
                total_gram_sapon = total_gram_sapon + rs.getInt("gram_sapon");
                
                String query = "SELECT `no_lp_sapon`, `tanggal_timbang`, `rendemen_bersih`, `hancuran`, `rontokan_kotor`, `rontokan_kuning` "
                        + "FROM `tb_laporan_produksi_sapon` "
                        + "WHERE `no_lp_sapon` = '" + rs.getString("no_lp_sapon") + "' ";
                ResultSet rst = Utility.db.getStatement().executeQuery(query);
                if (rst.next()) {
                    row[13] = rst.getDate("tanggal_timbang");
                    row[14] = rst.getFloat("rendemen_bersih");
                    row[15] = rst.getFloat("hancuran");
                    row[16] = rst.getFloat("rontokan_kotor");
                    row[17] = rst.getFloat("rontokan_kuning");
                    float gram_sh = rs.getFloat("gram_sapon") - (rst.getFloat("rendemen_bersih") + rst.getFloat("hancuran") + rst.getFloat("rontokan_kotor") + rst.getFloat("rontokan_kuning"));
                    row[18] = gram_sh;
                    row[19] = gram_sh / rs.getFloat("gram_sapon") * 100f;
                }
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_LP_Sapon);
            int rowData = Table_LP_Sapon.getRowCount();
            label_total_lp_sapon.setText(Integer.toString(rowData));
            label_total_keping_LP_sapon.setText(decimalFormat.format(total_kpg) + " Keping");
            label_total_gram_sapon.setText(decimalFormat.format(total_gram_sapon) + " Gram");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_DataSaponSub.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_pekerja_sapon(String no_lp) {
        try {
            Utility.db_sub.connect();
            float total_nilai_dikerjakan = 0;
            DefaultTableModel model = (DefaultTableModel) tabel_pekerja_sapon.getModel();
            model.setRowCount(0);
            sql = "SELECT `nomor`, `no_lp_sapon`, `tb_detail_pekerja_sapon`.`id_pegawai`, `tb_karyawan`.`nama_pegawai`, `tanggal_input`, `nilai_sapon`, `gram_sapon` \n"
                    + "FROM `tb_detail_pekerja_sapon` "
                    + "LEFT JOIN `tb_karyawan` ON `tb_detail_pekerja_sapon`.`id_pegawai` = `tb_karyawan`.`id_pegawai`\n"
                    + "WHERE `no_lp_sapon` = '" + no_lp + "'";
            rs = Utility.db_sub.getStatement().executeQuery(sql);
            Object[] row = new Object[10];
            while (rs.next()) {
                row[0] = rs.getInt("nomor");
                row[1] = rs.getString("id_pegawai");
                row[2] = rs.getString("nama_pegawai");
                row[3] = rs.getDate("tanggal_input");
                row[4] = rs.getFloat("nilai_sapon");
                row[5] = rs.getFloat("gram_sapon");
                model.addRow(row);
                total_nilai_dikerjakan = total_nilai_dikerjakan + rs.getFloat("nilai_sapon");;
            }
            ColumnsAutoSizer.sizeColumnsToFit(tabel_pekerja_sapon);
            int rowData = tabel_pekerja_sapon.getRowCount();
            label_total_data_pekerja_sapon.setText(decimalFormat.format(rowData));
            label_total_nilai_dikerjakan.setText(decimalFormat.format(total_nilai_dikerjakan));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_DataSaponSub.class.getName()).log(Level.SEVERE, null, ex);
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
        jLabel23 = new javax.swing.JLabel();
        label_total_keping_LP_sapon = new javax.swing.JLabel();
        ComboBox_sub_lp = new javax.swing.JComboBox<>();
        jLabel28 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        Table_LP_Sapon = new javax.swing.JTable();
        button_export_lp_sapon = new javax.swing.JButton();
        txt_search_no_lp_sapon = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        button_search_lp_sapon = new javax.swing.JButton();
        label_total_lp_sapon = new javax.swing.JLabel();
        Date_Search1 = new com.toedter.calendar.JDateChooser();
        label_total_gram_sapon = new javax.swing.JLabel();
        Date_Search2 = new com.toedter.calendar.JDateChooser();
        jLabel29 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabel_pekerja_sapon = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        label_no_lp_detail = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        label_total_data_pekerja_sapon = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        label_total_nilai_dikerjakan = new javax.swing.JLabel();
        button_input_rendemen = new javax.swing.JButton();

        jPanel_laporan_produksi.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_laporan_produksi.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Laporan Produksi Sapon", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N
        jPanel_laporan_produksi.setPreferredSize(new java.awt.Dimension(1366, 700));

        jLabel23.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel23.setText("Sub :");

        label_total_keping_LP_sapon.setBackground(new java.awt.Color(255, 255, 255));
        label_total_keping_LP_sapon.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        label_total_keping_LP_sapon.setText("TOTAL");

        ComboBox_sub_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_sub_lp.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

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
        jLabel36.setText("Tgl LP Sapon :");

        Table_LP_Sapon.setAutoCreateRowSorter(true);
        Table_LP_Sapon.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_LP_Sapon.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No LP Sapon", "SUB", "Tanggal LP", "Grade", "Bulu Upah", "Keping", "Gram Sapon", "Berat Setelah Cuci", "Nilai LP", "Pengembangan", "Proses", "Waktu Terima", "Waktu Setor", "Tgl Timbang", "Rend Bersih", "Hc", "Ront Kotor", "Ront Kuning", "SH", "% SH"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Table_LP_Sapon.getTableHeader().setReorderingAllowed(false);
        jScrollPane8.setViewportView(Table_LP_Sapon);

        button_export_lp_sapon.setBackground(new java.awt.Color(255, 255, 255));
        button_export_lp_sapon.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_lp_sapon.setText("Export Excel");
        button_export_lp_sapon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_lp_saponActionPerformed(evt);
            }
        });

        txt_search_no_lp_sapon.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_no_lp_sapon.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_no_lp_saponKeyPressed(evt);
            }
        });

        jLabel24.setBackground(new java.awt.Color(255, 255, 255));
        jLabel24.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel24.setText("Total Data :");

        button_search_lp_sapon.setBackground(new java.awt.Color(255, 255, 255));
        button_search_lp_sapon.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search_lp_sapon.setText("Search");
        button_search_lp_sapon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_search_lp_saponActionPerformed(evt);
            }
        });

        label_total_lp_sapon.setBackground(new java.awt.Color(255, 255, 255));
        label_total_lp_sapon.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        label_total_lp_sapon.setText("TOTAL");

        Date_Search1.setBackground(new java.awt.Color(255, 255, 255));
        Date_Search1.setToolTipText("");
        Date_Search1.setDate(new Date(new Date().getTime()-(7 * 24 * 60 * 60 * 1000)));
        Date_Search1.setDateFormatString("dd MMMM yyyy");
        Date_Search1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Date_Search1.setMinSelectableDate(new java.util.Date(1420048915000L));

        label_total_gram_sapon.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_sapon.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        label_total_gram_sapon.setText("TOTAL");

        Date_Search2.setBackground(new java.awt.Color(255, 255, 255));
        Date_Search2.setDate(new Date());
        Date_Search2.setDateFormatString("dd MMMM yyyy");
        Date_Search2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel29.setBackground(new java.awt.Color(255, 255, 255));
        jLabel29.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel29.setText("Tot Gram Sapon :");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Pekerja Sapon", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 12))); // NOI18N

        tabel_pekerja_sapon.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tabel_pekerja_sapon.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nomor", "ID", "Nama", "Tgl Input", "Nilai Sapon", "Gram"
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
        jScrollPane1.setViewportView(tabel_pekerja_sapon);

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel1.setText("No LP :");

        label_no_lp_detail.setBackground(new java.awt.Color(255, 255, 255));
        label_no_lp_detail.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_no_lp_detail.setText("XXX");

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel3.setText("Total data :");

        label_total_data_pekerja_sapon.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_pekerja_sapon.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_data_pekerja_sapon.setText("XXX");

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel4.setText("Total nilai dikerjakan :");

        label_total_nilai_dikerjakan.setBackground(new java.awt.Color(255, 255, 255));
        label_total_nilai_dikerjakan.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_nilai_dikerjakan.setText("XXX");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 462, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_no_lp_detail)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_data_pekerja_sapon)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_nilai_dikerjakan)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4)
                        .addComponent(label_total_nilai_dikerjakan))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(label_total_data_pekerja_sapon))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(label_no_lp_detail)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 595, Short.MAX_VALUE)
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

        javax.swing.GroupLayout jPanel_laporan_produksiLayout = new javax.swing.GroupLayout(jPanel_laporan_produksi);
        jPanel_laporan_produksi.setLayout(jPanel_laporan_produksiLayout);
        jPanel_laporan_produksiLayout.setHorizontalGroup(
            jPanel_laporan_produksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_laporan_produksiLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_laporan_produksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 846, Short.MAX_VALUE)
                    .addGroup(jPanel_laporan_produksiLayout.createSequentialGroup()
                        .addComponent(jLabel21)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_no_lp_sapon, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel23)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_sub_lp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel36)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_Search1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel37)
                        .addGap(6, 6, 6)
                        .addComponent(Date_Search2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_search_lp_sapon)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel_laporan_produksiLayout.createSequentialGroup()
                        .addComponent(jLabel24)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_lp_sapon)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel28)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_keping_LP_sapon)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel29)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gram_sapon)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_input_rendemen)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_export_lp_sapon)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel_laporan_produksiLayout.setVerticalGroup(
            jPanel_laporan_produksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_laporan_produksiLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_laporan_produksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_laporan_produksiLayout.createSequentialGroup()
                        .addGroup(jPanel_laporan_produksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Date_Search1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Date_Search2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_search_lp_sapon, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_search_no_lp_sapon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ComboBox_sub_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(7, 7, 7)
                        .addGroup(jPanel_laporan_produksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel28, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(label_total_keping_LP_sapon, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel29, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_laporan_produksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(label_total_gram_sapon)
                                .addComponent(button_input_rendemen, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(button_export_lp_sapon, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(label_total_lp_sapon, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel24, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane8)
                        .addContainerGap())
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
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

    private void button_export_lp_saponActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_lp_saponActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Table_LP_Sapon.getModel();
        ExportToExcel.writeToExcel(model, this);
    }//GEN-LAST:event_button_export_lp_saponActionPerformed

    private void button_search_lp_saponActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_lp_saponActionPerformed
        // TODO add your handling code here:
        refreshTable_lp_sapon();
    }//GEN-LAST:event_button_search_lp_saponActionPerformed

    private void txt_search_no_lp_saponKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_no_lp_saponKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_lp_sapon();
        }
    }//GEN-LAST:event_txt_search_no_lp_saponKeyPressed

    private void button_input_rendemenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_input_rendemenActionPerformed
        // TODO add your handling code here:
        try {
            int j = Table_LP_Sapon.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih data di tabel !");
            } else {
                String no_lp = Table_LP_Sapon.getValueAt(j, 0).toString();
                JDialog_Input_Rendemen_LP_Sapon dialog_edit = new JDialog_Input_Rendemen_LP_Sapon(new javax.swing.JFrame(), true, no_lp);
                dialog_edit.pack();
                dialog_edit.setLocationRelativeTo(this);
                dialog_edit.setVisible(true);
                dialog_edit.setEnabled(true);
                refreshTable_lp_sapon();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(JPanel_DataLPSesekanSub.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_input_rendemenActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_sub_lp;
    private com.toedter.calendar.JDateChooser Date_Search1;
    private com.toedter.calendar.JDateChooser Date_Search2;
    public static javax.swing.JTable Table_LP_Sapon;
    private javax.swing.JButton button_export_lp_sapon;
    private javax.swing.JButton button_input_rendemen;
    private javax.swing.JButton button_search_lp_sapon;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel_laporan_produksi;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JLabel label_no_lp_detail;
    private javax.swing.JLabel label_total_data_pekerja_sapon;
    private javax.swing.JLabel label_total_gram_sapon;
    private javax.swing.JLabel label_total_keping_LP_sapon;
    private javax.swing.JLabel label_total_lp_sapon;
    private javax.swing.JLabel label_total_nilai_dikerjakan;
    private javax.swing.JTable tabel_pekerja_sapon;
    private javax.swing.JTextField txt_search_no_lp_sapon;
    // End of variables declaration//GEN-END:variables
}
