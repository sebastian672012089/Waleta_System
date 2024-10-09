package waleta_system.BahanBaku;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.event.KeyEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
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
import waleta_system.MainForm;

public class JPanel_BahanBakuMasuk extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    String akses;

    public JPanel_BahanBakuMasuk() {
        initComponents();
        TableRowSorter tableRowSorter = new TableRowSorter(Table_Bahan_Baku_Masuk.getModel());
        Table_Bahan_Baku_Masuk.setRowSorter(tableRowSorter);
    }

    public void init(String akses) {
        this.akses = akses;
        if (MainForm.Login_kodeBagian == 244 || MainForm.Login_kodeBagian == 245 || MainForm.Login_idPegawai.equals("20180102221")) {//MANAGER OPERATIONAL / KADEP BAHAN MENTAH
            button_hapus_pecah.setEnabled(true);
        } else {
            button_hapus_pecah.setEnabled(false);
        }
        Table_Bahan_Baku_Masuk.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 11));
        refreshTable();

        Table_Bahan_Baku_Masuk.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && Table_Bahan_Baku_Masuk.getSelectedRow() != -1) {
                    try {
                        // TODO add your handling code here:
                        int i = Table_Bahan_Baku_Masuk.getSelectedRow();

                        CheckBox_uji_kerapatan.setSelected(Table_Bahan_Baku_Masuk.getValueAt(i, 13).equals("Passed"));
                        CheckBox_uji_kerusakan.setSelected(Table_Bahan_Baku_Masuk.getValueAt(i, 14).equals("Passed"));
                        CheckBox_uji_basah.setSelected(Table_Bahan_Baku_Masuk.getValueAt(i, 15).equals("Passed"));

                        //set tanggal harus setelah tanggal masuk
                        /*Date_Grading.setMinSelectableDate(new SimpleDateFormat("yyyy-MM-dd").parse(date_masuk));*/
                        if (Table_Bahan_Baku_Masuk.getValueAt(i, 16) == null) {
                            Date_Grading.setDate(null);
                        } else {
                            String date_grading = Table_Bahan_Baku_Masuk.getValueAt(i, 16).toString();
                            Date_Grading.setDate(new SimpleDateFormat("yyyy-MM-dd").parse(date_grading));
                        }
                        if (Table_Bahan_Baku_Masuk.getValueAt(i, 17) == null) {
                            Date_timbang.setDate(null);
                        } else {
                            String date_timbang = Table_Bahan_Baku_Masuk.getValueAt(i, 17).toString();
                            Date_timbang.setDate(new SimpleDateFormat("yyyy-MM-dd").parse(date_timbang));
                        }
                    } catch (ParseException ex) {
                        Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });

        Table_Grading_Bahan_Baku.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && Table_Grading_Bahan_Baku.getSelectedRow() != -1) {
                    int i = Table_Grading_Bahan_Baku.getSelectedRow();
                    refreshTable_pecah_lp(Table_Grading_Bahan_Baku.getValueAt(i, 0).toString());

                    if ((int) Table_Grading_Bahan_Baku.getValueAt(i, 5) > 0) {
                        button_input_pecah.setEnabled(false);
                    } else {
                        button_input_pecah.setEnabled(true);
                    }
                }
            }
        });
    }

    public void refreshTable() {
        try {
            int keping_grading = 0, gram_grading = 0;
            String showCMP = "";
            decimalFormat.setMaximumFractionDigits(0);
            decimalFormat.setGroupingUsed(true);
            DefaultTableModel model = (DefaultTableModel) Table_Bahan_Baku_Masuk.getModel();
            model.setRowCount(0);
            if (CheckBox_showCMP.isSelected()) {
                showCMP = "";
            } else {
                showCMP = "AND `no_kartu_waleta` NOT LIKE '%CMP%' ";
            }

            String filter_tanggal = "";
            if (Date_filter1.getDate() != null && Date_filter2.getDate() != null) {
                switch (ComboBox_filterTanggal.getSelectedIndex()) {
                    case 0:
                        filter_tanggal = " AND `tgl_masuk` BETWEEN '" + dateFormat.format(Date_filter1.getDate()) + "' AND '" + dateFormat.format(Date_filter2.getDate()) + "'";
                        break;
                    case 1:
                        filter_tanggal = " AND `tgl_kh` BETWEEN '" + dateFormat.format(Date_filter1.getDate()) + "' AND '" + dateFormat.format(Date_filter2.getDate()) + "'";
                        break;
                    case 2:
                        filter_tanggal = " AND `tgl_panen` BETWEEN '" + dateFormat.format(Date_filter1.getDate()) + "' AND '" + dateFormat.format(Date_filter2.getDate()) + "'";
                        break;
                    case 3:
                        filter_tanggal = " AND `tgl_grading` BETWEEN '" + dateFormat.format(Date_filter1.getDate()) + "' AND '" + dateFormat.format(Date_filter2.getDate()) + "'";
                        break;
                    case 4:
                        filter_tanggal = " AND `tgl_timbang` BETWEEN '" + dateFormat.format(Date_filter1.getDate()) + "' AND '" + dateFormat.format(Date_filter2.getDate()) + "'";
                        break;
                    default:
                        break;
                }
            }
            sql = "SELECT `no_kartu_waleta`, `no_kartu_waleta2`, `no_kartu_pengirim`, `tb_supplier`.`nama_supplier`, `tb_rumah_burung`.`nama_rumah_burung`, `tgl_kh`, `tgl_masuk`, `jumlah_koli`, `berat_awal`, `kadar_air_nota`, `berat_waleta`, `kadar_air_waleta`, `uji_kerapatan`, `uji_kerusakan`, `uji_basah`, `tgl_grading`, `tgl_timbang`, `berat_real`, `keping_real`, `kadar_air_bahan_baku`, `tgl_panen`, `status_kartu_baku` \n"
                    + "FROM `tb_bahan_baku_masuk` \n"
                    + "JOIN `tb_rumah_burung` ON `tb_bahan_baku_masuk`.`no_registrasi`=`tb_rumah_burung`.`no_registrasi`\n"
                    + "JOIN `tb_supplier` ON `tb_bahan_baku_masuk`.`kode_supplier`=`tb_supplier`.`kode_supplier` "
                    + "WHERE `no_kartu_waleta` LIKE '%" + txt_search_bahan_masuk.getText() + "%' " + showCMP + " AND `nama_supplier` LIKE '%" + txt_search_supplier.getText() + "%' AND `nama_rumah_burung` LIKE '%" + txt_search_rb.getText() + "%'"
                    + filter_tanggal + " ORDER BY `tgl_masuk` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[25];
            while (rs.next()) {
                row[0] = rs.getString("no_kartu_waleta");
                row[1] = rs.getString("no_kartu_waleta2");
                row[2] = rs.getString("no_kartu_pengirim");
                row[3] = rs.getString("nama_supplier");
                row[4] = rs.getString("nama_rumah_burung");
                row[5] = rs.getDate("tgl_kh");
                row[6] = rs.getDate("tgl_masuk");
                row[7] = rs.getDate("tgl_panen");
                row[8] = rs.getInt("jumlah_koli");
                row[9] = rs.getInt("berat_awal");
                row[10] = rs.getFloat("kadar_air_nota");
                row[11] = rs.getInt("berat_waleta");
                row[12] = rs.getFloat("kadar_air_waleta");
                row[13] = rs.getString("uji_kerapatan");
                row[14] = rs.getString("uji_kerusakan");
                row[15] = rs.getString("uji_basah");
                row[16] = rs.getDate("tgl_grading");
                row[17] = rs.getDate("tgl_timbang");
                row[18] = rs.getInt("keping_real");
                row[19] = rs.getInt("berat_real");
                row[20] = rs.getFloat("kadar_air_bahan_baku");
                if (rs.getInt("status_kartu_baku") == 0) {
                    row[21] = "PROSES";
                } else {
                    row[21] = "SELESAI";
                }
                keping_grading = keping_grading + rs.getInt("keping_real");
                gram_grading = gram_grading + rs.getInt("berat_real");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_Bahan_Baku_Masuk);
            Table_Bahan_Baku_Masuk.setDefaultRenderer(Float.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                    if (column == 19) {
                        if ((Float) Table_Bahan_Baku_Masuk.getValueAt(row, column) > 11) {
                            if (isSelected) {
                                comp.setBackground(Color.gray);
                                comp.setForeground(Color.red);
                            } else {
                                comp.setBackground(Color.PINK);
                                comp.setForeground(Color.red);
                            }
                        } else {
                            if (isSelected) {
                                comp.setBackground(Table_Bahan_Baku_Masuk.getSelectionBackground());
                                comp.setForeground(Table_Bahan_Baku_Masuk.getSelectionForeground());
                            } else {
                                comp.setBackground(Table_Bahan_Baku_Masuk.getBackground());
                                comp.setForeground(Table_Bahan_Baku_Masuk.getForeground());
                            }
                        }
                    } else {
                        if (isSelected) {
                            comp.setBackground(Table_Bahan_Baku_Masuk.getSelectionBackground());
                            comp.setForeground(Table_Bahan_Baku_Masuk.getSelectionForeground());
                        } else {
                            comp.setBackground(Table_Bahan_Baku_Masuk.getBackground());
                            comp.setForeground(Table_Bahan_Baku_Masuk.getForeground());
                        }
                    }
                    return comp;
                }
            });
            Table_Bahan_Baku_Masuk.repaint();
            int rowData = Table_Bahan_Baku_Masuk.getRowCount();
            label_total_data_bahan_baku_masuk.setText(Integer.toString(rowData));
            label_total_keping_grading.setText(decimalFormat.format(keping_grading));
            label_total_gram_grading.setText(decimalFormat.format(gram_grading));
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_BahanBakuMasuk.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_grading() {
        try {
            int keping_grading = 0, gram_grading = 0;
            decimalFormat.setMaximumFractionDigits(0);
            decimalFormat.setGroupingUsed(true);
            DefaultTableModel model = (DefaultTableModel) Table_Grading_Bahan_Baku.getModel();
            model.setRowCount(0);

            sql = "SELECT `tb_grading_bahan_baku`.`no_grading`, `no_kartu_waleta`, `kode_grade`, `tb_grading_bahan_baku`.`jumlah_keping`, `tb_grading_bahan_baku`.`total_berat`, COUNT(`tb_bahan_baku_pecah_kartu`.`kode_pecah_kartu`) AS 'pecah_lp'\n"
                    + "FROM `tb_grading_bahan_baku` \n"
                    + "LEFT JOIN `tb_bahan_baku_pecah_kartu` ON `tb_grading_bahan_baku`.`no_grading` = `tb_bahan_baku_pecah_kartu`.`no_grading`\n"
                    + "WHERE `no_kartu_waleta` LIKE '%" + txt_search_grading_no_kartu.getText() + "%' AND `kode_grade` LIKE '%" + txt_search_grading_grade.getText() + "%'\n"
                    + "GROUP BY `tb_grading_bahan_baku`.`no_grading` "
                    + "ORDER BY `tb_grading_bahan_baku`.`no_grading` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[10];
            while (rs.next()) {
                row[0] = rs.getInt("no_grading");
                row[1] = rs.getString("no_kartu_waleta");
                row[2] = rs.getString("kode_grade");
                row[3] = rs.getInt("jumlah_keping");
                row[4] = rs.getInt("total_berat");
                row[5] = rs.getInt("pecah_lp");
                keping_grading = keping_grading + rs.getInt("jumlah_keping");
                gram_grading = gram_grading + rs.getInt("total_berat");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_Grading_Bahan_Baku);
            int rowData = Table_Grading_Bahan_Baku.getRowCount();
            label_total_data_grading.setText(decimalFormat.format(rowData));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_BahanBakuMasuk.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_pecah_lp(String no_grading) {
        try {
            decimalFormat.setMaximumFractionDigits(0);
            decimalFormat.setGroupingUsed(true);
            int total_keping_riil = 0;
            int total_keping_upah = 0;
            int total_gram_riil = 0;
            int total_gram_sistem = 0;

            DefaultTableModel model = (DefaultTableModel) Table_pecah_lp.getModel();
            model.setRowCount(0);

            sql = "SELECT PCH.`kode_pecah_kartu`, PCH.`no_grading`, `tb_grading_bahan_baku`.`no_kartu_waleta`, `tb_grading_bahan_baku`.`kode_grade`, PCH.`jumlah_keping`, PCH.`keping_upah`, PCH.`berat_basah`, PCH.`berat_riil`, PCH.`jenis_bulu_lp`, PCH.`memo_lp`, \n"
                    + "PCH.`kaki_besar_lp`, PCH.`kaki_kecil_lp`, PCH.`hilang_kaki_lp`, PCH.`ada_susur_lp`, PCH.`ada_susur_besar_lp`, PCH.`tanpa_susur_lp`, PCH.`utuh_lp`, PCH.`hilang_ujung_lp`, PCH.`pecah_1_lp`, PCH.`pecah_2`, PCH.`jumlah_sobek`, PCH.`sobek_lepas`, PCH.`jumlah_gumpil`, "
                    + "`tb_laporan_produksi`.`no_laporan_produksi` \n"
                    + "FROM `tb_bahan_baku_pecah_kartu` PCH \n"
                    + "LEFT JOIN `tb_grading_bahan_baku` ON PCH.`no_grading` = `tb_grading_bahan_baku`.`no_grading`\n"
                    + "LEFT JOIN `tb_laporan_produksi` ON PCH.`kode_pecah_kartu` = `tb_laporan_produksi`.`kode_pecah_lp`\n"
                    + "WHERE PCH.`no_grading` = '" + no_grading + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[25];
            while (rs.next()) {
                row[0] = rs.getInt("kode_pecah_kartu");
                row[1] = rs.getInt("no_grading");
                row[2] = rs.getString("no_kartu_waleta");
                row[3] = rs.getString("kode_grade");
                row[4] = rs.getFloat("jumlah_keping");
                row[5] = rs.getFloat("keping_upah");
                row[6] = rs.getFloat("berat_basah");
                row[7] = rs.getFloat("berat_riil");
                row[8] = rs.getString("jenis_bulu_lp");
                row[9] = rs.getString("memo_lp");
                row[10] = rs.getInt("kaki_besar_lp");
                row[11] = rs.getInt("kaki_kecil_lp");
                row[12] = rs.getInt("hilang_kaki_lp");
                row[13] = rs.getInt("ada_susur_besar_lp");
                row[14] = rs.getInt("ada_susur_lp");
                row[15] = rs.getInt("tanpa_susur_lp");
                row[16] = rs.getInt("utuh_lp");
                row[17] = rs.getInt("hilang_ujung_lp");
                row[18] = rs.getInt("pecah_1_lp");
                row[19] = rs.getInt("pecah_2");
                row[20] = rs.getInt("jumlah_sobek");
                row[21] = rs.getInt("sobek_lepas");
                row[22] = rs.getInt("jumlah_gumpil");
                row[23] = rs.getString("no_laporan_produksi");
                model.addRow(row);

                total_keping_riil += rs.getInt("jumlah_keping");
                total_keping_upah += rs.getInt("keping_upah");
                total_gram_sistem += rs.getInt("berat_basah");
                total_gram_riil += rs.getInt("berat_riil");
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_pecah_lp);
            int rowData = Table_pecah_lp.getRowCount();

            label_total_keping_riil.setText(decimalFormat.format(total_keping_riil));
            label_total_keping_upah.setText(decimalFormat.format(total_keping_upah));
            label_total_gram_sistem.setText(decimalFormat.format(total_gram_sistem));
            label_total_gram_riil.setText(decimalFormat.format(total_gram_riil));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_BahanBakuMasuk.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_stok_pecah_lp() {
        try {
            decimalFormat.setMaximumFractionDigits(0);
            decimalFormat.setGroupingUsed(true);
            DefaultTableModel model = (DefaultTableModel) Table_stok_pecah_lp.getModel();
            model.setRowCount(0);
            String filter_status = "";
            switch (ComboBox_status_stok_pecah.getSelectedIndex()) {
                case 0:
                    filter_status = "";
                    break;
                case 1:
                    filter_status = "AND `tb_laporan_produksi`.`no_laporan_produksi` IS NULL \n";
                    break;
                case 2:
                    filter_status = "AND `tb_laporan_produksi`.`no_laporan_produksi` IS NOT NULL \n";
                    break;
                default:
                    break;
            }

            sql = "SELECT PCH.`kode_pecah_kartu`, PCH.`no_grading`, `tb_grading_bahan_baku`.`no_kartu_waleta`, `tb_grading_bahan_baku`.`kode_grade`, PCH.`jumlah_keping`, PCH.`keping_upah`, PCH.`berat_basah`, PCH.`berat_riil`, PCH.`jenis_bulu_lp`, PCH.`memo_lp`, \n"
                    + "PCH.`kaki_besar_lp`, PCH.`kaki_kecil_lp`, PCH.`hilang_kaki_lp`, PCH.`ada_susur_lp`, PCH.`ada_susur_besar_lp`, PCH.`tanpa_susur_lp`, PCH.`utuh_lp`, PCH.`hilang_ujung_lp`, PCH.`pecah_1_lp`, PCH.`pecah_2`, PCH.`jumlah_sobek`, PCH.`sobek_lepas`, PCH.`jumlah_gumpil`, "
                    + "`tb_laporan_produksi`.`no_laporan_produksi` \n"
                    + "FROM `tb_bahan_baku_pecah_kartu` PCH \n"
                    + "LEFT JOIN `tb_grading_bahan_baku` ON PCH.`no_grading` = `tb_grading_bahan_baku`.`no_grading`\n"
                    + "LEFT JOIN `tb_laporan_produksi` ON PCH.`kode_pecah_kartu` = `tb_laporan_produksi`.`kode_pecah_lp`\n"
                    + "WHERE "
                    + "`tb_grading_bahan_baku`.`no_kartu_waleta` LIKE '%" + txt_search_no_kartu_stok_pecah.getText() + "%' \n"
                    + "AND `tb_grading_bahan_baku`.`kode_grade` LIKE '%" + txt_search_grade_stok_pecah.getText() + "%' \n"
                    + filter_status;
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[25];
            while (rs.next()) {
                row[0] = rs.getInt("kode_pecah_kartu");
                row[1] = rs.getInt("no_grading");
                row[2] = rs.getString("no_kartu_waleta");
                row[3] = rs.getString("kode_grade");
                row[4] = rs.getFloat("jumlah_keping");
                row[5] = rs.getFloat("keping_upah");
                row[6] = rs.getFloat("berat_basah");
                row[7] = rs.getFloat("berat_riil");
                row[8] = rs.getString("jenis_bulu_lp");
                row[9] = rs.getString("memo_lp");
                row[10] = rs.getInt("kaki_besar_lp");
                row[11] = rs.getInt("kaki_kecil_lp");
                row[12] = rs.getInt("hilang_kaki_lp");
                row[13] = rs.getInt("ada_susur_besar_lp");
                row[14] = rs.getInt("ada_susur_lp");
                row[15] = rs.getInt("tanpa_susur_lp");
                row[16] = rs.getInt("utuh_lp");
                row[17] = rs.getInt("hilang_ujung_lp");
                row[18] = rs.getInt("pecah_1_lp");
                row[19] = rs.getInt("pecah_2");
                row[20] = rs.getInt("jumlah_sobek");
                row[21] = rs.getInt("sobek_lepas");
                row[22] = rs.getInt("jumlah_gumpil");
                row[23] = rs.getString("no_laporan_produksi");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_stok_pecah_lp);
            int rowData = Table_stok_pecah_lp.getRowCount();
            label_total_data_stok_pecah.setText(decimalFormat.format(rowData));

            DefaultTableModel model_rekap = (DefaultTableModel) tabel_rekap_grade.getModel();
            model_rekap.setRowCount(0);
            sql = "SELECT `kode_grade`, SUM(`keping_upah`) AS 'keping', SUM(`berat_basah`) AS 'gram'\n"
                    + "FROM (" + sql + ") DATA "
                    + "GROUP BY `kode_grade`";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                model_rekap.addRow(new Object[]{rs.getString("kode_grade"), rs.getFloat("keping"), rs.getFloat("gram")});
            }
            ColumnsAutoSizer.sizeColumnsToFit(tabel_rekap_grade);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_BahanBakuMasuk.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_grading_conveyor() {
        try {
            decimalFormat.setMaximumFractionDigits(0);
            decimalFormat.setGroupingUsed(true);
            DefaultTableModel model = (DefaultTableModel) Table_grading_conveyor.getModel();
            model.setRowCount(0);

            sql = "SELECT `tb_grading_bahan_baku_conveyor`.`no_grading`, `tb_grading_bahan_baku_conveyor`.`no_kartu_waleta`, `tb_grading_bahan_baku_conveyor`.`kode_grade`, `tb_grading_bahan_baku_conveyor`.`jumlah_keping`, `created_at`, `updated_at`, "
                    + "`tb_grading_bahan_baku`.`jumlah_keping` AS 'keping_manual', `tb_grading_bahan_baku`.`total_berat` AS 'berat_manual'"
                    + "FROM `tb_grading_bahan_baku_conveyor` "
                    + "LEFT JOIN `tb_grading_bahan_baku` ON `tb_grading_bahan_baku`.`no_kartu_waleta` = `tb_grading_bahan_baku_conveyor`.`no_kartu_waleta` AND `tb_grading_bahan_baku`.`kode_grade` = `tb_grading_bahan_baku_conveyor`.`kode_grade` \n"
                    + "WHERE "
                    + "`tb_grading_bahan_baku_conveyor`.`no_kartu_waleta` LIKE '%" + txt_search_no_kartu_grading_conveyor.getText() + "%' \n"
                    + "AND `tb_grading_bahan_baku_conveyor`.`kode_grade` LIKE '%" + txt_search_grade_grading_conveyor.getText() + "%' \n";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[10];
            while (rs.next()) {
                row[0] = rs.getInt("no_grading");
                row[1] = rs.getString("no_kartu_waleta");
                row[2] = rs.getString("kode_grade");
                row[3] = rs.getFloat("jumlah_keping");
                row[4] = rs.getTimestamp("created_at");
                row[5] = rs.getFloat("keping_manual");
                row[6] = rs.getFloat("berat_manual");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_grading_conveyor);
            int rowData = Table_grading_conveyor.getRowCount();
            label_total_data_grading_conveyor.setText(decimalFormat.format(rowData));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_BahanBakuMasuk.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void executeSQLQuery(String query, String message) {
        Utility.db.getConnection();
        try {
            Utility.db.getConnection().createStatement();
            if ((Utility.db.getStatement().executeUpdate(query)) == 1) {
                JOptionPane.showMessageDialog(this, "data " + message + " Successfully");
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, "data not " + message);
            }
        } catch (SQLException | HeadlessException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, e);
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

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel_Bahan_Baku_Masuk = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        Table_Bahan_Baku_Masuk = new javax.swing.JTable();
        jPanel_operation_uji_bahan_baku = new javax.swing.JPanel();
        button_save_uji_kemasan = new javax.swing.JButton();
        CheckBox_uji_kerapatan = new javax.swing.JCheckBox();
        CheckBox_uji_kerusakan = new javax.swing.JCheckBox();
        CheckBox_uji_basah = new javax.swing.JCheckBox();
        jPanel_operation_grading = new javax.swing.JPanel();
        label_tgl_grading = new javax.swing.JLabel();
        Date_Grading = new com.toedter.calendar.JDateChooser();
        button_save_timbang_bahan_baku = new javax.swing.JButton();
        button_detail_grading = new javax.swing.JButton();
        label_tgl_grading1 = new javax.swing.JLabel();
        Date_timbang = new com.toedter.calendar.JDateChooser();
        label_total_data_bahan_baku_masuk = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        label_total_keping_grading = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        label_total_gram_grading = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        button_tv_grading_baku = new javax.swing.JButton();
        button_selesai = new javax.swing.JButton();
        button_Catatan_Penimbangan_Sarang_Burung_Mentah = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        txt_search_supplier = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        txt_search_rb = new javax.swing.JTextField();
        txt_search_bahan_masuk = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        Date_filter1 = new com.toedter.calendar.JDateChooser();
        Date_filter2 = new com.toedter.calendar.JDateChooser();
        button_search = new javax.swing.JButton();
        ComboBox_filterTanggal = new javax.swing.JComboBox<>();
        CheckBox_showCMP = new javax.swing.JCheckBox();
        button_export_BahanBakuMasuk = new javax.swing.JButton();
        button_Catatan_Penimbangan_Sarang_Burung_Mentah_print1 = new javax.swing.JButton();
        button_delete_bahan_baku_masuk = new javax.swing.JButton();
        button_insert_bahan_baku_masuk = new javax.swing.JButton();
        button_update_bahan_baku_masuk = new javax.swing.JButton();
        jPanel_grading = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        Table_pecah_lp = new javax.swing.JTable();
        jLabel18 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        txt_search_grading_no_kartu = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        Table_Grading_Bahan_Baku = new javax.swing.JTable();
        jLabel17 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        txt_search_grading_grade = new javax.swing.JTextField();
        label_total_data_grading = new javax.swing.JLabel();
        button_search_grading = new javax.swing.JButton();
        button_export_grading = new javax.swing.JButton();
        jLabel19 = new javax.swing.JLabel();
        button_input_pecah = new javax.swing.JButton();
        button_export_pecah = new javax.swing.JButton();
        button_edit_pecah = new javax.swing.JButton();
        button_gabung_pecah = new javax.swing.JButton();
        jLabel28 = new javax.swing.JLabel();
        label_total_keping_riil = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        label_total_gram_sistem = new javax.swing.JLabel();
        label_total_gram_riil = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        label_total_keping_upah = new javax.swing.JLabel();
        button_hapus_pecah = new javax.swing.JButton();
        jPanel_StokPecah = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        Table_stok_pecah_lp = new javax.swing.JTable();
        button_export_stok_pecah = new javax.swing.JButton();
        jLabel21 = new javax.swing.JLabel();
        label_total_data_stok_pecah = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        txt_search_no_kartu_stok_pecah = new javax.swing.JTextField();
        button_search_stok_pecah = new javax.swing.JButton();
        txt_search_grade_stok_pecah = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        ComboBox_status_stok_pecah = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tabel_rekap_grade = new javax.swing.JTable();
        jPanel_GradingConveyor = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        Table_grading_conveyor = new javax.swing.JTable();
        button_export_grading_conveyor = new javax.swing.JButton();
        jLabel25 = new javax.swing.JLabel();
        label_total_data_grading_conveyor = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        txt_search_no_kartu_grading_conveyor = new javax.swing.JTextField();
        button_search_grading_conveyor = new javax.swing.JButton();
        txt_search_grade_grading_conveyor = new javax.swing.JTextField();

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N

        jPanel_Bahan_Baku_Masuk.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_Bahan_Baku_Masuk.setPreferredSize(new java.awt.Dimension(1366, 700));

        Table_Bahan_Baku_Masuk.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_Bahan_Baku_Masuk.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No Kartu Waleta", "No Kartu 2", "No Kartu Asal", "Supplier", "R. Burung", "Tgl KH", "Tgl Masuk", "Tgl Panen", "Koli", "Berat Nota", "KA Nota (%)", "Berat Waleta", "KA Waleta (%)", "Uji Kerapatan", "Uji Kerusakan", "Uji Basah", "Tgl Grading", "Tgl Timbang", "Total Keping", "Berat Real", "KA (%)", "Status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Float.class, java.lang.Integer.class, java.lang.Float.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Float.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Table_Bahan_Baku_Masuk.getTableHeader().setReorderingAllowed(false);
        jScrollPane5.setViewportView(Table_Bahan_Baku_Masuk);

        jPanel_operation_uji_bahan_baku.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_operation_uji_bahan_baku.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Uji Kemasan Barang", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Yu Gothic", 0, 12))); // NOI18N
        jPanel_operation_uji_bahan_baku.setName("aah"); // NOI18N

        button_save_uji_kemasan.setBackground(new java.awt.Color(255, 255, 255));
        button_save_uji_kemasan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_save_uji_kemasan.setText("Simpan");
        button_save_uji_kemasan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_save_uji_kemasanActionPerformed(evt);
            }
        });

        CheckBox_uji_kerapatan.setBackground(new java.awt.Color(255, 255, 255));
        CheckBox_uji_kerapatan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        CheckBox_uji_kerapatan.setText("Kemasan Rapat ?");

        CheckBox_uji_kerusakan.setBackground(new java.awt.Color(255, 255, 255));
        CheckBox_uji_kerusakan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        CheckBox_uji_kerusakan.setText("Kemasan Rusak ?");

        CheckBox_uji_basah.setBackground(new java.awt.Color(255, 255, 255));
        CheckBox_uji_basah.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        CheckBox_uji_basah.setText("Kemasan Basah ?");

        javax.swing.GroupLayout jPanel_operation_uji_bahan_bakuLayout = new javax.swing.GroupLayout(jPanel_operation_uji_bahan_baku);
        jPanel_operation_uji_bahan_baku.setLayout(jPanel_operation_uji_bahan_bakuLayout);
        jPanel_operation_uji_bahan_bakuLayout.setHorizontalGroup(
            jPanel_operation_uji_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_operation_uji_bahan_bakuLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_operation_uji_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_operation_uji_bahan_bakuLayout.createSequentialGroup()
                        .addComponent(button_save_uji_kemasan)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel_operation_uji_bahan_bakuLayout.createSequentialGroup()
                        .addGroup(jPanel_operation_uji_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(CheckBox_uji_kerusakan)
                            .addComponent(CheckBox_uji_kerapatan)
                            .addComponent(CheckBox_uji_basah))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel_operation_uji_bahan_bakuLayout.setVerticalGroup(
            jPanel_operation_uji_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_operation_uji_bahan_bakuLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(CheckBox_uji_kerapatan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(CheckBox_uji_kerusakan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(CheckBox_uji_basah, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(button_save_uji_kemasan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel_operation_grading.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_operation_grading.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Grading", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Yu Gothic", 0, 12))); // NOI18N
        jPanel_operation_grading.setName("aah"); // NOI18N

        label_tgl_grading.setBackground(new java.awt.Color(255, 255, 255));
        label_tgl_grading.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_tgl_grading.setText("Tanggal Grading :");

        Date_Grading.setBackground(new java.awt.Color(255, 255, 255));
        Date_Grading.setDateFormatString("dd MMMM yyyy");
        Date_Grading.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Date_Grading.setMaxSelectableDate(new Date());

        button_save_timbang_bahan_baku.setBackground(new java.awt.Color(255, 255, 255));
        button_save_timbang_bahan_baku.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_save_timbang_bahan_baku.setText("Simpan");
        button_save_timbang_bahan_baku.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_save_timbang_bahan_bakuActionPerformed(evt);
            }
        });

        button_detail_grading.setBackground(new java.awt.Color(255, 255, 255));
        button_detail_grading.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_detail_grading.setText("Data Grading");
        button_detail_grading.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_detail_gradingActionPerformed(evt);
            }
        });

        label_tgl_grading1.setBackground(new java.awt.Color(255, 255, 255));
        label_tgl_grading1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_tgl_grading1.setText("Tanggal Timbang :");

        Date_timbang.setBackground(new java.awt.Color(255, 255, 255));
        Date_timbang.setDateFormatString("dd MMMM yyyy");
        Date_timbang.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Date_timbang.setMaxSelectableDate(new Date());

        javax.swing.GroupLayout jPanel_operation_gradingLayout = new javax.swing.GroupLayout(jPanel_operation_grading);
        jPanel_operation_grading.setLayout(jPanel_operation_gradingLayout);
        jPanel_operation_gradingLayout.setHorizontalGroup(
            jPanel_operation_gradingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_operation_gradingLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_operation_gradingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_operation_gradingLayout.createSequentialGroup()
                        .addComponent(button_save_timbang_bahan_baku)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_detail_grading))
                    .addGroup(jPanel_operation_gradingLayout.createSequentialGroup()
                        .addComponent(label_tgl_grading, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_Grading, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_operation_gradingLayout.createSequentialGroup()
                        .addComponent(label_tgl_grading1, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_timbang, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel_operation_gradingLayout.setVerticalGroup(
            jPanel_operation_gradingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_operation_gradingLayout.createSequentialGroup()
                .addGroup(jPanel_operation_gradingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(Date_Grading, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_tgl_grading, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_gradingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(Date_timbang, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_tgl_grading1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_gradingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_save_timbang_bahan_baku, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_detail_grading, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        label_total_data_bahan_baku_masuk.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_bahan_baku_masuk.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_data_bahan_baku_masuk.setText("0");

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel8.setText("Total Data :");

        label_total_keping_grading.setBackground(new java.awt.Color(255, 255, 255));
        label_total_keping_grading.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_keping_grading.setText("0");

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel14.setText("Total Keping Grading :");

        label_total_gram_grading.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_grading.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_gram_grading.setText("0");

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel15.setText("Total Gram Grading :");

        button_tv_grading_baku.setBackground(new java.awt.Color(255, 255, 255));
        button_tv_grading_baku.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_tv_grading_baku.setText("TV Grading Baku");
        button_tv_grading_baku.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_tv_grading_bakuActionPerformed(evt);
            }
        });

        button_selesai.setBackground(new java.awt.Color(255, 255, 255));
        button_selesai.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_selesai.setText("SELESAI");
        button_selesai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_selesaiActionPerformed(evt);
            }
        });

        button_Catatan_Penimbangan_Sarang_Burung_Mentah.setBackground(new java.awt.Color(255, 255, 255));
        button_Catatan_Penimbangan_Sarang_Burung_Mentah.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_Catatan_Penimbangan_Sarang_Burung_Mentah.setText("Print Semua Catatan Penimbangan");
        button_Catatan_Penimbangan_Sarang_Burung_Mentah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_Catatan_Penimbangan_Sarang_Burung_MentahActionPerformed(evt);
            }
        });

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel12.setText("Supplier :");

        txt_search_supplier.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_supplier.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_supplierKeyPressed(evt);
            }
        });

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel13.setText("Rumah Burung :");

        txt_search_rb.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_rb.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_rbKeyPressed(evt);
            }
        });

        txt_search_bahan_masuk.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_bahan_masuk.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_bahan_masukKeyPressed(evt);
            }
        });

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel11.setText("By No Kartu Waleta :");

        Date_filter1.setBackground(new java.awt.Color(255, 255, 255));
        Date_filter1.setDateFormatString("dd MMM yyyy");
        Date_filter1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Date_filter2.setBackground(new java.awt.Color(255, 255, 255));
        Date_filter2.setDateFormatString("dd MMM yyyy");
        Date_filter2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        button_search.setBackground(new java.awt.Color(255, 255, 255));
        button_search.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search.setText("Search");
        button_search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_searchActionPerformed(evt);
            }
        });

        ComboBox_filterTanggal.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_filterTanggal.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tanggal Masuk", "Tanggal KH", "Tanggal Panen", "Tanggal Grading", "Tanggal Timbang" }));

        CheckBox_showCMP.setBackground(new java.awt.Color(255, 255, 255));
        CheckBox_showCMP.setText("Show CMP");
        CheckBox_showCMP.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                CheckBox_showCMPItemStateChanged(evt);
            }
        });

        button_export_BahanBakuMasuk.setBackground(new java.awt.Color(255, 255, 255));
        button_export_BahanBakuMasuk.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_BahanBakuMasuk.setText("Export To Excel");
        button_export_BahanBakuMasuk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_BahanBakuMasukActionPerformed(evt);
            }
        });

        button_Catatan_Penimbangan_Sarang_Burung_Mentah_print1.setBackground(new java.awt.Color(255, 255, 255));
        button_Catatan_Penimbangan_Sarang_Burung_Mentah_print1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_Catatan_Penimbangan_Sarang_Burung_Mentah_print1.setText("Print 1 Catatan Penimbangan");
        button_Catatan_Penimbangan_Sarang_Burung_Mentah_print1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_Catatan_Penimbangan_Sarang_Burung_Mentah_print1ActionPerformed(evt);
            }
        });

        button_delete_bahan_baku_masuk.setBackground(new java.awt.Color(255, 255, 255));
        button_delete_bahan_baku_masuk.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_delete_bahan_baku_masuk.setText("Delete");
        button_delete_bahan_baku_masuk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_delete_bahan_baku_masukActionPerformed(evt);
            }
        });

        button_insert_bahan_baku_masuk.setBackground(new java.awt.Color(255, 255, 255));
        button_insert_bahan_baku_masuk.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_insert_bahan_baku_masuk.setText("insert");
        button_insert_bahan_baku_masuk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_insert_bahan_baku_masukActionPerformed(evt);
            }
        });

        button_update_bahan_baku_masuk.setBackground(new java.awt.Color(255, 255, 255));
        button_update_bahan_baku_masuk.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_update_bahan_baku_masuk.setText("Edit");
        button_update_bahan_baku_masuk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_update_bahan_baku_masukActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_Bahan_Baku_MasukLayout = new javax.swing.GroupLayout(jPanel_Bahan_Baku_Masuk);
        jPanel_Bahan_Baku_Masuk.setLayout(jPanel_Bahan_Baku_MasukLayout);
        jPanel_Bahan_Baku_MasukLayout.setHorizontalGroup(
            jPanel_Bahan_Baku_MasukLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Bahan_Baku_MasukLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_Bahan_Baku_MasukLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5)
                    .addGroup(jPanel_Bahan_Baku_MasukLayout.createSequentialGroup()
                        .addGroup(jPanel_Bahan_Baku_MasukLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_Bahan_Baku_MasukLayout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_bahan_masuk, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_supplier, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_rb, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_filterTanggal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_filter1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_filter2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_search)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(CheckBox_showCMP)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(button_export_BahanBakuMasuk)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_tv_grading_baku)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_selesai))
                            .addGroup(jPanel_Bahan_Baku_MasukLayout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_data_bahan_baku_masuk)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_keping_grading)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_gram_grading)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(button_Catatan_Penimbangan_Sarang_Burung_Mentah)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_Catatan_Penimbangan_Sarang_Burung_Mentah_print1))
                            .addGroup(jPanel_Bahan_Baku_MasukLayout.createSequentialGroup()
                                .addComponent(button_insert_bahan_baku_masuk)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_update_bahan_baku_masuk)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_delete_bahan_baku_masuk)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel_operation_uji_bahan_baku, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel_operation_grading, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 87, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel_Bahan_Baku_MasukLayout.setVerticalGroup(
            jPanel_Bahan_Baku_MasukLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Bahan_Baku_MasukLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_Bahan_Baku_MasukLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_bahan_masuk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_supplier, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_rb, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export_BahanBakuMasuk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_filterTanggal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_filter1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_filter2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_search, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CheckBox_showCMP, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_tv_grading_baku, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_selesai, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_Bahan_Baku_MasukLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(label_total_data_bahan_baku_masuk)
                    .addComponent(jLabel8)
                    .addComponent(label_total_keping_grading)
                    .addComponent(jLabel14)
                    .addComponent(label_total_gram_grading)
                    .addComponent(jLabel15)
                    .addComponent(button_Catatan_Penimbangan_Sarang_Burung_Mentah_print1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_Catatan_Penimbangan_Sarang_Burung_Mentah, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 451, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_Bahan_Baku_MasukLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel_operation_grading, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel_operation_uji_bahan_baku, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel_Bahan_Baku_MasukLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(button_insert_bahan_baku_masuk)
                        .addComponent(button_update_bahan_baku_masuk)
                        .addComponent(button_delete_bahan_baku_masuk)))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Data Bahan Baku Masuk", jPanel_Bahan_Baku_Masuk);

        jPanel_grading.setBackground(new java.awt.Color(255, 255, 255));

        Table_pecah_lp.setAutoCreateRowSorter(true);
        Table_pecah_lp.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Table_pecah_lp.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode", "No Grading", "No Kartu", "Grade", "Kpg", "Kpg Upah", "Gram", "Gram Riil", "Jenis Bulu", "Memo LP", "kk BESAR", "kk KECIL", "Tanpa kk", "Susur BESAR", "Susur KECIL", "Tanpa Susur", "Utuh", "Hlg Ujung", "Pch 1", "Pch 2", "Sobek", "Sobek Lepas", "Gumpil", "No LP"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class
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
        Table_pecah_lp.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(Table_pecah_lp);

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel18.setText("Data pecah LP");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        txt_search_grading_no_kartu.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_search_grading_no_kartu.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_grading_no_kartuKeyPressed(evt);
            }
        });

        Table_Grading_Bahan_Baku.setAutoCreateRowSorter(true);
        Table_Grading_Bahan_Baku.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Table_Grading_Bahan_Baku.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No Grading", "No Kartu", "Grade", "Kpg", "Gram", "Pecah LP"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
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
        Table_Grading_Bahan_Baku.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(Table_Grading_Bahan_Baku);

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel17.setText("Grade Baku :");

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel16.setText("No Kartu :");

        txt_search_grading_grade.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_search_grading_grade.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_grading_gradeKeyPressed(evt);
            }
        });

        label_total_data_grading.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_grading.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_data_grading.setText("TOTAL");

        button_search_grading.setBackground(new java.awt.Color(255, 255, 255));
        button_search_grading.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_search_grading.setText("Search");
        button_search_grading.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_search_gradingActionPerformed(evt);
            }
        });

        button_export_grading.setBackground(new java.awt.Color(255, 255, 255));
        button_export_grading.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_export_grading.setText("Export");
        button_export_grading.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_gradingActionPerformed(evt);
            }
        });

        jLabel19.setBackground(new java.awt.Color(255, 255, 255));
        jLabel19.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel19.setText("Total Data :");

        button_input_pecah.setBackground(new java.awt.Color(255, 255, 255));
        button_input_pecah.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_input_pecah.setText("Input Pecah Pertama");
        button_input_pecah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_input_pecahActionPerformed(evt);
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
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_grading_no_kartu, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_grading_grade, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_search_grading)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_data_grading)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_input_pecah)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_export_grading)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_grading_no_kartu, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(button_search_grading, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_search_grading_grade, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_total_data_grading, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export_grading, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_input_pecah, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 571, Short.MAX_VALUE)
                .addContainerGap())
        );

        button_export_pecah.setBackground(new java.awt.Color(255, 255, 255));
        button_export_pecah.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_export_pecah.setText("Export");
        button_export_pecah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_pecahActionPerformed(evt);
            }
        });

        button_edit_pecah.setBackground(new java.awt.Color(255, 255, 255));
        button_edit_pecah.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_edit_pecah.setText("Edit Pecah");
        button_edit_pecah.setEnabled(false);
        button_edit_pecah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_edit_pecahActionPerformed(evt);
            }
        });

        button_gabung_pecah.setBackground(new java.awt.Color(255, 255, 255));
        button_gabung_pecah.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_gabung_pecah.setText("Gabung Pecah");
        button_gabung_pecah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_gabung_pecahActionPerformed(evt);
            }
        });

        jLabel28.setBackground(new java.awt.Color(255, 255, 255));
        jLabel28.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel28.setText("Total Keping Upah :");
        jLabel28.setFocusable(false);

        label_total_keping_riil.setBackground(new java.awt.Color(255, 255, 255));
        label_total_keping_riil.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_keping_riil.setForeground(new java.awt.Color(255, 0, 0));
        label_total_keping_riil.setText("0000");
        label_total_keping_riil.setFocusable(false);

        jLabel29.setBackground(new java.awt.Color(255, 255, 255));
        jLabel29.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel29.setText("Total Keping Riil :");
        jLabel29.setFocusable(false);

        jLabel30.setBackground(new java.awt.Color(255, 255, 255));
        jLabel30.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel30.setText("Total Gram :");
        jLabel30.setFocusable(false);

        label_total_gram_sistem.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_sistem.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_gram_sistem.setForeground(new java.awt.Color(255, 0, 0));
        label_total_gram_sistem.setText("0000");
        label_total_gram_sistem.setFocusable(false);

        label_total_gram_riil.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_riil.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_gram_riil.setForeground(new java.awt.Color(255, 0, 0));
        label_total_gram_riil.setText("0000");
        label_total_gram_riil.setFocusable(false);

        jLabel31.setBackground(new java.awt.Color(255, 255, 255));
        jLabel31.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel31.setText("Total Gram Riil :");
        jLabel31.setFocusable(false);

        label_total_keping_upah.setBackground(new java.awt.Color(255, 255, 255));
        label_total_keping_upah.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_keping_upah.setForeground(new java.awt.Color(255, 0, 0));
        label_total_keping_upah.setText("0000");
        label_total_keping_upah.setFocusable(false);

        button_hapus_pecah.setBackground(new java.awt.Color(255, 255, 255));
        button_hapus_pecah.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_hapus_pecah.setForeground(new java.awt.Color(255, 0, 0));
        button_hapus_pecah.setIcon(new javax.swing.ImageIcon(getClass().getResource("/waleta_system/Images/box_important.png"))); // NOI18N
        button_hapus_pecah.setText("Hapus semua pecahan");
        button_hapus_pecah.setIconTextGap(2);
        button_hapus_pecah.setMargin(new java.awt.Insets(2, 5, 2, 5));
        button_hapus_pecah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_hapus_pecahActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_gradingLayout = new javax.swing.GroupLayout(jPanel_grading);
        jPanel_grading.setLayout(jPanel_gradingLayout);
        jPanel_gradingLayout.setHorizontalGroup(
            jPanel_gradingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_gradingLayout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_gradingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 927, Short.MAX_VALUE)
                    .addGroup(jPanel_gradingLayout.createSequentialGroup()
                        .addComponent(jLabel18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_gabung_pecah)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_edit_pecah)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_hapus_pecah)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_export_pecah))
                    .addGroup(jPanel_gradingLayout.createSequentialGroup()
                        .addComponent(jLabel29)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_keping_riil)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel28)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_keping_upah)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel30)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gram_sistem)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel31)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gram_riil)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel_gradingLayout.setVerticalGroup(
            jPanel_gradingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_gradingLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_gradingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export_pecah, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_edit_pecah, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_gabung_pecah, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_hapus_pecah, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_gradingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_keping_riil, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_keping_upah, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_gram_riil, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_gram_sistem, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2)
                .addContainerGap())
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Grading Baku & Pecah LP", jPanel_grading);

        jPanel_StokPecah.setBackground(new java.awt.Color(255, 255, 255));

        Table_stok_pecah_lp.setAutoCreateRowSorter(true);
        Table_stok_pecah_lp.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Table_stok_pecah_lp.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode", "No Grading", "No Kartu", "Grade", "Kpg", "Kpg Upah", "Gram", "Gram Riil", "Jenis Bulu", "Memo LP", "kk BESAR", "kk KECIL", "Tanpa kk", "Susur BESAR", "Susur KECIL", "Tanpa Susur", "Utuh", "Hlg Ujung", "Pch 1", "Pch 2", "Sobek", "Sobek Lepas", "Gumpil", "No LP"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class
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
        Table_stok_pecah_lp.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(Table_stok_pecah_lp);

        button_export_stok_pecah.setBackground(new java.awt.Color(255, 255, 255));
        button_export_stok_pecah.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_export_stok_pecah.setText("Export");
        button_export_stok_pecah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_stok_pecahActionPerformed(evt);
            }
        });

        jLabel21.setBackground(new java.awt.Color(255, 255, 255));
        jLabel21.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel21.setText("Total Data :");

        label_total_data_stok_pecah.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_stok_pecah.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_data_stok_pecah.setText("0");

        jLabel22.setBackground(new java.awt.Color(255, 255, 255));
        jLabel22.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel22.setText("No Kartu :");

        jLabel23.setBackground(new java.awt.Color(255, 255, 255));
        jLabel23.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel23.setText("Grade Baku :");

        txt_search_no_kartu_stok_pecah.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_search_no_kartu_stok_pecah.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_no_kartu_stok_pecahKeyPressed(evt);
            }
        });

        button_search_stok_pecah.setBackground(new java.awt.Color(255, 255, 255));
        button_search_stok_pecah.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_search_stok_pecah.setText("Search");
        button_search_stok_pecah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_search_stok_pecahActionPerformed(evt);
            }
        });

        txt_search_grade_stok_pecah.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_search_grade_stok_pecah.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_grade_stok_pecahKeyPressed(evt);
            }
        });

        jLabel24.setBackground(new java.awt.Color(255, 255, 255));
        jLabel24.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel24.setText("Status :");

        ComboBox_status_stok_pecah.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        ComboBox_status_stok_pecah.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Belum Jadi LP", "Sudah Jadi LP" }));
        ComboBox_status_stok_pecah.setSelectedIndex(1);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Total / Grade", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 12))); // NOI18N

        tabel_rekap_grade.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Grade", "Kpg", "Gram"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Float.class, java.lang.Float.class
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
        tabel_rekap_grade.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(tabel_rekap_grade);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 268, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel_StokPecahLayout = new javax.swing.GroupLayout(jPanel_StokPecah);
        jPanel_StokPecah.setLayout(jPanel_StokPecahLayout);
        jPanel_StokPecahLayout.setHorizontalGroup(
            jPanel_StokPecahLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_StokPecahLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_StokPecahLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_StokPecahLayout.createSequentialGroup()
                        .addComponent(jLabel21)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_data_stok_pecah))
                    .addGroup(jPanel_StokPecahLayout.createSequentialGroup()
                        .addComponent(jLabel22)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_no_kartu_stok_pecah, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel23)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_grade_stok_pecah, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel24)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_status_stok_pecah, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_search_stok_pecah)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_export_stok_pecah))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 1106, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel_StokPecahLayout.setVerticalGroup(
            jPanel_StokPecahLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_StokPecahLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_StokPecahLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(ComboBox_status_stok_pecah, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_grade_stok_pecah, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_search_stok_pecah, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_no_kartu_stok_pecah, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export_stok_pecah, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_StokPecahLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_total_data_stok_pecah, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 589, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Stok Pecah LP", jPanel_StokPecah);

        jPanel_GradingConveyor.setBackground(new java.awt.Color(255, 255, 255));

        Table_grading_conveyor.setAutoCreateRowSorter(true);
        Table_grading_conveyor.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Table_grading_conveyor.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No Grading", "No Kartu", "Grade", "Kpg", "Created At", "Kpg Manual", "Gram Manual"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Object.class, java.lang.Float.class, java.lang.Float.class
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
        Table_grading_conveyor.getTableHeader().setReorderingAllowed(false);
        jScrollPane6.setViewportView(Table_grading_conveyor);

        button_export_grading_conveyor.setBackground(new java.awt.Color(255, 255, 255));
        button_export_grading_conveyor.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_export_grading_conveyor.setText("Export");
        button_export_grading_conveyor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_grading_conveyorActionPerformed(evt);
            }
        });

        jLabel25.setBackground(new java.awt.Color(255, 255, 255));
        jLabel25.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel25.setText("Total Data :");

        label_total_data_grading_conveyor.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_grading_conveyor.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_data_grading_conveyor.setText("0");

        jLabel26.setBackground(new java.awt.Color(255, 255, 255));
        jLabel26.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel26.setText("No Kartu :");

        jLabel27.setBackground(new java.awt.Color(255, 255, 255));
        jLabel27.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel27.setText("Grade :");

        txt_search_no_kartu_grading_conveyor.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_search_no_kartu_grading_conveyor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_no_kartu_grading_conveyorKeyPressed(evt);
            }
        });

        button_search_grading_conveyor.setBackground(new java.awt.Color(255, 255, 255));
        button_search_grading_conveyor.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_search_grading_conveyor.setText("Search");
        button_search_grading_conveyor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_search_grading_conveyorActionPerformed(evt);
            }
        });

        txt_search_grade_grading_conveyor.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_search_grade_grading_conveyor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_grade_grading_conveyorKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_GradingConveyorLayout = new javax.swing.GroupLayout(jPanel_GradingConveyor);
        jPanel_GradingConveyor.setLayout(jPanel_GradingConveyorLayout);
        jPanel_GradingConveyorLayout.setHorizontalGroup(
            jPanel_GradingConveyorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_GradingConveyorLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_GradingConveyorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane6)
                    .addGroup(jPanel_GradingConveyorLayout.createSequentialGroup()
                        .addGroup(jPanel_GradingConveyorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_GradingConveyorLayout.createSequentialGroup()
                                .addComponent(jLabel25)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_data_grading_conveyor))
                            .addGroup(jPanel_GradingConveyorLayout.createSequentialGroup()
                                .addComponent(jLabel26)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_no_kartu_grading_conveyor, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel27)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_grade_grading_conveyor, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_search_grading_conveyor)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_export_grading_conveyor)))
                        .addGap(0, 906, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel_GradingConveyorLayout.setVerticalGroup(
            jPanel_GradingConveyorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_GradingConveyorLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_GradingConveyorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_search_grade_grading_conveyor, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_search_grading_conveyor, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_no_kartu_grading_conveyor, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export_grading_conveyor, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_GradingConveyorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_total_data_grading_conveyor, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 589, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Data Grading Conveyor", jPanel_GradingConveyor);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 700, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void button_insert_bahan_baku_masukActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_insert_bahan_baku_masukActionPerformed
        // TODO add your handling code here:
        JDialog_Edit_Insert_KartuBaku dialog = new JDialog_Edit_Insert_KartuBaku(new javax.swing.JFrame(), true, "insert", null);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);
        refreshTable();
    }//GEN-LAST:event_button_insert_bahan_baku_masukActionPerformed

    private void button_update_bahan_baku_masukActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_update_bahan_baku_masukActionPerformed
        // TODO add your handling code here:
        int j = Table_Bahan_Baku_Masuk.getSelectedRow();
        if (j == -1) {
            JOptionPane.showMessageDialog(this, "Silahkan pilih data yang akan di edit !");
        } else {
            String no_kartu = Table_Bahan_Baku_Masuk.getValueAt(j, 0).toString();
            if (no_kartu.contains("CMP")) {
                JOptionPane.showMessageDialog(this, "Maaf tidak bisa edit kartu CMP");
            } else {
                JDialog_Edit_Insert_KartuBaku dialog = new JDialog_Edit_Insert_KartuBaku(new javax.swing.JFrame(), true, "update", no_kartu);
                dialog.pack();
                dialog.setLocationRelativeTo(this);
                dialog.setVisible(true);
                dialog.setEnabled(true);
                refreshTable();
            }
        }
    }//GEN-LAST:event_button_update_bahan_baku_masukActionPerformed

    private void button_delete_bahan_baku_masukActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_delete_bahan_baku_masukActionPerformed
        // TODO add your handling code here:
        try {
            int j = Table_Bahan_Baku_Masuk.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih data yang ingin di hapus !");
            } else {
                int dialogResult = JOptionPane.showConfirmDialog(this, "Yakin hapus data ini?", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    // delete code here
                    Utility.db.getConnection().setAutoCommit(false);
                    sql = "DELETE FROM `tb_bahan_baku_masuk` WHERE `no_kartu_waleta` = '" + Table_Bahan_Baku_Masuk.getValueAt(j, 0).toString() + "'";
                    Utility.db.getConnection().createStatement();
                    Utility.db.getStatement().executeUpdate(sql);
                    sql = "DELETE FROM `tb_bahan_baku_masuk_cheat` WHERE `no_kartu_waleta` = '" + Table_Bahan_Baku_Masuk.getValueAt(j, 0).toString() + "'";
                    Utility.db.getConnection().createStatement();
                    Utility.db.getStatement().executeUpdate(sql);

                    Utility.db.getConnection().commit();
                    JOptionPane.showMessageDialog(this, "data kartu baku & kartu baku cheat berhasil di hapus");
                    refreshTable();
                }
            }
        } catch (Exception e) {
            try {
                Utility.db.getConnection().rollback();
            } catch (SQLException ex) {
                Logger.getLogger(JDialog_Edit_Insert_KartuBaku.class.getName()).log(Level.SEVERE, null, ex);
            }
            JOptionPane.showMessageDialog(this, "data not inserted :" + e);
            Logger.getLogger(JDialog_Edit_Insert_KartuBaku.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            try {
                Utility.db.getConnection().setAutoCommit(true);
            } catch (SQLException ex) {
                Logger.getLogger(JDialog_Edit_Insert_KartuBaku.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_button_delete_bahan_baku_masukActionPerformed

    private void txt_search_bahan_masukKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_bahan_masukKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_search_bahan_masukKeyPressed

    private void button_export_BahanBakuMasukActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_BahanBakuMasukActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Table_Bahan_Baku_Masuk.getModel();
        ExportToExcel.writeToExcel(model, this);
    }//GEN-LAST:event_button_export_BahanBakuMasukActionPerformed

    private void button_save_uji_kemasanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_save_uji_kemasanActionPerformed
        // TODO add your handling code here:
        try {
            int j = Table_Bahan_Baku_Masuk.getSelectedRow();
            String rapat, rusak, basah;
            if (CheckBox_uji_kerapatan.isSelected()) {
                rapat = "Passed";
            } else {
                rapat = "Failed";
            }
            if (CheckBox_uji_kerusakan.isSelected()) {
                rusak = "Passed";
            } else {
                rusak = "Failed";
            }
            if (CheckBox_uji_basah.isSelected()) {
                basah = "Passed";
            } else {
                basah = "Failed";
            }
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih salah satu data pada tabel !");
            } else {
                String Query = "UPDATE `tb_bahan_baku_masuk` SET `uji_kerapatan` = '" + rapat + "', `uji_kerusakan` = '" + rusak + "', `uji_basah` = '" + basah + "' WHERE `tb_bahan_baku_masuk`.`no_kartu_waleta` = '" + Table_Bahan_Baku_Masuk.getValueAt(j, 0).toString() + "'";
                executeSQLQuery(Query, "updated !");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(JPanel_BahanBakuMasuk.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_save_uji_kemasanActionPerformed

    private void button_save_timbang_bahan_bakuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_save_timbang_bahan_bakuActionPerformed
        // TODO add your handling code here:
        try {
            int j = Table_Bahan_Baku_Masuk.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih salah satu data pada tabel !");
            } else {
                if (Date_Grading.getDate() != null && Date_timbang.getDate() != null) {
                    String Query = "UPDATE `tb_bahan_baku_masuk` SET `tgl_grading` = '" + dateFormat.format(Date_Grading.getDate()) + "', `tgl_timbang` = '" + dateFormat.format(Date_timbang.getDate()) + "' WHERE `tb_bahan_baku_masuk`.`no_kartu_waleta` = '" + Table_Bahan_Baku_Masuk.getValueAt(j, 0).toString() + "'";
                    executeSQLQuery(Query, "updated !");
                } else if (Date_Grading.getDate() != null && Date_timbang.getDate() == null) {
                    String Query = "UPDATE `tb_bahan_baku_masuk` SET `tgl_grading` = '" + dateFormat.format(Date_Grading.getDate()) + "' WHERE `tb_bahan_baku_masuk`.`no_kartu_waleta` = '" + Table_Bahan_Baku_Masuk.getValueAt(j, 0).toString() + "'";
                    executeSQLQuery(Query, "updated !");
                } else if (Date_Grading.getDate() == null && Date_timbang.getDate() != null) {
                    String Query = "UPDATE `tb_bahan_baku_masuk` SET `tgl_timbang` = '" + dateFormat.format(Date_timbang.getDate()) + "' WHERE `tb_bahan_baku_masuk`.`no_kartu_waleta` = '" + Table_Bahan_Baku_Masuk.getValueAt(j, 0).toString() + "'";
                    executeSQLQuery(Query, "updated !");
                } else {
                    JOptionPane.showMessageDialog(this, "silahkan memasukkan tanggal timbang / grading.");
                }
            }
        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(JPanel_BahanBakuMasuk.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_save_timbang_bahan_bakuActionPerformed

    private void button_detail_gradingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_detail_gradingActionPerformed
        int j = Table_Bahan_Baku_Masuk.getSelectedRow();
        if (j == -1) {
            JOptionPane.showMessageDialog(this, "Please Select Data First !");
        } else {
            String status = Table_Bahan_Baku_Masuk.getValueAt(j, 20).toString();
            DetailGradingBaku FormGrading = new DetailGradingBaku();
            FormGrading.setResizable(false);
            FormGrading.setLocationRelativeTo(this);
            FormGrading.setVisible(true);
            FormGrading.setEnabled(true);
            FormGrading.init();
            if (status.equals("SELESAI")) {
                FormGrading.Button_Tambah_data_grading.setEnabled(false);
                FormGrading.Button_Edit_data_grading.setEnabled(false);
                FormGrading.Button_hapus_data_grading.setEnabled(false);
                FormGrading.button_grading_selesai.setEnabled(false);
            } else if (status.equals("PROSES")) {
                FormGrading.Button_Tambah_data_grading.setEnabled(true);
                FormGrading.Button_Edit_data_grading.setEnabled(true);
                FormGrading.Button_hapus_data_grading.setEnabled(true);
                FormGrading.button_grading_selesai.setEnabled(true);
            }
            if (akses.charAt(0) == '0') {
                FormGrading.Button_Tambah_data_grading.setEnabled(false);
            } else {
                FormGrading.Button_Tambah_data_grading.setEnabled(true);
            }
            if (akses.charAt(1) == '0') {
                FormGrading.Button_Edit_data_grading.setEnabled(false);
            } else {
                FormGrading.Button_Edit_data_grading.setEnabled(true);
            }
            if (akses.charAt(2) == '0') {
                FormGrading.Button_hapus_data_grading.setEnabled(false);
                FormGrading.Button_hapus_grade_supplier.setEnabled(false);
            } else {
                FormGrading.Button_hapus_data_grading.setEnabled(true);
                FormGrading.Button_hapus_grade_supplier.setEnabled(true);
            }
            if (akses.charAt(0) == '1' && akses.charAt(1) == '1' && akses.charAt(2) == '1') {
                FormGrading.button_grading_selesai.setEnabled(true);
                FormGrading.ShowPriceColumn();
            } else {
                FormGrading.button_grading_selesai.setEnabled(false);
                FormGrading.HidePriceColumn();
            }
        }
    }//GEN-LAST:event_button_detail_gradingActionPerformed

    private void button_searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_searchActionPerformed
        // TODO add your handling code here:
        refreshTable();
    }//GEN-LAST:event_button_searchActionPerformed

    private void txt_search_supplierKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_supplierKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_search_supplierKeyPressed

    private void txt_search_rbKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_rbKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_search_rbKeyPressed

    private void CheckBox_showCMPItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_CheckBox_showCMPItemStateChanged
        // TODO add your handling code here:
        refreshTable();
    }//GEN-LAST:event_CheckBox_showCMPItemStateChanged

    private void button_tv_grading_bakuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_tv_grading_bakuActionPerformed
        // TODO add your handling code here:
        JFrame_TV_GradingBaku Frame = new JFrame_TV_GradingBaku();
        Frame.pack();
        Frame.setResizable(true);
        Frame.setLocationRelativeTo(this);
        Frame.setVisible(true);
        Frame.setEnabled(true);
        Frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }//GEN-LAST:event_button_tv_grading_bakuActionPerformed

    private void button_export_gradingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_gradingActionPerformed
        DefaultTableModel model = (DefaultTableModel) Table_Grading_Bahan_Baku.getModel();
        ExportToExcel.writeToExcel(model, jPanel_grading);
    }//GEN-LAST:event_button_export_gradingActionPerformed

    private void txt_search_grading_no_kartuKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_grading_no_kartuKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_grading();
        }
    }//GEN-LAST:event_txt_search_grading_no_kartuKeyPressed

    private void txt_search_grading_gradeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_grading_gradeKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_grading();
        }
    }//GEN-LAST:event_txt_search_grading_gradeKeyPressed

    private void button_search_gradingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_gradingActionPerformed
        // TODO add your handling code here:
        refreshTable_grading();
    }//GEN-LAST:event_button_search_gradingActionPerformed

    private void button_Catatan_Penimbangan_Sarang_Burung_MentahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_Catatan_Penimbangan_Sarang_Burung_MentahActionPerformed
        // TODO add your handling code here:
        try {
            String no_kartu = "";
            for (int i = 0; i < Table_Bahan_Baku_Masuk.getRowCount(); i++) {
                if (i != 0) {
                    no_kartu = no_kartu + ", ";
                }
                no_kartu = no_kartu + "'" + Table_Bahan_Baku_Masuk.getValueAt(i, 0).toString() + "'";
            }
            String Query = "SELECT `no_kartu_waleta`, `tgl_masuk`, `berat_awal`, `tb_bahan_baku_masuk_cheat`.`no_registrasi`, `tb_rumah_burung`.`nama_rumah_burung` \n"
                    + "FROM `tb_bahan_baku_masuk_cheat` \n"
                    + "LEFT JOIN `tb_rumah_burung` ON `tb_bahan_baku_masuk_cheat`.`no_registrasi` = `tb_rumah_burung`.`no_registrasi` "
                    + "WHERE `no_kartu_waleta` IN (" + no_kartu + ")";

            JRDesignQuery newQuery = new JRDesignQuery();
            newQuery.setText(Query);
            //String REPORT = "C:\\Users\\Z475\\Documents\\NetBeansProjects\\JavaApplication5\\src\\javaapplication5\\report1.jrxml";
            JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Catatan_Penimbangan_Sarang_Burung_Mentah_Label1.jrxml");
            JASP_DESIGN.setQuery(newQuery);

            JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
            JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, null, Utility.db.getConnection());
            JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
        } catch (JRException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(JPanel_BahanBakuMasuk.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_Catatan_Penimbangan_Sarang_Burung_MentahActionPerformed

    private void button_selesaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_selesaiActionPerformed
        // TODO add your handling code here:
        JOptionPane.showMessageDialog(this, "Fitur selesai kartu telah dipindahkan ke menu keuangan");
    }//GEN-LAST:event_button_selesaiActionPerformed

    private void button_Catatan_Penimbangan_Sarang_Burung_Mentah_print1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_Catatan_Penimbangan_Sarang_Burung_Mentah_print1ActionPerformed
        // TODO add your handling code here:
        int j = Table_Bahan_Baku_Masuk.getSelectedRow();
        if (j == -1) {
            JOptionPane.showMessageDialog(this, "Please Select Data First !");
        } else {
            try {
                String no_kartu = "'" + Table_Bahan_Baku_Masuk.getValueAt(j, 0).toString() + "'";
                String Query = "SELECT `no_kartu_waleta`, `tgl_masuk`, `berat_awal`, `tb_bahan_baku_masuk_cheat`.`no_registrasi`, `tb_rumah_burung`.`nama_rumah_burung` \n"
                        + "FROM `tb_bahan_baku_masuk_cheat` \n"
                        + "LEFT JOIN `tb_rumah_burung` ON `tb_bahan_baku_masuk_cheat`.`no_registrasi` = `tb_rumah_burung`.`no_registrasi` "
                        + "WHERE `no_kartu_waleta` IN (" + no_kartu + ")";

                JRDesignQuery newQuery = new JRDesignQuery();
                newQuery.setText(Query);
                //String REPORT = "C:\\Users\\Z475\\Documents\\NetBeansProjects\\JavaApplication5\\src\\javaapplication5\\report1.jrxml";
                JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Catatan_Penimbangan_Sarang_Burung_Mentah_Label1.jrxml");
                JASP_DESIGN.setQuery(newQuery);

                JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
                JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, null, Utility.db.getConnection());
                JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
            } catch (JRException e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
                Logger.getLogger(JPanel_BahanBakuMasuk.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }//GEN-LAST:event_button_Catatan_Penimbangan_Sarang_Burung_Mentah_print1ActionPerformed

    private void button_export_pecahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_pecahActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Table_pecah_lp.getModel();
        ExportToExcel.writeToExcel(model, jPanel_grading);
    }//GEN-LAST:event_button_export_pecahActionPerformed

    private void button_edit_pecahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_edit_pecahActionPerformed
        // TODO add your handling code here:
        int j = Table_pecah_lp.getSelectedRow();
        if (j == -1) {
            JOptionPane.showMessageDialog(this, "Silahkan pilih data yang akan di edit !");
        } else {
            if (Table_pecah_lp.getValueAt(j, 23) == null || Table_pecah_lp.getValueAt(j, 23).toString().equals("")) {
                DefaultTableModel model = (DefaultTableModel) Table_pecah_lp.getModel();
                JDialog_Edit_PecahLP dialog = new JDialog_Edit_PecahLP(new javax.swing.JFrame(), true, model, Table_pecah_lp.getSelectedRow());
                dialog.pack();
                dialog.setLocationRelativeTo(this);
                dialog.setVisible(true);
                dialog.setEnabled(true);
                int i = Table_Grading_Bahan_Baku.getSelectedRow();
                if (i > -1) {
                    refreshTable_pecah_lp(Table_Grading_Bahan_Baku.getValueAt(i, 0).toString());
                }
            } else {
                JOptionPane.showMessageDialog(this, "Pecah LP yang sudah terpakai tidak bisa di edit!");
            }
        }
    }//GEN-LAST:event_button_edit_pecahActionPerformed

    private void button_export_stok_pecahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_stok_pecahActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Table_stok_pecah_lp.getModel();
        ExportToExcel.writeToExcel(model, jPanel_grading);
    }//GEN-LAST:event_button_export_stok_pecahActionPerformed

    private void txt_search_no_kartu_stok_pecahKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_no_kartu_stok_pecahKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_stok_pecah_lp();
        }
    }//GEN-LAST:event_txt_search_no_kartu_stok_pecahKeyPressed

    private void button_search_stok_pecahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_stok_pecahActionPerformed
        // TODO add your handling code here:
        refreshTable_stok_pecah_lp();
    }//GEN-LAST:event_button_search_stok_pecahActionPerformed

    private void txt_search_grade_stok_pecahKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_grade_stok_pecahKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_stok_pecah_lp();
        }
    }//GEN-LAST:event_txt_search_grade_stok_pecahKeyPressed

    private void button_gabung_pecahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_gabung_pecahActionPerformed
        // TODO add your handling code here:
        int j = Table_Grading_Bahan_Baku.getSelectedRow();
        if (j == -1) {
            JOptionPane.showMessageDialog(this, "Silahkan pilih data pada tabel kiri !");
        } else {
            String no_grading = Table_Grading_Bahan_Baku.getValueAt(j, 0).toString();
            JDialog_PecahLP_GabungPecah dialog = new JDialog_PecahLP_GabungPecah(new javax.swing.JFrame(), true, no_grading);
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            dialog.setEnabled(true);
            refreshTable_pecah_lp(no_grading);
        }
    }//GEN-LAST:event_button_gabung_pecahActionPerformed

    private void button_export_grading_conveyorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_grading_conveyorActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Table_grading_conveyor.getModel();
        ExportToExcel.writeToExcel(model, jPanel_grading);
    }//GEN-LAST:event_button_export_grading_conveyorActionPerformed

    private void txt_search_no_kartu_grading_conveyorKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_no_kartu_grading_conveyorKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_grading_conveyor();
        }
    }//GEN-LAST:event_txt_search_no_kartu_grading_conveyorKeyPressed

    private void button_search_grading_conveyorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_grading_conveyorActionPerformed
        // TODO add your handling code here:
        refreshTable_grading_conveyor();
    }//GEN-LAST:event_button_search_grading_conveyorActionPerformed

    private void txt_search_grade_grading_conveyorKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_grade_grading_conveyorKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_grading_conveyor();
        }
    }//GEN-LAST:event_txt_search_grade_grading_conveyorKeyPressed

    private void button_input_pecahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_input_pecahActionPerformed
        // TODO add your handling code here:
        int j = Table_Grading_Bahan_Baku.getSelectedRow();
        if (j == -1) {
            JOptionPane.showMessageDialog(this, "Silahkan pilih data pada tabel grading sebelah kiri !");
        } else {
            String no_grading = Table_Grading_Bahan_Baku.getValueAt(j, 0).toString();
            String no_kartu = Table_Grading_Bahan_Baku.getValueAt(j, 1).toString();
            String grade = Table_Grading_Bahan_Baku.getValueAt(j, 2).toString();
            int keping = (int) Table_Grading_Bahan_Baku.getValueAt(j, 3);
            int gram = (int) Table_Grading_Bahan_Baku.getValueAt(j, 4);
            JDialog_PecahLP_InputPecah dialog = new JDialog_PecahLP_InputPecah(new javax.swing.JFrame(), true, no_grading, no_kartu, grade, keping, gram);
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            dialog.setEnabled(true);
            refreshTable_grading();
            refreshTable_pecah_lp(no_grading);
        }
    }//GEN-LAST:event_button_input_pecahActionPerformed

    private void button_hapus_pecahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_hapus_pecahActionPerformed
        // TODO add your handling code here:
        int j = Table_Grading_Bahan_Baku.getSelectedRow();
        if (j == -1) {
            JOptionPane.showMessageDialog(this, "Silahkan pilih data pada tabel kiri !");
        } else {
            String no_grading = Table_Grading_Bahan_Baku.getValueAt(j, 0).toString();
            String no_kartu = Table_Grading_Bahan_Baku.getValueAt(j, 1).toString();
            String grade = Table_Grading_Bahan_Baku.getValueAt(j, 2).toString();

            int dialogResult = JOptionPane.showConfirmDialog(this, "Yakin hapus data pecah " + no_kartu + "-" + grade + "?", "Warning", 0);
            if (dialogResult == JOptionPane.YES_OPTION) {
                PreparedStatement checkStatement = null;
                PreparedStatement deleteStatement = null;
                ResultSet resultSet = null;
                try {
                    Utility.db.getConnection().setAutoCommit(false);
                    //check for existing records
                    String checkSql = "SELECT `no_laporan_produksi`, `tb_bahan_baku_pecah_kartu`.`kode_pecah_kartu` "
                            + "FROM `tb_laporan_produksi` "
                            + "LEFT JOIN `tb_bahan_baku_pecah_kartu` "
                            + "ON `tb_bahan_baku_pecah_kartu`.`kode_pecah_kartu` = `tb_laporan_produksi`.`kode_pecah_lp` "
                            + "WHERE `tb_bahan_baku_pecah_kartu`.`no_grading` = ?";
                    checkStatement = Utility.db.getConnection().prepareStatement(checkSql);
                    checkStatement.setString(1, no_grading);
                    resultSet = checkStatement.executeQuery();
                    // If the query returns any rows, do not proceed with the deletion
                    if (resultSet.next()) {
                        JOptionPane.showMessageDialog(this, "Cannot delete record: kode pecah sudah dipakai menjadi LP.");
                    } else {
                        String Query = "DELETE FROM `tb_bahan_baku_pecah_kartu` WHERE `no_grading` = ?";
                        deleteStatement = Utility.db.getConnection().prepareStatement(Query);
                        deleteStatement.setString(1, no_grading);

                        int rowsAffected = deleteStatement.executeUpdate();
                        Utility.db.getConnection().commit();

                        DefaultTableModel model = (DefaultTableModel) Table_pecah_lp.getModel();
                        model.setRowCount(0);
                        label_total_keping_riil.setText("0");
                        label_total_keping_upah.setText("0");
                        label_total_gram_sistem.setText("0");
                        label_total_gram_riil.setText("0");
                        refreshTable_grading();

                        JOptionPane.showMessageDialog(this, rowsAffected + " record(s) deleted successfully.");
                    }
                } catch (Exception e) {
                    try {
                        Utility.db.getConnection().rollback();
                    } catch (SQLException ex) {
                        Logger.getLogger(JDialog_Edit_Insert_KartuBaku.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    JOptionPane.showMessageDialog(this, "An error occurred while deleting the record :" + e);
                    Logger.getLogger(JDialog_Edit_Insert_KartuBaku.class.getName()).log(Level.SEVERE, null, e);
                } finally {
                    // Clean up resources
                    if (resultSet != null) {
                        try {
                            resultSet.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    if (checkStatement != null) {
                        try {
                            checkStatement.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    if (deleteStatement != null) {
                        try {
                            deleteStatement.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        Utility.db.getConnection().setAutoCommit(true);
                    } catch (SQLException ex) {
                        Logger.getLogger(JDialog_Edit_Insert_KartuBaku.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }//GEN-LAST:event_button_hapus_pecahActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox CheckBox_showCMP;
    private javax.swing.JCheckBox CheckBox_uji_basah;
    private javax.swing.JCheckBox CheckBox_uji_kerapatan;
    private javax.swing.JCheckBox CheckBox_uji_kerusakan;
    private javax.swing.JComboBox<String> ComboBox_filterTanggal;
    private javax.swing.JComboBox<String> ComboBox_status_stok_pecah;
    private com.toedter.calendar.JDateChooser Date_Grading;
    private com.toedter.calendar.JDateChooser Date_filter1;
    private com.toedter.calendar.JDateChooser Date_filter2;
    private com.toedter.calendar.JDateChooser Date_timbang;
    public static javax.swing.JTable Table_Bahan_Baku_Masuk;
    private javax.swing.JTable Table_Grading_Bahan_Baku;
    private javax.swing.JTable Table_grading_conveyor;
    private javax.swing.JTable Table_pecah_lp;
    private javax.swing.JTable Table_stok_pecah_lp;
    private javax.swing.JButton button_Catatan_Penimbangan_Sarang_Burung_Mentah;
    private javax.swing.JButton button_Catatan_Penimbangan_Sarang_Burung_Mentah_print1;
    public javax.swing.JButton button_delete_bahan_baku_masuk;
    private javax.swing.JButton button_detail_grading;
    public javax.swing.JButton button_edit_pecah;
    private javax.swing.JButton button_export_BahanBakuMasuk;
    public javax.swing.JButton button_export_grading;
    public javax.swing.JButton button_export_grading_conveyor;
    public javax.swing.JButton button_export_pecah;
    public javax.swing.JButton button_export_stok_pecah;
    public javax.swing.JButton button_gabung_pecah;
    public javax.swing.JButton button_hapus_pecah;
    public javax.swing.JButton button_input_pecah;
    public javax.swing.JButton button_insert_bahan_baku_masuk;
    public javax.swing.JButton button_save_timbang_bahan_baku;
    public javax.swing.JButton button_save_uji_kemasan;
    public static javax.swing.JButton button_search;
    public static javax.swing.JButton button_search_grading;
    public static javax.swing.JButton button_search_grading_conveyor;
    public static javax.swing.JButton button_search_stok_pecah;
    private javax.swing.JButton button_selesai;
    private javax.swing.JButton button_tv_grading_baku;
    public javax.swing.JButton button_update_bahan_baku_masuk;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel_Bahan_Baku_Masuk;
    private javax.swing.JPanel jPanel_GradingConveyor;
    private javax.swing.JPanel jPanel_StokPecah;
    private javax.swing.JPanel jPanel_grading;
    private javax.swing.JPanel jPanel_operation_grading;
    private javax.swing.JPanel jPanel_operation_uji_bahan_baku;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel label_tgl_grading;
    private javax.swing.JLabel label_tgl_grading1;
    private javax.swing.JLabel label_total_data_bahan_baku_masuk;
    private javax.swing.JLabel label_total_data_grading;
    private javax.swing.JLabel label_total_data_grading_conveyor;
    private javax.swing.JLabel label_total_data_stok_pecah;
    private javax.swing.JLabel label_total_gram_grading;
    private javax.swing.JLabel label_total_gram_riil;
    private javax.swing.JLabel label_total_gram_sistem;
    private javax.swing.JLabel label_total_keping_grading;
    private javax.swing.JLabel label_total_keping_riil;
    private javax.swing.JLabel label_total_keping_upah;
    private javax.swing.JTable tabel_rekap_grade;
    private javax.swing.JTextField txt_search_bahan_masuk;
    private javax.swing.JTextField txt_search_grade_grading_conveyor;
    private javax.swing.JTextField txt_search_grade_stok_pecah;
    private javax.swing.JTextField txt_search_grading_grade;
    private javax.swing.JTextField txt_search_grading_no_kartu;
    private javax.swing.JTextField txt_search_no_kartu_grading_conveyor;
    private javax.swing.JTextField txt_search_no_kartu_stok_pecah;
    private javax.swing.JTextField txt_search_rb;
    private javax.swing.JTextField txt_search_supplier;
    // End of variables declaration//GEN-END:variables
}
