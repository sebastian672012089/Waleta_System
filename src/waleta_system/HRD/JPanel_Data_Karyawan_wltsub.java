package waleta_system.HRD;

import waleta_system.Class.Utility;
import java.awt.HeadlessException;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.DataKaryawan;
import waleta_system.Class.ExportToExcel;
import waleta_system.Fingerprint.MyEnrollmentForm;
import waleta_system.Fingerprint.MyVerificationForm;
import waleta_system.Interface.InterfacePanel;
import waleta_system.MainForm;

public class JPanel_Data_Karyawan_wltsub extends javax.swing.JPanel implements InterfacePanel {

    String sql = null;
    ResultSet rs;
    PreparedStatement pst;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Date today = new Date();
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    DefaultTableModel dm = new DefaultTableModel();
    JFileChooser chooser = new JFileChooser();

    public JPanel_Data_Karyawan_wltsub() {
        initComponents();
    }

    @Override
    public void init() {
        refreshTable();
    }

    public void refreshTable() {
        try {
            Utility.db_sub.connect();
            DefaultTableModel model = (DefaultTableModel) table_data_ktp.getModel();
            model.setRowCount(0);
            String filter_tanggal = "";
            String bagian = "AND `nama_bagian` LIKE '%" + txt_search_bagian.getText() + "%' ";
            if (txt_search_bagian.getText() == null || txt_search_bagian.getText().equals("")) {
                bagian = "";
            }
            if (Date_Search1.getDate() != null && Date_Search2.getDate() != null) {
                switch (ComboBox_filter_tanggal.getSelectedIndex()) {
                    case 0:
                        filter_tanggal = "AND `tanggal_masuk` BETWEEN '" + dateFormat.format(Date_Search1.getDate()) + "' AND '" + dateFormat.format(Date_Search2.getDate()) + "'";
                        break;
                    case 1:
                        filter_tanggal = "AND `tanggal_keluar` BETWEEN '" + dateFormat.format(Date_Search1.getDate()) + "' AND '" + dateFormat.format(Date_Search2.getDate()) + "'";
                        break;
                    default:
                        break;
                }
            }
            String local_query = "SELECT `id_pegawai`, `pin_finger`, `nik_ktp`, `nama_pegawai`, `jenis_kelamin`, `tempat_lahir`, `tanggal_lahir`, `agama`, `alamat`, `desa`, `kecamatan`, `kota_kabupaten`, `provinsi`, `golongan_darah`, `no_telp`, `email`, `status_kawin`, `nama_ibu`, `nama_bagian`, `kode_departemen`, `posisi`, `pendidikan`, `tanggal_interview`, `tanggal_masuk`, `tanggal_keluar`, `status`, `level_gaji`, `jam_kerja`, `keterangan`, "
                    + "`fc_ktp`, `sertifikat_vaksin1`, `sertifikat_vaksin2`, `berkas_surat_pernyataan`, `tanggal_surat`, `rek_cimb`, `jalur_jemputan`, `potongan_bpjs`, `keterangan`, DATE_ADD(`tanggal_surat`, INTERVAL 6 MONTH) AS 'tgl_surat_berakhir' "
                    + "FROM `tb_karyawan` LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`"
                    + "WHERE `nama_pegawai` LIKE '%" + txt_search_karyawan.getText() + "%' "
                    + "AND `id_pegawai` LIKE '%" + txt_search_id.getText() + "%' "
                    + "AND `status` LIKE '%" + txt_search_status.getText() + "%' "
                    + bagian
                    + filter_tanggal;
            rs = Utility.db.getStatement().executeQuery(local_query);
            Object[] row = new Object[35];
            while (rs.next()) {
                row[0] = rs.getString("id_pegawai");
                row[1] = rs.getString("nik_ktp");
                row[2] = rs.getString("nama_pegawai");
                row[3] = rs.getString("status");
                row[4] = rs.getString("posisi");
                row[5] = rs.getString("nama_bagian");
                row[6] = rs.getDate("tanggal_masuk");
                row[7] = rs.getDate("tanggal_keluar");
                row[8] = rs.getDate("tanggal_lahir");
                row[9] = rs.getString("no_telp");
                row[10] = rs.getString("email");
                //menghitung lama karyawan bekerja
                Date lahir = rs.getDate("tanggal_lahir");
                Date masuk = rs.getDate("tanggal_masuk");
                Date keluar = rs.getDate("tanggal_keluar");
                long lama_bekerja = 0;
                if (masuk != null && (rs.getString("status").contains("IN") || rs.getString("status").contains("OUT"))) {
                    if (keluar != null) {
                        lama_bekerja = Math.abs(keluar.getTime() - masuk.getTime());
                    } else {
                        lama_bekerja = Math.abs(today.getTime() - masuk.getTime());
                    }
                }

                row[11] = TimeUnit.MILLISECONDS.toDays(lama_bekerja) / 365;
                row[12] = (TimeUnit.MILLISECONDS.toDays(lama_bekerja) % 365) / 30;
                row[13] = (TimeUnit.MILLISECONDS.toDays(lama_bekerja) % 365) % 30;

                long umur_now = 0;
                if (lahir != null) {
                    umur_now = Math.abs(today.getTime() - lahir.getTime());
                }
                row[14] = TimeUnit.MILLISECONDS.toDays(umur_now) / 365;
                model.addRow(row);
            }

            String online_query = "SELECT `id_pegawai`, `nama_pegawai`, `bagian`, `status`, `email`, `tgl_lahir`, `jenis_kelamin`, `no_hp`, `bagian`, `jenis_pegawai`, `tanggal_masuk`, `tanggal_keluar` "
                    + "FROM `tb_karyawan` "
                    + "WHERE `nama_pegawai` LIKE '%" + txt_search_karyawan.getText() + "%' "
                    + "AND `id_pegawai` LIKE '%" + txt_search_id.getText() + "%' "
                    + "AND `status` LIKE '%" + txt_search_status.getText() + "%' "
                    + "AND `bagian` LIKE '%" + txt_search_bagian.getText() + "%' "
                    + filter_tanggal;
            rs = Utility.db_sub.getStatement().executeQuery(online_query);
            while (rs.next()) {
                row[0] = rs.getString("id_pegawai");
                row[1] = null;
                row[2] = rs.getString("nama_pegawai");
                row[3] = rs.getString("status");
                row[4] = rs.getString("jenis_pegawai");
                row[5] = rs.getString("bagian");
                row[6] = rs.getDate("tanggal_masuk");
                row[7] = rs.getDate("tanggal_keluar");
                row[8] = rs.getDate("tgl_lahir");
                row[9] = rs.getString("no_hp");
                row[10] = rs.getString("email");
                //menghitung lama karyawan bekerja
                Date lahir = rs.getDate("tgl_lahir");
                Date masuk = rs.getDate("tanggal_masuk");
                Date keluar = rs.getDate("tanggal_keluar");
                long lama_bekerja = 0;
                if (masuk != null && (rs.getString("status").contains("IN") || rs.getString("status").contains("OUT"))) {
                    if (keluar != null) {
                        lama_bekerja = Math.abs(keluar.getTime() - masuk.getTime());
                    } else {
                        lama_bekerja = Math.abs(today.getTime() - masuk.getTime());
                    }
                }

                row[11] = TimeUnit.MILLISECONDS.toDays(lama_bekerja) / 365;
                row[12] = (TimeUnit.MILLISECONDS.toDays(lama_bekerja) % 365) / 30;
                row[13] = (TimeUnit.MILLISECONDS.toDays(lama_bekerja) % 365) % 30;

                long umur_now = 0;
                if (lahir != null) {
                    umur_now = Math.abs(today.getTime() - lahir.getTime());
                }
                row[14] = TimeUnit.MILLISECONDS.toDays(umur_now) / 365;
                model.addRow(row);
            }

            ColumnsAutoSizer.sizeColumnsToFit(table_data_ktp);
            int rowData = table_data_ktp.getRowCount();
            label_total_data.setText(Integer.toString(rowData));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_Data_Karyawan_wltsub.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel_search_karyawan = new javax.swing.JPanel();
        txt_search_karyawan = new javax.swing.JTextField();
        button_search_karyawan = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        Date_Search1 = new com.toedter.calendar.JDateChooser();
        Date_Search2 = new com.toedter.calendar.JDateChooser();
        jLabel8 = new javax.swing.JLabel();
        txt_search_id = new javax.swing.JTextField();
        ComboBox_filter_tanggal = new javax.swing.JComboBox<>();
        jLabel16 = new javax.swing.JLabel();
        txt_search_bagian = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        txt_search_status = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        table_data_ktp = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        label_total_data = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Data Karyawan", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N
        setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        setPreferredSize(new java.awt.Dimension(1366, 700));

        jPanel_search_karyawan.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_search_karyawan.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));

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
        jLabel2.setText("Nama Karyawan :");

        jLabel34.setBackground(new java.awt.Color(255, 255, 255));
        jLabel34.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        jLabel34.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel34.setText("-");

        jLabel35.setBackground(new java.awt.Color(255, 255, 255));
        jLabel35.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        jLabel35.setText("Date Filter by ");

        Date_Search1.setBackground(new java.awt.Color(255, 255, 255));
        Date_Search1.setToolTipText("");
        Date_Search1.setDateFormatString("dd MMM yyyy");
        Date_Search1.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        Date_Search1.setMinSelectableDate(new java.util.Date(1483207315000L));

        Date_Search2.setBackground(new java.awt.Color(255, 255, 255));
        Date_Search2.setDateFormatString("dd MMM yyyy");
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

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel16.setText("Bagian :");

        txt_search_bagian.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_bagian.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_bagianKeyPressed(evt);
            }
        });

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel17.setText("Status :");

        txt_search_status.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_status.setText("IN");
        txt_search_status.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_statusKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_search_karyawanLayout = new javax.swing.GroupLayout(jPanel_search_karyawan);
        jPanel_search_karyawan.setLayout(jPanel_search_karyawanLayout);
        jPanel_search_karyawanLayout.setHorizontalGroup(
            jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_search_karyawanLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txt_search_karyawan)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txt_search_id)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txt_search_bagian)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txt_search_status)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_search_karyawanLayout.createSequentialGroup()
                        .addComponent(jLabel35)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_filter_tanggal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_search_karyawanLayout.createSequentialGroup()
                        .addComponent(Date_Search1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_Search2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_search_karyawan)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel_search_karyawanLayout.setVerticalGroup(
            jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_search_karyawanLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_search_karyawanLayout.createSequentialGroup()
                        .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
                        .addComponent(txt_search_status, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_search_karyawanLayout.createSequentialGroup()
                        .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
                        .addComponent(txt_search_bagian, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_search_karyawanLayout.createSequentialGroup()
                        .addGroup(jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
                        .addGroup(jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_search_karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_search_id, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_search_karyawanLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel34, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Date_Search1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_search_karyawanLayout.createSequentialGroup()
                                .addGroup(jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(ComboBox_filter_tanggal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_Search2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(button_search_karyawan, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );

        table_data_ktp.setAutoCreateRowSorter(true);
        table_data_ktp.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        table_data_ktp.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "NIK", "Nama", "Status", "Posisi", "Bagian", "Tgl Masuk", "Tgl Keluar", "Tgl Lahir", "No Telp", "E-mail", "Th", "Bln", "Hr", "Umur"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
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
        if (table_data_ktp.getColumnModel().getColumnCount() > 0) {
            table_data_ktp.getColumnModel().getColumn(2).setMaxWidth(200);
        }

        jLabel3.setText("Total Data :");

        label_total_data.setText("0");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel_search_karyawan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_data)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1336, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel_search_karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 559, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_total_data, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txt_search_karyawanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_karyawanKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_search_karyawanKeyPressed

    private void button_search_karyawanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_karyawanActionPerformed
        // TODO add your handling code here:
        refreshTable();
    }//GEN-LAST:event_button_search_karyawanActionPerformed

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

    private void txt_search_statusKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_statusKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_search_statusKeyPressed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_filter_tanggal;
    private com.toedter.calendar.JDateChooser Date_Search1;
    private com.toedter.calendar.JDateChooser Date_Search2;
    public static javax.swing.JButton button_search_karyawan;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel_search_karyawan;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel label_total_data;
    public static javax.swing.JTable table_data_ktp;
    private javax.swing.JTextField txt_search_bagian;
    private javax.swing.JTextField txt_search_id;
    private javax.swing.JTextField txt_search_karyawan;
    private javax.swing.JTextField txt_search_status;
    // End of variables declaration//GEN-END:variables

}
