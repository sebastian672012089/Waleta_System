package waleta_system.HRD;

import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
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

public class JPanel_Data_personalHygiene extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public JPanel_Data_personalHygiene() {
        initComponents();
    }

    public void init() {
        Table_pelanggaran_ph_rekap.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && Table_pelanggaran_ph_rekap.getSelectedRow() != -1) {
                    int i = Table_pelanggaran_ph_rekap.getSelectedRow();
                    refreshTable_rekap_Pelanggaran_hp_detail(Table_pelanggaran_ph_rekap.getValueAt(i, 0).toString());
                }
            }
        });
        refreshTable_Pelanggaran_hp();
    }

    public void refreshTable_Pelanggaran_hp() {
        try {
            DefaultTableModel model = (DefaultTableModel) Table_pelanggaran_ph.getModel();
            model.setRowCount(0);
            String filter_tanggal = "";
            if (Date1.getDate() != null && Date2.getDate() != null) {
                filter_tanggal = " AND `tanggal_pelanggaran` BETWEEN '" + dateFormat.format(Date1.getDate()) + "' AND '" + dateFormat.format(Date2.getDate()) + "'";
            }
            sql = "SELECT `nomor_pelanggaran`, PH.`id_pegawai`, `tb_karyawan`.`nama_pegawai`, `tb_bagian`.`nama_bagian`, `tanggal_pelanggaran`, `jenis_pelanggaran`, PH.`keterangan` \n"
                    + "FROM `tb_pelanggaran_personal_hygiene` PH \n"
                    + "LEFT JOIN `tb_karyawan` ON PH.`id_pegawai` = `tb_karyawan`.`id_pegawai`\n"
                    + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`\n"
                    + "WHERE "
                    + "`nama_pegawai` LIKE '%" + txt_search_keyword.getText() + "%'"
                    + filter_tanggal;
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[10];
            while (rs.next()) {
                row[0] = rs.getInt("nomor_pelanggaran");
                row[1] = rs.getString("id_pegawai");
                row[2] = rs.getString("nama_pegawai");
                row[3] = rs.getString("nama_bagian");
                row[4] = rs.getString("tanggal_pelanggaran");
                row[5] = rs.getString("jenis_pelanggaran");
                row[6] = rs.getString("keterangan");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_pelanggaran_ph);
            label_total_data.setText("Total data : " + Integer.toString(Table_pelanggaran_ph.getRowCount()));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Data_personalHygiene.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_rekap_Pelanggaran_hp() {
        try {
            DefaultTableModel model = (DefaultTableModel) Table_pelanggaran_ph_rekap.getModel();
            model.setRowCount(0);
            String filter_tanggal = "";
            if (Date_rekap1.getDate() != null && Date_rekap2.getDate() != null) {
                filter_tanggal = " AND `tanggal_pelanggaran` BETWEEN '" + dateFormat.format(Date_rekap1.getDate()) + "' AND '" + dateFormat.format(Date_rekap2.getDate()) + "' ";
            }
            sql = "SELECT PH.`id_pegawai`, `tb_karyawan`.`nama_pegawai`, `tb_bagian`.`nama_bagian`, `tanggal_masuk`, `status`, COUNT(`nomor_pelanggaran`) AS 'jumlah' \n"
                    + "FROM `tb_pelanggaran_personal_hygiene` PH \n"
                    + "LEFT JOIN `tb_karyawan` ON PH.`id_pegawai` = `tb_karyawan`.`id_pegawai`\n"
                    + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`\n"
                    + "WHERE "
                    + "`nama_pegawai` LIKE '%" + txt_search_nama_rekap.getText() + "%'"
                    + filter_tanggal
                    + "GROUP BY PH.`id_pegawai` "
                    + "ORDER BY `tb_karyawan`.`nama_pegawai` ";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[10];
            while (rs.next()) {
                row[0] = rs.getString("id_pegawai");
                row[1] = rs.getString("nama_pegawai");
                row[2] = rs.getString("nama_bagian");
                row[3] = rs.getDate("tanggal_masuk");
                row[4] = rs.getInt("jumlah");
                row[5] = rs.getString("status");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_pelanggaran_ph_rekap);
            label_total_data_rekap.setText("Total data : " + Integer.toString(Table_pelanggaran_ph_rekap.getRowCount()));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Data_personalHygiene.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void refreshTable_rekap_Pelanggaran_hp_detail(String id) {
        try {
            DefaultTableModel model = (DefaultTableModel) Table_pelanggaran_ph_rekap_detail.getModel();
            model.setRowCount(0);
            String filter_tanggal = "";
            if (Date_rekap1.getDate() != null && Date_rekap2.getDate() != null) {
                filter_tanggal = " AND `tanggal_pelanggaran` BETWEEN '" + dateFormat.format(Date_rekap1.getDate()) + "' AND '" + dateFormat.format(Date_rekap2.getDate()) + "' ";
            }
            sql = "SELECT `nomor_pelanggaran`, `id_pegawai`, `tanggal_pelanggaran`, `jenis_pelanggaran`, `keterangan` \n"
                    + "FROM `tb_pelanggaran_personal_hygiene` \n"
                    + "WHERE "
                    + "`id_pegawai` = '" + id + "' "
                    + filter_tanggal
                    + "ORDER BY `tanggal_pelanggaran` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[10];
            while (rs.next()) {
                row[0] = rs.getString("tanggal_pelanggaran");
                row[1] = rs.getString("jenis_pelanggaran");
                row[2] = rs.getString("keterangan");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_pelanggaran_ph_rekap_detail);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Data_personalHygiene.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        Table_pelanggaran_ph = new javax.swing.JTable();
        button_refresh = new javax.swing.JButton();
        label_total_data = new javax.swing.JLabel();
        txt_search_keyword = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        Date1 = new com.toedter.calendar.JDateChooser();
        Date2 = new com.toedter.calendar.JDateChooser();
        jLabel6 = new javax.swing.JLabel();
        button_export_tdkMasuk = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        Table_pelanggaran_ph_rekap = new javax.swing.JTable();
        button_refresh_rekap = new javax.swing.JButton();
        label_total_data_rekap = new javax.swing.JLabel();
        txt_search_nama_rekap = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        Date_rekap1 = new com.toedter.calendar.JDateChooser();
        Date_rekap2 = new com.toedter.calendar.JDateChooser();
        jLabel9 = new javax.swing.JLabel();
        button_export_pelanggaranph_rekap = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        Table_pelanggaran_ph_rekap_detail = new javax.swing.JTable();

        setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Data Personal Hygene", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        Table_pelanggaran_ph.setAutoCreateRowSorter(true);
        Table_pelanggaran_ph.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Table_pelanggaran_ph.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "ID pegawai", "Nama", "Bagian", "Tanggal", "Jenis Pelanggaran", "Keterangan"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class
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
        Table_pelanggaran_ph.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(Table_pelanggaran_ph);
        if (Table_pelanggaran_ph.getColumnModel().getColumnCount() > 0) {
            Table_pelanggaran_ph.getColumnModel().getColumn(0).setHeaderValue("No");
        }

        button_refresh.setBackground(new java.awt.Color(255, 255, 255));
        button_refresh.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_refresh.setText("Refresh");
        button_refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refreshActionPerformed(evt);
            }
        });

        label_total_data.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_data.setText("Total data :");

        txt_search_keyword.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_search_keyword.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_keywordKeyPressed(evt);
            }
        });

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel7.setText("Tanggal :");

        Date1.setBackground(new java.awt.Color(255, 255, 255));
        Date1.setDate(new Date());
        Date1.setDateFormatString("dd MMM yyyy");
        Date1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Date2.setBackground(new java.awt.Color(255, 255, 255));
        Date2.setDate(new Date());
        Date2.setDateFormatString("dd MMM yyyy");
        Date2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel6.setText("Nama :");

        button_export_tdkMasuk.setBackground(new java.awt.Color(255, 255, 255));
        button_export_tdkMasuk.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_tdkMasuk.setText("Export");
        button_export_tdkMasuk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_tdkMasukActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_keyword, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_refresh)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_data)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 652, Short.MAX_VALUE)
                        .addComponent(button_export_tdkMasuk)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_search_keyword, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_data, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_refresh, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export_tdkMasuk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 594, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Data Pelanggaran Karyawan", jPanel2);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        Table_pelanggaran_ph_rekap.setAutoCreateRowSorter(true);
        Table_pelanggaran_ph_rekap.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Table_pelanggaran_ph_rekap.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID pegawai", "Nama", "Bagian", "Tanggal Masuk", "Jumlah", "Status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Integer.class, java.lang.String.class
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
        Table_pelanggaran_ph_rekap.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(Table_pelanggaran_ph_rekap);

        button_refresh_rekap.setBackground(new java.awt.Color(255, 255, 255));
        button_refresh_rekap.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_refresh_rekap.setText("Refresh");
        button_refresh_rekap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refresh_rekapActionPerformed(evt);
            }
        });

        label_total_data_rekap.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_rekap.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_data_rekap.setText("Total data :");

        txt_search_nama_rekap.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_search_nama_rekap.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_nama_rekapKeyPressed(evt);
            }
        });

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel8.setText("Tanggal :");

        Date_rekap1.setBackground(new java.awt.Color(255, 255, 255));
        Date_rekap1.setDate(new Date());
        Date_rekap1.setDateFormatString("dd MMM yyyy");
        Date_rekap1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Date_rekap2.setBackground(new java.awt.Color(255, 255, 255));
        Date_rekap2.setDate(new Date());
        Date_rekap2.setDateFormatString("dd MMM yyyy");
        Date_rekap2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel9.setText("Nama :");

        button_export_pelanggaranph_rekap.setBackground(new java.awt.Color(255, 255, 255));
        button_export_pelanggaranph_rekap.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_pelanggaranph_rekap.setText("Export");
        button_export_pelanggaranph_rekap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_pelanggaranph_rekapActionPerformed(evt);
            }
        });

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Pelanggaran", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 12))); // NOI18N

        Table_pelanggaran_ph_rekap_detail.setAutoCreateRowSorter(true);
        Table_pelanggaran_ph_rekap_detail.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Table_pelanggaran_ph_rekap_detail.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tanggal", "Jenis Pelanggaran", "Keterangan"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class, java.lang.String.class
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
        Table_pelanggaran_ph_rekap_detail.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(Table_pelanggaran_ph_rekap_detail);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 453, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_nama_rekap, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_rekap1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_rekap2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_refresh_rekap)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_data_rekap)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 163, Short.MAX_VALUE)
                        .addComponent(button_export_pelanggaranph_rekap))
                    .addComponent(jScrollPane2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(txt_search_nama_rekap, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_data_rekap, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Date_rekap2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_refresh_rekap, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Date_rekap1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_export_pelanggaranph_rekap, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 594, Short.MAX_VALUE))
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Rekap Pelanggaran Karyawan", jPanel3);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
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

    private void button_refreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refreshActionPerformed
        // TODO add your handling code here:
        refreshTable_Pelanggaran_hp();
    }//GEN-LAST:event_button_refreshActionPerformed

    private void txt_search_keywordKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_keywordKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_Pelanggaran_hp();
        }
    }//GEN-LAST:event_txt_search_keywordKeyPressed

    private void button_export_tdkMasukActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_tdkMasukActionPerformed
        // TODO add your handling code here:
        DefaultTableModel table = (DefaultTableModel) Table_pelanggaran_ph.getModel();
        ExportToExcel.writeToExcel(table, this);
    }//GEN-LAST:event_button_export_tdkMasukActionPerformed

    private void button_refresh_rekapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refresh_rekapActionPerformed
        // TODO add your handling code here:
        refreshTable_rekap_Pelanggaran_hp();
    }//GEN-LAST:event_button_refresh_rekapActionPerformed

    private void txt_search_nama_rekapKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_nama_rekapKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_rekap_Pelanggaran_hp();
        }
    }//GEN-LAST:event_txt_search_nama_rekapKeyPressed

    private void button_export_pelanggaranph_rekapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_pelanggaranph_rekapActionPerformed
        // TODO add your handling code here:
        DefaultTableModel table = (DefaultTableModel) Table_pelanggaran_ph_rekap.getModel();
        ExportToExcel.writeToExcel(table, this);
    }//GEN-LAST:event_button_export_pelanggaranph_rekapActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser Date1;
    private com.toedter.calendar.JDateChooser Date2;
    private com.toedter.calendar.JDateChooser Date_rekap1;
    private com.toedter.calendar.JDateChooser Date_rekap2;
    private javax.swing.JTable Table_pelanggaran_ph;
    private javax.swing.JTable Table_pelanggaran_ph_rekap;
    private javax.swing.JTable Table_pelanggaran_ph_rekap_detail;
    private javax.swing.JButton button_export_pelanggaranph_rekap;
    private javax.swing.JButton button_export_tdkMasuk;
    private javax.swing.JButton button_refresh;
    private javax.swing.JButton button_refresh_rekap;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel label_total_data;
    private javax.swing.JLabel label_total_data_rekap;
    private javax.swing.JTextField txt_search_keyword;
    private javax.swing.JTextField txt_search_nama_rekap;
    // End of variables declaration//GEN-END:variables
}
