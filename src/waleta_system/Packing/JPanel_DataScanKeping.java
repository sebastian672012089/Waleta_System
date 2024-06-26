package waleta_system.Packing;

import java.awt.HeadlessException;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;
import waleta_system.BahanBaku.JPanel_GradeBahanBaku;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;
import waleta_system.Class.ExportToExcel;
import waleta_system.Class.TableColumnHider;

public class JPanel_DataScanKeping extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    TableColumnHider tableColumnHider;

    public JPanel_DataScanKeping() {
        initComponents();
    }

    public void init() {
        try {
            refreshTable_DataScan();
            refreshTable_SPK();
//            tableColumnHider = new TableColumnHider(Tabel_barcode);
//            tableColumnHider.hide("v");
            CheckBox_select_all_box.setEnabled(false);
            CheckBox_select_all_box.setVisible(false);
            Tabel_spk.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent event) {
                    if (!event.getValueIsAdjusting() && Tabel_spk.getSelectedRow() != -1) {
                        int i = Tabel_spk.getSelectedRow();
                        String kode_spk = Tabel_spk.getValueAt(i, 0).toString();
                        refreshTable_gradeSPK(kode_spk);
                    }
                }
            });

            Tabel_grade_spk.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent event) {
                    if (!event.getValueIsAdjusting() && Tabel_grade_spk.getSelectedRow() != -1) {
                        int i = Tabel_grade_spk.getSelectedRow();
                        String no = Tabel_grade_spk.getValueAt(i, 0).toString();
                        label_no_grade_spk.setText(no);
                        float berat_per_kemasan = Float.valueOf(Tabel_grade_spk.getValueAt(i, 2).toString());
                        refreshTable_barcode(berat_per_kemasan);
                    }
                }
            });

            Tabel_barcode.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent event) {
                    if (!event.getValueIsAdjusting() && Tabel_barcode.getSelectedRow() != -1) {
                        int i = Tabel_barcode.getSelectedRow();
                        String no_urut_barcode = Tabel_barcode.getValueAt(i, 0).toString();
                        refreshTable_scan_qr(no_urut_barcode);
                    }
                }
            });
        } catch (Exception ex) {
            Logger.getLogger(JPanel_GradeBahanBaku.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_DataScan() {
        try {
            decimalFormat.setMaximumFractionDigits(2);
            float total_berat = 0;
            DefaultTableModel model = (DefaultTableModel) Tabel_data_scan_export.getModel();
            model.setRowCount(0);

            String filter_grade_buyer = "";
            if (ComboBox_grade_buyer.getSelectedItem() != null && ComboBox_grade_buyer.getSelectedIndex() > 0) {
                filter_grade_buyer = "AND `tb_spk_detail`.`no` = '" + ComboBox_grade_buyer.getSelectedItem().toString().split(" > ")[0] + "' ";
            }

            String filter_tanggal = "";
            if (Date_scan1.getDate() != null && Date_scan2.getDate() != null) {
                filter_tanggal = " AND DATE(`scan_time`) BETWEEN '" + dateFormat.format(Date_scan1.getDate()) + "' AND '" + dateFormat.format(Date_scan2.getDate()) + "'";
            }

            sql = "SELECT `no_urut_barcode`, `tb_barcode_pengiriman`.`no_barcode`, `qrcode`, `gram`, `tb_spk_detail`.`prod_date`, `tb_spk_detail`.`kode_kh`, `tb_spk_detail`.`kode_spk`, `tb_scan_qr_packing`.`no_grade_spk`, `tb_spk_detail`.`grade_buyer`, `scan_time`, `no_urut_pcs`, `kode_packing` "
                    + "FROM `tb_scan_qr_packing` "
                    + "LEFT JOIN `tb_barcode_pengiriman` ON `tb_scan_qr_packing`.`no_grade_spk` = `tb_barcode_pengiriman`.`no_grade_spk` AND `tb_scan_qr_packing`.`no_urut_barcode` = `tb_barcode_pengiriman`.`no_urut` "
                    + "LEFT JOIN `tb_spk_detail` ON `tb_scan_qr_packing`.`no_grade_spk` = `tb_spk_detail`.`no` "
                    + "WHERE `kode_spk` LIKE '%" + txt_kode_spk1.getText() + "%' "
                    + "AND `qrcode` LIKE '%" + txt_search_qr.getText() + "%' "
                    + filter_grade_buyer
                    + filter_tanggal
                    + " ORDER BY `no_urut`, `scan_time` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[20];
            while (rs.next()) {
                row[0] = rs.getInt("no_urut_barcode");
                row[1] = rs.getString("no_barcode");
                row[2] = rs.getString("qrcode");
                row[3] = rs.getFloat("gram");
                row[4] = rs.getDate("prod_date");
                
                String kode_rsb = "";
                if (rs.getString("kode_kh") != null) {
                    kode_rsb = rs.getString("kode_kh").split("-")[0];
                }
                
                if (rs.getString("kode_kh") != null && rs.getDate("prod_date") != null) {
                    row[5] = kode_rsb + "-" + new SimpleDateFormat("yyMMdd").format(rs.getDate("prod_date"));
                }

                row[6] = kode_rsb;
                row[7] = rs.getString("kode_spk");
                row[8] = rs.getString("grade_buyer");
                row[9] = rs.getInt("no_grade_spk");
                row[10] = rs.getInt("no_urut_pcs");
                row[11] = rs.getString("kode_packing");
                row[12] = rs.getTimestamp("scan_time");
                model.addRow(row);
                total_berat = total_berat + rs.getFloat("gram");
            }
            ColumnsAutoSizer.sizeColumnsToFit(Tabel_data_scan_export);
            label_total_data.setText(decimalFormat.format(Tabel_data_scan_export.getRowCount()));
            label_total_berat.setText(decimalFormat.format(total_berat));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_DataScanKeping.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void SavePrint() {
        try {
            decimalFormat.setMaximumFractionDigits(1);
            boolean check = true;
            float berat_plastik = 0, gram_timbang = 0, gram_print = 0;
            String print = "", plus_minus = "", NW = "", GW = "";
            try {
                if (!(txt_berat_timbangan.getText().equals("") || txt_berat_timbangan.getText() == null)) {
                    berat_plastik = Float.valueOf(txt_berat_plastik.getText());
                    gram_timbang = Float.valueOf(txt_berat_timbangan.getText());
                    gram_print = gram_timbang - berat_plastik;
                    print = decimalFormat.format(gram_print) + " g";
                    NW = "净重/NW:" + decimalFormat.format(gram_print) + " g ±";
                    GW = "毛重/GW:" + decimalFormat.format(gram_print + 0.3f) + " g ±";
                } else {
                    check = false;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Inputan Angka Salah !");
                check = false;
                Logger.getLogger(JPanel_DataScanKeping.class.getName()).log(Level.SEVERE, null, e);
            }

            if (check) {
                if (RadioButton1.isSelected()) {
                    plus_minus = "";
                    JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\weight_label.jrxml");
                    Map<String, Object> map = new HashMap<>();
                    map.put("GRAM_KEPING", print);//parameter name should be like it was named inside your report.
                    map.put("PLUS_MINUS", plus_minus);//parameter name should be like it was named inside your report.
                    JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
                    JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, map, Utility.db.getConnection());
                    if (CheckBox_print.isSelected()) {
                        JasperPrintManager.printReport(JASP_PRINT, false);//langsung print
                    } else {
                        JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
                    }
                } else if (RadioButton2.isSelected()) {
                    if (gram_print > 5) {
                        plus_minus = "(± 0.3 g)";
                    } else {
                        plus_minus = "(± 0.2 g)";
                    }
                    JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\weight_label.jrxml");
                    Map<String, Object> map = new HashMap<>();
                    map.put("GRAM_KEPING", print);//parameter name should be like it was named inside your report.
                    map.put("PLUS_MINUS", plus_minus);//parameter name should be like it was named inside your report.
                    JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
                    JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, map, Utility.db.getConnection());
                    if (CheckBox_print.isSelected()) {
                        JasperPrintManager.printReport(JASP_PRINT, false);//langsung print
                    } else {
                        JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
                    }
                } else if (RadioButton3.isSelected()) {
                    plus_minus = "(± 0.3 g)";
                    JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\weight_label.jrxml");
                    Map<String, Object> map = new HashMap<>();
                    map.put("GRAM_KEPING", print);//parameter name should be like it was named inside your report.
                    map.put("PLUS_MINUS", plus_minus);//parameter name should be like it was named inside your report.
                    JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
                    JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, map, Utility.db.getConnection());
                    if (CheckBox_print.isSelected()) {
                        JasperPrintManager.printReport(JASP_PRINT, false);//langsung print
                    } else {
                        JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
                    }
                } else if (RadioButton4.isSelected()) {
                    plus_minus = "";
                    JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\weight_label_QR.jrxml");
                    Map<String, Object> map = new HashMap<>();
                    map.put("QR", decimalFormat.format(gram_print));
                    map.put("GRAM_KEPING", print);//parameter name should be like it was named inside your report.
                    map.put("PLUS_MINUS", plus_minus);//parameter name should be like it was named inside your report.
                    JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
                    JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, map, Utility.db.getConnection());
                    if (CheckBox_print.isSelected()) {
                        JasperPrintManager.printReport(JASP_PRINT, false);//langsung print
                    } else {
                        JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
                    }
                } else if (RadioButton5.isSelected()) {
                    if (gram_print > 5) {
                        plus_minus = "(± 0.3 g)";
                    } else {
                        plus_minus = "(± 0.2 g)";
                    }
                    JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\weight_label_QR.jrxml");
                    Map<String, Object> map = new HashMap<>();
                    map.put("QR", decimalFormat.format(gram_print));
                    map.put("GRAM_KEPING", print);//parameter name should be like it was named inside your report.
                    map.put("PLUS_MINUS", plus_minus);//parameter name should be like it was named inside your report.
                    JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
                    JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, map, Utility.db.getConnection());
                    if (CheckBox_print.isSelected()) {
                        JasperPrintManager.printReport(JASP_PRINT, false);//langsung print
                    } else {
                        JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
                    }
                } else if (RadioButton6.isSelected()) {
                    JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\weight_label_imperial_QR.jrxml");
                    Map<String, Object> map = new HashMap<>();
                    map.put("QR", decimalFormat.format(gram_print));
                    map.put("GRAM_KEPING_CHINESE", NW.split("/")[0]);//parameter name should be like it was named inside your report.
                    map.put("PLUS_MINUS_CHINESE", GW.split("/")[0]);//parameter name should be like it was named inside your report.
                    map.put("GRAM_KEPING", NW.split("/")[1]);//parameter name should be like it was named inside your report.
                    map.put("PLUS_MINUS", GW.split("/")[1]);//parameter name should be like it was named inside your report.
                    JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
                    JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, map, Utility.db.getConnection());
                    if (CheckBox_print.isSelected()) {
                        JasperPrintManager.printReport(JASP_PRINT, false);//langsung print
                    } else {
                        JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
                    }
                }

                txt_berat_timbangan.setText(null);
                txt_berat_timbangan.requestFocus();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_DataScanKeping.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_SPK() {
        try {
            decimalFormat.setMaximumFractionDigits(2);
            DefaultTableModel model = (DefaultTableModel) Tabel_spk.getModel();
            model.setRowCount(0);
            String filter_hanya_menampilkan_spk_belum_terkirim = "";
            if (CheckBox_spk_belum_terkirim.isSelected()) {
                filter_hanya_menampilkan_spk_belum_terkirim = " AND `tb_pengiriman`.`kode_spk` IS NULL";
            }
            sql = "SELECT `tb_spk`.`kode_spk`, `tb_buyer`.`nama`, `tanggal_awb`, SUM(`tb_spk_detail`.`berat`) AS 'total_berat_spk', `gram_scan`\n"
                    + "FROM `tb_spk` \n"
                    + "LEFT JOIN `tb_buyer` ON `tb_spk`.`buyer` = `tb_buyer`.`kode_buyer`\n"
                    + "LEFT JOIN `tb_spk_detail` ON `tb_spk`.`kode_spk` = `tb_spk_detail`.`kode_spk`\n"
                    + "LEFT JOIN `tb_pengiriman` ON `tb_spk`.`kode_spk` = `tb_pengiriman`.`kode_spk`\n"
                    + "LEFT JOIN ("
                    + "SELECT `kode_spk`, SUM(`tb_scan_qr_packing`.`gram`) AS 'gram_scan' FROM `tb_scan_qr_packing` "
                    + "LEFT JOIN `tb_spk_detail` ON `tb_scan_qr_packing`.`no_grade_spk` = `tb_spk_detail`.`no`\n"
                    + "WHERE 1 GROUP BY `tb_spk_detail`.`kode_spk`"
                    + ") data_scan ON `tb_spk`.`kode_spk` = data_scan.`kode_spk`\n"
                    + "WHERE `tb_spk`.`kode_spk` LIKE '%" + txt_kode_spk2.getText() + "%' "
                    //                    + "AND (`tb_spk`.`kode_spk` LIKE 'SPK%' OR `tb_spk`.`buyer` LIKE 'E%') "
                    + "AND `tb_spk`.`tanggal_spk` > '2022-01-01'"
                    + filter_hanya_menampilkan_spk_belum_terkirim
                    + " GROUP BY `tb_spk`.`kode_spk`";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[10];
            while (rs.next()) {
                row[0] = rs.getString("kode_spk");
                row[1] = rs.getString("nama");
                row[2] = rs.getDate("tanggal_awb");
                row[3] = Math.round(rs.getFloat("total_berat_spk") / 100f) / 10f;
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(Tabel_spk);
            label_total_data_spk.setText(decimalFormat.format(Tabel_spk.getRowCount()));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_DataScanKeping.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_gradeSPK(String kode_spk) {
        try {
            float total_gram_scan = 0;
            decimalFormat.setMaximumFractionDigits(2);
            DefaultTableModel model = (DefaultTableModel) Tabel_grade_spk.getModel();
            model.setRowCount(0);
            sql = "SELECT `tb_spk_detail`.`no`, `grade_waleta`, `grade_buyer`, `berat_kemasan`, `jumlah_kemasan`, `berat`, `gram_scan`\n"
                    + "FROM `tb_spk_detail`\n"
                    + "LEFT JOIN ("
                    + "SELECT `no`, SUM(`tb_scan_qr_packing`.`gram`) AS 'gram_scan'\n"
                    + "FROM `tb_scan_qr_packing`\n"
                    + "LEFT JOIN `tb_spk_detail` ON `tb_scan_qr_packing`.`no_grade_spk` = `tb_spk_detail`.`no`\n"
                    + "WHERE `tb_spk_detail`.`kode_spk` = '" + kode_spk + "' GROUP BY `tb_spk_detail`.`no`"
                    + ") data_scan ON `tb_spk_detail`.`no` = data_scan.`no`\n"
                    + "WHERE `tb_spk_detail`.`kode_spk` = '" + kode_spk + "' ";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[8];
            while (rs.next()) {
                row[0] = rs.getInt("no");
                row[1] = rs.getString("grade_buyer");
                row[2] = rs.getFloat("berat_kemasan");
                row[3] = rs.getFloat("jumlah_kemasan");
                row[4] = rs.getFloat("berat");
                row[5] = Math.round(rs.getFloat("gram_scan") * 100f) / 100f;
                row[6] = Math.round((rs.getFloat("gram_scan") / rs.getFloat("berat")) * 10000f) / 100f;
                model.addRow(row);
                total_gram_scan = total_gram_scan + rs.getFloat("gram_scan");
            }
            ColumnsAutoSizer.sizeColumnsToFit(Tabel_grade_spk);
            label_total_scan_grade.setText(decimalFormat.format(total_gram_scan));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_DataScanKeping.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_barcode(float berat_per_kemasan) {
        try {
            float total_gram_scan = 0;
            String no = label_no_grade_spk.getText();
            decimalFormat.setMaximumFractionDigits(2);
            DefaultTableModel model = (DefaultTableModel) Tabel_barcode.getModel();
            model.setRowCount(0);
            sql = "SELECT `tb_barcode_pengiriman`.`no_grade_spk`, `no_urut` AS 'no_urut_barcode', `no_barcode`, `kode_packing`, SUM(`tb_scan_qr_packing`.`gram`) AS 'gram_scan' "
                    + "FROM `tb_barcode_pengiriman` "
                    + "LEFT JOIN `tb_scan_qr_packing` ON `tb_barcode_pengiriman`.`no_grade_spk` = `tb_scan_qr_packing`.`no_grade_spk` AND `tb_barcode_pengiriman`.`no_urut` = `tb_scan_qr_packing`.`no_urut_barcode` "
                    + "WHERE `tb_barcode_pengiriman`.`no_grade_spk` = '" + no + "' GROUP BY `no_barcode`\n"
                    + "UNION ALL\n"
                    + "SELECT `tb_scan_qr_packing`.`no_grade_spk`, `no_urut_barcode`, `tb_barcode_pengiriman`.`no_barcode`, `kode_packing`, SUM(`tb_scan_qr_packing`.`gram`) AS 'gram_scan' "
                    + "FROM `tb_scan_qr_packing` "
                    + "LEFT JOIN `tb_barcode_pengiriman` ON `tb_scan_qr_packing`.`no_grade_spk` = `tb_barcode_pengiriman`.`no_grade_spk` AND `tb_scan_qr_packing`.`no_urut_barcode` = `tb_barcode_pengiriman`.`no_urut` "
                    + "WHERE `tb_scan_qr_packing`.`no_grade_spk` = '" + no + "' AND `tb_barcode_pengiriman`.`no_barcode` IS NULL GROUP BY `tb_scan_qr_packing`.`no_grade_spk`, `no_urut_barcode`\n"
                    + "ORDER BY `no_urut_barcode`";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[8];
            while (rs.next()) {
                row[0] = rs.getInt("no_urut_barcode");
                row[1] = rs.getString("no_barcode");
                row[2] = Math.round(rs.getFloat("gram_scan") * 100f) / 100f;
                row[3] = Math.round((rs.getFloat("gram_scan") / berat_per_kemasan) * 10000f) / 100f;
                row[4] = rs.getString("kode_packing");
                row[5] = false;
                model.addRow(row);
                total_gram_scan = total_gram_scan + rs.getFloat("gram_scan");
            }
            ColumnsAutoSizer.sizeColumnsToFit(Tabel_barcode);
            label_total_scan_barcode.setText(decimalFormat.format(total_gram_scan));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_DataScanKeping.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_scan_qr(String no_urut_barcode) {
        try {
            float total_gram_scan = 0;
            decimalFormat.setMaximumFractionDigits(2);
            DefaultTableModel model = (DefaultTableModel) Tabel_scan_qr.getModel();
            model.setRowCount(0);
            sql = "SELECT `no_urut_pcs`, `qrcode`, `gram`, `kode_packing` \n"
                    + "FROM `tb_scan_qr_packing`\n"
                    + "WHERE `no_urut_barcode` = '" + no_urut_barcode + "' AND `no_grade_spk` = '" + label_no_grade_spk.getText() + "'"
                    + "ORDER BY `no_urut_pcs`";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[8];
            while (rs.next()) {
                row[0] = rs.getInt("no_urut_pcs");
                row[1] = rs.getFloat("gram");
                row[2] = rs.getString("qrcode");
                row[3] = rs.getString("qrcode").length();
                row[4] = rs.getString("kode_packing");
                model.addRow(row);
                total_gram_scan = total_gram_scan + rs.getFloat("gram");
            }
            ColumnsAutoSizer.sizeColumnsToFit(Tabel_scan_qr);
            label_total_scan_satuan.setText(decimalFormat.format(total_gram_scan));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_DataScanKeping.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void edit() {
        int x = Tabel_data_scan_export.getSelectedRow();
        if (x > -1) {
//            String no = Tabel_data_scan.getValueAt(x, 0).toString();
//            String gram = Tabel_data_scan.getValueAt(x, 2).toString();
//            String gram_plastik = Tabel_data_scan.getValueAt(x, 3).toString();
//            String keterangan = Tabel_data_scan.getValueAt(x, 5).toString();
//            JDialog_Edit_DataTimbangan dialog = new JDialog_Edit_DataTimbangan(new javax.swing.JFrame(), true, no, keterangan, gram, gram_plastik);
//            dialog.pack();
//            dialog.setLocationRelativeTo(this);
//            dialog.setVisible(true);
//            dialog.setEnabled(true);
            refreshTable_DataScan();
        } else {
            JOptionPane.showMessageDialog(this, "Silahkan klik pada data yang akan di Edit");
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup_jenis_label = new javax.swing.ButtonGroup();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txt_kode_spk1 = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        Date_scan1 = new com.toedter.calendar.JDateChooser();
        Date_scan2 = new com.toedter.calendar.JDateChooser();
        button_search_dataScan = new javax.swing.JButton();
        button_export = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        Tabel_data_scan_export = new javax.swing.JTable();
        jLabel10 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        label_total_data = new javax.swing.JLabel();
        label_total_berat = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txt_berat_plastik = new javax.swing.JTextField();
        txt_berat_timbangan = new javax.swing.JTextField();
        button_print = new javax.swing.JButton();
        CheckBox_print = new javax.swing.JCheckBox();
        RadioButton6 = new javax.swing.JRadioButton();
        RadioButton5 = new javax.swing.JRadioButton();
        RadioButton4 = new javax.swing.JRadioButton();
        RadioButton3 = new javax.swing.JRadioButton();
        RadioButton2 = new javax.swing.JRadioButton();
        RadioButton1 = new javax.swing.JRadioButton();
        jLabel11 = new javax.swing.JLabel();
        txt_search_qr = new javax.swing.JTextField();
        button_sistem_penimbangan = new javax.swing.JButton();
        ComboBox_grade_buyer = new javax.swing.JComboBox<>();
        jPanel4 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        txt_kode_spk2 = new javax.swing.JTextField();
        CheckBox_spk_belum_terkirim = new javax.swing.JCheckBox();
        button_input_csv = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        button_refresh_spk = new javax.swing.JButton();
        label_total_data_spk = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        Tabel_spk = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        label_total_scan_grade = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        Tabel_grade_spk = new javax.swing.JTable();
        button_input_scan = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        button_refresh_grade = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        label_no_grade_spk = new javax.swing.JLabel();
        label_total_scan_barcode = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        Tabel_barcode = new javax.swing.JTable();
        button_delete_scan_box = new javax.swing.JToggleButton();
        button_refresh_barcode = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();
        CheckBox_select_all_box = new javax.swing.JCheckBox();
        jPanel7 = new javax.swing.JPanel();
        label_total_scan_satuan = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        Tabel_scan_qr = new javax.swing.JTable();
        button_refresh_scan = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        button_edit_scan = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Data Scan Keping Ekspor", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("Kode SPK :");

        txt_kode_spk1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_kode_spk1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_kode_spk1KeyPressed(evt);
            }
        });

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel8.setText("Tanggal Scan :");

        Date_scan1.setDate(new Date());
        Date_scan1.setDateFormatString("dd MMMM yyyy");
        Date_scan1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Date_scan2.setDate(new Date());
        Date_scan2.setDateFormatString("dd MMMM yyyy");
        Date_scan2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        button_search_dataScan.setBackground(new java.awt.Color(255, 255, 255));
        button_search_dataScan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search_dataScan.setText("Search");
        button_search_dataScan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_search_dataScanActionPerformed(evt);
            }
        });

        button_export.setBackground(new java.awt.Color(255, 255, 255));
        button_export.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export.setText("Export to Excel");
        button_export.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_exportActionPerformed(evt);
            }
        });

        Tabel_data_scan_export.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Tabel_data_scan_export.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No BOX", "Barcode", "Individual Label", "Individual Weight", "Manufacture Date", "Corp Batch Number", "Swiftlet House Registration Number", "Kode SPK", "Grade Buyer", "No GRADE", "No KPG", "Kode Packing", "Scan Time"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(Tabel_data_scan_export);

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel10.setText("Grade Buyer :");

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setText("Total Data :");

        label_total_data.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_data.setText("0");

        label_total_berat.setBackground(new java.awt.Color(255, 255, 255));
        label_total_berat.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_berat.setText("0");

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel3.setText("Total Berat :");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Weight Label System", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel7.setText("Berat timbang (Gram) :");
        jLabel7.setFocusable(false);

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel5.setText("Berat Plastik (Gram) :");
        jLabel5.setFocusable(false);

        txt_berat_plastik.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_berat_plastik.setText("0");

        txt_berat_timbangan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_berat_timbangan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_berat_timbanganKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_berat_timbanganKeyTyped(evt);
            }
        });

        button_print.setBackground(new java.awt.Color(255, 255, 255));
        button_print.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_print.setText("Print");
        button_print.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_printActionPerformed(evt);
            }
        });

        CheckBox_print.setBackground(new java.awt.Color(255, 255, 255));
        CheckBox_print.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        CheckBox_print.setSelected(true);
        CheckBox_print.setText("Langsung Print");

        RadioButton6.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroup_jenis_label.add(RadioButton6);
        RadioButton6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        RadioButton6.setText("Label Imperial +QR");

        RadioButton5.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroup_jenis_label.add(RadioButton5);
        RadioButton5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        RadioButton5.setText("Label dengan (± 0.2 gr) +QR");

        RadioButton4.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroup_jenis_label.add(RadioButton4);
        RadioButton4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        RadioButton4.setText("Label tanpa (± 0.2 gr) +QR");

        RadioButton3.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroup_jenis_label.add(RadioButton3);
        RadioButton3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        RadioButton3.setText("Label Imperial");

        RadioButton2.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroup_jenis_label.add(RadioButton2);
        RadioButton2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        RadioButton2.setText("Label dengan (± 0.2 gr)");

        RadioButton1.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroup_jenis_label.add(RadioButton1);
        RadioButton1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        RadioButton1.setText("Label tanpa (± 0.2 gr)");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(CheckBox_print)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(button_print))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_berat_plastik, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_berat_timbangan, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(RadioButton1)
                    .addComponent(RadioButton2)
                    .addComponent(RadioButton3)
                    .addComponent(RadioButton4)
                    .addComponent(RadioButton5)
                    .addComponent(RadioButton6))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_berat_plastik, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_berat_timbangan, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CheckBox_print, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_print, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(RadioButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(RadioButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(RadioButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(RadioButton4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(RadioButton5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(RadioButton6)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel11.setText("QR :");

        txt_search_qr.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_qr.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_qrKeyPressed(evt);
            }
        });

        button_sistem_penimbangan.setBackground(new java.awt.Color(255, 255, 255));
        button_sistem_penimbangan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_sistem_penimbangan.setText("Buka Sistem Penimbangan");
        button_sistem_penimbangan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_sistem_penimbanganActionPerformed(evt);
            }
        });

        ComboBox_grade_buyer.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1041, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_data)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_berat))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_kode_spk1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_grade_buyer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_qr, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_scan1, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_scan2, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_search_dataScan)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_export)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_sistem_penimbangan)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_search_qr, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_kode_spk1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ComboBox_grade_buyer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(Date_scan1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(Date_scan2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(button_search_dataScan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_export, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_sistem_penimbangan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(label_total_data))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(label_total_berat)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 579, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Data Scan QR", jPanel3);

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel4.setText("Kode SPK :");

        txt_kode_spk2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_kode_spk2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_kode_spk2KeyPressed(evt);
            }
        });

        CheckBox_spk_belum_terkirim.setBackground(new java.awt.Color(255, 255, 255));
        CheckBox_spk_belum_terkirim.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        CheckBox_spk_belum_terkirim.setSelected(true);
        CheckBox_spk_belum_terkirim.setText("Hanya Menampilkan SPK yang belum Terkirim");

        button_input_csv.setBackground(new java.awt.Color(255, 255, 255));
        button_input_csv.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_input_csv.setText("Input CSV");
        button_input_csv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_input_csvActionPerformed(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        button_refresh_spk.setBackground(new java.awt.Color(255, 255, 255));
        button_refresh_spk.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_refresh_spk.setText("Refresh SPK");
        button_refresh_spk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refresh_spkActionPerformed(evt);
            }
        });

        label_total_data_spk.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_spk.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_data_spk.setText("0");

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel13.setText("Total Data :");

        Tabel_spk.setAutoCreateRowSorter(true);
        Tabel_spk.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Tabel_spk.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode SPK", "Buyer", "Tanggal AWB", "Kg"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Float.class
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
        Tabel_spk.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(Tabel_spk);
        if (Tabel_spk.getColumnModel().getColumnCount() > 0) {
            Tabel_spk.getColumnModel().getColumn(0).setMaxWidth(200);
        }

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(label_total_data_spk)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 126, Short.MAX_VALUE)
                .addComponent(button_refresh_spk))
            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_data_spk, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_refresh_spk, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2))
        );

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));

        label_total_scan_grade.setBackground(new java.awt.Color(255, 255, 255));
        label_total_scan_grade.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_scan_grade.setText("0");

        Tabel_grade_spk.setAutoCreateRowSorter(true);
        Tabel_grade_spk.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Tabel_grade_spk.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No GRADE", "Grade Buyer", "Gr/Pack", "Pack", "Berat", "Gr Scan", "%"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
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
        Tabel_grade_spk.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(Tabel_grade_spk);
        if (Tabel_grade_spk.getColumnModel().getColumnCount() > 0) {
            Tabel_grade_spk.getColumnModel().getColumn(0).setMaxWidth(200);
        }

        button_input_scan.setBackground(new java.awt.Color(255, 255, 255));
        button_input_scan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_input_scan.setText("Input Scan");
        button_input_scan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_input_scanActionPerformed(evt);
            }
        });

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel16.setText("Gram Scan :");

        button_refresh_grade.setBackground(new java.awt.Color(255, 255, 255));
        button_refresh_grade.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_refresh_grade.setText("Refresh");
        button_refresh_grade.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refresh_gradeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(label_total_scan_grade)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 135, Short.MAX_VALUE)
                .addComponent(button_input_scan)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_refresh_grade))
            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(label_total_scan_grade, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_input_scan, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_refresh_grade, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3))
        );

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));

        label_no_grade_spk.setBackground(new java.awt.Color(255, 255, 255));
        label_no_grade_spk.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_no_grade_spk.setText("XXXX");

        label_total_scan_barcode.setBackground(new java.awt.Color(255, 255, 255));
        label_total_scan_barcode.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_scan_barcode.setText("0");

        Tabel_barcode.setAutoCreateRowSorter(true);
        Tabel_barcode.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Tabel_barcode.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No BOX", "Barcode", "Gram", "% Scan", "Kode Packing", "v"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.String.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Tabel_barcode.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(Tabel_barcode);
        if (Tabel_barcode.getColumnModel().getColumnCount() > 0) {
            Tabel_barcode.getColumnModel().getColumn(1).setMaxWidth(200);
            Tabel_barcode.getColumnModel().getColumn(5).setMinWidth(25);
            Tabel_barcode.getColumnModel().getColumn(5).setMaxWidth(25);
        }

        button_delete_scan_box.setBackground(new java.awt.Color(255, 255, 255));
        button_delete_scan_box.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_delete_scan_box.setText("Pilih Box");
        button_delete_scan_box.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_delete_scan_boxActionPerformed(evt);
            }
        });

        button_refresh_barcode.setBackground(new java.awt.Color(255, 255, 255));
        button_refresh_barcode.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_refresh_barcode.setText("Refresh");
        button_refresh_barcode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refresh_barcodeActionPerformed(evt);
            }
        });

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel15.setText("Scan :");

        CheckBox_select_all_box.setBackground(new java.awt.Color(255, 255, 255));
        CheckBox_select_all_box.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        CheckBox_select_all_box.setText("All");
        CheckBox_select_all_box.setEnabled(false);
        CheckBox_select_all_box.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                CheckBox_select_all_boxItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(label_no_grade_spk)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(label_total_scan_barcode)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 66, Short.MAX_VALUE)
                .addComponent(CheckBox_select_all_box)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_delete_scan_box)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_refresh_barcode))
            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_scan_barcode, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_refresh_barcode, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_no_grade_spk, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_delete_scan_box)
                    .addComponent(CheckBox_select_all_box))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4))
        );

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));

        label_total_scan_satuan.setBackground(new java.awt.Color(255, 255, 255));
        label_total_scan_satuan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_scan_satuan.setText("0");

        Tabel_scan_qr.setAutoCreateRowSorter(true);
        Tabel_scan_qr.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Tabel_scan_qr.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No KPG", "Gram", "QR", "@", "Kode Packing"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Float.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Tabel_scan_qr.getTableHeader().setReorderingAllowed(false);
        jScrollPane5.setViewportView(Tabel_scan_qr);

        button_refresh_scan.setBackground(new java.awt.Color(255, 255, 255));
        button_refresh_scan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_refresh_scan.setText("Refresh");
        button_refresh_scan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refresh_scanActionPerformed(evt);
            }
        });

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel14.setText("Total Gram :");

        button_edit_scan.setBackground(new java.awt.Color(255, 255, 255));
        button_edit_scan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_edit_scan.setText("Edit");
        button_edit_scan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_edit_scanActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(label_total_scan_satuan)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 91, Short.MAX_VALUE)
                .addComponent(button_edit_scan)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_refresh_scan)
                .addContainerGap())
            .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(label_total_scan_satuan, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_refresh_scan, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_edit_scan, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 568, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_kode_spk2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(CheckBox_spk_belum_terkirim)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(button_input_csv)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_kode_spk2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CheckBox_spk_belum_terkirim, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_input_csv, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        jTabbedPane1.addTab("Progress Scan", jPanel4);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void button_search_dataScanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_dataScanActionPerformed
        // TODO add your handling code here:
        refreshTable_DataScan();
    }//GEN-LAST:event_button_search_dataScanActionPerformed

    private void txt_kode_spk1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_kode_spk1KeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            try {
                refreshTable_DataScan();

                ComboBox_grade_buyer.removeAllItems();
                ComboBox_grade_buyer.addItem("All");
                sql = "SELECT `no`, `grade_buyer` FROM `tb_spk_detail` "
                        + "WHERE `kode_spk` LIKE '%" + txt_kode_spk1.getText() + "%' ORDER BY `grade_buyer`";
                rs = Utility.db.getStatement().executeQuery(sql);
                while (rs.next()) {
                    ComboBox_grade_buyer.addItem(rs.getString("no") + " > " + rs.getString("grade_buyer"));
                }
            } catch (SQLException ex) {
                Logger.getLogger(JPanel_DataScanKeping.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_txt_kode_spk1KeyPressed

    private void button_exportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_exportActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Tabel_data_scan_export.getModel();
        ExportToExcel.writeToExcel(model, this);
    }//GEN-LAST:event_button_exportActionPerformed

    private void txt_berat_timbanganKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_berat_timbanganKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            SavePrint();
        }
    }//GEN-LAST:event_txt_berat_timbanganKeyPressed

    private void button_printActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_printActionPerformed
        // TODO add your handling code here:
        SavePrint();
    }//GEN-LAST:event_button_printActionPerformed

    private void txt_berat_timbanganKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_berat_timbanganKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_berat_timbanganKeyTyped

    private void button_input_scanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_input_scanActionPerformed
        // TODO add your handling code here:
        try {
            int j = Tabel_grade_spk.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan Pilih No Box / No Barcode yang akan di input");
            } else {
                sql = "SELECT MAX(`no_urut_barcode`) AS 'last_number' FROM `tb_scan_qr_packing` WHERE `no_grade_spk` = " + Tabel_grade_spk.getValueAt(j, 0).toString();
                int next_number = 1;
                rs = Utility.db.getStatement().executeQuery(sql);
                if (rs.next()) {
                    next_number = rs.getInt("last_number") + 1;
                }
                String no_urut_barcode = JOptionPane.showInputDialog("Scan mulai dari No Box :", next_number);
                if (no_urut_barcode != null && !no_urut_barcode.equals("")) {
                    JDialog_ScanQR dialog = new JDialog_ScanQR(label_no_grade_spk.getText(), Integer.valueOf(no_urut_barcode), "");
                    dialog.pack();
                    dialog.setLocationRelativeTo(this);
                    dialog.setVisible(true);
                    dialog.setEnabled(true);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_DataScanKeping.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_input_scanActionPerformed

    private void txt_kode_spk2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_kode_spk2KeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_SPK();
        }
    }//GEN-LAST:event_txt_kode_spk2KeyPressed

    private void button_refresh_spkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refresh_spkActionPerformed
        // TODO add your handling code here:
        refreshTable_SPK();
    }//GEN-LAST:event_button_refresh_spkActionPerformed

    private void button_refresh_gradeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refresh_gradeActionPerformed
        // TODO add your handling code here:
        int i = Tabel_spk.getSelectedRow();
        if (i >= 0) {
            String kode_spk = Tabel_spk.getValueAt(i, 0).toString();
            refreshTable_gradeSPK(kode_spk);
        }
    }//GEN-LAST:event_button_refresh_gradeActionPerformed

    private void button_refresh_scanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refresh_scanActionPerformed
        // TODO add your handling code here:
        int i = Tabel_barcode.getSelectedRow();
        if (i >= 0) {
            String no_barcode = Tabel_barcode.getValueAt(i, 0).toString();
            refreshTable_scan_qr(no_barcode);
        }
    }//GEN-LAST:event_button_refresh_scanActionPerformed

    private void button_refresh_barcodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refresh_barcodeActionPerformed
        // TODO add your handling code here:
        int i = Tabel_grade_spk.getSelectedRow();
        if (i >= 0) {
            float berat_per_kemasan = Float.valueOf(Tabel_grade_spk.getValueAt(i, 2).toString());
            refreshTable_barcode(berat_per_kemasan);
        }
    }//GEN-LAST:event_button_refresh_barcodeActionPerformed

    private void button_edit_scanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_edit_scanActionPerformed
        // TODO add your handling code here:.
        try {
            int j = Tabel_barcode.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan Pilih No Box / No Barcode yang akan di input / Edit");
            } else {
                button_input_scan.setEnabled(false);
                button_edit_scan.setEnabled(false);
                String kode_packing = "";
                if (Tabel_barcode.getValueAt(j, 4) != null) {
                    kode_packing = Tabel_barcode.getValueAt(j, 4).toString();
                }
                final JDialog_ScanQR dialog = new JDialog_ScanQR(label_no_grade_spk.getText(), (int) Tabel_barcode.getValueAt(j, 0), kode_packing);
                dialog.pack();
                dialog.setLocationRelativeTo(this);
                dialog.setVisible(true);
                dialog.setEnabled(true);
                dialog.jLabel33.setVisible(false);
                dialog.txt_max_no_box.setVisible(false);
                dialog.button_next_box.setVisible(false);
                dialog.addWindowListener(new WindowAdapter() {
                    //I skipped unused callbacks for readability

                    public void windowClosing(WindowEvent e) {
//                        if (JOptionPane.showConfirmDialog(dialog, "Are you sure ?") == JOptionPane.OK_OPTION) {
//                            dialog.setVisible(false);
//                            dialog.dispose();
//                        }
                        button_input_scan.setEnabled(true);
                        button_edit_scan.setEnabled(true);
                    }
                });
                button_refresh_scan.doClick();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_DataScanKeping.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_edit_scanActionPerformed

    private void txt_search_qrKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_qrKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_DataScan();
        }
    }//GEN-LAST:event_txt_search_qrKeyPressed

    private void button_sistem_penimbanganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_sistem_penimbanganActionPerformed
        // TODO add your handling code here:
        final JFrame_SistemScanKeping frame = new JFrame_SistemScanKeping();
        frame.pack();
        frame.setLocationRelativeTo(this);
        frame.setVisible(true);
        frame.setEnabled(true);
        frame.setResizable(false);
    }//GEN-LAST:event_button_sistem_penimbanganActionPerformed

    private void button_input_csvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_input_csvActionPerformed
        // TODO add your handling code here:
        try {
            JOptionPane.showMessageDialog(this, "Format csv (No KEPING , SCAN QR , GRAM , NO GRADE SPK, NO BOX, KODE PACKING), pemisah koma (,)");
            int jumlah_sukses = 0;
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Select CSV file to import!");
            int result = chooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                Utility.db.getConnection();
                File file = chooser.getSelectedFile();
                String filename1 = file.getAbsolutePath();
                try ( BufferedReader br = new BufferedReader(new FileReader(filename1))) {
                    String line;
                    try {
                        Utility.db.getConnection().setAutoCommit(false);
                        while ((line = br.readLine()) != null) {
                            String[] value = line.split(",");

                            String Query = "INSERT INTO `tb_scan_qr_packing`(`no_urut_pcs`, `qrcode`, `gram`, `no_grade_spk`, `no_urut_barcode`, `kode_packing`, `scan_time`) "
                                    + "VALUES ("
                                    + "'" + value[0] + "', "
                                    + "'" + value[1] + "',"
                                    + "'" + value[2] + "',"
                                    + "'" + value[3] + "',"
                                    + "'" + value[4] + "',"
                                    + "'" + value[5] + "',"
                                    + "NOW())"
                                    + "ON DUPLICATE KEY UPDATE "
                                    + "`no_urut_pcs` = '" + value[0] + "', "
                                    + "`gram` = '" + value[2] + "', "
                                    + "`no_grade_spk` = '" + value[3] + "', "
                                    + "`no_urut_barcode` = '" + value[4] + "', "
                                    + "`kode_packing` = '" + value[5] + "', "
                                    + "`scan_time` = NOW() ";
                            Utility.db.getConnection().createStatement();
                            Utility.db.getStatement().executeUpdate(Query);
                            jumlah_sukses++;
                        }
                        Utility.db.getConnection().commit();
                        JOptionPane.showMessageDialog(this, "Data Berhasil Masuk : " + jumlah_sukses);
                    } catch (Exception ex) {
                        try {
                            Utility.db.getConnection().rollback();
                        } catch (SQLException ex1) {
                            Logger.getLogger(JPanel_DataScanKeping.class.getName()).log(Level.SEVERE, null, ex1);
                        }
                        JOptionPane.showMessageDialog(this, ex);
                        Logger.getLogger(JPanel_DataScanKeping.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {
                        try {
                            Utility.db.getConnection().setAutoCommit(true);
                        } catch (SQLException ex) {
                            Logger.getLogger(JPanel_DataScanKeping.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        } catch (HeadlessException | IOException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_DataScanKeping.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_input_csvActionPerformed

    private void button_delete_scan_boxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_delete_scan_boxActionPerformed
        // TODO add your handling code here:
        if (button_delete_scan_box.isSelected()) {
            button_delete_scan_box.setText("Delete");
            CheckBox_select_all_box.setEnabled(true);
            CheckBox_select_all_box.setVisible(true);
            CheckBox_select_all_box.setSelected(false);
            tableColumnHider.show("v");
            for (int i = 0; i < Tabel_barcode.getRowCount(); i++) {
                Tabel_barcode.setValueAt(false, i, 5);
            }
        } else {
            //SAVING CODE
            try {
                Utility.db.getConnection().setAutoCommit(false);
                int jumlah = 0;
                for (int i = 0; i < Tabel_barcode.getRowCount(); i++) {
                    if ((boolean) Tabel_barcode.getValueAt(i, 5)) {
                        System.out.println(Tabel_barcode.getValueAt(i, 0).toString());
                        String Query = "DELETE FROM `tb_scan_qr_packing` WHERE `no_grade_spk` = '" + label_no_grade_spk.getText() + "' AND `no_urut_barcode` = '" + Tabel_barcode.getValueAt(i, 0).toString() + "'";
                        Utility.db.getConnection().createStatement();
                        Utility.db.getStatement().executeUpdate(Query);
                        jumlah++;
                    }
                }
                Utility.db.getConnection().commit();
                JOptionPane.showMessageDialog(this, "Data scan dari " + jumlah + " Box berhasil dihapus !");
                button_delete_scan_box.setText("Pilih Box");
                CheckBox_select_all_box.setEnabled(false);
                CheckBox_select_all_box.setVisible(false);
                tableColumnHider.hide("v");
                button_refresh_barcode.doClick();
            } catch (SQLException ex) {
                try {
                    Utility.db.getConnection().rollback();
                } catch (SQLException e) {
                    Logger.getLogger(JPanel_DataScanKeping.class.getName()).log(Level.SEVERE, null, e);
                }
                Logger.getLogger(JPanel_DataScanKeping.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    Utility.db.getConnection().setAutoCommit(true);
                } catch (SQLException ex) {
                    Logger.getLogger(JPanel_DataScanKeping.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_button_delete_scan_boxActionPerformed

    private void CheckBox_select_all_boxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_CheckBox_select_all_boxItemStateChanged
        // TODO add your handling code here:
        if (CheckBox_select_all_box.isSelected()) {
            for (int i = 0; i < Tabel_barcode.getRowCount(); i++) {
                Tabel_barcode.setValueAt(true, i, 5);
            }
        } else {
            for (int i = 0; i < Tabel_barcode.getRowCount(); i++) {
                Tabel_barcode.setValueAt(false, i, 5);
            }
        }
    }//GEN-LAST:event_CheckBox_select_all_boxItemStateChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox CheckBox_print;
    private javax.swing.JCheckBox CheckBox_select_all_box;
    private javax.swing.JCheckBox CheckBox_spk_belum_terkirim;
    private javax.swing.JComboBox<String> ComboBox_grade_buyer;
    private com.toedter.calendar.JDateChooser Date_scan1;
    private com.toedter.calendar.JDateChooser Date_scan2;
    private javax.swing.JRadioButton RadioButton1;
    private javax.swing.JRadioButton RadioButton2;
    private javax.swing.JRadioButton RadioButton3;
    private javax.swing.JRadioButton RadioButton4;
    private javax.swing.JRadioButton RadioButton5;
    private javax.swing.JRadioButton RadioButton6;
    private javax.swing.JTable Tabel_barcode;
    private javax.swing.JTable Tabel_data_scan_export;
    private javax.swing.JTable Tabel_grade_spk;
    private javax.swing.JTable Tabel_scan_qr;
    private javax.swing.JTable Tabel_spk;
    private javax.swing.ButtonGroup buttonGroup_jenis_label;
    private javax.swing.JToggleButton button_delete_scan_box;
    public static javax.swing.JButton button_edit_scan;
    private javax.swing.JButton button_export;
    public static javax.swing.JButton button_input_csv;
    public static javax.swing.JButton button_input_scan;
    private javax.swing.JButton button_print;
    public static javax.swing.JButton button_refresh_barcode;
    public static javax.swing.JButton button_refresh_grade;
    public static javax.swing.JButton button_refresh_scan;
    public static javax.swing.JButton button_refresh_spk;
    public static javax.swing.JButton button_search_dataScan;
    private javax.swing.JButton button_sistem_penimbangan;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel label_no_grade_spk;
    private javax.swing.JLabel label_total_berat;
    private javax.swing.JLabel label_total_data;
    private javax.swing.JLabel label_total_data_spk;
    private javax.swing.JLabel label_total_scan_barcode;
    private javax.swing.JLabel label_total_scan_grade;
    private javax.swing.JLabel label_total_scan_satuan;
    private javax.swing.JTextField txt_berat_plastik;
    private javax.swing.JTextField txt_berat_timbangan;
    private javax.swing.JTextField txt_kode_spk1;
    private javax.swing.JTextField txt_kode_spk2;
    private javax.swing.JTextField txt_search_qr;
    // End of variables declaration//GEN-END:variables
}
