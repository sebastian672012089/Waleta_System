package waleta_system.Panel_produksi;

import waleta_system.Class.Utility;
import java.awt.Color;
import java.awt.HeadlessException;
import java.awt.event.KeyEvent;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
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
import waleta_system.Class.DataF2;
import waleta_system.Class.ExportToExcel;

public class JPanel_Finishing2 extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    PreparedStatement pst;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    float target_mk_br = 0, target_mk_bs = 0, target_mk_bb = 0;
    float target_ovlsgtg_br = 0, target_ovlsgtg_bs = 0, target_ovlsgtg_bb = 0;
    float target_othr_br = 0, target_othr_bs = 0, target_othr_bb = 0;

    float target_mk_br_12 = 0, target_mk_bs_12 = 0, target_mk_bb_12 = 0;
    float target_ovlsgtg_br_12 = 0, target_ovlsgtg_bs_12 = 0, target_ovlsgtg_bb_12 = 0;
    float target_othr_br_12 = 0, target_othr_bs_12 = 0, target_othr_bb_12 = 0;
//    DefaultCategoryDataset dataset;

    public JPanel_Finishing2() {
        initComponents();
    }

    public void init() {

//        decimalFormat = Utility.DecimalFormatUS(decimalFormat);
        refreshTable_F2();
//        refreshTable_Setoran();
        try {
            ComboBox_ruangan.removeAllItems();
            ComboBox_ruangan.addItem("All");
            ComboBox_evaluasi_ruanganMLEM.removeAllItems();
            ComboBox_evaluasi_ruanganMLEM.addItem("All");
            sql = "SELECT DISTINCT(`ruangan`) AS 'ruangan' FROM `tb_laporan_produksi` WHERE 1";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_ruangan.addItem(rs.getString("ruangan"));
                ComboBox_evaluasi_ruanganMLEM.addItem(rs.getString("ruangan"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_Finishing2.class.getName()).log(Level.SEVERE, null, ex);
        }
        Table_Setoran_harian_f2.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && Table_Setoran_harian_f2.getSelectedRow() != -1) {
                    if (Table_Setoran_harian_f2.getSelectedRow() != -1) {
                        refreshTable_Pencabut();
                    }
                }
            }
        });

        Table_balen.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && Table_balen.getSelectedRow() != -1) {
                    if (Table_balen.getSelectedRow() != -1) {
                        int a = Table_balen.getSelectedRow();
                        if (Table_balen.getValueAt(a, 7) == null) {
                            button_selesai_balen.setEnabled(true);
                        } else {
                            button_selesai_balen.setEnabled(false);
                        }
                    }
                }
            }
        });

        Table_Data_f2.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && Table_Data_f2.getSelectedRow() != -1) {
                    int i = Table_Data_f2.getSelectedRow();
                    if (i != -1) {
                        if (JPanel_Finishing2.Table_Data_f2.getValueAt(i, 6) == null) {
                            button_input_koreksi.setEnabled(false);
                            button_input_koreksi1.setEnabled(false);
                            button_input_f1.setEnabled(false);
                            button_input_f2.setEnabled(false);
                            button_f2_setor_lp.setEnabled(false);
                            button_f2_edit.setEnabled(false);
                        } else {
                            if (JPanel_Finishing2.Table_Data_f2.getValueAt(i, 8) == null) {
                                button_input_koreksi.setEnabled(true);
                                button_input_koreksi1.setEnabled(true);
                                button_input_f1.setEnabled(false);
                                button_input_f2.setEnabled(false);
                                button_f2_edit.setEnabled(false);
                                button_f2_setor_lp.setEnabled(false);
                            } else {
                                button_input_koreksi.setEnabled(false);
                                button_input_koreksi1.setEnabled(false);
                                if (JPanel_Finishing2.Table_Data_f2.getValueAt(i, 10) == null) {
                                    button_f2_setor_lp.setEnabled(true);
                                    button_input_f1.setEnabled(true);
                                    button_input_f2.setEnabled(false);
                                } else {
                                    button_input_f1.setEnabled(false);
                                    if (JPanel_Finishing2.Table_Data_f2.getValueAt(i, 12) == null) {
                                        button_input_f2.setEnabled(true);
                                        button_f2_setor_lp.setEnabled(false);
                                    } else {
                                        button_input_f2.setEnabled(false);
                                        button_f2_setor_lp.setEnabled(true);
                                    }
                                }

                                if (JPanel_Finishing2.Table_Data_f2.getValueAt(i, 14) == null) {
                                    button_f2_edit.setEnabled(false);
                                    button_f2_setor_lp.setEnabled(true);
                                } else {
                                    button_f2_setor_lp.setEnabled(false);
                                    button_f2_edit.setEnabled(true);
                                    button_input_f1.setEnabled(false);
                                    button_input_f2.setEnabled(false);
                                    button_input_koreksi.setEnabled(false);
                                    button_input_koreksi1.setEnabled(false);
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    public void check_TargetRendemen(String bentuk, String bulu, float bk, float bk_12) {
        if (null != bulu) {
            switch (bulu) {
                case "Bulu Ringan":
                    bulu = "BR";
                    break;
                case "Bulu Ringan Sekali/Bulu Ringan":
                    bulu = "BR";
                    break;
                case "Bulu Sedang":
                    bulu = "BS";
                    break;
                case "Bulu Berat":
                    bulu = "BB";
                    break;
                case "Bulu Berat Sekali":
                    bulu = "BB";
                    break;
                default:
                    bulu = "-";
                    break;
            }
        }
        if (bentuk.contains("Mangkok")) {
            switch (bulu) {
                case "BR":
                    target_mk_br = target_mk_br + bk;
                    target_mk_br_12 = target_mk_br_12 + bk_12;
                    break;
                case "BS":
                    target_mk_bs = target_mk_bs + bk;
                    target_mk_bs_12 = target_mk_bs_12 + bk_12;
                    break;
                case "BB":
                    target_mk_bb = target_mk_bb + bk;
                    target_mk_bb_12 = target_mk_bb_12 + bk_12;
                    break;
            }
        } else if (bentuk.contains("Oval") || bentuk.contains("Segitiga")) {
            switch (bulu) {
                case "BR":
                    target_ovlsgtg_br = target_ovlsgtg_br + bk;
                    target_ovlsgtg_br_12 = target_ovlsgtg_br_12 + bk_12;
                    break;
                case "BS":
                    target_ovlsgtg_bs = target_ovlsgtg_bs + bk;
                    target_ovlsgtg_bs_12 = target_ovlsgtg_bs_12 + bk_12;
                    break;
                case "BB":
                    target_ovlsgtg_bb = target_ovlsgtg_bb + bk;
                    target_ovlsgtg_bb_12 = target_ovlsgtg_bb_12 + bk_12;
            }
        } else {
            switch (bulu) {
                case "BR":
                    target_othr_br = target_othr_br + bk;
                    target_othr_br_12 = target_othr_br_12 + bk_12;
                    break;
                case "BS":
                    target_othr_bs = target_othr_bs + bk;
                    target_othr_bs_12 = target_othr_bs_12 + bk_12;
                    break;
                case "BB":
                    target_othr_bb = target_othr_bb + bk;
                    target_othr_bb_12 = target_othr_bb_12 + bk_12;
            }
        }
    }

    public void refreshTable_F2() {
        try {
            decimalFormat.setMaximumFractionDigits(2);
            DefaultTableModel model = (DefaultTableModel) Table_Data_f2.getModel();
            model.setRowCount(0);

            String search_tgl = "";
            if (Date1_f2.getDate() != null && Date2_f2.getDate() != null) {
                if (null != ComboBox_SearchDate.getSelectedItem().toString()) {
                    switch (ComboBox_SearchDate.getSelectedItem().toString()) {
                        case "Tgl Input By Product":
                            search_tgl = "AND `tb_finishing_2`.`tgl_input_byProduct` BETWEEN '" + dateFormat.format(Date1_f2.getDate()) + "' and '" + dateFormat.format(Date2_f2.getDate()) + "'";
                            break;
                        case "Tgl Koreksi Kering":
                            search_tgl = "AND `tb_finishing_2`.`tgl_dikerjakan_f2` BETWEEN '" + dateFormat.format(Date1_f2.getDate()) + "' and '" + dateFormat.format(Date2_f2.getDate()) + "'";
                            break;
                        case "Tgl F1":
                            search_tgl = "AND `tb_finishing_2`.`tgl_f1` BETWEEN '" + dateFormat.format(Date1_f2.getDate()) + "' and '" + dateFormat.format(Date2_f2.getDate()) + "'";
                            break;
                        case "Tgl F2":
                            search_tgl = "AND `tb_finishing_2`.`tgl_f2` BETWEEN '" + dateFormat.format(Date1_f2.getDate()) + "' and '" + dateFormat.format(Date2_f2.getDate()) + "'";
                            break;
                        case "Tgl Setor":
                            search_tgl = "AND `tb_finishing_2`.`tgl_setor_f2` BETWEEN '" + dateFormat.format(Date1_f2.getDate()) + "' and '" + dateFormat.format(Date2_f2.getDate()) + "'";
                            break;
                        case "Tgl input ssk":
                            search_tgl = "AND `tb_finishing_2`.`tgl_input_sesekan` BETWEEN '" + dateFormat.format(Date1_f2.getDate()) + "' and '" + dateFormat.format(Date2_f2.getDate()) + "'";
                            break;
                        default:
                            break;
                    }
                }
            }

            String search_ruangan = "AND `ruangan` LIKE '" + txt_search_ruangan.getText() + "'";
            if (txt_search_ruangan.getText() == null || txt_search_ruangan.getText().equals("") || txt_search_ruangan.getText().equals("%%")) {
                search_ruangan = "";
            }

            sql = "SELECT `tb_finishing_2`.*, `jumlah_keping`, `berat_basah`, `kode_grade`, `tb_laporan_produksi`.`no_kartu_waleta`, `ruangan`, `no_registrasi`, `cheat_no_kartu`, `cheat_rsb`, `edited` \n"
                    + "FROM `tb_finishing_2` "
                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_finishing_2`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_bahan_baku_masuk_cheat` ON `tb_laporan_produksi`.`no_kartu_waleta` = `tb_bahan_baku_masuk_cheat`.`no_kartu_waleta`\n"
                    //                        + "LEFT JOIN `tb_cetak` ON `tb_cetak`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    + "WHERE "
                    + "`tb_finishing_2`.`no_laporan_produksi` LIKE '%" + txt_search_no_lp.getText() + "%' "
                    + "AND `tb_laporan_produksi`.`memo_lp` LIKE '%" + txt_search_memo_f2.getText() + "%'"
                    + search_ruangan
                    + search_tgl
                    + " ORDER BY `tb_finishing_2`.`tgl_masuk_f2` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[50];
            while (rs.next()) {
                row[0] = rs.getString("no_laporan_produksi");
                row[1] = rs.getInt("jumlah_keping");
                row[2] = rs.getInt("berat_basah");
                row[3] = rs.getString("kode_grade");
                row[4] = rs.getString("ruangan");
                row[5] = rs.getDate("tgl_input_byProduct");
                row[6] = rs.getDate("tgl_masuk_f2");
                row[7] = rs.getString("f2_diterima");
                row[8] = rs.getDate("tgl_dikerjakan_f2");
                row[9] = rs.getString("pekerja_koreksi_kering");
                row[10] = rs.getDate("tgl_f1");
                row[11] = rs.getString("pekerja_f1");
                row[12] = rs.getDate("tgl_f2");
                row[13] = rs.getString("pekerja_f2");
                row[14] = rs.getDate("tgl_setor_f2");
                row[15] = rs.getString("f2_disetor");
                row[16] = rs.getString("f2_timbang");
                row[17] = rs.getInt("fbonus_f2");
                row[18] = rs.getInt("berat_fbonus");
                row[19] = rs.getInt("fnol_f2");
                row[20] = rs.getInt("berat_fnol");
                row[21] = rs.getInt("pecah_f2");
                row[22] = rs.getInt("berat_pecah");
                row[23] = rs.getInt("flat_f2");
                row[24] = rs.getInt("berat_flat");
                row[25] = rs.getInt("jidun_utuh_f2");
                row[26] = rs.getInt("jidun_pecah_f2");
                row[27] = rs.getInt("berat_jidun");

                row[28] = rs.getInt("sesekan");
                row[29] = rs.getInt("hancuran");
                row[30] = rs.getInt("rontokan");
                row[31] = rs.getInt("bonggol");
                row[32] = rs.getInt("serabut");

                row[33] = rs.getFloat("tambahan_kaki1");
                row[34] = rs.getString("lp_kaki1");
                row[35] = rs.getFloat("tambahan_kaki2");
                row[36] = rs.getString("lp_kaki2");

                row[37] = rs.getString("admin_f2");
                row[38] = rs.getString("otorisasi");
                row[39] = rs.getString("keterangan");
                row[40] = rs.getInt("tanpa_kaki_f1");
                row[41] = rs.getInt("kaki_kecil_f1");
                row[42] = rs.getInt("kaki_besar_f1");
                row[43] = rs.getInt("flat_f1");
                row[44] = rs.getDate("tgl_input_sesekan");
                row[45] = rs.getString("edited");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_Data_f2);

            int rowData = Table_Data_f2.getRowCount();
            label_total_data_f2.setText(Integer.toString(rowData));
        } catch (Exception ex) {
            Logger.getLogger(JPanel_Finishing2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ArrayList<DataF2> SetoranHarianList() {
        ArrayList<DataF2> F2List = new ArrayList<>();
        try {

            String SearchByBentuk = "";
            if (null != ComboBox_searchBentuk.getSelectedItem().toString()) {
                switch (ComboBox_searchBentuk.getSelectedItem().toString()) {
                    case "All":
                        SearchByBentuk = "";
                        break;
                    case "Mangkok":
                        SearchByBentuk = "AND `tb_grade_bahan_baku`.`jenis_bentuk` = 'Mangkok'";
                        break;
                    case "Oval":
                        SearchByBentuk = "AND `tb_grade_bahan_baku`.`jenis_bentuk` = 'Oval'";
                        break;
                    case "Segitiga":
                        SearchByBentuk = "AND `tb_grade_bahan_baku`.`jenis_bentuk` = 'Segitiga'";
                        break;
                    case "Flat/Pecah":
                        SearchByBentuk = "AND (`tb_grade_bahan_baku`.`jenis_bentuk` = 'Pecah'"
                                + "OR `tb_grade_bahan_baku`.`jenis_bentuk` = 'Lubang')";
                        break;
                    case "-":
                        SearchByBentuk = "AND `tb_grade_bahan_baku`.`jenis_bentuk` NOT LIKE 'Mangkok'"
                                + "AND `tb_grade_bahan_baku`.`jenis_bentuk` NOT LIKE 'Oval'"
                                + "AND `tb_grade_bahan_baku`.`jenis_bentuk` NOT LIKE 'Segitiga'"
                                + "AND `tb_grade_bahan_baku`.`jenis_bentuk` NOT LIKE 'Pecah'"
                                + "AND `tb_grade_bahan_baku`.`jenis_bentuk` NOT LIKE 'Lubang'";
                        break;
                    default:
                        break;
                }
            }
            String ruang = "";
            if (null != ComboBox_ruangan.getSelectedItem()) {
                switch (ComboBox_ruangan.getSelectedItem().toString()) {
                    case "All": {
                        ruang = "";
                        break;
                    }
                    default:
                        ruang = ComboBox_ruangan.getSelectedItem().toString();
                        break;
                }
            }
            if (Date_Setoran1.getDate() == null || Date_Setoran2.getDate() == null) {
                sql = "SELECT * \n"
                        + "FROM `tb_finishing_2` "
                        + "LEFT JOIN `tb_laporan_produksi` ON `tb_finishing_2`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                        + "LEFT JOIN `tb_bahan_baku_masuk_cheat` ON `tb_laporan_produksi`.`no_kartu_waleta` = `tb_bahan_baku_masuk_cheat`.`no_kartu_waleta`\n"
                        + "LEFT JOIN `tb_grade_bahan_baku` ON `tb_grade_bahan_baku`.`kode_grade` = `tb_laporan_produksi`.`kode_grade`\n"
                        + "LEFT JOIN `tb_cetak` ON `tb_cetak`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                        + "LEFT JOIN `tb_karyawan` ON `tb_karyawan`.`id_pegawai` = `tb_cetak`.`cetak_dikerjakan`\n"
                        + "LEFT JOIN `tb_cabut` ON `tb_cabut`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                        + "WHERE `tb_finishing_2`.`tgl_setor_f2` IS NOT NULL AND `tb_laporan_produksi`.`memo_lp` LIKE '%" + txt_search_Memo_Setoran.getText() + "%' AND `tb_laporan_produksi`.`no_kartu_waleta` LIKE '%" + txt_search_kartu_setoran.getText() + "%' AND `tb_finishing_2`.`no_laporan_produksi` LIKE '%" + txt_search_LP_setoran.getText() + "%' AND `tb_laporan_produksi`.`ruangan` LIKE '%" + ruang + "%' " + SearchByBentuk + "\n"
                        + "ORDER BY `tb_finishing_2`.`tgl_setor_f2` DESC";
            } else {
                sql = "SELECT * \n"
                        + "FROM `tb_finishing_2` "
                        + "LEFT JOIN `tb_laporan_produksi` ON `tb_finishing_2`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                        + "LEFT JOIN `tb_bahan_baku_masuk_cheat` ON `tb_laporan_produksi`.`no_kartu_waleta` = `tb_bahan_baku_masuk_cheat`.`no_kartu_waleta`\n"
                        + "LEFT JOIN `tb_grade_bahan_baku` ON `tb_grade_bahan_baku`.`kode_grade` = `tb_laporan_produksi`.`kode_grade`\n"
                        + "LEFT JOIN `tb_cetak` ON `tb_cetak`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                        + "LEFT JOIN `tb_karyawan` ON `tb_karyawan`.`id_pegawai` = `tb_cetak`.`cetak_dikerjakan`\n"
                        + "LEFT JOIN `tb_cabut` ON `tb_cabut`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                        + "WHERE `tb_finishing_2`.`tgl_setor_f2` IS NOT NULL AND `tb_laporan_produksi`.`memo_lp` LIKE '%" + txt_search_Memo_Setoran.getText() + "%' AND `tb_laporan_produksi`.`no_kartu_waleta` LIKE '%" + txt_search_kartu_setoran.getText() + "%' AND `tb_finishing_2`.`no_laporan_produksi` LIKE '%" + txt_search_LP_setoran.getText() + "%' AND `tb_laporan_produksi`.`ruangan` LIKE '%" + ruang + "%' AND `tb_finishing_2`.`tgl_setor_f2` BETWEEN '" + dateFormat.format(Date_Setoran1.getDate()) + "' AND '" + dateFormat.format(Date_Setoran2.getDate()) + "' " + SearchByBentuk + "\n"
                        + "ORDER BY `tb_finishing_2`.`tgl_setor_f2` DESC";
            }
//            System.out.println(sql);
            rs = Utility.db.getStatement().executeQuery(sql);
            DataF2 f2;
            while (rs.next()) {
                f2 = new DataF2(rs.getString("no_kartu_waleta"),
                        rs.getString("no_laporan_produksi"),
                        rs.getInt("jumlah_keping"),
                        rs.getInt("berat_kering"),
                        rs.getString("memo_lp"),
                        rs.getString("kode_grade"),
                        rs.getString("jenis_bentuk"),
                        rs.getString("jenis_bulu"),
                        rs.getString("ruangan"),
                        rs.getString("nama_pegawai"),
                        rs.getString("ketua_regu"),
                        rs.getDate("tgl_input_byProduct"),
                        rs.getDate("tgl_dikerjakan_f2"),
                        rs.getString("pekerja_koreksi_kering"),
                        rs.getDate("tgl_f1"),
                        rs.getString("pekerja_f1"),
                        rs.getDate("tgl_f2"),
                        rs.getString("pekerja_f2"),
                        rs.getDate("tgl_masuk_f2"),
                        rs.getString("f2_diterima"),
                        rs.getDate("tgl_setor_f2"),
                        rs.getString("f2_disetor"),
                        rs.getString("f2_timbang"),
                        rs.getInt("fbonus_f2"),
                        rs.getInt("berat_fbonus"),
                        rs.getInt("fnol_f2"),
                        rs.getInt("berat_fnol"),
                        rs.getInt("pecah_f2"),
                        rs.getInt("berat_pecah"),
                        rs.getInt("flat_f2"),
                        rs.getInt("berat_flat"),
                        rs.getInt("jidun_utuh_f2"),
                        rs.getInt("jidun_pecah_f2"),
                        rs.getInt("berat_jidun"),
                        rs.getInt("sesekan"),
                        rs.getInt("hancuran"),
                        rs.getInt("rontokan"),
                        rs.getInt("bonggol"),
                        rs.getInt("serabut"),
                        rs.getFloat("tambahan_kaki1"),
                        rs.getString("lp_kaki1"),
                        rs.getFloat("tambahan_kaki2"),
                        rs.getString("lp_kaki2"),
                        rs.getString("admin_f2"),
                        rs.getString("otorisasi"),
                        rs.getString("keterangan"));
                F2List.add(f2);
            }
        } catch (Exception ex) {
            Logger.getLogger(JPanel_Finishing2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return F2List;
    }

    public void refreshTable_Setoran() {
        decimalFormat.setMaximumFractionDigits(2);
        decimalFormat.setGroupingUsed(true);
        ArrayList<DataF2> list = SetoranHarianList();
        DefaultTableModel model = (DefaultTableModel) Table_Setoran_harian_f2.getModel();
        model.setRowCount(0);

        target_mk_br = 0;
        target_mk_bs = 0;
        target_mk_bb = 0;
        target_ovlsgtg_br = 0;
        target_ovlsgtg_bs = 0;
        target_ovlsgtg_bb = 0;
        target_othr_br = 0;
        target_othr_bs = 0;
        target_othr_bb = 0;

        target_mk_br_12 = 0;
        target_mk_bs_12 = 0;
        target_mk_bb_12 = 0;
        target_ovlsgtg_br_12 = 0;
        target_ovlsgtg_bs_12 = 0;
        target_ovlsgtg_bb_12 = 0;
        target_othr_br_12 = 0;
        target_othr_bs_12 = 0;
        target_othr_bb_12 = 0;

        int TotalData;
        float bk, bk_12, utuh, fbonus, fnol, flat, jidun, sh, sh_12, sp, kaki, netto_utuh, netto_jidun;
        float rend_utuh, rend_flat, rend_sh, rend_sp, rend_jidun, rend_total;
        float rend_utuh_12, rend_flat_12, rend_sh_12, rend_sp_12, rend_jidun_12, rend_total_12;
        float tot_bk = 0, tot_bk12 = 0, tot_kpg_utuh = 0, tot_brt_utuh = 0, tot_kpg_pch = 0, tot_brt_pch = 0, tot_kpg_flat = 0, tot_brt_flat = 0,
                tot_kpg_jidun = 0, tot_kpg_jidun_pecah = 0, tot_brt_jidun = 0, tot_kaki = 0, tot_sp = 0, tot_sh = 0, tot_sh_12 = 0, tot_netto = 0;
        float tot_sesekan = 0, tot_hancuran = 0, tot_rontokan = 0, tot_bonggol = 0, tot_serabut = 0;
        float tot_rend_sh = 0, tot_rend_sp = 0, tot_rend_utuh = 0, tot_rend_flat = 0;
        Color green = new Color(0, 145, 0);

        Object[] row = new Object[38];
        for (int i = 0; i < list.size(); i++) {
            bk = list.get(i).getBerat_Kering();
            bk_12 = bk * 1.12f;
            fbonus = list.get(i).getFbonus_berat();
            fnol = list.get(i).getFnol_berat();
            jidun = list.get(i).getJidun_berat();
            utuh = fbonus + fnol;
            kaki = list.get(i).getTambah_kaki1() + list.get(i).getTambah_kaki2();
            flat = list.get(i).getFlat_berat() + list.get(i).getPecah_berat();
            netto_utuh = utuh - kaki;
            if (jidun > utuh) {
                netto_jidun = jidun - kaki;
                netto_utuh = utuh;
            } else {
                netto_utuh = utuh - kaki;
                netto_jidun = jidun;
            }

            sp = list.get(i).getSesekan() + list.get(i).getHancuran() + list.get(i).getRontokan() + list.get(i).getBonggol() + list.get(i).getSerabut();
            sh = bk - (netto_utuh + netto_jidun + flat + sp);
            sh_12 = bk_12 - (netto_utuh + flat + sp + netto_jidun);

            rend_utuh = (netto_utuh / bk) * 100;
            rend_flat = (flat / bk) * 100;
            rend_sh = (sh / bk) * 100;
            rend_sp = (sp / bk) * 100;
            rend_jidun = (netto_jidun / bk) * 100;
            rend_total = rend_utuh + rend_flat + rend_sh + rend_sp + rend_jidun;

            rend_utuh_12 = (netto_utuh / bk_12) * 100;
            rend_flat_12 = (flat / bk_12) * 100;
            rend_sh_12 = (sh_12 / bk_12) * 100;
            rend_sp_12 = (sp / bk_12) * 100;
            rend_jidun_12 = (netto_jidun / bk_12) * 100;
            rend_total_12 = rend_utuh_12 + rend_flat_12 + rend_sh_12 + rend_sp_12 + rend_jidun_12;

            row[0] = list.get(i).getNo_kartu();
            row[1] = list.get(i).getNo_lp();
            row[2] = list.get(i).getGrade_bk();
            row[3] = list.get(i).getMemo_lp();
            row[4] = list.get(i).getBerat_Kering();
            row[5] = Math.round(bk_12 * 100.f) / 100.f;
            row[6] = list.get(i).getRuangan();
            row[7] = list.get(i).getCetak();
            row[8] = list.get(i).getCabut();

            row[9] = list.get(i).getFbonus_keping() + list.get(i).getFnol_keping();
            row[10] = list.get(i).getFbonus_berat() + list.get(i).getFnol_berat();
            row[11] = netto_utuh;
            row[12] = list.get(i).getPecah_keping();
            row[13] = list.get(i).getPecah_berat();
            row[14] = list.get(i).getFlat_keping();
            row[15] = list.get(i).getFlat_berat();
            row[16] = list.get(i).getJidun_keping();
            row[17] = list.get(i).getJidun_pecah();
            row[18] = list.get(i).getJidun_berat();
            row[19] = netto_jidun;

            row[20] = list.get(i).getSesekan();
            row[21] = list.get(i).getHancuran();
            row[22] = list.get(i).getRontokan();
            row[23] = list.get(i).getBonggol();
            row[24] = list.get(i).getSerabut();

            row[25] = kaki;
            row[26] = Math.round(rend_sh * 100.f) / 100.f;
            row[27] = Math.round(rend_sp * 100.f) / 100.f;
            row[28] = Math.round(rend_utuh * 100.f) / 100.f;
            row[29] = Math.round(rend_flat * 100.f) / 100.f;
            row[30] = Math.round(rend_jidun * 100.f) / 100.f;
            row[31] = Math.round(rend_total * 100.f) / 100.f;

            row[32] = Math.round(rend_sh_12 * 100.f) / 100.f;
            row[33] = Math.round(rend_sp_12 * 100.f) / 100.f;
            row[34] = Math.round(rend_utuh_12 * 100.f) / 100.f;
            row[35] = Math.round(rend_flat_12 * 100.f) / 100.f;
            row[36] = Math.round(rend_jidun_12 * 100.f) / 100.f;
            row[37] = Math.round(rend_total_12 * 100.f) / 100.f;

            model.addRow(row);

            //total Berat Kering
            tot_bk = tot_bk + bk;
            //total Berat Kering dengan asumsi kadar air 12%
            tot_bk12 = tot_bk12 + bk_12;
            //total keping utuh
            tot_kpg_utuh = tot_kpg_utuh + list.get(i).getFbonus_keping() + list.get(i).getFnol_keping();
            //total berat utuh
            tot_brt_utuh = tot_brt_utuh + list.get(i).getFbonus_berat() + list.get(i).getFnol_berat();
            //total netto
            tot_netto = tot_netto + netto_utuh;
            //total keping pecah
            tot_kpg_pch = tot_kpg_pch + list.get(i).getPecah_keping();
            //total berat pecah
            tot_brt_pch = tot_brt_pch + list.get(i).getPecah_berat();
            //total keping flat
            tot_kpg_flat = tot_kpg_flat + list.get(i).getFlat_keping();
            //total berat flat
            tot_brt_flat = tot_brt_flat + list.get(i).getFlat_berat();
            //total keping jidun
            tot_kpg_jidun = tot_kpg_jidun + list.get(i).getJidun_keping();
            //total keping jidun pecah
            tot_kpg_jidun_pecah = tot_kpg_jidun_pecah + list.get(i).getJidun_pecah();
            //total berat jidun
            tot_brt_jidun = tot_brt_jidun + netto_jidun;
            //total sesekan
            tot_sesekan = tot_sesekan + list.get(i).getSesekan();
            //total hancuran
            tot_hancuran = tot_hancuran + list.get(i).getHancuran();
            //total rontokan
            tot_rontokan = tot_rontokan + list.get(i).getRontokan();
            //total bonggol
            tot_bonggol = tot_bonggol + list.get(i).getBonggol();
            //total serabut
            tot_serabut = tot_serabut + list.get(i).getSerabut();
            //total sp
            tot_sp = tot_sp + sp;
            //total sh
            tot_sh = tot_sh + sh;
            //total sh 12
            tot_sh_12 = tot_sh_12 + sh_12;
            //total berat kaki
            tot_kaki = tot_kaki + kaki;

            //total rendemen
            tot_rend_sh = tot_rend_sh + rend_sh;
            tot_rend_sp = tot_rend_sp + rend_sp;
            tot_rend_utuh = tot_rend_utuh + rend_utuh;
            tot_rend_flat = tot_rend_flat + rend_flat;

            check_TargetRendemen(list.get(i).getGrade_bentuk(), list.get(i).getGrade_bulu(), bk, bk_12);
        }

        TotalData = Table_Setoran_harian_f2.getRowCount();
        //total LP
        label_total_lp.setText(decimalFormat.format(TotalData));
        label_bk.setText(decimalFormat.format(tot_bk));
        label_bk12.setText(decimalFormat.format(tot_bk12));
        label_kpg_utuh.setText(decimalFormat.format(tot_kpg_utuh));
        label_berat_netto_utuh.setText(decimalFormat.format(tot_netto));
        label_kpg_flat.setText(decimalFormat.format(tot_kpg_flat + tot_kpg_pch));
        label_berat_flat.setText(decimalFormat.format(tot_brt_flat + tot_brt_pch));
        label_berat_sp.setText(decimalFormat.format(tot_sp));
        label_berat_netto_jidun.setText(decimalFormat.format(tot_brt_jidun));
        label_total_berat_kaki.setText(decimalFormat.format(tot_kaki));
        //total keping setoran
        float total_kpg_setoran = tot_kpg_utuh + tot_kpg_flat + tot_kpg_pch;
        label_kpg_setoran.setText(decimalFormat.format(total_kpg_setoran));
        //total berat setoran
        float total_brt_setoran = tot_brt_utuh + tot_brt_flat + tot_brt_pch;
        label_berat_setoran.setText(decimalFormat.format(total_brt_setoran));

        //perhitungan rata2 rendemen 2
        float avg_rend_utuh1 = (tot_netto / tot_bk) * 100;
        label_rend_mk1.setText(decimalFormat.format(avg_rend_utuh1));
        float avg_rend_flat1 = ((tot_brt_flat + tot_brt_pch) / tot_bk) * 100;
        label_rend_flat1.setText(decimalFormat.format(avg_rend_flat1));
        float avg_rend_sp1 = (tot_sp / tot_bk) * 100;
        label_sp1.setText(decimalFormat.format(avg_rend_sp1));
        float avg_rend_sh1 = (tot_sh / tot_bk) * 100;
        label_sh1.setText(decimalFormat.format(avg_rend_sh1));
        float avg_rend_jidun1 = (tot_brt_jidun / tot_bk) * 100;
        label_rend_jidun1.setText(decimalFormat.format(avg_rend_jidun1));
        float avg_rend_total1 = avg_rend_sh1 + avg_rend_sp1 + avg_rend_utuh1 + avg_rend_flat1 + avg_rend_jidun1;
        label_rend_total1.setText(decimalFormat.format(avg_rend_total1));
        //perhitungan target rendemen 2
        float rata2_target_utuh1 = ((target_mk_br * 0.7f) + (target_mk_bs * 0.6f) + (target_mk_bb * 0.45f) + (target_ovlsgtg_br * 0.65f) + (target_ovlsgtg_bs * 0.55f) + (target_ovlsgtg_bb * 0.45f)) / tot_bk;
        label_target_utuh1.setText(decimalFormat.format(rata2_target_utuh1 * 100));
        float rata2_target_flat1 = ((target_mk_br * 0.1f) + (target_mk_bs * 0.15f) + (target_mk_bb * 0.25f) + (target_ovlsgtg_br * 0.15f) + (target_ovlsgtg_bs * 0.2f) + (target_ovlsgtg_bb * 0.3f) + (target_othr_br * 0.75f) + (target_othr_bs * 0.7f) + (target_othr_bb * 0.65f)) / tot_bk;
        label_target_flat1.setText(decimalFormat.format(rata2_target_flat1 * 100));
        float rata2_target_sp1 = ((target_mk_br * 0.14f) + (target_mk_bs * 0.17f) + (target_mk_bb * 0.2f) + (target_ovlsgtg_br * 0.14f) + (target_ovlsgtg_bs * 0.17f) + (target_ovlsgtg_bb * 0.2f) + (target_othr_br * 0.19f) + (target_othr_bs * 0.22f) + (target_othr_bb * 0.25f)) / tot_bk;
        label_target_sp1.setText(decimalFormat.format(rata2_target_sp1 * 100));
        float rata2_target_sh1 = ((target_mk_br * 0.06f) + (target_mk_bs * 0.08f) + (target_mk_bb * 0.1f) + (target_ovlsgtg_br * 0.06f) + (target_ovlsgtg_bs * 0.08f) + (target_ovlsgtg_bb * 0.1f) + (target_othr_br * 0.06f) + (target_othr_bs * 0.08f) + (target_othr_bb * 0.1f)) / tot_bk;
        label_target_sh1.setText(decimalFormat.format(rata2_target_sh1 * 100));
        //memberi warna jika susut lebih tinggi dari target maka warna merah, jika lebih rendah hijau, begitu kebalikan nya untuk rendemen
        if ((rata2_target_utuh1 * 100) > avg_rend_utuh1) {
            label_rend_mk1.setForeground(Color.red);
        } else {
            label_rend_mk1.setForeground(green);
        }
        if (avg_rend_flat1 > (rata2_target_flat1 * 100)) {
            label_rend_flat1.setForeground(Color.red);
        } else {
            label_rend_flat1.setForeground(green);
        }
        if (avg_rend_sp1 > (rata2_target_sp1 * 100)) {
            label_sp1.setForeground(Color.red);
        } else {
            label_sp1.setForeground(green);
        }
        if (avg_rend_sh1 > (rata2_target_sh1 * 100)) {
            label_sh1.setForeground(Color.red);
        } else {
            label_sh1.setForeground(green);
        }

        //perhitungan rata2 rendemen 3
        float avg_rend_utuh2 = (tot_netto / tot_bk12) * 100;
        label_rend_mk2.setText(decimalFormat.format(avg_rend_utuh2));
        float avg_rend_flat2 = ((tot_brt_flat + tot_brt_pch) / tot_bk12) * 100;
        label_rend_flat2.setText(decimalFormat.format(avg_rend_flat2));
        float avg_rend_sp2 = (tot_sp / tot_bk12) * 100;
        label_sp2.setText(decimalFormat.format(avg_rend_sp2));
        float avg_rend_sh2 = (tot_sh_12 / tot_bk12) * 100;
        label_sh2.setText(decimalFormat.format(avg_rend_sh2));
        float avg_rend_jidun2 = (tot_brt_jidun / tot_bk12) * 100;
        label_rend_jidun2.setText(decimalFormat.format(avg_rend_jidun2));
        float avg_rend_total2 = avg_rend_sh2 + avg_rend_sp2 + avg_rend_utuh2 + avg_rend_flat2 + avg_rend_jidun2;
        label_rend_total2.setText(decimalFormat.format(avg_rend_total2));
        //perhitungan target rendemen 3
        float rata2_target_utuh2 = ((target_mk_br_12 * 0.6f) + (target_mk_bs_12 * 0.5f) + (target_mk_bb_12 * 0.4f) + (target_ovlsgtg_br_12 * 0.55f) + (target_ovlsgtg_bs_12 * 0.45f) + (target_ovlsgtg_bb_12 * 0.35f)) / tot_bk12;
        label_target_utuh2.setText(decimalFormat.format(rata2_target_utuh2 * 100));
        float rata2_target_flat2 = ((target_mk_br_12 * 0.1f) + (target_mk_bs_12 * 0.15f) + (target_mk_bb_12 * 0.20f) + (target_ovlsgtg_br_12 * 0.15f) + (target_ovlsgtg_bs_12 * 0.2f) + (target_ovlsgtg_bb_12 * 0.25f) + (target_othr_br_12 * 0.70f) + (target_othr_bs_12 * 0.65f) + (target_othr_bb_12 * 0.60f)) / tot_bk12;
        label_target_flat2.setText(decimalFormat.format(rata2_target_flat2 * 100));
        float rata2_target_sp2 = ((target_mk_br_12 * 0.14f) + (target_mk_bs_12 * 0.16f) + (target_mk_bb_12 * 0.18f) + (target_ovlsgtg_br_12 * 0.14f) + (target_ovlsgtg_bs_12 * 0.16f) + (target_ovlsgtg_bb_12 * 0.18f) + (target_othr_br_12 * 0.14f) + (target_othr_bs_12 * 0.16f) + (target_othr_bb_12 * 0.18f)) / tot_bk12;
        label_target_sp2.setText(decimalFormat.format(rata2_target_sp2 * 100));
        float rata2_target_sh2 = ((target_mk_br_12 * 0.16f) + (target_mk_bs_12 * 0.19f) + (target_mk_bb_12 * 0.22f) + (target_ovlsgtg_br_12 * 0.16f) + (target_ovlsgtg_bs_12 * 0.19f) + (target_ovlsgtg_bb_12 * 0.22f) + (target_othr_br_12 * 0.16f) + (target_othr_bs_12 * 0.19f) + (target_othr_bb_12 * 0.22f)) / tot_bk12;
        label_target_sh2.setText(decimalFormat.format(rata2_target_sh2 * 100));
        //memberi warna jika susut lebih tinggi dari target maka warna merah, jika lebih rendah hijau, begitu kebalikan nya untuk rendemen

        if ((rata2_target_utuh2 * 100) > avg_rend_utuh2) {
            label_rend_mk2.setForeground(Color.red);
        } else {
            label_rend_mk2.setForeground(green);
        }
        if (avg_rend_flat2 > (rata2_target_flat2 * 100)) {
            label_rend_flat2.setForeground(Color.red);
        } else {
            label_rend_flat2.setForeground(green);
        }
        if (avg_rend_sp2 > (rata2_target_sp2 * 100)) {
            label_sp2.setForeground(Color.red);
        } else {
            label_sp2.setForeground(green);
        }
        if (avg_rend_sh2 > (rata2_target_sh2 * 100)) {
            label_sh2.setForeground(Color.red);
        } else {
            label_sh2.setForeground(green);
        }
//        DecimalFormat df = new DecimalFormat("#.#");
//        df.setRoundingMode(RoundingMode.CEILING);
        row[0] = "TOTAL";
        row[1] = null;
        row[2] = null;
        row[3] = null;
        row[4] = Math.round(tot_bk * 100.f) / 100.f;
        row[5] = Math.round(tot_bk12 * 10.f) / 10.f;
        row[6] = null;
        row[7] = null;
        row[8] = null;
        row[9] = Math.round(tot_kpg_utuh * 100.f) / 100.f;
        row[10] = Math.round(tot_brt_utuh * 100.f) / 100.f;
        row[11] = Math.round(tot_netto * 100.f) / 100.f;
        row[12] = Math.round(tot_kpg_pch * 100.f) / 100.f;
        row[13] = Math.round(tot_brt_pch * 100.f) / 100.f;
        row[14] = Math.round(tot_kpg_flat * 100.f) / 100.f;
        row[15] = Math.round(tot_brt_flat * 100.f) / 100.f;
        row[16] = Math.round(tot_kpg_jidun * 100.f) / 100.f;
        row[17] = Math.round(tot_kpg_jidun_pecah * 100.f) / 100.f;
        row[18] = null;
        row[19] = Math.round(tot_brt_jidun * 100.f) / 100.f;
        row[20] = Math.round(tot_sesekan * 100.f) / 100.f;
        row[21] = Math.round(tot_hancuran * 100.f) / 100.f;
        row[22] = Math.round(tot_rontokan * 100.f) / 100.f;
        row[23] = Math.round(tot_bonggol * 100.f) / 100.f;
        row[24] = Math.round(tot_serabut * 100.f) / 100.f;
        row[25] = Math.round(tot_kaki * 100.f) / 100.f;
        row[26] = null;
        row[27] = null;
        row[28] = null;
        row[29] = null;
        row[30] = null;
        row[31] = null;
        row[32] = null;
        row[33] = null;
        row[34] = null;
        row[35] = null;
        row[36] = null;
        row[37] = null;
        model.addRow(row);
        ColumnsAutoSizer.sizeColumnsToFit(Table_Setoran_harian_f2);

//        Table_Setoran_harian_f2.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
//            @Override
//            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
//                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
//                System.out.println("asuuww");
//                if ("TOTAL".equals(Table_Setoran_harian_f2.getValueAt(row, 0).toString())) {
//                    if (isSelected) {
//                        comp.setBackground(Table_Setoran_harian_f2.getSelectionBackground());
//                        comp.setForeground(Table_Setoran_harian_f2.getSelectionForeground());
//                    } else {
//                        comp.setBackground(Color.red);
//                        comp.setForeground(Color.white);
//                    }
//                }
//                return comp;
//            }
//        });
//        Table_Setoran_harian_f2.repaint();
    }

    public void refreshTable_Pencabut() {
        try {
            DefaultTableModel model = (DefaultTableModel) table_data_pencabut.getModel();
            model.setRowCount(0);
            int Row = Table_Setoran_harian_f2.getSelectedRow();
            sql = "SELECT `grup_cabut`,  `tb_detail_pencabut`.`id_pegawai`, `tb_karyawan`.`nama_pegawai`, `tanggal_cabut`, `jumlah_cabut`, `jumlah_gram` \n"
                    + "FROM `tb_detail_pencabut` \n"
                    + "JOIN `tb_karyawan` ON `tb_detail_pencabut`.`id_pegawai` = `tb_karyawan`.`id_pegawai`\n"
                    + "WHERE `no_laporan_produksi` = '" + Table_Setoran_harian_f2.getValueAt(Row, 1) + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[5];
            while (rs.next()) {
                row[0] = rs.getString("id_pegawai");
                row[1] = rs.getString("nama_pegawai");
                row[2] = rs.getDate("tanggal_cabut");
                row[3] = rs.getInt("jumlah_cabut");
                row[4] = rs.getString("grup_cabut");
                model.addRow(row);
            }
            int rowData = table_data_pencabut.getRowCount();
            label_total_pencabut.setText(Integer.toString(rowData));
            ColumnsAutoSizer.sizeColumnsToFit(table_data_pencabut);

            TableAlignment.setHorizontalAlignment(JLabel.CENTER);
            //tabel Data Bahan Baku
            for (int i = 0; i < table_data_pencabut.getColumnCount(); i++) {
                table_data_pencabut.getColumnModel().getColumn(i).setCellRenderer(TableAlignment);
            }
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_Finishing2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refresh_TabelBalen() {
        try {
            int tot_kpg_awal = 0, tot_kpg_akhir = 0;
            float tot_gram_awal = 0, tot_gram_akhir = 0;
            DefaultTableModel model = (DefaultTableModel) Table_balen.getModel();
            model.setRowCount(0);
            sql = "SELECT `no_laporan_produksi`, `tgl_balen`, `keping1`, `gram1`, `keping2`, `gram2`, `pekerja`, `tgl_selesai`, `Keterangan` FROM `tb_balen_f2` WHERE 1";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("no_laporan_produksi"),
                    rs.getDate("tgl_balen"),
                    rs.getInt("keping1"),
                    rs.getFloat("gram1"),
                    rs.getInt("keping2"),
                    rs.getFloat("gram2"),
                    rs.getString("pekerja"),
                    rs.getDate("tgl_selesai"),
                    rs.getString("Keterangan")});
                tot_kpg_awal = tot_kpg_awal + rs.getInt("keping1");
                tot_gram_awal = tot_gram_awal + rs.getInt("gram1");
                tot_kpg_akhir = tot_kpg_akhir + rs.getInt("keping2");
                tot_gram_akhir = tot_gram_akhir + rs.getInt("gram2");
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_balen);
            label_total_lp_balen.setText(Integer.toString(Table_balen.getRowCount()));
            label_total_kpg_awal.setText(Integer.toString(tot_kpg_awal));
            label_total_gram_awal.setText(Float.toString(tot_gram_awal));
            label_total_kpg_akhir.setText(Integer.toString(tot_kpg_akhir));
            label_total_gram_akhir.setText(Float.toString(tot_gram_akhir));
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_Finishing2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refresh_EvaluasiMLEM() {
        try {
            float total_mlem = 0;
            String ruang = "AND tutupan.`ruangan` = '" + ComboBox_evaluasi_ruanganMLEM.getSelectedItem().toString() + "'";
            if (ComboBox_evaluasi_ruanganMLEM.getSelectedItem().toString().equals("All")) {
                ruang = "";
            }
            String tanggal = "";
            if (DateTutupan1.getDate() != null && DateTutupan2.getDate() != null) {
                tanggal = "AND `tgl_statusBox` BETWEEN '" + dateFormat.format(DateTutupan1.getDate()) + "' AND '" + dateFormat.format(DateTutupan2.getDate()) + "'";
            }
            DefaultTableModel model = (DefaultTableModel) Table_evaluasi_MLEM.getModel();
            model.setRowCount(0);
            sql = "SELECT tutupan.`kode_tutupan`, tutupan.`ruangan`, `tgl_statusBox`, `jumlah_lp`, `gram_mlem`, `gram_grading`\n"
                    + "FROM\n"
                    + "(SELECT `tb_tutupan_grading`.`kode_tutupan`, `tgl_statusBox`, `tb_laporan_produksi`.`ruangan`, COUNT(`tb_bahan_jadi_masuk`.`kode_tutupan`) AS 'jumlah_lp'\n"
                    + "FROM `tb_tutupan_grading` \n"
                    + "LEFT JOIN `tb_bahan_jadi_masuk` ON `tb_tutupan_grading`.`kode_tutupan` = `tb_bahan_jadi_masuk`.`kode_tutupan`\n"
                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_bahan_jadi_masuk`.`kode_asal` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    + "WHERE `tb_laporan_produksi`.`ruangan` IS NOT NULL\n"
                    + "GROUP BY `kode_tutupan`, `tb_laporan_produksi`.`ruangan`) tutupan\n"
                    + "LEFT JOIN (SELECT `kode_tutupan`, `ruangan`, `grade_bahan_jadi`, SUM(`tb_grading_bahan_jadi`.`gram`) AS 'gram_grading', SUM(IF(`grade_bahan_jadi`=033, `tb_grading_bahan_jadi`.`gram`, 0)) AS 'gram_mlem'\n"
                    + "FROM `tb_grading_bahan_jadi` \n"
                    + "LEFT JOIN `tb_bahan_jadi_masuk` ON `tb_grading_bahan_jadi`.`kode_asal_bahan_jadi` = `tb_bahan_jadi_masuk`.`kode_asal`\n"
                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_grading_bahan_jadi`.`kode_asal_bahan_jadi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    + "WHERE `ruangan` IS NOT NULL \n"
                    + "GROUP BY `kode_tutupan`, `ruangan`) grading ON tutupan.`kode_tutupan` = grading.`kode_tutupan` AND tutupan.`ruangan` = grading.`ruangan`\n"
                    + "WHERE `tgl_statusBox` > '2021-01-01'"
                    + "AND tutupan.`kode_tutupan` LIKE '%" + txt_search_evaluasi_tutupan.getText() + "%' "
                    + ruang
                    + tanggal
                    + "ORDER BY tutupan.`kode_tutupan` DESC, tutupan.`ruangan`";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("kode_tutupan"),
                    rs.getString("ruangan"),
                    rs.getDate("tgl_statusBox"),
                    rs.getInt("jumlah_lp"),
                    rs.getFloat("gram_mlem"),
                    rs.getFloat("gram_grading"),
                    Math.round(rs.getFloat("gram_mlem") / rs.getFloat("gram_grading") * 1000) / 10d});
                total_mlem = total_mlem + rs.getFloat("gram_mlem");
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_evaluasi_MLEM);
            label_total_data_tutupan.setText(Integer.toString(Table_evaluasi_MLEM.getRowCount()));
            label_total_mlem.setText(decimalFormat.format(total_mlem));

            DefaultTableModel tabel_summary = (DefaultTableModel) Table_evaluasi_MLEM_summary.getModel();
            tabel_summary.setRowCount(0);
            sql = "SELECT `ruangan`, `grade_bahan_jadi`, SUM(`tb_grading_bahan_jadi`.`gram`) AS 'gram_grading', SUM(IF(`grade_bahan_jadi`=033, `tb_grading_bahan_jadi`.`gram`, 0)) AS 'gram_mlem'\n"
                    + "FROM `tb_grading_bahan_jadi` \n"
                    + "LEFT JOIN `tb_bahan_jadi_masuk` ON `tb_grading_bahan_jadi`.`kode_asal_bahan_jadi` = `tb_bahan_jadi_masuk`.`kode_asal`\n"
                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_grading_bahan_jadi`.`kode_asal_bahan_jadi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_tutupan_grading` ON `tb_bahan_jadi_masuk`.`kode_tutupan` = `tb_tutupan_grading`.`kode_tutupan`\n"
                    + "WHERE `ruangan` IS NOT NULL AND `tgl_statusBox` > '2021-01-01'"
                    + "AND `tb_bahan_jadi_masuk`.`kode_tutupan` LIKE '%" + txt_search_evaluasi_tutupan.getText() + "%' "
                    + ruang
                    + tanggal
                    + "GROUP BY `ruangan` ORDER BY `ruangan`";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                tabel_summary.addRow(new Object[]{
                    rs.getString("ruangan"),
                    rs.getFloat("gram_mlem"),
                    Math.round(rs.getFloat("gram_mlem") / rs.getFloat("gram_grading") * 1000) / 10d});
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_evaluasi_MLEM_summary);
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_Finishing2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void refreshTable_LP_suwir() {
        try {
            float total_stok = 0, total_keluar_f2 = 0;
            DefaultTableModel model = (DefaultTableModel) table_data_LPsuwir.getModel();
            model.setRowCount(0);
            String filter_tanggal = "";;
            if (Date_Search_LPsuwir_1.getDate() != null && Date_Search_LPsuwir_2.getDate() != null) {
                filter_tanggal = "AND `tgl_lp_suwir` BETWEEN '" + dateFormat.format(Date_Search_LPsuwir_1.getDate()) + "' AND '" + dateFormat.format(Date_Search_LPsuwir_2.getDate()) + "'\n";
            }
            sql = "SELECT `tb_lp_suwir`.`no_lp_suwir`, `tgl_lp_suwir`, `keping`, `gram`, `gram_akhir`, COUNT(`no_box`) AS 'jumlah_box'\n"
                    + "FROM `tb_lp_suwir` \n"
                    + "LEFT JOIN `tb_lp_suwir_detail` ON `tb_lp_suwir`.`no_lp_suwir` = `tb_lp_suwir_detail`.`no_lp_suwir`\n"
                    + "WHERE "
                    + "`tb_lp_suwir`.`no_lp_suwir` LIKE '%" + txt_search_lpsuwir.getText() + "%' "
                    + filter_tanggal
                    + "GROUP BY `tb_lp_suwir`.`no_lp_suwir` "
                    + "ORDER BY `tgl_lp_suwir` DESC";
            PreparedStatement pst = Utility.db.getConnection().prepareStatement(sql);
            rs = pst.executeQuery();
            Object[] baris = new Object[9];
            while (rs.next()) {
                baris[0] = rs.getString("no_lp_suwir");
                baris[1] = rs.getDate("tgl_lp_suwir");
                baris[2] = rs.getInt("keping");
                baris[3] = rs.getFloat("gram");
                baris[4] = rs.getFloat("gram_akhir");
                baris[5] = rs.getInt("jumlah_box");
                String lp_kaki = rs.getString("no_lp_suwir");
                float keluar_f2 = 0;
                String sql1 = "SELECT SUM(IF(`lp_kaki1` = '" + lp_kaki + "', `tambahan_kaki1`, 0)) AS 'tambahan_kaki1', "
                        + "SUM(IF(`lp_kaki2` = '" + lp_kaki + "', `tambahan_kaki2`, 0)) AS 'tambahan_kaki2' "
                        + "FROM `tb_finishing_2` WHERE `lp_kaki1` = '" + lp_kaki + "' OR `lp_kaki2` = '" + lp_kaki + "'";
                pst = Utility.db.getConnection().prepareStatement(sql1);
                ResultSet rs_keluar1 = pst.executeQuery();
                if (rs_keluar1.next()) {
                    keluar_f2 = rs_keluar1.getFloat("tambahan_kaki1") + rs_keluar1.getFloat("tambahan_kaki2");
                    total_keluar_f2 = total_keluar_f2 + keluar_f2;
                }
                float keluar_reproses = 0;
                String sql2 = "SELECT SUM(IF(`no_lp_suwir` = '" + lp_kaki + "', `gram_kaki`, 0)) AS 'keluar_reproses1', "
                        + "SUM(IF(`no_lp_suwir2` = '" + lp_kaki + "', `gram_kaki2`, 0)) AS 'keluar_reproses2' "
                        + "FROM `tb_reproses` WHERE `no_lp_suwir` = '" + lp_kaki + "' OR `no_lp_suwir2` = '" + lp_kaki + "'";
                pst = Utility.db.getConnection().prepareStatement(sql2);
                ResultSet rs_keluar2 = pst.executeQuery();
                if (rs_keluar2.next()) {
                    keluar_reproses = rs_keluar2.getFloat("keluar_reproses1") + rs_keluar2.getFloat("keluar_reproses2");
                }
                baris[6] = keluar_f2;
                baris[7] = keluar_reproses;
                float stok = rs.getFloat("gram_akhir") - (keluar_f2 + keluar_reproses);
                baris[8] = stok;
                total_stok = total_stok + stok;
                model.addRow(baris);
            }
            ColumnsAutoSizer.sizeColumnsToFit(table_data_LPsuwir);
            label_total_lpsuwir.setText(Integer.toString(table_data_LPsuwir.getRowCount()));
            label_total_stok.setText(decimalFormat.format(total_stok));
            label_total_keluar_f2.setText(decimalFormat.format(total_keluar_f2));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Finishing2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_LaporanProduksi_LPsuwir(String lp_kaki) {
        try {
            float total_gram = 0;
            DefaultTableModel model = (DefaultTableModel) table_LaporanProduksi_LPSuwir.getModel();
            model.setRowCount(0);
            sql = "SELECT `no_laporan_produksi`, `tambahan_kaki1`, `lp_kaki1`, `tambahan_kaki2`, `lp_kaki2` FROM `tb_finishing_2` "
                    + "WHERE "
                    + "`lp_kaki1` = '" + lp_kaki + "' "
                    + "OR `lp_kaki2` = '" + lp_kaki + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[5];
            while (rs.next()) {
                row[0] = rs.getString("no_laporan_produksi");
                row[1] = rs.getString("lp_kaki1");
                row[2] = rs.getFloat("tambahan_kaki1");
                row[3] = rs.getString("lp_kaki2");
                row[4] = rs.getFloat("tambahan_kaki2");
                model.addRow(row);

                if (rs.getString("lp_kaki1").equals(lp_kaki)) {
                    total_gram = total_gram + rs.getFloat("tambahan_kaki1");
                }
                if (rs.getString("lp_kaki2").equals(lp_kaki)) {
                    total_gram = total_gram + rs.getFloat("tambahan_kaki2");
                }
            }
            ColumnsAutoSizer.sizeColumnsToFit(table_LaporanProduksi_LPSuwir);
            label_total_LaporanProduksi_lpsuwir.setText(Integer.toString(table_LaporanProduksi_LPSuwir.getRowCount()));
            label_total_gram_LaporanProduksi_lpsuwir.setText(decimalFormat.format(total_gram));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Finishing2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_BoxReproses_LPsuwir(String lp_kaki) {
        try {
            float total_gram = 0;
            DefaultTableModel model = (DefaultTableModel) table_BoxReprosesi_LPSuwir.getModel();
            model.setRowCount(0);
            sql = "SELECT `no_reproses`, `no_box`, `tanggal_proses`, `no_lp_suwir`, `no_lp_suwir2`, `gram_kaki`, `gram_kaki2` "
                    + "FROM `tb_reproses` WHERE `no_lp_suwir` = '" + lp_kaki + "' OR `no_lp_suwir2` = '" + lp_kaki + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[5];
            while (rs.next()) {
                row[0] = rs.getString("no_reproses");
                row[1] = rs.getString("no_box");
                row[2] = rs.getDate("tanggal_proses");
                if (rs.getString("no_lp_suwir").equals(lp_kaki)) {
                    row[3] = rs.getString("no_lp_suwir");
                    row[4] = rs.getFloat("gram_kaki");
                    total_gram = total_gram + rs.getFloat("gram_kaki");
                } else if (rs.getString("no_lp_suwir2").equals(lp_kaki)) {
                    row[3] = rs.getString("no_lp_suwir2");
                    row[4] = rs.getFloat("gram_kaki2");
                    total_gram = total_gram + rs.getFloat("gram_kaki2");
                }

                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(table_BoxReprosesi_LPSuwir);
            label_total_BoxRepacking_lpsuwir.setText(Integer.toString(table_BoxReprosesi_LPSuwir.getRowCount()));
            label_total_gram_BoxRepracking_lpsuwir.setText(decimalFormat.format(total_gram));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Finishing2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_asalBox_LPSuwir(String no_lp_kaki) {
        try {
            int total_keping = 0;
            float total_gram = 0;
            DefaultTableModel model = (DefaultTableModel) table_asalBox_lpSuwir.getModel();
            model.setRowCount(0);
            sql = "SELECT `tb_lp_suwir_detail`.`no_box`, `tanggal_box`, `tb_grade_bahan_jadi`.`kode_grade`, `keping`, `berat`, `no_tutupan` FROM `tb_lp_suwir_detail` "
                    + "LEFT JOIN `tb_box_bahan_jadi` ON `tb_lp_suwir_detail`.`no_box` = `tb_box_bahan_jadi`.`no_box` "
                    + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_box_bahan_jadi`.`kode_grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode` "
                    + "WHERE `tb_lp_suwir_detail`.`no_lp_suwir` = '" + no_lp_kaki + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[6];
            while (rs.next()) {
                row[0] = rs.getString("no_box");
                row[1] = rs.getDate("tanggal_box");
                row[2] = rs.getString("kode_grade");
                row[3] = rs.getInt("keping");
                total_keping = total_keping + rs.getInt("keping");
                row[4] = rs.getFloat("berat");
                total_gram = total_gram + rs.getFloat("berat");
                row[5] = rs.getString("no_tutupan");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(table_asalBox_lpSuwir);
            label_total_asalBox_lpSuwir.setText(Integer.toString(table_asalBox_lpSuwir.getRowCount()));
            label_total_keping_asalBox_lpsuwir.setText(Integer.toString(total_keping));
            label_total_gram_asalBox_lpsuwir.setText(decimalFormat.format(total_gram));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Finishing2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        Table_Data_f2 = new javax.swing.JTable();
        button_f2_terima_lp = new javax.swing.JButton();
        button_f2_setor_lp = new javax.swing.JButton();
        button_f2_edit = new javax.swing.JButton();
        button_f2_delete = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        label_total_data_f2 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txt_search_no_lp = new javax.swing.JTextField();
        button_search_f2 = new javax.swing.JButton();
        Date1_f2 = new com.toedter.calendar.JDateChooser();
        Date2_f2 = new com.toedter.calendar.JDateChooser();
        button_export_f2 = new javax.swing.JButton();
        ComboBox_SearchDate = new javax.swing.JComboBox<>();
        button_input_sesekan = new javax.swing.JButton();
        button_input_koreksi = new javax.swing.JButton();
        button_input_f1 = new javax.swing.JButton();
        button_input_f2 = new javax.swing.JButton();
        button_input_byproduct = new javax.swing.JButton();
        button_input_koreksi1 = new javax.swing.JButton();
        button_balen = new javax.swing.JButton();
        button_input_kaki = new javax.swing.JButton();
        button_tv_reproses = new javax.swing.JButton();
        button_laporan_f2 = new javax.swing.JButton();
        jLabel17 = new javax.swing.JLabel();
        txt_search_memo_f2 = new javax.swing.JTextField();
        button_f2_edit_kaki = new javax.swing.JButton();
        button_laporan_terima_WLT = new javax.swing.JButton();
        ComboBox_LaporanTerima = new javax.swing.JComboBox<>();
        Date_LaporanTerima = new com.toedter.calendar.JDateChooser();
        jLabel24 = new javax.swing.JLabel();
        button_laporan_terima_SUB = new javax.swing.JButton();
        jLabel27 = new javax.swing.JLabel();
        txt_search_ruangan = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        txt_search_LP_setoran = new javax.swing.JTextField();
        button_search_setoran = new javax.swing.JButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        Table_Setoran_harian_f2 = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        ComboBox_ruangan = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        Date_Setoran1 = new com.toedter.calendar.JDateChooser();
        jPanel3 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        label_total_lp = new javax.swing.JLabel();
        label_kpg_setoran = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        label_berat_setoran = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        label_berat_netto_utuh = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        label_kpg_utuh = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        label_berat_flat = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        label_kpg_flat = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        label_bk = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        label_total_berat_kaki = new javax.swing.JLabel();
        jLabel68 = new javax.swing.JLabel();
        label_bk12 = new javax.swing.JLabel();
        jLabel69 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        label_berat_sp = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        label_berat_netto_jidun = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table_data_pencabut = new javax.swing.JTable();
        jLabel46 = new javax.swing.JLabel();
        button_export_f2_rendemen = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        label_total_pencabut = new javax.swing.JLabel();
        Date_Setoran2 = new com.toedter.calendar.JDateChooser();
        jLabel9 = new javax.swing.JLabel();
        txt_search_kartu_setoran = new javax.swing.JTextField();
        jPanel6 = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        label_sh1 = new javax.swing.JLabel();
        jLabel56 = new javax.swing.JLabel();
        jLabel57 = new javax.swing.JLabel();
        jLabel54 = new javax.swing.JLabel();
        label_rend_total1 = new javax.swing.JLabel();
        jLabel53 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel55 = new javax.swing.JLabel();
        label_rend_mk1 = new javax.swing.JLabel();
        label_rend_flat1 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel52 = new javax.swing.JLabel();
        label_sp1 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        label_rend_jidun1 = new javax.swing.JLabel();
        jLabel62 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel64 = new javax.swing.JLabel();
        label_target_sp1 = new javax.swing.JLabel();
        jLabel58 = new javax.swing.JLabel();
        jLabel61 = new javax.swing.JLabel();
        label_target_sh1 = new javax.swing.JLabel();
        jLabel60 = new javax.swing.JLabel();
        jLabel65 = new javax.swing.JLabel();
        label_target_utuh1 = new javax.swing.JLabel();
        jLabel66 = new javax.swing.JLabel();
        label_target_flat1 = new javax.swing.JLabel();
        jLabel63 = new javax.swing.JLabel();
        jLabel59 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        label_rend_flat2 = new javax.swing.JLabel();
        label_sh2 = new javax.swing.JLabel();
        jLabel96 = new javax.swing.JLabel();
        jLabel94 = new javax.swing.JLabel();
        jLabel93 = new javax.swing.JLabel();
        jLabel95 = new javax.swing.JLabel();
        jLabel92 = new javax.swing.JLabel();
        label_rend_mk2 = new javax.swing.JLabel();
        label_rend_total2 = new javax.swing.JLabel();
        jLabel91 = new javax.swing.JLabel();
        jLabel88 = new javax.swing.JLabel();
        jLabel90 = new javax.swing.JLabel();
        jLabel89 = new javax.swing.JLabel();
        label_sp2 = new javax.swing.JLabel();
        jLabel97 = new javax.swing.JLabel();
        jLabel98 = new javax.swing.JLabel();
        label_rend_jidun2 = new javax.swing.JLabel();
        jLabel99 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jLabel78 = new javax.swing.JLabel();
        label_target_sp2 = new javax.swing.JLabel();
        jLabel79 = new javax.swing.JLabel();
        jLabel80 = new javax.swing.JLabel();
        label_target_sh2 = new javax.swing.JLabel();
        jLabel81 = new javax.swing.JLabel();
        jLabel82 = new javax.swing.JLabel();
        label_target_utuh2 = new javax.swing.JLabel();
        jLabel83 = new javax.swing.JLabel();
        label_target_flat2 = new javax.swing.JLabel();
        jLabel86 = new javax.swing.JLabel();
        jLabel87 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        ComboBox_searchBentuk = new javax.swing.JComboBox<>();
        jLabel14 = new javax.swing.JLabel();
        txt_search_Memo_Setoran = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        txt_search_evaluasi_tutupan = new javax.swing.JTextField();
        button_refresh_evaluasiMLEM = new javax.swing.JButton();
        jScrollPane7 = new javax.swing.JScrollPane();
        Table_evaluasi_MLEM = new javax.swing.JTable();
        jLabel19 = new javax.swing.JLabel();
        label_total_data_tutupan = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        DateTutupan1 = new com.toedter.calendar.JDateChooser();
        DateTutupan2 = new com.toedter.calendar.JDateChooser();
        jLabel20 = new javax.swing.JLabel();
        ComboBox_evaluasi_ruanganMLEM = new javax.swing.JComboBox<>();
        jLabel22 = new javax.swing.JLabel();
        label_total_mlem = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        Table_evaluasi_MLEM_summary = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        txt_search_lp_balen = new javax.swing.JTextField();
        button_search_lp_balen = new javax.swing.JButton();
        jScrollPane6 = new javax.swing.JScrollPane();
        Table_balen = new javax.swing.JTable();
        button_selesai_balen = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jLabel70 = new javax.swing.JLabel();
        label_total_kpg_akhir = new javax.swing.JLabel();
        jLabel71 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        label_total_kpg_awal = new javax.swing.JLabel();
        label_total_lp_balen = new javax.swing.JLabel();
        jLabel72 = new javax.swing.JLabel();
        label_total_gram_akhir = new javax.swing.JLabel();
        label_total_gram_awal = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        button_delete = new javax.swing.JButton();
        jPanel_data_LP_suwir = new javax.swing.JPanel();
        jLabel40 = new javax.swing.JLabel();
        txt_search_lpsuwir = new javax.swing.JTextField();
        jLabel41 = new javax.swing.JLabel();
        Date_Search_LPsuwir_1 = new com.toedter.calendar.JDateChooser();
        jLabel42 = new javax.swing.JLabel();
        Date_Search_LPsuwir_2 = new com.toedter.calendar.JDateChooser();
        button_search_lp = new javax.swing.JButton();
        jScrollPane10 = new javax.swing.JScrollPane();
        table_data_LPsuwir = new javax.swing.JTable();
        jLabel67 = new javax.swing.JLabel();
        jLabel73 = new javax.swing.JLabel();
        jScrollPane11 = new javax.swing.JScrollPane();
        table_asalBox_lpSuwir = new javax.swing.JTable();
        jLabel74 = new javax.swing.JLabel();
        label_total_asalBox_lpSuwir = new javax.swing.JLabel();
        jLabel75 = new javax.swing.JLabel();
        label_total_keping_asalBox_lpsuwir = new javax.swing.JLabel();
        jLabel76 = new javax.swing.JLabel();
        label_total_gram_asalBox_lpsuwir = new javax.swing.JLabel();
        jLabel77 = new javax.swing.JLabel();
        label_total_lpsuwir = new javax.swing.JLabel();
        label_asalBox_lpSuwir = new javax.swing.JLabel();
        button_export_LPSuwir = new javax.swing.JButton();
        button_export_AsalBox = new javax.swing.JButton();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel10 = new javax.swing.JPanel();
        label_LaporanProduksi_lpSuwir = new javax.swing.JLabel();
        jLabel84 = new javax.swing.JLabel();
        jLabel85 = new javax.swing.JLabel();
        label_total_LaporanProduksi_lpsuwir = new javax.swing.JLabel();
        jLabel100 = new javax.swing.JLabel();
        button_export_LP_F2 = new javax.swing.JButton();
        jScrollPane12 = new javax.swing.JScrollPane();
        table_LaporanProduksi_LPSuwir = new javax.swing.JTable();
        label_total_gram_LaporanProduksi_lpsuwir = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jLabel101 = new javax.swing.JLabel();
        jScrollPane13 = new javax.swing.JScrollPane();
        table_BoxReprosesi_LPSuwir = new javax.swing.JTable();
        label_total_gram_BoxRepracking_lpsuwir = new javax.swing.JLabel();
        label_BoxReproses_lpSuwir1 = new javax.swing.JLabel();
        button_export_BoxReproses = new javax.swing.JButton();
        jLabel102 = new javax.swing.JLabel();
        label_total_BoxRepacking_lpsuwir = new javax.swing.JLabel();
        jLabel103 = new javax.swing.JLabel();
        jLabel104 = new javax.swing.JLabel();
        label_total_stok = new javax.swing.JLabel();
        label_total_keluar_f2 = new javax.swing.JLabel();
        jLabel105 = new javax.swing.JLabel();
        button_Print_LP_SWR = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Data Finishing 2", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N
        setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        setPreferredSize(new java.awt.Dimension(1366, 700));

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jTabbedPane1.setPreferredSize(new java.awt.Dimension(1356, 652));
        jTabbedPane1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPane1StateChanged(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setPreferredSize(new java.awt.Dimension(1351, 652));

        Table_Data_f2.setAutoCreateRowSorter(true);
        Table_Data_f2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_Data_f2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No LP", "Kpg", "Berat Angin", "Grade", "Ruang", "Tgl BP masuk", "Tgl Masuk", "Diterima", "Tgl koreksi kering", "Pekerja Koreksi", "Tgl F1", "Pekerja F1", "Tgl F2", "Pekerja F2", "Tgl Selesai", "Diserahkan", "P. Timbang", "FBonus", "Berat FBonus", "F Nol", "Berat F Nol", "Pch.Kpg", "Pch.BK", "Flat.Kpg", "Flat.BK", "Jidun.Utuh", "Jidun.Pch", "Jidun.BK", "S", "H", "R", "B", "Srbt", "G.Kaki 1", "LP kaki 1", "G. kaki 2", "LP kaki 2", "Admin", "Otorisasi", "Keterangan", "Tanpa Kaki F1", "Kaki Kecil F1", "Kaki Besar F1", "Flat F1", "Tgl Input Ssk", "Edited"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Float.class, java.lang.String.class, java.lang.Float.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Table_Data_f2.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(Table_Data_f2);

        button_f2_terima_lp.setBackground(new java.awt.Color(255, 255, 255));
        button_f2_terima_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_f2_terima_lp.setText("Terima LP");
        button_f2_terima_lp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_f2_terima_lpActionPerformed(evt);
            }
        });

        button_f2_setor_lp.setBackground(new java.awt.Color(255, 255, 255));
        button_f2_setor_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_f2_setor_lp.setText("Setor LP");
        button_f2_setor_lp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_f2_setor_lpActionPerformed(evt);
            }
        });

        button_f2_edit.setBackground(new java.awt.Color(255, 255, 255));
        button_f2_edit.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_f2_edit.setText("Edit");
        button_f2_edit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_f2_editActionPerformed(evt);
            }
        });

        button_f2_delete.setBackground(new java.awt.Color(255, 255, 255));
        button_f2_delete.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_f2_delete.setText("Delete");
        button_f2_delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_f2_deleteActionPerformed(evt);
            }
        });

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel6.setText("Total Data :");

        label_total_data_f2.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_f2.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_data_f2.setText("TOTAL");

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("No LP :");

        txt_search_no_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_no_lp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_no_lpKeyPressed(evt);
            }
        });

        button_search_f2.setBackground(new java.awt.Color(255, 255, 255));
        button_search_f2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search_f2.setText("Search");
        button_search_f2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_search_f2ActionPerformed(evt);
            }
        });

        Date1_f2.setBackground(new java.awt.Color(255, 255, 255));
        Date1_f2.setDate(new Date(new Date().getTime()- (7 * 24 * 60 * 60 * 1000)));
        Date1_f2.setDateFormatString("dd MMM yyyy");
        Date1_f2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Date2_f2.setBackground(new java.awt.Color(255, 255, 255));
        Date2_f2.setDate(new Date());
        Date2_f2.setDateFormatString("dd MMM yyyy");
        Date2_f2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        button_export_f2.setBackground(new java.awt.Color(255, 255, 255));
        button_export_f2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_f2.setText("Export to Excel");
        button_export_f2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_f2ActionPerformed(evt);
            }
        });

        ComboBox_SearchDate.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_SearchDate.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tgl Input By Product", "Tgl Koreksi Kering", "Tgl F1", "Tgl F2", "Tgl Setor", "Tgl input ssk" }));

        button_input_sesekan.setBackground(new java.awt.Color(255, 255, 255));
        button_input_sesekan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_input_sesekan.setText("Input Sesekan");
        button_input_sesekan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_input_sesekanActionPerformed(evt);
            }
        });

        button_input_koreksi.setBackground(new java.awt.Color(255, 255, 255));
        button_input_koreksi.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_input_koreksi.setText("Input Koreksi");
        button_input_koreksi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_input_koreksiActionPerformed(evt);
            }
        });

        button_input_f1.setBackground(new java.awt.Color(255, 255, 255));
        button_input_f1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_input_f1.setText("Input F1");
        button_input_f1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_input_f1ActionPerformed(evt);
            }
        });

        button_input_f2.setBackground(new java.awt.Color(255, 255, 255));
        button_input_f2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_input_f2.setText("Input F2");
        button_input_f2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_input_f2ActionPerformed(evt);
            }
        });

        button_input_byproduct.setBackground(new java.awt.Color(255, 255, 255));
        button_input_byproduct.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_input_byproduct.setText("Input By Product");
        button_input_byproduct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_input_byproductActionPerformed(evt);
            }
        });

        button_input_koreksi1.setBackground(new java.awt.Color(255, 255, 255));
        button_input_koreksi1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_input_koreksi1.setText("Input Koreksi V2");
        button_input_koreksi1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_input_koreksi1ActionPerformed(evt);
            }
        });

        button_balen.setBackground(new java.awt.Color(255, 255, 255));
        button_balen.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_balen.setText("Balen");
        button_balen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_balenActionPerformed(evt);
            }
        });

        button_input_kaki.setBackground(new java.awt.Color(255, 255, 255));
        button_input_kaki.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_input_kaki.setText("Input Kaki");
        button_input_kaki.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_input_kakiActionPerformed(evt);
            }
        });

        button_tv_reproses.setBackground(new java.awt.Color(255, 255, 255));
        button_tv_reproses.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_tv_reproses.setText("TV Reproses");
        button_tv_reproses.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_tv_reprosesActionPerformed(evt);
            }
        });

        button_laporan_f2.setBackground(new java.awt.Color(255, 255, 255));
        button_laporan_f2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_laporan_f2.setText("Catatan Pemeriksaan Kebersihan Sarang Walet Selama Proses");
        button_laporan_f2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_laporan_f2ActionPerformed(evt);
            }
        });

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel17.setText("Memo LP :");

        txt_search_memo_f2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_memo_f2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_memo_f2KeyPressed(evt);
            }
        });

        button_f2_edit_kaki.setBackground(new java.awt.Color(255, 255, 255));
        button_f2_edit_kaki.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_f2_edit_kaki.setText("Edit Kaki");
        button_f2_edit_kaki.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_f2_edit_kakiActionPerformed(evt);
            }
        });

        button_laporan_terima_WLT.setBackground(new java.awt.Color(255, 255, 255));
        button_laporan_terima_WLT.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_laporan_terima_WLT.setText("Print Laporan Terima LP WLT");
        button_laporan_terima_WLT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_laporan_terima_WLTActionPerformed(evt);
            }
        });

        ComboBox_LaporanTerima.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_LaporanTerima.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Koreksi Kering", "F1", "F2" }));

        Date_LaporanTerima.setBackground(new java.awt.Color(255, 255, 255));
        Date_LaporanTerima.setDate(new Date(new Date().getTime()- (7 * 24 * 60 * 60 * 1000)));
        Date_LaporanTerima.setDateFormatString("dd MMM yyyy");
        Date_LaporanTerima.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel24.setBackground(new java.awt.Color(255, 255, 255));
        jLabel24.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel24.setText("Laporan Terima :");

        button_laporan_terima_SUB.setBackground(new java.awt.Color(255, 255, 255));
        button_laporan_terima_SUB.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_laporan_terima_SUB.setText("Print Laporan Terima LP SUB");
        button_laporan_terima_SUB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_laporan_terima_SUBActionPerformed(evt);
            }
        });

        jLabel27.setBackground(new java.awt.Color(255, 255, 255));
        jLabel27.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel27.setText("Ruangan");

        txt_search_ruangan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_ruangan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_ruanganKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(button_input_sesekan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_input_byproduct)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_f2_terima_lp)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_input_koreksi)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_input_koreksi1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_input_f1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_input_f2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_input_kaki)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_f2_edit_kaki)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_f2_setor_lp)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_f2_edit)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_f2_delete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_balen)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 117, Short.MAX_VALUE)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_data_f2))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel24)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_LaporanTerima, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_LaporanTerima, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_laporan_terima_WLT)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_laporan_terima_SUB)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_laporan_f2))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_no_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel17)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_memo_f2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel27)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_ruangan, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_SearchDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date1_f2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date2_f2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_search_f2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_tv_reproses)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_export_f2)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txt_search_memo_f2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_search_ruangan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txt_search_no_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(ComboBox_SearchDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(Date2_f2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date1_f2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(button_search_f2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_tv_reproses, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_export_f2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ComboBox_LaporanTerima, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(Date_LaporanTerima, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(button_laporan_f2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(button_laporan_terima_WLT, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_laporan_terima_SUB, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 548, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_data_f2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(button_f2_terima_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_input_sesekan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_input_koreksi, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_input_f1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_input_f2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_input_byproduct, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_input_koreksi1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_input_kaki, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_f2_edit_kaki, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(button_f2_setor_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_f2_edit, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(button_f2_delete, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_balen, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Finishing 2", jPanel1);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setPreferredSize(new java.awt.Dimension(1351, 652));

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("No LP :");

        txt_search_LP_setoran.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_LP_setoran.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_LP_setoranKeyPressed(evt);
            }
        });

        button_search_setoran.setBackground(new java.awt.Color(255, 255, 255));
        button_search_setoran.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search_setoran.setText("Search");
        button_search_setoran.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_search_setoranActionPerformed(evt);
            }
        });

        Table_Setoran_harian_f2.setAutoCreateRowSorter(true);
        Table_Setoran_harian_f2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_Setoran_harian_f2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No Kartu", "No LP", "Grade", "Memo", "Berat 0%", "Berat 12%", "Ruang", "Cetak", "Cabut", "Kpg Utuh", "Realisasi Utuh", "Netto Utuh", "Pch.Kpg", "Pch", "Flat.Kpg", "Flat", "Jidun.Utuh", "Jidun.Pch", "Jidun", "Netto Jidun", "S", "H", "R", "B", "Srbt", "Total G.Kaki", "SH 0%", "SP 0%", "Utuh 0%", "F/P 0%", "Jidun 0%", "Total 0%", "SH 12%", "SP 12%", "Utuh 12%", "F/P 12%", "Jidun 12%", "Total 12%"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
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
        Table_Setoran_harian_f2.getTableHeader().setReorderingAllowed(false);
        jScrollPane5.setViewportView(Table_Setoran_harian_f2);

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("Ruangan :");

        ComboBox_ruangan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_ruangan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "A", "B", "C", "D" }));

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel4.setText("Tanggal Setoran :");

        Date_Setoran1.setBackground(new java.awt.Color(255, 255, 255));
        Date_Setoran1.setDate(new Date(new Date().getTime()- (7 * 24 * 60 * 60 * 1000)));
        Date_Setoran1.setDateFormatString("dd MMMM yyyy");
        Date_Setoran1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Total", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 12))); // NOI18N

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel5.setText("Total LP :");

        label_total_lp.setBackground(new java.awt.Color(255, 255, 255));
        label_total_lp.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_lp.setText("XXXX");

        label_kpg_setoran.setBackground(new java.awt.Color(255, 255, 255));
        label_kpg_setoran.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_kpg_setoran.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_kpg_setoran.setText("XXXX");

        jLabel29.setBackground(new java.awt.Color(255, 255, 255));
        jLabel29.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel29.setText("Kpg");

        label_berat_setoran.setBackground(new java.awt.Color(255, 255, 255));
        label_berat_setoran.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_berat_setoran.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_berat_setoran.setText("XXXX");

        jLabel39.setBackground(new java.awt.Color(255, 255, 255));
        jLabel39.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel39.setText("Grams");

        jLabel38.setBackground(new java.awt.Color(255, 255, 255));
        jLabel38.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel38.setText("Grams");

        label_berat_netto_utuh.setBackground(new java.awt.Color(255, 255, 255));
        label_berat_netto_utuh.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_berat_netto_utuh.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_berat_netto_utuh.setText("XXXX");

        jLabel28.setBackground(new java.awt.Color(255, 255, 255));
        jLabel28.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel28.setText("Kpg");

        label_kpg_utuh.setBackground(new java.awt.Color(255, 255, 255));
        label_kpg_utuh.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_kpg_utuh.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_kpg_utuh.setText("XXXX");

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel8.setText("Total Setoran :");

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel11.setText("Total Netto Utuh :");

        jLabel36.setBackground(new java.awt.Color(255, 255, 255));
        jLabel36.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel36.setText("Grams");

        label_berat_flat.setBackground(new java.awt.Color(255, 255, 255));
        label_berat_flat.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_berat_flat.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_berat_flat.setText("XXXX");

        jLabel26.setBackground(new java.awt.Color(255, 255, 255));
        jLabel26.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel26.setText("Kpg");

        label_kpg_flat.setBackground(new java.awt.Color(255, 255, 255));
        label_kpg_flat.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_kpg_flat.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_kpg_flat.setText("XXXX");

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel15.setText("Total Flat / Pecah :");

        label_bk.setBackground(new java.awt.Color(255, 255, 255));
        label_bk.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_bk.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_bk.setText("XXXX");

        jLabel43.setBackground(new java.awt.Color(255, 255, 255));
        jLabel43.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel43.setText("Total Berat 0% :");

        jLabel45.setBackground(new java.awt.Color(255, 255, 255));
        jLabel45.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel45.setText("Grams");

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel16.setText("Total Berat Kaki :");

        jLabel37.setBackground(new java.awt.Color(255, 255, 255));
        jLabel37.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel37.setText("Grams");

        label_total_berat_kaki.setBackground(new java.awt.Color(255, 255, 255));
        label_total_berat_kaki.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_berat_kaki.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_total_berat_kaki.setText("XXXX");

        jLabel68.setBackground(new java.awt.Color(255, 255, 255));
        jLabel68.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel68.setText("Total Berat 12% :");

        label_bk12.setBackground(new java.awt.Color(255, 255, 255));
        label_bk12.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_bk12.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_bk12.setText("XXXX");

        jLabel69.setBackground(new java.awt.Color(255, 255, 255));
        jLabel69.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel69.setText("Grams");

        jLabel31.setBackground(new java.awt.Color(255, 255, 255));
        jLabel31.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel31.setText("Total SP :");

        label_berat_sp.setBackground(new java.awt.Color(255, 255, 255));
        label_berat_sp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_berat_sp.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_berat_sp.setText("XXXX");

        jLabel48.setBackground(new java.awt.Color(255, 255, 255));
        jLabel48.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel48.setText("Grams");

        jLabel32.setBackground(new java.awt.Color(255, 255, 255));
        jLabel32.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel32.setText("Total Netto Jidun :");

        label_berat_netto_jidun.setBackground(new java.awt.Color(255, 255, 255));
        label_berat_netto_jidun.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_berat_netto_jidun.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_berat_netto_jidun.setText("XXXX");

        jLabel50.setBackground(new java.awt.Color(255, 255, 255));
        jLabel50.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel50.setText("Grams");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel68, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(label_bk)
                            .addComponent(label_kpg_setoran)
                            .addComponent(label_kpg_utuh)
                            .addComponent(label_kpg_flat)
                            .addComponent(label_bk12))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel45)
                            .addComponent(jLabel29)
                            .addComponent(jLabel28)
                            .addComponent(jLabel26)
                            .addComponent(jLabel69))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(label_berat_setoran)
                            .addComponent(label_berat_netto_utuh)
                            .addComponent(label_berat_flat)
                            .addComponent(label_total_berat_kaki)
                            .addComponent(label_berat_sp)
                            .addComponent(label_berat_netto_jidun))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel39)
                            .addComponent(jLabel38)
                            .addComponent(jLabel36)
                            .addComponent(jLabel37)
                            .addComponent(jLabel48)
                            .addComponent(jLabel50))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(label_total_lp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_bk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel45, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel68, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_bk12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel69, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_kpg_setoran, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_kpg_utuh, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addComponent(label_berat_setoran, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(label_berat_netto_utuh, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_kpg_flat, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(label_berat_flat, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_berat_sp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel48, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_berat_netto_jidun, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel50, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_berat_kaki, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        table_data_pencabut.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_data_pencabut.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Pegawai", "Nama", "Tanggal", "Kpg", "Grup"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Integer.class, java.lang.String.class
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
        jScrollPane1.setViewportView(table_data_pencabut);

        jLabel46.setBackground(new java.awt.Color(255, 255, 255));
        jLabel46.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel46.setText("Kelompok Cabut");

        button_export_f2_rendemen.setBackground(new java.awt.Color(255, 255, 255));
        button_export_f2_rendemen.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_f2_rendemen.setText("Export to Excel");
        button_export_f2_rendemen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_f2_rendemenActionPerformed(evt);
            }
        });

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel7.setText("Pejuang");

        label_total_pencabut.setBackground(new java.awt.Color(255, 255, 255));
        label_total_pencabut.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_pencabut.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_total_pencabut.setText("0");

        Date_Setoran2.setBackground(new java.awt.Color(255, 255, 255));
        Date_Setoran2.setDate(new Date());
        Date_Setoran2.setDateFormatString("dd MMMM yyyy");
        Date_Setoran2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel9.setText("No Kartu :");

        txt_search_kartu_setoran.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_kartu_setoran.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_kartu_setoranKeyPressed(evt);
            }
        });

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Evaluasi 0%", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 11))); // NOI18N

        jLabel23.setBackground(new java.awt.Color(255, 255, 255));
        jLabel23.setFont(new java.awt.Font("Arial", 2, 11)); // NOI18N
        jLabel23.setText("Susut Proses :");

        jLabel51.setBackground(new java.awt.Color(255, 255, 255));
        jLabel51.setFont(new java.awt.Font("Arial", 2, 11)); // NOI18N
        jLabel51.setText("%");

        label_sh1.setBackground(new java.awt.Color(255, 255, 255));
        label_sh1.setFont(new java.awt.Font("Arial", 2, 11)); // NOI18N
        label_sh1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_sh1.setText("XX,XX");

        jLabel56.setBackground(new java.awt.Color(255, 255, 255));
        jLabel56.setFont(new java.awt.Font("Arial", 2, 11)); // NOI18N
        jLabel56.setText("%");

        jLabel57.setBackground(new java.awt.Color(255, 255, 255));
        jLabel57.setFont(new java.awt.Font("Arial", 2, 11)); // NOI18N
        jLabel57.setText("Total :");

        jLabel54.setBackground(new java.awt.Color(255, 255, 255));
        jLabel54.setFont(new java.awt.Font("Arial", 2, 11)); // NOI18N
        jLabel54.setText("%");

        label_rend_total1.setBackground(new java.awt.Color(255, 255, 255));
        label_rend_total1.setFont(new java.awt.Font("Arial", 2, 11)); // NOI18N
        label_rend_total1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_rend_total1.setText("XX,XX");

        jLabel53.setBackground(new java.awt.Color(255, 255, 255));
        jLabel53.setFont(new java.awt.Font("Arial", 2, 11)); // NOI18N
        jLabel53.setText("%");

        jLabel33.setBackground(new java.awt.Color(255, 255, 255));
        jLabel33.setFont(new java.awt.Font("Arial", 2, 11)); // NOI18N
        jLabel33.setText("Netto Utuh :");

        jLabel55.setBackground(new java.awt.Color(255, 255, 255));
        jLabel55.setFont(new java.awt.Font("Arial", 2, 11)); // NOI18N
        jLabel55.setText("Susut Hilang :");

        label_rend_mk1.setBackground(new java.awt.Color(255, 255, 255));
        label_rend_mk1.setFont(new java.awt.Font("Arial", 2, 11)); // NOI18N
        label_rend_mk1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_rend_mk1.setText("XX,XX");

        label_rend_flat1.setBackground(new java.awt.Color(255, 255, 255));
        label_rend_flat1.setFont(new java.awt.Font("Arial", 2, 11)); // NOI18N
        label_rend_flat1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_rend_flat1.setText("XX,XX");

        jLabel34.setBackground(new java.awt.Color(255, 255, 255));
        jLabel34.setFont(new java.awt.Font("Arial", 2, 11)); // NOI18N
        jLabel34.setText("Flat / Pecah :");

        jLabel52.setBackground(new java.awt.Color(255, 255, 255));
        jLabel52.setFont(new java.awt.Font("Arial", 2, 11)); // NOI18N
        jLabel52.setText("%");

        label_sp1.setBackground(new java.awt.Color(255, 255, 255));
        label_sp1.setFont(new java.awt.Font("Arial", 2, 11)); // NOI18N
        label_sp1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_sp1.setText("XX,XX");

        jLabel35.setBackground(new java.awt.Color(255, 255, 255));
        jLabel35.setFont(new java.awt.Font("Arial", 2, 11)); // NOI18N
        jLabel35.setText("Jidun :");

        label_rend_jidun1.setBackground(new java.awt.Color(255, 255, 255));
        label_rend_jidun1.setFont(new java.awt.Font("Arial", 2, 11)); // NOI18N
        label_rend_jidun1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_rend_jidun1.setText("XX,XX");

        jLabel62.setBackground(new java.awt.Color(255, 255, 255));
        jLabel62.setFont(new java.awt.Font("Arial", 2, 11)); // NOI18N
        jLabel62.setText("%");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel55, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel57, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label_sh1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(label_sp1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(label_rend_mk1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(label_rend_flat1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(label_rend_total1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(label_rend_jidun1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel54)
                    .addComponent(jLabel53)
                    .addComponent(jLabel51)
                    .addComponent(jLabel52)
                    .addComponent(jLabel56)
                    .addComponent(jLabel62))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel55, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_sh1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel52, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_sp1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel53, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_rend_mk1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel54, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_rend_flat1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel56, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_rend_jidun1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel62, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel57, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_rend_total1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel51, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Target Evaluasi 0%", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 11))); // NOI18N

        jLabel64.setBackground(new java.awt.Color(255, 255, 255));
        jLabel64.setFont(new java.awt.Font("Arial", 2, 11)); // NOI18N
        jLabel64.setText("%");

        label_target_sp1.setBackground(new java.awt.Color(255, 255, 255));
        label_target_sp1.setFont(new java.awt.Font("Arial", 2, 11)); // NOI18N
        label_target_sp1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_target_sp1.setText("XX,XX");

        jLabel58.setBackground(new java.awt.Color(255, 255, 255));
        jLabel58.setFont(new java.awt.Font("Arial", 2, 11)); // NOI18N
        jLabel58.setText("Susut Hilang :");

        jLabel61.setBackground(new java.awt.Color(255, 255, 255));
        jLabel61.setFont(new java.awt.Font("Arial", 2, 11)); // NOI18N
        jLabel61.setText("Flat / Pecah :");

        label_target_sh1.setBackground(new java.awt.Color(255, 255, 255));
        label_target_sh1.setFont(new java.awt.Font("Arial", 2, 11)); // NOI18N
        label_target_sh1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_target_sh1.setText("XX,XX");

        jLabel60.setBackground(new java.awt.Color(255, 255, 255));
        jLabel60.setFont(new java.awt.Font("Arial", 2, 11)); // NOI18N
        jLabel60.setText("Netto Utuh :");

        jLabel65.setBackground(new java.awt.Color(255, 255, 255));
        jLabel65.setFont(new java.awt.Font("Arial", 2, 11)); // NOI18N
        jLabel65.setText("%");

        label_target_utuh1.setBackground(new java.awt.Color(255, 255, 255));
        label_target_utuh1.setFont(new java.awt.Font("Arial", 2, 11)); // NOI18N
        label_target_utuh1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_target_utuh1.setText("XX,XX");

        jLabel66.setBackground(new java.awt.Color(255, 255, 255));
        jLabel66.setFont(new java.awt.Font("Arial", 2, 11)); // NOI18N
        jLabel66.setText("%");

        label_target_flat1.setBackground(new java.awt.Color(255, 255, 255));
        label_target_flat1.setFont(new java.awt.Font("Arial", 2, 11)); // NOI18N
        label_target_flat1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_target_flat1.setText("XX,XX");

        jLabel63.setBackground(new java.awt.Color(255, 255, 255));
        jLabel63.setFont(new java.awt.Font("Arial", 2, 11)); // NOI18N
        jLabel63.setText("%");

        jLabel59.setBackground(new java.awt.Color(255, 255, 255));
        jLabel59.setFont(new java.awt.Font("Arial", 2, 11)); // NOI18N
        jLabel59.setText("Susut Proses :");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel58, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel59, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel60, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel61, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label_target_utuh1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(label_target_flat1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(label_target_sp1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(label_target_sh1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel66)
                    .addComponent(jLabel64)
                    .addComponent(jLabel63)
                    .addComponent(jLabel65))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel58, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_target_sh1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel64, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel59, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_target_sp1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel65, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel60, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_target_utuh1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel63, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel61, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_target_flat1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel66, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));
        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Evaluasi 12%", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 11))); // NOI18N

        label_rend_flat2.setBackground(new java.awt.Color(255, 255, 255));
        label_rend_flat2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_rend_flat2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_rend_flat2.setText("XX,XX");

        label_sh2.setBackground(new java.awt.Color(255, 255, 255));
        label_sh2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_sh2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_sh2.setText("XX,XX");

        jLabel96.setBackground(new java.awt.Color(255, 255, 255));
        jLabel96.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel96.setText("%");

        jLabel94.setBackground(new java.awt.Color(255, 255, 255));
        jLabel94.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel94.setText("%");

        jLabel93.setBackground(new java.awt.Color(255, 255, 255));
        jLabel93.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel93.setText("%");

        jLabel95.setBackground(new java.awt.Color(255, 255, 255));
        jLabel95.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel95.setText("%");

        jLabel92.setBackground(new java.awt.Color(255, 255, 255));
        jLabel92.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel92.setText("Total :");

        label_rend_mk2.setBackground(new java.awt.Color(255, 255, 255));
        label_rend_mk2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_rend_mk2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_rend_mk2.setText("XX,XX");

        label_rend_total2.setBackground(new java.awt.Color(255, 255, 255));
        label_rend_total2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_rend_total2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_rend_total2.setText("XX,XX");

        jLabel91.setBackground(new java.awt.Color(255, 255, 255));
        jLabel91.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel91.setText("Flat / Pecah :");

        jLabel88.setBackground(new java.awt.Color(255, 255, 255));
        jLabel88.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel88.setText("Susut Hilang :");

        jLabel90.setBackground(new java.awt.Color(255, 255, 255));
        jLabel90.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel90.setText("Netto Utuh :");

        jLabel89.setBackground(new java.awt.Color(255, 255, 255));
        jLabel89.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel89.setText("Susut Proses :");

        label_sp2.setBackground(new java.awt.Color(255, 255, 255));
        label_sp2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_sp2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_sp2.setText("XX,XX");

        jLabel97.setBackground(new java.awt.Color(255, 255, 255));
        jLabel97.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel97.setText("%");

        jLabel98.setBackground(new java.awt.Color(255, 255, 255));
        jLabel98.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel98.setText("Jidun :");

        label_rend_jidun2.setBackground(new java.awt.Color(255, 255, 255));
        label_rend_jidun2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_rend_jidun2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_rend_jidun2.setText("XX,XX");

        jLabel99.setBackground(new java.awt.Color(255, 255, 255));
        jLabel99.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel99.setText("%");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel88, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel89, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel90, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel91, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel92, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel98, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label_sh2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(label_sp2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(label_rend_mk2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(label_rend_flat2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(label_rend_total2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(label_rend_jidun2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel95)
                    .addComponent(jLabel94)
                    .addComponent(jLabel97)
                    .addComponent(jLabel93)
                    .addComponent(jLabel96)
                    .addComponent(jLabel99))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel88, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_sh2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel93, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel89, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_sp2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel94, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel90, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_rend_mk2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel95, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel91, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_rend_flat2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel96, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel98, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_rend_jidun2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel99, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel92, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_rend_total2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel97, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));
        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Target Evaluasi 12%", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 11))); // NOI18N

        jLabel78.setBackground(new java.awt.Color(255, 255, 255));
        jLabel78.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel78.setText("%");

        label_target_sp2.setBackground(new java.awt.Color(255, 255, 255));
        label_target_sp2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_target_sp2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_target_sp2.setText("XX,XX");

        jLabel79.setBackground(new java.awt.Color(255, 255, 255));
        jLabel79.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel79.setText("Susut Hilang :");

        jLabel80.setBackground(new java.awt.Color(255, 255, 255));
        jLabel80.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel80.setText("Flat / Pecah :");

        label_target_sh2.setBackground(new java.awt.Color(255, 255, 255));
        label_target_sh2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_target_sh2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_target_sh2.setText("XX,XX");

        jLabel81.setBackground(new java.awt.Color(255, 255, 255));
        jLabel81.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel81.setText("Netto Utuh :");

        jLabel82.setBackground(new java.awt.Color(255, 255, 255));
        jLabel82.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel82.setText("%");

        label_target_utuh2.setBackground(new java.awt.Color(255, 255, 255));
        label_target_utuh2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_target_utuh2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_target_utuh2.setText("XX,XX");

        jLabel83.setBackground(new java.awt.Color(255, 255, 255));
        jLabel83.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel83.setText("%");

        label_target_flat2.setBackground(new java.awt.Color(255, 255, 255));
        label_target_flat2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_target_flat2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_target_flat2.setText("XX,XX");

        jLabel86.setBackground(new java.awt.Color(255, 255, 255));
        jLabel86.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel86.setText("%");

        jLabel87.setBackground(new java.awt.Color(255, 255, 255));
        jLabel87.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel87.setText("Susut Proses :");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel79, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel87, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel81, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel80, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label_target_utuh2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(label_target_flat2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(label_target_sp2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(label_target_sh2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel83)
                    .addComponent(jLabel78)
                    .addComponent(jLabel86)
                    .addComponent(jLabel82))
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel79, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_target_sh2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel78, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel87, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_target_sp2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel82, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel81, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_target_utuh2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel86, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel80, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_target_flat2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel83, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel10.setText("Bentuk Grade :");

        ComboBox_searchBentuk.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_searchBentuk.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Mangkok", "Oval", "Segitiga", "Flat/Pecah", "-" }));

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel14.setText("Memo :");

        txt_search_Memo_Setoran.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_Memo_Setoran.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_Memo_SetoranKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_kartu_setoran, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_LP_setoran, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_Memo_Setoran, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_ruangan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_searchBentuk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_Setoran1, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_Setoran2, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_search_setoran)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_export_f2_rendemen))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(11, 11, 11)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel46)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(label_total_pencabut, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel7))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txt_search_kartu_setoran, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txt_search_LP_setoran, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_search_Memo_Setoran, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(Date_Setoran1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(button_search_setoran, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_export_f2_rendemen, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(Date_Setoran2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(ComboBox_searchBentuk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(ComboBox_ruangan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 333, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel46)
                            .addComponent(jLabel7)
                            .addComponent(label_total_pencabut))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addComponent(jPanel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Laporan Setoran Harian", jPanel2);

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel18.setText("Tutupan :");

        txt_search_evaluasi_tutupan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_evaluasi_tutupan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_evaluasi_tutupanKeyPressed(evt);
            }
        });

        button_refresh_evaluasiMLEM.setBackground(new java.awt.Color(255, 255, 255));
        button_refresh_evaluasiMLEM.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_refresh_evaluasiMLEM.setText("Search");
        button_refresh_evaluasiMLEM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refresh_evaluasiMLEMActionPerformed(evt);
            }
        });

        Table_evaluasi_MLEM.setAutoCreateRowSorter(true);
        Table_evaluasi_MLEM.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_evaluasi_MLEM.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tutupan", "Ruangan", "Tgl selesai tutupan", "Jumlah LP", "Total MLEM", "Total Gram", "% MLEM"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
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
        Table_evaluasi_MLEM.getTableHeader().setReorderingAllowed(false);
        jScrollPane7.setViewportView(Table_evaluasi_MLEM);

        jLabel19.setBackground(new java.awt.Color(255, 255, 255));
        jLabel19.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel19.setText("Total Data :");

        label_total_data_tutupan.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_tutupan.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_data_tutupan.setText("XXXX");

        jLabel21.setBackground(new java.awt.Color(255, 255, 255));
        jLabel21.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel21.setText("Tanggal Selesai Tutupan :");

        DateTutupan1.setBackground(new java.awt.Color(255, 255, 255));
        DateTutupan1.setDateFormatString("dd MMM yyyy");

        DateTutupan2.setBackground(new java.awt.Color(255, 255, 255));
        DateTutupan2.setDateFormatString("dd MMM yyyy");

        jLabel20.setBackground(new java.awt.Color(255, 255, 255));
        jLabel20.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel20.setText("Ruangan :");

        ComboBox_evaluasi_ruanganMLEM.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        jLabel22.setBackground(new java.awt.Color(255, 255, 255));
        jLabel22.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel22.setText("Total MLEM :");

        label_total_mlem.setBackground(new java.awt.Color(255, 255, 255));
        label_total_mlem.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_mlem.setText("XXXX");

        jLabel25.setBackground(new java.awt.Color(255, 255, 255));
        jLabel25.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel25.setText("Rerata MLEM :");

        Table_evaluasi_MLEM_summary.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_evaluasi_MLEM_summary.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Ruang", "Total MLEM", "%"
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
        Table_evaluasi_MLEM_summary.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(Table_evaluasi_MLEM_summary);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel19)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_data_tutupan)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel22)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_mlem))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel18)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_evaluasi_tutupan, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel20)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_evaluasi_ruanganMLEM, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel21)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(DateTutupan1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(DateTutupan2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_refresh_evaluasiMLEM)))
                        .addGap(0, 382, Short.MAX_VALUE))
                    .addComponent(jScrollPane7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_search_evaluasi_tutupan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_refresh_evaluasiMLEM, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DateTutupan1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DateTutupan2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_evaluasi_ruanganMLEM, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_mlem, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_data_tutupan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 574, Short.MAX_VALUE)
                    .addComponent(jScrollPane7))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Evaluasi MLEM", jPanel5);

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel12.setText("Laporan Produksi :");

        txt_search_lp_balen.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_lp_balen.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_lp_balenKeyPressed(evt);
            }
        });

        button_search_lp_balen.setBackground(new java.awt.Color(255, 255, 255));
        button_search_lp_balen.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search_lp_balen.setText("Search");
        button_search_lp_balen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_search_lp_balenActionPerformed(evt);
            }
        });

        Table_balen.setAutoCreateRowSorter(true);
        Table_balen.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_balen.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No LP", "Tanggal Balen", "Keping Awal", "Gram Awal", "Keping Akhir", "Gram Akhir", "Pekerja", "Tanggal Selesai", "Keterangan"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Float.class, java.lang.Integer.class, java.lang.Float.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Table_balen.getTableHeader().setReorderingAllowed(false);
        jScrollPane6.setViewportView(Table_balen);
        if (Table_balen.getColumnModel().getColumnCount() > 0) {
            Table_balen.getColumnModel().getColumn(2).setHeaderValue("Keping Awal");
            Table_balen.getColumnModel().getColumn(4).setHeaderValue("Keping Akhir");
        }

        button_selesai_balen.setBackground(new java.awt.Color(255, 255, 255));
        button_selesai_balen.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_selesai_balen.setText("Selesai");
        button_selesai_balen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_selesai_balenActionPerformed(evt);
            }
        });

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel13.setText("Total LP :");

        jLabel44.setBackground(new java.awt.Color(255, 255, 255));
        jLabel44.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel44.setText("Total Awal :");

        jLabel70.setBackground(new java.awt.Color(255, 255, 255));
        jLabel70.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel70.setText("Total Akhir :");

        label_total_kpg_akhir.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kpg_akhir.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_kpg_akhir.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_total_kpg_akhir.setText("XXXX");

        jLabel71.setBackground(new java.awt.Color(255, 255, 255));
        jLabel71.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel71.setText("Kpg");

        jLabel47.setBackground(new java.awt.Color(255, 255, 255));
        jLabel47.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel47.setText("Kpg");

        label_total_kpg_awal.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kpg_awal.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_kpg_awal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_total_kpg_awal.setText("XXXX");

        label_total_lp_balen.setBackground(new java.awt.Color(255, 255, 255));
        label_total_lp_balen.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_lp_balen.setText("XXXX");

        jLabel72.setBackground(new java.awt.Color(255, 255, 255));
        jLabel72.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel72.setText("Grams");

        label_total_gram_akhir.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_akhir.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_gram_akhir.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_total_gram_akhir.setText("XXXX");

        label_total_gram_awal.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_awal.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_gram_awal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_total_gram_awal.setText("XXXX");

        jLabel49.setBackground(new java.awt.Color(255, 255, 255));
        jLabel49.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel49.setText("Grams");

        button_delete.setBackground(new java.awt.Color(255, 255, 255));
        button_delete.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_delete.setText("Delete Data");
        button_delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_deleteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1331, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel70, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(label_total_kpg_awal)
                                            .addComponent(label_total_kpg_akhir))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel47)
                                            .addComponent(jLabel71))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(label_total_gram_awal)
                                            .addComponent(label_total_gram_akhir))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel49)
                                            .addComponent(jLabel72)))
                                    .addComponent(label_total_lp_balen)))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_lp_balen, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_search_lp_balen)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_selesai_balen)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_delete)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_search_lp_balen, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_search_lp_balen, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_selesai_balen, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_delete, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 522, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_lp_balen, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_kpg_awal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel47, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel70, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_kpg_akhir, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel71, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_total_gram_awal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel49, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_total_gram_akhir, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel72, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Daftar LP Balen", jPanel4);

        jPanel_data_LP_suwir.setBackground(new java.awt.Color(255, 255, 255));

        jLabel40.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel40.setText("No LP Suwir :");

        txt_search_lpsuwir.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_lpsuwir.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_lpsuwirKeyPressed(evt);
            }
        });

        jLabel41.setBackground(new java.awt.Color(255, 255, 255));
        jLabel41.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel41.setText("Date Filter :");

        Date_Search_LPsuwir_1.setBackground(new java.awt.Color(255, 255, 255));
        Date_Search_LPsuwir_1.setToolTipText("");
        Date_Search_LPsuwir_1.setDate(new Date(new Date().getTime()-(14 * 24 * 60 * 60 * 1000)));
        Date_Search_LPsuwir_1.setDateFormatString("dd MMMM yyyy");
        Date_Search_LPsuwir_1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Date_Search_LPsuwir_1.setMinSelectableDate(new java.util.Date(1420048915000L));

        jLabel42.setBackground(new java.awt.Color(255, 255, 255));
        jLabel42.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel42.setText("Sampai");

        Date_Search_LPsuwir_2.setBackground(new java.awt.Color(255, 255, 255));
        Date_Search_LPsuwir_2.setDate(new Date());
        Date_Search_LPsuwir_2.setDateFormatString("dd MMMM yyyy");
        Date_Search_LPsuwir_2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        button_search_lp.setBackground(new java.awt.Color(255, 255, 255));
        button_search_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search_lp.setText("Search");
        button_search_lp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_search_lpActionPerformed(evt);
            }
        });

        table_data_LPsuwir.setAutoCreateRowSorter(true);
        table_data_LPsuwir.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_data_LPsuwir.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No. LP Suwir", "Tanggal LP", "Keping", "Gram", "Gr Akhir", "Jumlah Box", "Keluar F2", "Keluar ReProses", "Stok"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Float.class, java.lang.Float.class, java.lang.Integer.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table_data_LPsuwir.getTableHeader().setReorderingAllowed(false);
        jScrollPane10.setViewportView(table_data_LPsuwir);

        jLabel67.setBackground(new java.awt.Color(255, 255, 255));
        jLabel67.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel67.setText("Data LP Suwir");

        jLabel73.setBackground(new java.awt.Color(255, 255, 255));
        jLabel73.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel73.setText("Asal Box LP Suwir");

        table_asalBox_lpSuwir.setAutoCreateRowSorter(true);
        table_asalBox_lpSuwir.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_asalBox_lpSuwir.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No Box", "Tgl Box", "Grade", "Keping", "Gram", "No Tutupan"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.Integer.class, java.lang.Float.class, java.lang.String.class
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
        table_asalBox_lpSuwir.setRowSelectionAllowed(false);
        table_asalBox_lpSuwir.getTableHeader().setReorderingAllowed(false);
        jScrollPane11.setViewportView(table_asalBox_lpSuwir);

        jLabel74.setBackground(new java.awt.Color(255, 255, 255));
        jLabel74.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel74.setText("Total Data :");

        label_total_asalBox_lpSuwir.setBackground(new java.awt.Color(255, 255, 255));
        label_total_asalBox_lpSuwir.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_asalBox_lpSuwir.setText("0");

        jLabel75.setBackground(new java.awt.Color(255, 255, 255));
        jLabel75.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel75.setText("Keping :");

        label_total_keping_asalBox_lpsuwir.setBackground(new java.awt.Color(255, 255, 255));
        label_total_keping_asalBox_lpsuwir.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_keping_asalBox_lpsuwir.setText("0");

        jLabel76.setBackground(new java.awt.Color(255, 255, 255));
        jLabel76.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel76.setText("Gram :");

        label_total_gram_asalBox_lpsuwir.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_asalBox_lpsuwir.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_gram_asalBox_lpsuwir.setText("0");

        jLabel77.setBackground(new java.awt.Color(255, 255, 255));
        jLabel77.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel77.setText("Total Data :");

        label_total_lpsuwir.setBackground(new java.awt.Color(255, 255, 255));
        label_total_lpsuwir.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_lpsuwir.setText("0");

        label_asalBox_lpSuwir.setBackground(new java.awt.Color(255, 255, 255));
        label_asalBox_lpSuwir.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_asalBox_lpSuwir.setText("(NO LP SUWIR)");

        button_export_LPSuwir.setBackground(new java.awt.Color(255, 255, 255));
        button_export_LPSuwir.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_LPSuwir.setText("Export");
        button_export_LPSuwir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_LPSuwirActionPerformed(evt);
            }
        });

        button_export_AsalBox.setBackground(new java.awt.Color(255, 255, 255));
        button_export_AsalBox.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_AsalBox.setText("Export");
        button_export_AsalBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_AsalBoxActionPerformed(evt);
            }
        });

        jPanel10.setBackground(new java.awt.Color(255, 255, 255));

        label_LaporanProduksi_lpSuwir.setBackground(new java.awt.Color(255, 255, 255));
        label_LaporanProduksi_lpSuwir.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_LaporanProduksi_lpSuwir.setText("(NO LP SUWIR)");

        jLabel84.setBackground(new java.awt.Color(255, 255, 255));
        jLabel84.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel84.setText("Total Gram kaki :");

        jLabel85.setBackground(new java.awt.Color(255, 255, 255));
        jLabel85.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel85.setText("Total Data :");

        label_total_LaporanProduksi_lpsuwir.setBackground(new java.awt.Color(255, 255, 255));
        label_total_LaporanProduksi_lpsuwir.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_LaporanProduksi_lpsuwir.setText("0");

        jLabel100.setBackground(new java.awt.Color(255, 255, 255));
        jLabel100.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel100.setText("Laporan Produksi yang menggunakan LP Suwir");

        button_export_LP_F2.setBackground(new java.awt.Color(255, 255, 255));
        button_export_LP_F2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_LP_F2.setText("Export");
        button_export_LP_F2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_LP_F2ActionPerformed(evt);
            }
        });

        table_LaporanProduksi_LPSuwir.setAutoCreateRowSorter(true);
        table_LaporanProduksi_LPSuwir.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_LaporanProduksi_LPSuwir.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No LP", "LP kaki 1", "Gram 1", "LP kaki 2", "Gram 2"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.String.class, java.lang.Float.class
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
        table_LaporanProduksi_LPSuwir.setRowSelectionAllowed(false);
        table_LaporanProduksi_LPSuwir.getTableHeader().setReorderingAllowed(false);
        jScrollPane12.setViewportView(table_LaporanProduksi_LPSuwir);

        label_total_gram_LaporanProduksi_lpsuwir.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_LaporanProduksi_lpsuwir.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_gram_LaporanProduksi_lpsuwir.setText("0");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel100)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_LaporanProduksi_lpSuwir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_export_LP_F2))
                    .addComponent(jScrollPane12, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel85)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_LaporanProduksi_lpsuwir)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel84)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gram_LaporanProduksi_lpsuwir)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel100, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_LaporanProduksi_lpSuwir, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export_LP_F2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane12, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel85, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_LaporanProduksi_lpsuwir, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel84, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_gram_LaporanProduksi_lpsuwir, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jTabbedPane2.addTab("F2", jPanel10);

        jPanel11.setBackground(new java.awt.Color(255, 255, 255));

        jLabel101.setBackground(new java.awt.Color(255, 255, 255));
        jLabel101.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel101.setText("Total Gram kaki :");

        table_BoxReprosesi_LPSuwir.setAutoCreateRowSorter(true);
        table_BoxReprosesi_LPSuwir.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_BoxReprosesi_LPSuwir.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No Reproses", "No Box", "Tgl Reproses", "No LP Suwir", "Gram"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.Float.class
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
        table_BoxReprosesi_LPSuwir.setRowSelectionAllowed(false);
        table_BoxReprosesi_LPSuwir.getTableHeader().setReorderingAllowed(false);
        jScrollPane13.setViewportView(table_BoxReprosesi_LPSuwir);

        label_total_gram_BoxRepracking_lpsuwir.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_BoxRepracking_lpsuwir.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_gram_BoxRepracking_lpsuwir.setText("0");

        label_BoxReproses_lpSuwir1.setBackground(new java.awt.Color(255, 255, 255));
        label_BoxReproses_lpSuwir1.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_BoxReproses_lpSuwir1.setText("(NO LP SUWIR)");

        button_export_BoxReproses.setBackground(new java.awt.Color(255, 255, 255));
        button_export_BoxReproses.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_BoxReproses.setText("Export");
        button_export_BoxReproses.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_BoxReprosesActionPerformed(evt);
            }
        });

        jLabel102.setBackground(new java.awt.Color(255, 255, 255));
        jLabel102.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel102.setText("Total Data :");

        label_total_BoxRepacking_lpsuwir.setBackground(new java.awt.Color(255, 255, 255));
        label_total_BoxRepacking_lpsuwir.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_BoxRepacking_lpsuwir.setText("0");

        jLabel103.setBackground(new java.awt.Color(255, 255, 255));
        jLabel103.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel103.setText("Box Re-Proses menggunakan LP susur perut");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jLabel103)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_BoxReproses_lpSuwir1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_export_BoxReproses))
                    .addComponent(jScrollPane13, javax.swing.GroupLayout.DEFAULT_SIZE, 425, Short.MAX_VALUE)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jLabel102)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_BoxRepacking_lpsuwir)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel101)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gram_BoxRepracking_lpsuwir)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel103, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_BoxReproses_lpSuwir1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export_BoxReproses, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane13, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel102, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_BoxRepacking_lpsuwir, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel101, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_gram_BoxRepracking_lpsuwir, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jTabbedPane2.addTab("Box Reproses", jPanel11);

        jLabel104.setBackground(new java.awt.Color(255, 255, 255));
        jLabel104.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel104.setText("Total Stok :");

        label_total_stok.setBackground(new java.awt.Color(255, 255, 255));
        label_total_stok.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_stok.setText("0");

        label_total_keluar_f2.setBackground(new java.awt.Color(255, 255, 255));
        label_total_keluar_f2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_keluar_f2.setText("0");

        jLabel105.setBackground(new java.awt.Color(255, 255, 255));
        jLabel105.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel105.setText("Total Keluar F2 :");

        button_Print_LP_SWR.setBackground(new java.awt.Color(255, 255, 255));
        button_Print_LP_SWR.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_Print_LP_SWR.setText("Print");
        button_Print_LP_SWR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_Print_LP_SWRActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_data_LP_suwirLayout = new javax.swing.GroupLayout(jPanel_data_LP_suwir);
        jPanel_data_LP_suwir.setLayout(jPanel_data_LP_suwirLayout);
        jPanel_data_LP_suwirLayout.setHorizontalGroup(
            jPanel_data_LP_suwirLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_data_LP_suwirLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_data_LP_suwirLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_data_LP_suwirLayout.createSequentialGroup()
                        .addComponent(jLabel40)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_lpsuwir, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel41)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_Search_LPsuwir_1, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel42)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_Search_LPsuwir_2, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_search_lp))
                    .addGroup(jPanel_data_LP_suwirLayout.createSequentialGroup()
                        .addGroup(jPanel_data_LP_suwirLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel_data_LP_suwirLayout.createSequentialGroup()
                                .addComponent(jLabel77)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_lpsuwir)
                                .addGap(21, 21, 21)
                                .addComponent(jLabel104)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_stok)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel105)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_keluar_f2))
                            .addGroup(jPanel_data_LP_suwirLayout.createSequentialGroup()
                                .addComponent(jLabel67)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(button_Print_LP_SWR)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_export_LPSuwir))
                            .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 871, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel_data_LP_suwirLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addGroup(jPanel_data_LP_suwirLayout.createSequentialGroup()
                                .addComponent(jLabel73)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_asalBox_lpSuwir)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(button_export_AsalBox))
                            .addGroup(jPanel_data_LP_suwirLayout.createSequentialGroup()
                                .addComponent(jLabel74)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_asalBox_lpSuwir)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel75)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_keping_asalBox_lpsuwir)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel76)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_gram_asalBox_lpsuwir)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jTabbedPane2))))
                .addContainerGap())
        );
        jPanel_data_LP_suwirLayout.setVerticalGroup(
            jPanel_data_LP_suwirLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_data_LP_suwirLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_data_LP_suwirLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_Search_LPsuwir_1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel42, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_Search_LPsuwir_2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_search_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_lpsuwir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_data_LP_suwirLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_data_LP_suwirLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel73, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_asalBox_lpSuwir, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_export_LPSuwir, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_export_AsalBox, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_Print_LP_SWR, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel67, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_data_LP_suwirLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_data_LP_suwirLayout.createSequentialGroup()
                        .addComponent(jScrollPane10, javax.swing.GroupLayout.DEFAULT_SIZE, 548, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_data_LP_suwirLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_data_LP_suwirLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel105, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(label_total_keluar_f2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel_data_LP_suwirLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel104, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(label_total_stok, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel_data_LP_suwirLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel77, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(label_total_lpsuwir, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel_data_LP_suwirLayout.createSequentialGroup()
                        .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_data_LP_suwirLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel74, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_asalBox_lpSuwir, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel75, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_keping_asalBox_lpsuwir, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel76, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_gram_asalBox_lpsuwir, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 282, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Data LP Suwir", jPanel_data_LP_suwir);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 679, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txt_search_no_lpKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_no_lpKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_F2();
        }
    }//GEN-LAST:event_txt_search_no_lpKeyPressed

    private void button_search_f2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_f2ActionPerformed
        // TODO add your handling code here:
        refreshTable_F2();
    }//GEN-LAST:event_button_search_f2ActionPerformed

    private void button_f2_terima_lpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_f2_terima_lpActionPerformed
        // TODO add your handling code here:
        int i = Table_Data_f2.getSelectedRow();
        if (i > -1) {
            String lp = Table_Data_f2.getValueAt(i, 0).toString();
            JDialog_Terima_LP_F2 terima_lp = new JDialog_Terima_LP_F2(new javax.swing.JFrame(), true, lp);
            terima_lp.pack();
            terima_lp.setLocationRelativeTo(this);
            terima_lp.setVisible(true);
            terima_lp.setEnabled(true);
        } else {
            JDialog_Terima_LP_F2 terima_lp = new JDialog_Terima_LP_F2(new javax.swing.JFrame(), true, null);
            terima_lp.pack();
            terima_lp.setLocationRelativeTo(this);
            terima_lp.setVisible(true);
            terima_lp.setEnabled(true);
        }
    }//GEN-LAST:event_button_f2_terima_lpActionPerformed

    private void button_f2_setor_lpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_f2_setor_lpActionPerformed
        // TODO add your handling code here:
        int j = Table_Data_f2.getSelectedRow();
        if (j == -1) {
            JOptionPane.showMessageDialog(this, "Anda belum memilih LP yang akan di setorkan !");
        } else {
            JDialog_Setor_LP_F2 setor_lp = new JDialog_Setor_LP_F2(new javax.swing.JFrame(), true, Table_Data_f2.getValueAt(j, 6));
            setor_lp.pack();
            setor_lp.setLocationRelativeTo(this);
            setor_lp.setVisible(true);
            setor_lp.setEnabled(true);
            refreshTable_F2();
//            refreshTable_Setoran();
        }
    }//GEN-LAST:event_button_f2_setor_lpActionPerformed

    private void button_f2_editActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_f2_editActionPerformed
        // TODO add your handling code here:
        try {
            int j = Table_Data_f2.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih data yang akan di edit !");
            } else {
                JDialog_Edit_Data_F2 edit_f2 = new JDialog_Edit_Data_F2(new javax.swing.JFrame(), true);
                edit_f2.pack();
                edit_f2.setLocationRelativeTo(this);
                edit_f2.setVisible(true);
                edit_f2.setEnabled(true);
                refreshTable_F2();
                JOptionPane.showMessageDialog(this, "Harap memberikan info edit ke bagian keuangan, karena data akan mempengaruhi perhitungan upah !!");
            }
//            refreshTable_Setoran();
        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_Finishing2.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_f2_editActionPerformed

    private void button_f2_deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_f2_deleteActionPerformed
        // TODO add your handling code here:
        try {
            int j = Table_Data_f2.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Please Select Row Data that you want to Delete !");
            } else {
                sql = "SELECT `no_laporan_produksi` FROM `tb_lab_laporan_produksi` WHERE `tb_lab_laporan_produksi`.`no_laporan_produksi` = '" + Table_Data_f2.getValueAt(j, 0) + "'";
                rs = Utility.db.getStatement().executeQuery(sql);
                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "Maaf tidak bisa hapus No LP ini karena sudah masuk QC, mohon hapus data QC terlebih dahulu!");
                } else {
                    int dialogResult = JOptionPane.showConfirmDialog(this, "Yakin hapus data ini?", "Warning", 0);
                    if (dialogResult == JOptionPane.YES_OPTION) {
                        // delete code here
                        String Query = "DELETE FROM `tb_finishing_2` WHERE `tb_finishing_2`.`no_laporan_produksi` = '" + Table_Data_f2.getValueAt(j, 0) + "'";
                        if ((Utility.db.getStatement().executeUpdate(Query)) == 1) {
                            JOptionPane.showMessageDialog(this, "data deleted Successfully");
                        } else {
                            JOptionPane.showMessageDialog(this, "data not deleted");
                        }
                        refreshTable_F2();
//                    refreshTable_Setoran();
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_Finishing2.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_f2_deleteActionPerformed

    private void txt_search_LP_setoranKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_LP_setoranKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_Setoran();
        }
    }//GEN-LAST:event_txt_search_LP_setoranKeyPressed

    private void button_search_setoranActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_setoranActionPerformed
        // TODO add your handling code here:
        refreshTable_Setoran();
    }//GEN-LAST:event_button_search_setoranActionPerformed

    private void jTabbedPane1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPane1StateChanged
        // TODO add your handling code here:
//        System.out.println("a");
//        Table_Data_f2.clearSelection();
//        Table_Setoran_harian_f2.clearSelection();
//        refreshTable_F2();
//        refreshTable_Setoran();
    }//GEN-LAST:event_jTabbedPane1StateChanged

    private void button_export_f2_rendemenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_f2_rendemenActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Table_Setoran_harian_f2.getModel();
        ExportToExcel.writeToExcel(model, jPanel2);
    }//GEN-LAST:event_button_export_f2_rendemenActionPerformed

    private void txt_search_kartu_setoranKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_kartu_setoranKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_Setoran();
        }
    }//GEN-LAST:event_txt_search_kartu_setoranKeyPressed

    private void button_export_f2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_f2ActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Table_Data_f2.getModel();
        ExportToExcel.writeToExcel(model, jPanel1);
    }//GEN-LAST:event_button_export_f2ActionPerformed

    private void button_input_koreksiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_input_koreksiActionPerformed
        // TODO add your handling code here:
        int i = Table_Data_f2.getSelectedRow();
        if (i > -1) {
            if (Table_Data_f2.getValueAt(i, 6) == null) {
                String lp = Table_Data_f2.getValueAt(i, 0).toString();
                JDialog_Input_Koreksi input_koreksi = new JDialog_Input_Koreksi(new javax.swing.JFrame(), true, lp);
                input_koreksi.pack();
                input_koreksi.setLocationRelativeTo(this);
                input_koreksi.setVisible(true);
                input_koreksi.setEnabled(true);
            } else {
                JOptionPane.showMessageDialog(this, "LP belum di terima");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Silahkan klik pada LP yang akan masuk");
        }
    }//GEN-LAST:event_button_input_koreksiActionPerformed

    private void button_input_f1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_input_f1ActionPerformed
        // TODO add your handling code here:
        int i = Table_Data_f2.getSelectedRow();
        if (i > -1) {
            String lp = Table_Data_f2.getValueAt(i, 0).toString();
            JDialog_Input_F1 input = new JDialog_Input_F1(new javax.swing.JFrame(), true, lp);
            input.pack();
            input.setLocationRelativeTo(this);
            input.setVisible(true);
            input.setEnabled(true);
        } else {
            JOptionPane.showMessageDialog(this, "Silahkan klik pada LP yang akan masuk");
        }
    }//GEN-LAST:event_button_input_f1ActionPerformed

    private void button_input_f2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_input_f2ActionPerformed
        // TODO add your handling code here:
        int i = Table_Data_f2.getSelectedRow();
        if (i > -1) {
            String lp = Table_Data_f2.getValueAt(i, 0).toString();
            JDialog_Input_F2 input = new JDialog_Input_F2(new javax.swing.JFrame(), true, lp);
            input.pack();
            input.setLocationRelativeTo(this);
            input.setVisible(true);
            input.setEnabled(true);
        } else {
            JOptionPane.showMessageDialog(this, "Silahkan klik pada LP yang akan masuk");
        }
    }//GEN-LAST:event_button_input_f2ActionPerformed

    private void button_input_byproductActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_input_byproductActionPerformed
        // TODO add your handling code here:
        JDialog_Input_ByProduct_F2_v2 input_bp = new JDialog_Input_ByProduct_F2_v2(new javax.swing.JFrame(), true);
        input_bp.pack();
        input_bp.setLocationRelativeTo(this);
        input_bp.setVisible(true);
        input_bp.setEnabled(true);
    }//GEN-LAST:event_button_input_byproductActionPerformed

    private void button_input_koreksi1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_input_koreksi1ActionPerformed
        // TODO add your handling code here:
        JDialog_Input_Koreksi_v21 input = new JDialog_Input_Koreksi_v21(new javax.swing.JFrame(), true);
        input.pack();
        input.setLocationRelativeTo(this);
        input.setVisible(true);
        input.setEnabled(true);
    }//GEN-LAST:event_button_input_koreksi1ActionPerformed

    private void button_balenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_balenActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_button_balenActionPerformed

    private void txt_search_lp_balenKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_lp_balenKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_Setoran();
        }
    }//GEN-LAST:event_txt_search_lp_balenKeyPressed

    private void button_search_lp_balenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_lp_balenActionPerformed
        // TODO add your handling code here:
        refresh_TabelBalen();
    }//GEN-LAST:event_button_search_lp_balenActionPerformed

    private void button_selesai_balenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_selesai_balenActionPerformed
        // TODO add your handling code here:
        int j = Table_balen.getSelectedRow();
        if (j == -1) {
            JOptionPane.showMessageDialog(this, "Silahkan pilih LP yang akan selesai");
        } else {
            String no_lp = Table_balen.getValueAt(j, 0).toString();
            String tgl_balen = Table_balen.getValueAt(j, 1).toString();
            String kpg_awal = Table_balen.getValueAt(j, 2).toString();
            String gram_awal = Table_balen.getValueAt(j, 3).toString();

            JDialog_Selesai_Balen input = new JDialog_Selesai_Balen(new javax.swing.JFrame(), true, no_lp, tgl_balen, kpg_awal, gram_awal);
            input.pack();
            input.setLocationRelativeTo(this);
            input.setVisible(true);
            input.setEnabled(true);
        }

    }//GEN-LAST:event_button_selesai_balenActionPerformed

    private void button_deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_deleteActionPerformed
        // TODO add your handling code here:
        try {
            int j = Table_balen.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Please Select Row Data that you want to Delete !");
            } else {
                int dialogResult = JOptionPane.showConfirmDialog(this, "Yakin hapus data ini?", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    // delete code here
                    String Query = "DELETE FROM `tb_balen_f2` WHERE `tb_balen_f2`.`no_laporan_produksi` = '" + Table_balen.getValueAt(j, 0) + "'";
                    Utility.db.getConnection().createStatement();
                    if ((Utility.db.getStatement().executeUpdate(Query)) == 1) {
                        JOptionPane.showMessageDialog(this, "Data berhasil disimpan");
                        button_search_lp_balen.doClick();
                    }
                }
            }
        } catch (HeadlessException | SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Finishing2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_deleteActionPerformed

    private void txt_search_Memo_SetoranKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_Memo_SetoranKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_Setoran();
        }
    }//GEN-LAST:event_txt_search_Memo_SetoranKeyPressed

    private void button_input_kakiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_input_kakiActionPerformed
        // TODO add your handling code here:
        JDialog_Input_Kaki_f2 input_kaki = new JDialog_Input_Kaki_f2(new javax.swing.JFrame(), true);
        input_kaki.pack();
        input_kaki.setLocationRelativeTo(this);
        input_kaki.setVisible(true);
        input_kaki.setEnabled(true);
    }//GEN-LAST:event_button_input_kakiActionPerformed

    private void button_tv_reprosesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_tv_reprosesActionPerformed
        // TODO add your handling code here:
        JFrame_TV_Reproses frame = new JFrame_TV_Reproses();
        frame.pack();
        frame.setLocationRelativeTo(this);
        frame.setVisible(true);
        frame.setEnabled(true);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }//GEN-LAST:event_button_tv_reprosesActionPerformed

    private void button_input_sesekanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_input_sesekanActionPerformed
        // TODO add your handling code here:
        JDialog_Input_Sesekan_F2_v2 input_sesekan = new JDialog_Input_Sesekan_F2_v2(new javax.swing.JFrame(), true);
        input_sesekan.pack();
        input_sesekan.setLocationRelativeTo(this);
        input_sesekan.setVisible(true);
        input_sesekan.setEnabled(true);
    }//GEN-LAST:event_button_input_sesekanActionPerformed

    private void button_laporan_f2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_laporan_f2ActionPerformed
        try {
            String no_lp = "";
            for (int i = 0; i < Table_Data_f2.getRowCount(); i++) {
                if (i != 0) {
                    no_lp = no_lp + ", ";
                }
                no_lp = no_lp + "'" + Table_Data_f2.getValueAt(i, 0).toString() + "'";
            }
            sql = "SELECT `tb_finishing_2`.*, `jumlah_keping`, `berat_basah`, `kode_grade`, `tb_laporan_produksi`.`no_kartu_waleta`, `ruangan`, `no_registrasi`, `cheat_no_kartu`, `cheat_rsb` \n"
                    + "FROM `tb_finishing_2` "
                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_finishing_2`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_bahan_baku_masuk_cheat` ON `tb_laporan_produksi`.`no_kartu_waleta` = `tb_bahan_baku_masuk_cheat`.`no_kartu_waleta`\n"
                    + "WHERE `tb_finishing_2`.`no_laporan_produksi` IN (" + no_lp + ") "
                    + " ORDER BY `tb_finishing_2`.`tgl_setor_f2`";
            JRDesignQuery newQuery = new JRDesignQuery();
            newQuery.setText(sql);
            JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Catatan_Pemeriksaan_Kebersihan_Sarang_Walet_Selama_Proses.jrxml");
            JASP_DESIGN.setQuery(newQuery);
            Map<String, Object> map = new HashMap<>();
            if (!(Date1_f2.getDate() == null || Date2_f2.getDate() == null)) {
                map.put("tanggal", new SimpleDateFormat("dd MMM yyy").format(Date1_f2.getDate()) + " - " + new SimpleDateFormat("dd MMM yyy").format(Date2_f2.getDate()));
            }
            JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
            JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, map, Utility.db.getConnection());
            JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
        } catch (JRException ex) {
            Logger.getLogger(JPanel_Finishing2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_laporan_f2ActionPerformed

    private void txt_search_memo_f2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_memo_f2KeyPressed
        // TODO add your handling code here:
        refreshTable_F2();
    }//GEN-LAST:event_txt_search_memo_f2KeyPressed

    private void button_f2_edit_kakiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_f2_edit_kakiActionPerformed
        // TODO add your handling code here:
        try {
            int j = Table_Data_f2.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Please Select Row Data that you want to change !");
            } else {
                JDialog_Edit_Data_Kaki_F2 edit_kaki = new JDialog_Edit_Data_Kaki_F2(new javax.swing.JFrame(), true);
                edit_kaki.pack();
                edit_kaki.setLocationRelativeTo(this);
                edit_kaki.setVisible(true);
                edit_kaki.setEnabled(true);
            }
            refreshTable_F2();
//            refreshTable_Setoran();
        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_Finishing2.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_f2_edit_kakiActionPerformed

    private void txt_search_evaluasi_tutupanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_evaluasi_tutupanKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refresh_EvaluasiMLEM();
        }
    }//GEN-LAST:event_txt_search_evaluasi_tutupanKeyPressed

    private void button_refresh_evaluasiMLEMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refresh_evaluasiMLEMActionPerformed
        // TODO add your handling code here:
        refresh_EvaluasiMLEM();
    }//GEN-LAST:event_button_refresh_evaluasiMLEMActionPerformed

    private void button_laporan_terima_WLTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_laporan_terima_WLTActionPerformed
        // TODO add your handling code here:
        try {
            if (Date_LaporanTerima.getDate() != null) {
                String tgl = "";
                if (ComboBox_LaporanTerima.getSelectedItem().toString().equals("Koreksi Kering")) {
                    tgl = "tgl_masuk_f2";
                } else if (ComboBox_LaporanTerima.getSelectedItem().toString().equals("F1")) {
                    tgl = "tgl_dikerjakan_f2";
                } else if (ComboBox_LaporanTerima.getSelectedItem().toString().equals("F2")) {
                    tgl = "tgl_f1";
                }

                sql = "SELECT `tb_finishing_2`.`no_laporan_produksi`, `tgl_masuk_f2`, `tgl_dikerjakan_f2`, `tgl_f1`, `ruangan`, `tb_laporan_produksi`.`jumlah_keping`, `tb_laporan_produksi`.`berat_basah`, `tb_laporan_produksi`.`kode_grade`\n"
                        + "FROM `tb_finishing_2` LEFT JOIN `tb_laporan_produksi` ON `tb_finishing_2`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                        + "WHERE `tb_finishing_2`.`" + tgl + "` = '" + dateFormat.format(Date_LaporanTerima.getDate()) + "' AND `tb_finishing_2`.`no_laporan_produksi` LIKE 'WL-%' "
                        + "ORDER BY `ruangan`, `tb_finishing_2`.`no_laporan_produksi`";
                JRDesignQuery newQuery = new JRDesignQuery();
                newQuery.setText(sql);
                JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Laporan_finishing2_WLT.jrxml");
                JASP_DESIGN.setQuery(newQuery);
                Map<String, Object> map = new HashMap<>();
                map.put("SUBREPORT_DIR", "Report\\");
                map.put("bagian", ComboBox_LaporanTerima.getSelectedItem().toString());
                JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
                JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, map, Utility.db.getConnection());
                JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
            } else {
                JOptionPane.showMessageDialog(this, "Tanggal Laporan Belum di pilih");
            }

        } catch (JRException ex) {
            Logger.getLogger(JPanel_DataCetak.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_laporan_terima_WLTActionPerformed

    private void button_laporan_terima_SUBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_laporan_terima_SUBActionPerformed
        // TODO add your handling code here:
        try {
            if (Date_LaporanTerima.getDate() != null) {
                String tgl = "";
                if (ComboBox_LaporanTerima.getSelectedItem().toString().equals("Koreksi Kering")) {
                    tgl = "tgl_masuk_f2";
                } else if (ComboBox_LaporanTerima.getSelectedItem().toString().equals("F1")) {
                    tgl = "tgl_dikerjakan_f2";
                } else if (ComboBox_LaporanTerima.getSelectedItem().toString().equals("F2")) {
                    tgl = "tgl_f1";
                }

                sql = "SELECT `tb_finishing_2`.`no_laporan_produksi`, `tgl_masuk_f2`, `tgl_dikerjakan_f2`, `tgl_f1`, `ruangan`, `tb_laporan_produksi`.`jumlah_keping`, `tb_laporan_produksi`.`berat_basah`, `tb_laporan_produksi`.`kode_grade`\n"
                        + "FROM `tb_finishing_2` LEFT JOIN `tb_laporan_produksi` ON `tb_finishing_2`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                        + "WHERE `tb_finishing_2`.`" + tgl + "` = '" + dateFormat.format(Date_LaporanTerima.getDate()) + "' AND `tb_finishing_2`.`no_laporan_produksi` LIKE 'WL.%' "
                        + "ORDER BY `ruangan`, `tb_finishing_2`.`no_laporan_produksi`";
                JRDesignQuery newQuery = new JRDesignQuery();
                newQuery.setText(sql);
                JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Laporan_finishing2_SUB.jrxml");
                JASP_DESIGN.setQuery(newQuery);
                Map<String, Object> map = new HashMap<>();
                map.put("SUBREPORT_DIR", "Report\\");
                map.put("bagian", ComboBox_LaporanTerima.getSelectedItem().toString());
                JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
                JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, map, Utility.db.getConnection());
                JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
            } else {
                JOptionPane.showMessageDialog(this, "Tanggal Laporan Belum di pilih");
            }

        } catch (JRException ex) {
            Logger.getLogger(JPanel_DataCetak.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_laporan_terima_SUBActionPerformed

    private void txt_search_ruanganKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_ruanganKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_F2();
        }
    }//GEN-LAST:event_txt_search_ruanganKeyPressed

    private void txt_search_lpsuwirKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_lpsuwirKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_LP_suwir();
        }
    }//GEN-LAST:event_txt_search_lpsuwirKeyPressed

    private void button_search_lpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_lpActionPerformed
        // TODO add your handling code here:
        refreshTable_LP_suwir();
    }//GEN-LAST:event_button_search_lpActionPerformed

    private void button_export_LPSuwirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_LPSuwirActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) table_data_LPsuwir.getModel();
        ExportToExcel.writeToExcel(model, this);
    }//GEN-LAST:event_button_export_LPSuwirActionPerformed

    private void button_export_AsalBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_AsalBoxActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) table_asalBox_lpSuwir.getModel();
        ExportToExcel.writeToExcel(model, this);
    }//GEN-LAST:event_button_export_AsalBoxActionPerformed

    private void button_export_LP_F2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_LP_F2ActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) table_LaporanProduksi_LPSuwir.getModel();
        ExportToExcel.writeToExcel(model, this);
    }//GEN-LAST:event_button_export_LP_F2ActionPerformed

    private void button_export_BoxReprosesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_BoxReprosesActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) table_BoxReprosesi_LPSuwir.getModel();
        ExportToExcel.writeToExcel(model, this);
    }//GEN-LAST:event_button_export_BoxReprosesActionPerformed

    private void button_Print_LP_SWRActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_Print_LP_SWRActionPerformed
        // TODO add your handling code here:
        try {
            int j = table_data_LPsuwir.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih salah satu LP Suwir pada tabel!", "warning!", 1);
            } else {
                JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Laporan_Produksi_LP_Suwir.jrxml");
                JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("NO_LP_SUWIR", table_data_LPsuwir.getValueAt(j, 0).toString());
                params.put("STOK", (float) table_data_LPsuwir.getValueAt(j, 8));
                JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, params, Utility.db.getConnection());
                JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
            }
        } catch (JRException ex) {
            JOptionPane.showMessageDialog(this, ex.getLocalizedMessage());
            Logger.getLogger(JPanel_Finishing2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_Print_LP_SWRActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_LaporanTerima;
    private javax.swing.JComboBox<String> ComboBox_SearchDate;
    private javax.swing.JComboBox<String> ComboBox_evaluasi_ruanganMLEM;
    private javax.swing.JComboBox<String> ComboBox_ruangan;
    private javax.swing.JComboBox<String> ComboBox_searchBentuk;
    private com.toedter.calendar.JDateChooser Date1_f2;
    private com.toedter.calendar.JDateChooser Date2_f2;
    private com.toedter.calendar.JDateChooser DateTutupan1;
    private com.toedter.calendar.JDateChooser DateTutupan2;
    private com.toedter.calendar.JDateChooser Date_LaporanTerima;
    private com.toedter.calendar.JDateChooser Date_Search_LPsuwir_1;
    private com.toedter.calendar.JDateChooser Date_Search_LPsuwir_2;
    private com.toedter.calendar.JDateChooser Date_Setoran1;
    private com.toedter.calendar.JDateChooser Date_Setoran2;
    public static javax.swing.JTable Table_Data_f2;
    public static javax.swing.JTable Table_Setoran_harian_f2;
    public static javax.swing.JTable Table_balen;
    public static javax.swing.JTable Table_evaluasi_MLEM;
    private javax.swing.JTable Table_evaluasi_MLEM_summary;
    private javax.swing.JButton button_Print_LP_SWR;
    public javax.swing.JButton button_balen;
    public static javax.swing.JButton button_delete;
    private javax.swing.JButton button_export_AsalBox;
    private javax.swing.JButton button_export_BoxReproses;
    private javax.swing.JButton button_export_LPSuwir;
    private javax.swing.JButton button_export_LP_F2;
    public static javax.swing.JButton button_export_f2;
    private javax.swing.JButton button_export_f2_rendemen;
    public javax.swing.JButton button_f2_delete;
    public javax.swing.JButton button_f2_edit;
    public javax.swing.JButton button_f2_edit_kaki;
    public javax.swing.JButton button_f2_setor_lp;
    public javax.swing.JButton button_f2_terima_lp;
    public javax.swing.JButton button_input_byproduct;
    public javax.swing.JButton button_input_f1;
    public javax.swing.JButton button_input_f2;
    public javax.swing.JButton button_input_kaki;
    public javax.swing.JButton button_input_koreksi;
    public javax.swing.JButton button_input_koreksi1;
    public javax.swing.JButton button_input_sesekan;
    public static javax.swing.JButton button_laporan_f2;
    public static javax.swing.JButton button_laporan_terima_SUB;
    public static javax.swing.JButton button_laporan_terima_WLT;
    public static javax.swing.JButton button_refresh_evaluasiMLEM;
    public static javax.swing.JButton button_search_f2;
    private javax.swing.JButton button_search_lp;
    public static javax.swing.JButton button_search_lp_balen;
    public static javax.swing.JButton button_search_setoran;
    public static javax.swing.JButton button_selesai_balen;
    public static javax.swing.JButton button_tv_reproses;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel100;
    private javax.swing.JLabel jLabel101;
    private javax.swing.JLabel jLabel102;
    private javax.swing.JLabel jLabel103;
    private javax.swing.JLabel jLabel104;
    private javax.swing.JLabel jLabel105;
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
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel72;
    private javax.swing.JLabel jLabel73;
    private javax.swing.JLabel jLabel74;
    private javax.swing.JLabel jLabel75;
    private javax.swing.JLabel jLabel76;
    private javax.swing.JLabel jLabel77;
    private javax.swing.JLabel jLabel78;
    private javax.swing.JLabel jLabel79;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel80;
    private javax.swing.JLabel jLabel81;
    private javax.swing.JLabel jLabel82;
    private javax.swing.JLabel jLabel83;
    private javax.swing.JLabel jLabel84;
    private javax.swing.JLabel jLabel85;
    private javax.swing.JLabel jLabel86;
    private javax.swing.JLabel jLabel87;
    private javax.swing.JLabel jLabel88;
    private javax.swing.JLabel jLabel89;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel90;
    private javax.swing.JLabel jLabel91;
    private javax.swing.JLabel jLabel92;
    private javax.swing.JLabel jLabel93;
    private javax.swing.JLabel jLabel94;
    private javax.swing.JLabel jLabel95;
    private javax.swing.JLabel jLabel96;
    private javax.swing.JLabel jLabel97;
    private javax.swing.JLabel jLabel98;
    private javax.swing.JLabel jLabel99;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel jPanel_data_LP_suwir;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane13;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JLabel label_BoxReproses_lpSuwir1;
    private javax.swing.JLabel label_LaporanProduksi_lpSuwir;
    private javax.swing.JLabel label_asalBox_lpSuwir;
    private javax.swing.JLabel label_berat_flat;
    private javax.swing.JLabel label_berat_netto_jidun;
    private javax.swing.JLabel label_berat_netto_utuh;
    private javax.swing.JLabel label_berat_setoran;
    private javax.swing.JLabel label_berat_sp;
    private javax.swing.JLabel label_bk;
    private javax.swing.JLabel label_bk12;
    private javax.swing.JLabel label_kpg_flat;
    private javax.swing.JLabel label_kpg_setoran;
    private javax.swing.JLabel label_kpg_utuh;
    private javax.swing.JLabel label_rend_flat1;
    private javax.swing.JLabel label_rend_flat2;
    private javax.swing.JLabel label_rend_jidun1;
    private javax.swing.JLabel label_rend_jidun2;
    private javax.swing.JLabel label_rend_mk1;
    private javax.swing.JLabel label_rend_mk2;
    private javax.swing.JLabel label_rend_total1;
    private javax.swing.JLabel label_rend_total2;
    private javax.swing.JLabel label_sh1;
    private javax.swing.JLabel label_sh2;
    private javax.swing.JLabel label_sp1;
    private javax.swing.JLabel label_sp2;
    private javax.swing.JLabel label_target_flat1;
    private javax.swing.JLabel label_target_flat2;
    private javax.swing.JLabel label_target_sh1;
    private javax.swing.JLabel label_target_sh2;
    private javax.swing.JLabel label_target_sp1;
    private javax.swing.JLabel label_target_sp2;
    private javax.swing.JLabel label_target_utuh1;
    private javax.swing.JLabel label_target_utuh2;
    private javax.swing.JLabel label_total_BoxRepacking_lpsuwir;
    private javax.swing.JLabel label_total_LaporanProduksi_lpsuwir;
    private javax.swing.JLabel label_total_asalBox_lpSuwir;
    private javax.swing.JLabel label_total_berat_kaki;
    private javax.swing.JLabel label_total_data_f2;
    private javax.swing.JLabel label_total_data_tutupan;
    private javax.swing.JLabel label_total_gram_BoxRepracking_lpsuwir;
    private javax.swing.JLabel label_total_gram_LaporanProduksi_lpsuwir;
    private javax.swing.JLabel label_total_gram_akhir;
    private javax.swing.JLabel label_total_gram_asalBox_lpsuwir;
    private javax.swing.JLabel label_total_gram_awal;
    private javax.swing.JLabel label_total_keluar_f2;
    private javax.swing.JLabel label_total_keping_asalBox_lpsuwir;
    private javax.swing.JLabel label_total_kpg_akhir;
    private javax.swing.JLabel label_total_kpg_awal;
    private javax.swing.JLabel label_total_lp;
    private javax.swing.JLabel label_total_lp_balen;
    private javax.swing.JLabel label_total_lpsuwir;
    private javax.swing.JLabel label_total_mlem;
    private javax.swing.JLabel label_total_pencabut;
    private javax.swing.JLabel label_total_stok;
    private javax.swing.JTable table_BoxReprosesi_LPSuwir;
    private javax.swing.JTable table_LaporanProduksi_LPSuwir;
    private javax.swing.JTable table_asalBox_lpSuwir;
    private javax.swing.JTable table_data_LPsuwir;
    private javax.swing.JTable table_data_pencabut;
    private javax.swing.JTextField txt_search_LP_setoran;
    private javax.swing.JTextField txt_search_Memo_Setoran;
    private javax.swing.JTextField txt_search_evaluasi_tutupan;
    private javax.swing.JTextField txt_search_kartu_setoran;
    private javax.swing.JTextField txt_search_lp_balen;
    private javax.swing.JTextField txt_search_lpsuwir;
    private javax.swing.JTextField txt_search_memo_f2;
    private javax.swing.JTextField txt_search_no_lp;
    private javax.swing.JTextField txt_search_ruangan;
    // End of variables declaration//GEN-END:variables

}
