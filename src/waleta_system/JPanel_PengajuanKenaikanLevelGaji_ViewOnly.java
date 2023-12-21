package waleta_system;

import waleta_system.Class.Utility;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.ExportToExcel;
import waleta_system.Finance.JDialog_InsertEdit_Pengajuan_Kenaikan_LevelGaji;

public class JPanel_PengajuanKenaikanLevelGaji_ViewOnly extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();

    public JPanel_PengajuanKenaikanLevelGaji_ViewOnly() {
        initComponents();
        Table_pengajuan_kenaikan_gaji.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && Table_pengajuan_kenaikan_gaji.getSelectedRow() != -1) {
                    int i = Table_pengajuan_kenaikan_gaji.getSelectedRow();
                    if (Table_pengajuan_kenaikan_gaji.getValueAt(i, 11) != null) {
                        //sudah dibatalkan
                        button_diketahui_kadep.setEnabled(false);
                    } else {
                        if (Table_pengajuan_kenaikan_gaji.getValueAt(i, 7) == null) {
                            //belum diketahui kadep
                            button_diketahui_kadep.setEnabled(true);
                        } else {
                            //sudah diketahui kadep
                            button_diketahui_kadep.setEnabled(false);
                        }
                    }
                }
            }
        });
    }

    public void init() {
        txt_departemen.setText(MainForm.Login_Departemen);
        refreshTable();
    }

    public void refreshTable() {
        try {
            String diketahui_kadep = "";
            if (ComboBox_diketahui_manager.getSelectedIndex() == 1) {
                diketahui_kadep = "AND `diketahui_kadep` IS NOT NULL ";
            } else if (ComboBox_diketahui_manager.getSelectedIndex() == 2) {
                diketahui_kadep = "AND `diketahui_kadep` IS NULL ";
            }
            String diketahui_manager = "";
            if (ComboBox_diketahui_manager.getSelectedIndex() == 1) {
                diketahui_manager = "AND `diketahui_manager` IS NOT NULL ";
            } else if (ComboBox_diketahui_manager.getSelectedIndex() == 2) {
                diketahui_manager = "AND `diketahui_manager` IS NULL ";
            }
            String disetujui_direktur = "";
            if (ComboBox_disetujui_direktur.getSelectedIndex() == 1) {
                disetujui_direktur = "AND `disetujui_direktur` IS NOT NULL ";
            } else if (ComboBox_disetujui_direktur.getSelectedIndex() == 2) {
                disetujui_direktur = "AND `disetujui_direktur` IS NULL ";
            }
            String diketahui_keuangan = "";
            if (ComboBox_diketahui_keuangan.getSelectedIndex() == 1) {
                diketahui_keuangan = "AND `diketahui_keuangan` IS NOT NULL ";
            } else if (ComboBox_diketahui_keuangan.getSelectedIndex() == 2) {
                diketahui_keuangan = "AND `diketahui_keuangan` IS NULL ";
            }
            String filter_tanggal = "";
            if (Date1.getDate() != null && Date2.getDate() != null) {
                filter_tanggal = "AND `tanggal_pengajuan` BETWEEN '" + dateFormat.format(Date1.getDate()) + "' AND '" + dateFormat.format(Date2.getDate()) + "' ";
            }
            String filter_departemen  = "";
            if (txt_departemen.getText() != null && !txt_departemen.getText().equals("")) {
                filter_departemen  = "AND `tb_bagian`.`kode_departemen` = '" + txt_departemen.getText() + "' ";
            }
            String filter_divisi_hrga = "";
            if (MainForm.Login_Departemen.equals("HRGA") && !MainForm.Login_namaBagian.split("-")[2].equals("")) {
                filter_divisi_hrga = "AND `tb_bagian`.`divisi_bagian` = '" + MainForm.Login_namaBagian.split("-")[2] + "'";
            }

            DefaultTableModel model = (DefaultTableModel) Table_pengajuan_kenaikan_gaji.getModel();
            model.setRowCount(0);
            sql = "SELECT `no`, `tanggal_pengajuan`, `catatan`, `tb_level_gaji_pengajuan_kenaikan`.`id_pegawai`, `tb_karyawan`.`nama_pegawai`, `nama_bagian`, `level_gaji_lama`, `level_gaji_baru`, "
                    + "`diketahui_kadep`, `diketahui_manager`, `disetujui_direktur`, `diketahui_keuangan`, `dibatalkan`, `admin`,"
                    + "`tanggal_masuk`, `tanggal_keluar` "
                    + "FROM `tb_level_gaji_pengajuan_kenaikan` "
                    + "LEFT JOIN `tb_karyawan` ON `tb_level_gaji_pengajuan_kenaikan`.`id_pegawai` = `tb_karyawan`.`id_pegawai` "
                    + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian` "
                    + "WHERE "
                    + "`nama_pegawai` LIKE '%" + txt_search_nama.getText() + "%' "
                    + "AND `tb_level_gaji_pengajuan_kenaikan`.`id_pegawai` LIKE '%" + txt_search_id.getText() + "%' "
                    + filter_departemen
                    + filter_divisi_hrga
                    + "AND `tb_bagian`.`nama_bagian` LIKE '%" + txt_search_bagian.getText() + "%' "
                    + diketahui_kadep
                    + diketahui_manager
                    + disetujui_direktur
                    + diketahui_keuangan
                    + filter_tanggal
                    + "ORDER BY `no` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[15];
            while (rs.next()) {
                row[0] = rs.getInt("no");
                row[1] = rs.getDate("tanggal_pengajuan");
                row[2] = rs.getString("id_pegawai");
                row[3] = rs.getString("nama_pegawai");
                row[4] = rs.getString("nama_bagian");
                row[5] = rs.getString("level_gaji_lama");
                row[6] = rs.getString("level_gaji_baru");
                row[7] = rs.getString("diketahui_kadep");
                row[8] = rs.getString("diketahui_manager");
                row[9] = rs.getString("disetujui_direktur");
                row[10] = rs.getString("diketahui_keuangan");
                row[11] = rs.getString("dibatalkan");
                row[12] = rs.getString("catatan");

                //menghitung lama karyawan bekerja
                Date masuk = rs.getDate("tanggal_masuk");
                Date tanggal_pengajuan = rs.getDate("tanggal_pengajuan");
                long lama_bekerja = 0;
                if (masuk != null && tanggal_pengajuan != null) {
                    lama_bekerja = Math.abs(tanggal_pengajuan.getTime() - masuk.getTime());
                }
                row[13] = TimeUnit.MILLISECONDS.toDays(lama_bekerja) / 365 + " Tahun "
                        + (TimeUnit.MILLISECONDS.toDays(lama_bekerja) % 365) / 30 + " Bulan "
                        + (TimeUnit.MILLISECONDS.toDays(lama_bekerja) % 365) % 30 + " Hari";
                row[14] = rs.getString("admin");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_pengajuan_kenaikan_gaji);
            int rowData = Table_pengajuan_kenaikan_gaji.getRowCount();
            label_total_data.setText(Integer.toString(rowData));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_PengajuanKenaikanLevelGaji_ViewOnly.class.getName()).log(Level.SEVERE, null, ex);
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

        jPanel_Customer_baku = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        Table_pengajuan_kenaikan_gaji = new javax.swing.JTable();
        button_insert = new javax.swing.JButton();
        button_export = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        label_total_data = new javax.swing.JLabel();
        Date2 = new com.toedter.calendar.JDateChooser();
        jLabel4 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txt_search_bagian = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        ComboBox_disetujui_direktur = new javax.swing.JComboBox<>();
        txt_search_nama = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txt_search_id = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        Date1 = new com.toedter.calendar.JDateChooser();
        button_search = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        ComboBox_diketahui_keuangan = new javax.swing.JComboBox<>();
        jLabel14 = new javax.swing.JLabel();
        ComboBox_diketahui_manager = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        txt_departemen = new javax.swing.JTextField();
        ComboBox_diketahui_kadep = new javax.swing.JComboBox<>();
        jLabel15 = new javax.swing.JLabel();
        button_diketahui_kadep = new javax.swing.JButton();
        button_edit = new javax.swing.JButton();

        jPanel_Customer_baku.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_Customer_baku.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Data Pengajuan Kenaikan Level Gaji", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N
        jPanel_Customer_baku.setPreferredSize(new java.awt.Dimension(1366, 701));

        Table_pengajuan_kenaikan_gaji.setAutoCreateRowSorter(true);
        Table_pengajuan_kenaikan_gaji.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "Tgl Pengajuan", "ID Pegawai", "Nama", "Bagian", "Level Gaji Lama", "Level Gaji Baru", "Diketahui Kadep", "Diketahui Manager", "Disetujui Direktur", "Diketahui Keuangan", "Dibatalkan", "Catatan", "Masa Kerja"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Table_pengajuan_kenaikan_gaji.getTableHeader().setReorderingAllowed(false);
        jScrollPane8.setViewportView(Table_pengajuan_kenaikan_gaji);

        button_insert.setBackground(new java.awt.Color(255, 255, 255));
        button_insert.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_insert.setText("NEW");
        button_insert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_insertActionPerformed(evt);
            }
        });

        button_export.setBackground(new java.awt.Color(255, 255, 255));
        button_export.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export.setText("Export To Excel");
        button_export.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_exportActionPerformed(evt);
            }
        });

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel12.setText("Total Data :");

        label_total_data.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_data.setText("TOTAL");

        Date2.setBackground(new java.awt.Color(255, 255, 255));
        Date2.setDate(new Date());
        Date2.setDateFormatString("dd MMM yyyy");
        Date2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel4.setText("Tgl Pengajuan :");

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("Posisi-Departemen-Divisi-Bagian-Ruang :");

        txt_search_bagian.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_bagian.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_bagianKeyPressed(evt);
            }
        });

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel11.setText("Disetujui Direktur :");

        ComboBox_disetujui_direktur.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_disetujui_direktur.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Sudah Disetujui", "Belum Disetujui" }));

        txt_search_nama.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_nama.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_namaKeyPressed(evt);
            }
        });

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("Nama :");

        txt_search_id.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_id.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_idKeyPressed(evt);
            }
        });

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel8.setText("ID Pegawai :");

        Date1.setBackground(new java.awt.Color(255, 255, 255));
        Date1.setDate(new Date(new Date().getTime()- (7 * 24 * 60 * 60 * 1000)));
        Date1.setDateFormatString("dd MMM yyyy");
        Date1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        button_search.setBackground(new java.awt.Color(255, 255, 255));
        button_search.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search.setText("Search");
        button_search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_searchActionPerformed(evt);
            }
        });

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel13.setText("Diketahui Keuangan :");

        ComboBox_diketahui_keuangan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_diketahui_keuangan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Sudah Diketahui", "Belum Diketahui" }));

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel14.setText("Diketahui Manager :");

        ComboBox_diketahui_manager.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_diketahui_manager.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Sudah Disetujui", "Belum Disetujui" }));

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel5.setText("Departemen :");

        txt_departemen.setEditable(false);
        txt_departemen.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_departemen.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_departemenKeyPressed(evt);
            }
        });

        ComboBox_diketahui_kadep.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_diketahui_kadep.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Sudah Disetujui", "Belum Disetujui" }));

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel15.setText("Diketahui Kadep :");

        button_diketahui_kadep.setBackground(new java.awt.Color(255, 255, 255));
        button_diketahui_kadep.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_diketahui_kadep.setText("Diketahui Kadep");
        button_diketahui_kadep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_diketahui_kadepActionPerformed(evt);
            }
        });

        button_edit.setBackground(new java.awt.Color(255, 255, 255));
        button_edit.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_edit.setText("EDIT");
        button_edit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_editActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_Customer_bakuLayout = new javax.swing.GroupLayout(jPanel_Customer_baku);
        jPanel_Customer_baku.setLayout(jPanel_Customer_bakuLayout);
        jPanel_Customer_bakuLayout.setHorizontalGroup(
            jPanel_Customer_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Customer_bakuLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_Customer_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane8)
                    .addGroup(jPanel_Customer_bakuLayout.createSequentialGroup()
                        .addGroup(jPanel_Customer_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_Customer_bakuLayout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_id, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_nama, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_departemen, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_bagian, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_search))
                            .addGroup(jPanel_Customer_bakuLayout.createSequentialGroup()
                                .addGroup(jPanel_Customer_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel_Customer_bakuLayout.createSequentialGroup()
                                        .addComponent(jLabel15)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(ComboBox_diketahui_kadep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel14)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(ComboBox_diketahui_manager, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel11))
                                    .addGroup(jPanel_Customer_bakuLayout.createSequentialGroup()
                                        .addComponent(button_insert)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(button_edit)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(button_diketahui_kadep)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(button_export)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel12)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_data)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_disetujui_direktur, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_diketahui_keuangan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 34, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel_Customer_bakuLayout.setVerticalGroup(
            jPanel_Customer_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Customer_bakuLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_Customer_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_id, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_nama, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_departemen, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel_Customer_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel_Customer_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_search_bagian, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel_Customer_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(button_search, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Date1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Date2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_Customer_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(ComboBox_disetujui_direktur, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_diketahui_keuangan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_diketahui_manager, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_diketahui_kadep, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_Customer_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(button_edit, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_data, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_diketahui_kadep, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_insert, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 576, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jPanel_Customer_baku, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jPanel_Customer_baku, javax.swing.GroupLayout.DEFAULT_SIZE, 700, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void button_insertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_insertActionPerformed
        // TODO add your handling code here:
        JDialog_InsertEdit_Pengajuan_Kenaikan_LevelGaji dialog = new JDialog_InsertEdit_Pengajuan_Kenaikan_LevelGaji(new javax.swing.JFrame(), true, null, null, null, null, null);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);
        dialog.setResizable(false);
        refreshTable();
    }//GEN-LAST:event_button_insertActionPerformed

    private void button_exportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_exportActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Table_pengajuan_kenaikan_gaji.getModel();
        ExportToExcel.writeToExcel(model, jPanel_Customer_baku);
    }//GEN-LAST:event_button_exportActionPerformed

    private void txt_search_namaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_namaKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_search_namaKeyPressed

    private void button_searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_searchActionPerformed
        // TODO add your handling code here:
        refreshTable();
    }//GEN-LAST:event_button_searchActionPerformed

    private void txt_search_idKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_idKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_search_idKeyPressed

    private void txt_search_bagianKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_bagianKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_search_bagianKeyPressed

    private void txt_departemenKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_departemenKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_departemenKeyPressed

    private void button_diketahui_kadepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_diketahui_kadepActionPerformed
        // TODO add your handling code here:
        try {
            int i = Table_pengajuan_kenaikan_gaji.getSelectedRow();
            if (i >= 0) {
                boolean check = true;
                String id_pegawai = Table_pengajuan_kenaikan_gaji.getValueAt(i, 2).toString();
                String kode_departemen = "";
                sql = "SELECT `tb_bagian`.`kode_departemen` \n"
                        + "FROM `tb_karyawan` \n"
                        + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`\n"
                        + "WHERE `id_pegawai` = '" + id_pegawai + "'";
                rs = Utility.db.getStatement().executeQuery(sql);
                if (rs.next()) {
                    kode_departemen = rs.getString("kode_departemen");
                }

                if (!MainForm.Login_namaBagian.toUpperCase().contains("KADEP-" + kode_departemen) && MainForm.Login_kodeBagian != 244) {
                    JOptionPane.showMessageDialog(this, "Hanya KADEP " + kode_departemen + " / OPERATIONAL MANAGER, yang bisa mengetahui pengajuan kenaikan level gaji!");
                    check = false;
                }

                if (check) {
                    String nomor = Table_pengajuan_kenaikan_gaji.getValueAt(i, 0).toString();
                    sql = "UPDATE `tb_level_gaji_pengajuan_kenaikan` SET "
                            + "`diketahui_kadep`='" + MainForm.Login_NamaPegawai + " " + dateFormat.format(date) + "' "
                            + "WHERE `no` = '" + nomor + "'";
                    Utility.db.getStatement().executeUpdate(sql);
                    JOptionPane.showMessageDialog(this, "Data Saved!");
                    refreshTable();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Anda belum memilih data!");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_PengajuanKenaikanLevelGaji_ViewOnly.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_diketahui_kadepActionPerformed

    private void button_editActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_editActionPerformed
        // TODO add your handling code here:
        int i = Table_pengajuan_kenaikan_gaji.getSelectedRow();
        if (i > -1) {
            boolean check = true;
            if (Table_pengajuan_kenaikan_gaji.getValueAt(i, 8) != null) {//sudah diketahui Manager
                JOptionPane.showMessageDialog(this, "Pengajuan sudah diketahui Manager Operasional, tidak bisa melakukan edit!");
                check = false;
            } else if (Table_pengajuan_kenaikan_gaji.getValueAt(i, 7) != null) {//sudah diketahui kadep
                String departemen = Table_pengajuan_kenaikan_gaji.getValueAt(i, 4).toString().split("-")[1];
                if (!MainForm.Login_namaBagian.contains("KADEP-" + departemen) && MainForm.Login_kodeBagian != 244) {
                    JOptionPane.showMessageDialog(this, "Pengajuan sudah diketahui Kadep, hanya Kadep yang bersangkutan yang bisa melakukan edit!");
                    check = false;
                }
            }

            if (check) {
                String nomor = Table_pengajuan_kenaikan_gaji.getValueAt(i, 0).toString();
                String tgl_pengajuan = Table_pengajuan_kenaikan_gaji.getValueAt(i, 1).toString();
                String id_pegawai = Table_pengajuan_kenaikan_gaji.getValueAt(i, 2).toString();
                String level_baru = Table_pengajuan_kenaikan_gaji.getValueAt(i, 6).toString();
                String catatan = Table_pengajuan_kenaikan_gaji.getValueAt(i, 12).toString();
                JDialog_InsertEdit_Pengajuan_Kenaikan_LevelGaji dialog = new JDialog_InsertEdit_Pengajuan_Kenaikan_LevelGaji(new javax.swing.JFrame(), true, nomor, id_pegawai, level_baru, catatan, tgl_pengajuan);
                dialog.pack();
                dialog.setLocationRelativeTo(this);
                dialog.setVisible(true);
                dialog.setEnabled(true);
                dialog.setResizable(false);
                refreshTable();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Silahkan pilih data pada tabel");
        }
    }//GEN-LAST:event_button_editActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_diketahui_kadep;
    private javax.swing.JComboBox<String> ComboBox_diketahui_keuangan;
    private javax.swing.JComboBox<String> ComboBox_diketahui_manager;
    private javax.swing.JComboBox<String> ComboBox_disetujui_direktur;
    private com.toedter.calendar.JDateChooser Date1;
    private com.toedter.calendar.JDateChooser Date2;
    private javax.swing.JTable Table_pengajuan_kenaikan_gaji;
    public javax.swing.JButton button_diketahui_kadep;
    public javax.swing.JButton button_edit;
    private javax.swing.JButton button_export;
    public javax.swing.JButton button_insert;
    public static javax.swing.JButton button_search;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel_Customer_baku;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JLabel label_total_data;
    private javax.swing.JTextField txt_departemen;
    private javax.swing.JTextField txt_search_bagian;
    private javax.swing.JTextField txt_search_id;
    private javax.swing.JTextField txt_search_nama;
    // End of variables declaration//GEN-END:variables
}
