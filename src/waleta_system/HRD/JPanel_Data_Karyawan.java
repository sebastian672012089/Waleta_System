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
import javax.swing.filechooser.FileNameExtensionFilter;
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
import waleta_system.MainForm;

public class JPanel_Data_Karyawan extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    PreparedStatement pst;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Date today = new Date();
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    DefaultTableModel dm = new DefaultTableModel();
    JFileChooser chooser = new JFileChooser();
    ArrayList<DataKaryawan> KaryawanList = new ArrayList<>();

    public JPanel_Data_Karyawan() {
        initComponents();
        //Hijack the keyboard manager
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new KeyEventDispatcher() {
            public boolean dispatchKeyEvent(KeyEvent e) {
                if (e.getID() == KeyEvent.KEY_PRESSED) {
                    if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_N) {
                        button_insert_karyawanActionPerformed(null);
                    }
                }
                return false;
            }
        });
    }

    public void init() {
        try {
            ComboBox_filter_posisi_bagian.removeAllItems();
            ComboBox_filter_posisi_bagian.addItem("All");
            sql = "SELECT DISTINCT(`posisi_bagian`) AS 'posisi_bagian' "
                    + "FROM `tb_bagian` "
                    + "WHERE `status_bagian` = 1 AND `posisi_bagian` IS NOT NULL "
                    + "ORDER BY `posisi_bagian`";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_filter_posisi_bagian.addItem(rs.getString("posisi_bagian"));
            }

            ComboBox_filter_departemen.removeAllItems();
            ComboBox_filter_departemen.addItem("All");
            sql = "SELECT DISTINCT(`kode_departemen`) AS 'kode_departemen' "
                    + "FROM `tb_bagian` "
                    + "WHERE `status_bagian` = 1 AND `kode_departemen` IS NOT NULL "
                    + "ORDER BY `kode_departemen`";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_filter_departemen.addItem(rs.getString("kode_departemen"));
            }

            ComboBox_filter_divisi.removeAllItems();
            ComboBox_filter_divisi.addItem("All");
            sql = "SELECT DISTINCT(`divisi_bagian`) AS 'divisi_bagian' "
                    + "FROM `tb_bagian` "
                    + "WHERE `status_bagian` = 1  AND `divisi_bagian` IS NOT NULL "
                    + "ORDER BY `divisi_bagian`";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_filter_divisi.addItem(rs.getString("divisi_bagian"));
            }

            ComboBox_filter_bagian.removeAllItems();
            ComboBox_filter_bagian.addItem("All");
            sql = "SELECT DISTINCT(`bagian_bagian`) AS 'bagian_bagian' "
                    + "FROM `tb_bagian` "
                    + "WHERE `status_bagian` = 1  AND `bagian_bagian` IS NOT NULL "
                    + "ORDER BY `bagian_bagian`";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_filter_bagian.addItem(rs.getString("bagian_bagian"));
            }

            ComboBox_filter_ruang.removeAllItems();
            ComboBox_filter_ruang.addItem("All");
            sql = "SELECT DISTINCT(`ruang_bagian`) AS 'ruang_bagian' "
                    + "FROM `tb_bagian` "
                    + "WHERE `status_bagian` = 1  AND `ruang_bagian` IS NOT NULL "
                    + "ORDER BY `ruang_bagian`";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_filter_ruang.addItem(rs.getString("ruang_bagian"));
            }

            ComboBox_posisi.removeAllItems();
            ComboBox_posisi.addItem("All");
            sql = "SELECT DISTINCT(`posisi`) AS 'posisi' FROM `tb_karyawan` WHERE `posisi` IS NOT NULL";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_posisi.addItem(rs.getString("posisi"));
            }
//            ComboBox_status_karyawan.removeAllItems();
//            ComboBox_status_karyawan.addItem("All");
//            sql = "SELECT DISTINCT(`status`) AS 'status' FROM `tb_karyawan` WHERE `status` IS NOT NULL";
//            rs = Utility.db.getStatement().executeQuery(sql);
//            while (rs.next()) {
//                ComboBox_status_karyawan.addItem(rs.getString("status"));
//            }
//            ComboBox_status_karyawan.setSelectedItem("IN");
            refreshTable();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, e);
        }

        button_status_IN.setEnabled(false);
        button_status_OUT.setEnabled(false);
        button_status_batal.setEnabled(false);
        table_data_ktp.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && table_data_ktp.getSelectedRow() != -1) {
                    int i = table_data_ktp.getSelectedRow();
                    String status = table_data_ktp.getValueAt(i, 4).toString();
                    switch (status) {
                        case "IN":
                            button_status_IN.setEnabled(false);
                            button_status_OUT.setEnabled(true);
                            button_status_batal.setEnabled(false);
                            button_status_absen.setEnabled(true);
                            break;
                        case "OUT":
                            button_status_IN.setEnabled(false);
                            button_status_OUT.setEnabled(true);
                            button_status_batal.setEnabled(false);
                            button_status_absen.setEnabled(false);
                            break;
                        case "OUT-SUB":
                            button_status_IN.setEnabled(false);
                            button_status_OUT.setEnabled(false);
                            button_status_batal.setEnabled(false);
                            button_status_absen.setEnabled(false);
                            break;
                        case "BATAL":
                            button_status_IN.setEnabled(true);
                            button_status_OUT.setEnabled(false);
                            button_status_batal.setEnabled(false);
                            button_status_absen.setEnabled(false);
                            break;
                        case "-":
                            button_status_IN.setEnabled(true);
                            button_status_OUT.setEnabled(false);
                            button_status_batal.setEnabled(true);
                            button_status_absen.setEnabled(false);
                            break;
                        case "ABSEN":
                            button_status_IN.setEnabled(true);
                            button_status_OUT.setEnabled(true);
                            button_status_batal.setEnabled(false);
                            button_status_absen.setEnabled(false);
                            break;
                        default:
                            break;
                    }
                }
            }
        });
        table_data_berkas.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && table_data_berkas.getSelectedRow() != -1) {
                    int i = table_data_berkas.getSelectedRow();
                    button_open_fc_ktp.setEnabled((boolean) table_data_berkas.getValueAt(i, 3));
                    button_open_sertifikat_vaksin1.setEnabled((boolean) table_data_berkas.getValueAt(i, 4));
                    button_open_sertifikat_vaksin2.setEnabled((boolean) table_data_berkas.getValueAt(i, 5));
                }
            }
        });
    }

    private void KaryawanList() {
        try {
            KaryawanList = new ArrayList<>();
            String posisi_bagian = "";
            if (ComboBox_filter_posisi_bagian.getSelectedItem() != null && !ComboBox_filter_posisi_bagian.getSelectedItem().toString().equals("All")) {
                posisi_bagian = " AND `posisi_bagian` = '" + ComboBox_filter_posisi_bagian.getSelectedItem().toString() + "' ";
            }
            String departemen = "";
            if (ComboBox_filter_departemen.getSelectedItem() != null && !ComboBox_filter_departemen.getSelectedItem().toString().equals("All")) {
                departemen = " AND `kode_departemen` = '" + ComboBox_filter_departemen.getSelectedItem().toString() + "' ";
            }
            String divisi = "";
            if (ComboBox_filter_divisi.getSelectedItem() != null && !ComboBox_filter_divisi.getSelectedItem().toString().equals("All")) {
                divisi = " AND `divisi_bagian` = '" + ComboBox_filter_divisi.getSelectedItem().toString() + "' ";
            }
            String bagian = "";
            if (ComboBox_filter_bagian.getSelectedItem() != null && !ComboBox_filter_bagian.getSelectedItem().toString().equals("All")) {
                bagian = " AND `bagian_bagian` = '" + ComboBox_filter_bagian.getSelectedItem().toString() + "' ";
            }
            String ruang = "";
            if (ComboBox_filter_ruang.getSelectedItem() != null && !ComboBox_filter_ruang.getSelectedItem().toString().equals("All")) {
                ruang = " AND `ruang_bagian` = '" + ComboBox_filter_ruang.getSelectedItem().toString() + "' ";
            }

            String bagian2 = "AND `nama_bagian` LIKE '%" + txt_search_bagian.getText() + "%' ";
            if (txt_search_bagian.getText() == null || txt_search_bagian.getText().equals("")) {
                bagian2 = "";
            }
            String Status = "AND `status` = '" + ComboBox_status_karyawan.getSelectedItem().toString() + "' ";
            if ("All".equals(ComboBox_status_karyawan.getSelectedItem().toString())) {
                Status = "";
            }
            String kelamin = ComboBox_kelamin_karyawan.getSelectedItem().toString();
            if ("All".equals(ComboBox_kelamin_karyawan.getSelectedItem().toString())) {
                kelamin = "";
            }
            String posisi = "AND `posisi` = '" + ComboBox_posisi.getSelectedItem().toString() + "' ";
            if ("All".equals(ComboBox_posisi.getSelectedItem().toString())) {
                posisi = "";
            }

            String filter_tanggal = "";
            if (Date_Search1.getDate() != null && Date_Search2.getDate() != null) {
                switch (ComboBox_filter_tanggal.getSelectedIndex()) {
                    case 0:
                        filter_tanggal = "AND `tanggal_interview` BETWEEN '" + dateFormat.format(Date_Search1.getDate()) + "' AND '" + dateFormat.format(Date_Search2.getDate()) + "'";
                        break;
                    case 1:
                        filter_tanggal = "AND `tanggal_masuk` BETWEEN '" + dateFormat.format(Date_Search1.getDate()) + "' AND '" + dateFormat.format(Date_Search2.getDate()) + "'";
                        break;
                    case 2:
                        filter_tanggal = "AND `tanggal_keluar` BETWEEN '" + dateFormat.format(Date_Search1.getDate()) + "' AND '" + dateFormat.format(Date_Search2.getDate()) + "'";
                        break;
                    default:
                        break;
                }
            }

            sql = "SELECT `id_pegawai`, `pin_finger`, `nik_ktp`, `nama_pegawai`, `jenis_kelamin`, `tempat_lahir`, `tanggal_lahir`, `agama`, `alamat`, `desa`, `kecamatan`, `kota_kabupaten`, `provinsi`, `golongan_darah`, `no_telp`, `email`, `status_kawin`, `nama_ibu`, `nama_bagian`, `kode_departemen`, `posisi`, `pendidikan`, `tanggal_interview`, `tanggal_masuk`, `tanggal_keluar`, `kategori_keluar`, `keterangan`, `status`, `level_gaji`, `jam_kerja`, IF(`uid_card` IS NOT NULL, TRUE, FALSE) AS 'uid_card', "
                    + "`fc_ktp`, `sertifikat_vaksin1`, `sertifikat_vaksin2`, `berkas_surat_pernyataan`, `tanggal_surat`, `rek_cimb`, `jalur_jemputan`, `potongan_bpjs`, `keterangan`, DATE_ADD(`tanggal_surat`, INTERVAL 6 MONTH) AS 'tgl_surat_berakhir' "
                    + "FROM `tb_karyawan` "
                    + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`"
                    + "WHERE `nama_pegawai` LIKE '%" + txt_search_karyawan.getText() + "%' "
                    + "AND `id_pegawai` LIKE '%" + txt_search_id.getText() + "%' "
                    + bagian2
                    + posisi_bagian
                    + departemen
                    + divisi
                    + bagian
                    + ruang
                    + Status
                    + posisi
                    + "AND `jenis_kelamin` LIKE '%" + kelamin + "%' "
                    + filter_tanggal
                    + "ORDER BY `id_pegawai` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            DataKaryawan Karyawan;
            while (rs.next()) {
                Karyawan = new DataKaryawan(rs.getString("id_pegawai"), rs.getString("pin_finger"), rs.getString("nik_ktp"), rs.getString("nama_pegawai"), rs.getString("jenis_kelamin"), rs.getString("tempat_lahir"), rs.getDate("tanggal_lahir"), rs.getString("alamat"), rs.getString("desa"), rs.getString("kecamatan"), rs.getString("kota_kabupaten"), rs.getString("provinsi"), rs.getString("golongan_darah"), rs.getString("status_kawin"), rs.getString("nama_ibu"), rs.getString("no_telp"), rs.getString("email"), rs.getString("kategori_keluar"), rs.getString("keterangan"), rs.getBoolean("uid_card"),
                        rs.getString("nama_bagian"), rs.getString("posisi"), rs.getString("kode_departemen"), rs.getString("pendidikan"), rs.getDate("tanggal_interview"), rs.getDate("tanggal_masuk"), rs.getDate("tanggal_keluar"), rs.getString("status"), rs.getString("jam_kerja"), rs.getString("jalur_jemputan"), rs.getInt("potongan_bpjs"),
                        rs.getInt("fc_ktp"), rs.getInt("sertifikat_vaksin1"), rs.getInt("sertifikat_vaksin2"), rs.getInt("berkas_surat_pernyataan"), rs.getDate("tanggal_surat"), rs.getDate("tgl_surat_berakhir"));
                KaryawanList.add(Karyawan);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void show_data_ktp_karyawan() {
        int IN = 0, OUT = 0, BATAL = 0, STRIP = 0, ABSEN = 0;
        ArrayList<DataKaryawan> list = KaryawanList;
        DefaultTableModel model = (DefaultTableModel) table_data_ktp.getModel();
        for (int i = 0; i < list.size(); i++) {
            Object[] row = new Object[45];
            row[0] = list.get(i).getId_pegawai();
            row[1] = list.get(i).getPin();
            row[2] = list.get(i).getNik_ktp();
            row[3] = list.get(i).getNama_pegawai();
            row[4] = list.get(i).getStatus();
            row[5] = list.get(i).getPosisi();
            if (list.get(i).getKode_bagian() != null) {
                if (list.get(i).getKode_bagian().split("-").length > 1) {
                    row[6] = list.get(i).getKode_bagian().split("-")[0];//posisi
                    row[7] = list.get(i).getKode_bagian().split("-")[1];//dept
                    row[8] = list.get(i).getKode_bagian().split("-")[2];//divisi
                    row[9] = list.get(i).getKode_bagian().split("-")[3];//bagian
                    row[10] = list.get(i).getKode_bagian().split("-")[4];//ruang
                } else {
                    row[9] = list.get(i).getKode_bagian().split("-")[0];//bagian
                }
            }
            row[11] = list.get(i).getTanggal_interview();
            row[12] = list.get(i).getTanggal_masuk();
            row[13] = list.get(i).getTanggal_keluar();
            row[14] = list.get(i).getKategori_keluar();
            row[15] = list.get(i).getKeterangan();
            row[16] = list.get(i).getJam_kerja();
            row[17] = list.get(i).getJenis_kelamin();
            row[18] = list.get(i).getTempat_lahir();
            row[19] = list.get(i).getTanggal_lahir();
            row[20] = list.get(i).getAlamat();
            row[21] = list.get(i).getDesa();
            row[22] = list.get(i).getKecamatan();
            row[23] = list.get(i).getKota();
            row[24] = list.get(i).getProvinsi();
            row[25] = list.get(i).getGolongan_darah();
            row[26] = list.get(i).getStatus_kawin();
            row[27] = list.get(i).getNama_ibu();
            row[28] = list.get(i).getNo_telp();
            row[29] = list.get(i).getEmail();
            row[30] = list.get(i).getPendidikan();
            row[31] = list.get(i).getJalur_jemputan();
            switch (list.get(i).getPotongan_bpjs()) {
                case 0:
                    row[32] = "Belum terdaftar";
                    break;
                case 1:
                    row[32] = "Sudah terdaftar";
                    break;
                case 2:
                    row[32] = "Sudah terbayar";
                    break;
                default:
                    break;
            }
            switch (list.get(i).getStatus()) {
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
            //menghitung lama karyawan bekerja
            Date lahir = list.get(i).getTanggal_lahir();
            Date masuk = list.get(i).getTanggal_masuk();
            Date keluar = list.get(i).getTanggal_keluar();
            long lama_bekerja = 0;
            if (masuk != null && ("IN".equals(list.get(i).getStatus()) || "OUT".equals(list.get(i).getStatus()))) {
                if (keluar != null) {
                    lama_bekerja = Math.abs(keluar.getTime() - masuk.getTime());
                } else {
                    lama_bekerja = Math.abs(today.getTime() - masuk.getTime());
                }
            }

            row[33] = TimeUnit.MILLISECONDS.toDays(lama_bekerja) / 365;
            row[34] = (TimeUnit.MILLISECONDS.toDays(lama_bekerja) % 365) / 30;
            row[35] = (TimeUnit.MILLISECONDS.toDays(lama_bekerja) % 365) % 30;

            long umur_now = 0;
            if (lahir != null) {
                umur_now = Math.abs(today.getTime() - lahir.getTime());
            }
            row[36] = TimeUnit.MILLISECONDS.toDays(umur_now) / 365;
            row[37] = list.get(i).isUid_card();

            model.addRow(row);
        }
        int rowData = table_data_ktp.getRowCount();
        label_total_data.setText(Integer.toString(rowData));
        label_total_in.setText(Integer.toString(IN));
        label_total_out.setText(Integer.toString(OUT));
        label_total_batal.setText(Integer.toString(BATAL));
        label_total_strip.setText(Integer.toString(STRIP));
    }

    public void show_data_berkas_karyawan() {
        DefaultTableModel model = (DefaultTableModel) table_data_berkas.getModel();
        model.setRowCount(0);
        ArrayList<DataKaryawan> list = KaryawanList;
        Object[][] data = new Object[list.size()][11];
        Object[] row = new Object[15];
        for (int i = 0; i < list.size(); i++) {
//            data[i][0] = list.get(i).getId_pegawai();
//            data[i][1] = list.get(i).getNik_ktp();
//            data[i][2] = list.get(i).getNama_pegawai();
//            data[i][3] = list.get(i).getFc_ktp() != 0;
//            data[i][4] = list.get(i).getSertifikat_vaksin1() != 0;
//            data[i][5] = list.get(i).getSertifikat_vaksin2() != 0;
//            data[i][6] = list.get(i).getBerkas_surat_pernyataan() != 0;
//            data[i][7] = list.get(i).getTanggal_surat();
//            data[i][8] = list.get(i).getTanggal_berakhir();
            row[0] = list.get(i).getId_pegawai();
            row[1] = list.get(i).getNik_ktp();
            row[2] = list.get(i).getNama_pegawai();
            row[3] = list.get(i).getFc_ktp() != 0;
            row[4] = list.get(i).getSertifikat_vaksin1() != 0;
            row[5] = list.get(i).getSertifikat_vaksin2() != 0;
            row[6] = list.get(i).getBerkas_surat_pernyataan() != 0;
            row[7] = list.get(i).getTanggal_surat();
            row[8] = list.get(i).getTanggal_berakhir();
            File KTP = new File("\\\\192.168.10.2\\Shared Folder\\DOKUMEN SISTEM\\FC_KTP\\" + list.get(i).getId_pegawai() + ".JPG");
            row[9] = KTP.exists();
            File FOTO = new File("\\\\192.168.10.2\\Shared Folder\\DOKUMEN SISTEM\\FC_KTP\\" + list.get(i).getId_pegawai() + ".JPG");
            row[10] = FOTO.exists();
            model.addRow(row);
        }
        
        ColumnsAutoSizer.sizeColumnsToFit(table_data_berkas);
        int rowData = table_data_berkas.getRowCount();
        label_total_data.setText(Integer.toString(rowData));

//        table_data_berkas.setModel(new javax.swing.table.DefaultTableModel(
//                data,
//                new String[]{
//                    "ID Pegawai", "NIK", "Nama Pegawai", "FC KTP", "Sertifikat Vaksin 1", "Sertifikat Vaksin 2", "Surat Pernyataan", "Tanggal Surat", "Berlaku Sampai"
//                }) {
//            Class[] types = new Class[]{
//                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class
//            };
//            boolean[] canEdit = new boolean[]{
//                false, false, false, false, false, false, false, false, false
//            };
//
//            @Override
//            public Class getColumnClass(int columnIndex) {
//                return types[columnIndex];
//            }
//
//            @Override
//            public boolean isCellEditable(int rowIndex, int columnIndex) {
//                return canEdit[columnIndex];
//            }
//        });
//        table_data_berkas.repaint();
//        jScrollPane3.setViewportView(table_data_berkas);
    }

    public void refresh_data_cimb() {
        DefaultTableModel model = (DefaultTableModel) table_data_cimb.getModel();
        model.setRowCount(0);
        ArrayList<DataKaryawan> list = KaryawanList;
        Object[] row = new Object[4];
        for (int i = 0; i < list.size(); i++) {
            row[0] = list.get(i).getNik_ktp();
            row[1] = list.get(i).getNama_pegawai();
            row[2] = list.get(i).getNama_ibu();
            row[3] = list.get(i).getNo_telp();
            model.addRow(row);
        }
        ColumnsAutoSizer.sizeColumnsToFit(table_data_cimb);
        int rowData = table_data_cimb.getRowCount();
        label_total_data.setText(Integer.toString(rowData));
    }

    public void refreshTable() {
        KaryawanList();
        DefaultTableModel model_table_ktp = (DefaultTableModel) table_data_ktp.getModel();
        model_table_ktp.setRowCount(0);
        show_data_ktp_karyawan();
        ColumnsAutoSizer.sizeColumnsToFit(table_data_ktp);

        refresh_data_cimb();
    }
    
    public void refreshTable_data_berkas_karyawan() {
        DefaultTableModel model_table_berkas = (DefaultTableModel) table_data_berkas.getModel();
        model_table_berkas.setRowCount(0);
        show_data_berkas_karyawan();
        ColumnsAutoSizer.sizeColumnsToFit(table_data_berkas);
    }

    public void executeQuery(String query, String message) {
        Utility.db.getConnection();
        try {
            Utility.db.getConnection().createStatement();
            if ((Utility.db.getStatement().executeUpdate(query)) == 1) {
                refreshTable();
                JOptionPane.showMessageDialog(this, "data " + message + " Successfully");
            } else {
                JOptionPane.showMessageDialog(this, "data not " + message);
            }
        } catch (SQLException | HeadlessException e) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, e);
            JOptionPane.showMessageDialog(this, e);
        }
    }

    public String generate_ID() {
        String new_ID = "";
        try {
            int last_ID = 0;
            String query = "SELECT MAX(SUBSTRING(`id_pegawai`, 8, 5)) AS 'last_id' FROM `tb_karyawan` WHERE 1";
            ResultSet result = Utility.db.getStatement().executeQuery(query);
            if (result.next()) {
                last_ID = result.getInt("last_id") + 1;
            }
            new_ID = new SimpleDateFormat("yyyyMM").format(new Date()) + "0" + last_ID;
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_Data_Karyawan.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new_ID;
    }

    public String generate_Nama(String nama, String nik, String Desa) {
        String nama_baru = "";
        boolean nik_belum_ada = true;
        try {
            String query = "SELECT `nama_pegawai` FROM `tb_karyawan` "
                    + "WHERE `status` NOT LIKE '%SUB' AND `nik_ktp` = " + nik + "'";
            ResultSet result = Utility.db.getStatement().executeQuery(query);
            if (result.next()) {
                nik_belum_ada = false;
                nama_baru = result.getString("nama_pegawai");
            }

            if (nik_belum_ada) {
                query = "SELECT `nama_pegawai`, MAX(SUBSTRING(`nama_pegawai`, " + (nama.length() + 2) + ", 2)+0) AS 'nomor' FROM `tb_karyawan`  "
                        + "WHERE `status` NOT LIKE '%SUB' AND `nama_pegawai` LIKE '" + nama + "%'";
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
            Logger.getLogger(JPanel_Data_Karyawan.class.getName()).log(Level.SEVERE, null, ex);
        }
        return nama_baru;
    }

    public void ImportFromCSV() throws ParseException {
        try {
            int n = 0;
            chooser.setDialogTitle("Select CSV file to import!");
            int result = chooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                Utility.db.getConnection();
                File file = chooser.getSelectedFile();
                String filename1 = file.getAbsolutePath();
                try (BufferedReader br = new BufferedReader(new FileReader(filename1))) {
                    String line;
                    String Query = null;
                    try {
                        while ((line = br.readLine()) != null) {
                            String[] value = line.split(";");
                            String ID = generate_ID();
                            String PIN = ID.substring(7);
                            String NAMA = generate_Nama(value[1], value[0], value[7]);
                            if ("".equals(value[9]) || value[9] == null) {
                                value[9] = "'";
                            }
//                            System.out.println(ID);
                            Query = "INSERT INTO `tb_karyawan`("
                                    + "`id_pegawai`, "
                                    + "`pin_finger`, "
                                    + "`nik_ktp`, "
                                    + "`nama_pegawai`, "
                                    + "`tanggal_lahir`, "
                                    + "`tempat_lahir`, "
                                    + "`jenis_kelamin`, "
                                    + "`nama_ibu`, "
                                    + "`alamat`, "
                                    + "`desa`, "
                                    + "`kecamatan`, "
                                    + "`no_telp`, "
                                    + "`status_kawin`, "
                                    + "`agama`, "
                                    + "`pendidikan`, "
                                    + "`tanggal_interview`) "
                                    + "VALUES ('" + ID + "','" + PIN + "'," + value[0] + "','" + NAMA + "','" + dateFormat.format(new SimpleDateFormat("dd/MM/yyyy").parse(value[2])) + "','" + value[3] + "','" + value[4].toUpperCase() + "','" + value[5] + "','" + value[6] + "','" + value[7] + "','" + value[8] + "'," + value[9] + "','" + value[10] + "','" + value[11] + "','" + value[12] + "','" + dateFormat.format(new SimpleDateFormat("dd/MM/yyyy").parse(value[13])) + "')";
                            System.out.println(Query);
                            boolean above18th = true;
                            try {
                                String qry = "SELECT (DATEDIFF(CURRENT_DATE(), '" + dateFormat.format(new SimpleDateFormat("dd/MM/yyyy").parse(value[2])) + "')/365) AS 'umur'";
                                rs = Utility.db.getStatement().executeQuery(qry);
                                if (rs.next()) {
                                    if (rs.getFloat("umur") < 18.f) {
                                        JOptionPane.showMessageDialog(this, "Maaf anak di bawah 18 tahun tidak boleh masuk !");
                                        above18th = false;
                                    }
                                }
                            } catch (SQLException e) {
                                Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, e);
                            }

                            if (above18th) {
                                Utility.db.getConnection().prepareStatement(Query);
                                if ((Utility.db.getStatement().executeUpdate(Query)) == 1) {
                                    n++;
                                } else {
                                    JOptionPane.showMessageDialog(this, "Failed insert " + NAMA);
                                }
                            }
                        }
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(this, ex);
                        JOptionPane.showMessageDialog(this, "Data Berhasil Masuk : " + n);
                        Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {
                        JOptionPane.showMessageDialog(this, "Data Berhasil Masuk : " + n);
                        refreshTable();
                    }
                }
            }
        } catch (HeadlessException | IOException ex) {
            JOptionPane.showMessageDialog(this, ex);
        }
    }

    public void ImportDataEdit() throws ParseException {
        try {
            int n = 0;
            chooser.setDialogTitle("Select CSV file to import!");
            int result = chooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                Utility.db.getConnection();
                File file = chooser.getSelectedFile();
                String filename1 = file.getAbsolutePath();
                try (BufferedReader br = new BufferedReader(new FileReader(filename1))) {
                    String line;
                    String Query = null;
                    try {
                        while ((line = br.readLine()) != null) {
                            String[] value = line.split(";");
//                            System.out.println(ID);
                            Query = "UPDATE `tb_karyawan` SET "
                                    + "`tanggal_lahir`='" + dateFormat.format(new SimpleDateFormat("dd/MM/yyyy").parse(value[1])) + "',"
                                    + "`tempat_lahir`='" + value[2] + "',"
                                    + "`jenis_kelamin`='" + value[3] + "',"
                                    + "`nama_ibu`='" + value[4] + "',"
                                    + "`no_telp`=" + value[5] + "',"
                                    + "`status_kawin`='" + value[6] + "',"
                                    + "`agama`='" + value[7] + "',"
                                    + "`pendidikan`='" + value[8] + "'"
                                    + "WHERE `nik_ktp`=" + value[0] + "'";
                            Utility.db.getConnection().prepareStatement(Query);
                            if ((Utility.db.getStatement().executeUpdate(Query)) > 0) {
                                System.out.println(value[0]);
                                n++;
                                System.out.println(n);
                            } else {
                                System.out.println("gagal");
//                                System.out.println(Query);
//                                JOptionPane.showMessageDialog(this, "Failed insert : " + value[0]);
                            }
                        }
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(this, ex);
                        JOptionPane.showMessageDialog(this, "Data Berhasil Masuk : " + n);
                        Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {
                        JOptionPane.showMessageDialog(this, "Data Berhasil Masuk : " + n);
                        refreshTable();
                    }
                }
            }
        } catch (HeadlessException | IOException ex) {
            JOptionPane.showMessageDialog(this, ex);
        }
    }

    public void delete() {
        try {
            String nik, id = null;
            int GET_ROW_KTP = table_data_ktp.getSelectedRow();
            int GET_ROW_BERKAS = table_data_berkas.getSelectedRow();
            if (GET_ROW_BERKAS == -1 || GET_ROW_KTP == -1) {
                id = "";
            }
            if (GET_ROW_BERKAS > -1) {
                id = (String) table_data_berkas.getValueAt(GET_ROW_BERKAS, 0);
            } else if (GET_ROW_KTP > -1) {
                id = (String) table_data_ktp.getValueAt(GET_ROW_KTP, 0);
            }

            if (GET_ROW_KTP == -1 && GET_ROW_BERKAS == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih data yang ingin di hapus !");
            } else {
                int dialogResult = JOptionPane.showConfirmDialog(this, "Yakin hapus data ini?", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    // delete code here
                    String Query = "DELETE FROM `tb_karyawan` WHERE `id_pegawai` = '" + id + "'";
                    executeQuery(Query, "deleted !");
                }
            }
        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void hasilFinger(boolean sukses) {
        JOptionPane.showMessageDialog(JPanel_Data_Karyawan.this, sukses);
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
        jLabel1 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        ComboBox_filter_departemen = new javax.swing.JComboBox<>();
        ComboBox_filter_bagian = new javax.swing.JComboBox<>();
        Date_Search1 = new com.toedter.calendar.JDateChooser();
        Date_Search2 = new com.toedter.calendar.JDateChooser();
        jLabel7 = new javax.swing.JLabel();
        ComboBox_kelamin_karyawan = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        txt_search_id = new javax.swing.JTextField();
        ComboBox_filter_tanggal = new javax.swing.JComboBox<>();
        ComboBox_status_karyawan = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        ComboBox_posisi = new javax.swing.JComboBox<>();
        jLabel14 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        txt_search_bagian = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        ComboBox_filter_posisi_bagian = new javax.swing.JComboBox<>();
        jLabel19 = new javax.swing.JLabel();
        ComboBox_filter_divisi = new javax.swing.JComboBox<>();
        jLabel20 = new javax.swing.JLabel();
        ComboBox_filter_ruang = new javax.swing.JComboBox<>();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel_Data_KTP_Karyawan = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table_data_ktp = new javax.swing.JTable();
        button_view_ktp = new javax.swing.JButton();
        button_export_data_karyawan_ktp = new javax.swing.JButton();
        button_import = new javax.swing.JButton();
        button_input_foto = new javax.swing.JButton();
        button_import_edit = new javax.swing.JButton();
        button_PrintIDCARD = new javax.swing.JButton();
        button_PrintIDCARD_Semua = new javax.swing.JButton();
        button_set_JamKerja = new javax.swing.JButton();
        button_finger2 = new javax.swing.JButton();
        button_finger1 = new javax.swing.JButton();
        button_tes_finger1 = new javax.swing.JButton();
        button_tes_finger2 = new javax.swing.JButton();
        button_set_jemputan = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        txt_nama_pelatihan = new javax.swing.JTextField();
        button_print_daftar_hadir_pelatihan = new javax.swing.JButton();
        Date_Pelatihan = new com.toedter.calendar.JDateChooser();
        jLabel5 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        Date_evaluasi_Pelatihan = new com.toedter.calendar.JDateChooser();
        button_print_evaluasi_pelatihan = new javax.swing.JButton();
        button_PrintIDCARD1 = new javax.swing.JButton();
        button_PrintIDCARD2 = new javax.swing.JButton();
        button_unduh_ktp = new javax.swing.JButton();
        jPanel_Data_Berkas_Karyawan = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        table_data_berkas = new javax.swing.JTable();
        button_export_data_karyawan_berkas = new javax.swing.JButton();
        button_open_sertifikat_vaksin1 = new javax.swing.JButton();
        button_open_fc_ktp = new javax.swing.JButton();
        button_open_sertifikat_vaksin2 = new javax.swing.JButton();
        button_data_berkas_karyawan = new javax.swing.JButton();
        jPanel_Data_CIMB_Karyawan = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        table_data_cimb = new javax.swing.JTable();
        button_export_data_cimb = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        button_set_JamKerja1 = new javax.swing.JButton();
        button_set_JamKerja2 = new javax.swing.JButton();
        button_insert_karyawan = new javax.swing.JButton();
        button_status_OUT = new javax.swing.JButton();
        button_status_IN = new javax.swing.JButton();
        button_status_batal = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        label_total_in = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        label_total_out = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        label_total_data = new javax.swing.JLabel();
        button_masuk_kembali = new javax.swing.JButton();
        button_edit_data_karyawan = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        label_total_batal = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        label_total_strip = new javax.swing.JLabel();
        button_status_absen = new javax.swing.JButton();
        jLabel17 = new javax.swing.JLabel();
        label_total_absen = new javax.swing.JLabel();

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

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("Departemen :");

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel6.setText("Bagian :");

        jLabel34.setBackground(new java.awt.Color(255, 255, 255));
        jLabel34.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        jLabel34.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel34.setText("-");

        jLabel35.setBackground(new java.awt.Color(255, 255, 255));
        jLabel35.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        jLabel35.setText("Date Filter by ");

        ComboBox_filter_departemen.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_filter_departemen.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));
        ComboBox_filter_departemen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ComboBox_filter_departemenActionPerformed(evt);
            }
        });

        ComboBox_filter_bagian.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_filter_bagian.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        Date_Search1.setBackground(new java.awt.Color(255, 255, 255));
        Date_Search1.setToolTipText("");
        Date_Search1.setDateFormatString("dd MMM yyyy");
        Date_Search1.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N

        Date_Search2.setBackground(new java.awt.Color(255, 255, 255));
        Date_Search2.setDateFormatString("dd MMM yyyy");
        Date_Search2.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        Date_Search2.setMinSelectableDate(new java.util.Date(-62135791118000L));

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel7.setText("Jenis Kelamin  :");

        ComboBox_kelamin_karyawan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_kelamin_karyawan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "LAKI-LAKI", "PEREMPUAN" }));

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
        ComboBox_filter_tanggal.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tanggal Interview", "Tanggal Masuk", "Tanggal Keluar" }));

        ComboBox_status_karyawan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_status_karyawan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "IN", "OUT", "OUT-SUB", "ABSEN", "BATAL", "-" }));

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

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel18.setText("Posisi :");

        ComboBox_filter_posisi_bagian.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_filter_posisi_bagian.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        jLabel19.setBackground(new java.awt.Color(255, 255, 255));
        jLabel19.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel19.setText("Divisi :");

        ComboBox_filter_divisi.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_filter_divisi.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));
        ComboBox_filter_divisi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ComboBox_filter_divisiActionPerformed(evt);
            }
        });

        jLabel20.setBackground(new java.awt.Color(255, 255, 255));
        jLabel20.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel20.setText("Ruang :");

        ComboBox_filter_ruang.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_filter_ruang.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        javax.swing.GroupLayout jPanel_search_karyawanLayout = new javax.swing.GroupLayout(jPanel_search_karyawan);
        jPanel_search_karyawan.setLayout(jPanel_search_karyawanLayout);
        jPanel_search_karyawanLayout.setHorizontalGroup(
            jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_search_karyawanLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_search_karyawanLayout.createSequentialGroup()
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_bagian, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_filter_posisi_bagian, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_filter_departemen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_filter_divisi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_filter_bagian, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_filter_ruang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_search_karyawan))
                    .addGroup(jPanel_search_karyawanLayout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_id, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_kelamin_karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_status_karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_posisi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                        .addComponent(Date_Search2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel_search_karyawanLayout.setVerticalGroup(
            jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_search_karyawanLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel_search_karyawanLayout.createSequentialGroup()
                        .addGroup(jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_search_id, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_search_karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ComboBox_kelamin_karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ComboBox_status_karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ComboBox_posisi, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ComboBox_filter_ruang, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel20, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ComboBox_filter_divisi, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel19, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ComboBox_filter_posisi_bagian, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel18, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ComboBox_filter_departemen, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ComboBox_filter_bagian, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_search_bagian, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_search_karyawan, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(Date_Search1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel_search_karyawanLayout.createSequentialGroup()
                            .addGroup(jPanel_search_karyawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(ComboBox_filter_tanggal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(Date_Search2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        jTabbedPane1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPane1StateChanged(evt);
            }
        });

        jPanel_Data_KTP_Karyawan.setBackground(new java.awt.Color(255, 255, 255));

        table_data_ktp.setAutoCreateRowSorter(true);
        table_data_ktp.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        table_data_ktp.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "PIN", "NIK", "Nama", "Status", "Posisi", "Posisi", "Dept", "Divisi", "Bagian", "Ruang", "Tgl Interview", "Tgl Masuk", "Tgl Keluar", "Kategori Keluar", "Keterangan Keluar", "Jam Kerja", "Jenis Kelamin", "Tmpt Lahir", "Tgl Lahir", "Alamat", "Desa", "Kecamatan", "Kab./Kota", "Provinsi", "Gol. Darah", "Status Kawin", "Ibu Kandung", "No Telp", "E-mail", "Pendidikan", "Jemputan", "BPJS", "Th", "Bln", "Hr", "Umur", "ID card"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
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
            table_data_ktp.getColumnModel().getColumn(3).setMaxWidth(200);
            table_data_ktp.getColumnModel().getColumn(20).setMaxWidth(300);
            table_data_ktp.getColumnModel().getColumn(27).setMaxWidth(150);
        }

        button_view_ktp.setBackground(new java.awt.Color(255, 255, 255));
        button_view_ktp.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        button_view_ktp.setText("View KTP");
        button_view_ktp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_view_ktpActionPerformed(evt);
            }
        });

        button_export_data_karyawan_ktp.setBackground(new java.awt.Color(255, 255, 255));
        button_export_data_karyawan_ktp.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        button_export_data_karyawan_ktp.setText("Export to Excel");
        button_export_data_karyawan_ktp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_data_karyawan_ktpActionPerformed(evt);
            }
        });

        button_import.setBackground(new java.awt.Color(255, 255, 255));
        button_import.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        button_import.setText("Import From CSV");
        button_import.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_importActionPerformed(evt);
            }
        });

        button_input_foto.setBackground(new java.awt.Color(255, 255, 255));
        button_input_foto.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        button_input_foto.setText("Input Foto");
        button_input_foto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_input_fotoActionPerformed(evt);
            }
        });

        button_import_edit.setBackground(new java.awt.Color(255, 255, 255));
        button_import_edit.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        button_import_edit.setText("Edit data From CSV");
        button_import_edit.setEnabled(false);
        button_import_edit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_import_editActionPerformed(evt);
            }
        });

        button_PrintIDCARD.setBackground(new java.awt.Color(255, 255, 255));
        button_PrintIDCARD.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_PrintIDCARD.setText("Print ID CARD");
        button_PrintIDCARD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_PrintIDCARDActionPerformed(evt);
            }
        });

        button_PrintIDCARD_Semua.setBackground(new java.awt.Color(255, 255, 255));
        button_PrintIDCARD_Semua.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_PrintIDCARD_Semua.setText("Print semua ID CARD");
        button_PrintIDCARD_Semua.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_PrintIDCARD_SemuaActionPerformed(evt);
            }
        });

        button_set_JamKerja.setBackground(new java.awt.Color(255, 255, 255));
        button_set_JamKerja.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_set_JamKerja.setText("Set Jam Kerja");
        button_set_JamKerja.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_set_JamKerjaActionPerformed(evt);
            }
        });

        button_finger2.setBackground(new java.awt.Color(255, 255, 255));
        button_finger2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_finger2.setText("Finger 2");
        button_finger2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_finger2ActionPerformed(evt);
            }
        });

        button_finger1.setBackground(new java.awt.Color(255, 255, 255));
        button_finger1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_finger1.setText("Finger 1");
        button_finger1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_finger1ActionPerformed(evt);
            }
        });

        button_tes_finger1.setBackground(new java.awt.Color(255, 255, 255));
        button_tes_finger1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_tes_finger1.setText("Tes Finger 1");
        button_tes_finger1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_tes_finger1ActionPerformed(evt);
            }
        });

        button_tes_finger2.setBackground(new java.awt.Color(255, 255, 255));
        button_tes_finger2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_tes_finger2.setText("Tes Finger 2");
        button_tes_finger2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_tes_finger2ActionPerformed(evt);
            }
        });

        button_set_jemputan.setBackground(new java.awt.Color(255, 255, 255));
        button_set_jemputan.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        button_set_jemputan.setText("Set Jalur Jemputan");
        button_set_jemputan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_set_jemputanActionPerformed(evt);
            }
        });

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel4.setText("Nama Pelatihan :");

        txt_nama_pelatihan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_nama_pelatihan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_nama_pelatihanKeyPressed(evt);
            }
        });

        button_print_daftar_hadir_pelatihan.setBackground(new java.awt.Color(255, 255, 255));
        button_print_daftar_hadir_pelatihan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_print_daftar_hadir_pelatihan.setText("Print daftar pelatihan");
        button_print_daftar_hadir_pelatihan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_print_daftar_hadir_pelatihanActionPerformed(evt);
            }
        });

        Date_Pelatihan.setBackground(new java.awt.Color(255, 255, 255));
        Date_Pelatihan.setDateFormatString("dd MMM yyyy");
        Date_Pelatihan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel5.setText("Tgl Pelatihan :");

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel15.setText("Tgl Evaluasi :");

        Date_evaluasi_Pelatihan.setBackground(new java.awt.Color(255, 255, 255));
        Date_evaluasi_Pelatihan.setDateFormatString("dd MMM yyyy");
        Date_evaluasi_Pelatihan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        button_print_evaluasi_pelatihan.setBackground(new java.awt.Color(255, 255, 255));
        button_print_evaluasi_pelatihan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_print_evaluasi_pelatihan.setText("Print Evaluasi");
        button_print_evaluasi_pelatihan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_print_evaluasi_pelatihanActionPerformed(evt);
            }
        });

        button_PrintIDCARD1.setBackground(new java.awt.Color(255, 255, 255));
        button_PrintIDCARD1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_PrintIDCARD1.setText("Print NEW ID CARD");
        button_PrintIDCARD1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_PrintIDCARD1ActionPerformed(evt);
            }
        });

        button_PrintIDCARD2.setBackground(new java.awt.Color(255, 255, 255));
        button_PrintIDCARD2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_PrintIDCARD2.setText("Print QR ID CARD");
        button_PrintIDCARD2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_PrintIDCARD2ActionPerformed(evt);
            }
        });

        button_unduh_ktp.setBackground(new java.awt.Color(255, 255, 255));
        button_unduh_ktp.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        button_unduh_ktp.setText("Unduh KTP");
        button_unduh_ktp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_unduh_ktpActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_Data_KTP_KaryawanLayout = new javax.swing.GroupLayout(jPanel_Data_KTP_Karyawan);
        jPanel_Data_KTP_Karyawan.setLayout(jPanel_Data_KTP_KaryawanLayout);
        jPanel_Data_KTP_KaryawanLayout.setHorizontalGroup(
            jPanel_Data_KTP_KaryawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Data_KTP_KaryawanLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_Data_KTP_KaryawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_Data_KTP_KaryawanLayout.createSequentialGroup()
                        .addGap(0, 79, Short.MAX_VALUE)
                        .addComponent(button_tes_finger1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_tes_finger2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_finger1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_finger2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_set_JamKerja)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_PrintIDCARD_Semua)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_PrintIDCARD)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_import_edit)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_import)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_export_data_karyawan_ktp)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_view_ktp)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_input_foto))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_Data_KTP_KaryawanLayout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_nama_pelatihan, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_Pelatihan, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_evaluasi_Pelatihan, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_print_daftar_hadir_pelatihan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_print_evaluasi_pelatihan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_PrintIDCARD1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_PrintIDCARD2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_unduh_ktp)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_set_jemputan)))
                .addContainerGap())
        );
        jPanel_Data_KTP_KaryawanLayout.setVerticalGroup(
            jPanel_Data_KTP_KaryawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_Data_KTP_KaryawanLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_Data_KTP_KaryawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(button_PrintIDCARD, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_import_edit, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_input_foto, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_import, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export_data_karyawan_ktp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_view_ktp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_tes_finger2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_tes_finger1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_finger1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_finger2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_set_JamKerja, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_PrintIDCARD_Semua, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_Data_KTP_KaryawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(Date_evaluasi_Pelatihan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_Pelatihan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_print_daftar_hadir_pelatihan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_nama_pelatihan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_set_jemputan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_PrintIDCARD1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_print_evaluasi_pelatihan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_PrintIDCARD2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_unduh_ktp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 452, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Data Karyawan", jPanel_Data_KTP_Karyawan);

        jPanel_Data_Berkas_Karyawan.setBackground(new java.awt.Color(255, 255, 255));

        table_data_berkas.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        table_data_berkas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Pegawai", "NIK", "Nama Pegawai", "FC KTP", "Sertifikat Vaksin 1", "Sertifikat Vaksin 2", "Surat Pernyataan", "Tanggal Surat", "Berlaku Sampai", "KTP", "FOTO"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(table_data_berkas);

        button_export_data_karyawan_berkas.setBackground(new java.awt.Color(255, 255, 255));
        button_export_data_karyawan_berkas.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        button_export_data_karyawan_berkas.setText("Export to Excel");
        button_export_data_karyawan_berkas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_data_karyawan_berkasActionPerformed(evt);
            }
        });

        button_open_sertifikat_vaksin1.setBackground(new java.awt.Color(255, 255, 255));
        button_open_sertifikat_vaksin1.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        button_open_sertifikat_vaksin1.setText("Open Sertifikat Vaksin 1");
        button_open_sertifikat_vaksin1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_open_sertifikat_vaksin1ActionPerformed(evt);
            }
        });

        button_open_fc_ktp.setBackground(new java.awt.Color(255, 255, 255));
        button_open_fc_ktp.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        button_open_fc_ktp.setText("Open FC KTP");
        button_open_fc_ktp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_open_fc_ktpActionPerformed(evt);
            }
        });

        button_open_sertifikat_vaksin2.setBackground(new java.awt.Color(255, 255, 255));
        button_open_sertifikat_vaksin2.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        button_open_sertifikat_vaksin2.setText("Open Sertifikat Vaksin 2");
        button_open_sertifikat_vaksin2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_open_sertifikat_vaksin2ActionPerformed(evt);
            }
        });

        button_data_berkas_karyawan.setBackground(new java.awt.Color(255, 255, 255));
        button_data_berkas_karyawan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_data_berkas_karyawan.setText("Refresh");
        button_data_berkas_karyawan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_data_berkas_karyawanActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_Data_Berkas_KaryawanLayout = new javax.swing.GroupLayout(jPanel_Data_Berkas_Karyawan);
        jPanel_Data_Berkas_Karyawan.setLayout(jPanel_Data_Berkas_KaryawanLayout);
        jPanel_Data_Berkas_KaryawanLayout.setHorizontalGroup(
            jPanel_Data_Berkas_KaryawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Data_Berkas_KaryawanLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_Data_Berkas_KaryawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 1311, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_Data_Berkas_KaryawanLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(button_data_berkas_karyawan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_open_fc_ktp)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_open_sertifikat_vaksin1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_open_sertifikat_vaksin2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_export_data_karyawan_berkas)))
                .addContainerGap())
        );
        jPanel_Data_Berkas_KaryawanLayout.setVerticalGroup(
            jPanel_Data_Berkas_KaryawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Data_Berkas_KaryawanLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_Data_Berkas_KaryawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(button_open_sertifikat_vaksin2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_open_fc_ktp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_open_sertifikat_vaksin1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_data_berkas_karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export_data_karyawan_berkas, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 478, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Data Berkas Karyawan", jPanel_Data_Berkas_Karyawan);

        jPanel_Data_CIMB_Karyawan.setBackground(new java.awt.Color(255, 255, 255));

        table_data_cimb.setAutoCreateRowSorter(true);
        table_data_cimb.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        table_data_cimb.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "NIK", "Nama", "Nama Ibu Kandung", "No Telpon"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table_data_cimb.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(table_data_cimb);

        button_export_data_cimb.setBackground(new java.awt.Color(255, 255, 255));
        button_export_data_cimb.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        button_export_data_cimb.setText("Export to Excel");
        button_export_data_cimb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_data_cimbActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_Data_CIMB_KaryawanLayout = new javax.swing.GroupLayout(jPanel_Data_CIMB_Karyawan);
        jPanel_Data_CIMB_Karyawan.setLayout(jPanel_Data_CIMB_KaryawanLayout);
        jPanel_Data_CIMB_KaryawanLayout.setHorizontalGroup(
            jPanel_Data_CIMB_KaryawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Data_CIMB_KaryawanLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_Data_CIMB_KaryawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1311, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_Data_CIMB_KaryawanLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(button_export_data_cimb)))
                .addContainerGap())
        );
        jPanel_Data_CIMB_KaryawanLayout.setVerticalGroup(
            jPanel_Data_CIMB_KaryawanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Data_CIMB_KaryawanLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(button_export_data_cimb, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 478, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Data Untuk CIMB", jPanel_Data_CIMB_Karyawan);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane4.setViewportView(jTable1);

        button_set_JamKerja1.setBackground(new java.awt.Color(255, 255, 255));
        button_set_JamKerja1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_set_JamKerja1.setText("Finger 2");
        button_set_JamKerja1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_set_JamKerja1ActionPerformed(evt);
            }
        });

        button_set_JamKerja2.setBackground(new java.awt.Color(255, 255, 255));
        button_set_JamKerja2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_set_JamKerja2.setText("Finger 1");
        button_set_JamKerja2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_set_JamKerja2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 1331, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(button_set_JamKerja2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_set_JamKerja1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(button_set_JamKerja2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_set_JamKerja1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 484, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Data Fingerprint", jPanel1);

        button_insert_karyawan.setBackground(new java.awt.Color(255, 255, 255));
        button_insert_karyawan.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        button_insert_karyawan.setText("Input Karyawan Baru");
        button_insert_karyawan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_insert_karyawanActionPerformed(evt);
            }
        });

        button_status_OUT.setBackground(new java.awt.Color(255, 255, 255));
        button_status_OUT.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        button_status_OUT.setText("OUT");
        button_status_OUT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_status_OUTActionPerformed(evt);
            }
        });

        button_status_IN.setBackground(new java.awt.Color(255, 255, 255));
        button_status_IN.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        button_status_IN.setText("Status IN");
        button_status_IN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_status_INActionPerformed(evt);
            }
        });

        button_status_batal.setBackground(new java.awt.Color(255, 255, 255));
        button_status_batal.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        button_status_batal.setText("Status BATAL");
        button_status_batal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_status_batalActionPerformed(evt);
            }
        });

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Total IN :");

        label_total_in.setBackground(new java.awt.Color(255, 255, 255));
        label_total_in.setText("0");

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Total OUT :");

        label_total_out.setBackground(new java.awt.Color(255, 255, 255));
        label_total_out.setText("0");

        jLabel3.setText("Total Data :");

        label_total_data.setText("0");

        button_masuk_kembali.setBackground(new java.awt.Color(255, 255, 255));
        button_masuk_kembali.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_masuk_kembali.setText("Input Karyawan Lama");
        button_masuk_kembali.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_masuk_kembaliActionPerformed(evt);
            }
        });

        button_edit_data_karyawan.setBackground(new java.awt.Color(255, 255, 255));
        button_edit_data_karyawan.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        button_edit_data_karyawan.setText("Edit");
        button_edit_data_karyawan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_edit_data_karyawanActionPerformed(evt);
            }
        });

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("Total BATAL :");

        label_total_batal.setBackground(new java.awt.Color(255, 255, 255));
        label_total_batal.setText("0");

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("Total - :");

        label_total_strip.setBackground(new java.awt.Color(255, 255, 255));
        label_total_strip.setText("0");

        button_status_absen.setBackground(new java.awt.Color(255, 255, 255));
        button_status_absen.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        button_status_absen.setText("Status ABSEN");
        button_status_absen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_status_absenActionPerformed(evt);
            }
        });

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setText("Total ABSEN :");

        label_total_absen.setBackground(new java.awt.Color(255, 255, 255));
        label_total_absen.setText("0");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel_search_karyawan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTabbedPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(button_insert_karyawan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_masuk_kembali)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_edit_data_karyawan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_status_IN)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_status_OUT)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_status_batal)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_status_absen)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                        .addComponent(label_total_absen)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel_search_karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(button_insert_karyawan)
                        .addComponent(button_masuk_kembali)
                        .addComponent(button_status_IN)
                        .addComponent(button_edit_data_karyawan)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(button_status_batal)
                            .addComponent(button_status_absen))
                        .addComponent(button_status_OUT))
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
                        .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_total_strip, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_total_batal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
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

    private void button_insert_karyawanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_insert_karyawanActionPerformed
        // TODO add your handling code here:
//        button_edit_data_karyawan.setEnabled(false);
        Insert_DataKaryawan insert_Dialog = new Insert_DataKaryawan(new javax.swing.JFrame(), true, generate_ID(), "new");
        insert_Dialog.label_title_op_karyawan.setText("Tambah Data Karyawan");
        insert_Dialog.pack();
        insert_Dialog.setLocationRelativeTo(this);
        insert_Dialog.setVisible(true);
        insert_Dialog.setEnabled(true);
        refreshTable();
    }//GEN-LAST:event_button_insert_karyawanActionPerformed

    private void button_export_data_karyawan_ktpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_data_karyawan_ktpActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model_table_ktp = (DefaultTableModel) table_data_ktp.getModel();
        ExportToExcel.writeToExcel(model_table_ktp, jPanel_Data_KTP_Karyawan);
    }//GEN-LAST:event_button_export_data_karyawan_ktpActionPerformed

    private void button_export_data_karyawan_berkasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_data_karyawan_berkasActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model_table_berkas = (DefaultTableModel) table_data_berkas.getModel();
        ExportToExcel.writeToExcel(model_table_berkas, jPanel_Data_Berkas_Karyawan);
    }//GEN-LAST:event_button_export_data_karyawan_berkasActionPerformed

    private void button_edit_data_karyawanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_edit_data_karyawanActionPerformed
        // TODO add your handling code here:
        int j = table_data_ktp.getSelectedRow();
        if (j == -1) {
            JOptionPane.showMessageDialog(this, "Please Select Data First !");
        } else {
//            button_insert_karyawan.setEnabled(false);
            Insert_DataKaryawan insert_Dialog = new Insert_DataKaryawan(new javax.swing.JFrame(), true, table_data_ktp.getValueAt(j, 0).toString(), "edit");
            insert_Dialog.label_title_op_karyawan.setText("Edit Data Karyawan");
            insert_Dialog.setLocationRelativeTo(this);
            insert_Dialog.setVisible(true);
            insert_Dialog.setEnabled(true);
        }
        refreshTable();
    }//GEN-LAST:event_button_edit_data_karyawanActionPerformed

    private void jTabbedPane1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPane1StateChanged
        // TODO add your handling code here:
        table_data_ktp.clearSelection();
        table_data_berkas.clearSelection();
    }//GEN-LAST:event_jTabbedPane1StateChanged

    private void ComboBox_filter_departemenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ComboBox_filter_departemenActionPerformed
        // TODO add your handling code here:
//        System.out.println("combobox departemen action");
//        try {
//            ComboBox_filter_divisi.removeAllItems();
//            String query = "SELECT DISTINCT(`divisi_bagian`) AS 'divisi_bagian' "
//                    + "FROM `tb_bagian` "
//                    + "WHERE `status_bagian` = '1'"
//                    + "AND `divisi_bagian` IS NOT NULL "
//                    + "ORDER BY `divisi_bagian`";
//            if (ComboBox_filter_departemen.getSelectedItem() != "All") {
//                query = "SELECT DISTINCT(`divisi_bagian`) AS 'divisi_bagian' "
//                        + "FROM `tb_bagian` "
//                        + "WHERE `status_bagian` = 1 "
//                        + "AND `kode_departemen`='" + ComboBox_filter_departemen.getSelectedItem() + "' "
//                        + "AND `divisi_bagian` IS NOT NULL "
//                        + "ORDER BY `divisi_bagian`";
//            }
//            rs = Utility.db.getStatement().executeQuery(query);
//            ComboBox_filter_divisi.addItem("All");
//            while (rs.next()) {
//                ComboBox_filter_divisi.addItem(rs.getString("divisi_bagian"));
//            }
//            
//            ComboBox_filter_ruang.removeAllItems();
//            String query_ruangan = "SELECT DISTINCT(`ruang_bagian`) AS 'ruang_bagian' "
//                    + "FROM `tb_bagian` "
//                    + "WHERE `status_bagian` = '1'"
//                    + "AND `ruang_bagian` IS NOT NULL "
//                    + "ORDER BY `ruang_bagian`";
//            if (ComboBox_filter_departemen.getSelectedItem() != "All") {
//                query_ruangan = "SELECT DISTINCT(`ruang_bagian`) AS 'ruang_bagian' "
//                        + "FROM `tb_bagian` "
//                        + "WHERE `status_bagian` = 1 "
//                        + "AND `kode_departemen`='" + ComboBox_filter_departemen.getSelectedItem() + "' "
//                        + "AND `ruang_bagian` IS NOT NULL "
//                        + "ORDER BY `ruang_bagian`";
//            }
//            rs = Utility.db.getStatement().executeQuery(query_ruangan);
//            ComboBox_filter_ruang.addItem("All");
//            while (rs.next()) {
//                ComboBox_filter_ruang.addItem(rs.getString("ruang_bagian"));
//            }
//        } catch (SQLException e) {
//            JOptionPane.showMessageDialog(this, e);
//        }
    }//GEN-LAST:event_ComboBox_filter_departemenActionPerformed

    private void button_status_OUTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_status_OUTActionPerformed
        // TODO add your handling code here:
        try {
            String id = null;
            int GET_ROW_KTP = table_data_ktp.getSelectedRow();
            if (GET_ROW_KTP == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih karyawan dari Halaman Data Karyawan");
            } else {
                id = (String) table_data_ktp.getValueAt(GET_ROW_KTP, 0);

                if (table_data_ktp.getValueAt(GET_ROW_KTP, 9).toString().equals("SUB")) {
                    JOptionPane.showMessageDialog(this, "Silahkan mengeluarkan karyawan SUB dari menu data karyawan Sub");
                } else {
                    String status = "Tanggal Keluar :";
                    JDialog_karyawan_keluar_masuk keluar = new JDialog_karyawan_keluar_masuk(new javax.swing.JFrame(), true, id, status);
                    keluar.pack();
                    keluar.setLocationRelativeTo(this);
                    keluar.setVisible(true);
                    keluar.setEnabled(true);
                    refreshTable();
                }
            }
        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_status_OUTActionPerformed

    private void button_view_ktpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_view_ktpActionPerformed
        // TODO add your handling code here:
        int x = table_data_ktp.getSelectedRow();
        if (x == -1) {
            JOptionPane.showMessageDialog(this, "Silahkan pilih salah satu data pada tabel !");
        } else {
            String id = table_data_ktp.getValueAt(x, 0).toString();
            String nik = table_data_ktp.getValueAt(x, 2).toString();
            String nama = table_data_ktp.getValueAt(x, 3).toString();
            JDialog_Show_KTP dialog = new JDialog_Show_KTP(new javax.swing.JFrame(), true, nik, nama);
            dialog.show_ktp_local(id);
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            dialog.setEnabled(true);
            dialog.setResizable(false);
        }
    }//GEN-LAST:event_button_view_ktpActionPerformed

    private void button_status_INActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_status_INActionPerformed
        // TODO add your handling code here:
        try {
            String id = null;
            int GET_ROW_KTP = table_data_ktp.getSelectedRow();
            if (GET_ROW_KTP == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih karyawan dari Halaman Data Karyawan");
            } else {
                id = (String) table_data_ktp.getValueAt(GET_ROW_KTP, 0);
                String qry = "SELECT `status` FROM `tb_karyawan` WHERE `id_pegawai` = '" + id + "'";
                rs = Utility.db.getStatement().executeQuery(qry);
                if (rs.next()) {
                    if (rs.getString("status").equals("ABSEN")) {
                        int dialogResult = JOptionPane.showConfirmDialog(this, "Karyawan sudah selesai ABSEN (CUTI) dan masuk kembali?", "Warning", 0);
                        if (dialogResult == JOptionPane.YES_OPTION) {
                            sql = "UPDATE `tb_karyawan` SET `tanggal_keluar`=NULL, `kategori_keluar`=NULL, `status`='IN' WHERE `id_pegawai` = '" + id + "'";
                            Utility.db.getConnection().createStatement();
                            if ((Utility.db.getStatement().executeUpdate(sql)) == 1) {
                                JOptionPane.showMessageDialog(this, "Ubah status dari ABSEN -> IN (sukses)");
                            } else {
                                JOptionPane.showMessageDialog(this, "failed!!");
                            }
                        }
                    } else {
                        JDialog_karyawan_keluar_masuk masuk = new JDialog_karyawan_keluar_masuk(new javax.swing.JFrame(), true, id, "Tanggal Masuk :");
                        masuk.pack();
                        masuk.setLocationRelativeTo(this);
                        masuk.setVisible(true);
                        masuk.setEnabled(true);
                    }
                    refreshTable();
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_status_INActionPerformed

    private void txt_search_idKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_idKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_search_idKeyPressed

    private void button_status_batalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_status_batalActionPerformed
        // TODO add your handling code here:
        try {
            String id = null;
            int GET_ROW_KTP = table_data_ktp.getSelectedRow();
            if (GET_ROW_KTP == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih karyawan dari Halaman Data Karyawan");
            } else {
                id = (String) table_data_ktp.getValueAt(GET_ROW_KTP, 0);
                int dialogResult = JOptionPane.showConfirmDialog(this, "Apakah yakin akan mengubah status karyawan menjadi BATAL?", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    String Query = "UPDATE `tb_karyawan` SET `status`='BATAL', `tanggal_masuk` = NULL WHERE `id_pegawai` = '" + id + "'";
                    Utility.db.getConnection().createStatement();
                    if ((Utility.db.getStatement().executeUpdate(Query)) == 1) {
                        JOptionPane.showMessageDialog(this, "status karyawan telah berubah menjadi BATAL");
                    } else {
                        JOptionPane.showMessageDialog(this, "ERROR, silahkan lapor ke bagian IT");
                    }
                    refreshTable();
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_status_batalActionPerformed

    private void button_importActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_importActionPerformed
        try {
            // TODO add your handling code here:
            ImportFromCSV();
        } catch (ParseException ex) {
            Logger.getLogger(JPanel_Data_Karyawan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_importActionPerformed

    private void button_export_data_cimbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_data_cimbActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model_table_cimb = (DefaultTableModel) table_data_cimb.getModel();
        ExportToExcel.writeToExcel(model_table_cimb, jPanel_Data_CIMB_Karyawan);
    }//GEN-LAST:event_button_export_data_cimbActionPerformed

    private void button_input_fotoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_input_fotoActionPerformed
        // TODO add your handling code here:
        int j = table_data_ktp.getSelectedRow();
        if (j == -1) {
            JOptionPane.showMessageDialog(this, "Please Select Data First !");
        } else {
            String id = table_data_ktp.getValueAt(j, 0).toString();
            String nama = table_data_ktp.getValueAt(j, 3).toString();
            JDialog_Input_Foto insert_foto = new JDialog_Input_Foto(new javax.swing.JFrame(), true, id, nama);
            insert_foto.pack();
            insert_foto.setLocationRelativeTo(this);
            insert_foto.setVisible(true);
            insert_foto.setEnabled(true);
        }
    }//GEN-LAST:event_button_input_fotoActionPerformed

    private void button_import_editActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_import_editActionPerformed
        // TODO add your handling code here:
        try {
            // TODO add your handling code here:
            ImportDataEdit();
        } catch (ParseException ex) {
            Logger.getLogger(JPanel_Data_Karyawan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_import_editActionPerformed

    private void button_PrintIDCARDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_PrintIDCARDActionPerformed
        // TODO add your handling code here:
        try {
            int x = table_data_ktp.getSelectedRow();
            if (x > -1) {
                String id = table_data_ktp.getValueAt(x, 0).toString();
                JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\IDCARD_Karyawan.jrxml");
                Map<String, Object> map = new HashMap<>();
                map.put("id_pegawai", id);//parameter name should be like it was named inside your report.
                map.put("TITLE", "PT. WALETA ASIA JAYA");//parameter name should be like it was named inside your report.
                JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
                JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, map, Utility.db.getConnection());
                JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
            }
        } catch (JRException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Data_Karyawan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_PrintIDCARDActionPerformed

    private void button_PrintIDCARD_SemuaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_PrintIDCARD_SemuaActionPerformed
        // TODO add your handling code here:
        try {
            JRDesignQuery newQuery = new JRDesignQuery();
            newQuery.setText(sql);
            JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\IDCARD_Karyawan_PrintAll.jrxml");
            Map<String, Object> map = new HashMap<>();
            map.put("TITLE", "PT. WALETA ASIA JAYA");//parameter name should be like it was named inside your report.
            JASP_DESIGN.setQuery(newQuery);
            JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
            JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, map, Utility.db.getConnection());
            JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
        } catch (JRException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Data_Karyawan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_PrintIDCARD_SemuaActionPerformed

    private void button_set_JamKerjaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_set_JamKerjaActionPerformed
        // TODO add your handling code here:
        int j = table_data_ktp.getSelectedRow();
        if (j == -1) {
            JOptionPane.showMessageDialog(this, "Please Select Data First !");
        } else {
            String id = table_data_ktp.getValueAt(j, 0).toString();
            String nama = table_data_ktp.getValueAt(j, 3).toString();
            JDialog_Set_JamKerja dialog = new JDialog_Set_JamKerja(new javax.swing.JFrame(), true, id, nama);
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            dialog.setEnabled(true);
        }
    }//GEN-LAST:event_button_set_JamKerjaActionPerformed

    private void button_masuk_kembaliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_masuk_kembaliActionPerformed
        // TODO add your handling code here:
        try {
            int GET_ROW_KTP = table_data_ktp.getSelectedRow();
            if (GET_ROW_KTP == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih karyawan dari Halaman Data Karyawan");
            } else {
                String id = table_data_ktp.getValueAt(GET_ROW_KTP, 0).toString();
                String nama = table_data_ktp.getValueAt(GET_ROW_KTP, 3).toString();
                int dialogResult = JOptionPane.showConfirmDialog(this, "Apakah " + nama + " masuk kembali?", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    String Select = "SELECT * FROM `tb_karyawan` WHERE `id_pegawai` = '" + id + "' AND `status` LIKE '%OUT%'";
                    PreparedStatement pst1 = Utility.db.getConnection().prepareStatement(Select);
                    ResultSet rs1 = pst1.executeQuery();
                    if (rs1.next()) {
                        String new_id = generate_ID();
                        String insert = "INSERT INTO `tb_karyawan`(`id_pegawai`, `pin_finger`, `nik_ktp`, `nama_pegawai`, `jenis_kelamin`, `tempat_lahir`, `tanggal_lahir`, `agama`, `alamat`, `desa`, `kecamatan`, `golongan_darah`, `no_telp`, `email`, `status_kawin`, `nama_ibu`, `kode_bagian`, `posisi`, `pendidikan`, `tanggal_interview`, `tanggal_masuk`, `tanggal_keluar`, `status`, `level_gaji`, `jam_kerja`, `fc_ktp`, `sertifikat_vaksin1`, `sertifikat_vaksin2`, `berkas_surat_pernyataan`, `tanggal_surat`, `rek_cimb`, `keterangan`) "
                                + "VALUES ("
                                + "'" + new_id + "',"
                                + "'" + new_id.substring(7) + "',"
                                + "'" + rs1.getString("nik_ktp") + "',"
                                + "'" + rs1.getString("nama_pegawai") + "',"
                                + "'" + rs1.getString("jenis_kelamin") + "',"
                                + "'" + rs1.getString("tempat_lahir") + "',"
                                + "'" + rs1.getString("tanggal_lahir") + "',"
                                + "'" + rs1.getString("agama") + "',"
                                + "'" + rs1.getString("alamat") + "',"
                                + "'" + rs1.getString("desa") + "',"
                                + "'" + rs1.getString("kecamatan") + "',"
                                + "'" + rs1.getString("golongan_darah") + "',"
                                + "'" + rs1.getString("no_telp") + "',"
                                + "'" + rs1.getString("email") + "',"
                                + "'" + rs1.getString("status_kawin") + "',"
                                + "'" + rs1.getString("nama_ibu") + "',"
                                + "NULL,"
                                + "'" + rs1.getString("posisi") + "',"
                                + "'" + rs1.getString("pendidikan") + "',"
                                + "'" + new SimpleDateFormat("yyyy-MM-dd").format(today) + "',"
                                + "'" + new SimpleDateFormat("yyyy-MM-dd").format(today) + "',"
                                + "NULL,"
                                + "'-',"
                                + "NULL,"
                                + "NULL,"
                                + "'" + rs1.getInt("fc_ktp") + "',"
                                + "'" + rs1.getInt("sertifikat_vaksin1") + "',"
                                + "'" + rs1.getInt("sertifikat_vaksin2") + "',"
                                + "'" + rs1.getInt("berkas_surat_pernyataan") + "',"
                                + "NULL,"
                                + "NULL,"
                                + "NULL"
                                + ")";
                        pst = Utility.db.getConnection().prepareStatement(insert);
                        if (pst.executeUpdate() > 0) {
                            JOptionPane.showMessageDialog(this, "Data Berhasil Ditambahkan !");
                        } else {
                            JOptionPane.showMessageDialog(this, "Data Gagal Ditambahkan !");
                        }

                        refreshTable();
                    } else {
                        JOptionPane.showMessageDialog(this, "Maaf, karyawan masih belum keluar");
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_masuk_kembaliActionPerformed

    private void button_set_JamKerja1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_set_JamKerja1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_button_set_JamKerja1ActionPerformed

    private void button_set_JamKerja2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_set_JamKerja2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_button_set_JamKerja2ActionPerformed

    private void button_finger2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_finger2ActionPerformed
        int j = table_data_ktp.getSelectedRow();
        if (j == -1) {
            JOptionPane.showMessageDialog(this, "Please Select Data First !");
        } else {
            String id = table_data_ktp.getValueAt(j, 0).toString();
            MyEnrollmentForm form = new MyEnrollmentForm(null, id, "finger2");
            form.setVisible(true);
        }
    }//GEN-LAST:event_button_finger2ActionPerformed

    private void button_finger1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_finger1ActionPerformed
        int j = table_data_ktp.getSelectedRow();
        if (j == -1) {
            JOptionPane.showMessageDialog(this, "Please Select Data First !");
        } else {
            String id = table_data_ktp.getValueAt(j, 0).toString();
            MyEnrollmentForm form = new MyEnrollmentForm(null, id, "finger1");
            form.setVisible(true);
        }
    }//GEN-LAST:event_button_finger1ActionPerformed

    private void button_tes_finger1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_tes_finger1ActionPerformed
        int j = table_data_ktp.getSelectedRow();
        if (j == -1) {
            JOptionPane.showMessageDialog(this, "Please Select Data First !");
        } else {
            String id = table_data_ktp.getValueAt(j, 0).toString();
            MyVerificationForm form = new MyVerificationForm(null, JPanel_Data_Karyawan.this, id, "finger1");
            form.setVisible(true);
        }
    }//GEN-LAST:event_button_tes_finger1ActionPerformed

    private void button_tes_finger2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_tes_finger2ActionPerformed
        int j = table_data_ktp.getSelectedRow();
        if (j == -1) {
            JOptionPane.showMessageDialog(this, "Please Select Data First !");
        } else {
            String id = table_data_ktp.getValueAt(j, 0).toString();
            MyVerificationForm form = new MyVerificationForm(null, JPanel_Data_Karyawan.this, id, "finger2");
            form.setVisible(true);
        }
    }//GEN-LAST:event_button_tes_finger2ActionPerformed

    private void button_set_jemputanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_set_jemputanActionPerformed
        // TODO add your handling code here:
        try {
            String id = null;
            int GET_ROW_KTP = table_data_ktp.getSelectedRow();
            if (GET_ROW_KTP == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih karyawan dari Halaman Data Karyawan");
            } else {
                id = table_data_ktp.getValueAt(GET_ROW_KTP, 0).toString();
                JDialog_Set_JalurJemputan dialog = new JDialog_Set_JalurJemputan(new javax.swing.JFrame(), true, id);
                dialog.pack();
                dialog.setLocationRelativeTo(this);
                dialog.setVisible(true);
                dialog.setEnabled(true);
                refreshTable();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_set_jemputanActionPerformed

    private void txt_nama_pelatihanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_nama_pelatihanKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_nama_pelatihanKeyPressed

    private void button_print_daftar_hadir_pelatihanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_print_daftar_hadir_pelatihanActionPerformed
        // TODO add your handling code here:
        try {
            JRDesignQuery newQuery = new JRDesignQuery();
            newQuery.setText(sql);
            JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Daftar_hadir_Pelatihan.jrxml");
            JASP_DESIGN.setQuery(newQuery);
            Map<String, Object> params = new HashMap<>();
            params.put("tanggal", new SimpleDateFormat("dd MMMM yyyy").format(Date_Pelatihan.getDate()));
            params.put("nama_pelatihan", txt_nama_pelatihan.getText());
            JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
            JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, params, Utility.db.getConnection());
            JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
        } catch (JRException ex) {
            Logger.getLogger(JPanel_Data_Karyawan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_print_daftar_hadir_pelatihanActionPerformed

    private void button_print_evaluasi_pelatihanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_print_evaluasi_pelatihanActionPerformed
        // TODO add your handling code here:
        try {
            JRDesignQuery newQuery = new JRDesignQuery();
            newQuery.setText(sql);
            JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Daftar_Evaluasi_Pelatihan.jrxml");
            JASP_DESIGN.setQuery(newQuery);
            JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("tgl_evaluasi", new SimpleDateFormat("dd MMMM yyyy").format(Date_evaluasi_Pelatihan.getDate()));
            params.put("nama_pelatihan", txt_nama_pelatihan.getText());
            JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, params, Utility.db.getConnection());
            JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
        } catch (JRException ex) {
            Logger.getLogger(JPanel_Data_Karyawan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_print_evaluasi_pelatihanActionPerformed

    private void txt_search_bagianKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_bagianKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_search_bagianKeyPressed

    private void button_open_sertifikat_vaksin1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_open_sertifikat_vaksin1ActionPerformed
        // TODO add your handling code here:
        int x = table_data_berkas.getSelectedRow();
        if (x < 0) {
            JOptionPane.showMessageDialog(this, "Pilih salah satu data BOX !");
        } else {
            String file_name = table_data_berkas.getValueAt(x, 1).toString();
            try {
                Runtime rt = Runtime.getRuntime();
                Process pr = rt.exec("explorer.exe \\\\192.168.10.2\\Shared Folder\\DOKUMEN SISTEM\\Sertifikat_Vaksin1_Covid19\\" + file_name + ".pdf");
            } catch (IOException ex) {
                Logger.getLogger(JPanel_Data_Karyawan.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_button_open_sertifikat_vaksin1ActionPerformed

    private void button_open_fc_ktpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_open_fc_ktpActionPerformed
        // TODO add your handling code here:
        int x = table_data_berkas.getSelectedRow();
        if (x < 0) {
            JOptionPane.showMessageDialog(this, "Pilih salah satu data BOX !");
        } else {
            String file_name = table_data_berkas.getValueAt(x, 1).toString();
            try {
                Runtime rt = Runtime.getRuntime();
                Process pr = rt.exec("explorer.exe \\\\192.168.10.2\\Shared Folder\\DOKUMEN SISTEM\\FC_KTP\\" + file_name + ".pdf");
            } catch (IOException ex) {
                Logger.getLogger(JPanel_Data_Karyawan.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_button_open_fc_ktpActionPerformed

    private void button_open_sertifikat_vaksin2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_open_sertifikat_vaksin2ActionPerformed
        // TODO add your handling code here:
        int x = table_data_berkas.getSelectedRow();
        if (x < 0) {
            JOptionPane.showMessageDialog(this, "Pilih salah satu data BOX !");
        } else {
            String file_name = table_data_berkas.getValueAt(x, 1).toString();
            try {
                Runtime rt = Runtime.getRuntime();
                Process pr = rt.exec("explorer.exe \\\\192.168.10.2\\Shared Folder\\DOKUMEN SISTEM\\Sertifikat_Vaksin2_Covid19\\" + file_name + ".pdf");
            } catch (IOException ex) {
                Logger.getLogger(JPanel_Data_Karyawan.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_button_open_sertifikat_vaksin2ActionPerformed

    private void button_PrintIDCARD1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_PrintIDCARD1ActionPerformed
        // TODO add your handling code here:
        try {
            int x = table_data_ktp.getSelectedRow();
            if (x > -1) {
                String id = table_data_ktp.getValueAt(x, 0).toString();
                JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\IDCARD_Karyawan_2.jrxml");
                Map<String, Object> map = new HashMap<>();
                map.put("id_pegawai", id);//parameter name should be like it was named inside your report.
                JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
                JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, map, Utility.db.getConnection());
                JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Data_Karyawan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_PrintIDCARD1ActionPerformed

    private void button_status_absenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_status_absenActionPerformed
        // TODO add your handling code here:
        try {
            String id = null;
            int GET_ROW_KTP = table_data_ktp.getSelectedRow();
            if (GET_ROW_KTP == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih karyawan dari Halaman Data Karyawan");
            } else {
                id = (String) table_data_ktp.getValueAt(GET_ROW_KTP, 0);
                int dialogResult = JOptionPane.showConfirmDialog(this, "Apakah yakin akan mengubah status karyawan menjadi ABSEN?", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    String Query = "UPDATE `tb_karyawan` SET `status`='ABSEN' WHERE `id_pegawai` = '" + id + "'";
                    Utility.db.getConnection().createStatement();
                    if ((Utility.db.getStatement().executeUpdate(Query)) == 1) {
                        JOptionPane.showMessageDialog(this, "status karyawan telah berubah menjadi ABSEN");
                    } else {
                        JOptionPane.showMessageDialog(this, "ERROR, silahkan lapor ke bagian IT");
                    }
                    refreshTable();
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_status_absenActionPerformed

    private void button_PrintIDCARD2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_PrintIDCARD2ActionPerformed
        // TODO add your handling code here:
        try {
            int x = table_data_ktp.getSelectedRow();
            if (x > -1) {
                String id = table_data_ktp.getValueAt(x, 0).toString();
                JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\IDCARD_Karyawan_2_QROnly.jrxml");
                Map<String, Object> map = new HashMap<>();
                map.put("id_pegawai", id);//parameter name should be like it was named inside your report.
                JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
                JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, map, Utility.db.getConnection());
                JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Data_Karyawan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_PrintIDCARD2ActionPerformed

    private void button_unduh_ktpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_unduh_ktpActionPerformed
        // TODO add your handling code here:
        int x = table_data_ktp.getSelectedRow();
        if (x > -1) {
            if ((boolean) table_data_ktp.getValueAt(x, 38)) {
                try {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
                    //filter files extension
                    FileNameExtensionFilter filter = new FileNameExtensionFilter("JPEG file", "jpg", "jpeg", "png");
                    fileChooser.setFileFilter(filter);
                    int OptionChoosed = fileChooser.showSaveDialog(this);
                    if (OptionChoosed == JFileChooser.APPROVE_OPTION) {
                        File selectedFile = fileChooser.getSelectedFile();
                        String fileName = selectedFile.getCanonicalPath();
                        if (!fileName.endsWith(".jpg")) {
                            File input = new File("\\\\192.168.10.2\\Shared Folder\\DOKUMEN SISTEM\\FC_KTP\\" + table_data_ktp.getValueAt(x, 0).toString() + ".JPG");
                            File output = new File(fileName + ".jpg");
                            Utility.createImage(input, output);
                            JOptionPane.showMessageDialog(this, "Download berhasil, silahkan cek pada folder anda.");
                        }
                    } else if (OptionChoosed == JFileChooser.CANCEL_OPTION) {
                    }
                } catch (Exception ex) {
                    System.out.println(table_data_ktp.getValueAt(x, 0).toString());
                    Logger.getLogger(JPanel_Data_Karyawan.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Foto KTP belum masukkan !");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Slahkan pilih karyawan terlebih dahulu !");
        }
    }//GEN-LAST:event_button_unduh_ktpActionPerformed

    private void ComboBox_filter_divisiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ComboBox_filter_divisiActionPerformed
        // TODO add your handling code here:
        System.out.println("combobox divisi action");
        try {
            ComboBox_filter_bagian.removeAllItems();
            String query = "SELECT DISTINCT(`bagian_bagian`) AS 'bagian_bagian' "
                    + "FROM `tb_bagian` "
                    + "WHERE `status_bagian` = '1'"
                    + "AND `bagian_bagian` IS NOT NULL "
                    + "ORDER BY `bagian_bagian`";
            if (ComboBox_filter_divisi.getSelectedItem() != "All") {
                query = "SELECT DISTINCT(`bagian_bagian`) AS 'bagian_bagian' "
                        + "FROM `tb_bagian` "
                        + "WHERE `status_bagian` = 1 "
                        + "AND `divisi_bagian`='" + ComboBox_filter_divisi.getSelectedItem() + "' "
                        + "AND `bagian_bagian` IS NOT NULL "
                        + "ORDER BY `bagian_bagian`";
            }
            rs = Utility.db.getStatement().executeQuery(query);
            ComboBox_filter_bagian.addItem("All");
            while (rs.next()) {
                ComboBox_filter_bagian.addItem(rs.getString("bagian_bagian"));
            }

            ComboBox_filter_ruang.removeAllItems();
            String query_ruangan = "SELECT DISTINCT(`ruang_bagian`) AS 'ruang_bagian' "
                    + "FROM `tb_bagian` "
                    + "WHERE `status_bagian` = '1'"
                    + "AND `ruang_bagian` IS NOT NULL "
                    + "ORDER BY `ruang_bagian`";
            if (ComboBox_filter_divisi.getSelectedItem() != "All") {
                query_ruangan = "SELECT DISTINCT(`ruang_bagian`) AS 'ruang_bagian' "
                        + "FROM `tb_bagian` "
                        + "WHERE `status_bagian` = 1 "
                        + "AND `divisi_bagian`='" + ComboBox_filter_divisi.getSelectedItem() + "' "
                        + "AND `ruang_bagian` IS NOT NULL "
                        + "ORDER BY `ruang_bagian`";
            }
            rs = Utility.db.getStatement().executeQuery(query_ruangan);
            ComboBox_filter_ruang.addItem("All");
            while (rs.next()) {
                ComboBox_filter_ruang.addItem(rs.getString("ruang_bagian"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e);
        }
    }//GEN-LAST:event_ComboBox_filter_divisiActionPerformed

    private void button_data_berkas_karyawanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_data_berkas_karyawanActionPerformed
        // TODO add your handling code here:
        refreshTable_data_berkas_karyawan();
    }//GEN-LAST:event_button_data_berkas_karyawanActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_filter_bagian;
    private javax.swing.JComboBox<String> ComboBox_filter_departemen;
    private javax.swing.JComboBox<String> ComboBox_filter_divisi;
    private javax.swing.JComboBox<String> ComboBox_filter_posisi_bagian;
    private javax.swing.JComboBox<String> ComboBox_filter_ruang;
    private javax.swing.JComboBox<String> ComboBox_filter_tanggal;
    private javax.swing.JComboBox<String> ComboBox_kelamin_karyawan;
    private javax.swing.JComboBox<String> ComboBox_posisi;
    private javax.swing.JComboBox<String> ComboBox_status_karyawan;
    private com.toedter.calendar.JDateChooser Date_Pelatihan;
    private com.toedter.calendar.JDateChooser Date_Search1;
    private com.toedter.calendar.JDateChooser Date_Search2;
    private com.toedter.calendar.JDateChooser Date_evaluasi_Pelatihan;
    public javax.swing.JButton button_PrintIDCARD;
    public javax.swing.JButton button_PrintIDCARD1;
    public javax.swing.JButton button_PrintIDCARD2;
    public javax.swing.JButton button_PrintIDCARD_Semua;
    public static javax.swing.JButton button_data_berkas_karyawan;
    public javax.swing.JButton button_edit_data_karyawan;
    private javax.swing.JButton button_export_data_cimb;
    private javax.swing.JButton button_export_data_karyawan_berkas;
    private javax.swing.JButton button_export_data_karyawan_ktp;
    public javax.swing.JButton button_finger1;
    public javax.swing.JButton button_finger2;
    private javax.swing.JButton button_import;
    private javax.swing.JButton button_import_edit;
    private javax.swing.JButton button_input_foto;
    public javax.swing.JButton button_insert_karyawan;
    public javax.swing.JButton button_masuk_kembali;
    private javax.swing.JButton button_open_fc_ktp;
    private javax.swing.JButton button_open_sertifikat_vaksin1;
    private javax.swing.JButton button_open_sertifikat_vaksin2;
    public javax.swing.JButton button_print_daftar_hadir_pelatihan;
    public javax.swing.JButton button_print_evaluasi_pelatihan;
    public static javax.swing.JButton button_search_karyawan;
    public javax.swing.JButton button_set_JamKerja;
    public javax.swing.JButton button_set_JamKerja1;
    public javax.swing.JButton button_set_JamKerja2;
    private javax.swing.JButton button_set_jemputan;
    public javax.swing.JButton button_status_IN;
    public javax.swing.JButton button_status_OUT;
    public javax.swing.JButton button_status_absen;
    public javax.swing.JButton button_status_batal;
    public javax.swing.JButton button_tes_finger1;
    public javax.swing.JButton button_tes_finger2;
    private javax.swing.JButton button_unduh_ktp;
    private javax.swing.JButton button_view_ktp;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel_Data_Berkas_Karyawan;
    private javax.swing.JPanel jPanel_Data_CIMB_Karyawan;
    private javax.swing.JPanel jPanel_Data_KTP_Karyawan;
    private javax.swing.JPanel jPanel_search_karyawan;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel label_total_absen;
    private javax.swing.JLabel label_total_batal;
    private javax.swing.JLabel label_total_data;
    private javax.swing.JLabel label_total_in;
    private javax.swing.JLabel label_total_out;
    private javax.swing.JLabel label_total_strip;
    public static javax.swing.JTable table_data_berkas;
    public static javax.swing.JTable table_data_cimb;
    public static javax.swing.JTable table_data_ktp;
    private javax.swing.JTextField txt_nama_pelatihan;
    private javax.swing.JTextField txt_search_bagian;
    private javax.swing.JTextField txt_search_id;
    private javax.swing.JTextField txt_search_karyawan;
    // End of variables declaration//GEN-END:variables

}
