package waleta_system.HRD;

import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;

public class JPanel_ijinToilet extends javax.swing.JPanel {

    String sql;
    ResultSet rs;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public JPanel_ijinToilet() {
        initComponents();
    }

    public void init() {
        refreshTable();
    }

    public void refreshTable() {
        try {
            DefaultTableModel model_kiri = (DefaultTableModel) Table_ijin_toilet.getModel();
            model_kiri.setRowCount(0);
            DefaultTableModel model_kanan = (DefaultTableModel) Table_Belum_Kembali.getModel();
            model_kanan.setRowCount(0);
            String filter_tanggal = "";
            if (Date_Search1.getDate() != null && Date_Search2.getDate() != null) {
                filter_tanggal = "AND DATE(`waktu_ijin`) BETWEEN '" + dateFormat.format(Date_Search1.getDate()) + "' AND '" + dateFormat.format(Date_Search2.getDate()) + "' ";
            }
            sql = "SELECT `no`, `tb_ijin_keluar_ruangan`.`id_pegawai`, `tb_karyawan`.`nama_pegawai`, `nama_bagian`, `waktu_ijin`, DATE(`waktu_ijin`) AS 'tgl_ijin', `waktu_kembali`, `keterangan_ijin`, "
                    + "IF(`waktu_kembali` IS NULL, TIMEDIFF(NOW(), `waktu_ijin`), TIMEDIFF(`waktu_kembali`, `waktu_ijin`)) AS 'durasi' \n"
                    + "FROM `tb_ijin_keluar_ruangan` \n"
                    + "LEFT JOIN `tb_karyawan` ON `tb_ijin_keluar_ruangan`.`id_pegawai` = `tb_karyawan`.`id_pegawai`\n"
                    + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`\n"
                    + "WHERE `tb_karyawan`.`nama_pegawai` LIKE '%" + txt_search_nama.getText() + "%' "
                    + "AND `nama_bagian` LIKE '%" + txt_search_bagian.getText() + "%' "
                    + filter_tanggal
                    + "ORDER BY `waktu_ijin` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[10];
            while (rs.next()) {
                row[0] = rs.getInt("no");
                row[1] = rs.getString("nama_pegawai");
                row[2] = rs.getString("nama_bagian");
                row[3] = rs.getTimestamp("waktu_ijin");
                row[4] = rs.getTimestamp("waktu_kembali");
                row[5] = rs.getString("keterangan_ijin");
                if (rs.getTimestamp("waktu_kembali") == null) {
                    row[6] = null;
                } else {
                    row[6] = rs.getTime("durasi");
                }
                model_kiri.addRow(row);
                if (rs.getTimestamp("waktu_kembali") == null && rs.getString("tgl_ijin").equals(dateFormat.format(new Date()))) {
                    row[6] = rs.getTime("durasi");
                    model_kanan.addRow(row);
                }
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_ijin_toilet);
            label_total_data_ijin.setText(Integer.toString(Table_ijin_toilet.getRowCount()));
            ColumnsAutoSizer.sizeColumnsToFit(Table_Belum_Kembali);
            label_total_data_belum_kembali.setText(Integer.toString(Table_Belum_Kembali.getRowCount()));
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_ijinToilet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_rekap() {
        try {
            DefaultTableModel model = (DefaultTableModel) Table_data_rekap_karyawan.getModel();
            model.setRowCount(0);
            String filter_tanggal = "";
            if (Date_Search1_rekap.getDate() != null && Date_Search2_rekap.getDate() != null) {
                filter_tanggal = "AND DATE(`waktu_ijin`) BETWEEN '" + dateFormat.format(Date_Search1_rekap.getDate()) + "' AND '" + dateFormat.format(Date_Search2_rekap.getDate()) + "' ";
            }
            sql = "SELECT `tb_ijin_keluar_ruangan`.`id_pegawai`, `tb_karyawan`.`nama_pegawai`, `nama_bagian`, `keterangan_ijin`, "
                    + "SUM(IF(`keterangan_ijin` = 'Toilet', 1, 0)) AS 'jumlah_toilet', "
                    + "SUM(IF(`keterangan_ijin` = 'Mushola', 1, 0)) AS 'jumlah_Mushola', "
                    + "SEC_TO_TIME(SUM(TIME_TO_SEC(IF(`waktu_kembali` IS NOT NULL AND `keterangan_ijin` = 'Toilet', TIMEDIFF(`waktu_kembali`, `waktu_ijin`), 0)))) AS 'durasi_toilet', \n"
                    + "SEC_TO_TIME(SUM(TIME_TO_SEC(IF(`waktu_kembali` IS NOT NULL AND `keterangan_ijin` = 'Mushola', TIMEDIFF(`waktu_kembali`, `waktu_ijin`), 0)))) AS 'durasi_Mushola' \n"
                    + "FROM `tb_ijin_keluar_ruangan` \n"
                    + "LEFT JOIN `tb_karyawan` ON `tb_ijin_keluar_ruangan`.`id_pegawai` = `tb_karyawan`.`id_pegawai`\n"
                    + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`\n"
                    + "WHERE `tb_karyawan`.`nama_pegawai` LIKE '%" + txt_search_nama_rekap.getText() + "%' "
                    + "AND `nama_bagian` LIKE '%" + txt_search_bagian_rekap.getText() + "%' "
                    + filter_tanggal
                    + "GROUP BY `tb_ijin_keluar_ruangan`.`id_pegawai` ORDER BY `nama_pegawai`";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[10];
            while (rs.next()) {
                row[0] = rs.getString("nama_pegawai");
                row[1] = rs.getString("nama_bagian");
                row[2] = rs.getInt("jumlah_toilet");
                row[3] = rs.getTime("durasi_toilet");
                row[4] = rs.getInt("jumlah_Mushola");
                row[5] = rs.getTime("durasi_Mushola");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_data_rekap_karyawan);
            label_total_data_karyawan.setText(Integer.toString(Table_data_rekap_karyawan.getRowCount()));
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_ijinToilet.class.getName()).log(Level.SEVERE, null, ex);
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
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel4 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        Table_ijin_toilet = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        Table_Belum_Kembali = new javax.swing.JTable();
        Jlabel7 = new javax.swing.JLabel();
        label_total_data_belum_kembali = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        txt_search_nama = new javax.swing.JTextField();
        Date_Search1 = new com.toedter.calendar.JDateChooser();
        label_total_data_ijin = new javax.swing.JLabel();
        button_search_data_ijin = new javax.swing.JButton();
        Jlabel5 = new javax.swing.JLabel();
        Date_Search2 = new com.toedter.calendar.JDateChooser();
        txt_search_bagian = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        button_tampilan_scan = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        Table_data_rekap_karyawan = new javax.swing.JTable();
        Jlabel9 = new javax.swing.JLabel();
        label_total_data_karyawan = new javax.swing.JLabel();
        Date_Search2_rekap = new com.toedter.calendar.JDateChooser();
        Date_Search1_rekap = new com.toedter.calendar.JDateChooser();
        jLabel18 = new javax.swing.JLabel();
        button_search_data_karyawan = new javax.swing.JButton();
        txt_search_bagian_rekap = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        txt_search_nama_rekap = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel17.setText("Tanggal :");

        Table_ijin_toilet.setAutoCreateRowSorter(true);
        Table_ijin_toilet.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Table_ijin_toilet.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "Nama", "Bagian", "Waktu ke Toilet", "Waktu Kembali", "Keterangan", "Durasi"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.Object.class
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
        Table_ijin_toilet.setRowHeight(20);
        jScrollPane1.setViewportView(Table_ijin_toilet);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Data Belum Kembali", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N

        Table_Belum_Kembali.setAutoCreateRowSorter(true);
        Table_Belum_Kembali.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Table_Belum_Kembali.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nama", "Bagian", "Jam Scan", "Jam Kembali", "Ket.", "Durasi"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.Object.class
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
        Table_Belum_Kembali.setRowHeight(20);
        jScrollPane2.setViewportView(Table_Belum_Kembali);

        Jlabel7.setBackground(new java.awt.Color(255, 255, 255));
        Jlabel7.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        Jlabel7.setText("Total Data :");
        Jlabel7.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        Jlabel7.setMaximumSize(new java.awt.Dimension(280, 15));

        label_total_data_belum_kembali.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_belum_kembali.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        label_total_data_belum_kembali.setText("0");
        label_total_data_belum_kembali.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        label_total_data_belum_kembali.setMaximumSize(new java.awt.Dimension(280, 15));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 591, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(Jlabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_data_belum_kembali, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Jlabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_data_belum_kembali, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 569, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel16.setText("Bagian :");

        txt_search_nama.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_nama.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_namaKeyPressed(evt);
            }
        });

        Date_Search1.setBackground(new java.awt.Color(255, 255, 255));
        Date_Search1.setToolTipText("");
        Date_Search1.setDate(new Date());
        Date_Search1.setDateFormatString("dd MMM yyyy");
        Date_Search1.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N

        label_total_data_ijin.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_ijin.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_data_ijin.setText("0");
        label_total_data_ijin.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        label_total_data_ijin.setMaximumSize(new java.awt.Dimension(280, 15));

        button_search_data_ijin.setBackground(new java.awt.Color(255, 255, 255));
        button_search_data_ijin.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search_data_ijin.setText("Search");
        button_search_data_ijin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_search_data_ijinActionPerformed(evt);
            }
        });

        Jlabel5.setBackground(new java.awt.Color(255, 255, 255));
        Jlabel5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Jlabel5.setText("Total Data :");
        Jlabel5.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        Jlabel5.setMaximumSize(new java.awt.Dimension(280, 15));

        Date_Search2.setBackground(new java.awt.Color(255, 255, 255));
        Date_Search2.setDate(new Date());
        Date_Search2.setDateFormatString("dd MMM yyyy");
        Date_Search2.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        Date_Search2.setMinSelectableDate(new java.util.Date(-62135791118000L));

        txt_search_bagian.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_bagian.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_bagianKeyPressed(evt);
            }
        });

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("Nama :");

        button_tampilan_scan.setBackground(new java.awt.Color(255, 255, 255));
        button_tampilan_scan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_tampilan_scan.setText("Tampilan Scan");
        button_tampilan_scan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_tampilan_scanActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1031, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(Jlabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_data_ijin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_nama, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel16)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_bagian, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel17)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_Search1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_Search2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_search_data_ijin)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_tampilan_scan)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(button_search_data_ijin, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_search_bagian, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_search_nama, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Date_Search1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Date_Search2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_tampilan_scan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(Jlabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_data_ijin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1))
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Data Ijin Keluar Ruangan", jPanel4);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        Table_data_rekap_karyawan.setAutoCreateRowSorter(true);
        Table_data_rekap_karyawan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Table_data_rekap_karyawan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nama", "Bagian", "Jumlah Ijin Toilet", "Durasi Ijin Toilet", "Jumlah Ijin Mushola", "Durasi Ijin Mushola"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Object.class, java.lang.Float.class, java.lang.Object.class
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
        Table_data_rekap_karyawan.setRowHeight(18);
        jScrollPane3.setViewportView(Table_data_rekap_karyawan);

        Jlabel9.setBackground(new java.awt.Color(255, 255, 255));
        Jlabel9.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        Jlabel9.setText("Total Data :");
        Jlabel9.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        Jlabel9.setMaximumSize(new java.awt.Dimension(280, 15));

        label_total_data_karyawan.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_karyawan.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        label_total_data_karyawan.setText("0");
        label_total_data_karyawan.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        label_total_data_karyawan.setMaximumSize(new java.awt.Dimension(280, 15));

        Date_Search2_rekap.setBackground(new java.awt.Color(255, 255, 255));
        Date_Search2_rekap.setDateFormatString("dd MMM yyyy");
        Date_Search2_rekap.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        Date_Search2_rekap.setMinSelectableDate(new java.util.Date(-62135791118000L));

        Date_Search1_rekap.setBackground(new java.awt.Color(255, 255, 255));
        Date_Search1_rekap.setToolTipText("");
        Date_Search1_rekap.setDateFormatString("dd MMM yyyy");
        Date_Search1_rekap.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel18.setText("Tanggal :");

        button_search_data_karyawan.setBackground(new java.awt.Color(255, 255, 255));
        button_search_data_karyawan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search_data_karyawan.setText("Search");
        button_search_data_karyawan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_search_data_karyawanActionPerformed(evt);
            }
        });

        txt_search_bagian_rekap.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_bagian_rekap.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_bagian_rekapKeyPressed(evt);
            }
        });

        jLabel19.setBackground(new java.awt.Color(255, 255, 255));
        jLabel19.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel19.setText("Bagian :");

        txt_search_nama_rekap.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_nama_rekap.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_nama_rekapKeyPressed(evt);
            }
        });

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("Nama Karyawan :");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(Jlabel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_data_karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_nama_rekap, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel19)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_bagian_rekap, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel18)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_Search1_rekap, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_Search2_rekap, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_search_data_karyawan)))
                        .addGap(0, 889, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(button_search_data_karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_bagian_rekap, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_nama_rekap, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_Search1_rekap, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_Search2_rekap, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Jlabel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_data_karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 589, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Rekap Karyawan", jPanel3);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING)
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

    private void txt_search_namaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_namaKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_search_namaKeyPressed

    private void txt_search_bagianKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_bagianKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_search_bagianKeyPressed

    private void txt_search_bagian_rekapKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_bagian_rekapKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_rekap();
        }
    }//GEN-LAST:event_txt_search_bagian_rekapKeyPressed

    private void txt_search_nama_rekapKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_nama_rekapKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_rekap();
        }
    }//GEN-LAST:event_txt_search_nama_rekapKeyPressed

    private void button_tampilan_scanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_tampilan_scanActionPerformed
        // TODO add your handling code here:
        JFrame_ScanIjinToilet frame = new JFrame_ScanIjinToilet();
        frame.pack();
        frame.setResizable(true);
        frame.setLocationRelativeTo(this);
        frame.setVisible(true);
        frame.setEnabled(true);
    }//GEN-LAST:event_button_tampilan_scanActionPerformed

    private void button_search_data_ijinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_data_ijinActionPerformed
        // TODO add your handling code here:
        refreshTable();
    }//GEN-LAST:event_button_search_data_ijinActionPerformed

    private void button_search_data_karyawanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_data_karyawanActionPerformed
        // TODO add your handling code here:
        refreshTable_rekap();
    }//GEN-LAST:event_button_search_data_karyawanActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser Date_Search1;
    private com.toedter.calendar.JDateChooser Date_Search1_rekap;
    private com.toedter.calendar.JDateChooser Date_Search2;
    private com.toedter.calendar.JDateChooser Date_Search2_rekap;
    private javax.swing.JLabel Jlabel5;
    private javax.swing.JLabel Jlabel7;
    private javax.swing.JLabel Jlabel9;
    private javax.swing.JTable Table_Belum_Kembali;
    private javax.swing.JTable Table_data_rekap_karyawan;
    private javax.swing.JTable Table_ijin_toilet;
    private javax.swing.JButton button_search_data_ijin;
    private javax.swing.JButton button_search_data_karyawan;
    private javax.swing.JButton button_tampilan_scan;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel label_total_data_belum_kembali;
    private javax.swing.JLabel label_total_data_ijin;
    private javax.swing.JLabel label_total_data_karyawan;
    private javax.swing.JTextField txt_search_bagian;
    private javax.swing.JTextField txt_search_bagian_rekap;
    private javax.swing.JTextField txt_search_nama;
    private javax.swing.JTextField txt_search_nama_rekap;
    // End of variables declaration//GEN-END:variables
}
