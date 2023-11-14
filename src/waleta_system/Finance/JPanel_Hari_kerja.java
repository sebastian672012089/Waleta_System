package waleta_system.Finance;

import waleta_system.Class.Utility;
import java.awt.event.KeyEvent;
import java.sql.PreparedStatement;
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

public class JPanel_Hari_kerja extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    PreparedStatement pst;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Date today = new Date();
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    DefaultTableModel dm = new DefaultTableModel();

    public JPanel_Hari_kerja() {
        initComponents();
    }

    public void init() {
        try {
            ComboBox_departemen_karyawan.removeAllItems();
            ComboBox_departemen_karyawan.addItem("All");
            sql = "SELECT `kode_dep` FROM `tb_departemen` ORDER BY `kode_dep`";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_departemen_karyawan.addItem(rs.getString("kode_dep"));
            }

            ComboBox_posisi.removeAllItems();
            ComboBox_posisi.addItem("All");
            sql = "SELECT DISTINCT(`posisi`) AS 'posisi' FROM `tb_karyawan` WHERE `status` = 'IN' AND `posisi` IS NOT NULL";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_posisi.addItem(rs.getString("posisi"));
            }
            ComboBox_status_karyawan.removeAllItems();
            ComboBox_status_karyawan.addItem("All");
            sql = "SELECT DISTINCT(`status`) AS 'status' FROM `tb_karyawan` WHERE `status` IS NOT NULL";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_status_karyawan.addItem(rs.getString("status"));
            }
            ComboBox_status_karyawan.setSelectedItem("IN");

            tabel_hari_kerja.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent event) {
                    if (!event.getValueIsAdjusting() && tabel_hari_kerja.getSelectedRow() != -1) {
                        int x = tabel_hari_kerja.getSelectedRow();
                        if (x > -1) {
                            label_nama.setText(tabel_hari_kerja.getValueAt(x, 2).toString());
                            String pin = tabel_hari_kerja.getValueAt(x, 1).toString();
                            refreshTable_Detail(pin);
                        }
                    }
                }
            });

        } catch (SQLException ex) {
            Logger.getLogger(JPanel_Hari_kerja.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(JPanel_Hari_kerja.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable() {
        try {
            DefaultTableModel model = (DefaultTableModel) tabel_hari_kerja.getModel();
            model.setRowCount(0);
            String bagian = "AND `nama_bagian` = '" + ComboBox_bagian_karyawan.getSelectedItem().toString() + "' ";
            String departemen = "AND `kode_departemen` = '" + ComboBox_departemen_karyawan.getSelectedItem().toString() + "' ";
            String Status = "AND `status` = '" + ComboBox_status_karyawan.getSelectedItem().toString() + "' ";
            String kelamin = "AND `jenis_kelamin` = '" + ComboBox_gender.getSelectedItem().toString() + "' ";
            String posisi = "AND `posisi` = '" + ComboBox_posisi.getSelectedItem().toString() + "' ";
            if ("All".equals(ComboBox_status_karyawan.getSelectedItem().toString())) {
                Status = "";
            }
            if ("All".equals(ComboBox_bagian_karyawan.getSelectedItem().toString())) {
                bagian = "";
            }
            if ("All".equals(ComboBox_departemen_karyawan.getSelectedItem().toString())) {
                departemen = "";
            }
            if ("All".equals(ComboBox_gender.getSelectedItem().toString())) {
                kelamin = "";
            }
            if ("All".equals(ComboBox_posisi.getSelectedItem().toString())) {
                posisi = "";
            }
            sql = "SELECT `id_pegawai`,`pin_finger`,`nama_pegawai`,`nama_bagian`,`kode_departemen`, `posisi` "
                    + "FROM `tb_karyawan` "
                    + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`"
                    + "WHERE `nama_pegawai` LIKE '%" + txt_search_karyawan.getText() + "%' "
                    + "AND `nik_ktp` LIKE '%" + txt_search_nik.getText() + "%' "
                    + bagian
                    + departemen
                    + kelamin
                    + Status
                    + posisi;

            pst = Utility.db.getConnection().prepareStatement(sql);
            rs = pst.executeQuery();
            Object[] row = new Object[9];
            while (rs.next()) {
                row[0] = rs.getString("id_pegawai");
                row[1] = rs.getString("pin_finger");
                row[2] = rs.getString("nama_pegawai");
                row[3] = rs.getString("nama_bagian");
                row[4] = rs.getString("kode_departemen");
                row[5] = rs.getString("posisi");
                String staff = rs.getString("posisi");

                int premi = 0, transport = 0, libur = 0;
                String query = "SELECT `pin`, `tb_libur`.`keterangan`, DAYNAME(DATE(`scan_date`)) AS 'hari', DATE(`scan_date`) AS 'tanggal', "
                        + "HOUR(TIMEDIFF(MAX(TIME(`scan_date`)),MIN(TIME(`scan_date`)))) AS 'lama_kerja' \n"
                        + "FROM `att_log` LEFT JOIN `tb_karyawan` ON `att_log`.`pin` = `tb_karyawan`.`pin_finger` \n"
                        + "LEFT JOIN `tb_libur` ON DATE(`att_log`.`scan_date`) = `tb_libur`.`tanggal_libur` "
                        + "WHERE `pin`='" + rs.getString("pin_finger") + "' "
                        + "AND DATE(`scan_date`) BETWEEN '" + dateFormat.format(Date1.getDate()) + "' AND '" + dateFormat.format(Date2.getDate()) + "'\n"
                        + "GROUP BY DATE(`scan_date`)";
                PreparedStatement pst1 = Utility.db.getConnection().prepareStatement(query);
                ResultSet result = pst1.executeQuery();
                while (result.next()) {
                    if (result.getString("hari").equals("Saturday")) {
                        if (result.getInt("lama_kerja") >= 4) {
                            premi++;
                            transport++;
                        } else {
                            transport++;
                        }
                    } else {
                        if (result.getInt("lama_kerja") >= 6) {
                            premi++;
                            transport++;
                        } else {
                            transport++;
                        }
                    }
                    if (result.getString("keterangan") != null) {
                        libur++;
                    } else {
                        if (staff.equals("STAFF 5") && (result.getString("hari").equals("Saturday") || result.getString("hari").equals("Sunday"))) {
                            libur++;
                        } else if (result.getString("hari").equals("Sunday")) {
                            libur++;
                        }
                    }
                }
                row[6] = premi;
                row[7] = transport;
                row[8] = libur;
                model.addRow(row);

            }
            ColumnsAutoSizer.sizeColumnsToFit(tabel_hari_kerja);
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_Hari_kerja.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_Detail(String pin) {
        try {
            DefaultTableModel model = (DefaultTableModel) Tabel_detail.getModel();
            model.setRowCount(0);
            String query = "SELECT `nama_pegawai`, `pin`, `tb_libur`.`keterangan`, DAYNAME(DATE(`scan_date`)) AS 'hari', DATE(`scan_date`) AS 'tanggal', MAX(TIME(`scan_date`)) AS 'pulang', MIN(TIME(`scan_date`)) AS 'masuk', TIMEDIFF(MAX(TIME(`scan_date`)),MIN(TIME(`scan_date`))) AS 'lama_kerja' \n"
                    + "FROM `att_log` LEFT JOIN `tb_karyawan` ON `att_log`.`pin` = `tb_karyawan`.`pin_finger`\n"
                    + "LEFT JOIN `tb_libur` ON DATE(`att_log`.`scan_date`) = `tb_libur`.`tanggal_libur` "
                    + "WHERE `pin`='" + pin + "' "
                    + "AND DATE(`scan_date`) BETWEEN '" + dateFormat.format(Date1.getDate()) + "' AND '" + dateFormat.format(Date2.getDate()) + "'\n"
                    + "GROUP BY DATE(`scan_date`)";
            PreparedStatement pst1 = Utility.db.getConnection().prepareStatement(query);
            ResultSet result = pst1.executeQuery();
            Object[] row = new Object[7];
            while (result.next()) {
                row[0] = result.getString("hari");
                row[1] = result.getDate("tanggal");
                row[2] = result.getString("nama_pegawai");
                row[3] = result.getString("masuk");
                row[4] = result.getString("pulang");
                row[5] = result.getString("lama_kerja");
                row[6] = result.getString("keterangan");
                model.addRow(row);
            }
            label_total_data_detail.setText(Integer.toString(Tabel_detail.getRowCount()));
            ColumnsAutoSizer.sizeColumnsToFit(Tabel_detail);
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_Hari_kerja.class.getName()).log(Level.SEVERE, null, ex);
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

        jPanel2 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabel_hari_kerja = new javax.swing.JTable();
        Date1 = new com.toedter.calendar.JDateChooser();
        Date2 = new com.toedter.calendar.JDateChooser();
        jPanel_search_karyawan = new javax.swing.JPanel();
        txt_search_karyawan = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txt_search_nik = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        ComboBox_status_karyawan = new javax.swing.JComboBox<>();
        jLabel14 = new javax.swing.JLabel();
        ComboBox_gender = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        ComboBox_bagian_karyawan = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        ComboBox_posisi = new javax.swing.JComboBox<>();
        ComboBox_departemen_karyawan = new javax.swing.JComboBox<>();
        button_refresh = new javax.swing.JButton();
        button_export_data_karyawan_masuk = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        Tabel_detail = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        label_nama = new javax.swing.JLabel();
        label_total_data_detail = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel13.setText("Range Tanggal :");

        jLabel34.setBackground(new java.awt.Color(255, 255, 255));
        jLabel34.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        jLabel34.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel34.setText("-");

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel1.setText("JUMLAH HARI KERJA");

        tabel_hari_kerja.setAutoCreateRowSorter(true);
        tabel_hari_kerja.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        tabel_hari_kerja.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Pegawai", "PIN", "Nama", "Bagian", "Departemen", "Posisi", "Premi", "Transport", "Libur"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class
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
        tabel_hari_kerja.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tabel_hari_kerja);

        Date1.setBackground(new java.awt.Color(255, 255, 255));
        Date1.setToolTipText("");
        Date1.setDateFormatString("dd MMMM yyyy");
        Date1.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        Date1.setMinSelectableDate(new java.util.Date(1483207315000L));

        Date2.setBackground(new java.awt.Color(255, 255, 255));
        Date2.setDateFormatString("dd MMMM yyyy");
        Date2.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N

        jPanel_search_karyawan.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_search_karyawan.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Filter", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 12))); // NOI18N

        txt_search_karyawan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_karyawan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_karyawanKeyPressed(evt);
            }
        });

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel7.setText("Nama Karyawan :");

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel8.setText("NIK KTP :");

        txt_search_nik.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_nik.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_nikKeyPressed(evt);
            }
        });

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel11.setText("Status :");

        ComboBox_status_karyawan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_status_karyawan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel14.setText("Jenis Kelamin :");

        ComboBox_gender.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_gender.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "PEREMPUAN", "LAKI-LAKI" }));

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel9.setText("Departemen :");

        ComboBox_bagian_karyawan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_bagian_karyawan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel10.setText("Bagian :");

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel15.setText("Posisi :");

        ComboBox_posisi.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_posisi.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        ComboBox_departemen_karyawan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_departemen_karyawan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));
        ComboBox_departemen_karyawan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ComboBox_departemen_karyawanActionPerformed(evt);
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
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_search_karyawanLayout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_nik, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_search_karyawanLayout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_departemen_karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_search_karyawanLayout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_bagian_karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_search_karyawanLayout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_gender, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_search_karyawanLayout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_status_karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_search_karyawanLayout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_posisi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel_search_karyawanLayout.setVerticalGroup(
            jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_search_karyawanLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_search_karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_search_nik, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(ComboBox_gender, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_departemen_karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_bagian_karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_posisi, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(ComboBox_status_karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        button_refresh.setBackground(new java.awt.Color(255, 255, 255));
        button_refresh.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_refresh.setText("Refresh");
        button_refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refreshActionPerformed(evt);
            }
        });

        button_export_data_karyawan_masuk.setBackground(new java.awt.Color(255, 255, 255));
        button_export_data_karyawan_masuk.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        button_export_data_karyawan_masuk.setText("Export to Excel");
        button_export_data_karyawan_masuk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_data_karyawan_masukActionPerformed(evt);
            }
        });

        Tabel_detail.setAutoCreateRowSorter(true);
        Tabel_detail.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Tabel_detail.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Hari", "Tanggal", "Nama", "Jam Masuk", "Jam Pulang", "Jam Kerja", "Libur"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
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
        Tabel_detail.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(Tabel_detail);

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel2.setText("Detail hari kerja");

        label_nama.setBackground(new java.awt.Color(255, 255, 255));
        label_nama.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_nama.setForeground(new java.awt.Color(255, 0, 0));
        label_nama.setText("NAMA");

        label_total_data_detail.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_detail.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_data_detail.setText("TOTAL DATA");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 803, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jPanel_search_karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 537, Short.MAX_VALUE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_nama)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(label_total_data_detail))))
                    .addComponent(jLabel1)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date1, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date2, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_refresh)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_export_data_karyawan_masuk)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(Date2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(Date1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_export_data_karyawan_masuk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_refresh, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 653, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jPanel_search_karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(label_nama)
                            .addComponent(label_total_data_detail))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txt_search_karyawanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_karyawanKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (Date1.getDate() != null && Date2.getDate() != null) {
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, "Silahkan masukkan Range Tanggal");
            }
        }
    }//GEN-LAST:event_txt_search_karyawanKeyPressed

    private void button_refreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refreshActionPerformed
        // TODO add your handling code here:
        if (Date1.getDate() != null && Date2.getDate() != null) {
            refreshTable();
        } else {
            JOptionPane.showMessageDialog(this, "Silahkan masukkan Range Tanggal");
        }
    }//GEN-LAST:event_button_refreshActionPerformed

    private void txt_search_nikKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_nikKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (Date1.getDate() != null && Date2.getDate() != null) {
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, "Silahkan masukkan Range Tanggal");
            }
        }
    }//GEN-LAST:event_txt_search_nikKeyPressed

    private void button_export_data_karyawan_masukActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_data_karyawan_masukActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) tabel_hari_kerja.getModel();
        ExportToExcel.writeToExcel(model, this);
    }//GEN-LAST:event_button_export_data_karyawan_masukActionPerformed

    private void ComboBox_departemen_karyawanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ComboBox_departemen_karyawanActionPerformed
        // TODO add your handling code here:
        try {
            ComboBox_bagian_karyawan.removeAllItems();
            String query = "SELECT `nama_bagian` FROM `tb_bagian` ORDER BY `nama_bagian`";
            if (ComboBox_departemen_karyawan.getSelectedItem() != "All") {
                query = "SELECT `nama_bagian` FROM `tb_bagian` WHERE `kode_departemen`='" + ComboBox_departemen_karyawan.getSelectedItem() + "'";
            }
            rs = Utility.db.getStatement().executeQuery(query);
            ComboBox_bagian_karyawan.addItem("All");
            while (rs.next()) {
                ComboBox_bagian_karyawan.addItem(rs.getString("nama_bagian"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e);
        }
    }//GEN-LAST:event_ComboBox_departemen_karyawanActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_bagian_karyawan;
    private javax.swing.JComboBox<String> ComboBox_departemen_karyawan;
    private javax.swing.JComboBox<String> ComboBox_gender;
    private javax.swing.JComboBox<String> ComboBox_posisi;
    private javax.swing.JComboBox<String> ComboBox_status_karyawan;
    private com.toedter.calendar.JDateChooser Date1;
    private com.toedter.calendar.JDateChooser Date2;
    private javax.swing.JTable Tabel_detail;
    private javax.swing.JButton button_export_data_karyawan_masuk;
    public static javax.swing.JButton button_refresh;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel_search_karyawan;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel label_nama;
    private javax.swing.JLabel label_total_data_detail;
    private javax.swing.JTable tabel_hari_kerja;
    private javax.swing.JTextField txt_search_karyawan;
    private javax.swing.JTextField txt_search_nik;
    // End of variables declaration//GEN-END:variables
}
