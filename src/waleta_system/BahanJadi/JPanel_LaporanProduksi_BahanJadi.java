package waleta_system.BahanJadi;

import java.awt.event.KeyEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;
import waleta_system.Class.ExportToExcel;
import waleta_system.MainForm;

public class JPanel_LaporanProduksi_BahanJadi extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    float tot_berat_kering = 0, tot_kpg = 0, tot_kaki = 0;

    public JPanel_LaporanProduksi_BahanJadi() {
        initComponents();
    }

    public void init() {
        try {
            refreshTableLP();
            refreshTableGrading();
            try {
                ComboBox_Grade.removeAllItems();
                ComboBox_Grade.addItem("All");
                sql = "SELECT `kode_grade` FROM `tb_grade_bahan_baku` ORDER BY `kode_grade` ASC";
                ResultSet rs1 = Utility.db.getStatement().executeQuery(sql);
                while (rs1.next()) {
                    ComboBox_Grade.addItem(rs1.getString("kode_grade"));
                }

                ComboBox_Ruangan.removeAllItems();
                ComboBox_Ruangan.addItem("All");
                sql = "SELECT DISTINCT(`ruangan`) AS 'ruangan' FROM `tb_laporan_produksi` WHERE 1";
                rs = Utility.db.getStatement().executeQuery(sql);
                while (rs.next()) {
                    ComboBox_Ruangan.addItem(rs.getString("ruangan"));
                }

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, e);
                Logger.getLogger(JPanel_LaporanProduksi_BahanJadi.class.getName()).log(Level.SEVERE, null, e);
            }
            AutoCompleteDecorator.decorate(ComboBox_Grade);

            tabel_LaporanProduksi.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent event) {
                    if (!event.getValueIsAdjusting() && tabel_LaporanProduksi.getSelectedRow() != -1) {
                        int i = tabel_LaporanProduksi.getSelectedRow();
//                        refreshTableGrading();
                    }
                }
            });
        } catch (Exception ex) {
            Logger.getLogger(JPanel_LaporanProduksi_BahanJadi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTableLP() {
        try {
            tot_berat_kering = 0;
            tot_kpg = 0;
            tot_kaki = 0;
            decimalFormat.setMaximumFractionDigits(2);
            float bk, bk12, utuh, fbonus, fnol, jidun, flat, sh, sh12, sp, kaki = 0, netto;
            float rend_utuh, rend_flat, rend_sh, rend_sp, rend_jidun;
            float rend_utuh12, rend_flat12, rend_sh12, rend_sp12, rend_jidun12;
            float tot_berat_basah = 0, tot_netto_utuh = 0, tot_utuh = 0, tot_jidun = 0, tot_pecah = 0, tot_flat = 0, tot_kpg_jidun = 0, tot_bk = 0, tot_sh = 0;
            float tot_sesekan = 0, tot_hancuran = 0, tot_rontokan = 0, tot_bonggol = 0, tot_serabut = 0;
            float rata2_rend_utuh = 0, rata2_rend_jidun = 0, rata2_rend_flat = 0, rata2_rend_sp = 0, rata2_rend_sh = 0;
            float rata2_rend_utuh12 = 0, rata2_rend_jidun12 = 0, rata2_rend_flat12 = 0, rata2_rend_sp12 = 0, rata2_rend_sh12 = 0;
            DefaultTableModel model = (DefaultTableModel) tabel_LaporanProduksi.getModel();
            model.setRowCount(0);

            String tgl = "";
            if (Date1_search_tgl.getDate() != null && Date2_search_tgl.getDate() != null) {
                if ("Tanggal LP".equals(ComboBox_SearchTgl.getSelectedItem().toString())) {
                    tgl = "AND `tb_laporan_produksi`.`tanggal_lp` BETWEEN '" + dateFormat.format(Date1_search_tgl.getDate()) + "' AND '" + dateFormat.format(Date2_search_tgl.getDate()) + "'";
                } else if ("Tanggal Grading".equals(ComboBox_SearchTgl.getSelectedItem().toString())) {
                    tgl = "AND `tanggal_grading` BETWEEN '" + dateFormat.format(Date1_search_tgl.getDate()) + "' AND '" + dateFormat.format(Date2_search_tgl.getDate()) + "'";
                }
            }

            String ruangan = " AND `tb_laporan_produksi`.`ruangan` = '" + ComboBox_Ruangan.getSelectedItem().toString() + "'";
            if (ComboBox_Ruangan.getSelectedIndex() == 0) {
                ruangan = "";
            }

            String search_pekerja = "";
            if (txt_search_pekerja.getText() != null && !txt_search_pekerja.getText().equals("")) {
                if (ComboBox_SearchPekerja.getSelectedIndex() == 0) {
                    search_pekerja = "AND `tb_cabut`.`ketua_regu` LIKE '%" + txt_search_pekerja.getText() + "%' ";
                } else if (ComboBox_SearchPekerja.getSelectedIndex() == 1) {
                    search_pekerja = "AND `tb_cetak`.`cetak_dikerjakan` LIKE '%" + txt_search_pekerja.getText() + "%' ";
                }
            }

            String kode_grade = "AND `tb_laporan_produksi`.`kode_grade` IN (";
            if (tabel_gradeBaku.getRowCount() > 0) {
                for (int a = 0; a < tabel_gradeBaku.getRowCount(); a++) {
                    kode_grade = kode_grade + "'" + tabel_gradeBaku.getValueAt(a, 0).toString() + "'";
                    if (a < tabel_gradeBaku.getRowCount() - 1) {
                        kode_grade = kode_grade + ", ";
                    }
                }
                kode_grade = kode_grade + ")";
            } else {
                kode_grade = "";
            }

            sql = "SELECT `tb_laporan_produksi`.`no_kartu_waleta`, `kode_asal`, `tb_laporan_produksi`.`kode_grade`, `tb_laporan_produksi`.`memo_lp`, `tb_laporan_produksi`.`ruangan`, `tb_laporan_produksi`.`tanggal_lp`, `tanggal_grading`, `tb_laporan_produksi`.`jumlah_keping`, `tb_laporan_produksi`.`berat_basah`, `tb_laporan_produksi`.`berat_kering`, `tb_cabut`.`ketua_regu`, `tb_karyawan`.`nama_pegawai`, "
                    + "`fbonus_f2`, `berat_fbonus`, `fnol_f2`, `berat_fnol`, `pecah_f2`, `berat_pecah`, `flat_f2`, `berat_flat`, `sesekan`, `hancuran`, `rontokan`, `bonggol`, `serabut`, `tambahan_kaki1`, `lp_kaki1`, `tambahan_kaki2`, `lp_kaki2`, `tb_finishing_2`.`berat_jidun`, `tb_finishing_2`.`jidun_utuh_f2`, `tb_finishing_2`.`jidun_pecah_f2`"
                    + "FROM `tb_bahan_jadi_masuk` "
                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_bahan_jadi_masuk`.`kode_asal` = `tb_laporan_produksi`.`no_laporan_produksi` "
                    + "LEFT JOIN `tb_finishing_2` ON `tb_finishing_2`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`"
                    + "LEFT JOIN `tb_cabut` ON `tb_cabut`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`"
                    + "LEFT JOIN `tb_cetak` ON `tb_cetak`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`"
                    + "LEFT JOIN `tb_karyawan` ON `tb_cetak`.`cetak_dikerjakan` = `tb_karyawan`.`id_pegawai`"
                    + "WHERE (`kode_tutupan` <> 'SALDO_AWAL2018' OR `kode_tutupan` IS NULL) "
                    + "AND `kode_asal` LIKE '%" + txt_search_no_lp.getText() + "%' "
                    + "AND `tb_laporan_produksi`.`no_kartu_waleta` LIKE '%" + txt_search_no_kartu.getText() + "%' "
                    + "AND `tb_laporan_produksi`.`memo_lp` LIKE '%" + txt_search_memo.getText() + "%' "
                    + "AND `tanggal_grading` IS NOT NULL "
                    + search_pekerja
                    + kode_grade
                    + ruangan
                    + tgl;
//            PreparedStatement pst = Utility.db.getConnection().prepareStatement(sql);
            rs = Utility.db.getConnection().prepareStatement(sql).executeQuery();
            Object[] row = new Object[27];
            while (rs.next()) {
                String query_cek_jidun = "SELECT `kode_asal_bahan_jadi`, `grade_bahan_jadi` FROM `tb_grading_bahan_jadi` \n"
                        + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_grading_bahan_jadi`.`grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode`\n"
                        + "WHERE `kode_asal_bahan_jadi` = '" + rs.getString("kode_asal") + "' AND `tb_grade_bahan_jadi`.`bentuk_grade` = 'Jidun'";
                ResultSet rs_cek_jidun = Utility.db.getConnection().prepareStatement(query_cek_jidun).executeQuery();
                if (rs_cek_jidun.next() && !CheckBox_LPJidun.isSelected()) {
//                    System.out.println(rs_cek_jidun.getString("kode_asal_bahan_jadi") + " : " + rs_cek_jidun.getString("grade_bahan_jadi"));
//                    rs.next();//skip row ini
                } else {
                    bk = rs.getFloat("berat_kering");
                    bk12 = bk * 1.12f;
                    fbonus = rs.getFloat("berat_fbonus");
                    fnol = rs.getFloat("berat_fnol");
                    jidun = rs.getInt("berat_jidun");
                    utuh = fbonus + fnol;
                    kaki = rs.getFloat("tambahan_kaki1") + rs.getFloat("tambahan_kaki2");
                    netto = utuh - kaki;
                    flat = rs.getFloat("berat_pecah") + rs.getFloat("berat_flat");
                    sp = rs.getFloat("sesekan") + rs.getFloat("hancuran") + rs.getFloat("rontokan") + rs.getFloat("bonggol") + rs.getFloat("serabut");
                    sh = bk - (netto + flat + sp + jidun);
                    sh12 = bk12 - (netto + flat + sp + jidun);

                    rend_utuh = (netto / bk) * 100;
                    rend_jidun = (jidun / bk) * 100;
                    rend_flat = (flat / bk) * 100;
                    rend_sp = (sp / bk) * 100;
                    rend_sh = (sh / bk) * 100;

                    rend_utuh12 = (netto / bk12) * 100;
                    rend_jidun12 = (jidun / bk12) * 100;
                    rend_flat12 = (flat / bk12) * 100;
                    rend_sp12 = (sp / bk12) * 100;
                    rend_sh12 = (sh12 / bk12) * 100;

                    row[0] = rs.getString("no_kartu_waleta");
                    row[1] = rs.getString("kode_asal");
                    row[2] = rs.getDate("tanggal_lp");
                    row[3] = rs.getString("memo_lp");
                    row[4] = rs.getString("kode_grade");
                    row[5] = rs.getString("ruangan");
                    row[6] = rs.getDate("tanggal_grading");
                    row[7] = rs.getInt("jumlah_keping");
                    row[8] = rs.getInt("berat_basah");
                    row[9] = rs.getInt("berat_kering");
                    row[10] = bk12;
                    row[11] = utuh;
                    row[12] = rs.getFloat("berat_pecah");
                    row[13] = rs.getFloat("berat_flat");
                    row[14] = rs.getInt("berat_jidun");
                    row[15] = rs.getFloat("sesekan");
                    row[16] = rs.getFloat("hancuran");
                    row[17] = rs.getFloat("rontokan");
                    row[18] = rs.getFloat("bonggol");
                    row[19] = rs.getFloat("serabut");
                    row[20] = kaki;
                    row[21] = (double) Math.round(rend_utuh * 100) / 100;
                    row[22] = (double) Math.round(rend_flat * 100) / 100;
                    row[23] = (double) Math.round(rend_sp * 100) / 100;
                    row[24] = (double) Math.round(rend_sh * 100) / 100;
                    row[25] = rs.getString("ketua_regu");
                    row[26] = rs.getString("nama_pegawai");

                    tot_kpg = tot_kpg + rs.getInt("jumlah_keping");
                    tot_kpg_jidun = rs.getInt("jidun_utuh_f2") + rs.getInt("jidun_pecah_f2");
                    tot_berat_basah = tot_berat_basah + rs.getInt("berat_basah");
                    tot_berat_kering = tot_berat_kering + rs.getInt("berat_kering");

                    tot_bk = tot_bk + bk;
                    tot_netto_utuh = tot_netto_utuh + netto;
                    tot_utuh = tot_utuh + utuh;
                    tot_jidun = tot_jidun + jidun;
                    tot_pecah = tot_pecah + rs.getFloat("berat_pecah");
                    tot_flat = tot_flat + rs.getFloat("berat_flat");
                    tot_kaki = tot_kaki + kaki;
                    tot_sh = tot_sh + sh;

                    tot_sesekan = tot_sesekan + rs.getFloat("sesekan");
                    tot_hancuran = tot_hancuran + rs.getFloat("hancuran");
                    tot_rontokan = tot_rontokan + rs.getFloat("rontokan");
                    tot_bonggol = tot_bonggol + rs.getFloat("bonggol");
                    tot_serabut = tot_serabut + rs.getFloat("serabut");

                    rata2_rend_utuh = rata2_rend_utuh + rend_utuh;
                    rata2_rend_jidun = rata2_rend_jidun + rend_jidun;
                    rata2_rend_flat = rata2_rend_flat + rend_flat;
                    rata2_rend_sp = rata2_rend_sp + rend_sp;
                    rata2_rend_sh = rata2_rend_sh + rend_sh;

                    rata2_rend_utuh12 = rata2_rend_utuh12 + rend_utuh12;
                    rata2_rend_jidun12 = rata2_rend_jidun12 + rend_jidun12;
                    rata2_rend_flat12 = rata2_rend_flat12 + rend_flat12;
                    rata2_rend_sp12 = rata2_rend_sp12 + rend_sp12;
                    rata2_rend_sh12 = rata2_rend_sh12 + rend_sh12;
                    model.addRow(row);
                }
            }

            rata2_rend_utuh = (tot_netto_utuh / tot_bk) * 100;
            rata2_rend_jidun = (tot_jidun / tot_bk) * 100;
            rata2_rend_flat = ((tot_pecah + tot_flat) / tot_bk) * 100;
            rata2_rend_sp = ((tot_sesekan + tot_hancuran + tot_rontokan + tot_bonggol + tot_serabut) / tot_bk) * 100;
            rata2_rend_sh = (tot_sh / tot_bk) * 100;

            rata2_rend_utuh12 = rata2_rend_utuh12 / tabel_LaporanProduksi.getRowCount();
            rata2_rend_jidun12 = rata2_rend_jidun12 / tabel_LaporanProduksi.getRowCount();
            rata2_rend_flat12 = rata2_rend_flat12 / tabel_LaporanProduksi.getRowCount();
            rata2_rend_sp12 = rata2_rend_sp12 / tabel_LaporanProduksi.getRowCount();
            rata2_rend_sh12 = rata2_rend_sh12 / tabel_LaporanProduksi.getRowCount();

            decimalFormat.setGroupingUsed(true);
            ColumnsAutoSizer.sizeColumnsToFit(tabel_LaporanProduksi);
            label_totalLP.setText(Integer.toString(tabel_LaporanProduksi.getRowCount()));
            label_total_kpg.setText(decimalFormat.format(tot_kpg));
            label_total_kpg_jidun.setText(decimalFormat.format(tot_kpg_jidun));
            label_total_gram_basah.setText(decimalFormat.format(tot_berat_basah));
            label_total_gram_kering.setText(decimalFormat.format(tot_berat_kering));
            label_total_gram_utuh.setText(decimalFormat.format(tot_utuh));
            label_total_gram_jidun.setText(decimalFormat.format(tot_jidun));
            label_total_gram_pch.setText(decimalFormat.format(tot_pecah));
            label_total_gram_flat.setText(decimalFormat.format(tot_flat));
            label_total_gram_kaki.setText(decimalFormat.format(tot_kaki));

            label_rata2_rend_utuh.setText(decimalFormat.format(rata2_rend_utuh));
            label_rata2_rend_jidun.setText(decimalFormat.format(rata2_rend_jidun));
            label_rata2_rend_flat.setText(decimalFormat.format(rata2_rend_flat));
            label_rata2_rend_sp.setText(decimalFormat.format(rata2_rend_sp));
            label_rata2_rend_sh.setText(decimalFormat.format(rata2_rend_sh));
//            label_rata2_rend_utuh12.setText(decimalFormat.format(rata2_rend_utuh12));
//            label_rata2_rend_jidun12.setText(decimalFormat.format(rata2_rend_jidun12));
//            label_rata2_rend_flat12.setText(decimalFormat.format(rata2_rend_flat12));
//            label_rata2_rend_sp12.setText(decimalFormat.format(rata2_rend_sp12));
//            label_rata2_rend_sh12.setText(decimalFormat.format(rata2_rend_sh12));

            label_tot_sesekan.setText(decimalFormat.format(tot_sesekan));
            label_tot_hancuran.setText(decimalFormat.format(tot_hancuran));
            label_tot_rontokan.setText(decimalFormat.format(tot_rontokan));
            label_tot_bonggol.setText(decimalFormat.format(tot_bonggol));
            label_tot_serabut.setText(decimalFormat.format(tot_serabut));
            refreshTableGrading();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_LaporanProduksi_BahanJadi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTableGrading() {
        try {
            decimalFormat.setMaximumFractionDigits(2);
            int tot_kpg_utuh = 0;
            float tot_gram_real = 0;
            float tot_gram = 0;
            float total_persen_gram = 0;
            float total_persen_gram_real = 0;
            int i = tabel_LaporanProduksi.getSelectedRow();
            DefaultTableModel model = (DefaultTableModel) tabel_hasilGrading.getModel();
            model.setRowCount(0);

            String filter_jidun = "";
            if (!CheckBox_LPJidun.isSelected()) {
                filter_jidun = "AND `kode_asal_bahan_jadi` NOT IN (SELECT DISTINCT(`kode_asal_bahan_jadi`) FROM `tb_grading_bahan_jadi` \n"
                        + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_grading_bahan_jadi`.`grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode`\n"
                        + "WHERE `tb_grade_bahan_jadi`.`bentuk_grade` = 'Jidun') ";
            }

            String tgl = "";
            if (Date1_search_tgl.getDate() != null && Date2_search_tgl.getDate() != null) {
                if ("Tanggal LP".equals(ComboBox_SearchTgl.getSelectedItem().toString())) {
                    tgl = "AND `tb_laporan_produksi`.`tanggal_lp` BETWEEN '" + dateFormat.format(Date1_search_tgl.getDate()) + "' AND '" + dateFormat.format(Date2_search_tgl.getDate()) + "' ";
                } else if ("Tanggal Grading".equals(ComboBox_SearchTgl.getSelectedItem().toString())) {
                    tgl = "AND `tanggal_grading` BETWEEN '" + dateFormat.format(Date1_search_tgl.getDate()) + "' AND '" + dateFormat.format(Date2_search_tgl.getDate()) + "' ";
                }
            }

            String ruangan = " AND `tb_laporan_produksi`.`ruangan` = '" + ComboBox_Ruangan.getSelectedItem().toString() + "' ";
            if (ComboBox_Ruangan.getSelectedIndex() == 0) {
                ruangan = "";
            }

            String search_pekerja = "";
            if (txt_search_pekerja.getText() != null && !txt_search_pekerja.getText().equals("")) {
                if (ComboBox_SearchPekerja.getSelectedIndex() == 0) {
                    search_pekerja = "AND `tb_cabut`.`ketua_regu` LIKE '%" + txt_search_pekerja.getText() + "%' ";
                } else if (ComboBox_SearchPekerja.getSelectedIndex() == 1) {
                    search_pekerja = "AND `tb_cetak`.`cetak_dikerjakan` LIKE '%" + txt_search_pekerja.getText() + "%' ";
                }
            }

            String kode_grade = "AND `tb_laporan_produksi`.`kode_grade` IN (";
            if (tabel_gradeBaku.getRowCount() > 0) {
                for (int a = 0; a < tabel_gradeBaku.getRowCount(); a++) {
                    kode_grade = kode_grade + "'" + tabel_gradeBaku.getValueAt(a, 0).toString() + "'";
                    if (a < tabel_gradeBaku.getRowCount() - 1) {
                        kode_grade = kode_grade + ", ";
                    }
                }
                kode_grade = kode_grade + ") ";
            } else {
                kode_grade = "";
            }

            sql = "SELECT `tb_grading_bahan_jadi`.`grade_bahan_jadi`, `tb_grade_bahan_jadi`.`kode_grade`, `tb_grade_bahan_jadi`.`bentuk_grade`, SUM(`tb_grading_bahan_jadi`.`keping`) AS 'keping', SUM(`tb_grading_bahan_jadi`.`gram`) AS 'gram'"
                    + "FROM `tb_grading_bahan_jadi` "
                    + "LEFT JOIN `tb_bahan_jadi_masuk` ON `tb_grading_bahan_jadi`.`kode_asal_bahan_jadi` = `tb_bahan_jadi_masuk`.`kode_asal` "
                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_bahan_jadi_masuk`.`kode_asal` = `tb_laporan_produksi`.`no_laporan_produksi` "
                    + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_grade_bahan_jadi`.`kode` = `tb_grading_bahan_jadi`.`grade_bahan_jadi`"
                    + "LEFT JOIN `tb_cabut` ON `tb_cabut`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`"
                    + "LEFT JOIN `tb_cetak` ON `tb_cetak`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`"
                    + "WHERE (`kode_tutupan` <> 'SALDO_AWAL2018' OR `kode_tutupan` IS NULL) "
                    + "AND `kode_asal_bahan_jadi` LIKE '%" + txt_search_no_lp.getText() + "%' "
                    + "AND `tb_laporan_produksi`.`no_kartu_waleta` LIKE '%" + txt_search_no_kartu.getText() + "%' "
                    + "AND `tb_laporan_produksi`.`memo_lp` LIKE '%" + txt_search_memo.getText() + "%' "
                    + search_pekerja
                    + kode_grade
                    + ruangan
                    + tgl
                    + filter_jidun
                    + "GROUP BY `tb_grading_bahan_jadi`.`grade_bahan_jadi`";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[9];
            while (rs.next()) {
                row[0] = rs.getString("kode_grade");
                row[1] = rs.getString("bentuk_grade");
                row[2] = rs.getInt("keping");
                row[3] = rs.getInt("gram");
                row[4] = null;//gram kaki
                row[5] = null;//gram real
                row[6] = (rs.getFloat("keping") / tot_kpg) * 100;//% keping

                float persen_gram = (rs.getInt("gram") / tot_berat_kering) * 100;
                total_persen_gram = total_persen_gram + persen_gram;
                row[7] = persen_gram;//% gram
                row[8] = null;//% gram real

                if (rs.getString("bentuk_grade").equals("Utuh")) {
                    tot_kpg_utuh = tot_kpg_utuh + rs.getInt("keping");
                }
                tot_gram = tot_gram + rs.getFloat("gram");
                if (rs.getString("kode_grade") != null) {
                    model.addRow(row);
                }
            }

            float kaki_per_kpg = tot_kaki / (float) tot_kpg_utuh;
            for (int j = 0; j < tabel_hasilGrading.getRowCount(); j++) {
                float kpg = Float.valueOf(tabel_hasilGrading.getValueAt(j, 2).toString());
                float gram = Float.valueOf(tabel_hasilGrading.getValueAt(j, 3).toString());
                float gram_real = gram;
                float gram_kaki = 0;
                if (tabel_hasilGrading.getValueAt(j, 1).toString().equals("Utuh")) {
                    gram_kaki = kpg * kaki_per_kpg;
                    gram_real = gram - gram_kaki;
                }
                tabel_hasilGrading.setValueAt(((double) Math.round(gram_kaki * 100) / 100), j, 4);
                tabel_hasilGrading.setValueAt(((double) Math.round(gram_real * 100) / 100), j, 5);
                tot_gram_real = tot_gram_real + gram_real;

                float persen_gram_real = (gram_real / tot_berat_kering) * 100;
                tabel_hasilGrading.setValueAt(((double) Math.round(persen_gram_real * 100) / 100), j, 8);
                total_persen_gram_real = total_persen_gram_real + persen_gram_real;
            }

            decimalFormat.setGroupingUsed(true);
            ColumnsAutoSizer.sizeColumnsToFit(tabel_hasilGrading);
            label_total_grade_hasil.setText(Integer.toString(tabel_hasilGrading.getRowCount()));
            label_total_kpg_grading.setText(decimalFormat.format(tot_kpg));
            label_total_gram_grading.setText(decimalFormat.format(tot_gram));
            label_total_gram_grading_real.setText(decimalFormat.format(tot_gram_real));
            label_gr_kaki_per_kpg.setText(decimalFormat.format(kaki_per_kpg));
            label_persen_gram.setText(decimalFormat.format(total_persen_gram) + "%");
            label_persen_real.setText(decimalFormat.format(total_persen_gram_real) + "%");
            label_persen_SH.setText(decimalFormat.format(100f - total_persen_gram_real) + "%");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_LaporanProduksi_BahanJadi.class.getName()).log(Level.SEVERE, null, ex);
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
        jScrollPane1 = new javax.swing.JScrollPane();
        tabel_LaporanProduksi = new javax.swing.JTable();
        txt_search_memo = new javax.swing.JTextField();
        ComboBox_SearchTgl = new javax.swing.JComboBox<>();
        Date1_search_tgl = new com.toedter.calendar.JDateChooser();
        jLabel3 = new javax.swing.JLabel();
        ComboBox_Ruangan = new javax.swing.JComboBox<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabel_hasilGrading = new javax.swing.JTable();
        button_refresh = new javax.swing.JButton();
        Date2_search_tgl = new com.toedter.calendar.JDateChooser();
        label_total_gradebaku = new javax.swing.JLabel();
        txt_search_no_lp = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        tabel_gradeBaku = new javax.swing.JTable();
        ComboBox_Grade = new javax.swing.JComboBox<>();
        button_add_grade = new javax.swing.JButton();
        button_remove_grade = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        ComboBox_SearchPekerja = new javax.swing.JComboBox<>();
        txt_search_pekerja = new javax.swing.JTextField();
        button_removeALL_grade = new javax.swing.JButton();
        button_export = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        button_export1 = new javax.swing.JButton();
        button_print_lp = new javax.swing.JButton();
        jLabel65 = new javax.swing.JLabel();
        txt_search_no_kartu = new javax.swing.JTextField();
        jLabel66 = new javax.swing.JLabel();
        CheckBox_LPJidun = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        label_tot_bonggol = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        label_total_gram_kering = new javax.swing.JLabel();
        label_rata2_rend_flat = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        label_tot_rontokan = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        label_tot_hancuran = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel64 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        label_total_gram_flat = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        label_total_gram_basah = new javax.swing.JLabel();
        jLabel63 = new javax.swing.JLabel();
        label_rata2_rend_sp = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jLabel59 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        label_total_kpg_jidun = new javax.swing.JLabel();
        label_rata2_rend_jidun = new javax.swing.JLabel();
        label_tot_serabut = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel55 = new javax.swing.JLabel();
        label_rata2_rend_utuh = new javax.swing.JLabel();
        label_total_kpg = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        label_rata2_rend_sh = new javax.swing.JLabel();
        label_tot_sesekan = new javax.swing.JLabel();
        label_total_gram_jidun = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel58 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel60 = new javax.swing.JLabel();
        label_totalLP = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        label_total_gram_pch = new javax.swing.JLabel();
        label_total_gram_utuh = new javax.swing.JLabel();
        label_total_gram_kaki = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel56 = new javax.swing.JLabel();
        label_total_kpg_grading = new javax.swing.JLabel();
        label_gr_kaki_per_kpg = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        label_persen_gram = new javax.swing.JLabel();
        jLabel54 = new javax.swing.JLabel();
        jLabel57 = new javax.swing.JLabel();
        jLabel53 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        label_total_gram_grading_real = new javax.swing.JLabel();
        label_persen_real = new javax.swing.JLabel();
        label_persen_SH = new javax.swing.JLabel();
        label_total_grade_hasil = new javax.swing.JLabel();
        label_total_gram_grading = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Laporan Produksi Barang Jadi", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N

        tabel_LaporanProduksi.setAutoCreateRowSorter(true);
        tabel_LaporanProduksi.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tabel_LaporanProduksi.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No Kartu", "No LP", "Tgl LP", "Memo", "Grade", "Ruang", "Tgl Grading", "Kpg", "Berat Angin", "Berat 0%", "BK 12%", "Real Utuh", "Pch", "Flat", "Jidun", "Sskn", "Hncr", "Ront", "Bgl", "Srbt", "Berat Kaki", "% Utuh", "% Flat", "% SP", "% SH", "Ketua Cabut", "Pekerja Cetak"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabel_LaporanProduksi.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tabel_LaporanProduksi);

        txt_search_memo.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_memo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_memoKeyPressed(evt);
            }
        });

        ComboBox_SearchTgl.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_SearchTgl.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tanggal LP", "Tanggal Grading" }));

        Date1_search_tgl.setBackground(new java.awt.Color(255, 255, 255));
        Date1_search_tgl.setDate(new Date(new Date().getTime()- (7 * 24 * 60 * 60 * 1000)));
        Date1_search_tgl.setDateFormatString("dd MMMM yyyy");
        Date1_search_tgl.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("Ruangan :");

        ComboBox_Ruangan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_Ruangan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "A", "B", "C", "D" }));

        tabel_hasilGrading.setAutoCreateRowSorter(true);
        tabel_hasilGrading.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tabel_hasilGrading.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Grade BJ", "Bentuk", "Kpg", "Gram", "Kaki", "Gram Real", "Kpg (%)", "Gram (%)", "Gram Real (%)"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
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
        tabel_hasilGrading.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(tabel_hasilGrading);

        button_refresh.setBackground(new java.awt.Color(255, 255, 255));
        button_refresh.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_refresh.setText("Refresh");
        button_refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refreshActionPerformed(evt);
            }
        });

        Date2_search_tgl.setBackground(new java.awt.Color(255, 255, 255));
        Date2_search_tgl.setDate(new Date());
        Date2_search_tgl.setDateFormatString("dd MMMM yyyy");
        Date2_search_tgl.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        label_total_gradebaku.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gradebaku.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_gradebaku.setText("0");

        txt_search_no_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_no_lp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_no_lpKeyPressed(evt);
            }
        });

        tabel_gradeBaku.setAutoCreateRowSorter(true);
        tabel_gradeBaku.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tabel_gradeBaku.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Grade"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabel_gradeBaku.setRowSelectionAllowed(false);
        jScrollPane3.setViewportView(tabel_gradeBaku);

        ComboBox_Grade.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        button_add_grade.setBackground(new java.awt.Color(255, 255, 255));
        button_add_grade.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_add_grade.setText("Add");
        button_add_grade.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_add_gradeActionPerformed(evt);
            }
        });

        button_remove_grade.setBackground(new java.awt.Color(255, 255, 255));
        button_remove_grade.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_remove_grade.setText("Remove");
        button_remove_grade.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_remove_gradeActionPerformed(evt);
            }
        });

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel16.setText("Hasil Grading");

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel17.setText("Grade");

        ComboBox_SearchPekerja.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_SearchPekerja.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Pekerja Cabut", "Pekerja Cetak" }));

        txt_search_pekerja.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_pekerja.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_pekerjaKeyPressed(evt);
            }
        });

        button_removeALL_grade.setBackground(new java.awt.Color(255, 255, 255));
        button_removeALL_grade.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_removeALL_grade.setText("Remove All");
        button_removeALL_grade.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_removeALL_gradeActionPerformed(evt);
            }
        });

        button_export.setBackground(new java.awt.Color(255, 255, 255));
        button_export.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export.setText("Export");
        button_export.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_exportActionPerformed(evt);
            }
        });

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel9.setText("Memo :");

        button_export1.setBackground(new java.awt.Color(255, 255, 255));
        button_export1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export1.setText("Export");
        button_export1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export1ActionPerformed(evt);
            }
        });

        button_print_lp.setBackground(new java.awt.Color(255, 255, 255));
        button_print_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_print_lp.setText("Print LP");
        button_print_lp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_print_lpActionPerformed(evt);
            }
        });

        jLabel65.setBackground(new java.awt.Color(255, 255, 255));
        jLabel65.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel65.setText("No LP :");

        txt_search_no_kartu.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_no_kartu.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_no_kartuKeyPressed(evt);
            }
        });

        jLabel66.setBackground(new java.awt.Color(255, 255, 255));
        jLabel66.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel66.setText("No Kartu Baku :");

        CheckBox_LPJidun.setBackground(new java.awt.Color(255, 255, 255));
        CheckBox_LPJidun.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        CheckBox_LPJidun.setSelected(true);
        CheckBox_LPJidun.setText(" LP Jidun");

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        label_tot_bonggol.setBackground(new java.awt.Color(255, 255, 255));
        label_tot_bonggol.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_tot_bonggol.setText("0");

        jLabel37.setBackground(new java.awt.Color(255, 255, 255));
        jLabel37.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel37.setText("Total Bonggol :");

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel18.setText("Gram");

        label_total_gram_kering.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_kering.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_gram_kering.setText("0");

        label_rata2_rend_flat.setBackground(new java.awt.Color(255, 255, 255));
        label_rata2_rend_flat.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_rata2_rend_flat.setText("0");

        jLabel41.setBackground(new java.awt.Color(255, 255, 255));
        jLabel41.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel41.setText("Total Serabut :");

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("Total Laporan Produksi :");

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel13.setText("Gram");

        label_tot_rontokan.setBackground(new java.awt.Color(255, 255, 255));
        label_tot_rontokan.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_tot_rontokan.setText("0");

        jLabel32.setBackground(new java.awt.Color(255, 255, 255));
        jLabel32.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel32.setText("%");

        label_tot_hancuran.setBackground(new java.awt.Color(255, 255, 255));
        label_tot_hancuran.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_tot_hancuran.setText("0");

        jLabel34.setBackground(new java.awt.Color(255, 255, 255));
        jLabel34.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel34.setText("Gram");

        jLabel64.setBackground(new java.awt.Color(255, 255, 255));
        jLabel64.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel64.setText("Kpg");

        jLabel44.setBackground(new java.awt.Color(255, 255, 255));
        jLabel44.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel44.setText("Kpg");

        label_total_gram_flat.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_flat.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_gram_flat.setText("0");

        jLabel25.setBackground(new java.awt.Color(255, 255, 255));
        jLabel25.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel25.setText("Rendemen Flat + Pecah :");

        jLabel24.setBackground(new java.awt.Color(255, 255, 255));
        jLabel24.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel24.setText("Gram");

        jLabel23.setBackground(new java.awt.Color(255, 255, 255));
        jLabel23.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel23.setText("Total Gram Berat Kering :");

        label_total_gram_basah.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_basah.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_gram_basah.setText("0");

        jLabel63.setBackground(new java.awt.Color(255, 255, 255));
        jLabel63.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel63.setText("Total Keping Jidun :");

        label_rata2_rend_sp.setBackground(new java.awt.Color(255, 255, 255));
        label_rata2_rend_sp.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_rata2_rend_sp.setText("0");

        jLabel42.setBackground(new java.awt.Color(255, 255, 255));
        jLabel42.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel42.setText("Gram");

        jLabel59.setBackground(new java.awt.Color(255, 255, 255));
        jLabel59.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel59.setText("Rendemen Jidun :");

        jLabel36.setBackground(new java.awt.Color(255, 255, 255));
        jLabel36.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel36.setText("Gram");

        jLabel26.setBackground(new java.awt.Color(255, 255, 255));
        jLabel26.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel26.setText("%");

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel8.setText("Total Gram Pecah :");

        jLabel35.setBackground(new java.awt.Color(255, 255, 255));
        jLabel35.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel35.setText("Total Rontokan :");

        label_total_kpg_jidun.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kpg_jidun.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_kpg_jidun.setText("0");

        label_rata2_rend_jidun.setBackground(new java.awt.Color(255, 255, 255));
        label_rata2_rend_jidun.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_rata2_rend_jidun.setText("0");

        label_tot_serabut.setBackground(new java.awt.Color(255, 255, 255));
        label_tot_serabut.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_tot_serabut.setText("0");

        jLabel43.setBackground(new java.awt.Color(255, 255, 255));
        jLabel43.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel43.setText("Total Keping :");

        jLabel20.setBackground(new java.awt.Color(255, 255, 255));
        jLabel20.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel20.setText("Gram");

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel10.setText("Total Gram Kaki :");

        jLabel55.setBackground(new java.awt.Color(255, 255, 255));
        jLabel55.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel55.setText("Total Gram Jidun :");

        label_rata2_rend_utuh.setBackground(new java.awt.Color(255, 255, 255));
        label_rata2_rend_utuh.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_rata2_rend_utuh.setText("0");

        label_total_kpg.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kpg.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_kpg.setText("0");

        jLabel31.setBackground(new java.awt.Color(255, 255, 255));
        jLabel31.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel31.setText("Rendemen Utuh :");

        jLabel27.setBackground(new java.awt.Color(255, 255, 255));
        jLabel27.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel27.setText("Susut Proses :");

        label_rata2_rend_sh.setBackground(new java.awt.Color(255, 255, 255));
        label_rata2_rend_sh.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_rata2_rend_sh.setText("0");

        label_tot_sesekan.setBackground(new java.awt.Color(255, 255, 255));
        label_tot_sesekan.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_tot_sesekan.setText("0");

        label_total_gram_jidun.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_jidun.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_gram_jidun.setText("0");

        jLabel39.setBackground(new java.awt.Color(255, 255, 255));
        jLabel39.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel39.setText("Total Sesekan :");

        jLabel30.setBackground(new java.awt.Color(255, 255, 255));
        jLabel30.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel30.setText("%");

        jLabel40.setBackground(new java.awt.Color(255, 255, 255));
        jLabel40.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel40.setText("Gram");

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel5.setText("Total Gram Utuh :");

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel11.setText("Gram");

        jLabel38.setBackground(new java.awt.Color(255, 255, 255));
        jLabel38.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel38.setText("Gram");

        jLabel58.setBackground(new java.awt.Color(255, 255, 255));
        jLabel58.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel58.setText("Gram");

        jLabel29.setBackground(new java.awt.Color(255, 255, 255));
        jLabel29.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel29.setText("Susut Hilang :");

        jLabel60.setBackground(new java.awt.Color(255, 255, 255));
        jLabel60.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel60.setText("%");

        label_totalLP.setBackground(new java.awt.Color(255, 255, 255));
        label_totalLP.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_totalLP.setText("0");

        jLabel33.setBackground(new java.awt.Color(255, 255, 255));
        jLabel33.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel33.setText("Total Hancuran :");

        jLabel19.setBackground(new java.awt.Color(255, 255, 255));
        jLabel19.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel19.setText("Total Gram Flat :");

        jLabel22.setBackground(new java.awt.Color(255, 255, 255));
        jLabel22.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel22.setText("Gram");

        jLabel28.setBackground(new java.awt.Color(255, 255, 255));
        jLabel28.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel28.setText("%");

        jLabel21.setBackground(new java.awt.Color(255, 255, 255));
        jLabel21.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel21.setText("Total Gram Berat Basah :");

        label_total_gram_pch.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_pch.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_gram_pch.setText("0");

        label_total_gram_utuh.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_utuh.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_gram_utuh.setText("0");

        label_total_gram_kaki.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_kaki.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_gram_kaki.setText("0");

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
                        .addComponent(label_totalLP))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel21)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gram_basah)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel22))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel23)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gram_kering)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel24))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel43)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_kpg)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel44))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel63)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_kpg_jidun)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel64)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gram_utuh)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel13))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gram_pch)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel18))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gram_flat)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel20))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gram_kaki)
                        .addGap(5, 5, 5)
                        .addComponent(jLabel11))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel55)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gram_jidun)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel58)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel31)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_rata2_rend_utuh)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel32))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel25)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_rata2_rend_flat)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel26))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel27)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_rata2_rend_sp)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel28))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel29)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_rata2_rend_sh)
                        .addGap(5, 5, 5)
                        .addComponent(jLabel30))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel59)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_rata2_rend_jidun)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel60)))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel39)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_tot_sesekan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel40))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel33)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_tot_hancuran)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel34))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel35)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_tot_rontokan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel36))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel37)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_tot_bonggol)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel38))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel41)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_tot_serabut)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel42)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_tot_sesekan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_tot_hancuran, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_tot_rontokan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_tot_bonggol, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_tot_serabut, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel42, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(label_rata2_rend_utuh, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(label_total_gram_utuh, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(label_totalLP, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_total_gram_basah, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(7, 7, 7)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_total_gram_kering, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_total_kpg, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel63, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_total_kpg_jidun, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel64, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel59, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_rata2_rend_jidun, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel60, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel55, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_total_gram_jidun, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel58, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(label_total_gram_pch, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(label_total_gram_flat, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(label_total_gram_kaki, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(label_rata2_rend_flat, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(label_rata2_rend_sp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(label_rata2_rend_sh, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))))))
                .addContainerGap())
        );

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        jLabel56.setBackground(new java.awt.Color(255, 255, 255));
        jLabel56.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel56.setText("Total Gram :");

        label_total_kpg_grading.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kpg_grading.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_kpg_grading.setText("0");

        label_gr_kaki_per_kpg.setBackground(new java.awt.Color(255, 255, 255));
        label_gr_kaki_per_kpg.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_gr_kaki_per_kpg.setText("0");

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel6.setText("Total Grade :");

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel12.setText("Total Gram Real :");

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel14.setText("Gram");

        label_persen_gram.setBackground(new java.awt.Color(255, 255, 255));
        label_persen_gram.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_persen_gram.setText("0");

        jLabel54.setBackground(new java.awt.Color(255, 255, 255));
        jLabel54.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel54.setText("gr Kaki / Kpg :");

        jLabel57.setBackground(new java.awt.Color(255, 255, 255));
        jLabel57.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel57.setText("Gram");

        jLabel53.setBackground(new java.awt.Color(255, 255, 255));
        jLabel53.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel53.setText("SH :");

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel15.setText("Keping");

        label_total_gram_grading_real.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_grading_real.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_gram_grading_real.setText("0");

        label_persen_real.setBackground(new java.awt.Color(255, 255, 255));
        label_persen_real.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_persen_real.setText("0");

        label_persen_SH.setBackground(new java.awt.Color(255, 255, 255));
        label_persen_SH.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_persen_SH.setText("0");

        label_total_grade_hasil.setBackground(new java.awt.Color(255, 255, 255));
        label_total_grade_hasil.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_grade_hasil.setText("0");

        label_total_gram_grading.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_grading.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_gram_grading.setText("0");

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel7.setText("Total Keping :");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_grade_hasil)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel53)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_persen_SH))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_kpg_grading)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel54)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_gr_kaki_per_kpg))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel56)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_gram_grading)
                                .addGap(5, 5, 5)
                                .addComponent(jLabel57)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_persen_gram))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_gram_grading_real)
                                .addGap(5, 5, 5)
                                .addComponent(jLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_persen_real)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel53, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_persen_SH, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_grade_hasil, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel54, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_gr_kaki_per_kpg, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_kpg_grading, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(8, 8, 8)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel56, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_gram_grading, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel57, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_persen_gram, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_gram_grading_real, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_persen_real, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(ComboBox_SearchTgl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(Date1_search_tgl, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(Date2_search_tgl, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel3)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(ComboBox_Ruangan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(CheckBox_LPJidun)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(button_export, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(button_print_lp))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel65)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txt_search_no_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel66)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txt_search_no_kartu, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel9)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txt_search_memo, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(ComboBox_SearchPekerja, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txt_search_pekerja, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(button_refresh)))
                                .addGap(0, 254, Short.MAX_VALUE))))
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel16)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(button_export1))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(ComboBox_Grade, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_add_grade))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(label_total_gradebaku)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel17)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 64, Short.MAX_VALUE)
                                .addComponent(button_remove_grade)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_removeALL_grade)))
                        .addContainerGap())
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_search_no_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_Grade, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_add_grade, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel65, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_no_kartu, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel66, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_memo, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_SearchPekerja, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_pekerja, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_refresh, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(Date2_search_tgl, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_SearchTgl, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date1_search_tgl, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_Ruangan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CheckBox_LPJidun, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_print_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_total_gradebaku, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_removeALL_grade, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_remove_grade, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_export1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 460, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
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

    private void button_refreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refreshActionPerformed
        // TODO add your handling code here:
        refreshTableLP();
    }//GEN-LAST:event_button_refreshActionPerformed

    private void txt_search_no_lpKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_no_lpKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTableLP();
        }
    }//GEN-LAST:event_txt_search_no_lpKeyPressed

    private void txt_search_memoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_memoKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTableLP();
        }
    }//GEN-LAST:event_txt_search_memoKeyPressed

    private void button_add_gradeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_add_gradeActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) tabel_gradeBaku.getModel();
        boolean check = true;

        for (int i = 0; i < tabel_gradeBaku.getRowCount(); i++) {
            if (ComboBox_Grade.getSelectedItem().toString().equals(tabel_gradeBaku.getValueAt(i, 0).toString())) {
                JOptionPane.showMessageDialog(this, "Maaf Grade " + ComboBox_Grade.getSelectedItem().toString() + " Sudah Masuk !");
                check = false;
                break;
            }
        }

        if (check) {
            try {
                model.addRow(new Object[]{ComboBox_Grade.getSelectedItem().toString()});

//                sql = "SELECT * FROM `tb_grade_bahan_baku` WHERE `kode_grade` = '" + ComboBox_Grade.getSelectedItem().toString() + "'";
//                rs = Utility.db.getStatement().executeQuery(sql);
//                while (rs.next()) {
//                    String bulu = null;
//                    if (null == rs.getString("jenis_bulu")) {
//                        bulu = "-";
//                    } else {
//                        switch (rs.getString("jenis_bulu")) {
//                            case "Bulu Ringan":
//                                bulu = "BR";
//                                break;
//                            case "Bulu Ringan Sekali/Bulu Ringan":
//                                bulu = "BRS/BR";
//                                break;
//                            case "Bulu Sedang":
//                                bulu = "BS";
//                                break;
//                            case "Bulu Berat":
//                                bulu = "BB";
//                                break;
//                            case "Bulu Berat Sekali":
//                                bulu = "BB2";
//                                break;
//                            default:
//                                bulu = "-";
//                                break;
//                        }
//                    }
//
//                    String bentuk = null;
//                    if (rs.getString("jenis_bentuk").contains("Mangkok")) {
//                        bentuk = "Mangkok";
//                    } else if (rs.getString("jenis_bentuk").contains("Oval")) {
//                        bentuk = "Oval";
//                    } else if (rs.getString("jenis_bentuk").contains("Segitiga") || rs.getString("jenis_bentuk").contains("SGTG")) {
//                        bentuk = "Segitiga";
//                    } else {
//                        bentuk = "-";
//                    }
//
//                    model.addRow(new Object[]{rs.getString("kode_grade"), bentuk, bulu, rs.getString("jenis_warna")});
//                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e);
                Logger.getLogger(JPanel_LaporanProduksi_BahanJadi.class.getName()).log(Level.SEVERE, null, e);
            }

            ComboBox_Grade.requestFocus();
//            ColumnsAutoSizer.sizeColumnsToFit(tabel_gradeBaku);
            label_total_gradebaku.setText(Integer.toString(tabel_gradeBaku.getRowCount()));
        }
    }//GEN-LAST:event_button_add_gradeActionPerformed

    private void button_remove_gradeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_remove_gradeActionPerformed
        // TODO add your handling code here:
        int i = tabel_gradeBaku.getSelectedRow();
        DefaultTableModel model = (DefaultTableModel) tabel_gradeBaku.getModel();
        if (i != -1) {
            model.removeRow(tabel_gradeBaku.getRowSorter().convertRowIndexToModel(i));
        }
//        ColumnsAutoSizer.sizeColumnsToFit(tabel_gradeBaku);
        label_total_gradebaku.setText(Integer.toString(tabel_gradeBaku.getRowCount()));
    }//GEN-LAST:event_button_remove_gradeActionPerformed

    private void txt_search_pekerjaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_pekerjaKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTableLP();
            refreshTableGrading();
        }
    }//GEN-LAST:event_txt_search_pekerjaKeyPressed

    private void button_removeALL_gradeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_removeALL_gradeActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) tabel_gradeBaku.getModel();
        model.setRowCount(0);
    }//GEN-LAST:event_button_removeALL_gradeActionPerformed

    private void button_exportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_exportActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) tabel_LaporanProduksi.getModel();
        ExportToExcel.writeToExcel(model, this);
    }//GEN-LAST:event_button_exportActionPerformed

    private void button_export1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export1ActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) tabel_hasilGrading.getModel();
        ExportToExcel.writeToExcel(model, this);
    }//GEN-LAST:event_button_export1ActionPerformed

    private void button_print_lpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_print_lpActionPerformed
        try {
            // TODO add your handling code here:
            //            DefaultTableModel Table = (DefaultTableModel)Table_laporan_produksi.getModel();
            int j = tabel_LaporanProduksi.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Please select data from the table first", "warning!", 1);
            } else {
                JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Laporan_Produksi_QR.jrxml");
                JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("PARAM_NO_LP", tabel_LaporanProduksi.getValueAt(j, 1));
                params.put("parameterHalaman", 1);
                params.put("parameterJumlahHalaman", 2);
                params.put("parameterIsKosong", false);
                params.put("SUBREPORT_DIR", "Report\\");
                JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, params, Utility.db.getConnection());
                JasperDesign JASP_DESIGN2 = JRXmlLoader.load("Report\\Laporan_Produksi_QR.jrxml");
                JasperReport JASP_REP2 = JasperCompileManager.compileReport(JASP_DESIGN2);
                Map<String, Object> params2 = new HashMap<String, Object>();
                params2.put("PARAM_NO_LP", tabel_LaporanProduksi.getValueAt(j, 1));
                params2.put("parameterHalaman", 2);
                params2.put("parameterJumlahHalaman", 2);
                params2.put("parameterIsKosong", false);
                params2.put("SUBREPORT_DIR", "Report\\");
                JasperPrint JASP_PRINT2 = JasperFillManager.fillReport(JASP_REP2, params2, Utility.db.getConnection());
                List pages = JASP_PRINT2.getPages();
                for (int k = 0; k < pages.size(); k++) {
                    JRPrintPage object = (JRPrintPage) pages.get(k);
                    JASP_PRINT.addPage(object);
                }
                JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
            }

        } catch (JRException ex) {
            JOptionPane.showMessageDialog(this, ex.getLocalizedMessage());
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_print_lpActionPerformed

    private void txt_search_no_kartuKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_no_kartuKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_search_no_kartuKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox CheckBox_LPJidun;
    private javax.swing.JComboBox<String> ComboBox_Grade;
    private javax.swing.JComboBox<String> ComboBox_Ruangan;
    private javax.swing.JComboBox<String> ComboBox_SearchPekerja;
    private javax.swing.JComboBox<String> ComboBox_SearchTgl;
    private com.toedter.calendar.JDateChooser Date1_search_tgl;
    private com.toedter.calendar.JDateChooser Date2_search_tgl;
    private javax.swing.JButton button_add_grade;
    private javax.swing.JButton button_export;
    private javax.swing.JButton button_export1;
    private javax.swing.JButton button_print_lp;
    private javax.swing.JButton button_refresh;
    private javax.swing.JButton button_removeALL_grade;
    private javax.swing.JButton button_remove_grade;
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
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel label_gr_kaki_per_kpg;
    private javax.swing.JLabel label_persen_SH;
    private javax.swing.JLabel label_persen_gram;
    private javax.swing.JLabel label_persen_real;
    private javax.swing.JLabel label_rata2_rend_flat;
    private javax.swing.JLabel label_rata2_rend_jidun;
    private javax.swing.JLabel label_rata2_rend_sh;
    private javax.swing.JLabel label_rata2_rend_sp;
    private javax.swing.JLabel label_rata2_rend_utuh;
    private javax.swing.JLabel label_tot_bonggol;
    private javax.swing.JLabel label_tot_hancuran;
    private javax.swing.JLabel label_tot_rontokan;
    private javax.swing.JLabel label_tot_serabut;
    private javax.swing.JLabel label_tot_sesekan;
    private javax.swing.JLabel label_totalLP;
    private javax.swing.JLabel label_total_grade_hasil;
    private javax.swing.JLabel label_total_gradebaku;
    private javax.swing.JLabel label_total_gram_basah;
    private javax.swing.JLabel label_total_gram_flat;
    private javax.swing.JLabel label_total_gram_grading;
    private javax.swing.JLabel label_total_gram_grading_real;
    private javax.swing.JLabel label_total_gram_jidun;
    private javax.swing.JLabel label_total_gram_kaki;
    private javax.swing.JLabel label_total_gram_kering;
    private javax.swing.JLabel label_total_gram_pch;
    private javax.swing.JLabel label_total_gram_utuh;
    private javax.swing.JLabel label_total_kpg;
    private javax.swing.JLabel label_total_kpg_grading;
    private javax.swing.JLabel label_total_kpg_jidun;
    private javax.swing.JTable tabel_LaporanProduksi;
    private javax.swing.JTable tabel_gradeBaku;
    private javax.swing.JTable tabel_hasilGrading;
    private javax.swing.JTextField txt_search_memo;
    private javax.swing.JTextField txt_search_no_kartu;
    private javax.swing.JTextField txt_search_no_lp;
    private javax.swing.JTextField txt_search_pekerja;
    // End of variables declaration//GEN-END:variables
}
