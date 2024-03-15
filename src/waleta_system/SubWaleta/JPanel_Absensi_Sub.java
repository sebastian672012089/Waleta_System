package waleta_system.SubWaleta;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.net.InetAddress;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.ExportToExcel;
import waleta_system.Class.Utility;
import waleta_system.MainForm;

public class JPanel_Absensi_Sub extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    PreparedStatement pst;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Date today = new Date();
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    String lihat_absen_staf = "";
    String rekap_sub = "";

    public JPanel_Absensi_Sub() {
        initComponents();
    }

    public void init() {
        label_hari_tanggal.setText("Hari, tanggal : " + new SimpleDateFormat("dd MMM yyyy").format(new Date()));
        refreshTable_absen_sub_hp();
        RekapAbsenSub();
        refreshTable_absen_waleta();
    }

    public void refreshTable_absen_sub_hp() {
        try {
            if (Date_absen_sub1.getDate() == null || Date_absen_sub2.getDate() == null) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih tanggal!");
            } else {
                Utility.db_sub.connect();
                DefaultTableModel model = (DefaultTableModel) tabel_data_absen_sub.getModel();
                model.setRowCount(0);
                String posisi = "AND `jenis_pegawai` = '" + ComboBox_posisi.getSelectedItem().toString() + "'";
                if (ComboBox_posisi.getSelectedItem().toString().equals("All")) {
                    posisi = "";
                }

                sql = "SELECT `sn`, DATE(`scan_date`) AS 'tgl_absen', TIME(`scan_date`) AS 'waktu_absen', `pin`, "
                        + "`verifymode`, `inoutmode`, `device_ip`, `id_pegawai`, `nama_pegawai`, `bagian`, `jenis_pegawai`, `status` "
                        + "FROM `att_log` "
                        + "LEFT JOIN `tb_karyawan` ON `att_log`.`pin` = `tb_karyawan`.`pin_calculated` "
                        + "WHERE "
                        + "`tb_karyawan`.`id_pegawai` LIKE '%" + txt_search_id.getText() + "%' \n"
                        + "AND `nama_pegawai` LIKE '%" + txt_search_NamaKaryawan_sub1.getText() + "%' \n"
                        + "AND `bagian` LIKE '%" + txt_search_bagian1.getText() + "%' \n"
                        + posisi
                        + "AND DATE(`scan_date`) BETWEEN '" + dateFormat.format(Date_absen_sub1.getDate()) + "' AND '" + dateFormat.format(Date_absen_sub2.getDate()) + "'";
                ResultSet result = Utility.db_sub.getStatement().executeQuery(sql);
                Object[] row = new Object[15];
                while (result.next()) {
                    row[0] = result.getString("pin");
                    row[1] = result.getString("id_pegawai");
                    row[2] = result.getString("nama_pegawai");
                    row[3] = result.getString("bagian");
                    row[4] = result.getString("jenis_pegawai");
                    row[5] = result.getDate("tgl_absen");
                    row[6] = result.getTime("waktu_absen");
                    row[7] = result.getString("sn");
                    row[8] = result.getString("verifymode");
                    row[9] = result.getString("inoutmode");
                    row[10] = result.getString("device_ip");
                    row[11] = result.getString("status");
                    model.addRow(row);
                }
                label_total_data_absen_sub.setText(Integer.toString(tabel_data_absen_sub.getRowCount()));
                ColumnsAutoSizer.sizeColumnsToFit(tabel_data_absen_sub);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Absensi_Sub.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_absen_waleta() {
        try {
            if (Date_absen_waleta1.getDate() == null || Date_absen_waleta2.getDate() == null) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih tanggal!");
            } else {
                Utility.db_sub.connect();
                DefaultTableModel model = (DefaultTableModel) tabel_data_absen_waleta.getModel();
                model.setRowCount(0);

                sql = "SELECT `sn`, DATE(`scan_date`) AS 'tgl_absen', TIME(`scan_date`) AS 'waktu_absen', `tb_karyawan`.`pin_calculated` AS 'pin', `verifymode`, `inoutmode`, `device_ip`, `id_pegawai`, `nama_pegawai`, `bagian`, `jenis_pegawai`, `status` "
                        + "FROM `att_log_waleta` "
                        + "LEFT JOIN `tb_karyawan` ON `att_log_waleta`.`pin2` = `tb_karyawan`.`id_pegawai` "
                        + "WHERE "
                        + "`nama_pegawai` LIKE '%" + txt_search_NamaKaryawan_waleta.getText() + "%'"
                        + "AND DATE(`scan_date`) BETWEEN '" + dateFormat.format(Date_absen_waleta1.getDate()) + "' AND '" + dateFormat.format(Date_absen_waleta2.getDate()) + "'";
                rs = Utility.db_sub.getStatement().executeQuery(sql);
                Object[] row = new Object[15];
                while (rs.next()) {
                    row[0] = rs.getString("pin");
                    row[1] = rs.getString("id_pegawai");
                    row[2] = rs.getString("nama_pegawai");
                    row[3] = rs.getString("bagian");
                    row[4] = rs.getString("jenis_pegawai");
                    row[5] = rs.getDate("tgl_absen");
                    row[6] = rs.getTime("waktu_absen");
                    row[7] = rs.getString("sn");
                    row[8] = rs.getString("verifymode");
                    row[9] = rs.getString("inoutmode");
                    row[10] = rs.getString("device_ip");
                    row[11] = rs.getString("status");
                    model.addRow(row);
                }
                label_total_data_absen_waleta.setText(Integer.toString(tabel_data_absen_waleta.getRowCount()));
                ColumnsAutoSizer.sizeColumnsToFit(tabel_data_absen_waleta);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Absensi_Sub.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void RekapAbsenSub() {
        try {
            Utility.db_sub.connect();
            int total_sub = 0;
            DefaultTableModel model = (DefaultTableModel) tabel_sub.getModel();
            model.setRowCount(0);
            rekap_sub = "";
            sql = "SELECT `bagian`, COUNT(DISTINCT(`pin`)) AS 'jumlah_absen' "
                    + "FROM `att_log` "
                    + "LEFT JOIN `tb_karyawan` ON `att_log`.`pin` = `tb_karyawan`.`pin_calculated` "
                    + "WHERE `jenis_pegawai` = 'KARYAWAN' "
                    + "AND DATE(`scan_date`) = CURRENT_DATE "
                    + "GROUP BY `bagian`";
            rs = Utility.db_sub.getStatement().executeQuery(sql);
            while (rs.next()) {
                model.addRow(new Object[]{rs.getString("bagian"), rs.getInt("jumlah_absen")});
                total_sub = total_sub + rs.getInt("jumlah_absen");
                rekap_sub = rekap_sub + rs.getString("bagian") + " : " + rs.getInt("jumlah_absen") + "\n";
            }
            rekap_sub = rekap_sub + "*TOTAL : " + total_sub + "*";
            label_totalSUB.setText(Integer.toString(total_sub));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Absensi_Sub.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_TidakMasuk() {
        try {
            Utility.db_sub.connect();
            int jumlah_tidak_masuk = 0, jumlah_masuk = 0;
            DefaultTableModel model = (DefaultTableModel) tabel_data_TidakMasuk.getModel();
            model.setRowCount(0);
            String status = "AND `status` = '" + ComboBox_status_karyawan_sub.getSelectedItem().toString() + "'";
            if (ComboBox_status_karyawan_sub.getSelectedItem().toString().equals("All")) {
                status = "";
            }

            if (Date_TidakMasuk1.getDate() != null && Date_TidakMasuk2.getDate() != null) {
                Date date = Date_TidakMasuk1.getDate();
                while (date.before(Date_TidakMasuk2.getDate())) {
                    sql = "SELECT `id_pegawai`, A.`pin_calculated` AS 'pin', `nama_pegawai`, `bagian`, `status`, `jenis_pegawai`, `tgl_absen`, `waktu_absen`, `sn`, `verifymode`, `inoutmode`, `device_ip`\n"
                            + "FROM `tb_karyawan` A \n"
                            + "LEFT JOIN (SELECT DATE(`scan_date`) AS 'tgl_absen', TIME(`scan_date`) AS 'waktu_absen', `pin`, `sn`, `verifymode`, `inoutmode`, `device_ip` FROM `att_log` WHERE DATE(`scan_date`) = '" + dateFormat.format(date) + "') B \n"
                            + "ON A.`pin_calculated` = B.`pin`"
                            + "WHERE SUBSTRING(`id_pegawai`, 7, 5) LIKE '%" + txt_search_pin2.getText() + "%' "
                            + "AND `nama_pegawai` LIKE '%" + txt_search_NamaKaryawan2.getText() + "%' "
                            + "AND `bagian` LIKE '%" + txt_search_bagian2.getText() + "%' "
                            + status;
                    rs = Utility.db_sub.getStatement().executeQuery(sql);
                    Object[] row = new Object[15];
                    while (rs.next()) {
                        row[0] = dateFormat.format(date);
                        row[1] = rs.getString("id_pegawai");
                        row[2] = rs.getString("pin");
                        row[3] = rs.getString("nama_pegawai");
                        row[4] = rs.getString("bagian");
                        row[5] = rs.getString("status");
                        row[6] = rs.getString("jenis_pegawai");

                        if (rs.getTime("waktu_absen") != null) {
                            row[7] = rs.getTime("waktu_absen");
                            jumlah_masuk++;
                        } else {
                            row[7] = "Tidak ada Data";
                            jumlah_tidak_masuk++;
                        }
                        model.addRow(row);
                    }
                    date = new Date(date.getTime() + (1 * 24 * 60 * 60 * 1000));
                }
            } else {
                JOptionPane.showMessageDialog(this, "Tanggal tidak boleh kosong !");
            }
            label_total_data.setText(Integer.toString(tabel_data_TidakMasuk.getRowCount()));
            label_total_tidak_masuk.setText(Integer.toString(jumlah_tidak_masuk));
            label_total_masuk.setText(Integer.toString(jumlah_masuk));
            ColumnsAutoSizer.sizeColumnsToFit(tabel_data_TidakMasuk);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Absensi_Sub.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel4 = new javax.swing.JPanel();
        jLabel26 = new javax.swing.JLabel();
        txt_search_NamaKaryawan_sub1 = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        txt_search_id = new javax.swing.JTextField();
        jLabel38 = new javax.swing.JLabel();
        Date_absen_sub1 = new com.toedter.calendar.JDateChooser();
        Date_absen_sub2 = new com.toedter.calendar.JDateChooser();
        button_refresh_absen_sub = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        tabel_data_absen_sub = new javax.swing.JTable();
        jLabel28 = new javax.swing.JLabel();
        label_total_data_absen_sub = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        button_export_absen_sub = new javax.swing.JButton();
        txt_search_bagian1 = new javax.swing.JTextField();
        jLabel31 = new javax.swing.JLabel();
        ComboBox_posisi = new javax.swing.JComboBox<>();
        button_input_absen = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabel_sub = new javax.swing.JTable();
        button_copy_text1 = new javax.swing.JButton();
        label_totalSUB = new javax.swing.JLabel();
        label_hari_tanggal = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel29 = new javax.swing.JLabel();
        txt_search_NamaKaryawan_waleta = new javax.swing.JTextField();
        jLabel40 = new javax.swing.JLabel();
        Date_absen_waleta1 = new com.toedter.calendar.JDateChooser();
        Date_absen_waleta2 = new com.toedter.calendar.JDateChooser();
        button_refresh_absen_waleta = new javax.swing.JButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        tabel_data_absen_waleta = new javax.swing.JTable();
        jLabel33 = new javax.swing.JLabel();
        label_total_data_absen_waleta = new javax.swing.JLabel();
        button_export_absen_waleta = new javax.swing.JButton();
        button_transfer_absen = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        txt_search_NamaKaryawan2 = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txt_search_pin2 = new javax.swing.JTextField();
        jLabel37 = new javax.swing.JLabel();
        Date_TidakMasuk1 = new com.toedter.calendar.JDateChooser();
        Date_TidakMasuk2 = new com.toedter.calendar.JDateChooser();
        button_refresh_data_TidakMasuk = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        tabel_data_TidakMasuk = new javax.swing.JTable();
        jLabel15 = new javax.swing.JLabel();
        label_total_data = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        button_export_tdkMasuk = new javax.swing.JButton();
        jLabel21 = new javax.swing.JLabel();
        label_total_tidak_masuk = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        label_total_masuk = new javax.swing.JLabel();
        txt_search_bagian2 = new javax.swing.JTextField();
        jLabel39 = new javax.swing.JLabel();
        ComboBox_status_karyawan_sub = new javax.swing.JComboBox<>();

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel26.setBackground(new java.awt.Color(255, 255, 255));
        jLabel26.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel26.setText("Nama :");

        txt_search_NamaKaryawan_sub1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_NamaKaryawan_sub1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_NamaKaryawan_sub1KeyPressed(evt);
            }
        });

        jLabel27.setBackground(new java.awt.Color(255, 255, 255));
        jLabel27.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel27.setText("PIN :");

        txt_search_id.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_id.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_idKeyPressed(evt);
            }
        });

        jLabel38.setBackground(new java.awt.Color(255, 255, 255));
        jLabel38.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        jLabel38.setText("Tanggal Absen :");

        Date_absen_sub1.setBackground(new java.awt.Color(255, 255, 255));
        Date_absen_sub1.setToolTipText("");
        Date_absen_sub1.setDate(new Date());
        Date_absen_sub1.setDateFormatString("dd MMMM yyyy");
        Date_absen_sub1.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        Date_absen_sub1.setMinSelectableDate(new java.util.Date(1483207315000L));

        Date_absen_sub2.setBackground(new java.awt.Color(255, 255, 255));
        Date_absen_sub2.setDate(new Date());
        Date_absen_sub2.setDateFormatString("dd MMMM yyyy");
        Date_absen_sub2.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N

        button_refresh_absen_sub.setBackground(new java.awt.Color(255, 255, 255));
        button_refresh_absen_sub.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_refresh_absen_sub.setText("Refresh");
        button_refresh_absen_sub.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refresh_absen_subActionPerformed(evt);
            }
        });

        tabel_data_absen_sub.setAutoCreateRowSorter(true);
        tabel_data_absen_sub.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tabel_data_absen_sub.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "PIN", "ID Pegawai", "Nama", "Bagian", "Posisi", "Tanggal", "Waktu Absen", "Mesin Absen", "Verifikasi", "inout mode", "ip", "Status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
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
        jScrollPane4.setViewportView(tabel_data_absen_sub);

        jLabel28.setBackground(new java.awt.Color(255, 255, 255));
        jLabel28.setText("Total Data :");

        label_total_data_absen_sub.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_absen_sub.setText("0");

        jLabel30.setBackground(new java.awt.Color(255, 255, 255));
        jLabel30.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel30.setText("Bagian :");

        button_export_absen_sub.setBackground(new java.awt.Color(255, 255, 255));
        button_export_absen_sub.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_absen_sub.setText("Export");
        button_export_absen_sub.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_absen_subActionPerformed(evt);
            }
        });

        txt_search_bagian1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_bagian1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_bagian1KeyPressed(evt);
            }
        });

        jLabel31.setBackground(new java.awt.Color(255, 255, 255));
        jLabel31.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel31.setText("Posisi :");

        ComboBox_posisi.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_posisi.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "KARYAWAN", "KEPALA" }));
        ComboBox_posisi.setSelectedIndex(1);

        button_input_absen.setBackground(new java.awt.Color(255, 255, 255));
        button_input_absen.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_input_absen.setText("Input Absen");
        button_input_absen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_input_absenActionPerformed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel3.setText("Jumlah Karyawan masuk SUB");

        tabel_sub.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        tabel_sub.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "SUB", "Absen"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabel_sub.setRowHeight(20);
        tabel_sub.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tabel_sub);

        button_copy_text1.setBackground(new java.awt.Color(255, 255, 255));
        button_copy_text1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_copy_text1.setText("Copy Text");
        button_copy_text1.setToolTipText("Copy text into clipboard");
        button_copy_text1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_copy_text1ActionPerformed(evt);
            }
        });

        label_totalSUB.setBackground(new java.awt.Color(255, 255, 255));
        label_totalSUB.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_totalSUB.setText("00");

        label_hari_tanggal.setBackground(new java.awt.Color(255, 255, 255));
        label_hari_tanggal.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        label_hari_tanggal.setText("Hari, tanggal :");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(label_totalSUB, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_copy_text1))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(label_hari_tanggal))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(label_totalSUB)
                    .addComponent(button_copy_text1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(label_hari_tanggal)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel26)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_NamaKaryawan_sub1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel27)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_id, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel30)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_bagian1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel31)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_posisi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel38)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_absen_sub1, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_absen_sub2, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_refresh_absen_sub)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_export_absen_sub)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel28)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_data_absen_sub))
                            .addComponent(button_input_absen))
                        .addContainerGap(279, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jScrollPane4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4))))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_NamaKaryawan_sub1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_id, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_absen_sub1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_absen_sub2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_bagian1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_refresh_absen_sub, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export_absen_sub, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_total_data_absen_sub, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_posisi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_input_absen, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 595, Short.MAX_VALUE)
                        .addContainerGap())
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        jTabbedPane1.addTab("DATA ABSENSI SUB", jPanel4);

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel29.setBackground(new java.awt.Color(255, 255, 255));
        jLabel29.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel29.setText("Nama :");

        txt_search_NamaKaryawan_waleta.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_NamaKaryawan_waleta.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_NamaKaryawan_waletaKeyPressed(evt);
            }
        });

        jLabel40.setBackground(new java.awt.Color(255, 255, 255));
        jLabel40.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        jLabel40.setText("Tanggal Absen :");

        Date_absen_waleta1.setBackground(new java.awt.Color(255, 255, 255));
        Date_absen_waleta1.setToolTipText("");
        Date_absen_waleta1.setDate(new Date());
        Date_absen_waleta1.setDateFormatString("dd MMMM yyyy");
        Date_absen_waleta1.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        Date_absen_waleta1.setMinSelectableDate(new java.util.Date(1483207315000L));

        Date_absen_waleta2.setBackground(new java.awt.Color(255, 255, 255));
        Date_absen_waleta2.setDate(new Date());
        Date_absen_waleta2.setDateFormatString("dd MMMM yyyy");
        Date_absen_waleta2.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N

        button_refresh_absen_waleta.setBackground(new java.awt.Color(255, 255, 255));
        button_refresh_absen_waleta.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_refresh_absen_waleta.setText("Refresh");
        button_refresh_absen_waleta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refresh_absen_waletaActionPerformed(evt);
            }
        });

        tabel_data_absen_waleta.setAutoCreateRowSorter(true);
        tabel_data_absen_waleta.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tabel_data_absen_waleta.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "PIN", "ID Pegawai", "Nama", "Bagian", "Posisi", "Tanggal", "Waktu Absen", "Mesin Absen", "Verifikasi", "inout mode", "ip", "Status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
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
        jScrollPane5.setViewportView(tabel_data_absen_waleta);

        jLabel33.setBackground(new java.awt.Color(255, 255, 255));
        jLabel33.setText("Total Data :");

        label_total_data_absen_waleta.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_absen_waleta.setText("0");

        button_export_absen_waleta.setBackground(new java.awt.Color(255, 255, 255));
        button_export_absen_waleta.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_absen_waleta.setText("Export");
        button_export_absen_waleta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_absen_waletaActionPerformed(evt);
            }
        });

        button_transfer_absen.setBackground(new java.awt.Color(255, 255, 255));
        button_transfer_absen.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_transfer_absen.setText("Transfer data absen ke waleta");
        button_transfer_absen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_transfer_absenActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel29)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_NamaKaryawan_waleta, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel40)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_absen_waleta1, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_absen_waleta2, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_refresh_absen_waleta)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_export_absen_waleta)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel33)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_data_absen_waleta))
                            .addComponent(button_transfer_absen))
                        .addGap(0, 675, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_NamaKaryawan_waleta, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_absen_waleta1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_absen_waleta2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_refresh_absen_waleta, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export_absen_waleta, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_total_data_absen_waleta, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_transfer_absen, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 595, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("DATA ABSENSI KARYAWAN WALETA", jPanel5);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel5.setText("Nama :");

        txt_search_NamaKaryawan2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_NamaKaryawan2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_NamaKaryawan2KeyPressed(evt);
            }
        });

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel10.setText("PIN :");

        txt_search_pin2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_pin2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_pin2KeyPressed(evt);
            }
        });

        jLabel37.setBackground(new java.awt.Color(255, 255, 255));
        jLabel37.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        jLabel37.setText("Tanggal :");

        Date_TidakMasuk1.setBackground(new java.awt.Color(255, 255, 255));
        Date_TidakMasuk1.setToolTipText("");
        Date_TidakMasuk1.setDate(new Date());
        Date_TidakMasuk1.setDateFormatString("dd MMMM yyyy");
        Date_TidakMasuk1.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        Date_TidakMasuk1.setMinSelectableDate(new java.util.Date(1483207315000L));

        Date_TidakMasuk2.setBackground(new java.awt.Color(255, 255, 255));
        Date_TidakMasuk2.setDate(new Date());
        Date_TidakMasuk2.setDateFormatString("dd MMMM yyyy");
        Date_TidakMasuk2.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N

        button_refresh_data_TidakMasuk.setBackground(new java.awt.Color(255, 255, 255));
        button_refresh_data_TidakMasuk.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_refresh_data_TidakMasuk.setText("Refresh");
        button_refresh_data_TidakMasuk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refresh_data_TidakMasukActionPerformed(evt);
            }
        });

        tabel_data_TidakMasuk.setAutoCreateRowSorter(true);
        tabel_data_TidakMasuk.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tabel_data_TidakMasuk.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tanggal", "ID Pegawai", "PIN", "Nama", "Bagian", "status", "Posisi", "Jam Absen"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class
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
        tabel_data_TidakMasuk.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(tabel_data_TidakMasuk);

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel15.setText("Total Data :");

        label_total_data.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        label_total_data.setText("0");

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel12.setText("Bagian :");

        button_export_tdkMasuk.setBackground(new java.awt.Color(255, 255, 255));
        button_export_tdkMasuk.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_tdkMasuk.setText("Export");
        button_export_tdkMasuk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_tdkMasukActionPerformed(evt);
            }
        });

        jLabel21.setBackground(new java.awt.Color(255, 255, 255));
        jLabel21.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(255, 0, 0));
        jLabel21.setText("Jumlah Tidak Masuk:");

        label_total_tidak_masuk.setBackground(new java.awt.Color(255, 255, 255));
        label_total_tidak_masuk.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        label_total_tidak_masuk.setForeground(new java.awt.Color(255, 0, 0));
        label_total_tidak_masuk.setText("0");

        jLabel22.setBackground(new java.awt.Color(255, 255, 255));
        jLabel22.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(0, 255, 0));
        jLabel22.setText("Jumlah Masuk:");

        label_total_masuk.setBackground(new java.awt.Color(255, 255, 255));
        label_total_masuk.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        label_total_masuk.setForeground(new java.awt.Color(0, 255, 0));
        label_total_masuk.setText("0");

        txt_search_bagian2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_bagian2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_bagian2KeyPressed(evt);
            }
        });

        jLabel39.setBackground(new java.awt.Color(255, 255, 255));
        jLabel39.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        jLabel39.setText("Status :");

        ComboBox_status_karyawan_sub.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_status_karyawan_sub.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "IN-SUB", "OUT-SUB" }));
        ComboBox_status_karyawan_sub.setSelectedIndex(1);

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
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_data)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel21)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_tidak_masuk)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel22)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_masuk))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_NamaKaryawan2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_pin2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_bagian2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel37)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_TidakMasuk1, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_TidakMasuk2, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel39)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_status_karyawan_sub, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_refresh_data_TidakMasuk)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_export_tdkMasuk)))
                        .addGap(0, 330, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_refresh_data_TidakMasuk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_TidakMasuk2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_TidakMasuk1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_bagian2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_pin2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export_tdkMasuk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_NamaKaryawan2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_status_karyawan_sub, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 595, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_total_masuk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_total_data, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_tidak_masuk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jTabbedPane1.addTab("DATA TIDAK MASUK / BELUM ABSEN", jPanel3);

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

    private void txt_search_NamaKaryawan2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_NamaKaryawan2KeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_TidakMasuk();
        }
    }//GEN-LAST:event_txt_search_NamaKaryawan2KeyPressed

    private void txt_search_pin2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_pin2KeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_TidakMasuk();
        }
    }//GEN-LAST:event_txt_search_pin2KeyPressed

    private void button_refresh_data_TidakMasukActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refresh_data_TidakMasukActionPerformed
        // TODO add your handling code here:
        refreshTable_TidakMasuk();
    }//GEN-LAST:event_button_refresh_data_TidakMasukActionPerformed

    private void button_export_tdkMasukActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_tdkMasukActionPerformed
        // TODO add your handling code here:
        DefaultTableModel table = (DefaultTableModel) tabel_data_TidakMasuk.getModel();
        ExportToExcel.writeToExcel(table, this);
    }//GEN-LAST:event_button_export_tdkMasukActionPerformed

    private void txt_search_NamaKaryawan_sub1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_NamaKaryawan_sub1KeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_absen_sub_hp();
        }
    }//GEN-LAST:event_txt_search_NamaKaryawan_sub1KeyPressed

    private void txt_search_idKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_idKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_absen_sub_hp();
        }
    }//GEN-LAST:event_txt_search_idKeyPressed

    private void button_refresh_absen_subActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refresh_absen_subActionPerformed
        // TODO add your handling code here:
        refreshTable_absen_sub_hp();
    }//GEN-LAST:event_button_refresh_absen_subActionPerformed

    private void button_export_absen_subActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_absen_subActionPerformed
        // TODO add your handling code here:
        DefaultTableModel table = (DefaultTableModel) tabel_data_absen_sub.getModel();
        ExportToExcel.writeToExcel(table, this);
    }//GEN-LAST:event_button_export_absen_subActionPerformed

    private void txt_search_bagian1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_bagian1KeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_absen_sub_hp();
        }
    }//GEN-LAST:event_txt_search_bagian1KeyPressed

    private void txt_search_bagian2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_bagian2KeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_TidakMasuk();
        }
    }//GEN-LAST:event_txt_search_bagian2KeyPressed

    private void button_input_absenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_input_absenActionPerformed
        // TODO add your handling code here:
        JDialog_input_absen keluar = new JDialog_input_absen(new javax.swing.JFrame(), true);
        keluar.pack();
        keluar.setLocationRelativeTo(this);
        keluar.setVisible(true);
        keluar.setEnabled(true);
        refreshTable_absen_sub_hp();
    }//GEN-LAST:event_button_input_absenActionPerformed

    private void button_transfer_absenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_transfer_absenActionPerformed
        // TODO add your handling code here:
        boolean check = true;
        for (int i = 0; i < tabel_data_absen_waleta.getRowCount(); i++) {
            if (!tabel_data_absen_waleta.getValueAt(i, 1).toString().substring(0, 2).equals("20")) {//semua yang id karyawan depannya 20
                JOptionPane.showMessageDialog(this, "Maaf, hanya bisa memasukkan data absen karyawan yang memiliki ID KARYAWAN WALETA.");
                check = false;
                break;
            }
        }
        if (check) {
            int dialogResult = JOptionPane.showConfirmDialog(this, "Semua data absen pada tabel akan dimasukkan ke data absen waleta.\nLanjutkan?", "Warning", 0);
            if (dialogResult == JOptionPane.YES_OPTION) {
                try {
                    InetAddress inetAddress = InetAddress.getLocalHost();
                    Utility.db.getConnection().setAutoCommit(false);
                    int data_masuk = 0;
                    for (int i = 0; i < tabel_data_absen_waleta.getRowCount(); i++) {
                        String sn = MainForm.Login_NamaPegawai;
                        String scan_date = tabel_data_absen_waleta.getValueAt(i, 5).toString() + " " + tabel_data_absen_waleta.getValueAt(i, 6).toString();
                        String pin = tabel_data_absen_waleta.getValueAt(i, 0).toString();
                        String verifymode = tabel_data_absen_waleta.getValueAt(i, 8).toString();
                        String inoutmode = tabel_data_absen_waleta.getValueAt(i, 9).toString();
                        String device_ip = inetAddress.getHostAddress();
                        sql = "INSERT IGNORE INTO `att_log`(`sn`, `scan_date`, `pin`, `verifymode`, `inoutmode`, `device_ip`) "
                                + "VALUES ("
                                + "'" + sn + "',"
                                + "'" + scan_date + "',"
                                + "'" + pin + "',"
                                + "'" + verifymode + "',"
                                + "'" + inoutmode + "',"
                                + "'" + device_ip + "'"
                                + ")";
                        Utility.db.getConnection().createStatement();
                        if ((Utility.db.getStatement().executeUpdate(sql)) == 1) {
                            data_masuk++;
                        }
                    }
                    Utility.db.getConnection().commit();
                    JOptionPane.showMessageDialog(this, "Data berhasil masuk : " + data_masuk);
                } catch (Exception e) {
                    try {
                        Utility.db.getConnection().rollback();
                    } catch (SQLException ex) {
                        Logger.getLogger(JPanel_Absensi_Sub.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    JOptionPane.showMessageDialog(this, e);
                    Logger.getLogger(JPanel_Absensi_Sub.class.getName()).log(Level.SEVERE, null, e);
                } finally {
                    try {
                        Utility.db.getConnection().setAutoCommit(true);
                    } catch (SQLException ex) {
                        Logger.getLogger(JPanel_Absensi_Sub.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }//GEN-LAST:event_button_transfer_absenActionPerformed

    private void button_copy_text1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_copy_text1ActionPerformed
        // TODO add your handling code here:
        String myString = "*Jumlah Karyawan PRODUKSI SUB masuk*\n"
                + label_hari_tanggal.getText() + "\n"
                + rekap_sub;
        StringSelection stringSelection = new StringSelection(myString);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }//GEN-LAST:event_button_copy_text1ActionPerformed

    private void txt_search_NamaKaryawan_waletaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_NamaKaryawan_waletaKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_absen_waleta();
        }
    }//GEN-LAST:event_txt_search_NamaKaryawan_waletaKeyPressed

    private void button_refresh_absen_waletaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refresh_absen_waletaActionPerformed
        // TODO add your handling code here:
        refreshTable_absen_waleta();
    }//GEN-LAST:event_button_refresh_absen_waletaActionPerformed

    private void button_export_absen_waletaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_absen_waletaActionPerformed
        // TODO add your handling code here:
        DefaultTableModel table = (DefaultTableModel) tabel_data_absen_waleta.getModel();
        ExportToExcel.writeToExcel(table, this);
    }//GEN-LAST:event_button_export_absen_waletaActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_posisi;
    private javax.swing.JComboBox<String> ComboBox_status_karyawan_sub;
    private com.toedter.calendar.JDateChooser Date_TidakMasuk1;
    private com.toedter.calendar.JDateChooser Date_TidakMasuk2;
    private com.toedter.calendar.JDateChooser Date_absen_sub1;
    private com.toedter.calendar.JDateChooser Date_absen_sub2;
    private com.toedter.calendar.JDateChooser Date_absen_waleta1;
    private com.toedter.calendar.JDateChooser Date_absen_waleta2;
    private javax.swing.JButton button_copy_text1;
    private javax.swing.JButton button_export_absen_sub;
    private javax.swing.JButton button_export_absen_waleta;
    private javax.swing.JButton button_export_tdkMasuk;
    private javax.swing.JButton button_input_absen;
    private javax.swing.JButton button_refresh_absen_sub;
    private javax.swing.JButton button_refresh_absen_waleta;
    private javax.swing.JButton button_refresh_data_TidakMasuk;
    private javax.swing.JButton button_transfer_absen;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel label_hari_tanggal;
    private javax.swing.JLabel label_totalSUB;
    private javax.swing.JLabel label_total_data;
    private javax.swing.JLabel label_total_data_absen_sub;
    private javax.swing.JLabel label_total_data_absen_waleta;
    private javax.swing.JLabel label_total_masuk;
    private javax.swing.JLabel label_total_tidak_masuk;
    private javax.swing.JTable tabel_data_TidakMasuk;
    private javax.swing.JTable tabel_data_absen_sub;
    private javax.swing.JTable tabel_data_absen_waleta;
    private javax.swing.JTable tabel_sub;
    private javax.swing.JTextField txt_search_NamaKaryawan2;
    private javax.swing.JTextField txt_search_NamaKaryawan_sub1;
    private javax.swing.JTextField txt_search_NamaKaryawan_waleta;
    private javax.swing.JTextField txt_search_bagian1;
    private javax.swing.JTextField txt_search_bagian2;
    private javax.swing.JTextField txt_search_id;
    private javax.swing.JTextField txt_search_pin2;
    // End of variables declaration//GEN-END:variables
}
