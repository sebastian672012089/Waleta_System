package waleta_system;

import java.awt.HeadlessException;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;

public class JPanel_Traceability2 extends javax.swing.JPanel {

    String sql = null;
//    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();

    public JPanel_Traceability2() {
        initComponents();
//        DateFilter2.setDate(date);
//        DateFilter1.setDate(new Date(date.getTime() - 1000 * 60 * 60 * 24 * 30L));
    }

    public void init() {
        try {

            ComboBox_grade1.removeAllItems();
            ComboBox_grade1.addItem("All");
            ComboBox_grade2.removeAllItems();
            ComboBox_grade2.addItem("All");
            String grade = "SELECT `kode_grade` FROM `tb_grade_bahan_jadi` WHERE `status_grade` = 'AKTIF'";
            ResultSet grade_result = Utility.db.getStatement().executeQuery(grade);
            while (grade_result.next()) {
                ComboBox_grade1.addItem(grade_result.getString("kode_grade"));
                ComboBox_grade2.addItem(grade_result.getString("kode_grade"));
            }
            AutoCompleteDecorator.decorate(ComboBox_grade1);
            AutoCompleteDecorator.decorate(ComboBox_grade2);

            Table_data_LP.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent event) {
                    if (!event.getValueIsAdjusting() && Table_data_LP.getSelectedRow() != -1) {
                        int x = Table_data_LP.getSelectedRow();
                        if (x > -1) {
                            refreshTable_dataLP_hasilGrading(Table_data_LP.getValueAt(x, 1).toString());
                        }
                    }
                }
            });

        } catch (Exception ex) {
            Logger.getLogger(JPanel_Traceability2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_dataLP() {
        try {
            DefaultTableModel model = (DefaultTableModel) Table_data_LP.getModel();
            model.setRowCount(0);
            sql = "SELECT `no_laporan_produksi`, `kode_rsb`, `no_kartu_waleta`, `kode_grade`, `memo_lp_cheat`, `tanggal_lp`, `jumlah_keping`, `berat_basah`, `tgl_grading`, `kode_tutupan` FROM `tb_laporan_produksi_tv2`"
                    + "WHERE `tb_laporan_produksi_tv2`.`no_laporan_produksi` LIKE '%" + txt_search_lp_dataLP.getText() + "%' "
                    + "AND `tb_laporan_produksi_tv2`.`kode_rsb` LIKE '%" + txt_search_kodeRSB_dataLP.getText() + "%' "
                    + "AND `tb_laporan_produksi_tv2`.`no_kartu_waleta` LIKE '%" + txt_search_no_kartu_dataLP.getText() + "%' "
                    + "AND `tb_laporan_produksi_tv2`.`kode_tutupan` LIKE '%" + txt_search_tutupan_dataLP.getText() + "%' ";
            PreparedStatement pst = Utility.db.getConnection().prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            int no = 0;
            Object[] row = new Object[30];
            while (rs.next()) {
                no++;
                row[0] = no;
                row[1] = rs.getString("no_laporan_produksi");
                row[2] = rs.getString("kode_rsb");
                row[3] = rs.getString("no_kartu_waleta");
                row[4] = rs.getString("kode_grade");
                row[5] = rs.getString("memo_lp_cheat");
                row[6] = rs.getDate("tanggal_lp");
                row[7] = rs.getInt("jumlah_keping");
                row[8] = rs.getInt("berat_basah");
                row[9] = rs.getDate("tgl_grading");
                row[10] = rs.getString("kode_tutupan");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_data_LP);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Traceability2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_dataLP_hasilGrading(String no_lp) {
        try {
            DefaultTableModel model = (DefaultTableModel) Table_data_GradingLP.getModel();
            model.setRowCount(0);
            sql = "SELECT `no_laporan_produksi`, `grade_barang_jadi`, `tb_grade_bahan_jadi`.`kode_grade`, `keping`, `gram` \n"
                    + "FROM `tb_grading_bahan_jadi_tv2` LEFT JOIN `tb_grade_bahan_jadi` ON `tb_grading_bahan_jadi_tv2`.`grade_barang_jadi` = `tb_grade_bahan_jadi`.`kode`\n"
                    + "WHERE `no_laporan_produksi` = '" + no_lp + "' ";
            PreparedStatement pst = Utility.db.getConnection().prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            int no = 0;
            Object[] row = new Object[30];
            while (rs.next()) {
                no++;
                row[0] = no;
                row[1] = rs.getString("no_laporan_produksi");
                row[2] = rs.getString("grade_barang_jadi");
                row[3] = rs.getString("kode_grade");
                row[4] = rs.getFloat("keping");
                row[5] = rs.getFloat("gram");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_data_GradingLP);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Traceability2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable1() {
        try {
            float total_keping = 0, total_gram = 0;
            String no_lp = null;
            DefaultTableModel model = (DefaultTableModel) Table_traceability.getModel();
            model.setRowCount(0);

            String search_grade = " AND `tb_grade_bahan_jadi`.`kode_grade` = '" + ComboBox_grade1.getSelectedItem().toString() + "'";
            if (ComboBox_grade1.getSelectedItem().toString().equals("All")) {
                search_grade = "";
            }

            Object[] row = new Object[30];
            sql = "SELECT `tb_laporan_produksi_tv2`.`kode_rsb`, `tb_rumah_burung`.`nama_rumah_burung`, `tb_laporan_produksi_tv2`.`no_kartu_waleta`, `tb_bahan_baku_masuk_cheat`.`tgl_masuk`, `tb_bahan_baku_masuk_cheat`.`tgl_panen`, `tb_laporan_produksi_tv2`.`no_laporan_produksi`, `tb_laporan_produksi_tv2`.`tanggal_lp`, `tb_laporan_produksi_tv2`.`kode_grade`, `tb_laporan_produksi_tv2`.`jumlah_keping`, `tb_laporan_produksi_tv2`.`berat_basah`, `tb_laporan_produksi_tv2`.`tgl_grading`, \n"
                    + "(`tb_finishing_2`.`fbonus_f2`+`tb_finishing_2`.`fnol_f2`+`tb_finishing_2`.`pecah_f2`+`tb_finishing_2`.`flat_f2`) AS 'kpg_f2',\n"
                    + "(`tb_finishing_2`.`berat_fbonus`+`tb_finishing_2`.`berat_fnol`+`tb_finishing_2`.`berat_pecah`+ `tb_finishing_2`.`berat_flat`) AS 'gram_f2', \n"
                    + "`tb_finishing_2`.`sesekan`, `tb_finishing_2`.`hancuran`, `tb_finishing_2`.`rontokan`, `tb_finishing_2`.`bonggol`, `tb_finishing_2`.`serabut` , `tb_lab_laporan_produksi`.`status`, `tb_lab_laporan_produksi`.`tgl_selesai`,`tb_grade_bahan_jadi`.`kode_grade` AS 'grade_bj', `tb_grading_bahan_jadi_tv2`.`keping`, `tb_grading_bahan_jadi_tv2`.`gram`, `tb_laporan_produksi_tv2`.`kode_tutupan` \n"
                    + "FROM `tb_grading_bahan_jadi_tv2` \n"
                    + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_grading_bahan_jadi_tv2`.`grade_barang_jadi` = `tb_grade_bahan_jadi`.`kode`\n"
                    + "LEFT JOIN `tb_lab_laporan_produksi` ON `tb_grading_bahan_jadi_tv2`.`no_laporan_produksi` = `tb_lab_laporan_produksi`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_finishing_2` ON `tb_grading_bahan_jadi_tv2`.`no_laporan_produksi` = `tb_finishing_2`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_laporan_produksi_tv2` ON `tb_grading_bahan_jadi_tv2`.`no_laporan_produksi` = `tb_laporan_produksi_tv2`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_bahan_baku_masuk_cheat` ON `tb_bahan_baku_masuk_cheat`.`no_kartu_waleta` = `tb_laporan_produksi_tv2`.`no_kartu_waleta`\n"
                    + "LEFT JOIN `tb_rumah_burung` ON `tb_laporan_produksi_tv2`.`kode_rsb` = `tb_rumah_burung`.`no_registrasi`\n"
                    + "WHERE `tb_laporan_produksi_tv2`.`no_laporan_produksi` LIKE '%" + txt_search_lp.getText() + "%' "
                    + "AND `tb_laporan_produksi_tv2`.`kode_rsb` LIKE '%" + txt_search_rsbKartu_CT2.getText() + "%' "
                    + "AND `tb_laporan_produksi_tv2`.`no_kartu_waleta` LIKE '%" + txt_no_kartu_CT2.getText() + "%' "
                    + "AND `tb_laporan_produksi_tv2`.`kode_tutupan` LIKE '%" + txt_search_tutupan1.getText() + "%' "
                    + search_grade;
//            System.out.println(sql);
            PreparedStatement pst = Utility.db.getConnection().prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                row[0] = rs.getString("kode_rsb");
                row[1] = rs.getString("nama_rumah_burung");
                row[2] = rs.getString("no_kartu_waleta");
                row[3] = rs.getDate("tgl_masuk");
                row[4] = rs.getDate("tgl_panen");
                row[5] = rs.getString("no_laporan_produksi");
                row[6] = rs.getDate("tanggal_lp");
                no_lp = rs.getString("no_laporan_produksi");
                row[7] = rs.getString("kode_grade");
                row[8] = rs.getInt("jumlah_keping");
                row[9] = rs.getInt("berat_basah");
                row[10] = rs.getInt("kpg_f2");
                row[11] = rs.getFloat("gram_f2");
                row[12] = rs.getFloat("sesekan");
                row[13] = rs.getFloat("hancuran");
                row[14] = rs.getFloat("rontokan");
                row[15] = rs.getFloat("bonggol");
                row[16] = rs.getFloat("serabut");
                row[17] = rs.getString("status");
                row[19] = rs.getString("tgl_grading");
                row[20] = rs.getString("grade_bj");
                row[21] = rs.getInt("keping");
                row[22] = rs.getFloat("gram");
                row[23] = rs.getString("kode_tutupan");
                if (null == rs.getString("status")) {
                    row[18] = null;
                } else {
                    switch (rs.getString("status")) {
                        case "PASSED":
                            row[18] = "PASSED";
                            break;
                        case "HOLD/NON GNS":
                            if (rs.getDate("tgl_selesai") == null) {
                                row[18] = null;
                            } else {
                                String status_treatment_utuh = null,
                                        status_treatment_flat = null;
                                try {
                                    String a = "SELECT COUNT(`kode_treatment`) AS 'jumlah_treatment' FROM `tb_lab_treatment_lp` WHERE `no_laporan_produksi` = '" + no_lp + "' AND `jenis_barang` = 'Flat'";
                                    ResultSet rs1 = Utility.db.getStatement().executeQuery(a);
                                    if (rs1.next()) {
                                        if (rs1.getInt("jumlah_treatment") == 3) {
                                            String c = "SELECT `status` FROM `tb_lab_treatment_lp` WHERE `no_laporan_produksi` = '" + no_lp + "' AND `jenis_barang` = 'Flat' "
                                                    + "AND `nitrit_akhir` = (SELECT MIN(`nitrit_akhir`) AS 'last_treatment' FROM `tb_lab_treatment_lp` WHERE `no_laporan_produksi` = '" + no_lp + "' AND `jenis_barang` = 'Flat')";
                                            ResultSet rs1_2 = Utility.db.getStatement().executeQuery(c);
                                            if (rs1_2.next()) {
                                                status_treatment_flat = rs1_2.getString("status");
                                            }
                                        }
                                    }

                                    String b = "SELECT COUNT(`kode_treatment`) AS 'jumlah_treatment' FROM `tb_lab_treatment_lp` WHERE `no_laporan_produksi` = '" + no_lp + "' AND `jenis_barang` = 'Utuh'";
                                    ResultSet rs2 = Utility.db.getStatement().executeQuery(b);
                                    if (rs2.next()) {
                                        if (rs2.getInt("jumlah_treatment") == 3) {
                                            String d = "SELECT `status` FROM `tb_lab_treatment_lp` WHERE `no_laporan_produksi` = '" + no_lp + "' AND `jenis_barang` = 'Utuh' "
                                                    + "AND `nitrit_akhir` = (SELECT MIN(`nitrit_akhir`) AS 'last_treatment' FROM `tb_lab_treatment_lp` WHERE `no_laporan_produksi` = '" + no_lp + "' AND `jenis_barang` = 'Utuh')";
                                            ResultSet rs2_2 = Utility.db.getStatement().executeQuery(d);
                                            if (rs2_2.next()) {
                                                status_treatment_utuh = rs2_2.getString("status");
                                            }
                                        }
                                    }

                                    if ("HOLD/NON GNS".equals(status_treatment_flat) || "HOLD/NON GNS".equals(status_treatment_utuh)) {
                                        row[18] = "HOLD/NON GNS";
                                    } else {
                                        row[18] = "PASSED";
                                    }
                                } catch (SQLException ex) {
                                    Logger.getLogger(JPanel_Traceability2.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                            break;
                        case "":
                            row[18] = null;
                            break;
                        default:
                            System.out.println("status salah = " + rs.getString("status"));
                    }
                }

//                total_keping = total_keping + rs.getInt("");
//                total_gram = total_gram + rs.getFloat("");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_traceability);
            int total_data = Table_traceability.getRowCount();
            label_total_data.setText(Integer.toString(total_data));
//            label_total_keping_all.setText(decimalFormat.format(total_keping));
//            label_total_gram_all.setText(decimalFormat.format(total_gram));
//            label_total_data.setText(Integer.toString(tabel_stockBJ.getRowCount()));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Traceability2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable2() {
        try {
            String search_grade = " AND `tb_grade_bahan_jadi`.`kode_grade` = '" + ComboBox_grade2.getSelectedItem().toString() + "'";
            if (ComboBox_grade2.getSelectedItem().toString().equals("All")) {
                search_grade = "";
            }

            String filter_rsb = "AND `tb_box_bahan_jadi`.`kode_rsb` = '" + txt_search_rsb2.getText() + "' ";
            if (txt_search_rsb2.getText() == null || txt_search_rsb2.getText().equals("")) {
                filter_rsb = "";
            }

            String filter_tutupan_ct = "AND `tb_box_bahan_jadi`.`no_tutupan_ct1` LIKE '%" + txt_search_tutupan_ct.getText() + "%' ";
            if (txt_search_tutupan_ct.getText() == null || txt_search_tutupan_ct.getText().equals("")) {
                filter_tutupan_ct = "";
            }

            String filter_invoice = "AND `tb_box_packing`.`invoice_pengiriman` LIKE '%" + txt_invoice2.getText() + "%' ";
            if (txt_invoice2.getText() == null || txt_invoice2.getText().equals("")) {
                filter_invoice = "";
            }

            String filter_tanggal_box = "";
            if (Date_box1.getDate() != null && Date_box2.getDate() != null) {
                filter_tanggal_box = "AND `tanggal_box` BETWEEN '" + dateFormat.format(Date_box1.getDate()) + "' AND '" + dateFormat.format(Date_box2.getDate()) + "'";
            }

            String filter_no_box_ct = "AND `tb_box_bahan_jadi`.`no_box_ct1` LIKE '%" + txt_search_box.getText() + "%' ";
            if (txt_search_box.getText() == null || txt_search_box.getText().equals("")) {
                filter_no_box_ct = "";
            }

            String filter_kode_spk = "AND `tb_spk_detail`.`kode_spk` LIKE '%" + txt_search_spk.getText() + "%' ";
            if (txt_search_spk.getText() == null || txt_search_spk.getText().equals("")) {
                filter_kode_spk = "";
            }

            DefaultTableModel model = (DefaultTableModel) Table_traceability2.getModel();
            model.setRowCount(0);
            sql = "SELECT `tb_box_bahan_jadi`.`no_tutupan`, `no_tutupan_ct1`, `tb_box_bahan_jadi`.`no_box`, `tb_box_bahan_jadi`.`no_box_ct1`, `tanggal_box`, `tb_grade_bahan_jadi`.`kode_grade`, `tb_box_bahan_jadi`.`keping`, `tb_box_bahan_jadi`.`berat`, `tb_box_packing`.`batch_number`, `tb_box_packing`.`invoice_pengiriman`, `tb_pengiriman`.`tanggal_pengiriman`, `tb_box_bahan_jadi`.`kode_rsb`, `tb_box_packing`.`tanggal_masuk` AS 'tgl_heat_treatment', `tb_spk_detail`.`prod_date`, `tb_spk_detail`.`kode_spk` \n"
                    + "FROM `tb_box_bahan_jadi` "
                    + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_box_bahan_jadi`.`kode_grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode`\n"
                    + "LEFT JOIN `tb_box_packing` ON `tb_box_bahan_jadi`.`no_box` = `tb_box_packing`.`no_box`\n"
                    + "LEFT JOIN `tb_spk_detail` ON `tb_spk_detail`.`no` = `tb_box_packing`.`no_grade_spk`\n"
                    + "LEFT JOIN `tb_pengiriman` ON `tb_box_packing`.`invoice_pengiriman` = `tb_pengiriman`.`invoice_no`\n"
                    + "WHERE `tb_box_bahan_jadi`.`no_tutupan` LIKE '%" + txt_search_tutupan2.getText() + "%' "
                    + filter_no_box_ct
                    + search_grade 
                    + filter_rsb 
                    + filter_invoice 
                    + filter_tutupan_ct 
                    + filter_tanggal_box 
                    + filter_kode_spk;
//            System.out.println(sql);
            ResultSet rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[20];
            while (rs.next()) {
                row[0] = rs.getString("kode_rsb");
                row[1] = rs.getString("no_tutupan");
                row[2] = rs.getString("no_tutupan_ct1");
                row[3] = rs.getString("no_box_ct1");
                row[4] = rs.getString("no_box");
                row[5] = rs.getDate("tanggal_box");
                row[6] = rs.getString("kode_grade");
                row[7] = rs.getInt("keping");
                row[8] = rs.getFloat("berat");
                row[9] = rs.getDate("tgl_heat_treatment");
                row[10] = rs.getString("batch_number");
                row[11] = rs.getString("invoice_pengiriman");
                row[12] = rs.getDate("tanggal_pengiriman");
                row[13] = rs.getDate("prod_date");
                if (rs.getDate("prod_date") != null && rs.getString("kode_rsb") != null) {
                    row[14] = rs.getString("kode_rsb") + "-" + new SimpleDateFormat("yyMMdd").format(rs.getDate("prod_date"));
                }
                row[15] = rs.getString("kode_spk");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_traceability2);
            int total_data = Table_traceability2.getRowCount();
            label_total_data2.setText(Integer.toString(total_data));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Traceability2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel4 = new javax.swing.JPanel();
        txt_search_tutupan_dataLP = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        txt_search_kodeRSB_dataLP = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        txt_search_no_kartu_dataLP = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        txt_search_lp_dataLP = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        Table_data_LP = new javax.swing.JTable();
        jScrollPane4 = new javax.swing.JScrollPane();
        Table_data_GradingLP = new javax.swing.JTable();
        button_delete_lp_1 = new javax.swing.JButton();
        button_delete_lp_semua = new javax.swing.JButton();
        Button_Search_dataLP = new javax.swing.JButton();
        button_delete_all_hasil_grading = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        Table_traceability = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        label_total_data = new javax.swing.JLabel();
        Button_Search = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        txt_search_tutupan1 = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        ComboBox_grade1 = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        txt_search_lp = new javax.swing.JTextField();
        button_KH14 = new javax.swing.JButton();
        button_surat_keterangan_pengiriman = new javax.swing.JButton();
        button_catatan_penerimaan_dan_grading_baku = new javax.swing.JButton();
        button_laporan_produksi = new javax.swing.JButton();
        button_catatan_pemeriksaan_baku_selama_proses = new javax.swing.JButton();
        button_catatan_perendaman_bahan_mentah = new javax.swing.JButton();
        button_catatan_uji_nitrit = new javax.swing.JButton();
        button_catatan_abnormal_produk = new javax.swing.JButton();
        button_insert_LP = new javax.swing.JButton();
        button_lembar_kerja_pencucian = new javax.swing.JButton();
        button_catatan_pemeriksaan_kebersihan_sarang_walet_selama_proses = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        txt_search_rsbKartu_CT2 = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        txt_no_kartu_CT2 = new javax.swing.JTextField();
        button_catatan_pengeringan_sarang_burung = new javax.swing.JButton();
        button_insert_hasil_gradingLP = new javax.swing.JButton();
        button_lp_kosong = new javax.swing.JButton();
        button_print_lp_semua = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        Table_traceability2 = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        label_total_data2 = new javax.swing.JLabel();
        Button_Search2 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        txt_invoice2 = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txt_search_tutupan2 = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txt_search_box = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        ComboBox_grade2 = new javax.swing.JComboBox<>();
        button_laporan_penyimpanan_barang_jadi = new javax.swing.JButton();
        button_laporan_pengiriman = new javax.swing.JButton();
        button_packing_list = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        txt_search_rsb2 = new javax.swing.JTextField();
        button_laporan_penerimaan_grading = new javax.swing.JButton();
        button_HC = new javax.swing.JButton();
        button_KH12 = new javax.swing.JButton();
        button_catatan_pemeriksaan_produk_jadi = new javax.swing.JButton();
        button_LabelBox = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        txt_search_tutupan_ct = new javax.swing.JTextField();
        button_print_cacatan_penyimpanan_barang_jadi = new javax.swing.JButton();
        button_edit_rsb_tutupan = new javax.swing.JButton();
        button_cheat_kode_tutupan = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        Date_box1 = new com.toedter.calendar.JDateChooser();
        Date_box2 = new com.toedter.calendar.JDateChooser();
        jLabel21 = new javax.swing.JLabel();
        txt_search_spk = new javax.swing.JTextField();

        setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Traceability Ver. II", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        txt_search_tutupan_dataLP.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_tutupan_dataLP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_tutupan_dataLPKeyPressed(evt);
            }
        });

        jLabel20.setBackground(new java.awt.Color(255, 255, 255));
        jLabel20.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel20.setText("No Tutupan :");

        txt_search_kodeRSB_dataLP.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_kodeRSB_dataLP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_kodeRSB_dataLPKeyPressed(evt);
            }
        });

        jLabel19.setBackground(new java.awt.Color(255, 255, 255));
        jLabel19.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel19.setText("Kode RSB :");

        txt_search_no_kartu_dataLP.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_no_kartu_dataLP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_no_kartu_dataLPKeyPressed(evt);
            }
        });

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel18.setText("No Kartu :");

        txt_search_lp_dataLP.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_lp_dataLP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_lp_dataLPKeyPressed(evt);
            }
        });

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel15.setText("No LP :");

        Table_data_LP.setAutoCreateRowSorter(true);
        Table_data_LP.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_data_LP.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "no", "No LP", "Kode RSB", "No Kartu", "Grade Baku", "Memo", "Tgl LP", "Kpg", "Gr", "Tgl Grading", "Kode Tutupan"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Float.class, java.lang.Float.class, java.lang.Object.class, java.lang.String.class
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
        Table_data_LP.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(Table_data_LP);

        Table_data_GradingLP.setAutoCreateRowSorter(true);
        Table_data_GradingLP.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_data_GradingLP.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "no", "No LP", "Kode Grade", "Grade", "Kpg", "Gram"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class
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
        Table_data_GradingLP.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(Table_data_GradingLP);

        button_delete_lp_1.setBackground(new java.awt.Color(255, 255, 255));
        button_delete_lp_1.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        button_delete_lp_1.setText("Delete 1 Data");
        button_delete_lp_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_delete_lp_1ActionPerformed(evt);
            }
        });

        button_delete_lp_semua.setBackground(new java.awt.Color(255, 255, 255));
        button_delete_lp_semua.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        button_delete_lp_semua.setText("Delete All Data");
        button_delete_lp_semua.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_delete_lp_semuaActionPerformed(evt);
            }
        });

        Button_Search_dataLP.setBackground(new java.awt.Color(255, 255, 255));
        Button_Search_dataLP.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Button_Search_dataLP.setText("Refresh");
        Button_Search_dataLP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_Search_dataLPActionPerformed(evt);
            }
        });

        button_delete_all_hasil_grading.setBackground(new java.awt.Color(255, 255, 255));
        button_delete_all_hasil_grading.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        button_delete_all_hasil_grading.setText("Delete All Hasil Grading");
        button_delete_all_hasil_grading.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_delete_all_hasil_gradingActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_lp_dataLP, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_no_kartu_dataLP, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_kodeRSB_dataLP, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_tutupan_dataLP, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Button_Search_dataLP)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_delete_lp_1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_delete_lp_semua)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_delete_all_hasil_grading)
                        .addGap(0, 261, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 939, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_tutupan_dataLP, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_lp_dataLP, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_kodeRSB_dataLP, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_no_kartu_dataLP, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_delete_lp_semua, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_delete_lp_1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_delete_all_hasil_grading, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Button_Search_dataLP, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 599, Short.MAX_VALUE)
                    .addComponent(jScrollPane3))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Data LP", jPanel4);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        Table_traceability.setAutoCreateRowSorter(true);
        Table_traceability.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_traceability.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode RSB", "Nama RSB", "No Kartu", "Tgl Masuk", "Tgl Panen", "No LP", "Tgl LP", "Grade Baku", "Kpg", "Gr", "Kpg F2", "Gr F2", "Ssk", "Hcrn", "Rtk", "Bgl", "Srbt", "QC Test1", "Qc Test", "Tgl Grading", "Grade BJ", "Kpg", "Gr", "No Tutupan"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Object.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.Integer.class, java.lang.Float.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Table_traceability.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(Table_traceability);

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("Total Data :");

        label_total_data.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_data.setText("0");

        Button_Search.setBackground(new java.awt.Color(255, 255, 255));
        Button_Search.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Button_Search.setText("Refresh");
        Button_Search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_SearchActionPerformed(evt);
            }
        });

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel6.setText("No Tutupan :");

        txt_search_tutupan1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_tutupan1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_tutupan1KeyPressed(evt);
            }
        });

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel7.setText("Grade BJD :");

        ComboBox_grade1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_grade1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel11.setText("No LP :");

        txt_search_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_lp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_lpKeyPressed(evt);
            }
        });

        button_KH14.setBackground(new java.awt.Color(255, 255, 255));
        button_KH14.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        button_KH14.setText("KH-14");
        button_KH14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_KH14ActionPerformed(evt);
            }
        });

        button_surat_keterangan_pengiriman.setBackground(new java.awt.Color(255, 255, 255));
        button_surat_keterangan_pengiriman.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        button_surat_keterangan_pengiriman.setText("Surat Keterangan Pengiriman");
        button_surat_keterangan_pengiriman.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_surat_keterangan_pengirimanActionPerformed(evt);
            }
        });

        button_catatan_penerimaan_dan_grading_baku.setBackground(new java.awt.Color(255, 255, 255));
        button_catatan_penerimaan_dan_grading_baku.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        button_catatan_penerimaan_dan_grading_baku.setText("Catatan Penerimaan & Grading Sarang Burung Mentah");
        button_catatan_penerimaan_dan_grading_baku.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_catatan_penerimaan_dan_grading_bakuActionPerformed(evt);
            }
        });

        button_laporan_produksi.setBackground(new java.awt.Color(255, 255, 255));
        button_laporan_produksi.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        button_laporan_produksi.setText("LP");
        button_laporan_produksi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_laporan_produksiActionPerformed(evt);
            }
        });

        button_catatan_pemeriksaan_baku_selama_proses.setBackground(new java.awt.Color(255, 255, 255));
        button_catatan_pemeriksaan_baku_selama_proses.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        button_catatan_pemeriksaan_baku_selama_proses.setText("Catatan Pemeriksaan Bahan Baku Selama Proses");
        button_catatan_pemeriksaan_baku_selama_proses.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_catatan_pemeriksaan_baku_selama_prosesActionPerformed(evt);
            }
        });

        button_catatan_perendaman_bahan_mentah.setBackground(new java.awt.Color(255, 255, 255));
        button_catatan_perendaman_bahan_mentah.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        button_catatan_perendaman_bahan_mentah.setText("Catatan Perendaman Bahan Mentah CCP1 ");
        button_catatan_perendaman_bahan_mentah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_catatan_perendaman_bahan_mentahActionPerformed(evt);
            }
        });

        button_catatan_uji_nitrit.setBackground(new java.awt.Color(255, 255, 255));
        button_catatan_uji_nitrit.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        button_catatan_uji_nitrit.setText("Catatan_Pengujian_Sodium_Nitrit_Semi_Barang_Jadi");
        button_catatan_uji_nitrit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_catatan_uji_nitritActionPerformed(evt);
            }
        });

        button_catatan_abnormal_produk.setBackground(new java.awt.Color(255, 255, 255));
        button_catatan_abnormal_produk.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        button_catatan_abnormal_produk.setText("Catatan Abnormal Produk");
        button_catatan_abnormal_produk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_catatan_abnormal_produkActionPerformed(evt);
            }
        });

        button_insert_LP.setBackground(new java.awt.Color(255, 255, 255));
        button_insert_LP.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        button_insert_LP.setText("Insert LP");
        button_insert_LP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_insert_LPActionPerformed(evt);
            }
        });

        button_lembar_kerja_pencucian.setBackground(new java.awt.Color(255, 255, 255));
        button_lembar_kerja_pencucian.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        button_lembar_kerja_pencucian.setText("Lembar Kerja Pencucian");
        button_lembar_kerja_pencucian.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_lembar_kerja_pencucianActionPerformed(evt);
            }
        });

        button_catatan_pemeriksaan_kebersihan_sarang_walet_selama_proses.setBackground(new java.awt.Color(255, 255, 255));
        button_catatan_pemeriksaan_kebersihan_sarang_walet_selama_proses.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        button_catatan_pemeriksaan_kebersihan_sarang_walet_selama_proses.setText("Catatan Pemeriksaan Kebersihan Sarang Walet Selama Proses");
        button_catatan_pemeriksaan_kebersihan_sarang_walet_selama_proses.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_catatan_pemeriksaan_kebersihan_sarang_walet_selama_prosesActionPerformed(evt);
            }
        });

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel16.setText("Kode RSB :");

        txt_search_rsbKartu_CT2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_rsbKartu_CT2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_rsbKartu_CT2KeyPressed(evt);
            }
        });

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel17.setText("No Kartu :");

        txt_no_kartu_CT2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_no_kartu_CT2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_no_kartu_CT2KeyPressed(evt);
            }
        });

        button_catatan_pengeringan_sarang_burung.setBackground(new java.awt.Color(255, 255, 255));
        button_catatan_pengeringan_sarang_burung.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        button_catatan_pengeringan_sarang_burung.setText("Catatan Pengeringan Sarang Burung");
        button_catatan_pengeringan_sarang_burung.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_catatan_pengeringan_sarang_burungActionPerformed(evt);
            }
        });

        button_insert_hasil_gradingLP.setBackground(new java.awt.Color(255, 255, 255));
        button_insert_hasil_gradingLP.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        button_insert_hasil_gradingLP.setText("Insert Hasil Grading LP");
        button_insert_hasil_gradingLP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_insert_hasil_gradingLPActionPerformed(evt);
            }
        });

        button_lp_kosong.setBackground(new java.awt.Color(255, 255, 255));
        button_lp_kosong.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        button_lp_kosong.setText("LP Kosong");
        button_lp_kosong.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_lp_kosongActionPerformed(evt);
            }
        });

        button_print_lp_semua.setBackground(new java.awt.Color(255, 255, 255));
        button_print_lp_semua.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        button_print_lp_semua.setText("LP Kecil");
        button_print_lp_semua.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_print_lp_semuaActionPerformed(evt);
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
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_data))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel17)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_no_kartu_CT2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel16)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_rsbKartu_CT2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_tutupan1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_grade1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Button_Search)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_insert_LP)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_insert_hasil_gradingLP))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(button_lembar_kerja_pencucian)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGap(157, 157, 157)
                                        .addComponent(button_catatan_pemeriksaan_kebersihan_sarang_walet_selama_proses)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(button_catatan_uji_nitrit)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(button_catatan_abnormal_produk)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_catatan_pengeringan_sarang_burung))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(button_KH14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_surat_keterangan_pengiriman)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_catatan_penerimaan_dan_grading_baku)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_laporan_produksi)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_lp_kosong)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_print_lp_semua)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_catatan_pemeriksaan_baku_selama_proses)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_catatan_perendaman_bahan_mentah)))
                        .addGap(0, 55, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(Button_Search, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_tutupan1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_grade1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_rsbKartu_CT2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_no_kartu_CT2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_insert_LP, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_insert_hasil_gradingLP, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(button_print_lp_semua)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(button_KH14)
                        .addComponent(button_surat_keterangan_pengiriman)
                        .addComponent(button_catatan_penerimaan_dan_grading_baku)
                        .addComponent(button_laporan_produksi)
                        .addComponent(button_catatan_pemeriksaan_baku_selama_proses)
                        .addComponent(button_catatan_perendaman_bahan_mentah)
                        .addComponent(button_lp_kosong)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_catatan_uji_nitrit)
                    .addComponent(button_catatan_abnormal_produk)
                    .addComponent(button_lembar_kerja_pencucian)
                    .addComponent(button_catatan_pemeriksaan_kebersihan_sarang_walet_selama_proses)
                    .addComponent(button_catatan_pengeringan_sarang_burung))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 514, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_data, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16))
        );

        jTabbedPane1.addTab("Tracebility 1", jPanel2);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        Table_traceability2.setAutoCreateRowSorter(true);
        Table_traceability2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_traceability2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "RSB", "No Tutupan", "No Tutupan CT1", "No Box", "No Box Real", "Tgl Box", "Grade", "Keping", "Gram", "Tgl Heat Treatment", "Batch No", "Invoice No", "Tgl Kirim", "Tgl Produksi", "Batch No Baru", "SPK"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.Integer.class, java.lang.Float.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class
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
        Table_traceability2.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(Table_traceability2);

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("Total Data :");

        label_total_data2.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_data2.setText("0");

        Button_Search2.setBackground(new java.awt.Color(255, 255, 255));
        Button_Search2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Button_Search2.setText("Search");
        Button_Search2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_Search2ActionPerformed(evt);
            }
        });

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel5.setText("No Invoice :");

        txt_invoice2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_invoice2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_invoice2KeyPressed(evt);
            }
        });

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel8.setText("No Tutupan :");

        txt_search_tutupan2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_tutupan2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_tutupan2KeyPressed(evt);
            }
        });

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel9.setText("No Box Cheat :");

        txt_search_box.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_box.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_boxKeyPressed(evt);
            }
        });

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel10.setText("Grade BJD :");

        ComboBox_grade2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_grade2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        button_laporan_penyimpanan_barang_jadi.setBackground(new java.awt.Color(255, 255, 255));
        button_laporan_penyimpanan_barang_jadi.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        button_laporan_penyimpanan_barang_jadi.setText("Laporan Penyimpanan Barang Jadi");
        button_laporan_penyimpanan_barang_jadi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_laporan_penyimpanan_barang_jadiActionPerformed(evt);
            }
        });

        button_laporan_pengiriman.setBackground(new java.awt.Color(255, 255, 255));
        button_laporan_pengiriman.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        button_laporan_pengiriman.setText("Laporan Pengiriman");
        button_laporan_pengiriman.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_laporan_pengirimanActionPerformed(evt);
            }
        });

        button_packing_list.setBackground(new java.awt.Color(255, 255, 255));
        button_packing_list.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        button_packing_list.setText("Packing List");
        button_packing_list.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_packing_listActionPerformed(evt);
            }
        });

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel13.setText("Kode RSB :");

        txt_search_rsb2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_rsb2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_rsb2KeyPressed(evt);
            }
        });

        button_laporan_penerimaan_grading.setBackground(new java.awt.Color(255, 255, 255));
        button_laporan_penerimaan_grading.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        button_laporan_penerimaan_grading.setText("Laporan Penerimaan Grading");
        button_laporan_penerimaan_grading.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_laporan_penerimaan_gradingActionPerformed(evt);
            }
        });

        button_HC.setBackground(new java.awt.Color(255, 255, 255));
        button_HC.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        button_HC.setText("HC");
        button_HC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_HCActionPerformed(evt);
            }
        });

        button_KH12.setBackground(new java.awt.Color(255, 255, 255));
        button_KH12.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        button_KH12.setText("KH-12");
        button_KH12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_KH12ActionPerformed(evt);
            }
        });

        button_catatan_pemeriksaan_produk_jadi.setBackground(new java.awt.Color(255, 255, 255));
        button_catatan_pemeriksaan_produk_jadi.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        button_catatan_pemeriksaan_produk_jadi.setText("Catatan Pemeriksaan Produk Jadi");
        button_catatan_pemeriksaan_produk_jadi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_catatan_pemeriksaan_produk_jadiActionPerformed(evt);
            }
        });

        button_LabelBox.setBackground(new java.awt.Color(255, 255, 255));
        button_LabelBox.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        button_LabelBox.setText("Label Box");
        button_LabelBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_LabelBoxActionPerformed(evt);
            }
        });

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel12.setText("No Tutupan CT :");

        txt_search_tutupan_ct.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_tutupan_ct.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_tutupan_ctKeyPressed(evt);
            }
        });

        button_print_cacatan_penyimpanan_barang_jadi.setBackground(new java.awt.Color(255, 255, 255));
        button_print_cacatan_penyimpanan_barang_jadi.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        button_print_cacatan_penyimpanan_barang_jadi.setText("Print Catatan Penyimpanan Barang Jadi No Box CT");
        button_print_cacatan_penyimpanan_barang_jadi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_print_cacatan_penyimpanan_barang_jadiActionPerformed(evt);
            }
        });

        button_edit_rsb_tutupan.setBackground(new java.awt.Color(255, 255, 255));
        button_edit_rsb_tutupan.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        button_edit_rsb_tutupan.setText("Edit RSB Tutupan");
        button_edit_rsb_tutupan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_edit_rsb_tutupanActionPerformed(evt);
            }
        });

        button_cheat_kode_tutupan.setBackground(new java.awt.Color(255, 255, 255));
        button_cheat_kode_tutupan.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        button_cheat_kode_tutupan.setText("Cheat Kode Tutupan");
        button_cheat_kode_tutupan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_cheat_kode_tutupanActionPerformed(evt);
            }
        });

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel14.setText("Tanggal Box :");

        Date_box1.setBackground(new java.awt.Color(255, 255, 255));
        Date_box1.setDateFormatString("dd MMM yyyy");
        Date_box1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Date_box2.setBackground(new java.awt.Color(255, 255, 255));
        Date_box2.setDateFormatString("dd MMM yyyy");
        Date_box2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel21.setBackground(new java.awt.Color(255, 255, 255));
        jLabel21.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel21.setText("SPK :");

        txt_search_spk.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_spk.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_spkKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1331, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_data2))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(button_print_cacatan_penyimpanan_barang_jadi)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_laporan_penerimaan_grading)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_laporan_penyimpanan_barang_jadi)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_catatan_pemeriksaan_produk_jadi)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_laporan_pengiriman)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_packing_list)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_HC)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_KH12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_LabelBox))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_box1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_box2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_edit_rsb_tutupan)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_cheat_kode_tutupan))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_invoice2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_tutupan2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_tutupan_ct, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_box, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_rsb2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_grade2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel21)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_spk, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Button_Search2)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_search_tutupan2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_search_tutupan_ct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_invoice2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_search_spk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(Button_Search2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(ComboBox_grade2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_search_rsb2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_search_box, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_box1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_box2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(button_edit_rsb_tutupan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_cheat_kode_tutupan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(button_laporan_penyimpanan_barang_jadi)
                        .addComponent(button_laporan_pengiriman)
                        .addComponent(button_packing_list)
                        .addComponent(button_HC)
                        .addComponent(button_KH12)
                        .addComponent(button_catatan_pemeriksaan_produk_jadi)
                        .addComponent(button_LabelBox))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(button_laporan_penerimaan_grading)
                        .addComponent(button_print_cacatan_penyimpanan_barang_jadi)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 520, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_data2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Tracebility 2", jPanel3);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
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

    private void Button_SearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_SearchActionPerformed
        // TODO add your handling code here:
        refreshTable1();
    }//GEN-LAST:event_Button_SearchActionPerformed

    private void Button_Search2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_Search2ActionPerformed
        // TODO add your handling code here:
        refreshTable2();
    }//GEN-LAST:event_Button_Search2ActionPerformed

    private void txt_invoice2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_invoice2KeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable2();
        }
    }//GEN-LAST:event_txt_invoice2KeyPressed

    private void txt_search_tutupan1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_tutupan1KeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable1();
        }
    }//GEN-LAST:event_txt_search_tutupan1KeyPressed

    private void txt_search_tutupan2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_tutupan2KeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable2();
        }
    }//GEN-LAST:event_txt_search_tutupan2KeyPressed

    private void txt_search_boxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_boxKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable2();
        }
    }//GEN-LAST:event_txt_search_boxKeyPressed

    private void txt_search_lpKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_lpKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable1();
        }
    }//GEN-LAST:event_txt_search_lpKeyPressed

    private void txt_search_rsb2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_rsb2KeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable2();
        }
    }//GEN-LAST:event_txt_search_rsb2KeyPressed

    private void button_laporan_pengirimanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_laporan_pengirimanActionPerformed
        // TODO add your handling code here:
        try {
            int j = Table_traceability2.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih salah satu nomor box yang akan dibuatkan laporan pengirimannya", "warning!", 1);
            } else {
                if (Table_traceability2.getValueAt(j, 11) == null || Table_traceability2.getValueAt(j, 11).toString().equals("")) {
                    JOptionPane.showMessageDialog(this, "Maaf Box ini belum ada nomor Invoice.", "warning!", 1);
                } else {
                    JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Traceability2\\Laporan_Pengiriman_tv2.jrxml");
                    JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
                    Map<String, Object> params = new HashMap<String, Object>();
                    params.put("no_invoice", Table_traceability2.getValueAt(j, 11).toString() + "%");
                    JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, params, Utility.db.getConnection());
                    JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
                }
            }
        } catch (JRException ex) {
            Logger.getLogger(JPanel_Traceability2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_laporan_pengirimanActionPerformed

    private void button_laporan_penyimpanan_barang_jadiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_laporan_penyimpanan_barang_jadiActionPerformed
        // TODO add your handling code here:
        try {
            int j = Table_traceability2.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih salah satu nomor box yang akan dibuatkan laporan nya", "warning!", 1);
            } else {
                JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Traceability2\\Laporan_Penyimpanan_Barang_Jadi_tv2.jrxml");
                JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("NO_TUTUPAN", Table_traceability2.getValueAt(j, 2).toString());
                params.put("KODE_GRADE", Table_traceability2.getValueAt(j, 6).toString());
                JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, params, Utility.db.getConnection());
                JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
            }
        } catch (Exception ex) {
            Logger.getLogger(JPanel_Traceability2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_laporan_penyimpanan_barang_jadiActionPerformed

    private void button_laporan_penerimaan_gradingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_laporan_penerimaan_gradingActionPerformed
        // TODO add your handling code here:
        try {
            int j = Table_traceability2.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih salah satu nomor box yang akan dibuatkan laporan nya", "warning!", 1);
            } else {
                String tgl_tutupan = "", rsb_tutupan = "";
                try {
                    sql = "SELECT `kode_rumah_burung`, DATE_FORMAT(`tgl_selesai_tutupan`, '%d %b %y') AS 'tgl_selesai_tutupan' FROM `tb_tutupan_grading` WHERE `kode_tutupan` = '" + Table_traceability2.getValueAt(j, 2).toString() + "'";
                    ResultSet rs = Utility.db.getStatement().executeQuery(sql);
                    if (rs.next()) {
                        if (rs.getString("tgl_selesai_tutupan") == null) {
                            JOptionPane.showMessageDialog(this, "Tutupan belum selesai, maka tidak ada tgl tutupan");
                        } else {
                            tgl_tutupan = rs.getString("tgl_selesai_tutupan");
                            rsb_tutupan = rs.getString("kode_rumah_burung");
                        }
                    }
                } catch (SQLException e) {
                    Logger.getLogger(JPanel_Traceability2.class.getName()).log(Level.SEVERE, null, e);
                }
                JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Traceability2\\Laporan_Penerimaan_Grading_tv2.jrxml");
                JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("NO_TUTUPAN", Table_traceability2.getValueAt(j, 2).toString());
                params.put("RSB_TUTUPAN", Table_traceability2.getValueAt(j, 0).toString());
                params.put("GRADE_BJD", Table_traceability2.getValueAt(j, 6).toString());
                params.put("TANGGAL_TUTUPAN", tgl_tutupan);
                JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, params, Utility.db.getConnection());
                JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
            }

        } catch (JRException ex) {
            JOptionPane.showMessageDialog(this, ex.getLocalizedMessage());
            Logger.getLogger(JPanel_Traceability2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_laporan_penerimaan_gradingActionPerformed

    private void button_catatan_penerimaan_dan_grading_bakuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_catatan_penerimaan_dan_grading_bakuActionPerformed
        // TODO add your handling code here:
        try {
            int j = Table_traceability.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Please select data from the table first", "warning!", 1);
            } else {
                JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Traceability2\\Catatan_Penerimaan_dan_Grading_Sarang_Burung_Mentah_tv2.jrxml");
                Map<String, Object> map = new HashMap<>();
                map.put("no_kartu_waleta", Table_traceability.getValueAt(j, 2).toString());//parameter name should be like it was named inside your report.
                JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
                JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, map, Utility.db.getConnection());
                JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
            }

        } catch (JRException ex) {
            JOptionPane.showMessageDialog(this, ex.getLocalizedMessage());
            Logger.getLogger(JPanel_Traceability2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_catatan_penerimaan_dan_grading_bakuActionPerformed

    private void button_surat_keterangan_pengirimanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_surat_keterangan_pengirimanActionPerformed
        // TODO add your handling code here:
        try {
            int j = Table_traceability.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Pilih salah satu data !", "warning!", 1);
            } else {
                String file_name = Table_traceability.getValueAt(j, 0).toString() + "-" + Table_traceability.getValueAt(j, 3).toString().replace("-", "");//CT1
                file_name = file_name.replace("-20", "-");

//                String nama_file = "";
//                sql = "SELECT `surat_keterangan_pengiriman` FROM `tb_bahan_baku_masuk_cheat` "
//                        + "WHERE `no_kartu_waleta` = '" + Table_traceability.getValueAt(j, 2).toString() + "'";
//                ResultSet rs = Utility.db.getStatement().executeQuery(sql);
//                if (rs.next()) {
//                    nama_file = rs.getString("surat_keterangan_pengiriman");
//                }
                if (file_name == null || file_name.equals("")) {
                    JOptionPane.showMessageDialog(this, "Maaf file tidak di temukan");
                } else {
                    try {
                        Runtime rt = Runtime.getRuntime();
                        Process pr = rt.exec("explorer.exe \\\\192.168.10.2\\Shared Folder\\DOKUMEN SISTEM\\1_Surat_Keterangan_Pengiriman\\" + file_name + ".pdf");
                    } catch (IOException ex) {
                        Logger.getLogger(JPanel_Traceability2.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getLocalizedMessage());
            Logger.getLogger(JPanel_Traceability2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_surat_keterangan_pengirimanActionPerformed

    private void button_laporan_produksiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_laporan_produksiActionPerformed
        // TODO add your handling code here:
        try {
            JasperPrint JASP_PRINT = null;
            int target_utuh = 0, target_pch = 0, target_sp = 0, target_sh = 0;
            String no_lp = "";
            for (int i = 0; i < Table_traceability.getRowCount(); i++) {
                if (i != 0) {
                    no_lp = no_lp + ", ";
                }
                no_lp = no_lp + "'" + Table_traceability.getValueAt(i, 5).toString() + "'";
            }
            sql = "SELECT `no_laporan_produksi`, `tb_laporan_produksi_tv2`.`kode_grade`, `tb_grade_bahan_baku`.`jenis_bentuk`, `tb_grade_bahan_baku`.`jenis_bulu` \n"
                    + "FROM `tb_laporan_produksi_tv2` "
                    + "LEFT JOIN `tb_grade_bahan_baku` ON `tb_laporan_produksi_tv2`.`kode_grade` = `tb_grade_bahan_baku`.`kode_grade` "
                    + "WHERE `no_laporan_produksi` = '" + Table_traceability.getValueAt(0, 5).toString() + "' ";
            ResultSet rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                if (rs.getString("jenis_bentuk").contains("Mangkok")) {
                    switch (rs.getString("jenis_bulu")) {
                        case "Bulu Ringan":
                            target_utuh = 70;
                            target_pch = 10;
                            target_sp = 14;
                            target_sh = 6;
                            break;
                        case "Bulu Ringan Sekali/Bulu Ringan":
                            target_utuh = 70;
                            target_pch = 10;
                            target_sp = 14;
                            target_sh = 6;
                            break;
                        case "Bulu Sedang":
                            target_utuh = 60;
                            target_pch = 15;
                            target_sp = 17;
                            target_sh = 8;
                            break;
                        case "Bulu Berat":
                            target_utuh = 45;
                            target_pch = 25;
                            target_sp = 20;
                            target_sh = 10;
                            break;
                        case "Bulu Berat Sekali":
                            target_utuh = 45;
                            target_pch = 25;
                            target_sp = 20;
                            target_sh = 10;
                            break;
                        default:
                            target_utuh = 0;
                            target_pch = 0;
                            target_sp = 0;
                            target_sh = 0;
                            break;
                    }
                } else if (rs.getString("jenis_bentuk").contains("Oval") || rs.getString("jenis_bentuk").contains("Segitiga")) {
                    switch (rs.getString("jenis_bulu")) {
                        case "Bulu Ringan":
                            target_utuh = 65;
                            target_pch = 15;
                            target_sp = 14;
                            target_sh = 6;
                            break;
                        case "Bulu Ringan Sekali/Bulu Ringan":
                            target_utuh = 65;
                            target_pch = 15;
                            target_sp = 14;
                            target_sh = 6;
                            break;
                        case "Bulu Sedang":
                            target_utuh = 55;
                            target_pch = 20;
                            target_sp = 17;
                            target_sh = 8;
                            break;
                        case "Bulu Berat":
                            target_utuh = 45;
                            target_pch = 30;
                            target_sp = 20;
                            target_sh = 10;
                            break;
                        case "Bulu Berat Sekali":
                            target_utuh = 45;
                            target_pch = 30;
                            target_sp = 20;
                            target_sh = 10;
                            break;
                        default:
                            target_utuh = 0;
                            target_pch = 0;
                            target_sp = 0;
                            target_sh = 0;
                            break;
                    }
                } else if (rs.getString("jenis_bentuk").contains("Pecah")) {
                    switch (rs.getString("jenis_bulu")) {
                        case "Bulu Ringan":
                            target_utuh = 0;
                            target_pch = 75;
                            target_sp = 19;
                            target_sh = 6;
                            break;
                        case "Bulu Ringan Sekali/Bulu Ringan":
                            target_utuh = 0;
                            target_pch = 75;
                            target_sp = 19;
                            target_sh = 6;
                            break;
                        case "Bulu Sedang":
                            target_utuh = 0;
                            target_pch = 70;
                            target_sp = 22;
                            target_sh = 8;
                            break;
                        case "Bulu Berat":
                            target_utuh = 0;
                            target_pch = 65;
                            target_sp = 25;
                            target_sh = 10;
                            break;
                        case "Bulu Berat Sekali":
                            target_utuh = 0;
                            target_pch = 65;
                            target_sp = 25;
                            target_sh = 10;
                            break;
                        default:
                            target_utuh = 0;
                            target_pch = 0;
                            target_sp = 0;
                            target_sh = 0;
                            break;
                    }
                } else {
                    switch (rs.getString("jenis_bulu")) {
                        case "Bulu Ringan":
                            target_utuh = 0;
                            target_pch = 75;
                            target_sp = 19;
                            target_sh = 6;
                            break;
                        case "Bulu Ringan Sekali/Bulu Ringan":
                            target_utuh = 0;
                            target_pch = 75;
                            target_sp = 19;
                            target_sh = 6;
                            break;
                        case "Bulu Sedang":
                            target_utuh = 0;
                            target_pch = 70;
                            target_sp = 22;
                            target_sh = 8;
                            break;
                        case "Bulu Berat":
                            target_utuh = 0;
                            target_pch = 65;
                            target_sp = 25;
                            target_sh = 10;
                            break;
                        case "Bulu Berat Sekali":
                            target_utuh = 0;
                            target_pch = 65;
                            target_sp = 25;
                            target_sh = 10;
                            break;
                        default:
                            target_utuh = 0;
                            target_pch = 0;
                            target_sp = 0;
                            target_sh = 0;
                            break;
                    }
                }
                JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Traceability2\\laporan_produksi_cheat\\Laporan_Produksi_QR_tv2.jrxml");
                JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("PARAM_NO_LP", rs.getString("no_laporan_produksi"));
                params.put("target_utuh", target_utuh);
                params.put("target_pch", target_pch);
                params.put("target_sp", target_sp);
                params.put("target_sh", target_sh);
                params.put("parameterIsKosong", false);
                params.put("parameterHalaman", 1);
                params.put("parameterJumlahHalaman", 1);
                params.put("SUBREPORT_DIR", "Report\\Traceability2\\laporan_produksi_cheat\\");
                JASP_PRINT = JasperFillManager.fillReport(JASP_REP, params, Utility.db.getConnection());
            }

            sql = "SELECT `no_laporan_produksi`, `tb_laporan_produksi_tv2`.`kode_grade`, `tb_grade_bahan_baku`.`jenis_bentuk`, `tb_grade_bahan_baku`.`jenis_bulu` \n"
                    + "FROM `tb_laporan_produksi_tv2` "
                    + "LEFT JOIN `tb_grade_bahan_baku` ON `tb_laporan_produksi_tv2`.`kode_grade` = `tb_grade_bahan_baku`.`kode_grade` "
                    + "WHERE `no_laporan_produksi` IN (" + no_lp + ") ";
            rs = Utility.db.getStatement().executeQuery(sql);
            rs.next();
            while (rs.next()) {
                if (rs.getString("jenis_bentuk").contains("Mangkok")) {
                    switch (rs.getString("jenis_bulu")) {
                        case "Bulu Ringan":
                            target_utuh = 70;
                            target_pch = 10;
                            target_sp = 14;
                            target_sh = 6;
                            break;
                        case "Bulu Ringan Sekali/Bulu Ringan":
                            target_utuh = 70;
                            target_pch = 10;
                            target_sp = 14;
                            target_sh = 6;
                            break;
                        case "Bulu Sedang":
                            target_utuh = 60;
                            target_pch = 15;
                            target_sp = 17;
                            target_sh = 8;
                            break;
                        case "Bulu Berat":
                            target_utuh = 45;
                            target_pch = 25;
                            target_sp = 20;
                            target_sh = 10;
                            break;
                        case "Bulu Berat Sekali":
                            target_utuh = 45;
                            target_pch = 25;
                            target_sp = 20;
                            target_sh = 10;
                            break;
                        default:
                            target_utuh = 0;
                            target_pch = 0;
                            target_sp = 0;
                            target_sh = 0;
                            break;
                    }
                } else if (rs.getString("jenis_bentuk").contains("Oval") || rs.getString("jenis_bentuk").contains("Segitiga")) {
                    switch (rs.getString("jenis_bulu")) {
                        case "Bulu Ringan":
                            target_utuh = 65;
                            target_pch = 15;
                            target_sp = 14;
                            target_sh = 6;
                            break;
                        case "Bulu Ringan Sekali/Bulu Ringan":
                            target_utuh = 65;
                            target_pch = 15;
                            target_sp = 14;
                            target_sh = 6;
                            break;
                        case "Bulu Sedang":
                            target_utuh = 55;
                            target_pch = 20;
                            target_sp = 17;
                            target_sh = 8;
                            break;
                        case "Bulu Berat":
                            target_utuh = 45;
                            target_pch = 30;
                            target_sp = 20;
                            target_sh = 10;
                            break;
                        case "Bulu Berat Sekali":
                            target_utuh = 45;
                            target_pch = 30;
                            target_sp = 20;
                            target_sh = 10;
                            break;
                        default:
                            target_utuh = 0;
                            target_pch = 0;
                            target_sp = 0;
                            target_sh = 0;
                            break;
                    }
                } else if (rs.getString("jenis_bentuk").contains("Pecah")) {
                    switch (rs.getString("jenis_bulu")) {
                        case "Bulu Ringan":
                            target_utuh = 0;
                            target_pch = 75;
                            target_sp = 19;
                            target_sh = 6;
                            break;
                        case "Bulu Ringan Sekali/Bulu Ringan":
                            target_utuh = 0;
                            target_pch = 75;
                            target_sp = 19;
                            target_sh = 6;
                            break;
                        case "Bulu Sedang":
                            target_utuh = 0;
                            target_pch = 70;
                            target_sp = 22;
                            target_sh = 8;
                            break;
                        case "Bulu Berat":
                            target_utuh = 0;
                            target_pch = 65;
                            target_sp = 25;
                            target_sh = 10;
                            break;
                        case "Bulu Berat Sekali":
                            target_utuh = 0;
                            target_pch = 65;
                            target_sp = 25;
                            target_sh = 10;
                            break;
                        default:
                            target_utuh = 0;
                            target_pch = 0;
                            target_sp = 0;
                            target_sh = 0;
                            break;
                    }
                } else {
                    switch (rs.getString("jenis_bulu")) {
                        case "Bulu Ringan":
                            target_utuh = 0;
                            target_pch = 75;
                            target_sp = 19;
                            target_sh = 6;
                            break;
                        case "Bulu Ringan Sekali/Bulu Ringan":
                            target_utuh = 0;
                            target_pch = 75;
                            target_sp = 19;
                            target_sh = 6;
                            break;
                        case "Bulu Sedang":
                            target_utuh = 0;
                            target_pch = 70;
                            target_sp = 22;
                            target_sh = 8;
                            break;
                        case "Bulu Berat":
                            target_utuh = 0;
                            target_pch = 65;
                            target_sp = 25;
                            target_sh = 10;
                            break;
                        case "Bulu Berat Sekali":
                            target_utuh = 0;
                            target_pch = 65;
                            target_sp = 25;
                            target_sh = 10;
                            break;
                        default:
                            target_utuh = 0;
                            target_pch = 0;
                            target_sp = 0;
                            target_sh = 0;
                            break;
                    }
                }

                JasperDesign JASP_DESIGN2 = JRXmlLoader.load("Report\\Traceability2\\laporan_produksi_cheat\\Laporan_Produksi_QR_tv2.jrxml");
                JasperReport JASP_REP2 = JasperCompileManager.compileReport(JASP_DESIGN2);
                Map<String, Object> params2 = new HashMap<String, Object>();
                params2.put("PARAM_NO_LP", rs.getString("no_laporan_produksi"));
                params2.put("target_utuh", target_utuh);
                params2.put("target_pch", target_pch);
                params2.put("target_sp", target_sp);
                params2.put("target_sh", target_sh);
                params2.put("parameterIsKosong", false);
                params2.put("SUBREPORT_DIR", "Report\\Traceability2\\laporan_produksi_cheat\\");
                JasperPrint JASP_PRINT2 = JasperFillManager.fillReport(JASP_REP2, params2, Utility.db.getConnection());
                List pages = JASP_PRINT2.getPages();
                for (int k = 0; k < pages.size(); k++) {
                    JRPrintPage object = (JRPrintPage) pages.get(k);
                    JASP_PRINT.addPage(object);
                }
            }
            JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getLocalizedMessage());
            Logger.getLogger(JPanel_Traceability2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_laporan_produksiActionPerformed

    private void button_catatan_pemeriksaan_baku_selama_prosesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_catatan_pemeriksaan_baku_selama_prosesActionPerformed
        // TODO add your handling code here:
        try {
            String no_lp = "";
            for (int i = 0; i < Table_traceability.getRowCount(); i++) {
                if (i != 0) {
                    no_lp = no_lp + ", ";
                }
                no_lp = no_lp + "'" + Table_traceability.getValueAt(i, 5).toString() + "'";
            }

            String Query = "SELECT LP.`no_laporan_produksi`, LP.`no_kartu_waleta`, LP.`kode_rsb`, `tb_bahan_baku_masuk_cheat`.`tgl_masuk`, `tb_bahan_baku_masuk_cheat`.`kadar_air_bahan_baku`, `tb_bahan_baku_masuk_cheat`.`nitrit_baku_by`, "
                    + "GREATEST(`nitrit_bm`, `nitrit_bm_w2`, `nitrit_bm_w3`) AS 'nitrit_bm', \n"
                    + "IF(`nitrit_utuh` > 30, (SELECT MIN(`nitrit_akhir`) AS 'nitrit' FROM `tb_lab_treatment_lp` WHERE `no_laporan_produksi` = LP.`no_laporan_produksi` AND `jenis_barang` = 'Utuh'), `nitrit_utuh`) AS 'nitrit_utuh', \n"
                    + "IF(`nitrit_flat` > 30, (SELECT MIN(`nitrit_akhir`) AS 'nitrit' FROM `tb_lab_treatment_lp` WHERE `no_laporan_produksi` = LP.`no_laporan_produksi` AND `jenis_barang` = 'Flat'), `nitrit_flat`) AS 'nitrit_flat', \n"
                    + "IF(`jidun` > 30, (SELECT MIN(`nitrit_akhir`) AS 'nitrit' FROM `tb_lab_treatment_lp` WHERE `no_laporan_produksi` = LP.`no_laporan_produksi` AND `jenis_barang` = 'Jidun'), `jidun`) AS 'jidun', \n"
                    + "ROUND((RAND() * (17-8))+8) AS 'random', `nitrit_rendam`\n"
                    + "FROM `tb_laporan_produksi_tv2` LP\n"
                    + "LEFT JOIN `tb_bahan_baku_masuk_cheat` ON LP.`no_kartu_waleta` = `tb_bahan_baku_masuk_cheat`.`no_kartu_waleta`\n"
                    + "LEFT JOIN `tb_lab_bahan_baku` ON LP.`no_kartu_waleta` = `tb_lab_bahan_baku`.`no_kartu_waleta`\n"
                    + "LEFT JOIN `tb_lab_laporan_produksi` ON LP.`no_laporan_produksi` = `tb_lab_laporan_produksi`.`no_laporan_produksi`"
                    + "LEFT JOIN `tb_lab_uji_nitrit_rendam` ON LP.`no_laporan_produksi` = `tb_lab_uji_nitrit_rendam`.`no_laporan_produksi`\n"
                    + "WHERE LP.`no_laporan_produksi` IN (" + no_lp + ")";
            JRDesignQuery newQuery = new JRDesignQuery();
            newQuery.setText(Query);
            JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Traceability2\\Catatan_Pemeriksaan_Bahan_Baku_Selama_Proses_tv2.jrxml");
            JASP_DESIGN.setQuery(newQuery);
            Map<String, Object> map = new HashMap<>();
            map.put("NO_LP", no_lp);
            JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
            JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, map, Utility.db.getConnection());
            JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
        } catch (JRException ex) {
            Logger.getLogger(JPanel_Traceability2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_catatan_pemeriksaan_baku_selama_prosesActionPerformed

    private void button_catatan_perendaman_bahan_mentahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_catatan_perendaman_bahan_mentahActionPerformed
        // TODO add your handling code here:
        try {
            String no_lp = "";
            for (int i = 0; i < Table_traceability.getRowCount(); i++) {
                if (i != 0) {
                    no_lp = no_lp + ", ";
                }
                no_lp = no_lp + "'" + Table_traceability.getValueAt(i, 5).toString() + "'";
            }
            String Query = "SELECT `tanggal_rendam`, `tb_rendam`.`no_laporan_produksi`, `no_kartu_waleta`, `kode_rsb`, `jumlah_keping`, `berat_basah`, `lama_waktu_rendam` \n"
                    + "FROM `tb_rendam` "
                    + "LEFT JOIN `tb_laporan_produksi_tv2` ON `tb_rendam`.`no_laporan_produksi` = `tb_laporan_produksi_tv2`.`no_laporan_produksi`\n"
                    + "WHERE `tb_rendam`.`no_laporan_produksi` IN (" + no_lp + ") \n"
                    + "ORDER BY `tanggal_rendam` DESC, `tb_rendam`.`no_laporan_produksi` ASC";
//            System.out.println(Query);
            JRDesignQuery newQuery = new JRDesignQuery();
            newQuery.setText(Query);
            JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Traceability2\\Catatan_Rendaman_Bahan_Mentah_tv2.jrxml");
            JASP_DESIGN.setQuery(newQuery);
            JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
            JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, null, Utility.db.getConnection());
            JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
        } catch (JRException ex) {
            Logger.getLogger(JPanel_Traceability2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_catatan_perendaman_bahan_mentahActionPerformed

    private void button_catatan_uji_nitritActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_catatan_uji_nitritActionPerformed
        // TODO add your handling code here:
//        int x = Table_traceability.getSelectedRow();
//        if (x < 0) {
//            JOptionPane.showMessageDialog(this, "Pilih salah satu data LP !");
//        } else {
//        }

//            String tgl_uji = "";
//            for (int i = 0; i < Table_traceability.getRowCount(); i++) {
//                try {
//                    if (i != 0) {
//                        tgl_uji = tgl_uji + ", ";
//                    }
//                    sql = "SELECT `tgl_uji` FROM `tb_lab_laporan_produksi` WHERE `no_laporan_produksi` = '" + Table_traceability.getValueAt(i, 5).toString() + "'";
//                    ResultSet rs = Utility.db.getStatement().executeQuery(sql);
//                    if (rs.next()) {
//                        tgl_uji = tgl_uji + "'" + rs.getString("tgl_uji") + "'";
//                    }
//                } catch (SQLException e) {
//                    Logger.getLogger(JPanel_Traceability.class.getName()).log(Level.SEVERE, null, e);
//                }
//            }
        try {
            String no_lp = "";
            for (int i = 0; i < Table_traceability.getRowCount(); i++) {
                if (i != 0) {
                    no_lp = no_lp + ", ";
                }
                no_lp = no_lp + "'" + Table_traceability.getValueAt(i, 5).toString() + "'";
            }

            String Query = "SELECT `tb_lab_laporan_produksi`.`no_laporan_produksi`, `kode_grade`, `tb_lab_laporan_produksi`.`tgl_masuk`, `tgl_uji`, `tgl_selesai`, `status_akhir`, `kode_rsb`, `no_kartu_waleta`,"
                    + "GREATEST(`nitrit_utuh`,`jidun`, `nitrit_flat`) AS 'nitrit', TR.`nitrit_treatment`\n"
                    + "FROM `tb_lab_laporan_produksi`\n"
                    + "LEFT JOIN `tb_laporan_produksi_tv2` ON `tb_lab_laporan_produksi`.`no_laporan_produksi` = `tb_laporan_produksi_tv2`.`no_laporan_produksi`\n"
                    + "LEFT JOIN (SELECT `no_laporan_produksi`, MIN(`nitrit_akhir`) AS 'nitrit_treatment' FROM `tb_lab_treatment_lp` WHERE 1 GROUP BY `no_laporan_produksi`) TR "
                    + "ON `tb_lab_laporan_produksi`.`no_laporan_produksi` = TR.`no_laporan_produksi`\n"
                    + "WHERE `tb_lab_laporan_produksi`.`no_laporan_produksi` IN (" + no_lp + ") AND `status_akhir` = 'PASSED'"
                    + " ORDER BY `tb_lab_laporan_produksi`.`no_laporan_produksi` ASC";
//            System.out.println(Query);
            JRDesignQuery newQuery = new JRDesignQuery();
            newQuery.setText(Query);
            JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Traceability2\\Catatan_Pengujian_Sodium_Nitrit_Semi_Barang_Jadi_tv2.jrxml");
            JASP_DESIGN.setQuery(newQuery);
            JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
            JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, null, Utility.db.getConnection());
            JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
        } catch (JRException ex) {
            Logger.getLogger(JPanel_Traceability2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_catatan_uji_nitritActionPerformed

    private void button_catatan_abnormal_produkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_catatan_abnormal_produkActionPerformed
        // TODO add your handling code here:
        try {
            String no_lp = "'" + Table_traceability.getValueAt(0, 5).toString() + "'";
            for (int i = 1; i < Table_traceability.getRowCount(); i++) {
                no_lp = no_lp + ", '" + Table_traceability.getValueAt(i, 5).toString() + "'";
            }
            sql = "SELECT `tgl_treatment`, `tb_lab_treatment_lp`.`no_laporan_produksi`, `no_kartu_waleta`, `tb_lab_treatment_lp`.`jenis_barang`, `waktu_treatment`, `nitrit_awal`, `tb_lab_treatment_lp`.`nitrit_akhir`, `status`, `kode_rsb` "
                    + "FROM `tb_lab_treatment_lp` "
                    + "LEFT JOIN `tb_laporan_produksi_tv2` ON `tb_lab_treatment_lp`.`no_laporan_produksi` = `tb_laporan_produksi_tv2`.`no_laporan_produksi` "
                    + "LEFT JOIN (SELECT `no_laporan_produksi`, MIN(`nitrit_akhir`) AS `nitrit_akhir`, COUNT(`no_laporan_produksi`) AS `jumlah_laporan`, `jenis_barang` "
                    + "FROM `tb_lab_treatment_lp` GROUP BY `no_laporan_produksi`, `jenis_barang`) AS `a` ON `tb_lab_treatment_lp`.`no_laporan_produksi` = `a`.`no_laporan_produksi` AND `tb_lab_treatment_lp`.`jenis_barang` = `a`.`jenis_barang` AND `tb_lab_treatment_lp`.`nitrit_akhir` = `a`.`nitrit_akhir` "
                    + "WHERE NOT (`status`='HOLD/NON GNS' AND `a`.`jumlah_laporan`<3) "
                    + "AND `tb_lab_treatment_lp`.`no_laporan_produksi` IN (" + no_lp + ") "
                    + "ORDER BY `tb_lab_treatment_lp`.`no_laporan_produksi` ASC";
            JRDesignQuery newQuery = new JRDesignQuery();
            newQuery.setText(sql);
            JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Traceability2\\Catatan_Abnormal_Produk_tv2.jrxml");
            JASP_DESIGN.setQuery(newQuery);
            JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
            JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, null, Utility.db.getConnection());
            JasperViewer.viewReport(JASP_PRINT, false);
        } catch (JRException ex) {
            Logger.getLogger(JPanel_Traceability2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_catatan_abnormal_produkActionPerformed

    private void button_insert_LPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_insert_LPActionPerformed
        // TODO add your handling code here:
        try {

            int n = 0;
            JFileChooser chooser = new JFileChooser();
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
                        Utility.db.getConnection().setAutoCommit(false);
                        while ((line = br.readLine()) != null) {
                            String[] value = line.split(";");
                            Query = "INSERT INTO `tb_laporan_produksi_tv2`("
                                    + "`no_laporan_produksi`, "
                                    + "`kode_rsb`, "
                                    + "`no_kartu_waleta`, "
                                    + "`kode_grade`, "
                                    + "`memo_lp_cheat`, "
                                    + "`tanggal_lp`, "
                                    + "`tgl_grading`, "
                                    + "`jumlah_keping`, "
                                    + "`berat_basah`, "
                                    + "`kode_tutupan`) "
                                    + "VALUES ("
                                    + "'" + value[0] + "',"
                                    + "'" + value[1] + "',"
                                    + "'" + value[2] + "',"
                                    + "'" + value[3] + "',"
                                    + "'" + value[4] + "',"
                                    + "'" + value[5] + "',"
                                    + "'" + value[6] + "',"
                                    + "'" + value[7] + "',"
                                    + "'" + value[8] + "',"
                                    + "'" + value[9] + "')";
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
                        Utility.db.getConnection().commit();
                        JOptionPane.showMessageDialog(this, "Data Berhasil Masuk : " + n);
                    } catch (Exception ex) {
                        try {
                            Utility.db.getConnection().rollback();
                        } catch (SQLException e) {
                            Logger.getLogger(JPanel_Traceability2.class.getName()).log(Level.SEVERE, null, e);
                        }
                        JOptionPane.showMessageDialog(this, ex);
                        Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {
                        try {
                            Utility.db.getConnection().setAutoCommit(true);
                        } catch (SQLException e) {
                            Logger.getLogger(JPanel_Traceability2.class.getName()).log(Level.SEVERE, null, e);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Traceability2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_insert_LPActionPerformed

    private void button_KH14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_KH14ActionPerformed
        // TODO add your handling code here:
        int x = Table_traceability.getSelectedRow();
        if (x < 0) {
            JOptionPane.showMessageDialog(this, "Pilih salah satu data !", "warning!", 1);
        } else {
            String file_name = Table_traceability.getValueAt(x, 0).toString() + "-" + Table_traceability.getValueAt(x, 3).toString().replace("-", "");//CT1
            file_name = file_name.replace("-20", "-");
            try {
                Runtime rt = Runtime.getRuntime();
                Process pr = rt.exec("explorer.exe \\\\192.168.10.2\\Shared Folder\\DOKUMEN SISTEM\\2_(KH-14)_Sertifikat_Pelepasan_Karantina_Hewan_Baku\\" + file_name + ".pdf");
            } catch (IOException ex) {
                Logger.getLogger(JPanel_Traceability2.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_button_KH14ActionPerformed

    private void button_HCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_HCActionPerformed
        // TODO add your handling code here:
        int x = Table_traceability2.getSelectedRow();
        if (x < 0) {
            JOptionPane.showMessageDialog(this, "Pilih salah satu data BOX !");
        } else {
            String file_name = Table_traceability2.getValueAt(x, 11).toString().replace("/", "_");
            try {
                Runtime rt = Runtime.getRuntime();
                Process pr = rt.exec("explorer.exe \\\\192.168.10.2\\Shared Folder\\DOKUMEN SISTEM\\5_Health_Certificate\\" + file_name + ".pdf");
            } catch (IOException ex) {
                Logger.getLogger(JPanel_Traceability2.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_button_HCActionPerformed

    private void button_packing_listActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_packing_listActionPerformed
        // TODO add your handling code here:
        int x = Table_traceability2.getSelectedRow();
        if (x < 0) {
            JOptionPane.showMessageDialog(this, "Pilih salah satu data BOX !");
        } else {
            String file_name = Table_traceability2.getValueAt(x, 11).toString().replace("/", "_");
            try {
                Runtime rt = Runtime.getRuntime();
                Process pr = rt.exec("explorer.exe \\\\192.168.10.2\\Shared Folder\\DOKUMEN SISTEM\\4_Packing_List\\" + file_name + ".pdf");
            } catch (IOException ex) {
                Logger.getLogger(JPanel_Traceability2.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_button_packing_listActionPerformed

    private void button_lembar_kerja_pencucianActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_lembar_kerja_pencucianActionPerformed
        // TODO add your handling code here:
        try {
            String no_lp = "";
            for (int i = 0; i < Table_traceability.getRowCount(); i++) {
                if (i != 0) {
                    no_lp = no_lp + ", ";
                }
                no_lp = no_lp + "'" + Table_traceability.getValueAt(i, 5).toString() + "'";
            }
            String Query = "SELECT `tb_laporan_produksi_tv2`.`no_laporan_produksi`, `tb_laporan_produksi_tv2`.`no_kartu_waleta`, `tb_laporan_produksi_tv2`.`kode_rsb`, `tb_laporan_produksi_tv2`.`tanggal_lp`, `tb_laporan_produksi_tv2`.`kode_grade`, `tb_laporan_produksi_tv2`.`jumlah_keping`, `tb_laporan_produksi_tv2`.`berat_basah`, `tgl_masuk_cuci`, `tb_cuci`.`id_pegawai`, `tb_karyawan`.`nama_pegawai`, `admin_cuci`, `memo_lp_cheat`, "
                    + "(SELECT MIN(`tgl_masuk_cuci`) FROM `tb_cuci` WHERE `no_laporan_produksi` IN (" + no_lp + ")) AS 'tgl_cuci1', \n"
                    + "(SELECT MAX(`tgl_masuk_cuci`) FROM `tb_cuci` WHERE `no_laporan_produksi` IN (" + no_lp + ")) AS 'tgl_cuci2'"
                    + "FROM `tb_laporan_produksi_tv2` \n"
                    + "LEFT JOIN `tb_cuci` ON `tb_cuci`.`no_laporan_produksi` = `tb_laporan_produksi_tv2`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_karyawan` ON `tb_cuci`.`id_pegawai` = `tb_karyawan`.`id_pegawai` \n"
                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_cuci`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`"
                    + "WHERE `tb_laporan_produksi_tv2`.`no_laporan_produksi` IN (" + no_lp + ") "
                    + "ORDER BY `tb_laporan_produksi_tv2`.`no_laporan_produksi` ASC";
//            System.out.println(Query);
            JRDesignQuery newQuery = new JRDesignQuery();
            newQuery.setText(Query);
            JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Traceability2\\Lembar_Kerja_Pencucian_tv2.jrxml");
            JASP_DESIGN.setQuery(newQuery);
            Map<String, Object> map = new HashMap<>();
            map.put("REPORT_MAX_COUNT", Table_traceability.getRowCount());
            JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
            JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, map, Utility.db.getConnection());
            JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
        } catch (JRException ex) {
            Logger.getLogger(JPanel_Traceability2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_lembar_kerja_pencucianActionPerformed

    private void button_catatan_pemeriksaan_kebersihan_sarang_walet_selama_prosesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_catatan_pemeriksaan_kebersihan_sarang_walet_selama_prosesActionPerformed
        // TODO add your handling code here:
        try {
            String no_lp = "", tanggal = "";
            for (int i = 0; i < Table_traceability.getRowCount(); i++) {
                if (i != 0) {
                    no_lp = no_lp + ", ";
                }
                no_lp = no_lp + "'" + Table_traceability.getValueAt(i, 5).toString() + "'";
            }
            try {
                sql = "SELECT MIN(`tgl_setor_f2`) AS 'date1', MAX(`tgl_setor_f2`) AS 'date2' "
                        + "FROM `tb_finishing_2` "
                        + "WHERE `tb_finishing_2`.`no_laporan_produksi` IN (" + no_lp + ") ";
                ResultSet rs = Utility.db.getStatement().executeQuery(sql);
                if (rs.next()) {
                    if (!(rs.getDate("date1") == null || rs.getDate("date2") == null)) {
                        tanggal = new SimpleDateFormat("dd MMM yyy").format(rs.getDate("date1")) + " - " + new SimpleDateFormat("dd MMM yyy").format(rs.getDate("date2"));
                    }
                }
            } catch (SQLException e) {
                Logger.getLogger(JPanel_Traceability2.class.getName()).log(Level.SEVERE, null, e);
            }
            String Query = "SELECT `tb_finishing_2`.`no_laporan_produksi`, `tgl_setor_f2`, `f2_disetor`, `tb_laporan_produksi_tv2`.`no_kartu_waleta`, `tb_laporan_produksi_tv2`.`kode_rsb`, `ruangan`, `fbonus_f2` , `fnol_f2` , `pecah_f2` , `flat_f2` , `jidun_utuh_f2` , `jidun_pecah_f2`, `tb_laporan_produksi_tv2`.`jumlah_keping`\n"
                    + "FROM `tb_finishing_2`\n"
                    + "LEFT JOIN `tb_laporan_produksi_tv2` ON `tb_finishing_2`.`no_laporan_produksi` = `tb_laporan_produksi_tv2`.`no_laporan_produksi` "
                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_finishing_2`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi` "
                    + "WHERE `tb_finishing_2`.`no_laporan_produksi` IN (" + no_lp + ") "
                    + "ORDER BY `tb_finishing_2`.`tgl_masuk_f2` DESC";
//            System.out.println(Query);
            JRDesignQuery newQuery = new JRDesignQuery();
            newQuery.setText(Query);
            JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Traceability2\\Catatan_Pemeriksaan_Kebersihan_Sarang_Walet_Selama_Proses_tv2.jrxml");
            JASP_DESIGN.setQuery(newQuery);
            Map<String, Object> map = new HashMap<>();
            map.put("tanggal", tanggal);
            JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
            JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, map, Utility.db.getConnection());
            JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
        } catch (JRException ex) {
            Logger.getLogger(JPanel_Traceability2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_catatan_pemeriksaan_kebersihan_sarang_walet_selama_prosesActionPerformed

    private void button_KH12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_KH12ActionPerformed
        // TODO add your handling code here:
        int x = Table_traceability2.getSelectedRow();
        if (x < 0) {
            JOptionPane.showMessageDialog(this, "Pilih salah satu data BOX !");
        } else {
            String file_name = Table_traceability2.getValueAt(x, 11).toString().replace("/", "_");
            try {
                Runtime rt = Runtime.getRuntime();
                Process pr = rt.exec("explorer.exe \\\\192.168.10.2\\Shared Folder\\DOKUMEN SISTEM\\3_(KH-12)_Sertifikat_Sanitasi_Produk_Hewan_Ekspor\\" + file_name + ".pdf");
            } catch (IOException ex) {
                Logger.getLogger(JPanel_Traceability2.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_button_KH12ActionPerformed

    private void txt_search_rsbKartu_CT2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_rsbKartu_CT2KeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable1();
        }
    }//GEN-LAST:event_txt_search_rsbKartu_CT2KeyPressed

    private void txt_no_kartu_CT2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_no_kartu_CT2KeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable1();
        }
    }//GEN-LAST:event_txt_no_kartu_CT2KeyPressed

    private void button_catatan_pemeriksaan_produk_jadiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_catatan_pemeriksaan_produk_jadiActionPerformed
        // TODO add your handling code here:
        try {
            String no_box = "";
            for (int i = 0; i < Table_traceability2.getRowCount(); i++) {
                if (Table_traceability2.getValueAt(i, 3) != null && !Table_traceability2.getValueAt(i, 3).toString().equals("")) {
                    if (!no_box.equals("")) {
                        no_box = no_box + ", ";
                    }
                    no_box = no_box + "'" + Table_traceability2.getValueAt(i, 3).toString() + "'";
                }
            }
            String Query = "SELECT `tb_grade_bahan_jadi`.`kode_grade`, `tb_lab_pemeriksaan_pengiriman`.`no_box`, `no_box_ct1`, `kode_rsb`, `tb_box_bahan_jadi`.`keping`, `tb_box_bahan_jadi`.`berat`, `tb_lab_pemeriksaan_pengiriman`.`tgl_uji`, `kadar_air`, `nitrit`\n"
                    + "FROM `tb_lab_pemeriksaan_pengiriman` \n"
                    + "LEFT JOIN `tb_box_packing` ON `tb_box_packing`.`no_box` = `tb_lab_pemeriksaan_pengiriman`.`no_box`\n"
                    + "LEFT JOIN `tb_box_bahan_jadi` ON `tb_box_packing`.`no_box` = `tb_box_bahan_jadi`.`no_box`\n"
                    + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_box_bahan_jadi`.`kode_grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode` "
                    + "WHERE `no_box_ct1` IN (" + no_box + ") "
                    + "ORDER BY `no_box_ct1`";
//            System.out.println(Query);
            JRDesignQuery newQuery = new JRDesignQuery();
            newQuery.setText(Query);
            JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Traceability2\\Catatan_Pemeriksaan_Produk_Jadi_tv2.jrxml");
            JASP_DESIGN.setQuery(newQuery);
            JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
            JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, null, Utility.db.getConnection());
            JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
        } catch (JRException ex) {
            Logger.getLogger(JPanel_Traceability2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_catatan_pemeriksaan_produk_jadiActionPerformed

    private void button_LabelBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_LabelBoxActionPerformed
        // TODO add your handling code here:
        int x = Table_traceability2.getSelectedRow();
        if (x < 0) {
            JOptionPane.showMessageDialog(this, "Pilih salah satu data BOX !");
        } else {
            String file_name = Table_traceability2.getValueAt(x, 11).toString().replace("/", "_");
            try {
                Runtime rt = Runtime.getRuntime();
                Process pr = rt.exec("explorer.exe \\\\192.168.10.2\\Shared Folder\\DOKUMEN SISTEM\\7_Label_Box\\" + file_name + ".pdf");
            } catch (IOException ex) {
                Logger.getLogger(JPanel_Traceability2.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_button_LabelBoxActionPerformed

    private void button_catatan_pengeringan_sarang_burungActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_catatan_pengeringan_sarang_burungActionPerformed
        // TODO add your handling code here:
        try {
            String no_lp = "", tanggal = "";
            for (int i = 0; i < Table_traceability.getRowCount(); i++) {
                if (i != 0) {
                    no_lp = no_lp + ", ";
                }
                no_lp = no_lp + "'" + Table_traceability.getValueAt(i, 5).toString() + "'";
            }
            try {
                sql = "SELECT MIN(`tgl_mulai_cetak`) AS 'date1', MAX(`tgl_selesai_cetak`) AS 'date2' "
                        + "FROM `tb_cetak` "
                        + "WHERE `tb_cetak`.`no_laporan_produksi` IN (" + no_lp + ") ";
                ResultSet rs = Utility.db.getStatement().executeQuery(sql);
                if (rs.next()) {
                    if (!(rs.getDate("date1") == null || rs.getDate("date2") == null)) {
                        tanggal = new SimpleDateFormat("MMMM").format(rs.getDate("date1"));
//                        tanggal = new SimpleDateFormat("dd MMM yyy").format(rs.getDate("date1")) + " - " + new SimpleDateFormat("dd MMM yyy").format(rs.getDate("date2"));
                    }
                }
            } catch (SQLException e) {
                Logger.getLogger(JPanel_Traceability2.class.getName()).log(Level.SEVERE, null, e);
            }
            String Query = "SELECT `tb_cetak`.`no_laporan_produksi`, LP.`kode_grade`, `tgl_selesai_cetak`, `cetak_diterima`, `cetak_diserahkan`,  `tb_laporan_produksi`.`ruangan`, LP.`jumlah_keping`, LP.`berat_basah`, LP.`no_kartu_waleta`, LP.`kode_rsb`, \n"
                    + "GRD.`gram_utuh`, GRD.`gram_flat`, GRD.`gram_jidun`, `waktu_mulai_pengeringan`, `waktu_selesai_pengeringan` \n"
                    + "FROM `tb_cetak` \n"
                    + "LEFT JOIN `tb_laporan_produksi_tv2` LP ON `tb_cetak`.`no_laporan_produksi` = LP.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_cetak`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    + "LEFT JOIN (SELECT `no_laporan_produksi`, \n"
                    + "SUM(IF(`bentuk_grade`='Utuh', `gram`, 0)) AS 'gram_utuh', \n"
                    + "SUM(IF(`bentuk_grade`='Flat', `gram`, 0)) AS 'gram_flat',\n"
                    + "SUM(IF(`bentuk_grade`='Jidun', `gram`, 0)) AS 'gram_jidun'\n"
                    + "FROM `tb_grading_bahan_jadi_tv2` \n"
                    + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_grading_bahan_jadi_tv2`.`grade_barang_jadi` = `tb_grade_bahan_jadi`.`kode`\n"
                    + "WHERE 1 GROUP BY `no_laporan_produksi`) GRD ON LP.`no_laporan_produksi` = GRD.`no_laporan_produksi`\n"
                    + "WHERE `tb_cetak`.`no_laporan_produksi` IN (" + no_lp + ")\n"
                    + "ORDER BY `ruangan`, `tb_cetak`.`no_laporan_produksi` ASC";
//            System.out.println(Query);
            JRDesignQuery newQuery = new JRDesignQuery();
            newQuery.setText(Query);
            JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Traceability2\\Catatan_Pengeringan_Sarang_Burung_tv2.jrxml");
            JASP_DESIGN.setQuery(newQuery);
            Map<String, Object> map = new HashMap<>();
            map.put("TGL_SETOR_CETAK", tanggal);
            JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
            JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, map, Utility.db.getConnection());
            JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
        } catch (JRException ex) {
            Logger.getLogger(JPanel_Traceability2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_catatan_pengeringan_sarang_burungActionPerformed

    private void txt_search_tutupan_ctKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_tutupan_ctKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable2();
        }
    }//GEN-LAST:event_txt_search_tutupan_ctKeyPressed

    private void button_print_cacatan_penyimpanan_barang_jadiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_print_cacatan_penyimpanan_barang_jadiActionPerformed
        // TODO add your handling code here:
        try {
            String no_box = "";
            for (int i = 0; i < Table_traceability2.getRowCount(); i++) {
                if (Table_traceability2.getValueAt(i, 3) != null && !Table_traceability2.getValueAt(i, 3).toString().equals("")) {
                    if (!no_box.equals("")) {
                        no_box = no_box + ", ";
                    }
                    no_box = no_box + "'" + Table_traceability2.getValueAt(i, 3).toString() + "'";
                }
            }
            String query = "SELECT `no_box_ct1` AS 'no_box', `tanggal_box`, `keping`, `berat`, `kode_grade`, `tb_box_bahan_jadi`.`kode_rsb`, \n"
                    + "(SELECT `no_kartu_waleta` FROM `tb_bahan_baku_masuk_cheat` WHERE `kode_kh` = `tb_dokumen_kh`.`kode_kh` ORDER BY `no_kartu_waleta` LIMIT 1) AS 'no_kartu_waleta'\n"
                    + "FROM `tb_box_bahan_jadi`\n"
                    + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_box_bahan_jadi`.`kode_grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode`\n"
                    + "LEFT JOIN `tb_dokumen_kh` ON `tb_box_bahan_jadi`.`kode_kh` = `tb_dokumen_kh`.`kode_kh`\n"
                    + "WHERE `no_box_ct1` IN (" + no_box + ")";
            JRDesignQuery newQuery = new JRDesignQuery();
            newQuery.setText(query);
            JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Label_Box_QR.jrxml");
            JASP_DESIGN.setQuery(newQuery);
            JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("SUBREPORT_DIR", "Report\\");
            params.put("JUDUL_LABEL", "Catatan Penyimpanan Barang Jadi");
            JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, params, Utility.db.getConnection());
            JasperViewer.viewReport(JASP_PRINT, false);

            String query2 = "SELECT `no_box_ct1` AS 'no_box', `tanggal_box`, `keping`, `berat`, `kode_grade`, `tb_box_bahan_jadi`.`kode_rsb`, `batch_number`  "
                    + "FROM `tb_box_bahan_jadi`\n"
                    + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_box_bahan_jadi`.`kode_grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode`"
                    + "LEFT JOIN `tb_dokumen_kh` ON `tb_box_bahan_jadi`.`kode_kh` = `tb_dokumen_kh`.`kode_kh`\n"
                    + "LEFT JOIN `tb_box_packing` ON `tb_box_bahan_jadi`.`no_box` = `tb_box_packing`.`no_box`\n"
                    + "WHERE `no_box_ct1` IN (" + no_box + ")";
            JRDesignQuery newQuery2 = new JRDesignQuery();
            newQuery2.setText(query2);
            JasperDesign JASP_DESIGN2 = JRXmlLoader.load("Report\\Label_Box_QR_Packing.jrxml");
            JASP_DESIGN2.setQuery(newQuery2);
            JasperReport JASP_REP2 = JasperCompileManager.compileReport(JASP_DESIGN2);
            Map<String, Object> params2 = new HashMap<>();
            JasperPrint JASP_PRINT2 = JasperFillManager.fillReport(JASP_REP2, params2, Utility.db.getConnection());
            JasperViewer.viewReport(JASP_PRINT2, false);
        } catch (JRException ex) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_print_cacatan_penyimpanan_barang_jadiActionPerformed

    private void button_insert_hasil_gradingLPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_insert_hasil_gradingLPActionPerformed
        // TODO add your handling code here:
        try {

            int n = 0;
            JFileChooser chooser = new JFileChooser();
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
                        Utility.db.getConnection().setAutoCommit(false);
                        while ((line = br.readLine()) != null) {
                            String[] value = line.split(";");
                            Query = "INSERT INTO `tb_grading_bahan_jadi_tv2`(`no_laporan_produksi`, `grade_barang_jadi`, `keping`, `gram`) "
                                    + "VALUES ('" + value[0].trim() + "','" + value[1].trim() + "','" + value[2].trim() + "','" + value[3].trim() + "')"
                                    + "ON DUPLICATE KEY UPDATE `keping` = '" + value[2].trim() + "', `gram` = '" + value[3].trim() + "'";
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
                        Utility.db.getConnection().commit();
                    } catch (Exception ex) {
                        try {
                            Utility.db.getConnection().rollback();
                        } catch (SQLException e) {
                            Logger.getLogger(JPanel_Traceability2.class.getName()).log(Level.SEVERE, null, e);
                        }
                        JOptionPane.showMessageDialog(this, "INPUT GAGAL : " + ex);
                        Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {
                        JOptionPane.showMessageDialog(this, "Data Berhasil Masuk : " + n);
                        try {
                            Utility.db.getConnection().setAutoCommit(true);
                        } catch (SQLException e) {
                            Logger.getLogger(JPanel_Traceability2.class.getName()).log(Level.SEVERE, null, e);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Traceability2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_insert_hasil_gradingLPActionPerformed

    private void button_delete_lp_1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_delete_lp_1ActionPerformed
        // TODO add your handling code here:
        try {
            int j = Table_data_LP.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih data yang mau dihapus !");
            } else {
                String no_lp = Table_data_LP.getValueAt(j, 1).toString();
                int dialogResult = JOptionPane.showConfirmDialog(this, "Delete " + no_lp + " ?", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    sql = "DELETE FROM `tb_laporan_produksi_tv2` WHERE `no_laporan_produksi` = '" + no_lp + "'";
                    Utility.db.getConnection().createStatement();
                    if ((Utility.db.getStatement().executeUpdate(sql)) == 1) {
                        JOptionPane.showMessageDialog(this, "data Deleted !");
                        refreshTable_dataLP();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed!");
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
        }
    }//GEN-LAST:event_button_delete_lp_1ActionPerformed

    private void button_delete_lp_semuaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_delete_lp_semuaActionPerformed
        // TODO add your handling code here:
        try {
            String no_lp = "";
            for (int i = 0; i < Table_data_LP.getRowCount(); i++) {
                if (i != 0) {
                    no_lp = no_lp + ", ";
                }
                no_lp = no_lp + "'" + Table_data_LP.getValueAt(i, 1).toString() + "'";
            }
            int dialogResult = JOptionPane.showConfirmDialog(this, "Delete Semua Data LP?", "Warning", 0);
            if (dialogResult == JOptionPane.YES_OPTION) {
                sql = "DELETE FROM `tb_laporan_produksi_tv2` WHERE `no_laporan_produksi` IN (" + no_lp + ")";
                Utility.db.getConnection().createStatement();
                if ((Utility.db.getStatement().executeUpdate(sql)) > 1) {
                    JOptionPane.showMessageDialog(this, "data Deleted !");
                    refreshTable_dataLP();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed!");
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
        }
    }//GEN-LAST:event_button_delete_lp_semuaActionPerformed

    private void button_edit_rsb_tutupanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_edit_rsb_tutupanActionPerformed
        // TODO add your handling code here:
        try {
            int j = Table_traceability2.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih salah satu No Tutupan", "warning!", 1);
            } else {
                String kode_rsb_baru = JOptionPane.showInputDialog("Silahkan masukkan kode RSB baru (3/4 digit) : ");
                if (kode_rsb_baru != null && !kode_rsb_baru.equals("")) {
                    int dialogResult = JOptionPane.showConfirmDialog(this, "Edit kode RSB tutupan " + Table_traceability2.getValueAt(j, 2).toString() + " menjadi " + kode_rsb_baru + ", lanjutkan ??", "Warning", 0);
                    if (dialogResult == JOptionPane.YES_OPTION) {
                        String Query = "UPDATE `tb_tutupan_grading` SET `kode_rumah_burung`='" + kode_rsb_baru + "' WHERE `kode_tutupan` = '" + Table_traceability2.getValueAt(j, 2).toString() + "'";
                        if ((Utility.db.getStatement().executeUpdate(Query)) == 1) {
                            JOptionPane.showMessageDialog(this, "Edit kode rsb tutupan berhasil");
                        } else {
                            JOptionPane.showMessageDialog(this, "Tidak ada perubahan data");
                        }

                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getLocalizedMessage());
            Logger.getLogger(JPanel_Traceability2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_edit_rsb_tutupanActionPerformed

    private void button_cheat_kode_tutupanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_cheat_kode_tutupanActionPerformed
        // TODO add your handling code here:
        String input = JOptionPane.showInputDialog("Masukkan Cheat Kode Tutupan :");
        if (input != null && !input.equals("")) {
//            try {
//                String query = "SELECT `kode_tutupan` FROM `tb_tutupan_grading` WHERE `kode_tutupan` = '" + input + "' ";
//                ResultSet rs = Utility.db.getStatement().executeQuery(query);
//                if (rs.next()) {
            for (int i = 0; i < Table_traceability2.getRowCount(); i++) {
                try {
                    if (Table_traceability2.getValueAt(i, 3) != null) {
                        sql = "UPDATE `tb_box_bahan_jadi` SET `no_tutupan_ct1` = '" + input + "'"
                                + "WHERE `no_box_ct1` = '" + Table_traceability2.getValueAt(i, 3).toString() + "'";
                        Utility.db.getStatement().executeUpdate(sql);
                    }
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, e);
                    Logger.getLogger(JPanel_Traceability2.class.getName()).log(Level.SEVERE, null, e);
                }
            }
            JOptionPane.showMessageDialog(this, "data Saved");
//                } else {
//                    JOptionPane.showMessageDialog(this, "Maaf kode_tutupan tidak ada dalam data tutupan !");
//                }
//            } catch (SQLException ex) {
//                Logger.getLogger(JPanel_Traceability.class.getName()).log(Level.SEVERE, null, ex);
//            }
        } else {
            int dialogResult = JOptionPane.showConfirmDialog(this, "Hapus data cheat grading tutupan??", "Warning", 0);
            if (dialogResult == JOptionPane.YES_OPTION) {
                for (int i = 0; i < Table_traceability2.getRowCount(); i++) {
                    try {
                        if (Table_traceability2.getValueAt(i, 3) != null) {
                            sql = "UPDATE `tb_box_bahan_jadi` SET `no_tutupan_ct1` = NULL "
                                    + "WHERE `no_box_ct1` = '" + Table_traceability2.getValueAt(i, 3).toString() + "'";
                            Utility.db.getStatement().executeUpdate(sql);
                        }
                    } catch (SQLException e) {
                        JOptionPane.showMessageDialog(this, e);
                        Logger.getLogger(JPanel_Traceability2.class.getName()).log(Level.SEVERE, null, e);
                    }
                }
            }
        }
        refreshTable2();
    }//GEN-LAST:event_button_cheat_kode_tutupanActionPerformed

    private void txt_search_lp_dataLPKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_lp_dataLPKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_dataLP();
        }
    }//GEN-LAST:event_txt_search_lp_dataLPKeyPressed

    private void txt_search_no_kartu_dataLPKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_no_kartu_dataLPKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_dataLP();
        }
    }//GEN-LAST:event_txt_search_no_kartu_dataLPKeyPressed

    private void txt_search_kodeRSB_dataLPKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_kodeRSB_dataLPKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_dataLP();
        }
    }//GEN-LAST:event_txt_search_kodeRSB_dataLPKeyPressed

    private void txt_search_tutupan_dataLPKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_tutupan_dataLPKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_dataLP();
        }
    }//GEN-LAST:event_txt_search_tutupan_dataLPKeyPressed

    private void Button_Search_dataLPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_Search_dataLPActionPerformed
        // TODO add your handling code here:
        refreshTable_dataLP();
    }//GEN-LAST:event_Button_Search_dataLPActionPerformed

    private void button_delete_all_hasil_gradingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_delete_all_hasil_gradingActionPerformed
        // TODO add your handling code here:
        try {
            String no_lp = "";
            for (int i = 0; i < Table_data_LP.getRowCount(); i++) {
                if (i != 0) {
                    no_lp = no_lp + ", ";
                }
                no_lp = no_lp + "'" + Table_data_LP.getValueAt(i, 1).toString() + "'";
            }
            int dialogResult = JOptionPane.showConfirmDialog(this, "Delete Semua Data Hasil Grading LP?", "Warning", 0);
            if (dialogResult == JOptionPane.YES_OPTION) {
                sql = "DELETE FROM `tb_grading_bahan_jadi_tv2` WHERE `no_laporan_produksi` IN (" + no_lp + ")";
                Utility.db.getConnection().createStatement();
                if ((Utility.db.getStatement().executeUpdate(sql)) > 1) {
                    JOptionPane.showMessageDialog(this, "data Deleted !");
                    refreshTable_dataLP();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed!");
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
        }
    }//GEN-LAST:event_button_delete_all_hasil_gradingActionPerformed

    private void button_lp_kosongActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_lp_kosongActionPerformed
        // TODO add your handling code here:
        try {
            JasperPrint JASP_PRINT = null;
            int target_utuh = 0, target_pch = 0, target_sp = 0, target_sh = 0;
            String no_lp = "";
            for (int i = 0; i < Table_traceability.getRowCount(); i++) {
                if (i != 0) {
                    no_lp = no_lp + ", ";
                }
                no_lp = no_lp + "'" + Table_traceability.getValueAt(i, 5).toString() + "'";
            }
            sql = "SELECT `no_laporan_produksi`, `tb_laporan_produksi_tv2`.`kode_grade`, `tb_grade_bahan_baku`.`jenis_bentuk`, `tb_grade_bahan_baku`.`jenis_bulu` \n"
                    + "FROM `tb_laporan_produksi_tv2` LEFT JOIN `tb_grade_bahan_baku` ON `tb_laporan_produksi_tv2`.`kode_grade` = `tb_grade_bahan_baku`.`kode_grade` "
                    + "WHERE `no_laporan_produksi` = '" + Table_traceability.getValueAt(0, 5).toString() + "' ";
            ResultSet rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                if (rs.getString("jenis_bentuk").contains("Mangkok")) {
                    switch (rs.getString("jenis_bulu")) {
                        case "Bulu Ringan":
                            target_utuh = 70;
                            target_pch = 10;
                            target_sp = 14;
                            target_sh = 6;
                            break;
                        case "Bulu Ringan Sekali/Bulu Ringan":
                            target_utuh = 70;
                            target_pch = 10;
                            target_sp = 14;
                            target_sh = 6;
                            break;
                        case "Bulu Sedang":
                            target_utuh = 60;
                            target_pch = 15;
                            target_sp = 17;
                            target_sh = 8;
                            break;
                        case "Bulu Berat":
                            target_utuh = 45;
                            target_pch = 25;
                            target_sp = 20;
                            target_sh = 10;
                            break;
                        case "Bulu Berat Sekali":
                            target_utuh = 45;
                            target_pch = 25;
                            target_sp = 20;
                            target_sh = 10;
                            break;
                        default:
                            target_utuh = 0;
                            target_pch = 0;
                            target_sp = 0;
                            target_sh = 0;
                            break;
                    }
                } else if (rs.getString("jenis_bentuk").contains("Oval") || rs.getString("jenis_bentuk").contains("Segitiga")) {
                    switch (rs.getString("jenis_bulu")) {
                        case "Bulu Ringan":
                            target_utuh = 65;
                            target_pch = 15;
                            target_sp = 14;
                            target_sh = 6;
                            break;
                        case "Bulu Ringan Sekali/Bulu Ringan":
                            target_utuh = 65;
                            target_pch = 15;
                            target_sp = 14;
                            target_sh = 6;
                            break;
                        case "Bulu Sedang":
                            target_utuh = 55;
                            target_pch = 20;
                            target_sp = 17;
                            target_sh = 8;
                            break;
                        case "Bulu Berat":
                            target_utuh = 45;
                            target_pch = 30;
                            target_sp = 20;
                            target_sh = 10;
                            break;
                        case "Bulu Berat Sekali":
                            target_utuh = 45;
                            target_pch = 30;
                            target_sp = 20;
                            target_sh = 10;
                            break;
                        default:
                            target_utuh = 0;
                            target_pch = 0;
                            target_sp = 0;
                            target_sh = 0;
                            break;
                    }
                } else if (rs.getString("jenis_bentuk").contains("Pecah")) {
                    switch (rs.getString("jenis_bulu")) {
                        case "Bulu Ringan":
                            target_utuh = 0;
                            target_pch = 75;
                            target_sp = 19;
                            target_sh = 6;
                            break;
                        case "Bulu Ringan Sekali/Bulu Ringan":
                            target_utuh = 0;
                            target_pch = 75;
                            target_sp = 19;
                            target_sh = 6;
                            break;
                        case "Bulu Sedang":
                            target_utuh = 0;
                            target_pch = 70;
                            target_sp = 22;
                            target_sh = 8;
                            break;
                        case "Bulu Berat":
                            target_utuh = 0;
                            target_pch = 65;
                            target_sp = 25;
                            target_sh = 10;
                            break;
                        case "Bulu Berat Sekali":
                            target_utuh = 0;
                            target_pch = 65;
                            target_sp = 25;
                            target_sh = 10;
                            break;
                        default:
                            target_utuh = 0;
                            target_pch = 0;
                            target_sp = 0;
                            target_sh = 0;
                            break;
                    }
                } else {
                    switch (rs.getString("jenis_bulu")) {
                        case "Bulu Ringan":
                            target_utuh = 0;
                            target_pch = 75;
                            target_sp = 19;
                            target_sh = 6;
                            break;
                        case "Bulu Ringan Sekali/Bulu Ringan":
                            target_utuh = 0;
                            target_pch = 75;
                            target_sp = 19;
                            target_sh = 6;
                            break;
                        case "Bulu Sedang":
                            target_utuh = 0;
                            target_pch = 70;
                            target_sp = 22;
                            target_sh = 8;
                            break;
                        case "Bulu Berat":
                            target_utuh = 0;
                            target_pch = 65;
                            target_sp = 25;
                            target_sh = 10;
                            break;
                        case "Bulu Berat Sekali":
                            target_utuh = 0;
                            target_pch = 65;
                            target_sp = 25;
                            target_sh = 10;
                            break;
                        default:
                            target_utuh = 0;
                            target_pch = 0;
                            target_sp = 0;
                            target_sh = 0;
                            break;
                    }
                }
                JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Traceability2\\laporan_produksi_cheat_kosong\\Laporan_Produksi_QR_tv2.jrxml");
                JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("PARAM_NO_LP", rs.getString("no_laporan_produksi"));
                params.put("target_utuh", target_utuh);
                params.put("target_pch", target_pch);
                params.put("target_sp", target_sp);
                params.put("target_sh", target_sh);
                params.put("parameterIsKosong", false);
                params.put("parameterHalaman", 1);
                params.put("parameterJumlahHalaman", 1);
                params.put("SUBREPORT_DIR", "Report\\Traceability2\\laporan_produksi_cheat_kosong\\");
                JASP_PRINT = JasperFillManager.fillReport(JASP_REP, params, Utility.db.getConnection());
            }

            sql = "SELECT `no_laporan_produksi`, `tb_laporan_produksi_tv2`.`kode_grade`, `tb_grade_bahan_baku`.`jenis_bentuk`, `tb_grade_bahan_baku`.`jenis_bulu` \n"
                    + "FROM `tb_laporan_produksi_tv2` LEFT JOIN `tb_grade_bahan_baku` ON `tb_laporan_produksi_tv2`.`kode_grade` = `tb_grade_bahan_baku`.`kode_grade` "
                    + "WHERE `no_laporan_produksi` IN (" + no_lp + ") ";
            rs = Utility.db.getStatement().executeQuery(sql);
            rs.next();
            while (rs.next()) {
                if (rs.getString("jenis_bentuk").contains("Mangkok")) {
                    switch (rs.getString("jenis_bulu")) {
                        case "Bulu Ringan":
                            target_utuh = 70;
                            target_pch = 10;
                            target_sp = 14;
                            target_sh = 6;
                            break;
                        case "Bulu Ringan Sekali/Bulu Ringan":
                            target_utuh = 70;
                            target_pch = 10;
                            target_sp = 14;
                            target_sh = 6;
                            break;
                        case "Bulu Sedang":
                            target_utuh = 60;
                            target_pch = 15;
                            target_sp = 17;
                            target_sh = 8;
                            break;
                        case "Bulu Berat":
                            target_utuh = 45;
                            target_pch = 25;
                            target_sp = 20;
                            target_sh = 10;
                            break;
                        case "Bulu Berat Sekali":
                            target_utuh = 45;
                            target_pch = 25;
                            target_sp = 20;
                            target_sh = 10;
                            break;
                        default:
                            target_utuh = 0;
                            target_pch = 0;
                            target_sp = 0;
                            target_sh = 0;
                            break;
                    }
                } else if (rs.getString("jenis_bentuk").contains("Oval") || rs.getString("jenis_bentuk").contains("Segitiga")) {
                    switch (rs.getString("jenis_bulu")) {
                        case "Bulu Ringan":
                            target_utuh = 65;
                            target_pch = 15;
                            target_sp = 14;
                            target_sh = 6;
                            break;
                        case "Bulu Ringan Sekali/Bulu Ringan":
                            target_utuh = 65;
                            target_pch = 15;
                            target_sp = 14;
                            target_sh = 6;
                            break;
                        case "Bulu Sedang":
                            target_utuh = 55;
                            target_pch = 20;
                            target_sp = 17;
                            target_sh = 8;
                            break;
                        case "Bulu Berat":
                            target_utuh = 45;
                            target_pch = 30;
                            target_sp = 20;
                            target_sh = 10;
                            break;
                        case "Bulu Berat Sekali":
                            target_utuh = 45;
                            target_pch = 30;
                            target_sp = 20;
                            target_sh = 10;
                            break;
                        default:
                            target_utuh = 0;
                            target_pch = 0;
                            target_sp = 0;
                            target_sh = 0;
                            break;
                    }
                } else if (rs.getString("jenis_bentuk").contains("Pecah")) {
                    switch (rs.getString("jenis_bulu")) {
                        case "Bulu Ringan":
                            target_utuh = 0;
                            target_pch = 75;
                            target_sp = 19;
                            target_sh = 6;
                            break;
                        case "Bulu Ringan Sekali/Bulu Ringan":
                            target_utuh = 0;
                            target_pch = 75;
                            target_sp = 19;
                            target_sh = 6;
                            break;
                        case "Bulu Sedang":
                            target_utuh = 0;
                            target_pch = 70;
                            target_sp = 22;
                            target_sh = 8;
                            break;
                        case "Bulu Berat":
                            target_utuh = 0;
                            target_pch = 65;
                            target_sp = 25;
                            target_sh = 10;
                            break;
                        case "Bulu Berat Sekali":
                            target_utuh = 0;
                            target_pch = 65;
                            target_sp = 25;
                            target_sh = 10;
                            break;
                        default:
                            target_utuh = 0;
                            target_pch = 0;
                            target_sp = 0;
                            target_sh = 0;
                            break;
                    }
                } else {
                    switch (rs.getString("jenis_bulu")) {
                        case "Bulu Ringan":
                            target_utuh = 0;
                            target_pch = 75;
                            target_sp = 19;
                            target_sh = 6;
                            break;
                        case "Bulu Ringan Sekali/Bulu Ringan":
                            target_utuh = 0;
                            target_pch = 75;
                            target_sp = 19;
                            target_sh = 6;
                            break;
                        case "Bulu Sedang":
                            target_utuh = 0;
                            target_pch = 70;
                            target_sp = 22;
                            target_sh = 8;
                            break;
                        case "Bulu Berat":
                            target_utuh = 0;
                            target_pch = 65;
                            target_sp = 25;
                            target_sh = 10;
                            break;
                        case "Bulu Berat Sekali":
                            target_utuh = 0;
                            target_pch = 65;
                            target_sp = 25;
                            target_sh = 10;
                            break;
                        default:
                            target_utuh = 0;
                            target_pch = 0;
                            target_sp = 0;
                            target_sh = 0;
                            break;
                    }
                }

                JasperDesign JASP_DESIGN2 = JRXmlLoader.load("Report\\Traceability2\\laporan_produksi_cheat_kosong\\Laporan_Produksi_QR_tv2.jrxml");
                JasperReport JASP_REP2 = JasperCompileManager.compileReport(JASP_DESIGN2);
                Map<String, Object> params2 = new HashMap<String, Object>();
                params2.put("PARAM_NO_LP", rs.getString("no_laporan_produksi"));
                params2.put("target_utuh", target_utuh);
                params2.put("target_pch", target_pch);
                params2.put("target_sp", target_sp);
                params2.put("target_sh", target_sh);
                params2.put("parameterIsKosong", false);
                params2.put("SUBREPORT_DIR", "Report\\Traceability2\\laporan_produksi_cheat_kosong\\");
                JasperPrint JASP_PRINT2 = JasperFillManager.fillReport(JASP_REP2, params2, Utility.db.getConnection());
                List pages = JASP_PRINT2.getPages();
                for (int k = 0; k < pages.size(); k++) {
                    JRPrintPage object = (JRPrintPage) pages.get(k);
                    JASP_PRINT.addPage(object);
                }
            }
            JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getLocalizedMessage());
            Logger.getLogger(JPanel_Traceability2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_lp_kosongActionPerformed

    private void txt_search_spkKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_spkKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable2();
        }
    }//GEN-LAST:event_txt_search_spkKeyPressed

    private void button_print_lp_semuaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_print_lp_semuaActionPerformed
        // TODO add your handling code here:
        try {
            if (Table_traceability.getRowCount() < 0) {
                JOptionPane.showMessageDialog(this, "Tidak ada data pada tabel", "warning!", 1);
            } else {
                String no_lp = "";
                for (int i = 0; i < Table_traceability.getRowCount(); i++) {
                    if (i != 0) {
                        no_lp = no_lp + ", ";
                    }
                    no_lp = no_lp + "'" + Table_traceability.getValueAt(i, 5).toString() + "'";
                }
                String query = "SELECT CT.`no_laporan_produksi`, CT.`kode_rsb` AS 'no_registrasi', CT.`no_kartu_waleta`, CT.`tanggal_lp`, CT.`kode_grade`, `tb_laporan_produksi`.`ruangan`, CT.`memo_lp_cheat`, CT.`jumlah_keping`, CT.`berat_basah`, "
                        + "CT.`berat_basah` * (1 - (`tb_bahan_baku_masuk_cheat`.`kadar_air_bahan_baku` / 100)) AS `berat_kering`, `tb_laporan_produksi`.`jenis_bulu_lp`, `target_ctk_mku`,\n"
                        + "`tgl_input_byProduct`, `tgl_setor_f2`, `f2_disetor`, `pekerja_f2`,\n"
                        + "GRD.`Kpg_Utuh`, GRD.`Kpg_Pecah`, GRD.`Kpg_Flat`, GRD.`Kpg_Jidun`,\n"
                        + "GRD.`Gram_Utuh`, GRD.`Gram_Pecah`, GRD.`Gram_Flat`, GRD.`Gram_Jidun`,\n"
                        + "GRD.`Sesekan`, GRD.`Hancuran`, GRD.`Rontokan`, GRD.`Bonggol`, GRD.`Serabut`\n"
                        + "FROM `tb_laporan_produksi_tv2` CT\n"
                        + "LEFT JOIN `tb_laporan_produksi` ON CT.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                        + "LEFT JOIN `tb_bahan_baku_masuk_cheat` ON CT.`no_kartu_waleta` = `tb_bahan_baku_masuk_cheat`.`no_kartu_waleta`\n"
                        + "LEFT JOIN `tb_grade_bahan_baku` ON CT.`kode_grade` = `tb_grade_bahan_baku`.`kode_grade`\n"
                        + "LEFT JOIN `tb_finishing_2` ON CT.`no_laporan_produksi` = `tb_finishing_2`.`no_laporan_produksi`\n"
                        + "LEFT JOIN (SELECT `no_laporan_produksi`,\n"
                        + "SUM(IF(`bentuk_grade`='Utuh', `keping`, 0)) AS 'Kpg_Utuh',\n"
                        + "SUM(IF(`bentuk_grade`='Pecah', `keping`, 0)) AS 'Kpg_Pecah',\n"
                        + "SUM(IF(`bentuk_grade`='Flat', `keping`, 0)) AS 'Kpg_Flat',\n"
                        + "SUM(IF(`bentuk_grade`='Jidun', `keping`, 0)) AS 'Kpg_Jidun',\n"
                        + "SUM(IF(`bentuk_grade`='Utuh', `gram`, 0)) AS 'Gram_Utuh',\n"
                        + "SUM(IF(`bentuk_grade`='Pecah', `gram`, 0)) AS 'Gram_Pecah',\n"
                        + "SUM(IF(`bentuk_grade`='Flat', `gram`, 0)) AS 'Gram_Flat',\n"
                        + "SUM(IF(`bentuk_grade`='Jidun', `gram`, 0)) AS 'Gram_Jidun',\n"
                        + "SUM(IF(`kode_grade` IN ('GNS SSK', 'GNS SSK BRSH', 'GNS HCRN KTR 3', 'GNS MESS 3 R', 'GNS SSK B'), `gram`, 0)) AS 'Sesekan',\n"
                        + "SUM(IF(`kode_grade` IN ('GNS HC', 'GNS MESS 2', 'GNS HCRN KTR 2', 'GNS MESS 2 R'), `gram`, 0)) AS 'Hancuran',\n"
                        + "SUM(IF(`kode_grade` IN ('GNS RONT LP', 'GNS MESS 1', 'GNS SK3', 'GNS SK3 K', 'GNS MESS', 'GNS MESS SAND', 'GNS MESS 1 R'), `gram`, 0)) AS 'Rontokan',\n"
                        + "SUM(IF(`kode_grade` IN ('GNS BGL', 'GNS F1', 'GNS F2', 'GNS F3', 'GNS B2', 'GNS FEET K', 'GNS B 3 R', 'GNS FEET 3 R', 'GNS FEET 1+2 R', 'GNS F3 K', 'GNS B 3', 'GNS FEET 1+2', 'GNS K3', 'GNS FEET'), `gram`, 0)) AS 'Bonggol',\n"
                        + "SUM(IF(`kode_grade` IN ('GNS SRBT', 'GNS ZLX', 'GNS NET'), `gram`, 0)) AS 'Serabut'\n"
                        + "FROM `tb_grading_bahan_jadi_tv2`\n"
                        + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_grading_bahan_jadi_tv2`.`grade_barang_jadi` = `tb_grade_bahan_jadi`.`kode`\n"
                        + "WHERE `no_laporan_produksi` IN (" + no_lp + ") GROUP BY `no_laporan_produksi`) GRD\n"
                        + "ON CT.`no_laporan_produksi` = GRD.`no_laporan_produksi`\n"
                        + "WHERE `tb_laporan_produksi`.`no_laporan_produksi` IN (" + no_lp + ") "
                        + "ORDER BY `no_laporan_produksi`";

                JRDesignQuery newQuery = new JRDesignQuery();
                newQuery.setText(query);
                JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Traceability2\\Laporan_Produksi_QR_Struk_tv2.jrxml");
                JASP_DESIGN.setQuery(newQuery);

                JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
                Map<String, Object> params = new HashMap<String, Object>();
                JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, params, Utility.db.getConnection());
                JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
            }
        } catch (JRException ex) {
            JOptionPane.showMessageDialog(this, ex.getLocalizedMessage());
            Logger.getLogger(JPanel_Traceability2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_print_lp_semuaActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Button_Search;
    private javax.swing.JButton Button_Search2;
    private javax.swing.JButton Button_Search_dataLP;
    private javax.swing.JComboBox<String> ComboBox_grade1;
    private javax.swing.JComboBox<String> ComboBox_grade2;
    private com.toedter.calendar.JDateChooser Date_box1;
    private com.toedter.calendar.JDateChooser Date_box2;
    private javax.swing.JTable Table_data_GradingLP;
    private javax.swing.JTable Table_data_LP;
    private javax.swing.JTable Table_traceability;
    private javax.swing.JTable Table_traceability2;
    private javax.swing.JButton button_HC;
    private javax.swing.JButton button_KH12;
    private javax.swing.JButton button_KH14;
    private javax.swing.JButton button_LabelBox;
    private javax.swing.JButton button_catatan_abnormal_produk;
    private javax.swing.JButton button_catatan_pemeriksaan_baku_selama_proses;
    private javax.swing.JButton button_catatan_pemeriksaan_kebersihan_sarang_walet_selama_proses;
    private javax.swing.JButton button_catatan_pemeriksaan_produk_jadi;
    private javax.swing.JButton button_catatan_penerimaan_dan_grading_baku;
    private javax.swing.JButton button_catatan_pengeringan_sarang_burung;
    private javax.swing.JButton button_catatan_perendaman_bahan_mentah;
    private javax.swing.JButton button_catatan_uji_nitrit;
    private javax.swing.JButton button_cheat_kode_tutupan;
    private javax.swing.JButton button_delete_all_hasil_grading;
    private javax.swing.JButton button_delete_lp_1;
    private javax.swing.JButton button_delete_lp_semua;
    private javax.swing.JButton button_edit_rsb_tutupan;
    private javax.swing.JButton button_insert_LP;
    private javax.swing.JButton button_insert_hasil_gradingLP;
    private javax.swing.JButton button_laporan_penerimaan_grading;
    private javax.swing.JButton button_laporan_pengiriman;
    private javax.swing.JButton button_laporan_penyimpanan_barang_jadi;
    private javax.swing.JButton button_laporan_produksi;
    private javax.swing.JButton button_lembar_kerja_pencucian;
    private javax.swing.JButton button_lp_kosong;
    private javax.swing.JButton button_packing_list;
    private javax.swing.JButton button_print_cacatan_penyimpanan_barang_jadi;
    private javax.swing.JButton button_print_lp_semua;
    private javax.swing.JButton button_surat_keterangan_pengiriman;
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
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
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
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel label_total_data;
    private javax.swing.JLabel label_total_data2;
    private javax.swing.JTextField txt_invoice2;
    private javax.swing.JTextField txt_no_kartu_CT2;
    private javax.swing.JTextField txt_search_box;
    private javax.swing.JTextField txt_search_kodeRSB_dataLP;
    private javax.swing.JTextField txt_search_lp;
    private javax.swing.JTextField txt_search_lp_dataLP;
    private javax.swing.JTextField txt_search_no_kartu_dataLP;
    private javax.swing.JTextField txt_search_rsb2;
    private javax.swing.JTextField txt_search_rsbKartu_CT2;
    private javax.swing.JTextField txt_search_spk;
    private javax.swing.JTextField txt_search_tutupan1;
    private javax.swing.JTextField txt_search_tutupan2;
    private javax.swing.JTextField txt_search_tutupan_ct;
    private javax.swing.JTextField txt_search_tutupan_dataLP;
    // End of variables declaration//GEN-END:variables
}
