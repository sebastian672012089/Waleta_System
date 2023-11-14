package waleta_system;

import waleta_system.Class.Utility;
import java.awt.event.KeyEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.ExportToExcel;

public class JPanel_Data_Karyawan_ViewOnly extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    PreparedStatement pst;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Date today = new Date();

    public JPanel_Data_Karyawan_ViewOnly() {
        initComponents();
    }

    public void init() {
        try {
            label_departemen.setText(MainForm.Login_Departemen);
            ComboBox_status_karyawan.removeAllItems();
            ComboBox_status_karyawan.addItem("All");
            sql = "SELECT DISTINCT(`status`) AS 'status' "
                    + "FROM `tb_karyawan` "
                    + "WHERE `status` IS NOT NULL ";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_status_karyawan.addItem(rs.getString("status"));
            }

            ComboBox_posisi.removeAllItems();
            ComboBox_posisi.addItem("All");
            sql = "SELECT DISTINCT(`posisi`) AS 'posisi' FROM `tb_karyawan` WHERE `posisi` IS NOT NULL";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_posisi.addItem(rs.getString("posisi"));
            }

            refreshTable();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_Data_Karyawan_ViewOnly.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void refreshTable() {
        try {
            int IN = 0, OUT = 0, BATAL = 0, STRIP = 0, ABSEN = 0;
            DefaultTableModel model = (DefaultTableModel) table_data_ktp.getModel();
            model.setRowCount(0);
            String bagian2 = "AND `nama_bagian` LIKE '%" + txt_search_bagian.getText() + "%' \n";
            if (txt_search_bagian.getText() == null || txt_search_bagian.getText().equals("")) {
                bagian2 = "";
            }
            String Status = "AND `status` = '" + ComboBox_status_karyawan.getSelectedItem().toString() + "' \n";
            if ("All".equals(ComboBox_status_karyawan.getSelectedItem().toString())) {
                Status = "";
            }
            String kelamin = "AND `jenis_kelamin` = '" + ComboBox_kelamin_karyawan.getSelectedItem().toString() + "' \n";
            if ("All".equals(ComboBox_kelamin_karyawan.getSelectedItem().toString())) {
                kelamin = "";
            }
            String posisi = "AND `posisi` = '" + ComboBox_posisi.getSelectedItem().toString() + "' \n";
            if ("All".equals(ComboBox_posisi.getSelectedItem().toString())) {
                posisi = "";
            }
            String filter_tanggal = "";
            if (Date_Search1.getDate() != null && Date_Search2.getDate() != null) {
                switch (ComboBox_filter_tanggal.getSelectedIndex()) {
                    case 0:
                        filter_tanggal = "AND `tanggal_interview` BETWEEN '" + dateFormat.format(Date_Search1.getDate()) + "' AND '" + dateFormat.format(Date_Search2.getDate()) + "' \n";
                        break;
                    case 1:
                        filter_tanggal = "AND `tanggal_masuk` BETWEEN '" + dateFormat.format(Date_Search1.getDate()) + "' AND '" + dateFormat.format(Date_Search2.getDate()) + "' \n";
                        break;
                    case 2:
                        filter_tanggal = "AND `tanggal_keluar` BETWEEN '" + dateFormat.format(Date_Search1.getDate()) + "' AND '" + dateFormat.format(Date_Search2.getDate()) + "' \n";
                        break;
                    default:
                        break;
                }
            }
            sql = "SELECT `id_pegawai`, `nama_pegawai`, `kode_departemen`, `posisi`, `level_gaji`, `jam_kerja`, `jalur_jemputan`, \n"
                    + "`nama_bagian`, `posisi_bagian`, `divisi_bagian`, `bagian_bagian`, `ruang_bagian`, \n"
                    + "`status`, `tanggal_masuk`, `tanggal_keluar`, `kategori_keluar`, `keterangan` \n"
                    + "FROM `tb_karyawan` \n"
                    + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian` \n"
                    + "WHERE `nama_pegawai` LIKE '%" + txt_search_NamaKaryawan.getText() + "%' \n"
                    + "AND `id_pegawai` LIKE '%" + txt_search_id.getText() + "%' \n"
                    + "AND `kode_departemen` = '" + MainForm.Login_Departemen + "' \n"
                    + bagian2
                    + Status
                    + posisi
                    + kelamin
                    + filter_tanggal
                    + "ORDER BY `id_pegawai` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                Object[] row = new Object[20];
                row[0] = rs.getString("id_pegawai");
                row[1] = rs.getString("nama_pegawai");
                row[2] = rs.getString("posisi");
                row[3] = rs.getString("kode_departemen");
                row[4] = rs.getString("posisi_bagian");
                row[5] = rs.getString("divisi_bagian");
                row[6] = rs.getString("bagian_bagian");
                row[7] = rs.getString("ruang_bagian");
                row[8] = rs.getString("level_gaji");
                row[9] = rs.getString("jam_kerja");
                row[10] = rs.getString("jalur_jemputan");
                row[11] = rs.getString("status");
                row[12] = rs.getDate("tanggal_masuk");
                row[13] = rs.getDate("tanggal_keluar");
                row[14] = rs.getString("kategori_keluar");
                row[15] = rs.getString("keterangan");
                Date masuk = rs.getDate("tanggal_masuk");
                Date keluar = rs.getDate("tanggal_keluar");
                long lama_bekerja = 0;
                if (masuk != null) {
                    if (keluar != null) {
                        lama_bekerja = Math.abs(keluar.getTime() - masuk.getTime());
                    } else {
                        lama_bekerja = Math.abs(today.getTime() - masuk.getTime());
                    }
                }
                Long tahun_masa_kerja = TimeUnit.MILLISECONDS.toDays(lama_bekerja) / 365;
                Long bulan_masa_kerja = (TimeUnit.MILLISECONDS.toDays(lama_bekerja) % 365) / 30;
                Long hari_masa_kerja = (TimeUnit.MILLISECONDS.toDays(lama_bekerja) % 365) % 30;
                row[16] = tahun_masa_kerja + " Tahun " + bulan_masa_kerja + " Bulan " + hari_masa_kerja + " Hari";
                model.addRow(row);
                switch (rs.getString("status")) {
                    case "IN":
                    case "IN-SUB":
                        IN++;
                        break;
                    case "OUT":
                    case "OUT-SUB":
                        OUT++;
                        break;
                    case "BATAL":
                        BATAL++;
                        break;
                    case "-":
                        STRIP++;
                        break;
                    case "ABSEN":
                        ABSEN++;
                        break;
                    default:
                        System.out.println("status karyawan tidak terdeteksi");
                        break;
                }
            }
            ColumnsAutoSizer.sizeColumnsToFit(table_data_ktp);
            int rowData = table_data_ktp.getRowCount();
            label_total_data.setText(Integer.toString(rowData));
            label_total_in.setText(Integer.toString(IN));
            label_total_out.setText(Integer.toString(OUT));
            label_total_batal.setText(Integer.toString(BATAL));
            label_total_strip.setText(Integer.toString(STRIP));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Data_Karyawan_ViewOnly.class.getName()).log(Level.SEVERE, null, ex);
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

        jLabel9 = new javax.swing.JLabel();
        label_total_in = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        label_total_out = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        label_total_data = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        label_total_batal = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        label_total_strip = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        label_total_absen = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txt_search_id = new javax.swing.JTextField();
        txt_search_NamaKaryawan = new javax.swing.JTextField();
        button_search_karyawan = new javax.swing.JButton();
        ComboBox_status_karyawan = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        ComboBox_posisi = new javax.swing.JComboBox<>();
        jLabel14 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        txt_search_bagian = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        ComboBox_kelamin_karyawan = new javax.swing.JComboBox<>();
        ComboBox_filter_tanggal = new javax.swing.JComboBox<>();
        Date_Search1 = new com.toedter.calendar.JDateChooser();
        Date_Search2 = new com.toedter.calendar.JDateChooser();
        jScrollPane1 = new javax.swing.JScrollPane();
        table_data_ktp = new javax.swing.JTable();
        button_export_data_karyawan_ktp = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();
        label_departemen = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Data Karyawan", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N
        setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        setPreferredSize(new java.awt.Dimension(1366, 700));

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel9.setText("Total IN :");

        label_total_in.setBackground(new java.awt.Color(255, 255, 255));
        label_total_in.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_in.setText("0");

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel10.setText("Total OUT :");

        label_total_out.setBackground(new java.awt.Color(255, 255, 255));
        label_total_out.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_out.setText("0");

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("Total Data :");

        label_total_data.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_data.setText("0");

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel11.setText("Total BATAL :");

        label_total_batal.setBackground(new java.awt.Color(255, 255, 255));
        label_total_batal.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_batal.setText("0");

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel13.setText("Total - :");

        label_total_strip.setBackground(new java.awt.Color(255, 255, 255));
        label_total_strip.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_strip.setText("0");

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel17.setText("Total ABSEN :");

        label_total_absen.setBackground(new java.awt.Color(255, 255, 255));
        label_total_absen.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_absen.setText("0");

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel8.setText("ID :");

        txt_search_id.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_id.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_idKeyPressed(evt);
            }
        });

        txt_search_NamaKaryawan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_NamaKaryawan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_NamaKaryawanKeyPressed(evt);
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

        ComboBox_status_karyawan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_status_karyawan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "IN", "OUT", "ABSEN", "IN-SUB", "OUT-SUB", "BATAL", "-" }));
        ComboBox_status_karyawan.setSelectedIndex(1);

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("Nama :");

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel12.setText("Status :");

        ComboBox_posisi.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_posisi.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel14.setText("Posisi :");

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel16.setText("Bagian :");

        txt_search_bagian.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_bagian.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_bagianKeyPressed(evt);
            }
        });

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel7.setText("Departemen :");

        ComboBox_kelamin_karyawan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_kelamin_karyawan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "LAKI-LAKI", "PEREMPUAN" }));

        ComboBox_filter_tanggal.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_filter_tanggal.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tanggal Interview", "Tanggal Masuk", "Tanggal Keluar" }));

        Date_Search1.setBackground(new java.awt.Color(255, 255, 255));
        Date_Search1.setToolTipText("");
        Date_Search1.setDateFormatString("dd MMM yyyy");
        Date_Search1.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N

        Date_Search2.setBackground(new java.awt.Color(255, 255, 255));
        Date_Search2.setDateFormatString("dd MMM yyyy");
        Date_Search2.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        Date_Search2.setMinSelectableDate(new java.util.Date(-62135791118000L));

        table_data_ktp.setAutoCreateRowSorter(true);
        table_data_ktp.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        table_data_ktp.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Nama", "Posisi", "Dept", "Posisi", "Divisi", "Bagian", "Ruang", "Level Gaji", "Jam Kerja", "Jemputan", "Status", "Tgl Masuk", "Tgl Keluar", "Kategori Keluar", "Keterangan Keluar", "Masa Kerja"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table_data_ktp.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(table_data_ktp);

        button_export_data_karyawan_ktp.setBackground(new java.awt.Color(255, 255, 255));
        button_export_data_karyawan_ktp.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        button_export_data_karyawan_ktp.setText("Export to Excel");
        button_export_data_karyawan_ktp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_data_karyawan_ktpActionPerformed(evt);
            }
        });

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel15.setText("Jenis Kelamin  :");

        label_departemen.setBackground(new java.awt.Color(255, 255, 255));
        label_departemen.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_departemen.setText("-");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_id, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_NamaKaryawan, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_bagian, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_kelamin_karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_status_karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_posisi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_filter_tanggal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_Search1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_Search2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_search_karyawan)
                        .addGap(0, 64, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_data)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_in)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_out)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_batal)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_strip)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_absen)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_departemen)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_export_data_karyawan_ktp)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_search_id, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_search_NamaKaryawan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txt_search_bagian, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(ComboBox_kelamin_karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(ComboBox_status_karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(ComboBox_posisi, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(ComboBox_filter_tanggal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_Search1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_Search2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_search_karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(button_export_data_karyawan_ktp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_total_out, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_total_data, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_total_in, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_total_absen, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_departemen, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_total_strip, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_total_batal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 602, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txt_search_NamaKaryawanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_NamaKaryawanKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_search_NamaKaryawanKeyPressed

    private void button_search_karyawanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_karyawanActionPerformed
        // TODO add your handling code here:
        refreshTable();
    }//GEN-LAST:event_button_search_karyawanActionPerformed

    private void button_export_data_karyawan_ktpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_data_karyawan_ktpActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model_table_ktp = (DefaultTableModel) table_data_ktp.getModel();
        ExportToExcel.writeToExcel(model_table_ktp, JPanel_Data_Karyawan_ViewOnly.this);
    }//GEN-LAST:event_button_export_data_karyawan_ktpActionPerformed

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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_filter_tanggal;
    private javax.swing.JComboBox<String> ComboBox_kelamin_karyawan;
    private javax.swing.JComboBox<String> ComboBox_posisi;
    private javax.swing.JComboBox<String> ComboBox_status_karyawan;
    private com.toedter.calendar.JDateChooser Date_Search1;
    private com.toedter.calendar.JDateChooser Date_Search2;
    private javax.swing.JButton button_export_data_karyawan_ktp;
    public static javax.swing.JButton button_search_karyawan;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel label_departemen;
    private javax.swing.JLabel label_total_absen;
    private javax.swing.JLabel label_total_batal;
    private javax.swing.JLabel label_total_data;
    private javax.swing.JLabel label_total_in;
    private javax.swing.JLabel label_total_out;
    private javax.swing.JLabel label_total_strip;
    public static javax.swing.JTable table_data_ktp;
    private javax.swing.JTextField txt_search_NamaKaryawan;
    private javax.swing.JTextField txt_search_bagian;
    private javax.swing.JTextField txt_search_id;
    // End of variables declaration//GEN-END:variables

}
