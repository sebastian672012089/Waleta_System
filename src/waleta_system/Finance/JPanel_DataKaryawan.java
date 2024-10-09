package waleta_system.Finance;

import waleta_system.Class.Utility;
import java.awt.Color;
import java.awt.Component;
import java.awt.HeadlessException;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.ExportToExcel;
import waleta_system.HRD.JDialog_Show_KTP;
import waleta_system.Interface.InterfacePanel;
import waleta_system.MainForm;

public class JPanel_DataKaryawan extends javax.swing.JPanel implements InterfacePanel {

    String sql = null;
    ResultSet rs;
    PreparedStatement pst;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Date today = new Date();
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    DefaultTableModel dm = new DefaultTableModel();
    JFileChooser chooser = new JFileChooser();

    @Override
    public void init() {
        try {
            if (MainForm.Login_idPegawai.equals("20171201644")//INDRIKA
                    || MainForm.Login_idPegawai.equals("20230907768")//diyan
                    ) {//INDRIKA
                button_ubah_level_gaji1.setEnabled(true);
                button_buka_kunci_grup.setEnabled(true);
                button_daftar_bpjs.setEnabled(true);
                button_refresh_bpjs.setEnabled(true);
                button_daftar_bpjs_tk.setEnabled(true);
                button_ubah_level_gaji.setEnabled(true);
            }

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
            refreshTable();
        } catch (Exception ex) {
            Logger.getLogger(JPanel_DataKaryawan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable() {
        try {
            DefaultTableModel model = (DefaultTableModel) table_data.getModel();
            model.setRowCount(0);
            int IN = 0, OUT = 0, BATAL = 0, STRIP = 0;
            String filter_tanggal = "";
            String bagian = "AND `nama_bagian` LIKE '%" + txt_search_bagian.getText() + "%' ";
            String departemen = "AND `kode_departemen` = '" + ComboBox_departemen_karyawan.getSelectedItem().toString() + "' ";
            String Status = "AND `status` = '" + ComboBox_status_karyawan.getSelectedItem().toString() + "' ";
            String kelamin = "AND `jenis_kelamin` = '" + ComboBox_gender.getSelectedItem().toString() + "' ";
            String posisi = "AND `posisi` = '" + ComboBox_posisi.getSelectedItem().toString() + "' ";
            String level_gaji = "AND `level_gaji` LIKE '" + txt_search_level_gaji.getText() + "' ";
            if ("All".equals(ComboBox_status_karyawan.getSelectedItem().toString())) {
                Status = "";
            }
            if (txt_search_bagian.getText() == null || "".equals(txt_search_bagian.getText())) {
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
            if (txt_search_level_gaji.getText() == null || txt_search_level_gaji.getText().equals("")) {
                level_gaji = "";
            }

            if (Date_Search1.getDate() != null && Date_Search2.getDate() != null) {
                switch (ComboBox_filter_tanggal.getSelectedIndex()) {
//                case 0:
//                    filter_tanggal = "tanggal_interview";
//                    break;
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
            sql = "SELECT `id_pegawai`,`nik_ktp`,`nama_pegawai`,`jenis_kelamin`,`alamat`,`nama_ibu`,`nama_bagian`,`kode_departemen`,`tanggal_masuk`,`tanggal_keluar`,`status`,`level_gaji`, `rek_cimb`, `posisi`, `no_telp`, `email`, `potongan_bpjs`, `potongan_bpjs_tk`, `jalur_jemputan`, `jam_kerja`, `status_pajak`, `no_npwp` "
                    + "FROM `tb_karyawan` "
                    + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`"
                    + "WHERE `nama_pegawai` LIKE '%" + txt_search_karyawan.getText() + "%' "
                    + "AND `nik_ktp` LIKE '%" + txt_search_nik.getText() + "%' "
                    + bagian
                    + departemen
                    + kelamin
                    + Status
                    + posisi
                    + level_gaji
                    + filter_tanggal
                    + "ORDER BY `tanggal_masuk` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                Object[] row = new Object[30];
                row[0] = rs.getString("id_pegawai");
                row[1] = rs.getString("nik_ktp");
                row[2] = rs.getString("rek_cimb");
                row[3] = rs.getString("nama_pegawai");
                if (rs.getString("jenis_kelamin").equalsIgnoreCase("Laki-laki")) {
                    row[4] = "L";
                } else if (rs.getString("jenis_kelamin").equalsIgnoreCase("Perempuan")) {
                    row[4] = "P";
                } else {
                    row[4] = "-";
                }
                row[5] = rs.getString("alamat");
                row[6] = rs.getString("nama_ibu");
                row[7] = rs.getString("no_telp");
                row[8] = rs.getString("email");
                if (rs.getString("nama_bagian") != null) {
                    if (rs.getString("nama_bagian").split("-").length > 1) {
                        row[9] = rs.getString("nama_bagian").split("-")[0];//posisi
                        row[10] = rs.getString("nama_bagian").split("-")[1];//dept
                        row[11] = rs.getString("nama_bagian").split("-")[2];//divisi
                        row[12] = rs.getString("nama_bagian").split("-")[3];//bagian
                        row[13] = rs.getString("nama_bagian").split("-")[4];//ruang
                    } else {
                        row[12] = rs.getString("nama_bagian").split("-")[0];//bagian
                    }
                }
                row[14] = rs.getString("posisi");
                row[15] = rs.getString("level_gaji");
                row[16] = rs.getDate("tanggal_masuk");
                row[17] = rs.getDate("tanggal_keluar");
                row[18] = rs.getString("status");
                switch (rs.getString("status")) {
                    case "IN":
                        IN++;
                        break;
                    case "OUT":
                        OUT++;
                        break;
                    case "BATAL":
                        BATAL++;
                        break;
                    default:
                        STRIP++;
                        break;
                }
                //menghitung lama karyawan bekerja
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
                row[19] = TimeUnit.MILLISECONDS.toDays(lama_bekerja) / 365 + " Tahun "
                        + (TimeUnit.MILLISECONDS.toDays(lama_bekerja) % 365) / 30 + " Bulan "
                        + (TimeUnit.MILLISECONDS.toDays(lama_bekerja) % 365) % 30 + " Hari";

                switch (rs.getInt("potongan_bpjs")) {
                    case 0:
                        row[20] = "Belum terdaftar";
                        break;
                    case 1:
                        row[20] = "Sudah terdaftar";
                        break;
                    case 2:
                        row[20] = "Sudah terbayar";
                        break;
                    default:
                        break;
                }

                switch (rs.getInt("potongan_bpjs_tk")) {
                    case 0:
                        row[21] = "Belum terdaftar";
                        break;
                    case 1:
                        row[21] = "Sudah terdaftar";
                        break;
                    case 2:
                        row[21] = "Sudah terbayar";
                        break;
                    default:
                        break;
                }
                row[22] = rs.getString("jalur_jemputan");
                row[23] = rs.getString("jam_kerja");
                row[24] = rs.getString("status_pajak");
                row[25] = rs.getString("no_npwp");

                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(table_data);

            table_data.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if (table_data.getValueAt(row, 11) == null || table_data.getValueAt(row, 12) == null || table_data.getValueAt(row, 15) == null) {
                        if (isSelected) {
                            comp.setBackground(table_data.getSelectionBackground());
                            comp.setForeground(table_data.getSelectionForeground());
                        } else {
                            comp.setBackground(table_data.getBackground());
                            comp.setForeground(table_data.getForeground());
                        }
                    } else if (table_data.getValueAt(row, 11).toString().toUpperCase().contains("CABUT")
                            && //divisi
                            (table_data.getValueAt(row, 12).toString().toUpperCase().contains("BORONG") || table_data.getValueAt(row, 12).toString().toUpperCase().contains("TRAINING"))
                            && //bagian
                            !table_data.getValueAt(row, 15).toString().toUpperCase().contains("BORONG")//level gaji
                            ) {
                        if (isSelected) {
                            comp.setBackground(table_data.getSelectionBackground());
                            comp.setForeground(table_data.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.red);
                            comp.setForeground(Color.WHITE);
                        }
                    } else if (table_data.getValueAt(row, 11).toString().toUpperCase().contains("CABUT")
                            && //divisi
                            !(table_data.getValueAt(row, 12).toString().toUpperCase().contains("BORONG") || table_data.getValueAt(row, 12).toString().toUpperCase().contains("TRAINING"))
                            && //bagian
                            table_data.getValueAt(row, 15).toString().toUpperCase().contains("BORONG")//level gaji
                            ) {
                        if (isSelected) {
                            comp.setBackground(table_data.getSelectionBackground());
                            comp.setForeground(table_data.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.red);
                            comp.setForeground(Color.WHITE);
                        }
                    } else {
                        if (isSelected) {
                            comp.setBackground(table_data.getSelectionBackground());
                            comp.setForeground(table_data.getSelectionForeground());
                        } else {
                            comp.setBackground(table_data.getBackground());
                            comp.setForeground(table_data.getForeground());
                        }
                    }
                    return comp;
                }
            });
            table_data.repaint();

            int rowDataMasuk = table_data.getRowCount();
            label_total_data_masuk.setText(Integer.toString(rowDataMasuk));
            label_total_in.setText(Integer.toString(IN));
            label_total_out.setText(Integer.toString(OUT));
            label_total_batal.setText(Integer.toString(BATAL));
            label_total_strip.setText(Integer.toString(STRIP));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_DataKaryawan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public JPanel_DataKaryawan() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel_search_karyawan = new javax.swing.JPanel();
        txt_search_karyawan = new javax.swing.JTextField();
        button_search_karyawan = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        Date_Search1 = new com.toedter.calendar.JDateChooser();
        Date_Search2 = new com.toedter.calendar.JDateChooser();
        jLabel8 = new javax.swing.JLabel();
        txt_search_nik = new javax.swing.JTextField();
        ComboBox_filter_tanggal = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        ComboBox_status_karyawan = new javax.swing.JComboBox<>();
        button_show_ktp = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        ComboBox_gender = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        ComboBox_departemen_karyawan = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        ComboBox_posisi = new javax.swing.JComboBox<>();
        button_hari_kerja = new javax.swing.JButton();
        button_ubah_level_gaji = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        txt_search_level_gaji = new javax.swing.JTextField();
        txt_search_bagian = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        table_data = new javax.swing.JTable();
        label_total_in = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        label_total_data_masuk = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        label_total_out = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        label_total_batal = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        label_total_strip = new javax.swing.JLabel();
        button_ubah_level_gaji1 = new javax.swing.JButton();
        button_buka_kunci_grup = new javax.swing.JButton();
        button_daftar_bpjs = new javax.swing.JButton();
        button_refresh_bpjs = new javax.swing.JButton();
        button_daftar_bpjs_tk = new javax.swing.JButton();
        button_export_data_karyawan_masuk = new javax.swing.JButton();

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
        jLabel2.setText("Nama :");

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
        jLabel8.setText("NIK KTP :");

        txt_search_nik.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_nik.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_nikKeyPressed(evt);
            }
        });

        ComboBox_filter_tanggal.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_filter_tanggal.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tanggal Masuk", "Tanggal Keluar" }));

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel11.setText("Status :");

        ComboBox_status_karyawan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        button_show_ktp.setBackground(new java.awt.Color(255, 255, 255));
        button_show_ktp.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        button_show_ktp.setText("Show KTP");
        button_show_ktp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_show_ktpActionPerformed(evt);
            }
        });

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel14.setText("Jenis Kelamin :");

        ComboBox_gender.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_gender.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "PEREMPUAN", "LAKI-LAKI" }));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("Departemen :");

        ComboBox_departemen_karyawan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_departemen_karyawan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel6.setText("Bagian :");

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel15.setText("Posisi :");

        ComboBox_posisi.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_posisi.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        button_hari_kerja.setBackground(new java.awt.Color(255, 255, 255));
        button_hari_kerja.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        button_hari_kerja.setText("Hari Kerja");
        button_hari_kerja.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_hari_kerjaActionPerformed(evt);
            }
        });

        button_ubah_level_gaji.setBackground(new java.awt.Color(255, 255, 255));
        button_ubah_level_gaji.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        button_ubah_level_gaji.setText("Edit No Rek & Level Gaji");
        button_ubah_level_gaji.setEnabled(false);
        button_ubah_level_gaji.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_ubah_level_gajiActionPerformed(evt);
            }
        });

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel16.setText("Level Gaji :");

        txt_search_level_gaji.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_level_gaji.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_level_gajiKeyPressed(evt);
            }
        });

        txt_search_bagian.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_bagian.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_bagianKeyPressed(evt);
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
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_nik, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_status_karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_posisi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_level_gaji, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_ubah_level_gaji)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_hari_kerja)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_show_ktp))
                    .addGroup(jPanel_search_karyawanLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_gender, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_departemen_karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_bagian, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_filter_tanggal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_Search1, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_Search2, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_search_karyawan)
                        .addGap(0, 192, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel_search_karyawanLayout.setVerticalGroup(
            jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_search_karyawanLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_search_karyawanLayout.createSequentialGroup()
                        .addGroup(jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(txt_search_karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ComboBox_gender, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ComboBox_departemen_karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Date_Search2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ComboBox_filter_tanggal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_search_karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Date_Search1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_search_bagian, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                .addComponent(txt_search_nik, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(button_show_ktp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(button_hari_kerja, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(button_ubah_level_gaji, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel_search_karyawanLayout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addGroup(jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                .addComponent(ComboBox_status_karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(ComboBox_posisi, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txt_search_level_gaji, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        table_data.setAutoCreateRowSorter(true);
        table_data.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        table_data.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "NIK", "No Rek CIMB", "Nama", "Gender", "Desa", "Ibu Kandung", "No Telp", "Email", "Posisi", "Dept.", "Divisi", "Bagian", "Ruang", "Posisi", "Level Gaji", "Tgl Masuk", "Tgl Keluar", "Status", "Masa Kerja", "BPJS KS", "BPJS TK", "Jemputan", "Jam Kerja", "Status Pajak", "No NPWP"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table_data.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                table_dataMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(table_data);

        label_total_in.setBackground(new java.awt.Color(255, 255, 255));
        label_total_in.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_total_in.setText("0");

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel9.setText("Total IN :");

        label_total_data_masuk.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_masuk.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_total_data_masuk.setText("0");

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel4.setText("Total Data :");

        label_total_out.setBackground(new java.awt.Color(255, 255, 255));
        label_total_out.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_total_out.setText("0");

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel10.setText("Total OUT :");

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel12.setText("Total Batal :");

        label_total_batal.setBackground(new java.awt.Color(255, 255, 255));
        label_total_batal.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_total_batal.setText("0");

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel13.setText("Total - :");

        label_total_strip.setBackground(new java.awt.Color(255, 255, 255));
        label_total_strip.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_total_strip.setText("0");

        button_ubah_level_gaji1.setBackground(new java.awt.Color(255, 255, 255));
        button_ubah_level_gaji1.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        button_ubah_level_gaji1.setText("Edit No Rek & Level Gaji From CSV");
        button_ubah_level_gaji1.setEnabled(false);
        button_ubah_level_gaji1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_ubah_level_gaji1ActionPerformed(evt);
            }
        });

        button_buka_kunci_grup.setBackground(new java.awt.Color(255, 255, 255));
        button_buka_kunci_grup.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        button_buka_kunci_grup.setText("UNLOCK Grup Karyawan");
        button_buka_kunci_grup.setEnabled(false);
        button_buka_kunci_grup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_buka_kunci_grupActionPerformed(evt);
            }
        });

        button_daftar_bpjs.setBackground(new java.awt.Color(255, 255, 255));
        button_daftar_bpjs.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        button_daftar_bpjs.setText("csv daftar BPJS KS");
        button_daftar_bpjs.setEnabled(false);
        button_daftar_bpjs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_daftar_bpjsActionPerformed(evt);
            }
        });

        button_refresh_bpjs.setBackground(new java.awt.Color(255, 255, 255));
        button_refresh_bpjs.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        button_refresh_bpjs.setText("Refresh Potongan BPJS & BPJS TK");
        button_refresh_bpjs.setEnabled(false);
        button_refresh_bpjs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refresh_bpjsActionPerformed(evt);
            }
        });

        button_daftar_bpjs_tk.setBackground(new java.awt.Color(255, 255, 255));
        button_daftar_bpjs_tk.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        button_daftar_bpjs_tk.setText("csv daftar BPJS TK");
        button_daftar_bpjs_tk.setEnabled(false);
        button_daftar_bpjs_tk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_daftar_bpjs_tkActionPerformed(evt);
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel_search_karyawan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_data_masuk, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_in, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_out, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_strip, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_batal, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(button_ubah_level_gaji1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_buka_kunci_grup)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_daftar_bpjs)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_daftar_bpjs_tk)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_refresh_bpjs)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_export_data_karyawan_masuk)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel_search_karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_total_data_masuk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_in, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_out, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_strip, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_batal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(button_ubah_level_gaji1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_buka_kunci_grup, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_daftar_bpjs, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_daftar_bpjs_tk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_refresh_bpjs, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export_data_karyawan_masuk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 510, Short.MAX_VALUE)
                .addContainerGap())
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

    private void button_export_data_karyawan_masukActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_data_karyawan_masukActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model_table_masuk = (DefaultTableModel) table_data.getModel();
        ExportToExcel.writeToExcel(model_table_masuk, this);
    }//GEN-LAST:event_button_export_data_karyawan_masukActionPerformed

    private void txt_search_nikKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_nikKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_search_nikKeyPressed

    private void button_show_ktpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_show_ktpActionPerformed
        // TODO add your handling code here:
        int x = table_data.getSelectedRow();
        if (x == -1) {
            JOptionPane.showMessageDialog(this, "Silahkan pilih salah satu data pada tabel !");
        } else {
            String id = table_data.getValueAt(x, 0).toString();
            String nik = table_data.getValueAt(x, 1).toString();
            String nama = table_data.getValueAt(x, 3).toString();
            JDialog_Show_KTP dialog = new JDialog_Show_KTP(new javax.swing.JFrame(), true, nik, nama);
            dialog.show_ktp_local(id);
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            dialog.setEnabled(true);
            dialog.setResizable(false);
        }
    }//GEN-LAST:event_button_show_ktpActionPerformed

    private void table_dataMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_table_dataMouseClicked
        // TODO add your handling code here:
        int i = table_data.getSelectedRow();
        if (evt.getClickCount() == 2) {
            String id = table_data.getValueAt(i, 3).toString();
            String Nama = table_data.getValueAt(i, 3).toString();
            String no_rek = table_data.getValueAt(i, 3).toString();
            String level_gaji = table_data.getValueAt(i, 3).toString();

        }
    }//GEN-LAST:event_table_dataMouseClicked

    private void button_hari_kerjaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_hari_kerjaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_button_hari_kerjaActionPerformed

    private void button_ubah_level_gajiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_ubah_level_gajiActionPerformed
        // TODO add your handling code here:
        int j = table_data.getSelectedRow();
        String ID = table_data.getValueAt(j, 0).toString();
        try {
            if (j < 0) {
                JOptionPane.showMessageDialog(this, "Anda belum memilih data yang akan di edit");
            } else {
                if (table_data.getValueAt(j, 18).toString().equals("IN")) {
                    JDialog_Edit_NoRek_LevelGaji dialog = new JDialog_Edit_NoRek_LevelGaji(new javax.swing.JFrame(), true, ID);
                    dialog.pack();
                    dialog.setLocationRelativeTo(this);
                    dialog.setVisible(true);
                    dialog.setEnabled(true);
                    dialog.setResizable(false);
                    refreshTable();
                } else {
                    JOptionPane.showMessageDialog(this, "Untuk edit level gaji, status karyawan harus 'IN' ");
                }
            }
        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }//GEN-LAST:event_button_ubah_level_gajiActionPerformed

    private void button_ubah_level_gaji1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_ubah_level_gaji1ActionPerformed
        // TODO add your handling code here:
        try {
            int n = 0;
            chooser.setDialogTitle("Select CSV file to import!");
            int result = chooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                Utility.db.getConnection();
                File file = chooser.getSelectedFile();
                String filename1 = file.getAbsolutePath();
                try ( BufferedReader br = new BufferedReader(new FileReader(filename1))) {
                    String line;
                    String Query = null;
                    try {
                        while ((line = br.readLine()) != null) {
                            String[] value = line.split(";");
                            Query = "UPDATE `tb_karyawan` SET `level_gaji` = '" + value[1] + "' WHERE `id_pegawai` = '" + value[0] + "'";
//                            System.out.println(Query);
                            Utility.db.getConnection().prepareStatement(Query);
                            if ((Utility.db.getStatement().executeUpdate(Query)) == 1) {
                                n++;
                            } else {
                                JOptionPane.showMessageDialog(this, "Failed insert " + value[0]);
                            }
                        }
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(this, ex.getMessage());
                        JOptionPane.showMessageDialog(this, "Data Berhasil Masuk : " + n);
                        Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {
                        JOptionPane.showMessageDialog(this, "Data Berhasil Masuk : " + n);
                        refreshTable();
                    }
                }
            }
        } catch (HeadlessException | IOException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }//GEN-LAST:event_button_ubah_level_gaji1ActionPerformed

    private void button_buka_kunci_grupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_buka_kunci_grupActionPerformed
        // TODO add your handling code here:
        try {
            int dialogResult = JOptionPane.showConfirmDialog(this, "yakin akan membuka kunci data grup?", "Warning", 0);
            if (dialogResult == JOptionPane.YES_OPTION) {
                // delete code here
                sql = "UPDATE `tb_grup` SET `status`='PROSES' WHERE 1";
                if (Utility.db.getStatement().executeUpdate(sql) > 0) {
                    JOptionPane.showMessageDialog(this, "Kunci Grup dibuka !");
                }
            }
        } catch (HeadlessException | SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_buka_kunci_grupActionPerformed

    private void button_daftar_bpjsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_daftar_bpjsActionPerformed
        // TODO add your handling code here:
        int n = 0;
        try {
            JOptionPane.showMessageDialog(this, "Harap pastikan kolom pertama adalah kolom ID PEGAWAI!");
            chooser.setDialogTitle("Select an CSV file to import!");
            int result = chooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                Utility.db.getConnection();
                File file = chooser.getSelectedFile();
                String filename1 = file.getAbsolutePath();
                try ( BufferedReader br = new BufferedReader(new FileReader(filename1))) {
                    String line;
                    String Query = null;
                    try {
                        while ((line = br.readLine()) != null) {
                            String[] value = line.split(",");
                            Query = "UPDATE `tb_karyawan` SET "
                                    + "`potongan_bpjs`='1' "
                                    + "WHERE `id_pegawai`='" + value[0] + "' AND `potongan_bpjs`='0'";
                            Utility.db.getConnection().prepareStatement(Query);
                            if ((Utility.db.getStatement().executeUpdate(Query)) > 0) {
//                                System.out.println(value[0]);
                                n++;
//                                System.out.println(n);
                            } else {
                                JOptionPane.showMessageDialog(this, "Failed insert : " + value[0]);
                            }
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, ex.getMessage());
                        JOptionPane.showMessageDialog(this, "Data Berhasil Masuk : " + n);
                        Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {
                        JOptionPane.showMessageDialog(this, "Data Berhasil Masuk : " + n);
                    }
                }
            }
            refreshTable();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }//GEN-LAST:event_button_daftar_bpjsActionPerformed

    private void button_refresh_bpjsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refresh_bpjsActionPerformed
        // TODO add your handling code here:
        try {

            LocalDate currentDate = LocalDate.now();
            int currentDay = currentDate.getDayOfMonth();
            if (currentDay < 8) {
                int dialogResult = JOptionPane.showConfirmDialog(this, "Yakin akan refresh semua potongan BPJS?", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    sql = "UPDATE `tb_karyawan` SET `potongan_bpjs`=1 WHERE `potongan_bpjs`=2";
                    Utility.db.getConnection().createStatement();
                    Utility.db.getStatement().executeUpdate(sql);
                    JOptionPane.showMessageDialog(this, "Potongan bpjs kesehatan telah memasuki bulan baru!");
                    sql = "UPDATE `tb_karyawan` SET `potongan_bpjs_tk`=1 WHERE `potongan_bpjs_tk`=2";
                    Utility.db.getConnection().createStatement();
                    Utility.db.getStatement().executeUpdate(sql);
                    JOptionPane.showMessageDialog(this, "Potongan bpjs ketenagakerjaan telah memasuki bulan baru!");
                }
            } else {
                int dialogResult = JOptionPane.showConfirmDialog(this, "Sudah lewat tanggal 7 apakah yakin akan refresh semua potongan BPJS?", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    sql = "UPDATE `tb_karyawan` SET `potongan_bpjs`=1 WHERE `potongan_bpjs`=2";
                    Utility.db.getConnection().createStatement();
                    Utility.db.getStatement().executeUpdate(sql);
                    JOptionPane.showMessageDialog(this, "Potongan bpjs kesehatan telah memasuki bulan baru!");
                    sql = "UPDATE `tb_karyawan` SET `potongan_bpjs_tk`=1 WHERE `potongan_bpjs_tk`=2";
                    Utility.db.getConnection().createStatement();
                    Utility.db.getStatement().executeUpdate(sql);
                    JOptionPane.showMessageDialog(this, "Potongan bpjs ketenagakerjaan telah memasuki bulan baru!");
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_refresh_bpjsActionPerformed

    private void button_daftar_bpjs_tkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_daftar_bpjs_tkActionPerformed
        // TODO add your handling code here:
        int n = 0;
        try {
            JOptionPane.showMessageDialog(this, "Harap pastikan kolom pertama adalah kolom ID PEGAWAI!");
            chooser.setDialogTitle("Select an CSV file to import!");
            int result = chooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                Utility.db.getConnection();
                File file = chooser.getSelectedFile();
                String filename1 = file.getAbsolutePath();
                try ( BufferedReader br = new BufferedReader(new FileReader(filename1))) {
                    String line;
                    String Query = null;
                    try {
                        while ((line = br.readLine()) != null) {
                            String[] value = line.split(",");
                            Query = "UPDATE `tb_karyawan` SET "
                                    + "`potongan_bpjs_tk`='1' "
                                    + "WHERE `id_pegawai`='" + value[0] + "' AND `potongan_bpjs_tk`='0'";
                            Utility.db.getConnection().prepareStatement(Query);
                            if ((Utility.db.getStatement().executeUpdate(Query)) > 0) {
//                                System.out.println(value[0]);
                                n++;
//                                System.out.println(n);
                            } else {
                                JOptionPane.showMessageDialog(this, "Failed insert : " + value[0]);
                            }
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, ex.getMessage());
                        JOptionPane.showMessageDialog(this, "Data Berhasil Masuk : " + n);
                        Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {
                        JOptionPane.showMessageDialog(this, "Data Berhasil Masuk : " + n);
                    }
                }
            }
            refreshTable();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }//GEN-LAST:event_button_daftar_bpjs_tkActionPerformed

    private void txt_search_level_gajiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_level_gajiKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_search_level_gajiKeyPressed

    private void txt_search_bagianKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_bagianKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_search_bagianKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_departemen_karyawan;
    private javax.swing.JComboBox<String> ComboBox_filter_tanggal;
    private javax.swing.JComboBox<String> ComboBox_gender;
    private javax.swing.JComboBox<String> ComboBox_posisi;
    private javax.swing.JComboBox<String> ComboBox_status_karyawan;
    private com.toedter.calendar.JDateChooser Date_Search1;
    private com.toedter.calendar.JDateChooser Date_Search2;
    private javax.swing.JButton button_buka_kunci_grup;
    private javax.swing.JButton button_daftar_bpjs;
    private javax.swing.JButton button_daftar_bpjs_tk;
    private javax.swing.JButton button_export_data_karyawan_masuk;
    private javax.swing.JButton button_hari_kerja;
    private javax.swing.JButton button_refresh_bpjs;
    public static javax.swing.JButton button_search_karyawan;
    private javax.swing.JButton button_show_ktp;
    private javax.swing.JButton button_ubah_level_gaji;
    private javax.swing.JButton button_ubah_level_gaji1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel_search_karyawan;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel label_total_batal;
    private javax.swing.JLabel label_total_data_masuk;
    private javax.swing.JLabel label_total_in;
    private javax.swing.JLabel label_total_out;
    private javax.swing.JLabel label_total_strip;
    public static javax.swing.JTable table_data;
    private javax.swing.JTextField txt_search_bagian;
    private javax.swing.JTextField txt_search_karyawan;
    private javax.swing.JTextField txt_search_level_gaji;
    private javax.swing.JTextField txt_search_nik;
    // End of variables declaration//GEN-END:variables

}
