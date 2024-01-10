package waleta_system.SubWaleta;

import java.awt.HeadlessException;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;
import waleta_system.Class.ExportToExcel;
import waleta_system.HRD.JDialog_Show_KTP;
import waleta_system.HRD.JDialog_karyawan_keluar_masuk;

public class JPanel_Data_Karyawan_Sub extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    PreparedStatement pst;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Date today = new Date();
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    DefaultTableModel dm = new DefaultTableModel();
    JFileChooser chooser = new JFileChooser();

    public JPanel_Data_Karyawan_Sub() {
        initComponents();
        //Hijack the keyboard manager
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new KeyEventDispatcher() {
            public boolean dispatchKeyEvent(KeyEvent e) {
                if (e.getID() == KeyEvent.KEY_PRESSED) {
                    if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_N) {
                        button_tambah_data_onlineActionPerformed(null);
                    }
                }
                return false;
            }
        });
    }

    public void init() {
        refreshTabel_online();
    }

    public String generate_Nama(String nama, String nik, String Desa) {
        String nama_baru = "";
        boolean nik_belum_ada = true;
        try {
            String query = "SELECT `nama_pegawai` FROM `tb_karyawan` "
                    + "WHERE `nik_ktp` = " + nik + "'";
            ResultSet result = Utility.db.getStatement().executeQuery(query);
            if (result.next()) {
                nik_belum_ada = false;
                nama_baru = result.getString("nama_pegawai");
            }

            if (nik_belum_ada) {
                query = "SELECT `nama_pegawai`, MAX(SUBSTRING(`nama_pegawai`, " + (nama.length() + 2) + ", 2)+0) AS 'nomor' FROM `tb_karyawan`  "
                        + "WHERE `status` LIKE '%SUB' AND `nama_pegawai` LIKE '" + nama + "%'";
                result = Utility.db.getStatement().executeQuery(query);
                if (result.next()) {
                    if (result.getString("nama_pegawai") == null) {
                        nama_baru = nama;
                    } else if (result.getInt("nomor") == 0) {
                        String query_check = "SELECT `nama_pegawai` FROM `tb_karyawan` WHERE `nama_pegawai` = '" + nama + "'";
                        ResultSet result_check = Utility.db.getStatement().executeQuery(query_check);
                        if (result_check.next()) {
                            nama_baru = nama + " 2 " + Desa;
                            JOptionPane.showMessageDialog(this, nama_baru + " teridentifikasi nomor 2, harap menambahkan untuk yang nomor 1");
                        } else {
                            nama_baru = nama;
                        }
                    } else {
                        nama_baru = nama + " " + (result.getInt("nomor") + 1) + " " + Desa;
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_Data_Karyawan_Sub.class.getName()).log(Level.SEVERE, null, ex);
        }
        return nama_baru;
    }

    public void refreshTabel_online() {
        try {
            Utility.db_sub.connect();
            DefaultTableModel model = (DefaultTableModel) table_data_online.getModel();
            model.setRowCount(0);
            String bagian = "AND `bagian` LIKE '%" + txt_search_bagian.getText() + "%' ";
            if (txt_search_bagian.getText() == null || txt_search_bagian.getText().equals("")) {
                bagian = "";
            }
            String Status = "AND `status` = '" + ComboBox_status.getSelectedItem().toString() + "' ";
            if ("All".equals(ComboBox_status.getSelectedItem().toString())) {
                Status = "";
            }
            String query = "SELECT `id_pegawai`, `nama_pegawai`, `bagian`, `status`, `email`, `tgl_lahir`, `jenis_kelamin`, `no_hp`, `bagian`, `jenis_pegawai`, `level_gaji`, `login_username`, `login_pin`, `tanggal_masuk`, `tanggal_keluar`, `saldo`, `foto_ktp`  "
                    + "FROM `tb_karyawan` "
                    + "WHERE `nama_pegawai` LIKE '%" + txt_search_karyawan.getText() + "%' "
                    + "AND `id_pegawai` LIKE '%" + txt_search_id.getText() + "%' "
                    + bagian + Status;
            rs = Utility.db_sub.getStatement().executeQuery(query);
            Object[] row = new Object[20];
            while (rs.next()) {
                row[0] = rs.getString("id_pegawai");
                row[1] = rs.getString("nama_pegawai");
                row[2] = rs.getString("bagian");
                row[3] = rs.getString("status");
                row[4] = rs.getString("email");
                row[5] = rs.getDate("tgl_lahir");
                row[6] = rs.getString("jenis_kelamin");
                row[7] = rs.getString("no_hp");
                row[8] = rs.getString("jenis_pegawai");
                row[9] = rs.getString("level_gaji");
                row[10] = rs.getString("login_username");
                row[11] = rs.getString("login_pin");
                row[12] = rs.getString("tanggal_masuk");
                row[13] = rs.getDate("tanggal_keluar");
                row[14] = 0;
                if (rs.getString("foto_ktp") == null || rs.getString("foto_ktp").equals("")) {
                    row[15] = false;
                } else {
                    row[15] = true;
                }

                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(table_data_online);
            label_total_data_online.setText(Integer.toString(table_data_online.getRowCount()));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_Data_Karyawan_Sub.class.getName()).log(Level.SEVERE, null, e);
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
        txt_search_karyawan = new javax.swing.JTextField();
        button_search_karyawan = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        Date_Search1 = new com.toedter.calendar.JDateChooser();
        Date_Search2 = new com.toedter.calendar.JDateChooser();
        jLabel8 = new javax.swing.JLabel();
        txt_search_id = new javax.swing.JTextField();
        ComboBox_filter_tanggal = new javax.swing.JComboBox<>();
        ComboBox_status = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        txt_search_bagian = new javax.swing.JTextField();
        label_total_data_online = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        button_level_gaji_sub = new javax.swing.JButton();
        button_status_OUT_online = new javax.swing.JButton();
        button_edit_data_online = new javax.swing.JButton();
        button_list_sub_online = new javax.swing.JButton();
        button_lihat_KTP_online = new javax.swing.JButton();
        button_tambah_data_online = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        table_data_online = new javax.swing.JTable();
        button_export_data_online = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));
        setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        setPreferredSize(new java.awt.Dimension(1366, 700));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Data Karyawan Sub Online", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N

        txt_search_karyawan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_karyawan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_karyawanKeyPressed(evt);
            }
        });

        button_search_karyawan.setBackground(new java.awt.Color(255, 255, 255));
        button_search_karyawan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search_karyawan.setText("Search");
        button_search_karyawan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_search_karyawanActionPerformed(evt);
            }
        });

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("Nama :");

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel6.setText("Bagian :");

        Date_Search1.setBackground(new java.awt.Color(255, 255, 255));
        Date_Search1.setToolTipText("");
        Date_Search1.setDateFormatString("dd MMMM yyyy");
        Date_Search1.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        Date_Search1.setMinSelectableDate(new java.util.Date(1483207315000L));

        Date_Search2.setBackground(new java.awt.Color(255, 255, 255));
        Date_Search2.setDateFormatString("dd MMMM yyyy");
        Date_Search2.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel8.setText("ID Pegawai :");

        txt_search_id.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_id.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_idKeyPressed(evt);
            }
        });

        ComboBox_filter_tanggal.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_filter_tanggal.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tanggal Masuk", "Tanggal Keluar" }));

        ComboBox_status.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_status.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "IN-SUB", "OUT-SUB" }));
        ComboBox_status.setSelectedIndex(1);

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel12.setText("Status :");

        txt_search_bagian.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_bagian.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_bagianKeyPressed(evt);
            }
        });

        label_total_data_online.setText("0");

        jLabel4.setText("Total Data :");

        button_level_gaji_sub.setBackground(new java.awt.Color(255, 255, 255));
        button_level_gaji_sub.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_level_gaji_sub.setText("Level Gaji Sub");
        button_level_gaji_sub.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_level_gaji_subActionPerformed(evt);
            }
        });

        button_status_OUT_online.setBackground(new java.awt.Color(255, 255, 255));
        button_status_OUT_online.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        button_status_OUT_online.setText("OUT-SUB");
        button_status_OUT_online.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_status_OUT_onlineActionPerformed(evt);
            }
        });

        button_edit_data_online.setBackground(new java.awt.Color(255, 255, 255));
        button_edit_data_online.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        button_edit_data_online.setText("Edit Data");
        button_edit_data_online.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_edit_data_onlineActionPerformed(evt);
            }
        });

        button_list_sub_online.setBackground(new java.awt.Color(255, 255, 255));
        button_list_sub_online.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_list_sub_online.setText("List BAGIAN online");
        button_list_sub_online.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_list_sub_onlineActionPerformed(evt);
            }
        });

        button_lihat_KTP_online.setBackground(new java.awt.Color(255, 255, 255));
        button_lihat_KTP_online.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        button_lihat_KTP_online.setText("Lihat KTP");
        button_lihat_KTP_online.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_lihat_KTP_onlineActionPerformed(evt);
            }
        });

        button_tambah_data_online.setBackground(new java.awt.Color(255, 255, 255));
        button_tambah_data_online.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        button_tambah_data_online.setText("Tambah Karyawan Baru");
        button_tambah_data_online.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_tambah_data_onlineActionPerformed(evt);
            }
        });

        table_data_online.setAutoCreateRowSorter(true);
        table_data_online.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        table_data_online.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "NAMA", "BAGIAN", "Status", "Email", "Tgl Lahir", "Jenis Kelamin", "No hp", "Posisi", "LevelGaji", "Login User", "Login PIN", "Tgl Masuk", "Tgl Keluar", "Saldo", "KTP"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Float.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table_data_online.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(table_data_online);

        button_export_data_online.setBackground(new java.awt.Color(255, 255, 255));
        button_export_data_online.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        button_export_data_online.setText("Export to Excel");
        button_export_data_online.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_data_onlineActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_id, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_bagian, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_status, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_filter_tanggal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_Search1, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_Search2, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_search_karyawan)
                        .addGap(0, 248, Short.MAX_VALUE))
                    .addComponent(jScrollPane2)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_data_online, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_lihat_KTP_online)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_tambah_data_online)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_edit_data_online)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_status_OUT_online)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_list_sub_online)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_level_gaji_sub)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_export_data_online)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_search_bagian, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ComboBox_status, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ComboBox_filter_tanggal, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_search_id, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_search_karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(Date_Search2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Date_Search1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(button_search_karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(button_export_data_online, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_level_gaji_sub, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_status_OUT_online, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_edit_data_online, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_list_sub_online, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_lihat_KTP_online, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_tambah_data_online, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_data_online, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 594, Short.MAX_VALUE)
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

    private void txt_search_karyawanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_karyawanKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTabel_online();
        }
    }//GEN-LAST:event_txt_search_karyawanKeyPressed

    private void button_search_karyawanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_karyawanActionPerformed
        // TODO add your handling code here:
        refreshTabel_online();
    }//GEN-LAST:event_button_search_karyawanActionPerformed

    private void button_export_data_onlineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_data_onlineActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model_table_berkas = (DefaultTableModel) table_data_online.getModel();
        ExportToExcel.writeToExcel(model_table_berkas, jPanel1);
    }//GEN-LAST:event_button_export_data_onlineActionPerformed

    private void txt_search_idKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_idKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTabel_online();
        }
    }//GEN-LAST:event_txt_search_idKeyPressed

    private void button_level_gaji_subActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_level_gaji_subActionPerformed
        // TODO add your handling code here:
        JDialog_Level_Gaji_Sub dialog = new JDialog_Level_Gaji_Sub(new javax.swing.JFrame(), true);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);
        dialog.setResizable(false);
        refreshTabel_online();
    }//GEN-LAST:event_button_level_gaji_subActionPerformed

    private void button_status_OUT_onlineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_status_OUT_onlineActionPerformed
        // TODO add your handling code here:
        try {
            String id = null;
            int row = table_data_online.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih karyawan dari Halaman Data Karyawan online");
            } else {
                id = table_data_online.getValueAt(row, 0).toString();
                JDialog_karyawan_keluar_masuk_sub keluar = new JDialog_karyawan_keluar_masuk_sub(new javax.swing.JFrame(), true, id, "keluar");
                keluar.pack();
                keluar.setLocationRelativeTo(this);
                keluar.setVisible(true);
                keluar.setEnabled(true);
                refreshTabel_online();
            }
        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_Data_Karyawan_Sub.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_status_OUT_onlineActionPerformed

    private void button_edit_data_onlineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_edit_data_onlineActionPerformed
        // TODO add your handling code here:
        try {
            String id = null;
            int row = table_data_online.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih karyawan dari Halaman Data Karyawan online");
            } else {
                id = (String) table_data_online.getValueAt(row, 0);
                JDialog_Edit_DataKaryawanSUB_Online dialog = new JDialog_Edit_DataKaryawanSUB_Online(new javax.swing.JFrame(), true, id);
                dialog.pack();
                dialog.setLocationRelativeTo(this);
                dialog.setVisible(true);
                dialog.setEnabled(true);
                refreshTabel_online();
            }
        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_Data_Karyawan_Sub.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_edit_data_onlineActionPerformed

    private void button_list_sub_onlineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_list_sub_onlineActionPerformed
        // TODO add your handling code here:
        JOptionPane.showMessageDialog(this, "List bagian akan mengikuti data SUB, silahkan melakukan penambahan pada data SUB");
//        JDialog_List_bagian_sub dialog = new JDialog_List_bagian_sub(new javax.swing.JFrame(), true);
//        dialog.pack();
//        dialog.setLocationRelativeTo(this);
//        dialog.setVisible(true);
//        dialog.setEnabled(true);
//        dialog.setResizable(false);
    }//GEN-LAST:event_button_list_sub_onlineActionPerformed

    private void button_lihat_KTP_onlineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_lihat_KTP_onlineActionPerformed
        // TODO add your handling code here:
        int x = table_data_online.getSelectedRow();
        if (x == -1) {
            JOptionPane.showMessageDialog(this, "Silahkan pilih salah satu data pada tabel !");
        } else {
            if ((boolean) table_data_online.getValueAt(x, 15)) {
                String id = table_data_online.getValueAt(x, 0).toString();
                String nama = table_data_online.getValueAt(x, 1).toString();
                JDialog_Show_KTP dialog = new JDialog_Show_KTP(new javax.swing.JFrame(), true, null, nama);
                dialog.show_ktp_online(id);
                dialog.pack();
                dialog.setLocationRelativeTo(this);
                dialog.setVisible(true);
                dialog.setEnabled(true);
                dialog.setResizable(false);
            } else {
                JOptionPane.showMessageDialog(this, "tidak ada data KTP");
            }
        }
    }//GEN-LAST:event_button_lihat_KTP_onlineActionPerformed

    private void button_tambah_data_onlineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_tambah_data_onlineActionPerformed
        // TODO add your handling code here:
        Insert_DataKaryawan_SUB insert_Dialog = new Insert_DataKaryawan_SUB(true);
        insert_Dialog.label_title_op_karyawan.setText("Tambah Data Karyawan Sub");
        insert_Dialog.pack();
        insert_Dialog.setLocationRelativeTo(this);
        insert_Dialog.setVisible(true);
        insert_Dialog.setEnabled(true);
        refreshTabel_online();
    }//GEN-LAST:event_button_tambah_data_onlineActionPerformed

    private void txt_search_bagianKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_bagianKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_search_bagianKeyPressed

    public void hasilFinger(boolean sukses) {
        JOptionPane.showMessageDialog(JPanel_Data_Karyawan_Sub.this, sukses);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_filter_tanggal;
    private javax.swing.JComboBox<String> ComboBox_status;
    private com.toedter.calendar.JDateChooser Date_Search1;
    private com.toedter.calendar.JDateChooser Date_Search2;
    public javax.swing.JButton button_edit_data_online;
    private javax.swing.JButton button_export_data_online;
    public javax.swing.JButton button_level_gaji_sub;
    public javax.swing.JButton button_lihat_KTP_online;
    public javax.swing.JButton button_list_sub_online;
    public static javax.swing.JButton button_search_karyawan;
    public javax.swing.JButton button_status_OUT_online;
    public javax.swing.JButton button_tambah_data_online;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel label_total_data_online;
    public static javax.swing.JTable table_data_online;
    private javax.swing.JTextField txt_search_bagian;
    private javax.swing.JTextField txt_search_id;
    private javax.swing.JTextField txt_search_karyawan;
    // End of variables declaration//GEN-END:variables

}
