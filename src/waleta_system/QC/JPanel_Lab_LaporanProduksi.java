package waleta_system.QC;

import java.awt.HeadlessException;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
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
import waleta_system.Class.ExportToExcel;
import waleta_system.Class.Utility;

public class JPanel_Lab_LaporanProduksi extends javax.swing.JPanel {

    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    String sql = null;
    ResultSet rs;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    JFileChooser chooser = new JFileChooser();

    public void init() {
        refreshTable();
        refreshTabel_rekapLP_HOLD();
        Table_Data_Lab_LP.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && Table_Data_Lab_LP.getSelectedRow() != -1) {
                    int i = Table_Data_Lab_LP.getSelectedRow();
                    label_no_lp.setText(Table_Data_Lab_LP.getValueAt(i, 0).toString());
                    label_no_lp1.setText(Table_Data_Lab_LP.getValueAt(i, 0).toString());
                    refreshTable_Treatment();
                    refreshTable_Treatment_sela();
                    if (Table_Data_Lab_LP.getValueAt(i, 9) != null) {
                        try {
                            Date tgl_uji_lp = dateFormat.parse(Table_Data_Lab_LP.getValueAt(i, 9).toString());
                        } catch (ParseException ex) {
                            Logger.getLogger(JPanel_Lab_LaporanProduksi.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        button_input_data.setEnabled(false);
                        if (Table_Data_Lab_LP.getValueAt(i, 10) != null) {
                            button_setor_lp.setEnabled(false);
                            button_edit_treatment.setEnabled(false);
                            button_delete_treatment.setEnabled(false);
                            button_insert_treatment.setEnabled(false);
                        } else {
                            button_setor_lp.setEnabled(true);
                            button_edit_treatment.setEnabled(true);
                            button_delete_treatment.setEnabled(true);
                            button_insert_treatment.setEnabled(true);
                        }
                    } else {
                        button_input_data.setEnabled(true);
                        button_setor_lp.setEnabled(false);
                    }

                }
            }
        });
    }

    public void refreshTable() {
        try {
            DefaultTableModel model = (DefaultTableModel) Table_Data_Lab_LP.getModel();
            model.setRowCount(0);
            int total_passed = 0, total_hold = 0, total_belumUji = 0, total_nonGNS = 0;

            String status_print_1 = "";
            if (ComboBox_status_print1.getSelectedIndex() == 1) {
                status_print_1 = " AND `print_label` = 1 ";
            } else if (ComboBox_status_print1.getSelectedIndex() == 2) {
                status_print_1 = " AND `print_label` = 0 ";
            }

            String status_print_2 = "";
            if (ComboBox_status_print2.getSelectedIndex() == 1) {
                status_print_2 = " AND `print_label_setor` = 1 ";
            } else if (ComboBox_status_print2.getSelectedIndex() == 2) {
                status_print_2 = " AND `print_label_setor` = 0 ";
            }

            String filter_status_uji = "";
            String filter_status_akhir = "";
            switch (ComboBox_Filter_status_uji.getSelectedIndex()) {
                case 0:
                    filter_status_uji = "";
                    break;
                case 1:
                    filter_status_uji = " AND `tb_lab_laporan_produksi`.`status`='PASSED'";
                    break;
                case 2:
                    filter_status_uji = " AND `tb_lab_laporan_produksi`.`status`='HOLD/NON GNS' ";
                    break;
                default:
                    break;
            }

            switch (ComboBox_Filter_status_akhir.getSelectedIndex()) {
                case 0:
                    filter_status_akhir = "";
                    break;
                case 1:
                    filter_status_akhir = " AND `tb_lab_laporan_produksi`.`status_akhir`='PASSED'";
                    break;
                case 2:
                    filter_status_akhir = " AND `tb_lab_laporan_produksi`.`status_akhir`='HOLD/NON GNS'";
                    break;
                default:
                    break;
            }

            String filter_tanggal_baku_masuk_ct1 = "";
            if (Date1_masuk_baku_ct1.getDate() != null && Date2_masuk_baku_ct1.getDate() != null) {
                if (ComboBox_filter_tgl.getSelectedIndex() == 0) {
                    filter_tanggal_baku_masuk_ct1 = " AND `tb_bahan_baku_masuk_cheat`.`tgl_masuk` BETWEEN '" + dateFormat.format(Date1_masuk_baku_ct1.getDate()) + "' AND '" + dateFormat.format(Date2_masuk_baku_ct1.getDate()) + "'";
                } else {
                    filter_tanggal_baku_masuk_ct1 = " AND `tanggal_rendam` BETWEEN '" + dateFormat.format(Date1_masuk_baku_ct1.getDate()) + "' AND '" + dateFormat.format(Date2_masuk_baku_ct1.getDate()) + "'";
                }
            }
            String filter_tanggal = "";
            if (Date_1.getDate() != null && Date_2.getDate() != null) {
                switch (ComboBox_search_tanggal.getSelectedIndex()) {
                    case 0:
                        filter_tanggal = " AND `tb_lab_laporan_produksi`.`tgl_masuk` BETWEEN '" + dateFormat.format(Date_1.getDate()) + "' AND '" + dateFormat.format(Date_2.getDate()) + "'";
                        break;
                    case 1:
                        filter_tanggal = " AND `tgl_sampling` BETWEEN '" + dateFormat.format(Date_1.getDate()) + "' AND '" + dateFormat.format(Date_2.getDate()) + "'";
                        break;
                    case 2:
                        filter_tanggal = " AND `tgl_uji` BETWEEN '" + dateFormat.format(Date_1.getDate()) + "' AND '" + dateFormat.format(Date_2.getDate()) + "'";
                        break;
                    case 3:
                        filter_tanggal = " AND `tgl_selesai` BETWEEN '" + dateFormat.format(Date_1.getDate()) + "' AND '" + dateFormat.format(Date_2.getDate()) + "'";
                        break;
                    default:
                        break;
                }
            }

            sql = "SELECT `tb_lab_laporan_produksi`.`no_laporan_produksi`, `tb_laporan_produksi`.`kode_grade`, `ruangan`, `tb_laporan_produksi`.`no_kartu_waleta`, `tb_laporan_produksi`.`memo_lp`, `tb_lab_laporan_produksi`.`tgl_masuk`, `tgl_sampling`, `pekerja_sampling`, `tb_karyawan`.`nama_pegawai` AS 'nama_pekerja_sampling', `tgl_uji`, `tgl_selesai`, `tb_rumah_burung`.`nama_rumah_burung`, `status_akhir`, `print_label`, `print_label_setor`, `tb_lab_laporan_produksi`.`keterangan`, "
                    + "GREATEST(`nitrit_bm`, `nitrit_bm_w2`, `nitrit_bm_w3`) AS 'nitrit_bm', `nitrit_utuh`, `nitrit_flat`, `jidun`, `kadar_air_bahan_jadi`, `tb_lab_laporan_produksi`.`status`, `utuh`, `pecah`, `flat`, `kpg_akhir`, `gram_akhir`, `jenis_treatment`, CONCAT(`suhu_awal_steam`, '-', `suhu_akhir_steam`, '°C / ', TIME_FORMAT(`waktu_steam`, '%i:%s')) AS 'waktu_suhu_steam', \n"
                    + "`tanggal_rendam` "
                    + "FROM `tb_lab_laporan_produksi` "
                    + "LEFT JOIN `tb_rendam` ON `tb_lab_laporan_produksi`.`no_laporan_produksi` = `tb_rendam`.`no_laporan_produksi` "
                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_lab_laporan_produksi`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi` "
                    + "LEFT JOIN `tb_lab_bahan_baku` ON `tb_laporan_produksi`.`no_kartu_waleta` = `tb_lab_bahan_baku`.`no_kartu_waleta`\n"
                    + "LEFT JOIN `tb_bahan_baku_masuk_cheat` ON `tb_laporan_produksi`.`cheat_no_kartu` = `tb_bahan_baku_masuk_cheat`.`no_kartu_waleta`\n"
                    + "LEFT JOIN `tb_bahan_baku_masuk` ON `tb_laporan_produksi`.`no_kartu_waleta` = `tb_bahan_baku_masuk`.`no_kartu_waleta`\n"
                    + "LEFT JOIN `tb_rumah_burung` ON `tb_rumah_burung`.`no_registrasi` = `tb_bahan_baku_masuk`.`no_registrasi`\n"
                    + "LEFT JOIN `tb_karyawan` ON `tb_lab_laporan_produksi`.`pekerja_sampling` = `tb_karyawan`.`id_pegawai`\n"
                    + "WHERE "
                    + "`tb_lab_laporan_produksi`.`no_laporan_produksi` LIKE '%" + txt_search_No_LP.getText() + "%' \n"
                    + "AND `tb_laporan_produksi`.`no_kartu_waleta` LIKE '%" + txt_search_No_Kartu.getText() + "%' \n"
                    + "AND `tb_laporan_produksi`.`memo_lp` LIKE '%" + txt_search_Memo.getText() + "%' \n"
                    + "AND `tb_laporan_produksi`.`ruangan` LIKE '%" + txt_search_Ruangan.getText() + "%' \n"
                    + filter_status_uji
                    + filter_status_akhir
                    + status_print_1
                    + status_print_2
                    + filter_tanggal
                    + filter_tanggal_baku_masuk_ct1
                    + "ORDER BY `tb_lab_laporan_produksi`.`no_laporan_produksi` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[30];
            while (rs.next()) {
                String waktu_suhu_steam = "";
                if (rs.getString("waktu_suhu_steam") != null) {
                    waktu_suhu_steam = rs.getString("waktu_suhu_steam");
                }
                row[0] = rs.getString("no_laporan_produksi");
                row[1] = rs.getString("kode_grade");
                row[2] = rs.getString("no_kartu_waleta");
                row[3] = rs.getString("nama_rumah_burung");
                row[4] = rs.getString("memo_lp");
                row[5] = rs.getString("jenis_treatment") + " " + waktu_suhu_steam;
                row[6] = rs.getDate("tgl_masuk");
                row[7] = rs.getDate("tgl_sampling");
                row[8] = rs.getString("nama_pekerja_sampling");
                row[9] = rs.getDate("tgl_uji");
                row[10] = rs.getDate("tgl_selesai");
                row[11] = rs.getFloat("nitrit_bm") == 0 ? "0" : rs.getFloat("nitrit_bm");
                row[12] = rs.getFloat("nitrit_utuh") == 0 ? "0" : rs.getFloat("nitrit_utuh");
                row[13] = rs.getFloat("nitrit_flat") == 0 ? "0" : rs.getFloat("nitrit_flat");
                row[14] = rs.getFloat("jidun") == 0 ? "0" : rs.getFloat("jidun");
                row[15] = rs.getFloat("kadar_air_bahan_jadi") == 0 ? "0" : rs.getFloat("kadar_air_bahan_jadi");
                row[16] = rs.getFloat("utuh");
                row[17] = rs.getFloat("pecah");
                row[18] = rs.getFloat("flat");
                row[19] = rs.getInt("kpg_akhir");
                row[20] = rs.getFloat("gram_akhir");
                row[21] = rs.getString("status");
                row[22] = rs.getString("status_akhir");
                row[23] = rs.getString("keterangan");
                row[24] = rs.getBoolean("print_label");
                row[25] = rs.getBoolean("print_label_setor");
                if (CheckBox_utuh.isSelected() && rs.getFloat("nitrit_utuh") > 0) {
                    model.addRow(row);
                } else if (CheckBox_flat.isSelected() && rs.getFloat("nitrit_flat") > 0) {
                    model.addRow(row);
                } else if (CheckBox_jdn.isSelected() && rs.getFloat("jidun") > 0) {
                    model.addRow(row);
                } else if (!CheckBox_utuh.isSelected() && !CheckBox_flat.isSelected() && !CheckBox_jdn.isSelected()) {
                    model.addRow(row);
                }

                if (null == rs.getString("status")) {
                    total_belumUji++;
                } else {
                    switch (rs.getString("status")) {
                        case "PASSED":
                            total_passed++;
                            break;
                        case "HOLD/NON GNS":
                            if ("HOLD/NON GNS".equals(rs.getString("status_akhir"))) {
                                total_nonGNS++;
                            }
                            total_hold++;
                            break;
                        case "":
                            total_belumUji++;
                            break;
                        default:
                            System.out.println("status salah = " + rs.getString("status"));
                            break;
                    }
                }
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_Data_Lab_LP);
            Table_Data_Lab_LP.getColumnModel().getColumn(4).setPreferredWidth(100);
            int rowData = Table_Data_Lab_LP.getRowCount();
            label_total_lp.setText(Integer.toString(rowData));
            label_total_passed.setText(Integer.toString(total_passed));
            label_total_hold.setText(Integer.toString(total_hold));
            label_total_non_ns.setText(Integer.toString(total_nonGNS));
            label_total_belumUji.setText(Integer.toString(total_belumUji));

            int total_lp_sudahUji = rowData - total_belumUji;
            float persen_passed = ((float) total_passed / (float) total_lp_sudahUji) * 100;
            float persen_hold = ((float) total_hold / (float) total_lp_sudahUji) * 100;
            float persen_nonGNS = ((float) total_nonGNS / (float) total_lp_sudahUji) * 100;
            float persen_belumUji = ((float) total_belumUji / (float) rowData) * 100;
            label_persen_passed.setText(decimalFormat.format(persen_passed));
            label_persen_hold.setText(decimalFormat.format(persen_hold));
            label_persen_non_ns.setText(decimalFormat.format(persen_nonGNS));
            label_persen_belumUji.setText(decimalFormat.format(persen_belumUji));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Lab_LaporanProduksi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_Treatment() {
        try {
            DefaultTableModel model = (DefaultTableModel) Table_data_treatment.getModel();
            model.setRowCount(0);
            String sql_treatment = "SELECT * FROM `tb_lab_treatment_lp` WHERE `no_laporan_produksi` = '" + label_no_lp.getText() + "'";
            rs = Utility.db.getStatement().executeQuery(sql_treatment);
            Object[] row = new Object[8];
            while (rs.next()) {
                row[0] = rs.getString("kode_treatment");
                row[1] = rs.getString("no_laporan_produksi");
                row[2] = rs.getString("jenis_barang");
                row[3] = rs.getDate("tgl_treatment");
                row[4] = rs.getInt("waktu_treatment");
                row[5] = rs.getDouble("nitrit_awal");
                row[6] = rs.getDouble("nitrit_akhir");
                row[7] = rs.getString("status");
                model.addRow(row);
            }
            label_total_treatment.setText(Integer.toString(Table_data_treatment.getRowCount()));
            ColumnsAutoSizer.sizeColumnsToFit(Table_data_treatment);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Lab_LaporanProduksi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_Treatment_sela() {
        try {
            DefaultTableModel model = (DefaultTableModel) Table_data_treatment_sela.getModel();
            model.setRowCount(0);
            String sql_treatment = "SELECT `nomor`, `no_laporan_produksi`, `id_pegawai`, `tanggal_tr_sela`, `waktu` "
                    + "FROM `tb_treatment_sela` WHERE `no_laporan_produksi` = '" + label_no_lp1.getText() + "'";
            rs = Utility.db.getStatement().executeQuery(sql_treatment);
            Object[] row = new Object[8];
            while (rs.next()) {
                row[0] = rs.getString("nomor");
                row[1] = rs.getString("no_laporan_produksi");
                row[2] = rs.getDate("tanggal_tr_sela");
                row[3] = rs.getTime("waktu");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_data_treatment_sela);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Lab_LaporanProduksi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTabel_rekapLP_HOLD() {
        try {
            DefaultTableModel model = (DefaultTableModel) Table_Data_rekapLP_HOLD.getModel();
            model.setRowCount(0);
            String sql_treatment = null;
            if (Date_1.getDate() == null || Date_2.getDate() == null) {
                sql_treatment = "SELECT `tb_laporan_produksi`.`no_kartu_waleta`, COUNT(`tb_lab_laporan_produksi`.`no_laporan_produksi`) AS 'jumlah_lp', (SELECT COUNT(`tb_lab_laporan_produksi`.`no_laporan_produksi`) FROM `tb_lab_laporan_produksi` WHERE `status`='HOLD/NON GNS' AND `tb_lab_laporan_produksi`.`no_laporan_produksi` LIKE '%" + txt_search_No_LP.getText() + "%') AS 'total_lp' "
                        + "FROM `tb_lab_laporan_produksi` LEFT JOIN `tb_laporan_produksi` ON `tb_lab_laporan_produksi`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi` "
                        + "WHERE `status`='HOLD/NON GNS' AND `tb_lab_laporan_produksi`.`no_laporan_produksi` LIKE '%" + txt_search_No_LP.getText() + "%'"
                        + "GROUP BY `tb_laporan_produksi`.`no_kartu_waleta`";
            } else {
                sql_treatment = "SELECT `tb_laporan_produksi`.`no_kartu_waleta`, COUNT(`tb_lab_laporan_produksi`.`no_laporan_produksi`) AS 'jumlah_lp', (SELECT COUNT(`tb_lab_laporan_produksi`.`no_laporan_produksi`) FROM `tb_lab_laporan_produksi` WHERE `status`='HOLD/NON GNS'AND `tb_lab_laporan_produksi`.`no_laporan_produksi` LIKE '%" + txt_search_No_LP.getText() + "%' AND `tgl_uji` BETWEEN '" + dateFormat.format(Date_1.getDate()) + "' AND '" + dateFormat.format(Date_2.getDate()) + "') AS 'total_lp'  "
                        + "FROM `tb_lab_laporan_produksi` LEFT JOIN `tb_laporan_produksi` ON `tb_lab_laporan_produksi`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi` "
                        + "WHERE `status`='HOLD/NON GNS' AND `tb_lab_laporan_produksi`.`no_laporan_produksi` LIKE '%" + txt_search_No_LP.getText() + "%' AND `tgl_uji` BETWEEN '" + dateFormat.format(Date_1.getDate()) + "' AND '" + dateFormat.format(Date_2.getDate()) + "'"
                        + "GROUP BY `tb_laporan_produksi`.`no_kartu_waleta`";
            }
            rs = Utility.db.getStatement().executeQuery(sql_treatment);
            Object[] row = new Object[3];
            int total_lp = 0;
            float total_persen = 0;
            while (rs.next()) {
                float presentase = (rs.getFloat("jumlah_lp") / rs.getFloat("total_lp")) * 100;
                row[0] = rs.getString("no_kartu_waleta");
                row[1] = rs.getInt("jumlah_lp");
                row[2] = presentase;
                total_lp = total_lp + rs.getInt("jumlah_lp");
                total_persen = total_persen + presentase;
                model.addRow(row);
            }

            label_total_lp_rekap_hold.setText(Integer.toString(total_lp));
            label_total_presentase.setText(Float.toString(total_persen));
            ColumnsAutoSizer.sizeColumnsToFit(Table_Data_rekapLP_HOLD);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Lab_LaporanProduksi.class.getName()).log(Level.SEVERE, null, ex);
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
                            Query = "UPDATE `tb_lab_laporan_produksi` SET "
                                    + "`status_akhir`='PASSED'"
                                    + "WHERE `no_laporan_produksi`='" + value[0] + "'";
                            Utility.db.getConnection().prepareStatement(Query);
                            if ((Utility.db.getStatement().executeUpdate(Query)) > 0) {
                                System.out.println(value[0]);
                                n++;
                            } else {
                                System.out.println("gagal");
//                                System.out.println(Query);
//                                JOptionPane.showMessageDialog(this, "Failed insert : " + value[0]);
                            }
                        }
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(this, ex);
                        JOptionPane.showMessageDialog(this, "Data Berhasil Masuk : " + n);
                        Logger.getLogger(JPanel_Lab_LaporanProduksi.class.getName()).log(Level.SEVERE, null, ex);
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

    public void executeSQLQuery(String query, String message) {
        Utility.db.getConnection();
        try {
            Utility.db.getConnection().createStatement();
            if ((Utility.db.getStatement().executeUpdate(query)) == 1) {
                JOptionPane.showMessageDialog(this, "data " + message + " Successfully");
            } else {
                JOptionPane.showMessageDialog(this, "data not " + message);
            }
        } catch (SQLException | HeadlessException e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_Lab_LaporanProduksi.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public JPanel_Lab_LaporanProduksi() {
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

        jPanel1 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        Table_Data_Lab_LP = new javax.swing.JTable();
        jLabel8 = new javax.swing.JLabel();
        label_total_lp = new javax.swing.JLabel();
        txt_search_No_LP = new javax.swing.JTextField();
        button_search_lab_LP = new javax.swing.JButton();
        Date_1 = new com.toedter.calendar.JDateChooser();
        Date_2 = new com.toedter.calendar.JDateChooser();
        button_setor_lp = new javax.swing.JButton();
        button_edit_lp = new javax.swing.JButton();
        button_delete_lp = new javax.swing.JButton();
        button_export_dataLab = new javax.swing.JButton();
        button_input_data = new javax.swing.JButton();
        button_print = new javax.swing.JButton();
        label_total_passed = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        label_total_hold = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        label_total_belumUji = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        label_total_passed1 = new javax.swing.JLabel();
        label_total_passed2 = new javax.swing.JLabel();
        label_total_passed3 = new javax.swing.JLabel();
        label_persen_passed = new javax.swing.JLabel();
        label_persen_hold = new javax.swing.JLabel();
        label_persen_belumUji = new javax.swing.JLabel();
        label_total_passed5 = new javax.swing.JLabel();
        label_total_passed6 = new javax.swing.JLabel();
        label_total_passed7 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        label_total_non_ns = new javax.swing.JLabel();
        label_total_passed4 = new javax.swing.JLabel();
        label_persen_non_ns = new javax.swing.JLabel();
        label_total_passed8 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        Table_Data_rekapLP_HOLD = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        label_total_lp_rekap_hold = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        label_total_presentase = new javax.swing.JLabel();
        ComboBox_Filter_status_uji = new javax.swing.JComboBox<>();
        ComboBox_Filter_status_akhir = new javax.swing.JComboBox<>();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        button_catatan_pemeriksaan_baku = new javax.swing.JButton();
        CheckBox_utuh = new javax.swing.JCheckBox();
        CheckBox_flat = new javax.swing.JCheckBox();
        CheckBox_jdn = new javax.swing.JCheckBox();
        jLabel20 = new javax.swing.JLabel();
        button_print_label_qc1 = new javax.swing.JButton();
        button_print_label_qc_setor = new javax.swing.JButton();
        jLabel21 = new javax.swing.JLabel();
        ComboBox_status_print1 = new javax.swing.JComboBox<>();
        button_catatan_uji_nitrit_semi_bj = new javax.swing.JButton();
        Date1_masuk_baku_ct1 = new com.toedter.calendar.JDateChooser();
        Date2_masuk_baku_ct1 = new com.toedter.calendar.JDateChooser();
        ComboBox_filter_tgl = new javax.swing.JComboBox<>();
        button_cheat_rsb = new javax.swing.JButton();
        button_cheat_kartu = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        label_total_treatment = new javax.swing.JLabel();
        button_edit_treatment = new javax.swing.JButton();
        button_export_dataTreatment = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        Table_data_treatment = new javax.swing.JTable();
        button_insert_treatment = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        label_no_lp1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        Table_data_treatment_sela = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        label_no_lp = new javax.swing.JLabel();
        button_delete_treatment = new javax.swing.JButton();
        jLabel22 = new javax.swing.JLabel();
        txt_max_nitrit = new javax.swing.JTextField();
        ComboBox_search_tanggal = new javax.swing.JComboBox<>();
        jLabel23 = new javax.swing.JLabel();
        ComboBox_status_print2 = new javax.swing.JComboBox<>();
        button_input_keterangan = new javax.swing.JButton();
        button_input_sampling = new javax.swing.JButton();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        txt_search_No_Kartu = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        txt_search_Memo = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        txt_search_Ruangan = new javax.swing.JTextField();

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Data Pemeriksaan Lab Bahan Jadi", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N
        jPanel1.setPreferredSize(new java.awt.Dimension(1366, 700));

        Table_Data_Lab_LP.setAutoCreateRowSorter(true);
        Table_Data_Lab_LP.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_Data_Lab_LP.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No LP", "Grade", "kartu", "RSB", "Memo", "Treatment", "Tgl Masuk", "Tgl Sampling", "Pekerja Sampling", "Tgl Uji", "Tgl Selesai", "NO2 Baku", "NO2 Utuh", "NO2 Flat", "NO2 Jdn", "Moist", "Utuh", "Pecah", "Flat", "Kpg Akhir", "Gram Akhir", "Status", "Status Akhir", "Keterangan", "Print 1", "Print 2"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Integer.class, java.lang.Float.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Boolean.class, java.lang.Boolean.class
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
        Table_Data_Lab_LP.getTableHeader().setReorderingAllowed(false);
        jScrollPane5.setViewportView(Table_Data_Lab_LP);
        if (Table_Data_Lab_LP.getColumnModel().getColumnCount() > 0) {
            Table_Data_Lab_LP.getColumnModel().getColumn(4).setMaxWidth(100);
        }

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel8.setText("Total LP :");

        label_total_lp.setBackground(new java.awt.Color(255, 255, 255));
        label_total_lp.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        label_total_lp.setText("TOTAL");

        txt_search_No_LP.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_No_LP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_No_LPKeyPressed(evt);
            }
        });

        button_search_lab_LP.setBackground(new java.awt.Color(255, 255, 255));
        button_search_lab_LP.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search_lab_LP.setText("Search");
        button_search_lab_LP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_search_lab_LPActionPerformed(evt);
            }
        });

        Date_1.setBackground(new java.awt.Color(255, 255, 255));
        Date_1.setDate(new Date(new Date().getTime()- (7 * 24 * 60 * 60 * 1000)));
        Date_1.setDateFormatString("dd MMM yyyy");
        Date_1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Date_2.setBackground(new java.awt.Color(255, 255, 255));
        Date_2.setDate(new Date());
        Date_2.setDateFormatString("dd MMM yyyy");
        Date_2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        button_setor_lp.setBackground(new java.awt.Color(255, 255, 255));
        button_setor_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_setor_lp.setText("Setor LP");
        button_setor_lp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_setor_lpActionPerformed(evt);
            }
        });

        button_edit_lp.setBackground(new java.awt.Color(255, 255, 255));
        button_edit_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_edit_lp.setText("Edit");
        button_edit_lp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_edit_lpActionPerformed(evt);
            }
        });

        button_delete_lp.setBackground(new java.awt.Color(255, 255, 255));
        button_delete_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_delete_lp.setText("Delete");
        button_delete_lp.setEnabled(false);
        button_delete_lp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_delete_lpActionPerformed(evt);
            }
        });

        button_export_dataLab.setBackground(new java.awt.Color(255, 255, 255));
        button_export_dataLab.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_dataLab.setText("Export to Excel");
        button_export_dataLab.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_dataLabActionPerformed(evt);
            }
        });

        button_input_data.setBackground(new java.awt.Color(255, 255, 255));
        button_input_data.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_input_data.setText("Input Data Lab");
        button_input_data.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_input_dataActionPerformed(evt);
            }
        });

        button_print.setBackground(new java.awt.Color(255, 255, 255));
        button_print.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_print.setText("Pengujian Nitrit Setelah Rendam CCP1");
        button_print.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_printActionPerformed(evt);
            }
        });

        label_total_passed.setBackground(new java.awt.Color(255, 255, 255));
        label_total_passed.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        label_total_passed.setText("TOTAL");

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel9.setText("Total PASSED :");

        label_total_hold.setBackground(new java.awt.Color(255, 255, 255));
        label_total_hold.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        label_total_hold.setText("TOTAL");

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel15.setText("Total HOLD :");

        label_total_belumUji.setBackground(new java.awt.Color(255, 255, 255));
        label_total_belumUji.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        label_total_belumUji.setText("TOTAL");

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel16.setText("Total Belum Uji :");

        label_total_passed1.setBackground(new java.awt.Color(255, 255, 255));
        label_total_passed1.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        label_total_passed1.setText("LP");

        label_total_passed2.setBackground(new java.awt.Color(255, 255, 255));
        label_total_passed2.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        label_total_passed2.setText("LP");

        label_total_passed3.setBackground(new java.awt.Color(255, 255, 255));
        label_total_passed3.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        label_total_passed3.setText("LP");

        label_persen_passed.setBackground(new java.awt.Color(255, 255, 255));
        label_persen_passed.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        label_persen_passed.setText("TOTAL");

        label_persen_hold.setBackground(new java.awt.Color(255, 255, 255));
        label_persen_hold.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        label_persen_hold.setText("TOTAL");

        label_persen_belumUji.setBackground(new java.awt.Color(255, 255, 255));
        label_persen_belumUji.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        label_persen_belumUji.setText("TOTAL");

        label_total_passed5.setBackground(new java.awt.Color(255, 255, 255));
        label_total_passed5.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        label_total_passed5.setText("%");

        label_total_passed6.setBackground(new java.awt.Color(255, 255, 255));
        label_total_passed6.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        label_total_passed6.setText("%");

        label_total_passed7.setBackground(new java.awt.Color(255, 255, 255));
        label_total_passed7.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        label_total_passed7.setText("%");

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel17.setText("Total LP NON NS :");

        label_total_non_ns.setBackground(new java.awt.Color(255, 255, 255));
        label_total_non_ns.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        label_total_non_ns.setText("TOTAL");

        label_total_passed4.setBackground(new java.awt.Color(255, 255, 255));
        label_total_passed4.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        label_total_passed4.setText("LP");

        label_persen_non_ns.setBackground(new java.awt.Color(255, 255, 255));
        label_persen_non_ns.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        label_persen_non_ns.setText("TOTAL");

        label_total_passed8.setBackground(new java.awt.Color(255, 255, 255));
        label_total_passed8.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        label_total_passed8.setText("%");

        Table_Data_rekapLP_HOLD.setAutoCreateRowSorter(true);
        Table_Data_rekapLP_HOLD.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_Data_rekapLP_HOLD.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No Kartu", "Jumlah LP Hold", "Presentase (%)"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.Float.class
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
        Table_Data_rekapLP_HOLD.getTableHeader().setReorderingAllowed(false);
        jScrollPane6.setViewportView(Table_Data_rekapLP_HOLD);

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("Total LP :");

        label_total_lp_rekap_hold.setBackground(new java.awt.Color(255, 255, 255));
        label_total_lp_rekap_hold.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_lp_rekap_hold.setText("0");

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel5.setText("Total % :");

        label_total_presentase.setBackground(new java.awt.Color(255, 255, 255));
        label_total_presentase.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_presentase.setText("0");

        ComboBox_Filter_status_uji.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_Filter_status_uji.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "PASSED", "HOLD / NON NS" }));

        ComboBox_Filter_status_akhir.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_Filter_status_akhir.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "PASSED", "HOLD / NON NS" }));

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel18.setText("Status Awal :");

        jLabel19.setBackground(new java.awt.Color(255, 255, 255));
        jLabel19.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel19.setText("Status Akhir :");

        button_catatan_pemeriksaan_baku.setBackground(new java.awt.Color(255, 255, 255));
        button_catatan_pemeriksaan_baku.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_catatan_pemeriksaan_baku.setText("Catatan Pemeriksaan Baku");
        button_catatan_pemeriksaan_baku.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_catatan_pemeriksaan_bakuActionPerformed(evt);
            }
        });

        CheckBox_utuh.setBackground(new java.awt.Color(255, 255, 255));
        CheckBox_utuh.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        CheckBox_utuh.setText("Utuh");

        CheckBox_flat.setBackground(new java.awt.Color(255, 255, 255));
        CheckBox_flat.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        CheckBox_flat.setText("Flat");

        CheckBox_jdn.setBackground(new java.awt.Color(255, 255, 255));
        CheckBox_jdn.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        CheckBox_jdn.setText("Jidun");

        jLabel20.setBackground(new java.awt.Color(255, 255, 255));
        jLabel20.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel20.setText("Filter Bentuk :");

        button_print_label_qc1.setBackground(new java.awt.Color(255, 255, 255));
        button_print_label_qc1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_print_label_qc1.setText("Label QC uji 1");
        button_print_label_qc1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_print_label_qc1ActionPerformed(evt);
            }
        });

        button_print_label_qc_setor.setBackground(new java.awt.Color(255, 255, 255));
        button_print_label_qc_setor.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_print_label_qc_setor.setText("Label QC Setor");
        button_print_label_qc_setor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_print_label_qc_setorActionPerformed(evt);
            }
        });

        jLabel21.setBackground(new java.awt.Color(255, 255, 255));
        jLabel21.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel21.setText("Status print 1 :");

        ComboBox_status_print1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_status_print1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Sudah Print", "Belum Print" }));

        button_catatan_uji_nitrit_semi_bj.setBackground(new java.awt.Color(255, 255, 255));
        button_catatan_uji_nitrit_semi_bj.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_catatan_uji_nitrit_semi_bj.setText("Catatan uji nitrit semi BJ");
        button_catatan_uji_nitrit_semi_bj.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_catatan_uji_nitrit_semi_bjActionPerformed(evt);
            }
        });

        Date1_masuk_baku_ct1.setBackground(new java.awt.Color(255, 255, 255));
        Date1_masuk_baku_ct1.setDateFormatString("dd MMM yyyy");
        Date1_masuk_baku_ct1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Date2_masuk_baku_ct1.setBackground(new java.awt.Color(255, 255, 255));
        Date2_masuk_baku_ct1.setDateFormatString("dd MMM yyyy");
        Date2_masuk_baku_ct1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        ComboBox_filter_tgl.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "tgl masuk baku ct1", "tgl rendam" }));

        button_cheat_rsb.setBackground(new java.awt.Color(255, 255, 255));
        button_cheat_rsb.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_cheat_rsb.setText("Cheat RSB");
        button_cheat_rsb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_cheat_rsbActionPerformed(evt);
            }
        });

        button_cheat_kartu.setBackground(new java.awt.Color(255, 255, 255));
        button_cheat_kartu.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_cheat_kartu.setText("Cheat kartu");
        button_cheat_kartu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_cheat_kartuActionPerformed(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("DATA TREATMENT SELA");

        label_total_treatment.setText("0");

        button_edit_treatment.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_edit_treatment.setText("Edit");
        button_edit_treatment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_edit_treatmentActionPerformed(evt);
            }
        });

        button_export_dataTreatment.setBackground(new java.awt.Color(255, 255, 255));
        button_export_dataTreatment.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_dataTreatment.setText("Export to Excel");
        button_export_dataTreatment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_dataTreatmentActionPerformed(evt);
            }
        });

        Table_data_treatment.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode", "No. LP", "Jenis", "Tgl", "Menit", "NO2 Awal", "NO2 Akhir", "Status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Float.class, java.lang.Float.class, java.lang.String.class
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
        Table_data_treatment.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(Table_data_treatment);
        if (Table_data_treatment.getColumnModel().getColumnCount() > 0) {
            Table_data_treatment.getColumnModel().getColumn(2).setHeaderValue("Jenis");
            Table_data_treatment.getColumnModel().getColumn(5).setHeaderValue("NO2 Awal");
            Table_data_treatment.getColumnModel().getColumn(6).setHeaderValue("NO2 Akhir");
            Table_data_treatment.getColumnModel().getColumn(7).setHeaderValue("Status");
        }

        button_insert_treatment.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_insert_treatment.setText("insert");
        button_insert_treatment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_insert_treatmentActionPerformed(evt);
            }
        });

        jLabel2.setText("Total Treatment :");

        label_no_lp1.setBackground(new java.awt.Color(255, 255, 255));
        label_no_lp1.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_no_lp1.setForeground(new java.awt.Color(255, 0, 0));
        label_no_lp1.setText("LP");

        Table_data_treatment_sela.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode", "No. LP", "Tgl", "Waktu"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class
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
        Table_data_treatment_sela.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(Table_data_treatment_sela);

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("DATA TREATMENT");

        label_no_lp.setBackground(new java.awt.Color(255, 255, 255));
        label_no_lp.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_no_lp.setForeground(new java.awt.Color(255, 0, 0));
        label_no_lp.setText("LP");

        button_delete_treatment.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_delete_treatment.setText("Delete");
        button_delete_treatment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_delete_treatmentActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_no_lp)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_export_dataTreatment))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_treatment)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 67, Short.MAX_VALUE)
                        .addComponent(button_edit_treatment)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_insert_treatment)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_delete_treatment))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_no_lp1))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(label_no_lp)
                    .addComponent(jLabel1)
                    .addComponent(button_export_dataTreatment, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(label_total_treatment)
                    .addComponent(button_edit_treatment, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_insert_treatment, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_delete_treatment, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(label_no_lp1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel22.setBackground(new java.awt.Color(255, 255, 255));
        jLabel22.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel22.setText("Max Nitrit :");

        txt_max_nitrit.setEditable(false);
        txt_max_nitrit.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        txt_max_nitrit.setText("21");

        ComboBox_search_tanggal.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_search_tanggal.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tgl Masuk", "Tgl Sampling", "Tgl Uji", "Tgl Selesai" }));

        jLabel23.setBackground(new java.awt.Color(255, 255, 255));
        jLabel23.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel23.setText("Status print 2 :");

        ComboBox_status_print2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_status_print2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Sudah Print", "Belum Print" }));

        button_input_keterangan.setBackground(new java.awt.Color(255, 255, 255));
        button_input_keterangan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_input_keterangan.setText("Input Keterangan");
        button_input_keterangan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_input_keteranganActionPerformed(evt);
            }
        });

        button_input_sampling.setBackground(new java.awt.Color(255, 255, 255));
        button_input_sampling.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_input_sampling.setText("Input Sampling");
        button_input_sampling.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_input_samplingActionPerformed(evt);
            }
        });

        jLabel24.setBackground(new java.awt.Color(255, 255, 255));
        jLabel24.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel24.setText("No LP :");

        jLabel25.setBackground(new java.awt.Color(255, 255, 255));
        jLabel25.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel25.setText("No Kartu :");

        txt_search_No_Kartu.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_No_Kartu.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_No_KartuKeyPressed(evt);
            }
        });

        jLabel26.setBackground(new java.awt.Color(255, 255, 255));
        jLabel26.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel26.setText("Memo :");

        txt_search_Memo.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_Memo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_MemoKeyPressed(evt);
            }
        });

        jLabel27.setBackground(new java.awt.Color(255, 255, 255));
        jLabel27.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel27.setText("Ruangan :");

        txt_search_Ruangan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_Ruangan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_RuanganKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane5, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel9)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_passed)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_passed1)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(label_persen_passed)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_passed7))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel17)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_non_ns)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_passed4)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_persen_non_ns)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_passed8))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel15)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_hold)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_passed2)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_persen_hold)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_passed6))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel8)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_lp))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel16)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_belumUji)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_passed3)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_persen_belumUji)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_passed5)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel3)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_lp_rekap_hold)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel5)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_total_presentase))
                                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 343, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(10, 10, 10))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(button_catatan_uji_nitrit_semi_bj)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(button_catatan_pemeriksaan_baku)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(button_print)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(button_export_dataLab)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(button_print_label_qc1)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(button_print_label_qc_setor)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 5, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel18)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(ComboBox_Filter_status_uji, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel19)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(ComboBox_Filter_status_akhir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel21)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(ComboBox_status_print1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel23)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(ComboBox_status_print2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel20)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(CheckBox_utuh)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(CheckBox_flat)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(CheckBox_jdn)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLabel22)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txt_max_nitrit, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                        .addComponent(button_input_sampling)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(button_input_data)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(button_setor_lp)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(button_edit_lp)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(button_delete_lp)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(button_input_keterangan)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(button_cheat_rsb)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(button_cheat_kartu))))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(ComboBox_search_tanggal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_filter_tgl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date1_masuk_baku_ct1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date2_masuk_baku_ct1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel24)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_No_LP, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel25)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_No_Kartu, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel26)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_Memo, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel27)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_Ruangan, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_search_lab_LP)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 14, Short.MAX_VALUE)))
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_search_No_LP, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_No_Kartu, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_Memo, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(txt_search_Ruangan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_search_lab_LP, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(Date_1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date1_masuk_baku_ct1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date2_masuk_baku_ct1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_filter_tgl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_search_tanggal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ComboBox_Filter_status_akhir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_Filter_status_uji, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_status_print1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(CheckBox_utuh, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(CheckBox_flat, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(CheckBox_jdn, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_max_nitrit, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ComboBox_status_print2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(button_catatan_uji_nitrit_semi_bj, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_catatan_pemeriksaan_baku, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_print, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_export_dataLab, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(button_print_label_qc1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_print_label_qc_setor, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(button_input_data, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_setor_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_edit_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_delete_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_cheat_rsb, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_cheat_kartu, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_input_keterangan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_input_sampling, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 383, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_total_lp)
                            .addComponent(jLabel8))
                        .addGap(9, 9, 9)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_total_passed)
                            .addComponent(jLabel9)
                            .addComponent(label_total_passed1)
                            .addComponent(label_persen_passed)
                            .addComponent(label_total_passed7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_total_hold)
                            .addComponent(jLabel15)
                            .addComponent(label_total_passed2)
                            .addComponent(label_persen_hold)
                            .addComponent(label_total_passed6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_total_non_ns)
                            .addComponent(jLabel17)
                            .addComponent(label_total_passed4)
                            .addComponent(label_persen_non_ns)
                            .addComponent(label_total_passed8))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_total_belumUji)
                            .addComponent(jLabel16)
                            .addComponent(label_total_passed3)
                            .addComponent(label_persen_belumUji)
                            .addComponent(label_total_passed5))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(label_total_lp_rekap_hold)
                    .addComponent(jLabel5)
                    .addComponent(label_total_presentase))
                .addContainerGap())
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

    private void button_insert_treatmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_insert_treatmentActionPerformed
        int j = Table_Data_Lab_LP.getSelectedRow();
        if (j > -1) {
            JDialog_InsertEdit_TreatmentLP dialog = new JDialog_InsertEdit_TreatmentLP(new javax.swing.JFrame(), true, null, Table_Data_Lab_LP.getValueAt(j, 0).toString());
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            dialog.setEnabled(true);
            refreshTable_Treatment();
        } else {
            JOptionPane.showMessageDialog(this, "Silahkan Pilih No Laporan Produksi yang akan di lakukan proses Treatment");
        }
    }//GEN-LAST:event_button_insert_treatmentActionPerformed

    private void button_edit_treatmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_edit_treatmentActionPerformed
        // TODO add your handling code here:
        decimalFormat.setMaximumFractionDigits(2);
        int j = Table_data_treatment.getSelectedRow();
        try {
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih data yang mau diedit !");
            } else {
                JDialog_InsertEdit_TreatmentLP dialog = new JDialog_InsertEdit_TreatmentLP(new javax.swing.JFrame(), true, Table_data_treatment.getValueAt(j, 0).toString(), null);
                dialog.pack();
                dialog.setLocationRelativeTo(this);
                dialog.setVisible(true);
                dialog.setEnabled(true);
                refreshTable_Treatment();
            }
        } catch (HeadlessException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, e);
        }
    }//GEN-LAST:event_button_edit_treatmentActionPerformed

    private void button_delete_treatmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_delete_treatmentActionPerformed
        // TODO add your handling code here:
        try {
            int j = Table_data_treatment.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih data yang ingin di hapus !");
            } else {
                int dialogResult = JOptionPane.showConfirmDialog(this, "Yakin hapus data ini?", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    // delete code here
                    String Query = "DELETE FROM `tb_lab_treatment_lp` WHERE `kode_treatment` = '" + Table_data_treatment.getValueAt(j, 0) + "'";
                    executeSQLQuery(Query, "deleted !");
                }
                refreshTable_Treatment();
            }
        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(this, e);
        }
    }//GEN-LAST:event_button_delete_treatmentActionPerformed

    private void txt_search_No_LPKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_No_LPKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
            refreshTabel_rekapLP_HOLD();
        }
    }//GEN-LAST:event_txt_search_No_LPKeyPressed

    private void button_search_lab_LPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_lab_LPActionPerformed
        // TODO add your handling code here:
        refreshTable();
        refreshTabel_rekapLP_HOLD();
    }//GEN-LAST:event_button_search_lab_LPActionPerformed

    private void button_setor_lpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_setor_lpActionPerformed
        // TODO add your handling code here:
        int x = Table_Data_Lab_LP.getSelectedRow();
        if (x < 0) {
            JOptionPane.showMessageDialog(this, "Pilih salah satu data LP yang akan di setor !");
        } else {
            if (Table_Data_Lab_LP.getValueAt(x, 10) == null || "".equals(Table_Data_Lab_LP.getValueAt(x, 10).toString())) {
                String no_lp = Table_Data_Lab_LP.getValueAt(x, 0).toString();
                String status_awal = Table_Data_Lab_LP.getValueAt(x, 21).toString();
                JDialog_SetorEdit_DataLabLP dialog = new JDialog_SetorEdit_DataLabLP(new javax.swing.JFrame(), true, no_lp, "setor", Table_Data_Lab_LP.getValueAt(x, 6).toString(), status_awal);
                dialog.pack();
                dialog.setLocationRelativeTo(this);
                dialog.setVisible(true);
                dialog.setEnabled(true);
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, "No Laporan Produksi " + Table_Data_Lab_LP.getValueAt(x, 0).toString() + " sudah di setor");
            }
        }
    }//GEN-LAST:event_button_setor_lpActionPerformed

    private void button_edit_lpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_edit_lpActionPerformed
        // TODO add your handling code here:
        int x = Table_Data_Lab_LP.getSelectedRow();
        if (x < 0) {
            JOptionPane.showMessageDialog(this, "Pilih salah satu data LP yang akan di edit !");
        } else {
            String no_lp = Table_Data_Lab_LP.getValueAt(x, 0).toString();
            String status_awal = Table_Data_Lab_LP.getValueAt(x, 21).toString();
            JDialog_SetorEdit_DataLabLP dialog = new JDialog_SetorEdit_DataLabLP(new javax.swing.JFrame(), true, no_lp, "edit", Table_Data_Lab_LP.getValueAt(x, 6).toString(), status_awal);
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            dialog.setEnabled(true);
            refreshTable();
            refreshTabel_rekapLP_HOLD();
        }
    }//GEN-LAST:event_button_edit_lpActionPerformed

    private void button_delete_lpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_delete_lpActionPerformed
        // TODO add your handling code here:
        try {
            int j = Table_Data_Lab_LP.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Pilih satu data LP yang akan di hapus !");
            } else {
                int dialogResult = JOptionPane.showConfirmDialog(this, "Yakin hapus data ini?", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    // delete code here
                    String Query = "DELETE FROM `tb_lab_laporan_produksi` WHERE `no_laporan_produksi` = '" + Table_Data_Lab_LP.getValueAt(j, 0) + "'";
                    executeSQLQuery(Query, "deleted !");
                    refreshTable();
                    refreshTabel_rekapLP_HOLD();
                }
                refreshTable_Treatment();
            }
        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(this, e);
        }
    }//GEN-LAST:event_button_delete_lpActionPerformed

    private void button_export_dataLabActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_dataLabActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Table_Data_Lab_LP.getModel();
        ExportToExcel.writeToExcel(model, this);
    }//GEN-LAST:event_button_export_dataLabActionPerformed

    private void button_export_dataTreatmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_dataTreatmentActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Table_data_treatment.getModel();
        ExportToExcel.writeToExcel(model, this);
    }//GEN-LAST:event_button_export_dataTreatmentActionPerformed

    private void button_input_dataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_input_dataActionPerformed
        // TODO add your handling code here:
        int x = Table_Data_Lab_LP.getSelectedRow();
        if (x < 0) {
            JOptionPane.showMessageDialog(this, "Pilih salah satu data LP yang akan di edit !");
        } else {
            if (Table_Data_Lab_LP.getValueAt(x, 7) == null) {
                JOptionPane.showMessageDialog(this, "LP " + Table_Data_Lab_LP.getValueAt(x, 0).toString() + " belum input sampling!");
            } else if (Table_Data_Lab_LP.getValueAt(x, 9) != null) {
                JOptionPane.showMessageDialog(this, "Data Uji No LP " + Table_Data_Lab_LP.getValueAt(x, 0).toString() + " sudah diinput!");
            } else {
                String no_lp = Table_Data_Lab_LP.getValueAt(x, 0).toString();
                JDialog_SetorEdit_DataLabLP dialog = new JDialog_SetorEdit_DataLabLP(new javax.swing.JFrame(), true, no_lp, "input", Table_Data_Lab_LP.getValueAt(x, 6).toString(), null);
                dialog.pack();
                dialog.setLocationRelativeTo(this);
                dialog.setVisible(true);
                dialog.setEnabled(true);
                refreshTable();
                refreshTabel_rekapLP_HOLD();
            }
        }
    }//GEN-LAST:event_button_input_dataActionPerformed

    private void button_printActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_printActionPerformed
        // TODO add your handling code here:
        try {
            String no_lp = "";
            for (int i = 0; i < Table_Data_Lab_LP.getRowCount(); i++) {
                if (i != 0) {
                    no_lp = no_lp + ", ";
                }
                no_lp = no_lp + "'" + Table_Data_Lab_LP.getValueAt(i, 0).toString() + "'";
            }
            sql = "SELECT `tb_lab_laporan_produksi`.`no_laporan_produksi`, `cheat_rsb`, `cheat_no_kartu`, `tb_laporan_produksi`.`kode_grade`, `no_registrasi`, `tb_lab_laporan_produksi`.`tgl_masuk`, `tgl_uji`, `tanggal_rendam`, `tgl_selesai`, "
                    + "`tb_lab_bahan_baku`.`nitrit_bm_w3`, `nitrit_utuh`, `nitrit_flat`, `jidun`, GREATEST(`nitrit_utuh`,`jidun`, `nitrit_flat`) AS 'nitrit', `kadar_air_bahan_jadi`, `status`, `utuh`, `pecah`, `flat`, `kpg_akhir`, `gram_akhir`\n"
                    + "FROM `tb_lab_laporan_produksi` "
                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_lab_laporan_produksi`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi` "
                    + "LEFT JOIN `tb_rendam` ON `tb_laporan_produksi`.`no_laporan_produksi` = `tb_rendam`.`no_laporan_produksi` "
                    + "LEFT JOIN `tb_bahan_baku_masuk_cheat` ON `tb_laporan_produksi`.`no_kartu_waleta` = `tb_bahan_baku_masuk_cheat`.`no_kartu_waleta` "
                    + "LEFT JOIN `tb_lab_bahan_baku` ON `tb_laporan_produksi`.`no_kartu_waleta` = `tb_lab_bahan_baku`.`no_kartu_waleta`\n"
                    + "WHERE `tb_lab_laporan_produksi`.`no_laporan_produksi` IN (" + no_lp + ") AND `tgl_uji` IS NOT NULL "
                    + "ORDER BY `tb_lab_laporan_produksi`.`no_laporan_produksi` ASC";
            JRDesignQuery newQuery = new JRDesignQuery();
            newQuery.setText(sql);
            JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Sodium_Nitrit_Catatan_Pengujian_Grading_Bahan_Jadi.jrxml");
            JASP_DESIGN.setQuery(newQuery);

            JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
            JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, null, Utility.db.getConnection());
            JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
        } catch (JRException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Lab_LaporanProduksi.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_button_printActionPerformed

    private void button_catatan_pemeriksaan_bakuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_catatan_pemeriksaan_bakuActionPerformed
        // TODO add your handling code here:
        try {
            String filter_tanggal_baku_masuk_ct1 = "";
            if (Date1_masuk_baku_ct1.getDate() != null && Date2_masuk_baku_ct1.getDate() != null) {
                if (ComboBox_filter_tgl.getSelectedIndex() == 0) {
                    filter_tanggal_baku_masuk_ct1 = " AND CT2.`tgl_masuk` BETWEEN '" + dateFormat.format(Date1_masuk_baku_ct1.getDate()) + "' AND '" + dateFormat.format(Date2_masuk_baku_ct1.getDate()) + "'";
                }
            }
            String no_lp = "";
            for (int i = 0; i < Table_Data_Lab_LP.getRowCount(); i++) {
                if (i != 0) {
                    no_lp = no_lp + ", ";
                }
                no_lp = no_lp + "'" + Table_Data_Lab_LP.getValueAt(i, 0).toString() + "'";
            }
            String query = "SELECT LP.`no_laporan_produksi`, CT2.`tgl_masuk`, "
                    + "IF(LP.`cheat_no_kartu` IS NULL, LP.`no_kartu_waleta`, LP.`cheat_no_kartu`) AS 'no_kartu',\n"
                    + "IF(LP.`cheat_rsb` IS NULL, CT2.`no_registrasi`, LP.`cheat_rsb`) AS 'kode_rsb',\n"
                    + "IF(CT2.`kadar_air_bahan_baku` = 0, `tb_bahan_baku_masuk`.`kadar_air_bahan_baku`, CT2.`kadar_air_bahan_baku`) AS 'KA_CT2',\n"
                    + "IF(LP.`cheat_no_kartu` IS NULL, `tb_lab_bahan_baku`.`nitrit_bm_w3`, NITRIT_BM_CT.`nitrit_bm_w3`) AS 'nitrit_bm_ct2',\n"
                    + "IF(IFNULL(`nitrit_utuh`, 0) > 21, (SELECT MIN(`nitrit_akhir`) AS 'nitrit' FROM `tb_lab_treatment_lp` WHERE `no_laporan_produksi` = LP.`no_laporan_produksi` AND `jenis_barang` = 'Utuh'), IFNULL(`nitrit_utuh`, 0)) AS 'nitrit_utuh',\n"
                    + "IF(IFNULL(`nitrit_flat`, 0) > 21, (SELECT MIN(`nitrit_akhir`) AS 'nitrit' FROM `tb_lab_treatment_lp` WHERE `no_laporan_produksi` = LP.`no_laporan_produksi` AND `jenis_barang` = 'Flat'), IFNULL(`nitrit_flat`, 0)) AS 'nitrit_flat',\n"
                    + "IF(IFNULL(`jidun`, 0) > 21, (SELECT MIN(`nitrit_akhir`) AS 'nitrit' FROM `tb_lab_treatment_lp` WHERE `no_laporan_produksi` = LP.`no_laporan_produksi` AND `jenis_barang` = 'Jidun'), IFNULL(`jidun`, 0)) AS 'jidun',\n"
                    + "ROUND((RAND() * (17-8))+8) AS 'random'\n"
                    + "FROM `tb_laporan_produksi` LP\n"
                    + "LEFT JOIN `tb_bahan_baku_masuk` ON LP.`no_kartu_waleta` = `tb_bahan_baku_masuk`.`no_kartu_waleta`\n"
                    + "LEFT JOIN `tb_bahan_baku_masuk_cheat` CT2 ON LP.`no_kartu_waleta` = CT2.`no_kartu_waleta`\n"
                    + "LEFT JOIN `tb_lab_bahan_baku` ON LP.`no_kartu_waleta` = `tb_lab_bahan_baku`.`no_kartu_waleta`\n"
                    + "LEFT JOIN `tb_lab_bahan_baku` NITRIT_BM_CT ON LP.`cheat_no_kartu` = NITRIT_BM_CT.`no_kartu_waleta`\n"
                    + "LEFT JOIN `tb_lab_laporan_produksi` ON LP.`no_laporan_produksi` = `tb_lab_laporan_produksi`.`no_laporan_produksi`\n"
                    + "WHERE LP.`no_laporan_produksi` IN (" + no_lp + ") "
                    + filter_tanggal_baku_masuk_ct1
                    + "AND `tb_lab_laporan_produksi`.`tgl_selesai` IS NOT NULL";
            System.out.println(query);
            JRDesignQuery newQuery = new JRDesignQuery();
            newQuery.setText(query);
            JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Catatan_Pemeriksaan_Bahan_Baku_Selama_Proses.jrxml");
            JASP_DESIGN.setQuery(newQuery);
            Map<String, Object> map = new HashMap<>();
            JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
            JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, map, Utility.db.getConnection());
            JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
        } catch (JRException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Lab_LaporanProduksi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_catatan_pemeriksaan_bakuActionPerformed

    private void button_print_label_qc1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_print_label_qc1ActionPerformed
        // TODO add your handling code here:
        try {
            String no_lp = "";
            for (int i = 0; i < Table_Data_Lab_LP.getRowCount(); i++) {
                if (i != 0) {
                    no_lp = no_lp + ", ";
                }
                no_lp = no_lp + "'" + Table_Data_Lab_LP.getValueAt(i, 0).toString() + "'";
            }
            String Query = "SELECT CONCAT(A.`no_laporan_produksi`, IF(`cheat_rsb` IS NULL, CONCAT('-', `no_registrasi`), CONCAT('-', `cheat_rsb`))) AS 'no_lp_rsb', A.`no_laporan_produksi`, `tgl_uji` AS 'tanggal', `status`, "
                    + "CONCAT(IF(`nitrit_utuh`>0, CONCAT('Utuh:',`nitrit_utuh`), ''), IF(`nitrit_flat`>0, CONCAT(' Flat:',`nitrit_flat`), ''), IF(`jidun`>0, CONCAT(' Jdn:',`jidun`), '')) AS 'nitrit' \n"
                    + "FROM `tb_lab_laporan_produksi` A "
                    + "LEFT JOIN `tb_laporan_produksi` ON A.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi` "
                    + "LEFT JOIN `tb_bahan_baku_masuk_cheat` ON `tb_laporan_produksi`.`no_kartu_waleta` = `tb_bahan_baku_masuk_cheat`.`no_kartu_waleta`"
                    + "WHERE `tgl_uji` IS NOT NULL "
                    + "AND A.`no_laporan_produksi` IN (" + no_lp + ")";
//                    + "AND `print_label` = 0 ";
            JRDesignQuery newQuery = new JRDesignQuery();
            newQuery.setText(Query);
            JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Label_QC_Passed.jrxml");
            JASP_DESIGN.setQuery(newQuery);
            Map<String, Object> map = new HashMap<>();
            JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
            JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, map, Utility.db.getConnection());
            JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)

            String Query_update = "UPDATE `tb_lab_laporan_produksi` SET `print_label`=1 WHERE `print_label` = 0 AND `tgl_uji` IS NOT NULL AND `no_laporan_produksi` IN (" + no_lp + ")";
            Utility.db.getStatement().executeUpdate(Query_update);
            refreshTable();
            refreshTabel_rekapLP_HOLD();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Lab_LaporanProduksi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_print_label_qc1ActionPerformed

    private void button_print_label_qc_setorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_print_label_qc_setorActionPerformed
        // TODO add your handling code here:
        try {
            String no_lp = "";
            for (int i = 0; i < Table_Data_Lab_LP.getRowCount(); i++) {
                if (i != 0) {
                    no_lp = no_lp + ", ";
                }
                no_lp = no_lp + "'" + Table_Data_Lab_LP.getValueAt(i, 0).toString() + "'";
            }
            int max_nitrit = Integer.valueOf(txt_max_nitrit.getText());
            String Query = "SELECT CONCAT(A.`no_laporan_produksi`, IF(`cheat_rsb` IS NULL, CONCAT('-', `no_registrasi`), CONCAT('-', `cheat_rsb`))) AS 'no_lp_rsb', A.`no_laporan_produksi`, `tgl_selesai` AS 'tanggal', `status_akhir` AS 'status', "
                    + "CONCAT("
                    + "IF(`nitrit_utuh`>0, IF(`nitrit_utuh`>" + max_nitrit + ",CONCAT('Utuh(T):',ROUND((SELECT `nitrit_akhir` FROM `tb_lab_treatment_lp` WHERE `no_laporan_produksi` = A.`no_laporan_produksi` AND `jenis_barang` = 'Utuh' ORDER BY `nitrit_akhir` LIMIT 1), 1)),CONCAT('Utuh:',`nitrit_utuh`)), ''), "
                    + "IF(`nitrit_flat`>0, IF(`nitrit_flat`>" + max_nitrit + ",CONCAT(' Flat(T):',ROUND((SELECT `nitrit_akhir` FROM `tb_lab_treatment_lp` WHERE `no_laporan_produksi` = A.`no_laporan_produksi` AND `jenis_barang` = 'Flat' ORDER BY `nitrit_akhir` LIMIT 1), 1)),CONCAT(' Flat:',`nitrit_flat`)), ''), "
                    + "IF(`jidun`>0, IF(`jidun`>" + max_nitrit + ",CONCAT(' Jdn(T):',ROUND((SELECT `nitrit_akhir` FROM `tb_lab_treatment_lp` WHERE `no_laporan_produksi` = A.`no_laporan_produksi` AND `jenis_barang` = 'Jidun' ORDER BY `nitrit_akhir` LIMIT 1), 1)),CONCAT(' Jdn:',`jidun`)), '')"
                    + ") AS 'nitrit' \n"
                    + "FROM `tb_lab_laporan_produksi` A "
                    + "LEFT JOIN `tb_laporan_produksi` ON A.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi` "
                    + "LEFT JOIN `tb_bahan_baku_masuk_cheat` ON `tb_laporan_produksi`.`no_kartu_waleta` = `tb_bahan_baku_masuk_cheat`.`no_kartu_waleta`"
                    + "WHERE `tgl_selesai` IS NOT NULL "
                    + "AND A.`no_laporan_produksi` IN (" + no_lp + ")";
//                    + "AND `print_label` = 0 ";
            JRDesignQuery newQuery = new JRDesignQuery();
            newQuery.setText(Query);
            JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Label_QC_Passed.jrxml");
            JASP_DESIGN.setQuery(newQuery);
            Map<String, Object> map = new HashMap<>();
            JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
            JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, map, Utility.db.getConnection());
            JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)

            String Query_update = "UPDATE `tb_lab_laporan_produksi` SET `print_label_setor`=1 WHERE `print_label_setor` = 0 AND `tgl_selesai` IS NOT NULL AND `no_laporan_produksi` IN (" + no_lp + ")";
            Utility.db.getStatement().executeUpdate(Query_update);
            refreshTable();
            refreshTabel_rekapLP_HOLD();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Lab_LaporanProduksi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_print_label_qc_setorActionPerformed

    private void button_catatan_uji_nitrit_semi_bjActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_catatan_uji_nitrit_semi_bjActionPerformed
        // TODO add your handling code here:
        try {
            if (Date_1.getDate() == null || Date_2.getDate() == null) {
                sql = "SELECT `tb_lab_laporan_produksi`.`no_laporan_produksi`, `cheat_no_kartu`, `cheat_rsb`, `tb_laporan_produksi`.`kode_grade`, `no_registrasi`, `tb_lab_laporan_produksi`.`tgl_masuk`, `tgl_uji`, `tgl_selesai`, "
                        + "`tb_lab_bahan_baku`.`nitrit_bm_w3`, `nitrit_utuh`, `nitrit_flat`, `jidun`, GREATEST(`nitrit_utuh`,`jidun`, `nitrit_flat`) AS 'nitrit', `kadar_air_bahan_jadi`, `status`, `utuh`, `pecah`, `flat`, `kpg_akhir`, `gram_akhir`\n"
                        + "FROM `tb_lab_laporan_produksi` "
                        + "LEFT JOIN `tb_laporan_produksi` ON `tb_lab_laporan_produksi`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi` "
                        + "LEFT JOIN `tb_bahan_baku_masuk_cheat` ON `tb_laporan_produksi`.`no_kartu_waleta` = `tb_bahan_baku_masuk_cheat`.`no_kartu_waleta` "
                        + "LEFT JOIN `tb_lab_bahan_baku` ON `tb_laporan_produksi`.`no_kartu_waleta` = `tb_lab_bahan_baku`.`no_kartu_waleta`\n"
                        + "WHERE `tb_lab_laporan_produksi`.`no_laporan_produksi` LIKE '%" + txt_search_No_LP.getText() + "%' "
                        + "ORDER BY `tgl_uji` ASC";
            } else {
                sql = "SELECT `tb_lab_laporan_produksi`.`no_laporan_produksi`, `cheat_no_kartu`, `cheat_rsb`, `tb_laporan_produksi`.`kode_grade`, `no_registrasi`, `tb_lab_laporan_produksi`.`tgl_masuk`, `tgl_uji`, `tgl_selesai`, "
                        + "`tb_lab_bahan_baku`.`nitrit_bm_w3`, `nitrit_utuh`, `nitrit_flat`, `jidun`, GREATEST(`nitrit_utuh`,`jidun`, `nitrit_flat`) AS 'nitrit', `kadar_air_bahan_jadi`, `status`, `utuh`, `pecah`, `flat`, `kpg_akhir`, `gram_akhir`\n"
                        + "FROM `tb_lab_laporan_produksi` "
                        + "LEFT JOIN `tb_laporan_produksi` ON `tb_lab_laporan_produksi`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi` "
                        + "LEFT JOIN `tb_bahan_baku_masuk_cheat` ON `tb_laporan_produksi`.`no_kartu_waleta` = `tb_bahan_baku_masuk_cheat`.`no_kartu_waleta` "
                        + "LEFT JOIN `tb_lab_bahan_baku` ON `tb_laporan_produksi`.`no_kartu_waleta` = `tb_lab_bahan_baku`.`no_kartu_waleta`\n"
                        + "WHERE `tb_lab_laporan_produksi`.`no_laporan_produksi` LIKE '%" + txt_search_No_LP.getText() + "%' AND `tgl_uji` BETWEEN '" + dateFormat.format(Date_1.getDate()) + "' AND '" + dateFormat.format(Date_2.getDate()) + "'"
                        + "ORDER BY `tgl_uji` ASC";
            }
            JRDesignQuery newQuery = new JRDesignQuery();
            newQuery.setText(sql);
            JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Catatan_Pengujian_Sodium_Nitrit_Semi_Barang_Jadi.jrxml");
            JASP_DESIGN.setQuery(newQuery);

            JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
            JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, null, Utility.db.getConnection());
            JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
        } catch (JRException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Lab_LaporanProduksi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_catatan_uji_nitrit_semi_bjActionPerformed

    private void button_cheat_rsbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_cheat_rsbActionPerformed
        // TODO add your handling code here:
        String input = JOptionPane.showInputDialog("Masukkan No Registrasi Rumah Burung :");
        if (input == null) {

        } else if (input.equals("")) {
            int dialogResult = JOptionPane.showConfirmDialog(this, "Hapus data cheat rsb??", "Warning", 0);
            if (dialogResult == JOptionPane.YES_OPTION) {
                for (int i = 0; i < Table_Data_Lab_LP.getRowCount(); i++) {
                    try {
                        sql = "UPDATE `tb_laporan_produksi` SET `cheat_rsb` = NULL "
                                + "WHERE `no_laporan_produksi` = '" + Table_Data_Lab_LP.getValueAt(i, 0).toString() + "'";
                        Utility.db.getStatement().executeUpdate(sql);
                    } catch (SQLException e) {
                        JOptionPane.showMessageDialog(this, e);
                        Logger.getLogger(JPanel_Lab_LaporanProduksi.class.getName()).log(Level.SEVERE, null, e);
                    }
                }
            }
        } else {
            try {
                String query = "SELECT `kode_rb`, `no_registrasi`, `nama_rumah_burung`, `kapasitas_per_tahun` "
                        + "FROM `tb_rumah_burung` WHERE `no_registrasi` = '" + input + "' "
                        + "AND CHAR_LENGTH(`no_registrasi`) IN (3, 4)";
                ResultSet rs = Utility.db.getStatement().executeQuery(query);
                if (rs.next()) {
                    for (int i = 0; i < Table_Data_Lab_LP.getRowCount(); i++) {
                        try {
                            sql = "UPDATE `tb_laporan_produksi` SET `cheat_rsb` = '" + input + "' WHERE `no_laporan_produksi` = '" + Table_Data_Lab_LP.getValueAt(i, 0).toString() + "'";
                            Utility.db.getStatement().executeUpdate(sql);
                        } catch (SQLException e) {
                            JOptionPane.showMessageDialog(this, e);
                            Logger.getLogger(JPanel_Lab_LaporanProduksi.class.getName()).log(Level.SEVERE, null, e);
                        }
                    }
                    JOptionPane.showMessageDialog(this, "data Saved");
                } else {
                    JOptionPane.showMessageDialog(this, "Maaf Rumah burung belum terdaftar / teregistrasi !");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, ex);
                Logger.getLogger(JPanel_Lab_LaporanProduksi.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        refreshTable();
    }//GEN-LAST:event_button_cheat_rsbActionPerformed

    private void button_cheat_kartuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_cheat_kartuActionPerformed
        // TODO add your handling code here:
        String input = JOptionPane.showInputDialog("No Kartu :");
        if (input == null) {

        } else if (input.equals("")) {
            int dialogResult = JOptionPane.showConfirmDialog(this, "Hapus data cheat??", "Warning", 0);
            if (dialogResult == JOptionPane.YES_OPTION) {
                for (int i = 0; i < Table_Data_Lab_LP.getRowCount(); i++) {
                    try {
                        sql = "UPDATE `tb_laporan_produksi` SET `cheat_no_kartu` = NULL "
                                + "WHERE `no_laporan_produksi` = '" + Table_Data_Lab_LP.getValueAt(i, 0).toString() + "'";
                        Utility.db.getStatement().executeUpdate(sql);
                    } catch (SQLException e) {
                        JOptionPane.showMessageDialog(this, e);
                        Logger.getLogger(JPanel_Lab_LaporanProduksi.class.getName()).log(Level.SEVERE, null, e);
                    }
                }
            }
        } else {
            try {
                String query = "SELECT `no_kartu_waleta` FROM `tb_bahan_baku_masuk` WHERE `no_kartu_waleta` = '" + input + "' ";
                ResultSet rs = Utility.db.getStatement().executeQuery(query);
                if (rs.next()) {
                    for (int i = 0; i < Table_Data_Lab_LP.getRowCount(); i++) {
                        try {
                            sql = "UPDATE `tb_laporan_produksi` SET `cheat_no_kartu` = '" + input + "' "
                                    + "WHERE `no_laporan_produksi` = '" + Table_Data_Lab_LP.getValueAt(i, 0).toString() + "'";
                            Utility.db.getStatement().executeUpdate(sql);
                        } catch (SQLException e) {
                            JOptionPane.showMessageDialog(this, e);
                            Logger.getLogger(JPanel_Lab_LaporanProduksi.class.getName()).log(Level.SEVERE, null, e);
                        }
                    }
                    JOptionPane.showMessageDialog(this, "data Saved");
                } else {
                    JOptionPane.showMessageDialog(this, "Maaf No Kartu salah, tidak ada di bahan baku masuk !");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, ex);
                Logger.getLogger(JPanel_Lab_LaporanProduksi.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        refreshTable();
    }//GEN-LAST:event_button_cheat_kartuActionPerformed

    private void button_input_keteranganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_input_keteranganActionPerformed
        // TODO add your handling code here:
        int x = Table_Data_Lab_LP.getSelectedRow();
        if (x < 0) {
            JOptionPane.showMessageDialog(this, "Pilih salah satu data LP yang akan di edit !");
        } else {
            String old_keterangan = Table_Data_Lab_LP.getValueAt(x, 23) == null ? "" : Table_Data_Lab_LP.getValueAt(x, 23).toString();
            String keterangan = JOptionPane.showInputDialog("Keterangan :", old_keterangan);
            if (keterangan != null) {
                try {
                    sql = "UPDATE `tb_lab_laporan_produksi` SET `keterangan` = '" + keterangan + "' WHERE `no_laporan_produksi` = '" + Table_Data_Lab_LP.getValueAt(x, 0).toString() + "'";
                    if (Utility.db.getStatement().executeUpdate(sql) == 1) {
                        refreshTable();
                    }
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, e);
                    Logger.getLogger(JPanel_Lab_LaporanProduksi.class.getName()).log(Level.SEVERE, null, e);
                }
            }
        }
    }//GEN-LAST:event_button_input_keteranganActionPerformed

    private void button_input_samplingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_input_samplingActionPerformed
        // TODO add your handling code here:
        int x = Table_Data_Lab_LP.getSelectedRow();
        if (x < 0) {
            JOptionPane.showMessageDialog(this, "Pilih salah satu data LP yang akan di edit !");
        } else {
            String no_lp = Table_Data_Lab_LP.getValueAt(x, 0).toString();
            JDialog_DataLabLP_InputSampling dialog = new JDialog_DataLabLP_InputSampling(new javax.swing.JFrame(), true, no_lp);
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            dialog.setEnabled(true);
            refreshTable();
        }
    }//GEN-LAST:event_button_input_samplingActionPerformed

    private void txt_search_No_KartuKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_No_KartuKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
            refreshTabel_rekapLP_HOLD();
        }
    }//GEN-LAST:event_txt_search_No_KartuKeyPressed

    private void txt_search_MemoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_MemoKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
            refreshTabel_rekapLP_HOLD();
        }
    }//GEN-LAST:event_txt_search_MemoKeyPressed

    private void txt_search_RuanganKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_RuanganKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_search_RuanganKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox CheckBox_flat;
    private javax.swing.JCheckBox CheckBox_jdn;
    private javax.swing.JCheckBox CheckBox_utuh;
    private javax.swing.JComboBox<String> ComboBox_Filter_status_akhir;
    private javax.swing.JComboBox<String> ComboBox_Filter_status_uji;
    private javax.swing.JComboBox<String> ComboBox_filter_tgl;
    private javax.swing.JComboBox<String> ComboBox_search_tanggal;
    private javax.swing.JComboBox<String> ComboBox_status_print1;
    private javax.swing.JComboBox<String> ComboBox_status_print2;
    private com.toedter.calendar.JDateChooser Date1_masuk_baku_ct1;
    private com.toedter.calendar.JDateChooser Date2_masuk_baku_ct1;
    private com.toedter.calendar.JDateChooser Date_1;
    private com.toedter.calendar.JDateChooser Date_2;
    public static javax.swing.JTable Table_Data_Lab_LP;
    public static javax.swing.JTable Table_Data_rekapLP_HOLD;
    private javax.swing.JTable Table_data_treatment;
    private javax.swing.JTable Table_data_treatment_sela;
    private javax.swing.JButton button_catatan_pemeriksaan_baku;
    private javax.swing.JButton button_catatan_uji_nitrit_semi_bj;
    private javax.swing.JButton button_cheat_kartu;
    private javax.swing.JButton button_cheat_rsb;
    private javax.swing.JButton button_delete_lp;
    public static javax.swing.JButton button_delete_treatment;
    private javax.swing.JButton button_edit_lp;
    public static javax.swing.JButton button_edit_treatment;
    public static javax.swing.JButton button_export_dataLab;
    public static javax.swing.JButton button_export_dataTreatment;
    private javax.swing.JButton button_input_data;
    private javax.swing.JButton button_input_keterangan;
    private javax.swing.JButton button_input_sampling;
    public static javax.swing.JButton button_insert_treatment;
    private javax.swing.JButton button_print;
    private javax.swing.JButton button_print_label_qc1;
    private javax.swing.JButton button_print_label_qc_setor;
    public static javax.swing.JButton button_search_lab_LP;
    private javax.swing.JButton button_setor_lp;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JLabel label_no_lp;
    private javax.swing.JLabel label_no_lp1;
    private javax.swing.JLabel label_persen_belumUji;
    private javax.swing.JLabel label_persen_hold;
    private javax.swing.JLabel label_persen_non_ns;
    private javax.swing.JLabel label_persen_passed;
    private javax.swing.JLabel label_total_belumUji;
    private javax.swing.JLabel label_total_hold;
    private javax.swing.JLabel label_total_lp;
    private javax.swing.JLabel label_total_lp_rekap_hold;
    private javax.swing.JLabel label_total_non_ns;
    private javax.swing.JLabel label_total_passed;
    private javax.swing.JLabel label_total_passed1;
    private javax.swing.JLabel label_total_passed2;
    private javax.swing.JLabel label_total_passed3;
    private javax.swing.JLabel label_total_passed4;
    private javax.swing.JLabel label_total_passed5;
    private javax.swing.JLabel label_total_passed6;
    private javax.swing.JLabel label_total_passed7;
    private javax.swing.JLabel label_total_passed8;
    private javax.swing.JLabel label_total_presentase;
    private javax.swing.JLabel label_total_treatment;
    private javax.swing.JTextField txt_max_nitrit;
    private javax.swing.JTextField txt_search_Memo;
    private javax.swing.JTextField txt_search_No_Kartu;
    private javax.swing.JTextField txt_search_No_LP;
    private javax.swing.JTextField txt_search_Ruangan;
    // End of variables declaration//GEN-END:variables
}
