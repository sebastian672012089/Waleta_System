package waleta_system.Finance;

import waleta_system.Class.Utility;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.DataF2;
import waleta_system.Class.ExportToExcel;

public class JPanel_BonusMangkok extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    Date date_setoran1, date_setoran2;

    public JPanel_BonusMangkok() {
        initComponents();
    }

    public void init() {
        refreshTable_Setoran();
        Table_LP_Bonus.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && Table_LP_Bonus.getSelectedRow() != -1) {
                    int i = Table_LP_Bonus.getSelectedRow();
                    if (i >= 0) {
                        refreshTable_Pencabut();
                        label_no_lp.setText(Table_LP_Bonus.getValueAt(i, 0).toString());
                    }
                }
            }
        });
    }

    public boolean check_TargetRendemen(String bentuk, String bulu, double utuh, double flat, double serabut, double sh) {
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
                    if (utuh > 70 && flat < 10 && sh < 6) {
                        return true;
                    }
                    break;
                case "BS":
                    if (utuh > 60 && flat < 15 && sh < 8) {
                        return true;
                    }
                    break;
                case "BB":
                    if (utuh > 45 && flat < 25 && sh < 10) {
                        return true;
                    }
                    break;
            }
        } else if (bentuk.contains("Oval") || bentuk.contains("Segitiga")) {
            switch (bulu) {
                case "BR":
                    if (utuh > 65 && flat < 15 && sh < 6) {
                        return true;
                    }
                    break;
                case "BS":
                    if (utuh > 55 && flat < 20 && sh < 8) {
                        return true;
                    }
                    break;
                case "BB":
                    if (utuh > 45 && flat < 30 && sh < 10) {
                        return true;
                    }
                    break;
            }
        } else {
            switch (bulu) {
                case "BR":
                    if (flat > 75 && sh < 6) {
                        return true;
                    }
                    break;
                case "BS":
                    if (flat > 70 && sh < 8) {
                        return true;
                    }
                    break;
                case "BB":
                    if (flat > 65 && sh < 10) {
                        return true;
                    }
                    break;
            }
        }
        return false;
    }

    public int upah_bonus_cabut(String bulu) {
        int upah_bonus_cabut = 0;
        if (null != bulu) {
            switch (bulu) {
                case "Bulu Ringan":
                    upah_bonus_cabut = 1500;
                    break;
                case "Bulu Ringan Sekali/Bulu Ringan":
                    upah_bonus_cabut = 1500;
                    break;
                case "Bulu Sedang":
                    upah_bonus_cabut = 2000;
                    break;
                case "Bulu Berat":
                    upah_bonus_cabut = 2500;
                    break;
                case "Bulu Berat Sekali":
                    upah_bonus_cabut = 2500;
                    break;
                default:
                    upah_bonus_cabut = 1500;
                    break;
            }
        }
        return upah_bonus_cabut;
    }

    public int upah_bonus_cetak(String bulu) {
        int upah_bonus_cetak = 0;
        if (null != bulu) {
            switch (bulu) {
                case "Bulu Ringan":
                    upah_bonus_cetak = 300;
                    break;
                case "Bulu Ringan Sekali/Bulu Ringan":
                    upah_bonus_cetak = 300;
                    break;
                case "Bulu Sedang":
                    upah_bonus_cetak = 600;
                    break;
                case "Bulu Berat":
                    upah_bonus_cetak = 800;
                    break;
                case "Bulu Berat Sekali":
                    upah_bonus_cetak = 800;
                    break;
                default:
                    upah_bonus_cetak = 300;
                    break;
            }
        }
        return upah_bonus_cetak;
    }

    public ArrayList<DataF2> SetoranHarianList() {
        ArrayList<DataF2> F2List = new ArrayList<>();
        try {

            String ruang = "";
            if (ComboBox_ruangan.getSelectedItem() != "All") {
                ruang = ComboBox_ruangan.getSelectedItem().toString();
            }
            if (date_setoran1 == null || date_setoran2 == null) {
                sql = "SELECT `tb_finishing_2`.`no_laporan_produksi`, `tb_karyawan`.`nama_pegawai`, `tb_laporan_produksi`.`tanggal_lp`, `tb_laporan_produksi`.`no_kartu_waleta`, `tb_laporan_produksi`.`jumlah_keping`, `tb_laporan_produksi`.`berat_kering`, `tb_laporan_produksi`.`memo_lp`, `tb_laporan_produksi`.`ruangan`, `tb_grade_bahan_baku`.`kode_grade`, `tb_grade_bahan_baku`.`jenis_bentuk`, `tb_grade_bahan_baku`.`jenis_bulu`, "
                        + "`tgl_input_sesekan`, `tgl_dikerjakan_f2`, `pekerja_koreksi_kering`, `tgl_f1`, `pekerja_f1`, `tgl_f2`, `pekerja_f2`, `tgl_masuk_f2`, `f2_diterima`, `tgl_setor_f2`, `f2_disetor`, `f2_timbang`, `fbonus_f2`, `berat_fbonus`, `fnol_f2`, `berat_fnol`, `pecah_f2`, `berat_pecah`, `flat_f2`, `berat_flat`, `jidun_utuh_f2`, `jidun_pecah_f2`, `berat_jidun`, `sesekan`, `hancuran`, `rontokan`, `bonggol`, `serabut`, `tanpa_kaki_f1`, `kaki_kecil_f1`, `kaki_besar_f1`, `flat_f1`, `tambahan_kaki1`, `lp_kaki1`, `tambahan_kaki2`, `lp_kaki2`, `admin_f2`, `otorisasi`, COUNT(`tb_detail_pencabut`.`id_pegawai`) AS 'total_pekerja' \n"
                        + "FROM `tb_finishing_2` "
                        + "LEFT JOIN `tb_laporan_produksi` ON `tb_finishing_2`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                        + "LEFT JOIN `tb_grade_bahan_baku` ON `tb_grade_bahan_baku`.`kode_grade` = `tb_laporan_produksi`.`kode_grade`\n"
                        + "LEFT JOIN `tb_cetak` ON `tb_cetak`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                        + "LEFT JOIN `tb_karyawan` ON `tb_karyawan`.`id_pegawai` = `tb_cetak`.`cetak_dikerjakan`\n"
                        + "LEFT JOIN `tb_detail_pencabut` ON `tb_detail_pencabut`.`no_laporan_produksi` = `tb_finishing_2`.`no_laporan_produksi`\n"
                        + "WHERE `tb_finishing_2`.`no_laporan_produksi` LIKE '%" + txt_no_lp.getText() + "%' AND `tb_laporan_produksi`.`ruangan` LIKE '%" + ruang + "%' \n"
                        + "GROUP BY `tb_finishing_2`.`no_laporan_produksi` ORDER BY `tb_finishing_2`.`tgl_setor_f2` DESC";
            } else {
                sql = "SELECT `tb_finishing_2`.`no_laporan_produksi`, `tb_karyawan`.`nama_pegawai`, `tb_laporan_produksi`.`tanggal_lp`, `tb_laporan_produksi`.`no_kartu_waleta`, `tb_laporan_produksi`.`jumlah_keping`, `tb_laporan_produksi`.`berat_kering`, `tb_laporan_produksi`.`memo_lp`, `tb_laporan_produksi`.`ruangan`, `tb_grade_bahan_baku`.`kode_grade`, `tb_grade_bahan_baku`.`jenis_bentuk`, `tb_grade_bahan_baku`.`jenis_bulu`, "
                        + "`tgl_input_sesekan`, `tgl_dikerjakan_f2`, `pekerja_koreksi_kering`, `tgl_f1`, `pekerja_f1`, `tgl_f2`, `pekerja_f2`, `tgl_masuk_f2`, `f2_diterima`, `tgl_setor_f2`, `f2_disetor`, `f2_timbang`, `fbonus_f2`, `berat_fbonus`, `fnol_f2`, `berat_fnol`, `pecah_f2`, `berat_pecah`, `flat_f2`, `berat_flat`, `jidun_utuh_f2`, `jidun_pecah_f2`, `berat_jidun`, `sesekan`, `hancuran`, `rontokan`, `bonggol`, `serabut`, `tanpa_kaki_f1`, `kaki_kecil_f1`, `kaki_besar_f1`, `flat_f1`, `tambahan_kaki1`, `lp_kaki1`, `tambahan_kaki2`, `lp_kaki2`, `admin_f2`, `otorisasi`, COUNT(`tb_detail_pencabut`.`id_pegawai`) AS 'total_pekerja' \n"
                        + "FROM `tb_finishing_2` "
                        + "LEFT JOIN `tb_laporan_produksi` ON `tb_finishing_2`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                        + "LEFT JOIN `tb_grade_bahan_baku` ON `tb_grade_bahan_baku`.`kode_grade` = `tb_laporan_produksi`.`kode_grade`\n"
                        + "LEFT JOIN `tb_cetak` ON `tb_cetak`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                        + "LEFT JOIN `tb_karyawan` ON `tb_karyawan`.`id_pegawai` = `tb_cetak`.`cetak_dikerjakan`\n"
                        + "LEFT JOIN `tb_detail_pencabut` ON `tb_detail_pencabut`.`no_laporan_produksi` = `tb_finishing_2`.`no_laporan_produksi`\n"
                        + "WHERE `tb_finishing_2`.`no_laporan_produksi` LIKE '%" + txt_no_lp.getText() + "%' AND `tb_laporan_produksi`.`ruangan` LIKE '%" + ruang + "%' \n"
                        + "AND `tb_finishing_2`.`tgl_setor_f2` BETWEEN '" + dateFormat.format(date_setoran1) + "' AND '" + dateFormat.format(date_setoran2) + "'\n"
                        + "GROUP BY `tb_finishing_2`.`no_laporan_produksi` ORDER BY `tb_finishing_2`.`tgl_setor_f2` DESC";
            }
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
                        null,
                        null,
                        rs.getDate("tgl_dikerjakan_f2"),
                        rs.getString("pekerja_koreksi_kering"),
                        rs.getDate("tgl_f1"),
                        rs.getString("pekerja_f1"),
                        rs.getDate("tgl_f2"),
                        rs.getString("pekerja_f2"),
                        rs.getDate("tanggal_lp"),
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
                        rs.getInt("tambahan_kaki1"),
                        rs.getString("lp_kaki1"),
                        rs.getInt("tambahan_kaki2"),
                        rs.getString("lp_kaki2"),
                        rs.getString("admin_f2"),
                        rs.getString("otorisasi"),
                        rs.getString("total_pekerja")
                );
                F2List.add(f2);
            }
        } catch (Exception ex) {
            Logger.getLogger(JPanel_BonusMangkok.class.getName()).log(Level.SEVERE, null, ex);
        }
        return F2List;
    }

    public void show_data_bonusMangkok() {
//        decimalFormat.setGroupingUsed(false);
//        decimalFormat.setMaximumFractionDigits(2);
//        decimalFormat.setGroupingUsed(true);
        ArrayList<DataF2> list = SetoranHarianList();
        DefaultTableModel model = (DefaultTableModel) Table_LP_Bonus.getModel();
        Object[] row = new Object[35];
        int total_lp_get_Bonus = 0;
        double bonus_cabut, bonus_cetak, bonus_f2, total_bonus_cabut = 0, total_bonus_cetak = 0, total_bonus_f2 = 0;
        double bk, fbonus, fbonus_kpg, fnol, fnol_kpg, pch, pch_kpg, flat, flat_kpg, sh, sp, jidun, kaki, utuh, netto_utuh, netto_jidun;
        double rend_mk, rend_pch, rend_flat, rend_sh, rend_sp, rend_total, rend_jidun;
        for (int i = 0; i < list.size(); i++) {
            bk = list.get(i).getBerat_Kering();
            fbonus = list.get(i).getFbonus_berat();
            fbonus_kpg = list.get(i).getFbonus_keping();
            fnol = list.get(i).getFnol_berat();
            fnol_kpg = list.get(i).getFnol_keping();
            jidun = list.get(i).getJidun_berat();
            pch = list.get(i).getPecah_berat();
            pch_kpg = list.get(i).getPecah_keping();
            flat = list.get(i).getFlat_berat();
            flat_kpg = list.get(i).getFlat_keping();
            kaki = list.get(i).getTambah_kaki1() + list.get(i).getTambah_kaki2();
            utuh = fbonus + fnol;
            netto_utuh = utuh - kaki;
            if (jidun > utuh) {
                netto_jidun = jidun - kaki;
                netto_utuh = utuh;
            } else {
                netto_utuh = utuh - kaki;
                netto_jidun = jidun;
            }
            sp = list.get(i).getSesekan() + list.get(i).getHancuran() + list.get(i).getRontokan() + list.get(i).getBonggol() + list.get(i).getSerabut();
            sh = bk - (netto_utuh + pch + flat + sp + netto_jidun);

            rend_mk = (netto_utuh / bk) * 100;
            rend_pch = (pch / bk) * 100;
            rend_flat = (flat / bk) * 100;
            rend_sh = (sh / bk) * 100;
            rend_sp = (sp / bk) * 100;
            rend_jidun = (netto_jidun / bk) * 100;
            rend_total = rend_mk + rend_pch + rend_flat + rend_sh + rend_sp + rend_jidun;

            row[0] = list.get(i).getNo_lp();
            row[1] = list.get(i).getGrade_bk();
            row[2] = list.get(i).getRuangan();
            row[3] = list.get(i).getJumlah_keping();
            row[4] = list.get(i).getTgl_mulai_f2();
            row[5] = list.get(i).getTgl_setor_f2();
            row[6] = list.get(i).getFbonus_keping();
            row[7] = list.get(i).getFnol_keping();
            row[8] = list.get(i).getPecah_keping() + list.get(i).getFlat_keping();
            row[9] = list.get(i).getFbonus_keping() - (list.get(i).getPecah_keping() + list.get(i).getFlat_keping());
            double persen_bonus = (double) (fbonus_kpg / (fbonus_kpg + fnol_kpg + pch_kpg + flat_kpg)) * (double) 100;
            row[10] = Math.round(persen_bonus * 100) / 100.0d;
            row[11] = Math.round(rend_mk * 100) / 100.0d;
            row[12] = Math.round(rend_jidun * 100) / 100.0d;
            row[13] = Math.round((rend_pch + rend_flat) * 100) / 100.0d;
            row[14] = Math.round(rend_sh * 100) / 100.0d;
            row[15] = Math.round(rend_sp * 100) / 100.0d;

            if (check_TargetRendemen(list.get(i).getGrade_bentuk(), list.get(i).getGrade_bulu(), rend_mk, rend_pch + rend_flat, 0, rend_sh)) {
                row[16] = "YES";
            } else {
                row[16] = "NO";
            }

            bonus_cabut = (fbonus_kpg - (pch_kpg + flat_kpg)) * upah_bonus_cabut(list.get(i).getGrade_bulu());
            bonus_cetak = (fbonus_kpg - (pch_kpg + flat_kpg)) * upah_bonus_cetak(list.get(i).getGrade_bulu());
            //untuk bonus berdasarkan rendemen
            /*if (bonus_cabut > 0 && check_TargetRendemen(list.get(i).getGrade_bentuk(), list.get(i).getGrade_bulu(), rend_mk, rend_pch + rend_flat, 0, rend_sh)) {
                row[14] = bonus_cabut;
                row[15] = bonus_cetak;
                total_bonus_cabut = total_bonus_cabut + bonus_cabut;
                total_bonus_cetak = total_bonus_cetak + bonus_cetak;
                total_lp_get_Bonus++;
            } else {
                row[14] = 0.0;
                row[15] = 0.0;
            }*/

            String pekerja_cabut = null;
            String ketua_grup_cabut = null;
            try {
                String query = "SELECT `tb_karyawan`.`nama_pegawai`, `tb_cabut`.`ketua_regu` FROM `tb_detail_pencabut` "
                        + "LEFT JOIN `tb_karyawan` ON `tb_detail_pencabut`.`id_pegawai` = `tb_karyawan`.`id_pegawai`"
                        + "LEFT JOIN `tb_cabut` ON `tb_detail_pencabut`.`no_laporan_produksi` = `tb_cabut`.`no_laporan_produksi`"
                        + "WHERE `tb_detail_pencabut`.`no_laporan_produksi` = '" + list.get(i).getNo_lp() + "'";
//                System.out.println(query);
                ResultSet rs1 = Utility.db.getStatement().executeQuery(query);
                if (rs1.next()) {
                    pekerja_cabut = rs1.getString("nama_pegawai");
                    ketua_grup_cabut = rs1.getString("ketua_regu");
                }
            } catch (SQLException ex) {
                Logger.getLogger(JPanel_BonusMangkok.class.getName()).log(Level.SEVERE, null, ex);
            }

            row[22] = list.get(i).getPekerja_koreksi();
            row[23] = list.get(i).getPekerja_f1();
            row[24] = list.get(i).getPekerja_f2();
            row[25] = list.get(i).getF2_disetor();
            row[26] = list.get(i).getKeterangan();
            double f1 = bonus_cabut * Double.valueOf(txt_persen_bonus_f1.getText()) / 100d;
            double f2 = bonus_cabut * Double.valueOf(txt_persen_bonus_f2.getText()) / 100d;
            double finalCheck = bonus_cabut * Double.valueOf(txt_persen_bonus_finalCheck.getText()) / 100d;
            row[27] = f1;//bonus f1
            row[28] = f2;//bonus f2
            row[29] = finalCheck;//bonus final check
            bonus_f2 = f1 + f2 + finalCheck;//bonus f1, bonus f2, bonus final check
            if (CheckBox_Show_BonusOnly.isSelected()) {
                if (bonus_cabut > 0) {
                    row[17] = bonus_cabut;
                    row[18] = bonus_cetak;
                    row[19] = list.get(i).getCetak();
                    row[20] = pekerja_cabut;
                    row[21] = ketua_grup_cabut;
                    total_bonus_cabut = total_bonus_cabut + bonus_cabut;
                    total_bonus_cetak = total_bonus_cetak + bonus_cetak;
                    total_bonus_f2 = total_bonus_f2 + bonus_f2;
                    total_lp_get_Bonus++;
                    model.addRow(row);
                }
            } else {
                if (bonus_cabut > 0) {
                    row[17] = (int) bonus_cabut;
                    row[18] = (int) bonus_cetak;
                    total_bonus_cabut = total_bonus_cabut + bonus_cabut;
                    total_bonus_cetak = total_bonus_cetak + bonus_cetak;
                    total_bonus_f2 = total_bonus_f2 + bonus_f2;
                    total_lp_get_Bonus++;
                } else {
                    row[17] = 0;
                    row[18] = 0;
                }
                row[19] = list.get(i).getCetak();
                row[20] = pekerja_cabut;
                row[21] = ketua_grup_cabut;
                model.addRow(row);
            }
        }
        decimalFormat.setGroupingUsed(true);
        label_total_bonus_cabut.setText("Rp. " + decimalFormat.format(total_bonus_cabut));
        label_total_bonus_cetak.setText("Rp. " + decimalFormat.format(total_bonus_cetak));
        label_total_bonus_f2.setText("Rp. " + decimalFormat.format(total_bonus_f2));
        decimalFormat.setGroupingUsed(false);

        int data = list.size();
        label_total_lp.setText(Integer.toString(data));
        label_total_lp_getBonus.setText(Integer.toString(total_lp_get_Bonus));
        label_total_lp_noBonus.setText(Integer.toString(data - total_lp_get_Bonus));
    }

    public void refreshTable_Setoran() {
        DefaultTableModel model = (DefaultTableModel) Table_LP_Bonus.getModel();
        model.setRowCount(0);
        show_data_bonusMangkok();
        ColumnsAutoSizer.sizeColumnsToFit(Table_LP_Bonus);

        DefaultTableModel model2 = (DefaultTableModel) table_pegawai_bonus.getModel();
        model2.setRowCount(0);
        label_no_lp.setText("-");
        label_total_pekerja.setText("0");
    }

    public void refreshTable_Pencabut() {
        try {
            DefaultTableModel model = (DefaultTableModel) table_pegawai_bonus.getModel();
            model.setRowCount(0);
            int Row = Table_LP_Bonus.getSelectedRow();
            int size = 0;
            float bonus_cabut = Float.valueOf(Table_LP_Bonus.getValueAt(Row, 17).toString());
            sql = "SELECT `nomor`,`grup_cabut`, `no_laporan_produksi`, `tb_detail_pencabut`.`id_pegawai`, `tb_karyawan`.`nama_pegawai`, `tb_bagian`.`nama_bagian`, `tanggal_cabut`, `jumlah_cabut`, `jumlah_gram` \n"
                    + "FROM `tb_detail_pencabut` \n"
                    + "JOIN `tb_karyawan` ON `tb_detail_pencabut`.`id_pegawai` = `tb_karyawan`.`id_pegawai`\n"
                    + "JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`\n"
                    + "WHERE `no_laporan_produksi` = '" + Table_LP_Bonus.getValueAt(Row, 0) + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[5];
            if (rs != null) {
                rs.last();    // moves cursor to the last row
                size = rs.getRow(); // get row id
                rs.beforeFirst();
            }
            while (rs.next()) {
                row[0] = rs.getString("id_pegawai");
                row[1] = rs.getString("nama_pegawai");
                row[2] = rs.getString("grup_cabut");
                row[3] = rs.getInt("jumlah_cabut");
                row[4] = Math.round(bonus_cabut / size);
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(table_pegawai_bonus);
            int rowData = table_pegawai_bonus.getRowCount();
            label_total_pekerja.setText(Integer.toString(rowData));

            TableAlignment.setHorizontalAlignment(JLabel.CENTER);
            //tabel Data Bahan Baku
            for (int i = 0; i < table_pegawai_bonus.getColumnCount(); i++) {
                table_pegawai_bonus.getColumnModel().getColumn(i).setCellRenderer(TableAlignment);
            }
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_BonusMangkok.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public double getBonusCabutPekerjaFrom(String lp) {
        double bonus = 0;
        for (int i = 0; i < Table_LP_Bonus.getRowCount(); i++) {
            if (lp.equals(Table_LP_Bonus.getValueAt(i, 0).toString())) {
                bonus = Float.valueOf(Table_LP_Bonus.getValueAt(i, 17).toString()) / Float.valueOf(Table_LP_Bonus.getValueAt(i, 26).toString());
            }
        }
        return bonus;
    }

    public double getBonusCabutFrom(String lp) {
        double bonus = 0;
        for (int i = 0; i < Table_LP_Bonus.getRowCount(); i++) {
            if (lp.equals(Table_LP_Bonus.getValueAt(i, 0).toString())) {
                bonus = Float.valueOf(Table_LP_Bonus.getValueAt(i, 17).toString());
            }
        }
        return bonus;
    }

    public double getBonusCetakFrom(String lp) {
        double bonus = 0;
        for (int i = 0; i < Table_LP_Bonus.getRowCount(); i++) {
            if (lp.equals(Table_LP_Bonus.getValueAt(i, 0).toString())) {
                bonus = Float.valueOf(Table_LP_Bonus.getValueAt(i, 18).toString());
            }
        }
        return bonus;
    }

    public void refreshTable_bonusPegawaiCabut() {
        try {
            DefaultTableModel model = (DefaultTableModel) tabel_BonusPegawaiCabut.getModel();
            model.setRowCount(0);
            DefaultTableModel model_rekap = (DefaultTableModel) tabel_RekapBonusCabut.getModel();
            model_rekap.setRowCount(0);
            decimalFormat.setGroupingUsed(true);
            String ruangan = label_ruangan_cabut.getText();
            if ("All".equals(label_ruangan_cabut.getText())) {
                ruangan = "";
            }
            sql = "SELECT `nomor`, `grup_cabut`, `tb_detail_pencabut`.`no_laporan_produksi`, `tb_detail_pencabut`.`id_pegawai`, `tb_karyawan`.`nama_pegawai`, `jumlah_cabut`, `jumlah_gram`, `tb_laporan_produksi`.`ruangan`, `tb_laporan_produksi`.`jumlah_keping`"
                    + "FROM `tb_detail_pencabut` \n"
                    + "LEFT JOIN `tb_finishing_2` ON `tb_detail_pencabut`.`no_laporan_produksi` = `tb_finishing_2`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_detail_pencabut`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    + "JOIN `tb_karyawan` ON `tb_detail_pencabut`.`id_pegawai` = `tb_karyawan`.`id_pegawai`\n"
                    + "JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`\n"
                    + "WHERE `tb_finishing_2`.`tgl_setor_f2` BETWEEN '" + dateFormat.format(date_setoran1) + "' AND '" + dateFormat.format(date_setoran2) + "' "
                    + "AND `tb_laporan_produksi`.`ruangan` LIKE '%" + ruangan + "%' "
                    + "AND `tb_karyawan`.`level_gaji` LIKE 'BORONG%' "
                    + "ORDER BY `tb_karyawan`.`nama_pegawai`";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[8];
            String id = "";
            float jumlah_gram = 0;
            int nomor_urut = 1, nomor_urut_rekap = 1, lp_rekap = 0, jumlah_kpg = 0;
            long total_bonus = 0, total_bonus_rekap = 0, rekap_bonus = 0;
            while (rs.next()) {
                if (!id.equals(rs.getString("id_pegawai"))) {
                    row[0] = nomor_urut_rekap;
                    row[4] = lp_rekap;
                    row[5] = jumlah_kpg;
                    row[6] = jumlah_gram;
                    row[7] = rekap_bonus;
                    if (rekap_bonus > 0) {
                        model_rekap.addRow(row);
                        nomor_urut_rekap++;
                        total_bonus_rekap = total_bonus_rekap + rekap_bonus;
                    }
                    lp_rekap = 0;
                    jumlah_kpg = 0;
                    jumlah_gram = 0;
                    rekap_bonus = 0;
                    id = rs.getString("id_pegawai");
                }
                row[0] = nomor_urut;
                row[1] = rs.getString("id_pegawai");
                row[2] = rs.getString("nama_pegawai");
                row[3] = rs.getString("grup_cabut");
                row[4] = rs.getString("no_laporan_produksi");
                row[5] = rs.getInt("jumlah_cabut");
                row[6] = Math.round(rs.getFloat("jumlah_gram") * 10) / 10.f;
                double bonus_pekerja = getBonusCabutPekerjaFrom(rs.getString("no_laporan_produksi"));
                row[7] = Math.round(bonus_pekerja);
                total_bonus = total_bonus + Math.round(bonus_pekerja);
                if (bonus_pekerja > 0) {
                    model.addRow(row);
                    nomor_urut++;

                    lp_rekap++;
                    rekap_bonus = rekap_bonus + Math.round(bonus_pekerja);
                    jumlah_kpg = jumlah_kpg + rs.getInt("jumlah_cabut");
                    jumlah_gram = jumlah_gram + Math.round(rs.getFloat("jumlah_gram") * 10) / 10.f;
                }
            }

            row[0] = nomor_urut_rekap;
            row[4] = lp_rekap;
            row[5] = jumlah_kpg;
            row[6] = jumlah_gram;
            row[7] = rekap_bonus;
            if (rekap_bonus > 0) {
                model_rekap.addRow(row);
                nomor_urut_rekap++;
                total_bonus_rekap = total_bonus_rekap + rekap_bonus;
            }

            ColumnsAutoSizer.sizeColumnsToFit(tabel_BonusPegawaiCabut);
            ColumnsAutoSizer.sizeColumnsToFit(tabel_RekapBonusCabut);
            label_total_rupiah_cabut.setText("Rp. " + decimalFormat.format(total_bonus));
            label_total_rupiah_RekapCabut.setText("Rp. " + decimalFormat.format(total_bonus_rekap));
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_BonusMangkok.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_bonusPegawaiCetak() {
        try {
            DefaultTableModel model = (DefaultTableModel) tabel_BonusPegawaiCetak.getModel();
            model.setRowCount(0);
            DefaultTableModel model_rekap = (DefaultTableModel) tabel_RekapBonusCetak.getModel();
            model_rekap.setRowCount(0);
            decimalFormat.setGroupingUsed(true);
            String ruangan = label_ruangan_cabut.getText();
            if ("All".equals(label_ruangan_cabut.getText())) {
                ruangan = "";
            }
            sql = "SELECT `cetak_dikerjakan`, `tb_karyawan`.`nama_pegawai`, `tb_bagian`.`nama_bagian`, `tb_cetak`.`no_laporan_produksi`, `tb_laporan_produksi`.`jumlah_keping`, `tb_laporan_produksi`.`berat_basah`"
                    + "FROM `tb_cetak`\n"
                    + "LEFT JOIN `tb_finishing_2` ON `tb_cetak`.`no_laporan_produksi` = `tb_finishing_2`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_cetak`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_karyawan` ON `tb_cetak`.`cetak_dikerjakan` = `tb_karyawan`.`id_pegawai`\n"
                    + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`\n"
                    + "WHERE `tb_finishing_2`.`tgl_setor_f2` BETWEEN '" + dateFormat.format(date_setoran1) + "' AND '" + dateFormat.format(date_setoran2) + "' "
                    + "AND `tb_laporan_produksi`.`ruangan` LIKE '%" + ruangan + "%'"
                    + "AND `cetak_dikerjakan_level` NOT LIKE '%TRAINING%' "
                    + "AND `tb_bagian`.`nama_bagian` NOT LIKE '%TRAINER%' "
                    + "AND `tb_bagian`.`nama_bagian` NOT LIKE '%PENGAWAS%' "
                    + "ORDER BY `tb_karyawan`.`nama_pegawai`";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[8];
            String id = "";
            float jumlah_gram = 0;
            int nomor_urut = 1, nomor_urut_rekap = 1, lp_rekap = 0, jumlah_kpg = 0;
            long total_bonus = 0, total_bonus_rekap = 0, rekap_bonus = 0;
            while (rs.next()) {
                if (!id.equals(rs.getString("cetak_dikerjakan"))) {
                    row[0] = nomor_urut_rekap;
                    row[4] = lp_rekap;
                    row[5] = jumlah_kpg;
                    row[6] = jumlah_gram;
                    row[7] = rekap_bonus;
                    if (rekap_bonus > 0) {
                        model_rekap.addRow(row);
                        nomor_urut_rekap++;
                        total_bonus_rekap = total_bonus_rekap + rekap_bonus;
                    }
                    lp_rekap = 0;
                    jumlah_kpg = 0;
                    jumlah_gram = 0;
                    rekap_bonus = 0;
                    id = rs.getString("cetak_dikerjakan");
                }
                row[0] = nomor_urut;
                row[1] = rs.getString("cetak_dikerjakan");
                row[2] = rs.getString("nama_pegawai");
                row[3] = rs.getString("nama_bagian");
                row[4] = rs.getString("no_laporan_produksi");
                row[5] = rs.getInt("jumlah_keping");
                row[6] = rs.getFloat("berat_basah");
                double bonus_pekerja = getBonusCetakFrom(rs.getString("no_laporan_produksi"));
                row[7] = Math.round(bonus_pekerja);
                total_bonus = total_bonus + Math.round(bonus_pekerja);
                if (bonus_pekerja > 0) {
                    model.addRow(row);
                    nomor_urut++;

                    lp_rekap++;
                    rekap_bonus = rekap_bonus + Math.round(bonus_pekerja);
                    jumlah_kpg = jumlah_kpg + rs.getInt("jumlah_keping");
                    jumlah_gram = jumlah_gram + Math.round(rs.getFloat("berat_basah") * 10) / 10.f;
                }
            }

            row[0] = nomor_urut_rekap;
            row[4] = lp_rekap;
            row[5] = jumlah_kpg;
            row[6] = jumlah_gram;
            row[7] = rekap_bonus;
            if (rekap_bonus > 0) {
                model_rekap.addRow(row);
                nomor_urut_rekap++;
                total_bonus_rekap = total_bonus_rekap + rekap_bonus;
            }

            ColumnsAutoSizer.sizeColumnsToFit(tabel_BonusPegawaiCetak);
            ColumnsAutoSizer.sizeColumnsToFit(tabel_RekapBonusCetak);
            label_total_rupiah_cetak.setText("Rp. " + decimalFormat.format(total_bonus));
            label_total_rupiah_RekapCetak.setText("Rp. " + decimalFormat.format(total_bonus_rekap));
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_BonusMangkok.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_bonusPegawaiF2() {
        try {
            DefaultTableModel model = (DefaultTableModel) tabel_BonusPegawaiF2.getModel();
            model.setRowCount(0);
            DefaultTableModel model_rekap = (DefaultTableModel) tabel_RekapBonusF2.getModel();
            model_rekap.setRowCount(0);
            decimalFormat.setGroupingUsed(true);
            String ruangan = label_ruangan_f2.getText();
            if ("All".equals(label_ruangan_f2.getText())) {
                ruangan = "";
            }
            sql = "SELECT * FROM ("
                    + "SELECT `tb_karyawan`.`id_pegawai` AS 'ID', `pekerja_f1` AS 'NAMA', 'f1' AS 'jenis_pekerja', `tb_bagian`.`nama_bagian`, `tb_finishing_2`.`no_laporan_produksi`, `tb_laporan_produksi`.`jumlah_keping`, `tb_laporan_produksi`.`berat_basah` \n"
                    + "FROM `tb_finishing_2` \n"
                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_finishing_2`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_karyawan` ON `tb_finishing_2`.`pekerja_f1` = `tb_karyawan`.`nama_pegawai`\n"
                    + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`\n"
                    + "WHERE `tb_finishing_2`.`tgl_setor_f2` BETWEEN '" + dateFormat.format(date_setoran1) + "' AND '" + dateFormat.format(date_setoran2) + "' AND `pekerja_f1` IS NOT NULL AND `tb_karyawan`.`status` LIKE 'IN%' AND `tb_karyawan`.`level_gaji` NOT LIKE 'TRAINING%'\n"
                    + "AND `tb_laporan_produksi`.`ruangan` LIKE '%" + ruangan + "%' AND `tb_bagian`.`nama_bagian` NOT LIKE '%TRAINER%' AND `tb_bagian`.`nama_bagian` NOT LIKE '%PENGAWAS%' "
                    + "UNION ALL\n"
                    + "SELECT `tb_karyawan`.`id_pegawai` AS 'ID', `pekerja_f2` AS 'NAMA', 'f2' AS 'jenis_pekerja', `tb_bagian`.`nama_bagian`, `tb_finishing_2`.`no_laporan_produksi`, `tb_laporan_produksi`.`jumlah_keping`, `tb_laporan_produksi`.`berat_basah` \n"
                    + "FROM `tb_finishing_2` \n"
                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_finishing_2`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_karyawan` ON `tb_finishing_2`.`pekerja_f2` = `tb_karyawan`.`nama_pegawai`\n"
                    + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`\n"
                    + "WHERE `tb_finishing_2`.`tgl_setor_f2` BETWEEN '" + dateFormat.format(date_setoran1) + "' AND '" + dateFormat.format(date_setoran2) + "' AND `pekerja_f2` IS NOT NULL AND `tb_karyawan`.`status` LIKE 'IN%' AND `tb_karyawan`.`level_gaji` NOT LIKE 'TRAINING%'\n"
                    + "AND `tb_laporan_produksi`.`ruangan` LIKE '%" + ruangan + "%' AND `tb_bagian`.`nama_bagian` NOT LIKE '%TRAINER%' AND `tb_bagian`.`nama_bagian` NOT LIKE '%PENGAWAS%' "
                    + "UNION ALL\n"
                    + "SELECT `tb_karyawan`.`id_pegawai` AS 'ID', `f2_disetor` AS 'NAMA', 'final' AS 'jenis_pekerja', `tb_bagian`.`nama_bagian`, `tb_finishing_2`.`no_laporan_produksi`, `tb_laporan_produksi`.`jumlah_keping`, `tb_laporan_produksi`.`berat_basah` \n"
                    + "FROM `tb_finishing_2` \n"
                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_finishing_2`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_karyawan` ON `tb_finishing_2`.`f2_disetor` = `tb_karyawan`.`nama_pegawai`\n"
                    + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`\n"
                    + "WHERE `tb_finishing_2`.`tgl_setor_f2` BETWEEN '" + dateFormat.format(date_setoran1) + "' AND '" + dateFormat.format(date_setoran2) + "' AND `f2_disetor` IS NOT NULL AND `tb_karyawan`.`status` LIKE 'IN%' AND `tb_karyawan`.`level_gaji` NOT LIKE 'TRAINING%' "
                    + "AND `tb_laporan_produksi`.`ruangan` LIKE '%" + ruangan + "%' AND `tb_bagian`.`nama_bagian` NOT LIKE '%TRAINER%' AND `tb_bagian`.`nama_bagian` NOT LIKE '%PENGAWAS%' "
                    + ") A WHERE 1 ORDER BY `NAMA`, `no_laporan_produksi`";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[8];
            String id = "";
            float jumlah_gram = 0;
            int nomor_urut = 1, nomor_urut_rekap = 1, lp_rekap = 0, jumlah_kpg = 0;
            double total_bonus = 0, total_bonus_rekap = 0, rekap_bonus = 0;
            while (rs.next()) {
                if (!id.equals(rs.getString("ID"))) {
                    row[0] = nomor_urut_rekap;
                    row[4] = lp_rekap;
                    row[5] = jumlah_kpg;
                    row[6] = jumlah_gram;
                    row[7] = rekap_bonus;
                    if (rekap_bonus > 0) {
                        model_rekap.addRow(row);
                        nomor_urut_rekap++;
                        total_bonus_rekap = total_bonus_rekap + rekap_bonus;
                    }
                    lp_rekap = 0;
                    jumlah_kpg = 0;
                    jumlah_gram = 0;
                    rekap_bonus = 0;
                    id = rs.getString("ID");
                }
                double bonus_cabut = getBonusCabutFrom(rs.getString("no_laporan_produksi"));
                if (rs.getString("jenis_pekerja").equals("f1")) {
                    bonus_cabut = bonus_cabut * Double.valueOf(txt_persen_bonus_f1.getText()) / 100d;//bonus f1
                } else if (rs.getString("jenis_pekerja").equals("f2")) {
                    bonus_cabut = bonus_cabut * Double.valueOf(txt_persen_bonus_f2.getText()) / 100d;//bonus f2
                } else if (rs.getString("jenis_pekerja").equals("final")) {
                    bonus_cabut = bonus_cabut * Double.valueOf(txt_persen_bonus_finalCheck.getText()) / 100d;//bonus final check
                }
                row[0] = nomor_urut;
                row[1] = rs.getString("ID");
                row[2] = rs.getString("NAMA");
                row[3] = rs.getString("nama_bagian");
                row[4] = rs.getString("no_laporan_produksi");
                row[5] = rs.getInt("jumlah_keping");
                row[6] = rs.getFloat("berat_basah");
                row[7] = bonus_cabut;
                total_bonus = total_bonus + bonus_cabut;
                if (bonus_cabut > 0) {
                    model.addRow(row);
                    nomor_urut++;

                    lp_rekap++;
                    rekap_bonus = rekap_bonus + bonus_cabut;
                    jumlah_kpg = jumlah_kpg + rs.getInt("jumlah_keping");
                    jumlah_gram = jumlah_gram + Math.round(rs.getFloat("berat_basah") * 10) / 10.f;
                }
            }

            row[0] = nomor_urut_rekap;
            row[4] = lp_rekap;
            row[5] = jumlah_kpg;
            row[6] = jumlah_gram;
            row[7] = rekap_bonus;
            if (rekap_bonus > 0) {
                model_rekap.addRow(row);
                nomor_urut_rekap++;
                total_bonus_rekap = total_bonus_rekap + rekap_bonus;
            }

            ColumnsAutoSizer.sizeColumnsToFit(tabel_BonusPegawaiF2);
            ColumnsAutoSizer.sizeColumnsToFit(tabel_RekapBonusF2);
            label_total_rupiah_f2.setText("Rp. " + decimalFormat.format(total_bonus));
            label_total_rupiah_RekapF2.setText("Rp. " + decimalFormat.format(total_bonus_rekap));
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_BonusMangkok.class.getName()).log(Level.SEVERE, null, ex);
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
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        Table_LP_Bonus = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        txt_no_lp = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        ComboBox_ruangan = new javax.swing.JComboBox<>();
        button_search = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        label_total_lp = new javax.swing.JLabel();
        label_total_bonus_cabut = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        label_total_bonus_cetak = new javax.swing.JLabel();
        button_export_bonus = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        label_total_lp_getBonus = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        label_total_lp_noBonus = new javax.swing.JLabel();
        CheckBox_Show_BonusOnly = new javax.swing.JCheckBox();
        button_form_laporan = new javax.swing.JButton();
        jLabel51 = new javax.swing.JLabel();
        label_total_bonus_f2 = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        button_export_pekerja = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        table_pegawai_bonus = new javax.swing.JTable();
        label = new javax.swing.JLabel();
        label_total_pekerja = new javax.swing.JLabel();
        label_no_lp = new javax.swing.JLabel();
        jLabel52 = new javax.swing.JLabel();
        txt_persen_bonus_f1 = new javax.swing.JTextField();
        jLabel53 = new javax.swing.JLabel();
        txt_persen_bonus_f2 = new javax.swing.JTextField();
        jLabel54 = new javax.swing.JLabel();
        txt_persen_bonus_finalCheck = new javax.swing.JTextField();
        Date_Penggajian = new com.toedter.calendar.JDateChooser();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        tabel_BonusPegawaiCabut = new javax.swing.JTable();
        button_export_detail_cabut = new javax.swing.JButton();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        label_date_cabut1 = new javax.swing.JLabel();
        label_date_cabut2 = new javax.swing.JLabel();
        label_ruangan_cabut = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        label_total_rupiah_cabut = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        tabel_BonusPegawaiCetak = new javax.swing.JTable();
        button_export_detail_cetak = new javax.swing.JButton();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        label_date_cetak1 = new javax.swing.JLabel();
        label_date_cetak2 = new javax.swing.JLabel();
        label_ruangan_cetak = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        label_total_rupiah_cetak = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jLabel41 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jScrollPane12 = new javax.swing.JScrollPane();
        tabel_BonusPegawaiF2 = new javax.swing.JTable();
        button_export_detail_f2 = new javax.swing.JButton();
        jLabel43 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        label_date_finishing1 = new javax.swing.JLabel();
        label_date_finishing2 = new javax.swing.JLabel();
        label_ruangan_f2 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        label_total_rupiah_f2 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jScrollPane9 = new javax.swing.JScrollPane();
        tabel_RekapBonusCabut = new javax.swing.JTable();
        button_export_rekap_cabut = new javax.swing.JButton();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        label_date_rekapCabut1 = new javax.swing.JLabel();
        label_date_rekapCabut2 = new javax.swing.JLabel();
        label_ruangan_RekapCabut = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        label_total_rupiah_RekapCabut = new javax.swing.JLabel();
        button_saveData_bonusCabut = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jScrollPane10 = new javax.swing.JScrollPane();
        tabel_RekapBonusCetak = new javax.swing.JTable();
        button_export_rekap_cetak = new javax.swing.JButton();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        label_date_rekapCetak1 = new javax.swing.JLabel();
        label_date_rekapCetak2 = new javax.swing.JLabel();
        label_ruangan_RekapCetak = new javax.swing.JLabel();
        label_total_rupiah_RekapCetak = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        button_saveData_bonusCetak = new javax.swing.JButton();
        jPanel11 = new javax.swing.JPanel();
        jLabel46 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        jScrollPane13 = new javax.swing.JScrollPane();
        tabel_RekapBonusF2 = new javax.swing.JTable();
        button_export_rekap_f2 = new javax.swing.JButton();
        jLabel48 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        label_date_rekapFinishing1 = new javax.swing.JLabel();
        label_date_rekapFinishing2 = new javax.swing.JLabel();
        label_ruangan_RekapF2 = new javax.swing.JLabel();
        label_total_rupiah_RekapF2 = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        button_saveData_bonusF2 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jScrollPane7 = new javax.swing.JScrollPane();
        jTable5 = new javax.swing.JTable();

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Bonus Mangkok", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N
        setPreferredSize(new java.awt.Dimension(1366, 700));

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        Table_LP_Bonus.setAutoCreateRowSorter(true);
        Table_LP_Bonus.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_LP_Bonus.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No. LP", "Grade", "Ruang", "Kpg LP", "Tgl LP", "Tgl Setor", "F.Bonus", "Faktor 0", "F.( - )", "Bonus", "(%) Bonus", "Utuh (%)", "Jidun (%)", "Pch/Flat (%)", "SH (%)", "SP (%)", "Target", "Bonus Cabut", "Bonus Cetak", "Pekerja Cetak", "Pekerja Cabut", "Ketua Regu", "Koreksi Kering", "Pekerja F1", "Pekerja F2", "Pekerja Final Check", "Pekerja Cabut", "Bonus F1", "Bonus F2", "Bonus Final"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.String.class, java.lang.Double.class, java.lang.Double.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Table_LP_Bonus.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(Table_LP_Bonus);

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("No. LP :");

        txt_no_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_no_lp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_no_lpKeyPressed(evt);
            }
        });

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("Tanggal Penggajian :");

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel4.setText("Ruangan :");

        ComboBox_ruangan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_ruangan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "A", "B", "C", "D" }));
        ComboBox_ruangan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ComboBox_ruanganActionPerformed(evt);
            }
        });

        button_search.setBackground(new java.awt.Color(255, 255, 255));
        button_search.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search.setText("Search");
        button_search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_searchActionPerformed(evt);
            }
        });

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel5.setText("Total LP :");

        label_total_lp.setBackground(new java.awt.Color(255, 255, 255));
        label_total_lp.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_lp.setText("0000");

        label_total_bonus_cabut.setBackground(new java.awt.Color(255, 255, 255));
        label_total_bonus_cabut.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_bonus_cabut.setText("Rp. 0");

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel8.setText("Total Bonus Cabut :");

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel9.setText("Total Bonus Cetak :");

        label_total_bonus_cetak.setBackground(new java.awt.Color(255, 255, 255));
        label_total_bonus_cetak.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_bonus_cetak.setText("Rp. 0");

        button_export_bonus.setBackground(new java.awt.Color(255, 255, 255));
        button_export_bonus.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_bonus.setText("Export to Excel");
        button_export_bonus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_bonusActionPerformed(evt);
            }
        });

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel6.setText("Total LP Bonus :");

        label_total_lp_getBonus.setBackground(new java.awt.Color(255, 255, 255));
        label_total_lp_getBonus.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_lp_getBonus.setText("0000");

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel7.setText("Total LP tidak Bonus :");

        label_total_lp_noBonus.setBackground(new java.awt.Color(255, 255, 255));
        label_total_lp_noBonus.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_lp_noBonus.setText("0000");

        CheckBox_Show_BonusOnly.setBackground(new java.awt.Color(255, 255, 255));
        CheckBox_Show_BonusOnly.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        CheckBox_Show_BonusOnly.setSelected(true);
        CheckBox_Show_BonusOnly.setText("Show get Bonus Only");
        CheckBox_Show_BonusOnly.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CheckBox_Show_BonusOnlyActionPerformed(evt);
            }
        });

        button_form_laporan.setBackground(new java.awt.Color(255, 255, 255));
        button_form_laporan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_form_laporan.setText("Form Laporan");
        button_form_laporan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_form_laporanActionPerformed(evt);
            }
        });

        jLabel51.setBackground(new java.awt.Color(255, 255, 255));
        jLabel51.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel51.setText("Total Bonus F2 :");

        label_total_bonus_f2.setBackground(new java.awt.Color(255, 255, 255));
        label_total_bonus_f2.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_bonus_f2.setText("Rp. 0");

        jPanel12.setBackground(new java.awt.Color(255, 255, 255));

        button_export_pekerja.setBackground(new java.awt.Color(255, 255, 255));
        button_export_pekerja.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_pekerja.setText("Export to Excel");
        button_export_pekerja.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_pekerjaActionPerformed(evt);
            }
        });

        table_pegawai_bonus.setAutoCreateRowSorter(true);
        table_pegawai_bonus.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Pegawai", "Nama", "Grup", "Kpg", "Bonus"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Float.class
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
        jScrollPane2.setViewportView(table_pegawai_bonus);

        label.setBackground(new java.awt.Color(255, 255, 255));
        label.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label.setText("Total Pekerja :");

        label_total_pekerja.setBackground(new java.awt.Color(255, 255, 255));
        label_total_pekerja.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_pekerja.setText("0");

        label_no_lp.setBackground(new java.awt.Color(255, 255, 255));
        label_no_lp.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_no_lp.setText("No. Laporan Produksi");

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                        .addComponent(label_no_lp)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(label)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_pekerja))
                    .addComponent(button_export_pekerja, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 370, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(label, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_pekerja, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(9, 9, 9)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 571, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_export_pekerja, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(label_no_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jLabel52.setBackground(new java.awt.Color(255, 255, 255));
        jLabel52.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel52.setText("Bonus F1 :");

        txt_persen_bonus_f1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_persen_bonus_f1.setText("2.5");

        jLabel53.setBackground(new java.awt.Color(255, 255, 255));
        jLabel53.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel53.setText("Bonus F2 :");

        txt_persen_bonus_f2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_persen_bonus_f2.setText("2.5");

        jLabel54.setBackground(new java.awt.Color(255, 255, 255));
        jLabel54.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel54.setText("Bonus Final Check :");

        txt_persen_bonus_finalCheck.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_persen_bonus_finalCheck.setText("1");

        Date_Penggajian.setBackground(new java.awt.Color(255, 255, 255));
        Date_Penggajian.setDate(new Date());
        Date_Penggajian.setDateFormatString("dd MMM yyyy");
        Date_Penggajian.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_lp)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_lp_getBonus)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_lp_noBonus))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_bonus_cabut))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_bonus_cetak))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel51)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_bonus_f2))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_no_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_ruangan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_Penggajian, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(6, 6, 6)
                                .addComponent(button_search)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(CheckBox_Show_BonusOnly))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel52)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_persen_bonus_f1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel53)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_persen_bonus_f2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel54)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_persen_bonus_finalCheck, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_form_laporan)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_export_bonus)))
                        .addGap(0, 207, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel12, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_no_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_ruangan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(CheckBox_Show_BonusOnly, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_search, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(Date_Penggajian, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel52, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_persen_bonus_f1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel53, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_persen_bonus_f2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel54, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_persen_bonus_finalCheck, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(button_export_bonus, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_form_laporan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(label_total_lp)
                    .addComponent(jLabel6)
                    .addComponent(label_total_lp_getBonus)
                    .addComponent(jLabel7)
                    .addComponent(label_total_lp_noBonus))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(label_total_bonus_cabut))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(label_total_bonus_cetak))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel51)
                    .addComponent(label_total_bonus_f2))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Data Bonus", jPanel1);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel10.setText("Data Bonus Mangkok Utuh untuk pegawai Cabut");

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel17.setText("Ruangan :");

        tabel_BonusPegawaiCabut.setAutoCreateRowSorter(true);
        tabel_BonusPegawaiCabut.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tabel_BonusPegawaiCabut.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "ID Pegawai", "Nama", "Grup", "No LP", "Kpg", "Gram", "Bonus"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Float.class, java.lang.Float.class
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
        jScrollPane6.setViewportView(tabel_BonusPegawaiCabut);
        if (tabel_BonusPegawaiCabut.getColumnModel().getColumnCount() > 0) {
            tabel_BonusPegawaiCabut.getColumnModel().getColumn(6).setHeaderValue("Gram");
        }

        button_export_detail_cabut.setBackground(new java.awt.Color(255, 255, 255));
        button_export_detail_cabut.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_detail_cabut.setText("Export to Excel");
        button_export_detail_cabut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_detail_cabutActionPerformed(evt);
            }
        });

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel18.setText("Tanggal Setor LP :");

        jLabel19.setBackground(new java.awt.Color(255, 255, 255));
        jLabel19.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel19.setText("-");

        label_date_cabut1.setBackground(new java.awt.Color(255, 255, 255));
        label_date_cabut1.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_date_cabut1.setText("dd MMMM yyyy");

        label_date_cabut2.setBackground(new java.awt.Color(255, 255, 255));
        label_date_cabut2.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_date_cabut2.setText("dd MMMM yyyy");

        label_ruangan_cabut.setBackground(new java.awt.Color(255, 255, 255));
        label_ruangan_cabut.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_ruangan_cabut.setText("-");

        jLabel34.setBackground(new java.awt.Color(255, 255, 255));
        jLabel34.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel34.setText("Total :");

        label_total_rupiah_cabut.setBackground(new java.awt.Color(255, 255, 255));
        label_total_rupiah_cabut.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_rupiah_cabut.setText("-");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 424, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel18)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_date_cabut1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel19)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_date_cabut2)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_ruangan_cabut)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel34)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_rupiah_cabut)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_export_detail_cabut)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel19, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_date_cabut1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_date_cabut2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_rupiah_cabut, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_ruangan_cabut, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_export_detail_cabut, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 546, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel20.setBackground(new java.awt.Color(255, 255, 255));
        jLabel20.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel20.setText("Data Bonus Mangkok Utuh untuk pegawai Cetak");

        jLabel21.setBackground(new java.awt.Color(255, 255, 255));
        jLabel21.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel21.setText("Ruangan :");

        tabel_BonusPegawaiCetak.setAutoCreateRowSorter(true);
        tabel_BonusPegawaiCetak.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tabel_BonusPegawaiCetak.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "ID Pegawai", "Nama", "Bagian", "No LP", "Kpg", "Gram", "Bonus"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Float.class, java.lang.Float.class
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
        jScrollPane8.setViewportView(tabel_BonusPegawaiCetak);

        button_export_detail_cetak.setBackground(new java.awt.Color(255, 255, 255));
        button_export_detail_cetak.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_detail_cetak.setText("Export to Excel");
        button_export_detail_cetak.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_detail_cetakActionPerformed(evt);
            }
        });

        jLabel22.setBackground(new java.awt.Color(255, 255, 255));
        jLabel22.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel22.setText("Tanggal Setor LP :");

        jLabel23.setBackground(new java.awt.Color(255, 255, 255));
        jLabel23.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel23.setText("-");

        label_date_cetak1.setBackground(new java.awt.Color(255, 255, 255));
        label_date_cetak1.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_date_cetak1.setText("dd MMMM yyyy");

        label_date_cetak2.setBackground(new java.awt.Color(255, 255, 255));
        label_date_cetak2.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_date_cetak2.setText("dd MMMM yyyy");

        label_ruangan_cetak.setBackground(new java.awt.Color(255, 255, 255));
        label_ruangan_cetak.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_ruangan_cetak.setText("-");

        jLabel35.setBackground(new java.awt.Color(255, 255, 255));
        jLabel35.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel35.setText("Total :");

        label_total_rupiah_cetak.setBackground(new java.awt.Color(255, 255, 255));
        label_total_rupiah_cetak.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_rupiah_cetak.setText("-");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 426, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel20)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel22)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_date_cetak1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel23)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_date_cetak2)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel21)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_ruangan_cetak)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel35)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_rupiah_cetak)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_export_detail_cetak)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel23, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_date_cetak1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_date_cetak2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_rupiah_cetak, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_ruangan_cetak, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_export_detail_cetak, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane8)
                .addContainerGap())
        );

        jPanel10.setBackground(new java.awt.Color(255, 255, 255));
        jPanel10.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel41.setBackground(new java.awt.Color(255, 255, 255));
        jLabel41.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel41.setText("Data Bonus Mangkok Utuh untuk pegawai F2");

        jLabel42.setBackground(new java.awt.Color(255, 255, 255));
        jLabel42.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel42.setText("Ruangan :");

        tabel_BonusPegawaiF2.setAutoCreateRowSorter(true);
        tabel_BonusPegawaiF2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tabel_BonusPegawaiF2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "ID Pegawai", "Nama", "Bagian", "No LP", "Kpg", "Gram", "Bonus"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Float.class, java.lang.Float.class
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
        jScrollPane12.setViewportView(tabel_BonusPegawaiF2);

        button_export_detail_f2.setBackground(new java.awt.Color(255, 255, 255));
        button_export_detail_f2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_detail_f2.setText("Export to Excel");
        button_export_detail_f2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_detail_f2ActionPerformed(evt);
            }
        });

        jLabel43.setBackground(new java.awt.Color(255, 255, 255));
        jLabel43.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel43.setText("Tanggal Setor LP :");

        jLabel44.setBackground(new java.awt.Color(255, 255, 255));
        jLabel44.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel44.setText("-");

        label_date_finishing1.setBackground(new java.awt.Color(255, 255, 255));
        label_date_finishing1.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_date_finishing1.setText("dd MMMM yyyy");

        label_date_finishing2.setBackground(new java.awt.Color(255, 255, 255));
        label_date_finishing2.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_date_finishing2.setText("dd MMMM yyyy");

        label_ruangan_f2.setBackground(new java.awt.Color(255, 255, 255));
        label_ruangan_f2.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_ruangan_f2.setText("-");

        jLabel45.setBackground(new java.awt.Color(255, 255, 255));
        jLabel45.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel45.setText("Total :");

        label_total_rupiah_f2.setBackground(new java.awt.Color(255, 255, 255));
        label_total_rupiah_f2.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_rupiah_f2.setText("-");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane12, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel41)
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addComponent(jLabel43)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_date_finishing1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel44)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_date_finishing2)))
                        .addGap(0, 135, Short.MAX_VALUE))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel42)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_ruangan_f2)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel45)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_rupiah_f2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_export_detail_f2)))
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel44, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_date_finishing1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_date_finishing2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel45, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_rupiah_f2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel42, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_ruangan_f2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_export_detail_f2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane12)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Laporan Bonus Mangkok / LP", jPanel3);

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel24.setBackground(new java.awt.Color(255, 255, 255));
        jLabel24.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel24.setText("Data Bonus Mangkok Utuh untuk pegawai Cabut");

        jLabel25.setBackground(new java.awt.Color(255, 255, 255));
        jLabel25.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel25.setText("Ruangan :");

        tabel_RekapBonusCabut.setAutoCreateRowSorter(true);
        tabel_RekapBonusCabut.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tabel_RekapBonusCabut.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "ID Pegawai", "Nama", "Grup", "LP", "Kpg", "Gram", "Bonus"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Float.class, java.lang.Float.class
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
        jScrollPane9.setViewportView(tabel_RekapBonusCabut);

        button_export_rekap_cabut.setBackground(new java.awt.Color(255, 255, 255));
        button_export_rekap_cabut.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_rekap_cabut.setText("Export to Excel");
        button_export_rekap_cabut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_rekap_cabutActionPerformed(evt);
            }
        });

        jLabel26.setBackground(new java.awt.Color(255, 255, 255));
        jLabel26.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel26.setText("Tanggal Setor LP :");

        jLabel27.setBackground(new java.awt.Color(255, 255, 255));
        jLabel27.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel27.setText("-");

        label_date_rekapCabut1.setBackground(new java.awt.Color(255, 255, 255));
        label_date_rekapCabut1.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_date_rekapCabut1.setText("dd MMMM yyyy");

        label_date_rekapCabut2.setBackground(new java.awt.Color(255, 255, 255));
        label_date_rekapCabut2.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_date_rekapCabut2.setText("dd MMMM yyyy");

        label_ruangan_RekapCabut.setBackground(new java.awt.Color(255, 255, 255));
        label_ruangan_RekapCabut.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_ruangan_RekapCabut.setText("-");

        jLabel32.setBackground(new java.awt.Color(255, 255, 255));
        jLabel32.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel32.setText("Total :");

        label_total_rupiah_RekapCabut.setBackground(new java.awt.Color(255, 255, 255));
        label_total_rupiah_RekapCabut.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_rupiah_RekapCabut.setText("-");

        button_saveData_bonusCabut.setBackground(new java.awt.Color(255, 255, 255));
        button_saveData_bonusCabut.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_saveData_bonusCabut.setText("Save Data");
        button_saveData_bonusCabut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_saveData_bonusCabutActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 440, Short.MAX_VALUE)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel24)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel26)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_date_rekapCabut1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel27)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_date_rekapCabut2)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel25)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_ruangan_RekapCabut)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel32)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_rupiah_RekapCabut)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_saveData_bonusCabut)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_export_rekap_cabut)))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel27, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_date_rekapCabut1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_date_rekapCabut2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_rupiah_RekapCabut, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_ruangan_RekapCabut, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_export_rekap_cabut, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_saveData_bonusCabut, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 546, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));
        jPanel8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel28.setBackground(new java.awt.Color(255, 255, 255));
        jLabel28.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel28.setText("Data Bonus Mangkok Utuh untuk pegawai Cetak");

        jLabel29.setBackground(new java.awt.Color(255, 255, 255));
        jLabel29.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel29.setText("Ruangan :");

        tabel_RekapBonusCetak.setAutoCreateRowSorter(true);
        tabel_RekapBonusCetak.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tabel_RekapBonusCetak.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "ID Pegawai", "Nama", "Bagian", "LP", "Kpg", "Gram", "Bonus"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Float.class, java.lang.Float.class
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
        jScrollPane10.setViewportView(tabel_RekapBonusCetak);

        button_export_rekap_cetak.setBackground(new java.awt.Color(255, 255, 255));
        button_export_rekap_cetak.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_rekap_cetak.setText("Export to Excel");
        button_export_rekap_cetak.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_rekap_cetakActionPerformed(evt);
            }
        });

        jLabel30.setBackground(new java.awt.Color(255, 255, 255));
        jLabel30.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel30.setText("Tanggal Setor LP :");

        jLabel31.setBackground(new java.awt.Color(255, 255, 255));
        jLabel31.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel31.setText("-");

        label_date_rekapCetak1.setBackground(new java.awt.Color(255, 255, 255));
        label_date_rekapCetak1.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_date_rekapCetak1.setText("dd MMMM yyyy");

        label_date_rekapCetak2.setBackground(new java.awt.Color(255, 255, 255));
        label_date_rekapCetak2.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_date_rekapCetak2.setText("dd MMMM yyyy");

        label_ruangan_RekapCetak.setBackground(new java.awt.Color(255, 255, 255));
        label_ruangan_RekapCetak.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_ruangan_RekapCetak.setText("-");

        label_total_rupiah_RekapCetak.setBackground(new java.awt.Color(255, 255, 255));
        label_total_rupiah_RekapCetak.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_rupiah_RekapCetak.setText("-");

        jLabel33.setBackground(new java.awt.Color(255, 255, 255));
        jLabel33.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel33.setText("Total :");

        button_saveData_bonusCetak.setBackground(new java.awt.Color(255, 255, 255));
        button_saveData_bonusCetak.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_saveData_bonusCetak.setText("Save Data");
        button_saveData_bonusCetak.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_saveData_bonusCetakActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel28)
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addComponent(jLabel30)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_date_rekapCetak1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel31)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_date_rekapCetak2)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel29)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_ruangan_RekapCetak)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel33)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_rupiah_RekapCetak)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 114, Short.MAX_VALUE)
                        .addComponent(button_saveData_bonusCetak)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_export_rekap_cetak)))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel31, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_date_rekapCetak1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_date_rekapCetak2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_rupiah_RekapCetak, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_ruangan_RekapCetak, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_export_rekap_cetak, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_saveData_bonusCetak, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane10, javax.swing.GroupLayout.DEFAULT_SIZE, 546, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel11.setBackground(new java.awt.Color(255, 255, 255));
        jPanel11.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel46.setBackground(new java.awt.Color(255, 255, 255));
        jLabel46.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel46.setText("Data Bonus Mangkok Utuh untuk pegawai F2");

        jLabel47.setBackground(new java.awt.Color(255, 255, 255));
        jLabel47.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel47.setText("Ruangan :");

        tabel_RekapBonusF2.setAutoCreateRowSorter(true);
        tabel_RekapBonusF2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tabel_RekapBonusF2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "ID Pegawai", "Nama", "Bagian", "LP", "Kpg", "Gram", "Bonus"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Float.class, java.lang.Float.class
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
        jScrollPane13.setViewportView(tabel_RekapBonusF2);

        button_export_rekap_f2.setBackground(new java.awt.Color(255, 255, 255));
        button_export_rekap_f2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_rekap_f2.setText("Export to Excel");
        button_export_rekap_f2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_rekap_f2ActionPerformed(evt);
            }
        });

        jLabel48.setBackground(new java.awt.Color(255, 255, 255));
        jLabel48.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel48.setText("Tanggal Setor LP :");

        jLabel49.setBackground(new java.awt.Color(255, 255, 255));
        jLabel49.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel49.setText("-");

        label_date_rekapFinishing1.setBackground(new java.awt.Color(255, 255, 255));
        label_date_rekapFinishing1.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_date_rekapFinishing1.setText("dd MMMM yyyy");

        label_date_rekapFinishing2.setBackground(new java.awt.Color(255, 255, 255));
        label_date_rekapFinishing2.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_date_rekapFinishing2.setText("dd MMMM yyyy");

        label_ruangan_RekapF2.setBackground(new java.awt.Color(255, 255, 255));
        label_ruangan_RekapF2.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_ruangan_RekapF2.setText("-");

        label_total_rupiah_RekapF2.setBackground(new java.awt.Color(255, 255, 255));
        label_total_rupiah_RekapF2.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_rupiah_RekapF2.setText("-");

        jLabel50.setBackground(new java.awt.Color(255, 255, 255));
        jLabel50.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel50.setText("Total :");

        button_saveData_bonusF2.setBackground(new java.awt.Color(255, 255, 255));
        button_saveData_bonusF2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_saveData_bonusF2.setText("Save Data");
        button_saveData_bonusF2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_saveData_bonusF2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane13, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel46)
                            .addGroup(jPanel11Layout.createSequentialGroup()
                                .addComponent(jLabel48)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_date_rekapFinishing1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel49)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_date_rekapFinishing2)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jLabel47)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_ruangan_RekapF2)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel50)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_rupiah_RekapF2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 99, Short.MAX_VALUE)
                        .addComponent(button_saveData_bonusF2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_export_rekap_f2)))
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel46, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel49, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel48, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_date_rekapFinishing1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_date_rekapFinishing2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel50, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_rupiah_RekapF2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel47, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_ruangan_RekapF2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_export_rekap_f2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_saveData_bonusF2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane13, javax.swing.GroupLayout.DEFAULT_SIZE, 546, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Rekap / Karyawan", jPanel6);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel11.setText("1. Tergabung dalam kelompok Borong cabut dan cetak.");

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel12.setText("2. Persentase Rendemen HARUS diatas Target.");
        jLabel12.setEnabled(false);

        jTable1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"BR", "70%", "10%", "14%", "6%"},
                {"BS", "60%", "15%", "17%", "8%"},
                {"BB", "45%", "25%", "20%", "10%"}
            },
            new String [] {
                "Bulu", "Utuh", "Flat", "SP", "SH"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
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
        jTable1.setEnabled(false);
        jTable1.setRowSelectionAllowed(false);
        jTable1.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(jTable1);

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel13.setText("Target Rendemen Mangkok");
        jLabel13.setEnabled(false);

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel14.setText("Target Rendemen Oval / Segitiga");
        jLabel14.setEnabled(false);

        jTable2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"BR", "65%", "15%", "14%", "6%"},
                {"BS", "55%", "20%", "17%", "8%"},
                {"BB", "45%", "30%", "20%", "10%"}
            },
            new String [] {
                "Bulu", "Utuh", "Flat", "SP", "SH"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
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
        jTable2.setEnabled(false);
        jTable2.setRowSelectionAllowed(false);
        jTable2.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(jTable2);

        jTable3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"BR", "-", "75%", "19%", "6%"},
                {"BS", "-", "70%", "22%", "8%"},
                {"BB", "-", "65%", "25%", "10%"}
            },
            new String [] {
                "Bulu", "Utuh", "Flat", "SP", "SH"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
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
        jTable3.setEnabled(false);
        jTable3.setRowSelectionAllowed(false);
        jTable3.getTableHeader().setReorderingAllowed(false);
        jScrollPane5.setViewportView(jTable3);

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel15.setText("Target Rendemen Pecah");
        jLabel15.setEnabled(false);

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel16.setText("3. Upah Bonus Mangkok Utuh per Keping, sesuai bulu upah");

        jTable5.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"BRS/BR", "Rp. 1500", "Rp. 300"},
                {"BS", "Rp. 2000", "Rp. 600"},
                {"BB", "Rp. 2500", "Rp. 800"}
            },
            new String [] {
                "Bulu", "Bonus Cabut", "Bonus Cetak"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
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
        jTable5.getTableHeader().setReorderingAllowed(false);
        jScrollPane7.setViewportView(jTable5);
        if (jTable5.getColumnModel().getColumnCount() > 0) {
            jTable5.getColumnModel().getColumn(0).setResizable(false);
            jTable5.getColumnModel().getColumn(2).setResizable(false);
        }

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel14)
                    .addComponent(jLabel11)
                    .addComponent(jLabel12)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jLabel13)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE)
                    .addComponent(jLabel15))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel16)
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(581, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 606, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Syarat & Ketentuan", jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void button_searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_searchActionPerformed
        // TODO add your handling code here:
        boolean check = true;
        try {
            double persen_bonus_f1 = Double.valueOf(txt_persen_bonus_f1.getText());
            double persen_bonus_f2 = Double.valueOf(txt_persen_bonus_f2.getText());
            double persen_bonus_finalCheck = Double.valueOf(txt_persen_bonus_finalCheck.getText());
        } catch (NumberFormatException e) {
            check = false;
            JOptionPane.showMessageDialog(this, "Maaf Format angka pada persentase bonus F1/F2/Final Check Salah!");
        }
        if (Date_Penggajian.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Filter Tanggal tidak boleh kosong !");
            check = false;
        } else if (!(new SimpleDateFormat("EEEEE").format(Date_Penggajian.getDate()).toUpperCase().equals("THURSDAY") || new SimpleDateFormat("EEEEE").format(Date_Penggajian.getDate()).toUpperCase().equals("KAMIS"))) {
            check = false;
            JOptionPane.showMessageDialog(this, "Tanggal penggajian seharusnya hari kamis");
        }
        if (check) {
            date_setoran1 = new Date(Date_Penggajian.getDate().getTime() - (7 * 24 * 60 * 60 * 1000));
            date_setoran2 = new Date(Date_Penggajian.getDate().getTime() - (1 * 24 * 60 * 60 * 1000));
            refreshTable_Setoran();
        }
    }//GEN-LAST:event_button_searchActionPerformed

    private void button_export_bonusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_bonusActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Table_LP_Bonus.getModel();
        ExportToExcel.writeToExcel(model, jPanel1);
    }//GEN-LAST:event_button_export_bonusActionPerformed

    private void button_export_pekerjaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_pekerjaActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) table_pegawai_bonus.getModel();
        ExportToExcel.writeToExcel(model, jPanel1);
    }//GEN-LAST:event_button_export_pekerjaActionPerformed

    private void txt_no_lpKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_no_lpKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_Setoran();
        }
    }//GEN-LAST:event_txt_no_lpKeyPressed

    private void ComboBox_ruanganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ComboBox_ruanganActionPerformed
        // TODO add your handling code here:
        refreshTable_Setoran();
    }//GEN-LAST:event_ComboBox_ruanganActionPerformed

    private void CheckBox_Show_BonusOnlyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CheckBox_Show_BonusOnlyActionPerformed
        // TODO add your handling code here:
        refreshTable_Setoran();
    }//GEN-LAST:event_CheckBox_Show_BonusOnlyActionPerformed

    private void button_form_laporanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_form_laporanActionPerformed
        // TODO add your handling code here:
        boolean check = true;
        try {
            double persen_bonus_f1 = Double.valueOf(txt_persen_bonus_f1.getText());
            double persen_bonus_f2 = Double.valueOf(txt_persen_bonus_f2.getText());
            double persen_bonus_finalCheck = Double.valueOf(txt_persen_bonus_finalCheck.getText());
        } catch (NumberFormatException e) {
            check = false;
            JOptionPane.showMessageDialog(this, "Maaf Format angka pada persentase bonus F1/F2/Final Check Salah!");
        }
        if (Date_Penggajian.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Filter Tanggal tidak boleh kosong !");
            check = false;
        } else if (!(new SimpleDateFormat("EEEEE").format(Date_Penggajian.getDate()).toUpperCase().equals("THURSDAY") || new SimpleDateFormat("EEEEE").format(Date_Penggajian.getDate()).toUpperCase().equals("KAMIS"))) {
            check = false;
            JOptionPane.showMessageDialog(this, "Tanggal penggajian seharusnya hari kamis");
        }
        if (check) {
            date_setoran1 = new Date(Date_Penggajian.getDate().getTime() - (7 * 24 * 60 * 60 * 1000));
            date_setoran2 = new Date(Date_Penggajian.getDate().getTime() - (1 * 24 * 60 * 60 * 1000));
            label_date_cabut1.setText(new SimpleDateFormat("dd MMMM yyyy").format(date_setoran1));
            label_date_cabut2.setText(new SimpleDateFormat("dd MMMM yyyy").format(date_setoran2));
            label_date_cetak1.setText(new SimpleDateFormat("dd MMMM yyyy").format(date_setoran1));
            label_date_cetak2.setText(new SimpleDateFormat("dd MMMM yyyy").format(date_setoran2));
            label_date_finishing1.setText(new SimpleDateFormat("dd MMMM yyyy").format(date_setoran1));
            label_date_finishing2.setText(new SimpleDateFormat("dd MMMM yyyy").format(date_setoran2));
            label_date_rekapCabut1.setText(new SimpleDateFormat("dd MMMM yyyy").format(date_setoran1));
            label_date_rekapCabut2.setText(new SimpleDateFormat("dd MMMM yyyy").format(date_setoran2));
            label_date_rekapCetak1.setText(new SimpleDateFormat("dd MMMM yyyy").format(date_setoran1));
            label_date_rekapCetak2.setText(new SimpleDateFormat("dd MMMM yyyy").format(date_setoran2));
            label_date_rekapFinishing1.setText(new SimpleDateFormat("dd MMMM yyyy").format(date_setoran1));
            label_date_rekapFinishing2.setText(new SimpleDateFormat("dd MMMM yyyy").format(date_setoran2));
            label_ruangan_cabut.setText(ComboBox_ruangan.getSelectedItem().toString());
            label_ruangan_cetak.setText(ComboBox_ruangan.getSelectedItem().toString());
            label_ruangan_f2.setText(ComboBox_ruangan.getSelectedItem().toString());
            label_ruangan_RekapCabut.setText(ComboBox_ruangan.getSelectedItem().toString());
            label_ruangan_RekapCetak.setText(ComboBox_ruangan.getSelectedItem().toString());
            label_ruangan_RekapF2.setText(ComboBox_ruangan.getSelectedItem().toString());
            refreshTable_bonusPegawaiCabut();
            refreshTable_bonusPegawaiCetak();
            refreshTable_bonusPegawaiF2();
            jTabbedPane1.setSelectedIndex(1);
        }
    }//GEN-LAST:event_button_form_laporanActionPerformed

    private void button_export_detail_cabutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_detail_cabutActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) tabel_BonusPegawaiCabut.getModel();
        ExportToExcel.writeToExcel(model, jPanel1);
    }//GEN-LAST:event_button_export_detail_cabutActionPerformed

    private void button_export_detail_cetakActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_detail_cetakActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) tabel_BonusPegawaiCetak.getModel();
        ExportToExcel.writeToExcel(model, jPanel1);
    }//GEN-LAST:event_button_export_detail_cetakActionPerformed

    private void button_export_rekap_cabutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_rekap_cabutActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) tabel_RekapBonusCabut.getModel();
        ExportToExcel.writeToExcel(model, jPanel1);
    }//GEN-LAST:event_button_export_rekap_cabutActionPerformed

    private void button_export_rekap_cetakActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_rekap_cetakActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) tabel_RekapBonusCetak.getModel();
        ExportToExcel.writeToExcel(model, jPanel1);
    }//GEN-LAST:event_button_export_rekap_cetakActionPerformed

    private void button_saveData_bonusCabutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_saveData_bonusCabutActionPerformed
        try {
            Date tanggal = new SimpleDateFormat("dd MMMM yyyy").parse(label_date_rekapCabut2.getText());
            int dialogResult = JOptionPane.showConfirmDialog(this, "Save " + tabel_RekapBonusCabut.getRowCount() + " data ?", "Warning", 0);
            if (dialogResult == JOptionPane.YES_OPTION) {
                for (int i = 0; i < tabel_RekapBonusCabut.getRowCount(); i++) {
                    String Query = "INSERT INTO `tb_lembur_rekap`(`id_pegawai`, `tanggal`, `bonus2`) "
                            + "VALUES ("
                            + "'" + tabel_RekapBonusCabut.getValueAt(i, 1).toString() + "',"
                            + "'" + dateFormat.format(tanggal) + "',"
                            + tabel_RekapBonusCabut.getValueAt(i, 7) + ") "
                            + "ON DUPLICATE KEY UPDATE "
                            + "`bonus2`=" + tabel_RekapBonusCabut.getValueAt(i, 7);
                    Utility.db.getConnection().createStatement();
                    Utility.db.getStatement().executeUpdate(Query);
                }
                JOptionPane.showMessageDialog(this, "Data Saved Successfully");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_BonusMangkok.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_saveData_bonusCabutActionPerformed

    private void button_saveData_bonusCetakActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_saveData_bonusCetakActionPerformed
        // TODO add your handling code here:
        try {
            Date tanggal = new SimpleDateFormat("dd MMMM yyyy").parse(label_date_rekapCetak2.getText());
            int dialogResult = JOptionPane.showConfirmDialog(this, "Save " + tabel_RekapBonusCetak.getRowCount() + " data ?", "Warning", 0);
            if (dialogResult == JOptionPane.YES_OPTION) {
                for (int i = 0; i < tabel_RekapBonusCetak.getRowCount(); i++) {
                    String Query = "INSERT INTO `tb_lembur_rekap`(`id_pegawai`, `tanggal`, `bonus2`) "
                            + "VALUES ("
                            + "'" + tabel_RekapBonusCetak.getValueAt(i, 1).toString() + "',"
                            + "'" + dateFormat.format(tanggal) + "',"
                            + tabel_RekapBonusCetak.getValueAt(i, 7) + ") "
                            + "ON DUPLICATE KEY UPDATE "
                            + "`bonus2`=" + tabel_RekapBonusCetak.getValueAt(i, 7);
                    Utility.db.getConnection().createStatement();
                    Utility.db.getStatement().executeUpdate(Query);
                }
                JOptionPane.showMessageDialog(this, "Data Saved Successfully");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_BonusMangkok.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_saveData_bonusCetakActionPerformed

    private void button_export_detail_f2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_detail_f2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_button_export_detail_f2ActionPerformed

    private void button_export_rekap_f2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_rekap_f2ActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) tabel_RekapBonusF2.getModel();
        ExportToExcel.writeToExcel(model, jPanel1);
    }//GEN-LAST:event_button_export_rekap_f2ActionPerformed

    private void button_saveData_bonusF2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_saveData_bonusF2ActionPerformed
        // TODO add your handling code here:
        try {
            Date tanggal = new SimpleDateFormat("dd MMMM yyyy").parse(label_date_rekapFinishing2.getText());
            int dialogResult = JOptionPane.showConfirmDialog(this, "Save " + tabel_RekapBonusF2.getRowCount() + " data ?", "Warning", 0);
            if (dialogResult == JOptionPane.YES_OPTION) {
                for (int i = 0; i < tabel_RekapBonusF2.getRowCount(); i++) {
                    String Query = "INSERT INTO `tb_lembur_rekap`(`id_pegawai`, `tanggal`, `bonus2`) "
                            + "VALUES ("
                            + "'" + tabel_RekapBonusF2.getValueAt(i, 1).toString() + "',"
                            + "'" + dateFormat.format(tanggal) + "',"
                            + tabel_RekapBonusF2.getValueAt(i, 7) + ") "
                            + "ON DUPLICATE KEY UPDATE "
                            + "`bonus2`=" + tabel_RekapBonusF2.getValueAt(i, 7);
                    Utility.db.getConnection().createStatement();
                    Utility.db.getStatement().executeUpdate(Query);
                }
                JOptionPane.showMessageDialog(this, "Data Saved Successfully");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_BonusMangkok.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_saveData_bonusF2ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox CheckBox_Show_BonusOnly;
    private javax.swing.JComboBox<String> ComboBox_ruangan;
    private com.toedter.calendar.JDateChooser Date_Penggajian;
    private javax.swing.JTable Table_LP_Bonus;
    private javax.swing.JButton button_export_bonus;
    private javax.swing.JButton button_export_detail_cabut;
    private javax.swing.JButton button_export_detail_cetak;
    private javax.swing.JButton button_export_detail_f2;
    private javax.swing.JButton button_export_pekerja;
    private javax.swing.JButton button_export_rekap_cabut;
    private javax.swing.JButton button_export_rekap_cetak;
    private javax.swing.JButton button_export_rekap_f2;
    private javax.swing.JButton button_form_laporan;
    private javax.swing.JButton button_saveData_bonusCabut;
    private javax.swing.JButton button_saveData_bonusCetak;
    private javax.swing.JButton button_saveData_bonusF2;
    private javax.swing.JButton button_search;
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
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel4;
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
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane13;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable3;
    private javax.swing.JTable jTable5;
    private javax.swing.JLabel label;
    private javax.swing.JLabel label_date_cabut1;
    private javax.swing.JLabel label_date_cabut2;
    private javax.swing.JLabel label_date_cetak1;
    private javax.swing.JLabel label_date_cetak2;
    private javax.swing.JLabel label_date_finishing1;
    private javax.swing.JLabel label_date_finishing2;
    private javax.swing.JLabel label_date_rekapCabut1;
    private javax.swing.JLabel label_date_rekapCabut2;
    private javax.swing.JLabel label_date_rekapCetak1;
    private javax.swing.JLabel label_date_rekapCetak2;
    private javax.swing.JLabel label_date_rekapFinishing1;
    private javax.swing.JLabel label_date_rekapFinishing2;
    private javax.swing.JLabel label_no_lp;
    private javax.swing.JLabel label_ruangan_RekapCabut;
    private javax.swing.JLabel label_ruangan_RekapCetak;
    private javax.swing.JLabel label_ruangan_RekapF2;
    private javax.swing.JLabel label_ruangan_cabut;
    private javax.swing.JLabel label_ruangan_cetak;
    private javax.swing.JLabel label_ruangan_f2;
    private javax.swing.JLabel label_total_bonus_cabut;
    private javax.swing.JLabel label_total_bonus_cetak;
    private javax.swing.JLabel label_total_bonus_f2;
    private javax.swing.JLabel label_total_lp;
    private javax.swing.JLabel label_total_lp_getBonus;
    private javax.swing.JLabel label_total_lp_noBonus;
    private javax.swing.JLabel label_total_pekerja;
    private javax.swing.JLabel label_total_rupiah_RekapCabut;
    private javax.swing.JLabel label_total_rupiah_RekapCetak;
    private javax.swing.JLabel label_total_rupiah_RekapF2;
    private javax.swing.JLabel label_total_rupiah_cabut;
    private javax.swing.JLabel label_total_rupiah_cetak;
    private javax.swing.JLabel label_total_rupiah_f2;
    private javax.swing.JTable tabel_BonusPegawaiCabut;
    private javax.swing.JTable tabel_BonusPegawaiCetak;
    private javax.swing.JTable tabel_BonusPegawaiF2;
    private javax.swing.JTable tabel_RekapBonusCabut;
    private javax.swing.JTable tabel_RekapBonusCetak;
    private javax.swing.JTable tabel_RekapBonusF2;
    private javax.swing.JTable table_pegawai_bonus;
    private javax.swing.JTextField txt_no_lp;
    private javax.swing.JTextField txt_persen_bonus_f1;
    private javax.swing.JTextField txt_persen_bonus_f2;
    private javax.swing.JTextField txt_persen_bonus_finalCheck;
    // End of variables declaration//GEN-END:variables
}
