package waleta_system.Manajemen;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;
import waleta_system.Interface.InterfacePanel;

public class JPanel_BonusPetikRSB extends javax.swing.JPanel implements InterfacePanel {

    
    String sql = null;
    ResultSet rs;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    java.util.Date today = new java.util.Date();//date berisikan tanggal hari ini
    DecimalFormat decimalFormat = new DecimalFormat();
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    int jumlah_tahun = 3;

    public JPanel_BonusPetikRSB() {
        initComponents();
    }

    @Override
    public void init() {
        try {
            
            

            int this_year = Integer.valueOf(new SimpleDateFormat("yyyy").format(today));
//            int jumlah_tahun = 3;

            JTableHeader TableHeader = Table_data_panen_bulanan.getTableHeader();
            TableColumnModel TColumnModel = TableHeader.getColumnModel();
            String header = "";
            for (int x = 0; x < jumlah_tahun; x++) {
                header = Integer.toString(this_year - x);
                TableColumn tc = TColumnModel.getColumn(x + 2);
                tc.setHeaderValue(header + " (Kg)");
            }

            refreshTable_bonus_rsb();
            refreshTable_grade_baku();
            refreshTable_bonus_per_keping();
            Table_bonus_rsb.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent event) {
                    if (!event.getValueIsAdjusting() && Table_bonus_rsb.getSelectedRow() != -1) {
                        int i = Table_bonus_rsb.getSelectedRow();
                        String rsb = Table_bonus_rsb.getValueAt(i, 0).toString();
                        label_rsb1.setText(rsb);
                        label_rsb2.setText(rsb);
                        refreshTable_bonus_kartu(rsb);
                        refreshTable_rekap_bonus(rsb);
                    }
                }
            });
            Table_bonus_kartu.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent event) {
                    if (!event.getValueIsAdjusting() && Table_bonus_kartu.getSelectedRow() != -1) {
                        int i = Table_bonus_kartu.getSelectedRow();
                        String no_kartu = Table_bonus_kartu.getValueAt(i, 0).toString();
                        label_no_kartu.setText(no_kartu);
                        refreshTable_detail_grading(no_kartu);
                    }
                }
            });
            Table_data_grade.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent event) {
                    if (!event.getValueIsAdjusting() && Table_data_grade.getSelectedRow() != -1) {
                        int i = Table_data_grade.getSelectedRow();
                        txt_grade.setText(Table_data_grade.getValueAt(i, 0).toString());
                        ComboBox_kategori_bonus.setSelectedItem(Table_data_grade.getValueAt(i, 1).toString());
                    }
                }
            });
            Table_data_bonus_per_keping.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent event) {
                    if (!event.getValueIsAdjusting() && Table_data_bonus_per_keping.getSelectedRow() != -1) {
                        int i = Table_data_bonus_per_keping.getSelectedRow();
                        txt_kategori_bonus.setText(Table_data_bonus_per_keping.getValueAt(i, 0).toString());
                        txt_nominal_bonus.setText(Table_data_bonus_per_keping.getValueAt(i, 1).toString());
                    }
                }
            });
        } catch (Exception ex) {
            Logger.getLogger(JPanel_BonusPetikRSB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //SLIDE 1 - DATA DETAIL
    public void refreshTable_bonus_rsb() {
        try {
            int total_keping = 0, total_gram = 0, total_bonus = 0;
            DefaultTableModel model = (DefaultTableModel) Table_bonus_rsb.getModel();
            model.setRowCount(0);
            if (Date1.getDate() != null && Date2.getDate() != null) {
                sql = "SELECT `tb_rumah_burung`.`nama_rumah_burung`, SUM(`jumlah_keping`) AS 'keping', SUM(`total_berat`) AS 'berat', `bonus_petik`, "
                        + "SUM(`tb_grading_bahan_baku`.`jumlah_keping` * `tb_bonus_petik_rsb`.`bonus_per_kpg`) AS 'bonus'\n"
                        + "FROM `tb_bahan_baku_masuk` \n"
                        + "LEFT JOIN `tb_rumah_burung` ON `tb_bahan_baku_masuk`.`no_registrasi` = `tb_rumah_burung`.`no_registrasi`\n"
                        + "LEFT JOIN `tb_grading_bahan_baku` ON `tb_bahan_baku_masuk`.`no_kartu_waleta` = `tb_grading_bahan_baku`.`no_kartu_waleta` \n"
                        + "LEFT JOIN `tb_grade_bahan_baku` ON `tb_grading_bahan_baku`.`kode_grade` = `tb_grade_bahan_baku`.`kode_grade`\n"
                        + "LEFT JOIN `tb_bonus_petik_rsb` ON `tb_grade_bahan_baku`.`kode_bonus_rsb` = `tb_bonus_petik_rsb`.`kode_bonus`\n"
                        + "WHERE "
                        + "`kode_supplier` = 'P' AND "
                        + "`tb_bahan_baku_masuk`.`no_kartu_waleta` NOT LIKE '%CMP%' AND "
                        + "`tb_bahan_baku_masuk`.`no_kartu_waleta` LIKE '%" + txt_filter_nokartu.getText() + "%' AND "
                        + "`tgl_panen` BETWEEN '" + dateFormat.format(Date1.getDate()) + "' AND '" + dateFormat.format(Date2.getDate()) + "' "
                        + "GROUP BY `tb_rumah_burung`.`no_registrasi` ORDER BY `nama_rumah_burung`";
                rs = Utility.db.getStatement().executeQuery(sql);
                Object[] row = new Object[5];
                while (rs.next()) {
                    row[0] = rs.getString("nama_rumah_burung");
                    row[1] = rs.getFloat("keping");
                    row[2] = rs.getFloat("berat");
                    row[3] = rs.getFloat("bonus") * rs.getFloat("bonus_petik");
                    model.addRow(row);

                    total_keping = total_keping + rs.getInt("keping");
                    total_gram = total_gram + rs.getInt("berat");
                    total_bonus = total_bonus + (rs.getInt("bonus") * rs.getInt("bonus_petik"));
                }
            }
            decimalFormat.setGroupingUsed(true);
            label_total_rsb.setText(decimalFormat.format(Table_bonus_rsb.getRowCount()));
            label_total_kpg_rsb.setText(decimalFormat.format(total_keping) + " Keping");
            label_total_gram_rsb.setText(decimalFormat.format(total_gram) + " Gram");
            label_total_bonus_rsb.setText("Rp. " + decimalFormat.format(total_bonus));
            ColumnsAutoSizer.sizeColumnsToFit(Table_bonus_rsb);
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_BonusPetikRSB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_rekap_bonus(String RSB) {
        try {
            int total_bonus = 0;
            DefaultTableModel model = (DefaultTableModel) Table_bonus_rekap.getModel();
            model.setRowCount(0);
            if (Date1.getDate() != null && Date2.getDate() != null) {
                sql = "SELECT `tb_grade_bahan_baku`.`kode_bonus_rsb`, SUM(`jumlah_keping`) AS 'keping', `tb_bonus_petik_rsb`.`bonus_per_kpg`, "
                        + "SUM(`tb_grading_bahan_baku`.`jumlah_keping` * `tb_bonus_petik_rsb`.`bonus_per_kpg`) AS 'bonus'\n"
                        + "FROM `tb_bahan_baku_masuk` \n"
                        + "LEFT JOIN `tb_rumah_burung` ON `tb_bahan_baku_masuk`.`no_registrasi` = `tb_rumah_burung`.`no_registrasi`\n"
                        + "LEFT JOIN `tb_grading_bahan_baku` ON `tb_bahan_baku_masuk`.`no_kartu_waleta` = `tb_grading_bahan_baku`.`no_kartu_waleta` \n"
                        + "LEFT JOIN `tb_grade_bahan_baku` ON `tb_grading_bahan_baku`.`kode_grade` = `tb_grade_bahan_baku`.`kode_grade`\n"
                        + "LEFT JOIN `tb_bonus_petik_rsb` ON `tb_grade_bahan_baku`.`kode_bonus_rsb` = `tb_bonus_petik_rsb`.`kode_bonus`\n"
                        + "WHERE "
                        + "`kode_supplier` = 'P' AND "
                        + "`tb_bahan_baku_masuk`.`no_kartu_waleta` NOT LIKE '%CMP%' AND "
                        + "`tb_rumah_burung`.`nama_rumah_burung` = '" + RSB + "' AND "
                        + "`tgl_panen` BETWEEN '" + dateFormat.format(Date1.getDate()) + "' AND '" + dateFormat.format(Date2.getDate()) + "' "
                        + "GROUP BY `tb_grade_bahan_baku`.`kode_bonus_rsb` ORDER BY `tb_grade_bahan_baku`.`kode_bonus_rsb`";
                rs = Utility.db.getStatement().executeQuery(sql);
                Object[] row = new Object[5];
                while (rs.next()) {
                    row[0] = rs.getString("kode_bonus_rsb");
                    row[1] = rs.getFloat("keping");
                    row[2] = rs.getFloat("bonus_per_kpg");
                    row[3] = rs.getFloat("bonus");
                    if (rs.getString("kode_bonus_rsb") != null) {
                        model.addRow(row);
                    }

                    total_bonus = total_bonus + rs.getInt("bonus");
                }
            }
            decimalFormat.setGroupingUsed(true);
            label_total_bonus_rekap.setText("Rp. " + decimalFormat.format(total_bonus));
            ColumnsAutoSizer.sizeColumnsToFit(Table_bonus_rekap);
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_BonusPetikRSB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_bonus_kartu(String RSB) {
        try {
            int total_keping = 0, total_gram = 0, total_bonus = 0;
            DefaultTableModel model = (DefaultTableModel) Table_bonus_kartu.getModel();
            model.setRowCount(0);
            if (Date1.getDate() != null && Date2.getDate() != null) {
                sql = "SELECT `tb_bahan_baku_masuk`.`no_kartu_waleta`, `tb_rumah_burung`.`nama_rumah_burung`, `tgl_masuk`, `tgl_panen`, `keping_real`, `berat_real`, `tgl_timbang`, SUM(`tb_grading_bahan_baku`.`jumlah_keping` * `tb_bonus_petik_rsb`.`bonus_per_kpg`) AS 'bonus'\n"
                        + "FROM `tb_bahan_baku_masuk` \n"
                        + "LEFT JOIN `tb_rumah_burung` ON `tb_bahan_baku_masuk`.`no_registrasi` = `tb_rumah_burung`.`no_registrasi`\n"
                        + "LEFT JOIN `tb_grading_bahan_baku` ON `tb_bahan_baku_masuk`.`no_kartu_waleta` = `tb_grading_bahan_baku`.`no_kartu_waleta` \n"
                        + "LEFT JOIN `tb_grade_bahan_baku` ON `tb_grading_bahan_baku`.`kode_grade` = `tb_grade_bahan_baku`.`kode_grade`\n"
                        + "LEFT JOIN `tb_bonus_petik_rsb` ON `tb_grade_bahan_baku`.`kode_bonus_rsb` = `tb_bonus_petik_rsb`.`kode_bonus`\n"
                        + "WHERE "
                        + "`kode_supplier` = 'P' AND "
                        + "`tb_bahan_baku_masuk`.`no_kartu_waleta` NOT LIKE '%CMP%' AND "
                        + "`tb_bahan_baku_masuk`.`no_kartu_waleta` LIKE '%" + txt_filter_nokartu.getText() + "%' AND "
                        + "`tb_rumah_burung`.`nama_rumah_burung` = '" + RSB + "' AND "
                        + "`tgl_panen` BETWEEN '" + dateFormat.format(Date1.getDate()) + "' AND '" + dateFormat.format(Date2.getDate()) + "' "
                        + "GROUP BY `no_kartu_waleta` ORDER BY `tgl_panen` DESC";
                rs = Utility.db.getStatement().executeQuery(sql);
                Object[] row = new Object[7];
                while (rs.next()) {
                    row[0] = rs.getString("no_kartu_waleta");
                    row[1] = rs.getString("nama_rumah_burung");
                    row[2] = rs.getDate("tgl_masuk");
                    row[3] = rs.getDate("tgl_panen");
                    row[4] = rs.getFloat("keping_real");
                    row[5] = rs.getFloat("berat_real");
                    row[6] = rs.getFloat("bonus");
                    model.addRow(row);

                    total_keping = total_keping + rs.getInt("keping_real");
                    total_gram = total_gram + rs.getInt("berat_real");
                    total_bonus = total_bonus + rs.getInt("bonus");
                }
            }
            decimalFormat.setGroupingUsed(true);
            label_total_kartu.setText(decimalFormat.format(Table_bonus_kartu.getRowCount()));
            label_total_kpg_kartu.setText(decimalFormat.format(total_keping) + " Keping");
            label_total_gram_kartu.setText(decimalFormat.format(total_gram) + " Gram");
            label_total_bonus_kartu.setText("Rp. " + decimalFormat.format(total_bonus));
            ColumnsAutoSizer.sizeColumnsToFit(Table_bonus_kartu);
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_BonusPetikRSB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_detail_grading(String no_kartu) {
        try {
            int total_keping = 0, total_gram = 0, total_bonus = 0;
            DefaultTableModel model = (DefaultTableModel) Table_detail_grading.getModel();
            model.setRowCount(0);
            sql = "SELECT `tb_grading_bahan_baku`.`kode_grade`, `jumlah_keping`, `total_berat`, `bonus_per_kpg`, (`jumlah_keping` * `bonus_per_kpg`) AS 'bonus'\n"
                    + "FROM `tb_grading_bahan_baku` \n"
                    + "LEFT JOIN `tb_grade_bahan_baku` ON `tb_grading_bahan_baku`.`kode_grade` = `tb_grade_bahan_baku`.`kode_grade`\n"
                    + "LEFT JOIN `tb_bonus_petik_rsb` ON `tb_grade_bahan_baku`.`kode_bonus_rsb` = `tb_bonus_petik_rsb`.`kode_bonus`\n"
                    + "WHERE "
                    + "`tb_grading_bahan_baku`.`no_kartu_waleta` = '" + no_kartu + "' ";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[5];
            while (rs.next()) {
                row[0] = rs.getString("kode_grade");
                row[1] = rs.getFloat("jumlah_keping");
                row[2] = rs.getFloat("total_berat");
                row[3] = rs.getFloat("bonus_per_kpg");
                row[4] = rs.getFloat("bonus");
                model.addRow(row);

                total_keping = total_keping + rs.getInt("jumlah_keping");
                total_gram = total_gram + rs.getInt("total_berat");
                total_bonus = total_bonus + rs.getInt("bonus");
            }
            decimalFormat.setGroupingUsed(true);
            label_total_detail_grade.setText(decimalFormat.format(Table_detail_grading.getRowCount()));
            label_total_kpg_grading.setText(decimalFormat.format(total_keping) + " Keping");
            label_total_gram_grading.setText(decimalFormat.format(total_gram) + " Gram");
            label_total_bonus_grading.setText("Rp. " + decimalFormat.format(total_bonus));
            ColumnsAutoSizer.sizeColumnsToFit(Table_detail_grading);
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_BonusPetikRSB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //SLIDE 2 - DATA TAHUNAN & BULANAN
    public void refreshTable_data_panen_tahunan() {
        try {
            int this_year = Integer.valueOf(new SimpleDateFormat("yyyy").format(today));
            DefaultTableModel model_1 = (DefaultTableModel) Table_data_panen_tahunan.getModel();
            model_1.setRowCount(0);
            DefaultTableModel model_2 = (DefaultTableModel) Table_data_beratKpg_tahunan.getModel();
            model_2.setRowCount(0);
            DefaultTableModel model_3 = (DefaultTableModel) Table_data_bonus_tahunan.getModel();
            model_3.setRowCount(0);
            sql = "SELECT YEAR(`tgl_panen`) AS 'tahun', MONTH(`tgl_panen`) AS 'bulan', "
                    + "SUM(`tb_grading_bahan_baku`.`total_berat`) AS 'berat_grading',\n"
                    + "AVG(IF(`tb_grading_bahan_baku`.`jumlah_keping`>0, `tb_grading_bahan_baku`.`total_berat` / `tb_grading_bahan_baku`.`jumlah_keping`, NULL)) AS 'beratKpg',\n"
                    + "SUM(`tb_grading_bahan_baku`.`jumlah_keping` * `tb_bonus_petik_rsb`.`bonus_per_kpg`) AS 'bonus'\n"
                    + "FROM `tb_bahan_baku_masuk` "
                    + "LEFT JOIN `tb_grading_bahan_baku` ON `tb_bahan_baku_masuk`.`no_kartu_waleta` = `tb_grading_bahan_baku`.`no_kartu_waleta` \n"
                    + "LEFT JOIN `tb_grade_bahan_baku` ON `tb_grading_bahan_baku`.`kode_grade` = `tb_grade_bahan_baku`.`kode_grade`\n"
                    + "LEFT JOIN `tb_bonus_petik_rsb` ON `tb_grade_bahan_baku`.`kode_bonus_rsb` = `tb_bonus_petik_rsb`.`kode_bonus`\n"
                    + "WHERE `kode_supplier` = 'P' "
                    + "AND `tb_bahan_baku_masuk`.`no_kartu_waleta` NOT LIKE '%CMP%' "
                    + "AND YEAR(`tgl_panen`) >= " + (this_year - 3)
                    + " GROUP BY `tb_bahan_baku_masuk`.`no_kartu_waleta`"
                    + "ORDER BY `tgl_panen` DESC";
//            System.out.println(sql);
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row_1 = new Object[5];
            Object[] row_2 = new Object[5];
            Object[] row_3 = new Object[5];
            int panen_periode_1 = 0, panen_periode_2 = 0, panen_periode_3 = 0;
            float brtKpg_periode_1 = 0, brtKpg_periode_2 = 0, brtKpg_periode_3 = 0, brtKpg_tot_periode_1 = 0, brtKpg_tot_periode_2 = 0, brtKpg_tot_periode_3 = 0;
            int bonus_periode_1 = 0, bonus_periode_2 = 0, bonus_periode_3 = 0, bonus_tot_periode_1 = 0, bonus_tot_periode_2 = 0, bonus_tot_periode_3 = 0;
            float kartu_periode_1 = 0, kartu_periode_2 = 0, kartu_periode_3 = 0, kartu_tot_periode_1 = 0, kartu_tot_periode_2 = 0, kartu_tot_periode_3 = 0;
            while (rs.next()) {
                if (rs.getInt("tahun") == this_year) {
                    if (rs.getInt("bulan") == 1 || rs.getInt("bulan") == 2 || rs.getInt("bulan") == 3 || rs.getInt("bulan") == 4) {
                        panen_periode_1 = panen_periode_1 + rs.getInt("berat_grading");
                        brtKpg_periode_1 = brtKpg_periode_1 + rs.getInt("beratKpg");
                        bonus_periode_1 = bonus_periode_1 + rs.getInt("bonus");
                        if (rs.getInt("beratKpg") > 0) {
                            kartu_periode_1++;
                        }
                    } else if (rs.getInt("bulan") == 5 || rs.getInt("bulan") == 6 || rs.getInt("bulan") == 7 || rs.getInt("bulan") == 8) {
                        panen_periode_2 = panen_periode_2 + rs.getInt("berat_grading");
                        brtKpg_periode_2 = brtKpg_periode_2 + rs.getInt("beratKpg");
                        bonus_periode_2 = bonus_periode_2 + rs.getInt("bonus");
                        if (rs.getInt("beratKpg") > 0) {
                            kartu_periode_2++;
                        }
                    } else if (rs.getInt("bulan") == 9 || rs.getInt("bulan") == 10 || rs.getInt("bulan") == 11 || rs.getInt("bulan") == 12) {
                        panen_periode_3 = panen_periode_3 + rs.getInt("berat_grading");
                        brtKpg_periode_3 = brtKpg_periode_3 + rs.getInt("beratKpg");
                        bonus_periode_3 = bonus_periode_3 + rs.getInt("bonus");
                        if (rs.getInt("beratKpg") > 0) {
                            kartu_periode_3++;
                        }
                    }
                } else {
                    //TABEL 1
                    row_1[0] = this_year;
                    row_1[1] = panen_periode_1;
                    row_1[2] = panen_periode_2;
                    row_1[3] = panen_periode_3;
                    row_1[4] = panen_periode_1 + panen_periode_2 + panen_periode_3;
                    model_1.addRow(row_1);
                    panen_periode_1 = 0;
                    panen_periode_2 = 0;
                    panen_periode_3 = 0;

                    //TABEL 2
                    row_2[0] = this_year;
                    if (kartu_periode_1 > 0) {
                        row_2[1] = Math.round((brtKpg_periode_1 / kartu_periode_1) * 100.f) / 100.f;
                        brtKpg_tot_periode_1 = brtKpg_tot_periode_1 + brtKpg_periode_1;
                        kartu_tot_periode_1 = kartu_tot_periode_1 + kartu_periode_1;
                    } else {
                        row_2[1] = 0.f;
                    }
                    if (kartu_periode_2 > 0) {
                        row_2[2] = Math.round((brtKpg_periode_2 / kartu_periode_2) * 100.f) / 100.f;
                        brtKpg_tot_periode_2 = brtKpg_tot_periode_2 + brtKpg_periode_2;
                        kartu_tot_periode_2 = kartu_tot_periode_2 + kartu_periode_2;
                    } else {
                        row_2[2] = 0.f;
                    }
                    if (kartu_periode_3 > 0) {
                        row_2[3] = Math.round((brtKpg_periode_3 / kartu_periode_3) * 100.f) / 100.f;
                        brtKpg_tot_periode_3 = brtKpg_tot_periode_3 + brtKpg_periode_3;
                        kartu_tot_periode_3 = kartu_tot_periode_3 + kartu_periode_3;
                    } else {
                        row_2[3] = 0.f;
                    }
                    if ((kartu_periode_1 + kartu_periode_2 + kartu_periode_3) > 0) {
                        float avg = (brtKpg_periode_1 + brtKpg_periode_2 + brtKpg_periode_3) / (kartu_periode_1 + kartu_periode_2 + kartu_periode_3);
                        row_2[4] = Math.round(avg * 100.f) / 100.f;
                    } else {
                        row_2[4] = 0.f;
                    }
                    model_2.addRow(row_2);
                    brtKpg_periode_1 = 0;
                    brtKpg_periode_2 = 0;
                    brtKpg_periode_3 = 0;
                    kartu_periode_1 = 0;
                    kartu_periode_2 = 0;
                    kartu_periode_3 = 0;

                    //TABEL 3
                    row_3[0] = this_year;
                    row_3[1] = bonus_periode_1;
                    row_3[2] = bonus_periode_2;
                    row_3[3] = bonus_periode_3;
                    row_3[4] = bonus_periode_1 + bonus_periode_2 + bonus_periode_3;
                    bonus_tot_periode_1 = bonus_tot_periode_1 + bonus_periode_1;
                    bonus_tot_periode_2 = bonus_tot_periode_2 + bonus_periode_2;
                    bonus_tot_periode_3 = bonus_tot_periode_3 + bonus_periode_3;
                    model_3.addRow(row_3);
                    bonus_periode_1 = 0;
                    bonus_periode_2 = 0;
                    bonus_periode_3 = 0;

                    this_year--;
                    if (rs.getInt("bulan") == 1 || rs.getInt("bulan") == 2 || rs.getInt("bulan") == 3 || rs.getInt("bulan") == 4) {
                        panen_periode_1 = panen_periode_1 + rs.getInt("berat_grading");
                        brtKpg_periode_1 = brtKpg_periode_1 + rs.getInt("beratKpg");
                        bonus_periode_1 = bonus_periode_1 + rs.getInt("bonus");
                    } else if (rs.getInt("bulan") == 5 || rs.getInt("bulan") == 6 || rs.getInt("bulan") == 7 || rs.getInt("bulan") == 8) {
                        panen_periode_2 = panen_periode_2 + rs.getInt("berat_grading");
                        brtKpg_periode_2 = brtKpg_periode_2 + rs.getInt("beratKpg");
                        bonus_periode_2 = bonus_periode_2 + rs.getInt("bonus");
                    } else if (rs.getInt("bulan") == 9 || rs.getInt("bulan") == 10 || rs.getInt("bulan") == 11 || rs.getInt("bulan") == 12) {
                        panen_periode_3 = panen_periode_3 + rs.getInt("berat_grading");
                        brtKpg_periode_3 = brtKpg_periode_3 + rs.getInt("beratKpg");
                        bonus_periode_3 = bonus_periode_3 + rs.getInt("bonus");
                    }
                }
            }

            //TABEL 1
            row_1[0] = this_year;
            row_1[1] = panen_periode_1;
            row_1[2] = panen_periode_2;
            row_1[3] = panen_periode_3;
            row_1[4] = panen_periode_1 + panen_periode_2 + panen_periode_3;
            model_1.addRow(row_1);

            //TABEL 2
            row_2[0] = this_year;
            if (kartu_periode_1 > 0) {
                row_2[1] = Math.round((brtKpg_periode_1 / kartu_periode_1) * 100.f) / 100.f;
                brtKpg_tot_periode_1 = brtKpg_tot_periode_1 + brtKpg_periode_1;
                kartu_tot_periode_1 = kartu_tot_periode_1 + kartu_periode_1;
            } else {
                row_2[1] = 0.f;
            }
            if (kartu_periode_2 > 0) {
                row_2[2] = Math.round((brtKpg_periode_2 / kartu_periode_2) * 100.f) / 100.f;
                brtKpg_tot_periode_2 = brtKpg_tot_periode_2 + brtKpg_periode_2;
                kartu_tot_periode_2 = kartu_tot_periode_2 + kartu_periode_2;
            } else {
                row_2[2] = 0.f;
            }
            if (kartu_periode_3 > 0) {
                row_2[3] = Math.round((brtKpg_periode_3 / kartu_periode_3) * 100.f) / 100.f;
                brtKpg_tot_periode_3 = brtKpg_tot_periode_3 + brtKpg_periode_3;
                kartu_tot_periode_3 = kartu_tot_periode_3 + kartu_periode_3;
            } else {
                row_2[3] = 0.f;
            }
            if ((kartu_periode_1 + kartu_periode_2 + kartu_periode_3) > 0) {
                float avg = (brtKpg_periode_1 + brtKpg_periode_2 + brtKpg_periode_3) / (kartu_periode_1 + kartu_periode_2 + kartu_periode_3);
                row_2[4] = Math.round(avg * 100.f) / 100.f;
            } else {
                row_2[4] = 0.f;
            }
            model_2.addRow(row_2);

            row_2[0] = "AVG";
            row_2[1] = Math.round((brtKpg_tot_periode_1 / kartu_tot_periode_1) * 100.f) / 100.f;
            row_2[2] = Math.round((brtKpg_tot_periode_2 / kartu_tot_periode_2) * 100.f) / 100.f;
            row_2[3] = Math.round((brtKpg_tot_periode_3 / kartu_tot_periode_3) * 100.f) / 100.f;
            float avg = (brtKpg_tot_periode_1 + brtKpg_tot_periode_2 + brtKpg_tot_periode_3) / (kartu_tot_periode_1 + kartu_tot_periode_2 + kartu_tot_periode_3);
            row_2[4] = Math.round(avg * 100.f) / 100.f;;
            model_2.addRow(row_2);

            //TABEL 3
            row_3[0] = this_year;
            row_3[1] = bonus_periode_1;
            row_3[2] = bonus_periode_2;
            row_3[3] = bonus_periode_3;
            row_3[4] = bonus_periode_1 + bonus_periode_2 + bonus_periode_3;
            bonus_tot_periode_1 = bonus_tot_periode_1 + bonus_periode_1;
            bonus_tot_periode_2 = bonus_tot_periode_2 + bonus_periode_2;
            bonus_tot_periode_3 = bonus_tot_periode_3 + bonus_periode_3;
            model_3.addRow(row_3);

            row_3[0] = "TOTAL";
            row_3[1] = bonus_tot_periode_1;
            row_3[2] = bonus_tot_periode_2;
            row_3[3] = bonus_tot_periode_3;
            row_3[4] = bonus_tot_periode_1 + bonus_tot_periode_2 + bonus_tot_periode_3;
            model_3.addRow(row_3);
//            decimalFormat.setGroupingUsed(true);
//            ColumnsAutoSizer.sizeColumnsToFit(Table_panen_tahunan);
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_BonusPetikRSB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_data_panen_bulanan() {
        try {
            DefaultTableModel model = (DefaultTableModel) Table_data_panen_bulanan.getModel();
            model.setRowCount(0);
            int jumlah_bulan = 12;
            int[] bulan = new int[jumlah_bulan];
            int[] tahun = new int[jumlah_bulan];
            bulan[0] = Integer.valueOf(new SimpleDateFormat("MM").format(today));
            tahun[0] = Integer.valueOf(new SimpleDateFormat("yyyy").format(today));
            for (int x = 1; x < jumlah_bulan; x++) {
                bulan[x] = bulan[0] - x;
                tahun[x] = tahun[0];
                if (bulan[x] < 1) {
                    bulan[x] = bulan[x] + 12;
                    tahun[x] = tahun[x] - 1;
                }
            }

            JTableHeader TableHeader = Table_data_panen_bulanan.getTableHeader();
            TableColumnModel TColumnModel = TableHeader.getColumnModel();
            String header_bulan = "";
            DateFormatSymbols dfs = new DateFormatSymbols();
            String[] months = dfs.getMonths();

            for (int x = 0; x < jumlah_bulan; x++) {
                header_bulan = months[bulan[x] - 1].substring(0, 3) + " " + tahun[x];
                TableColumn tc = TColumnModel.getColumn(x + 1);
                tc.setHeaderValue(header_bulan);
            }
            Table_data_panen_bulanan.repaint();

            sql = "SELECT `tb_rumah_burung`.`nama_rumah_burung`, MONTH(`tgl_panen`) AS 'bulan', YEAR(`tgl_panen`) AS 'tahun', "
                    + "SUM(`total_berat`) AS 'berat', "
                    + "SUM(`tb_grading_bahan_baku`.`jumlah_keping` * `tb_bonus_petik_rsb`.`bonus_per_kpg`) AS 'bonus'\n"
                    + "FROM `tb_bahan_baku_masuk` \n"
                    + "LEFT JOIN `tb_rumah_burung` ON `tb_bahan_baku_masuk`.`no_registrasi` = `tb_rumah_burung`.`no_registrasi`\n"
                    + "LEFT JOIN `tb_grading_bahan_baku` ON `tb_bahan_baku_masuk`.`no_kartu_waleta` = `tb_grading_bahan_baku`.`no_kartu_waleta` \n"
                    + "LEFT JOIN `tb_grade_bahan_baku` ON `tb_grading_bahan_baku`.`kode_grade` = `tb_grade_bahan_baku`.`kode_grade`\n"
                    + "LEFT JOIN `tb_bonus_petik_rsb` ON `tb_grade_bahan_baku`.`kode_bonus_rsb` = `tb_bonus_petik_rsb`.`kode_bonus`\n"
                    + "WHERE "
                    + "`kode_supplier` = 'P' AND "
                    + "`tb_bahan_baku_masuk`.`no_kartu_waleta` NOT LIKE '%CMP%' AND "
                    + "`tgl_panen` BETWEEN '" + tahun[jumlah_bulan - 1] + "-" + bulan[jumlah_bulan - 1] + "-01' AND '" + tahun[0] + "-" + bulan[0] + "-31' "
                    + "GROUP BY `tb_rumah_burung`.`nama_rumah_burung`, MONTH(`tgl_panen`)\n"
                    + "ORDER BY `nama_rumah_burung`, `tgl_panen` DESC";
//            System.out.println(sql);
            rs = Utility.db.getStatement().executeQuery(sql);
            int[] total_gram = new int[jumlah_bulan];
            Object[] row = new Object[jumlah_bulan + 1];
            String rsb = "";
            while (rs.next()) {
                if (!rsb.equals(rs.getString("nama_rumah_burung"))) {
                    if (!rsb.equals("")) {
                        model.addRow(row);
                    }
                    rsb = rs.getString("nama_rumah_burung");
                    row = new Object[jumlah_bulan + 1];
                }

                row[0] = rs.getString("nama_rumah_burung");
                for (int i = 0; i < jumlah_bulan; i++) {
                    if (rs.getInt("bulan") == bulan[i]) {
                        row[i + 1] = rs.getInt("berat");
                        total_gram[i] = total_gram[i] + rs.getInt("berat");
                    }
                }
            }

            row[0] = "TOTAL";
            for (int i = 0; i < jumlah_bulan; i++) {
                row[i + 1] = total_gram[i];
            }
            model.addRow(row);

//            ColumnsAutoSizer.sizeColumnsToFit(Table_data_panen_bulanan);
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_BonusPetikRSB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_data_panen_tahunan_rsb() {
        try {
            DefaultTableModel model = (DefaultTableModel) Table_data_panen_bulanan.getModel();
            model.setRowCount(0);
            decimalFormat.setMaximumFractionDigits(1);
            int this_year = Integer.valueOf(new SimpleDateFormat("yyyy").format(today));

            sql = "SELECT `tb_rumah_burung`.`nama_rumah_burung`, MONTH(`tgl_panen`) AS 'bulan', YEAR(`tgl_panen`) AS 'tahun', "
                    + "SUM(`total_berat`) AS 'berat', "
                    + "SUM(`tb_grading_bahan_baku`.`jumlah_keping` * `tb_bonus_petik_rsb`.`bonus_per_kpg`) AS 'bonus'\n"
                    + "FROM `tb_bahan_baku_masuk` \n"
                    + "LEFT JOIN `tb_rumah_burung` ON `tb_bahan_baku_masuk`.`no_registrasi` = `tb_rumah_burung`.`no_registrasi`\n"
                    + "LEFT JOIN `tb_grading_bahan_baku` ON `tb_bahan_baku_masuk`.`no_kartu_waleta` = `tb_grading_bahan_baku`.`no_kartu_waleta` \n"
                    + "LEFT JOIN `tb_grade_bahan_baku` ON `tb_grading_bahan_baku`.`kode_grade` = `tb_grade_bahan_baku`.`kode_grade`\n"
                    + "LEFT JOIN `tb_bonus_petik_rsb` ON `tb_grade_bahan_baku`.`kode_bonus_rsb` = `tb_bonus_petik_rsb`.`kode_bonus`\n"
                    + "WHERE "
                    + "`kode_supplier` = 'P' AND "
                    + "`tb_bahan_baku_masuk`.`no_kartu_waleta` NOT LIKE '%CMP%' AND "
                    + "YEAR(`tgl_panen`) >= " + (this_year - 3)
                    + " GROUP BY `tb_rumah_burung`.`nama_rumah_burung`, YEAR(`tgl_panen`)\n"
                    + "ORDER BY `nama_rumah_burung`, `tgl_panen` DESC";
//            System.out.println(sql);
            rs = Utility.db.getStatement().executeQuery(sql);
            int[] total_gram = new int[jumlah_tahun];
            Object[] row = new Object[jumlah_tahun + 2];
            ArrayList<Float> Last_Year_Weight = new ArrayList<>();
            String rsb = "";
            while (rs.next()) {
                if (!rsb.equals(rs.getString("nama_rumah_burung"))) {
                    if (!rsb.equals("")) {
                        model.addRow(row);
                    }
                    rsb = rs.getString("nama_rumah_burung");
                    row = new Object[jumlah_tahun + 2];
                }

                row[0] = null;
                row[1] = rs.getString("nama_rumah_burung");
                for (int i = this_year; i > this_year - 3; i--) {
                    if (rs.getInt("tahun") == i) {
                        row[(this_year - i) + 2] = decimalFormat.format(rs.getFloat("berat") / 1000.f);
                        total_gram[this_year - i] = total_gram[this_year - i] + rs.getInt("berat");
                    }
                }
            }
            model.addRow(row);

            for (int i = 0; i < Table_data_panen_bulanan.getRowCount(); i++) {
                String[] a = Table_data_panen_bulanan.getValueAt(i, 3).toString().split("\\(");
                float berat = Float.valueOf(a[0].replace(",", ""));
                try {
                    berat = (float) Table_data_panen_bulanan.getValueAt(i, 4);
                } catch (Exception e) {
                }
                Last_Year_Weight.add(berat);
            }

            row[0] = null;
            row[1] = "TOTAL";
            for (int i = 0; i < jumlah_tahun; i++) {
                row[i + 2] = Math.round(total_gram[i] / 100.f) / 10.f;
            }
            model.addRow(row);

            for (int i = 0; i < Table_data_panen_bulanan.getRowCount(); i++) {
                for (int j = 0; j < jumlah_tahun; j++) {
                    float a = 0, b = 0, persen = 0;
                    try {
                        a = Float.valueOf(Table_data_panen_bulanan.getValueAt(i, 2 + j).toString().replace(",", ""));
                    } catch (Exception e) {
                    }
                    try {
                        b = Float.valueOf(Table_data_panen_bulanan.getValueAt(i, 3 + j).toString().replace(",", ""));
                    } catch (Exception e) {
                    }
                    persen = ((a - b) / b) * 100.f;
                    if (b == 0) {
                        if (a > 0) {
                            persen = 100.f;
                        } else {
                            persen = 0.f;
                        }
                    }
                    String arrow = "";
                    if (persen > 0) {
                        arrow = "\u25B2";
                    } else if (persen == 0) {
                        arrow = "=";
                    } else if (persen < 0) {
                        arrow = "\u25BC";
                    }
                    decimalFormat.setMaximumFractionDigits(1);
                    Table_data_panen_bulanan.setValueAt(a + "(" + decimalFormat.format(persen) + "%)" + arrow, i, 2 + j);
                }
            }

            for (int i = 0; i < Last_Year_Weight.size(); i++) {
                for (int j = i; j < Last_Year_Weight.size(); j++) {
//                    System.out.println("i = " + i + ", j = " + j);
                    if (Last_Year_Weight.get(j) > Last_Year_Weight.get(i)) {
                        float save = Last_Year_Weight.get(i);
                        Last_Year_Weight.set(i, Last_Year_Weight.get(j));
                        Last_Year_Weight.set(j, save);
                        DefaultTableModel tabel = (DefaultTableModel) Table_data_panen_bulanan.getModel();
                        tabel.moveRow(j, j, i);
                        tabel.moveRow(i + 1, i + 1, j);
                    }
                }
            }

            for (int i = 1; i < Table_data_panen_bulanan.getRowCount(); i++) {
                Table_data_panen_bulanan.setValueAt(i, i - 1, 0);
            }

            Table_data_panen_bulanan.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                    if (column > 1) {
                        String[] a = Table_data_panen_bulanan.getValueAt(row, column).toString().split("\\(");
                        String[] b = a[1].split("%");
                        float persentase = Float.valueOf(b[0].replace(",", ""));
//                        System.out.println(persentase);
                        if (persentase > 0) {
                            if (!isSelected) {
                                comp.setForeground(Color.black);
                                comp.setBackground(new Color(102, 255, 102));
                            } else {
                                comp.setBackground(Table_data_panen_bulanan.getSelectionBackground());
                                comp.setForeground(Table_data_panen_bulanan.getSelectionForeground());
                            }
                        } else if (persentase < 0) {
                            if (!isSelected) {
                                comp.setForeground(Color.black);
                                comp.setBackground(new Color(255, 102, 102));
                            } else {
                                comp.setBackground(Table_data_panen_bulanan.getSelectionBackground());
                                comp.setForeground(Table_data_panen_bulanan.getSelectionForeground());
                            }
                        } else {
                            if (!isSelected) {
                                comp.setForeground(Color.black);
                                comp.setBackground(Color.lightGray);
                            } else {
                                comp.setBackground(Table_data_panen_bulanan.getSelectionBackground());
                                comp.setForeground(Table_data_panen_bulanan.getSelectionForeground());
                            }
                        }
                    } else {
                        if (isSelected) {
                            comp.setBackground(Table_data_panen_bulanan.getSelectionBackground());
                            comp.setForeground(Table_data_panen_bulanan.getSelectionForeground());
                        } else {
                            comp.setBackground(Table_data_panen_bulanan.getBackground());
                            comp.setForeground(Table_data_panen_bulanan.getForeground());
                        }
                    }
                    return comp;
                }
            });
            Table_data_panen_bulanan.repaint();

//            ColumnsAutoSizer.sizeColumnsToFit(Table_data_panen_bulanan);
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_BonusPetikRSB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //SLIDE 3 - DATA GRADE & DATA BONUS / KEPING
    public void refreshTable_grade_baku() {
        try {
            ComboBox_kategori_bonus.removeAllItems();
            rs = Utility.db.getStatement().executeQuery("SELECT DISTINCT(`kode_bonus_rsb`) AS 'kode_bonus' FROM `tb_grade_bahan_baku` WHERE 1");
            while (rs.next()) {
                ComboBox_kategori_bonus.addItem(rs.getString("kode_bonus"));
            }

            DefaultTableModel model = (DefaultTableModel) Table_data_grade.getModel();
            model.setRowCount(0);
            sql = "SELECT `kode_grade`, `kode_bonus_rsb` FROM `tb_grade_bahan_baku`"
                    + "WHERE `kode_grade` LIKE '%" + txt_search_grade.getText() + "%'";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[2];
            while (rs.next()) {
                row[0] = rs.getString("kode_grade");
                row[1] = rs.getString("kode_bonus_rsb");
                model.addRow(row);
            }
            decimalFormat.setGroupingUsed(true);
            label_total_grade.setText(decimalFormat.format(Table_data_grade.getRowCount()) + " Grade");
            ColumnsAutoSizer.sizeColumnsToFit(Table_data_grade);
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_BonusPetikRSB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_bonus_per_keping() {
        try {
            DefaultTableModel model = (DefaultTableModel) Table_data_bonus_per_keping.getModel();
            model.setRowCount(0);
            sql = "SELECT `kode_bonus`, `bonus_per_kpg` FROM `tb_bonus_petik_rsb` WHERE 1";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[2];
            while (rs.next()) {
                row[0] = rs.getString("kode_bonus");
                row[1] = rs.getInt("bonus_per_kpg");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_data_bonus_per_keping);
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_BonusPetikRSB.class.getName()).log(Level.SEVERE, null, ex);
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

        jPanel1 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel_Perbandingan = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        txt_filter_nokartu = new javax.swing.JTextField();
        Button_refresh = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        Date1 = new com.toedter.calendar.JDateChooser();
        Date2 = new com.toedter.calendar.JDateChooser();
        jPanel2 = new javax.swing.JPanel();
        label_total_gram_grading = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        label_no_kartu = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        Table_detail_grading = new javax.swing.JTable();
        label_total_bonus_grading = new javax.swing.JLabel();
        label_total_kpg_grading = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        label_total_detail_grade = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        label_total_gram_kartu = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        Table_bonus_kartu = new javax.swing.JTable();
        jLabel7 = new javax.swing.JLabel();
        label_total_kpg_kartu = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        label_total_kartu = new javax.swing.JLabel();
        label_total_bonus_kartu = new javax.swing.JLabel();
        label_rsb1 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        label_total_gram_rsb = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        Table_bonus_rsb = new javax.swing.JTable();
        jLabel9 = new javax.swing.JLabel();
        label_total_kpg_rsb = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        label_total_rsb = new javax.swing.JLabel();
        label_total_bonus_rsb = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        Table_bonus_rekap = new javax.swing.JTable();
        jLabel27 = new javax.swing.JLabel();
        label_total_bonus_rekap = new javax.swing.JLabel();
        label_rsb2 = new javax.swing.JLabel();
        jPanel_data_tahunan_bulanan = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        Table_data_panen_tahunan = new javax.swing.JTable();
        jPanel10 = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        Table_data_bonus_tahunan = new javax.swing.JTable();
        Button_refresh_panen_tahunan = new javax.swing.JButton();
        jPanel11 = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        Table_data_beratKpg_tahunan = new javax.swing.JTable();
        jPanel13 = new javax.swing.JPanel();
        jScrollPane10 = new javax.swing.JScrollPane();
        Table_data_panen_bulanan = new javax.swing.JTable();
        jPanel_data_bonus_per_keping = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jScrollPane9 = new javax.swing.JScrollPane();
        Table_data_grade = new javax.swing.JTable();
        jLabel16 = new javax.swing.JLabel();
        txt_search_grade = new javax.swing.JTextField();
        Button_search_grade = new javax.swing.JButton();
        label_total_grade = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        txt_grade = new javax.swing.JTextField();
        ComboBox_kategori_bonus = new javax.swing.JComboBox<>();
        Button_edit_kategori_bonus = new javax.swing.JButton();
        jPanel14 = new javax.swing.JPanel();
        jScrollPane11 = new javax.swing.JScrollPane();
        Table_data_bonus_per_keping = new javax.swing.JTable();
        button_new_data_bonus = new javax.swing.JButton();
        button_edit_data_bonus = new javax.swing.JButton();
        button_delete_data_bonus = new javax.swing.JButton();
        jLabel21 = new javax.swing.JLabel();
        txt_kategori_bonus = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        txt_nominal_bonus = new javax.swing.JTextField();

        setBackground(new java.awt.Color(255, 255, 255));
        setPreferredSize(new java.awt.Dimension(1366, 700));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Perhitungan Bonus Petik Rumah Burung", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N
        jPanel1.setPreferredSize(new java.awt.Dimension(1366, 652));

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jPanel_Perbandingan.setBackground(new java.awt.Color(255, 255, 255));

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel12.setText("Tanggal Petik :");

        txt_filter_nokartu.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_filter_nokartu.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_filter_nokartuKeyPressed(evt);
            }
        });

        Button_refresh.setBackground(new java.awt.Color(255, 255, 255));
        Button_refresh.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Button_refresh.setText("Refresh");
        Button_refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_refreshActionPerformed(evt);
            }
        });

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel14.setText("No Kartu :");

        Date1.setDateFormatString("dd MMMM yyyy");
        Date1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        Date2.setDateFormatString("dd MMMM yyyy");
        Date2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        label_total_gram_grading.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_grading.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_gram_grading.setText("0 Gram");

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel8.setText("TOTAL GRADE :");

        label_no_kartu.setBackground(new java.awt.Color(255, 255, 255));
        label_no_kartu.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_no_kartu.setForeground(new java.awt.Color(255, 0, 0));
        label_no_kartu.setText("No Kartu");

        Table_detail_grading.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Table_detail_grading.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Grade", "Kpg", "Gram", "Bonus/Kpg", "Total"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
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
        Table_detail_grading.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(Table_detail_grading);

        label_total_bonus_grading.setBackground(new java.awt.Color(255, 255, 255));
        label_total_bonus_grading.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_bonus_grading.setText("Rp. 00000000");

        label_total_kpg_grading.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kpg_grading.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_kpg_grading.setText("0 Keping");

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel6.setText("TOTAL BONUS :");

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel4.setText("TOTAL GRAM :");

        label_total_detail_grade.setBackground(new java.awt.Color(255, 255, 255));
        label_total_detail_grade.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_detail_grade.setText("0");

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel5.setText("TOTAL KEPING :");

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel17.setText("Detail Grading");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 383, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel17)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_no_kartu))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(label_total_gram_grading)
                                    .addComponent(label_total_kpg_grading)
                                    .addComponent(label_total_bonus_grading)
                                    .addComponent(label_total_detail_grade))))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(label_no_kartu))
                .addGap(6, 6, 6)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 476, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(label_total_detail_grade)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_kpg_grading)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gram_grading)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_bonus_grading)))
                .addContainerGap())
        );

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        label_total_gram_kartu.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_kartu.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_gram_kartu.setText("0 Gram");

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel13.setText("Bonus Per Kartu Waleta");

        Table_bonus_kartu.setAutoCreateRowSorter(true);
        Table_bonus_kartu.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        Table_bonus_kartu.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No Kartu", "RSB", "Tgl Masuk", "Tgl Petik", "Kpg", "Berat", "Bonus"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
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
        Table_bonus_kartu.setMaximumSize(new java.awt.Dimension(420, 0));
        Table_bonus_kartu.setRowHeight(20);
        Table_bonus_kartu.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(Table_bonus_kartu);

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel7.setText("TOTAL KARTU :");

        label_total_kpg_kartu.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kpg_kartu.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_kpg_kartu.setText("0 Keping");

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setText("TOTAL KEPING :");

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel3.setText("TOTAL BONUS :");

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel1.setText("TOTAL GRAM :");

        label_total_kartu.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kartu.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_kartu.setText("0");

        label_total_bonus_kartu.setBackground(new java.awt.Color(255, 255, 255));
        label_total_bonus_kartu.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_bonus_kartu.setText("Rp. 0");

        label_rsb1.setBackground(new java.awt.Color(255, 255, 255));
        label_rsb1.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_rsb1.setForeground(new java.awt.Color(255, 0, 0));
        label_rsb1.setText("RSB");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 542, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(label_total_gram_kartu)
                                    .addComponent(label_total_kpg_kartu)
                                    .addComponent(label_total_bonus_kartu)))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_kartu))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_rsb1)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(label_rsb1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_total_kartu)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_total_kpg_kartu)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_total_gram_kartu)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_total_bonus_kartu)
                    .addComponent(jLabel3))
                .addContainerGap())
        );

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        label_total_gram_rsb.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_rsb.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_gram_rsb.setText("0 Gram");

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel18.setText("Bonus Per Rumah Burung");

        Table_bonus_rsb.setAutoCreateRowSorter(true);
        Table_bonus_rsb.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        Table_bonus_rsb.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "RSB", "Kpg", "Berat", "Bonus"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
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
        Table_bonus_rsb.setMaximumSize(new java.awt.Dimension(420, 0));
        Table_bonus_rsb.setRowHeight(20);
        Table_bonus_rsb.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(Table_bonus_rsb);

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel9.setText("TOTAL RSB :");

        label_total_kpg_rsb.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kpg_rsb.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_kpg_rsb.setText("0 Keping");

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel10.setText("TOTAL KEPING :");

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel11.setText("TOTAL BONUS :");

        jLabel19.setBackground(new java.awt.Color(255, 255, 255));
        jLabel19.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel19.setText("TOTAL GRAM :");

        label_total_rsb.setBackground(new java.awt.Color(255, 255, 255));
        label_total_rsb.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_rsb.setText("0");

        label_total_bonus_rsb.setBackground(new java.awt.Color(255, 255, 255));
        label_total_bonus_rsb.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_bonus_rsb.setText("Rp. 000.000.000");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel18)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 348, Short.MAX_VALUE)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(label_total_kpg_rsb)
                            .addComponent(label_total_rsb)
                            .addComponent(label_total_bonus_rsb)
                            .addComponent(label_total_gram_rsb))))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_total_rsb)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_total_kpg_rsb)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_total_gram_rsb)
                    .addComponent(jLabel19))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_total_bonus_rsb)
                    .addComponent(jLabel11))
                .addContainerGap())
        );

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel24.setBackground(new java.awt.Color(255, 255, 255));
        jLabel24.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel24.setText("Detail Bonus RSB");

        Table_bonus_rekap.setAutoCreateRowSorter(true);
        Table_bonus_rekap.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        Table_bonus_rekap.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kategori", "Kpg", "Bonus", "Total"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
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
        Table_bonus_rekap.setMaximumSize(new java.awt.Dimension(420, 0));
        Table_bonus_rekap.setRowHeight(20);
        Table_bonus_rekap.getTableHeader().setReorderingAllowed(false);
        jScrollPane5.setViewportView(Table_bonus_rekap);
        if (Table_bonus_rekap.getColumnModel().getColumnCount() > 0) {
            Table_bonus_rekap.getColumnModel().getColumn(3).setHeaderValue("Status");
        }

        jLabel27.setBackground(new java.awt.Color(255, 255, 255));
        jLabel27.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel27.setText("TOTAL BONUS :");

        label_total_bonus_rekap.setBackground(new java.awt.Color(255, 255, 255));
        label_total_bonus_rekap.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_bonus_rekap.setText("Rp. 0");

        label_rsb2.setBackground(new java.awt.Color(255, 255, 255));
        label_rsb2.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_rsb2.setForeground(new java.awt.Color(255, 0, 0));
        label_rsb2.setText("RSB");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel24)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_rsb2)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_bonus_rekap)))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24)
                    .addComponent(label_rsb2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 175, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_total_bonus_rekap)
                    .addComponent(jLabel27))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel_PerbandinganLayout = new javax.swing.GroupLayout(jPanel_Perbandingan);
        jPanel_Perbandingan.setLayout(jPanel_PerbandinganLayout);
        jPanel_PerbandinganLayout.setHorizontalGroup(
            jPanel_PerbandinganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_PerbandinganLayout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_PerbandinganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel_PerbandinganLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_filter_nokartu, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Date1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Date2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Button_refresh)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel_PerbandinganLayout.setVerticalGroup(
            jPanel_PerbandinganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_PerbandinganLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_PerbandinganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_filter_nokartu, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Button_refresh, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_PerbandinganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel_PerbandinganLayout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );

        jTabbedPane1.addTab("DATA DETAIL", jPanel_Perbandingan);

        jPanel_data_tahunan_bulanan.setBackground(new java.awt.Color(255, 255, 255));

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));
        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Berat Panen Tahunan", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 14))); // NOI18N

        Table_data_panen_tahunan.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        Table_data_panen_tahunan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"2021", null, null, null, null},
                {"2020", null, null, null, null},
                {"2019", null, null, null, null}
            },
            new String [] {
                "Tahun", "I (Jan-Apr)", "II (May-Aug)", "III (Sep-Dec)", "TOTAL"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
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
        Table_data_panen_tahunan.setRowHeight(20);
        Table_data_panen_tahunan.getTableHeader().setReorderingAllowed(false);
        jScrollPane6.setViewportView(Table_data_panen_tahunan);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane6)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );

        jPanel10.setBackground(new java.awt.Color(255, 255, 255));
        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Bonus Panen Tahunan", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 14))); // NOI18N

        Table_data_bonus_tahunan.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        Table_data_bonus_tahunan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"2021", null, null, null, null},
                {"2020", null, null, null, null},
                {"2019", null, null, null, null},
                {"TOTAL", null, null, null, null}
            },
            new String [] {
                "Tahun", "I (Jan-Apr)", "II (May-Aug)", "III (Sep-Dec)", "TOTAL"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
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
        Table_data_bonus_tahunan.setRowHeight(20);
        Table_data_bonus_tahunan.getTableHeader().setReorderingAllowed(false);
        jScrollPane7.setViewportView(Table_data_bonus_tahunan);

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane7)
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );

        Button_refresh_panen_tahunan.setBackground(new java.awt.Color(255, 255, 255));
        Button_refresh_panen_tahunan.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        Button_refresh_panen_tahunan.setText("Refresh");
        Button_refresh_panen_tahunan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_refresh_panen_tahunanActionPerformed(evt);
            }
        });

        jPanel11.setBackground(new java.awt.Color(255, 255, 255));
        jPanel11.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Berat / Kpg Panen Tahunan", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 14))); // NOI18N

        Table_data_beratKpg_tahunan.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        Table_data_beratKpg_tahunan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"2021", null, null, null, null},
                {"2020", null, null, null, null},
                {"2019", null, null, null, null},
                {"AVG", null, null, null, null}
            },
            new String [] {
                "Tahun", "I (Jan-Apr)", "II (May-Aug)", "III (Sep-Dec)", "AVG"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
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
        Table_data_beratKpg_tahunan.setRowHeight(20);
        Table_data_beratKpg_tahunan.getTableHeader().setReorderingAllowed(false);
        jScrollPane8.setViewportView(Table_data_beratKpg_tahunan);

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 490, Short.MAX_VALUE)
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );

        jPanel13.setBackground(new java.awt.Color(255, 255, 255));
        jPanel13.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Berat Panen Tahunan Setiap RSB", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 14))); // NOI18N

        Table_data_panen_bulanan.setAutoCreateRowSorter(true);
        Table_data_panen_bulanan.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        Table_data_panen_bulanan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "RSB", "", "", ""
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
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
        Table_data_panen_bulanan.setRowHeight(20);
        Table_data_panen_bulanan.setRowSelectionAllowed(false);
        Table_data_panen_bulanan.getTableHeader().setReorderingAllowed(false);
        jScrollPane10.setViewportView(Table_data_panen_bulanan);
        if (Table_data_panen_bulanan.getColumnModel().getColumnCount() > 0) {
            Table_data_panen_bulanan.getColumnModel().getColumn(0).setMinWidth(30);
            Table_data_panen_bulanan.getColumnModel().getColumn(0).setMaxWidth(40);
            Table_data_panen_bulanan.getColumnModel().getColumn(1).setResizable(false);
            Table_data_panen_bulanan.getColumnModel().getColumn(1).setPreferredWidth(160);
        }

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane10, javax.swing.GroupLayout.DEFAULT_SIZE, 825, Short.MAX_VALUE)
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane10, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 572, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel_data_tahunan_bulananLayout = new javax.swing.GroupLayout(jPanel_data_tahunan_bulanan);
        jPanel_data_tahunan_bulanan.setLayout(jPanel_data_tahunan_bulananLayout);
        jPanel_data_tahunan_bulananLayout.setHorizontalGroup(
            jPanel_data_tahunan_bulananLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_data_tahunan_bulananLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_data_tahunan_bulananLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_data_tahunan_bulananLayout.createSequentialGroup()
                        .addComponent(Button_refresh_panen_tahunan)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel_data_tahunan_bulananLayout.createSequentialGroup()
                        .addGroup(jPanel_data_tahunan_bulananLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jPanel11, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel_data_tahunan_bulananLayout.setVerticalGroup(
            jPanel_data_tahunan_bulananLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_data_tahunan_bulananLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(Button_refresh_panen_tahunan)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_data_tahunan_bulananLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_data_tahunan_bulananLayout.createSequentialGroup()
                        .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("DATA Tahunan Rumah Burung", jPanel_data_tahunan_bulanan);

        jPanel_data_bonus_per_keping.setBackground(new java.awt.Color(255, 255, 255));

        jPanel12.setBackground(new java.awt.Color(255, 255, 255));
        jPanel12.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Grade Baku", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N

        Table_data_grade.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        Table_data_grade.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Grade", "Kategori Bonus"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Table_data_grade.setRowHeight(20);
        Table_data_grade.getTableHeader().setReorderingAllowed(false);
        jScrollPane9.setViewportView(Table_data_grade);

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel16.setText("Search Grade :");

        txt_search_grade.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_search_grade.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_gradeKeyPressed(evt);
            }
        });

        Button_search_grade.setBackground(new java.awt.Color(255, 255, 255));
        Button_search_grade.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Button_search_grade.setText("Search");
        Button_search_grade.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_search_gradeActionPerformed(evt);
            }
        });

        label_total_grade.setBackground(new java.awt.Color(255, 255, 255));
        label_total_grade.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_grade.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_total_grade.setText("0 Grade");

        jLabel20.setBackground(new java.awt.Color(255, 255, 255));
        jLabel20.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel20.setText("Grade :");

        txt_grade.setEditable(false);
        txt_grade.setBackground(new java.awt.Color(255, 255, 255));
        txt_grade.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        ComboBox_kategori_bonus.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Button_edit_kategori_bonus.setBackground(new java.awt.Color(255, 255, 255));
        Button_edit_kategori_bonus.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Button_edit_kategori_bonus.setText("Edit");
        Button_edit_kategori_bonus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_edit_kategori_bonusActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel12Layout.createSequentialGroup()
                                .addComponent(jLabel20)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_grade, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_kategori_bonus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Button_edit_kategori_bonus))
                            .addGroup(jPanel12Layout.createSequentialGroup()
                                .addComponent(jLabel16)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_grade, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Button_search_grade)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_grade, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_grade, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(Button_search_grade, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_grade, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_grade, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_kategori_bonus, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Button_edit_kategori_bonus, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 548, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel14.setBackground(new java.awt.Color(255, 255, 255));
        jPanel14.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Bonus / Keping", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N

        Table_data_bonus_per_keping.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        Table_data_bonus_per_keping.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kategori Bonus", "Bonus / Keping"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Table_data_bonus_per_keping.setRowHeight(20);
        Table_data_bonus_per_keping.getTableHeader().setReorderingAllowed(false);
        jScrollPane11.setViewportView(Table_data_bonus_per_keping);

        button_new_data_bonus.setBackground(new java.awt.Color(255, 255, 255));
        button_new_data_bonus.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_new_data_bonus.setText("New");
        button_new_data_bonus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_new_data_bonusActionPerformed(evt);
            }
        });

        button_edit_data_bonus.setBackground(new java.awt.Color(255, 255, 255));
        button_edit_data_bonus.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_edit_data_bonus.setText("Edit");
        button_edit_data_bonus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_edit_data_bonusActionPerformed(evt);
            }
        });

        button_delete_data_bonus.setBackground(new java.awt.Color(255, 255, 255));
        button_delete_data_bonus.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_delete_data_bonus.setText("Delete");
        button_delete_data_bonus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_delete_data_bonusActionPerformed(evt);
            }
        });

        jLabel21.setBackground(new java.awt.Color(255, 255, 255));
        jLabel21.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel21.setText("Kategori :");

        txt_kategori_bonus.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel22.setBackground(new java.awt.Color(255, 255, 255));
        jLabel22.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel22.setText("Bonus :");

        txt_nominal_bonus.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_nominal_bonus.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_nominal_bonusKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel14Layout.createSequentialGroup()
                        .addComponent(button_edit_data_bonus)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_new_data_bonus)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_delete_data_bonus))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel14Layout.createSequentialGroup()
                        .addComponent(jLabel21)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_kategori_bonus, javax.swing.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel22)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_nominal_bonus, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_edit_data_bonus, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_new_data_bonus, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_delete_data_bonus, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_kategori_bonus, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_nominal_bonus, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane11, javax.swing.GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel_data_bonus_per_kepingLayout = new javax.swing.GroupLayout(jPanel_data_bonus_per_keping);
        jPanel_data_bonus_per_keping.setLayout(jPanel_data_bonus_per_kepingLayout);
        jPanel_data_bonus_per_kepingLayout.setHorizontalGroup(
            jPanel_data_bonus_per_kepingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_data_bonus_per_kepingLayout.createSequentialGroup()
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 622, Short.MAX_VALUE))
        );
        jPanel_data_bonus_per_kepingLayout.setVerticalGroup(
            jPanel_data_bonus_per_kepingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel_data_bonus_per_kepingLayout.createSequentialGroup()
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("DATA Grade & Bonus / Keping", jPanel_data_bonus_per_keping);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 700, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txt_filter_nokartuKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_filter_nokartuKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_bonus_rsb();
        }
    }//GEN-LAST:event_txt_filter_nokartuKeyPressed

    private void Button_refreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_refreshActionPerformed
        // TODO add your handling code here:
        refreshTable_bonus_rsb();
    }//GEN-LAST:event_Button_refreshActionPerformed

    private void Button_refresh_panen_tahunanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_refresh_panen_tahunanActionPerformed
        // TODO add your handling code here:
        refreshTable_data_panen_tahunan();
        refreshTable_data_panen_tahunan_rsb();
//        refreshTable_data_panen_bulanan();
    }//GEN-LAST:event_Button_refresh_panen_tahunanActionPerformed

    private void button_new_data_bonusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_new_data_bonusActionPerformed
        // TODO add your handling code here:
        try {
            int bonus = Integer.valueOf(txt_nominal_bonus.getText());
            String Query = "INSERT INTO `tb_bonus_petik_rsb`(`kode_bonus`, `bonus_per_kpg`) VALUES ('" + txt_kategori_bonus.getText() + "','" + bonus + "')";
            Utility.db.getConnection().createStatement();
            if ((Utility.db.getStatement().executeUpdate(Query)) == 1) {
                refreshTable_bonus_per_keping();
            } else {
                JOptionPane.showMessageDialog(this, "Insert failed !");
            }
        } catch (SQLException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_BonusPetikRSB.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_new_data_bonusActionPerformed

    private void button_edit_data_bonusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_edit_data_bonusActionPerformed
        try {
            int bonus = Integer.valueOf(txt_nominal_bonus.getText());
            String Query = "UPDATE `tb_bonus_petik_rsb` SET `bonus_per_kpg`='" + bonus + "' WHERE `kode_bonus`='" + txt_kategori_bonus.getText() + "'";
            Utility.db.getConnection().createStatement();
            if ((Utility.db.getStatement().executeUpdate(Query)) == 1) {
                refreshTable_bonus_per_keping();
            } else {
                JOptionPane.showMessageDialog(this, "Edit failed !");
            }
        } catch (SQLException | NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_BonusPetikRSB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_edit_data_bonusActionPerformed

    private void button_delete_data_bonusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_delete_data_bonusActionPerformed
        // TODO add your handling code here:
        try {
            int j = Table_data_bonus_per_keping.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Klik data yang akan di hapus");
            } else {
                int dialogResult = JOptionPane.showConfirmDialog(this, "Hapus data?", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    // delete code here
                    String Query = "DELETE FROM `tb_bonus_petik_rsb` WHERE `kode_bonus`='" + Table_data_bonus_per_keping.getValueAt(j, 0).toString() + "'";
                    Utility.db.getConnection().createStatement();
                    if ((Utility.db.getStatement().executeUpdate(Query)) == 1) {
                        refreshTable_bonus_per_keping();
                    } else {
                        JOptionPane.showMessageDialog(this, "Delete failed !");
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e);
        }
    }//GEN-LAST:event_button_delete_data_bonusActionPerformed

    private void txt_nominal_bonusKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_nominal_bonusKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyChar() != '.'
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_nominal_bonusKeyTyped

    private void txt_search_gradeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_gradeKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_grade_baku();
        }
    }//GEN-LAST:event_txt_search_gradeKeyPressed

    private void Button_search_gradeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_search_gradeActionPerformed
        // TODO add your handling code here:
        refreshTable_grade_baku();
    }//GEN-LAST:event_Button_search_gradeActionPerformed

    private void Button_edit_kategori_bonusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_edit_kategori_bonusActionPerformed
        // TODO add your handling code here:
        try {
            String Query = "UPDATE `tb_grade_bahan_baku` SET `kode_bonus_rsb`='" + ComboBox_kategori_bonus.getSelectedItem().toString() + "' WHERE `kode_grade`='" + txt_grade.getText() + "'";
            Utility.db.getConnection().createStatement();
            if ((Utility.db.getStatement().executeUpdate(Query)) == 1) {
                refreshTable_grade_baku();
            } else {
                JOptionPane.showMessageDialog(this, "Edit failed !");
            }
        } catch (SQLException | NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_BonusPetikRSB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_Button_edit_kategori_bonusActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Button_edit_kategori_bonus;
    private javax.swing.JButton Button_refresh;
    private javax.swing.JButton Button_refresh_panen_tahunan;
    private javax.swing.JButton Button_search_grade;
    private javax.swing.JComboBox<String> ComboBox_kategori_bonus;
    private com.toedter.calendar.JDateChooser Date1;
    private com.toedter.calendar.JDateChooser Date2;
    private javax.swing.JTable Table_bonus_kartu;
    private javax.swing.JTable Table_bonus_rekap;
    private javax.swing.JTable Table_bonus_rsb;
    private javax.swing.JTable Table_data_beratKpg_tahunan;
    private javax.swing.JTable Table_data_bonus_per_keping;
    private javax.swing.JTable Table_data_bonus_tahunan;
    private javax.swing.JTable Table_data_grade;
    private javax.swing.JTable Table_data_panen_bulanan;
    private javax.swing.JTable Table_data_panen_tahunan;
    private javax.swing.JTable Table_detail_grading;
    public javax.swing.JButton button_delete_data_bonus;
    public javax.swing.JButton button_edit_data_bonus;
    public javax.swing.JButton button_new_data_bonus;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel jPanel_Perbandingan;
    private javax.swing.JPanel jPanel_data_bonus_per_keping;
    private javax.swing.JPanel jPanel_data_tahunan_bulanan;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel label_no_kartu;
    private javax.swing.JLabel label_rsb1;
    private javax.swing.JLabel label_rsb2;
    private javax.swing.JLabel label_total_bonus_grading;
    private javax.swing.JLabel label_total_bonus_kartu;
    private javax.swing.JLabel label_total_bonus_rekap;
    private javax.swing.JLabel label_total_bonus_rsb;
    private javax.swing.JLabel label_total_detail_grade;
    private javax.swing.JLabel label_total_grade;
    private javax.swing.JLabel label_total_gram_grading;
    private javax.swing.JLabel label_total_gram_kartu;
    private javax.swing.JLabel label_total_gram_rsb;
    private javax.swing.JLabel label_total_kartu;
    private javax.swing.JLabel label_total_kpg_grading;
    private javax.swing.JLabel label_total_kpg_kartu;
    private javax.swing.JLabel label_total_kpg_rsb;
    private javax.swing.JLabel label_total_rsb;
    private javax.swing.JTextField txt_filter_nokartu;
    private javax.swing.JTextField txt_grade;
    private javax.swing.JTextField txt_kategori_bonus;
    private javax.swing.JTextField txt_nominal_bonus;
    private javax.swing.JTextField txt_search_grade;
    // End of variables declaration//GEN-END:variables

}
